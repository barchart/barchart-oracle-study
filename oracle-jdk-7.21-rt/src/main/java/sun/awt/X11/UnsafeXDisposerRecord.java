/*    */ package sun.awt.X11;
/*    */ 
/*    */ import sun.java2d.DisposerRecord;
/*    */ import sun.misc.Unsafe;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ 
/*    */ class UnsafeXDisposerRecord
/*    */   implements DisposerRecord
/*    */ {
/* 31 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.UnsafeXDisposerRecord");
/* 32 */   private static Unsafe unsafe = XlibWrapper.unsafe;
/*    */   final long[] unsafe_ptrs;
/*    */   final long[] x_ptrs;
/*    */   final String name;
/*    */   volatile boolean disposed;
/*    */   final Throwable place;
/*    */ 
/*    */   public UnsafeXDisposerRecord(String paramString, long[] paramArrayOfLong1, long[] paramArrayOfLong2)
/*    */   {
/* 38 */     this.unsafe_ptrs = paramArrayOfLong1;
/* 39 */     this.x_ptrs = paramArrayOfLong2;
/* 40 */     this.name = paramString;
/* 41 */     if (XlibWrapper.isBuildInternal)
/* 42 */       this.place = new Throwable();
/*    */     else
/* 44 */       this.place = null;
/*    */   }
/*    */ 
/*    */   public UnsafeXDisposerRecord(String paramString, long[] paramArrayOfLong) {
/* 48 */     this.unsafe_ptrs = paramArrayOfLong;
/* 49 */     this.x_ptrs = null;
/* 50 */     this.name = paramString;
/* 51 */     if (XlibWrapper.isBuildInternal)
/* 52 */       this.place = new Throwable();
/*    */     else
/* 54 */       this.place = null;
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/* 59 */     XToolkit.awtLock();
/*    */     try {
/* 61 */       if (!this.disposed) {
/* 62 */         if ((XlibWrapper.isBuildInternal) && ("Java2D Disposer".equals(Thread.currentThread().getName())) && (log.isLoggable(900)))
/* 63 */           if (this.place != null)
/* 64 */             log.warning(this.name + " object was not disposed before finalization!", this.place);
/*    */           else
/* 66 */             log.warning(this.name + " object was not disposed before finalization!");
/*    */         long l;
/* 70 */         if (this.unsafe_ptrs != null) {
/* 71 */           for (l : this.unsafe_ptrs) {
/* 72 */             if (l != 0L) {
/* 73 */               unsafe.freeMemory(l);
/*    */             }
/*    */           }
/*    */         }
/* 77 */         if (this.x_ptrs != null) {
/* 78 */           for (l : this.x_ptrs) {
/* 79 */             if (l != 0L) {
/* 80 */               if (Native.getLong(l) != 0L) {
/* 81 */                 XlibWrapper.XFree(Native.getLong(l));
/*    */               }
/* 83 */               unsafe.freeMemory(l);
/*    */             }
/*    */           }
/*    */         }
/* 87 */         this.disposed = true;
/*    */       }
/*    */     } finally {
/* 90 */       XToolkit.awtUnlock();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.UnsafeXDisposerRecord
 * JD-Core Version:    0.6.2
 */