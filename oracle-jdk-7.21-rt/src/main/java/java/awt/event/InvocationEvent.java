/*     */ package java.awt.event;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.ActiveEvent;
/*     */ 
/*     */ public class InvocationEvent extends AWTEvent
/*     */   implements ActiveEvent
/*     */ {
/*     */   public static final int INVOCATION_FIRST = 1200;
/*     */   public static final int INVOCATION_DEFAULT = 1200;
/*     */   public static final int INVOCATION_LAST = 1200;
/*     */   protected Runnable runnable;
/*     */   protected Object notifier;
/*  94 */   private volatile boolean dispatched = false;
/*     */   protected boolean catchExceptions;
/* 108 */   private Exception exception = null;
/*     */ 
/* 115 */   private Throwable throwable = null;
/*     */   private long when;
/*     */   private static final long serialVersionUID = 436056344909459450L;
/*     */ 
/*     */   public InvocationEvent(Object paramObject, Runnable paramRunnable)
/*     */   {
/* 150 */     this(paramObject, paramRunnable, null, false);
/*     */   }
/*     */ 
/*     */   public InvocationEvent(Object paramObject1, Runnable paramRunnable, Object paramObject2, boolean paramBoolean)
/*     */   {
/* 188 */     this(paramObject1, 1200, paramRunnable, paramObject2, paramBoolean);
/*     */   }
/*     */ 
/*     */   protected InvocationEvent(Object paramObject1, int paramInt, Runnable paramRunnable, Object paramObject2, boolean paramBoolean)
/*     */   {
/* 224 */     super(paramObject1, paramInt);
/* 225 */     this.runnable = paramRunnable;
/* 226 */     this.notifier = paramObject2;
/* 227 */     this.catchExceptions = paramBoolean;
/* 228 */     this.when = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public void dispatch()
/*     */   {
/*     */     try
/*     */     {
/* 239 */       if (this.catchExceptions) {
/*     */         try {
/* 241 */           this.runnable.run();
/*     */         }
/*     */         catch (Throwable localThrowable) {
/* 244 */           if ((localThrowable instanceof Exception)) {
/* 245 */             this.exception = ((Exception)localThrowable);
/*     */           }
/* 247 */           this.throwable = localThrowable;
/*     */         }
/*     */       }
/*     */       else
/* 251 */         this.runnable.run();
/*     */     }
/*     */     finally {
/* 254 */       this.dispatched = true;
/*     */ 
/* 256 */       if (this.notifier != null)
/* 257 */         synchronized (this.notifier) {
/* 258 */           this.notifier.notifyAll();
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Exception getException()
/*     */   {
/* 273 */     return this.catchExceptions ? this.exception : null;
/*     */   }
/*     */ 
/*     */   public Throwable getThrowable()
/*     */   {
/* 286 */     return this.catchExceptions ? this.throwable : null;
/*     */   }
/*     */ 
/*     */   public long getWhen()
/*     */   {
/* 296 */     return this.when;
/*     */   }
/*     */ 
/*     */   public boolean isDispatched()
/*     */   {
/* 330 */     return this.dispatched;
/*     */   }
/*     */ 
/*     */   public String paramString()
/*     */   {
/*     */     String str;
/* 341 */     switch (this.id) {
/*     */     case 1200:
/* 343 */       str = "INVOCATION_DEFAULT";
/* 344 */       break;
/*     */     default:
/* 346 */       str = "unknown type";
/*     */     }
/* 348 */     return str + ",runnable=" + this.runnable + ",notifier=" + this.notifier + ",catchExceptions=" + this.catchExceptions + ",when=" + this.when;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.InvocationEvent
 * JD-Core Version:    0.6.2
 */