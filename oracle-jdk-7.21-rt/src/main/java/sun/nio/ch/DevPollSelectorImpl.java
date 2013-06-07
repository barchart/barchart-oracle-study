/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ClosedSelectorException;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ class DevPollSelectorImpl extends SelectorImpl
/*     */ {
/*     */   protected int fd0;
/*     */   protected int fd1;
/*     */   DevPollArrayWrapper pollWrapper;
/*     */   private Map<Integer, SelectionKeyImpl> fdToKey;
/*  53 */   private boolean closed = false;
/*     */ 
/*  56 */   private Object closeLock = new Object();
/*     */ 
/*  59 */   private Object interruptLock = new Object();
/*  60 */   private boolean interruptTriggered = false;
/*     */ 
/*     */   DevPollSelectorImpl(SelectorProvider paramSelectorProvider)
/*     */   {
/*  67 */     super(paramSelectorProvider);
/*  68 */     long l = IOUtil.makePipe(false);
/*  69 */     this.fd0 = ((int)(l >>> 32));
/*  70 */     this.fd1 = ((int)l);
/*  71 */     this.pollWrapper = new DevPollArrayWrapper();
/*  72 */     this.pollWrapper.initInterrupt(this.fd0, this.fd1);
/*  73 */     this.fdToKey = new HashMap();
/*     */   }
/*     */ 
/*     */   protected int doSelect(long paramLong)
/*     */     throws IOException
/*     */   {
/*  79 */     if (this.closed)
/*  80 */       throw new ClosedSelectorException();
/*  81 */     processDeregisterQueue();
/*     */     try {
/*  83 */       begin();
/*  84 */       this.pollWrapper.poll(paramLong);
/*     */     } finally {
/*  86 */       end();
/*     */     }
/*  88 */     processDeregisterQueue();
/*  89 */     int i = updateSelectedKeys();
/*  90 */     if (this.pollWrapper.interrupted())
/*     */     {
/*  92 */       this.pollWrapper.putReventOps(this.pollWrapper.interruptedIndex(), 0);
/*  93 */       synchronized (this.interruptLock) {
/*  94 */         this.pollWrapper.clearInterrupted();
/*  95 */         IOUtil.drain(this.fd0);
/*  96 */         this.interruptTriggered = false;
/*     */       }
/*     */     }
/*  99 */     return i;
/*     */   }
/*     */ 
/*     */   private int updateSelectedKeys()
/*     */   {
/* 107 */     int i = this.pollWrapper.updated;
/* 108 */     int j = 0;
/* 109 */     for (int k = 0; k < i; k++) {
/* 110 */       int m = this.pollWrapper.getDescriptor(k);
/* 111 */       SelectionKeyImpl localSelectionKeyImpl = (SelectionKeyImpl)this.fdToKey.get(Integer.valueOf(m));
/*     */ 
/* 113 */       if (localSelectionKeyImpl != null) {
/* 114 */         int n = this.pollWrapper.getReventOps(k);
/* 115 */         if (this.selectedKeys.contains(localSelectionKeyImpl)) {
/* 116 */           if (localSelectionKeyImpl.channel.translateAndSetReadyOps(n, localSelectionKeyImpl))
/* 117 */             j++;
/*     */         }
/*     */         else {
/* 120 */           localSelectionKeyImpl.channel.translateAndSetReadyOps(n, localSelectionKeyImpl);
/* 121 */           if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0) {
/* 122 */             this.selectedKeys.add(localSelectionKeyImpl);
/* 123 */             j++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 128 */     return j;
/*     */   }
/*     */ 
/*     */   protected void implClose() throws IOException {
/* 132 */     if (this.closed)
/* 133 */       return;
/* 134 */     this.closed = true;
/*     */ 
/* 137 */     synchronized (this.interruptLock) {
/* 138 */       this.interruptTriggered = true;
/*     */     }
/*     */ 
/* 141 */     FileDispatcherImpl.closeIntFD(this.fd0);
/* 142 */     FileDispatcherImpl.closeIntFD(this.fd1);
/*     */ 
/* 144 */     this.pollWrapper.release(this.fd0);
/* 145 */     this.pollWrapper.closeDevPollFD();
/* 146 */     this.selectedKeys = null;
/*     */ 
/* 149 */     ??? = this.keys.iterator();
/* 150 */     while (((Iterator)???).hasNext()) {
/* 151 */       SelectionKeyImpl localSelectionKeyImpl = (SelectionKeyImpl)((Iterator)???).next();
/* 152 */       deregister(localSelectionKeyImpl);
/* 153 */       SelectableChannel localSelectableChannel = localSelectionKeyImpl.channel();
/* 154 */       if ((!localSelectableChannel.isOpen()) && (!localSelectableChannel.isRegistered()))
/* 155 */         ((SelChImpl)localSelectableChannel).kill();
/* 156 */       ((Iterator)???).remove();
/*     */     }
/* 158 */     this.fd0 = -1;
/* 159 */     this.fd1 = -1;
/*     */   }
/*     */ 
/*     */   protected void implRegister(SelectionKeyImpl paramSelectionKeyImpl) {
/* 163 */     int i = IOUtil.fdVal(paramSelectionKeyImpl.channel.getFD());
/* 164 */     this.fdToKey.put(Integer.valueOf(i), paramSelectionKeyImpl);
/* 165 */     this.keys.add(paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   protected void implDereg(SelectionKeyImpl paramSelectionKeyImpl) throws IOException {
/* 169 */     int i = paramSelectionKeyImpl.getIndex();
/* 170 */     assert (i >= 0);
/* 171 */     int j = paramSelectionKeyImpl.channel.getFDVal();
/* 172 */     this.fdToKey.remove(Integer.valueOf(j));
/* 173 */     this.pollWrapper.release(j);
/* 174 */     paramSelectionKeyImpl.setIndex(-1);
/* 175 */     this.keys.remove(paramSelectionKeyImpl);
/* 176 */     this.selectedKeys.remove(paramSelectionKeyImpl);
/* 177 */     deregister(paramSelectionKeyImpl);
/* 178 */     SelectableChannel localSelectableChannel = paramSelectionKeyImpl.channel();
/* 179 */     if ((!localSelectableChannel.isOpen()) && (!localSelectableChannel.isRegistered()))
/* 180 */       ((SelChImpl)localSelectableChannel).kill();
/*     */   }
/*     */ 
/*     */   void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {
/* 184 */     if (this.closed)
/* 185 */       throw new ClosedSelectorException();
/* 186 */     int i = IOUtil.fdVal(paramSelectionKeyImpl.channel.getFD());
/* 187 */     this.pollWrapper.setInterest(i, paramInt);
/*     */   }
/*     */ 
/*     */   public Selector wakeup() {
/* 191 */     synchronized (this.interruptLock) {
/* 192 */       if (!this.interruptTriggered) {
/* 193 */         this.pollWrapper.interrupt();
/* 194 */         this.interruptTriggered = true;
/*     */       }
/*     */     }
/* 197 */     return this;
/*     */   }
/*     */ 
/*     */   static {
/* 201 */     Util.load();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.DevPollSelectorImpl
 * JD-Core Version:    0.6.2
 */