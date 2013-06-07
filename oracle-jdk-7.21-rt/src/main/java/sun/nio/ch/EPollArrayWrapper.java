/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ class EPollArrayWrapper
/*     */ {
/*     */   static final int EPOLLIN = 1;
/*     */   static final int EPOLL_CTL_ADD = 1;
/*     */   static final int EPOLL_CTL_DEL = 2;
/*     */   static final int EPOLL_CTL_MOD = 3;
/*  73 */   static final int SIZE_EPOLLEVENT = sizeofEPollEvent();
/*     */   static final int EVENT_OFFSET = 0;
/*  75 */   static final int DATA_OFFSET = offsetofData();
/*  76 */   static final int FD_OFFSET = DATA_OFFSET;
/*  77 */   static final int NUM_EPOLLEVENTS = Math.min(fdLimit(), 8192);
/*     */   private final long pollArrayAddress;
/*     */   private final HashSet<SelChImpl> idleSet;
/* 118 */   private LinkedList<Updator> updateList = new LinkedList();
/*     */   private AllocatedNativeObject pollArray;
/*     */   final int epfd;
/*     */   int outgoingInterruptFD;
/*     */   int incomingInterruptFD;
/*     */   int interruptedIndex;
/*     */   int updated;
/* 271 */   boolean interrupted = false;
/*     */ 
/*     */   EPollArrayWrapper()
/*     */   {
/*  87 */     this.epfd = epollCreate();
/*     */ 
/*  90 */     int i = NUM_EPOLLEVENTS * SIZE_EPOLLEVENT;
/*  91 */     this.pollArray = new AllocatedNativeObject(i, true);
/*  92 */     this.pollArrayAddress = this.pollArray.address();
/*     */ 
/*  94 */     for (int j = 0; j < NUM_EPOLLEVENTS; j++) {
/*  95 */       putEventOps(j, 0);
/*  96 */       putData(j, 0L);
/*     */     }
/*     */ 
/* 100 */     this.idleSet = new HashSet();
/*     */   }
/*     */ 
/*     */   void initInterrupt(int paramInt1, int paramInt2)
/*     */   {
/* 139 */     this.outgoingInterruptFD = paramInt2;
/* 140 */     this.incomingInterruptFD = paramInt1;
/* 141 */     epollCtl(this.epfd, 1, paramInt1, 1);
/*     */   }
/*     */ 
/*     */   void putEventOps(int paramInt1, int paramInt2) {
/* 145 */     int i = SIZE_EPOLLEVENT * paramInt1 + 0;
/* 146 */     this.pollArray.putInt(i, paramInt2);
/*     */   }
/*     */ 
/*     */   void putData(int paramInt, long paramLong) {
/* 150 */     int i = SIZE_EPOLLEVENT * paramInt + DATA_OFFSET;
/* 151 */     this.pollArray.putLong(i, paramLong);
/*     */   }
/*     */ 
/*     */   void putDescriptor(int paramInt1, int paramInt2) {
/* 155 */     int i = SIZE_EPOLLEVENT * paramInt1 + FD_OFFSET;
/* 156 */     this.pollArray.putInt(i, paramInt2);
/*     */   }
/*     */ 
/*     */   int getEventOps(int paramInt) {
/* 160 */     int i = SIZE_EPOLLEVENT * paramInt + 0;
/* 161 */     return this.pollArray.getInt(i);
/*     */   }
/*     */ 
/*     */   int getDescriptor(int paramInt) {
/* 165 */     int i = SIZE_EPOLLEVENT * paramInt + FD_OFFSET;
/* 166 */     return this.pollArray.getInt(i);
/*     */   }
/*     */ 
/*     */   void setInterest(SelChImpl paramSelChImpl, int paramInt)
/*     */   {
/* 173 */     synchronized (this.updateList)
/*     */     {
/* 176 */       if (this.updateList.size() > 0) {
/* 177 */         Updator localUpdator = (Updator)this.updateList.getLast();
/* 178 */         if ((localUpdator.channel == paramSelChImpl) && (localUpdator.opcode == 1)) {
/* 179 */           localUpdator.events = paramInt;
/* 180 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 185 */       this.updateList.add(new Updator(paramSelChImpl, 3, paramInt));
/*     */     }
/*     */   }
/*     */ 
/*     */   void add(SelChImpl paramSelChImpl)
/*     */   {
/* 193 */     synchronized (this.updateList) {
/* 194 */       this.updateList.add(new Updator(paramSelChImpl, 1));
/*     */     }
/*     */   }
/*     */ 
/*     */   void release(SelChImpl paramSelChImpl)
/*     */   {
/* 202 */     synchronized (this.updateList)
/*     */     {
/* 204 */       for (Iterator localIterator = this.updateList.iterator(); localIterator.hasNext(); ) {
/* 205 */         if (((Updator)localIterator.next()).channel == paramSelChImpl) {
/* 206 */           localIterator.remove();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 211 */       this.idleSet.remove(paramSelChImpl);
/*     */ 
/* 214 */       epollCtl(this.epfd, 2, paramSelChImpl.getFDVal(), 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   void closeEPollFD()
/*     */     throws IOException
/*     */   {
/* 222 */     FileDispatcherImpl.closeIntFD(this.epfd);
/* 223 */     this.pollArray.free();
/*     */   }
/*     */ 
/*     */   int poll(long paramLong) throws IOException {
/* 227 */     updateRegistrations();
/* 228 */     this.updated = epollWait(this.pollArrayAddress, NUM_EPOLLEVENTS, paramLong, this.epfd);
/* 229 */     for (int i = 0; i < this.updated; i++) {
/* 230 */       if (getDescriptor(i) == this.incomingInterruptFD) {
/* 231 */         this.interruptedIndex = i;
/* 232 */         this.interrupted = true;
/* 233 */         break;
/*     */       }
/*     */     }
/* 236 */     return this.updated;
/*     */   }
/*     */ 
/*     */   void updateRegistrations()
/*     */   {
/* 243 */     synchronized (this.updateList) {
/* 244 */       Updator localUpdator = null;
/* 245 */       while ((localUpdator = (Updator)this.updateList.poll()) != null) {
/* 246 */         SelChImpl localSelChImpl = localUpdator.channel;
/* 247 */         if (localSelChImpl.isOpen())
/*     */         {
/*     */           boolean bool;
/* 252 */           if (localUpdator.events == 0) {
/* 253 */             bool = this.idleSet.add(localUpdator.channel);
/*     */ 
/* 255 */             if ((bool) && (localUpdator.opcode == 3))
/* 256 */               epollCtl(this.epfd, 2, localSelChImpl.getFDVal(), 0);
/*     */           }
/*     */           else
/*     */           {
/* 260 */             bool = false;
/* 261 */             if (!this.idleSet.isEmpty())
/* 262 */               bool = this.idleSet.remove(localUpdator.channel);
/* 263 */             int i = bool ? 1 : localUpdator.opcode;
/* 264 */             epollCtl(this.epfd, i, localSelChImpl.getFDVal(), localUpdator.events);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void interrupt()
/*     */   {
/* 274 */     interrupt(this.outgoingInterruptFD);
/*     */   }
/*     */ 
/*     */   public int interruptedIndex() {
/* 278 */     return this.interruptedIndex;
/*     */   }
/*     */ 
/*     */   boolean interrupted() {
/* 282 */     return this.interrupted;
/*     */   }
/*     */ 
/*     */   void clearInterrupted() {
/* 286 */     this.interrupted = false; } 
/*     */   private native int epollCreate();
/*     */ 
/*     */   private native void epollCtl(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   private native int epollWait(long paramLong1, int paramInt1, long paramLong2, int paramInt2) throws IOException;
/*     */ 
/*     */   private static native int sizeofEPollEvent();
/*     */ 
/*     */   private static native int offsetofData();
/*     */ 
/*     */   private static native int fdLimit();
/*     */ 
/*     */   private static native void interrupt(int paramInt);
/*     */ 
/*     */   private static native void init();
/*     */ 
/* 290 */   static { init(); }
/*     */ 
/*     */ 
/*     */   private static class Updator
/*     */   {
/*     */     SelChImpl channel;
/*     */     int opcode;
/*     */     int events;
/*     */ 
/*     */     Updator(SelChImpl paramSelChImpl, int paramInt1, int paramInt2)
/*     */     {
/* 109 */       this.channel = paramSelChImpl;
/* 110 */       this.opcode = paramInt1;
/* 111 */       this.events = paramInt2;
/*     */     }
/*     */     Updator(SelChImpl paramSelChImpl, int paramInt) {
/* 114 */       this(paramSelChImpl, paramInt, 0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.EPollArrayWrapper
 * JD-Core Version:    0.6.2
 */