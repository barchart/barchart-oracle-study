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
/*     */ final class EPollPort extends Port
/*     */ {
/*     */   private static final int MAX_EPOLL_EVENTS = 512;
/*     */   private static final int ENOENT = 2;
/*     */   private final int epfd;
/*     */   private boolean closed;
/*     */   private final int[] sp;
/*  58 */   private final AtomicInteger wakeupCount = new AtomicInteger();
/*     */   private final long address;
/*     */   private final ArrayBlockingQueue<Event> queue;
/*  80 */   private final Event NEED_TO_POLL = new Event(null, 0);
/*  81 */   private final Event EXECUTE_TASK_OR_SHUTDOWN = new Event(null, 0);
/*     */ 
/*     */   EPollPort(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool)
/*     */     throws IOException
/*     */   {
/*  86 */     super(paramAsynchronousChannelProvider, paramThreadPool);
/*     */ 
/*  89 */     this.epfd = EPoll.epollCreate();
/*     */ 
/*  92 */     int[] arrayOfInt = new int[2];
/*     */     try {
/*  94 */       socketpair(arrayOfInt);
/*     */ 
/*  96 */       EPoll.epollCtl(this.epfd, 1, arrayOfInt[0], 1);
/*     */     } catch (IOException localIOException) {
/*  98 */       close0(this.epfd);
/*  99 */       throw localIOException;
/*     */     }
/* 101 */     this.sp = arrayOfInt;
/*     */ 
/* 104 */     this.address = EPoll.allocatePollArray(512);
/*     */ 
/* 108 */     this.queue = new ArrayBlockingQueue(512);
/* 109 */     this.queue.offer(this.NEED_TO_POLL);
/*     */   }
/*     */ 
/*     */   EPollPort start() {
/* 113 */     startThreads(new EventHandlerTask(null));
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   private void implClose()
/*     */   {
/* 121 */     synchronized (this) {
/* 122 */       if (this.closed)
/* 123 */         return;
/* 124 */       this.closed = true;
/*     */     }
/* 126 */     EPoll.freePollArray(this.address);
/* 127 */     close0(this.sp[0]);
/* 128 */     close0(this.sp[1]);
/* 129 */     close0(this.epfd);
/*     */   }
/*     */ 
/*     */   private void wakeup() {
/* 133 */     if (this.wakeupCount.incrementAndGet() == 1)
/*     */       try
/*     */       {
/* 136 */         interrupt(this.sp[1]);
/*     */       } catch (IOException localIOException) {
/* 138 */         throw new AssertionError(localIOException);
/*     */       }
/*     */   }
/*     */ 
/*     */   void executeOnHandlerTask(Runnable paramRunnable)
/*     */   {
/* 145 */     synchronized (this) {
/* 146 */       if (this.closed)
/* 147 */         throw new RejectedExecutionException();
/* 148 */       offerTask(paramRunnable);
/* 149 */       wakeup();
/*     */     }
/*     */   }
/*     */ 
/*     */   void shutdownHandlerTasks()
/*     */   {
/* 159 */     int i = threadCount();
/* 160 */     if (i == 0) {
/* 161 */       implClose();
/*     */     }
/*     */     else
/* 164 */       while (i-- > 0)
/* 165 */         wakeup();
/*     */   }
/*     */ 
/*     */   void startPoll(int paramInt1, int paramInt2)
/*     */   {
/* 174 */     int i = EPoll.epollCtl(this.epfd, 3, paramInt1, paramInt2 | 0x40000000);
/* 175 */     if (i == 2)
/* 176 */       i = EPoll.epollCtl(this.epfd, 1, paramInt1, paramInt2 | 0x40000000);
/* 177 */     if (i != 0)
/* 178 */       throw new AssertionError();
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
/* 321 */     Util.load();
/*     */   }
/*     */ 
/*     */   static class Event
/*     */   {
/*     */     final Port.PollableChannel channel;
/*     */     final int events;
/*     */ 
/*     */     Event(Port.PollableChannel paramPollableChannel, int paramInt)
/*     */     {
/*  69 */       this.channel = paramPollableChannel;
/*  70 */       this.events = paramInt;
/*     */     }
/*     */     Port.PollableChannel channel() {
/*  73 */       return this.channel; } 
/*  74 */     int events() { return this.events; }
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
/*     */     private EPollPort.Event poll()
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/* 194 */           int i = EPoll.epollWait(EPollPort.this.epfd, EPollPort.this.address, 512);
/*     */ 
/* 201 */           EPollPort.this.fdToChannelLock.readLock().lock();
/*     */           try {
/* 203 */             while (i-- > 0) {
/* 204 */               long l = EPoll.getEvent(EPollPort.this.address, i);
/* 205 */               int j = EPoll.getDescriptor(l);
/*     */               Object localObject1;
/* 208 */               if (j == EPollPort.this.sp[0]) {
/* 209 */                 if (EPollPort.this.wakeupCount.decrementAndGet() == 0)
/*     */                 {
/* 211 */                   EPollPort.drain1(EPollPort.this.sp[0]);
/*     */                 }
/*     */ 
/* 216 */                 if (i > 0) {
/* 217 */                   EPollPort.this.queue.offer(EPollPort.this.EXECUTE_TASK_OR_SHUTDOWN);
/*     */                 }
/*     */                 else {
/* 220 */                   localObject1 = EPollPort.this.EXECUTE_TASK_OR_SHUTDOWN;
/*     */ 
/* 238 */                   EPollPort.this.fdToChannelLock.readLock().unlock();
/*     */ 
/* 244 */                   return localObject1;
/*     */                 }
/*     */               }
/*     */               else
/*     */               {
/* 223 */                 localObject1 = (Port.PollableChannel)EPollPort.this.fdToChannel.get(Integer.valueOf(j));
/* 224 */                 if (localObject1 != null) {
/* 225 */                   int k = EPoll.getEvents(l);
/* 226 */                   EPollPort.Event localEvent1 = new EPollPort.Event((Port.PollableChannel)localObject1, k);
/*     */ 
/* 230 */                   if (i > 0) {
/* 231 */                     EPollPort.this.queue.offer(localEvent1);
/*     */                   } else {
/* 233 */                     EPollPort.Event localEvent2 = localEvent1;
/*     */ 
/* 238 */                     EPollPort.this.fdToChannelLock.readLock().unlock();
/*     */ 
/* 244 */                     return localEvent2;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 238 */             EPollPort.this.fdToChannelLock.readLock().unlock();
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 244 */         EPollPort.this.queue.offer(EPollPort.this.NEED_TO_POLL);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run() {
/* 249 */       Invoker.GroupAndInvokeCount localGroupAndInvokeCount = Invoker.getGroupAndInvokeCount();
/*     */ 
/* 251 */       boolean bool1 = localGroupAndInvokeCount != null;
/* 252 */       boolean bool2 = false;
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/* 257 */           if (bool1)
/* 258 */             localGroupAndInvokeCount.resetInvokeCount(); EPollPort.Event localEvent;
/*     */           int i;
/*     */           try { bool2 = false;
/* 262 */             localEvent = (EPollPort.Event)EPollPort.this.queue.take();
/*     */ 
/* 266 */             if (localEvent == EPollPort.this.NEED_TO_POLL)
/*     */               try {
/* 268 */                 localEvent = poll();
/*     */               } catch (IOException localIOException) {
/* 270 */                 localIOException.printStackTrace();
/*     */ 
/* 302 */                 i = EPollPort.this.threadExit(this, bool2);
/* 303 */                 if ((i == 0) && (EPollPort.this.isShutdown()))
/* 304 */                   EPollPort.this.implClose();
/*     */                 return;
/*     */               }
/*     */           }
/*     */           catch (InterruptedException localInterruptedException)
/*     */           {
/*     */           }
/* 275 */           continue;
/*     */ 
/* 279 */           if (localEvent == EPollPort.this.EXECUTE_TASK_OR_SHUTDOWN) {
/* 280 */             Runnable localRunnable = EPollPort.this.pollTask();
/* 281 */             if (localRunnable == null)
/*     */             {
/*     */               return;
/*     */             }
/*     */ 
/* 286 */             bool2 = true;
/* 287 */             localRunnable.run();
/* 288 */             continue;
/*     */           }
/*     */ 
/*     */           try
/*     */           {
/* 293 */             localEvent.channel().onEvent(localEvent.events(), bool1);
/*     */           } catch (Error localError) {
/* 295 */             bool2 = true; throw localError;
/*     */           } catch (RuntimeException localRuntimeException) {
/* 297 */             bool2 = true; throw localRuntimeException;
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 302 */         int j = EPollPort.this.threadExit(this, bool2);
/* 303 */         if ((j == 0) && (EPollPort.this.isShutdown()))
/* 304 */           EPollPort.this.implClose();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.EPollPort
 * JD-Core Version:    0.6.2
 */