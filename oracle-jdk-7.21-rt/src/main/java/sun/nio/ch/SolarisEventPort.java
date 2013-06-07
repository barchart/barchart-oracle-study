/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.spi.AsynchronousChannelProvider;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ class SolarisEventPort extends Port
/*     */ {
/*  41 */   private static final Unsafe unsafe = Unsafe.getUnsafe();
/*  42 */   private static final int addressSize = unsafe.addressSize();
/*     */ 
/*  57 */   private static final int SIZEOF_PORT_EVENT = dependsArch(16, 24);
/*     */   private static final int OFFSETOF_EVENTS = 0;
/*     */   private static final int OFFSETOF_SOURCE = 4;
/*     */   private static final int OFFSETOF_OBJECT = 8;
/*     */   private static final short PORT_SOURCE_USER = 3;
/*     */   private static final short PORT_SOURCE_FD = 4;
/*     */   private final int port;
/*     */   private boolean closed;
/*     */ 
/*     */   private static int dependsArch(int paramInt1, int paramInt2)
/*     */   {
/*  45 */     return addressSize == 4 ? paramInt1 : paramInt2;
/*     */   }
/*     */ 
/*     */   SolarisEventPort(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool)
/*     */     throws IOException
/*     */   {
/*  75 */     super(paramAsynchronousChannelProvider, paramThreadPool);
/*     */ 
/*  78 */     this.port = portCreate();
/*     */   }
/*     */ 
/*     */   SolarisEventPort start() {
/*  82 */     startThreads(new EventHandlerTask(null));
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */   private void implClose()
/*     */   {
/*  88 */     synchronized (this) {
/*  89 */       if (this.closed)
/*  90 */         return;
/*  91 */       this.closed = true;
/*     */     }
/*  93 */     portClose(this.port);
/*     */   }
/*     */ 
/*     */   private void wakeup() {
/*     */     try {
/*  98 */       portSend(this.port, 0);
/*     */     } catch (IOException localIOException) {
/* 100 */       throw new AssertionError(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   void executeOnHandlerTask(Runnable paramRunnable)
/*     */   {
/* 106 */     synchronized (this) {
/* 107 */       if (this.closed)
/* 108 */         throw new RejectedExecutionException();
/* 109 */       offerTask(paramRunnable);
/* 110 */       wakeup();
/*     */     }
/*     */   }
/*     */ 
/*     */   void shutdownHandlerTasks()
/*     */   {
/* 120 */     int i = threadCount();
/* 121 */     if (i == 0) {
/* 122 */       implClose();
/*     */     }
/*     */     else
/* 125 */       while (i-- > 0)
/*     */         try {
/* 127 */           portSend(this.port, 0);
/*     */         } catch (IOException localIOException) {
/* 129 */           throw new AssertionError(localIOException);
/*     */         }
/*     */   }
/*     */ 
/*     */   void startPoll(int paramInt1, int paramInt2)
/*     */   {
/*     */     try
/*     */     {
/* 140 */       portAssociate(this.port, 4, paramInt1, paramInt2);
/*     */     } catch (IOException localIOException) {
/* 142 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void init();
/*     */ 
/*     */   private static native int portCreate()
/*     */     throws IOException;
/*     */ 
/*     */   private static native void portAssociate(int paramInt1, int paramInt2, long paramLong, int paramInt3)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void portGet(int paramInt, long paramLong)
/*     */     throws IOException;
/*     */ 
/*     */   private static native int portGetn(int paramInt1, long paramLong, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void portSend(int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void portClose(int paramInt);
/*     */ 
/*     */   static
/*     */   {
/* 242 */     Util.load();
/* 243 */     init();
/*     */   }
/*     */ 
/*     */   private class EventHandlerTask
/*     */     implements Runnable
/*     */   {
/*     */     private EventHandlerTask()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 152 */       Invoker.GroupAndInvokeCount localGroupAndInvokeCount = Invoker.getGroupAndInvokeCount();
/*     */ 
/* 154 */       boolean bool1 = localGroupAndInvokeCount != null;
/* 155 */       boolean bool2 = false;
/* 156 */       long l = SolarisEventPort.unsafe.allocateMemory(SolarisEventPort.SIZEOF_PORT_EVENT);
/*     */       try
/*     */       {
/*     */         while (true) {
/* 160 */           if (bool1) {
/* 161 */             localGroupAndInvokeCount.resetInvokeCount();
/*     */           }
/*     */ 
/* 165 */           bool2 = false;
/*     */           try {
/* 167 */             SolarisEventPort.portGet(SolarisEventPort.this.port, l);
/*     */           } catch (IOException localIOException) {
/* 169 */             localIOException.printStackTrace();
/*     */ 
/* 214 */             SolarisEventPort.unsafe.freeMemory(l);
/*     */ 
/* 216 */             int j = SolarisEventPort.this.threadExit(this, bool2);
/* 217 */             if ((j == 0) && (SolarisEventPort.this.isShutdown()))
/* 218 */               SolarisEventPort.this.implClose();
/*     */             return;
/*     */           }
/* 174 */           int i = SolarisEventPort.unsafe.getShort(l + 4L);
/*     */           int m;
/* 175 */           if (i != 4)
/*     */           {
/* 177 */             if (i == 3) {
/* 178 */               Runnable localRunnable = SolarisEventPort.this.pollTask();
/* 179 */               if (localRunnable == null)
/*     */               {
/*     */                 return;
/*     */               }
/*     */ 
/* 184 */               bool2 = true;
/* 185 */               localRunnable.run();
/*     */             }
/*     */ 
/*     */           }
/*     */           else {
/* 192 */             int k = (int)SolarisEventPort.unsafe.getAddress(l + 8L);
/*     */ 
/* 194 */             m = SolarisEventPort.unsafe.getInt(l + 0L);
/*     */ 
/* 198 */             SolarisEventPort.this.fdToChannelLock.readLock().lock();
/*     */             Port.PollableChannel localPollableChannel;
/*     */             try {
/* 200 */               localPollableChannel = (Port.PollableChannel)SolarisEventPort.this.fdToChannel.get(Integer.valueOf(k));
/*     */             } finally {
/* 202 */               SolarisEventPort.this.fdToChannelLock.readLock().unlock();
/*     */             }
/*     */ 
/* 206 */             if (localPollableChannel != null) {
/* 207 */               bool2 = true;
/*     */ 
/* 209 */               localPollableChannel.onEvent(m, bool1);
/*     */             }
/*     */           }
/*     */         }
/*     */       } finally {
/* 214 */         SolarisEventPort.unsafe.freeMemory(l);
/*     */ 
/* 216 */         int n = SolarisEventPort.this.threadExit(this, bool2);
/* 217 */         if ((n == 0) && (SolarisEventPort.this.isShutdown()))
/* 218 */           SolarisEventPort.this.implClose();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SolarisEventPort
 * JD-Core Version:    0.6.2
 */