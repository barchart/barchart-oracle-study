/*     */ package sun.net.httpserver;
/*     */ 
/*     */ import com.sun.net.httpserver.Filter.Chain;
/*     */ import com.sun.net.httpserver.Headers;
/*     */ import com.sun.net.httpserver.HttpContext;
/*     */ import com.sun.net.httpserver.HttpExchange;
/*     */ import com.sun.net.httpserver.HttpHandler;
/*     */ import com.sun.net.httpserver.HttpServer;
/*     */ import com.sun.net.httpserver.HttpsConfigurator;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.BindException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Set;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ 
/*     */ class ServerImpl
/*     */   implements TimeSource
/*     */ {
/*     */   private String protocol;
/*     */   private boolean https;
/*     */   private Executor executor;
/*     */   private HttpsConfigurator httpsConfig;
/*     */   private SSLContext sslContext;
/*     */   private ContextList contexts;
/*     */   private InetSocketAddress address;
/*     */   private ServerSocketChannel schan;
/*     */   private Selector selector;
/*     */   private SelectionKey listenerKey;
/*     */   private Set<HttpConnection> idleConnections;
/*     */   private Set<HttpConnection> allConnections;
/*     */   private Set<HttpConnection> reqConnections;
/*     */   private Set<HttpConnection> rspConnections;
/*     */   private List<Event> events;
/*  63 */   private Object lolock = new Object();
/*  64 */   private volatile boolean finished = false;
/*  65 */   private volatile boolean terminating = false;
/*  66 */   private boolean bound = false;
/*  67 */   private boolean started = false;
/*     */   private volatile long time;
/*  69 */   private volatile long subticks = 0L;
/*     */   private volatile long ticks;
/*     */   private HttpServer wrapper;
/*  73 */   static final int CLOCK_TICK = ServerConfig.getClockTick();
/*  74 */   static final long IDLE_INTERVAL = ServerConfig.getIdleInterval();
/*  75 */   static final int MAX_IDLE_CONNECTIONS = ServerConfig.getMaxIdleConnections();
/*  76 */   static final long TIMER_MILLIS = ServerConfig.getTimerMillis();
/*  77 */   static final long MAX_REQ_TIME = getTimeMillis(ServerConfig.getMaxReqTime());
/*  78 */   static final long MAX_RSP_TIME = getTimeMillis(ServerConfig.getMaxRspTime());
/*  79 */   static final boolean timer1Enabled = (MAX_REQ_TIME != -1L) || (MAX_RSP_TIME != -1L);
/*     */   private Timer timer;
/*     */   private Timer timer1;
/*     */   private Logger logger;
/*     */   Dispatcher dispatcher;
/* 435 */   static boolean debug = ServerConfig.debugEnabled();
/*     */ 
/* 749 */   private int exchangeCount = 0;
/*     */ 
/*     */   ServerImpl(HttpServer paramHttpServer, String paramString, InetSocketAddress paramInetSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/*  88 */     this.protocol = paramString;
/*  89 */     this.wrapper = paramHttpServer;
/*  90 */     this.logger = Logger.getLogger("com.sun.net.httpserver");
/*  91 */     ServerConfig.checkLegacyProperties(this.logger);
/*  92 */     this.https = paramString.equalsIgnoreCase("https");
/*  93 */     this.address = paramInetSocketAddress;
/*  94 */     this.contexts = new ContextList();
/*  95 */     this.schan = ServerSocketChannel.open();
/*  96 */     if (paramInetSocketAddress != null) {
/*  97 */       ServerSocket localServerSocket = this.schan.socket();
/*  98 */       localServerSocket.bind(paramInetSocketAddress, paramInt);
/*  99 */       this.bound = true;
/*     */     }
/* 101 */     this.selector = Selector.open();
/* 102 */     this.schan.configureBlocking(false);
/* 103 */     this.listenerKey = this.schan.register(this.selector, 16);
/* 104 */     this.dispatcher = new Dispatcher();
/* 105 */     this.idleConnections = Collections.synchronizedSet(new HashSet());
/* 106 */     this.allConnections = Collections.synchronizedSet(new HashSet());
/* 107 */     this.reqConnections = Collections.synchronizedSet(new HashSet());
/* 108 */     this.rspConnections = Collections.synchronizedSet(new HashSet());
/* 109 */     this.time = System.currentTimeMillis();
/* 110 */     this.timer = new Timer("server-timer", true);
/* 111 */     this.timer.schedule(new ServerTimerTask(), CLOCK_TICK, CLOCK_TICK);
/* 112 */     if (timer1Enabled) {
/* 113 */       this.timer1 = new Timer("server-timer1", true);
/* 114 */       this.timer1.schedule(new ServerTimerTask1(), TIMER_MILLIS, TIMER_MILLIS);
/* 115 */       this.logger.config("HttpServer timer1 enabled period in ms:  " + TIMER_MILLIS);
/* 116 */       this.logger.config("MAX_REQ_TIME:  " + MAX_REQ_TIME);
/* 117 */       this.logger.config("MAX_RSP_TIME:  " + MAX_RSP_TIME);
/*     */     }
/* 119 */     this.events = new LinkedList();
/* 120 */     this.logger.config("HttpServer created " + paramString + " " + paramInetSocketAddress);
/*     */   }
/*     */ 
/*     */   public void bind(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException {
/* 124 */     if (this.bound) {
/* 125 */       throw new BindException("HttpServer already bound");
/*     */     }
/* 127 */     if (paramInetSocketAddress == null) {
/* 128 */       throw new NullPointerException("null address");
/*     */     }
/* 130 */     ServerSocket localServerSocket = this.schan.socket();
/* 131 */     localServerSocket.bind(paramInetSocketAddress, paramInt);
/* 132 */     this.bound = true;
/*     */   }
/*     */ 
/*     */   public void start() {
/* 136 */     if ((!this.bound) || (this.started) || (this.finished)) {
/* 137 */       throw new IllegalStateException("server in wrong state");
/*     */     }
/* 139 */     if (this.executor == null) {
/* 140 */       this.executor = new DefaultExecutor(null);
/*     */     }
/* 142 */     Thread localThread = new Thread(this.dispatcher);
/* 143 */     this.started = true;
/* 144 */     localThread.start();
/*     */   }
/*     */ 
/*     */   public void setExecutor(Executor paramExecutor) {
/* 148 */     if (this.started) {
/* 149 */       throw new IllegalStateException("server already started");
/*     */     }
/* 151 */     this.executor = paramExecutor;
/*     */   }
/*     */ 
/*     */   public Executor getExecutor()
/*     */   {
/* 161 */     return this.executor;
/*     */   }
/*     */ 
/*     */   public void setHttpsConfigurator(HttpsConfigurator paramHttpsConfigurator) {
/* 165 */     if (paramHttpsConfigurator == null) {
/* 166 */       throw new NullPointerException("null HttpsConfigurator");
/*     */     }
/* 168 */     if (this.started) {
/* 169 */       throw new IllegalStateException("server already started");
/*     */     }
/* 171 */     this.httpsConfig = paramHttpsConfigurator;
/* 172 */     this.sslContext = paramHttpsConfigurator.getSSLContext();
/*     */   }
/*     */ 
/*     */   public HttpsConfigurator getHttpsConfigurator() {
/* 176 */     return this.httpsConfig;
/*     */   }
/*     */ 
/*     */   public void stop(int paramInt) {
/* 180 */     if (paramInt < 0) {
/* 181 */       throw new IllegalArgumentException("negative delay parameter");
/*     */     }
/* 183 */     this.terminating = true;
/*     */     try { this.schan.close(); } catch (IOException localIOException) {
/* 185 */     }this.selector.wakeup();
/* 186 */     long l = System.currentTimeMillis() + paramInt * 1000;
/* 187 */     while (System.currentTimeMillis() < l) {
/* 188 */       delay();
/* 189 */       if (this.finished) {
/* 190 */         break;
/*     */       }
/*     */     }
/* 193 */     this.finished = true;
/* 194 */     this.selector.wakeup();
/* 195 */     synchronized (this.allConnections) {
/* 196 */       for (HttpConnection localHttpConnection : this.allConnections) {
/* 197 */         localHttpConnection.close();
/*     */       }
/*     */     }
/* 200 */     this.allConnections.clear();
/* 201 */     this.idleConnections.clear();
/* 202 */     this.timer.cancel();
/* 203 */     if (timer1Enabled)
/* 204 */       this.timer1.cancel();
/*     */   }
/*     */ 
/*     */   public synchronized HttpContextImpl createContext(String paramString, HttpHandler paramHttpHandler)
/*     */   {
/* 211 */     if ((paramHttpHandler == null) || (paramString == null)) {
/* 212 */       throw new NullPointerException("null handler, or path parameter");
/*     */     }
/* 214 */     HttpContextImpl localHttpContextImpl = new HttpContextImpl(this.protocol, paramString, paramHttpHandler, this);
/* 215 */     this.contexts.add(localHttpContextImpl);
/* 216 */     this.logger.config("context created: " + paramString);
/* 217 */     return localHttpContextImpl;
/*     */   }
/*     */ 
/*     */   public synchronized HttpContextImpl createContext(String paramString) {
/* 221 */     if (paramString == null) {
/* 222 */       throw new NullPointerException("null path parameter");
/*     */     }
/* 224 */     HttpContextImpl localHttpContextImpl = new HttpContextImpl(this.protocol, paramString, null, this);
/* 225 */     this.contexts.add(localHttpContextImpl);
/* 226 */     this.logger.config("context created: " + paramString);
/* 227 */     return localHttpContextImpl;
/*     */   }
/*     */ 
/*     */   public synchronized void removeContext(String paramString) throws IllegalArgumentException {
/* 231 */     if (paramString == null) {
/* 232 */       throw new NullPointerException("null path parameter");
/*     */     }
/* 234 */     this.contexts.remove(this.protocol, paramString);
/* 235 */     this.logger.config("context removed: " + paramString);
/*     */   }
/*     */ 
/*     */   public synchronized void removeContext(HttpContext paramHttpContext) throws IllegalArgumentException {
/* 239 */     if (!(paramHttpContext instanceof HttpContextImpl)) {
/* 240 */       throw new IllegalArgumentException("wrong HttpContext type");
/*     */     }
/* 242 */     this.contexts.remove((HttpContextImpl)paramHttpContext);
/* 243 */     this.logger.config("context removed: " + paramHttpContext.getPath());
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getAddress() {
/* 247 */     return (InetSocketAddress)this.schan.socket().getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   Selector getSelector() {
/* 251 */     return this.selector;
/*     */   }
/*     */ 
/*     */   void addEvent(Event paramEvent) {
/* 255 */     synchronized (this.lolock) {
/* 256 */       this.events.add(paramEvent);
/* 257 */       this.selector.wakeup();
/*     */     }
/*     */   }
/*     */ 
/*     */   static synchronized void dprint(String paramString)
/*     */   {
/* 438 */     if (debug)
/* 439 */       System.out.println(paramString);
/*     */   }
/*     */ 
/*     */   static synchronized void dprint(Exception paramException)
/*     */   {
/* 444 */     if (debug) {
/* 445 */       System.out.println(paramException);
/* 446 */       paramException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   Logger getLogger() {
/* 451 */     return this.logger;
/*     */   }
/*     */ 
/*     */   private void closeConnection(HttpConnection paramHttpConnection) {
/* 455 */     paramHttpConnection.close();
/* 456 */     this.allConnections.remove(paramHttpConnection);
/* 457 */     switch (1.$SwitchMap$sun$net$httpserver$HttpConnection$State[paramHttpConnection.getState().ordinal()]) {
/*     */     case 1:
/* 459 */       this.reqConnections.remove(paramHttpConnection);
/* 460 */       break;
/*     */     case 2:
/* 462 */       this.rspConnections.remove(paramHttpConnection);
/* 463 */       break;
/*     */     case 3:
/* 465 */       this.idleConnections.remove(paramHttpConnection);
/*     */     }
/*     */ 
/* 468 */     assert (!this.reqConnections.remove(paramHttpConnection));
/* 469 */     assert (!this.rspConnections.remove(paramHttpConnection));
/* 470 */     assert (!this.idleConnections.remove(paramHttpConnection));
/*     */   }
/*     */ 
/*     */   void logReply(int paramInt, String paramString1, String paramString2)
/*     */   {
/* 717 */     if (!this.logger.isLoggable(Level.FINE)) {
/* 718 */       return;
/*     */     }
/* 720 */     if (paramString2 == null)
/* 721 */       paramString2 = "";
/*     */     String str1;
/* 724 */     if (paramString1.length() > 80)
/* 725 */       str1 = paramString1.substring(0, 80) + "<TRUNCATED>";
/*     */     else {
/* 727 */       str1 = paramString1;
/*     */     }
/* 729 */     String str2 = str1 + " [" + paramInt + " " + Code.msg(paramInt) + "] (" + paramString2 + ")";
/*     */ 
/* 731 */     this.logger.fine(str2);
/*     */   }
/*     */ 
/*     */   long getTicks() {
/* 735 */     return this.ticks;
/*     */   }
/*     */ 
/*     */   public long getTime() {
/* 739 */     return this.time;
/*     */   }
/*     */ 
/*     */   void delay() {
/* 743 */     Thread.yield();
/*     */     try {
/* 745 */       Thread.sleep(200L);
/*     */     }
/*     */     catch (InterruptedException localInterruptedException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void startExchange() {
/* 752 */     this.exchangeCount += 1;
/*     */   }
/*     */ 
/*     */   synchronized int endExchange() {
/* 756 */     this.exchangeCount -= 1;
/* 757 */     assert (this.exchangeCount >= 0);
/* 758 */     return this.exchangeCount;
/*     */   }
/*     */ 
/*     */   HttpServer getWrapper() {
/* 762 */     return this.wrapper;
/*     */   }
/*     */ 
/*     */   void requestStarted(HttpConnection paramHttpConnection) {
/* 766 */     paramHttpConnection.creationTime = getTime();
/* 767 */     paramHttpConnection.setState(HttpConnection.State.REQUEST);
/* 768 */     this.reqConnections.add(paramHttpConnection);
/*     */   }
/*     */ 
/*     */   void requestCompleted(HttpConnection paramHttpConnection)
/*     */   {
/* 779 */     assert (paramHttpConnection.getState() == HttpConnection.State.REQUEST);
/* 780 */     this.reqConnections.remove(paramHttpConnection);
/* 781 */     paramHttpConnection.rspStartedTime = getTime();
/* 782 */     this.rspConnections.add(paramHttpConnection);
/* 783 */     paramHttpConnection.setState(HttpConnection.State.RESPONSE);
/*     */   }
/*     */ 
/*     */   void responseCompleted(HttpConnection paramHttpConnection)
/*     */   {
/* 788 */     assert (paramHttpConnection.getState() == HttpConnection.State.RESPONSE);
/* 789 */     this.rspConnections.remove(paramHttpConnection);
/* 790 */     paramHttpConnection.setState(HttpConnection.State.IDLE);
/*     */   }
/*     */ 
/*     */   void logStackTrace(String paramString)
/*     */   {
/* 857 */     this.logger.finest(paramString);
/* 858 */     StringBuilder localStringBuilder = new StringBuilder();
/* 859 */     StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
/* 860 */     for (int i = 0; i < arrayOfStackTraceElement.length; i++) {
/* 861 */       localStringBuilder.append(arrayOfStackTraceElement[i].toString()).append("\n");
/*     */     }
/* 863 */     this.logger.finest(localStringBuilder.toString());
/*     */   }
/*     */ 
/*     */   static long getTimeMillis(long paramLong) {
/* 867 */     if (paramLong == -1L) {
/* 868 */       return -1L;
/*     */     }
/* 870 */     return paramLong * 1000L;
/*     */   }
/*     */ 
/*     */   private static class DefaultExecutor
/*     */     implements Executor
/*     */   {
/*     */     public void execute(Runnable paramRunnable)
/*     */     {
/* 156 */       paramRunnable.run();
/*     */     }
/*     */   }
/*     */ 
/*     */   class Dispatcher
/*     */     implements Runnable
/*     */   {
/* 301 */     final LinkedList<HttpConnection> connsToRegister = new LinkedList();
/*     */ 
/*     */     Dispatcher()
/*     */     {
/*     */     }
/*     */ 
/*     */     private void handleEvent(Event paramEvent)
/*     */     {
/* 266 */       ExchangeImpl localExchangeImpl = paramEvent.exchange;
/* 267 */       HttpConnection localHttpConnection = localExchangeImpl.getConnection();
/*     */       try {
/* 269 */         if ((paramEvent instanceof WriteFinishedEvent))
/*     */         {
/* 271 */           int i = ServerImpl.this.endExchange();
/* 272 */           if ((ServerImpl.this.terminating) && (i == 0)) {
/* 273 */             ServerImpl.this.finished = true;
/*     */           }
/* 275 */           ServerImpl.this.responseCompleted(localHttpConnection);
/* 276 */           LeftOverInputStream localLeftOverInputStream = localExchangeImpl.getOriginalInputStream();
/* 277 */           if (!localLeftOverInputStream.isEOF()) {
/* 278 */             localExchangeImpl.close = true;
/*     */           }
/* 280 */           if ((localExchangeImpl.close) || (ServerImpl.this.idleConnections.size() >= ServerImpl.MAX_IDLE_CONNECTIONS)) {
/* 281 */             localHttpConnection.close();
/* 282 */             ServerImpl.this.allConnections.remove(localHttpConnection);
/*     */           }
/* 284 */           else if (localLeftOverInputStream.isDataBuffered())
/*     */           {
/* 286 */             ServerImpl.this.requestStarted(localHttpConnection);
/* 287 */             handle(localHttpConnection.getChannel(), localHttpConnection);
/*     */           } else {
/* 289 */             this.connsToRegister.add(localHttpConnection);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {
/* 294 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (1)", localIOException);
/*     */ 
/* 297 */         localHttpConnection.close();
/*     */       }
/*     */     }
/*     */ 
/*     */     void reRegister(HttpConnection paramHttpConnection)
/*     */     {
/*     */       try
/*     */       {
/* 307 */         SocketChannel localSocketChannel = paramHttpConnection.getChannel();
/* 308 */         localSocketChannel.configureBlocking(false);
/* 309 */         SelectionKey localSelectionKey = localSocketChannel.register(ServerImpl.this.selector, 1);
/* 310 */         localSelectionKey.attach(paramHttpConnection);
/* 311 */         paramHttpConnection.selectionKey = localSelectionKey;
/* 312 */         paramHttpConnection.time = (ServerImpl.this.getTime() + ServerImpl.IDLE_INTERVAL);
/* 313 */         ServerImpl.this.idleConnections.add(paramHttpConnection);
/*     */       } catch (IOException localIOException) {
/* 315 */         ServerImpl.dprint(localIOException);
/* 316 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher(8)", localIOException);
/* 317 */         paramHttpConnection.close();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run() {
/* 322 */       while (!ServerImpl.this.finished)
/*     */         try {
/* 324 */           ListIterator localListIterator = this.connsToRegister.listIterator();
/*     */ 
/* 326 */           for (Object localObject1 = this.connsToRegister.iterator(); ((Iterator)localObject1).hasNext(); ) { HttpConnection localHttpConnection1 = (HttpConnection)((Iterator)localObject1).next();
/* 327 */             reRegister(localHttpConnection1);
/*     */           }
/* 329 */           this.connsToRegister.clear();
/*     */ 
/* 331 */           localObject1 = null;
/* 332 */           ServerImpl.this.selector.select(1000L);
/* 333 */           synchronized (ServerImpl.this.lolock) {
/* 334 */             if (ServerImpl.this.events.size() > 0) {
/* 335 */               localObject1 = ServerImpl.this.events;
/* 336 */               ServerImpl.this.events = new LinkedList();
/*     */             }
/*     */           }
/*     */ 
/* 340 */           if (localObject1 != null) {
/* 341 */             for (??? = ((List)localObject1).iterator(); ((Iterator)???).hasNext(); ) { localObject3 = (Event)((Iterator)???).next();
/* 342 */               handleEvent((Event)localObject3);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 348 */           ??? = ServerImpl.this.selector.selectedKeys();
/* 349 */           Object localObject3 = ((Set)???).iterator();
/* 350 */           while (((Iterator)localObject3).hasNext()) {
/* 351 */             SelectionKey localSelectionKey = (SelectionKey)((Iterator)localObject3).next();
/* 352 */             ((Iterator)localObject3).remove();
/* 353 */             if (localSelectionKey.equals(ServerImpl.this.listenerKey)) {
/* 354 */               if (!ServerImpl.this.terminating)
/*     */               {
/* 357 */                 SocketChannel localSocketChannel = ServerImpl.this.schan.accept();
/*     */ 
/* 360 */                 if (ServerConfig.noDelay()) {
/* 361 */                   localSocketChannel.socket().setTcpNoDelay(true);
/*     */                 }
/*     */ 
/* 364 */                 if (localSocketChannel != null)
/*     */                 {
/* 367 */                   localSocketChannel.configureBlocking(false);
/* 368 */                   localObject4 = localSocketChannel.register(ServerImpl.this.selector, 1);
/* 369 */                   localHttpConnection2 = new HttpConnection();
/* 370 */                   localHttpConnection2.selectionKey = ((SelectionKey)localObject4);
/* 371 */                   localHttpConnection2.setChannel(localSocketChannel);
/* 372 */                   ((SelectionKey)localObject4).attach(localHttpConnection2);
/* 373 */                   ServerImpl.this.requestStarted(localHttpConnection2);
/* 374 */                   ServerImpl.this.allConnections.add(localHttpConnection2);
/*     */                 }
/*     */               }
/*     */             }
/*     */             else {
/*     */               try
/*     */               {
/*     */                 Object localObject4;
/*     */                 HttpConnection localHttpConnection2;
/* 377 */                 if (localSelectionKey.isReadable())
/*     */                 {
/* 379 */                   localObject4 = (SocketChannel)localSelectionKey.channel();
/* 380 */                   localHttpConnection2 = (HttpConnection)localSelectionKey.attachment();
/*     */ 
/* 382 */                   localSelectionKey.cancel();
/* 383 */                   ((SocketChannel)localObject4).configureBlocking(true);
/* 384 */                   if (ServerImpl.this.idleConnections.remove(localHttpConnection2))
/*     */                   {
/* 387 */                     ServerImpl.this.requestStarted(localHttpConnection2);
/*     */                   }
/* 389 */                   handle((SocketChannel)localObject4, localHttpConnection2);
/*     */                 }
/* 391 */                 else if (!$assertionsDisabled) { throw new AssertionError(); }
/*     */               }
/*     */               catch (CancelledKeyException localCancelledKeyException) {
/* 394 */                 handleException(localSelectionKey, null);
/*     */               } catch (IOException localIOException2) {
/* 396 */                 handleException(localSelectionKey, localIOException2);
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 401 */           ServerImpl.this.selector.selectNow();
/*     */         } catch (IOException localIOException1) {
/* 403 */           ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", localIOException1);
/*     */         } catch (Exception localException) {
/* 405 */           localException.printStackTrace();
/* 406 */           ServerImpl.this.logger.log(Level.FINER, "Dispatcher (7)", localException);
/*     */         }
/*     */     }
/*     */ 
/*     */     private void handleException(SelectionKey paramSelectionKey, Exception paramException)
/*     */     {
/* 412 */       HttpConnection localHttpConnection = (HttpConnection)paramSelectionKey.attachment();
/* 413 */       if (paramException != null) {
/* 414 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (2)", paramException);
/*     */       }
/* 416 */       ServerImpl.this.closeConnection(localHttpConnection);
/*     */     }
/*     */ 
/*     */     public void handle(SocketChannel paramSocketChannel, HttpConnection paramHttpConnection) throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 423 */         ServerImpl.Exchange localExchange = new ServerImpl.Exchange(ServerImpl.this, paramSocketChannel, ServerImpl.this.protocol, paramHttpConnection);
/* 424 */         ServerImpl.this.executor.execute(localExchange);
/*     */       } catch (HttpError localHttpError) {
/* 426 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", localHttpError);
/* 427 */         ServerImpl.this.closeConnection(paramHttpConnection);
/*     */       } catch (IOException localIOException) {
/* 429 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (5)", localIOException);
/* 430 */         ServerImpl.this.closeConnection(paramHttpConnection);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class Exchange
/*     */     implements Runnable
/*     */   {
/*     */     SocketChannel chan;
/*     */     HttpConnection connection;
/*     */     HttpContextImpl context;
/*     */     InputStream rawin;
/*     */     OutputStream rawout;
/*     */     String protocol;
/*     */     ExchangeImpl tx;
/*     */     HttpContextImpl ctx;
/* 484 */     boolean rejected = false;
/*     */ 
/*     */     Exchange(SocketChannel paramString, String paramHttpConnection, HttpConnection arg4) throws IOException {
/* 487 */       this.chan = paramString;
/*     */       Object localObject;
/* 488 */       this.connection = localObject;
/* 489 */       this.protocol = paramHttpConnection;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 494 */       this.context = this.connection.getHttpContext();
/*     */ 
/* 496 */       SSLEngine localSSLEngine = null;
/* 497 */       String str1 = null;
/* 498 */       SSLStreams localSSLStreams = null;
/*     */       try
/*     */       {
/*     */         int i;
/* 500 */         if (this.context != null) {
/* 501 */           this.rawin = this.connection.getInputStream();
/* 502 */           this.rawout = this.connection.getRawOutputStream();
/* 503 */           i = 0;
/*     */         }
/*     */         else {
/* 506 */           i = 1;
/* 507 */           if (ServerImpl.this.https) {
/* 508 */             if (ServerImpl.this.sslContext == null) {
/* 509 */               ServerImpl.this.logger.warning("SSL connection received. No https contxt created");
/* 510 */               throw new HttpError("No SSL context established");
/*     */             }
/* 512 */             localSSLStreams = new SSLStreams(ServerImpl.this, ServerImpl.this.sslContext, this.chan);
/* 513 */             this.rawin = localSSLStreams.getInputStream();
/* 514 */             this.rawout = localSSLStreams.getOutputStream();
/* 515 */             localSSLEngine = localSSLStreams.getSSLEngine();
/* 516 */             this.connection.sslStreams = localSSLStreams;
/*     */           } else {
/* 518 */             this.rawin = new BufferedInputStream(new Request.ReadStream(ServerImpl.this, this.chan));
/*     */ 
/* 522 */             this.rawout = new Request.WriteStream(ServerImpl.this, this.chan);
/*     */           }
/*     */ 
/* 526 */           this.connection.raw = this.rawin;
/* 527 */           this.connection.rawout = this.rawout;
/*     */         }
/* 529 */         Request localRequest = new Request(this.rawin, this.rawout);
/* 530 */         str1 = localRequest.requestLine();
/* 531 */         if (str1 == null)
/*     */         {
/* 533 */           ServerImpl.this.closeConnection(this.connection);
/* 534 */           return;
/*     */         }
/* 536 */         int j = str1.indexOf(' ');
/* 537 */         if (j == -1) {
/* 538 */           reject(400, str1, "Bad request line");
/*     */ 
/* 540 */           return;
/*     */         }
/* 542 */         String str2 = str1.substring(0, j);
/* 543 */         int k = j + 1;
/* 544 */         j = str1.indexOf(' ', k);
/* 545 */         if (j == -1) {
/* 546 */           reject(400, str1, "Bad request line");
/*     */ 
/* 548 */           return;
/*     */         }
/* 550 */         String str3 = str1.substring(k, j);
/* 551 */         URI localURI = new URI(str3);
/* 552 */         k = j + 1;
/* 553 */         String str4 = str1.substring(k);
/* 554 */         Headers localHeaders1 = localRequest.headers();
/* 555 */         String str5 = localHeaders1.getFirst("Transfer-encoding");
/* 556 */         long l = 0L;
/* 557 */         if ((str5 != null) && (str5.equalsIgnoreCase("chunked"))) {
/* 558 */           l = -1L;
/*     */         } else {
/* 560 */           str5 = localHeaders1.getFirst("Content-Length");
/* 561 */           if (str5 != null) {
/* 562 */             l = Long.parseLong(str5);
/*     */           }
/* 564 */           if (l == 0L) {
/* 565 */             ServerImpl.this.requestCompleted(this.connection);
/*     */           }
/*     */         }
/* 568 */         this.ctx = ServerImpl.this.contexts.findContext(this.protocol, localURI.getPath());
/* 569 */         if (this.ctx == null) {
/* 570 */           reject(404, str1, "No context found for request");
/*     */ 
/* 572 */           return;
/*     */         }
/* 574 */         this.connection.setContext(this.ctx);
/* 575 */         if (this.ctx.getHandler() == null) {
/* 576 */           reject(500, str1, "No handler for context");
/*     */ 
/* 578 */           return;
/*     */         }
/* 580 */         this.tx = new ExchangeImpl(str2, localURI, localRequest, l, this.connection);
/*     */ 
/* 583 */         String str6 = localHeaders1.getFirst("Connection");
/* 584 */         Headers localHeaders2 = this.tx.getResponseHeaders();
/*     */ 
/* 586 */         if ((str6 != null) && (str6.equalsIgnoreCase("close"))) {
/* 587 */           this.tx.close = true;
/*     */         }
/* 589 */         if (str4.equalsIgnoreCase("http/1.0")) {
/* 590 */           this.tx.http10 = true;
/* 591 */           if (str6 == null) {
/* 592 */             this.tx.close = true;
/* 593 */             localHeaders2.set("Connection", "close");
/* 594 */           } else if (str6.equalsIgnoreCase("keep-alive")) {
/* 595 */             localHeaders2.set("Connection", "keep-alive");
/* 596 */             int m = (int)ServerConfig.getIdleInterval() / 1000;
/* 597 */             int n = ServerConfig.getMaxIdleConnections();
/* 598 */             localObject = "timeout=" + m + ", max=" + n;
/* 599 */             localHeaders2.set("Keep-Alive", (String)localObject);
/*     */           }
/*     */         }
/*     */ 
/* 603 */         if (i != 0) {
/* 604 */           this.connection.setParameters(this.rawin, this.rawout, this.chan, localSSLEngine, localSSLStreams, ServerImpl.this.sslContext, this.protocol, this.ctx, this.rawin);
/*     */         }
/*     */ 
/* 614 */         String str7 = localHeaders1.getFirst("Expect");
/* 615 */         if ((str7 != null) && (str7.equalsIgnoreCase("100-continue"))) {
/* 616 */           ServerImpl.this.logReply(100, str1, null);
/* 617 */           sendReply(100, false, null);
/*     */         }
/*     */ 
/* 628 */         List localList = this.ctx.getSystemFilters();
/* 629 */         Object localObject = this.ctx.getFilters();
/*     */ 
/* 631 */         Filter.Chain localChain1 = new Filter.Chain(localList, this.ctx.getHandler());
/* 632 */         Filter.Chain localChain2 = new Filter.Chain((List)localObject, new LinkHandler(localChain1));
/*     */ 
/* 635 */         this.tx.getRequestBody();
/* 636 */         this.tx.getResponseBody();
/* 637 */         if (ServerImpl.this.https)
/* 638 */           localChain2.doFilter(new HttpsExchangeImpl(this.tx));
/*     */         else
/* 640 */           localChain2.doFilter(new HttpExchangeImpl(this.tx));
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 644 */         ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (1)", localIOException);
/* 645 */         ServerImpl.this.closeConnection(this.connection);
/*     */       } catch (NumberFormatException localNumberFormatException) {
/* 647 */         reject(400, str1, "NumberFormatException thrown");
/*     */       }
/*     */       catch (URISyntaxException localURISyntaxException) {
/* 650 */         reject(400, str1, "URISyntaxException thrown");
/*     */       }
/*     */       catch (Exception localException) {
/* 653 */         ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (2)", localException);
/* 654 */         ServerImpl.this.closeConnection(this.connection);
/*     */       }
/*     */     }
/*     */ 
/*     */     void reject(int paramInt, String paramString1, String paramString2)
/*     */     {
/* 673 */       this.rejected = true;
/* 674 */       ServerImpl.this.logReply(paramInt, paramString1, paramString2);
/* 675 */       sendReply(paramInt, false, "<h1>" + paramInt + Code.msg(paramInt) + "</h1>" + paramString2);
/*     */ 
/* 678 */       ServerImpl.this.closeConnection(this.connection);
/*     */     }
/*     */ 
/*     */     void sendReply(int paramInt, boolean paramBoolean, String paramString)
/*     */     {
/*     */       try
/*     */       {
/* 685 */         StringBuilder localStringBuilder = new StringBuilder(512);
/* 686 */         localStringBuilder.append("HTTP/1.1 ").append(paramInt).append(Code.msg(paramInt)).append("\r\n");
/*     */ 
/* 689 */         if ((paramString != null) && (paramString.length() != 0)) {
/* 690 */           localStringBuilder.append("Content-Length: ").append(paramString.length()).append("\r\n").append("Content-Type: text/html\r\n");
/*     */         }
/*     */         else
/*     */         {
/* 694 */           localStringBuilder.append("Content-Length: 0\r\n");
/* 695 */           paramString = "";
/*     */         }
/* 697 */         if (paramBoolean) {
/* 698 */           localStringBuilder.append("Connection: close\r\n");
/*     */         }
/* 700 */         localStringBuilder.append("\r\n").append(paramString);
/* 701 */         String str = localStringBuilder.toString();
/* 702 */         byte[] arrayOfByte = str.getBytes("ISO8859_1");
/* 703 */         this.rawout.write(arrayOfByte);
/* 704 */         this.rawout.flush();
/* 705 */         if (paramBoolean)
/* 706 */           ServerImpl.this.closeConnection(this.connection);
/*     */       }
/*     */       catch (IOException localIOException) {
/* 709 */         ServerImpl.this.logger.log(Level.FINER, "ServerImpl.sendReply", localIOException);
/* 710 */         ServerImpl.this.closeConnection(this.connection);
/*     */       }
/*     */     }
/*     */ 
/*     */     class LinkHandler
/*     */       implements HttpHandler
/*     */     {
/*     */       Filter.Chain nextChain;
/*     */ 
/*     */       LinkHandler(Filter.Chain arg2)
/*     */       {
/*     */         Object localObject;
/* 664 */         this.nextChain = localObject;
/*     */       }
/*     */ 
/*     */       public void handle(HttpExchange paramHttpExchange) throws IOException {
/* 668 */         this.nextChain.doFilter(paramHttpExchange);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class ServerTimerTask extends TimerTask
/*     */   {
/*     */     ServerTimerTask()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 798 */       LinkedList localLinkedList = new LinkedList();
/* 799 */       ServerImpl.this.time = System.currentTimeMillis();
/* 800 */       ServerImpl.access$1808(ServerImpl.this);
/*     */       Iterator localIterator;
/*     */       HttpConnection localHttpConnection;
/* 801 */       synchronized (ServerImpl.this.idleConnections) {
/* 802 */         for (localIterator = ServerImpl.this.idleConnections.iterator(); localIterator.hasNext(); ) { localHttpConnection = (HttpConnection)localIterator.next();
/* 803 */           if (localHttpConnection.time <= ServerImpl.this.time) {
/* 804 */             localLinkedList.add(localHttpConnection);
/*     */           }
/*     */         }
/* 807 */         for (localIterator = localLinkedList.iterator(); localIterator.hasNext(); ) { localHttpConnection = (HttpConnection)localIterator.next();
/* 808 */           ServerImpl.this.idleConnections.remove(localHttpConnection);
/* 809 */           ServerImpl.this.allConnections.remove(localHttpConnection);
/* 810 */           localHttpConnection.close(); }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class ServerTimerTask1 extends TimerTask {
/*     */     ServerTimerTask1() {
/*     */     }
/*     */ 
/*     */     public void run() {
/* 820 */       LinkedList localLinkedList = new LinkedList();
/* 821 */       ServerImpl.this.time = System.currentTimeMillis();
/*     */       Iterator localIterator;
/*     */       HttpConnection localHttpConnection;
/* 822 */       synchronized (ServerImpl.this.reqConnections) {
/* 823 */         if (ServerImpl.MAX_REQ_TIME != -1L) {
/* 824 */           for (localIterator = ServerImpl.this.reqConnections.iterator(); localIterator.hasNext(); ) { localHttpConnection = (HttpConnection)localIterator.next();
/* 825 */             if (localHttpConnection.creationTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_REQ_TIME <= ServerImpl.this.time) {
/* 826 */               localLinkedList.add(localHttpConnection);
/*     */             }
/*     */           }
/* 829 */           for (localIterator = localLinkedList.iterator(); localIterator.hasNext(); ) { localHttpConnection = (HttpConnection)localIterator.next();
/* 830 */             ServerImpl.this.logger.log(Level.FINE, "closing: no request: " + localHttpConnection);
/* 831 */             ServerImpl.this.reqConnections.remove(localHttpConnection);
/* 832 */             ServerImpl.this.allConnections.remove(localHttpConnection);
/* 833 */             localHttpConnection.close();
/*     */           }
/*     */         }
/*     */       }
/* 837 */       localLinkedList = new LinkedList();
/* 838 */       synchronized (ServerImpl.this.rspConnections) {
/* 839 */         if (ServerImpl.MAX_RSP_TIME != -1L) {
/* 840 */           for (localIterator = ServerImpl.this.rspConnections.iterator(); localIterator.hasNext(); ) { localHttpConnection = (HttpConnection)localIterator.next();
/* 841 */             if (localHttpConnection.rspStartedTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_RSP_TIME <= ServerImpl.this.time) {
/* 842 */               localLinkedList.add(localHttpConnection);
/*     */             }
/*     */           }
/* 845 */           for (localIterator = localLinkedList.iterator(); localIterator.hasNext(); ) { localHttpConnection = (HttpConnection)localIterator.next();
/* 846 */             ServerImpl.this.logger.log(Level.FINE, "closing: no response: " + localHttpConnection);
/* 847 */             ServerImpl.this.rspConnections.remove(localHttpConnection);
/* 848 */             ServerImpl.this.allConnections.remove(localHttpConnection);
/* 849 */             localHttpConnection.close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.httpserver.ServerImpl
 * JD-Core Version:    0.6.2
 */