/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.AcceptPendingException;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.AsynchronousSocketChannel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ class UnixAsynchronousServerSocketChannelImpl extends AsynchronousServerSocketChannelImpl
/*     */   implements Port.PollableChannel
/*     */ {
/*  46 */   private static final NativeDispatcher nd = new SocketDispatcher();
/*     */   private final Port port;
/*     */   private final int fdVal;
/*  52 */   private final AtomicBoolean accepting = new AtomicBoolean();
/*     */ 
/*  59 */   private final Object updateLock = new Object();
/*     */   private boolean acceptPending;
/*     */   private CompletionHandler<AsynchronousSocketChannel, Object> acceptHandler;
/*     */   private Object acceptAttachment;
/*     */   private PendingFuture<AsynchronousSocketChannel, Object> acceptFuture;
/*     */   private AccessControlContext acceptAcc;
/*     */ 
/*     */   private void enableAccept()
/*     */   {
/*  54 */     this.accepting.set(false);
/*     */   }
/*     */ 
/*     */   UnixAsynchronousServerSocketChannelImpl(Port paramPort)
/*     */     throws IOException
/*     */   {
/*  74 */     super(paramPort);
/*     */     try
/*     */     {
/*  77 */       IOUtil.configureBlocking(this.fd, false);
/*     */     } catch (IOException localIOException) {
/*  79 */       nd.close(this.fd);
/*  80 */       throw localIOException;
/*     */     }
/*  82 */     this.port = paramPort;
/*  83 */     this.fdVal = IOUtil.fdVal(this.fd);
/*     */ 
/*  86 */     paramPort.register(this.fdVal, this);
/*     */   }
/*     */ 
/*     */   void implClose()
/*     */     throws IOException
/*     */   {
/*  92 */     this.port.unregister(this.fdVal);
/*     */ 
/*  95 */     nd.close(this.fd);
/*     */     CompletionHandler localCompletionHandler;
/*     */     Object localObject1;
/*     */     PendingFuture localPendingFuture;
/* 101 */     synchronized (this.updateLock) {
/* 102 */       if (!this.acceptPending)
/* 103 */         return;
/* 104 */       this.acceptPending = false;
/* 105 */       localCompletionHandler = this.acceptHandler;
/* 106 */       localObject1 = this.acceptAttachment;
/* 107 */       localPendingFuture = this.acceptFuture;
/*     */     }
/*     */ 
/* 112 */     ??? = new AsynchronousCloseException();
/* 113 */     ((AsynchronousCloseException)???).setStackTrace(new StackTraceElement[0]);
/* 114 */     if (localCompletionHandler == null) {
/* 115 */       localPendingFuture.setFailure((Throwable)???);
/*     */     }
/*     */     else
/* 118 */       Invoker.invokeIndirectly(this, localCompletionHandler, localObject1, null, (Throwable)???);
/*     */   }
/*     */ 
/*     */   public AsynchronousChannelGroupImpl group()
/*     */   {
/* 124 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void onEvent(int paramInt, boolean paramBoolean)
/*     */   {
/* 132 */     synchronized (this.updateLock) {
/* 133 */       if (!this.acceptPending)
/* 134 */         return;
/* 135 */       this.acceptPending = false;
/*     */     }
/*     */ 
/* 139 */     ??? = new FileDescriptor();
/* 140 */     InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
/* 141 */     Object localObject2 = null;
/*     */     try {
/* 143 */       begin();
/* 144 */       int i = accept0(this.fd, (FileDescriptor)???, arrayOfInetSocketAddress);
/*     */ 
/* 147 */       if (i == -2) {
/* 148 */         synchronized (this.updateLock) {
/* 149 */           this.acceptPending = true;
/*     */         }
/* 151 */         this.port.startPoll(this.fdVal, 1);
/*     */         return;
/*     */       }
/*     */     } catch (Throwable localThrowable1) {
/* 156 */       if ((localThrowable1 instanceof ClosedChannelException))
/* 157 */         localObject3 = new AsynchronousCloseException();
/* 158 */       localObject2 = localObject3;
/*     */     } finally {
/* 160 */       end();
/*     */     }
/*     */ 
/* 164 */     Object localObject3 = null;
/* 165 */     if (localObject2 == null) {
/*     */       try {
/* 167 */         localObject3 = finishAccept((FileDescriptor)???, arrayOfInetSocketAddress[0], this.acceptAcc);
/*     */       } catch (Throwable localThrowable2) {
/* 169 */         if ((!(localThrowable2 instanceof IOException)) && (!(localThrowable2 instanceof SecurityException)))
/* 170 */           localObject4 = new IOException(localThrowable2);
/* 171 */         localObject2 = localObject4;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 176 */     Object localObject4 = this.acceptHandler;
/* 177 */     Object localObject6 = this.acceptAttachment;
/* 178 */     PendingFuture localPendingFuture = this.acceptFuture;
/*     */ 
/* 181 */     enableAccept();
/*     */ 
/* 183 */     if (localObject4 == null) {
/* 184 */       localPendingFuture.setResult(localObject3, localObject2);
/*     */ 
/* 187 */       if ((localObject3 != null) && (localPendingFuture.isCancelled()))
/*     */         try {
/* 189 */           ((AsynchronousSocketChannel)localObject3).close();
/*     */         } catch (IOException localIOException) {
/*     */         }
/*     */     } else {
/* 193 */       Invoker.invoke(this, (CompletionHandler)localObject4, localObject6, localObject3, localObject2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private AsynchronousSocketChannel finishAccept(FileDescriptor paramFileDescriptor, final InetSocketAddress paramInetSocketAddress, AccessControlContext paramAccessControlContext)
/*     */     throws IOException, SecurityException
/*     */   {
/* 208 */     UnixAsynchronousSocketChannelImpl localUnixAsynchronousSocketChannelImpl = null;
/*     */     try {
/* 210 */       localUnixAsynchronousSocketChannelImpl = new UnixAsynchronousSocketChannelImpl(this.port, paramFileDescriptor, paramInetSocketAddress);
/*     */     } catch (IOException localIOException) {
/* 212 */       nd.close(paramFileDescriptor);
/* 213 */       throw localIOException;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 218 */       if (paramAccessControlContext != null) {
/* 219 */         AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public Void run() {
/* 221 */             SecurityManager localSecurityManager = System.getSecurityManager();
/* 222 */             if (localSecurityManager != null) {
/* 223 */               localSecurityManager.checkAccept(paramInetSocketAddress.getAddress().getHostAddress(), paramInetSocketAddress.getPort());
/*     */             }
/*     */ 
/* 226 */             return null;
/*     */           }
/*     */         }
/*     */         , paramAccessControlContext);
/*     */       }
/*     */       else
/*     */       {
/* 230 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 231 */         if (localSecurityManager != null)
/* 232 */           localSecurityManager.checkAccept(paramInetSocketAddress.getAddress().getHostAddress(), paramInetSocketAddress.getPort());
/*     */       }
/*     */     }
/*     */     catch (SecurityException localSecurityException)
/*     */     {
/*     */       try {
/* 238 */         localUnixAsynchronousSocketChannelImpl.close();
/*     */       } catch (Throwable localThrowable) {
/* 240 */         localSecurityException.addSuppressed(localThrowable);
/*     */       }
/* 242 */       throw localSecurityException;
/*     */     }
/* 244 */     return localUnixAsynchronousSocketChannelImpl;
/*     */   }
/*     */ 
/*     */   Future<AsynchronousSocketChannel> implAccept(Object paramObject, CompletionHandler<AsynchronousSocketChannel, Object> paramCompletionHandler)
/*     */   {
/* 252 */     if (!isOpen()) {
/* 253 */       localObject1 = new ClosedChannelException();
/* 254 */       if (paramCompletionHandler == null) {
/* 255 */         return CompletedFuture.withFailure((Throwable)localObject1);
/*     */       }
/* 257 */       Invoker.invoke(this, paramCompletionHandler, paramObject, null, (Throwable)localObject1);
/* 258 */       return null;
/*     */     }
/*     */ 
/* 261 */     if (this.localAddress == null) {
/* 262 */       throw new NotYetBoundException();
/*     */     }
/*     */ 
/* 266 */     if (isAcceptKilled()) {
/* 267 */       throw new RuntimeException("Accept not allowed due cancellation");
/*     */     }
/*     */ 
/* 270 */     if (!this.accepting.compareAndSet(false, true)) {
/* 271 */       throw new AcceptPendingException();
/*     */     }
/*     */ 
/* 274 */     Object localObject1 = new FileDescriptor();
/* 275 */     InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
/* 276 */     Object localObject2 = null;
/*     */     try {
/* 278 */       begin();
/*     */ 
/* 280 */       int i = accept0(this.fd, (FileDescriptor)localObject1, arrayOfInetSocketAddress);
/* 281 */       if (i == -2)
/*     */       {
/* 286 */         PendingFuture localPendingFuture = null;
/* 287 */         synchronized (this.updateLock) {
/* 288 */           if (paramCompletionHandler == null) {
/* 289 */             this.acceptHandler = null;
/* 290 */             localPendingFuture = new PendingFuture(this);
/* 291 */             this.acceptFuture = localPendingFuture;
/*     */           } else {
/* 293 */             this.acceptHandler = paramCompletionHandler;
/* 294 */             this.acceptAttachment = paramObject;
/*     */           }
/* 296 */           this.acceptAcc = (System.getSecurityManager() == null ? null : AccessController.getContext());
/*     */ 
/* 298 */           this.acceptPending = true;
/*     */         }
/*     */ 
/* 302 */         this.port.startPoll(this.fdVal, 1);
/* 303 */         return localPendingFuture;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1) {
/* 307 */       if ((localThrowable1 instanceof ClosedChannelException))
/* 308 */         localObject3 = new AsynchronousCloseException();
/* 309 */       localObject2 = localObject3;
/*     */     } finally {
/* 311 */       end();
/*     */     }
/*     */ 
/* 314 */     Object localObject3 = null;
/* 315 */     if (localObject2 == null) {
/*     */       try
/*     */       {
/* 318 */         localObject3 = finishAccept((FileDescriptor)localObject1, arrayOfInetSocketAddress[0], null);
/*     */       } catch (Throwable localThrowable2) {
/* 320 */         localObject2 = localThrowable2;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 325 */     enableAccept();
/*     */ 
/* 327 */     if (paramCompletionHandler == null) {
/* 328 */       return CompletedFuture.withResult(localObject3, localObject2);
/*     */     }
/* 330 */     Invoker.invokeIndirectly(this, paramCompletionHandler, paramObject, localObject3, localObject2);
/* 331 */     return null;
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   private native int accept0(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
/*     */     throws IOException;
/*     */ 
/*     */   static
/*     */   {
/* 348 */     Util.load();
/* 349 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.UnixAsynchronousServerSocketChannelImpl
 * JD-Core Version:    0.6.2
 */