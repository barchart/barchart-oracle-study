/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class ThreadPool
/*     */ {
/*     */   private static final String DEFAULT_THREAD_POOL_THREAD_FACTORY = "java.nio.channels.DefaultThreadPool.threadFactory";
/*     */   private static final String DEFAULT_THREAD_POOL_INITIAL_SIZE = "java.nio.channels.DefaultThreadPool.initialSize";
/*  42 */   private static final ThreadFactory defaultThreadFactory = new ThreadFactory()
/*     */   {
/*     */     public Thread newThread(Runnable paramAnonymousRunnable) {
/*  45 */       Thread localThread = new Thread(paramAnonymousRunnable);
/*  46 */       localThread.setDaemon(true);
/*  47 */       return localThread;
/*     */     }
/*  42 */   };
/*     */   private final ExecutorService executor;
/*     */   private final boolean isFixed;
/*     */   private final int poolSize;
/*     */ 
/*     */   private ThreadPool(ExecutorService paramExecutorService, boolean paramBoolean, int paramInt)
/*     */   {
/*  64 */     this.executor = paramExecutorService;
/*  65 */     this.isFixed = paramBoolean;
/*  66 */     this.poolSize = paramInt;
/*     */   }
/*     */ 
/*     */   ExecutorService executor() {
/*  70 */     return this.executor;
/*     */   }
/*     */ 
/*     */   boolean isFixedThreadPool() {
/*  74 */     return this.isFixed;
/*     */   }
/*     */ 
/*     */   int poolSize() {
/*  78 */     return this.poolSize;
/*     */   }
/*     */ 
/*     */   static ThreadFactory defaultThreadFactory() {
/*  82 */     return defaultThreadFactory;
/*     */   }
/*     */ 
/*     */   static ThreadPool getDefault()
/*     */   {
/*  91 */     return DefaultThreadPoolHolder.defaultThreadPool;
/*     */   }
/*     */ 
/*     */   static ThreadPool createDefault()
/*     */   {
/*  97 */     int i = getDefaultThreadPoolInitialSize();
/*  98 */     if (i < 0) {
/*  99 */       i = Runtime.getRuntime().availableProcessors();
/*     */     }
/* 101 */     ThreadFactory localThreadFactory = getDefaultThreadPoolThreadFactory();
/* 102 */     if (localThreadFactory == null) {
/* 103 */       localThreadFactory = defaultThreadFactory;
/*     */     }
/* 105 */     ThreadPoolExecutor localThreadPoolExecutor = new ThreadPoolExecutor(0, 2147483647, 9223372036854775807L, TimeUnit.MILLISECONDS, new SynchronousQueue(), localThreadFactory);
/*     */ 
/* 110 */     return new ThreadPool(localThreadPoolExecutor, false, i);
/*     */   }
/*     */ 
/*     */   static ThreadPool create(int paramInt, ThreadFactory paramThreadFactory)
/*     */   {
/* 115 */     if (paramInt <= 0)
/* 116 */       throw new IllegalArgumentException("'nThreads' must be > 0");
/* 117 */     ExecutorService localExecutorService = Executors.newFixedThreadPool(paramInt, paramThreadFactory);
/* 118 */     return new ThreadPool(localExecutorService, true, paramInt);
/*     */   }
/*     */ 
/*     */   public static ThreadPool wrap(ExecutorService paramExecutorService, int paramInt)
/*     */   {
/* 123 */     if (paramExecutorService == null) {
/* 124 */       throw new NullPointerException("'executor' is null");
/*     */     }
/* 126 */     if ((paramExecutorService instanceof ThreadPoolExecutor)) {
/* 127 */       int i = ((ThreadPoolExecutor)paramExecutorService).getMaximumPoolSize();
/* 128 */       if (i == 2147483647) {
/* 129 */         if (paramInt < 0) {
/* 130 */           paramInt = Runtime.getRuntime().availableProcessors();
/*     */         }
/*     */         else {
/* 133 */           paramInt = 0;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 138 */     else if (paramInt < 0) {
/* 139 */       paramInt = 0;
/*     */     }
/* 141 */     return new ThreadPool(paramExecutorService, false, paramInt);
/*     */   }
/*     */ 
/*     */   private static int getDefaultThreadPoolInitialSize() {
/* 145 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.nio.channels.DefaultThreadPool.initialSize"));
/*     */ 
/* 147 */     if (str != null) {
/*     */       try {
/* 149 */         return Integer.parseInt(str);
/*     */       } catch (NumberFormatException localNumberFormatException) {
/* 151 */         throw new Error("Value of property 'java.nio.channels.DefaultThreadPool.initialSize' is invalid: " + localNumberFormatException);
/*     */       }
/*     */     }
/*     */ 
/* 155 */     return -1;
/*     */   }
/*     */ 
/*     */   private static ThreadFactory getDefaultThreadPoolThreadFactory() {
/* 159 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.nio.channels.DefaultThreadPool.threadFactory"));
/*     */ 
/* 161 */     if (str != null) {
/*     */       try {
/* 163 */         Class localClass = Class.forName(str, true, ClassLoader.getSystemClassLoader());
/*     */ 
/* 165 */         return (ThreadFactory)localClass.newInstance();
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 167 */         throw new Error(localClassNotFoundException);
/*     */       } catch (InstantiationException localInstantiationException) {
/* 169 */         throw new Error(localInstantiationException);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 171 */         throw new Error(localIllegalAccessException);
/*     */       }
/*     */     }
/* 174 */     return null;
/*     */   }
/*     */ 
/*     */   private static class DefaultThreadPoolHolder
/*     */   {
/*  86 */     static final ThreadPool defaultThreadPool = ThreadPool.createDefault();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.ThreadPool
 * JD-Core Version:    0.6.2
 */