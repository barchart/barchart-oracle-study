/*     */ package sun.awt.X11;
/*     */ 
/*     */ class XScrollRepeater
/*     */   implements Runnable
/*     */ {
/* 863 */   static int beginPause = 500;
/*     */ 
/* 869 */   static int repeatPause = 100;
/*     */   XScrollbar sb;
/*     */   boolean newScroll;
/*     */   boolean shouldSkip;
/*     */ 
/*     */   XScrollRepeater(XScrollbar paramXScrollbar)
/*     */   {
/* 890 */     setScrollbar(paramXScrollbar);
/* 891 */     this.newScroll = true;
/*     */   }
/*     */ 
/*     */   public void start() {
/* 895 */     stop();
/* 896 */     this.shouldSkip = false;
/* 897 */     XToolkit.schedule(this, beginPause);
/*     */   }
/*     */ 
/*     */   public void stop() {
/* 901 */     synchronized (this) {
/* 902 */       this.shouldSkip = true;
/*     */     }
/* 904 */     XToolkit.remove(this);
/*     */   }
/*     */ 
/*     */   public synchronized void setScrollbar(XScrollbar paramXScrollbar)
/*     */   {
/* 912 */     this.sb = paramXScrollbar;
/* 913 */     stop();
/* 914 */     this.newScroll = true;
/*     */   }
/*     */ 
/*     */   public void run() {
/* 918 */     synchronized (this) {
/* 919 */       if (this.shouldSkip) {
/* 920 */         return;
/*     */       }
/*     */     }
/* 923 */     this.sb.scroll();
/* 924 */     XToolkit.schedule(this, repeatPause);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XScrollRepeater
 * JD-Core Version:    0.6.2
 */