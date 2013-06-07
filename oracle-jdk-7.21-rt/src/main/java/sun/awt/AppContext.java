/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.InvocationEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import sun.misc.JavaAWTAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public final class AppContext
/*     */ {
/* 133 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.AppContext");
/*     */ 
/* 141 */   public static final Object EVENT_QUEUE_KEY = new StringBuffer("EventQueue");
/*     */ 
/* 146 */   public static final Object EVENT_QUEUE_LOCK_KEY = new StringBuilder("EventQueue.Lock");
/* 147 */   public static final Object EVENT_QUEUE_COND_KEY = new StringBuilder("EventQueue.Condition");
/*     */ 
/* 151 */   private static final Map<ThreadGroup, AppContext> threadGroup2appContext = Collections.synchronizedMap(new IdentityHashMap());
/*     */ 
/* 166 */   private static volatile AppContext mainAppContext = null;
/*     */ 
/* 174 */   private final HashMap table = new HashMap();
/*     */   private final ThreadGroup threadGroup;
/* 186 */   private PropertyChangeSupport changeSupport = null;
/*     */   public static final String DISPOSED_PROPERTY_NAME = "disposed";
/*     */   public static final String GUI_DISPOSED = "guidisposed";
/* 191 */   private volatile boolean isDisposed = false;
/*     */   private static volatile int numAppContexts;
/*     */   private final ClassLoader contextClassLoader;
/*     */   private static final ThreadLocal<AppContext> threadAppContext;
/* 361 */   private long DISPOSAL_TIMEOUT = 5000L;
/*     */ 
/* 367 */   private long THREAD_INTERRUPT_TIMEOUT = 1000L;
/*     */ 
/* 580 */   private MostRecentKeyValue mostRecentKeyValue = null;
/* 581 */   private MostRecentKeyValue shadowMostRecentKeyValue = null;
/*     */ 
/*     */   public static Set<AppContext> getAppContexts()
/*     */   {
/* 158 */     synchronized (threadGroup2appContext) {
/* 159 */       return new HashSet(threadGroup2appContext.values());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/* 194 */     return this.isDisposed;
/*     */   }
/*     */ 
/*     */   AppContext(ThreadGroup paramThreadGroup)
/*     */   {
/* 246 */     numAppContexts += 1;
/*     */ 
/* 248 */     this.threadGroup = paramThreadGroup;
/* 249 */     threadGroup2appContext.put(paramThreadGroup, this);
/*     */ 
/* 251 */     this.contextClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ClassLoader run() {
/* 254 */         return Thread.currentThread().getContextClassLoader();
/*     */       }
/*     */     }));
/* 260 */     ReentrantLock localReentrantLock = new ReentrantLock();
/* 261 */     put(EVENT_QUEUE_LOCK_KEY, localReentrantLock);
/* 262 */     Condition localCondition = localReentrantLock.newCondition();
/* 263 */     put(EVENT_QUEUE_COND_KEY, localCondition);
/*     */   }
/*     */ 
/*     */   public static final AppContext getAppContext()
/*     */   {
/* 281 */     if (numAppContexts == 1) {
/* 282 */       return mainAppContext;
/*     */     }
/* 284 */     Object localObject = (AppContext)threadAppContext.get();
/*     */ 
/* 286 */     if (null == localObject) {
/* 287 */       localObject = (AppContext)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public AppContext run()
/*     */         {
/* 294 */           ThreadGroup localThreadGroup1 = Thread.currentThread().getThreadGroup();
/* 295 */           ThreadGroup localThreadGroup2 = localThreadGroup1;
/* 296 */           AppContext localAppContext = (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup2);
/* 297 */           while (localAppContext == null) {
/* 298 */             localThreadGroup2 = localThreadGroup2.getParent();
/* 299 */             if (localThreadGroup2 == null)
/*     */             {
/* 305 */               throw new RuntimeException("Invalid ThreadGroup");
/*     */             }
/* 307 */             localAppContext = (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup2);
/*     */           }
/*     */ 
/* 312 */           for (ThreadGroup localThreadGroup3 = localThreadGroup1; localThreadGroup3 != localThreadGroup2; localThreadGroup3 = localThreadGroup3.getParent()) {
/* 313 */             AppContext.threadGroup2appContext.put(localThreadGroup3, localAppContext);
/*     */           }
/*     */ 
/* 319 */           AppContext.threadAppContext.set(localAppContext);
/*     */ 
/* 321 */           return localAppContext;
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/* 326 */     if (localObject == mainAppContext)
/*     */     {
/* 330 */       AppContext localAppContext = getExecutionAppContext();
/* 331 */       if (localAppContext != null) {
/* 332 */         localObject = localAppContext;
/*     */       }
/*     */     }
/*     */ 
/* 336 */     return localObject;
/*     */   }
/*     */ 
/*     */   private static final AppContext getExecutionAppContext() {
/* 340 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 341 */     if ((localSecurityManager != null) && ((localSecurityManager instanceof AWTSecurityManager)))
/*     */     {
/* 344 */       AWTSecurityManager localAWTSecurityManager = (AWTSecurityManager)localSecurityManager;
/* 345 */       AppContext localAppContext = localAWTSecurityManager.getAppContext();
/* 346 */       return localAppContext;
/*     */     }
/* 348 */     return null;
/*     */   }
/*     */ 
/*     */   static final AppContext getMainAppContext()
/*     */   {
/* 358 */     return mainAppContext;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */     throws IllegalThreadStateException
/*     */   {
/* 385 */     if (this.threadGroup.parentOf(Thread.currentThread().getThreadGroup())) {
/* 386 */       throw new IllegalThreadStateException("Current Thread is contained within AppContext to be disposed.");
/*     */     }
/*     */ 
/* 391 */     synchronized (this) {
/* 392 */       if (this.isDisposed) {
/* 393 */         return;
/*     */       }
/* 395 */       this.isDisposed = true;
/*     */     }
/*     */ 
/* 398 */     ??? = this.changeSupport;
/* 399 */     if (??? != null) {
/* 400 */       ((PropertyChangeSupport)???).firePropertyChange("disposed", false, true);
/*     */     }
/*     */ 
/* 406 */     final Object localObject2 = new Object();
/*     */ 
/* 408 */     Object localObject3 = new Runnable() {
/*     */       public void run() {
/* 410 */         Window[] arrayOfWindow1 = Window.getOwnerlessWindows();
/* 411 */         for (Window localWindow : arrayOfWindow1) {
/*     */           try {
/* 413 */             localWindow.dispose();
/*     */           } catch (Throwable localThrowable) {
/* 415 */             AppContext.log.finer("exception occured while disposing app context", localThrowable);
/*     */           }
/*     */         }
/* 418 */         AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public Object run() {
/* 420 */             if ((!GraphicsEnvironment.isHeadless()) && (SystemTray.isSupported()))
/*     */             {
/* 422 */               SystemTray localSystemTray = SystemTray.getSystemTray();
/* 423 */               TrayIcon[] arrayOfTrayIcon1 = localSystemTray.getTrayIcons();
/* 424 */               for (TrayIcon localTrayIcon : arrayOfTrayIcon1) {
/* 425 */                 localSystemTray.remove(localTrayIcon);
/*     */               }
/*     */             }
/* 428 */             return null;
/*     */           }
/*     */         });
/* 432 */         if (this.val$changeSupport != null) {
/* 433 */           this.val$changeSupport.firePropertyChange("guidisposed", false, true);
/*     */         }
/* 435 */         synchronized (localObject2) {
/* 436 */           localObject2.notifyAll();
/*     */         }
/*     */       }
/*     */     };
/* 440 */     synchronized (localObject2) {
/* 441 */       SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), (Runnable)localObject3));
/*     */       try
/*     */       {
/* 444 */         localObject2.wait(this.DISPOSAL_TIMEOUT);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException1)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 452 */     localObject3 = new Runnable() {
/* 453 */       public void run() { synchronized (localObject2) {
/* 454 */           localObject2.notifyAll();
/*     */         }
/*     */       }
/*     */     };
/* 457 */     synchronized (localObject2) {
/* 458 */       SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), (Runnable)localObject3));
/*     */       try
/*     */       {
/* 461 */         localObject2.wait(this.DISPOSAL_TIMEOUT);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException2) {
/*     */       }
/*     */     }
/* 466 */     this.threadGroup.interrupt();
/*     */ 
/* 474 */     long l1 = System.currentTimeMillis();
/* 475 */     long l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
/* 476 */     while ((this.threadGroup.activeCount() > 0) && (System.currentTimeMillis() < l2))
/*     */       try
/*     */       {
/* 479 */         Thread.sleep(10L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException3)
/*     */       {
/*     */       }
/* 484 */     this.threadGroup.stop();
/*     */ 
/* 489 */     l1 = System.currentTimeMillis();
/* 490 */     l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
/* 491 */     while ((this.threadGroup.activeCount() > 0) && (System.currentTimeMillis() < l2))
/*     */       try
/*     */       {
/* 494 */         Thread.sleep(10L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException4)
/*     */       {
/*     */       }
/* 499 */     int i = this.threadGroup.activeGroupCount();
/* 500 */     if (i > 0) {
/* 501 */       ThreadGroup[] arrayOfThreadGroup = new ThreadGroup[i];
/* 502 */       i = this.threadGroup.enumerate(arrayOfThreadGroup);
/* 503 */       for (int j = 0; j < i; j++) {
/* 504 */         threadGroup2appContext.remove(arrayOfThreadGroup[j]);
/*     */       }
/*     */     }
/* 507 */     threadGroup2appContext.remove(this.threadGroup);
/*     */ 
/* 509 */     threadAppContext.set(null);
/*     */     try
/*     */     {
/* 513 */       this.threadGroup.destroy();
/*     */     }
/*     */     catch (IllegalThreadStateException localIllegalThreadStateException)
/*     */     {
/*     */     }
/* 518 */     synchronized (this.table) {
/* 519 */       this.table.clear();
/*     */     }
/*     */ 
/* 522 */     numAppContexts -= 1;
/*     */ 
/* 524 */     this.mostRecentKeyValue = null;
/*     */   }
/*     */ 
/*     */   static void stopEventDispatchThreads()
/*     */   {
/* 561 */     for (AppContext localAppContext : getAppContexts())
/* 562 */       if (!localAppContext.isDisposed())
/*     */       {
/* 565 */         PostShutdownEventRunnable localPostShutdownEventRunnable = new PostShutdownEventRunnable(localAppContext);
/*     */ 
/* 568 */         if (localAppContext != getAppContext())
/*     */         {
/* 571 */           CreateThreadAction localCreateThreadAction = new CreateThreadAction(localAppContext, localPostShutdownEventRunnable);
/* 572 */           Thread localThread = (Thread)AccessController.doPrivileged(localCreateThreadAction);
/* 573 */           localThread.start();
/*     */         } else {
/* 575 */           localPostShutdownEventRunnable.run();
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public Object get(Object paramObject)
/*     */   {
/* 598 */     synchronized (this.table)
/*     */     {
/* 605 */       MostRecentKeyValue localMostRecentKeyValue1 = this.mostRecentKeyValue;
/* 606 */       if ((localMostRecentKeyValue1 != null) && (localMostRecentKeyValue1.key == paramObject)) {
/* 607 */         return localMostRecentKeyValue1.value;
/*     */       }
/*     */ 
/* 610 */       Object localObject1 = this.table.get(paramObject);
/* 611 */       if (this.mostRecentKeyValue == null) {
/* 612 */         this.mostRecentKeyValue = new MostRecentKeyValue(paramObject, localObject1);
/* 613 */         this.shadowMostRecentKeyValue = new MostRecentKeyValue(paramObject, localObject1);
/*     */       } else {
/* 615 */         MostRecentKeyValue localMostRecentKeyValue2 = this.mostRecentKeyValue;
/* 616 */         this.shadowMostRecentKeyValue.setPair(paramObject, localObject1);
/* 617 */         this.mostRecentKeyValue = this.shadowMostRecentKeyValue;
/* 618 */         this.shadowMostRecentKeyValue = localMostRecentKeyValue2;
/*     */       }
/* 620 */       return localObject1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object put(Object paramObject1, Object paramObject2)
/*     */   {
/* 642 */     synchronized (this.table) {
/* 643 */       MostRecentKeyValue localMostRecentKeyValue = this.mostRecentKeyValue;
/* 644 */       if ((localMostRecentKeyValue != null) && (localMostRecentKeyValue.key == paramObject1))
/* 645 */         localMostRecentKeyValue.value = paramObject2;
/* 646 */       return this.table.put(paramObject1, paramObject2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object remove(Object paramObject)
/*     */   {
/* 661 */     synchronized (this.table) {
/* 662 */       MostRecentKeyValue localMostRecentKeyValue = this.mostRecentKeyValue;
/* 663 */       if ((localMostRecentKeyValue != null) && (localMostRecentKeyValue.key == paramObject))
/* 664 */         localMostRecentKeyValue.value = null;
/* 665 */       return this.table.remove(paramObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ThreadGroup getThreadGroup()
/*     */   {
/* 675 */     return this.threadGroup;
/*     */   }
/*     */ 
/*     */   public ClassLoader getContextClassLoader()
/*     */   {
/* 685 */     return this.contextClassLoader;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 694 */     return getClass().getName() + "[threadGroup=" + this.threadGroup.getName() + "]";
/*     */   }
/*     */ 
/*     */   public synchronized PropertyChangeListener[] getPropertyChangeListeners()
/*     */   {
/* 712 */     if (this.changeSupport == null) {
/* 713 */       return new PropertyChangeListener[0];
/*     */     }
/* 715 */     return this.changeSupport.getPropertyChangeListeners();
/*     */   }
/*     */ 
/*     */   public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 744 */     if (paramPropertyChangeListener == null) {
/* 745 */       return;
/*     */     }
/* 747 */     if (this.changeSupport == null) {
/* 748 */       this.changeSupport = new PropertyChangeSupport(this);
/*     */     }
/* 750 */     this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*     */   }
/*     */ 
/*     */   public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 770 */     if ((paramPropertyChangeListener == null) || (this.changeSupport == null)) {
/* 771 */       return;
/*     */     }
/* 773 */     this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
/*     */   }
/*     */ 
/*     */   public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
/*     */   {
/* 791 */     if (this.changeSupport == null) {
/* 792 */       return new PropertyChangeListener[0];
/*     */     }
/* 794 */     return this.changeSupport.getPropertyChangeListeners(paramString);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 201 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/* 203 */         Object localObject = Thread.currentThread().getThreadGroup();
/*     */ 
/* 205 */         ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent();
/* 206 */         while (localThreadGroup != null)
/*     */         {
/* 208 */           localObject = localThreadGroup;
/* 209 */           localThreadGroup = ((ThreadGroup)localObject).getParent();
/*     */         }
/* 211 */         AppContext.access$002(new AppContext((ThreadGroup)localObject));
/* 212 */         AppContext.access$102(1);
/* 213 */         return AppContext.mainAppContext;
/*     */       }
/*     */     });
/* 266 */     threadAppContext = new ThreadLocal();
/*     */ 
/* 799 */     SharedSecrets.setJavaAWTAccess(new JavaAWTAccess() {
/*     */       public Object get(Object paramAnonymousObject) {
/* 801 */         return AppContext.getAppContext().get(paramAnonymousObject);
/*     */       }
/*     */       public void put(Object paramAnonymousObject1, Object paramAnonymousObject2) {
/* 804 */         AppContext.getAppContext().put(paramAnonymousObject1, paramAnonymousObject2);
/*     */       }
/*     */       public void remove(Object paramAnonymousObject) {
/* 807 */         AppContext.getAppContext().remove(paramAnonymousObject);
/*     */       }
/*     */       public boolean isDisposed() {
/* 810 */         return AppContext.getAppContext().isDisposed();
/*     */       }
/*     */       public boolean isMainAppContext() {
/* 813 */         return AppContext.numAppContexts == 1;
/*     */       }
/*     */       public Object getContext() {
/* 816 */         return AppContext.getAppContext();
/*     */       }
/*     */       public Object getExecutionContext() {
/* 819 */         return AppContext.access$500();
/*     */       }
/*     */       public Object get(Object paramAnonymousObject1, Object paramAnonymousObject2) {
/* 822 */         return ((AppContext)paramAnonymousObject1).get(paramAnonymousObject2);
/*     */       }
/*     */       public void put(Object paramAnonymousObject1, Object paramAnonymousObject2, Object paramAnonymousObject3) {
/* 825 */         ((AppContext)paramAnonymousObject1).put(paramAnonymousObject2, paramAnonymousObject3);
/*     */       }
/*     */       public void remove(Object paramAnonymousObject1, Object paramAnonymousObject2) {
/* 828 */         ((AppContext)paramAnonymousObject1).remove(paramAnonymousObject2);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static final class CreateThreadAction
/*     */     implements PrivilegedAction
/*     */   {
/*     */     private final AppContext appContext;
/*     */     private final Runnable runnable;
/*     */ 
/*     */     public CreateThreadAction(AppContext paramAppContext, Runnable paramRunnable)
/*     */     {
/* 547 */       this.appContext = paramAppContext;
/* 548 */       this.runnable = paramRunnable;
/*     */     }
/*     */ 
/*     */     public Object run() {
/* 552 */       Thread localThread = new Thread(this.appContext.getThreadGroup(), this.runnable);
/* 553 */       localThread.setContextClassLoader(this.appContext.getContextClassLoader());
/* 554 */       localThread.setPriority(6);
/* 555 */       localThread.setDaemon(true);
/* 556 */       return localThread;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class PostShutdownEventRunnable
/*     */     implements Runnable
/*     */   {
/*     */     private final AppContext appContext;
/*     */ 
/*     */     public PostShutdownEventRunnable(AppContext paramAppContext)
/*     */     {
/* 531 */       this.appContext = paramAppContext;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 535 */       EventQueue localEventQueue = (EventQueue)this.appContext.get(AppContext.EVENT_QUEUE_KEY);
/* 536 */       if (localEventQueue != null)
/* 537 */         localEventQueue.postEvent(AWTAutoShutdown.getShutdownEvent());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.AppContext
 * JD-Core Version:    0.6.2
 */