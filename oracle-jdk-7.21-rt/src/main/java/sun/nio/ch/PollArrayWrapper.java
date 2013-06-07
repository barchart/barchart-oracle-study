/*     */ package sun.nio.ch;
/*     */ 
/*     */ class PollArrayWrapper extends AbstractPollArrayWrapper
/*     */ {
/*     */   static final short POLLCONN = 4;
/*     */   int interruptFD;
/*     */ 
/*     */   PollArrayWrapper(int paramInt)
/*     */   {
/*  52 */     paramInt = (paramInt + 1) * 8;
/*  53 */     this.pollArray = new AllocatedNativeObject(paramInt, false);
/*  54 */     this.pollArrayAddress = this.pollArray.address();
/*  55 */     this.totalChannels = 1;
/*     */   }
/*     */ 
/*     */   void initInterrupt(int paramInt1, int paramInt2) {
/*  59 */     this.interruptFD = paramInt2;
/*  60 */     putDescriptor(0, paramInt1);
/*  61 */     putEventOps(0, 1);
/*  62 */     putReventOps(0, 0);
/*     */   }
/*     */ 
/*     */   void release(int paramInt)
/*     */   {
/*     */   }
/*     */ 
/*     */   void free() {
/*  70 */     this.pollArray.free();
/*     */   }
/*     */ 
/*     */   void addEntry(SelChImpl paramSelChImpl)
/*     */   {
/*  77 */     putDescriptor(this.totalChannels, IOUtil.fdVal(paramSelChImpl.getFD()));
/*  78 */     putEventOps(this.totalChannels, 0);
/*  79 */     putReventOps(this.totalChannels, 0);
/*  80 */     this.totalChannels += 1;
/*     */   }
/*     */ 
/*     */   static void replaceEntry(PollArrayWrapper paramPollArrayWrapper1, int paramInt1, PollArrayWrapper paramPollArrayWrapper2, int paramInt2)
/*     */   {
/*  91 */     paramPollArrayWrapper2.putDescriptor(paramInt2, paramPollArrayWrapper1.getDescriptor(paramInt1));
/*  92 */     paramPollArrayWrapper2.putEventOps(paramInt2, paramPollArrayWrapper1.getEventOps(paramInt1));
/*  93 */     paramPollArrayWrapper2.putReventOps(paramInt2, paramPollArrayWrapper1.getReventOps(paramInt1));
/*     */   }
/*     */ 
/*     */   void grow(int paramInt)
/*     */   {
/* 104 */     PollArrayWrapper localPollArrayWrapper = new PollArrayWrapper(paramInt);
/*     */ 
/* 107 */     for (int i = 0; i < this.totalChannels; i++) {
/* 108 */       replaceEntry(this, i, localPollArrayWrapper, i);
/*     */     }
/*     */ 
/* 111 */     this.pollArray.free();
/* 112 */     this.pollArray = localPollArrayWrapper.pollArray;
/* 113 */     this.pollArrayAddress = this.pollArray.address();
/*     */   }
/*     */ 
/*     */   int poll(int paramInt1, int paramInt2, long paramLong) {
/* 117 */     return poll0(this.pollArrayAddress + paramInt2 * 8, paramInt1, paramLong);
/*     */   }
/*     */ 
/*     */   public void interrupt()
/*     */   {
/* 122 */     interrupt(this.interruptFD);
/*     */   }
/*     */ 
/*     */   private native int poll0(long paramLong1, int paramInt, long paramLong2);
/*     */ 
/*     */   private static native void interrupt(int paramInt);
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.PollArrayWrapper
 * JD-Core Version:    0.6.2
 */