/*      */ package java.awt;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.InputEvent;
/*      */ import java.awt.event.InputMethodEvent;
/*      */ import java.awt.event.InvocationEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.PaintEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.EmptyStackException;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.locks.Condition;
/*      */ import java.util.concurrent.locks.Lock;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.EventQueueAccessor;
/*      */ import sun.awt.AWTAutoShutdown;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.EventQueueItem;
/*      */ import sun.awt.PeerEvent;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.dnd.SunDropTargetEvent;
/*      */ import sun.misc.JavaSecurityAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class EventQueue
/*      */ {
/*  104 */   private static final AtomicInteger threadInitNumber = new AtomicInteger(0);
/*      */   private static final int LOW_PRIORITY = 0;
/*      */   private static final int NORM_PRIORITY = 1;
/*      */   private static final int HIGH_PRIORITY = 2;
/*      */   private static final int ULTIMATE_PRIORITY = 3;
/*      */   private static final int NUM_PRIORITIES = 4;
/*  121 */   private Queue[] queues = new Queue[4];
/*      */   private EventQueue nextQueue;
/*      */   private EventQueue previousQueue;
/*      */   private final Lock pushPopLock;
/*      */   private final Condition pushPopCond;
/*  148 */   private static final Runnable dummyRunnable = new Runnable() {
/*  148 */     public void run() {  }  } ;
/*      */   private EventDispatchThread dispatchThread;
/*  155 */   private final ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
/*      */ 
/*  157 */   private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
/*      */ 
/*  163 */   private long mostRecentEventTime = System.currentTimeMillis();
/*      */   private WeakReference currentEvent;
/*      */   private volatile int waitForID;
/*  177 */   private final String name = "AWT-EventQueue-" + threadInitNumber.getAndIncrement();
/*      */ 
/*  179 */   private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.EventQueue");
/*      */   private static final int PAINT = 0;
/*      */   private static final int UPDATE = 1;
/*      */   private static final int MOVE = 2;
/*      */   private static final int DRAG = 3;
/*      */   private static final int PEER = 4;
/*      */   private static final int CACHE_LENGTH = 5;
/*  643 */   private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
/*      */ 
/*      */   public EventQueue()
/*      */   {
/*  211 */     for (int i = 0; i < 4; i++) {
/*  212 */       this.queues[i] = new Queue();
/*      */     }
/*      */ 
/*  223 */     this.pushPopLock = ((Lock)AppContext.getAppContext().get(AppContext.EVENT_QUEUE_LOCK_KEY));
/*  224 */     this.pushPopCond = ((Condition)AppContext.getAppContext().get(AppContext.EVENT_QUEUE_COND_KEY));
/*      */   }
/*      */ 
/*      */   public void postEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  238 */     SunToolkit.flushPendingEvents();
/*  239 */     postEventPrivate(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   private final void postEventPrivate(AWTEvent paramAWTEvent)
/*      */   {
/*  252 */     paramAWTEvent.isPosted = true;
/*  253 */     this.pushPopLock.lock();
/*      */     try {
/*  255 */       if (this.nextQueue != null)
/*      */       {
/*  257 */         this.nextQueue.postEventPrivate(paramAWTEvent);
/*      */       }
/*      */       else {
/*  260 */         if (this.dispatchThread == null) {
/*  261 */           if (paramAWTEvent.getSource() == AWTAutoShutdown.getInstance()) {
/*      */             return;
/*      */           }
/*  264 */           initDispatchThread();
/*      */         }
/*      */ 
/*  267 */         postEvent(paramAWTEvent, getPriority(paramAWTEvent));
/*      */       }
/*      */     } finally { this.pushPopLock.unlock(); }
/*      */   }
/*      */ 
/*      */   private static int getPriority(AWTEvent paramAWTEvent)
/*      */   {
/*  274 */     if ((paramAWTEvent instanceof PeerEvent)) {
/*  275 */       PeerEvent localPeerEvent = (PeerEvent)paramAWTEvent;
/*  276 */       if ((localPeerEvent.getFlags() & 0x2) != 0L) {
/*  277 */         return 3;
/*      */       }
/*  279 */       if ((localPeerEvent.getFlags() & 1L) != 0L) {
/*  280 */         return 2;
/*      */       }
/*  282 */       if ((localPeerEvent.getFlags() & 0x4) != 0L) {
/*  283 */         return 0;
/*      */       }
/*      */     }
/*  286 */     int i = paramAWTEvent.getID();
/*  287 */     if ((i >= 800) && (i <= 801)) {
/*  288 */       return 0;
/*      */     }
/*  290 */     return 1;
/*      */   }
/*      */ 
/*      */   private void postEvent(AWTEvent paramAWTEvent, int paramInt)
/*      */   {
/*  302 */     if (coalesceEvent(paramAWTEvent, paramInt)) {
/*  303 */       return;
/*      */     }
/*      */ 
/*  306 */     EventQueueItem localEventQueueItem = new EventQueueItem(paramAWTEvent);
/*      */ 
/*  308 */     cacheEQItem(localEventQueueItem);
/*      */ 
/*  310 */     int i = paramAWTEvent.getID() == this.waitForID ? 1 : 0;
/*      */ 
/*  312 */     if (this.queues[paramInt].head == null) {
/*  313 */       boolean bool = noEvents();
/*  314 */       this.queues[paramInt].head = (this.queues[paramInt].tail = localEventQueueItem);
/*      */ 
/*  316 */       if (bool) {
/*  317 */         if (paramAWTEvent.getSource() != AWTAutoShutdown.getInstance()) {
/*  318 */           AWTAutoShutdown.getInstance().notifyThreadBusy(this.dispatchThread);
/*      */         }
/*  320 */         this.pushPopCond.signalAll();
/*  321 */       } else if (i != 0) {
/*  322 */         this.pushPopCond.signalAll();
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  327 */       this.queues[paramInt].tail.next = localEventQueueItem;
/*  328 */       this.queues[paramInt].tail = localEventQueueItem;
/*  329 */       if (i != 0)
/*  330 */         this.pushPopCond.signalAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean coalescePaintEvent(PaintEvent paramPaintEvent)
/*      */   {
/*  336 */     ComponentPeer localComponentPeer = ((Component)paramPaintEvent.getSource()).peer;
/*  337 */     if (localComponentPeer != null) {
/*  338 */       localComponentPeer.coalescePaintEvent(paramPaintEvent);
/*      */     }
/*  340 */     EventQueueItem[] arrayOfEventQueueItem = ((Component)paramPaintEvent.getSource()).eventCache;
/*  341 */     if (arrayOfEventQueueItem == null) {
/*  342 */       return false;
/*      */     }
/*  344 */     int i = eventToCacheIndex(paramPaintEvent);
/*      */ 
/*  346 */     if ((i != -1) && (arrayOfEventQueueItem[i] != null)) {
/*  347 */       PaintEvent localPaintEvent = mergePaintEvents(paramPaintEvent, (PaintEvent)arrayOfEventQueueItem[i].event);
/*  348 */       if (localPaintEvent != null) {
/*  349 */         arrayOfEventQueueItem[i].event = localPaintEvent;
/*  350 */         return true;
/*      */       }
/*      */     }
/*  353 */     return false;
/*      */   }
/*      */ 
/*      */   private PaintEvent mergePaintEvents(PaintEvent paramPaintEvent1, PaintEvent paramPaintEvent2) {
/*  357 */     Rectangle localRectangle1 = paramPaintEvent1.getUpdateRect();
/*  358 */     Rectangle localRectangle2 = paramPaintEvent2.getUpdateRect();
/*  359 */     if (localRectangle2.contains(localRectangle1)) {
/*  360 */       return paramPaintEvent2;
/*      */     }
/*  362 */     if (localRectangle1.contains(localRectangle2)) {
/*  363 */       return paramPaintEvent1;
/*      */     }
/*  365 */     return null;
/*      */   }
/*      */ 
/*      */   private boolean coalesceMouseEvent(MouseEvent paramMouseEvent) {
/*  369 */     if ((paramMouseEvent instanceof SunDropTargetEvent))
/*      */     {
/*  371 */       return false;
/*      */     }
/*  373 */     EventQueueItem[] arrayOfEventQueueItem = ((Component)paramMouseEvent.getSource()).eventCache;
/*  374 */     if (arrayOfEventQueueItem == null) {
/*  375 */       return false;
/*      */     }
/*  377 */     int i = eventToCacheIndex(paramMouseEvent);
/*  378 */     if ((i != -1) && (arrayOfEventQueueItem[i] != null)) {
/*  379 */       arrayOfEventQueueItem[i].event = paramMouseEvent;
/*  380 */       return true;
/*      */     }
/*  382 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean coalescePeerEvent(PeerEvent paramPeerEvent) {
/*  386 */     EventQueueItem[] arrayOfEventQueueItem = ((Component)paramPeerEvent.getSource()).eventCache;
/*  387 */     if (arrayOfEventQueueItem == null) {
/*  388 */       return false;
/*      */     }
/*  390 */     int i = eventToCacheIndex(paramPeerEvent);
/*  391 */     if ((i != -1) && (arrayOfEventQueueItem[i] != null)) {
/*  392 */       paramPeerEvent = paramPeerEvent.coalesceEvents((PeerEvent)arrayOfEventQueueItem[i].event);
/*  393 */       if (paramPeerEvent != null) {
/*  394 */         arrayOfEventQueueItem[i].event = paramPeerEvent;
/*  395 */         return true;
/*      */       }
/*  397 */       arrayOfEventQueueItem[i] = null;
/*      */     }
/*      */ 
/*  400 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean coalesceOtherEvent(AWTEvent paramAWTEvent, int paramInt)
/*      */   {
/*  411 */     int i = paramAWTEvent.getID();
/*  412 */     Component localComponent = (Component)paramAWTEvent.getSource();
/*  413 */     for (EventQueueItem localEventQueueItem = this.queues[paramInt].head; 
/*  414 */       localEventQueueItem != null; localEventQueueItem = localEventQueueItem.next)
/*      */     {
/*  417 */       if ((localEventQueueItem.event.getSource() == localComponent) && (localEventQueueItem.event.getID() == i)) {
/*  418 */         AWTEvent localAWTEvent = localComponent.coalesceEvents(localEventQueueItem.event, paramAWTEvent);
/*      */ 
/*  420 */         if (localAWTEvent != null) {
/*  421 */           localEventQueueItem.event = localAWTEvent;
/*  422 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  426 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean coalesceEvent(AWTEvent paramAWTEvent, int paramInt) {
/*  430 */     if (!(paramAWTEvent.getSource() instanceof Component)) {
/*  431 */       return false;
/*      */     }
/*  433 */     if ((paramAWTEvent instanceof PeerEvent)) {
/*  434 */       return coalescePeerEvent((PeerEvent)paramAWTEvent);
/*      */     }
/*      */ 
/*  437 */     if ((((Component)paramAWTEvent.getSource()).isCoalescingEnabled()) && (coalesceOtherEvent(paramAWTEvent, paramInt)))
/*      */     {
/*  440 */       return true;
/*      */     }
/*  442 */     if ((paramAWTEvent instanceof PaintEvent)) {
/*  443 */       return coalescePaintEvent((PaintEvent)paramAWTEvent);
/*      */     }
/*  445 */     if ((paramAWTEvent instanceof MouseEvent)) {
/*  446 */       return coalesceMouseEvent((MouseEvent)paramAWTEvent);
/*      */     }
/*  448 */     return false;
/*      */   }
/*      */ 
/*      */   private void cacheEQItem(EventQueueItem paramEventQueueItem) {
/*  452 */     if ((paramEventQueueItem.event instanceof SunDropTargetEvent))
/*      */     {
/*  454 */       return;
/*      */     }
/*  456 */     int i = eventToCacheIndex(paramEventQueueItem.event);
/*  457 */     if ((i != -1) && ((paramEventQueueItem.event.getSource() instanceof Component))) {
/*  458 */       Component localComponent = (Component)paramEventQueueItem.event.getSource();
/*  459 */       if (localComponent.eventCache == null) {
/*  460 */         localComponent.eventCache = new EventQueueItem[5];
/*      */       }
/*  462 */       localComponent.eventCache[i] = paramEventQueueItem;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void uncacheEQItem(EventQueueItem paramEventQueueItem) {
/*  467 */     int i = eventToCacheIndex(paramEventQueueItem.event);
/*  468 */     if ((i != -1) && ((paramEventQueueItem.event.getSource() instanceof Component))) {
/*  469 */       Component localComponent = (Component)paramEventQueueItem.event.getSource();
/*  470 */       if (localComponent.eventCache == null) {
/*  471 */         return;
/*      */       }
/*  473 */       localComponent.eventCache[i] = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int eventToCacheIndex(AWTEvent paramAWTEvent)
/*      */   {
/*  485 */     switch (paramAWTEvent.getID()) {
/*      */     case 800:
/*  487 */       return 0;
/*      */     case 801:
/*  489 */       return 1;
/*      */     case 503:
/*  491 */       return 2;
/*      */     case 506:
/*  493 */       return 3;
/*      */     }
/*  495 */     return (paramAWTEvent instanceof PeerEvent) ? 4 : -1;
/*      */   }
/*      */ 
/*      */   private boolean noEvents()
/*      */   {
/*  505 */     for (int i = 0; i < 4; i++) {
/*  506 */       if (this.queues[i].head != null) {
/*  507 */         return false;
/*      */       }
/*      */     }
/*      */ 
/*  511 */     return true;
/*      */   }
/*      */ 
/*      */   public AWTEvent getNextEvent()
/*      */     throws InterruptedException
/*      */   {
/*      */     while (true)
/*      */     {
/*  529 */       SunToolkit.flushPendingEvents();
/*  530 */       this.pushPopLock.lock();
/*      */       try {
/*  532 */         AWTEvent localAWTEvent1 = getNextEventPrivate();
/*  533 */         if (localAWTEvent1 != null) {
/*  534 */           return localAWTEvent1;
/*      */         }
/*  536 */         AWTAutoShutdown.getInstance().notifyThreadFree(this.dispatchThread);
/*  537 */         this.pushPopCond.await();
/*      */       } finally {
/*  539 */         this.pushPopLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   AWTEvent getNextEventPrivate()
/*      */     throws InterruptedException
/*      */   {
/*  548 */     for (int i = 3; i >= 0; i--) {
/*  549 */       if (this.queues[i].head != null) {
/*  550 */         EventQueueItem localEventQueueItem = this.queues[i].head;
/*  551 */         this.queues[i].head = localEventQueueItem.next;
/*  552 */         if (localEventQueueItem.next == null) {
/*  553 */           this.queues[i].tail = null;
/*      */         }
/*  555 */         uncacheEQItem(localEventQueueItem);
/*  556 */         return localEventQueueItem.event;
/*      */       }
/*      */     }
/*  559 */     return null;
/*      */   }
/*      */ 
/*      */   AWTEvent getNextEvent(int paramInt)
/*      */     throws InterruptedException
/*      */   {
/*      */     while (true)
/*      */     {
/*  569 */       SunToolkit.flushPendingEvents();
/*  570 */       this.pushPopLock.lock();
/*      */       try {
/*  572 */         for (int i = 0; i < 4; i++) {
/*  573 */           EventQueueItem localEventQueueItem1 = this.queues[i].head; EventQueueItem localEventQueueItem2 = null;
/*  574 */           for (; localEventQueueItem1 != null; localEventQueueItem1 = localEventQueueItem1.next)
/*      */           {
/*  576 */             if (localEventQueueItem1.event.getID() == paramInt) {
/*  577 */               if (localEventQueueItem2 == null)
/*  578 */                 this.queues[i].head = localEventQueueItem1.next;
/*      */               else {
/*  580 */                 localEventQueueItem2.next = localEventQueueItem1.next;
/*      */               }
/*  582 */               if (this.queues[i].tail == localEventQueueItem1) {
/*  583 */                 this.queues[i].tail = localEventQueueItem2;
/*      */               }
/*  585 */               uncacheEQItem(localEventQueueItem1);
/*  586 */               return localEventQueueItem1.event;
/*      */             }
/*  574 */             localEventQueueItem2 = localEventQueueItem1;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  590 */         this.waitForID = paramInt;
/*  591 */         this.pushPopCond.await();
/*  592 */         this.waitForID = 0;
/*      */       } finally {
/*  594 */         this.pushPopLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public AWTEvent peekEvent()
/*      */   {
/*  605 */     this.pushPopLock.lock();
/*      */     try {
/*  607 */       for (int i = 3; i >= 0; i--)
/*  608 */         if (this.queues[i].head != null)
/*  609 */           return this.queues[i].head.event;
/*      */     }
/*      */     finally
/*      */     {
/*  613 */       this.pushPopLock.unlock();
/*      */     }
/*      */ 
/*  616 */     return null;
/*      */   }
/*      */ 
/*      */   public AWTEvent peekEvent(int paramInt)
/*      */   {
/*  626 */     this.pushPopLock.lock();
/*      */     try {
/*  628 */       for (int i = 3; i >= 0; i--) {
/*  629 */         for (EventQueueItem localEventQueueItem = this.queues[i].head; 
/*  630 */           localEventQueueItem != null; localEventQueueItem = localEventQueueItem.next)
/*  631 */           if (localEventQueueItem.event.getID() == paramInt)
/*  632 */             return localEventQueueItem.event;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  637 */       this.pushPopLock.unlock();
/*      */     }
/*      */ 
/*  640 */     return null;
/*      */   }
/*      */ 
/*      */   protected void dispatchEvent(final AWTEvent paramAWTEvent)
/*      */   {
/*  685 */     final Object localObject = paramAWTEvent.getSource();
/*  686 */     final PrivilegedAction local3 = new PrivilegedAction() {
/*      */       public Void run() {
/*  688 */         EventQueue.this.dispatchEventImpl(paramAWTEvent, localObject);
/*  689 */         return null;
/*      */       }
/*      */     };
/*  693 */     AccessControlContext localAccessControlContext1 = AccessController.getContext();
/*  694 */     AccessControlContext localAccessControlContext2 = getAccessControlContextFrom(localObject);
/*  695 */     final AccessControlContext localAccessControlContext3 = paramAWTEvent.getAccessControlContext();
/*  696 */     if (localAccessControlContext2 == null)
/*  697 */       javaSecurityAccess.doIntersectionPrivilege(local3, localAccessControlContext1, localAccessControlContext3);
/*      */     else
/*  699 */       javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */       {
/*      */         public Void run() {
/*  702 */           EventQueue.javaSecurityAccess.doIntersectionPrivilege(local3, localAccessControlContext3);
/*  703 */           return null;
/*      */         }
/*      */       }
/*      */       , localAccessControlContext1, localAccessControlContext2);
/*      */   }
/*      */ 
/*      */   private static AccessControlContext getAccessControlContextFrom(Object paramObject)
/*      */   {
/*  710 */     return (paramObject instanceof TrayIcon) ? ((TrayIcon)paramObject).getAccessControlContext() : (paramObject instanceof MenuComponent) ? ((MenuComponent)paramObject).getAccessControlContext() : (paramObject instanceof Component) ? ((Component)paramObject).getAccessControlContext() : null;
/*      */   }
/*      */ 
/*      */   private void dispatchEventImpl(AWTEvent paramAWTEvent, Object paramObject)
/*      */   {
/*  723 */     paramAWTEvent.isPosted = true;
/*  724 */     if ((paramAWTEvent instanceof ActiveEvent))
/*      */     {
/*  726 */       setCurrentEventAndMostRecentTimeImpl(paramAWTEvent);
/*  727 */       ((ActiveEvent)paramAWTEvent).dispatch();
/*  728 */     } else if ((paramObject instanceof Component)) {
/*  729 */       ((Component)paramObject).dispatchEvent(paramAWTEvent);
/*  730 */       paramAWTEvent.dispatched();
/*  731 */     } else if ((paramObject instanceof MenuComponent)) {
/*  732 */       ((MenuComponent)paramObject).dispatchEvent(paramAWTEvent);
/*  733 */     } else if ((paramObject instanceof TrayIcon)) {
/*  734 */       ((TrayIcon)paramObject).dispatchEvent(paramAWTEvent);
/*  735 */     } else if ((paramObject instanceof AWTAutoShutdown)) {
/*  736 */       if (noEvents()) {
/*  737 */         this.dispatchThread.stopDispatching();
/*      */       }
/*      */     }
/*  740 */     else if (eventLog.isLoggable(500)) {
/*  741 */       eventLog.fine("Unable to dispatch event: " + paramAWTEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static long getMostRecentEventTime()
/*      */   {
/*  775 */     return Toolkit.getEventQueue().getMostRecentEventTimeImpl();
/*      */   }
/*      */   private long getMostRecentEventTimeImpl() {
/*  778 */     this.pushPopLock.lock();
/*      */     try {
/*  780 */       return Thread.currentThread() == this.dispatchThread ? this.mostRecentEventTime : System.currentTimeMillis();
/*      */     }
/*      */     finally
/*      */     {
/*  784 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   long getMostRecentEventTimeEx()
/*      */   {
/*  792 */     this.pushPopLock.lock();
/*      */     try {
/*  794 */       return this.mostRecentEventTime;
/*      */     } finally {
/*  796 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static AWTEvent getCurrentEvent()
/*      */   {
/*  813 */     return Toolkit.getEventQueue().getCurrentEventImpl();
/*      */   }
/*      */   private AWTEvent getCurrentEventImpl() {
/*  816 */     this.pushPopLock.lock();
/*      */     try {
/*  818 */       return Thread.currentThread() == this.dispatchThread ? (AWTEvent)this.currentEvent.get() : null;
/*      */     }
/*      */     finally
/*      */     {
/*  822 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void push(EventQueue paramEventQueue)
/*      */   {
/*  838 */     if (eventLog.isLoggable(500)) {
/*  839 */       eventLog.fine("EventQueue.push(" + paramEventQueue + ")");
/*      */     }
/*      */ 
/*  842 */     this.pushPopLock.lock();
/*      */     try {
/*  844 */       EventQueue localEventQueue = this;
/*  845 */       while (localEventQueue.nextQueue != null) {
/*  846 */         localEventQueue = localEventQueue.nextQueue;
/*      */       }
/*      */ 
/*  849 */       if ((localEventQueue.dispatchThread != null) && (localEventQueue.dispatchThread.getEventQueue() == this))
/*      */       {
/*  852 */         paramEventQueue.dispatchThread = localEventQueue.dispatchThread;
/*  853 */         localEventQueue.dispatchThread.setEventQueue(paramEventQueue);
/*      */       }
/*      */ 
/*  857 */       while (localEventQueue.peekEvent() != null) {
/*      */         try
/*      */         {
/*  860 */           paramEventQueue.postEventPrivate(localEventQueue.getNextEventPrivate());
/*      */         } catch (InterruptedException localInterruptedException) {
/*  862 */           if (eventLog.isLoggable(500)) {
/*  863 */             eventLog.fine("Interrupted push", localInterruptedException);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  872 */       localEventQueue.postEventPrivate(new InvocationEvent(localEventQueue, dummyRunnable));
/*      */ 
/*  874 */       paramEventQueue.previousQueue = localEventQueue;
/*  875 */       localEventQueue.nextQueue = paramEventQueue;
/*      */ 
/*  877 */       AppContext localAppContext = AppContext.getAppContext();
/*  878 */       if (localAppContext.get(AppContext.EVENT_QUEUE_KEY) == localEventQueue) {
/*  879 */         localAppContext.put(AppContext.EVENT_QUEUE_KEY, paramEventQueue);
/*      */       }
/*      */ 
/*  882 */       this.pushPopCond.signalAll();
/*      */     } finally {
/*  884 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void pop()
/*      */     throws EmptyStackException
/*      */   {
/*  902 */     if (eventLog.isLoggable(500)) {
/*  903 */       eventLog.fine("EventQueue.pop(" + this + ")");
/*      */     }
/*      */ 
/*  906 */     this.pushPopLock.lock();
/*      */     try {
/*  908 */       EventQueue localEventQueue1 = this;
/*  909 */       while (localEventQueue1.nextQueue != null) {
/*  910 */         localEventQueue1 = localEventQueue1.nextQueue;
/*      */       }
/*  912 */       EventQueue localEventQueue2 = localEventQueue1.previousQueue;
/*  913 */       if (localEventQueue2 == null) {
/*  914 */         throw new EmptyStackException();
/*      */       }
/*      */ 
/*  917 */       localEventQueue1.previousQueue = null;
/*  918 */       localEventQueue2.nextQueue = null;
/*      */ 
/*  921 */       while (localEventQueue1.peekEvent() != null) {
/*      */         try {
/*  923 */           localEventQueue2.postEventPrivate(localEventQueue1.getNextEventPrivate());
/*      */         } catch (InterruptedException localInterruptedException) {
/*  925 */           if (eventLog.isLoggable(500)) {
/*  926 */             eventLog.fine("Interrupted pop", localInterruptedException);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  931 */       if ((localEventQueue1.dispatchThread != null) && (localEventQueue1.dispatchThread.getEventQueue() == this))
/*      */       {
/*  934 */         localEventQueue2.dispatchThread = localEventQueue1.dispatchThread;
/*  935 */         localEventQueue1.dispatchThread.setEventQueue(localEventQueue2);
/*      */       }
/*      */ 
/*  938 */       AppContext localAppContext = AppContext.getAppContext();
/*  939 */       if (localAppContext.get(AppContext.EVENT_QUEUE_KEY) == this) {
/*  940 */         localAppContext.put(AppContext.EVENT_QUEUE_KEY, localEventQueue2);
/*      */       }
/*      */ 
/*  945 */       localEventQueue1.postEventPrivate(new InvocationEvent(localEventQueue1, dummyRunnable));
/*      */ 
/*  947 */       this.pushPopCond.signalAll();
/*      */     } finally {
/*  949 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public SecondaryLoop createSecondaryLoop()
/*      */   {
/*  969 */     return createSecondaryLoop(null, null, 0L);
/*      */   }
/*      */ 
/*      */   SecondaryLoop createSecondaryLoop(Conditional paramConditional, EventFilter paramEventFilter, long paramLong) {
/*  973 */     this.pushPopLock.lock();
/*      */     try
/*      */     {
/*      */       Object localObject1;
/*  975 */       if (this.nextQueue != null)
/*      */       {
/*  977 */         return this.nextQueue.createSecondaryLoop(paramConditional, paramEventFilter, paramLong);
/*      */       }
/*  979 */       if (this.dispatchThread == null) {
/*  980 */         initDispatchThread();
/*      */       }
/*  982 */       return new WaitDispatchSupport(this.dispatchThread, paramConditional, paramEventFilter, paramLong);
/*      */     } finally {
/*  984 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean isDispatchThread()
/*      */   {
/* 1009 */     EventQueue localEventQueue = Toolkit.getEventQueue();
/* 1010 */     return localEventQueue.isDispatchThreadImpl();
/*      */   }
/*      */ 
/*      */   final boolean isDispatchThreadImpl() {
/* 1014 */     Object localObject1 = this;
/* 1015 */     this.pushPopLock.lock();
/*      */     try {
/* 1017 */       EventQueue localEventQueue = ((EventQueue)localObject1).nextQueue;
/* 1018 */       while (localEventQueue != null) {
/* 1019 */         localObject1 = localEventQueue;
/* 1020 */         localEventQueue = ((EventQueue)localObject1).nextQueue;
/*      */       }
/* 1022 */       return Thread.currentThread() == ((EventQueue)localObject1).dispatchThread;
/*      */     } finally {
/* 1024 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   final void initDispatchThread() {
/* 1029 */     this.pushPopLock.lock();
/*      */     try {
/* 1031 */       AppContext localAppContext = AppContext.getAppContext();
/* 1032 */       if ((this.dispatchThread == null) && (!this.threadGroup.isDestroyed()) && (!localAppContext.isDisposed())) {
/* 1033 */         this.dispatchThread = ((EventDispatchThread)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public EventDispatchThread run() {
/* 1036 */             EventDispatchThread localEventDispatchThread = new EventDispatchThread(EventQueue.this.threadGroup, EventQueue.this.name, EventQueue.this);
/*      */ 
/* 1040 */             localEventDispatchThread.setContextClassLoader(EventQueue.this.classLoader);
/* 1041 */             localEventDispatchThread.setPriority(6);
/* 1042 */             localEventDispatchThread.setDaemon(false);
/* 1043 */             return localEventDispatchThread;
/*      */           }
/*      */         }));
/* 1047 */         AWTAutoShutdown.getInstance().notifyThreadBusy(this.dispatchThread);
/* 1048 */         this.dispatchThread.start();
/*      */       }
/*      */     } finally {
/* 1051 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   final boolean detachDispatchThread(EventDispatchThread paramEventDispatchThread, boolean paramBoolean)
/*      */   {
/* 1064 */     this.pushPopLock.lock();
/*      */     try
/*      */     {
/*      */       boolean bool;
/* 1066 */       if (paramEventDispatchThread == this.dispatchThread)
/*      */       {
/* 1074 */         if (((!paramBoolean) && (peekEvent() != null)) || (!SunToolkit.isPostEventQueueEmpty())) {
/* 1075 */           return false;
/*      */         }
/* 1077 */         this.dispatchThread = null;
/*      */       }
/* 1079 */       AWTAutoShutdown.getInstance().notifyThreadFree(paramEventDispatchThread);
/* 1080 */       return true;
/*      */     } finally {
/* 1082 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   final EventDispatchThread getDispatchThread()
/*      */   {
/* 1096 */     this.pushPopLock.lock();
/*      */     try {
/* 1098 */       return this.dispatchThread;
/*      */     } finally {
/* 1100 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   final void removeSourceEvents(Object paramObject, boolean paramBoolean)
/*      */   {
/* 1117 */     SunToolkit.flushPendingEvents();
/* 1118 */     this.pushPopLock.lock();
/*      */     try {
/* 1120 */       for (int i = 0; i < 4; i++) {
/* 1121 */         EventQueueItem localEventQueueItem1 = this.queues[i].head;
/* 1122 */         EventQueueItem localEventQueueItem2 = null;
/* 1123 */         while (localEventQueueItem1 != null) {
/* 1124 */           if ((localEventQueueItem1.event.getSource() == paramObject) && ((paramBoolean) || ((!(localEventQueueItem1.event instanceof SequencedEvent)) && (!(localEventQueueItem1.event instanceof SentEvent)) && (!(localEventQueueItem1.event instanceof FocusEvent)) && (!(localEventQueueItem1.event instanceof WindowEvent)) && (!(localEventQueueItem1.event instanceof KeyEvent)) && (!(localEventQueueItem1.event instanceof InputMethodEvent)))))
/*      */           {
/* 1133 */             if ((localEventQueueItem1.event instanceof SequencedEvent)) {
/* 1134 */               ((SequencedEvent)localEventQueueItem1.event).dispose();
/*      */             }
/* 1136 */             if ((localEventQueueItem1.event instanceof SentEvent)) {
/* 1137 */               ((SentEvent)localEventQueueItem1.event).dispose();
/*      */             }
/* 1139 */             if (localEventQueueItem2 == null)
/* 1140 */               this.queues[i].head = localEventQueueItem1.next;
/*      */             else {
/* 1142 */               localEventQueueItem2.next = localEventQueueItem1.next;
/*      */             }
/* 1144 */             uncacheEQItem(localEventQueueItem1);
/*      */           } else {
/* 1146 */             localEventQueueItem2 = localEventQueueItem1;
/*      */           }
/* 1148 */           localEventQueueItem1 = localEventQueueItem1.next;
/*      */         }
/* 1150 */         this.queues[i].tail = localEventQueueItem2;
/*      */       }
/*      */     } finally {
/* 1153 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setCurrentEventAndMostRecentTime(AWTEvent paramAWTEvent) {
/* 1158 */     Toolkit.getEventQueue().setCurrentEventAndMostRecentTimeImpl(paramAWTEvent);
/*      */   }
/*      */   private void setCurrentEventAndMostRecentTimeImpl(AWTEvent paramAWTEvent) {
/* 1161 */     this.pushPopLock.lock();
/*      */     try {
/* 1163 */       if (Thread.currentThread() != this.dispatchThread)
/*      */       {
/*      */         return;
/*      */       }
/* 1167 */       this.currentEvent = new WeakReference(paramAWTEvent);
/*      */ 
/* 1177 */       long l = -9223372036854775808L;
/*      */       Object localObject1;
/* 1178 */       if ((paramAWTEvent instanceof InputEvent)) {
/* 1179 */         localObject1 = (InputEvent)paramAWTEvent;
/* 1180 */         l = ((InputEvent)localObject1).getWhen();
/* 1181 */       } else if ((paramAWTEvent instanceof InputMethodEvent)) {
/* 1182 */         localObject1 = (InputMethodEvent)paramAWTEvent;
/* 1183 */         l = ((InputMethodEvent)localObject1).getWhen();
/* 1184 */       } else if ((paramAWTEvent instanceof ActionEvent)) {
/* 1185 */         localObject1 = (ActionEvent)paramAWTEvent;
/* 1186 */         l = ((ActionEvent)localObject1).getWhen();
/* 1187 */       } else if ((paramAWTEvent instanceof InvocationEvent)) {
/* 1188 */         localObject1 = (InvocationEvent)paramAWTEvent;
/* 1189 */         l = ((InvocationEvent)localObject1).getWhen();
/*      */       }
/* 1191 */       this.mostRecentEventTime = Math.max(this.mostRecentEventTime, l);
/*      */     } finally {
/* 1193 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void invokeLater(Runnable paramRunnable)
/*      */   {
/* 1214 */     Toolkit.getEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), paramRunnable));
/*      */   }
/*      */ 
/*      */   public static void invokeAndWait(Runnable paramRunnable)
/*      */     throws InterruptedException, InvocationTargetException
/*      */   {
/* 1244 */     invokeAndWait(Toolkit.getDefaultToolkit(), paramRunnable);
/*      */   }
/*      */ 
/*      */   static void invokeAndWait(Object paramObject, Runnable paramRunnable)
/*      */     throws InterruptedException, InvocationTargetException
/*      */   {
/* 1250 */     if (isDispatchThread()) {
/* 1251 */       throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
/*      */     }
/*      */ 
/* 1255 */     Object local1AWTInvocationLock = new Object()
/*      */     {
/*      */     };
/* 1257 */     InvocationEvent localInvocationEvent = new InvocationEvent(paramObject, paramRunnable, local1AWTInvocationLock, true);
/*      */ 
/* 1260 */     synchronized (local1AWTInvocationLock) {
/* 1261 */       Toolkit.getEventQueue().postEvent(localInvocationEvent);
/* 1262 */       while (!localInvocationEvent.isDispatched()) {
/* 1263 */         local1AWTInvocationLock.wait();
/*      */       }
/*      */     }
/*      */ 
/* 1267 */     ??? = localInvocationEvent.getThrowable();
/* 1268 */     if (??? != null)
/* 1269 */       throw new InvocationTargetException((Throwable)???);
/*      */   }
/*      */ 
/*      */   private void wakeup(boolean paramBoolean)
/*      */   {
/* 1280 */     this.pushPopLock.lock();
/*      */     try {
/* 1282 */       if (this.nextQueue != null)
/*      */       {
/* 1284 */         this.nextQueue.wakeup(paramBoolean);
/* 1285 */       } else if (this.dispatchThread != null)
/* 1286 */         this.pushPopCond.signalAll();
/* 1287 */       else if (!paramBoolean)
/* 1288 */         initDispatchThread();
/*      */     }
/*      */     finally {
/* 1291 */       this.pushPopLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  182 */     AWTAccessor.setEventQueueAccessor(new AWTAccessor.EventQueueAccessor()
/*      */     {
/*      */       public Thread getDispatchThread(EventQueue paramAnonymousEventQueue) {
/*  185 */         return paramAnonymousEventQueue.getDispatchThread();
/*      */       }
/*      */       public boolean isDispatchThreadImpl(EventQueue paramAnonymousEventQueue) {
/*  188 */         return paramAnonymousEventQueue.isDispatchThreadImpl();
/*      */       }
/*      */ 
/*      */       public void removeSourceEvents(EventQueue paramAnonymousEventQueue, Object paramAnonymousObject, boolean paramAnonymousBoolean)
/*      */       {
/*  194 */         paramAnonymousEventQueue.removeSourceEvents(paramAnonymousObject, paramAnonymousBoolean);
/*      */       }
/*      */       public boolean noEvents(EventQueue paramAnonymousEventQueue) {
/*  197 */         return paramAnonymousEventQueue.noEvents();
/*      */       }
/*      */       public void wakeup(EventQueue paramAnonymousEventQueue, boolean paramAnonymousBoolean) {
/*  200 */         paramAnonymousEventQueue.wakeup(paramAnonymousBoolean);
/*      */       }
/*      */ 
/*      */       public void invokeAndWait(Object paramAnonymousObject, Runnable paramAnonymousRunnable) throws InterruptedException, InvocationTargetException
/*      */       {
/*  205 */         EventQueue.invokeAndWait(paramAnonymousObject, paramAnonymousRunnable);
/*      */       }
/*      */     });
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.EventQueue
 * JD-Core Version:    0.6.2
 */