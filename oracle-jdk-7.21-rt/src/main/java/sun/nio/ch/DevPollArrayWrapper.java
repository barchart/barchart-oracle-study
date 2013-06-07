/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.BitSet;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ class DevPollArrayWrapper
/*     */ {
/*     */   static final short POLLIN = 1;
/*     */   static final short POLLPRI = 2;
/*     */   static final short POLLOUT = 4;
/*     */   static final short POLLRDNORM = 64;
/*     */   static final short POLLWRNORM = 4;
/*     */   static final short POLLRDBAND = 128;
/*     */   static final short POLLWRBAND = 256;
/*     */   static final short POLLNORM = 64;
/*     */   static final short POLLERR = 8;
/*     */   static final short POLLHUP = 16;
/*     */   static final short POLLNVAL = 32;
/*     */   static final short POLLREMOVE = 2048;
/*     */   static final short POLLCONN = 4;
/*     */   static final short SIZE_POLLFD = 8;
/*     */   static final short FD_OFFSET = 0;
/*     */   static final short EVENT_OFFSET = 4;
/*     */   static final short REVENT_OFFSET = 6;
/*     */   static final byte IGNORE = -1;
/*  74 */   static final int OPEN_MAX = fdLimit();
/*     */ 
/*  78 */   static final int NUM_POLLFDS = Math.min(OPEN_MAX - 1, 8192);
/*     */ 
/*  81 */   private final int INITIAL_PENDING_UPDATE_SIZE = 64;
/*     */ 
/*  84 */   private final int MAX_UPDATE_ARRAY_SIZE = Math.min(OPEN_MAX, 65536);
/*     */   private final AllocatedNativeObject pollArray;
/*     */   private final long pollArrayAddress;
/*     */   private int wfd;
/*     */   private int outgoingInterruptFD;
/*     */   private int incomingInterruptFD;
/*     */   private int interruptedIndex;
/*     */   int updated;
/* 108 */   private final Object updateLock = new Object();
/*     */   private int updateCount;
/* 114 */   private int[] updateDescriptors = new int[64];
/*     */ 
/* 120 */   private final byte[] eventsLow = new byte[this.MAX_UPDATE_ARRAY_SIZE];
/*     */   private Map<Integer, Byte> eventsHigh;
/* 125 */   private final BitSet registered = new BitSet();
/*     */ 
/* 294 */   boolean interrupted = false;
/*     */ 
/*     */   DevPollArrayWrapper()
/*     */   {
/* 128 */     int i = NUM_POLLFDS * 8;
/* 129 */     this.pollArray = new AllocatedNativeObject(i, true);
/* 130 */     this.pollArrayAddress = this.pollArray.address();
/* 131 */     this.wfd = init();
/* 132 */     if (OPEN_MAX > this.MAX_UPDATE_ARRAY_SIZE)
/* 133 */       this.eventsHigh = new HashMap();
/*     */   }
/*     */ 
/*     */   void initInterrupt(int paramInt1, int paramInt2) {
/* 137 */     this.outgoingInterruptFD = paramInt2;
/* 138 */     this.incomingInterruptFD = paramInt1;
/* 139 */     register(this.wfd, paramInt1, 1);
/*     */   }
/*     */ 
/*     */   void putReventOps(int paramInt1, int paramInt2) {
/* 143 */     int i = 8 * paramInt1 + 6;
/* 144 */     this.pollArray.putShort(i, (short)paramInt2);
/*     */   }
/*     */ 
/*     */   int getEventOps(int paramInt) {
/* 148 */     int i = 8 * paramInt + 4;
/* 149 */     return this.pollArray.getShort(i);
/*     */   }
/*     */ 
/*     */   int getReventOps(int paramInt) {
/* 153 */     int i = 8 * paramInt + 6;
/* 154 */     return this.pollArray.getShort(i);
/*     */   }
/*     */ 
/*     */   int getDescriptor(int paramInt) {
/* 158 */     int i = 8 * paramInt + 0;
/* 159 */     return this.pollArray.getInt(i);
/*     */   }
/*     */ 
/*     */   private void setUpdateEvents(int paramInt, byte paramByte) {
/* 163 */     if (paramInt < this.MAX_UPDATE_ARRAY_SIZE)
/* 164 */       this.eventsLow[paramInt] = paramByte;
/*     */     else
/* 166 */       this.eventsHigh.put(Integer.valueOf(paramInt), Byte.valueOf(paramByte));
/*     */   }
/*     */ 
/*     */   private byte getUpdateEvents(int paramInt)
/*     */   {
/* 171 */     if (paramInt < this.MAX_UPDATE_ARRAY_SIZE) {
/* 172 */       return this.eventsLow[paramInt];
/*     */     }
/* 174 */     Byte localByte = (Byte)this.eventsHigh.get(Integer.valueOf(paramInt));
/*     */ 
/* 176 */     return localByte.byteValue();
/*     */   }
/*     */ 
/*     */   void setInterest(int paramInt1, int paramInt2)
/*     */   {
/* 181 */     synchronized (this.updateLock)
/*     */     {
/* 184 */       int i = this.updateDescriptors.length;
/* 185 */       if (this.updateCount == i) {
/* 186 */         j = i + 64;
/* 187 */         int[] arrayOfInt = new int[j];
/* 188 */         System.arraycopy(this.updateDescriptors, 0, arrayOfInt, 0, i);
/* 189 */         this.updateDescriptors = arrayOfInt;
/*     */       }
/* 191 */       this.updateDescriptors[(this.updateCount++)] = paramInt1;
/*     */ 
/* 194 */       int j = (byte)paramInt2;
/* 195 */       assert ((j == paramInt2) && (j != -1));
/* 196 */       setUpdateEvents(paramInt1, j);
/*     */     }
/*     */   }
/*     */ 
/*     */   void release(int paramInt) {
/* 201 */     synchronized (this.updateLock)
/*     */     {
/* 203 */       setUpdateEvents(paramInt, (byte)-1);
/*     */ 
/* 206 */       if (this.registered.get(paramInt)) {
/* 207 */         register(this.wfd, paramInt, 2048);
/* 208 */         this.registered.clear(paramInt);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void closeDevPollFD() throws IOException {
/* 214 */     FileDispatcherImpl.closeIntFD(this.wfd);
/* 215 */     this.pollArray.free();
/*     */   }
/*     */ 
/*     */   int poll(long paramLong) throws IOException {
/* 219 */     updateRegistrations();
/* 220 */     this.updated = poll0(this.pollArrayAddress, NUM_POLLFDS, paramLong, this.wfd);
/* 221 */     for (int i = 0; i < this.updated; i++) {
/* 222 */       if (getDescriptor(i) == this.incomingInterruptFD) {
/* 223 */         this.interruptedIndex = i;
/* 224 */         this.interrupted = true;
/* 225 */         break;
/*     */       }
/*     */     }
/* 228 */     return this.updated;
/*     */   }
/*     */ 
/*     */   void updateRegistrations() throws IOException {
/* 232 */     synchronized (this.updateLock)
/*     */     {
/* 234 */       int i = 0;
/* 235 */       int j = 0;
/* 236 */       while (i < this.updateCount) {
/* 237 */         int k = this.updateDescriptors[i];
/* 238 */         short s = (short)getUpdateEvents(k);
/* 239 */         boolean bool = this.registered.get(k);
/*     */ 
/* 242 */         if (s != -1) {
/* 243 */           if (s == 0) {
/* 244 */             if (bool) {
/* 245 */               s = 2048;
/* 246 */               this.registered.clear(k);
/*     */             } else {
/* 248 */               s = -1;
/*     */             }
/*     */           }
/* 251 */           else if (!bool) {
/* 252 */             this.registered.set(k);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 258 */         if (s != -1)
/*     */         {
/* 260 */           if ((bool) && (s != 2048)) {
/* 261 */             putPollFD(this.pollArray, j, k, (short)2048);
/* 262 */             j++;
/*     */           }
/* 264 */           putPollFD(this.pollArray, j, k, s);
/* 265 */           j++;
/* 266 */           if (j >= NUM_POLLFDS - 1) {
/* 267 */             registerMultiple(this.wfd, this.pollArray.address(), j);
/* 268 */             j = 0;
/*     */           }
/*     */ 
/* 272 */           setUpdateEvents(k, (byte)-1);
/*     */         }
/* 274 */         i++;
/*     */       }
/*     */ 
/* 278 */       if (j > 0) {
/* 279 */         registerMultiple(this.wfd, this.pollArray.address(), j);
/*     */       }
/* 281 */       this.updateCount = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void putPollFD(AllocatedNativeObject paramAllocatedNativeObject, int paramInt1, int paramInt2, short paramShort)
/*     */   {
/* 288 */     int i = 8 * paramInt1;
/* 289 */     paramAllocatedNativeObject.putInt(i + 0, paramInt2);
/* 290 */     paramAllocatedNativeObject.putShort(i + 4, paramShort);
/* 291 */     paramAllocatedNativeObject.putShort(i + 6, (short)0);
/*     */   }
/*     */ 
/*     */   public void interrupt()
/*     */   {
/* 297 */     interrupt(this.outgoingInterruptFD);
/*     */   }
/*     */ 
/*     */   public int interruptedIndex() {
/* 301 */     return this.interruptedIndex;
/*     */   }
/*     */ 
/*     */   boolean interrupted() {
/* 305 */     return this.interrupted;
/*     */   }
/*     */ 
/*     */   void clearInterrupted() {
/* 309 */     this.interrupted = false;
/*     */   }
/*     */ 
/*     */   private native int init();
/*     */ 
/*     */   private native void register(int paramInt1, int paramInt2, int paramInt3);
/*     */ 
/*     */   private native void registerMultiple(int paramInt1, long paramLong, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   private native int poll0(long paramLong1, int paramInt1, long paramLong2, int paramInt2);
/*     */ 
/*     */   private static native void interrupt(int paramInt);
/*     */ 
/*     */   private static native int fdLimit();
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.DevPollArrayWrapper
 * JD-Core Version:    0.6.2
 */