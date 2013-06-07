/*      */ package javax.swing;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.beans.ConstructorProperties;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.Accessible;
/*      */ import javax.accessibility.AccessibleAction;
/*      */ import javax.accessibility.AccessibleComponent;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.accessibility.AccessibleRole;
/*      */ import javax.accessibility.AccessibleSelection;
/*      */ import javax.accessibility.AccessibleState;
/*      */ import javax.accessibility.AccessibleStateSet;
/*      */ import javax.accessibility.AccessibleText;
/*      */ import javax.accessibility.AccessibleValue;
/*      */ import javax.swing.event.EventListenerList;
/*      */ import javax.swing.event.TreeExpansionEvent;
/*      */ import javax.swing.event.TreeExpansionListener;
/*      */ import javax.swing.event.TreeModelEvent;
/*      */ import javax.swing.event.TreeModelListener;
/*      */ import javax.swing.event.TreeSelectionEvent;
/*      */ import javax.swing.event.TreeSelectionListener;
/*      */ import javax.swing.event.TreeWillExpandListener;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.TreeUI;
/*      */ import javax.swing.text.Position.Bias;
/*      */ import javax.swing.tree.DefaultMutableTreeNode;
/*      */ import javax.swing.tree.DefaultTreeModel;
/*      */ import javax.swing.tree.DefaultTreeSelectionModel;
/*      */ import javax.swing.tree.ExpandVetoException;
/*      */ import javax.swing.tree.RowMapper;
/*      */ import javax.swing.tree.TreeCellEditor;
/*      */ import javax.swing.tree.TreeCellRenderer;
/*      */ import javax.swing.tree.TreeModel;
/*      */ import javax.swing.tree.TreeNode;
/*      */ import javax.swing.tree.TreePath;
/*      */ import javax.swing.tree.TreeSelectionModel;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.SwingUtilities2.Section;
/*      */ 
/*      */ public class JTree extends JComponent
/*      */   implements Scrollable, Accessible
/*      */ {
/*      */   private static final String uiClassID = "TreeUI";
/*      */   protected transient TreeModel treeModel;
/*      */   protected transient TreeSelectionModel selectionModel;
/*      */   protected boolean rootVisible;
/*      */   protected transient TreeCellRenderer cellRenderer;
/*      */   protected int rowHeight;
/*  180 */   private boolean rowHeightSet = false;
/*      */   private transient Hashtable<TreePath, Boolean> expandedState;
/*      */   protected boolean showsRootHandles;
/*  216 */   private boolean showsRootHandlesSet = false;
/*      */   protected transient TreeSelectionRedirector selectionRedirector;
/*      */   protected transient TreeCellEditor cellEditor;
/*      */   protected boolean editable;
/*      */   protected boolean largeModel;
/*      */   protected int visibleRowCount;
/*      */   protected boolean invokesStopCellEditing;
/*      */   protected boolean scrollsOnExpand;
/*  268 */   private boolean scrollsOnExpandSet = false;
/*      */   protected int toggleClickCount;
/*      */   protected transient TreeModelListener treeModelListener;
/*      */   private transient Stack<Stack<TreePath>> expandedStack;
/*      */   private TreePath leadPath;
/*      */   private TreePath anchorPath;
/*      */   private boolean expandsSelectedPaths;
/*      */   private boolean settingUI;
/*      */   private boolean dragEnabled;
/*  312 */   private DropMode dropMode = DropMode.USE_SELECTION;
/*      */   private transient DropLocation dropLocation;
/*  422 */   private int expandRow = -1;
/*      */   private TreeTimer dropTimer;
/*      */   private transient TreeExpansionListener uiTreeExpansionListener;
/*  453 */   private static int TEMP_STACK_SIZE = 11;
/*      */   public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
/*      */   public static final String TREE_MODEL_PROPERTY = "model";
/*      */   public static final String ROOT_VISIBLE_PROPERTY = "rootVisible";
/*      */   public static final String SHOWS_ROOT_HANDLES_PROPERTY = "showsRootHandles";
/*      */   public static final String ROW_HEIGHT_PROPERTY = "rowHeight";
/*      */   public static final String CELL_EDITOR_PROPERTY = "cellEditor";
/*      */   public static final String EDITABLE_PROPERTY = "editable";
/*      */   public static final String LARGE_MODEL_PROPERTY = "largeModel";
/*      */   public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
/*      */   public static final String VISIBLE_ROW_COUNT_PROPERTY = "visibleRowCount";
/*      */   public static final String INVOKES_STOP_CELL_EDITING_PROPERTY = "invokesStopCellEditing";
/*      */   public static final String SCROLLS_ON_EXPAND_PROPERTY = "scrollsOnExpand";
/*      */   public static final String TOGGLE_CLICK_COUNT_PROPERTY = "toggleClickCount";
/*      */   public static final String LEAD_SELECTION_PATH_PROPERTY = "leadSelectionPath";
/*      */   public static final String ANCHOR_SELECTION_PATH_PROPERTY = "anchorSelectionPath";
/*      */   public static final String EXPANDS_SELECTED_PATHS_PROPERTY = "expandsSelectedPaths";
/*      */ 
/*      */   protected static TreeModel getDefaultTreeModel()
/*      */   {
/*  502 */     DefaultMutableTreeNode localDefaultMutableTreeNode1 = new DefaultMutableTreeNode("JTree");
/*      */ 
/*  505 */     DefaultMutableTreeNode localDefaultMutableTreeNode2 = new DefaultMutableTreeNode("colors");
/*  506 */     localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode2);
/*  507 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("blue"));
/*  508 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("violet"));
/*  509 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("red"));
/*  510 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("yellow"));
/*      */ 
/*  512 */     localDefaultMutableTreeNode2 = new DefaultMutableTreeNode("sports");
/*  513 */     localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode2);
/*  514 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("basketball"));
/*  515 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("soccer"));
/*  516 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("football"));
/*  517 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("hockey"));
/*      */ 
/*  519 */     localDefaultMutableTreeNode2 = new DefaultMutableTreeNode("food");
/*  520 */     localDefaultMutableTreeNode1.add(localDefaultMutableTreeNode2);
/*  521 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("hot dogs"));
/*  522 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("pizza"));
/*  523 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("ravioli"));
/*  524 */     localDefaultMutableTreeNode2.add(new DefaultMutableTreeNode("bananas"));
/*  525 */     return new DefaultTreeModel(localDefaultMutableTreeNode1);
/*      */   }
/*      */ 
/*      */   protected static TreeModel createTreeModel(Object paramObject)
/*      */   {
/*      */     Object localObject;
/*  545 */     if (((paramObject instanceof Object[])) || ((paramObject instanceof Hashtable)) || ((paramObject instanceof Vector)))
/*      */     {
/*  547 */       localObject = new DefaultMutableTreeNode("root");
/*  548 */       DynamicUtilTreeNode.createChildren((DefaultMutableTreeNode)localObject, paramObject);
/*      */     }
/*      */     else {
/*  551 */       localObject = new DynamicUtilTreeNode("root", paramObject);
/*      */     }
/*  553 */     return new DefaultTreeModel((TreeNode)localObject, false);
/*      */   }
/*      */ 
/*      */   public JTree()
/*      */   {
/*  564 */     this(getDefaultTreeModel());
/*      */   }
/*      */ 
/*      */   public JTree(Object[] paramArrayOfObject)
/*      */   {
/*  578 */     this(createTreeModel(paramArrayOfObject));
/*  579 */     setRootVisible(false);
/*  580 */     setShowsRootHandles(true);
/*  581 */     expandRoot();
/*      */   }
/*      */ 
/*      */   public JTree(Vector<?> paramVector)
/*      */   {
/*  594 */     this(createTreeModel(paramVector));
/*  595 */     setRootVisible(false);
/*  596 */     setShowsRootHandles(true);
/*  597 */     expandRoot();
/*      */   }
/*      */ 
/*      */   public JTree(Hashtable<?, ?> paramHashtable)
/*      */   {
/*  611 */     this(createTreeModel(paramHashtable));
/*  612 */     setRootVisible(false);
/*  613 */     setShowsRootHandles(true);
/*  614 */     expandRoot();
/*      */   }
/*      */ 
/*      */   public JTree(TreeNode paramTreeNode)
/*      */   {
/*  627 */     this(paramTreeNode, false);
/*      */   }
/*      */ 
/*      */   public JTree(TreeNode paramTreeNode, boolean paramBoolean)
/*      */   {
/*  643 */     this(new DefaultTreeModel(paramTreeNode, paramBoolean));
/*      */   }
/*      */ 
/*      */   @ConstructorProperties({"model"})
/*      */   public JTree(TreeModel paramTreeModel)
/*      */   {
/*  655 */     this.expandedStack = new Stack();
/*  656 */     this.toggleClickCount = 2;
/*  657 */     this.expandedState = new Hashtable();
/*  658 */     setLayout(null);
/*  659 */     this.rowHeight = 16;
/*  660 */     this.visibleRowCount = 20;
/*  661 */     this.rootVisible = true;
/*  662 */     this.selectionModel = new DefaultTreeSelectionModel();
/*  663 */     this.cellRenderer = null;
/*  664 */     this.scrollsOnExpand = true;
/*  665 */     setOpaque(true);
/*  666 */     this.expandsSelectedPaths = true;
/*  667 */     updateUI();
/*  668 */     setModel(paramTreeModel);
/*      */   }
/*      */ 
/*      */   public TreeUI getUI()
/*      */   {
/*  677 */     return (TreeUI)this.ui;
/*      */   }
/*      */ 
/*      */   public void setUI(TreeUI paramTreeUI)
/*      */   {
/*  694 */     if (this.ui != paramTreeUI) {
/*  695 */       this.settingUI = true;
/*  696 */       this.uiTreeExpansionListener = null;
/*      */       try {
/*  698 */         super.setUI(paramTreeUI);
/*      */       }
/*      */       finally {
/*  701 */         this.settingUI = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateUI()
/*      */   {
/*  714 */     setUI((TreeUI)UIManager.getUI(this));
/*      */ 
/*  716 */     SwingUtilities.updateRendererOrEditorUI(getCellRenderer());
/*  717 */     SwingUtilities.updateRendererOrEditorUI(getCellEditor());
/*      */   }
/*      */ 
/*      */   public String getUIClassID()
/*      */   {
/*  729 */     return "TreeUI";
/*      */   }
/*      */ 
/*      */   public TreeCellRenderer getCellRenderer()
/*      */   {
/*  740 */     return this.cellRenderer;
/*      */   }
/*      */ 
/*      */   public void setCellRenderer(TreeCellRenderer paramTreeCellRenderer)
/*      */   {
/*  756 */     TreeCellRenderer localTreeCellRenderer = this.cellRenderer;
/*      */ 
/*  758 */     this.cellRenderer = paramTreeCellRenderer;
/*  759 */     firePropertyChange("cellRenderer", localTreeCellRenderer, this.cellRenderer);
/*  760 */     invalidate();
/*      */   }
/*      */ 
/*      */   public void setEditable(boolean paramBoolean)
/*      */   {
/*  776 */     boolean bool = this.editable;
/*      */ 
/*  778 */     this.editable = paramBoolean;
/*  779 */     firePropertyChange("editable", bool, paramBoolean);
/*  780 */     if (this.accessibleContext != null)
/*  781 */       this.accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.EDITABLE : null, paramBoolean ? AccessibleState.EDITABLE : null);
/*      */   }
/*      */ 
/*      */   public boolean isEditable()
/*      */   {
/*  794 */     return this.editable;
/*      */   }
/*      */ 
/*      */   public void setCellEditor(TreeCellEditor paramTreeCellEditor)
/*      */   {
/*  812 */     TreeCellEditor localTreeCellEditor = this.cellEditor;
/*      */ 
/*  814 */     this.cellEditor = paramTreeCellEditor;
/*  815 */     firePropertyChange("cellEditor", localTreeCellEditor, paramTreeCellEditor);
/*  816 */     invalidate();
/*      */   }
/*      */ 
/*      */   public TreeCellEditor getCellEditor()
/*      */   {
/*  826 */     return this.cellEditor;
/*      */   }
/*      */ 
/*      */   public TreeModel getModel()
/*      */   {
/*  835 */     return this.treeModel;
/*      */   }
/*      */ 
/*      */   public void setModel(TreeModel paramTreeModel)
/*      */   {
/*  849 */     clearSelection();
/*      */ 
/*  851 */     TreeModel localTreeModel = this.treeModel;
/*      */ 
/*  853 */     if ((this.treeModel != null) && (this.treeModelListener != null)) {
/*  854 */       this.treeModel.removeTreeModelListener(this.treeModelListener);
/*      */     }
/*  856 */     if (this.accessibleContext != null) {
/*  857 */       if (this.treeModel != null) {
/*  858 */         this.treeModel.removeTreeModelListener((TreeModelListener)this.accessibleContext);
/*      */       }
/*  860 */       if (paramTreeModel != null) {
/*  861 */         paramTreeModel.addTreeModelListener((TreeModelListener)this.accessibleContext);
/*      */       }
/*      */     }
/*      */ 
/*  865 */     this.treeModel = paramTreeModel;
/*  866 */     clearToggledPaths();
/*  867 */     if (this.treeModel != null) {
/*  868 */       if (this.treeModelListener == null)
/*  869 */         this.treeModelListener = createTreeModelListener();
/*  870 */       if (this.treeModelListener != null) {
/*  871 */         this.treeModel.addTreeModelListener(this.treeModelListener);
/*      */       }
/*  873 */       if ((this.treeModel.getRoot() != null) && (!this.treeModel.isLeaf(this.treeModel.getRoot())))
/*      */       {
/*  875 */         this.expandedState.put(new TreePath(this.treeModel.getRoot()), Boolean.TRUE);
/*      */       }
/*      */     }
/*      */ 
/*  879 */     firePropertyChange("model", localTreeModel, this.treeModel);
/*  880 */     invalidate();
/*      */   }
/*      */ 
/*      */   public boolean isRootVisible()
/*      */   {
/*  890 */     return this.rootVisible;
/*      */   }
/*      */ 
/*      */   public void setRootVisible(boolean paramBoolean)
/*      */   {
/*  907 */     boolean bool = this.rootVisible;
/*      */ 
/*  909 */     this.rootVisible = paramBoolean;
/*  910 */     firePropertyChange("rootVisible", bool, this.rootVisible);
/*  911 */     if (this.accessibleContext != null)
/*  912 */       ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
/*      */   }
/*      */ 
/*      */   public void setShowsRootHandles(boolean paramBoolean)
/*      */   {
/*  936 */     boolean bool = this.showsRootHandles;
/*  937 */     TreeModel localTreeModel = getModel();
/*      */ 
/*  939 */     this.showsRootHandles = paramBoolean;
/*  940 */     this.showsRootHandlesSet = true;
/*  941 */     firePropertyChange("showsRootHandles", bool, this.showsRootHandles);
/*      */ 
/*  943 */     if (this.accessibleContext != null) {
/*  944 */       ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
/*      */     }
/*  946 */     invalidate();
/*      */   }
/*      */ 
/*      */   public boolean getShowsRootHandles()
/*      */   {
/*  957 */     return this.showsRootHandles;
/*      */   }
/*      */ 
/*      */   public void setRowHeight(int paramInt)
/*      */   {
/*  974 */     int i = this.rowHeight;
/*      */ 
/*  976 */     this.rowHeight = paramInt;
/*  977 */     this.rowHeightSet = true;
/*  978 */     firePropertyChange("rowHeight", i, this.rowHeight);
/*  979 */     invalidate();
/*      */   }
/*      */ 
/*      */   public int getRowHeight()
/*      */   {
/*  990 */     return this.rowHeight;
/*      */   }
/*      */ 
/*      */   public boolean isFixedRowHeight()
/*      */   {
/* 1000 */     return this.rowHeight > 0;
/*      */   }
/*      */ 
/*      */   public void setLargeModel(boolean paramBoolean)
/*      */   {
/* 1018 */     boolean bool = this.largeModel;
/*      */ 
/* 1020 */     this.largeModel = paramBoolean;
/* 1021 */     firePropertyChange("largeModel", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean isLargeModel()
/*      */   {
/* 1031 */     return this.largeModel;
/*      */   }
/*      */ 
/*      */   public void setInvokesStopCellEditing(boolean paramBoolean)
/*      */   {
/* 1052 */     boolean bool = this.invokesStopCellEditing;
/*      */ 
/* 1054 */     this.invokesStopCellEditing = paramBoolean;
/* 1055 */     firePropertyChange("invokesStopCellEditing", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getInvokesStopCellEditing()
/*      */   {
/* 1068 */     return this.invokesStopCellEditing;
/*      */   }
/*      */ 
/*      */   public void setScrollsOnExpand(boolean paramBoolean)
/*      */   {
/* 1093 */     boolean bool = this.scrollsOnExpand;
/*      */ 
/* 1095 */     this.scrollsOnExpand = paramBoolean;
/* 1096 */     this.scrollsOnExpandSet = true;
/* 1097 */     firePropertyChange("scrollsOnExpand", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getScrollsOnExpand()
/*      */   {
/* 1107 */     return this.scrollsOnExpand;
/*      */   }
/*      */ 
/*      */   public void setToggleClickCount(int paramInt)
/*      */   {
/* 1122 */     int i = this.toggleClickCount;
/*      */ 
/* 1124 */     this.toggleClickCount = paramInt;
/* 1125 */     firePropertyChange("toggleClickCount", i, paramInt);
/*      */   }
/*      */ 
/*      */   public int getToggleClickCount()
/*      */   {
/* 1136 */     return this.toggleClickCount;
/*      */   }
/*      */ 
/*      */   public void setExpandsSelectedPaths(boolean paramBoolean)
/*      */   {
/* 1162 */     boolean bool = this.expandsSelectedPaths;
/*      */ 
/* 1164 */     this.expandsSelectedPaths = paramBoolean;
/* 1165 */     firePropertyChange("expandsSelectedPaths", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getExpandsSelectedPaths()
/*      */   {
/* 1177 */     return this.expandsSelectedPaths;
/*      */   }
/*      */ 
/*      */   public void setDragEnabled(boolean paramBoolean)
/*      */   {
/* 1214 */     if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
/* 1215 */       throw new HeadlessException();
/*      */     }
/* 1217 */     this.dragEnabled = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getDragEnabled()
/*      */   {
/* 1228 */     return this.dragEnabled;
/*      */   }
/*      */ 
/*      */   public final void setDropMode(DropMode paramDropMode)
/*      */   {
/* 1260 */     if (paramDropMode != null) {
/* 1261 */       switch (1.$SwitchMap$javax$swing$DropMode[paramDropMode.ordinal()]) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/* 1266 */         this.dropMode = paramDropMode;
/* 1267 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1271 */     throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for tree");
/*      */   }
/*      */ 
/*      */   public final DropMode getDropMode()
/*      */   {
/* 1282 */     return this.dropMode;
/*      */   }
/*      */ 
/*      */   DropLocation dropLocationForPoint(Point paramPoint)
/*      */   {
/* 1293 */     DropLocation localDropLocation = null;
/*      */ 
/* 1295 */     int i = getClosestRowForLocation(paramPoint.x, paramPoint.y);
/* 1296 */     Rectangle localRectangle = getRowBounds(i);
/* 1297 */     TreeModel localTreeModel = getModel();
/* 1298 */     Object localObject = localTreeModel == null ? null : localTreeModel.getRoot();
/* 1299 */     TreePath localTreePath1 = localObject == null ? null : new TreePath(localObject);
/*      */ 
/* 1303 */     int j = (i == -1) || (paramPoint.y < localRectangle.y) || (paramPoint.y >= localRectangle.y + localRectangle.height) ? 1 : 0;
/*      */ 
/* 1307 */     switch (1.$SwitchMap$javax$swing$DropMode[this.dropMode.ordinal()]) {
/*      */     case 1:
/*      */     case 2:
/* 1310 */       if (j != 0)
/* 1311 */         localDropLocation = new DropLocation(paramPoint, null, -1, null);
/*      */       else {
/* 1313 */         localDropLocation = new DropLocation(paramPoint, getPathForRow(i), -1, null);
/*      */       }
/*      */ 
/* 1316 */       break;
/*      */     case 3:
/*      */     case 4:
/* 1319 */       if (i == -1) {
/* 1320 */         if ((localObject != null) && (!localTreeModel.isLeaf(localObject)) && (isExpanded(localTreePath1)))
/* 1321 */           localDropLocation = new DropLocation(paramPoint, localTreePath1, 0, null);
/*      */         else {
/* 1323 */           localDropLocation = new DropLocation(paramPoint, null, -1, null);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1329 */         boolean bool = (this.dropMode == DropMode.ON_OR_INSERT) || (!localTreeModel.isLeaf(getPathForRow(i).getLastPathComponent()));
/*      */ 
/* 1332 */         SwingUtilities2.Section localSection = SwingUtilities2.liesInVertical(localRectangle, paramPoint, bool);
/*      */         TreePath localTreePath2;
/*      */         TreePath localTreePath3;
/* 1333 */         if (localSection == SwingUtilities2.Section.LEADING) {
/* 1334 */           localTreePath2 = getPathForRow(i);
/* 1335 */           localTreePath3 = localTreePath2.getParentPath();
/* 1336 */         } else if (localSection == SwingUtilities2.Section.TRAILING) {
/* 1337 */           int k = i + 1;
/* 1338 */           if (k >= getRowCount()) {
/* 1339 */             if ((localTreeModel.isLeaf(localObject)) || (!isExpanded(localTreePath1))) {
/* 1340 */               localDropLocation = new DropLocation(paramPoint, null, -1, null); break;
/*      */             }
/* 1342 */             localTreePath3 = localTreePath1;
/* 1343 */             k = localTreeModel.getChildCount(localObject);
/* 1344 */             localDropLocation = new DropLocation(paramPoint, localTreePath3, k, null);
/*      */ 
/* 1347 */             break;
/*      */           }
/*      */ 
/* 1350 */           localTreePath2 = getPathForRow(k);
/* 1351 */           localTreePath3 = localTreePath2.getParentPath();
/*      */         } else {
/* 1353 */           assert (bool);
/* 1354 */           localDropLocation = new DropLocation(paramPoint, getPathForRow(i), -1, null);
/* 1355 */           break;
/*      */         }
/*      */ 
/* 1358 */         if (localTreePath3 != null) {
/* 1359 */           localDropLocation = new DropLocation(paramPoint, localTreePath3, localTreeModel.getIndexOfChild(localTreePath3.getLastPathComponent(), localTreePath2.getLastPathComponent()), null);
/*      */         }
/* 1362 */         else if ((bool) || (!localTreeModel.isLeaf(localObject)))
/* 1363 */           localDropLocation = new DropLocation(paramPoint, localTreePath1, -1, null);
/*      */         else {
/* 1365 */           localDropLocation = new DropLocation(paramPoint, null, -1, null);
/*      */         }
/*      */       }
/* 1368 */       break;
/*      */     default:
/* 1370 */       if (!$assertionsDisabled) throw new AssertionError("Unexpected drop mode");
/*      */       break;
/*      */     }
/* 1373 */     if ((j != 0) || (i != this.expandRow)) {
/* 1374 */       cancelDropTimer();
/*      */     }
/*      */ 
/* 1377 */     if ((j == 0) && (i != this.expandRow) && 
/* 1378 */       (isCollapsed(i))) {
/* 1379 */       this.expandRow = i;
/* 1380 */       startDropTimer();
/*      */     }
/*      */ 
/* 1384 */     return localDropLocation;
/*      */   }
/*      */ 
/*      */   Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean)
/*      */   {
/* 1424 */     Object localObject1 = null;
/* 1425 */     DropLocation localDropLocation = (DropLocation)paramDropLocation;
/*      */ 
/* 1427 */     if (this.dropMode == DropMode.USE_SELECTION) {
/* 1428 */       if (localDropLocation == null) {
/* 1429 */         if ((!paramBoolean) && (paramObject != null)) {
/* 1430 */           setSelectionPaths(((TreePath[][])(TreePath[][])paramObject)[0]);
/* 1431 */           setAnchorSelectionPath(((TreePath[][])(TreePath[][])paramObject)[1][0]);
/* 1432 */           setLeadSelectionPath(((TreePath[][])(TreePath[][])paramObject)[1][1]);
/*      */         }
/*      */       } else {
/* 1435 */         if (this.dropLocation == null) {
/* 1436 */           localObject2 = getSelectionPaths();
/* 1437 */           if (localObject2 == null) {
/* 1438 */             localObject2 = new TreePath[0];
/*      */           }
/*      */ 
/* 1441 */           localObject1 = new TreePath[][] { localObject2, { getAnchorSelectionPath(), getLeadSelectionPath() } };
/*      */         }
/*      */         else {
/* 1444 */           localObject1 = paramObject;
/*      */         }
/*      */ 
/* 1447 */         setSelectionPath(localDropLocation.getPath());
/*      */       }
/*      */     }
/*      */ 
/* 1451 */     Object localObject2 = this.dropLocation;
/* 1452 */     this.dropLocation = localDropLocation;
/* 1453 */     firePropertyChange("dropLocation", localObject2, this.dropLocation);
/*      */ 
/* 1455 */     return localObject1;
/*      */   }
/*      */ 
/*      */   void dndDone()
/*      */   {
/* 1463 */     cancelDropTimer();
/* 1464 */     this.dropTimer = null;
/*      */   }
/*      */ 
/*      */   public final DropLocation getDropLocation()
/*      */   {
/* 1486 */     return this.dropLocation;
/*      */   }
/*      */ 
/*      */   private void startDropTimer() {
/* 1490 */     if (this.dropTimer == null) {
/* 1491 */       this.dropTimer = new TreeTimer();
/*      */     }
/* 1493 */     this.dropTimer.start();
/*      */   }
/*      */ 
/*      */   private void cancelDropTimer() {
/* 1497 */     if ((this.dropTimer != null) && (this.dropTimer.isRunning())) {
/* 1498 */       this.expandRow = -1;
/* 1499 */       this.dropTimer.stop();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isPathEditable(TreePath paramTreePath)
/*      */   {
/* 1513 */     return isEditable();
/*      */   }
/*      */ 
/*      */   public String getToolTipText(MouseEvent paramMouseEvent)
/*      */   {
/* 1533 */     String str = null;
/*      */ 
/* 1535 */     if (paramMouseEvent != null) {
/* 1536 */       Point localPoint = paramMouseEvent.getPoint();
/* 1537 */       int i = getRowForLocation(localPoint.x, localPoint.y);
/* 1538 */       TreeCellRenderer localTreeCellRenderer = getCellRenderer();
/*      */ 
/* 1540 */       if ((i != -1) && (localTreeCellRenderer != null)) {
/* 1541 */         TreePath localTreePath = getPathForRow(i);
/* 1542 */         Object localObject = localTreePath.getLastPathComponent();
/* 1543 */         Component localComponent = localTreeCellRenderer.getTreeCellRendererComponent(this, localObject, isRowSelected(i), isExpanded(i), getModel().isLeaf(localObject), i, true);
/*      */ 
/* 1548 */         if ((localComponent instanceof JComponent))
/*      */         {
/* 1550 */           Rectangle localRectangle = getPathBounds(localTreePath);
/*      */ 
/* 1552 */           localPoint.translate(-localRectangle.x, -localRectangle.y);
/* 1553 */           MouseEvent localMouseEvent = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
/*      */ 
/* 1563 */           str = ((JComponent)localComponent).getToolTipText(localMouseEvent);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1568 */     if (str == null) {
/* 1569 */       str = getToolTipText();
/*      */     }
/* 1571 */     return str;
/*      */   }
/*      */ 
/*      */   public String convertValueToText(Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
/*      */   {
/* 1592 */     if (paramObject != null) {
/* 1593 */       String str = paramObject.toString();
/* 1594 */       if (str != null) {
/* 1595 */         return str;
/*      */       }
/*      */     }
/* 1598 */     return "";
/*      */   }
/*      */ 
/*      */   public int getRowCount()
/*      */   {
/* 1615 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 1617 */     if (localTreeUI != null)
/* 1618 */       return localTreeUI.getRowCount(this);
/* 1619 */     return 0;
/*      */   }
/*      */ 
/*      */   public void setSelectionPath(TreePath paramTreePath)
/*      */   {
/* 1631 */     getSelectionModel().setSelectionPath(paramTreePath);
/*      */   }
/*      */ 
/*      */   public void setSelectionPaths(TreePath[] paramArrayOfTreePath)
/*      */   {
/* 1644 */     getSelectionModel().setSelectionPaths(paramArrayOfTreePath);
/*      */   }
/*      */ 
/*      */   public void setLeadSelectionPath(TreePath paramTreePath)
/*      */   {
/* 1661 */     TreePath localTreePath = this.leadPath;
/*      */ 
/* 1663 */     this.leadPath = paramTreePath;
/* 1664 */     firePropertyChange("leadSelectionPath", localTreePath, paramTreePath);
/*      */   }
/*      */ 
/*      */   public void setAnchorSelectionPath(TreePath paramTreePath)
/*      */   {
/* 1681 */     TreePath localTreePath = this.anchorPath;
/*      */ 
/* 1683 */     this.anchorPath = paramTreePath;
/* 1684 */     firePropertyChange("anchorSelectionPath", localTreePath, paramTreePath);
/*      */   }
/*      */ 
/*      */   public void setSelectionRow(int paramInt)
/*      */   {
/* 1694 */     int[] arrayOfInt = { paramInt };
/*      */ 
/* 1696 */     setSelectionRows(arrayOfInt);
/*      */   }
/*      */ 
/*      */   public void setSelectionRows(int[] paramArrayOfInt)
/*      */   {
/* 1712 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 1714 */     if ((localTreeUI != null) && (paramArrayOfInt != null)) {
/* 1715 */       int i = paramArrayOfInt.length;
/* 1716 */       TreePath[] arrayOfTreePath = new TreePath[i];
/*      */ 
/* 1718 */       for (int j = 0; j < i; j++) {
/* 1719 */         arrayOfTreePath[j] = localTreeUI.getPathForRow(this, paramArrayOfInt[j]);
/*      */       }
/* 1721 */       setSelectionPaths(arrayOfTreePath);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addSelectionPath(TreePath paramTreePath)
/*      */   {
/* 1738 */     getSelectionModel().addSelectionPath(paramTreePath);
/*      */   }
/*      */ 
/*      */   public void addSelectionPaths(TreePath[] paramArrayOfTreePath)
/*      */   {
/* 1755 */     getSelectionModel().addSelectionPaths(paramArrayOfTreePath);
/*      */   }
/*      */ 
/*      */   public void addSelectionRow(int paramInt)
/*      */   {
/* 1765 */     int[] arrayOfInt = { paramInt };
/*      */ 
/* 1767 */     addSelectionRows(arrayOfInt);
/*      */   }
/*      */ 
/*      */   public void addSelectionRows(int[] paramArrayOfInt)
/*      */   {
/* 1777 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 1779 */     if ((localTreeUI != null) && (paramArrayOfInt != null)) {
/* 1780 */       int i = paramArrayOfInt.length;
/* 1781 */       TreePath[] arrayOfTreePath = new TreePath[i];
/*      */ 
/* 1783 */       for (int j = 0; j < i; j++)
/* 1784 */         arrayOfTreePath[j] = localTreeUI.getPathForRow(this, paramArrayOfInt[j]);
/* 1785 */       addSelectionPaths(arrayOfTreePath);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getLastSelectedPathComponent()
/*      */   {
/* 1800 */     TreePath localTreePath = getSelectionModel().getSelectionPath();
/*      */ 
/* 1802 */     if (localTreePath != null)
/* 1803 */       return localTreePath.getLastPathComponent();
/* 1804 */     return null;
/*      */   }
/*      */ 
/*      */   public TreePath getLeadSelectionPath()
/*      */   {
/* 1812 */     return this.leadPath;
/*      */   }
/*      */ 
/*      */   public TreePath getAnchorSelectionPath()
/*      */   {
/* 1821 */     return this.anchorPath;
/*      */   }
/*      */ 
/*      */   public TreePath getSelectionPath()
/*      */   {
/* 1831 */     return getSelectionModel().getSelectionPath();
/*      */   }
/*      */ 
/*      */   public TreePath[] getSelectionPaths()
/*      */   {
/* 1841 */     TreePath[] arrayOfTreePath = getSelectionModel().getSelectionPaths();
/*      */ 
/* 1843 */     return (arrayOfTreePath != null) && (arrayOfTreePath.length > 0) ? arrayOfTreePath : null;
/*      */   }
/*      */ 
/*      */   public int[] getSelectionRows()
/*      */   {
/* 1857 */     return getSelectionModel().getSelectionRows();
/*      */   }
/*      */ 
/*      */   public int getSelectionCount()
/*      */   {
/* 1866 */     return this.selectionModel.getSelectionCount();
/*      */   }
/*      */ 
/*      */   public int getMinSelectionRow()
/*      */   {
/* 1876 */     return getSelectionModel().getMinSelectionRow();
/*      */   }
/*      */ 
/*      */   public int getMaxSelectionRow()
/*      */   {
/* 1886 */     return getSelectionModel().getMaxSelectionRow();
/*      */   }
/*      */ 
/*      */   public int getLeadSelectionRow()
/*      */   {
/* 1897 */     TreePath localTreePath = getLeadSelectionPath();
/*      */ 
/* 1899 */     if (localTreePath != null) {
/* 1900 */       return getRowForPath(localTreePath);
/*      */     }
/* 1902 */     return -1;
/*      */   }
/*      */ 
/*      */   public boolean isPathSelected(TreePath paramTreePath)
/*      */   {
/* 1912 */     return getSelectionModel().isPathSelected(paramTreePath);
/*      */   }
/*      */ 
/*      */   public boolean isRowSelected(int paramInt)
/*      */   {
/* 1923 */     return getSelectionModel().isRowSelected(paramInt);
/*      */   }
/*      */ 
/*      */   public Enumeration<TreePath> getExpandedDescendants(TreePath paramTreePath)
/*      */   {
/* 1942 */     if (!isExpanded(paramTreePath)) {
/* 1943 */       return null;
/*      */     }
/* 1945 */     Enumeration localEnumeration = this.expandedState.keys();
/* 1946 */     Vector localVector = null;
/*      */ 
/* 1950 */     if (localEnumeration != null) {
/* 1951 */       while (localEnumeration.hasMoreElements()) {
/* 1952 */         TreePath localTreePath = (TreePath)localEnumeration.nextElement();
/* 1953 */         Object localObject = this.expandedState.get(localTreePath);
/*      */ 
/* 1957 */         if ((localTreePath != paramTreePath) && (localObject != null) && (((Boolean)localObject).booleanValue()) && (paramTreePath.isDescendant(localTreePath)) && (isVisible(localTreePath)))
/*      */         {
/* 1960 */           if (localVector == null) {
/* 1961 */             localVector = new Vector();
/*      */           }
/* 1963 */           localVector.addElement(localTreePath);
/*      */         }
/*      */       }
/*      */     }
/* 1967 */     if (localVector == null) {
/* 1968 */       Set localSet = Collections.emptySet();
/* 1969 */       return Collections.enumeration(localSet);
/*      */     }
/* 1971 */     return localVector.elements();
/*      */   }
/*      */ 
/*      */   public boolean hasBeenExpanded(TreePath paramTreePath)
/*      */   {
/* 1980 */     return (paramTreePath != null) && (this.expandedState.get(paramTreePath) != null);
/*      */   }
/*      */ 
/*      */   public boolean isExpanded(TreePath paramTreePath)
/*      */   {
/* 1992 */     if (paramTreePath == null) {
/* 1993 */       return false;
/*      */     }
/*      */     do
/*      */     {
/* 1997 */       Object localObject = this.expandedState.get(paramTreePath);
/* 1998 */       if ((localObject == null) || (!((Boolean)localObject).booleanValue()))
/* 1999 */         return false; 
/*      */     }
/* 2000 */     while ((paramTreePath = paramTreePath.getParentPath()) != null);
/*      */ 
/* 2002 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isExpanded(int paramInt)
/*      */   {
/* 2014 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2016 */     if (localTreeUI != null) {
/* 2017 */       TreePath localTreePath = localTreeUI.getPathForRow(this, paramInt);
/*      */ 
/* 2019 */       if (localTreePath != null) {
/* 2020 */         Boolean localBoolean = (Boolean)this.expandedState.get(localTreePath);
/*      */ 
/* 2022 */         return (localBoolean != null) && (localBoolean.booleanValue());
/*      */       }
/*      */     }
/* 2025 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isCollapsed(TreePath paramTreePath)
/*      */   {
/* 2038 */     return !isExpanded(paramTreePath);
/*      */   }
/*      */ 
/*      */   public boolean isCollapsed(int paramInt)
/*      */   {
/* 2049 */     return !isExpanded(paramInt);
/*      */   }
/*      */ 
/*      */   public void makeVisible(TreePath paramTreePath)
/*      */   {
/* 2058 */     if (paramTreePath != null) {
/* 2059 */       TreePath localTreePath = paramTreePath.getParentPath();
/*      */ 
/* 2061 */       if (localTreePath != null)
/* 2062 */         expandPath(localTreePath);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isVisible(TreePath paramTreePath)
/*      */   {
/* 2075 */     if (paramTreePath != null) {
/* 2076 */       TreePath localTreePath = paramTreePath.getParentPath();
/*      */ 
/* 2078 */       if (localTreePath != null) {
/* 2079 */         return isExpanded(localTreePath);
/*      */       }
/* 2081 */       return true;
/*      */     }
/* 2083 */     return false;
/*      */   }
/*      */ 
/*      */   public Rectangle getPathBounds(TreePath paramTreePath)
/*      */   {
/* 2100 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2102 */     if (localTreeUI != null)
/* 2103 */       return localTreeUI.getPathBounds(this, paramTreePath);
/* 2104 */     return null;
/*      */   }
/*      */ 
/*      */   public Rectangle getRowBounds(int paramInt)
/*      */   {
/* 2116 */     return getPathBounds(getPathForRow(paramInt));
/*      */   }
/*      */ 
/*      */   public void scrollPathToVisible(TreePath paramTreePath)
/*      */   {
/* 2129 */     if (paramTreePath != null) {
/* 2130 */       makeVisible(paramTreePath);
/*      */ 
/* 2132 */       Rectangle localRectangle = getPathBounds(paramTreePath);
/*      */ 
/* 2134 */       if (localRectangle != null) {
/* 2135 */         scrollRectToVisible(localRectangle);
/* 2136 */         if (this.accessibleContext != null)
/* 2137 */           ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void scrollRowToVisible(int paramInt)
/*      */   {
/* 2153 */     scrollPathToVisible(getPathForRow(paramInt));
/*      */   }
/*      */ 
/*      */   public TreePath getPathForRow(int paramInt)
/*      */   {
/* 2167 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2169 */     if (localTreeUI != null)
/* 2170 */       return localTreeUI.getPathForRow(this, paramInt);
/* 2171 */     return null;
/*      */   }
/*      */ 
/*      */   public int getRowForPath(TreePath paramTreePath)
/*      */   {
/* 2184 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2186 */     if (localTreeUI != null)
/* 2187 */       return localTreeUI.getRowForPath(this, paramTreePath);
/* 2188 */     return -1;
/*      */   }
/*      */ 
/*      */   public void expandPath(TreePath paramTreePath)
/*      */   {
/* 2200 */     TreeModel localTreeModel = getModel();
/*      */ 
/* 2202 */     if ((paramTreePath != null) && (localTreeModel != null) && (!localTreeModel.isLeaf(paramTreePath.getLastPathComponent())))
/*      */     {
/* 2204 */       setExpandedState(paramTreePath, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void expandRow(int paramInt)
/*      */   {
/* 2219 */     expandPath(getPathForRow(paramInt));
/*      */   }
/*      */ 
/*      */   public void collapsePath(TreePath paramTreePath)
/*      */   {
/* 2229 */     setExpandedState(paramTreePath, false);
/*      */   }
/*      */ 
/*      */   public void collapseRow(int paramInt)
/*      */   {
/* 2242 */     collapsePath(getPathForRow(paramInt));
/*      */   }
/*      */ 
/*      */   public TreePath getPathForLocation(int paramInt1, int paramInt2)
/*      */   {
/* 2255 */     TreePath localTreePath = getClosestPathForLocation(paramInt1, paramInt2);
/*      */ 
/* 2257 */     if (localTreePath != null) {
/* 2258 */       Rectangle localRectangle = getPathBounds(localTreePath);
/*      */ 
/* 2260 */       if ((localRectangle != null) && (paramInt1 >= localRectangle.x) && (paramInt1 < localRectangle.x + localRectangle.width) && (paramInt2 >= localRectangle.y) && (paramInt2 < localRectangle.y + localRectangle.height))
/*      */       {
/* 2263 */         return localTreePath;
/*      */       }
/*      */     }
/* 2265 */     return null;
/*      */   }
/*      */ 
/*      */   public int getRowForLocation(int paramInt1, int paramInt2)
/*      */   {
/* 2280 */     return getRowForPath(getPathForLocation(paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */   public TreePath getClosestPathForLocation(int paramInt1, int paramInt2)
/*      */   {
/* 2301 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2303 */     if (localTreeUI != null)
/* 2304 */       return localTreeUI.getClosestPathForLocation(this, paramInt1, paramInt2);
/* 2305 */     return null;
/*      */   }
/*      */ 
/*      */   public int getClosestRowForLocation(int paramInt1, int paramInt2)
/*      */   {
/* 2326 */     return getRowForPath(getClosestPathForLocation(paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */   public boolean isEditing()
/*      */   {
/* 2337 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2339 */     if (localTreeUI != null)
/* 2340 */       return localTreeUI.isEditing(this);
/* 2341 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean stopEditing()
/*      */   {
/* 2360 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2362 */     if (localTreeUI != null)
/* 2363 */       return localTreeUI.stopEditing(this);
/* 2364 */     return false;
/*      */   }
/*      */ 
/*      */   public void cancelEditing()
/*      */   {
/* 2372 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2374 */     if (localTreeUI != null)
/* 2375 */       localTreeUI.cancelEditing(this);
/*      */   }
/*      */ 
/*      */   public void startEditingAtPath(TreePath paramTreePath)
/*      */   {
/* 2387 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2389 */     if (localTreeUI != null)
/* 2390 */       localTreeUI.startEditingAtPath(this, paramTreePath);
/*      */   }
/*      */ 
/*      */   public TreePath getEditingPath()
/*      */   {
/* 2399 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2401 */     if (localTreeUI != null)
/* 2402 */       return localTreeUI.getEditingPath(this);
/* 2403 */     return null;
/*      */   }
/*      */ 
/*      */   public void setSelectionModel(TreeSelectionModel paramTreeSelectionModel)
/*      */   {
/* 2430 */     if (paramTreeSelectionModel == null) {
/* 2431 */       paramTreeSelectionModel = EmptySelectionModel.sharedInstance();
/*      */     }
/* 2433 */     TreeSelectionModel localTreeSelectionModel = this.selectionModel;
/*      */ 
/* 2435 */     if ((this.selectionModel != null) && (this.selectionRedirector != null)) {
/* 2436 */       this.selectionModel.removeTreeSelectionListener(this.selectionRedirector);
/*      */     }
/*      */ 
/* 2439 */     if (this.accessibleContext != null) {
/* 2440 */       this.selectionModel.removeTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
/* 2441 */       paramTreeSelectionModel.addTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
/*      */     }
/*      */ 
/* 2444 */     this.selectionModel = paramTreeSelectionModel;
/* 2445 */     if (this.selectionRedirector != null) {
/* 2446 */       this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
/*      */     }
/* 2448 */     firePropertyChange("selectionModel", localTreeSelectionModel, this.selectionModel);
/*      */ 
/* 2451 */     if (this.accessibleContext != null)
/* 2452 */       this.accessibleContext.firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
/*      */   }
/*      */ 
/*      */   public TreeSelectionModel getSelectionModel()
/*      */   {
/* 2468 */     return this.selectionModel;
/*      */   }
/*      */ 
/*      */   protected TreePath[] getPathBetweenRows(int paramInt1, int paramInt2)
/*      */   {
/* 2498 */     TreeUI localTreeUI = getUI();
/* 2499 */     if (localTreeUI != null) {
/* 2500 */       int i = getRowCount();
/* 2501 */       if ((i > 0) && ((paramInt1 >= 0) || (paramInt2 >= 0)) && ((paramInt1 < i) || (paramInt2 < i)))
/*      */       {
/* 2503 */         paramInt1 = Math.min(i - 1, Math.max(paramInt1, 0));
/* 2504 */         paramInt2 = Math.min(i - 1, Math.max(paramInt2, 0));
/* 2505 */         int j = Math.min(paramInt1, paramInt2);
/* 2506 */         int k = Math.max(paramInt1, paramInt2);
/* 2507 */         TreePath[] arrayOfTreePath = new TreePath[k - j + 1];
/*      */ 
/* 2509 */         for (int m = j; m <= k; m++) {
/* 2510 */           arrayOfTreePath[(m - j)] = localTreeUI.getPathForRow(this, m);
/*      */         }
/*      */ 
/* 2513 */         return arrayOfTreePath;
/*      */       }
/*      */     }
/* 2516 */     return new TreePath[0];
/*      */   }
/*      */ 
/*      */   public void setSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2540 */     TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
/*      */ 
/* 2542 */     getSelectionModel().setSelectionPaths(arrayOfTreePath);
/*      */   }
/*      */ 
/*      */   public void addSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2567 */     TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
/*      */ 
/* 2569 */     if ((arrayOfTreePath != null) && (arrayOfTreePath.length > 0))
/* 2570 */       getSelectionModel().addSelectionPaths(arrayOfTreePath);
/*      */   }
/*      */ 
/*      */   public void removeSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2595 */     TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
/*      */ 
/* 2597 */     if ((arrayOfTreePath != null) && (arrayOfTreePath.length > 0))
/* 2598 */       getSelectionModel().removeSelectionPaths(arrayOfTreePath);
/*      */   }
/*      */ 
/*      */   public void removeSelectionPath(TreePath paramTreePath)
/*      */   {
/* 2609 */     getSelectionModel().removeSelectionPath(paramTreePath);
/*      */   }
/*      */ 
/*      */   public void removeSelectionPaths(TreePath[] paramArrayOfTreePath)
/*      */   {
/* 2620 */     getSelectionModel().removeSelectionPaths(paramArrayOfTreePath);
/*      */   }
/*      */ 
/*      */   public void removeSelectionRow(int paramInt)
/*      */   {
/* 2630 */     int[] arrayOfInt = { paramInt };
/*      */ 
/* 2632 */     removeSelectionRows(arrayOfInt);
/*      */   }
/*      */ 
/*      */   public void removeSelectionRows(int[] paramArrayOfInt)
/*      */   {
/* 2643 */     TreeUI localTreeUI = getUI();
/*      */ 
/* 2645 */     if ((localTreeUI != null) && (paramArrayOfInt != null)) {
/* 2646 */       int i = paramArrayOfInt.length;
/* 2647 */       TreePath[] arrayOfTreePath = new TreePath[i];
/*      */ 
/* 2649 */       for (int j = 0; j < i; j++)
/* 2650 */         arrayOfTreePath[j] = localTreeUI.getPathForRow(this, paramArrayOfInt[j]);
/* 2651 */       removeSelectionPaths(arrayOfTreePath);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearSelection()
/*      */   {
/* 2659 */     getSelectionModel().clearSelection();
/*      */   }
/*      */ 
/*      */   public boolean isSelectionEmpty()
/*      */   {
/* 2668 */     return getSelectionModel().isSelectionEmpty();
/*      */   }
/*      */ 
/*      */   public void addTreeExpansionListener(TreeExpansionListener paramTreeExpansionListener)
/*      */   {
/* 2679 */     if (this.settingUI) {
/* 2680 */       this.uiTreeExpansionListener = paramTreeExpansionListener;
/*      */     }
/* 2682 */     this.listenerList.add(TreeExpansionListener.class, paramTreeExpansionListener);
/*      */   }
/*      */ 
/*      */   public void removeTreeExpansionListener(TreeExpansionListener paramTreeExpansionListener)
/*      */   {
/* 2691 */     this.listenerList.remove(TreeExpansionListener.class, paramTreeExpansionListener);
/* 2692 */     if (this.uiTreeExpansionListener == paramTreeExpansionListener)
/* 2693 */       this.uiTreeExpansionListener = null;
/*      */   }
/*      */ 
/*      */   public TreeExpansionListener[] getTreeExpansionListeners()
/*      */   {
/* 2706 */     return (TreeExpansionListener[])this.listenerList.getListeners(TreeExpansionListener.class);
/*      */   }
/*      */ 
/*      */   public void addTreeWillExpandListener(TreeWillExpandListener paramTreeWillExpandListener)
/*      */   {
/* 2717 */     this.listenerList.add(TreeWillExpandListener.class, paramTreeWillExpandListener);
/*      */   }
/*      */ 
/*      */   public void removeTreeWillExpandListener(TreeWillExpandListener paramTreeWillExpandListener)
/*      */   {
/* 2726 */     this.listenerList.remove(TreeWillExpandListener.class, paramTreeWillExpandListener);
/*      */   }
/*      */ 
/*      */   public TreeWillExpandListener[] getTreeWillExpandListeners()
/*      */   {
/* 2738 */     return (TreeWillExpandListener[])this.listenerList.getListeners(TreeWillExpandListener.class);
/*      */   }
/*      */ 
/*      */   public void fireTreeExpanded(TreePath paramTreePath)
/*      */   {
/* 2752 */     Object[] arrayOfObject = this.listenerList.getListenerList();
/* 2753 */     TreeExpansionEvent localTreeExpansionEvent = null;
/* 2754 */     if (this.uiTreeExpansionListener != null) {
/* 2755 */       localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
/* 2756 */       this.uiTreeExpansionListener.treeExpanded(localTreeExpansionEvent);
/*      */     }
/*      */ 
/* 2760 */     for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
/* 2761 */       if ((arrayOfObject[i] == TreeExpansionListener.class) && (arrayOfObject[(i + 1)] != this.uiTreeExpansionListener))
/*      */       {
/* 2764 */         if (localTreeExpansionEvent == null)
/* 2765 */           localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
/* 2766 */         ((TreeExpansionListener)arrayOfObject[(i + 1)]).treeExpanded(localTreeExpansionEvent);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void fireTreeCollapsed(TreePath paramTreePath)
/*      */   {
/* 2783 */     Object[] arrayOfObject = this.listenerList.getListenerList();
/* 2784 */     TreeExpansionEvent localTreeExpansionEvent = null;
/* 2785 */     if (this.uiTreeExpansionListener != null) {
/* 2786 */       localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
/* 2787 */       this.uiTreeExpansionListener.treeCollapsed(localTreeExpansionEvent);
/*      */     }
/*      */ 
/* 2791 */     for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
/* 2792 */       if ((arrayOfObject[i] == TreeExpansionListener.class) && (arrayOfObject[(i + 1)] != this.uiTreeExpansionListener))
/*      */       {
/* 2795 */         if (localTreeExpansionEvent == null)
/* 2796 */           localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
/* 2797 */         ((TreeExpansionListener)arrayOfObject[(i + 1)]).treeCollapsed(localTreeExpansionEvent);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void fireTreeWillExpand(TreePath paramTreePath)
/*      */     throws ExpandVetoException
/*      */   {
/* 2814 */     Object[] arrayOfObject = this.listenerList.getListenerList();
/* 2815 */     TreeExpansionEvent localTreeExpansionEvent = null;
/*      */ 
/* 2818 */     for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
/* 2819 */       if (arrayOfObject[i] == TreeWillExpandListener.class)
/*      */       {
/* 2821 */         if (localTreeExpansionEvent == null)
/* 2822 */           localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
/* 2823 */         ((TreeWillExpandListener)arrayOfObject[(i + 1)]).treeWillExpand(localTreeExpansionEvent);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void fireTreeWillCollapse(TreePath paramTreePath)
/*      */     throws ExpandVetoException
/*      */   {
/* 2840 */     Object[] arrayOfObject = this.listenerList.getListenerList();
/* 2841 */     TreeExpansionEvent localTreeExpansionEvent = null;
/*      */ 
/* 2844 */     for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
/* 2845 */       if (arrayOfObject[i] == TreeWillExpandListener.class)
/*      */       {
/* 2847 */         if (localTreeExpansionEvent == null)
/* 2848 */           localTreeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
/* 2849 */         ((TreeWillExpandListener)arrayOfObject[(i + 1)]).treeWillCollapse(localTreeExpansionEvent);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
/*      */   {
/* 2863 */     this.listenerList.add(TreeSelectionListener.class, paramTreeSelectionListener);
/* 2864 */     if ((this.listenerList.getListenerCount(TreeSelectionListener.class) != 0) && (this.selectionRedirector == null))
/*      */     {
/* 2866 */       this.selectionRedirector = new TreeSelectionRedirector();
/* 2867 */       this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
/*      */   {
/* 2877 */     this.listenerList.remove(TreeSelectionListener.class, paramTreeSelectionListener);
/* 2878 */     if ((this.listenerList.getListenerCount(TreeSelectionListener.class) == 0) && (this.selectionRedirector != null))
/*      */     {
/* 2880 */       this.selectionModel.removeTreeSelectionListener(this.selectionRedirector);
/*      */ 
/* 2882 */       this.selectionRedirector = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TreeSelectionListener[] getTreeSelectionListeners()
/*      */   {
/* 2895 */     return (TreeSelectionListener[])this.listenerList.getListeners(TreeSelectionListener.class);
/*      */   }
/*      */ 
/*      */   protected void fireValueChanged(TreeSelectionEvent paramTreeSelectionEvent)
/*      */   {
/* 2910 */     Object[] arrayOfObject = this.listenerList.getListenerList();
/*      */ 
/* 2913 */     for (int i = arrayOfObject.length - 2; i >= 0; i -= 2)
/*      */     {
/* 2915 */       if (arrayOfObject[i] == TreeSelectionListener.class)
/*      */       {
/* 2919 */         ((TreeSelectionListener)arrayOfObject[(i + 1)]).valueChanged(paramTreeSelectionEvent);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void treeDidChange()
/*      */   {
/* 2932 */     revalidate();
/* 2933 */     repaint();
/*      */   }
/*      */ 
/*      */   public void setVisibleRowCount(int paramInt)
/*      */   {
/* 2950 */     int i = this.visibleRowCount;
/*      */ 
/* 2952 */     this.visibleRowCount = paramInt;
/* 2953 */     firePropertyChange("visibleRowCount", i, this.visibleRowCount);
/*      */ 
/* 2955 */     invalidate();
/* 2956 */     if (this.accessibleContext != null)
/* 2957 */       ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
/*      */   }
/*      */ 
/*      */   public int getVisibleRowCount()
/*      */   {
/* 2967 */     return this.visibleRowCount;
/*      */   }
/*      */ 
/*      */   private void expandRoot()
/*      */   {
/* 2974 */     TreeModel localTreeModel = getModel();
/*      */ 
/* 2976 */     if ((localTreeModel != null) && (localTreeModel.getRoot() != null))
/* 2977 */       expandPath(new TreePath(localTreeModel.getRoot()));
/*      */   }
/*      */ 
/*      */   public TreePath getNextMatch(String paramString, int paramInt, Position.Bias paramBias)
/*      */   {
/* 3000 */     int i = getRowCount();
/* 3001 */     if (paramString == null) {
/* 3002 */       throw new IllegalArgumentException();
/*      */     }
/* 3004 */     if ((paramInt < 0) || (paramInt >= i)) {
/* 3005 */       throw new IllegalArgumentException();
/*      */     }
/* 3007 */     paramString = paramString.toUpperCase();
/*      */ 
/* 3011 */     int j = paramBias == Position.Bias.Forward ? 1 : -1;
/* 3012 */     int k = paramInt;
/*      */     do {
/* 3014 */       TreePath localTreePath = getPathForRow(k);
/* 3015 */       String str = convertValueToText(localTreePath.getLastPathComponent(), isRowSelected(k), isExpanded(k), true, k, false);
/*      */ 
/* 3019 */       if (str.toUpperCase().startsWith(paramString)) {
/* 3020 */         return localTreePath;
/*      */       }
/* 3022 */       k = (k + j + i) % i;
/* 3023 */     }while (k != paramInt);
/* 3024 */     return null;
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException
/*      */   {
/* 3029 */     Vector localVector = new Vector();
/*      */ 
/* 3031 */     paramObjectOutputStream.defaultWriteObject();
/*      */ 
/* 3033 */     if ((this.cellRenderer != null) && ((this.cellRenderer instanceof Serializable))) {
/* 3034 */       localVector.addElement("cellRenderer");
/* 3035 */       localVector.addElement(this.cellRenderer);
/*      */     }
/*      */ 
/* 3038 */     if ((this.cellEditor != null) && ((this.cellEditor instanceof Serializable))) {
/* 3039 */       localVector.addElement("cellEditor");
/* 3040 */       localVector.addElement(this.cellEditor);
/*      */     }
/*      */ 
/* 3043 */     if ((this.treeModel != null) && ((this.treeModel instanceof Serializable))) {
/* 3044 */       localVector.addElement("treeModel");
/* 3045 */       localVector.addElement(this.treeModel);
/*      */     }
/*      */ 
/* 3048 */     if ((this.selectionModel != null) && ((this.selectionModel instanceof Serializable))) {
/* 3049 */       localVector.addElement("selectionModel");
/* 3050 */       localVector.addElement(this.selectionModel);
/*      */     }
/*      */ 
/* 3053 */     Object localObject = getArchivableExpandedState();
/*      */ 
/* 3055 */     if (localObject != null) {
/* 3056 */       localVector.addElement("expandedState");
/* 3057 */       localVector.addElement(localObject);
/*      */     }
/*      */ 
/* 3060 */     paramObjectOutputStream.writeObject(localVector);
/* 3061 */     if (getUIClassID().equals("TreeUI")) {
/* 3062 */       byte b = JComponent.getWriteObjCounter(this);
/* 3063 */       b = (byte)(b - 1); JComponent.setWriteObjCounter(this, b);
/* 3064 */       if ((b == 0) && (this.ui != null))
/* 3065 */         this.ui.installUI(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 3072 */     paramObjectInputStream.defaultReadObject();
/*      */ 
/* 3076 */     this.expandedState = new Hashtable();
/*      */ 
/* 3078 */     this.expandedStack = new Stack();
/*      */ 
/* 3080 */     Vector localVector = (Vector)paramObjectInputStream.readObject();
/* 3081 */     int i = 0;
/* 3082 */     int j = localVector.size();
/*      */ 
/* 3084 */     if ((i < j) && (localVector.elementAt(i).equals("cellRenderer")))
/*      */     {
/* 3086 */       this.cellRenderer = ((TreeCellRenderer)localVector.elementAt(++i));
/* 3087 */       i++;
/*      */     }
/* 3089 */     if ((i < j) && (localVector.elementAt(i).equals("cellEditor")))
/*      */     {
/* 3091 */       this.cellEditor = ((TreeCellEditor)localVector.elementAt(++i));
/* 3092 */       i++;
/*      */     }
/* 3094 */     if ((i < j) && (localVector.elementAt(i).equals("treeModel")))
/*      */     {
/* 3096 */       this.treeModel = ((TreeModel)localVector.elementAt(++i));
/* 3097 */       i++;
/*      */     }
/* 3099 */     if ((i < j) && (localVector.elementAt(i).equals("selectionModel")))
/*      */     {
/* 3101 */       this.selectionModel = ((TreeSelectionModel)localVector.elementAt(++i));
/* 3102 */       i++;
/*      */     }
/* 3104 */     if ((i < j) && (localVector.elementAt(i).equals("expandedState")))
/*      */     {
/* 3106 */       unarchiveExpandedState(localVector.elementAt(++i));
/* 3107 */       i++;
/*      */     }
/*      */ 
/* 3110 */     if (this.listenerList.getListenerCount(TreeSelectionListener.class) != 0) {
/* 3111 */       this.selectionRedirector = new TreeSelectionRedirector();
/* 3112 */       this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
/*      */     }
/*      */ 
/* 3115 */     if (this.treeModel != null) {
/* 3116 */       this.treeModelListener = createTreeModelListener();
/* 3117 */       if (this.treeModelListener != null)
/* 3118 */         this.treeModel.addTreeModelListener(this.treeModelListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object getArchivableExpandedState()
/*      */   {
/* 3128 */     TreeModel localTreeModel = getModel();
/*      */ 
/* 3130 */     if (localTreeModel != null) {
/* 3131 */       Enumeration localEnumeration = this.expandedState.keys();
/*      */ 
/* 3133 */       if (localEnumeration != null) {
/* 3134 */         Vector localVector = new Vector();
/*      */ 
/* 3136 */         while (localEnumeration.hasMoreElements()) {
/* 3137 */           TreePath localTreePath = (TreePath)localEnumeration.nextElement();
/*      */           int[] arrayOfInt;
/*      */           try {
/* 3141 */             arrayOfInt = getModelIndexsForPath(localTreePath);
/*      */           } catch (Error localError) {
/* 3143 */             arrayOfInt = null;
/*      */           }
/* 3145 */           if (arrayOfInt != null) {
/* 3146 */             localVector.addElement(arrayOfInt);
/* 3147 */             localVector.addElement(this.expandedState.get(localTreePath));
/*      */           }
/*      */         }
/* 3150 */         return localVector;
/*      */       }
/*      */     }
/* 3153 */     return null;
/*      */   }
/*      */ 
/*      */   private void unarchiveExpandedState(Object paramObject)
/*      */   {
/* 3161 */     if ((paramObject instanceof Vector)) {
/* 3162 */       Vector localVector = (Vector)paramObject;
/*      */ 
/* 3164 */       for (int i = localVector.size() - 1; i >= 0; i--) {
/* 3165 */         Boolean localBoolean = (Boolean)localVector.elementAt(i--);
/*      */         try
/*      */         {
/* 3169 */           TreePath localTreePath = getPathForIndexs((int[])localVector.elementAt(i));
/* 3170 */           if (localTreePath != null)
/* 3171 */             this.expandedState.put(localTreePath, localBoolean);
/*      */         }
/*      */         catch (Error localError)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int[] getModelIndexsForPath(TreePath paramTreePath)
/*      */   {
/* 3184 */     if (paramTreePath != null) {
/* 3185 */       TreeModel localTreeModel = getModel();
/* 3186 */       int i = paramTreePath.getPathCount();
/* 3187 */       int[] arrayOfInt = new int[i - 1];
/* 3188 */       Object localObject = localTreeModel.getRoot();
/*      */ 
/* 3190 */       for (int j = 1; j < i; j++) {
/* 3191 */         arrayOfInt[(j - 1)] = localTreeModel.getIndexOfChild(localObject, paramTreePath.getPathComponent(j));
/*      */ 
/* 3193 */         localObject = paramTreePath.getPathComponent(j);
/* 3194 */         if (arrayOfInt[(j - 1)] < 0)
/* 3195 */           return null;
/*      */       }
/* 3197 */       return arrayOfInt;
/*      */     }
/* 3199 */     return null;
/*      */   }
/*      */ 
/*      */   private TreePath getPathForIndexs(int[] paramArrayOfInt)
/*      */   {
/* 3209 */     if (paramArrayOfInt == null) {
/* 3210 */       return null;
/*      */     }
/* 3212 */     TreeModel localTreeModel = getModel();
/*      */ 
/* 3214 */     if (localTreeModel == null) {
/* 3215 */       return null;
/*      */     }
/* 3217 */     int i = paramArrayOfInt.length;
/* 3218 */     Object localObject = localTreeModel.getRoot();
/* 3219 */     TreePath localTreePath = new TreePath(localObject);
/*      */ 
/* 3221 */     for (int j = 0; j < i; j++) {
/* 3222 */       localObject = localTreeModel.getChild(localObject, paramArrayOfInt[j]);
/* 3223 */       if (localObject == null)
/* 3224 */         return null;
/* 3225 */       localTreePath = localTreePath.pathByAddingChild(localObject);
/*      */     }
/* 3227 */     return localTreePath;
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredScrollableViewportSize()
/*      */   {
/* 3394 */     int i = getPreferredSize().width;
/* 3395 */     int j = getVisibleRowCount();
/* 3396 */     int k = -1;
/*      */ 
/* 3398 */     if (isFixedRowHeight()) {
/* 3399 */       k = j * getRowHeight();
/*      */     } else {
/* 3401 */       TreeUI localTreeUI = getUI();
/*      */ 
/* 3403 */       if ((localTreeUI != null) && (j > 0)) {
/* 3404 */         int m = localTreeUI.getRowCount(this);
/*      */         Rectangle localRectangle;
/* 3406 */         if (m >= j) {
/* 3407 */           localRectangle = getRowBounds(j - 1);
/* 3408 */           if (localRectangle != null) {
/* 3409 */             k = localRectangle.y + localRectangle.height;
/*      */           }
/*      */         }
/* 3412 */         else if (m > 0) {
/* 3413 */           localRectangle = getRowBounds(0);
/* 3414 */           if (localRectangle != null) {
/* 3415 */             k = localRectangle.height * j;
/*      */           }
/*      */         }
/*      */       }
/* 3419 */       if (k == -1) {
/* 3420 */         k = 16 * j;
/*      */       }
/*      */     }
/* 3423 */     return new Dimension(i, k);
/*      */   }
/*      */ 
/*      */   public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
/*      */   {
/* 3442 */     if (paramInt1 == 1)
/*      */     {
/* 3444 */       int i = getClosestRowForLocation(0, paramRectangle.y);
/*      */ 
/* 3447 */       if (i != -1) {
/* 3448 */         Rectangle localRectangle = getRowBounds(i);
/* 3449 */         if (localRectangle.y != paramRectangle.y) {
/* 3450 */           if (paramInt2 < 0)
/*      */           {
/* 3452 */             return Math.max(0, paramRectangle.y - localRectangle.y);
/*      */           }
/* 3454 */           return localRectangle.y + localRectangle.height - paramRectangle.y;
/*      */         }
/* 3456 */         if (paramInt2 < 0) {
/* 3457 */           if (i != 0) {
/* 3458 */             localRectangle = getRowBounds(i - 1);
/* 3459 */             return localRectangle.height;
/*      */           }
/*      */         }
/*      */         else {
/* 3463 */           return localRectangle.height;
/*      */         }
/*      */       }
/* 3466 */       return 0;
/*      */     }
/* 3468 */     return 4;
/*      */   }
/*      */ 
/*      */   public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
/*      */   {
/* 3486 */     return paramInt1 == 1 ? paramRectangle.height : paramRectangle.width;
/*      */   }
/*      */ 
/*      */   public boolean getScrollableTracksViewportWidth()
/*      */   {
/* 3500 */     Container localContainer = SwingUtilities.getUnwrappedParent(this);
/* 3501 */     if ((localContainer instanceof JViewport)) {
/* 3502 */       return localContainer.getWidth() > getPreferredSize().width;
/*      */     }
/* 3504 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean getScrollableTracksViewportHeight()
/*      */   {
/* 3517 */     Container localContainer = SwingUtilities.getUnwrappedParent(this);
/* 3518 */     if ((localContainer instanceof JViewport)) {
/* 3519 */       return localContainer.getHeight() > getPreferredSize().height;
/*      */     }
/* 3521 */     return false;
/*      */   }
/*      */ 
/*      */   protected void setExpandedState(TreePath paramTreePath, boolean paramBoolean)
/*      */   {
/* 3534 */     if (paramTreePath != null)
/*      */     {
/* 3537 */       TreePath localTreePath = paramTreePath.getParentPath();
/*      */       Stack localStack;
/* 3539 */       if (this.expandedStack.size() == 0) {
/* 3540 */         localStack = new Stack();
/*      */       }
/*      */       else {
/* 3543 */         localStack = (Stack)this.expandedStack.pop();
/*      */       }
/*      */       try
/*      */       {
/* 3547 */         while (localTreePath != null) {
/* 3548 */           if (isExpanded(localTreePath)) {
/* 3549 */             localTreePath = null;
/*      */           }
/*      */           else {
/* 3552 */             localStack.push(localTreePath);
/* 3553 */             localTreePath = localTreePath.getParentPath();
/*      */           }
/*      */         }
/* 3556 */         for (int i = localStack.size() - 1; i >= 0; i--) {
/* 3557 */           localTreePath = (TreePath)localStack.pop();
/* 3558 */           if (!isExpanded(localTreePath)) {
/*      */             try {
/* 3560 */               fireTreeWillExpand(localTreePath);
/*      */             }
/*      */             catch (ExpandVetoException localExpandVetoException1) {
/*      */               return;
/*      */             }
/* 3565 */             this.expandedState.put(localTreePath, Boolean.TRUE);
/* 3566 */             fireTreeExpanded(localTreePath);
/* 3567 */             if (this.accessibleContext != null) {
/* 3568 */               ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3575 */         if (this.expandedStack.size() < TEMP_STACK_SIZE) {
/* 3576 */           localStack.removeAllElements();
/* 3577 */           this.expandedStack.push(localStack);
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 3575 */         if (this.expandedStack.size() < TEMP_STACK_SIZE) {
/* 3576 */           localStack.removeAllElements();
/* 3577 */           this.expandedStack.push(localStack);
/*      */         }
/*      */       }
/*      */       Object localObject1;
/* 3580 */       if (!paramBoolean)
/*      */       {
/* 3582 */         localObject1 = this.expandedState.get(paramTreePath);
/*      */ 
/* 3584 */         if ((localObject1 != null) && (((Boolean)localObject1).booleanValue())) {
/*      */           try {
/* 3586 */             fireTreeWillCollapse(paramTreePath);
/*      */           }
/*      */           catch (ExpandVetoException localExpandVetoException2) {
/* 3589 */             return;
/*      */           }
/* 3591 */           this.expandedState.put(paramTreePath, Boolean.FALSE);
/* 3592 */           fireTreeCollapsed(paramTreePath);
/* 3593 */           if ((removeDescendantSelectedPaths(paramTreePath, false)) && (!isPathSelected(paramTreePath)))
/*      */           {
/* 3596 */             addSelectionPath(paramTreePath);
/*      */           }
/* 3598 */           if (this.accessibleContext != null) {
/* 3599 */             ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 3606 */         localObject1 = this.expandedState.get(paramTreePath);
/*      */ 
/* 3608 */         if ((localObject1 == null) || (!((Boolean)localObject1).booleanValue())) {
/*      */           try {
/* 3610 */             fireTreeWillExpand(paramTreePath);
/*      */           }
/*      */           catch (ExpandVetoException localExpandVetoException3) {
/* 3613 */             return;
/*      */           }
/* 3615 */           this.expandedState.put(paramTreePath, Boolean.TRUE);
/* 3616 */           fireTreeExpanded(paramTreePath);
/* 3617 */           if (this.accessibleContext != null)
/* 3618 */             ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Enumeration<TreePath> getDescendantToggledPaths(TreePath paramTreePath)
/*      */   {
/* 3634 */     if (paramTreePath == null) {
/* 3635 */       return null;
/*      */     }
/* 3637 */     Vector localVector = new Vector();
/* 3638 */     Enumeration localEnumeration = this.expandedState.keys();
/*      */ 
/* 3640 */     while (localEnumeration.hasMoreElements()) {
/* 3641 */       TreePath localTreePath = (TreePath)localEnumeration.nextElement();
/* 3642 */       if (paramTreePath.isDescendant(localTreePath))
/* 3643 */         localVector.addElement(localTreePath);
/*      */     }
/* 3645 */     return localVector.elements();
/*      */   }
/*      */ 
/*      */   protected void removeDescendantToggledPaths(Enumeration<TreePath> paramEnumeration)
/*      */   {
/* 3662 */     if (paramEnumeration != null)
/* 3663 */       while (paramEnumeration.hasMoreElements()) {
/* 3664 */         Enumeration localEnumeration = getDescendantToggledPaths((TreePath)paramEnumeration.nextElement());
/*      */ 
/* 3667 */         if (localEnumeration != null)
/* 3668 */           while (localEnumeration.hasMoreElements())
/* 3669 */             this.expandedState.remove(localEnumeration.nextElement());
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void clearToggledPaths()
/*      */   {
/* 3681 */     this.expandedState.clear();
/*      */   }
/*      */ 
/*      */   protected TreeModelListener createTreeModelListener()
/*      */   {
/* 3694 */     return new TreeModelHandler();
/*      */   }
/*      */ 
/*      */   protected boolean removeDescendantSelectedPaths(TreePath paramTreePath, boolean paramBoolean)
/*      */   {
/* 3707 */     TreePath[] arrayOfTreePath = getDescendantSelectedPaths(paramTreePath, paramBoolean);
/*      */ 
/* 3709 */     if (arrayOfTreePath != null) {
/* 3710 */       getSelectionModel().removeSelectionPaths(arrayOfTreePath);
/* 3711 */       return true;
/*      */     }
/* 3713 */     return false;
/*      */   }
/*      */ 
/*      */   private TreePath[] getDescendantSelectedPaths(TreePath paramTreePath, boolean paramBoolean)
/*      */   {
/* 3722 */     TreeSelectionModel localTreeSelectionModel = getSelectionModel();
/* 3723 */     TreePath[] arrayOfTreePath = localTreeSelectionModel != null ? localTreeSelectionModel.getSelectionPaths() : null;
/*      */ 
/* 3726 */     if (arrayOfTreePath != null) {
/* 3727 */       int i = 0;
/*      */ 
/* 3729 */       for (int j = arrayOfTreePath.length - 1; j >= 0; j--) {
/* 3730 */         if ((arrayOfTreePath[j] != null) && (paramTreePath.isDescendant(arrayOfTreePath[j])) && ((!paramTreePath.equals(arrayOfTreePath[j])) || (paramBoolean)))
/*      */         {
/* 3733 */           i = 1;
/*      */         }
/* 3735 */         else arrayOfTreePath[j] = null;
/*      */       }
/* 3737 */       if (i == 0) {
/* 3738 */         arrayOfTreePath = null;
/*      */       }
/* 3740 */       return arrayOfTreePath;
/*      */     }
/* 3742 */     return null;
/*      */   }
/*      */ 
/*      */   void removeDescendantSelectedPaths(TreeModelEvent paramTreeModelEvent)
/*      */   {
/* 3750 */     TreePath localTreePath = paramTreeModelEvent.getTreePath();
/* 3751 */     Object[] arrayOfObject = paramTreeModelEvent.getChildren();
/* 3752 */     TreeSelectionModel localTreeSelectionModel = getSelectionModel();
/*      */ 
/* 3754 */     if ((localTreeSelectionModel != null) && (localTreePath != null) && (arrayOfObject != null) && (arrayOfObject.length > 0))
/*      */     {
/* 3756 */       for (int i = arrayOfObject.length - 1; i >= 0; 
/* 3757 */         i--)
/*      */       {
/* 3760 */         removeDescendantSelectedPaths(localTreePath.pathByAddingChild(arrayOfObject[i]), true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void setUIProperty(String paramString, Object paramObject)
/*      */   {
/* 4004 */     if (paramString == "rowHeight") {
/* 4005 */       if (!this.rowHeightSet) {
/* 4006 */         setRowHeight(((Number)paramObject).intValue());
/* 4007 */         this.rowHeightSet = false;
/*      */       }
/* 4009 */     } else if (paramString == "scrollsOnExpand") {
/* 4010 */       if (!this.scrollsOnExpandSet) {
/* 4011 */         setScrollsOnExpand(((Boolean)paramObject).booleanValue());
/* 4012 */         this.scrollsOnExpandSet = false;
/*      */       }
/* 4014 */     } else if (paramString == "showsRootHandles") {
/* 4015 */       if (!this.showsRootHandlesSet) {
/* 4016 */         setShowsRootHandles(((Boolean)paramObject).booleanValue());
/* 4017 */         this.showsRootHandlesSet = false;
/*      */       }
/*      */     }
/* 4020 */     else super.setUIProperty(paramString, paramObject);
/*      */   }
/*      */ 
/*      */   protected String paramString()
/*      */   {
/* 4036 */     String str1 = this.rootVisible ? "true" : "false";
/*      */ 
/* 4038 */     String str2 = this.showsRootHandles ? "true" : "false";
/*      */ 
/* 4040 */     String str3 = this.editable ? "true" : "false";
/*      */ 
/* 4042 */     String str4 = this.largeModel ? "true" : "false";
/*      */ 
/* 4044 */     String str5 = this.invokesStopCellEditing ? "true" : "false";
/*      */ 
/* 4046 */     String str6 = this.scrollsOnExpand ? "true" : "false";
/*      */ 
/* 4049 */     return super.paramString() + ",editable=" + str3 + ",invokesStopCellEditing=" + str5 + ",largeModel=" + str4 + ",rootVisible=" + str1 + ",rowHeight=" + this.rowHeight + ",scrollsOnExpand=" + str6 + ",showsRootHandles=" + str2 + ",toggleClickCount=" + this.toggleClickCount + ",visibleRowCount=" + this.visibleRowCount;
/*      */   }
/*      */ 
/*      */   public AccessibleContext getAccessibleContext()
/*      */   {
/* 4075 */     if (this.accessibleContext == null) {
/* 4076 */       this.accessibleContext = new AccessibleJTree();
/*      */     }
/* 4078 */     return this.accessibleContext;
/*      */   }
/*      */ 
/*      */   protected class AccessibleJTree extends JComponent.AccessibleJComponent
/*      */     implements AccessibleSelection, TreeSelectionListener, TreeModelListener, TreeExpansionListener
/*      */   {
/*      */     TreePath leadSelectionPath;
/*      */     Accessible leadSelectionAccessible;
/*      */ 
/*      */     public AccessibleJTree()
/*      */     {
/* 4102 */       super();
/*      */ 
/* 4104 */       TreeModel localTreeModel = JTree.this.getModel();
/* 4105 */       if (localTreeModel != null) {
/* 4106 */         localTreeModel.addTreeModelListener(this);
/*      */       }
/* 4108 */       JTree.this.addTreeExpansionListener(this);
/* 4109 */       JTree.this.addTreeSelectionListener(this);
/* 4110 */       this.leadSelectionPath = JTree.this.getLeadSelectionPath();
/* 4111 */       this.leadSelectionAccessible = (this.leadSelectionPath != null ? new AccessibleJTreeNode(JTree.this, this.leadSelectionPath, JTree.this) : null);
/*      */     }
/*      */ 
/*      */     public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
/*      */     {
/* 4128 */       TreePath localTreePath = paramTreeSelectionEvent.getOldLeadSelectionPath();
/* 4129 */       this.leadSelectionPath = paramTreeSelectionEvent.getNewLeadSelectionPath();
/*      */ 
/* 4131 */       if (localTreePath != this.leadSelectionPath)
/*      */       {
/* 4134 */         Accessible localAccessible = this.leadSelectionAccessible;
/* 4135 */         this.leadSelectionAccessible = (this.leadSelectionPath != null ? new AccessibleJTreeNode(JTree.this, this.leadSelectionPath, null) : null);
/*      */ 
/* 4140 */         firePropertyChange("AccessibleActiveDescendant", localAccessible, this.leadSelectionAccessible);
/*      */       }
/*      */ 
/* 4143 */       firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
/*      */     }
/*      */ 
/*      */     public void fireVisibleDataPropertyChange()
/*      */     {
/* 4157 */       firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
/*      */     }
/*      */ 
/*      */     public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
/*      */     {
/* 4169 */       fireVisibleDataPropertyChange();
/*      */     }
/*      */ 
/*      */     public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
/*      */     {
/* 4178 */       fireVisibleDataPropertyChange();
/*      */     }
/*      */ 
/*      */     public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
/*      */     {
/* 4187 */       fireVisibleDataPropertyChange();
/*      */     }
/*      */ 
/*      */     public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
/*      */     {
/* 4196 */       fireVisibleDataPropertyChange();
/*      */     }
/*      */ 
/*      */     public void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent)
/*      */     {
/* 4205 */       fireVisibleDataPropertyChange();
/* 4206 */       TreePath localTreePath = paramTreeExpansionEvent.getPath();
/* 4207 */       if (localTreePath != null)
/*      */       {
/* 4210 */         AccessibleJTreeNode localAccessibleJTreeNode = new AccessibleJTreeNode(JTree.this, localTreePath, null);
/*      */ 
/* 4213 */         PropertyChangeEvent localPropertyChangeEvent = new PropertyChangeEvent(localAccessibleJTreeNode, "AccessibleState", AccessibleState.EXPANDED, AccessibleState.COLLAPSED);
/*      */ 
/* 4217 */         firePropertyChange("AccessibleState", null, localPropertyChangeEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent)
/*      */     {
/* 4228 */       fireVisibleDataPropertyChange();
/* 4229 */       TreePath localTreePath = paramTreeExpansionEvent.getPath();
/* 4230 */       if (localTreePath != null)
/*      */       {
/* 4234 */         AccessibleJTreeNode localAccessibleJTreeNode = new AccessibleJTreeNode(JTree.this, localTreePath, null);
/*      */ 
/* 4237 */         PropertyChangeEvent localPropertyChangeEvent = new PropertyChangeEvent(localAccessibleJTreeNode, "AccessibleState", AccessibleState.COLLAPSED, AccessibleState.EXPANDED);
/*      */ 
/* 4241 */         firePropertyChange("AccessibleState", null, localPropertyChangeEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     private AccessibleContext getCurrentAccessibleContext()
/*      */     {
/* 4248 */       Component localComponent = getCurrentComponent();
/* 4249 */       if ((localComponent instanceof Accessible)) {
/* 4250 */         return localComponent.getAccessibleContext();
/*      */       }
/* 4252 */       return null;
/*      */     }
/*      */ 
/*      */     private Component getCurrentComponent()
/*      */     {
/* 4260 */       TreeModel localTreeModel = JTree.this.getModel();
/* 4261 */       if (localTreeModel == null) {
/* 4262 */         return null;
/*      */       }
/* 4264 */       TreePath localTreePath = new TreePath(localTreeModel.getRoot());
/* 4265 */       if (JTree.this.isVisible(localTreePath)) {
/* 4266 */         TreeCellRenderer localTreeCellRenderer = JTree.this.getCellRenderer();
/* 4267 */         TreeUI localTreeUI = JTree.this.getUI();
/* 4268 */         if (localTreeUI != null) {
/* 4269 */           int i = localTreeUI.getRowForPath(JTree.this, localTreePath);
/* 4270 */           int j = JTree.this.getLeadSelectionRow();
/* 4271 */           boolean bool1 = (JTree.this.isFocusOwner()) && (j == i);
/*      */ 
/* 4273 */           boolean bool2 = JTree.this.isPathSelected(localTreePath);
/* 4274 */           boolean bool3 = JTree.this.isExpanded(localTreePath);
/*      */ 
/* 4276 */           return localTreeCellRenderer.getTreeCellRendererComponent(JTree.this, localTreeModel.getRoot(), bool2, bool3, localTreeModel.isLeaf(localTreeModel.getRoot()), i, bool1);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4281 */       return null;
/*      */     }
/*      */ 
/*      */     public AccessibleRole getAccessibleRole()
/*      */     {
/* 4294 */       return AccessibleRole.TREE;
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleAt(Point paramPoint)
/*      */     {
/* 4307 */       TreePath localTreePath = JTree.this.getClosestPathForLocation(paramPoint.x, paramPoint.y);
/* 4308 */       if (localTreePath != null)
/*      */       {
/* 4310 */         return new AccessibleJTreeNode(JTree.this, localTreePath, null);
/*      */       }
/* 4312 */       return null;
/*      */     }
/*      */ 
/*      */     public int getAccessibleChildrenCount()
/*      */     {
/* 4323 */       TreeModel localTreeModel = JTree.this.getModel();
/* 4324 */       if (localTreeModel == null) {
/* 4325 */         return 0;
/*      */       }
/* 4327 */       if (JTree.this.isRootVisible()) {
/* 4328 */         return 1;
/*      */       }
/*      */ 
/* 4332 */       return localTreeModel.getChildCount(localTreeModel.getRoot());
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleChild(int paramInt)
/*      */     {
/* 4342 */       TreeModel localTreeModel = JTree.this.getModel();
/* 4343 */       if (localTreeModel == null) {
/* 4344 */         return null;
/*      */       }
/* 4346 */       if (JTree.this.isRootVisible()) {
/* 4347 */         if (paramInt == 0) {
/* 4348 */           Object[] arrayOfObject1 = { localTreeModel.getRoot() };
/* 4349 */           localObject = new TreePath(arrayOfObject1);
/* 4350 */           return new AccessibleJTreeNode(JTree.this, (TreePath)localObject, JTree.this);
/*      */         }
/* 4352 */         return null;
/*      */       }
/*      */ 
/* 4357 */       int i = localTreeModel.getChildCount(localTreeModel.getRoot());
/* 4358 */       if ((paramInt < 0) || (paramInt >= i)) {
/* 4359 */         return null;
/*      */       }
/* 4361 */       Object localObject = localTreeModel.getChild(localTreeModel.getRoot(), paramInt);
/* 4362 */       Object[] arrayOfObject2 = { localTreeModel.getRoot(), localObject };
/* 4363 */       TreePath localTreePath = new TreePath(arrayOfObject2);
/* 4364 */       return new AccessibleJTreeNode(JTree.this, localTreePath, JTree.this);
/*      */     }
/*      */ 
/*      */     public int getAccessibleIndexInParent()
/*      */     {
/* 4376 */       return super.getAccessibleIndexInParent();
/*      */     }
/*      */ 
/*      */     public AccessibleSelection getAccessibleSelection()
/*      */     {
/* 4389 */       return this;
/*      */     }
/*      */ 
/*      */     public int getAccessibleSelectionCount()
/*      */     {
/* 4399 */       Object[] arrayOfObject = new Object[1];
/* 4400 */       arrayOfObject[0] = JTree.this.treeModel.getRoot();
/* 4401 */       TreePath localTreePath = new TreePath(arrayOfObject);
/* 4402 */       if (JTree.this.isPathSelected(localTreePath)) {
/* 4403 */         return 1;
/*      */       }
/* 4405 */       return 0;
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleSelection(int paramInt)
/*      */     {
/* 4420 */       if (paramInt == 0) {
/* 4421 */         Object[] arrayOfObject = new Object[1];
/* 4422 */         arrayOfObject[0] = JTree.this.treeModel.getRoot();
/* 4423 */         TreePath localTreePath = new TreePath(arrayOfObject);
/* 4424 */         if (JTree.this.isPathSelected(localTreePath)) {
/* 4425 */           return new AccessibleJTreeNode(JTree.this, localTreePath, JTree.this);
/*      */         }
/*      */       }
/* 4428 */       return null;
/*      */     }
/*      */ 
/*      */     public boolean isAccessibleChildSelected(int paramInt)
/*      */     {
/* 4439 */       if (paramInt == 0) {
/* 4440 */         Object[] arrayOfObject = new Object[1];
/* 4441 */         arrayOfObject[0] = JTree.this.treeModel.getRoot();
/* 4442 */         TreePath localTreePath = new TreePath(arrayOfObject);
/* 4443 */         return JTree.this.isPathSelected(localTreePath);
/*      */       }
/* 4445 */       return false;
/*      */     }
/*      */ 
/*      */     public void addAccessibleSelection(int paramInt)
/*      */     {
/* 4459 */       TreeModel localTreeModel = JTree.this.getModel();
/* 4460 */       if ((localTreeModel != null) && 
/* 4461 */         (paramInt == 0)) {
/* 4462 */         Object[] arrayOfObject = { localTreeModel.getRoot() };
/* 4463 */         TreePath localTreePath = new TreePath(arrayOfObject);
/* 4464 */         JTree.this.addSelectionPath(localTreePath);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void removeAccessibleSelection(int paramInt)
/*      */     {
/* 4477 */       TreeModel localTreeModel = JTree.this.getModel();
/* 4478 */       if ((localTreeModel != null) && 
/* 4479 */         (paramInt == 0)) {
/* 4480 */         Object[] arrayOfObject = { localTreeModel.getRoot() };
/* 4481 */         TreePath localTreePath = new TreePath(arrayOfObject);
/* 4482 */         JTree.this.removeSelectionPath(localTreePath);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void clearAccessibleSelection()
/*      */     {
/* 4492 */       int i = getAccessibleChildrenCount();
/* 4493 */       for (int j = 0; j < i; j++)
/* 4494 */         removeAccessibleSelection(j);
/*      */     }
/*      */ 
/*      */     public void selectAllAccessibleSelection()
/*      */     {
/* 4503 */       TreeModel localTreeModel = JTree.this.getModel();
/* 4504 */       if (localTreeModel != null) {
/* 4505 */         Object[] arrayOfObject = { localTreeModel.getRoot() };
/* 4506 */         TreePath localTreePath = new TreePath(arrayOfObject);
/* 4507 */         JTree.this.addSelectionPath(localTreePath);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected class AccessibleJTreeNode extends AccessibleContext
/*      */       implements Accessible, AccessibleComponent, AccessibleSelection, AccessibleAction
/*      */     {
/* 4520 */       private JTree tree = null;
/* 4521 */       private TreeModel treeModel = null;
/* 4522 */       private Object obj = null;
/* 4523 */       private TreePath path = null;
/* 4524 */       private Accessible accessibleParent = null;
/* 4525 */       private int index = 0;
/* 4526 */       private boolean isLeaf = false;
/*      */ 
/*      */       public AccessibleJTreeNode(JTree paramTreePath, TreePath paramAccessible, Accessible arg4)
/*      */       {
/* 4533 */         this.tree = paramTreePath;
/* 4534 */         this.path = paramAccessible;
/*      */         Object localObject;
/* 4535 */         this.accessibleParent = localObject;
/* 4536 */         this.treeModel = paramTreePath.getModel();
/* 4537 */         this.obj = paramAccessible.getLastPathComponent();
/* 4538 */         if (this.treeModel != null)
/* 4539 */           this.isLeaf = this.treeModel.isLeaf(this.obj);
/*      */       }
/*      */ 
/*      */       private TreePath getChildTreePath(int paramInt)
/*      */       {
/* 4546 */         if ((paramInt < 0) || (paramInt >= getAccessibleChildrenCount())) {
/* 4547 */           return null;
/*      */         }
/* 4549 */         Object localObject = this.treeModel.getChild(this.obj, paramInt);
/* 4550 */         Object[] arrayOfObject1 = this.path.getPath();
/* 4551 */         Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
/* 4552 */         System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
/* 4553 */         arrayOfObject2[(arrayOfObject2.length - 1)] = localObject;
/* 4554 */         return new TreePath(arrayOfObject2);
/*      */       }
/*      */ 
/*      */       public AccessibleContext getAccessibleContext()
/*      */       {
/* 4567 */         return this;
/*      */       }
/*      */ 
/*      */       private AccessibleContext getCurrentAccessibleContext() {
/* 4571 */         Component localComponent = getCurrentComponent();
/* 4572 */         if ((localComponent instanceof AccessibleAction)) {
/* 4573 */           return localComponent.getAccessibleContext();
/*      */         }
/* 4575 */         return null;
/*      */       }
/*      */ 
/*      */       private Component getCurrentComponent()
/*      */       {
/* 4583 */         if (this.tree.isVisible(this.path)) {
/* 4584 */           TreeCellRenderer localTreeCellRenderer = this.tree.getCellRenderer();
/* 4585 */           if (localTreeCellRenderer == null) {
/* 4586 */             return null;
/*      */           }
/* 4588 */           TreeUI localTreeUI = this.tree.getUI();
/* 4589 */           if (localTreeUI != null) {
/* 4590 */             int i = localTreeUI.getRowForPath(JTree.this, this.path);
/* 4591 */             boolean bool1 = this.tree.isPathSelected(this.path);
/* 4592 */             boolean bool2 = this.tree.isExpanded(this.path);
/* 4593 */             boolean bool3 = false;
/* 4594 */             return localTreeCellRenderer.getTreeCellRendererComponent(this.tree, this.obj, bool1, bool2, this.isLeaf, i, bool3);
/*      */           }
/*      */         }
/*      */ 
/* 4598 */         return null;
/*      */       }
/*      */ 
/*      */       public String getAccessibleName()
/*      */       {
/* 4610 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4611 */         if (localAccessibleContext != null) {
/* 4612 */           String str = localAccessibleContext.getAccessibleName();
/* 4613 */           if ((str != null) && (str != "")) {
/* 4614 */             return localAccessibleContext.getAccessibleName();
/*      */           }
/* 4616 */           return null;
/*      */         }
/*      */ 
/* 4619 */         if ((this.accessibleName != null) && (this.accessibleName != "")) {
/* 4620 */           return this.accessibleName;
/*      */         }
/*      */ 
/* 4623 */         return (String)JTree.this.getClientProperty("AccessibleName");
/*      */       }
/*      */ 
/*      */       public void setAccessibleName(String paramString)
/*      */       {
/* 4633 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4634 */         if (localAccessibleContext != null)
/* 4635 */           localAccessibleContext.setAccessibleName(paramString);
/*      */         else
/* 4637 */           super.setAccessibleName(paramString);
/*      */       }
/*      */ 
/*      */       public String getAccessibleDescription()
/*      */       {
/* 4651 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4652 */         if (localAccessibleContext != null) {
/* 4653 */           return localAccessibleContext.getAccessibleDescription();
/*      */         }
/* 4655 */         return super.getAccessibleDescription();
/*      */       }
/*      */ 
/*      */       public void setAccessibleDescription(String paramString)
/*      */       {
/* 4665 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4666 */         if (localAccessibleContext != null)
/* 4667 */           localAccessibleContext.setAccessibleDescription(paramString);
/*      */         else
/* 4669 */           super.setAccessibleDescription(paramString);
/*      */       }
/*      */ 
/*      */       public AccessibleRole getAccessibleRole()
/*      */       {
/* 4680 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4681 */         if (localAccessibleContext != null) {
/* 4682 */           return localAccessibleContext.getAccessibleRole();
/*      */         }
/* 4684 */         return AccessibleRole.UNKNOWN;
/*      */       }
/*      */ 
/*      */       public AccessibleStateSet getAccessibleStateSet()
/*      */       {
/* 4696 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/*      */         AccessibleStateSet localAccessibleStateSet;
/* 4698 */         if (localAccessibleContext != null)
/* 4699 */           localAccessibleStateSet = localAccessibleContext.getAccessibleStateSet();
/*      */         else {
/* 4701 */           localAccessibleStateSet = new AccessibleStateSet();
/*      */         }
/*      */ 
/* 4705 */         if (isShowing())
/* 4706 */           localAccessibleStateSet.add(AccessibleState.SHOWING);
/* 4707 */         else if (localAccessibleStateSet.contains(AccessibleState.SHOWING)) {
/* 4708 */           localAccessibleStateSet.remove(AccessibleState.SHOWING);
/*      */         }
/* 4710 */         if (isVisible())
/* 4711 */           localAccessibleStateSet.add(AccessibleState.VISIBLE);
/* 4712 */         else if (localAccessibleStateSet.contains(AccessibleState.VISIBLE)) {
/* 4713 */           localAccessibleStateSet.remove(AccessibleState.VISIBLE);
/*      */         }
/* 4715 */         if (this.tree.isPathSelected(this.path)) {
/* 4716 */           localAccessibleStateSet.add(AccessibleState.SELECTED);
/*      */         }
/* 4718 */         if (this.path == JTree.this.getLeadSelectionPath()) {
/* 4719 */           localAccessibleStateSet.add(AccessibleState.ACTIVE);
/*      */         }
/* 4721 */         if (!this.isLeaf) {
/* 4722 */           localAccessibleStateSet.add(AccessibleState.EXPANDABLE);
/*      */         }
/* 4724 */         if (this.tree.isExpanded(this.path))
/* 4725 */           localAccessibleStateSet.add(AccessibleState.EXPANDED);
/*      */         else {
/* 4727 */           localAccessibleStateSet.add(AccessibleState.COLLAPSED);
/*      */         }
/* 4729 */         if (this.tree.isEditable()) {
/* 4730 */           localAccessibleStateSet.add(AccessibleState.EDITABLE);
/*      */         }
/* 4732 */         return localAccessibleStateSet;
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleParent()
/*      */       {
/* 4744 */         if (this.accessibleParent == null) {
/* 4745 */           Object[] arrayOfObject1 = this.path.getPath();
/* 4746 */           if (arrayOfObject1.length > 1) {
/* 4747 */             Object localObject = arrayOfObject1[(arrayOfObject1.length - 2)];
/* 4748 */             if (this.treeModel != null) {
/* 4749 */               this.index = this.treeModel.getIndexOfChild(localObject, this.obj);
/*      */             }
/* 4751 */             Object[] arrayOfObject2 = new Object[arrayOfObject1.length - 1];
/* 4752 */             System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length - 1);
/*      */ 
/* 4754 */             TreePath localTreePath = new TreePath(arrayOfObject2);
/* 4755 */             this.accessibleParent = new AccessibleJTreeNode(JTree.AccessibleJTree.this, this.tree, localTreePath, null);
/*      */ 
/* 4758 */             setAccessibleParent(this.accessibleParent);
/* 4759 */           } else if (this.treeModel != null) {
/* 4760 */             this.accessibleParent = this.tree;
/* 4761 */             this.index = 0;
/* 4762 */             setAccessibleParent(this.accessibleParent);
/*      */           }
/*      */         }
/* 4765 */         return this.accessibleParent;
/*      */       }
/*      */ 
/*      */       public int getAccessibleIndexInParent()
/*      */       {
/* 4777 */         if (this.accessibleParent == null) {
/* 4778 */           getAccessibleParent();
/*      */         }
/* 4780 */         Object[] arrayOfObject = this.path.getPath();
/* 4781 */         if (arrayOfObject.length > 1) {
/* 4782 */           Object localObject = arrayOfObject[(arrayOfObject.length - 2)];
/* 4783 */           if (this.treeModel != null) {
/* 4784 */             this.index = this.treeModel.getIndexOfChild(localObject, this.obj);
/*      */           }
/*      */         }
/* 4787 */         return this.index;
/*      */       }
/*      */ 
/*      */       public int getAccessibleChildrenCount()
/*      */       {
/* 4798 */         return this.treeModel.getChildCount(this.obj);
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleChild(int paramInt)
/*      */       {
/* 4810 */         if ((paramInt < 0) || (paramInt >= getAccessibleChildrenCount())) {
/* 4811 */           return null;
/*      */         }
/* 4813 */         Object localObject = this.treeModel.getChild(this.obj, paramInt);
/* 4814 */         Object[] arrayOfObject1 = this.path.getPath();
/* 4815 */         Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
/* 4816 */         System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
/* 4817 */         arrayOfObject2[(arrayOfObject2.length - 1)] = localObject;
/* 4818 */         TreePath localTreePath = new TreePath(arrayOfObject2);
/* 4819 */         return new AccessibleJTreeNode(JTree.AccessibleJTree.this, JTree.this, localTreePath, this);
/*      */       }
/*      */ 
/*      */       public Locale getLocale()
/*      */       {
/* 4836 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4837 */         if (localAccessibleContext != null) {
/* 4838 */           return localAccessibleContext.getLocale();
/*      */         }
/* 4840 */         return this.tree.getLocale();
/*      */       }
/*      */ 
/*      */       public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */       {
/* 4851 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4852 */         if (localAccessibleContext != null)
/* 4853 */           localAccessibleContext.addPropertyChangeListener(paramPropertyChangeListener);
/*      */         else
/* 4855 */           super.addPropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */ 
/*      */       public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */       {
/* 4867 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4868 */         if (localAccessibleContext != null)
/* 4869 */           localAccessibleContext.removePropertyChangeListener(paramPropertyChangeListener);
/*      */         else
/* 4871 */           super.removePropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */ 
/*      */       public AccessibleAction getAccessibleAction()
/*      */       {
/* 4884 */         return this;
/*      */       }
/*      */ 
/*      */       public AccessibleComponent getAccessibleComponent()
/*      */       {
/* 4896 */         return this;
/*      */       }
/*      */ 
/*      */       public AccessibleSelection getAccessibleSelection()
/*      */       {
/* 4906 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4907 */         if ((localAccessibleContext != null) && (this.isLeaf)) {
/* 4908 */           return getCurrentAccessibleContext().getAccessibleSelection();
/*      */         }
/* 4910 */         return this;
/*      */       }
/*      */ 
/*      */       public AccessibleText getAccessibleText()
/*      */       {
/* 4921 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4922 */         if (localAccessibleContext != null) {
/* 4923 */           return getCurrentAccessibleContext().getAccessibleText();
/*      */         }
/* 4925 */         return null;
/*      */       }
/*      */ 
/*      */       public AccessibleValue getAccessibleValue()
/*      */       {
/* 4936 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4937 */         if (localAccessibleContext != null) {
/* 4938 */           return getCurrentAccessibleContext().getAccessibleValue();
/*      */         }
/* 4940 */         return null;
/*      */       }
/*      */ 
/*      */       public Color getBackground()
/*      */       {
/* 4954 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4955 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 4956 */           return ((AccessibleComponent)localAccessibleContext).getBackground();
/*      */         }
/* 4958 */         Component localComponent = getCurrentComponent();
/* 4959 */         if (localComponent != null) {
/* 4960 */           return localComponent.getBackground();
/*      */         }
/* 4962 */         return null;
/*      */       }
/*      */ 
/*      */       public void setBackground(Color paramColor)
/*      */       {
/* 4973 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4974 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 4975 */           ((AccessibleComponent)localAccessibleContext).setBackground(paramColor);
/*      */         } else {
/* 4977 */           Component localComponent = getCurrentComponent();
/* 4978 */           if (localComponent != null)
/* 4979 */             localComponent.setBackground(paramColor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Color getForeground()
/*      */       {
/* 4992 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 4993 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 4994 */           return ((AccessibleComponent)localAccessibleContext).getForeground();
/*      */         }
/* 4996 */         Component localComponent = getCurrentComponent();
/* 4997 */         if (localComponent != null) {
/* 4998 */           return localComponent.getForeground();
/*      */         }
/* 5000 */         return null;
/*      */       }
/*      */ 
/*      */       public void setForeground(Color paramColor)
/*      */       {
/* 5006 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5007 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5008 */           ((AccessibleComponent)localAccessibleContext).setForeground(paramColor);
/*      */         } else {
/* 5010 */           Component localComponent = getCurrentComponent();
/* 5011 */           if (localComponent != null)
/* 5012 */             localComponent.setForeground(paramColor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Cursor getCursor()
/*      */       {
/* 5018 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5019 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5020 */           return ((AccessibleComponent)localAccessibleContext).getCursor();
/*      */         }
/* 5022 */         Component localComponent = getCurrentComponent();
/* 5023 */         if (localComponent != null) {
/* 5024 */           return localComponent.getCursor();
/*      */         }
/* 5026 */         Accessible localAccessible = getAccessibleParent();
/* 5027 */         if ((localAccessible instanceof AccessibleComponent)) {
/* 5028 */           return ((AccessibleComponent)localAccessible).getCursor();
/*      */         }
/* 5030 */         return null;
/*      */       }
/*      */ 
/*      */       public void setCursor(Cursor paramCursor)
/*      */       {
/* 5037 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5038 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5039 */           ((AccessibleComponent)localAccessibleContext).setCursor(paramCursor);
/*      */         } else {
/* 5041 */           Component localComponent = getCurrentComponent();
/* 5042 */           if (localComponent != null)
/* 5043 */             localComponent.setCursor(paramCursor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Font getFont()
/*      */       {
/* 5049 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5050 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5051 */           return ((AccessibleComponent)localAccessibleContext).getFont();
/*      */         }
/* 5053 */         Component localComponent = getCurrentComponent();
/* 5054 */         if (localComponent != null) {
/* 5055 */           return localComponent.getFont();
/*      */         }
/* 5057 */         return null;
/*      */       }
/*      */ 
/*      */       public void setFont(Font paramFont)
/*      */       {
/* 5063 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5064 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5065 */           ((AccessibleComponent)localAccessibleContext).setFont(paramFont);
/*      */         } else {
/* 5067 */           Component localComponent = getCurrentComponent();
/* 5068 */           if (localComponent != null)
/* 5069 */             localComponent.setFont(paramFont);
/*      */         }
/*      */       }
/*      */ 
/*      */       public FontMetrics getFontMetrics(Font paramFont)
/*      */       {
/* 5075 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5076 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5077 */           return ((AccessibleComponent)localAccessibleContext).getFontMetrics(paramFont);
/*      */         }
/* 5079 */         Component localComponent = getCurrentComponent();
/* 5080 */         if (localComponent != null) {
/* 5081 */           return localComponent.getFontMetrics(paramFont);
/*      */         }
/* 5083 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean isEnabled()
/*      */       {
/* 5089 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5090 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5091 */           return ((AccessibleComponent)localAccessibleContext).isEnabled();
/*      */         }
/* 5093 */         Component localComponent = getCurrentComponent();
/* 5094 */         if (localComponent != null) {
/* 5095 */           return localComponent.isEnabled();
/*      */         }
/* 5097 */         return false;
/*      */       }
/*      */ 
/*      */       public void setEnabled(boolean paramBoolean)
/*      */       {
/* 5103 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5104 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5105 */           ((AccessibleComponent)localAccessibleContext).setEnabled(paramBoolean);
/*      */         } else {
/* 5107 */           Component localComponent = getCurrentComponent();
/* 5108 */           if (localComponent != null)
/* 5109 */             localComponent.setEnabled(paramBoolean);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean isVisible()
/*      */       {
/* 5115 */         Rectangle localRectangle1 = this.tree.getPathBounds(this.path);
/* 5116 */         Rectangle localRectangle2 = this.tree.getVisibleRect();
/* 5117 */         return (localRectangle1 != null) && (localRectangle2 != null) && (localRectangle2.intersects(localRectangle1));
/*      */       }
/*      */ 
/*      */       public void setVisible(boolean paramBoolean)
/*      */       {
/*      */       }
/*      */ 
/*      */       public boolean isShowing() {
/* 5125 */         return (this.tree.isShowing()) && (isVisible());
/*      */       }
/*      */ 
/*      */       public boolean contains(Point paramPoint) {
/* 5129 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5130 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5131 */           localObject = ((AccessibleComponent)localAccessibleContext).getBounds();
/* 5132 */           return ((Rectangle)localObject).contains(paramPoint);
/*      */         }
/* 5134 */         Object localObject = getCurrentComponent();
/* 5135 */         if (localObject != null) {
/* 5136 */           Rectangle localRectangle = ((Component)localObject).getBounds();
/* 5137 */           return localRectangle.contains(paramPoint);
/*      */         }
/* 5139 */         return getBounds().contains(paramPoint);
/*      */       }
/*      */ 
/*      */       public Point getLocationOnScreen()
/*      */       {
/* 5145 */         if (this.tree != null) {
/* 5146 */           Point localPoint1 = this.tree.getLocationOnScreen();
/* 5147 */           Rectangle localRectangle = this.tree.getPathBounds(this.path);
/* 5148 */           if ((localPoint1 != null) && (localRectangle != null)) {
/* 5149 */             Point localPoint2 = new Point(localRectangle.x, localRectangle.y);
/*      */ 
/* 5151 */             localPoint2.translate(localPoint1.x, localPoint1.y);
/* 5152 */             return localPoint2;
/*      */           }
/* 5154 */           return null;
/*      */         }
/*      */ 
/* 5157 */         return null;
/*      */       }
/*      */ 
/*      */       protected Point getLocationInJTree()
/*      */       {
/* 5162 */         Rectangle localRectangle = this.tree.getPathBounds(this.path);
/* 5163 */         if (localRectangle != null) {
/* 5164 */           return localRectangle.getLocation();
/*      */         }
/* 5166 */         return null;
/*      */       }
/*      */ 
/*      */       public Point getLocation()
/*      */       {
/* 5171 */         Rectangle localRectangle = getBounds();
/* 5172 */         if (localRectangle != null) {
/* 5173 */           return localRectangle.getLocation();
/*      */         }
/* 5175 */         return null;
/*      */       }
/*      */ 
/*      */       public void setLocation(Point paramPoint)
/*      */       {
/*      */       }
/*      */ 
/*      */       public Rectangle getBounds() {
/* 5183 */         Rectangle localRectangle = this.tree.getPathBounds(this.path);
/* 5184 */         Accessible localAccessible = getAccessibleParent();
/* 5185 */         if ((localAccessible != null) && 
/* 5186 */           ((localAccessible instanceof AccessibleJTreeNode))) {
/* 5187 */           Point localPoint = ((AccessibleJTreeNode)localAccessible).getLocationInJTree();
/* 5188 */           if ((localPoint != null) && (localRectangle != null))
/* 5189 */             localRectangle.translate(-localPoint.x, -localPoint.y);
/*      */           else {
/* 5191 */             return null;
/*      */           }
/*      */         }
/*      */ 
/* 5195 */         return localRectangle;
/*      */       }
/*      */ 
/*      */       public void setBounds(Rectangle paramRectangle) {
/* 5199 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5200 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5201 */           ((AccessibleComponent)localAccessibleContext).setBounds(paramRectangle);
/*      */         } else {
/* 5203 */           Component localComponent = getCurrentComponent();
/* 5204 */           if (localComponent != null)
/* 5205 */             localComponent.setBounds(paramRectangle);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Dimension getSize()
/*      */       {
/* 5211 */         return getBounds().getSize();
/*      */       }
/*      */ 
/*      */       public void setSize(Dimension paramDimension) {
/* 5215 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5216 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5217 */           ((AccessibleComponent)localAccessibleContext).setSize(paramDimension);
/*      */         } else {
/* 5219 */           Component localComponent = getCurrentComponent();
/* 5220 */           if (localComponent != null)
/* 5221 */             localComponent.setSize(paramDimension);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleAt(Point paramPoint)
/*      */       {
/* 5237 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5238 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5239 */           return ((AccessibleComponent)localAccessibleContext).getAccessibleAt(paramPoint);
/*      */         }
/* 5241 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean isFocusTraversable()
/*      */       {
/* 5246 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5247 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5248 */           return ((AccessibleComponent)localAccessibleContext).isFocusTraversable();
/*      */         }
/* 5250 */         Component localComponent = getCurrentComponent();
/* 5251 */         if (localComponent != null) {
/* 5252 */           return localComponent.isFocusTraversable();
/*      */         }
/* 5254 */         return false;
/*      */       }
/*      */ 
/*      */       public void requestFocus()
/*      */       {
/* 5260 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5261 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5262 */           ((AccessibleComponent)localAccessibleContext).requestFocus();
/*      */         } else {
/* 5264 */           Component localComponent = getCurrentComponent();
/* 5265 */           if (localComponent != null)
/* 5266 */             localComponent.requestFocus();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void addFocusListener(FocusListener paramFocusListener)
/*      */       {
/* 5272 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5273 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5274 */           ((AccessibleComponent)localAccessibleContext).addFocusListener(paramFocusListener);
/*      */         } else {
/* 5276 */           Component localComponent = getCurrentComponent();
/* 5277 */           if (localComponent != null)
/* 5278 */             localComponent.addFocusListener(paramFocusListener);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void removeFocusListener(FocusListener paramFocusListener)
/*      */       {
/* 5284 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5285 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 5286 */           ((AccessibleComponent)localAccessibleContext).removeFocusListener(paramFocusListener);
/*      */         } else {
/* 5288 */           Component localComponent = getCurrentComponent();
/* 5289 */           if (localComponent != null)
/* 5290 */             localComponent.removeFocusListener(paramFocusListener);
/*      */         }
/*      */       }
/*      */ 
/*      */       public int getAccessibleSelectionCount()
/*      */       {
/* 5304 */         int i = 0;
/* 5305 */         int j = getAccessibleChildrenCount();
/* 5306 */         for (int k = 0; k < j; k++) {
/* 5307 */           TreePath localTreePath = getChildTreePath(k);
/* 5308 */           if (this.tree.isPathSelected(localTreePath)) {
/* 5309 */             i++;
/*      */           }
/*      */         }
/* 5312 */         return i;
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleSelection(int paramInt)
/*      */       {
/* 5325 */         int i = getAccessibleChildrenCount();
/* 5326 */         if ((paramInt < 0) || (paramInt >= i)) {
/* 5327 */           return null;
/*      */         }
/* 5329 */         int j = 0;
/* 5330 */         for (int k = 0; (k < i) && (paramInt >= j); k++) {
/* 5331 */           TreePath localTreePath = getChildTreePath(k);
/* 5332 */           if (this.tree.isPathSelected(localTreePath)) {
/* 5333 */             if (j == paramInt) {
/* 5334 */               return new AccessibleJTreeNode(JTree.AccessibleJTree.this, this.tree, localTreePath, this);
/*      */             }
/* 5336 */             j++;
/*      */           }
/*      */         }
/*      */ 
/* 5340 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean isAccessibleChildSelected(int paramInt)
/*      */       {
/* 5351 */         int i = getAccessibleChildrenCount();
/* 5352 */         if ((paramInt < 0) || (paramInt >= i)) {
/* 5353 */           return false;
/*      */         }
/* 5355 */         TreePath localTreePath = getChildTreePath(paramInt);
/* 5356 */         return this.tree.isPathSelected(localTreePath);
/*      */       }
/*      */ 
/*      */       public void addAccessibleSelection(int paramInt)
/*      */       {
/* 5370 */         TreeModel localTreeModel = JTree.this.getModel();
/* 5371 */         if ((localTreeModel != null) && 
/* 5372 */           (paramInt >= 0) && (paramInt < getAccessibleChildrenCount())) {
/* 5373 */           TreePath localTreePath = getChildTreePath(paramInt);
/* 5374 */           JTree.this.addSelectionPath(localTreePath);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void removeAccessibleSelection(int paramInt)
/*      */       {
/* 5388 */         TreeModel localTreeModel = JTree.this.getModel();
/* 5389 */         if ((localTreeModel != null) && 
/* 5390 */           (paramInt >= 0) && (paramInt < getAccessibleChildrenCount())) {
/* 5391 */           TreePath localTreePath = getChildTreePath(paramInt);
/* 5392 */           JTree.this.removeSelectionPath(localTreePath);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void clearAccessibleSelection()
/*      */       {
/* 5402 */         int i = getAccessibleChildrenCount();
/* 5403 */         for (int j = 0; j < i; j++)
/* 5404 */           removeAccessibleSelection(j);
/*      */       }
/*      */ 
/*      */       public void selectAllAccessibleSelection()
/*      */       {
/* 5413 */         TreeModel localTreeModel = JTree.this.getModel();
/* 5414 */         if (localTreeModel != null) {
/* 5415 */           int i = getAccessibleChildrenCount();
/*      */ 
/* 5417 */           for (int j = 0; j < i; j++) {
/* 5418 */             TreePath localTreePath = getChildTreePath(j);
/* 5419 */             JTree.this.addSelectionPath(localTreePath);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */       public int getAccessibleActionCount()
/*      */       {
/* 5435 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5436 */         if (localAccessibleContext != null) {
/* 5437 */           AccessibleAction localAccessibleAction = localAccessibleContext.getAccessibleAction();
/* 5438 */           if (localAccessibleAction != null) {
/* 5439 */             return localAccessibleAction.getAccessibleActionCount() + (this.isLeaf ? 0 : 1);
/*      */           }
/*      */         }
/* 5442 */         return this.isLeaf ? 0 : 1;
/*      */       }
/*      */ 
/*      */       public String getAccessibleActionDescription(int paramInt)
/*      */       {
/* 5455 */         if ((paramInt < 0) || (paramInt >= getAccessibleActionCount())) {
/* 5456 */           return null;
/*      */         }
/* 5458 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5459 */         if (paramInt == 0)
/*      */         {
/* 5461 */           return AccessibleAction.TOGGLE_EXPAND;
/* 5462 */         }if (localAccessibleContext != null) {
/* 5463 */           AccessibleAction localAccessibleAction = localAccessibleContext.getAccessibleAction();
/* 5464 */           if (localAccessibleAction != null) {
/* 5465 */             return localAccessibleAction.getAccessibleActionDescription(paramInt - 1);
/*      */           }
/*      */         }
/* 5468 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean doAccessibleAction(int paramInt)
/*      */       {
/* 5481 */         if ((paramInt < 0) || (paramInt >= getAccessibleActionCount())) {
/* 5482 */           return false;
/*      */         }
/* 5484 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 5485 */         if (paramInt == 0) {
/* 5486 */           if (JTree.this.isExpanded(this.path))
/* 5487 */             JTree.this.collapsePath(this.path);
/*      */           else {
/* 5489 */             JTree.this.expandPath(this.path);
/*      */           }
/* 5491 */           return true;
/* 5492 */         }if (localAccessibleContext != null) {
/* 5493 */           AccessibleAction localAccessibleAction = localAccessibleContext.getAccessibleAction();
/* 5494 */           if (localAccessibleAction != null) {
/* 5495 */             return localAccessibleAction.doAccessibleAction(paramInt - 1);
/*      */           }
/*      */         }
/* 5498 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class DropLocation extends TransferHandler.DropLocation
/*      */   {
/*      */     private final TreePath path;
/*      */     private final int index;
/*      */ 
/*      */     private DropLocation(Point paramPoint, TreePath paramTreePath, int paramInt)
/*      */     {
/*  331 */       super();
/*  332 */       this.path = paramTreePath;
/*  333 */       this.index = paramInt;
/*      */     }
/*      */ 
/*      */     public int getChildIndex()
/*      */     {
/*  361 */       return this.index;
/*      */     }
/*      */ 
/*      */     public TreePath getPath()
/*      */     {
/*  400 */       return this.path;
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  412 */       return getClass().getName() + "[dropPoint=" + getDropPoint() + "," + "path=" + this.path + "," + "childIndex=" + this.index + "]";
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class DynamicUtilTreeNode extends DefaultMutableTreeNode
/*      */   {
/*      */     protected boolean hasChildren;
/*      */     protected Object childValue;
/*      */     protected boolean loadedChildren;
/*      */ 
/*      */     public static void createChildren(DefaultMutableTreeNode paramDefaultMutableTreeNode, Object paramObject)
/*      */     {
/*      */       Object localObject1;
/* 3884 */       if ((paramObject instanceof Vector)) {
/* 3885 */         localObject1 = (Vector)paramObject;
/*      */ 
/* 3887 */         int i = 0; int k = ((Vector)localObject1).size();
/* 3888 */         for (; i < k; i++) {
/* 3889 */           paramDefaultMutableTreeNode.add(new DynamicUtilTreeNode(((Vector)localObject1).elementAt(i), ((Vector)localObject1).elementAt(i)));
/*      */         }
/*      */ 
/*      */       }
/* 3893 */       else if ((paramObject instanceof Hashtable)) {
/* 3894 */         localObject1 = (Hashtable)paramObject;
/* 3895 */         Enumeration localEnumeration = ((Hashtable)localObject1).keys();
/*      */ 
/* 3898 */         while (localEnumeration.hasMoreElements()) {
/* 3899 */           Object localObject2 = localEnumeration.nextElement();
/* 3900 */           paramDefaultMutableTreeNode.add(new DynamicUtilTreeNode(localObject2, ((Hashtable)localObject1).get(localObject2)));
/*      */         }
/*      */ 
/*      */       }
/* 3904 */       else if ((paramObject instanceof Object[])) {
/* 3905 */         localObject1 = (Object[])paramObject;
/*      */ 
/* 3907 */         int j = 0; int m = localObject1.length;
/* 3908 */         for (; j < m; j++)
/* 3909 */           paramDefaultMutableTreeNode.add(new DynamicUtilTreeNode(localObject1[j], localObject1[j]));
/*      */       }
/*      */     }
/*      */ 
/*      */     public DynamicUtilTreeNode(Object paramObject1, Object paramObject2)
/*      */     {
/* 3932 */       super();
/* 3933 */       this.loadedChildren = false;
/* 3934 */       this.childValue = paramObject2;
/* 3935 */       if (paramObject2 != null) {
/* 3936 */         if ((paramObject2 instanceof Vector))
/* 3937 */           setAllowsChildren(true);
/* 3938 */         else if ((paramObject2 instanceof Hashtable))
/* 3939 */           setAllowsChildren(true);
/* 3940 */         else if ((paramObject2 instanceof Object[]))
/* 3941 */           setAllowsChildren(true);
/*      */         else
/* 3943 */           setAllowsChildren(false);
/*      */       }
/*      */       else
/* 3946 */         setAllowsChildren(false);
/*      */     }
/*      */ 
/*      */     public boolean isLeaf()
/*      */     {
/* 3957 */       return !getAllowsChildren();
/*      */     }
/*      */ 
/*      */     public int getChildCount()
/*      */     {
/* 3966 */       if (!this.loadedChildren)
/* 3967 */         loadChildren();
/* 3968 */       return super.getChildCount();
/*      */     }
/*      */ 
/*      */     protected void loadChildren()
/*      */     {
/* 3980 */       this.loadedChildren = true;
/* 3981 */       createChildren(this, this.childValue);
/*      */     }
/*      */ 
/*      */     public TreeNode getChildAt(int paramInt)
/*      */     {
/* 3988 */       if (!this.loadedChildren)
/* 3989 */         loadChildren();
/* 3990 */       return super.getChildAt(paramInt);
/*      */     }
/*      */ 
/*      */     public Enumeration children()
/*      */     {
/* 3997 */       if (!this.loadedChildren)
/* 3998 */         loadChildren();
/* 3999 */       return super.children();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static class EmptySelectionModel extends DefaultTreeSelectionModel
/*      */   {
/* 3249 */     protected static final EmptySelectionModel sharedInstance = new EmptySelectionModel();
/*      */ 
/*      */     public static EmptySelectionModel sharedInstance()
/*      */     {
/* 3258 */       return sharedInstance;
/*      */     }
/*      */ 
/*      */     public void setSelectionPaths(TreePath[] paramArrayOfTreePath)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addSelectionPaths(TreePath[] paramArrayOfTreePath)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void removeSelectionPaths(TreePath[] paramArrayOfTreePath)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setSelectionMode(int paramInt)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setRowMapper(RowMapper paramRowMapper)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
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
/*      */   protected class TreeModelHandler
/*      */     implements TreeModelListener
/*      */   {
/*      */     protected TreeModelHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
/*      */     {
/* 3777 */       if (paramTreeModelEvent == null) {
/* 3778 */         return;
/*      */       }
/*      */ 
/* 3784 */       TreePath localTreePath = paramTreeModelEvent.getTreePath();
/*      */ 
/* 3786 */       if (localTreePath == null) {
/* 3787 */         return;
/*      */       }
/* 3789 */       if (localTreePath.getPathCount() == 1)
/*      */       {
/* 3791 */         JTree.this.clearToggledPaths();
/* 3792 */         if ((JTree.this.treeModel.getRoot() != null) && (!JTree.this.treeModel.isLeaf(JTree.this.treeModel.getRoot())))
/*      */         {
/* 3795 */           JTree.this.expandedState.put(localTreePath, Boolean.TRUE);
/*      */         }
/*      */       }
/* 3798 */       else if (JTree.this.expandedState.get(localTreePath) != null) {
/* 3799 */         Vector localVector = new Vector(1);
/* 3800 */         boolean bool = JTree.this.isExpanded(localTreePath);
/*      */ 
/* 3802 */         localVector.addElement(localTreePath);
/* 3803 */         JTree.this.removeDescendantToggledPaths(localVector.elements());
/* 3804 */         if (bool) {
/* 3805 */           TreeModel localTreeModel = JTree.this.getModel();
/*      */ 
/* 3807 */           if ((localTreeModel == null) || (localTreeModel.isLeaf(localTreePath.getLastPathComponent())))
/*      */           {
/* 3809 */             JTree.this.collapsePath(localTreePath);
/*      */           }
/* 3811 */           else JTree.this.expandedState.put(localTreePath, Boolean.TRUE);
/*      */         }
/*      */       }
/* 3814 */       JTree.this.removeDescendantSelectedPaths(localTreePath, false);
/*      */     }
/*      */ 
/*      */     public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent) {
/* 3818 */       if (paramTreeModelEvent == null) {
/* 3819 */         return;
/*      */       }
/* 3821 */       TreePath localTreePath1 = paramTreeModelEvent.getTreePath();
/* 3822 */       Object[] arrayOfObject = paramTreeModelEvent.getChildren();
/*      */ 
/* 3824 */       if (arrayOfObject == null) {
/* 3825 */         return;
/*      */       }
/*      */ 
/* 3828 */       Vector localVector = new Vector(Math.max(1, arrayOfObject.length));
/*      */ 
/* 3831 */       for (int i = arrayOfObject.length - 1; i >= 0; i--) {
/* 3832 */         TreePath localTreePath2 = localTreePath1.pathByAddingChild(arrayOfObject[i]);
/* 3833 */         if (JTree.this.expandedState.get(localTreePath2) != null)
/* 3834 */           localVector.addElement(localTreePath2);
/*      */       }
/* 3836 */       if (localVector.size() > 0) {
/* 3837 */         JTree.this.removeDescendantToggledPaths(localVector.elements());
/*      */       }
/* 3839 */       TreeModel localTreeModel = JTree.this.getModel();
/*      */ 
/* 3841 */       if ((localTreeModel == null) || (localTreeModel.isLeaf(localTreePath1.getLastPathComponent()))) {
/* 3842 */         JTree.this.expandedState.remove(localTreePath1);
/*      */       }
/* 3844 */       JTree.this.removeDescendantSelectedPaths(paramTreeModelEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class TreeSelectionRedirector
/*      */     implements Serializable, TreeSelectionListener
/*      */   {
/*      */     protected TreeSelectionRedirector()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
/*      */     {
/* 3377 */       TreeSelectionEvent localTreeSelectionEvent = (TreeSelectionEvent)paramTreeSelectionEvent.cloneWithSource(JTree.this);
/* 3378 */       JTree.this.fireValueChanged(localTreeSelectionEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TreeTimer extends Timer
/*      */   {
/*      */     public TreeTimer()
/*      */     {
/*  426 */       super(null);
/*  427 */       setRepeats(false);
/*      */     }
/*      */ 
/*      */     public void fireActionPerformed(ActionEvent paramActionEvent) {
/*  431 */       JTree.this.expandRow(JTree.this.expandRow);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.JTree
 * JD-Core Version:    0.6.2
 */