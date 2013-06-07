/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.spi.AsynchronousChannelProvider;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ 
/*     */ final class KQueuePort extends Port
/*     */ {
/*     */   private static final int MAX_KEVENTS_TO_POLL = 512;
/*     */   private final int kqfd;
/*     */   private boolean closed;
/*     */   private final int[] sp;
/*  55 */   private final AtomicInteger wakeupCount = new AtomicInteger();
/*     */   private final long address;
/*     */   private final ArrayBlockingQueue<Event> queue;
/*  77 */   private final Event NEED_TO_POLL = new Event(null, 0);
/*  78 */   private final Event EXECUTE_TASK_OR_SHUTDOWN = new Event(null, 0);
/*     */ 
/*     */   KQueuePort(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool)
/*     */     throws IOException
/*     */   {
/*  83 */     super(paramAsynchronousChannelProvider, paramThreadPool);
/*     */ 
/*  86 */     this.kqfd = KQueue.kqueue();
/*     */ 
/*  89 */     int[] arrayOfInt = new int[2];
/*     */     try {
/*  91 */       socketpair(arrayOfInt);
/*     */ 
/*  94 */       KQueue.keventRegister(this.kqfd, arrayOfInt[0], -1, 1);
/*     */     } catch (IOException localIOException) {
/*  96 */       close0(this.kqfd);
/*  97 */       throw localIOException;
/*     */     }
/*  99 */     this.sp = arrayOfInt;
/*     */ 
/* 102 */     this.address = KQueue.allocatePollArray(512);
/*     */ 
/* 106 */     this.queue = new ArrayBlockingQueue(512);
/* 107 */     this.queue.offer(this.NEED_TO_POLL);
/*     */   }
/*     */ 
/*     */   KQueuePort start() {
/* 111 */     startThreads(new EventHandlerTask(null));
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */   private void implClose()
/*     */   {
/* 119 */     synchronized (this) {
/* 120 */       if (this.closed)
/* 121 */         return;
/* 122 */       this.closed = true;
/*     */     }
/* 124 */     KQueue.freePollArray(this.address);
/* 125 */     close0(this.sp[0]);
/* 126 */     close0(this.sp[1]);
/* 127 */     close0(this.kqfd);
/*     */   }
/*     */ 
/*     */   private void wakeup() {
/* 131 */     if (this.wakeupCount.incrementAndGet() == 1)
/*     */       try
/*     */       {
/* 134 */         interrupt(this.sp[1]);
/*     */       } catch (IOException localIOException) {
/* 136 */         throw new AssertionError(localIOException);
/*     */       }
/*     */   }
/*     */ 
/*     */   void executeOnHandlerTask(Runnable paramRunnable)
/*     */   {
/* 143 */     synchronized (this) {
/* 144 */       if (this.closed)
/* 145 */         throw new RejectedExecutionException();
/* 146 */       offerTask(paramRunnable);
/* 147 */       wakeup();
/*     */     }
/*     */   }
/*     */ 
/*     */   void shutdownHandlerTasks()
/*     */   {
/* 157 */     int i = threadCount();
/* 158 */     if (i == 0) {
/* 159 */       implClose();
/*     */     }
/*     */     else
/* 162 */       while (i-- > 0)
/* 163 */         wakeup();
/*     */   }
/*     */ 
/*     */   void startPoll(int paramInt1, int paramInt2)
/*     */   {
/* 173 */     int i = 0;
/* 174 */     int j = 17;
/* 175 */     if ((paramInt2 & 0x1) > 0)
/* 176 */       i = KQueue.keventRegister(this.kqfd, paramInt1, -1, j);
/* 177 */     if ((i == 0) && ((paramInt2 & 0x4) > 0))
/* 178 */       i = KQueue.keventRegister(this.kqfd, paramInt1, -2, j);
/* 179 */     if (i != 0)
/* 180 */       throw new InternalError("kevent failed: " + i);
/*     */   }
/*     */ 
/*     */   private static native void socketpair(int[] paramArrayOfInt)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void interrupt(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void drain1(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void close0(int paramInt);
/*     */ 
/*     */   static
/*     */   {
/* 329 */     Util.load();
/*     */   }
/*     */ 
/*     */   static class Event
/*     */   {
/*     */     final Port.PollableChannel channel;
/*     */     final int events;
/*     */ 
/*     */     Event(Port.PollableChannel paramPollableChannel, int paramInt)
/*     */     {
/*  66 */       this.channel = paramPollableChannel;
/*  67 */       this.events = paramInt;
/*     */     }
/*     */     Port.PollableChannel channel() {
/*  70 */       return this.channel; } 
/*  71 */     int events() { return this.events; }
/*     */ 
/*     */   }
/*     */ 
/*     */   private class EventHandlerTask
/*     */     implements Runnable
/*     */   {
/*     */     private EventHandlerTask()
/*     */     {
/*     */     }
/*     */ 
/*     */     private KQueuePort.Event poll()
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/* 196 */           int i = KQueue.keventPoll(KQueuePort.this.kqfd, KQueuePort.this.address, 512);
/*     */ 
/* 203 */           KQueuePort.this.fdToChannelLock.readLock().lock();
/*     */           try {
/* 205 */             while (i-- > 0) {
/* 206 */               long l = KQueue.getEvent(KQueuePort.this.address, i);
/* 207 */               int j = KQueue.getDescriptor(l);
/*     */               Object localObject1;
/* 210 */               if (j == KQueuePort.this.sp[0]) {
/* 211 */                 if (KQueuePort.this.wakeupCount.decrementAndGet() == 0)
/*     */                 {
/* 213 */                   KQueuePort.drain1(KQueuePort.this.sp[0]);
/*     */                 }
/*     */ 
/* 218 */                 if (i > 0) {
/* 219 */                   KQueuePort.this.queue.offer(KQueuePort.this.EXECUTE_TASK_OR_SHUTDOWN);
/*     */                 }
/*     */                 else {
/* 222 */                   localObject1 = KQueuePort.this.EXECUTE_TASK_OR_SHUTDOWN;
/*     */ 
/* 246 */                   KQueuePort.this.fdToChannelLock.readLock().unlock();
/*     */ 
/* 252 */                   return localObject1;
/*     */                 }
/*     */               }
/*     */               else
/*     */               {
/* 225 */                 localObject1 = (Port.PollableChannel)KQueuePort.this.fdToChannel.get(Integer.valueOf(j));
/* 226 */                 if (localObject1 != null) {
/* 227 */                   int k = KQueue.getFilter(l);
/* 228 */                   int m = 0;
/* 229 */                   if (k == -1)
/* 230 */                     m = 1;
/* 231 */                   else if (k == -2) {
/* 232 */                     m = 4;
/*     */                   }
/* 234 */                   KQueuePort.Event localEvent1 = new KQueuePort.Event((Port.PollableChannel)localObject1, m);
/*     */ 
/* 238 */                   if (i > 0) {
/* 239 */                     KQueuePort.this.queue.offer(localEvent1);
/*     */                   } else {
/* 241 */                     KQueuePort.Event localEvent2 = localEvent1;
/*     */ 
/* 246 */                     KQueuePort.this.fdToChannelLock.readLock().unlock();
/*     */ 
/* 252 */                     return localEvent2;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 246 */             KQueuePort.this.fdToChannelLock.readLock().unlock();
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 252 */         KQueuePort.this.queue.offer(KQueuePort.this.NEED_TO_POLL);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run() {
/* 257 */       Invoker.GroupAndInvokeCount localGroupAndInvokeCount = Invoker.getGroupAndInvokeCount();
/*     */ 
/* 259 */       boolean bool1 = localGroupAndInvokeCount != null;
/* 260 */       boolean bool2 = false;
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/* 265 */           if (bool1)
/* 266 */             localGroupAndInvokeCount.resetInvokeCount(); KQueuePort.Event localEvent;
/*     */           int i;
/*     */           try { bool2 = false;
/* 270 */             localEvent = (KQueuePort.Event)KQueuePort.this.queue.take();
/*     */ 
/* 274 */             if (localEvent == KQueuePort.this.NEED_TO_POLL)
/*     */               try {
/* 276 */                 localEvent = poll();
/*     */               } catch (IOException localIOException) {
/* 278 */                 localIOException.printStackTrace();
/*     */ 
/* 310 */                 i = KQueuePort.this.threadExit(this, bool2);
/* 311 */                 if ((i == 0) && (KQueuePort.this.isShutdown()))
/* 312 */                   KQueuePort.this.implClose();
/*     */                 return;
/*     */               }
/*     */           }
/*     */           catch (InterruptedException localInterruptedException)
/*     */           {
/*     */           }
/* 283 */           continue;
/*     */ 
/* 287 */           if (localEvent == KQueuePort.this.EXECUTE_TASK_OR_SHUTDOWN) {
/* 288 */             Runnable localRunnable = KQueuePort.this.pollTask();
/* 289 */             if (localRunnable == null)
/*     */             {
/*     */               return;
/*     */             }
/*     */ 
/* 294 */             bool2 = true;
/* 295 */             localRunnable.run();
/* 296 */             continue;
/*     */           }
/*     */ 
/*     */           try
/*     */           {
/* 301 */             localEvent.channel().onEvent(localEvent.events(), bool1);
/*     */           } catch (Error localError) {
/* 303 */             bool2 = true; throw localError;
/*     */           } catch (RuntimeException localRuntimeException) {
/* 305 */             bool2 = true; throw localRuntimeException;
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 310 */         int j = KQueuePort.this.threadExit(this, bool2);
/* 311 */         if ((j == 0) && (KQueuePort.this.isShutdown()))
/* 312 */           KQueuePort.this.implClose();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.KQueuePort
 * JD-Core Version:    0.6.2
 */