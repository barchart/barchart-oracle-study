/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.AWTKeyStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.InputEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.PaintEvent;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Method;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.AWTEventAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.AWTAccessor.KeyEventAccessor;
/*      */ import sun.awt.EmbeddedFrame;
/*      */ import sun.awt.ExtendedKeyCodes;
/*      */ import sun.awt.PaintEventDispatcher;
/*      */ import sun.awt.PeerEvent;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.X11ComponentPeer;
/*      */ import sun.awt.X11GraphicsConfig;
/*      */ import sun.awt.image.PixelConverter;
/*      */ import sun.java2d.SunGraphics2D;
/*      */ import sun.java2d.SurfaceData;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class XWindow extends XBaseWindow
/*      */   implements X11ComponentPeer
/*      */ {
/*   48 */   private static PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XWindow");
/*   49 */   private static PlatformLogger insLog = PlatformLogger.getLogger("sun.awt.X11.insets.XWindow");
/*   50 */   private static PlatformLogger eventLog = PlatformLogger.getLogger("sun.awt.X11.event.XWindow");
/*   51 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.X11.focus.XWindow");
/*   52 */   private static PlatformLogger keyEventLog = PlatformLogger.getLogger("sun.awt.X11.kye.XWindow");
/*      */   private static final int AWT_MULTICLICK_SMUDGE = 4;
/*   59 */   static int rbutton = 0;
/*   60 */   static int lastX = 0; static int lastY = 0;
/*   61 */   static long lastTime = 0L;
/*   62 */   static long lastButton = 0L;
/*   63 */   static WeakReference lastWindowRef = null;
/*   64 */   static int clickCount = 0;
/*      */ 
/*   67 */   int oldWidth = -1;
/*   68 */   int oldHeight = -1;
/*      */   protected PropMwmHints mwm_hints;
/*      */   protected static XAtom wm_protocols;
/*      */   protected static XAtom wm_delete_window;
/*      */   protected static XAtom wm_take_focus;
/*      */   private boolean stateChanged;
/*      */   private int savedState;
/*      */   XWindowAttributesData winAttr;
/*      */   protected X11GraphicsConfig graphicsConfig;
/*      */   protected AwtGraphicsConfigData graphicsConfigData;
/*      */   private boolean reparented;
/*      */   XWindow parent;
/*      */   Component target;
/*   89 */   private static int JAWT_LOCK_ERROR = 1;
/*   90 */   private static int JAWT_LOCK_CLIP_CHANGED = 2;
/*   91 */   private static int JAWT_LOCK_BOUNDS_CHANGED = 4;
/*   92 */   private static int JAWT_LOCK_SURFACE_CHANGED = 8;
/*   93 */   private int drawState = JAWT_LOCK_CLIP_CHANGED | JAWT_LOCK_BOUNDS_CHANGED | JAWT_LOCK_SURFACE_CHANGED;
/*      */   public static final String TARGET = "target";
/*      */   public static final String REPARENTED = "reparented";
/*      */   SurfaceData surfaceData;
/*      */   XRepaintArea paintArea;
/*      */   private static Font defaultFont;
/*  121 */   private int mouseButtonClickAllowed = 0;
/*      */   static Method m_sendMessage;
/*      */   Color backgroundColor;
/*      */   private boolean mouseAboveMe;
/* 1485 */   private boolean fullScreenExclusiveModeState = false;
/*      */ 
/*      */   static synchronized Font getDefaultFont()
/*      */   {
/*  108 */     if (null == defaultFont) {
/*  109 */       defaultFont = new Font("Dialog", 0, 12);
/*      */     }
/*  111 */     return defaultFont;
/*      */   }
/*      */ 
/*      */   native int getNativeColor(Color paramColor, GraphicsConfiguration paramGraphicsConfiguration);
/*      */ 
/*      */   native void getWMInsets(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6);
/*      */ 
/*      */   native long getTopWindow(long paramLong1, long paramLong2);
/*      */ 
/*      */   native void getWindowBounds(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   XWindow(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  134 */     super(paramXCreateWindowParams);
/*      */   }
/*      */ 
/*      */   XWindow() {
/*      */   }
/*      */ 
/*      */   XWindow(long paramLong, Rectangle paramRectangle) {
/*  141 */     super(new XCreateWindowParams(new Object[] { "bounds", paramRectangle, "parent window", Long.valueOf(paramLong) }));
/*      */   }
/*      */ 
/*      */   XWindow(Component paramComponent, long paramLong, Rectangle paramRectangle)
/*      */   {
/*  147 */     super(new XCreateWindowParams(new Object[] { "bounds", paramRectangle, "parent window", Long.valueOf(paramLong), "target", paramComponent }));
/*      */   }
/*      */ 
/*      */   XWindow(Component paramComponent, long paramLong)
/*      */   {
/*  154 */     this(paramComponent, paramLong, new Rectangle(paramComponent.getBounds()));
/*      */   }
/*      */ 
/*      */   XWindow(Component paramComponent) {
/*  158 */     this(paramComponent, paramComponent.getParent() == null ? 0L : getParentWindowID(paramComponent), new Rectangle(paramComponent.getBounds()));
/*      */   }
/*      */ 
/*      */   XWindow(Object paramObject) {
/*  162 */     this(null, 0L, null);
/*      */   }
/*      */ 
/*      */   XWindow(long paramLong)
/*      */   {
/*  168 */     super(new XCreateWindowParams(new Object[] { "parent window", Long.valueOf(paramLong), "reparented", Boolean.TRUE, "embedded", Boolean.TRUE }));
/*      */   }
/*      */ 
/*      */   protected void initGraphicsConfiguration()
/*      */   {
/*  175 */     this.graphicsConfig = ((X11GraphicsConfig)this.target.getGraphicsConfiguration());
/*  176 */     this.graphicsConfigData = new AwtGraphicsConfigData(this.graphicsConfig.getAData());
/*      */   }
/*      */ 
/*      */   void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*  180 */     super.preInit(paramXCreateWindowParams);
/*  181 */     this.reparented = Boolean.TRUE.equals(paramXCreateWindowParams.get("reparented"));
/*      */ 
/*  183 */     this.target = ((Component)paramXCreateWindowParams.get("target"));
/*      */ 
/*  185 */     initGraphicsConfiguration();
/*      */ 
/*  187 */     AwtGraphicsConfigData localAwtGraphicsConfigData = getGraphicsConfigurationData();
/*  188 */     X11GraphicsConfig localX11GraphicsConfig = (X11GraphicsConfig)getGraphicsConfiguration();
/*  189 */     XVisualInfo localXVisualInfo = localAwtGraphicsConfigData.get_awt_visInfo();
/*  190 */     paramXCreateWindowParams.putIfNull("event mask", 2269311L);
/*      */ 
/*  195 */     if (this.target != null)
/*  196 */       paramXCreateWindowParams.putIfNull("bounds", new Rectangle(this.target.getBounds()));
/*      */     else {
/*  198 */       paramXCreateWindowParams.putIfNull("bounds", new Rectangle(0, 0, 1, 1));
/*      */     }
/*  200 */     paramXCreateWindowParams.putIfNull("border pixel", Long.valueOf(0L));
/*  201 */     getColorModel();
/*  202 */     paramXCreateWindowParams.putIfNull("color map", localAwtGraphicsConfigData.get_awt_cmap());
/*  203 */     paramXCreateWindowParams.putIfNull("visual depth", localAwtGraphicsConfigData.get_awt_depth());
/*  204 */     paramXCreateWindowParams.putIfNull("visual class", Integer.valueOf(1));
/*  205 */     paramXCreateWindowParams.putIfNull("visual", localXVisualInfo.get_visual());
/*  206 */     paramXCreateWindowParams.putIfNull("value mask", 10248L);
/*  207 */     Long localLong = (Long)paramXCreateWindowParams.get("parent window");
/*  208 */     if ((localLong == null) || (localLong.longValue() == 0L)) {
/*  209 */       XToolkit.awtLock();
/*      */       try {
/*  211 */         int i = localXVisualInfo.get_screen();
/*  212 */         if (i != -1)
/*  213 */           paramXCreateWindowParams.add("parent window", XlibWrapper.RootWindow(XToolkit.getDisplay(), i));
/*      */         else
/*  215 */           paramXCreateWindowParams.add("parent window", XToolkit.getDefaultRootWindow());
/*      */       }
/*      */       finally {
/*  218 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/*      */ 
/*  222 */     this.paintArea = new XRepaintArea();
/*  223 */     if (this.target != null) {
/*  224 */       this.parent = getParentXWindowObject(this.target.getParent());
/*      */     }
/*      */ 
/*  227 */     paramXCreateWindowParams.putIfNull("backing store", XToolkit.getBackingStoreType());
/*      */ 
/*  229 */     XToolkit.awtLock();
/*      */     try {
/*  231 */       if (wm_protocols == null) {
/*  232 */         wm_protocols = XAtom.get("WM_PROTOCOLS");
/*  233 */         wm_delete_window = XAtom.get("WM_DELETE_WINDOW");
/*  234 */         wm_take_focus = XAtom.get("WM_TAKE_FOCUS");
/*      */       }
/*      */     }
/*      */     finally {
/*  238 */       XToolkit.awtUnlock();
/*      */     }
/*  240 */     this.winAttr = new XWindowAttributesData();
/*  241 */     this.savedState = 0;
/*      */   }
/*      */ 
/*      */   void postInit(XCreateWindowParams paramXCreateWindowParams) {
/*  245 */     super.postInit(paramXCreateWindowParams);
/*      */ 
/*  247 */     setWMClass(getWMClass());
/*      */ 
/*  249 */     this.surfaceData = this.graphicsConfig.createSurfaceData(this);
/*      */     Color localColor;
/*  251 */     if ((this.target != null) && ((localColor = this.target.getBackground()) != null))
/*      */     {
/*  257 */       xSetBackground(localColor);
/*      */     }
/*      */   }
/*      */ 
/*      */   public GraphicsConfiguration getGraphicsConfiguration() {
/*  262 */     if (this.graphicsConfig == null) {
/*  263 */       initGraphicsConfiguration();
/*      */     }
/*  265 */     return this.graphicsConfig;
/*      */   }
/*      */ 
/*      */   public AwtGraphicsConfigData getGraphicsConfigurationData() {
/*  269 */     if (this.graphicsConfigData == null) {
/*  270 */       initGraphicsConfiguration();
/*      */     }
/*  272 */     return this.graphicsConfigData;
/*      */   }
/*      */ 
/*      */   protected String[] getWMClass() {
/*  276 */     return new String[] { XToolkit.getCorrectXIDString(getClass().getName()), XToolkit.getAWTAppClassName() };
/*      */   }
/*      */ 
/*      */   void setReparented(boolean paramBoolean) {
/*  280 */     this.reparented = paramBoolean;
/*      */   }
/*      */ 
/*      */   boolean isReparented() {
/*  284 */     return this.reparented;
/*      */   }
/*      */ 
/*      */   static long getParentWindowID(Component paramComponent)
/*      */   {
/*  289 */     ComponentPeer localComponentPeer = paramComponent.getParent().getPeer();
/*  290 */     Container localContainer = paramComponent.getParent();
/*  291 */     while (!(localComponentPeer instanceof XWindow))
/*      */     {
/*  293 */       localContainer = localContainer.getParent();
/*  294 */       localComponentPeer = localContainer.getPeer();
/*      */     }
/*      */ 
/*  297 */     if ((localComponentPeer != null) && ((localComponentPeer instanceof XWindow)))
/*  298 */       return ((XWindow)localComponentPeer).getContentWindow();
/*  299 */     return 0L;
/*      */   }
/*      */ 
/*      */   static XWindow getParentXWindowObject(Component paramComponent)
/*      */   {
/*  304 */     if (paramComponent == null) return null;
/*  305 */     Container localContainer = paramComponent.getParent();
/*  306 */     if (localContainer == null) return null;
/*  307 */     ComponentPeer localComponentPeer = localContainer.getPeer();
/*  308 */     if (localComponentPeer == null) return null;
/*  309 */     while ((localComponentPeer != null) && (!(localComponentPeer instanceof XWindow)))
/*      */     {
/*  311 */       localContainer = localContainer.getParent();
/*  312 */       localComponentPeer = localContainer.getPeer();
/*      */     }
/*  314 */     if ((localComponentPeer != null) && ((localComponentPeer instanceof XWindow)))
/*  315 */       return (XWindow)localComponentPeer;
/*  316 */     return null;
/*      */   }
/*      */ 
/*      */   boolean isParentOf(XWindow paramXWindow)
/*      */   {
/*  321 */     if ((!(this.target instanceof Container)) || (paramXWindow == null) || (paramXWindow.getTarget() == null)) {
/*  322 */       return false;
/*      */     }
/*  324 */     Container localContainer = AWTAccessor.getComponentAccessor().getParent(paramXWindow.target);
/*  325 */     while ((localContainer != null) && (localContainer != this.target)) {
/*  326 */       localContainer = AWTAccessor.getComponentAccessor().getParent(localContainer);
/*      */     }
/*  328 */     return localContainer == this.target;
/*      */   }
/*      */ 
/*      */   public Object getTarget() {
/*  332 */     return this.target;
/*      */   }
/*      */   public Component getEventSource() {
/*  335 */     return this.target;
/*      */   }
/*      */ 
/*      */   public ColorModel getColorModel(int paramInt) {
/*  339 */     return this.graphicsConfig.getColorModel(paramInt);
/*      */   }
/*      */ 
/*      */   public ColorModel getColorModel() {
/*  343 */     if (this.graphicsConfig != null) {
/*  344 */       return this.graphicsConfig.getColorModel();
/*      */     }
/*      */ 
/*  347 */     return XToolkit.getStaticColorModel();
/*      */   }
/*      */ 
/*      */   Graphics getGraphics(SurfaceData paramSurfaceData, Color paramColor1, Color paramColor2, Font paramFont)
/*      */   {
/*  352 */     if (paramSurfaceData == null) return null;
/*      */ 
/*  354 */     Component localComponent = this.target;
/*      */ 
/*  357 */     Object localObject1 = paramColor2;
/*  358 */     if (localObject1 == null) {
/*  359 */       localObject1 = SystemColor.window;
/*      */     }
/*  361 */     Object localObject2 = paramColor1;
/*  362 */     if (localObject2 == null) {
/*  363 */       localObject2 = SystemColor.windowText;
/*      */     }
/*  365 */     Font localFont = paramFont;
/*  366 */     if (localFont == null) {
/*  367 */       localFont = getDefaultFont();
/*      */     }
/*  369 */     return new SunGraphics2D(paramSurfaceData, (Color)localObject2, (Color)localObject1, localFont);
/*      */   }
/*      */ 
/*      */   public Graphics getGraphics() {
/*  373 */     return getGraphics(this.surfaceData, this.target.getForeground(), this.target.getBackground(), this.target.getFont());
/*      */   }
/*      */ 
/*      */   public FontMetrics getFontMetrics(Font paramFont)
/*      */   {
/*  380 */     return Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
/*      */   }
/*      */ 
/*      */   public Rectangle getTargetBounds() {
/*  384 */     return this.target.getBounds();
/*      */   }
/*      */ 
/*      */   boolean prePostEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  392 */     return false;
/*      */   }
/*      */ 
/*      */   static void sendEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  398 */     SunToolkit.setSystemGenerated(paramAWTEvent);
/*  399 */     PeerEvent localPeerEvent = new PeerEvent(Toolkit.getDefaultToolkit(), new Runnable() {
/*      */       public void run() {
/*  401 */         AWTAccessor.getAWTEventAccessor().setPosted(this.val$e);
/*  402 */         ((Component)this.val$e.getSource()).dispatchEvent(this.val$e);
/*      */       }
/*      */     }
/*  405 */     , 2L);
/*      */ 
/*  405 */     if ((focusLog.isLoggable(400)) && ((paramAWTEvent instanceof FocusEvent))) focusLog.finer("Sending " + paramAWTEvent);
/*  406 */     XToolkit.postEvent(XToolkit.targetToAppContext(paramAWTEvent.getSource()), localPeerEvent);
/*      */   }
/*      */ 
/*      */   void postEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  416 */     XToolkit.postEvent(XToolkit.targetToAppContext(paramAWTEvent.getSource()), paramAWTEvent);
/*      */   }
/*      */ 
/*      */   static void postEventStatic(AWTEvent paramAWTEvent) {
/*  420 */     XToolkit.postEvent(XToolkit.targetToAppContext(paramAWTEvent.getSource()), paramAWTEvent);
/*      */   }
/*      */ 
/*      */   public void postEventToEventQueue(AWTEvent paramAWTEvent)
/*      */   {
/*  425 */     if (!prePostEvent(paramAWTEvent))
/*      */     {
/*  427 */       postEvent(paramAWTEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean doEraseBackground()
/*      */   {
/*  433 */     return true;
/*      */   }
/*      */ 
/*      */   public final void xSetBackground(Color paramColor)
/*      */   {
/*  441 */     XToolkit.awtLock();
/*      */     try {
/*  443 */       winBackground(paramColor);
/*      */ 
/*  446 */       if (!doEraseBackground())
/*      */       {
/*      */         return;
/*      */       }
/*      */ 
/*  455 */       ColorModel localColorModel = getColorModel();
/*  456 */       int i = PixelConverter.instance.rgbToPixel(paramColor.getRGB(), localColorModel);
/*  457 */       XlibWrapper.XSetWindowBackground(XToolkit.getDisplay(), getContentWindow(), i);
/*      */     }
/*      */     finally {
/*  460 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor) {
/*  465 */     xSetBackground(paramColor);
/*      */   }
/*      */ 
/*      */   void winBackground(Color paramColor)
/*      */   {
/*  470 */     this.backgroundColor = paramColor;
/*      */   }
/*      */ 
/*      */   public Color getWinBackground() {
/*  474 */     Color localColor = null;
/*      */ 
/*  476 */     if (this.backgroundColor != null)
/*  477 */       localColor = this.backgroundColor;
/*  478 */     else if (this.parent != null) {
/*  479 */       localColor = this.parent.getWinBackground();
/*      */     }
/*      */ 
/*  482 */     if ((localColor instanceof SystemColor)) {
/*  483 */       localColor = new Color(localColor.getRGB());
/*      */     }
/*      */ 
/*  486 */     return localColor;
/*      */   }
/*      */ 
/*      */   public boolean isEmbedded() {
/*  490 */     return this.embedded;
/*      */   }
/*      */ 
/*      */   public void repaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  494 */     if (!isVisible()) {
/*  495 */       return;
/*      */     }
/*  497 */     Graphics localGraphics = getGraphics();
/*  498 */     if (localGraphics != null)
/*      */       try {
/*  500 */         localGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
/*  501 */         paint(localGraphics);
/*      */       } finally {
/*  503 */         localGraphics.dispose();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void repaint()
/*      */   {
/*  509 */     if (!isVisible()) {
/*  510 */       return;
/*      */     }
/*  512 */     Graphics localGraphics = getGraphics();
/*  513 */     if (localGraphics != null)
/*      */       try {
/*  515 */         paint(localGraphics);
/*      */       } finally {
/*  517 */         localGraphics.dispose();
/*      */       }
/*      */   }
/*      */ 
/*      */   void paint(Graphics paramGraphics)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void flush()
/*      */   {
/*  527 */     XToolkit.awtLock();
/*      */     try {
/*  529 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*      */     } finally {
/*  531 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void popup(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  537 */     xSetBounds(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void handleExposeEvent(XEvent paramXEvent) {
/*  541 */     super.handleExposeEvent(paramXEvent);
/*  542 */     XExposeEvent localXExposeEvent = paramXEvent.get_xexpose();
/*  543 */     if (isEventDisabled(paramXEvent)) {
/*  544 */       return;
/*      */     }
/*  546 */     int i = localXExposeEvent.get_x();
/*  547 */     int j = localXExposeEvent.get_y();
/*  548 */     int k = localXExposeEvent.get_width();
/*  549 */     int m = localXExposeEvent.get_height();
/*      */ 
/*  551 */     Component localComponent = getEventSource();
/*  552 */     AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/*      */ 
/*  554 */     if ((!localComponentAccessor.getIgnoreRepaint(localComponent)) && (localComponentAccessor.getWidth(localComponent) != 0) && (localComponentAccessor.getHeight(localComponent) != 0))
/*      */     {
/*  558 */       handleExposeEvent(localComponent, i, j, k, m);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleExposeEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  563 */     PaintEvent localPaintEvent = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*  565 */     if (localPaintEvent != null)
/*  566 */       postEventToEventQueue(localPaintEvent);
/*      */   }
/*      */ 
/*      */   static int getModifiers(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  571 */     return getModifiers(paramInt1, paramInt2, paramInt3, 0, false);
/*      */   }
/*      */ 
/*      */   static int getModifiers(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
/*  575 */     int i = 0;
/*      */ 
/*  577 */     if ((((paramInt1 & 0x1) != 0 ? 1 : 0) ^ (paramInt3 == 16 ? 1 : 0)) != 0) {
/*  578 */       i |= 64;
/*      */     }
/*  580 */     if ((((paramInt1 & 0x4) != 0 ? 1 : 0) ^ (paramInt3 == 17 ? 1 : 0)) != 0) {
/*  581 */       i |= 128;
/*      */     }
/*  583 */     if ((((paramInt1 & XToolkit.metaMask) != 0 ? 1 : 0) ^ (paramInt3 == 157 ? 1 : 0)) != 0) {
/*  584 */       i |= 256;
/*      */     }
/*  586 */     if ((((paramInt1 & XToolkit.altMask) != 0 ? 1 : 0) ^ (paramInt3 == 18 ? 1 : 0)) != 0) {
/*  587 */       i |= 512;
/*      */     }
/*  589 */     if ((((paramInt1 & XToolkit.modeSwitchMask) != 0 ? 1 : 0) ^ (paramInt3 == 65406 ? 1 : 0)) != 0) {
/*  590 */       i |= 8192;
/*      */     }
/*      */ 
/*  598 */     for (int j = 0; j < XConstants.buttonsMask.length; j++)
/*      */     {
/*  603 */       if (((paramInt1 & XConstants.buttonsMask[j]) != 0 ? 1 : 0) != (paramInt2 == XConstants.buttons[j] ? 1 : 0))
/*      */       {
/*  605 */         if (!paramBoolean) {
/*  606 */           i |= InputEvent.getMaskForButton(j + 1);
/*      */         }
/*      */       }
/*      */     }
/*  610 */     return i;
/*      */   }
/*      */ 
/*      */   static int getXModifiers(AWTKeyStroke paramAWTKeyStroke) {
/*  614 */     int i = paramAWTKeyStroke.getModifiers();
/*  615 */     int j = 0;
/*  616 */     if ((i & 0x41) != 0) {
/*  617 */       j |= 1;
/*      */     }
/*  619 */     if ((i & 0x82) != 0) {
/*  620 */       j |= 4;
/*      */     }
/*  622 */     if ((i & 0x208) != 0) {
/*  623 */       j |= XToolkit.altMask;
/*      */     }
/*  625 */     if ((i & 0x104) != 0) {
/*  626 */       j |= XToolkit.metaMask;
/*      */     }
/*  628 */     if ((i & 0x2020) != 0) {
/*  629 */       j |= XToolkit.modeSwitchMask;
/*      */     }
/*  631 */     return j;
/*      */   }
/*      */ 
/*      */   static int getRightButtonNumber()
/*      */   {
/*  639 */     if (rbutton == 0) {
/*  640 */       XToolkit.awtLock();
/*      */       try {
/*  642 */         rbutton = XlibWrapper.XGetPointerMapping(XToolkit.getDisplay(), XlibWrapper.ibuffer, 3);
/*      */       }
/*      */       finally {
/*  645 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/*  648 */     return rbutton;
/*      */   }
/*      */ 
/*      */   static int getMouseMovementSmudge()
/*      */   {
/*  653 */     return 4;
/*      */   }
/*      */ 
/*      */   public void handleButtonPressRelease(XEvent paramXEvent) {
/*  657 */     super.handleButtonPressRelease(paramXEvent);
/*  658 */     XButtonEvent localXButtonEvent = paramXEvent.get_xbutton();
/*  659 */     if (isEventDisabled(paramXEvent)) {
/*  660 */       return;
/*      */     }
/*  662 */     if (eventLog.isLoggable(500)) eventLog.fine(localXButtonEvent.toString());
/*      */ 
/*  665 */     boolean bool1 = false;
/*  666 */     int j = 0;
/*  667 */     boolean bool2 = false;
/*  668 */     int k = localXButtonEvent.get_button();
/*      */ 
/*  674 */     if (k > 20) {
/*  675 */       return;
/*      */     }
/*  677 */     int m = paramXEvent.get_type();
/*  678 */     long l1 = localXButtonEvent.get_time();
/*  679 */     long l2 = XToolkit.nowMillisUTC_offset(l1);
/*      */ 
/*  681 */     int n = localXButtonEvent.get_x();
/*  682 */     int i1 = localXButtonEvent.get_y();
/*      */     Object localObject;
/*  683 */     if (paramXEvent.get_xany().get_window() != this.window) {
/*  684 */       localObject = toLocal(localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root());
/*  685 */       n = ((Point)localObject).x;
/*  686 */       i1 = ((Point)localObject).y;
/*      */     }
/*      */ 
/*  689 */     if (m == 4)
/*      */     {
/*  691 */       this.mouseButtonClickAllowed |= XConstants.buttonsMask[k];
/*  692 */       localObject = lastWindowRef != null ? (XWindow)lastWindowRef.get() : null;
/*      */ 
/*  696 */       if (eventLog.isLoggable(300)) eventLog.finest("lastWindow = " + localObject + ", lastButton " + lastButton + ", lastTime " + lastTime + ", multiClickTime " + XToolkit.getMultiClickTime());
/*      */ 
/*  699 */       if ((localObject == this) && (lastButton == k) && (l1 - lastTime < XToolkit.getMultiClickTime())) {
/*  700 */         clickCount += 1;
/*      */       } else {
/*  702 */         clickCount = 1;
/*  703 */         lastWindowRef = new WeakReference(this);
/*  704 */         lastButton = k;
/*  705 */         lastX = n;
/*  706 */         lastY = i1;
/*      */       }
/*  708 */       lastTime = l1;
/*      */ 
/*  714 */       if ((k == getRightButtonNumber()) || (k > 2))
/*  715 */         bool1 = true;
/*      */       else {
/*  717 */         bool1 = false;
/*      */       }
/*      */     }
/*      */ 
/*  721 */     j = XConstants.buttons[(k - 1)];
/*      */ 
/*  723 */     if ((k == XConstants.buttons[3]) || (k == XConstants.buttons[4]))
/*      */     {
/*  725 */       bool2 = true;
/*      */     }
/*      */ 
/*  729 */     if ((j > XConstants.buttons[4]) && (!Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled())) {
/*  730 */       return;
/*      */     }
/*      */ 
/*  733 */     if (j > XConstants.buttons[4]) {
/*  734 */       j -= 2;
/*      */     }
/*  736 */     int i = getModifiers(localXButtonEvent.get_state(), j, 0, m, bool2);
/*      */ 
/*  738 */     if (!bool2) {
/*  739 */       localObject = new MouseEvent(getEventSource(), m == 4 ? 501 : 502, l2, i, n, i1, localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root(), clickCount, bool1, j);
/*      */ 
/*  746 */       postEventToEventQueue((AWTEvent)localObject);
/*      */ 
/*  748 */       if ((m == 5) && ((this.mouseButtonClickAllowed & XConstants.buttonsMask[k]) != 0))
/*      */       {
/*  751 */         postEventToEventQueue(localObject = new MouseEvent(getEventSource(), 500, l2, i, n, i1, localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root(), clickCount, false, j));
/*      */       }
/*      */ 
/*      */     }
/*  764 */     else if (paramXEvent.get_type() == 4) {
/*  765 */       localObject = new MouseWheelEvent(getEventSource(), 507, l2, i, n, i1, localXButtonEvent.get_x_root(), localXButtonEvent.get_y_root(), 1, false, 0, 3, j == 4 ? -1 : 1);
/*      */ 
/*  772 */       postEventToEventQueue((AWTEvent)localObject);
/*      */     }
/*      */ 
/*  777 */     if (m == 5)
/*      */     {
/*  779 */       this.mouseButtonClickAllowed &= (XConstants.buttonsMask[k] ^ 0xFFFFFFFF);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleMotionNotify(XEvent paramXEvent) {
/*  784 */     super.handleMotionNotify(paramXEvent);
/*  785 */     XMotionEvent localXMotionEvent = paramXEvent.get_xmotion();
/*  786 */     if (isEventDisabled(paramXEvent)) {
/*  787 */       return;
/*      */     }
/*      */ 
/*  790 */     int i = 0;
/*      */ 
/*  795 */     int j = ((SunToolkit)Toolkit.getDefaultToolkit()).getNumberOfButtons();
/*      */ 
/*  797 */     for (int k = 0; k < j; k++)
/*      */     {
/*  799 */       if ((k != 4) && (k != 5)) {
/*  800 */         i |= localXMotionEvent.get_state() & XConstants.buttonsMask[k];
/*      */       }
/*      */     }
/*      */ 
/*  804 */     k = i != 0 ? 1 : 0;
/*  805 */     int m = 0;
/*      */ 
/*  807 */     if (k != 0)
/*  808 */       m = 506;
/*      */     else {
/*  810 */       m = 503;
/*      */     }
/*      */ 
/*  816 */     int n = localXMotionEvent.get_x();
/*  817 */     int i1 = localXMotionEvent.get_y();
/*  818 */     Object localObject1 = lastWindowRef != null ? (XWindow)lastWindowRef.get() : null;
/*      */ 
/*  820 */     if ((localObject1 != this) || (localXMotionEvent.get_time() - lastTime >= XToolkit.getMultiClickTime()) || (Math.abs(lastX - n) >= 4) || (Math.abs(lastY - i1) >= 4))
/*      */     {
/*  824 */       clickCount = 0;
/*  825 */       lastWindowRef = null;
/*  826 */       this.mouseButtonClickAllowed = 0;
/*  827 */       lastTime = 0L;
/*  828 */       lastX = 0;
/*  829 */       lastY = 0;
/*      */     }
/*      */ 
/*  832 */     long l = XToolkit.nowMillisUTC_offset(localXMotionEvent.get_time());
/*  833 */     int i2 = getModifiers(localXMotionEvent.get_state(), 0, 0);
/*  834 */     boolean bool = false;
/*      */ 
/*  836 */     Component localComponent = getEventSource();
/*      */     Object localObject2;
/*  838 */     if (localXMotionEvent.get_window() != this.window) {
/*  839 */       localObject2 = toLocal(localXMotionEvent.get_x_root(), localXMotionEvent.get_y_root());
/*  840 */       n = ((Point)localObject2).x;
/*  841 */       i1 = ((Point)localObject2).y;
/*      */     }
/*      */ 
/*  846 */     if (((k != 0) && (clickCount == 0)) || (k == 0)) {
/*  847 */       localObject2 = new MouseEvent(localComponent, m, l, i2, n, i1, localXMotionEvent.get_x_root(), localXMotionEvent.get_y_root(), clickCount, bool, 0);
/*      */ 
/*  850 */       postEventToEventQueue((AWTEvent)localObject2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public native boolean x11inputMethodLookupString(long paramLong, long[] paramArrayOfLong);
/*      */ 
/*      */   native boolean haveCurrentX11InputMethodInstance();
/*      */ 
/*      */   public boolean isMouseAbove()
/*      */   {
/*  862 */     synchronized (getStateLock()) {
/*  863 */       return this.mouseAboveMe;
/*      */     }
/*      */   }
/*      */ 
/*  867 */   protected void setMouseAbove(boolean paramBoolean) { synchronized (getStateLock()) {
/*  868 */       this.mouseAboveMe = paramBoolean;
/*      */     } }
/*      */ 
/*      */   protected void enterNotify(long paramLong)
/*      */   {
/*  873 */     if (paramLong == getWindow())
/*  874 */       setMouseAbove(true);
/*      */   }
/*      */ 
/*      */   protected void leaveNotify(long paramLong) {
/*  878 */     if (paramLong == getWindow())
/*  879 */       setMouseAbove(false);
/*      */   }
/*      */ 
/*      */   public void handleXCrossingEvent(XEvent paramXEvent)
/*      */   {
/*  884 */     super.handleXCrossingEvent(paramXEvent);
/*  885 */     XCrossingEvent localXCrossingEvent = paramXEvent.get_xcrossing();
/*      */ 
/*  887 */     if (eventLog.isLoggable(300)) eventLog.finest(localXCrossingEvent.toString());
/*      */ 
/*  889 */     if (localXCrossingEvent.get_type() == 7)
/*  890 */       enterNotify(localXCrossingEvent.get_window());
/*      */     else {
/*  892 */       leaveNotify(localXCrossingEvent.get_window());
/*      */     }
/*      */ 
/*  901 */     XWindowPeer localXWindowPeer = getToplevelXWindow();
/*  902 */     if ((localXWindowPeer != null) && (!localXWindowPeer.isModalBlocked()) && 
/*  903 */       (localXCrossingEvent.get_mode() != 0))
/*      */     {
/*  906 */       if (localXCrossingEvent.get_type() == 7) {
/*  907 */         XAwtState.setComponentMouseEntered(getEventSource());
/*  908 */         XGlobalCursorManager.nativeUpdateCursor(getEventSource());
/*      */       } else {
/*  910 */         XAwtState.setComponentMouseEntered(null);
/*      */       }
/*  912 */       return;
/*      */     }
/*      */ 
/*  919 */     long l1 = localXCrossingEvent.get_subwindow();
/*  920 */     if (l1 != 0L) {
/*  921 */       localObject1 = XToolkit.windowToXWindow(l1);
/*  922 */       if ((localObject1 != null) && ((localObject1 instanceof XWindow)) && (!((XBaseWindow)localObject1).isEventDisabled(paramXEvent)))
/*      */       {
/*  925 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  930 */     Object localObject1 = XAwtState.getComponentMouseEntered();
/*  931 */     if (localXWindowPeer != null) {
/*  932 */       if (!localXWindowPeer.isModalBlocked()) {
/*  933 */         if (localXCrossingEvent.get_type() == 7)
/*      */         {
/*  937 */           XAwtState.setComponentMouseEntered(getEventSource());
/*  938 */           XGlobalCursorManager.nativeUpdateCursor(getEventSource());
/*      */         } else {
/*  940 */           XAwtState.setComponentMouseEntered(null);
/*      */         }
/*      */       }
/*  943 */       else ((XComponentPeer)AWTAccessor.getComponentAccessor().getPeer(this.target)).pSetCursor(Cursor.getPredefinedCursor(0));
/*      */ 
/*      */     }
/*      */ 
/*  948 */     if (isEventDisabled(paramXEvent)) {
/*  949 */       return;
/*      */     }
/*      */ 
/*  952 */     long l2 = XToolkit.nowMillisUTC_offset(localXCrossingEvent.get_time());
/*  953 */     int i = getModifiers(localXCrossingEvent.get_state(), 0, 0);
/*  954 */     int j = 0;
/*  955 */     boolean bool = false;
/*  956 */     int k = localXCrossingEvent.get_x();
/*  957 */     int m = localXCrossingEvent.get_y();
/*      */     Object localObject2;
/*  958 */     if (localXCrossingEvent.get_window() != this.window) {
/*  959 */       localObject2 = toLocal(localXCrossingEvent.get_x_root(), localXCrossingEvent.get_y_root());
/*  960 */       k = ((Point)localObject2).x;
/*  961 */       m = ((Point)localObject2).y;
/*      */     }
/*      */ 
/*  966 */     if (localObject1 != null) {
/*  967 */       localObject2 = new MouseEvent((Component)localObject1, 505, l2, i, localXCrossingEvent.get_x(), localXCrossingEvent.get_y(), localXCrossingEvent.get_x_root(), localXCrossingEvent.get_y_root(), j, bool, 0);
/*      */ 
/*  971 */       postEventToEventQueue((AWTEvent)localObject2);
/*  972 */       eventLog.finest("Clearing last window ref");
/*  973 */       lastWindowRef = null;
/*      */     }
/*  975 */     if (localXCrossingEvent.get_type() == 7) {
/*  976 */       localObject2 = new MouseEvent(getEventSource(), 504, l2, i, localXCrossingEvent.get_x(), localXCrossingEvent.get_y(), localXCrossingEvent.get_x_root(), localXCrossingEvent.get_y_root(), j, bool, 0);
/*      */ 
/*  979 */       postEventToEventQueue((AWTEvent)localObject2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void doLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */   }
/*      */ 
/*  986 */   public void handleConfigureNotifyEvent(XEvent paramXEvent) { Rectangle localRectangle1 = getBounds();
/*      */ 
/*  988 */     super.handleConfigureNotifyEvent(paramXEvent);
/*  989 */     insLog.finer("Configure, {0}, event disabled: {1}", new Object[] { paramXEvent.get_xconfigure(), Boolean.valueOf(isEventDisabled(paramXEvent)) });
/*      */ 
/*  991 */     if (isEventDisabled(paramXEvent)) {
/*  992 */       return;
/*      */     }
/*      */ 
/*  997 */     Rectangle localRectangle2 = getBounds();
/*  998 */     if (!localRectangle2.getSize().equals(localRectangle1.getSize())) {
/*  999 */       postEventToEventQueue(new ComponentEvent(getEventSource(), 101));
/*      */     }
/* 1001 */     if (!localRectangle2.getLocation().equals(localRectangle1.getLocation()))
/* 1002 */       postEventToEventQueue(new ComponentEvent(getEventSource(), 100));
/*      */   }
/*      */ 
/*      */   public void handleMapNotifyEvent(XEvent paramXEvent)
/*      */   {
/* 1008 */     super.handleMapNotifyEvent(paramXEvent);
/* 1009 */     log.fine("Mapped {0}", new Object[] { this });
/* 1010 */     if (isEventDisabled(paramXEvent)) {
/* 1011 */       return;
/*      */     }
/*      */ 
/* 1015 */     ComponentEvent localComponentEvent = new ComponentEvent(getEventSource(), 102);
/* 1016 */     postEventToEventQueue(localComponentEvent);
/*      */   }
/*      */ 
/*      */   public void handleUnmapNotifyEvent(XEvent paramXEvent) {
/* 1020 */     super.handleUnmapNotifyEvent(paramXEvent);
/* 1021 */     if (isEventDisabled(paramXEvent)) {
/* 1022 */       return;
/*      */     }
/*      */ 
/* 1026 */     ComponentEvent localComponentEvent = new ComponentEvent(this.target, 103);
/* 1027 */     postEventToEventQueue(localComponentEvent);
/*      */   }
/*      */ 
/*      */   private void dumpKeysymArray(XKeyEvent paramXKeyEvent) {
/* 1031 */     keyEventLog.fine("  " + Long.toHexString(XlibWrapper.XKeycodeToKeysym(XToolkit.getDisplay(), paramXKeyEvent.get_keycode(), 0)) + "\n        " + Long.toHexString(XlibWrapper.XKeycodeToKeysym(XToolkit.getDisplay(), paramXKeyEvent.get_keycode(), 1)) + "\n        " + Long.toHexString(XlibWrapper.XKeycodeToKeysym(XToolkit.getDisplay(), paramXKeyEvent.get_keycode(), 2)) + "\n        " + Long.toHexString(XlibWrapper.XKeycodeToKeysym(XToolkit.getDisplay(), paramXKeyEvent.get_keycode(), 3)));
/*      */   }
/*      */ 
/*      */   int keysymToUnicode(long paramLong, int paramInt)
/*      */   {
/* 1042 */     return XKeysym.convertKeysym(paramLong, paramInt);
/*      */   }
/*      */   int keyEventType2Id(int paramInt) {
/* 1045 */     return paramInt == 3 ? 402 : paramInt == 2 ? 401 : 0;
/*      */   }
/*      */ 
/*      */   private static long xkeycodeToKeysym(XKeyEvent paramXKeyEvent) {
/* 1049 */     return XKeysym.getKeysym(paramXKeyEvent);
/*      */   }
/*      */   private long xkeycodeToPrimaryKeysym(XKeyEvent paramXKeyEvent) {
/* 1052 */     return XKeysym.xkeycode2primary_keysym(paramXKeyEvent);
/*      */   }
/*      */   private static int primaryUnicode2JavaKeycode(int paramInt) {
/* 1055 */     return paramInt > 0 ? ExtendedKeyCodes.getExtendedKeyCodeForChar(paramInt) : 0;
/*      */   }
/*      */ 
/*      */   void logIncomingKeyEvent(XKeyEvent paramXKeyEvent) {
/* 1059 */     keyEventLog.fine("--XWindow.java:handleKeyEvent:" + paramXKeyEvent);
/* 1060 */     dumpKeysymArray(paramXKeyEvent);
/* 1061 */     keyEventLog.fine("XXXXXXXXXXXXXX javakeycode will be most probably:0x" + Integer.toHexString(XKeysym.getJavaKeycodeOnly(paramXKeyEvent)));
/*      */   }
/*      */   public void handleKeyPress(XEvent paramXEvent) {
/* 1064 */     super.handleKeyPress(paramXEvent);
/* 1065 */     XKeyEvent localXKeyEvent = paramXEvent.get_xkey();
/* 1066 */     if (eventLog.isLoggable(500)) eventLog.fine(localXKeyEvent.toString());
/* 1067 */     if (isEventDisabled(paramXEvent)) {
/* 1068 */       return;
/*      */     }
/* 1070 */     handleKeyPress(localXKeyEvent);
/*      */   }
/*      */ 
/*      */   final void handleKeyPress(XKeyEvent paramXKeyEvent)
/*      */   {
/* 1075 */     long[] arrayOfLong = new long[2];
/* 1076 */     int i = 0;
/* 1077 */     arrayOfLong[0] = 0L;
/*      */ 
/* 1079 */     if (keyEventLog.isLoggable(500)) {
/* 1080 */       logIncomingKeyEvent(paramXKeyEvent);
/*      */     }
/* 1082 */     if (haveCurrentX11InputMethodInstance())
/*      */     {
/* 1085 */       if (x11inputMethodLookupString(paramXKeyEvent.pData, arrayOfLong)) {
/* 1086 */         if (keyEventLog.isLoggable(500)) {
/* 1087 */           keyEventLog.fine("--XWindow.java XIM did process event; return; dec keysym processed:" + arrayOfLong[0] + "; hex keysym processed:" + Long.toHexString(arrayOfLong[0]));
/*      */         }
/*      */ 
/* 1091 */         return;
/*      */       }
/* 1093 */       i = keysymToUnicode(arrayOfLong[0], paramXKeyEvent.get_state());
/* 1094 */       if (keyEventLog.isLoggable(500)) {
/* 1095 */         keyEventLog.fine("--XWindow.java XIM did NOT process event, hex keysym:" + Long.toHexString(arrayOfLong[0]) + "\n" + "                                         unicode key:" + Integer.toHexString(i));
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1102 */       arrayOfLong[0] = xkeycodeToKeysym(paramXKeyEvent);
/* 1103 */       i = keysymToUnicode(arrayOfLong[0], paramXKeyEvent.get_state());
/* 1104 */       if (keyEventLog.isLoggable(500)) {
/* 1105 */         keyEventLog.fine("--XWindow.java XIM is absent;             hex keysym:" + Long.toHexString(arrayOfLong[0]) + "\n" + "                                         unicode key:" + Integer.toHexString(i));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1117 */     XKeysym.Keysym2JavaKeycode localKeysym2JavaKeycode = XKeysym.getJavaKeycode(paramXKeyEvent);
/* 1118 */     if (localKeysym2JavaKeycode == null) {
/* 1119 */       localKeysym2JavaKeycode = new XKeysym.Keysym2JavaKeycode(0, 0);
/*      */     }
/*      */ 
/* 1125 */     int j = keysymToUnicode(xkeycodeToPrimaryKeysym(paramXKeyEvent), 0);
/*      */ 
/* 1127 */     if (keyEventLog.isLoggable(500)) {
/* 1128 */       keyEventLog.fine(">>>Fire Event:" + (paramXKeyEvent.get_type() == 2 ? "KEY_PRESSED; " : "KEY_RELEASED; ") + "jkeycode:decimal=" + localKeysym2JavaKeycode.getJavaKeycode() + ", hex=0x" + Integer.toHexString(localKeysym2JavaKeycode.getJavaKeycode()) + "; " + " legacy jkeycode: decimal=" + XKeysym.getLegacyJavaKeycodeOnly(paramXKeyEvent) + ", hex=0x" + Integer.toHexString(XKeysym.getLegacyJavaKeycodeOnly(paramXKeyEvent)) + "; ");
/*      */     }
/*      */ 
/* 1137 */     int k = XKeysym.getLegacyJavaKeycodeOnly(paramXKeyEvent);
/* 1138 */     int m = localKeysym2JavaKeycode.getJavaKeycode() == 0 ? primaryUnicode2JavaKeycode(j) : localKeysym2JavaKeycode.getJavaKeycode();
/*      */ 
/* 1141 */     postKeyEvent(401, paramXKeyEvent.get_time(), k, i == 0 ? 65535 : i, localKeysym2JavaKeycode.getKeyLocation(), paramXKeyEvent.get_state(), paramXKeyEvent.getPData(), XKeyEvent.getSize(), paramXKeyEvent.get_keycode(), j, m);
/*      */ 
/* 1151 */     if (i > 0) {
/* 1152 */       keyEventLog.fine("fire _TYPED on " + i);
/* 1153 */       postKeyEvent(400, paramXKeyEvent.get_time(), 0, i, 0, paramXKeyEvent.get_state(), paramXKeyEvent.getPData(), XKeyEvent.getSize(), 0L, j, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleKeyRelease(XEvent paramXEvent)
/*      */   {
/* 1168 */     super.handleKeyRelease(paramXEvent);
/* 1169 */     XKeyEvent localXKeyEvent = paramXEvent.get_xkey();
/* 1170 */     if (eventLog.isLoggable(500)) eventLog.fine(localXKeyEvent.toString());
/* 1171 */     if (isEventDisabled(paramXEvent)) {
/* 1172 */       return;
/*      */     }
/* 1174 */     handleKeyRelease(localXKeyEvent);
/*      */   }
/*      */ 
/*      */   private void handleKeyRelease(XKeyEvent paramXKeyEvent) {
/* 1178 */     long[] arrayOfLong = new long[2];
/* 1179 */     int i = 0;
/* 1180 */     arrayOfLong[0] = 0L;
/*      */ 
/* 1182 */     if (keyEventLog.isLoggable(500)) {
/* 1183 */       logIncomingKeyEvent(paramXKeyEvent);
/*      */     }
/*      */ 
/* 1189 */     XKeysym.Keysym2JavaKeycode localKeysym2JavaKeycode = XKeysym.getJavaKeycode(paramXKeyEvent);
/* 1190 */     if (localKeysym2JavaKeycode == null) {
/* 1191 */       localKeysym2JavaKeycode = new XKeysym.Keysym2JavaKeycode(0, 0);
/*      */     }
/* 1193 */     if (keyEventLog.isLoggable(500)) {
/* 1194 */       keyEventLog.fine(">>>Fire Event:" + (paramXKeyEvent.get_type() == 2 ? "KEY_PRESSED; " : "KEY_RELEASED; ") + "jkeycode:decimal=" + localKeysym2JavaKeycode.getJavaKeycode() + ", hex=0x" + Integer.toHexString(localKeysym2JavaKeycode.getJavaKeycode()) + "; " + " legacy jkeycode: decimal=" + XKeysym.getLegacyJavaKeycodeOnly(paramXKeyEvent) + ", hex=0x" + Integer.toHexString(XKeysym.getLegacyJavaKeycodeOnly(paramXKeyEvent)) + "; ");
/*      */     }
/*      */ 
/* 1208 */     i = keysymToUnicode(xkeycodeToKeysym(paramXKeyEvent), paramXKeyEvent.get_state());
/*      */ 
/* 1213 */     int j = keysymToUnicode(xkeycodeToPrimaryKeysym(paramXKeyEvent), 0);
/*      */ 
/* 1215 */     int k = XKeysym.getLegacyJavaKeycodeOnly(paramXKeyEvent);
/* 1216 */     int m = localKeysym2JavaKeycode.getJavaKeycode() == 0 ? primaryUnicode2JavaKeycode(j) : localKeysym2JavaKeycode.getJavaKeycode();
/*      */ 
/* 1219 */     postKeyEvent(402, paramXKeyEvent.get_time(), k, i == 0 ? 65535 : i, localKeysym2JavaKeycode.getKeyLocation(), paramXKeyEvent.get_state(), paramXKeyEvent.getPData(), XKeyEvent.getSize(), paramXKeyEvent.get_keycode(), j, m);
/*      */   }
/*      */ 
/*      */   int getWMState()
/*      */   {
/* 1240 */     if (this.stateChanged) {
/* 1241 */       this.stateChanged = false;
/* 1242 */       WindowPropertyGetter localWindowPropertyGetter = new WindowPropertyGetter(this.window, XWM.XA_WM_STATE, 0L, 1L, false, XWM.XA_WM_STATE);
/*      */       try
/*      */       {
/* 1246 */         int i = localWindowPropertyGetter.execute();
/*      */         int j;
/* 1247 */         if ((i != 0) || (localWindowPropertyGetter.getData() == 0L)) {
/* 1248 */           return this.savedState = 0;
/*      */         }
/*      */ 
/* 1251 */         if ((localWindowPropertyGetter.getActualType() != XWM.XA_WM_STATE.getAtom()) && (localWindowPropertyGetter.getActualFormat() != 32)) {
/* 1252 */           return this.savedState = 0;
/*      */         }
/* 1254 */         this.savedState = ((int)Native.getCard32(localWindowPropertyGetter.getData()));
/*      */       } finally {
/* 1256 */         localWindowPropertyGetter.dispose();
/*      */       }
/*      */     }
/* 1259 */     return this.savedState;
/*      */   }
/*      */ 
/*      */   protected void stateChanged(long paramLong, int paramInt1, int paramInt2)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void handlePropertyNotify(XEvent paramXEvent)
/*      */   {
/* 1271 */     super.handlePropertyNotify(paramXEvent);
/* 1272 */     XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/* 1273 */     if (localXPropertyEvent.get_atom() == XWM.XA_WM_STATE.getAtom())
/*      */     {
/* 1275 */       this.stateChanged = true;
/* 1276 */       stateChanged(localXPropertyEvent.get_time(), this.savedState, getWMState());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reshape(Rectangle paramRectangle) {
/* 1281 */     reshape(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 1285 */     if (paramInt3 <= 0) {
/* 1286 */       paramInt3 = 1;
/*      */     }
/* 1288 */     if (paramInt4 <= 0) {
/* 1289 */       paramInt4 = 1;
/*      */     }
/* 1291 */     this.x = paramInt1;
/* 1292 */     this.y = paramInt2;
/* 1293 */     this.width = paramInt3;
/* 1294 */     this.height = paramInt4;
/* 1295 */     xSetBounds(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/* 1300 */     validateSurface();
/* 1301 */     layout();
/*      */   }
/*      */   public void layout() {
/*      */   }
/*      */ 
/*      */   boolean isShowing() {
/* 1307 */     return this.visible;
/*      */   }
/*      */ 
/*      */   boolean isResizable() {
/* 1311 */     return true;
/*      */   }
/*      */ 
/*      */   boolean isLocationByPlatform() {
/* 1315 */     return false;
/*      */   }
/*      */ 
/*      */   void updateSizeHints() {
/* 1319 */     updateSizeHints(this.x, this.y, this.width, this.height);
/*      */   }
/*      */ 
/*      */   void updateSizeHints(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 1323 */     long l = 0x8 | (isLocationByPlatform() ? 0L : 5L);
/* 1324 */     if (!isResizable()) {
/* 1325 */       log.finer("Window {0} is not resizable", new Object[] { this });
/* 1326 */       l |= 48L;
/*      */     } else {
/* 1328 */       log.finer("Window {0} is resizable", new Object[] { this });
/*      */     }
/* 1330 */     setSizeHints(l, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   void updateSizeHints(int paramInt1, int paramInt2) {
/* 1334 */     long l = isLocationByPlatform() ? 0L : 5L;
/* 1335 */     if (!isResizable()) {
/* 1336 */       log.finer("Window {0} is not resizable", new Object[] { this });
/* 1337 */       l |= 56L;
/*      */     } else {
/* 1339 */       log.finer("Window {0} is resizable", new Object[] { this });
/*      */     }
/* 1341 */     setSizeHints(l, paramInt1, paramInt2, this.width, this.height);
/*      */   }
/*      */ 
/*      */   void validateSurface() {
/* 1345 */     if ((this.width != this.oldWidth) || (this.height != this.oldHeight)) {
/* 1346 */       doValidateSurface();
/*      */ 
/* 1348 */       this.oldWidth = this.width;
/* 1349 */       this.oldHeight = this.height;
/*      */     }
/*      */   }
/*      */ 
/*      */   final void doValidateSurface() {
/* 1354 */     SurfaceData localSurfaceData = this.surfaceData;
/* 1355 */     if (localSurfaceData != null) {
/* 1356 */       this.surfaceData = this.graphicsConfig.createSurfaceData(this);
/* 1357 */       localSurfaceData.invalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   public SurfaceData getSurfaceData() {
/* 1362 */     return this.surfaceData;
/*      */   }
/*      */ 
/*      */   public void dispose() {
/* 1366 */     SurfaceData localSurfaceData = this.surfaceData;
/* 1367 */     this.surfaceData = null;
/* 1368 */     if (localSurfaceData != null) {
/* 1369 */       localSurfaceData.invalidate();
/*      */     }
/* 1371 */     XToolkit.targetDisposedPeer(this.target, this);
/* 1372 */     destroy();
/*      */   }
/*      */ 
/*      */   public Point getLocationOnScreen() {
/* 1376 */     synchronized (this.target.getTreeLock()) {
/* 1377 */       Object localObject1 = this.target;
/*      */ 
/* 1379 */       while ((localObject1 != null) && (!(localObject1 instanceof Window))) {
/* 1380 */         localObject1 = AWTAccessor.getComponentAccessor().getParent((Component)localObject1);
/*      */       }
/*      */ 
/* 1385 */       if ((localObject1 == null) || ((localObject1 instanceof EmbeddedFrame))) {
/* 1386 */         return toGlobal(0, 0);
/*      */       }
/*      */ 
/* 1389 */       XToolkit.awtLock();
/*      */       try {
/* 1391 */         Object localObject2 = XToolkit.targetToPeer(localObject1);
/* 1392 */         if ((localObject2 == null) || (!(localObject2 instanceof XDecoratedPeer)) || (((XDecoratedPeer)localObject2).configure_seen))
/*      */         {
/* 1396 */           localPoint1 = toGlobal(0, 0);
/*      */ 
/* 1411 */           XToolkit.awtUnlock(); return localPoint1;
/*      */         }
/* 1400 */         Point localPoint1 = toOtherWindow(getContentWindow(), ((XDecoratedPeer)localObject2).getContentWindow(), 0, 0);
/*      */ 
/* 1404 */         if (localPoint1 == null) {
/* 1405 */           localPoint1 = new Point(((XBaseWindow)localObject2).getAbsoluteX(), ((XBaseWindow)localObject2).getAbsoluteY());
/*      */         }
/* 1407 */         localPoint1.x += ((Component)localObject1).getX();
/* 1408 */         localPoint1.y += ((Component)localObject1).getY();
/* 1409 */         Point localPoint2 = localPoint1;
/*      */ 
/* 1411 */         XToolkit.awtUnlock(); return localPoint2; } finally { XToolkit.awtUnlock(); }
/*      */ 
/*      */     }
/*      */   }
/*      */ 
/*      */   static void setBData(KeyEvent paramKeyEvent, byte[] paramArrayOfByte)
/*      */   {
/* 1418 */     AWTAccessor.getAWTEventAccessor().setBData(paramKeyEvent, paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   public void postKeyEvent(int paramInt1, long paramLong1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong2, int paramInt6, long paramLong3, int paramInt7, int paramInt8)
/*      */   {
/* 1426 */     long l = XToolkit.nowMillisUTC_offset(paramLong1);
/* 1427 */     int i = getModifiers(paramInt5, 0, paramInt2);
/*      */ 
/* 1429 */     KeyEvent localKeyEvent = new KeyEvent(getEventSource(), paramInt1, l, i, paramInt2, (char)paramInt3, paramInt4);
/*      */ 
/* 1431 */     if (paramLong2 != 0L) {
/* 1432 */       localObject = Native.toBytes(paramLong2, paramInt6);
/* 1433 */       setBData(localKeyEvent, (byte[])localObject);
/*      */     }
/*      */ 
/* 1436 */     Object localObject = AWTAccessor.getKeyEventAccessor();
/* 1437 */     ((AWTAccessor.KeyEventAccessor)localObject).setRawCode(localKeyEvent, paramLong3);
/* 1438 */     ((AWTAccessor.KeyEventAccessor)localObject).setPrimaryLevelUnicode(localKeyEvent, paramInt7);
/* 1439 */     ((AWTAccessor.KeyEventAccessor)localObject).setExtendedKeyCode(localKeyEvent, paramInt8);
/* 1440 */     postEventToEventQueue(localKeyEvent);
/*      */   }
/*      */ 
/*      */   static native int getAWTKeyCodeForKeySym(int paramInt);
/*      */ 
/*      */   static native int getKeySymForAWTKeyCode(int paramInt);
/*      */ 
/*      */   public PropMwmHints getMWMHints()
/*      */   {
/* 1452 */     if (this.mwm_hints == null) {
/* 1453 */       this.mwm_hints = new PropMwmHints();
/* 1454 */       if (!XWM.XA_MWM_HINTS.getAtomData(getWindow(), this.mwm_hints.pData, 5)) {
/* 1455 */         this.mwm_hints.zero();
/*      */       }
/*      */     }
/* 1458 */     return this.mwm_hints;
/*      */   }
/*      */ 
/*      */   public void setMWMHints(PropMwmHints paramPropMwmHints) {
/* 1462 */     this.mwm_hints = paramPropMwmHints;
/* 1463 */     if (paramPropMwmHints != null)
/* 1464 */       XWM.XA_MWM_HINTS.setAtomData(getWindow(), this.mwm_hints.pData, 5);
/*      */   }
/*      */ 
/*      */   protected final void initWMProtocols()
/*      */   {
/* 1469 */     wm_protocols.setAtomListProperty(this, getWMProtocols());
/*      */   }
/*      */ 
/*      */   protected XAtomList getWMProtocols()
/*      */   {
/* 1478 */     return new XAtomList();
/*      */   }
/*      */ 
/*      */   public void setFullScreenExclusiveModeState(boolean paramBoolean)
/*      */   {
/* 1490 */     synchronized (getStateLock()) {
/* 1491 */       this.fullScreenExclusiveModeState = paramBoolean;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final boolean isFullScreenExclusiveMode() {
/* 1496 */     synchronized (getStateLock()) {
/* 1497 */       return this.fullScreenExclusiveModeState;
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  130 */     initIDs();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XWindow
 * JD-Core Version:    0.6.2
 */