/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Set;
/*     */ import sun.java2d.pipe.RenderBuffer;
/*     */ import sun.java2d.pipe.RenderQueue;
/*     */ 
/*     */ public class OGLRenderQueue extends RenderQueue
/*     */ {
/*     */   private static OGLRenderQueue theInstance;
/*     */   private final QueueFlusher flusher;
/*     */ 
/*     */   private OGLRenderQueue()
/*     */   {
/*  50 */     this.flusher = ((QueueFlusher)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public OGLRenderQueue.QueueFlusher run() {
/*  52 */         ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/*  53 */         while (localThreadGroup.getParent() != null) {
/*  54 */           localThreadGroup = localThreadGroup.getParent();
/*     */         }
/*  56 */         return new OGLRenderQueue.QueueFlusher(OGLRenderQueue.this, localThreadGroup);
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   public static synchronized OGLRenderQueue getInstance()
/*     */   {
/*  67 */     if (theInstance == null) {
/*  68 */       theInstance = new OGLRenderQueue();
/*     */     }
/*  70 */     return theInstance;
/*     */   }
/*     */ 
/*     */   public static void sync()
/*     */   {
/*  83 */     if (theInstance != null) {
/*  84 */       theInstance.lock();
/*     */       try {
/*  86 */         theInstance.ensureCapacity(4);
/*  87 */         theInstance.getBuffer().putInt(76);
/*  88 */         theInstance.flushNow();
/*     */       } finally {
/*  90 */         theInstance.unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void disposeGraphicsConfig(long paramLong)
/*     */   {
/* 100 */     OGLRenderQueue localOGLRenderQueue = getInstance();
/* 101 */     localOGLRenderQueue.lock();
/*     */     try
/*     */     {
/* 105 */       OGLContext.setScratchSurface(paramLong);
/*     */ 
/* 107 */       RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
/* 108 */       localOGLRenderQueue.ensureCapacityAndAlignment(12, 4);
/* 109 */       localRenderBuffer.putInt(74);
/* 110 */       localRenderBuffer.putLong(paramLong);
/*     */ 
/* 113 */       localOGLRenderQueue.flushNow();
/*     */     } finally {
/* 115 */       localOGLRenderQueue.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isQueueFlusherThread()
/*     */   {
/* 123 */     return Thread.currentThread() == getInstance().flusher;
/*     */   }
/*     */ 
/*     */   public void flushNow()
/*     */   {
/*     */     try {
/* 129 */       this.flusher.flushNow();
/*     */     } catch (Exception localException) {
/* 131 */       System.err.println("exception in flushNow:");
/* 132 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flushAndInvokeNow(Runnable paramRunnable)
/*     */   {
/*     */     try {
/* 139 */       this.flusher.flushAndInvokeNow(paramRunnable);
/*     */     } catch (Exception localException) {
/* 141 */       System.err.println("exception in flushAndInvokeNow:");
/* 142 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private native void flushBuffer(long paramLong, int paramInt);
/*     */ 
/*     */   private void flushBuffer()
/*     */   {
/* 150 */     int i = this.buf.position();
/* 151 */     if (i > 0)
/*     */     {
/* 153 */       flushBuffer(this.buf.getAddress(), i);
/*     */     }
/*     */ 
/* 156 */     this.buf.clear();
/*     */ 
/* 158 */     this.refSet.clear();
/*     */   }
/*     */   private class QueueFlusher extends Thread {
/*     */     private boolean needsFlush;
/*     */     private Runnable task;
/*     */     private Error error;
/*     */ 
/* 167 */     public QueueFlusher(ThreadGroup arg2) { super("Java2D Queue Flusher");
/* 168 */       setDaemon(true);
/* 169 */       setPriority(10);
/* 170 */       start();
/*     */     }
/*     */ 
/*     */     public synchronized void flushNow()
/*     */     {
/* 175 */       this.needsFlush = true;
/* 176 */       notify();
/*     */ 
/* 179 */       while (this.needsFlush) {
/*     */         try {
/* 181 */           wait();
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */       }
/* 187 */       if (this.error != null)
/* 188 */         throw this.error;
/*     */     }
/*     */ 
/*     */     public synchronized void flushAndInvokeNow(Runnable paramRunnable)
/*     */     {
/* 193 */       this.task = paramRunnable;
/* 194 */       flushNow();
/*     */     }
/*     */ 
/*     */     public synchronized void run() {
/* 198 */       boolean bool = false;
/*     */       while (true)
/* 200 */         if (!this.needsFlush)
/*     */           try {
/* 202 */             bool = false;
/*     */ 
/* 208 */             wait(100L);
/*     */ 
/* 217 */             if ((!this.needsFlush) && ((bool = OGLRenderQueue.this.tryLock())))
/* 218 */               if (OGLRenderQueue.this.buf.position() > 0)
/* 219 */                 this.needsFlush = true;
/*     */               else
/* 221 */                 OGLRenderQueue.this.unlock();
/*     */           }
/*     */           catch (InterruptedException localInterruptedException)
/*     */           {
/*     */           }
/*     */         else
/*     */           try
/*     */           {
/* 229 */             this.error = null;
/*     */ 
/* 231 */             OGLRenderQueue.this.flushBuffer();
/*     */ 
/* 233 */             if (this.task != null)
/* 234 */               this.task.run();
/*     */           }
/*     */           catch (Error localError) {
/* 237 */             this.error = localError;
/*     */           } catch (Exception localException) {
/* 239 */             System.err.println("exception in QueueFlusher:");
/* 240 */             localException.printStackTrace();
/*     */           } finally {
/* 242 */             if (bool) {
/* 243 */               OGLRenderQueue.this.unlock();
/*     */             }
/* 245 */             this.task = null;
/*     */ 
/* 247 */             this.needsFlush = false;
/* 248 */             notify();
/*     */           }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.opengl.OGLRenderQueue
 * JD-Core Version:    0.6.2
 */