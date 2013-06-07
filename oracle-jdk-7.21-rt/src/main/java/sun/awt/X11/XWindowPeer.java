/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.Window.Type;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.awt.peer.WindowPeer;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.AWTAccessor.WindowAccessor;
/*      */ import sun.awt.DisplayChangedListener;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.UngrabEvent;
/*      */ import sun.awt.X11GraphicsDevice;
/*      */ import sun.awt.X11GraphicsEnvironment;
/*      */ import sun.java2d.pipe.Region;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ class XWindowPeer extends XPanelPeer
/*      */   implements WindowPeer, DisplayChangedListener
/*      */ {
/*   64 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XWindowPeer");
/*   65 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.X11.focus.XWindowPeer");
/*   66 */   private static final PlatformLogger insLog = PlatformLogger.getLogger("sun.awt.X11.insets.XWindowPeer");
/*   67 */   private static final PlatformLogger grabLog = PlatformLogger.getLogger("sun.awt.X11.grab.XWindowPeer");
/*   68 */   private static final PlatformLogger iconLog = PlatformLogger.getLogger("sun.awt.X11.icon.XWindowPeer");
/*      */ 
/*   71 */   private static Set<XWindowPeer> windows = new HashSet();
/*      */   private boolean cachedFocusableWindow;
/*      */   XWarningWindow warningWindow;
/*      */   private boolean alwaysOnTop;
/*      */   private boolean locationByPlatform;
/*      */   Dialog modalBlocker;
/*   81 */   boolean delayedModalBlocking = false;
/*   82 */   Dimension targetMinimumSize = null;
/*      */   private XWindowPeer ownerPeer;
/*      */   protected XWindowPeer prevTransientFor;
/*      */   protected XWindowPeer nextTransientFor;
/*      */   private XWindowPeer curRealTransientFor;
/*   91 */   private boolean grab = false;
/*      */ 
/*   93 */   private boolean isMapped = false;
/*   94 */   private boolean mustControlStackPosition = false;
/*   95 */   private XEventDispatcher rootPropertyEventDispatcher = null;
/*      */ 
/*   97 */   private static final AtomicBoolean isStartupNotificationRemoved = new AtomicBoolean();
/*      */ 
/*  102 */   private boolean isUnhiding = false;
/*  103 */   private boolean isBeforeFirstMapNotify = false;
/*      */ 
/*  112 */   private Window.Type windowType = Window.Type.NORMAL;
/*      */ 
/*  119 */   protected Vector<ToplevelStateListener> toplevelStateListeners = new Vector();
/*      */   private static final int PREFERRED_SIZE_FOR_ICON = 128;
/*      */   private static final int MAXIMUM_BUFFER_LENGTH_NET_WM_ICON = 65535;
/*      */   private static ArrayList<XIconInfo> defaultIconInfo;
/* 1909 */   private int dropTargetCount = 0;
/*      */   XAtom XA_NET_WM_STATE;
/*      */   XAtomList net_wm_state;
/* 2105 */   private XBaseWindow pressTarget = this;
/*      */ 
/*      */   public final Window.Type getWindowType()
/*      */   {
/*  115 */     return this.windowType;
/*      */   }
/*      */ 
/*      */   XWindowPeer(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  121 */     super(paramXCreateWindowParams.putIfNull("parent window", Long.valueOf(0L)));
/*      */   }
/*      */ 
/*      */   XWindowPeer(Window paramWindow) {
/*  125 */     super(new XCreateWindowParams(new Object[] { "target", paramWindow, "parent window", Long.valueOf(0L) }));
/*      */   }
/*      */ 
/*      */   void preInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  148 */     this.target = ((Component)paramXCreateWindowParams.get("target"));
/*  149 */     this.windowType = ((Window)this.target).getType();
/*  150 */     paramXCreateWindowParams.put("reparented", Boolean.valueOf((isOverrideRedirect()) || (isSimpleWindow())));
/*      */ 
/*  152 */     super.preInit(paramXCreateWindowParams);
/*  153 */     paramXCreateWindowParams.putIfNull("bit gravity", Integer.valueOf(1));
/*      */ 
/*  155 */     long l = 0L;
/*  156 */     if (paramXCreateWindowParams.containsKey("event mask")) {
/*  157 */       l = ((Long)paramXCreateWindowParams.get("event mask")).longValue();
/*      */     }
/*  159 */     l |= 65536L;
/*  160 */     paramXCreateWindowParams.put("event mask", Long.valueOf(l));
/*      */ 
/*  162 */     this.XA_NET_WM_STATE = XAtom.get("_NET_WM_STATE");
/*      */ 
/*  165 */     paramXCreateWindowParams.put("overrideRedirect", Boolean.valueOf(isOverrideRedirect()));
/*      */ 
/*  167 */     SunToolkit.awtLock();
/*      */     try {
/*  169 */       windows.add(this);
/*      */     } finally {
/*  171 */       SunToolkit.awtUnlock();
/*      */     }
/*      */ 
/*  174 */     this.cachedFocusableWindow = isFocusableWindow();
/*      */ 
/*  176 */     Font localFont = this.target.getFont();
/*  177 */     if (localFont == null) {
/*  178 */       localFont = XWindow.getDefaultFont();
/*  179 */       this.target.setFont(localFont);
/*      */     }
/*      */ 
/*  183 */     Color localColor = this.target.getBackground();
/*  184 */     if (localColor == null) {
/*  185 */       localObject2 = SystemColor.window;
/*  186 */       this.target.setBackground((Color)localObject2);
/*      */     }
/*      */ 
/*  190 */     localColor = this.target.getForeground();
/*  191 */     if (localColor == null) {
/*  192 */       this.target.setForeground(SystemColor.windowText);
/*      */     }
/*      */ 
/*  197 */     this.alwaysOnTop = ((((Window)this.target).isAlwaysOnTop()) && (((Window)this.target).isAlwaysOnTopSupported()));
/*      */ 
/*  199 */     Object localObject2 = getGraphicsConfiguration();
/*  200 */     ((X11GraphicsDevice)((GraphicsConfiguration)localObject2).getDevice()).addDisplayChangedListener(this);
/*      */   }
/*      */ 
/*      */   protected String getWMName() {
/*  204 */     String str = this.target.getName();
/*  205 */     if ((str == null) || (str.trim().equals(""))) {
/*  206 */       str = " ";
/*      */     }
/*  208 */     return str;
/*      */   }
/*      */   private static native String getLocalHostname();
/*      */ 
/*      */   private static native int getJvmPID();
/*      */ 
/*      */   void postInit(XCreateWindowParams paramXCreateWindowParams) {
/*  215 */     super.postInit(paramXCreateWindowParams);
/*      */ 
/*  218 */     initWMProtocols();
/*      */ 
/*  221 */     XAtom.get("WM_CLIENT_MACHINE").setProperty(getWindow(), getLocalHostname());
/*  222 */     XAtom.get("_NET_WM_PID").setCard32Property(getWindow(), getJvmPID());
/*      */ 
/*  225 */     Window localWindow1 = (Window)this.target;
/*  226 */     Window localWindow2 = localWindow1.getOwner();
/*  227 */     if (localWindow2 != null) {
/*  228 */       this.ownerPeer = ((XWindowPeer)localWindow2.getPeer());
/*  229 */       if (focusLog.isLoggable(400)) {
/*  230 */         focusLog.fine("Owner is " + localWindow2);
/*  231 */         focusLog.fine("Owner peer is " + this.ownerPeer);
/*  232 */         focusLog.fine("Owner X window " + Long.toHexString(this.ownerPeer.getWindow()));
/*  233 */         focusLog.fine("Owner content X window " + Long.toHexString(this.ownerPeer.getContentWindow()));
/*      */       }
/*      */ 
/*  237 */       long l = this.ownerPeer.getWindow();
/*  238 */       if (l != 0L) {
/*  239 */         XToolkit.awtLock();
/*      */         try
/*      */         {
/*  242 */           if (focusLog.isLoggable(500)) focusLog.fine("Setting transient on " + Long.toHexString(getWindow()) + " for " + Long.toHexString(l));
/*      */ 
/*  244 */           setToplevelTransientFor(this, this.ownerPeer, false, true);
/*      */ 
/*  247 */           XWMHints localXWMHints = getWMHints();
/*  248 */           localXWMHints.set_flags(localXWMHints.get_flags() | 0x40);
/*  249 */           localXWMHints.set_window_group(l);
/*  250 */           XlibWrapper.XSetWMHints(XToolkit.getDisplay(), getWindow(), localXWMHints.pData);
/*      */         }
/*      */         finally {
/*  253 */           XToolkit.awtUnlock();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  258 */     if ((localWindow2 != null) || (isSimpleWindow())) {
/*  259 */       XToolkit.awtLock();
/*      */       try {
/*  261 */         XNETProtocol localXNETProtocol = XWM.getWM().getNETProtocol();
/*  262 */         if ((localXNETProtocol != null) && (localXNETProtocol.active())) {
/*  263 */           XAtomList localXAtomList = getNETWMState();
/*  264 */           localXAtomList.add(localXNETProtocol.XA_NET_WM_STATE_SKIP_TASKBAR);
/*  265 */           setNETWMState(localXAtomList);
/*      */         }
/*      */       } finally {
/*  268 */         XToolkit.awtUnlock();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  273 */     if (((Window)this.target).getWarningString() != null)
/*      */     {
/*  276 */       if (!AWTAccessor.getWindowAccessor().isTrayIconWindow((Window)this.target)) {
/*  277 */         this.warningWindow = new XWarningWindow((Window)this.target, getWindow(), this);
/*      */       }
/*      */     }
/*      */ 
/*  281 */     setSaveUnder(true);
/*      */ 
/*  283 */     updateIconImages();
/*      */ 
/*  285 */     updateShape();
/*  286 */     updateOpacity();
/*      */   }
/*      */ 
/*      */   public void updateIconImages()
/*      */   {
/*  291 */     Window localWindow = (Window)this.target;
/*  292 */     List localList = localWindow.getIconImages();
/*  293 */     XWindowPeer localXWindowPeer = getOwnerPeer();
/*  294 */     this.winAttr.icons = new ArrayList();
/*      */     Iterator localIterator;
/*  295 */     if (localList.size() != 0)
/*      */     {
/*  297 */       this.winAttr.iconsInherited = false;
/*  298 */       for (localIterator = localList.iterator(); localIterator.hasNext(); ) {
/*  299 */         Image localImage = (Image)localIterator.next();
/*  300 */         if (localImage == null) {
/*  301 */           if (log.isLoggable(300))
/*  302 */             log.finest("XWindowPeer.updateIconImages: Skipping the image passed into Java because it's null.");
/*      */         }
/*      */         else
/*      */         {
/*      */           XIconInfo localXIconInfo;
/*      */           try {
/*  308 */             localXIconInfo = new XIconInfo(localImage);
/*      */           } catch (Exception localException) {
/*  310 */             if (log.isLoggable(300))
/*  311 */               log.finest("XWindowPeer.updateIconImages: Perhaps the image passed into Java is broken. Skipping this icon.");
/*      */           }
/*  313 */           continue;
/*      */ 
/*  315 */           if (localXIconInfo.isValid()) {
/*  316 */             this.winAttr.icons.add(localXIconInfo);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  322 */     this.winAttr.icons = normalizeIconImages(this.winAttr.icons);
/*      */ 
/*  324 */     if (this.winAttr.icons.size() == 0)
/*      */     {
/*  326 */       if (localXWindowPeer != null)
/*      */       {
/*  328 */         this.winAttr.iconsInherited = true;
/*  329 */         this.winAttr.icons = localXWindowPeer.getIconInfo();
/*      */       }
/*      */       else {
/*  332 */         this.winAttr.iconsInherited = false;
/*  333 */         this.winAttr.icons = getDefaultIconInfo();
/*      */       }
/*      */     }
/*  336 */     recursivelySetIcon(this.winAttr.icons);
/*      */   }
/*      */ 
/*      */   static List<XIconInfo> normalizeIconImages(List<XIconInfo> paramList)
/*      */   {
/*  347 */     ArrayList localArrayList = new ArrayList();
/*  348 */     int i = 0;
/*  349 */     int j = 0;
/*      */ 
/*  351 */     for (XIconInfo localXIconInfo : paramList) {
/*  352 */       int k = localXIconInfo.getWidth();
/*  353 */       int m = localXIconInfo.getHeight();
/*  354 */       int n = localXIconInfo.getRawLength();
/*      */ 
/*  356 */       if ((k > 128) || (m > 128)) {
/*  357 */         if (j == 0)
/*      */         {
/*  360 */           int i1 = k;
/*  361 */           int i2 = m;
/*  362 */           while ((i1 > 128) || (i2 > 128))
/*      */           {
/*  364 */             i1 /= 2;
/*  365 */             i2 /= 2;
/*      */           }
/*      */ 
/*  368 */           localXIconInfo.setScaledSize(i1, i2);
/*  369 */           n = localXIconInfo.getRawLength();
/*      */         }
/*      */       }
/*  372 */       else if (i + n <= 65535) {
/*  373 */         i += n;
/*  374 */         localArrayList.add(localXIconInfo);
/*  375 */         if ((k > 128) || (m > 128)) {
/*  376 */           j = 1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  381 */     if (iconLog.isLoggable(300)) {
/*  382 */       iconLog.finest(">>> Length_ of buffer of icons data: " + i + ", maximum length: " + 65535);
/*      */     }
/*      */ 
/*  386 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   static void dumpIcons(List<XIconInfo> paramList)
/*      */   {
/*      */     Iterator localIterator;
/*  393 */     if (iconLog.isLoggable(300)) {
/*  394 */       iconLog.finest(">>> Sizes of icon images:");
/*  395 */       for (localIterator = paramList.iterator(); localIterator.hasNext(); )
/*  396 */         iconLog.finest("    {0}", new Object[] { localIterator.next() });
/*      */     }
/*      */   }
/*      */ 
/*      */   public void recursivelySetIcon(List<XIconInfo> paramList)
/*      */   {
/*  402 */     dumpIcons(this.winAttr.icons);
/*  403 */     setIconHints(paramList);
/*  404 */     Window localWindow = (Window)this.target;
/*  405 */     Window[] arrayOfWindow = localWindow.getOwnedWindows();
/*  406 */     int i = arrayOfWindow.length;
/*  407 */     for (int j = 0; j < i; j++) {
/*  408 */       ComponentPeer localComponentPeer = arrayOfWindow[j].getPeer();
/*  409 */       if ((localComponentPeer != null) && ((localComponentPeer instanceof XWindowPeer)) && 
/*  410 */         (((XWindowPeer)localComponentPeer).winAttr.iconsInherited)) {
/*  411 */         ((XWindowPeer)localComponentPeer).winAttr.icons = paramList;
/*  412 */         ((XWindowPeer)localComponentPeer).recursivelySetIcon(paramList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   List<XIconInfo> getIconInfo()
/*      */   {
/*  419 */     return this.winAttr.icons;
/*      */   }
/*      */ 
/*      */   void setIconHints(List<XIconInfo> paramList)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected static synchronized List<XIconInfo> getDefaultIconInfo()
/*      */   {
/*  428 */     if (defaultIconInfo == null) {
/*  429 */       defaultIconInfo = new ArrayList();
/*  430 */       if (XlibWrapper.dataModel == 32) {
/*  431 */         defaultIconInfo.add(new XIconInfo(XAWTIcon32_java_icon16_png.java_icon16_png));
/*  432 */         defaultIconInfo.add(new XIconInfo(XAWTIcon32_java_icon24_png.java_icon24_png));
/*  433 */         defaultIconInfo.add(new XIconInfo(XAWTIcon32_java_icon32_png.java_icon32_png));
/*  434 */         defaultIconInfo.add(new XIconInfo(XAWTIcon32_java_icon48_png.java_icon48_png));
/*      */       } else {
/*  436 */         defaultIconInfo.add(new XIconInfo(XAWTIcon64_java_icon16_png.java_icon16_png));
/*  437 */         defaultIconInfo.add(new XIconInfo(XAWTIcon64_java_icon24_png.java_icon24_png));
/*  438 */         defaultIconInfo.add(new XIconInfo(XAWTIcon64_java_icon32_png.java_icon32_png));
/*  439 */         defaultIconInfo.add(new XIconInfo(XAWTIcon64_java_icon48_png.java_icon48_png));
/*      */       }
/*      */     }
/*  442 */     return defaultIconInfo;
/*      */   }
/*      */ 
/*      */   private void updateShape()
/*      */   {
/*  447 */     Shape localShape = AWTAccessor.getWindowAccessor().getShape((Window)this.target);
/*  448 */     if (localShape != null)
/*  449 */       applyShape(Region.getInstance(localShape, null));
/*      */   }
/*      */ 
/*      */   private void updateOpacity()
/*      */   {
/*  455 */     float f = AWTAccessor.getWindowAccessor().getOpacity((Window)this.target);
/*  456 */     if (f < 1.0F)
/*  457 */       setOpacity(f);
/*      */   }
/*      */ 
/*      */   public void updateMinimumSize()
/*      */   {
/*  464 */     this.targetMinimumSize = (this.target.isMinimumSizeSet() ? this.target.getMinimumSize() : null);
/*      */   }
/*      */ 
/*      */   public Dimension getTargetMinimumSize()
/*      */   {
/*  469 */     return this.targetMinimumSize == null ? null : new Dimension(this.targetMinimumSize);
/*      */   }
/*      */ 
/*      */   public XWindowPeer getOwnerPeer() {
/*  473 */     return this.ownerPeer;
/*      */   }
/*      */ 
/*      */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  484 */     XToolkit.awtLock();
/*      */     try {
/*  486 */       Rectangle localRectangle1 = getBounds();
/*      */ 
/*  488 */       super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/*      */ 
/*  490 */       Rectangle localRectangle2 = getBounds();
/*      */ 
/*  492 */       XSizeHints localXSizeHints = getHints();
/*  493 */       setSizeHints(localXSizeHints.get_flags() | 0x4 | 0x8, localRectangle2.x, localRectangle2.y, localRectangle2.width, localRectangle2.height);
/*      */ 
/*  495 */       XWM.setMotifDecor(this, false, 0, 0);
/*      */ 
/*  497 */       int i = !localRectangle2.getSize().equals(localRectangle1.getSize()) ? 1 : 0;
/*  498 */       int j = !localRectangle2.getLocation().equals(localRectangle1.getLocation()) ? 1 : 0;
/*  499 */       if ((j != 0) || (i != 0)) {
/*  500 */         repositionSecurityWarning();
/*      */       }
/*  502 */       if (i != 0) {
/*  503 */         postEventToEventQueue(new ComponentEvent(getEventSource(), 101));
/*      */       }
/*  505 */       if (j != 0)
/*  506 */         postEventToEventQueue(new ComponentEvent(getEventSource(), 100));
/*      */     }
/*      */     finally {
/*  509 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   void updateFocusability() {
/*  514 */     updateFocusableWindowState();
/*  515 */     XToolkit.awtLock();
/*      */     try {
/*  517 */       XWMHints localXWMHints = getWMHints();
/*  518 */       localXWMHints.set_flags(localXWMHints.get_flags() | 1L);
/*  519 */       localXWMHints.set_input(false);
/*  520 */       XlibWrapper.XSetWMHints(XToolkit.getDisplay(), getWindow(), localXWMHints.pData);
/*      */     }
/*      */     finally {
/*  523 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Insets getInsets() {
/*  528 */     return new Insets(0, 0, 0, 0);
/*      */   }
/*      */ 
/*      */   public void handleIconify()
/*      */   {
/*  534 */     postEvent(new WindowEvent((Window)this.target, 203));
/*      */   }
/*      */ 
/*      */   public void handleDeiconify()
/*      */   {
/*  540 */     postEvent(new WindowEvent((Window)this.target, 204));
/*      */   }
/*      */ 
/*      */   public void handleStateChange(int paramInt1, int paramInt2)
/*      */   {
/*  546 */     postEvent(new WindowEvent((Window)this.target, 209, paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */   public Insets insets()
/*      */   {
/*  555 */     return getInsets();
/*      */   }
/*      */ 
/*      */   boolean isAutoRequestFocus() {
/*  559 */     if (XToolkit.isToolkitThread()) {
/*  560 */       return AWTAccessor.getWindowAccessor().isAutoRequestFocus((Window)this.target);
/*      */     }
/*  562 */     return ((Window)this.target).isAutoRequestFocus();
/*      */   }
/*      */ 
/*      */   static XWindowPeer getNativeFocusedWindowPeer()
/*      */   {
/*  570 */     XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(xGetInputFocus());
/*  571 */     return (localXBaseWindow instanceof XFocusProxyWindow) ? ((XFocusProxyWindow)localXBaseWindow).getOwner() : (localXBaseWindow instanceof XWindowPeer) ? (XWindowPeer)localXBaseWindow : null;
/*      */   }
/*      */ 
/*      */   static Window getNativeFocusedWindow()
/*      */   {
/*  580 */     XWindowPeer localXWindowPeer = getNativeFocusedWindowPeer();
/*  581 */     return localXWindowPeer != null ? (Window)localXWindowPeer.target : null;
/*      */   }
/*      */ 
/*      */   boolean isFocusableWindow() {
/*  585 */     if ((XToolkit.isToolkitThread()) || (SunToolkit.isAWTLockHeldByCurrentThread()))
/*      */     {
/*  587 */       return this.cachedFocusableWindow;
/*      */     }
/*  589 */     return ((Window)this.target).isFocusableWindow();
/*      */   }
/*      */ 
/*      */   boolean isFocusedWindowModalBlocker()
/*      */   {
/*  595 */     return false;
/*      */   }
/*      */ 
/*      */   long getFocusTargetWindow() {
/*  599 */     return getContentWindow();
/*      */   }
/*      */ 
/*      */   boolean isNativelyNonFocusableWindow()
/*      */   {
/*  609 */     if ((XToolkit.isToolkitThread()) || (SunToolkit.isAWTLockHeldByCurrentThread()))
/*      */     {
/*  611 */       return (isSimpleWindow()) || (!this.cachedFocusableWindow);
/*      */     }
/*  613 */     return (isSimpleWindow()) || (!((Window)this.target).isFocusableWindow());
/*      */   }
/*      */ 
/*      */   public void handleWindowFocusIn_Dispatch()
/*      */   {
/*  618 */     if (EventQueue.isDispatchThread()) {
/*  619 */       XKeyboardFocusManagerPeer.setCurrentNativeFocusedWindow((Window)this.target);
/*  620 */       WindowEvent localWindowEvent = new WindowEvent((Window)this.target, 207);
/*  621 */       SunToolkit.setSystemGenerated(localWindowEvent);
/*  622 */       this.target.dispatchEvent(localWindowEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleWindowFocusInSync(long paramLong) {
/*  627 */     WindowEvent localWindowEvent = new WindowEvent((Window)this.target, 207);
/*  628 */     XKeyboardFocusManagerPeer.setCurrentNativeFocusedWindow((Window)this.target);
/*  629 */     sendEvent(localWindowEvent);
/*      */   }
/*      */ 
/*      */   public void handleWindowFocusIn(long paramLong)
/*      */   {
/*  634 */     WindowEvent localWindowEvent = new WindowEvent((Window)this.target, 207);
/*      */ 
/*  636 */     XKeyboardFocusManagerPeer.setCurrentNativeFocusedWindow((Window)this.target);
/*  637 */     postEvent(wrapInSequenced(localWindowEvent));
/*      */   }
/*      */ 
/*      */   public void handleWindowFocusOut(Window paramWindow, long paramLong)
/*      */   {
/*  643 */     WindowEvent localWindowEvent = new WindowEvent((Window)this.target, 208, paramWindow);
/*  644 */     XKeyboardFocusManagerPeer.setCurrentNativeFocusedWindow(null);
/*  645 */     XKeyboardFocusManagerPeer.setCurrentNativeFocusOwner(null);
/*      */ 
/*  647 */     postEvent(wrapInSequenced(localWindowEvent));
/*      */   }
/*      */   public void handleWindowFocusOutSync(Window paramWindow, long paramLong) {
/*  650 */     WindowEvent localWindowEvent = new WindowEvent((Window)this.target, 208, paramWindow);
/*  651 */     XKeyboardFocusManagerPeer.setCurrentNativeFocusedWindow(null);
/*  652 */     XKeyboardFocusManagerPeer.setCurrentNativeFocusOwner(null);
/*  653 */     sendEvent(localWindowEvent);
/*      */   }
/*      */ 
/*      */   public void checkIfOnNewScreen(Rectangle paramRectangle)
/*      */   {
/*  663 */     if (!XToolkit.localEnv.runningXinerama()) {
/*  664 */       return;
/*      */     }
/*      */ 
/*  667 */     if (log.isLoggable(300)) {
/*  668 */       log.finest("XWindowPeer: Check if we've been moved to a new screen since we're running in Xinerama mode");
/*      */     }
/*      */ 
/*  671 */     int i = paramRectangle.width * paramRectangle.height;
/*      */ 
/*  673 */     int n = 0;
/*  674 */     int i1 = ((X11GraphicsDevice)getGraphicsConfiguration().getDevice()).getScreen();
/*  675 */     int i2 = 0;
/*  676 */     GraphicsDevice[] arrayOfGraphicsDevice = XToolkit.localEnv.getScreenDevices();
/*  677 */     GraphicsConfiguration localGraphicsConfiguration = null;
/*      */ 
/*  680 */     for (int i3 = 0; i3 < arrayOfGraphicsDevice.length; i3++) {
/*  681 */       Rectangle localRectangle = arrayOfGraphicsDevice[i3].getDefaultConfiguration().getBounds();
/*  682 */       if (paramRectangle.intersects(localRectangle)) {
/*  683 */         int m = Math.min(paramRectangle.x + paramRectangle.width, localRectangle.x + localRectangle.width) - Math.max(paramRectangle.x, localRectangle.x);
/*      */ 
/*  686 */         int k = Math.min(paramRectangle.y + paramRectangle.height, localRectangle.y + localRectangle.height) - Math.max(paramRectangle.y, localRectangle.y);
/*      */ 
/*  689 */         int j = m * k;
/*  690 */         if (j == i)
/*      */         {
/*  692 */           i2 = i3;
/*  693 */           localGraphicsConfiguration = arrayOfGraphicsDevice[i3].getDefaultConfiguration();
/*  694 */           break;
/*      */         }
/*  696 */         if (j > n) {
/*  697 */           n = j;
/*  698 */           i2 = i3;
/*  699 */           localGraphicsConfiguration = arrayOfGraphicsDevice[i3].getDefaultConfiguration();
/*      */         }
/*      */       }
/*      */     }
/*  703 */     if (i2 != i1) {
/*  704 */       if (log.isLoggable(300)) {
/*  705 */         log.finest("XWindowPeer: Moved to a new screen");
/*      */       }
/*  707 */       executeDisplayChangedOnEDT(localGraphicsConfiguration);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void executeDisplayChangedOnEDT(final GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/*  717 */     Runnable local1 = new Runnable() {
/*      */       public void run() {
/*  719 */         AWTAccessor.getComponentAccessor().setGraphicsConfiguration(XWindowPeer.this.target, paramGraphicsConfiguration);
/*      */       }
/*      */     };
/*  723 */     SunToolkit.executeOnEventHandlerThread(this.target, local1);
/*      */   }
/*      */ 
/*      */   public void displayChanged()
/*      */   {
/*  731 */     executeDisplayChangedOnEDT(getGraphicsConfiguration());
/*      */   }
/*      */ 
/*      */   public void paletteChanged()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void handleConfigureNotifyEvent(XEvent paramXEvent)
/*      */   {
/*  749 */     XConfigureEvent localXConfigureEvent = paramXEvent.get_xconfigure();
/*  750 */     checkIfOnNewScreen(new Rectangle(localXConfigureEvent.get_x(), localXConfigureEvent.get_y(), localXConfigureEvent.get_width(), localXConfigureEvent.get_height()));
/*      */ 
/*  758 */     super.handleConfigureNotifyEvent(paramXEvent);
/*  759 */     repositionSecurityWarning();
/*      */   }
/*      */ 
/*      */   final void requestXFocus(long paramLong) {
/*  763 */     requestXFocus(paramLong, true);
/*      */   }
/*      */ 
/*      */   final void requestXFocus() {
/*  767 */     requestXFocus(0L, false);
/*      */   }
/*      */ 
/*      */   protected void requestXFocus(long paramLong, boolean paramBoolean)
/*      */   {
/*  779 */     if (focusLog.isLoggable(500)) focusLog.fine("Requesting window focus");
/*  780 */     requestWindowFocus(paramLong, paramBoolean);
/*      */   }
/*      */ 
/*      */   public final boolean focusAllowedFor() {
/*  784 */     if (isNativelyNonFocusableWindow()) {
/*  785 */       return false;
/*      */     }
/*      */ 
/*  796 */     if (isModalBlocked()) {
/*  797 */       return false;
/*      */     }
/*  799 */     return true;
/*      */   }
/*      */ 
/*      */   public void handleFocusEvent(XEvent paramXEvent) {
/*  803 */     XFocusChangeEvent localXFocusChangeEvent = paramXEvent.get_xfocus();
/*      */ 
/*  805 */     focusLog.fine("{0}", new Object[] { localXFocusChangeEvent });
/*  806 */     if (isEventDisabled(paramXEvent)) {
/*  807 */       return;
/*      */     }
/*  809 */     if (paramXEvent.get_type() == 9)
/*      */     {
/*  812 */       if ((focusAllowedFor()) && (
/*  813 */         (localXFocusChangeEvent.get_mode() == 0) || (localXFocusChangeEvent.get_mode() == 3)))
/*      */       {
/*  816 */         handleWindowFocusIn(localXFocusChangeEvent.get_serial());
/*      */       }
/*      */ 
/*      */     }
/*  822 */     else if ((localXFocusChangeEvent.get_mode() == 0) || (localXFocusChangeEvent.get_mode() == 3))
/*      */     {
/*  826 */       if (!isNativelyNonFocusableWindow()) {
/*  827 */         XWindowPeer localXWindowPeer = getNativeFocusedWindowPeer();
/*  828 */         Object localObject = localXWindowPeer != null ? localXWindowPeer.getTarget() : null;
/*  829 */         Window localWindow = null;
/*  830 */         if ((localObject instanceof Window)) {
/*  831 */           localWindow = (Window)localObject;
/*      */         }
/*      */ 
/*  835 */         if ((localXWindowPeer != null) && (localXWindowPeer.isNativelyNonFocusableWindow())) {
/*  836 */           return;
/*      */         }
/*  838 */         if (this == localXWindowPeer) {
/*  839 */           localWindow = null;
/*  840 */         } else if (((localXWindowPeer instanceof XDecoratedPeer)) && 
/*  841 */           (((XDecoratedPeer)localXWindowPeer).actualFocusedWindow != null)) {
/*  842 */           localXWindowPeer = ((XDecoratedPeer)localXWindowPeer).actualFocusedWindow;
/*  843 */           localObject = localXWindowPeer.getTarget();
/*  844 */           if (((localObject instanceof Window)) && (localXWindowPeer.isVisible()) && (localXWindowPeer.isNativelyNonFocusableWindow()))
/*      */           {
/*  848 */             localWindow = (Window)localObject;
/*      */           }
/*      */         }
/*      */ 
/*  852 */         handleWindowFocusOut(localWindow, localXFocusChangeEvent.get_serial());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void setSaveUnder(boolean paramBoolean) {
/*      */   }
/*      */ 
/*      */   public void toFront() {
/*  861 */     if ((isOverrideRedirect()) && (this.mustControlStackPosition)) {
/*  862 */       this.mustControlStackPosition = false;
/*  863 */       removeRootPropertyEventDispatcher();
/*      */     }
/*  865 */     if (isVisible()) {
/*  866 */       super.toFront();
/*  867 */       if ((isFocusableWindow()) && (isAutoRequestFocus()) && (!isModalBlocked()) && (!isWithdrawn()))
/*      */       {
/*  870 */         requestInitialFocus();
/*      */       }
/*      */     } else {
/*  873 */       setVisible(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void toBack() {
/*  878 */     XToolkit.awtLock();
/*      */     try {
/*  880 */       if (!isOverrideRedirect())
/*  881 */         XlibWrapper.XLowerWindow(XToolkit.getDisplay(), getWindow());
/*      */       else
/*  883 */         lowerOverrideRedirect();
/*      */     }
/*      */     finally
/*      */     {
/*  887 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void lowerOverrideRedirect()
/*      */   {
/*  895 */     HashSet localHashSet = new HashSet();
/*  896 */     long l1 = 0L; long l2 = 0L;
/*      */ 
/*  898 */     for (XWindowPeer localXWindowPeer : windows) {
/*  899 */       l1 = getToplevelWindow(localXWindowPeer.getWindow());
/*  900 */       if (localXWindowPeer.equals(this)) {
/*  901 */         l2 = l1;
/*      */       }
/*  903 */       if (l1 > 0L) {
/*  904 */         localHashSet.add(Long.valueOf(l1));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  913 */     long l4 = -1L; long l5 = -1L;
/*  914 */     int i = -1; int j = -1; int k = -1;
/*  915 */     int m = 0;
/*  916 */     XQueryTree localXQueryTree = new XQueryTree(XToolkit.getDefaultRootWindow());
/*      */     try {
/*  918 */       if (localXQueryTree.execute() > 0) {
/*  919 */         int n = localXQueryTree.get_nchildren();
/*  920 */         long l7 = localXQueryTree.get_children();
/*  921 */         for (m = 0; m < n; m++) {
/*  922 */           long l3 = Native.getWindow(l7, m);
/*  923 */           if (l3 == l2) {
/*  924 */             i = m;
/*  925 */           } else if (isDesktopWindow(l3))
/*      */           {
/*  927 */             j = m;
/*  928 */             l4 = l3;
/*  929 */           } else if ((k < 0) && (localHashSet.contains(Long.valueOf(l3))) && (l3 != l2))
/*      */           {
/*  932 */             k = m;
/*  933 */             l5 = l3;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  938 */       if (((i < k) || (k < 0)) && (j < i)) {
/*      */         return;
/*      */       }
/*  941 */       long l6 = Native.allocateLongArray(2);
/*  942 */       Native.putLong(l6, 0, l5);
/*  943 */       Native.putLong(l6, 1, l2);
/*  944 */       XlibWrapper.XRestackWindows(XToolkit.getDisplay(), l6, 2);
/*  945 */       XlibWrapper.unsafe.freeMemory(l6);
/*      */ 
/*  948 */       if (!this.mustControlStackPosition) {
/*  949 */         this.mustControlStackPosition = true;
/*      */ 
/*  952 */         addRootPropertyEventDispatcher();
/*      */       }
/*      */     } finally {
/*  955 */       localXQueryTree.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   private long getToplevelWindow(long paramLong) {
/*  964 */     long l1 = paramLong;
/*      */     long l2;
/*      */     long l3;
/*      */     do {
/*  966 */       l2 = l1;
/*  967 */       XQueryTree localXQueryTree = new XQueryTree(l1);
/*      */       try {
/*  969 */         if (localXQueryTree.execute() == 0) {
/*  970 */           return 0L;
/*      */         }
/*  972 */         l3 = localXQueryTree.get_root();
/*  973 */         l1 = localXQueryTree.get_parent();
/*      */       } finally {
/*  975 */         localXQueryTree.dispose();
/*      */       }
/*      */     }
/*  978 */     while (l1 != l3);
/*      */ 
/*  980 */     return l2;
/*      */   }
/*      */ 
/*      */   private static boolean isDesktopWindow(long paramLong) {
/*  984 */     return XWM.getWM().isDesktopWindow(paramLong);
/*      */   }
/*      */ 
/*      */   private void updateAlwaysOnTop() {
/*  988 */     log.fine("Promoting always-on-top state {0}", new Object[] { Boolean.valueOf(this.alwaysOnTop) });
/*  989 */     XWM.getWM().setLayer(this, this.alwaysOnTop ? 1 : 0);
/*      */   }
/*      */ 
/*      */   public void updateAlwaysOnTopState()
/*      */   {
/*  996 */     this.alwaysOnTop = ((Window)this.target).isAlwaysOnTop();
/*  997 */     updateAlwaysOnTop();
/*      */   }
/*      */ 
/*      */   boolean isLocationByPlatform() {
/* 1001 */     return this.locationByPlatform;
/*      */   }
/*      */ 
/*      */   private void promoteDefaultPosition() {
/* 1005 */     this.locationByPlatform = ((Window)this.target).isLocationByPlatform();
/* 1006 */     if (this.locationByPlatform) {
/* 1007 */       XToolkit.awtLock();
/*      */       try {
/* 1009 */         Rectangle localRectangle = getBounds();
/* 1010 */         XSizeHints localXSizeHints = getHints();
/* 1011 */         setSizeHints(localXSizeHints.get_flags() & 0xFFFFFFFA, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */       }
/*      */       finally {
/* 1014 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setVisible(boolean paramBoolean) {
/* 1020 */     if ((!isVisible()) && (paramBoolean)) {
/* 1021 */       this.isBeforeFirstMapNotify = true;
/* 1022 */       this.winAttr.initialFocus = isAutoRequestFocus();
/* 1023 */       if (!this.winAttr.initialFocus)
/*      */       {
/* 1032 */         suppressWmTakeFocus(true);
/*      */       }
/*      */     }
/* 1035 */     updateFocusability();
/* 1036 */     promoteDefaultPosition();
/* 1037 */     if ((!paramBoolean) && (this.warningWindow != null)) {
/* 1038 */       this.warningWindow.setSecurityWarningVisible(false, false);
/*      */     }
/* 1040 */     super.setVisible(paramBoolean);
/* 1041 */     if ((!paramBoolean) && (!isWithdrawn()))
/*      */     {
/* 1046 */       XToolkit.awtLock();
/*      */       try {
/* 1048 */         XUnmapEvent localXUnmapEvent = new XUnmapEvent();
/* 1049 */         localXUnmapEvent.set_window(this.window);
/* 1050 */         localXUnmapEvent.set_event(XToolkit.getDefaultRootWindow());
/* 1051 */         localXUnmapEvent.set_type(18);
/* 1052 */         localXUnmapEvent.set_from_configure(false);
/* 1053 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XToolkit.getDefaultRootWindow(), false, 1572864L, localXUnmapEvent.pData);
/*      */ 
/* 1056 */         localXUnmapEvent.dispose();
/*      */       }
/*      */       finally {
/* 1059 */         XToolkit.awtUnlock();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1066 */     if ((isOverrideRedirect()) && (paramBoolean)) {
/* 1067 */       updateChildrenSizes();
/*      */     }
/* 1069 */     repositionSecurityWarning();
/*      */   }
/*      */ 
/*      */   protected void suppressWmTakeFocus(boolean paramBoolean) {
/*      */   }
/*      */ 
/*      */   final boolean isSimpleWindow() {
/* 1076 */     return (!(this.target instanceof Frame)) && (!(this.target instanceof Dialog));
/*      */   }
/*      */   boolean hasWarningWindow() {
/* 1079 */     return ((Window)this.target).getWarningString() != null;
/*      */   }
/*      */ 
/*      */   int getMenuBarHeight()
/*      */   {
/* 1084 */     return 0;
/*      */   }
/*      */ 
/*      */   void updateChildrenSizes()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void repositionSecurityWarning()
/*      */   {
/* 1099 */     if (this.warningWindow != null)
/*      */     {
/* 1102 */       AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/* 1103 */       int i = localComponentAccessor.getX(this.target);
/* 1104 */       int j = localComponentAccessor.getY(this.target);
/* 1105 */       int k = localComponentAccessor.getWidth(this.target);
/* 1106 */       int m = localComponentAccessor.getHeight(this.target);
/* 1107 */       this.warningWindow.reposition(i, j, k, m);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setMouseAbove(boolean paramBoolean)
/*      */   {
/* 1113 */     super.setMouseAbove(paramBoolean);
/* 1114 */     updateSecurityWarningVisibility();
/*      */   }
/*      */ 
/*      */   public void setFullScreenExclusiveModeState(boolean paramBoolean)
/*      */   {
/* 1119 */     super.setFullScreenExclusiveModeState(paramBoolean);
/* 1120 */     updateSecurityWarningVisibility();
/*      */   }
/*      */ 
/*      */   public void updateSecurityWarningVisibility() {
/* 1124 */     if (this.warningWindow == null) {
/* 1125 */       return;
/*      */     }
/*      */ 
/* 1128 */     if (!isVisible()) {
/* 1129 */       return;
/*      */     }
/*      */ 
/* 1132 */     boolean bool = false;
/*      */ 
/* 1134 */     if (!isFullScreenExclusiveMode()) {
/* 1135 */       int i = getWMState();
/*      */ 
/* 1139 */       if ((isVisible()) && ((i == 1) || (isSimpleWindow()))) {
/* 1140 */         if (XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow() == getTarget())
/*      */         {
/* 1143 */           bool = true;
/*      */         }
/*      */ 
/* 1146 */         if ((isMouseAbove()) || (this.warningWindow.isMouseAbove()))
/*      */         {
/* 1148 */           bool = true;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1153 */     this.warningWindow.setSecurityWarningVisible(bool, true);
/*      */   }
/*      */ 
/*      */   boolean isOverrideRedirect() {
/* 1157 */     return (XWM.getWMID() == 4) || (Window.Type.POPUP.equals(getWindowType()));
/*      */   }
/*      */ 
/*      */   final boolean isOLWMDecorBug()
/*      */   {
/* 1162 */     return (XWM.getWMID() == 4) && (!this.winAttr.nativeDecor);
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/* 1167 */     SunToolkit.awtLock();
/*      */     try {
/* 1169 */       windows.remove(this);
/*      */     } finally {
/* 1171 */       SunToolkit.awtUnlock();
/*      */     }
/* 1173 */     if (this.warningWindow != null) {
/* 1174 */       this.warningWindow.destroy();
/*      */     }
/* 1176 */     removeRootPropertyEventDispatcher();
/* 1177 */     this.mustControlStackPosition = false;
/* 1178 */     super.dispose();
/*      */ 
/* 1186 */     if ((isSimpleWindow()) && 
/* 1187 */       (this.target == XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow())) {
/* 1188 */       Window localWindow = getDecoratedOwner((Window)this.target);
/* 1189 */       ((XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow)).requestWindowFocus();
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isResizable() {
/* 1194 */     return this.winAttr.isResizable;
/*      */   }
/*      */ 
/*      */   public void handleVisibilityEvent(XEvent paramXEvent) {
/* 1198 */     super.handleVisibilityEvent(paramXEvent);
/* 1199 */     XVisibilityEvent localXVisibilityEvent = paramXEvent.get_xvisibility();
/* 1200 */     this.winAttr.visibilityState = localXVisibilityEvent.get_state();
/*      */ 
/* 1204 */     repositionSecurityWarning();
/*      */   }
/*      */ 
/*      */   void handleRootPropertyNotify(XEvent paramXEvent) {
/* 1208 */     XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/* 1209 */     if ((this.mustControlStackPosition) && (localXPropertyEvent.get_atom() == XAtom.get("_NET_CLIENT_LIST_STACKING").getAtom()))
/*      */     {
/* 1214 */       if (isOverrideRedirect())
/* 1215 */         toBack();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void removeStartupNotification()
/*      */   {
/* 1221 */     if (isStartupNotificationRemoved.getAndSet(true)) {
/* 1222 */       return;
/*      */     }
/*      */ 
/* 1225 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public String run() {
/* 1227 */         return XToolkit.getEnv("DESKTOP_STARTUP_ID");
/*      */       }
/*      */     });
/* 1230 */     if (str == null) {
/* 1231 */       return;
/*      */     }
/*      */ 
/* 1234 */     StringBuilder localStringBuilder = new StringBuilder("remove: ID=");
/* 1235 */     localStringBuilder.append('"');
/* 1236 */     for (int i = 0; i < str.length(); i++) {
/* 1237 */       if ((str.charAt(i) == '"') || (str.charAt(i) == '\\')) {
/* 1238 */         localStringBuilder.append('\\');
/*      */       }
/* 1240 */       localStringBuilder.append(str.charAt(i));
/* 1242 */     }localStringBuilder.append('"');
/* 1243 */     localStringBuilder.append('\000');
/*      */     byte[] arrayOfByte;
/*      */     try { arrayOfByte = localStringBuilder.toString().getBytes("UTF-8");
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1248 */       return;
/*      */     }
/*      */ 
/* 1251 */     XClientMessageEvent localXClientMessageEvent = null;
/*      */ 
/* 1253 */     XToolkit.awtLock();
/*      */     try {
/* 1255 */       XAtom localXAtom1 = XAtom.get("_NET_STARTUP_INFO_BEGIN");
/* 1256 */       XAtom localXAtom2 = XAtom.get("_NET_STARTUP_INFO");
/*      */ 
/* 1258 */       localXClientMessageEvent = new XClientMessageEvent();
/* 1259 */       localXClientMessageEvent.set_type(33);
/* 1260 */       localXClientMessageEvent.set_window(getWindow());
/* 1261 */       localXClientMessageEvent.set_message_type(localXAtom1.getAtom());
/* 1262 */       localXClientMessageEvent.set_format(8);
/*      */ 
/* 1264 */       for (int j = 0; j < arrayOfByte.length; j += 20) {
/* 1265 */         int k = Math.min(arrayOfByte.length - j, 20);
/* 1266 */         for (int m = 0; 
/* 1267 */           m < k; m++) {
/* 1268 */           XlibWrapper.unsafe.putByte(localXClientMessageEvent.get_data() + m, arrayOfByte[(j + m)]);
/*      */         }
/* 1270 */         for (; m < 20; m++) {
/* 1271 */           XlibWrapper.unsafe.putByte(localXClientMessageEvent.get_data() + m, (byte)0);
/*      */         }
/* 1273 */         XlibWrapper.XSendEvent(XToolkit.getDisplay(), XlibWrapper.RootWindow(XToolkit.getDisplay(), getScreenNumber()), false, 4194304L, localXClientMessageEvent.pData);
/*      */ 
/* 1278 */         localXClientMessageEvent.set_message_type(localXAtom2.getAtom());
/*      */       }
/*      */     } finally {
/* 1281 */       XToolkit.awtUnlock();
/* 1282 */       if (localXClientMessageEvent != null)
/* 1283 */         localXClientMessageEvent.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleMapNotifyEvent(XEvent paramXEvent)
/*      */   {
/* 1289 */     removeStartupNotification();
/*      */ 
/* 1292 */     this.isUnhiding |= isWMStateNetHidden();
/*      */ 
/* 1294 */     super.handleMapNotifyEvent(paramXEvent);
/* 1295 */     if (!this.winAttr.initialFocus) {
/* 1296 */       suppressWmTakeFocus(false);
/*      */ 
/* 1302 */       XToolkit.awtLock();
/*      */       try {
/* 1304 */         XlibWrapper.XRaiseWindow(XToolkit.getDisplay(), getWindow());
/*      */       } finally {
/* 1306 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/* 1309 */     if (shouldFocusOnMapNotify()) {
/* 1310 */       focusLog.fine("Automatically request focus on window");
/* 1311 */       requestInitialFocus();
/*      */     }
/* 1313 */     this.isUnhiding = false;
/* 1314 */     this.isBeforeFirstMapNotify = false;
/* 1315 */     updateAlwaysOnTop();
/*      */ 
/* 1317 */     synchronized (getStateLock()) {
/* 1318 */       if (!this.isMapped)
/* 1319 */         this.isMapped = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleUnmapNotifyEvent(XEvent paramXEvent)
/*      */   {
/* 1325 */     super.handleUnmapNotifyEvent(paramXEvent);
/*      */ 
/* 1329 */     this.isUnhiding |= isWMStateNetHidden();
/*      */ 
/* 1331 */     synchronized (getStateLock()) {
/* 1332 */       if (this.isMapped)
/* 1333 */         this.isMapped = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean shouldFocusOnMapNotify()
/*      */   {
/* 1339 */     boolean bool = false;
/*      */ 
/* 1341 */     if (this.isBeforeFirstMapNotify) {
/* 1342 */       bool = (this.winAttr.initialFocus) || (isFocusedWindowModalBlocker());
/*      */     }
/*      */     else {
/* 1345 */       bool = this.isUnhiding;
/*      */     }
/* 1347 */     bool = (bool) && (isFocusableWindow()) && (!isModalBlocked());
/*      */ 
/* 1351 */     return bool;
/*      */   }
/*      */ 
/*      */   protected boolean isWMStateNetHidden() {
/* 1355 */     XNETProtocol localXNETProtocol = XWM.getWM().getNETProtocol();
/* 1356 */     return (localXNETProtocol != null) && (localXNETProtocol.isWMStateNetHidden(this));
/*      */   }
/*      */ 
/*      */   protected void requestInitialFocus() {
/* 1360 */     requestXFocus();
/*      */   }
/*      */ 
/*      */   public void addToplevelStateListener(ToplevelStateListener paramToplevelStateListener) {
/* 1364 */     this.toplevelStateListeners.add(paramToplevelStateListener);
/*      */   }
/*      */ 
/*      */   public void removeToplevelStateListener(ToplevelStateListener paramToplevelStateListener) {
/* 1368 */     this.toplevelStateListeners.remove(paramToplevelStateListener);
/*      */   }
/*      */ 
/*      */   protected void stateChanged(long paramLong, int paramInt1, int paramInt2)
/*      */   {
/* 1384 */     updateTransientFor();
/*      */ 
/* 1386 */     for (ToplevelStateListener localToplevelStateListener : this.toplevelStateListeners) {
/* 1387 */       localToplevelStateListener.stateChangedICCCM(paramInt1, paramInt2);
/*      */     }
/*      */ 
/* 1390 */     updateSecurityWarningVisibility();
/*      */   }
/*      */ 
/*      */   boolean isWithdrawn() {
/* 1394 */     return getWMState() == 0;
/*      */   }
/*      */ 
/*      */   boolean hasDecorations(int paramInt) {
/* 1398 */     if (!this.winAttr.nativeDecor) {
/* 1399 */       return false;
/*      */     }
/*      */ 
/* 1402 */     int i = this.winAttr.decorations;
/* 1403 */     boolean bool = (i & paramInt) == paramInt;
/* 1404 */     if ((i & XWindowAttributesData.AWT_DECOR_ALL) != 0) {
/* 1405 */       return !bool;
/*      */     }
/* 1407 */     return bool;
/*      */   }
/*      */ 
/*      */   void setReparented(boolean paramBoolean)
/*      */   {
/* 1412 */     super.setReparented(paramBoolean);
/* 1413 */     XToolkit.awtLock();
/*      */     try {
/* 1415 */       if ((isReparented()) && (this.delayedModalBlocking)) {
/* 1416 */         addToTransientFors((XDialogPeer)AWTAccessor.getComponentAccessor().getPeer(this.modalBlocker));
/* 1417 */         this.delayedModalBlocking = false;
/*      */       }
/*      */     } finally {
/* 1420 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static Vector<XWindowPeer> collectJavaToplevels()
/*      */   {
/* 1429 */     Vector localVector1 = new Vector();
/* 1430 */     Vector localVector2 = new Vector();
/* 1431 */     X11GraphicsEnvironment localX11GraphicsEnvironment = (X11GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
/*      */ 
/* 1433 */     GraphicsDevice[] arrayOfGraphicsDevice1 = localX11GraphicsEnvironment.getScreenDevices();
/*      */     Object localObject1;
/*      */     int m;
/*      */     long l2;
/* 1434 */     if ((!localX11GraphicsEnvironment.runningXinerama()) && (arrayOfGraphicsDevice1.length > 1))
/* 1435 */       for (localObject1 : arrayOfGraphicsDevice1) {
/* 1436 */         m = ((X11GraphicsDevice)localObject1).getScreen();
/* 1437 */         l2 = XlibWrapper.RootWindow(XToolkit.getDisplay(), m);
/* 1438 */         localVector2.add(Long.valueOf(l2));
/*      */       }
/*      */     else {
/* 1441 */       localVector2.add(Long.valueOf(XToolkit.getDefaultRootWindow()));
/*      */     }
/* 1443 */     int i = windows.size();
/* 1444 */     while ((localVector2.size() > 0) && (localVector1.size() < i)) {
/* 1445 */       long l1 = ((Long)localVector2.remove(0)).longValue();
/* 1446 */       localObject1 = new XQueryTree(l1);
/*      */       try {
/* 1448 */         if (((XQueryTree)localObject1).execute() != 0) {
/* 1449 */           m = ((XQueryTree)localObject1).get_nchildren();
/* 1450 */           l2 = ((XQueryTree)localObject1).get_children();
/*      */ 
/* 1452 */           for (int n = 0; n < m; n++) {
/* 1453 */             long l3 = Native.getWindow(l2, n);
/* 1454 */             XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(l3);
/*      */ 
/* 1456 */             if ((localXBaseWindow == null) || ((localXBaseWindow instanceof XWindowPeer)))
/*      */             {
/* 1459 */               localVector2.add(Long.valueOf(l3));
/*      */ 
/* 1461 */               if ((localXBaseWindow instanceof XWindowPeer)) {
/* 1462 */                 XWindowPeer localXWindowPeer1 = (XWindowPeer)localXBaseWindow;
/* 1463 */                 localVector1.add(localXWindowPeer1);
/*      */ 
/* 1468 */                 int i1 = 0;
/* 1469 */                 XWindowPeer localXWindowPeer2 = (XWindowPeer)localVector1.get(i1);
/* 1470 */                 while (localXWindowPeer2 != localXWindowPeer1) {
/* 1471 */                   XWindowPeer localXWindowPeer3 = localXWindowPeer2.getOwnerPeer();
/* 1472 */                   if (localXWindowPeer3 == localXWindowPeer1) {
/* 1473 */                     localVector1.remove(i1);
/* 1474 */                     localVector1.add(localXWindowPeer2);
/*      */                   } else {
/* 1476 */                     i1++;
/*      */                   }
/* 1478 */                   localXWindowPeer2 = (XWindowPeer)localVector1.get(i1);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       } finally { ((XQueryTree)localObject1).dispose(); }
/*      */ 
/*      */     }
/* 1487 */     return localVector1;
/*      */   }
/*      */ 
/*      */   public void setModalBlocked(Dialog paramDialog, boolean paramBoolean) {
/* 1491 */     setModalBlocked(paramDialog, paramBoolean, null);
/*      */   }
/*      */ 
/*      */   public void setModalBlocked(Dialog paramDialog, boolean paramBoolean, Vector<XWindowPeer> paramVector)
/*      */   {
/* 1496 */     XToolkit.awtLock();
/*      */     try
/*      */     {
/* 1499 */       synchronized (getStateLock()) {
/* 1500 */         XDialogPeer localXDialogPeer = (XDialogPeer)AWTAccessor.getComponentAccessor().getPeer(paramDialog);
/* 1501 */         if (paramBoolean) {
/* 1502 */           log.fine("{0} is blocked by {1}", new Object[] { this, localXDialogPeer });
/* 1503 */           this.modalBlocker = paramDialog;
/*      */ 
/* 1505 */           if ((isReparented()) || (XWM.isNonReparentingWM()))
/* 1506 */             addToTransientFors(localXDialogPeer, paramVector);
/*      */           else
/* 1508 */             this.delayedModalBlocking = true;
/*      */         }
/*      */         else {
/* 1511 */           if (paramDialog != this.modalBlocker) {
/* 1512 */             throw new IllegalStateException("Trying to unblock window blocked by another dialog");
/*      */           }
/* 1514 */           this.modalBlocker = null;
/*      */ 
/* 1516 */           if ((isReparented()) || (XWM.isNonReparentingWM()))
/* 1517 */             removeFromTransientFors();
/*      */           else {
/* 1519 */             this.delayedModalBlocking = false;
/*      */           }
/*      */         }
/*      */ 
/* 1523 */         updateTransientFor();
/*      */       }
/*      */     } finally {
/* 1526 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setToplevelTransientFor(XWindowPeer paramXWindowPeer1, XWindowPeer paramXWindowPeer2, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 1549 */     if ((paramXWindowPeer1 == null) || (paramXWindowPeer2 == null)) {
/* 1550 */       return;
/*      */     }
/* 1552 */     if (paramBoolean1) {
/* 1553 */       paramXWindowPeer1.prevTransientFor = paramXWindowPeer2;
/* 1554 */       paramXWindowPeer2.nextTransientFor = paramXWindowPeer1;
/*      */     }
/* 1556 */     if (paramXWindowPeer1.curRealTransientFor == paramXWindowPeer2) {
/* 1557 */       return;
/*      */     }
/* 1559 */     if ((!paramBoolean2) && (paramXWindowPeer1.getWMState() != paramXWindowPeer2.getWMState())) {
/* 1560 */       return;
/*      */     }
/* 1562 */     if (paramXWindowPeer1.getScreenNumber() != paramXWindowPeer2.getScreenNumber()) {
/* 1563 */       return;
/*      */     }
/* 1565 */     long l1 = paramXWindowPeer1.getWindow();
/* 1566 */     while ((!XlibUtil.isToplevelWindow(l1)) && (!XlibUtil.isXAWTToplevelWindow(l1))) {
/* 1567 */       l1 = XlibUtil.getParentWindow(l1);
/*      */     }
/* 1569 */     long l2 = paramXWindowPeer2.getWindow();
/* 1570 */     while ((!XlibUtil.isToplevelWindow(l2)) && (!XlibUtil.isXAWTToplevelWindow(l2))) {
/* 1571 */       l2 = XlibUtil.getParentWindow(l2);
/*      */     }
/* 1573 */     XlibWrapper.XSetTransientFor(XToolkit.getDisplay(), l1, l2);
/* 1574 */     paramXWindowPeer1.curRealTransientFor = paramXWindowPeer2;
/*      */   }
/*      */ 
/*      */   void updateTransientFor()
/*      */   {
/* 1586 */     int i = getWMState();
/* 1587 */     XWindowPeer localXWindowPeer1 = this.prevTransientFor;
/* 1588 */     while ((localXWindowPeer1 != null) && ((localXWindowPeer1.getWMState() != i) || (localXWindowPeer1.getScreenNumber() != getScreenNumber()))) {
/* 1589 */       localXWindowPeer1 = localXWindowPeer1.prevTransientFor;
/*      */     }
/* 1591 */     if (localXWindowPeer1 != null)
/* 1592 */       setToplevelTransientFor(this, localXWindowPeer1, false, false);
/*      */     else {
/* 1594 */       restoreTransientFor(this);
/*      */     }
/* 1596 */     XWindowPeer localXWindowPeer2 = this.nextTransientFor;
/* 1597 */     while ((localXWindowPeer2 != null) && ((localXWindowPeer2.getWMState() != i) || (localXWindowPeer2.getScreenNumber() != getScreenNumber()))) {
/* 1598 */       localXWindowPeer2 = localXWindowPeer2.nextTransientFor;
/*      */     }
/* 1600 */     if (localXWindowPeer2 != null)
/* 1601 */       setToplevelTransientFor(localXWindowPeer2, this, false, false);
/*      */   }
/*      */ 
/*      */   private static void removeTransientForHint(XWindowPeer paramXWindowPeer)
/*      */   {
/* 1614 */     XAtom localXAtom = XAtom.get(68L);
/* 1615 */     long l = paramXWindowPeer.getWindow();
/* 1616 */     while ((!XlibUtil.isToplevelWindow(l)) && (!XlibUtil.isXAWTToplevelWindow(l))) {
/* 1617 */       l = XlibUtil.getParentWindow(l);
/*      */     }
/* 1619 */     XlibWrapper.XDeleteProperty(XToolkit.getDisplay(), l, localXAtom.getAtom());
/* 1620 */     paramXWindowPeer.curRealTransientFor = null;
/*      */   }
/*      */ 
/*      */   private void addToTransientFors(XDialogPeer paramXDialogPeer)
/*      */   {
/* 1664 */     addToTransientFors(paramXDialogPeer, null);
/*      */   }
/*      */ 
/*      */   private void addToTransientFors(XDialogPeer paramXDialogPeer, Vector<XWindowPeer> paramVector)
/*      */   {
/* 1670 */     Object localObject1 = paramXDialogPeer;
/* 1671 */     while (((XWindowPeer)localObject1).prevTransientFor != null) {
/* 1672 */       localObject1 = ((XWindowPeer)localObject1).prevTransientFor;
/*      */     }
/*      */ 
/* 1677 */     XWindowPeer localXWindowPeer1 = this;
/* 1678 */     while (localXWindowPeer1.prevTransientFor != null)
/* 1679 */       localXWindowPeer1 = localXWindowPeer1.prevTransientFor;
/*      */     Object localObject2;
/* 1683 */     if (localObject1 == paramXDialogPeer) {
/* 1684 */       setToplevelTransientFor(paramXDialogPeer, this, true, false);
/*      */     }
/*      */     else {
/* 1687 */       if (paramVector == null) {
/* 1688 */         paramVector = collectJavaToplevels();
/*      */       }
/*      */ 
/* 1691 */       localObject2 = null;
/* 1692 */       for (XWindowPeer localXWindowPeer2 : paramVector) {
/* 1693 */         Object localObject3 = localObject2;
/* 1694 */         if (localXWindowPeer2 == localXWindowPeer1) {
/* 1695 */           if (localXWindowPeer1 == this) {
/* 1696 */             if (localObject3 != null) {
/* 1697 */               setToplevelTransientFor(this, localObject3, true, false);
/*      */             }
/* 1699 */             setToplevelTransientFor((XWindowPeer)localObject1, this, true, false);
/* 1700 */             break;
/*      */           }
/* 1702 */           localObject2 = localXWindowPeer1;
/* 1703 */           localXWindowPeer1 = localXWindowPeer1.nextTransientFor;
/*      */         } else {
/* 1705 */           if (localXWindowPeer2 != localObject1) continue;
/* 1706 */           localObject2 = localObject1;
/* 1707 */           localObject1 = ((XWindowPeer)localObject1).nextTransientFor;
/*      */         }
/*      */ 
/* 1711 */         if (localObject3 == null) {
/* 1712 */           ((XWindowPeer)localObject2).prevTransientFor = null;
/*      */         } else {
/* 1714 */           setToplevelTransientFor((XWindowPeer)localObject2, localObject3, true, false);
/* 1715 */           ((XWindowPeer)localObject2).updateTransientFor();
/*      */         }
/* 1717 */         if (localObject1 == paramXDialogPeer) {
/* 1718 */           setToplevelTransientFor(localXWindowPeer1, (XWindowPeer)localObject2, true, false);
/* 1719 */           setToplevelTransientFor((XWindowPeer)localObject1, this, true, false);
/* 1720 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1725 */     XToolkit.XSync();
/*      */   }
/*      */ 
/*      */   static void restoreTransientFor(XWindowPeer paramXWindowPeer) {
/* 1729 */     XWindowPeer localXWindowPeer = paramXWindowPeer.getOwnerPeer();
/* 1730 */     if (localXWindowPeer != null)
/* 1731 */       setToplevelTransientFor(paramXWindowPeer, localXWindowPeer, false, true);
/*      */     else
/* 1733 */       removeTransientForHint(paramXWindowPeer);
/*      */   }
/*      */ 
/*      */   private void removeFromTransientFors()
/*      */   {
/* 1769 */     Object localObject1 = this;
/*      */ 
/* 1772 */     Object localObject2 = this.nextTransientFor;
/*      */ 
/* 1775 */     HashSet localHashSet = new HashSet();
/* 1776 */     localHashSet.add(this);
/*      */ 
/* 1778 */     XWindowPeer localXWindowPeer1 = this.prevTransientFor;
/* 1779 */     while (localXWindowPeer1 != null) {
/* 1780 */       XWindowPeer localXWindowPeer2 = (XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localXWindowPeer1.modalBlocker);
/* 1781 */       if (localHashSet.contains(localXWindowPeer2))
/*      */       {
/* 1783 */         setToplevelTransientFor((XWindowPeer)localObject1, localXWindowPeer1, true, false);
/* 1784 */         localObject1 = localXWindowPeer1;
/* 1785 */         localHashSet.add(localXWindowPeer1);
/*      */       }
/*      */       else {
/* 1788 */         setToplevelTransientFor((XWindowPeer)localObject2, localXWindowPeer1, true, false);
/* 1789 */         localObject2 = localXWindowPeer1;
/*      */       }
/* 1791 */       localXWindowPeer1 = localXWindowPeer1.prevTransientFor;
/*      */     }
/* 1793 */     restoreTransientFor((XWindowPeer)localObject1);
/* 1794 */     ((XWindowPeer)localObject1).prevTransientFor = null;
/* 1795 */     restoreTransientFor((XWindowPeer)localObject2);
/* 1796 */     ((XWindowPeer)localObject2).prevTransientFor = null;
/* 1797 */     this.nextTransientFor = null;
/*      */ 
/* 1799 */     XToolkit.XSync();
/*      */   }
/*      */ 
/*      */   boolean isModalBlocked() {
/* 1803 */     return this.modalBlocker != null;
/*      */   }
/*      */ 
/*      */   static Window getDecoratedOwner(Window paramWindow) {
/* 1807 */     while ((null != paramWindow) && (!(paramWindow instanceof Frame)) && (!(paramWindow instanceof Dialog))) {
/* 1808 */       paramWindow = (Window)AWTAccessor.getComponentAccessor().getParent(paramWindow);
/*      */     }
/* 1810 */     return paramWindow;
/*      */   }
/*      */ 
/*      */   public boolean requestWindowFocus(XWindowPeer paramXWindowPeer) {
/* 1814 */     setActualFocusedWindow(paramXWindowPeer);
/* 1815 */     return requestWindowFocus();
/*      */   }
/*      */ 
/*      */   public boolean requestWindowFocus() {
/* 1819 */     return requestWindowFocus(0L, false);
/*      */   }
/*      */ 
/*      */   public boolean requestWindowFocus(long paramLong, boolean paramBoolean) {
/* 1823 */     focusLog.fine("Request for window focus");
/*      */ 
/* 1826 */     Window localWindow1 = getDecoratedOwner((Window)this.target);
/* 1827 */     Window localWindow2 = XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow();
/* 1828 */     Window localWindow3 = getDecoratedOwner(localWindow2);
/*      */ 
/* 1830 */     if (isWMStateNetHidden()) {
/* 1831 */       focusLog.fine("The window is unmapped, so rejecting the request");
/* 1832 */       return false;
/*      */     }
/* 1834 */     if (localWindow3 == localWindow1) {
/* 1835 */       focusLog.fine("Parent window is active - generating focus for this window");
/* 1836 */       handleWindowFocusInSync(-1L);
/* 1837 */       return true;
/*      */     }
/* 1839 */     focusLog.fine("Parent window is not active");
/*      */ 
/* 1841 */     XDecoratedPeer localXDecoratedPeer = (XDecoratedPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow1);
/* 1842 */     if ((localXDecoratedPeer != null) && (localXDecoratedPeer.requestWindowFocus(this, paramLong, paramBoolean))) {
/* 1843 */       focusLog.fine("Parent window accepted focus request - generating focus for this window");
/* 1844 */       return true;
/*      */     }
/* 1846 */     focusLog.fine("Denied - parent window is not active and didn't accept focus request");
/* 1847 */     return false;
/*      */   }
/*      */ 
/*      */   void setActualFocusedWindow(XWindowPeer paramXWindowPeer)
/*      */   {
/*      */   }
/*      */ 
/*      */   private void applyWindowType()
/*      */   {
/* 1858 */     XNETProtocol localXNETProtocol = XWM.getWM().getNETProtocol();
/* 1859 */     if (localXNETProtocol == null) {
/* 1860 */       return;
/*      */     }
/*      */ 
/* 1863 */     XAtom localXAtom = null;
/*      */ 
/* 1865 */     switch (4.$SwitchMap$java$awt$Window$Type[getWindowType().ordinal()])
/*      */     {
/*      */     case 1:
/* 1868 */       localXAtom = localXNETProtocol.XA_NET_WM_WINDOW_TYPE_NORMAL;
/* 1869 */       break;
/*      */     case 2:
/* 1871 */       localXAtom = localXNETProtocol.XA_NET_WM_WINDOW_TYPE_UTILITY;
/* 1872 */       break;
/*      */     case 3:
/* 1874 */       localXAtom = localXNETProtocol.XA_NET_WM_WINDOW_TYPE_POPUP_MENU;
/*      */     }
/*      */ 
/* 1878 */     if (localXAtom != null) {
/* 1879 */       XAtomList localXAtomList = new XAtomList();
/* 1880 */       localXAtomList.add(localXAtom);
/* 1881 */       localXNETProtocol.XA_NET_WM_WINDOW_TYPE.setAtomListProperty(getWindow(), localXAtomList);
/*      */     }
/*      */     else {
/* 1884 */       localXNETProtocol.XA_NET_WM_WINDOW_TYPE.DeleteProperty(getWindow());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void xSetVisible(boolean paramBoolean)
/*      */   {
/* 1891 */     if (log.isLoggable(500)) log.fine("Setting visible on " + this + " to " + paramBoolean);
/* 1892 */     XToolkit.awtLock();
/*      */     try {
/* 1894 */       this.visible = paramBoolean;
/* 1895 */       if (paramBoolean) {
/* 1896 */         applyWindowType();
/* 1897 */         XlibWrapper.XMapRaised(XToolkit.getDisplay(), getWindow());
/*      */       } else {
/* 1899 */         XlibWrapper.XUnmapWindow(XToolkit.getDisplay(), getWindow());
/*      */       }
/* 1901 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*      */     }
/*      */     finally {
/* 1904 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addDropTarget()
/*      */   {
/* 1912 */     XToolkit.awtLock();
/*      */     try {
/* 1914 */       if (this.dropTargetCount == 0) {
/* 1915 */         long l = getWindow();
/* 1916 */         if (l != 0L) {
/* 1917 */           XDropTargetRegistry.getRegistry().registerDropSite(l);
/*      */         }
/*      */       }
/* 1920 */       this.dropTargetCount += 1;
/*      */     } finally {
/* 1922 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeDropTarget() {
/* 1927 */     XToolkit.awtLock();
/*      */     try {
/* 1929 */       this.dropTargetCount -= 1;
/* 1930 */       if (this.dropTargetCount == 0) {
/* 1931 */         long l = getWindow();
/* 1932 */         if (l != 0L)
/* 1933 */           XDropTargetRegistry.getRegistry().unregisterDropSite(l);
/*      */       }
/*      */     }
/*      */     finally {
/* 1937 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/* 1941 */   void addRootPropertyEventDispatcher() { if (this.rootPropertyEventDispatcher == null) {
/* 1942 */       this.rootPropertyEventDispatcher = new XEventDispatcher() {
/*      */         public void dispatchEvent(XEvent paramAnonymousXEvent) {
/* 1944 */           if (paramAnonymousXEvent.get_type() == 28)
/* 1945 */             XWindowPeer.this.handleRootPropertyNotify(paramAnonymousXEvent);
/*      */         }
/*      */       };
/* 1949 */       XlibWrapper.XSelectInput(XToolkit.getDisplay(), XToolkit.getDefaultRootWindow(), 4194304L);
/*      */ 
/* 1952 */       XToolkit.addEventDispatcher(XToolkit.getDefaultRootWindow(), this.rootPropertyEventDispatcher);
/*      */     } }
/*      */ 
/*      */   void removeRootPropertyEventDispatcher()
/*      */   {
/* 1957 */     if (this.rootPropertyEventDispatcher != null) {
/* 1958 */       XToolkit.removeEventDispatcher(XToolkit.getDefaultRootWindow(), this.rootPropertyEventDispatcher);
/*      */ 
/* 1960 */       this.rootPropertyEventDispatcher = null;
/*      */     }
/*      */   }
/*      */ 
/* 1964 */   public void updateFocusableWindowState() { this.cachedFocusableWindow = isFocusableWindow(); }
/*      */ 
/*      */ 
/*      */   public XAtomList getNETWMState()
/*      */   {
/* 1970 */     if (this.net_wm_state == null) {
/* 1971 */       this.net_wm_state = this.XA_NET_WM_STATE.getAtomListPropertyList(this);
/*      */     }
/* 1973 */     return this.net_wm_state;
/*      */   }
/*      */ 
/*      */   public void setNETWMState(XAtomList paramXAtomList) {
/* 1977 */     this.net_wm_state = paramXAtomList;
/* 1978 */     if (paramXAtomList != null)
/* 1979 */       this.XA_NET_WM_STATE.setAtomListProperty(this, paramXAtomList);
/*      */   }
/*      */ 
/*      */   public PropMwmHints getMWMHints()
/*      */   {
/* 1984 */     if (this.mwm_hints == null) {
/* 1985 */       this.mwm_hints = new PropMwmHints();
/* 1986 */       if (!XWM.XA_MWM_HINTS.getAtomData(getWindow(), this.mwm_hints.pData, 5)) {
/* 1987 */         this.mwm_hints.zero();
/*      */       }
/*      */     }
/* 1990 */     return this.mwm_hints;
/*      */   }
/*      */ 
/*      */   public void setMWMHints(PropMwmHints paramPropMwmHints) {
/* 1994 */     this.mwm_hints = paramPropMwmHints;
/* 1995 */     if (paramPropMwmHints != null)
/* 1996 */       XWM.XA_MWM_HINTS.setAtomData(getWindow(), this.mwm_hints.pData, 5);
/*      */   }
/*      */ 
/*      */   protected void updateDropTarget()
/*      */   {
/* 2001 */     XToolkit.awtLock();
/*      */     try {
/* 2003 */       if (this.dropTargetCount > 0) {
/* 2004 */         long l = getWindow();
/* 2005 */         if (l != 0L) {
/* 2006 */           XDropTargetRegistry.getRegistry().unregisterDropSite(l);
/* 2007 */           XDropTargetRegistry.getRegistry().registerDropSite(l);
/*      */         }
/*      */       }
/*      */     } finally {
/* 2011 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setGrab(boolean paramBoolean) {
/* 2016 */     this.grab = paramBoolean;
/* 2017 */     if (paramBoolean) {
/* 2018 */       this.pressTarget = this;
/* 2019 */       grabInput();
/*      */     } else {
/* 2021 */       ungrabInput();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isGrabbed() {
/* 2026 */     return (this.grab) && (XAwtState.getGrabWindow() == this);
/*      */   }
/*      */ 
/*      */   public void handleXCrossingEvent(XEvent paramXEvent) {
/* 2030 */     XCrossingEvent localXCrossingEvent = paramXEvent.get_xcrossing();
/* 2031 */     if (grabLog.isLoggable(500)) {
/* 2032 */       grabLog.fine("{0}, when grabbed {1}, contains {2}", new Object[] { localXCrossingEvent, Boolean.valueOf(isGrabbed()), Boolean.valueOf(containsGlobal(localXCrossingEvent.get_x_root(), localXCrossingEvent.get_y_root())) });
/*      */     }
/*      */ 
/* 2035 */     if (isGrabbed())
/*      */     {
/* 2043 */       XBaseWindow localXBaseWindow = XToolkit.windowToXWindow(localXCrossingEvent.get_window());
/* 2044 */       grabLog.finer("  -  Grab event target {0}", new Object[] { localXBaseWindow });
/* 2045 */       if ((localXBaseWindow != null) && (localXBaseWindow != this)) {
/* 2046 */         localXBaseWindow.dispatchEvent(paramXEvent);
/* 2047 */         return;
/*      */       }
/*      */     }
/* 2050 */     super.handleXCrossingEvent(paramXEvent);
/*      */   }
/*      */ 
/*      */   public void handleMotionNotify(XEvent paramXEvent) {
/* 2054 */     XMotionEvent localXMotionEvent = paramXEvent.get_xmotion();
/* 2055 */     if (grabLog.isLoggable(500)) {
/* 2056 */       grabLog.finer("{0}, when grabbed {1}, contains {2}", new Object[] { localXMotionEvent, Boolean.valueOf(isGrabbed()), Boolean.valueOf(containsGlobal(localXMotionEvent.get_x_root(), localXMotionEvent.get_y_root())) });
/*      */     }
/*      */ 
/* 2059 */     if (isGrabbed()) {
/* 2060 */       int i = 0;
/* 2061 */       int j = ((SunToolkit)Toolkit.getDefaultToolkit()).getNumberOfButtons();
/*      */ 
/* 2063 */       for (int k = 0; k < j; k++)
/*      */       {
/* 2065 */         if ((k != 4) && (k != 5)) {
/* 2066 */           i = (i != 0) || ((localXMotionEvent.get_state() & XConstants.buttonsMask[k]) != 0) ? 1 : 0;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2073 */       Object localObject = XToolkit.windowToXWindow(localXMotionEvent.get_window());
/* 2074 */       if ((i != 0) && (this.pressTarget != localObject))
/*      */       {
/* 2080 */         localObject = this.pressTarget.isVisible() ? this.pressTarget : this;
/* 2081 */         localXMotionEvent.set_window(((XBaseWindow)localObject).getWindow());
/* 2082 */         Point localPoint = ((XBaseWindow)localObject).toLocal(localXMotionEvent.get_x_root(), localXMotionEvent.get_y_root());
/* 2083 */         localXMotionEvent.set_x(localPoint.x);
/* 2084 */         localXMotionEvent.set_y(localPoint.y);
/*      */       }
/* 2086 */       grabLog.finer("  -  Grab event target {0}", new Object[] { localObject });
/* 2087 */       if ((localObject != null) && 
/* 2088 */         (localObject != getContentXWindow()) && (localObject != this)) {
/* 2089 */         ((XBaseWindow)localObject).dispatchEvent(paramXEvent);
/* 2090 */         return;
/*      */       }
/*      */ 
/* 2096 */       if ((!containsGlobal(localXMotionEvent.get_x_root(), localXMotionEvent.get_y_root())) && (i == 0))
/*      */       {
/* 2098 */         return;
/*      */       }
/*      */     }
/* 2101 */     super.handleMotionNotify(paramXEvent);
/*      */   }
/*      */ 
/*      */   public void handleButtonPressRelease(XEvent paramXEvent)
/*      */   {
/* 2108 */     XButtonEvent localXButtonEvent = paramXEvent.get_xbutton();
/*      */ 
/* 2115 */     if (localXButtonEvent.get_button() > 20) {
/* 2116 */       return;
/*      */     }
/* 2118 */     if (grabLog.isLoggable(500)) {
/* 2119 */       grabLog.fine("{0}, when grabbed {1}, contains {2} ({3}, {4}, {5}x{6})", new Object[] { localXButtonEvent, Boolean.valueOf(isGrabbed()), Boolean.valueOf(containsGlobal(localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root())), Integer.valueOf(getAbsoluteX()), Integer.valueOf(getAbsoluteY()), Integer.valueOf(getWidth()), Integer.valueOf(getHeight()) });
/*      */     }
/*      */ 
/* 2122 */     if (isGrabbed())
/*      */     {
/* 2127 */       Object localObject1 = XToolkit.windowToXWindow(localXButtonEvent.get_window());
/*      */       try {
/* 2129 */         grabLog.finer("  -  Grab event target {0} (press target {1})", new Object[] { localObject1, this.pressTarget });
/*      */         Object localObject2;
/* 2130 */         if ((localXButtonEvent.get_type() == 4) && (localXButtonEvent.get_button() == XConstants.buttons[0]))
/*      */         {
/* 2134 */           this.pressTarget = ((XBaseWindow)localObject1);
/* 2135 */         } else if ((localXButtonEvent.get_type() == 5) && (localXButtonEvent.get_button() == XConstants.buttons[0]) && (this.pressTarget != localObject1))
/*      */         {
/* 2142 */           localObject1 = this.pressTarget.isVisible() ? this.pressTarget : this;
/* 2143 */           localXButtonEvent.set_window(((XBaseWindow)localObject1).getWindow());
/* 2144 */           localObject2 = ((XBaseWindow)localObject1).toLocal(localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root());
/* 2145 */           localXButtonEvent.set_x(((Point)localObject2).x);
/* 2146 */           localXButtonEvent.set_y(((Point)localObject2).y);
/* 2147 */           this.pressTarget = this;
/*      */         }
/*      */         Window localWindow1;
/* 2149 */         if ((localObject1 != null) && (localObject1 != getContentXWindow()) && (localObject1 != this)) {
/* 2150 */           ((XBaseWindow)localObject1).dispatchEvent(paramXEvent);
/*      */ 
/* 2154 */           if (localObject1 != null)
/*      */           {
/* 2158 */             if (((localObject1 == this) || (localObject1 == getContentXWindow())) && (!containsGlobal(localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root())))
/*      */             {
/* 2162 */               if (localXButtonEvent.get_type() == 4) { grabLog.fine("Generating UngrabEvent on {0} because not inside of shell", new Object[] { this });
/* 2164 */                 postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */                 return;
/*      */               }
/*      */             }
/* 2169 */             localObject2 = ((XBaseWindow)localObject1).getToplevelXWindow();
/* 2170 */             if (localObject2 != null) {
/* 2171 */               localWindow1 = (Window)((XWindowPeer)localObject2).target;
/* 2172 */               while ((localWindow1 != null) && (localObject2 != this) && (!(localObject2 instanceof XDialogPeer))) {
/* 2173 */                 localWindow1 = (Window)AWTAccessor.getComponentAccessor().getParent(localWindow1);
/* 2174 */                 if (localWindow1 != null) {
/* 2175 */                   localObject2 = (XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow1);
/*      */                 }
/*      */               }
/* 2178 */               if ((localWindow1 == null) || ((localWindow1 != this.target) && ((localWindow1 instanceof Dialog))))
/*      */               {
/* 2182 */                 grabLog.fine("Generating UngrabEvent on {0} because hierarchy ended", new Object[] { this });
/* 2183 */                 postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */               }
/*      */             } else { grabLog.fine("Generating UngrabEvent on {0} because toplevel is null", new Object[] { this });
/* 2188 */               postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */               return;
/*      */             }
/*      */           }
/*      */           else {
/* 2193 */             grabLog.fine("Generating UngrabEvent on because target is null {0}", new Object[] { this });
/* 2194 */             postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */             return;
/*      */           }
/*      */           return;
/*      */         }
/* 2154 */         if (localObject1 != null)
/*      */         {
/* 2158 */           if (((localObject1 == this) || (localObject1 == getContentXWindow())) && (!containsGlobal(localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root())))
/*      */           {
/* 2162 */             if (localXButtonEvent.get_type() == 4) { grabLog.fine("Generating UngrabEvent on {0} because not inside of shell", new Object[] { this });
/* 2164 */               postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */               return;
/*      */             }
/*      */           }
/* 2169 */           localObject2 = ((XBaseWindow)localObject1).getToplevelXWindow();
/* 2170 */           if (localObject2 != null) {
/* 2171 */             localWindow1 = (Window)((XWindowPeer)localObject2).target;
/* 2172 */             while ((localWindow1 != null) && (localObject2 != this) && (!(localObject2 instanceof XDialogPeer))) {
/* 2173 */               localWindow1 = (Window)AWTAccessor.getComponentAccessor().getParent(localWindow1);
/* 2174 */               if (localWindow1 != null) {
/* 2175 */                 localObject2 = (XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow1);
/*      */               }
/*      */             }
/* 2178 */             if ((localWindow1 == null) || ((localWindow1 != this.target) && ((localWindow1 instanceof Dialog))))
/*      */             {
/* 2182 */               grabLog.fine("Generating UngrabEvent on {0} because hierarchy ended", new Object[] { this });
/* 2183 */               postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */             }
/*      */           } else { grabLog.fine("Generating UngrabEvent on {0} because toplevel is null", new Object[] { this });
/* 2188 */             postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */             return;
/*      */           }
/* 2191 */           break label1132;
/*      */         }
/* 2193 */         grabLog.fine("Generating UngrabEvent on because target is null {0}", new Object[] { this });
/* 2194 */         postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */         return;
/*      */       }
/*      */       finally
/*      */       {
/* 2154 */         if (localObject1 != null)
/*      */         {
/* 2158 */           if (((localObject1 == this) || (localObject1 == getContentXWindow())) && (!containsGlobal(localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root())))
/*      */           {
/* 2162 */             if (localXButtonEvent.get_type() == 4) {
/* 2163 */               grabLog.fine("Generating UngrabEvent on {0} because not inside of shell", new Object[] { this });
/* 2164 */               postEventToEventQueue(new UngrabEvent(getEventSource()));
/* 2165 */               return;
/*      */             }
/*      */           }
/*      */ 
/* 2169 */           XWindowPeer localXWindowPeer = ((XBaseWindow)localObject1).getToplevelXWindow();
/* 2170 */           if (localXWindowPeer != null) {
/* 2171 */             Window localWindow2 = (Window)localXWindowPeer.target;
/* 2172 */             while ((localWindow2 != null) && (localXWindowPeer != this) && (!(localXWindowPeer instanceof XDialogPeer))) {
/* 2173 */               localWindow2 = (Window)AWTAccessor.getComponentAccessor().getParent(localWindow2);
/* 2174 */               if (localWindow2 != null) {
/* 2175 */                 localXWindowPeer = (XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow2);
/*      */               }
/*      */             }
/* 2178 */             if ((localWindow2 == null) || ((localWindow2 != this.target) && ((localWindow2 instanceof Dialog))))
/*      */             {
/* 2182 */               grabLog.fine("Generating UngrabEvent on {0} because hierarchy ended", new Object[] { this });
/* 2183 */               postEventToEventQueue(new UngrabEvent(getEventSource()));
/*      */             }
/*      */           }
/*      */           else {
/* 2187 */             grabLog.fine("Generating UngrabEvent on {0} because toplevel is null", new Object[] { this });
/* 2188 */             postEventToEventQueue(new UngrabEvent(getEventSource()));
/* 2189 */             return;
/*      */           }
/*      */         }
/*      */         else {
/* 2193 */           grabLog.fine("Generating UngrabEvent on because target is null {0}", new Object[] { this });
/* 2194 */           postEventToEventQueue(new UngrabEvent(getEventSource()));
/* 2195 */           return; }  } throw localObject3;
/*      */     }
/*      */ 
/* 2199 */     label1132: super.handleButtonPressRelease(paramXEvent);
/*      */   }
/*      */ 
/*      */   public void print(Graphics paramGraphics)
/*      */   {
/* 2205 */     Shape localShape = AWTAccessor.getWindowAccessor().getShape((Window)this.target);
/* 2206 */     if (localShape != null) {
/* 2207 */       paramGraphics.setClip(localShape);
/*      */     }
/* 2209 */     super.print(paramGraphics);
/*      */   }
/*      */ 
/*      */   public void setOpacity(float paramFloat)
/*      */   {
/* 2215 */     long l = ()(paramFloat * 4.294967E+09F);
/* 2216 */     if (l < 0L) {
/* 2217 */       l = 0L;
/*      */     }
/* 2219 */     if (l > 4294967295L) {
/* 2220 */       l = 4294967295L;
/*      */     }
/*      */ 
/* 2223 */     XAtom localXAtom = XAtom.get("_NET_WM_WINDOW_OPACITY");
/*      */ 
/* 2225 */     if (l == 4294967295L)
/* 2226 */       localXAtom.DeleteProperty(getWindow());
/*      */     else
/* 2228 */       localXAtom.setCard32Property(getWindow(), l);
/*      */   }
/*      */ 
/*      */   public void setOpaque(boolean paramBoolean)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void updateWindow()
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWindowPeer
 * JD-Core Version:    0.6.2
 */