/*      */ package javax.swing;
/*      */ 
/*      */ import java.applet.Applet;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.KeyboardFocusManager;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterAbortException;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.text.DateFormat;
/*      */ import java.text.MessageFormat;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.EventObject;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.Accessible;
/*      */ import javax.accessibility.AccessibleAction;
/*      */ import javax.accessibility.AccessibleComponent;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.accessibility.AccessibleExtendedTable;
/*      */ import javax.accessibility.AccessibleRole;
/*      */ import javax.accessibility.AccessibleSelection;
/*      */ import javax.accessibility.AccessibleState;
/*      */ import javax.accessibility.AccessibleStateSet;
/*      */ import javax.accessibility.AccessibleTable;
/*      */ import javax.accessibility.AccessibleTableModelChange;
/*      */ import javax.accessibility.AccessibleText;
/*      */ import javax.accessibility.AccessibleValue;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.border.LineBorder;
/*      */ import javax.swing.event.CellEditorListener;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.event.RowSorterEvent;
/*      */ import javax.swing.event.RowSorterEvent.Type;
/*      */ import javax.swing.event.RowSorterListener;
/*      */ import javax.swing.event.TableColumnModelEvent;
/*      */ import javax.swing.event.TableColumnModelListener;
/*      */ import javax.swing.event.TableModelEvent;
/*      */ import javax.swing.event.TableModelListener;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.TableUI;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.table.AbstractTableModel;
/*      */ import javax.swing.table.DefaultTableCellRenderer.UIResource;
/*      */ import javax.swing.table.DefaultTableColumnModel;
/*      */ import javax.swing.table.DefaultTableModel;
/*      */ import javax.swing.table.JTableHeader;
/*      */ import javax.swing.table.TableCellEditor;
/*      */ import javax.swing.table.TableCellRenderer;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import javax.swing.table.TableModel;
/*      */ import javax.swing.table.TableRowSorter;
/*      */ import sun.swing.PrintingStatus;
/*      */ import sun.swing.SwingLazyValue;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.SwingUtilities2.Section;
/*      */ 
/*      */ public class JTable extends JComponent
/*      */   implements TableModelListener, Scrollable, TableColumnModelListener, ListSelectionListener, CellEditorListener, Accessible, RowSorterListener
/*      */ {
/*      */   private static final String uiClassID = "TableUI";
/*      */   public static final int AUTO_RESIZE_OFF = 0;
/*      */   public static final int AUTO_RESIZE_NEXT_COLUMN = 1;
/*      */   public static final int AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;
/*      */   public static final int AUTO_RESIZE_LAST_COLUMN = 3;
/*      */   public static final int AUTO_RESIZE_ALL_COLUMNS = 4;
/*      */   protected TableModel dataModel;
/*      */   protected TableColumnModel columnModel;
/*      */   protected ListSelectionModel selectionModel;
/*      */   protected JTableHeader tableHeader;
/*      */   protected int rowHeight;
/*      */   protected int rowMargin;
/*      */   protected Color gridColor;
/*      */   protected boolean showHorizontalLines;
/*      */   protected boolean showVerticalLines;
/*      */   protected int autoResizeMode;
/*      */   protected boolean autoCreateColumnsFromModel;
/*      */   protected Dimension preferredViewportSize;
/*      */   protected boolean rowSelectionAllowed;
/*      */   protected boolean cellSelectionEnabled;
/*      */   protected transient Component editorComp;
/*      */   protected transient TableCellEditor cellEditor;
/*      */   protected transient int editingColumn;
/*      */   protected transient int editingRow;
/*      */   protected transient Hashtable defaultRenderersByColumnClass;
/*      */   protected transient Hashtable defaultEditorsByColumnClass;
/*      */   protected Color selectionForeground;
/*      */   protected Color selectionBackground;
/*      */   private SizeSequence rowModel;
/*      */   private boolean dragEnabled;
/*      */   private boolean surrendersFocusOnKeystroke;
/*  385 */   private PropertyChangeListener editorRemover = null;
/*      */   private boolean columnSelectionAdjusting;
/*      */   private boolean rowSelectionAdjusting;
/*      */   private Throwable printError;
/*      */   private boolean isRowHeightSet;
/*      */   private boolean updateSelectionOnSort;
/*      */   private transient SortManager sortManager;
/*      */   private boolean ignoreSortChange;
/*      */   private boolean sorterChanged;
/*      */   private boolean autoCreateRowSorter;
/*      */   private boolean fillsViewportHeight;
/*  443 */   private DropMode dropMode = DropMode.USE_SELECTION;
/*      */   private transient DropLocation dropLocation;
/*      */ 
/*      */   public JTable()
/*      */   {
/*  561 */     this(null, null, null);
/*      */   }
/*      */ 
/*      */   public JTable(TableModel paramTableModel)
/*      */   {
/*  574 */     this(paramTableModel, null, null);
/*      */   }
/*      */ 
/*      */   public JTable(TableModel paramTableModel, TableColumnModel paramTableColumnModel)
/*      */   {
/*  587 */     this(paramTableModel, paramTableColumnModel, null);
/*      */   }
/*      */ 
/*      */   public JTable(TableModel paramTableModel, TableColumnModel paramTableColumnModel, ListSelectionModel paramListSelectionModel)
/*      */   {
/*  610 */     setLayout(null);
/*      */ 
/*  612 */     setFocusTraversalKeys(0, JComponent.getManagingFocusForwardTraversalKeys());
/*      */ 
/*  614 */     setFocusTraversalKeys(1, JComponent.getManagingFocusBackwardTraversalKeys());
/*      */ 
/*  616 */     if (paramTableColumnModel == null) {
/*  617 */       paramTableColumnModel = createDefaultColumnModel();
/*  618 */       this.autoCreateColumnsFromModel = true;
/*      */     }
/*  620 */     setColumnModel(paramTableColumnModel);
/*      */ 
/*  622 */     if (paramListSelectionModel == null) {
/*  623 */       paramListSelectionModel = createDefaultSelectionModel();
/*      */     }
/*  625 */     setSelectionModel(paramListSelectionModel);
/*      */ 
/*  630 */     if (paramTableModel == null) {
/*  631 */       paramTableModel = createDefaultDataModel();
/*      */     }
/*  633 */     setModel(paramTableModel);
/*      */ 
/*  635 */     initializeLocalVars();
/*  636 */     updateUI();
/*      */   }
/*      */ 
/*      */   public JTable(int paramInt1, int paramInt2)
/*      */   {
/*  650 */     this(new DefaultTableModel(paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */   public JTable(Vector paramVector1, Vector paramVector2)
/*      */   {
/*  668 */     this(new DefaultTableModel(paramVector1, paramVector2));
/*      */   }
/*      */ 
/*      */   public JTable(final Object[][] paramArrayOfObject, Object[] paramArrayOfObject1)
/*      */   {
/*  685 */     this(new AbstractTableModel() {
/*  686 */       public String getColumnName(int paramAnonymousInt) { return JTable.this[paramAnonymousInt].toString(); } 
/*  687 */       public int getRowCount() { return paramArrayOfObject.length; } 
/*  688 */       public int getColumnCount() { return JTable.this.length; } 
/*  689 */       public Object getValueAt(int paramAnonymousInt1, int paramAnonymousInt2) { return paramArrayOfObject[paramAnonymousInt1][paramAnonymousInt2]; } 
/*  690 */       public boolean isCellEditable(int paramAnonymousInt1, int paramAnonymousInt2) { return true; } 
/*      */       public void setValueAt(Object paramAnonymousObject, int paramAnonymousInt1, int paramAnonymousInt2) {
/*  692 */         paramArrayOfObject[paramAnonymousInt1][paramAnonymousInt2] = paramAnonymousObject;
/*  693 */         fireTableCellUpdated(paramAnonymousInt1, paramAnonymousInt2);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void addNotify()
/*      */   {
/*  704 */     super.addNotify();
/*  705 */     configureEnclosingScrollPane();
/*      */   }
/*      */ 
/*      */   protected void configureEnclosingScrollPane()
/*      */   {
/*  722 */     Container localContainer1 = SwingUtilities.getUnwrappedParent(this);
/*  723 */     if ((localContainer1 instanceof JViewport)) {
/*  724 */       JViewport localJViewport1 = (JViewport)localContainer1;
/*  725 */       Container localContainer2 = localJViewport1.getParent();
/*  726 */       if ((localContainer2 instanceof JScrollPane)) {
/*  727 */         JScrollPane localJScrollPane = (JScrollPane)localContainer2;
/*      */ 
/*  731 */         JViewport localJViewport2 = localJScrollPane.getViewport();
/*  732 */         if ((localJViewport2 == null) || (SwingUtilities.getUnwrappedView(localJViewport2) != this))
/*      */         {
/*  734 */           return;
/*      */         }
/*  736 */         localJScrollPane.setColumnHeaderView(getTableHeader());
/*      */ 
/*  738 */         configureEnclosingScrollPaneUI();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void configureEnclosingScrollPaneUI()
/*      */   {
/*  756 */     Container localContainer1 = SwingUtilities.getUnwrappedParent(this);
/*  757 */     if ((localContainer1 instanceof JViewport)) {
/*  758 */       JViewport localJViewport1 = (JViewport)localContainer1;
/*  759 */       Container localContainer2 = localJViewport1.getParent();
/*  760 */       if ((localContainer2 instanceof JScrollPane)) {
/*  761 */         JScrollPane localJScrollPane = (JScrollPane)localContainer2;
/*      */ 
/*  765 */         JViewport localJViewport2 = localJScrollPane.getViewport();
/*  766 */         if ((localJViewport2 == null) || (SwingUtilities.getUnwrappedView(localJViewport2) != this))
/*      */         {
/*  768 */           return;
/*      */         }
/*      */ 
/*  771 */         Border localBorder = localJScrollPane.getBorder();
/*  772 */         if ((localBorder == null) || ((localBorder instanceof UIResource))) {
/*  773 */           localObject = UIManager.getBorder("Table.scrollPaneBorder");
/*      */ 
/*  775 */           if (localObject != null) {
/*  776 */             localJScrollPane.setBorder((Border)localObject);
/*      */           }
/*      */         }
/*      */ 
/*  780 */         Object localObject = localJScrollPane.getCorner("UPPER_TRAILING_CORNER");
/*      */ 
/*  782 */         if ((localObject == null) || ((localObject instanceof UIResource))) {
/*  783 */           localObject = null;
/*      */           try {
/*  785 */             localObject = (Component)UIManager.get("Table.scrollPaneCornerComponent");
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/*      */           }
/*  790 */           localJScrollPane.setCorner("UPPER_TRAILING_CORNER", (Component)localObject);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeNotify()
/*      */   {
/*  803 */     KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
/*      */ 
/*  805 */     this.editorRemover = null;
/*  806 */     unconfigureEnclosingScrollPane();
/*  807 */     super.removeNotify();
/*      */   }
/*      */ 
/*      */   protected void unconfigureEnclosingScrollPane()
/*      */   {
/*  823 */     Container localContainer1 = SwingUtilities.getUnwrappedParent(this);
/*  824 */     if ((localContainer1 instanceof JViewport)) {
/*  825 */       JViewport localJViewport1 = (JViewport)localContainer1;
/*  826 */       Container localContainer2 = localJViewport1.getParent();
/*  827 */       if ((localContainer2 instanceof JScrollPane)) {
/*  828 */         JScrollPane localJScrollPane = (JScrollPane)localContainer2;
/*      */ 
/*  832 */         JViewport localJViewport2 = localJScrollPane.getViewport();
/*  833 */         if ((localJViewport2 == null) || (SwingUtilities.getUnwrappedView(localJViewport2) != this))
/*      */         {
/*  835 */           return;
/*      */         }
/*  837 */         localJScrollPane.setColumnHeaderView(null);
/*      */ 
/*  839 */         Component localComponent = localJScrollPane.getCorner("UPPER_TRAILING_CORNER");
/*      */ 
/*  841 */         if ((localComponent instanceof UIResource))
/*  842 */           localJScrollPane.setCorner("UPPER_TRAILING_CORNER", null);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void setUIProperty(String paramString, Object paramObject)
/*      */   {
/*  850 */     if (paramString == "rowHeight") {
/*  851 */       if (!this.isRowHeightSet) {
/*  852 */         setRowHeight(((Number)paramObject).intValue());
/*  853 */         this.isRowHeightSet = false;
/*      */       }
/*  855 */       return;
/*      */     }
/*  857 */     super.setUIProperty(paramString, paramObject);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static JScrollPane createScrollPaneForTable(JTable paramJTable)
/*      */   {
/*  872 */     return new JScrollPane(paramJTable);
/*      */   }
/*      */ 
/*      */   public void setTableHeader(JTableHeader paramJTableHeader)
/*      */   {
/*  890 */     if (this.tableHeader != paramJTableHeader) {
/*  891 */       JTableHeader localJTableHeader = this.tableHeader;
/*      */ 
/*  893 */       if (localJTableHeader != null) {
/*  894 */         localJTableHeader.setTable(null);
/*      */       }
/*  896 */       this.tableHeader = paramJTableHeader;
/*  897 */       if (paramJTableHeader != null) {
/*  898 */         paramJTableHeader.setTable(this);
/*      */       }
/*  900 */       firePropertyChange("tableHeader", localJTableHeader, paramJTableHeader);
/*      */     }
/*      */   }
/*      */ 
/*      */   public JTableHeader getTableHeader()
/*      */   {
/*  911 */     return this.tableHeader;
/*      */   }
/*      */ 
/*      */   public void setRowHeight(int paramInt)
/*      */   {
/*  929 */     if (paramInt <= 0) {
/*  930 */       throw new IllegalArgumentException("New row height less than 1");
/*      */     }
/*  932 */     int i = this.rowHeight;
/*  933 */     this.rowHeight = paramInt;
/*  934 */     this.rowModel = null;
/*  935 */     if (this.sortManager != null) {
/*  936 */       this.sortManager.modelRowSizes = null;
/*      */     }
/*  938 */     this.isRowHeightSet = true;
/*  939 */     resizeAndRepaint();
/*  940 */     firePropertyChange("rowHeight", i, paramInt);
/*      */   }
/*      */ 
/*      */   public int getRowHeight()
/*      */   {
/*  950 */     return this.rowHeight;
/*      */   }
/*      */ 
/*      */   private SizeSequence getRowModel() {
/*  954 */     if (this.rowModel == null) {
/*  955 */       this.rowModel = new SizeSequence(getRowCount(), getRowHeight());
/*      */     }
/*  957 */     return this.rowModel;
/*      */   }
/*      */ 
/*      */   public void setRowHeight(int paramInt1, int paramInt2)
/*      */   {
/*  976 */     if (paramInt2 <= 0) {
/*  977 */       throw new IllegalArgumentException("New row height less than 1");
/*      */     }
/*  979 */     getRowModel().setSize(paramInt1, paramInt2);
/*  980 */     if (this.sortManager != null) {
/*  981 */       this.sortManager.setViewRowHeight(paramInt1, paramInt2);
/*      */     }
/*  983 */     resizeAndRepaint();
/*      */   }
/*      */ 
/*      */   public int getRowHeight(int paramInt)
/*      */   {
/*  993 */     return this.rowModel == null ? getRowHeight() : this.rowModel.getSize(paramInt);
/*      */   }
/*      */ 
/*      */   public void setRowMargin(int paramInt)
/*      */   {
/* 1006 */     int i = this.rowMargin;
/* 1007 */     this.rowMargin = paramInt;
/* 1008 */     resizeAndRepaint();
/* 1009 */     firePropertyChange("rowMargin", i, paramInt);
/*      */   }
/*      */ 
/*      */   public int getRowMargin()
/*      */   {
/* 1020 */     return this.rowMargin;
/*      */   }
/*      */ 
/*      */   public void setIntercellSpacing(Dimension paramDimension)
/*      */   {
/* 1038 */     setRowMargin(paramDimension.height);
/* 1039 */     getColumnModel().setColumnMargin(paramDimension.width);
/*      */ 
/* 1041 */     resizeAndRepaint();
/*      */   }
/*      */ 
/*      */   public Dimension getIntercellSpacing()
/*      */   {
/* 1052 */     return new Dimension(getColumnModel().getColumnMargin(), this.rowMargin);
/*      */   }
/*      */ 
/*      */   public void setGridColor(Color paramColor)
/*      */   {
/* 1067 */     if (paramColor == null) {
/* 1068 */       throw new IllegalArgumentException("New color is null");
/*      */     }
/* 1070 */     Color localColor = this.gridColor;
/* 1071 */     this.gridColor = paramColor;
/* 1072 */     firePropertyChange("gridColor", localColor, paramColor);
/*      */ 
/* 1074 */     repaint();
/*      */   }
/*      */ 
/*      */   public Color getGridColor()
/*      */   {
/* 1085 */     return this.gridColor;
/*      */   }
/*      */ 
/*      */   public void setShowGrid(boolean paramBoolean)
/*      */   {
/* 1103 */     setShowHorizontalLines(paramBoolean);
/* 1104 */     setShowVerticalLines(paramBoolean);
/*      */ 
/* 1107 */     repaint();
/*      */   }
/*      */ 
/*      */   public void setShowHorizontalLines(boolean paramBoolean)
/*      */   {
/* 1123 */     boolean bool = this.showHorizontalLines;
/* 1124 */     this.showHorizontalLines = paramBoolean;
/* 1125 */     firePropertyChange("showHorizontalLines", bool, paramBoolean);
/*      */ 
/* 1128 */     repaint();
/*      */   }
/*      */ 
/*      */   public void setShowVerticalLines(boolean paramBoolean)
/*      */   {
/* 1144 */     boolean bool = this.showVerticalLines;
/* 1145 */     this.showVerticalLines = paramBoolean;
/* 1146 */     firePropertyChange("showVerticalLines", bool, paramBoolean);
/*      */ 
/* 1148 */     repaint();
/*      */   }
/*      */ 
/*      */   public boolean getShowHorizontalLines()
/*      */   {
/* 1160 */     return this.showHorizontalLines;
/*      */   }
/*      */ 
/*      */   public boolean getShowVerticalLines()
/*      */   {
/* 1172 */     return this.showVerticalLines;
/*      */   }
/*      */ 
/*      */   public void setAutoResizeMode(int paramInt)
/*      */   {
/* 1199 */     if ((paramInt == 0) || (paramInt == 1) || (paramInt == 2) || (paramInt == 3) || (paramInt == 4))
/*      */     {
/* 1204 */       int i = this.autoResizeMode;
/* 1205 */       this.autoResizeMode = paramInt;
/* 1206 */       resizeAndRepaint();
/* 1207 */       if (this.tableHeader != null) {
/* 1208 */         this.tableHeader.resizeAndRepaint();
/*      */       }
/* 1210 */       firePropertyChange("autoResizeMode", i, this.autoResizeMode);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getAutoResizeMode()
/*      */   {
/* 1224 */     return this.autoResizeMode;
/*      */   }
/*      */ 
/*      */   public void setAutoCreateColumnsFromModel(boolean paramBoolean)
/*      */   {
/* 1240 */     if (this.autoCreateColumnsFromModel != paramBoolean) {
/* 1241 */       boolean bool = this.autoCreateColumnsFromModel;
/* 1242 */       this.autoCreateColumnsFromModel = paramBoolean;
/* 1243 */       if (paramBoolean) {
/* 1244 */         createDefaultColumnsFromModel();
/*      */       }
/* 1246 */       firePropertyChange("autoCreateColumnsFromModel", bool, paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getAutoCreateColumnsFromModel()
/*      */   {
/* 1263 */     return this.autoCreateColumnsFromModel;
/*      */   }
/*      */ 
/*      */   public void createDefaultColumnsFromModel()
/*      */   {
/* 1277 */     TableModel localTableModel = getModel();
/* 1278 */     if (localTableModel != null)
/*      */     {
/* 1280 */       TableColumnModel localTableColumnModel = getColumnModel();
/* 1281 */       while (localTableColumnModel.getColumnCount() > 0) {
/* 1282 */         localTableColumnModel.removeColumn(localTableColumnModel.getColumn(0));
/*      */       }
/*      */ 
/* 1286 */       for (int i = 0; i < localTableModel.getColumnCount(); i++) {
/* 1287 */         TableColumn localTableColumn = new TableColumn(i);
/* 1288 */         addColumn(localTableColumn);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDefaultRenderer(Class<?> paramClass, TableCellRenderer paramTableCellRenderer)
/*      */   {
/* 1305 */     if (paramTableCellRenderer != null) {
/* 1306 */       this.defaultRenderersByColumnClass.put(paramClass, paramTableCellRenderer);
/*      */     }
/*      */     else
/* 1309 */       this.defaultRenderersByColumnClass.remove(paramClass);
/*      */   }
/*      */ 
/*      */   public TableCellRenderer getDefaultRenderer(Class<?> paramClass)
/*      */   {
/* 1329 */     if (paramClass == null) {
/* 1330 */       return null;
/*      */     }
/*      */ 
/* 1333 */     Object localObject1 = this.defaultRenderersByColumnClass.get(paramClass);
/* 1334 */     if (localObject1 != null) {
/* 1335 */       return (TableCellRenderer)localObject1;
/*      */     }
/*      */ 
/* 1338 */     Object localObject2 = paramClass.getSuperclass();
/* 1339 */     if ((localObject2 == null) && (paramClass != Object.class)) {
/* 1340 */       localObject2 = Object.class;
/*      */     }
/* 1342 */     return getDefaultRenderer((Class)localObject2);
/*      */   }
/*      */ 
/*      */   public void setDefaultEditor(Class<?> paramClass, TableCellEditor paramTableCellEditor)
/*      */   {
/* 1363 */     if (paramTableCellEditor != null) {
/* 1364 */       this.defaultEditorsByColumnClass.put(paramClass, paramTableCellEditor);
/*      */     }
/*      */     else
/* 1367 */       this.defaultEditorsByColumnClass.remove(paramClass);
/*      */   }
/*      */ 
/*      */   public TableCellEditor getDefaultEditor(Class<?> paramClass)
/*      */   {
/* 1386 */     if (paramClass == null) {
/* 1387 */       return null;
/*      */     }
/*      */ 
/* 1390 */     Object localObject = this.defaultEditorsByColumnClass.get(paramClass);
/* 1391 */     if (localObject != null) {
/* 1392 */       return (TableCellEditor)localObject;
/*      */     }
/*      */ 
/* 1395 */     return getDefaultEditor(paramClass.getSuperclass());
/*      */   }
/*      */ 
/*      */   public void setDragEnabled(boolean paramBoolean)
/*      */   {
/* 1435 */     if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
/* 1436 */       throw new HeadlessException();
/*      */     }
/* 1438 */     this.dragEnabled = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getDragEnabled()
/*      */   {
/* 1449 */     return this.dragEnabled;
/*      */   }
/*      */ 
/*      */   public final void setDropMode(DropMode paramDropMode)
/*      */   {
/* 1485 */     if (paramDropMode != null) {
/* 1486 */       switch (7.$SwitchMap$javax$swing$DropMode[paramDropMode.ordinal()]) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/* 1495 */         this.dropMode = paramDropMode;
/* 1496 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1500 */     throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for table");
/*      */   }
/*      */ 
/*      */   public final DropMode getDropMode()
/*      */   {
/* 1511 */     return this.dropMode;
/*      */   }
/*      */ 
/*      */   DropLocation dropLocationForPoint(Point paramPoint)
/*      */   {
/* 1522 */     DropLocation localDropLocation = null;
/*      */ 
/* 1524 */     int i = rowAtPoint(paramPoint);
/* 1525 */     int j = columnAtPoint(paramPoint);
/* 1526 */     int k = (Boolean.TRUE == getClientProperty("Table.isFileList")) && (SwingUtilities2.pointOutsidePrefSize(this, i, j, paramPoint)) ? 1 : 0;
/*      */ 
/* 1529 */     Rectangle localRectangle = getCellRect(i, j, true);
/*      */ 
/* 1531 */     boolean bool1 = false;
/* 1532 */     boolean bool2 = getComponentOrientation().isLeftToRight();
/*      */     SwingUtilities2.Section localSection1;
/*      */     SwingUtilities2.Section localSection2;
/* 1534 */     switch (7.$SwitchMap$javax$swing$DropMode[this.dropMode.ordinal()]) {
/*      */     case 1:
/*      */     case 2:
/* 1537 */       if ((i == -1) || (j == -1) || (k != 0))
/* 1538 */         localDropLocation = new DropLocation(paramPoint, -1, -1, false, false, null);
/*      */       else {
/* 1540 */         localDropLocation = new DropLocation(paramPoint, i, j, false, false, null);
/*      */       }
/* 1542 */       break;
/*      */     case 3:
/* 1544 */       if ((i == -1) && (j == -1)) {
/* 1545 */         localDropLocation = new DropLocation(paramPoint, 0, 0, true, true, null);
/*      */       }
/*      */       else
/*      */       {
/* 1549 */         localSection1 = SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool2, true);
/*      */ 
/* 1551 */         if (i == -1) {
/* 1552 */           if (localSection1 == SwingUtilities2.Section.LEADING)
/* 1553 */             localDropLocation = new DropLocation(paramPoint, getRowCount(), j, true, true, null);
/* 1554 */           else if (localSection1 == SwingUtilities2.Section.TRAILING)
/* 1555 */             localDropLocation = new DropLocation(paramPoint, getRowCount(), j + 1, true, true, null);
/*      */           else
/* 1557 */             localDropLocation = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
/*      */         }
/* 1559 */         else if ((localSection1 == SwingUtilities2.Section.LEADING) || (localSection1 == SwingUtilities2.Section.TRAILING)) {
/* 1560 */           localSection2 = SwingUtilities2.liesInVertical(localRectangle, paramPoint, true);
/* 1561 */           if (localSection2 == SwingUtilities2.Section.LEADING) {
/* 1562 */             bool1 = true;
/* 1563 */           } else if (localSection2 == SwingUtilities2.Section.TRAILING) {
/* 1564 */             i++;
/* 1565 */             bool1 = true;
/*      */           }
/*      */ 
/* 1568 */           localDropLocation = new DropLocation(paramPoint, i, localSection1 == SwingUtilities2.Section.TRAILING ? j + 1 : j, bool1, true, null);
/*      */         }
/*      */         else
/*      */         {
/* 1572 */           if (SwingUtilities2.liesInVertical(localRectangle, paramPoint, false) == SwingUtilities2.Section.TRAILING) {
/* 1573 */             i++;
/*      */           }
/*      */ 
/* 1576 */           localDropLocation = new DropLocation(paramPoint, i, j, true, false, null);
/*      */         }
/*      */       }
/* 1579 */       break;
/*      */     case 4:
/* 1581 */       if ((i == -1) && (j == -1)) {
/* 1582 */         localDropLocation = new DropLocation(paramPoint, -1, -1, false, false, null);
/*      */       }
/* 1586 */       else if (i == -1) {
/* 1587 */         localDropLocation = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
/*      */       }
/*      */       else
/*      */       {
/* 1591 */         if (SwingUtilities2.liesInVertical(localRectangle, paramPoint, false) == SwingUtilities2.Section.TRAILING) {
/* 1592 */           i++;
/*      */         }
/*      */ 
/* 1595 */         localDropLocation = new DropLocation(paramPoint, i, j, true, false, null);
/* 1596 */       }break;
/*      */     case 7:
/* 1598 */       if ((i == -1) && (j == -1)) {
/* 1599 */         localDropLocation = new DropLocation(paramPoint, -1, -1, false, false, null);
/*      */       }
/* 1603 */       else if (i == -1) {
/* 1604 */         localDropLocation = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
/*      */       }
/*      */       else
/*      */       {
/* 1608 */         localSection2 = SwingUtilities2.liesInVertical(localRectangle, paramPoint, true);
/* 1609 */         if (localSection2 == SwingUtilities2.Section.LEADING) {
/* 1610 */           bool1 = true;
/* 1611 */         } else if (localSection2 == SwingUtilities2.Section.TRAILING) {
/* 1612 */           i++;
/* 1613 */           bool1 = true;
/*      */         }
/*      */ 
/* 1616 */         localDropLocation = new DropLocation(paramPoint, i, j, bool1, false, null);
/* 1617 */       }break;
/*      */     case 5:
/* 1619 */       if (i == -1) {
/* 1620 */         localDropLocation = new DropLocation(paramPoint, -1, -1, false, false, null);
/*      */       }
/* 1624 */       else if (j == -1) {
/* 1625 */         localDropLocation = new DropLocation(paramPoint, getColumnCount(), j, false, true, null);
/*      */       }
/*      */       else
/*      */       {
/* 1629 */         if (SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool2, false) == SwingUtilities2.Section.TRAILING) {
/* 1630 */           j++;
/*      */         }
/*      */ 
/* 1633 */         localDropLocation = new DropLocation(paramPoint, i, j, false, true, null);
/* 1634 */       }break;
/*      */     case 8:
/* 1636 */       if (i == -1) {
/* 1637 */         localDropLocation = new DropLocation(paramPoint, -1, -1, false, false, null);
/*      */       }
/* 1641 */       else if (j == -1) {
/* 1642 */         localDropLocation = new DropLocation(paramPoint, i, getColumnCount(), false, true, null);
/*      */       }
/*      */       else
/*      */       {
/* 1646 */         localSection1 = SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool2, true);
/* 1647 */         if (localSection1 == SwingUtilities2.Section.LEADING) {
/* 1648 */           bool1 = true;
/* 1649 */         } else if (localSection1 == SwingUtilities2.Section.TRAILING) {
/* 1650 */           j++;
/* 1651 */           bool1 = true;
/*      */         }
/*      */ 
/* 1654 */         localDropLocation = new DropLocation(paramPoint, i, j, false, bool1, null);
/* 1655 */       }break;
/*      */     case 6:
/* 1657 */       if ((i == -1) && (j == -1)) {
/* 1658 */         localDropLocation = new DropLocation(paramPoint, 0, 0, true, true, null);
/*      */       }
/*      */       else
/*      */       {
/* 1662 */         localSection1 = SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool2, true);
/*      */ 
/* 1664 */         if (i == -1) {
/* 1665 */           if (localSection1 == SwingUtilities2.Section.LEADING)
/* 1666 */             localDropLocation = new DropLocation(paramPoint, getRowCount(), j, true, true, null);
/* 1667 */           else if (localSection1 == SwingUtilities2.Section.TRAILING)
/* 1668 */             localDropLocation = new DropLocation(paramPoint, getRowCount(), j + 1, true, true, null);
/*      */           else {
/* 1670 */             localDropLocation = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1676 */           localSection2 = SwingUtilities2.liesInVertical(localRectangle, paramPoint, true);
/* 1677 */           if (localSection2 == SwingUtilities2.Section.LEADING) {
/* 1678 */             bool1 = true;
/* 1679 */           } else if (localSection2 == SwingUtilities2.Section.TRAILING) {
/* 1680 */             i++;
/* 1681 */             bool1 = true;
/*      */           }
/*      */ 
/* 1684 */           localDropLocation = new DropLocation(paramPoint, i, localSection1 == SwingUtilities2.Section.TRAILING ? j + 1 : j, bool1, localSection1 != SwingUtilities2.Section.MIDDLE, null);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1689 */       break;
/*      */     default:
/* 1691 */       if (!$assertionsDisabled) throw new AssertionError("Unexpected drop mode");
/*      */       break;
/*      */     }
/* 1694 */     return localDropLocation;
/*      */   }
/*      */ 
/*      */   Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean)
/*      */   {
/* 1734 */     Object localObject1 = null;
/* 1735 */     DropLocation localDropLocation = (DropLocation)paramDropLocation;
/*      */ 
/* 1737 */     if (this.dropMode == DropMode.USE_SELECTION) {
/* 1738 */       if (localDropLocation == null) {
/* 1739 */         if ((!paramBoolean) && (paramObject != null)) {
/* 1740 */           clearSelection();
/*      */ 
/* 1742 */           localObject2 = ((int[][])(int[][])paramObject)[0];
/* 1743 */           int[] arrayOfInt1 = ((int[][])(int[][])paramObject)[1];
/* 1744 */           int[] arrayOfInt2 = ((int[][])(int[][])paramObject)[2];
/*      */           int k;
/* 1746 */           for (k : localObject2) {
/* 1747 */             addRowSelectionInterval(k, k);
/*      */           }
/*      */ 
/* 1750 */           for (k : arrayOfInt1) {
/* 1751 */             addColumnSelectionInterval(k, k);
/*      */           }
/*      */ 
/* 1754 */           SwingUtilities2.setLeadAnchorWithoutSelection(getSelectionModel(), arrayOfInt2[1], arrayOfInt2[0]);
/*      */ 
/* 1757 */           SwingUtilities2.setLeadAnchorWithoutSelection(getColumnModel().getSelectionModel(), arrayOfInt2[3], arrayOfInt2[2]);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1762 */         if (this.dropLocation == null) {
/* 1763 */           localObject1 = new int[][] { getSelectedRows(), getSelectedColumns(), { getAdjustedIndex(getSelectionModel().getAnchorSelectionIndex(), true), getAdjustedIndex(getSelectionModel().getLeadSelectionIndex(), true), getAdjustedIndex(getColumnModel().getSelectionModel().getAnchorSelectionIndex(), false), getAdjustedIndex(getColumnModel().getSelectionModel().getLeadSelectionIndex(), false) } };
/*      */         }
/*      */         else
/*      */         {
/* 1775 */           localObject1 = paramObject;
/*      */         }
/*      */ 
/* 1778 */         if (localDropLocation.getRow() == -1) {
/* 1779 */           clearSelectionAndLeadAnchor();
/*      */         } else {
/* 1781 */           setRowSelectionInterval(localDropLocation.getRow(), localDropLocation.getRow());
/*      */ 
/* 1783 */           setColumnSelectionInterval(localDropLocation.getColumn(), localDropLocation.getColumn());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1789 */     Object localObject2 = this.dropLocation;
/* 1790 */     this.dropLocation = localDropLocation;
/* 1791 */     firePropertyChange("dropLocation", localObject2, this.dropLocation);
/*      */ 
/* 1793 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public final DropLocation getDropLocation()
/*      */   {
/* 1815 */     return this.dropLocation;
/*      */   }
/*      */ 
/*      */   public void setAutoCreateRowSorter(boolean paramBoolean)
/*      */   {
/* 1838 */     boolean bool = this.autoCreateRowSorter;
/* 1839 */     this.autoCreateRowSorter = paramBoolean;
/* 1840 */     if (paramBoolean) {
/* 1841 */       setRowSorter(new TableRowSorter(getModel()));
/*      */     }
/* 1843 */     firePropertyChange("autoCreateRowSorter", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getAutoCreateRowSorter()
/*      */   {
/* 1857 */     return this.autoCreateRowSorter;
/*      */   }
/*      */ 
/*      */   public void setUpdateSelectionOnSort(boolean paramBoolean)
/*      */   {
/* 1874 */     if (this.updateSelectionOnSort != paramBoolean) {
/* 1875 */       this.updateSelectionOnSort = paramBoolean;
/* 1876 */       firePropertyChange("updateSelectionOnSort", !paramBoolean, paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getUpdateSelectionOnSort()
/*      */   {
/* 1887 */     return this.updateSelectionOnSort;
/*      */   }
/*      */ 
/*      */   public void setRowSorter(RowSorter<? extends TableModel> paramRowSorter)
/*      */   {
/* 1913 */     RowSorter localRowSorter = null;
/* 1914 */     if (this.sortManager != null) {
/* 1915 */       localRowSorter = this.sortManager.sorter;
/* 1916 */       this.sortManager.dispose();
/* 1917 */       this.sortManager = null;
/*      */     }
/* 1919 */     this.rowModel = null;
/* 1920 */     clearSelectionAndLeadAnchor();
/* 1921 */     if (paramRowSorter != null) {
/* 1922 */       this.sortManager = new SortManager(paramRowSorter);
/*      */     }
/* 1924 */     resizeAndRepaint();
/* 1925 */     firePropertyChange("rowSorter", localRowSorter, paramRowSorter);
/* 1926 */     firePropertyChange("sorter", localRowSorter, paramRowSorter);
/*      */   }
/*      */ 
/*      */   public RowSorter<? extends TableModel> getRowSorter()
/*      */   {
/* 1936 */     return this.sortManager != null ? this.sortManager.sorter : null;
/*      */   }
/*      */ 
/*      */   public void setSelectionMode(int paramInt)
/*      */   {
/* 1969 */     clearSelection();
/* 1970 */     getSelectionModel().setSelectionMode(paramInt);
/* 1971 */     getColumnModel().getSelectionModel().setSelectionMode(paramInt);
/*      */   }
/*      */ 
/*      */   public void setRowSelectionAllowed(boolean paramBoolean)
/*      */   {
/* 1985 */     boolean bool = this.rowSelectionAllowed;
/* 1986 */     this.rowSelectionAllowed = paramBoolean;
/* 1987 */     if (bool != paramBoolean) {
/* 1988 */       repaint();
/*      */     }
/* 1990 */     firePropertyChange("rowSelectionAllowed", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getRowSelectionAllowed()
/*      */   {
/* 2000 */     return this.rowSelectionAllowed;
/*      */   }
/*      */ 
/*      */   public void setColumnSelectionAllowed(boolean paramBoolean)
/*      */   {
/* 2014 */     boolean bool = this.columnModel.getColumnSelectionAllowed();
/* 2015 */     this.columnModel.setColumnSelectionAllowed(paramBoolean);
/* 2016 */     if (bool != paramBoolean) {
/* 2017 */       repaint();
/*      */     }
/* 2019 */     firePropertyChange("columnSelectionAllowed", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getColumnSelectionAllowed()
/*      */   {
/* 2029 */     return this.columnModel.getColumnSelectionAllowed();
/*      */   }
/*      */ 
/*      */   public void setCellSelectionEnabled(boolean paramBoolean)
/*      */   {
/* 2053 */     setRowSelectionAllowed(paramBoolean);
/* 2054 */     setColumnSelectionAllowed(paramBoolean);
/* 2055 */     boolean bool = this.cellSelectionEnabled;
/* 2056 */     this.cellSelectionEnabled = paramBoolean;
/* 2057 */     firePropertyChange("cellSelectionEnabled", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getCellSelectionEnabled()
/*      */   {
/* 2070 */     return (getRowSelectionAllowed()) && (getColumnSelectionAllowed());
/*      */   }
/*      */ 
/*      */   public void selectAll()
/*      */   {
/* 2078 */     if (isEditing()) {
/* 2079 */       removeEditor();
/*      */     }
/* 2081 */     if ((getRowCount() > 0) && (getColumnCount() > 0))
/*      */     {
/* 2086 */       ListSelectionModel localListSelectionModel = this.selectionModel;
/* 2087 */       localListSelectionModel.setValueIsAdjusting(true);
/* 2088 */       int i = getAdjustedIndex(localListSelectionModel.getLeadSelectionIndex(), true);
/* 2089 */       int j = getAdjustedIndex(localListSelectionModel.getAnchorSelectionIndex(), true);
/*      */ 
/* 2091 */       setRowSelectionInterval(0, getRowCount() - 1);
/*      */ 
/* 2094 */       SwingUtilities2.setLeadAnchorWithoutSelection(localListSelectionModel, i, j);
/*      */ 
/* 2096 */       localListSelectionModel.setValueIsAdjusting(false);
/*      */ 
/* 2098 */       localListSelectionModel = this.columnModel.getSelectionModel();
/* 2099 */       localListSelectionModel.setValueIsAdjusting(true);
/* 2100 */       i = getAdjustedIndex(localListSelectionModel.getLeadSelectionIndex(), false);
/* 2101 */       j = getAdjustedIndex(localListSelectionModel.getAnchorSelectionIndex(), false);
/*      */ 
/* 2103 */       setColumnSelectionInterval(0, getColumnCount() - 1);
/*      */ 
/* 2106 */       SwingUtilities2.setLeadAnchorWithoutSelection(localListSelectionModel, i, j);
/*      */ 
/* 2108 */       localListSelectionModel.setValueIsAdjusting(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearSelection()
/*      */   {
/* 2116 */     this.selectionModel.clearSelection();
/* 2117 */     this.columnModel.getSelectionModel().clearSelection();
/*      */   }
/*      */ 
/*      */   private void clearSelectionAndLeadAnchor() {
/* 2121 */     this.selectionModel.setValueIsAdjusting(true);
/* 2122 */     this.columnModel.getSelectionModel().setValueIsAdjusting(true);
/*      */ 
/* 2124 */     clearSelection();
/*      */ 
/* 2126 */     this.selectionModel.setAnchorSelectionIndex(-1);
/* 2127 */     this.selectionModel.setLeadSelectionIndex(-1);
/* 2128 */     this.columnModel.getSelectionModel().setAnchorSelectionIndex(-1);
/* 2129 */     this.columnModel.getSelectionModel().setLeadSelectionIndex(-1);
/*      */ 
/* 2131 */     this.selectionModel.setValueIsAdjusting(false);
/* 2132 */     this.columnModel.getSelectionModel().setValueIsAdjusting(false);
/*      */   }
/*      */ 
/*      */   private int getAdjustedIndex(int paramInt, boolean paramBoolean) {
/* 2136 */     int i = paramBoolean ? getRowCount() : getColumnCount();
/* 2137 */     return paramInt < i ? paramInt : -1;
/*      */   }
/*      */ 
/*      */   private int boundRow(int paramInt) throws IllegalArgumentException {
/* 2141 */     if ((paramInt < 0) || (paramInt >= getRowCount())) {
/* 2142 */       throw new IllegalArgumentException("Row index out of range");
/*      */     }
/* 2144 */     return paramInt;
/*      */   }
/*      */ 
/*      */   private int boundColumn(int paramInt) {
/* 2148 */     if ((paramInt < 0) || (paramInt >= getColumnCount())) {
/* 2149 */       throw new IllegalArgumentException("Column index out of range");
/*      */     }
/* 2151 */     return paramInt;
/*      */   }
/*      */ 
/*      */   public void setRowSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2165 */     this.selectionModel.setSelectionInterval(boundRow(paramInt1), boundRow(paramInt2));
/*      */   }
/*      */ 
/*      */   public void setColumnSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2179 */     this.columnModel.getSelectionModel().setSelectionInterval(boundColumn(paramInt1), boundColumn(paramInt2));
/*      */   }
/*      */ 
/*      */   public void addRowSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2192 */     this.selectionModel.addSelectionInterval(boundRow(paramInt1), boundRow(paramInt2));
/*      */   }
/*      */ 
/*      */   public void addColumnSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2206 */     this.columnModel.getSelectionModel().addSelectionInterval(boundColumn(paramInt1), boundColumn(paramInt2));
/*      */   }
/*      */ 
/*      */   public void removeRowSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2219 */     this.selectionModel.removeSelectionInterval(boundRow(paramInt1), boundRow(paramInt2));
/*      */   }
/*      */ 
/*      */   public void removeColumnSelectionInterval(int paramInt1, int paramInt2)
/*      */   {
/* 2232 */     this.columnModel.getSelectionModel().removeSelectionInterval(boundColumn(paramInt1), boundColumn(paramInt2));
/*      */   }
/*      */ 
/*      */   public int getSelectedRow()
/*      */   {
/* 2240 */     return this.selectionModel.getMinSelectionIndex();
/*      */   }
/*      */ 
/*      */   public int getSelectedColumn()
/*      */   {
/* 2249 */     return this.columnModel.getSelectionModel().getMinSelectionIndex();
/*      */   }
/*      */ 
/*      */   public int[] getSelectedRows()
/*      */   {
/* 2260 */     int i = this.selectionModel.getMinSelectionIndex();
/* 2261 */     int j = this.selectionModel.getMaxSelectionIndex();
/*      */ 
/* 2263 */     if ((i == -1) || (j == -1)) {
/* 2264 */       return new int[0];
/*      */     }
/*      */ 
/* 2267 */     int[] arrayOfInt1 = new int[1 + (j - i)];
/* 2268 */     int k = 0;
/* 2269 */     for (int m = i; m <= j; m++) {
/* 2270 */       if (this.selectionModel.isSelectedIndex(m)) {
/* 2271 */         arrayOfInt1[(k++)] = m;
/*      */       }
/*      */     }
/* 2274 */     int[] arrayOfInt2 = new int[k];
/* 2275 */     System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, k);
/* 2276 */     return arrayOfInt2;
/*      */   }
/*      */ 
/*      */   public int[] getSelectedColumns()
/*      */   {
/* 2287 */     return this.columnModel.getSelectedColumns();
/*      */   }
/*      */ 
/*      */   public int getSelectedRowCount()
/*      */   {
/* 2296 */     int i = this.selectionModel.getMinSelectionIndex();
/* 2297 */     int j = this.selectionModel.getMaxSelectionIndex();
/* 2298 */     int k = 0;
/*      */ 
/* 2300 */     for (int m = i; m <= j; m++) {
/* 2301 */       if (this.selectionModel.isSelectedIndex(m)) {
/* 2302 */         k++;
/*      */       }
/*      */     }
/* 2305 */     return k;
/*      */   }
/*      */ 
/*      */   public int getSelectedColumnCount()
/*      */   {
/* 2314 */     return this.columnModel.getSelectedColumnCount();
/*      */   }
/*      */ 
/*      */   public boolean isRowSelected(int paramInt)
/*      */   {
/* 2325 */     return this.selectionModel.isSelectedIndex(paramInt);
/*      */   }
/*      */ 
/*      */   public boolean isColumnSelected(int paramInt)
/*      */   {
/* 2337 */     return this.columnModel.getSelectionModel().isSelectedIndex(paramInt);
/*      */   }
/*      */ 
/*      */   public boolean isCellSelected(int paramInt1, int paramInt2)
/*      */   {
/* 2351 */     if ((!getRowSelectionAllowed()) && (!getColumnSelectionAllowed())) {
/* 2352 */       return false;
/*      */     }
/* 2354 */     return ((!getRowSelectionAllowed()) || (isRowSelected(paramInt1))) && ((!getColumnSelectionAllowed()) || (isColumnSelected(paramInt2)));
/*      */   }
/*      */ 
/*      */   private void changeSelectionModel(ListSelectionModel paramListSelectionModel, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, boolean paramBoolean4)
/*      */   {
/* 2361 */     if (paramBoolean2) {
/* 2362 */       if (paramBoolean1) {
/* 2363 */         if (paramBoolean4) {
/* 2364 */           paramListSelectionModel.addSelectionInterval(paramInt2, paramInt1);
/*      */         } else {
/* 2366 */           paramListSelectionModel.removeSelectionInterval(paramInt2, paramInt1);
/*      */ 
/* 2368 */           if (Boolean.TRUE == getClientProperty("Table.isFileList")) {
/* 2369 */             paramListSelectionModel.addSelectionInterval(paramInt1, paramInt1);
/* 2370 */             paramListSelectionModel.setAnchorSelectionIndex(paramInt2);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 2375 */         paramListSelectionModel.setSelectionInterval(paramInt2, paramInt1);
/*      */       }
/*      */ 
/*      */     }
/* 2379 */     else if (paramBoolean1) {
/* 2380 */       if (paramBoolean3) {
/* 2381 */         paramListSelectionModel.removeSelectionInterval(paramInt1, paramInt1);
/*      */       }
/*      */       else {
/* 2384 */         paramListSelectionModel.addSelectionInterval(paramInt1, paramInt1);
/*      */       }
/*      */     }
/*      */     else
/* 2388 */       paramListSelectionModel.setSelectionInterval(paramInt1, paramInt1);
/*      */   }
/*      */ 
/*      */   public void changeSelection(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 2423 */     ListSelectionModel localListSelectionModel1 = getSelectionModel();
/* 2424 */     ListSelectionModel localListSelectionModel2 = getColumnModel().getSelectionModel();
/*      */ 
/* 2426 */     int i = getAdjustedIndex(localListSelectionModel1.getAnchorSelectionIndex(), true);
/* 2427 */     int j = getAdjustedIndex(localListSelectionModel2.getAnchorSelectionIndex(), false);
/*      */ 
/* 2429 */     boolean bool1 = true;
/*      */ 
/* 2431 */     if (i == -1) {
/* 2432 */       if (getRowCount() > 0) {
/* 2433 */         i = 0;
/*      */       }
/* 2435 */       bool1 = false;
/*      */     }
/*      */ 
/* 2438 */     if (j == -1) {
/* 2439 */       if (getColumnCount() > 0) {
/* 2440 */         j = 0;
/*      */       }
/* 2442 */       bool1 = false;
/*      */     }
/*      */ 
/* 2452 */     boolean bool2 = isCellSelected(paramInt1, paramInt2);
/* 2453 */     bool1 = (bool1) && (isCellSelected(i, j));
/*      */ 
/* 2455 */     changeSelectionModel(localListSelectionModel2, paramInt2, paramBoolean1, paramBoolean2, bool2, j, bool1);
/*      */ 
/* 2457 */     changeSelectionModel(localListSelectionModel1, paramInt1, paramBoolean1, paramBoolean2, bool2, i, bool1);
/*      */ 
/* 2463 */     if (getAutoscrolls()) {
/* 2464 */       Rectangle localRectangle = getCellRect(paramInt1, paramInt2, false);
/* 2465 */       if (localRectangle != null)
/* 2466 */         scrollRectToVisible(localRectangle);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Color getSelectionForeground()
/*      */   {
/* 2479 */     return this.selectionForeground;
/*      */   }
/*      */ 
/*      */   public void setSelectionForeground(Color paramColor)
/*      */   {
/* 2504 */     Color localColor = this.selectionForeground;
/* 2505 */     this.selectionForeground = paramColor;
/* 2506 */     firePropertyChange("selectionForeground", localColor, paramColor);
/* 2507 */     repaint();
/*      */   }
/*      */ 
/*      */   public Color getSelectionBackground()
/*      */   {
/* 2518 */     return this.selectionBackground;
/*      */   }
/*      */ 
/*      */   public void setSelectionBackground(Color paramColor)
/*      */   {
/* 2542 */     Color localColor = this.selectionBackground;
/* 2543 */     this.selectionBackground = paramColor;
/* 2544 */     firePropertyChange("selectionBackground", localColor, paramColor);
/* 2545 */     repaint();
/*      */   }
/*      */ 
/*      */   public TableColumn getColumn(Object paramObject)
/*      */   {
/* 2559 */     TableColumnModel localTableColumnModel = getColumnModel();
/* 2560 */     int i = localTableColumnModel.getColumnIndex(paramObject);
/* 2561 */     return localTableColumnModel.getColumn(i);
/*      */   }
/*      */ 
/*      */   public int convertColumnIndexToModel(int paramInt)
/*      */   {
/* 2581 */     return SwingUtilities2.convertColumnIndexToModel(getColumnModel(), paramInt);
/*      */   }
/*      */ 
/*      */   public int convertColumnIndexToView(int paramInt)
/*      */   {
/* 2599 */     return SwingUtilities2.convertColumnIndexToView(getColumnModel(), paramInt);
/*      */   }
/*      */ 
/*      */   public int convertRowIndexToView(int paramInt)
/*      */   {
/* 2617 */     RowSorter localRowSorter = getRowSorter();
/* 2618 */     if (localRowSorter != null) {
/* 2619 */       return localRowSorter.convertRowIndexToView(paramInt);
/*      */     }
/* 2621 */     return paramInt;
/*      */   }
/*      */ 
/*      */   public int convertRowIndexToModel(int paramInt)
/*      */   {
/* 2639 */     RowSorter localRowSorter = getRowSorter();
/* 2640 */     if (localRowSorter != null) {
/* 2641 */       return localRowSorter.convertRowIndexToModel(paramInt);
/*      */     }
/* 2643 */     return paramInt;
/*      */   }
/*      */ 
/*      */   public int getRowCount()
/*      */   {
/* 2657 */     RowSorter localRowSorter = getRowSorter();
/* 2658 */     if (localRowSorter != null) {
/* 2659 */       return localRowSorter.getViewRowCount();
/*      */     }
/* 2661 */     return getModel().getRowCount();
/*      */   }
/*      */ 
/*      */   public int getColumnCount()
/*      */   {
/* 2673 */     return getColumnModel().getColumnCount();
/*      */   }
/*      */ 
/*      */   public String getColumnName(int paramInt)
/*      */   {
/* 2685 */     return getModel().getColumnName(convertColumnIndexToModel(paramInt));
/*      */   }
/*      */ 
/*      */   public Class<?> getColumnClass(int paramInt)
/*      */   {
/* 2697 */     return getModel().getColumnClass(convertColumnIndexToModel(paramInt));
/*      */   }
/*      */ 
/*      */   public Object getValueAt(int paramInt1, int paramInt2)
/*      */   {
/* 2716 */     return getModel().getValueAt(convertRowIndexToModel(paramInt1), convertColumnIndexToModel(paramInt2));
/*      */   }
/*      */ 
/*      */   public void setValueAt(Object paramObject, int paramInt1, int paramInt2)
/*      */   {
/* 2740 */     getModel().setValueAt(paramObject, convertRowIndexToModel(paramInt1), convertColumnIndexToModel(paramInt2));
/*      */   }
/*      */ 
/*      */   public boolean isCellEditable(int paramInt1, int paramInt2)
/*      */   {
/* 2764 */     return getModel().isCellEditable(convertRowIndexToModel(paramInt1), convertColumnIndexToModel(paramInt2));
/*      */   }
/*      */ 
/*      */   public void addColumn(TableColumn paramTableColumn)
/*      */   {
/* 2798 */     if (paramTableColumn.getHeaderValue() == null) {
/* 2799 */       int i = paramTableColumn.getModelIndex();
/* 2800 */       String str = getModel().getColumnName(i);
/* 2801 */       paramTableColumn.setHeaderValue(str);
/*      */     }
/* 2803 */     getColumnModel().addColumn(paramTableColumn);
/*      */   }
/*      */ 
/*      */   public void removeColumn(TableColumn paramTableColumn)
/*      */   {
/* 2816 */     getColumnModel().removeColumn(paramTableColumn);
/*      */   }
/*      */ 
/*      */   public void moveColumn(int paramInt1, int paramInt2)
/*      */   {
/* 2829 */     getColumnModel().moveColumn(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public int columnAtPoint(Point paramPoint)
/*      */   {
/* 2848 */     int i = paramPoint.x;
/* 2849 */     if (!getComponentOrientation().isLeftToRight()) {
/* 2850 */       i = getWidth() - i - 1;
/*      */     }
/* 2852 */     return getColumnModel().getColumnIndexAtX(i);
/*      */   }
/*      */ 
/*      */   public int rowAtPoint(Point paramPoint)
/*      */   {
/* 2867 */     int i = paramPoint.y;
/* 2868 */     int j = this.rowModel == null ? i / getRowHeight() : this.rowModel.getIndex(i);
/* 2869 */     if (j < 0) {
/* 2870 */       return -1;
/*      */     }
/* 2872 */     if (j >= getRowCount()) {
/* 2873 */       return -1;
/*      */     }
/*      */ 
/* 2876 */     return j;
/*      */   }
/*      */ 
/*      */   public Rectangle getCellRect(int paramInt1, int paramInt2, boolean paramBoolean)
/*      */   {
/* 2925 */     Rectangle localRectangle = new Rectangle();
/* 2926 */     int i = 1;
/* 2927 */     if (paramInt1 < 0)
/*      */     {
/* 2929 */       i = 0;
/*      */     }
/* 2931 */     else if (paramInt1 >= getRowCount()) {
/* 2932 */       localRectangle.y = getHeight();
/* 2933 */       i = 0;
/*      */     }
/*      */     else {
/* 2936 */       localRectangle.height = getRowHeight(paramInt1);
/* 2937 */       localRectangle.y = (this.rowModel == null ? paramInt1 * localRectangle.height : this.rowModel.getPosition(paramInt1));
/*      */     }
/*      */     int k;
/* 2940 */     if (paramInt2 < 0) {
/* 2941 */       if (!getComponentOrientation().isLeftToRight()) {
/* 2942 */         localRectangle.x = getWidth();
/*      */       }
/*      */ 
/* 2945 */       i = 0;
/*      */     }
/* 2947 */     else if (paramInt2 >= getColumnCount()) {
/* 2948 */       if (getComponentOrientation().isLeftToRight()) {
/* 2949 */         localRectangle.x = getWidth();
/*      */       }
/*      */ 
/* 2952 */       i = 0;
/*      */     }
/*      */     else {
/* 2955 */       TableColumnModel localTableColumnModel = getColumnModel();
/* 2956 */       if (getComponentOrientation().isLeftToRight()) {
/* 2957 */         for (k = 0; k < paramInt2; k++)
/* 2958 */           localRectangle.x += localTableColumnModel.getColumn(k).getWidth();
/*      */       }
/*      */       else {
/* 2961 */         for (k = localTableColumnModel.getColumnCount() - 1; k > paramInt2; k--) {
/* 2962 */           localRectangle.x += localTableColumnModel.getColumn(k).getWidth();
/*      */         }
/*      */       }
/* 2965 */       localRectangle.width = localTableColumnModel.getColumn(paramInt2).getWidth();
/*      */     }
/*      */ 
/* 2968 */     if ((i != 0) && (!paramBoolean))
/*      */     {
/* 2971 */       int j = Math.min(getRowMargin(), localRectangle.height);
/* 2972 */       k = Math.min(getColumnModel().getColumnMargin(), localRectangle.width);
/*      */ 
/* 2974 */       localRectangle.setBounds(localRectangle.x + k / 2, localRectangle.y + j / 2, localRectangle.width - k, localRectangle.height - j);
/*      */     }
/* 2976 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   private int viewIndexForColumn(TableColumn paramTableColumn) {
/* 2980 */     TableColumnModel localTableColumnModel = getColumnModel();
/* 2981 */     for (int i = 0; i < localTableColumnModel.getColumnCount(); i++) {
/* 2982 */       if (localTableColumnModel.getColumn(i) == paramTableColumn) {
/* 2983 */         return i;
/*      */       }
/*      */     }
/* 2986 */     return -1;
/*      */   }
/*      */ 
/*      */   public void doLayout()
/*      */   {
/* 3125 */     TableColumn localTableColumn = getResizingColumn();
/* 3126 */     if (localTableColumn == null) {
/* 3127 */       setWidthsFromPreferredWidths(false);
/*      */     }
/*      */     else
/*      */     {
/* 3136 */       int i = viewIndexForColumn(localTableColumn);
/* 3137 */       int j = getWidth() - getColumnModel().getTotalColumnWidth();
/* 3138 */       accommodateDelta(i, j);
/* 3139 */       j = getWidth() - getColumnModel().getTotalColumnWidth();
/*      */ 
/* 3151 */       if (j != 0) {
/* 3152 */         localTableColumn.setWidth(localTableColumn.getWidth() + j);
/*      */       }
/*      */ 
/* 3161 */       setWidthsFromPreferredWidths(true);
/*      */     }
/*      */ 
/* 3164 */     super.doLayout();
/*      */   }
/*      */ 
/*      */   private TableColumn getResizingColumn() {
/* 3168 */     return this.tableHeader == null ? null : this.tableHeader.getResizingColumn();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void sizeColumnsToFit(boolean paramBoolean)
/*      */   {
/* 3180 */     int i = this.autoResizeMode;
/* 3181 */     setAutoResizeMode(paramBoolean ? 3 : 4);
/*      */ 
/* 3183 */     sizeColumnsToFit(-1);
/* 3184 */     setAutoResizeMode(i);
/*      */   }
/*      */ 
/*      */   public void sizeColumnsToFit(int paramInt)
/*      */   {
/* 3195 */     if (paramInt == -1) {
/* 3196 */       setWidthsFromPreferredWidths(false);
/*      */     }
/* 3199 */     else if (this.autoResizeMode == 0) {
/* 3200 */       TableColumn localTableColumn = getColumnModel().getColumn(paramInt);
/* 3201 */       localTableColumn.setPreferredWidth(localTableColumn.getWidth());
/*      */     }
/*      */     else {
/* 3204 */       int i = getWidth() - getColumnModel().getTotalColumnWidth();
/* 3205 */       accommodateDelta(paramInt, i);
/* 3206 */       setWidthsFromPreferredWidths(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setWidthsFromPreferredWidths(final boolean paramBoolean)
/*      */   {
/* 3212 */     int i = getWidth();
/* 3213 */     int j = getPreferredSize().width;
/* 3214 */     int k = !paramBoolean ? i : j;
/*      */ 
/* 3216 */     final TableColumnModel localTableColumnModel = this.columnModel;
/* 3217 */     Resizable3 local2 = new Resizable3() {
/* 3218 */       public int getElementCount() { return localTableColumnModel.getColumnCount(); } 
/* 3219 */       public int getLowerBoundAt(int paramAnonymousInt) { return localTableColumnModel.getColumn(paramAnonymousInt).getMinWidth(); } 
/* 3220 */       public int getUpperBoundAt(int paramAnonymousInt) { return localTableColumnModel.getColumn(paramAnonymousInt).getMaxWidth(); } 
/*      */       public int getMidPointAt(int paramAnonymousInt) {
/* 3222 */         if (!paramBoolean) {
/* 3223 */           return localTableColumnModel.getColumn(paramAnonymousInt).getPreferredWidth();
/*      */         }
/*      */ 
/* 3226 */         return localTableColumnModel.getColumn(paramAnonymousInt).getWidth();
/*      */       }
/*      */ 
/*      */       public void setSizeAt(int paramAnonymousInt1, int paramAnonymousInt2) {
/* 3230 */         if (!paramBoolean) {
/* 3231 */           localTableColumnModel.getColumn(paramAnonymousInt2).setWidth(paramAnonymousInt1);
/*      */         }
/*      */         else
/* 3234 */           localTableColumnModel.getColumn(paramAnonymousInt2).setPreferredWidth(paramAnonymousInt1);
/*      */       }
/*      */     };
/* 3239 */     adjustSizes(k, local2, paramBoolean);
/*      */   }
/*      */ 
/*      */   private void accommodateDelta(int paramInt1, int paramInt2)
/*      */   {
/* 3245 */     int i = getColumnCount();
/* 3246 */     int j = paramInt1;
/*      */     int k;
/* 3250 */     switch (this.autoResizeMode) {
/*      */     case 1:
/* 3252 */       j += 1;
/* 3253 */       k = Math.min(j + 1, i); break;
/*      */     case 2:
/* 3255 */       j += 1;
/* 3256 */       k = i; break;
/*      */     case 3:
/* 3258 */       j = i - 1;
/* 3259 */       k = j + 1; break;
/*      */     case 4:
/* 3261 */       j = 0;
/* 3262 */       k = i; break;
/*      */     default:
/* 3264 */       return;
/*      */     }
/*      */ 
/* 3267 */     final int m = j;
/* 3268 */     final int n = k;
/* 3269 */     final TableColumnModel localTableColumnModel = this.columnModel;
/* 3270 */     Resizable3 local3 = new Resizable3() {
/* 3271 */       public int getElementCount() { return n - m; } 
/* 3272 */       public int getLowerBoundAt(int paramAnonymousInt) { return localTableColumnModel.getColumn(paramAnonymousInt + m).getMinWidth(); } 
/* 3273 */       public int getUpperBoundAt(int paramAnonymousInt) { return localTableColumnModel.getColumn(paramAnonymousInt + m).getMaxWidth(); } 
/* 3274 */       public int getMidPointAt(int paramAnonymousInt) { return localTableColumnModel.getColumn(paramAnonymousInt + m).getWidth(); } 
/* 3275 */       public void setSizeAt(int paramAnonymousInt1, int paramAnonymousInt2) { localTableColumnModel.getColumn(paramAnonymousInt2 + m).setWidth(paramAnonymousInt1); }
/*      */ 
/*      */     };
/* 3278 */     int i1 = 0;
/* 3279 */     for (int i2 = j; i2 < k; i2++) {
/* 3280 */       TableColumn localTableColumn = this.columnModel.getColumn(i2);
/* 3281 */       int i3 = localTableColumn.getWidth();
/* 3282 */       i1 += i3;
/*      */     }
/*      */ 
/* 3285 */     adjustSizes(i1 + paramInt2, local3, false);
/*      */   }
/*      */ 
/*      */   private void adjustSizes(long paramLong, final Resizable3 paramResizable3, boolean paramBoolean)
/*      */   {
/* 3301 */     int i = paramResizable3.getElementCount();
/* 3302 */     long l = 0L;
/* 3303 */     for (int j = 0; j < i; j++)
/* 3304 */       l += paramResizable3.getMidPointAt(j);
/*      */     Object localObject;
/* 3307 */     if ((paramLong < l ? 1 : 0) == (!paramBoolean ? 1 : 0)) {
/* 3308 */       localObject = new Resizable2() {
/* 3309 */         public int getElementCount() { return paramResizable3.getElementCount(); } 
/* 3310 */         public int getLowerBoundAt(int paramAnonymousInt) { return paramResizable3.getLowerBoundAt(paramAnonymousInt); } 
/* 3311 */         public int getUpperBoundAt(int paramAnonymousInt) { return paramResizable3.getMidPointAt(paramAnonymousInt); } 
/* 3312 */         public void setSizeAt(int paramAnonymousInt1, int paramAnonymousInt2) { paramResizable3.setSizeAt(paramAnonymousInt1, paramAnonymousInt2); }
/*      */ 
/*      */       };
/*      */     }
/*      */     else {
/* 3317 */       localObject = new Resizable2() {
/* 3318 */         public int getElementCount() { return paramResizable3.getElementCount(); } 
/* 3319 */         public int getLowerBoundAt(int paramAnonymousInt) { return paramResizable3.getMidPointAt(paramAnonymousInt); } 
/* 3320 */         public int getUpperBoundAt(int paramAnonymousInt) { return paramResizable3.getUpperBoundAt(paramAnonymousInt); } 
/* 3321 */         public void setSizeAt(int paramAnonymousInt1, int paramAnonymousInt2) { paramResizable3.setSizeAt(paramAnonymousInt1, paramAnonymousInt2); }
/*      */ 
/*      */       };
/*      */     }
/* 3325 */     adjustSizes(paramLong, (Resizable2)localObject, !paramBoolean);
/*      */   }
/*      */ 
/*      */   private void adjustSizes(long paramLong, Resizable2 paramResizable2, boolean paramBoolean) {
/* 3329 */     long l1 = 0L;
/* 3330 */     long l2 = 0L;
/* 3331 */     for (int i = 0; i < paramResizable2.getElementCount(); i++) {
/* 3332 */       l1 += paramResizable2.getLowerBoundAt(i);
/* 3333 */       l2 += paramResizable2.getUpperBoundAt(i);
/*      */     }
/*      */ 
/* 3336 */     if (paramBoolean) {
/* 3337 */       paramLong = Math.min(Math.max(l1, paramLong), l2);
/*      */     }
/*      */ 
/* 3340 */     for (i = 0; i < paramResizable2.getElementCount(); i++) {
/* 3341 */       int j = paramResizable2.getLowerBoundAt(i);
/* 3342 */       int k = paramResizable2.getUpperBoundAt(i);
/*      */       int m;
/* 3347 */       if (l1 == l2) {
/* 3348 */         m = j;
/*      */       }
/*      */       else {
/* 3351 */         double d = (paramLong - l1) / (l2 - l1);
/* 3352 */         m = (int)Math.round(j + d * (k - j));
/*      */       }
/*      */ 
/* 3357 */       paramResizable2.setSizeAt(m, i);
/* 3358 */       paramLong -= m;
/* 3359 */       l1 -= j;
/* 3360 */       l2 -= k;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getToolTipText(MouseEvent paramMouseEvent)
/*      */   {
/* 3381 */     String str = null;
/* 3382 */     Point localPoint = paramMouseEvent.getPoint();
/*      */ 
/* 3385 */     int i = columnAtPoint(localPoint);
/* 3386 */     int j = rowAtPoint(localPoint);
/*      */ 
/* 3388 */     if ((i != -1) && (j != -1)) {
/* 3389 */       TableCellRenderer localTableCellRenderer = getCellRenderer(j, i);
/* 3390 */       Component localComponent = prepareRenderer(localTableCellRenderer, j, i);
/*      */ 
/* 3394 */       if ((localComponent instanceof JComponent))
/*      */       {
/* 3396 */         Rectangle localRectangle = getCellRect(j, i, false);
/* 3397 */         localPoint.translate(-localRectangle.x, -localRectangle.y);
/* 3398 */         MouseEvent localMouseEvent = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
/*      */ 
/* 3407 */         str = ((JComponent)localComponent).getToolTipText(localMouseEvent);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3412 */     if (str == null) {
/* 3413 */       str = getToolTipText();
/*      */     }
/* 3415 */     return str;
/*      */   }
/*      */ 
/*      */   public void setSurrendersFocusOnKeystroke(boolean paramBoolean)
/*      */   {
/* 3438 */     this.surrendersFocusOnKeystroke = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getSurrendersFocusOnKeystroke()
/*      */   {
/* 3453 */     return this.surrendersFocusOnKeystroke;
/*      */   }
/*      */ 
/*      */   public boolean editCellAt(int paramInt1, int paramInt2)
/*      */   {
/* 3469 */     return editCellAt(paramInt1, paramInt2, null);
/*      */   }
/*      */ 
/*      */   public boolean editCellAt(int paramInt1, int paramInt2, EventObject paramEventObject)
/*      */   {
/* 3490 */     if ((this.cellEditor != null) && (!this.cellEditor.stopCellEditing())) {
/* 3491 */       return false;
/*      */     }
/*      */ 
/* 3494 */     if ((paramInt1 < 0) || (paramInt1 >= getRowCount()) || (paramInt2 < 0) || (paramInt2 >= getColumnCount()))
/*      */     {
/* 3496 */       return false;
/*      */     }
/*      */ 
/* 3499 */     if (!isCellEditable(paramInt1, paramInt2)) {
/* 3500 */       return false;
/*      */     }
/* 3502 */     if (this.editorRemover == null) {
/* 3503 */       localObject = KeyboardFocusManager.getCurrentKeyboardFocusManager();
/*      */ 
/* 3505 */       this.editorRemover = new CellEditorRemover((KeyboardFocusManager)localObject);
/* 3506 */       ((KeyboardFocusManager)localObject).addPropertyChangeListener("permanentFocusOwner", this.editorRemover);
/*      */     }
/*      */ 
/* 3509 */     Object localObject = getCellEditor(paramInt1, paramInt2);
/* 3510 */     if ((localObject != null) && (((TableCellEditor)localObject).isCellEditable(paramEventObject))) {
/* 3511 */       this.editorComp = prepareEditor((TableCellEditor)localObject, paramInt1, paramInt2);
/* 3512 */       if (this.editorComp == null) {
/* 3513 */         removeEditor();
/* 3514 */         return false;
/*      */       }
/* 3516 */       this.editorComp.setBounds(getCellRect(paramInt1, paramInt2, false));
/* 3517 */       add(this.editorComp);
/* 3518 */       this.editorComp.validate();
/* 3519 */       this.editorComp.repaint();
/*      */ 
/* 3521 */       setCellEditor((TableCellEditor)localObject);
/* 3522 */       setEditingRow(paramInt1);
/* 3523 */       setEditingColumn(paramInt2);
/* 3524 */       ((TableCellEditor)localObject).addCellEditorListener(this);
/*      */ 
/* 3526 */       return true;
/*      */     }
/* 3528 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isEditing()
/*      */   {
/* 3539 */     return this.cellEditor != null;
/*      */   }
/*      */ 
/*      */   public Component getEditorComponent()
/*      */   {
/* 3549 */     return this.editorComp;
/*      */   }
/*      */ 
/*      */   public int getEditingColumn()
/*      */   {
/* 3561 */     return this.editingColumn;
/*      */   }
/*      */ 
/*      */   public int getEditingRow()
/*      */   {
/* 3573 */     return this.editingRow;
/*      */   }
/*      */ 
/*      */   public TableUI getUI()
/*      */   {
/* 3586 */     return (TableUI)this.ui;
/*      */   }
/*      */ 
/*      */   public void setUI(TableUI paramTableUI)
/*      */   {
/* 3601 */     if (this.ui != paramTableUI) {
/* 3602 */       super.setUI(paramTableUI);
/* 3603 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateUI()
/*      */   {
/* 3616 */     TableColumnModel localTableColumnModel = getColumnModel();
/* 3617 */     for (int i = 0; i < localTableColumnModel.getColumnCount(); i++) {
/* 3618 */       localObject = localTableColumnModel.getColumn(i);
/* 3619 */       SwingUtilities.updateRendererOrEditorUI(((TableColumn)localObject).getCellRenderer());
/* 3620 */       SwingUtilities.updateRendererOrEditorUI(((TableColumn)localObject).getCellEditor());
/* 3621 */       SwingUtilities.updateRendererOrEditorUI(((TableColumn)localObject).getHeaderRenderer());
/*      */     }
/*      */ 
/* 3625 */     Enumeration localEnumeration = this.defaultRenderersByColumnClass.elements();
/* 3626 */     while (localEnumeration.hasMoreElements()) {
/* 3627 */       SwingUtilities.updateRendererOrEditorUI(localEnumeration.nextElement());
/*      */     }
/*      */ 
/* 3631 */     Object localObject = this.defaultEditorsByColumnClass.elements();
/* 3632 */     while (((Enumeration)localObject).hasMoreElements()) {
/* 3633 */       SwingUtilities.updateRendererOrEditorUI(((Enumeration)localObject).nextElement());
/*      */     }
/*      */ 
/* 3637 */     if ((this.tableHeader != null) && (this.tableHeader.getParent() == null)) {
/* 3638 */       this.tableHeader.updateUI();
/*      */     }
/*      */ 
/* 3642 */     configureEnclosingScrollPaneUI();
/*      */ 
/* 3644 */     setUI((TableUI)UIManager.getUI(this));
/*      */   }
/*      */ 
/*      */   public String getUIClassID()
/*      */   {
/* 3656 */     return "TableUI";
/*      */   }
/*      */ 
/*      */   public void setModel(TableModel paramTableModel)
/*      */   {
/* 3676 */     if (paramTableModel == null) {
/* 3677 */       throw new IllegalArgumentException("Cannot set a null TableModel");
/*      */     }
/* 3679 */     if (this.dataModel != paramTableModel) {
/* 3680 */       TableModel localTableModel = this.dataModel;
/* 3681 */       if (localTableModel != null) {
/* 3682 */         localTableModel.removeTableModelListener(this);
/*      */       }
/* 3684 */       this.dataModel = paramTableModel;
/* 3685 */       paramTableModel.addTableModelListener(this);
/*      */ 
/* 3687 */       tableChanged(new TableModelEvent(paramTableModel, -1));
/*      */ 
/* 3689 */       firePropertyChange("model", localTableModel, paramTableModel);
/*      */ 
/* 3691 */       if (getAutoCreateRowSorter())
/* 3692 */         setRowSorter(new TableRowSorter(paramTableModel));
/*      */     }
/*      */   }
/*      */ 
/*      */   public TableModel getModel()
/*      */   {
/* 3705 */     return this.dataModel;
/*      */   }
/*      */ 
/*      */   public void setColumnModel(TableColumnModel paramTableColumnModel)
/*      */   {
/* 3721 */     if (paramTableColumnModel == null) {
/* 3722 */       throw new IllegalArgumentException("Cannot set a null ColumnModel");
/*      */     }
/* 3724 */     TableColumnModel localTableColumnModel = this.columnModel;
/* 3725 */     if (paramTableColumnModel != localTableColumnModel) {
/* 3726 */       if (localTableColumnModel != null) {
/* 3727 */         localTableColumnModel.removeColumnModelListener(this);
/*      */       }
/* 3729 */       this.columnModel = paramTableColumnModel;
/* 3730 */       paramTableColumnModel.addColumnModelListener(this);
/*      */ 
/* 3733 */       if (this.tableHeader != null) {
/* 3734 */         this.tableHeader.setColumnModel(paramTableColumnModel);
/*      */       }
/*      */ 
/* 3737 */       firePropertyChange("columnModel", localTableColumnModel, paramTableColumnModel);
/* 3738 */       resizeAndRepaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public TableColumnModel getColumnModel()
/*      */   {
/* 3750 */     return this.columnModel;
/*      */   }
/*      */ 
/*      */   public void setSelectionModel(ListSelectionModel paramListSelectionModel)
/*      */   {
/* 3765 */     if (paramListSelectionModel == null) {
/* 3766 */       throw new IllegalArgumentException("Cannot set a null SelectionModel");
/*      */     }
/*      */ 
/* 3769 */     ListSelectionModel localListSelectionModel = this.selectionModel;
/*      */ 
/* 3771 */     if (paramListSelectionModel != localListSelectionModel) {
/* 3772 */       if (localListSelectionModel != null) {
/* 3773 */         localListSelectionModel.removeListSelectionListener(this);
/*      */       }
/*      */ 
/* 3776 */       this.selectionModel = paramListSelectionModel;
/* 3777 */       paramListSelectionModel.addListSelectionListener(this);
/*      */ 
/* 3779 */       firePropertyChange("selectionModel", localListSelectionModel, paramListSelectionModel);
/* 3780 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ListSelectionModel getSelectionModel()
/*      */   {
/* 3793 */     return this.selectionModel;
/*      */   }
/*      */ 
/*      */   public void sorterChanged(RowSorterEvent paramRowSorterEvent)
/*      */   {
/* 3809 */     if (paramRowSorterEvent.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
/* 3810 */       JTableHeader localJTableHeader = getTableHeader();
/* 3811 */       if (localJTableHeader != null) {
/* 3812 */         localJTableHeader.repaint();
/*      */       }
/*      */     }
/* 3815 */     else if (paramRowSorterEvent.getType() == RowSorterEvent.Type.SORTED) {
/* 3816 */       this.sorterChanged = true;
/* 3817 */       if (!this.ignoreSortChange)
/* 3818 */         sortedTableChanged(paramRowSorterEvent, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void sortedTableChanged(RowSorterEvent paramRowSorterEvent, TableModelEvent paramTableModelEvent)
/*      */   {
/* 4102 */     int i = -1;
/* 4103 */     ModelChange localModelChange = paramTableModelEvent != null ? new ModelChange(paramTableModelEvent) : null;
/*      */ 
/* 4105 */     if (((localModelChange == null) || (!localModelChange.allRowsChanged)) && (this.editingRow != -1))
/*      */     {
/* 4107 */       i = convertRowIndexToModel(paramRowSorterEvent, this.editingRow);
/*      */     }
/*      */ 
/* 4111 */     this.sortManager.prepareForChange(paramRowSorterEvent, localModelChange);
/*      */ 
/* 4113 */     if (paramTableModelEvent != null) {
/* 4114 */       if (localModelChange.type == 0) {
/* 4115 */         repaintSortedRows(localModelChange);
/*      */       }
/* 4117 */       notifySorter(localModelChange);
/* 4118 */       if (localModelChange.type != 0)
/*      */       {
/* 4121 */         this.sorterChanged = true;
/*      */       }
/*      */     }
/*      */     else {
/* 4125 */       this.sorterChanged = true;
/*      */     }
/*      */ 
/* 4128 */     this.sortManager.processChange(paramRowSorterEvent, localModelChange, this.sorterChanged);
/*      */ 
/* 4130 */     if (this.sorterChanged)
/*      */     {
/* 4132 */       if (this.editingRow != -1) {
/* 4133 */         int j = i == -1 ? -1 : convertRowIndexToView(i, localModelChange);
/*      */ 
/* 4135 */         restoreSortingEditingRow(j);
/*      */       }
/*      */ 
/* 4139 */       if ((paramTableModelEvent == null) || (localModelChange.type != 0)) {
/* 4140 */         resizeAndRepaint();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4145 */     if ((localModelChange != null) && (localModelChange.allRowsChanged)) {
/* 4146 */       clearSelectionAndLeadAnchor();
/* 4147 */       resizeAndRepaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void repaintSortedRows(ModelChange paramModelChange)
/*      */   {
/* 4155 */     if ((paramModelChange.startModelIndex > paramModelChange.endModelIndex) || (paramModelChange.startModelIndex + 10 < paramModelChange.endModelIndex))
/*      */     {
/* 4158 */       repaint();
/* 4159 */       return;
/*      */     }
/* 4161 */     int i = paramModelChange.event.getColumn();
/* 4162 */     int j = i;
/* 4163 */     if (j == -1) {
/* 4164 */       j = 0;
/*      */     }
/*      */     else {
/* 4167 */       j = convertColumnIndexToView(j);
/* 4168 */       if (j == -1) {
/* 4169 */         return;
/*      */       }
/*      */     }
/* 4172 */     int k = paramModelChange.startModelIndex;
/* 4173 */     while (k <= paramModelChange.endModelIndex) {
/* 4174 */       int m = convertRowIndexToView(k++);
/* 4175 */       if (m != -1) {
/* 4176 */         Rectangle localRectangle = getCellRect(m, j, false);
/*      */ 
/* 4178 */         int n = localRectangle.x;
/* 4179 */         int i1 = localRectangle.width;
/* 4180 */         if (i == -1) {
/* 4181 */           n = 0;
/* 4182 */           i1 = getWidth();
/*      */         }
/* 4184 */         repaint(n, localRectangle.y, i1, localRectangle.height);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void restoreSortingSelection(int[] paramArrayOfInt, int paramInt, ModelChange paramModelChange)
/*      */   {
/* 4196 */     for (int i = paramArrayOfInt.length - 1; i >= 0; i--) {
/* 4197 */       paramArrayOfInt[i] = convertRowIndexToView(paramArrayOfInt[i], paramModelChange);
/*      */     }
/* 4199 */     paramInt = convertRowIndexToView(paramInt, paramModelChange);
/*      */ 
/* 4202 */     if ((paramArrayOfInt.length == 0) || ((paramArrayOfInt.length == 1) && (paramArrayOfInt[0] == getSelectedRow())))
/*      */     {
/* 4204 */       return;
/*      */     }
/*      */ 
/* 4208 */     this.selectionModel.setValueIsAdjusting(true);
/* 4209 */     this.selectionModel.clearSelection();
/* 4210 */     for (i = paramArrayOfInt.length - 1; i >= 0; i--) {
/* 4211 */       if (paramArrayOfInt[i] != -1) {
/* 4212 */         this.selectionModel.addSelectionInterval(paramArrayOfInt[i], paramArrayOfInt[i]);
/*      */       }
/*      */     }
/*      */ 
/* 4216 */     SwingUtilities2.setLeadAnchorWithoutSelection(this.selectionModel, paramInt, paramInt);
/*      */ 
/* 4218 */     this.selectionModel.setValueIsAdjusting(false);
/*      */   }
/*      */ 
/*      */   private void restoreSortingEditingRow(int paramInt)
/*      */   {
/* 4227 */     if (paramInt == -1)
/*      */     {
/* 4229 */       TableCellEditor localTableCellEditor = getCellEditor();
/* 4230 */       if (localTableCellEditor != null)
/*      */       {
/* 4232 */         localTableCellEditor.cancelCellEditing();
/* 4233 */         if (getCellEditor() != null)
/*      */         {
/* 4236 */           removeEditor();
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 4242 */       this.editingRow = paramInt;
/* 4243 */       repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifySorter(ModelChange paramModelChange)
/*      */   {
/*      */     try
/*      */     {
/* 4252 */       this.ignoreSortChange = true;
/* 4253 */       this.sorterChanged = false;
/* 4254 */       switch (paramModelChange.type) {
/*      */       case 0:
/* 4256 */         if (paramModelChange.event.getLastRow() == 2147483647)
/* 4257 */           this.sortManager.sorter.allRowsChanged();
/* 4258 */         else if (paramModelChange.event.getColumn() == -1)
/*      */         {
/* 4260 */           this.sortManager.sorter.rowsUpdated(paramModelChange.startModelIndex, paramModelChange.endModelIndex);
/*      */         }
/*      */         else {
/* 4263 */           this.sortManager.sorter.rowsUpdated(paramModelChange.startModelIndex, paramModelChange.endModelIndex, paramModelChange.event.getColumn());
/*      */         }
/*      */ 
/* 4267 */         break;
/*      */       case 1:
/* 4269 */         this.sortManager.sorter.rowsInserted(paramModelChange.startModelIndex, paramModelChange.endModelIndex);
/*      */ 
/* 4271 */         break;
/*      */       case -1:
/* 4273 */         this.sortManager.sorter.rowsDeleted(paramModelChange.startModelIndex, paramModelChange.endModelIndex);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 4278 */       this.ignoreSortChange = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private int convertRowIndexToView(int paramInt, ModelChange paramModelChange)
/*      */   {
/* 4290 */     if (paramInt < 0) {
/* 4291 */       return -1;
/*      */     }
/* 4293 */     if ((paramModelChange != null) && (paramInt >= paramModelChange.startModelIndex)) {
/* 4294 */       if (paramModelChange.type == 1) {
/* 4295 */         if (paramInt + paramModelChange.length >= paramModelChange.modelRowCount) {
/* 4296 */           return -1;
/*      */         }
/* 4298 */         return this.sortManager.sorter.convertRowIndexToView(paramInt + paramModelChange.length);
/*      */       }
/*      */ 
/* 4301 */       if (paramModelChange.type == -1) {
/* 4302 */         if (paramInt <= paramModelChange.endModelIndex)
/*      */         {
/* 4304 */           return -1;
/*      */         }
/*      */ 
/* 4307 */         if (paramInt - paramModelChange.length >= paramModelChange.modelRowCount) {
/* 4308 */           return -1;
/*      */         }
/* 4310 */         return this.sortManager.sorter.convertRowIndexToView(paramInt - paramModelChange.length);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4316 */     if (paramInt >= getModel().getRowCount()) {
/* 4317 */       return -1;
/*      */     }
/* 4319 */     return this.sortManager.sorter.convertRowIndexToView(paramInt);
/*      */   }
/*      */ 
/*      */   private int[] convertSelectionToModel(RowSorterEvent paramRowSorterEvent)
/*      */   {
/* 4327 */     int[] arrayOfInt = getSelectedRows();
/* 4328 */     for (int i = arrayOfInt.length - 1; i >= 0; i--) {
/* 4329 */       arrayOfInt[i] = convertRowIndexToModel(paramRowSorterEvent, arrayOfInt[i]);
/*      */     }
/* 4331 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   private int convertRowIndexToModel(RowSorterEvent paramRowSorterEvent, int paramInt) {
/* 4335 */     if (paramRowSorterEvent != null) {
/* 4336 */       if (paramRowSorterEvent.getPreviousRowCount() == 0) {
/* 4337 */         return paramInt;
/*      */       }
/*      */ 
/* 4340 */       return paramRowSorterEvent.convertPreviousRowIndexToModel(paramInt);
/*      */     }
/*      */ 
/* 4343 */     if ((paramInt < 0) || (paramInt >= getRowCount())) {
/* 4344 */       return -1;
/*      */     }
/* 4346 */     return convertRowIndexToModel(paramInt);
/*      */   }
/*      */ 
/*      */   public void tableChanged(TableModelEvent paramTableModelEvent)
/*      */   {
/* 4367 */     if ((paramTableModelEvent == null) || (paramTableModelEvent.getFirstRow() == -1))
/*      */     {
/* 4369 */       clearSelectionAndLeadAnchor();
/*      */ 
/* 4371 */       this.rowModel = null;
/*      */ 
/* 4373 */       if (this.sortManager != null) {
/*      */         try {
/* 4375 */           this.ignoreSortChange = true;
/* 4376 */           this.sortManager.sorter.modelStructureChanged();
/*      */         } finally {
/* 4378 */           this.ignoreSortChange = false;
/*      */         }
/* 4380 */         this.sortManager.allChanged();
/*      */       }
/*      */ 
/* 4383 */       if (getAutoCreateColumnsFromModel())
/*      */       {
/* 4385 */         createDefaultColumnsFromModel();
/* 4386 */         return;
/*      */       }
/*      */ 
/* 4389 */       resizeAndRepaint();
/* 4390 */       return;
/*      */     }
/*      */ 
/* 4393 */     if (this.sortManager != null) {
/* 4394 */       sortedTableChanged(null, paramTableModelEvent);
/* 4395 */       return;
/*      */     }
/*      */ 
/* 4401 */     if (this.rowModel != null) {
/* 4402 */       repaint();
/*      */     }
/*      */ 
/* 4405 */     if (paramTableModelEvent.getType() == 1) {
/* 4406 */       tableRowsInserted(paramTableModelEvent);
/* 4407 */       return;
/*      */     }
/*      */ 
/* 4410 */     if (paramTableModelEvent.getType() == -1) {
/* 4411 */       tableRowsDeleted(paramTableModelEvent);
/* 4412 */       return;
/*      */     }
/*      */ 
/* 4415 */     int i = paramTableModelEvent.getColumn();
/* 4416 */     int j = paramTableModelEvent.getFirstRow();
/* 4417 */     int k = paramTableModelEvent.getLastRow();
/*      */     Rectangle localRectangle;
/* 4420 */     if (i == -1)
/*      */     {
/* 4422 */       localRectangle = new Rectangle(0, j * getRowHeight(), getColumnModel().getTotalColumnWidth(), 0);
/*      */     }
/*      */     else
/*      */     {
/* 4431 */       int m = convertColumnIndexToView(i);
/* 4432 */       localRectangle = getCellRect(j, m, false);
/*      */     }
/*      */ 
/* 4437 */     if (k != 2147483647) {
/* 4438 */       localRectangle.height = ((k - j + 1) * getRowHeight());
/* 4439 */       repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*      */     }
/*      */     else
/*      */     {
/* 4444 */       clearSelectionAndLeadAnchor();
/* 4445 */       resizeAndRepaint();
/* 4446 */       this.rowModel = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void tableRowsInserted(TableModelEvent paramTableModelEvent)
/*      */   {
/* 4459 */     int i = paramTableModelEvent.getFirstRow();
/* 4460 */     int j = paramTableModelEvent.getLastRow();
/* 4461 */     if (i < 0) {
/* 4462 */       i = 0;
/*      */     }
/* 4464 */     if (j < 0) {
/* 4465 */       j = getRowCount() - 1;
/*      */     }
/*      */ 
/* 4469 */     int k = j - i + 1;
/* 4470 */     this.selectionModel.insertIndexInterval(i, k, true);
/*      */ 
/* 4473 */     if (this.rowModel != null) {
/* 4474 */       this.rowModel.insertEntries(i, k, getRowHeight());
/*      */     }
/* 4476 */     int m = getRowHeight();
/* 4477 */     Rectangle localRectangle = new Rectangle(0, i * m, getColumnModel().getTotalColumnWidth(), (getRowCount() - i) * m);
/*      */ 
/* 4481 */     revalidate();
/*      */ 
/* 4484 */     repaint(localRectangle);
/*      */   }
/*      */ 
/*      */   private void tableRowsDeleted(TableModelEvent paramTableModelEvent)
/*      */   {
/* 4496 */     int i = paramTableModelEvent.getFirstRow();
/* 4497 */     int j = paramTableModelEvent.getLastRow();
/* 4498 */     if (i < 0) {
/* 4499 */       i = 0;
/*      */     }
/* 4501 */     if (j < 0) {
/* 4502 */       j = getRowCount() - 1;
/*      */     }
/*      */ 
/* 4505 */     int k = j - i + 1;
/* 4506 */     int m = getRowCount() + k;
/*      */ 
/* 4508 */     this.selectionModel.removeIndexInterval(i, j);
/*      */ 
/* 4511 */     if (this.rowModel != null) {
/* 4512 */       this.rowModel.removeEntries(i, k);
/*      */     }
/*      */ 
/* 4515 */     int n = getRowHeight();
/* 4516 */     Rectangle localRectangle = new Rectangle(0, i * n, getColumnModel().getTotalColumnWidth(), (m - i) * n);
/*      */ 
/* 4520 */     revalidate();
/*      */ 
/* 4523 */     repaint(localRectangle);
/*      */   }
/*      */ 
/*      */   public void columnAdded(TableColumnModelEvent paramTableColumnModelEvent)
/*      */   {
/* 4540 */     if (isEditing()) {
/* 4541 */       removeEditor();
/*      */     }
/* 4543 */     resizeAndRepaint();
/*      */   }
/*      */ 
/*      */   public void columnRemoved(TableColumnModelEvent paramTableColumnModelEvent)
/*      */   {
/* 4556 */     if (isEditing()) {
/* 4557 */       removeEditor();
/*      */     }
/* 4559 */     resizeAndRepaint();
/*      */   }
/*      */ 
/*      */   public void columnMoved(TableColumnModelEvent paramTableColumnModelEvent)
/*      */   {
/* 4573 */     if ((isEditing()) && (!getCellEditor().stopCellEditing())) {
/* 4574 */       getCellEditor().cancelCellEditing();
/*      */     }
/* 4576 */     repaint();
/*      */   }
/*      */ 
/*      */   public void columnMarginChanged(ChangeEvent paramChangeEvent)
/*      */   {
/* 4591 */     if ((isEditing()) && (!getCellEditor().stopCellEditing())) {
/* 4592 */       getCellEditor().cancelCellEditing();
/*      */     }
/* 4594 */     TableColumn localTableColumn = getResizingColumn();
/*      */ 
/* 4597 */     if ((localTableColumn != null) && (this.autoResizeMode == 0)) {
/* 4598 */       localTableColumn.setPreferredWidth(localTableColumn.getWidth());
/*      */     }
/* 4600 */     resizeAndRepaint();
/*      */   }
/*      */ 
/*      */   private int limit(int paramInt1, int paramInt2, int paramInt3) {
/* 4604 */     return Math.min(paramInt3, Math.max(paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */   public void columnSelectionChanged(ListSelectionEvent paramListSelectionEvent)
/*      */   {
/* 4618 */     boolean bool = paramListSelectionEvent.getValueIsAdjusting();
/* 4619 */     if ((this.columnSelectionAdjusting) && (!bool))
/*      */     {
/* 4623 */       this.columnSelectionAdjusting = false;
/* 4624 */       return;
/*      */     }
/* 4626 */     this.columnSelectionAdjusting = bool;
/*      */ 
/* 4628 */     if ((getRowCount() <= 0) || (getColumnCount() <= 0)) {
/* 4629 */       return;
/*      */     }
/* 4631 */     int i = limit(paramListSelectionEvent.getFirstIndex(), 0, getColumnCount() - 1);
/* 4632 */     int j = limit(paramListSelectionEvent.getLastIndex(), 0, getColumnCount() - 1);
/* 4633 */     int k = 0;
/* 4634 */     int m = getRowCount() - 1;
/* 4635 */     if (getRowSelectionAllowed()) {
/* 4636 */       k = this.selectionModel.getMinSelectionIndex();
/* 4637 */       m = this.selectionModel.getMaxSelectionIndex();
/* 4638 */       int n = getAdjustedIndex(this.selectionModel.getLeadSelectionIndex(), true);
/*      */ 
/* 4640 */       if ((k == -1) || (m == -1)) {
/* 4641 */         if (n == -1)
/*      */         {
/* 4643 */           return;
/*      */         }
/*      */ 
/* 4647 */         k = m = n;
/*      */       }
/* 4652 */       else if (n != -1) {
/* 4653 */         k = Math.min(k, n);
/* 4654 */         m = Math.max(m, n);
/*      */       }
/*      */     }
/*      */ 
/* 4658 */     Rectangle localRectangle1 = getCellRect(k, i, false);
/* 4659 */     Rectangle localRectangle2 = getCellRect(m, j, false);
/* 4660 */     Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
/* 4661 */     repaint(localRectangle3);
/*      */   }
/*      */ 
/*      */   public void valueChanged(ListSelectionEvent paramListSelectionEvent)
/*      */   {
/* 4679 */     if (this.sortManager != null) {
/* 4680 */       this.sortManager.viewSelectionChanged(paramListSelectionEvent);
/*      */     }
/* 4682 */     boolean bool = paramListSelectionEvent.getValueIsAdjusting();
/* 4683 */     if ((this.rowSelectionAdjusting) && (!bool))
/*      */     {
/* 4687 */       this.rowSelectionAdjusting = false;
/* 4688 */       return;
/*      */     }
/* 4690 */     this.rowSelectionAdjusting = bool;
/*      */ 
/* 4692 */     if ((getRowCount() <= 0) || (getColumnCount() <= 0)) {
/* 4693 */       return;
/*      */     }
/* 4695 */     int i = limit(paramListSelectionEvent.getFirstIndex(), 0, getRowCount() - 1);
/* 4696 */     int j = limit(paramListSelectionEvent.getLastIndex(), 0, getRowCount() - 1);
/* 4697 */     Rectangle localRectangle1 = getCellRect(i, 0, false);
/* 4698 */     Rectangle localRectangle2 = getCellRect(j, getColumnCount() - 1, false);
/* 4699 */     Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
/* 4700 */     repaint(localRectangle3);
/*      */   }
/*      */ 
/*      */   public void editingStopped(ChangeEvent paramChangeEvent)
/*      */   {
/* 4719 */     TableCellEditor localTableCellEditor = getCellEditor();
/* 4720 */     if (localTableCellEditor != null) {
/* 4721 */       Object localObject = localTableCellEditor.getCellEditorValue();
/* 4722 */       setValueAt(localObject, this.editingRow, this.editingColumn);
/* 4723 */       removeEditor();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void editingCanceled(ChangeEvent paramChangeEvent)
/*      */   {
/* 4738 */     removeEditor();
/*      */   }
/*      */ 
/*      */   public void setPreferredScrollableViewportSize(Dimension paramDimension)
/*      */   {
/* 4755 */     this.preferredViewportSize = paramDimension;
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredScrollableViewportSize()
/*      */   {
/* 4766 */     return this.preferredViewportSize;
/*      */   }
/*      */ 
/*      */   public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
/*      */   {
/* 4794 */     int i = getLeadingRow(paramRectangle);
/* 4795 */     int j = getLeadingCol(paramRectangle);
/* 4796 */     if ((paramInt1 == 1) && (i < 0))
/*      */     {
/* 4798 */       return getRowHeight();
/*      */     }
/* 4800 */     if ((paramInt1 == 0) && (j < 0))
/*      */     {
/* 4802 */       return 100;
/*      */     }
/*      */ 
/* 4808 */     Rectangle localRectangle = getCellRect(i, j, true);
/* 4809 */     int k = leadingEdge(paramRectangle, paramInt1);
/* 4810 */     int m = leadingEdge(localRectangle, paramInt1);
/*      */     int n;
/* 4812 */     if (paramInt1 == 1) {
/* 4813 */       n = localRectangle.height;
/*      */     }
/*      */     else
/*      */     {
/* 4817 */       n = localRectangle.width;
/*      */     }
/*      */ 
/* 4826 */     if (k == m)
/*      */     {
/* 4829 */       if (paramInt2 < 0) {
/* 4830 */         i1 = 0;
/*      */ 
/* 4832 */         if (paramInt1 == 1) {
/*      */           do {
/* 4834 */             i--; if (i < 0) break;
/* 4835 */             i1 = getRowHeight(i);
/* 4836 */           }while (i1 == 0);
/*      */         }
/*      */         else
/*      */         {
/*      */           while (true)
/*      */           {
/* 4843 */             j--; if (j >= 0) {
/* 4844 */               i1 = getCellRect(i, j, true).width;
/* 4845 */               if (i1 != 0)
/* 4846 */                 break;
/*      */             }
/*      */           }
/*      */         }
/* 4850 */         return i1;
/*      */       }
/*      */ 
/* 4853 */       return n;
/*      */     }
/*      */ 
/* 4858 */     int i1 = Math.abs(k - m);
/* 4859 */     int i2 = n - i1;
/*      */ 
/* 4861 */     if (paramInt2 > 0)
/*      */     {
/* 4863 */       return i2;
/*      */     }
/*      */ 
/* 4866 */     return i1;
/*      */   }
/*      */ 
/*      */   public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
/*      */   {
/*      */     int i;
/* 4887 */     if (getRowCount() == 0)
/*      */     {
/* 4889 */       if (1 == paramInt1) {
/* 4890 */         i = getRowHeight();
/* 4891 */         return i > 0 ? Math.max(i, paramRectangle.height / i * i) : paramRectangle.height;
/*      */       }
/*      */ 
/* 4895 */       return paramRectangle.width;
/*      */     }
/*      */ 
/* 4899 */     if ((null == this.rowModel) && (1 == paramInt1)) {
/* 4900 */       i = rowAtPoint(paramRectangle.getLocation());
/* 4901 */       assert (i != -1);
/* 4902 */       int j = columnAtPoint(paramRectangle.getLocation());
/* 4903 */       Rectangle localRectangle = getCellRect(i, j, true);
/*      */ 
/* 4905 */       if (localRectangle.y == paramRectangle.y) {
/* 4906 */         int k = getRowHeight();
/* 4907 */         assert (k > 0);
/* 4908 */         return Math.max(k, paramRectangle.height / k * k);
/*      */       }
/*      */     }
/* 4911 */     if (paramInt2 < 0) {
/* 4912 */       return getPreviousBlockIncrement(paramRectangle, paramInt1);
/*      */     }
/*      */ 
/* 4915 */     return getNextBlockIncrement(paramRectangle, paramInt1);
/*      */   }
/*      */ 
/*      */   private int getPreviousBlockIncrement(Rectangle paramRectangle, int paramInt)
/*      */   {
/* 4936 */     int m = leadingEdge(paramRectangle, paramInt);
/* 4937 */     boolean bool = getComponentOrientation().isLeftToRight();
/*      */     int k;
/*      */     Point localPoint;
/* 4943 */     if (paramInt == 1) {
/* 4944 */       k = m - paramRectangle.height;
/* 4945 */       int i1 = paramRectangle.x + (bool ? 0 : paramRectangle.width);
/* 4946 */       localPoint = new Point(i1, k);
/*      */     }
/* 4948 */     else if (bool) {
/* 4949 */       k = m - paramRectangle.width;
/* 4950 */       localPoint = new Point(k, paramRectangle.y);
/*      */     }
/*      */     else {
/* 4953 */       k = m + paramRectangle.width;
/* 4954 */       localPoint = new Point(k - 1, paramRectangle.y);
/*      */     }
/* 4956 */     int i = rowAtPoint(localPoint);
/* 4957 */     int j = columnAtPoint(localPoint);
/*      */     int n;
/* 4961 */     if (((paramInt == 1 ? 1 : 0) & (i < 0 ? 1 : 0)) != 0) {
/* 4962 */       n = 0;
/*      */     }
/* 4964 */     else if (((paramInt == 0 ? 1 : 0) & (j < 0 ? 1 : 0)) != 0) {
/* 4965 */       if (bool) {
/* 4966 */         n = 0;
/*      */       }
/*      */       else {
/* 4969 */         n = getWidth();
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 4974 */       Rectangle localRectangle = getCellRect(i, j, true);
/* 4975 */       int i2 = leadingEdge(localRectangle, paramInt);
/* 4976 */       int i3 = trailingEdge(localRectangle, paramInt);
/*      */ 
/* 4988 */       if (((paramInt == 1) || (bool)) && (i3 >= m))
/*      */       {
/* 4990 */         n = i2;
/*      */       }
/* 4992 */       else if ((paramInt == 0) && (!bool) && (i3 <= m))
/*      */       {
/* 4995 */         n = i2;
/*      */       }
/* 4998 */       else if (k == i2) {
/* 4999 */         n = i2;
/*      */       }
/*      */       else
/*      */       {
/* 5003 */         n = i3;
/*      */       }
/*      */     }
/* 5006 */     return Math.abs(m - n);
/*      */   }
/*      */ 
/*      */   private int getNextBlockIncrement(Rectangle paramRectangle, int paramInt)
/*      */   {
/* 5018 */     int i = getTrailingRow(paramRectangle);
/* 5019 */     int j = getTrailingCol(paramRectangle);
/*      */ 
/* 5027 */     int i2 = leadingEdge(paramRectangle, paramInt);
/*      */ 
/* 5035 */     if ((paramInt == 1) && (i < 0)) {
/* 5036 */       return paramRectangle.height;
/*      */     }
/* 5038 */     if ((paramInt == 0) && (j < 0)) {
/* 5039 */       return paramRectangle.width;
/*      */     }
/* 5041 */     Rectangle localRectangle = getCellRect(i, j, true);
/* 5042 */     int m = leadingEdge(localRectangle, paramInt);
/* 5043 */     int n = trailingEdge(localRectangle, paramInt);
/*      */     int k;
/* 5045 */     if ((paramInt == 1) || (getComponentOrientation().isLeftToRight()))
/*      */     {
/* 5047 */       k = m <= i2 ? 1 : 0;
/*      */     }
/*      */     else
/* 5050 */       k = m >= i2 ? 1 : 0;
/*      */     int i1;
/* 5053 */     if (k != 0)
/*      */     {
/* 5056 */       i1 = n;
/*      */     }
/* 5058 */     else if (n == trailingEdge(paramRectangle, paramInt))
/*      */     {
/* 5061 */       i1 = n;
/*      */     }
/*      */     else
/*      */     {
/* 5067 */       i1 = m;
/*      */     }
/* 5069 */     return Math.abs(i1 - i2);
/*      */   }
/*      */ 
/*      */   private int getLeadingRow(Rectangle paramRectangle)
/*      */   {
/*      */     Point localPoint;
/* 5080 */     if (getComponentOrientation().isLeftToRight()) {
/* 5081 */       localPoint = new Point(paramRectangle.x, paramRectangle.y);
/*      */     }
/*      */     else {
/* 5084 */       localPoint = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
/*      */     }
/*      */ 
/* 5087 */     return rowAtPoint(localPoint);
/*      */   }
/*      */ 
/*      */   private int getLeadingCol(Rectangle paramRectangle)
/*      */   {
/*      */     Point localPoint;
/* 5098 */     if (getComponentOrientation().isLeftToRight()) {
/* 5099 */       localPoint = new Point(paramRectangle.x, paramRectangle.y);
/*      */     }
/*      */     else {
/* 5102 */       localPoint = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
/*      */     }
/*      */ 
/* 5105 */     return columnAtPoint(localPoint);
/*      */   }
/*      */ 
/*      */   private int getTrailingRow(Rectangle paramRectangle)
/*      */   {
/*      */     Point localPoint;
/* 5116 */     if (getComponentOrientation().isLeftToRight()) {
/* 5117 */       localPoint = new Point(paramRectangle.x, paramRectangle.y + paramRectangle.height - 1);
/*      */     }
/*      */     else
/*      */     {
/* 5121 */       localPoint = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y + paramRectangle.height - 1);
/*      */     }
/*      */ 
/* 5124 */     return rowAtPoint(localPoint);
/*      */   }
/*      */ 
/*      */   private int getTrailingCol(Rectangle paramRectangle)
/*      */   {
/*      */     Point localPoint;
/* 5135 */     if (getComponentOrientation().isLeftToRight()) {
/* 5136 */       localPoint = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
/*      */     }
/*      */     else
/*      */     {
/* 5140 */       localPoint = new Point(paramRectangle.x, paramRectangle.y);
/*      */     }
/* 5142 */     return columnAtPoint(localPoint);
/*      */   }
/*      */ 
/*      */   private int leadingEdge(Rectangle paramRectangle, int paramInt)
/*      */   {
/* 5151 */     if (paramInt == 1) {
/* 5152 */       return paramRectangle.y;
/*      */     }
/* 5154 */     if (getComponentOrientation().isLeftToRight()) {
/* 5155 */       return paramRectangle.x;
/*      */     }
/*      */ 
/* 5158 */     return paramRectangle.x + paramRectangle.width;
/*      */   }
/*      */ 
/*      */   private int trailingEdge(Rectangle paramRectangle, int paramInt)
/*      */   {
/* 5168 */     if (paramInt == 1) {
/* 5169 */       return paramRectangle.y + paramRectangle.height;
/*      */     }
/* 5171 */     if (getComponentOrientation().isLeftToRight()) {
/* 5172 */       return paramRectangle.x + paramRectangle.width;
/*      */     }
/*      */ 
/* 5175 */     return paramRectangle.x;
/*      */   }
/*      */ 
/*      */   public boolean getScrollableTracksViewportWidth()
/*      */   {
/* 5190 */     return this.autoResizeMode != 0;
/*      */   }
/*      */ 
/*      */   public boolean getScrollableTracksViewportHeight()
/*      */   {
/* 5207 */     Container localContainer = SwingUtilities.getUnwrappedParent(this);
/* 5208 */     return (getFillsViewportHeight()) && ((localContainer instanceof JViewport)) && (localContainer.getHeight() > getPreferredSize().height);
/*      */   }
/*      */ 
/*      */   public void setFillsViewportHeight(boolean paramBoolean)
/*      */   {
/* 5233 */     boolean bool = this.fillsViewportHeight;
/* 5234 */     this.fillsViewportHeight = paramBoolean;
/* 5235 */     resizeAndRepaint();
/* 5236 */     firePropertyChange("fillsViewportHeight", bool, paramBoolean);
/*      */   }
/*      */ 
/*      */   public boolean getFillsViewportHeight()
/*      */   {
/* 5249 */     return this.fillsViewportHeight;
/*      */   }
/*      */ 
/*      */   protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean)
/*      */   {
/* 5258 */     boolean bool = super.processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean);
/*      */ 
/* 5262 */     if ((!bool) && (paramInt == 1) && (isFocusOwner()) && (!Boolean.FALSE.equals(getClientProperty("JTable.autoStartsEdit"))))
/*      */     {
/* 5266 */       Component localComponent = getEditorComponent();
/* 5267 */       if (localComponent == null)
/*      */       {
/* 5269 */         if ((paramKeyEvent == null) || (paramKeyEvent.getID() != 401)) {
/* 5270 */           return false;
/*      */         }
/*      */ 
/* 5273 */         int i = paramKeyEvent.getKeyCode();
/* 5274 */         if ((i == 16) || (i == 17) || (i == 18))
/*      */         {
/* 5276 */           return false;
/*      */         }
/*      */ 
/* 5279 */         int j = getSelectionModel().getLeadSelectionIndex();
/* 5280 */         int k = getColumnModel().getSelectionModel().getLeadSelectionIndex();
/*      */ 
/* 5282 */         if ((j != -1) && (k != -1) && (!isEditing()) && 
/* 5283 */           (!editCellAt(j, k, paramKeyEvent))) {
/* 5284 */           return false;
/*      */         }
/*      */ 
/* 5287 */         localComponent = getEditorComponent();
/* 5288 */         if (localComponent == null) {
/* 5289 */           return false;
/*      */         }
/*      */       }
/*      */ 
/* 5293 */       if ((localComponent instanceof JComponent)) {
/* 5294 */         bool = ((JComponent)localComponent).processKeyBinding(paramKeyStroke, paramKeyEvent, 0, paramBoolean);
/*      */ 
/* 5299 */         if (getSurrendersFocusOnKeystroke()) {
/* 5300 */           localComponent.requestFocus();
/*      */         }
/*      */       }
/*      */     }
/* 5304 */     return bool;
/*      */   }
/*      */ 
/*      */   private void setLazyValue(Hashtable paramHashtable, Class paramClass, String paramString) {
/* 5308 */     paramHashtable.put(paramClass, new SwingLazyValue(paramString));
/*      */   }
/*      */ 
/*      */   private void setLazyRenderer(Class paramClass, String paramString) {
/* 5312 */     setLazyValue(this.defaultRenderersByColumnClass, paramClass, paramString);
/*      */   }
/*      */ 
/*      */   protected void createDefaultRenderers()
/*      */   {
/* 5322 */     this.defaultRenderersByColumnClass = new UIDefaults(8, 0.75F);
/*      */ 
/* 5325 */     setLazyRenderer(Object.class, "javax.swing.table.DefaultTableCellRenderer$UIResource");
/*      */ 
/* 5328 */     setLazyRenderer(Number.class, "javax.swing.JTable$NumberRenderer");
/*      */ 
/* 5331 */     setLazyRenderer(Float.class, "javax.swing.JTable$DoubleRenderer");
/* 5332 */     setLazyRenderer(Double.class, "javax.swing.JTable$DoubleRenderer");
/*      */ 
/* 5335 */     setLazyRenderer(Date.class, "javax.swing.JTable$DateRenderer");
/*      */ 
/* 5338 */     setLazyRenderer(Icon.class, "javax.swing.JTable$IconRenderer");
/* 5339 */     setLazyRenderer(ImageIcon.class, "javax.swing.JTable$IconRenderer");
/*      */ 
/* 5342 */     setLazyRenderer(Boolean.class, "javax.swing.JTable$BooleanRenderer");
/*      */   }
/*      */ 
/*      */   private void setLazyEditor(Class paramClass, String paramString)
/*      */   {
/* 5421 */     setLazyValue(this.defaultEditorsByColumnClass, paramClass, paramString);
/*      */   }
/*      */ 
/*      */   protected void createDefaultEditors()
/*      */   {
/* 5429 */     this.defaultEditorsByColumnClass = new UIDefaults(3, 0.75F);
/*      */ 
/* 5432 */     setLazyEditor(Object.class, "javax.swing.JTable$GenericEditor");
/*      */ 
/* 5435 */     setLazyEditor(Number.class, "javax.swing.JTable$NumberEditor");
/*      */ 
/* 5438 */     setLazyEditor(Boolean.class, "javax.swing.JTable$BooleanEditor");
/*      */   }
/*      */ 
/*      */   protected void initializeLocalVars()
/*      */   {
/* 5526 */     this.updateSelectionOnSort = true;
/* 5527 */     setOpaque(true);
/* 5528 */     createDefaultRenderers();
/* 5529 */     createDefaultEditors();
/*      */ 
/* 5531 */     setTableHeader(createDefaultTableHeader());
/*      */ 
/* 5533 */     setShowGrid(true);
/* 5534 */     setAutoResizeMode(2);
/* 5535 */     setRowHeight(16);
/* 5536 */     this.isRowHeightSet = false;
/* 5537 */     setRowMargin(1);
/* 5538 */     setRowSelectionAllowed(true);
/* 5539 */     setCellEditor(null);
/* 5540 */     setEditingColumn(-1);
/* 5541 */     setEditingRow(-1);
/* 5542 */     setSurrendersFocusOnKeystroke(false);
/* 5543 */     setPreferredScrollableViewportSize(new Dimension(450, 400));
/*      */ 
/* 5546 */     ToolTipManager localToolTipManager = ToolTipManager.sharedInstance();
/* 5547 */     localToolTipManager.registerComponent(this);
/*      */ 
/* 5549 */     setAutoscrolls(true);
/*      */   }
/*      */ 
/*      */   protected TableModel createDefaultDataModel()
/*      */   {
/* 5561 */     return new DefaultTableModel();
/*      */   }
/*      */ 
/*      */   protected TableColumnModel createDefaultColumnModel()
/*      */   {
/* 5573 */     return new DefaultTableColumnModel();
/*      */   }
/*      */ 
/*      */   protected ListSelectionModel createDefaultSelectionModel()
/*      */   {
/* 5585 */     return new DefaultListSelectionModel();
/*      */   }
/*      */ 
/*      */   protected JTableHeader createDefaultTableHeader()
/*      */   {
/* 5597 */     return new JTableHeader(this.columnModel);
/*      */   }
/*      */ 
/*      */   protected void resizeAndRepaint()
/*      */   {
/* 5604 */     revalidate();
/* 5605 */     repaint();
/*      */   }
/*      */ 
/*      */   public TableCellEditor getCellEditor()
/*      */   {
/* 5618 */     return this.cellEditor;
/*      */   }
/*      */ 
/*      */   public void setCellEditor(TableCellEditor paramTableCellEditor)
/*      */   {
/* 5631 */     TableCellEditor localTableCellEditor = this.cellEditor;
/* 5632 */     this.cellEditor = paramTableCellEditor;
/* 5633 */     firePropertyChange("tableCellEditor", localTableCellEditor, paramTableCellEditor);
/*      */   }
/*      */ 
/*      */   public void setEditingColumn(int paramInt)
/*      */   {
/* 5643 */     this.editingColumn = paramInt;
/*      */   }
/*      */ 
/*      */   public void setEditingRow(int paramInt)
/*      */   {
/* 5653 */     this.editingRow = paramInt;
/*      */   }
/*      */ 
/*      */   public TableCellRenderer getCellRenderer(int paramInt1, int paramInt2)
/*      */   {
/* 5679 */     TableColumn localTableColumn = getColumnModel().getColumn(paramInt2);
/* 5680 */     TableCellRenderer localTableCellRenderer = localTableColumn.getCellRenderer();
/* 5681 */     if (localTableCellRenderer == null) {
/* 5682 */       localTableCellRenderer = getDefaultRenderer(getColumnClass(paramInt2));
/*      */     }
/* 5684 */     return localTableCellRenderer;
/*      */   }
/*      */ 
/*      */   public Component prepareRenderer(TableCellRenderer paramTableCellRenderer, int paramInt1, int paramInt2)
/*      */   {
/* 5714 */     Object localObject = getValueAt(paramInt1, paramInt2);
/*      */ 
/* 5716 */     boolean bool1 = false;
/* 5717 */     boolean bool2 = false;
/*      */ 
/* 5720 */     if (!isPaintingForPrint()) {
/* 5721 */       bool1 = isCellSelected(paramInt1, paramInt2);
/*      */ 
/* 5723 */       int i = this.selectionModel.getLeadSelectionIndex() == paramInt1 ? 1 : 0;
/*      */ 
/* 5725 */       int j = this.columnModel.getSelectionModel().getLeadSelectionIndex() == paramInt2 ? 1 : 0;
/*      */ 
/* 5728 */       bool2 = (i != 0) && (j != 0) && (isFocusOwner());
/*      */     }
/*      */ 
/* 5731 */     return paramTableCellRenderer.getTableCellRendererComponent(this, localObject, bool1, bool2, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public TableCellEditor getCellEditor(int paramInt1, int paramInt2)
/*      */   {
/* 5758 */     TableColumn localTableColumn = getColumnModel().getColumn(paramInt2);
/* 5759 */     TableCellEditor localTableCellEditor = localTableColumn.getCellEditor();
/* 5760 */     if (localTableCellEditor == null) {
/* 5761 */       localTableCellEditor = getDefaultEditor(getColumnClass(paramInt2));
/*      */     }
/* 5763 */     return localTableCellEditor;
/*      */   }
/*      */ 
/*      */   public Component prepareEditor(TableCellEditor paramTableCellEditor, int paramInt1, int paramInt2)
/*      */   {
/* 5784 */     Object localObject = getValueAt(paramInt1, paramInt2);
/* 5785 */     boolean bool = isCellSelected(paramInt1, paramInt2);
/* 5786 */     Component localComponent = paramTableCellEditor.getTableCellEditorComponent(this, localObject, bool, paramInt1, paramInt2);
/*      */ 
/* 5788 */     if ((localComponent instanceof JComponent)) {
/* 5789 */       JComponent localJComponent = (JComponent)localComponent;
/* 5790 */       if (localJComponent.getNextFocusableComponent() == null) {
/* 5791 */         localJComponent.setNextFocusableComponent(this);
/*      */       }
/*      */     }
/* 5794 */     return localComponent;
/*      */   }
/*      */ 
/*      */   public void removeEditor()
/*      */   {
/* 5802 */     KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
/*      */ 
/* 5804 */     this.editorRemover = null;
/*      */ 
/* 5806 */     TableCellEditor localTableCellEditor = getCellEditor();
/* 5807 */     if (localTableCellEditor != null) {
/* 5808 */       localTableCellEditor.removeCellEditorListener(this);
/* 5809 */       if (this.editorComp != null) {
/* 5810 */         localObject = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
/*      */ 
/* 5812 */         int i = localObject != null ? SwingUtilities.isDescendingFrom((Component)localObject, this) : 0;
/*      */ 
/* 5814 */         remove(this.editorComp);
/* 5815 */         if (i != 0) {
/* 5816 */           requestFocusInWindow();
/*      */         }
/*      */       }
/*      */ 
/* 5820 */       Object localObject = getCellRect(this.editingRow, this.editingColumn, false);
/*      */ 
/* 5822 */       setCellEditor(null);
/* 5823 */       setEditingColumn(-1);
/* 5824 */       setEditingRow(-1);
/* 5825 */       this.editorComp = null;
/*      */ 
/* 5827 */       repaint((Rectangle)localObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 5840 */     paramObjectOutputStream.defaultWriteObject();
/* 5841 */     if (getUIClassID().equals("TableUI")) {
/* 5842 */       byte b = JComponent.getWriteObjCounter(this);
/* 5843 */       b = (byte)(b - 1); JComponent.setWriteObjCounter(this, b);
/* 5844 */       if ((b == 0) && (this.ui != null))
/* 5845 */         this.ui.installUI(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 5853 */     paramObjectInputStream.defaultReadObject();
/* 5854 */     if ((this.ui != null) && (getUIClassID().equals("TableUI"))) {
/* 5855 */       this.ui.installUI(this);
/*      */     }
/* 5857 */     createDefaultRenderers();
/* 5858 */     createDefaultEditors();
/*      */ 
/* 5863 */     if (getToolTipText() == null)
/* 5864 */       ToolTipManager.sharedInstance().registerComponent(this);
/*      */   }
/*      */ 
/*      */   void compWriteObjectNotify()
/*      */   {
/* 5872 */     super.compWriteObjectNotify();
/*      */ 
/* 5875 */     if (getToolTipText() == null)
/* 5876 */       ToolTipManager.sharedInstance().unregisterComponent(this);
/*      */   }
/*      */ 
/*      */   protected String paramString()
/*      */   {
/* 5890 */     String str1 = this.gridColor != null ? this.gridColor.toString() : "";
/*      */ 
/* 5892 */     String str2 = this.showHorizontalLines ? "true" : "false";
/*      */ 
/* 5894 */     String str3 = this.showVerticalLines ? "true" : "false";
/*      */     String str4;
/* 5897 */     if (this.autoResizeMode == 0)
/* 5898 */       str4 = "AUTO_RESIZE_OFF";
/* 5899 */     else if (this.autoResizeMode == 1)
/* 5900 */       str4 = "AUTO_RESIZE_NEXT_COLUMN";
/* 5901 */     else if (this.autoResizeMode == 2)
/* 5902 */       str4 = "AUTO_RESIZE_SUBSEQUENT_COLUMNS";
/* 5903 */     else if (this.autoResizeMode == 3)
/* 5904 */       str4 = "AUTO_RESIZE_LAST_COLUMN";
/* 5905 */     else if (this.autoResizeMode == 4)
/* 5906 */       str4 = "AUTO_RESIZE_ALL_COLUMNS";
/* 5907 */     else str4 = "";
/* 5908 */     String str5 = this.autoCreateColumnsFromModel ? "true" : "false";
/*      */ 
/* 5910 */     String str6 = this.preferredViewportSize != null ? this.preferredViewportSize.toString() : "";
/*      */ 
/* 5913 */     String str7 = this.rowSelectionAllowed ? "true" : "false";
/*      */ 
/* 5915 */     String str8 = this.cellSelectionEnabled ? "true" : "false";
/*      */ 
/* 5917 */     String str9 = this.selectionForeground != null ? this.selectionForeground.toString() : "";
/*      */ 
/* 5920 */     String str10 = this.selectionBackground != null ? this.selectionBackground.toString() : "";
/*      */ 
/* 5924 */     return super.paramString() + ",autoCreateColumnsFromModel=" + str5 + ",autoResizeMode=" + str4 + ",cellSelectionEnabled=" + str8 + ",editingColumn=" + this.editingColumn + ",editingRow=" + this.editingRow + ",gridColor=" + str1 + ",preferredViewportSize=" + str6 + ",rowHeight=" + this.rowHeight + ",rowMargin=" + this.rowMargin + ",rowSelectionAllowed=" + str7 + ",selectionBackground=" + str10 + ",selectionForeground=" + str9 + ",showHorizontalLines=" + str2 + ",showVerticalLines=" + str3;
/*      */   }
/*      */ 
/*      */   public boolean print()
/*      */     throws PrinterException
/*      */   {
/* 6002 */     return print(PrintMode.FIT_WIDTH);
/*      */   }
/*      */ 
/*      */   public boolean print(PrintMode paramPrintMode)
/*      */     throws PrinterException
/*      */   {
/* 6028 */     return print(paramPrintMode, null, null);
/*      */   }
/*      */ 
/*      */   public boolean print(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
/*      */     throws PrinterException
/*      */   {
/* 6062 */     boolean bool = !GraphicsEnvironment.isHeadless();
/* 6063 */     return print(paramPrintMode, paramMessageFormat1, paramMessageFormat2, bool, null, bool);
/*      */   }
/*      */ 
/*      */   public boolean print(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2, boolean paramBoolean1, PrintRequestAttributeSet paramPrintRequestAttributeSet, boolean paramBoolean2)
/*      */     throws PrinterException, HeadlessException
/*      */   {
/* 6108 */     return print(paramPrintMode, paramMessageFormat1, paramMessageFormat2, paramBoolean1, paramPrintRequestAttributeSet, paramBoolean2, null);
/*      */   }
/*      */ 
/*      */   public boolean print(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2, boolean paramBoolean1, PrintRequestAttributeSet paramPrintRequestAttributeSet, boolean paramBoolean2, PrintService paramPrintService)
/*      */     throws PrinterException, HeadlessException
/*      */   {
/* 6205 */     boolean bool = GraphicsEnvironment.isHeadless();
/* 6206 */     if (bool) {
/* 6207 */       if (paramBoolean1) {
/* 6208 */         throw new HeadlessException("Can't show print dialog.");
/*      */       }
/*      */ 
/* 6211 */       if (paramBoolean2) {
/* 6212 */         throw new HeadlessException("Can't run interactively.");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 6219 */     final PrinterJob localPrinterJob = PrinterJob.getPrinterJob();
/*      */ 
/* 6221 */     if (isEditing())
/*      */     {
/* 6223 */       if (!getCellEditor().stopCellEditing()) {
/* 6224 */         getCellEditor().cancelCellEditing();
/*      */       }
/*      */     }
/*      */ 
/* 6228 */     if (paramPrintRequestAttributeSet == null) {
/* 6229 */       paramPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/*      */     }
/*      */ 
/* 6235 */     Object localObject1 = getPrintable(paramPrintMode, paramMessageFormat1, paramMessageFormat2);
/*      */     final PrintingStatus localPrintingStatus;
/* 6238 */     if (paramBoolean2)
/*      */     {
/* 6240 */       localObject1 = new ThreadSafePrintable((Printable)localObject1);
/* 6241 */       localPrintingStatus = PrintingStatus.createPrintingStatus(this, localPrinterJob);
/* 6242 */       localObject1 = localPrintingStatus.createNotificationPrintable((Printable)localObject1);
/*      */     }
/*      */     else {
/* 6245 */       localPrintingStatus = null;
/*      */     }
/*      */ 
/* 6249 */     localPrinterJob.setPrintable((Printable)localObject1);
/*      */ 
/* 6252 */     if (paramPrintService != null) {
/* 6253 */       localPrinterJob.setPrintService(paramPrintService);
/*      */     }
/*      */ 
/* 6257 */     if ((paramBoolean1) && (!localPrinterJob.printDialog(paramPrintRequestAttributeSet)))
/*      */     {
/* 6259 */       return false;
/*      */     }
/*      */ 
/* 6263 */     if (!paramBoolean2)
/*      */     {
/* 6265 */       localPrinterJob.print(paramPrintRequestAttributeSet);
/*      */ 
/* 6268 */       return true;
/*      */     }
/*      */ 
/* 6272 */     this.printError = null;
/*      */ 
/* 6275 */     final Object localObject2 = new Object();
/*      */ 
/* 6278 */     final PrintRequestAttributeSet localPrintRequestAttributeSet = paramPrintRequestAttributeSet;
/*      */ 
/* 6282 */     Runnable local6 = new Runnable()
/*      */     {
/*      */       public void run() {
/*      */         try {
/* 6286 */           localPrinterJob.print(localPrintRequestAttributeSet);
/*      */         }
/*      */         catch (Throwable localThrowable) {
/* 6289 */           synchronized (localObject2) {
/* 6290 */             JTable.this.printError = localThrowable;
/*      */           }
/*      */         }
/*      */         finally {
/* 6294 */           localPrintingStatus.dispose();
/*      */         }
/*      */       }
/*      */     };
/* 6300 */     Thread localThread = new Thread(local6);
/* 6301 */     localThread.start();
/*      */ 
/* 6303 */     localPrintingStatus.showModal(true);
/*      */     Throwable localThrowable;
/* 6307 */     synchronized (localObject2) {
/* 6308 */       localThrowable = this.printError;
/* 6309 */       this.printError = null;
/*      */     }
/*      */ 
/* 6313 */     if (localThrowable != null)
/*      */     {
/* 6316 */       if ((localThrowable instanceof PrinterAbortException))
/* 6317 */         return false;
/* 6318 */       if ((localThrowable instanceof PrinterException))
/* 6319 */         throw ((PrinterException)localThrowable);
/* 6320 */       if ((localThrowable instanceof RuntimeException))
/* 6321 */         throw ((RuntimeException)localThrowable);
/* 6322 */       if ((localThrowable instanceof Error)) {
/* 6323 */         throw ((Error)localThrowable);
/*      */       }
/*      */ 
/* 6327 */       throw new AssertionError(localThrowable);
/*      */     }
/*      */ 
/* 6330 */     return true;
/*      */   }
/*      */ 
/*      */   public Printable getPrintable(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
/*      */   {
/* 6438 */     return new TablePrintable(this, paramPrintMode, paramMessageFormat1, paramMessageFormat2);
/*      */   }
/*      */ 
/*      */   public AccessibleContext getAccessibleContext()
/*      */   {
/* 6556 */     if (this.accessibleContext == null) {
/* 6557 */       this.accessibleContext = new AccessibleJTable();
/*      */     }
/* 6559 */     return this.accessibleContext;
/*      */   }
/*      */ 
/*      */   protected class AccessibleJTable extends JComponent.AccessibleJComponent
/*      */     implements AccessibleSelection, ListSelectionListener, TableModelListener, TableColumnModelListener, CellEditorListener, PropertyChangeListener, AccessibleExtendedTable
/*      */   {
/*      */     int lastSelectedRow;
/*      */     int lastSelectedCol;
/*      */     private Accessible caption;
/*      */     private Accessible summary;
/*      */     private Accessible[] rowDescription;
/*      */     private Accessible[] columnDescription;
/*      */ 
/*      */     protected AccessibleJTable()
/*      */     {
/* 6594 */       super();
/* 6595 */       JTable.this.addPropertyChangeListener(this);
/* 6596 */       JTable.this.getSelectionModel().addListSelectionListener(this);
/* 6597 */       TableColumnModel localTableColumnModel = JTable.this.getColumnModel();
/* 6598 */       localTableColumnModel.addColumnModelListener(this);
/* 6599 */       localTableColumnModel.getSelectionModel().addListSelectionListener(this);
/* 6600 */       JTable.this.getModel().addTableModelListener(this);
/* 6601 */       this.lastSelectedRow = JTable.this.getSelectedRow();
/* 6602 */       this.lastSelectedCol = JTable.this.getSelectedColumn();
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/* 6614 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 6615 */       Object localObject1 = paramPropertyChangeEvent.getOldValue();
/* 6616 */       Object localObject2 = paramPropertyChangeEvent.getNewValue();
/*      */ 
/* 6619 */       if (str.compareTo("model") == 0)
/*      */       {
/* 6621 */         if ((localObject1 != null) && ((localObject1 instanceof TableModel))) {
/* 6622 */           ((TableModel)localObject1).removeTableModelListener(this);
/*      */         }
/* 6624 */         if ((localObject2 != null) && ((localObject2 instanceof TableModel)))
/* 6625 */           ((TableModel)localObject2).addTableModelListener(this);
/*      */       }
/*      */       else
/*      */       {
/*      */         Object localObject3;
/* 6629 */         if (str.compareTo("selectionModel") == 0)
/*      */         {
/* 6631 */           localObject3 = paramPropertyChangeEvent.getSource();
/* 6632 */           if (localObject3 == JTable.this)
/*      */           {
/* 6634 */             if ((localObject1 != null) && ((localObject1 instanceof ListSelectionModel)))
/*      */             {
/* 6636 */               ((ListSelectionModel)localObject1).removeListSelectionListener(this);
/*      */             }
/* 6638 */             if ((localObject2 != null) && ((localObject2 instanceof ListSelectionModel)))
/*      */             {
/* 6640 */               ((ListSelectionModel)localObject2).addListSelectionListener(this);
/*      */             }
/*      */           }
/* 6643 */           else if (localObject3 == JTable.this.getColumnModel())
/*      */           {
/* 6645 */             if ((localObject1 != null) && ((localObject1 instanceof ListSelectionModel)))
/*      */             {
/* 6647 */               ((ListSelectionModel)localObject1).removeListSelectionListener(this);
/*      */             }
/* 6649 */             if ((localObject2 != null) && ((localObject2 instanceof ListSelectionModel)))
/*      */             {
/* 6651 */               ((ListSelectionModel)localObject2).addListSelectionListener(this);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/* 6660 */         else if (str.compareTo("columnModel") == 0)
/*      */         {
/* 6662 */           if ((localObject1 != null) && ((localObject1 instanceof TableColumnModel))) {
/* 6663 */             localObject3 = (TableColumnModel)localObject1;
/* 6664 */             ((TableColumnModel)localObject3).removeColumnModelListener(this);
/* 6665 */             ((TableColumnModel)localObject3).getSelectionModel().removeListSelectionListener(this);
/*      */           }
/* 6667 */           if ((localObject2 != null) && ((localObject2 instanceof TableColumnModel))) {
/* 6668 */             localObject3 = (TableColumnModel)localObject2;
/* 6669 */             ((TableColumnModel)localObject3).addColumnModelListener(this);
/* 6670 */             ((TableColumnModel)localObject3).getSelectionModel().addListSelectionListener(this);
/*      */           }
/*      */ 
/*      */         }
/* 6674 */         else if (str.compareTo("tableCellEditor") == 0)
/*      */         {
/* 6676 */           if ((localObject1 != null) && ((localObject1 instanceof TableCellEditor))) {
/* 6677 */             ((TableCellEditor)localObject1).removeCellEditorListener(this);
/*      */           }
/* 6679 */           if ((localObject2 != null) && ((localObject2 instanceof TableCellEditor)))
/* 6680 */             ((TableCellEditor)localObject2).addCellEditorListener(this);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void tableChanged(TableModelEvent paramTableModelEvent)
/*      */     {
/* 6735 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */ 
/* 6737 */       if (paramTableModelEvent != null) {
/* 6738 */         int i = paramTableModelEvent.getColumn();
/* 6739 */         int j = paramTableModelEvent.getColumn();
/* 6740 */         if (i == -1) {
/* 6741 */           i = 0;
/* 6742 */           j = JTable.this.getColumnCount() - 1;
/*      */         }
/*      */ 
/* 6747 */         AccessibleJTableModelChange localAccessibleJTableModelChange = new AccessibleJTableModelChange(paramTableModelEvent.getType(), paramTableModelEvent.getFirstRow(), paramTableModelEvent.getLastRow(), i, j);
/*      */ 
/* 6753 */         firePropertyChange("accessibleTableModelChanged", null, localAccessibleJTableModelChange);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void tableRowsInserted(TableModelEvent paramTableModelEvent)
/*      */     {
/* 6762 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */ 
/* 6767 */       int i = paramTableModelEvent.getColumn();
/* 6768 */       int j = paramTableModelEvent.getColumn();
/* 6769 */       if (i == -1) {
/* 6770 */         i = 0;
/* 6771 */         j = JTable.this.getColumnCount() - 1;
/*      */       }
/* 6773 */       AccessibleJTableModelChange localAccessibleJTableModelChange = new AccessibleJTableModelChange(paramTableModelEvent.getType(), paramTableModelEvent.getFirstRow(), paramTableModelEvent.getLastRow(), i, j);
/*      */ 
/* 6779 */       firePropertyChange("accessibleTableModelChanged", null, localAccessibleJTableModelChange);
/*      */     }
/*      */ 
/*      */     public void tableRowsDeleted(TableModelEvent paramTableModelEvent)
/*      */     {
/* 6787 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */ 
/* 6792 */       int i = paramTableModelEvent.getColumn();
/* 6793 */       int j = paramTableModelEvent.getColumn();
/* 6794 */       if (i == -1) {
/* 6795 */         i = 0;
/* 6796 */         j = JTable.this.getColumnCount() - 1;
/*      */       }
/* 6798 */       AccessibleJTableModelChange localAccessibleJTableModelChange = new AccessibleJTableModelChange(paramTableModelEvent.getType(), paramTableModelEvent.getFirstRow(), paramTableModelEvent.getLastRow(), i, j);
/*      */ 
/* 6804 */       firePropertyChange("accessibleTableModelChanged", null, localAccessibleJTableModelChange);
/*      */     }
/*      */ 
/*      */     public void columnAdded(TableColumnModelEvent paramTableColumnModelEvent)
/*      */     {
/* 6812 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */ 
/* 6817 */       int i = 1;
/* 6818 */       AccessibleJTableModelChange localAccessibleJTableModelChange = new AccessibleJTableModelChange(i, 0, 0, paramTableColumnModelEvent.getFromIndex(), paramTableColumnModelEvent.getToIndex());
/*      */ 
/* 6824 */       firePropertyChange("accessibleTableModelChanged", null, localAccessibleJTableModelChange);
/*      */     }
/*      */ 
/*      */     public void columnRemoved(TableColumnModelEvent paramTableColumnModelEvent)
/*      */     {
/* 6832 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */ 
/* 6836 */       int i = -1;
/* 6837 */       AccessibleJTableModelChange localAccessibleJTableModelChange = new AccessibleJTableModelChange(i, 0, 0, paramTableColumnModelEvent.getFromIndex(), paramTableColumnModelEvent.getToIndex());
/*      */ 
/* 6843 */       firePropertyChange("accessibleTableModelChanged", null, localAccessibleJTableModelChange);
/*      */     }
/*      */ 
/*      */     public void columnMoved(TableColumnModelEvent paramTableColumnModelEvent)
/*      */     {
/* 6853 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */ 
/* 6858 */       int i = -1;
/* 6859 */       AccessibleJTableModelChange localAccessibleJTableModelChange1 = new AccessibleJTableModelChange(i, 0, 0, paramTableColumnModelEvent.getFromIndex(), paramTableColumnModelEvent.getFromIndex());
/*      */ 
/* 6865 */       firePropertyChange("accessibleTableModelChanged", null, localAccessibleJTableModelChange1);
/*      */ 
/* 6868 */       int j = 1;
/* 6869 */       AccessibleJTableModelChange localAccessibleJTableModelChange2 = new AccessibleJTableModelChange(j, 0, 0, paramTableColumnModelEvent.getToIndex(), paramTableColumnModelEvent.getToIndex());
/*      */ 
/* 6875 */       firePropertyChange("accessibleTableModelChanged", null, localAccessibleJTableModelChange2);
/*      */     }
/*      */ 
/*      */     public void columnMarginChanged(ChangeEvent paramChangeEvent)
/*      */     {
/* 6885 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */     }
/*      */ 
/*      */     public void columnSelectionChanged(ListSelectionEvent paramListSelectionEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void editingStopped(ChangeEvent paramChangeEvent)
/*      */     {
/* 6909 */       firePropertyChange("AccessibleVisibleData", null, null);
/*      */     }
/*      */ 
/*      */     public void editingCanceled(ChangeEvent paramChangeEvent)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void valueChanged(ListSelectionEvent paramListSelectionEvent)
/*      */     {
/* 6927 */       firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
/*      */ 
/* 6930 */       int i = JTable.this.getSelectedRow();
/* 6931 */       int j = JTable.this.getSelectedColumn();
/* 6932 */       if ((i != this.lastSelectedRow) || (j != this.lastSelectedCol))
/*      */       {
/* 6934 */         Accessible localAccessible1 = getAccessibleAt(this.lastSelectedRow, this.lastSelectedCol);
/*      */ 
/* 6936 */         Accessible localAccessible2 = getAccessibleAt(i, j);
/* 6937 */         firePropertyChange("AccessibleActiveDescendant", localAccessible1, localAccessible2);
/*      */ 
/* 6939 */         this.lastSelectedRow = i;
/* 6940 */         this.lastSelectedCol = j;
/*      */       }
/*      */     }
/*      */ 
/*      */     public AccessibleSelection getAccessibleSelection()
/*      */     {
/* 6958 */       return this;
/*      */     }
/*      */ 
/*      */     public AccessibleRole getAccessibleRole()
/*      */     {
/* 6969 */       return AccessibleRole.TABLE;
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleAt(Point paramPoint)
/*      */     {
/* 6983 */       int i = JTable.this.columnAtPoint(paramPoint);
/* 6984 */       int j = JTable.this.rowAtPoint(paramPoint);
/*      */ 
/* 6986 */       if ((i != -1) && (j != -1)) {
/* 6987 */         TableColumn localTableColumn = JTable.this.getColumnModel().getColumn(i);
/* 6988 */         TableCellRenderer localTableCellRenderer = localTableColumn.getCellRenderer();
/* 6989 */         if (localTableCellRenderer == null) {
/* 6990 */           localObject = JTable.this.getColumnClass(i);
/* 6991 */           localTableCellRenderer = JTable.this.getDefaultRenderer((Class)localObject);
/*      */         }
/* 6993 */         Object localObject = localTableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, j, i);
/*      */ 
/* 6996 */         return new AccessibleJTableCell(JTable.this, j, i, getAccessibleIndexAt(j, i));
/*      */       }
/*      */ 
/* 6999 */       return null;
/*      */     }
/*      */ 
/*      */     public int getAccessibleChildrenCount()
/*      */     {
/* 7010 */       return JTable.this.getColumnCount() * JTable.this.getRowCount();
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleChild(int paramInt)
/*      */     {
/* 7020 */       if ((paramInt < 0) || (paramInt >= getAccessibleChildrenCount())) {
/* 7021 */         return null;
/*      */       }
/*      */ 
/* 7025 */       int i = getAccessibleColumnAtIndex(paramInt);
/* 7026 */       int j = getAccessibleRowAtIndex(paramInt);
/*      */ 
/* 7028 */       TableColumn localTableColumn = JTable.this.getColumnModel().getColumn(i);
/* 7029 */       TableCellRenderer localTableCellRenderer = localTableColumn.getCellRenderer();
/* 7030 */       if (localTableCellRenderer == null) {
/* 7031 */         localObject = JTable.this.getColumnClass(i);
/* 7032 */         localTableCellRenderer = JTable.this.getDefaultRenderer((Class)localObject);
/*      */       }
/* 7034 */       Object localObject = localTableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, j, i);
/*      */ 
/* 7037 */       return new AccessibleJTableCell(JTable.this, j, i, getAccessibleIndexAt(j, i));
/*      */     }
/*      */ 
/*      */     public int getAccessibleSelectionCount()
/*      */     {
/* 7052 */       int i = JTable.this.getSelectedRowCount();
/* 7053 */       int j = JTable.this.getSelectedColumnCount();
/*      */ 
/* 7055 */       if (JTable.this.cellSelectionEnabled) {
/* 7056 */         return i * j;
/*      */       }
/*      */ 
/* 7060 */       if ((JTable.this.getRowSelectionAllowed()) && (JTable.this.getColumnSelectionAllowed()))
/*      */       {
/* 7062 */         return i * JTable.this.getColumnCount() + j * JTable.this.getRowCount() - i * j;
/*      */       }
/*      */ 
/* 7067 */       if (JTable.this.getRowSelectionAllowed()) {
/* 7068 */         return i * JTable.this.getColumnCount();
/*      */       }
/*      */ 
/* 7071 */       if (JTable.this.getColumnSelectionAllowed()) {
/* 7072 */         return j * JTable.this.getRowCount();
/*      */       }
/*      */ 
/* 7075 */       return 0;
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleSelection(int paramInt)
/*      */     {
/* 7094 */       if ((paramInt < 0) || (paramInt > getAccessibleSelectionCount())) {
/* 7095 */         return null;
/*      */       }
/*      */ 
/* 7098 */       int i = JTable.this.getSelectedRowCount();
/* 7099 */       int j = JTable.this.getSelectedColumnCount();
/* 7100 */       int[] arrayOfInt1 = JTable.this.getSelectedRows();
/* 7101 */       int[] arrayOfInt2 = JTable.this.getSelectedColumns();
/* 7102 */       int k = JTable.this.getColumnCount();
/* 7103 */       int m = JTable.this.getRowCount();
/*      */       int n;
/*      */       int i1;
/* 7107 */       if (JTable.this.cellSelectionEnabled) {
/* 7108 */         n = arrayOfInt1[(paramInt / j)];
/* 7109 */         i1 = arrayOfInt2[(paramInt % j)];
/* 7110 */         return getAccessibleChild(n * k + i1);
/*      */       }
/*      */ 
/* 7114 */       if ((JTable.this.getRowSelectionAllowed()) && (JTable.this.getColumnSelectionAllowed()))
/*      */       {
/* 7138 */         int i2 = paramInt;
/*      */ 
/* 7141 */         int i3 = arrayOfInt1[0] == 0 ? 0 : 1;
/* 7142 */         int i4 = 0;
/* 7143 */         int i5 = -1;
/* 7144 */         while (i4 < arrayOfInt1.length) {
/* 7145 */           switch (i3)
/*      */           {
/*      */           case 0:
/* 7148 */             if (i2 < k) {
/* 7149 */               i1 = i2 % k;
/* 7150 */               n = arrayOfInt1[i4];
/* 7151 */               return getAccessibleChild(n * k + i1);
/*      */             }
/* 7153 */             i2 -= k;
/*      */ 
/* 7156 */             if ((i4 + 1 == arrayOfInt1.length) || (arrayOfInt1[i4] != arrayOfInt1[(i4 + 1)] - 1))
/*      */             {
/* 7158 */               i3 = 1;
/* 7159 */               i5 = arrayOfInt1[i4];
/*      */             }
/* 7161 */             i4++;
/* 7162 */             break;
/*      */           case 1:
/* 7165 */             if (i2 < j * (arrayOfInt1[i4] - (i5 == -1 ? 0 : i5 + 1)))
/*      */             {
/* 7170 */               i1 = arrayOfInt2[(i2 % j)];
/* 7171 */               n = (i4 > 0 ? arrayOfInt1[(i4 - 1)] + 1 : 0) + i2 / j;
/*      */ 
/* 7173 */               return getAccessibleChild(n * k + i1);
/*      */             }
/* 7175 */             i2 -= j * (arrayOfInt1[i4] - (i5 == -1 ? 0 : i5 + 1));
/*      */ 
/* 7178 */             i3 = 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 7184 */         if (i2 < j * (m - (i5 == -1 ? 0 : i5 + 1)))
/*      */         {
/* 7187 */           i1 = arrayOfInt2[(i2 % j)];
/* 7188 */           n = arrayOfInt1[(i4 - 1)] + i2 / j + 1;
/* 7189 */           return getAccessibleChild(n * k + i1);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 7196 */         if (JTable.this.getRowSelectionAllowed()) {
/* 7197 */           i1 = paramInt % k;
/* 7198 */           n = arrayOfInt1[(paramInt / k)];
/* 7199 */           return getAccessibleChild(n * k + i1);
/*      */         }
/*      */ 
/* 7202 */         if (JTable.this.getColumnSelectionAllowed()) {
/* 7203 */           i1 = arrayOfInt2[(paramInt % j)];
/* 7204 */           n = paramInt / j;
/* 7205 */           return getAccessibleChild(n * k + i1);
/*      */         }
/*      */       }
/* 7208 */       return null;
/*      */     }
/*      */ 
/*      */     public boolean isAccessibleChildSelected(int paramInt)
/*      */     {
/* 7220 */       int i = getAccessibleColumnAtIndex(paramInt);
/* 7221 */       int j = getAccessibleRowAtIndex(paramInt);
/* 7222 */       return JTable.this.isCellSelected(j, i);
/*      */     }
/*      */ 
/*      */     public void addAccessibleSelection(int paramInt)
/*      */     {
/* 7241 */       int i = getAccessibleColumnAtIndex(paramInt);
/* 7242 */       int j = getAccessibleRowAtIndex(paramInt);
/* 7243 */       JTable.this.changeSelection(j, i, true, false);
/*      */     }
/*      */ 
/*      */     public void removeAccessibleSelection(int paramInt)
/*      */     {
/* 7258 */       if (JTable.this.cellSelectionEnabled) {
/* 7259 */         int i = getAccessibleColumnAtIndex(paramInt);
/* 7260 */         int j = getAccessibleRowAtIndex(paramInt);
/* 7261 */         JTable.this.removeRowSelectionInterval(j, j);
/* 7262 */         JTable.this.removeColumnSelectionInterval(i, i);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void clearAccessibleSelection()
/*      */     {
/* 7271 */       JTable.this.clearSelection();
/*      */     }
/*      */ 
/*      */     public void selectAllAccessibleSelection()
/*      */     {
/* 7280 */       if (JTable.this.cellSelectionEnabled)
/* 7281 */         JTable.this.selectAll();
/*      */     }
/*      */ 
/*      */     public int getAccessibleRow(int paramInt)
/*      */     {
/* 7296 */       return getAccessibleRowAtIndex(paramInt);
/*      */     }
/*      */ 
/*      */     public int getAccessibleColumn(int paramInt)
/*      */     {
/* 7308 */       return getAccessibleColumnAtIndex(paramInt);
/*      */     }
/*      */ 
/*      */     public int getAccessibleIndex(int paramInt1, int paramInt2)
/*      */     {
/* 7321 */       return getAccessibleIndexAt(paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public AccessibleTable getAccessibleTable()
/*      */     {
/* 7344 */       return this;
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleCaption()
/*      */     {
/* 7354 */       return this.caption;
/*      */     }
/*      */ 
/*      */     public void setAccessibleCaption(Accessible paramAccessible)
/*      */     {
/* 7364 */       Accessible localAccessible = this.caption;
/* 7365 */       this.caption = paramAccessible;
/* 7366 */       firePropertyChange("accessibleTableCaptionChanged", localAccessible, this.caption);
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleSummary()
/*      */     {
/* 7377 */       return this.summary;
/*      */     }
/*      */ 
/*      */     public void setAccessibleSummary(Accessible paramAccessible)
/*      */     {
/* 7387 */       Accessible localAccessible = this.summary;
/* 7388 */       this.summary = paramAccessible;
/* 7389 */       firePropertyChange("accessibleTableSummaryChanged", localAccessible, this.summary);
/*      */     }
/*      */ 
/*      */     public int getAccessibleRowCount()
/*      */     {
/* 7399 */       return JTable.this.getRowCount();
/*      */     }
/*      */ 
/*      */     public int getAccessibleColumnCount()
/*      */     {
/* 7408 */       return JTable.this.getColumnCount();
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleAt(int paramInt1, int paramInt2)
/*      */     {
/* 7421 */       return getAccessibleChild(paramInt1 * getAccessibleColumnCount() + paramInt2);
/*      */     }
/*      */ 
/*      */     public int getAccessibleRowExtentAt(int paramInt1, int paramInt2)
/*      */     {
/* 7433 */       return 1;
/*      */     }
/*      */ 
/*      */     public int getAccessibleColumnExtentAt(int paramInt1, int paramInt2)
/*      */     {
/* 7445 */       return 1;
/*      */     }
/*      */ 
/*      */     public AccessibleTable getAccessibleRowHeader()
/*      */     {
/* 7457 */       return null;
/*      */     }
/*      */ 
/*      */     public void setAccessibleRowHeader(AccessibleTable paramAccessibleTable)
/*      */     {
/*      */     }
/*      */ 
/*      */     public AccessibleTable getAccessibleColumnHeader()
/*      */     {
/* 7480 */       JTableHeader localJTableHeader = JTable.this.getTableHeader();
/* 7481 */       return localJTableHeader == null ? null : new AccessibleTableHeader(localJTableHeader);
/*      */     }
/*      */ 
/*      */     public void setAccessibleColumnHeader(AccessibleTable paramAccessibleTable)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleRowDescription(int paramInt)
/*      */     {
/* 7729 */       if ((paramInt < 0) || (paramInt >= getAccessibleRowCount())) {
/* 7730 */         throw new IllegalArgumentException(Integer.toString(paramInt));
/*      */       }
/* 7732 */       if (this.rowDescription == null) {
/* 7733 */         return null;
/*      */       }
/* 7735 */       return this.rowDescription[paramInt];
/*      */     }
/*      */ 
/*      */     public void setAccessibleRowDescription(int paramInt, Accessible paramAccessible)
/*      */     {
/* 7747 */       if ((paramInt < 0) || (paramInt >= getAccessibleRowCount())) {
/* 7748 */         throw new IllegalArgumentException(Integer.toString(paramInt));
/*      */       }
/* 7750 */       if (this.rowDescription == null) {
/* 7751 */         int i = getAccessibleRowCount();
/* 7752 */         this.rowDescription = new Accessible[i];
/*      */       }
/* 7754 */       this.rowDescription[paramInt] = paramAccessible;
/*      */     }
/*      */ 
/*      */     public Accessible getAccessibleColumnDescription(int paramInt)
/*      */     {
/* 7765 */       if ((paramInt < 0) || (paramInt >= getAccessibleColumnCount())) {
/* 7766 */         throw new IllegalArgumentException(Integer.toString(paramInt));
/*      */       }
/* 7768 */       if (this.columnDescription == null) {
/* 7769 */         return null;
/*      */       }
/* 7771 */       return this.columnDescription[paramInt];
/*      */     }
/*      */ 
/*      */     public void setAccessibleColumnDescription(int paramInt, Accessible paramAccessible)
/*      */     {
/* 7783 */       if ((paramInt < 0) || (paramInt >= getAccessibleColumnCount())) {
/* 7784 */         throw new IllegalArgumentException(Integer.toString(paramInt));
/*      */       }
/* 7786 */       if (this.columnDescription == null) {
/* 7787 */         int i = getAccessibleColumnCount();
/* 7788 */         this.columnDescription = new Accessible[i];
/*      */       }
/* 7790 */       this.columnDescription[paramInt] = paramAccessible;
/*      */     }
/*      */ 
/*      */     public boolean isAccessibleSelected(int paramInt1, int paramInt2)
/*      */     {
/* 7804 */       return JTable.this.isCellSelected(paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public boolean isAccessibleRowSelected(int paramInt)
/*      */     {
/* 7817 */       return JTable.this.isRowSelected(paramInt);
/*      */     }
/*      */ 
/*      */     public boolean isAccessibleColumnSelected(int paramInt)
/*      */     {
/* 7830 */       return JTable.this.isColumnSelected(paramInt);
/*      */     }
/*      */ 
/*      */     public int[] getSelectedAccessibleRows()
/*      */     {
/* 7841 */       return JTable.this.getSelectedRows();
/*      */     }
/*      */ 
/*      */     public int[] getSelectedAccessibleColumns()
/*      */     {
/* 7852 */       return JTable.this.getSelectedColumns();
/*      */     }
/*      */ 
/*      */     public int getAccessibleRowAtIndex(int paramInt)
/*      */     {
/* 7863 */       int i = getAccessibleColumnCount();
/* 7864 */       if (i == 0) {
/* 7865 */         return -1;
/*      */       }
/* 7867 */       return paramInt / i;
/*      */     }
/*      */ 
/*      */     public int getAccessibleColumnAtIndex(int paramInt)
/*      */     {
/* 7879 */       int i = getAccessibleColumnCount();
/* 7880 */       if (i == 0) {
/* 7881 */         return -1;
/*      */       }
/* 7883 */       return paramInt % i;
/*      */     }
/*      */ 
/*      */     public int getAccessibleIndexAt(int paramInt1, int paramInt2)
/*      */     {
/* 7896 */       return paramInt1 * getAccessibleColumnCount() + paramInt2;
/*      */     }
/*      */ 
/*      */     protected class AccessibleJTableCell extends AccessibleContext
/*      */       implements Accessible, AccessibleComponent
/*      */     {
/*      */       private JTable parent;
/*      */       private int row;
/*      */       private int column;
/*      */       private int index;
/*      */ 
/*      */       public AccessibleJTableCell(JTable paramInt1, int paramInt2, int paramInt3, int arg5)
/*      */       {
/* 7918 */         this.parent = paramInt1;
/* 7919 */         this.row = paramInt2;
/* 7920 */         this.column = paramInt3;
/*      */         int i;
/* 7921 */         this.index = i;
/* 7922 */         setAccessibleParent(this.parent);
/*      */       }
/*      */ 
/*      */       public AccessibleContext getAccessibleContext()
/*      */       {
/* 7934 */         return this;
/*      */       }
/*      */ 
/*      */       protected AccessibleContext getCurrentAccessibleContext()
/*      */       {
/* 7946 */         TableColumn localTableColumn = JTable.this.getColumnModel().getColumn(this.column);
/* 7947 */         TableCellRenderer localTableCellRenderer = localTableColumn.getCellRenderer();
/* 7948 */         if (localTableCellRenderer == null) {
/* 7949 */           localObject = JTable.this.getColumnClass(this.column);
/* 7950 */           localTableCellRenderer = JTable.this.getDefaultRenderer((Class)localObject);
/*      */         }
/* 7952 */         Object localObject = localTableCellRenderer.getTableCellRendererComponent(JTable.this, JTable.this.getValueAt(this.row, this.column), false, false, this.row, this.column);
/*      */ 
/* 7955 */         if ((localObject instanceof AccessibleComponent)) {
/* 7956 */           return ((Component)localObject).getAccessibleContext();
/*      */         }
/* 7958 */         return null;
/*      */       }
/*      */ 
/*      */       protected Component getCurrentComponent()
/*      */       {
/* 7970 */         TableColumn localTableColumn = JTable.this.getColumnModel().getColumn(this.column);
/* 7971 */         TableCellRenderer localTableCellRenderer = localTableColumn.getCellRenderer();
/* 7972 */         if (localTableCellRenderer == null) {
/* 7973 */           Class localClass = JTable.this.getColumnClass(this.column);
/* 7974 */           localTableCellRenderer = JTable.this.getDefaultRenderer(localClass);
/*      */         }
/* 7976 */         return localTableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, this.row, this.column);
/*      */       }
/*      */ 
/*      */       public String getAccessibleName()
/*      */       {
/* 7990 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 7991 */         if (localAccessibleContext != null) {
/* 7992 */           String str = localAccessibleContext.getAccessibleName();
/* 7993 */           if ((str != null) && (str != ""))
/*      */           {
/* 7995 */             return str;
/*      */           }
/*      */         }
/* 7998 */         if ((this.accessibleName != null) && (this.accessibleName != "")) {
/* 7999 */           return this.accessibleName;
/*      */         }
/*      */ 
/* 8002 */         return (String)JTable.this.getClientProperty("AccessibleName");
/*      */       }
/*      */ 
/*      */       public void setAccessibleName(String paramString)
/*      */       {
/* 8012 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8013 */         if (localAccessibleContext != null)
/* 8014 */           localAccessibleContext.setAccessibleName(paramString);
/*      */         else
/* 8016 */           super.setAccessibleName(paramString);
/*      */       }
/*      */ 
/*      */       public String getAccessibleDescription()
/*      */       {
/* 8031 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8032 */         if (localAccessibleContext != null) {
/* 8033 */           return localAccessibleContext.getAccessibleDescription();
/*      */         }
/* 8035 */         return super.getAccessibleDescription();
/*      */       }
/*      */ 
/*      */       public void setAccessibleDescription(String paramString)
/*      */       {
/* 8045 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8046 */         if (localAccessibleContext != null)
/* 8047 */           localAccessibleContext.setAccessibleDescription(paramString);
/*      */         else
/* 8049 */           super.setAccessibleDescription(paramString);
/*      */       }
/*      */ 
/*      */       public AccessibleRole getAccessibleRole()
/*      */       {
/* 8061 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8062 */         if (localAccessibleContext != null) {
/* 8063 */           return localAccessibleContext.getAccessibleRole();
/*      */         }
/* 8065 */         return AccessibleRole.UNKNOWN;
/*      */       }
/*      */ 
/*      */       public AccessibleStateSet getAccessibleStateSet()
/*      */       {
/* 8077 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8078 */         AccessibleStateSet localAccessibleStateSet = null;
/*      */ 
/* 8080 */         if (localAccessibleContext != null) {
/* 8081 */           localAccessibleStateSet = localAccessibleContext.getAccessibleStateSet();
/*      */         }
/* 8083 */         if (localAccessibleStateSet == null) {
/* 8084 */           localAccessibleStateSet = new AccessibleStateSet();
/*      */         }
/* 8086 */         Rectangle localRectangle1 = JTable.this.getVisibleRect();
/* 8087 */         Rectangle localRectangle2 = JTable.this.getCellRect(this.row, this.column, false);
/* 8088 */         if (localRectangle1.intersects(localRectangle2)) {
/* 8089 */           localAccessibleStateSet.add(AccessibleState.SHOWING);
/*      */         }
/* 8091 */         else if (localAccessibleStateSet.contains(AccessibleState.SHOWING)) {
/* 8092 */           localAccessibleStateSet.remove(AccessibleState.SHOWING);
/*      */         }
/*      */ 
/* 8095 */         if (this.parent.isCellSelected(this.row, this.column))
/* 8096 */           localAccessibleStateSet.add(AccessibleState.SELECTED);
/* 8097 */         else if (localAccessibleStateSet.contains(AccessibleState.SELECTED)) {
/* 8098 */           localAccessibleStateSet.remove(AccessibleState.SELECTED);
/*      */         }
/* 8100 */         if ((this.row == JTable.this.getSelectedRow()) && (this.column == JTable.this.getSelectedColumn())) {
/* 8101 */           localAccessibleStateSet.add(AccessibleState.ACTIVE);
/*      */         }
/* 8103 */         localAccessibleStateSet.add(AccessibleState.TRANSIENT);
/* 8104 */         return localAccessibleStateSet;
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleParent()
/*      */       {
/* 8115 */         return this.parent;
/*      */       }
/*      */ 
/*      */       public int getAccessibleIndexInParent()
/*      */       {
/* 8126 */         return this.index;
/*      */       }
/*      */ 
/*      */       public int getAccessibleChildrenCount()
/*      */       {
/* 8135 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8136 */         if (localAccessibleContext != null) {
/* 8137 */           return localAccessibleContext.getAccessibleChildrenCount();
/*      */         }
/* 8139 */         return 0;
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleChild(int paramInt)
/*      */       {
/* 8151 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8152 */         if (localAccessibleContext != null) {
/* 8153 */           Accessible localAccessible = localAccessibleContext.getAccessibleChild(paramInt);
/* 8154 */           localAccessibleContext.setAccessibleParent(this);
/* 8155 */           return localAccessible;
/*      */         }
/* 8157 */         return null;
/*      */       }
/*      */ 
/*      */       public Locale getLocale()
/*      */       {
/* 8176 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8177 */         if (localAccessibleContext != null) {
/* 8178 */           return localAccessibleContext.getLocale();
/*      */         }
/* 8180 */         return null;
/*      */       }
/*      */ 
/*      */       public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */       {
/* 8192 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8193 */         if (localAccessibleContext != null)
/* 8194 */           localAccessibleContext.addPropertyChangeListener(paramPropertyChangeListener);
/*      */         else
/* 8196 */           super.addPropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */ 
/*      */       public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */       {
/* 8209 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8210 */         if (localAccessibleContext != null)
/* 8211 */           localAccessibleContext.removePropertyChangeListener(paramPropertyChangeListener);
/*      */         else
/* 8213 */           super.removePropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */ 
/*      */       public AccessibleAction getAccessibleAction()
/*      */       {
/* 8224 */         return getCurrentAccessibleContext().getAccessibleAction();
/*      */       }
/*      */ 
/*      */       public AccessibleComponent getAccessibleComponent()
/*      */       {
/* 8235 */         return this;
/*      */       }
/*      */ 
/*      */       public AccessibleSelection getAccessibleSelection()
/*      */       {
/* 8246 */         return getCurrentAccessibleContext().getAccessibleSelection();
/*      */       }
/*      */ 
/*      */       public AccessibleText getAccessibleText()
/*      */       {
/* 8256 */         return getCurrentAccessibleContext().getAccessibleText();
/*      */       }
/*      */ 
/*      */       public AccessibleValue getAccessibleValue()
/*      */       {
/* 8266 */         return getCurrentAccessibleContext().getAccessibleValue();
/*      */       }
/*      */ 
/*      */       public Color getBackground()
/*      */       {
/* 8279 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8280 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8281 */           return ((AccessibleComponent)localAccessibleContext).getBackground();
/*      */         }
/* 8283 */         Component localComponent = getCurrentComponent();
/* 8284 */         if (localComponent != null) {
/* 8285 */           return localComponent.getBackground();
/*      */         }
/* 8287 */         return null;
/*      */       }
/*      */ 
/*      */       public void setBackground(Color paramColor)
/*      */       {
/* 8298 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8299 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8300 */           ((AccessibleComponent)localAccessibleContext).setBackground(paramColor);
/*      */         } else {
/* 8302 */           Component localComponent = getCurrentComponent();
/* 8303 */           if (localComponent != null)
/* 8304 */             localComponent.setBackground(paramColor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Color getForeground()
/*      */       {
/* 8316 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8317 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8318 */           return ((AccessibleComponent)localAccessibleContext).getForeground();
/*      */         }
/* 8320 */         Component localComponent = getCurrentComponent();
/* 8321 */         if (localComponent != null) {
/* 8322 */           return localComponent.getForeground();
/*      */         }
/* 8324 */         return null;
/*      */       }
/*      */ 
/*      */       public void setForeground(Color paramColor)
/*      */       {
/* 8335 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8336 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8337 */           ((AccessibleComponent)localAccessibleContext).setForeground(paramColor);
/*      */         } else {
/* 8339 */           Component localComponent = getCurrentComponent();
/* 8340 */           if (localComponent != null)
/* 8341 */             localComponent.setForeground(paramColor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Cursor getCursor()
/*      */       {
/* 8353 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8354 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8355 */           return ((AccessibleComponent)localAccessibleContext).getCursor();
/*      */         }
/* 8357 */         Component localComponent = getCurrentComponent();
/* 8358 */         if (localComponent != null) {
/* 8359 */           return localComponent.getCursor();
/*      */         }
/* 8361 */         Accessible localAccessible = getAccessibleParent();
/* 8362 */         if ((localAccessible instanceof AccessibleComponent)) {
/* 8363 */           return ((AccessibleComponent)localAccessible).getCursor();
/*      */         }
/* 8365 */         return null;
/*      */       }
/*      */ 
/*      */       public void setCursor(Cursor paramCursor)
/*      */       {
/* 8377 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8378 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8379 */           ((AccessibleComponent)localAccessibleContext).setCursor(paramCursor);
/*      */         } else {
/* 8381 */           Component localComponent = getCurrentComponent();
/* 8382 */           if (localComponent != null)
/* 8383 */             localComponent.setCursor(paramCursor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Font getFont()
/*      */       {
/* 8395 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8396 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8397 */           return ((AccessibleComponent)localAccessibleContext).getFont();
/*      */         }
/* 8399 */         Component localComponent = getCurrentComponent();
/* 8400 */         if (localComponent != null) {
/* 8401 */           return localComponent.getFont();
/*      */         }
/* 8403 */         return null;
/*      */       }
/*      */ 
/*      */       public void setFont(Font paramFont)
/*      */       {
/* 8414 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8415 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8416 */           ((AccessibleComponent)localAccessibleContext).setFont(paramFont);
/*      */         } else {
/* 8418 */           Component localComponent = getCurrentComponent();
/* 8419 */           if (localComponent != null)
/* 8420 */             localComponent.setFont(paramFont);
/*      */         }
/*      */       }
/*      */ 
/*      */       public FontMetrics getFontMetrics(Font paramFont)
/*      */       {
/* 8434 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8435 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8436 */           return ((AccessibleComponent)localAccessibleContext).getFontMetrics(paramFont);
/*      */         }
/* 8438 */         Component localComponent = getCurrentComponent();
/* 8439 */         if (localComponent != null) {
/* 8440 */           return localComponent.getFontMetrics(paramFont);
/*      */         }
/* 8442 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean isEnabled()
/*      */       {
/* 8453 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8454 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8455 */           return ((AccessibleComponent)localAccessibleContext).isEnabled();
/*      */         }
/* 8457 */         Component localComponent = getCurrentComponent();
/* 8458 */         if (localComponent != null) {
/* 8459 */           return localComponent.isEnabled();
/*      */         }
/* 8461 */         return false;
/*      */       }
/*      */ 
/*      */       public void setEnabled(boolean paramBoolean)
/*      */       {
/* 8472 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8473 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8474 */           ((AccessibleComponent)localAccessibleContext).setEnabled(paramBoolean);
/*      */         } else {
/* 8476 */           Component localComponent = getCurrentComponent();
/* 8477 */           if (localComponent != null)
/* 8478 */             localComponent.setEnabled(paramBoolean);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean isVisible()
/*      */       {
/* 8493 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8494 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8495 */           return ((AccessibleComponent)localAccessibleContext).isVisible();
/*      */         }
/* 8497 */         Component localComponent = getCurrentComponent();
/* 8498 */         if (localComponent != null) {
/* 8499 */           return localComponent.isVisible();
/*      */         }
/* 8501 */         return false;
/*      */       }
/*      */ 
/*      */       public void setVisible(boolean paramBoolean)
/*      */       {
/* 8512 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8513 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8514 */           ((AccessibleComponent)localAccessibleContext).setVisible(paramBoolean);
/*      */         } else {
/* 8516 */           Component localComponent = getCurrentComponent();
/* 8517 */           if (localComponent != null)
/* 8518 */             localComponent.setVisible(paramBoolean);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean isShowing()
/*      */       {
/* 8533 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8534 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8535 */           if (localAccessibleContext.getAccessibleParent() != null) {
/* 8536 */             return ((AccessibleComponent)localAccessibleContext).isShowing();
/*      */           }
/*      */ 
/* 8541 */           return isVisible();
/*      */         }
/*      */ 
/* 8544 */         Component localComponent = getCurrentComponent();
/* 8545 */         if (localComponent != null) {
/* 8546 */           return localComponent.isShowing();
/*      */         }
/* 8548 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean contains(Point paramPoint)
/*      */       {
/* 8565 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8566 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8567 */           localObject = ((AccessibleComponent)localAccessibleContext).getBounds();
/* 8568 */           return ((Rectangle)localObject).contains(paramPoint);
/*      */         }
/* 8570 */         Object localObject = getCurrentComponent();
/* 8571 */         if (localObject != null) {
/* 8572 */           Rectangle localRectangle = ((Component)localObject).getBounds();
/* 8573 */           return localRectangle.contains(paramPoint);
/*      */         }
/* 8575 */         return getBounds().contains(paramPoint);
/*      */       }
/*      */ 
/*      */       public Point getLocationOnScreen()
/*      */       {
/* 8587 */         if (this.parent != null) {
/* 8588 */           Point localPoint1 = this.parent.getLocationOnScreen();
/* 8589 */           Point localPoint2 = getLocation();
/* 8590 */           localPoint2.translate(localPoint1.x, localPoint1.y);
/* 8591 */           return localPoint2;
/*      */         }
/* 8593 */         return null;
/*      */       }
/*      */ 
/*      */       public Point getLocation()
/*      */       {
/* 8608 */         if (this.parent != null) {
/* 8609 */           Rectangle localRectangle = this.parent.getCellRect(this.row, this.column, false);
/* 8610 */           if (localRectangle != null) {
/* 8611 */             return localRectangle.getLocation();
/*      */           }
/*      */         }
/* 8614 */         return null;
/*      */       }
/*      */ 
/*      */       public void setLocation(Point paramPoint)
/*      */       {
/*      */       }
/*      */ 
/*      */       public Rectangle getBounds()
/*      */       {
/* 8627 */         if (this.parent != null) {
/* 8628 */           return this.parent.getCellRect(this.row, this.column, false);
/*      */         }
/* 8630 */         return null;
/*      */       }
/*      */ 
/*      */       public void setBounds(Rectangle paramRectangle)
/*      */       {
/* 8635 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8636 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8637 */           ((AccessibleComponent)localAccessibleContext).setBounds(paramRectangle);
/*      */         } else {
/* 8639 */           Component localComponent = getCurrentComponent();
/* 8640 */           if (localComponent != null)
/* 8641 */             localComponent.setBounds(paramRectangle);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Dimension getSize()
/*      */       {
/* 8647 */         if (this.parent != null) {
/* 8648 */           Rectangle localRectangle = this.parent.getCellRect(this.row, this.column, false);
/* 8649 */           if (localRectangle != null) {
/* 8650 */             return localRectangle.getSize();
/*      */           }
/*      */         }
/* 8653 */         return null;
/*      */       }
/*      */ 
/*      */       public void setSize(Dimension paramDimension) {
/* 8657 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8658 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8659 */           ((AccessibleComponent)localAccessibleContext).setSize(paramDimension);
/*      */         } else {
/* 8661 */           Component localComponent = getCurrentComponent();
/* 8662 */           if (localComponent != null)
/* 8663 */             localComponent.setSize(paramDimension);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleAt(Point paramPoint)
/*      */       {
/* 8669 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8670 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8671 */           return ((AccessibleComponent)localAccessibleContext).getAccessibleAt(paramPoint);
/*      */         }
/* 8673 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean isFocusTraversable()
/*      */       {
/* 8678 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8679 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8680 */           return ((AccessibleComponent)localAccessibleContext).isFocusTraversable();
/*      */         }
/* 8682 */         Component localComponent = getCurrentComponent();
/* 8683 */         if (localComponent != null) {
/* 8684 */           return localComponent.isFocusTraversable();
/*      */         }
/* 8686 */         return false;
/*      */       }
/*      */ 
/*      */       public void requestFocus()
/*      */       {
/* 8692 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8693 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8694 */           ((AccessibleComponent)localAccessibleContext).requestFocus();
/*      */         } else {
/* 8696 */           Component localComponent = getCurrentComponent();
/* 8697 */           if (localComponent != null)
/* 8698 */             localComponent.requestFocus();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void addFocusListener(FocusListener paramFocusListener)
/*      */       {
/* 8704 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8705 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8706 */           ((AccessibleComponent)localAccessibleContext).addFocusListener(paramFocusListener);
/*      */         } else {
/* 8708 */           Component localComponent = getCurrentComponent();
/* 8709 */           if (localComponent != null)
/* 8710 */             localComponent.addFocusListener(paramFocusListener);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void removeFocusListener(FocusListener paramFocusListener)
/*      */       {
/* 8716 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8717 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 8718 */           ((AccessibleComponent)localAccessibleContext).removeFocusListener(paramFocusListener);
/*      */         } else {
/* 8720 */           Component localComponent = getCurrentComponent();
/* 8721 */           if (localComponent != null)
/* 8722 */             localComponent.removeFocusListener(paramFocusListener);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private class AccessibleJTableHeaderCell extends AccessibleContext
/*      */       implements Accessible, AccessibleComponent
/*      */     {
/*      */       private int row;
/*      */       private int column;
/*      */       private JTableHeader parent;
/*      */       private Component rendererComponent;
/*      */ 
/*      */       public AccessibleJTableHeaderCell(int paramInt1, int paramJTableHeader, JTableHeader paramComponent, Component arg5)
/*      */       {
/* 8753 */         this.row = paramInt1;
/* 8754 */         this.column = paramJTableHeader;
/* 8755 */         this.parent = paramComponent;
/*      */         Object localObject;
/* 8756 */         this.rendererComponent = localObject;
/* 8757 */         setAccessibleParent(paramComponent);
/*      */       }
/*      */ 
/*      */       public AccessibleContext getAccessibleContext()
/*      */       {
/* 8769 */         return this;
/*      */       }
/*      */ 
/*      */       private AccessibleContext getCurrentAccessibleContext()
/*      */       {
/* 8777 */         return this.rendererComponent.getAccessibleContext();
/*      */       }
/*      */ 
/*      */       private Component getCurrentComponent()
/*      */       {
/* 8784 */         return this.rendererComponent;
/*      */       }
/*      */ 
/*      */       public String getAccessibleName()
/*      */       {
/* 8796 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8797 */         if (localAccessibleContext != null) {
/* 8798 */           String str = localAccessibleContext.getAccessibleName();
/* 8799 */           if ((str != null) && (str != "")) {
/* 8800 */             return localAccessibleContext.getAccessibleName();
/*      */           }
/*      */         }
/* 8803 */         if ((this.accessibleName != null) && (this.accessibleName != "")) {
/* 8804 */           return this.accessibleName;
/*      */         }
/* 8806 */         return null;
/*      */       }
/*      */ 
/*      */       public void setAccessibleName(String paramString)
/*      */       {
/* 8816 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8817 */         if (localAccessibleContext != null)
/* 8818 */           localAccessibleContext.setAccessibleName(paramString);
/*      */         else
/* 8820 */           super.setAccessibleName(paramString);
/*      */       }
/*      */ 
/*      */       public String getAccessibleDescription()
/*      */       {
/* 8832 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8833 */         if (localAccessibleContext != null) {
/* 8834 */           return localAccessibleContext.getAccessibleDescription();
/*      */         }
/* 8836 */         return super.getAccessibleDescription();
/*      */       }
/*      */ 
/*      */       public void setAccessibleDescription(String paramString)
/*      */       {
/* 8846 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8847 */         if (localAccessibleContext != null)
/* 8848 */           localAccessibleContext.setAccessibleDescription(paramString);
/*      */         else
/* 8850 */           super.setAccessibleDescription(paramString);
/*      */       }
/*      */ 
/*      */       public AccessibleRole getAccessibleRole()
/*      */       {
/* 8862 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8863 */         if (localAccessibleContext != null) {
/* 8864 */           return localAccessibleContext.getAccessibleRole();
/*      */         }
/* 8866 */         return AccessibleRole.UNKNOWN;
/*      */       }
/*      */ 
/*      */       public AccessibleStateSet getAccessibleStateSet()
/*      */       {
/* 8878 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8879 */         AccessibleStateSet localAccessibleStateSet = null;
/*      */ 
/* 8881 */         if (localAccessibleContext != null) {
/* 8882 */           localAccessibleStateSet = localAccessibleContext.getAccessibleStateSet();
/*      */         }
/* 8884 */         if (localAccessibleStateSet == null) {
/* 8885 */           localAccessibleStateSet = new AccessibleStateSet();
/*      */         }
/* 8887 */         Rectangle localRectangle1 = JTable.this.getVisibleRect();
/* 8888 */         Rectangle localRectangle2 = JTable.this.getCellRect(this.row, this.column, false);
/* 8889 */         if (localRectangle1.intersects(localRectangle2)) {
/* 8890 */           localAccessibleStateSet.add(AccessibleState.SHOWING);
/*      */         }
/* 8892 */         else if (localAccessibleStateSet.contains(AccessibleState.SHOWING)) {
/* 8893 */           localAccessibleStateSet.remove(AccessibleState.SHOWING);
/*      */         }
/*      */ 
/* 8896 */         if (JTable.this.isCellSelected(this.row, this.column))
/* 8897 */           localAccessibleStateSet.add(AccessibleState.SELECTED);
/* 8898 */         else if (localAccessibleStateSet.contains(AccessibleState.SELECTED)) {
/* 8899 */           localAccessibleStateSet.remove(AccessibleState.SELECTED);
/*      */         }
/* 8901 */         if ((this.row == JTable.this.getSelectedRow()) && (this.column == JTable.this.getSelectedColumn())) {
/* 8902 */           localAccessibleStateSet.add(AccessibleState.ACTIVE);
/*      */         }
/* 8904 */         localAccessibleStateSet.add(AccessibleState.TRANSIENT);
/* 8905 */         return localAccessibleStateSet;
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleParent()
/*      */       {
/* 8916 */         return this.parent;
/*      */       }
/*      */ 
/*      */       public int getAccessibleIndexInParent()
/*      */       {
/* 8927 */         return this.column;
/*      */       }
/*      */ 
/*      */       public int getAccessibleChildrenCount()
/*      */       {
/* 8936 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8937 */         if (localAccessibleContext != null) {
/* 8938 */           return localAccessibleContext.getAccessibleChildrenCount();
/*      */         }
/* 8940 */         return 0;
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleChild(int paramInt)
/*      */       {
/* 8952 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8953 */         if (localAccessibleContext != null) {
/* 8954 */           Accessible localAccessible = localAccessibleContext.getAccessibleChild(paramInt);
/* 8955 */           localAccessibleContext.setAccessibleParent(this);
/* 8956 */           return localAccessible;
/*      */         }
/* 8958 */         return null;
/*      */       }
/*      */ 
/*      */       public Locale getLocale()
/*      */       {
/* 8977 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8978 */         if (localAccessibleContext != null) {
/* 8979 */           return localAccessibleContext.getLocale();
/*      */         }
/* 8981 */         return null;
/*      */       }
/*      */ 
/*      */       public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */       {
/* 8993 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 8994 */         if (localAccessibleContext != null)
/* 8995 */           localAccessibleContext.addPropertyChangeListener(paramPropertyChangeListener);
/*      */         else
/* 8997 */           super.addPropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */ 
/*      */       public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */       {
/* 9010 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9011 */         if (localAccessibleContext != null)
/* 9012 */           localAccessibleContext.removePropertyChangeListener(paramPropertyChangeListener);
/*      */         else
/* 9014 */           super.removePropertyChangeListener(paramPropertyChangeListener);
/*      */       }
/*      */ 
/*      */       public AccessibleAction getAccessibleAction()
/*      */       {
/* 9025 */         return getCurrentAccessibleContext().getAccessibleAction();
/*      */       }
/*      */ 
/*      */       public AccessibleComponent getAccessibleComponent()
/*      */       {
/* 9036 */         return this;
/*      */       }
/*      */ 
/*      */       public AccessibleSelection getAccessibleSelection()
/*      */       {
/* 9047 */         return getCurrentAccessibleContext().getAccessibleSelection();
/*      */       }
/*      */ 
/*      */       public AccessibleText getAccessibleText()
/*      */       {
/* 9057 */         return getCurrentAccessibleContext().getAccessibleText();
/*      */       }
/*      */ 
/*      */       public AccessibleValue getAccessibleValue()
/*      */       {
/* 9067 */         return getCurrentAccessibleContext().getAccessibleValue();
/*      */       }
/*      */ 
/*      */       public Color getBackground()
/*      */       {
/* 9080 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9081 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9082 */           return ((AccessibleComponent)localAccessibleContext).getBackground();
/*      */         }
/* 9084 */         Component localComponent = getCurrentComponent();
/* 9085 */         if (localComponent != null) {
/* 9086 */           return localComponent.getBackground();
/*      */         }
/* 9088 */         return null;
/*      */       }
/*      */ 
/*      */       public void setBackground(Color paramColor)
/*      */       {
/* 9099 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9100 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9101 */           ((AccessibleComponent)localAccessibleContext).setBackground(paramColor);
/*      */         } else {
/* 9103 */           Component localComponent = getCurrentComponent();
/* 9104 */           if (localComponent != null)
/* 9105 */             localComponent.setBackground(paramColor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Color getForeground()
/*      */       {
/* 9117 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9118 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9119 */           return ((AccessibleComponent)localAccessibleContext).getForeground();
/*      */         }
/* 9121 */         Component localComponent = getCurrentComponent();
/* 9122 */         if (localComponent != null) {
/* 9123 */           return localComponent.getForeground();
/*      */         }
/* 9125 */         return null;
/*      */       }
/*      */ 
/*      */       public void setForeground(Color paramColor)
/*      */       {
/* 9136 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9137 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9138 */           ((AccessibleComponent)localAccessibleContext).setForeground(paramColor);
/*      */         } else {
/* 9140 */           Component localComponent = getCurrentComponent();
/* 9141 */           if (localComponent != null)
/* 9142 */             localComponent.setForeground(paramColor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Cursor getCursor()
/*      */       {
/* 9154 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9155 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9156 */           return ((AccessibleComponent)localAccessibleContext).getCursor();
/*      */         }
/* 9158 */         Component localComponent = getCurrentComponent();
/* 9159 */         if (localComponent != null) {
/* 9160 */           return localComponent.getCursor();
/*      */         }
/* 9162 */         Accessible localAccessible = getAccessibleParent();
/* 9163 */         if ((localAccessible instanceof AccessibleComponent)) {
/* 9164 */           return ((AccessibleComponent)localAccessible).getCursor();
/*      */         }
/* 9166 */         return null;
/*      */       }
/*      */ 
/*      */       public void setCursor(Cursor paramCursor)
/*      */       {
/* 9178 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9179 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9180 */           ((AccessibleComponent)localAccessibleContext).setCursor(paramCursor);
/*      */         } else {
/* 9182 */           Component localComponent = getCurrentComponent();
/* 9183 */           if (localComponent != null)
/* 9184 */             localComponent.setCursor(paramCursor);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Font getFont()
/*      */       {
/* 9196 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9197 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9198 */           return ((AccessibleComponent)localAccessibleContext).getFont();
/*      */         }
/* 9200 */         Component localComponent = getCurrentComponent();
/* 9201 */         if (localComponent != null) {
/* 9202 */           return localComponent.getFont();
/*      */         }
/* 9204 */         return null;
/*      */       }
/*      */ 
/*      */       public void setFont(Font paramFont)
/*      */       {
/* 9215 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9216 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9217 */           ((AccessibleComponent)localAccessibleContext).setFont(paramFont);
/*      */         } else {
/* 9219 */           Component localComponent = getCurrentComponent();
/* 9220 */           if (localComponent != null)
/* 9221 */             localComponent.setFont(paramFont);
/*      */         }
/*      */       }
/*      */ 
/*      */       public FontMetrics getFontMetrics(Font paramFont)
/*      */       {
/* 9235 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9236 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9237 */           return ((AccessibleComponent)localAccessibleContext).getFontMetrics(paramFont);
/*      */         }
/* 9239 */         Component localComponent = getCurrentComponent();
/* 9240 */         if (localComponent != null) {
/* 9241 */           return localComponent.getFontMetrics(paramFont);
/*      */         }
/* 9243 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean isEnabled()
/*      */       {
/* 9254 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9255 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9256 */           return ((AccessibleComponent)localAccessibleContext).isEnabled();
/*      */         }
/* 9258 */         Component localComponent = getCurrentComponent();
/* 9259 */         if (localComponent != null) {
/* 9260 */           return localComponent.isEnabled();
/*      */         }
/* 9262 */         return false;
/*      */       }
/*      */ 
/*      */       public void setEnabled(boolean paramBoolean)
/*      */       {
/* 9273 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9274 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9275 */           ((AccessibleComponent)localAccessibleContext).setEnabled(paramBoolean);
/*      */         } else {
/* 9277 */           Component localComponent = getCurrentComponent();
/* 9278 */           if (localComponent != null)
/* 9279 */             localComponent.setEnabled(paramBoolean);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean isVisible()
/*      */       {
/* 9294 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9295 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9296 */           return ((AccessibleComponent)localAccessibleContext).isVisible();
/*      */         }
/* 9298 */         Component localComponent = getCurrentComponent();
/* 9299 */         if (localComponent != null) {
/* 9300 */           return localComponent.isVisible();
/*      */         }
/* 9302 */         return false;
/*      */       }
/*      */ 
/*      */       public void setVisible(boolean paramBoolean)
/*      */       {
/* 9313 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9314 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9315 */           ((AccessibleComponent)localAccessibleContext).setVisible(paramBoolean);
/*      */         } else {
/* 9317 */           Component localComponent = getCurrentComponent();
/* 9318 */           if (localComponent != null)
/* 9319 */             localComponent.setVisible(paramBoolean);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean isShowing()
/*      */       {
/* 9334 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9335 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9336 */           if (localAccessibleContext.getAccessibleParent() != null) {
/* 9337 */             return ((AccessibleComponent)localAccessibleContext).isShowing();
/*      */           }
/*      */ 
/* 9342 */           return isVisible();
/*      */         }
/*      */ 
/* 9345 */         Component localComponent = getCurrentComponent();
/* 9346 */         if (localComponent != null) {
/* 9347 */           return localComponent.isShowing();
/*      */         }
/* 9349 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean contains(Point paramPoint)
/*      */       {
/* 9366 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9367 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9368 */           localObject = ((AccessibleComponent)localAccessibleContext).getBounds();
/* 9369 */           return ((Rectangle)localObject).contains(paramPoint);
/*      */         }
/* 9371 */         Object localObject = getCurrentComponent();
/* 9372 */         if (localObject != null) {
/* 9373 */           Rectangle localRectangle = ((Component)localObject).getBounds();
/* 9374 */           return localRectangle.contains(paramPoint);
/*      */         }
/* 9376 */         return getBounds().contains(paramPoint);
/*      */       }
/*      */ 
/*      */       public Point getLocationOnScreen()
/*      */       {
/* 9388 */         if (this.parent != null) {
/* 9389 */           Point localPoint1 = this.parent.getLocationOnScreen();
/* 9390 */           Point localPoint2 = getLocation();
/* 9391 */           localPoint2.translate(localPoint1.x, localPoint1.y);
/* 9392 */           return localPoint2;
/*      */         }
/* 9394 */         return null;
/*      */       }
/*      */ 
/*      */       public Point getLocation()
/*      */       {
/* 9409 */         if (this.parent != null) {
/* 9410 */           Rectangle localRectangle = this.parent.getHeaderRect(this.column);
/* 9411 */           if (localRectangle != null) {
/* 9412 */             return localRectangle.getLocation();
/*      */           }
/*      */         }
/* 9415 */         return null;
/*      */       }
/*      */ 
/*      */       public void setLocation(Point paramPoint)
/*      */       {
/*      */       }
/*      */ 
/*      */       public Rectangle getBounds()
/*      */       {
/* 9436 */         if (this.parent != null) {
/* 9437 */           return this.parent.getHeaderRect(this.column);
/*      */         }
/* 9439 */         return null;
/*      */       }
/*      */ 
/*      */       public void setBounds(Rectangle paramRectangle)
/*      */       {
/* 9452 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9453 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9454 */           ((AccessibleComponent)localAccessibleContext).setBounds(paramRectangle);
/*      */         } else {
/* 9456 */           Component localComponent = getCurrentComponent();
/* 9457 */           if (localComponent != null)
/* 9458 */             localComponent.setBounds(paramRectangle);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Dimension getSize()
/*      */       {
/* 9474 */         if (this.parent != null) {
/* 9475 */           Rectangle localRectangle = this.parent.getHeaderRect(this.column);
/* 9476 */           if (localRectangle != null) {
/* 9477 */             return localRectangle.getSize();
/*      */           }
/*      */         }
/* 9480 */         return null;
/*      */       }
/*      */ 
/*      */       public void setSize(Dimension paramDimension)
/*      */       {
/* 9490 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9491 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9492 */           ((AccessibleComponent)localAccessibleContext).setSize(paramDimension);
/*      */         } else {
/* 9494 */           Component localComponent = getCurrentComponent();
/* 9495 */           if (localComponent != null)
/* 9496 */             localComponent.setSize(paramDimension);
/*      */         }
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleAt(Point paramPoint)
/*      */       {
/* 9510 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9511 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9512 */           return ((AccessibleComponent)localAccessibleContext).getAccessibleAt(paramPoint);
/*      */         }
/* 9514 */         return null;
/*      */       }
/*      */ 
/*      */       public boolean isFocusTraversable()
/*      */       {
/* 9530 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9531 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9532 */           return ((AccessibleComponent)localAccessibleContext).isFocusTraversable();
/*      */         }
/* 9534 */         Component localComponent = getCurrentComponent();
/* 9535 */         if (localComponent != null) {
/* 9536 */           return localComponent.isFocusTraversable();
/*      */         }
/* 9538 */         return false;
/*      */       }
/*      */ 
/*      */       public void requestFocus()
/*      */       {
/* 9550 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9551 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9552 */           ((AccessibleComponent)localAccessibleContext).requestFocus();
/*      */         } else {
/* 9554 */           Component localComponent = getCurrentComponent();
/* 9555 */           if (localComponent != null)
/* 9556 */             localComponent.requestFocus();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void addFocusListener(FocusListener paramFocusListener)
/*      */       {
/* 9569 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9570 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9571 */           ((AccessibleComponent)localAccessibleContext).addFocusListener(paramFocusListener);
/*      */         } else {
/* 9573 */           Component localComponent = getCurrentComponent();
/* 9574 */           if (localComponent != null)
/* 9575 */             localComponent.addFocusListener(paramFocusListener);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void removeFocusListener(FocusListener paramFocusListener)
/*      */       {
/* 9588 */         AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
/* 9589 */         if ((localAccessibleContext instanceof AccessibleComponent)) {
/* 9590 */           ((AccessibleComponent)localAccessibleContext).removeFocusListener(paramFocusListener);
/*      */         } else {
/* 9592 */           Component localComponent = getCurrentComponent();
/* 9593 */           if (localComponent != null)
/* 9594 */             localComponent.removeFocusListener(paramFocusListener);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     protected class AccessibleJTableModelChange
/*      */       implements AccessibleTableModelChange
/*      */     {
/*      */       protected int type;
/*      */       protected int firstRow;
/*      */       protected int lastRow;
/*      */       protected int firstColumn;
/*      */       protected int lastColumn;
/*      */ 
/*      */       protected AccessibleJTableModelChange(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int arg6)
/*      */       {
/* 6703 */         this.type = paramInt1;
/* 6704 */         this.firstRow = paramInt2;
/* 6705 */         this.lastRow = paramInt3;
/* 6706 */         this.firstColumn = paramInt4;
/*      */         int i;
/* 6707 */         this.lastColumn = i;
/*      */       }
/*      */ 
/*      */       public int getType() {
/* 6711 */         return this.type;
/*      */       }
/*      */ 
/*      */       public int getFirstRow() {
/* 6715 */         return this.firstRow;
/*      */       }
/*      */ 
/*      */       public int getLastRow() {
/* 6719 */         return this.lastRow;
/*      */       }
/*      */ 
/*      */       public int getFirstColumn() {
/* 6723 */         return this.firstColumn;
/*      */       }
/*      */ 
/*      */       public int getLastColumn() {
/* 6727 */         return this.lastColumn;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class AccessibleTableHeader
/*      */       implements AccessibleTable
/*      */     {
/*      */       private JTableHeader header;
/*      */       private TableColumnModel headerModel;
/*      */ 
/*      */       AccessibleTableHeader(JTableHeader arg2)
/*      */       {
/*      */         Object localObject;
/* 7492 */         this.header = localObject;
/* 7493 */         this.headerModel = localObject.getColumnModel();
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleCaption()
/*      */       {
/* 7501 */         return null;
/*      */       }
/*      */ 
/*      */       public void setAccessibleCaption(Accessible paramAccessible)
/*      */       {
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleSummary()
/*      */       {
/* 7516 */         return null;
/*      */       }
/*      */ 
/*      */       public void setAccessibleSummary(Accessible paramAccessible)
/*      */       {
/*      */       }
/*      */ 
/*      */       public int getAccessibleRowCount()
/*      */       {
/* 7530 */         return 1;
/*      */       }
/*      */ 
/*      */       public int getAccessibleColumnCount()
/*      */       {
/* 7538 */         return this.headerModel.getColumnCount();
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleAt(int paramInt1, int paramInt2)
/*      */       {
/* 7553 */         TableColumn localTableColumn = this.headerModel.getColumn(paramInt2);
/* 7554 */         TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
/* 7555 */         if (localTableCellRenderer == null) {
/* 7556 */           localTableCellRenderer = this.header.getDefaultRenderer();
/*      */         }
/* 7558 */         Component localComponent = localTableCellRenderer.getTableCellRendererComponent(this.header.getTable(), localTableColumn.getHeaderValue(), false, false, -1, paramInt2);
/*      */ 
/* 7563 */         return new JTable.AccessibleJTable.AccessibleJTableHeaderCell(JTable.AccessibleJTable.this, paramInt1, paramInt2, JTable.this.getTableHeader(), localComponent);
/*      */       }
/*      */ 
/*      */       public int getAccessibleRowExtentAt(int paramInt1, int paramInt2)
/*      */       {
/* 7575 */         return 1;
/*      */       }
/*      */ 
/*      */       public int getAccessibleColumnExtentAt(int paramInt1, int paramInt2)
/*      */       {
/* 7584 */         return 1;
/*      */       }
/*      */ 
/*      */       public AccessibleTable getAccessibleRowHeader()
/*      */       {
/* 7592 */         return null;
/*      */       }
/*      */ 
/*      */       public void setAccessibleRowHeader(AccessibleTable paramAccessibleTable)
/*      */       {
/*      */       }
/*      */ 
/*      */       public AccessibleTable getAccessibleColumnHeader()
/*      */       {
/* 7608 */         return null;
/*      */       }
/*      */ 
/*      */       public void setAccessibleColumnHeader(AccessibleTable paramAccessibleTable)
/*      */       {
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleRowDescription(int paramInt)
/*      */       {
/* 7626 */         return null;
/*      */       }
/*      */ 
/*      */       public void setAccessibleRowDescription(int paramInt, Accessible paramAccessible)
/*      */       {
/*      */       }
/*      */ 
/*      */       public Accessible getAccessibleColumnDescription(int paramInt)
/*      */       {
/* 7644 */         return null;
/*      */       }
/*      */ 
/*      */       public void setAccessibleColumnDescription(int paramInt, Accessible paramAccessible)
/*      */       {
/*      */       }
/*      */ 
/*      */       public boolean isAccessibleSelected(int paramInt1, int paramInt2)
/*      */       {
/* 7666 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean isAccessibleRowSelected(int paramInt)
/*      */       {
/* 7677 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean isAccessibleColumnSelected(int paramInt)
/*      */       {
/* 7688 */         return false;
/*      */       }
/*      */ 
/*      */       public int[] getSelectedAccessibleRows()
/*      */       {
/* 7697 */         return new int[0];
/*      */       }
/*      */ 
/*      */       public int[] getSelectedAccessibleColumns()
/*      */       {
/* 7706 */         return new int[0];
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static class BooleanEditor extends DefaultCellEditor
/*      */   {
/*      */     public BooleanEditor()
/*      */     {
/* 5516 */       super();
/* 5517 */       JCheckBox localJCheckBox = (JCheckBox)getComponent();
/* 5518 */       localJCheckBox.setHorizontalAlignment(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class BooleanRenderer extends JCheckBox
/*      */     implements TableCellRenderer, UIResource
/*      */   {
/* 5390 */     private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
/*      */ 
/*      */     public BooleanRenderer()
/*      */     {
/* 5394 */       setHorizontalAlignment(0);
/* 5395 */       setBorderPainted(true);
/*      */     }
/*      */ 
/*      */     public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
/*      */     {
/* 5400 */       if (paramBoolean1) {
/* 5401 */         setForeground(paramJTable.getSelectionForeground());
/* 5402 */         super.setBackground(paramJTable.getSelectionBackground());
/*      */       }
/*      */       else {
/* 5405 */         setForeground(paramJTable.getForeground());
/* 5406 */         setBackground(paramJTable.getBackground());
/*      */       }
/* 5408 */       setSelected((paramObject != null) && (((Boolean)paramObject).booleanValue()));
/*      */ 
/* 5410 */       if (paramBoolean2)
/* 5411 */         setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
/*      */       else {
/* 5413 */         setBorder(noFocusBorder);
/*      */       }
/*      */ 
/* 5416 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   class CellEditorRemover
/*      */     implements PropertyChangeListener
/*      */   {
/*      */     KeyboardFocusManager focusManager;
/*      */ 
/*      */     public CellEditorRemover(KeyboardFocusManager arg2)
/*      */     {
/*      */       Object localObject;
/* 5949 */       this.focusManager = localObject;
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 5953 */       if ((!JTable.this.isEditing()) || (JTable.this.getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE)) {
/* 5954 */         return;
/*      */       }
/*      */ 
/* 5957 */       Object localObject = this.focusManager.getPermanentFocusOwner();
/* 5958 */       while (localObject != null) {
/* 5959 */         if (localObject == JTable.this)
/*      */         {
/* 5961 */           return;
/* 5962 */         }if (((localObject instanceof Window)) || (((localObject instanceof Applet)) && (((Component)localObject).getParent() == null)))
/*      */         {
/* 5964 */           if ((localObject != SwingUtilities.getRoot(JTable.this)) || 
/* 5965 */             (JTable.this.getCellEditor().stopCellEditing())) break;
/* 5966 */           JTable.this.getCellEditor().cancelCellEditing(); break;
/*      */         }
/*      */ 
/* 5971 */         localObject = ((Component)localObject).getParent();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static class DateRenderer extends DefaultTableCellRenderer.UIResource
/*      */   {
/*      */     DateFormat formatter;
/*      */ 
/*      */     public void setValue(Object paramObject)
/*      */     {
/* 5372 */       if (this.formatter == null) {
/* 5373 */         this.formatter = DateFormat.getDateInstance();
/*      */       }
/* 5375 */       setText(paramObject == null ? "" : this.formatter.format(paramObject));
/*      */     }
/*      */   }
/*      */ 
/*      */   static class DoubleRenderer extends JTable.NumberRenderer
/*      */   {
/*      */     NumberFormat formatter;
/*      */ 
/*      */     public void setValue(Object paramObject)
/*      */     {
/* 5360 */       if (this.formatter == null) {
/* 5361 */         this.formatter = NumberFormat.getInstance();
/*      */       }
/* 5363 */       setText(paramObject == null ? "" : this.formatter.format(paramObject));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class DropLocation extends TransferHandler.DropLocation
/*      */   {
/*      */     private final int row;
/*      */     private final int col;
/*      */     private final boolean isInsertRow;
/*      */     private final boolean isInsertCol;
/*      */ 
/*      */     private DropLocation(Point paramPoint, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/*  466 */       super();
/*  467 */       this.row = paramInt1;
/*  468 */       this.col = paramInt2;
/*  469 */       this.isInsertRow = paramBoolean1;
/*  470 */       this.isInsertCol = paramBoolean2;
/*      */     }
/*      */ 
/*      */     public int getRow()
/*      */     {
/*  488 */       return this.row;
/*      */     }
/*      */ 
/*      */     public int getColumn()
/*      */     {
/*  506 */       return this.col;
/*      */     }
/*      */ 
/*      */     public boolean isInsertRow()
/*      */     {
/*  516 */       return this.isInsertRow;
/*      */     }
/*      */ 
/*      */     public boolean isInsertColumn()
/*      */     {
/*  526 */       return this.isInsertCol;
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  538 */       return getClass().getName() + "[dropPoint=" + getDropPoint() + "," + "row=" + this.row + "," + "column=" + this.col + "," + "insertRow=" + this.isInsertRow + "," + "insertColumn=" + this.isInsertCol + "]";
/*      */     }
/*      */   }
/*      */ 
/*      */   static class GenericEditor extends DefaultCellEditor
/*      */   {
/* 5446 */     Class[] argTypes = { String.class };
/*      */     Constructor constructor;
/*      */     Object value;
/*      */ 
/*      */     public GenericEditor()
/*      */     {
/* 5451 */       super();
/* 5452 */       getComponent().setName("Table.editor");
/*      */     }
/*      */ 
/*      */     public boolean stopCellEditing() {
/* 5456 */       String str = (String)super.getCellEditorValue();
/*      */ 
/* 5463 */       if ("".equals(str)) {
/* 5464 */         if (this.constructor.getDeclaringClass() == String.class) {
/* 5465 */           this.value = str;
/*      */         }
/* 5467 */         super.stopCellEditing();
/*      */       }
/*      */       try
/*      */       {
/* 5471 */         this.value = this.constructor.newInstance(new Object[] { str });
/*      */       }
/*      */       catch (Exception localException) {
/* 5474 */         ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
/* 5475 */         return false;
/*      */       }
/* 5477 */       return super.stopCellEditing();
/*      */     }
/*      */ 
/*      */     public Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2)
/*      */     {
/* 5483 */       this.value = null;
/* 5484 */       ((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
/*      */       try {
/* 5486 */         Object localObject = paramJTable.getColumnClass(paramInt2);
/*      */ 
/* 5491 */         if (localObject == Object.class) {
/* 5492 */           localObject = String.class;
/*      */         }
/* 5494 */         this.constructor = ((Class)localObject).getConstructor(this.argTypes);
/*      */       }
/*      */       catch (Exception localException) {
/* 5497 */         return null;
/*      */       }
/* 5499 */       return super.getTableCellEditorComponent(paramJTable, paramObject, paramBoolean, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public Object getCellEditorValue() {
/* 5503 */       return this.value;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class IconRenderer extends DefaultTableCellRenderer.UIResource
/*      */   {
/*      */     public IconRenderer()
/*      */     {
/* 5382 */       setHorizontalAlignment(0);
/*      */     }
/* 5384 */     public void setValue(Object paramObject) { setIcon((paramObject instanceof Icon) ? (Icon)paramObject : null); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class ModelChange
/*      */   {
/*      */     int startModelIndex;
/*      */     int endModelIndex;
/*      */     int type;
/*      */     int modelRowCount;
/*      */     TableModelEvent event;
/*      */     int length;
/*      */     boolean allRowsChanged;
/*      */ 
/*      */     ModelChange(TableModelEvent arg2)
/*      */     {
/*      */       Object localObject;
/* 4083 */       this.startModelIndex = Math.max(0, localObject.getFirstRow());
/* 4084 */       this.endModelIndex = localObject.getLastRow();
/* 4085 */       this.modelRowCount = JTable.this.getModel().getRowCount();
/* 4086 */       if (this.endModelIndex < 0) {
/* 4087 */         this.endModelIndex = Math.max(0, this.modelRowCount - 1);
/*      */       }
/* 4089 */       this.length = (this.endModelIndex - this.startModelIndex + 1);
/* 4090 */       this.type = localObject.getType();
/* 4091 */       this.event = localObject;
/* 4092 */       this.allRowsChanged = (localObject.getLastRow() == 2147483647);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class NumberEditor extends JTable.GenericEditor
/*      */   {
/*      */     public NumberEditor()
/*      */     {
/* 5510 */       ((JTextField)getComponent()).setHorizontalAlignment(4);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class NumberRenderer extends DefaultTableCellRenderer.UIResource
/*      */   {
/*      */     public NumberRenderer()
/*      */     {
/* 5351 */       setHorizontalAlignment(4);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static enum PrintMode
/*      */   {
/*  265 */     NORMAL, 
/*      */ 
/*  272 */     FIT_WIDTH;
/*      */   }
/*      */ 
/*      */   private static abstract interface Resizable2
/*      */   {
/*      */     public abstract int getElementCount();
/*      */ 
/*      */     public abstract int getLowerBoundAt(int paramInt);
/*      */ 
/*      */     public abstract int getUpperBoundAt(int paramInt);
/*      */ 
/*      */     public abstract void setSizeAt(int paramInt1, int paramInt2);
/*      */   }
/*      */ 
/*      */   private static abstract interface Resizable3 extends JTable.Resizable2
/*      */   {
/*      */     public abstract int getMidPointAt(int paramInt);
/*      */   }
/*      */ 
/*      */   private final class SortManager
/*      */   {
/*      */     RowSorter<? extends TableModel> sorter;
/*      */     private ListSelectionModel modelSelection;
/*      */     private int modelLeadIndex;
/*      */     private boolean syncingSelection;
/*      */     private int[] lastModelSelection;
/*      */     private SizeSequence modelRowSizes;
/*      */ 
/*      */     SortManager()
/*      */     {
/*      */       Object localObject;
/* 3848 */       this.sorter = localObject;
/* 3849 */       localObject.addRowSorterListener(JTable.this);
/*      */     }
/*      */ 
/*      */     public void dispose()
/*      */     {
/* 3856 */       if (this.sorter != null)
/* 3857 */         this.sorter.removeRowSorterListener(JTable.this);
/*      */     }
/*      */ 
/*      */     public void setViewRowHeight(int paramInt1, int paramInt2)
/*      */     {
/* 3865 */       if (this.modelRowSizes == null) {
/* 3866 */         this.modelRowSizes = new SizeSequence(JTable.this.getModel().getRowCount(), JTable.this.getRowHeight());
/*      */       }
/*      */ 
/* 3869 */       this.modelRowSizes.setSize(JTable.this.convertRowIndexToModel(paramInt1), paramInt2);
/*      */     }
/*      */ 
/*      */     public void allChanged()
/*      */     {
/* 3876 */       this.modelLeadIndex = -1;
/* 3877 */       this.modelSelection = null;
/* 3878 */       this.modelRowSizes = null;
/*      */     }
/*      */ 
/*      */     public void viewSelectionChanged(ListSelectionEvent paramListSelectionEvent)
/*      */     {
/* 3885 */       if ((!this.syncingSelection) && (this.modelSelection != null))
/* 3886 */         this.modelSelection = null;
/*      */     }
/*      */ 
/*      */     public void prepareForChange(RowSorterEvent paramRowSorterEvent, JTable.ModelChange paramModelChange)
/*      */     {
/* 3897 */       if (JTable.this.getUpdateSelectionOnSort())
/* 3898 */         cacheSelection(paramRowSorterEvent, paramModelChange);
/*      */     }
/*      */ 
/*      */     private void cacheSelection(RowSorterEvent paramRowSorterEvent, JTable.ModelChange paramModelChange)
/*      */     {
/* 3907 */       if (paramRowSorterEvent != null)
/*      */       {
/* 3912 */         if ((this.modelSelection == null) && (this.sorter.getViewRowCount() != JTable.this.getModel().getRowCount()))
/*      */         {
/* 3914 */           this.modelSelection = new DefaultListSelectionModel();
/* 3915 */           ListSelectionModel localListSelectionModel = JTable.this.getSelectionModel();
/* 3916 */           int i = localListSelectionModel.getMinSelectionIndex();
/* 3917 */           int j = localListSelectionModel.getMaxSelectionIndex();
/*      */ 
/* 3919 */           for (int m = i; m <= j; m++) {
/* 3920 */             if (localListSelectionModel.isSelectedIndex(m)) {
/* 3921 */               k = JTable.this.convertRowIndexToModel(paramRowSorterEvent, m);
/*      */ 
/* 3923 */               if (k != -1) {
/* 3924 */                 this.modelSelection.addSelectionInterval(k, k);
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 3929 */           int k = JTable.this.convertRowIndexToModel(paramRowSorterEvent, localListSelectionModel.getLeadSelectionIndex());
/*      */ 
/* 3931 */           SwingUtilities2.setLeadAnchorWithoutSelection(this.modelSelection, k, k);
/*      */         }
/* 3933 */         else if (this.modelSelection == null)
/*      */         {
/* 3936 */           cacheModelSelection(paramRowSorterEvent);
/*      */         }
/* 3938 */       } else if (paramModelChange.allRowsChanged)
/*      */       {
/* 3940 */         this.modelSelection = null;
/* 3941 */       } else if (this.modelSelection != null)
/*      */       {
/* 3943 */         switch (paramModelChange.type) {
/*      */         case -1:
/* 3945 */           this.modelSelection.removeIndexInterval(paramModelChange.startModelIndex, paramModelChange.endModelIndex);
/*      */ 
/* 3947 */           break;
/*      */         case 1:
/* 3949 */           this.modelSelection.insertIndexInterval(paramModelChange.startModelIndex, paramModelChange.endModelIndex, true);
/*      */ 
/* 3952 */           break;
/*      */         default:
/* 3954 */           break;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 3959 */         cacheModelSelection(null);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void cacheModelSelection(RowSorterEvent paramRowSorterEvent) {
/* 3964 */       this.lastModelSelection = JTable.this.convertSelectionToModel(paramRowSorterEvent);
/* 3965 */       this.modelLeadIndex = JTable.this.convertRowIndexToModel(paramRowSorterEvent, JTable.this.selectionModel.getLeadSelectionIndex());
/*      */     }
/*      */ 
/*      */     public void processChange(RowSorterEvent paramRowSorterEvent, JTable.ModelChange paramModelChange, boolean paramBoolean)
/*      */     {
/* 3977 */       if (paramModelChange != null) {
/* 3978 */         if (paramModelChange.allRowsChanged) {
/* 3979 */           this.modelRowSizes = null;
/* 3980 */           JTable.this.rowModel = null;
/* 3981 */         } else if (this.modelRowSizes != null) {
/* 3982 */           if (paramModelChange.type == 1) {
/* 3983 */             this.modelRowSizes.insertEntries(paramModelChange.startModelIndex, paramModelChange.endModelIndex - paramModelChange.startModelIndex + 1, JTable.this.getRowHeight());
/*      */           }
/* 3987 */           else if (paramModelChange.type == -1) {
/* 3988 */             this.modelRowSizes.removeEntries(paramModelChange.startModelIndex, paramModelChange.endModelIndex - paramModelChange.startModelIndex + 1);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3994 */       if (paramBoolean) {
/* 3995 */         setViewRowHeightsFromModel();
/* 3996 */         restoreSelection(paramModelChange);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void setViewRowHeightsFromModel()
/*      */     {
/* 4005 */       if (this.modelRowSizes != null) {
/* 4006 */         JTable.this.rowModel.setSizes(JTable.this.getRowCount(), JTable.this.getRowHeight());
/* 4007 */         for (int i = JTable.this.getRowCount() - 1; i >= 0; 
/* 4008 */           i--) {
/* 4009 */           int j = JTable.this.convertRowIndexToModel(i);
/* 4010 */           JTable.this.rowModel.setSize(i, this.modelRowSizes.getSize(j));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void restoreSelection(JTable.ModelChange paramModelChange)
/*      */     {
/* 4020 */       this.syncingSelection = true;
/* 4021 */       if (this.lastModelSelection != null) {
/* 4022 */         JTable.this.restoreSortingSelection(this.lastModelSelection, this.modelLeadIndex, paramModelChange);
/*      */ 
/* 4024 */         this.lastModelSelection = null;
/* 4025 */       } else if (this.modelSelection != null) {
/* 4026 */         ListSelectionModel localListSelectionModel = JTable.this.getSelectionModel();
/* 4027 */         localListSelectionModel.setValueIsAdjusting(true);
/* 4028 */         localListSelectionModel.clearSelection();
/* 4029 */         int i = this.modelSelection.getMinSelectionIndex();
/* 4030 */         int j = this.modelSelection.getMaxSelectionIndex();
/*      */ 
/* 4032 */         for (int m = i; m <= j; m++) {
/* 4033 */           if (this.modelSelection.isSelectedIndex(m)) {
/* 4034 */             int k = JTable.this.convertRowIndexToView(m);
/* 4035 */             if (k != -1) {
/* 4036 */               localListSelectionModel.addSelectionInterval(k, k);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 4042 */         m = this.modelSelection.getLeadSelectionIndex();
/* 4043 */         if (m != -1) {
/* 4044 */           m = JTable.this.convertRowIndexToView(m);
/*      */         }
/* 4046 */         SwingUtilities2.setLeadAnchorWithoutSelection(localListSelectionModel, m, m);
/*      */ 
/* 4048 */         localListSelectionModel.setValueIsAdjusting(false);
/*      */       }
/* 4050 */       this.syncingSelection = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ThreadSafePrintable
/*      */     implements Printable
/*      */   {
/*      */     private Printable printDelegate;
/*      */     private int retVal;
/*      */     private Throwable retThrowable;
/*      */ 
/*      */     public ThreadSafePrintable(Printable arg2)
/*      */     {
/*      */       Object localObject;
/* 6468 */       this.printDelegate = localObject;
/*      */     }
/*      */ 
/*      */     public int print(final Graphics paramGraphics, final PageFormat paramPageFormat, final int paramInt)
/*      */       throws PrinterException
/*      */     {
/* 6490 */       Runnable local1 = new Runnable()
/*      */       {
/*      */         public synchronized void run() {
/*      */           try {
/* 6494 */             JTable.ThreadSafePrintable.this.retVal = JTable.ThreadSafePrintable.this.printDelegate.print(paramGraphics, paramPageFormat, paramInt);
/*      */           }
/*      */           catch (Throwable localThrowable) {
/* 6497 */             JTable.ThreadSafePrintable.this.retThrowable = localThrowable;
/*      */           }
/*      */           finally {
/* 6500 */             notifyAll();
/*      */           }
/*      */         }
/*      */       };
/* 6505 */       synchronized (local1)
/*      */       {
/* 6507 */         this.retVal = -1;
/* 6508 */         this.retThrowable = null;
/*      */ 
/* 6511 */         SwingUtilities.invokeLater(local1);
/*      */ 
/* 6514 */         while ((this.retVal == -1) && (this.retThrowable == null)) {
/*      */           try {
/* 6516 */             local1.wait();
/*      */           }
/*      */           catch (InterruptedException localInterruptedException)
/*      */           {
/*      */           }
/*      */         }
/*      */ 
/* 6523 */         if (this.retThrowable != null) {
/* 6524 */           if ((this.retThrowable instanceof PrinterException))
/* 6525 */             throw ((PrinterException)this.retThrowable);
/* 6526 */           if ((this.retThrowable instanceof RuntimeException))
/* 6527 */             throw ((RuntimeException)this.retThrowable);
/* 6528 */           if ((this.retThrowable instanceof Error)) {
/* 6529 */             throw ((Error)this.retThrowable);
/*      */           }
/*      */ 
/* 6533 */           throw new AssertionError(this.retThrowable);
/*      */         }
/*      */ 
/* 6536 */         return this.retVal;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.JTable
 * JD-Core Version:    0.6.2
 */