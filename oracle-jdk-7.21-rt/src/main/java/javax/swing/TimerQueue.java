/*     */ package javax.swing;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.DelayQueue;
/*     */ import java.util.concurrent.Delayed;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import sun.awt.AppContext;
/*     */ 
/*     */ class TimerQueue
/*     */   implements Runnable
/*     */ {
/*  50 */   private static final Object sharedInstanceKey = new StringBuffer("TimerQueue.sharedInstanceKey");
/*     */ 
/*  52 */   private static final Object expiredTimersKey = new StringBuffer("TimerQueue.expiredTimersKey");
/*     */   private final DelayQueue<DelayedTimer> queue;
/*     */   private volatile boolean running;
/*     */   private final Lock runningLock;
/*  62 */   private static final Object classLock = new Object();
/*     */ 
/*  65 */   private static final long NANO_ORIGIN = System.nanoTime();
/*     */ 
/*     */   public TimerQueue()
/*     */   {
/*  72 */     this.queue = new DelayQueue();
/*     */ 
/*  74 */     this.runningLock = new ReentrantLock();
/*  75 */     startIfNeeded();
/*     */   }
/*     */ 
/*     */   public static TimerQueue sharedInstance()
/*     */   {
/*  80 */     synchronized (classLock) {
/*  81 */       TimerQueue localTimerQueue = (TimerQueue)SwingUtilities.appContextGet(sharedInstanceKey);
/*     */ 
/*  84 */       if (localTimerQueue == null) {
/*  85 */         localTimerQueue = new TimerQueue();
/*  86 */         SwingUtilities.appContextPut(sharedInstanceKey, localTimerQueue);
/*     */       }
/*  88 */       return localTimerQueue;
/*     */     }
/*     */   }
/*     */ 
/*     */   void startIfNeeded()
/*     */   {
/*  94 */     if (!this.running) {
/*  95 */       this.runningLock.lock();
/*     */       try {
/*  97 */         final ThreadGroup localThreadGroup = AppContext.getAppContext().getThreadGroup();
/*     */ 
/*  99 */         AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/* 102 */             Thread localThread = new Thread(localThreadGroup, TimerQueue.this, "TimerQueue");
/*     */ 
/* 104 */             localThread.setDaemon(true);
/* 105 */             localThread.setPriority(5);
/* 106 */             localThread.start();
/* 107 */             return null;
/*     */           }
/*     */         });
/* 110 */         this.running = true;
/*     */       } finally {
/* 112 */         this.runningLock.unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void addTimer(Timer paramTimer, long paramLong) {
/* 118 */     paramTimer.getLock().lock();
/*     */     try
/*     */     {
/* 121 */       if (!containsTimer(paramTimer)) {
/* 122 */         addTimer(new DelayedTimer(paramTimer, TimeUnit.MILLISECONDS.toNanos(paramLong) + now()));
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 127 */       paramTimer.getLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addTimer(DelayedTimer paramDelayedTimer) {
/* 132 */     assert ((paramDelayedTimer != null) && (!containsTimer(paramDelayedTimer.getTimer())));
/*     */ 
/* 134 */     Timer localTimer = paramDelayedTimer.getTimer();
/* 135 */     localTimer.getLock().lock();
/*     */     try {
/* 137 */       localTimer.delayedTimer = paramDelayedTimer;
/* 138 */       this.queue.add(paramDelayedTimer);
/*     */     } finally {
/* 140 */       localTimer.getLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeTimer(Timer paramTimer) {
/* 145 */     paramTimer.getLock().lock();
/*     */     try {
/* 147 */       if (paramTimer.delayedTimer != null) {
/* 148 */         this.queue.remove(paramTimer.delayedTimer);
/* 149 */         paramTimer.delayedTimer = null;
/*     */       }
/*     */     } finally {
/* 152 */       paramTimer.getLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean containsTimer(Timer paramTimer) {
/* 157 */     paramTimer.getLock().lock();
/*     */     try {
/* 159 */       return paramTimer.delayedTimer != null;
/*     */     } finally {
/* 161 */       paramTimer.getLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 167 */     this.runningLock.lock();
/*     */     try {
/*     */       while (true) if (this.running) {
/*     */           try {
/* 171 */             Timer localTimer = ((DelayedTimer)this.queue.take()).getTimer();
/* 172 */             localTimer.getLock().lock();
/*     */             try {
/* 174 */               DelayedTimer localDelayedTimer1 = localTimer.delayedTimer;
/* 175 */               if (localDelayedTimer1 != null)
/*     */               {
/* 181 */                 localTimer.post();
/* 182 */                 localTimer.delayedTimer = null;
/* 183 */                 if (localTimer.isRepeats()) {
/* 184 */                   localDelayedTimer1.setTime(now() + TimeUnit.MILLISECONDS.toNanos(localTimer.getDelay()));
/*     */ 
/* 187 */                   addTimer(localDelayedTimer1);
/*     */                 }
/*     */               }
/*     */             } catch (SecurityException localSecurityException) {
/*     */             } finally {
/* 192 */               localTimer.getLock().unlock();
/*     */             }
/*     */           }
/*     */           catch (InterruptedException localInterruptedException)
/*     */           {
/* 197 */             if (AppContext.getAppContext().isDisposed()) {
/* 198 */               break label144;
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/*     */     catch (ThreadDeath localThreadDeath)
/*     */     {
/* 205 */       label144: for (DelayedTimer localDelayedTimer2 : this.queue) {
/* 206 */         localDelayedTimer2.getTimer().cancelEvent();
/*     */       }
/* 208 */       throw localThreadDeath;
/*     */     } finally {
/* 210 */       this.running = false;
/* 211 */       this.runningLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 217 */     StringBuilder localStringBuilder = new StringBuilder();
/* 218 */     localStringBuilder.append("TimerQueue (");
/* 219 */     int i = 1;
/* 220 */     for (DelayedTimer localDelayedTimer : this.queue) {
/* 221 */       if (i == 0) {
/* 222 */         localStringBuilder.append(", ");
/*     */       }
/* 224 */       localStringBuilder.append(localDelayedTimer.getTimer().toString());
/* 225 */       i = 0;
/*     */     }
/* 227 */     localStringBuilder.append(")");
/* 228 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   private static long now()
/*     */   {
/* 235 */     return System.nanoTime() - NANO_ORIGIN;
/*     */   }
/*     */ 
/*     */   static class DelayedTimer
/*     */     implements Delayed
/*     */   {
/* 246 */     private static final AtomicLong sequencer = new AtomicLong(0L);
/*     */     private final long sequenceNumber;
/*     */     private volatile long time;
/*     */     private final Timer timer;
/*     */ 
/*     */     DelayedTimer(Timer paramTimer, long paramLong)
/*     */     {
/* 258 */       this.timer = paramTimer;
/* 259 */       this.time = paramLong;
/* 260 */       this.sequenceNumber = sequencer.getAndIncrement();
/*     */     }
/*     */ 
/*     */     public final long getDelay(TimeUnit paramTimeUnit)
/*     */     {
/* 265 */       return paramTimeUnit.convert(this.time - TimerQueue.access$000(), TimeUnit.NANOSECONDS);
/*     */     }
/*     */ 
/*     */     final void setTime(long paramLong) {
/* 269 */       this.time = paramLong;
/*     */     }
/*     */ 
/*     */     final Timer getTimer() {
/* 273 */       return this.timer;
/*     */     }
/*     */ 
/*     */     public int compareTo(Delayed paramDelayed) {
/* 277 */       if (paramDelayed == this) {
/* 278 */         return 0;
/*     */       }
/* 280 */       if ((paramDelayed instanceof DelayedTimer)) {
/* 281 */         DelayedTimer localDelayedTimer = (DelayedTimer)paramDelayed;
/* 282 */         long l2 = this.time - localDelayedTimer.time;
/* 283 */         if (l2 < 0L)
/* 284 */           return -1;
/* 285 */         if (l2 > 0L)
/* 286 */           return 1;
/* 287 */         if (this.sequenceNumber < localDelayedTimer.sequenceNumber) {
/* 288 */           return -1;
/*     */         }
/* 290 */         return 1;
/*     */       }
/*     */ 
/* 293 */       long l1 = getDelay(TimeUnit.NANOSECONDS) - paramDelayed.getDelay(TimeUnit.NANOSECONDS);
/*     */ 
/* 295 */       return l1 < 0L ? -1 : l1 == 0L ? 0 : 1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.TimerQueue
 * JD-Core Version:    0.6.2
 */