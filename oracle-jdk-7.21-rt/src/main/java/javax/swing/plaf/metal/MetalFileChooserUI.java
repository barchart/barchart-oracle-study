/*      */ package javax.swing.plaf.metal;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.FocusAdapter;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Arrays;
/*      */ import java.util.Locale;
/*      */ import java.util.Vector;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.AbstractListModel;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.ComboBoxModel;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JToggleButton;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.filechooser.FileFilter;
/*      */ import javax.swing.filechooser.FileSystemView;
/*      */ import javax.swing.filechooser.FileView;
/*      */ import javax.swing.plaf.ActionMapUIResource;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.basic.BasicDirectoryModel;
/*      */ import javax.swing.plaf.basic.BasicFileChooserUI;
/*      */ import sun.awt.shell.ShellFolder;
/*      */ import sun.swing.FilePane;
/*      */ import sun.swing.FilePane.FileChooserUIAccessor;
/*      */ 
/*      */ public class MetalFileChooserUI extends BasicFileChooserUI
/*      */ {
/*      */   private JLabel lookInLabel;
/*      */   private JComboBox directoryComboBox;
/*      */   private DirectoryComboBoxModel directoryComboBoxModel;
/*   63 */   private Action directoryComboBoxAction = new DirectoryComboBoxAction();
/*      */   private FilterComboBoxModel filterComboBoxModel;
/*      */   private JTextField fileNameTextField;
/*      */   private FilePane filePane;
/*      */   private JToggleButton listViewButton;
/*      */   private JToggleButton detailsViewButton;
/*      */   private JButton approveButton;
/*      */   private JButton cancelButton;
/*      */   private JPanel buttonPanel;
/*      */   private JPanel bottomPanel;
/*      */   private JComboBox filterComboBox;
/*   81 */   private static final Dimension hstrut5 = new Dimension(5, 1);
/*   82 */   private static final Dimension hstrut11 = new Dimension(11, 1);
/*      */ 
/*   84 */   private static final Dimension vstrut5 = new Dimension(1, 5);
/*      */ 
/*   86 */   private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
/*      */ 
/*   89 */   private static int PREF_WIDTH = 500;
/*   90 */   private static int PREF_HEIGHT = 326;
/*   91 */   private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
/*      */ 
/*   93 */   private static int MIN_WIDTH = 500;
/*   94 */   private static int MIN_HEIGHT = 326;
/*   95 */   private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);
/*      */ 
/*   97 */   private static int LIST_PREF_WIDTH = 405;
/*   98 */   private static int LIST_PREF_HEIGHT = 135;
/*   99 */   private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);
/*      */ 
/*  102 */   private int lookInLabelMnemonic = 0;
/*  103 */   private String lookInLabelText = null;
/*  104 */   private String saveInLabelText = null;
/*      */ 
/*  106 */   private int fileNameLabelMnemonic = 0;
/*  107 */   private String fileNameLabelText = null;
/*  108 */   private int folderNameLabelMnemonic = 0;
/*  109 */   private String folderNameLabelText = null;
/*      */ 
/*  111 */   private int filesOfTypeLabelMnemonic = 0;
/*  112 */   private String filesOfTypeLabelText = null;
/*      */ 
/*  114 */   private String upFolderToolTipText = null;
/*  115 */   private String upFolderAccessibleName = null;
/*      */ 
/*  117 */   private String homeFolderToolTipText = null;
/*  118 */   private String homeFolderAccessibleName = null;
/*      */ 
/*  120 */   private String newFolderToolTipText = null;
/*  121 */   private String newFolderAccessibleName = null;
/*      */ 
/*  123 */   private String listViewButtonToolTipText = null;
/*  124 */   private String listViewButtonAccessibleName = null;
/*      */ 
/*  126 */   private String detailsViewButtonToolTipText = null;
/*  127 */   private String detailsViewButtonAccessibleName = null;
/*      */   private AlignedLabel fileNameLabel;
/*      */   static final int space = 10;
/*      */ 
/*      */   private void populateFileNameLabel()
/*      */   {
/*  132 */     if (getFileChooser().getFileSelectionMode() == 1) {
/*  133 */       this.fileNameLabel.setText(this.folderNameLabelText);
/*  134 */       this.fileNameLabel.setDisplayedMnemonic(this.folderNameLabelMnemonic);
/*      */     } else {
/*  136 */       this.fileNameLabel.setText(this.fileNameLabelText);
/*  137 */       this.fileNameLabel.setDisplayedMnemonic(this.fileNameLabelMnemonic);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static ComponentUI createUI(JComponent paramJComponent)
/*      */   {
/*  145 */     return new MetalFileChooserUI((JFileChooser)paramJComponent);
/*      */   }
/*      */ 
/*      */   public MetalFileChooserUI(JFileChooser paramJFileChooser) {
/*  149 */     super(paramJFileChooser);
/*      */   }
/*      */ 
/*      */   public void installUI(JComponent paramJComponent) {
/*  153 */     super.installUI(paramJComponent);
/*      */   }
/*      */ 
/*      */   public void uninstallComponents(JFileChooser paramJFileChooser) {
/*  157 */     paramJFileChooser.removeAll();
/*  158 */     this.bottomPanel = null;
/*  159 */     this.buttonPanel = null;
/*      */   }
/*      */ 
/*      */   public void installComponents(JFileChooser paramJFileChooser)
/*      */   {
/*  210 */     FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
/*      */ 
/*  212 */     paramJFileChooser.setBorder(new EmptyBorder(12, 12, 11, 11));
/*  213 */     paramJFileChooser.setLayout(new BorderLayout(0, 11));
/*      */ 
/*  215 */     this.filePane = new FilePane(new MetalFileChooserUIAccessor(null));
/*  216 */     paramJFileChooser.addPropertyChangeListener(this.filePane);
/*      */ 
/*  223 */     JPanel localJPanel1 = new JPanel(new BorderLayout(11, 0));
/*  224 */     JPanel localJPanel2 = new JPanel();
/*  225 */     localJPanel2.setLayout(new BoxLayout(localJPanel2, 2));
/*  226 */     localJPanel1.add(localJPanel2, "After");
/*      */ 
/*  229 */     paramJFileChooser.add(localJPanel1, "North");
/*      */ 
/*  232 */     this.lookInLabel = new JLabel(this.lookInLabelText);
/*  233 */     this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
/*  234 */     localJPanel1.add(this.lookInLabel, "Before");
/*      */ 
/*  237 */     this.directoryComboBox = new JComboBox() {
/*      */       public Dimension getPreferredSize() {
/*  239 */         Dimension localDimension = super.getPreferredSize();
/*      */ 
/*  241 */         localDimension.width = 150;
/*  242 */         return localDimension;
/*      */       }
/*      */     };
/*  245 */     this.directoryComboBox.putClientProperty("AccessibleDescription", this.lookInLabelText);
/*      */ 
/*  247 */     this.directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
/*  248 */     this.lookInLabel.setLabelFor(this.directoryComboBox);
/*  249 */     this.directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
/*  250 */     this.directoryComboBox.setModel(this.directoryComboBoxModel);
/*  251 */     this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
/*  252 */     this.directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(paramJFileChooser));
/*  253 */     this.directoryComboBox.setAlignmentX(0.0F);
/*  254 */     this.directoryComboBox.setAlignmentY(0.0F);
/*  255 */     this.directoryComboBox.setMaximumRowCount(8);
/*      */ 
/*  257 */     localJPanel1.add(this.directoryComboBox, "Center");
/*      */ 
/*  260 */     JButton localJButton1 = new JButton(getChangeToParentDirectoryAction());
/*  261 */     localJButton1.setText(null);
/*  262 */     localJButton1.setIcon(this.upFolderIcon);
/*  263 */     localJButton1.setToolTipText(this.upFolderToolTipText);
/*  264 */     localJButton1.putClientProperty("AccessibleName", this.upFolderAccessibleName);
/*      */ 
/*  266 */     localJButton1.setAlignmentX(0.0F);
/*  267 */     localJButton1.setAlignmentY(0.5F);
/*  268 */     localJButton1.setMargin(shrinkwrap);
/*      */ 
/*  270 */     localJPanel2.add(localJButton1);
/*  271 */     localJPanel2.add(Box.createRigidArea(hstrut5));
/*      */ 
/*  274 */     File localFile = localFileSystemView.getHomeDirectory();
/*  275 */     String str = this.homeFolderToolTipText;
/*  276 */     if (localFileSystemView.isRoot(localFile)) {
/*  277 */       str = getFileView(paramJFileChooser).getName(localFile);
/*      */     }
/*      */ 
/*  283 */     JButton localJButton2 = new JButton(this.homeFolderIcon);
/*  284 */     localJButton2.setToolTipText(str);
/*  285 */     localJButton2.putClientProperty("AccessibleName", this.homeFolderAccessibleName);
/*      */ 
/*  287 */     localJButton2.setAlignmentX(0.0F);
/*  288 */     localJButton2.setAlignmentY(0.5F);
/*  289 */     localJButton2.setMargin(shrinkwrap);
/*      */ 
/*  291 */     localJButton2.addActionListener(getGoHomeAction());
/*  292 */     localJPanel2.add(localJButton2);
/*  293 */     localJPanel2.add(Box.createRigidArea(hstrut5));
/*      */ 
/*  296 */     if (!UIManager.getBoolean("FileChooser.readOnly")) {
/*  297 */       localJButton2 = new JButton(this.filePane.getNewFolderAction());
/*  298 */       localJButton2.setText(null);
/*  299 */       localJButton2.setIcon(this.newFolderIcon);
/*  300 */       localJButton2.setToolTipText(this.newFolderToolTipText);
/*  301 */       localJButton2.putClientProperty("AccessibleName", this.newFolderAccessibleName);
/*      */ 
/*  303 */       localJButton2.setAlignmentX(0.0F);
/*  304 */       localJButton2.setAlignmentY(0.5F);
/*  305 */       localJButton2.setMargin(shrinkwrap);
/*      */     }
/*  307 */     localJPanel2.add(localJButton2);
/*  308 */     localJPanel2.add(Box.createRigidArea(hstrut5));
/*      */ 
/*  311 */     ButtonGroup localButtonGroup = new ButtonGroup();
/*      */ 
/*  314 */     this.listViewButton = new JToggleButton(this.listViewIcon);
/*  315 */     this.listViewButton.setToolTipText(this.listViewButtonToolTipText);
/*  316 */     this.listViewButton.putClientProperty("AccessibleName", this.listViewButtonAccessibleName);
/*      */ 
/*  318 */     this.listViewButton.setSelected(true);
/*  319 */     this.listViewButton.setAlignmentX(0.0F);
/*  320 */     this.listViewButton.setAlignmentY(0.5F);
/*  321 */     this.listViewButton.setMargin(shrinkwrap);
/*  322 */     this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
/*  323 */     localJPanel2.add(this.listViewButton);
/*  324 */     localButtonGroup.add(this.listViewButton);
/*      */ 
/*  327 */     this.detailsViewButton = new JToggleButton(this.detailsViewIcon);
/*  328 */     this.detailsViewButton.setToolTipText(this.detailsViewButtonToolTipText);
/*  329 */     this.detailsViewButton.putClientProperty("AccessibleName", this.detailsViewButtonAccessibleName);
/*      */ 
/*  331 */     this.detailsViewButton.setAlignmentX(0.0F);
/*  332 */     this.detailsViewButton.setAlignmentY(0.5F);
/*  333 */     this.detailsViewButton.setMargin(shrinkwrap);
/*  334 */     this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
/*  335 */     localJPanel2.add(this.detailsViewButton);
/*  336 */     localButtonGroup.add(this.detailsViewButton);
/*      */ 
/*  338 */     this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/*  340 */         if ("viewType".equals(paramAnonymousPropertyChangeEvent.getPropertyName())) {
/*  341 */           int i = MetalFileChooserUI.this.filePane.getViewType();
/*  342 */           switch (i) {
/*      */           case 0:
/*  344 */             MetalFileChooserUI.this.listViewButton.setSelected(true);
/*  345 */             break;
/*      */           case 1:
/*  348 */             MetalFileChooserUI.this.detailsViewButton.setSelected(true);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  358 */     paramJFileChooser.add(getAccessoryPanel(), "After");
/*  359 */     JComponent localJComponent = paramJFileChooser.getAccessory();
/*  360 */     if (localJComponent != null) {
/*  361 */       getAccessoryPanel().add(localJComponent);
/*      */     }
/*  363 */     this.filePane.setPreferredSize(LIST_PREF_SIZE);
/*  364 */     paramJFileChooser.add(this.filePane, "Center");
/*      */ 
/*  369 */     JPanel localJPanel3 = getBottomPanel();
/*  370 */     localJPanel3.setLayout(new BoxLayout(localJPanel3, 1));
/*  371 */     paramJFileChooser.add(localJPanel3, "South");
/*      */ 
/*  374 */     JPanel localJPanel4 = new JPanel();
/*  375 */     localJPanel4.setLayout(new BoxLayout(localJPanel4, 2));
/*  376 */     localJPanel3.add(localJPanel4);
/*  377 */     localJPanel3.add(Box.createRigidArea(vstrut5));
/*      */ 
/*  379 */     this.fileNameLabel = new AlignedLabel();
/*  380 */     populateFileNameLabel();
/*  381 */     localJPanel4.add(this.fileNameLabel);
/*      */ 
/*  383 */     this.fileNameTextField = new JTextField(35) {
/*      */       public Dimension getMaximumSize() {
/*  385 */         return new Dimension(32767, super.getPreferredSize().height);
/*      */       }
/*      */     };
/*  388 */     localJPanel4.add(this.fileNameTextField);
/*  389 */     this.fileNameLabel.setLabelFor(this.fileNameTextField);
/*  390 */     this.fileNameTextField.addFocusListener(new FocusAdapter()
/*      */     {
/*      */       public void focusGained(FocusEvent paramAnonymousFocusEvent) {
/*  393 */         if (!MetalFileChooserUI.this.getFileChooser().isMultiSelectionEnabled())
/*  394 */           MetalFileChooserUI.this.filePane.clearSelection();
/*      */       }
/*      */     });
/*  399 */     if (paramJFileChooser.isMultiSelectionEnabled())
/*  400 */       setFileName(fileNameString(paramJFileChooser.getSelectedFiles()));
/*      */     else {
/*  402 */       setFileName(fileNameString(paramJFileChooser.getSelectedFile()));
/*      */     }
/*      */ 
/*  407 */     JPanel localJPanel5 = new JPanel();
/*  408 */     localJPanel5.setLayout(new BoxLayout(localJPanel5, 2));
/*  409 */     localJPanel3.add(localJPanel5);
/*      */ 
/*  411 */     AlignedLabel localAlignedLabel = new AlignedLabel(this.filesOfTypeLabelText);
/*  412 */     localAlignedLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
/*  413 */     localJPanel5.add(localAlignedLabel);
/*      */ 
/*  415 */     this.filterComboBoxModel = createFilterComboBoxModel();
/*  416 */     paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
/*  417 */     this.filterComboBox = new JComboBox(this.filterComboBoxModel);
/*  418 */     this.filterComboBox.putClientProperty("AccessibleDescription", this.filesOfTypeLabelText);
/*      */ 
/*  420 */     localAlignedLabel.setLabelFor(this.filterComboBox);
/*  421 */     this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
/*  422 */     localJPanel5.add(this.filterComboBox);
/*      */ 
/*  425 */     getButtonPanel().setLayout(new ButtonAreaLayout(null));
/*      */ 
/*  427 */     this.approveButton = new JButton(getApproveButtonText(paramJFileChooser));
/*      */ 
/*  429 */     this.approveButton.addActionListener(getApproveSelectionAction());
/*  430 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
/*  431 */     getButtonPanel().add(this.approveButton);
/*      */ 
/*  433 */     this.cancelButton = new JButton(this.cancelButtonText);
/*  434 */     this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
/*  435 */     this.cancelButton.addActionListener(getCancelSelectionAction());
/*  436 */     getButtonPanel().add(this.cancelButton);
/*      */ 
/*  438 */     if (paramJFileChooser.getControlButtonsAreShown()) {
/*  439 */       addControlButtons();
/*      */     }
/*      */ 
/*  442 */     groupLabels(new AlignedLabel[] { this.fileNameLabel, localAlignedLabel });
/*      */   }
/*      */ 
/*      */   protected JPanel getButtonPanel() {
/*  446 */     if (this.buttonPanel == null) {
/*  447 */       this.buttonPanel = new JPanel();
/*      */     }
/*  449 */     return this.buttonPanel;
/*      */   }
/*      */ 
/*      */   protected JPanel getBottomPanel() {
/*  453 */     if (this.bottomPanel == null) {
/*  454 */       this.bottomPanel = new JPanel();
/*      */     }
/*  456 */     return this.bottomPanel;
/*      */   }
/*      */ 
/*      */   protected void installStrings(JFileChooser paramJFileChooser) {
/*  460 */     super.installStrings(paramJFileChooser);
/*      */ 
/*  462 */     Locale localLocale = paramJFileChooser.getLocale();
/*      */ 
/*  464 */     this.lookInLabelMnemonic = UIManager.getInt("FileChooser.lookInLabelMnemonic");
/*  465 */     this.lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", localLocale);
/*  466 */     this.saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", localLocale);
/*      */ 
/*  468 */     this.fileNameLabelMnemonic = UIManager.getInt("FileChooser.fileNameLabelMnemonic");
/*  469 */     this.fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", localLocale);
/*  470 */     this.folderNameLabelMnemonic = UIManager.getInt("FileChooser.folderNameLabelMnemonic");
/*  471 */     this.folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", localLocale);
/*      */ 
/*  473 */     this.filesOfTypeLabelMnemonic = UIManager.getInt("FileChooser.filesOfTypeLabelMnemonic");
/*  474 */     this.filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", localLocale);
/*      */ 
/*  476 */     this.upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", localLocale);
/*  477 */     this.upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", localLocale);
/*      */ 
/*  479 */     this.homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", localLocale);
/*  480 */     this.homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", localLocale);
/*      */ 
/*  482 */     this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", localLocale);
/*  483 */     this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", localLocale);
/*      */ 
/*  485 */     this.listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", localLocale);
/*  486 */     this.listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", localLocale);
/*      */ 
/*  488 */     this.detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", localLocale);
/*  489 */     this.detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", localLocale);
/*      */   }
/*      */ 
/*      */   protected void installListeners(JFileChooser paramJFileChooser) {
/*  493 */     super.installListeners(paramJFileChooser);
/*  494 */     ActionMap localActionMap = getActionMap();
/*  495 */     SwingUtilities.replaceUIActionMap(paramJFileChooser, localActionMap);
/*      */   }
/*      */ 
/*      */   protected ActionMap getActionMap() {
/*  499 */     return createActionMap();
/*      */   }
/*      */ 
/*      */   protected ActionMap createActionMap() {
/*  503 */     ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
/*  504 */     FilePane.addActionsToMap(localActionMapUIResource, this.filePane.getActions());
/*  505 */     return localActionMapUIResource;
/*      */   }
/*      */ 
/*      */   protected JPanel createList(JFileChooser paramJFileChooser) {
/*  509 */     return this.filePane.createList();
/*      */   }
/*      */ 
/*      */   protected JPanel createDetailsView(JFileChooser paramJFileChooser) {
/*  513 */     return this.filePane.createDetailsView();
/*      */   }
/*      */ 
/*      */   public ListSelectionListener createListSelectionListener(JFileChooser paramJFileChooser)
/*      */   {
/*  523 */     return super.createListSelectionListener(paramJFileChooser);
/*      */   }
/*      */ 
/*      */   public void uninstallUI(JComponent paramJComponent)
/*      */   {
/*  538 */     paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
/*  539 */     paramJComponent.removePropertyChangeListener(this.filePane);
/*  540 */     this.cancelButton.removeActionListener(getCancelSelectionAction());
/*  541 */     this.approveButton.removeActionListener(getApproveSelectionAction());
/*  542 */     this.fileNameTextField.removeActionListener(getApproveSelectionAction());
/*      */ 
/*  544 */     if (this.filePane != null) {
/*  545 */       this.filePane.uninstallUI();
/*  546 */       this.filePane = null;
/*      */     }
/*      */ 
/*  549 */     super.uninstallUI(paramJComponent);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(JComponent paramJComponent)
/*      */   {
/*  565 */     int i = PREF_SIZE.width;
/*  566 */     Dimension localDimension = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
/*  567 */     if (localDimension != null) {
/*  568 */       return new Dimension(localDimension.width < i ? i : localDimension.width, localDimension.height < PREF_SIZE.height ? PREF_SIZE.height : localDimension.height);
/*      */     }
/*      */ 
/*  571 */     return new Dimension(i, PREF_SIZE.height);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(JComponent paramJComponent)
/*      */   {
/*  583 */     return MIN_SIZE;
/*      */   }
/*      */ 
/*      */   public Dimension getMaximumSize(JComponent paramJComponent)
/*      */   {
/*  594 */     return new Dimension(2147483647, 2147483647);
/*      */   }
/*      */ 
/*      */   private String fileNameString(File paramFile) {
/*  598 */     if (paramFile == null) {
/*  599 */       return null;
/*      */     }
/*  601 */     JFileChooser localJFileChooser = getFileChooser();
/*  602 */     if (((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) || ((localJFileChooser.isDirectorySelectionEnabled()) && (localJFileChooser.isFileSelectionEnabled()) && (localJFileChooser.getFileSystemView().isFileSystemRoot(paramFile))))
/*      */     {
/*  604 */       return paramFile.getPath();
/*      */     }
/*  606 */     return paramFile.getName();
/*      */   }
/*      */ 
/*      */   private String fileNameString(File[] paramArrayOfFile)
/*      */   {
/*  612 */     StringBuffer localStringBuffer = new StringBuffer();
/*  613 */     for (int i = 0; (paramArrayOfFile != null) && (i < paramArrayOfFile.length); i++) {
/*  614 */       if (i > 0) {
/*  615 */         localStringBuffer.append(" ");
/*      */       }
/*  617 */       if (paramArrayOfFile.length > 1) {
/*  618 */         localStringBuffer.append("\"");
/*      */       }
/*  620 */       localStringBuffer.append(fileNameString(paramArrayOfFile[i]));
/*  621 */       if (paramArrayOfFile.length > 1) {
/*  622 */         localStringBuffer.append("\"");
/*      */       }
/*      */     }
/*  625 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  631 */     File localFile = (File)paramPropertyChangeEvent.getNewValue();
/*  632 */     JFileChooser localJFileChooser = getFileChooser();
/*  633 */     if ((localFile != null) && (((localJFileChooser.isFileSelectionEnabled()) && (!localFile.isDirectory())) || ((localFile.isDirectory()) && (localJFileChooser.isDirectorySelectionEnabled()))))
/*      */     {
/*  637 */       setFileName(fileNameString(localFile));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  642 */     File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
/*  643 */     JFileChooser localJFileChooser = getFileChooser();
/*  644 */     if ((arrayOfFile != null) && (arrayOfFile.length > 0) && ((arrayOfFile.length > 1) || (localJFileChooser.isDirectorySelectionEnabled()) || (!arrayOfFile[0].isDirectory())))
/*      */     {
/*  647 */       setFileName(fileNameString(arrayOfFile));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  652 */     JFileChooser localJFileChooser = getFileChooser();
/*  653 */     FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
/*      */ 
/*  655 */     clearIconCache();
/*  656 */     File localFile = localJFileChooser.getCurrentDirectory();
/*  657 */     if (localFile != null) {
/*  658 */       this.directoryComboBoxModel.addItem(localFile);
/*      */ 
/*  660 */       if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled()))
/*  661 */         if (localFileSystemView.isFileSystem(localFile))
/*  662 */           setFileName(localFile.getPath());
/*      */         else
/*  664 */           setFileName(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doFilterChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  671 */     clearIconCache();
/*      */   }
/*      */ 
/*      */   private void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  675 */     if (this.fileNameLabel != null) {
/*  676 */       populateFileNameLabel();
/*      */     }
/*  678 */     clearIconCache();
/*      */ 
/*  680 */     JFileChooser localJFileChooser = getFileChooser();
/*  681 */     File localFile = localJFileChooser.getCurrentDirectory();
/*  682 */     if ((localFile != null) && (localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled()) && (localJFileChooser.getFileSystemView().isFileSystem(localFile)))
/*      */     {
/*  687 */       setFileName(localFile.getPath());
/*      */     }
/*  689 */     else setFileName(null);
/*      */   }
/*      */ 
/*      */   private void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  694 */     if (getAccessoryPanel() != null) {
/*  695 */       if (paramPropertyChangeEvent.getOldValue() != null) {
/*  696 */         getAccessoryPanel().remove((JComponent)paramPropertyChangeEvent.getOldValue());
/*      */       }
/*  698 */       JComponent localJComponent = (JComponent)paramPropertyChangeEvent.getNewValue();
/*  699 */       if (localJComponent != null)
/*  700 */         getAccessoryPanel().add(localJComponent, "Center");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doApproveButtonTextChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  706 */     JFileChooser localJFileChooser = getFileChooser();
/*  707 */     this.approveButton.setText(getApproveButtonText(localJFileChooser));
/*  708 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
/*      */   }
/*      */ 
/*      */   private void doDialogTypeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  712 */     JFileChooser localJFileChooser = getFileChooser();
/*  713 */     this.approveButton.setText(getApproveButtonText(localJFileChooser));
/*  714 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
/*  715 */     if (localJFileChooser.getDialogType() == 1)
/*  716 */       this.lookInLabel.setText(this.saveInLabelText);
/*      */     else
/*  718 */       this.lookInLabel.setText(this.lookInLabelText);
/*      */   }
/*      */ 
/*      */   private void doApproveButtonMnemonicChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*      */   }
/*      */ 
/*      */   private void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  727 */     if (getFileChooser().getControlButtonsAreShown())
/*  728 */       addControlButtons();
/*      */     else
/*  730 */       removeControlButtons();
/*      */   }
/*      */ 
/*      */   public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser)
/*      */   {
/*  739 */     return new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/*  741 */         String str = paramAnonymousPropertyChangeEvent.getPropertyName();
/*  742 */         if (str.equals("SelectedFileChangedProperty")) {
/*  743 */           MetalFileChooserUI.this.doSelectedFileChanged(paramAnonymousPropertyChangeEvent);
/*  744 */         } else if (str.equals("SelectedFilesChangedProperty")) {
/*  745 */           MetalFileChooserUI.this.doSelectedFilesChanged(paramAnonymousPropertyChangeEvent);
/*  746 */         } else if (str.equals("directoryChanged")) {
/*  747 */           MetalFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
/*  748 */         } else if (str.equals("fileFilterChanged")) {
/*  749 */           MetalFileChooserUI.this.doFilterChanged(paramAnonymousPropertyChangeEvent);
/*  750 */         } else if (str.equals("fileSelectionChanged")) {
/*  751 */           MetalFileChooserUI.this.doFileSelectionModeChanged(paramAnonymousPropertyChangeEvent);
/*  752 */         } else if (str.equals("AccessoryChangedProperty")) {
/*  753 */           MetalFileChooserUI.this.doAccessoryChanged(paramAnonymousPropertyChangeEvent);
/*  754 */         } else if ((str.equals("ApproveButtonTextChangedProperty")) || (str.equals("ApproveButtonToolTipTextChangedProperty")))
/*      */         {
/*  756 */           MetalFileChooserUI.this.doApproveButtonTextChanged(paramAnonymousPropertyChangeEvent);
/*  757 */         } else if (str.equals("DialogTypeChangedProperty")) {
/*  758 */           MetalFileChooserUI.this.doDialogTypeChanged(paramAnonymousPropertyChangeEvent);
/*  759 */         } else if (str.equals("ApproveButtonMnemonicChangedProperty")) {
/*  760 */           MetalFileChooserUI.this.doApproveButtonMnemonicChanged(paramAnonymousPropertyChangeEvent);
/*  761 */         } else if (str.equals("ControlButtonsAreShownChangedProperty")) {
/*  762 */           MetalFileChooserUI.this.doControlButtonsChanged(paramAnonymousPropertyChangeEvent);
/*  763 */         } else if (str.equals("componentOrientation")) {
/*  764 */           ComponentOrientation localComponentOrientation = (ComponentOrientation)paramAnonymousPropertyChangeEvent.getNewValue();
/*  765 */           JFileChooser localJFileChooser = (JFileChooser)paramAnonymousPropertyChangeEvent.getSource();
/*  766 */           if (localComponentOrientation != paramAnonymousPropertyChangeEvent.getOldValue())
/*  767 */             localJFileChooser.applyComponentOrientation(localComponentOrientation);
/*      */         }
/*  769 */         else if (str == "FileChooser.useShellFolder") {
/*  770 */           MetalFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
/*  771 */         } else if ((str.equals("ancestor")) && 
/*  772 */           (paramAnonymousPropertyChangeEvent.getOldValue() == null) && (paramAnonymousPropertyChangeEvent.getNewValue() != null))
/*      */         {
/*  774 */           MetalFileChooserUI.this.fileNameTextField.selectAll();
/*  775 */           MetalFileChooserUI.this.fileNameTextField.requestFocus();
/*      */         }
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   protected void removeControlButtons()
/*      */   {
/*  784 */     getBottomPanel().remove(getButtonPanel());
/*      */   }
/*      */ 
/*      */   protected void addControlButtons() {
/*  788 */     getBottomPanel().add(getButtonPanel());
/*      */   }
/*      */ 
/*      */   public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {
/*  792 */     this.filePane.ensureFileIsVisible(paramJFileChooser, paramFile);
/*      */   }
/*      */ 
/*      */   public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {
/*  796 */     this.filePane.rescanCurrentDirectory();
/*      */   }
/*      */ 
/*      */   public String getFileName() {
/*  800 */     if (this.fileNameTextField != null) {
/*  801 */       return this.fileNameTextField.getText();
/*      */     }
/*  803 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFileName(String paramString)
/*      */   {
/*  808 */     if (this.fileNameTextField != null)
/*  809 */       this.fileNameTextField.setText(paramString);
/*      */   }
/*      */ 
/*      */   protected void setDirectorySelected(boolean paramBoolean)
/*      */   {
/*  821 */     super.setDirectorySelected(paramBoolean);
/*  822 */     JFileChooser localJFileChooser = getFileChooser();
/*  823 */     if (paramBoolean) {
/*  824 */       if (this.approveButton != null) {
/*  825 */         this.approveButton.setText(this.directoryOpenButtonText);
/*  826 */         this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
/*      */       }
/*      */     }
/*  829 */     else if (this.approveButton != null) {
/*  830 */       this.approveButton.setText(getApproveButtonText(localJFileChooser));
/*  831 */       this.approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getDirectoryName()
/*      */   {
/*  838 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDirectoryName(String paramString)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser) {
/*  846 */     return new DirectoryComboBoxRenderer();
/*      */   }
/*      */ 
/*      */   protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser)
/*      */   {
/*  903 */     return new DirectoryComboBoxModel();
/*      */   }
/*      */ 
/*      */   protected FilterComboBoxRenderer createFilterComboBoxRenderer()
/*      */   {
/* 1035 */     return new FilterComboBoxRenderer();
/*      */   }
/*      */ 
/*      */   protected FilterComboBoxModel createFilterComboBoxModel()
/*      */   {
/* 1060 */     return new FilterComboBoxModel();
/*      */   }
/*      */ 
/*      */   public void valueChanged(ListSelectionEvent paramListSelectionEvent)
/*      */   {
/* 1133 */     JFileChooser localJFileChooser = getFileChooser();
/* 1134 */     File localFile = localJFileChooser.getSelectedFile();
/* 1135 */     if ((!paramListSelectionEvent.getValueIsAdjusting()) && (localFile != null) && (!getFileChooser().isTraversable(localFile)))
/* 1136 */       setFileName(fileNameString(localFile));
/*      */   }
/*      */ 
/*      */   protected JButton getApproveButton(JFileChooser paramJFileChooser)
/*      */   {
/* 1158 */     return this.approveButton;
/*      */   }
/*      */ 
/*      */   private static void groupLabels(AlignedLabel[] paramArrayOfAlignedLabel)
/*      */   {
/* 1238 */     for (int i = 0; i < paramArrayOfAlignedLabel.length; i++)
/* 1239 */       paramArrayOfAlignedLabel[i].group = paramArrayOfAlignedLabel;
/*      */   }
/*      */ 
/*      */   private class AlignedLabel extends JLabel
/*      */   {
/*      */     private AlignedLabel[] group;
/* 1245 */     private int maxWidth = 0;
/*      */ 
/*      */     AlignedLabel()
/*      */     {
/* 1249 */       setAlignmentX(0.0F);
/*      */     }
/*      */ 
/*      */     AlignedLabel(String arg2)
/*      */     {
/* 1254 */       super();
/* 1255 */       setAlignmentX(0.0F);
/*      */     }
/*      */ 
/*      */     public Dimension getPreferredSize() {
/* 1259 */       Dimension localDimension = super.getPreferredSize();
/*      */ 
/* 1261 */       return new Dimension(getMaxWidth() + 11, localDimension.height);
/*      */     }
/*      */ 
/*      */     private int getMaxWidth() {
/* 1265 */       if ((this.maxWidth == 0) && (this.group != null)) {
/* 1266 */         int i = 0;
/* 1267 */         for (int j = 0; j < this.group.length; j++) {
/* 1268 */           i = Math.max(this.group[j].getSuperPreferredWidth(), i);
/*      */         }
/* 1270 */         for (j = 0; j < this.group.length; j++) {
/* 1271 */           this.group[j].maxWidth = i;
/*      */         }
/*      */       }
/* 1274 */       return this.maxWidth;
/*      */     }
/*      */ 
/*      */     private int getSuperPreferredWidth() {
/* 1278 */       return super.getPreferredSize().width;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ButtonAreaLayout
/*      */     implements LayoutManager
/*      */   {
/* 1169 */     private int hGap = 5;
/* 1170 */     private int topMargin = 17;
/*      */ 
/*      */     public void addLayoutComponent(String paramString, Component paramComponent) {
/*      */     }
/*      */ 
/*      */     public void layoutContainer(Container paramContainer) {
/* 1176 */       Component[] arrayOfComponent = paramContainer.getComponents();
/*      */ 
/* 1178 */       if ((arrayOfComponent != null) && (arrayOfComponent.length > 0)) {
/* 1179 */         int i = arrayOfComponent.length;
/* 1180 */         Dimension[] arrayOfDimension = new Dimension[i];
/* 1181 */         Insets localInsets = paramContainer.getInsets();
/* 1182 */         int j = localInsets.top + this.topMargin;
/* 1183 */         int k = 0;
/*      */ 
/* 1185 */         for (int m = 0; m < i; m++) {
/* 1186 */           arrayOfDimension[m] = arrayOfComponent[m].getPreferredSize();
/* 1187 */           k = Math.max(k, arrayOfDimension[m].width);
/*      */         }
/*      */         int n;
/* 1190 */         if (paramContainer.getComponentOrientation().isLeftToRight()) {
/* 1191 */           m = paramContainer.getSize().width - localInsets.left - k;
/* 1192 */           n = this.hGap + k;
/*      */         } else {
/* 1194 */           m = localInsets.left;
/* 1195 */           n = -(this.hGap + k);
/*      */         }
/* 1197 */         for (int i1 = i - 1; i1 >= 0; i1--) {
/* 1198 */           arrayOfComponent[i1].setBounds(m, j, k, arrayOfDimension[i1].height);
/*      */ 
/* 1200 */           m -= n;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public Dimension minimumLayoutSize(Container paramContainer) {
/* 1206 */       if (paramContainer != null) {
/* 1207 */         Component[] arrayOfComponent = paramContainer.getComponents();
/*      */ 
/* 1209 */         if ((arrayOfComponent != null) && (arrayOfComponent.length > 0)) {
/* 1210 */           int i = arrayOfComponent.length;
/* 1211 */           int j = 0;
/* 1212 */           Insets localInsets = paramContainer.getInsets();
/* 1213 */           int k = this.topMargin + localInsets.top + localInsets.bottom;
/* 1214 */           int m = localInsets.left + localInsets.right;
/* 1215 */           int n = 0;
/*      */ 
/* 1217 */           for (int i1 = 0; i1 < i; i1++) {
/* 1218 */             Dimension localDimension = arrayOfComponent[i1].getPreferredSize();
/* 1219 */             j = Math.max(j, localDimension.height);
/* 1220 */             n = Math.max(n, localDimension.width);
/*      */           }
/* 1222 */           return new Dimension(m + i * n + (i - 1) * this.hGap, k + j);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1227 */       return new Dimension(0, 0);
/*      */     }
/*      */ 
/*      */     public Dimension preferredLayoutSize(Container paramContainer) {
/* 1231 */       return minimumLayoutSize(paramContainer);
/*      */     }
/*      */ 
/*      */     public void removeLayoutComponent(Component paramComponent)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class DirectoryComboBoxAction extends AbstractAction
/*      */   {
/*      */     protected DirectoryComboBoxAction()
/*      */     {
/* 1145 */       super();
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1149 */       MetalFileChooserUI.this.directoryComboBox.hidePopup();
/* 1150 */       File localFile = (File)MetalFileChooserUI.this.directoryComboBox.getSelectedItem();
/* 1151 */       if (!MetalFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(localFile))
/* 1152 */         MetalFileChooserUI.this.getFileChooser().setCurrentDirectory(localFile);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class DirectoryComboBoxModel extends AbstractListModel<Object>
/*      */     implements ComboBoxModel<Object>
/*      */   {
/*  910 */     Vector<File> directories = new Vector();
/*  911 */     int[] depths = null;
/*  912 */     File selectedDirectory = null;
/*  913 */     JFileChooser chooser = MetalFileChooserUI.this.getFileChooser();
/*  914 */     FileSystemView fsv = this.chooser.getFileSystemView();
/*      */ 
/*      */     public DirectoryComboBoxModel()
/*      */     {
/*  919 */       File localFile = MetalFileChooserUI.this.getFileChooser().getCurrentDirectory();
/*  920 */       if (localFile != null)
/*  921 */         addItem(localFile);
/*      */     }
/*      */ 
/*      */     private void addItem(File paramFile)
/*      */     {
/*  932 */       if (paramFile == null) {
/*  933 */         return;
/*      */       }
/*      */ 
/*  936 */       boolean bool = FilePane.usesShellFolder(this.chooser);
/*      */ 
/*  938 */       this.directories.clear();
/*      */       File[] arrayOfFile;
/*  941 */       if (bool)
/*  942 */         arrayOfFile = (File[])AccessController.doPrivileged(new PrivilegedAction() {
/*      */           public File[] run() {
/*  944 */             return (File[])ShellFolder.get("fileChooserComboBoxFolders");
/*      */           }
/*      */         });
/*      */       else {
/*  948 */         arrayOfFile = this.fsv.getRoots();
/*      */       }
/*      */ 
/*  950 */       this.directories.addAll(Arrays.asList(arrayOfFile));
/*      */       File localFile1;
/*      */       try
/*      */       {
/*  957 */         localFile1 = ShellFolder.getNormalizedFile(paramFile);
/*      */       }
/*      */       catch (IOException localIOException) {
/*  960 */         localFile1 = paramFile;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  965 */         File localFile2 = bool ? ShellFolder.getShellFolder(localFile1) : localFile1;
/*      */ 
/*  967 */         File localFile3 = localFile2;
/*  968 */         Vector localVector = new Vector(10);
/*      */         do
/*  970 */           localVector.addElement(localFile3);
/*  971 */         while ((localFile3 = localFile3.getParentFile()) != null);
/*      */ 
/*  973 */         int i = localVector.size();
/*      */ 
/*  975 */         for (int j = 0; j < i; j++) {
/*  976 */           localFile3 = (File)localVector.get(j);
/*  977 */           if (this.directories.contains(localFile3)) {
/*  978 */             int k = this.directories.indexOf(localFile3);
/*  979 */             for (int m = j - 1; m >= 0; m--) {
/*  980 */               this.directories.insertElementAt(localVector.get(m), k + j - m);
/*      */             }
/*  982 */             break;
/*      */           }
/*      */         }
/*  985 */         calculateDepths();
/*  986 */         setSelectedItem(localFile2);
/*      */       } catch (FileNotFoundException localFileNotFoundException) {
/*  988 */         calculateDepths();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void calculateDepths() {
/*  993 */       this.depths = new int[this.directories.size()];
/*  994 */       for (int i = 0; i < this.depths.length; i++) {
/*  995 */         File localFile1 = (File)this.directories.get(i);
/*  996 */         File localFile2 = localFile1.getParentFile();
/*  997 */         this.depths[i] = 0;
/*  998 */         if (localFile2 != null)
/*  999 */           for (int j = i - 1; j >= 0; j--)
/* 1000 */             if (localFile2.equals(this.directories.get(j))) {
/* 1001 */               this.depths[j] += 1;
/* 1002 */               break;
/*      */             }
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getDepth(int paramInt)
/*      */     {
/* 1010 */       return (this.depths != null) && (paramInt >= 0) && (paramInt < this.depths.length) ? this.depths[paramInt] : 0;
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object paramObject) {
/* 1014 */       this.selectedDirectory = ((File)paramObject);
/* 1015 */       fireContentsChanged(this, -1, -1);
/*      */     }
/*      */ 
/*      */     public Object getSelectedItem() {
/* 1019 */       return this.selectedDirectory;
/*      */     }
/*      */ 
/*      */     public int getSize() {
/* 1023 */       return this.directories.size();
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt) {
/* 1027 */       return this.directories.elementAt(paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   class DirectoryComboBoxRenderer extends DefaultListCellRenderer
/*      */   {
/*  853 */     MetalFileChooserUI.IndentIcon ii = new MetalFileChooserUI.IndentIcon(MetalFileChooserUI.this);
/*      */ 
/*      */     DirectoryComboBoxRenderer() {
/*      */     }
/*      */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
/*  858 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*      */ 
/*  860 */       if (paramObject == null) {
/*  861 */         setText("");
/*  862 */         return this;
/*      */       }
/*  864 */       File localFile = (File)paramObject;
/*  865 */       setText(MetalFileChooserUI.this.getFileChooser().getName(localFile));
/*  866 */       Icon localIcon = MetalFileChooserUI.this.getFileChooser().getIcon(localFile);
/*  867 */       this.ii.icon = localIcon;
/*  868 */       this.ii.depth = MetalFileChooserUI.this.directoryComboBoxModel.getDepth(paramInt);
/*  869 */       setIcon(this.ii);
/*      */ 
/*  871 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class FileRenderer extends DefaultListCellRenderer
/*      */   {
/*      */     protected FileRenderer()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class FilterComboBoxModel extends AbstractListModel<Object>
/*      */     implements ComboBoxModel<Object>, PropertyChangeListener
/*      */   {
/*      */     protected FileFilter[] filters;
/*      */ 
/*      */     protected FilterComboBoxModel()
/*      */     {
/* 1070 */       this.filters = MetalFileChooserUI.this.getFileChooser().getChoosableFileFilters();
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 1074 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 1075 */       if (str == "ChoosableFileFilterChangedProperty") {
/* 1076 */         this.filters = ((FileFilter[])paramPropertyChangeEvent.getNewValue());
/* 1077 */         fireContentsChanged(this, -1, -1);
/* 1078 */       } else if (str == "fileFilterChanged") {
/* 1079 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object paramObject) {
/* 1084 */       if (paramObject != null) {
/* 1085 */         MetalFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)paramObject);
/* 1086 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object getSelectedItem()
/*      */     {
/* 1096 */       FileFilter localFileFilter1 = MetalFileChooserUI.this.getFileChooser().getFileFilter();
/* 1097 */       int i = 0;
/* 1098 */       if (localFileFilter1 != null) {
/* 1099 */         for (FileFilter localFileFilter2 : this.filters) {
/* 1100 */           if (localFileFilter2 == localFileFilter1) {
/* 1101 */             i = 1;
/*      */           }
/*      */         }
/* 1104 */         if (i == 0) {
/* 1105 */           MetalFileChooserUI.this.getFileChooser().addChoosableFileFilter(localFileFilter1);
/*      */         }
/*      */       }
/* 1108 */       return MetalFileChooserUI.this.getFileChooser().getFileFilter();
/*      */     }
/*      */ 
/*      */     public int getSize() {
/* 1112 */       if (this.filters != null) {
/* 1113 */         return this.filters.length;
/*      */       }
/* 1115 */       return 0;
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt)
/*      */     {
/* 1120 */       if (paramInt > getSize() - 1)
/*      */       {
/* 1122 */         return MetalFileChooserUI.this.getFileChooser().getFileFilter();
/*      */       }
/* 1124 */       if (this.filters != null) {
/* 1125 */         return this.filters[paramInt];
/*      */       }
/* 1127 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class FilterComboBoxRenderer extends DefaultListCellRenderer
/*      */   {
/*      */     public FilterComboBoxRenderer()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 1046 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*      */ 
/* 1048 */       if ((paramObject != null) && ((paramObject instanceof FileFilter))) {
/* 1049 */         setText(((FileFilter)paramObject).getDescription());
/*      */       }
/*      */ 
/* 1052 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   class IndentIcon
/*      */     implements Icon
/*      */   {
/*  878 */     Icon icon = null;
/*  879 */     int depth = 0;
/*      */ 
/*      */     IndentIcon() {  } 
/*  882 */     public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) { if (paramComponent.getComponentOrientation().isLeftToRight())
/*  883 */         this.icon.paintIcon(paramComponent, paramGraphics, paramInt1 + this.depth * 10, paramInt2);
/*      */       else
/*  885 */         this.icon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public int getIconWidth()
/*      */     {
/*  890 */       return this.icon.getIconWidth() + this.depth * 10;
/*      */     }
/*      */ 
/*      */     public int getIconHeight() {
/*  894 */       return this.icon.getIconHeight();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class MetalFileChooserUIAccessor
/*      */     implements FilePane.FileChooserUIAccessor
/*      */   {
/*      */     private MetalFileChooserUIAccessor()
/*      */     {
/*      */     }
/*      */ 
/*      */     public JFileChooser getFileChooser()
/*      */     {
/*  164 */       return MetalFileChooserUI.this.getFileChooser();
/*      */     }
/*      */ 
/*      */     public BasicDirectoryModel getModel() {
/*  168 */       return MetalFileChooserUI.this.getModel();
/*      */     }
/*      */ 
/*      */     public JPanel createList() {
/*  172 */       return MetalFileChooserUI.this.createList(getFileChooser());
/*      */     }
/*      */ 
/*      */     public JPanel createDetailsView() {
/*  176 */       return MetalFileChooserUI.this.createDetailsView(getFileChooser());
/*      */     }
/*      */ 
/*      */     public boolean isDirectorySelected() {
/*  180 */       return MetalFileChooserUI.this.isDirectorySelected();
/*      */     }
/*      */ 
/*      */     public File getDirectory() {
/*  184 */       return MetalFileChooserUI.this.getDirectory();
/*      */     }
/*      */ 
/*      */     public Action getChangeToParentDirectoryAction() {
/*  188 */       return MetalFileChooserUI.this.getChangeToParentDirectoryAction();
/*      */     }
/*      */ 
/*      */     public Action getApproveSelectionAction() {
/*  192 */       return MetalFileChooserUI.this.getApproveSelectionAction();
/*      */     }
/*      */ 
/*      */     public Action getNewFolderAction() {
/*  196 */       return MetalFileChooserUI.this.getNewFolderAction();
/*      */     }
/*      */ 
/*      */     public MouseListener createDoubleClickListener(JList paramJList) {
/*  200 */       return MetalFileChooserUI.this.createDoubleClickListener(getFileChooser(), paramJList);
/*      */     }
/*      */ 
/*      */     public ListSelectionListener createListSelectionListener()
/*      */     {
/*  205 */       return MetalFileChooserUI.this.createListSelectionListener(getFileChooser());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class SingleClickListener extends MouseAdapter
/*      */   {
/*      */     public SingleClickListener(JList arg2)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.metal.MetalFileChooserUI
 * JD-Core Version:    0.6.2
 */