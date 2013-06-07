/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.AWTException;
/*      */ import java.awt.Button;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.Checkbox;
/*      */ import java.awt.CheckboxMenuItem;
/*      */ import java.awt.Choice;
/*      */ import java.awt.Component;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Desktop;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.Dialog.ModalExclusionType;
/*      */ import java.awt.Dialog.ModalityType;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.FileDialog;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.JobAttributes;
/*      */ import java.awt.KeyboardFocusManager;
/*      */ import java.awt.Label;
/*      */ import java.awt.Menu;
/*      */ import java.awt.MenuBar;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.PageAttributes;
/*      */ import java.awt.Panel;
/*      */ import java.awt.Point;
/*      */ import java.awt.PopupMenu;
/*      */ import java.awt.PrintJob;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Robot;
/*      */ import java.awt.ScrollPane;
/*      */ import java.awt.Scrollbar;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.SystemTray;
/*      */ import java.awt.TextArea;
/*      */ import java.awt.TextField;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.TrayIcon;
/*      */ import java.awt.Window;
/*      */ import java.awt.datatransfer.Clipboard;
/*      */ import java.awt.dnd.DragGestureEvent;
/*      */ import java.awt.dnd.DragGestureListener;
/*      */ import java.awt.dnd.DragGestureRecognizer;
/*      */ import java.awt.dnd.DragSource;
/*      */ import java.awt.dnd.InvalidDnDOperationException;
/*      */ import java.awt.dnd.MouseDragGestureRecognizer;
/*      */ import java.awt.dnd.peer.DragSourceContextPeer;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.im.InputMethodHighlight;
/*      */ import java.awt.im.spi.InputMethodDescriptor;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.peer.ButtonPeer;
/*      */ import java.awt.peer.CanvasPeer;
/*      */ import java.awt.peer.CheckboxMenuItemPeer;
/*      */ import java.awt.peer.CheckboxPeer;
/*      */ import java.awt.peer.ChoicePeer;
/*      */ import java.awt.peer.DesktopPeer;
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
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.TreeMap;
/*      */ import java.util.Vector;
/*      */ import javax.swing.LookAndFeel;
/*      */ import javax.swing.UIDefaults;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.AWTAccessor.EventQueueAccessor;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.SunToolkit.OperationTimedOut;
/*      */ import sun.awt.UNIXToolkit;
/*      */ import sun.awt.X11GraphicsConfig;
/*      */ import sun.awt.X11GraphicsDevice;
/*      */ import sun.awt.X11GraphicsEnvironment;
/*      */ import sun.awt.XSettings;
/*      */ import sun.font.FontConfigManager;
/*      */ import sun.java2d.x11.X11SurfaceData;
/*      */ import sun.misc.PerformanceLogger;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.print.PrintJob2D;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public final class XToolkit extends UNIXToolkit
/*      */   implements Runnable
/*      */ {
/*   58 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XToolkit");
/*   59 */   private static final PlatformLogger eventLog = PlatformLogger.getLogger("sun.awt.X11.event.XToolkit");
/*   60 */   private static final PlatformLogger timeoutTaskLog = PlatformLogger.getLogger("sun.awt.X11.timeoutTask.XToolkit");
/*   61 */   private static final PlatformLogger keyEventLog = PlatformLogger.getLogger("sun.awt.X11.kye.XToolkit");
/*   62 */   private static final PlatformLogger backingStoreLog = PlatformLogger.getLogger("sun.awt.X11.backingStore.XToolkit");
/*      */   private static final int AWT_MULTICLICK_DEFAULT_TIME = 500;
/*      */   static final boolean PRIMARY_LOOP = false;
/*      */   static final boolean SECONDARY_LOOP = true;
/*   71 */   private static String awtAppClassName = null;
/*      */   XClipboard clipboard;
/*      */   XClipboard selection;
/*   79 */   protected static boolean dynamicLayoutSetting = false;
/*      */ 
/*   83 */   private static boolean areExtraMouseButtonsEnabled = true;
/*      */   private boolean loadedXSettings;
/*      */   private XSettings xs;
/*   96 */   private FontConfigManager fcManager = new FontConfigManager();
/*      */   static int arrowCursor;
/*   99 */   static TreeMap winMap = new TreeMap();
/*  100 */   static HashMap specialPeerMap = new HashMap();
/*  101 */   static HashMap winToDispatcher = new HashMap();
/*      */   private static long _display;
/*      */   static UIDefaults uidefaults;
/*      */   static X11GraphicsEnvironment localEnv;
/*      */   static X11GraphicsDevice device;
/*      */   static final X11GraphicsConfig config;
/*      */   static int awt_multiclick_time;
/*      */   static boolean securityWarningEnabled;
/*  110 */   private static int screenWidth = -1; private static int screenHeight = -1;
/*      */   static long awt_defaultFg;
/*      */   private static XMouseInfoPeer xPeer;
/*      */   private static long saved_error_handler;
/*      */   static volatile XErrorEvent saved_error;
/*      */   private static XErrorHandler current_error_handler;
/*  130 */   private static boolean noisyAwtHandler = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.noisyerrorhandler"))).booleanValue();
/*      */   static Thread toolkitThread;
/*  269 */   static Object displayLock = new Object();
/*      */   static final String DATA_TRANSFERER_CLASS_NAME = "sun.awt.X11.XDataTransferer";
/*      */   private Point lastCursorPos;
/*  519 */   private Collection<XEventListener> listeners = new LinkedList();
/*      */ 
/* 1058 */   private static Boolean sunAwtDisableGtkFileDialogs = null;
/*      */   static ColorModel screenmodel;
/*      */   private static boolean initialized;
/*      */   private static boolean timeStampUpdated;
/*      */   private static long timeStamp;
/* 1386 */   private static final XEventDispatcher timeFetcher = new XEventDispatcher()
/*      */   {
/*      */     public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 1389 */       switch (paramAnonymousXEvent.get_type()) {
/*      */       case 28:
/* 1391 */         XPropertyEvent localXPropertyEvent = paramAnonymousXEvent.get_xproperty();
/*      */ 
/* 1393 */         SunToolkit.awtLock();
/*      */         try {
/* 1395 */           XToolkit.access$202(localXPropertyEvent.get_time());
/* 1396 */           XToolkit.access$302(true);
/* 1397 */           SunToolkit.awtLockNotifyAll();
/*      */         } finally {
/* 1399 */           SunToolkit.awtUnlock();
/*      */         }
/*      */       }
/*      */     }
/* 1386 */   };
/*      */   private static XAtom _XA_JAVA_TIME_PROPERTY_ATOM;
/*      */   private static final String prefix = "DnD.Cursor.";
/*      */   private static final String postfix = ".32x32";
/*      */   private static final String dndPrefix = "DnD.";
/*      */   static int altMask;
/*      */   static int metaMask;
/*      */   static int numLockMask;
/*      */   static int modeSwitchMask;
/*      */   static int modLockIsShiftLock;
/*      */   private static SortedMap timeoutTasks;
/*      */   static long reset_time_utc;
/*      */   static final long WRAP_TIME_MILLIS = 4294967295L;
/*      */   private static int backingStoreType;
/*      */   static final int XSUN_KP_BEHAVIOR = 1;
/*      */   static final int XORG_KP_BEHAVIOR = 2;
/*      */   static final int IS_SUN_KEYBOARD = 1;
/*      */   static final int IS_NONSUN_KEYBOARD = 2;
/*      */   static final int IS_KANA_KEYBOARD = 1;
/*      */   static final int IS_NONKANA_KEYBOARD = 2;
/* 2166 */   static int awt_IsXsunKPBehavior = 0;
/* 2167 */   static boolean awt_UseXKB = false;
/* 2168 */   static boolean awt_UseXKB_Calls = false;
/* 2169 */   static int awt_XKBBaseEventCode = 0;
/* 2170 */   static int awt_XKBEffectiveGroup = 0;
/*      */ 
/* 2172 */   static long awt_XKBDescPtr = 0L;
/*      */ 
/* 2195 */   static int sunOrNotKeyboard = 0;
/* 2196 */   static int kanaOrNotKeyboard = 0;
/*      */   private static long eventNumber;
/*      */   private static XEventDispatcher oops_waiter;
/*      */   private static boolean oops_updated;
/*      */   private static boolean oops_failed;
/*      */   private XAtom oops;
/*      */   private static final long WORKAROUND_SLEEP = 100L;
/*      */ 
/*      */   static native long getTrayIconDisplayTimeout();
/*      */ 
/*      */   public static void WITH_XERROR_HANDLER(XErrorHandler paramXErrorHandler)
/*      */   {
/*  162 */     saved_error = null;
/*  163 */     current_error_handler = paramXErrorHandler;
/*      */   }
/*      */ 
/*      */   public static void RESTORE_XERROR_HANDLER()
/*      */   {
/*  169 */     XSync();
/*  170 */     current_error_handler = null;
/*      */   }
/*      */ 
/*      */   public static int SAVED_ERROR_HANDLER(long paramLong, XErrorEvent paramXErrorEvent)
/*      */   {
/*  175 */     if ((saved_error_handler == 0L) || 
/*  179 */       (log.isLoggable(500))) {
/*  180 */       log.fine("Unhandled XErrorEvent: id=" + paramXErrorEvent.get_resourceid() + ", " + "serial=" + paramXErrorEvent.get_serial() + ", " + "ec=" + paramXErrorEvent.get_error_code() + ", " + "rc=" + paramXErrorEvent.get_request_code() + ", " + "mc=" + paramXErrorEvent.get_minor_code());
/*      */     }
/*      */ 
/*  187 */     return 0;
/*      */   }
/*      */ 
/*      */   private static int globalErrorHandler(long paramLong1, long paramLong2)
/*      */   {
/*  192 */     if (noisyAwtHandler) {
/*  193 */       XlibWrapper.PrintXErrorEvent(paramLong1, paramLong2);
/*      */     }
/*  195 */     XErrorEvent localXErrorEvent = new XErrorEvent(paramLong2);
/*  196 */     saved_error = localXErrorEvent;
/*      */     try {
/*  198 */       if (current_error_handler != null) {
/*  199 */         return current_error_handler.handleError(paramLong1, localXErrorEvent);
/*      */       }
/*  201 */       return SAVED_ERROR_HANDLER(paramLong1, localXErrorEvent);
/*      */     }
/*      */     catch (Throwable localThrowable) {
/*  204 */       log.fine("Error in GlobalErrorHandler", localThrowable);
/*      */     }
/*  206 */     return 0;
/*      */   }
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   static native void waitForEvents(long paramLong);
/*      */ 
/*      */   static boolean isToolkitThread()
/*      */   {
/*  215 */     return Thread.currentThread() == toolkitThread;
/*      */   }
/*      */ 
/*      */   static void initSecurityWarning()
/*      */   {
/*  220 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.runtime.version"));
/*      */ 
/*  222 */     securityWarningEnabled = (str != null) && (str.contains("internal"));
/*      */   }
/*      */ 
/*      */   static boolean isSecurityWarningEnabled() {
/*  226 */     return securityWarningEnabled;
/*      */   }
/*      */ 
/*      */   static native void awt_output_flush();
/*      */ 
/*      */   static final void awtFUnlock() {
/*  232 */     awtUnlock();
/*  233 */     awt_output_flush();
/*      */   }
/*      */ 
/*      */   public native void nativeLoadSystemColors(int[] paramArrayOfInt);
/*      */ 
/*      */   static UIDefaults getUIDefaults()
/*      */   {
/*  240 */     if (uidefaults == null) {
/*  241 */       initUIDefaults();
/*      */     }
/*  243 */     return uidefaults;
/*      */   }
/*      */ 
/*      */   public void loadSystemColors(int[] paramArrayOfInt) {
/*  247 */     nativeLoadSystemColors(paramArrayOfInt);
/*  248 */     MotifColorUtilities.loadSystemColors(paramArrayOfInt);
/*      */   }
/*      */ 
/*      */   static void initUIDefaults()
/*      */   {
/*      */     try
/*      */     {
/*  258 */       SystemColor localSystemColor = SystemColor.text;
/*      */ 
/*  260 */       XAWTLookAndFeel localXAWTLookAndFeel = new XAWTLookAndFeel();
/*  261 */       uidefaults = localXAWTLookAndFeel.getDefaults();
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*  265 */       localException.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static long getDisplay()
/*      */   {
/*  272 */     return _display;
/*      */   }
/*      */ 
/*      */   public static long getDefaultRootWindow() {
/*  276 */     awtLock();
/*      */     try {
/*  278 */       long l1 = XlibWrapper.RootWindow(getDisplay(), XlibWrapper.DefaultScreen(getDisplay()));
/*      */ 
/*  281 */       if (l1 == 0L) {
/*  282 */         throw new IllegalStateException("Root window must not be null");
/*      */       }
/*  284 */       return l1;
/*      */     } finally {
/*  286 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   void init() {
/*  291 */     awtLock();
/*      */     try {
/*  293 */       XlibWrapper.XSupportsLocale();
/*  294 */       if (XlibWrapper.XSetLocaleModifiers("") == null) {
/*  295 */         log.finer("X locale modifiers are not supported, using default");
/*      */       }
/*  297 */       tryXKB();
/*      */ 
/*  299 */       localObject1 = new AwtScreenData(getDefaultScreenData());
/*  300 */       awt_defaultFg = ((AwtScreenData)localObject1).get_blackpixel();
/*      */ 
/*  302 */       arrowCursor = XlibWrapper.XCreateFontCursor(getDisplay(), 2);
/*      */ 
/*  304 */       areExtraMouseButtonsEnabled = Boolean.parseBoolean(System.getProperty("sun.awt.enableExtraMouseButtons", "true"));
/*      */ 
/*  306 */       System.setProperty("sun.awt.enableExtraMouseButtons", "" + areExtraMouseButtonsEnabled);
/*      */ 
/*  308 */       saved_error_handler = XlibWrapper.SetToolkitErrorHandler();
/*      */     } finally {
/*  310 */       awtUnlock();
/*      */     }
/*  312 */     Object localObject1 = new PrivilegedAction() {
/*      */       public Void run() {
/*  314 */         Object localObject = Thread.currentThread().getThreadGroup();
/*  315 */         ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent();
/*  316 */         while (localThreadGroup != null) {
/*  317 */           localObject = localThreadGroup;
/*  318 */           localThreadGroup = ((ThreadGroup)localObject).getParent();
/*      */         }
/*  320 */         Thread local1 = new Thread((ThreadGroup)localObject, "XToolkt-Shutdown-Thread") {
/*      */           public void run() {
/*  322 */             XSystemTrayPeer localXSystemTrayPeer = XSystemTrayPeer.getPeerInstance();
/*  323 */             if (localXSystemTrayPeer != null) {
/*  324 */               localXSystemTrayPeer.dispose();
/*      */             }
/*  326 */             if (XToolkit.this.xs != null) {
/*  327 */               ((XAWTXSettings)XToolkit.this.xs).dispose();
/*      */             }
/*  329 */             XToolkit.this.freeXKB();
/*  330 */             if (XToolkit.log.isLoggable(500))
/*  331 */               XToolkit.dumpPeers();
/*      */           }
/*      */         };
/*  335 */         local1.setContextClassLoader(null);
/*  336 */         Runtime.getRuntime().addShutdownHook(local1);
/*  337 */         return null;
/*      */       }
/*      */     };
/*  340 */     AccessController.doPrivileged((PrivilegedAction)localObject1);
/*      */   }
/*      */ 
/*      */   static String getCorrectXIDString(String paramString) {
/*  344 */     if (paramString != null) {
/*  345 */       return paramString.replace('.', '-');
/*      */     }
/*  347 */     return paramString;
/*      */   }
/*      */ 
/*      */   static native String getEnv(String paramString);
/*      */ 
/*      */   static String getAWTAppClassName()
/*      */   {
/*  355 */     return awtAppClassName;
/*      */   }
/*      */ 
/*      */   public XToolkit()
/*      */   {
/*  362 */     if (PerformanceLogger.loggingEnabled()) {
/*  363 */       PerformanceLogger.setTime("XToolkit construction");
/*      */     }
/*      */ 
/*  366 */     if (!GraphicsEnvironment.isHeadless()) {
/*  367 */       String str = null;
/*      */ 
/*  369 */       StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
/*  370 */       int i = arrayOfStackTraceElement.length - 1;
/*  371 */       if (i >= 0) {
/*  372 */         str = arrayOfStackTraceElement[i].getClassName();
/*      */       }
/*  374 */       if ((str == null) || (str.equals(""))) {
/*  375 */         str = "AWT";
/*      */       }
/*  377 */       awtAppClassName = getCorrectXIDString(str);
/*      */ 
/*  379 */       init();
/*  380 */       XWM.init();
/*  381 */       SunToolkit.setDataTransfererClassName("sun.awt.X11.XDataTransferer");
/*      */ 
/*  383 */       PrivilegedAction local2 = new PrivilegedAction() {
/*      */         public Thread run() {
/*  385 */           Object localObject = Thread.currentThread().getThreadGroup();
/*  386 */           ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent();
/*  387 */           while (localThreadGroup != null) {
/*  388 */             localObject = localThreadGroup;
/*  389 */             localThreadGroup = ((ThreadGroup)localObject).getParent();
/*      */           }
/*  391 */           Thread localThread = new Thread((ThreadGroup)localObject, XToolkit.this, "AWT-XAWT");
/*  392 */           localThread.setPriority(6);
/*  393 */           localThread.setDaemon(true);
/*  394 */           return localThread;
/*      */         }
/*      */       };
/*  397 */       toolkitThread = (Thread)AccessController.doPrivileged(local2);
/*  398 */       toolkitThread.start();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ButtonPeer createButton(Button paramButton) {
/*  403 */     XButtonPeer localXButtonPeer = new XButtonPeer(paramButton);
/*  404 */     targetCreatedPeer(paramButton, localXButtonPeer);
/*  405 */     return localXButtonPeer;
/*      */   }
/*      */ 
/*      */   public FramePeer createFrame(Frame paramFrame) {
/*  409 */     XFramePeer localXFramePeer = new XFramePeer(paramFrame);
/*  410 */     targetCreatedPeer(paramFrame, localXFramePeer);
/*  411 */     return localXFramePeer;
/*      */   }
/*      */ 
/*      */   static void addToWinMap(long paramLong, XBaseWindow paramXBaseWindow)
/*      */   {
/*  416 */     synchronized (winMap) {
/*  417 */       winMap.put(Long.valueOf(paramLong), paramXBaseWindow);
/*      */     }
/*      */   }
/*      */ 
/*      */   static void removeFromWinMap(long paramLong, XBaseWindow paramXBaseWindow) {
/*  422 */     synchronized (winMap) {
/*  423 */       winMap.remove(Long.valueOf(paramLong));
/*      */     }
/*      */   }
/*      */ 
/*  427 */   static XBaseWindow windowToXWindow(long paramLong) { synchronized (winMap) {
/*  428 */       return (XBaseWindow)winMap.get(Long.valueOf(paramLong));
/*      */     } }
/*      */ 
/*      */   static void addEventDispatcher(long paramLong, XEventDispatcher paramXEventDispatcher)
/*      */   {
/*  433 */     synchronized (winToDispatcher) {
/*  434 */       Long localLong = Long.valueOf(paramLong);
/*  435 */       Object localObject1 = (Collection)winToDispatcher.get(localLong);
/*  436 */       if (localObject1 == null) {
/*  437 */         localObject1 = new Vector();
/*  438 */         winToDispatcher.put(localLong, localObject1);
/*      */       }
/*  440 */       ((Collection)localObject1).add(paramXEventDispatcher);
/*      */     }
/*      */   }
/*      */ 
/*  444 */   static void removeEventDispatcher(long paramLong, XEventDispatcher paramXEventDispatcher) { synchronized (winToDispatcher) {
/*  445 */       Long localLong = Long.valueOf(paramLong);
/*  446 */       Collection localCollection = (Collection)winToDispatcher.get(localLong);
/*  447 */       if (localCollection != null)
/*  448 */         localCollection.remove(paramXEventDispatcher);
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean getLastCursorPos(Point paramPoint)
/*      */   {
/*  463 */     awtLock();
/*      */     try
/*      */     {
/*      */       boolean bool;
/*  465 */       if (this.lastCursorPos == null) {
/*  466 */         return false;
/*      */       }
/*  468 */       paramPoint.setLocation(this.lastCursorPos);
/*  469 */       return true;
/*      */     } finally {
/*  471 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processGlobalMotionEvent(XEvent paramXEvent)
/*      */   {
/*      */     Object localObject1;
/*  479 */     if (paramXEvent.get_type() == 6) {
/*  480 */       localObject1 = paramXEvent.get_xmotion();
/*  481 */       awtLock();
/*      */       try {
/*  483 */         if (this.lastCursorPos == null)
/*  484 */           this.lastCursorPos = new Point(((XMotionEvent)localObject1).get_x_root(), ((XMotionEvent)localObject1).get_y_root());
/*      */         else
/*  486 */           this.lastCursorPos.setLocation(((XMotionEvent)localObject1).get_x_root(), ((XMotionEvent)localObject1).get_y_root());
/*      */       }
/*      */       finally {
/*  489 */         awtUnlock();
/*      */       }
/*  491 */     } else if (paramXEvent.get_type() == 8)
/*      */     {
/*  493 */       awtLock();
/*      */       try {
/*  495 */         this.lastCursorPos = null;
/*      */       } finally {
/*  497 */         awtUnlock();
/*      */       }
/*  499 */     } else if (paramXEvent.get_type() == 7)
/*      */     {
/*  501 */       localObject1 = paramXEvent.get_xcrossing();
/*  502 */       awtLock();
/*      */       try {
/*  504 */         if (this.lastCursorPos == null)
/*  505 */           this.lastCursorPos = new Point(((XCrossingEvent)localObject1).get_x_root(), ((XCrossingEvent)localObject1).get_y_root());
/*      */         else
/*  507 */           this.lastCursorPos.setLocation(((XCrossingEvent)localObject1).get_x_root(), ((XCrossingEvent)localObject1).get_y_root());
/*      */       }
/*      */       finally {
/*  510 */         awtUnlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addXEventListener(XEventListener paramXEventListener)
/*      */   {
/*  522 */     synchronized (this.listeners) {
/*  523 */       this.listeners.add(paramXEventListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifyListeners(XEvent paramXEvent) {
/*  528 */     synchronized (this.listeners) {
/*  529 */       if (this.listeners.size() == 0) return;
/*      */ 
/*  531 */       XEvent localXEvent = paramXEvent.clone();
/*      */       try {
/*  533 */         for (XEventListener localXEventListener : this.listeners)
/*  534 */           localXEventListener.eventProcessed(localXEvent);
/*      */       }
/*      */       finally {
/*  537 */         localXEvent.dispose();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void dispatchEvent(XEvent paramXEvent) {
/*  543 */     XAnyEvent localXAnyEvent = paramXEvent.get_xany();
/*      */ 
/*  545 */     if ((windowToXWindow(localXAnyEvent.get_window()) != null) && ((paramXEvent.get_type() == 6) || (paramXEvent.get_type() == 7) || (paramXEvent.get_type() == 8)))
/*      */     {
/*  548 */       processGlobalMotionEvent(paramXEvent);
/*      */     }
/*      */ 
/*  551 */     if (paramXEvent.get_type() == 34)
/*      */     {
/*  557 */       XlibWrapper.XRefreshKeyboardMapping(paramXEvent.pData);
/*  558 */       resetKeyboardSniffer();
/*  559 */       setupModifierMap();
/*      */     }
/*  561 */     XBaseWindow.dispatchToWindow(paramXEvent);
/*      */ 
/*  563 */     Object localObject1 = null;
/*      */     Object localObject2;
/*  564 */     synchronized (winToDispatcher) {
/*  565 */       localObject2 = Long.valueOf(localXAnyEvent.get_window());
/*  566 */       localObject1 = (Collection)winToDispatcher.get(localObject2);
/*  567 */       if (localObject1 != null) {
/*  568 */         localObject1 = new Vector((Collection)localObject1);
/*      */       }
/*      */     }
/*  571 */     if (localObject1 != null) {
/*  572 */       ??? = ((Collection)localObject1).iterator();
/*  573 */       while (((Iterator)???).hasNext()) {
/*  574 */         localObject2 = (XEventDispatcher)((Iterator)???).next();
/*  575 */         ((XEventDispatcher)localObject2).dispatchEvent(paramXEvent);
/*      */       }
/*      */     }
/*  578 */     notifyListeners(paramXEvent);
/*      */   }
/*      */ 
/*      */   static void processException(Throwable paramThrowable) {
/*  582 */     if (log.isLoggable(900))
/*  583 */       log.warning("Exception on Toolkit thread", paramThrowable);
/*      */   }
/*      */ 
/*      */   static native void awt_toolkit_init();
/*      */ 
/*      */   public void run()
/*      */   {
/*  590 */     awt_toolkit_init();
/*  591 */     run(false);
/*      */   }
/*      */ 
/*      */   public void run(boolean paramBoolean)
/*      */   {
/*  596 */     XEvent localXEvent = new XEvent();
/*      */ 
/*  599 */     while ((!Thread.currentThread().isInterrupted()) || 
/*  605 */       (!AppContext.getAppContext().isDisposed()))
/*      */     {
/*  609 */       awtLock();
/*      */       try {
/*  611 */         if (paramBoolean == true)
/*      */         {
/*  616 */           if (!XlibWrapper.XNextSecondaryLoopEvent(getDisplay(), localXEvent.pData))
/*      */           {
/*  678 */             awtUnlock(); break;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  620 */           callTimeoutTasks();
/*      */ 
/*  624 */           while ((XlibWrapper.XEventsQueued(getDisplay(), 1) == 0) && (XlibWrapper.XEventsQueued(getDisplay(), 2) == 0))
/*      */           {
/*  626 */             callTimeoutTasks();
/*  627 */             waitForEvents(getNextTaskTime());
/*      */           }
/*  629 */           XlibWrapper.XNextEvent(getDisplay(), localXEvent.pData);
/*      */         }
/*      */ 
/*  632 */         if (localXEvent.get_type() != 14) {
/*  633 */           eventNumber += 1L;
/*      */         }
/*  635 */         if ((awt_UseXKB_Calls) && (localXEvent.get_type() == awt_XKBBaseEventCode)) {
/*  636 */           processXkbChanges(localXEvent);
/*      */         }
/*      */ 
/*  639 */         if ((XDropTargetEventProcessor.processEvent(localXEvent)) || (XDragSourceContextPeer.processEvent(localXEvent)))
/*      */         {
/*  678 */           awtUnlock();
/*      */         }
/*      */         else
/*      */         {
/*  644 */           if (eventLog.isLoggable(400)) {
/*  645 */             eventLog.finer("{0}", new Object[] { localXEvent });
/*      */           }
/*      */ 
/*  649 */           long l = 0L;
/*  650 */           if (windowToXWindow(localXEvent.get_xany().get_window()) != null) {
/*  651 */             Component localComponent = XKeyboardFocusManagerPeer.getCurrentNativeFocusOwner();
/*      */ 
/*  653 */             if (localComponent != null) {
/*  654 */               XWindow localXWindow = (XWindow)AWTAccessor.getComponentAccessor().getPeer(localComponent);
/*  655 */               if (localXWindow != null) {
/*  656 */                 l = localXWindow.getContentWindow();
/*      */               }
/*      */             }
/*      */           }
/*  660 */           if ((keyEventLog.isLoggable(500)) && ((localXEvent.get_type() == 2) || (localXEvent.get_type() == 3))) {
/*  661 */             keyEventLog.fine("before XFilterEvent:" + localXEvent);
/*      */           }
/*  663 */           if (XlibWrapper.XFilterEvent(localXEvent.getPData(), l))
/*      */           {
/*  678 */             awtUnlock();
/*      */           }
/*      */           else
/*      */           {
/*  666 */             if ((keyEventLog.isLoggable(500)) && ((localXEvent.get_type() == 2) || (localXEvent.get_type() == 3))) {
/*  667 */               keyEventLog.fine("after XFilterEvent:" + localXEvent);
/*      */             }
/*      */ 
/*  670 */             dispatchEvent(localXEvent);
/*      */           }
/*      */         } } catch (ThreadDeath localThreadDeath) { XBaseWindow.ungrabInput();
/*      */         return; } catch (Throwable localThrowable) {
/*  675 */         XBaseWindow.ungrabInput();
/*  676 */         processException(localThrowable);
/*      */       } finally {
/*  678 */         awtUnlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static int getDefaultScreenWidth() {
/*  684 */     if (screenWidth == -1) {
/*  685 */       long l = getDisplay();
/*  686 */       awtLock();
/*      */       try {
/*  688 */         screenWidth = (int)XlibWrapper.DisplayWidth(l, XlibWrapper.DefaultScreen(l));
/*      */       } finally {
/*  690 */         awtUnlock();
/*      */       }
/*      */     }
/*  693 */     return screenWidth;
/*      */   }
/*      */ 
/*      */   static int getDefaultScreenHeight() {
/*  697 */     if (screenHeight == -1) {
/*  698 */       long l = getDisplay();
/*  699 */       awtLock();
/*      */       try {
/*  701 */         screenHeight = (int)XlibWrapper.DisplayHeight(l, XlibWrapper.DefaultScreen(l));
/*      */       } finally {
/*  703 */         awtUnlock();
/*      */       }
/*      */     }
/*  706 */     return screenHeight;
/*      */   }
/*      */ 
/*      */   protected int getScreenWidth() {
/*  710 */     return getDefaultScreenWidth();
/*      */   }
/*      */ 
/*      */   protected int getScreenHeight() {
/*  714 */     return getDefaultScreenHeight();
/*      */   }
/*      */ 
/*      */   private static Rectangle getWorkArea(long paramLong)
/*      */   {
/*  719 */     XAtom localXAtom = XAtom.get("_NET_WORKAREA");
/*      */ 
/*  721 */     long l = Native.allocateLongArray(4);
/*      */     try
/*      */     {
/*  724 */       boolean bool = localXAtom.getAtomData(paramLong, 6L, l, 4);
/*      */ 
/*  726 */       if (bool)
/*      */       {
/*  728 */         int i = (int)Native.getLong(l, 0);
/*  729 */         int j = (int)Native.getLong(l, 1);
/*  730 */         int k = (int)Native.getLong(l, 2);
/*  731 */         int m = (int)Native.getLong(l, 3);
/*      */ 
/*  733 */         return new Rectangle(i, j, k, m);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  738 */       XlibWrapper.unsafe.freeMemory(l);
/*      */     }
/*      */ 
/*  741 */     return null;
/*      */   }
/*      */ 
/*      */   public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/*  755 */     XNETProtocol localXNETProtocol = XWM.getWM().getNETProtocol();
/*  756 */     if ((localXNETProtocol == null) || (!localXNETProtocol.active()))
/*      */     {
/*  758 */       return super.getScreenInsets(paramGraphicsConfiguration);
/*      */     }
/*      */ 
/*  761 */     awtLock();
/*      */     try
/*      */     {
/*  764 */       X11GraphicsConfig localX11GraphicsConfig = (X11GraphicsConfig)paramGraphicsConfiguration;
/*  765 */       X11GraphicsDevice localX11GraphicsDevice = (X11GraphicsDevice)localX11GraphicsConfig.getDevice();
/*  766 */       long l = XlibUtil.getRootWindow(localX11GraphicsDevice.getScreen());
/*  767 */       Rectangle localRectangle = XlibUtil.getWindowGeometry(l);
/*      */ 
/*  769 */       X11GraphicsEnvironment localX11GraphicsEnvironment = (X11GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
/*      */       Object localObject1;
/*  771 */       if (!localX11GraphicsEnvironment.runningXinerama())
/*      */       {
/*  773 */         localObject1 = getWorkArea(l);
/*  774 */         if (localObject1 != null)
/*      */         {
/*  776 */           return new Insets(((Rectangle)localObject1).y, ((Rectangle)localObject1).x, localRectangle.height - ((Rectangle)localObject1).height - ((Rectangle)localObject1).y, localRectangle.width - ((Rectangle)localObject1).width - ((Rectangle)localObject1).x);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  783 */       return getScreenInsetsManually(l, localRectangle, paramGraphicsConfiguration.getBounds());
/*      */     }
/*      */     finally
/*      */     {
/*  787 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private Insets getScreenInsetsManually(long paramLong, Rectangle paramRectangle1, Rectangle paramRectangle2)
/*      */   {
/*  809 */     XAtom localXAtom1 = XAtom.get("_NET_WM_STRUT");
/*  810 */     XAtom localXAtom2 = XAtom.get("_NET_WM_STRUT_PARTIAL");
/*      */ 
/*  812 */     Insets localInsets = new Insets(0, 0, 0, 0);
/*      */ 
/*  814 */     LinkedList localLinkedList = new LinkedList();
/*  815 */     localLinkedList.add(Long.valueOf(paramLong));
/*  816 */     localLinkedList.add(Integer.valueOf(0));
/*      */     int i;
/*      */     Object localObject1;
/*  817 */     while (!localLinkedList.isEmpty())
/*      */     {
/*  819 */       long l1 = ((Long)localLinkedList.remove(0)).longValue();
/*  820 */       i = ((Integer)localLinkedList.remove(0)).intValue();
/*      */ 
/*  828 */       if (XlibUtil.getWindowMapState(l1) != 0)
/*      */       {
/*  833 */         long l2 = Native.allocateLongArray(4);
/*      */         try
/*      */         {
/*  838 */           boolean bool = localXAtom2.getAtomData(l1, 6L, l2, 4);
/*  839 */           if (!bool)
/*      */           {
/*  841 */             bool = localXAtom1.getAtomData(l1, 6L, l2, 4);
/*      */           }
/*  843 */           if (bool)
/*      */           {
/*  846 */             localObject1 = XlibUtil.getWindowGeometry(l1);
/*  847 */             if (i > 1)
/*      */             {
/*  849 */               localObject1 = XlibUtil.translateCoordinates(l1, paramLong, (Rectangle)localObject1);
/*      */             }
/*      */ 
/*  854 */             if ((localObject1 != null) && (((Rectangle)localObject1).intersects(paramRectangle2)))
/*      */             {
/*  856 */               localInsets.left = Math.max((int)Native.getLong(l2, 0), localInsets.left);
/*  857 */               localInsets.right = Math.max((int)Native.getLong(l2, 1), localInsets.right);
/*  858 */               localInsets.top = Math.max((int)Native.getLong(l2, 2), localInsets.top);
/*  859 */               localInsets.bottom = Math.max((int)Native.getLong(l2, 3), localInsets.bottom);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  865 */           XlibWrapper.unsafe.freeMemory(l2);
/*      */         }
/*      */ 
/*  868 */         if (i < 3)
/*      */         {
/*  870 */           Set localSet = XlibUtil.getChildWindows(l1);
/*  871 */           for (localObject1 = localSet.iterator(); ((Iterator)localObject1).hasNext(); ) { long l3 = ((Long)((Iterator)localObject1).next()).longValue();
/*      */ 
/*  873 */             localLinkedList.add(Long.valueOf(l3));
/*  874 */             localLinkedList.add(Integer.valueOf(i + 1));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  879 */     return localInsets;
/*      */   }
/*      */ 
/*      */   protected static final Object targetToPeer(Object paramObject)
/*      */   {
/*  904 */     Object localObject = null;
/*  905 */     if ((paramObject != null) && (!GraphicsEnvironment.isHeadless())) {
/*  906 */       localObject = specialPeerMap.get(paramObject);
/*      */     }
/*  908 */     if (localObject != null) return localObject;
/*      */ 
/*  910 */     return SunToolkit.targetToPeer(paramObject);
/*      */   }
/*      */ 
/*      */   protected static final void targetDisposedPeer(Object paramObject1, Object paramObject2)
/*      */   {
/*  915 */     SunToolkit.targetDisposedPeer(paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) {
/*  919 */     return new XRobotPeer(paramGraphicsDevice.getDefaultConfiguration());
/*      */   }
/*      */ 
/*      */   public void setDynamicLayout(boolean paramBoolean)
/*      */   {
/*  930 */     dynamicLayoutSetting = paramBoolean;
/*      */   }
/*      */ 
/*      */   protected boolean isDynamicLayoutSet() {
/*  934 */     return dynamicLayoutSetting;
/*      */   }
/*      */ 
/*      */   protected boolean isDynamicLayoutSupported()
/*      */   {
/*  941 */     return XWM.getWM().supportsDynamicLayout();
/*      */   }
/*      */ 
/*      */   public boolean isDynamicLayoutActive() {
/*  945 */     return isDynamicLayoutSupported();
/*      */   }
/*      */ 
/*      */   public FontPeer getFontPeer(String paramString, int paramInt)
/*      */   {
/*  950 */     return new XFontPeer(paramString, paramInt);
/*      */   }
/*      */ 
/*      */   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException {
/*  954 */     return XDragSourceContextPeer.createDragSourceContextPeer(paramDragGestureEvent);
/*      */   }
/*      */ 
/*      */   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*      */   {
/*  964 */     if (MouseDragGestureRecognizer.class.equals(paramClass)) {
/*  965 */       return new XMouseDragGestureRecognizer(paramDragSource, paramComponent, paramInt, paramDragGestureListener);
/*      */     }
/*  967 */     return null;
/*      */   }
/*      */ 
/*      */   public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) {
/*  971 */     XCheckboxMenuItemPeer localXCheckboxMenuItemPeer = new XCheckboxMenuItemPeer(paramCheckboxMenuItem);
/*      */ 
/*  975 */     return localXCheckboxMenuItemPeer;
/*      */   }
/*      */ 
/*      */   public MenuItemPeer createMenuItem(MenuItem paramMenuItem) {
/*  979 */     XMenuItemPeer localXMenuItemPeer = new XMenuItemPeer(paramMenuItem);
/*      */ 
/*  983 */     return localXMenuItemPeer;
/*      */   }
/*      */ 
/*      */   public TextFieldPeer createTextField(TextField paramTextField) {
/*  987 */     XTextFieldPeer localXTextFieldPeer = new XTextFieldPeer(paramTextField);
/*  988 */     targetCreatedPeer(paramTextField, localXTextFieldPeer);
/*  989 */     return localXTextFieldPeer;
/*      */   }
/*      */ 
/*      */   public LabelPeer createLabel(Label paramLabel) {
/*  993 */     XLabelPeer localXLabelPeer = new XLabelPeer(paramLabel);
/*  994 */     targetCreatedPeer(paramLabel, localXLabelPeer);
/*  995 */     return localXLabelPeer;
/*      */   }
/*      */ 
/*      */   public ListPeer createList(java.awt.List paramList) {
/*  999 */     XListPeer localXListPeer = new XListPeer(paramList);
/* 1000 */     targetCreatedPeer(paramList, localXListPeer);
/* 1001 */     return localXListPeer;
/*      */   }
/*      */ 
/*      */   public CheckboxPeer createCheckbox(Checkbox paramCheckbox) {
/* 1005 */     XCheckboxPeer localXCheckboxPeer = new XCheckboxPeer(paramCheckbox);
/* 1006 */     targetCreatedPeer(paramCheckbox, localXCheckboxPeer);
/* 1007 */     return localXCheckboxPeer;
/*      */   }
/*      */ 
/*      */   public ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) {
/* 1011 */     XScrollbarPeer localXScrollbarPeer = new XScrollbarPeer(paramScrollbar);
/* 1012 */     targetCreatedPeer(paramScrollbar, localXScrollbarPeer);
/* 1013 */     return localXScrollbarPeer;
/*      */   }
/*      */ 
/*      */   public ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) {
/* 1017 */     XScrollPanePeer localXScrollPanePeer = new XScrollPanePeer(paramScrollPane);
/* 1018 */     targetCreatedPeer(paramScrollPane, localXScrollPanePeer);
/* 1019 */     return localXScrollPanePeer;
/*      */   }
/*      */ 
/*      */   public TextAreaPeer createTextArea(TextArea paramTextArea) {
/* 1023 */     XTextAreaPeer localXTextAreaPeer = new XTextAreaPeer(paramTextArea);
/* 1024 */     targetCreatedPeer(paramTextArea, localXTextAreaPeer);
/* 1025 */     return localXTextAreaPeer;
/*      */   }
/*      */ 
/*      */   public ChoicePeer createChoice(Choice paramChoice) {
/* 1029 */     XChoicePeer localXChoicePeer = new XChoicePeer(paramChoice);
/* 1030 */     targetCreatedPeer(paramChoice, localXChoicePeer);
/* 1031 */     return localXChoicePeer;
/*      */   }
/*      */ 
/*      */   public CanvasPeer createCanvas(Canvas paramCanvas) {
/* 1035 */     XCanvasPeer localXCanvasPeer = isXEmbedServerRequested() ? new XEmbedCanvasPeer(paramCanvas) : new XCanvasPeer(paramCanvas);
/* 1036 */     targetCreatedPeer(paramCanvas, localXCanvasPeer);
/* 1037 */     return localXCanvasPeer;
/*      */   }
/*      */ 
/*      */   public PanelPeer createPanel(Panel paramPanel) {
/* 1041 */     XPanelPeer localXPanelPeer = new XPanelPeer(paramPanel);
/* 1042 */     targetCreatedPeer(paramPanel, localXPanelPeer);
/* 1043 */     return localXPanelPeer;
/*      */   }
/*      */ 
/*      */   public WindowPeer createWindow(Window paramWindow) {
/* 1047 */     XWindowPeer localXWindowPeer = new XWindowPeer(paramWindow);
/* 1048 */     targetCreatedPeer(paramWindow, localXWindowPeer);
/* 1049 */     return localXWindowPeer;
/*      */   }
/*      */ 
/*      */   public DialogPeer createDialog(Dialog paramDialog) {
/* 1053 */     XDialogPeer localXDialogPeer = new XDialogPeer(paramDialog);
/* 1054 */     targetCreatedPeer(paramDialog, localXDialogPeer);
/* 1055 */     return localXDialogPeer;
/*      */   }
/*      */ 
/*      */   public static synchronized boolean getSunAwtDisableGtkFileDialogs()
/*      */   {
/* 1065 */     if (sunAwtDisableGtkFileDialogs == null) {
/* 1066 */       sunAwtDisableGtkFileDialogs = (Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.disableGtkFileDialogs"));
/*      */     }
/*      */ 
/* 1069 */     return sunAwtDisableGtkFileDialogs.booleanValue();
/*      */   }
/*      */ 
/*      */   public FileDialogPeer createFileDialog(FileDialog paramFileDialog) {
/* 1073 */     Object localObject = null;
/*      */ 
/* 1075 */     if ((!getSunAwtDisableGtkFileDialogs()) && (checkGtkVersion(2, 4, 0)))
/* 1076 */       localObject = new GtkFileDialogPeer(paramFileDialog);
/*      */     else {
/* 1078 */       localObject = new XFileDialogPeer(paramFileDialog);
/*      */     }
/* 1080 */     targetCreatedPeer(paramFileDialog, localObject);
/* 1081 */     return localObject;
/*      */   }
/*      */ 
/*      */   public MenuBarPeer createMenuBar(MenuBar paramMenuBar) {
/* 1085 */     XMenuBarPeer localXMenuBarPeer = new XMenuBarPeer(paramMenuBar);
/* 1086 */     targetCreatedPeer(paramMenuBar, localXMenuBarPeer);
/* 1087 */     return localXMenuBarPeer;
/*      */   }
/*      */ 
/*      */   public MenuPeer createMenu(Menu paramMenu) {
/* 1091 */     XMenuPeer localXMenuPeer = new XMenuPeer(paramMenu);
/*      */ 
/* 1095 */     return localXMenuPeer;
/*      */   }
/*      */ 
/*      */   public PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) {
/* 1099 */     XPopupMenuPeer localXPopupMenuPeer = new XPopupMenuPeer(paramPopupMenu);
/* 1100 */     targetCreatedPeer(paramPopupMenu, localXPopupMenuPeer);
/* 1101 */     return localXPopupMenuPeer;
/*      */   }
/*      */ 
/*      */   public synchronized MouseInfoPeer getMouseInfoPeer() {
/* 1105 */     if (xPeer == null) {
/* 1106 */       xPeer = new XMouseInfoPeer();
/*      */     }
/* 1108 */     return xPeer;
/*      */   }
/*      */ 
/*      */   public XEmbeddedFramePeer createEmbeddedFrame(XEmbeddedFrame paramXEmbeddedFrame)
/*      */   {
/* 1113 */     XEmbeddedFramePeer localXEmbeddedFramePeer = new XEmbeddedFramePeer(paramXEmbeddedFrame);
/* 1114 */     targetCreatedPeer(paramXEmbeddedFrame, localXEmbeddedFramePeer);
/* 1115 */     return localXEmbeddedFramePeer;
/*      */   }
/*      */ 
/*      */   XEmbedChildProxyPeer createEmbedProxy(XEmbedChildProxy paramXEmbedChildProxy) {
/* 1119 */     XEmbedChildProxyPeer localXEmbedChildProxyPeer = new XEmbedChildProxyPeer(paramXEmbedChildProxy);
/* 1120 */     targetCreatedPeer(paramXEmbedChildProxy, localXEmbedChildProxyPeer);
/* 1121 */     return localXEmbedChildProxyPeer;
/*      */   }
/*      */ 
/*      */   public KeyboardFocusManagerPeer createKeyboardFocusManagerPeer(KeyboardFocusManager paramKeyboardFocusManager) throws HeadlessException {
/* 1125 */     XKeyboardFocusManagerPeer localXKeyboardFocusManagerPeer = new XKeyboardFocusManagerPeer(paramKeyboardFocusManager);
/* 1126 */     return localXKeyboardFocusManagerPeer;
/*      */   }
/*      */ 
/*      */   public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString)
/*      */     throws IndexOutOfBoundsException
/*      */   {
/* 1134 */     return new XCustomCursor(paramImage, paramPoint, paramString);
/*      */   }
/*      */ 
/*      */   public TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon)
/*      */     throws HeadlessException, AWTException
/*      */   {
/* 1140 */     XTrayIconPeer localXTrayIconPeer = new XTrayIconPeer(paramTrayIcon);
/* 1141 */     targetCreatedPeer(paramTrayIcon, localXTrayIconPeer);
/* 1142 */     return localXTrayIconPeer;
/*      */   }
/*      */ 
/*      */   public SystemTrayPeer createSystemTray(SystemTray paramSystemTray) throws HeadlessException {
/* 1146 */     XSystemTrayPeer localXSystemTrayPeer = new XSystemTrayPeer(paramSystemTray);
/* 1147 */     return localXSystemTrayPeer;
/*      */   }
/*      */ 
/*      */   public boolean isTraySupported() {
/* 1151 */     XSystemTrayPeer localXSystemTrayPeer = XSystemTrayPeer.getPeerInstance();
/* 1152 */     if (localXSystemTrayPeer != null) {
/* 1153 */       return localXSystemTrayPeer.isAvailable();
/*      */     }
/* 1155 */     return false;
/*      */   }
/*      */ 
/*      */   public Dimension getBestCursorSize(int paramInt1, int paramInt2)
/*      */   {
/* 1162 */     return XCustomCursor.getBestCursorSize(Math.max(1, paramInt1), Math.max(1, paramInt2));
/*      */   }
/*      */ 
/*      */   public int getMaximumCursorColors()
/*      */   {
/* 1168 */     return 2;
/*      */   }
/*      */ 
/*      */   public Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) {
/* 1172 */     return XInputMethod.mapInputMethodHighlight(paramInputMethodHighlight);
/*      */   }
/*      */ 
/*      */   public boolean getLockingKeyState(int paramInt) {
/* 1176 */     if ((paramInt != 20) && (paramInt != 144) && (paramInt != 145) && (paramInt != 262))
/*      */     {
/* 1178 */       throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
/*      */     }
/* 1180 */     awtLock();
/*      */     try {
/* 1182 */       return getModifierState(paramInt);
/*      */     } finally {
/* 1184 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Clipboard getSystemClipboard() {
/* 1189 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1190 */     if (localSecurityManager != null) {
/* 1191 */       localSecurityManager.checkSystemClipboardAccess();
/*      */     }
/* 1193 */     synchronized (this) {
/* 1194 */       if (this.clipboard == null) {
/* 1195 */         this.clipboard = new XClipboard("System", "CLIPBOARD");
/*      */       }
/*      */     }
/* 1198 */     return this.clipboard;
/*      */   }
/*      */ 
/*      */   public Clipboard getSystemSelection() {
/* 1202 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1203 */     if (localSecurityManager != null) {
/* 1204 */       localSecurityManager.checkSystemClipboardAccess();
/*      */     }
/* 1206 */     synchronized (this) {
/* 1207 */       if (this.selection == null) {
/* 1208 */         this.selection = new XClipboard("Selection", "PRIMARY");
/*      */       }
/*      */     }
/* 1211 */     return this.selection;
/*      */   }
/*      */ 
/*      */   public void beep() {
/* 1215 */     awtLock();
/*      */     try {
/* 1217 */       XlibWrapper.XBell(getDisplay(), 0);
/* 1218 */       XlibWrapper.XFlush(getDisplay());
/*      */     } finally {
/* 1220 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties)
/*      */   {
/* 1227 */     if (paramFrame == null) {
/* 1228 */       throw new NullPointerException("frame must not be null");
/*      */     }
/*      */ 
/* 1231 */     PrintJob2D localPrintJob2D = new PrintJob2D(paramFrame, paramString, paramProperties);
/*      */ 
/* 1233 */     if (!localPrintJob2D.printDialog()) {
/* 1234 */       localPrintJob2D = null;
/*      */     }
/* 1236 */     return localPrintJob2D;
/*      */   }
/*      */ 
/*      */   public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
/*      */   {
/* 1243 */     if (paramFrame == null) {
/* 1244 */       throw new NullPointerException("frame must not be null");
/*      */     }
/*      */ 
/* 1247 */     PrintJob2D localPrintJob2D = new PrintJob2D(paramFrame, paramString, paramJobAttributes, paramPageAttributes);
/*      */ 
/* 1250 */     if (!localPrintJob2D.printDialog()) {
/* 1251 */       localPrintJob2D = null;
/*      */     }
/*      */ 
/* 1254 */     return localPrintJob2D;
/*      */   }
/*      */ 
/*      */   static void XSync() {
/* 1258 */     awtLock();
/*      */     try {
/* 1260 */       XlibWrapper.XSync(getDisplay(), 0);
/*      */     } finally {
/* 1262 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getScreenResolution() {
/* 1267 */     long l = getDisplay();
/* 1268 */     awtLock();
/*      */     try {
/* 1270 */       return (int)(XlibWrapper.DisplayWidth(l, XlibWrapper.DefaultScreen(l)) * 25.399999999999999D / XlibWrapper.DisplayWidthMM(l, XlibWrapper.DefaultScreen(l)));
/*      */     }
/*      */     finally
/*      */     {
/* 1275 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static native long getDefaultXColormap();
/*      */ 
/*      */   static native long getDefaultScreenData();
/*      */ 
/*      */   static ColorModel getStaticColorModel()
/*      */   {
/* 1285 */     if (screenmodel == null) {
/* 1286 */       screenmodel = config.getColorModel();
/*      */     }
/* 1288 */     return screenmodel;
/*      */   }
/*      */ 
/*      */   public ColorModel getColorModel() {
/* 1292 */     return getStaticColorModel();
/*      */   }
/*      */ 
/*      */   public InputMethodDescriptor getInputMethodAdapterDescriptor()
/*      */     throws AWTException
/*      */   {
/* 1299 */     return new XInputMethodDescriptor();
/*      */   }
/*      */ 
/*      */   static int getMultiClickTime() {
/* 1303 */     if (awt_multiclick_time == 0) {
/* 1304 */       initializeMultiClickTime();
/*      */     }
/* 1306 */     return awt_multiclick_time;
/*      */   }
/*      */   static void initializeMultiClickTime() {
/* 1309 */     awtLock();
/*      */     try {
/*      */       try {
/* 1312 */         String str = XlibWrapper.XGetDefault(getDisplay(), "*", "multiClickTime");
/* 1313 */         if (str != null) {
/* 1314 */           awt_multiclick_time = (int)Long.parseLong(str);
/*      */         } else {
/* 1316 */           str = XlibWrapper.XGetDefault(getDisplay(), "OpenWindows", "MultiClickTimeout");
/*      */ 
/* 1318 */           if (str != null)
/*      */           {
/* 1322 */             awt_multiclick_time = (int)Long.parseLong(str) * 100;
/*      */           }
/* 1324 */           else awt_multiclick_time = 500; 
/*      */         }
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException)
/*      */       {
/* 1328 */         awt_multiclick_time = 500;
/*      */       } catch (NullPointerException localNullPointerException) {
/* 1330 */         awt_multiclick_time = 500;
/*      */       }
/*      */     } finally {
/* 1333 */       awtUnlock();
/*      */     }
/* 1335 */     if (awt_multiclick_time == 0)
/* 1336 */       awt_multiclick_time = 500;
/*      */   }
/*      */ 
/*      */   public boolean isFrameStateSupported(int paramInt)
/*      */     throws HeadlessException
/*      */   {
/* 1343 */     if ((paramInt == 0) || (paramInt == 1)) {
/* 1344 */       return true;
/*      */     }
/* 1346 */     return XWM.getWM().supportsExtendedState(paramInt);
/*      */   }
/*      */ 
/*      */   static void dumpPeers()
/*      */   {
/* 1351 */     if (log.isLoggable(500)) {
/* 1352 */       log.fine("Mapped windows:");
/* 1353 */       Iterator localIterator = winMap.entrySet().iterator();
/*      */       Map.Entry localEntry;
/* 1354 */       while (localIterator.hasNext()) {
/* 1355 */         localEntry = (Map.Entry)localIterator.next();
/* 1356 */         log.fine(localEntry.getKey() + "->" + localEntry.getValue());
/* 1357 */         if ((localEntry.getValue() instanceof XComponentPeer)) {
/* 1358 */           Component localComponent = (Component)((XComponentPeer)localEntry.getValue()).getTarget();
/* 1359 */           log.fine("\ttarget: " + localComponent);
/*      */         }
/*      */       }
/*      */ 
/* 1363 */       SunToolkit.dumpPeers(log);
/*      */ 
/* 1365 */       log.fine("Mapped special peers:");
/* 1366 */       localIterator = specialPeerMap.entrySet().iterator();
/* 1367 */       while (localIterator.hasNext()) {
/* 1368 */         localEntry = (Map.Entry)localIterator.next();
/* 1369 */         log.fine(localEntry.getKey() + "->" + localEntry.getValue());
/*      */       }
/*      */ 
/* 1372 */       log.fine("Mapped dispatchers:");
/* 1373 */       localIterator = winToDispatcher.entrySet().iterator();
/* 1374 */       while (localIterator.hasNext()) {
/* 1375 */         localEntry = (Map.Entry)localIterator.next();
/* 1376 */         log.fine(localEntry.getKey() + "->" + localEntry.getValue());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static long getCurrentServerTime()
/*      */   {
/* 1410 */     awtLock();
/*      */     try {
/*      */       try {
/* 1413 */         if (!initialized) {
/* 1414 */           addEventDispatcher(XBaseWindow.getXAWTRootWindow().getWindow(), timeFetcher);
/*      */ 
/* 1416 */           _XA_JAVA_TIME_PROPERTY_ATOM = XAtom.get("_SUNW_JAVA_AWT_TIME");
/* 1417 */           initialized = true;
/*      */         }
/* 1419 */         timeStampUpdated = false;
/* 1420 */         XlibWrapper.XChangeProperty(getDisplay(), XBaseWindow.getXAWTRootWindow().getWindow(), _XA_JAVA_TIME_PROPERTY_ATOM.getAtom(), 4L, 32, 2, 0L, 0);
/*      */ 
/* 1425 */         XlibWrapper.XFlush(getDisplay());
/*      */ 
/* 1427 */         if (isToolkitThread()) {
/* 1428 */           XEvent localXEvent = new XEvent();
/*      */           try {
/* 1430 */             XlibWrapper.XWindowEvent(getDisplay(), XBaseWindow.getXAWTRootWindow().getWindow(), 4194304L, localXEvent.pData);
/*      */ 
/* 1434 */             timeFetcher.dispatchEvent(localXEvent);
/*      */           }
/*      */           finally {
/* 1437 */             localXEvent.dispose();
/*      */           }
/*      */         }
/*      */         else {
/* 1441 */           while (!timeStampUpdated)
/* 1442 */             awtLockWait();
/*      */         }
/*      */       }
/*      */       catch (InterruptedException localInterruptedException)
/*      */       {
/* 1447 */         if (log.isLoggable(500)) log.fine("Catched exception, timeStamp may not be correct (ie = " + localInterruptedException + ")"); 
/*      */       }
/*      */     }
/* 1450 */     finally { awtUnlock(); }
/*      */ 
/* 1452 */     return timeStamp;
/*      */   }
/*      */   protected void initializeDesktopProperties() {
/* 1455 */     this.desktopProperties.put("DnD.Autoscroll.initialDelay", Integer.valueOf(50));
/*      */ 
/* 1457 */     this.desktopProperties.put("DnD.Autoscroll.interval", Integer.valueOf(50));
/*      */ 
/* 1459 */     this.desktopProperties.put("DnD.Autoscroll.cursorHysteresis", Integer.valueOf(5));
/*      */ 
/* 1461 */     this.desktopProperties.put("Shell.shellFolderManager", "sun.awt.shell.ShellFolderManager");
/*      */ 
/* 1464 */     if (!GraphicsEnvironment.isHeadless()) {
/* 1465 */       this.desktopProperties.put("awt.multiClickInterval", Integer.valueOf(getMultiClickTime()));
/*      */ 
/* 1467 */       this.desktopProperties.put("awt.mouse.numButtons", Integer.valueOf(getNumberOfButtons()));
/*      */     }
/*      */   }
/*      */ 
/*      */   private native int getNumberOfButtonsImpl();
/*      */ 
/*      */   public int getNumberOfButtons()
/*      */   {
/* 1482 */     awtLock();
/*      */     try {
/* 1484 */       if (numberOfButtons == 0) {
/* 1485 */         numberOfButtons = getNumberOfButtonsImpl();
/* 1486 */         numberOfButtons = numberOfButtons > 20 ? 20 : numberOfButtons;
/*      */ 
/* 1491 */         if (numberOfButtons >= 5)
/* 1492 */           numberOfButtons -= 2;
/* 1493 */         else if ((numberOfButtons == 4) || (numberOfButtons == 5)) {
/* 1494 */           numberOfButtons = 3;
/*      */         }
/*      */       }
/*      */ 
/* 1498 */       return numberOfButtons;
/*      */     } finally {
/* 1500 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Object lazilyLoadDesktopProperty(String paramString)
/*      */   {
/* 1509 */     if (paramString.startsWith("DnD.Cursor.")) {
/* 1510 */       String str = paramString.substring("DnD.Cursor.".length(), paramString.length()) + ".32x32";
/*      */       try
/*      */       {
/* 1513 */         return Cursor.getSystemCustomCursor(str);
/*      */       } catch (AWTException localAWTException) {
/* 1515 */         throw new RuntimeException("cannot load system cursor: " + str, localAWTException);
/*      */       }
/*      */     }
/*      */ 
/* 1519 */     if (paramString.equals("awt.dynamicLayoutSupported")) {
/* 1520 */       return Boolean.valueOf(isDynamicLayoutSupported());
/*      */     }
/*      */ 
/* 1523 */     if (initXSettingsIfNeeded(paramString)) {
/* 1524 */       return this.desktopProperties.get(paramString);
/*      */     }
/*      */ 
/* 1527 */     return super.lazilyLoadDesktopProperty(paramString);
/*      */   }
/*      */ 
/*      */   public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
/* 1531 */     if (paramString == null)
/*      */     {
/* 1533 */       return;
/*      */     }
/* 1535 */     initXSettingsIfNeeded(paramString);
/* 1536 */     super.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   private boolean initXSettingsIfNeeded(String paramString)
/*      */   {
/* 1546 */     if ((!this.loadedXSettings) && ((paramString.startsWith("gnome.")) || (paramString.equals("awt.font.desktophints")) || (paramString.startsWith("DnD."))))
/*      */     {
/* 1551 */       this.loadedXSettings = true;
/* 1552 */       if (!GraphicsEnvironment.isHeadless()) {
/* 1553 */         loadXSettings();
/*      */ 
/* 1559 */         if (this.desktopProperties.get("awt.font.desktophints") == null) {
/* 1560 */           if (XWM.isKDE2()) {
/* 1561 */             Object localObject = FontConfigManager.getFontConfigAAHint();
/* 1562 */             if (localObject != null)
/*      */             {
/* 1567 */               this.desktopProperties.put("fontconfig/Antialias", localObject);
/*      */             }
/*      */           }
/*      */ 
/* 1571 */           this.desktopProperties.put("awt.font.desktophints", SunToolkit.getDesktopFontHints());
/*      */         }
/*      */ 
/* 1575 */         return true;
/*      */       }
/*      */     }
/* 1578 */     return false;
/*      */   }
/*      */ 
/*      */   private void loadXSettings() {
/* 1582 */     this.xs = new XAWTXSettings();
/*      */   }
/*      */ 
/*      */   void parseXSettings(int paramInt, Map paramMap)
/*      */   {
/* 1597 */     if ((paramMap == null) || (paramMap.isEmpty())) {
/* 1598 */       return;
/*      */     }
/*      */ 
/* 1601 */     Iterator localIterator = paramMap.entrySet().iterator();
/* 1602 */     while (localIterator.hasNext()) {
/* 1603 */       localObject1 = (Map.Entry)localIterator.next();
/* 1604 */       String str = (String)((Map.Entry)localObject1).getKey();
/*      */ 
/* 1606 */       str = "gnome." + str;
/* 1607 */       setDesktopProperty(str, ((Map.Entry)localObject1).getValue());
/* 1608 */       log.fine("name = " + str + " value = " + ((Map.Entry)localObject1).getValue());
/*      */     }
/*      */ 
/* 1618 */     setDesktopProperty("awt.font.desktophints", SunToolkit.getDesktopFontHints());
/*      */ 
/* 1621 */     Object localObject1 = null;
/* 1622 */     synchronized (this) {
/* 1623 */       localObject1 = (Integer)this.desktopProperties.get("gnome.Net/DndDragThreshold");
/*      */     }
/* 1625 */     if (localObject1 != null)
/* 1626 */       setDesktopProperty("DnD.gestureMotionThreshold", localObject1);
/*      */   }
/*      */ 
/*      */   static int keysymToPrimaryKeycode(long paramLong)
/*      */   {
/* 1643 */     awtLock();
/*      */     try {
/* 1645 */       int i = XlibWrapper.XKeysymToKeycode(getDisplay(), paramLong);
/* 1646 */       if (i == 0) {
/* 1647 */         return 0;
/*      */       }
/* 1649 */       long l = XlibWrapper.XKeycodeToKeysym(getDisplay(), i, 0);
/*      */       int k;
/* 1650 */       if (paramLong != l) {
/* 1651 */         return 0;
/*      */       }
/* 1653 */       return i;
/*      */     } finally {
/* 1655 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/* 1659 */   static boolean getModifierState(int paramInt) { int i = 0;
/* 1660 */     long l1 = XKeysym.javaKeycode2Keysym(paramInt);
/* 1661 */     int j = XlibWrapper.XKeysymToKeycode(getDisplay(), l1);
/* 1662 */     if (j == 0) {
/* 1663 */       return false;
/*      */     }
/* 1665 */     awtLock();
/*      */     try {
/* 1667 */       XModifierKeymap localXModifierKeymap = new XModifierKeymap(XlibWrapper.XGetModifierMapping(getDisplay()));
/*      */ 
/* 1670 */       int k = localXModifierKeymap.get_max_keypermod();
/*      */ 
/* 1672 */       long l2 = localXModifierKeymap.get_modifiermap();
/* 1673 */       for (int m = 0; m < 8; m++) {
/* 1674 */         for (int n = 0; n < k; n++) {
/* 1675 */           int i1 = Native.getUByte(l2, m * k + n);
/* 1676 */           if (i1 != 0)
/*      */           {
/* 1679 */             if (j == i1) {
/* 1680 */               i = 1 << m;
/* 1681 */               break;
/*      */             }
/*      */           }
/*      */         }
/* 1684 */         if (i != 0) {
/*      */           break;
/*      */         }
/*      */       }
/* 1688 */       XlibWrapper.XFreeModifiermap(localXModifierKeymap.pData);
/* 1689 */       if (i == 0) {
/* 1690 */         return 0;
/*      */       }
/*      */ 
/* 1696 */       long l3 = 0L;
/*      */       try
/*      */       {
/* 1699 */         l3 = ((Long)winMap.firstKey()).longValue();
/*      */       }
/*      */       catch (NoSuchElementException localNoSuchElementException) {
/* 1702 */         l3 = getDefaultRootWindow();
/*      */       }
/* 1704 */       boolean bool1 = XlibWrapper.XQueryPointer(getDisplay(), l3, XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5, XlibWrapper.larg6, XlibWrapper.larg7);
/*      */ 
/* 1712 */       int i2 = Native.getInt(XlibWrapper.larg7);
/* 1713 */       return (i2 & i) != 0;
/*      */     } finally {
/* 1715 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setupModifierMap()
/*      */   {
/* 1723 */     int i = keysymToPrimaryKeycode(65511L);
/* 1724 */     int j = keysymToPrimaryKeycode(65512L);
/* 1725 */     int k = keysymToPrimaryKeycode(65513L);
/* 1726 */     int m = keysymToPrimaryKeycode(65514L);
/* 1727 */     int n = keysymToPrimaryKeycode(65407L);
/* 1728 */     int i1 = keysymToPrimaryKeycode(65406L);
/* 1729 */     int i2 = keysymToPrimaryKeycode(65510L);
/* 1730 */     int i3 = keysymToPrimaryKeycode(65509L);
/*      */ 
/* 1732 */     int[] arrayOfInt = { 1, 2, 4, 8, 16, 32, 64, 128 };
/*      */ 
/* 1735 */     log.fine("In setupModifierMap");
/* 1736 */     awtLock();
/*      */     try {
/* 1738 */       XModifierKeymap localXModifierKeymap = new XModifierKeymap(XlibWrapper.XGetModifierMapping(getDisplay()));
/*      */ 
/* 1741 */       int i4 = localXModifierKeymap.get_max_keypermod();
/*      */ 
/* 1743 */       long l = localXModifierKeymap.get_modifiermap();
/*      */       int i6;
/* 1745 */       for (int i5 = 3; 
/* 1746 */         i5 <= 7; 
/* 1747 */         i5++)
/*      */       {
/* 1749 */         for (i6 = 0; i6 < i4; i6++)
/*      */         {
/* 1751 */           int i7 = Native.getUByte(l, i5 * i4 + i6);
/*      */ 
/* 1753 */           if (i7 == 0) {
/*      */             break;
/*      */           }
/* 1756 */           if ((metaMask == 0) && ((i7 == i) || (i7 == j)))
/*      */           {
/* 1759 */             metaMask = arrayOfInt[i5];
/* 1760 */             break;
/*      */           }
/* 1762 */           if ((altMask == 0) && ((i7 == k) || (i7 == m))) {
/* 1763 */             altMask = arrayOfInt[i5];
/* 1764 */             break;
/*      */           }
/* 1766 */           if ((numLockMask == 0) && (i7 == n)) {
/* 1767 */             numLockMask = arrayOfInt[i5];
/* 1768 */             break;
/*      */           }
/* 1770 */           if ((modeSwitchMask == 0) && (i7 == i1)) {
/* 1771 */             modeSwitchMask = arrayOfInt[i5];
/* 1772 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1777 */       modLockIsShiftLock = 0;
/* 1778 */       for (i5 = 0; i5 < i4; i5++) {
/* 1779 */         i6 = Native.getUByte(l, 1 * i4 + i5);
/* 1780 */         if (i6 == 0) {
/*      */           break;
/*      */         }
/* 1783 */         if (i6 == i2) {
/* 1784 */           modLockIsShiftLock = 1;
/*      */         }
/*      */         else {
/* 1787 */           if (i6 == i3)
/*      */             break;
/*      */         }
/*      */       }
/* 1791 */       XlibWrapper.XFreeModifiermap(localXModifierKeymap.pData);
/*      */     } finally {
/* 1793 */       awtUnlock();
/*      */     }
/* 1795 */     if (log.isLoggable(500)) {
/* 1796 */       log.fine("metaMask = " + metaMask);
/* 1797 */       log.fine("altMask = " + altMask);
/* 1798 */       log.fine("numLockMask = " + numLockMask);
/* 1799 */       log.fine("modeSwitchMask = " + modeSwitchMask);
/* 1800 */       log.fine("modLockIsShiftLock = " + modLockIsShiftLock);
/*      */     }
/*      */   }
/*      */ 
/*      */   static void remove(Runnable paramRunnable)
/*      */   {
/* 1812 */     if (paramRunnable == null) {
/* 1813 */       throw new NullPointerException("task is null");
/*      */     }
/* 1815 */     awtLock();
/*      */     try {
/* 1817 */       if (timeoutTaskLog.isLoggable(400)) {
/* 1818 */         timeoutTaskLog.finer("Removing task " + paramRunnable);
/*      */       }
/* 1820 */       if (timeoutTasks == null) {
/* 1821 */         if (timeoutTaskLog.isLoggable(400))
/* 1822 */           timeoutTaskLog.finer("Task is not scheduled");
/*      */       }
/*      */       else
/*      */       {
/* 1826 */         Collection localCollection = timeoutTasks.values();
/* 1827 */         Iterator localIterator = localCollection.iterator();
/* 1828 */         while (localIterator.hasNext()) {
/* 1829 */           java.util.List localList = (java.util.List)localIterator.next();
/* 1830 */           int i = 0;
/* 1831 */           if (localList.contains(paramRunnable)) {
/* 1832 */             localList.remove(paramRunnable);
/* 1833 */             if (!localList.isEmpty()) break;
/* 1834 */             localIterator.remove(); break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1840 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static native void wakeup_poll();
/*      */ 
/*      */   static void schedule(Runnable paramRunnable, long paramLong)
/*      */   {
/* 1859 */     if (paramRunnable == null) {
/* 1860 */       throw new NullPointerException("task is null");
/*      */     }
/* 1862 */     if (paramLong <= 0L) {
/* 1863 */       throw new IllegalArgumentException("interval " + paramLong + " is not positive");
/*      */     }
/*      */ 
/* 1866 */     awtLock();
/*      */     try {
/* 1868 */       if (timeoutTaskLog.isLoggable(400)) {
/* 1869 */         timeoutTaskLog.finer("XToolkit.schedule(): current time={0};  interval={1};  task being added={2};  tasks before addition={3}", new Object[] { Long.valueOf(System.currentTimeMillis()), Long.valueOf(paramLong), paramRunnable, timeoutTasks });
/*      */       }
/*      */ 
/* 1875 */       if (timeoutTasks == null) {
/* 1876 */         timeoutTasks = new TreeMap();
/*      */       }
/*      */ 
/* 1879 */       Long localLong = Long.valueOf(System.currentTimeMillis() + paramLong);
/* 1880 */       Object localObject1 = (java.util.List)timeoutTasks.get(localLong);
/* 1881 */       if (localObject1 == null) {
/* 1882 */         localObject1 = new ArrayList(1);
/* 1883 */         timeoutTasks.put(localLong, localObject1);
/*      */       }
/* 1885 */       ((java.util.List)localObject1).add(paramRunnable);
/*      */ 
/* 1888 */       if ((timeoutTasks.get(timeoutTasks.firstKey()) == localObject1) && (((java.util.List)localObject1).size() == 1))
/*      */       {
/* 1891 */         wakeup_poll();
/*      */       }
/*      */     } finally {
/* 1894 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private long getNextTaskTime() {
/* 1899 */     awtLock();
/*      */     try
/*      */     {
/*      */       long l;
/* 1901 */       if ((timeoutTasks == null) || (timeoutTasks.isEmpty())) {
/* 1902 */         return -1L;
/*      */       }
/* 1904 */       return ((Long)timeoutTasks.firstKey()).longValue();
/*      */     } finally {
/* 1906 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void callTimeoutTasks()
/*      */   {
/* 1915 */     if (timeoutTaskLog.isLoggable(400)) {
/* 1916 */       timeoutTaskLog.finer("XToolkit.callTimeoutTasks(): current time={0};  tasks={1}", new Object[] { Long.valueOf(System.currentTimeMillis()), timeoutTasks });
/*      */     }
/*      */ 
/* 1920 */     if ((timeoutTasks == null) || (timeoutTasks.isEmpty())) {
/* 1921 */       return;
/*      */     }
/*      */ 
/* 1924 */     Long localLong1 = Long.valueOf(System.currentTimeMillis());
/* 1925 */     Long localLong2 = (Long)timeoutTasks.firstKey();
/*      */ 
/* 1927 */     while (localLong2.compareTo(localLong1) <= 0) {
/* 1928 */       java.util.List localList = (java.util.List)timeoutTasks.remove(localLong2);
/*      */ 
/* 1930 */       for (Iterator localIterator = localList.iterator(); localIterator.hasNext(); ) {
/* 1931 */         Runnable localRunnable = (Runnable)localIterator.next();
/*      */ 
/* 1933 */         if (timeoutTaskLog.isLoggable(400)) {
/* 1934 */           timeoutTaskLog.finer("XToolkit.callTimeoutTasks(): current time={0};  about to run task={1}", new Object[] { Long.valueOf(localLong1.longValue()), localRunnable });
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1939 */           localRunnable.run();
/*      */         } catch (ThreadDeath localThreadDeath) {
/* 1941 */           throw localThreadDeath;
/*      */         } catch (Throwable localThrowable) {
/* 1943 */           processException(localThrowable);
/*      */         }
/*      */       }
/*      */ 
/* 1947 */       if (timeoutTasks.isEmpty()) {
/*      */         break;
/*      */       }
/* 1950 */       localLong2 = (Long)timeoutTasks.firstKey();
/*      */     }
/*      */   }
/*      */ 
/*      */   static long getAwtDefaultFg() {
/* 1955 */     return awt_defaultFg;
/*      */   }
/*      */ 
/*      */   static boolean isLeftMouseButton(MouseEvent paramMouseEvent) {
/* 1959 */     switch (paramMouseEvent.getID()) {
/*      */     case 501:
/*      */     case 502:
/* 1962 */       return paramMouseEvent.getButton() == 1;
/*      */     case 500:
/*      */     case 504:
/*      */     case 505:
/*      */     case 506:
/* 1967 */       return (paramMouseEvent.getModifiersEx() & 0x400) != 0;
/*      */     case 503:
/* 1969 */     }return false;
/*      */   }
/*      */ 
/*      */   static boolean isRightMouseButton(MouseEvent paramMouseEvent) {
/* 1973 */     int i = ((Integer)getDefaultToolkit().getDesktopProperty("awt.mouse.numButtons")).intValue();
/* 1974 */     switch (paramMouseEvent.getID()) {
/*      */     case 501:
/*      */     case 502:
/* 1977 */       return ((i == 2) && (paramMouseEvent.getButton() == 2)) || ((i > 2) && (paramMouseEvent.getButton() == 3));
/*      */     case 500:
/*      */     case 504:
/*      */     case 505:
/*      */     case 506:
/* 1983 */       return ((i == 2) && ((paramMouseEvent.getModifiersEx() & 0x800) != 0)) || ((i > 2) && ((paramMouseEvent.getModifiersEx() & 0x1000) != 0));
/*      */     case 503:
/*      */     }
/* 1986 */     return false;
/*      */   }
/*      */ 
/*      */   static long nowMillisUTC_offset(long paramLong)
/*      */   {
/* 2007 */     long l = System.currentTimeMillis();
/* 2008 */     if (log.isLoggable(400)) {
/* 2009 */       log.finer("reset_time=" + reset_time_utc + ", current_time=" + l + ", server_offset=" + paramLong + ", wrap_time=" + 4294967295L);
/*      */     }
/*      */ 
/* 2013 */     if (l - reset_time_utc > 4294967295L) {
/* 2014 */       reset_time_utc = System.currentTimeMillis() - getCurrentServerTime();
/*      */     }
/*      */ 
/* 2017 */     if (log.isLoggable(400)) {
/* 2018 */       log.finer("result = " + (reset_time_utc + paramLong));
/*      */     }
/* 2020 */     return reset_time_utc + paramLong;
/*      */   }
/*      */ 
/*      */   protected boolean needsXEmbedImpl()
/*      */   {
/* 2029 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType) {
/* 2033 */     return (paramModalityType == null) || (paramModalityType == Dialog.ModalityType.MODELESS) || (paramModalityType == Dialog.ModalityType.DOCUMENT_MODAL) || (paramModalityType == Dialog.ModalityType.APPLICATION_MODAL) || (paramModalityType == Dialog.ModalityType.TOOLKIT_MODAL);
/*      */   }
/*      */ 
/*      */   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType)
/*      */   {
/* 2041 */     return (paramModalExclusionType == null) || (paramModalExclusionType == Dialog.ModalExclusionType.NO_EXCLUDE) || (paramModalExclusionType == Dialog.ModalExclusionType.APPLICATION_EXCLUDE) || (paramModalExclusionType == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
/*      */   }
/*      */ 
/*      */   static EventQueue getEventQueue(Object paramObject)
/*      */   {
/* 2048 */     AppContext localAppContext = targetToAppContext(paramObject);
/* 2049 */     if (localAppContext != null) {
/* 2050 */       return (EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY);
/*      */     }
/* 2052 */     return null;
/*      */   }
/*      */ 
/*      */   static void removeSourceEvents(EventQueue paramEventQueue, Object paramObject, boolean paramBoolean)
/*      */   {
/* 2058 */     AWTAccessor.getEventQueueAccessor().removeSourceEvents(paramEventQueue, paramObject, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean isAlwaysOnTopSupported()
/*      */   {
/* 2063 */     for (XLayerProtocol localXLayerProtocol : XWM.getWM().getProtocols(XLayerProtocol.class)) {
/* 2064 */       if (localXLayerProtocol.supportsLayer(1)) {
/* 2065 */         return true;
/*      */       }
/*      */     }
/* 2068 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean useBufferPerWindow() {
/* 2072 */     return getBackingStoreType() == 0;
/*      */   }
/*      */ 
/*      */   static int getBackingStoreType()
/*      */   {
/* 2087 */     return backingStoreType;
/*      */   }
/*      */ 
/*      */   private static void setBackingStoreType() {
/* 2091 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.awt.backingStore"));
/*      */ 
/* 2094 */     if (str == null) {
/* 2095 */       backingStoreType = 0;
/* 2096 */       if (backingStoreLog.isLoggable(700)) {
/* 2097 */         backingStoreLog.config("The system property sun.awt.backingStore is not set, by default backingStore=NotUseful");
/*      */       }
/*      */ 
/* 2100 */       return;
/*      */     }
/*      */ 
/* 2103 */     if (backingStoreLog.isLoggable(700)) {
/* 2104 */       backingStoreLog.config("The system property sun.awt.backingStore is " + str);
/*      */     }
/* 2106 */     str = str.toLowerCase();
/* 2107 */     if (str.equals("always"))
/* 2108 */       backingStoreType = 2;
/* 2109 */     else if (str.equals("whenmapped"))
/* 2110 */       backingStoreType = 1;
/*      */     else {
/* 2112 */       backingStoreType = 0;
/*      */     }
/*      */ 
/* 2115 */     if (backingStoreLog.isLoggable(700)) {
/* 2116 */       backingStoreLog.config("backingStore(as provided by the system property)=" + (backingStoreType == 1 ? "WhenMapped" : backingStoreType == 0 ? "NotUseful" : "Always"));
/*      */     }
/*      */ 
/* 2122 */     if (X11SurfaceData.isDgaAvailable()) {
/* 2123 */       backingStoreType = 0;
/*      */ 
/* 2125 */       if (backingStoreLog.isLoggable(700)) {
/* 2126 */         backingStoreLog.config("DGA is available, backingStore=NotUseful");
/*      */       }
/*      */ 
/* 2129 */       return;
/*      */     }
/*      */ 
/* 2132 */     awtLock();
/*      */     try {
/* 2134 */       int i = XlibWrapper.ScreenCount(getDisplay());
/* 2135 */       for (j = 0; j < i; ) {
/* 2136 */         if (XlibWrapper.DoesBackingStore(XlibWrapper.ScreenOfDisplay(getDisplay(), j)) == 0)
/*      */         {
/* 2138 */           backingStoreType = 0;
/*      */ 
/* 2140 */           if (backingStoreLog.isLoggable(700))
/* 2141 */             backingStoreLog.config("Backing store is not available on the screen " + j + ", backingStore=NotUseful");
/*      */           return;
/*      */         }
/* 2135 */         j++;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*      */       int j;
/* 2149 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean isXsunKPBehavior()
/*      */   {
/* 2180 */     awtLock();
/*      */     try {
/* 2182 */       if (awt_IsXsunKPBehavior == 0) {
/* 2183 */         if (XlibWrapper.IsXsunKPBehavior(getDisplay()))
/* 2184 */           awt_IsXsunKPBehavior = 1;
/*      */         else {
/* 2186 */           awt_IsXsunKPBehavior = 2;
/*      */         }
/*      */       }
/* 2189 */       return awt_IsXsunKPBehavior == 1;
/*      */     } finally {
/* 2191 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void resetKeyboardSniffer()
/*      */   {
/* 2198 */     sunOrNotKeyboard = 0;
/* 2199 */     kanaOrNotKeyboard = 0;
/*      */   }
/*      */   static boolean isSunKeyboard() {
/* 2202 */     if (sunOrNotKeyboard == 0) {
/* 2203 */       if (XlibWrapper.IsSunKeyboard(getDisplay()))
/* 2204 */         sunOrNotKeyboard = 1;
/*      */       else {
/* 2206 */         sunOrNotKeyboard = 2;
/*      */       }
/*      */     }
/* 2209 */     return sunOrNotKeyboard == 1;
/*      */   }
/*      */   static boolean isKanaKeyboard() {
/* 2212 */     if (kanaOrNotKeyboard == 0) {
/* 2213 */       if (XlibWrapper.IsKanaKeyboard(getDisplay()))
/* 2214 */         kanaOrNotKeyboard = 1;
/*      */       else {
/* 2216 */         kanaOrNotKeyboard = 2;
/*      */       }
/*      */     }
/* 2219 */     return kanaOrNotKeyboard == 1;
/*      */   }
/*      */   static boolean isXKBenabled() {
/* 2222 */     awtLock();
/*      */     try {
/* 2224 */       return awt_UseXKB;
/*      */     } finally {
/* 2226 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static boolean tryXKB()
/*      */   {
/* 2235 */     awtLock();
/*      */     try {
/* 2237 */       String str = "XKEYBOARD";
/*      */ 
/* 2239 */       awt_UseXKB = XlibWrapper.XQueryExtension(getDisplay(), str, XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3);
/* 2240 */       if (awt_UseXKB)
/*      */       {
/* 2244 */         awt_UseXKB_Calls = XlibWrapper.XkbLibraryVersion(XlibWrapper.larg1, XlibWrapper.larg2);
/* 2245 */         if (awt_UseXKB_Calls) {
/* 2246 */           awt_UseXKB_Calls = XlibWrapper.XkbQueryExtension(getDisplay(), XlibWrapper.larg1, XlibWrapper.larg2, XlibWrapper.larg3, XlibWrapper.larg4, XlibWrapper.larg5);
/*      */ 
/* 2248 */           if (awt_UseXKB_Calls) {
/* 2249 */             awt_XKBBaseEventCode = Native.getInt(XlibWrapper.larg2);
/* 2250 */             XlibWrapper.XkbSelectEvents(getDisplay(), 256L, 3L, 3L);
/*      */ 
/* 2259 */             XlibWrapper.XkbSelectEventDetails(getDisplay(), 256L, 2L, 16L, 16L);
/*      */ 
/* 2264 */             awt_XKBDescPtr = XlibWrapper.XkbGetMap(getDisplay(), 71L, 256L);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2273 */       return awt_UseXKB;
/*      */     } finally {
/* 2275 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/* 2279 */   static boolean canUseXKBCalls() { awtLock();
/*      */     try {
/* 2281 */       return awt_UseXKB_Calls;
/*      */     } finally {
/* 2283 */       awtUnlock();
/*      */     } }
/*      */ 
/*      */   static int getXKBEffectiveGroup() {
/* 2287 */     awtLock();
/*      */     try {
/* 2289 */       return awt_XKBEffectiveGroup;
/*      */     } finally {
/* 2291 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/* 2295 */   static int getXKBBaseEventCode() { awtLock();
/*      */     try {
/* 2297 */       return awt_XKBBaseEventCode;
/*      */     } finally {
/* 2299 */       awtUnlock();
/*      */     } }
/*      */ 
/*      */   static long getXKBKbdDesc() {
/* 2303 */     awtLock();
/*      */     try {
/* 2305 */       return awt_XKBDescPtr;
/*      */     } finally {
/* 2307 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/* 2311 */   void freeXKB() { awtLock();
/*      */     try {
/* 2313 */       if ((awt_UseXKB_Calls) && (awt_XKBDescPtr != 0L)) {
/* 2314 */         XlibWrapper.XkbFreeKeyboard(awt_XKBDescPtr, 255L, true);
/* 2315 */         awt_XKBDescPtr = 0L;
/*      */       }
/*      */     } finally {
/* 2318 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processXkbChanges(XEvent paramXEvent)
/*      */   {
/* 2325 */     XkbEvent localXkbEvent = new XkbEvent(paramXEvent.getPData());
/* 2326 */     int i = localXkbEvent.get_any().get_xkb_type();
/* 2327 */     switch (i) {
/*      */     case 0:
/* 2329 */       if (awt_XKBDescPtr != 0L) {
/* 2330 */         freeXKB();
/*      */       }
/* 2332 */       awt_XKBDescPtr = XlibWrapper.XkbGetMap(getDisplay(), 71L, 256L);
/*      */ 
/* 2339 */       break;
/*      */     case 1:
/* 2342 */       XlibWrapper.XkbGetUpdatedMap(getDisplay(), 71L, awt_XKBDescPtr);
/*      */ 
/* 2349 */       break;
/*      */     case 2:
/* 2353 */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static long getEventNumber()
/*      */   {
/* 2362 */     awtLock();
/*      */     try {
/* 2364 */       return eventNumber;
/*      */     } finally {
/* 2366 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean syncNativeQueue(long paramLong)
/*      */   {
/* 2380 */     XRootWindow localXRootWindow = XBaseWindow.getXAWTRootWindow();
/*      */ 
/* 2382 */     if (oops_waiter == null) {
/* 2383 */       oops_waiter = new XEventDispatcher() {
/*      */         public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 2385 */           if (paramAnonymousXEvent.get_type() == 31) {
/* 2386 */             XSelectionEvent localXSelectionEvent = paramAnonymousXEvent.get_xselection();
/* 2387 */             if (localXSelectionEvent.get_property() == XToolkit.this.oops.getAtom()) {
/* 2388 */               XToolkit.access$502(true);
/* 2389 */               SunToolkit.awtLockNotifyAll();
/* 2390 */             } else if ((localXSelectionEvent.get_selection() == XAtom.get("WM_S0").getAtom()) && (localXSelectionEvent.get_target() == XAtom.get("VERSION").getAtom()) && (localXSelectionEvent.get_property() == 0L) && (XlibWrapper.XGetSelectionOwner(XToolkit.getDisplay(), XAtom.get("WM_S0").getAtom()) == 0L))
/*      */             {
/* 2396 */               XToolkit.access$602(true);
/* 2397 */               SunToolkit.awtLockNotifyAll();
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       };
/*      */     }
/*      */ 
/* 2405 */     if (this.oops == null) {
/* 2406 */       this.oops = XAtom.get("OOPS");
/*      */     }
/*      */ 
/* 2409 */     awtLock();
/*      */     try {
/* 2411 */       addEventDispatcher(localXRootWindow.getWindow(), oops_waiter);
/*      */ 
/* 2413 */       oops_updated = false;
/* 2414 */       oops_failed = false;
/*      */ 
/* 2416 */       long l1 = getEventNumber();
/* 2417 */       XAtom localXAtom = XAtom.get("WM_S0");
/* 2418 */       eventLog.finer("WM_S0 selection owner {0}", new Object[] { Long.valueOf(XlibWrapper.XGetSelectionOwner(getDisplay(), localXAtom.getAtom())) });
/* 2419 */       XlibWrapper.XConvertSelection(getDisplay(), localXAtom.getAtom(), XAtom.get("VERSION").getAtom(), this.oops.getAtom(), localXRootWindow.getWindow(), 0L);
/*      */ 
/* 2422 */       XSync();
/*      */ 
/* 2425 */       eventLog.finer("Requested OOPS");
/*      */ 
/* 2427 */       long l2 = System.currentTimeMillis();
/* 2428 */       while ((!oops_updated) && (!oops_failed)) {
/*      */         try {
/* 2430 */           awtLockWait(paramLong);
/*      */         } catch (InterruptedException localInterruptedException1) {
/* 2432 */           throw new RuntimeException(localInterruptedException1);
/*      */         }
/*      */ 
/* 2436 */         if ((System.currentTimeMillis() - l2 > paramLong) && (paramLong >= 0L)) {
/* 2437 */           throw new SunToolkit.OperationTimedOut(Long.toString(System.currentTimeMillis() - l2));
/*      */         }
/*      */       }
/* 2440 */       if ((oops_failed) && (getEventNumber() - l1 == 1L))
/*      */       {
/* 2443 */         awtUnlock();
/* 2444 */         eventLog.finest("Emergency sleep");
/*      */         try {
/* 2446 */           Thread.sleep(100L);
/*      */         } catch (InterruptedException localInterruptedException2) {
/* 2448 */           throw new RuntimeException(localInterruptedException2);
/*      */         } finally {
/* 2450 */           awtLock();
/*      */         }
/*      */       }
/* 2453 */       return getEventNumber() - l1 > 2L;
/*      */     } finally {
/* 2455 */       removeEventDispatcher(localXRootWindow.getWindow(), oops_waiter);
/* 2456 */       eventLog.finer("Exiting syncNativeQueue");
/* 2457 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/* 2461 */   public void grab(Window paramWindow) { if (paramWindow.getPeer() != null)
/* 2462 */       ((XWindowPeer)paramWindow.getPeer()).setGrab(true);
/*      */   }
/*      */ 
/*      */   public void ungrab(Window paramWindow)
/*      */   {
/* 2467 */     if (paramWindow.getPeer() != null)
/* 2468 */       ((XWindowPeer)paramWindow.getPeer()).setGrab(false);
/*      */   }
/*      */ 
/*      */   public boolean isDesktopSupported()
/*      */   {
/* 2479 */     return XDesktopPeer.isDesktopSupported();
/*      */   }
/*      */ 
/*      */   public DesktopPeer createDesktopPeer(Desktop paramDesktop) {
/* 2483 */     return new XDesktopPeer();
/*      */   }
/*      */ 
/*      */   public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
/* 2487 */     return areExtraMouseButtonsEnabled;
/*      */   }
/*      */ 
/*      */   public boolean isWindowOpacitySupported()
/*      */   {
/* 2492 */     XNETProtocol localXNETProtocol = XWM.getWM().getNETProtocol();
/*      */ 
/* 2494 */     if (localXNETProtocol == null) {
/* 2495 */       return false;
/*      */     }
/*      */ 
/* 2498 */     return localXNETProtocol.doOpacityProtocol();
/*      */   }
/*      */ 
/*      */   public boolean isWindowShapingSupported()
/*      */   {
/* 2503 */     return XlibUtil.isShapingSupported();
/*      */   }
/*      */ 
/*      */   public boolean isWindowTranslucencySupported()
/*      */   {
/* 2511 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isTranslucencyCapable(GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/* 2516 */     if (!(paramGraphicsConfiguration instanceof X11GraphicsConfig)) {
/* 2517 */       return false;
/*      */     }
/* 2519 */     return ((X11GraphicsConfig)paramGraphicsConfiguration).isTranslucencyCapable();
/*      */   }
/*      */ 
/*      */   public static boolean getSunAwtDisableGrab()
/*      */   {
/* 2527 */     return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.disablegrab"))).booleanValue();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  115 */     initSecurityWarning();
/*  116 */     if (GraphicsEnvironment.isHeadless()) {
/*  117 */       config = null;
/*      */     } else {
/*  119 */       localEnv = (X11GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
/*      */ 
/*  121 */       device = (X11GraphicsDevice)localEnv.getDefaultScreenDevice();
/*  122 */       config = (X11GraphicsConfig)device.getDefaultConfiguration();
/*  123 */       if (device != null) {
/*  124 */         _display = device.getDisplay();
/*      */       }
/*  126 */       setupModifierMap();
/*  127 */       initIDs();
/*  128 */       setBackingStoreType();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface XEventListener
/*      */   {
/*      */     public abstract void eventProcessed(XEvent paramXEvent);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XToolkit
 * JD-Core Version:    0.6.2
 */