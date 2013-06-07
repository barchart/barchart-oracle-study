/*     */ package sun.applet;
/*     */ 
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ class AppContextCreator extends Thread
/*     */ {
/* 857 */   Object syncObject = new Object();
/* 858 */   AppContext appContext = null;
/* 859 */   volatile boolean created = false;
/*     */ 
/*     */   AppContextCreator(ThreadGroup paramThreadGroup) {
/* 862 */     super(paramThreadGroup, "AppContextCreator");
/*     */   }
/*     */ 
/*     */   public void run() {
/* 866 */     this.appContext = SunToolkit.createNewAppContext();
/* 867 */     this.created = true;
/* 868 */     synchronized (this.syncObject) {
/* 869 */       this.syncObject.notifyAll();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.applet.AppContextCreator
 * JD-Core Version:    0.6.2
 */