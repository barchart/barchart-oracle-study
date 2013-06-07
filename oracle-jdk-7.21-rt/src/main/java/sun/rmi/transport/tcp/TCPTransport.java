/*     */ package sun.rmi.transport.tcp;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.net.BindException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.ExportException;
/*     */ import java.rmi.server.LogStream;
/*     */ import java.rmi.server.RMIFailureHandler;
/*     */ import java.rmi.server.RMISocketFactory;
/*     */ import java.rmi.server.ServerNotActiveException;
/*     */ import java.rmi.server.UID;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.logging.Level;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.runtime.NewThreadAction;
/*     */ import sun.rmi.transport.Connection;
/*     */ import sun.rmi.transport.DGCAckHandler;
/*     */ import sun.rmi.transport.Endpoint;
/*     */ import sun.rmi.transport.StreamRemoteCall;
/*     */ import sun.rmi.transport.Target;
/*     */ import sun.rmi.transport.Transport;
/*     */ import sun.rmi.transport.proxy.HttpReceiveSocket;
/*     */ import sun.security.action.GetIntegerAction;
/*     */ import sun.security.action.GetLongAction;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class TCPTransport extends Transport
/*     */ {
/*  89 */   static final Log tcpLog = Log.getLog("sun.rmi.transport.tcp", "tcp", LogStream.parseLevel((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.tcp.logLevel"))));
/*     */ 
/*  94 */   private static final int maxConnectionThreads = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.maxConnectionThreads", 2147483647))).intValue();
/*     */ 
/* 100 */   private static final long threadKeepAliveTime = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.tcp.threadKeepAliveTime", 60000L))).longValue();
/*     */ 
/* 106 */   private static final ExecutorService connectionThreadPool = new ThreadPoolExecutor(0, maxConnectionThreads, threadKeepAliveTime, TimeUnit.MILLISECONDS, new SynchronousQueue(), new ThreadFactory()
/*     */   {
/*     */     public Thread newThread(Runnable paramAnonymousRunnable)
/*     */     {
/* 112 */       return (Thread)AccessController.doPrivileged(new NewThreadAction(paramAnonymousRunnable, "TCP Connection(idle)", true, true));
/*     */     }
/*     */   });
/*     */ 
/* 118 */   private static final AtomicInteger connectionCount = new AtomicInteger(0);
/*     */ 
/* 122 */   private static final ThreadLocal<ConnectionHandler> threadConnectionHandler = new ThreadLocal();
/*     */   private final LinkedList<TCPEndpoint> epList;
/* 127 */   private int exportCount = 0;
/*     */ 
/* 129 */   private ServerSocket server = null;
/*     */ 
/* 131 */   private final Map<TCPEndpoint, Reference<TCPChannel>> channelTable = new WeakHashMap();
/*     */ 
/* 134 */   static final RMISocketFactory defaultSocketFactory = RMISocketFactory.getDefaultSocketFactory();
/*     */ 
/* 143 */   private static final int connectionReadTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.readTimeout", 7200000))).intValue();
/*     */ 
/*     */   TCPTransport(LinkedList<TCPEndpoint> paramLinkedList)
/*     */   {
/* 153 */     this.epList = paramLinkedList;
/* 154 */     if (tcpLog.isLoggable(Log.BRIEF))
/* 155 */       tcpLog.log(Log.BRIEF, "Version = 2, ep = " + getEndpoint());
/*     */   }
/*     */ 
/*     */   public void shedConnectionCaches()
/*     */   {
/*     */     ArrayList localArrayList;
/*     */     Object localObject1;
/* 166 */     synchronized (this.channelTable) {
/* 167 */       localArrayList = new ArrayList(this.channelTable.values().size());
/* 168 */       for (localObject1 = this.channelTable.values().iterator(); ((Iterator)localObject1).hasNext(); ) { Reference localReference = (Reference)((Iterator)localObject1).next();
/* 169 */         TCPChannel localTCPChannel = (TCPChannel)localReference.get();
/* 170 */         if (localTCPChannel != null) {
/* 171 */           localArrayList.add(localTCPChannel);
/*     */         }
/*     */       }
/*     */     }
/* 175 */     for (??? = localArrayList.iterator(); ((Iterator)???).hasNext(); ) { localObject1 = (TCPChannel)((Iterator)???).next();
/* 176 */       ((TCPChannel)localObject1).shedCache();
/*     */     }
/*     */   }
/*     */ 
/*     */   public TCPChannel getChannel(Endpoint paramEndpoint)
/*     */   {
/* 190 */     TCPChannel localTCPChannel = null;
/* 191 */     if ((paramEndpoint instanceof TCPEndpoint)) {
/* 192 */       synchronized (this.channelTable) {
/* 193 */         Reference localReference = (Reference)this.channelTable.get(paramEndpoint);
/* 194 */         if (localReference != null) {
/* 195 */           localTCPChannel = (TCPChannel)localReference.get();
/*     */         }
/* 197 */         if (localTCPChannel == null) {
/* 198 */           TCPEndpoint localTCPEndpoint = (TCPEndpoint)paramEndpoint;
/* 199 */           localTCPChannel = new TCPChannel(this, localTCPEndpoint);
/* 200 */           this.channelTable.put(localTCPEndpoint, new WeakReference(localTCPChannel));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 205 */     return localTCPChannel;
/*     */   }
/*     */ 
/*     */   public void free(Endpoint paramEndpoint)
/*     */   {
/* 213 */     if ((paramEndpoint instanceof TCPEndpoint))
/* 214 */       synchronized (this.channelTable) {
/* 215 */         Reference localReference = (Reference)this.channelTable.remove(paramEndpoint);
/* 216 */         if (localReference != null) {
/* 217 */           TCPChannel localTCPChannel = (TCPChannel)localReference.get();
/* 218 */           if (localTCPChannel != null)
/* 219 */             localTCPChannel.shedCache();
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public void exportObject(Target paramTarget)
/*     */     throws RemoteException
/*     */   {
/* 235 */     synchronized (this) {
/* 236 */       listen();
/* 237 */       this.exportCount += 1;
/*     */     }
/*     */ 
/* 245 */     int i = 0;
/*     */     try {
/* 247 */       super.exportObject(paramTarget);
/* 248 */       i = 1;
/*     */     } finally {
/* 250 */       if (i == 0)
/* 251 */         synchronized (this) {
/* 252 */           decrementExportCount();
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void targetUnexported()
/*     */   {
/* 259 */     decrementExportCount();
/*     */   }
/*     */ 
/*     */   private void decrementExportCount()
/*     */   {
/* 267 */     assert (Thread.holdsLock(this));
/* 268 */     this.exportCount -= 1;
/* 269 */     if ((this.exportCount == 0) && (getEndpoint().getListenPort() != 0)) {
/* 270 */       ServerSocket localServerSocket = this.server;
/* 271 */       this.server = null;
/*     */       try {
/* 273 */         localServerSocket.close();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void checkAcceptPermission(AccessControlContext paramAccessControlContext)
/*     */   {
/* 284 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 285 */     if (localSecurityManager == null) {
/* 286 */       return;
/*     */     }
/* 288 */     ConnectionHandler localConnectionHandler = (ConnectionHandler)threadConnectionHandler.get();
/* 289 */     if (localConnectionHandler == null) {
/* 290 */       throw new Error("checkAcceptPermission not in ConnectionHandler thread");
/*     */     }
/*     */ 
/* 293 */     localConnectionHandler.checkAcceptPermission(localSecurityManager, paramAccessControlContext);
/*     */   }
/*     */ 
/*     */   private TCPEndpoint getEndpoint() {
/* 297 */     synchronized (this.epList) {
/* 298 */       return (TCPEndpoint)this.epList.getLast();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void listen()
/*     */     throws RemoteException
/*     */   {
/* 306 */     assert (Thread.holdsLock(this));
/* 307 */     TCPEndpoint localTCPEndpoint = getEndpoint();
/* 308 */     int i = localTCPEndpoint.getPort();
/*     */ 
/* 310 */     if (this.server == null) {
/* 311 */       if (tcpLog.isLoggable(Log.BRIEF)) {
/* 312 */         tcpLog.log(Log.BRIEF, "(port " + i + ") create server socket");
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 317 */         this.server = localTCPEndpoint.newServerSocket();
/*     */ 
/* 323 */         Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(new AcceptLoop(this.server), "TCP Accept-" + i, true));
/*     */ 
/* 326 */         localThread.start();
/*     */       } catch (BindException localBindException) {
/* 328 */         throw new ExportException("Port already in use: " + i, localBindException);
/*     */       } catch (IOException localIOException) {
/* 330 */         throw new ExportException("Listen failed on port: " + i, localIOException);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 335 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 336 */       if (localSecurityManager != null)
/* 337 */         localSecurityManager.checkListen(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void closeSocket(Socket paramSocket)
/*     */   {
/*     */     try
/*     */     {
/* 518 */       paramSocket.close();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   void handleMessages(Connection paramConnection, boolean paramBoolean)
/*     */   {
/* 530 */     int i = getEndpoint().getPort();
/*     */     try
/*     */     {
/* 533 */       DataInputStream localDataInputStream = new DataInputStream(paramConnection.getInputStream());
/*     */       do {
/* 535 */         int j = localDataInputStream.read();
/* 536 */         if (j == -1) {
/* 537 */           if (!tcpLog.isLoggable(Log.BRIEF)) break;
/* 538 */           tcpLog.log(Log.BRIEF, "(port " + i + ") connection closed"); break;
/*     */         }
/*     */ 
/* 544 */         if (tcpLog.isLoggable(Log.BRIEF)) {
/* 545 */           tcpLog.log(Log.BRIEF, "(port " + i + ") op = " + j);
/*     */         }
/*     */ 
/* 549 */         switch (j)
/*     */         {
/*     */         case 80:
/* 552 */           StreamRemoteCall localStreamRemoteCall = new StreamRemoteCall(paramConnection);
/* 553 */           if (!serviceCall(localStreamRemoteCall))
/*     */           {
/*     */             return;
/*     */           }
/*     */           break;
/*     */         case 82:
/* 559 */           DataOutputStream localDataOutputStream = new DataOutputStream(paramConnection.getOutputStream());
/*     */ 
/* 561 */           localDataOutputStream.writeByte(83);
/* 562 */           paramConnection.releaseOutputStream();
/* 563 */           break;
/*     */         case 84:
/* 566 */           DGCAckHandler.received(UID.read(localDataInputStream));
/* 567 */           break;
/*     */         case 81:
/*     */         case 83:
/*     */         default:
/* 570 */           throw new IOException("unknown transport op " + j);
/*     */         }
/*     */       }
/* 572 */       while (paramBoolean);
/*     */     }
/*     */     catch (IOException localIOException2)
/*     */     {
/* 576 */       if (tcpLog.isLoggable(Log.BRIEF))
/* 577 */         tcpLog.log(Log.BRIEF, "(port " + i + ") exception: ", localIOException2);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 582 */         paramConnection.close();
/*     */       }
/*     */       catch (IOException localIOException5)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getClientHost()
/*     */     throws ServerNotActiveException
/*     */   {
/* 594 */     ConnectionHandler localConnectionHandler = (ConnectionHandler)threadConnectionHandler.get();
/* 595 */     if (localConnectionHandler != null) {
/* 596 */       return localConnectionHandler.getClientHost();
/*     */     }
/* 598 */     throw new ServerNotActiveException("not in a remote call");
/*     */   }
/*     */ 
/*     */   private class AcceptLoop
/*     */     implements Runnable
/*     */   {
/*     */     private final ServerSocket serverSocket;
/* 350 */     private long lastExceptionTime = 0L;
/*     */     private int recentExceptionCount;
/*     */ 
/*     */     AcceptLoop(ServerSocket arg2)
/*     */     {
/*     */       Object localObject;
/* 354 */       this.serverSocket = localObject;
/*     */     }
/*     */ 
/*     */     public void run() {
/*     */       try {
/* 359 */         executeAcceptLoop();
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/* 368 */           this.serverSocket.close();
/*     */         }
/*     */         catch (IOException localIOException2)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void executeAcceptLoop()
/*     */     {
/* 379 */       if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
/* 380 */         TCPTransport.tcpLog.log(Log.BRIEF, "listening on port " + TCPTransport.this.getEndpoint().getPort());
/*     */       }
/*     */ 
/*     */       while (true)
/*     */       {
/* 385 */         Socket localSocket = null;
/*     */         try {
/* 387 */           localSocket = this.serverSocket.accept();
/*     */ 
/* 392 */           InetAddress localInetAddress = localSocket.getInetAddress();
/* 393 */           String str = localInetAddress != null ? localInetAddress.getHostAddress() : "0.0.0.0";
/*     */           try
/*     */           {
/* 402 */             TCPTransport.connectionThreadPool.execute(new TCPTransport.ConnectionHandler(TCPTransport.this, localSocket, str));
/*     */           }
/*     */           catch (RejectedExecutionException localRejectedExecutionException) {
/* 405 */             TCPTransport.closeSocket(localSocket);
/* 406 */             TCPTransport.tcpLog.log(Log.BRIEF, "rejected connection from " + str);
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (Throwable localThrowable1)
/*     */         {
/*     */           try
/*     */           {
/* 418 */             if (this.serverSocket.isClosed())
/*     */             {
/* 436 */               if (localSocket != null)
/* 437 */                 TCPTransport.closeSocket(localSocket); break;
/*     */             }
/*     */             try
/*     */             {
/* 423 */               if (TCPTransport.tcpLog.isLoggable(Level.WARNING)) {
/* 424 */                 TCPTransport.tcpLog.log(Level.WARNING, "accept loop for " + this.serverSocket + " throws", localThrowable1);
/*     */               }
/*     */ 
/*     */             }
/*     */             catch (Throwable localThrowable2)
/*     */             {
/*     */             }
/*     */ 
/*     */           }
/*     */           finally
/*     */           {
/* 436 */             if (localSocket != null) {
/* 437 */               TCPTransport.closeSocket(localSocket);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 445 */           if (!(localThrowable1 instanceof SecurityException)) {
/*     */             try {
/* 447 */               TCPEndpoint.shedConnectionCaches();
/*     */             }
/*     */             catch (Throwable localThrowable3)
/*     */             {
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 457 */           if (((localThrowable1 instanceof Exception)) || ((localThrowable1 instanceof OutOfMemoryError)) || ((localThrowable1 instanceof NoClassDefFoundError)))
/*     */           {
/* 461 */             if (continueAfterAcceptFailure(localThrowable1));
/*     */           }
/*     */           else
/*     */           {
/* 466 */             throw ((Error)localThrowable1);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private boolean continueAfterAcceptFailure(Throwable paramThrowable)
/*     */     {
/* 482 */       RMIFailureHandler localRMIFailureHandler = RMISocketFactory.getFailureHandler();
/* 483 */       if (localRMIFailureHandler != null) {
/* 484 */         return localRMIFailureHandler.failure((paramThrowable instanceof Exception) ? (Exception)paramThrowable : new InvocationTargetException(paramThrowable));
/*     */       }
/*     */ 
/* 487 */       throttleLoopOnException();
/* 488 */       return true;
/*     */     }
/*     */ 
/*     */     private void throttleLoopOnException()
/*     */     {
/* 498 */       long l = System.currentTimeMillis();
/* 499 */       if ((this.lastExceptionTime == 0L) || (l - this.lastExceptionTime > 5000L))
/*     */       {
/* 501 */         this.lastExceptionTime = l;
/* 502 */         this.recentExceptionCount = 0;
/*     */       }
/* 505 */       else if (++this.recentExceptionCount >= 10) {
/*     */         try {
/* 507 */           Thread.sleep(10000L);
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ConnectionHandler
/*     */     implements Runnable
/*     */   {
/*     */     private static final int POST = 1347375956;
/*     */     private AccessControlContext okContext;
/*     */     private Map<AccessControlContext, Reference<AccessControlContext>> authCache;
/* 617 */     private SecurityManager cacheSecurityManager = null;
/*     */     private Socket socket;
/*     */     private String remoteHost;
/*     */ 
/*     */     ConnectionHandler(Socket paramString, String arg3)
/*     */     {
/* 623 */       this.socket = paramString;
/*     */       Object localObject;
/* 624 */       this.remoteHost = localObject;
/*     */     }
/*     */ 
/*     */     String getClientHost() {
/* 628 */       return this.remoteHost;
/*     */     }
/*     */ 
/*     */     void checkAcceptPermission(SecurityManager paramSecurityManager, AccessControlContext paramAccessControlContext)
/*     */     {
/* 642 */       if (paramSecurityManager != this.cacheSecurityManager) {
/* 643 */         this.okContext = null;
/* 644 */         this.authCache = new WeakHashMap();
/*     */ 
/* 646 */         this.cacheSecurityManager = paramSecurityManager;
/*     */       }
/* 648 */       if ((paramAccessControlContext.equals(this.okContext)) || (this.authCache.containsKey(paramAccessControlContext))) {
/* 649 */         return;
/*     */       }
/* 651 */       InetAddress localInetAddress = this.socket.getInetAddress();
/* 652 */       String str = localInetAddress != null ? localInetAddress.getHostAddress() : "*";
/*     */ 
/* 654 */       paramSecurityManager.checkAccept(str, this.socket.getPort());
/*     */ 
/* 656 */       this.authCache.put(paramAccessControlContext, new SoftReference(paramAccessControlContext));
/* 657 */       this.okContext = paramAccessControlContext;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 661 */       Thread localThread = Thread.currentThread();
/* 662 */       String str = localThread.getName();
/*     */       try {
/* 664 */         localThread.setName("RMI TCP Connection(" + TCPTransport.connectionCount.incrementAndGet() + ")-" + this.remoteHost);
/*     */ 
/* 667 */         run0();
/*     */       } finally {
/* 669 */         localThread.setName(str);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void run0() {
/* 674 */       TCPEndpoint localTCPEndpoint1 = TCPTransport.this.getEndpoint();
/* 675 */       int i = localTCPEndpoint1.getPort();
/*     */ 
/* 677 */       TCPTransport.threadConnectionHandler.set(this);
/*     */       try
/*     */       {
/* 683 */         this.socket.setTcpNoDelay(true);
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */       try {
/* 689 */         if (TCPTransport.connectionReadTimeout > 0)
/* 690 */           this.socket.setSoTimeout(TCPTransport.connectionReadTimeout);
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/*     */       }
/*     */       try {
/* 696 */         InputStream localInputStream = this.socket.getInputStream();
/* 697 */         BufferedInputStream localBufferedInputStream = localInputStream.markSupported() ? localInputStream : new BufferedInputStream(localInputStream);
/*     */ 
/* 702 */         localBufferedInputStream.mark(4);
/* 703 */         DataInputStream localDataInputStream = new DataInputStream(localBufferedInputStream);
/* 704 */         int j = localDataInputStream.readInt();
/*     */ 
/* 706 */         if (j == 1347375956) {
/* 707 */           TCPTransport.tcpLog.log(Log.BRIEF, "decoding HTTP-wrapped call");
/*     */ 
/* 712 */           localBufferedInputStream.reset();
/*     */           try
/*     */           {
/* 715 */             this.socket = new HttpReceiveSocket(this.socket, localBufferedInputStream, null);
/* 716 */             this.remoteHost = "0.0.0.0";
/* 717 */             localInputStream = this.socket.getInputStream();
/* 718 */             localBufferedInputStream = new BufferedInputStream(localInputStream);
/* 719 */             localDataInputStream = new DataInputStream(localBufferedInputStream);
/* 720 */             j = localDataInputStream.readInt();
/*     */           }
/*     */           catch (IOException localIOException2) {
/* 723 */             throw new RemoteException("Error HTTP-unwrapping call", localIOException2);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 731 */         int k = localDataInputStream.readShort();
/* 732 */         if ((j != 1246907721) || (k != 2))
/*     */         {
/* 738 */           TCPTransport.closeSocket(this.socket);
/*     */         }
/*     */         else
/*     */         {
/* 742 */           OutputStream localOutputStream = this.socket.getOutputStream();
/* 743 */           BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localOutputStream);
/*     */ 
/* 745 */           DataOutputStream localDataOutputStream = new DataOutputStream(localBufferedOutputStream);
/*     */ 
/* 747 */           int m = this.socket.getPort();
/*     */ 
/* 749 */           if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
/* 750 */             TCPTransport.tcpLog.log(Log.BRIEF, "accepted socket from [" + this.remoteHost + ":" + m + "]");
/*     */           }
/*     */ 
/* 759 */           int n = localDataInputStream.readByte();
/*     */           TCPEndpoint localTCPEndpoint2;
/*     */           TCPChannel localTCPChannel;
/*     */           TCPConnection localTCPConnection;
/* 760 */           switch (n)
/*     */           {
/*     */           case 76:
/* 765 */             localTCPEndpoint2 = new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), localTCPEndpoint1.getClientSocketFactory(), localTCPEndpoint1.getServerSocketFactory());
/*     */ 
/* 768 */             localTCPChannel = new TCPChannel(TCPTransport.this, localTCPEndpoint2);
/* 769 */             localTCPConnection = new TCPConnection(localTCPChannel, this.socket, localBufferedInputStream, localBufferedOutputStream);
/*     */ 
/* 772 */             TCPTransport.this.handleMessages(localTCPConnection, false);
/* 773 */             break;
/*     */           case 75:
/* 777 */             localDataOutputStream.writeByte(78);
/*     */ 
/* 780 */             if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
/* 781 */               TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") " + "suggesting " + this.remoteHost + ":" + m);
/*     */             }
/*     */ 
/* 786 */             localDataOutputStream.writeUTF(this.remoteHost);
/* 787 */             localDataOutputStream.writeInt(m);
/* 788 */             localDataOutputStream.flush();
/*     */ 
/* 792 */             String str = localDataInputStream.readUTF();
/* 793 */             int i1 = localDataInputStream.readInt();
/* 794 */             if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
/* 795 */               TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") client using " + str + ":" + i1);
/*     */             }
/*     */ 
/* 801 */             localTCPEndpoint2 = new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), localTCPEndpoint1.getClientSocketFactory(), localTCPEndpoint1.getServerSocketFactory());
/*     */ 
/* 804 */             localTCPChannel = new TCPChannel(TCPTransport.this, localTCPEndpoint2);
/* 805 */             localTCPConnection = new TCPConnection(localTCPChannel, this.socket, localBufferedInputStream, localBufferedOutputStream);
/*     */ 
/* 808 */             TCPTransport.this.handleMessages(localTCPConnection, true);
/* 809 */             break;
/*     */           case 77:
/* 812 */             if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
/* 813 */               TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") accepting multiplex protocol");
/*     */             }
/*     */ 
/* 818 */             localDataOutputStream.writeByte(78);
/*     */ 
/* 821 */             if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
/* 822 */               TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") suggesting " + this.remoteHost + ":" + m);
/*     */             }
/*     */ 
/* 826 */             localDataOutputStream.writeUTF(this.remoteHost);
/* 827 */             localDataOutputStream.writeInt(m);
/* 828 */             localDataOutputStream.flush();
/*     */ 
/* 831 */             localTCPEndpoint2 = new TCPEndpoint(localDataInputStream.readUTF(), localDataInputStream.readInt(), localTCPEndpoint1.getClientSocketFactory(), localTCPEndpoint1.getServerSocketFactory());
/*     */ 
/* 834 */             if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE))
/* 835 */               TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + i + ") client using " + localTCPEndpoint2.getHost() + ":" + localTCPEndpoint2.getPort());
/*     */             ConnectionMultiplexer localConnectionMultiplexer;
/* 841 */             synchronized (TCPTransport.this.channelTable)
/*     */             {
/* 843 */               localTCPChannel = TCPTransport.this.getChannel(localTCPEndpoint2);
/* 844 */               localConnectionMultiplexer = new ConnectionMultiplexer(localTCPChannel, localBufferedInputStream, localOutputStream, false);
/*     */ 
/* 847 */               localTCPChannel.useMultiplexer(localConnectionMultiplexer);
/*     */             }
/* 849 */             localConnectionMultiplexer.run();
/* 850 */             break;
/*     */           default:
/* 854 */             localDataOutputStream.writeByte(79);
/* 855 */             localDataOutputStream.flush();
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException1)
/*     */       {
/* 861 */         TCPTransport.tcpLog.log(Log.BRIEF, "terminated with exception:", localIOException1);
/*     */       } finally {
/* 863 */         TCPTransport.closeSocket(this.socket);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.tcp.TCPTransport
 * JD-Core Version:    0.6.2
 */