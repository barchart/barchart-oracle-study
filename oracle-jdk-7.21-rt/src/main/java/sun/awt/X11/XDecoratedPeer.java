/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Window;
/*      */ import java.awt.Window.Type;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.InvocationEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.PrintStream;
/*      */ import java.util.List;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ abstract class XDecoratedPeer extends XWindowPeer
/*      */ {
/*   39 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XDecoratedPeer");
/*   40 */   private static final PlatformLogger insLog = PlatformLogger.getLogger("sun.awt.X11.insets.XDecoratedPeer");
/*   41 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.X11.focus.XDecoratedPeer");
/*   42 */   private static final PlatformLogger iconLog = PlatformLogger.getLogger("sun.awt.X11.icon.XDecoratedPeer");
/*      */   boolean configure_seen;
/*      */   boolean insets_corrected;
/*      */   XIconWindow iconWindow;
/*      */   WindowDimensions dimensions;
/*      */   XContentWindow content;
/*      */   Insets currentInsets;
/*      */   XFocusProxyWindow focusProxy;
/*      */   private Insets wm_set_insets;
/*  295 */   long reparent_serial = 0L;
/*      */ 
/*  643 */   boolean no_reparent_artifacts = false;
/*      */ 
/* 1158 */   XWindowPeer actualFocusedWindow = null;
/*      */ 
/*      */   XDecoratedPeer(Window paramWindow)
/*      */   {
/*   56 */     super(paramWindow);
/*      */   }
/*      */ 
/*      */   XDecoratedPeer(XCreateWindowParams paramXCreateWindowParams) {
/*   60 */     super(paramXCreateWindowParams);
/*      */   }
/*      */ 
/*      */   public long getShell() {
/*   64 */     return this.window;
/*      */   }
/*      */ 
/*      */   public long getContentWindow() {
/*   68 */     return this.content == null ? this.window : this.content.getWindow();
/*      */   }
/*      */ 
/*      */   void preInit(XCreateWindowParams paramXCreateWindowParams) {
/*   72 */     super.preInit(paramXCreateWindowParams);
/*   73 */     this.winAttr.initialFocus = true;
/*      */ 
/*   75 */     this.currentInsets = new Insets(0, 0, 0, 0);
/*   76 */     applyGuessedInsets();
/*      */ 
/*   78 */     Rectangle localRectangle = (Rectangle)paramXCreateWindowParams.get("bounds");
/*   79 */     this.dimensions = new WindowDimensions(localRectangle, getRealInsets(), false);
/*   80 */     paramXCreateWindowParams.put("bounds", this.dimensions.getClientRect());
/*   81 */     insLog.fine("Initial dimensions {0}", new Object[] { this.dimensions });
/*      */ 
/*   85 */     Long localLong = (Long)paramXCreateWindowParams.get("event mask");
/*   86 */     paramXCreateWindowParams.add("event mask", Long.valueOf(localLong.longValue() & 0xFFDFFFFC));
/*      */   }
/*      */ 
/*      */   void postInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*   91 */     updateSizeHints(this.dimensions);
/*      */ 
/*   94 */     super.postInit(paramXCreateWindowParams);
/*      */ 
/*   98 */     initResizability();
/*   99 */     XWM.requestWMExtents(getWindow());
/*      */ 
/*  101 */     this.content = XContentWindow.createContent(this);
/*      */ 
/*  103 */     if (this.warningWindow != null) {
/*  104 */       this.warningWindow.toFront();
/*      */     }
/*  106 */     this.focusProxy = createFocusProxy();
/*      */   }
/*      */ 
/*      */   void setIconHints(List<XIconInfo> paramList) {
/*  110 */     if ((!XWM.getWM().setNetWMIcon(this, paramList)) && 
/*  111 */       (paramList.size() > 0)) {
/*  112 */       if (this.iconWindow == null) {
/*  113 */         this.iconWindow = new XIconWindow(this);
/*      */       }
/*  115 */       this.iconWindow.setIconImages(paramList);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateMinimumSize()
/*      */   {
/*  121 */     super.updateMinimumSize();
/*  122 */     updateMinSizeHints();
/*      */   }
/*      */ 
/*      */   private void updateMinSizeHints() {
/*  126 */     if (isResizable()) {
/*  127 */       Dimension localDimension = getTargetMinimumSize();
/*  128 */       if (localDimension != null) {
/*  129 */         Insets localInsets = getRealInsets();
/*  130 */         int i = localDimension.width - localInsets.left - localInsets.right;
/*  131 */         int j = localDimension.height - localInsets.top - localInsets.bottom;
/*  132 */         if (i < 0) i = 0;
/*  133 */         if (j < 0) j = 0;
/*  134 */         setSizeHints(0x10 | (isLocationByPlatform() ? 0L : 5L), getX(), getY(), i, j);
/*      */ 
/*  136 */         if (isVisible()) {
/*  137 */           Rectangle localRectangle = getShellBounds();
/*  138 */           int k = localRectangle.width < i ? i : localRectangle.width;
/*  139 */           int m = localRectangle.height < j ? j : localRectangle.height;
/*  140 */           if ((k != localRectangle.width) || (m != localRectangle.height))
/*  141 */             setShellSize(new Rectangle(0, 0, k, m));
/*      */         }
/*      */       }
/*      */       else {
/*  145 */         boolean bool = isMinSizeSet();
/*  146 */         XWM.removeSizeHints(this, 16L);
/*      */ 
/*  148 */         if ((bool) && (isShowing()) && (XWM.needRemap(this)))
/*      */         {
/*  154 */           xSetVisible(false);
/*  155 */           XToolkit.XSync();
/*  156 */           xSetVisible(true);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   XFocusProxyWindow createFocusProxy() {
/*  163 */     return new XFocusProxyWindow(this);
/*      */   }
/*      */ 
/*      */   protected XAtomList getWMProtocols() {
/*  167 */     XAtomList localXAtomList = super.getWMProtocols();
/*  168 */     localXAtomList.add(wm_delete_window);
/*  169 */     localXAtomList.add(wm_take_focus);
/*  170 */     return localXAtomList;
/*      */   }
/*      */ 
/*      */   public Graphics getGraphics() {
/*  174 */     AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/*  175 */     return getGraphics(this.content.surfaceData, localComponentAccessor.getForeground(this.target), localComponentAccessor.getBackground(this.target), localComponentAccessor.getFont(this.target));
/*      */   }
/*      */ 
/*      */   public void setTitle(String paramString)
/*      */   {
/*  182 */     if (log.isLoggable(500)) log.fine("Title is " + paramString);
/*  183 */     this.winAttr.title = paramString;
/*  184 */     updateWMName();
/*      */   }
/*      */ 
/*      */   protected String getWMName() {
/*  188 */     if ((this.winAttr.title == null) || (this.winAttr.title.trim().equals(""))) {
/*  189 */       return " ";
/*      */     }
/*  191 */     return this.winAttr.title;
/*      */   }
/*      */ 
/*      */   void updateWMName()
/*      */   {
/*  196 */     super.updateWMName();
/*  197 */     String str = getWMName();
/*  198 */     XToolkit.awtLock();
/*      */     try {
/*  200 */       if ((str == null) || (str.trim().equals(""))) {
/*  201 */         str = "Java";
/*      */       }
/*  203 */       XAtom localXAtom1 = XAtom.get(37L);
/*  204 */       localXAtom1.setProperty(getWindow(), str);
/*  205 */       XAtom localXAtom2 = XAtom.get("_NET_WM_ICON_NAME");
/*  206 */       localXAtom2.setPropertyUTF8(getWindow(), str);
/*      */     } finally {
/*  208 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleIconify()
/*      */   {
/*  215 */     postEvent(new WindowEvent((Window)this.target, 203));
/*      */   }
/*      */ 
/*      */   public void handleDeiconify()
/*      */   {
/*  221 */     postEvent(new WindowEvent((Window)this.target, 204));
/*      */   }
/*      */ 
/*      */   public void handleFocusEvent(XEvent paramXEvent) {
/*  225 */     super.handleFocusEvent(paramXEvent);
/*  226 */     XFocusChangeEvent localXFocusChangeEvent = paramXEvent.get_xfocus();
/*      */ 
/*  230 */     focusLog.finer("Received focus event on shell: " + localXFocusChangeEvent);
/*      */   }
/*      */ 
/*      */   protected boolean isInitialReshape()
/*      */   {
/*  239 */     return false;
/*      */   }
/*      */ 
/*      */   private static Insets difference(Insets paramInsets1, Insets paramInsets2) {
/*  243 */     return new Insets(paramInsets1.top - paramInsets2.top, paramInsets1.left - paramInsets2.left, paramInsets1.bottom - paramInsets2.bottom, paramInsets1.right - paramInsets2.right);
/*      */   }
/*      */ 
/*      */   private static boolean isNull(Insets paramInsets) {
/*  247 */     return (paramInsets == null) || ((paramInsets.left | paramInsets.top | paramInsets.right | paramInsets.bottom) == 0);
/*      */   }
/*      */ 
/*      */   private static Insets copy(Insets paramInsets) {
/*  251 */     return new Insets(paramInsets.top, paramInsets.left, paramInsets.bottom, paramInsets.right);
/*      */   }
/*      */ 
/*      */   private Insets getWMSetInsets(XAtom paramXAtom)
/*      */   {
/*  258 */     if (isEmbedded()) {
/*  259 */       return null;
/*      */     }
/*      */ 
/*  262 */     if (this.wm_set_insets != null) {
/*  263 */       return this.wm_set_insets;
/*      */     }
/*      */ 
/*  266 */     if (paramXAtom == null)
/*  267 */       this.wm_set_insets = XWM.getInsetsFromExtents(getWindow());
/*      */     else {
/*  269 */       this.wm_set_insets = XWM.getInsetsFromProp(getWindow(), paramXAtom);
/*      */     }
/*      */ 
/*  272 */     insLog.finer("FRAME_EXTENTS: {0}", new Object[] { this.wm_set_insets });
/*      */ 
/*  274 */     if (this.wm_set_insets != null) {
/*  275 */       this.wm_set_insets = copy(this.wm_set_insets);
/*      */     }
/*  277 */     return this.wm_set_insets;
/*      */   }
/*      */ 
/*      */   private void resetWMSetInsets() {
/*  281 */     this.wm_set_insets = null;
/*      */   }
/*      */ 
/*      */   public void handlePropertyNotify(XEvent paramXEvent) {
/*  285 */     super.handlePropertyNotify(paramXEvent);
/*      */ 
/*  287 */     XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/*  288 */     if ((localXPropertyEvent.get_atom() == XWM.XA_KDE_NET_WM_FRAME_STRUT.getAtom()) || (localXPropertyEvent.get_atom() == XWM.XA_NET_FRAME_EXTENTS.getAtom()))
/*      */     {
/*  291 */       getWMSetInsets(XAtom.get(localXPropertyEvent.get_atom()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleReparentNotifyEvent(XEvent paramXEvent)
/*      */   {
/*  298 */     XReparentEvent localXReparentEvent = paramXEvent.get_xreparent();
/*  299 */     if (insLog.isLoggable(500)) insLog.fine(localXReparentEvent.toString());
/*  300 */     this.reparent_serial = localXReparentEvent.get_serial();
/*  301 */     XToolkit.awtLock();
/*      */     try {
/*  303 */       long l = XlibWrapper.RootWindow(XToolkit.getDisplay(), getScreenNumber());
/*      */ 
/*  305 */       if (isEmbedded()) {
/*  306 */         setReparented(true);
/*  307 */         this.insets_corrected = true;
/*      */       }
/*      */       else {
/*  310 */         Component localComponent = this.target;
/*  311 */         if (getDecorations() == XWindowAttributesData.AWT_DECOR_NONE) {
/*  312 */           setReparented(true);
/*  313 */           this.insets_corrected = true;
/*  314 */           reshape(this.dimensions, 2, false);
/*  315 */         } else if (localXReparentEvent.get_parent() == l) {
/*  316 */           this.configure_seen = false;
/*  317 */           this.insets_corrected = false;
/*      */ 
/*  324 */           if (isVisible())
/*      */           {
/*  326 */             XWM.getWM().unshadeKludge(this);
/*  327 */             insLog.fine("- WM exited");
/*      */           } else {
/*  329 */             insLog.fine(" - reparent due to hide");
/*      */           }
/*      */         } else {
/*  332 */           setReparented(true);
/*  333 */           this.insets_corrected = false;
/*      */ 
/*  336 */           Insets localInsets1 = getWMSetInsets(null);
/*  337 */           if (localInsets1 != null) {
/*  338 */             insLog.finer("wm-provided insets {0}", new Object[] { localInsets1 });
/*      */ 
/*  340 */             Insets localInsets2 = this.dimensions.getInsets();
/*  341 */             if (localInsets1.equals(localInsets2)) { insLog.finer("Insets are the same as estimated - no additional reshapes necessary");
/*  343 */               this.no_reparent_artifacts = true;
/*  344 */               this.insets_corrected = true;
/*  345 */               applyGuessedInsets();
/*      */               return; }  } else { localInsets1 = XWM.getWM().getInsets(this, localXReparentEvent.get_window(), localXReparentEvent.get_parent());
/*      */ 
/*  351 */             if (localInsets1 != null)
/*  352 */               insLog.finer("correctWM {0}", new Object[] { localInsets1 });
/*      */             else {
/*  354 */               insLog.finer("correctWM insets are not available, waiting for configureNotify");
/*      */             }
/*      */           }
/*      */ 
/*  358 */           if (localInsets1 != null)
/*  359 */             handleCorrectInsets(localInsets1);
/*      */         }
/*      */       }
/*      */     } finally {
/*  363 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleCorrectInsets(Insets paramInsets) {
/*  368 */     XToolkit.awtLock();
/*      */     try
/*      */     {
/*  374 */       Insets localInsets = difference(paramInsets, this.currentInsets);
/*  375 */       insLog.finest("Corrention {0}", new Object[] { localInsets });
/*  376 */       if (!isNull(localInsets)) {
/*  377 */         this.currentInsets = copy(paramInsets);
/*  378 */         applyGuessedInsets();
/*      */ 
/*  383 */         updateMinSizeHints();
/*      */       }
/*  385 */       if (insLog.isLoggable(400)) insLog.finer("Dimensions before reparent: " + this.dimensions);
/*      */ 
/*  387 */       this.dimensions.setInsets(getRealInsets());
/*  388 */       this.insets_corrected = true;
/*      */ 
/*  390 */       if (isMaximized())
/*      */       {
/*      */         return;
/*      */       }
/*      */ 
/*  400 */       if ((getHints().get_flags() & 0x5) != 0L)
/*  401 */         reshape(this.dimensions, 3, false);
/*      */       else
/*  403 */         reshape(this.dimensions, 2, false);
/*      */     }
/*      */     finally {
/*  406 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleMoved(WindowDimensions paramWindowDimensions) {
/*  411 */     Point localPoint = paramWindowDimensions.getLocation();
/*  412 */     AWTAccessor.getComponentAccessor().setLocation(this.target, localPoint.x, localPoint.y);
/*  413 */     postEvent(new ComponentEvent(this.target, 100));
/*      */   }
/*      */ 
/*      */   protected Insets guessInsets()
/*      */   {
/*  418 */     if ((isEmbedded()) || (isTargetUndecorated())) {
/*  419 */       return new Insets(0, 0, 0, 0);
/*      */     }
/*  421 */     if (!isNull(this.currentInsets))
/*      */     {
/*  423 */       return copy(this.currentInsets);
/*      */     }
/*  425 */     Insets localInsets = getWMSetInsets(null);
/*  426 */     if (localInsets == null) {
/*  427 */       localInsets = XWM.getWM().guessInsets(this);
/*      */     }
/*  429 */     return localInsets;
/*      */   }
/*      */ 
/*      */   private void applyGuessedInsets()
/*      */   {
/*  435 */     Insets localInsets = guessInsets();
/*  436 */     this.currentInsets = copy(localInsets);
/*      */   }
/*      */ 
/*      */   public void revalidate() {
/*  440 */     XToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
/*      */       public void run() {
/*  442 */         XDecoratedPeer.this.target.invalidate();
/*  443 */         XDecoratedPeer.this.target.validate();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   Insets getRealInsets() {
/*  449 */     if (isNull(this.currentInsets)) {
/*  450 */       applyGuessedInsets();
/*      */     }
/*  452 */     return this.currentInsets;
/*      */   }
/*      */ 
/*      */   public Insets getInsets() {
/*  456 */     Insets localInsets = copy(getRealInsets());
/*  457 */     localInsets.top += getMenuBarHeight();
/*  458 */     if (insLog.isLoggable(300)) {
/*  459 */       insLog.finest("Get insets returns {0}", new Object[] { localInsets });
/*      */     }
/*  461 */     return localInsets;
/*      */   }
/*      */ 
/*      */   boolean gravityBug() {
/*  465 */     return XWM.configureGravityBuggy();
/*      */   }
/*      */ 
/*      */   int getInputMethodHeight()
/*      */   {
/*  470 */     return 0;
/*      */   }
/*      */ 
/*      */   void updateSizeHints(WindowDimensions paramWindowDimensions) {
/*  474 */     Rectangle localRectangle = paramWindowDimensions.getClientRect();
/*  475 */     checkShellRect(localRectangle);
/*  476 */     updateSizeHints(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */   }
/*      */ 
/*      */   void updateSizeHints() {
/*  480 */     updateSizeHints(this.dimensions);
/*      */   }
/*      */ 
/*      */   public void reshape(WindowDimensions paramWindowDimensions, int paramInt, boolean paramBoolean)
/*      */   {
/*  488 */     if (insLog.isLoggable(500))
/*  489 */       insLog.fine("Reshaping " + this + " to " + paramWindowDimensions + " op " + paramInt + " user reshape " + paramBoolean);
/*      */     Object localObject1;
/*      */     Object localObject2;
/*  491 */     if (paramBoolean)
/*      */     {
/*  496 */       localObject1 = paramWindowDimensions.getBounds();
/*  497 */       localObject2 = paramWindowDimensions.getInsets();
/*      */ 
/*  499 */       if (paramWindowDimensions.isClientSizeSet()) {
/*  500 */         localObject1 = new Rectangle(((Rectangle)localObject1).x, ((Rectangle)localObject1).y, ((Rectangle)localObject1).width - ((Insets)localObject2).left - ((Insets)localObject2).right, ((Rectangle)localObject1).height - ((Insets)localObject2).top - ((Insets)localObject2).bottom);
/*      */       }
/*      */ 
/*  504 */       paramWindowDimensions = new WindowDimensions((Rectangle)localObject1, (Insets)localObject2, paramWindowDimensions.isClientSizeSet());
/*      */     }
/*  506 */     XToolkit.awtLock();
/*      */     try
/*      */     {
/*      */       Object localObject3;
/*  508 */       if ((!isReparented()) || (!isVisible())) {
/*  509 */         insLog.fine("- not reparented({0}) or not visible({1}), default reshape", new Object[] { Boolean.valueOf(isReparented()), Boolean.valueOf(this.visible) });
/*      */ 
/*  516 */         localObject1 = getLocation();
/*      */ 
/*  518 */         localObject2 = new Point(AWTAccessor.getComponentAccessor().getX(this.target), AWTAccessor.getComponentAccessor().getY(this.target));
/*      */ 
/*  521 */         if (!((Point)localObject2).equals(localObject1)) {
/*  522 */           handleMoved(paramWindowDimensions);
/*      */         }
/*      */ 
/*  525 */         this.dimensions = new WindowDimensions(paramWindowDimensions);
/*  526 */         updateSizeHints(this.dimensions);
/*  527 */         localObject3 = this.dimensions.getClientRect();
/*  528 */         checkShellRect((Rectangle)localObject3);
/*  529 */         setShellBounds((Rectangle)localObject3);
/*  530 */         if ((this.content != null) && (!this.content.getSize().equals(paramWindowDimensions.getSize())))
/*      */         {
/*  533 */           reconfigureContentWindow(paramWindowDimensions);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  538 */         int i = XWM.getWMID();
/*  539 */         updateChildrenSizes();
/*  540 */         applyGuessedInsets();
/*      */ 
/*  542 */         localObject2 = paramWindowDimensions.getClientRect();
/*      */ 
/*  544 */         if (gravityBug()) {
/*  545 */           localObject3 = paramWindowDimensions.getInsets();
/*  546 */           ((Rectangle)localObject2).translate(((Insets)localObject3).left, ((Insets)localObject3).top);
/*      */         }
/*      */ 
/*  549 */         if (((paramInt & 0x4000) == 0) && (isEmbedded())) {
/*  550 */           ((Rectangle)localObject2).setLocation(0, 0);
/*      */         }
/*      */ 
/*  553 */         checkShellRectSize((Rectangle)localObject2);
/*  554 */         if (!isEmbedded()) {
/*  555 */           checkShellRectPos((Rectangle)localObject2);
/*      */         }
/*      */ 
/*  558 */         paramInt &= -16385;
/*      */ 
/*  560 */         if (paramInt == 1) {
/*  561 */           setShellPosition((Rectangle)localObject2);
/*  562 */         } else if (isResizable()) {
/*  563 */           if (paramInt == 3)
/*  564 */             setShellBounds((Rectangle)localObject2);
/*      */           else
/*  566 */             setShellSize((Rectangle)localObject2);
/*      */         }
/*      */         else {
/*  569 */           XWM.setShellNotResizable(this, paramWindowDimensions, (Rectangle)localObject2, true);
/*  570 */           if (paramInt == 3) {
/*  571 */             setShellPosition((Rectangle)localObject2);
/*      */           }
/*      */         }
/*      */ 
/*  575 */         reconfigureContentWindow(paramWindowDimensions);
/*      */       }
/*      */     } finally { XToolkit.awtUnlock(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
/*      */   {
/*  588 */     int i = 0;
/*  589 */     WindowDimensions localWindowDimensions = new WindowDimensions(this.dimensions);
/*  590 */     switch (paramInt5 & 0xFFFFBFFF)
/*      */     {
/*      */     case 1:
/*  594 */       localWindowDimensions.setLocation(paramInt1, paramInt2);
/*  595 */       break;
/*      */     case 2:
/*  599 */       localWindowDimensions.setSize(paramInt3, paramInt4);
/*  600 */       break;
/*      */     case 4:
/*  603 */       Insets localInsets = this.currentInsets;
/*  604 */       paramInt3 -= localInsets.left + localInsets.right;
/*  605 */       paramInt4 -= localInsets.top + localInsets.bottom;
/*  606 */       localWindowDimensions.setClientSize(paramInt3, paramInt4);
/*  607 */       break;
/*      */     case 3:
/*      */     default:
/*  611 */       localWindowDimensions.setLocation(paramInt1, paramInt2);
/*  612 */       localWindowDimensions.setSize(paramInt3, paramInt4);
/*      */     }
/*      */ 
/*  615 */     if (insLog.isLoggable(500)) {
/*  616 */       insLog.fine("For the operation {0} new dimensions are {1}", new Object[] { operationToString(paramInt5), localWindowDimensions });
/*      */     }
/*      */ 
/*  619 */     reshape(localWindowDimensions, paramInt5, paramBoolean);
/*      */   }
/*      */ 
/*      */   abstract boolean isTargetUndecorated();
/*      */ 
/*      */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  630 */     reshape(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, true);
/*  631 */     validateSurface();
/*      */   }
/*      */ 
/*      */   void reconfigureContentWindow(WindowDimensions paramWindowDimensions)
/*      */   {
/*  636 */     if (this.content == null) {
/*  637 */       insLog.fine("WARNING: Content window is null");
/*  638 */       return;
/*      */     }
/*  640 */     this.content.setContentBounds(paramWindowDimensions);
/*      */   }
/*      */ 
/*      */   public void handleConfigureNotifyEvent(XEvent paramXEvent)
/*      */   {
/*  645 */     assert (SunToolkit.isAWTLockHeldByCurrentThread());
/*  646 */     XConfigureEvent localXConfigureEvent = paramXEvent.get_xconfigure();
/*  647 */     insLog.fine("Configure notify {0}", new Object[] { localXConfigureEvent });
/*      */ 
/*  650 */     if (isReparented()) {
/*  651 */       this.configure_seen = true;
/*      */     }
/*      */ 
/*  654 */     if ((!isMaximized()) && ((localXConfigureEvent.get_serial() == this.reparent_serial) || (localXConfigureEvent.get_window() != getShell())) && (!this.no_reparent_artifacts))
/*      */     {
/*  658 */       insLog.fine("- reparent artifact, skipping");
/*  659 */       return;
/*      */     }
/*  661 */     this.no_reparent_artifacts = false;
/*      */ 
/*  671 */     if ((!isVisible()) && (XWM.getWMID() != 2)) {
/*  672 */       insLog.fine(" - not visible, skipping");
/*  673 */       return;
/*      */     }
/*      */ 
/*  683 */     int i = XWM.getWMID();
/*  684 */     if (insLog.isLoggable(500)) {
/*  685 */       insLog.fine("reparented={0}, visible={1}, WM={2}, decorations={3}", new Object[] { Boolean.valueOf(isReparented()), Boolean.valueOf(isVisible()), Integer.valueOf(i), Integer.valueOf(getDecorations()) });
/*      */     }
/*      */ 
/*  688 */     if ((!isReparented()) && (isVisible()) && (i != 2) && (!XWM.isNonReparentingWM()) && (getDecorations() != XWindowAttributesData.AWT_DECOR_NONE))
/*      */     {
/*  691 */       insLog.fine("- visible but not reparented, skipping");
/*  692 */       return;
/*      */     }
/*      */ 
/*  695 */     if ((!this.insets_corrected) && (getDecorations() != XWindowAttributesData.AWT_DECOR_NONE)) {
/*  696 */       long l = XlibUtil.getParentWindow(this.window);
/*  697 */       localObject2 = l != -1L ? XWM.getWM().getInsets(this, this.window, l) : null;
/*  698 */       if (insLog.isLoggable(400)) {
/*  699 */         if (localObject2 != null)
/*  700 */           insLog.finer("Configure notify - insets : " + localObject2);
/*      */         else {
/*  702 */           insLog.finer("Configure notify - insets are still not available");
/*      */         }
/*      */       }
/*  705 */       if (localObject2 != null) {
/*  706 */         handleCorrectInsets((Insets)localObject2);
/*      */       }
/*      */       else
/*      */       {
/*  710 */         this.insets_corrected = true;
/*      */       }
/*      */     }
/*      */ 
/*  714 */     updateChildrenSizes();
/*      */ 
/*  717 */     Rectangle localRectangle = AWTAccessor.getComponentAccessor().getBounds(this.target);
/*      */ 
/*  719 */     Object localObject1 = localRectangle.getLocation();
/*  720 */     if ((localXConfigureEvent.get_send_event()) || (i == 2) || (XWM.isNonReparentingWM()))
/*      */     {
/*  722 */       localObject1 = new Point(localXConfigureEvent.get_x() - this.currentInsets.left, localXConfigureEvent.get_y() - this.currentInsets.top);
/*      */     }
/*      */     else
/*      */     {
/*  730 */       switch (XWM.getWMID())
/*      */       {
/*      */       case 5:
/*      */       case 6:
/*      */       case 9:
/*      */       case 11:
/*      */       case 15:
/*  737 */         localObject2 = queryXLocation();
/*  738 */         if (log.isLoggable(500)) log.fine("New X location: {0}", new Object[] { localObject2 });
/*  739 */         if (localObject2 != null)
/*  740 */           localObject1 = localObject2; break;
/*      */       case 7:
/*      */       case 8:
/*      */       case 10:
/*      */       case 12:
/*      */       case 13:
/*      */       case 14:
/*      */       }
/*      */     }
/*  749 */     Object localObject2 = new WindowDimensions((Point)localObject1, new Dimension(localXConfigureEvent.get_width(), localXConfigureEvent.get_height()), copy(this.currentInsets), true);
/*      */ 
/*  755 */     insLog.finer("Insets are {0}, new dimensions {1}", new Object[] { this.currentInsets, localObject2 });
/*      */ 
/*  758 */     checkIfOnNewScreen(((WindowDimensions)localObject2).getBounds());
/*      */ 
/*  760 */     Point localPoint = getLocation();
/*  761 */     this.dimensions = ((WindowDimensions)localObject2);
/*  762 */     if (!((Point)localObject1).equals(localPoint)) {
/*  763 */       handleMoved((WindowDimensions)localObject2);
/*      */     }
/*  765 */     reconfigureContentWindow((WindowDimensions)localObject2);
/*  766 */     updateChildrenSizes();
/*      */ 
/*  768 */     repositionSecurityWarning();
/*      */   }
/*      */ 
/*      */   private void checkShellRectSize(Rectangle paramRectangle) {
/*  772 */     paramRectangle.width = Math.max(1, paramRectangle.width);
/*  773 */     paramRectangle.height = Math.max(1, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   private void checkShellRectPos(Rectangle paramRectangle) {
/*  777 */     int i = XWM.getWMID();
/*  778 */     if (((i == 5) || (i == 6)) && 
/*  779 */       (paramRectangle.x == 0) && (paramRectangle.y == 0))
/*  780 */       paramRectangle.x = (paramRectangle.y = 1);
/*      */   }
/*      */ 
/*      */   private void checkShellRect(Rectangle paramRectangle)
/*      */   {
/*  786 */     checkShellRectSize(paramRectangle);
/*  787 */     checkShellRectPos(paramRectangle);
/*      */   }
/*      */ 
/*      */   public void setShellBounds(Rectangle paramRectangle) {
/*  791 */     if (insLog.isLoggable(500)) insLog.fine("Setting shell bounds on " + this + " to " + paramRectangle);
/*      */ 
/*  793 */     XToolkit.awtLock();
/*      */     try {
/*  795 */       updateSizeHints(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*  796 */       XlibWrapper.XResizeWindow(XToolkit.getDisplay(), getShell(), paramRectangle.width, paramRectangle.height);
/*  797 */       XlibWrapper.XMoveWindow(XToolkit.getDisplay(), getShell(), paramRectangle.x, paramRectangle.y);
/*      */     }
/*      */     finally {
/*  800 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*  804 */   public void setShellSize(Rectangle paramRectangle) { if (insLog.isLoggable(500)) insLog.fine("Setting shell size on " + this + " to " + paramRectangle);
/*      */ 
/*  806 */     XToolkit.awtLock();
/*      */     try {
/*  808 */       updateSizeHints(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*  809 */       XlibWrapper.XResizeWindow(XToolkit.getDisplay(), getShell(), paramRectangle.width, paramRectangle.height);
/*      */     }
/*      */     finally {
/*  812 */       XToolkit.awtUnlock();
/*      */     } }
/*      */ 
/*      */   public void setShellPosition(Rectangle paramRectangle) {
/*  816 */     if (insLog.isLoggable(500)) insLog.fine("Setting shell position on " + this + " to " + paramRectangle);
/*      */ 
/*  818 */     XToolkit.awtLock();
/*      */     try {
/*  820 */       updateSizeHints(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*  821 */       XlibWrapper.XMoveWindow(XToolkit.getDisplay(), getShell(), paramRectangle.x, paramRectangle.y);
/*      */     }
/*      */     finally {
/*  824 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   void initResizability() {
/*  829 */     setResizable(this.winAttr.initialResizability);
/*      */   }
/*      */   public void setResizable(boolean paramBoolean) {
/*  832 */     int i = this.winAttr.functions;
/*  833 */     if ((!isResizable()) && (paramBoolean)) {
/*  834 */       this.currentInsets = new Insets(0, 0, 0, 0);
/*  835 */       resetWMSetInsets();
/*  836 */       if (!isEmbedded()) {
/*  837 */         setReparented(false);
/*      */       }
/*  839 */       this.winAttr.isResizable = paramBoolean;
/*  840 */       if ((i & 0x1) != 0)
/*  841 */         i &= -19;
/*      */       else {
/*  843 */         i |= 18;
/*      */       }
/*  845 */       this.winAttr.functions = i;
/*  846 */       XWM.setShellResizable(this);
/*  847 */     } else if ((isResizable()) && (!paramBoolean)) {
/*  848 */       this.currentInsets = new Insets(0, 0, 0, 0);
/*  849 */       resetWMSetInsets();
/*  850 */       if (!isEmbedded()) {
/*  851 */         setReparented(false);
/*      */       }
/*  853 */       this.winAttr.isResizable = paramBoolean;
/*  854 */       if ((i & 0x1) != 0)
/*  855 */         i |= 18;
/*      */       else {
/*  857 */         i &= -19;
/*      */       }
/*  859 */       this.winAttr.functions = i;
/*  860 */       XWM.setShellNotResizable(this, this.dimensions, this.dimensions.getBounds(), false);
/*      */     }
/*      */   }
/*      */ 
/*      */   Rectangle getShellBounds() {
/*  865 */     return this.dimensions.getClientRect();
/*      */   }
/*      */ 
/*      */   public Rectangle getBounds() {
/*  869 */     return this.dimensions.getBounds();
/*      */   }
/*      */ 
/*      */   public Dimension getSize() {
/*  873 */     return this.dimensions.getSize();
/*      */   }
/*      */ 
/*      */   public int getX() {
/*  877 */     return this.dimensions.getLocation().x;
/*      */   }
/*      */ 
/*      */   public int getY() {
/*  881 */     return this.dimensions.getLocation().y;
/*      */   }
/*      */ 
/*      */   public Point getLocation() {
/*  885 */     return this.dimensions.getLocation();
/*      */   }
/*      */ 
/*      */   public int getAbsoluteX()
/*      */   {
/*  890 */     return this.dimensions.getScreenBounds().x;
/*      */   }
/*      */ 
/*      */   public int getAbsoluteY()
/*      */   {
/*  895 */     return this.dimensions.getScreenBounds().y;
/*      */   }
/*      */ 
/*      */   public int getWidth() {
/*  899 */     return getSize().width;
/*      */   }
/*      */ 
/*      */   public int getHeight() {
/*  903 */     return getSize().height;
/*      */   }
/*      */ 
/*      */   public final WindowDimensions getDimensions() {
/*  907 */     return this.dimensions;
/*      */   }
/*      */ 
/*      */   public Point getLocationOnScreen() {
/*  911 */     XToolkit.awtLock();
/*      */     try {
/*  913 */       if (this.configure_seen) {
/*  914 */         return toGlobal(0, 0);
/*      */       }
/*  916 */       Point localPoint1 = this.target.getLocation();
/*  917 */       if (insLog.isLoggable(500)) {
/*  918 */         insLog.fine("getLocationOnScreen {0} not reparented: {1} ", new Object[] { this, localPoint1 });
/*      */       }
/*  920 */       return localPoint1;
/*      */     }
/*      */     finally {
/*  923 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isEventDisabled(XEvent paramXEvent)
/*      */   {
/*  933 */     switch (paramXEvent.get_type())
/*      */     {
/*      */     case 22:
/*  936 */       return true;
/*      */     case 7:
/*      */     case 8:
/*  941 */       return true;
/*      */     }
/*  943 */     return super.isEventDisabled(paramXEvent);
/*      */   }
/*      */ 
/*      */   int getDecorations()
/*      */   {
/*  948 */     return this.winAttr.decorations;
/*      */   }
/*      */ 
/*      */   int getFunctions() {
/*  952 */     return this.winAttr.functions;
/*      */   }
/*      */ 
/*      */   public void setVisible(boolean paramBoolean) {
/*  956 */     log.finer("Setting {0} to visible {1}", new Object[] { this, Boolean.valueOf(paramBoolean) });
/*  957 */     if ((paramBoolean) && (!isVisible())) {
/*  958 */       XWM.setShellDecor(this);
/*  959 */       super.setVisible(paramBoolean);
/*  960 */       if (this.winAttr.isResizable)
/*      */       {
/*  963 */         XWM.removeSizeHints(this, 32L);
/*  964 */         updateMinimumSize();
/*      */       }
/*      */     } else {
/*  967 */       super.setVisible(paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void suppressWmTakeFocus(boolean paramBoolean) {
/*  972 */     XAtomList localXAtomList = getWMProtocols();
/*  973 */     if (paramBoolean)
/*  974 */       localXAtomList.remove(wm_take_focus);
/*      */     else {
/*  976 */       localXAtomList.add(wm_take_focus);
/*      */     }
/*  978 */     wm_protocols.setAtomListProperty(this, localXAtomList);
/*      */   }
/*      */ 
/*      */   public void dispose() {
/*  982 */     if (this.content != null) {
/*  983 */       this.content.destroy();
/*      */     }
/*  985 */     this.focusProxy.destroy();
/*      */ 
/*  987 */     if (this.iconWindow != null) {
/*  988 */       this.iconWindow.destroy();
/*      */     }
/*      */ 
/*  991 */     super.dispose();
/*      */   }
/*      */ 
/*      */   public void handleClientMessage(XEvent paramXEvent) {
/*  995 */     super.handleClientMessage(paramXEvent);
/*  996 */     XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/*  997 */     if ((wm_protocols != null) && (localXClientMessageEvent.get_message_type() == wm_protocols.getAtom()))
/*  998 */       if (localXClientMessageEvent.get_data(0) == wm_delete_window.getAtom())
/*  999 */         handleQuit();
/* 1000 */       else if (localXClientMessageEvent.get_data(0) == wm_take_focus.getAtom())
/* 1001 */         handleWmTakeFocus(localXClientMessageEvent);
/*      */   }
/*      */ 
/*      */   private void handleWmTakeFocus(XClientMessageEvent paramXClientMessageEvent)
/*      */   {
/* 1007 */     focusLog.fine("WM_TAKE_FOCUS on {0}", new Object[] { this });
/* 1008 */     requestWindowFocus(paramXClientMessageEvent.get_data(1), true);
/*      */   }
/*      */ 
/*      */   protected void requestXFocus(long paramLong, boolean paramBoolean)
/*      */   {
/* 1019 */     if (this.focusProxy == null) {
/* 1020 */       if (focusLog.isLoggable(500)) focusLog.warning("Focus proxy is null for " + this); 
/*      */     }
/* 1022 */     else { if (focusLog.isLoggable(500)) focusLog.fine("Requesting focus to proxy: " + this.focusProxy);
/* 1023 */       if (paramBoolean)
/* 1024 */         this.focusProxy.xRequestFocus(paramLong);
/*      */       else
/* 1026 */         this.focusProxy.xRequestFocus();
/*      */     }
/*      */   }
/*      */ 
/*      */   XFocusProxyWindow getFocusProxy()
/*      */   {
/* 1032 */     return this.focusProxy;
/*      */   }
/*      */ 
/*      */   public void handleQuit() {
/* 1036 */     postEvent(new WindowEvent((Window)this.target, 201));
/*      */   }
/*      */ 
/*      */   final void dumpMe() {
/* 1040 */     System.err.println(">>> Peer: " + this.x + ", " + this.y + ", " + this.width + ", " + this.height);
/*      */   }
/*      */ 
/*      */   final void dumpTarget() {
/* 1044 */     AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/* 1045 */     int i = localComponentAccessor.getWidth(this.target);
/* 1046 */     int j = localComponentAccessor.getHeight(this.target);
/* 1047 */     int k = localComponentAccessor.getX(this.target);
/* 1048 */     int m = localComponentAccessor.getY(this.target);
/* 1049 */     System.err.println(">>> Target: " + k + ", " + m + ", " + i + ", " + j);
/*      */   }
/*      */ 
/*      */   final void dumpShell() {
/* 1053 */     dumpWindow("Shell", getShell());
/*      */   }
/*      */   final void dumpContent() {
/* 1056 */     dumpWindow("Content", getContentWindow());
/*      */   }
/*      */   final void dumpParent() {
/* 1059 */     long l = XlibUtil.getParentWindow(getShell());
/* 1060 */     if (l != 0L)
/*      */     {
/* 1062 */       dumpWindow("Parent", l);
/*      */     }
/*      */     else
/*      */     {
/* 1066 */       System.err.println(">>> NO PARENT");
/*      */     }
/*      */   }
/*      */ 
/*      */   final void dumpWindow(String paramString, long paramLong) {
/* 1071 */     XWindowAttributes localXWindowAttributes = new XWindowAttributes();
/*      */     try {
/* 1073 */       XToolkit.awtLock();
/*      */       try {
/* 1075 */         int i = XlibWrapper.XGetWindowAttributes(XToolkit.getDisplay(), paramLong, localXWindowAttributes.pData);
/*      */       }
/*      */       finally
/*      */       {
/* 1080 */         XToolkit.awtUnlock();
/*      */       }
/* 1082 */       System.err.println(">>>> " + paramString + ": " + localXWindowAttributes.get_x() + ", " + localXWindowAttributes.get_y() + ", " + localXWindowAttributes.get_width() + ", " + localXWindowAttributes.get_height());
/*      */     }
/*      */     finally
/*      */     {
/* 1086 */       localXWindowAttributes.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   final void dumpAll() {
/* 1091 */     dumpTarget();
/* 1092 */     dumpMe();
/* 1093 */     dumpParent();
/* 1094 */     dumpShell();
/* 1095 */     dumpContent();
/*      */   }
/*      */ 
/*      */   boolean isMaximized() {
/* 1099 */     return false;
/*      */   }
/*      */ 
/*      */   boolean isOverrideRedirect()
/*      */   {
/* 1104 */     return Window.Type.POPUP.equals(getWindowType());
/*      */   }
/*      */ 
/*      */   public boolean requestWindowFocus(long paramLong, boolean paramBoolean) {
/* 1108 */     focusLog.fine("Request for decorated window focus");
/*      */ 
/* 1111 */     Window localWindow1 = XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow();
/* 1112 */     Window localWindow2 = XWindowPeer.getDecoratedOwner(localWindow1);
/*      */ 
/* 1114 */     focusLog.finer("Current window is: active={0}, focused={1}", new Object[] { Boolean.valueOf(this.target == localWindow2 ? 1 : false), Boolean.valueOf(this.target == localWindow1 ? 1 : false) });
/*      */ 
/* 1118 */     Object localObject = this;
/* 1119 */     while (((XWindowPeer)localObject).nextTransientFor != null) {
/* 1120 */       localObject = ((XWindowPeer)localObject).nextTransientFor;
/*      */     }
/* 1122 */     if ((localObject == null) || (!((XWindowPeer)localObject).focusAllowedFor()))
/*      */     {
/* 1125 */       return false;
/*      */     }
/* 1127 */     if (this == localObject) {
/* 1128 */       if (isWMStateNetHidden()) {
/* 1129 */         focusLog.fine("The window is unmapped, so rejecting the request");
/* 1130 */         return false;
/*      */       }
/* 1132 */       if ((this.target == localWindow2) && (this.target != localWindow1))
/*      */       {
/* 1134 */         focusLog.fine("Focus is on child window - transfering it back to the owner");
/* 1135 */         handleWindowFocusInSync(-1L);
/* 1136 */         return true;
/*      */       }
/* 1138 */       Window localWindow3 = XWindowPeer.getNativeFocusedWindow();
/* 1139 */       focusLog.finest("Real native focused window: " + localWindow3 + "\nKFM's focused window: " + localWindow1);
/*      */ 
/* 1143 */       if (this.target == localWindow3) {
/* 1144 */         focusLog.fine("The window is already natively focused.");
/* 1145 */         return true;
/*      */       }
/*      */     }
/* 1148 */     focusLog.fine("Requesting focus to " + (this == localObject ? "this window" : localObject));
/*      */ 
/* 1150 */     if (paramBoolean)
/* 1151 */       ((XWindowPeer)localObject).requestXFocus(paramLong);
/*      */     else {
/* 1153 */       ((XWindowPeer)localObject).requestXFocus();
/*      */     }
/* 1155 */     return this == localObject;
/*      */   }
/*      */ 
/*      */   void setActualFocusedWindow(XWindowPeer paramXWindowPeer)
/*      */   {
/* 1160 */     synchronized (getStateLock()) {
/* 1161 */       this.actualFocusedWindow = paramXWindowPeer;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean requestWindowFocus(XWindowPeer paramXWindowPeer, long paramLong, boolean paramBoolean)
/*      */   {
/* 1168 */     setActualFocusedWindow(paramXWindowPeer);
/* 1169 */     return requestWindowFocus(paramLong, paramBoolean);
/*      */   }
/*      */   public void handleWindowFocusIn(long paramLong) {
/* 1172 */     if (null == this.actualFocusedWindow) {
/* 1173 */       super.handleWindowFocusIn(paramLong);
/*      */     }
/*      */     else
/*      */     {
/* 1187 */       postEvent(new InvocationEvent(this.target, new Runnable() {
/*      */         public void run() {
/* 1189 */           Object localObject1 = null;
/* 1190 */           synchronized (XDecoratedPeer.this.getStateLock()) {
/* 1191 */             localObject1 = XDecoratedPeer.this.actualFocusedWindow;
/* 1192 */             XDecoratedPeer.this.actualFocusedWindow = null;
/* 1193 */             if ((null == localObject1) || (!((XWindowPeer)localObject1).isVisible()) || (!((XWindowPeer)localObject1).isFocusableWindow())) {
/* 1194 */               localObject1 = XDecoratedPeer.this;
/*      */             }
/*      */           }
/* 1197 */           ((XWindowPeer)localObject1).handleWindowFocusIn_Dispatch();
/*      */         }
/*      */       }));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleWindowFocusOut(Window paramWindow, long paramLong) {
/* 1204 */     Window localWindow1 = XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow();
/*      */ 
/* 1207 */     if ((localWindow1 != null) && (localWindow1 != this.target)) {
/* 1208 */       Window localWindow2 = XWindowPeer.getDecoratedOwner(localWindow1);
/*      */ 
/* 1210 */       if ((localWindow2 != null) && (localWindow2 == this.target)) {
/* 1211 */         setActualFocusedWindow((XWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow1));
/*      */       }
/*      */     }
/* 1214 */     super.handleWindowFocusOut(paramWindow, paramLong);
/*      */   }
/*      */ 
/*      */   private Point queryXLocation()
/*      */   {
/* 1219 */     return XlibUtil.translateCoordinates(getContentWindow(), XlibWrapper.RootWindow(XToolkit.getDisplay(), getScreenNumber()), new Point(0, 0));
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XDecoratedPeer
 * JD-Core Version:    0.6.2
 */