/*      */ package sun.swing;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.DefaultKeyboardFocusManager;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Insets;
/*      */ import java.awt.KeyboardFocusManager;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ComponentAdapter;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.FocusAdapter;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.KeyAdapter;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.text.DateFormat;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.concurrent.Callable;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.AbstractListModel;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.DefaultCellEditor;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.DefaultListSelectionModel;
/*      */ import javax.swing.DefaultRowSorter.ModelWrapper;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JRadioButtonMenuItem;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JTable;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListModel;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.LookAndFeel;
/*      */ import javax.swing.RowSorter.SortKey;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.TransferHandler;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.event.ListDataEvent;
/*      */ import javax.swing.event.ListDataListener;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.event.RowSorterEvent;
/*      */ import javax.swing.event.RowSorterListener;
/*      */ import javax.swing.event.TableModelEvent;
/*      */ import javax.swing.event.TableModelListener;
/*      */ import javax.swing.filechooser.FileSystemView;
/*      */ import javax.swing.plaf.basic.BasicDirectoryModel;
/*      */ import javax.swing.table.AbstractTableModel;
/*      */ import javax.swing.table.DefaultTableCellRenderer;
/*      */ import javax.swing.table.DefaultTableColumnModel;
/*      */ import javax.swing.table.JTableHeader;
/*      */ import javax.swing.table.TableCellEditor;
/*      */ import javax.swing.table.TableCellRenderer;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import javax.swing.table.TableModel;
/*      */ import javax.swing.table.TableRowSorter;
/*      */ import javax.swing.text.Position.Bias;
/*      */ import sun.awt.shell.ShellFolder;
/*      */ import sun.awt.shell.ShellFolderColumnInfo;
/*      */ 
/*      */ public class FilePane extends JPanel
/*      */   implements PropertyChangeListener
/*      */ {
/*      */   public static final String ACTION_APPROVE_SELECTION = "approveSelection";
/*      */   public static final String ACTION_CANCEL = "cancelSelection";
/*      */   public static final String ACTION_EDIT_FILE_NAME = "editFileName";
/*      */   public static final String ACTION_REFRESH = "refresh";
/*      */   public static final String ACTION_CHANGE_TO_PARENT_DIRECTORY = "Go Up";
/*      */   public static final String ACTION_NEW_FOLDER = "New Folder";
/*      */   public static final String ACTION_VIEW_LIST = "viewTypeList";
/*      */   public static final String ACTION_VIEW_DETAILS = "viewTypeDetails";
/*      */   private Action[] actions;
/*      */   public static final int VIEWTYPE_LIST = 0;
/*      */   public static final int VIEWTYPE_DETAILS = 1;
/*      */   private static final int VIEWTYPE_COUNT = 2;
/*   80 */   private int viewType = -1;
/*   81 */   private JPanel[] viewPanels = new JPanel[2];
/*      */   private JPanel currentViewPanel;
/*      */   private String[] viewTypeActionNames;
/*      */   private JPopupMenu contextMenu;
/*      */   private JMenu viewMenu;
/*      */   private String viewMenuLabelText;
/*      */   private String refreshActionLabelText;
/*      */   private String newFolderActionLabelText;
/*      */   private String kiloByteString;
/*      */   private String megaByteString;
/*      */   private String gigaByteString;
/*      */   private String renameErrorTitleText;
/*      */   private String renameErrorText;
/*      */   private String renameErrorFileExistsText;
/*  100 */   private static final Cursor waitCursor = Cursor.getPredefinedCursor(3);
/*      */ 
/*  103 */   private final KeyListener detailsKeyListener = new KeyAdapter()
/*      */   {
/*      */     private final long timeFactor;
/*      */     private final StringBuilder typedString;
/*      */     private long lastTime;
/*      */ 
/*      */     public void keyTyped(KeyEvent paramAnonymousKeyEvent)
/*      */     {
/*  125 */       BasicDirectoryModel localBasicDirectoryModel = FilePane.this.getModel();
/*  126 */       int i = localBasicDirectoryModel.getSize();
/*      */ 
/*  128 */       if ((FilePane.this.detailsTable == null) || (i == 0) || (paramAnonymousKeyEvent.isAltDown()) || (paramAnonymousKeyEvent.isControlDown()) || (paramAnonymousKeyEvent.isMetaDown()))
/*      */       {
/*  130 */         return;
/*      */       }
/*      */ 
/*  133 */       InputMap localInputMap = FilePane.this.detailsTable.getInputMap(1);
/*  134 */       KeyStroke localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramAnonymousKeyEvent);
/*      */ 
/*  136 */       if ((localInputMap != null) && (localInputMap.get(localKeyStroke) != null)) {
/*  137 */         return;
/*      */       }
/*      */ 
/*  140 */       int j = FilePane.this.detailsTable.getSelectionModel().getLeadSelectionIndex();
/*      */ 
/*  142 */       if (j < 0) {
/*  143 */         j = 0;
/*      */       }
/*      */ 
/*  146 */       if (j >= i) {
/*  147 */         j = i - 1;
/*      */       }
/*      */ 
/*  150 */       char c = paramAnonymousKeyEvent.getKeyChar();
/*      */ 
/*  152 */       long l = paramAnonymousKeyEvent.getWhen();
/*      */ 
/*  154 */       if (l - this.lastTime < this.timeFactor) {
/*  155 */         if ((this.typedString.length() == 1) && (this.typedString.charAt(0) == c))
/*      */         {
/*  158 */           j++;
/*      */         }
/*  160 */         else this.typedString.append(c); 
/*      */       }
/*      */       else
/*      */       {
/*  163 */         j++;
/*      */ 
/*  165 */         this.typedString.setLength(0);
/*  166 */         this.typedString.append(c);
/*      */       }
/*      */ 
/*  169 */       this.lastTime = l;
/*      */ 
/*  171 */       if (j >= i) {
/*  172 */         j = 0;
/*      */       }
/*      */ 
/*  176 */       int k = getNextMatch(j, i - 1);
/*      */ 
/*  178 */       if ((k < 0) && (j > 0)) {
/*  179 */         k = getNextMatch(0, j - 1);
/*      */       }
/*      */ 
/*  182 */       if (k >= 0) {
/*  183 */         FilePane.this.detailsTable.getSelectionModel().setSelectionInterval(k, k);
/*      */ 
/*  185 */         Rectangle localRectangle = FilePane.this.detailsTable.getCellRect(k, FilePane.this.detailsTable.convertColumnIndexToView(0), false);
/*      */ 
/*  187 */         FilePane.this.detailsTable.scrollRectToVisible(localRectangle);
/*      */       }
/*      */     }
/*      */ 
/*      */     private int getNextMatch(int paramAnonymousInt1, int paramAnonymousInt2) {
/*  192 */       BasicDirectoryModel localBasicDirectoryModel = FilePane.this.getModel();
/*  193 */       JFileChooser localJFileChooser = FilePane.this.getFileChooser();
/*  194 */       FilePane.DetailsTableRowSorter localDetailsTableRowSorter = FilePane.this.getRowSorter();
/*      */ 
/*  196 */       String str1 = this.typedString.toString().toLowerCase();
/*      */ 
/*  199 */       for (int i = paramAnonymousInt1; i <= paramAnonymousInt2; i++) {
/*  200 */         File localFile = (File)localBasicDirectoryModel.getElementAt(localDetailsTableRowSorter.convertRowIndexToModel(i));
/*      */ 
/*  202 */         String str2 = localJFileChooser.getName(localFile).toLowerCase();
/*      */ 
/*  204 */         if (str2.startsWith(str1)) {
/*  205 */           return i;
/*      */         }
/*      */       }
/*      */ 
/*  209 */       return -1;
/*      */     }
/*  103 */   };
/*      */ 
/*  213 */   private FocusListener editorFocusListener = new FocusAdapter() {
/*      */     public void focusLost(FocusEvent paramAnonymousFocusEvent) {
/*  215 */       if (!paramAnonymousFocusEvent.isTemporary())
/*  216 */         FilePane.this.applyEdit();
/*      */     }
/*  213 */   };
/*      */ 
/*  221 */   private static FocusListener repaintListener = new FocusListener() {
/*      */     public void focusGained(FocusEvent paramAnonymousFocusEvent) {
/*  223 */       repaintSelection(paramAnonymousFocusEvent.getSource());
/*      */     }
/*      */ 
/*      */     public void focusLost(FocusEvent paramAnonymousFocusEvent) {
/*  227 */       repaintSelection(paramAnonymousFocusEvent.getSource());
/*      */     }
/*      */ 
/*      */     private void repaintSelection(Object paramAnonymousObject) {
/*  231 */       if ((paramAnonymousObject instanceof JList))
/*  232 */         repaintListSelection((JList)paramAnonymousObject);
/*  233 */       else if ((paramAnonymousObject instanceof JTable))
/*  234 */         repaintTableSelection((JTable)paramAnonymousObject);
/*      */     }
/*      */ 
/*      */     private void repaintListSelection(JList paramAnonymousJList)
/*      */     {
/*  239 */       int[] arrayOfInt1 = paramAnonymousJList.getSelectedIndices();
/*  240 */       for (int k : arrayOfInt1) {
/*  241 */         Rectangle localRectangle = paramAnonymousJList.getCellBounds(k, k);
/*  242 */         paramAnonymousJList.repaint(localRectangle);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void repaintTableSelection(JTable paramAnonymousJTable) {
/*  247 */       int i = paramAnonymousJTable.getSelectionModel().getMinSelectionIndex();
/*  248 */       int j = paramAnonymousJTable.getSelectionModel().getMaxSelectionIndex();
/*  249 */       if ((i == -1) || (j == -1)) {
/*  250 */         return;
/*      */       }
/*      */ 
/*  253 */       int k = paramAnonymousJTable.convertColumnIndexToView(0);
/*      */ 
/*  255 */       Rectangle localRectangle1 = paramAnonymousJTable.getCellRect(i, k, false);
/*  256 */       Rectangle localRectangle2 = paramAnonymousJTable.getCellRect(j, k, false);
/*  257 */       Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
/*  258 */       paramAnonymousJTable.repaint(localRectangle3); }  } ;
/*      */ 
/*  262 */   private boolean smallIconsView = false;
/*      */   private Border listViewBorder;
/*      */   private Color listViewBackground;
/*      */   private boolean listViewWindowsStyle;
/*      */   private boolean readOnly;
/*  267 */   private boolean fullRowSelection = false;
/*      */   private ListSelectionModel listSelectionModel;
/*      */   private JList list;
/*      */   private JTable detailsTable;
/*      */   private static final int COLUMN_FILENAME = 0;
/*      */   private File newFolderFile;
/*      */   private FileChooserUIAccessor fileChooserUIAccessor;
/*      */   private DetailsTableModel detailsTableModel;
/*      */   private DetailsTableRowSorter rowSorter;
/*      */   private DetailsTableCellEditor tableCellEditor;
/* 1306 */   int lastIndex = -1;
/* 1307 */   File editFile = null;
/*      */ 
/* 1331 */   JTextField editCell = null;
/*      */   protected Action newFolderAction;
/*      */   private Handler handler;
/*      */ 
/*  285 */   public FilePane(FileChooserUIAccessor paramFileChooserUIAccessor) { super(new BorderLayout());
/*      */ 
/*  287 */     this.fileChooserUIAccessor = paramFileChooserUIAccessor;
/*      */ 
/*  289 */     installDefaults();
/*  290 */     createActionMap(); }
/*      */ 
/*      */   public void uninstallUI()
/*      */   {
/*  294 */     if (getModel() != null)
/*  295 */       getModel().removePropertyChangeListener(this);
/*      */   }
/*      */ 
/*      */   protected JFileChooser getFileChooser()
/*      */   {
/*  300 */     return this.fileChooserUIAccessor.getFileChooser();
/*      */   }
/*      */ 
/*      */   protected BasicDirectoryModel getModel() {
/*  304 */     return this.fileChooserUIAccessor.getModel();
/*      */   }
/*      */ 
/*      */   public int getViewType() {
/*  308 */     return this.viewType;
/*      */   }
/*      */ 
/*      */   public void setViewType(int paramInt) {
/*  312 */     if (paramInt == this.viewType) {
/*  313 */       return;
/*      */     }
/*      */ 
/*  316 */     int i = this.viewType;
/*  317 */     this.viewType = paramInt;
/*      */ 
/*  319 */     JPanel localJPanel = null;
/*  320 */     Object localObject = null;
/*      */ 
/*  322 */     switch (paramInt) {
/*      */     case 0:
/*  324 */       if (this.viewPanels[paramInt] == null) {
/*  325 */         localJPanel = this.fileChooserUIAccessor.createList();
/*  326 */         if (localJPanel == null) {
/*  327 */           localJPanel = createList();
/*      */         }
/*      */ 
/*  330 */         this.list = ((JList)findChildComponent(localJPanel, JList.class));
/*  331 */         if (this.listSelectionModel == null) {
/*  332 */           this.listSelectionModel = this.list.getSelectionModel();
/*  333 */           if (this.detailsTable != null)
/*  334 */             this.detailsTable.setSelectionModel(this.listSelectionModel);
/*      */         }
/*      */         else {
/*  337 */           this.list.setSelectionModel(this.listSelectionModel);
/*      */         }
/*      */       }
/*  340 */       this.list.setLayoutOrientation(1);
/*  341 */       localObject = this.list;
/*  342 */       break;
/*      */     case 1:
/*  345 */       if (this.viewPanels[paramInt] == null) {
/*  346 */         localJPanel = this.fileChooserUIAccessor.createDetailsView();
/*  347 */         if (localJPanel == null) {
/*  348 */           localJPanel = createDetailsView();
/*      */         }
/*      */ 
/*  351 */         this.detailsTable = ((JTable)findChildComponent(localJPanel, JTable.class));
/*  352 */         this.detailsTable.setRowHeight(Math.max(this.detailsTable.getFont().getSize() + 4, 17));
/*  353 */         if (this.listSelectionModel != null) {
/*  354 */           this.detailsTable.setSelectionModel(this.listSelectionModel);
/*      */         }
/*      */       }
/*  357 */       localObject = this.detailsTable;
/*      */     }
/*      */ 
/*  361 */     if (localJPanel != null) {
/*  362 */       this.viewPanels[paramInt] = localJPanel;
/*  363 */       recursivelySetInheritsPopupMenu(localJPanel, true);
/*      */     }
/*      */ 
/*  366 */     int j = 0;
/*      */ 
/*  368 */     if (this.currentViewPanel != null) {
/*  369 */       Component localComponent = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
/*      */ 
/*  372 */       j = (localComponent == this.detailsTable) || (localComponent == this.list) ? 1 : 0;
/*      */ 
/*  374 */       remove(this.currentViewPanel);
/*      */     }
/*      */ 
/*  377 */     this.currentViewPanel = this.viewPanels[paramInt];
/*  378 */     add(this.currentViewPanel, "Center");
/*      */ 
/*  380 */     if ((j != 0) && (localObject != null)) {
/*  381 */       ((Component)localObject).requestFocusInWindow();
/*      */     }
/*      */ 
/*  384 */     revalidate();
/*  385 */     repaint();
/*  386 */     updateViewMenu();
/*  387 */     firePropertyChange("viewType", i, paramInt);
/*      */   }
/*      */ 
/*      */   public Action getViewTypeAction(int paramInt)
/*      */   {
/*  412 */     return new ViewTypeAction(paramInt);
/*      */   }
/*      */ 
/*      */   private static void recursivelySetInheritsPopupMenu(Container paramContainer, boolean paramBoolean) {
/*  416 */     if ((paramContainer instanceof JComponent)) {
/*  417 */       ((JComponent)paramContainer).setInheritsPopupMenu(paramBoolean);
/*      */     }
/*  419 */     int i = paramContainer.getComponentCount();
/*  420 */     for (int j = 0; j < i; j++)
/*  421 */       recursivelySetInheritsPopupMenu((Container)paramContainer.getComponent(j), paramBoolean);
/*      */   }
/*      */ 
/*      */   protected void installDefaults()
/*      */   {
/*  426 */     Locale localLocale = getFileChooser().getLocale();
/*      */ 
/*  428 */     this.listViewBorder = UIManager.getBorder("FileChooser.listViewBorder");
/*  429 */     this.listViewBackground = UIManager.getColor("FileChooser.listViewBackground");
/*  430 */     this.listViewWindowsStyle = UIManager.getBoolean("FileChooser.listViewWindowsStyle");
/*  431 */     this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
/*      */ 
/*  435 */     this.viewMenuLabelText = UIManager.getString("FileChooser.viewMenuLabelText", localLocale);
/*      */ 
/*  437 */     this.refreshActionLabelText = UIManager.getString("FileChooser.refreshActionLabelText", localLocale);
/*      */ 
/*  439 */     this.newFolderActionLabelText = UIManager.getString("FileChooser.newFolderActionLabelText", localLocale);
/*      */ 
/*  442 */     this.viewTypeActionNames = new String[2];
/*  443 */     this.viewTypeActionNames[0] = UIManager.getString("FileChooser.listViewActionLabelText", localLocale);
/*      */ 
/*  445 */     this.viewTypeActionNames[1] = UIManager.getString("FileChooser.detailsViewActionLabelText", localLocale);
/*      */ 
/*  448 */     this.kiloByteString = UIManager.getString("FileChooser.fileSizeKiloBytes", localLocale);
/*  449 */     this.megaByteString = UIManager.getString("FileChooser.fileSizeMegaBytes", localLocale);
/*  450 */     this.gigaByteString = UIManager.getString("FileChooser.fileSizeGigaBytes", localLocale);
/*  451 */     this.fullRowSelection = UIManager.getBoolean("FileView.fullRowSelection");
/*      */ 
/*  453 */     this.renameErrorTitleText = UIManager.getString("FileChooser.renameErrorTitleText", localLocale);
/*  454 */     this.renameErrorText = UIManager.getString("FileChooser.renameErrorText", localLocale);
/*  455 */     this.renameErrorFileExistsText = UIManager.getString("FileChooser.renameErrorFileExistsText", localLocale);
/*      */   }
/*      */ 
/*      */   public Action[] getActions()
/*      */   {
/*  465 */     if (this.actions == null)
/*      */     {
/*  511 */       ArrayList localArrayList = new ArrayList(8);
/*      */ 
/*  514 */       localArrayList.add(new AbstractAction()
/*      */       {
/*      */         public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/*      */         {
/*  477 */           String str = (String)getValue("ActionCommandKey");
/*      */ 
/*  479 */           if (str == "cancelSelection") {
/*  480 */             if (FilePane.this.editFile != null)
/*  481 */               FilePane.this.cancelEdit();
/*      */             else
/*  483 */               FilePane.this.getFileChooser().cancelSelection();
/*      */           }
/*  485 */           else if (str == "editFileName") {
/*  486 */             JFileChooser localJFileChooser = FilePane.this.getFileChooser();
/*  487 */             int i = FilePane.this.listSelectionModel.getMinSelectionIndex();
/*  488 */             if ((i >= 0) && (FilePane.this.editFile == null) && ((!localJFileChooser.isMultiSelectionEnabled()) || (localJFileChooser.getSelectedFiles().length <= 1)))
/*      */             {
/*  492 */               FilePane.this.editFileName(i);
/*      */             }
/*  494 */           } else if (str == "refresh") {
/*  495 */             FilePane.this.getFileChooser().rescanCurrentDirectory();
/*      */           }
/*      */         }
/*      */ 
/*      */         public boolean isEnabled() {
/*  500 */           String str = (String)getValue("ActionCommandKey");
/*  501 */           if (str == "cancelSelection")
/*  502 */             return FilePane.this.getFileChooser().isEnabled();
/*  503 */           if (str == "editFileName") {
/*  504 */             return (!FilePane.this.readOnly) && (FilePane.this.getFileChooser().isEnabled());
/*      */           }
/*  506 */           return true;
/*      */         }
/*      */       });
/*  515 */       localArrayList.add(new AbstractAction()
/*      */       {
/*      */         public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/*      */         {
/*  477 */           String str = (String)getValue("ActionCommandKey");
/*      */ 
/*  479 */           if (str == "cancelSelection") {
/*  480 */             if (FilePane.this.editFile != null)
/*  481 */               FilePane.this.cancelEdit();
/*      */             else
/*  483 */               FilePane.this.getFileChooser().cancelSelection();
/*      */           }
/*  485 */           else if (str == "editFileName") {
/*  486 */             JFileChooser localJFileChooser = FilePane.this.getFileChooser();
/*  487 */             int i = FilePane.this.listSelectionModel.getMinSelectionIndex();
/*  488 */             if ((i >= 0) && (FilePane.this.editFile == null) && ((!localJFileChooser.isMultiSelectionEnabled()) || (localJFileChooser.getSelectedFiles().length <= 1)))
/*      */             {
/*  492 */               FilePane.this.editFileName(i);
/*      */             }
/*  494 */           } else if (str == "refresh") {
/*  495 */             FilePane.this.getFileChooser().rescanCurrentDirectory();
/*      */           }
/*      */         }
/*      */ 
/*      */         public boolean isEnabled() {
/*  500 */           String str = (String)getValue("ActionCommandKey");
/*  501 */           if (str == "cancelSelection")
/*  502 */             return FilePane.this.getFileChooser().isEnabled();
/*  503 */           if (str == "editFileName") {
/*  504 */             return (!FilePane.this.readOnly) && (FilePane.this.getFileChooser().isEnabled());
/*      */           }
/*  506 */           return true;
/*      */         }
/*      */       });
/*  516 */       localArrayList.add(new AbstractAction(this.refreshActionLabelText)
/*      */       {
/*      */         public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/*      */         {
/*  477 */           String str = (String)getValue("ActionCommandKey");
/*      */ 
/*  479 */           if (str == "cancelSelection") {
/*  480 */             if (FilePane.this.editFile != null)
/*  481 */               FilePane.this.cancelEdit();
/*      */             else
/*  483 */               FilePane.this.getFileChooser().cancelSelection();
/*      */           }
/*  485 */           else if (str == "editFileName") {
/*  486 */             JFileChooser localJFileChooser = FilePane.this.getFileChooser();
/*  487 */             int i = FilePane.this.listSelectionModel.getMinSelectionIndex();
/*  488 */             if ((i >= 0) && (FilePane.this.editFile == null) && ((!localJFileChooser.isMultiSelectionEnabled()) || (localJFileChooser.getSelectedFiles().length <= 1)))
/*      */             {
/*  492 */               FilePane.this.editFileName(i);
/*      */             }
/*  494 */           } else if (str == "refresh") {
/*  495 */             FilePane.this.getFileChooser().rescanCurrentDirectory();
/*      */           }
/*      */         }
/*      */ 
/*      */         public boolean isEnabled() {
/*  500 */           String str = (String)getValue("ActionCommandKey");
/*  501 */           if (str == "cancelSelection")
/*  502 */             return FilePane.this.getFileChooser().isEnabled();
/*  503 */           if (str == "editFileName") {
/*  504 */             return (!FilePane.this.readOnly) && (FilePane.this.getFileChooser().isEnabled());
/*      */           }
/*  506 */           return true;
/*      */         }
/*      */       });
/*  518 */       Action localAction = this.fileChooserUIAccessor.getApproveSelectionAction();
/*  519 */       if (localAction != null) {
/*  520 */         localArrayList.add(localAction);
/*      */       }
/*  522 */       localAction = this.fileChooserUIAccessor.getChangeToParentDirectoryAction();
/*  523 */       if (localAction != null) {
/*  524 */         localArrayList.add(localAction);
/*      */       }
/*  526 */       localAction = getNewFolderAction();
/*  527 */       if (localAction != null) {
/*  528 */         localArrayList.add(localAction);
/*      */       }
/*  530 */       localAction = getViewTypeAction(0);
/*  531 */       if (localAction != null) {
/*  532 */         localArrayList.add(localAction);
/*      */       }
/*  534 */       localAction = getViewTypeAction(1);
/*  535 */       if (localAction != null) {
/*  536 */         localArrayList.add(localAction);
/*      */       }
/*  538 */       this.actions = ((Action[])localArrayList.toArray(new Action[localArrayList.size()]));
/*      */     }
/*      */ 
/*  541 */     return this.actions;
/*      */   }
/*      */ 
/*      */   protected void createActionMap() {
/*  545 */     addActionsToMap(super.getActionMap(), getActions());
/*      */   }
/*      */ 
/*      */   public static void addActionsToMap(ActionMap paramActionMap, Action[] paramArrayOfAction)
/*      */   {
/*  550 */     if ((paramActionMap != null) && (paramArrayOfAction != null))
/*  551 */       for (Action localAction : paramArrayOfAction) {
/*  552 */         String str = (String)localAction.getValue("ActionCommandKey");
/*  553 */         if (str == null) {
/*  554 */           str = (String)localAction.getValue("Name");
/*      */         }
/*  556 */         paramActionMap.put(str, localAction);
/*      */       }
/*      */   }
/*      */ 
/*      */   private void updateListRowCount(JList paramJList)
/*      */   {
/*  563 */     if (this.smallIconsView)
/*  564 */       paramJList.setVisibleRowCount(getModel().getSize() / 3);
/*      */     else
/*  566 */       paramJList.setVisibleRowCount(-1);
/*      */   }
/*      */ 
/*      */   public JPanel createList()
/*      */   {
/*  571 */     JPanel localJPanel = new JPanel(new BorderLayout());
/*  572 */     final JFileChooser localJFileChooser = getFileChooser();
/*  573 */     final JList local4 = new JList() {
/*      */       public int getNextMatch(String paramAnonymousString, int paramAnonymousInt, Position.Bias paramAnonymousBias) {
/*  575 */         ListModel localListModel = getModel();
/*  576 */         int i = localListModel.getSize();
/*  577 */         if ((paramAnonymousString == null) || (paramAnonymousInt < 0) || (paramAnonymousInt >= i)) {
/*  578 */           throw new IllegalArgumentException();
/*      */         }
/*      */ 
/*  581 */         int j = paramAnonymousBias == Position.Bias.Backward ? 1 : 0;
/*  582 */         for (int k = paramAnonymousInt; j != 0 ? k >= 0 : k < i; k += (j != 0 ? -1 : 1)) {
/*  583 */           String str = localJFileChooser.getName((File)localListModel.getElementAt(k));
/*  584 */           if (str.regionMatches(true, 0, paramAnonymousString, 0, paramAnonymousString.length())) {
/*  585 */             return k;
/*      */           }
/*      */         }
/*  588 */         return -1;
/*      */       }
/*      */     };
/*  591 */     local4.setCellRenderer(new FileRenderer());
/*  592 */     local4.setLayoutOrientation(1);
/*      */ 
/*  595 */     local4.putClientProperty("List.isFileList", Boolean.TRUE);
/*      */ 
/*  597 */     if (this.listViewWindowsStyle) {
/*  598 */       local4.addFocusListener(repaintListener);
/*      */     }
/*      */ 
/*  601 */     updateListRowCount(local4);
/*      */ 
/*  603 */     getModel().addListDataListener(new ListDataListener() {
/*      */       public void intervalAdded(ListDataEvent paramAnonymousListDataEvent) {
/*  605 */         FilePane.this.updateListRowCount(local4);
/*      */       }
/*      */       public void intervalRemoved(ListDataEvent paramAnonymousListDataEvent) {
/*  608 */         FilePane.this.updateListRowCount(local4);
/*      */       }
/*      */       public void contentsChanged(ListDataEvent paramAnonymousListDataEvent) {
/*  611 */         if (FilePane.this.isShowing()) {
/*  612 */           FilePane.this.clearSelection();
/*      */         }
/*  614 */         FilePane.this.updateListRowCount(local4);
/*      */       }
/*      */     });
/*  618 */     getModel().addPropertyChangeListener(this);
/*      */ 
/*  620 */     if (localJFileChooser.isMultiSelectionEnabled())
/*  621 */       local4.setSelectionMode(2);
/*      */     else {
/*  623 */       local4.setSelectionMode(0);
/*      */     }
/*  625 */     local4.setModel(new SortableListModel());
/*      */ 
/*  627 */     local4.addListSelectionListener(createListSelectionListener());
/*  628 */     local4.addMouseListener(getMouseHandler());
/*      */ 
/*  630 */     JScrollPane localJScrollPane = new JScrollPane(local4);
/*  631 */     if (this.listViewBackground != null) {
/*  632 */       local4.setBackground(this.listViewBackground);
/*      */     }
/*  634 */     if (this.listViewBorder != null) {
/*  635 */       localJScrollPane.setBorder(this.listViewBorder);
/*      */     }
/*  637 */     localJPanel.add(localJScrollPane, "Center");
/*  638 */     return localJPanel;
/*      */   }
/*      */ 
/*      */   private DetailsTableModel getDetailsTableModel()
/*      */   {
/*  671 */     if (this.detailsTableModel == null) {
/*  672 */       this.detailsTableModel = new DetailsTableModel(getFileChooser());
/*      */     }
/*  674 */     return this.detailsTableModel;
/*      */   }
/*      */ 
/*      */   private void updateDetailsColumnModel(JTable paramJTable)
/*      */   {
/*  853 */     if (paramJTable != null) {
/*  854 */       ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = this.detailsTableModel.getColumns();
/*      */ 
/*  856 */       DefaultTableColumnModel localDefaultTableColumnModel = new DefaultTableColumnModel();
/*  857 */       for (int i = 0; i < arrayOfShellFolderColumnInfo.length; i++) {
/*  858 */         ShellFolderColumnInfo localShellFolderColumnInfo = arrayOfShellFolderColumnInfo[i];
/*  859 */         TableColumn localTableColumn = new TableColumn(i);
/*      */ 
/*  861 */         Object localObject1 = localShellFolderColumnInfo.getTitle();
/*  862 */         if ((localObject1 != null) && (((String)localObject1).startsWith("FileChooser.")) && (((String)localObject1).endsWith("HeaderText")))
/*      */         {
/*  864 */           localObject2 = UIManager.getString(localObject1, paramJTable.getLocale());
/*  865 */           if (localObject2 != null) {
/*  866 */             localObject1 = localObject2;
/*      */           }
/*      */         }
/*  869 */         localTableColumn.setHeaderValue(localObject1);
/*      */ 
/*  871 */         Object localObject2 = localShellFolderColumnInfo.getWidth();
/*  872 */         if (localObject2 != null) {
/*  873 */           localTableColumn.setPreferredWidth(((Integer)localObject2).intValue());
/*      */         }
/*      */ 
/*  877 */         localDefaultTableColumnModel.addColumn(localTableColumn);
/*      */       }
/*      */ 
/*  881 */       if ((!this.readOnly) && (localDefaultTableColumnModel.getColumnCount() > 0)) {
/*  882 */         localDefaultTableColumnModel.getColumn(0).setCellEditor(getDetailsTableCellEditor());
/*      */       }
/*      */ 
/*  886 */       paramJTable.setColumnModel(localDefaultTableColumnModel);
/*      */     }
/*      */   }
/*      */ 
/*      */   private DetailsTableRowSorter getRowSorter() {
/*  891 */     if (this.rowSorter == null) {
/*  892 */       this.rowSorter = new DetailsTableRowSorter();
/*      */     }
/*  894 */     return this.rowSorter;
/*      */   }
/*      */ 
/*      */   private DetailsTableCellEditor getDetailsTableCellEditor()
/*      */   {
/*  990 */     if (this.tableCellEditor == null) {
/*  991 */       this.tableCellEditor = new DetailsTableCellEditor(new JTextField());
/*      */     }
/*  993 */     return this.tableCellEditor;
/*      */   }
/*      */ 
/*      */   public JPanel createDetailsView()
/*      */   {
/* 1118 */     final JFileChooser localJFileChooser = getFileChooser();
/*      */ 
/* 1120 */     JPanel localJPanel = new JPanel(new BorderLayout());
/*      */ 
/* 1122 */     JTable local6 = new JTable(getDetailsTableModel())
/*      */     {
/*      */       protected boolean processKeyBinding(KeyStroke paramAnonymousKeyStroke, KeyEvent paramAnonymousKeyEvent, int paramAnonymousInt, boolean paramAnonymousBoolean) {
/* 1125 */         if ((paramAnonymousKeyEvent.getKeyCode() == 27) && (getCellEditor() == null))
/*      */         {
/* 1127 */           localJFileChooser.dispatchEvent(paramAnonymousKeyEvent);
/* 1128 */           return true;
/*      */         }
/* 1130 */         return super.processKeyBinding(paramAnonymousKeyStroke, paramAnonymousKeyEvent, paramAnonymousInt, paramAnonymousBoolean);
/*      */       }
/*      */ 
/*      */       public void tableChanged(TableModelEvent paramAnonymousTableModelEvent) {
/* 1134 */         super.tableChanged(paramAnonymousTableModelEvent);
/*      */ 
/* 1136 */         if (paramAnonymousTableModelEvent.getFirstRow() == -1)
/*      */         {
/* 1138 */           FilePane.this.updateDetailsColumnModel(this);
/*      */         }
/*      */       }
/*      */     };
/* 1143 */     local6.setRowSorter(getRowSorter());
/* 1144 */     local6.setAutoCreateColumnsFromModel(false);
/* 1145 */     local6.setComponentOrientation(localJFileChooser.getComponentOrientation());
/* 1146 */     local6.setAutoResizeMode(0);
/* 1147 */     local6.setShowGrid(false);
/* 1148 */     local6.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
/* 1149 */     local6.addKeyListener(this.detailsKeyListener);
/*      */ 
/* 1151 */     Font localFont = this.list.getFont();
/* 1152 */     local6.setFont(localFont);
/* 1153 */     local6.setIntercellSpacing(new Dimension(0, 0));
/*      */ 
/* 1155 */     AlignableTableHeaderRenderer localAlignableTableHeaderRenderer = new AlignableTableHeaderRenderer(local6.getTableHeader().getDefaultRenderer());
/*      */ 
/* 1157 */     local6.getTableHeader().setDefaultRenderer(localAlignableTableHeaderRenderer);
/* 1158 */     DetailsTableCellRenderer localDetailsTableCellRenderer = new DetailsTableCellRenderer(localJFileChooser);
/* 1159 */     local6.setDefaultRenderer(Object.class, localDetailsTableCellRenderer);
/*      */ 
/* 1162 */     local6.getColumnModel().getSelectionModel().setSelectionMode(0);
/*      */ 
/* 1165 */     local6.addMouseListener(getMouseHandler());
/*      */ 
/* 1170 */     local6.putClientProperty("Table.isFileList", Boolean.TRUE);
/*      */ 
/* 1172 */     if (this.listViewWindowsStyle) {
/* 1173 */       local6.addFocusListener(repaintListener);
/*      */     }
/*      */ 
/* 1178 */     ActionMap localActionMap = SwingUtilities.getUIActionMap(local6);
/* 1179 */     localActionMap.remove("selectNextRowCell");
/* 1180 */     localActionMap.remove("selectPreviousRowCell");
/* 1181 */     localActionMap.remove("selectNextColumnCell");
/* 1182 */     localActionMap.remove("selectPreviousColumnCell");
/* 1183 */     local6.setFocusTraversalKeys(0, null);
/*      */ 
/* 1185 */     local6.setFocusTraversalKeys(1, null);
/*      */ 
/* 1188 */     JScrollPane localJScrollPane = new JScrollPane(local6);
/* 1189 */     localJScrollPane.setComponentOrientation(localJFileChooser.getComponentOrientation());
/* 1190 */     LookAndFeel.installColors(localJScrollPane.getViewport(), "Table.background", "Table.foreground");
/*      */ 
/* 1194 */     localJScrollPane.addComponentListener(new ComponentAdapter() {
/*      */       public void componentResized(ComponentEvent paramAnonymousComponentEvent) {
/* 1196 */         JScrollPane localJScrollPane = (JScrollPane)paramAnonymousComponentEvent.getComponent();
/* 1197 */         FilePane.this.fixNameColumnWidth(localJScrollPane.getViewport().getSize().width);
/* 1198 */         localJScrollPane.removeComponentListener(this);
/*      */       }
/*      */     });
/* 1206 */     localJScrollPane.addMouseListener(new MouseAdapter() {
/*      */       public void mousePressed(MouseEvent paramAnonymousMouseEvent) {
/* 1208 */         JScrollPane localJScrollPane = (JScrollPane)paramAnonymousMouseEvent.getComponent();
/* 1209 */         JTable localJTable = (JTable)localJScrollPane.getViewport().getView();
/*      */ 
/* 1211 */         if ((!paramAnonymousMouseEvent.isShiftDown()) || (localJTable.getSelectionModel().getSelectionMode() == 0)) {
/* 1212 */           FilePane.this.clearSelection();
/* 1213 */           TableCellEditor localTableCellEditor = localJTable.getCellEditor();
/* 1214 */           if (localTableCellEditor != null)
/* 1215 */             localTableCellEditor.stopCellEditing();
/*      */         }
/*      */       }
/*      */     });
/* 1221 */     local6.setForeground(this.list.getForeground());
/* 1222 */     local6.setBackground(this.list.getBackground());
/*      */ 
/* 1224 */     if (this.listViewBorder != null) {
/* 1225 */       localJScrollPane.setBorder(this.listViewBorder);
/*      */     }
/* 1227 */     localJPanel.add(localJScrollPane, "Center");
/*      */ 
/* 1229 */     this.detailsTableModel.fireTableStructureChanged();
/*      */ 
/* 1231 */     return localJPanel;
/*      */   }
/*      */ 
/*      */   private void fixNameColumnWidth(int paramInt)
/*      */   {
/* 1264 */     TableColumn localTableColumn = this.detailsTable.getColumnModel().getColumn(0);
/* 1265 */     int i = this.detailsTable.getPreferredSize().width;
/*      */ 
/* 1267 */     if (i < paramInt)
/* 1268 */       localTableColumn.setPreferredWidth(localTableColumn.getPreferredWidth() + paramInt - i);
/*      */   }
/*      */ 
/*      */   public ListSelectionListener createListSelectionListener()
/*      */   {
/* 1303 */     return this.fileChooserUIAccessor.createListSelectionListener();
/*      */   }
/*      */ 
/*      */   private int getEditIndex()
/*      */   {
/* 1310 */     return this.lastIndex;
/*      */   }
/*      */ 
/*      */   private void setEditIndex(int paramInt) {
/* 1314 */     this.lastIndex = paramInt;
/*      */   }
/*      */ 
/*      */   private void resetEditIndex() {
/* 1318 */     this.lastIndex = -1;
/*      */   }
/*      */ 
/*      */   private void cancelEdit() {
/* 1322 */     if (this.editFile != null) {
/* 1323 */       this.editFile = null;
/* 1324 */       this.list.remove(this.editCell);
/* 1325 */       repaint();
/* 1326 */     } else if ((this.detailsTable != null) && (this.detailsTable.isEditing())) {
/* 1327 */       this.detailsTable.getCellEditor().cancelCellEditing();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void editFileName(int paramInt)
/*      */   {
/* 1337 */     JFileChooser localJFileChooser = getFileChooser();
/* 1338 */     File localFile = localJFileChooser.getCurrentDirectory();
/*      */ 
/* 1340 */     if ((this.readOnly) || (!canWrite(localFile))) {
/* 1341 */       return;
/*      */     }
/*      */ 
/* 1344 */     ensureIndexIsVisible(paramInt);
/* 1345 */     switch (this.viewType) {
/*      */     case 0:
/* 1347 */       this.editFile = ((File)getModel().getElementAt(getRowSorter().convertRowIndexToModel(paramInt)));
/* 1348 */       Rectangle localRectangle = this.list.getCellBounds(paramInt, paramInt);
/* 1349 */       if (this.editCell == null) {
/* 1350 */         this.editCell = new JTextField();
/* 1351 */         this.editCell.setName("Tree.cellEditor");
/* 1352 */         this.editCell.addActionListener(new EditActionListener());
/* 1353 */         this.editCell.addFocusListener(this.editorFocusListener);
/* 1354 */         this.editCell.setNextFocusableComponent(this.list);
/*      */       }
/* 1356 */       this.list.add(this.editCell);
/* 1357 */       this.editCell.setText(localJFileChooser.getName(this.editFile));
/* 1358 */       ComponentOrientation localComponentOrientation = this.list.getComponentOrientation();
/* 1359 */       this.editCell.setComponentOrientation(localComponentOrientation);
/*      */ 
/* 1361 */       Icon localIcon = localJFileChooser.getIcon(this.editFile);
/*      */ 
/* 1364 */       int i = localIcon == null ? 20 : localIcon.getIconWidth() + 4;
/*      */ 
/* 1366 */       if (localComponentOrientation.isLeftToRight())
/* 1367 */         this.editCell.setBounds(i + localRectangle.x, localRectangle.y, localRectangle.width - i, localRectangle.height);
/*      */       else {
/* 1369 */         this.editCell.setBounds(localRectangle.x, localRectangle.y, localRectangle.width - i, localRectangle.height);
/*      */       }
/* 1371 */       this.editCell.requestFocus();
/* 1372 */       this.editCell.selectAll();
/* 1373 */       break;
/*      */     case 1:
/* 1376 */       this.detailsTable.editCellAt(paramInt, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void applyEdit()
/*      */   {
/* 1389 */     if ((this.editFile != null) && (this.editFile.exists())) {
/* 1390 */       JFileChooser localJFileChooser = getFileChooser();
/* 1391 */       String str1 = localJFileChooser.getName(this.editFile);
/* 1392 */       String str2 = this.editFile.getName();
/* 1393 */       String str3 = this.editCell.getText().trim();
/*      */ 
/* 1396 */       if (!str3.equals(str1)) {
/* 1397 */         String str4 = str3;
/*      */ 
/* 1399 */         int i = str2.length();
/* 1400 */         int j = str1.length();
/* 1401 */         if ((i > j) && (str2.charAt(j) == '.')) {
/* 1402 */           str4 = str3 + str2.substring(j);
/*      */         }
/*      */ 
/* 1406 */         FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
/* 1407 */         File localFile = localFileSystemView.createFileObject(this.editFile.getParentFile(), str4);
/* 1408 */         if (localFile.exists()) {
/* 1409 */           JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(this.renameErrorFileExistsText, new Object[] { str2 }), this.renameErrorTitleText, 0);
/*      */         }
/* 1412 */         else if (getModel().renameFile(this.editFile, localFile)) {
/* 1413 */           if (localFileSystemView.isParent(localJFileChooser.getCurrentDirectory(), localFile)) {
/* 1414 */             if (localJFileChooser.isMultiSelectionEnabled())
/* 1415 */               localJFileChooser.setSelectedFiles(new File[] { localFile });
/*      */             else {
/* 1417 */               localJFileChooser.setSelectedFile(localFile);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1424 */           JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(this.renameErrorText, new Object[] { str2 }), this.renameErrorTitleText, 0);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1430 */     if ((this.detailsTable != null) && (this.detailsTable.isEditing())) {
/* 1431 */       this.detailsTable.getCellEditor().stopCellEditing();
/*      */     }
/* 1433 */     cancelEdit();
/*      */   }
/*      */ 
/*      */   public Action getNewFolderAction()
/*      */   {
/* 1439 */     if ((!this.readOnly) && (this.newFolderAction == null)) {
/* 1440 */       this.newFolderAction = new AbstractAction(this.newFolderActionLabelText)
/*      */       {
/*      */         private Action basicNewFolderAction;
/*      */ 
/*      */         public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/*      */         {
/* 1454 */           if (this.basicNewFolderAction == null) {
/* 1455 */             this.basicNewFolderAction = FilePane.this.fileChooserUIAccessor.getNewFolderAction();
/*      */           }
/* 1457 */           JFileChooser localJFileChooser = FilePane.this.getFileChooser();
/* 1458 */           File localFile1 = localJFileChooser.getSelectedFile();
/* 1459 */           this.basicNewFolderAction.actionPerformed(paramAnonymousActionEvent);
/* 1460 */           File localFile2 = localJFileChooser.getSelectedFile();
/* 1461 */           if ((localFile2 != null) && (!localFile2.equals(localFile1)) && (localFile2.isDirectory())) {
/* 1462 */             FilePane.this.newFolderFile = localFile2;
/*      */           }
/*      */         }
/*      */       };
/*      */     }
/* 1467 */     return this.newFolderAction;
/*      */   }
/*      */ 
/*      */   void setFileSelected()
/*      */   {
/*      */     Object localObject1;
/*      */     Object localObject2;
/*      */     int i;
/*      */     int j;
/* 1501 */     if ((getFileChooser().isMultiSelectionEnabled()) && (!isDirectorySelected())) {
/* 1502 */       localObject1 = getFileChooser().getSelectedFiles();
/* 1503 */       localObject2 = this.list.getSelectedValues();
/*      */ 
/* 1505 */       this.listSelectionModel.setValueIsAdjusting(true);
/*      */       try {
/* 1507 */         i = this.listSelectionModel.getLeadSelectionIndex();
/* 1508 */         j = this.listSelectionModel.getAnchorSelectionIndex();
/*      */ 
/* 1510 */         Arrays.sort((Object[])localObject1);
/* 1511 */         Arrays.sort((Object[])localObject2);
/*      */ 
/* 1513 */         int k = 0;
/* 1514 */         int m = 0;
/*      */ 
/* 1518 */         while ((k < localObject1.length) && (m < localObject2.length))
/*      */         {
/* 1520 */           int n = localObject1[k].compareTo((File)localObject2[m]);
/* 1521 */           if (n < 0) {
/* 1522 */             doSelectFile(localObject1[(k++)]);
/* 1523 */           } else if (n > 0) {
/* 1524 */             doDeselectFile(localObject2[(m++)]);
/*      */           }
/*      */           else {
/* 1527 */             k++;
/* 1528 */             m++;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1533 */         while (k < localObject1.length) {
/* 1534 */           doSelectFile(localObject1[(k++)]);
/*      */         }
/*      */ 
/* 1537 */         while (m < localObject2.length) {
/* 1538 */           doDeselectFile(localObject2[(m++)]);
/*      */         }
/*      */ 
/* 1542 */         if ((this.listSelectionModel instanceof DefaultListSelectionModel)) {
/* 1543 */           ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(i);
/*      */ 
/* 1545 */           this.listSelectionModel.setAnchorSelectionIndex(j);
/*      */         }
/*      */       } finally {
/* 1548 */         this.listSelectionModel.setValueIsAdjusting(false);
/*      */       }
/*      */     } else {
/* 1551 */       localObject1 = getFileChooser();
/*      */ 
/* 1553 */       if (isDirectorySelected())
/* 1554 */         localObject2 = getDirectory();
/*      */       else {
/* 1556 */         localObject2 = ((JFileChooser)localObject1).getSelectedFile();
/*      */       }
/*      */ 
/* 1559 */       if ((localObject2 != null) && ((i = getModel().indexOf(localObject2)) >= 0)) {
/* 1560 */         j = getRowSorter().convertRowIndexToView(i);
/* 1561 */         this.listSelectionModel.setSelectionInterval(j, j);
/* 1562 */         ensureIndexIsVisible(j);
/*      */       } else {
/* 1564 */         clearSelection();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doSelectFile(File paramFile) {
/* 1570 */     int i = getModel().indexOf(paramFile);
/*      */ 
/* 1572 */     if (i >= 0) {
/* 1573 */       i = getRowSorter().convertRowIndexToView(i);
/* 1574 */       this.listSelectionModel.addSelectionInterval(i, i);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doDeselectFile(Object paramObject) {
/* 1579 */     int i = getRowSorter().convertRowIndexToView(getModel().indexOf(paramObject));
/*      */ 
/* 1581 */     this.listSelectionModel.removeSelectionInterval(i, i);
/*      */   }
/*      */ 
/*      */   private void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/* 1587 */     applyEdit();
/* 1588 */     File localFile = (File)paramPropertyChangeEvent.getNewValue();
/* 1589 */     JFileChooser localJFileChooser = getFileChooser();
/* 1590 */     if ((localFile != null) && (((localJFileChooser.isFileSelectionEnabled()) && (!localFile.isDirectory())) || ((localFile.isDirectory()) && (localJFileChooser.isDirectorySelectionEnabled()))))
/*      */     {
/* 1594 */       setFileSelected();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/* 1599 */     applyEdit();
/* 1600 */     File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
/* 1601 */     JFileChooser localJFileChooser = getFileChooser();
/* 1602 */     if ((arrayOfFile != null) && (arrayOfFile.length > 0) && ((arrayOfFile.length > 1) || (localJFileChooser.isDirectorySelectionEnabled()) || (!arrayOfFile[0].isDirectory())))
/*      */     {
/* 1605 */       setFileSelected();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/* 1610 */     getDetailsTableModel().updateColumnInfo();
/*      */ 
/* 1612 */     JFileChooser localJFileChooser = getFileChooser();
/* 1613 */     FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
/*      */ 
/* 1615 */     applyEdit();
/* 1616 */     resetEditIndex();
/* 1617 */     ensureIndexIsVisible(0);
/* 1618 */     File localFile = localJFileChooser.getCurrentDirectory();
/* 1619 */     if (localFile != null) {
/* 1620 */       if (!this.readOnly) {
/* 1621 */         getNewFolderAction().setEnabled(canWrite(localFile));
/*      */       }
/* 1623 */       this.fileChooserUIAccessor.getChangeToParentDirectoryAction().setEnabled(!localFileSystemView.isRoot(localFile));
/*      */     }
/* 1625 */     if (this.list != null)
/* 1626 */       this.list.clearSelection();
/*      */   }
/*      */ 
/*      */   private void doFilterChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/* 1631 */     applyEdit();
/* 1632 */     resetEditIndex();
/* 1633 */     clearSelection();
/*      */   }
/*      */ 
/*      */   private void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/* 1637 */     applyEdit();
/* 1638 */     resetEditIndex();
/* 1639 */     clearSelection();
/*      */   }
/*      */ 
/*      */   private void doMultiSelectionChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/* 1643 */     if (getFileChooser().isMultiSelectionEnabled()) {
/* 1644 */       this.listSelectionModel.setSelectionMode(2);
/*      */     } else {
/* 1646 */       this.listSelectionModel.setSelectionMode(0);
/* 1647 */       clearSelection();
/* 1648 */       getFileChooser().setSelectedFiles(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/* 1657 */     if (this.viewType == -1) {
/* 1658 */       setViewType(0);
/*      */     }
/*      */ 
/* 1661 */     String str = paramPropertyChangeEvent.getPropertyName();
/* 1662 */     if (str.equals("SelectedFileChangedProperty")) {
/* 1663 */       doSelectedFileChanged(paramPropertyChangeEvent);
/* 1664 */     } else if (str.equals("SelectedFilesChangedProperty")) {
/* 1665 */       doSelectedFilesChanged(paramPropertyChangeEvent);
/* 1666 */     } else if (str.equals("directoryChanged")) {
/* 1667 */       doDirectoryChanged(paramPropertyChangeEvent);
/* 1668 */     } else if (str.equals("fileFilterChanged")) {
/* 1669 */       doFilterChanged(paramPropertyChangeEvent);
/* 1670 */     } else if (str.equals("fileSelectionChanged")) {
/* 1671 */       doFileSelectionModeChanged(paramPropertyChangeEvent);
/* 1672 */     } else if (str.equals("MultiSelectionEnabledChangedProperty")) {
/* 1673 */       doMultiSelectionChanged(paramPropertyChangeEvent);
/* 1674 */     } else if (str.equals("CancelSelection")) {
/* 1675 */       applyEdit();
/* 1676 */     } else if (str.equals("busy")) {
/* 1677 */       setCursor(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue() ? waitCursor : null);
/* 1678 */     } else if (str.equals("componentOrientation")) {
/* 1679 */       ComponentOrientation localComponentOrientation = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
/* 1680 */       JFileChooser localJFileChooser = (JFileChooser)paramPropertyChangeEvent.getSource();
/* 1681 */       if (localComponentOrientation != paramPropertyChangeEvent.getOldValue()) {
/* 1682 */         localJFileChooser.applyComponentOrientation(localComponentOrientation);
/*      */       }
/* 1684 */       if (this.detailsTable != null) {
/* 1685 */         this.detailsTable.setComponentOrientation(localComponentOrientation);
/* 1686 */         this.detailsTable.getParent().getParent().setComponentOrientation(localComponentOrientation);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void ensureIndexIsVisible(int paramInt) {
/* 1692 */     if (paramInt >= 0) {
/* 1693 */       if (this.list != null) {
/* 1694 */         this.list.ensureIndexIsVisible(paramInt);
/*      */       }
/* 1696 */       if (this.detailsTable != null)
/* 1697 */         this.detailsTable.scrollRectToVisible(this.detailsTable.getCellRect(paramInt, 0, true));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile)
/*      */   {
/* 1703 */     int i = getModel().indexOf(paramFile);
/* 1704 */     if (i >= 0)
/* 1705 */       ensureIndexIsVisible(getRowSorter().convertRowIndexToView(i));
/*      */   }
/*      */ 
/*      */   public void rescanCurrentDirectory()
/*      */   {
/* 1710 */     getModel().validateFileCache();
/*      */   }
/*      */ 
/*      */   public void clearSelection() {
/* 1714 */     if (this.listSelectionModel != null) {
/* 1715 */       this.listSelectionModel.clearSelection();
/* 1716 */       if ((this.listSelectionModel instanceof DefaultListSelectionModel)) {
/* 1717 */         ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(0);
/* 1718 */         this.listSelectionModel.setAnchorSelectionIndex(0);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public JMenu getViewMenu() {
/* 1724 */     if (this.viewMenu == null) {
/* 1725 */       this.viewMenu = new JMenu(this.viewMenuLabelText);
/* 1726 */       ButtonGroup localButtonGroup = new ButtonGroup();
/*      */ 
/* 1728 */       for (int i = 0; i < 2; i++) {
/* 1729 */         JRadioButtonMenuItem localJRadioButtonMenuItem = new JRadioButtonMenuItem(new ViewTypeAction(i));
/*      */ 
/* 1731 */         localButtonGroup.add(localJRadioButtonMenuItem);
/* 1732 */         this.viewMenu.add(localJRadioButtonMenuItem);
/*      */       }
/* 1734 */       updateViewMenu();
/*      */     }
/* 1736 */     return this.viewMenu;
/*      */   }
/*      */ 
/*      */   private void updateViewMenu() {
/* 1740 */     if (this.viewMenu != null) {
/* 1741 */       Component[] arrayOfComponent1 = this.viewMenu.getMenuComponents();
/* 1742 */       for (Component localComponent : arrayOfComponent1)
/* 1743 */         if ((localComponent instanceof JRadioButtonMenuItem)) {
/* 1744 */           JRadioButtonMenuItem localJRadioButtonMenuItem = (JRadioButtonMenuItem)localComponent;
/* 1745 */           if (((ViewTypeAction)localJRadioButtonMenuItem.getAction()).viewType == this.viewType)
/* 1746 */             localJRadioButtonMenuItem.setSelected(true);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public JPopupMenu getComponentPopupMenu()
/*      */   {
/* 1754 */     JPopupMenu localJPopupMenu = getFileChooser().getComponentPopupMenu();
/* 1755 */     if (localJPopupMenu != null) {
/* 1756 */       return localJPopupMenu;
/*      */     }
/*      */ 
/* 1759 */     JMenu localJMenu = getViewMenu();
/* 1760 */     if (this.contextMenu == null) {
/* 1761 */       this.contextMenu = new JPopupMenu();
/* 1762 */       if (localJMenu != null) {
/* 1763 */         this.contextMenu.add(localJMenu);
/* 1764 */         if (this.listViewWindowsStyle) {
/* 1765 */           this.contextMenu.addSeparator();
/*      */         }
/*      */       }
/* 1768 */       ActionMap localActionMap = getActionMap();
/* 1769 */       Action localAction1 = localActionMap.get("refresh");
/* 1770 */       Action localAction2 = localActionMap.get("New Folder");
/* 1771 */       if (localAction1 != null) {
/* 1772 */         this.contextMenu.add(localAction1);
/* 1773 */         if ((this.listViewWindowsStyle) && (localAction2 != null)) {
/* 1774 */           this.contextMenu.addSeparator();
/*      */         }
/*      */       }
/* 1777 */       if (localAction2 != null) {
/* 1778 */         this.contextMenu.add(localAction2);
/*      */       }
/*      */     }
/* 1781 */     if (localJMenu != null) {
/* 1782 */       localJMenu.getPopupMenu().setInvoker(localJMenu);
/*      */     }
/* 1784 */     return this.contextMenu;
/*      */   }
/*      */ 
/*      */   protected Handler getMouseHandler()
/*      */   {
/* 1791 */     if (this.handler == null) {
/* 1792 */       this.handler = new Handler(null);
/*      */     }
/* 1794 */     return this.handler;
/*      */   }
/*      */ 
/*      */   protected boolean isDirectorySelected()
/*      */   {
/* 1934 */     return this.fileChooserUIAccessor.isDirectorySelected();
/*      */   }
/*      */ 
/*      */   protected File getDirectory()
/*      */   {
/* 1945 */     return this.fileChooserUIAccessor.getDirectory();
/*      */   }
/*      */ 
/*      */   private Component findChildComponent(Container paramContainer, Class paramClass) {
/* 1949 */     int i = paramContainer.getComponentCount();
/* 1950 */     for (int j = 0; j < i; j++) {
/* 1951 */       Component localComponent1 = paramContainer.getComponent(j);
/* 1952 */       if (paramClass.isInstance(localComponent1))
/* 1953 */         return localComponent1;
/* 1954 */       if ((localComponent1 instanceof Container)) {
/* 1955 */         Component localComponent2 = findChildComponent((Container)localComponent1, paramClass);
/* 1956 */         if (localComponent2 != null) {
/* 1957 */           return localComponent2;
/*      */         }
/*      */       }
/*      */     }
/* 1961 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean canWrite(File paramFile)
/*      */   {
/* 1966 */     if (!paramFile.exists()) {
/* 1967 */       return false;
/*      */     }
/*      */ 
/* 1970 */     if ((paramFile instanceof ShellFolder)) {
/* 1971 */       return ((ShellFolder)paramFile).isFileSystem();
/*      */     }
/* 1973 */     if (usesShellFolder(getFileChooser())) {
/*      */       try {
/* 1975 */         return ShellFolder.getShellFolder(paramFile).isFileSystem();
/*      */       }
/*      */       catch (FileNotFoundException localFileNotFoundException) {
/* 1978 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1982 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean usesShellFolder(JFileChooser paramJFileChooser)
/*      */   {
/* 1991 */     Boolean localBoolean = (Boolean)paramJFileChooser.getClientProperty("FileChooser.useShellFolder");
/*      */ 
/* 1993 */     return localBoolean == null ? paramJFileChooser.getFileSystemView().equals(FileSystemView.getFileSystemView()) : localBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   private class AlignableTableHeaderRenderer
/*      */     implements TableCellRenderer
/*      */   {
/*      */     TableCellRenderer wrappedRenderer;
/*      */ 
/*      */     public AlignableTableHeaderRenderer(TableCellRenderer arg2)
/*      */     {
/*      */       Object localObject;
/* 1238 */       this.wrappedRenderer = localObject;
/*      */     }
/*      */ 
/*      */     public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
/*      */     {
/* 1245 */       Component localComponent = this.wrappedRenderer.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
/*      */ 
/* 1248 */       int i = paramJTable.convertColumnIndexToModel(paramInt2);
/* 1249 */       ShellFolderColumnInfo localShellFolderColumnInfo = FilePane.this.detailsTableModel.getColumns()[i];
/*      */ 
/* 1251 */       Integer localInteger = localShellFolderColumnInfo.getAlignment();
/* 1252 */       if (localInteger == null) {
/* 1253 */         localInteger = Integer.valueOf(0);
/*      */       }
/* 1255 */       if ((localComponent instanceof JLabel)) {
/* 1256 */         ((JLabel)localComponent).setHorizontalAlignment(localInteger.intValue());
/*      */       }
/*      */ 
/* 1259 */       return localComponent;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DelayedSelectionUpdater
/*      */     implements Runnable
/*      */   {
/*      */     File editFile;
/*      */ 
/*      */     DelayedSelectionUpdater()
/*      */     {
/* 1276 */       this(null);
/*      */     }
/*      */ 
/*      */     DelayedSelectionUpdater(File arg2)
/*      */     {
/*      */       Object localObject;
/* 1280 */       this.editFile = localObject;
/* 1281 */       if (FilePane.this.isShowing())
/* 1282 */         SwingUtilities.invokeLater(this);
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1287 */       FilePane.this.setFileSelected();
/* 1288 */       if (this.editFile != null) {
/* 1289 */         FilePane.this.editFileName(FilePane.access$100(FilePane.this).convertRowIndexToView(FilePane.this.getModel().indexOf(this.editFile)));
/*      */ 
/* 1291 */         this.editFile = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DetailsTableCellEditor extends DefaultCellEditor
/*      */   {
/*      */     private final JTextField tf;
/*      */ 
/*      */     public DetailsTableCellEditor(JTextField arg2)
/*      */     {
/* 1000 */       super();
/* 1001 */       this.tf = localJTextField;
/* 1002 */       localJTextField.setName("Table.editor");
/* 1003 */       localJTextField.addFocusListener(FilePane.this.editorFocusListener);
/*      */     }
/*      */ 
/*      */     public Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2)
/*      */     {
/* 1008 */       Component localComponent = super.getTableCellEditorComponent(paramJTable, paramObject, paramBoolean, paramInt1, paramInt2);
/*      */ 
/* 1010 */       if ((paramObject instanceof File)) {
/* 1011 */         this.tf.setText(FilePane.this.getFileChooser().getName((File)paramObject));
/* 1012 */         this.tf.selectAll();
/*      */       }
/* 1014 */       return localComponent;
/*      */     }
/*      */   }
/*      */ 
/*      */   class DetailsTableCellRenderer extends DefaultTableCellRenderer
/*      */   {
/*      */     JFileChooser chooser;
/*      */     DateFormat df;
/*      */ 
/*      */     DetailsTableCellRenderer(JFileChooser arg2)
/*      */     {
/*      */       Object localObject;
/* 1024 */       this.chooser = localObject;
/* 1025 */       this.df = DateFormat.getDateTimeInstance(3, 3, localObject.getLocale());
/*      */     }
/*      */ 
/*      */     public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 1030 */       if ((getHorizontalAlignment() == 10) && (!FilePane.this.fullRowSelection))
/*      */       {
/* 1033 */         paramInt3 = Math.min(paramInt3, getPreferredSize().width + 4);
/*      */       }
/* 1035 */       else paramInt1 -= 4;
/*      */ 
/* 1037 */       super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*      */ 
/*      */     public Insets getInsets(Insets paramInsets)
/*      */     {
/* 1043 */       paramInsets = super.getInsets(paramInsets);
/* 1044 */       paramInsets.left += 4;
/* 1045 */       paramInsets.right += 4;
/* 1046 */       return paramInsets;
/*      */     }
/*      */ 
/*      */     public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
/*      */     {
/* 1052 */       if (((paramJTable.convertColumnIndexToModel(paramInt2) != 0) || ((FilePane.this.listViewWindowsStyle) && (!paramJTable.isFocusOwner()))) && (!FilePane.this.fullRowSelection))
/*      */       {
/* 1055 */         paramBoolean1 = false;
/*      */       }
/*      */ 
/* 1058 */       super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
/*      */ 
/* 1061 */       setIcon(null);
/*      */ 
/* 1063 */       int i = paramJTable.convertColumnIndexToModel(paramInt2);
/* 1064 */       ShellFolderColumnInfo localShellFolderColumnInfo = FilePane.this.detailsTableModel.getColumns()[i];
/*      */ 
/* 1066 */       Integer localInteger = localShellFolderColumnInfo.getAlignment();
/* 1067 */       if (localInteger == null) {
/* 1068 */         localInteger = Integer.valueOf((paramObject instanceof Number) ? 4 : 10);
/*      */       }
/*      */ 
/* 1073 */       setHorizontalAlignment(localInteger.intValue());
/*      */       String str;
/* 1079 */       if (paramObject == null) {
/* 1080 */         str = "";
/*      */       }
/* 1082 */       else if ((paramObject instanceof File)) {
/* 1083 */         File localFile = (File)paramObject;
/* 1084 */         str = this.chooser.getName(localFile);
/* 1085 */         Icon localIcon = this.chooser.getIcon(localFile);
/* 1086 */         setIcon(localIcon);
/*      */       }
/* 1088 */       else if ((paramObject instanceof Long)) {
/* 1089 */         long l = ((Long)paramObject).longValue() / 1024L;
/* 1090 */         if (FilePane.this.listViewWindowsStyle) {
/* 1091 */           str = MessageFormat.format(FilePane.this.kiloByteString, new Object[] { Long.valueOf(l + 1L) });
/* 1092 */         } else if (l < 1024L) {
/* 1093 */           str = MessageFormat.format(FilePane.this.kiloByteString, new Object[] { Long.valueOf(l == 0L ? 1L : l) });
/*      */         } else {
/* 1095 */           l /= 1024L;
/* 1096 */           if (l < 1024L) {
/* 1097 */             str = MessageFormat.format(FilePane.this.megaByteString, new Object[] { Long.valueOf(l) });
/*      */           } else {
/* 1099 */             l /= 1024L;
/* 1100 */             str = MessageFormat.format(FilePane.this.gigaByteString, new Object[] { Long.valueOf(l) });
/*      */           }
/*      */         }
/*      */       }
/* 1104 */       else if ((paramObject instanceof Date)) {
/* 1105 */         str = this.df.format((Date)paramObject);
/*      */       }
/*      */       else {
/* 1108 */         str = paramObject.toString();
/*      */       }
/*      */ 
/* 1111 */       setText(str);
/*      */ 
/* 1113 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   class DetailsTableModel extends AbstractTableModel
/*      */     implements ListDataListener
/*      */   {
/*      */     JFileChooser chooser;
/*      */     BasicDirectoryModel directoryModel;
/*      */     ShellFolderColumnInfo[] columns;
/*      */     int[] columnMap;
/*      */ 
/*      */     DetailsTableModel(JFileChooser arg2)
/*      */     {
/*      */       Object localObject;
/*  685 */       this.chooser = localObject;
/*  686 */       this.directoryModel = FilePane.this.getModel();
/*  687 */       this.directoryModel.addListDataListener(this);
/*      */ 
/*  689 */       updateColumnInfo();
/*      */     }
/*      */ 
/*      */     void updateColumnInfo() {
/*  693 */       Object localObject = this.chooser.getCurrentDirectory();
/*  694 */       if ((localObject != null) && (FilePane.usesShellFolder(this.chooser))) {
/*      */         try {
/*  696 */           localObject = ShellFolder.getShellFolder((File)localObject);
/*      */         }
/*      */         catch (FileNotFoundException localFileNotFoundException)
/*      */         {
/*      */         }
/*      */       }
/*  702 */       ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = ShellFolder.getFolderColumns((File)localObject);
/*      */ 
/*  704 */       ArrayList localArrayList = new ArrayList();
/*      */ 
/*  706 */       this.columnMap = new int[arrayOfShellFolderColumnInfo.length];
/*  707 */       for (int i = 0; i < arrayOfShellFolderColumnInfo.length; i++) {
/*  708 */         ShellFolderColumnInfo localShellFolderColumnInfo = arrayOfShellFolderColumnInfo[i];
/*  709 */         if (localShellFolderColumnInfo.isVisible()) {
/*  710 */           this.columnMap[localArrayList.size()] = i;
/*  711 */           localArrayList.add(localShellFolderColumnInfo);
/*      */         }
/*      */       }
/*      */ 
/*  715 */       this.columns = new ShellFolderColumnInfo[localArrayList.size()];
/*  716 */       localArrayList.toArray(this.columns);
/*  717 */       this.columnMap = Arrays.copyOf(this.columnMap, this.columns.length);
/*      */ 
/*  719 */       List localList = FilePane.this.rowSorter == null ? null : FilePane.this.rowSorter.getSortKeys();
/*      */ 
/*  721 */       fireTableStructureChanged();
/*  722 */       restoreSortKeys(localList);
/*      */     }
/*      */ 
/*      */     private void restoreSortKeys(List<? extends RowSorter.SortKey> paramList) {
/*  726 */       if (paramList != null)
/*      */       {
/*  728 */         for (int i = 0; i < paramList.size(); i++) {
/*  729 */           RowSorter.SortKey localSortKey = (RowSorter.SortKey)paramList.get(i);
/*  730 */           if (localSortKey.getColumn() >= this.columns.length) {
/*  731 */             paramList = null;
/*  732 */             break;
/*      */           }
/*      */         }
/*  735 */         if (paramList != null)
/*  736 */           FilePane.this.rowSorter.setSortKeys(paramList);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getRowCount()
/*      */     {
/*  742 */       return this.directoryModel.getSize();
/*      */     }
/*      */ 
/*      */     public int getColumnCount() {
/*  746 */       return this.columns.length;
/*      */     }
/*      */ 
/*      */     public Object getValueAt(int paramInt1, int paramInt2)
/*      */     {
/*  755 */       return getFileColumnValue((File)this.directoryModel.getElementAt(paramInt1), paramInt2);
/*      */     }
/*      */ 
/*      */     private Object getFileColumnValue(File paramFile, int paramInt) {
/*  759 */       return paramInt == 0 ? paramFile : ShellFolder.getFolderColumnValue(paramFile, this.columnMap[paramInt]);
/*      */     }
/*      */ 
/*      */     public void setValueAt(Object paramObject, int paramInt1, int paramInt2)
/*      */     {
/*  765 */       if (paramInt2 == 0) {
/*  766 */         final JFileChooser localJFileChooser = FilePane.this.getFileChooser();
/*  767 */         File localFile1 = (File)getValueAt(paramInt1, paramInt2);
/*  768 */         if (localFile1 != null) {
/*  769 */           String str1 = localJFileChooser.getName(localFile1);
/*  770 */           String str2 = localFile1.getName();
/*  771 */           String str3 = ((String)paramObject).trim();
/*      */ 
/*  774 */           if (!str3.equals(str1)) {
/*  775 */             String str4 = str3;
/*      */ 
/*  777 */             int i = str2.length();
/*  778 */             int j = str1.length();
/*  779 */             if ((i > j) && (str2.charAt(j) == '.')) {
/*  780 */               str4 = str3 + str2.substring(j);
/*      */             }
/*      */ 
/*  784 */             FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
/*  785 */             final File localFile2 = localFileSystemView.createFileObject(localFile1.getParentFile(), str4);
/*  786 */             if (localFile2.exists()) {
/*  787 */               JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(FilePane.this.renameErrorFileExistsText, new Object[] { str2 }), FilePane.this.renameErrorTitleText, 0);
/*      */             }
/*  790 */             else if (FilePane.this.getModel().renameFile(localFile1, localFile2)) {
/*  791 */               if (localFileSystemView.isParent(localJFileChooser.getCurrentDirectory(), localFile2))
/*      */               {
/*  795 */                 SwingUtilities.invokeLater(new Runnable() {
/*      */                   public void run() {
/*  797 */                     if (localJFileChooser.isMultiSelectionEnabled())
/*  798 */                       localJFileChooser.setSelectedFiles(new File[] { localFile2 });
/*      */                     else {
/*  800 */                       localJFileChooser.setSelectedFile(localFile2);
/*      */                     }
/*      */                   }
/*      */                 });
/*      */               }
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*  809 */               JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(FilePane.this.renameErrorText, new Object[] { str2 }), FilePane.this.renameErrorTitleText, 0);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean isCellEditable(int paramInt1, int paramInt2)
/*      */     {
/*  819 */       File localFile = FilePane.this.getFileChooser().getCurrentDirectory();
/*  820 */       return (!FilePane.this.readOnly) && (paramInt2 == 0) && (FilePane.this.canWrite(localFile));
/*      */     }
/*      */ 
/*      */     public void contentsChanged(ListDataEvent paramListDataEvent)
/*      */     {
/*  825 */       new FilePane.DelayedSelectionUpdater(FilePane.this);
/*  826 */       fireTableDataChanged();
/*      */     }
/*      */ 
/*      */     public void intervalAdded(ListDataEvent paramListDataEvent) {
/*  830 */       int i = paramListDataEvent.getIndex0();
/*  831 */       int j = paramListDataEvent.getIndex1();
/*  832 */       if (i == j) {
/*  833 */         File localFile = (File)FilePane.this.getModel().getElementAt(i);
/*  834 */         if (localFile.equals(FilePane.this.newFolderFile)) {
/*  835 */           new FilePane.DelayedSelectionUpdater(FilePane.this, localFile);
/*  836 */           FilePane.this.newFolderFile = null;
/*      */         }
/*      */       }
/*      */ 
/*  840 */       fireTableRowsInserted(paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*      */     }
/*      */     public void intervalRemoved(ListDataEvent paramListDataEvent) {
/*  843 */       fireTableRowsDeleted(paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*      */     }
/*      */ 
/*      */     public ShellFolderColumnInfo[] getColumns() {
/*  847 */       return this.columns;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DetailsTableRowSorter extends TableRowSorter<TableModel>
/*      */   {
/*      */     public DetailsTableRowSorter()
/*      */     {
/*  899 */       setModelWrapper(new SorterModelWrapper(null));
/*      */     }
/*      */ 
/*      */     public void updateComparators(ShellFolderColumnInfo[] paramArrayOfShellFolderColumnInfo) {
/*  903 */       for (int i = 0; i < paramArrayOfShellFolderColumnInfo.length; i++) {
/*  904 */         Object localObject = paramArrayOfShellFolderColumnInfo[i].getComparator();
/*  905 */         if (localObject != null) {
/*  906 */           localObject = new FilePane.DirectoriesFirstComparatorWrapper(FilePane.this, i, (Comparator)localObject);
/*      */         }
/*  908 */         setComparator(i, (Comparator)localObject);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void sort()
/*      */     {
/*  914 */       ShellFolder.invoke(new Callable() {
/*      */         public Void call() {
/*  916 */           FilePane.DetailsTableRowSorter.this.sort();
/*  917 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public void modelStructureChanged() {
/*  923 */       super.modelStructureChanged();
/*  924 */       updateComparators(FilePane.this.detailsTableModel.getColumns());
/*      */     }
/*      */     private class SorterModelWrapper extends DefaultRowSorter.ModelWrapper<TableModel, Integer> {
/*      */       private SorterModelWrapper() {
/*      */       }
/*  929 */       public TableModel getModel() { return FilePane.this.getDetailsTableModel(); }
/*      */ 
/*      */       public int getColumnCount()
/*      */       {
/*  933 */         return FilePane.this.getDetailsTableModel().getColumnCount();
/*      */       }
/*      */ 
/*      */       public int getRowCount() {
/*  937 */         return FilePane.this.getDetailsTableModel().getRowCount();
/*      */       }
/*      */ 
/*      */       public Object getValueAt(int paramInt1, int paramInt2) {
/*  941 */         return FilePane.this.getModel().getElementAt(paramInt1);
/*      */       }
/*      */ 
/*      */       public Integer getIdentifier(int paramInt) {
/*  945 */         return Integer.valueOf(paramInt);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DirectoriesFirstComparatorWrapper
/*      */     implements Comparator<File>
/*      */   {
/*      */     private Comparator comparator;
/*      */     private int column;
/*      */ 
/*      */     public DirectoriesFirstComparatorWrapper(int paramComparator, Comparator arg3)
/*      */     {
/*  959 */       this.column = paramComparator;
/*      */       Object localObject;
/*  960 */       this.comparator = localObject;
/*      */     }
/*      */ 
/*      */     public int compare(File paramFile1, File paramFile2) {
/*  964 */       if ((paramFile1 != null) && (paramFile2 != null)) {
/*  965 */         boolean bool1 = FilePane.this.getFileChooser().isTraversable(paramFile1);
/*  966 */         boolean bool2 = FilePane.this.getFileChooser().isTraversable(paramFile2);
/*      */ 
/*  968 */         if ((bool1) && (!bool2)) {
/*  969 */           return -1;
/*      */         }
/*  971 */         if ((!bool1) && (bool2)) {
/*  972 */           return 1;
/*      */         }
/*      */       }
/*  975 */       if (FilePane.this.detailsTableModel.getColumns()[this.column].isCompareByColumn()) {
/*  976 */         return this.comparator.compare(FilePane.access$900(FilePane.this).getFileColumnValue(paramFile1, this.column), FilePane.access$900(FilePane.this).getFileColumnValue(paramFile2, this.column));
/*      */       }
/*      */ 
/*  983 */       return this.comparator.compare(paramFile1, paramFile2);
/*      */     }
/*      */   }
/*      */ 
/*      */   class EditActionListener
/*      */     implements ActionListener
/*      */   {
/*      */     EditActionListener()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 1384 */       FilePane.this.applyEdit();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface FileChooserUIAccessor
/*      */   {
/*      */     public abstract JFileChooser getFileChooser();
/*      */ 
/*      */     public abstract BasicDirectoryModel getModel();
/*      */ 
/*      */     public abstract JPanel createList();
/*      */ 
/*      */     public abstract JPanel createDetailsView();
/*      */ 
/*      */     public abstract boolean isDirectorySelected();
/*      */ 
/*      */     public abstract File getDirectory();
/*      */ 
/*      */     public abstract Action getApproveSelectionAction();
/*      */ 
/*      */     public abstract Action getChangeToParentDirectoryAction();
/*      */ 
/*      */     public abstract Action getNewFolderAction();
/*      */ 
/*      */     public abstract MouseListener createDoubleClickListener(JList paramJList);
/*      */ 
/*      */     public abstract ListSelectionListener createListSelectionListener();
/*      */   }
/*      */ 
/*      */   protected class FileRenderer extends DefaultListCellRenderer
/*      */   {
/*      */     protected FileRenderer()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 1476 */       if ((FilePane.this.listViewWindowsStyle) && (!paramJList.isFocusOwner())) {
/* 1477 */         paramBoolean1 = false;
/*      */       }
/*      */ 
/* 1480 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/* 1481 */       File localFile = (File)paramObject;
/* 1482 */       String str = FilePane.this.getFileChooser().getName(localFile);
/* 1483 */       setText(str);
/* 1484 */       setFont(paramJList.getFont());
/*      */ 
/* 1486 */       Icon localIcon = FilePane.this.getFileChooser().getIcon(localFile);
/* 1487 */       if (localIcon != null) {
/* 1488 */         setIcon(localIcon);
/*      */       }
/* 1490 */       else if (FilePane.this.getFileChooser().getFileSystemView().isTraversable(localFile).booleanValue()) {
/* 1491 */         setText(str + File.separator);
/*      */       }
/*      */ 
/* 1495 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Handler
/*      */     implements MouseListener
/*      */   {
/*      */     private MouseListener doubleClickListener;
/*      */ 
/*      */     private Handler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void mouseClicked(MouseEvent paramMouseEvent)
/*      */     {
/* 1801 */       JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
/*      */       int i;
/*      */       Object localObject;
/* 1804 */       if ((localJComponent instanceof JList)) {
/* 1805 */         i = SwingUtilities2.loc2IndexFileList(FilePane.this.list, paramMouseEvent.getPoint());
/* 1806 */       } else if ((localJComponent instanceof JTable)) {
/* 1807 */         localObject = (JTable)localJComponent;
/* 1808 */         Point localPoint = paramMouseEvent.getPoint();
/* 1809 */         i = ((JTable)localObject).rowAtPoint(localPoint);
/*      */ 
/* 1811 */         boolean bool = SwingUtilities2.pointOutsidePrefSize((JTable)localObject, i, ((JTable)localObject).columnAtPoint(localPoint), localPoint);
/*      */ 
/* 1815 */         if ((bool) && (!FilePane.this.fullRowSelection)) {
/* 1816 */           return;
/*      */         }
/*      */ 
/* 1820 */         if ((i >= 0) && (FilePane.this.list != null) && (FilePane.this.listSelectionModel.isSelectedIndex(i)))
/*      */         {
/* 1825 */           Rectangle localRectangle = FilePane.this.list.getCellBounds(i, i);
/* 1826 */           paramMouseEvent = new MouseEvent(FilePane.this.list, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localRectangle.x + 1, localRectangle.y + localRectangle.height / 2, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), paramMouseEvent.getButton());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1835 */         return;
/*      */       }
/*      */ 
/* 1838 */       if ((i >= 0) && (SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
/* 1839 */         localObject = FilePane.this.getFileChooser();
/*      */ 
/* 1842 */         if ((paramMouseEvent.getClickCount() == 1) && ((localJComponent instanceof JList))) {
/* 1843 */           if (((!((JFileChooser)localObject).isMultiSelectionEnabled()) || (((JFileChooser)localObject).getSelectedFiles().length <= 1)) && (i >= 0) && (FilePane.this.listSelectionModel.isSelectedIndex(i)) && (FilePane.this.getEditIndex() == i) && (FilePane.this.editFile == null))
/*      */           {
/* 1847 */             FilePane.this.editFileName(i);
/*      */           }
/* 1849 */           else if (i >= 0)
/* 1850 */             FilePane.this.setEditIndex(i);
/*      */           else {
/* 1852 */             FilePane.this.resetEditIndex();
/*      */           }
/*      */         }
/* 1855 */         else if (paramMouseEvent.getClickCount() == 2)
/*      */         {
/* 1858 */           FilePane.this.resetEditIndex();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1863 */       if (getDoubleClickListener() != null)
/* 1864 */         getDoubleClickListener().mouseClicked(paramMouseEvent);
/*      */     }
/*      */ 
/*      */     public void mouseEntered(MouseEvent paramMouseEvent)
/*      */     {
/* 1869 */       JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
/* 1870 */       if ((localJComponent instanceof JTable)) {
/* 1871 */         JTable localJTable = (JTable)paramMouseEvent.getSource();
/*      */ 
/* 1873 */         TransferHandler localTransferHandler1 = FilePane.this.getFileChooser().getTransferHandler();
/* 1874 */         TransferHandler localTransferHandler2 = localJTable.getTransferHandler();
/* 1875 */         if (localTransferHandler1 != localTransferHandler2) {
/* 1876 */           localJTable.setTransferHandler(localTransferHandler1);
/*      */         }
/*      */ 
/* 1879 */         boolean bool = FilePane.this.getFileChooser().getDragEnabled();
/* 1880 */         if (bool != localJTable.getDragEnabled())
/* 1881 */           localJTable.setDragEnabled(bool);
/*      */       }
/* 1883 */       else if ((localJComponent instanceof JList))
/*      */       {
/* 1885 */         if (getDoubleClickListener() != null)
/* 1886 */           getDoubleClickListener().mouseEntered(paramMouseEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void mouseExited(MouseEvent paramMouseEvent)
/*      */     {
/* 1892 */       if ((paramMouseEvent.getSource() instanceof JList))
/*      */       {
/* 1894 */         if (getDoubleClickListener() != null)
/* 1895 */           getDoubleClickListener().mouseExited(paramMouseEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void mousePressed(MouseEvent paramMouseEvent)
/*      */     {
/* 1901 */       if ((paramMouseEvent.getSource() instanceof JList))
/*      */       {
/* 1903 */         if (getDoubleClickListener() != null)
/* 1904 */           getDoubleClickListener().mousePressed(paramMouseEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void mouseReleased(MouseEvent paramMouseEvent)
/*      */     {
/* 1910 */       if ((paramMouseEvent.getSource() instanceof JList))
/*      */       {
/* 1912 */         if (getDoubleClickListener() != null)
/* 1913 */           getDoubleClickListener().mouseReleased(paramMouseEvent);
/*      */       }
/*      */     }
/*      */ 
/*      */     private MouseListener getDoubleClickListener()
/*      */     {
/* 1920 */       if ((this.doubleClickListener == null) && (FilePane.this.list != null)) {
/* 1921 */         this.doubleClickListener = FilePane.this.fileChooserUIAccessor.createDoubleClickListener(FilePane.this.list);
/*      */       }
/*      */ 
/* 1924 */       return this.doubleClickListener;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SortableListModel extends AbstractListModel
/*      */     implements TableModelListener, RowSorterListener
/*      */   {
/*      */     public SortableListModel()
/*      */     {
/*  648 */       FilePane.this.getDetailsTableModel().addTableModelListener(this);
/*  649 */       FilePane.this.getRowSorter().addRowSorterListener(this);
/*      */     }
/*      */ 
/*      */     public int getSize() {
/*  653 */       return FilePane.this.getModel().getSize();
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt)
/*      */     {
/*  658 */       return FilePane.this.getModel().getElementAt(FilePane.this.getRowSorter().convertRowIndexToModel(paramInt));
/*      */     }
/*      */ 
/*      */     public void tableChanged(TableModelEvent paramTableModelEvent) {
/*  662 */       fireContentsChanged(this, 0, getSize());
/*      */     }
/*      */ 
/*      */     public void sorterChanged(RowSorterEvent paramRowSorterEvent) {
/*  666 */       fireContentsChanged(this, 0, getSize());
/*      */     }
/*      */   }
/*      */ 
/*      */   class ViewTypeAction extends AbstractAction
/*      */   {
/*      */     private int viewType;
/*      */ 
/*      */     ViewTypeAction(int arg2)
/*      */     {
/*  394 */       super();
/*  395 */       this.viewType = i;
/*      */       String str;
/*  398 */       switch (i) { case 0:
/*  399 */         str = "viewTypeList"; break;
/*      */       case 1:
/*  400 */         str = "viewTypeDetails"; break;
/*      */       default:
/*  401 */         str = (String)getValue("Name");
/*      */       }
/*  403 */       putValue("ActionCommandKey", str);
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/*  407 */       FilePane.this.setViewType(this.viewType);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.FilePane
 * JD-Core Version:    0.6.2
 */