/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public final class AWTAutoShutdown
/*     */   implements Runnable
/*     */ {
/*  63 */   private static final AWTAutoShutdown theInstance = new AWTAutoShutdown();
/*     */ 
/*  68 */   private final Object mainLock = new Object();
/*     */ 
/*  76 */   private final Object activationLock = new Object();
/*     */ 
/*  84 */   private final HashSet busyThreadSet = new HashSet(7);
/*     */ 
/*  90 */   private boolean toolkitThreadBusy = false;
/*     */ 
/*  96 */   private final Map peerMap = new IdentityHashMap();
/*     */ 
/* 102 */   private Thread blockerThread = null;
/*     */ 
/* 108 */   private boolean timeoutPassed = false;
/*     */   private static final int SAFETY_TIMEOUT = 1000;
/*     */ 
/*     */   public static AWTAutoShutdown getInstance()
/*     */   {
/* 128 */     return theInstance;
/*     */   }
/*     */ 
/*     */   public static void notifyToolkitThreadBusy()
/*     */   {
/* 140 */     getInstance().setToolkitBusy(true);
/*     */   }
/*     */ 
/*     */   public static void notifyToolkitThreadFree()
/*     */   {
/* 152 */     getInstance().setToolkitBusy(false);
/*     */   }
/*     */ 
/*     */   public void notifyThreadBusy(Thread paramThread)
/*     */   {
/* 165 */     if (paramThread == null) {
/* 166 */       return;
/*     */     }
/* 168 */     synchronized (this.activationLock) {
/* 169 */       synchronized (this.mainLock) {
/* 170 */         if (this.blockerThread == null) {
/* 171 */           activateBlockerThread();
/* 172 */         } else if (isReadyToShutdown()) {
/* 173 */           this.mainLock.notifyAll();
/* 174 */           this.timeoutPassed = false;
/*     */         }
/* 176 */         this.busyThreadSet.add(paramThread);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void notifyThreadFree(Thread paramThread)
/*     */   {
/* 191 */     if (paramThread == null) {
/* 192 */       return;
/*     */     }
/* 194 */     synchronized (this.activationLock) {
/* 195 */       synchronized (this.mainLock) {
/* 196 */         this.busyThreadSet.remove(paramThread);
/* 197 */         if (isReadyToShutdown()) {
/* 198 */           this.mainLock.notifyAll();
/* 199 */           this.timeoutPassed = false;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void notifyPeerMapUpdated()
/*     */   {
/* 212 */     synchronized (this.activationLock) {
/* 213 */       synchronized (this.mainLock) {
/* 214 */         if ((!isReadyToShutdown()) && (this.blockerThread == null)) {
/* 215 */           activateBlockerThread();
/*     */         } else {
/* 217 */           this.mainLock.notifyAll();
/* 218 */           this.timeoutPassed = false;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isReadyToShutdown()
/*     */   {
/* 233 */     return (!this.toolkitThreadBusy) && (this.peerMap.isEmpty()) && (this.busyThreadSet.isEmpty());
/*     */   }
/*     */ 
/*     */   private void setToolkitBusy(boolean paramBoolean)
/*     */   {
/* 248 */     if (paramBoolean != this.toolkitThreadBusy)
/* 249 */       synchronized (this.activationLock) {
/* 250 */         synchronized (this.mainLock) {
/* 251 */           if (paramBoolean != this.toolkitThreadBusy)
/* 252 */             if (paramBoolean) {
/* 253 */               if (this.blockerThread == null) {
/* 254 */                 activateBlockerThread();
/* 255 */               } else if (isReadyToShutdown()) {
/* 256 */                 this.mainLock.notifyAll();
/* 257 */                 this.timeoutPassed = false;
/*     */               }
/* 259 */               this.toolkitThreadBusy = paramBoolean;
/*     */             } else {
/* 261 */               this.toolkitThreadBusy = paramBoolean;
/* 262 */               if (isReadyToShutdown()) {
/* 263 */                 this.mainLock.notifyAll();
/* 264 */                 this.timeoutPassed = false;
/*     */               }
/*     */             }
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 280 */     Thread localThread = Thread.currentThread();
/* 281 */     int i = 0;
/* 282 */     synchronized (this.mainLock)
/*     */     {
/*     */       try {
/* 285 */         this.mainLock.notifyAll();
/* 286 */         label83: while (this.blockerThread == localThread) {
/* 287 */           this.mainLock.wait();
/* 288 */           this.timeoutPassed = false;
/*     */           while (true)
/*     */           {
/* 299 */             if (!isReadyToShutdown()) break label83;
/* 300 */             if (this.timeoutPassed) {
/* 301 */               this.timeoutPassed = false;
/* 302 */               this.blockerThread = null;
/* 303 */               break;
/*     */             }
/* 305 */             this.timeoutPassed = true;
/* 306 */             this.mainLock.wait(1000L);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 312 */         if (this.blockerThread == localThread)
/* 313 */           this.blockerThread = null;
/*     */       }
/*     */       catch (InterruptedException localInterruptedException)
/*     */       {
/* 310 */         i = 1;
/*     */ 
/* 312 */         if (this.blockerThread == localThread)
/* 313 */           this.blockerThread = null;
/*     */       }
/*     */       finally
/*     */       {
/* 312 */         if (this.blockerThread == localThread) {
/* 313 */           this.blockerThread = null;
/*     */         }
/*     */       }
/*     */     }
/* 317 */     if (i == 0)
/* 318 */       AppContext.stopEventDispatchThreads();
/*     */   }
/*     */ 
/*     */   static AWTEvent getShutdownEvent()
/*     */   {
/* 323 */     return new AWTEvent(getInstance(), 0)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   private void activateBlockerThread()
/*     */   {
/* 331 */     Thread localThread = new Thread(this, "AWT-Shutdown");
/* 332 */     localThread.setDaemon(false);
/* 333 */     this.blockerThread = localThread;
/* 334 */     localThread.start();
/*     */     try
/*     */     {
/* 337 */       this.mainLock.wait();
/*     */     } catch (InterruptedException localInterruptedException) {
/* 339 */       System.err.println("AWT blocker activation interrupted:");
/* 340 */       localInterruptedException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   final void registerPeer(Object paramObject1, Object paramObject2) {
/* 345 */     synchronized (this.activationLock) {
/* 346 */       synchronized (this.mainLock) {
/* 347 */         this.peerMap.put(paramObject1, paramObject2);
/* 348 */         notifyPeerMapUpdated();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   final void unregisterPeer(Object paramObject1, Object paramObject2) {
/* 354 */     synchronized (this.activationLock) {
/* 355 */       synchronized (this.mainLock) {
/* 356 */         if (this.peerMap.get(paramObject1) == paramObject2) {
/* 357 */           this.peerMap.remove(paramObject1);
/* 358 */           notifyPeerMapUpdated();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   final Object getPeer(Object paramObject) {
/* 365 */     synchronized (this.activationLock) {
/* 366 */       synchronized (this.mainLock) {
/*     */       }
/* 368 */       throw localObject1;
/*     */     }
/*     */   }
/*     */ 
/*     */   final void dumpPeers(PlatformLogger paramPlatformLogger) {
/* 373 */     synchronized (this.activationLock)
/*     */     {
/*     */       Iterator localIterator;
/* 374 */       synchronized (this.mainLock) {
/* 375 */         paramPlatformLogger.fine("Mapped peers:");
/* 376 */         for (localIterator = this.peerMap.keySet().iterator(); localIterator.hasNext(); ) { Object localObject1 = localIterator.next();
/* 377 */           paramPlatformLogger.fine(localObject1 + "->" + this.peerMap.get(localObject1));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.AWTAutoShutdown
 * JD-Core Version:    0.6.2
 */