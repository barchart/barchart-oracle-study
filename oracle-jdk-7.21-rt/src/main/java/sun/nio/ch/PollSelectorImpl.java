/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ClosedSelectorException;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ 
/*     */ class PollSelectorImpl extends AbstractPollSelectorImpl
/*     */ {
/*     */   private int fd0;
/*     */   private int fd1;
/*  48 */   private Object interruptLock = new Object();
/*  49 */   private boolean interruptTriggered = false;
/*     */ 
/*     */   PollSelectorImpl(SelectorProvider paramSelectorProvider)
/*     */   {
/*  56 */     super(paramSelectorProvider, 1, 1);
/*  57 */     long l = IOUtil.makePipe(false);
/*  58 */     this.fd0 = ((int)(l >>> 32));
/*  59 */     this.fd1 = ((int)l);
/*  60 */     this.pollWrapper = new PollArrayWrapper(10);
/*  61 */     this.pollWrapper.initInterrupt(this.fd0, this.fd1);
/*  62 */     this.channelArray = new SelectionKeyImpl[10];
/*     */   }
/*     */ 
/*     */   protected int doSelect(long paramLong)
/*     */     throws IOException
/*     */   {
/*  68 */     if (this.channelArray == null)
/*  69 */       throw new ClosedSelectorException();
/*  70 */     processDeregisterQueue();
/*     */     try {
/*  72 */       begin();
/*  73 */       this.pollWrapper.poll(this.totalChannels, 0, paramLong);
/*     */     } finally {
/*  75 */       end();
/*     */     }
/*  77 */     processDeregisterQueue();
/*  78 */     int i = updateSelectedKeys();
/*  79 */     if (this.pollWrapper.getReventOps(0) != 0)
/*     */     {
/*  81 */       this.pollWrapper.putReventOps(0, 0);
/*  82 */       synchronized (this.interruptLock) {
/*  83 */         IOUtil.drain(this.fd0);
/*  84 */         this.interruptTriggered = false;
/*     */       }
/*     */     }
/*  87 */     return i;
/*     */   }
/*     */ 
/*     */   protected void implCloseInterrupt() throws IOException
/*     */   {
/*  92 */     synchronized (this.interruptLock) {
/*  93 */       this.interruptTriggered = true;
/*     */     }
/*  95 */     FileDispatcherImpl.closeIntFD(this.fd0);
/*  96 */     FileDispatcherImpl.closeIntFD(this.fd1);
/*  97 */     this.fd0 = -1;
/*  98 */     this.fd1 = -1;
/*  99 */     this.pollWrapper.release(0);
/*     */   }
/*     */ 
/*     */   public Selector wakeup() {
/* 103 */     synchronized (this.interruptLock) {
/* 104 */       if (!this.interruptTriggered) {
/* 105 */         this.pollWrapper.interrupt();
/* 106 */         this.interruptTriggered = true;
/*     */       }
/*     */     }
/* 109 */     return this;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.PollSelectorImpl
 * JD-Core Version:    0.6.2
 */