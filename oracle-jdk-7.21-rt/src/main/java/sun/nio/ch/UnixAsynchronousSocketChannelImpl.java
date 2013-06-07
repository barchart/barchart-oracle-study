/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AlreadyConnectedException;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.ConnectionPendingException;
/*     */ import java.nio.channels.InterruptedByTimeoutException;
/*     */ import java.nio.channels.ShutdownChannelGroupException;
/*     */ import java.security.AccessController;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import sun.net.NetHooks;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ class UnixAsynchronousSocketChannelImpl extends AsynchronousSocketChannelImpl
/*     */   implements Port.PollableChannel
/*     */ {
/*     */   private static final NativeDispatcher nd;
/*     */   private static final boolean disableSynchronousRead;
/*     */   private final Port port;
/*     */   private final int fdVal;
/*  61 */   private final Object updateLock = new Object();
/*     */   private boolean connectPending;
/*     */   private CompletionHandler<Void, Object> connectHandler;
/*     */   private Object connectAttachment;
/*     */   private PendingFuture<Void, Object> connectFuture;
/*     */   private SocketAddress pendingRemote;
/*     */   private boolean readPending;
/*     */   private boolean isScatteringRead;
/*     */   private ByteBuffer readBuffer;
/*     */   private ByteBuffer[] readBuffers;
/*     */   private CompletionHandler<Number, Object> readHandler;
/*     */   private Object readAttachment;
/*     */   private PendingFuture<Number, Object> readFuture;
/*     */   private Future<?> readTimer;
/*     */   private boolean writePending;
/*     */   private boolean isGatheringWrite;
/*     */   private ByteBuffer writeBuffer;
/*     */   private ByteBuffer[] writeBuffers;
/*     */   private CompletionHandler<Number, Object> writeHandler;
/*     */   private Object writeAttachment;
/*     */   private PendingFuture<Number, Object> writeFuture;
/*     */   private Future<?> writeTimer;
/* 437 */   private Runnable readTimeoutTask = new Runnable() {
/*     */     public void run() {
/* 439 */       CompletionHandler localCompletionHandler = null;
/* 440 */       Object localObject1 = null;
/* 441 */       PendingFuture localPendingFuture = null;
/*     */ 
/* 443 */       synchronized (UnixAsynchronousSocketChannelImpl.this.updateLock) {
/* 444 */         if (!UnixAsynchronousSocketChannelImpl.this.readPending)
/* 445 */           return;
/* 446 */         UnixAsynchronousSocketChannelImpl.this.readPending = false;
/* 447 */         localCompletionHandler = UnixAsynchronousSocketChannelImpl.this.readHandler;
/* 448 */         localObject1 = UnixAsynchronousSocketChannelImpl.this.readAttachment;
/* 449 */         localPendingFuture = UnixAsynchronousSocketChannelImpl.this.readFuture;
/*     */       }
/*     */ 
/* 453 */       UnixAsynchronousSocketChannelImpl.this.enableReading(true);
/*     */ 
/* 456 */       ??? = new InterruptedByTimeoutException();
/* 457 */       if (localCompletionHandler == null) {
/* 458 */         localPendingFuture.setFailure((Throwable)???);
/*     */       } else {
/* 460 */         UnixAsynchronousSocketChannelImpl localUnixAsynchronousSocketChannelImpl = UnixAsynchronousSocketChannelImpl.this;
/* 461 */         Invoker.invokeIndirectly(localUnixAsynchronousSocketChannelImpl, localCompletionHandler, localObject1, null, (Throwable)???);
/*     */       }
/*     */     }
/* 437 */   };
/*     */ 
/* 632 */   private Runnable writeTimeoutTask = new Runnable() {
/*     */     public void run() {
/* 634 */       CompletionHandler localCompletionHandler = null;
/* 635 */       Object localObject1 = null;
/* 636 */       PendingFuture localPendingFuture = null;
/*     */ 
/* 638 */       synchronized (UnixAsynchronousSocketChannelImpl.this.updateLock) {
/* 639 */         if (!UnixAsynchronousSocketChannelImpl.this.writePending)
/* 640 */           return;
/* 641 */         UnixAsynchronousSocketChannelImpl.this.writePending = false;
/* 642 */         localCompletionHandler = UnixAsynchronousSocketChannelImpl.this.writeHandler;
/* 643 */         localObject1 = UnixAsynchronousSocketChannelImpl.this.writeAttachment;
/* 644 */         localPendingFuture = UnixAsynchronousSocketChannelImpl.this.writeFuture;
/*     */       }
/*     */ 
/* 648 */       UnixAsynchronousSocketChannelImpl.this.enableWriting(true);
/*     */ 
/* 651 */       ??? = new InterruptedByTimeoutException();
/* 652 */       if (localCompletionHandler != null) {
/* 653 */         Invoker.invokeIndirectly(UnixAsynchronousSocketChannelImpl.this, localCompletionHandler, localObject1, null, (Throwable)???);
/*     */       }
/*     */       else
/* 656 */         localPendingFuture.setFailure((Throwable)???);
/*     */     }
/* 632 */   };
/*     */ 
/*     */   UnixAsynchronousSocketChannelImpl(Port paramPort)
/*     */     throws IOException
/*     */   {
/*  96 */     super(paramPort);
/*     */     try
/*     */     {
/* 100 */       IOUtil.configureBlocking(this.fd, false);
/*     */     } catch (IOException localIOException) {
/* 102 */       nd.close(this.fd);
/* 103 */       throw localIOException;
/*     */     }
/*     */ 
/* 106 */     this.port = paramPort;
/* 107 */     this.fdVal = IOUtil.fdVal(this.fd);
/*     */ 
/* 110 */     paramPort.register(this.fdVal, this);
/*     */   }
/*     */ 
/*     */   UnixAsynchronousSocketChannelImpl(Port paramPort, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
/*     */     throws IOException
/*     */   {
/* 119 */     super(paramPort, paramFileDescriptor, paramInetSocketAddress);
/*     */ 
/* 121 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/* 122 */     IOUtil.configureBlocking(paramFileDescriptor, false);
/*     */     try
/*     */     {
/* 125 */       paramPort.register(this.fdVal, this);
/*     */     }
/*     */     catch (ShutdownChannelGroupException localShutdownChannelGroupException)
/*     */     {
/* 129 */       throw new IOException(localShutdownChannelGroupException);
/*     */     }
/*     */ 
/* 132 */     this.port = paramPort;
/*     */   }
/*     */ 
/*     */   public AsynchronousChannelGroupImpl group()
/*     */   {
/* 137 */     return this.port;
/*     */   }
/*     */ 
/*     */   private void updateEvents()
/*     */   {
/* 142 */     assert (Thread.holdsLock(this.updateLock));
/* 143 */     int i = 0;
/* 144 */     if (this.readPending)
/* 145 */       i |= 1;
/* 146 */     if ((this.connectPending) || (this.writePending))
/* 147 */       i |= 4;
/* 148 */     if (i != 0)
/* 149 */       this.port.startPoll(this.fdVal, i);
/*     */   }
/*     */ 
/*     */   private void lockAndUpdateEvents()
/*     */   {
/* 154 */     synchronized (this.updateLock) {
/* 155 */       updateEvents();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void finish(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*     */   {
/* 164 */     int i = 0;
/* 165 */     int j = 0;
/* 166 */     int k = 0;
/*     */ 
/* 169 */     synchronized (this.updateLock) {
/* 170 */       if ((paramBoolean2) && (this.readPending)) {
/* 171 */         this.readPending = false;
/* 172 */         i = 1;
/*     */       }
/* 174 */       if (paramBoolean3) {
/* 175 */         if (this.writePending) {
/* 176 */           this.writePending = false;
/* 177 */           j = 1;
/* 178 */         } else if (this.connectPending) {
/* 179 */           this.connectPending = false;
/* 180 */           k = 1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 188 */     if (i != 0) {
/* 189 */       if (j != 0)
/* 190 */         finishWrite(false);
/* 191 */       finishRead(paramBoolean1);
/* 192 */       return;
/*     */     }
/* 194 */     if (j != 0) {
/* 195 */       finishWrite(paramBoolean1);
/*     */     }
/* 197 */     if (k != 0)
/* 198 */       finishConnect(paramBoolean1);
/*     */   }
/*     */ 
/*     */   public void onEvent(int paramInt, boolean paramBoolean)
/*     */   {
/* 207 */     boolean bool1 = (paramInt & 0x1) > 0;
/* 208 */     boolean bool2 = (paramInt & 0x4) > 0;
/* 209 */     if ((paramInt & 0x18) > 0) {
/* 210 */       bool1 = true;
/* 211 */       bool2 = true;
/*     */     }
/* 213 */     finish(paramBoolean, bool1, bool2);
/*     */   }
/*     */ 
/*     */   void implClose()
/*     */     throws IOException
/*     */   {
/* 219 */     this.port.unregister(this.fdVal);
/*     */ 
/* 222 */     nd.close(this.fd);
/*     */ 
/* 225 */     finish(false, true, true);
/*     */   }
/*     */ 
/*     */   public void onCancel(PendingFuture<?, ?> paramPendingFuture)
/*     */   {
/* 230 */     if (paramPendingFuture.getContext() == OpType.CONNECT)
/* 231 */       killConnect();
/* 232 */     if (paramPendingFuture.getContext() == OpType.READ)
/* 233 */       killReading();
/* 234 */     if (paramPendingFuture.getContext() == OpType.WRITE)
/* 235 */       killWriting();
/*     */   }
/*     */ 
/*     */   private void setConnected()
/*     */     throws IOException
/*     */   {
/* 241 */     synchronized (this.stateLock) {
/* 242 */       this.state = 2;
/* 243 */       this.localAddress = Net.localAddress(this.fd);
/* 244 */       this.remoteAddress = this.pendingRemote;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void finishConnect(boolean paramBoolean) {
/* 249 */     Object localObject1 = null;
/*     */     try {
/* 251 */       begin();
/* 252 */       checkConnect(this.fdVal);
/* 253 */       setConnected();
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/*     */       AsynchronousCloseException localAsynchronousCloseException;
/* 255 */       if ((localThrowable1 instanceof ClosedChannelException))
/* 256 */         localAsynchronousCloseException = new AsynchronousCloseException();
/* 257 */       localObject1 = localAsynchronousCloseException;
/*     */     } finally {
/* 259 */       end();
/*     */     }
/* 261 */     if (localObject1 != null) {
/*     */       try
/*     */       {
/* 264 */         close();
/*     */       } catch (Throwable localThrowable2) {
/* 266 */         localObject1.addSuppressed(localThrowable2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 271 */     CompletionHandler localCompletionHandler = this.connectHandler;
/* 272 */     Object localObject3 = this.connectAttachment;
/* 273 */     PendingFuture localPendingFuture = this.connectFuture;
/* 274 */     if (localCompletionHandler == null) {
/* 275 */       localPendingFuture.setResult(null, localObject1);
/*     */     }
/* 277 */     else if (paramBoolean)
/* 278 */       Invoker.invokeUnchecked(localCompletionHandler, localObject3, null, localObject1);
/*     */     else
/* 280 */       Invoker.invokeIndirectly(this, localCompletionHandler, localObject3, null, localObject1);
/*     */   }
/*     */ 
/*     */   <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
/*     */   {
/* 291 */     if (!isOpen()) {
/* 292 */       localObject1 = new ClosedChannelException();
/* 293 */       if (paramCompletionHandler == null) {
/* 294 */         return CompletedFuture.withFailure((Throwable)localObject1);
/*     */       }
/* 296 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject1);
/* 297 */       return null;
/*     */     }
/*     */ 
/* 301 */     Object localObject1 = Net.checkAddress(paramSocketAddress);
/*     */ 
/* 304 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 305 */     if (localSecurityManager != null)
/* 306 */       localSecurityManager.checkConnect(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1).getPort());
/*     */     int i;
/* 310 */     synchronized (this.stateLock) {
/* 311 */       if (this.state == 2)
/* 312 */         throw new AlreadyConnectedException();
/* 313 */       if (this.state == 1)
/* 314 */         throw new ConnectionPendingException();
/* 315 */       this.state = 1;
/* 316 */       this.pendingRemote = paramSocketAddress;
/* 317 */       i = this.localAddress == null ? 1 : 0;
/*     */     }
/*     */ 
/* 320 */     ??? = null;
/*     */     try {
/* 322 */       begin();
/*     */ 
/* 324 */       if (i != 0)
/* 325 */         NetHooks.beforeTcpConnect(this.fd, ((InetSocketAddress)localObject1).getAddress(), ((InetSocketAddress)localObject1).getPort());
/* 326 */       int j = Net.connect(this.fd, ((InetSocketAddress)localObject1).getAddress(), ((InetSocketAddress)localObject1).getPort());
/* 327 */       if (j == -2)
/*     */       {
/* 329 */         PendingFuture localPendingFuture = null;
/* 330 */         synchronized (this.updateLock) {
/* 331 */           if (paramCompletionHandler == null) {
/* 332 */             localPendingFuture = new PendingFuture(this, OpType.CONNECT);
/* 333 */             this.connectFuture = localPendingFuture;
/*     */           } else {
/* 335 */             this.connectHandler = paramCompletionHandler;
/* 336 */             this.connectAttachment = paramA;
/*     */           }
/* 338 */           this.connectPending = true;
/* 339 */           updateEvents();
/*     */         }
/* 341 */         return localPendingFuture;
/*     */       }
/* 343 */       setConnected();
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/*     */       AsynchronousCloseException localAsynchronousCloseException;
/* 345 */       if ((localThrowable1 instanceof ClosedChannelException))
/* 346 */         localAsynchronousCloseException = new AsynchronousCloseException();
/* 347 */       ??? = localAsynchronousCloseException;
/*     */     } finally {
/* 349 */       end();
/*     */     }
/*     */ 
/* 353 */     if (??? != null) {
/*     */       try {
/* 355 */         close();
/*     */       } catch (Throwable localThrowable2) {
/* 357 */         ((Throwable)???).addSuppressed(localThrowable2);
/*     */       }
/*     */     }
/* 360 */     if (paramCompletionHandler == null) {
/* 361 */       return CompletedFuture.withResult(null, (Throwable)???);
/*     */     }
/* 363 */     Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)???);
/* 364 */     return null;
/*     */   }
/*     */ 
/*     */   private void finishRead(boolean paramBoolean)
/*     */   {
/* 371 */     int i = -1;
/* 372 */     Object localObject1 = null;
/*     */ 
/* 375 */     boolean bool = this.isScatteringRead;
/* 376 */     CompletionHandler localCompletionHandler = this.readHandler;
/* 377 */     Object localObject2 = this.readAttachment;
/* 378 */     PendingFuture localPendingFuture = this.readFuture;
/* 379 */     Future localFuture = this.readTimer;
/*     */     try
/*     */     {
/* 382 */       begin();
/*     */ 
/* 384 */       if (bool)
/* 385 */         i = (int)IOUtil.read(this.fd, this.readBuffers, nd);
/*     */       else {
/* 387 */         i = IOUtil.read(this.fd, this.readBuffer, -1L, nd, null);
/*     */       }
/* 389 */       if (i == -2)
/*     */       {
/* 391 */         synchronized (this.updateLock) {
/* 392 */           this.readPending = true;
/*     */         }
/*     */ 
/*     */         return;
/*     */       }
/*     */ 
/* 398 */       this.readBuffer = null;
/* 399 */       this.readBuffers = null;
/* 400 */       this.readAttachment = null;
/*     */ 
/* 403 */       enableReading();
/*     */     }
/*     */     catch (Throwable localThrowable) {
/* 406 */       enableReading();
/* 407 */       if ((localThrowable instanceof ClosedChannelException))
/* 408 */         localObject3 = new AsynchronousCloseException();
/* 409 */       localObject1 = localObject3;
/*     */     }
/*     */     finally {
/* 412 */       if (!(localObject1 instanceof AsynchronousCloseException))
/* 413 */         lockAndUpdateEvents();
/* 414 */       end();
/*     */     }
/*     */ 
/* 418 */     if (localFuture != null) {
/* 419 */       localFuture.cancel(false);
/*     */     }
/*     */ 
/* 422 */     Object localObject3 = bool ? Long.valueOf(i) : localObject1 != null ? null : Integer.valueOf(i);
/*     */ 
/* 426 */     if (localCompletionHandler == null) {
/* 427 */       localPendingFuture.setResult(localObject3, localObject1);
/*     */     }
/* 429 */     else if (paramBoolean)
/* 430 */       Invoker.invokeUnchecked(localCompletionHandler, localObject2, localObject3, localObject1);
/*     */     else
/* 432 */       Invoker.invokeIndirectly(this, localCompletionHandler, localObject2, localObject3, localObject1);
/*     */   }
/*     */ 
/*     */   <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 483 */     Invoker.GroupAndInvokeCount localGroupAndInvokeCount = null;
/* 484 */     boolean bool = false;
/* 485 */     int i = 0;
/* 486 */     if (!disableSynchronousRead) {
/* 487 */       if (paramCompletionHandler == null) {
/* 488 */         i = 1;
/*     */       } else {
/* 490 */         localGroupAndInvokeCount = Invoker.getGroupAndInvokeCount();
/* 491 */         bool = Invoker.mayInvokeDirect(localGroupAndInvokeCount, this.port);
/*     */ 
/* 493 */         i = (bool) || (!this.port.isFixedThreadPool()) ? 1 : 0;
/*     */       }
/*     */     }
/*     */ 
/* 497 */     int j = -2;
/* 498 */     Object localObject1 = null;
/* 499 */     int k = 0;
/*     */     try
/*     */     {
/* 502 */       begin();
/*     */ 
/* 504 */       if (i != 0) {
/* 505 */         if (paramBoolean)
/* 506 */           j = (int)IOUtil.read(this.fd, paramArrayOfByteBuffer, nd);
/*     */         else {
/* 508 */           j = IOUtil.read(this.fd, paramByteBuffer, -1L, nd, null);
/*     */         }
/*     */       }
/*     */ 
/* 512 */       if (j == -2) {
/* 513 */         PendingFuture localPendingFuture = null;
/* 514 */         synchronized (this.updateLock) {
/* 515 */           this.isScatteringRead = paramBoolean;
/* 516 */           this.readBuffer = paramByteBuffer;
/* 517 */           this.readBuffers = paramArrayOfByteBuffer;
/* 518 */           if (paramCompletionHandler == null) {
/* 519 */             this.readHandler = null;
/* 520 */             localPendingFuture = new PendingFuture(this, OpType.READ);
/* 521 */             this.readFuture = localPendingFuture;
/* 522 */             this.readAttachment = null;
/*     */           } else {
/* 524 */             this.readHandler = paramCompletionHandler;
/* 525 */             this.readAttachment = paramA;
/* 526 */             this.readFuture = null;
/*     */           }
/* 528 */           if (paramLong > 0L) {
/* 529 */             this.readTimer = this.port.schedule(this.readTimeoutTask, paramLong, paramTimeUnit);
/*     */           }
/* 531 */           this.readPending = true;
/* 532 */           updateEvents();
/*     */         }
/* 534 */         k = 1;
/* 535 */         return localPendingFuture;
/*     */       }
/*     */     } catch (Throwable localThrowable) {
/* 538 */       if ((localThrowable instanceof ClosedChannelException))
/* 539 */         localObject2 = new AsynchronousCloseException();
/* 540 */       localObject1 = localObject2;
/*     */     } finally {
/* 542 */       if (k == 0)
/* 543 */         enableReading();
/* 544 */       end();
/*     */     }
/*     */ 
/* 547 */     Object localObject2 = paramBoolean ? Long.valueOf(j) : localObject1 != null ? null : Integer.valueOf(j);
/*     */ 
/* 551 */     if (paramCompletionHandler != null) {
/* 552 */       if (bool)
/* 553 */         Invoker.invokeDirect(localGroupAndInvokeCount, paramCompletionHandler, paramA, localObject2, localObject1);
/*     */       else {
/* 555 */         Invoker.invokeIndirectly(this, paramCompletionHandler, paramA, localObject2, localObject1);
/*     */       }
/* 557 */       return null;
/*     */     }
/* 559 */     return CompletedFuture.withResult(localObject2, localObject1);
/*     */   }
/*     */ 
/*     */   private void finishWrite(boolean paramBoolean)
/*     */   {
/* 566 */     int i = -1;
/* 567 */     Object localObject1 = null;
/*     */ 
/* 570 */     boolean bool = this.isGatheringWrite;
/* 571 */     CompletionHandler localCompletionHandler = this.writeHandler;
/* 572 */     Object localObject2 = this.writeAttachment;
/* 573 */     PendingFuture localPendingFuture = this.writeFuture;
/* 574 */     Future localFuture = this.writeTimer;
/*     */     try
/*     */     {
/* 577 */       begin();
/*     */ 
/* 579 */       if (bool)
/* 580 */         i = (int)IOUtil.write(this.fd, this.writeBuffers, nd);
/*     */       else {
/* 582 */         i = IOUtil.write(this.fd, this.writeBuffer, -1L, nd, null);
/*     */       }
/* 584 */       if (i == -2)
/*     */       {
/* 586 */         synchronized (this.updateLock) {
/* 587 */           this.writePending = true;
/*     */         }
/*     */ 
/*     */         return;
/*     */       }
/*     */ 
/* 593 */       this.writeBuffer = null;
/* 594 */       this.writeBuffers = null;
/* 595 */       this.writeAttachment = null;
/*     */ 
/* 598 */       enableWriting();
/*     */     }
/*     */     catch (Throwable localThrowable) {
/* 601 */       enableWriting();
/* 602 */       if ((localThrowable instanceof ClosedChannelException))
/* 603 */         localObject3 = new AsynchronousCloseException();
/* 604 */       localObject1 = localObject3;
/*     */     }
/*     */     finally {
/* 607 */       if (!(localObject1 instanceof AsynchronousCloseException))
/* 608 */         lockAndUpdateEvents();
/* 609 */       end();
/*     */     }
/*     */ 
/* 613 */     if (localFuture != null) {
/* 614 */       localFuture.cancel(false);
/*     */     }
/*     */ 
/* 617 */     Object localObject3 = bool ? Long.valueOf(i) : localObject1 != null ? null : Integer.valueOf(i);
/*     */ 
/* 621 */     if (localCompletionHandler == null) {
/* 622 */       localPendingFuture.setResult(localObject3, localObject1);
/*     */     }
/* 624 */     else if (paramBoolean)
/* 625 */       Invoker.invokeUnchecked(localCompletionHandler, localObject2, localObject3, localObject1);
/*     */     else
/* 627 */       Invoker.invokeIndirectly(this, localCompletionHandler, localObject2, localObject3, localObject1);
/*     */   }
/*     */ 
/*     */   <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 674 */     Invoker.GroupAndInvokeCount localGroupAndInvokeCount = Invoker.getGroupAndInvokeCount();
/*     */ 
/* 676 */     boolean bool = Invoker.mayInvokeDirect(localGroupAndInvokeCount, this.port);
/* 677 */     int i = (paramCompletionHandler == null) || (bool) || (!this.port.isFixedThreadPool()) ? 1 : 0;
/*     */ 
/* 680 */     int j = -2;
/* 681 */     Object localObject1 = null;
/* 682 */     int k = 0;
/*     */     try
/*     */     {
/* 685 */       begin();
/*     */ 
/* 687 */       if (i != 0) {
/* 688 */         if (paramBoolean)
/* 689 */           j = (int)IOUtil.write(this.fd, paramArrayOfByteBuffer, nd);
/*     */         else {
/* 691 */           j = IOUtil.write(this.fd, paramByteBuffer, -1L, nd, null);
/*     */         }
/*     */       }
/*     */ 
/* 695 */       if (j == -2) {
/* 696 */         PendingFuture localPendingFuture = null;
/* 697 */         synchronized (this.updateLock) {
/* 698 */           this.isGatheringWrite = paramBoolean;
/* 699 */           this.writeBuffer = paramByteBuffer;
/* 700 */           this.writeBuffers = paramArrayOfByteBuffer;
/* 701 */           if (paramCompletionHandler == null) {
/* 702 */             this.writeHandler = null;
/* 703 */             localPendingFuture = new PendingFuture(this, OpType.WRITE);
/* 704 */             this.writeFuture = localPendingFuture;
/* 705 */             this.writeAttachment = null;
/*     */           } else {
/* 707 */             this.writeHandler = paramCompletionHandler;
/* 708 */             this.writeAttachment = paramA;
/* 709 */             this.writeFuture = null;
/*     */           }
/* 711 */           if (paramLong > 0L) {
/* 712 */             this.writeTimer = this.port.schedule(this.writeTimeoutTask, paramLong, paramTimeUnit);
/*     */           }
/* 714 */           this.writePending = true;
/* 715 */           updateEvents();
/*     */         }
/* 717 */         k = 1;
/* 718 */         return localPendingFuture;
/*     */       }
/*     */     } catch (Throwable localThrowable) {
/* 721 */       if ((localThrowable instanceof ClosedChannelException))
/* 722 */         localObject2 = new AsynchronousCloseException();
/* 723 */       localObject1 = localObject2;
/*     */     } finally {
/* 725 */       if (k == 0)
/* 726 */         enableWriting();
/* 727 */       end();
/*     */     }
/*     */ 
/* 730 */     Object localObject2 = paramBoolean ? Long.valueOf(j) : localObject1 != null ? null : Integer.valueOf(j);
/*     */ 
/* 734 */     if (paramCompletionHandler != null) {
/* 735 */       if (bool)
/* 736 */         Invoker.invokeDirect(localGroupAndInvokeCount, paramCompletionHandler, paramA, localObject2, localObject1);
/*     */       else {
/* 738 */         Invoker.invokeIndirectly(this, paramCompletionHandler, paramA, localObject2, localObject1);
/*     */       }
/* 740 */       return null;
/*     */     }
/* 742 */     return CompletedFuture.withResult(localObject2, localObject1);
/*     */   }
/*     */ 
/*     */   private static native void checkConnect(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static
/*     */   {
/*  45 */     nd = new SocketDispatcher();
/*     */ 
/*  50 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.disableSynchronousRead", "false"));
/*     */ 
/*  52 */     disableSynchronousRead = str.length() == 0 ? true : Boolean.valueOf(str).booleanValue();
/*     */ 
/* 751 */     Util.load();
/*     */   }
/*     */ 
/*     */   private static enum OpType
/*     */   {
/*  46 */     CONNECT, READ, WRITE;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.UnixAsynchronousSocketChannelImpl
 * JD-Core Version:    0.6.2
 */