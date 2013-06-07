/*      */ package sun.awt;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.AWTException;
/*      */ import java.awt.Button;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.Checkbox;
/*      */ import java.awt.CheckboxMenuItem;
/*      */ import java.awt.Choice;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.DefaultKeyboardFocusManager;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.Dialog.ModalExclusionType;
/*      */ import java.awt.Dialog.ModalityType;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.FileDialog;
/*      */ import java.awt.FocusTraversalPolicy;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Image;
/*      */ import java.awt.KeyboardFocusManager;
/*      */ import java.awt.Label;
/*      */ import java.awt.Menu;
/*      */ import java.awt.MenuBar;
/*      */ import java.awt.MenuComponent;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.Panel;
/*      */ import java.awt.PopupMenu;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.Robot;
/*      */ import java.awt.ScrollPane;
/*      */ import java.awt.Scrollbar;
/*      */ import java.awt.SystemTray;
/*      */ import java.awt.TextArea;
/*      */ import java.awt.TextField;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.TrayIcon;
/*      */ import java.awt.Window;
/*      */ import java.awt.dnd.DragGestureEvent;
/*      */ import java.awt.dnd.InvalidDnDOperationException;
/*      */ import java.awt.dnd.peer.DragSourceContextPeer;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.DataBuffer;
/*      */ import java.awt.image.DataBufferInt;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.ImageProducer;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.awt.peer.ButtonPeer;
/*      */ import java.awt.peer.CanvasPeer;
/*      */ import java.awt.peer.CheckboxMenuItemPeer;
/*      */ import java.awt.peer.CheckboxPeer;
/*      */ import java.awt.peer.ChoicePeer;
/*      */ import java.awt.peer.DialogPeer;
/*      */ import java.awt.peer.FileDialogPeer;
/*      */ import java.awt.peer.FontPeer;
/*      */ import java.awt.peer.FramePeer;
/*      */ import java.awt.peer.KeyboardFocusManagerPeer;
/*      */ import java.awt.peer.LabelPeer;
/*      */ import java.awt.peer.ListPeer;
/*      */ import java.awt.peer.MenuBarPeer;
/*      */ import java.awt.peer.MenuItemPeer;
/*      */ import java.awt.peer.MenuPeer;
/*      */ import java.awt.peer.MouseInfoPeer;
/*      */ import java.awt.peer.PanelPeer;
/*      */ import java.awt.peer.PopupMenuPeer;
/*      */ import java.awt.peer.RobotPeer;
/*      */ import java.awt.peer.ScrollPanePeer;
/*      */ import java.awt.peer.ScrollbarPeer;
/*      */ import java.awt.peer.SystemTrayPeer;
/*      */ import java.awt.peer.TextAreaPeer;
/*      */ import java.awt.peer.TextFieldPeer;
/*      */ import java.awt.peer.TrayIconPeer;
/*      */ import java.awt.peer.WindowPeer;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.net.SocketPermission;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permission;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.locks.Condition;
/*      */ import java.util.concurrent.locks.Lock;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import sun.awt.im.InputContext;
/*      */ import sun.awt.im.SimpleInputMethodWindow;
/*      */ import sun.awt.image.ByteArrayImageSource;
/*      */ import sun.awt.image.FileImageSource;
/*      */ import sun.awt.image.ImageRepresentation;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.awt.image.URLImageSource;
/*      */ import sun.font.FontDesignMetrics;
/*      */ import sun.misc.SoftCache;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.util.SecurityConstants.AWT;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public abstract class SunToolkit extends Toolkit
/*      */   implements WindowClosingSupport, WindowClosingListener, ComponentFactory, InputMethodSupport, KeyboardFocusManagerPeerProvider
/*      */ {
/*      */   private static final PlatformLogger log;
/*      */   public static final int GRAB_EVENT_MASK = -2147483648;
/*      */   private static final String POST_EVENT_QUEUE_KEY = "PostEventQueue";
/*   86 */   protected static int numberOfButtons = 0;
/*      */   public static final int MAX_BUTTONS_SUPPORTED = 20;
/*  234 */   private static final ReentrantLock AWT_LOCK = new ReentrantLock();
/*  235 */   private static final Condition AWT_LOCK_COND = AWT_LOCK.newCondition();
/*      */ 
/*  327 */   private static final Map appContextMap = Collections.synchronizedMap(new WeakHashMap());
/*      */ 
/*  508 */   protected static final Lock flushLock = new ReentrantLock();
/*  509 */   private static boolean isFlushingPendingEvents = false;
/*      */ 
/*  717 */   static final SoftCache imgCache = new SoftCache();
/*      */ 
/* 1106 */   private static Locale startupLocale = null;
/*      */ 
/* 1147 */   private static String dataTransfererClassName = null;
/*      */ 
/* 1161 */   private transient WindowClosingListener windowClosingListener = null;
/*      */ 
/* 1196 */   private static DefaultMouseInfoPeer mPeer = null;
/*      */ 
/* 1238 */   private static Dialog.ModalExclusionType DEFAULT_MODAL_EXCLUSION_TYPE = null;
/*      */ 
/* 1350 */   private ModalityListenerList modalityListeners = new ModalityListenerList();
/*      */   public static final int DEFAULT_WAIT_TIME = 10000;
/*      */   private static final int MAX_ITERS = 20;
/*      */   private static final int MIN_ITERS = 0;
/*      */   private static final int MINIMAL_EDELAY = 0;
/* 1562 */   private boolean eventDispatched = false;
/* 1563 */   private boolean queueEmpty = false;
/* 1564 */   private final Object waitLock = "Wait Lock";
/*      */   private static boolean checkedSystemAAFontSettings;
/*      */   private static boolean useSystemAAFontSettings;
/* 1672 */   private static boolean lastExtraCondition = true;
/*      */   private static RenderingHints desktopFontHints;
/*      */   public static final String DESKTOPFONTHINTS = "awt.font.desktophints";
/* 1850 */   private static Boolean sunAwtDisableMixing = null;
/*      */ 
/* 1873 */   private static final Object DEACTIVATION_TIMES_MAP_KEY = new Object();
/*      */ 
/*      */   private static void initEQ(AppContext paramAppContext)
/*      */   {
/*  103 */     String str = System.getProperty("AWT.EventQueueClass", "java.awt.EventQueue");
/*      */     EventQueue localEventQueue;
/*      */     try
/*      */     {
/*  107 */       localEventQueue = (EventQueue)Class.forName(str).newInstance();
/*      */     } catch (Exception localException) {
/*  109 */       localException.printStackTrace();
/*  110 */       System.err.println("Failed loading " + str + ": " + localException);
/*  111 */       localEventQueue = new EventQueue();
/*      */     }
/*  113 */     paramAppContext.put(AppContext.EVENT_QUEUE_KEY, localEventQueue);
/*      */ 
/*  115 */     PostEventQueue localPostEventQueue = new PostEventQueue(localEventQueue);
/*  116 */     paramAppContext.put("PostEventQueue", localPostEventQueue);
/*      */   }
/*      */ 
/*      */   public SunToolkit()
/*      */   {
/*  121 */     initEQ(AppContext.getMainAppContext());
/*      */   }
/*      */ 
/*      */   public boolean useBufferPerWindow() {
/*  125 */     return false;
/*      */   }
/*      */ 
/*      */   public abstract WindowPeer createWindow(Window paramWindow)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract FramePeer createFrame(Frame paramFrame)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract DialogPeer createDialog(Dialog paramDialog)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract ButtonPeer createButton(Button paramButton)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract TextFieldPeer createTextField(TextField paramTextField)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract ChoicePeer createChoice(Choice paramChoice)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract LabelPeer createLabel(Label paramLabel)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract ListPeer createList(java.awt.List paramList)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract CheckboxPeer createCheckbox(Checkbox paramCheckbox)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract ScrollbarPeer createScrollbar(Scrollbar paramScrollbar)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract ScrollPanePeer createScrollPane(ScrollPane paramScrollPane)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract TextAreaPeer createTextArea(TextArea paramTextArea)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract FileDialogPeer createFileDialog(FileDialog paramFileDialog)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract MenuBarPeer createMenuBar(MenuBar paramMenuBar)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract MenuPeer createMenu(Menu paramMenu)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract MenuItemPeer createMenuItem(MenuItem paramMenuItem)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*      */     throws InvalidDnDOperationException;
/*      */ 
/*      */   public abstract TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon)
/*      */     throws HeadlessException, AWTException;
/*      */ 
/*      */   public abstract SystemTrayPeer createSystemTray(SystemTray paramSystemTray);
/*      */ 
/*      */   public abstract boolean isTraySupported();
/*      */ 
/*      */   public abstract FontPeer getFontPeer(String paramString, int paramInt);
/*      */ 
/*      */   public abstract RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice)
/*      */     throws AWTException;
/*      */ 
/*      */   public abstract KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager)
/*      */     throws HeadlessException;
/*      */ 
/*      */   public static final void awtLock()
/*      */   {
/*  238 */     AWT_LOCK.lock();
/*      */   }
/*      */ 
/*      */   public static final boolean awtTryLock() {
/*  242 */     return AWT_LOCK.tryLock();
/*      */   }
/*      */ 
/*      */   public static final void awtUnlock() {
/*  246 */     AWT_LOCK.unlock();
/*      */   }
/*      */ 
/*      */   public static final void awtLockWait()
/*      */     throws InterruptedException
/*      */   {
/*  252 */     AWT_LOCK_COND.await();
/*      */   }
/*      */ 
/*      */   public static final void awtLockWait(long paramLong)
/*      */     throws InterruptedException
/*      */   {
/*  258 */     AWT_LOCK_COND.await(paramLong, TimeUnit.MILLISECONDS);
/*      */   }
/*      */ 
/*      */   public static final void awtLockNotify() {
/*  262 */     AWT_LOCK_COND.signal();
/*      */   }
/*      */ 
/*      */   public static final void awtLockNotifyAll() {
/*  266 */     AWT_LOCK_COND.signalAll();
/*      */   }
/*      */ 
/*      */   public static final boolean isAWTLockHeldByCurrentThread() {
/*  270 */     return AWT_LOCK.isHeldByCurrentThread();
/*      */   }
/*      */ 
/*      */   public static AppContext createNewAppContext()
/*      */   {
/*  279 */     ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/*      */ 
/*  283 */     AppContext localAppContext = new AppContext(localThreadGroup);
/*      */ 
/*  285 */     initEQ(localAppContext);
/*      */ 
/*  287 */     return localAppContext;
/*      */   }
/*      */ 
/*      */   static void wakeupEventQueue(EventQueue paramEventQueue, boolean paramBoolean) {
/*  291 */     AWTAccessor.getEventQueueAccessor().wakeup(paramEventQueue, paramBoolean);
/*      */   }
/*      */ 
/*      */   protected static Object targetToPeer(Object paramObject)
/*      */   {
/*  303 */     if ((paramObject != null) && (!GraphicsEnvironment.isHeadless())) {
/*  304 */       return AWTAutoShutdown.getInstance().getPeer(paramObject);
/*      */     }
/*  306 */     return null;
/*      */   }
/*      */ 
/*      */   protected static void targetCreatedPeer(Object paramObject1, Object paramObject2) {
/*  310 */     if ((paramObject1 != null) && (paramObject2 != null) && (!GraphicsEnvironment.isHeadless()))
/*      */     {
/*  313 */       AWTAutoShutdown.getInstance().registerPeer(paramObject1, paramObject2);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void targetDisposedPeer(Object paramObject1, Object paramObject2) {
/*  318 */     if ((paramObject1 != null) && (paramObject2 != null) && (!GraphicsEnvironment.isHeadless()))
/*      */     {
/*  321 */       AWTAutoShutdown.getInstance().unregisterPeer(paramObject1, paramObject2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean setAppContext(Object paramObject, AppContext paramAppContext)
/*      */   {
/*  336 */     if ((paramObject instanceof Component)) {
/*  337 */       AWTAccessor.getComponentAccessor().setAppContext((Component)paramObject, paramAppContext);
/*      */     }
/*  339 */     else if ((paramObject instanceof MenuComponent)) {
/*  340 */       AWTAccessor.getMenuComponentAccessor().setAppContext((MenuComponent)paramObject, paramAppContext);
/*      */     }
/*      */     else {
/*  343 */       return false;
/*      */     }
/*  345 */     return true;
/*      */   }
/*      */ 
/*      */   private static AppContext getAppContext(Object paramObject)
/*      */   {
/*  353 */     if ((paramObject instanceof Component)) {
/*  354 */       return AWTAccessor.getComponentAccessor().getAppContext((Component)paramObject);
/*      */     }
/*  356 */     if ((paramObject instanceof MenuComponent)) {
/*  357 */       return AWTAccessor.getMenuComponentAccessor().getAppContext((MenuComponent)paramObject);
/*      */     }
/*      */ 
/*  360 */     return null;
/*      */   }
/*      */ 
/*      */   public static AppContext targetToAppContext(Object paramObject)
/*      */   {
/*  371 */     if ((paramObject == null) || (GraphicsEnvironment.isHeadless())) {
/*  372 */       return null;
/*      */     }
/*  374 */     AppContext localAppContext = getAppContext(paramObject);
/*  375 */     if (localAppContext == null)
/*      */     {
/*  378 */       localAppContext = (AppContext)appContextMap.get(paramObject);
/*      */     }
/*  380 */     return localAppContext;
/*      */   }
/*      */ 
/*      */   public static void setLWRequestStatus(Window paramWindow, boolean paramBoolean)
/*      */   {
/*  409 */     AWTAccessor.getWindowAccessor().setLWRequestStatus(paramWindow, paramBoolean);
/*      */   }
/*      */ 
/*      */   public static void checkAndSetPolicy(Container paramContainer) {
/*  413 */     FocusTraversalPolicy localFocusTraversalPolicy = KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
/*      */ 
/*  417 */     paramContainer.setFocusTraversalPolicy(localFocusTraversalPolicy);
/*      */   }
/*      */ 
/*      */   private static FocusTraversalPolicy createLayoutPolicy() {
/*  421 */     FocusTraversalPolicy localFocusTraversalPolicy = null;
/*      */     try {
/*  423 */       Class localClass = Class.forName("javax.swing.LayoutFocusTraversalPolicy");
/*      */ 
/*  425 */       localFocusTraversalPolicy = (FocusTraversalPolicy)localClass.newInstance();
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException) {
/*  428 */       if (!$assertionsDisabled) throw new AssertionError(); 
/*      */     }
/*      */     catch (InstantiationException localInstantiationException)
/*      */     {
/*  431 */       if (!$assertionsDisabled) throw new AssertionError(); 
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException)
/*      */     {
/*  434 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */     }
/*      */ 
/*  437 */     return localFocusTraversalPolicy;
/*      */   }
/*      */ 
/*      */   public static void insertTargetMapping(Object paramObject, AppContext paramAppContext)
/*      */   {
/*  445 */     if ((!GraphicsEnvironment.isHeadless()) && 
/*  446 */       (!setAppContext(paramObject, paramAppContext)))
/*      */     {
/*  449 */       appContextMap.put(paramObject, paramAppContext);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void postEvent(AppContext paramAppContext, AWTEvent paramAWTEvent)
/*      */   {
/*  462 */     if (paramAWTEvent == null) {
/*  463 */       throw new NullPointerException();
/*      */     }
/*      */ 
/*  466 */     AWTAccessor.SequencedEventAccessor localSequencedEventAccessor = AWTAccessor.getSequencedEventAccessor();
/*  467 */     if ((localSequencedEventAccessor != null) && (localSequencedEventAccessor.isSequencedEvent(paramAWTEvent))) {
/*  468 */       localObject1 = localSequencedEventAccessor.getNested(paramAWTEvent);
/*  469 */       if ((((AWTEvent)localObject1).getID() == 208) && ((localObject1 instanceof TimedWindowEvent)))
/*      */       {
/*  472 */         localObject2 = (TimedWindowEvent)localObject1;
/*  473 */         ((SunToolkit)Toolkit.getDefaultToolkit()).setWindowDeactivationTime((Window)((TimedWindowEvent)localObject2).getSource(), ((TimedWindowEvent)localObject2).getWhen());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  483 */     setSystemGenerated(paramAWTEvent);
/*  484 */     Object localObject1 = targetToAppContext(paramAWTEvent.getSource());
/*  485 */     if ((localObject1 != null) && (!localObject1.equals(paramAppContext))) {
/*  486 */       log.fine("Event posted on wrong app context : " + paramAWTEvent);
/*      */     }
/*  488 */     Object localObject2 = (PostEventQueue)paramAppContext.get("PostEventQueue");
/*      */ 
/*  490 */     if (localObject2 != null)
/*  491 */       ((PostEventQueue)localObject2).postEvent(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   public static void postPriorityEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  499 */     PeerEvent localPeerEvent = new PeerEvent(Toolkit.getDefaultToolkit(), new Runnable() {
/*      */       public void run() {
/*  501 */         AWTAccessor.getAWTEventAccessor().setPosted(this.val$e);
/*  502 */         ((Component)this.val$e.getSource()).dispatchEvent(this.val$e);
/*      */       }
/*      */     }
/*      */     , 2L);
/*      */ 
/*  505 */     postEvent(targetToAppContext(paramAWTEvent.getSource()), localPeerEvent);
/*      */   }
/*      */ 
/*      */   public static void flushPendingEvents()
/*      */   {
/*  516 */     flushLock.lock();
/*      */     try
/*      */     {
/*  519 */       if (!isFlushingPendingEvents) {
/*  520 */         isFlushingPendingEvents = true;
/*      */         try {
/*  522 */           AppContext localAppContext = AppContext.getAppContext();
/*  523 */           PostEventQueue localPostEventQueue = (PostEventQueue)localAppContext.get("PostEventQueue");
/*      */ 
/*  525 */           if (localPostEventQueue != null)
/*  526 */             localPostEventQueue.flush();
/*      */         }
/*      */         finally
/*      */         {
/*  530 */           isFlushingPendingEvents = false;
/*      */         }
/*      */       }
/*      */     } finally {
/*  534 */       flushLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean isPostEventQueueEmpty() {
/*  539 */     AppContext localAppContext = AppContext.getAppContext();
/*  540 */     PostEventQueue localPostEventQueue = (PostEventQueue)localAppContext.get("PostEventQueue");
/*      */ 
/*  542 */     if (localPostEventQueue != null) {
/*  543 */       return localPostEventQueue.noEvents();
/*      */     }
/*  545 */     return true;
/*      */   }
/*      */ 
/*      */   public static void executeOnEventHandlerThread(Object paramObject, Runnable paramRunnable)
/*      */   {
/*  556 */     executeOnEventHandlerThread(new PeerEvent(paramObject, paramRunnable, 1L));
/*      */   }
/*      */ 
/*      */   public static void executeOnEventHandlerThread(Object paramObject, Runnable paramRunnable, long paramLong)
/*      */   {
/*  566 */     executeOnEventHandlerThread(new PeerEvent(paramObject, paramRunnable, 1L) {
/*      */       public long getWhen() {
/*  568 */         return this.val$when;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static void executeOnEventHandlerThread(PeerEvent paramPeerEvent)
/*      */   {
/*  579 */     postEvent(targetToAppContext(paramPeerEvent.getSource()), paramPeerEvent);
/*      */   }
/*      */ 
/*      */   public static void invokeLaterOnAppContext(AppContext paramAppContext, Runnable paramRunnable)
/*      */   {
/*  593 */     postEvent(paramAppContext, new PeerEvent(Toolkit.getDefaultToolkit(), paramRunnable, 1L));
/*      */   }
/*      */ 
/*      */   public static void executeOnEDTAndWait(Object paramObject, Runnable paramRunnable)
/*      */     throws InterruptedException, InvocationTargetException
/*      */   {
/*  606 */     if (EventQueue.isDispatchThread()) {
/*  607 */       throw new Error("Cannot call executeOnEDTAndWait from any event dispatcher thread");
/*      */     }
/*      */ 
/*  611 */     Object local1AWTInvocationLock = new Object()
/*      */     {
/*      */     };
/*  613 */     PeerEvent localPeerEvent = new PeerEvent(paramObject, paramRunnable, local1AWTInvocationLock, true, 1L);
/*      */ 
/*  615 */     synchronized (local1AWTInvocationLock) {
/*  616 */       executeOnEventHandlerThread(localPeerEvent);
/*  617 */       while (!localPeerEvent.isDispatched()) {
/*  618 */         local1AWTInvocationLock.wait();
/*      */       }
/*      */     }
/*      */ 
/*  622 */     ??? = localPeerEvent.getThrowable();
/*  623 */     if (??? != null)
/*  624 */       throw new InvocationTargetException((Throwable)???);
/*      */   }
/*      */ 
/*      */   public static boolean isDispatchThreadForAppContext(Object paramObject)
/*      */   {
/*  635 */     AppContext localAppContext = targetToAppContext(paramObject);
/*  636 */     EventQueue localEventQueue = (EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY);
/*      */ 
/*  638 */     AWTAccessor.EventQueueAccessor localEventQueueAccessor = AWTAccessor.getEventQueueAccessor();
/*  639 */     return localEventQueueAccessor.isDispatchThreadImpl(localEventQueue);
/*      */   }
/*      */ 
/*      */   public Dimension getScreenSize() {
/*  643 */     return new Dimension(getScreenWidth(), getScreenHeight());
/*      */   }
/*      */   protected abstract int getScreenWidth();
/*      */ 
/*      */   protected abstract int getScreenHeight();
/*      */ 
/*  649 */   public FontMetrics getFontMetrics(Font paramFont) { return FontDesignMetrics.getMetrics(paramFont); }
/*      */ 
/*      */   public String[] getFontList()
/*      */   {
/*  653 */     String[] arrayOfString = { "Dialog", "SansSerif", "Serif", "Monospaced", "DialogInput" };
/*      */ 
/*  661 */     return arrayOfString;
/*      */   }
/*      */ 
/*      */   public PanelPeer createPanel(Panel paramPanel) {
/*  665 */     return (PanelPeer)createComponent(paramPanel);
/*      */   }
/*      */ 
/*      */   public CanvasPeer createCanvas(Canvas paramCanvas) {
/*  669 */     return (CanvasPeer)createComponent(paramCanvas);
/*      */   }
/*      */ 
/*      */   public void disableBackgroundErase(Canvas paramCanvas)
/*      */   {
/*  680 */     disableBackgroundEraseImpl(paramCanvas);
/*      */   }
/*      */ 
/*      */   public void disableBackgroundErase(Component paramComponent)
/*      */   {
/*  693 */     disableBackgroundEraseImpl(paramComponent);
/*      */   }
/*      */ 
/*      */   private void disableBackgroundEraseImpl(Component paramComponent) {
/*  697 */     AWTAccessor.getComponentAccessor().setBackgroundEraseDisabled(paramComponent, true);
/*      */   }
/*      */ 
/*      */   public static boolean getSunAwtNoerasebackground()
/*      */   {
/*  705 */     return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.noerasebackground"))).booleanValue();
/*      */   }
/*      */ 
/*      */   public static boolean getSunAwtErasebackgroundonresize()
/*      */   {
/*  713 */     return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.erasebackgroundonresize"))).booleanValue();
/*      */   }
/*      */ 
/*      */   static Image getImageFromHash(Toolkit paramToolkit, URL paramURL)
/*      */   {
/*  720 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  721 */     if (localSecurityManager != null) {
/*      */       try {
/*  723 */         Permission localPermission = paramURL.openConnection().getPermission();
/*      */ 
/*  725 */         if (localPermission != null)
/*      */           try {
/*  727 */             localSecurityManager.checkPermission(localPermission);
/*      */           }
/*      */           catch (SecurityException localSecurityException)
/*      */           {
/*  731 */             if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1))
/*      */             {
/*  733 */               localSecurityManager.checkRead(localPermission.getName());
/*  734 */             } else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1))
/*      */             {
/*  737 */               localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*      */             }
/*  739 */             else throw localSecurityException;
/*      */           }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  744 */         localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*      */       }
/*      */     }
/*  747 */     synchronized (imgCache) {
/*  748 */       Image localImage = (Image)imgCache.get(paramURL);
/*  749 */       if (localImage == null)
/*      */         try {
/*  751 */           localImage = paramToolkit.createImage(new URLImageSource(paramURL));
/*  752 */           imgCache.put(paramURL, localImage);
/*      */         }
/*      */         catch (Exception localException) {
/*      */         }
/*  756 */       return localImage;
/*      */     }
/*      */   }
/*      */ 
/*      */   static Image getImageFromHash(Toolkit paramToolkit, String paramString)
/*      */   {
/*  762 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  763 */     if (localSecurityManager != null) {
/*  764 */       localSecurityManager.checkRead(paramString);
/*      */     }
/*  766 */     synchronized (imgCache) {
/*  767 */       Image localImage = (Image)imgCache.get(paramString);
/*  768 */       if (localImage == null)
/*      */         try {
/*  770 */           localImage = paramToolkit.createImage(new FileImageSource(paramString));
/*  771 */           imgCache.put(paramString, localImage);
/*      */         }
/*      */         catch (Exception localException) {
/*      */         }
/*  775 */       return localImage;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Image getImage(String paramString) {
/*  780 */     return getImageFromHash(this, paramString);
/*      */   }
/*      */ 
/*      */   public Image getImage(URL paramURL) {
/*  784 */     return getImageFromHash(this, paramURL);
/*      */   }
/*      */ 
/*      */   public Image createImage(String paramString) {
/*  788 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  789 */     if (localSecurityManager != null) {
/*  790 */       localSecurityManager.checkRead(paramString);
/*      */     }
/*  792 */     return createImage(new FileImageSource(paramString));
/*      */   }
/*      */ 
/*      */   public Image createImage(URL paramURL) {
/*  796 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  797 */     if (localSecurityManager != null) {
/*      */       try {
/*  799 */         Permission localPermission = paramURL.openConnection().getPermission();
/*      */ 
/*  801 */         if (localPermission != null)
/*      */           try {
/*  803 */             localSecurityManager.checkPermission(localPermission);
/*      */           }
/*      */           catch (SecurityException localSecurityException)
/*      */           {
/*  807 */             if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1))
/*      */             {
/*  809 */               localSecurityManager.checkRead(localPermission.getName());
/*  810 */             } else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1))
/*      */             {
/*  813 */               localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*      */             }
/*  815 */             else throw localSecurityException;
/*      */           }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  820 */         localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*      */       }
/*      */     }
/*  823 */     return createImage(new URLImageSource(paramURL));
/*      */   }
/*      */ 
/*      */   public Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/*  827 */     return createImage(new ByteArrayImageSource(paramArrayOfByte, paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */   public Image createImage(ImageProducer paramImageProducer) {
/*  831 */     return new ToolkitImage(paramImageProducer);
/*      */   }
/*      */ 
/*      */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
/*  835 */     if (!(paramImage instanceof ToolkitImage)) {
/*  836 */       return 32;
/*      */     }
/*      */ 
/*  839 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/*      */     int i;
/*  841 */     if ((paramInt1 == 0) || (paramInt2 == 0))
/*  842 */       i = 32;
/*      */     else {
/*  844 */       i = localToolkitImage.getImageRep().check(paramImageObserver);
/*      */     }
/*  846 */     return localToolkitImage.check(paramImageObserver) | i;
/*      */   }
/*      */ 
/*      */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
/*  850 */     if ((paramInt1 == 0) || (paramInt2 == 0)) {
/*  851 */       return true;
/*      */     }
/*      */ 
/*  855 */     if (!(paramImage instanceof ToolkitImage)) {
/*  856 */       return true;
/*      */     }
/*      */ 
/*  859 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/*  860 */     if (localToolkitImage.hasError()) {
/*  861 */       if (paramImageObserver != null) {
/*  862 */         paramImageObserver.imageUpdate(paramImage, 192, -1, -1, -1, -1);
/*      */       }
/*      */ 
/*  865 */       return false;
/*      */     }
/*  867 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/*  868 */     return localImageRepresentation.prepare(paramImageObserver);
/*      */   }
/*      */ 
/*      */   public static BufferedImage getScaledIconImage(java.util.List<Image> paramList, int paramInt1, int paramInt2)
/*      */   {
/*  876 */     if ((paramInt1 == 0) || (paramInt2 == 0)) {
/*  877 */       return null;
/*      */     }
/*  879 */     Object localObject1 = null;
/*  880 */     int i = 0;
/*  881 */     int j = 0;
/*  882 */     double d1 = 3.0D;
/*  883 */     double d2 = 0.0D;
/*  884 */     for (Object localObject2 = paramList.iterator(); ((Iterator)localObject2).hasNext(); )
/*      */     {
/*  890 */       localObject3 = (Image)((Iterator)localObject2).next();
/*  891 */       if (localObject3 == null) {
/*  892 */         if (log.isLoggable(400)) {
/*  893 */           log.finer("SunToolkit.getScaledIconImage: Skipping the image passed into Java because it's null.");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  898 */         if ((localObject3 instanceof ToolkitImage)) {
/*  899 */           ImageRepresentation localImageRepresentation = ((ToolkitImage)localObject3).getImageRep();
/*  900 */           localImageRepresentation.reconstruct(32);
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  905 */           k = ((Image)localObject3).getWidth(null);
/*  906 */           m = ((Image)localObject3).getHeight(null);
/*      */         } catch (Exception localException) {
/*  908 */           if (log.isLoggable(400)) {
/*  909 */             log.finer("SunToolkit.getScaledIconImage: Perhaps the image passed into Java is broken. Skipping this icon.");
/*      */           }
/*      */         }
/*  912 */         continue;
/*      */ 
/*  914 */         if ((k > 0) && (m > 0))
/*      */         {
/*  916 */           double d3 = Math.min(paramInt1 / k, paramInt2 / m);
/*      */ 
/*  920 */           int n = 0;
/*  921 */           int i1 = 0;
/*  922 */           double d4 = 1.0D;
/*  923 */           if (d3 >= 2.0D)
/*      */           {
/*  926 */             d3 = Math.floor(d3);
/*  927 */             n = k * (int)d3;
/*  928 */             i1 = m * (int)d3;
/*  929 */             d4 = 1.0D - 0.5D / d3;
/*  930 */           } else if (d3 >= 1.0D)
/*      */           {
/*  932 */             d3 = 1.0D;
/*  933 */             n = k;
/*  934 */             i1 = m;
/*  935 */             d4 = 0.0D;
/*  936 */           } else if (d3 >= 0.75D)
/*      */           {
/*  938 */             d3 = 0.75D;
/*  939 */             n = k * 3 / 4;
/*  940 */             i1 = m * 3 / 4;
/*  941 */             d4 = 0.3D;
/*  942 */           } else if (d3 >= 0.6666D)
/*      */           {
/*  944 */             d3 = 0.6666D;
/*  945 */             n = k * 2 / 3;
/*  946 */             i1 = m * 2 / 3;
/*  947 */             d4 = 0.33D;
/*      */           }
/*      */           else
/*      */           {
/*  952 */             d5 = Math.ceil(1.0D / d3);
/*  953 */             d3 = 1.0D / d5;
/*  954 */             n = (int)Math.round(k / d5);
/*  955 */             i1 = (int)Math.round(m / d5);
/*  956 */             d4 = 1.0D - 1.0D / d5;
/*      */           }
/*  958 */           double d5 = (paramInt1 - n) / paramInt1 + (paramInt2 - i1) / paramInt2 + d4;
/*      */ 
/*  961 */           if (d5 < d1) {
/*  962 */             d1 = d5;
/*  963 */             d2 = d3;
/*  964 */             localObject1 = localObject3;
/*  965 */             i = n;
/*  966 */             j = i1;
/*      */           }
/*  968 */           if (d5 == 0.0D)
/*      */             break;
/*      */         }
/*      */       }
/*      */     }
/*      */     int k;
/*      */     int m;
/*  971 */     if (localObject1 == null)
/*      */     {
/*  973 */       return null;
/*      */     }
/*  975 */     localObject2 = new BufferedImage(paramInt1, paramInt2, 2);
/*      */ 
/*  977 */     Object localObject3 = ((BufferedImage)localObject2).createGraphics();
/*  978 */     ((Graphics2D)localObject3).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/*      */     try
/*      */     {
/*  981 */       k = (paramInt1 - i) / 2;
/*  982 */       m = (paramInt2 - j) / 2;
/*  983 */       if (log.isLoggable(400)) {
/*  984 */         log.finer("WWindowPeer.getScaledIconData() result : w : " + paramInt1 + " h : " + paramInt2 + " iW : " + localObject1.getWidth(null) + " iH : " + localObject1.getHeight(null) + " sim : " + d1 + " sf : " + d2 + " adjW : " + i + " adjH : " + j + " x : " + k + " y : " + m);
/*      */       }
/*      */ 
/*  991 */       ((Graphics2D)localObject3).drawImage(localObject1, k, m, i, j, null);
/*      */     } finally {
/*  993 */       ((Graphics2D)localObject3).dispose();
/*      */     }
/*  995 */     return localObject2;
/*      */   }
/*      */ 
/*      */   public static DataBufferInt getScaledIconData(java.util.List<Image> paramList, int paramInt1, int paramInt2) {
/*  999 */     BufferedImage localBufferedImage = getScaledIconImage(paramList, paramInt1, paramInt2);
/* 1000 */     if (localBufferedImage == null) {
/* 1001 */       if (log.isLoggable(400)) {
/* 1002 */         log.finer("SunToolkit.getScaledIconData: Perhaps the image passed into Java is broken. Skipping this icon.");
/*      */       }
/*      */ 
/* 1005 */       return null;
/*      */     }
/* 1007 */     WritableRaster localWritableRaster = localBufferedImage.getRaster();
/* 1008 */     DataBuffer localDataBuffer = localWritableRaster.getDataBuffer();
/* 1009 */     return (DataBufferInt)localDataBuffer;
/*      */   }
/*      */ 
/*      */   protected EventQueue getSystemEventQueueImpl() {
/* 1013 */     return getSystemEventQueueImplPP();
/*      */   }
/*      */ 
/*      */   static EventQueue getSystemEventQueueImplPP()
/*      */   {
/* 1018 */     return getSystemEventQueueImplPP(AppContext.getAppContext());
/*      */   }
/*      */ 
/*      */   public static EventQueue getSystemEventQueueImplPP(AppContext paramAppContext) {
/* 1022 */     EventQueue localEventQueue = (EventQueue)paramAppContext.get(AppContext.EVENT_QUEUE_KEY);
/*      */ 
/* 1024 */     return localEventQueue;
/*      */   }
/*      */ 
/*      */   public static Container getNativeContainer(Component paramComponent)
/*      */   {
/* 1032 */     return Toolkit.getNativeContainer(paramComponent);
/*      */   }
/*      */ 
/*      */   public static Component getHeavyweightComponent(Component paramComponent)
/*      */   {
/* 1041 */     while ((paramComponent != null) && (AWTAccessor.getComponentAccessor().isLightweight(paramComponent))) {
/* 1042 */       paramComponent = AWTAccessor.getComponentAccessor().getParent(paramComponent);
/*      */     }
/* 1044 */     return paramComponent;
/*      */   }
/*      */ 
/*      */   public int getFocusAcceleratorKeyMask()
/*      */   {
/* 1051 */     return 8;
/*      */   }
/*      */ 
/*      */   public boolean isPrintableCharacterModifiersMask(int paramInt)
/*      */   {
/* 1061 */     return (paramInt & 0x8) == (paramInt & 0x2);
/*      */   }
/*      */ 
/*      */   public boolean canPopupOverlapTaskBar()
/*      */   {
/* 1070 */     boolean bool = true;
/*      */     try {
/* 1072 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 1073 */       if (localSecurityManager != null) {
/* 1074 */         localSecurityManager.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
/*      */       }
/*      */     }
/*      */     catch (SecurityException localSecurityException)
/*      */     {
/* 1079 */       bool = false;
/*      */     }
/* 1081 */     return bool;
/*      */   }
/*      */ 
/*      */   public Window createInputMethodWindow(String paramString, InputContext paramInputContext)
/*      */   {
/* 1095 */     return new SimpleInputMethodWindow(paramString, paramInputContext);
/*      */   }
/*      */ 
/*      */   public boolean enableInputMethodsForTextComponent()
/*      */   {
/* 1103 */     return false;
/*      */   }
/*      */ 
/*      */   public static Locale getStartupLocale()
/*      */   {
/* 1112 */     if (startupLocale == null)
/*      */     {
/* 1114 */       String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.language", "en"));
/*      */ 
/* 1117 */       String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.region"));
/*      */       String str3;
/*      */       String str4;
/* 1119 */       if (str2 != null)
/*      */       {
/* 1121 */         int i = str2.indexOf('_');
/* 1122 */         if (i >= 0) {
/* 1123 */           str3 = str2.substring(0, i);
/* 1124 */           str4 = str2.substring(i + 1);
/*      */         } else {
/* 1126 */           str3 = str2;
/* 1127 */           str4 = "";
/*      */         }
/*      */       } else {
/* 1130 */         str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.country", ""));
/*      */ 
/* 1132 */         str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.variant", ""));
/*      */       }
/*      */ 
/* 1135 */       startupLocale = new Locale(str1, str3, str4);
/*      */     }
/* 1137 */     return startupLocale;
/*      */   }
/*      */ 
/*      */   public Locale getDefaultKeyboardLocale()
/*      */   {
/* 1144 */     return getStartupLocale();
/*      */   }
/*      */ 
/*      */   protected static void setDataTransfererClassName(String paramString)
/*      */   {
/* 1150 */     dataTransfererClassName = paramString;
/*      */   }
/*      */ 
/*      */   public static String getDataTransfererClassName() {
/* 1154 */     if (dataTransfererClassName == null) {
/* 1155 */       Toolkit.getDefaultToolkit();
/*      */     }
/* 1157 */     return dataTransfererClassName;
/*      */   }
/*      */ 
/*      */   public WindowClosingListener getWindowClosingListener()
/*      */   {
/* 1166 */     return this.windowClosingListener;
/*      */   }
/*      */ 
/*      */   public void setWindowClosingListener(WindowClosingListener paramWindowClosingListener)
/*      */   {
/* 1172 */     this.windowClosingListener = paramWindowClosingListener;
/*      */   }
/*      */ 
/*      */   public RuntimeException windowClosingNotify(WindowEvent paramWindowEvent)
/*      */   {
/* 1179 */     if (this.windowClosingListener != null) {
/* 1180 */       return this.windowClosingListener.windowClosingNotify(paramWindowEvent);
/*      */     }
/* 1182 */     return null;
/*      */   }
/*      */ 
/*      */   public RuntimeException windowClosingDelivered(WindowEvent paramWindowEvent)
/*      */   {
/* 1189 */     if (this.windowClosingListener != null) {
/* 1190 */       return this.windowClosingListener.windowClosingDelivered(paramWindowEvent);
/*      */     }
/* 1192 */     return null;
/*      */   }
/*      */ 
/*      */   protected synchronized MouseInfoPeer getMouseInfoPeer()
/*      */   {
/* 1199 */     if (mPeer == null) {
/* 1200 */       mPeer = new DefaultMouseInfoPeer();
/*      */     }
/* 1202 */     return mPeer;
/*      */   }
/*      */ 
/*      */   public static boolean needsXEmbed()
/*      */   {
/* 1212 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.awt.noxembed", "false"));
/*      */ 
/* 1214 */     if ("true".equals(str)) {
/* 1215 */       return false;
/*      */     }
/*      */ 
/* 1218 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1219 */     if ((localToolkit instanceof SunToolkit))
/*      */     {
/* 1222 */       return ((SunToolkit)localToolkit).needsXEmbedImpl();
/*      */     }
/*      */ 
/* 1225 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean needsXEmbedImpl()
/*      */   {
/* 1235 */     return false;
/*      */   }
/*      */ 
/*      */   protected final boolean isXEmbedServerRequested()
/*      */   {
/* 1246 */     return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.xembedserver"))).booleanValue();
/*      */   }
/*      */ 
/*      */   public static boolean isModalExcludedSupported()
/*      */   {
/* 1263 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1264 */     return localToolkit.isModalExclusionTypeSupported(DEFAULT_MODAL_EXCLUSION_TYPE);
/*      */   }
/*      */ 
/*      */   protected boolean isModalExcludedSupportedImpl()
/*      */   {
/* 1276 */     return false;
/*      */   }
/*      */ 
/*      */   public static void setModalExcluded(Window paramWindow)
/*      */   {
/* 1297 */     if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
/* 1298 */       DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
/*      */     }
/* 1300 */     paramWindow.setModalExclusionType(DEFAULT_MODAL_EXCLUSION_TYPE);
/*      */   }
/*      */ 
/*      */   public static boolean isModalExcluded(Window paramWindow)
/*      */   {
/* 1321 */     if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
/* 1322 */       DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
/*      */     }
/* 1324 */     return paramWindow.getModalExclusionType().compareTo(DEFAULT_MODAL_EXCLUSION_TYPE) >= 0;
/*      */   }
/*      */ 
/*      */   public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType)
/*      */   {
/* 1331 */     return (paramModalityType == Dialog.ModalityType.MODELESS) || (paramModalityType == Dialog.ModalityType.APPLICATION_MODAL);
/*      */   }
/*      */ 
/*      */   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType)
/*      */   {
/* 1339 */     return paramModalExclusionType == Dialog.ModalExclusionType.NO_EXCLUDE;
/*      */   }
/*      */ 
/*      */   public void addModalityListener(ModalityListener paramModalityListener)
/*      */   {
/* 1353 */     this.modalityListeners.add(paramModalityListener);
/*      */   }
/*      */ 
/*      */   public void removeModalityListener(ModalityListener paramModalityListener) {
/* 1357 */     this.modalityListeners.remove(paramModalityListener);
/*      */   }
/*      */ 
/*      */   public void notifyModalityPushed(Dialog paramDialog) {
/* 1361 */     notifyModalityChange(1300, paramDialog);
/*      */   }
/*      */ 
/*      */   public void notifyModalityPopped(Dialog paramDialog) {
/* 1365 */     notifyModalityChange(1301, paramDialog);
/*      */   }
/*      */ 
/*      */   final void notifyModalityChange(int paramInt, Dialog paramDialog) {
/* 1369 */     ModalityEvent localModalityEvent = new ModalityEvent(paramDialog, this.modalityListeners, paramInt);
/* 1370 */     localModalityEvent.dispatch();
/*      */   }
/*      */ 
/*      */   public static boolean isLightweightOrUnknown(Component paramComponent)
/*      */   {
/* 1405 */     if ((paramComponent.isLightweight()) || (!(getDefaultToolkit() instanceof SunToolkit)))
/*      */     {
/* 1408 */       return true;
/*      */     }
/* 1410 */     return (!(paramComponent instanceof Button)) && (!(paramComponent instanceof Canvas)) && (!(paramComponent instanceof Checkbox)) && (!(paramComponent instanceof Choice)) && (!(paramComponent instanceof Label)) && (!(paramComponent instanceof java.awt.List)) && (!(paramComponent instanceof Panel)) && (!(paramComponent instanceof Scrollbar)) && (!(paramComponent instanceof ScrollPane)) && (!(paramComponent instanceof TextArea)) && (!(paramComponent instanceof TextField)) && (!(paramComponent instanceof Window));
/*      */   }
/*      */ 
/*      */   public void realSync()
/*      */     throws SunToolkit.OperationTimedOut, SunToolkit.InfiniteLoop
/*      */   {
/* 1451 */     realSync(10000L);
/*      */   }
/*      */ 
/*      */   public void realSync(long paramLong)
/*      */     throws SunToolkit.OperationTimedOut, SunToolkit.InfiniteLoop
/*      */   {
/* 1501 */     if (EventQueue.isDispatchThread()) {
/* 1502 */       throw new IllegalThreadException("The SunToolkit.realSync() method cannot be used on the event dispatch thread (EDT).");
/*      */     }
/* 1504 */     int i = 0;
/*      */     do
/*      */     {
/* 1507 */       sync();
/*      */ 
/* 1514 */       int j = 0;
/* 1515 */       while (j < 0) {
/* 1516 */         syncNativeQueue(paramLong);
/* 1517 */         j++;
/*      */       }
/* 1519 */       while ((syncNativeQueue(paramLong)) && (j < 20)) {
/* 1520 */         j++;
/*      */       }
/* 1522 */       if (j >= 20) {
/* 1523 */         throw new InfiniteLoop();
/*      */       }
/*      */ 
/* 1533 */       j = 0;
/* 1534 */       while (j < 0) {
/* 1535 */         waitForIdle(paramLong);
/* 1536 */         j++;
/*      */       }
/* 1538 */       while ((waitForIdle(paramLong)) && (j < 20)) {
/* 1539 */         j++;
/*      */       }
/* 1541 */       if (j >= 20) {
/* 1542 */         throw new InfiniteLoop();
/*      */       }
/*      */ 
/* 1545 */       i++;
/*      */     }
/*      */ 
/* 1549 */     while (((syncNativeQueue(paramLong)) || (waitForIdle(paramLong))) && (i < 20));
/*      */   }
/*      */ 
/*      */   protected abstract boolean syncNativeQueue(long paramLong);
/*      */ 
/*      */   private boolean isEQEmpty()
/*      */   {
/* 1567 */     EventQueue localEventQueue = getSystemEventQueueImpl();
/* 1568 */     return AWTAccessor.getEventQueueAccessor().noEvents(localEventQueue);
/*      */   }
/*      */ 
/*      */   protected final boolean waitForIdle(long paramLong)
/*      */   {
/* 1579 */     flushPendingEvents();
/* 1580 */     boolean bool = isEQEmpty();
/* 1581 */     this.queueEmpty = false;
/* 1582 */     this.eventDispatched = false;
/* 1583 */     synchronized (this.waitLock) {
/* 1584 */       postEvent(AppContext.getAppContext(), new PeerEvent(getSystemEventQueueImpl(), null, 4L)
/*      */       {
/*      */         public void dispatch()
/*      */         {
/* 1592 */           int i = 0;
/* 1593 */           while (i < 0) {
/* 1594 */             SunToolkit.this.syncNativeQueue(this.val$timeout);
/* 1595 */             i++;
/*      */           }
/* 1597 */           while ((SunToolkit.this.syncNativeQueue(this.val$timeout)) && (i < 20)) {
/* 1598 */             i++;
/*      */           }
/* 1600 */           SunToolkit.flushPendingEvents();
/*      */ 
/* 1602 */           synchronized (SunToolkit.this.waitLock) {
/* 1603 */             SunToolkit.this.queueEmpty = SunToolkit.this.isEQEmpty();
/* 1604 */             SunToolkit.this.eventDispatched = true;
/* 1605 */             SunToolkit.this.waitLock.notifyAll();
/*      */           }
/*      */         }
/*      */       });
/*      */       try {
/* 1610 */         while (!this.eventDispatched)
/* 1611 */           this.waitLock.wait();
/*      */       }
/*      */       catch (InterruptedException localInterruptedException) {
/* 1614 */         return false;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1619 */       Thread.sleep(0L);
/*      */     } catch (InterruptedException ) {
/* 1621 */       throw new RuntimeException("Interrupted");
/*      */     }
/*      */ 
/* 1624 */     flushPendingEvents();
/*      */ 
/* 1627 */     synchronized (this.waitLock) {
/* 1628 */       return (!this.queueEmpty) || (!isEQEmpty()) || (!bool);
/*      */     }
/*      */   }
/*      */ 
/*      */   public abstract void grab(Window paramWindow);
/*      */ 
/*      */   public abstract void ungrab(Window paramWindow);
/*      */ 
/*      */   public static native void closeSplashScreen();
/*      */ 
/*      */   private void fireDesktopFontPropertyChanges()
/*      */   {
/* 1666 */     setDesktopProperty("awt.font.desktophints", getDesktopFontHints());
/*      */   }
/*      */ 
/*      */   public static void setAAFontSettingsCondition(boolean paramBoolean)
/*      */   {
/* 1700 */     if (paramBoolean != lastExtraCondition) {
/* 1701 */       lastExtraCondition = paramBoolean;
/* 1702 */       if (checkedSystemAAFontSettings)
/*      */       {
/* 1708 */         checkedSystemAAFontSettings = false;
/* 1709 */         Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1710 */         if ((localToolkit instanceof SunToolkit))
/* 1711 */           ((SunToolkit)localToolkit).fireDesktopFontPropertyChanges();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static RenderingHints getDesktopAAHintsByName(String paramString)
/*      */   {
/* 1722 */     Object localObject = null;
/* 1723 */     paramString = paramString.toLowerCase(Locale.ENGLISH);
/* 1724 */     if (paramString.equals("on"))
/* 1725 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
/* 1726 */     else if (paramString.equals("gasp"))
/* 1727 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
/* 1728 */     else if ((paramString.equals("lcd")) || (paramString.equals("lcd_hrgb")))
/* 1729 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
/* 1730 */     else if (paramString.equals("lcd_hbgr"))
/* 1731 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
/* 1732 */     else if (paramString.equals("lcd_vrgb"))
/* 1733 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
/* 1734 */     else if (paramString.equals("lcd_vbgr")) {
/* 1735 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
/*      */     }
/* 1737 */     if (localObject != null) {
/* 1738 */       RenderingHints localRenderingHints = new RenderingHints(null);
/* 1739 */       localRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, localObject);
/* 1740 */       return localRenderingHints;
/*      */     }
/* 1742 */     return null;
/*      */   }
/*      */ 
/*      */   private static boolean useSystemAAFontSettings()
/*      */   {
/* 1755 */     if (!checkedSystemAAFontSettings) {
/* 1756 */       useSystemAAFontSettings = true;
/* 1757 */       String str = null;
/* 1758 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1759 */       if ((localToolkit instanceof SunToolkit)) {
/* 1760 */         str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.useSystemAAFontSettings"));
/*      */       }
/*      */ 
/* 1764 */       if (str != null) {
/* 1765 */         useSystemAAFontSettings = Boolean.valueOf(str).booleanValue();
/*      */ 
/* 1770 */         if (!useSystemAAFontSettings) {
/* 1771 */           desktopFontHints = getDesktopAAHintsByName(str);
/*      */         }
/*      */       }
/*      */ 
/* 1775 */       if (useSystemAAFontSettings) {
/* 1776 */         useSystemAAFontSettings = lastExtraCondition;
/*      */       }
/* 1778 */       checkedSystemAAFontSettings = true;
/*      */     }
/* 1780 */     return useSystemAAFontSettings;
/*      */   }
/*      */ 
/*      */   protected RenderingHints getDesktopAAHints()
/*      */   {
/* 1788 */     return null;
/*      */   }
/*      */ 
/*      */   public static RenderingHints getDesktopFontHints()
/*      */   {
/* 1798 */     if (useSystemAAFontSettings()) {
/* 1799 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1800 */       if ((localToolkit instanceof SunToolkit)) {
/* 1801 */         RenderingHints localRenderingHints = ((SunToolkit)localToolkit).getDesktopAAHints();
/* 1802 */         return (RenderingHints)localRenderingHints;
/*      */       }
/* 1804 */       return null;
/*      */     }
/* 1806 */     if (desktopFontHints != null)
/*      */     {
/* 1810 */       return (RenderingHints)desktopFontHints.clone();
/*      */     }
/* 1812 */     return null;
/*      */   }
/*      */ 
/*      */   public abstract boolean isDesktopSupported();
/*      */ 
/*      */   public static synchronized void consumeNextKeyTyped(KeyEvent paramKeyEvent)
/*      */   {
/*      */     try
/*      */     {
/* 1825 */       AWTAccessor.getDefaultKeyboardFocusManagerAccessor().consumeNextKeyTyped((DefaultKeyboardFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager(), paramKeyEvent);
/*      */     }
/*      */     catch (ClassCastException localClassCastException)
/*      */     {
/* 1830 */       localClassCastException.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void dumpPeers(PlatformLogger paramPlatformLogger) {
/* 1835 */     AWTAutoShutdown.getInstance().dumpPeers(paramPlatformLogger);
/*      */   }
/*      */ 
/*      */   public static Window getContainingWindow(Component paramComponent)
/*      */   {
/* 1844 */     while ((paramComponent != null) && (!(paramComponent instanceof Window))) {
/* 1845 */       paramComponent = paramComponent.getParent();
/*      */     }
/* 1847 */     return (Window)paramComponent;
/*      */   }
/*      */ 
/*      */   public static synchronized boolean getSunAwtDisableMixing()
/*      */   {
/* 1857 */     if (sunAwtDisableMixing == null) {
/* 1858 */       sunAwtDisableMixing = (Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.disableMixing"));
/*      */     }
/*      */ 
/* 1861 */     return sunAwtDisableMixing.booleanValue();
/*      */   }
/*      */ 
/*      */   public boolean isNativeGTKAvailable()
/*      */   {
/* 1870 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized void setWindowDeactivationTime(Window paramWindow, long paramLong)
/*      */   {
/* 1876 */     AppContext localAppContext = getAppContext(paramWindow);
/* 1877 */     WeakHashMap localWeakHashMap = (WeakHashMap)localAppContext.get(DEACTIVATION_TIMES_MAP_KEY);
/* 1878 */     if (localWeakHashMap == null) {
/* 1879 */       localWeakHashMap = new WeakHashMap();
/* 1880 */       localAppContext.put(DEACTIVATION_TIMES_MAP_KEY, localWeakHashMap);
/*      */     }
/* 1882 */     localWeakHashMap.put(paramWindow, Long.valueOf(paramLong));
/*      */   }
/*      */ 
/*      */   public synchronized long getWindowDeactivationTime(Window paramWindow) {
/* 1886 */     AppContext localAppContext = getAppContext(paramWindow);
/* 1887 */     WeakHashMap localWeakHashMap = (WeakHashMap)localAppContext.get(DEACTIVATION_TIMES_MAP_KEY);
/* 1888 */     if (localWeakHashMap == null) {
/* 1889 */       return -1L;
/*      */     }
/* 1891 */     Long localLong = (Long)localWeakHashMap.get(paramWindow);
/* 1892 */     return localLong == null ? -1L : localLong.longValue();
/*      */   }
/*      */ 
/*      */   public boolean isWindowOpacitySupported()
/*      */   {
/* 1897 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isWindowShapingSupported()
/*      */   {
/* 1902 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isWindowTranslucencySupported()
/*      */   {
/* 1907 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isTranslucencyCapable(GraphicsConfiguration paramGraphicsConfiguration) {
/* 1911 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean isContainingTopLevelOpaque(Component paramComponent)
/*      */   {
/* 1926 */     Window localWindow = getContainingWindow(paramComponent);
/* 1927 */     return (localWindow != null) && (localWindow.isOpaque());
/*      */   }
/*      */ 
/*      */   public static boolean isContainingTopLevelTranslucent(Component paramComponent)
/*      */   {
/* 1942 */     Window localWindow = getContainingWindow(paramComponent);
/* 1943 */     return (localWindow != null) && (localWindow.getOpacity() < 1.0F);
/*      */   }
/*      */ 
/*      */   public boolean needUpdateWindow()
/*      */   {
/* 1954 */     return false;
/*      */   }
/*      */ 
/*      */   public int getNumberOfButtons()
/*      */   {
/* 1961 */     return 3;
/*      */   }
/*      */ 
/*      */   public static boolean isInstanceOf(Object paramObject, String paramString)
/*      */   {
/* 1979 */     if (paramObject == null) return false;
/* 1980 */     if (paramString == null) return false;
/*      */ 
/* 1982 */     return isInstanceOf(paramObject.getClass(), paramString);
/*      */   }
/*      */ 
/*      */   private static boolean isInstanceOf(Class paramClass, String paramString) {
/* 1986 */     if (paramClass == null) return false;
/*      */ 
/* 1988 */     if (paramClass.getName().equals(paramString)) {
/* 1989 */       return true;
/*      */     }
/*      */ 
/* 1992 */     for (Class localClass : paramClass.getInterfaces()) {
/* 1993 */       if (localClass.getName().equals(paramString)) {
/* 1994 */         return true;
/*      */       }
/*      */     }
/* 1997 */     return isInstanceOf(paramClass.getSuperclass(), paramString);
/*      */   }
/*      */ 
/*      */   public static void setSystemGenerated(AWTEvent paramAWTEvent)
/*      */   {
/* 2011 */     AWTAccessor.getAWTEventAccessor().setSystemGenerated(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   public static boolean isSystemGenerated(AWTEvent paramAWTEvent) {
/* 2015 */     return AWTAccessor.getAWTEventAccessor().isSystemGenerated(paramAWTEvent);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   61 */     log = PlatformLogger.getLogger("sun.awt.SunToolkit");
/*      */ 
/*   65 */     if (((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.nativedebug"))).booleanValue())
/*   66 */       DebugSettings.init();
/*      */   }
/*      */ 
/*      */   public static class IllegalThreadException extends RuntimeException
/*      */   {
/*      */     public IllegalThreadException(String paramString)
/*      */     {
/* 1436 */       super();
/*      */     }
/*      */ 
/*      */     public IllegalThreadException()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class InfiniteLoop extends RuntimeException
/*      */   {
/*      */   }
/*      */ 
/*      */   static class ModalityListenerList
/*      */     implements ModalityListener
/*      */   {
/* 1375 */     Vector<ModalityListener> listeners = new Vector();
/*      */ 
/*      */     void add(ModalityListener paramModalityListener) {
/* 1378 */       this.listeners.addElement(paramModalityListener);
/*      */     }
/*      */ 
/*      */     void remove(ModalityListener paramModalityListener) {
/* 1382 */       this.listeners.removeElement(paramModalityListener);
/*      */     }
/*      */ 
/*      */     public void modalityPushed(ModalityEvent paramModalityEvent) {
/* 1386 */       Iterator localIterator = this.listeners.iterator();
/* 1387 */       while (localIterator.hasNext())
/* 1388 */         ((ModalityListener)localIterator.next()).modalityPushed(paramModalityEvent);
/*      */     }
/*      */ 
/*      */     public void modalityPopped(ModalityEvent paramModalityEvent)
/*      */     {
/* 1393 */       Iterator localIterator = this.listeners.iterator();
/* 1394 */       while (localIterator.hasNext())
/* 1395 */         ((ModalityListener)localIterator.next()).modalityPopped(paramModalityEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class OperationTimedOut extends RuntimeException
/*      */   {
/*      */     public OperationTimedOut(String paramString)
/*      */     {
/* 1426 */       super();
/*      */     }
/*      */ 
/*      */     public OperationTimedOut()
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.SunToolkit
 * JD-Core Version:    0.6.2
 */