/*      */ package javax.swing.plaf.basic;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Component.BaselineResizeBehavior;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.Point;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ContainerEvent;
/*      */ import java.awt.event.ContainerListener;
/*      */ import java.awt.event.FocusAdapter;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JTabbedPane;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.LookAndFeel;
/*      */ import javax.swing.SwingConstants;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.plaf.ComponentInputMapUIResource;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.TabbedPaneUI;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.text.View;
/*      */ import sun.swing.DefaultLookup;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.UIAction;
/*      */ 
/*      */ public class BasicTabbedPaneUI extends TabbedPaneUI
/*      */   implements SwingConstants
/*      */ {
/*      */   protected JTabbedPane tabPane;
/*      */   protected Color highlight;
/*      */   protected Color lightHighlight;
/*      */   protected Color shadow;
/*      */   protected Color darkShadow;
/*      */   protected Color focus;
/*      */   private Color selectedColor;
/*      */   protected int textIconGap;
/*      */   protected int tabRunOverlay;
/*      */   protected Insets tabInsets;
/*      */   protected Insets selectedTabPadInsets;
/*      */   protected Insets tabAreaInsets;
/*      */   protected Insets contentBorderInsets;
/*      */   private boolean tabsOverlapBorder;
/*      */   private boolean tabsOpaque;
/*      */   private boolean contentOpaque;
/*      */ 
/*      */   @Deprecated
/*      */   protected KeyStroke upKey;
/*      */ 
/*      */   @Deprecated
/*      */   protected KeyStroke downKey;
/*      */ 
/*      */   @Deprecated
/*      */   protected KeyStroke leftKey;
/*      */ 
/*      */   @Deprecated
/*      */   protected KeyStroke rightKey;
/*      */   protected int[] tabRuns;
/*      */   protected int runCount;
/*      */   protected int selectedRun;
/*      */   protected Rectangle[] rects;
/*      */   protected int maxTabHeight;
/*      */   protected int maxTabWidth;
/*      */   protected ChangeListener tabChangeListener;
/*      */   protected PropertyChangeListener propertyChangeListener;
/*      */   protected MouseListener mouseListener;
/*      */   protected FocusListener focusListener;
/*      */   private Insets currentPadInsets;
/*      */   private Insets currentTabAreaInsets;
/*      */   private Component visibleComponent;
/*      */   private Vector<View> htmlViews;
/*      */   private Hashtable<Integer, Integer> mnemonicToIndexMap;
/*      */   private InputMap mnemonicInputMap;
/*      */   private ScrollableTabSupport tabScroller;
/*      */   private TabContainer tabContainer;
/*      */   protected transient Rectangle calcRect;
/*      */   private int focusIndex;
/*      */   private Handler handler;
/*      */   private int rolloverTabIndex;
/*      */   private boolean isRunsDirty;
/*      */   private boolean calculatedBaseline;
/*      */   private int baseline;
/*  915 */   private static int[] xCropLen = { 1, 1, 0, 0, 1, 1, 2, 2 };
/*  916 */   private static int[] yCropLen = { 0, 3, 3, 6, 6, 9, 9, 12 };
/*      */   private static final int CROP_SEGMENT = 12;
/*      */ 
/*      */   public BasicTabbedPaneUI()
/*      */   {
/*   77 */     this.tabsOpaque = true;
/*   78 */     this.contentOpaque = true;
/*      */ 
/*  124 */     this.tabRuns = new int[10];
/*  125 */     this.runCount = 0;
/*  126 */     this.selectedRun = -1;
/*  127 */     this.rects = new Rectangle[0];
/*      */ 
/*  140 */     this.currentPadInsets = new Insets(0, 0, 0, 0);
/*  141 */     this.currentTabAreaInsets = new Insets(0, 0, 0, 0);
/*      */ 
/*  164 */     this.calcRect = new Rectangle(0, 0, 0, 0);
/*      */   }
/*      */ 
/*      */   public static ComponentUI createUI(JComponent paramJComponent)
/*      */   {
/*  194 */     return new BasicTabbedPaneUI();
/*      */   }
/*      */ 
/*      */   static void loadActionMap(LazyActionMap paramLazyActionMap) {
/*  198 */     paramLazyActionMap.put(new Actions("navigateNext"));
/*  199 */     paramLazyActionMap.put(new Actions("navigatePrevious"));
/*  200 */     paramLazyActionMap.put(new Actions("navigateRight"));
/*  201 */     paramLazyActionMap.put(new Actions("navigateLeft"));
/*  202 */     paramLazyActionMap.put(new Actions("navigateUp"));
/*  203 */     paramLazyActionMap.put(new Actions("navigateDown"));
/*  204 */     paramLazyActionMap.put(new Actions("navigatePageUp"));
/*  205 */     paramLazyActionMap.put(new Actions("navigatePageDown"));
/*  206 */     paramLazyActionMap.put(new Actions("requestFocus"));
/*  207 */     paramLazyActionMap.put(new Actions("requestFocusForVisibleComponent"));
/*  208 */     paramLazyActionMap.put(new Actions("setSelectedIndex"));
/*  209 */     paramLazyActionMap.put(new Actions("selectTabWithFocus"));
/*  210 */     paramLazyActionMap.put(new Actions("scrollTabsForwardAction"));
/*  211 */     paramLazyActionMap.put(new Actions("scrollTabsBackwardAction"));
/*      */   }
/*      */ 
/*      */   public void installUI(JComponent paramJComponent)
/*      */   {
/*  217 */     this.tabPane = ((JTabbedPane)paramJComponent);
/*      */ 
/*  219 */     this.calculatedBaseline = false;
/*  220 */     this.rolloverTabIndex = -1;
/*  221 */     this.focusIndex = -1;
/*  222 */     paramJComponent.setLayout(createLayoutManager());
/*  223 */     installComponents();
/*  224 */     installDefaults();
/*  225 */     installListeners();
/*  226 */     installKeyboardActions();
/*      */   }
/*      */ 
/*      */   public void uninstallUI(JComponent paramJComponent) {
/*  230 */     uninstallKeyboardActions();
/*  231 */     uninstallListeners();
/*  232 */     uninstallDefaults();
/*  233 */     uninstallComponents();
/*  234 */     paramJComponent.setLayout(null);
/*      */ 
/*  236 */     this.tabPane = null;
/*      */   }
/*      */ 
/*      */   protected LayoutManager createLayoutManager()
/*      */   {
/*  250 */     if (this.tabPane.getTabLayoutPolicy() == 1) {
/*  251 */       return new TabbedPaneScrollLayout(null);
/*      */     }
/*  253 */     return new TabbedPaneLayout();
/*      */   }
/*      */ 
/*      */   private boolean scrollableTabLayoutEnabled()
/*      */   {
/*  263 */     return this.tabPane.getLayout() instanceof TabbedPaneScrollLayout;
/*      */   }
/*      */ 
/*      */   protected void installComponents()
/*      */   {
/*  273 */     if ((scrollableTabLayoutEnabled()) && 
/*  274 */       (this.tabScroller == null)) {
/*  275 */       this.tabScroller = new ScrollableTabSupport(this.tabPane.getTabPlacement());
/*  276 */       this.tabPane.add(this.tabScroller.viewport);
/*      */     }
/*      */ 
/*  279 */     installTabContainer();
/*      */   }
/*      */ 
/*      */   private void installTabContainer() {
/*  283 */     for (int i = 0; i < this.tabPane.getTabCount(); i++) {
/*  284 */       Component localComponent = this.tabPane.getTabComponentAt(i);
/*  285 */       if (localComponent != null) {
/*  286 */         if (this.tabContainer == null) {
/*  287 */           this.tabContainer = new TabContainer();
/*      */         }
/*  289 */         this.tabContainer.add(localComponent);
/*      */       }
/*      */     }
/*  292 */     if (this.tabContainer == null) {
/*  293 */       return;
/*      */     }
/*  295 */     if (scrollableTabLayoutEnabled())
/*  296 */       this.tabScroller.tabPanel.add(this.tabContainer);
/*      */     else
/*  298 */       this.tabPane.add(this.tabContainer);
/*      */   }
/*      */ 
/*      */   protected JButton createScrollButton(int paramInt)
/*      */   {
/*  317 */     if ((paramInt != 5) && (paramInt != 1) && (paramInt != 3) && (paramInt != 7))
/*      */     {
/*  319 */       throw new IllegalArgumentException("Direction must be one of: SOUTH, NORTH, EAST or WEST");
/*      */     }
/*      */ 
/*  322 */     return new ScrollableTabButton(paramInt);
/*      */   }
/*      */ 
/*      */   protected void uninstallComponents()
/*      */   {
/*  332 */     uninstallTabContainer();
/*  333 */     if (scrollableTabLayoutEnabled()) {
/*  334 */       this.tabPane.remove(this.tabScroller.viewport);
/*  335 */       this.tabPane.remove(this.tabScroller.scrollForwardButton);
/*  336 */       this.tabPane.remove(this.tabScroller.scrollBackwardButton);
/*  337 */       this.tabScroller = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void uninstallTabContainer() {
/*  342 */     if (this.tabContainer == null) {
/*  343 */       return;
/*      */     }
/*      */ 
/*  347 */     this.tabContainer.notifyTabbedPane = false;
/*  348 */     this.tabContainer.removeAll();
/*  349 */     if (scrollableTabLayoutEnabled()) {
/*  350 */       this.tabContainer.remove(this.tabScroller.croppedEdge);
/*  351 */       this.tabScroller.tabPanel.remove(this.tabContainer);
/*      */     } else {
/*  353 */       this.tabPane.remove(this.tabContainer);
/*      */     }
/*  355 */     this.tabContainer = null;
/*      */   }
/*      */ 
/*      */   protected void installDefaults() {
/*  359 */     LookAndFeel.installColorsAndFont(this.tabPane, "TabbedPane.background", "TabbedPane.foreground", "TabbedPane.font");
/*      */ 
/*  361 */     this.highlight = UIManager.getColor("TabbedPane.light");
/*  362 */     this.lightHighlight = UIManager.getColor("TabbedPane.highlight");
/*  363 */     this.shadow = UIManager.getColor("TabbedPane.shadow");
/*  364 */     this.darkShadow = UIManager.getColor("TabbedPane.darkShadow");
/*  365 */     this.focus = UIManager.getColor("TabbedPane.focus");
/*  366 */     this.selectedColor = UIManager.getColor("TabbedPane.selected");
/*      */ 
/*  368 */     this.textIconGap = UIManager.getInt("TabbedPane.textIconGap");
/*  369 */     this.tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
/*  370 */     this.selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
/*  371 */     this.tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
/*  372 */     this.tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");
/*  373 */     this.contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
/*  374 */     this.tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
/*  375 */     this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
/*  376 */     this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
/*  377 */     Object localObject = UIManager.get("TabbedPane.opaque");
/*  378 */     if (localObject == null) {
/*  379 */       localObject = Boolean.FALSE;
/*      */     }
/*  381 */     LookAndFeel.installProperty(this.tabPane, "opaque", localObject);
/*      */ 
/*  386 */     if (this.tabInsets == null) this.tabInsets = new Insets(0, 4, 1, 4);
/*  387 */     if (this.selectedTabPadInsets == null) this.selectedTabPadInsets = new Insets(2, 2, 2, 1);
/*  388 */     if (this.tabAreaInsets == null) this.tabAreaInsets = new Insets(3, 2, 0, 2);
/*  389 */     if (this.contentBorderInsets == null) this.contentBorderInsets = new Insets(2, 2, 3, 3); 
/*      */   }
/*      */ 
/*      */   protected void uninstallDefaults()
/*      */   {
/*  393 */     this.highlight = null;
/*  394 */     this.lightHighlight = null;
/*  395 */     this.shadow = null;
/*  396 */     this.darkShadow = null;
/*  397 */     this.focus = null;
/*  398 */     this.tabInsets = null;
/*  399 */     this.selectedTabPadInsets = null;
/*  400 */     this.tabAreaInsets = null;
/*  401 */     this.contentBorderInsets = null;
/*      */   }
/*      */ 
/*      */   protected void installListeners() {
/*  405 */     if ((this.propertyChangeListener = createPropertyChangeListener()) != null) {
/*  406 */       this.tabPane.addPropertyChangeListener(this.propertyChangeListener);
/*      */     }
/*  408 */     if ((this.tabChangeListener = createChangeListener()) != null) {
/*  409 */       this.tabPane.addChangeListener(this.tabChangeListener);
/*      */     }
/*  411 */     if ((this.mouseListener = createMouseListener()) != null) {
/*  412 */       this.tabPane.addMouseListener(this.mouseListener);
/*      */     }
/*  414 */     this.tabPane.addMouseMotionListener(getHandler());
/*  415 */     if ((this.focusListener = createFocusListener()) != null) {
/*  416 */       this.tabPane.addFocusListener(this.focusListener);
/*      */     }
/*  418 */     this.tabPane.addContainerListener(getHandler());
/*  419 */     if (this.tabPane.getTabCount() > 0)
/*  420 */       this.htmlViews = createHTMLVector();
/*      */   }
/*      */ 
/*      */   protected void uninstallListeners()
/*      */   {
/*  425 */     if (this.mouseListener != null) {
/*  426 */       this.tabPane.removeMouseListener(this.mouseListener);
/*  427 */       this.mouseListener = null;
/*      */     }
/*  429 */     this.tabPane.removeMouseMotionListener(getHandler());
/*  430 */     if (this.focusListener != null) {
/*  431 */       this.tabPane.removeFocusListener(this.focusListener);
/*  432 */       this.focusListener = null;
/*      */     }
/*      */ 
/*  435 */     this.tabPane.removeContainerListener(getHandler());
/*  436 */     if (this.htmlViews != null) {
/*  437 */       this.htmlViews.removeAllElements();
/*  438 */       this.htmlViews = null;
/*      */     }
/*  440 */     if (this.tabChangeListener != null) {
/*  441 */       this.tabPane.removeChangeListener(this.tabChangeListener);
/*  442 */       this.tabChangeListener = null;
/*      */     }
/*  444 */     if (this.propertyChangeListener != null) {
/*  445 */       this.tabPane.removePropertyChangeListener(this.propertyChangeListener);
/*  446 */       this.propertyChangeListener = null;
/*      */     }
/*  448 */     this.handler = null;
/*      */   }
/*      */ 
/*      */   protected MouseListener createMouseListener() {
/*  452 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected FocusListener createFocusListener() {
/*  456 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected ChangeListener createChangeListener() {
/*  460 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected PropertyChangeListener createPropertyChangeListener() {
/*  464 */     return getHandler();
/*      */   }
/*      */ 
/*      */   private Handler getHandler() {
/*  468 */     if (this.handler == null) {
/*  469 */       this.handler = new Handler(null);
/*      */     }
/*  471 */     return this.handler;
/*      */   }
/*      */ 
/*      */   protected void installKeyboardActions() {
/*  475 */     InputMap localInputMap = getInputMap(1);
/*      */ 
/*  478 */     SwingUtilities.replaceUIInputMap(this.tabPane, 1, localInputMap);
/*      */ 
/*  481 */     localInputMap = getInputMap(0);
/*  482 */     SwingUtilities.replaceUIInputMap(this.tabPane, 0, localInputMap);
/*      */ 
/*  484 */     LazyActionMap.installLazyActionMap(this.tabPane, BasicTabbedPaneUI.class, "TabbedPane.actionMap");
/*      */ 
/*  486 */     updateMnemonics();
/*      */   }
/*      */ 
/*      */   InputMap getInputMap(int paramInt) {
/*  490 */     if (paramInt == 1) {
/*  491 */       return (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.ancestorInputMap");
/*      */     }
/*      */ 
/*  494 */     if (paramInt == 0) {
/*  495 */       return (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.focusInputMap");
/*      */     }
/*      */ 
/*  498 */     return null;
/*      */   }
/*      */ 
/*      */   protected void uninstallKeyboardActions() {
/*  502 */     SwingUtilities.replaceUIActionMap(this.tabPane, null);
/*  503 */     SwingUtilities.replaceUIInputMap(this.tabPane, 1, null);
/*      */ 
/*  506 */     SwingUtilities.replaceUIInputMap(this.tabPane, 0, null);
/*      */ 
/*  508 */     SwingUtilities.replaceUIInputMap(this.tabPane, 2, null);
/*      */ 
/*  511 */     this.mnemonicToIndexMap = null;
/*  512 */     this.mnemonicInputMap = null;
/*      */   }
/*      */ 
/*      */   private void updateMnemonics()
/*      */   {
/*  520 */     resetMnemonics();
/*  521 */     for (int i = this.tabPane.getTabCount() - 1; i >= 0; 
/*  522 */       i--) {
/*  523 */       int j = this.tabPane.getMnemonicAt(i);
/*      */ 
/*  525 */       if (j > 0)
/*  526 */         addMnemonic(i, j);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void resetMnemonics()
/*      */   {
/*  535 */     if (this.mnemonicToIndexMap != null) {
/*  536 */       this.mnemonicToIndexMap.clear();
/*  537 */       this.mnemonicInputMap.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addMnemonic(int paramInt1, int paramInt2)
/*      */   {
/*  545 */     if (this.mnemonicToIndexMap == null) {
/*  546 */       initMnemonics();
/*      */     }
/*  548 */     this.mnemonicInputMap.put(KeyStroke.getKeyStroke(paramInt2, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "setSelectedIndex");
/*      */ 
/*  550 */     this.mnemonicToIndexMap.put(Integer.valueOf(paramInt2), Integer.valueOf(paramInt1));
/*      */   }
/*      */ 
/*      */   private void initMnemonics()
/*      */   {
/*  557 */     this.mnemonicToIndexMap = new Hashtable();
/*  558 */     this.mnemonicInputMap = new ComponentInputMapUIResource(this.tabPane);
/*  559 */     this.mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(this.tabPane, 2));
/*      */ 
/*  561 */     SwingUtilities.replaceUIInputMap(this.tabPane, 2, this.mnemonicInputMap);
/*      */   }
/*      */ 
/*      */   private void setRolloverTab(int paramInt1, int paramInt2)
/*      */   {
/*  575 */     setRolloverTab(tabForCoordinate(this.tabPane, paramInt1, paramInt2, false));
/*      */   }
/*      */ 
/*      */   protected void setRolloverTab(int paramInt)
/*      */   {
/*  588 */     this.rolloverTabIndex = paramInt;
/*      */   }
/*      */ 
/*      */   protected int getRolloverTab()
/*      */   {
/*  600 */     return this.rolloverTabIndex;
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(JComponent paramJComponent)
/*      */   {
/*  605 */     return null;
/*      */   }
/*      */ 
/*      */   public Dimension getMaximumSize(JComponent paramJComponent)
/*      */   {
/*  610 */     return null;
/*      */   }
/*      */ 
/*      */   public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
/*      */   {
/*  622 */     super.getBaseline(paramJComponent, paramInt1, paramInt2);
/*  623 */     int i = calculateBaselineIfNecessary();
/*  624 */     if (i != -1) {
/*  625 */       int j = this.tabPane.getTabPlacement();
/*  626 */       Insets localInsets1 = this.tabPane.getInsets();
/*  627 */       Insets localInsets2 = getTabAreaInsets(j);
/*  628 */       switch (j) {
/*      */       case 1:
/*  630 */         i += localInsets1.top + localInsets2.top;
/*  631 */         return i;
/*      */       case 3:
/*  633 */         i = paramInt2 - localInsets1.bottom - localInsets2.bottom - this.maxTabHeight + i;
/*      */ 
/*  635 */         return i;
/*      */       case 2:
/*      */       case 4:
/*  638 */         i += localInsets1.top + localInsets2.top;
/*  639 */         return i;
/*      */       }
/*      */     }
/*  642 */     return -1;
/*      */   }
/*      */ 
/*      */   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
/*      */   {
/*  655 */     super.getBaselineResizeBehavior(paramJComponent);
/*  656 */     switch (this.tabPane.getTabPlacement()) {
/*      */     case 1:
/*      */     case 2:
/*      */     case 4:
/*  660 */       return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
/*      */     case 3:
/*  662 */       return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
/*      */     }
/*  664 */     return Component.BaselineResizeBehavior.OTHER;
/*      */   }
/*      */ 
/*      */   protected int getBaseline(int paramInt)
/*      */   {
/*  678 */     if (this.tabPane.getTabComponentAt(paramInt) != null) {
/*  679 */       int i = getBaselineOffset();
/*  680 */       if (i != 0)
/*      */       {
/*  684 */         return -1;
/*      */       }
/*  686 */       Component localComponent = this.tabPane.getTabComponentAt(paramInt);
/*  687 */       Dimension localDimension = localComponent.getPreferredSize();
/*  688 */       Insets localInsets = getTabInsets(this.tabPane.getTabPlacement(), paramInt);
/*  689 */       int m = this.maxTabHeight - localInsets.top - localInsets.bottom;
/*  690 */       return localComponent.getBaseline(localDimension.width, localDimension.height) + (m - localDimension.height) / 2 + localInsets.top;
/*      */     }
/*      */ 
/*  694 */     Object localObject = getTextViewForTab(paramInt);
/*  695 */     if (localObject != null) {
/*  696 */       j = (int)((View)localObject).getPreferredSpan(1);
/*  697 */       k = BasicHTML.getHTMLBaseline((View)localObject, (int)((View)localObject).getPreferredSpan(0), j);
/*      */ 
/*  699 */       if (k >= 0) {
/*  700 */         return this.maxTabHeight / 2 - j / 2 + k + getBaselineOffset();
/*      */       }
/*      */ 
/*  703 */       return -1;
/*      */     }
/*      */ 
/*  706 */     localObject = getFontMetrics();
/*  707 */     int j = ((FontMetrics)localObject).getHeight();
/*  708 */     int k = ((FontMetrics)localObject).getAscent();
/*  709 */     return this.maxTabHeight / 2 - j / 2 + k + getBaselineOffset();
/*      */   }
/*      */ 
/*      */   protected int getBaselineOffset()
/*      */   {
/*  721 */     switch (this.tabPane.getTabPlacement()) {
/*      */     case 1:
/*  723 */       if (this.tabPane.getTabCount() > 1) {
/*  724 */         return 1;
/*      */       }
/*      */ 
/*  727 */       return -1;
/*      */     case 3:
/*  730 */       if (this.tabPane.getTabCount() > 1) {
/*  731 */         return -1;
/*      */       }
/*      */ 
/*  734 */       return 1;
/*      */     }
/*      */ 
/*  737 */     return this.maxTabHeight % 2;
/*      */   }
/*      */ 
/*      */   private int calculateBaselineIfNecessary()
/*      */   {
/*  742 */     if (!this.calculatedBaseline) {
/*  743 */       this.calculatedBaseline = true;
/*  744 */       this.baseline = -1;
/*  745 */       if (this.tabPane.getTabCount() > 0) {
/*  746 */         calculateBaseline();
/*      */       }
/*      */     }
/*  749 */     return this.baseline;
/*      */   }
/*      */ 
/*      */   private void calculateBaseline() {
/*  753 */     int i = this.tabPane.getTabCount();
/*  754 */     int j = this.tabPane.getTabPlacement();
/*  755 */     this.maxTabHeight = calculateMaxTabHeight(j);
/*  756 */     this.baseline = getBaseline(0);
/*  757 */     if (isHorizontalTabPlacement()) {
/*  758 */       for (int k = 1; k < i; k++) {
/*  759 */         if (getBaseline(k) != this.baseline) {
/*  760 */           this.baseline = -1;
/*  761 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  767 */       FontMetrics localFontMetrics = getFontMetrics();
/*  768 */       int m = localFontMetrics.getHeight();
/*  769 */       int n = calculateTabHeight(j, 0, m);
/*  770 */       for (int i1 = 1; i1 < i; i1++) {
/*  771 */         int i2 = calculateTabHeight(j, i1, m);
/*  772 */         if (n != i2)
/*      */         {
/*  774 */           this.baseline = -1;
/*  775 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics, JComponent paramJComponent)
/*      */   {
/*  784 */     int i = this.tabPane.getSelectedIndex();
/*  785 */     int j = this.tabPane.getTabPlacement();
/*      */ 
/*  787 */     ensureCurrentLayout();
/*      */ 
/*  790 */     if (this.tabsOverlapBorder) {
/*  791 */       paintContentBorder(paramGraphics, j, i);
/*      */     }
/*      */ 
/*  796 */     if (!scrollableTabLayoutEnabled()) {
/*  797 */       paintTabArea(paramGraphics, j, i);
/*      */     }
/*  799 */     if (!this.tabsOverlapBorder)
/*  800 */       paintContentBorder(paramGraphics, j, i);
/*      */   }
/*      */ 
/*      */   protected void paintTabArea(Graphics paramGraphics, int paramInt1, int paramInt2)
/*      */   {
/*  822 */     int i = this.tabPane.getTabCount();
/*      */ 
/*  824 */     Rectangle localRectangle1 = new Rectangle();
/*  825 */     Rectangle localRectangle2 = new Rectangle();
/*  826 */     Rectangle localRectangle3 = paramGraphics.getClipBounds();
/*      */ 
/*  829 */     for (int j = this.runCount - 1; j >= 0; j--) {
/*  830 */       int k = this.tabRuns[j];
/*  831 */       int m = this.tabRuns[(j + 1)];
/*  832 */       int n = m != 0 ? m - 1 : i - 1;
/*  833 */       for (int i1 = k; i1 <= n; i1++) {
/*  834 */         if ((i1 != paramInt2) && (this.rects[i1].intersects(localRectangle3))) {
/*  835 */           paintTab(paramGraphics, paramInt1, this.rects, i1, localRectangle1, localRectangle2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  842 */     if ((paramInt2 >= 0) && (this.rects[paramInt2].intersects(localRectangle3)))
/*  843 */       paintTab(paramGraphics, paramInt1, this.rects, paramInt2, localRectangle1, localRectangle2);
/*      */   }
/*      */ 
/*      */   protected void paintTab(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2)
/*      */   {
/*  850 */     Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
/*  851 */     int i = this.tabPane.getSelectedIndex();
/*  852 */     boolean bool = i == paramInt2;
/*      */ 
/*  854 */     if ((this.tabsOpaque) || (this.tabPane.isOpaque())) {
/*  855 */       paintTabBackground(paramGraphics, paramInt1, paramInt2, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, bool);
/*      */     }
/*      */ 
/*  859 */     paintTabBorder(paramGraphics, paramInt1, paramInt2, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, bool);
/*      */ 
/*  862 */     String str1 = this.tabPane.getTitleAt(paramInt2);
/*  863 */     Font localFont = this.tabPane.getFont();
/*  864 */     FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(this.tabPane, paramGraphics, localFont);
/*  865 */     Icon localIcon = getIconForTab(paramInt2);
/*      */ 
/*  867 */     layoutLabel(paramInt1, localFontMetrics, paramInt2, str1, localIcon, localRectangle, paramRectangle1, paramRectangle2, bool);
/*      */ 
/*  870 */     if (this.tabPane.getTabComponentAt(paramInt2) == null) {
/*  871 */       String str2 = str1;
/*      */ 
/*  873 */       if ((scrollableTabLayoutEnabled()) && (this.tabScroller.croppedEdge.isParamsSet()) && (this.tabScroller.croppedEdge.getTabIndex() == paramInt2) && (isHorizontalTabPlacement()))
/*      */       {
/*  875 */         int j = this.tabScroller.croppedEdge.getCropline() - (paramRectangle2.x - localRectangle.x) - this.tabScroller.croppedEdge.getCroppedSideWidth();
/*      */ 
/*  877 */         str2 = SwingUtilities2.clipStringIfNecessary(null, localFontMetrics, str1, j);
/*      */       }
/*      */ 
/*  880 */       paintText(paramGraphics, paramInt1, localFont, localFontMetrics, paramInt2, str2, paramRectangle2, bool);
/*      */ 
/*  883 */       paintIcon(paramGraphics, paramInt1, paramInt2, localIcon, paramRectangle1, bool);
/*      */     }
/*  885 */     paintFocusIndicator(paramGraphics, paramInt1, paramArrayOfRectangle, paramInt2, paramRectangle1, paramRectangle2, bool);
/*      */   }
/*      */ 
/*      */   private boolean isHorizontalTabPlacement()
/*      */   {
/*  890 */     return (this.tabPane.getTabPlacement() == 1) || (this.tabPane.getTabPlacement() == 3);
/*      */   }
/*      */ 
/*      */   private static Polygon createCroppedTabShape(int paramInt1, Rectangle paramRectangle, int paramInt2)
/*      */   {
/*      */     int i;
/*      */     int j;
/*      */     int k;
/*      */     int m;
/*  925 */     switch (paramInt1) {
/*      */     case 2:
/*      */     case 4:
/*  928 */       i = paramRectangle.width;
/*  929 */       j = paramRectangle.x;
/*  930 */       k = paramRectangle.x + paramRectangle.width;
/*  931 */       m = paramRectangle.y + paramRectangle.height;
/*  932 */       break;
/*      */     case 1:
/*      */     case 3:
/*      */     default:
/*  936 */       i = paramRectangle.height;
/*  937 */       j = paramRectangle.y;
/*  938 */       k = paramRectangle.y + paramRectangle.height;
/*  939 */       m = paramRectangle.x + paramRectangle.width;
/*      */     }
/*  941 */     int n = i / 12;
/*  942 */     if (i % 12 > 0) {
/*  943 */       n++;
/*      */     }
/*  945 */     int i1 = 2 + n * 8;
/*  946 */     int[] arrayOfInt1 = new int[i1];
/*  947 */     int[] arrayOfInt2 = new int[i1];
/*  948 */     int i2 = 0;
/*      */ 
/*  950 */     arrayOfInt1[i2] = m;
/*  951 */     arrayOfInt2[(i2++)] = k;
/*  952 */     arrayOfInt1[i2] = m;
/*  953 */     arrayOfInt2[(i2++)] = j;
/*  954 */     for (int i3 = 0; i3 < n; i3++) {
/*  955 */       for (int i4 = 0; i4 < xCropLen.length; i4++) {
/*  956 */         arrayOfInt1[i2] = (paramInt2 - xCropLen[i4]);
/*  957 */         arrayOfInt2[i2] = (j + i3 * 12 + yCropLen[i4]);
/*  958 */         if (arrayOfInt2[i2] >= k) {
/*  959 */           arrayOfInt2[i2] = k;
/*  960 */           i2++;
/*  961 */           break;
/*      */         }
/*  963 */         i2++;
/*      */       }
/*      */     }
/*  966 */     if ((paramInt1 == 1) || (paramInt1 == 3)) {
/*  967 */       return new Polygon(arrayOfInt1, arrayOfInt2, i2);
/*      */     }
/*      */ 
/*  970 */     return new Polygon(arrayOfInt2, arrayOfInt1, i2);
/*      */   }
/*      */ 
/*      */   private void paintCroppedTabEdge(Graphics paramGraphics)
/*      */   {
/*  978 */     int i = this.tabScroller.croppedEdge.getTabIndex();
/*  979 */     int j = this.tabScroller.croppedEdge.getCropline();
/*      */     int k;
/*      */     int m;
/*      */     int n;
/*  981 */     switch (this.tabPane.getTabPlacement()) {
/*      */     case 2:
/*      */     case 4:
/*  984 */       k = this.rects[i].x;
/*  985 */       m = j;
/*  986 */       n = k;
/*  987 */       paramGraphics.setColor(this.shadow);
/*      */     case 1:
/*  988 */     case 3: } while (n <= k + this.rects[i].width) {
/*  989 */       for (int i1 = 0; i1 < xCropLen.length; i1 += 2) {
/*  990 */         paramGraphics.drawLine(n + yCropLen[i1], m - xCropLen[i1], n + yCropLen[(i1 + 1)] - 1, m - xCropLen[(i1 + 1)]);
/*      */       }
/*      */ 
/*  993 */       n += 12; continue;
/*      */ 
/*  999 */       k = j;
/* 1000 */       m = this.rects[i].y;
/* 1001 */       i1 = m;
/* 1002 */       paramGraphics.setColor(this.shadow);
/* 1003 */       while (i1 <= m + this.rects[i].height) {
/* 1004 */         for (int i2 = 0; i2 < xCropLen.length; i2 += 2) {
/* 1005 */           paramGraphics.drawLine(k - xCropLen[i2], i1 + yCropLen[i2], k - xCropLen[(i2 + 1)], i1 + yCropLen[(i2 + 1)] - 1);
/*      */         }
/*      */ 
/* 1008 */         i1 += 12;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void layoutLabel(int paramInt1, FontMetrics paramFontMetrics, int paramInt2, String paramString, Icon paramIcon, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, boolean paramBoolean)
/*      */   {
/* 1018 */     paramRectangle3.x = (paramRectangle3.y = paramRectangle2.x = paramRectangle2.y = 0);
/*      */ 
/* 1020 */     View localView = getTextViewForTab(paramInt2);
/* 1021 */     if (localView != null) {
/* 1022 */       this.tabPane.putClientProperty("html", localView);
/*      */     }
/*      */ 
/* 1025 */     SwingUtilities.layoutCompoundLabel(this.tabPane, paramFontMetrics, paramString, paramIcon, 0, 0, 0, 11, paramRectangle1, paramRectangle2, paramRectangle3, this.textIconGap);
/*      */ 
/* 1036 */     this.tabPane.putClientProperty("html", null);
/*      */ 
/* 1038 */     int i = getTabLabelShiftX(paramInt1, paramInt2, paramBoolean);
/* 1039 */     int j = getTabLabelShiftY(paramInt1, paramInt2, paramBoolean);
/* 1040 */     paramRectangle2.x += i;
/* 1041 */     paramRectangle2.y += j;
/* 1042 */     paramRectangle3.x += i;
/* 1043 */     paramRectangle3.y += j;
/*      */   }
/*      */ 
/*      */   protected void paintIcon(Graphics paramGraphics, int paramInt1, int paramInt2, Icon paramIcon, Rectangle paramRectangle, boolean paramBoolean)
/*      */   {
/* 1049 */     if (paramIcon != null)
/* 1050 */       paramIcon.paintIcon(this.tabPane, paramGraphics, paramRectangle.x, paramRectangle.y);
/*      */   }
/*      */ 
/*      */   protected void paintText(Graphics paramGraphics, int paramInt1, Font paramFont, FontMetrics paramFontMetrics, int paramInt2, String paramString, Rectangle paramRectangle, boolean paramBoolean)
/*      */   {
/* 1059 */     paramGraphics.setFont(paramFont);
/*      */ 
/* 1061 */     View localView = getTextViewForTab(paramInt2);
/* 1062 */     if (localView != null)
/*      */     {
/* 1064 */       localView.paint(paramGraphics, paramRectangle);
/*      */     }
/*      */     else {
/* 1067 */       int i = this.tabPane.getDisplayedMnemonicIndexAt(paramInt2);
/*      */ 
/* 1069 */       if ((this.tabPane.isEnabled()) && (this.tabPane.isEnabledAt(paramInt2))) {
/* 1070 */         Object localObject = this.tabPane.getForegroundAt(paramInt2);
/* 1071 */         if ((paramBoolean) && ((localObject instanceof UIResource))) {
/* 1072 */           Color localColor = UIManager.getColor("TabbedPane.selectedForeground");
/*      */ 
/* 1074 */           if (localColor != null) {
/* 1075 */             localObject = localColor;
/*      */           }
/*      */         }
/* 1078 */         paramGraphics.setColor((Color)localObject);
/* 1079 */         SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + paramFontMetrics.getAscent());
/*      */       }
/*      */       else
/*      */       {
/* 1084 */         paramGraphics.setColor(this.tabPane.getBackgroundAt(paramInt2).brighter());
/* 1085 */         SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + paramFontMetrics.getAscent());
/*      */ 
/* 1088 */         paramGraphics.setColor(this.tabPane.getBackgroundAt(paramInt2).darker());
/* 1089 */         SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x - 1, paramRectangle.y + paramFontMetrics.getAscent() - 1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getTabLabelShiftX(int paramInt1, int paramInt2, boolean paramBoolean)
/*      */   {
/* 1099 */     Rectangle localRectangle = this.rects[paramInt2];
/* 1100 */     String str = paramBoolean ? "selectedLabelShift" : "labelShift";
/* 1101 */     int i = DefaultLookup.getInt(this.tabPane, this, "TabbedPane." + str, 1);
/*      */ 
/* 1104 */     switch (paramInt1) {
/*      */     case 2:
/* 1106 */       return i;
/*      */     case 4:
/* 1108 */       return -i;
/*      */     case 1:
/*      */     case 3:
/*      */     }
/* 1112 */     return localRectangle.width % 2;
/*      */   }
/*      */ 
/*      */   protected int getTabLabelShiftY(int paramInt1, int paramInt2, boolean paramBoolean)
/*      */   {
/* 1117 */     Rectangle localRectangle = this.rects[paramInt2];
/* 1118 */     int i = paramBoolean ? DefaultLookup.getInt(this.tabPane, this, "TabbedPane.selectedLabelShift", -1) : DefaultLookup.getInt(this.tabPane, this, "TabbedPane.labelShift", 1);
/*      */ 
/* 1121 */     switch (paramInt1) {
/*      */     case 3:
/* 1123 */       return -i;
/*      */     case 2:
/*      */     case 4:
/* 1126 */       return localRectangle.height % 2;
/*      */     case 1:
/*      */     }
/* 1129 */     return i;
/*      */   }
/*      */ 
/*      */   protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean)
/*      */   {
/* 1137 */     Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
/* 1138 */     if ((this.tabPane.hasFocus()) && (paramBoolean))
/*      */     {
/* 1140 */       paramGraphics.setColor(this.focus);
/*      */       int i;
/*      */       int j;
/*      */       int k;
/*      */       int m;
/* 1141 */       switch (paramInt1) {
/*      */       case 2:
/* 1143 */         i = localRectangle.x + 3;
/* 1144 */         j = localRectangle.y + 3;
/* 1145 */         k = localRectangle.width - 5;
/* 1146 */         m = localRectangle.height - 6;
/* 1147 */         break;
/*      */       case 4:
/* 1149 */         i = localRectangle.x + 2;
/* 1150 */         j = localRectangle.y + 3;
/* 1151 */         k = localRectangle.width - 5;
/* 1152 */         m = localRectangle.height - 6;
/* 1153 */         break;
/*      */       case 3:
/* 1155 */         i = localRectangle.x + 3;
/* 1156 */         j = localRectangle.y + 2;
/* 1157 */         k = localRectangle.width - 6;
/* 1158 */         m = localRectangle.height - 5;
/* 1159 */         break;
/*      */       case 1:
/*      */       default:
/* 1162 */         i = localRectangle.x + 3;
/* 1163 */         j = localRectangle.y + 3;
/* 1164 */         k = localRectangle.width - 6;
/* 1165 */         m = localRectangle.height - 5;
/*      */       }
/* 1167 */       BasicGraphicsUtils.drawDashedRect(paramGraphics, i, j, k, m);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
/*      */   {
/* 1180 */     paramGraphics.setColor(this.lightHighlight);
/*      */ 
/* 1182 */     switch (paramInt1) {
/*      */     case 2:
/* 1184 */       paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
/* 1185 */       paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 3);
/* 1186 */       paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
/* 1187 */       paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 1, paramInt4);
/*      */ 
/* 1189 */       paramGraphics.setColor(this.shadow);
/* 1190 */       paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
/*      */ 
/* 1192 */       paramGraphics.setColor(this.darkShadow);
/* 1193 */       paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
/* 1194 */       break;
/*      */     case 4:
/* 1196 */       paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
/*      */ 
/* 1198 */       paramGraphics.setColor(this.shadow);
/* 1199 */       paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
/* 1200 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
/*      */ 
/* 1202 */       paramGraphics.setColor(this.darkShadow);
/* 1203 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
/* 1204 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
/* 1205 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
/* 1206 */       paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
/* 1207 */       break;
/*      */     case 3:
/* 1209 */       paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 3);
/* 1210 */       paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
/*      */ 
/* 1212 */       paramGraphics.setColor(this.shadow);
/* 1213 */       paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
/* 1214 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
/*      */ 
/* 1216 */       paramGraphics.setColor(this.darkShadow);
/* 1217 */       paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
/* 1218 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
/* 1219 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
/* 1220 */       break;
/*      */     case 1:
/*      */     default:
/* 1223 */       paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 1);
/* 1224 */       paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
/* 1225 */       paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
/*      */ 
/* 1227 */       paramGraphics.setColor(this.shadow);
/* 1228 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 1);
/*      */ 
/* 1230 */       paramGraphics.setColor(this.darkShadow);
/* 1231 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
/* 1232 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
/*      */   {
/* 1240 */     paramGraphics.setColor((!paramBoolean) || (this.selectedColor == null) ? this.tabPane.getBackgroundAt(paramInt2) : this.selectedColor);
/*      */ 
/* 1242 */     switch (paramInt1) {
/*      */     case 2:
/* 1244 */       paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 1, paramInt6 - 3);
/* 1245 */       break;
/*      */     case 4:
/* 1247 */       paramGraphics.fillRect(paramInt3, paramInt4 + 1, paramInt5 - 2, paramInt6 - 3);
/* 1248 */       break;
/*      */     case 3:
/* 1250 */       paramGraphics.fillRect(paramInt3 + 1, paramInt4, paramInt5 - 3, paramInt6 - 1);
/* 1251 */       break;
/*      */     case 1:
/*      */     default:
/* 1254 */       paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 3, paramInt6 - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintContentBorder(Graphics paramGraphics, int paramInt1, int paramInt2) {
/* 1259 */     int i = this.tabPane.getWidth();
/* 1260 */     int j = this.tabPane.getHeight();
/* 1261 */     Insets localInsets1 = this.tabPane.getInsets();
/* 1262 */     Insets localInsets2 = getTabAreaInsets(paramInt1);
/*      */ 
/* 1264 */     int k = localInsets1.left;
/* 1265 */     int m = localInsets1.top;
/* 1266 */     int n = i - localInsets1.right - localInsets1.left;
/* 1267 */     int i1 = j - localInsets1.top - localInsets1.bottom;
/*      */ 
/* 1269 */     switch (paramInt1) {
/*      */     case 2:
/* 1271 */       k += calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
/* 1272 */       if (this.tabsOverlapBorder) {
/* 1273 */         k -= localInsets2.right;
/*      */       }
/* 1275 */       n -= k - localInsets1.left;
/* 1276 */       break;
/*      */     case 4:
/* 1278 */       n -= calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
/* 1279 */       if (this.tabsOverlapBorder)
/* 1280 */         n += localInsets2.left; break;
/*      */     case 3:
/* 1284 */       i1 -= calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
/* 1285 */       if (this.tabsOverlapBorder)
/* 1286 */         i1 += localInsets2.top; break;
/*      */     case 1:
/*      */     default:
/* 1291 */       m += calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
/* 1292 */       if (this.tabsOverlapBorder) {
/* 1293 */         m -= localInsets2.bottom;
/*      */       }
/* 1295 */       i1 -= m - localInsets1.top;
/*      */     }
/*      */ 
/* 1298 */     if ((this.tabPane.getTabCount() > 0) && ((this.contentOpaque) || (this.tabPane.isOpaque())))
/*      */     {
/* 1300 */       Color localColor = UIManager.getColor("TabbedPane.contentAreaColor");
/* 1301 */       if (localColor != null) {
/* 1302 */         paramGraphics.setColor(localColor);
/*      */       }
/* 1304 */       else if ((this.selectedColor == null) || (paramInt2 == -1)) {
/* 1305 */         paramGraphics.setColor(this.tabPane.getBackground());
/*      */       }
/*      */       else {
/* 1308 */         paramGraphics.setColor(this.selectedColor);
/*      */       }
/* 1310 */       paramGraphics.fillRect(k, m, n, i1);
/*      */     }
/*      */ 
/* 1313 */     paintContentBorderTopEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
/* 1314 */     paintContentBorderLeftEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
/* 1315 */     paintContentBorderBottomEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
/* 1316 */     paintContentBorderRightEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
/*      */   }
/*      */ 
/*      */   protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/* 1323 */     Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
/*      */ 
/* 1326 */     paramGraphics.setColor(this.lightHighlight);
/*      */ 
/* 1332 */     if ((paramInt1 != 1) || (paramInt2 < 0) || (localRectangle.y + localRectangle.height + 1 < paramInt4) || (localRectangle.x < paramInt3) || (localRectangle.x > paramInt3 + paramInt5))
/*      */     {
/* 1335 */       paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
/*      */     }
/*      */     else {
/* 1338 */       paramGraphics.drawLine(paramInt3, paramInt4, localRectangle.x - 1, paramInt4);
/* 1339 */       if (localRectangle.x + localRectangle.width < paramInt3 + paramInt5 - 2) {
/* 1340 */         paramGraphics.drawLine(localRectangle.x + localRectangle.width, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
/*      */       }
/*      */       else {
/* 1343 */         paramGraphics.setColor(this.shadow);
/* 1344 */         paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintContentBorderLeftEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/* 1352 */     Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
/*      */ 
/* 1355 */     paramGraphics.setColor(this.lightHighlight);
/*      */ 
/* 1361 */     if ((paramInt1 != 2) || (paramInt2 < 0) || (localRectangle.x + localRectangle.width + 1 < paramInt3) || (localRectangle.y < paramInt4) || (localRectangle.y > paramInt4 + paramInt6))
/*      */     {
/* 1364 */       paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 2);
/*      */     }
/*      */     else {
/* 1367 */       paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, localRectangle.y - 1);
/* 1368 */       if (localRectangle.y + localRectangle.height < paramInt4 + paramInt6 - 2)
/* 1369 */         paramGraphics.drawLine(paramInt3, localRectangle.y + localRectangle.height, paramInt3, paramInt4 + paramInt6 - 2);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/* 1378 */     Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
/*      */ 
/* 1381 */     paramGraphics.setColor(this.shadow);
/*      */ 
/* 1387 */     if ((paramInt1 != 3) || (paramInt2 < 0) || (localRectangle.y - 1 > paramInt6) || (localRectangle.x < paramInt3) || (localRectangle.x > paramInt3 + paramInt5))
/*      */     {
/* 1390 */       paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
/* 1391 */       paramGraphics.setColor(this.darkShadow);
/* 1392 */       paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
/*      */     }
/*      */     else {
/* 1395 */       paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, localRectangle.x - 1, paramInt4 + paramInt6 - 2);
/* 1396 */       paramGraphics.setColor(this.darkShadow);
/* 1397 */       paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, localRectangle.x - 1, paramInt4 + paramInt6 - 1);
/* 1398 */       if (localRectangle.x + localRectangle.width < paramInt3 + paramInt5 - 2) {
/* 1399 */         paramGraphics.setColor(this.shadow);
/* 1400 */         paramGraphics.drawLine(localRectangle.x + localRectangle.width, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
/* 1401 */         paramGraphics.setColor(this.darkShadow);
/* 1402 */         paramGraphics.drawLine(localRectangle.x + localRectangle.width, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/* 1411 */     Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
/*      */ 
/* 1414 */     paramGraphics.setColor(this.shadow);
/*      */ 
/* 1420 */     if ((paramInt1 != 4) || (paramInt2 < 0) || (localRectangle.x - 1 > paramInt5) || (localRectangle.y < paramInt4) || (localRectangle.y > paramInt4 + paramInt6))
/*      */     {
/* 1423 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
/* 1424 */       paramGraphics.setColor(this.darkShadow);
/* 1425 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
/*      */     }
/*      */     else {
/* 1428 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, localRectangle.y - 1);
/* 1429 */       paramGraphics.setColor(this.darkShadow);
/* 1430 */       paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, localRectangle.y - 1);
/*      */ 
/* 1432 */       if (localRectangle.y + localRectangle.height < paramInt4 + paramInt6 - 2) {
/* 1433 */         paramGraphics.setColor(this.shadow);
/* 1434 */         paramGraphics.drawLine(paramInt3 + paramInt5 - 2, localRectangle.y + localRectangle.height, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
/*      */ 
/* 1436 */         paramGraphics.setColor(this.darkShadow);
/* 1437 */         paramGraphics.drawLine(paramInt3 + paramInt5 - 1, localRectangle.y + localRectangle.height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void ensureCurrentLayout()
/*      */   {
/* 1444 */     if (!this.tabPane.isValid()) {
/* 1445 */       this.tabPane.validate();
/*      */     }
/*      */ 
/* 1451 */     if (!this.tabPane.isValid()) {
/* 1452 */       TabbedPaneLayout localTabbedPaneLayout = (TabbedPaneLayout)this.tabPane.getLayout();
/* 1453 */       localTabbedPaneLayout.calculateLayoutInfo();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Rectangle getTabBounds(JTabbedPane paramJTabbedPane, int paramInt)
/*      */   {
/* 1465 */     ensureCurrentLayout();
/* 1466 */     Rectangle localRectangle = new Rectangle();
/* 1467 */     return getTabBounds(paramInt, localRectangle);
/*      */   }
/*      */ 
/*      */   public int getTabRunCount(JTabbedPane paramJTabbedPane) {
/* 1471 */     ensureCurrentLayout();
/* 1472 */     return this.runCount;
/*      */   }
/*      */ 
/*      */   public int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2)
/*      */   {
/* 1480 */     return tabForCoordinate(paramJTabbedPane, paramInt1, paramInt2, true);
/*      */   }
/*      */ 
/*      */   private int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2, boolean paramBoolean)
/*      */   {
/* 1485 */     if (paramBoolean) {
/* 1486 */       ensureCurrentLayout();
/*      */     }
/* 1488 */     if (this.isRunsDirty)
/*      */     {
/* 1491 */       return -1;
/*      */     }
/* 1493 */     Point localPoint = new Point(paramInt1, paramInt2);
/*      */ 
/* 1495 */     if (scrollableTabLayoutEnabled()) {
/* 1496 */       translatePointToTabPanel(paramInt1, paramInt2, localPoint);
/* 1497 */       Rectangle localRectangle = this.tabScroller.viewport.getViewRect();
/* 1498 */       if (!localRectangle.contains(localPoint)) {
/* 1499 */         return -1;
/*      */       }
/*      */     }
/* 1502 */     int i = this.tabPane.getTabCount();
/* 1503 */     for (int j = 0; j < i; j++) {
/* 1504 */       if (this.rects[j].contains(localPoint.x, localPoint.y)) {
/* 1505 */         return j;
/*      */       }
/*      */     }
/* 1508 */     return -1;
/*      */   }
/*      */ 
/*      */   protected Rectangle getTabBounds(int paramInt, Rectangle paramRectangle)
/*      */   {
/* 1532 */     paramRectangle.width = this.rects[paramInt].width;
/* 1533 */     paramRectangle.height = this.rects[paramInt].height;
/*      */ 
/* 1535 */     if (scrollableTabLayoutEnabled())
/*      */     {
/* 1538 */       Point localPoint1 = this.tabScroller.viewport.getLocation();
/* 1539 */       Point localPoint2 = this.tabScroller.viewport.getViewPosition();
/* 1540 */       paramRectangle.x = (this.rects[paramInt].x + localPoint1.x - localPoint2.x);
/* 1541 */       paramRectangle.y = (this.rects[paramInt].y + localPoint1.y - localPoint2.y);
/*      */     }
/*      */     else {
/* 1544 */       paramRectangle.x = this.rects[paramInt].x;
/* 1545 */       paramRectangle.y = this.rects[paramInt].y;
/*      */     }
/* 1547 */     return paramRectangle;
/*      */   }
/*      */ 
/*      */   private int getClosestTab(int paramInt1, int paramInt2)
/*      */   {
/* 1555 */     int i = 0;
/* 1556 */     int j = Math.min(this.rects.length, this.tabPane.getTabCount());
/* 1557 */     int k = j;
/* 1558 */     int m = this.tabPane.getTabPlacement();
/* 1559 */     int n = (m == 1) || (m == 3) ? 1 : 0;
/* 1560 */     int i1 = n != 0 ? paramInt1 : paramInt2;
/*      */ 
/* 1562 */     while (i != k) {
/* 1563 */       int i2 = (k + i) / 2;
/*      */       int i3;
/*      */       int i4;
/* 1567 */       if (n != 0) {
/* 1568 */         i3 = this.rects[i2].x;
/* 1569 */         i4 = i3 + this.rects[i2].width;
/*      */       }
/*      */       else {
/* 1572 */         i3 = this.rects[i2].y;
/* 1573 */         i4 = i3 + this.rects[i2].height;
/*      */       }
/* 1575 */       if (i1 < i3) {
/* 1576 */         k = i2;
/* 1577 */         if (i == k) {
/* 1578 */           return Math.max(0, i2 - 1);
/*      */         }
/*      */       }
/* 1581 */       else if (i1 >= i4) {
/* 1582 */         i = i2;
/* 1583 */         if (k - i <= 1)
/* 1584 */           return Math.max(i2 + 1, j - 1);
/*      */       }
/*      */       else
/*      */       {
/* 1588 */         return i2;
/*      */       }
/*      */     }
/* 1591 */     return i;
/*      */   }
/*      */ 
/*      */   private Point translatePointToTabPanel(int paramInt1, int paramInt2, Point paramPoint)
/*      */   {
/* 1600 */     Point localPoint1 = this.tabScroller.viewport.getLocation();
/* 1601 */     Point localPoint2 = this.tabScroller.viewport.getViewPosition();
/* 1602 */     paramPoint.x = (paramInt1 - localPoint1.x + localPoint2.x);
/* 1603 */     paramPoint.y = (paramInt2 - localPoint1.y + localPoint2.y);
/* 1604 */     return paramPoint;
/*      */   }
/*      */ 
/*      */   protected Component getVisibleComponent()
/*      */   {
/* 1610 */     return this.visibleComponent;
/*      */   }
/*      */ 
/*      */   protected void setVisibleComponent(Component paramComponent) {
/* 1614 */     if ((this.visibleComponent != null) && (this.visibleComponent != paramComponent) && (this.visibleComponent.getParent() == this.tabPane) && (this.visibleComponent.isVisible()))
/*      */     {
/* 1619 */       this.visibleComponent.setVisible(false);
/*      */     }
/* 1621 */     if ((paramComponent != null) && (!paramComponent.isVisible())) {
/* 1622 */       paramComponent.setVisible(true);
/*      */     }
/* 1624 */     this.visibleComponent = paramComponent;
/*      */   }
/*      */ 
/*      */   protected void assureRectsCreated(int paramInt) {
/* 1628 */     int i = this.rects.length;
/* 1629 */     if (paramInt != i) {
/* 1630 */       Rectangle[] arrayOfRectangle = new Rectangle[paramInt];
/* 1631 */       System.arraycopy(this.rects, 0, arrayOfRectangle, 0, Math.min(i, paramInt));
/*      */ 
/* 1633 */       this.rects = arrayOfRectangle;
/* 1634 */       for (int j = i; j < paramInt; j++)
/* 1635 */         this.rects[j] = new Rectangle();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void expandTabRunsArray()
/*      */   {
/* 1642 */     int i = this.tabRuns.length;
/* 1643 */     int[] arrayOfInt = new int[i + 10];
/* 1644 */     System.arraycopy(this.tabRuns, 0, arrayOfInt, 0, this.runCount);
/* 1645 */     this.tabRuns = arrayOfInt;
/*      */   }
/*      */ 
/*      */   protected int getRunForTab(int paramInt1, int paramInt2) {
/* 1649 */     for (int i = 0; i < this.runCount; i++) {
/* 1650 */       int j = this.tabRuns[i];
/* 1651 */       int k = lastTabInRun(paramInt1, i);
/* 1652 */       if ((paramInt2 >= j) && (paramInt2 <= k)) {
/* 1653 */         return i;
/*      */       }
/*      */     }
/* 1656 */     return 0;
/*      */   }
/*      */ 
/*      */   protected int lastTabInRun(int paramInt1, int paramInt2) {
/* 1660 */     if (this.runCount == 1) {
/* 1661 */       return paramInt1 - 1;
/*      */     }
/* 1663 */     int i = paramInt2 == this.runCount - 1 ? 0 : paramInt2 + 1;
/* 1664 */     if (this.tabRuns[i] == 0) {
/* 1665 */       return paramInt1 - 1;
/*      */     }
/* 1667 */     return this.tabRuns[i] - 1;
/*      */   }
/*      */ 
/*      */   protected int getTabRunOverlay(int paramInt) {
/* 1671 */     return this.tabRunOverlay;
/*      */   }
/*      */ 
/*      */   protected int getTabRunIndent(int paramInt1, int paramInt2) {
/* 1675 */     return 0;
/*      */   }
/*      */ 
/*      */   protected boolean shouldPadTabRun(int paramInt1, int paramInt2) {
/* 1679 */     return this.runCount > 1;
/*      */   }
/*      */ 
/*      */   protected boolean shouldRotateTabRuns(int paramInt) {
/* 1683 */     return true;
/*      */   }
/*      */ 
/*      */   protected Icon getIconForTab(int paramInt) {
/* 1687 */     return (!this.tabPane.isEnabled()) || (!this.tabPane.isEnabledAt(paramInt)) ? this.tabPane.getDisabledIconAt(paramInt) : this.tabPane.getIconAt(paramInt);
/*      */   }
/*      */ 
/*      */   protected View getTextViewForTab(int paramInt)
/*      */   {
/* 1703 */     if (this.htmlViews != null) {
/* 1704 */       return (View)this.htmlViews.elementAt(paramInt);
/*      */     }
/* 1706 */     return null;
/*      */   }
/*      */ 
/*      */   protected int calculateTabHeight(int paramInt1, int paramInt2, int paramInt3) {
/* 1710 */     int i = 0;
/* 1711 */     Component localComponent = this.tabPane.getTabComponentAt(paramInt2);
/* 1712 */     if (localComponent != null) {
/* 1713 */       i = localComponent.getPreferredSize().height;
/*      */     } else {
/* 1715 */       localObject = getTextViewForTab(paramInt2);
/* 1716 */       if (localObject != null)
/*      */       {
/* 1718 */         i += (int)((View)localObject).getPreferredSpan(1);
/*      */       }
/*      */       else {
/* 1721 */         i += paramInt3;
/*      */       }
/* 1723 */       Icon localIcon = getIconForTab(paramInt2);
/*      */ 
/* 1725 */       if (localIcon != null) {
/* 1726 */         i = Math.max(i, localIcon.getIconHeight());
/*      */       }
/*      */     }
/* 1729 */     Object localObject = getTabInsets(paramInt1, paramInt2);
/* 1730 */     i += ((Insets)localObject).top + ((Insets)localObject).bottom + 2;
/* 1731 */     return i;
/*      */   }
/*      */ 
/*      */   protected int calculateMaxTabHeight(int paramInt) {
/* 1735 */     FontMetrics localFontMetrics = getFontMetrics();
/* 1736 */     int i = this.tabPane.getTabCount();
/* 1737 */     int j = 0;
/* 1738 */     int k = localFontMetrics.getHeight();
/* 1739 */     for (int m = 0; m < i; m++) {
/* 1740 */       j = Math.max(calculateTabHeight(paramInt, m, k), j);
/*      */     }
/* 1742 */     return j;
/*      */   }
/*      */ 
/*      */   protected int calculateTabWidth(int paramInt1, int paramInt2, FontMetrics paramFontMetrics) {
/* 1746 */     Insets localInsets = getTabInsets(paramInt1, paramInt2);
/* 1747 */     int i = localInsets.left + localInsets.right + 3;
/* 1748 */     Component localComponent = this.tabPane.getTabComponentAt(paramInt2);
/* 1749 */     if (localComponent != null) {
/* 1750 */       i += localComponent.getPreferredSize().width;
/*      */     } else {
/* 1752 */       Icon localIcon = getIconForTab(paramInt2);
/* 1753 */       if (localIcon != null) {
/* 1754 */         i += localIcon.getIconWidth() + this.textIconGap;
/*      */       }
/* 1756 */       View localView = getTextViewForTab(paramInt2);
/* 1757 */       if (localView != null)
/*      */       {
/* 1759 */         i += (int)localView.getPreferredSpan(0);
/*      */       }
/*      */       else {
/* 1762 */         String str = this.tabPane.getTitleAt(paramInt2);
/* 1763 */         i += SwingUtilities2.stringWidth(this.tabPane, paramFontMetrics, str);
/*      */       }
/*      */     }
/* 1766 */     return i;
/*      */   }
/*      */ 
/*      */   protected int calculateMaxTabWidth(int paramInt) {
/* 1770 */     FontMetrics localFontMetrics = getFontMetrics();
/* 1771 */     int i = this.tabPane.getTabCount();
/* 1772 */     int j = 0;
/* 1773 */     for (int k = 0; k < i; k++) {
/* 1774 */       j = Math.max(calculateTabWidth(paramInt, k, localFontMetrics), j);
/*      */     }
/* 1776 */     return j;
/*      */   }
/*      */ 
/*      */   protected int calculateTabAreaHeight(int paramInt1, int paramInt2, int paramInt3) {
/* 1780 */     Insets localInsets = getTabAreaInsets(paramInt1);
/* 1781 */     int i = getTabRunOverlay(paramInt1);
/* 1782 */     return paramInt2 > 0 ? paramInt2 * (paramInt3 - i) + i + localInsets.top + localInsets.bottom : 0;
/*      */   }
/*      */ 
/*      */   protected int calculateTabAreaWidth(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1789 */     Insets localInsets = getTabAreaInsets(paramInt1);
/* 1790 */     int i = getTabRunOverlay(paramInt1);
/* 1791 */     return paramInt2 > 0 ? paramInt2 * (paramInt3 - i) + i + localInsets.left + localInsets.right : 0;
/*      */   }
/*      */ 
/*      */   protected Insets getTabInsets(int paramInt1, int paramInt2)
/*      */   {
/* 1798 */     return this.tabInsets;
/*      */   }
/*      */ 
/*      */   protected Insets getSelectedTabPadInsets(int paramInt) {
/* 1802 */     rotateInsets(this.selectedTabPadInsets, this.currentPadInsets, paramInt);
/* 1803 */     return this.currentPadInsets;
/*      */   }
/*      */ 
/*      */   protected Insets getTabAreaInsets(int paramInt) {
/* 1807 */     rotateInsets(this.tabAreaInsets, this.currentTabAreaInsets, paramInt);
/* 1808 */     return this.currentTabAreaInsets;
/*      */   }
/*      */ 
/*      */   protected Insets getContentBorderInsets(int paramInt) {
/* 1812 */     return this.contentBorderInsets;
/*      */   }
/*      */ 
/*      */   protected FontMetrics getFontMetrics() {
/* 1816 */     Font localFont = this.tabPane.getFont();
/* 1817 */     return this.tabPane.getFontMetrics(localFont);
/*      */   }
/*      */ 
/*      */   protected void navigateSelectedTab(int paramInt)
/*      */   {
/* 1824 */     int i = this.tabPane.getTabPlacement();
/* 1825 */     int j = DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true) ? this.tabPane.getSelectedIndex() : getFocusIndex();
/*      */ 
/* 1828 */     int k = this.tabPane.getTabCount();
/* 1829 */     boolean bool = BasicGraphicsUtils.isLeftToRight(this.tabPane);
/*      */ 
/* 1832 */     if (k <= 0)
/*      */       return;
/*      */     int m;
/* 1837 */     switch (i) {
/*      */     case 2:
/*      */     case 4:
/* 1840 */       switch (paramInt) {
/*      */       case 12:
/* 1842 */         selectNextTab(j);
/* 1843 */         break;
/*      */       case 13:
/* 1845 */         selectPreviousTab(j);
/* 1846 */         break;
/*      */       case 1:
/* 1848 */         selectPreviousTabInRun(j);
/* 1849 */         break;
/*      */       case 5:
/* 1851 */         selectNextTabInRun(j);
/* 1852 */         break;
/*      */       case 7:
/* 1854 */         m = getTabRunOffset(i, k, j, false);
/* 1855 */         selectAdjacentRunTab(i, j, m);
/* 1856 */         break;
/*      */       case 3:
/* 1858 */         m = getTabRunOffset(i, k, j, true);
/* 1859 */         selectAdjacentRunTab(i, j, m);
/*      */       case 2:
/*      */       case 4:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 1863 */       case 11: } break;
/*      */     case 1:
/*      */     case 3:
/*      */     default:
/* 1867 */       switch (paramInt) {
/*      */       case 12:
/* 1869 */         selectNextTab(j);
/* 1870 */         break;
/*      */       case 13:
/* 1872 */         selectPreviousTab(j);
/* 1873 */         break;
/*      */       case 1:
/* 1875 */         m = getTabRunOffset(i, k, j, false);
/* 1876 */         selectAdjacentRunTab(i, j, m);
/* 1877 */         break;
/*      */       case 5:
/* 1879 */         m = getTabRunOffset(i, k, j, true);
/* 1880 */         selectAdjacentRunTab(i, j, m);
/* 1881 */         break;
/*      */       case 3:
/* 1883 */         if (bool)
/* 1884 */           selectNextTabInRun(j);
/*      */         else {
/* 1886 */           selectPreviousTabInRun(j);
/*      */         }
/* 1888 */         break;
/*      */       case 7:
/* 1890 */         if (bool)
/* 1891 */           selectPreviousTabInRun(j);
/*      */         else
/* 1893 */           selectNextTabInRun(j); break;
/*      */       case 2:
/*      */       case 4:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 1895 */       case 11: } break;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void selectNextTabInRun(int paramInt)
/*      */   {
/* 1902 */     int i = this.tabPane.getTabCount();
/* 1903 */     int j = getNextTabIndexInRun(i, paramInt);
/*      */ 
/* 1905 */     while ((j != paramInt) && (!this.tabPane.isEnabledAt(j))) {
/* 1906 */       j = getNextTabIndexInRun(i, j);
/*      */     }
/* 1908 */     navigateTo(j);
/*      */   }
/*      */ 
/*      */   protected void selectPreviousTabInRun(int paramInt) {
/* 1912 */     int i = this.tabPane.getTabCount();
/* 1913 */     int j = getPreviousTabIndexInRun(i, paramInt);
/*      */ 
/* 1915 */     while ((j != paramInt) && (!this.tabPane.isEnabledAt(j))) {
/* 1916 */       j = getPreviousTabIndexInRun(i, j);
/*      */     }
/* 1918 */     navigateTo(j);
/*      */   }
/*      */ 
/*      */   protected void selectNextTab(int paramInt) {
/* 1922 */     int i = getNextTabIndex(paramInt);
/*      */ 
/* 1924 */     while ((i != paramInt) && (!this.tabPane.isEnabledAt(i))) {
/* 1925 */       i = getNextTabIndex(i);
/*      */     }
/* 1927 */     navigateTo(i);
/*      */   }
/*      */ 
/*      */   protected void selectPreviousTab(int paramInt) {
/* 1931 */     int i = getPreviousTabIndex(paramInt);
/*      */ 
/* 1933 */     while ((i != paramInt) && (!this.tabPane.isEnabledAt(i))) {
/* 1934 */       i = getPreviousTabIndex(i);
/*      */     }
/* 1936 */     navigateTo(i);
/*      */   }
/*      */ 
/*      */   protected void selectAdjacentRunTab(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1941 */     if (this.runCount < 2) {
/* 1942 */       return;
/*      */     }
/*      */ 
/* 1945 */     Rectangle localRectangle = this.rects[paramInt2];
/*      */     int i;
/* 1946 */     switch (paramInt1) {
/*      */     case 2:
/*      */     case 4:
/* 1949 */       i = tabForCoordinate(this.tabPane, localRectangle.x + localRectangle.width / 2 + paramInt3, localRectangle.y + localRectangle.height / 2);
/*      */ 
/* 1951 */       break;
/*      */     case 1:
/*      */     case 3:
/*      */     default:
/* 1955 */       i = tabForCoordinate(this.tabPane, localRectangle.x + localRectangle.width / 2, localRectangle.y + localRectangle.height / 2 + paramInt3);
/*      */     }
/*      */ 
/* 1958 */     if (i != -1) {
/* 1959 */       while ((!this.tabPane.isEnabledAt(i)) && (i != paramInt2)) {
/* 1960 */         i = getNextTabIndex(i);
/*      */       }
/* 1962 */       navigateTo(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void navigateTo(int paramInt) {
/* 1967 */     if (DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true))
/*      */     {
/* 1969 */       this.tabPane.setSelectedIndex(paramInt);
/*      */     }
/*      */     else
/* 1972 */       setFocusIndex(paramInt, true);
/*      */   }
/*      */ 
/*      */   void setFocusIndex(int paramInt, boolean paramBoolean)
/*      */   {
/* 1977 */     if ((paramBoolean) && (!this.isRunsDirty)) {
/* 1978 */       repaintTab(this.focusIndex);
/* 1979 */       this.focusIndex = paramInt;
/* 1980 */       repaintTab(this.focusIndex);
/*      */     }
/*      */     else {
/* 1983 */       this.focusIndex = paramInt;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void repaintTab(int paramInt)
/*      */   {
/* 1993 */     if ((!this.isRunsDirty) && (paramInt >= 0) && (paramInt < this.tabPane.getTabCount()))
/* 1994 */       this.tabPane.repaint(getTabBounds(this.tabPane, paramInt));
/*      */   }
/*      */ 
/*      */   private void validateFocusIndex()
/*      */   {
/* 2002 */     if (this.focusIndex >= this.tabPane.getTabCount())
/* 2003 */       setFocusIndex(this.tabPane.getSelectedIndex(), false);
/*      */   }
/*      */ 
/*      */   protected int getFocusIndex()
/*      */   {
/* 2014 */     return this.focusIndex;
/*      */   }
/*      */ 
/*      */   protected int getTabRunOffset(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
/*      */   {
/* 2019 */     int i = getRunForTab(paramInt2, paramInt3);
/*      */     int j;
/* 2021 */     switch (paramInt1) {
/*      */     case 2:
/* 2023 */       if (i == 0) {
/* 2024 */         j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
/*      */       }
/* 2028 */       else if (i == this.runCount - 1) {
/* 2029 */         j = paramBoolean ? this.maxTabWidth : calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth;
/*      */       }
/*      */       else
/*      */       {
/* 2033 */         j = paramBoolean ? this.maxTabWidth : -this.maxTabWidth;
/*      */       }
/* 2035 */       break;
/*      */     case 4:
/* 2038 */       if (i == 0) {
/* 2039 */         j = paramBoolean ? this.maxTabWidth : calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth;
/*      */       }
/* 2042 */       else if (i == this.runCount - 1) {
/* 2043 */         j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
/*      */       }
/*      */       else
/*      */       {
/* 2047 */         j = paramBoolean ? this.maxTabWidth : -this.maxTabWidth;
/*      */       }
/* 2049 */       break;
/*      */     case 3:
/* 2052 */       if (i == 0) {
/* 2053 */         j = paramBoolean ? this.maxTabHeight : calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight;
/*      */       }
/* 2056 */       else if (i == this.runCount - 1) {
/* 2057 */         j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
/*      */       }
/*      */       else
/*      */       {
/* 2061 */         j = paramBoolean ? this.maxTabHeight : -this.maxTabHeight;
/*      */       }
/* 2063 */       break;
/*      */     case 1:
/*      */     default:
/* 2067 */       if (i == 0) {
/* 2068 */         j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
/*      */       }
/* 2071 */       else if (i == this.runCount - 1) {
/* 2072 */         j = paramBoolean ? this.maxTabHeight : calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight;
/*      */       }
/*      */       else
/*      */       {
/* 2076 */         j = paramBoolean ? this.maxTabHeight : -this.maxTabHeight;
/*      */       }
/*      */       break;
/*      */     }
/* 2080 */     return j;
/*      */   }
/*      */ 
/*      */   protected int getPreviousTabIndex(int paramInt) {
/* 2084 */     int i = paramInt - 1 >= 0 ? paramInt - 1 : this.tabPane.getTabCount() - 1;
/* 2085 */     return i >= 0 ? i : 0;
/*      */   }
/*      */ 
/*      */   protected int getNextTabIndex(int paramInt) {
/* 2089 */     return (paramInt + 1) % this.tabPane.getTabCount();
/*      */   }
/*      */ 
/*      */   protected int getNextTabIndexInRun(int paramInt1, int paramInt2) {
/* 2093 */     if (this.runCount < 2) {
/* 2094 */       return getNextTabIndex(paramInt2);
/*      */     }
/* 2096 */     int i = getRunForTab(paramInt1, paramInt2);
/* 2097 */     int j = getNextTabIndex(paramInt2);
/* 2098 */     if (j == this.tabRuns[getNextTabRun(i)]) {
/* 2099 */       return this.tabRuns[i];
/*      */     }
/* 2101 */     return j;
/*      */   }
/*      */ 
/*      */   protected int getPreviousTabIndexInRun(int paramInt1, int paramInt2) {
/* 2105 */     if (this.runCount < 2) {
/* 2106 */       return getPreviousTabIndex(paramInt2);
/*      */     }
/* 2108 */     int i = getRunForTab(paramInt1, paramInt2);
/* 2109 */     if (paramInt2 == this.tabRuns[i]) {
/* 2110 */       int j = this.tabRuns[getNextTabRun(i)] - 1;
/* 2111 */       return j != -1 ? j : paramInt1 - 1;
/*      */     }
/* 2113 */     return getPreviousTabIndex(paramInt2);
/*      */   }
/*      */ 
/*      */   protected int getPreviousTabRun(int paramInt) {
/* 2117 */     int i = paramInt - 1 >= 0 ? paramInt - 1 : this.runCount - 1;
/* 2118 */     return i >= 0 ? i : 0;
/*      */   }
/*      */ 
/*      */   protected int getNextTabRun(int paramInt) {
/* 2122 */     return (paramInt + 1) % this.runCount;
/*      */   }
/*      */ 
/*      */   protected static void rotateInsets(Insets paramInsets1, Insets paramInsets2, int paramInt)
/*      */   {
/* 2127 */     switch (paramInt) {
/*      */     case 2:
/* 2129 */       paramInsets2.top = paramInsets1.left;
/* 2130 */       paramInsets2.left = paramInsets1.top;
/* 2131 */       paramInsets2.bottom = paramInsets1.right;
/* 2132 */       paramInsets2.right = paramInsets1.bottom;
/* 2133 */       break;
/*      */     case 3:
/* 2135 */       paramInsets2.top = paramInsets1.bottom;
/* 2136 */       paramInsets2.left = paramInsets1.left;
/* 2137 */       paramInsets2.bottom = paramInsets1.top;
/* 2138 */       paramInsets2.right = paramInsets1.right;
/* 2139 */       break;
/*      */     case 4:
/* 2141 */       paramInsets2.top = paramInsets1.left;
/* 2142 */       paramInsets2.left = paramInsets1.bottom;
/* 2143 */       paramInsets2.bottom = paramInsets1.right;
/* 2144 */       paramInsets2.right = paramInsets1.top;
/* 2145 */       break;
/*      */     case 1:
/*      */     default:
/* 2148 */       paramInsets2.top = paramInsets1.top;
/* 2149 */       paramInsets2.left = paramInsets1.left;
/* 2150 */       paramInsets2.bottom = paramInsets1.bottom;
/* 2151 */       paramInsets2.right = paramInsets1.right;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean requestFocusForVisibleComponent()
/*      */   {
/* 2159 */     return SwingUtilities2.tabbedPaneChangeFocusTo(getVisibleComponent());
/*      */   }
/*      */ 
/*      */   private Vector<View> createHTMLVector()
/*      */   {
/* 3803 */     Vector localVector = new Vector();
/* 3804 */     int i = this.tabPane.getTabCount();
/* 3805 */     if (i > 0) {
/* 3806 */       for (int j = 0; j < i; j++) {
/* 3807 */         String str = this.tabPane.getTitleAt(j);
/* 3808 */         if (BasicHTML.isHTMLString(str))
/* 3809 */           localVector.addElement(BasicHTML.createHTMLView(this.tabPane, str));
/*      */         else {
/* 3811 */           localVector.addElement(null);
/*      */         }
/*      */       }
/*      */     }
/* 3815 */     return localVector;
/*      */   }
/*      */ 
/*      */   private static class Actions extends UIAction
/*      */   {
/*      */     static final String NEXT = "navigateNext";
/*      */     static final String PREVIOUS = "navigatePrevious";
/*      */     static final String RIGHT = "navigateRight";
/*      */     static final String LEFT = "navigateLeft";
/*      */     static final String UP = "navigateUp";
/*      */     static final String DOWN = "navigateDown";
/*      */     static final String PAGE_UP = "navigatePageUp";
/*      */     static final String PAGE_DOWN = "navigatePageDown";
/*      */     static final String REQUEST_FOCUS = "requestFocus";
/*      */     static final String REQUEST_FOCUS_FOR_VISIBLE = "requestFocusForVisibleComponent";
/*      */     static final String SET_SELECTED = "setSelectedIndex";
/*      */     static final String SELECT_FOCUSED = "selectTabWithFocus";
/*      */     static final String SCROLL_FORWARD = "scrollTabsForwardAction";
/*      */     static final String SCROLL_BACKWARD = "scrollTabsBackwardAction";
/*      */ 
/*      */     Actions(String paramString)
/*      */     {
/* 2180 */       super();
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2184 */       String str1 = getName();
/* 2185 */       JTabbedPane localJTabbedPane = (JTabbedPane)paramActionEvent.getSource();
/* 2186 */       BasicTabbedPaneUI localBasicTabbedPaneUI = (BasicTabbedPaneUI)BasicLookAndFeel.getUIOfType(localJTabbedPane.getUI(), BasicTabbedPaneUI.class);
/*      */ 
/* 2189 */       if (localBasicTabbedPaneUI == null) {
/* 2190 */         return;
/*      */       }
/* 2192 */       if (str1 == "navigateNext") {
/* 2193 */         localBasicTabbedPaneUI.navigateSelectedTab(12);
/*      */       }
/* 2195 */       else if (str1 == "navigatePrevious") {
/* 2196 */         localBasicTabbedPaneUI.navigateSelectedTab(13);
/*      */       }
/* 2198 */       else if (str1 == "navigateRight") {
/* 2199 */         localBasicTabbedPaneUI.navigateSelectedTab(3);
/*      */       }
/* 2201 */       else if (str1 == "navigateLeft") {
/* 2202 */         localBasicTabbedPaneUI.navigateSelectedTab(7);
/*      */       }
/* 2204 */       else if (str1 == "navigateUp") {
/* 2205 */         localBasicTabbedPaneUI.navigateSelectedTab(1);
/*      */       }
/* 2207 */       else if (str1 == "navigateDown") {
/* 2208 */         localBasicTabbedPaneUI.navigateSelectedTab(5);
/*      */       }
/*      */       else
/*      */       {
/*      */         int i;
/* 2210 */         if (str1 == "navigatePageUp") {
/* 2211 */           i = localJTabbedPane.getTabPlacement();
/* 2212 */           if ((i == 1) || (i == 3))
/* 2213 */             localBasicTabbedPaneUI.navigateSelectedTab(7);
/*      */           else {
/* 2215 */             localBasicTabbedPaneUI.navigateSelectedTab(1);
/*      */           }
/*      */         }
/* 2218 */         else if (str1 == "navigatePageDown") {
/* 2219 */           i = localJTabbedPane.getTabPlacement();
/* 2220 */           if ((i == 1) || (i == 3))
/* 2221 */             localBasicTabbedPaneUI.navigateSelectedTab(3);
/*      */           else {
/* 2223 */             localBasicTabbedPaneUI.navigateSelectedTab(5);
/*      */           }
/*      */         }
/* 2226 */         else if (str1 == "requestFocus") {
/* 2227 */           localJTabbedPane.requestFocus();
/*      */         }
/* 2229 */         else if (str1 == "requestFocusForVisibleComponent") {
/* 2230 */           localBasicTabbedPaneUI.requestFocusForVisibleComponent();
/*      */         }
/* 2232 */         else if (str1 == "setSelectedIndex") {
/* 2233 */           String str2 = paramActionEvent.getActionCommand();
/*      */ 
/* 2235 */           if ((str2 != null) && (str2.length() > 0)) {
/* 2236 */             int k = paramActionEvent.getActionCommand().charAt(0);
/* 2237 */             if ((k >= 97) && (k <= 122)) {
/* 2238 */               k -= 32;
/*      */             }
/* 2240 */             Integer localInteger = (Integer)localBasicTabbedPaneUI.mnemonicToIndexMap.get(Integer.valueOf(k));
/* 2241 */             if ((localInteger != null) && (localJTabbedPane.isEnabledAt(localInteger.intValue()))) {
/* 2242 */               localJTabbedPane.setSelectedIndex(localInteger.intValue());
/*      */             }
/*      */           }
/*      */         }
/* 2246 */         else if (str1 == "selectTabWithFocus") {
/* 2247 */           int j = localBasicTabbedPaneUI.getFocusIndex();
/* 2248 */           if (j != -1) {
/* 2249 */             localJTabbedPane.setSelectedIndex(j);
/*      */           }
/*      */         }
/* 2252 */         else if (str1 == "scrollTabsForwardAction") {
/* 2253 */           if (localBasicTabbedPaneUI.scrollableTabLayoutEnabled()) {
/* 2254 */             localBasicTabbedPaneUI.tabScroller.scrollForward(localJTabbedPane.getTabPlacement());
/*      */           }
/*      */         }
/* 2257 */         else if ((str1 == "scrollTabsBackwardAction") && 
/* 2258 */           (localBasicTabbedPaneUI.scrollableTabLayoutEnabled())) {
/* 2259 */           localBasicTabbedPaneUI.tabScroller.scrollBackward(localJTabbedPane.getTabPlacement());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CroppedEdge extends JPanel
/*      */     implements UIResource
/*      */   {
/*      */     private Shape shape;
/*      */     private int tabIndex;
/*      */     private int cropline;
/*      */     private int cropx;
/*      */     private int cropy;
/*      */ 
/*      */     public CroppedEdge()
/*      */     {
/* 3869 */       setOpaque(false);
/*      */     }
/*      */ 
/*      */     public void setParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 3873 */       this.tabIndex = paramInt1;
/* 3874 */       this.cropline = paramInt2;
/* 3875 */       this.cropx = paramInt3;
/* 3876 */       this.cropy = paramInt4;
/* 3877 */       Rectangle localRectangle = BasicTabbedPaneUI.this.rects[paramInt1];
/* 3878 */       setBounds(localRectangle);
/* 3879 */       this.shape = BasicTabbedPaneUI.createCroppedTabShape(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), localRectangle, paramInt2);
/* 3880 */       if ((getParent() == null) && (BasicTabbedPaneUI.this.tabContainer != null))
/* 3881 */         BasicTabbedPaneUI.this.tabContainer.add(this, 0);
/*      */     }
/*      */ 
/*      */     public void resetParams()
/*      */     {
/* 3886 */       this.shape = null;
/* 3887 */       if ((getParent() == BasicTabbedPaneUI.this.tabContainer) && (BasicTabbedPaneUI.this.tabContainer != null))
/* 3888 */         BasicTabbedPaneUI.this.tabContainer.remove(this);
/*      */     }
/*      */ 
/*      */     public boolean isParamsSet()
/*      */     {
/* 3893 */       return this.shape != null;
/*      */     }
/*      */ 
/*      */     public int getTabIndex() {
/* 3897 */       return this.tabIndex;
/*      */     }
/*      */ 
/*      */     public int getCropline() {
/* 3901 */       return this.cropline;
/*      */     }
/*      */ 
/*      */     public int getCroppedSideWidth() {
/* 3905 */       return 3;
/*      */     }
/*      */ 
/*      */     private Color getBgColor() {
/* 3909 */       Container localContainer = BasicTabbedPaneUI.this.tabPane.getParent();
/* 3910 */       if (localContainer != null) {
/* 3911 */         Color localColor = localContainer.getBackground();
/* 3912 */         if (localColor != null) {
/* 3913 */           return localColor;
/*      */         }
/*      */       }
/* 3916 */       return UIManager.getColor("control");
/*      */     }
/*      */ 
/*      */     protected void paintComponent(Graphics paramGraphics) {
/* 3920 */       super.paintComponent(paramGraphics);
/* 3921 */       if ((isParamsSet()) && ((paramGraphics instanceof Graphics2D))) {
/* 3922 */         Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
/* 3923 */         localGraphics2D.clipRect(0, 0, getWidth(), getHeight());
/* 3924 */         localGraphics2D.setColor(getBgColor());
/* 3925 */         localGraphics2D.translate(this.cropx, this.cropy);
/* 3926 */         localGraphics2D.fill(this.shape);
/* 3927 */         BasicTabbedPaneUI.this.paintCroppedTabEdge(paramGraphics);
/* 3928 */         localGraphics2D.translate(-this.cropx, -this.cropy);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public class FocusHandler extends FocusAdapter
/*      */   {
/*      */     public FocusHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/* 3795 */       BasicTabbedPaneUI.this.getHandler().focusGained(paramFocusEvent);
/*      */     }
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 3798 */       BasicTabbedPaneUI.this.getHandler().focusLost(paramFocusEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Handler
/*      */     implements ChangeListener, ContainerListener, FocusListener, MouseListener, MouseMotionListener, PropertyChangeListener
/*      */   {
/*      */     private Handler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 3514 */       JTabbedPane localJTabbedPane = (JTabbedPane)paramPropertyChangeEvent.getSource();
/* 3515 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 3516 */       boolean bool1 = BasicTabbedPaneUI.this.scrollableTabLayoutEnabled();
/* 3517 */       if (str == "mnemonicAt") {
/* 3518 */         BasicTabbedPaneUI.this.updateMnemonics();
/* 3519 */         localJTabbedPane.repaint();
/*      */       }
/* 3521 */       else if (str == "displayedMnemonicIndexAt") {
/* 3522 */         localJTabbedPane.repaint();
/*      */       }
/* 3524 */       else if (str == "indexForTitle") {
/* 3525 */         BasicTabbedPaneUI.this.calculatedBaseline = false;
/* 3526 */         Integer localInteger = (Integer)paramPropertyChangeEvent.getNewValue();
/*      */ 
/* 3529 */         if (BasicTabbedPaneUI.this.htmlViews != null) {
/* 3530 */           BasicTabbedPaneUI.this.htmlViews.removeElementAt(localInteger.intValue());
/*      */         }
/* 3532 */         updateHtmlViews(localInteger.intValue());
/* 3533 */       } else if (str == "tabLayoutPolicy") {
/* 3534 */         BasicTabbedPaneUI.this.uninstallUI(localJTabbedPane);
/* 3535 */         BasicTabbedPaneUI.this.installUI(localJTabbedPane);
/* 3536 */         BasicTabbedPaneUI.this.calculatedBaseline = false;
/* 3537 */       } else if (str == "tabPlacement") {
/* 3538 */         if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
/* 3539 */           BasicTabbedPaneUI.this.tabScroller.createButtons();
/*      */         }
/* 3541 */         BasicTabbedPaneUI.this.calculatedBaseline = false;
/* 3542 */       } else if ((str == "opaque") && (bool1)) {
/* 3543 */         boolean bool2 = ((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue();
/* 3544 */         BasicTabbedPaneUI.this.tabScroller.tabPanel.setOpaque(bool2);
/* 3545 */         BasicTabbedPaneUI.this.tabScroller.viewport.setOpaque(bool2);
/*      */       }
/*      */       else
/*      */       {
/*      */         Object localObject;
/* 3546 */         if ((str == "background") && (bool1)) {
/* 3547 */           localObject = (Color)paramPropertyChangeEvent.getNewValue();
/* 3548 */           BasicTabbedPaneUI.this.tabScroller.tabPanel.setBackground((Color)localObject);
/* 3549 */           BasicTabbedPaneUI.this.tabScroller.viewport.setBackground((Color)localObject);
/* 3550 */           Color localColor = BasicTabbedPaneUI.this.selectedColor == null ? localObject : BasicTabbedPaneUI.this.selectedColor;
/* 3551 */           BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.setBackground(localColor);
/* 3552 */           BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton.setBackground(localColor);
/* 3553 */         } else if (str == "indexForTabComponent") {
/* 3554 */           if (BasicTabbedPaneUI.this.tabContainer != null) {
/* 3555 */             BasicTabbedPaneUI.TabContainer.access$1700(BasicTabbedPaneUI.this.tabContainer);
/*      */           }
/* 3557 */           localObject = BasicTabbedPaneUI.this.tabPane.getTabComponentAt(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
/*      */ 
/* 3559 */           if (localObject != null) {
/* 3560 */             if (BasicTabbedPaneUI.this.tabContainer == null)
/* 3561 */               BasicTabbedPaneUI.this.installTabContainer();
/*      */             else {
/* 3563 */               BasicTabbedPaneUI.this.tabContainer.add((Component)localObject);
/*      */             }
/*      */           }
/* 3566 */           BasicTabbedPaneUI.this.tabPane.revalidate();
/* 3567 */           BasicTabbedPaneUI.this.tabPane.repaint();
/* 3568 */           BasicTabbedPaneUI.this.calculatedBaseline = false;
/* 3569 */         } else if (str == "indexForNullComponent") {
/* 3570 */           BasicTabbedPaneUI.this.isRunsDirty = true;
/* 3571 */           updateHtmlViews(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
/* 3572 */         } else if (str == "font") {
/* 3573 */           BasicTabbedPaneUI.this.calculatedBaseline = false;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3578 */     private void updateHtmlViews(int paramInt) { String str = BasicTabbedPaneUI.this.tabPane.getTitleAt(paramInt);
/* 3579 */       boolean bool = BasicHTML.isHTMLString(str);
/* 3580 */       if (bool) {
/* 3581 */         if (BasicTabbedPaneUI.this.htmlViews == null) {
/* 3582 */           BasicTabbedPaneUI.this.htmlViews = BasicTabbedPaneUI.this.createHTMLVector();
/*      */         } else {
/* 3584 */           View localView = BasicHTML.createHTMLView(BasicTabbedPaneUI.this.tabPane, str);
/* 3585 */           BasicTabbedPaneUI.this.htmlViews.insertElementAt(localView, paramInt);
/*      */         }
/*      */       }
/* 3588 */       else if (BasicTabbedPaneUI.this.htmlViews != null) {
/* 3589 */         BasicTabbedPaneUI.this.htmlViews.insertElementAt(null, paramInt);
/*      */       }
/*      */ 
/* 3592 */       BasicTabbedPaneUI.this.updateMnemonics();
/*      */     }
/*      */ 
/*      */     public void stateChanged(ChangeEvent paramChangeEvent)
/*      */     {
/* 3599 */       JTabbedPane localJTabbedPane = (JTabbedPane)paramChangeEvent.getSource();
/* 3600 */       localJTabbedPane.revalidate();
/* 3601 */       localJTabbedPane.repaint();
/*      */ 
/* 3603 */       BasicTabbedPaneUI.this.setFocusIndex(localJTabbedPane.getSelectedIndex(), false);
/*      */ 
/* 3605 */       if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
/* 3606 */         int i = localJTabbedPane.getSelectedIndex();
/* 3607 */         if ((i < BasicTabbedPaneUI.this.rects.length) && (i != -1))
/* 3608 */           BasicTabbedPaneUI.this.tabScroller.tabPanel.scrollRectToVisible((Rectangle)BasicTabbedPaneUI.this.rects[i].clone());
/*      */       }
/*      */     }
/*      */ 
/*      */     public void mouseClicked(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseReleased(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseEntered(MouseEvent paramMouseEvent)
/*      */     {
/* 3624 */       BasicTabbedPaneUI.this.setRolloverTab(paramMouseEvent.getX(), paramMouseEvent.getY());
/*      */     }
/*      */ 
/*      */     public void mouseExited(MouseEvent paramMouseEvent) {
/* 3628 */       BasicTabbedPaneUI.this.setRolloverTab(-1);
/*      */     }
/*      */ 
/*      */     public void mousePressed(MouseEvent paramMouseEvent) {
/* 3632 */       if (!BasicTabbedPaneUI.this.tabPane.isEnabled()) {
/* 3633 */         return;
/*      */       }
/* 3635 */       int i = BasicTabbedPaneUI.this.tabForCoordinate(BasicTabbedPaneUI.this.tabPane, paramMouseEvent.getX(), paramMouseEvent.getY());
/* 3636 */       if ((i >= 0) && (BasicTabbedPaneUI.this.tabPane.isEnabledAt(i)))
/* 3637 */         if (i != BasicTabbedPaneUI.this.tabPane.getSelectedIndex())
/*      */         {
/* 3642 */           BasicTabbedPaneUI.this.tabPane.setSelectedIndex(i);
/*      */         }
/* 3644 */         else if (BasicTabbedPaneUI.this.tabPane.isRequestFocusEnabled())
/*      */         {
/* 3647 */           BasicTabbedPaneUI.this.tabPane.requestFocus();
/*      */         }
/*      */     }
/*      */ 
/*      */     public void mouseDragged(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseMoved(MouseEvent paramMouseEvent)
/*      */     {
/* 3659 */       BasicTabbedPaneUI.this.setRolloverTab(paramMouseEvent.getX(), paramMouseEvent.getY());
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/* 3666 */       BasicTabbedPaneUI.this.setFocusIndex(BasicTabbedPaneUI.this.tabPane.getSelectedIndex(), true);
/*      */     }
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 3669 */       BasicTabbedPaneUI.this.repaintTab(BasicTabbedPaneUI.this.focusIndex);
/*      */     }
/*      */ 
/*      */     public void componentAdded(ContainerEvent paramContainerEvent)
/*      */     {
/* 3707 */       JTabbedPane localJTabbedPane = (JTabbedPane)paramContainerEvent.getContainer();
/* 3708 */       Component localComponent = paramContainerEvent.getChild();
/* 3709 */       if ((localComponent instanceof UIResource)) {
/* 3710 */         return;
/*      */       }
/* 3712 */       BasicTabbedPaneUI.this.isRunsDirty = true;
/* 3713 */       updateHtmlViews(localJTabbedPane.indexOfComponent(localComponent));
/*      */     }
/*      */     public void componentRemoved(ContainerEvent paramContainerEvent) {
/* 3716 */       JTabbedPane localJTabbedPane = (JTabbedPane)paramContainerEvent.getContainer();
/* 3717 */       Component localComponent = paramContainerEvent.getChild();
/* 3718 */       if ((localComponent instanceof UIResource)) {
/* 3719 */         return;
/*      */       }
/*      */ 
/* 3727 */       Integer localInteger = (Integer)localJTabbedPane.getClientProperty("__index_to_remove__");
/*      */ 
/* 3729 */       if (localInteger != null) {
/* 3730 */         int i = localInteger.intValue();
/* 3731 */         if ((BasicTabbedPaneUI.this.htmlViews != null) && (BasicTabbedPaneUI.this.htmlViews.size() > i)) {
/* 3732 */           BasicTabbedPaneUI.this.htmlViews.removeElementAt(i);
/*      */         }
/* 3734 */         localJTabbedPane.putClientProperty("__index_to_remove__", null);
/*      */       }
/* 3736 */       BasicTabbedPaneUI.this.isRunsDirty = true;
/* 3737 */       BasicTabbedPaneUI.this.updateMnemonics();
/*      */ 
/* 3739 */       BasicTabbedPaneUI.this.validateFocusIndex();
/*      */     }
/*      */   }
/*      */ 
/*      */   public class MouseHandler extends MouseAdapter
/*      */   {
/*      */     public MouseHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mousePressed(MouseEvent paramMouseEvent)
/*      */     {
/* 3781 */       BasicTabbedPaneUI.this.getHandler().mousePressed(paramMouseEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class PropertyChangeHandler
/*      */     implements PropertyChangeListener
/*      */   {
/*      */     public PropertyChangeHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 3753 */       BasicTabbedPaneUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ScrollableTabButton extends BasicArrowButton
/*      */     implements UIResource, SwingConstants
/*      */   {
/*      */     public ScrollableTabButton(int arg2)
/*      */     {
/* 3496 */       super(UIManager.getColor("TabbedPane.selected"), UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"), UIManager.getColor("TabbedPane.highlight"));
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ScrollableTabPanel extends JPanel
/*      */     implements UIResource
/*      */   {
/*      */     public ScrollableTabPanel()
/*      */     {
/* 3465 */       super();
/* 3466 */       setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
/* 3467 */       Color localColor = UIManager.getColor("TabbedPane.tabAreaBackground");
/* 3468 */       if (localColor == null) {
/* 3469 */         localColor = BasicTabbedPaneUI.this.tabPane.getBackground();
/*      */       }
/* 3471 */       setBackground(localColor);
/*      */     }
/*      */     public void paintComponent(Graphics paramGraphics) {
/* 3474 */       super.paintComponent(paramGraphics);
/* 3475 */       BasicTabbedPaneUI.this.paintTabArea(paramGraphics, BasicTabbedPaneUI.this.tabPane.getTabPlacement(), BasicTabbedPaneUI.this.tabPane.getSelectedIndex());
/*      */ 
/* 3477 */       if ((BasicTabbedPaneUI.this.tabScroller.croppedEdge.isParamsSet()) && (BasicTabbedPaneUI.this.tabContainer == null)) {
/* 3478 */         Rectangle localRectangle = BasicTabbedPaneUI.this.rects[BasicTabbedPaneUI.this.tabScroller.croppedEdge.getTabIndex()];
/* 3479 */         paramGraphics.translate(localRectangle.x, localRectangle.y);
/* 3480 */         BasicTabbedPaneUI.this.tabScroller.croppedEdge.paintComponent(paramGraphics);
/* 3481 */         paramGraphics.translate(-localRectangle.x, -localRectangle.y);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void doLayout() {
/* 3486 */       if (getComponentCount() > 0) {
/* 3487 */         Component localComponent = getComponent(0);
/* 3488 */         localComponent.setBounds(0, 0, getWidth(), getHeight());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ScrollableTabSupport
/*      */     implements ActionListener, ChangeListener
/*      */   {
/*      */     public BasicTabbedPaneUI.ScrollableTabViewport viewport;
/*      */     public BasicTabbedPaneUI.ScrollableTabPanel tabPanel;
/*      */     public JButton scrollForwardButton;
/*      */     public JButton scrollBackwardButton;
/*      */     public BasicTabbedPaneUI.CroppedEdge croppedEdge;
/*      */     public int leadingTabIndex;
/* 3252 */     private Point tabViewPosition = new Point(0, 0);
/*      */ 
/*      */     ScrollableTabSupport(int arg2) {
/* 3255 */       this.viewport = new BasicTabbedPaneUI.ScrollableTabViewport(BasicTabbedPaneUI.this);
/* 3256 */       this.tabPanel = new BasicTabbedPaneUI.ScrollableTabPanel(BasicTabbedPaneUI.this);
/* 3257 */       this.viewport.setView(this.tabPanel);
/* 3258 */       this.viewport.addChangeListener(this);
/* 3259 */       this.croppedEdge = new BasicTabbedPaneUI.CroppedEdge(BasicTabbedPaneUI.this);
/* 3260 */       createButtons();
/*      */     }
/*      */ 
/*      */     void createButtons()
/*      */     {
/* 3267 */       if (this.scrollForwardButton != null) {
/* 3268 */         BasicTabbedPaneUI.this.tabPane.remove(this.scrollForwardButton);
/* 3269 */         this.scrollForwardButton.removeActionListener(this);
/* 3270 */         BasicTabbedPaneUI.this.tabPane.remove(this.scrollBackwardButton);
/* 3271 */         this.scrollBackwardButton.removeActionListener(this);
/*      */       }
/* 3273 */       int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
/* 3274 */       if ((i == 1) || (i == 3)) {
/* 3275 */         this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(3);
/* 3276 */         this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(7);
/*      */       }
/*      */       else {
/* 3279 */         this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(5);
/* 3280 */         this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(1);
/*      */       }
/* 3282 */       this.scrollForwardButton.addActionListener(this);
/* 3283 */       this.scrollBackwardButton.addActionListener(this);
/* 3284 */       BasicTabbedPaneUI.this.tabPane.add(this.scrollForwardButton);
/* 3285 */       BasicTabbedPaneUI.this.tabPane.add(this.scrollBackwardButton);
/*      */     }
/*      */ 
/*      */     public void scrollForward(int paramInt) {
/* 3289 */       Dimension localDimension = this.viewport.getViewSize();
/* 3290 */       Rectangle localRectangle = this.viewport.getViewRect();
/*      */ 
/* 3292 */       if ((paramInt == 1) || (paramInt == 3))
/*      */       {
/* 3293 */         if (localRectangle.width < localDimension.width - localRectangle.x);
/*      */       }
/* 3297 */       else if (localRectangle.height >= localDimension.height - localRectangle.y) {
/* 3298 */         return;
/*      */       }
/*      */ 
/* 3301 */       setLeadingTabIndex(paramInt, this.leadingTabIndex + 1);
/*      */     }
/*      */ 
/*      */     public void scrollBackward(int paramInt) {
/* 3305 */       if (this.leadingTabIndex == 0) {
/* 3306 */         return;
/*      */       }
/* 3308 */       setLeadingTabIndex(paramInt, this.leadingTabIndex - 1);
/*      */     }
/*      */ 
/*      */     public void setLeadingTabIndex(int paramInt1, int paramInt2) {
/* 3312 */       this.leadingTabIndex = paramInt2;
/* 3313 */       Dimension localDimension1 = this.viewport.getViewSize();
/* 3314 */       Rectangle localRectangle = this.viewport.getViewRect();
/*      */       Dimension localDimension2;
/* 3316 */       switch (paramInt1) {
/*      */       case 1:
/*      */       case 3:
/* 3319 */         this.tabViewPosition.x = (this.leadingTabIndex == 0 ? 0 : BasicTabbedPaneUI.this.rects[this.leadingTabIndex].x);
/*      */ 
/* 3321 */         if (localDimension1.width - this.tabViewPosition.x < localRectangle.width)
/*      */         {
/* 3324 */           localDimension2 = new Dimension(localDimension1.width - this.tabViewPosition.x, localRectangle.height);
/*      */ 
/* 3326 */           this.viewport.setExtentSize(localDimension2);
/* 3327 */         }break;
/*      */       case 2:
/*      */       case 4:
/* 3331 */         this.tabViewPosition.y = (this.leadingTabIndex == 0 ? 0 : BasicTabbedPaneUI.this.rects[this.leadingTabIndex].y);
/*      */ 
/* 3333 */         if (localDimension1.height - this.tabViewPosition.y < localRectangle.height)
/*      */         {
/* 3336 */           localDimension2 = new Dimension(localRectangle.width, localDimension1.height - this.tabViewPosition.y);
/*      */ 
/* 3338 */           this.viewport.setExtentSize(localDimension2);
/*      */         }break;
/*      */       }
/* 3341 */       this.viewport.setViewPosition(this.tabViewPosition);
/*      */     }
/*      */ 
/*      */     public void stateChanged(ChangeEvent paramChangeEvent) {
/* 3345 */       updateView();
/*      */     }
/*      */ 
/*      */     private void updateView() {
/* 3349 */       int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
/* 3350 */       int j = BasicTabbedPaneUI.this.tabPane.getTabCount();
/* 3351 */       Rectangle localRectangle1 = this.viewport.getBounds();
/* 3352 */       Dimension localDimension = this.viewport.getViewSize();
/* 3353 */       Rectangle localRectangle2 = this.viewport.getViewRect();
/*      */ 
/* 3355 */       this.leadingTabIndex = BasicTabbedPaneUI.this.getClosestTab(localRectangle2.x, localRectangle2.y);
/*      */ 
/* 3358 */       if (this.leadingTabIndex + 1 < j) {
/* 3359 */         switch (i) {
/*      */         case 1:
/*      */         case 3:
/* 3362 */           if (BasicTabbedPaneUI.this.rects[this.leadingTabIndex].x < localRectangle2.x)
/* 3363 */             this.leadingTabIndex += 1; break;
/*      */         case 2:
/*      */         case 4:
/* 3368 */           if (BasicTabbedPaneUI.this.rects[this.leadingTabIndex].y < localRectangle2.y) {
/* 3369 */             this.leadingTabIndex += 1;
/*      */           }
/*      */           break;
/*      */         }
/*      */       }
/* 3374 */       Insets localInsets = BasicTabbedPaneUI.this.getContentBorderInsets(i);
/* 3375 */       switch (i) {
/*      */       case 2:
/* 3377 */         BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x + localRectangle1.width, localRectangle1.y, localInsets.left, localRectangle1.height);
/*      */ 
/* 3379 */         this.scrollBackwardButton.setEnabled((localRectangle2.y > 0) && (this.leadingTabIndex > 0));
/*      */ 
/* 3381 */         this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.height - localRectangle2.y > localRectangle2.height));
/*      */ 
/* 3384 */         break;
/*      */       case 4:
/* 3386 */         BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x - localInsets.right, localRectangle1.y, localInsets.right, localRectangle1.height);
/*      */ 
/* 3388 */         this.scrollBackwardButton.setEnabled((localRectangle2.y > 0) && (this.leadingTabIndex > 0));
/*      */ 
/* 3390 */         this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.height - localRectangle2.y > localRectangle2.height));
/*      */ 
/* 3393 */         break;
/*      */       case 3:
/* 3395 */         BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x, localRectangle1.y - localInsets.bottom, localRectangle1.width, localInsets.bottom);
/*      */ 
/* 3397 */         this.scrollBackwardButton.setEnabled((localRectangle2.x > 0) && (this.leadingTabIndex > 0));
/*      */ 
/* 3399 */         this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.width - localRectangle2.x > localRectangle2.width));
/*      */ 
/* 3402 */         break;
/*      */       case 1:
/*      */       default:
/* 3405 */         BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x, localRectangle1.y + localRectangle1.height, localRectangle1.width, localInsets.top);
/*      */ 
/* 3407 */         this.scrollBackwardButton.setEnabled((localRectangle2.x > 0) && (this.leadingTabIndex > 0));
/*      */ 
/* 3409 */         this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.width - localRectangle2.x > localRectangle2.width));
/*      */       }
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 3419 */       ActionMap localActionMap = BasicTabbedPaneUI.this.tabPane.getActionMap();
/*      */ 
/* 3421 */       if (localActionMap != null)
/*      */       {
/*      */         String str;
/* 3424 */         if (paramActionEvent.getSource() == this.scrollForwardButton) {
/* 3425 */           str = "scrollTabsForwardAction";
/*      */         }
/*      */         else {
/* 3428 */           str = "scrollTabsBackwardAction";
/*      */         }
/* 3430 */         Action localAction = localActionMap.get(str);
/*      */ 
/* 3432 */         if ((localAction != null) && (localAction.isEnabled()))
/* 3433 */           localAction.actionPerformed(new ActionEvent(BasicTabbedPaneUI.this.tabPane, 1001, null, paramActionEvent.getWhen(), paramActionEvent.getModifiers()));
/*      */       }
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 3441 */       return "viewport.viewSize=" + this.viewport.getViewSize() + "\n" + "viewport.viewRectangle=" + this.viewport.getViewRect() + "\n" + "leadingTabIndex=" + this.leadingTabIndex + "\n" + "tabViewPosition=" + this.tabViewPosition;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ScrollableTabViewport extends JViewport
/*      */     implements UIResource
/*      */   {
/*      */     public ScrollableTabViewport()
/*      */     {
/* 3452 */       setName("TabbedPane.scrollableViewport");
/* 3453 */       setScrollMode(0);
/* 3454 */       setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
/* 3455 */       Color localColor = UIManager.getColor("TabbedPane.tabAreaBackground");
/* 3456 */       if (localColor == null) {
/* 3457 */         localColor = BasicTabbedPaneUI.this.tabPane.getBackground();
/*      */       }
/* 3459 */       setBackground(localColor);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TabContainer extends JPanel
/*      */     implements UIResource
/*      */   {
/* 3819 */     private boolean notifyTabbedPane = true;
/*      */ 
/*      */     public TabContainer() {
/* 3822 */       super();
/* 3823 */       setOpaque(false);
/*      */     }
/*      */ 
/*      */     public void remove(Component paramComponent) {
/* 3827 */       int i = BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(paramComponent);
/* 3828 */       super.remove(paramComponent);
/* 3829 */       if ((this.notifyTabbedPane) && (i != -1))
/* 3830 */         BasicTabbedPaneUI.this.tabPane.setTabComponentAt(i, null);
/*      */     }
/*      */ 
/*      */     private void removeUnusedTabComponents()
/*      */     {
/* 3835 */       for (Component localComponent : getComponents())
/* 3836 */         if (!(localComponent instanceof UIResource)) {
/* 3837 */           int k = BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(localComponent);
/* 3838 */           if (k == -1)
/* 3839 */             super.remove(localComponent);
/*      */         }
/*      */     }
/*      */ 
/*      */     public boolean isOptimizedDrawingEnabled()
/*      */     {
/* 3846 */       return (BasicTabbedPaneUI.this.tabScroller != null) && (!BasicTabbedPaneUI.this.tabScroller.croppedEdge.isParamsSet());
/*      */     }
/*      */ 
/*      */     public void doLayout()
/*      */     {
/* 3853 */       if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
/* 3854 */         BasicTabbedPaneUI.this.tabScroller.tabPanel.repaint();
/* 3855 */         BasicTabbedPaneUI.this.tabScroller.updateView();
/*      */       } else {
/* 3857 */         BasicTabbedPaneUI.this.tabPane.repaint(getBounds());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TabSelectionHandler
/*      */     implements ChangeListener
/*      */   {
/*      */     public TabSelectionHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void stateChanged(ChangeEvent paramChangeEvent)
/*      */     {
/* 3767 */       BasicTabbedPaneUI.this.getHandler().stateChanged(paramChangeEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TabbedPaneLayout
/*      */     implements LayoutManager
/*      */   {
/*      */     public TabbedPaneLayout()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addLayoutComponent(String paramString, Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void removeLayoutComponent(Component paramComponent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Dimension preferredLayoutSize(Container paramContainer)
/*      */     {
/* 2276 */       return calculateSize(false);
/*      */     }
/*      */ 
/*      */     public Dimension minimumLayoutSize(Container paramContainer) {
/* 2280 */       return calculateSize(true);
/*      */     }
/*      */ 
/*      */     protected Dimension calculateSize(boolean paramBoolean) {
/* 2284 */       int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
/* 2285 */       Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
/* 2286 */       Insets localInsets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
/* 2287 */       Insets localInsets3 = BasicTabbedPaneUI.this.getTabAreaInsets(i);
/*      */ 
/* 2289 */       Dimension localDimension1 = new Dimension(0, 0);
/* 2290 */       int j = 0;
/* 2291 */       int k = 0;
/* 2292 */       int m = 0;
/* 2293 */       int n = 0;
/*      */ 
/* 2298 */       for (int i1 = 0; i1 < BasicTabbedPaneUI.this.tabPane.getTabCount(); i1++) {
/* 2299 */         Component localComponent = BasicTabbedPaneUI.this.tabPane.getComponentAt(i1);
/* 2300 */         if (localComponent != null) {
/* 2301 */           Dimension localDimension2 = paramBoolean ? localComponent.getMinimumSize() : localComponent.getPreferredSize();
/*      */ 
/* 2304 */           if (localDimension2 != null) {
/* 2305 */             n = Math.max(localDimension2.height, n);
/* 2306 */             m = Math.max(localDimension2.width, m);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2311 */       k += m;
/* 2312 */       j += n;
/*      */ 
/* 2318 */       switch (i) {
/*      */       case 2:
/*      */       case 4:
/* 2321 */         j = Math.max(j, BasicTabbedPaneUI.this.calculateMaxTabHeight(i));
/* 2322 */         i1 = preferredTabAreaWidth(i, j - localInsets3.top - localInsets3.bottom);
/* 2323 */         k += i1;
/* 2324 */         break;
/*      */       case 1:
/*      */       case 3:
/*      */       default:
/* 2328 */         k = Math.max(k, BasicTabbedPaneUI.this.calculateMaxTabWidth(i));
/* 2329 */         i1 = preferredTabAreaHeight(i, k - localInsets3.left - localInsets3.right);
/* 2330 */         j += i1;
/*      */       }
/* 2332 */       return new Dimension(k + localInsets1.left + localInsets1.right + localInsets2.left + localInsets2.right, j + localInsets1.bottom + localInsets1.top + localInsets2.top + localInsets2.bottom);
/*      */     }
/*      */ 
/*      */     protected int preferredTabAreaHeight(int paramInt1, int paramInt2)
/*      */     {
/* 2338 */       FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
/* 2339 */       int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
/* 2340 */       int j = 0;
/* 2341 */       if (i > 0) {
/* 2342 */         int k = 1;
/* 2343 */         int m = 0;
/*      */ 
/* 2345 */         int n = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
/*      */ 
/* 2347 */         for (int i1 = 0; i1 < i; i1++) {
/* 2348 */           int i2 = BasicTabbedPaneUI.this.calculateTabWidth(paramInt1, i1, localFontMetrics);
/*      */ 
/* 2350 */           if ((m != 0) && (m + i2 > paramInt2)) {
/* 2351 */             k++;
/* 2352 */             m = 0;
/*      */           }
/* 2354 */           m += i2;
/*      */         }
/* 2356 */         j = BasicTabbedPaneUI.this.calculateTabAreaHeight(paramInt1, k, n);
/*      */       }
/* 2358 */       return j;
/*      */     }
/*      */ 
/*      */     protected int preferredTabAreaWidth(int paramInt1, int paramInt2) {
/* 2362 */       FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
/* 2363 */       int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
/* 2364 */       int j = 0;
/* 2365 */       if (i > 0) {
/* 2366 */         int k = 1;
/* 2367 */         int m = 0;
/* 2368 */         int n = localFontMetrics.getHeight();
/*      */ 
/* 2370 */         BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
/*      */ 
/* 2372 */         for (int i1 = 0; i1 < i; i1++) {
/* 2373 */           int i2 = BasicTabbedPaneUI.this.calculateTabHeight(paramInt1, i1, n);
/*      */ 
/* 2375 */           if ((m != 0) && (m + i2 > paramInt2)) {
/* 2376 */             k++;
/* 2377 */             m = 0;
/*      */           }
/* 2379 */           m += i2;
/*      */         }
/* 2381 */         j = BasicTabbedPaneUI.this.calculateTabAreaWidth(paramInt1, k, BasicTabbedPaneUI.this.maxTabWidth);
/*      */       }
/* 2383 */       return j;
/*      */     }
/*      */ 
/*      */     public void layoutContainer(Container paramContainer)
/*      */     {
/* 2398 */       BasicTabbedPaneUI.this.setRolloverTab(-1);
/*      */ 
/* 2400 */       int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
/* 2401 */       Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
/* 2402 */       int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
/* 2403 */       Component localComponent1 = BasicTabbedPaneUI.this.getVisibleComponent();
/*      */ 
/* 2405 */       calculateLayoutInfo();
/*      */ 
/* 2407 */       Component localComponent2 = null;
/* 2408 */       if (j < 0) {
/* 2409 */         if (localComponent1 != null)
/*      */         {
/* 2411 */           BasicTabbedPaneUI.this.setVisibleComponent(null);
/*      */         }
/*      */       }
/* 2414 */       else localComponent2 = BasicTabbedPaneUI.this.tabPane.getComponentAt(j);
/*      */ 
/* 2417 */       int i2 = 0;
/* 2418 */       int i3 = 0;
/* 2419 */       Insets localInsets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
/*      */ 
/* 2421 */       int i4 = 0;
/*      */ 
/* 2430 */       if (localComponent2 != null) {
/* 2431 */         if ((localComponent2 != localComponent1) && (localComponent1 != null))
/*      */         {
/* 2433 */           if (SwingUtilities.findFocusOwner(localComponent1) != null) {
/* 2434 */             i4 = 1;
/*      */           }
/*      */         }
/* 2437 */         BasicTabbedPaneUI.this.setVisibleComponent(localComponent2);
/*      */       }
/*      */ 
/* 2440 */       Rectangle localRectangle = BasicTabbedPaneUI.this.tabPane.getBounds();
/* 2441 */       int i5 = BasicTabbedPaneUI.this.tabPane.getComponentCount();
/*      */ 
/* 2443 */       if (i5 > 0)
/*      */       {
/*      */         int k;
/*      */         int m;
/* 2445 */         switch (i) {
/*      */         case 2:
/* 2447 */           i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
/* 2448 */           k = localInsets1.left + i2 + localInsets2.left;
/* 2449 */           m = localInsets1.top + localInsets2.top;
/* 2450 */           break;
/*      */         case 4:
/* 2452 */           i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
/* 2453 */           k = localInsets1.left + localInsets2.left;
/* 2454 */           m = localInsets1.top + localInsets2.top;
/* 2455 */           break;
/*      */         case 3:
/* 2457 */           i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
/* 2458 */           k = localInsets1.left + localInsets2.left;
/* 2459 */           m = localInsets1.top + localInsets2.top;
/* 2460 */           break;
/*      */         case 1:
/*      */         default:
/* 2463 */           i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
/* 2464 */           k = localInsets1.left + localInsets2.left;
/* 2465 */           m = localInsets1.top + i3 + localInsets2.top;
/*      */         }
/*      */ 
/* 2468 */         int n = localRectangle.width - i2 - localInsets1.left - localInsets1.right - localInsets2.left - localInsets2.right;
/*      */ 
/* 2471 */         int i1 = localRectangle.height - i3 - localInsets1.top - localInsets1.bottom - localInsets2.top - localInsets2.bottom;
/*      */ 
/* 2475 */         for (int i6 = 0; i6 < i5; i6++) {
/* 2476 */           Component localComponent3 = BasicTabbedPaneUI.this.tabPane.getComponent(i6);
/* 2477 */           if (localComponent3 == BasicTabbedPaneUI.this.tabContainer)
/*      */           {
/* 2479 */             int i7 = i2 == 0 ? localRectangle.width : i2 + localInsets1.left + localInsets1.right + localInsets2.left + localInsets2.right;
/*      */ 
/* 2482 */             int i8 = i3 == 0 ? localRectangle.height : i3 + localInsets1.top + localInsets1.bottom + localInsets2.top + localInsets2.bottom;
/*      */ 
/* 2486 */             int i9 = 0;
/* 2487 */             int i10 = 0;
/* 2488 */             if (i == 3)
/* 2489 */               i10 = localRectangle.height - i8;
/* 2490 */             else if (i == 4) {
/* 2491 */               i9 = localRectangle.width - i7;
/*      */             }
/* 2493 */             localComponent3.setBounds(i9, i10, i7, i8);
/*      */           } else {
/* 2495 */             localComponent3.setBounds(k, m, n, i1);
/*      */           }
/*      */         }
/*      */       }
/* 2499 */       layoutTabComponents();
/* 2500 */       if ((i4 != 0) && 
/* 2501 */         (!BasicTabbedPaneUI.this.requestFocusForVisibleComponent()))
/* 2502 */         BasicTabbedPaneUI.this.tabPane.requestFocus();
/*      */     }
/*      */ 
/*      */     public void calculateLayoutInfo()
/*      */     {
/* 2508 */       int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
/* 2509 */       BasicTabbedPaneUI.this.assureRectsCreated(i);
/* 2510 */       calculateTabRects(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), i);
/* 2511 */       BasicTabbedPaneUI.this.isRunsDirty = false;
/*      */     }
/*      */ 
/*      */     private void layoutTabComponents() {
/* 2515 */       if (BasicTabbedPaneUI.this.tabContainer == null) {
/* 2516 */         return;
/*      */       }
/* 2518 */       Rectangle localRectangle = new Rectangle();
/* 2519 */       Point localPoint = new Point(-BasicTabbedPaneUI.this.tabContainer.getX(), -BasicTabbedPaneUI.this.tabContainer.getY());
/* 2520 */       if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
/* 2521 */         BasicTabbedPaneUI.this.translatePointToTabPanel(0, 0, localPoint);
/*      */       }
/* 2523 */       for (int i = 0; i < BasicTabbedPaneUI.this.tabPane.getTabCount(); i++) {
/* 2524 */         Component localComponent = BasicTabbedPaneUI.this.tabPane.getTabComponentAt(i);
/* 2525 */         if (localComponent != null)
/*      */         {
/* 2528 */           BasicTabbedPaneUI.this.getTabBounds(i, localRectangle);
/* 2529 */           Dimension localDimension = localComponent.getPreferredSize();
/* 2530 */           Insets localInsets = BasicTabbedPaneUI.this.getTabInsets(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), i);
/* 2531 */           int j = localRectangle.x + localInsets.left + localPoint.x;
/* 2532 */           int k = localRectangle.y + localInsets.top + localPoint.y;
/* 2533 */           int m = localRectangle.width - localInsets.left - localInsets.right;
/* 2534 */           int n = localRectangle.height - localInsets.top - localInsets.bottom;
/*      */ 
/* 2536 */           int i1 = j + (m - localDimension.width) / 2;
/* 2537 */           int i2 = k + (n - localDimension.height) / 2;
/* 2538 */           int i3 = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
/* 2539 */           boolean bool = i == BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
/* 2540 */           localComponent.setBounds(i1 + BasicTabbedPaneUI.this.getTabLabelShiftX(i3, i, bool), i2 + BasicTabbedPaneUI.this.getTabLabelShiftY(i3, i, bool), localDimension.width, localDimension.height);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void calculateTabRects(int paramInt1, int paramInt2)
/*      */     {
/* 2547 */       FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
/* 2548 */       Dimension localDimension = BasicTabbedPaneUI.this.tabPane.getSize();
/* 2549 */       Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
/* 2550 */       Insets localInsets2 = BasicTabbedPaneUI.this.getTabAreaInsets(paramInt1);
/* 2551 */       int i = localFontMetrics.getHeight();
/* 2552 */       int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
/*      */ 
/* 2557 */       int i4 = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
/* 2558 */       boolean bool = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
/*      */       int i1;
/*      */       int i2;
/*      */       int i3;
/* 2563 */       switch (paramInt1) {
/*      */       case 2:
/* 2565 */         BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
/* 2566 */         i1 = localInsets1.left + localInsets2.left;
/* 2567 */         i2 = localInsets1.top + localInsets2.top;
/* 2568 */         i3 = localDimension.height - (localInsets1.bottom + localInsets2.bottom);
/* 2569 */         break;
/*      */       case 4:
/* 2571 */         BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
/* 2572 */         i1 = localDimension.width - localInsets1.right - localInsets2.right - BasicTabbedPaneUI.this.maxTabWidth;
/* 2573 */         i2 = localInsets1.top + localInsets2.top;
/* 2574 */         i3 = localDimension.height - (localInsets1.bottom + localInsets2.bottom);
/* 2575 */         break;
/*      */       case 3:
/* 2577 */         BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
/* 2578 */         i1 = localInsets1.left + localInsets2.left;
/* 2579 */         i2 = localDimension.height - localInsets1.bottom - localInsets2.bottom - BasicTabbedPaneUI.this.maxTabHeight;
/* 2580 */         i3 = localDimension.width - (localInsets1.right + localInsets2.right);
/* 2581 */         break;
/*      */       case 1:
/*      */       default:
/* 2584 */         BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
/* 2585 */         i1 = localInsets1.left + localInsets2.left;
/* 2586 */         i2 = localInsets1.top + localInsets2.top;
/* 2587 */         i3 = localDimension.width - (localInsets1.right + localInsets2.right);
/*      */       }
/*      */ 
/* 2591 */       int k = BasicTabbedPaneUI.this.getTabRunOverlay(paramInt1);
/*      */ 
/* 2593 */       BasicTabbedPaneUI.this.runCount = 0;
/* 2594 */       BasicTabbedPaneUI.this.selectedRun = -1;
/*      */ 
/* 2596 */       if (paramInt2 == 0)
/*      */         return;
/*      */       Rectangle localRectangle;
/* 2602 */       for (int m = 0; m < paramInt2; m++) {
/* 2603 */         localRectangle = BasicTabbedPaneUI.this.rects[m];
/*      */ 
/* 2605 */         if (i4 == 0)
/*      */         {
/* 2607 */           if (m > 0) {
/* 2608 */             localRectangle.x = (BasicTabbedPaneUI.this.rects[(m - 1)].x + BasicTabbedPaneUI.this.rects[(m - 1)].width);
/*      */           } else {
/* 2610 */             BasicTabbedPaneUI.this.tabRuns[0] = 0;
/* 2611 */             BasicTabbedPaneUI.this.runCount = 1;
/* 2612 */             BasicTabbedPaneUI.this.maxTabWidth = 0;
/* 2613 */             localRectangle.x = i1;
/*      */           }
/* 2615 */           localRectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(paramInt1, m, localFontMetrics);
/* 2616 */           BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, localRectangle.width);
/*      */ 
/* 2621 */           if ((localRectangle.x != 2 + localInsets1.left) && (localRectangle.x + localRectangle.width > i3)) {
/* 2622 */             if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1) {
/* 2623 */               BasicTabbedPaneUI.this.expandTabRunsArray();
/*      */             }
/* 2625 */             BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = m;
/* 2626 */             BasicTabbedPaneUI.this.runCount += 1;
/* 2627 */             localRectangle.x = i1;
/*      */           }
/*      */ 
/* 2630 */           localRectangle.y = i2;
/* 2631 */           localRectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
/*      */         }
/*      */         else
/*      */         {
/* 2635 */           if (m > 0) {
/* 2636 */             localRectangle.y = (BasicTabbedPaneUI.this.rects[(m - 1)].y + BasicTabbedPaneUI.this.rects[(m - 1)].height);
/*      */           } else {
/* 2638 */             BasicTabbedPaneUI.this.tabRuns[0] = 0;
/* 2639 */             BasicTabbedPaneUI.this.runCount = 1;
/* 2640 */             BasicTabbedPaneUI.this.maxTabHeight = 0;
/* 2641 */             localRectangle.y = i2;
/*      */           }
/* 2643 */           localRectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(paramInt1, m, i);
/* 2644 */           BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, localRectangle.height);
/*      */ 
/* 2649 */           if ((localRectangle.y != 2 + localInsets1.top) && (localRectangle.y + localRectangle.height > i3)) {
/* 2650 */             if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1) {
/* 2651 */               BasicTabbedPaneUI.this.expandTabRunsArray();
/*      */             }
/* 2653 */             BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = m;
/* 2654 */             BasicTabbedPaneUI.this.runCount += 1;
/* 2655 */             localRectangle.y = i2;
/*      */           }
/*      */ 
/* 2658 */           localRectangle.x = i1;
/* 2659 */           localRectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
/*      */         }
/*      */ 
/* 2662 */         if (m == j) {
/* 2663 */           BasicTabbedPaneUI.this.selectedRun = (BasicTabbedPaneUI.this.runCount - 1);
/*      */         }
/*      */       }
/*      */ 
/* 2667 */       if (BasicTabbedPaneUI.this.runCount > 1)
/*      */       {
/* 2669 */         normalizeTabRuns(paramInt1, paramInt2, i4 != 0 ? i2 : i1, i3);
/*      */ 
/* 2671 */         BasicTabbedPaneUI.this.selectedRun = BasicTabbedPaneUI.this.getRunForTab(paramInt2, j);
/*      */ 
/* 2674 */         if (BasicTabbedPaneUI.this.shouldRotateTabRuns(paramInt1))
/* 2675 */           rotateTabRuns(paramInt1, BasicTabbedPaneUI.this.selectedRun);
/*      */       }
/*      */       int i5;
/* 2681 */       for (m = BasicTabbedPaneUI.this.runCount - 1; m >= 0; m--) {
/* 2682 */         i5 = BasicTabbedPaneUI.this.tabRuns[m];
/* 2683 */         int i6 = BasicTabbedPaneUI.this.tabRuns[(m + 1)];
/* 2684 */         int i7 = i6 != 0 ? i6 - 1 : paramInt2 - 1;
/*      */         int n;
/* 2685 */         if (i4 == 0) {
/* 2686 */           for (n = i5; n <= i7; n++) {
/* 2687 */             localRectangle = BasicTabbedPaneUI.this.rects[n];
/* 2688 */             localRectangle.y = i2;
/* 2689 */             localRectangle.x += BasicTabbedPaneUI.this.getTabRunIndent(paramInt1, m);
/*      */           }
/* 2691 */           if (BasicTabbedPaneUI.this.shouldPadTabRun(paramInt1, m)) {
/* 2692 */             padTabRun(paramInt1, i5, i7, i3);
/*      */           }
/* 2694 */           if (paramInt1 == 3)
/* 2695 */             i2 -= BasicTabbedPaneUI.this.maxTabHeight - k;
/*      */           else
/* 2697 */             i2 += BasicTabbedPaneUI.this.maxTabHeight - k;
/*      */         }
/*      */         else {
/* 2700 */           for (n = i5; n <= i7; n++) {
/* 2701 */             localRectangle = BasicTabbedPaneUI.this.rects[n];
/* 2702 */             localRectangle.x = i1;
/* 2703 */             localRectangle.y += BasicTabbedPaneUI.this.getTabRunIndent(paramInt1, m);
/*      */           }
/* 2705 */           if (BasicTabbedPaneUI.this.shouldPadTabRun(paramInt1, m)) {
/* 2706 */             padTabRun(paramInt1, i5, i7, i3);
/*      */           }
/* 2708 */           if (paramInt1 == 4)
/* 2709 */             i1 -= BasicTabbedPaneUI.this.maxTabWidth - k;
/*      */           else {
/* 2711 */             i1 += BasicTabbedPaneUI.this.maxTabWidth - k;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2717 */       padSelectedTab(paramInt1, j);
/*      */ 
/* 2721 */       if ((!bool) && (i4 == 0)) {
/* 2722 */         i5 = localDimension.width - (localInsets1.right + localInsets2.right);
/*      */ 
/* 2724 */         for (m = 0; m < paramInt2; m++)
/* 2725 */           BasicTabbedPaneUI.this.rects[m].x = (i5 - BasicTabbedPaneUI.this.rects[m].x - BasicTabbedPaneUI.this.rects[m].width);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void rotateTabRuns(int paramInt1, int paramInt2)
/*      */     {
/* 2735 */       for (int i = 0; i < paramInt2; i++) {
/* 2736 */         int j = BasicTabbedPaneUI.this.tabRuns[0];
/* 2737 */         for (int k = 1; k < BasicTabbedPaneUI.this.runCount; k++) {
/* 2738 */           BasicTabbedPaneUI.this.tabRuns[(k - 1)] = BasicTabbedPaneUI.this.tabRuns[k];
/*      */         }
/* 2740 */         BasicTabbedPaneUI.this.tabRuns[(BasicTabbedPaneUI.this.runCount - 1)] = j;
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void normalizeTabRuns(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 2746 */       int i = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
/* 2747 */       int j = BasicTabbedPaneUI.this.runCount - 1;
/* 2748 */       int k = 1;
/* 2749 */       double d = 1.25D;
/*      */ 
/* 2762 */       while (k != 0) {
/* 2763 */         int m = BasicTabbedPaneUI.this.lastTabInRun(paramInt2, j);
/* 2764 */         int n = BasicTabbedPaneUI.this.lastTabInRun(paramInt2, j - 1);
/*      */         int i1;
/*      */         int i2;
/* 2768 */         if (i == 0) {
/* 2769 */           i1 = BasicTabbedPaneUI.this.rects[m].x + BasicTabbedPaneUI.this.rects[m].width;
/* 2770 */           i2 = (int)(BasicTabbedPaneUI.this.maxTabWidth * d);
/*      */         } else {
/* 2772 */           i1 = BasicTabbedPaneUI.this.rects[m].y + BasicTabbedPaneUI.this.rects[m].height;
/* 2773 */           i2 = (int)(BasicTabbedPaneUI.this.maxTabHeight * d * 2.0D);
/*      */         }
/*      */ 
/* 2778 */         if (paramInt4 - i1 > i2)
/*      */         {
/* 2781 */           BasicTabbedPaneUI.this.tabRuns[j] = n;
/* 2782 */           if (i == 0)
/* 2783 */             BasicTabbedPaneUI.this.rects[n].x = paramInt3;
/*      */           else {
/* 2785 */             BasicTabbedPaneUI.this.rects[n].y = paramInt3;
/*      */           }
/* 2787 */           for (int i3 = n + 1; i3 <= m; i3++) {
/* 2788 */             if (i == 0)
/* 2789 */               BasicTabbedPaneUI.this.rects[i3].x = (BasicTabbedPaneUI.this.rects[(i3 - 1)].x + BasicTabbedPaneUI.this.rects[(i3 - 1)].width);
/*      */             else {
/* 2791 */               BasicTabbedPaneUI.this.rects[i3].y = (BasicTabbedPaneUI.this.rects[(i3 - 1)].y + BasicTabbedPaneUI.this.rects[(i3 - 1)].height);
/*      */             }
/*      */           }
/*      */         }
/* 2795 */         else if (j == BasicTabbedPaneUI.this.runCount - 1)
/*      */         {
/* 2797 */           k = 0;
/*      */         }
/* 2799 */         if (j - 1 > 0)
/*      */         {
/* 2801 */           j--;
/*      */         }
/*      */         else
/*      */         {
/* 2806 */           j = BasicTabbedPaneUI.this.runCount - 1;
/* 2807 */           d += 0.25D;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void padTabRun(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 2813 */       Rectangle localRectangle1 = BasicTabbedPaneUI.this.rects[paramInt3];
/*      */       int i;
/*      */       int j;
/*      */       float f;
/*      */       int k;
/*      */       Rectangle localRectangle2;
/* 2814 */       if ((paramInt1 == 1) || (paramInt1 == 3)) {
/* 2815 */         i = localRectangle1.x + localRectangle1.width - BasicTabbedPaneUI.this.rects[paramInt2].x;
/* 2816 */         j = paramInt4 - (localRectangle1.x + localRectangle1.width);
/* 2817 */         f = j / i;
/*      */ 
/* 2819 */         for (k = paramInt2; k <= paramInt3; k++) {
/* 2820 */           localRectangle2 = BasicTabbedPaneUI.this.rects[k];
/* 2821 */           if (k > paramInt2) {
/* 2822 */             localRectangle2.x = (BasicTabbedPaneUI.this.rects[(k - 1)].x + BasicTabbedPaneUI.this.rects[(k - 1)].width);
/*      */           }
/* 2824 */           localRectangle2.width += Math.round(localRectangle2.width * f);
/*      */         }
/* 2826 */         localRectangle1.width = (paramInt4 - localRectangle1.x);
/*      */       } else {
/* 2828 */         i = localRectangle1.y + localRectangle1.height - BasicTabbedPaneUI.this.rects[paramInt2].y;
/* 2829 */         j = paramInt4 - (localRectangle1.y + localRectangle1.height);
/* 2830 */         f = j / i;
/*      */ 
/* 2832 */         for (k = paramInt2; k <= paramInt3; k++) {
/* 2833 */           localRectangle2 = BasicTabbedPaneUI.this.rects[k];
/* 2834 */           if (k > paramInt2) {
/* 2835 */             localRectangle2.y = (BasicTabbedPaneUI.this.rects[(k - 1)].y + BasicTabbedPaneUI.this.rects[(k - 1)].height);
/*      */           }
/* 2837 */           localRectangle2.height += Math.round(localRectangle2.height * f);
/*      */         }
/* 2839 */         localRectangle1.height = (paramInt4 - localRectangle1.y);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void padSelectedTab(int paramInt1, int paramInt2)
/*      */     {
/* 2845 */       if (paramInt2 >= 0) {
/* 2846 */         Rectangle localRectangle = BasicTabbedPaneUI.this.rects[paramInt2];
/* 2847 */         Insets localInsets1 = BasicTabbedPaneUI.this.getSelectedTabPadInsets(paramInt1);
/* 2848 */         localRectangle.x -= localInsets1.left;
/* 2849 */         localRectangle.width += localInsets1.left + localInsets1.right;
/* 2850 */         localRectangle.y -= localInsets1.top;
/* 2851 */         localRectangle.height += localInsets1.top + localInsets1.bottom;
/*      */ 
/* 2853 */         if (!BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
/*      */         {
/* 2855 */           Dimension localDimension = BasicTabbedPaneUI.this.tabPane.getSize();
/* 2856 */           Insets localInsets2 = BasicTabbedPaneUI.this.tabPane.getInsets();
/*      */           int i;
/*      */           int j;
/* 2858 */           if ((paramInt1 == 2) || (paramInt1 == 4)) {
/* 2859 */             i = localInsets2.top - localRectangle.y;
/* 2860 */             if (i > 0) {
/* 2861 */               localRectangle.y += i;
/* 2862 */               localRectangle.height -= i;
/*      */             }
/* 2864 */             j = localRectangle.y + localRectangle.height + localInsets2.bottom - localDimension.height;
/* 2865 */             if (j > 0)
/* 2866 */               localRectangle.height -= j;
/*      */           }
/*      */           else {
/* 2869 */             i = localInsets2.left - localRectangle.x;
/* 2870 */             if (i > 0) {
/* 2871 */               localRectangle.x += i;
/* 2872 */               localRectangle.width -= i;
/*      */             }
/* 2874 */             j = localRectangle.x + localRectangle.width + localInsets2.right - localDimension.width;
/* 2875 */             if (j > 0)
/* 2876 */               localRectangle.width -= j; 
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TabbedPaneScrollLayout extends BasicTabbedPaneUI.TabbedPaneLayout {
/* 2884 */     private TabbedPaneScrollLayout() { super(); }
/*      */ 
/*      */     protected int preferredTabAreaHeight(int paramInt1, int paramInt2) {
/* 2887 */       return BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
/*      */     }
/*      */ 
/*      */     protected int preferredTabAreaWidth(int paramInt1, int paramInt2) {
/* 2891 */       return BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
/*      */     }
/*      */ 
/*      */     public void layoutContainer(Container paramContainer)
/*      */     {
/* 2906 */       BasicTabbedPaneUI.this.setRolloverTab(-1);
/*      */ 
/* 2908 */       int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
/* 2909 */       int j = BasicTabbedPaneUI.this.tabPane.getTabCount();
/* 2910 */       Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
/* 2911 */       int k = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
/* 2912 */       Component localComponent1 = BasicTabbedPaneUI.this.getVisibleComponent();
/*      */ 
/* 2914 */       calculateLayoutInfo();
/*      */ 
/* 2916 */       Component localComponent2 = null;
/* 2917 */       if (k < 0) {
/* 2918 */         if (localComponent1 != null)
/*      */         {
/* 2920 */           BasicTabbedPaneUI.this.setVisibleComponent(null);
/*      */         }
/*      */       }
/* 2923 */       else localComponent2 = BasicTabbedPaneUI.this.tabPane.getComponentAt(k);
/*      */ 
/* 2926 */       if (BasicTabbedPaneUI.this.tabPane.getTabCount() == 0) {
/* 2927 */         BasicTabbedPaneUI.this.tabScroller.croppedEdge.resetParams();
/* 2928 */         BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.setVisible(false);
/* 2929 */         BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton.setVisible(false);
/* 2930 */         return;
/*      */       }
/*      */ 
/* 2933 */       int m = 0;
/*      */ 
/* 2942 */       if (localComponent2 != null) {
/* 2943 */         if ((localComponent2 != localComponent1) && (localComponent1 != null))
/*      */         {
/* 2945 */           if (SwingUtilities.findFocusOwner(localComponent1) != null) {
/* 2946 */             m = 1;
/*      */           }
/*      */         }
/* 2949 */         BasicTabbedPaneUI.this.setVisibleComponent(localComponent2);
/*      */       }
/*      */ 
/* 2953 */       Insets localInsets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
/* 2954 */       Rectangle localRectangle = BasicTabbedPaneUI.this.tabPane.getBounds();
/* 2955 */       int i8 = BasicTabbedPaneUI.this.tabPane.getComponentCount();
/*      */ 
/* 2957 */       if (i8 > 0)
/*      */       {
/*      */         int i2;
/*      */         int i3;
/*      */         int n;
/*      */         int i1;
/*      */         int i4;
/*      */         int i5;
/*      */         int i6;
/*      */         int i7;
/* 2958 */         switch (i)
/*      */         {
/*      */         case 2:
/* 2961 */           i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
/* 2962 */           i3 = localRectangle.height - localInsets1.top - localInsets1.bottom;
/* 2963 */           n = localInsets1.left;
/* 2964 */           i1 = localInsets1.top;
/*      */ 
/* 2967 */           i4 = n + i2 + localInsets2.left;
/* 2968 */           i5 = i1 + localInsets2.top;
/* 2969 */           i6 = localRectangle.width - localInsets1.left - localInsets1.right - i2 - localInsets2.left - localInsets2.right;
/*      */ 
/* 2971 */           i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - localInsets2.top - localInsets2.bottom;
/*      */ 
/* 2973 */           break;
/*      */         case 4:
/* 2976 */           i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
/* 2977 */           i3 = localRectangle.height - localInsets1.top - localInsets1.bottom;
/* 2978 */           n = localRectangle.width - localInsets1.right - i2;
/* 2979 */           i1 = localInsets1.top;
/*      */ 
/* 2982 */           i4 = localInsets1.left + localInsets2.left;
/* 2983 */           i5 = localInsets1.top + localInsets2.top;
/* 2984 */           i6 = localRectangle.width - localInsets1.left - localInsets1.right - i2 - localInsets2.left - localInsets2.right;
/*      */ 
/* 2986 */           i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - localInsets2.top - localInsets2.bottom;
/*      */ 
/* 2988 */           break;
/*      */         case 3:
/* 2991 */           i2 = localRectangle.width - localInsets1.left - localInsets1.right;
/* 2992 */           i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
/* 2993 */           n = localInsets1.left;
/* 2994 */           i1 = localRectangle.height - localInsets1.bottom - i3;
/*      */ 
/* 2997 */           i4 = localInsets1.left + localInsets2.left;
/* 2998 */           i5 = localInsets1.top + localInsets2.top;
/* 2999 */           i6 = localRectangle.width - localInsets1.left - localInsets1.right - localInsets2.left - localInsets2.right;
/*      */ 
/* 3001 */           i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - i3 - localInsets2.top - localInsets2.bottom;
/*      */ 
/* 3003 */           break;
/*      */         case 1:
/*      */         default:
/* 3007 */           i2 = localRectangle.width - localInsets1.left - localInsets1.right;
/* 3008 */           i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
/* 3009 */           n = localInsets1.left;
/* 3010 */           i1 = localInsets1.top;
/*      */ 
/* 3013 */           i4 = n + localInsets2.left;
/* 3014 */           i5 = i1 + i3 + localInsets2.top;
/* 3015 */           i6 = localRectangle.width - localInsets1.left - localInsets1.right - localInsets2.left - localInsets2.right;
/*      */ 
/* 3017 */           i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - i3 - localInsets2.top - localInsets2.bottom;
/*      */         }
/*      */ 
/* 3021 */         for (int i9 = 0; i9 < i8; i9++) {
/* 3022 */           Component localComponent3 = BasicTabbedPaneUI.this.tabPane.getComponent(i9);
/*      */           Object localObject1;
/*      */           Object localObject2;
/*      */           int i10;
/*      */           int i11;
/*      */           int i13;
/*      */           int i14;
/* 3024 */           if ((BasicTabbedPaneUI.this.tabScroller != null) && (localComponent3 == BasicTabbedPaneUI.this.tabScroller.viewport)) {
/* 3025 */             localObject1 = (JViewport)localComponent3;
/* 3026 */             localObject2 = ((JViewport)localObject1).getViewRect();
/* 3027 */             i10 = i2;
/* 3028 */             i11 = i3;
/* 3029 */             Dimension localDimension = BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.getPreferredSize();
/* 3030 */             switch (i) {
/*      */             case 2:
/*      */             case 4:
/* 3033 */               i13 = BasicTabbedPaneUI.this.rects[(j - 1)].y + BasicTabbedPaneUI.this.rects[(j - 1)].height;
/* 3034 */               if (i13 > i3)
/*      */               {
/* 3036 */                 i11 = i3 > 2 * localDimension.height ? i3 - 2 * localDimension.height : 0;
/* 3037 */                 if (i13 - ((Rectangle)localObject2).y <= i11)
/*      */                 {
/* 3040 */                   i11 = i13 - ((Rectangle)localObject2).y; }  } break;
/*      */             case 1:
/*      */             case 3:
/*      */             default:
/* 3047 */               i14 = BasicTabbedPaneUI.this.rects[(j - 1)].x + BasicTabbedPaneUI.this.rects[(j - 1)].width;
/* 3048 */               if (i14 > i2)
/*      */               {
/* 3050 */                 i10 = i2 > 2 * localDimension.width ? i2 - 2 * localDimension.width : 0;
/* 3051 */                 if (i14 - ((Rectangle)localObject2).x <= i10)
/*      */                 {
/* 3054 */                   i10 = i14 - ((Rectangle)localObject2).x;
/*      */                 }
/*      */               }
/*      */               break;
/*      */             }
/* 3058 */             localComponent3.setBounds(n, i1, i10, i11);
/*      */           }
/* 3060 */           else if ((BasicTabbedPaneUI.this.tabScroller != null) && ((localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton) || (localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton)))
/*      */           {
/* 3063 */             localObject1 = localComponent3;
/* 3064 */             localObject2 = ((Component)localObject1).getPreferredSize();
/* 3065 */             i10 = 0;
/* 3066 */             i11 = 0;
/* 3067 */             int i12 = ((Dimension)localObject2).width;
/* 3068 */             i13 = ((Dimension)localObject2).height;
/* 3069 */             i14 = 0;
/*      */ 
/* 3071 */             switch (i) {
/*      */             case 2:
/*      */             case 4:
/* 3074 */               int i15 = BasicTabbedPaneUI.this.rects[(j - 1)].y + BasicTabbedPaneUI.this.rects[(j - 1)].height;
/* 3075 */               if (i15 > i3) {
/* 3076 */                 i14 = 1;
/* 3077 */                 i10 = i == 2 ? n + i2 - ((Dimension)localObject2).width : n;
/* 3078 */                 i11 = localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton ? localRectangle.height - localInsets1.bottom - ((Dimension)localObject2).height : localRectangle.height - localInsets1.bottom - 2 * ((Dimension)localObject2).height; } break;
/*      */             case 1:
/*      */             case 3:
/*      */             default:
/* 3087 */               int i16 = BasicTabbedPaneUI.this.rects[(j - 1)].x + BasicTabbedPaneUI.this.rects[(j - 1)].width;
/*      */ 
/* 3089 */               if (i16 > i2) {
/* 3090 */                 i14 = 1;
/* 3091 */                 i10 = localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton ? localRectangle.width - localInsets1.left - ((Dimension)localObject2).width : localRectangle.width - localInsets1.left - 2 * ((Dimension)localObject2).width;
/*      */ 
/* 3094 */                 i11 = i == 1 ? i1 + i3 - ((Dimension)localObject2).height : i1;
/*      */               }break;
/*      */             }
/* 3097 */             localComponent3.setVisible(i14);
/* 3098 */             if (i14 != 0) {
/* 3099 */               localComponent3.setBounds(i10, i11, i12, i13);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 3104 */             localComponent3.setBounds(i4, i5, i6, i7);
/*      */           }
/*      */         }
/* 3107 */         super.layoutTabComponents();
/* 3108 */         layoutCroppedEdge();
/* 3109 */         if ((m != 0) && 
/* 3110 */           (!BasicTabbedPaneUI.this.requestFocusForVisibleComponent()))
/* 3111 */           BasicTabbedPaneUI.this.tabPane.requestFocus();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void layoutCroppedEdge()
/*      */     {
/* 3118 */       BasicTabbedPaneUI.this.tabScroller.croppedEdge.resetParams();
/* 3119 */       Rectangle localRectangle1 = BasicTabbedPaneUI.this.tabScroller.viewport.getViewRect();
/*      */ 
/* 3121 */       for (int j = 0; j < BasicTabbedPaneUI.this.rects.length; j++) {
/* 3122 */         Rectangle localRectangle2 = BasicTabbedPaneUI.this.rects[j];
/*      */         int i;
/* 3123 */         switch (BasicTabbedPaneUI.this.tabPane.getTabPlacement()) {
/*      */         case 2:
/*      */         case 4:
/* 3126 */           i = localRectangle1.y + localRectangle1.height;
/* 3127 */           if ((localRectangle2.y < i) && (localRectangle2.y + localRectangle2.height > i))
/* 3128 */             BasicTabbedPaneUI.this.tabScroller.croppedEdge.setParams(j, i - localRectangle2.y - 1, -BasicTabbedPaneUI.this.currentTabAreaInsets.left, 0); break;
/*      */         case 1:
/*      */         case 3:
/*      */         default:
/* 3135 */           i = localRectangle1.x + localRectangle1.width;
/* 3136 */           if ((localRectangle2.x < i - 1) && (localRectangle2.x + localRectangle2.width > i))
/* 3137 */             BasicTabbedPaneUI.this.tabScroller.croppedEdge.setParams(j, i - localRectangle2.x - 1, 0, -BasicTabbedPaneUI.this.currentTabAreaInsets.top);
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void calculateTabRects(int paramInt1, int paramInt2)
/*      */     {
/* 3145 */       FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
/* 3146 */       Dimension localDimension = BasicTabbedPaneUI.this.tabPane.getSize();
/* 3147 */       Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
/* 3148 */       Insets localInsets2 = BasicTabbedPaneUI.this.getTabAreaInsets(paramInt1);
/* 3149 */       int i = localFontMetrics.getHeight();
/* 3150 */       int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
/*      */ 
/* 3152 */       int m = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
/* 3153 */       boolean bool = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
/* 3154 */       int n = localInsets2.left;
/* 3155 */       int i1 = localInsets2.top;
/* 3156 */       int i2 = 0;
/* 3157 */       int i3 = 0;
/*      */ 
/* 3162 */       switch (paramInt1) {
/*      */       case 2:
/*      */       case 4:
/* 3165 */         BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
/* 3166 */         break;
/*      */       case 1:
/*      */       case 3:
/*      */       default:
/* 3170 */         BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
/*      */       }
/*      */ 
/* 3173 */       BasicTabbedPaneUI.this.runCount = 0;
/* 3174 */       BasicTabbedPaneUI.this.selectedRun = -1;
/*      */ 
/* 3176 */       if (paramInt2 == 0) {
/* 3177 */         return;
/*      */       }
/*      */ 
/* 3180 */       BasicTabbedPaneUI.this.selectedRun = 0;
/* 3181 */       BasicTabbedPaneUI.this.runCount = 1;
/*      */ 
/* 3185 */       for (int k = 0; k < paramInt2; k++) {
/* 3186 */         Rectangle localRectangle = BasicTabbedPaneUI.this.rects[k];
/*      */ 
/* 3188 */         if (m == 0)
/*      */         {
/* 3190 */           if (k > 0) {
/* 3191 */             localRectangle.x = (BasicTabbedPaneUI.this.rects[(k - 1)].x + BasicTabbedPaneUI.this.rects[(k - 1)].width);
/*      */           } else {
/* 3193 */             BasicTabbedPaneUI.this.tabRuns[0] = 0;
/* 3194 */             BasicTabbedPaneUI.this.maxTabWidth = 0;
/* 3195 */             i3 += BasicTabbedPaneUI.this.maxTabHeight;
/* 3196 */             localRectangle.x = n;
/*      */           }
/* 3198 */           localRectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(paramInt1, k, localFontMetrics);
/* 3199 */           i2 = localRectangle.x + localRectangle.width;
/* 3200 */           BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, localRectangle.width);
/*      */ 
/* 3202 */           localRectangle.y = i1;
/* 3203 */           localRectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
/*      */         }
/*      */         else
/*      */         {
/* 3207 */           if (k > 0) {
/* 3208 */             localRectangle.y = (BasicTabbedPaneUI.this.rects[(k - 1)].y + BasicTabbedPaneUI.this.rects[(k - 1)].height);
/*      */           } else {
/* 3210 */             BasicTabbedPaneUI.this.tabRuns[0] = 0;
/* 3211 */             BasicTabbedPaneUI.this.maxTabHeight = 0;
/* 3212 */             i2 = BasicTabbedPaneUI.this.maxTabWidth;
/* 3213 */             localRectangle.y = i1;
/*      */           }
/* 3215 */           localRectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(paramInt1, k, i);
/* 3216 */           i3 = localRectangle.y + localRectangle.height;
/* 3217 */           BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, localRectangle.height);
/*      */ 
/* 3219 */           localRectangle.x = n;
/* 3220 */           localRectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3225 */       if (BasicTabbedPaneUI.this.tabsOverlapBorder)
/*      */       {
/* 3227 */         padSelectedTab(paramInt1, j);
/*      */       }
/*      */ 
/* 3232 */       if ((!bool) && (m == 0)) {
/* 3233 */         int i4 = localDimension.width - (localInsets1.right + localInsets2.right);
/*      */ 
/* 3235 */         for (k = 0; k < paramInt2; k++) {
/* 3236 */           BasicTabbedPaneUI.this.rects[k].x = (i4 - BasicTabbedPaneUI.this.rects[k].x - BasicTabbedPaneUI.this.rects[k].width);
/*      */         }
/*      */       }
/* 3239 */       BasicTabbedPaneUI.this.tabScroller.tabPanel.setPreferredSize(new Dimension(i2, i3));
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.basic.BasicTabbedPaneUI
 * JD-Core Version:    0.6.2
 */