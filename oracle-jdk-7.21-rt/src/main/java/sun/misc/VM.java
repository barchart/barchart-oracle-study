/*     */ package sun.misc;
/*     */ 
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class VM
/*     */ {
/*  43 */   private static boolean suspended = false;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int STATE_GREEN = 1;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int STATE_YELLOW = 2;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int STATE_RED = 3;
/* 149 */   private static volatile boolean booted = false;
/*     */ 
/* 172 */   private static long directMemory = 67108864L;
/*     */   private static boolean pageAlignDirectMemory;
/* 202 */   private static boolean defaultAllowArraySyntax = false;
/* 203 */   private static boolean allowArraySyntax = defaultAllowArraySyntax;
/*     */ 
/* 241 */   private static final Properties savedProps = new Properties();
/*     */ 
/* 304 */   private static volatile int finalRefCount = 0;
/*     */ 
/* 307 */   private static volatile int peakFinalRefCount = 0;
/*     */   private static final int JVMTI_THREAD_STATE_ALIVE = 1;
/*     */   private static final int JVMTI_THREAD_STATE_TERMINATED = 2;
/*     */   private static final int JVMTI_THREAD_STATE_RUNNABLE = 4;
/*     */   private static final int JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER = 1024;
/*     */   private static final int JVMTI_THREAD_STATE_WAITING_INDEFINITELY = 16;
/*     */   private static final int JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT = 32;
/*     */ 
/*  48 */   @Deprecated
/*     */   public static boolean threadsSuspended() { return suspended; }
/*     */ 
/*     */   public static boolean allowThreadSuspension(ThreadGroup paramThreadGroup, boolean paramBoolean)
/*     */   {
/*  52 */     return paramThreadGroup.allowThreadSuspension(paramBoolean);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static boolean suspendThreads()
/*     */   {
/*  58 */     suspended = true;
/*  59 */     return true;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void unsuspendThreads()
/*     */   {
/*  66 */     suspended = false;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void unsuspendSomeThreads()
/*     */   {
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static final int getState()
/*     */   {
/*  92 */     return 1;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void registerVMNotification(VMNotification paramVMNotification)
/*     */   {
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void asChange(int paramInt1, int paramInt2)
/*     */   {
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void asChange_otherthread(int paramInt1, int paramInt2)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static void booted()
/*     */   {
/* 157 */     booted = true;
/*     */   }
/*     */ 
/*     */   public static boolean isBooted() {
/* 161 */     return booted;
/*     */   }
/*     */ 
/*     */   public static long maxDirectMemory()
/*     */   {
/* 179 */     return directMemory;
/*     */   }
/*     */ 
/*     */   public static boolean isDirectMemoryPageAligned()
/*     */   {
/* 190 */     return pageAlignDirectMemory;
/*     */   }
/*     */ 
/*     */   public static boolean allowArraySyntax()
/*     */   {
/* 216 */     return allowArraySyntax;
/*     */   }
/*     */ 
/*     */   public static String getSavedProperty(String paramString)
/*     */   {
/* 232 */     if (savedProps.isEmpty()) {
/* 233 */       throw new IllegalStateException("Should be non-empty if initialized");
/*     */     }
/* 235 */     return savedProps.getProperty(paramString);
/*     */   }
/*     */ 
/*     */   public static void saveAndRemoveProperties(Properties paramProperties)
/*     */   {
/* 248 */     if (booted) {
/* 249 */       throw new IllegalStateException("System initialization has completed");
/*     */     }
/* 251 */     savedProps.putAll(paramProperties);
/*     */ 
/* 258 */     String str = (String)paramProperties.remove("sun.nio.MaxDirectMemorySize");
/* 259 */     if (str != null) {
/* 260 */       if (str.equals("-1"))
/*     */       {
/* 262 */         directMemory = Runtime.getRuntime().maxMemory();
/*     */       } else {
/* 264 */         long l = Long.parseLong(str);
/* 265 */         if (l > -1L) {
/* 266 */           directMemory = l;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 271 */     str = (String)paramProperties.remove("sun.nio.PageAlignDirectMemory");
/* 272 */     if ("true".equals(str)) {
/* 273 */       pageAlignDirectMemory = true;
/*     */     }
/*     */ 
/* 278 */     str = paramProperties.getProperty("sun.lang.ClassLoader.allowArraySyntax");
/* 279 */     allowArraySyntax = str == null ? defaultAllowArraySyntax : Boolean.parseBoolean(str);
/*     */ 
/* 285 */     paramProperties.remove("java.lang.Integer.IntegerCache.high");
/*     */ 
/* 288 */     paramProperties.remove("sun.zip.disableMemoryMapping");
/*     */ 
/* 291 */     paramProperties.remove("sun.java.launcher.diag");
/*     */   }
/*     */ 
/*     */   public static void initializeOSEnvironment()
/*     */   {
/* 298 */     if (!booted)
/* 299 */       OSEnvironment.initialize();
/*     */   }
/*     */ 
/*     */   public static int getFinalRefCount()
/*     */   {
/* 315 */     return finalRefCount;
/*     */   }
/*     */ 
/*     */   public static int getPeakFinalRefCount()
/*     */   {
/* 324 */     return peakFinalRefCount;
/*     */   }
/*     */ 
/*     */   public static void addFinalRefCount(int paramInt)
/*     */   {
/* 336 */     finalRefCount += paramInt;
/* 337 */     if (finalRefCount > peakFinalRefCount)
/* 338 */       peakFinalRefCount = finalRefCount;
/*     */   }
/*     */ 
/*     */   public static Thread.State toThreadState(int paramInt)
/*     */   {
/* 346 */     if ((paramInt & 0x4) != 0)
/* 347 */       return Thread.State.RUNNABLE;
/* 348 */     if ((paramInt & 0x400) != 0)
/* 349 */       return Thread.State.BLOCKED;
/* 350 */     if ((paramInt & 0x10) != 0)
/* 351 */       return Thread.State.WAITING;
/* 352 */     if ((paramInt & 0x20) != 0)
/* 353 */       return Thread.State.TIMED_WAITING;
/* 354 */     if ((paramInt & 0x2) != 0)
/* 355 */       return Thread.State.TERMINATED;
/* 356 */     if ((paramInt & 0x1) == 0) {
/* 357 */       return Thread.State.NEW;
/*     */     }
/* 359 */     return Thread.State.RUNNABLE;
/*     */   }
/*     */ 
/*     */   private static native void initialize();
/*     */ 
/*     */   static
/*     */   {
/* 375 */     initialize();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.VM
 * JD-Core Version:    0.6.2
 */