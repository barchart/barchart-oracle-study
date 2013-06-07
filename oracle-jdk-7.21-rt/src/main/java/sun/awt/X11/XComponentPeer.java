/*      */ package sun.awt.X11;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.AWTException;
/*      */ import java.awt.BufferCapabilities;
/*      */ import java.awt.BufferCapabilities.FlipContents;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.dnd.DropTarget;
/*      */ import java.awt.dnd.peer.DropTargetPeer;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.InputEvent;
/*      */ import java.awt.event.InputMethodEvent;
/*      */ import java.awt.event.InvocationEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.PaintEvent;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.ImageProducer;
/*      */ import java.awt.image.VolatileImage;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.awt.peer.ContainerPeer;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.Collection;
/*      */ import java.util.Set;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.CausedFocusEvent.Cause;
/*      */ import sun.awt.GlobalCursorManager;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.X11GraphicsConfig;
/*      */ import sun.awt.event.IgnorePaintEvent;
/*      */ import sun.awt.image.SunVolatileImage;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.font.FontDesignMetrics;
/*      */ import sun.java2d.BackBufferCapsProvider;
/*      */ import sun.java2d.pipe.Region;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class XComponentPeer extends XWindow
/*      */   implements ComponentPeer, DropTargetPeer, BackBufferCapsProvider
/*      */ {
/*   81 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XComponentPeer");
/*   82 */   private static final PlatformLogger buffersLog = PlatformLogger.getLogger("sun.awt.X11.XComponentPeer.multibuffer");
/*   83 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.X11.focus.XComponentPeer");
/*   84 */   private static final PlatformLogger fontLog = PlatformLogger.getLogger("sun.awt.X11.font.XComponentPeer");
/*   85 */   private static final PlatformLogger enableLog = PlatformLogger.getLogger("sun.awt.X11.enable.XComponentPeer");
/*   86 */   private static final PlatformLogger shapeLog = PlatformLogger.getLogger("sun.awt.X11.shape.XComponentPeer");
/*      */ 
/*   88 */   boolean paintPending = false;
/*   89 */   boolean isLayouting = false;
/*      */   boolean enabled;
/*      */   protected int boundsOperation;
/*      */   Color foreground;
/*      */   Color background;
/*      */   Color darkShadow;
/*      */   Color lightShadow;
/*      */   Color selectColor;
/*      */   Font font;
/*  106 */   private long backBuffer = 0L;
/*  107 */   private VolatileImage xBackBuffer = null;
/*      */   static Color[] systemColors;
/*  222 */   boolean bHasFocus = false;
/*      */   private static Class seClass;
/*      */   private static Constructor seCtor;
/*      */   static final int BACKGROUND_COLOR = 0;
/*      */   static final int HIGHLIGHT_COLOR = 1;
/*      */   static final int SHADOW_COLOR = 2;
/*      */   static final int FOREGROUND_COLOR = 3;
/*      */   private BufferCapabilities backBufferCaps;
/*      */ 
/*      */   XComponentPeer()
/*      */   {
/*      */   }
/*      */ 
/*      */   XComponentPeer(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  115 */     super(paramXCreateWindowParams);
/*      */   }
/*      */ 
/*      */   XComponentPeer(Component paramComponent, long paramLong, Rectangle paramRectangle) {
/*  119 */     super(paramComponent, paramLong, paramRectangle);
/*      */   }
/*      */ 
/*      */   XComponentPeer(Component paramComponent)
/*      */   {
/*  126 */     super(paramComponent);
/*      */   }
/*      */ 
/*      */   void preInit(XCreateWindowParams paramXCreateWindowParams)
/*      */   {
/*  131 */     super.preInit(paramXCreateWindowParams);
/*  132 */     this.boundsOperation = 3;
/*      */   }
/*      */   void postInit(XCreateWindowParams paramXCreateWindowParams) {
/*  135 */     super.postInit(paramXCreateWindowParams);
/*      */ 
/*  140 */     pSetCursor(this.target.getCursor());
/*      */ 
/*  142 */     this.foreground = this.target.getForeground();
/*  143 */     this.background = this.target.getBackground();
/*  144 */     this.font = this.target.getFont();
/*      */ 
/*  146 */     if (isInitialReshape()) {
/*  147 */       localObject = this.target.getBounds();
/*  148 */       reshape(((Rectangle)localObject).x, ((Rectangle)localObject).y, ((Rectangle)localObject).width, ((Rectangle)localObject).height);
/*      */     }
/*      */ 
/*  151 */     this.enabled = this.target.isEnabled();
/*      */ 
/*  155 */     Object localObject = this.target;
/*  156 */     while ((localObject != null) && (!(localObject instanceof Window))) {
/*  157 */       localObject = ((Component)localObject).getParent();
/*  158 */       if ((localObject != null) && (!((Component)localObject).isEnabled()) && (!((Component)localObject).isLightweight())) {
/*  159 */         setEnabled(false);
/*      */       }
/*      */     }
/*      */ 
/*  163 */     enableLog.fine("Initial enable state: {0}", new Object[] { Boolean.valueOf(this.enabled) });
/*      */ 
/*  165 */     if (this.target.isVisible())
/*  166 */       setVisible(true);
/*      */   }
/*      */ 
/*      */   protected boolean isInitialReshape()
/*      */   {
/*  171 */     return true;
/*      */   }
/*      */ 
/*      */   public void reparent(ContainerPeer paramContainerPeer) {
/*  175 */     XComponentPeer localXComponentPeer = (XComponentPeer)paramContainerPeer;
/*  176 */     XToolkit.awtLock();
/*      */     try {
/*  178 */       XlibWrapper.XReparentWindow(XToolkit.getDisplay(), getWindow(), localXComponentPeer.getContentWindow(), this.x, this.y);
/*  179 */       this.parentWindow = localXComponentPeer;
/*      */     } finally {
/*  181 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*  185 */   public boolean isReparentSupported() { return System.getProperty("sun.awt.X11.XComponentPeer.reparentNotSupported", "false").equals("false"); }
/*      */ 
/*      */   public boolean isObscured()
/*      */   {
/*  189 */     Object localObject = (this.target instanceof Container) ? (Container)this.target : this.target.getParent();
/*      */ 
/*  192 */     if (localObject == null)
/*  193 */       return true;
/*      */     Container localContainer;
/*  197 */     while ((localContainer = ((Container)localObject).getParent()) != null) {
/*  198 */       localObject = localContainer;
/*      */     }
/*      */ 
/*  201 */     if ((localObject instanceof Window)) {
/*  202 */       XWindowPeer localXWindowPeer = (XWindowPeer)((Container)localObject).getPeer();
/*  203 */       if (localXWindowPeer != null) {
/*  204 */         return localXWindowPeer.winAttr.visibilityState != XWindowAttributesData.AWT_UNOBSCURED;
/*      */       }
/*      */     }
/*      */ 
/*  208 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean canDetermineObscurity() {
/*  212 */     return true;
/*      */   }
/*      */ 
/*      */   public final boolean hasFocus()
/*      */   {
/*  229 */     return this.bHasFocus;
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent paramFocusEvent)
/*      */   {
/*  236 */     focusLog.fine("{0}", new Object[] { paramFocusEvent });
/*  237 */     this.bHasFocus = true;
/*      */   }
/*      */ 
/*      */   public void focusLost(FocusEvent paramFocusEvent)
/*      */   {
/*  244 */     focusLog.fine("{0}", new Object[] { paramFocusEvent });
/*  245 */     this.bHasFocus = false;
/*      */   }
/*      */ 
/*      */   public boolean isFocusable()
/*      */   {
/*  250 */     return false;
/*      */   }
/*      */ 
/*      */   static final AWTEvent wrapInSequenced(AWTEvent paramAWTEvent)
/*      */   {
/*      */     try
/*      */     {
/*  258 */       if (seClass == null) {
/*  259 */         seClass = Class.forName("java.awt.SequencedEvent");
/*      */       }
/*      */ 
/*  262 */       if (seCtor == null) {
/*  263 */         seCtor = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*      */           public Object run() throws Exception {
/*  265 */             Constructor localConstructor = XComponentPeer.seClass.getConstructor(new Class[] { AWTEvent.class });
/*  266 */             localConstructor.setAccessible(true);
/*  267 */             return localConstructor;
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*  272 */       return (AWTEvent)seCtor.newInstance(new Object[] { paramAWTEvent });
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException) {
/*  275 */       throw new NoClassDefFoundError("java.awt.SequencedEvent.");
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  278 */       throw new NoClassDefFoundError("java.awt.SequencedEvent.");
/*      */     }
/*      */     catch (InstantiationException localInstantiationException) {
/*  281 */       if (!$assertionsDisabled) throw new AssertionError(); 
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException)
/*      */     {
/*  284 */       if (!$assertionsDisabled) throw new AssertionError(); 
/*      */     }
/*      */     catch (InvocationTargetException localInvocationTargetException)
/*      */     {
/*  287 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */     }
/*      */ 
/*  290 */     return null;
/*      */   }
/*      */ 
/*      */   public final boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*      */   {
/*  298 */     if (XKeyboardFocusManagerPeer.processSynchronousLightweightTransfer(this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong))
/*      */     {
/*  302 */       return true;
/*      */     }
/*      */ 
/*  305 */     int i = XKeyboardFocusManagerPeer.shouldNativelyFocusHeavyweight(this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong, paramCause);
/*      */ 
/*  310 */     switch (i) {
/*      */     case 0:
/*  312 */       return false;
/*      */     case 2:
/*  316 */       if (focusLog.isLoggable(400)) focusLog.finer("Proceeding with request to " + paramComponent + " in " + this.target);
/*      */ 
/*  326 */       Window localWindow = SunToolkit.getContainingWindow(this.target);
/*  327 */       if (localWindow == null) {
/*  328 */         return rejectFocusRequestHelper("WARNING: Parent window is null");
/*      */       }
/*  330 */       XWindowPeer localXWindowPeer = (XWindowPeer)localWindow.getPeer();
/*  331 */       if (localXWindowPeer == null) {
/*  332 */         return rejectFocusRequestHelper("WARNING: Parent window's peer is null");
/*      */       }
/*      */ 
/*  339 */       boolean bool = localXWindowPeer.requestWindowFocus(null);
/*      */ 
/*  341 */       if (focusLog.isLoggable(400)) focusLog.finer("Requested window focus: " + bool);
/*      */ 
/*  344 */       if ((!bool) || (!localWindow.isFocused())) {
/*  345 */         return rejectFocusRequestHelper("Waiting for asynchronous processing of the request");
/*      */       }
/*  347 */       return XKeyboardFocusManagerPeer.deliverFocus(paramComponent, this.target, paramBoolean1, paramBoolean2, paramLong, paramCause);
/*      */     case 1:
/*  355 */       return true;
/*      */     }
/*  357 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean rejectFocusRequestHelper(String paramString) {
/*  361 */     if (focusLog.isLoggable(400)) focusLog.finer(paramString);
/*  362 */     XKeyboardFocusManagerPeer.removeLastFocusRequest(this.target);
/*  363 */     return false;
/*      */   }
/*      */ 
/*      */   void handleJavaFocusEvent(AWTEvent paramAWTEvent) {
/*  367 */     if (focusLog.isLoggable(400)) focusLog.finer(paramAWTEvent.toString());
/*  368 */     if (paramAWTEvent.getID() == 1004)
/*  369 */       focusGained((FocusEvent)paramAWTEvent);
/*      */     else
/*  371 */       focusLost((FocusEvent)paramAWTEvent);
/*      */   }
/*      */ 
/*      */   void handleJavaWindowFocusEvent(AWTEvent paramAWTEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setVisible(boolean paramBoolean)
/*      */   {
/*  385 */     xSetVisible(paramBoolean);
/*      */   }
/*      */ 
/*      */   public void hide() {
/*  389 */     setVisible(false);
/*      */   }
/*      */ 
/*      */   public void setEnabled(boolean paramBoolean)
/*      */   {
/*  397 */     enableLog.fine("{0}ing {1}", new Object[] { paramBoolean ? "Enabl" : "Disabl", this });
/*  398 */     int i = this.enabled != paramBoolean ? 1 : 0;
/*  399 */     this.enabled = paramBoolean;
/*  400 */     if ((this.target instanceof Container)) {
/*  401 */       Component[] arrayOfComponent = ((Container)this.target).getComponents();
/*  402 */       for (int j = 0; j < arrayOfComponent.length; j++) {
/*  403 */         boolean bool = arrayOfComponent[j].isEnabled();
/*  404 */         ComponentPeer localComponentPeer = arrayOfComponent[j].getPeer();
/*  405 */         if (localComponentPeer != null) {
/*  406 */           localComponentPeer.setEnabled((paramBoolean) && (bool));
/*      */         }
/*      */       }
/*      */     }
/*  410 */     if (i != 0)
/*  411 */       repaint();
/*      */   }
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  419 */     return this.enabled;
/*      */   }
/*      */ 
/*      */   public void enable()
/*      */   {
/*  425 */     setEnabled(true);
/*      */   }
/*      */ 
/*      */   public void disable() {
/*  429 */     setEnabled(false);
/*      */   }
/*      */   public void paint(Graphics paramGraphics) {
/*      */   }
/*      */ 
/*      */   public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  435 */     repaint();
/*      */   }
/*      */ 
/*      */   public Graphics getGraphics()
/*      */   {
/*  440 */     return getGraphics(this.surfaceData, getPeerForeground(), getPeerBackground(), getPeerFont());
/*      */   }
/*      */ 
/*      */   public void print(Graphics paramGraphics)
/*      */   {
/*  447 */     paramGraphics.setColor(this.target.getBackground());
/*  448 */     paramGraphics.fillRect(0, 0, this.target.getWidth(), this.target.getHeight());
/*  449 */     paramGraphics.setColor(this.target.getForeground());
/*      */ 
/*  451 */     paint(paramGraphics);
/*      */ 
/*  453 */     this.target.print(paramGraphics);
/*      */   }
/*      */ 
/*      */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
/*  457 */     this.x = paramInt1;
/*  458 */     this.y = paramInt2;
/*  459 */     this.width = paramInt3;
/*  460 */     this.height = paramInt4;
/*  461 */     xSetBounds(paramInt1, paramInt2, paramInt3, paramInt4);
/*  462 */     validateSurface();
/*  463 */     layout();
/*      */   }
/*      */ 
/*      */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  467 */     setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3);
/*      */   }
/*      */ 
/*      */   public void coalescePaintEvent(PaintEvent paramPaintEvent) {
/*  471 */     Rectangle localRectangle = paramPaintEvent.getUpdateRect();
/*  472 */     if (!(paramPaintEvent instanceof IgnorePaintEvent)) {
/*  473 */       this.paintArea.add(localRectangle, paramPaintEvent.getID());
/*      */     }
/*      */ 
/*  476 */     switch (paramPaintEvent.getID()) {
/*      */     case 801:
/*  478 */       if (log.isLoggable(400)) {
/*  479 */         log.finer("XCP coalescePaintEvent : UPDATE : add : x = " + localRectangle.x + ", y = " + localRectangle.y + ", width = " + localRectangle.width + ",height = " + localRectangle.height);
/*      */       }
/*      */ 
/*  482 */       return;
/*      */     case 800:
/*  484 */       if (log.isLoggable(400)) {
/*  485 */         log.finer("XCP coalescePaintEvent : PAINT : add : x = " + localRectangle.x + ", y = " + localRectangle.y + ", width = " + localRectangle.width + ",height = " + localRectangle.height);
/*      */       }
/*      */ 
/*  488 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   XWindowPeer getParentTopLevel()
/*      */   {
/*  494 */     AWTAccessor.ComponentAccessor localComponentAccessor = AWTAccessor.getComponentAccessor();
/*  495 */     Container localContainer = (this.target instanceof Container) ? (Container)this.target : localComponentAccessor.getParent(this.target);
/*      */ 
/*  497 */     while ((localContainer != null) && (!(localContainer instanceof Window))) {
/*  498 */       localContainer = localComponentAccessor.getParent(localContainer);
/*      */     }
/*  500 */     if (localContainer != null) {
/*  501 */       return (XWindowPeer)localComponentAccessor.getPeer(localContainer);
/*      */     }
/*  503 */     return null;
/*      */   }
/*      */ 
/*      */   void handleJavaMouseEvent(MouseEvent paramMouseEvent)
/*      */   {
/*  509 */     switch (paramMouseEvent.getID()) {
/*      */     case 501:
/*  511 */       if ((this.target == paramMouseEvent.getSource()) && (!this.target.isFocusOwner()) && (XKeyboardFocusManagerPeer.shouldFocusOnClick(this.target)))
/*      */       {
/*  515 */         XWindowPeer localXWindowPeer = getParentTopLevel();
/*  516 */         Window localWindow = (Window)localXWindowPeer.getTarget();
/*      */ 
/*  527 */         XKeyboardFocusManagerPeer.requestFocusFor(this.target, CausedFocusEvent.Cause.MOUSE_EVENT);
/*      */       }
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleJavaKeyEvent(KeyEvent paramKeyEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   void handleJavaMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   void handleJavaInputMethodEvent(InputMethodEvent paramInputMethodEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   void handleF10JavaKeyEvent(KeyEvent paramKeyEvent)
/*      */   {
/*  547 */     if ((paramKeyEvent.getID() == 401) && (paramKeyEvent.getKeyCode() == 121)) {
/*  548 */       XWindowPeer localXWindowPeer = getToplevelXWindow();
/*  549 */       if ((localXWindowPeer instanceof XFramePeer)) {
/*  550 */         XMenuBarPeer localXMenuBarPeer = ((XFramePeer)localXWindowPeer).getMenubarPeer();
/*  551 */         if (localXMenuBarPeer != null)
/*  552 */           localXMenuBarPeer.handleF10KeyPress(paramKeyEvent);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  559 */     if (((paramAWTEvent instanceof InputEvent)) && (!((InputEvent)paramAWTEvent).isConsumed()) && (this.target.isEnabled())) {
/*  560 */       if ((paramAWTEvent instanceof MouseEvent)) {
/*  561 */         if ((paramAWTEvent instanceof MouseWheelEvent)) {
/*  562 */           handleJavaMouseWheelEvent((MouseWheelEvent)paramAWTEvent);
/*      */         }
/*      */         else
/*  565 */           handleJavaMouseEvent((MouseEvent)paramAWTEvent);
/*      */       }
/*  567 */       else if ((paramAWTEvent instanceof KeyEvent)) {
/*  568 */         handleF10JavaKeyEvent((KeyEvent)paramAWTEvent);
/*  569 */         handleJavaKeyEvent((KeyEvent)paramAWTEvent);
/*      */       }
/*      */     }
/*  572 */     else if (((paramAWTEvent instanceof KeyEvent)) && (!((InputEvent)paramAWTEvent).isConsumed()))
/*      */     {
/*  574 */       handleF10JavaKeyEvent((KeyEvent)paramAWTEvent);
/*      */     }
/*  576 */     else if ((paramAWTEvent instanceof InputMethodEvent)) {
/*  577 */       handleJavaInputMethodEvent((InputMethodEvent)paramAWTEvent);
/*      */     }
/*      */ 
/*  580 */     int i = paramAWTEvent.getID();
/*      */ 
/*  582 */     switch (i)
/*      */     {
/*      */     case 800:
/*  585 */       this.paintPending = false;
/*      */     case 801:
/*  590 */       if ((!this.isLayouting) && (!this.paintPending)) {
/*  591 */         this.paintArea.paint(this.target, false);
/*      */       }
/*  593 */       return;
/*      */     case 1004:
/*      */     case 1005:
/*  596 */       handleJavaFocusEvent(paramAWTEvent);
/*  597 */       break;
/*      */     case 207:
/*      */     case 208:
/*  600 */       handleJavaWindowFocusEvent(paramAWTEvent);
/*  601 */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleButtonPressRelease(XEvent paramXEvent)
/*      */   {
/*  617 */     if (paramXEvent.get_type() == 4) {
/*  618 */       final XWindowPeer localXWindowPeer = getParentTopLevel();
/*  619 */       Window localWindow = (Window)localXWindowPeer.getTarget();
/*  620 */       if ((localXWindowPeer.isFocusableWindow()) && (localXWindowPeer.isSimpleWindow()) && (XKeyboardFocusManagerPeer.getCurrentNativeFocusedWindow() != localWindow))
/*      */       {
/*  623 */         postEvent(new InvocationEvent(localWindow, new Runnable()
/*      */         {
/*      */           public void run()
/*      */           {
/*  627 */             localXWindowPeer.requestXFocus();
/*      */           }
/*      */         }));
/*      */       }
/*      */     }
/*  632 */     super.handleButtonPressRelease(paramXEvent);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize() {
/*  636 */     return this.target.getSize();
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize() {
/*  640 */     return getMinimumSize();
/*      */   }
/*      */   public void layout() {
/*      */   }
/*      */ 
/*      */   public Toolkit getToolkit() {
/*  646 */     return Toolkit.getDefaultToolkit();
/*      */   }
/*      */ 
/*      */   void updateMotifColors(Color paramColor) {
/*  650 */     int i = paramColor.getRed();
/*  651 */     int j = paramColor.getGreen();
/*  652 */     int k = paramColor.getBlue();
/*      */ 
/*  654 */     this.darkShadow = new Color(MotifColorUtilities.calculateBottomShadowFromBackground(i, j, k));
/*  655 */     this.lightShadow = new Color(MotifColorUtilities.calculateTopShadowFromBackground(i, j, k));
/*  656 */     this.selectColor = new Color(MotifColorUtilities.calculateSelectFromBackground(i, j, k));
/*      */   }
/*      */ 
/*      */   public void drawMotif3DRect(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
/*      */   {
/*  668 */     paramGraphics.setColor(paramBoolean ? this.darkShadow : this.lightShadow);
/*  669 */     paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2);
/*  670 */     paramGraphics.drawLine(paramInt1, paramInt2 + paramInt4, paramInt1, paramInt2);
/*      */ 
/*  672 */     paramGraphics.setColor(paramBoolean ? this.lightShadow : this.darkShadow);
/*  673 */     paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
/*  674 */     paramGraphics.drawLine(paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + 1);
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor) {
/*  678 */     if (log.isLoggable(500)) log.fine("Set background to " + paramColor);
/*  679 */     synchronized (getStateLock()) {
/*  680 */       this.background = paramColor;
/*      */     }
/*  682 */     super.setBackground(paramColor);
/*  683 */     repaint();
/*      */   }
/*      */ 
/*      */   public void setForeground(Color paramColor) {
/*  687 */     if (log.isLoggable(500)) log.fine("Set foreground to " + paramColor);
/*  688 */     synchronized (getStateLock()) {
/*  689 */       this.foreground = paramColor;
/*      */     }
/*  691 */     repaint();
/*      */   }
/*      */ 
/*      */   public FontMetrics getFontMetrics(Font paramFont)
/*      */   {
/*  706 */     if (fontLog.isLoggable(500)) fontLog.fine("Getting font metrics for " + paramFont);
/*  707 */     return FontDesignMetrics.getMetrics(paramFont);
/*      */   }
/*      */ 
/*      */   public void setFont(Font paramFont) {
/*  711 */     synchronized (getStateLock()) {
/*  712 */       if (paramFont == null) {
/*  713 */         paramFont = XWindow.getDefaultFont();
/*      */       }
/*  715 */       this.font = paramFont;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Font getFont()
/*      */   {
/*  725 */     return this.font;
/*      */   }
/*      */ 
/*      */   public void updateCursorImmediately() {
/*  729 */     XGlobalCursorManager.getCursorManager().updateCursorImmediately();
/*      */   }
/*      */ 
/*      */   public final void pSetCursor(Cursor paramCursor) {
/*  733 */     pSetCursor(paramCursor, true);
/*      */   }
/*      */ 
/*      */   public void pSetCursor(Cursor paramCursor, boolean paramBoolean)
/*      */   {
/*  747 */     XToolkit.awtLock();
/*      */     try {
/*  749 */       long l1 = XGlobalCursorManager.getCursor(paramCursor);
/*      */ 
/*  751 */       XSetWindowAttributes localXSetWindowAttributes = new XSetWindowAttributes();
/*  752 */       localXSetWindowAttributes.set_cursor(l1);
/*      */ 
/*  754 */       long l2 = 16384L;
/*      */ 
/*  756 */       XlibWrapper.XChangeWindowAttributes(XToolkit.getDisplay(), getWindow(), l2, localXSetWindowAttributes.pData);
/*  757 */       XlibWrapper.XFlush(XToolkit.getDisplay());
/*  758 */       localXSetWindowAttributes.dispose();
/*      */     } finally {
/*  760 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Image createImage(ImageProducer paramImageProducer) {
/*  765 */     return new ToolkitImage(paramImageProducer);
/*      */   }
/*      */ 
/*      */   public Image createImage(int paramInt1, int paramInt2) {
/*  769 */     return this.graphicsConfig.createAcceleratedImage(this.target, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public VolatileImage createVolatileImage(int paramInt1, int paramInt2) {
/*  773 */     return new SunVolatileImage(this.target, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
/*  777 */     return getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
/*  781 */     return getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public Dimension preferredSize() {
/*  785 */     return getPreferredSize();
/*      */   }
/*      */ 
/*      */   public Dimension minimumSize() {
/*  789 */     return getMinimumSize();
/*      */   }
/*      */ 
/*      */   public Insets getInsets() {
/*  793 */     return new Insets(0, 0, 0, 0);
/*      */   }
/*      */ 
/*      */   public void beginValidate()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void endValidate()
/*      */   {
/*      */   }
/*      */ 
/*      */   public Insets insets()
/*      */   {
/*  808 */     return getInsets();
/*      */   }
/*      */ 
/*      */   public boolean isPaintPending()
/*      */   {
/*  814 */     return (this.paintPending) && (this.isLayouting);
/*      */   }
/*      */ 
/*      */   public boolean handlesWheelScrolling() {
/*  818 */     return false;
/*      */   }
/*      */ 
/*      */   public void beginLayout()
/*      */   {
/*  823 */     this.isLayouting = true;
/*      */   }
/*      */ 
/*      */   public void endLayout()
/*      */   {
/*  828 */     if ((!this.paintPending) && (!this.paintArea.isEmpty()) && (!AWTAccessor.getComponentAccessor().getIgnoreRepaint(this.target)))
/*      */     {
/*  832 */       postEvent(new PaintEvent(this.target, 800, new Rectangle()));
/*      */     }
/*      */ 
/*  835 */     this.isLayouting = false;
/*      */   }
/*      */ 
/*      */   public Color getWinBackground() {
/*  839 */     return getPeerBackground();
/*      */   }
/*      */ 
/*      */   static int[] getRGBvals(Color paramColor)
/*      */   {
/*  844 */     int[] arrayOfInt = new int[3];
/*      */ 
/*  846 */     arrayOfInt[0] = paramColor.getRed();
/*  847 */     arrayOfInt[1] = paramColor.getGreen();
/*  848 */     arrayOfInt[2] = paramColor.getBlue();
/*      */ 
/*  850 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   public Color[] getGUIcolors()
/*      */   {
/*  859 */     Color[] arrayOfColor = new Color[4];
/*      */ 
/*  861 */     arrayOfColor[0] = getWinBackground();
/*  862 */     if (arrayOfColor[0] == null) {
/*  863 */       arrayOfColor[0] = super.getWinBackground();
/*      */     }
/*  865 */     if (arrayOfColor[0] == null) {
/*  866 */       arrayOfColor[0] = Color.lightGray;
/*      */     }
/*      */ 
/*  869 */     int[] arrayOfInt = getRGBvals(arrayOfColor[0]);
/*      */ 
/*  871 */     float[] arrayOfFloat = Color.RGBtoHSB(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], null);
/*      */ 
/*  873 */     float f4 = arrayOfFloat[0];
/*  874 */     float f5 = arrayOfFloat[1];
/*  875 */     float f1 = arrayOfFloat[2];
/*      */ 
/*  880 */     float f2 = f1 + 0.2F;
/*  881 */     float f3 = f1 - 0.4F;
/*  882 */     if (f2 > 1.0D) {
/*  883 */       if (1.0D - f1 < 0.05D)
/*  884 */         f2 = f3 + 0.25F;
/*      */       else {
/*  886 */         f2 = 1.0F;
/*      */       }
/*      */     }
/*  889 */     else if (f3 < 0.0D) {
/*  890 */       if (f1 - 0.0D < 0.25D) {
/*  891 */         f2 = f1 + 0.75F;
/*  892 */         f3 = f2 - 0.2F;
/*      */       } else {
/*  894 */         f3 = 0.0F;
/*      */       }
/*      */     }
/*      */ 
/*  898 */     arrayOfColor[1] = Color.getHSBColor(f4, f5, f2);
/*  899 */     arrayOfColor[2] = Color.getHSBColor(f4, f5, f3);
/*      */ 
/*  909 */     arrayOfColor[3] = getPeerForeground();
/*  910 */     if (arrayOfColor[3] == null) {
/*  911 */       arrayOfColor[3] = Color.black;
/*      */     }
/*      */ 
/*  925 */     if (!isEnabled()) {
/*  926 */       arrayOfColor[0] = arrayOfColor[0].darker();
/*      */ 
/*  933 */       Color localColor1 = arrayOfColor[0];
/*  934 */       int i = localColor1.getRed() * 30 + localColor1.getGreen() * 59 + localColor1.getBlue() * 11;
/*      */ 
/*  936 */       localColor1 = arrayOfColor[3];
/*  937 */       int j = localColor1.getRed() * 30 + localColor1.getGreen() * 59 + localColor1.getBlue() * 11;
/*      */ 
/*  939 */       float f6 = (float)((j + i) / 51000.0D);
/*      */ 
/*  942 */       Color localColor2 = new Color((int)(localColor1.getRed() * f6), (int)(localColor1.getGreen() * f6), (int)(localColor1.getBlue() * f6));
/*      */ 
/*  946 */       if (localColor2.equals(arrayOfColor[3]))
/*      */       {
/*  948 */         localColor2 = new Color(f6, f6, f6);
/*      */       }
/*  950 */       arrayOfColor[3] = localColor2;
/*      */     }
/*      */ 
/*  955 */     return arrayOfColor;
/*      */   }
/*      */ 
/*      */   static Color[] getSystemColors()
/*      */   {
/*  965 */     if (systemColors == null) {
/*  966 */       systemColors = new Color[4];
/*  967 */       systemColors[0] = SystemColor.window;
/*  968 */       systemColors[1] = SystemColor.controlLtHighlight;
/*  969 */       systemColors[2] = SystemColor.controlShadow;
/*  970 */       systemColors[3] = SystemColor.windowText;
/*      */     }
/*  972 */     return systemColors;
/*      */   }
/*      */ 
/*      */   public void draw3DOval(Graphics paramGraphics, Color[] paramArrayOfColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
/*      */   {
/*  981 */     Color localColor = paramGraphics.getColor();
/*  982 */     paramGraphics.setColor(paramBoolean ? paramArrayOfColor[1] : paramArrayOfColor[2]);
/*  983 */     paramGraphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, 45, 180);
/*  984 */     paramGraphics.setColor(paramBoolean ? paramArrayOfColor[2] : paramArrayOfColor[1]);
/*  985 */     paramGraphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, 225, 180);
/*  986 */     paramGraphics.setColor(localColor);
/*      */   }
/*      */ 
/*      */   public void draw3DRect(Graphics paramGraphics, Color[] paramArrayOfColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
/*      */   {
/*  992 */     Color localColor = paramGraphics.getColor();
/*  993 */     paramGraphics.setColor(paramBoolean ? paramArrayOfColor[1] : paramArrayOfColor[2]);
/*  994 */     paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt2 + paramInt4);
/*  995 */     paramGraphics.drawLine(paramInt1 + 1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
/*  996 */     paramGraphics.setColor(paramBoolean ? paramArrayOfColor[2] : paramArrayOfColor[1]);
/*  997 */     paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
/*  998 */     paramGraphics.drawLine(paramInt1 + paramInt3, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4 - 1);
/*  999 */     paramGraphics.setColor(localColor);
/*      */   }
/*      */ 
/*      */   void draw3DOval(Graphics paramGraphics, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
/*      */   {
/* 1011 */     Color localColor1 = paramGraphics.getColor();
/* 1012 */     Color localColor2 = paramColor.darker();
/* 1013 */     Color localColor3 = paramColor.brighter();
/*      */ 
/* 1015 */     paramGraphics.setColor(paramBoolean ? localColor3 : localColor2);
/* 1016 */     paramGraphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, 45, 180);
/* 1017 */     paramGraphics.setColor(paramBoolean ? localColor2 : localColor3);
/* 1018 */     paramGraphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, 225, 180);
/* 1019 */     paramGraphics.setColor(localColor1);
/*      */   }
/*      */ 
/*      */   void draw3DRect(Graphics paramGraphics, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
/*      */   {
/* 1025 */     Color localColor1 = paramGraphics.getColor();
/* 1026 */     Color localColor2 = paramColor.darker();
/* 1027 */     Color localColor3 = paramColor.brighter();
/*      */ 
/* 1029 */     paramGraphics.setColor(paramBoolean ? localColor3 : localColor2);
/* 1030 */     paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt2 + paramInt4);
/* 1031 */     paramGraphics.drawLine(paramInt1 + 1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
/* 1032 */     paramGraphics.setColor(paramBoolean ? localColor2 : localColor3);
/* 1033 */     paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
/* 1034 */     paramGraphics.drawLine(paramInt1 + paramInt3, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4 - 1);
/* 1035 */     paramGraphics.setColor(localColor1);
/*      */   }
/*      */ 
/*      */   void drawScrollbar(Graphics paramGraphics, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
/*      */   {
/* 1040 */     Color localColor1 = paramGraphics.getColor();
/* 1041 */     double d = (paramInt2 - 2 * (paramInt1 - 1)) / Math.max(1, paramInt4 - paramInt3 + paramInt6);
/* 1042 */     int i = paramInt1 + (int)(d * (paramInt5 - paramInt3));
/* 1043 */     int j = (int)(d * paramInt6);
/* 1044 */     int k = paramInt1 - 4;
/* 1045 */     int[] arrayOfInt1 = new int[3];
/* 1046 */     int[] arrayOfInt2 = new int[3];
/*      */ 
/* 1048 */     if (paramInt2 < 3 * k) {
/* 1049 */       i = j = 0;
/* 1050 */       if (paramInt2 < 2 * k + 2)
/* 1051 */         k = (paramInt2 - 2) / 2;
/*      */     }
/* 1053 */     else if (j < 7)
/*      */     {
/* 1055 */       i = Math.max(0, i - (7 - j >> 1));
/* 1056 */       j = 7;
/*      */     }
/*      */ 
/* 1059 */     int m = paramInt1 / 2;
/* 1060 */     int n = m - k / 2;
/* 1061 */     int i1 = m + k / 2;
/*      */ 
/* 1065 */     Color localColor2 = new Color((int)(paramColor.getRed() * 0.85D), (int)(paramColor.getGreen() * 0.85D), (int)(paramColor.getBlue() * 0.85D));
/*      */ 
/* 1069 */     paramGraphics.setColor(localColor2);
/* 1070 */     if (paramBoolean)
/* 1071 */       paramGraphics.fillRect(0, 0, paramInt2, paramInt1);
/*      */     else {
/* 1073 */       paramGraphics.fillRect(0, 0, paramInt1, paramInt2);
/*      */     }
/*      */ 
/* 1078 */     paramGraphics.setColor(paramColor);
/* 1079 */     if (i > 0) {
/* 1080 */       if (paramBoolean)
/* 1081 */         paramGraphics.fillRect(i, 3, j, paramInt1 - 3);
/*      */       else {
/* 1083 */         paramGraphics.fillRect(3, i, paramInt1 - 3, j);
/*      */       }
/*      */     }
/*      */ 
/* 1087 */     arrayOfInt1[0] = m; arrayOfInt2[0] = 2;
/* 1088 */     arrayOfInt1[1] = n; arrayOfInt2[1] = k;
/* 1089 */     arrayOfInt1[2] = i1; arrayOfInt2[2] = k;
/* 1090 */     if (paramBoolean)
/* 1091 */       paramGraphics.fillPolygon(arrayOfInt2, arrayOfInt1, 3);
/*      */     else {
/* 1093 */       paramGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
/*      */     }
/*      */ 
/* 1096 */     arrayOfInt2[0] = (paramInt2 - 2);
/* 1097 */     arrayOfInt2[1] = (paramInt2 - k);
/* 1098 */     arrayOfInt2[2] = (paramInt2 - k);
/* 1099 */     if (paramBoolean)
/* 1100 */       paramGraphics.fillPolygon(arrayOfInt2, arrayOfInt1, 3);
/*      */     else {
/* 1102 */       paramGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
/*      */     }
/*      */ 
/* 1105 */     localColor2 = paramColor.brighter();
/*      */ 
/* 1108 */     paramGraphics.setColor(localColor2);
/*      */ 
/* 1111 */     if (paramBoolean) {
/* 1112 */       paramGraphics.drawLine(1, paramInt1, paramInt2 - 1, paramInt1);
/* 1113 */       paramGraphics.drawLine(paramInt2 - 1, 1, paramInt2 - 1, paramInt1);
/*      */ 
/* 1116 */       paramGraphics.drawLine(1, m, k, n);
/* 1117 */       paramGraphics.drawLine(paramInt2 - k, n, paramInt2 - k, i1);
/* 1118 */       paramGraphics.drawLine(paramInt2 - k, n, paramInt2 - 2, m);
/*      */     }
/*      */     else {
/* 1121 */       paramGraphics.drawLine(paramInt1, 1, paramInt1, paramInt2 - 1);
/* 1122 */       paramGraphics.drawLine(1, paramInt2 - 1, paramInt1, paramInt2 - 1);
/*      */ 
/* 1125 */       paramGraphics.drawLine(m, 1, n, k);
/* 1126 */       paramGraphics.drawLine(n, paramInt2 - k, i1, paramInt2 - k);
/* 1127 */       paramGraphics.drawLine(n, paramInt2 - k, m, paramInt2 - 2);
/*      */     }
/*      */ 
/* 1131 */     if (i > 0) {
/* 1132 */       if (paramBoolean) {
/* 1133 */         paramGraphics.drawLine(i, 2, i + j, 2);
/* 1134 */         paramGraphics.drawLine(i, 2, i, paramInt1 - 3);
/*      */       } else {
/* 1136 */         paramGraphics.drawLine(2, i, 2, i + j);
/* 1137 */         paramGraphics.drawLine(2, i, paramInt1 - 3, i);
/*      */       }
/*      */     }
/*      */ 
/* 1141 */     Color localColor3 = paramColor.darker();
/*      */ 
/* 1144 */     paramGraphics.setColor(localColor3);
/*      */ 
/* 1147 */     if (paramBoolean) {
/* 1148 */       paramGraphics.drawLine(0, 0, 0, paramInt1);
/* 1149 */       paramGraphics.drawLine(0, 0, paramInt2 - 1, 0);
/*      */ 
/* 1152 */       paramGraphics.drawLine(k, n, k, i1);
/* 1153 */       paramGraphics.drawLine(k, i1, 1, m);
/* 1154 */       paramGraphics.drawLine(paramInt2 - 2, m, paramInt2 - k, i1);
/*      */     }
/*      */     else {
/* 1157 */       paramGraphics.drawLine(0, 0, paramInt1, 0);
/* 1158 */       paramGraphics.drawLine(0, 0, 0, paramInt2 - 1);
/*      */ 
/* 1161 */       paramGraphics.drawLine(n, k, i1, k);
/* 1162 */       paramGraphics.drawLine(i1, k, m, 1);
/* 1163 */       paramGraphics.drawLine(m, paramInt2 - 2, i1, paramInt2 - k);
/*      */     }
/*      */ 
/* 1167 */     if (i > 0) {
/* 1168 */       if (paramBoolean) {
/* 1169 */         paramGraphics.drawLine(i + j, 2, i + j, paramInt1 - 2);
/* 1170 */         paramGraphics.drawLine(i, paramInt1 - 2, i + j, paramInt1 - 2);
/*      */       } else {
/* 1172 */         paramGraphics.drawLine(2, i + j, paramInt1 - 2, i + j);
/* 1173 */         paramGraphics.drawLine(paramInt1 - 2, i, paramInt1 - 2, i + j);
/*      */       }
/*      */     }
/* 1176 */     paramGraphics.setColor(localColor1);
/*      */   }
/*      */ 
/*      */   public void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities)
/*      */     throws AWTException
/*      */   {
/* 1190 */     if (buffersLog.isLoggable(500)) {
/* 1191 */       buffersLog.fine("createBuffers(" + paramInt + ", " + paramBufferCapabilities + ")");
/*      */     }
/*      */ 
/* 1194 */     this.backBufferCaps = paramBufferCapabilities;
/* 1195 */     this.backBuffer = this.graphicsConfig.createBackBuffer(this, paramInt, paramBufferCapabilities);
/* 1196 */     this.xBackBuffer = this.graphicsConfig.createBackBufferImage(this.target, this.backBuffer);
/*      */   }
/*      */ 
/*      */   public BufferCapabilities getBackBufferCaps()
/*      */   {
/* 1202 */     return this.backBufferCaps;
/*      */   }
/*      */ 
/*      */   public void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
/*      */   {
/* 1208 */     if (buffersLog.isLoggable(500)) {
/* 1209 */       buffersLog.fine("flip(" + paramFlipContents + ")");
/*      */     }
/* 1211 */     if (this.backBuffer == 0L) {
/* 1212 */       throw new IllegalStateException("Buffers have not been created");
/*      */     }
/* 1214 */     this.graphicsConfig.flip(this, this.target, this.xBackBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramFlipContents);
/*      */   }
/*      */ 
/*      */   public Image getBackBuffer()
/*      */   {
/* 1219 */     if (buffersLog.isLoggable(500)) {
/* 1220 */       buffersLog.fine("getBackBuffer()");
/*      */     }
/* 1222 */     if (this.backBuffer == 0L) {
/* 1223 */       throw new IllegalStateException("Buffers have not been created");
/*      */     }
/* 1225 */     return this.xBackBuffer;
/*      */   }
/*      */ 
/*      */   public void destroyBuffers() {
/* 1229 */     if (buffersLog.isLoggable(500)) {
/* 1230 */       buffersLog.fine("destroyBuffers()");
/*      */     }
/* 1232 */     this.graphicsConfig.destroyBackBuffer(this.backBuffer);
/* 1233 */     this.backBuffer = 0L;
/* 1234 */     this.xBackBuffer = null;
/*      */   }
/*      */ 
/*      */   public void notifyTextComponentChange(boolean paramBoolean)
/*      */   {
/* 1240 */     Container localContainer = AWTAccessor.getComponentAccessor().getParent(this.target);
/* 1241 */     while ((localContainer != null) && (!(localContainer instanceof Frame)) && (!(localContainer instanceof Dialog)))
/*      */     {
/* 1244 */       localContainer = AWTAccessor.getComponentAccessor().getParent(localContainer);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isEventDisabled(XEvent paramXEvent)
/*      */   {
/* 1264 */     if (enableLog.isLoggable(300)) {
/* 1265 */       enableLog.finest("Component is {1}, checking for disabled event {0}", new Object[] { paramXEvent, isEnabled() ? "enabled" : "disable" });
/*      */     }
/* 1267 */     if (!isEnabled()) {
/* 1268 */       switch (paramXEvent.get_type()) {
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/* 1276 */         if (enableLog.isLoggable(400)) {
/* 1277 */           enableLog.finer("Event {0} is disable", new Object[] { paramXEvent });
/*      */         }
/* 1279 */         return true;
/*      */       }
/*      */     }
/* 1282 */     switch (paramXEvent.get_type()) {
/*      */     case 18:
/*      */     case 19:
/* 1285 */       return true;
/*      */     }
/* 1287 */     return super.isEventDisabled(paramXEvent);
/*      */   }
/*      */ 
/*      */   Color getPeerBackground() {
/* 1291 */     return this.background;
/*      */   }
/*      */ 
/*      */   Color getPeerForeground() {
/* 1295 */     return this.foreground;
/*      */   }
/*      */ 
/*      */   Font getPeerFont() {
/* 1299 */     return this.font;
/*      */   }
/*      */ 
/*      */   Dimension getPeerSize() {
/* 1303 */     return new Dimension(this.width, this.height);
/*      */   }
/*      */ 
/*      */   public void setBoundsOperation(int paramInt) {
/* 1307 */     synchronized (getStateLock()) {
/* 1308 */       if (this.boundsOperation == 3)
/* 1309 */         this.boundsOperation = paramInt;
/* 1310 */       else if (paramInt == 5)
/* 1311 */         this.boundsOperation = 3;
/*      */     }
/*      */   }
/*      */ 
/*      */   static String operationToString(int paramInt)
/*      */   {
/* 1317 */     switch (paramInt) {
/*      */     case 1:
/* 1319 */       return "SET_LOCATION";
/*      */     case 2:
/* 1321 */       return "SET_SIZE";
/*      */     case 4:
/* 1323 */       return "SET_CLIENT_SIZE";
/*      */     case 3:
/*      */     }
/* 1326 */     return "SET_BOUNDS";
/*      */   }
/*      */ 
/*      */   public void setZOrder(ComponentPeer paramComponentPeer)
/*      */   {
/* 1335 */     long l = paramComponentPeer != null ? ((XComponentPeer)paramComponentPeer).getWindow() : 0L;
/*      */ 
/* 1337 */     XToolkit.awtLock();
/*      */     try {
/* 1339 */       XlibWrapper.SetZOrder(XToolkit.getDisplay(), getWindow(), l);
/*      */     } finally {
/* 1341 */       XToolkit.awtUnlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addTree(Collection paramCollection, Set paramSet, Container paramContainer) {
/* 1346 */     for (int i = 0; i < paramContainer.getComponentCount(); i++) {
/* 1347 */       Component localComponent = paramContainer.getComponent(i);
/* 1348 */       ComponentPeer localComponentPeer = localComponent.getPeer();
/* 1349 */       if ((localComponentPeer instanceof XComponentPeer)) {
/* 1350 */         Long localLong = Long.valueOf(((XComponentPeer)localComponentPeer).getWindow());
/* 1351 */         if (!paramSet.contains(localLong)) {
/* 1352 */           paramSet.add(localLong);
/* 1353 */           paramCollection.add(localLong);
/*      */         }
/* 1355 */       } else if ((localComponent instanceof Container))
/*      */       {
/* 1358 */         addTree(paramCollection, paramSet, (Container)localComponent);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addDropTarget(DropTarget paramDropTarget)
/*      */   {
/* 1366 */     Object localObject = this.target;
/* 1367 */     while ((localObject != null) && (!(localObject instanceof Window))) {
/* 1368 */       localObject = ((Component)localObject).getParent();
/*      */     }
/*      */ 
/* 1371 */     if ((localObject instanceof Window)) {
/* 1372 */       XWindowPeer localXWindowPeer = (XWindowPeer)((Component)localObject).getPeer();
/* 1373 */       if (localXWindowPeer != null)
/* 1374 */         localXWindowPeer.addDropTarget();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeDropTarget(DropTarget paramDropTarget)
/*      */   {
/* 1380 */     Object localObject = this.target;
/* 1381 */     while ((localObject != null) && (!(localObject instanceof Window))) {
/* 1382 */       localObject = ((Component)localObject).getParent();
/*      */     }
/*      */ 
/* 1385 */     if ((localObject instanceof Window)) {
/* 1386 */       XWindowPeer localXWindowPeer = (XWindowPeer)((Component)localObject).getPeer();
/* 1387 */       if (localXWindowPeer != null)
/* 1388 */         localXWindowPeer.removeDropTarget();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void applyShape(Region paramRegion)
/*      */   {
/* 1398 */     if (XlibUtil.isShapingSupported()) {
/* 1399 */       if (shapeLog.isLoggable(400)) {
/* 1400 */         shapeLog.finer("*** INFO: Setting shape: PEER: " + this + "; WINDOW: " + getWindow() + "; TARGET: " + this.target + "; SHAPE: " + paramRegion);
/*      */       }
/*      */ 
/* 1406 */       XToolkit.awtLock();
/*      */       try {
/* 1408 */         if (paramRegion != null) {
/* 1409 */           XlibWrapper.SetRectangularShape(XToolkit.getDisplay(), getWindow(), paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), paramRegion.isRectangular() ? null : paramRegion);
/*      */         }
/*      */         else
/*      */         {
/* 1417 */           XlibWrapper.SetRectangularShape(XToolkit.getDisplay(), getWindow(), 0, 0, 0, 0, null);
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 1426 */         XToolkit.awtUnlock();
/*      */       }
/*      */     }
/* 1429 */     else if (shapeLog.isLoggable(400)) {
/* 1430 */       shapeLog.finer("*** WARNING: Shaping is NOT supported!");
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/* 1436 */     int i = -1; int j = -1;
/*      */ 
/* 1438 */     if (this.graphicsConfig != null) {
/* 1439 */       i = this.graphicsConfig.getVisual();
/*      */     }
/* 1441 */     if ((paramGraphicsConfiguration != null) && ((paramGraphicsConfiguration instanceof X11GraphicsConfig))) {
/* 1442 */       j = ((X11GraphicsConfig)paramGraphicsConfiguration).getVisual();
/*      */     }
/*      */ 
/* 1449 */     if ((i != -1) && (i != j)) {
/* 1450 */       return true;
/*      */     }
/*      */ 
/* 1453 */     initGraphicsConfiguration();
/* 1454 */     doValidateSurface();
/* 1455 */     return false;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XComponentPeer
 * JD-Core Version:    0.6.2
 */