/*     */ package java.lang.ref;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ final class Finalizer extends FinalReference
/*     */ {
/*  41 */   private static ReferenceQueue queue = new ReferenceQueue();
/*  42 */   private static Finalizer unfinalized = null;
/*  43 */   private static final Object lock = new Object();
/*     */ 
/*  45 */   private Finalizer next = null; private Finalizer prev = null;
/*     */ 
/*     */   static native void invokeFinalizeMethod(Object paramObject) throws Throwable;
/*     */ 
/*     */   private boolean hasBeenFinalized() {
/*  50 */     return this.next == this;
/*     */   }
/*     */ 
/*     */   private void add() {
/*  54 */     synchronized (lock) {
/*  55 */       if (unfinalized != null) {
/*  56 */         this.next = unfinalized;
/*  57 */         unfinalized.prev = this;
/*     */       }
/*  59 */       unfinalized = this;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void remove() {
/*  64 */     synchronized (lock) {
/*  65 */       if (unfinalized == this) {
/*  66 */         if (this.next != null)
/*  67 */           unfinalized = this.next;
/*     */         else {
/*  69 */           unfinalized = this.prev;
/*     */         }
/*     */       }
/*  72 */       if (this.next != null) {
/*  73 */         this.next.prev = this.prev;
/*     */       }
/*  75 */       if (this.prev != null) {
/*  76 */         this.prev.next = this.next;
/*     */       }
/*  78 */       this.next = this;
/*  79 */       this.prev = this;
/*     */     }
/*     */   }
/*     */ 
/*     */   private Finalizer(Object paramObject) {
/*  84 */     super(paramObject, queue);
/*  85 */     add();
/*     */   }
/*     */ 
/*     */   static void register(Object paramObject)
/*     */   {
/*  90 */     new Finalizer(paramObject);
/*     */   }
/*     */ 
/*     */   private void runFinalizer() {
/*  94 */     synchronized (this) {
/*  95 */       if (hasBeenFinalized()) return;
/*  96 */       remove();
/*     */     }
/*     */     try {
/*  99 */       ??? = get();
/* 100 */       if ((??? != null) && (!(??? instanceof Enum))) {
/* 101 */         invokeFinalizeMethod(???);
/*     */ 
/* 104 */         ??? = null;
/*     */       }
/*     */     } catch (Throwable localThrowable) {  }
/*     */ 
/* 107 */     super.clear();
/*     */   }
/*     */ 
/*     */   private static void forkSecondaryFinalizer(Runnable paramRunnable)
/*     */   {
/* 124 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 127 */         Object localObject1 = Thread.currentThread().getThreadGroup();
/* 128 */         for (Object localObject2 = localObject1; 
/* 129 */           localObject2 != null; 
/* 130 */           localObject2 = ((ThreadGroup)localObject1).getParent()) localObject1 = localObject2;
/* 131 */         localObject2 = new Thread((ThreadGroup)localObject1, this.val$proc, "Secondary finalizer");
/* 132 */         ((Thread)localObject2).start();
/*     */         try {
/* 134 */           ((Thread)localObject2).join();
/*     */         }
/*     */         catch (InterruptedException localInterruptedException) {
/*     */         }
/* 138 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static void runFinalization() {
/* 144 */     forkSecondaryFinalizer(new Runnable() {
/*     */       private volatile boolean running;
/*     */ 
/* 147 */       public void run() { if (this.running)
/* 148 */           return;
/* 149 */         this.running = true;
/*     */         while (true) {
/* 151 */           Finalizer localFinalizer = (Finalizer)Finalizer.queue.poll();
/* 152 */           if (localFinalizer == null) break;
/* 153 */           localFinalizer.runFinalizer();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static void runAllFinalizers()
/*     */   {
/* 161 */     forkSecondaryFinalizer(new Runnable() {
/*     */       private volatile boolean running;
/*     */ 
/* 164 */       public void run() { if (this.running)
/* 165 */           return;
/* 166 */         this.running = true;
/*     */         while (true)
/*     */         {
/*     */           Finalizer localFinalizer;
/* 169 */           synchronized (Finalizer.lock) {
/* 170 */             localFinalizer = Finalizer.unfinalized;
/* 171 */             if (localFinalizer == null) break;
/* 172 */             Finalizer.access$302(localFinalizer.next);
/*     */           }
/* 174 */           localFinalizer.runFinalizer();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 199 */     Object localObject1 = Thread.currentThread().getThreadGroup();
/* 200 */     for (Object localObject2 = localObject1; 
/* 201 */       localObject2 != null; 
/* 202 */       localObject2 = ((ThreadGroup)localObject1).getParent()) localObject1 = localObject2;
/* 203 */     localObject2 = new FinalizerThread((ThreadGroup)localObject1);
/* 204 */     ((Thread)localObject2).setPriority(8);
/* 205 */     ((Thread)localObject2).setDaemon(true);
/* 206 */     ((Thread)localObject2).start();
/*     */   }
/*     */ 
/*     */   private static class FinalizerThread extends Thread
/*     */   {
/*     */     private volatile boolean running;
/*     */ 
/*     */     FinalizerThread(ThreadGroup paramThreadGroup)
/*     */     {
/* 181 */       super("Finalizer");
/*     */     }
/*     */     public void run() {
/* 184 */       if (this.running)
/* 185 */         return;
/* 186 */       this.running = true;
/*     */       while (true)
/*     */         try {
/* 189 */           Finalizer localFinalizer = (Finalizer)Finalizer.queue.remove();
/* 190 */           localFinalizer.runFinalizer();
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ref.Finalizer
 * JD-Core Version:    0.6.2
 */