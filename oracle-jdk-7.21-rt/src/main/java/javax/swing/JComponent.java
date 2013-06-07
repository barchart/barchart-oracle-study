/*      */ package javax.swing;
/*      */ 
/*      */ import java.applet.Applet;
/*      */ import java.awt.AWTKeyStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Component.BaselineResizeBehavior;
/*      */ import java.awt.Container;
/*      */ import java.awt.Container.AccessibleAWTContainer;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FocusTraversalPolicy;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ContainerEvent;
/*      */ import java.awt.event.ContainerListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.peer.LightweightPeer;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.beans.PropertyVetoException;
/*      */ import java.beans.Transient;
/*      */ import java.beans.VetoableChangeListener;
/*      */ import java.beans.VetoableChangeSupport;
/*      */ import java.io.IOException;
/*      */ import java.io.InvalidObjectException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectInputValidation;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.EventListener;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.Accessible;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.accessibility.AccessibleExtendedComponent;
/*      */ import javax.accessibility.AccessibleKeyBinding;
/*      */ import javax.accessibility.AccessibleRole;
/*      */ import javax.accessibility.AccessibleState;
/*      */ import javax.accessibility.AccessibleStateSet;
/*      */ import javax.swing.border.AbstractBorder;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.border.CompoundBorder;
/*      */ import javax.swing.border.TitledBorder;
/*      */ import javax.swing.event.AncestorListener;
/*      */ import javax.swing.event.EventListenerList;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.table.JTableHeader;
/*      */ import sun.awt.CausedFocusEvent.Cause;
/*      */ import sun.awt.RequestFocusController;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.UIClientPropertyKey;
/*      */ 
/*      */ public abstract class JComponent extends Container
/*      */   implements Serializable, TransferHandler.HasGetTransferHandler
/*      */ {
/*      */   private static final String uiClassID = "ComponentUI";
/*  195 */   private static final Hashtable<ObjectInputStream, ReadObjectCallback> readObjectCallbacks = new Hashtable(1);
/*      */   private static Set<KeyStroke> managingFocusForwardTraversalKeys;
/*      */   private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
/*      */   private static final int NOT_OBSCURED = 0;
/*      */   private static final int PARTIALLY_OBSCURED = 1;
/*      */   private static final int COMPLETELY_OBSCURED = 2;
/*      */   static boolean DEBUG_GRAPHICS_LOADED;
/*  227 */   private static final Object INPUT_VERIFIER_SOURCE_KEY = new StringBuilder("InputVerifierSourceKey");
/*      */   private boolean isAlignmentXSet;
/*      */   private float alignmentX;
/*      */   private boolean isAlignmentYSet;
/*      */   private float alignmentY;
/*      */   protected transient ComponentUI ui;
/*  245 */   protected EventListenerList listenerList = new EventListenerList();
/*      */   private transient ArrayTable clientProperties;
/*      */   private VetoableChangeSupport vetoableChangeSupport;
/*      */   private boolean autoscrolls;
/*      */   private Border border;
/*      */   private int flags;
/*  257 */   private InputVerifier inputVerifier = null;
/*      */ 
/*  259 */   private boolean verifyInputWhenFocusTarget = true;
/*      */   transient Component paintingChild;
/*      */   public static final int WHEN_FOCUSED = 0;
/*      */   public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1;
/*      */   public static final int WHEN_IN_FOCUSED_WINDOW = 2;
/*      */   public static final int UNDEFINED_CONDITION = -1;
/*      */   private static final String KEYBOARD_BINDINGS_KEY = "_KeyboardBindings";
/*      */   private static final String WHEN_IN_FOCUSED_WINDOW_BINDINGS = "_WhenInFocusedWindow";
/*      */   public static final String TOOL_TIP_TEXT_KEY = "ToolTipText";
/*      */   private static final String NEXT_FOCUS = "nextFocus";
/*      */   private JPopupMenu popupMenu;
/*      */   private static final int IS_DOUBLE_BUFFERED = 0;
/*      */   private static final int ANCESTOR_USING_BUFFER = 1;
/*      */   private static final int IS_PAINTING_TILE = 2;
/*      */   private static final int IS_OPAQUE = 3;
/*      */   private static final int KEY_EVENTS_ENABLED = 4;
/*      */   private static final int FOCUS_INPUTMAP_CREATED = 5;
/*      */   private static final int ANCESTOR_INPUTMAP_CREATED = 6;
/*      */   private static final int WIF_INPUTMAP_CREATED = 7;
/*      */   private static final int ACTIONMAP_CREATED = 8;
/*      */   private static final int CREATED_DOUBLE_BUFFER = 9;
/*      */   private static final int IS_PRINTING = 11;
/*      */   private static final int IS_PRINTING_ALL = 12;
/*      */   private static final int IS_REPAINTING = 13;
/*      */   private static final int WRITE_OBJ_COUNTER_FIRST = 14;
/*      */   private static final int RESERVED_1 = 15;
/*      */   private static final int RESERVED_2 = 16;
/*      */   private static final int RESERVED_3 = 17;
/*      */   private static final int RESERVED_4 = 18;
/*      */   private static final int RESERVED_5 = 19;
/*      */   private static final int RESERVED_6 = 20;
/*      */   private static final int WRITE_OBJ_COUNTER_LAST = 21;
/*      */   private static final int REQUEST_FOCUS_DISABLED = 22;
/*      */   private static final int INHERITS_POPUP_MENU = 23;
/*      */   private static final int OPAQUE_SET = 24;
/*      */   private static final int AUTOSCROLLS_SET = 25;
/*      */   private static final int FOCUS_TRAVERSAL_KEYS_FORWARD_SET = 26;
/*      */   private static final int FOCUS_TRAVERSAL_KEYS_BACKWARD_SET = 27;
/*      */   private static final int REVALIDATE_RUNNABLE_SCHEDULED = 28;
/*  360 */   private static List<Rectangle> tempRectangles = new ArrayList(11);
/*      */   private InputMap focusInputMap;
/*      */   private InputMap ancestorInputMap;
/*      */   private ComponentInputMap windowInputMap;
/*      */   private ActionMap actionMap;
/*      */   private static final String defaultLocale = "JComponent.defaultLocale";
/*      */   private static Component componentObtainingGraphicsFrom;
/*  376 */   private static Object componentObtainingGraphicsFromLock = new StringBuilder("componentObtainingGraphicsFrom");
/*      */   private transient Object aaTextInfo;
/* 3554 */   static final RequestFocusController focusController = new RequestFocusController()
/*      */   {
/*      */     public boolean acceptRequestFocus(Component paramAnonymousComponent1, Component paramAnonymousComponent2, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, CausedFocusEvent.Cause paramAnonymousCause)
/*      */     {
/* 3560 */       if ((paramAnonymousComponent2 == null) || (!(paramAnonymousComponent2 instanceof JComponent))) {
/* 3561 */         return true;
/*      */       }
/*      */ 
/* 3564 */       if ((paramAnonymousComponent1 == null) || (!(paramAnonymousComponent1 instanceof JComponent))) {
/* 3565 */         return true;
/*      */       }
/*      */ 
/* 3568 */       JComponent localJComponent1 = (JComponent)paramAnonymousComponent2;
/* 3569 */       if (!localJComponent1.getVerifyInputWhenFocusTarget()) {
/* 3570 */         return true;
/*      */       }
/*      */ 
/* 3573 */       JComponent localJComponent2 = (JComponent)paramAnonymousComponent1;
/* 3574 */       InputVerifier localInputVerifier = localJComponent2.getInputVerifier();
/*      */ 
/* 3576 */       if (localInputVerifier == null) {
/* 3577 */         return true;
/*      */       }
/* 3579 */       Object localObject1 = SwingUtilities.appContextGet(JComponent.INPUT_VERIFIER_SOURCE_KEY);
/*      */ 
/* 3581 */       if (localObject1 == localJComponent2)
/*      */       {
/* 3584 */         return true;
/*      */       }
/* 3586 */       SwingUtilities.appContextPut(JComponent.INPUT_VERIFIER_SOURCE_KEY, localJComponent2);
/*      */       try
/*      */       {
/* 3589 */         return localInputVerifier.shouldYieldFocus(localJComponent2);
/*      */       } finally {
/* 3591 */         if (localObject1 != null)
/*      */         {
/* 3597 */           SwingUtilities.appContextPut(JComponent.INPUT_VERIFIER_SOURCE_KEY, localObject1);
/*      */         }
/*      */         else
/* 3600 */           SwingUtilities.appContextRemove(JComponent.INPUT_VERIFIER_SOURCE_KEY);
/*      */       }
/*      */     }
/* 3554 */   };
/*      */ 
/* 3648 */   protected AccessibleContext accessibleContext = null;
/*      */ 
/*      */   static Graphics safelyGetGraphics(Component paramComponent)
/*      */   {
/*  385 */     return safelyGetGraphics(paramComponent, SwingUtilities.getRoot(paramComponent));
/*      */   }
/*      */ 
/*      */   static Graphics safelyGetGraphics(Component paramComponent1, Component paramComponent2) {
/*  389 */     synchronized (componentObtainingGraphicsFromLock) {
/*  390 */       componentObtainingGraphicsFrom = paramComponent2;
/*  391 */       Graphics localGraphics = paramComponent1.getGraphics();
/*  392 */       componentObtainingGraphicsFrom = null;
/*  393 */       return localGraphics;
/*      */     }
/*      */   }
/*      */ 
/*      */   static void getGraphicsInvoked(Component paramComponent) {
/*  398 */     if (!isComponentObtainingGraphicsFrom(paramComponent)) {
/*  399 */       JRootPane localJRootPane = ((RootPaneContainer)paramComponent).getRootPane();
/*  400 */       if (localJRootPane != null)
/*  401 */         localJRootPane.disableTrueDoubleBuffering();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean isComponentObtainingGraphicsFrom(Component paramComponent)
/*      */   {
/*  412 */     synchronized (componentObtainingGraphicsFromLock) {
/*  413 */       return componentObtainingGraphicsFrom == paramComponent;
/*      */     }
/*      */   }
/*      */ 
/*      */   static Set<KeyStroke> getManagingFocusForwardTraversalKeys()
/*      */   {
/*  422 */     synchronized (JComponent.class) {
/*  423 */       if (managingFocusForwardTraversalKeys == null) {
/*  424 */         managingFocusForwardTraversalKeys = new HashSet(1);
/*  425 */         managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 2));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  430 */     return managingFocusForwardTraversalKeys;
/*      */   }
/*      */ 
/*      */   static Set<KeyStroke> getManagingFocusBackwardTraversalKeys()
/*      */   {
/*  438 */     synchronized (JComponent.class) {
/*  439 */       if (managingFocusBackwardTraversalKeys == null) {
/*  440 */         managingFocusBackwardTraversalKeys = new HashSet(1);
/*  441 */         managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 3));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  447 */     return managingFocusBackwardTraversalKeys;
/*      */   }
/*      */ 
/*      */   private static Rectangle fetchRectangle() {
/*  451 */     synchronized (tempRectangles)
/*      */     {
/*  453 */       int i = tempRectangles.size();
/*      */       Rectangle localRectangle;
/*  454 */       if (i > 0) {
/*  455 */         localRectangle = (Rectangle)tempRectangles.remove(i - 1);
/*      */       }
/*      */       else {
/*  458 */         localRectangle = new Rectangle(0, 0, 0, 0);
/*      */       }
/*  460 */       return localRectangle;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void recycleRectangle(Rectangle paramRectangle) {
/*  465 */     synchronized (tempRectangles) {
/*  466 */       tempRectangles.add(paramRectangle);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setInheritsPopupMenu(boolean paramBoolean)
/*      */   {
/*  489 */     boolean bool = getFlag(23);
/*  490 */     setFlag(23, paramBoolean);
/*  491 */     firePropertyChange("inheritsPopupMenu", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getInheritsPopupMenu()
/*      */   {
/*  501 */     return getFlag(23);
/*      */   }
/*      */ 
/*      */   public void setComponentPopupMenu(JPopupMenu paramJPopupMenu)
/*      */   {
/*  529 */     if (paramJPopupMenu != null) {
/*  530 */       enableEvents(16L);
/*      */     }
/*  532 */     JPopupMenu localJPopupMenu = this.popupMenu;
/*  533 */     this.popupMenu = paramJPopupMenu;
/*  534 */     firePropertyChange("componentPopupMenu", localJPopupMenu, paramJPopupMenu);
/*      */   }
/*      */ 
/*      */   public JPopupMenu getComponentPopupMenu()
/*      */   {
/*  551 */     if (!getInheritsPopupMenu()) {
/*  552 */       return this.popupMenu;
/*      */     }
/*      */ 
/*  555 */     if (this.popupMenu == null)
/*      */     {
/*  557 */       Container localContainer = getParent();
/*  558 */       while (localContainer != null) {
/*  559 */         if ((localContainer instanceof JComponent)) {
/*  560 */           return ((JComponent)localContainer).getComponentPopupMenu();
/*      */         }
/*  562 */         if (((localContainer instanceof Window)) || ((localContainer instanceof Applet)))
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*  567 */         localContainer = localContainer.getParent();
/*      */       }
/*  569 */       return null;
/*      */     }
/*      */ 
/*  572 */     return this.popupMenu;
/*      */   }
/*      */ 
/*      */   public JComponent()
/*      */   {
/*  590 */     enableEvents(8L);
/*  591 */     if (isManagingFocus()) {
/*  592 */       LookAndFeel.installProperty(this, "focusTraversalKeysForward", getManagingFocusForwardTraversalKeys());
/*      */ 
/*  595 */       LookAndFeel.installProperty(this, "focusTraversalKeysBackward", getManagingFocusBackwardTraversalKeys());
/*      */     }
/*      */ 
/*  600 */     super.setLocale(getDefaultLocale());
/*      */   }
/*      */ 
/*      */   public void updateUI()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void setUI(ComponentUI paramComponentUI)
/*      */   {
/*  656 */     uninstallUIAndProperties();
/*      */ 
/*  659 */     this.aaTextInfo = UIManager.getDefaults().get(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
/*      */ 
/*  661 */     ComponentUI localComponentUI = this.ui;
/*  662 */     this.ui = paramComponentUI;
/*  663 */     if (this.ui != null) {
/*  664 */       this.ui.installUI(this);
/*      */     }
/*      */ 
/*  667 */     firePropertyChange("UI", localComponentUI, paramComponentUI);
/*  668 */     revalidate();
/*  669 */     repaint();
/*      */   }
/*      */ 
/*      */   private void uninstallUIAndProperties()
/*      */   {
/*  678 */     if (this.ui != null) {
/*  679 */       this.ui.uninstallUI(this);
/*      */ 
/*  681 */       if (this.clientProperties != null)
/*  682 */         synchronized (this.clientProperties) {
/*  683 */           Object[] arrayOfObject1 = this.clientProperties.getKeys(null);
/*      */ 
/*  685 */           if (arrayOfObject1 != null)
/*  686 */             for (Object localObject1 : arrayOfObject1)
/*  687 */               if ((localObject1 instanceof UIClientPropertyKey))
/*  688 */                 putClientProperty(localObject1, null);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getUIClassID()
/*      */   {
/*  715 */     return "ComponentUI";
/*      */   }
/*      */ 
/*      */   protected Graphics getComponentGraphics(Graphics paramGraphics)
/*      */   {
/*  730 */     Object localObject = paramGraphics;
/*  731 */     if ((this.ui != null) && (DEBUG_GRAPHICS_LOADED) && 
/*  732 */       (DebugGraphics.debugComponentCount() != 0) && (shouldDebugGraphics() != 0) && (!(paramGraphics instanceof DebugGraphics)))
/*      */     {
/*  735 */       localObject = new DebugGraphics(paramGraphics, this);
/*      */     }
/*      */ 
/*  738 */     ((Graphics)localObject).setColor(getForeground());
/*  739 */     ((Graphics)localObject).setFont(getFont());
/*      */ 
/*  741 */     return localObject;
/*      */   }
/*      */ 
/*      */   protected void paintComponent(Graphics paramGraphics)
/*      */   {
/*  775 */     if (this.ui != null) {
/*  776 */       Graphics localGraphics = paramGraphics == null ? null : paramGraphics.create();
/*      */       try {
/*  778 */         this.ui.update(localGraphics, this);
/*      */       }
/*      */       finally {
/*  781 */         localGraphics.dispose();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintChildren(Graphics paramGraphics)
/*      */   {
/*  798 */     Graphics localGraphics1 = paramGraphics;
/*      */ 
/*  800 */     synchronized (getTreeLock()) {
/*  801 */       int i = getComponentCount() - 1;
/*  802 */       if (i < 0) {
/*  803 */         return;
/*      */       }
/*      */ 
/*  807 */       if ((this.paintingChild != null) && ((this.paintingChild instanceof JComponent)) && (this.paintingChild.isOpaque()))
/*      */       {
/*  810 */         while ((i >= 0) && 
/*  811 */           (getComponent(i) != this.paintingChild)) {
/*  810 */           i--;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  816 */       Rectangle localRectangle1 = fetchRectangle();
/*  817 */       int j = (!isOptimizedDrawingEnabled()) && (checkIfChildObscuredBySibling()) ? 1 : 0;
/*      */ 
/*  819 */       Rectangle localRectangle2 = null;
/*  820 */       if (j != 0) {
/*  821 */         localRectangle2 = localGraphics1.getClipBounds();
/*  822 */         if (localRectangle2 == null) {
/*  823 */           localRectangle2 = new Rectangle(0, 0, getWidth(), getHeight());
/*      */         }
/*      */       }
/*      */ 
/*  827 */       boolean bool1 = getFlag(11);
/*  828 */       Window localWindow = SwingUtilities.getWindowAncestor(this);
/*  829 */       int k = (localWindow == null) || (localWindow.isOpaque()) ? 1 : 0;
/*  830 */       for (; i >= 0; i--) {
/*  831 */         Component localComponent = getComponent(i);
/*  832 */         if (localComponent != null)
/*      */         {
/*  836 */           boolean bool2 = localComponent instanceof JComponent;
/*      */ 
/*  840 */           if (((k == 0) || (bool2) || (isLightweightComponent(localComponent))) && (localComponent.isVisible()))
/*      */           {
/*  845 */             Rectangle localRectangle3 = localComponent.getBounds(localRectangle1);
/*      */ 
/*  847 */             boolean bool3 = paramGraphics.hitClip(localRectangle3.x, localRectangle3.y, localRectangle3.width, localRectangle3.height);
/*      */ 
/*  850 */             if (bool3)
/*      */             {
/*      */               int n;
/*  851 */               if ((j != 0) && (i > 0)) {
/*  852 */                 int m = localRectangle3.x;
/*  853 */                 n = localRectangle3.y;
/*  854 */                 int i1 = localRectangle3.width;
/*  855 */                 int i2 = localRectangle3.height;
/*  856 */                 SwingUtilities.computeIntersection(localRectangle2.x, localRectangle2.y, localRectangle2.width, localRectangle2.height, localRectangle3);
/*      */ 
/*  860 */                 if (getObscuredState(i, localRectangle3.x, localRectangle3.y, localRectangle3.width, localRectangle3.height) != 2)
/*      */                 {
/*  864 */                   localRectangle3.x = m;
/*  865 */                   localRectangle3.y = n;
/*  866 */                   localRectangle3.width = i1;
/*  867 */                   localRectangle3.height = i2;
/*      */                 }
/*      */               } else { Graphics localGraphics2 = localGraphics1.create(localRectangle3.x, localRectangle3.y, localRectangle3.width, localRectangle3.height);
/*      */ 
/*  871 */                 localGraphics2.setColor(localComponent.getForeground());
/*  872 */                 localGraphics2.setFont(localComponent.getFont());
/*  873 */                 n = 0;
/*      */                 try {
/*  875 */                   if (bool2) {
/*  876 */                     if (getFlag(1)) {
/*  877 */                       ((JComponent)localComponent).setFlag(1, true);
/*      */ 
/*  879 */                       n = 1;
/*      */                     }
/*  881 */                     if (getFlag(2)) {
/*  882 */                       ((JComponent)localComponent).setFlag(2, true);
/*      */ 
/*  884 */                       n = 1;
/*      */                     }
/*  886 */                     if (!bool1) {
/*  887 */                       localComponent.paint(localGraphics2);
/*      */                     }
/*  890 */                     else if (!getFlag(12)) {
/*  891 */                       localComponent.print(localGraphics2);
/*      */                     }
/*      */                     else {
/*  894 */                       localComponent.printAll(localGraphics2);
/*      */                     }
/*      */ 
/*      */                   }
/*  900 */                   else if (!bool1) {
/*  901 */                     localComponent.paint(localGraphics2);
/*      */                   }
/*  904 */                   else if (!getFlag(12)) {
/*  905 */                     localComponent.print(localGraphics2);
/*      */                   }
/*      */                   else {
/*  908 */                     localComponent.printAll(localGraphics2);
/*      */                   }
/*      */                 }
/*      */                 finally
/*      */                 {
/*  913 */                   localGraphics2.dispose();
/*  914 */                   if (n != 0) {
/*  915 */                     ((JComponent)localComponent).setFlag(1, false);
/*      */ 
/*  917 */                     ((JComponent)localComponent).setFlag(2, false);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  925 */       recycleRectangle(localRectangle1);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintBorder(Graphics paramGraphics)
/*      */   {
/*  945 */     Border localBorder = getBorder();
/*  946 */     if (localBorder != null)
/*  947 */       localBorder.paintBorder(this, paramGraphics, 0, 0, getWidth(), getHeight());
/*      */   }
/*      */ 
/*      */   public void update(Graphics paramGraphics)
/*      */   {
/*  963 */     paint(paramGraphics);
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics)
/*      */   {
/*  993 */     int i = 0;
/*      */ 
/*  995 */     if ((getWidth() <= 0) || (getHeight() <= 0)) {
/*  996 */       return;
/*      */     }
/*      */ 
/*  999 */     Graphics localGraphics1 = getComponentGraphics(paramGraphics);
/* 1000 */     Graphics localGraphics2 = localGraphics1.create();
/*      */     try {
/* 1002 */       RepaintManager localRepaintManager = RepaintManager.currentManager(this);
/* 1003 */       Rectangle localRectangle = localGraphics2.getClipBounds();
/*      */       int k;
/*      */       int j;
/*      */       int m;
/*      */       int n;
/* 1008 */       if (localRectangle == null) {
/* 1009 */         j = k = 0;
/* 1010 */         m = getWidth();
/* 1011 */         n = getHeight();
/*      */       }
/*      */       else {
/* 1014 */         j = localRectangle.x;
/* 1015 */         k = localRectangle.y;
/* 1016 */         m = localRectangle.width;
/* 1017 */         n = localRectangle.height;
/*      */       }
/*      */ 
/* 1020 */       if (m > getWidth()) {
/* 1021 */         m = getWidth();
/*      */       }
/* 1023 */       if (n > getHeight()) {
/* 1024 */         n = getHeight();
/*      */       }
/*      */ 
/* 1027 */       if ((getParent() != null) && (!(getParent() instanceof JComponent))) {
/* 1028 */         adjustPaintFlags();
/* 1029 */         i = 1;
/*      */       }
/*      */ 
/* 1033 */       boolean bool = getFlag(11);
/* 1034 */       if ((!bool) && (localRepaintManager.isDoubleBufferingEnabled()) && (!getFlag(1)) && (isDoubleBuffered()) && ((getFlag(13)) || (localRepaintManager.isPainting())))
/*      */       {
/* 1038 */         localRepaintManager.beginPaint();
/*      */         try {
/* 1040 */           localRepaintManager.paint(this, this, localGraphics2, j, k, m, n);
/*      */         }
/*      */         finally {
/* 1043 */           localRepaintManager.endPaint();
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1048 */         if (localRectangle == null) {
/* 1049 */           localGraphics2.setClip(j, k, m, n);
/*      */         }
/*      */ 
/* 1052 */         if (!rectangleIsObscured(j, k, m, n)) {
/* 1053 */           if (!bool) {
/* 1054 */             paintComponent(localGraphics2);
/* 1055 */             paintBorder(localGraphics2);
/*      */           }
/*      */           else {
/* 1058 */             printComponent(localGraphics2);
/* 1059 */             printBorder(localGraphics2);
/*      */           }
/*      */         }
/* 1062 */         if (!bool) {
/* 1063 */           paintChildren(localGraphics2);
/*      */         }
/*      */         else
/* 1066 */           printChildren(localGraphics2);
/*      */       }
/*      */     }
/*      */     finally {
/* 1070 */       localGraphics2.dispose();
/* 1071 */       if (i != 0) {
/* 1072 */         setFlag(1, false);
/* 1073 */         setFlag(2, false);
/* 1074 */         setFlag(11, false);
/* 1075 */         setFlag(12, false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void paintForceDoubleBuffered(Graphics paramGraphics)
/*      */   {
/* 1085 */     RepaintManager localRepaintManager = RepaintManager.currentManager(this);
/* 1086 */     Rectangle localRectangle = paramGraphics.getClipBounds();
/* 1087 */     localRepaintManager.beginPaint();
/* 1088 */     setFlag(13, true);
/*      */     try {
/* 1090 */       localRepaintManager.paint(this, this, paramGraphics, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */     } finally {
/* 1092 */       localRepaintManager.endPaint();
/* 1093 */       setFlag(13, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isPainting()
/*      */   {
/* 1102 */     Object localObject = this;
/* 1103 */     while (localObject != null) {
/* 1104 */       if (((localObject instanceof JComponent)) && (((JComponent)localObject).getFlag(1)))
/*      */       {
/* 1106 */         return true;
/*      */       }
/* 1108 */       localObject = ((Container)localObject).getParent();
/*      */     }
/* 1110 */     return false;
/*      */   }
/*      */ 
/*      */   private void adjustPaintFlags()
/*      */   {
/* 1116 */     for (Container localContainer = getParent(); localContainer != null; localContainer = localContainer.getParent())
/*      */     {
/* 1118 */       if ((localContainer instanceof JComponent)) {
/* 1119 */         JComponent localJComponent = (JComponent)localContainer;
/* 1120 */         if (localJComponent.getFlag(1))
/* 1121 */           setFlag(1, true);
/* 1122 */         if (localJComponent.getFlag(2))
/* 1123 */           setFlag(2, true);
/* 1124 */         if (localJComponent.getFlag(11))
/* 1125 */           setFlag(11, true);
/* 1126 */         if (!localJComponent.getFlag(12)) break;
/* 1127 */         setFlag(12, true); break;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void printAll(Graphics paramGraphics)
/*      */   {
/* 1144 */     setFlag(12, true);
/*      */     try {
/* 1146 */       print(paramGraphics);
/*      */     }
/*      */     finally {
/* 1149 */       setFlag(12, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void print(Graphics paramGraphics)
/*      */   {
/* 1197 */     setFlag(11, true);
/* 1198 */     firePropertyChange("paintingForPrint", false, true);
/*      */     try {
/* 1200 */       paint(paramGraphics);
/*      */     }
/*      */     finally {
/* 1203 */       setFlag(11, false);
/* 1204 */       firePropertyChange("paintingForPrint", true, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void printComponent(Graphics paramGraphics)
/*      */   {
/* 1218 */     paintComponent(paramGraphics);
/*      */   }
/*      */ 
/*      */   protected void printChildren(Graphics paramGraphics)
/*      */   {
/* 1231 */     paintChildren(paramGraphics);
/*      */   }
/*      */ 
/*      */   protected void printBorder(Graphics paramGraphics)
/*      */   {
/* 1244 */     paintBorder(paramGraphics);
/*      */   }
/*      */ 
/*      */   public boolean isPaintingTile()
/*      */   {
/* 1258 */     return getFlag(2);
/*      */   }
/*      */ 
/*      */   public final boolean isPaintingForPrint()
/*      */   {
/* 1288 */     return getFlag(11);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean isManagingFocus()
/*      */   {
/* 1311 */     return false;
/*      */   }
/*      */ 
/*      */   private void registerNextFocusableComponent() {
/* 1315 */     registerNextFocusableComponent(getNextFocusableComponent());
/*      */   }
/*      */ 
/*      */   private void registerNextFocusableComponent(Component paramComponent)
/*      */   {
/* 1320 */     if (paramComponent == null) {
/* 1321 */       return;
/*      */     }
/*      */ 
/* 1324 */     Container localContainer = isFocusCycleRoot() ? this : getFocusCycleRootAncestor();
/*      */ 
/* 1326 */     Object localObject = localContainer.getFocusTraversalPolicy();
/* 1327 */     if (!(localObject instanceof LegacyGlueFocusTraversalPolicy)) {
/* 1328 */       localObject = new LegacyGlueFocusTraversalPolicy((FocusTraversalPolicy)localObject);
/* 1329 */       localContainer.setFocusTraversalPolicy((FocusTraversalPolicy)localObject);
/*      */     }
/* 1331 */     ((LegacyGlueFocusTraversalPolicy)localObject).setNextFocusableComponent(this, paramComponent);
/*      */   }
/*      */ 
/*      */   private void deregisterNextFocusableComponent()
/*      */   {
/* 1336 */     Component localComponent = getNextFocusableComponent();
/* 1337 */     if (localComponent == null) {
/* 1338 */       return;
/*      */     }
/*      */ 
/* 1341 */     Container localContainer = isFocusCycleRoot() ? this : getFocusCycleRootAncestor();
/*      */ 
/* 1343 */     if (localContainer == null) {
/* 1344 */       return;
/*      */     }
/* 1346 */     FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
/* 1347 */     if ((localFocusTraversalPolicy instanceof LegacyGlueFocusTraversalPolicy))
/* 1348 */       ((LegacyGlueFocusTraversalPolicy)localFocusTraversalPolicy).unsetNextFocusableComponent(this, localComponent);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setNextFocusableComponent(Component paramComponent)
/*      */   {
/* 1376 */     boolean bool = isDisplayable();
/* 1377 */     if (bool) {
/* 1378 */       deregisterNextFocusableComponent();
/*      */     }
/* 1380 */     putClientProperty("nextFocus", paramComponent);
/* 1381 */     if (bool)
/* 1382 */       registerNextFocusableComponent(paramComponent);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Component getNextFocusableComponent()
/*      */   {
/* 1406 */     return (Component)getClientProperty("nextFocus");
/*      */   }
/*      */ 
/*      */   public void setRequestFocusEnabled(boolean paramBoolean)
/*      */   {
/* 1433 */     setFlag(22, !paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean isRequestFocusEnabled()
/*      */   {
/* 1454 */     return !getFlag(22);
/*      */   }
/*      */ 
/*      */   public void requestFocus()
/*      */   {
/* 1476 */     super.requestFocus();
/*      */   }
/*      */ 
/*      */   public boolean requestFocus(boolean paramBoolean)
/*      */   {
/* 1502 */     return super.requestFocus(paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean requestFocusInWindow()
/*      */   {
/* 1523 */     return super.requestFocusInWindow();
/*      */   }
/*      */ 
/*      */   protected boolean requestFocusInWindow(boolean paramBoolean)
/*      */   {
/* 1545 */     return super.requestFocusInWindow(paramBoolean);
/*      */   }
/*      */ 
/*      */   public void grabFocus()
/*      */   {
/* 1561 */     requestFocus();
/*      */   }
/*      */ 
/*      */   public void setVerifyInputWhenFocusTarget(boolean paramBoolean)
/*      */   {
/* 1587 */     boolean bool = this.verifyInputWhenFocusTarget;
/*      */ 
/* 1589 */     this.verifyInputWhenFocusTarget = paramBoolean;
/* 1590 */     firePropertyChange("verifyInputWhenFocusTarget", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getVerifyInputWhenFocusTarget()
/*      */   {
/* 1610 */     return this.verifyInputWhenFocusTarget;
/*      */   }
/*      */ 
/*      */   public FontMetrics getFontMetrics(Font paramFont)
/*      */   {
/* 1624 */     return SwingUtilities2.getFontMetrics(this, paramFont);
/*      */   }
/*      */ 
/*      */   public void setPreferredSize(Dimension paramDimension)
/*      */   {
/* 1638 */     super.setPreferredSize(paramDimension);
/*      */   }
/*      */ 
/*      */   @Transient
/*      */   public Dimension getPreferredSize()
/*      */   {
/* 1655 */     if (isPreferredSizeSet()) {
/* 1656 */       return super.getPreferredSize();
/*      */     }
/* 1658 */     Dimension localDimension = null;
/* 1659 */     if (this.ui != null) {
/* 1660 */       localDimension = this.ui.getPreferredSize(this);
/*      */     }
/* 1662 */     return localDimension != null ? localDimension : super.getPreferredSize();
/*      */   }
/*      */ 
/*      */   public void setMaximumSize(Dimension paramDimension)
/*      */   {
/* 1681 */     super.setMaximumSize(paramDimension);
/*      */   }
/*      */ 
/*      */   @Transient
/*      */   public Dimension getMaximumSize()
/*      */   {
/* 1697 */     if (isMaximumSizeSet()) {
/* 1698 */       return super.getMaximumSize();
/*      */     }
/* 1700 */     Dimension localDimension = null;
/* 1701 */     if (this.ui != null) {
/* 1702 */       localDimension = this.ui.getMaximumSize(this);
/*      */     }
/* 1704 */     return localDimension != null ? localDimension : super.getMaximumSize();
/*      */   }
/*      */ 
/*      */   public void setMinimumSize(Dimension paramDimension)
/*      */   {
/* 1722 */     super.setMinimumSize(paramDimension);
/*      */   }
/*      */ 
/*      */   @Transient
/*      */   public Dimension getMinimumSize()
/*      */   {
/* 1737 */     if (isMinimumSizeSet()) {
/* 1738 */       return super.getMinimumSize();
/*      */     }
/* 1740 */     Dimension localDimension = null;
/* 1741 */     if (this.ui != null) {
/* 1742 */       localDimension = this.ui.getMinimumSize(this);
/*      */     }
/* 1744 */     return localDimension != null ? localDimension : super.getMinimumSize();
/*      */   }
/*      */ 
/*      */   public boolean contains(int paramInt1, int paramInt2)
/*      */   {
/* 1756 */     return this.ui != null ? this.ui.contains(this, paramInt1, paramInt2) : super.contains(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public void setBorder(Border paramBorder)
/*      */   {
/* 1791 */     Border localBorder = this.border;
/*      */ 
/* 1793 */     this.border = paramBorder;
/* 1794 */     firePropertyChange("border", localBorder, paramBorder);
/* 1795 */     if (paramBorder != localBorder) {
/* 1796 */       if ((paramBorder == null) || (localBorder == null) || (!paramBorder.getBorderInsets(this).equals(localBorder.getBorderInsets(this))))
/*      */       {
/* 1798 */         revalidate();
/*      */       }
/* 1800 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Border getBorder()
/*      */   {
/* 1812 */     return this.border;
/*      */   }
/*      */ 
/*      */   public Insets getInsets()
/*      */   {
/* 1823 */     if (this.border != null) {
/* 1824 */       return this.border.getBorderInsets(this);
/*      */     }
/* 1826 */     return super.getInsets();
/*      */   }
/*      */ 
/*      */   public Insets getInsets(Insets paramInsets)
/*      */   {
/* 1844 */     if (paramInsets == null) {
/* 1845 */       paramInsets = new Insets(0, 0, 0, 0);
/*      */     }
/* 1847 */     if (this.border != null) {
/* 1848 */       if ((this.border instanceof AbstractBorder)) {
/* 1849 */         return ((AbstractBorder)this.border).getBorderInsets(this, paramInsets);
/*      */       }
/*      */ 
/* 1853 */       return this.border.getBorderInsets(this);
/*      */     }
/*      */ 
/* 1858 */     paramInsets.left = (paramInsets.top = paramInsets.right = paramInsets.bottom = 0);
/* 1859 */     return paramInsets;
/*      */   }
/*      */ 
/*      */   public float getAlignmentY()
/*      */   {
/* 1872 */     if (this.isAlignmentYSet) {
/* 1873 */       return this.alignmentY;
/*      */     }
/* 1875 */     return super.getAlignmentY();
/*      */   }
/*      */ 
/*      */   public void setAlignmentY(float paramFloat)
/*      */   {
/* 1887 */     this.alignmentY = (paramFloat < 0.0F ? 0.0F : paramFloat > 1.0F ? 1.0F : paramFloat);
/* 1888 */     this.isAlignmentYSet = true;
/*      */   }
/*      */ 
/*      */   public float getAlignmentX()
/*      */   {
/* 1901 */     if (this.isAlignmentXSet) {
/* 1902 */       return this.alignmentX;
/*      */     }
/* 1904 */     return super.getAlignmentX();
/*      */   }
/*      */ 
/*      */   public void setAlignmentX(float paramFloat)
/*      */   {
/* 1916 */     this.alignmentX = (paramFloat < 0.0F ? 0.0F : paramFloat > 1.0F ? 1.0F : paramFloat);
/* 1917 */     this.isAlignmentXSet = true;
/*      */   }
/*      */ 
/*      */   public void setInputVerifier(InputVerifier paramInputVerifier)
/*      */   {
/* 1931 */     InputVerifier localInputVerifier = (InputVerifier)getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER);
/*      */ 
/* 1933 */     putClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER, paramInputVerifier);
/* 1934 */     firePropertyChange("inputVerifier", localInputVerifier, paramInputVerifier);
/*      */   }
/*      */ 
/*      */   public InputVerifier getInputVerifier()
/*      */   {
/* 1945 */     return (InputVerifier)getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER);
/*      */   }
/*      */ 
/*      */   public Graphics getGraphics()
/*      */   {
/* 1955 */     if ((DEBUG_GRAPHICS_LOADED) && (shouldDebugGraphics() != 0)) {
/* 1956 */       DebugGraphics localDebugGraphics = new DebugGraphics(super.getGraphics(), this);
/*      */ 
/* 1958 */       return localDebugGraphics;
/*      */     }
/* 1960 */     return super.getGraphics();
/*      */   }
/*      */ 
/*      */   public void setDebugGraphicsOptions(int paramInt)
/*      */   {
/* 1990 */     DebugGraphics.setDebugOptions(this, paramInt);
/*      */   }
/*      */ 
/*      */   public int getDebugGraphicsOptions()
/*      */   {
/* 2009 */     return DebugGraphics.getDebugOptions(this);
/*      */   }
/*      */ 
/*      */   int shouldDebugGraphics()
/*      */   {
/* 2018 */     return DebugGraphics.shouldComponentDebug(this);
/*      */   }
/*      */ 
/*      */   public void registerKeyboardAction(ActionListener paramActionListener, String paramString, KeyStroke paramKeyStroke, int paramInt)
/*      */   {
/* 2087 */     InputMap localInputMap = getInputMap(paramInt, true);
/*      */ 
/* 2089 */     if (localInputMap != null) {
/* 2090 */       ActionMap localActionMap = getActionMap(true);
/* 2091 */       ActionStandin localActionStandin = new ActionStandin(paramActionListener, paramString);
/* 2092 */       localInputMap.put(paramKeyStroke, localActionStandin);
/* 2093 */       if (localActionMap != null)
/* 2094 */         localActionMap.put(localActionStandin, localActionStandin);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void registerWithKeyboardManager(boolean paramBoolean)
/*      */   {
/* 2110 */     InputMap localInputMap = getInputMap(2, false);
/*      */ 
/* 2112 */     Hashtable localHashtable = (Hashtable)getClientProperty("_WhenInFocusedWindow");
/*      */     KeyStroke[] arrayOfKeyStroke;
/* 2115 */     if (localInputMap != null)
/*      */     {
/* 2117 */       arrayOfKeyStroke = localInputMap.allKeys();
/* 2118 */       if (arrayOfKeyStroke != null) {
/* 2119 */         for (int i = arrayOfKeyStroke.length - 1; i >= 0; 
/* 2120 */           i--) {
/* 2121 */           if ((!paramBoolean) || (localHashtable == null) || (localHashtable.get(arrayOfKeyStroke[i]) == null))
/*      */           {
/* 2123 */             registerWithKeyboardManager(arrayOfKeyStroke[i]);
/*      */           }
/* 2125 */           if (localHashtable != null)
/* 2126 */             localHashtable.remove(arrayOfKeyStroke[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 2132 */       arrayOfKeyStroke = null;
/*      */     }
/*      */ 
/* 2135 */     if ((localHashtable != null) && (localHashtable.size() > 0)) {
/* 2136 */       Enumeration localEnumeration = localHashtable.keys();
/*      */ 
/* 2138 */       while (localEnumeration.hasMoreElements()) {
/* 2139 */         KeyStroke localKeyStroke = (KeyStroke)localEnumeration.nextElement();
/* 2140 */         unregisterWithKeyboardManager(localKeyStroke);
/*      */       }
/* 2142 */       localHashtable.clear();
/*      */     }
/*      */ 
/* 2145 */     if ((arrayOfKeyStroke != null) && (arrayOfKeyStroke.length > 0)) {
/* 2146 */       if (localHashtable == null) {
/* 2147 */         localHashtable = new Hashtable(arrayOfKeyStroke.length);
/* 2148 */         putClientProperty("_WhenInFocusedWindow", localHashtable);
/*      */       }
/* 2150 */       for (int j = arrayOfKeyStroke.length - 1; j >= 0; j--)
/* 2151 */         localHashtable.put(arrayOfKeyStroke[j], arrayOfKeyStroke[j]);
/*      */     }
/*      */     else
/*      */     {
/* 2155 */       putClientProperty("_WhenInFocusedWindow", null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void unregisterWithKeyboardManager()
/*      */   {
/* 2164 */     Hashtable localHashtable = (Hashtable)getClientProperty("_WhenInFocusedWindow");
/*      */ 
/* 2167 */     if ((localHashtable != null) && (localHashtable.size() > 0)) {
/* 2168 */       Enumeration localEnumeration = localHashtable.keys();
/*      */ 
/* 2170 */       while (localEnumeration.hasMoreElements()) {
/* 2171 */         KeyStroke localKeyStroke = (KeyStroke)localEnumeration.nextElement();
/* 2172 */         unregisterWithKeyboardManager(localKeyStroke);
/*      */       }
/*      */     }
/* 2175 */     putClientProperty("_WhenInFocusedWindow", null);
/*      */   }
/*      */ 
/*      */   void componentInputMapChanged(ComponentInputMap paramComponentInputMap)
/*      */   {
/* 2187 */     InputMap localInputMap = getInputMap(2, false);
/*      */ 
/* 2189 */     while ((localInputMap != paramComponentInputMap) && (localInputMap != null)) {
/* 2190 */       localInputMap = localInputMap.getParent();
/*      */     }
/* 2192 */     if (localInputMap != null)
/* 2193 */       registerWithKeyboardManager(false);
/*      */   }
/*      */ 
/*      */   private void registerWithKeyboardManager(KeyStroke paramKeyStroke)
/*      */   {
/* 2198 */     KeyboardManager.getCurrentManager().registerKeyStroke(paramKeyStroke, this);
/*      */   }
/*      */ 
/*      */   private void unregisterWithKeyboardManager(KeyStroke paramKeyStroke) {
/* 2202 */     KeyboardManager.getCurrentManager().unregisterKeyStroke(paramKeyStroke, this);
/*      */   }
/*      */ 
/*      */   public void registerKeyboardAction(ActionListener paramActionListener, KeyStroke paramKeyStroke, int paramInt)
/*      */   {
/* 2212 */     registerKeyboardAction(paramActionListener, null, paramKeyStroke, paramInt);
/*      */   }
/*      */ 
/*      */   public void unregisterKeyboardAction(KeyStroke paramKeyStroke)
/*      */   {
/* 2230 */     ActionMap localActionMap = getActionMap(false);
/* 2231 */     for (int i = 0; i < 3; i++) {
/* 2232 */       InputMap localInputMap = getInputMap(i, false);
/* 2233 */       if (localInputMap != null) {
/* 2234 */         Object localObject = localInputMap.get(paramKeyStroke);
/*      */ 
/* 2236 */         if ((localActionMap != null) && (localObject != null)) {
/* 2237 */           localActionMap.remove(localObject);
/*      */         }
/* 2239 */         localInputMap.remove(paramKeyStroke);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public KeyStroke[] getRegisteredKeyStrokes()
/*      */   {
/* 2252 */     int[] arrayOfInt = new int[3];
/* 2253 */     KeyStroke[][] arrayOfKeyStroke; = new KeyStroke[3][];
/*      */ 
/* 2255 */     for (int i = 0; i < 3; i++) {
/* 2256 */       InputMap localInputMap = getInputMap(i, false);
/* 2257 */       arrayOfKeyStroke;[i] = (localInputMap != null ? localInputMap.allKeys() : null);
/* 2258 */       arrayOfInt[i] = (arrayOfKeyStroke;[i] != null ? arrayOfKeyStroke;[i].length : 0);
/*      */     }
/*      */ 
/* 2261 */     KeyStroke[] arrayOfKeyStroke = new KeyStroke[arrayOfInt[0] + arrayOfInt[1] + arrayOfInt[2]];
/*      */ 
/* 2263 */     int j = 0; for (int k = 0; j < 3; j++) {
/* 2264 */       if (arrayOfInt[j] > 0) {
/* 2265 */         System.arraycopy(arrayOfKeyStroke;[j], 0, arrayOfKeyStroke, k, arrayOfInt[j]);
/*      */ 
/* 2267 */         k += arrayOfInt[j];
/*      */       }
/*      */     }
/* 2270 */     return arrayOfKeyStroke;
/*      */   }
/*      */ 
/*      */   public int getConditionForKeyStroke(KeyStroke paramKeyStroke)
/*      */   {
/* 2286 */     for (int i = 0; i < 3; i++) {
/* 2287 */       InputMap localInputMap = getInputMap(i, false);
/* 2288 */       if ((localInputMap != null) && (localInputMap.get(paramKeyStroke) != null)) {
/* 2289 */         return i;
/*      */       }
/*      */     }
/* 2292 */     return -1;
/*      */   }
/*      */ 
/*      */   public ActionListener getActionForKeyStroke(KeyStroke paramKeyStroke)
/*      */   {
/* 2303 */     ActionMap localActionMap = getActionMap(false);
/*      */ 
/* 2305 */     if (localActionMap == null) {
/* 2306 */       return null;
/*      */     }
/* 2308 */     for (int i = 0; i < 3; i++) {
/* 2309 */       InputMap localInputMap = getInputMap(i, false);
/* 2310 */       if (localInputMap != null) {
/* 2311 */         Object localObject = localInputMap.get(paramKeyStroke);
/*      */ 
/* 2313 */         if (localObject != null) {
/* 2314 */           Action localAction = localActionMap.get(localObject);
/* 2315 */           if ((localAction instanceof ActionStandin)) {
/* 2316 */             return ((ActionStandin)localAction).actionListener;
/*      */           }
/* 2318 */           return localAction;
/*      */         }
/*      */       }
/*      */     }
/* 2322 */     return null;
/*      */   }
/*      */ 
/*      */   public void resetKeyboardActions()
/*      */   {
/* 2334 */     for (int i = 0; i < 3; i++) {
/* 2335 */       InputMap localInputMap = getInputMap(i, false);
/*      */ 
/* 2337 */       if (localInputMap != null) {
/* 2338 */         localInputMap.clear();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2343 */     ActionMap localActionMap = getActionMap(false);
/*      */ 
/* 2345 */     if (localActionMap != null)
/* 2346 */       localActionMap.clear();
/*      */   }
/*      */ 
/*      */   public final void setInputMap(int paramInt, InputMap paramInputMap)
/*      */   {
/* 2378 */     switch (paramInt) {
/*      */     case 2:
/* 2380 */       if ((paramInputMap != null) && (!(paramInputMap instanceof ComponentInputMap))) {
/* 2381 */         throw new IllegalArgumentException("WHEN_IN_FOCUSED_WINDOW InputMaps must be of type ComponentInputMap");
/*      */       }
/* 2383 */       this.windowInputMap = ((ComponentInputMap)paramInputMap);
/* 2384 */       setFlag(7, true);
/* 2385 */       registerWithKeyboardManager(false);
/* 2386 */       break;
/*      */     case 1:
/* 2388 */       this.ancestorInputMap = paramInputMap;
/* 2389 */       setFlag(6, true);
/* 2390 */       break;
/*      */     case 0:
/* 2392 */       this.focusInputMap = paramInputMap;
/* 2393 */       setFlag(5, true);
/* 2394 */       break;
/*      */     default:
/* 2396 */       throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
/*      */     }
/*      */   }
/*      */ 
/*      */   public final InputMap getInputMap(int paramInt)
/*      */   {
/* 2411 */     return getInputMap(paramInt, true);
/*      */   }
/*      */ 
/*      */   public final InputMap getInputMap()
/*      */   {
/* 2423 */     return getInputMap(0, true);
/*      */   }
/*      */ 
/*      */   public final void setActionMap(ActionMap paramActionMap)
/*      */   {
/* 2435 */     this.actionMap = paramActionMap;
/* 2436 */     setFlag(8, true);
/*      */   }
/*      */ 
/*      */   public final ActionMap getActionMap()
/*      */   {
/* 2449 */     return getActionMap(true);
/*      */   }
/*      */ 
/*      */   final InputMap getInputMap(int paramInt, boolean paramBoolean)
/*      */   {
/*      */     Object localObject;
/* 2473 */     switch (paramInt) {
/*      */     case 0:
/* 2475 */       if (getFlag(5)) {
/* 2476 */         return this.focusInputMap;
/*      */       }
/*      */ 
/* 2479 */       if (paramBoolean) {
/* 2480 */         localObject = new InputMap();
/* 2481 */         setInputMap(paramInt, (InputMap)localObject);
/* 2482 */         return localObject;
/*      */       }
/*      */       break;
/*      */     case 1:
/* 2486 */       if (getFlag(6)) {
/* 2487 */         return this.ancestorInputMap;
/*      */       }
/*      */ 
/* 2490 */       if (paramBoolean) {
/* 2491 */         localObject = new InputMap();
/* 2492 */         setInputMap(paramInt, (InputMap)localObject);
/* 2493 */         return localObject;
/*      */       }
/*      */       break;
/*      */     case 2:
/* 2497 */       if (getFlag(7)) {
/* 2498 */         return this.windowInputMap;
/*      */       }
/*      */ 
/* 2501 */       if (paramBoolean) {
/* 2502 */         localObject = new ComponentInputMap(this);
/* 2503 */         setInputMap(paramInt, (InputMap)localObject);
/* 2504 */         return localObject;
/*      */       }
/*      */       break;
/*      */     default:
/* 2508 */       throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
/*      */     }
/* 2510 */     return null;
/*      */   }
/*      */ 
/*      */   final ActionMap getActionMap(boolean paramBoolean)
/*      */   {
/* 2523 */     if (getFlag(8)) {
/* 2524 */       return this.actionMap;
/*      */     }
/*      */ 
/* 2527 */     if (paramBoolean) {
/* 2528 */       ActionMap localActionMap = new ActionMap();
/* 2529 */       setActionMap(localActionMap);
/* 2530 */       return localActionMap;
/*      */     }
/* 2532 */     return null;
/*      */   }
/*      */ 
/*      */   public int getBaseline(int paramInt1, int paramInt2)
/*      */   {
/* 2558 */     super.getBaseline(paramInt1, paramInt2);
/* 2559 */     if (this.ui != null) {
/* 2560 */       return this.ui.getBaseline(this, paramInt1, paramInt2);
/*      */     }
/* 2562 */     return -1;
/*      */   }
/*      */ 
/*      */   public Component.BaselineResizeBehavior getBaselineResizeBehavior()
/*      */   {
/* 2587 */     if (this.ui != null) {
/* 2588 */       return this.ui.getBaselineResizeBehavior(this);
/*      */     }
/* 2590 */     return Component.BaselineResizeBehavior.OTHER;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean requestDefaultFocus()
/*      */   {
/* 2613 */     Container localContainer = isFocusCycleRoot() ? this : getFocusCycleRootAncestor();
/*      */ 
/* 2615 */     if (localContainer == null) {
/* 2616 */       return false;
/*      */     }
/* 2618 */     Component localComponent = localContainer.getFocusTraversalPolicy().getDefaultComponent(localContainer);
/*      */ 
/* 2620 */     if (localComponent != null) {
/* 2621 */       localComponent.requestFocus();
/* 2622 */       return true;
/*      */     }
/* 2624 */     return false;
/*      */   }
/*      */ 
/*      */   public void setVisible(boolean paramBoolean)
/*      */   {
/* 2639 */     if (paramBoolean != isVisible()) {
/* 2640 */       super.setVisible(paramBoolean);
/* 2641 */       Container localContainer = getParent();
/* 2642 */       if (localContainer != null) {
/* 2643 */         Rectangle localRectangle = getBounds();
/* 2644 */         localContainer.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */       }
/*      */ 
/* 2649 */       revalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setEnabled(boolean paramBoolean)
/*      */   {
/* 2676 */     boolean bool = isEnabled();
/* 2677 */     super.setEnabled(paramBoolean);
/* 2678 */     firePropertyChange("enabled", bool, paramBoolean);
/* 2679 */     if (paramBoolean != bool)
/* 2680 */       repaint();
/*      */   }
/*      */ 
/*      */   public void setForeground(Color paramColor)
/*      */   {
/* 2699 */     Color localColor = getForeground();
/* 2700 */     super.setForeground(paramColor);
/* 2701 */     if (localColor != null ? !localColor.equals(paramColor) : (paramColor != null) && (!paramColor.equals(localColor)))
/*      */     {
/* 2703 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor)
/*      */   {
/* 2729 */     Color localColor = getBackground();
/* 2730 */     super.setBackground(paramColor);
/* 2731 */     if (localColor != null ? !localColor.equals(paramColor) : (paramColor != null) && (!paramColor.equals(localColor)))
/*      */     {
/* 2733 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFont(Font paramFont)
/*      */   {
/* 2750 */     Font localFont = getFont();
/* 2751 */     super.setFont(paramFont);
/*      */ 
/* 2753 */     if (paramFont != localFont) {
/* 2754 */       revalidate();
/* 2755 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Locale getDefaultLocale()
/*      */   {
/* 2775 */     Locale localLocale = (Locale)SwingUtilities.appContextGet("JComponent.defaultLocale");
/* 2776 */     if (localLocale == null)
/*      */     {
/* 2779 */       localLocale = Locale.getDefault();
/* 2780 */       setDefaultLocale(localLocale);
/*      */     }
/* 2782 */     return localLocale;
/*      */   }
/*      */ 
/*      */   public static void setDefaultLocale(Locale paramLocale)
/*      */   {
/* 2802 */     SwingUtilities.appContextPut("JComponent.defaultLocale", paramLocale);
/*      */   }
/*      */ 
/*      */   protected void processComponentKeyEvent(KeyEvent paramKeyEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void processKeyEvent(KeyEvent paramKeyEvent)
/*      */   {
/* 2829 */     super.processKeyEvent(paramKeyEvent);
/*      */ 
/* 2832 */     if (!paramKeyEvent.isConsumed()) {
/* 2833 */       processComponentKeyEvent(paramKeyEvent);
/*      */     }
/*      */ 
/* 2836 */     boolean bool = KeyboardState.shouldProcess(paramKeyEvent);
/*      */ 
/* 2838 */     if (paramKeyEvent.isConsumed()) {
/* 2839 */       return;
/*      */     }
/*      */ 
/* 2842 */     if (bool) if (processKeyBindings(paramKeyEvent, paramKeyEvent.getID() == 401))
/*      */       {
/* 2844 */         paramKeyEvent.consume();
/*      */       }
/*      */   }
/*      */ 
/*      */   protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean)
/*      */   {
/* 2872 */     InputMap localInputMap = getInputMap(paramInt, false);
/* 2873 */     ActionMap localActionMap = getActionMap(false);
/*      */ 
/* 2875 */     if ((localInputMap != null) && (localActionMap != null) && (isEnabled())) {
/* 2876 */       Object localObject = localInputMap.get(paramKeyStroke);
/* 2877 */       Action localAction = localObject == null ? null : localActionMap.get(localObject);
/* 2878 */       if (localAction != null) {
/* 2879 */         return SwingUtilities.notifyAction(localAction, paramKeyStroke, paramKeyEvent, this, paramKeyEvent.getModifiers());
/*      */       }
/*      */     }
/*      */ 
/* 2883 */     return false;
/*      */   }
/*      */ 
/*      */   boolean processKeyBindings(KeyEvent paramKeyEvent, boolean paramBoolean)
/*      */   {
/* 2899 */     if (!SwingUtilities.isValidKeyEventForKeyBindings(paramKeyEvent)) {
/* 2900 */       return false;
/*      */     }
/*      */ 
/* 2906 */     KeyStroke localKeyStroke2 = null;
/*      */     KeyStroke localKeyStroke1;
/* 2908 */     if (paramKeyEvent.getID() == 400) {
/* 2909 */       localKeyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyChar());
/*      */     }
/*      */     else {
/* 2912 */       localKeyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
/*      */ 
/* 2914 */       if (paramKeyEvent.getKeyCode() != paramKeyEvent.getExtendedKeyCode()) {
/* 2915 */         localKeyStroke2 = KeyStroke.getKeyStroke(paramKeyEvent.getExtendedKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2923 */     if ((localKeyStroke2 != null) && (processKeyBinding(localKeyStroke2, paramKeyEvent, 0, paramBoolean))) {
/* 2924 */       return true;
/*      */     }
/* 2926 */     if (processKeyBinding(localKeyStroke1, paramKeyEvent, 0, paramBoolean)) {
/* 2927 */       return true;
/*      */     }
/*      */ 
/* 2933 */     Object localObject = this;
/* 2934 */     while ((localObject != null) && (!(localObject instanceof Window)) && (!(localObject instanceof Applet)))
/*      */     {
/* 2936 */       if ((localObject instanceof JComponent)) {
/* 2937 */         if ((localKeyStroke2 != null) && (((JComponent)localObject).processKeyBinding(localKeyStroke2, paramKeyEvent, 1, paramBoolean)))
/*      */         {
/* 2939 */           return true;
/* 2940 */         }if (((JComponent)localObject).processKeyBinding(localKeyStroke1, paramKeyEvent, 1, paramBoolean))
/*      */         {
/* 2942 */           return true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2951 */       if (((localObject instanceof JInternalFrame)) && (processKeyBindingsForAllComponents(paramKeyEvent, (Container)localObject, paramBoolean)))
/*      */       {
/* 2953 */         return true;
/*      */       }
/* 2955 */       localObject = ((Container)localObject).getParent();
/*      */     }
/*      */ 
/* 2962 */     if (localObject != null) {
/* 2963 */       return processKeyBindingsForAllComponents(paramKeyEvent, (Container)localObject, paramBoolean);
/*      */     }
/* 2965 */     return false;
/*      */   }
/*      */ 
/*      */   static boolean processKeyBindingsForAllComponents(KeyEvent paramKeyEvent, Container paramContainer, boolean paramBoolean)
/*      */   {
/*      */     while (true) {
/* 2971 */       if (KeyboardManager.getCurrentManager().fireKeyboardAction(paramKeyEvent, paramBoolean, paramContainer))
/*      */       {
/* 2973 */         return true;
/*      */       }
/* 2975 */       if (!(paramContainer instanceof Popup.HeavyWeightWindow)) break;
/* 2976 */       paramContainer = ((Window)paramContainer).getOwner();
/*      */     }
/*      */ 
/* 2979 */     return false;
/*      */   }
/*      */ 
/*      */   public void setToolTipText(String paramString)
/*      */   {
/* 3000 */     String str = getToolTipText();
/* 3001 */     putClientProperty("ToolTipText", paramString);
/* 3002 */     ToolTipManager localToolTipManager = ToolTipManager.sharedInstance();
/* 3003 */     if (paramString != null) {
/* 3004 */       if (str == null)
/* 3005 */         localToolTipManager.registerComponent(this);
/*      */     }
/*      */     else
/* 3008 */       localToolTipManager.unregisterComponent(this);
/*      */   }
/*      */ 
/*      */   public String getToolTipText()
/*      */   {
/* 3020 */     return (String)getClientProperty("ToolTipText");
/*      */   }
/*      */ 
/*      */   public String getToolTipText(MouseEvent paramMouseEvent)
/*      */   {
/* 3032 */     return getToolTipText();
/*      */   }
/*      */ 
/*      */   public Point getToolTipLocation(MouseEvent paramMouseEvent)
/*      */   {
/* 3045 */     return null;
/*      */   }
/*      */ 
/*      */   public Point getPopupLocation(MouseEvent paramMouseEvent)
/*      */   {
/* 3061 */     return null;
/*      */   }
/*      */ 
/*      */   public JToolTip createToolTip()
/*      */   {
/* 3075 */     JToolTip localJToolTip = new JToolTip();
/* 3076 */     localJToolTip.setComponent(this);
/* 3077 */     return localJToolTip;
/*      */   }
/*      */ 
/*      */   public void scrollRectToVisible(Rectangle paramRectangle)
/*      */   {
/* 3091 */     int i = getX(); int j = getY();
/*      */ 
/* 3093 */     for (Container localContainer = getParent(); 
/* 3095 */       (localContainer != null) && (!(localContainer instanceof JComponent)) && (!(localContainer instanceof CellRendererPane)); 
/* 3097 */       localContainer = localContainer.getParent()) {
/* 3098 */       Rectangle localRectangle = localContainer.getBounds();
/*      */ 
/* 3100 */       i += localRectangle.x;
/* 3101 */       j += localRectangle.y;
/*      */     }
/*      */ 
/* 3104 */     if ((localContainer != null) && (!(localContainer instanceof CellRendererPane))) {
/* 3105 */       paramRectangle.x += i;
/* 3106 */       paramRectangle.y += j;
/*      */ 
/* 3108 */       ((JComponent)localContainer).scrollRectToVisible(paramRectangle);
/* 3109 */       paramRectangle.x -= i;
/* 3110 */       paramRectangle.y -= j;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAutoscrolls(boolean paramBoolean)
/*      */   {
/* 3159 */     setFlag(25, true);
/* 3160 */     if (this.autoscrolls != paramBoolean) {
/* 3161 */       this.autoscrolls = paramBoolean;
/* 3162 */       if (paramBoolean) {
/* 3163 */         enableEvents(16L);
/* 3164 */         enableEvents(32L);
/*      */       }
/*      */       else {
/* 3167 */         Autoscroller.stop(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getAutoscrolls()
/*      */   {
/* 3180 */     return this.autoscrolls;
/*      */   }
/*      */ 
/*      */   public void setTransferHandler(TransferHandler paramTransferHandler)
/*      */   {
/* 3223 */     TransferHandler localTransferHandler = (TransferHandler)getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER);
/*      */ 
/* 3225 */     putClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER, paramTransferHandler);
/*      */ 
/* 3227 */     SwingUtilities.installSwingDropTargetAsNecessary(this, paramTransferHandler);
/* 3228 */     firePropertyChange("transferHandler", localTransferHandler, paramTransferHandler);
/*      */   }
/*      */ 
/*      */   public TransferHandler getTransferHandler()
/*      */   {
/* 3241 */     return (TransferHandler)getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER);
/*      */   }
/*      */ 
/*      */   TransferHandler.DropLocation dropLocationForPoint(Point paramPoint)
/*      */   {
/* 3256 */     return null;
/*      */   }
/*      */ 
/*      */   Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean)
/*      */   {
/* 3296 */     return null;
/*      */   }
/*      */ 
/*      */   void dndDone()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void processMouseEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 3318 */     if ((this.autoscrolls) && (paramMouseEvent.getID() == 502)) {
/* 3319 */       Autoscroller.stop(this);
/*      */     }
/* 3321 */     super.processMouseEvent(paramMouseEvent);
/*      */   }
/*      */ 
/*      */   protected void processMouseMotionEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 3331 */     int i = 1;
/* 3332 */     if ((this.autoscrolls) && (paramMouseEvent.getID() == 506))
/*      */     {
/* 3335 */       i = !Autoscroller.isRunning(this) ? 1 : 0;
/* 3336 */       Autoscroller.processMouseDragged(paramMouseEvent);
/*      */     }
/* 3338 */     if (i != 0)
/* 3339 */       super.processMouseMotionEvent(paramMouseEvent);
/*      */   }
/*      */ 
/*      */   void superProcessMouseMotionEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 3345 */     super.processMouseMotionEvent(paramMouseEvent);
/*      */   }
/*      */ 
/*      */   void setCreatedDoubleBuffer(boolean paramBoolean)
/*      */   {
/* 3355 */     setFlag(9, paramBoolean);
/*      */   }
/*      */ 
/*      */   boolean getCreatedDoubleBuffer()
/*      */   {
/* 3365 */     return getFlag(9);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void enable()
/*      */   {
/* 3618 */     if (isEnabled() != true) {
/* 3619 */       super.enable();
/* 3620 */       if (this.accessibleContext != null)
/* 3621 */         this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
/*      */     }
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void disable()
/*      */   {
/* 3634 */     if (isEnabled()) {
/* 3635 */       super.disable();
/* 3636 */       if (this.accessibleContext != null)
/* 3637 */         this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public AccessibleContext getAccessibleContext()
/*      */   {
/* 3661 */     return this.accessibleContext;
/*      */   }
/*      */ 
/*      */   private ArrayTable getClientProperties()
/*      */   {
/* 4010 */     if (this.clientProperties == null) {
/* 4011 */       this.clientProperties = new ArrayTable();
/*      */     }
/* 4013 */     return this.clientProperties;
/*      */   }
/*      */ 
/*      */   public final Object getClientProperty(Object paramObject)
/*      */   {
/* 4027 */     if (paramObject == SwingUtilities2.AA_TEXT_PROPERTY_KEY)
/* 4028 */       return this.aaTextInfo;
/* 4029 */     if (paramObject == SwingUtilities2.COMPONENT_UI_PROPERTY_KEY) {
/* 4030 */       return this.ui;
/*      */     }
/* 4032 */     if (this.clientProperties == null) {
/* 4033 */       return null;
/*      */     }
/* 4035 */     synchronized (this.clientProperties) {
/* 4036 */       return this.clientProperties.get(paramObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void putClientProperty(Object paramObject1, Object paramObject2)
/*      */   {
/* 4070 */     if (paramObject1 == SwingUtilities2.AA_TEXT_PROPERTY_KEY) {
/* 4071 */       this.aaTextInfo = paramObject2;
/* 4072 */       return;
/*      */     }
/* 4074 */     if ((paramObject2 == null) && (this.clientProperties == null))
/*      */     {
/* 4077 */       return;
/*      */     }
/* 4079 */     ArrayTable localArrayTable = getClientProperties();
/*      */     Object localObject1;
/* 4081 */     synchronized (localArrayTable) {
/* 4082 */       localObject1 = localArrayTable.get(paramObject1);
/* 4083 */       if (paramObject2 != null)
/* 4084 */         localArrayTable.put(paramObject1, paramObject2);
/* 4085 */       else if (localObject1 != null) {
/* 4086 */         localArrayTable.remove(paramObject1);
/*      */       }
/*      */       else {
/* 4089 */         return;
/*      */       }
/*      */     }
/* 4092 */     clientPropertyChanged(paramObject1, localObject1, paramObject2);
/* 4093 */     firePropertyChange(paramObject1.toString(), localObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   void clientPropertyChanged(Object paramObject1, Object paramObject2, Object paramObject3)
/*      */   {
/*      */   }
/*      */ 
/*      */   void setUIProperty(String paramString, Object paramObject)
/*      */   {
/* 4114 */     if (paramString == "opaque") {
/* 4115 */       if (!getFlag(24)) {
/* 4116 */         setOpaque(((Boolean)paramObject).booleanValue());
/* 4117 */         setFlag(24, false);
/*      */       }
/* 4119 */     } else if (paramString == "autoscrolls") {
/* 4120 */       if (!getFlag(25)) {
/* 4121 */         setAutoscrolls(((Boolean)paramObject).booleanValue());
/* 4122 */         setFlag(25, false);
/*      */       }
/* 4124 */     } else if (paramString == "focusTraversalKeysForward") {
/* 4125 */       if (!getFlag(26)) {
/* 4126 */         super.setFocusTraversalKeys(0, (Set)paramObject);
/*      */       }
/*      */ 
/*      */     }
/* 4130 */     else if (paramString == "focusTraversalKeysBackward") {
/* 4131 */       if (!getFlag(27)) {
/* 4132 */         super.setFocusTraversalKeys(1, (Set)paramObject);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 4137 */       throw new IllegalArgumentException("property \"" + paramString + "\" cannot be set using this method");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet)
/*      */   {
/* 4172 */     if (paramInt == 0)
/* 4173 */       setFlag(26, true);
/* 4174 */     else if (paramInt == 1) {
/* 4175 */       setFlag(27, true);
/*      */     }
/* 4177 */     super.setFocusTraversalKeys(paramInt, paramSet);
/*      */   }
/*      */ 
/*      */   public static boolean isLightweightComponent(Component paramComponent)
/*      */   {
/* 4192 */     return paramComponent.getPeer() instanceof LightweightPeer;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 4210 */     super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public Rectangle getBounds(Rectangle paramRectangle)
/*      */   {
/* 4228 */     if (paramRectangle == null) {
/* 4229 */       return new Rectangle(getX(), getY(), getWidth(), getHeight());
/*      */     }
/*      */ 
/* 4232 */     paramRectangle.setBounds(getX(), getY(), getWidth(), getHeight());
/* 4233 */     return paramRectangle;
/*      */   }
/*      */ 
/*      */   public Dimension getSize(Dimension paramDimension)
/*      */   {
/* 4250 */     if (paramDimension == null) {
/* 4251 */       return new Dimension(getWidth(), getHeight());
/*      */     }
/*      */ 
/* 4254 */     paramDimension.setSize(getWidth(), getHeight());
/* 4255 */     return paramDimension;
/*      */   }
/*      */ 
/*      */   public Point getLocation(Point paramPoint)
/*      */   {
/* 4272 */     if (paramPoint == null) {
/* 4273 */       return new Point(getX(), getY());
/*      */     }
/*      */ 
/* 4276 */     paramPoint.setLocation(getX(), getY());
/* 4277 */     return paramPoint;
/*      */   }
/*      */ 
/*      */   public int getX()
/*      */   {
/* 4291 */     return super.getX();
/*      */   }
/*      */ 
/*      */   public int getY()
/*      */   {
/* 4303 */     return super.getY();
/*      */   }
/*      */ 
/*      */   public int getWidth()
/*      */   {
/* 4315 */     return super.getWidth();
/*      */   }
/*      */ 
/*      */   public int getHeight()
/*      */   {
/* 4327 */     return super.getHeight();
/*      */   }
/*      */ 
/*      */   public boolean isOpaque()
/*      */   {
/* 4345 */     return getFlag(3);
/*      */   }
/*      */ 
/*      */   public void setOpaque(boolean paramBoolean)
/*      */   {
/* 4366 */     boolean bool = getFlag(3);
/* 4367 */     setFlag(3, paramBoolean);
/* 4368 */     setFlag(24, true);
/* 4369 */     firePropertyChange("opaque", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   boolean rectangleIsObscured(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 4390 */     int i = getComponentCount();
/*      */ 
/* 4392 */     for (int j = 0; j < i; j++) {
/* 4393 */       Component localComponent = getComponent(j);
/*      */ 
/* 4396 */       int k = localComponent.getX();
/* 4397 */       int m = localComponent.getY();
/* 4398 */       int n = localComponent.getWidth();
/* 4399 */       int i1 = localComponent.getHeight();
/*      */ 
/* 4401 */       if ((paramInt1 >= k) && (paramInt1 + paramInt3 <= k + n) && (paramInt2 >= m) && (paramInt2 + paramInt4 <= m + i1) && (localComponent.isVisible()))
/*      */       {
/* 4404 */         if ((localComponent instanceof JComponent))
/*      */         {
/* 4408 */           return localComponent.isOpaque();
/*      */         }
/*      */ 
/* 4413 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4418 */     return false;
/*      */   }
/*      */ 
/*      */   static final void computeVisibleRect(Component paramComponent, Rectangle paramRectangle)
/*      */   {
/* 4436 */     Container localContainer = paramComponent.getParent();
/* 4437 */     Rectangle localRectangle = paramComponent.getBounds();
/*      */ 
/* 4439 */     if ((localContainer == null) || ((localContainer instanceof Window)) || ((localContainer instanceof Applet))) {
/* 4440 */       paramRectangle.setBounds(0, 0, localRectangle.width, localRectangle.height);
/*      */     } else {
/* 4442 */       computeVisibleRect(localContainer, paramRectangle);
/* 4443 */       paramRectangle.x -= localRectangle.x;
/* 4444 */       paramRectangle.y -= localRectangle.y;
/* 4445 */       SwingUtilities.computeIntersection(0, 0, localRectangle.width, localRectangle.height, paramRectangle);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void computeVisibleRect(Rectangle paramRectangle)
/*      */   {
/* 4463 */     computeVisibleRect(this, paramRectangle);
/*      */   }
/*      */ 
/*      */   public Rectangle getVisibleRect()
/*      */   {
/* 4476 */     Rectangle localRectangle = new Rectangle();
/*      */ 
/* 4478 */     computeVisibleRect(localRectangle);
/* 4479 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 4494 */     super.firePropertyChange(paramString, paramBoolean1, paramBoolean2);
/*      */   }
/*      */ 
/*      */   public void firePropertyChange(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 4510 */     super.firePropertyChange(paramString, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public void firePropertyChange(String paramString, char paramChar1, char paramChar2)
/*      */   {
/* 4516 */     super.firePropertyChange(paramString, paramChar1, paramChar2);
/*      */   }
/*      */ 
/*      */   protected void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2)
/*      */     throws PropertyVetoException
/*      */   {
/* 4534 */     if (this.vetoableChangeSupport == null) {
/* 4535 */       return;
/*      */     }
/* 4537 */     this.vetoableChangeSupport.fireVetoableChange(paramString, paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   public synchronized void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
/*      */   {
/* 4548 */     if (this.vetoableChangeSupport == null) {
/* 4549 */       this.vetoableChangeSupport = new VetoableChangeSupport(this);
/*      */     }
/* 4551 */     this.vetoableChangeSupport.addVetoableChangeListener(paramVetoableChangeListener);
/*      */   }
/*      */ 
/*      */   public synchronized void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
/*      */   {
/* 4563 */     if (this.vetoableChangeSupport == null) {
/* 4564 */       return;
/*      */     }
/* 4566 */     this.vetoableChangeSupport.removeVetoableChangeListener(paramVetoableChangeListener);
/*      */   }
/*      */ 
/*      */   public synchronized VetoableChangeListener[] getVetoableChangeListeners()
/*      */   {
/* 4584 */     if (this.vetoableChangeSupport == null) {
/* 4585 */       return new VetoableChangeListener[0];
/*      */     }
/* 4587 */     return this.vetoableChangeSupport.getVetoableChangeListeners();
/*      */   }
/*      */ 
/*      */   public Container getTopLevelAncestor()
/*      */   {
/* 4601 */     for (Object localObject = this; localObject != null; localObject = ((Container)localObject).getParent()) {
/* 4602 */       if (((localObject instanceof Window)) || ((localObject instanceof Applet))) {
/* 4603 */         return localObject;
/*      */       }
/*      */     }
/* 4606 */     return null;
/*      */   }
/*      */ 
/*      */   private AncestorNotifier getAncestorNotifier() {
/* 4610 */     return (AncestorNotifier)getClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER);
/*      */   }
/*      */ 
/*      */   public void addAncestorListener(AncestorListener paramAncestorListener)
/*      */   {
/* 4625 */     AncestorNotifier localAncestorNotifier = getAncestorNotifier();
/* 4626 */     if (localAncestorNotifier == null) {
/* 4627 */       localAncestorNotifier = new AncestorNotifier(this);
/* 4628 */       putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, localAncestorNotifier);
/*      */     }
/*      */ 
/* 4631 */     localAncestorNotifier.addAncestorListener(paramAncestorListener);
/*      */   }
/*      */ 
/*      */   public void removeAncestorListener(AncestorListener paramAncestorListener)
/*      */   {
/* 4642 */     AncestorNotifier localAncestorNotifier = getAncestorNotifier();
/* 4643 */     if (localAncestorNotifier == null) {
/* 4644 */       return;
/*      */     }
/* 4646 */     localAncestorNotifier.removeAncestorListener(paramAncestorListener);
/* 4647 */     if (localAncestorNotifier.listenerList.getListenerList().length == 0) {
/* 4648 */       localAncestorNotifier.removeAllListeners();
/* 4649 */       putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public AncestorListener[] getAncestorListeners()
/*      */   {
/* 4667 */     AncestorNotifier localAncestorNotifier = getAncestorNotifier();
/* 4668 */     if (localAncestorNotifier == null) {
/* 4669 */       return new AncestorListener[0];
/*      */     }
/* 4671 */     return localAncestorNotifier.getAncestorListeners();
/*      */   }
/*      */ 
/*      */   public <T extends EventListener> T[] getListeners(Class<T> paramClass)
/*      */   {
/*      */     EventListener[] arrayOfEventListener;
/* 4711 */     if (paramClass == AncestorListener.class)
/*      */     {
/* 4713 */       arrayOfEventListener = (EventListener[])getAncestorListeners();
/*      */     }
/* 4715 */     else if (paramClass == VetoableChangeListener.class)
/*      */     {
/* 4717 */       arrayOfEventListener = (EventListener[])getVetoableChangeListeners();
/*      */     }
/* 4719 */     else if (paramClass == PropertyChangeListener.class)
/*      */     {
/* 4721 */       arrayOfEventListener = (EventListener[])getPropertyChangeListeners();
/*      */     }
/*      */     else {
/* 4724 */       arrayOfEventListener = this.listenerList.getListeners(paramClass);
/*      */     }
/*      */ 
/* 4727 */     if (arrayOfEventListener.length == 0) {
/* 4728 */       return super.getListeners(paramClass);
/*      */     }
/* 4730 */     return arrayOfEventListener;
/*      */   }
/*      */ 
/*      */   public void addNotify()
/*      */   {
/* 4743 */     super.addNotify();
/* 4744 */     firePropertyChange("ancestor", null, getParent());
/*      */ 
/* 4746 */     registerWithKeyboardManager(false);
/* 4747 */     registerNextFocusableComponent();
/*      */   }
/*      */ 
/*      */   public void removeNotify()
/*      */   {
/* 4761 */     super.removeNotify();
/*      */ 
/* 4765 */     firePropertyChange("ancestor", getParent(), null);
/*      */ 
/* 4767 */     unregisterWithKeyboardManager();
/* 4768 */     deregisterNextFocusableComponent();
/*      */ 
/* 4770 */     if (getCreatedDoubleBuffer()) {
/* 4771 */       RepaintManager.currentManager(this).resetDoubleBuffer();
/* 4772 */       setCreatedDoubleBuffer(false);
/*      */     }
/* 4774 */     if (this.autoscrolls)
/* 4775 */       Autoscroller.stop(this);
/*      */   }
/*      */ 
/*      */   public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 4795 */     RepaintManager.currentManager(this).addDirtyRegion(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void repaint(Rectangle paramRectangle)
/*      */   {
/* 4810 */     repaint(0L, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public void revalidate()
/*      */   {
/* 4841 */     if (getParent() == null)
/*      */     {
/* 4848 */       return;
/*      */     }
/* 4850 */     if (SwingUtilities.isEventDispatchThread()) {
/* 4851 */       invalidate();
/* 4852 */       RepaintManager.currentManager(this).addInvalidComponent(this);
/*      */     }
/*      */     else
/*      */     {
/* 4858 */       synchronized (this) {
/* 4859 */         if (getFlag(28)) {
/* 4860 */           return;
/*      */         }
/* 4862 */         setFlag(28, true);
/*      */       }
/* 4864 */       ??? = new Runnable() {
/*      */         public void run() {
/* 4866 */           synchronized (JComponent.this) {
/* 4867 */             JComponent.this.setFlag(28, false);
/*      */           }
/* 4869 */           JComponent.this.revalidate();
/*      */         }
/*      */       };
/* 4872 */       SwingUtilities.invokeLater((Runnable)???);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isValidateRoot()
/*      */   {
/* 4891 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isOptimizedDrawingEnabled()
/*      */   {
/* 4906 */     return true;
/*      */   }
/*      */ 
/*      */   protected boolean isPaintingOrigin()
/*      */   {
/* 4926 */     return false;
/*      */   }
/*      */ 
/*      */   public void paintImmediately(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 4950 */     Object localObject = this;
/*      */ 
/* 4953 */     if (!isShowing()) {
/* 4954 */       return;
/*      */     }
/*      */ 
/* 4957 */     JComponent localJComponent = SwingUtilities.getPaintingOrigin(this);
/* 4958 */     if (localJComponent != null) {
/* 4959 */       Rectangle localRectangle = SwingUtilities.convertRectangle((Component)localObject, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4), localJComponent);
/*      */ 
/* 4961 */       localJComponent.paintImmediately(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/* 4962 */       return;
/*      */     }
/*      */ 
/* 4965 */     while (!((Component)localObject).isOpaque()) {
/* 4966 */       Container localContainer = ((Component)localObject).getParent();
/* 4967 */       if (localContainer != null) {
/* 4968 */         paramInt1 += ((Component)localObject).getX();
/* 4969 */         paramInt2 += ((Component)localObject).getY();
/* 4970 */         localObject = localContainer;
/*      */ 
/* 4975 */         if (!(localObject instanceof JComponent))
/* 4976 */           break;
/*      */       }
/*      */     }
/* 4979 */     if ((localObject instanceof JComponent))
/* 4980 */       ((JComponent)localObject)._paintImmediately(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     else
/* 4982 */       ((Component)localObject).repaint(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void paintImmediately(Rectangle paramRectangle)
/*      */   {
/* 4992 */     paintImmediately(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   boolean alwaysOnTop()
/*      */   {
/* 5005 */     return false;
/*      */   }
/*      */ 
/*      */   void setPaintingChild(Component paramComponent) {
/* 5009 */     this.paintingChild = paramComponent;
/*      */   }
/*      */ 
/*      */   void _paintImmediately(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 5018 */     int n = 0; int i1 = 0;
/*      */ 
/* 5020 */     int i2 = 0;
/*      */ 
/* 5022 */     Object localObject2 = null;
/* 5023 */     Object localObject3 = this;
/*      */ 
/* 5025 */     RepaintManager localRepaintManager = RepaintManager.currentManager(this);
/*      */ 
/* 5030 */     ArrayList localArrayList = new ArrayList(7);
/* 5031 */     int i3 = -1;
/* 5032 */     int i4 = 0;
/*      */     int m;
/*      */     int k;
/*      */     int j;
/* 5034 */     int i = j = k = m = 0;
/*      */ 
/* 5036 */     Rectangle localRectangle = fetchRectangle();
/* 5037 */     localRectangle.x = paramInt1;
/* 5038 */     localRectangle.y = paramInt2;
/* 5039 */     localRectangle.width = paramInt3;
/* 5040 */     localRectangle.height = paramInt4;
/*      */ 
/* 5045 */     int i5 = (alwaysOnTop()) && (isOpaque()) ? 1 : 0;
/* 5046 */     if (i5 != 0) {
/* 5047 */       SwingUtilities.computeIntersection(0, 0, getWidth(), getHeight(), localRectangle);
/*      */ 
/* 5049 */       if (localRectangle.width == 0) {
/* 5050 */         recycleRectangle(localRectangle);
/* 5051 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 5055 */     Object localObject1 = this; Object localObject4 = null;
/*      */     Object localObject5;
/*      */     int i6;
/* 5057 */     for (; (localObject1 != null) && (!(localObject1 instanceof Window)) && (!(localObject1 instanceof Applet)); 
/* 5057 */       localObject1 = ((Container)localObject1).getParent()) {
/* 5058 */       localObject5 = (localObject1 instanceof JComponent) ? (JComponent)localObject1 : null;
/*      */ 
/* 5060 */       localArrayList.add(localObject1);
/* 5061 */       if ((i5 == 0) && (localObject5 != null) && (!((JComponent)localObject5).isOptimizedDrawingEnabled()))
/*      */       {
/* 5072 */         if (localObject1 != this) {
/* 5073 */           if (((JComponent)localObject5).isPaintingOrigin()) {
/* 5074 */             i6 = 1;
/*      */           }
/*      */           else {
/* 5077 */             Component[] arrayOfComponent = ((Container)localObject1).getComponents();
/* 5078 */             int i8 = 0;
/* 5079 */             while ((i8 < arrayOfComponent.length) && 
/* 5080 */               (arrayOfComponent[i8] != localObject4)) {
/* 5079 */               i8++;
/*      */             }
/*      */ 
/* 5082 */             switch (((JComponent)localObject5).getObscuredState(i8, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height))
/*      */             {
/*      */             case 0:
/* 5088 */               i6 = 0;
/* 5089 */               break;
/*      */             case 2:
/* 5091 */               recycleRectangle(localRectangle);
/* 5092 */               return;
/*      */             default:
/* 5094 */               i6 = 1;
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 5100 */           i6 = 0;
/*      */         }
/*      */ 
/* 5103 */         if (i6 != 0)
/*      */         {
/* 5106 */           localObject3 = localObject5;
/* 5107 */           i3 = i4;
/* 5108 */           n = i1 = 0;
/* 5109 */           i2 = 0;
/*      */         }
/*      */       }
/* 5112 */       i4++;
/*      */ 
/* 5116 */       if ((localRepaintManager.isDoubleBufferingEnabled()) && (localObject5 != null) && (((JComponent)localObject5).isDoubleBuffered()))
/*      */       {
/* 5118 */         i2 = 1;
/* 5119 */         localObject2 = localObject5;
/*      */       }
/*      */ 
/* 5123 */       if (i5 == 0) {
/* 5124 */         i6 = ((Container)localObject1).getX();
/* 5125 */         int i7 = ((Container)localObject1).getY();
/* 5126 */         k = ((Container)localObject1).getWidth();
/* 5127 */         m = ((Container)localObject1).getHeight();
/* 5128 */         SwingUtilities.computeIntersection(i, j, k, m, localRectangle);
/* 5129 */         localRectangle.x += i6;
/* 5130 */         localRectangle.y += i7;
/* 5131 */         n += i6;
/* 5132 */         i1 += i7;
/*      */       }
/* 5057 */       localObject4 = localObject1;
/*      */     }
/*      */ 
/* 5137 */     if ((localObject1 == null) || (((Container)localObject1).getPeer() == null) || (localRectangle.width <= 0) || (localRectangle.height <= 0))
/*      */     {
/* 5140 */       recycleRectangle(localRectangle);
/* 5141 */       return;
/*      */     }
/*      */ 
/* 5144 */     ((JComponent)localObject3).setFlag(13, true);
/*      */ 
/* 5146 */     localRectangle.x -= n;
/* 5147 */     localRectangle.y -= i1;
/*      */ 
/* 5151 */     if (localObject3 != this)
/*      */     {
/* 5153 */       for (i6 = i3; 
/* 5154 */         i6 > 0; i6--) {
/* 5155 */         localObject5 = (Component)localArrayList.get(i6);
/* 5156 */         if ((localObject5 instanceof JComponent))
/* 5157 */           ((JComponent)localObject5).setPaintingChild((Component)localArrayList.get(i6 - 1));
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*      */       Graphics localGraphics;
/* 5162 */       if ((localGraphics = safelyGetGraphics((Component)localObject3, (Component)localObject1)) != null) {
/*      */         try {
/* 5164 */           if (i2 != 0) {
/* 5165 */             localObject5 = RepaintManager.currentManager(localObject2);
/*      */ 
/* 5167 */             ((RepaintManager)localObject5).beginPaint();
/*      */             try {
/* 5169 */               ((RepaintManager)localObject5).paint((JComponent)localObject3, localObject2, localGraphics, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */             }
/*      */             finally
/*      */             {
/* 5175 */               ((RepaintManager)localObject5).endPaint();
/*      */             }
/*      */           } else {
/* 5178 */             localGraphics.setClip(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */ 
/* 5180 */             ((JComponent)localObject3).paint(localGraphics);
/*      */           }
/*      */         } finally {
/* 5183 */           localGraphics.dispose();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 5189 */       if (localObject3 != this)
/*      */       {
/* 5191 */         for (int i9 = i3; 
/* 5192 */           i9 > 0; i9--) {
/* 5193 */           Component localComponent = (Component)localArrayList.get(i9);
/* 5194 */           if ((localComponent instanceof JComponent)) {
/* 5195 */             ((JComponent)localComponent).setPaintingChild(null);
/*      */           }
/*      */         }
/*      */       }
/* 5199 */       ((JComponent)localObject3).setFlag(13, false);
/*      */     }
/* 5201 */     recycleRectangle(localRectangle);
/*      */   }
/*      */ 
/*      */   void paintToOffscreen(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*      */     try
/*      */     {
/* 5214 */       setFlag(1, true);
/* 5215 */       if ((paramInt2 + paramInt4 < paramInt6) || (paramInt1 + paramInt3 < paramInt5)) {
/* 5216 */         setFlag(2, true);
/*      */       }
/* 5218 */       if (getFlag(13))
/*      */       {
/* 5221 */         paint(paramGraphics);
/*      */       }
/*      */       else {
/* 5224 */         if (!rectangleIsObscured(paramInt1, paramInt2, paramInt3, paramInt4)) {
/* 5225 */           paintComponent(paramGraphics);
/* 5226 */           paintBorder(paramGraphics);
/*      */         }
/* 5228 */         paintChildren(paramGraphics);
/*      */       }
/*      */     } finally {
/* 5231 */       setFlag(1, false);
/* 5232 */       setFlag(2, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int getObscuredState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/* 5247 */     int i = 0;
/* 5248 */     Rectangle localRectangle1 = fetchRectangle();
/*      */ 
/* 5250 */     for (int j = paramInt1 - 1; j >= 0; j--) {
/* 5251 */       Component localComponent = getComponent(j);
/* 5252 */       if (localComponent.isVisible())
/*      */       {
/*      */         boolean bool;
/* 5257 */         if ((localComponent instanceof JComponent)) {
/* 5258 */           bool = localComponent.isOpaque();
/* 5259 */           if ((!bool) && 
/* 5260 */             (i == 1)) {
/* 5261 */             continue;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 5266 */           bool = true;
/*      */         }
/* 5268 */         Rectangle localRectangle2 = localComponent.getBounds(localRectangle1);
/* 5269 */         if ((bool) && (paramInt2 >= localRectangle2.x) && (paramInt2 + paramInt4 <= localRectangle2.x + localRectangle2.width) && (paramInt3 >= localRectangle2.y) && (paramInt3 + paramInt5 <= localRectangle2.y + localRectangle2.height))
/*      */         {
/* 5273 */           recycleRectangle(localRectangle1);
/* 5274 */           return 2;
/*      */         }
/* 5276 */         if ((i == 0) && (paramInt2 + paramInt4 > localRectangle2.x) && (paramInt3 + paramInt5 > localRectangle2.y) && (paramInt2 < localRectangle2.x + localRectangle2.width) && (paramInt3 < localRectangle2.y + localRectangle2.height))
/*      */         {
/* 5281 */           i = 1;
/*      */         }
/*      */       }
/*      */     }
/* 5284 */     recycleRectangle(localRectangle1);
/* 5285 */     return i;
/*      */   }
/*      */ 
/*      */   boolean checkIfChildObscuredBySibling()
/*      */   {
/* 5297 */     return true;
/*      */   }
/*      */ 
/*      */   private void setFlag(int paramInt, boolean paramBoolean)
/*      */   {
/* 5302 */     if (paramBoolean)
/* 5303 */       this.flags |= 1 << paramInt;
/*      */     else
/* 5305 */       this.flags &= (1 << paramInt ^ 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */   private boolean getFlag(int paramInt) {
/* 5309 */     int i = 1 << paramInt;
/* 5310 */     return (this.flags & i) == i;
/*      */   }
/*      */ 
/*      */   static void setWriteObjCounter(JComponent paramJComponent, byte paramByte)
/*      */   {
/* 5316 */     paramJComponent.flags = (paramJComponent.flags & 0xFFC03FFF | paramByte << 14);
/*      */   }
/*      */ 
/*      */   static byte getWriteObjCounter(JComponent paramJComponent) {
/* 5320 */     return (byte)(paramJComponent.flags >> 14 & 0xFF);
/*      */   }
/*      */ 
/*      */   public void setDoubleBuffered(boolean paramBoolean)
/*      */   {
/* 5336 */     setFlag(0, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean isDoubleBuffered()
/*      */   {
/* 5345 */     return getFlag(0);
/*      */   }
/*      */ 
/*      */   public JRootPane getRootPane()
/*      */   {
/* 5355 */     return SwingUtilities.getRootPane(this);
/*      */   }
/*      */ 
/*      */   void compWriteObjectNotify()
/*      */   {
/* 5366 */     int i = getWriteObjCounter(this);
/* 5367 */     setWriteObjCounter(this, (byte)(i + 1));
/* 5368 */     if (i != 0) {
/* 5369 */       return;
/*      */     }
/*      */ 
/* 5372 */     uninstallUIAndProperties();
/*      */ 
/* 5381 */     if ((getToolTipText() != null) || ((this instanceof JTableHeader)))
/*      */     {
/* 5383 */       ToolTipManager.sharedInstance().unregisterComponent(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 5478 */     paramObjectInputStream.defaultReadObject();
/*      */ 
/* 5486 */     ReadObjectCallback localReadObjectCallback = (ReadObjectCallback)readObjectCallbacks.get(paramObjectInputStream);
/* 5487 */     if (localReadObjectCallback == null) {
/*      */       try {
/* 5489 */         readObjectCallbacks.put(paramObjectInputStream, localReadObjectCallback = new ReadObjectCallback(paramObjectInputStream));
/*      */       }
/*      */       catch (Exception localException) {
/* 5492 */         throw new IOException(localException.toString());
/*      */       }
/*      */     }
/* 5495 */     localReadObjectCallback.registerComponent(this);
/*      */ 
/* 5498 */     int i = paramObjectInputStream.readInt();
/* 5499 */     if (i > 0) {
/* 5500 */       this.clientProperties = new ArrayTable();
/* 5501 */       for (int j = 0; j < i; j++) {
/* 5502 */         this.clientProperties.put(paramObjectInputStream.readObject(), paramObjectInputStream.readObject());
/*      */       }
/*      */     }
/*      */ 
/* 5506 */     if (getToolTipText() != null) {
/* 5507 */       ToolTipManager.sharedInstance().registerComponent(this);
/*      */     }
/* 5509 */     setWriteObjCounter(this, (byte)0);
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 5525 */     paramObjectOutputStream.defaultWriteObject();
/* 5526 */     if (getUIClassID().equals("ComponentUI")) {
/* 5527 */       byte b = getWriteObjCounter(this);
/* 5528 */       b = (byte)(b - 1); setWriteObjCounter(this, b);
/* 5529 */       if ((b == 0) && (this.ui != null)) {
/* 5530 */         this.ui.installUI(this);
/*      */       }
/*      */     }
/* 5533 */     ArrayTable.writeArrayTable(paramObjectOutputStream, this.clientProperties);
/*      */   }
/*      */ 
/*      */   protected String paramString()
/*      */   {
/* 5548 */     String str1 = isPreferredSizeSet() ? getPreferredSize().toString() : "";
/*      */ 
/* 5550 */     String str2 = isMinimumSizeSet() ? getMinimumSize().toString() : "";
/*      */ 
/* 5552 */     String str3 = isMaximumSizeSet() ? getMaximumSize().toString() : "";
/*      */ 
/* 5554 */     String str4 = this.border == this ? "this" : this.border == null ? "" : this.border.toString();
/*      */ 
/* 5557 */     return super.paramString() + ",alignmentX=" + this.alignmentX + ",alignmentY=" + this.alignmentY + ",border=" + str4 + ",flags=" + this.flags + ",maximumSize=" + str3 + ",minimumSize=" + str2 + ",preferredSize=" + str1;
/*      */   }
/*      */ 
/*      */   public abstract class AccessibleJComponent extends Container.AccessibleAWTContainer
/*      */     implements AccessibleExtendedComponent
/*      */   {
/* 3690 */     protected ContainerListener accessibleContainerHandler = null;
/* 3691 */     protected FocusListener accessibleFocusHandler = null;
/*      */ 
/*      */     protected AccessibleJComponent()
/*      */     {
/* 3687 */       super();
/*      */     }
/*      */ 
/*      */     public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/* 3746 */       if (this.accessibleFocusHandler == null) {
/* 3747 */         this.accessibleFocusHandler = new AccessibleFocusHandler();
/* 3748 */         JComponent.this.addFocusListener(this.accessibleFocusHandler);
/*      */       }
/* 3750 */       if (this.accessibleContainerHandler == null) {
/* 3751 */         this.accessibleContainerHandler = new AccessibleContainerHandler();
/* 3752 */         JComponent.this.addContainerListener(this.accessibleContainerHandler);
/*      */       }
/* 3754 */       super.addPropertyChangeListener(paramPropertyChangeListener);
/*      */     }
/*      */ 
/*      */     public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/* 3765 */       if (this.accessibleFocusHandler != null) {
/* 3766 */         JComponent.this.removeFocusListener(this.accessibleFocusHandler);
/* 3767 */         this.accessibleFocusHandler = null;
/*      */       }
/* 3769 */       super.removePropertyChangeListener(paramPropertyChangeListener);
/*      */     }
/*      */ 
/*      */     protected String getBorderTitle(Border paramBorder)
/*      */     {
/* 3785 */       if ((paramBorder instanceof TitledBorder))
/* 3786 */         return ((TitledBorder)paramBorder).getTitle();
/* 3787 */       if ((paramBorder instanceof CompoundBorder)) {
/* 3788 */         String str = getBorderTitle(((CompoundBorder)paramBorder).getInsideBorder());
/* 3789 */         if (str == null) {
/* 3790 */           str = getBorderTitle(((CompoundBorder)paramBorder).getOutsideBorder());
/*      */         }
/* 3792 */         return str;
/*      */       }
/* 3794 */       return null;
/*      */     }
/*      */ 
/*      */     public String getAccessibleName()
/*      */     {
/* 3815 */       String str = this.accessibleName;
/*      */ 
/* 3819 */       if (str == null) {
/* 3820 */         str = (String)JComponent.this.getClientProperty("AccessibleName");
/*      */       }
/*      */ 
/* 3825 */       if (str == null) {
/* 3826 */         str = getBorderTitle(JComponent.this.getBorder());
/*      */       }
/*      */ 
/* 3831 */       if (str == null) {
/* 3832 */         Object localObject = JComponent.this.getClientProperty("labeledBy");
/* 3833 */         if ((localObject instanceof Accessible)) {
/* 3834 */           AccessibleContext localAccessibleContext = ((Accessible)localObject).getAccessibleContext();
/* 3835 */           if (localAccessibleContext != null) {
/* 3836 */             str = localAccessibleContext.getAccessibleName();
/*      */           }
/*      */         }
/*      */       }
/* 3840 */       return str;
/*      */     }
/*      */ 
/*      */     public String getAccessibleDescription()
/*      */     {
/* 3859 */       String str = this.accessibleDescription;
/*      */ 
/* 3863 */       if (str == null) {
/* 3864 */         str = (String)JComponent.this.getClientProperty("AccessibleDescription");
/*      */       }
/*      */ 
/* 3869 */       if (str == null) {
/*      */         try {
/* 3871 */           str = getToolTipText();
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3887 */       if (str == null) {
/* 3888 */         Object localObject = JComponent.this.getClientProperty("labeledBy");
/* 3889 */         if ((localObject instanceof Accessible)) {
/* 3890 */           AccessibleContext localAccessibleContext = ((Accessible)localObject).getAccessibleContext();
/* 3891 */           if (localAccessibleContext != null) {
/* 3892 */             str = localAccessibleContext.getAccessibleDescription();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3897 */       return str;
/*      */     }
/*      */ 
/*      */     public AccessibleRole getAccessibleRole()
/*      */     {
/* 3908 */       return AccessibleRole.SWING_COMPONENT;
/*      */     }
/*      */ 
/*      */     public AccessibleStateSet getAccessibleStateSet()
/*      */     {
/* 3919 */       AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
/* 3920 */       if (JComponent.this.isOpaque()) {
/* 3921 */         localAccessibleStateSet.add(AccessibleState.OPAQUE);
/*      */       }
/* 3923 */       return localAccessibleStateSet;
/*      */     }
/*      */ 
/*      */     public int getAccessibleChildrenCount()
/*      */     {
/* 3934 */       return super.getAccessibleChildrenCount();
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleChild(int paramInt)
/*      */     {
/* 3944 */       return super.getAccessibleChild(paramInt);
/*      */     }
/*      */ 
/*      */     AccessibleExtendedComponent getAccessibleExtendedComponent()
/*      */     {
/* 3955 */       return this;
/*      */     }
/*      */ 
/*      */     public String getToolTipText()
/*      */     {
/* 3966 */       return JComponent.this.getToolTipText();
/*      */     }
/*      */ 
/*      */     public String getTitledBorderText()
/*      */     {
/* 3977 */       Border localBorder = JComponent.this.getBorder();
/* 3978 */       if ((localBorder instanceof TitledBorder)) {
/* 3979 */         return ((TitledBorder)localBorder).getTitle();
/*      */       }
/* 3981 */       return null;
/*      */     }
/*      */ 
/*      */     public AccessibleKeyBinding getAccessibleKeyBinding()
/*      */     {
/* 3994 */       return null;
/*      */     }
/*      */ 
/*      */     protected class AccessibleContainerHandler
/*      */       implements ContainerListener
/*      */     {
/*      */       protected AccessibleContainerHandler()
/*      */       {
/*      */       }
/*      */ 
/*      */       public void componentAdded(ContainerEvent paramContainerEvent)
/*      */       {
/* 3700 */         Component localComponent = paramContainerEvent.getChild();
/* 3701 */         if ((localComponent != null) && ((localComponent instanceof Accessible)))
/* 3702 */           JComponent.AccessibleJComponent.this.firePropertyChange("AccessibleChild", null, localComponent.getAccessibleContext());
/*      */       }
/*      */ 
/*      */       public void componentRemoved(ContainerEvent paramContainerEvent)
/*      */       {
/* 3708 */         Component localComponent = paramContainerEvent.getChild();
/* 3709 */         if ((localComponent != null) && ((localComponent instanceof Accessible)))
/* 3710 */           JComponent.AccessibleJComponent.this.firePropertyChange("AccessibleChild", localComponent.getAccessibleContext(), null);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected class AccessibleFocusHandler
/*      */       implements FocusListener
/*      */     {
/*      */       protected AccessibleFocusHandler()
/*      */       {
/*      */       }
/*      */ 
/*      */       public void focusGained(FocusEvent paramFocusEvent)
/*      */       {
/* 3724 */         if (JComponent.this.accessibleContext != null)
/* 3725 */           JComponent.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
/*      */       }
/*      */ 
/*      */       public void focusLost(FocusEvent paramFocusEvent)
/*      */       {
/* 3731 */         if (JComponent.this.accessibleContext != null)
/* 3732 */           JComponent.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   final class ActionStandin
/*      */     implements Action
/*      */   {
/*      */     private final ActionListener actionListener;
/*      */     private final String command;
/*      */     private final Action action;
/*      */ 
/*      */     ActionStandin(ActionListener paramString, String arg3)
/*      */     {
/* 3380 */       this.actionListener = paramString;
/* 3381 */       if ((paramString instanceof Action)) {
/* 3382 */         this.action = ((Action)paramString);
/*      */       }
/*      */       else
/* 3385 */         this.action = null;
/*      */       Object localObject;
/* 3387 */       this.command = localObject;
/*      */     }
/*      */ 
/*      */     public Object getValue(String paramString) {
/* 3391 */       if (paramString != null) {
/* 3392 */         if (paramString.equals("ActionCommandKey")) {
/* 3393 */           return this.command;
/*      */         }
/* 3395 */         if (this.action != null) {
/* 3396 */           return this.action.getValue(paramString);
/*      */         }
/* 3398 */         if (paramString.equals("Name")) {
/* 3399 */           return "ActionStandin";
/*      */         }
/*      */       }
/* 3402 */       return null;
/*      */     }
/*      */ 
/*      */     public boolean isEnabled() {
/* 3406 */       if (this.actionListener == null)
/*      */       {
/* 3412 */         return false;
/*      */       }
/* 3414 */       if (this.action == null) {
/* 3415 */         return true;
/*      */       }
/* 3417 */       return this.action.isEnabled();
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 3421 */       if (this.actionListener != null)
/* 3422 */         this.actionListener.actionPerformed(paramActionEvent);
/*      */     }
/*      */ 
/*      */     public void putValue(String paramString, Object paramObject)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setEnabled(boolean paramBoolean)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class IntVector {
/* 3443 */     int[] array = null;
/* 3444 */     int count = 0;
/* 3445 */     int capacity = 0;
/*      */ 
/*      */     int size() {
/* 3448 */       return this.count;
/*      */     }
/*      */ 
/*      */     int elementAt(int paramInt) {
/* 3452 */       return this.array[paramInt];
/*      */     }
/*      */ 
/*      */     void addElement(int paramInt) {
/* 3456 */       if (this.count == this.capacity) {
/* 3457 */         this.capacity = ((this.capacity + 2) * 2);
/* 3458 */         int[] arrayOfInt = new int[this.capacity];
/* 3459 */         if (this.count > 0) {
/* 3460 */           System.arraycopy(this.array, 0, arrayOfInt, 0, this.count);
/*      */         }
/* 3462 */         this.array = arrayOfInt;
/*      */       }
/* 3464 */       this.array[(this.count++)] = paramInt;
/*      */     }
/*      */ 
/*      */     void setElementAt(int paramInt1, int paramInt2) {
/* 3468 */       this.array[paramInt2] = paramInt1;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class KeyboardState implements Serializable {
/* 3473 */     private static final Object keyCodesKey = KeyboardState.class;
/*      */ 
/*      */     static JComponent.IntVector getKeyCodeArray()
/*      */     {
/* 3478 */       JComponent.IntVector localIntVector = (JComponent.IntVector)SwingUtilities.appContextGet(keyCodesKey);
/*      */ 
/* 3480 */       if (localIntVector == null) {
/* 3481 */         localIntVector = new JComponent.IntVector();
/* 3482 */         SwingUtilities.appContextPut(keyCodesKey, localIntVector);
/*      */       }
/* 3484 */       return localIntVector;
/*      */     }
/*      */ 
/*      */     static void registerKeyPressed(int paramInt) {
/* 3488 */       JComponent.IntVector localIntVector = getKeyCodeArray();
/* 3489 */       int i = localIntVector.size();
/*      */ 
/* 3491 */       for (int j = 0; j < i; j++) {
/* 3492 */         if (localIntVector.elementAt(j) == -1) {
/* 3493 */           localIntVector.setElementAt(paramInt, j);
/* 3494 */           return;
/*      */         }
/*      */       }
/* 3497 */       localIntVector.addElement(paramInt);
/*      */     }
/*      */ 
/*      */     static void registerKeyReleased(int paramInt) {
/* 3501 */       JComponent.IntVector localIntVector = getKeyCodeArray();
/* 3502 */       int i = localIntVector.size();
/*      */ 
/* 3504 */       for (int j = 0; j < i; j++)
/* 3505 */         if (localIntVector.elementAt(j) == paramInt) {
/* 3506 */           localIntVector.setElementAt(-1, j);
/* 3507 */           return;
/*      */         }
/*      */     }
/*      */ 
/*      */     static boolean keyIsPressed(int paramInt)
/*      */     {
/* 3513 */       JComponent.IntVector localIntVector = getKeyCodeArray();
/* 3514 */       int i = localIntVector.size();
/*      */ 
/* 3516 */       for (int j = 0; j < i; j++) {
/* 3517 */         if (localIntVector.elementAt(j) == paramInt) {
/* 3518 */           return true;
/*      */         }
/*      */       }
/* 3521 */       return false;
/*      */     }
/*      */ 
/*      */     static boolean shouldProcess(KeyEvent paramKeyEvent)
/*      */     {
/* 3529 */       switch (paramKeyEvent.getID()) {
/*      */       case 401:
/* 3531 */         if (!keyIsPressed(paramKeyEvent.getKeyCode())) {
/* 3532 */           registerKeyPressed(paramKeyEvent.getKeyCode());
/*      */         }
/* 3534 */         return true;
/*      */       case 402:
/* 3540 */         if ((keyIsPressed(paramKeyEvent.getKeyCode())) || (paramKeyEvent.getKeyCode() == 154)) {
/* 3541 */           registerKeyReleased(paramKeyEvent.getKeyCode());
/* 3542 */           return true;
/*      */         }
/* 3544 */         return false;
/*      */       case 400:
/* 3546 */         return true;
/*      */       }
/*      */ 
/* 3549 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ReadObjectCallback
/*      */     implements ObjectInputValidation
/*      */   {
/* 5405 */     private final Vector<JComponent> roots = new Vector(1);
/*      */     private final ObjectInputStream inputStream;
/*      */ 
/*      */     ReadObjectCallback(ObjectInputStream arg2)
/*      */       throws Exception
/*      */     {
/*      */       Object localObject;
/* 5409 */       this.inputStream = localObject;
/* 5410 */       localObject.registerValidation(this, 0);
/*      */     }
/*      */ 
/*      */     public void validateObject()
/*      */       throws InvalidObjectException
/*      */     {
/*      */       try
/*      */       {
/* 5421 */         for (JComponent localJComponent : this.roots)
/* 5422 */           SwingUtilities.updateComponentTreeUI(localJComponent);
/*      */       }
/*      */       finally
/*      */       {
/* 5426 */         JComponent.readObjectCallbacks.remove(this.inputStream);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void registerComponent(JComponent paramJComponent)
/*      */     {
/* 5441 */       for (Iterator localIterator = this.roots.iterator(); localIterator.hasNext(); ) { localJComponent = (JComponent)localIterator.next();
/* 5442 */         for (localObject = paramJComponent; localObject != null; localObject = ((Component)localObject).getParent())
/* 5443 */           if (localObject == localJComponent)
/*      */             return;
/*      */       }
/*      */       JComponent localJComponent;
/*      */       Object localObject;
/* 5453 */       for (int i = 0; i < this.roots.size(); i++) {
/* 5454 */         localJComponent = (JComponent)this.roots.elementAt(i);
/* 5455 */         for (localObject = localJComponent.getParent(); localObject != null; localObject = ((Component)localObject).getParent()) {
/* 5456 */           if (localObject == paramJComponent) {
/* 5457 */             this.roots.removeElementAt(i--);
/* 5458 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 5463 */       this.roots.addElement(paramJComponent);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.JComponent
 * JD-Core Version:    0.6.2
 */