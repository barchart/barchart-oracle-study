/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.AsynchronousSocketChannel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.ConnectionPendingException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.nio.channels.ReadPendingException;
/*     */ import java.nio.channels.WritePendingException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import sun.net.NetHooks;
/*     */ 
/*     */ abstract class AsynchronousSocketChannelImpl extends AsynchronousSocketChannel
/*     */   implements Cancellable, Groupable
/*     */ {
/*     */   protected final FileDescriptor fd;
/*  54 */   protected final Object stateLock = new Object();
/*     */ 
/*  56 */   protected volatile SocketAddress localAddress = null;
/*  57 */   protected volatile SocketAddress remoteAddress = null;
/*     */   static final int ST_UNINITIALIZED = -1;
/*     */   static final int ST_UNCONNECTED = 0;
/*     */   static final int ST_PENDING = 1;
/*     */   static final int ST_CONNECTED = 2;
/*  64 */   protected volatile int state = -1;
/*     */ 
/*  67 */   private final Object readLock = new Object();
/*     */   private boolean reading;
/*     */   private boolean readShutdown;
/*     */   private boolean readKilled;
/*  73 */   private final Object writeLock = new Object();
/*     */   private boolean writing;
/*     */   private boolean writeShutdown;
/*     */   private boolean writeKilled;
/*  79 */   private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
/*  80 */   private volatile boolean open = true;
/*     */ 
/*     */   AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
/*     */     throws IOException
/*     */   {
/*  85 */     super(paramAsynchronousChannelGroupImpl.provider());
/*  86 */     this.fd = Net.socket(true);
/*  87 */     this.state = 0;
/*     */   }
/*     */ 
/*     */   AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
/*     */     throws IOException
/*     */   {
/*  96 */     super(paramAsynchronousChannelGroupImpl.provider());
/*  97 */     this.fd = paramFileDescriptor;
/*  98 */     this.state = 2;
/*  99 */     this.localAddress = Net.localAddress(paramFileDescriptor);
/* 100 */     this.remoteAddress = paramInetSocketAddress;
/*     */   }
/*     */ 
/*     */   public final boolean isOpen()
/*     */   {
/* 105 */     return this.open;
/*     */   }
/*     */ 
/*     */   final void begin()
/*     */     throws IOException
/*     */   {
/* 112 */     this.closeLock.readLock().lock();
/* 113 */     if (!isOpen())
/* 114 */       throw new ClosedChannelException();
/*     */   }
/*     */ 
/*     */   final void end()
/*     */   {
/* 121 */     this.closeLock.readLock().unlock();
/*     */   }
/*     */ 
/*     */   abstract void implClose()
/*     */     throws IOException;
/*     */ 
/*     */   public final void close()
/*     */     throws IOException
/*     */   {
/* 132 */     this.closeLock.writeLock().lock();
/*     */     try {
/* 134 */       if (!this.open)
/*     */         return;
/* 136 */       this.open = false;
/*     */     } finally {
/* 138 */       this.closeLock.writeLock().unlock();
/*     */     }
/* 140 */     implClose();
/*     */   }
/*     */ 
/*     */   final void enableReading(boolean paramBoolean) {
/* 144 */     synchronized (this.readLock) {
/* 145 */       this.reading = false;
/* 146 */       if (paramBoolean)
/* 147 */         this.readKilled = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   final void enableReading() {
/* 152 */     enableReading(false);
/*     */   }
/*     */ 
/*     */   final void enableWriting(boolean paramBoolean) {
/* 156 */     synchronized (this.writeLock) {
/* 157 */       this.writing = false;
/* 158 */       if (paramBoolean)
/* 159 */         this.writeKilled = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   final void enableWriting() {
/* 164 */     enableWriting(false);
/*     */   }
/*     */ 
/*     */   final void killReading() {
/* 168 */     synchronized (this.readLock) {
/* 169 */       this.readKilled = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   final void killWriting() {
/* 174 */     synchronized (this.writeLock) {
/* 175 */       this.writeKilled = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   final void killConnect()
/*     */   {
/* 182 */     killReading();
/* 183 */     killWriting();
/*     */   }
/*     */ 
/*     */   abstract <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler);
/*     */ 
/*     */   public final Future<Void> connect(SocketAddress paramSocketAddress)
/*     */   {
/* 195 */     return implConnect(paramSocketAddress, null, null);
/*     */   }
/*     */ 
/*     */   public final <A> void connect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
/*     */   {
/* 203 */     if (paramCompletionHandler == null)
/* 204 */       throw new NullPointerException("'handler' is null");
/* 205 */     implConnect(paramSocketAddress, paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   abstract <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
/*     */ 
/*     */   private <V extends Number, A> Future<V> read(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 228 */     if (!isOpen()) {
/* 229 */       ClosedChannelException localClosedChannelException = new ClosedChannelException();
/* 230 */       if (paramCompletionHandler == null)
/* 231 */         return CompletedFuture.withFailure(localClosedChannelException);
/* 232 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, localClosedChannelException);
/* 233 */       return null;
/*     */     }
/*     */ 
/* 236 */     if (this.remoteAddress == null) {
/* 237 */       throw new NotYetConnectedException();
/*     */     }
/* 239 */     int i = (paramBoolean) || (paramByteBuffer.hasRemaining()) ? 1 : 0;
/* 240 */     int j = 0;
/*     */ 
/* 243 */     synchronized (this.readLock) {
/* 244 */       if (this.readKilled)
/* 245 */         throw new IllegalStateException("Reading not allowed due to timeout or cancellation");
/* 246 */       if (this.reading)
/* 247 */         throw new ReadPendingException();
/* 248 */       if (this.readShutdown) {
/* 249 */         j = 1;
/*     */       }
/* 251 */       else if (i != 0) {
/* 252 */         this.reading = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 259 */     if ((j != 0) || (i == 0))
/*     */     {
/* 261 */       if (paramBoolean)
/* 262 */         ??? = j != 0 ? Long.valueOf(-1L) : Long.valueOf(0L);
/*     */       else {
/* 264 */         ??? = Integer.valueOf(j != 0 ? -1 : 0);
/*     */       }
/* 266 */       if (paramCompletionHandler == null)
/* 267 */         return CompletedFuture.withResult(???);
/* 268 */       Invoker.invoke(this, paramCompletionHandler, paramA, ???, null);
/* 269 */       return null;
/*     */     }
/*     */ 
/* 272 */     return implRead(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   public final Future<Integer> read(ByteBuffer paramByteBuffer)
/*     */   {
/* 277 */     if (paramByteBuffer.isReadOnly())
/* 278 */       throw new IllegalArgumentException("Read-only buffer");
/* 279 */     return read(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null);
/*     */   }
/*     */ 
/*     */   public final <A> void read(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 289 */     if (paramCompletionHandler == null)
/* 290 */       throw new NullPointerException("'handler' is null");
/* 291 */     if (paramByteBuffer.isReadOnly())
/* 292 */       throw new IllegalArgumentException("Read-only buffer");
/* 293 */     read(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   public final <A> void read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler)
/*     */   {
/* 305 */     if (paramCompletionHandler == null)
/* 306 */       throw new NullPointerException("'handler' is null");
/* 307 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 308 */       throw new IndexOutOfBoundsException();
/* 309 */     ByteBuffer[] arrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
/* 310 */     for (int i = 0; i < arrayOfByteBuffer.length; i++) {
/* 311 */       if (arrayOfByteBuffer[i].isReadOnly())
/* 312 */         throw new IllegalArgumentException("Read-only buffer");
/*     */     }
/* 314 */     read(true, null, arrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   abstract <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
/*     */ 
/*     */   private <V extends Number, A> Future<V> write(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 337 */     int i = (paramBoolean) || (paramByteBuffer.hasRemaining()) ? 1 : 0;
/*     */ 
/* 339 */     int j = 0;
/* 340 */     if (isOpen()) {
/* 341 */       if (this.remoteAddress == null) {
/* 342 */         throw new NotYetConnectedException();
/*     */       }
/* 344 */       synchronized (this.writeLock) {
/* 345 */         if (this.writeKilled)
/* 346 */           throw new IllegalStateException("Writing not allowed due to timeout or cancellation");
/* 347 */         if (this.writing)
/* 348 */           throw new WritePendingException();
/* 349 */         if (this.writeShutdown) {
/* 350 */           j = 1;
/*     */         }
/* 352 */         else if (i != 0)
/* 353 */           this.writing = true;
/*     */       }
/*     */     }
/*     */     else {
/* 357 */       j = 1;
/*     */     }
/*     */ 
/* 361 */     if (j != 0) {
/* 362 */       ??? = new ClosedChannelException();
/* 363 */       if (paramCompletionHandler == null)
/* 364 */         return CompletedFuture.withFailure((Throwable)???);
/* 365 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)???);
/* 366 */       return null;
/*     */     }
/*     */ 
/* 370 */     if (i == 0) {
/* 371 */       ??? = paramBoolean ? Long.valueOf(0L) : Integer.valueOf(0);
/* 372 */       if (paramCompletionHandler == null)
/* 373 */         return CompletedFuture.withResult(???);
/* 374 */       Invoker.invoke(this, paramCompletionHandler, paramA, ???, null);
/* 375 */       return null;
/*     */     }
/*     */ 
/* 378 */     return implWrite(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   public final Future<Integer> write(ByteBuffer paramByteBuffer)
/*     */   {
/* 383 */     return write(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null);
/*     */   }
/*     */ 
/*     */   public final <A> void write(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 393 */     if (paramCompletionHandler == null)
/* 394 */       throw new NullPointerException("'handler' is null");
/* 395 */     write(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   public final <A> void write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler)
/*     */   {
/* 407 */     if (paramCompletionHandler == null)
/* 408 */       throw new NullPointerException("'handler' is null");
/* 409 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 410 */       throw new IndexOutOfBoundsException();
/* 411 */     paramArrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
/* 412 */     write(true, null, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   public final AsynchronousSocketChannel bind(SocketAddress paramSocketAddress)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 420 */       begin();
/* 421 */       synchronized (this.stateLock) {
/* 422 */         if (this.state == 1)
/* 423 */           throw new ConnectionPendingException();
/* 424 */         if (this.localAddress != null)
/* 425 */           throw new AlreadyBoundException();
/* 426 */         InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*     */ 
/* 428 */         NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 429 */         Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 430 */         this.localAddress = Net.localAddress(this.fd);
/*     */       }
/*     */     } finally {
/* 433 */       end();
/*     */     }
/* 435 */     return this;
/*     */   }
/*     */ 
/*     */   public final SocketAddress getLocalAddress() throws IOException
/*     */   {
/* 440 */     if (!isOpen())
/* 441 */       throw new ClosedChannelException();
/* 442 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   public final <T> AsynchronousSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/* 449 */     if (paramSocketOption == null)
/* 450 */       throw new NullPointerException();
/* 451 */     if (!supportedOptions().contains(paramSocketOption))
/* 452 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     try
/*     */     {
/* 455 */       begin();
/* 456 */       if (this.writeShutdown)
/* 457 */         throw new IOException("Connection has been shutdown for writing");
/* 458 */       Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/* 459 */       return this;
/*     */     } finally {
/* 461 */       end();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final <T> T getOption(SocketOption<T> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 468 */     if (paramSocketOption == null)
/* 469 */       throw new NullPointerException();
/* 470 */     if (!supportedOptions().contains(paramSocketOption))
/* 471 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     try
/*     */     {
/* 474 */       begin();
/* 475 */       return Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*     */     } finally {
/* 477 */       end();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Set<SocketOption<?>> supportedOptions()
/*     */   {
/* 497 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */ 
/*     */   public final SocketAddress getRemoteAddress() throws IOException
/*     */   {
/* 502 */     if (!isOpen())
/* 503 */       throw new ClosedChannelException();
/* 504 */     return this.remoteAddress;
/*     */   }
/*     */ 
/*     */   public final AsynchronousSocketChannel shutdownInput() throws IOException
/*     */   {
/*     */     try {
/* 510 */       begin();
/* 511 */       if (this.remoteAddress == null)
/* 512 */         throw new NotYetConnectedException();
/* 513 */       synchronized (this.readLock) {
/* 514 */         if (!this.readShutdown) {
/* 515 */           Net.shutdown(this.fd, 0);
/* 516 */           this.readShutdown = true;
/*     */         }
/*     */       }
/*     */     } finally {
/* 520 */       end();
/*     */     }
/* 522 */     return this;
/*     */   }
/*     */ 
/*     */   public final AsynchronousSocketChannel shutdownOutput() throws IOException
/*     */   {
/*     */     try {
/* 528 */       begin();
/* 529 */       if (this.remoteAddress == null)
/* 530 */         throw new NotYetConnectedException();
/* 531 */       synchronized (this.writeLock) {
/* 532 */         if (!this.writeShutdown) {
/* 533 */           Net.shutdown(this.fd, 1);
/* 534 */           this.writeShutdown = true;
/*     */         }
/*     */       }
/*     */     } finally {
/* 538 */       end();
/*     */     }
/* 540 */     return this;
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 545 */     StringBuilder localStringBuilder = new StringBuilder();
/* 546 */     localStringBuilder.append(getClass().getName());
/* 547 */     localStringBuilder.append('[');
/* 548 */     synchronized (this.stateLock) {
/* 549 */       if (!isOpen()) {
/* 550 */         localStringBuilder.append("closed");
/*     */       } else {
/* 552 */         switch (this.state) {
/*     */         case 0:
/* 554 */           localStringBuilder.append("unconnected");
/* 555 */           break;
/*     */         case 1:
/* 557 */           localStringBuilder.append("connection-pending");
/* 558 */           break;
/*     */         case 2:
/* 560 */           localStringBuilder.append("connected");
/* 561 */           if (this.readShutdown)
/* 562 */             localStringBuilder.append(" ishut");
/* 563 */           if (this.writeShutdown)
/* 564 */             localStringBuilder.append(" oshut");
/*     */           break;
/*     */         }
/* 567 */         if (this.localAddress != null) {
/* 568 */           localStringBuilder.append(" local=");
/* 569 */           localStringBuilder.append(this.localAddress.toString());
/*     */         }
/* 571 */         if (this.remoteAddress != null) {
/* 572 */           localStringBuilder.append(" remote=");
/* 573 */           localStringBuilder.append(this.remoteAddress.toString());
/*     */         }
/*     */       }
/*     */     }
/* 577 */     localStringBuilder.append(']');
/* 578 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   private static class DefaultOptionsHolder
/*     */   {
/* 482 */     static final Set<SocketOption<?>> defaultOptions = defaultOptions();
/*     */ 
/*     */     private static Set<SocketOption<?>> defaultOptions() {
/* 485 */       HashSet localHashSet = new HashSet(5);
/* 486 */       localHashSet.add(StandardSocketOptions.SO_SNDBUF);
/* 487 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/* 488 */       localHashSet.add(StandardSocketOptions.SO_KEEPALIVE);
/* 489 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/* 490 */       localHashSet.add(StandardSocketOptions.TCP_NODELAY);
/* 491 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.AsynchronousSocketChannelImpl
 * JD-Core Version:    0.6.2
 */