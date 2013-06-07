/*     */ package java.util.concurrent;
/*     */ 
/*     */ import java.util.concurrent.locks.AbstractQueuedSynchronizer;
/*     */ 
/*     */ public class FutureTask<V>
/*     */   implements RunnableFuture<V>
/*     */ {
/*     */   private final FutureTask<V>.Sync sync;
/*     */ 
/*     */   public FutureTask(Callable<V> paramCallable)
/*     */   {
/*  74 */     if (paramCallable == null)
/*  75 */       throw new NullPointerException();
/*  76 */     this.sync = new Sync(paramCallable);
/*     */   }
/*     */ 
/*     */   public FutureTask(Runnable paramRunnable, V paramV)
/*     */   {
/*  92 */     this.sync = new Sync(Executors.callable(paramRunnable, paramV));
/*     */   }
/*     */ 
/*     */   public boolean isCancelled() {
/*  96 */     return this.sync.innerIsCancelled();
/*     */   }
/*     */ 
/*     */   public boolean isDone() {
/* 100 */     return this.sync.innerIsDone();
/*     */   }
/*     */ 
/*     */   public boolean cancel(boolean paramBoolean) {
/* 104 */     return this.sync.innerCancel(paramBoolean);
/*     */   }
/*     */ 
/*     */   public V get()
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 111 */     return this.sync.innerGet();
/*     */   }
/*     */ 
/*     */   public V get(long paramLong, TimeUnit paramTimeUnit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 119 */     return this.sync.innerGet(paramTimeUnit.toNanos(paramLong));
/*     */   }
/*     */ 
/*     */   protected void done()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void set(V paramV)
/*     */   {
/* 141 */     this.sync.innerSet(paramV);
/*     */   }
/*     */ 
/*     */   protected void setException(Throwable paramThrowable)
/*     */   {
/* 153 */     this.sync.innerSetException(paramThrowable);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 166 */     this.sync.innerRun();
/*     */   }
/*     */ 
/*     */   protected boolean runAndReset()
/*     */   {
/* 178 */     return this.sync.innerRunAndReset();
/*     */   }
/*     */ 
/*     */   private final class Sync extends AbstractQueuedSynchronizer
/*     */   {
/*     */     private static final long serialVersionUID = -7828117401763700385L;
/*     */     private static final int READY = 0;
/*     */     private static final int RUNNING = 1;
/*     */     private static final int RAN = 2;
/*     */     private static final int CANCELLED = 4;
/*     */     private final Callable<V> callable;
/*     */     private V result;
/*     */     private Throwable exception;
/*     */     private volatile Thread runner;
/*     */ 
/*     */     Sync()
/*     */     {
/*     */       Object localObject;
/* 216 */       this.callable = localObject;
/*     */     }
/*     */ 
/*     */     private boolean ranOrCancelled(int paramInt) {
/* 220 */       return (paramInt & 0x6) != 0;
/*     */     }
/*     */ 
/*     */     protected int tryAcquireShared(int paramInt)
/*     */     {
/* 227 */       return innerIsDone() ? 1 : -1;
/*     */     }
/*     */ 
/*     */     protected boolean tryReleaseShared(int paramInt)
/*     */     {
/* 235 */       this.runner = null;
/* 236 */       return true;
/*     */     }
/*     */ 
/*     */     boolean innerIsCancelled() {
/* 240 */       return getState() == 4;
/*     */     }
/*     */ 
/*     */     boolean innerIsDone() {
/* 244 */       return (ranOrCancelled(getState())) && (this.runner == null);
/*     */     }
/*     */ 
/*     */     V innerGet() throws InterruptedException, ExecutionException {
/* 248 */       acquireSharedInterruptibly(0);
/* 249 */       if (getState() == 4)
/* 250 */         throw new CancellationException();
/* 251 */       if (this.exception != null)
/* 252 */         throw new ExecutionException(this.exception);
/* 253 */       return this.result;
/*     */     }
/*     */ 
/*     */     V innerGet(long paramLong) throws InterruptedException, ExecutionException, TimeoutException {
/* 257 */       if (!tryAcquireSharedNanos(0, paramLong))
/* 258 */         throw new TimeoutException();
/* 259 */       if (getState() == 4)
/* 260 */         throw new CancellationException();
/* 261 */       if (this.exception != null)
/* 262 */         throw new ExecutionException(this.exception);
/* 263 */       return this.result;
/*     */     }
/*     */ 
/*     */     void innerSet(V paramV) {
/*     */       while (true) {
/* 268 */         int i = getState();
/* 269 */         if (i == 2)
/* 270 */           return;
/* 271 */         if (i == 4)
/*     */         {
/* 275 */           releaseShared(0);
/* 276 */           return;
/*     */         }
/* 278 */         if (compareAndSetState(i, 2)) {
/* 279 */           this.result = paramV;
/* 280 */           releaseShared(0);
/* 281 */           FutureTask.this.done();
/* 282 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void innerSetException(Throwable paramThrowable) {
/*     */       while (true) {
/* 289 */         int i = getState();
/* 290 */         if (i == 2)
/* 291 */           return;
/* 292 */         if (i == 4)
/*     */         {
/* 296 */           releaseShared(0);
/* 297 */           return;
/*     */         }
/* 299 */         if (compareAndSetState(i, 2)) {
/* 300 */           this.exception = paramThrowable;
/* 301 */           releaseShared(0);
/* 302 */           FutureTask.this.done();
/* 303 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     boolean innerCancel(boolean paramBoolean) {
/*     */       while (true) {
/* 310 */         int i = getState();
/* 311 */         if (ranOrCancelled(i))
/* 312 */           return false;
/* 313 */         if (compareAndSetState(i, 4))
/*     */           break;
/*     */       }
/* 316 */       if (paramBoolean) {
/* 317 */         Thread localThread = this.runner;
/* 318 */         if (localThread != null)
/* 319 */           localThread.interrupt();
/*     */       }
/* 321 */       releaseShared(0);
/* 322 */       FutureTask.this.done();
/* 323 */       return true;
/*     */     }
/*     */ 
/*     */     void innerRun() {
/* 327 */       if (!compareAndSetState(0, 1)) {
/* 328 */         return;
/*     */       }
/* 330 */       this.runner = Thread.currentThread();
/* 331 */       if (getState() == 1) {
/*     */         Object localObject;
/*     */         try {
/* 334 */           localObject = this.callable.call();
/*     */         } catch (Throwable localThrowable) {
/* 336 */           FutureTask.this.setException(localThrowable);
/* 337 */           return;
/*     */         }
/* 339 */         FutureTask.this.set(localObject);
/*     */       } else {
/* 341 */         releaseShared(0);
/*     */       }
/*     */     }
/*     */ 
/*     */     boolean innerRunAndReset() {
/* 346 */       if (!compareAndSetState(0, 1))
/* 347 */         return false;
/*     */       try {
/* 349 */         this.runner = Thread.currentThread();
/* 350 */         if (getState() == 1)
/* 351 */           this.callable.call();
/* 352 */         this.runner = null;
/* 353 */         return compareAndSetState(1, 0);
/*     */       } catch (Throwable localThrowable) {
/* 355 */         FutureTask.this.setException(localThrowable);
/* 356 */       }return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.FutureTask
 * JD-Core Version:    0.6.2
 */