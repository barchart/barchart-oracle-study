/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ class KQueue
/*     */ {
/*  38 */   private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */ 
/*  50 */   private static final int SIZEOF_KQUEUEEVENT = keventSize();
/*  51 */   private static final int OFFSET_IDENT = identOffset();
/*  52 */   private static final int OFFSET_FILTER = filterOffset();
/*  53 */   private static final int OFFSET_FLAGS = flagsOffset();
/*     */   static final int EVFILT_READ = -1;
/*     */   static final int EVFILT_WRITE = -2;
/*     */   static final int EV_ADD = 1;
/*     */   static final int EV_ONESHOT = 16;
/*     */   static final int EV_CLEAR = 32;
/*     */ 
/*     */   static long allocatePollArray(int paramInt)
/*     */   {
/*  68 */     return unsafe.allocateMemory(paramInt * SIZEOF_KQUEUEEVENT);
/*     */   }
/*     */ 
/*     */   static void freePollArray(long paramLong)
/*     */   {
/*  75 */     unsafe.freeMemory(paramLong);
/*     */   }
/*     */ 
/*     */   static long getEvent(long paramLong, int paramInt)
/*     */   {
/*  82 */     return paramLong + SIZEOF_KQUEUEEVENT * paramInt;
/*     */   }
/*     */ 
/*     */   static int getDescriptor(long paramLong)
/*     */   {
/*  89 */     return unsafe.getInt(paramLong + OFFSET_IDENT);
/*     */   }
/*     */ 
/*     */   static int getFilter(long paramLong) {
/*  93 */     return unsafe.getShort(paramLong + OFFSET_FILTER);
/*     */   }
/*     */ 
/*     */   static int getFlags(long paramLong) {
/*  97 */     return unsafe.getShort(paramLong + OFFSET_FLAGS);
/*     */   }
/*     */ 
/*     */   private static native int keventSize();
/*     */ 
/*     */   private static native int identOffset();
/*     */ 
/*     */   private static native int filterOffset();
/*     */ 
/*     */   private static native int flagsOffset();
/*     */ 
/*     */   static native int kqueue()
/*     */     throws IOException;
/*     */ 
/*     */   static native int keventRegister(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   static native int keventPoll(int paramInt1, long paramLong, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   static
/*     */   {
/* 118 */     Util.load();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.KQueue
 * JD-Core Version:    0.6.2
 */