/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class XBaseWindow
/*      */ {
/*   34 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XBaseWindow");
/*   35 */   private static final PlatformLogger insLog = PlatformLogger.getLogger("sun.awt.X11.insets.XBaseWindow");
/*   36 */   private static final PlatformLogger eventLog = PlatformLogger.getLogger("sun.awt.X11.event.XBaseWindow");
/*   37 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.X11.focus.XBaseWindow");
/*   38 */   private static final PlatformLogger grabLog = PlatformLogger.getLogger("sun.awt.X11.grab.XBaseWindow");
/*      */   public static final String PARENT_WINDOW = "parent window";
/*      */   public static final String BOUNDS = "bounds";
/*      */   public static final String OVERRIDE_REDIRECT = "overrideRedirect";
/*      */   public static final String EVENT_MASK = "event mask";
/*      */   public static final String VALUE_MASK = "value mask";
/*      */   public static final String BORDER_PIXEL = "border pixel";
/*      */   public static final String COLORMAP = "color map";
/*      */   public static final String DEPTH = "visual depth";
/*      */   public static final String VISUAL_CLASS = "visual class";
/*      */   public static final String VISUAL = "visual";
/*      */   public static final String EMBEDDED = "embedded";
/*      */   public static final String DELAYED = "delayed";
/*      */   public static final String PARENT = "parent";
/*      */   public static final String BACKGROUND_PIXMAP = "pixmap";
/*      */   public static final String VISIBLE = "visible";
/*      */   public static final String SAVE_UNDER = "save under";
/*      */   public static final String BACKING_STORE = "backing store";
/*      */   public static final String BIT_GRAVITY = "bit gravity";
/*      */   private XCreateWindowParams delayedParams;
/*   61 */   Set<Long> children = new HashSet();
/*      */   long window;
/*      */   boolean visible;
/*      */   boolean mapped;
/*      */   boolean embedded;
/*      */   Rectangle maxBounds;
/*      */   volatile XBaseWindow parentWindow;
/*      */   private boolean disposed;
/*      */   private long screen;
/*      */   private XSizeHints hints;
/*      */   private XWMHints wmHints;
/*      */   static final int MIN_SIZE = 1;
/*      */   static final int DEF_LOCATION = 1;
/*      */   private static XAtom wm_client_leader;
/*      */   private InitialiseState initialising;
/*      */   int x;
/*      */   int y;
/*      */   int width;
/*      */   int height;
/*      */   protected StateLock state_lock;
/*      */ 
/*      */   void awtLock()
/*      */   {
/*   95 */     XToolkit.awtLock();
/*      */   }
/*      */ 
/*      */   void awtUnlock() {
/*   99 */     XToolkit.awtUnlock();
/*      */   }
/*      */ 
/*      */   void awtLockNotifyAll() {
/*  103 */     XToolkit.awtLockNotifyAll();
/*      */   }
/*      */ 
/*      */   void awtLockWait() throws InterruptedException {
/*  107 */     XToolkit.awtLockWait();
/*      */   }
/*      */ 
/*      */   protected final void init(long paramLong, Rectangle paramRectangle)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected final void preInit()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected final void postInit()
/*      */   {
/*      */   }
/*      */ 
/*      */   void instantPreInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  124 */     this.state_lock = new StateLock();
/*  125 */     this.initialising = InitialiseState.NOT_INITIALISED;
/*      */   }
/*      */ 
/*      */   void preInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  133 */     this.state_lock = new StateLock();
/*  134 */     this.initialising = InitialiseState.NOT_INITIALISED;
/*  135 */     this.embedded = Boolean.TRUE.equals(paramXCreateWindowParams.get("embedded"));
/*  136 */     this.visible = Boolean.TRUE.equals(paramXCreateWindowParams.get("visible"));
/*      */ 
/*  138 */     Object localObject = paramXCreateWindowParams.get("parent");
/*  139 */     if ((localObject instanceof XBaseWindow)) {
/*  140 */       this.parentWindow = ((XBaseWindow)localObject);
/*      */     } else {
/*  142 */       localLong = (Long)paramXCreateWindowParams.get("parent window");
/*  143 */       if (localLong != null) {
/*  144 */         this.parentWindow = XToolkit.windowToXWindow(localLong.longValue());
/*      */       }
/*      */     }
/*      */ 
/*  148 */     Long localLong = (Long)paramXCreateWindowParams.get("event mask");
/*  149 */     if (localLong != null) {
/*  150 */       long l = localLong.longValue();
/*  151 */       l |= 524288L;
/*  152 */       paramXCreateWindowParams.put("event mask", Long.valueOf(l));
/*      */     }
/*      */ 
/*  155 */     this.screen = -1L;
/*      */   }
/*      */ 
/*      */   void postInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  163 */     if (log.isLoggable(500)) log.fine("WM name is " + getWMName());
/*  164 */     updateWMName();
/*      */ 
/*  167 */     initClientLeader();
/*      */   }
/*      */ 
/*      */   protected final void init(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  177 */     awtLock();
/*  178 */     this.initialising = InitialiseState.INITIALISING;
/*  179 */     awtUnlock();
/*      */     try
/*      */     {
/*  182 */       if (!Boolean.TRUE.equals(paramXCreateWindowParams.get("delayed"))) {
/*  183 */         preInit(paramXCreateWindowParams);
/*  184 */         create(paramXCreateWindowParams);
/*  185 */         postInit(paramXCreateWindowParams);
/*      */       } else {
/*  187 */         instantPreInit(paramXCreateWindowParams);
/*  188 */         this.delayedParams = paramXCreateWindowParams;
/*      */       }
/*  190 */       awtLock();
/*  191 */       this.initialising = InitialiseState.INITIALISED;
/*  192 */       awtLockNotifyAll();
/*  193 */       awtUnlock();
/*      */     } catch (RuntimeException localRuntimeException) {
/*  195 */       awtLock();
/*  196 */       this.initialising = InitialiseState.FAILED_INITIALISATION;
/*  197 */       awtLockNotifyAll();
/*  198 */       awtUnlock();
/*  199 */       throw localRuntimeException;
/*      */     } catch (Throwable localThrowable) {
/*  201 */       log.warning("Exception during peer initialization", localThrowable);
/*  202 */       awtLock();
/*  203 */       this.initialising = InitialiseState.FAILED_INITIALISATION;
/*  204 */       awtLockNotifyAll();
/*  205 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean checkInitialised() {
/*  210 */     awtLock();
/*      */     try
/*      */     {
/*      */       boolean bool2;
/*  212 */       switch (1.$SwitchMap$sun$awt$X11$XBaseWindow$InitialiseState[this.initialising.ordinal()]) {
/*      */       case 1:
/*  214 */         return true;
/*      */       case 2:
/*      */         try {
/*  217 */           while (this.initialising != InitialiseState.INITIALISED)
/*  218 */             awtLockWait();
/*      */         }
/*      */         catch (InterruptedException localInterruptedException) {
/*  221 */           return false;
/*      */         }
/*  223 */         return true;
/*      */       case 3:
/*      */       case 4:
/*  226 */         return false;
/*      */       }
/*  228 */       return false;
/*      */     }
/*      */     finally {
/*  231 */       awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   XBaseWindow()
/*      */   {
/*  239 */     this(new XCreateWindowParams());
/*      */   }
/*      */ 
/*      */   XBaseWindow(long paramLong, Rectangle paramRectangle)
/*      */   {
/*  246 */     this(new XCreateWindowParams(new Object[] { "bounds", paramRectangle, "parent window", Long.valueOf(paramLong) }));
/*      */   }
/*      */ 
/*      */   XBaseWindow(Rectangle paramRectangle)
/*      */   {
/*  255 */     this(new XCreateWindowParams(new Object[] { "bounds", paramRectangle }));
/*      */   }
/*      */ 
/*      */   public XBaseWindow(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  261 */     init(paramXCreateWindowParams);
/*      */   }
/*      */ 
/*      */   XBaseWindow(long paramLong)
/*      */   {
/*  267 */     this(new XCreateWindowParams(new Object[] { "parent window", Long.valueOf(paramLong), "embedded", Boolean.TRUE }));
/*      */   }
/*      */ 
/*      */   protected void checkParams(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  279 */     if (paramXCreateWindowParams == null) {
/*  280 */       throw new IllegalArgumentException("Window creation parameters are null");
/*      */     }
/*  282 */     paramXCreateWindowParams.putIfNull("parent window", Long.valueOf(XToolkit.getDefaultRootWindow()));
/*  283 */     paramXCreateWindowParams.putIfNull("bounds", new Rectangle(1, 1, 1, 1));
/*  284 */     paramXCreateWindowParams.putIfNull("visual depth", Integer.valueOf(0));
/*  285 */     paramXCreateWindowParams.putIfNull("visual", Long.valueOf(0L));
/*  286 */     paramXCreateWindowParams.putIfNull("visual class", Integer.valueOf(2));
/*  287 */     paramXCreateWindowParams.putIfNull("value mask", Long.valueOf(2048L));
/*  288 */     Rectangle localRectangle = (Rectangle)paramXCreateWindowParams.get("bounds");
/*  289 */     localRectangle.width = Math.max(1, localRectangle.width);
/*  290 */     localRectangle.height = Math.max(1, localRectangle.height);
/*      */ 
/*  292 */     Long localLong = (Long)paramXCreateWindowParams.get("event mask");
/*  293 */     long l = localLong != null ? localLong.longValue() : 0L;
/*      */ 
/*  296 */     l |= 20971520L;
/*  297 */     paramXCreateWindowParams.put("event mask", Long.valueOf(l));
/*      */   }
/*      */ 
/*      */   private final void create(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  305 */     XToolkit.awtLock();
/*      */     try {
/*  307 */       XSetWindowAttributes localXSetWindowAttributes = new XSetWindowAttributes();
/*      */       try {
/*  309 */         checkParams(paramXCreateWindowParams);
/*      */ 
/*  311 */         long l = ((Long)paramXCreateWindowParams.get("value mask")).longValue();
/*      */ 
/*  313 */         Long localLong1 = (Long)paramXCreateWindowParams.get("event mask");
/*  314 */         localXSetWindowAttributes.set_event_mask(localLong1.longValue());
/*  315 */         l |= 2048L;
/*      */ 
/*  317 */         Long localLong2 = (Long)paramXCreateWindowParams.get("border pixel");
/*  318 */         if (localLong2 != null) {
/*  319 */           localXSetWindowAttributes.set_border_pixel(localLong2.longValue());
/*  320 */           l |= 8L;
/*      */         }
/*      */ 
/*  323 */         Long localLong3 = (Long)paramXCreateWindowParams.get("color map");
/*  324 */         if (localLong3 != null) {
/*  325 */           localXSetWindowAttributes.set_colormap(localLong3.longValue());
/*  326 */           l |= 8192L;
/*      */         }
/*  328 */         Long localLong4 = (Long)paramXCreateWindowParams.get("pixmap");
/*  329 */         if (localLong4 != null) {
/*  330 */           localXSetWindowAttributes.set_background_pixmap(localLong4.longValue());
/*  331 */           l |= 1L;
/*      */         }
/*      */ 
/*  334 */         Long localLong5 = (Long)paramXCreateWindowParams.get("parent window");
/*  335 */         Rectangle localRectangle = (Rectangle)paramXCreateWindowParams.get("bounds");
/*  336 */         Integer localInteger1 = (Integer)paramXCreateWindowParams.get("visual depth");
/*  337 */         Integer localInteger2 = (Integer)paramXCreateWindowParams.get("visual class");
/*  338 */         Long localLong6 = (Long)paramXCreateWindowParams.get("visual");
/*  339 */         Boolean localBoolean1 = (Boolean)paramXCreateWindowParams.get("overrideRedirect");
/*  340 */         if (localBoolean1 != null) {
/*  341 */           localXSetWindowAttributes.set_override_redirect(localBoolean1.booleanValue());
/*  342 */           l |= 512L;
/*      */         }
/*      */ 
/*  345 */         Boolean localBoolean2 = (Boolean)paramXCreateWindowParams.get("save under");
/*  346 */         if (localBoolean2 != null) {
/*  347 */           localXSetWindowAttributes.set_save_under(localBoolean2.booleanValue());
/*  348 */           l |= 1024L;
/*      */         }
/*      */ 
/*  351 */         Integer localInteger3 = (Integer)paramXCreateWindowParams.get("backing store");
/*  352 */         if (localInteger3 != null) {
/*  353 */           localXSetWindowAttributes.set_backing_store(localInteger3.intValue());
/*  354 */           l |= 64L;
/*      */         }
/*      */ 
/*  357 */         Integer localInteger4 = (Integer)paramXCreateWindowParams.get("bit gravity");
/*  358 */         if (localInteger4 != null) {
/*  359 */           localXSetWindowAttributes.set_bit_gravity(localInteger4.intValue());
/*  360 */           l |= 16L;
/*      */         }
/*      */ 
/*  363 */         if (log.isLoggable(500)) {
/*  364 */           log.fine("Creating window for " + this + " with the following attributes: \n" + paramXCreateWindowParams);
/*      */         }
/*  366 */         this.window = XlibWrapper.XCreateWindow(XToolkit.getDisplay(), localLong5.longValue(), localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, 0, localInteger1.intValue(), localInteger2.intValue(), localLong6.longValue(), l, localXSetWindowAttributes.pData);
/*      */ 
/*  377 */         if (this.window == 0L) {
/*  378 */           throw new IllegalStateException("Couldn't create window because of wrong parameters. Run with NOISY_AWT to see details");
/*      */         }
/*  380 */         XToolkit.addToWinMap(this.window, this);
/*      */       } finally {
/*  382 */         localXSetWindowAttributes.dispose();
/*      */       }
/*      */     } finally {
/*  385 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public XCreateWindowParams getDelayedParams() {
/*  390 */     return this.delayedParams;
/*      */   }
/*      */ 
/*      */   protected String getWMName() {
/*  394 */     return XToolkit.getCorrectXIDString(getClass().getName());
/*      */   }
/*      */ 
/*      */   protected void initClientLeader() {
/*  398 */     XToolkit.awtLock();
/*      */     try {
/*  400 */       if (wm_client_leader == null) {
/*  401 */         wm_client_leader = XAtom.get("WM_CLIENT_LEADER");
/*      */       }
/*  403 */       wm_client_leader.setWindowProperty(this, getXAWTRootWindow());
/*      */     } finally {
/*  405 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static XRootWindow getXAWTRootWindow() {
/*  410 */     return XRootWindow.getInstance();
/*      */   }
/*      */ 
/*      */   void destroy() {
/*  414 */     XToolkit.awtLock();
/*      */     try {
/*  416 */       if (this.hints != null) {
/*  417 */         XlibWrapper.XFree(this.hints.pData);
/*  418 */         this.hints = null;
/*      */       }
/*  420 */       XToolkit.removeFromWinMap(getWindow(), this);
/*  421 */       XlibWrapper.XDestroyWindow(XToolkit.getDisplay(), getWindow());
/*  422 */       if (XPropertyCache.isCachingSupported()) {
/*  423 */         XPropertyCache.clearCache(this.window);
/*      */       }
/*  425 */       this.window = -1L;
/*  426 */       if (!isDisposed()) {
/*  427 */         setDisposed(true);
/*      */       }
/*      */ 
/*  430 */       XAwtState.getGrabWindow();
/*      */     } finally {
/*  432 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   void flush() {
/*  437 */     XToolkit.awtLock();
/*      */     try {
/*  439 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*      */     } finally {
/*  441 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void setWMHints(XWMHints paramXWMHints)
/*      */   {
/*  449 */     XToolkit.awtLock();
/*      */     try {
/*  451 */       XlibWrapper.XSetWMHints(XToolkit.getDisplay(), getWindow(), paramXWMHints.pData);
/*      */     } finally {
/*  453 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public XWMHints getWMHints() {
/*  458 */     if (this.wmHints == null) {
/*  459 */       this.wmHints = new XWMHints(XlibWrapper.XAllocWMHints());
/*      */     }
/*      */ 
/*  464 */     return this.wmHints;
/*      */   }
/*      */ 
/*      */   public XSizeHints getHints()
/*      */   {
/*  473 */     if (this.hints == null) {
/*  474 */       long l = XlibWrapper.XAllocSizeHints();
/*  475 */       this.hints = new XSizeHints(l);
/*      */     }
/*      */ 
/*  479 */     return this.hints;
/*      */   }
/*      */ 
/*      */   public void setSizeHints(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  483 */     if (insLog.isLoggable(400)) insLog.finer("Setting hints, flags " + XlibWrapper.hintsToString(paramLong));
/*  484 */     XToolkit.awtLock();
/*      */     try {
/*  486 */       XSizeHints localXSizeHints = getHints();
/*      */ 
/*  490 */       if ((paramLong & 0x4) != 0L) {
/*  491 */         localXSizeHints.set_x(paramInt1);
/*  492 */         localXSizeHints.set_y(paramInt2);
/*      */       }
/*  494 */       if ((paramLong & 0x8) != 0L) {
/*  495 */         localXSizeHints.set_width(paramInt3);
/*  496 */         localXSizeHints.set_height(paramInt4);
/*  497 */       } else if ((localXSizeHints.get_flags() & 0x8) != 0L) {
/*  498 */         paramLong |= 8L;
/*      */       }
/*  500 */       if ((paramLong & 0x10) != 0L) {
/*  501 */         localXSizeHints.set_min_width(paramInt3);
/*  502 */         localXSizeHints.set_min_height(paramInt4);
/*  503 */       } else if ((localXSizeHints.get_flags() & 0x10) != 0L) {
/*  504 */         paramLong |= 16L;
/*      */       }
/*      */ 
/*  508 */       if ((paramLong & 0x20) != 0L) {
/*  509 */         if (this.maxBounds != null) {
/*  510 */           if (this.maxBounds.width != 2147483647)
/*  511 */             localXSizeHints.set_max_width(this.maxBounds.width);
/*      */           else {
/*  513 */             localXSizeHints.set_max_width(XToolkit.getDefaultScreenWidth());
/*      */           }
/*  515 */           if (this.maxBounds.height != 2147483647)
/*  516 */             localXSizeHints.set_max_height(this.maxBounds.height);
/*      */           else
/*  518 */             localXSizeHints.set_max_height(XToolkit.getDefaultScreenHeight());
/*      */         }
/*      */         else {
/*  521 */           localXSizeHints.set_max_width(paramInt3);
/*  522 */           localXSizeHints.set_max_height(paramInt4);
/*      */         }
/*  524 */       } else if ((localXSizeHints.get_flags() & 0x20) != 0L) {
/*  525 */         paramLong |= 32L;
/*  526 */         if (this.maxBounds != null) {
/*  527 */           if (this.maxBounds.width != 2147483647)
/*  528 */             localXSizeHints.set_max_width(this.maxBounds.width);
/*      */           else {
/*  530 */             localXSizeHints.set_max_width(XToolkit.getDefaultScreenWidth());
/*      */           }
/*  532 */           if (this.maxBounds.height != 2147483647)
/*  533 */             localXSizeHints.set_max_height(this.maxBounds.height);
/*      */           else {
/*  535 */             localXSizeHints.set_max_height(XToolkit.getDefaultScreenHeight());
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  541 */       paramLong |= 512L;
/*  542 */       localXSizeHints.set_flags(paramLong);
/*  543 */       localXSizeHints.set_win_gravity(1);
/*  544 */       if (insLog.isLoggable(400)) insLog.finer("Setting hints, resulted flags " + XlibWrapper.hintsToString(paramLong) + ", values " + localXSizeHints);
/*      */ 
/*  546 */       XlibWrapper.XSetWMNormalHints(XToolkit.getDisplay(), getWindow(), localXSizeHints.pData);
/*      */     } finally {
/*  548 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isMinSizeSet() {
/*  553 */     XSizeHints localXSizeHints = getHints();
/*  554 */     long l = localXSizeHints.get_flags();
/*  555 */     return (l & 0x10) == 16L;
/*      */   }
/*      */ 
/*      */   Object getStateLock()
/*      */   {
/*  563 */     return this.state_lock;
/*      */   }
/*      */ 
/*      */   public long getWindow() {
/*  567 */     return this.window;
/*      */   }
/*      */   public long getContentWindow() {
/*  570 */     return this.window;
/*      */   }
/*      */ 
/*      */   public XBaseWindow getContentXWindow() {
/*  574 */     return XToolkit.windowToXWindow(getContentWindow());
/*      */   }
/*      */ 
/*      */   public Rectangle getBounds() {
/*  578 */     return new Rectangle(this.x, this.y, this.width, this.height);
/*      */   }
/*      */   public Dimension getSize() {
/*  581 */     return new Dimension(this.width, this.height);
/*      */   }
/*      */ 
/*      */   public void toFront()
/*      */   {
/*  586 */     XToolkit.awtLock();
/*      */     try {
/*  588 */       XlibWrapper.XRaiseWindow(XToolkit.getDisplay(), getWindow());
/*      */     } finally {
/*  590 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*  594 */   public void xRequestFocus(long paramLong) { XToolkit.awtLock();
/*      */     try {
/*  596 */       if (focusLog.isLoggable(400)) focusLog.finer("XSetInputFocus on " + Long.toHexString(getWindow()) + " with time " + paramLong);
/*  597 */       XlibWrapper.XSetInputFocus2(XToolkit.getDisplay(), getWindow(), paramLong);
/*      */     } finally {
/*  599 */       XToolkit.awtUnlock();
/*      */     } }
/*      */ 
/*      */   public void xRequestFocus() {
/*  603 */     XToolkit.awtLock();
/*      */     try {
/*  605 */       if (focusLog.isLoggable(400)) focusLog.finer("XSetInputFocus on " + Long.toHexString(getWindow()));
/*  606 */       XlibWrapper.XSetInputFocus(XToolkit.getDisplay(), getWindow());
/*      */     } finally {
/*  608 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static long xGetInputFocus() {
/*  613 */     XToolkit.awtLock();
/*      */     try {
/*  615 */       return XlibWrapper.XGetInputFocus(XToolkit.getDisplay());
/*      */     } finally {
/*  617 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void xSetVisible(boolean paramBoolean) {
/*  622 */     if (log.isLoggable(500)) log.fine("Setting visible on " + this + " to " + paramBoolean);
/*  623 */     XToolkit.awtLock();
/*      */     try {
/*  625 */       this.visible = paramBoolean;
/*  626 */       if (paramBoolean) {
/*  627 */         XlibWrapper.XMapWindow(XToolkit.getDisplay(), getWindow());
/*      */       }
/*      */       else {
/*  630 */         XlibWrapper.XUnmapWindow(XToolkit.getDisplay(), getWindow());
/*      */       }
/*  632 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*      */     } finally {
/*  634 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isMapped() {
/*  639 */     return this.mapped;
/*      */   }
/*      */ 
/*      */   void updateWMName() {
/*  643 */     String str = getWMName();
/*  644 */     XToolkit.awtLock();
/*      */     try {
/*  646 */       if (str == null) {
/*  647 */         str = " ";
/*      */       }
/*  649 */       XAtom localXAtom1 = XAtom.get(39L);
/*  650 */       localXAtom1.setProperty(getWindow(), str);
/*  651 */       XAtom localXAtom2 = XAtom.get("_NET_WM_NAME");
/*  652 */       localXAtom2.setPropertyUTF8(getWindow(), str);
/*      */     } finally {
/*  654 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*  658 */   void setWMClass(String[] paramArrayOfString) { if (paramArrayOfString.length != 2) {
/*  659 */       throw new IllegalArgumentException("WM_CLASS_NAME consists of exactly two strings");
/*      */     }
/*  661 */     XToolkit.awtLock();
/*      */     try {
/*  663 */       XAtom localXAtom = XAtom.get(67L);
/*  664 */       localXAtom.setProperty8(getWindow(), paramArrayOfString[0] + '\000' + paramArrayOfString[1]);
/*      */     } finally {
/*  666 */       XToolkit.awtUnlock();
/*      */     } }
/*      */ 
/*      */   boolean isVisible()
/*      */   {
/*  671 */     return this.visible;
/*      */   }
/*      */ 
/*      */   static long getScreenOfWindow(long paramLong) {
/*  675 */     XToolkit.awtLock();
/*      */     try {
/*  677 */       return XlibWrapper.getScreenOfWindow(XToolkit.getDisplay(), paramLong);
/*      */     } finally {
/*  679 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*  683 */   long getScreenNumber() { XToolkit.awtLock();
/*      */     try {
/*  685 */       return XlibWrapper.XScreenNumberOfScreen(getScreen());
/*      */     } finally {
/*  687 */       XToolkit.awtUnlock();
/*      */     } }
/*      */ 
/*      */   long getScreen()
/*      */   {
/*  692 */     if (this.screen == -1L) {
/*  693 */       this.screen = getScreenOfWindow(this.window);
/*      */     }
/*  695 */     return this.screen;
/*      */   }
/*      */ 
/*      */   public void xSetBounds(Rectangle paramRectangle) {
/*  699 */     xSetBounds(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public void xSetBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  703 */     if (getWindow() == 0L) {
/*  704 */       insLog.warning("Attempt to resize uncreated window");
/*  705 */       throw new IllegalStateException("Attempt to resize uncreated window");
/*      */     }
/*  707 */     insLog.fine("Setting bounds on " + this + " to (" + paramInt1 + ", " + paramInt2 + "), " + paramInt3 + "x" + paramInt4);
/*  708 */     paramInt3 = Math.max(1, paramInt3);
/*  709 */     paramInt4 = Math.max(1, paramInt4);
/*  710 */     XToolkit.awtLock();
/*      */     try {
/*  712 */       XlibWrapper.XMoveResizeWindow(XToolkit.getDisplay(), getWindow(), paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } finally {
/*  714 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static Point toOtherWindow(long paramLong1, long paramLong2, int paramInt1, int paramInt2)
/*      */   {
/*  725 */     Point localPoint = new Point(0, 0);
/*      */ 
/*  729 */     XBaseWindow localXBaseWindow1 = XToolkit.windowToXWindow(paramLong1);
/*  730 */     XBaseWindow localXBaseWindow2 = XToolkit.windowToXWindow(paramLong2);
/*      */ 
/*  732 */     if ((localXBaseWindow1 != null) && (localXBaseWindow2 != null))
/*      */     {
/*  734 */       localPoint.x = (paramInt1 + localXBaseWindow1.getAbsoluteX() - localXBaseWindow2.getAbsoluteX());
/*  735 */       localPoint.y = (paramInt2 + localXBaseWindow1.getAbsoluteY() - localXBaseWindow2.getAbsoluteY());
/*  736 */     } else if ((localXBaseWindow2 != null) && (XlibUtil.isRoot(paramLong1, localXBaseWindow2.getScreenNumber())))
/*      */     {
/*  738 */       localPoint.x = (paramInt1 - localXBaseWindow2.getAbsoluteX());
/*  739 */       localPoint.y = (paramInt2 - localXBaseWindow2.getAbsoluteY());
/*  740 */     } else if ((localXBaseWindow1 != null) && (XlibUtil.isRoot(paramLong2, localXBaseWindow1.getScreenNumber())))
/*      */     {
/*  742 */       localPoint.x = (paramInt1 + localXBaseWindow1.getAbsoluteX());
/*  743 */       localPoint.y = (paramInt2 + localXBaseWindow1.getAbsoluteY());
/*      */     } else {
/*  745 */       localPoint = XlibUtil.translateCoordinates(paramLong1, paramLong2, new Point(paramInt1, paramInt2));
/*      */     }
/*  747 */     return localPoint;
/*      */   }
/*      */ 
/*      */   Rectangle toGlobal(Rectangle paramRectangle)
/*      */   {
/*  754 */     Point localPoint = toGlobal(paramRectangle.getLocation());
/*  755 */     Rectangle localRectangle = new Rectangle(paramRectangle);
/*  756 */     if (localPoint != null) {
/*  757 */       localRectangle.setLocation(localPoint);
/*      */     }
/*  759 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   Point toGlobal(Point paramPoint) {
/*  763 */     Point localPoint = toGlobal(paramPoint.x, paramPoint.y);
/*  764 */     if (localPoint != null) {
/*  765 */       return localPoint;
/*      */     }
/*  767 */     return new Point(paramPoint);
/*      */   }
/*      */ 
/*      */   Point toGlobal(int paramInt1, int paramInt2) {
/*  773 */     XToolkit.awtLock();
/*      */     long l;
/*      */     try {
/*  775 */       l = XlibWrapper.RootWindow(XToolkit.getDisplay(), getScreenNumber());
/*      */     }
/*      */     finally {
/*  778 */       XToolkit.awtUnlock();
/*      */     }
/*  780 */     Point localPoint = toOtherWindow(getContentWindow(), l, paramInt1, paramInt2);
/*  781 */     if (localPoint != null) {
/*  782 */       return localPoint;
/*      */     }
/*  784 */     return new Point(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   Point toLocal(Point paramPoint)
/*      */   {
/*  792 */     Point localPoint = toLocal(paramPoint.x, paramPoint.y);
/*  793 */     if (localPoint != null) {
/*  794 */       return localPoint;
/*      */     }
/*  796 */     return new Point(paramPoint);
/*      */   }
/*      */ 
/*      */   Point toLocal(int paramInt1, int paramInt2) {
/*  802 */     XToolkit.awtLock();
/*      */     long l;
/*      */     try {
/*  804 */       l = XlibWrapper.RootWindow(XToolkit.getDisplay(), getScreenNumber());
/*      */     }
/*      */     finally {
/*  807 */       XToolkit.awtUnlock();
/*      */     }
/*  809 */     Point localPoint = toOtherWindow(l, getContentWindow(), paramInt1, paramInt2);
/*  810 */     if (localPoint != null) {
/*  811 */       return localPoint;
/*      */     }
/*  813 */     return new Point(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public boolean grabInput()
/*      */   {
/*  823 */     grabLog.fine("Grab input on {0}", new Object[] { this });
/*      */ 
/*  825 */     XToolkit.awtLock();
/*      */     try {
/*  827 */       if ((XAwtState.getGrabWindow() == this) && (XAwtState.isManualGrab()))
/*      */       {
/*  830 */         grabLog.fine("    Already Grabbed");
/*  831 */         return true;
/*      */       }
/*      */ 
/*  835 */       XBaseWindow localXBaseWindow = XAwtState.getGrabWindow();
/*      */       int i;
/*  847 */       if (!XToolkit.getSunAwtDisableGrab()) {
/*  848 */         i = XlibWrapper.XGrabPointer(XToolkit.getDisplay(), getContentWindow(), 1, 8316, 1, 1, 0L, XWM.isMotif() ? XToolkit.arrowCursor : 0L, 0L);
/*      */ 
/*  853 */         if (i != 0) {
/*  854 */           XlibWrapper.XUngrabPointer(XToolkit.getDisplay(), 0L);
/*  855 */           XAwtState.setGrabWindow(null);
/*  856 */           grabLog.fine("    Grab Failure - mouse");
/*  857 */           return false;
/*      */         }
/*      */ 
/*  860 */         int j = XlibWrapper.XGrabKeyboard(XToolkit.getDisplay(), getContentWindow(), 1, 1, 1, 0L);
/*      */ 
/*  863 */         if (j != 0) {
/*  864 */           XlibWrapper.XUngrabPointer(XToolkit.getDisplay(), 0L);
/*  865 */           XlibWrapper.XUngrabKeyboard(XToolkit.getDisplay(), 0L);
/*  866 */           XAwtState.setGrabWindow(null);
/*  867 */           grabLog.fine("    Grab Failure - keyboard");
/*  868 */           return false;
/*      */         }
/*      */       }
/*  871 */       if (localXBaseWindow != null) {
/*  872 */         localXBaseWindow.ungrabInputImpl();
/*      */       }
/*  874 */       XAwtState.setGrabWindow(this);
/*  875 */       grabLog.fine("    Grab - success");
/*  876 */       return 1;
/*      */     } finally {
/*  878 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   static void ungrabInput() {
/*  883 */     XToolkit.awtLock();
/*      */     try {
/*  885 */       XBaseWindow localXBaseWindow = XAwtState.getGrabWindow();
/*  886 */       grabLog.fine("UnGrab input on {0}", new Object[] { localXBaseWindow });
/*  887 */       if (localXBaseWindow != null) {
/*  888 */         localXBaseWindow.ungrabInputImpl();
/*  889 */         if (!XToolkit.getSunAwtDisableGrab()) {
/*  890 */           XlibWrapper.XUngrabPointer(XToolkit.getDisplay(), 0L);
/*  891 */           XlibWrapper.XUngrabKeyboard(XToolkit.getDisplay(), 0L);
/*      */         }
/*  893 */         XAwtState.setGrabWindow(null);
/*      */ 
/*  896 */         XlibWrapper.XFlush(XToolkit.getDisplay());
/*      */       }
/*      */     } finally {
/*  899 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   void ungrabInputImpl()
/*      */   {
/*      */   }
/*      */ 
/*      */   static void checkSecurity() {
/*  908 */     if ((XToolkit.isSecurityWarningEnabled()) && (XToolkit.isToolkitThread())) {
/*  909 */       StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
/*  910 */       log.warning(arrayOfStackTraceElement[1] + ": Security violation: calling user code on toolkit thread");
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set<Long> getChildren() {
/*  915 */     synchronized (getStateLock()) {
/*  916 */       return new HashSet(this.children);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleMapNotifyEvent(XEvent paramXEvent)
/*      */   {
/*  922 */     this.mapped = true;
/*      */   }
/*      */   public void handleUnmapNotifyEvent(XEvent paramXEvent) {
/*  925 */     this.mapped = false;
/*      */   }
/*      */   public void handleReparentNotifyEvent(XEvent paramXEvent) {
/*  928 */     if (eventLog.isLoggable(400)) {
/*  929 */       XReparentEvent localXReparentEvent = paramXEvent.get_xreparent();
/*  930 */       eventLog.finer(localXReparentEvent.toString());
/*      */     }
/*      */   }
/*      */ 
/*  934 */   public void handlePropertyNotify(XEvent paramXEvent) { XPropertyEvent localXPropertyEvent = paramXEvent.get_xproperty();
/*  935 */     if (XPropertyCache.isCachingSupported()) {
/*  936 */       XPropertyCache.clearCache(this.window, XAtom.get(localXPropertyEvent.get_atom()));
/*      */     }
/*  938 */     if (eventLog.isLoggable(400))
/*  939 */       eventLog.finer("{0}", new Object[] { localXPropertyEvent });
/*      */   }
/*      */ 
/*      */   public void handleDestroyNotify(XEvent paramXEvent)
/*      */   {
/*  944 */     XAnyEvent localXAnyEvent = paramXEvent.get_xany();
/*  945 */     if (localXAnyEvent.get_window() == getWindow()) {
/*  946 */       XToolkit.removeFromWinMap(getWindow(), this);
/*  947 */       if (XPropertyCache.isCachingSupported()) {
/*  948 */         XPropertyCache.clearCache(getWindow());
/*      */       }
/*      */     }
/*  951 */     if (localXAnyEvent.get_window() != getWindow())
/*  952 */       synchronized (getStateLock()) {
/*  953 */         this.children.remove(Long.valueOf(localXAnyEvent.get_window()));
/*      */       }
/*      */   }
/*      */ 
/*      */   public void handleCreateNotify(XEvent paramXEvent)
/*      */   {
/*  959 */     XAnyEvent localXAnyEvent = paramXEvent.get_xany();
/*  960 */     if (localXAnyEvent.get_window() != getWindow())
/*  961 */       synchronized (getStateLock()) {
/*  962 */         this.children.add(Long.valueOf(localXAnyEvent.get_window()));
/*      */       }
/*      */   }
/*      */ 
/*      */   public void handleClientMessage(XEvent paramXEvent)
/*      */   {
/*  968 */     if (eventLog.isLoggable(400)) {
/*  969 */       XClientMessageEvent localXClientMessageEvent = paramXEvent.get_xclient();
/*  970 */       eventLog.finer(localXClientMessageEvent.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleVisibilityEvent(XEvent paramXEvent) {
/*      */   }
/*      */ 
/*      */   public void handleKeyPress(XEvent paramXEvent) {
/*      */   }
/*      */ 
/*      */   public void handleKeyRelease(XEvent paramXEvent) {
/*      */   }
/*      */ 
/*      */   public void handleExposeEvent(XEvent paramXEvent) {
/*      */   }
/*      */ 
/*      */   public void handleButtonPressRelease(XEvent paramXEvent) {
/*  987 */     XButtonEvent localXButtonEvent = paramXEvent.get_xbutton();
/*      */ 
/*  993 */     if (localXButtonEvent.get_button() > 20) {
/*  994 */       return;
/*      */     }
/*  996 */     int i = 0;
/*  997 */     int j = ((SunToolkit)Toolkit.getDefaultToolkit()).getNumberOfButtons();
/*  998 */     for (int k = 0; k < j; k++) {
/*  999 */       i |= localXButtonEvent.get_state() & XConstants.buttonsMask[k];
/*      */     }
/* 1001 */     switch (paramXEvent.get_type()) {
/*      */     case 4:
/* 1003 */       if (i == 0)
/* 1004 */         XAwtState.setAutoGrabWindow(this); break;
/*      */     case 5:
/* 1008 */       if (isFullRelease(i, localXButtonEvent.get_button()))
/* 1009 */         XAwtState.setAutoGrabWindow(null); break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleMotionNotify(XEvent paramXEvent) {
/*      */   }
/*      */ 
/*      */   public void handleXCrossingEvent(XEvent paramXEvent) {
/*      */   }
/*      */ 
/* 1019 */   public void handleConfigureNotifyEvent(XEvent paramXEvent) { XConfigureEvent localXConfigureEvent = paramXEvent.get_xconfigure();
/* 1020 */     insLog.finer("Configure, {0}", new Object[] { localXConfigureEvent });
/* 1021 */     this.x = localXConfigureEvent.get_x();
/* 1022 */     this.y = localXConfigureEvent.get_y();
/* 1023 */     this.width = localXConfigureEvent.get_width();
/* 1024 */     this.height = localXConfigureEvent.get_height();
/*      */   }
/*      */ 
/*      */   static boolean isFullRelease(int paramInt1, int paramInt2)
/*      */   {
/* 1030 */     int i = ((SunToolkit)Toolkit.getDefaultToolkit()).getNumberOfButtons();
/*      */ 
/* 1032 */     if ((paramInt2 < 0) || (paramInt2 > i)) {
/* 1033 */       return paramInt1 == 0;
/*      */     }
/* 1035 */     return paramInt1 == XConstants.buttonsMask[(paramInt2 - 1)];
/*      */   }
/*      */ 
/*      */   static boolean isGrabbedEvent(XEvent paramXEvent, XBaseWindow paramXBaseWindow)
/*      */   {
/* 1040 */     switch (paramXEvent.get_type()) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/* 1046 */       return true;
/*      */     case 7:
/*      */     case 8:
/* 1051 */       return paramXBaseWindow instanceof XWindowPeer;
/*      */     }
/* 1053 */     return false;
/*      */   }
/*      */ 
/*      */   static void dispatchToWindow(XEvent paramXEvent)
/*      */   {
/* 1061 */     XBaseWindow localXBaseWindow = XAwtState.getGrabWindow();
/* 1062 */     if ((localXBaseWindow == null) || (!isGrabbedEvent(paramXEvent, localXBaseWindow))) {
/* 1063 */       localXBaseWindow = XToolkit.windowToXWindow(paramXEvent.get_xany().get_window());
/*      */     }
/* 1065 */     if ((localXBaseWindow != null) && (localXBaseWindow.checkInitialised()))
/* 1066 */       localXBaseWindow.dispatchEvent(paramXEvent);
/*      */   }
/*      */ 
/*      */   public void dispatchEvent(XEvent paramXEvent)
/*      */   {
/* 1071 */     if (eventLog.isLoggable(300)) eventLog.finest(paramXEvent.toString());
/* 1072 */     int i = paramXEvent.get_type();
/*      */ 
/* 1074 */     if (isDisposed()) {
/* 1075 */       return;
/*      */     }
/*      */ 
/* 1078 */     switch (i)
/*      */     {
/*      */     case 15:
/* 1081 */       handleVisibilityEvent(paramXEvent);
/* 1082 */       break;
/*      */     case 33:
/* 1084 */       handleClientMessage(paramXEvent);
/* 1085 */       break;
/*      */     case 12:
/*      */     case 13:
/* 1088 */       handleExposeEvent(paramXEvent);
/* 1089 */       break;
/*      */     case 4:
/*      */     case 5:
/* 1092 */       handleButtonPressRelease(paramXEvent);
/* 1093 */       break;
/*      */     case 6:
/* 1096 */       handleMotionNotify(paramXEvent);
/* 1097 */       break;
/*      */     case 2:
/* 1099 */       handleKeyPress(paramXEvent);
/* 1100 */       break;
/*      */     case 3:
/* 1102 */       handleKeyRelease(paramXEvent);
/* 1103 */       break;
/*      */     case 7:
/*      */     case 8:
/* 1106 */       handleXCrossingEvent(paramXEvent);
/* 1107 */       break;
/*      */     case 22:
/* 1109 */       handleConfigureNotifyEvent(paramXEvent);
/* 1110 */       break;
/*      */     case 19:
/* 1112 */       handleMapNotifyEvent(paramXEvent);
/* 1113 */       break;
/*      */     case 18:
/* 1115 */       handleUnmapNotifyEvent(paramXEvent);
/* 1116 */       break;
/*      */     case 21:
/* 1118 */       handleReparentNotifyEvent(paramXEvent);
/* 1119 */       break;
/*      */     case 28:
/* 1121 */       handlePropertyNotify(paramXEvent);
/* 1122 */       break;
/*      */     case 17:
/* 1124 */       handleDestroyNotify(paramXEvent);
/* 1125 */       break;
/*      */     case 16:
/* 1127 */       handleCreateNotify(paramXEvent);
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 14:
/*      */     case 20:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*      */     case 29:
/*      */     case 30:
/*      */     case 31:
/*      */     case 32: }  } 
/* 1132 */   protected boolean isEventDisabled(XEvent paramXEvent) { return false; }
/*      */ 
/*      */   int getX()
/*      */   {
/* 1136 */     return this.x;
/*      */   }
/*      */ 
/*      */   int getY() {
/* 1140 */     return this.y;
/*      */   }
/*      */ 
/*      */   int getWidth() {
/* 1144 */     return this.width;
/*      */   }
/*      */ 
/*      */   int getHeight() {
/* 1148 */     return this.height;
/*      */   }
/*      */ 
/*      */   void setDisposed(boolean paramBoolean) {
/* 1152 */     this.disposed = paramBoolean;
/*      */   }
/*      */ 
/*      */   boolean isDisposed() {
/* 1156 */     return this.disposed;
/*      */   }
/*      */ 
/*      */   public int getAbsoluteX() {
/* 1160 */     XBaseWindow localXBaseWindow = getParentWindow();
/* 1161 */     if (localXBaseWindow != null) {
/* 1162 */       return localXBaseWindow.getAbsoluteX() + getX();
/*      */     }
/*      */ 
/* 1165 */     return getX();
/*      */   }
/*      */ 
/*      */   public int getAbsoluteY()
/*      */   {
/* 1170 */     XBaseWindow localXBaseWindow = getParentWindow();
/* 1171 */     if (localXBaseWindow != null) {
/* 1172 */       return localXBaseWindow.getAbsoluteY() + getY();
/*      */     }
/* 1174 */     return getY();
/*      */   }
/*      */ 
/*      */   public XBaseWindow getParentWindow()
/*      */   {
/* 1179 */     return this.parentWindow;
/*      */   }
/*      */ 
/*      */   public XWindowPeer getToplevelXWindow() {
/* 1183 */     XBaseWindow localXBaseWindow = this;
/* 1184 */     while ((localXBaseWindow != null) && (!(localXBaseWindow instanceof XWindowPeer))) {
/* 1185 */       localXBaseWindow = localXBaseWindow.getParentWindow();
/*      */     }
/* 1187 */     return (XWindowPeer)localXBaseWindow;
/*      */   }
/*      */   public String toString() {
/* 1190 */     return super.toString() + "(" + Long.toString(getWindow(), 16) + ")";
/*      */   }
/*      */ 
/*      */   public boolean contains(int paramInt1, int paramInt2)
/*      */   {
/* 1197 */     return (paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt1 < getWidth()) && (paramInt2 < getHeight());
/*      */   }
/*      */ 
/*      */   public boolean containsGlobal(int paramInt1, int paramInt2)
/*      */   {
/* 1204 */     return (paramInt1 >= getAbsoluteX()) && (paramInt2 >= getAbsoluteY()) && (paramInt1 < getAbsoluteX() + getWidth()) && (paramInt2 < getAbsoluteY() + getHeight());
/*      */   }
/*      */ 
/*      */   static enum InitialiseState
/*      */   {
/*   81 */     INITIALISING, 
/*   82 */     NOT_INITIALISED, 
/*   83 */     INITIALISED, 
/*   84 */     FAILED_INITIALISATION;
/*      */   }
/*      */ 
/*      */   static class StateLock
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XBaseWindow
 * JD-Core Version:    0.6.2
 */