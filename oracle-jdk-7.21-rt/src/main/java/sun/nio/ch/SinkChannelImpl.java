/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.Pipe.SinkChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ 
/*     */ class SinkChannelImpl extends Pipe.SinkChannel
/*     */   implements SelChImpl
/*     */ {
/* 212 */   private static NativeDispatcher nd = new FileDispatcherImpl();
/*     */   FileDescriptor fd;
/*     */   int fdVal;
/*  50 */   private volatile long thread = 0L;
/*     */ 
/*  53 */   private final Object lock = new Object();
/*     */ 
/*  57 */   private final Object stateLock = new Object();
/*     */   private static final int ST_UNINITIALIZED = -1;
/*     */   private static final int ST_INUSE = 0;
/*     */   private static final int ST_KILLED = 1;
/*  65 */   private volatile int state = -1;
/*     */ 
/*     */   public FileDescriptor getFD()
/*     */   {
/*  71 */     return this.fd;
/*     */   }
/*     */ 
/*     */   public int getFDVal() {
/*  75 */     return this.fdVal;
/*     */   }
/*     */ 
/*     */   SinkChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor) {
/*  79 */     super(paramSelectorProvider);
/*  80 */     this.fd = paramFileDescriptor;
/*  81 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/*  82 */     this.state = 0;
/*     */   }
/*     */ 
/*     */   protected void implCloseSelectableChannel() throws IOException {
/*  86 */     synchronized (this.stateLock) {
/*  87 */       if (this.state != 1)
/*  88 */         nd.preClose(this.fd);
/*  89 */       long l = this.thread;
/*  90 */       if (l != 0L)
/*  91 */         NativeThread.signal(l);
/*  92 */       if (!isRegistered())
/*  93 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void kill() throws IOException {
/*  98 */     synchronized (this.stateLock) {
/*  99 */       if (this.state == 1)
/* 100 */         return;
/* 101 */       if (this.state == -1) {
/* 102 */         this.state = 1;
/* 103 */         return;
/*     */       }
/* 105 */       assert ((!isOpen()) && (!isRegistered()));
/* 106 */       nd.close(this.fd);
/* 107 */       this.state = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
/* 112 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 117 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 118 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 119 */     int k = paramInt2;
/*     */ 
/* 121 */     if ((paramInt1 & 0x20) != 0) {
/* 122 */       throw new Error("POLLNVAL detected");
/*     */     }
/* 124 */     if ((paramInt1 & 0x18) != 0)
/*     */     {
/* 126 */       k = i;
/* 127 */       paramSelectionKeyImpl.nioReadyOps(k);
/* 128 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */ 
/* 131 */     if (((paramInt1 & 0x4) != 0) && ((i & 0x4) != 0))
/*     */     {
/* 133 */       k |= 4;
/*     */     }
/* 135 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 136 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */ 
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 140 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 144 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 148 */     if (paramInt == 4)
/* 149 */       paramInt = 4;
/* 150 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, paramInt);
/*     */   }
/*     */ 
/*     */   private void ensureOpen() throws IOException {
/* 154 */     if (!isOpen())
/* 155 */       throw new ClosedChannelException();
/*     */   }
/*     */ 
/*     */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/* 159 */     ensureOpen();
/* 160 */     synchronized (this.lock) {
/* 161 */       int i = 0;
/*     */       try {
/* 163 */         begin();
/* 164 */         if (!isOpen()) {
/* 165 */           j = 0;
/*     */ 
/* 172 */           this.thread = 0L;
/* 173 */           end((i > 0) || (i == -2));
/* 174 */           assert (IOStatus.check(i)); return j;
/*     */         }
/* 166 */         this.thread = NativeThread.current();
/*     */         do
/* 168 */           i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd, this.lock);
/* 169 */         while ((i == -3) && (isOpen()));
/* 170 */         int j = IOStatus.normalize(i);
/*     */ 
/* 172 */         this.thread = 0L;
/* 173 */         end((i > 0) || (i == -2));
/* 174 */         assert (IOStatus.check(i)); return j;
/*     */       }
/*     */       finally
/*     */       {
/* 172 */         this.thread = 0L;
/* 173 */         end((i > 0) || (i == -2));
/* 174 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError(); 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long write(ByteBuffer[] paramArrayOfByteBuffer) throws IOException {
/* 180 */     if (paramArrayOfByteBuffer == null)
/* 181 */       throw new NullPointerException();
/* 182 */     ensureOpen();
/* 183 */     synchronized (this.lock) {
/* 184 */       long l1 = 0L;
/*     */       try {
/* 186 */         begin();
/* 187 */         if (!isOpen()) {
/* 188 */           l2 = 0L;
/*     */ 
/* 195 */           this.thread = 0L;
/* 196 */           end((l1 > 0L) || (l1 == -2L));
/* 197 */           assert (IOStatus.check(l1)); return l2;
/*     */         }
/* 189 */         this.thread = NativeThread.current();
/*     */         do
/* 191 */           l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, nd);
/* 192 */         while ((l1 == -3L) && (isOpen()));
/* 193 */         long l2 = IOStatus.normalize(l1);
/*     */ 
/* 195 */         this.thread = 0L;
/* 196 */         end((l1 > 0L) || (l1 == -2L));
/* 197 */         assert (IOStatus.check(l1)); return l2;
/*     */       }
/*     */       finally
/*     */       {
/* 195 */         this.thread = 0L;
/* 196 */         end((l1 > 0L) || (l1 == -2L));
/* 197 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 205 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 206 */       throw new IndexOutOfBoundsException();
/* 207 */     return write(Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2));
/*     */   }
/*     */ 
/*     */   static {
/* 211 */     Util.load();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SinkChannelImpl
 * JD-Core Version:    0.6.2
 */