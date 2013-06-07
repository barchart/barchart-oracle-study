/*      */ package javax.swing.plaf.basic;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Component.BaselineResizeBehavior;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.datatransfer.Transferable;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ComponentAdapter;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.ComponentListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.KeyAdapter;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.CellRendererPane;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JScrollBar;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JTree;
/*      */ import javax.swing.JTree.DropLocation;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.LookAndFeel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.Timer;
/*      */ import javax.swing.TransferHandler;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.event.CellEditorListener;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.MouseInputListener;
/*      */ import javax.swing.event.TreeExpansionEvent;
/*      */ import javax.swing.event.TreeExpansionListener;
/*      */ import javax.swing.event.TreeModelEvent;
/*      */ import javax.swing.event.TreeModelListener;
/*      */ import javax.swing.event.TreeSelectionEvent;
/*      */ import javax.swing.event.TreeSelectionListener;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.TreeUI;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.text.Position.Bias;
/*      */ import javax.swing.tree.AbstractLayoutCache;
/*      */ import javax.swing.tree.AbstractLayoutCache.NodeDimensions;
/*      */ import javax.swing.tree.DefaultTreeCellEditor;
/*      */ import javax.swing.tree.DefaultTreeCellRenderer;
/*      */ import javax.swing.tree.FixedHeightLayoutCache;
/*      */ import javax.swing.tree.TreeCellEditor;
/*      */ import javax.swing.tree.TreeCellRenderer;
/*      */ import javax.swing.tree.TreeModel;
/*      */ import javax.swing.tree.TreePath;
/*      */ import javax.swing.tree.TreeSelectionModel;
/*      */ import javax.swing.tree.VariableHeightLayoutCache;
/*      */ import sun.swing.DefaultLookup;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.UIAction;
/*      */ 
/*      */ public class BasicTreeUI extends TreeUI
/*      */ {
/*   60 */   private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("Tree.baselineComponent");
/*      */ 
/*   64 */   private static final Actions SHARED_ACTION = new Actions();
/*      */   protected transient Icon collapsedIcon;
/*      */   protected transient Icon expandedIcon;
/*      */   private Color hashColor;
/*      */   protected int leftChildIndent;
/*      */   protected int rightChildIndent;
/*      */   protected int totalChildIndent;
/*      */   protected Dimension preferredMinSize;
/*      */   protected int lastSelectedRow;
/*      */   protected JTree tree;
/*      */   protected transient TreeCellRenderer currentCellRenderer;
/*      */   protected boolean createdRenderer;
/*      */   protected transient TreeCellEditor cellEditor;
/*      */   protected boolean createdCellEditor;
/*      */   protected boolean stopEditingInCompleteEditing;
/*      */   protected CellRendererPane rendererPane;
/*      */   protected Dimension preferredSize;
/*      */   protected boolean validCachedPreferredSize;
/*      */   protected AbstractLayoutCache treeState;
/*      */   protected Hashtable<TreePath, Boolean> drawingCache;
/*      */   protected boolean largeModel;
/*      */   protected AbstractLayoutCache.NodeDimensions nodeDimensions;
/*      */   protected TreeModel treeModel;
/*      */   protected TreeSelectionModel treeSelectionModel;
/*      */   protected int depthOffset;
/*      */   protected Component editingComponent;
/*      */   protected TreePath editingPath;
/*      */   protected int editingRow;
/*      */   protected boolean editorHasDifferentSize;
/*      */   private int leadRow;
/*      */   private boolean ignoreLAChange;
/*      */   private boolean leftToRight;
/*      */   private PropertyChangeListener propertyChangeListener;
/*      */   private PropertyChangeListener selectionModelPropertyChangeListener;
/*      */   private MouseListener mouseListener;
/*      */   private FocusListener focusListener;
/*      */   private KeyListener keyListener;
/*      */   private ComponentListener componentListener;
/*      */   private CellEditorListener cellEditorListener;
/*      */   private TreeSelectionListener treeSelectionListener;
/*      */   private TreeModelListener treeModelListener;
/*      */   private TreeExpansionListener treeExpansionListener;
/*  194 */   private boolean paintLines = true;
/*      */   private boolean lineTypeDashed;
/*  203 */   private long timeFactor = 1000L;
/*      */   private Handler handler;
/*      */   private MouseEvent releaseEvent;
/* 3166 */   private static final TransferHandler defaultTransferHandler = new TreeTransferHandler();
/*      */ 
/*      */   public static ComponentUI createUI(JComponent paramJComponent)
/*      */   {
/*  214 */     return new BasicTreeUI();
/*      */   }
/*      */ 
/*      */   static void loadActionMap(LazyActionMap paramLazyActionMap)
/*      */   {
/*  219 */     paramLazyActionMap.put(new Actions("selectPrevious"));
/*  220 */     paramLazyActionMap.put(new Actions("selectPreviousChangeLead"));
/*  221 */     paramLazyActionMap.put(new Actions("selectPreviousExtendSelection"));
/*      */ 
/*  223 */     paramLazyActionMap.put(new Actions("selectNext"));
/*  224 */     paramLazyActionMap.put(new Actions("selectNextChangeLead"));
/*  225 */     paramLazyActionMap.put(new Actions("selectNextExtendSelection"));
/*      */ 
/*  227 */     paramLazyActionMap.put(new Actions("selectChild"));
/*  228 */     paramLazyActionMap.put(new Actions("selectChildChangeLead"));
/*      */ 
/*  230 */     paramLazyActionMap.put(new Actions("selectParent"));
/*  231 */     paramLazyActionMap.put(new Actions("selectParentChangeLead"));
/*      */ 
/*  233 */     paramLazyActionMap.put(new Actions("scrollUpChangeSelection"));
/*  234 */     paramLazyActionMap.put(new Actions("scrollUpChangeLead"));
/*  235 */     paramLazyActionMap.put(new Actions("scrollUpExtendSelection"));
/*      */ 
/*  237 */     paramLazyActionMap.put(new Actions("scrollDownChangeSelection"));
/*  238 */     paramLazyActionMap.put(new Actions("scrollDownExtendSelection"));
/*  239 */     paramLazyActionMap.put(new Actions("scrollDownChangeLead"));
/*      */ 
/*  241 */     paramLazyActionMap.put(new Actions("selectFirst"));
/*  242 */     paramLazyActionMap.put(new Actions("selectFirstChangeLead"));
/*  243 */     paramLazyActionMap.put(new Actions("selectFirstExtendSelection"));
/*      */ 
/*  245 */     paramLazyActionMap.put(new Actions("selectLast"));
/*  246 */     paramLazyActionMap.put(new Actions("selectLastChangeLead"));
/*  247 */     paramLazyActionMap.put(new Actions("selectLastExtendSelection"));
/*      */ 
/*  249 */     paramLazyActionMap.put(new Actions("toggle"));
/*      */ 
/*  251 */     paramLazyActionMap.put(new Actions("cancel"));
/*      */ 
/*  253 */     paramLazyActionMap.put(new Actions("startEditing"));
/*      */ 
/*  255 */     paramLazyActionMap.put(new Actions("selectAll"));
/*      */ 
/*  257 */     paramLazyActionMap.put(new Actions("clearSelection"));
/*      */ 
/*  259 */     paramLazyActionMap.put(new Actions("scrollLeft"));
/*  260 */     paramLazyActionMap.put(new Actions("scrollRight"));
/*      */ 
/*  262 */     paramLazyActionMap.put(new Actions("scrollLeftExtendSelection"));
/*  263 */     paramLazyActionMap.put(new Actions("scrollRightExtendSelection"));
/*      */ 
/*  265 */     paramLazyActionMap.put(new Actions("scrollRightChangeLead"));
/*  266 */     paramLazyActionMap.put(new Actions("scrollLeftChangeLead"));
/*      */ 
/*  268 */     paramLazyActionMap.put(new Actions("expand"));
/*  269 */     paramLazyActionMap.put(new Actions("collapse"));
/*  270 */     paramLazyActionMap.put(new Actions("moveSelectionToParent"));
/*      */ 
/*  272 */     paramLazyActionMap.put(new Actions("addToSelection"));
/*  273 */     paramLazyActionMap.put(new Actions("toggleAndAnchor"));
/*  274 */     paramLazyActionMap.put(new Actions("extendTo"));
/*  275 */     paramLazyActionMap.put(new Actions("moveSelectionTo"));
/*      */ 
/*  277 */     paramLazyActionMap.put(TransferHandler.getCutAction());
/*  278 */     paramLazyActionMap.put(TransferHandler.getCopyAction());
/*  279 */     paramLazyActionMap.put(TransferHandler.getPasteAction());
/*      */   }
/*      */ 
/*      */   protected Color getHashColor()
/*      */   {
/*  288 */     return this.hashColor;
/*      */   }
/*      */ 
/*      */   protected void setHashColor(Color paramColor) {
/*  292 */     this.hashColor = paramColor;
/*      */   }
/*      */ 
/*      */   public void setLeftChildIndent(int paramInt) {
/*  296 */     this.leftChildIndent = paramInt;
/*  297 */     this.totalChildIndent = (this.leftChildIndent + this.rightChildIndent);
/*  298 */     if (this.treeState != null)
/*  299 */       this.treeState.invalidateSizes();
/*  300 */     updateSize();
/*      */   }
/*      */ 
/*      */   public int getLeftChildIndent() {
/*  304 */     return this.leftChildIndent;
/*      */   }
/*      */ 
/*      */   public void setRightChildIndent(int paramInt) {
/*  308 */     this.rightChildIndent = paramInt;
/*  309 */     this.totalChildIndent = (this.leftChildIndent + this.rightChildIndent);
/*  310 */     if (this.treeState != null)
/*  311 */       this.treeState.invalidateSizes();
/*  312 */     updateSize();
/*      */   }
/*      */ 
/*      */   public int getRightChildIndent() {
/*  316 */     return this.rightChildIndent;
/*      */   }
/*      */ 
/*      */   public void setExpandedIcon(Icon paramIcon) {
/*  320 */     this.expandedIcon = paramIcon;
/*      */   }
/*      */ 
/*      */   public Icon getExpandedIcon() {
/*  324 */     return this.expandedIcon;
/*      */   }
/*      */ 
/*      */   public void setCollapsedIcon(Icon paramIcon) {
/*  328 */     this.collapsedIcon = paramIcon;
/*      */   }
/*      */ 
/*      */   public Icon getCollapsedIcon() {
/*  332 */     return this.collapsedIcon;
/*      */   }
/*      */ 
/*      */   protected void setLargeModel(boolean paramBoolean)
/*      */   {
/*  345 */     if (getRowHeight() < 1)
/*  346 */       paramBoolean = false;
/*  347 */     if (this.largeModel != paramBoolean) {
/*  348 */       completeEditing();
/*  349 */       this.largeModel = paramBoolean;
/*  350 */       this.treeState = createLayoutCache();
/*  351 */       configureLayoutCache();
/*  352 */       updateLayoutCacheExpandedNodesIfNecessary();
/*  353 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isLargeModel() {
/*  358 */     return this.largeModel;
/*      */   }
/*      */ 
/*      */   protected void setRowHeight(int paramInt)
/*      */   {
/*  365 */     completeEditing();
/*  366 */     if (this.treeState != null) {
/*  367 */       setLargeModel(this.tree.isLargeModel());
/*  368 */       this.treeState.setRowHeight(paramInt);
/*  369 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getRowHeight() {
/*  374 */     return this.tree == null ? -1 : this.tree.getRowHeight();
/*      */   }
/*      */ 
/*      */   protected void setCellRenderer(TreeCellRenderer paramTreeCellRenderer)
/*      */   {
/*  382 */     completeEditing();
/*  383 */     updateRenderer();
/*  384 */     if (this.treeState != null) {
/*  385 */       this.treeState.invalidateSizes();
/*  386 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected TreeCellRenderer getCellRenderer()
/*      */   {
/*  395 */     return this.currentCellRenderer;
/*      */   }
/*      */ 
/*      */   protected void setModel(TreeModel paramTreeModel)
/*      */   {
/*  402 */     completeEditing();
/*  403 */     if ((this.treeModel != null) && (this.treeModelListener != null))
/*  404 */       this.treeModel.removeTreeModelListener(this.treeModelListener);
/*  405 */     this.treeModel = paramTreeModel;
/*  406 */     if ((this.treeModel != null) && 
/*  407 */       (this.treeModelListener != null)) {
/*  408 */       this.treeModel.addTreeModelListener(this.treeModelListener);
/*      */     }
/*  410 */     if (this.treeState != null) {
/*  411 */       this.treeState.setModel(paramTreeModel);
/*  412 */       updateLayoutCacheExpandedNodesIfNecessary();
/*  413 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected TreeModel getModel() {
/*  418 */     return this.treeModel;
/*      */   }
/*      */ 
/*      */   protected void setRootVisible(boolean paramBoolean)
/*      */   {
/*  425 */     completeEditing();
/*  426 */     updateDepthOffset();
/*  427 */     if (this.treeState != null) {
/*  428 */       this.treeState.setRootVisible(paramBoolean);
/*  429 */       this.treeState.invalidateSizes();
/*  430 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isRootVisible() {
/*  435 */     return this.tree != null ? this.tree.isRootVisible() : false;
/*      */   }
/*      */ 
/*      */   protected void setShowsRootHandles(boolean paramBoolean)
/*      */   {
/*  442 */     completeEditing();
/*  443 */     updateDepthOffset();
/*  444 */     if (this.treeState != null) {
/*  445 */       this.treeState.invalidateSizes();
/*  446 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean getShowsRootHandles() {
/*  451 */     return this.tree != null ? this.tree.getShowsRootHandles() : false;
/*      */   }
/*      */ 
/*      */   protected void setCellEditor(TreeCellEditor paramTreeCellEditor)
/*      */   {
/*  458 */     updateCellEditor();
/*      */   }
/*      */ 
/*      */   protected TreeCellEditor getCellEditor() {
/*  462 */     return this.tree != null ? this.tree.getCellEditor() : null;
/*      */   }
/*      */ 
/*      */   protected void setEditable(boolean paramBoolean)
/*      */   {
/*  469 */     updateCellEditor();
/*      */   }
/*      */ 
/*      */   protected boolean isEditable() {
/*  473 */     return this.tree != null ? this.tree.isEditable() : false;
/*      */   }
/*      */ 
/*      */   protected void setSelectionModel(TreeSelectionModel paramTreeSelectionModel)
/*      */   {
/*  481 */     completeEditing();
/*  482 */     if ((this.selectionModelPropertyChangeListener != null) && (this.treeSelectionModel != null))
/*      */     {
/*  484 */       this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener);
/*      */     }
/*  486 */     if ((this.treeSelectionListener != null) && (this.treeSelectionModel != null)) {
/*  487 */       this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener);
/*      */     }
/*  489 */     this.treeSelectionModel = paramTreeSelectionModel;
/*  490 */     if (this.treeSelectionModel != null) {
/*  491 */       if (this.selectionModelPropertyChangeListener != null) {
/*  492 */         this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener);
/*      */       }
/*  494 */       if (this.treeSelectionListener != null) {
/*  495 */         this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener);
/*      */       }
/*  497 */       if (this.treeState != null)
/*  498 */         this.treeState.setSelectionModel(this.treeSelectionModel);
/*      */     }
/*  500 */     else if (this.treeState != null) {
/*  501 */       this.treeState.setSelectionModel(null);
/*  502 */     }if (this.tree != null)
/*  503 */       this.tree.repaint();
/*      */   }
/*      */ 
/*      */   protected TreeSelectionModel getSelectionModel() {
/*  507 */     return this.treeSelectionModel;
/*      */   }
/*      */ 
/*      */   public Rectangle getPathBounds(JTree paramJTree, TreePath paramTreePath)
/*      */   {
/*  520 */     if ((paramJTree != null) && (this.treeState != null)) {
/*  521 */       return getPathBounds(paramTreePath, paramJTree.getInsets(), new Rectangle());
/*      */     }
/*  523 */     return null;
/*      */   }
/*      */ 
/*      */   private Rectangle getPathBounds(TreePath paramTreePath, Insets paramInsets, Rectangle paramRectangle)
/*      */   {
/*  528 */     paramRectangle = this.treeState.getBounds(paramTreePath, paramRectangle);
/*  529 */     if (paramRectangle != null) {
/*  530 */       if (this.leftToRight)
/*  531 */         paramRectangle.x += paramInsets.left;
/*      */       else {
/*  533 */         paramRectangle.x = (this.tree.getWidth() - (paramRectangle.x + paramRectangle.width) - paramInsets.right);
/*      */       }
/*      */ 
/*  536 */       paramRectangle.y += paramInsets.top;
/*      */     }
/*  538 */     return paramRectangle;
/*      */   }
/*      */ 
/*      */   public TreePath getPathForRow(JTree paramJTree, int paramInt)
/*      */   {
/*  546 */     return this.treeState != null ? this.treeState.getPathForRow(paramInt) : null;
/*      */   }
/*      */ 
/*      */   public int getRowForPath(JTree paramJTree, TreePath paramTreePath)
/*      */   {
/*  555 */     return this.treeState != null ? this.treeState.getRowForPath(paramTreePath) : -1;
/*      */   }
/*      */ 
/*      */   public int getRowCount(JTree paramJTree)
/*      */   {
/*  562 */     return this.treeState != null ? this.treeState.getRowCount() : 0;
/*      */   }
/*      */ 
/*      */   public TreePath getClosestPathForLocation(JTree paramJTree, int paramInt1, int paramInt2)
/*      */   {
/*  573 */     if ((paramJTree != null) && (this.treeState != null))
/*      */     {
/*  576 */       paramInt2 -= paramJTree.getInsets().top;
/*  577 */       return this.treeState.getPathClosestTo(paramInt1, paramInt2);
/*      */     }
/*  579 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isEditing(JTree paramJTree)
/*      */   {
/*  587 */     return this.editingComponent != null;
/*      */   }
/*      */ 
/*      */   public boolean stopEditing(JTree paramJTree)
/*      */   {
/*  596 */     if ((this.editingComponent != null) && (this.cellEditor.stopCellEditing())) {
/*  597 */       completeEditing(false, false, true);
/*  598 */       return true;
/*      */     }
/*  600 */     return false;
/*      */   }
/*      */ 
/*      */   public void cancelEditing(JTree paramJTree)
/*      */   {
/*  607 */     if (this.editingComponent != null)
/*  608 */       completeEditing(false, true, false);
/*      */   }
/*      */ 
/*      */   public void startEditingAtPath(JTree paramJTree, TreePath paramTreePath)
/*      */   {
/*  617 */     paramJTree.scrollPathToVisible(paramTreePath);
/*  618 */     if ((paramTreePath != null) && (paramJTree.isVisible(paramTreePath)))
/*  619 */       startEditing(paramTreePath, null);
/*      */   }
/*      */ 
/*      */   public TreePath getEditingPath(JTree paramJTree)
/*      */   {
/*  626 */     return this.editingPath;
/*      */   }
/*      */ 
/*      */   public void installUI(JComponent paramJComponent)
/*      */   {
/*  634 */     if (paramJComponent == null) {
/*  635 */       throw new NullPointerException("null component passed to BasicTreeUI.installUI()");
/*      */     }
/*      */ 
/*  638 */     this.tree = ((JTree)paramJComponent);
/*      */ 
/*  640 */     prepareForUIInstall();
/*      */ 
/*  643 */     installDefaults();
/*  644 */     installKeyboardActions();
/*  645 */     installComponents();
/*  646 */     installListeners();
/*      */ 
/*  648 */     completeUIInstall();
/*      */   }
/*      */ 
/*      */   protected void prepareForUIInstall()
/*      */   {
/*  656 */     this.drawingCache = new Hashtable(7);
/*      */ 
/*  659 */     this.leftToRight = BasicGraphicsUtils.isLeftToRight(this.tree);
/*  660 */     this.stopEditingInCompleteEditing = true;
/*  661 */     this.lastSelectedRow = -1;
/*  662 */     this.leadRow = -1;
/*  663 */     this.preferredSize = new Dimension();
/*      */ 
/*  665 */     this.largeModel = this.tree.isLargeModel();
/*  666 */     if (getRowHeight() <= 0)
/*  667 */       this.largeModel = false;
/*  668 */     setModel(this.tree.getModel());
/*      */   }
/*      */ 
/*      */   protected void completeUIInstall()
/*      */   {
/*  678 */     setShowsRootHandles(this.tree.getShowsRootHandles());
/*      */ 
/*  680 */     updateRenderer();
/*      */ 
/*  682 */     updateDepthOffset();
/*      */ 
/*  684 */     setSelectionModel(this.tree.getSelectionModel());
/*      */ 
/*  687 */     this.treeState = createLayoutCache();
/*  688 */     configureLayoutCache();
/*      */ 
/*  690 */     updateSize();
/*      */   }
/*      */ 
/*      */   protected void installDefaults() {
/*  694 */     if ((this.tree.getBackground() == null) || ((this.tree.getBackground() instanceof UIResource)))
/*      */     {
/*  696 */       this.tree.setBackground(UIManager.getColor("Tree.background"));
/*      */     }
/*  698 */     if ((getHashColor() == null) || ((getHashColor() instanceof UIResource))) {
/*  699 */       setHashColor(UIManager.getColor("Tree.hash"));
/*      */     }
/*  701 */     if ((this.tree.getFont() == null) || ((this.tree.getFont() instanceof UIResource))) {
/*  702 */       this.tree.setFont(UIManager.getFont("Tree.font"));
/*      */     }
/*      */ 
/*  711 */     setExpandedIcon((Icon)UIManager.get("Tree.expandedIcon"));
/*  712 */     setCollapsedIcon((Icon)UIManager.get("Tree.collapsedIcon"));
/*      */ 
/*  714 */     setLeftChildIndent(((Integer)UIManager.get("Tree.leftChildIndent")).intValue());
/*      */ 
/*  716 */     setRightChildIndent(((Integer)UIManager.get("Tree.rightChildIndent")).intValue());
/*      */ 
/*  719 */     LookAndFeel.installProperty(this.tree, "rowHeight", UIManager.get("Tree.rowHeight"));
/*      */ 
/*  722 */     this.largeModel = ((this.tree.isLargeModel()) && (this.tree.getRowHeight() > 0));
/*      */ 
/*  724 */     Object localObject1 = UIManager.get("Tree.scrollsOnExpand");
/*  725 */     if (localObject1 != null) {
/*  726 */       LookAndFeel.installProperty(this.tree, "scrollsOnExpand", localObject1);
/*      */     }
/*      */ 
/*  729 */     this.paintLines = UIManager.getBoolean("Tree.paintLines");
/*  730 */     this.lineTypeDashed = UIManager.getBoolean("Tree.lineTypeDashed");
/*      */ 
/*  732 */     Long localLong = (Long)UIManager.get("Tree.timeFactor");
/*  733 */     this.timeFactor = (localLong != null ? localLong.longValue() : 1000L);
/*      */ 
/*  735 */     Object localObject2 = UIManager.get("Tree.showsRootHandles");
/*  736 */     if (localObject2 != null)
/*  737 */       LookAndFeel.installProperty(this.tree, "showsRootHandles", localObject2);
/*      */   }
/*      */ 
/*      */   protected void installListeners()
/*      */   {
/*  743 */     if ((this.propertyChangeListener = createPropertyChangeListener()) != null)
/*      */     {
/*  745 */       this.tree.addPropertyChangeListener(this.propertyChangeListener);
/*      */     }
/*  747 */     if ((this.mouseListener = createMouseListener()) != null) {
/*  748 */       this.tree.addMouseListener(this.mouseListener);
/*  749 */       if ((this.mouseListener instanceof MouseMotionListener)) {
/*  750 */         this.tree.addMouseMotionListener((MouseMotionListener)this.mouseListener);
/*      */       }
/*      */     }
/*  753 */     if ((this.focusListener = createFocusListener()) != null) {
/*  754 */       this.tree.addFocusListener(this.focusListener);
/*      */     }
/*  756 */     if ((this.keyListener = createKeyListener()) != null) {
/*  757 */       this.tree.addKeyListener(this.keyListener);
/*      */     }
/*  759 */     if ((this.treeExpansionListener = createTreeExpansionListener()) != null) {
/*  760 */       this.tree.addTreeExpansionListener(this.treeExpansionListener);
/*      */     }
/*  762 */     if (((this.treeModelListener = createTreeModelListener()) != null) && (this.treeModel != null))
/*      */     {
/*  764 */       this.treeModel.addTreeModelListener(this.treeModelListener);
/*      */     }
/*  766 */     if (((this.selectionModelPropertyChangeListener = createSelectionModelPropertyChangeListener()) != null) && (this.treeSelectionModel != null))
/*      */     {
/*  769 */       this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener);
/*      */     }
/*      */ 
/*  772 */     if (((this.treeSelectionListener = createTreeSelectionListener()) != null) && (this.treeSelectionModel != null))
/*      */     {
/*  774 */       this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener);
/*      */     }
/*      */ 
/*  777 */     TransferHandler localTransferHandler = this.tree.getTransferHandler();
/*  778 */     if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource))) {
/*  779 */       this.tree.setTransferHandler(defaultTransferHandler);
/*      */ 
/*  782 */       if ((this.tree.getDropTarget() instanceof UIResource)) {
/*  783 */         this.tree.setDropTarget(null);
/*      */       }
/*      */     }
/*      */ 
/*  787 */     LookAndFeel.installProperty(this.tree, "opaque", Boolean.TRUE);
/*      */   }
/*      */ 
/*      */   protected void installKeyboardActions() {
/*  791 */     InputMap localInputMap = getInputMap(1);
/*      */ 
/*  794 */     SwingUtilities.replaceUIInputMap(this.tree, 1, localInputMap);
/*      */ 
/*  797 */     localInputMap = getInputMap(0);
/*  798 */     SwingUtilities.replaceUIInputMap(this.tree, 0, localInputMap);
/*      */ 
/*  800 */     LazyActionMap.installLazyActionMap(this.tree, BasicTreeUI.class, "Tree.actionMap");
/*      */   }
/*      */ 
/*      */   InputMap getInputMap(int paramInt)
/*      */   {
/*  805 */     if (paramInt == 1) {
/*  806 */       return (InputMap)DefaultLookup.get(this.tree, this, "Tree.ancestorInputMap");
/*      */     }
/*      */ 
/*  809 */     if (paramInt == 0) {
/*  810 */       InputMap localInputMap1 = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap");
/*      */       InputMap localInputMap2;
/*  814 */       if ((this.tree.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap.RightToLeft")) == null))
/*      */       {
/*  817 */         return localInputMap1;
/*      */       }
/*  819 */       localInputMap2.setParent(localInputMap1);
/*  820 */       return localInputMap2;
/*      */     }
/*      */ 
/*  823 */     return null;
/*      */   }
/*      */ 
/*      */   protected void installComponents()
/*      */   {
/*  830 */     if ((this.rendererPane = createCellRendererPane()) != null)
/*  831 */       this.tree.add(this.rendererPane);
/*      */   }
/*      */ 
/*      */   protected AbstractLayoutCache.NodeDimensions createNodeDimensions()
/*      */   {
/*  844 */     return new NodeDimensionsHandler();
/*      */   }
/*      */ 
/*      */   protected PropertyChangeListener createPropertyChangeListener()
/*      */   {
/*  852 */     return getHandler();
/*      */   }
/*      */ 
/*      */   private Handler getHandler() {
/*  856 */     if (this.handler == null) {
/*  857 */       this.handler = new Handler(null);
/*      */     }
/*  859 */     return this.handler;
/*      */   }
/*      */ 
/*      */   protected MouseListener createMouseListener()
/*      */   {
/*  867 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected FocusListener createFocusListener()
/*      */   {
/*  875 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected KeyListener createKeyListener()
/*      */   {
/*  883 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected PropertyChangeListener createSelectionModelPropertyChangeListener()
/*      */   {
/*  891 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected TreeSelectionListener createTreeSelectionListener()
/*      */   {
/*  899 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected CellEditorListener createCellEditorListener()
/*      */   {
/*  906 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected ComponentListener createComponentListener()
/*      */   {
/*  915 */     return new ComponentHandler();
/*      */   }
/*      */ 
/*      */   protected TreeExpansionListener createTreeExpansionListener()
/*      */   {
/*  923 */     return getHandler();
/*      */   }
/*      */ 
/*      */   protected AbstractLayoutCache createLayoutCache()
/*      */   {
/*  931 */     if ((isLargeModel()) && (getRowHeight() > 0)) {
/*  932 */       return new FixedHeightLayoutCache();
/*      */     }
/*  934 */     return new VariableHeightLayoutCache();
/*      */   }
/*      */ 
/*      */   protected CellRendererPane createCellRendererPane()
/*      */   {
/*  941 */     return new CellRendererPane();
/*      */   }
/*      */ 
/*      */   protected TreeCellEditor createDefaultCellEditor()
/*      */   {
/*  948 */     if ((this.currentCellRenderer != null) && ((this.currentCellRenderer instanceof DefaultTreeCellRenderer)))
/*      */     {
/*  950 */       DefaultTreeCellEditor localDefaultTreeCellEditor = new DefaultTreeCellEditor(this.tree, (DefaultTreeCellRenderer)this.currentCellRenderer);
/*      */ 
/*  953 */       return localDefaultTreeCellEditor;
/*      */     }
/*  955 */     return new DefaultTreeCellEditor(this.tree, null);
/*      */   }
/*      */ 
/*      */   protected TreeCellRenderer createDefaultCellRenderer()
/*      */   {
/*  963 */     return new DefaultTreeCellRenderer();
/*      */   }
/*      */ 
/*      */   protected TreeModelListener createTreeModelListener()
/*      */   {
/*  970 */     return getHandler();
/*      */   }
/*      */ 
/*      */   public void uninstallUI(JComponent paramJComponent)
/*      */   {
/*  978 */     completeEditing();
/*      */ 
/*  980 */     prepareForUIUninstall();
/*      */ 
/*  982 */     uninstallDefaults();
/*  983 */     uninstallListeners();
/*  984 */     uninstallKeyboardActions();
/*  985 */     uninstallComponents();
/*      */ 
/*  987 */     completeUIUninstall();
/*      */   }
/*      */ 
/*      */   protected void prepareForUIUninstall() {
/*      */   }
/*      */ 
/*      */   protected void completeUIUninstall() {
/*  994 */     if (this.createdRenderer) {
/*  995 */       this.tree.setCellRenderer(null);
/*      */     }
/*  997 */     if (this.createdCellEditor) {
/*  998 */       this.tree.setCellEditor(null);
/*      */     }
/* 1000 */     this.cellEditor = null;
/* 1001 */     this.currentCellRenderer = null;
/* 1002 */     this.rendererPane = null;
/* 1003 */     this.componentListener = null;
/* 1004 */     this.propertyChangeListener = null;
/* 1005 */     this.mouseListener = null;
/* 1006 */     this.focusListener = null;
/* 1007 */     this.keyListener = null;
/* 1008 */     setSelectionModel(null);
/* 1009 */     this.treeState = null;
/* 1010 */     this.drawingCache = null;
/* 1011 */     this.selectionModelPropertyChangeListener = null;
/* 1012 */     this.tree = null;
/* 1013 */     this.treeModel = null;
/* 1014 */     this.treeSelectionModel = null;
/* 1015 */     this.treeSelectionListener = null;
/* 1016 */     this.treeExpansionListener = null;
/*      */   }
/*      */ 
/*      */   protected void uninstallDefaults() {
/* 1020 */     if ((this.tree.getTransferHandler() instanceof UIResource))
/* 1021 */       this.tree.setTransferHandler(null);
/*      */   }
/*      */ 
/*      */   protected void uninstallListeners()
/*      */   {
/* 1026 */     if (this.componentListener != null) {
/* 1027 */       this.tree.removeComponentListener(this.componentListener);
/*      */     }
/* 1029 */     if (this.propertyChangeListener != null) {
/* 1030 */       this.tree.removePropertyChangeListener(this.propertyChangeListener);
/*      */     }
/* 1032 */     if (this.mouseListener != null) {
/* 1033 */       this.tree.removeMouseListener(this.mouseListener);
/* 1034 */       if ((this.mouseListener instanceof MouseMotionListener)) {
/* 1035 */         this.tree.removeMouseMotionListener((MouseMotionListener)this.mouseListener);
/*      */       }
/*      */     }
/* 1038 */     if (this.focusListener != null) {
/* 1039 */       this.tree.removeFocusListener(this.focusListener);
/*      */     }
/* 1041 */     if (this.keyListener != null) {
/* 1042 */       this.tree.removeKeyListener(this.keyListener);
/*      */     }
/* 1044 */     if (this.treeExpansionListener != null) {
/* 1045 */       this.tree.removeTreeExpansionListener(this.treeExpansionListener);
/*      */     }
/* 1047 */     if ((this.treeModel != null) && (this.treeModelListener != null)) {
/* 1048 */       this.treeModel.removeTreeModelListener(this.treeModelListener);
/*      */     }
/* 1050 */     if ((this.selectionModelPropertyChangeListener != null) && (this.treeSelectionModel != null))
/*      */     {
/* 1052 */       this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener);
/*      */     }
/*      */ 
/* 1055 */     if ((this.treeSelectionListener != null) && (this.treeSelectionModel != null)) {
/* 1056 */       this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener);
/*      */     }
/*      */ 
/* 1059 */     this.handler = null;
/*      */   }
/*      */ 
/*      */   protected void uninstallKeyboardActions() {
/* 1063 */     SwingUtilities.replaceUIActionMap(this.tree, null);
/* 1064 */     SwingUtilities.replaceUIInputMap(this.tree, 1, null);
/*      */ 
/* 1067 */     SwingUtilities.replaceUIInputMap(this.tree, 0, null);
/*      */   }
/*      */ 
/*      */   protected void uninstallComponents()
/*      */   {
/* 1074 */     if (this.rendererPane != null)
/* 1075 */       this.tree.remove(this.rendererPane);
/*      */   }
/*      */ 
/*      */   private void redoTheLayout()
/*      */   {
/* 1083 */     if (this.treeState != null)
/* 1084 */       this.treeState.invalidateSizes();
/*      */   }
/*      */ 
/*      */   public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
/*      */   {
/* 1097 */     super.getBaseline(paramJComponent, paramInt1, paramInt2);
/* 1098 */     UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
/* 1099 */     Component localComponent = (Component)localUIDefaults.get(BASELINE_COMPONENT_KEY);
/*      */ 
/* 1101 */     if (localComponent == null) {
/* 1102 */       TreeCellRenderer localTreeCellRenderer = createDefaultCellRenderer();
/* 1103 */       localComponent = localTreeCellRenderer.getTreeCellRendererComponent(this.tree, "a", false, false, false, -1, false);
/*      */ 
/* 1105 */       localUIDefaults.put(BASELINE_COMPONENT_KEY, localComponent);
/*      */     }
/* 1107 */     int i = this.tree.getRowHeight();
/*      */     int j;
/* 1109 */     if (i > 0) {
/* 1110 */       j = localComponent.getBaseline(2147483647, i);
/*      */     }
/*      */     else {
/* 1113 */       Dimension localDimension = localComponent.getPreferredSize();
/* 1114 */       j = localComponent.getBaseline(localDimension.width, localDimension.height);
/*      */     }
/* 1116 */     return j + this.tree.getInsets().top;
/*      */   }
/*      */ 
/*      */   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
/*      */   {
/* 1129 */     super.getBaselineResizeBehavior(paramJComponent);
/* 1130 */     return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
/*      */   }
/*      */ 
/*      */   public void paint(Graphics paramGraphics, JComponent paramJComponent)
/*      */   {
/* 1138 */     if (this.tree != paramJComponent) {
/* 1139 */       throw new InternalError("incorrect component");
/*      */     }
/*      */ 
/* 1143 */     if (this.treeState == null) {
/* 1144 */       return;
/*      */     }
/*      */ 
/* 1147 */     Rectangle localRectangle1 = paramGraphics.getClipBounds();
/* 1148 */     Insets localInsets = this.tree.getInsets();
/* 1149 */     TreePath localTreePath1 = getClosestPathForLocation(this.tree, 0, localRectangle1.y);
/*      */ 
/* 1151 */     Enumeration localEnumeration = this.treeState.getVisiblePathsFrom(localTreePath1);
/*      */ 
/* 1153 */     int i = this.treeState.getRowForPath(localTreePath1);
/* 1154 */     int j = localRectangle1.y + localRectangle1.height;
/*      */ 
/* 1156 */     this.drawingCache.clear();
/*      */ 
/* 1158 */     if ((localTreePath1 != null) && (localEnumeration != null)) {
/* 1159 */       TreePath localTreePath2 = localTreePath1;
/*      */ 
/* 1164 */       localTreePath2 = localTreePath2.getParentPath();
/* 1165 */       while (localTreePath2 != null) {
/* 1166 */         paintVerticalPartOfLeg(paramGraphics, localRectangle1, localInsets, localTreePath2);
/* 1167 */         this.drawingCache.put(localTreePath2, Boolean.TRUE);
/* 1168 */         localTreePath2 = localTreePath2.getParentPath();
/*      */       }
/*      */ 
/* 1171 */       int k = 0;
/*      */ 
/* 1176 */       Rectangle localRectangle2 = new Rectangle();
/*      */ 
/* 1179 */       boolean bool4 = isRootVisible();
/*      */ 
/* 1181 */       while ((k == 0) && (localEnumeration.hasMoreElements())) {
/* 1182 */         TreePath localTreePath3 = (TreePath)localEnumeration.nextElement();
/* 1183 */         if (localTreePath3 != null) {
/* 1184 */           boolean bool3 = this.treeModel.isLeaf(localTreePath3.getLastPathComponent());
/*      */           boolean bool2;
/*      */           boolean bool1;
/* 1185 */           if (bool3) {
/* 1186 */             bool1 = bool2 = 0;
/*      */           } else {
/* 1188 */             bool1 = this.treeState.getExpandedState(localTreePath3);
/* 1189 */             bool2 = this.tree.hasBeenExpanded(localTreePath3);
/*      */           }
/* 1191 */           Rectangle localRectangle3 = getPathBounds(localTreePath3, localInsets, localRectangle2);
/* 1192 */           if (localRectangle3 == null)
/*      */           {
/* 1197 */             return;
/*      */           }
/* 1199 */           localTreePath2 = localTreePath3.getParentPath();
/* 1200 */           if (localTreePath2 != null) {
/* 1201 */             if (this.drawingCache.get(localTreePath2) == null) {
/* 1202 */               paintVerticalPartOfLeg(paramGraphics, localRectangle1, localInsets, localTreePath2);
/*      */ 
/* 1204 */               this.drawingCache.put(localTreePath2, Boolean.TRUE);
/*      */             }
/* 1206 */             paintHorizontalPartOfLeg(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
/*      */           }
/* 1211 */           else if ((bool4) && (i == 0)) {
/* 1212 */             paintHorizontalPartOfLeg(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
/*      */           }
/*      */ 
/* 1217 */           if (shouldPaintExpandControl(localTreePath3, i, bool1, bool2, bool3))
/*      */           {
/* 1219 */             paintExpandControl(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
/*      */           }
/*      */ 
/* 1223 */           paintRow(paramGraphics, localRectangle1, localInsets, localRectangle3, localTreePath3, i, bool1, bool2, bool3);
/*      */ 
/* 1225 */           if (localRectangle3.y + localRectangle3.height >= j)
/* 1226 */             k = 1;
/*      */         }
/*      */         else {
/* 1229 */           k = 1;
/*      */         }
/* 1231 */         i++;
/*      */       }
/*      */     }
/*      */ 
/* 1235 */     paintDropLine(paramGraphics);
/*      */ 
/* 1238 */     this.rendererPane.removeAll();
/*      */ 
/* 1240 */     this.drawingCache.clear();
/*      */   }
/*      */ 
/*      */   protected boolean isDropLine(JTree.DropLocation paramDropLocation)
/*      */   {
/* 1253 */     return (paramDropLocation != null) && (paramDropLocation.getPath() != null) && (paramDropLocation.getChildIndex() != -1);
/*      */   }
/*      */ 
/*      */   protected void paintDropLine(Graphics paramGraphics)
/*      */   {
/* 1263 */     JTree.DropLocation localDropLocation = this.tree.getDropLocation();
/* 1264 */     if (!isDropLine(localDropLocation)) {
/* 1265 */       return;
/*      */     }
/*      */ 
/* 1268 */     Color localColor = UIManager.getColor("Tree.dropLineColor");
/* 1269 */     if (localColor != null) {
/* 1270 */       paramGraphics.setColor(localColor);
/* 1271 */       Rectangle localRectangle = getDropLineRect(localDropLocation);
/* 1272 */       paramGraphics.fillRect(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Rectangle getDropLineRect(JTree.DropLocation paramDropLocation)
/*      */   {
/* 1285 */     TreePath localTreePath1 = paramDropLocation.getPath();
/* 1286 */     int i = paramDropLocation.getChildIndex();
/* 1287 */     boolean bool = this.leftToRight;
/*      */ 
/* 1289 */     Insets localInsets = this.tree.getInsets();
/*      */     Rectangle localRectangle1;
/* 1291 */     if (this.tree.getRowCount() == 0) {
/* 1292 */       localRectangle1 = new Rectangle(localInsets.left, localInsets.top, this.tree.getWidth() - localInsets.left - localInsets.right, 0);
/*      */     }
/*      */     else
/*      */     {
/* 1297 */       TreeModel localTreeModel = getModel();
/* 1298 */       Object localObject = localTreeModel.getRoot();
/*      */ 
/* 1300 */       if ((localTreePath1.getLastPathComponent() == localObject) && (i >= localTreeModel.getChildCount(localObject)))
/*      */       {
/* 1303 */         localRectangle1 = this.tree.getRowBounds(this.tree.getRowCount() - 1);
/* 1304 */         localRectangle1.y += localRectangle1.height;
/*      */         Rectangle localRectangle2;
/* 1307 */         if (!this.tree.isRootVisible()) {
/* 1308 */           localRectangle2 = this.tree.getRowBounds(0);
/* 1309 */         } else if (localTreeModel.getChildCount(localObject) == 0) {
/* 1310 */           localRectangle2 = this.tree.getRowBounds(0);
/* 1311 */           localRectangle2.x += this.totalChildIndent;
/* 1312 */           localRectangle2.width -= this.totalChildIndent + this.totalChildIndent;
/*      */         } else {
/* 1314 */           TreePath localTreePath2 = localTreePath1.pathByAddingChild(localTreeModel.getChild(localObject, localTreeModel.getChildCount(localObject) - 1));
/*      */ 
/* 1316 */           localRectangle2 = this.tree.getPathBounds(localTreePath2);
/*      */         }
/*      */ 
/* 1319 */         localRectangle1.x = localRectangle2.x;
/* 1320 */         localRectangle1.width = localRectangle2.width;
/*      */       } else {
/* 1322 */         localRectangle1 = this.tree.getPathBounds(localTreePath1.pathByAddingChild(localTreeModel.getChild(localTreePath1.getLastPathComponent(), i)));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1327 */     if (localRectangle1.y != 0) {
/* 1328 */       localRectangle1.y -= 1;
/*      */     }
/*      */ 
/* 1331 */     if (!bool) {
/* 1332 */       localRectangle1.x = (localRectangle1.x + localRectangle1.width - 100);
/*      */     }
/*      */ 
/* 1335 */     localRectangle1.width = 100;
/* 1336 */     localRectangle1.height = 2;
/*      */ 
/* 1338 */     return localRectangle1;
/*      */   }
/*      */ 
/*      */   protected void paintHorizontalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 1352 */     if (!this.paintLines) {
/* 1353 */       return;
/*      */     }
/*      */ 
/* 1357 */     int i = paramTreePath.getPathCount() - 1;
/* 1358 */     if (((i == 0) || ((i == 1) && (!isRootVisible()))) && (!getShowsRootHandles()))
/*      */     {
/* 1360 */       return;
/*      */     }
/*      */ 
/* 1363 */     int j = paramRectangle1.x;
/* 1364 */     int k = paramRectangle1.x + paramRectangle1.width;
/* 1365 */     int m = paramRectangle1.y;
/* 1366 */     int n = paramRectangle1.y + paramRectangle1.height;
/* 1367 */     int i1 = paramRectangle2.y + paramRectangle2.height / 2;
/*      */     int i2;
/*      */     int i3;
/* 1369 */     if (this.leftToRight) {
/* 1370 */       i2 = paramRectangle2.x - getRightChildIndent();
/* 1371 */       i3 = paramRectangle2.x - getHorizontalLegBuffer();
/*      */ 
/* 1373 */       if ((i1 >= m) && (i1 < n) && (i3 >= j) && (i2 < k) && (i2 < i3))
/*      */       {
/* 1379 */         paramGraphics.setColor(getHashColor());
/* 1380 */         paintHorizontalLine(paramGraphics, this.tree, i1, i2, i3 - 1);
/*      */       }
/*      */     } else {
/* 1383 */       i2 = paramRectangle2.x + paramRectangle2.width + getHorizontalLegBuffer();
/* 1384 */       i3 = paramRectangle2.x + paramRectangle2.width + getRightChildIndent();
/*      */ 
/* 1386 */       if ((i1 >= m) && (i1 < n) && (i3 >= j) && (i2 < k) && (i2 < i3))
/*      */       {
/* 1392 */         paramGraphics.setColor(getHashColor());
/* 1393 */         paintHorizontalLine(paramGraphics, this.tree, i1, i2, i3 - 1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintVerticalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle, Insets paramInsets, TreePath paramTreePath)
/*      */   {
/* 1404 */     if (!this.paintLines) {
/* 1405 */       return;
/*      */     }
/*      */ 
/* 1408 */     int i = paramTreePath.getPathCount() - 1;
/* 1409 */     if ((i == 0) && (!getShowsRootHandles()) && (!isRootVisible())) {
/* 1410 */       return;
/*      */     }
/* 1412 */     int j = getRowX(-1, i + 1);
/* 1413 */     if (this.leftToRight) {
/* 1414 */       j = j - getRightChildIndent() + paramInsets.left;
/*      */     }
/*      */     else {
/* 1417 */       j = this.tree.getWidth() - j - paramInsets.right + getRightChildIndent() - 1;
/*      */     }
/*      */ 
/* 1420 */     int k = paramRectangle.x;
/* 1421 */     int m = paramRectangle.x + (paramRectangle.width - 1);
/*      */ 
/* 1423 */     if ((j >= k) && (j <= m)) {
/* 1424 */       int n = paramRectangle.y;
/* 1425 */       int i1 = paramRectangle.y + paramRectangle.height;
/* 1426 */       Rectangle localRectangle1 = getPathBounds(this.tree, paramTreePath);
/* 1427 */       Rectangle localRectangle2 = getPathBounds(this.tree, getLastChildPath(paramTreePath));
/*      */ 
/* 1430 */       if (localRectangle2 == null)
/*      */         return;
/*      */       int i2;
/* 1439 */       if (localRectangle1 == null) {
/* 1440 */         i2 = Math.max(paramInsets.top + getVerticalLegBuffer(), n);
/*      */       }
/*      */       else
/*      */       {
/* 1444 */         i2 = Math.max(localRectangle1.y + localRectangle1.height + getVerticalLegBuffer(), n);
/*      */       }
/* 1446 */       if ((i == 0) && (!isRootVisible())) {
/* 1447 */         TreeModel localTreeModel = getModel();
/*      */ 
/* 1449 */         if (localTreeModel != null) {
/* 1450 */           Object localObject = localTreeModel.getRoot();
/*      */ 
/* 1452 */           if (localTreeModel.getChildCount(localObject) > 0) {
/* 1453 */             localRectangle1 = getPathBounds(this.tree, paramTreePath.pathByAddingChild(localTreeModel.getChild(localObject, 0)));
/*      */ 
/* 1455 */             if (localRectangle1 != null) {
/* 1456 */               i2 = Math.max(paramInsets.top + getVerticalLegBuffer(), localRectangle1.y + localRectangle1.height / 2);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1463 */       int i3 = Math.min(localRectangle2.y + localRectangle2.height / 2, i1);
/*      */ 
/* 1466 */       if (i2 <= i3) {
/* 1467 */         paramGraphics.setColor(getHashColor());
/* 1468 */         paintVerticalLine(paramGraphics, this.tree, j, i2, i3);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintExpandControl(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 1483 */     Object localObject = paramTreePath.getLastPathComponent();
/*      */ 
/* 1487 */     if ((!paramBoolean3) && ((!paramBoolean2) || (this.treeModel.getChildCount(localObject) > 0)))
/*      */     {
/*      */       int i;
/* 1490 */       if (this.leftToRight)
/* 1491 */         i = paramRectangle2.x - getRightChildIndent() + 1;
/*      */       else {
/* 1493 */         i = paramRectangle2.x + paramRectangle2.width + getRightChildIndent() - 1;
/*      */       }
/* 1495 */       int j = paramRectangle2.y + paramRectangle2.height / 2;
/*      */       Icon localIcon;
/* 1497 */       if (paramBoolean1) {
/* 1498 */         localIcon = getExpandedIcon();
/* 1499 */         if (localIcon != null)
/* 1500 */           drawCentered(this.tree, paramGraphics, localIcon, i, j);
/*      */       }
/*      */       else
/*      */       {
/* 1504 */         localIcon = getCollapsedIcon();
/* 1505 */         if (localIcon != null)
/* 1506 */           drawCentered(this.tree, paramGraphics, localIcon, i, j);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void paintRow(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 1521 */     if ((this.editingComponent != null) && (this.editingRow == paramInt))
/*      */       return;
/*      */     int i;
/* 1526 */     if (this.tree.hasFocus()) {
/* 1527 */       i = getLeadSelectionRow();
/*      */     }
/*      */     else {
/* 1530 */       i = -1;
/*      */     }
/*      */ 
/* 1534 */     Component localComponent = this.currentCellRenderer.getTreeCellRendererComponent(this.tree, paramTreePath.getLastPathComponent(), this.tree.isRowSelected(paramInt), paramBoolean1, paramBoolean3, paramInt, i == paramInt);
/*      */ 
/* 1539 */     this.rendererPane.paintComponent(paramGraphics, localComponent, this.tree, paramRectangle2.x, paramRectangle2.y, paramRectangle2.width, paramRectangle2.height, true);
/*      */   }
/*      */ 
/*      */   protected boolean shouldPaintExpandControl(TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 1551 */     if (paramBoolean3) {
/* 1552 */       return false;
/*      */     }
/* 1554 */     int i = paramTreePath.getPathCount() - 1;
/*      */ 
/* 1556 */     if (((i == 0) || ((i == 1) && (!isRootVisible()))) && (!getShowsRootHandles()))
/*      */     {
/* 1558 */       return false;
/* 1559 */     }return true;
/*      */   }
/*      */ 
/*      */   protected void paintVerticalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1567 */     if (this.lineTypeDashed)
/* 1568 */       drawDashedVerticalLine(paramGraphics, paramInt1, paramInt2, paramInt3);
/*      */     else
/* 1570 */       paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt3);
/*      */   }
/*      */ 
/*      */   protected void paintHorizontalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1579 */     if (this.lineTypeDashed)
/* 1580 */       drawDashedHorizontalLine(paramGraphics, paramInt1, paramInt2, paramInt3);
/*      */     else
/* 1582 */       paramGraphics.drawLine(paramInt2, paramInt1, paramInt3, paramInt1);
/*      */   }
/*      */ 
/*      */   protected int getVerticalLegBuffer()
/*      */   {
/* 1591 */     return 0;
/*      */   }
/*      */ 
/*      */   protected int getHorizontalLegBuffer()
/*      */   {
/* 1600 */     return 0;
/*      */   }
/*      */ 
/*      */   private int findCenteredX(int paramInt1, int paramInt2) {
/* 1604 */     return this.leftToRight ? paramInt1 - (int)Math.ceil(paramInt2 / 2.0D) : paramInt1 - (int)Math.floor(paramInt2 / 2.0D);
/*      */   }
/*      */ 
/*      */   protected void drawCentered(Component paramComponent, Graphics paramGraphics, Icon paramIcon, int paramInt1, int paramInt2)
/*      */   {
/* 1616 */     paramIcon.paintIcon(paramComponent, paramGraphics, findCenteredX(paramInt1, paramIcon.getIconWidth()), paramInt2 - paramIcon.getIconHeight() / 2);
/*      */   }
/*      */ 
/*      */   protected void drawDashedHorizontalLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1627 */     paramInt2 += paramInt2 % 2;
/*      */ 
/* 1629 */     for (int i = paramInt2; i <= paramInt3; i += 2)
/* 1630 */       paramGraphics.drawLine(i, paramInt1, i, paramInt1);
/*      */   }
/*      */ 
/*      */   protected void drawDashedVerticalLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1640 */     paramInt2 += paramInt2 % 2;
/*      */ 
/* 1642 */     for (int i = paramInt2; i <= paramInt3; i += 2)
/* 1643 */       paramGraphics.drawLine(paramInt1, i, paramInt1, i);
/*      */   }
/*      */ 
/*      */   protected int getRowX(int paramInt1, int paramInt2)
/*      */   {
/* 1664 */     return this.totalChildIndent * (paramInt2 + this.depthOffset);
/*      */   }
/*      */ 
/*      */   protected void updateLayoutCacheExpandedNodes()
/*      */   {
/* 1672 */     if ((this.treeModel != null) && (this.treeModel.getRoot() != null))
/* 1673 */       updateExpandedDescendants(new TreePath(this.treeModel.getRoot()));
/*      */   }
/*      */ 
/*      */   private void updateLayoutCacheExpandedNodesIfNecessary() {
/* 1677 */     if ((this.treeModel != null) && (this.treeModel.getRoot() != null)) {
/* 1678 */       TreePath localTreePath = new TreePath(this.treeModel.getRoot());
/* 1679 */       if (this.tree.isExpanded(localTreePath))
/* 1680 */         updateLayoutCacheExpandedNodes();
/*      */       else
/* 1682 */         this.treeState.setExpandedState(localTreePath, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void updateExpandedDescendants(TreePath paramTreePath)
/*      */   {
/* 1693 */     completeEditing();
/* 1694 */     if (this.treeState != null) {
/* 1695 */       this.treeState.setExpandedState(paramTreePath, true);
/*      */ 
/* 1697 */       Enumeration localEnumeration = this.tree.getExpandedDescendants(paramTreePath);
/*      */ 
/* 1699 */       if (localEnumeration != null) {
/* 1700 */         while (localEnumeration.hasMoreElements()) {
/* 1701 */           paramTreePath = (TreePath)localEnumeration.nextElement();
/* 1702 */           this.treeState.setExpandedState(paramTreePath, true);
/*      */         }
/*      */       }
/* 1705 */       updateLeadSelectionRow();
/* 1706 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected TreePath getLastChildPath(TreePath paramTreePath)
/*      */   {
/* 1714 */     if (this.treeModel != null) {
/* 1715 */       int i = this.treeModel.getChildCount(paramTreePath.getLastPathComponent());
/*      */ 
/* 1718 */       if (i > 0) {
/* 1719 */         return paramTreePath.pathByAddingChild(this.treeModel.getChild(paramTreePath.getLastPathComponent(), i - 1));
/*      */       }
/*      */     }
/* 1722 */     return null;
/*      */   }
/*      */ 
/*      */   protected void updateDepthOffset()
/*      */   {
/* 1729 */     if (isRootVisible()) {
/* 1730 */       if (getShowsRootHandles())
/* 1731 */         this.depthOffset = 1;
/*      */       else
/* 1733 */         this.depthOffset = 0;
/*      */     }
/* 1735 */     else if (!getShowsRootHandles())
/* 1736 */       this.depthOffset = -1;
/*      */     else
/* 1738 */       this.depthOffset = 0;
/*      */   }
/*      */ 
/*      */   protected void updateCellEditor()
/*      */   {
/* 1749 */     completeEditing();
/*      */     TreeCellEditor localTreeCellEditor;
/* 1750 */     if (this.tree == null) {
/* 1751 */       localTreeCellEditor = null;
/*      */     }
/* 1753 */     else if (this.tree.isEditable()) {
/* 1754 */       localTreeCellEditor = this.tree.getCellEditor();
/* 1755 */       if (localTreeCellEditor == null) {
/* 1756 */         localTreeCellEditor = createDefaultCellEditor();
/* 1757 */         if (localTreeCellEditor != null) {
/* 1758 */           this.tree.setCellEditor(localTreeCellEditor);
/* 1759 */           this.createdCellEditor = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1764 */       localTreeCellEditor = null;
/*      */     }
/* 1766 */     if (localTreeCellEditor != this.cellEditor) {
/* 1767 */       if ((this.cellEditor != null) && (this.cellEditorListener != null))
/* 1768 */         this.cellEditor.removeCellEditorListener(this.cellEditorListener);
/* 1769 */       this.cellEditor = localTreeCellEditor;
/* 1770 */       if (this.cellEditorListener == null)
/* 1771 */         this.cellEditorListener = createCellEditorListener();
/* 1772 */       if ((localTreeCellEditor != null) && (this.cellEditorListener != null))
/* 1773 */         localTreeCellEditor.addCellEditorListener(this.cellEditorListener);
/* 1774 */       this.createdCellEditor = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void updateRenderer()
/*      */   {
/* 1782 */     if (this.tree != null)
/*      */     {
/* 1785 */       TreeCellRenderer localTreeCellRenderer = this.tree.getCellRenderer();
/* 1786 */       if (localTreeCellRenderer == null) {
/* 1787 */         this.tree.setCellRenderer(createDefaultCellRenderer());
/* 1788 */         this.createdRenderer = true;
/*      */       }
/*      */       else {
/* 1791 */         this.createdRenderer = false;
/* 1792 */         this.currentCellRenderer = localTreeCellRenderer;
/* 1793 */         if (this.createdCellEditor)
/* 1794 */           this.tree.setCellEditor(null);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1799 */       this.createdRenderer = false;
/* 1800 */       this.currentCellRenderer = null;
/*      */     }
/* 1802 */     updateCellEditor();
/*      */   }
/*      */ 
/*      */   protected void configureLayoutCache()
/*      */   {
/* 1810 */     if ((this.treeState != null) && (this.tree != null)) {
/* 1811 */       if (this.nodeDimensions == null)
/* 1812 */         this.nodeDimensions = createNodeDimensions();
/* 1813 */       this.treeState.setNodeDimensions(this.nodeDimensions);
/* 1814 */       this.treeState.setRootVisible(this.tree.isRootVisible());
/* 1815 */       this.treeState.setRowHeight(this.tree.getRowHeight());
/* 1816 */       this.treeState.setSelectionModel(getSelectionModel());
/*      */ 
/* 1819 */       if (this.treeState.getModel() != this.tree.getModel())
/* 1820 */         this.treeState.setModel(this.tree.getModel());
/* 1821 */       updateLayoutCacheExpandedNodesIfNecessary();
/*      */ 
/* 1824 */       if (isLargeModel()) {
/* 1825 */         if (this.componentListener == null) {
/* 1826 */           this.componentListener = createComponentListener();
/* 1827 */           if (this.componentListener != null)
/* 1828 */             this.tree.addComponentListener(this.componentListener);
/*      */         }
/*      */       }
/* 1831 */       else if (this.componentListener != null) {
/* 1832 */         this.tree.removeComponentListener(this.componentListener);
/* 1833 */         this.componentListener = null;
/*      */       }
/*      */     }
/* 1836 */     else if (this.componentListener != null) {
/* 1837 */       this.tree.removeComponentListener(this.componentListener);
/* 1838 */       this.componentListener = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void updateSize()
/*      */   {
/* 1847 */     this.validCachedPreferredSize = false;
/* 1848 */     this.tree.treeDidChange();
/*      */   }
/*      */ 
/*      */   private void updateSize0() {
/* 1852 */     this.validCachedPreferredSize = false;
/* 1853 */     this.tree.revalidate();
/*      */   }
/*      */ 
/*      */   protected void updateCachedPreferredSize()
/*      */   {
/* 1864 */     if (this.treeState != null) {
/* 1865 */       Insets localInsets = this.tree.getInsets();
/*      */ 
/* 1867 */       if (isLargeModel()) {
/* 1868 */         Rectangle localRectangle = this.tree.getVisibleRect();
/*      */ 
/* 1870 */         if ((localRectangle.x == 0) && (localRectangle.y == 0) && (localRectangle.width == 0) && (localRectangle.height == 0) && (this.tree.getVisibleRowCount() > 0))
/*      */         {
/* 1875 */           localRectangle.width = 1;
/* 1876 */           localRectangle.height = (this.tree.getRowHeight() * this.tree.getVisibleRowCount());
/*      */         }
/*      */         else {
/* 1879 */           localRectangle.x -= localInsets.left;
/* 1880 */           localRectangle.y -= localInsets.top;
/*      */         }
/* 1882 */         this.preferredSize.width = this.treeState.getPreferredWidth(localRectangle);
/*      */       }
/*      */       else {
/* 1885 */         this.preferredSize.width = this.treeState.getPreferredWidth(null);
/*      */       }
/* 1887 */       this.preferredSize.height = this.treeState.getPreferredHeight();
/* 1888 */       this.preferredSize.width += localInsets.left + localInsets.right;
/* 1889 */       this.preferredSize.height += localInsets.top + localInsets.bottom;
/*      */     }
/* 1891 */     this.validCachedPreferredSize = true;
/*      */   }
/*      */ 
/*      */   protected void pathWasExpanded(TreePath paramTreePath)
/*      */   {
/* 1898 */     if (this.tree != null)
/* 1899 */       this.tree.fireTreeExpanded(paramTreePath);
/*      */   }
/*      */ 
/*      */   protected void pathWasCollapsed(TreePath paramTreePath)
/*      */   {
/* 1907 */     if (this.tree != null)
/* 1908 */       this.tree.fireTreeCollapsed(paramTreePath);
/*      */   }
/*      */ 
/*      */   protected void ensureRowsAreVisible(int paramInt1, int paramInt2)
/*      */   {
/* 1917 */     if ((this.tree != null) && (paramInt1 >= 0) && (paramInt2 < getRowCount(this.tree))) {
/* 1918 */       boolean bool = DefaultLookup.getBoolean(this.tree, this, "Tree.scrollsHorizontallyAndVertically", false);
/*      */       Rectangle localRectangle1;
/* 1920 */       if (paramInt1 == paramInt2) {
/* 1921 */         localRectangle1 = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
/*      */ 
/* 1924 */         if (localRectangle1 != null) {
/* 1925 */           if (!bool) {
/* 1926 */             localRectangle1.x = this.tree.getVisibleRect().x;
/* 1927 */             localRectangle1.width = 1;
/*      */           }
/* 1929 */           this.tree.scrollRectToVisible(localRectangle1);
/*      */         }
/*      */       }
/*      */       else {
/* 1933 */         localRectangle1 = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
/*      */ 
/* 1935 */         if (localRectangle1 != null) {
/* 1936 */           Rectangle localRectangle2 = this.tree.getVisibleRect();
/* 1937 */           Rectangle localRectangle3 = localRectangle1;
/* 1938 */           int i = localRectangle1.y;
/* 1939 */           int j = i + localRectangle2.height;
/*      */ 
/* 1941 */           for (int k = paramInt1 + 1; k <= paramInt2; k++) {
/* 1942 */             localRectangle3 = getPathBounds(this.tree, getPathForRow(this.tree, k));
/*      */ 
/* 1944 */             if (localRectangle3.y + localRectangle3.height > j)
/* 1945 */               k = paramInt2;
/*      */           }
/* 1947 */           this.tree.scrollRectToVisible(new Rectangle(localRectangle2.x, i, 1, localRectangle3.y + localRectangle3.height - i));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPreferredMinSize(Dimension paramDimension)
/*      */   {
/* 1958 */     this.preferredMinSize = paramDimension;
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredMinSize()
/*      */   {
/* 1964 */     if (this.preferredMinSize == null)
/* 1965 */       return null;
/* 1966 */     return new Dimension(this.preferredMinSize);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(JComponent paramJComponent)
/*      */   {
/* 1973 */     return getPreferredSize(paramJComponent, true);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(JComponent paramJComponent, boolean paramBoolean)
/*      */   {
/* 1982 */     Dimension localDimension = getPreferredMinSize();
/*      */ 
/* 1984 */     if (!this.validCachedPreferredSize)
/* 1985 */       updateCachedPreferredSize();
/* 1986 */     if (this.tree != null) {
/* 1987 */       if (localDimension != null) {
/* 1988 */         return new Dimension(Math.max(localDimension.width, this.preferredSize.width), Math.max(localDimension.height, this.preferredSize.height));
/*      */       }
/*      */ 
/* 1991 */       return new Dimension(this.preferredSize.width, this.preferredSize.height);
/*      */     }
/* 1993 */     if (localDimension != null) {
/* 1994 */       return localDimension;
/*      */     }
/* 1996 */     return new Dimension(0, 0);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(JComponent paramJComponent)
/*      */   {
/* 2004 */     if (getPreferredMinSize() != null)
/* 2005 */       return getPreferredMinSize();
/* 2006 */     return new Dimension(0, 0);
/*      */   }
/*      */ 
/*      */   public Dimension getMaximumSize(JComponent paramJComponent)
/*      */   {
/* 2014 */     if (this.tree != null)
/* 2015 */       return getPreferredSize(this.tree);
/* 2016 */     if (getPreferredMinSize() != null)
/* 2017 */       return getPreferredMinSize();
/* 2018 */     return new Dimension(0, 0);
/*      */   }
/*      */ 
/*      */   protected void completeEditing()
/*      */   {
/* 2032 */     if ((this.tree.getInvokesStopCellEditing()) && (this.stopEditingInCompleteEditing) && (this.editingComponent != null))
/*      */     {
/* 2034 */       this.cellEditor.stopCellEditing();
/*      */     }
/*      */ 
/* 2038 */     completeEditing(false, true, false);
/*      */   }
/*      */ 
/*      */   protected void completeEditing(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/* 2050 */     if ((this.stopEditingInCompleteEditing) && (this.editingComponent != null)) {
/* 2051 */       Component localComponent = this.editingComponent;
/* 2052 */       TreePath localTreePath = this.editingPath;
/* 2053 */       TreeCellEditor localTreeCellEditor = this.cellEditor;
/* 2054 */       Object localObject = localTreeCellEditor.getCellEditorValue();
/* 2055 */       Rectangle localRectangle = getPathBounds(this.tree, this.editingPath);
/*      */ 
/* 2057 */       int i = (this.tree != null) && ((this.tree.hasFocus()) || (SwingUtilities.findFocusOwner(this.editingComponent) != null)) ? 1 : 0;
/*      */ 
/* 2061 */       this.editingComponent = null;
/* 2062 */       this.editingPath = null;
/* 2063 */       if (paramBoolean1)
/* 2064 */         localTreeCellEditor.stopCellEditing();
/* 2065 */       else if (paramBoolean2)
/* 2066 */         localTreeCellEditor.cancelCellEditing();
/* 2067 */       this.tree.remove(localComponent);
/* 2068 */       if (this.editorHasDifferentSize) {
/* 2069 */         this.treeState.invalidatePathBounds(localTreePath);
/* 2070 */         updateSize();
/*      */       }
/*      */       else {
/* 2073 */         localRectangle.x = 0;
/* 2074 */         localRectangle.width = this.tree.getSize().width;
/* 2075 */         this.tree.repaint(localRectangle);
/*      */       }
/* 2077 */       if (i != 0)
/* 2078 */         this.tree.requestFocus();
/* 2079 */       if (paramBoolean3)
/* 2080 */         this.treeModel.valueForPathChanged(localTreePath, localObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean startEditingOnRelease(TreePath paramTreePath, MouseEvent paramMouseEvent1, MouseEvent paramMouseEvent2)
/*      */   {
/* 2089 */     this.releaseEvent = paramMouseEvent2;
/*      */     try {
/* 2091 */       return startEditing(paramTreePath, paramMouseEvent1);
/*      */     } finally {
/* 2093 */       this.releaseEvent = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean startEditing(TreePath paramTreePath, MouseEvent paramMouseEvent)
/*      */   {
/* 2103 */     if ((isEditing(this.tree)) && (this.tree.getInvokesStopCellEditing()) && (!stopEditing(this.tree)))
/*      */     {
/* 2105 */       return false;
/*      */     }
/* 2107 */     completeEditing();
/* 2108 */     if ((this.cellEditor != null) && (this.tree.isPathEditable(paramTreePath))) {
/* 2109 */       int i = getRowForPath(this.tree, paramTreePath);
/*      */ 
/* 2111 */       if (this.cellEditor.isCellEditable(paramMouseEvent)) {
/* 2112 */         this.editingComponent = this.cellEditor.getTreeCellEditorComponent(this.tree, paramTreePath.getLastPathComponent(), this.tree.isPathSelected(paramTreePath), this.tree.isExpanded(paramTreePath), this.treeModel.isLeaf(paramTreePath.getLastPathComponent()), i);
/*      */ 
/* 2116 */         Rectangle localRectangle = getPathBounds(this.tree, paramTreePath);
/*      */ 
/* 2118 */         this.editingRow = i;
/*      */ 
/* 2120 */         Dimension localDimension = this.editingComponent.getPreferredSize();
/*      */ 
/* 2123 */         if ((localDimension.height != localRectangle.height) && (getRowHeight() > 0))
/*      */         {
/* 2125 */           localDimension.height = getRowHeight();
/*      */         }
/* 2127 */         if ((localDimension.width != localRectangle.width) || (localDimension.height != localRectangle.height))
/*      */         {
/* 2131 */           this.editorHasDifferentSize = true;
/* 2132 */           this.treeState.invalidatePathBounds(paramTreePath);
/* 2133 */           updateSize();
/*      */ 
/* 2136 */           localRectangle = getPathBounds(this.tree, paramTreePath);
/*      */         }
/*      */         else {
/* 2139 */           this.editorHasDifferentSize = false;
/* 2140 */         }this.tree.add(this.editingComponent);
/* 2141 */         this.editingComponent.setBounds(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */ 
/* 2144 */         this.editingPath = paramTreePath;
/* 2145 */         if ((this.editingComponent instanceof JComponent))
/* 2146 */           ((JComponent)this.editingComponent).revalidate();
/*      */         else {
/* 2148 */           this.editingComponent.validate();
/*      */         }
/* 2150 */         this.editingComponent.repaint();
/* 2151 */         if (this.cellEditor.shouldSelectCell(paramMouseEvent)) {
/* 2152 */           this.stopEditingInCompleteEditing = false;
/* 2153 */           this.tree.setSelectionRow(i);
/* 2154 */           this.stopEditingInCompleteEditing = true;
/*      */         }
/*      */ 
/* 2157 */         Component localComponent1 = SwingUtilities2.compositeRequestFocus(this.editingComponent);
/*      */ 
/* 2159 */         int j = 1;
/*      */ 
/* 2161 */         if (paramMouseEvent != null)
/*      */         {
/* 2164 */           Point localPoint = SwingUtilities.convertPoint(this.tree, new Point(paramMouseEvent.getX(), paramMouseEvent.getY()), this.editingComponent);
/*      */ 
/* 2173 */           Component localComponent2 = SwingUtilities.getDeepestComponentAt(this.editingComponent, localPoint.x, localPoint.y);
/*      */ 
/* 2176 */           if (localComponent2 != null) {
/* 2177 */             MouseInputHandler localMouseInputHandler = new MouseInputHandler(this.tree, localComponent2, paramMouseEvent, localComponent1);
/*      */ 
/* 2181 */             if (this.releaseEvent != null) {
/* 2182 */               localMouseInputHandler.mouseReleased(this.releaseEvent);
/*      */             }
/*      */ 
/* 2185 */             j = 0;
/*      */           }
/*      */         }
/* 2188 */         if ((j != 0) && ((localComponent1 instanceof JTextField))) {
/* 2189 */           ((JTextField)localComponent1).selectAll();
/*      */         }
/* 2191 */         return true;
/*      */       }
/*      */ 
/* 2194 */       this.editingComponent = null;
/*      */     }
/* 2196 */     return false;
/*      */   }
/*      */ 
/*      */   protected void checkForClickInExpandControl(TreePath paramTreePath, int paramInt1, int paramInt2)
/*      */   {
/* 2210 */     if (isLocationInExpandControl(paramTreePath, paramInt1, paramInt2))
/* 2211 */       handleExpandControlClick(paramTreePath, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   protected boolean isLocationInExpandControl(TreePath paramTreePath, int paramInt1, int paramInt2)
/*      */   {
/* 2222 */     if ((paramTreePath != null) && (!this.treeModel.isLeaf(paramTreePath.getLastPathComponent())))
/*      */     {
/* 2224 */       Insets localInsets = this.tree.getInsets();
/*      */       int i;
/* 2226 */       if (getExpandedIcon() != null)
/* 2227 */         i = getExpandedIcon().getIconWidth();
/*      */       else {
/* 2229 */         i = 8;
/*      */       }
/* 2231 */       int j = getRowX(this.tree.getRowForPath(paramTreePath), paramTreePath.getPathCount() - 1);
/*      */ 
/* 2234 */       if (this.leftToRight)
/* 2235 */         j = j + localInsets.left - getRightChildIndent() + 1;
/*      */       else {
/* 2237 */         j = this.tree.getWidth() - j - localInsets.right + getRightChildIndent() - 1;
/*      */       }
/*      */ 
/* 2240 */       j = findCenteredX(j, i);
/*      */ 
/* 2242 */       return (paramInt1 >= j) && (paramInt1 < j + i);
/*      */     }
/* 2244 */     return false;
/*      */   }
/*      */ 
/*      */   protected void handleExpandControlClick(TreePath paramTreePath, int paramInt1, int paramInt2)
/*      */   {
/* 2253 */     toggleExpandState(paramTreePath);
/*      */   }
/*      */ 
/*      */   protected void toggleExpandState(TreePath paramTreePath)
/*      */   {
/* 2263 */     if (!this.tree.isExpanded(paramTreePath)) {
/* 2264 */       int i = getRowForPath(this.tree, paramTreePath);
/*      */ 
/* 2266 */       this.tree.expandPath(paramTreePath);
/* 2267 */       updateSize();
/* 2268 */       if (i != -1)
/* 2269 */         if (this.tree.getScrollsOnExpand()) {
/* 2270 */           ensureRowsAreVisible(i, i + this.treeState.getVisibleChildCount(paramTreePath));
/*      */         }
/*      */         else
/* 2273 */           ensureRowsAreVisible(i, i);
/*      */     }
/*      */     else
/*      */     {
/* 2277 */       this.tree.collapsePath(paramTreePath);
/* 2278 */       updateSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isToggleSelectionEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 2287 */     return (SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent));
/*      */   }
/*      */ 
/*      */   protected boolean isMultiSelectEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 2296 */     return (SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (paramMouseEvent.isShiftDown());
/*      */   }
/*      */ 
/*      */   protected boolean isToggleEvent(MouseEvent paramMouseEvent)
/*      */   {
/* 2306 */     if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
/* 2307 */       return false;
/*      */     }
/* 2309 */     int i = this.tree.getToggleClickCount();
/*      */ 
/* 2311 */     if (i <= 0) {
/* 2312 */       return false;
/*      */     }
/* 2314 */     return paramMouseEvent.getClickCount() % i == 0;
/*      */   }
/*      */ 
/*      */   protected void selectPathForEvent(TreePath paramTreePath, MouseEvent paramMouseEvent)
/*      */   {
/* 2327 */     if (isMultiSelectEvent(paramMouseEvent)) {
/* 2328 */       TreePath localTreePath1 = getAnchorSelectionPath();
/* 2329 */       int i = localTreePath1 == null ? -1 : getRowForPath(this.tree, localTreePath1);
/*      */ 
/* 2332 */       if ((i == -1) || (this.tree.getSelectionModel().getSelectionMode() == 1))
/*      */       {
/* 2335 */         this.tree.setSelectionPath(paramTreePath);
/*      */       }
/*      */       else {
/* 2338 */         int j = getRowForPath(this.tree, paramTreePath);
/* 2339 */         TreePath localTreePath2 = localTreePath1;
/*      */ 
/* 2341 */         if (isToggleSelectionEvent(paramMouseEvent)) {
/* 2342 */           if (this.tree.isRowSelected(i)) {
/* 2343 */             this.tree.addSelectionInterval(i, j);
/*      */           } else {
/* 2345 */             this.tree.removeSelectionInterval(i, j);
/* 2346 */             this.tree.addSelectionInterval(j, j);
/*      */           }
/* 2348 */         } else if (j < i)
/* 2349 */           this.tree.setSelectionInterval(j, i);
/*      */         else {
/* 2351 */           this.tree.setSelectionInterval(i, j);
/*      */         }
/* 2353 */         this.lastSelectedRow = j;
/* 2354 */         setAnchorSelectionPath(localTreePath2);
/* 2355 */         setLeadSelectionPath(paramTreePath);
/*      */       }
/*      */ 
/*      */     }
/* 2361 */     else if (isToggleSelectionEvent(paramMouseEvent)) {
/* 2362 */       if (this.tree.isPathSelected(paramTreePath))
/* 2363 */         this.tree.removeSelectionPath(paramTreePath);
/*      */       else
/* 2365 */         this.tree.addSelectionPath(paramTreePath);
/* 2366 */       this.lastSelectedRow = getRowForPath(this.tree, paramTreePath);
/* 2367 */       setAnchorSelectionPath(paramTreePath);
/* 2368 */       setLeadSelectionPath(paramTreePath);
/*      */     }
/* 2372 */     else if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
/* 2373 */       this.tree.setSelectionPath(paramTreePath);
/* 2374 */       if (isToggleEvent(paramMouseEvent))
/* 2375 */         toggleExpandState(paramTreePath);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isLeaf(int paramInt)
/*      */   {
/* 2384 */     TreePath localTreePath = getPathForRow(this.tree, paramInt);
/*      */ 
/* 2386 */     if (localTreePath != null) {
/* 2387 */       return this.treeModel.isLeaf(localTreePath.getLastPathComponent());
/*      */     }
/* 2389 */     return true;
/*      */   }
/*      */ 
/*      */   private void setAnchorSelectionPath(TreePath paramTreePath)
/*      */   {
/* 2397 */     this.ignoreLAChange = true;
/*      */     try {
/* 2399 */       this.tree.setAnchorSelectionPath(paramTreePath);
/*      */     } finally {
/* 2401 */       this.ignoreLAChange = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private TreePath getAnchorSelectionPath() {
/* 2406 */     return this.tree.getAnchorSelectionPath();
/*      */   }
/*      */ 
/*      */   private void setLeadSelectionPath(TreePath paramTreePath) {
/* 2410 */     setLeadSelectionPath(paramTreePath, false);
/*      */   }
/*      */ 
/*      */   private void setLeadSelectionPath(TreePath paramTreePath, boolean paramBoolean) {
/* 2414 */     Rectangle localRectangle = paramBoolean ? getPathBounds(this.tree, getLeadSelectionPath()) : null;
/*      */ 
/* 2417 */     this.ignoreLAChange = true;
/*      */     try {
/* 2419 */       this.tree.setLeadSelectionPath(paramTreePath);
/*      */     } finally {
/* 2421 */       this.ignoreLAChange = false;
/*      */     }
/* 2423 */     this.leadRow = getRowForPath(this.tree, paramTreePath);
/*      */ 
/* 2425 */     if (paramBoolean) {
/* 2426 */       if (localRectangle != null) {
/* 2427 */         this.tree.repaint(getRepaintPathBounds(localRectangle));
/*      */       }
/* 2429 */       localRectangle = getPathBounds(this.tree, paramTreePath);
/* 2430 */       if (localRectangle != null)
/* 2431 */         this.tree.repaint(getRepaintPathBounds(localRectangle));
/*      */     }
/*      */   }
/*      */ 
/*      */   private Rectangle getRepaintPathBounds(Rectangle paramRectangle)
/*      */   {
/* 2437 */     if (UIManager.getBoolean("Tree.repaintWholeRow")) {
/* 2438 */       paramRectangle.x = 0;
/* 2439 */       paramRectangle.width = this.tree.getWidth();
/*      */     }
/* 2441 */     return paramRectangle;
/*      */   }
/*      */ 
/*      */   private TreePath getLeadSelectionPath() {
/* 2445 */     return this.tree.getLeadSelectionPath();
/*      */   }
/*      */ 
/*      */   protected void updateLeadSelectionRow()
/*      */   {
/* 2453 */     this.leadRow = getRowForPath(this.tree, getLeadSelectionPath());
/*      */   }
/*      */ 
/*      */   protected int getLeadSelectionRow()
/*      */   {
/* 2463 */     return this.leadRow;
/*      */   }
/*      */ 
/*      */   private void extendSelection(TreePath paramTreePath)
/*      */   {
/* 2471 */     TreePath localTreePath = getAnchorSelectionPath();
/* 2472 */     int i = localTreePath == null ? -1 : getRowForPath(this.tree, localTreePath);
/*      */ 
/* 2474 */     int j = getRowForPath(this.tree, paramTreePath);
/*      */ 
/* 2476 */     if (i == -1) {
/* 2477 */       this.tree.setSelectionRow(j);
/*      */     }
/*      */     else {
/* 2480 */       if (i < j) {
/* 2481 */         this.tree.setSelectionInterval(i, j);
/*      */       }
/*      */       else {
/* 2484 */         this.tree.setSelectionInterval(j, i);
/*      */       }
/* 2486 */       setAnchorSelectionPath(localTreePath);
/* 2487 */       setLeadSelectionPath(paramTreePath);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void repaintPath(TreePath paramTreePath)
/*      */   {
/* 2496 */     if (paramTreePath != null) {
/* 2497 */       Rectangle localRectangle = getPathBounds(this.tree, paramTreePath);
/* 2498 */       if (localRectangle != null)
/* 2499 */         this.tree.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Actions extends UIAction
/*      */   {
/*      */     private static final String SELECT_PREVIOUS = "selectPrevious";
/*      */     private static final String SELECT_PREVIOUS_CHANGE_LEAD = "selectPreviousChangeLead";
/*      */     private static final String SELECT_PREVIOUS_EXTEND_SELECTION = "selectPreviousExtendSelection";
/*      */     private static final String SELECT_NEXT = "selectNext";
/*      */     private static final String SELECT_NEXT_CHANGE_LEAD = "selectNextChangeLead";
/*      */     private static final String SELECT_NEXT_EXTEND_SELECTION = "selectNextExtendSelection";
/*      */     private static final String SELECT_CHILD = "selectChild";
/*      */     private static final String SELECT_CHILD_CHANGE_LEAD = "selectChildChangeLead";
/*      */     private static final String SELECT_PARENT = "selectParent";
/*      */     private static final String SELECT_PARENT_CHANGE_LEAD = "selectParentChangeLead";
/*      */     private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
/*      */     private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
/*      */     private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
/*      */     private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
/*      */     private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
/*      */     private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
/*      */     private static final String SELECT_FIRST = "selectFirst";
/*      */     private static final String SELECT_FIRST_CHANGE_LEAD = "selectFirstChangeLead";
/*      */     private static final String SELECT_FIRST_EXTEND_SELECTION = "selectFirstExtendSelection";
/*      */     private static final String SELECT_LAST = "selectLast";
/*      */     private static final String SELECT_LAST_CHANGE_LEAD = "selectLastChangeLead";
/*      */     private static final String SELECT_LAST_EXTEND_SELECTION = "selectLastExtendSelection";
/*      */     private static final String TOGGLE = "toggle";
/*      */     private static final String CANCEL_EDITING = "cancel";
/*      */     private static final String START_EDITING = "startEditing";
/*      */     private static final String SELECT_ALL = "selectAll";
/*      */     private static final String CLEAR_SELECTION = "clearSelection";
/*      */     private static final String SCROLL_LEFT = "scrollLeft";
/*      */     private static final String SCROLL_RIGHT = "scrollRight";
/*      */     private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
/*      */     private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
/*      */     private static final String SCROLL_RIGHT_CHANGE_LEAD = "scrollRightChangeLead";
/*      */     private static final String SCROLL_LEFT_CHANGE_LEAD = "scrollLeftChangeLead";
/*      */     private static final String EXPAND = "expand";
/*      */     private static final String COLLAPSE = "collapse";
/*      */     private static final String MOVE_SELECTION_TO_PARENT = "moveSelectionToParent";
/*      */     private static final String ADD_TO_SELECTION = "addToSelection";
/*      */     private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
/*      */     private static final String EXTEND_TO = "extendTo";
/*      */     private static final String MOVE_SELECTION_TO = "moveSelectionTo";
/*      */ 
/*      */     Actions()
/*      */     {
/* 3982 */       super();
/*      */     }
/*      */ 
/*      */     Actions(String paramString) {
/* 3986 */       super();
/*      */     }
/*      */ 
/*      */     public boolean isEnabled(Object paramObject) {
/* 3990 */       if (((paramObject instanceof JTree)) && 
/* 3991 */         (getName() == "cancel")) {
/* 3992 */         return ((JTree)paramObject).isEditing();
/*      */       }
/*      */ 
/* 3995 */       return true;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 3999 */       JTree localJTree = (JTree)paramActionEvent.getSource();
/* 4000 */       BasicTreeUI localBasicTreeUI = (BasicTreeUI)BasicLookAndFeel.getUIOfType(localJTree.getUI(), BasicTreeUI.class);
/*      */ 
/* 4002 */       if (localBasicTreeUI == null) {
/* 4003 */         return;
/*      */       }
/* 4005 */       String str = getName();
/* 4006 */       if (str == "selectPrevious") {
/* 4007 */         increment(localJTree, localBasicTreeUI, -1, false, true);
/*      */       }
/* 4009 */       else if (str == "selectPreviousChangeLead") {
/* 4010 */         increment(localJTree, localBasicTreeUI, -1, false, false);
/*      */       }
/* 4012 */       else if (str == "selectPreviousExtendSelection") {
/* 4013 */         increment(localJTree, localBasicTreeUI, -1, true, true);
/*      */       }
/* 4015 */       else if (str == "selectNext") {
/* 4016 */         increment(localJTree, localBasicTreeUI, 1, false, true);
/*      */       }
/* 4018 */       else if (str == "selectNextChangeLead") {
/* 4019 */         increment(localJTree, localBasicTreeUI, 1, false, false);
/*      */       }
/* 4021 */       else if (str == "selectNextExtendSelection") {
/* 4022 */         increment(localJTree, localBasicTreeUI, 1, true, true);
/*      */       }
/* 4024 */       else if (str == "selectChild") {
/* 4025 */         traverse(localJTree, localBasicTreeUI, 1, true);
/*      */       }
/* 4027 */       else if (str == "selectChildChangeLead") {
/* 4028 */         traverse(localJTree, localBasicTreeUI, 1, false);
/*      */       }
/* 4030 */       else if (str == "selectParent") {
/* 4031 */         traverse(localJTree, localBasicTreeUI, -1, true);
/*      */       }
/* 4033 */       else if (str == "selectParentChangeLead") {
/* 4034 */         traverse(localJTree, localBasicTreeUI, -1, false);
/*      */       }
/* 4036 */       else if (str == "scrollUpChangeSelection") {
/* 4037 */         page(localJTree, localBasicTreeUI, -1, false, true);
/*      */       }
/* 4039 */       else if (str == "scrollUpChangeLead") {
/* 4040 */         page(localJTree, localBasicTreeUI, -1, false, false);
/*      */       }
/* 4042 */       else if (str == "scrollUpExtendSelection") {
/* 4043 */         page(localJTree, localBasicTreeUI, -1, true, true);
/*      */       }
/* 4045 */       else if (str == "scrollDownChangeSelection") {
/* 4046 */         page(localJTree, localBasicTreeUI, 1, false, true);
/*      */       }
/* 4048 */       else if (str == "scrollDownExtendSelection") {
/* 4049 */         page(localJTree, localBasicTreeUI, 1, true, true);
/*      */       }
/* 4051 */       else if (str == "scrollDownChangeLead") {
/* 4052 */         page(localJTree, localBasicTreeUI, 1, false, false);
/*      */       }
/* 4054 */       else if (str == "selectFirst") {
/* 4055 */         home(localJTree, localBasicTreeUI, -1, false, true);
/*      */       }
/* 4057 */       else if (str == "selectFirstChangeLead") {
/* 4058 */         home(localJTree, localBasicTreeUI, -1, false, false);
/*      */       }
/* 4060 */       else if (str == "selectFirstExtendSelection") {
/* 4061 */         home(localJTree, localBasicTreeUI, -1, true, true);
/*      */       }
/* 4063 */       else if (str == "selectLast") {
/* 4064 */         home(localJTree, localBasicTreeUI, 1, false, true);
/*      */       }
/* 4066 */       else if (str == "selectLastChangeLead") {
/* 4067 */         home(localJTree, localBasicTreeUI, 1, false, false);
/*      */       }
/* 4069 */       else if (str == "selectLastExtendSelection") {
/* 4070 */         home(localJTree, localBasicTreeUI, 1, true, true);
/*      */       }
/* 4072 */       else if (str == "toggle") {
/* 4073 */         toggle(localJTree, localBasicTreeUI);
/*      */       }
/* 4075 */       else if (str == "cancel") {
/* 4076 */         cancelEditing(localJTree, localBasicTreeUI);
/*      */       }
/* 4078 */       else if (str == "startEditing") {
/* 4079 */         startEditing(localJTree, localBasicTreeUI);
/*      */       }
/* 4081 */       else if (str == "selectAll") {
/* 4082 */         selectAll(localJTree, localBasicTreeUI, true);
/*      */       }
/* 4084 */       else if (str == "clearSelection") {
/* 4085 */         selectAll(localJTree, localBasicTreeUI, false);
/*      */       }
/*      */       else
/*      */       {
/*      */         int i;
/*      */         TreePath localTreePath;
/* 4087 */         if (str == "addToSelection") {
/* 4088 */           if (localBasicTreeUI.getRowCount(localJTree) > 0) {
/* 4089 */             i = localBasicTreeUI.getLeadSelectionRow();
/* 4090 */             if (!localJTree.isRowSelected(i)) {
/* 4091 */               localTreePath = localBasicTreeUI.getAnchorSelectionPath();
/* 4092 */               localJTree.addSelectionRow(i);
/* 4093 */               localBasicTreeUI.setAnchorSelectionPath(localTreePath);
/*      */             }
/*      */           }
/*      */         }
/* 4097 */         else if (str == "toggleAndAnchor") {
/* 4098 */           if (localBasicTreeUI.getRowCount(localJTree) > 0) {
/* 4099 */             i = localBasicTreeUI.getLeadSelectionRow();
/* 4100 */             localTreePath = localBasicTreeUI.getLeadSelectionPath();
/* 4101 */             if (!localJTree.isRowSelected(i)) {
/* 4102 */               localJTree.addSelectionRow(i);
/*      */             } else {
/* 4104 */               localJTree.removeSelectionRow(i);
/* 4105 */               localBasicTreeUI.setLeadSelectionPath(localTreePath);
/*      */             }
/* 4107 */             localBasicTreeUI.setAnchorSelectionPath(localTreePath);
/*      */           }
/*      */         }
/* 4110 */         else if (str == "extendTo") {
/* 4111 */           extendSelection(localJTree, localBasicTreeUI);
/*      */         }
/* 4113 */         else if (str == "moveSelectionTo") {
/* 4114 */           if (localBasicTreeUI.getRowCount(localJTree) > 0) {
/* 4115 */             i = localBasicTreeUI.getLeadSelectionRow();
/* 4116 */             localJTree.setSelectionInterval(i, i);
/*      */           }
/*      */         }
/* 4119 */         else if (str == "scrollLeft") {
/* 4120 */           scroll(localJTree, localBasicTreeUI, 0, -10);
/*      */         }
/* 4122 */         else if (str == "scrollRight") {
/* 4123 */           scroll(localJTree, localBasicTreeUI, 0, 10);
/*      */         }
/* 4125 */         else if (str == "scrollLeftExtendSelection") {
/* 4126 */           scrollChangeSelection(localJTree, localBasicTreeUI, -1, true, true);
/*      */         }
/* 4128 */         else if (str == "scrollRightExtendSelection") {
/* 4129 */           scrollChangeSelection(localJTree, localBasicTreeUI, 1, true, true);
/*      */         }
/* 4131 */         else if (str == "scrollRightChangeLead") {
/* 4132 */           scrollChangeSelection(localJTree, localBasicTreeUI, 1, false, false);
/*      */         }
/* 4134 */         else if (str == "scrollLeftChangeLead") {
/* 4135 */           scrollChangeSelection(localJTree, localBasicTreeUI, -1, false, false);
/*      */         }
/* 4137 */         else if (str == "expand") {
/* 4138 */           expand(localJTree, localBasicTreeUI);
/*      */         }
/* 4140 */         else if (str == "collapse") {
/* 4141 */           collapse(localJTree, localBasicTreeUI);
/*      */         }
/* 4143 */         else if (str == "moveSelectionToParent")
/* 4144 */           moveSelectionToParent(localJTree, localBasicTreeUI);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void scrollChangeSelection(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/*      */       int i;
/* 4153 */       if (((i = paramBasicTreeUI.getRowCount(paramJTree)) > 0) && (paramBasicTreeUI.treeSelectionModel != null))
/*      */       {
/* 4156 */         Rectangle localRectangle = paramJTree.getVisibleRect();
/*      */         TreePath localTreePath;
/* 4158 */         if (paramInt == -1) {
/* 4159 */           localTreePath = paramBasicTreeUI.getClosestPathForLocation(paramJTree, localRectangle.x, localRectangle.y);
/*      */ 
/* 4161 */           localRectangle.x = Math.max(0, localRectangle.x - localRectangle.width);
/*      */         }
/*      */         else {
/* 4164 */           localRectangle.x = Math.min(Math.max(0, paramJTree.getWidth() - localRectangle.width), localRectangle.x + localRectangle.width);
/*      */ 
/* 4166 */           localTreePath = paramBasicTreeUI.getClosestPathForLocation(paramJTree, localRectangle.x, localRectangle.y + localRectangle.height);
/*      */         }
/*      */ 
/* 4170 */         paramJTree.scrollRectToVisible(localRectangle);
/*      */ 
/* 4172 */         if (paramBoolean1) {
/* 4173 */           paramBasicTreeUI.extendSelection(localTreePath);
/*      */         }
/* 4175 */         else if (paramBoolean2) {
/* 4176 */           paramJTree.setSelectionPath(localTreePath);
/*      */         }
/*      */         else
/* 4179 */           paramBasicTreeUI.setLeadSelectionPath(localTreePath, true);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void scroll(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt1, int paramInt2)
/*      */     {
/* 4186 */       Rectangle localRectangle = paramJTree.getVisibleRect();
/* 4187 */       Dimension localDimension = paramJTree.getSize();
/* 4188 */       if (paramInt1 == 0) {
/* 4189 */         localRectangle.x += paramInt2;
/* 4190 */         localRectangle.x = Math.max(0, localRectangle.x);
/* 4191 */         localRectangle.x = Math.min(Math.max(0, localDimension.width - localRectangle.width), localRectangle.x);
/*      */       }
/*      */       else
/*      */       {
/* 4195 */         localRectangle.y += paramInt2;
/* 4196 */         localRectangle.y = Math.max(0, localRectangle.y);
/* 4197 */         localRectangle.y = Math.min(Math.max(0, localDimension.width - localRectangle.height), localRectangle.y);
/*      */       }
/*      */ 
/* 4200 */       paramJTree.scrollRectToVisible(localRectangle);
/*      */     }
/*      */ 
/*      */     private void extendSelection(JTree paramJTree, BasicTreeUI paramBasicTreeUI) {
/* 4204 */       if (paramBasicTreeUI.getRowCount(paramJTree) > 0) {
/* 4205 */         int i = paramBasicTreeUI.getLeadSelectionRow();
/*      */ 
/* 4207 */         if (i != -1) {
/* 4208 */           TreePath localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
/* 4209 */           TreePath localTreePath2 = paramBasicTreeUI.getAnchorSelectionPath();
/* 4210 */           int j = paramBasicTreeUI.getRowForPath(paramJTree, localTreePath2);
/*      */ 
/* 4212 */           if (j == -1)
/* 4213 */             j = 0;
/* 4214 */           paramJTree.setSelectionInterval(j, i);
/* 4215 */           paramBasicTreeUI.setLeadSelectionPath(localTreePath1);
/* 4216 */           paramBasicTreeUI.setAnchorSelectionPath(localTreePath2);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void selectAll(JTree paramJTree, BasicTreeUI paramBasicTreeUI, boolean paramBoolean) {
/* 4222 */       int i = paramBasicTreeUI.getRowCount(paramJTree);
/*      */ 
/* 4224 */       if (i > 0)
/*      */       {
/*      */         TreePath localTreePath1;
/*      */         TreePath localTreePath2;
/* 4225 */         if (paramBoolean) {
/* 4226 */           if (paramJTree.getSelectionModel().getSelectionMode() == 1)
/*      */           {
/* 4229 */             int j = paramBasicTreeUI.getLeadSelectionRow();
/* 4230 */             if (j != -1) {
/* 4231 */               paramJTree.setSelectionRow(j);
/* 4232 */             } else if (paramJTree.getMinSelectionRow() == -1) {
/* 4233 */               paramJTree.setSelectionRow(0);
/* 4234 */               paramBasicTreeUI.ensureRowsAreVisible(0, 0);
/*      */             }
/* 4236 */             return;
/*      */           }
/*      */ 
/* 4239 */           localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
/* 4240 */           localTreePath2 = paramBasicTreeUI.getAnchorSelectionPath();
/*      */ 
/* 4242 */           if ((localTreePath1 != null) && (!paramJTree.isVisible(localTreePath1))) {
/* 4243 */             localTreePath1 = null;
/*      */           }
/* 4245 */           paramJTree.setSelectionInterval(0, i - 1);
/* 4246 */           if (localTreePath1 != null) {
/* 4247 */             paramBasicTreeUI.setLeadSelectionPath(localTreePath1);
/*      */           }
/* 4249 */           if ((localTreePath2 != null) && (paramJTree.isVisible(localTreePath2)))
/* 4250 */             paramBasicTreeUI.setAnchorSelectionPath(localTreePath2);
/*      */         }
/*      */         else
/*      */         {
/* 4254 */           localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
/* 4255 */           localTreePath2 = paramBasicTreeUI.getAnchorSelectionPath();
/*      */ 
/* 4257 */           paramJTree.clearSelection();
/* 4258 */           paramBasicTreeUI.setAnchorSelectionPath(localTreePath2);
/* 4259 */           paramBasicTreeUI.setLeadSelectionPath(localTreePath1);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void startEditing(JTree paramJTree, BasicTreeUI paramBasicTreeUI) {
/* 4265 */       TreePath localTreePath = paramBasicTreeUI.getLeadSelectionPath();
/* 4266 */       int i = localTreePath != null ? paramBasicTreeUI.getRowForPath(paramJTree, localTreePath) : -1;
/*      */ 
/* 4269 */       if (i != -1)
/* 4270 */         paramJTree.startEditingAtPath(localTreePath);
/*      */     }
/*      */ 
/*      */     private void cancelEditing(JTree paramJTree, BasicTreeUI paramBasicTreeUI)
/*      */     {
/* 4275 */       paramJTree.cancelEditing();
/*      */     }
/*      */ 
/*      */     private void toggle(JTree paramJTree, BasicTreeUI paramBasicTreeUI) {
/* 4279 */       int i = paramBasicTreeUI.getLeadSelectionRow();
/*      */ 
/* 4281 */       if ((i != -1) && (!paramBasicTreeUI.isLeaf(i))) {
/* 4282 */         TreePath localTreePath1 = paramBasicTreeUI.getAnchorSelectionPath();
/* 4283 */         TreePath localTreePath2 = paramBasicTreeUI.getLeadSelectionPath();
/*      */ 
/* 4285 */         paramBasicTreeUI.toggleExpandState(paramBasicTreeUI.getPathForRow(paramJTree, i));
/* 4286 */         paramBasicTreeUI.setAnchorSelectionPath(localTreePath1);
/* 4287 */         paramBasicTreeUI.setLeadSelectionPath(localTreePath2);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void expand(JTree paramJTree, BasicTreeUI paramBasicTreeUI) {
/* 4292 */       int i = paramBasicTreeUI.getLeadSelectionRow();
/* 4293 */       paramJTree.expandRow(i);
/*      */     }
/*      */ 
/*      */     private void collapse(JTree paramJTree, BasicTreeUI paramBasicTreeUI) {
/* 4297 */       int i = paramBasicTreeUI.getLeadSelectionRow();
/* 4298 */       paramJTree.collapseRow(i);
/*      */     }
/*      */ 
/*      */     private void increment(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 4306 */       if ((!paramBoolean1) && (!paramBoolean2) && (paramJTree.getSelectionModel().getSelectionMode() != 4))
/*      */       {
/* 4309 */         paramBoolean2 = true;
/*      */       }
/*      */       int i;
/* 4314 */       if ((paramBasicTreeUI.treeSelectionModel != null) && ((i = paramJTree.getRowCount()) > 0))
/*      */       {
/* 4316 */         int j = paramBasicTreeUI.getLeadSelectionRow();
/*      */         int k;
/* 4319 */         if (j == -1) {
/* 4320 */           if (paramInt == 1)
/* 4321 */             k = 0;
/*      */           else {
/* 4323 */             k = i - 1;
/*      */           }
/*      */         }
/*      */         else {
/* 4327 */           k = Math.min(i - 1, Math.max(0, j + paramInt));
/*      */         }
/* 4329 */         if ((paramBoolean1) && (paramBasicTreeUI.treeSelectionModel.getSelectionMode() != 1))
/*      */         {
/* 4332 */           paramBasicTreeUI.extendSelection(paramJTree.getPathForRow(k));
/*      */         }
/* 4334 */         else if (paramBoolean2) {
/* 4335 */           paramJTree.setSelectionInterval(k, k);
/*      */         }
/*      */         else {
/* 4338 */           paramBasicTreeUI.setLeadSelectionPath(paramJTree.getPathForRow(k), true);
/*      */         }
/* 4340 */         paramBasicTreeUI.ensureRowsAreVisible(k, k);
/* 4341 */         paramBasicTreeUI.lastSelectedRow = k;
/*      */       }
/*      */     }
/*      */ 
/*      */     private void traverse(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean)
/*      */     {
/* 4349 */       if ((!paramBoolean) && (paramJTree.getSelectionModel().getSelectionMode() != 4))
/*      */       {
/* 4352 */         paramBoolean = true;
/*      */       }
/*      */       int i;
/* 4357 */       if ((i = paramJTree.getRowCount()) > 0) {
/* 4358 */         int j = paramBasicTreeUI.getLeadSelectionRow();
/*      */         int k;
/* 4361 */         if (j == -1) {
/* 4362 */           k = 0;
/*      */         }
/*      */         else
/*      */         {
/*      */           TreePath localTreePath;
/* 4366 */           if (paramInt == 1) {
/* 4367 */             localTreePath = paramBasicTreeUI.getPathForRow(paramJTree, j);
/* 4368 */             int m = paramJTree.getModel().getChildCount(localTreePath.getLastPathComponent());
/*      */ 
/* 4370 */             k = -1;
/* 4371 */             if (!paramBasicTreeUI.isLeaf(j)) {
/* 4372 */               if (!paramJTree.isExpanded(j)) {
/* 4373 */                 paramBasicTreeUI.toggleExpandState(localTreePath);
/*      */               }
/* 4375 */               else if (m > 0) {
/* 4376 */                 k = Math.min(j + 1, i - 1);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/* 4382 */           else if ((!paramBasicTreeUI.isLeaf(j)) && (paramJTree.isExpanded(j)))
/*      */           {
/* 4384 */             paramBasicTreeUI.toggleExpandState(paramBasicTreeUI.getPathForRow(paramJTree, j));
/*      */ 
/* 4386 */             k = -1;
/*      */           }
/*      */           else {
/* 4389 */             localTreePath = paramBasicTreeUI.getPathForRow(paramJTree, j);
/*      */ 
/* 4392 */             if ((localTreePath != null) && (localTreePath.getPathCount() > 1)) {
/* 4393 */               k = paramBasicTreeUI.getRowForPath(paramJTree, localTreePath.getParentPath());
/*      */             }
/*      */             else
/*      */             {
/* 4397 */               k = -1;
/*      */             }
/*      */           }
/*      */         }
/* 4401 */         if (k != -1) {
/* 4402 */           if (paramBoolean) {
/* 4403 */             paramJTree.setSelectionInterval(k, k);
/*      */           }
/*      */           else {
/* 4406 */             paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, k), true);
/*      */           }
/*      */ 
/* 4409 */           paramBasicTreeUI.ensureRowsAreVisible(k, k);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void moveSelectionToParent(JTree paramJTree, BasicTreeUI paramBasicTreeUI) {
/* 4415 */       int i = paramBasicTreeUI.getLeadSelectionRow();
/* 4416 */       TreePath localTreePath = paramBasicTreeUI.getPathForRow(paramJTree, i);
/* 4417 */       if ((localTreePath != null) && (localTreePath.getPathCount() > 1)) {
/* 4418 */         int j = paramBasicTreeUI.getRowForPath(paramJTree, localTreePath.getParentPath());
/* 4419 */         if (j != -1) {
/* 4420 */           paramJTree.setSelectionInterval(j, j);
/* 4421 */           paramBasicTreeUI.ensureRowsAreVisible(j, j);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void page(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 4430 */       if ((!paramBoolean1) && (!paramBoolean2) && (paramJTree.getSelectionModel().getSelectionMode() != 4))
/*      */       {
/* 4433 */         paramBoolean2 = true;
/*      */       }
/*      */       int i;
/* 4438 */       if (((i = paramBasicTreeUI.getRowCount(paramJTree)) > 0) && (paramBasicTreeUI.treeSelectionModel != null))
/*      */       {
/* 4440 */         Dimension localDimension = paramJTree.getSize();
/* 4441 */         TreePath localTreePath1 = paramBasicTreeUI.getLeadSelectionPath();
/*      */ 
/* 4443 */         Rectangle localRectangle1 = paramJTree.getVisibleRect();
/*      */         TreePath localTreePath2;
/* 4445 */         if (paramInt == -1)
/*      */         {
/* 4447 */           localTreePath2 = paramBasicTreeUI.getClosestPathForLocation(paramJTree, localRectangle1.x, localRectangle1.y);
/*      */ 
/* 4449 */           if (localTreePath2.equals(localTreePath1)) {
/* 4450 */             localRectangle1.y = Math.max(0, localRectangle1.y - localRectangle1.height);
/* 4451 */             localTreePath2 = paramJTree.getClosestPathForLocation(localRectangle1.x, localRectangle1.y);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 4457 */           localRectangle1.y = Math.min(localDimension.height, localRectangle1.y + localRectangle1.height - 1);
/*      */ 
/* 4459 */           localTreePath2 = paramJTree.getClosestPathForLocation(localRectangle1.x, localRectangle1.y);
/*      */ 
/* 4461 */           if (localTreePath2.equals(localTreePath1)) {
/* 4462 */             localRectangle1.y = Math.min(localDimension.height, localRectangle1.y + localRectangle1.height - 1);
/*      */ 
/* 4464 */             localTreePath2 = paramJTree.getClosestPathForLocation(localRectangle1.x, localRectangle1.y);
/*      */           }
/*      */         }
/*      */ 
/* 4468 */         Rectangle localRectangle2 = paramBasicTreeUI.getPathBounds(paramJTree, localTreePath2);
/*      */ 
/* 4470 */         localRectangle2.x = localRectangle1.x;
/* 4471 */         localRectangle2.width = localRectangle1.width;
/* 4472 */         if (paramInt == -1) {
/* 4473 */           localRectangle2.height = localRectangle1.height;
/*      */         }
/*      */         else {
/* 4476 */           localRectangle2.y -= localRectangle1.height - localRectangle2.height;
/* 4477 */           localRectangle2.height = localRectangle1.height;
/*      */         }
/*      */ 
/* 4480 */         if (paramBoolean1) {
/* 4481 */           paramBasicTreeUI.extendSelection(localTreePath2);
/*      */         }
/* 4483 */         else if (paramBoolean2) {
/* 4484 */           paramJTree.setSelectionPath(localTreePath2);
/*      */         }
/*      */         else {
/* 4487 */           paramBasicTreeUI.setLeadSelectionPath(localTreePath2, true);
/*      */         }
/* 4489 */         paramJTree.scrollRectToVisible(localRectangle2);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void home(JTree paramJTree, BasicTreeUI paramBasicTreeUI, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 4497 */       if ((!paramBoolean1) && (!paramBoolean2) && (paramJTree.getSelectionModel().getSelectionMode() != 4))
/*      */       {
/* 4500 */         paramBoolean2 = true;
/*      */       }
/*      */ 
/* 4503 */       int i = paramBasicTreeUI.getRowCount(paramJTree);
/*      */ 
/* 4505 */       if (i > 0)
/*      */       {
/*      */         TreePath localTreePath;
/*      */         int j;
/* 4506 */         if (paramInt == -1) {
/* 4507 */           paramBasicTreeUI.ensureRowsAreVisible(0, 0);
/* 4508 */           if (paramBoolean1) {
/* 4509 */             localTreePath = paramBasicTreeUI.getAnchorSelectionPath();
/* 4510 */             j = localTreePath == null ? -1 : paramBasicTreeUI.getRowForPath(paramJTree, localTreePath);
/*      */ 
/* 4513 */             if (j == -1) {
/* 4514 */               paramJTree.setSelectionInterval(0, 0);
/*      */             }
/*      */             else {
/* 4517 */               paramJTree.setSelectionInterval(0, j);
/* 4518 */               paramBasicTreeUI.setAnchorSelectionPath(localTreePath);
/* 4519 */               paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, 0));
/*      */             }
/*      */           }
/* 4522 */           else if (paramBoolean2) {
/* 4523 */             paramJTree.setSelectionInterval(0, 0);
/*      */           }
/*      */           else {
/* 4526 */             paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, 0), true);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 4531 */           paramBasicTreeUI.ensureRowsAreVisible(i - 1, i - 1);
/* 4532 */           if (paramBoolean1) {
/* 4533 */             localTreePath = paramBasicTreeUI.getAnchorSelectionPath();
/* 4534 */             j = localTreePath == null ? -1 : paramBasicTreeUI.getRowForPath(paramJTree, localTreePath);
/*      */ 
/* 4537 */             if (j == -1) {
/* 4538 */               paramJTree.setSelectionInterval(i - 1, i - 1);
/*      */             }
/*      */             else
/*      */             {
/* 4542 */               paramJTree.setSelectionInterval(j, i - 1);
/* 4543 */               paramBasicTreeUI.setAnchorSelectionPath(localTreePath);
/* 4544 */               paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, i - 1));
/*      */             }
/*      */ 
/*      */           }
/* 4548 */           else if (paramBoolean2) {
/* 4549 */             paramJTree.setSelectionInterval(i - 1, i - 1);
/*      */           }
/*      */           else {
/* 4552 */             paramBasicTreeUI.setLeadSelectionPath(paramBasicTreeUI.getPathForRow(paramJTree, i - 1), true);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public class CellEditorHandler
/*      */     implements CellEditorListener
/*      */   {
/*      */     public CellEditorHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void editingStopped(ChangeEvent paramChangeEvent)
/*      */     {
/* 2667 */       BasicTreeUI.this.getHandler().editingStopped(paramChangeEvent);
/*      */     }
/*      */ 
/*      */     public void editingCanceled(ChangeEvent paramChangeEvent)
/*      */     {
/* 2672 */       BasicTreeUI.this.getHandler().editingCanceled(paramChangeEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class ComponentHandler extends ComponentAdapter
/*      */     implements ActionListener
/*      */   {
/*      */     protected Timer timer;
/*      */     protected JScrollBar scrollBar;
/*      */ 
/*      */     public ComponentHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void componentMoved(ComponentEvent paramComponentEvent)
/*      */     {
/* 2541 */       if (this.timer == null) {
/* 2542 */         JScrollPane localJScrollPane = getScrollPane();
/*      */ 
/* 2544 */         if (localJScrollPane == null) {
/* 2545 */           BasicTreeUI.this.updateSize();
/*      */         } else {
/* 2547 */           this.scrollBar = localJScrollPane.getVerticalScrollBar();
/* 2548 */           if ((this.scrollBar == null) || (!this.scrollBar.getValueIsAdjusting()))
/*      */           {
/* 2551 */             if (((this.scrollBar = localJScrollPane.getHorizontalScrollBar()) != null) && (this.scrollBar.getValueIsAdjusting()))
/*      */             {
/* 2553 */               startTimer();
/*      */             }
/* 2555 */             else BasicTreeUI.this.updateSize();
/*      */           }
/*      */           else
/* 2558 */             startTimer();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void startTimer()
/*      */     {
/* 2568 */       if (this.timer == null) {
/* 2569 */         this.timer = new Timer(200, this);
/* 2570 */         this.timer.setRepeats(true);
/*      */       }
/* 2572 */       this.timer.start();
/*      */     }
/*      */ 
/*      */     protected JScrollPane getScrollPane()
/*      */     {
/* 2580 */       Container localContainer = BasicTreeUI.this.tree.getParent();
/*      */ 
/* 2582 */       while ((localContainer != null) && (!(localContainer instanceof JScrollPane)))
/* 2583 */         localContainer = localContainer.getParent();
/* 2584 */       if ((localContainer instanceof JScrollPane))
/* 2585 */         return (JScrollPane)localContainer;
/* 2586 */       return null;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 2594 */       if ((this.scrollBar == null) || (!this.scrollBar.getValueIsAdjusting())) {
/* 2595 */         if (this.timer != null)
/* 2596 */           this.timer.stop();
/* 2597 */         BasicTreeUI.this.updateSize();
/* 2598 */         this.timer = null;
/* 2599 */         this.scrollBar = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public class FocusHandler
/*      */     implements FocusListener
/*      */   {
/*      */     public FocusHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/* 2735 */       BasicTreeUI.this.getHandler().focusGained(paramFocusEvent);
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent)
/*      */     {
/* 2743 */       BasicTreeUI.this.getHandler().focusLost(paramFocusEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Handler
/*      */     implements CellEditorListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, PropertyChangeListener, TreeExpansionListener, TreeModelListener, TreeSelectionListener, DragRecognitionSupport.BeforeDrag
/*      */   {
/* 3269 */     private String prefix = "";
/* 3270 */     private String typedString = "";
/* 3271 */     private long lastTime = 0L;
/*      */     private boolean dragPressDidSelection;
/*      */     private boolean dragStarted;
/*      */     private TreePath pressedPath;
/*      */     private MouseEvent pressedEvent;
/*      */     private boolean valueChangedOnPress;
/*      */ 
/*      */     private Handler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void keyTyped(KeyEvent paramKeyEvent)
/*      */     {
/* 3286 */       if ((BasicTreeUI.this.tree != null) && (BasicTreeUI.this.tree.getRowCount() > 0) && (BasicTreeUI.this.tree.hasFocus()) && (BasicTreeUI.this.tree.isEnabled()))
/*      */       {
/* 3288 */         if ((paramKeyEvent.isAltDown()) || (BasicGraphicsUtils.isMenuShortcutKeyDown(paramKeyEvent)) || (isNavigationKey(paramKeyEvent)))
/*      */         {
/* 3290 */           return;
/*      */         }
/* 3292 */         int i = 1;
/*      */ 
/* 3294 */         char c = paramKeyEvent.getKeyChar();
/*      */ 
/* 3296 */         long l = paramKeyEvent.getWhen();
/* 3297 */         int j = BasicTreeUI.this.tree.getLeadSelectionRow();
/* 3298 */         if (l - this.lastTime < BasicTreeUI.this.timeFactor) {
/* 3299 */           this.typedString += c;
/* 3300 */           if ((this.prefix.length() == 1) && (c == this.prefix.charAt(0)))
/*      */           {
/* 3303 */             j++;
/*      */           }
/* 3305 */           else this.prefix = this.typedString; 
/*      */         }
/*      */         else
/*      */         {
/* 3308 */           j++;
/* 3309 */           this.typedString = ("" + c);
/* 3310 */           this.prefix = this.typedString;
/*      */         }
/* 3312 */         this.lastTime = l;
/*      */ 
/* 3314 */         if ((j < 0) || (j >= BasicTreeUI.this.tree.getRowCount())) {
/* 3315 */           i = 0;
/* 3316 */           j = 0;
/*      */         }
/* 3318 */         TreePath localTreePath = BasicTreeUI.this.tree.getNextMatch(this.prefix, j, Position.Bias.Forward);
/*      */         int k;
/* 3320 */         if (localTreePath != null) {
/* 3321 */           BasicTreeUI.this.tree.setSelectionPath(localTreePath);
/* 3322 */           k = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, localTreePath);
/* 3323 */           BasicTreeUI.this.ensureRowsAreVisible(k, k);
/* 3324 */         } else if (i != 0) {
/* 3325 */           localTreePath = BasicTreeUI.this.tree.getNextMatch(this.prefix, 0, Position.Bias.Forward);
/*      */ 
/* 3327 */           if (localTreePath != null) {
/* 3328 */             BasicTreeUI.this.tree.setSelectionPath(localTreePath);
/* 3329 */             k = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, localTreePath);
/* 3330 */             BasicTreeUI.this.ensureRowsAreVisible(k, k);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void keyPressed(KeyEvent paramKeyEvent)
/*      */     {
/* 3343 */       if ((BasicTreeUI.this.tree != null) && (isNavigationKey(paramKeyEvent))) {
/* 3344 */         this.prefix = "";
/* 3345 */         this.typedString = "";
/* 3346 */         this.lastTime = 0L;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void keyReleased(KeyEvent paramKeyEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     private boolean isNavigationKey(KeyEvent paramKeyEvent)
/*      */     {
/* 3359 */       InputMap localInputMap = BasicTreeUI.this.tree.getInputMap(1);
/* 3360 */       KeyStroke localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramKeyEvent);
/*      */ 
/* 3362 */       return (localInputMap != null) && (localInputMap.get(localKeyStroke) != null);
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 3370 */       if (paramPropertyChangeEvent.getSource() == BasicTreeUI.this.treeSelectionModel) {
/* 3371 */         BasicTreeUI.this.treeSelectionModel.resetRowSelection();
/*      */       }
/* 3373 */       else if (paramPropertyChangeEvent.getSource() == BasicTreeUI.this.tree) {
/* 3374 */         String str = paramPropertyChangeEvent.getPropertyName();
/*      */ 
/* 3376 */         if (str == "leadSelectionPath") {
/* 3377 */           if (!BasicTreeUI.this.ignoreLAChange) {
/* 3378 */             BasicTreeUI.this.updateLeadSelectionRow();
/* 3379 */             BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getOldValue());
/* 3380 */             BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getNewValue());
/*      */           }
/*      */         }
/* 3383 */         else if ((str == "anchorSelectionPath") && 
/* 3384 */           (!BasicTreeUI.this.ignoreLAChange)) {
/* 3385 */           BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getOldValue());
/* 3386 */           BasicTreeUI.this.repaintPath((TreePath)paramPropertyChangeEvent.getNewValue());
/*      */         }
/*      */ 
/* 3389 */         if (str == "cellRenderer") {
/* 3390 */           BasicTreeUI.this.setCellRenderer((TreeCellRenderer)paramPropertyChangeEvent.getNewValue());
/* 3391 */           BasicTreeUI.this.redoTheLayout();
/*      */         }
/* 3393 */         else if (str == "model") {
/* 3394 */           BasicTreeUI.this.setModel((TreeModel)paramPropertyChangeEvent.getNewValue());
/*      */         }
/* 3396 */         else if (str == "rootVisible") {
/* 3397 */           BasicTreeUI.this.setRootVisible(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
/*      */         }
/* 3400 */         else if (str == "showsRootHandles") {
/* 3401 */           BasicTreeUI.this.setShowsRootHandles(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
/*      */         }
/* 3404 */         else if (str == "rowHeight") {
/* 3405 */           BasicTreeUI.this.setRowHeight(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
/*      */         }
/* 3408 */         else if (str == "cellEditor") {
/* 3409 */           BasicTreeUI.this.setCellEditor((TreeCellEditor)paramPropertyChangeEvent.getNewValue());
/*      */         }
/* 3411 */         else if (str == "editable") {
/* 3412 */           BasicTreeUI.this.setEditable(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
/*      */         }
/* 3414 */         else if (str == "largeModel") {
/* 3415 */           BasicTreeUI.this.setLargeModel(BasicTreeUI.this.tree.isLargeModel());
/*      */         }
/* 3417 */         else if (str == "selectionModel") {
/* 3418 */           BasicTreeUI.this.setSelectionModel(BasicTreeUI.this.tree.getSelectionModel());
/*      */         }
/* 3420 */         else if (str == "font") {
/* 3421 */           BasicTreeUI.this.completeEditing();
/* 3422 */           if (BasicTreeUI.this.treeState != null)
/* 3423 */             BasicTreeUI.this.treeState.invalidateSizes();
/* 3424 */           BasicTreeUI.this.updateSize();
/*      */         }
/*      */         else
/*      */         {
/*      */           Object localObject;
/* 3426 */           if (str == "componentOrientation") {
/* 3427 */             if (BasicTreeUI.this.tree != null) {
/* 3428 */               BasicTreeUI.this.leftToRight = BasicGraphicsUtils.isLeftToRight(BasicTreeUI.this.tree);
/* 3429 */               BasicTreeUI.this.redoTheLayout();
/* 3430 */               BasicTreeUI.this.tree.treeDidChange();
/*      */ 
/* 3432 */               localObject = BasicTreeUI.this.getInputMap(0);
/* 3433 */               SwingUtilities.replaceUIInputMap(BasicTreeUI.this.tree, 0, (InputMap)localObject);
/*      */             }
/*      */           }
/* 3436 */           else if ("dropLocation" == str) {
/* 3437 */             localObject = (JTree.DropLocation)paramPropertyChangeEvent.getOldValue();
/* 3438 */             repaintDropLocation((JTree.DropLocation)localObject);
/* 3439 */             repaintDropLocation(BasicTreeUI.this.tree.getDropLocation());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3445 */     private void repaintDropLocation(JTree.DropLocation paramDropLocation) { if (paramDropLocation == null)
/*      */         return;
/*      */       Rectangle localRectangle;
/* 3451 */       if (BasicTreeUI.this.isDropLine(paramDropLocation))
/* 3452 */         localRectangle = BasicTreeUI.this.getDropLineRect(paramDropLocation);
/*      */       else {
/* 3454 */         localRectangle = BasicTreeUI.this.tree.getPathBounds(paramDropLocation.getPath());
/*      */       }
/*      */ 
/* 3457 */       if (localRectangle != null)
/* 3458 */         BasicTreeUI.this.tree.repaint(localRectangle);
/*      */     }
/*      */ 
/*      */     private boolean isActualPath(TreePath paramTreePath, int paramInt1, int paramInt2)
/*      */     {
/* 3485 */       if (paramTreePath == null) {
/* 3486 */         return false;
/*      */       }
/*      */ 
/* 3489 */       Rectangle localRectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, paramTreePath);
/* 3490 */       if ((localRectangle == null) || (paramInt2 > localRectangle.y + localRectangle.height)) {
/* 3491 */         return false;
/*      */       }
/*      */ 
/* 3494 */       return (paramInt1 >= localRectangle.x) && (paramInt1 <= localRectangle.x + localRectangle.width);
/*      */     }
/*      */ 
/*      */     public void mouseClicked(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseEntered(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseExited(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mousePressed(MouseEvent paramMouseEvent) {
/* 3510 */       if (SwingUtilities2.shouldIgnore(paramMouseEvent, BasicTreeUI.this.tree)) {
/* 3511 */         return;
/*      */       }
/*      */ 
/* 3515 */       if ((BasicTreeUI.this.isEditing(BasicTreeUI.this.tree)) && (BasicTreeUI.this.tree.getInvokesStopCellEditing()) && (!BasicTreeUI.this.stopEditing(BasicTreeUI.this.tree)))
/*      */       {
/* 3517 */         return;
/*      */       }
/*      */ 
/* 3520 */       BasicTreeUI.this.completeEditing();
/*      */ 
/* 3522 */       this.pressedPath = BasicTreeUI.this.getClosestPathForLocation(BasicTreeUI.this.tree, paramMouseEvent.getX(), paramMouseEvent.getY());
/*      */ 
/* 3524 */       if (BasicTreeUI.this.tree.getDragEnabled()) {
/* 3525 */         mousePressedDND(paramMouseEvent);
/*      */       } else {
/* 3527 */         SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
/* 3528 */         handleSelection(paramMouseEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void mousePressedDND(MouseEvent paramMouseEvent) {
/* 3533 */       this.pressedEvent = paramMouseEvent;
/* 3534 */       int i = 1;
/* 3535 */       this.dragStarted = false;
/* 3536 */       this.valueChangedOnPress = false;
/*      */ 
/* 3539 */       if ((isActualPath(this.pressedPath, paramMouseEvent.getX(), paramMouseEvent.getY())) && (DragRecognitionSupport.mousePressed(paramMouseEvent)))
/*      */       {
/* 3542 */         this.dragPressDidSelection = false;
/*      */ 
/* 3544 */         if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent))
/*      */         {
/* 3547 */           return;
/* 3548 */         }if ((!paramMouseEvent.isShiftDown()) && (BasicTreeUI.this.tree.isPathSelected(this.pressedPath)))
/*      */         {
/* 3551 */           BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
/* 3552 */           BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
/* 3553 */           return;
/*      */         }
/*      */ 
/* 3556 */         this.dragPressDidSelection = true;
/*      */ 
/* 3559 */         i = 0;
/*      */       }
/*      */ 
/* 3562 */       if (i != 0) {
/* 3563 */         SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
/*      */       }
/*      */ 
/* 3566 */       handleSelection(paramMouseEvent);
/*      */     }
/*      */ 
/*      */     void handleSelection(MouseEvent paramMouseEvent) {
/* 3570 */       if (this.pressedPath != null) {
/* 3571 */         Rectangle localRectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, this.pressedPath);
/*      */ 
/* 3573 */         if (paramMouseEvent.getY() >= localRectangle.y + localRectangle.height) {
/* 3574 */           return;
/*      */         }
/*      */ 
/* 3579 */         if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
/* 3580 */           BasicTreeUI.this.checkForClickInExpandControl(this.pressedPath, paramMouseEvent.getX(), paramMouseEvent.getY());
/*      */         }
/*      */ 
/* 3583 */         int i = paramMouseEvent.getX();
/*      */ 
/* 3587 */         if ((i >= localRectangle.x) && (i < localRectangle.x + localRectangle.width) && (
/* 3588 */           (BasicTreeUI.this.tree.getDragEnabled()) || (!BasicTreeUI.this.startEditing(this.pressedPath, paramMouseEvent))))
/* 3589 */           BasicTreeUI.this.selectPathForEvent(this.pressedPath, paramMouseEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void dragStarting(MouseEvent paramMouseEvent)
/*      */     {
/* 3596 */       this.dragStarted = true;
/*      */ 
/* 3598 */       if (BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent)) {
/* 3599 */         BasicTreeUI.this.tree.addSelectionPath(this.pressedPath);
/* 3600 */         BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
/* 3601 */         BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
/*      */       }
/*      */ 
/* 3604 */       this.pressedEvent = null;
/* 3605 */       this.pressedPath = null;
/*      */     }
/*      */ 
/*      */     public void mouseDragged(MouseEvent paramMouseEvent) {
/* 3609 */       if (SwingUtilities2.shouldIgnore(paramMouseEvent, BasicTreeUI.this.tree)) {
/* 3610 */         return;
/*      */       }
/*      */ 
/* 3613 */       if (BasicTreeUI.this.tree.getDragEnabled())
/* 3614 */         DragRecognitionSupport.mouseDragged(paramMouseEvent, this);
/*      */     }
/*      */ 
/*      */     public void mouseMoved(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseReleased(MouseEvent paramMouseEvent)
/*      */     {
/* 3626 */       if (SwingUtilities2.shouldIgnore(paramMouseEvent, BasicTreeUI.this.tree)) {
/* 3627 */         return;
/*      */       }
/*      */ 
/* 3630 */       if (BasicTreeUI.this.tree.getDragEnabled()) {
/* 3631 */         mouseReleasedDND(paramMouseEvent);
/*      */       }
/*      */ 
/* 3634 */       this.pressedEvent = null;
/* 3635 */       this.pressedPath = null;
/*      */     }
/*      */ 
/*      */     private void mouseReleasedDND(MouseEvent paramMouseEvent) {
/* 3639 */       MouseEvent localMouseEvent = DragRecognitionSupport.mouseReleased(paramMouseEvent);
/* 3640 */       if (localMouseEvent != null) {
/* 3641 */         SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
/* 3642 */         if (!this.dragPressDidSelection) {
/* 3643 */           handleSelection(localMouseEvent);
/*      */         }
/*      */       }
/*      */ 
/* 3647 */       if (!this.dragStarted)
/*      */       {
/* 3658 */         if ((this.pressedPath != null) && (!this.valueChangedOnPress) && (isActualPath(this.pressedPath, this.pressedEvent.getX(), this.pressedEvent.getY())))
/*      */         {
/* 3661 */           BasicTreeUI.this.startEditingOnRelease(this.pressedPath, this.pressedEvent, paramMouseEvent);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent)
/*      */     {
/* 3670 */       if (BasicTreeUI.this.tree != null)
/*      */       {
/* 3673 */         Rectangle localRectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.tree.getLeadSelectionPath());
/* 3674 */         if (localRectangle != null)
/* 3675 */           BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(localRectangle));
/* 3676 */         localRectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.getLeadSelectionPath());
/* 3677 */         if (localRectangle != null)
/* 3678 */           BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(localRectangle));
/*      */       }
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 3683 */       focusGained(paramFocusEvent);
/*      */     }
/*      */ 
/*      */     public void editingStopped(ChangeEvent paramChangeEvent)
/*      */     {
/* 3690 */       BasicTreeUI.this.completeEditing(false, false, true);
/*      */     }
/*      */ 
/*      */     public void editingCanceled(ChangeEvent paramChangeEvent)
/*      */     {
/* 3695 */       BasicTreeUI.this.completeEditing(false, false, false);
/*      */     }
/*      */ 
/*      */     public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
/*      */     {
/* 3703 */       this.valueChangedOnPress = true;
/*      */ 
/* 3706 */       BasicTreeUI.this.completeEditing();
/*      */ 
/* 3709 */       if ((BasicTreeUI.this.tree.getExpandsSelectedPaths()) && (BasicTreeUI.this.treeSelectionModel != null)) {
/* 3710 */         localObject1 = BasicTreeUI.this.treeSelectionModel.getSelectionPaths();
/*      */ 
/* 3713 */         if (localObject1 != null) {
/* 3714 */           for (int i = localObject1.length - 1; i >= 0; 
/* 3715 */             i--) {
/* 3716 */             localObject2 = localObject1[i].getParentPath();
/* 3717 */             int j = 1;
/*      */ 
/* 3719 */             while (localObject2 != null)
/*      */             {
/* 3722 */               if (BasicTreeUI.this.treeModel.isLeaf(((TreePath)localObject2).getLastPathComponent())) {
/* 3723 */                 j = 0;
/* 3724 */                 localObject2 = null;
/*      */               }
/*      */               else {
/* 3727 */                 localObject2 = ((TreePath)localObject2).getParentPath();
/*      */               }
/*      */             }
/* 3730 */             if (j != 0) {
/* 3731 */               BasicTreeUI.this.tree.makeVisible(localObject1[i]);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3737 */       Object localObject1 = BasicTreeUI.this.getLeadSelectionPath();
/* 3738 */       BasicTreeUI.this.lastSelectedRow = BasicTreeUI.this.tree.getMinSelectionRow();
/* 3739 */       TreePath localTreePath = BasicTreeUI.this.tree.getSelectionModel().getLeadSelectionPath();
/* 3740 */       BasicTreeUI.this.setAnchorSelectionPath(localTreePath);
/* 3741 */       BasicTreeUI.this.setLeadSelectionPath(localTreePath);
/*      */ 
/* 3743 */       Object localObject2 = paramTreeSelectionEvent.getPaths();
/*      */ 
/* 3745 */       Rectangle localRectangle2 = BasicTreeUI.this.tree.getVisibleRect();
/* 3746 */       int k = 1;
/* 3747 */       int m = BasicTreeUI.this.tree.getWidth();
/*      */       Rectangle localRectangle1;
/* 3749 */       if (localObject2 != null) {
/* 3750 */         int i1 = localObject2.length;
/*      */ 
/* 3752 */         if (i1 > 4) {
/* 3753 */           BasicTreeUI.this.tree.repaint();
/* 3754 */           k = 0;
/*      */         }
/*      */         else {
/* 3757 */           for (int n = 0; n < i1; n++) {
/* 3758 */             localRectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, localObject2[n]);
/*      */ 
/* 3760 */             if ((localRectangle1 != null) && (localRectangle2.intersects(localRectangle1)))
/*      */             {
/* 3762 */               BasicTreeUI.this.tree.repaint(0, localRectangle1.y, m, localRectangle1.height);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3767 */       if (k != 0) {
/* 3768 */         localRectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, (TreePath)localObject1);
/* 3769 */         if ((localRectangle1 != null) && (localRectangle2.intersects(localRectangle1)))
/* 3770 */           BasicTreeUI.this.tree.repaint(0, localRectangle1.y, m, localRectangle1.height);
/* 3771 */         localRectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, localTreePath);
/* 3772 */         if ((localRectangle1 != null) && (localRectangle2.intersects(localRectangle1)))
/* 3773 */           BasicTreeUI.this.tree.repaint(0, localRectangle1.y, m, localRectangle1.height);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent)
/*      */     {
/* 3782 */       if ((paramTreeExpansionEvent != null) && (BasicTreeUI.this.tree != null)) {
/* 3783 */         TreePath localTreePath = paramTreeExpansionEvent.getPath();
/*      */ 
/* 3785 */         BasicTreeUI.this.updateExpandedDescendants(localTreePath);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent) {
/* 3790 */       if ((paramTreeExpansionEvent != null) && (BasicTreeUI.this.tree != null)) {
/* 3791 */         TreePath localTreePath = paramTreeExpansionEvent.getPath();
/*      */ 
/* 3793 */         BasicTreeUI.this.completeEditing();
/* 3794 */         if ((localTreePath != null) && (BasicTreeUI.this.tree.isVisible(localTreePath))) {
/* 3795 */           BasicTreeUI.this.treeState.setExpandedState(localTreePath, false);
/* 3796 */           BasicTreeUI.this.updateLeadSelectionRow();
/* 3797 */           BasicTreeUI.this.updateSize();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
/*      */     {
/* 3806 */       if ((BasicTreeUI.this.treeState != null) && (paramTreeModelEvent != null)) {
/* 3807 */         TreePath localTreePath1 = paramTreeModelEvent.getTreePath();
/* 3808 */         int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
/* 3809 */         if ((arrayOfInt == null) || (arrayOfInt.length == 0))
/*      */         {
/* 3811 */           BasicTreeUI.this.treeState.treeNodesChanged(paramTreeModelEvent);
/* 3812 */           BasicTreeUI.this.updateSize();
/*      */         }
/* 3814 */         else if (BasicTreeUI.this.treeState.isExpanded(localTreePath1))
/*      */         {
/* 3818 */           int i = arrayOfInt[0];
/* 3819 */           for (int j = arrayOfInt.length - 1; j > 0; j--) {
/* 3820 */             i = Math.min(arrayOfInt[j], i);
/*      */           }
/* 3822 */           Object localObject = BasicTreeUI.this.treeModel.getChild(localTreePath1.getLastPathComponent(), i);
/*      */ 
/* 3824 */           TreePath localTreePath2 = localTreePath1.pathByAddingChild(localObject);
/* 3825 */           Rectangle localRectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, localTreePath2);
/*      */ 
/* 3828 */           BasicTreeUI.this.treeState.treeNodesChanged(paramTreeModelEvent);
/*      */ 
/* 3831 */           BasicTreeUI.this.updateSize0();
/*      */ 
/* 3834 */           Rectangle localRectangle2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, localTreePath2);
/* 3835 */           if ((arrayOfInt.length == 1) && (localRectangle2.height == localRectangle1.height))
/*      */           {
/* 3837 */             BasicTreeUI.this.tree.repaint(0, localRectangle1.y, BasicTreeUI.this.tree.getWidth(), localRectangle1.height);
/*      */           }
/*      */           else
/*      */           {
/* 3841 */             BasicTreeUI.this.tree.repaint(0, localRectangle1.y, BasicTreeUI.this.tree.getWidth(), BasicTreeUI.this.tree.getHeight() - localRectangle1.y);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 3847 */           BasicTreeUI.this.treeState.treeNodesChanged(paramTreeModelEvent);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void treeNodesInserted(TreeModelEvent paramTreeModelEvent) {
/* 3853 */       if ((BasicTreeUI.this.treeState != null) && (paramTreeModelEvent != null)) {
/* 3854 */         BasicTreeUI.this.treeState.treeNodesInserted(paramTreeModelEvent);
/*      */ 
/* 3856 */         BasicTreeUI.this.updateLeadSelectionRow();
/*      */ 
/* 3858 */         TreePath localTreePath = paramTreeModelEvent.getTreePath();
/*      */ 
/* 3860 */         if (BasicTreeUI.this.treeState.isExpanded(localTreePath)) {
/* 3861 */           BasicTreeUI.this.updateSize();
/*      */         }
/*      */         else
/*      */         {
/* 3867 */           int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
/* 3868 */           int i = BasicTreeUI.this.treeModel.getChildCount(localTreePath.getLastPathComponent());
/*      */ 
/* 3871 */           if ((arrayOfInt != null) && (i - arrayOfInt.length == 0))
/* 3872 */             BasicTreeUI.this.updateSize();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent) {
/* 3878 */       if ((BasicTreeUI.this.treeState != null) && (paramTreeModelEvent != null)) {
/* 3879 */         BasicTreeUI.this.treeState.treeNodesRemoved(paramTreeModelEvent);
/*      */ 
/* 3881 */         BasicTreeUI.this.updateLeadSelectionRow();
/*      */ 
/* 3883 */         TreePath localTreePath = paramTreeModelEvent.getTreePath();
/*      */ 
/* 3885 */         if ((BasicTreeUI.this.treeState.isExpanded(localTreePath)) || (BasicTreeUI.this.treeModel.getChildCount(localTreePath.getLastPathComponent()) == 0))
/*      */         {
/* 3887 */           BasicTreeUI.this.updateSize();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3892 */     public void treeStructureChanged(TreeModelEvent paramTreeModelEvent) { if ((BasicTreeUI.this.treeState != null) && (paramTreeModelEvent != null)) {
/* 3893 */         BasicTreeUI.this.treeState.treeStructureChanged(paramTreeModelEvent);
/*      */ 
/* 3895 */         BasicTreeUI.this.updateLeadSelectionRow();
/*      */ 
/* 3897 */         TreePath localTreePath = paramTreeModelEvent.getTreePath();
/*      */ 
/* 3899 */         if (localTreePath != null) {
/* 3900 */           localTreePath = localTreePath.getParentPath();
/*      */         }
/* 3902 */         if ((localTreePath == null) || (BasicTreeUI.this.treeState.isExpanded(localTreePath)))
/* 3903 */           BasicTreeUI.this.updateSize();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public class KeyHandler extends KeyAdapter
/*      */   {
/*      */     protected Action repeatKeyAction;
/*      */     protected boolean isKeyDown;
/*      */ 
/*      */     public KeyHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void keyTyped(KeyEvent paramKeyEvent)
/*      */     {
/* 2708 */       BasicTreeUI.this.getHandler().keyTyped(paramKeyEvent);
/*      */     }
/*      */ 
/*      */     public void keyPressed(KeyEvent paramKeyEvent) {
/* 2712 */       BasicTreeUI.this.getHandler().keyPressed(paramKeyEvent);
/*      */     }
/*      */ 
/*      */     public void keyReleased(KeyEvent paramKeyEvent) {
/* 2716 */       BasicTreeUI.this.getHandler().keyReleased(paramKeyEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class MouseHandler extends MouseAdapter
/*      */     implements MouseMotionListener
/*      */   {
/*      */     public MouseHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mousePressed(MouseEvent paramMouseEvent)
/*      */     {
/* 2836 */       BasicTreeUI.this.getHandler().mousePressed(paramMouseEvent);
/*      */     }
/*      */ 
/*      */     public void mouseDragged(MouseEvent paramMouseEvent) {
/* 2840 */       BasicTreeUI.this.getHandler().mouseDragged(paramMouseEvent);
/*      */     }
/*      */ 
/*      */     public void mouseMoved(MouseEvent paramMouseEvent)
/*      */     {
/* 2849 */       BasicTreeUI.this.getHandler().mouseMoved(paramMouseEvent);
/*      */     }
/*      */ 
/*      */     public void mouseReleased(MouseEvent paramMouseEvent) {
/* 2853 */       BasicTreeUI.this.getHandler().mouseReleased(paramMouseEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class MouseInputHandler
/*      */     implements MouseInputListener
/*      */   {
/*      */     protected Component source;
/*      */     protected Component destination;
/*      */     private Component focusComponent;
/*      */     private boolean dispatchedEvent;
/*      */ 
/*      */     public MouseInputHandler(Component paramComponent1, Component paramMouseEvent, MouseEvent arg4)
/*      */     {
/* 3090 */       this(paramComponent1, paramMouseEvent, localMouseEvent, null);
/*      */     }
/*      */ 
/*      */     MouseInputHandler(Component paramComponent1, Component paramMouseEvent, MouseEvent paramComponent2, Component arg5)
/*      */     {
/* 3095 */       this.source = paramComponent1;
/* 3096 */       this.destination = paramMouseEvent;
/* 3097 */       this.source.addMouseListener(this);
/* 3098 */       this.source.addMouseMotionListener(this);
/*      */ 
/* 3100 */       SwingUtilities2.setSkipClickCount(paramMouseEvent, paramComponent2.getClickCount() - 1);
/*      */ 
/* 3104 */       paramMouseEvent.dispatchEvent(SwingUtilities.convertMouseEvent(paramComponent1, paramComponent2, paramMouseEvent));
/*      */       Object localObject;
/* 3106 */       this.focusComponent = localObject;
/*      */     }
/*      */ 
/*      */     public void mouseClicked(MouseEvent paramMouseEvent) {
/* 3110 */       if (this.destination != null) {
/* 3111 */         this.dispatchedEvent = true;
/* 3112 */         this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, paramMouseEvent, this.destination));
/*      */       }
/*      */     }
/*      */ 
/*      */     public void mousePressed(MouseEvent paramMouseEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseReleased(MouseEvent paramMouseEvent) {
/* 3121 */       if (this.destination != null) {
/* 3122 */         this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, paramMouseEvent, this.destination));
/*      */       }
/* 3124 */       removeFromSource();
/*      */     }
/*      */ 
/*      */     public void mouseEntered(MouseEvent paramMouseEvent) {
/* 3128 */       if (!SwingUtilities.isLeftMouseButton(paramMouseEvent))
/* 3129 */         removeFromSource();
/*      */     }
/*      */ 
/*      */     public void mouseExited(MouseEvent paramMouseEvent)
/*      */     {
/* 3134 */       if (!SwingUtilities.isLeftMouseButton(paramMouseEvent))
/* 3135 */         removeFromSource();
/*      */     }
/*      */ 
/*      */     public void mouseDragged(MouseEvent paramMouseEvent)
/*      */     {
/* 3140 */       if (this.destination != null) {
/* 3141 */         this.dispatchedEvent = true;
/* 3142 */         this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, paramMouseEvent, this.destination));
/*      */       }
/*      */     }
/*      */ 
/*      */     public void mouseMoved(MouseEvent paramMouseEvent)
/*      */     {
/* 3148 */       removeFromSource();
/*      */     }
/*      */ 
/*      */     protected void removeFromSource() {
/* 3152 */       if (this.source != null) {
/* 3153 */         this.source.removeMouseListener(this);
/* 3154 */         this.source.removeMouseMotionListener(this);
/* 3155 */         if ((this.focusComponent != null) && (this.focusComponent == this.destination) && (!this.dispatchedEvent) && ((this.focusComponent instanceof JTextField)))
/*      */         {
/* 3158 */           ((JTextField)this.focusComponent).selectAll();
/*      */         }
/*      */       }
/* 3161 */       this.source = (this.destination = null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class NodeDimensionsHandler extends AbstractLayoutCache.NodeDimensions
/*      */   {
/*      */     public NodeDimensionsHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Rectangle getNodeDimensions(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean, Rectangle paramRectangle)
/*      */     {
/*      */       Object localObject;
/* 2764 */       if ((BasicTreeUI.this.editingComponent != null) && (BasicTreeUI.this.editingRow == paramInt1)) {
/* 2765 */         localObject = BasicTreeUI.this.editingComponent.getPreferredSize();
/*      */ 
/* 2767 */         int i = BasicTreeUI.this.getRowHeight();
/*      */ 
/* 2769 */         if ((i > 0) && (i != ((Dimension)localObject).height))
/* 2770 */           ((Dimension)localObject).height = i;
/* 2771 */         if (paramRectangle != null) {
/* 2772 */           paramRectangle.x = getRowX(paramInt1, paramInt2);
/* 2773 */           paramRectangle.width = ((Dimension)localObject).width;
/* 2774 */           paramRectangle.height = ((Dimension)localObject).height;
/*      */         }
/*      */         else {
/* 2777 */           paramRectangle = new Rectangle(getRowX(paramInt1, paramInt2), 0, ((Dimension)localObject).width, ((Dimension)localObject).height);
/*      */         }
/*      */ 
/* 2780 */         return paramRectangle;
/*      */       }
/*      */ 
/* 2783 */       if (BasicTreeUI.this.currentCellRenderer != null)
/*      */       {
/* 2786 */         localObject = BasicTreeUI.this.currentCellRenderer.getTreeCellRendererComponent(BasicTreeUI.this.tree, paramObject, BasicTreeUI.this.tree.isRowSelected(paramInt1), paramBoolean, BasicTreeUI.this.treeModel.isLeaf(paramObject), paramInt1, false);
/*      */ 
/* 2790 */         if (BasicTreeUI.this.tree != null)
/*      */         {
/* 2792 */           BasicTreeUI.this.rendererPane.add((Component)localObject);
/* 2793 */           ((Component)localObject).validate();
/*      */         }
/* 2795 */         Dimension localDimension = ((Component)localObject).getPreferredSize();
/*      */ 
/* 2797 */         if (paramRectangle != null) {
/* 2798 */           paramRectangle.x = getRowX(paramInt1, paramInt2);
/* 2799 */           paramRectangle.width = localDimension.width;
/* 2800 */           paramRectangle.height = localDimension.height;
/*      */         }
/*      */         else {
/* 2803 */           paramRectangle = new Rectangle(getRowX(paramInt1, paramInt2), 0, localDimension.width, localDimension.height);
/*      */         }
/*      */ 
/* 2806 */         return paramRectangle;
/*      */       }
/* 2808 */       return null;
/*      */     }
/*      */ 
/*      */     protected int getRowX(int paramInt1, int paramInt2)
/*      */     {
/* 2815 */       return BasicTreeUI.this.getRowX(paramInt1, paramInt2);
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
/* 2871 */       BasicTreeUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class SelectionModelPropertyChangeHandler
/*      */     implements PropertyChangeListener
/*      */   {
/*      */     public SelectionModelPropertyChangeHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 2889 */       BasicTreeUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeCancelEditingAction extends AbstractAction
/*      */   {
/*      */     public TreeCancelEditingAction(String arg2)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 3061 */       if (BasicTreeUI.this.tree != null)
/* 3062 */         BasicTreeUI.SHARED_ACTION.cancelEditing(BasicTreeUI.this.tree, BasicTreeUI.this);
/*      */     }
/*      */ 
/*      */     public boolean isEnabled() {
/* 3066 */       return (BasicTreeUI.this.tree != null) && (BasicTreeUI.this.tree.isEnabled()) && (BasicTreeUI.this.isEditing(BasicTreeUI.this.tree));
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeExpansionHandler
/*      */     implements TreeExpansionListener
/*      */   {
/*      */     public TreeExpansionHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent)
/*      */     {
/* 2517 */       BasicTreeUI.this.getHandler().treeExpanded(paramTreeExpansionEvent);
/*      */     }
/*      */ 
/*      */     public void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent)
/*      */     {
/* 2524 */       BasicTreeUI.this.getHandler().treeCollapsed(paramTreeExpansionEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeHomeAction extends AbstractAction
/*      */   {
/*      */     protected int direction;
/*      */     private boolean addToSelection;
/*      */     private boolean changeSelection;
/*      */ 
/*      */     public TreeHomeAction(int paramString, String arg3)
/*      */     {
/* 3010 */       this(paramString, str, false, true);
/*      */     }
/*      */ 
/*      */     private TreeHomeAction(int paramString, String paramBoolean1, boolean paramBoolean2, boolean arg5)
/*      */     {
/* 3016 */       this.direction = paramString;
/*      */       boolean bool;
/* 3017 */       this.changeSelection = bool;
/* 3018 */       this.addToSelection = paramBoolean2;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 3022 */       if (BasicTreeUI.this.tree != null)
/* 3023 */         BasicTreeUI.SHARED_ACTION.home(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
/*      */     }
/*      */ 
/*      */     public boolean isEnabled()
/*      */     {
/* 3028 */       return (BasicTreeUI.this.tree != null) && (BasicTreeUI.this.tree.isEnabled());
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeIncrementAction extends AbstractAction
/*      */   {
/*      */     protected int direction;
/*      */     private boolean addToSelection;
/*      */     private boolean changeSelection;
/*      */ 
/*      */     public TreeIncrementAction(int paramString, String arg3)
/*      */     {
/* 2975 */       this(paramString, str, false, true);
/*      */     }
/*      */ 
/*      */     private TreeIncrementAction(int paramString, String paramBoolean1, boolean paramBoolean2, boolean arg5)
/*      */     {
/* 2981 */       this.direction = paramString;
/* 2982 */       this.addToSelection = paramBoolean2;
/*      */       boolean bool;
/* 2983 */       this.changeSelection = bool;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2987 */       if (BasicTreeUI.this.tree != null)
/* 2988 */         BasicTreeUI.SHARED_ACTION.increment(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
/*      */     }
/*      */ 
/*      */     public boolean isEnabled()
/*      */     {
/* 2993 */       return (BasicTreeUI.this.tree != null) && (BasicTreeUI.this.tree.isEnabled());
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeModelHandler
/*      */     implements TreeModelListener
/*      */   {
/*      */     public TreeModelHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
/*      */     {
/* 2616 */       BasicTreeUI.this.getHandler().treeNodesChanged(paramTreeModelEvent);
/*      */     }
/*      */ 
/*      */     public void treeNodesInserted(TreeModelEvent paramTreeModelEvent) {
/* 2620 */       BasicTreeUI.this.getHandler().treeNodesInserted(paramTreeModelEvent);
/*      */     }
/*      */ 
/*      */     public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent) {
/* 2624 */       BasicTreeUI.this.getHandler().treeNodesRemoved(paramTreeModelEvent);
/*      */     }
/*      */ 
/*      */     public void treeStructureChanged(TreeModelEvent paramTreeModelEvent) {
/* 2628 */       BasicTreeUI.this.getHandler().treeStructureChanged(paramTreeModelEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreePageAction extends AbstractAction
/*      */   {
/*      */     protected int direction;
/*      */     private boolean addToSelection;
/*      */     private boolean changeSelection;
/*      */ 
/*      */     public TreePageAction(int paramString, String arg3)
/*      */     {
/* 2939 */       this(paramString, str, false, true);
/*      */     }
/*      */ 
/*      */     private TreePageAction(int paramString, String paramBoolean1, boolean paramBoolean2, boolean arg5)
/*      */     {
/* 2945 */       this.direction = paramString;
/* 2946 */       this.addToSelection = paramBoolean2;
/*      */       boolean bool;
/* 2947 */       this.changeSelection = bool;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2951 */       if (BasicTreeUI.this.tree != null)
/* 2952 */         BasicTreeUI.SHARED_ACTION.page(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
/*      */     }
/*      */ 
/*      */     public boolean isEnabled()
/*      */     {
/* 2957 */       return (BasicTreeUI.this.tree != null) && (BasicTreeUI.this.tree.isEnabled());
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeSelectionHandler
/*      */     implements TreeSelectionListener
/*      */   {
/*      */     public TreeSelectionHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
/*      */     {
/* 2649 */       BasicTreeUI.this.getHandler().valueChanged(paramTreeSelectionEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeToggleAction extends AbstractAction
/*      */   {
/*      */     public TreeToggleAction(String arg2)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 3042 */       if (BasicTreeUI.this.tree != null)
/* 3043 */         BasicTreeUI.SHARED_ACTION.toggle(BasicTreeUI.this.tree, BasicTreeUI.this);
/*      */     }
/*      */ 
/*      */     public boolean isEnabled() {
/* 3047 */       return (BasicTreeUI.this.tree != null) && (BasicTreeUI.this.tree.isEnabled());
/*      */     }
/*      */   }
/*      */ 
/*      */   static class TreeTransferHandler extends TransferHandler
/*      */     implements UIResource, Comparator<TreePath>
/*      */   {
/*      */     private JTree tree;
/*      */ 
/*      */     protected Transferable createTransferable(JComponent paramJComponent)
/*      */     {
/* 3182 */       if ((paramJComponent instanceof JTree)) {
/* 3183 */         this.tree = ((JTree)paramJComponent);
/* 3184 */         TreePath[] arrayOfTreePath1 = this.tree.getSelectionPaths();
/*      */ 
/* 3186 */         if ((arrayOfTreePath1 == null) || (arrayOfTreePath1.length == 0)) {
/* 3187 */           return null;
/*      */         }
/*      */ 
/* 3190 */         StringBuffer localStringBuffer1 = new StringBuffer();
/* 3191 */         StringBuffer localStringBuffer2 = new StringBuffer();
/*      */ 
/* 3193 */         localStringBuffer2.append("<html>\n<body>\n<ul>\n");
/*      */ 
/* 3195 */         TreeModel localTreeModel = this.tree.getModel();
/* 3196 */         Object localObject1 = null;
/* 3197 */         TreePath[] arrayOfTreePath2 = getDisplayOrderPaths(arrayOfTreePath1);
/*      */ 
/* 3199 */         for (TreePath localTreePath : arrayOfTreePath2) {
/* 3200 */           Object localObject2 = localTreePath.getLastPathComponent();
/* 3201 */           boolean bool = localTreeModel.isLeaf(localObject2);
/* 3202 */           String str = getDisplayString(localTreePath, true, bool);
/*      */ 
/* 3204 */           localStringBuffer1.append(str + "\n");
/* 3205 */           localStringBuffer2.append("  <li>" + str + "\n");
/*      */         }
/*      */ 
/* 3209 */         localStringBuffer1.deleteCharAt(localStringBuffer1.length() - 1);
/* 3210 */         localStringBuffer2.append("</ul>\n</body>\n</html>");
/*      */ 
/* 3212 */         this.tree = null;
/*      */ 
/* 3214 */         return new BasicTransferable(localStringBuffer1.toString(), localStringBuffer2.toString());
/*      */       }
/*      */ 
/* 3217 */       return null;
/*      */     }
/*      */ 
/*      */     public int compare(TreePath paramTreePath1, TreePath paramTreePath2) {
/* 3221 */       int i = this.tree.getRowForPath(paramTreePath1);
/* 3222 */       int j = this.tree.getRowForPath(paramTreePath2);
/* 3223 */       return i - j;
/*      */     }
/*      */ 
/*      */     String getDisplayString(TreePath paramTreePath, boolean paramBoolean1, boolean paramBoolean2) {
/* 3227 */       int i = this.tree.getRowForPath(paramTreePath);
/* 3228 */       boolean bool = this.tree.getLeadSelectionRow() == i;
/* 3229 */       Object localObject = paramTreePath.getLastPathComponent();
/* 3230 */       return this.tree.convertValueToText(localObject, paramBoolean1, this.tree.isExpanded(i), paramBoolean2, i, bool);
/*      */     }
/*      */ 
/*      */     TreePath[] getDisplayOrderPaths(TreePath[] paramArrayOfTreePath)
/*      */     {
/* 3241 */       ArrayList localArrayList = new ArrayList();
/* 3242 */       for (TreePath localTreePath : paramArrayOfTreePath) {
/* 3243 */         localArrayList.add(localTreePath);
/*      */       }
/* 3245 */       Collections.sort(localArrayList, this);
/* 3246 */       int i = localArrayList.size();
/* 3247 */       TreePath[] arrayOfTreePath2 = new TreePath[i];
/* 3248 */       for (??? = 0; ??? < i; ???++) {
/* 3249 */         arrayOfTreePath2[???] = ((TreePath)localArrayList.get(???));
/*      */       }
/* 3251 */       return arrayOfTreePath2;
/*      */     }
/*      */ 
/*      */     public int getSourceActions(JComponent paramJComponent) {
/* 3255 */       return 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class TreeTraverseAction extends AbstractAction
/*      */   {
/*      */     protected int direction;
/*      */     private boolean changeSelection;
/*      */ 
/*      */     public TreeTraverseAction(int paramString, String arg3)
/*      */     {
/* 2908 */       this(paramString, str, true);
/*      */     }
/*      */ 
/*      */     private TreeTraverseAction(int paramString, String paramBoolean, boolean arg4)
/*      */     {
/* 2913 */       this.direction = paramString;
/*      */       boolean bool;
/* 2914 */       this.changeSelection = bool;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2918 */       if (BasicTreeUI.this.tree != null)
/* 2919 */         BasicTreeUI.SHARED_ACTION.traverse(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.changeSelection);
/*      */     }
/*      */ 
/*      */     public boolean isEnabled()
/*      */     {
/* 2924 */       return (BasicTreeUI.this.tree != null) && (BasicTreeUI.this.tree.isEnabled());
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.basic.BasicTreeUI
 * JD-Core Version:    0.6.2
 */