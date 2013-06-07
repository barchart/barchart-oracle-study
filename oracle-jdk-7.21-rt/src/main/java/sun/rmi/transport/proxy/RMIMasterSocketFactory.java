/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.NoRouteToHostException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.rmi.server.LogStream;
/*     */ import java.rmi.server.RMISocketFactory;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.runtime.NewThreadAction;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.action.GetLongAction;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class RMIMasterSocketFactory extends RMISocketFactory
/*     */ {
/*  50 */   static int logLevel = LogStream.parseLevel(getLogLevel());
/*     */ 
/*  58 */   static final Log proxyLog = Log.getLog("sun.rmi.transport.tcp.proxy", "transport", logLevel);
/*     */ 
/*  63 */   private static long connectTimeout = getConnectTimeout();
/*     */ 
/*  72 */   private static final boolean eagerHttpFallback = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.transport.proxy.eagerHttpFallback"))).booleanValue();
/*     */ 
/*  77 */   private Hashtable successTable = new Hashtable();
/*     */   private static final int MaxRememberedHosts = 64;
/*  83 */   private Vector hostList = new Vector(64);
/*     */ 
/*  86 */   protected RMISocketFactory initialFactory = new RMIDirectSocketFactory();
/*     */   protected Vector altFactoryList;
/*     */ 
/*     */   private static String getLogLevel()
/*     */   {
/*  53 */     return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.proxy.logLevel"));
/*     */   }
/*     */ 
/*     */   private static long getConnectTimeout()
/*     */   {
/*  66 */     return ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.proxy.connectTimeout", 15000L))).longValue();
/*     */   }
/*     */ 
/*     */   public RMIMasterSocketFactory()
/*     */   {
/*  98 */     this.altFactoryList = new Vector(2);
/*  99 */     int i = 0;
/*     */     try
/*     */     {
/* 103 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyHost"));
/*     */ 
/* 106 */       if (str == null) {
/* 107 */         str = (String)AccessController.doPrivileged(new GetPropertyAction("proxyHost"));
/*     */       }
/*     */ 
/* 110 */       Boolean localBoolean = (Boolean)AccessController.doPrivileged(new GetBooleanAction("java.rmi.server.disableHttp"));
/*     */ 
/* 113 */       if ((!localBoolean.booleanValue()) && (str != null) && (str.length() > 0))
/*     */       {
/* 115 */         i = 1;
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {
/* 119 */       i = 1;
/*     */     }
/*     */ 
/* 122 */     if (i != 0) {
/* 123 */       this.altFactoryList.addElement(new RMIHttpToPortSocketFactory());
/* 124 */       this.altFactoryList.addElement(new RMIHttpToCGISocketFactory());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Socket createSocket(String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 137 */     if (proxyLog.isLoggable(Log.BRIEF)) {
/* 138 */       proxyLog.log(Log.BRIEF, "host: " + paramString + ", port: " + paramInt);
/*     */     }
/*     */ 
/* 145 */     if (this.altFactoryList.size() == 0) {
/* 146 */       return this.initialFactory.createSocket(paramString, paramInt);
/*     */     }
/*     */ 
/* 155 */     RMISocketFactory localRMISocketFactory = (RMISocketFactory)this.successTable.get(paramString);
/* 156 */     if (localRMISocketFactory != null) {
/* 157 */       if (proxyLog.isLoggable(Log.BRIEF)) {
/* 158 */         proxyLog.log(Log.BRIEF, "previously successful factory found: " + localRMISocketFactory);
/*     */       }
/*     */ 
/* 161 */       return localRMISocketFactory.createSocket(paramString, paramInt);
/*     */     }
/*     */ 
/* 169 */     Socket localSocket1 = null;
/* 170 */     Socket localSocket2 = null;
/* 171 */     AsyncConnector localAsyncConnector = new AsyncConnector(this.initialFactory, paramString, paramInt, AccessController.getContext());
/*     */ 
/* 176 */     Object localObject1 = null;
/*     */     try
/*     */     {
/* 179 */       synchronized (localAsyncConnector)
/*     */       {
/* 181 */         Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(localAsyncConnector, "AsyncConnector", true));
/*     */ 
/* 183 */         localThread.start();
/*     */         try
/*     */         {
/* 186 */           long l1 = System.currentTimeMillis();
/* 187 */           long l2 = l1 + connectTimeout;
/*     */           do {
/* 189 */             localAsyncConnector.wait(l2 - l1);
/* 190 */             localSocket1 = checkConnector(localAsyncConnector);
/* 191 */             if (localSocket1 != null)
/*     */               break;
/* 193 */             l1 = System.currentTimeMillis();
/* 194 */           }while (l1 < l2);
/*     */         } catch (InterruptedException localInterruptedException) {
/* 196 */           throw new InterruptedIOException("interrupted while waiting for connector");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 202 */       if (localSocket1 == null) {
/* 203 */         throw new NoRouteToHostException("connect timed out: " + paramString);
/*     */       }
/*     */ 
/* 206 */       proxyLog.log(Log.BRIEF, "direct socket connection successful");
/*     */       int i;
/*     */       Socket localSocket6;
/*     */       InputStream localInputStream2;
/*     */       int k;
/* 208 */       return localSocket1;
/*     */     }
/*     */     catch (UnknownHostException ) {
/* 211 */       localObject1 = ???;
/*     */ 
/* 221 */       if (localObject1 != null)
/*     */       {
/* 223 */         if (proxyLog.isLoggable(Log.BRIEF)) {
/* 224 */           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
/*     */         }
/*     */ 
/* 229 */         for (??? = 0; ??? < this.altFactoryList.size(); ???++) {
/* 230 */           localRMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(???);
/*     */           try {
/* 232 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 233 */               proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
/*     */             }
/*     */ 
/* 242 */             Socket localSocket3 = localRMISocketFactory.createSocket(paramString, paramInt);
/* 243 */             localInputStream1 = localSocket3.getInputStream();
/* 244 */             j = localInputStream1.read();
/* 245 */             localSocket3.close();
/*     */           } catch (IOException localIOException1) {
/* 247 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 248 */               proxyLog.log(Log.BRIEF, "factory failed: ", localIOException1);
/*     */             }
/*     */ 
/* 251 */             continue;
/*     */           }
/* 253 */           proxyLog.log(Log.BRIEF, "factory succeeded");
/*     */           try
/*     */           {
/* 257 */             localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
/*     */           }
/*     */           catch (IOException localIOException2)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NoRouteToHostException )
/*     */     {
/* 213 */       localObject1 = ???;
/*     */ 
/* 221 */       if (localObject1 != null)
/*     */       {
/* 223 */         if (proxyLog.isLoggable(Log.BRIEF)) {
/* 224 */           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
/*     */         }
/*     */ 
/* 229 */         for (??? = 0; ??? < this.altFactoryList.size(); ???++) {
/* 230 */           localRMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(???);
/*     */           try {
/* 232 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 233 */               proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
/*     */             }
/*     */ 
/* 242 */             Socket localSocket4 = localRMISocketFactory.createSocket(paramString, paramInt);
/* 243 */             localInputStream1 = localSocket4.getInputStream();
/* 244 */             j = localInputStream1.read();
/* 245 */             localSocket4.close();
/*     */           } catch (IOException localIOException3) {
/* 247 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 248 */               proxyLog.log(Log.BRIEF, "factory failed: ", localIOException3);
/*     */             }
/*     */ 
/* 251 */             continue;
/*     */           }
/* 253 */           proxyLog.log(Log.BRIEF, "factory succeeded");
/*     */           try
/*     */           {
/* 257 */             localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
/*     */           }
/*     */           catch (IOException localIOException4)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (SocketException )
/*     */     {
/*     */       InputStream localInputStream1;
/*     */       int j;
/* 215 */       if (eagerHttpFallback)
/* 216 */         localObject1 = ???;
/*     */       else {
/* 218 */         throw ???;
/*     */       }
/*     */ 
/* 221 */       if (localObject1 != null)
/*     */       {
/* 223 */         if (proxyLog.isLoggable(Log.BRIEF)) {
/* 224 */           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
/*     */         }
/*     */ 
/* 229 */         for (??? = 0; ??? < this.altFactoryList.size(); ???++) {
/* 230 */           localRMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(???);
/*     */           try {
/* 232 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 233 */               proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
/*     */             }
/*     */ 
/* 242 */             Socket localSocket5 = localRMISocketFactory.createSocket(paramString, paramInt);
/* 243 */             localInputStream1 = localSocket5.getInputStream();
/* 244 */             j = localInputStream1.read();
/* 245 */             localSocket5.close();
/*     */           } catch (IOException localIOException5) {
/* 247 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 248 */               proxyLog.log(Log.BRIEF, "factory failed: ", localIOException5);
/*     */             }
/*     */ 
/* 251 */             continue;
/*     */           }
/* 253 */           proxyLog.log(Log.BRIEF, "factory succeeded");
/*     */           try
/*     */           {
/* 257 */             localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
/*     */           }
/*     */           catch (IOException localIOException6)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 221 */       if (localObject1 != null)
/*     */       {
/* 223 */         if (proxyLog.isLoggable(Log.BRIEF)) {
/* 224 */           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
/*     */         }
/*     */ 
/* 229 */         for (int m = 0; m < this.altFactoryList.size(); m++) {
/* 230 */           localRMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(m);
/*     */           try {
/* 232 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 233 */               proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
/*     */             }
/*     */ 
/* 242 */             Socket localSocket7 = localRMISocketFactory.createSocket(paramString, paramInt);
/* 243 */             InputStream localInputStream3 = localSocket7.getInputStream();
/* 244 */             int n = localInputStream3.read();
/* 245 */             localSocket7.close();
/*     */           } catch (IOException localIOException9) {
/* 247 */             if (proxyLog.isLoggable(Log.BRIEF)) {
/* 248 */               proxyLog.log(Log.BRIEF, "factory failed: ", localIOException9);
/*     */             }
/*     */ 
/* 251 */             continue;
/*     */           }
/* 253 */           proxyLog.log(Log.BRIEF, "factory succeeded");
/*     */           try
/*     */           {
/* 257 */             localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
/*     */           }
/*     */           catch (IOException localIOException10)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 265 */     synchronized (this.successTable)
/*     */     {
/*     */       try {
/* 268 */         synchronized (localAsyncConnector) {
/* 269 */           localSocket1 = checkConnector(localAsyncConnector);
/*     */         }
/* 271 */         if (localSocket1 != null)
/*     */         {
/* 273 */           if (localSocket2 != null)
/* 274 */             localSocket2.close();
/* 275 */           return localSocket1;
/*     */         }
/*     */ 
/* 278 */         localAsyncConnector.notUsed();
/*     */       } catch (UnknownHostException localUnknownHostException) {
/* 280 */         localObject1 = localUnknownHostException;
/*     */       } catch (NoRouteToHostException localNoRouteToHostException) {
/* 282 */         localObject1 = localNoRouteToHostException;
/*     */       } catch (SocketException localSocketException) {
/* 284 */         if (eagerHttpFallback)
/* 285 */           localObject1 = localSocketException;
/*     */         else {
/* 287 */           throw localSocketException;
/*     */         }
/*     */       }
/*     */ 
/* 291 */       if (localSocket2 != null)
/*     */       {
/* 293 */         rememberFactory(paramString, localRMISocketFactory);
/* 294 */         return localSocket2;
/*     */       }
/* 296 */       throw ((Throwable)localObject1);
/*     */     }
/*     */   }
/*     */ 
/*     */   void rememberFactory(String paramString, RMISocketFactory paramRMISocketFactory)
/*     */   {
/* 306 */     synchronized (this.successTable) {
/* 307 */       while (this.hostList.size() >= 64) {
/* 308 */         this.successTable.remove(this.hostList.elementAt(0));
/* 309 */         this.hostList.removeElementAt(0);
/*     */       }
/* 311 */       this.hostList.addElement(paramString);
/* 312 */       this.successTable.put(paramString, paramRMISocketFactory);
/*     */     }
/*     */   }
/*     */ 
/*     */   Socket checkConnector(AsyncConnector paramAsyncConnector)
/*     */     throws IOException
/*     */   {
/* 323 */     Exception localException = paramAsyncConnector.getException();
/* 324 */     if (localException != null) {
/* 325 */       localException.fillInStackTrace();
/*     */ 
/* 332 */       if ((localException instanceof IOException))
/* 333 */         throw ((IOException)localException);
/* 334 */       if ((localException instanceof RuntimeException)) {
/* 335 */         throw ((RuntimeException)localException);
/*     */       }
/* 337 */       throw new Error("internal error: unexpected checked exception: " + localException.toString());
/*     */     }
/*     */ 
/* 341 */     return paramAsyncConnector.getSocket();
/*     */   }
/*     */ 
/*     */   public ServerSocket createServerSocket(int paramInt)
/*     */     throws IOException
/*     */   {
/* 349 */     return this.initialFactory.createServerSocket(paramInt);
/*     */   }
/*     */ 
/*     */   private class AsyncConnector
/*     */     implements Runnable
/*     */   {
/*     */     private RMISocketFactory factory;
/*     */     private String host;
/*     */     private int port;
/*     */     private AccessControlContext acc;
/* 373 */     private Exception exception = null;
/*     */ 
/* 376 */     private Socket socket = null;
/*     */ 
/* 379 */     private boolean cleanUp = false;
/*     */ 
/*     */     AsyncConnector(RMISocketFactory paramString, String paramInt, int paramAccessControlContext, AccessControlContext arg5)
/*     */     {
/* 387 */       this.factory = paramString;
/* 388 */       this.host = paramInt;
/* 389 */       this.port = paramAccessControlContext;
/*     */       Object localObject;
/* 390 */       this.acc = localObject;
/* 391 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 392 */       if (localSecurityManager != null)
/* 393 */         localSecurityManager.checkConnect(paramInt, paramAccessControlContext);
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         try
/*     */         {
/* 413 */           Socket localSocket = this.factory.createSocket(this.host, this.port);
/* 414 */           synchronized (this) {
/* 415 */             this.socket = localSocket;
/* 416 */             notify();
/*     */           }
/* 418 */           RMIMasterSocketFactory.this.rememberFactory(this.host, this.factory);
/* 419 */           synchronized (this) {
/* 420 */             if (this.cleanUp)
/*     */               try {
/* 422 */                 this.socket.close();
/*     */               }
/*     */               catch (IOException localIOException)
/*     */               {
/*     */               }
/*     */           }
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/* 431 */           synchronized (this) {
/* 432 */             this.exception = localException;
/* 433 */             notify();
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     private synchronized Exception getException()
/*     */     {
/* 452 */       return this.exception;
/*     */     }
/*     */ 
/*     */     private synchronized Socket getSocket()
/*     */     {
/* 459 */       return this.socket;
/*     */     }
/*     */ 
/*     */     synchronized void notUsed()
/*     */     {
/* 467 */       if (this.socket != null)
/*     */         try {
/* 469 */           this.socket.close();
/*     */         }
/*     */         catch (IOException localIOException) {
/*     */         }
/* 473 */       this.cleanUp = true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.RMIMasterSocketFactory
 * JD-Core Version:    0.6.2
 */