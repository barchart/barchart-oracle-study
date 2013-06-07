/*       */ package java.awt;
/*       */ 
/*       */ import java.applet.Applet;
/*       */ import java.awt.dnd.DropTarget;
/*       */ import java.awt.event.ComponentEvent;
/*       */ import java.awt.event.ComponentListener;
/*       */ import java.awt.event.FocusEvent;
/*       */ import java.awt.event.FocusListener;
/*       */ import java.awt.event.HierarchyBoundsListener;
/*       */ import java.awt.event.HierarchyEvent;
/*       */ import java.awt.event.HierarchyListener;
/*       */ import java.awt.event.InputEvent;
/*       */ import java.awt.event.InputMethodEvent;
/*       */ import java.awt.event.InputMethodListener;
/*       */ import java.awt.event.KeyEvent;
/*       */ import java.awt.event.KeyListener;
/*       */ import java.awt.event.MouseEvent;
/*       */ import java.awt.event.MouseListener;
/*       */ import java.awt.event.MouseMotionListener;
/*       */ import java.awt.event.MouseWheelEvent;
/*       */ import java.awt.event.MouseWheelListener;
/*       */ import java.awt.event.PaintEvent;
/*       */ import java.awt.event.WindowEvent;
/*       */ import java.awt.im.InputMethodRequests;
/*       */ import java.awt.image.BufferStrategy;
/*       */ import java.awt.image.ColorModel;
/*       */ import java.awt.image.ImageObserver;
/*       */ import java.awt.image.ImageProducer;
/*       */ import java.awt.image.VolatileImage;
/*       */ import java.awt.peer.ComponentPeer;
/*       */ import java.awt.peer.ContainerPeer;
/*       */ import java.awt.peer.LightweightPeer;
/*       */ import java.awt.peer.MouseInfoPeer;
/*       */ import java.beans.PropertyChangeListener;
/*       */ import java.beans.PropertyChangeSupport;
/*       */ import java.beans.Transient;
/*       */ import java.io.IOException;
/*       */ import java.io.ObjectInputStream;
/*       */ import java.io.ObjectOutputStream;
/*       */ import java.io.OptionalDataException;
/*       */ import java.io.PrintStream;
/*       */ import java.io.PrintWriter;
/*       */ import java.io.Serializable;
/*       */ import java.lang.reflect.InvocationTargetException;
/*       */ import java.lang.reflect.Method;
/*       */ import java.security.AccessControlContext;
/*       */ import java.security.AccessController;
/*       */ import java.security.PrivilegedAction;
/*       */ import java.util.Collections;
/*       */ import java.util.EventListener;
/*       */ import java.util.HashSet;
/*       */ import java.util.Iterator;
/*       */ import java.util.Locale;
/*       */ import java.util.Map;
/*       */ import java.util.Set;
/*       */ import java.util.Vector;
/*       */ import java.util.WeakHashMap;
/*       */ import javax.accessibility.Accessible;
/*       */ import javax.accessibility.AccessibleComponent;
/*       */ import javax.accessibility.AccessibleContext;
/*       */ import javax.accessibility.AccessibleRole;
/*       */ import javax.accessibility.AccessibleSelection;
/*       */ import javax.accessibility.AccessibleState;
/*       */ import javax.accessibility.AccessibleStateSet;
/*       */ import javax.swing.JComponent;
/*       */ import sun.awt.AWTAccessor;
/*       */ import sun.awt.AWTAccessor.ComponentAccessor;
/*       */ import sun.awt.AppContext;
/*       */ import sun.awt.CausedFocusEvent.Cause;
/*       */ import sun.awt.ConstrainableGraphics;
/*       */ import sun.awt.EmbeddedFrame;
/*       */ import sun.awt.EventQueueItem;
/*       */ import sun.awt.RequestFocusController;
/*       */ import sun.awt.SubRegionShowable;
/*       */ import sun.awt.SunToolkit;
/*       */ import sun.awt.WindowClosingListener;
/*       */ import sun.awt.dnd.SunDropTargetEvent;
/*       */ import sun.awt.im.CompositionArea;
/*       */ import sun.awt.image.VSyncedBSManager;
/*       */ import sun.font.FontDesignMetrics;
/*       */ import sun.font.FontManager;
/*       */ import sun.font.FontManagerFactory;
/*       */ import sun.font.SunFontManager;
/*       */ import sun.java2d.SunGraphics2D;
/*       */ import sun.java2d.SunGraphicsEnvironment;
/*       */ import sun.java2d.pipe.Region;
/*       */ import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
/*       */ import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;
/*       */ import sun.security.action.GetPropertyAction;
/*       */ import sun.util.logging.PlatformLogger;
/*       */ 
/*       */ public abstract class Component
/*       */   implements ImageObserver, MenuContainer, Serializable
/*       */ {
/*       */   private static final PlatformLogger log;
/*       */   private static final PlatformLogger eventLog;
/*       */   private static final PlatformLogger focusLog;
/*       */   private static final PlatformLogger mixingLog;
/*       */   transient ComponentPeer peer;
/*       */   transient Container parent;
/*       */   transient AppContext appContext;
/*       */   int x;
/*       */   int y;
/*       */   int width;
/*       */   int height;
/*       */   Color foreground;
/*       */   Color background;
/*       */   Font font;
/*       */   Font peerFont;
/*       */   Cursor cursor;
/*       */   Locale locale;
/*   317 */   private transient GraphicsConfiguration graphicsConfig = null;
/*       */ 
/*   327 */   transient BufferStrategy bufferStrategy = null;
/*       */ 
/*   337 */   boolean ignoreRepaint = false;
/*       */ 
/*   347 */   boolean visible = true;
/*       */ 
/*   357 */   boolean enabled = true;
/*       */ 
/*   369 */   private volatile boolean valid = false;
/*       */   DropTarget dropTarget;
/*       */   Vector popups;
/*       */   private String name;
/*   407 */   private boolean nameExplicitlySet = false;
/*       */ 
/*   417 */   private boolean focusable = true;
/*       */   private static final int FOCUS_TRAVERSABLE_UNKNOWN = 0;
/*       */   private static final int FOCUS_TRAVERSABLE_DEFAULT = 1;
/*       */   private static final int FOCUS_TRAVERSABLE_SET = 2;
/*   429 */   private int isFocusTraversableOverridden = 0;
/*       */   Set[] focusTraversalKeys;
/*       */   private static final String[] focusTraversalKeyPropertyNames;
/*   465 */   private boolean focusTraversalKeysEnabled = true;
/*       */   static final Object LOCK;
/*   478 */   private volatile transient AccessControlContext acc = AccessController.getContext();
/*       */   Dimension minSize;
/*       */   boolean minSizeSet;
/*       */   Dimension prefSize;
/*       */   boolean prefSizeSet;
/*       */   Dimension maxSize;
/*       */   boolean maxSizeSet;
/*   524 */   transient ComponentOrientation componentOrientation = ComponentOrientation.UNKNOWN;
/*       */ 
/*   538 */   boolean newEventsOnly = false;
/*       */   transient ComponentListener componentListener;
/*       */   transient FocusListener focusListener;
/*       */   transient HierarchyListener hierarchyListener;
/*       */   transient HierarchyBoundsListener hierarchyBoundsListener;
/*       */   transient KeyListener keyListener;
/*       */   transient MouseListener mouseListener;
/*       */   transient MouseMotionListener mouseMotionListener;
/*       */   transient MouseWheelListener mouseWheelListener;
/*       */   transient InputMethodListener inputMethodListener;
/*   549 */   transient RuntimeException windowClosingException = null;
/*       */   static final String actionListenerK = "actionL";
/*       */   static final String adjustmentListenerK = "adjustmentL";
/*       */   static final String componentListenerK = "componentL";
/*       */   static final String containerListenerK = "containerL";
/*       */   static final String focusListenerK = "focusL";
/*       */   static final String itemListenerK = "itemL";
/*       */   static final String keyListenerK = "keyL";
/*       */   static final String mouseListenerK = "mouseL";
/*       */   static final String mouseMotionListenerK = "mouseMotionL";
/*       */   static final String mouseWheelListenerK = "mouseWheelL";
/*       */   static final String textListenerK = "textL";
/*       */   static final String ownedWindowK = "ownedL";
/*       */   static final String windowListenerK = "windowL";
/*       */   static final String inputMethodListenerK = "inputMethodL";
/*       */   static final String hierarchyListenerK = "hierarchyL";
/*       */   static final String hierarchyBoundsListenerK = "hierarchyBoundsL";
/*       */   static final String windowStateListenerK = "windowStateL";
/*       */   static final String windowFocusListenerK = "windowFocusL";
/*   585 */   long eventMask = 4096L;
/*       */   static boolean isInc;
/*       */   static int incRate;
/*       */   public static final float TOP_ALIGNMENT = 0.0F;
/*       */   public static final float CENTER_ALIGNMENT = 0.5F;
/*       */   public static final float BOTTOM_ALIGNMENT = 1.0F;
/*       */   public static final float LEFT_ALIGNMENT = 0.0F;
/*       */   public static final float RIGHT_ALIGNMENT = 1.0F;
/*       */   private static final long serialVersionUID = -7644114512714619750L;
/*       */   private PropertyChangeSupport changeSupport;
/*   677 */   private transient Object objectLock = new Object();
/*       */ 
/*   692 */   boolean isPacked = false;
/*       */ 
/*   700 */   private int boundsOp = 3;
/*       */ 
/*   797 */   private transient Region compoundShape = null;
/*       */ 
/*   806 */   private transient Region mixingCutoutRegion = null;
/*       */ 
/*   812 */   private transient boolean isAddNotifyComplete = false;
/*       */   transient boolean backgroundEraseDisabled;
/*       */   transient EventQueueItem[] eventCache;
/*  6110 */   private transient boolean coalescingEnabled = checkCoalescing();
/*       */ 
/*  6117 */   private static final Map<Class<?>, Boolean> coalesceMap = new WeakHashMap();
/*       */ 
/*  6156 */   private static final Class[] coalesceEventsParams = { AWTEvent.class, AWTEvent.class };
/*       */ 
/*  7740 */   private static RequestFocusController requestFocusController = new DummyRequestFocusController(null);
/*       */ 
/*  8002 */   private boolean autoFocusTransferOnDisposal = true;
/*       */ 
/*  8542 */   private int componentSerializedDataVersion = 4;
/*       */ 
/*  8965 */   AccessibleContext accessibleContext = null;
/*       */ 
/*       */   Object getObjectLock()
/*       */   {
/*   679 */     return this.objectLock;
/*       */   }
/*       */ 
/*       */   final AccessControlContext getAccessControlContext()
/*       */   {
/*   686 */     if (this.acc == null) {
/*   687 */       throw new SecurityException("Component is missing AccessControlContext");
/*       */     }
/*   689 */     return this.acc;
/*       */   }
/*       */ 
/*       */   int getBoundsOp()
/*       */   {
/*   819 */     assert (Thread.holdsLock(getTreeLock()));
/*   820 */     return this.boundsOp;
/*       */   }
/*       */ 
/*       */   void setBoundsOp(int paramInt) {
/*   824 */     assert (Thread.holdsLock(getTreeLock()));
/*   825 */     if (paramInt == 5) {
/*   826 */       this.boundsOp = 3;
/*       */     }
/*   828 */     else if (this.boundsOp == 3)
/*   829 */       this.boundsOp = paramInt;
/*       */   }
/*       */ 
/*       */   protected Component()
/*       */   {
/*   986 */     this.appContext = AppContext.getAppContext();
/*       */   }
/*       */ 
/*       */   void initializeFocusTraversalKeys() {
/*   990 */     this.focusTraversalKeys = new Set[3];
/*       */   }
/*       */ 
/*       */   String constructComponentName()
/*       */   {
/*   998 */     return null;
/*       */   }
/*       */ 
/*       */   public String getName()
/*       */   {
/*  1010 */     if ((this.name == null) && (!this.nameExplicitlySet)) {
/*  1011 */       synchronized (getObjectLock()) {
/*  1012 */         if ((this.name == null) && (!this.nameExplicitlySet))
/*  1013 */           this.name = constructComponentName();
/*       */       }
/*       */     }
/*  1016 */     return this.name;
/*       */   }
/*       */ 
/*       */   public void setName(String paramString)
/*       */   {
/*       */     String str;
/*  1028 */     synchronized (getObjectLock()) {
/*  1029 */       str = this.name;
/*  1030 */       this.name = paramString;
/*  1031 */       this.nameExplicitlySet = true;
/*       */     }
/*  1033 */     firePropertyChange("name", str, paramString);
/*       */   }
/*       */ 
/*       */   public Container getParent()
/*       */   {
/*  1042 */     return getParent_NoClientCode();
/*       */   }
/*       */ 
/*       */   final Container getParent_NoClientCode()
/*       */   {
/*  1050 */     return this.parent;
/*       */   }
/*       */ 
/*       */   Container getContainer()
/*       */   {
/*  1057 */     return getParent();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public ComponentPeer getPeer()
/*       */   {
/*  1067 */     return this.peer;
/*       */   }
/*       */ 
/*       */   public synchronized void setDropTarget(DropTarget paramDropTarget)
/*       */   {
/*  1080 */     if ((paramDropTarget == this.dropTarget) || ((this.dropTarget != null) && (this.dropTarget.equals(paramDropTarget))))
/*       */       return;
/*       */     DropTarget localDropTarget1;
/*  1085 */     if ((localDropTarget1 = this.dropTarget) != null) {
/*  1086 */       if (this.peer != null) this.dropTarget.removeNotify(this.peer);
/*       */ 
/*  1088 */       DropTarget localDropTarget2 = this.dropTarget;
/*       */ 
/*  1090 */       this.dropTarget = null;
/*       */       try
/*       */       {
/*  1093 */         localDropTarget2.setComponent(null);
/*       */       }
/*       */       catch (IllegalArgumentException localIllegalArgumentException2)
/*       */       {
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  1101 */     if ((this.dropTarget = paramDropTarget) != null)
/*       */       try {
/*  1103 */         this.dropTarget.setComponent(this);
/*  1104 */         if (this.peer != null) this.dropTarget.addNotify(this.peer); 
/*       */       }
/*  1106 */       catch (IllegalArgumentException localIllegalArgumentException1) { if (localDropTarget1 != null)
/*       */           try {
/*  1108 */             localDropTarget1.setComponent(this);
/*  1109 */             if (this.peer != null) this.dropTarget.addNotify(this.peer);
/*       */           }
/*       */           catch (IllegalArgumentException localIllegalArgumentException3)
/*       */           {
/*       */           }
/*       */       }
/*       */   }
/*       */ 
/*       */   public synchronized DropTarget getDropTarget()
/*       */   {
/*  1123 */     return this.dropTarget;
/*       */   }
/*       */ 
/*       */   public GraphicsConfiguration getGraphicsConfiguration()
/*       */   {
/*  1141 */     synchronized (getTreeLock()) {
/*  1142 */       return getGraphicsConfiguration_NoClientCode();
/*       */     }
/*       */   }
/*       */ 
/*       */   final GraphicsConfiguration getGraphicsConfiguration_NoClientCode() {
/*  1147 */     return this.graphicsConfig;
/*       */   }
/*       */ 
/*       */   void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration) {
/*  1151 */     synchronized (getTreeLock()) {
/*  1152 */       if (updateGraphicsData(paramGraphicsConfiguration)) {
/*  1153 */         removeNotify();
/*  1154 */         addNotify();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration) {
/*  1160 */     checkTreeLock();
/*       */ 
/*  1162 */     this.graphicsConfig = paramGraphicsConfiguration;
/*       */ 
/*  1164 */     ComponentPeer localComponentPeer = getPeer();
/*  1165 */     if (localComponentPeer != null) {
/*  1166 */       return localComponentPeer.updateGraphicsData(paramGraphicsConfiguration);
/*       */     }
/*  1168 */     return false;
/*       */   }
/*       */ 
/*       */   void checkGD(String paramString)
/*       */   {
/*  1176 */     if ((this.graphicsConfig != null) && 
/*  1177 */       (!this.graphicsConfig.getDevice().getIDstring().equals(paramString)))
/*  1178 */       throw new IllegalArgumentException("adding a container to a container on a different GraphicsDevice");
/*       */   }
/*       */ 
/*       */   public final Object getTreeLock()
/*       */   {
/*  1191 */     return LOCK;
/*       */   }
/*       */ 
/*       */   final void checkTreeLock() {
/*  1195 */     if (!Thread.holdsLock(getTreeLock()))
/*  1196 */       throw new IllegalStateException("This function should be called while holding treeLock");
/*       */   }
/*       */ 
/*       */   public Toolkit getToolkit()
/*       */   {
/*  1209 */     return getToolkitImpl();
/*       */   }
/*       */ 
/*       */   final Toolkit getToolkitImpl()
/*       */   {
/*  1217 */     ComponentPeer localComponentPeer = this.peer;
/*  1218 */     if ((localComponentPeer != null) && (!(localComponentPeer instanceof LightweightPeer))) {
/*  1219 */       return localComponentPeer.getToolkit();
/*       */     }
/*  1221 */     Container localContainer = this.parent;
/*  1222 */     if (localContainer != null) {
/*  1223 */       return localContainer.getToolkitImpl();
/*       */     }
/*  1225 */     return Toolkit.getDefaultToolkit();
/*       */   }
/*       */ 
/*       */   public boolean isValid()
/*       */   {
/*  1242 */     return (this.peer != null) && (this.valid);
/*       */   }
/*       */ 
/*       */   public boolean isDisplayable()
/*       */   {
/*  1270 */     return getPeer() != null;
/*       */   }
/*       */ 
/*       */   @Transient
/*       */   public boolean isVisible()
/*       */   {
/*  1285 */     return isVisible_NoClientCode();
/*       */   }
/*       */   final boolean isVisible_NoClientCode() {
/*  1288 */     return this.visible;
/*       */   }
/*       */ 
/*       */   boolean isRecursivelyVisible()
/*       */   {
/*  1298 */     return (this.visible) && ((this.parent == null) || (this.parent.isRecursivelyVisible()));
/*       */   }
/*       */ 
/*       */   Point pointRelativeToComponent(Point paramPoint)
/*       */   {
/*  1306 */     Point localPoint = getLocationOnScreen();
/*  1307 */     return new Point(paramPoint.x - localPoint.x, paramPoint.y - localPoint.y);
/*       */   }
/*       */ 
/*       */   Component findUnderMouseInWindow(PointerInfo paramPointerInfo)
/*       */   {
/*  1321 */     if (!isShowing()) {
/*  1322 */       return null;
/*       */     }
/*  1324 */     Window localWindow = getContainingWindow();
/*  1325 */     if (!Toolkit.getDefaultToolkit().getMouseInfoPeer().isWindowUnderMouse(localWindow)) {
/*  1326 */       return null;
/*       */     }
/*       */ 
/*  1329 */     Point localPoint = localWindow.pointRelativeToComponent(paramPointerInfo.getLocation());
/*  1330 */     Component localComponent = localWindow.findComponentAt(localPoint.x, localPoint.y, true);
/*       */ 
/*  1333 */     return localComponent;
/*       */   }
/*       */ 
/*       */   public Point getMousePosition()
/*       */     throws HeadlessException
/*       */   {
/*  1364 */     if (GraphicsEnvironment.isHeadless()) {
/*  1365 */       throw new HeadlessException();
/*       */     }
/*       */ 
/*  1368 */     PointerInfo localPointerInfo = (PointerInfo)AccessController.doPrivileged(new PrivilegedAction()
/*       */     {
/*       */       public Object run() {
/*  1371 */         return MouseInfo.getPointerInfo();
/*       */       }
/*       */     });
/*  1376 */     synchronized (getTreeLock()) {
/*  1377 */       Component localComponent = findUnderMouseInWindow(localPointerInfo);
/*  1378 */       if (!isSameOrAncestorOf(localComponent, true)) {
/*  1379 */         return null;
/*       */       }
/*  1381 */       return pointRelativeToComponent(localPointerInfo.getLocation());
/*       */     }
/*       */   }
/*       */ 
/*       */   boolean isSameOrAncestorOf(Component paramComponent, boolean paramBoolean)
/*       */   {
/*  1389 */     return paramComponent == this;
/*       */   }
/*       */ 
/*       */   public boolean isShowing()
/*       */   {
/*  1411 */     if ((this.visible) && (this.peer != null)) {
/*  1412 */       Container localContainer = this.parent;
/*  1413 */       return (localContainer == null) || (localContainer.isShowing());
/*       */     }
/*  1415 */     return false;
/*       */   }
/*       */ 
/*       */   public boolean isEnabled()
/*       */   {
/*  1429 */     return isEnabledImpl();
/*       */   }
/*       */ 
/*       */   final boolean isEnabledImpl()
/*       */   {
/*  1437 */     return this.enabled;
/*       */   }
/*       */ 
/*       */   public void setEnabled(boolean paramBoolean)
/*       */   {
/*  1458 */     enable(paramBoolean);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void enable()
/*       */   {
/*  1467 */     if (!this.enabled) {
/*  1468 */       synchronized (getTreeLock()) {
/*  1469 */         this.enabled = true;
/*  1470 */         ComponentPeer localComponentPeer = this.peer;
/*  1471 */         if (localComponentPeer != null) {
/*  1472 */           localComponentPeer.setEnabled(true);
/*  1473 */           if (this.visible) {
/*  1474 */             updateCursorImmediately();
/*       */           }
/*       */         }
/*       */       }
/*  1478 */       if (this.accessibleContext != null)
/*  1479 */         this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
/*       */     }
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void enable(boolean paramBoolean)
/*       */   {
/*  1492 */     if (paramBoolean)
/*  1493 */       enable();
/*       */     else
/*  1495 */       disable();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void disable()
/*       */   {
/*  1505 */     if (this.enabled) {
/*  1506 */       KeyboardFocusManager.clearMostRecentFocusOwner(this);
/*  1507 */       synchronized (getTreeLock()) {
/*  1508 */         this.enabled = false;
/*       */ 
/*  1510 */         if (((isFocusOwner()) || ((containsFocus()) && (!isLightweight()))) && (KeyboardFocusManager.isAutoFocusTransferEnabled()))
/*       */         {
/*  1517 */           transferFocus(false);
/*       */         }
/*  1519 */         ComponentPeer localComponentPeer = this.peer;
/*  1520 */         if (localComponentPeer != null) {
/*  1521 */           localComponentPeer.setEnabled(false);
/*  1522 */           if (this.visible) {
/*  1523 */             updateCursorImmediately();
/*       */           }
/*       */         }
/*       */       }
/*  1527 */       if (this.accessibleContext != null)
/*  1528 */         this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean isDoubleBuffered()
/*       */   {
/*  1544 */     return false;
/*       */   }
/*       */ 
/*       */   public void enableInputMethods(boolean paramBoolean)
/*       */   {
/*       */     java.awt.im.InputContext localInputContext;
/*  1560 */     if (paramBoolean) {
/*  1561 */       if ((this.eventMask & 0x1000) != 0L) {
/*  1562 */         return;
/*       */       }
/*       */ 
/*  1567 */       if (isFocusOwner()) {
/*  1568 */         localInputContext = getInputContext();
/*  1569 */         if (localInputContext != null) {
/*  1570 */           FocusEvent localFocusEvent = new FocusEvent(this, 1004);
/*       */ 
/*  1572 */           localInputContext.dispatchEvent(localFocusEvent);
/*       */         }
/*       */       }
/*       */ 
/*  1576 */       this.eventMask |= 4096L;
/*       */     } else {
/*  1578 */       if ((this.eventMask & 0x1000) != 0L) {
/*  1579 */         localInputContext = getInputContext();
/*  1580 */         if (localInputContext != null) {
/*  1581 */           localInputContext.endComposition();
/*  1582 */           localInputContext.removeNotify(this);
/*       */         }
/*       */       }
/*  1585 */       this.eventMask &= -4097L;
/*       */     }
/*       */   }
/*       */ 
/*       */   public void setVisible(boolean paramBoolean)
/*       */   {
/*  1603 */     show(paramBoolean);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void show()
/*       */   {
/*  1612 */     if (!this.visible) {
/*  1613 */       synchronized (getTreeLock()) {
/*  1614 */         this.visible = true;
/*  1615 */         mixOnShowing();
/*  1616 */         ComponentPeer localComponentPeer = this.peer;
/*  1617 */         if (localComponentPeer != null) {
/*  1618 */           localComponentPeer.setVisible(true);
/*  1619 */           createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
/*       */ 
/*  1623 */           if ((localComponentPeer instanceof LightweightPeer)) {
/*  1624 */             repaint();
/*       */           }
/*  1626 */           updateCursorImmediately();
/*       */         }
/*       */ 
/*  1629 */         if ((this.componentListener != null) || ((this.eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
/*       */         {
/*  1632 */           ComponentEvent localComponentEvent = new ComponentEvent(this, 102);
/*       */ 
/*  1634 */           Toolkit.getEventQueue().postEvent(localComponentEvent);
/*       */         }
/*       */       }
/*  1637 */       ??? = this.parent;
/*  1638 */       if (??? != null)
/*  1639 */         ((Container)???).invalidate();
/*       */     }
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void show(boolean paramBoolean)
/*       */   {
/*  1650 */     if (paramBoolean)
/*  1651 */       show();
/*       */     else
/*  1653 */       hide();
/*       */   }
/*       */ 
/*       */   boolean containsFocus()
/*       */   {
/*  1658 */     return isFocusOwner();
/*       */   }
/*       */ 
/*       */   void clearMostRecentFocusOwnerOnHide() {
/*  1662 */     KeyboardFocusManager.clearMostRecentFocusOwner(this);
/*       */   }
/*       */ 
/*       */   void clearCurrentFocusCycleRootOnHide()
/*       */   {
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void hide()
/*       */   {
/*  1675 */     this.isPacked = false;
/*       */ 
/*  1677 */     if (this.visible) {
/*  1678 */       clearCurrentFocusCycleRootOnHide();
/*  1679 */       clearMostRecentFocusOwnerOnHide();
/*  1680 */       synchronized (getTreeLock()) {
/*  1681 */         this.visible = false;
/*  1682 */         mixOnHiding(isLightweight());
/*  1683 */         if ((containsFocus()) && (KeyboardFocusManager.isAutoFocusTransferEnabled())) {
/*  1684 */           transferFocus(true);
/*       */         }
/*  1686 */         ComponentPeer localComponentPeer = this.peer;
/*  1687 */         if (localComponentPeer != null) {
/*  1688 */           localComponentPeer.setVisible(false);
/*  1689 */           createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
/*       */ 
/*  1693 */           if ((localComponentPeer instanceof LightweightPeer)) {
/*  1694 */             repaint();
/*       */           }
/*  1696 */           updateCursorImmediately();
/*       */         }
/*  1698 */         if ((this.componentListener != null) || ((this.eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
/*       */         {
/*  1701 */           ComponentEvent localComponentEvent = new ComponentEvent(this, 103);
/*       */ 
/*  1703 */           Toolkit.getEventQueue().postEvent(localComponentEvent);
/*       */         }
/*       */       }
/*  1706 */       ??? = this.parent;
/*  1707 */       if (??? != null)
/*  1708 */         ((Container)???).invalidate();
/*       */     }
/*       */   }
/*       */ 
/*       */   @Transient
/*       */   public Color getForeground()
/*       */   {
/*  1725 */     Color localColor = this.foreground;
/*  1726 */     if (localColor != null) {
/*  1727 */       return localColor;
/*       */     }
/*  1729 */     Container localContainer = this.parent;
/*  1730 */     return localContainer != null ? localContainer.getForeground() : null;
/*       */   }
/*       */ 
/*       */   public void setForeground(Color paramColor)
/*       */   {
/*  1743 */     Color localColor = this.foreground;
/*  1744 */     ComponentPeer localComponentPeer = this.peer;
/*  1745 */     this.foreground = paramColor;
/*  1746 */     if (localComponentPeer != null) {
/*  1747 */       paramColor = getForeground();
/*  1748 */       if (paramColor != null) {
/*  1749 */         localComponentPeer.setForeground(paramColor);
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  1754 */     firePropertyChange("foreground", localColor, paramColor);
/*       */   }
/*       */ 
/*       */   public boolean isForegroundSet()
/*       */   {
/*  1767 */     return this.foreground != null;
/*       */   }
/*       */ 
/*       */   @Transient
/*       */   public Color getBackground()
/*       */   {
/*  1780 */     Color localColor = this.background;
/*  1781 */     if (localColor != null) {
/*  1782 */       return localColor;
/*       */     }
/*  1784 */     Container localContainer = this.parent;
/*  1785 */     return localContainer != null ? localContainer.getBackground() : null;
/*       */   }
/*       */ 
/*       */   public void setBackground(Color paramColor)
/*       */   {
/*  1804 */     Color localColor = this.background;
/*  1805 */     ComponentPeer localComponentPeer = this.peer;
/*  1806 */     this.background = paramColor;
/*  1807 */     if (localComponentPeer != null) {
/*  1808 */       paramColor = getBackground();
/*  1809 */       if (paramColor != null) {
/*  1810 */         localComponentPeer.setBackground(paramColor);
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  1815 */     firePropertyChange("background", localColor, paramColor);
/*       */   }
/*       */ 
/*       */   public boolean isBackgroundSet()
/*       */   {
/*  1828 */     return this.background != null;
/*       */   }
/*       */ 
/*       */   @Transient
/*       */   public Font getFont()
/*       */   {
/*  1840 */     return getFont_NoClientCode();
/*       */   }
/*       */ 
/*       */   final Font getFont_NoClientCode()
/*       */   {
/*  1848 */     Font localFont = this.font;
/*  1849 */     if (localFont != null) {
/*  1850 */       return localFont;
/*       */     }
/*  1852 */     Container localContainer = this.parent;
/*  1853 */     return localContainer != null ? localContainer.getFont_NoClientCode() : null;
/*       */   }
/*       */ 
/*       */   public void setFont(Font paramFont)
/*       */   {
/*       */     Font localFont1;
/*       */     Font localFont2;
/*  1873 */     synchronized (getTreeLock()) {
/*  1874 */       synchronized (this) {
/*  1875 */         localFont1 = this.font;
/*  1876 */         localFont2 = this.font = paramFont;
/*       */       }
/*  1878 */       ??? = this.peer;
/*  1879 */       if (??? != null) {
/*  1880 */         paramFont = getFont();
/*  1881 */         if (paramFont != null) {
/*  1882 */           ((ComponentPeer)???).setFont(paramFont);
/*  1883 */           this.peerFont = paramFont;
/*       */         }
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  1889 */     firePropertyChange("font", localFont1, localFont2);
/*       */ 
/*  1894 */     if ((paramFont != localFont1) && ((localFont1 == null) || (!localFont1.equals(paramFont))))
/*       */     {
/*  1896 */       invalidateIfValid();
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean isFontSet()
/*       */   {
/*  1910 */     return this.font != null;
/*       */   }
/*       */ 
/*       */   public Locale getLocale()
/*       */   {
/*  1925 */     Locale localLocale = this.locale;
/*  1926 */     if (localLocale != null) {
/*  1927 */       return localLocale;
/*       */     }
/*  1929 */     Container localContainer = this.parent;
/*       */ 
/*  1931 */     if (localContainer == null) {
/*  1932 */       throw new IllegalComponentStateException("This component must have a parent in order to determine its locale");
/*       */     }
/*  1934 */     return localContainer.getLocale();
/*       */   }
/*       */ 
/*       */   public void setLocale(Locale paramLocale)
/*       */   {
/*  1950 */     Locale localLocale = this.locale;
/*  1951 */     this.locale = paramLocale;
/*       */ 
/*  1955 */     firePropertyChange("locale", localLocale, paramLocale);
/*       */ 
/*  1958 */     invalidateIfValid();
/*       */   }
/*       */ 
/*       */   public ColorModel getColorModel()
/*       */   {
/*  1971 */     ComponentPeer localComponentPeer = this.peer;
/*  1972 */     if ((localComponentPeer != null) && (!(localComponentPeer instanceof LightweightPeer)))
/*  1973 */       return localComponentPeer.getColorModel();
/*  1974 */     if (GraphicsEnvironment.isHeadless()) {
/*  1975 */       return ColorModel.getRGBdefault();
/*       */     }
/*  1977 */     return getToolkit().getColorModel();
/*       */   }
/*       */ 
/*       */   public Point getLocation()
/*       */   {
/*  2001 */     return location();
/*       */   }
/*       */ 
/*       */   public Point getLocationOnScreen()
/*       */   {
/*  2017 */     synchronized (getTreeLock()) {
/*  2018 */       return getLocationOnScreen_NoTreeLock();
/*       */     }
/*       */   }
/*       */ 
/*       */   final Point getLocationOnScreen_NoTreeLock()
/*       */   {
/*  2028 */     if ((this.peer != null) && (isShowing())) {
/*  2029 */       if ((this.peer instanceof LightweightPeer))
/*       */       {
/*  2032 */         localObject1 = getNativeContainer();
/*  2033 */         Point localPoint = ((Container)localObject1).peer.getLocationOnScreen();
/*  2034 */         for (Object localObject2 = this; localObject2 != localObject1; localObject2 = ((Component)localObject2).getParent()) {
/*  2035 */           localPoint.x += ((Component)localObject2).x;
/*  2036 */           localPoint.y += ((Component)localObject2).y;
/*       */         }
/*  2038 */         return localPoint;
/*       */       }
/*  2040 */       Object localObject1 = this.peer.getLocationOnScreen();
/*  2041 */       return localObject1;
/*       */     }
/*       */ 
/*  2044 */     throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public Point location()
/*       */   {
/*  2055 */     return location_NoClientCode();
/*       */   }
/*       */ 
/*       */   private Point location_NoClientCode() {
/*  2059 */     return new Point(this.x, this.y);
/*       */   }
/*       */ 
/*       */   public void setLocation(int paramInt1, int paramInt2)
/*       */   {
/*  2080 */     move(paramInt1, paramInt2);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void move(int paramInt1, int paramInt2)
/*       */   {
/*  2089 */     synchronized (getTreeLock()) {
/*  2090 */       setBoundsOp(1);
/*  2091 */       setBounds(paramInt1, paramInt2, this.width, this.height);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void setLocation(Point paramPoint)
/*       */   {
/*  2112 */     setLocation(paramPoint.x, paramPoint.y);
/*       */   }
/*       */ 
/*       */   public Dimension getSize()
/*       */   {
/*  2128 */     return size();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public Dimension size()
/*       */   {
/*  2137 */     return new Dimension(this.width, this.height);
/*       */   }
/*       */ 
/*       */   public void setSize(int paramInt1, int paramInt2)
/*       */   {
/*  2155 */     resize(paramInt1, paramInt2);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void resize(int paramInt1, int paramInt2)
/*       */   {
/*  2164 */     synchronized (getTreeLock()) {
/*  2165 */       setBoundsOp(2);
/*  2166 */       setBounds(this.x, this.y, paramInt1, paramInt2);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void setSize(Dimension paramDimension)
/*       */   {
/*  2186 */     resize(paramDimension);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void resize(Dimension paramDimension)
/*       */   {
/*  2195 */     setSize(paramDimension.width, paramDimension.height);
/*       */   }
/*       */ 
/*       */   public Rectangle getBounds()
/*       */   {
/*  2209 */     return bounds();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public Rectangle bounds()
/*       */   {
/*  2218 */     return new Rectangle(this.x, this.y, this.width, this.height);
/*       */   }
/*       */ 
/*       */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */   {
/*  2243 */     reshape(paramInt1, paramInt2, paramInt3, paramInt4);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */   {
/*  2252 */     synchronized (getTreeLock()) {
/*       */       try {
/*  2254 */         setBoundsOp(3);
/*  2255 */         boolean bool1 = (this.width != paramInt3) || (this.height != paramInt4);
/*  2256 */         boolean bool2 = (this.x != paramInt1) || (this.y != paramInt2);
/*  2257 */         if ((!bool1) && (!bool2))
/*       */         {
/*  2302 */           setBoundsOp(5);
/*       */         }
/*       */         else
/*       */         {
/*  2260 */           int i = this.x;
/*  2261 */           int j = this.y;
/*  2262 */           int k = this.width;
/*  2263 */           int m = this.height;
/*  2264 */           this.x = paramInt1;
/*  2265 */           this.y = paramInt2;
/*  2266 */           this.width = paramInt3;
/*  2267 */           this.height = paramInt4;
/*       */ 
/*  2269 */           if (bool1) {
/*  2270 */             this.isPacked = false;
/*       */           }
/*       */ 
/*  2273 */           int n = 1;
/*  2274 */           mixOnReshaping();
/*  2275 */           if (this.peer != null)
/*       */           {
/*  2277 */             if (!(this.peer instanceof LightweightPeer)) {
/*  2278 */               reshapeNativePeer(paramInt1, paramInt2, paramInt3, paramInt4, getBoundsOp());
/*       */ 
/*  2280 */               bool1 = (k != this.width) || (m != this.height);
/*  2281 */               bool2 = (i != this.x) || (j != this.y);
/*       */ 
/*  2286 */               if ((this instanceof Window)) {
/*  2287 */                 n = 0;
/*       */               }
/*       */             }
/*  2290 */             if (bool1) {
/*  2291 */               invalidate();
/*       */             }
/*  2293 */             if (this.parent != null) {
/*  2294 */               this.parent.invalidateIfValid();
/*       */             }
/*       */           }
/*  2297 */           if (n != 0) {
/*  2298 */             notifyNewBounds(bool1, bool2);
/*       */           }
/*  2300 */           repaintParentIfNeeded(i, j, k, m);
/*       */         }
/*       */       } finally { setBoundsOp(5); }
/*       */ 
/*       */     }
/*       */   }
/*       */ 
/*       */   private void repaintParentIfNeeded(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */   {
/*  2310 */     if ((this.parent != null) && ((this.peer instanceof LightweightPeer)) && (isShowing()))
/*       */     {
/*  2312 */       this.parent.repaint(paramInt1, paramInt2, paramInt3, paramInt4);
/*       */ 
/*  2314 */       repaint();
/*       */     }
/*       */   }
/*       */ 
/*       */   private void reshapeNativePeer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*       */   {
/*  2321 */     int i = paramInt1;
/*  2322 */     int j = paramInt2;
/*  2323 */     for (Container localContainer = this.parent; 
/*  2324 */       (localContainer != null) && ((localContainer.peer instanceof LightweightPeer)); 
/*  2325 */       localContainer = localContainer.parent)
/*       */     {
/*  2327 */       i += localContainer.x;
/*  2328 */       j += localContainer.y;
/*       */     }
/*  2330 */     this.peer.setBounds(i, j, paramInt3, paramInt4, paramInt5);
/*       */   }
/*       */ 
/*       */   private void notifyNewBounds(boolean paramBoolean1, boolean paramBoolean2)
/*       */   {
/*  2335 */     if ((this.componentListener != null) || ((this.eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
/*       */     {
/*       */       ComponentEvent localComponentEvent;
/*  2339 */       if (paramBoolean1) {
/*  2340 */         localComponentEvent = new ComponentEvent(this, 101);
/*       */ 
/*  2342 */         Toolkit.getEventQueue().postEvent(localComponentEvent);
/*       */       }
/*  2344 */       if (paramBoolean2) {
/*  2345 */         localComponentEvent = new ComponentEvent(this, 100);
/*       */ 
/*  2347 */         Toolkit.getEventQueue().postEvent(localComponentEvent);
/*       */       }
/*       */     }
/*  2350 */     else if (((this instanceof Container)) && (((Container)this).countComponents() > 0)) {
/*  2351 */       boolean bool = Toolkit.enabledOnToolkit(65536L);
/*       */ 
/*  2353 */       if (paramBoolean1)
/*       */       {
/*  2355 */         ((Container)this).createChildHierarchyEvents(1402, 0L, bool);
/*       */       }
/*       */ 
/*  2358 */       if (paramBoolean2)
/*  2359 */         ((Container)this).createChildHierarchyEvents(1401, 0L, bool);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void setBounds(Rectangle paramRectangle)
/*       */   {
/*  2387 */     setBounds(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*       */   }
/*       */ 
/*       */   public int getX()
/*       */   {
/*  2402 */     return this.x;
/*       */   }
/*       */ 
/*       */   public int getY()
/*       */   {
/*  2417 */     return this.y;
/*       */   }
/*       */ 
/*       */   public int getWidth()
/*       */   {
/*  2432 */     return this.width;
/*       */   }
/*       */ 
/*       */   public int getHeight()
/*       */   {
/*  2447 */     return this.height;
/*       */   }
/*       */ 
/*       */   public Rectangle getBounds(Rectangle paramRectangle)
/*       */   {
/*  2462 */     if (paramRectangle == null) {
/*  2463 */       return new Rectangle(getX(), getY(), getWidth(), getHeight());
/*       */     }
/*       */ 
/*  2466 */     paramRectangle.setBounds(getX(), getY(), getWidth(), getHeight());
/*  2467 */     return paramRectangle;
/*       */   }
/*       */ 
/*       */   public Dimension getSize(Dimension paramDimension)
/*       */   {
/*  2482 */     if (paramDimension == null) {
/*  2483 */       return new Dimension(getWidth(), getHeight());
/*       */     }
/*       */ 
/*  2486 */     paramDimension.setSize(getWidth(), getHeight());
/*  2487 */     return paramDimension;
/*       */   }
/*       */ 
/*       */   public Point getLocation(Point paramPoint)
/*       */   {
/*  2503 */     if (paramPoint == null) {
/*  2504 */       return new Point(getX(), getY());
/*       */     }
/*       */ 
/*  2507 */     paramPoint.setLocation(getX(), getY());
/*  2508 */     return paramPoint;
/*       */   }
/*       */ 
/*       */   public boolean isOpaque()
/*       */   {
/*  2530 */     if (getPeer() == null) {
/*  2531 */       return false;
/*       */     }
/*       */ 
/*  2534 */     return !isLightweight();
/*       */   }
/*       */ 
/*       */   public boolean isLightweight()
/*       */   {
/*  2556 */     return getPeer() instanceof LightweightPeer;
/*       */   }
/*       */ 
/*       */   public void setPreferredSize(Dimension paramDimension)
/*       */   {
/*       */     Dimension localDimension;
/*  2576 */     if (this.prefSizeSet) {
/*  2577 */       localDimension = this.prefSize;
/*       */     }
/*       */     else {
/*  2580 */       localDimension = null;
/*       */     }
/*  2582 */     this.prefSize = paramDimension;
/*  2583 */     this.prefSizeSet = (paramDimension != null);
/*  2584 */     firePropertyChange("preferredSize", localDimension, paramDimension);
/*       */   }
/*       */ 
/*       */   public boolean isPreferredSizeSet()
/*       */   {
/*  2597 */     return this.prefSizeSet;
/*       */   }
/*       */ 
/*       */   public Dimension getPreferredSize()
/*       */   {
/*  2608 */     return preferredSize();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public Dimension preferredSize()
/*       */   {
/*  2621 */     Dimension localDimension = this.prefSize;
/*  2622 */     if ((localDimension == null) || ((!isPreferredSizeSet()) && (!isValid()))) {
/*  2623 */       synchronized (getTreeLock()) {
/*  2624 */         this.prefSize = (this.peer != null ? this.peer.getPreferredSize() : getMinimumSize());
/*       */ 
/*  2627 */         localDimension = this.prefSize;
/*       */       }
/*       */     }
/*  2630 */     return new Dimension(localDimension);
/*       */   }
/*       */ 
/*       */   public void setMinimumSize(Dimension paramDimension)
/*       */   {
/*       */     Dimension localDimension;
/*  2649 */     if (this.minSizeSet) {
/*  2650 */       localDimension = this.minSize;
/*       */     }
/*       */     else {
/*  2653 */       localDimension = null;
/*       */     }
/*  2655 */     this.minSize = paramDimension;
/*  2656 */     this.minSizeSet = (paramDimension != null);
/*  2657 */     firePropertyChange("minimumSize", localDimension, paramDimension);
/*       */   }
/*       */ 
/*       */   public boolean isMinimumSizeSet()
/*       */   {
/*  2669 */     return this.minSizeSet;
/*       */   }
/*       */ 
/*       */   public Dimension getMinimumSize()
/*       */   {
/*  2679 */     return minimumSize();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public Dimension minimumSize()
/*       */   {
/*  2691 */     Dimension localDimension = this.minSize;
/*  2692 */     if ((localDimension == null) || ((!isMinimumSizeSet()) && (!isValid()))) {
/*  2693 */       synchronized (getTreeLock()) {
/*  2694 */         this.minSize = (this.peer != null ? this.peer.getMinimumSize() : size());
/*       */ 
/*  2697 */         localDimension = this.minSize;
/*       */       }
/*       */     }
/*  2700 */     return new Dimension(localDimension);
/*       */   }
/*       */ 
/*       */   public void setMaximumSize(Dimension paramDimension)
/*       */   {
/*       */     Dimension localDimension;
/*  2720 */     if (this.maxSizeSet) {
/*  2721 */       localDimension = this.maxSize;
/*       */     }
/*       */     else {
/*  2724 */       localDimension = null;
/*       */     }
/*  2726 */     this.maxSize = paramDimension;
/*  2727 */     this.maxSizeSet = (paramDimension != null);
/*  2728 */     firePropertyChange("maximumSize", localDimension, paramDimension);
/*       */   }
/*       */ 
/*       */   public boolean isMaximumSizeSet()
/*       */   {
/*  2740 */     return this.maxSizeSet;
/*       */   }
/*       */ 
/*       */   public Dimension getMaximumSize()
/*       */   {
/*  2751 */     if (isMaximumSizeSet()) {
/*  2752 */       return new Dimension(this.maxSize);
/*       */     }
/*  2754 */     return new Dimension(32767, 32767);
/*       */   }
/*       */ 
/*       */   public float getAlignmentX()
/*       */   {
/*  2765 */     return 0.5F;
/*       */   }
/*       */ 
/*       */   public float getAlignmentY()
/*       */   {
/*  2776 */     return 0.5F;
/*       */   }
/*       */ 
/*       */   public int getBaseline(int paramInt1, int paramInt2)
/*       */   {
/*  2804 */     if ((paramInt1 < 0) || (paramInt2 < 0)) {
/*  2805 */       throw new IllegalArgumentException("Width and height must be >= 0");
/*       */     }
/*       */ 
/*  2808 */     return -1;
/*       */   }
/*       */ 
/*       */   public BaselineResizeBehavior getBaselineResizeBehavior()
/*       */   {
/*  2833 */     return BaselineResizeBehavior.OTHER;
/*       */   }
/*       */ 
/*       */   public void doLayout()
/*       */   {
/*  2844 */     layout();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void layout()
/*       */   {
/*       */   }
/*       */ 
/*       */   public void validate()
/*       */   {
/*  2868 */     synchronized (getTreeLock()) {
/*  2869 */       ComponentPeer localComponentPeer = this.peer;
/*  2870 */       boolean bool = isValid();
/*  2871 */       if ((!bool) && (localComponentPeer != null)) {
/*  2872 */         Font localFont1 = getFont();
/*  2873 */         Font localFont2 = this.peerFont;
/*  2874 */         if ((localFont1 != localFont2) && ((localFont2 == null) || (!localFont2.equals(localFont1))))
/*       */         {
/*  2876 */           localComponentPeer.setFont(localFont1);
/*  2877 */           this.peerFont = localFont1;
/*       */         }
/*  2879 */         localComponentPeer.layout();
/*       */       }
/*  2881 */       this.valid = true;
/*  2882 */       if (!bool)
/*  2883 */         mixOnValidating();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void invalidate()
/*       */   {
/*  2911 */     synchronized (getTreeLock())
/*       */     {
/*  2916 */       this.valid = false;
/*  2917 */       if (!isPreferredSizeSet()) {
/*  2918 */         this.prefSize = null;
/*       */       }
/*  2920 */       if (!isMinimumSizeSet()) {
/*  2921 */         this.minSize = null;
/*       */       }
/*  2923 */       if (!isMaximumSizeSet()) {
/*  2924 */         this.maxSize = null;
/*       */       }
/*  2926 */       invalidateParent();
/*       */     }
/*       */   }
/*       */ 
/*       */   void invalidateParent()
/*       */   {
/*  2936 */     if (this.parent != null)
/*  2937 */       this.parent.invalidateIfValid();
/*       */   }
/*       */ 
/*       */   final void invalidateIfValid()
/*       */   {
/*  2944 */     if (isValid())
/*  2945 */       invalidate();
/*       */   }
/*       */ 
/*       */   public void revalidate()
/*       */   {
/*  2966 */     synchronized (getTreeLock()) {
/*  2967 */       invalidate();
/*       */ 
/*  2969 */       Container localContainer = getContainer();
/*  2970 */       if (localContainer == null)
/*       */       {
/*  2972 */         validate();
/*       */       } else {
/*  2974 */         while ((!localContainer.isValidateRoot()) && 
/*  2975 */           (localContainer.getContainer() != null))
/*       */         {
/*  2981 */           localContainer = localContainer.getContainer();
/*       */         }
/*       */ 
/*  2984 */         localContainer.validate();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public Graphics getGraphics()
/*       */   {
/*  2999 */     if ((this.peer instanceof LightweightPeer))
/*       */     {
/*  3003 */       if (this.parent == null) return null;
/*  3004 */       localObject = this.parent.getGraphics();
/*  3005 */       if (localObject == null) return null;
/*  3006 */       if ((localObject instanceof ConstrainableGraphics)) {
/*  3007 */         ((ConstrainableGraphics)localObject).constrain(this.x, this.y, this.width, this.height);
/*       */       } else {
/*  3009 */         ((Graphics)localObject).translate(this.x, this.y);
/*  3010 */         ((Graphics)localObject).setClip(0, 0, this.width, this.height);
/*       */       }
/*  3012 */       ((Graphics)localObject).setFont(getFont());
/*  3013 */       return localObject;
/*       */     }
/*  3015 */     Object localObject = this.peer;
/*  3016 */     return localObject != null ? ((ComponentPeer)localObject).getGraphics() : null;
/*       */   }
/*       */ 
/*       */   final Graphics getGraphics_NoClientCode()
/*       */   {
/*  3021 */     ComponentPeer localComponentPeer = this.peer;
/*  3022 */     if ((localComponentPeer instanceof LightweightPeer))
/*       */     {
/*  3026 */       Container localContainer = this.parent;
/*  3027 */       if (localContainer == null) return null;
/*  3028 */       Graphics localGraphics = localContainer.getGraphics_NoClientCode();
/*  3029 */       if (localGraphics == null) return null;
/*  3030 */       if ((localGraphics instanceof ConstrainableGraphics)) {
/*  3031 */         ((ConstrainableGraphics)localGraphics).constrain(this.x, this.y, this.width, this.height);
/*       */       } else {
/*  3033 */         localGraphics.translate(this.x, this.y);
/*  3034 */         localGraphics.setClip(0, 0, this.width, this.height);
/*       */       }
/*  3036 */       localGraphics.setFont(getFont_NoClientCode());
/*  3037 */       return localGraphics;
/*       */     }
/*  3039 */     return localComponentPeer != null ? localComponentPeer.getGraphics() : null;
/*       */   }
/*       */ 
/*       */   public FontMetrics getFontMetrics(Font paramFont)
/*       */   {
/*  3065 */     FontManager localFontManager = FontManagerFactory.getInstance();
/*  3066 */     if (((localFontManager instanceof SunFontManager)) && (((SunFontManager)localFontManager).usePlatformFontMetrics()))
/*       */     {
/*  3069 */       if ((this.peer != null) && (!(this.peer instanceof LightweightPeer)))
/*       */       {
/*  3071 */         return this.peer.getFontMetrics(paramFont);
/*       */       }
/*       */     }
/*  3074 */     return FontDesignMetrics.getMetrics(paramFont);
/*       */   }
/*       */ 
/*       */   public void setCursor(Cursor paramCursor)
/*       */   {
/*  3103 */     this.cursor = paramCursor;
/*  3104 */     updateCursorImmediately();
/*       */   }
/*       */ 
/*       */   final void updateCursorImmediately()
/*       */   {
/*  3112 */     if ((this.peer instanceof LightweightPeer)) {
/*  3113 */       Container localContainer = getNativeContainer();
/*       */ 
/*  3115 */       if (localContainer == null) return;
/*       */ 
/*  3117 */       ComponentPeer localComponentPeer = localContainer.getPeer();
/*       */ 
/*  3119 */       if (localComponentPeer != null)
/*  3120 */         localComponentPeer.updateCursorImmediately();
/*       */     }
/*  3122 */     else if (this.peer != null) {
/*  3123 */       this.peer.updateCursorImmediately();
/*       */     }
/*       */   }
/*       */ 
/*       */   public Cursor getCursor()
/*       */   {
/*  3136 */     return getCursor_NoClientCode();
/*       */   }
/*       */ 
/*       */   final Cursor getCursor_NoClientCode() {
/*  3140 */     Cursor localCursor = this.cursor;
/*  3141 */     if (localCursor != null) {
/*  3142 */       return localCursor;
/*       */     }
/*  3144 */     Container localContainer = this.parent;
/*  3145 */     if (localContainer != null) {
/*  3146 */       return localContainer.getCursor_NoClientCode();
/*       */     }
/*  3148 */     return Cursor.getPredefinedCursor(0);
/*       */   }
/*       */ 
/*       */   public boolean isCursorSet()
/*       */   {
/*  3162 */     return this.cursor != null;
/*       */   }
/*       */ 
/*       */   public void paint(Graphics paramGraphics)
/*       */   {
/*       */   }
/*       */ 
/*       */   public void update(Graphics paramGraphics)
/*       */   {
/*  3226 */     paint(paramGraphics);
/*       */   }
/*       */ 
/*       */   public void paintAll(Graphics paramGraphics)
/*       */   {
/*  3242 */     if (isShowing())
/*  3243 */       GraphicsCallback.PeerPaintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), paramGraphics, paramGraphics.getClip(), 3);
/*       */   }
/*       */ 
/*       */   void lightweightPaint(Graphics paramGraphics)
/*       */   {
/*  3258 */     paint(paramGraphics);
/*       */   }
/*       */ 
/*       */   void paintHeavyweightComponents(Graphics paramGraphics)
/*       */   {
/*       */   }
/*       */ 
/*       */   public void repaint()
/*       */   {
/*  3286 */     repaint(0L, 0, 0, this.width, this.height);
/*       */   }
/*       */ 
/*       */   public void repaint(long paramLong)
/*       */   {
/*  3305 */     repaint(paramLong, 0, 0, this.width, this.height);
/*       */   }
/*       */ 
/*       */   public void repaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */   {
/*  3329 */     repaint(0L, paramInt1, paramInt2, paramInt3, paramInt4);
/*       */   }
/*       */ 
/*       */   public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */   {
/*  3355 */     if ((this.peer instanceof LightweightPeer))
/*       */     {
/*  3360 */       if (this.parent != null) {
/*  3361 */         if (paramInt1 < 0) {
/*  3362 */           paramInt3 += paramInt1;
/*  3363 */           paramInt1 = 0;
/*       */         }
/*  3365 */         if (paramInt2 < 0) {
/*  3366 */           paramInt4 += paramInt2;
/*  3367 */           paramInt2 = 0;
/*       */         }
/*       */ 
/*  3370 */         int i = paramInt3 > this.width ? this.width : paramInt3;
/*  3371 */         int j = paramInt4 > this.height ? this.height : paramInt4;
/*       */ 
/*  3373 */         if ((i <= 0) || (j <= 0)) {
/*  3374 */           return;
/*       */         }
/*       */ 
/*  3377 */         int k = this.x + paramInt1;
/*  3378 */         int m = this.y + paramInt2;
/*  3379 */         this.parent.repaint(paramLong, k, m, i, j);
/*       */       }
/*       */     }
/*  3382 */     else if ((isVisible()) && (this.peer != null) && (paramInt3 > 0) && (paramInt4 > 0))
/*       */     {
/*  3384 */       PaintEvent localPaintEvent = new PaintEvent(this, 801, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
/*       */ 
/*  3386 */       Toolkit.getEventQueue().postEvent(localPaintEvent);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void print(Graphics paramGraphics)
/*       */   {
/*  3408 */     paint(paramGraphics);
/*       */   }
/*       */ 
/*       */   public void printAll(Graphics paramGraphics)
/*       */   {
/*  3423 */     if (isShowing())
/*  3424 */       GraphicsCallback.PeerPrintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), paramGraphics, paramGraphics.getClip(), 3);
/*       */   }
/*       */ 
/*       */   void lightweightPrint(Graphics paramGraphics)
/*       */   {
/*  3439 */     print(paramGraphics);
/*       */   }
/*       */ 
/*       */   void printHeavyweightComponents(Graphics paramGraphics)
/*       */   {
/*       */   }
/*       */ 
/*       */   private Insets getInsets_NoClientCode()
/*       */   {
/*  3449 */     ComponentPeer localComponentPeer = this.peer;
/*  3450 */     if ((localComponentPeer instanceof ContainerPeer)) {
/*  3451 */       return (Insets)((ContainerPeer)localComponentPeer).getInsets().clone();
/*       */     }
/*  3453 */     return new Insets(0, 0, 0, 0);
/*       */   }
/*       */ 
/*       */   public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*       */   {
/*  3504 */     int i = -1;
/*  3505 */     if ((paramInt1 & 0x30) != 0) {
/*  3506 */       i = 0;
/*  3507 */     } else if (((paramInt1 & 0x8) != 0) && 
/*  3508 */       (isInc)) {
/*  3509 */       i = incRate;
/*  3510 */       if (i < 0) {
/*  3511 */         i = 0;
/*       */       }
/*       */     }
/*       */ 
/*  3515 */     if (i >= 0) {
/*  3516 */       repaint(i, 0, 0, this.width, this.height);
/*       */     }
/*  3518 */     return (paramInt1 & 0xA0) == 0;
/*       */   }
/*       */ 
/*       */   public Image createImage(ImageProducer paramImageProducer)
/*       */   {
/*  3528 */     ComponentPeer localComponentPeer = this.peer;
/*  3529 */     if ((localComponentPeer != null) && (!(localComponentPeer instanceof LightweightPeer))) {
/*  3530 */       return localComponentPeer.createImage(paramImageProducer);
/*       */     }
/*  3532 */     return getToolkit().createImage(paramImageProducer);
/*       */   }
/*       */ 
/*       */   public Image createImage(int paramInt1, int paramInt2)
/*       */   {
/*  3550 */     ComponentPeer localComponentPeer = this.peer;
/*  3551 */     if ((localComponentPeer instanceof LightweightPeer)) {
/*  3552 */       if (this.parent != null) return this.parent.createImage(paramInt1, paramInt2);
/*  3553 */       return null;
/*       */     }
/*  3555 */     return localComponentPeer != null ? localComponentPeer.createImage(paramInt1, paramInt2) : null;
/*       */   }
/*       */ 
/*       */   public VolatileImage createVolatileImage(int paramInt1, int paramInt2)
/*       */   {
/*  3575 */     ComponentPeer localComponentPeer = this.peer;
/*  3576 */     if ((localComponentPeer instanceof LightweightPeer)) {
/*  3577 */       if (this.parent != null) {
/*  3578 */         return this.parent.createVolatileImage(paramInt1, paramInt2);
/*       */       }
/*  3580 */       return null;
/*       */     }
/*  3582 */     return localComponentPeer != null ? localComponentPeer.createVolatileImage(paramInt1, paramInt2) : null;
/*       */   }
/*       */ 
/*       */   public VolatileImage createVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities)
/*       */     throws AWTException
/*       */   {
/*  3605 */     return createVolatileImage(paramInt1, paramInt2);
/*       */   }
/*       */ 
/*       */   public boolean prepareImage(Image paramImage, ImageObserver paramImageObserver)
/*       */   {
/*  3621 */     return prepareImage(paramImage, -1, -1, paramImageObserver);
/*       */   }
/*       */ 
/*       */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*       */   {
/*  3644 */     ComponentPeer localComponentPeer = this.peer;
/*  3645 */     if ((localComponentPeer instanceof LightweightPeer)) {
/*  3646 */       return this.parent != null ? this.parent.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*       */     }
/*       */ 
/*  3650 */     return localComponentPeer != null ? localComponentPeer.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*       */   }
/*       */ 
/*       */   public int checkImage(Image paramImage, ImageObserver paramImageObserver)
/*       */   {
/*  3679 */     return checkImage(paramImage, -1, -1, paramImageObserver);
/*       */   }
/*       */ 
/*       */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*       */   {
/*  3716 */     ComponentPeer localComponentPeer = this.peer;
/*  3717 */     if ((localComponentPeer instanceof LightweightPeer)) {
/*  3718 */       return this.parent != null ? this.parent.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*       */     }
/*       */ 
/*  3722 */     return localComponentPeer != null ? localComponentPeer.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*       */   }
/*       */ 
/*       */   void createBufferStrategy(int paramInt)
/*       */   {
/*  3750 */     if (paramInt > 1)
/*       */     {
/*  3752 */       localBufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.UNDEFINED);
/*       */       try
/*       */       {
/*  3756 */         createBufferStrategy(paramInt, localBufferCapabilities);
/*  3757 */         return;
/*       */       }
/*       */       catch (AWTException localAWTException1)
/*       */       {
/*       */       }
/*       */     }
/*  3763 */     BufferCapabilities localBufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), null);
/*       */     try
/*       */     {
/*  3767 */       createBufferStrategy(paramInt, localBufferCapabilities);
/*  3768 */       return;
/*       */     }
/*       */     catch (AWTException localAWTException2)
/*       */     {
/*  3773 */       localBufferCapabilities = new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), null);
/*       */       try
/*       */       {
/*  3777 */         createBufferStrategy(paramInt, localBufferCapabilities);
/*  3778 */         return;
/*       */       }
/*       */       catch (AWTException localAWTException3)
/*       */       {
/*       */       }
/*       */     }
/*  3784 */     throw new InternalError("Could not create a buffer strategy");
/*       */   }
/*       */ 
/*       */   void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
/*       */     throws AWTException
/*       */   {
/*  3812 */     if (paramInt < 1) {
/*  3813 */       throw new IllegalArgumentException("Number of buffers must be at least 1");
/*       */     }
/*       */ 
/*  3816 */     if (paramBufferCapabilities == null) {
/*  3817 */       throw new IllegalArgumentException("No capabilities specified");
/*       */     }
/*       */ 
/*  3820 */     if (this.bufferStrategy != null) {
/*  3821 */       this.bufferStrategy.dispose();
/*       */     }
/*  3823 */     if (paramInt == 1) {
/*  3824 */       this.bufferStrategy = new SingleBufferStrategy(paramBufferCapabilities);
/*       */     } else {
/*  3826 */       SunGraphicsEnvironment localSunGraphicsEnvironment = (SunGraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
/*       */ 
/*  3828 */       if ((!paramBufferCapabilities.isPageFlipping()) && (localSunGraphicsEnvironment.isFlipStrategyPreferred(this.peer))) {
/*  3829 */         paramBufferCapabilities = new ProxyCapabilities(paramBufferCapabilities, null);
/*       */       }
/*       */ 
/*  3832 */       if (paramBufferCapabilities.isPageFlipping())
/*  3833 */         this.bufferStrategy = new FlipSubRegionBufferStrategy(paramInt, paramBufferCapabilities);
/*       */       else
/*  3835 */         this.bufferStrategy = new BltSubRegionBufferStrategy(paramInt, paramBufferCapabilities);
/*       */     }
/*       */   }
/*       */ 
/*       */   BufferStrategy getBufferStrategy()
/*       */   {
/*  3866 */     return this.bufferStrategy;
/*       */   }
/*       */ 
/*       */   Image getBackBuffer()
/*       */   {
/*  3875 */     if (this.bufferStrategy != null)
/*       */     {
/*       */       Object localObject;
/*  3876 */       if ((this.bufferStrategy instanceof BltBufferStrategy)) {
/*  3877 */         localObject = (BltBufferStrategy)this.bufferStrategy;
/*  3878 */         return ((BltBufferStrategy)localObject).getBackBuffer();
/*  3879 */       }if ((this.bufferStrategy instanceof FlipBufferStrategy)) {
/*  3880 */         localObject = (FlipBufferStrategy)this.bufferStrategy;
/*  3881 */         return ((FlipBufferStrategy)localObject).getBackBuffer();
/*       */       }
/*       */     }
/*  3884 */     return null;
/*       */   }
/*       */ 
/*       */   public void setIgnoreRepaint(boolean paramBoolean)
/*       */   {
/*  4577 */     this.ignoreRepaint = paramBoolean;
/*       */   }
/*       */ 
/*       */   public boolean getIgnoreRepaint()
/*       */   {
/*  4588 */     return this.ignoreRepaint;
/*       */   }
/*       */ 
/*       */   public boolean contains(int paramInt1, int paramInt2)
/*       */   {
/*  4601 */     return inside(paramInt1, paramInt2);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean inside(int paramInt1, int paramInt2)
/*       */   {
/*  4610 */     return (paramInt1 >= 0) && (paramInt1 < this.width) && (paramInt2 >= 0) && (paramInt2 < this.height);
/*       */   }
/*       */ 
/*       */   public boolean contains(Point paramPoint)
/*       */   {
/*  4623 */     return contains(paramPoint.x, paramPoint.y);
/*       */   }
/*       */ 
/*       */   public Component getComponentAt(int paramInt1, int paramInt2)
/*       */   {
/*  4648 */     return locate(paramInt1, paramInt2);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public Component locate(int paramInt1, int paramInt2)
/*       */   {
/*  4657 */     return contains(paramInt1, paramInt2) ? this : null;
/*       */   }
/*       */ 
/*       */   public Component getComponentAt(Point paramPoint)
/*       */   {
/*  4668 */     return getComponentAt(paramPoint.x, paramPoint.y);
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void deliverEvent(Event paramEvent)
/*       */   {
/*  4677 */     postEvent(paramEvent);
/*       */   }
/*       */ 
/*       */   public final void dispatchEvent(AWTEvent paramAWTEvent)
/*       */   {
/*  4687 */     dispatchEventImpl(paramAWTEvent);
/*       */   }
/*       */ 
/*       */   void dispatchEventImpl(AWTEvent paramAWTEvent) {
/*  4691 */     int i = paramAWTEvent.getID();
/*       */ 
/*  4694 */     AppContext localAppContext = this.appContext;
/*  4695 */     if ((localAppContext != null) && (!localAppContext.equals(AppContext.getAppContext())) && 
/*  4696 */       (eventLog.isLoggable(500))) {
/*  4697 */       eventLog.fine("Event " + paramAWTEvent + " is being dispatched on the wrong AppContext");
/*       */     }
/*       */ 
/*  4701 */     if (eventLog.isLoggable(300)) {
/*  4702 */       eventLog.finest("{0}", new Object[] { paramAWTEvent });
/*       */     }
/*       */ 
/*  4708 */     EventQueue.setCurrentEventAndMostRecentTime(paramAWTEvent);
/*       */ 
/*  4715 */     if ((paramAWTEvent instanceof SunDropTargetEvent)) {
/*  4716 */       ((SunDropTargetEvent)paramAWTEvent).dispatch();
/*  4717 */       return;
/*       */     }
/*       */ 
/*  4720 */     if (!paramAWTEvent.focusManagerIsDispatching)
/*       */     {
/*  4723 */       if (paramAWTEvent.isPosted) {
/*  4724 */         paramAWTEvent = KeyboardFocusManager.retargetFocusEvent(paramAWTEvent);
/*  4725 */         paramAWTEvent.isPosted = true;
/*       */       }
/*       */ 
/*  4731 */       if (KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchEvent(paramAWTEvent))
/*       */       {
/*  4734 */         return;
/*       */       }
/*       */     }
/*  4737 */     if (((paramAWTEvent instanceof FocusEvent)) && (focusLog.isLoggable(300))) {
/*  4738 */       focusLog.finest("" + paramAWTEvent);
/*       */     }
/*       */ 
/*  4745 */     if ((i == 507) && (!eventTypeEnabled(i)) && (this.peer != null) && (!this.peer.handlesWheelScrolling()) && (dispatchMouseWheelToAncestor((MouseWheelEvent)paramAWTEvent)))
/*       */     {
/*  4750 */       return;
/*       */     }
/*       */ 
/*  4756 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  4757 */     localToolkit.notifyAWTEventListeners(paramAWTEvent);
/*       */ 
/*  4764 */     if ((!paramAWTEvent.isConsumed()) && 
/*  4765 */       ((paramAWTEvent instanceof KeyEvent))) {
/*  4766 */       KeyboardFocusManager.getCurrentKeyboardFocusManager().processKeyEvent(this, (KeyEvent)paramAWTEvent);
/*       */ 
/*  4768 */       if (paramAWTEvent.isConsumed())
/*       */         return;
/*       */     }
/*       */     Object localObject;
/*  4777 */     if (areInputMethodsEnabled())
/*       */     {
/*  4782 */       if ((((paramAWTEvent instanceof InputMethodEvent)) && (!(this instanceof CompositionArea))) || ((paramAWTEvent instanceof InputEvent)) || ((paramAWTEvent instanceof FocusEvent)))
/*       */       {
/*  4789 */         localObject = getInputContext();
/*       */ 
/*  4792 */         if (localObject != null) {
/*  4793 */           ((java.awt.im.InputContext)localObject).dispatchEvent(paramAWTEvent);
/*  4794 */           if (paramAWTEvent.isConsumed()) {
/*  4795 */             if (((paramAWTEvent instanceof FocusEvent)) && (focusLog.isLoggable(300))) {
/*  4796 */               focusLog.finest("3579: Skipping " + paramAWTEvent);
/*       */             }
/*  4798 */             return;
/*       */           }
/*       */ 
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*       */     }
/*  4806 */     else if (i == 1004) {
/*  4807 */       localObject = getInputContext();
/*  4808 */       if ((localObject != null) && ((localObject instanceof sun.awt.im.InputContext))) {
/*  4809 */         ((sun.awt.im.InputContext)localObject).disableNativeIM();
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  4818 */     switch (i)
/*       */     {
/*       */     case 401:
/*       */     case 402:
/*  4826 */       localObject = (Container)((this instanceof Container) ? this : this.parent);
/*  4827 */       if (localObject != null) {
/*  4828 */         ((Container)localObject).preProcessKeyEvent((KeyEvent)paramAWTEvent);
/*  4829 */         if (paramAWTEvent.isConsumed()) {
/*  4830 */           if (focusLog.isLoggable(300)) {
/*  4831 */             focusLog.finest("Pre-process consumed event");
/*       */           }
/*       */           return;
/*       */         }
/*       */       }
/*       */ 
/*       */       break;
/*       */     case 201:
/*  4839 */       if ((localToolkit instanceof WindowClosingListener)) {
/*  4840 */         this.windowClosingException = ((WindowClosingListener)localToolkit).windowClosingNotify((WindowEvent)paramAWTEvent);
/*       */ 
/*  4842 */         if (checkWindowClosingException())
/*       */         {
/*       */           return;
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*       */       break;
/*       */     }
/*       */ 
/*  4855 */     if (this.newEventsOnly)
/*       */     {
/*  4860 */       if (eventEnabled(paramAWTEvent))
/*  4861 */         processEvent(paramAWTEvent);
/*       */     }
/*  4863 */     else if (i == 507)
/*       */     {
/*  4867 */       autoProcessMouseWheel((MouseWheelEvent)paramAWTEvent);
/*  4868 */     } else if ((!(paramAWTEvent instanceof MouseEvent)) || (postsOldMouseEvents()))
/*       */     {
/*  4872 */       localObject = paramAWTEvent.convertToOld();
/*  4873 */       if (localObject != null) {
/*  4874 */         int j = ((Event)localObject).key;
/*  4875 */         int k = ((Event)localObject).modifiers;
/*       */ 
/*  4877 */         postEvent((Event)localObject);
/*  4878 */         if (((Event)localObject).isConsumed()) {
/*  4879 */           paramAWTEvent.consume();
/*       */         }
/*       */ 
/*  4884 */         switch (((Event)localObject).id) {
/*       */         case 401:
/*       */         case 402:
/*       */         case 403:
/*       */         case 404:
/*  4889 */           if (((Event)localObject).key != j) {
/*  4890 */             ((KeyEvent)paramAWTEvent).setKeyChar(((Event)localObject).getKeyEventChar());
/*       */           }
/*  4892 */           if (((Event)localObject).modifiers != k)
/*  4893 */             ((KeyEvent)paramAWTEvent).setModifiers(((Event)localObject).modifiers); break;
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  4906 */     if ((i == 201) && (!paramAWTEvent.isConsumed()) && 
/*  4907 */       ((localToolkit instanceof WindowClosingListener))) {
/*  4908 */       this.windowClosingException = ((WindowClosingListener)localToolkit).windowClosingDelivered((WindowEvent)paramAWTEvent);
/*       */ 
/*  4911 */       if (checkWindowClosingException()) {
/*  4912 */         return;
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  4923 */     if (!(paramAWTEvent instanceof KeyEvent)) {
/*  4924 */       localObject = this.peer;
/*  4925 */       if (((paramAWTEvent instanceof FocusEvent)) && ((localObject == null) || ((localObject instanceof LightweightPeer))))
/*       */       {
/*  4928 */         Component localComponent = (Component)paramAWTEvent.getSource();
/*  4929 */         if (localComponent != null) {
/*  4930 */           Container localContainer = localComponent.getNativeContainer();
/*  4931 */           if (localContainer != null) {
/*  4932 */             localObject = localContainer.getPeer();
/*       */           }
/*       */         }
/*       */       }
/*  4936 */       if (localObject != null)
/*  4937 */         ((ComponentPeer)localObject).handleEvent(paramAWTEvent);
/*       */     }
/*       */   }
/*       */ 
/*       */   void autoProcessMouseWheel(MouseWheelEvent paramMouseWheelEvent)
/*       */   {
/*       */   }
/*       */ 
/*       */   boolean dispatchMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent)
/*       */   {
/*  4957 */     int i = paramMouseWheelEvent.getX() + getX();
/*  4958 */     int j = paramMouseWheelEvent.getY() + getY();
/*       */ 
/*  4963 */     if (eventLog.isLoggable(300)) {
/*  4964 */       eventLog.finest("dispatchMouseWheelToAncestor");
/*  4965 */       eventLog.finest("orig event src is of " + paramMouseWheelEvent.getSource().getClass());
/*       */     }
/*       */ 
/*  4971 */     synchronized (getTreeLock()) {
/*  4972 */       Container localContainer = getParent();
/*  4973 */       while ((localContainer != null) && (!localContainer.eventEnabled(paramMouseWheelEvent)))
/*       */       {
/*  4975 */         i += localContainer.getX();
/*  4976 */         j += localContainer.getY();
/*       */ 
/*  4978 */         if ((localContainer instanceof Window)) break;
/*  4979 */         localContainer = localContainer.getParent();
/*       */       }
/*       */ 
/*  4986 */       if (eventLog.isLoggable(300)) {
/*  4987 */         eventLog.finest("new event src is " + localContainer.getClass());
/*       */       }
/*       */ 
/*  4990 */       if ((localContainer != null) && (localContainer.eventEnabled(paramMouseWheelEvent)))
/*       */       {
/*  4994 */         MouseWheelEvent localMouseWheelEvent = new MouseWheelEvent(localContainer, paramMouseWheelEvent.getID(), paramMouseWheelEvent.getWhen(), paramMouseWheelEvent.getModifiers(), i, j, paramMouseWheelEvent.getXOnScreen(), paramMouseWheelEvent.getYOnScreen(), paramMouseWheelEvent.getClickCount(), paramMouseWheelEvent.isPopupTrigger(), paramMouseWheelEvent.getScrollType(), paramMouseWheelEvent.getScrollAmount(), paramMouseWheelEvent.getWheelRotation(), paramMouseWheelEvent.getPreciseWheelRotation());
/*       */ 
/*  5008 */         paramMouseWheelEvent.copyPrivateDataInto(localMouseWheelEvent);
/*       */ 
/*  5014 */         localContainer.dispatchEventToSelf(localMouseWheelEvent);
/*  5015 */         if (localMouseWheelEvent.isConsumed()) {
/*  5016 */           paramMouseWheelEvent.consume();
/*       */         }
/*  5018 */         return true;
/*       */       }
/*       */     }
/*  5021 */     return false;
/*       */   }
/*       */ 
/*       */   boolean checkWindowClosingException() {
/*  5025 */     if (this.windowClosingException != null) {
/*  5026 */       if ((this instanceof Dialog)) {
/*  5027 */         ((Dialog)this).interruptBlocking();
/*       */       } else {
/*  5029 */         this.windowClosingException.fillInStackTrace();
/*  5030 */         this.windowClosingException.printStackTrace();
/*  5031 */         this.windowClosingException = null;
/*       */       }
/*  5033 */       return true;
/*       */     }
/*  5035 */     return false;
/*       */   }
/*       */ 
/*       */   boolean areInputMethodsEnabled()
/*       */   {
/*  5042 */     return ((this.eventMask & 0x1000) != 0L) && (((this.eventMask & 0x8) != 0L) || (this.keyListener != null));
/*       */   }
/*       */ 
/*       */   boolean eventEnabled(AWTEvent paramAWTEvent)
/*       */   {
/*  5048 */     return eventTypeEnabled(paramAWTEvent.id);
/*       */   }
/*       */ 
/*       */   boolean eventTypeEnabled(int paramInt) {
/*  5052 */     switch (paramInt) {
/*       */     case 100:
/*       */     case 101:
/*       */     case 102:
/*       */     case 103:
/*  5057 */       if (((this.eventMask & 1L) != 0L) || (this.componentListener != null))
/*       */       {
/*  5059 */         return true;
/*       */       }
/*       */       break;
/*       */     case 1004:
/*       */     case 1005:
/*  5064 */       if (((this.eventMask & 0x4) != 0L) || (this.focusListener != null))
/*       */       {
/*  5066 */         return true;
/*       */       }
/*       */       break;
/*       */     case 400:
/*       */     case 401:
/*       */     case 402:
/*  5072 */       if (((this.eventMask & 0x8) != 0L) || (this.keyListener != null))
/*       */       {
/*  5074 */         return true;
/*       */       }
/*       */       break;
/*       */     case 500:
/*       */     case 501:
/*       */     case 502:
/*       */     case 504:
/*       */     case 505:
/*  5082 */       if (((this.eventMask & 0x10) != 0L) || (this.mouseListener != null))
/*       */       {
/*  5084 */         return true;
/*       */       }
/*       */       break;
/*       */     case 503:
/*       */     case 506:
/*  5089 */       if (((this.eventMask & 0x20) != 0L) || (this.mouseMotionListener != null))
/*       */       {
/*  5091 */         return true;
/*       */       }
/*       */       break;
/*       */     case 507:
/*  5095 */       if (((this.eventMask & 0x20000) != 0L) || (this.mouseWheelListener != null))
/*       */       {
/*  5097 */         return true;
/*       */       }
/*       */       break;
/*       */     case 1100:
/*       */     case 1101:
/*  5102 */       if (((this.eventMask & 0x800) != 0L) || (this.inputMethodListener != null))
/*       */       {
/*  5104 */         return true;
/*       */       }
/*       */       break;
/*       */     case 1400:
/*  5108 */       if (((this.eventMask & 0x8000) != 0L) || (this.hierarchyListener != null))
/*       */       {
/*  5110 */         return true;
/*       */       }
/*       */       break;
/*       */     case 1401:
/*       */     case 1402:
/*  5115 */       if (((this.eventMask & 0x10000) != 0L) || (this.hierarchyBoundsListener != null))
/*       */       {
/*  5117 */         return true;
/*       */       }
/*       */       break;
/*       */     case 1001:
/*  5121 */       if ((this.eventMask & 0x80) != 0L) {
/*  5122 */         return true;
/*       */       }
/*       */       break;
/*       */     case 900:
/*  5126 */       if ((this.eventMask & 0x400) != 0L) {
/*  5127 */         return true;
/*       */       }
/*       */       break;
/*       */     case 701:
/*  5131 */       if ((this.eventMask & 0x200) != 0L) {
/*  5132 */         return true;
/*       */       }
/*       */       break;
/*       */     case 601:
/*  5136 */       if ((this.eventMask & 0x100) != 0L) {
/*  5137 */         return true;
/*       */       }
/*       */ 
/*       */       break;
/*       */     }
/*       */ 
/*  5146 */     if (paramInt > 1999) {
/*  5147 */       return true;
/*       */     }
/*  5149 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean postEvent(Event paramEvent)
/*       */   {
/*  5158 */     ComponentPeer localComponentPeer = this.peer;
/*       */ 
/*  5160 */     if (handleEvent(paramEvent)) {
/*  5161 */       paramEvent.consume();
/*  5162 */       return true;
/*       */     }
/*       */ 
/*  5165 */     Container localContainer = this.parent;
/*  5166 */     int i = paramEvent.x;
/*  5167 */     int j = paramEvent.y;
/*  5168 */     if (localContainer != null) {
/*  5169 */       paramEvent.translate(this.x, this.y);
/*  5170 */       if (localContainer.postEvent(paramEvent)) {
/*  5171 */         paramEvent.consume();
/*  5172 */         return true;
/*       */       }
/*       */ 
/*  5175 */       paramEvent.x = i;
/*  5176 */       paramEvent.y = j;
/*       */     }
/*  5178 */     return false;
/*       */   }
/*       */ 
/*       */   public synchronized void addComponentListener(ComponentListener paramComponentListener)
/*       */   {
/*  5199 */     if (paramComponentListener == null) {
/*  5200 */       return;
/*       */     }
/*  5202 */     this.componentListener = AWTEventMulticaster.add(this.componentListener, paramComponentListener);
/*  5203 */     this.newEventsOnly = true;
/*       */   }
/*       */ 
/*       */   public synchronized void removeComponentListener(ComponentListener paramComponentListener)
/*       */   {
/*  5223 */     if (paramComponentListener == null) {
/*  5224 */       return;
/*       */     }
/*  5226 */     this.componentListener = AWTEventMulticaster.remove(this.componentListener, paramComponentListener);
/*       */   }
/*       */ 
/*       */   public synchronized ComponentListener[] getComponentListeners()
/*       */   {
/*  5242 */     return (ComponentListener[])getListeners(ComponentListener.class);
/*       */   }
/*       */ 
/*       */   public synchronized void addFocusListener(FocusListener paramFocusListener)
/*       */   {
/*  5261 */     if (paramFocusListener == null) {
/*  5262 */       return;
/*       */     }
/*  5264 */     this.focusListener = AWTEventMulticaster.add(this.focusListener, paramFocusListener);
/*  5265 */     this.newEventsOnly = true;
/*       */ 
/*  5269 */     if ((this.peer instanceof LightweightPeer))
/*  5270 */       this.parent.proxyEnableEvents(4L);
/*       */   }
/*       */ 
/*       */   public synchronized void removeFocusListener(FocusListener paramFocusListener)
/*       */   {
/*  5292 */     if (paramFocusListener == null) {
/*  5293 */       return;
/*       */     }
/*  5295 */     this.focusListener = AWTEventMulticaster.remove(this.focusListener, paramFocusListener);
/*       */   }
/*       */ 
/*       */   public synchronized FocusListener[] getFocusListeners()
/*       */   {
/*  5311 */     return (FocusListener[])getListeners(FocusListener.class);
/*       */   }
/*       */ 
/*       */   public void addHierarchyListener(HierarchyListener paramHierarchyListener)
/*       */   {
/*  5331 */     if (paramHierarchyListener == null)
/*       */       return;
/*       */     int i;
/*  5335 */     synchronized (this) {
/*  5336 */       i = (this.hierarchyListener == null) && ((this.eventMask & 0x8000) == 0L) ? 1 : 0;
/*       */ 
/*  5339 */       this.hierarchyListener = AWTEventMulticaster.add(this.hierarchyListener, paramHierarchyListener);
/*  5340 */       i = (i != 0) && (this.hierarchyListener != null) ? 1 : 0;
/*  5341 */       this.newEventsOnly = true;
/*       */     }
/*  5343 */     if (i != 0)
/*  5344 */       synchronized (getTreeLock()) {
/*  5345 */         adjustListeningChildrenOnParent(32768L, 1);
/*       */       }
/*       */   }
/*       */ 
/*       */   public void removeHierarchyListener(HierarchyListener paramHierarchyListener)
/*       */   {
/*  5369 */     if (paramHierarchyListener == null)
/*       */       return;
/*       */     int i;
/*  5373 */     synchronized (this) {
/*  5374 */       i = (this.hierarchyListener != null) && ((this.eventMask & 0x8000) == 0L) ? 1 : 0;
/*       */ 
/*  5377 */       this.hierarchyListener = AWTEventMulticaster.remove(this.hierarchyListener, paramHierarchyListener);
/*       */ 
/*  5379 */       i = (i != 0) && (this.hierarchyListener == null) ? 1 : 0;
/*       */     }
/*  5381 */     if (i != 0)
/*  5382 */       synchronized (getTreeLock()) {
/*  5383 */         adjustListeningChildrenOnParent(32768L, -1);
/*       */       }
/*       */   }
/*       */ 
/*       */   public synchronized HierarchyListener[] getHierarchyListeners()
/*       */   {
/*  5402 */     return (HierarchyListener[])getListeners(HierarchyListener.class);
/*       */   }
/*       */ 
/*       */   public void addHierarchyBoundsListener(HierarchyBoundsListener paramHierarchyBoundsListener)
/*       */   {
/*  5422 */     if (paramHierarchyBoundsListener == null)
/*       */       return;
/*       */     int i;
/*  5426 */     synchronized (this) {
/*  5427 */       i = (this.hierarchyBoundsListener == null) && ((this.eventMask & 0x10000) == 0L) ? 1 : 0;
/*       */ 
/*  5430 */       this.hierarchyBoundsListener = AWTEventMulticaster.add(this.hierarchyBoundsListener, paramHierarchyBoundsListener);
/*       */ 
/*  5432 */       i = (i != 0) && (this.hierarchyBoundsListener != null) ? 1 : 0;
/*       */ 
/*  5434 */       this.newEventsOnly = true;
/*       */     }
/*  5436 */     if (i != 0)
/*  5437 */       synchronized (getTreeLock()) {
/*  5438 */         adjustListeningChildrenOnParent(65536L, 1);
/*       */       }
/*       */   }
/*       */ 
/*       */   public void removeHierarchyBoundsListener(HierarchyBoundsListener paramHierarchyBoundsListener)
/*       */   {
/*  5462 */     if (paramHierarchyBoundsListener == null)
/*       */       return;
/*       */     int i;
/*  5466 */     synchronized (this) {
/*  5467 */       i = (this.hierarchyBoundsListener != null) && ((this.eventMask & 0x10000) == 0L) ? 1 : 0;
/*       */ 
/*  5470 */       this.hierarchyBoundsListener = AWTEventMulticaster.remove(this.hierarchyBoundsListener, paramHierarchyBoundsListener);
/*       */ 
/*  5472 */       i = (i != 0) && (this.hierarchyBoundsListener == null) ? 1 : 0;
/*       */     }
/*       */ 
/*  5475 */     if (i != 0)
/*  5476 */       synchronized (getTreeLock()) {
/*  5477 */         adjustListeningChildrenOnParent(65536L, -1);
/*       */       }
/*       */   }
/*       */ 
/*       */   int numListening(long paramLong)
/*       */   {
/*  5486 */     if ((eventLog.isLoggable(500)) && 
/*  5487 */       (paramLong != 32768L) && (paramLong != 65536L))
/*       */     {
/*  5490 */       eventLog.fine("Assertion failed");
/*       */     }
/*       */ 
/*  5493 */     if (((paramLong == 32768L) && ((this.hierarchyListener != null) || ((this.eventMask & 0x8000) != 0L))) || ((paramLong == 65536L) && ((this.hierarchyBoundsListener != null) || ((this.eventMask & 0x10000) != 0L))))
/*       */     {
/*  5499 */       return 1;
/*       */     }
/*  5501 */     return 0;
/*       */   }
/*       */ 
/*       */   int countHierarchyMembers()
/*       */   {
/*  5507 */     return 1;
/*       */   }
/*       */ 
/*       */   int createHierarchyEvents(int paramInt, Component paramComponent, Container paramContainer, long paramLong, boolean paramBoolean)
/*       */   {
/*       */     HierarchyEvent localHierarchyEvent;
/*  5513 */     switch (paramInt) {
/*       */     case 1400:
/*  5515 */       if ((this.hierarchyListener != null) || ((this.eventMask & 0x8000) != 0L) || (paramBoolean))
/*       */       {
/*  5518 */         localHierarchyEvent = new HierarchyEvent(this, paramInt, paramComponent, paramContainer, paramLong);
/*       */ 
/*  5521 */         dispatchEvent(localHierarchyEvent);
/*  5522 */         return 1;
/*       */       }
/*       */       break;
/*       */     case 1401:
/*       */     case 1402:
/*  5527 */       if ((eventLog.isLoggable(500)) && 
/*  5528 */         (paramLong != 0L)) {
/*  5529 */         eventLog.fine("Assertion (changeFlags == 0) failed");
/*       */       }
/*       */ 
/*  5532 */       if ((this.hierarchyBoundsListener != null) || ((this.eventMask & 0x10000) != 0L) || (paramBoolean))
/*       */       {
/*  5535 */         localHierarchyEvent = new HierarchyEvent(this, paramInt, paramComponent, paramContainer);
/*       */ 
/*  5537 */         dispatchEvent(localHierarchyEvent);
/*  5538 */         return 1;
/*       */       }
/*       */ 
/*       */       break;
/*       */     default:
/*  5543 */       if (eventLog.isLoggable(500)) {
/*  5544 */         eventLog.fine("This code must never be reached");
/*       */       }
/*       */       break;
/*       */     }
/*  5548 */     return 0;
/*       */   }
/*       */ 
/*       */   public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners()
/*       */   {
/*  5564 */     return (HierarchyBoundsListener[])getListeners(HierarchyBoundsListener.class);
/*       */   }
/*       */ 
/*       */   void adjustListeningChildrenOnParent(long paramLong, int paramInt)
/*       */   {
/*  5574 */     if (this.parent != null)
/*  5575 */       this.parent.adjustListeningChildren(paramLong, paramInt);
/*       */   }
/*       */ 
/*       */   public synchronized void addKeyListener(KeyListener paramKeyListener)
/*       */   {
/*  5594 */     if (paramKeyListener == null) {
/*  5595 */       return;
/*       */     }
/*  5597 */     this.keyListener = AWTEventMulticaster.add(this.keyListener, paramKeyListener);
/*  5598 */     this.newEventsOnly = true;
/*       */ 
/*  5602 */     if ((this.peer instanceof LightweightPeer))
/*  5603 */       this.parent.proxyEnableEvents(8L);
/*       */   }
/*       */ 
/*       */   public synchronized void removeKeyListener(KeyListener paramKeyListener)
/*       */   {
/*  5625 */     if (paramKeyListener == null) {
/*  5626 */       return;
/*       */     }
/*  5628 */     this.keyListener = AWTEventMulticaster.remove(this.keyListener, paramKeyListener);
/*       */   }
/*       */ 
/*       */   public synchronized KeyListener[] getKeyListeners()
/*       */   {
/*  5644 */     return (KeyListener[])getListeners(KeyListener.class);
/*       */   }
/*       */ 
/*       */   public synchronized void addMouseListener(MouseListener paramMouseListener)
/*       */   {
/*  5663 */     if (paramMouseListener == null) {
/*  5664 */       return;
/*       */     }
/*  5666 */     this.mouseListener = AWTEventMulticaster.add(this.mouseListener, paramMouseListener);
/*  5667 */     this.newEventsOnly = true;
/*       */ 
/*  5671 */     if ((this.peer instanceof LightweightPeer))
/*  5672 */       this.parent.proxyEnableEvents(16L);
/*       */   }
/*       */ 
/*       */   public synchronized void removeMouseListener(MouseListener paramMouseListener)
/*       */   {
/*  5694 */     if (paramMouseListener == null) {
/*  5695 */       return;
/*       */     }
/*  5697 */     this.mouseListener = AWTEventMulticaster.remove(this.mouseListener, paramMouseListener);
/*       */   }
/*       */ 
/*       */   public synchronized MouseListener[] getMouseListeners()
/*       */   {
/*  5713 */     return (MouseListener[])getListeners(MouseListener.class);
/*       */   }
/*       */ 
/*       */   public synchronized void addMouseMotionListener(MouseMotionListener paramMouseMotionListener)
/*       */   {
/*  5732 */     if (paramMouseMotionListener == null) {
/*  5733 */       return;
/*       */     }
/*  5735 */     this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, paramMouseMotionListener);
/*  5736 */     this.newEventsOnly = true;
/*       */ 
/*  5740 */     if ((this.peer instanceof LightweightPeer))
/*  5741 */       this.parent.proxyEnableEvents(32L);
/*       */   }
/*       */ 
/*       */   public synchronized void removeMouseMotionListener(MouseMotionListener paramMouseMotionListener)
/*       */   {
/*  5763 */     if (paramMouseMotionListener == null) {
/*  5764 */       return;
/*       */     }
/*  5766 */     this.mouseMotionListener = AWTEventMulticaster.remove(this.mouseMotionListener, paramMouseMotionListener);
/*       */   }
/*       */ 
/*       */   public synchronized MouseMotionListener[] getMouseMotionListeners()
/*       */   {
/*  5782 */     return (MouseMotionListener[])getListeners(MouseMotionListener.class);
/*       */   }
/*       */ 
/*       */   public synchronized void addMouseWheelListener(MouseWheelListener paramMouseWheelListener)
/*       */   {
/*  5806 */     if (paramMouseWheelListener == null) {
/*  5807 */       return;
/*       */     }
/*  5809 */     this.mouseWheelListener = AWTEventMulticaster.add(this.mouseWheelListener, paramMouseWheelListener);
/*  5810 */     this.newEventsOnly = true;
/*       */ 
/*  5814 */     if ((this.peer instanceof LightweightPeer))
/*  5815 */       this.parent.proxyEnableEvents(131072L);
/*       */   }
/*       */ 
/*       */   public synchronized void removeMouseWheelListener(MouseWheelListener paramMouseWheelListener)
/*       */   {
/*  5836 */     if (paramMouseWheelListener == null) {
/*  5837 */       return;
/*       */     }
/*  5839 */     this.mouseWheelListener = AWTEventMulticaster.remove(this.mouseWheelListener, paramMouseWheelListener);
/*       */   }
/*       */ 
/*       */   public synchronized MouseWheelListener[] getMouseWheelListeners()
/*       */   {
/*  5855 */     return (MouseWheelListener[])getListeners(MouseWheelListener.class);
/*       */   }
/*       */ 
/*       */   public synchronized void addInputMethodListener(InputMethodListener paramInputMethodListener)
/*       */   {
/*  5878 */     if (paramInputMethodListener == null) {
/*  5879 */       return;
/*       */     }
/*  5881 */     this.inputMethodListener = AWTEventMulticaster.add(this.inputMethodListener, paramInputMethodListener);
/*  5882 */     this.newEventsOnly = true;
/*       */   }
/*       */ 
/*       */   public synchronized void removeInputMethodListener(InputMethodListener paramInputMethodListener)
/*       */   {
/*  5903 */     if (paramInputMethodListener == null) {
/*  5904 */       return;
/*       */     }
/*  5906 */     this.inputMethodListener = AWTEventMulticaster.remove(this.inputMethodListener, paramInputMethodListener);
/*       */   }
/*       */ 
/*       */   public synchronized InputMethodListener[] getInputMethodListeners()
/*       */   {
/*  5922 */     return (InputMethodListener[])getListeners(InputMethodListener.class);
/*       */   }
/*       */ 
/*       */   public <T extends EventListener> T[] getListeners(Class<T> paramClass)
/*       */   {
/*  5968 */     Object localObject = null;
/*  5969 */     if (paramClass == ComponentListener.class)
/*  5970 */       localObject = this.componentListener;
/*  5971 */     else if (paramClass == FocusListener.class)
/*  5972 */       localObject = this.focusListener;
/*  5973 */     else if (paramClass == HierarchyListener.class)
/*  5974 */       localObject = this.hierarchyListener;
/*  5975 */     else if (paramClass == HierarchyBoundsListener.class)
/*  5976 */       localObject = this.hierarchyBoundsListener;
/*  5977 */     else if (paramClass == KeyListener.class)
/*  5978 */       localObject = this.keyListener;
/*  5979 */     else if (paramClass == MouseListener.class)
/*  5980 */       localObject = this.mouseListener;
/*  5981 */     else if (paramClass == MouseMotionListener.class)
/*  5982 */       localObject = this.mouseMotionListener;
/*  5983 */     else if (paramClass == MouseWheelListener.class)
/*  5984 */       localObject = this.mouseWheelListener;
/*  5985 */     else if (paramClass == InputMethodListener.class)
/*  5986 */       localObject = this.inputMethodListener;
/*  5987 */     else if (paramClass == PropertyChangeListener.class) {
/*  5988 */       return (EventListener[])getPropertyChangeListeners();
/*       */     }
/*  5990 */     return AWTEventMulticaster.getListeners((EventListener)localObject, paramClass);
/*       */   }
/*       */ 
/*       */   public InputMethodRequests getInputMethodRequests()
/*       */   {
/*  6006 */     return null;
/*       */   }
/*       */ 
/*       */   public java.awt.im.InputContext getInputContext()
/*       */   {
/*  6021 */     Container localContainer = this.parent;
/*  6022 */     if (localContainer == null) {
/*  6023 */       return null;
/*       */     }
/*  6025 */     return localContainer.getInputContext();
/*       */   }
/*       */ 
/*       */   protected final void enableEvents(long paramLong)
/*       */   {
/*  6047 */     long l = 0L;
/*  6048 */     synchronized (this) {
/*  6049 */       if (((paramLong & 0x8000) != 0L) && (this.hierarchyListener == null) && ((this.eventMask & 0x8000) == 0L))
/*       */       {
/*  6052 */         l |= 32768L;
/*       */       }
/*  6054 */       if (((paramLong & 0x10000) != 0L) && (this.hierarchyBoundsListener == null) && ((this.eventMask & 0x10000) == 0L))
/*       */       {
/*  6057 */         l |= 65536L;
/*       */       }
/*  6059 */       this.eventMask |= paramLong;
/*  6060 */       this.newEventsOnly = true;
/*       */     }
/*       */ 
/*  6065 */     if ((this.peer instanceof LightweightPeer)) {
/*  6066 */       this.parent.proxyEnableEvents(this.eventMask);
/*       */     }
/*  6068 */     if (l != 0L)
/*  6069 */       synchronized (getTreeLock()) {
/*  6070 */         adjustListeningChildrenOnParent(l, 1);
/*       */       }
/*       */   }
/*       */ 
/*       */   protected final void disableEvents(long paramLong)
/*       */   {
/*  6083 */     long l = 0L;
/*  6084 */     synchronized (this) {
/*  6085 */       if (((paramLong & 0x8000) != 0L) && (this.hierarchyListener == null) && ((this.eventMask & 0x8000) != 0L))
/*       */       {
/*  6088 */         l |= 32768L;
/*       */       }
/*  6090 */       if (((paramLong & 0x10000) != 0L) && (this.hierarchyBoundsListener == null) && ((this.eventMask & 0x10000) != 0L))
/*       */       {
/*  6093 */         l |= 65536L;
/*       */       }
/*  6095 */       this.eventMask &= (paramLong ^ 0xFFFFFFFF);
/*       */     }
/*  6097 */     if (l != 0L)
/*  6098 */       synchronized (getTreeLock()) {
/*  6099 */         adjustListeningChildrenOnParent(l, -1);
/*       */       }
/*       */   }
/*       */ 
/*       */   private boolean checkCoalescing()
/*       */   {
/*  6129 */     if (getClass().getClassLoader() == null) {
/*  6130 */       return false;
/*       */     }
/*  6132 */     final Class localClass = getClass();
/*  6133 */     synchronized (coalesceMap)
/*       */     {
/*  6135 */       Boolean localBoolean1 = (Boolean)coalesceMap.get(localClass);
/*  6136 */       if (localBoolean1 != null) {
/*  6137 */         return localBoolean1.booleanValue();
/*       */       }
/*       */ 
/*  6141 */       Boolean localBoolean2 = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*       */       {
/*       */         public Boolean run() {
/*  6144 */           return Boolean.valueOf(Component.isCoalesceEventsOverriden(localClass));
/*       */         }
/*       */       });
/*  6148 */       coalesceMap.put(localClass, localBoolean2);
/*  6149 */       return localBoolean2.booleanValue();
/*       */     }
/*       */   }
/*       */ 
/*       */   private static boolean isCoalesceEventsOverriden(Class<?> paramClass)
/*       */   {
/*  6166 */     assert (Thread.holdsLock(coalesceMap));
/*       */ 
/*  6169 */     Class localClass = paramClass.getSuperclass();
/*  6170 */     if (localClass == null)
/*       */     {
/*  6173 */       return false;
/*       */     }
/*  6175 */     if (localClass.getClassLoader() != null) {
/*  6176 */       Boolean localBoolean = (Boolean)coalesceMap.get(localClass);
/*  6177 */       if (localBoolean == null)
/*       */       {
/*  6179 */         if (isCoalesceEventsOverriden(localClass)) {
/*  6180 */           coalesceMap.put(localClass, Boolean.valueOf(true));
/*  6181 */           return true;
/*       */         }
/*  6183 */       } else if (localBoolean.booleanValue()) {
/*  6184 */         return true;
/*       */       }
/*       */     }
/*       */ 
/*       */     try
/*       */     {
/*  6190 */       paramClass.getDeclaredMethod("coalesceEvents", coalesceEventsParams);
/*       */ 
/*  6193 */       return true;
/*       */     } catch (NoSuchMethodException localNoSuchMethodException) {
/*       */     }
/*  6196 */     return false;
/*       */   }
/*       */ 
/*       */   final boolean isCoalescingEnabled()
/*       */   {
/*  6204 */     return this.coalescingEnabled;
/*       */   }
/*       */ 
/*       */   protected AWTEvent coalesceEvents(AWTEvent paramAWTEvent1, AWTEvent paramAWTEvent2)
/*       */   {
/*  6236 */     return null;
/*       */   }
/*       */ 
/*       */   protected void processEvent(AWTEvent paramAWTEvent)
/*       */   {
/*  6260 */     if ((paramAWTEvent instanceof FocusEvent)) {
/*  6261 */       processFocusEvent((FocusEvent)paramAWTEvent);
/*       */     }
/*  6263 */     else if ((paramAWTEvent instanceof MouseEvent)) {
/*  6264 */       switch (paramAWTEvent.getID()) {
/*       */       case 500:
/*       */       case 501:
/*       */       case 502:
/*       */       case 504:
/*       */       case 505:
/*  6270 */         processMouseEvent((MouseEvent)paramAWTEvent);
/*  6271 */         break;
/*       */       case 503:
/*       */       case 506:
/*  6274 */         processMouseMotionEvent((MouseEvent)paramAWTEvent);
/*  6275 */         break;
/*       */       case 507:
/*  6277 */         processMouseWheelEvent((MouseWheelEvent)paramAWTEvent);
/*       */       }
/*       */ 
/*       */     }
/*  6281 */     else if ((paramAWTEvent instanceof KeyEvent)) {
/*  6282 */       processKeyEvent((KeyEvent)paramAWTEvent);
/*       */     }
/*  6284 */     else if ((paramAWTEvent instanceof ComponentEvent))
/*  6285 */       processComponentEvent((ComponentEvent)paramAWTEvent);
/*  6286 */     else if ((paramAWTEvent instanceof InputMethodEvent))
/*  6287 */       processInputMethodEvent((InputMethodEvent)paramAWTEvent);
/*  6288 */     else if ((paramAWTEvent instanceof HierarchyEvent))
/*  6289 */       switch (paramAWTEvent.getID()) {
/*       */       case 1400:
/*  6291 */         processHierarchyEvent((HierarchyEvent)paramAWTEvent);
/*  6292 */         break;
/*       */       case 1401:
/*       */       case 1402:
/*  6295 */         processHierarchyBoundsEvent((HierarchyEvent)paramAWTEvent);
/*       */       }
/*       */   }
/*       */ 
/*       */   protected void processComponentEvent(ComponentEvent paramComponentEvent)
/*       */   {
/*  6326 */     ComponentListener localComponentListener = this.componentListener;
/*  6327 */     if (localComponentListener != null) {
/*  6328 */       int i = paramComponentEvent.getID();
/*  6329 */       switch (i) {
/*       */       case 101:
/*  6331 */         localComponentListener.componentResized(paramComponentEvent);
/*  6332 */         break;
/*       */       case 100:
/*  6334 */         localComponentListener.componentMoved(paramComponentEvent);
/*  6335 */         break;
/*       */       case 102:
/*  6337 */         localComponentListener.componentShown(paramComponentEvent);
/*  6338 */         break;
/*       */       case 103:
/*  6340 */         localComponentListener.componentHidden(paramComponentEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void processFocusEvent(FocusEvent paramFocusEvent)
/*       */   {
/*  6389 */     FocusListener localFocusListener = this.focusListener;
/*  6390 */     if (localFocusListener != null) {
/*  6391 */       int i = paramFocusEvent.getID();
/*  6392 */       switch (i) {
/*       */       case 1004:
/*  6394 */         localFocusListener.focusGained(paramFocusEvent);
/*  6395 */         break;
/*       */       case 1005:
/*  6397 */         localFocusListener.focusLost(paramFocusEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void processKeyEvent(KeyEvent paramKeyEvent)
/*       */   {
/*  6455 */     KeyListener localKeyListener = this.keyListener;
/*  6456 */     if (localKeyListener != null) {
/*  6457 */       int i = paramKeyEvent.getID();
/*  6458 */       switch (i) {
/*       */       case 400:
/*  6460 */         localKeyListener.keyTyped(paramKeyEvent);
/*  6461 */         break;
/*       */       case 401:
/*  6463 */         localKeyListener.keyPressed(paramKeyEvent);
/*  6464 */         break;
/*       */       case 402:
/*  6466 */         localKeyListener.keyReleased(paramKeyEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void processMouseEvent(MouseEvent paramMouseEvent)
/*       */   {
/*  6497 */     MouseListener localMouseListener = this.mouseListener;
/*  6498 */     if (localMouseListener != null) {
/*  6499 */       int i = paramMouseEvent.getID();
/*  6500 */       switch (i) {
/*       */       case 501:
/*  6502 */         localMouseListener.mousePressed(paramMouseEvent);
/*  6503 */         break;
/*       */       case 502:
/*  6505 */         localMouseListener.mouseReleased(paramMouseEvent);
/*  6506 */         break;
/*       */       case 500:
/*  6508 */         localMouseListener.mouseClicked(paramMouseEvent);
/*  6509 */         break;
/*       */       case 505:
/*  6511 */         localMouseListener.mouseExited(paramMouseEvent);
/*  6512 */         break;
/*       */       case 504:
/*  6514 */         localMouseListener.mouseEntered(paramMouseEvent);
/*       */       case 503:
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void processMouseMotionEvent(MouseEvent paramMouseEvent)
/*       */   {
/*  6545 */     MouseMotionListener localMouseMotionListener = this.mouseMotionListener;
/*  6546 */     if (localMouseMotionListener != null) {
/*  6547 */       int i = paramMouseEvent.getID();
/*  6548 */       switch (i) {
/*       */       case 503:
/*  6550 */         localMouseMotionListener.mouseMoved(paramMouseEvent);
/*  6551 */         break;
/*       */       case 506:
/*  6553 */         localMouseMotionListener.mouseDragged(paramMouseEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void processMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent)
/*       */   {
/*  6588 */     MouseWheelListener localMouseWheelListener = this.mouseWheelListener;
/*  6589 */     if (localMouseWheelListener != null) {
/*  6590 */       int i = paramMouseWheelEvent.getID();
/*  6591 */       switch (i) {
/*       */       case 507:
/*  6593 */         localMouseWheelListener.mouseWheelMoved(paramMouseWheelEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   boolean postsOldMouseEvents()
/*       */   {
/*  6600 */     return false;
/*       */   }
/*       */ 
/*       */   protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent)
/*       */   {
/*  6628 */     InputMethodListener localInputMethodListener = this.inputMethodListener;
/*  6629 */     if (localInputMethodListener != null) {
/*  6630 */       int i = paramInputMethodEvent.getID();
/*  6631 */       switch (i) {
/*       */       case 1100:
/*  6633 */         localInputMethodListener.inputMethodTextChanged(paramInputMethodEvent);
/*  6634 */         break;
/*       */       case 1101:
/*  6636 */         localInputMethodListener.caretPositionChanged(paramInputMethodEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void processHierarchyEvent(HierarchyEvent paramHierarchyEvent)
/*       */   {
/*  6667 */     HierarchyListener localHierarchyListener = this.hierarchyListener;
/*  6668 */     if (localHierarchyListener != null) {
/*  6669 */       int i = paramHierarchyEvent.getID();
/*  6670 */       switch (i) {
/*       */       case 1400:
/*  6672 */         localHierarchyListener.hierarchyChanged(paramHierarchyEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void processHierarchyBoundsEvent(HierarchyEvent paramHierarchyEvent)
/*       */   {
/*  6703 */     HierarchyBoundsListener localHierarchyBoundsListener = this.hierarchyBoundsListener;
/*  6704 */     if (localHierarchyBoundsListener != null) {
/*  6705 */       int i = paramHierarchyEvent.getID();
/*  6706 */       switch (i) {
/*       */       case 1401:
/*  6708 */         localHierarchyBoundsListener.ancestorMoved(paramHierarchyEvent);
/*  6709 */         break;
/*       */       case 1402:
/*  6711 */         localHierarchyBoundsListener.ancestorResized(paramHierarchyEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean handleEvent(Event paramEvent)
/*       */   {
/*  6723 */     switch (paramEvent.id) {
/*       */     case 504:
/*  6725 */       return mouseEnter(paramEvent, paramEvent.x, paramEvent.y);
/*       */     case 505:
/*  6728 */       return mouseExit(paramEvent, paramEvent.x, paramEvent.y);
/*       */     case 503:
/*  6731 */       return mouseMove(paramEvent, paramEvent.x, paramEvent.y);
/*       */     case 501:
/*  6734 */       return mouseDown(paramEvent, paramEvent.x, paramEvent.y);
/*       */     case 506:
/*  6737 */       return mouseDrag(paramEvent, paramEvent.x, paramEvent.y);
/*       */     case 502:
/*  6740 */       return mouseUp(paramEvent, paramEvent.x, paramEvent.y);
/*       */     case 401:
/*       */     case 403:
/*  6744 */       return keyDown(paramEvent, paramEvent.key);
/*       */     case 402:
/*       */     case 404:
/*  6748 */       return keyUp(paramEvent, paramEvent.key);
/*       */     case 1001:
/*  6751 */       return action(paramEvent, paramEvent.arg);
/*       */     case 1004:
/*  6753 */       return gotFocus(paramEvent, paramEvent.arg);
/*       */     case 1005:
/*  6755 */       return lostFocus(paramEvent, paramEvent.arg);
/*       */     }
/*  6757 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean mouseDown(Event paramEvent, int paramInt1, int paramInt2)
/*       */   {
/*  6766 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean mouseDrag(Event paramEvent, int paramInt1, int paramInt2)
/*       */   {
/*  6775 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean mouseUp(Event paramEvent, int paramInt1, int paramInt2)
/*       */   {
/*  6784 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean mouseMove(Event paramEvent, int paramInt1, int paramInt2)
/*       */   {
/*  6793 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean mouseEnter(Event paramEvent, int paramInt1, int paramInt2)
/*       */   {
/*  6802 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean mouseExit(Event paramEvent, int paramInt1, int paramInt2)
/*       */   {
/*  6811 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean keyDown(Event paramEvent, int paramInt)
/*       */   {
/*  6820 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean keyUp(Event paramEvent, int paramInt)
/*       */   {
/*  6829 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean action(Event paramEvent, Object paramObject)
/*       */   {
/*  6839 */     return false;
/*       */   }
/*       */ 
/*       */   public void addNotify()
/*       */   {
/*  6857 */     synchronized (getTreeLock()) {
/*  6858 */       Object localObject1 = this.peer;
/*  6859 */       if ((localObject1 == null) || ((localObject1 instanceof LightweightPeer))) {
/*  6860 */         if (localObject1 == null)
/*       */         {
/*  6863 */           this.peer = (localObject1 = getToolkit().createComponent(this));
/*       */         }
/*       */ 
/*  6870 */         if (this.parent != null) {
/*  6871 */           long l = 0L;
/*  6872 */           if ((this.mouseListener != null) || ((this.eventMask & 0x10) != 0L)) {
/*  6873 */             l |= 16L;
/*       */           }
/*  6875 */           if ((this.mouseMotionListener != null) || ((this.eventMask & 0x20) != 0L))
/*       */           {
/*  6877 */             l |= 32L;
/*       */           }
/*  6879 */           if ((this.mouseWheelListener != null) || ((this.eventMask & 0x20000) != 0L))
/*       */           {
/*  6881 */             l |= 131072L;
/*       */           }
/*  6883 */           if ((this.focusListener != null) || ((this.eventMask & 0x4) != 0L)) {
/*  6884 */             l |= 4L;
/*       */           }
/*  6886 */           if ((this.keyListener != null) || ((this.eventMask & 0x8) != 0L)) {
/*  6887 */             l |= 8L;
/*       */           }
/*  6889 */           if (l != 0L) {
/*  6890 */             this.parent.proxyEnableEvents(l);
/*       */           }
/*       */         }
/*       */       }
/*       */       else
/*       */       {
/*  6896 */         Container localContainer = getContainer();
/*  6897 */         if ((localContainer != null) && (localContainer.isLightweight())) {
/*  6898 */           relocateComponent();
/*  6899 */           if (!localContainer.isRecursivelyVisibleUpToHeavyweightContainer())
/*       */           {
/*  6901 */             ((ComponentPeer)localObject1).setVisible(false);
/*       */           }
/*       */         }
/*       */       }
/*  6905 */       invalidate();
/*       */ 
/*  6907 */       int i = this.popups != null ? this.popups.size() : 0;
/*  6908 */       for (int j = 0; j < i; j++) {
/*  6909 */         PopupMenu localPopupMenu = (PopupMenu)this.popups.elementAt(j);
/*  6910 */         localPopupMenu.addNotify();
/*       */       }
/*       */ 
/*  6913 */       if (this.dropTarget != null) this.dropTarget.addNotify((ComponentPeer)localObject1);
/*       */ 
/*  6915 */       this.peerFont = getFont();
/*       */ 
/*  6917 */       if ((getContainer() != null) && (!this.isAddNotifyComplete)) {
/*  6918 */         getContainer().increaseComponentCount(this);
/*       */       }
/*       */ 
/*  6923 */       updateZOrder();
/*       */ 
/*  6925 */       if (!this.isAddNotifyComplete) {
/*  6926 */         mixOnShowing();
/*       */       }
/*       */ 
/*  6929 */       this.isAddNotifyComplete = true;
/*       */ 
/*  6931 */       if ((this.hierarchyListener != null) || ((this.eventMask & 0x8000) != 0L) || (Toolkit.enabledOnToolkit(32768L)))
/*       */       {
/*  6934 */         HierarchyEvent localHierarchyEvent = new HierarchyEvent(this, 1400, this, this.parent, 0x2 | (isRecursivelyVisible() ? 4 : 0));
/*       */ 
/*  6941 */         dispatchEvent(localHierarchyEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public void removeNotify()
/*       */   {
/*  6960 */     KeyboardFocusManager.clearMostRecentFocusOwner(this);
/*  6961 */     if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner() == this)
/*       */     {
/*  6964 */       KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalPermanentFocusOwner(null);
/*       */     }
/*       */ 
/*  6968 */     synchronized (getTreeLock()) {
/*  6969 */       if ((isFocusOwner()) && (KeyboardFocusManager.isAutoFocusTransferEnabledFor(this))) {
/*  6970 */         transferFocus(true);
/*       */       }
/*       */ 
/*  6973 */       if ((getContainer() != null) && (this.isAddNotifyComplete)) {
/*  6974 */         getContainer().decreaseComponentCount(this);
/*       */       }
/*       */ 
/*  6977 */       int i = this.popups != null ? this.popups.size() : 0;
/*  6978 */       for (int j = 0; j < i; j++) {
/*  6979 */         PopupMenu localPopupMenu = (PopupMenu)this.popups.elementAt(j);
/*  6980 */         localPopupMenu.removeNotify();
/*       */       }
/*       */ 
/*  6985 */       if ((this.eventMask & 0x1000) != 0L) {
/*  6986 */         localObject1 = getInputContext();
/*  6987 */         if (localObject1 != null) {
/*  6988 */           ((java.awt.im.InputContext)localObject1).removeNotify(this);
/*       */         }
/*       */       }
/*       */ 
/*  6992 */       Object localObject1 = this.peer;
/*  6993 */       if (localObject1 != null) {
/*  6994 */         boolean bool = isLightweight();
/*       */ 
/*  6996 */         if ((this.bufferStrategy instanceof FlipBufferStrategy)) {
/*  6997 */           ((FlipBufferStrategy)this.bufferStrategy).destroyBuffers();
/*       */         }
/*       */ 
/*  7000 */         if (this.dropTarget != null) this.dropTarget.removeNotify(this.peer);
/*       */ 
/*  7003 */         if (this.visible) {
/*  7004 */           ((ComponentPeer)localObject1).setVisible(false);
/*       */         }
/*       */ 
/*  7007 */         this.peer = null;
/*  7008 */         this.peerFont = null;
/*       */ 
/*  7010 */         Toolkit.getEventQueue().removeSourceEvents(this, false);
/*  7011 */         KeyboardFocusManager.getCurrentKeyboardFocusManager().discardKeyEvents(this);
/*       */ 
/*  7014 */         ((ComponentPeer)localObject1).dispose();
/*       */ 
/*  7016 */         mixOnHiding(bool);
/*       */ 
/*  7018 */         this.isAddNotifyComplete = false;
/*       */ 
/*  7021 */         this.compoundShape = null;
/*       */       }
/*       */ 
/*  7024 */       if ((this.hierarchyListener != null) || ((this.eventMask & 0x8000) != 0L) || (Toolkit.enabledOnToolkit(32768L)))
/*       */       {
/*  7027 */         HierarchyEvent localHierarchyEvent = new HierarchyEvent(this, 1400, this, this.parent, 0x2 | (isRecursivelyVisible() ? 4 : 0));
/*       */ 
/*  7034 */         dispatchEvent(localHierarchyEvent);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean gotFocus(Event paramEvent, Object paramObject)
/*       */   {
/*  7045 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean lostFocus(Event paramEvent, Object paramObject)
/*       */   {
/*  7054 */     return false;
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public boolean isFocusTraversable()
/*       */   {
/*  7069 */     if (this.isFocusTraversableOverridden == 0) {
/*  7070 */       this.isFocusTraversableOverridden = 1;
/*       */     }
/*  7072 */     return this.focusable;
/*       */   }
/*       */ 
/*       */   public boolean isFocusable()
/*       */   {
/*  7084 */     return isFocusTraversable();
/*       */   }
/*       */ 
/*       */   public void setFocusable(boolean paramBoolean)
/*       */   {
/*       */     boolean bool;
/*  7099 */     synchronized (this) {
/*  7100 */       bool = this.focusable;
/*  7101 */       this.focusable = paramBoolean;
/*       */     }
/*  7103 */     this.isFocusTraversableOverridden = 2;
/*       */ 
/*  7105 */     firePropertyChange("focusable", bool, paramBoolean);
/*  7106 */     if ((bool) && (!paramBoolean)) {
/*  7107 */       if ((isFocusOwner()) && (KeyboardFocusManager.isAutoFocusTransferEnabled())) {
/*  7108 */         transferFocus(true);
/*       */       }
/*  7110 */       KeyboardFocusManager.clearMostRecentFocusOwner(this);
/*       */     }
/*       */   }
/*       */ 
/*       */   final boolean isFocusTraversableOverridden() {
/*  7115 */     return this.isFocusTraversableOverridden != 1;
/*       */   }
/*       */ 
/*       */   public void setFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet)
/*       */   {
/*  7191 */     if ((paramInt < 0) || (paramInt >= 3)) {
/*  7192 */       throw new IllegalArgumentException("invalid focus traversal key identifier");
/*       */     }
/*       */ 
/*  7195 */     setFocusTraversalKeys_NoIDCheck(paramInt, paramSet);
/*       */   }
/*       */ 
/*       */   public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt)
/*       */   {
/*  7225 */     if ((paramInt < 0) || (paramInt >= 3)) {
/*  7226 */       throw new IllegalArgumentException("invalid focus traversal key identifier");
/*       */     }
/*       */ 
/*  7229 */     return getFocusTraversalKeys_NoIDCheck(paramInt);
/*       */   }
/*       */ 
/*       */   final void setFocusTraversalKeys_NoIDCheck(int paramInt, Set<? extends AWTKeyStroke> paramSet)
/*       */   {
/*       */     Set localSet;
/*  7240 */     synchronized (this) {
/*  7241 */       if (this.focusTraversalKeys == null)
/*  7242 */         initializeFocusTraversalKeys();
/*       */       Iterator localIterator;
/*  7245 */       if (paramSet != null) {
/*  7246 */         for (localIterator = paramSet.iterator(); localIterator.hasNext(); ) {
/*  7247 */           Object localObject1 = localIterator.next();
/*       */ 
/*  7249 */           if (localObject1 == null) {
/*  7250 */             throw new IllegalArgumentException("cannot set null focus traversal key");
/*       */           }
/*       */ 
/*  7255 */           if (!(localObject1 instanceof AWTKeyStroke)) {
/*  7256 */             throw new IllegalArgumentException("object is expected to be AWTKeyStroke");
/*       */           }
/*  7258 */           AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)localObject1;
/*       */ 
/*  7260 */           if (localAWTKeyStroke.getKeyChar() != 65535) {
/*  7261 */             throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
/*       */           }
/*       */ 
/*  7264 */           for (int i = 0; i < this.focusTraversalKeys.length; i++) {
/*  7265 */             if (i != paramInt)
/*       */             {
/*  7269 */               if (getFocusTraversalKeys_NoIDCheck(i).contains(localAWTKeyStroke))
/*       */               {
/*  7271 */                 throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*  7277 */       localSet = this.focusTraversalKeys[paramInt];
/*  7278 */       this.focusTraversalKeys[paramInt] = (paramSet != null ? Collections.unmodifiableSet(new HashSet(paramSet)) : null);
/*       */     }
/*       */ 
/*  7283 */     firePropertyChange(focusTraversalKeyPropertyNames[paramInt], localSet, paramSet);
/*       */   }
/*       */ 
/*       */   final Set getFocusTraversalKeys_NoIDCheck(int paramInt)
/*       */   {
/*  7288 */     Set localSet = this.focusTraversalKeys != null ? this.focusTraversalKeys[paramInt] : null;
/*       */ 
/*  7292 */     if (localSet != null) {
/*  7293 */       return localSet;
/*       */     }
/*  7295 */     Container localContainer = this.parent;
/*  7296 */     if (localContainer != null) {
/*  7297 */       return localContainer.getFocusTraversalKeys(paramInt);
/*       */     }
/*  7299 */     return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(paramInt);
/*       */   }
/*       */ 
/*       */   public boolean areFocusTraversalKeysSet(int paramInt)
/*       */   {
/*  7324 */     if ((paramInt < 0) || (paramInt >= 3)) {
/*  7325 */       throw new IllegalArgumentException("invalid focus traversal key identifier");
/*       */     }
/*       */ 
/*  7328 */     return (this.focusTraversalKeys != null) && (this.focusTraversalKeys[paramInt] != null);
/*       */   }
/*       */ 
/*       */   public void setFocusTraversalKeysEnabled(boolean paramBoolean)
/*       */   {
/*       */     boolean bool;
/*  7350 */     synchronized (this) {
/*  7351 */       bool = this.focusTraversalKeysEnabled;
/*  7352 */       this.focusTraversalKeysEnabled = paramBoolean;
/*       */     }
/*  7354 */     firePropertyChange("focusTraversalKeysEnabled", bool, paramBoolean);
/*       */   }
/*       */ 
/*       */   public boolean getFocusTraversalKeysEnabled()
/*       */   {
/*  7373 */     return this.focusTraversalKeysEnabled;
/*       */   }
/*       */ 
/*       */   public void requestFocus()
/*       */   {
/*  7411 */     requestFocusHelper(false, true);
/*       */   }
/*       */ 
/*       */   boolean requestFocus(CausedFocusEvent.Cause paramCause) {
/*  7415 */     return requestFocusHelper(false, true, paramCause);
/*       */   }
/*       */ 
/*       */   protected boolean requestFocus(boolean paramBoolean)
/*       */   {
/*  7478 */     return requestFocusHelper(paramBoolean, true);
/*       */   }
/*       */ 
/*       */   boolean requestFocus(boolean paramBoolean, CausedFocusEvent.Cause paramCause) {
/*  7482 */     return requestFocusHelper(paramBoolean, true, paramCause);
/*       */   }
/*       */ 
/*       */   public boolean requestFocusInWindow()
/*       */   {
/*  7529 */     return requestFocusHelper(false, false);
/*       */   }
/*       */ 
/*       */   boolean requestFocusInWindow(CausedFocusEvent.Cause paramCause) {
/*  7533 */     return requestFocusHelper(false, false, paramCause);
/*       */   }
/*       */ 
/*       */   protected boolean requestFocusInWindow(boolean paramBoolean)
/*       */   {
/*  7594 */     return requestFocusHelper(paramBoolean, false);
/*       */   }
/*       */ 
/*       */   boolean requestFocusInWindow(boolean paramBoolean, CausedFocusEvent.Cause paramCause) {
/*  7598 */     return requestFocusHelper(paramBoolean, false, paramCause);
/*       */   }
/*       */ 
/*       */   final boolean requestFocusHelper(boolean paramBoolean1, boolean paramBoolean2)
/*       */   {
/*  7603 */     return requestFocusHelper(paramBoolean1, paramBoolean2, CausedFocusEvent.Cause.UNKNOWN);
/*       */   }
/*       */ 
/*       */   final boolean requestFocusHelper(boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause)
/*       */   {
/*  7610 */     if (!isRequestFocusAccepted(paramBoolean1, paramBoolean2, paramCause)) {
/*  7611 */       if (focusLog.isLoggable(300)) {
/*  7612 */         focusLog.finest("requestFocus is not accepted");
/*       */       }
/*  7614 */       return false;
/*       */     }
/*       */ 
/*  7618 */     KeyboardFocusManager.setMostRecentFocusOwner(this);
/*       */ 
/*  7620 */     Object localObject = this;
/*  7621 */     while ((localObject != null) && (!(localObject instanceof Window))) {
/*  7622 */       if (!((Component)localObject).isVisible()) {
/*  7623 */         if (focusLog.isLoggable(300)) {
/*  7624 */           focusLog.finest("component is recurively invisible");
/*       */         }
/*  7626 */         return false;
/*       */       }
/*  7628 */       localObject = ((Component)localObject).parent;
/*       */     }
/*       */ 
/*  7631 */     ComponentPeer localComponentPeer = this.peer;
/*  7632 */     Component localComponent = (localComponentPeer instanceof LightweightPeer) ? getNativeContainer() : this;
/*       */ 
/*  7634 */     if ((localComponent == null) || (!localComponent.isVisible())) {
/*  7635 */       if (focusLog.isLoggable(300)) {
/*  7636 */         focusLog.finest("Component is not a part of visible hierarchy");
/*       */       }
/*  7638 */       return false;
/*       */     }
/*  7640 */     localComponentPeer = localComponent.peer;
/*  7641 */     if (localComponentPeer == null) {
/*  7642 */       if (focusLog.isLoggable(300)) {
/*  7643 */         focusLog.finest("Peer is null");
/*       */       }
/*  7645 */       return false;
/*       */     }
/*       */ 
/*  7649 */     long l = EventQueue.getMostRecentEventTime();
/*  7650 */     boolean bool = localComponentPeer.requestFocus(this, paramBoolean1, paramBoolean2, l, paramCause);
/*       */ 
/*  7652 */     if (!bool) {
/*  7653 */       KeyboardFocusManager.getCurrentKeyboardFocusManager(this.appContext).dequeueKeyEvents(l, this);
/*       */ 
/*  7655 */       if (focusLog.isLoggable(300)) {
/*  7656 */         focusLog.finest("Peer request failed");
/*       */       }
/*       */     }
/*  7659 */     else if (focusLog.isLoggable(300)) {
/*  7660 */       focusLog.finest("Pass for " + this);
/*       */     }
/*       */ 
/*  7663 */     return bool;
/*       */   }
/*       */ 
/*       */   private boolean isRequestFocusAccepted(boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause)
/*       */   {
/*  7670 */     if ((!isFocusable()) || (!isVisible())) {
/*  7671 */       if (focusLog.isLoggable(300)) {
/*  7672 */         focusLog.finest("Not focusable or not visible");
/*       */       }
/*  7674 */       return false;
/*       */     }
/*       */ 
/*  7677 */     ComponentPeer localComponentPeer = this.peer;
/*  7678 */     if (localComponentPeer == null) {
/*  7679 */       if (focusLog.isLoggable(300)) {
/*  7680 */         focusLog.finest("peer is null");
/*       */       }
/*  7682 */       return false;
/*       */     }
/*       */ 
/*  7685 */     Window localWindow = getContainingWindow();
/*  7686 */     if ((localWindow == null) || (!localWindow.isFocusableWindow())) {
/*  7687 */       if (focusLog.isLoggable(300)) {
/*  7688 */         focusLog.finest("Component doesn't have toplevel");
/*       */       }
/*  7690 */       return false;
/*       */     }
/*       */ 
/*  7695 */     Component localComponent = KeyboardFocusManager.getMostRecentFocusOwner(localWindow);
/*  7696 */     if (localComponent == null)
/*       */     {
/*  7699 */       localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
/*  7700 */       if ((localComponent != null) && (localComponent.getContainingWindow() != localWindow)) {
/*  7701 */         localComponent = null;
/*       */       }
/*       */     }
/*       */ 
/*  7705 */     if ((localComponent == this) || (localComponent == null))
/*       */     {
/*  7709 */       if (focusLog.isLoggable(300)) {
/*  7710 */         focusLog.finest("focus owner is null or this");
/*       */       }
/*  7712 */       return true;
/*       */     }
/*       */ 
/*  7715 */     if (CausedFocusEvent.Cause.ACTIVATION == paramCause)
/*       */     {
/*  7722 */       if (focusLog.isLoggable(300)) {
/*  7723 */         focusLog.finest("cause is activation");
/*       */       }
/*  7725 */       return true;
/*       */     }
/*       */ 
/*  7728 */     boolean bool = requestFocusController.acceptRequestFocus(localComponent, this, paramBoolean1, paramBoolean2, paramCause);
/*       */ 
/*  7733 */     if (focusLog.isLoggable(300)) {
/*  7734 */       focusLog.finest("RequestFocusController returns {0}", new Object[] { Boolean.valueOf(bool) });
/*       */     }
/*       */ 
/*  7737 */     return bool;
/*       */   }
/*       */ 
/*       */   static synchronized void setRequestFocusController(RequestFocusController paramRequestFocusController)
/*       */   {
/*  7755 */     if (paramRequestFocusController == null)
/*  7756 */       requestFocusController = new DummyRequestFocusController(null);
/*       */     else
/*  7758 */       requestFocusController = paramRequestFocusController;
/*       */   }
/*       */ 
/*       */   public Container getFocusCycleRootAncestor()
/*       */   {
/*  7777 */     Container localContainer = this.parent;
/*  7778 */     while ((localContainer != null) && (!localContainer.isFocusCycleRoot())) {
/*  7779 */       localContainer = localContainer.parent;
/*       */     }
/*  7781 */     return localContainer;
/*       */   }
/*       */ 
/*       */   public boolean isFocusCycleRoot(Container paramContainer)
/*       */   {
/*  7797 */     Container localContainer = getFocusCycleRootAncestor();
/*  7798 */     return localContainer == paramContainer;
/*       */   }
/*       */ 
/*       */   Container getTraversalRoot() {
/*  7802 */     return getFocusCycleRootAncestor();
/*       */   }
/*       */ 
/*       */   public void transferFocus()
/*       */   {
/*  7812 */     nextFocus();
/*       */   }
/*       */ 
/*       */   @Deprecated
/*       */   public void nextFocus()
/*       */   {
/*  7821 */     transferFocus(false);
/*       */   }
/*       */ 
/*       */   boolean transferFocus(boolean paramBoolean) {
/*  7825 */     if (focusLog.isLoggable(400)) {
/*  7826 */       focusLog.finer("clearOnFailure = " + paramBoolean);
/*       */     }
/*  7828 */     Component localComponent = getNextFocusCandidate();
/*  7829 */     boolean bool = false;
/*  7830 */     if ((localComponent != null) && (!localComponent.isFocusOwner()) && (localComponent != this)) {
/*  7831 */       bool = localComponent.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_FORWARD);
/*       */     }
/*  7833 */     if ((paramBoolean) && (!bool)) {
/*  7834 */       if (focusLog.isLoggable(400)) {
/*  7835 */         focusLog.finer("clear global focus owner");
/*       */       }
/*  7837 */       KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
/*       */     }
/*  7839 */     if (focusLog.isLoggable(400)) {
/*  7840 */       focusLog.finer("returning result: " + bool);
/*       */     }
/*  7842 */     return bool;
/*       */   }
/*       */ 
/*       */   final Component getNextFocusCandidate() {
/*  7846 */     Container localContainer = getTraversalRoot();
/*  7847 */     Object localObject1 = this;
/*  7848 */     while ((localContainer != null) && ((!localContainer.isShowing()) || (!localContainer.canBeFocusOwner())))
/*       */     {
/*  7851 */       localObject1 = localContainer;
/*  7852 */       localContainer = ((Component)localObject1).getFocusCycleRootAncestor();
/*       */     }
/*  7854 */     if (focusLog.isLoggable(400)) {
/*  7855 */       focusLog.finer("comp = " + localObject1 + ", root = " + localContainer);
/*       */     }
/*  7857 */     Object localObject2 = null;
/*  7858 */     if (localContainer != null) {
/*  7859 */       FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
/*  7860 */       Object localObject3 = localFocusTraversalPolicy.getComponentAfter(localContainer, (Component)localObject1);
/*  7861 */       if (focusLog.isLoggable(400)) {
/*  7862 */         focusLog.finer("component after is " + localObject3);
/*       */       }
/*  7864 */       if (localObject3 == null) {
/*  7865 */         localObject3 = localFocusTraversalPolicy.getDefaultComponent(localContainer);
/*  7866 */         if (focusLog.isLoggable(400)) {
/*  7867 */           focusLog.finer("default component is " + localObject3);
/*       */         }
/*       */       }
/*  7870 */       if (localObject3 == null) {
/*  7871 */         Applet localApplet = EmbeddedFrame.getAppletIfAncestorOf(this);
/*  7872 */         if (localApplet != null) {
/*  7873 */           localObject3 = localApplet;
/*       */         }
/*       */       }
/*  7876 */       localObject2 = localObject3;
/*       */     }
/*  7878 */     if (focusLog.isLoggable(400)) {
/*  7879 */       focusLog.finer("Focus transfer candidate: " + localObject2);
/*       */     }
/*  7881 */     return localObject2;
/*       */   }
/*       */ 
/*       */   public void transferFocusBackward()
/*       */   {
/*  7891 */     transferFocusBackward(false);
/*       */   }
/*       */ 
/*       */   boolean transferFocusBackward(boolean paramBoolean) {
/*  7895 */     Container localContainer = getTraversalRoot();
/*  7896 */     Object localObject = this;
/*  7897 */     while ((localContainer != null) && ((!localContainer.isShowing()) || (!localContainer.canBeFocusOwner())))
/*       */     {
/*  7900 */       localObject = localContainer;
/*  7901 */       localContainer = ((Component)localObject).getFocusCycleRootAncestor();
/*       */     }
/*  7903 */     boolean bool = false;
/*  7904 */     if (localContainer != null) {
/*  7905 */       FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
/*  7906 */       Component localComponent = localFocusTraversalPolicy.getComponentBefore(localContainer, (Component)localObject);
/*  7907 */       if (localComponent == null) {
/*  7908 */         localComponent = localFocusTraversalPolicy.getDefaultComponent(localContainer);
/*       */       }
/*  7910 */       if (localComponent != null) {
/*  7911 */         bool = localComponent.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_BACKWARD);
/*       */       }
/*       */     }
/*  7914 */     if (!bool) {
/*  7915 */       if (focusLog.isLoggable(400)) {
/*  7916 */         focusLog.finer("clear global focus owner");
/*       */       }
/*  7918 */       KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
/*       */     }
/*  7920 */     if (focusLog.isLoggable(400)) {
/*  7921 */       focusLog.finer("returning result: " + bool);
/*       */     }
/*  7923 */     return bool;
/*       */   }
/*       */ 
/*       */   public void transferFocusUpCycle()
/*       */   {
/*  7941 */     Container localContainer = getFocusCycleRootAncestor();
/*  7942 */     while ((localContainer != null) && ((!localContainer.isShowing()) || (!localContainer.isFocusable()) || (!localContainer.isEnabled())))
/*       */     {
/*  7945 */       localContainer = localContainer.getFocusCycleRootAncestor();
/*       */     }
/*       */     Object localObject;
/*  7948 */     if (localContainer != null) {
/*  7949 */       localObject = localContainer.getFocusCycleRootAncestor();
/*       */ 
/*  7951 */       KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRoot(localObject != null ? localObject : localContainer);
/*       */ 
/*  7956 */       localContainer.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
/*       */     } else {
/*  7958 */       localObject = getContainingWindow();
/*       */ 
/*  7960 */       if (localObject != null) {
/*  7961 */         Component localComponent = ((Window)localObject).getFocusTraversalPolicy().getDefaultComponent((Container)localObject);
/*       */ 
/*  7963 */         if (localComponent != null) {
/*  7964 */           KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRoot((Container)localObject);
/*       */ 
/*  7966 */           localComponent.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
/*       */         }
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public boolean hasFocus()
/*       */   {
/*  7982 */     return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this;
/*       */   }
/*       */ 
/*       */   public boolean isFocusOwner()
/*       */   {
/*  7995 */     return hasFocus();
/*       */   }
/*       */ 
/*       */   void setAutoFocusTransferOnDisposal(boolean paramBoolean)
/*       */   {
/*  8005 */     this.autoFocusTransferOnDisposal = paramBoolean;
/*       */   }
/*       */ 
/*       */   boolean isAutoFocusTransferOnDisposal() {
/*  8009 */     return this.autoFocusTransferOnDisposal;
/*       */   }
/*       */ 
/*       */   public void add(PopupMenu paramPopupMenu)
/*       */   {
/*  8020 */     synchronized (getTreeLock()) {
/*  8021 */       if (paramPopupMenu.parent != null) {
/*  8022 */         paramPopupMenu.parent.remove(paramPopupMenu);
/*       */       }
/*  8024 */       if (this.popups == null) {
/*  8025 */         this.popups = new Vector();
/*       */       }
/*  8027 */       this.popups.addElement(paramPopupMenu);
/*  8028 */       paramPopupMenu.parent = this;
/*       */ 
/*  8030 */       if ((this.peer != null) && 
/*  8031 */         (paramPopupMenu.peer == null))
/*  8032 */         paramPopupMenu.addNotify();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void remove(MenuComponent paramMenuComponent)
/*       */   {
/*  8045 */     synchronized (getTreeLock()) {
/*  8046 */       if (this.popups == null) {
/*  8047 */         return;
/*       */       }
/*  8049 */       int i = this.popups.indexOf(paramMenuComponent);
/*  8050 */       if (i >= 0) {
/*  8051 */         PopupMenu localPopupMenu = (PopupMenu)paramMenuComponent;
/*  8052 */         if (localPopupMenu.peer != null) {
/*  8053 */           localPopupMenu.removeNotify();
/*       */         }
/*  8055 */         localPopupMenu.parent = null;
/*  8056 */         this.popups.removeElementAt(i);
/*  8057 */         if (this.popups.size() == 0)
/*  8058 */           this.popups = null;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected String paramString()
/*       */   {
/*  8075 */     String str1 = getName();
/*  8076 */     String str2 = (str1 != null ? str1 : "") + "," + this.x + "," + this.y + "," + this.width + "x" + this.height;
/*  8077 */     if (!isValid()) {
/*  8078 */       str2 = str2 + ",invalid";
/*       */     }
/*  8080 */     if (!this.visible) {
/*  8081 */       str2 = str2 + ",hidden";
/*       */     }
/*  8083 */     if (!this.enabled) {
/*  8084 */       str2 = str2 + ",disabled";
/*       */     }
/*  8086 */     return str2;
/*       */   }
/*       */ 
/*       */   public String toString()
/*       */   {
/*  8095 */     return getClass().getName() + "[" + paramString() + "]";
/*       */   }
/*       */ 
/*       */   public void list()
/*       */   {
/*  8105 */     list(System.out, 0);
/*       */   }
/*       */ 
/*       */   public void list(PrintStream paramPrintStream)
/*       */   {
/*  8116 */     list(paramPrintStream, 0);
/*       */   }
/*       */ 
/*       */   public void list(PrintStream paramPrintStream, int paramInt)
/*       */   {
/*  8129 */     for (int i = 0; i < paramInt; i++) {
/*  8130 */       paramPrintStream.print(" ");
/*       */     }
/*  8132 */     paramPrintStream.println(this);
/*       */   }
/*       */ 
/*       */   public void list(PrintWriter paramPrintWriter)
/*       */   {
/*  8142 */     list(paramPrintWriter, 0);
/*       */   }
/*       */ 
/*       */   public void list(PrintWriter paramPrintWriter, int paramInt)
/*       */   {
/*  8155 */     for (int i = 0; i < paramInt; i++) {
/*  8156 */       paramPrintWriter.print(" ");
/*       */     }
/*  8158 */     paramPrintWriter.println(this);
/*       */   }
/*       */ 
/*       */   Container getNativeContainer()
/*       */   {
/*  8166 */     Container localContainer = this.parent;
/*  8167 */     while ((localContainer != null) && ((localContainer.peer instanceof LightweightPeer))) {
/*  8168 */       localContainer = localContainer.getParent_NoClientCode();
/*       */     }
/*  8170 */     return localContainer;
/*       */   }
/*       */ 
/*       */   public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*       */   {
/*  8209 */     synchronized (getObjectLock()) {
/*  8210 */       if (paramPropertyChangeListener == null) {
/*  8211 */         return;
/*       */       }
/*  8213 */       if (this.changeSupport == null) {
/*  8214 */         this.changeSupport = new PropertyChangeSupport(this);
/*       */       }
/*  8216 */       this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*       */   {
/*  8235 */     synchronized (getObjectLock()) {
/*  8236 */       if ((paramPropertyChangeListener == null) || (this.changeSupport == null)) {
/*  8237 */         return;
/*       */       }
/*  8239 */       this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
/*       */     }
/*       */   }
/*       */ 
/*       */   public PropertyChangeListener[] getPropertyChangeListeners()
/*       */   {
/*  8258 */     synchronized (getObjectLock()) {
/*  8259 */       if (this.changeSupport == null) {
/*  8260 */         return new PropertyChangeListener[0];
/*       */       }
/*  8262 */       return this.changeSupport.getPropertyChangeListeners();
/*       */     }
/*       */   }
/*       */ 
/*       */   public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*       */   {
/*  8300 */     synchronized (getObjectLock()) {
/*  8301 */       if (paramPropertyChangeListener == null) {
/*  8302 */         return;
/*       */       }
/*  8304 */       if (this.changeSupport == null) {
/*  8305 */         this.changeSupport = new PropertyChangeSupport(this);
/*       */       }
/*  8307 */       this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*       */     }
/*       */   }
/*       */ 
/*       */   public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*       */   {
/*  8330 */     synchronized (getObjectLock()) {
/*  8331 */       if ((paramPropertyChangeListener == null) || (this.changeSupport == null)) {
/*  8332 */         return;
/*       */       }
/*  8334 */       this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
/*       */     }
/*       */   }
/*       */ 
/*       */   public PropertyChangeListener[] getPropertyChangeListeners(String paramString)
/*       */   {
/*  8354 */     synchronized (getObjectLock()) {
/*  8355 */       if (this.changeSupport == null) {
/*  8356 */         return new PropertyChangeListener[0];
/*       */       }
/*  8358 */       return this.changeSupport.getPropertyChangeListeners(paramString);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
/*       */   {
/*       */     PropertyChangeSupport localPropertyChangeSupport;
/*  8375 */     synchronized (getObjectLock()) {
/*  8376 */       localPropertyChangeSupport = this.changeSupport;
/*       */     }
/*  8378 */     if ((localPropertyChangeSupport == null) || ((paramObject1 != null) && (paramObject2 != null) && (paramObject1.equals(paramObject2))))
/*       */     {
/*  8380 */       return;
/*       */     }
/*  8382 */     localPropertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
/*       */   }
/*       */ 
/*       */   protected void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2)
/*       */   {
/*  8398 */     PropertyChangeSupport localPropertyChangeSupport = this.changeSupport;
/*  8399 */     if ((localPropertyChangeSupport == null) || (paramBoolean1 == paramBoolean2)) {
/*  8400 */       return;
/*       */     }
/*  8402 */     localPropertyChangeSupport.firePropertyChange(paramString, paramBoolean1, paramBoolean2);
/*       */   }
/*       */ 
/*       */   protected void firePropertyChange(String paramString, int paramInt1, int paramInt2)
/*       */   {
/*  8418 */     PropertyChangeSupport localPropertyChangeSupport = this.changeSupport;
/*  8419 */     if ((localPropertyChangeSupport == null) || (paramInt1 == paramInt2)) {
/*  8420 */       return;
/*       */     }
/*  8422 */     localPropertyChangeSupport.firePropertyChange(paramString, paramInt1, paramInt2);
/*       */   }
/*       */ 
/*       */   public void firePropertyChange(String paramString, byte paramByte1, byte paramByte2)
/*       */   {
/*  8437 */     if ((this.changeSupport == null) || (paramByte1 == paramByte2)) {
/*  8438 */       return;
/*       */     }
/*  8440 */     firePropertyChange(paramString, Byte.valueOf(paramByte1), Byte.valueOf(paramByte2));
/*       */   }
/*       */ 
/*       */   public void firePropertyChange(String paramString, char paramChar1, char paramChar2)
/*       */   {
/*  8455 */     if ((this.changeSupport == null) || (paramChar1 == paramChar2)) {
/*  8456 */       return;
/*       */     }
/*  8458 */     firePropertyChange(paramString, new Character(paramChar1), new Character(paramChar2));
/*       */   }
/*       */ 
/*       */   public void firePropertyChange(String paramString, short paramShort1, short paramShort2)
/*       */   {
/*  8473 */     if ((this.changeSupport == null) || (paramShort1 == paramShort2)) {
/*  8474 */       return;
/*       */     }
/*  8476 */     firePropertyChange(paramString, Short.valueOf(paramShort1), Short.valueOf(paramShort2));
/*       */   }
/*       */ 
/*       */   public void firePropertyChange(String paramString, long paramLong1, long paramLong2)
/*       */   {
/*  8492 */     if ((this.changeSupport == null) || (paramLong1 == paramLong2)) {
/*  8493 */       return;
/*       */     }
/*  8495 */     firePropertyChange(paramString, Long.valueOf(paramLong1), Long.valueOf(paramLong2));
/*       */   }
/*       */ 
/*       */   public void firePropertyChange(String paramString, float paramFloat1, float paramFloat2)
/*       */   {
/*  8510 */     if ((this.changeSupport == null) || (paramFloat1 == paramFloat2)) {
/*  8511 */       return;
/*       */     }
/*  8513 */     firePropertyChange(paramString, Float.valueOf(paramFloat1), Float.valueOf(paramFloat2));
/*       */   }
/*       */ 
/*       */   public void firePropertyChange(String paramString, double paramDouble1, double paramDouble2)
/*       */   {
/*  8528 */     if ((this.changeSupport == null) || (paramDouble1 == paramDouble2)) {
/*  8529 */       return;
/*       */     }
/*  8531 */     firePropertyChange(paramString, Double.valueOf(paramDouble1), Double.valueOf(paramDouble2));
/*       */   }
/*       */ 
/*       */   private void doSwingSerialization()
/*       */   {
/*  8549 */     Package localPackage = Package.getPackage("javax.swing");
/*       */ 
/*  8556 */     for (Class localClass1 = getClass(); localClass1 != null; 
/*  8557 */       localClass1 = localClass1.getSuperclass())
/*  8558 */       if ((localClass1.getPackage() == localPackage) && (localClass1.getClassLoader() == null))
/*       */       {
/*  8560 */         final Class localClass2 = localClass1;
/*       */ 
/*  8562 */         Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction()
/*       */         {
/*       */           public Object run() {
/*  8565 */             return localClass2.getDeclaredMethods();
/*       */           }
/*       */         });
/*  8568 */         for (int i = arrayOfMethod.length - 1; i >= 0; 
/*  8569 */           i--) {
/*  8570 */           final Method localMethod = arrayOfMethod[i];
/*  8571 */           if (localMethod.getName().equals("compWriteObjectNotify"))
/*       */           {
/*  8574 */             AccessController.doPrivileged(new PrivilegedAction() {
/*       */               public Object run() {
/*  8576 */                 localMethod.setAccessible(true);
/*  8577 */                 return null;
/*       */               }
/*       */             });
/*       */             try
/*       */             {
/*  8582 */               localMethod.invoke(this, (Object[])null);
/*       */             } catch (IllegalAccessException localIllegalAccessException) {
/*       */             }
/*       */             catch (InvocationTargetException localInvocationTargetException) {
/*       */             }
/*  8587 */             return;
/*       */           }
/*       */         }
/*       */       }
/*       */   }
/*       */ 
/*       */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*       */     throws IOException
/*       */   {
/*  8641 */     doSwingSerialization();
/*       */ 
/*  8643 */     paramObjectOutputStream.defaultWriteObject();
/*       */ 
/*  8645 */     AWTEventMulticaster.save(paramObjectOutputStream, "componentL", this.componentListener);
/*  8646 */     AWTEventMulticaster.save(paramObjectOutputStream, "focusL", this.focusListener);
/*  8647 */     AWTEventMulticaster.save(paramObjectOutputStream, "keyL", this.keyListener);
/*  8648 */     AWTEventMulticaster.save(paramObjectOutputStream, "mouseL", this.mouseListener);
/*  8649 */     AWTEventMulticaster.save(paramObjectOutputStream, "mouseMotionL", this.mouseMotionListener);
/*  8650 */     AWTEventMulticaster.save(paramObjectOutputStream, "inputMethodL", this.inputMethodListener);
/*       */ 
/*  8652 */     paramObjectOutputStream.writeObject(null);
/*  8653 */     paramObjectOutputStream.writeObject(this.componentOrientation);
/*       */ 
/*  8655 */     AWTEventMulticaster.save(paramObjectOutputStream, "hierarchyL", this.hierarchyListener);
/*  8656 */     AWTEventMulticaster.save(paramObjectOutputStream, "hierarchyBoundsL", this.hierarchyBoundsListener);
/*       */ 
/*  8658 */     paramObjectOutputStream.writeObject(null);
/*       */ 
/*  8660 */     AWTEventMulticaster.save(paramObjectOutputStream, "mouseWheelL", this.mouseWheelListener);
/*  8661 */     paramObjectOutputStream.writeObject(null);
/*       */   }
/*       */ 
/*       */   private void readObject(ObjectInputStream paramObjectInputStream)
/*       */     throws ClassNotFoundException, IOException
/*       */   {
/*  8677 */     this.objectLock = new Object();
/*       */ 
/*  8679 */     this.acc = AccessController.getContext();
/*       */ 
/*  8681 */     paramObjectInputStream.defaultReadObject();
/*       */ 
/*  8683 */     this.appContext = AppContext.getAppContext();
/*  8684 */     this.coalescingEnabled = checkCoalescing();
/*  8685 */     if (this.componentSerializedDataVersion < 4)
/*       */     {
/*  8690 */       this.focusable = true;
/*  8691 */       this.isFocusTraversableOverridden = 0;
/*  8692 */       initializeFocusTraversalKeys();
/*  8693 */       this.focusTraversalKeysEnabled = true;
/*       */     }
/*       */     Object localObject1;
/*  8697 */     while (null != (localObject1 = paramObjectInputStream.readObject())) {
/*  8698 */       localObject2 = ((String)localObject1).intern();
/*       */ 
/*  8700 */       if ("componentL" == localObject2) {
/*  8701 */         addComponentListener((ComponentListener)paramObjectInputStream.readObject());
/*       */       }
/*  8703 */       else if ("focusL" == localObject2) {
/*  8704 */         addFocusListener((FocusListener)paramObjectInputStream.readObject());
/*       */       }
/*  8706 */       else if ("keyL" == localObject2) {
/*  8707 */         addKeyListener((KeyListener)paramObjectInputStream.readObject());
/*       */       }
/*  8709 */       else if ("mouseL" == localObject2) {
/*  8710 */         addMouseListener((MouseListener)paramObjectInputStream.readObject());
/*       */       }
/*  8712 */       else if ("mouseMotionL" == localObject2) {
/*  8713 */         addMouseMotionListener((MouseMotionListener)paramObjectInputStream.readObject());
/*       */       }
/*  8715 */       else if ("inputMethodL" == localObject2) {
/*  8716 */         addInputMethodListener((InputMethodListener)paramObjectInputStream.readObject());
/*       */       }
/*       */       else {
/*  8719 */         paramObjectInputStream.readObject();
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  8724 */     Object localObject2 = null;
/*       */     try
/*       */     {
/*  8727 */       localObject2 = paramObjectInputStream.readObject();
/*       */     }
/*       */     catch (OptionalDataException localOptionalDataException1)
/*       */     {
/*  8736 */       if (!localOptionalDataException1.eof) {
/*  8737 */         throw localOptionalDataException1;
/*       */       }
/*       */     }
/*       */ 
/*  8741 */     if (localObject2 != null)
/*  8742 */       this.componentOrientation = ((ComponentOrientation)localObject2);
/*       */     else {
/*  8744 */       this.componentOrientation = ComponentOrientation.UNKNOWN;
/*       */     }
/*       */     try
/*       */     {
/*  8748 */       while (null != (localObject1 = paramObjectInputStream.readObject())) {
/*  8749 */         String str1 = ((String)localObject1).intern();
/*       */ 
/*  8751 */         if ("hierarchyL" == str1) {
/*  8752 */           addHierarchyListener((HierarchyListener)paramObjectInputStream.readObject());
/*       */         }
/*  8754 */         else if ("hierarchyBoundsL" == str1) {
/*  8755 */           addHierarchyBoundsListener((HierarchyBoundsListener)paramObjectInputStream.readObject());
/*       */         }
/*       */         else
/*       */         {
/*  8760 */           paramObjectInputStream.readObject();
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*       */     }
/*       */     catch (OptionalDataException localOptionalDataException2)
/*       */     {
/*  8771 */       if (!localOptionalDataException2.eof) {
/*  8772 */         throw localOptionalDataException2;
/*       */       }
/*       */     }
/*       */     try
/*       */     {
/*  8777 */       while (null != (localObject1 = paramObjectInputStream.readObject())) {
/*  8778 */         String str2 = ((String)localObject1).intern();
/*       */ 
/*  8780 */         if ("mouseWheelL" == str2) {
/*  8781 */           addMouseWheelListener((MouseWheelListener)paramObjectInputStream.readObject());
/*       */         }
/*       */         else
/*       */         {
/*  8785 */           paramObjectInputStream.readObject();
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*       */     }
/*       */     catch (OptionalDataException localOptionalDataException3)
/*       */     {
/*  8796 */       if (!localOptionalDataException3.eof) {
/*  8797 */         throw localOptionalDataException3;
/*       */       }
/*       */     }
/*       */ 
/*  8801 */     if (this.popups != null) {
/*  8802 */       int i = this.popups.size();
/*  8803 */       for (int j = 0; j < i; j++) {
/*  8804 */         PopupMenu localPopupMenu = (PopupMenu)this.popups.elementAt(j);
/*  8805 */         localPopupMenu.parent = this;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public void setComponentOrientation(ComponentOrientation paramComponentOrientation)
/*       */   {
/*  8840 */     ComponentOrientation localComponentOrientation = this.componentOrientation;
/*  8841 */     this.componentOrientation = paramComponentOrientation;
/*       */ 
/*  8845 */     firePropertyChange("componentOrientation", localComponentOrientation, paramComponentOrientation);
/*       */ 
/*  8848 */     invalidateIfValid();
/*       */   }
/*       */ 
/*       */   public ComponentOrientation getComponentOrientation()
/*       */   {
/*  8863 */     return this.componentOrientation;
/*       */   }
/*       */ 
/*       */   public void applyComponentOrientation(ComponentOrientation paramComponentOrientation)
/*       */   {
/*  8883 */     if (paramComponentOrientation == null) {
/*  8884 */       throw new NullPointerException();
/*       */     }
/*  8886 */     setComponentOrientation(paramComponentOrientation);
/*       */   }
/*       */ 
/*       */   final boolean canBeFocusOwner()
/*       */   {
/*  8891 */     if ((isEnabled()) && (isDisplayable()) && (isVisible()) && (isFocusable())) {
/*  8892 */       return true;
/*       */     }
/*  8894 */     return false;
/*       */   }
/*       */ 
/*       */   final boolean canBeFocusOwnerRecursively()
/*       */   {
/*  8908 */     if (!canBeFocusOwner()) {
/*  8909 */       return false;
/*       */     }
/*       */ 
/*  8913 */     synchronized (getTreeLock()) {
/*  8914 */       if (this.parent != null) {
/*  8915 */         return this.parent.canContainFocusOwner(this);
/*       */       }
/*       */     }
/*  8918 */     return true;
/*       */   }
/*       */ 
/*       */   final void relocateComponent()
/*       */   {
/*  8925 */     synchronized (getTreeLock()) {
/*  8926 */       if (this.peer == null) {
/*  8927 */         return;
/*       */       }
/*  8929 */       int i = this.x;
/*  8930 */       int j = this.y;
/*  8931 */       for (Container localContainer = getContainer(); 
/*  8932 */         (localContainer != null) && (localContainer.isLightweight()); 
/*  8933 */         localContainer = localContainer.getContainer())
/*       */       {
/*  8935 */         i += localContainer.x;
/*  8936 */         j += localContainer.y;
/*       */       }
/*  8938 */       this.peer.setBounds(i, j, this.width, this.height, 1);
/*       */     }
/*       */   }
/*       */ 
/*       */   Window getContainingWindow()
/*       */   {
/*  8949 */     return SunToolkit.getContainingWindow(this);
/*       */   }
/*       */ 
/*       */   private static native void initIDs();
/*       */ 
/*       */   public AccessibleContext getAccessibleContext()
/*       */   {
/*  8981 */     return this.accessibleContext;
/*       */   }
/*       */ 
/*       */   int getAccessibleIndexInParent()
/*       */   {
/*  9574 */     synchronized (getTreeLock()) {
/*  9575 */       int i = -1;
/*  9576 */       Container localContainer = getParent();
/*  9577 */       if ((localContainer != null) && ((localContainer instanceof Accessible))) {
/*  9578 */         Component[] arrayOfComponent = localContainer.getComponents();
/*  9579 */         for (int j = 0; j < arrayOfComponent.length; j++) {
/*  9580 */           if ((arrayOfComponent[j] instanceof Accessible)) {
/*  9581 */             i++;
/*       */           }
/*  9583 */           if (equals(arrayOfComponent[j])) {
/*  9584 */             return i;
/*       */           }
/*       */         }
/*       */       }
/*  9588 */       return -1;
/*       */     }
/*       */   }
/*       */ 
/*       */   AccessibleStateSet getAccessibleStateSet()
/*       */   {
/*  9600 */     synchronized (getTreeLock()) {
/*  9601 */       AccessibleStateSet localAccessibleStateSet = new AccessibleStateSet();
/*  9602 */       if (isEnabled()) {
/*  9603 */         localAccessibleStateSet.add(AccessibleState.ENABLED);
/*       */       }
/*  9605 */       if (isFocusTraversable()) {
/*  9606 */         localAccessibleStateSet.add(AccessibleState.FOCUSABLE);
/*       */       }
/*  9608 */       if (isVisible()) {
/*  9609 */         localAccessibleStateSet.add(AccessibleState.VISIBLE);
/*       */       }
/*  9611 */       if (isShowing()) {
/*  9612 */         localAccessibleStateSet.add(AccessibleState.SHOWING);
/*       */       }
/*  9614 */       if (isFocusOwner()) {
/*  9615 */         localAccessibleStateSet.add(AccessibleState.FOCUSED);
/*       */       }
/*  9617 */       if ((this instanceof Accessible)) {
/*  9618 */         AccessibleContext localAccessibleContext1 = ((Accessible)this).getAccessibleContext();
/*  9619 */         if (localAccessibleContext1 != null) {
/*  9620 */           Accessible localAccessible = localAccessibleContext1.getAccessibleParent();
/*  9621 */           if (localAccessible != null) {
/*  9622 */             AccessibleContext localAccessibleContext2 = localAccessible.getAccessibleContext();
/*  9623 */             if (localAccessibleContext2 != null) {
/*  9624 */               AccessibleSelection localAccessibleSelection = localAccessibleContext2.getAccessibleSelection();
/*  9625 */               if (localAccessibleSelection != null) {
/*  9626 */                 localAccessibleStateSet.add(AccessibleState.SELECTABLE);
/*  9627 */                 int i = localAccessibleContext1.getAccessibleIndexInParent();
/*  9628 */                 if ((i >= 0) && 
/*  9629 */                   (localAccessibleSelection.isAccessibleChildSelected(i))) {
/*  9630 */                   localAccessibleStateSet.add(AccessibleState.SELECTED);
/*       */                 }
/*       */               }
/*       */             }
/*       */           }
/*       */         }
/*       */       }
/*       */ 
/*  9638 */       if ((isInstanceOf(this, "javax.swing.JComponent")) && 
/*  9639 */         (((JComponent)this).isOpaque())) {
/*  9640 */         localAccessibleStateSet.add(AccessibleState.OPAQUE);
/*       */       }
/*       */ 
/*  9643 */       return localAccessibleStateSet;
/*       */     }
/*       */   }
/*       */ 
/*       */   static boolean isInstanceOf(Object paramObject, String paramString)
/*       */   {
/*  9655 */     if (paramObject == null) return false;
/*  9656 */     if (paramString == null) return false;
/*       */ 
/*  9658 */     Class localClass = paramObject.getClass();
/*  9659 */     while (localClass != null) {
/*  9660 */       if (localClass.getName().equals(paramString)) {
/*  9661 */         return true;
/*       */       }
/*  9663 */       localClass = localClass.getSuperclass();
/*       */     }
/*  9665 */     return false;
/*       */   }
/*       */ 
/*       */   final boolean areBoundsValid()
/*       */   {
/*  9680 */     Container localContainer = getContainer();
/*  9681 */     return (localContainer == null) || (localContainer.isValid()) || (localContainer.getLayout() == null);
/*       */   }
/*       */ 
/*       */   void applyCompoundShape(Region paramRegion)
/*       */   {
/*  9689 */     checkTreeLock();
/*       */ 
/*  9691 */     if (!areBoundsValid()) {
/*  9692 */       if (mixingLog.isLoggable(500)) {
/*  9693 */         mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid());
/*       */       }
/*  9695 */       return;
/*       */     }
/*       */ 
/*  9698 */     if (!isLightweight()) {
/*  9699 */       ComponentPeer localComponentPeer = getPeer();
/*  9700 */       if (localComponentPeer != null)
/*       */       {
/*  9706 */         if (paramRegion.isEmpty()) {
/*  9707 */           paramRegion = Region.EMPTY_REGION;
/*       */         }
/*       */ 
/*  9716 */         if (paramRegion.equals(getNormalShape())) {
/*  9717 */           if (this.compoundShape == null) {
/*  9718 */             return;
/*       */           }
/*  9720 */           this.compoundShape = null;
/*  9721 */           localComponentPeer.applyShape(null);
/*       */         } else {
/*  9723 */           if (paramRegion.equals(getAppliedShape())) {
/*  9724 */             return;
/*       */           }
/*  9726 */           this.compoundShape = paramRegion;
/*  9727 */           Point localPoint = getLocationOnWindow();
/*  9728 */           if (mixingLog.isLoggable(400)) {
/*  9729 */             mixingLog.fine("this = " + this + "; compAbsolute=" + localPoint + "; shape=" + paramRegion);
/*       */           }
/*       */ 
/*  9732 */           localComponentPeer.applyShape(paramRegion.getTranslatedRegion(-localPoint.x, -localPoint.y));
/*       */         }
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private Region getAppliedShape()
/*       */   {
/*  9744 */     checkTreeLock();
/*       */ 
/*  9746 */     return (this.compoundShape == null) || (isLightweight()) ? getNormalShape() : this.compoundShape;
/*       */   }
/*       */ 
/*       */   Point getLocationOnWindow() {
/*  9750 */     checkTreeLock();
/*  9751 */     Point localPoint = getLocation();
/*       */ 
/*  9753 */     for (Container localContainer = getContainer(); 
/*  9754 */       (localContainer != null) && (!(localContainer instanceof Window)); 
/*  9755 */       localContainer = localContainer.getContainer())
/*       */     {
/*  9757 */       localPoint.x += localContainer.getX();
/*  9758 */       localPoint.y += localContainer.getY();
/*       */     }
/*       */ 
/*  9761 */     return localPoint;
/*       */   }
/*       */ 
/*       */   final Region getNormalShape()
/*       */   {
/*  9768 */     checkTreeLock();
/*       */ 
/*  9770 */     Point localPoint = getLocationOnWindow();
/*  9771 */     return Region.getInstanceXYWH(localPoint.x, localPoint.y, getWidth(), getHeight());
/*       */   }
/*       */ 
/*       */   Region getOpaqueShape()
/*       */   {
/*  9793 */     checkTreeLock();
/*  9794 */     if (this.mixingCutoutRegion != null) {
/*  9795 */       return this.mixingCutoutRegion;
/*       */     }
/*  9797 */     return getNormalShape();
/*       */   }
/*       */ 
/*       */   final int getSiblingIndexAbove()
/*       */   {
/*  9802 */     checkTreeLock();
/*  9803 */     Container localContainer = getContainer();
/*  9804 */     if (localContainer == null) {
/*  9805 */       return -1;
/*       */     }
/*       */ 
/*  9808 */     int i = localContainer.getComponentZOrder(this) - 1;
/*       */ 
/*  9810 */     return i < 0 ? -1 : i;
/*       */   }
/*       */ 
/*       */   final ComponentPeer getHWPeerAboveMe() {
/*  9814 */     checkTreeLock();
/*       */ 
/*  9816 */     Container localContainer = getContainer();
/*  9817 */     int i = getSiblingIndexAbove();
/*       */ 
/*  9819 */     while (localContainer != null) {
/*  9820 */       for (int j = i; j > -1; j--) {
/*  9821 */         Component localComponent = localContainer.getComponent(j);
/*  9822 */         if ((localComponent != null) && (localComponent.isDisplayable()) && (!localComponent.isLightweight())) {
/*  9823 */           return localComponent.getPeer();
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*  9830 */       if (!localContainer.isLightweight())
/*       */       {
/*       */         break;
/*       */       }
/*  9834 */       i = localContainer.getSiblingIndexAbove();
/*  9835 */       localContainer = localContainer.getContainer();
/*       */     }
/*       */ 
/*  9838 */     return null;
/*       */   }
/*       */ 
/*       */   final int getSiblingIndexBelow() {
/*  9842 */     checkTreeLock();
/*  9843 */     Container localContainer = getContainer();
/*  9844 */     if (localContainer == null) {
/*  9845 */       return -1;
/*       */     }
/*       */ 
/*  9848 */     int i = localContainer.getComponentZOrder(this) + 1;
/*       */ 
/*  9850 */     return i >= localContainer.getComponentCount() ? -1 : i;
/*       */   }
/*       */ 
/*       */   final boolean isNonOpaqueForMixing() {
/*  9854 */     return (this.mixingCutoutRegion != null) && (this.mixingCutoutRegion.isEmpty());
/*       */   }
/*       */ 
/*       */   private Region calculateCurrentShape()
/*       */   {
/*  9859 */     checkTreeLock();
/*  9860 */     Region localRegion = getNormalShape();
/*       */ 
/*  9862 */     if (mixingLog.isLoggable(500)) {
/*  9863 */       mixingLog.fine("this = " + this + "; normalShape=" + localRegion);
/*       */     }
/*       */ 
/*  9866 */     if (getContainer() != null) {
/*  9867 */       Object localObject = this;
/*  9868 */       Container localContainer = ((Component)localObject).getContainer();
/*       */ 
/*  9870 */       while (localContainer != null) {
/*  9871 */         for (int i = ((Component)localObject).getSiblingIndexAbove(); i != -1; i--)
/*       */         {
/*  9879 */           Component localComponent = localContainer.getComponent(i);
/*  9880 */           if ((localComponent.isLightweight()) && (localComponent.isShowing())) {
/*  9881 */             localRegion = localRegion.getDifference(localComponent.getOpaqueShape());
/*       */           }
/*       */         }
/*       */ 
/*  9885 */         if (!localContainer.isLightweight()) break;
/*  9886 */         localRegion = localRegion.getIntersection(localContainer.getNormalShape());
/*       */ 
/*  9891 */         localObject = localContainer;
/*  9892 */         localContainer = localContainer.getContainer();
/*       */       }
/*       */     }
/*       */ 
/*  9896 */     if (mixingLog.isLoggable(500)) {
/*  9897 */       mixingLog.fine("currentShape=" + localRegion);
/*       */     }
/*       */ 
/*  9900 */     return localRegion;
/*       */   }
/*       */ 
/*       */   void applyCurrentShape() {
/*  9904 */     checkTreeLock();
/*  9905 */     if (!areBoundsValid()) {
/*  9906 */       if (mixingLog.isLoggable(500)) {
/*  9907 */         mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid());
/*       */       }
/*  9909 */       return;
/*       */     }
/*  9911 */     if (mixingLog.isLoggable(500)) {
/*  9912 */       mixingLog.fine("this = " + this);
/*       */     }
/*  9914 */     applyCompoundShape(calculateCurrentShape());
/*       */   }
/*       */ 
/*       */   final void subtractAndApplyShape(Region paramRegion) {
/*  9918 */     checkTreeLock();
/*       */ 
/*  9920 */     if (mixingLog.isLoggable(500)) {
/*  9921 */       mixingLog.fine("this = " + this + "; s=" + paramRegion);
/*       */     }
/*       */ 
/*  9924 */     applyCompoundShape(getAppliedShape().getDifference(paramRegion));
/*       */   }
/*       */ 
/*       */   private final void applyCurrentShapeBelowMe() {
/*  9928 */     checkTreeLock();
/*  9929 */     Object localObject = getContainer();
/*  9930 */     if ((localObject != null) && (((Container)localObject).isShowing()))
/*       */     {
/*  9932 */       ((Container)localObject).recursiveApplyCurrentShape(getSiblingIndexBelow());
/*       */ 
/*  9935 */       Container localContainer = ((Container)localObject).getContainer();
/*  9936 */       while ((!((Container)localObject).isOpaque()) && (localContainer != null)) {
/*  9937 */         localContainer.recursiveApplyCurrentShape(((Container)localObject).getSiblingIndexBelow());
/*       */ 
/*  9939 */         localObject = localContainer;
/*  9940 */         localContainer = ((Container)localObject).getContainer();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   final void subtractAndApplyShapeBelowMe() {
/*  9946 */     checkTreeLock();
/*  9947 */     Object localObject = getContainer();
/*  9948 */     if ((localObject != null) && (isShowing())) {
/*  9949 */       Region localRegion = getOpaqueShape();
/*       */ 
/*  9952 */       ((Container)localObject).recursiveSubtractAndApplyShape(localRegion, getSiblingIndexBelow());
/*       */ 
/*  9955 */       Container localContainer = ((Container)localObject).getContainer();
/*  9956 */       while ((!((Container)localObject).isOpaque()) && (localContainer != null)) {
/*  9957 */         localContainer.recursiveSubtractAndApplyShape(localRegion, ((Container)localObject).getSiblingIndexBelow());
/*       */ 
/*  9959 */         localObject = localContainer;
/*  9960 */         localContainer = ((Container)localObject).getContainer();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   void mixOnShowing() {
/*  9966 */     synchronized (getTreeLock()) {
/*  9967 */       if (mixingLog.isLoggable(500)) {
/*  9968 */         mixingLog.fine("this = " + this);
/*       */       }
/*  9970 */       if (!isMixingNeeded()) {
/*  9971 */         return;
/*       */       }
/*  9973 */       if (isLightweight())
/*  9974 */         subtractAndApplyShapeBelowMe();
/*       */       else
/*  9976 */         applyCurrentShape();
/*       */     }
/*       */   }
/*       */ 
/*       */   void mixOnHiding(boolean paramBoolean)
/*       */   {
/*  9984 */     synchronized (getTreeLock()) {
/*  9985 */       if (mixingLog.isLoggable(500)) {
/*  9986 */         mixingLog.fine("this = " + this + "; isLightweight = " + paramBoolean);
/*       */       }
/*  9988 */       if (!isMixingNeeded()) {
/*  9989 */         return;
/*       */       }
/*  9991 */       if (paramBoolean)
/*  9992 */         applyCurrentShapeBelowMe();
/*       */     }
/*       */   }
/*       */ 
/*       */   void mixOnReshaping()
/*       */   {
/*  9998 */     synchronized (getTreeLock()) {
/*  9999 */       if (mixingLog.isLoggable(500)) {
/* 10000 */         mixingLog.fine("this = " + this);
/*       */       }
/* 10002 */       if (!isMixingNeeded()) {
/* 10003 */         return;
/*       */       }
/* 10005 */       if (isLightweight())
/* 10006 */         applyCurrentShapeBelowMe();
/*       */       else
/* 10008 */         applyCurrentShape();
/*       */     }
/*       */   }
/*       */ 
/*       */   void mixOnZOrderChanging(int paramInt1, int paramInt2)
/*       */   {
/* 10014 */     synchronized (getTreeLock()) {
/* 10015 */       int i = paramInt2 < paramInt1 ? 1 : 0;
/* 10016 */       Container localContainer = getContainer();
/*       */ 
/* 10018 */       if (mixingLog.isLoggable(500)) {
/* 10019 */         mixingLog.fine("this = " + this + "; oldZorder=" + paramInt1 + "; newZorder=" + paramInt2 + "; parent=" + localContainer);
/*       */       }
/*       */ 
/* 10022 */       if (!isMixingNeeded()) {
/* 10023 */         return;
/*       */       }
/* 10025 */       if (isLightweight()) {
/* 10026 */         if (i != 0) {
/* 10027 */           if ((localContainer != null) && (isShowing())) {
/* 10028 */             localContainer.recursiveSubtractAndApplyShape(getOpaqueShape(), getSiblingIndexBelow(), paramInt1);
/*       */           }
/*       */         }
/* 10031 */         else if (localContainer != null) {
/* 10032 */           localContainer.recursiveApplyCurrentShape(paramInt1, paramInt2);
/*       */         }
/*       */ 
/*       */       }
/* 10036 */       else if (i != 0) {
/* 10037 */         applyCurrentShape();
/*       */       }
/* 10039 */       else if (localContainer != null) {
/* 10040 */         Region localRegion = getAppliedShape();
/*       */ 
/* 10042 */         for (int j = paramInt1; j < paramInt2; j++) {
/* 10043 */           Component localComponent = localContainer.getComponent(j);
/* 10044 */           if ((localComponent.isLightweight()) && (localComponent.isShowing())) {
/* 10045 */             localRegion = localRegion.getDifference(localComponent.getOpaqueShape());
/*       */           }
/*       */         }
/* 10048 */         applyCompoundShape(localRegion);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   void mixOnValidating()
/*       */   {
/*       */   }
/*       */ 
/*       */   final boolean isMixingNeeded()
/*       */   {
/* 10061 */     if (SunToolkit.getSunAwtDisableMixing()) {
/* 10062 */       if (mixingLog.isLoggable(300)) {
/* 10063 */         mixingLog.finest("this = " + this + "; Mixing disabled via sun.awt.disableMixing");
/*       */       }
/* 10065 */       return false;
/*       */     }
/* 10067 */     if (!areBoundsValid()) {
/* 10068 */       if (mixingLog.isLoggable(500)) {
/* 10069 */         mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid());
/*       */       }
/* 10071 */       return false;
/*       */     }
/* 10073 */     Window localWindow = getContainingWindow();
/* 10074 */     if (localWindow != null) {
/* 10075 */       if ((!localWindow.hasHeavyweightDescendants()) || (!localWindow.hasLightweightDescendants()) || (localWindow.isDisposing())) {
/* 10076 */         if (mixingLog.isLoggable(500)) {
/* 10077 */           mixingLog.fine("containing window = " + localWindow + "; has h/w descendants = " + localWindow.hasHeavyweightDescendants() + "; has l/w descendants = " + localWindow.hasLightweightDescendants() + "; disposing = " + localWindow.isDisposing());
/*       */         }
/*       */ 
/* 10082 */         return false;
/*       */       }
/*       */     } else {
/* 10085 */       if (mixingLog.isLoggable(500)) {
/* 10086 */         mixingLog.fine("this = " + this + "; containing window is null");
/*       */       }
/* 10088 */       return false;
/*       */     }
/* 10090 */     return true;
/*       */   }
/*       */ 
/*       */   void updateZOrder()
/*       */   {
/* 10098 */     this.peer.setZOrder(getHWPeerAboveMe());
/*       */   }
/*       */ 
/*       */   static
/*       */   {
/*   192 */     log = PlatformLogger.getLogger("java.awt.Component");
/*   193 */     eventLog = PlatformLogger.getLogger("java.awt.event.Component");
/*   194 */     focusLog = PlatformLogger.getLogger("java.awt.focus.Component");
/*   195 */     mixingLog = PlatformLogger.getLogger("java.awt.mixing.Component");
/*       */ 
/*   446 */     focusTraversalKeyPropertyNames = new String[] { "forwardFocusTraversalKeys", "backwardFocusTraversalKeys", "upCycleFocusTraversalKeys", "downCycleFocusTraversalKeys" };
/*       */ 
/*   472 */     LOCK = new AWTTreeLock();
/*       */ 
/*   595 */     Toolkit.loadLibraries();
/*       */ 
/*   597 */     if (!GraphicsEnvironment.isHeadless()) {
/*   598 */       initIDs();
/*       */     }
/*       */ 
/*   601 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.image.incrementaldraw"));
/*       */ 
/*   603 */     isInc = (str == null) || (str.equals("true"));
/*       */ 
/*   605 */     str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.image.redrawrate"));
/*       */ 
/*   607 */     incRate = str != null ? Integer.parseInt(str) : 100;
/*       */ 
/*   841 */     AWTAccessor.setComponentAccessor(new AWTAccessor.ComponentAccessor() {
/*       */       public void setBackgroundEraseDisabled(Component paramAnonymousComponent, boolean paramAnonymousBoolean) {
/*   843 */         paramAnonymousComponent.backgroundEraseDisabled = paramAnonymousBoolean;
/*       */       }
/*       */       public boolean getBackgroundEraseDisabled(Component paramAnonymousComponent) {
/*   846 */         return paramAnonymousComponent.backgroundEraseDisabled;
/*       */       }
/*       */       public Rectangle getBounds(Component paramAnonymousComponent) {
/*   849 */         return new Rectangle(paramAnonymousComponent.x, paramAnonymousComponent.y, paramAnonymousComponent.width, paramAnonymousComponent.height);
/*       */       }
/*       */       public void setMixingCutoutShape(Component paramAnonymousComponent, Shape paramAnonymousShape) {
/*   852 */         Region localRegion = paramAnonymousShape == null ? null : Region.getInstance(paramAnonymousShape, null);
/*       */ 
/*   855 */         synchronized (paramAnonymousComponent.getTreeLock()) {
/*   856 */           int i = 0;
/*   857 */           int j = 0;
/*       */ 
/*   859 */           if (!paramAnonymousComponent.isNonOpaqueForMixing()) {
/*   860 */             j = 1;
/*       */           }
/*       */ 
/*   863 */           paramAnonymousComponent.mixingCutoutRegion = localRegion;
/*       */ 
/*   865 */           if (!paramAnonymousComponent.isNonOpaqueForMixing()) {
/*   866 */             i = 1;
/*       */           }
/*       */ 
/*   869 */           if (paramAnonymousComponent.isMixingNeeded()) {
/*   870 */             if (j != 0) {
/*   871 */               paramAnonymousComponent.mixOnHiding(paramAnonymousComponent.isLightweight());
/*       */             }
/*   873 */             if (i != 0)
/*   874 */               paramAnonymousComponent.mixOnShowing();
/*       */           }
/*       */         }
/*       */       }
/*       */ 
/*       */       public void setGraphicsConfiguration(Component paramAnonymousComponent, GraphicsConfiguration paramAnonymousGraphicsConfiguration)
/*       */       {
/*   883 */         paramAnonymousComponent.setGraphicsConfiguration(paramAnonymousGraphicsConfiguration);
/*       */       }
/*       */       public boolean requestFocus(Component paramAnonymousComponent, CausedFocusEvent.Cause paramAnonymousCause) {
/*   886 */         return paramAnonymousComponent.requestFocus(paramAnonymousCause);
/*       */       }
/*       */       public boolean canBeFocusOwner(Component paramAnonymousComponent) {
/*   889 */         return paramAnonymousComponent.canBeFocusOwner();
/*       */       }
/*       */ 
/*       */       public boolean isVisible(Component paramAnonymousComponent) {
/*   893 */         return paramAnonymousComponent.isVisible_NoClientCode();
/*       */       }
/*       */ 
/*       */       public void setRequestFocusController(RequestFocusController paramAnonymousRequestFocusController)
/*       */       {
/*   898 */         Component.setRequestFocusController(paramAnonymousRequestFocusController);
/*       */       }
/*       */       public AppContext getAppContext(Component paramAnonymousComponent) {
/*   901 */         return paramAnonymousComponent.appContext;
/*       */       }
/*       */       public void setAppContext(Component paramAnonymousComponent, AppContext paramAnonymousAppContext) {
/*   904 */         paramAnonymousComponent.appContext = paramAnonymousAppContext;
/*       */       }
/*       */       public Container getParent(Component paramAnonymousComponent) {
/*   907 */         return paramAnonymousComponent.getParent_NoClientCode();
/*       */       }
/*       */       public void setParent(Component paramAnonymousComponent, Container paramAnonymousContainer) {
/*   910 */         paramAnonymousComponent.parent = paramAnonymousContainer;
/*       */       }
/*       */       public void setSize(Component paramAnonymousComponent, int paramAnonymousInt1, int paramAnonymousInt2) {
/*   913 */         paramAnonymousComponent.width = paramAnonymousInt1;
/*   914 */         paramAnonymousComponent.height = paramAnonymousInt2;
/*       */       }
/*       */       public Point getLocation(Component paramAnonymousComponent) {
/*   917 */         return paramAnonymousComponent.location_NoClientCode();
/*       */       }
/*       */       public void setLocation(Component paramAnonymousComponent, int paramAnonymousInt1, int paramAnonymousInt2) {
/*   920 */         paramAnonymousComponent.x = paramAnonymousInt1;
/*   921 */         paramAnonymousComponent.y = paramAnonymousInt2;
/*       */       }
/*       */       public boolean isEnabled(Component paramAnonymousComponent) {
/*   924 */         return paramAnonymousComponent.isEnabledImpl();
/*       */       }
/*       */       public boolean isDisplayable(Component paramAnonymousComponent) {
/*   927 */         return paramAnonymousComponent.peer != null;
/*       */       }
/*       */       public Cursor getCursor(Component paramAnonymousComponent) {
/*   930 */         return paramAnonymousComponent.getCursor_NoClientCode();
/*       */       }
/*       */       public ComponentPeer getPeer(Component paramAnonymousComponent) {
/*   933 */         return paramAnonymousComponent.peer;
/*       */       }
/*       */       public void setPeer(Component paramAnonymousComponent, ComponentPeer paramAnonymousComponentPeer) {
/*   936 */         paramAnonymousComponent.peer = paramAnonymousComponentPeer;
/*       */       }
/*       */       public boolean isLightweight(Component paramAnonymousComponent) {
/*   939 */         return paramAnonymousComponent.peer instanceof LightweightPeer;
/*       */       }
/*       */       public boolean getIgnoreRepaint(Component paramAnonymousComponent) {
/*   942 */         return paramAnonymousComponent.ignoreRepaint;
/*       */       }
/*       */       public int getWidth(Component paramAnonymousComponent) {
/*   945 */         return paramAnonymousComponent.width;
/*       */       }
/*       */       public int getHeight(Component paramAnonymousComponent) {
/*   948 */         return paramAnonymousComponent.height;
/*       */       }
/*       */       public int getX(Component paramAnonymousComponent) {
/*   951 */         return paramAnonymousComponent.x;
/*       */       }
/*       */       public int getY(Component paramAnonymousComponent) {
/*   954 */         return paramAnonymousComponent.y;
/*       */       }
/*       */       public Color getForeground(Component paramAnonymousComponent) {
/*   957 */         return paramAnonymousComponent.foreground;
/*       */       }
/*       */       public Color getBackground(Component paramAnonymousComponent) {
/*   960 */         return paramAnonymousComponent.background;
/*       */       }
/*       */       public void setBackground(Component paramAnonymousComponent, Color paramAnonymousColor) {
/*   963 */         paramAnonymousComponent.background = paramAnonymousColor;
/*       */       }
/*       */       public Font getFont(Component paramAnonymousComponent) {
/*   966 */         return paramAnonymousComponent.getFont_NoClientCode();
/*       */       }
/*       */       public void processEvent(Component paramAnonymousComponent, AWTEvent paramAnonymousAWTEvent) {
/*   969 */         paramAnonymousComponent.processEvent(paramAnonymousAWTEvent);
/*       */       }
/*       */ 
/*       */       public AccessControlContext getAccessControlContext(Component paramAnonymousComponent) {
/*   973 */         return paramAnonymousComponent.getAccessControlContext();
/*       */       }
/*       */     });
/*       */   }
/*       */ 
/*       */   static class AWTTreeLock
/*       */   {
/*       */   }
/*       */ 
/*       */   protected abstract class AccessibleAWTComponent extends AccessibleContext
/*       */     implements Serializable, AccessibleComponent
/*       */   {
/*       */     private static final long serialVersionUID = 642321655757800191L;
/*  9005 */     protected ComponentListener accessibleAWTComponentHandler = null;
/*  9006 */     protected FocusListener accessibleAWTFocusHandler = null;
/*       */ 
/*       */     protected AccessibleAWTComponent()
/*       */     {
/*       */     }
/*       */ 
/*       */     public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*       */     {
/*  9067 */       if (this.accessibleAWTComponentHandler == null) {
/*  9068 */         this.accessibleAWTComponentHandler = new AccessibleAWTComponentHandler();
/*  9069 */         Component.this.addComponentListener(this.accessibleAWTComponentHandler);
/*       */       }
/*  9071 */       if (this.accessibleAWTFocusHandler == null) {
/*  9072 */         this.accessibleAWTFocusHandler = new AccessibleAWTFocusHandler();
/*  9073 */         Component.this.addFocusListener(this.accessibleAWTFocusHandler);
/*       */       }
/*  9075 */       super.addPropertyChangeListener(paramPropertyChangeListener);
/*       */     }
/*       */ 
/*       */     public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*       */     {
/*  9086 */       if (this.accessibleAWTComponentHandler != null) {
/*  9087 */         Component.this.removeComponentListener(this.accessibleAWTComponentHandler);
/*  9088 */         this.accessibleAWTComponentHandler = null;
/*       */       }
/*  9090 */       if (this.accessibleAWTFocusHandler != null) {
/*  9091 */         Component.this.removeFocusListener(this.accessibleAWTFocusHandler);
/*  9092 */         this.accessibleAWTFocusHandler = null;
/*       */       }
/*  9094 */       super.removePropertyChangeListener(paramPropertyChangeListener);
/*       */     }
/*       */ 
/*       */     public String getAccessibleName()
/*       */     {
/*  9115 */       return this.accessibleName;
/*       */     }
/*       */ 
/*       */     public String getAccessibleDescription()
/*       */     {
/*  9134 */       return this.accessibleDescription;
/*       */     }
/*       */ 
/*       */     public AccessibleRole getAccessibleRole()
/*       */     {
/*  9145 */       return AccessibleRole.AWT_COMPONENT;
/*       */     }
/*       */ 
/*       */     public AccessibleStateSet getAccessibleStateSet()
/*       */     {
/*  9156 */       return Component.this.getAccessibleStateSet();
/*       */     }
/*       */ 
/*       */     public Accessible getAccessibleParent()
/*       */     {
/*  9169 */       if (this.accessibleParent != null) {
/*  9170 */         return this.accessibleParent;
/*       */       }
/*  9172 */       Container localContainer = Component.this.getParent();
/*  9173 */       if ((localContainer instanceof Accessible)) {
/*  9174 */         return (Accessible)localContainer;
/*       */       }
/*       */ 
/*  9177 */       return null;
/*       */     }
/*       */ 
/*       */     public int getAccessibleIndexInParent()
/*       */     {
/*  9188 */       return Component.this.getAccessibleIndexInParent();
/*       */     }
/*       */ 
/*       */     public int getAccessibleChildrenCount()
/*       */     {
/*  9199 */       return 0;
/*       */     }
/*       */ 
/*       */     public Accessible getAccessibleChild(int paramInt)
/*       */     {
/*  9209 */       return null;
/*       */     }
/*       */ 
/*       */     public Locale getLocale()
/*       */     {
/*  9218 */       return Component.this.getLocale();
/*       */     }
/*       */ 
/*       */     public AccessibleComponent getAccessibleComponent()
/*       */     {
/*  9229 */       return this;
/*       */     }
/*       */ 
/*       */     public Color getBackground()
/*       */     {
/*  9242 */       return Component.this.getBackground();
/*       */     }
/*       */ 
/*       */     public void setBackground(Color paramColor)
/*       */     {
/*  9253 */       Component.this.setBackground(paramColor);
/*       */     }
/*       */ 
/*       */     public Color getForeground()
/*       */     {
/*  9263 */       return Component.this.getForeground();
/*       */     }
/*       */ 
/*       */     public void setForeground(Color paramColor)
/*       */     {
/*  9272 */       Component.this.setForeground(paramColor);
/*       */     }
/*       */ 
/*       */     public Cursor getCursor()
/*       */     {
/*  9282 */       return Component.this.getCursor();
/*       */     }
/*       */ 
/*       */     public void setCursor(Cursor paramCursor)
/*       */     {
/*  9294 */       Component.this.setCursor(paramCursor);
/*       */     }
/*       */ 
/*       */     public Font getFont()
/*       */     {
/*  9304 */       return Component.this.getFont();
/*       */     }
/*       */ 
/*       */     public void setFont(Font paramFont)
/*       */     {
/*  9313 */       Component.this.setFont(paramFont);
/*       */     }
/*       */ 
/*       */     public FontMetrics getFontMetrics(Font paramFont)
/*       */     {
/*  9325 */       if (paramFont == null) {
/*  9326 */         return null;
/*       */       }
/*  9328 */       return Component.this.getFontMetrics(paramFont);
/*       */     }
/*       */ 
/*       */     public boolean isEnabled()
/*       */     {
/*  9338 */       return Component.this.isEnabled();
/*       */     }
/*       */ 
/*       */     public void setEnabled(boolean paramBoolean)
/*       */     {
/*  9347 */       boolean bool = Component.this.isEnabled();
/*  9348 */       Component.this.setEnabled(paramBoolean);
/*  9349 */       if ((paramBoolean != bool) && 
/*  9350 */         (Component.this.accessibleContext != null))
/*  9351 */         if (paramBoolean) {
/*  9352 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
/*       */         }
/*       */         else
/*       */         {
/*  9356 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, null);
/*       */         }
/*       */     }
/*       */ 
/*       */     public boolean isVisible()
/*       */     {
/*  9374 */       return Component.this.isVisible();
/*       */     }
/*       */ 
/*       */     public void setVisible(boolean paramBoolean)
/*       */     {
/*  9383 */       boolean bool = Component.this.isVisible();
/*  9384 */       Component.this.setVisible(paramBoolean);
/*  9385 */       if ((paramBoolean != bool) && 
/*  9386 */         (Component.this.accessibleContext != null))
/*  9387 */         if (paramBoolean) {
/*  9388 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
/*       */         }
/*       */         else
/*       */         {
/*  9392 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
/*       */         }
/*       */     }
/*       */ 
/*       */     public boolean isShowing()
/*       */     {
/*  9410 */       return Component.this.isShowing();
/*       */     }
/*       */ 
/*       */     public boolean contains(Point paramPoint)
/*       */     {
/*  9423 */       return Component.this.contains(paramPoint);
/*       */     }
/*       */ 
/*       */     public Point getLocationOnScreen()
/*       */     {
/*  9433 */       synchronized (Component.this.getTreeLock()) {
/*  9434 */         if (Component.this.isShowing()) {
/*  9435 */           return Component.this.getLocationOnScreen();
/*       */         }
/*  9437 */         return null;
/*       */       }
/*       */     }
/*       */ 
/*       */     public Point getLocation()
/*       */     {
/*  9452 */       return Component.this.getLocation();
/*       */     }
/*       */ 
/*       */     public void setLocation(Point paramPoint)
/*       */     {
/*  9460 */       Component.this.setLocation(paramPoint);
/*       */     }
/*       */ 
/*       */     public Rectangle getBounds()
/*       */     {
/*  9472 */       return Component.this.getBounds();
/*       */     }
/*       */ 
/*       */     public void setBounds(Rectangle paramRectangle)
/*       */     {
/*  9484 */       Component.this.setBounds(paramRectangle);
/*       */     }
/*       */ 
/*       */     public Dimension getSize()
/*       */     {
/*  9499 */       return Component.this.getSize();
/*       */     }
/*       */ 
/*       */     public void setSize(Dimension paramDimension)
/*       */     {
/*  9508 */       Component.this.setSize(paramDimension);
/*       */     }
/*       */ 
/*       */     public Accessible getAccessibleAt(Point paramPoint)
/*       */     {
/*  9524 */       return null;
/*       */     }
/*       */ 
/*       */     public boolean isFocusTraversable()
/*       */     {
/*  9533 */       return Component.this.isFocusTraversable();
/*       */     }
/*       */ 
/*       */     public void requestFocus()
/*       */     {
/*  9540 */       Component.this.requestFocus();
/*       */     }
/*       */ 
/*       */     public void addFocusListener(FocusListener paramFocusListener)
/*       */     {
/*  9550 */       Component.this.addFocusListener(paramFocusListener);
/*       */     }
/*       */ 
/*       */     public void removeFocusListener(FocusListener paramFocusListener)
/*       */     {
/*  9560 */       Component.this.removeFocusListener(paramFocusListener);
/*       */     }
/*       */ 
/*       */     protected class AccessibleAWTComponentHandler
/*       */       implements ComponentListener
/*       */     {
/*       */       protected AccessibleAWTComponentHandler()
/*       */       {
/*       */       }
/*       */ 
/*       */       public void componentHidden(ComponentEvent paramComponentEvent)
/*       */       {
/*  9015 */         if (Component.this.accessibleContext != null)
/*  9016 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
/*       */       }
/*       */ 
/*       */       public void componentShown(ComponentEvent paramComponentEvent)
/*       */       {
/*  9023 */         if (Component.this.accessibleContext != null)
/*  9024 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
/*       */       }
/*       */ 
/*       */       public void componentMoved(ComponentEvent paramComponentEvent)
/*       */       {
/*       */       }
/*       */ 
/*       */       public void componentResized(ComponentEvent paramComponentEvent)
/*       */       {
/*       */       }
/*       */     }
/*       */ 
/*       */     protected class AccessibleAWTFocusHandler
/*       */       implements FocusListener
/*       */     {
/*       */       protected AccessibleAWTFocusHandler()
/*       */       {
/*       */       }
/*       */ 
/*       */       public void focusGained(FocusEvent paramFocusEvent)
/*       */       {
/*  9045 */         if (Component.this.accessibleContext != null)
/*  9046 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
/*       */       }
/*       */ 
/*       */       public void focusLost(FocusEvent paramFocusEvent)
/*       */       {
/*  9052 */         if (Component.this.accessibleContext != null)
/*  9053 */           Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public static enum BaselineResizeBehavior
/*       */   {
/*   729 */     CONSTANT_ASCENT, 
/*       */ 
/*   740 */     CONSTANT_DESCENT, 
/*       */ 
/*   779 */     CENTER_OFFSET, 
/*       */ 
/*   787 */     OTHER;
/*       */   }
/*       */ 
/*       */   protected class BltBufferStrategy extends BufferStrategy
/*       */   {
/*       */     protected BufferCapabilities caps;
/*       */     protected VolatileImage[] backBuffers;
/*       */     protected boolean validatedContents;
/*       */     protected int width;
/*       */     protected int height;
/*       */     private Insets insets;
/*       */ 
/*       */     protected BltBufferStrategy(int paramBufferCapabilities, BufferCapabilities arg3)
/*       */     {
/*       */       Object localObject;
/*  4255 */       this.caps = localObject;
/*  4256 */       createBackBuffers(paramBufferCapabilities - 1);
/*       */     }
/*       */ 
/*       */     public void dispose()
/*       */     {
/*  4264 */       if (this.backBuffers != null) {
/*  4265 */         for (int i = this.backBuffers.length - 1; i >= 0; 
/*  4266 */           i--) {
/*  4267 */           if (this.backBuffers[i] != null) {
/*  4268 */             this.backBuffers[i].flush();
/*  4269 */             this.backBuffers[i] = null;
/*       */           }
/*       */         }
/*       */       }
/*  4273 */       if (Component.this.bufferStrategy == this)
/*  4274 */         Component.this.bufferStrategy = null;
/*       */     }
/*       */ 
/*       */     protected void createBackBuffers(int paramInt)
/*       */     {
/*  4282 */       if (paramInt == 0) {
/*  4283 */         this.backBuffers = null;
/*       */       }
/*       */       else {
/*  4286 */         this.width = Component.this.getWidth();
/*  4287 */         this.height = Component.this.getHeight();
/*  4288 */         this.insets = Component.this.getInsets_NoClientCode();
/*  4289 */         int i = this.width - this.insets.left - this.insets.right;
/*  4290 */         int j = this.height - this.insets.top - this.insets.bottom;
/*       */ 
/*  4295 */         i = Math.max(1, i);
/*  4296 */         j = Math.max(1, j);
/*  4297 */         if (this.backBuffers == null) {
/*  4298 */           this.backBuffers = new VolatileImage[paramInt];
/*       */         }
/*       */         else {
/*  4301 */           for (k = 0; k < paramInt; k++) {
/*  4302 */             if (this.backBuffers[k] != null) {
/*  4303 */               this.backBuffers[k].flush();
/*  4304 */               this.backBuffers[k] = null;
/*       */             }
/*       */           }
/*       */ 
/*       */         }
/*       */ 
/*  4310 */         for (int k = 0; k < paramInt; k++)
/*  4311 */           this.backBuffers[k] = Component.this.createVolatileImage(i, j);
/*       */       }
/*       */     }
/*       */ 
/*       */     public BufferCapabilities getCapabilities()
/*       */     {
/*  4320 */       return this.caps;
/*       */     }
/*       */ 
/*       */     public Graphics getDrawGraphics()
/*       */     {
/*  4327 */       revalidate();
/*  4328 */       Image localImage = getBackBuffer();
/*  4329 */       if (localImage == null) {
/*  4330 */         return Component.this.getGraphics();
/*       */       }
/*  4332 */       SunGraphics2D localSunGraphics2D = (SunGraphics2D)localImage.getGraphics();
/*  4333 */       localSunGraphics2D.constrain(-this.insets.left, -this.insets.top, localImage.getWidth(null) + this.insets.left, localImage.getHeight(null) + this.insets.top);
/*       */ 
/*  4336 */       return localSunGraphics2D;
/*       */     }
/*       */ 
/*       */     Image getBackBuffer()
/*       */     {
/*  4344 */       if (this.backBuffers != null) {
/*  4345 */         return this.backBuffers[(this.backBuffers.length - 1)];
/*       */       }
/*  4347 */       return null;
/*       */     }
/*       */ 
/*       */     public void show()
/*       */     {
/*  4355 */       showSubRegion(this.insets.left, this.insets.top, this.width - this.insets.right, this.height - this.insets.bottom);
/*       */     }
/*       */ 
/*       */     void showSubRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */     {
/*  4370 */       if (this.backBuffers == null) {
/*  4371 */         return;
/*       */       }
/*       */ 
/*  4374 */       paramInt1 -= this.insets.left;
/*  4375 */       paramInt3 -= this.insets.left;
/*  4376 */       paramInt2 -= this.insets.top;
/*  4377 */       paramInt4 -= this.insets.top;
/*  4378 */       Graphics localGraphics = Component.this.getGraphics_NoClientCode();
/*  4379 */       if (localGraphics == null)
/*       */       {
/*  4381 */         return;
/*       */       }
/*       */ 
/*       */       try
/*       */       {
/*  4386 */         localGraphics.translate(this.insets.left, this.insets.top);
/*  4387 */         for (int i = 0; i < this.backBuffers.length; i++) {
/*  4388 */           localGraphics.drawImage(this.backBuffers[i], paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
/*       */ 
/*  4392 */           localGraphics.dispose();
/*  4393 */           localGraphics = null;
/*  4394 */           localGraphics = this.backBuffers[i].getGraphics();
/*       */         }
/*       */       } finally {
/*  4397 */         if (localGraphics != null)
/*  4398 */           localGraphics.dispose();
/*       */       }
/*       */     }
/*       */ 
/*       */     protected void revalidate()
/*       */     {
/*  4407 */       revalidate(true);
/*       */     }
/*       */ 
/*       */     void revalidate(boolean paramBoolean) {
/*  4411 */       this.validatedContents = false;
/*       */ 
/*  4413 */       if (this.backBuffers == null) {
/*  4414 */         return;
/*       */       }
/*       */ 
/*  4417 */       if (paramBoolean) {
/*  4418 */         localObject = Component.this.getInsets_NoClientCode();
/*  4419 */         if ((Component.this.getWidth() != this.width) || (Component.this.getHeight() != this.height) || (!((Insets)localObject).equals(this.insets)))
/*       */         {
/*  4422 */           createBackBuffers(this.backBuffers.length);
/*  4423 */           this.validatedContents = true;
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*  4428 */       Object localObject = Component.this.getGraphicsConfiguration_NoClientCode();
/*  4429 */       int i = this.backBuffers[(this.backBuffers.length - 1)].validate((GraphicsConfiguration)localObject);
/*       */ 
/*  4431 */       if (i == 2) {
/*  4432 */         if (paramBoolean) {
/*  4433 */           createBackBuffers(this.backBuffers.length);
/*       */ 
/*  4435 */           this.backBuffers[(this.backBuffers.length - 1)].validate((GraphicsConfiguration)localObject);
/*       */         }
/*       */ 
/*  4441 */         this.validatedContents = true;
/*  4442 */       } else if (i == 1) {
/*  4443 */         this.validatedContents = true;
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean contentsLost()
/*       */     {
/*  4452 */       if (this.backBuffers == null) {
/*  4453 */         return false;
/*       */       }
/*  4455 */       return this.backBuffers[(this.backBuffers.length - 1)].contentsLost();
/*       */     }
/*       */ 
/*       */     public boolean contentsRestored()
/*       */     {
/*  4464 */       return this.validatedContents;
/*       */     }
/*       */   }
/*       */ 
/*       */   private class BltSubRegionBufferStrategy extends Component.BltBufferStrategy
/*       */     implements SubRegionShowable
/*       */   {
/*       */     protected BltSubRegionBufferStrategy(int paramBufferCapabilities, BufferCapabilities arg3)
/*       */     {
/*  4509 */       super(paramBufferCapabilities, localBufferCapabilities);
/*       */     }
/*       */ 
/*       */     public void show(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  4513 */       showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
/*       */     }
/*       */ 
/*       */     public boolean showIfNotLost(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */     {
/*  4518 */       if (!contentsLost()) {
/*  4519 */         showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
/*  4520 */         return !contentsLost();
/*       */       }
/*  4522 */       return false;
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class DummyRequestFocusController
/*       */     implements RequestFocusController
/*       */   {
/*       */     public boolean acceptRequestFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause)
/*       */     {
/*  7749 */       return true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected class FlipBufferStrategy extends BufferStrategy
/*       */   {
/*       */     protected int numBuffers;
/*       */     protected BufferCapabilities caps;
/*       */     protected Image drawBuffer;
/*       */     protected VolatileImage drawVBuffer;
/*       */     protected boolean validatedContents;
/*       */     int width;
/*       */     int height;
/*       */ 
/*       */     protected FlipBufferStrategy(int paramBufferCapabilities, BufferCapabilities arg3)
/*       */       throws AWTException
/*       */     {
/*  3948 */       if ((!(Component.this instanceof Window)) && (!(Component.this instanceof Canvas)))
/*       */       {
/*  3951 */         throw new ClassCastException("Component must be a Canvas or Window");
/*       */       }
/*       */ 
/*  3954 */       this.numBuffers = paramBufferCapabilities;
/*       */       BufferCapabilities localBufferCapabilities;
/*  3955 */       this.caps = localBufferCapabilities;
/*  3956 */       createBuffers(paramBufferCapabilities, localBufferCapabilities);
/*       */     }
/*       */ 
/*       */     protected void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities)
/*       */       throws AWTException
/*       */     {
/*  3978 */       if (paramInt < 2) {
/*  3979 */         throw new IllegalArgumentException("Number of buffers cannot be less than two");
/*       */       }
/*  3981 */       if (Component.this.peer == null) {
/*  3982 */         throw new IllegalStateException("Component must have a valid peer");
/*       */       }
/*  3984 */       if ((paramBufferCapabilities == null) || (!paramBufferCapabilities.isPageFlipping())) {
/*  3985 */         throw new IllegalArgumentException("Page flipping capabilities must be specified");
/*       */       }
/*       */ 
/*  3990 */       this.width = Component.this.getWidth();
/*  3991 */       this.height = Component.this.getHeight();
/*       */ 
/*  3993 */       if (this.drawBuffer != null)
/*       */       {
/*  3995 */         this.drawBuffer = null;
/*  3996 */         this.drawVBuffer = null;
/*  3997 */         destroyBuffers();
/*       */       }
/*       */ 
/*  4001 */       if ((paramBufferCapabilities instanceof ExtendedBufferCapabilities)) {
/*  4002 */         ExtendedBufferCapabilities localExtendedBufferCapabilities = (ExtendedBufferCapabilities)paramBufferCapabilities;
/*       */ 
/*  4004 */         if (localExtendedBufferCapabilities.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON)
/*       */         {
/*  4010 */           if (!VSyncedBSManager.vsyncAllowed(this)) {
/*  4011 */             paramBufferCapabilities = localExtendedBufferCapabilities.derive(ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT);
/*       */           }
/*       */         }
/*       */       }
/*       */ 
/*  4016 */       Component.this.peer.createBuffers(paramInt, paramBufferCapabilities);
/*  4017 */       updateInternalBuffers();
/*       */     }
/*       */ 
/*       */     private void updateInternalBuffers()
/*       */     {
/*  4026 */       this.drawBuffer = getBackBuffer();
/*  4027 */       if ((this.drawBuffer instanceof VolatileImage))
/*  4028 */         this.drawVBuffer = ((VolatileImage)this.drawBuffer);
/*       */       else
/*  4030 */         this.drawVBuffer = null;
/*       */     }
/*       */ 
/*       */     protected Image getBackBuffer()
/*       */     {
/*  4040 */       if (Component.this.peer != null) {
/*  4041 */         return Component.this.peer.getBackBuffer();
/*       */       }
/*  4043 */       throw new IllegalStateException("Component must have a valid peer");
/*       */     }
/*       */ 
/*       */     protected void flip(BufferCapabilities.FlipContents paramFlipContents)
/*       */     {
/*  4060 */       if (Component.this.peer != null) {
/*  4061 */         Image localImage = getBackBuffer();
/*  4062 */         if (localImage != null) {
/*  4063 */           Component.this.peer.flip(0, 0, localImage.getWidth(null), localImage.getHeight(null), paramFlipContents);
/*       */         }
/*       */       }
/*       */       else
/*       */       {
/*  4068 */         throw new IllegalStateException("Component must have a valid peer");
/*       */       }
/*       */     }
/*       */ 
/*       */     void flipSubRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
/*       */     {
/*  4076 */       if (Component.this.peer != null)
/*  4077 */         Component.this.peer.flip(paramInt1, paramInt2, paramInt3, paramInt4, paramFlipContents);
/*       */       else
/*  4079 */         throw new IllegalStateException("Component must have a valid peer");
/*       */     }
/*       */ 
/*       */     protected void destroyBuffers()
/*       */     {
/*  4088 */       VSyncedBSManager.releaseVsync(this);
/*  4089 */       if (Component.this.peer != null)
/*  4090 */         Component.this.peer.destroyBuffers();
/*       */       else
/*  4092 */         throw new IllegalStateException("Component must have a valid peer");
/*       */     }
/*       */ 
/*       */     public BufferCapabilities getCapabilities()
/*       */     {
/*  4101 */       if ((this.caps instanceof Component.ProxyCapabilities)) {
/*  4102 */         return Component.ProxyCapabilities.access$300((Component.ProxyCapabilities)this.caps);
/*       */       }
/*  4104 */       return this.caps;
/*       */     }
/*       */ 
/*       */     public Graphics getDrawGraphics()
/*       */     {
/*  4115 */       revalidate();
/*  4116 */       return this.drawBuffer.getGraphics();
/*       */     }
/*       */ 
/*       */     protected void revalidate()
/*       */     {
/*  4123 */       revalidate(true);
/*       */     }
/*       */ 
/*       */     void revalidate(boolean paramBoolean) {
/*  4127 */       this.validatedContents = false;
/*       */ 
/*  4129 */       if ((paramBoolean) && ((Component.this.getWidth() != this.width) || (Component.this.getHeight() != this.height)))
/*       */       {
/*       */         try {
/*  4132 */           createBuffers(this.numBuffers, this.caps);
/*       */         }
/*       */         catch (AWTException localAWTException1) {
/*       */         }
/*  4136 */         this.validatedContents = true;
/*       */       }
/*       */ 
/*  4141 */       updateInternalBuffers();
/*       */ 
/*  4144 */       if (this.drawVBuffer != null) {
/*  4145 */         GraphicsConfiguration localGraphicsConfiguration = Component.this.getGraphicsConfiguration_NoClientCode();
/*       */ 
/*  4147 */         int i = this.drawVBuffer.validate(localGraphicsConfiguration);
/*  4148 */         if (i == 2) {
/*       */           try {
/*  4150 */             createBuffers(this.numBuffers, this.caps);
/*       */           }
/*       */           catch (AWTException localAWTException2) {
/*       */           }
/*  4154 */           if (this.drawVBuffer != null)
/*       */           {
/*  4156 */             this.drawVBuffer.validate(localGraphicsConfiguration);
/*       */           }
/*  4158 */           this.validatedContents = true;
/*  4159 */         } else if (i == 1) {
/*  4160 */           this.validatedContents = true;
/*       */         }
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean contentsLost()
/*       */     {
/*  4170 */       if (this.drawVBuffer == null) {
/*  4171 */         return false;
/*       */       }
/*  4173 */       return this.drawVBuffer.contentsLost();
/*       */     }
/*       */ 
/*       */     public boolean contentsRestored()
/*       */     {
/*  4181 */       return this.validatedContents;
/*       */     }
/*       */ 
/*       */     public void show()
/*       */     {
/*  4189 */       flip(this.caps.getFlipContents());
/*       */     }
/*       */ 
/*       */     void showSubRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */     {
/*  4197 */       flipSubRegion(paramInt1, paramInt2, paramInt3, paramInt4, this.caps.getFlipContents());
/*       */     }
/*       */ 
/*       */     public void dispose()
/*       */     {
/*  4205 */       if (Component.this.bufferStrategy == this) {
/*  4206 */         Component.this.bufferStrategy = null;
/*  4207 */         if (Component.this.peer != null)
/*  4208 */           destroyBuffers();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private class FlipSubRegionBufferStrategy extends Component.FlipBufferStrategy
/*       */     implements SubRegionShowable
/*       */   {
/*       */     protected FlipSubRegionBufferStrategy(int paramBufferCapabilities, BufferCapabilities arg3)
/*       */       throws AWTException
/*       */     {
/*  4479 */       super(paramBufferCapabilities, localBufferCapabilities);
/*       */     }
/*       */ 
/*       */     public void show(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  4483 */       showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
/*       */     }
/*       */ 
/*       */     public boolean showIfNotLost(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*       */     {
/*  4488 */       if (!contentsLost()) {
/*  4489 */         showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
/*  4490 */         return !contentsLost();
/*       */       }
/*  4492 */       return false;
/*       */     }
/*       */   }
/*       */ 
/*       */   private class ProxyCapabilities extends ExtendedBufferCapabilities
/*       */   {
/*       */     private BufferCapabilities orig;
/*       */ 
/*       */     private ProxyCapabilities(BufferCapabilities arg2)
/*       */     {
/*  3849 */       super(localObject.getBackBufferCapabilities(), localObject.getFlipContents() == BufferCapabilities.FlipContents.BACKGROUND ? BufferCapabilities.FlipContents.BACKGROUND : BufferCapabilities.FlipContents.COPIED);
/*       */ 
/*  3855 */       this.orig = localObject;
/*       */     }
/*       */   }
/*       */ 
/*       */   private class SingleBufferStrategy extends BufferStrategy
/*       */   {
/*       */     private BufferCapabilities caps;
/*       */ 
/*       */     public SingleBufferStrategy(BufferCapabilities arg2)
/*       */     {
/*       */       Object localObject;
/*  4540 */       this.caps = localObject;
/*       */     }
/*       */     public BufferCapabilities getCapabilities() {
/*  4543 */       return this.caps;
/*       */     }
/*       */     public Graphics getDrawGraphics() {
/*  4546 */       return Component.this.getGraphics();
/*       */     }
/*       */     public boolean contentsLost() {
/*  4549 */       return false;
/*       */     }
/*       */     public boolean contentsRestored() {
/*  4552 */       return false;
/*       */     }
/*       */ 
/*       */     public void show()
/*       */     {
/*       */     }
/*       */   }
/*       */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.Component
 * JD-Core Version:    0.6.2
 */