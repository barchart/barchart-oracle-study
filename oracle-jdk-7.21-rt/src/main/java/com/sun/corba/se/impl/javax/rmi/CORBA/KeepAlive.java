/*     */ package com.sun.corba.se.impl.javax.rmi.CORBA;
/*     */ 
/*     */ class KeepAlive extends Thread
/*     */ {
/* 758 */   boolean quit = false;
/*     */ 
/*     */   public KeepAlive()
/*     */   {
/* 762 */     setDaemon(false);
/*     */   }
/*     */ 
/*     */   public synchronized void run()
/*     */   {
/* 767 */     while (!this.quit)
/*     */       try {
/* 769 */         wait();
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {
/*     */       }
/*     */   }
/*     */ 
/*     */   public synchronized void quit() {
/* 776 */     this.quit = true;
/* 777 */     notifyAll();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.javax.rmi.CORBA.KeepAlive
 * JD-Core Version:    0.6.2
 */