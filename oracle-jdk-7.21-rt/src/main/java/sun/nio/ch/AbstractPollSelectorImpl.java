/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ClosedSelectorException;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ abstract class AbstractPollSelectorImpl extends SelectorImpl
/*     */ {
/*     */   PollArrayWrapper pollWrapper;
/*  47 */   protected final int INIT_CAP = 10;
/*     */   protected SelectionKeyImpl[] channelArray;
/*  53 */   protected int channelOffset = 0;
/*     */   protected int totalChannels;
/*  59 */   private boolean closed = false;
/*     */ 
/*  62 */   private Object closeLock = new Object();
/*     */ 
/*     */   AbstractPollSelectorImpl(SelectorProvider paramSelectorProvider, int paramInt1, int paramInt2) {
/*  65 */     super(paramSelectorProvider);
/*  66 */     this.totalChannels = paramInt1;
/*  67 */     this.channelOffset = paramInt2;
/*     */   }
/*     */ 
/*     */   void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {
/*  71 */     synchronized (this.closeLock) {
/*  72 */       if (this.closed)
/*  73 */         throw new ClosedSelectorException();
/*  74 */       this.pollWrapper.putEventOps(paramSelectionKeyImpl.getIndex(), paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Selector wakeup() {
/*  79 */     this.pollWrapper.interrupt();
/*  80 */     return this;
/*     */   }
/*     */ 
/*     */   protected abstract int doSelect(long paramLong) throws IOException;
/*     */ 
/*     */   protected void implClose() throws IOException {
/*  86 */     synchronized (this.closeLock) {
/*  87 */       if (this.closed)
/*  88 */         return;
/*  89 */       this.closed = true;
/*     */ 
/*  91 */       for (int i = this.channelOffset; i < this.totalChannels; i++) {
/*  92 */         SelectionKeyImpl localSelectionKeyImpl = this.channelArray[i];
/*  93 */         assert (localSelectionKeyImpl.getIndex() != -1);
/*  94 */         localSelectionKeyImpl.setIndex(-1);
/*  95 */         deregister(localSelectionKeyImpl);
/*  96 */         SelectableChannel localSelectableChannel = this.channelArray[i].channel();
/*  97 */         if ((!localSelectableChannel.isOpen()) && (!localSelectableChannel.isRegistered()))
/*  98 */           ((SelChImpl)localSelectableChannel).kill();
/*     */       }
/* 100 */       implCloseInterrupt();
/* 101 */       this.pollWrapper.free();
/* 102 */       this.pollWrapper = null;
/* 103 */       this.selectedKeys = null;
/* 104 */       this.channelArray = null;
/* 105 */       this.totalChannels = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void implCloseInterrupt()
/*     */     throws IOException;
/*     */ 
/*     */   protected int updateSelectedKeys()
/*     */   {
/* 117 */     int i = 0;
/*     */ 
/* 119 */     for (int j = this.channelOffset; j < this.totalChannels; j++) {
/* 120 */       int k = this.pollWrapper.getReventOps(j);
/* 121 */       if (k != 0) {
/* 122 */         SelectionKeyImpl localSelectionKeyImpl = this.channelArray[j];
/* 123 */         this.pollWrapper.putReventOps(j, 0);
/* 124 */         if (this.selectedKeys.contains(localSelectionKeyImpl)) {
/* 125 */           if (localSelectionKeyImpl.channel.translateAndSetReadyOps(k, localSelectionKeyImpl))
/* 126 */             i++;
/*     */         }
/*     */         else {
/* 129 */           localSelectionKeyImpl.channel.translateAndSetReadyOps(k, localSelectionKeyImpl);
/* 130 */           if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0) {
/* 131 */             this.selectedKeys.add(localSelectionKeyImpl);
/* 132 */             i++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 137 */     return i;
/*     */   }
/*     */ 
/*     */   protected void implRegister(SelectionKeyImpl paramSelectionKeyImpl) {
/* 141 */     synchronized (this.closeLock) {
/* 142 */       if (this.closed) {
/* 143 */         throw new ClosedSelectorException();
/*     */       }
/*     */ 
/* 146 */       if (this.channelArray.length == this.totalChannels)
/*     */       {
/* 148 */         int i = this.pollWrapper.totalChannels * 2;
/* 149 */         SelectionKeyImpl[] arrayOfSelectionKeyImpl = new SelectionKeyImpl[i];
/*     */ 
/* 151 */         for (int j = this.channelOffset; j < this.totalChannels; j++)
/* 152 */           arrayOfSelectionKeyImpl[j] = this.channelArray[j];
/* 153 */         this.channelArray = arrayOfSelectionKeyImpl;
/*     */ 
/* 155 */         this.pollWrapper.grow(i);
/*     */       }
/* 157 */       this.channelArray[this.totalChannels] = paramSelectionKeyImpl;
/* 158 */       paramSelectionKeyImpl.setIndex(this.totalChannels);
/* 159 */       this.pollWrapper.addEntry(paramSelectionKeyImpl.channel);
/* 160 */       this.totalChannels += 1;
/* 161 */       this.keys.add(paramSelectionKeyImpl);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implDereg(SelectionKeyImpl paramSelectionKeyImpl)
/*     */     throws IOException
/*     */   {
/* 170 */     int i = paramSelectionKeyImpl.getIndex();
/* 171 */     assert (i >= 0);
/* 172 */     if (i != this.totalChannels - 1)
/*     */     {
/* 174 */       localObject = this.channelArray[(this.totalChannels - 1)];
/* 175 */       this.channelArray[i] = localObject;
/* 176 */       ((SelectionKeyImpl)localObject).setIndex(i);
/* 177 */       this.pollWrapper.release(i);
/* 178 */       PollArrayWrapper.replaceEntry(this.pollWrapper, this.totalChannels - 1, this.pollWrapper, i);
/*     */     }
/*     */     else {
/* 181 */       this.pollWrapper.release(i);
/*     */     }
/*     */ 
/* 184 */     this.channelArray[(this.totalChannels - 1)] = null;
/* 185 */     this.totalChannels -= 1;
/* 186 */     this.pollWrapper.totalChannels -= 1;
/* 187 */     paramSelectionKeyImpl.setIndex(-1);
/*     */ 
/* 189 */     this.keys.remove(paramSelectionKeyImpl);
/* 190 */     this.selectedKeys.remove(paramSelectionKeyImpl);
/* 191 */     deregister(paramSelectionKeyImpl);
/* 192 */     Object localObject = paramSelectionKeyImpl.channel();
/* 193 */     if ((!((SelectableChannel)localObject).isOpen()) && (!((SelectableChannel)localObject).isRegistered()))
/* 194 */       ((SelChImpl)localObject).kill();
/*     */   }
/*     */ 
/*     */   static {
/* 198 */     Util.load();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.AbstractPollSelectorImpl
 * JD-Core Version:    0.6.2
 */