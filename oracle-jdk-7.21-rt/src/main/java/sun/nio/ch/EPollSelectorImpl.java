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
/*     */ class EPollSelectorImpl extends SelectorImpl
/*     */ {
/*     */   protected int fd0;
/*     */   protected int fd1;
/*     */   EPollArrayWrapper pollWrapper;
/*     */   private Map<Integer, SelectionKeyImpl> fdToKey;
/*  53 */   private volatile boolean closed = false;
/*     */ 
/*  56 */   private Object interruptLock = new Object();
/*  57 */   private boolean interruptTriggered = false;
/*     */ 
/*     */   EPollSelectorImpl(SelectorProvider paramSelectorProvider)
/*     */   {
/*  64 */     super(paramSelectorProvider);
/*  65 */     long l = IOUtil.makePipe(false);
/*  66 */     this.fd0 = ((int)(l >>> 32));
/*  67 */     this.fd1 = ((int)l);
/*  68 */     this.pollWrapper = new EPollArrayWrapper();
/*  69 */     this.pollWrapper.initInterrupt(this.fd0, this.fd1);
/*  70 */     this.fdToKey = new HashMap();
/*     */   }
/*     */ 
/*     */   protected int doSelect(long paramLong)
/*     */     throws IOException
/*     */   {
/*  76 */     if (this.closed)
/*  77 */       throw new ClosedSelectorException();
/*  78 */     processDeregisterQueue();
/*     */     try {
/*  80 */       begin();
/*  81 */       this.pollWrapper.poll(paramLong);
/*     */     } finally {
/*  83 */       end();
/*     */     }
/*  85 */     processDeregisterQueue();
/*  86 */     int i = updateSelectedKeys();
/*  87 */     if (this.pollWrapper.interrupted())
/*     */     {
/*  89 */       this.pollWrapper.putEventOps(this.pollWrapper.interruptedIndex(), 0);
/*  90 */       synchronized (this.interruptLock) {
/*  91 */         this.pollWrapper.clearInterrupted();
/*  92 */         IOUtil.drain(this.fd0);
/*  93 */         this.interruptTriggered = false;
/*     */       }
/*     */     }
/*  96 */     return i;
/*     */   }
/*     */ 
/*     */   private int updateSelectedKeys()
/*     */   {
/* 104 */     int i = this.pollWrapper.updated;
/* 105 */     int j = 0;
/* 106 */     for (int k = 0; k < i; k++) {
/* 107 */       int m = this.pollWrapper.getDescriptor(k);
/* 108 */       SelectionKeyImpl localSelectionKeyImpl = (SelectionKeyImpl)this.fdToKey.get(Integer.valueOf(m));
/*     */ 
/* 110 */       if (localSelectionKeyImpl != null) {
/* 111 */         int n = this.pollWrapper.getEventOps(k);
/* 112 */         if (this.selectedKeys.contains(localSelectionKeyImpl)) {
/* 113 */           if (localSelectionKeyImpl.channel.translateAndSetReadyOps(n, localSelectionKeyImpl))
/* 114 */             j++;
/*     */         }
/*     */         else {
/* 117 */           localSelectionKeyImpl.channel.translateAndSetReadyOps(n, localSelectionKeyImpl);
/* 118 */           if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0) {
/* 119 */             this.selectedKeys.add(localSelectionKeyImpl);
/* 120 */             j++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 125 */     return j;
/*     */   }
/*     */ 
/*     */   protected void implClose() throws IOException {
/* 129 */     if (this.closed)
/* 130 */       return;
/* 131 */     this.closed = true;
/*     */ 
/* 134 */     synchronized (this.interruptLock) {
/* 135 */       this.interruptTriggered = true;
/*     */     }
/*     */ 
/* 138 */     FileDispatcherImpl.closeIntFD(this.fd0);
/* 139 */     FileDispatcherImpl.closeIntFD(this.fd1);
/*     */ 
/* 141 */     this.pollWrapper.closeEPollFD();
/*     */ 
/* 143 */     this.selectedKeys = null;
/*     */ 
/* 146 */     ??? = this.keys.iterator();
/* 147 */     while (((Iterator)???).hasNext()) {
/* 148 */       SelectionKeyImpl localSelectionKeyImpl = (SelectionKeyImpl)((Iterator)???).next();
/* 149 */       deregister(localSelectionKeyImpl);
/* 150 */       SelectableChannel localSelectableChannel = localSelectionKeyImpl.channel();
/* 151 */       if ((!localSelectableChannel.isOpen()) && (!localSelectableChannel.isRegistered()))
/* 152 */         ((SelChImpl)localSelectableChannel).kill();
/* 153 */       ((Iterator)???).remove();
/*     */     }
/*     */ 
/* 156 */     this.fd0 = -1;
/* 157 */     this.fd1 = -1;
/*     */   }
/*     */ 
/*     */   protected void implRegister(SelectionKeyImpl paramSelectionKeyImpl) {
/* 161 */     if (this.closed)
/* 162 */       throw new ClosedSelectorException();
/* 163 */     SelChImpl localSelChImpl = paramSelectionKeyImpl.channel;
/* 164 */     this.fdToKey.put(Integer.valueOf(localSelChImpl.getFDVal()), paramSelectionKeyImpl);
/* 165 */     this.pollWrapper.add(localSelChImpl);
/* 166 */     this.keys.add(paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   protected void implDereg(SelectionKeyImpl paramSelectionKeyImpl) throws IOException {
/* 170 */     assert (paramSelectionKeyImpl.getIndex() >= 0);
/* 171 */     SelChImpl localSelChImpl = paramSelectionKeyImpl.channel;
/* 172 */     int i = localSelChImpl.getFDVal();
/* 173 */     this.fdToKey.remove(Integer.valueOf(i));
/* 174 */     this.pollWrapper.release(localSelChImpl);
/* 175 */     paramSelectionKeyImpl.setIndex(-1);
/* 176 */     this.keys.remove(paramSelectionKeyImpl);
/* 177 */     this.selectedKeys.remove(paramSelectionKeyImpl);
/* 178 */     deregister(paramSelectionKeyImpl);
/* 179 */     SelectableChannel localSelectableChannel = paramSelectionKeyImpl.channel();
/* 180 */     if ((!localSelectableChannel.isOpen()) && (!localSelectableChannel.isRegistered()))
/* 181 */       ((SelChImpl)localSelectableChannel).kill();
/*     */   }
/*     */ 
/*     */   void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {
/* 185 */     if (this.closed)
/* 186 */       throw new ClosedSelectorException();
/* 187 */     this.pollWrapper.setInterest(paramSelectionKeyImpl.channel, paramInt);
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
 * Qualified Name:     sun.nio.ch.EPollSelectorImpl
 * JD-Core Version:    0.6.2
 */