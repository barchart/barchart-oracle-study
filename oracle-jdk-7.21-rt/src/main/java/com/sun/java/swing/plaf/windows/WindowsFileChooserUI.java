/*      */ package com.sun.java.swing.plaf.windows;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.FocusAdapter;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.KeyAdapter;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.image.BufferedImage;
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
/*      */ import javax.swing.AbstractListModel;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.ButtonModel;
/*      */ import javax.swing.ComboBoxModel;
/*      */ import javax.swing.DefaultButtonModel;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JRadioButtonMenuItem;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.event.PopupMenuEvent;
/*      */ import javax.swing.event.PopupMenuListener;
/*      */ import javax.swing.filechooser.FileFilter;
/*      */ import javax.swing.filechooser.FileSystemView;
/*      */ import javax.swing.filechooser.FileView;
/*      */ import javax.swing.plaf.ActionMapUIResource;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.InsetsUIResource;
/*      */ import javax.swing.plaf.basic.BasicDirectoryModel;
/*      */ import javax.swing.plaf.basic.BasicFileChooserUI;
/*      */ import javax.swing.plaf.basic.BasicFileChooserUI.BasicFileView;
/*      */ import javax.swing.plaf.basic.BasicFileChooserUI.NewFolderAction;
/*      */ import sun.awt.shell.ShellFolder;
/*      */ import sun.swing.FilePane;
/*      */ import sun.swing.FilePane.FileChooserUIAccessor;
/*      */ import sun.swing.WindowsPlacesBar;
/*      */ 
/*      */ public class WindowsFileChooserUI extends BasicFileChooserUI
/*      */ {
/*      */   private JPanel centerPanel;
/*      */   private JLabel lookInLabel;
/*      */   private JComboBox directoryComboBox;
/*      */   private DirectoryComboBoxModel directoryComboBoxModel;
/*   65 */   private ActionListener directoryComboBoxAction = new DirectoryComboBoxAction();
/*      */   private FilterComboBoxModel filterComboBoxModel;
/*      */   private JTextField filenameTextField;
/*      */   private FilePane filePane;
/*      */   private WindowsPlacesBar placesBar;
/*      */   private JButton approveButton;
/*      */   private JButton cancelButton;
/*      */   private JPanel buttonPanel;
/*      */   private JPanel bottomPanel;
/*      */   private JComboBox filterComboBox;
/*   81 */   private static final Dimension hstrut10 = new Dimension(10, 1);
/*      */ 
/*   83 */   private static final Dimension vstrut4 = new Dimension(1, 4);
/*   84 */   private static final Dimension vstrut6 = new Dimension(1, 6);
/*   85 */   private static final Dimension vstrut8 = new Dimension(1, 8);
/*      */ 
/*   87 */   private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
/*      */ 
/*   90 */   private static int PREF_WIDTH = 425;
/*   91 */   private static int PREF_HEIGHT = 245;
/*   92 */   private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
/*      */ 
/*   94 */   private static int MIN_WIDTH = 425;
/*   95 */   private static int MIN_HEIGHT = 245;
/*   96 */   private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);
/*      */ 
/*   98 */   private static int LIST_PREF_WIDTH = 444;
/*   99 */   private static int LIST_PREF_HEIGHT = 138;
/*  100 */   private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);
/*      */ 
/*  103 */   private int lookInLabelMnemonic = 0;
/*  104 */   private String lookInLabelText = null;
/*  105 */   private String saveInLabelText = null;
/*      */ 
/*  107 */   private int fileNameLabelMnemonic = 0;
/*  108 */   private String fileNameLabelText = null;
/*  109 */   private int folderNameLabelMnemonic = 0;
/*  110 */   private String folderNameLabelText = null;
/*      */ 
/*  112 */   private int filesOfTypeLabelMnemonic = 0;
/*  113 */   private String filesOfTypeLabelText = null;
/*      */ 
/*  115 */   private String upFolderToolTipText = null;
/*  116 */   private String upFolderAccessibleName = null;
/*      */ 
/*  118 */   private String newFolderToolTipText = null;
/*  119 */   private String newFolderAccessibleName = null;
/*      */ 
/*  121 */   private String viewMenuButtonToolTipText = null;
/*  122 */   private String viewMenuButtonAccessibleName = null;
/*      */ 
/*  124 */   private BasicFileChooserUI.BasicFileView fileView = new WindowsFileView();
/*      */   private JLabel fileNameLabel;
/*      */   static final int space = 10;
/*      */ 
/*      */   private void populateFileNameLabel()
/*      */   {
/*  129 */     if (getFileChooser().getFileSelectionMode() == 1) {
/*  130 */       this.fileNameLabel.setText(this.folderNameLabelText);
/*  131 */       this.fileNameLabel.setDisplayedMnemonic(this.folderNameLabelMnemonic);
/*      */     } else {
/*  133 */       this.fileNameLabel.setText(this.fileNameLabelText);
/*  134 */       this.fileNameLabel.setDisplayedMnemonic(this.fileNameLabelMnemonic);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static ComponentUI createUI(JComponent paramJComponent)
/*      */   {
/*  142 */     return new WindowsFileChooserUI((JFileChooser)paramJComponent);
/*      */   }
/*      */ 
/*      */   public WindowsFileChooserUI(JFileChooser paramJFileChooser) {
/*  146 */     super(paramJFileChooser);
/*      */   }
/*      */ 
/*      */   public void installUI(JComponent paramJComponent) {
/*  150 */     super.installUI(paramJComponent);
/*      */   }
/*      */ 
/*      */   public void uninstallComponents(JFileChooser paramJFileChooser) {
/*  154 */     paramJFileChooser.removeAll();
/*      */   }
/*      */ 
/*      */   public void installComponents(JFileChooser paramJFileChooser)
/*      */   {
/*  205 */     this.filePane = new FilePane(new WindowsFileChooserUIAccessor(null));
/*  206 */     paramJFileChooser.addPropertyChangeListener(this.filePane);
/*      */ 
/*  208 */     FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
/*      */ 
/*  210 */     paramJFileChooser.setBorder(new EmptyBorder(4, 10, 10, 10));
/*  211 */     paramJFileChooser.setLayout(new BorderLayout(8, 8));
/*      */ 
/*  213 */     updateUseShellFolder();
/*      */ 
/*  220 */     JToolBar localJToolBar = new JToolBar();
/*  221 */     localJToolBar.setFloatable(false);
/*  222 */     localJToolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
/*      */ 
/*  225 */     paramJFileChooser.add(localJToolBar, "North");
/*      */ 
/*  228 */     this.lookInLabel = new JLabel(this.lookInLabelText, 11) {
/*      */       public Dimension getPreferredSize() {
/*  230 */         return getMinimumSize();
/*      */       }
/*      */ 
/*      */       public Dimension getMinimumSize() {
/*  234 */         Dimension localDimension = super.getPreferredSize();
/*  235 */         if (WindowsFileChooserUI.this.placesBar != null) {
/*  236 */           localDimension.width = Math.max(localDimension.width, WindowsFileChooserUI.this.placesBar.getWidth());
/*      */         }
/*  238 */         return localDimension;
/*      */       }
/*      */     };
/*  241 */     this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
/*  242 */     this.lookInLabel.setAlignmentX(0.0F);
/*  243 */     this.lookInLabel.setAlignmentY(0.5F);
/*  244 */     localJToolBar.add(this.lookInLabel);
/*  245 */     localJToolBar.add(Box.createRigidArea(new Dimension(8, 0)));
/*      */ 
/*  248 */     this.directoryComboBox = new JComboBox() {
/*      */       public Dimension getMinimumSize() {
/*  250 */         Dimension localDimension = super.getMinimumSize();
/*  251 */         localDimension.width = 60;
/*  252 */         return localDimension;
/*      */       }
/*      */ 
/*      */       public Dimension getPreferredSize() {
/*  256 */         Dimension localDimension = super.getPreferredSize();
/*      */ 
/*  258 */         localDimension.width = 150;
/*  259 */         return localDimension;
/*      */       }
/*      */     };
/*  262 */     this.directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
/*  263 */     this.lookInLabel.setLabelFor(this.directoryComboBox);
/*  264 */     this.directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
/*  265 */     this.directoryComboBox.setModel(this.directoryComboBoxModel);
/*  266 */     this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
/*  267 */     this.directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(paramJFileChooser));
/*  268 */     this.directoryComboBox.setAlignmentX(0.0F);
/*  269 */     this.directoryComboBox.setAlignmentY(0.5F);
/*  270 */     this.directoryComboBox.setMaximumRowCount(8);
/*      */ 
/*  272 */     localJToolBar.add(this.directoryComboBox);
/*  273 */     localJToolBar.add(Box.createRigidArea(hstrut10));
/*      */ 
/*  276 */     JButton localJButton1 = createToolButton(getChangeToParentDirectoryAction(), this.upFolderIcon, this.upFolderToolTipText, this.upFolderAccessibleName);
/*      */ 
/*  278 */     localJToolBar.add(localJButton1);
/*      */ 
/*  281 */     if (!UIManager.getBoolean("FileChooser.readOnly")) {
/*  282 */       localObject1 = createToolButton(this.filePane.getNewFolderAction(), this.newFolderIcon, this.newFolderToolTipText, this.newFolderAccessibleName);
/*      */ 
/*  284 */       localJToolBar.add((Component)localObject1);
/*      */     }
/*      */ 
/*  288 */     Object localObject1 = new ButtonGroup();
/*      */ 
/*  291 */     final JPopupMenu localJPopupMenu = new JPopupMenu();
/*      */ 
/*  293 */     final JRadioButtonMenuItem localJRadioButtonMenuItem1 = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(0));
/*      */ 
/*  295 */     localJRadioButtonMenuItem1.setSelected(this.filePane.getViewType() == 0);
/*  296 */     localJPopupMenu.add(localJRadioButtonMenuItem1);
/*  297 */     ((ButtonGroup)localObject1).add(localJRadioButtonMenuItem1);
/*      */ 
/*  299 */     final JRadioButtonMenuItem localJRadioButtonMenuItem2 = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(1));
/*      */ 
/*  301 */     localJRadioButtonMenuItem2.setSelected(this.filePane.getViewType() == 1);
/*  302 */     localJPopupMenu.add(localJRadioButtonMenuItem2);
/*  303 */     ((ButtonGroup)localObject1).add(localJRadioButtonMenuItem2);
/*      */ 
/*  306 */     BufferedImage localBufferedImage = new BufferedImage(this.viewMenuIcon.getIconWidth() + 7, this.viewMenuIcon.getIconHeight(), 2);
/*      */ 
/*  308 */     Graphics localGraphics = localBufferedImage.getGraphics();
/*  309 */     this.viewMenuIcon.paintIcon(this.filePane, localGraphics, 0, 0);
/*  310 */     int i = localBufferedImage.getWidth() - 5;
/*  311 */     int j = localBufferedImage.getHeight() / 2 - 1;
/*  312 */     localGraphics.setColor(Color.BLACK);
/*  313 */     localGraphics.fillPolygon(new int[] { i, i + 5, i + 2 }, new int[] { j, j, j + 3 }, 3);
/*      */ 
/*  316 */     final JButton localJButton2 = createToolButton(null, new ImageIcon(localBufferedImage), this.viewMenuButtonToolTipText, this.viewMenuButtonAccessibleName);
/*      */ 
/*  319 */     localJButton2.addMouseListener(new MouseAdapter() {
/*      */       public void mousePressed(MouseEvent paramAnonymousMouseEvent) {
/*  321 */         if ((SwingUtilities.isLeftMouseButton(paramAnonymousMouseEvent)) && (!localJButton2.isSelected())) {
/*  322 */           localJButton2.setSelected(true);
/*      */ 
/*  324 */           localJPopupMenu.show(localJButton2, 0, localJButton2.getHeight());
/*      */         }
/*      */       }
/*      */     });
/*  328 */     localJButton2.addKeyListener(new KeyAdapter()
/*      */     {
/*      */       public void keyPressed(KeyEvent paramAnonymousKeyEvent) {
/*  331 */         if ((paramAnonymousKeyEvent.getKeyCode() == 32) && (localJButton2.getModel().isRollover())) {
/*  332 */           localJButton2.setSelected(true);
/*      */ 
/*  334 */           localJPopupMenu.show(localJButton2, 0, localJButton2.getHeight());
/*      */         }
/*      */       }
/*      */     });
/*  338 */     localJPopupMenu.addPopupMenuListener(new PopupMenuListener() {
/*      */       public void popupMenuWillBecomeVisible(PopupMenuEvent paramAnonymousPopupMenuEvent) {
/*      */       }
/*      */ 
/*      */       public void popupMenuWillBecomeInvisible(PopupMenuEvent paramAnonymousPopupMenuEvent) {
/*  343 */         SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/*  345 */             WindowsFileChooserUI.5.this.val$viewMenuButton.setSelected(false);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void popupMenuCanceled(PopupMenuEvent paramAnonymousPopupMenuEvent)
/*      */       {
/*      */       }
/*      */     });
/*  354 */     localJToolBar.add(localJButton2);
/*      */ 
/*  356 */     localJToolBar.add(Box.createRigidArea(new Dimension(80, 0)));
/*      */ 
/*  358 */     this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/*  360 */         if ("viewType".equals(paramAnonymousPropertyChangeEvent.getPropertyName()))
/*  361 */           switch (WindowsFileChooserUI.this.filePane.getViewType()) {
/*      */           case 0:
/*  363 */             localJRadioButtonMenuItem1.setSelected(true);
/*  364 */             break;
/*      */           case 1:
/*  367 */             localJRadioButtonMenuItem2.setSelected(true);
/*      */           }
/*      */       }
/*      */     });
/*  377 */     this.centerPanel = new JPanel(new BorderLayout());
/*  378 */     this.centerPanel.add(getAccessoryPanel(), "After");
/*  379 */     JComponent localJComponent = paramJFileChooser.getAccessory();
/*  380 */     if (localJComponent != null) {
/*  381 */       getAccessoryPanel().add(localJComponent);
/*      */     }
/*  383 */     this.filePane.setPreferredSize(LIST_PREF_SIZE);
/*  384 */     this.centerPanel.add(this.filePane, "Center");
/*  385 */     paramJFileChooser.add(this.centerPanel, "Center");
/*      */ 
/*  390 */     getBottomPanel().setLayout(new BoxLayout(getBottomPanel(), 2));
/*      */ 
/*  393 */     this.centerPanel.add(getBottomPanel(), "South");
/*      */ 
/*  396 */     JPanel localJPanel1 = new JPanel();
/*  397 */     localJPanel1.setLayout(new BoxLayout(localJPanel1, 3));
/*  398 */     localJPanel1.add(Box.createRigidArea(vstrut4));
/*      */ 
/*  400 */     this.fileNameLabel = new JLabel();
/*  401 */     populateFileNameLabel();
/*  402 */     this.fileNameLabel.setAlignmentY(0.0F);
/*  403 */     localJPanel1.add(this.fileNameLabel);
/*      */ 
/*  405 */     localJPanel1.add(Box.createRigidArea(new Dimension(1, 12)));
/*      */ 
/*  407 */     JLabel localJLabel = new JLabel(this.filesOfTypeLabelText);
/*  408 */     localJLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
/*  409 */     localJPanel1.add(localJLabel);
/*      */ 
/*  411 */     getBottomPanel().add(localJPanel1);
/*  412 */     getBottomPanel().add(Box.createRigidArea(new Dimension(15, 0)));
/*      */ 
/*  415 */     JPanel localJPanel2 = new JPanel();
/*  416 */     localJPanel2.add(Box.createRigidArea(vstrut8));
/*  417 */     localJPanel2.setLayout(new BoxLayout(localJPanel2, 1));
/*      */ 
/*  420 */     this.filenameTextField = new JTextField(35) {
/*      */       public Dimension getMaximumSize() {
/*  422 */         return new Dimension(32767, super.getPreferredSize().height);
/*      */       }
/*      */     };
/*  426 */     this.fileNameLabel.setLabelFor(this.filenameTextField);
/*  427 */     this.filenameTextField.addFocusListener(new FocusAdapter()
/*      */     {
/*      */       public void focusGained(FocusEvent paramAnonymousFocusEvent) {
/*  430 */         if (!WindowsFileChooserUI.this.getFileChooser().isMultiSelectionEnabled())
/*  431 */           WindowsFileChooserUI.this.filePane.clearSelection();
/*      */       }
/*      */     });
/*  437 */     if (paramJFileChooser.isMultiSelectionEnabled())
/*  438 */       setFileName(fileNameString(paramJFileChooser.getSelectedFiles()));
/*      */     else {
/*  440 */       setFileName(fileNameString(paramJFileChooser.getSelectedFile()));
/*      */     }
/*      */ 
/*  443 */     localJPanel2.add(this.filenameTextField);
/*  444 */     localJPanel2.add(Box.createRigidArea(vstrut8));
/*      */ 
/*  446 */     this.filterComboBoxModel = createFilterComboBoxModel();
/*  447 */     paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
/*  448 */     this.filterComboBox = new JComboBox(this.filterComboBoxModel);
/*  449 */     localJLabel.setLabelFor(this.filterComboBox);
/*  450 */     this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
/*  451 */     localJPanel2.add(this.filterComboBox);
/*      */ 
/*  453 */     getBottomPanel().add(localJPanel2);
/*  454 */     getBottomPanel().add(Box.createRigidArea(new Dimension(30, 0)));
/*      */ 
/*  457 */     getButtonPanel().setLayout(new BoxLayout(getButtonPanel(), 1));
/*      */ 
/*  459 */     this.approveButton = new JButton(getApproveButtonText(paramJFileChooser)) {
/*      */       public Dimension getMaximumSize() {
/*  461 */         return WindowsFileChooserUI.this.approveButton.getPreferredSize().width > WindowsFileChooserUI.this.cancelButton.getPreferredSize().width ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize();
/*      */       }
/*      */     };
/*  465 */     Object localObject2 = this.approveButton.getMargin();
/*  466 */     localObject2 = new InsetsUIResource(((Insets)localObject2).top, ((Insets)localObject2).left + 5, ((Insets)localObject2).bottom, ((Insets)localObject2).right + 5);
/*      */ 
/*  468 */     this.approveButton.setMargin((Insets)localObject2);
/*  469 */     this.approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
/*  470 */     this.approveButton.addActionListener(getApproveSelectionAction());
/*  471 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
/*  472 */     getButtonPanel().add(Box.createRigidArea(vstrut6));
/*  473 */     getButtonPanel().add(this.approveButton);
/*  474 */     getButtonPanel().add(Box.createRigidArea(vstrut4));
/*      */ 
/*  476 */     this.cancelButton = new JButton(this.cancelButtonText) {
/*      */       public Dimension getMaximumSize() {
/*  478 */         return WindowsFileChooserUI.this.approveButton.getPreferredSize().width > WindowsFileChooserUI.this.cancelButton.getPreferredSize().width ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize();
/*      */       }
/*      */     };
/*  482 */     this.cancelButton.setMargin((Insets)localObject2);
/*  483 */     this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
/*  484 */     this.cancelButton.addActionListener(getCancelSelectionAction());
/*  485 */     getButtonPanel().add(this.cancelButton);
/*      */ 
/*  487 */     if (paramJFileChooser.getControlButtonsAreShown())
/*  488 */       addControlButtons();
/*      */   }
/*      */ 
/*      */   private void updateUseShellFolder()
/*      */   {
/*  495 */     JFileChooser localJFileChooser = getFileChooser();
/*      */ 
/*  497 */     if (FilePane.usesShellFolder(localJFileChooser)) {
/*  498 */       if ((this.placesBar == null) && (!UIManager.getBoolean("FileChooser.noPlacesBar"))) {
/*  499 */         this.placesBar = new WindowsPlacesBar(localJFileChooser, XPStyle.getXP() != null);
/*  500 */         localJFileChooser.add(this.placesBar, "Before");
/*  501 */         localJFileChooser.addPropertyChangeListener(this.placesBar);
/*      */       }
/*      */     }
/*  504 */     else if (this.placesBar != null) {
/*  505 */       localJFileChooser.remove(this.placesBar);
/*  506 */       localJFileChooser.removePropertyChangeListener(this.placesBar);
/*  507 */       this.placesBar = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JPanel getButtonPanel()
/*      */   {
/*  513 */     if (this.buttonPanel == null) {
/*  514 */       this.buttonPanel = new JPanel();
/*      */     }
/*  516 */     return this.buttonPanel;
/*      */   }
/*      */ 
/*      */   protected JPanel getBottomPanel() {
/*  520 */     if (this.bottomPanel == null) {
/*  521 */       this.bottomPanel = new JPanel();
/*      */     }
/*  523 */     return this.bottomPanel;
/*      */   }
/*      */ 
/*      */   protected void installStrings(JFileChooser paramJFileChooser) {
/*  527 */     super.installStrings(paramJFileChooser);
/*      */ 
/*  529 */     Locale localLocale = paramJFileChooser.getLocale();
/*      */ 
/*  531 */     this.lookInLabelMnemonic = UIManager.getInt("FileChooser.lookInLabelMnemonic");
/*  532 */     this.lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", localLocale);
/*  533 */     this.saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", localLocale);
/*      */ 
/*  535 */     this.fileNameLabelMnemonic = UIManager.getInt("FileChooser.fileNameLabelMnemonic");
/*  536 */     this.fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", localLocale);
/*  537 */     this.folderNameLabelMnemonic = UIManager.getInt("FileChooser.folderNameLabelMnemonic");
/*  538 */     this.folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", localLocale);
/*      */ 
/*  540 */     this.filesOfTypeLabelMnemonic = UIManager.getInt("FileChooser.filesOfTypeLabelMnemonic");
/*  541 */     this.filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", localLocale);
/*      */ 
/*  543 */     this.upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", localLocale);
/*  544 */     this.upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", localLocale);
/*      */ 
/*  546 */     this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", localLocale);
/*  547 */     this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", localLocale);
/*      */ 
/*  549 */     this.viewMenuButtonToolTipText = UIManager.getString("FileChooser.viewMenuButtonToolTipText", localLocale);
/*  550 */     this.viewMenuButtonAccessibleName = UIManager.getString("FileChooser.viewMenuButtonAccessibleName", localLocale);
/*      */   }
/*      */ 
/*      */   protected void installListeners(JFileChooser paramJFileChooser) {
/*  554 */     super.installListeners(paramJFileChooser);
/*  555 */     ActionMap localActionMap = getActionMap();
/*  556 */     SwingUtilities.replaceUIActionMap(paramJFileChooser, localActionMap);
/*      */   }
/*      */ 
/*      */   protected ActionMap getActionMap() {
/*  560 */     return createActionMap();
/*      */   }
/*      */ 
/*      */   protected ActionMap createActionMap() {
/*  564 */     ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
/*  565 */     FilePane.addActionsToMap(localActionMapUIResource, this.filePane.getActions());
/*  566 */     return localActionMapUIResource;
/*      */   }
/*      */ 
/*      */   protected JPanel createList(JFileChooser paramJFileChooser) {
/*  570 */     return this.filePane.createList();
/*      */   }
/*      */ 
/*      */   protected JPanel createDetailsView(JFileChooser paramJFileChooser) {
/*  574 */     return this.filePane.createDetailsView();
/*      */   }
/*      */ 
/*      */   public ListSelectionListener createListSelectionListener(JFileChooser paramJFileChooser)
/*      */   {
/*  584 */     return super.createListSelectionListener(paramJFileChooser);
/*      */   }
/*      */ 
/*      */   public void uninstallUI(JComponent paramJComponent)
/*      */   {
/*  601 */     paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
/*  602 */     paramJComponent.removePropertyChangeListener(this.filePane);
/*  603 */     if (this.placesBar != null) {
/*  604 */       paramJComponent.removePropertyChangeListener(this.placesBar);
/*      */     }
/*  606 */     this.cancelButton.removeActionListener(getCancelSelectionAction());
/*  607 */     this.approveButton.removeActionListener(getApproveSelectionAction());
/*  608 */     this.filenameTextField.removeActionListener(getApproveSelectionAction());
/*      */ 
/*  610 */     if (this.filePane != null) {
/*  611 */       this.filePane.uninstallUI();
/*  612 */       this.filePane = null;
/*      */     }
/*      */ 
/*  615 */     super.uninstallUI(paramJComponent);
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(JComponent paramJComponent)
/*      */   {
/*  631 */     int i = PREF_SIZE.width;
/*  632 */     Dimension localDimension = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
/*  633 */     if (localDimension != null) {
/*  634 */       return new Dimension(localDimension.width < i ? i : localDimension.width, localDimension.height < PREF_SIZE.height ? PREF_SIZE.height : localDimension.height);
/*      */     }
/*      */ 
/*  637 */     return new Dimension(i, PREF_SIZE.height);
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(JComponent paramJComponent)
/*      */   {
/*  649 */     return MIN_SIZE;
/*      */   }
/*      */ 
/*      */   public Dimension getMaximumSize(JComponent paramJComponent)
/*      */   {
/*  660 */     return new Dimension(2147483647, 2147483647);
/*      */   }
/*      */ 
/*      */   private String fileNameString(File paramFile) {
/*  664 */     if (paramFile == null) {
/*  665 */       return null;
/*      */     }
/*  667 */     JFileChooser localJFileChooser = getFileChooser();
/*  668 */     if (((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) || ((localJFileChooser.isDirectorySelectionEnabled()) && (localJFileChooser.isFileSelectionEnabled()) && (localJFileChooser.getFileSystemView().isFileSystemRoot(paramFile))))
/*      */     {
/*  670 */       return paramFile.getPath();
/*      */     }
/*  672 */     return paramFile.getName();
/*      */   }
/*      */ 
/*      */   private String fileNameString(File[] paramArrayOfFile)
/*      */   {
/*  678 */     StringBuffer localStringBuffer = new StringBuffer();
/*  679 */     for (int i = 0; (paramArrayOfFile != null) && (i < paramArrayOfFile.length); i++) {
/*  680 */       if (i > 0) {
/*  681 */         localStringBuffer.append(" ");
/*      */       }
/*  683 */       if (paramArrayOfFile.length > 1) {
/*  684 */         localStringBuffer.append("\"");
/*      */       }
/*  686 */       localStringBuffer.append(fileNameString(paramArrayOfFile[i]));
/*  687 */       if (paramArrayOfFile.length > 1) {
/*  688 */         localStringBuffer.append("\"");
/*      */       }
/*      */     }
/*  691 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  697 */     File localFile = (File)paramPropertyChangeEvent.getNewValue();
/*  698 */     JFileChooser localJFileChooser = getFileChooser();
/*  699 */     if ((localFile != null) && (((localJFileChooser.isFileSelectionEnabled()) && (!localFile.isDirectory())) || ((localFile.isDirectory()) && (localJFileChooser.isDirectorySelectionEnabled()))))
/*      */     {
/*  703 */       setFileName(fileNameString(localFile));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  708 */     File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
/*  709 */     JFileChooser localJFileChooser = getFileChooser();
/*  710 */     if ((arrayOfFile != null) && (arrayOfFile.length > 0) && ((arrayOfFile.length > 1) || (localJFileChooser.isDirectorySelectionEnabled()) || (!arrayOfFile[0].isDirectory())))
/*      */     {
/*  713 */       setFileName(fileNameString(arrayOfFile));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  718 */     JFileChooser localJFileChooser = getFileChooser();
/*  719 */     FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
/*      */ 
/*  721 */     clearIconCache();
/*  722 */     File localFile = localJFileChooser.getCurrentDirectory();
/*  723 */     if (localFile != null) {
/*  724 */       this.directoryComboBoxModel.addItem(localFile);
/*      */ 
/*  726 */       if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled()))
/*  727 */         if (localFileSystemView.isFileSystem(localFile))
/*  728 */           setFileName(localFile.getPath());
/*      */         else
/*  730 */           setFileName(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doFilterChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  737 */     clearIconCache();
/*      */   }
/*      */ 
/*      */   private void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  741 */     if (this.fileNameLabel != null) {
/*  742 */       populateFileNameLabel();
/*      */     }
/*  744 */     clearIconCache();
/*      */ 
/*  746 */     JFileChooser localJFileChooser = getFileChooser();
/*  747 */     File localFile = localJFileChooser.getCurrentDirectory();
/*  748 */     if ((localFile != null) && (localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled()) && (localJFileChooser.getFileSystemView().isFileSystem(localFile)))
/*      */     {
/*  753 */       setFileName(localFile.getPath());
/*      */     }
/*  755 */     else setFileName(null);
/*      */   }
/*      */ 
/*      */   private void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  760 */     if (getAccessoryPanel() != null) {
/*  761 */       if (paramPropertyChangeEvent.getOldValue() != null) {
/*  762 */         getAccessoryPanel().remove((JComponent)paramPropertyChangeEvent.getOldValue());
/*      */       }
/*  764 */       JComponent localJComponent = (JComponent)paramPropertyChangeEvent.getNewValue();
/*  765 */       if (localJComponent != null)
/*  766 */         getAccessoryPanel().add(localJComponent, "Center");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doApproveButtonTextChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  772 */     JFileChooser localJFileChooser = getFileChooser();
/*  773 */     this.approveButton.setText(getApproveButtonText(localJFileChooser));
/*  774 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
/*  775 */     this.approveButton.setMnemonic(getApproveButtonMnemonic(localJFileChooser));
/*      */   }
/*      */ 
/*      */   private void doDialogTypeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  779 */     JFileChooser localJFileChooser = getFileChooser();
/*  780 */     this.approveButton.setText(getApproveButtonText(localJFileChooser));
/*  781 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
/*  782 */     this.approveButton.setMnemonic(getApproveButtonMnemonic(localJFileChooser));
/*  783 */     if (localJFileChooser.getDialogType() == 1)
/*  784 */       this.lookInLabel.setText(this.saveInLabelText);
/*      */     else
/*  786 */       this.lookInLabel.setText(this.lookInLabelText);
/*      */   }
/*      */ 
/*      */   private void doApproveButtonMnemonicChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  791 */     this.approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
/*      */   }
/*      */ 
/*      */   private void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  795 */     if (getFileChooser().getControlButtonsAreShown())
/*  796 */       addControlButtons();
/*      */     else
/*  798 */       removeControlButtons();
/*      */   }
/*      */ 
/*      */   public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser)
/*      */   {
/*  807 */     return new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/*  809 */         String str = paramAnonymousPropertyChangeEvent.getPropertyName();
/*  810 */         if (str.equals("SelectedFileChangedProperty")) {
/*  811 */           WindowsFileChooserUI.this.doSelectedFileChanged(paramAnonymousPropertyChangeEvent);
/*  812 */         } else if (str.equals("SelectedFilesChangedProperty")) {
/*  813 */           WindowsFileChooserUI.this.doSelectedFilesChanged(paramAnonymousPropertyChangeEvent);
/*  814 */         } else if (str.equals("directoryChanged")) {
/*  815 */           WindowsFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
/*  816 */         } else if (str.equals("fileFilterChanged")) {
/*  817 */           WindowsFileChooserUI.this.doFilterChanged(paramAnonymousPropertyChangeEvent);
/*  818 */         } else if (str.equals("fileSelectionChanged")) {
/*  819 */           WindowsFileChooserUI.this.doFileSelectionModeChanged(paramAnonymousPropertyChangeEvent);
/*  820 */         } else if (str.equals("AccessoryChangedProperty")) {
/*  821 */           WindowsFileChooserUI.this.doAccessoryChanged(paramAnonymousPropertyChangeEvent);
/*  822 */         } else if ((str.equals("ApproveButtonTextChangedProperty")) || (str.equals("ApproveButtonToolTipTextChangedProperty")))
/*      */         {
/*  824 */           WindowsFileChooserUI.this.doApproveButtonTextChanged(paramAnonymousPropertyChangeEvent);
/*  825 */         } else if (str.equals("DialogTypeChangedProperty")) {
/*  826 */           WindowsFileChooserUI.this.doDialogTypeChanged(paramAnonymousPropertyChangeEvent);
/*  827 */         } else if (str.equals("ApproveButtonMnemonicChangedProperty")) {
/*  828 */           WindowsFileChooserUI.this.doApproveButtonMnemonicChanged(paramAnonymousPropertyChangeEvent);
/*  829 */         } else if (str.equals("ControlButtonsAreShownChangedProperty")) {
/*  830 */           WindowsFileChooserUI.this.doControlButtonsChanged(paramAnonymousPropertyChangeEvent);
/*  831 */         } else if (str == "FileChooser.useShellFolder") {
/*  832 */           WindowsFileChooserUI.this.updateUseShellFolder();
/*  833 */           WindowsFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
/*  834 */         } else if (str.equals("componentOrientation")) {
/*  835 */           ComponentOrientation localComponentOrientation = (ComponentOrientation)paramAnonymousPropertyChangeEvent.getNewValue();
/*  836 */           JFileChooser localJFileChooser = (JFileChooser)paramAnonymousPropertyChangeEvent.getSource();
/*  837 */           if (localComponentOrientation != paramAnonymousPropertyChangeEvent.getOldValue())
/*  838 */             localJFileChooser.applyComponentOrientation(localComponentOrientation);
/*      */         }
/*  840 */         else if ((str.equals("ancestor")) && 
/*  841 */           (paramAnonymousPropertyChangeEvent.getOldValue() == null) && (paramAnonymousPropertyChangeEvent.getNewValue() != null))
/*      */         {
/*  843 */           WindowsFileChooserUI.this.filenameTextField.selectAll();
/*  844 */           WindowsFileChooserUI.this.filenameTextField.requestFocus();
/*      */         }
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   protected void removeControlButtons()
/*      */   {
/*  853 */     getBottomPanel().remove(getButtonPanel());
/*      */   }
/*      */ 
/*      */   protected void addControlButtons() {
/*  857 */     getBottomPanel().add(getButtonPanel());
/*      */   }
/*      */ 
/*      */   public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {
/*  861 */     this.filePane.ensureFileIsVisible(paramJFileChooser, paramFile);
/*      */   }
/*      */ 
/*      */   public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {
/*  865 */     this.filePane.rescanCurrentDirectory();
/*      */   }
/*      */ 
/*      */   public String getFileName() {
/*  869 */     if (this.filenameTextField != null) {
/*  870 */       return this.filenameTextField.getText();
/*      */     }
/*  872 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFileName(String paramString)
/*      */   {
/*  877 */     if (this.filenameTextField != null)
/*  878 */       this.filenameTextField.setText(paramString);
/*      */   }
/*      */ 
/*      */   protected void setDirectorySelected(boolean paramBoolean)
/*      */   {
/*  890 */     super.setDirectorySelected(paramBoolean);
/*  891 */     JFileChooser localJFileChooser = getFileChooser();
/*  892 */     if (paramBoolean) {
/*  893 */       this.approveButton.setText(this.directoryOpenButtonText);
/*  894 */       this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
/*  895 */       this.approveButton.setMnemonic(this.directoryOpenButtonMnemonic);
/*      */     } else {
/*  897 */       this.approveButton.setText(getApproveButtonText(localJFileChooser));
/*  898 */       this.approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
/*  899 */       this.approveButton.setMnemonic(getApproveButtonMnemonic(localJFileChooser));
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getDirectoryName()
/*      */   {
/*  905 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDirectoryName(String paramString)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser) {
/*  913 */     return new DirectoryComboBoxRenderer();
/*      */   }
/*      */ 
/*      */   private static JButton createToolButton(Action paramAction, Icon paramIcon, String paramString1, String paramString2) {
/*  917 */     JButton localJButton = new JButton(paramAction);
/*      */ 
/*  919 */     localJButton.setText(null);
/*  920 */     localJButton.setIcon(paramIcon);
/*  921 */     localJButton.setToolTipText(paramString1);
/*  922 */     localJButton.setRequestFocusEnabled(false);
/*  923 */     localJButton.putClientProperty("AccessibleName", paramString2);
/*  924 */     localJButton.putClientProperty(WindowsLookAndFeel.HI_RES_DISABLED_ICON_CLIENT_KEY, Boolean.TRUE);
/*  925 */     localJButton.setAlignmentX(0.0F);
/*  926 */     localJButton.setAlignmentY(0.5F);
/*  927 */     localJButton.setMargin(shrinkwrap);
/*  928 */     localJButton.setFocusPainted(false);
/*      */ 
/*  930 */     localJButton.setModel(new DefaultButtonModel()
/*      */     {
/*      */       public void setPressed(boolean paramAnonymousBoolean) {
/*  933 */         if ((!paramAnonymousBoolean) || (isRollover()))
/*  934 */           super.setPressed(paramAnonymousBoolean);
/*      */       }
/*      */ 
/*      */       public void setRollover(boolean paramAnonymousBoolean)
/*      */       {
/*  939 */         if ((paramAnonymousBoolean) && (!isRollover()))
/*      */         {
/*  941 */           for (Component localComponent : this.val$result.getParent().getComponents()) {
/*  942 */             if (((localComponent instanceof JButton)) && (localComponent != this.val$result)) {
/*  943 */               ((JButton)localComponent).getModel().setRollover(false);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  948 */         super.setRollover(paramAnonymousBoolean);
/*      */       }
/*      */ 
/*      */       public void setSelected(boolean paramAnonymousBoolean) {
/*  952 */         super.setSelected(paramAnonymousBoolean);
/*      */ 
/*  954 */         if (paramAnonymousBoolean)
/*  955 */           this.stateMask |= 5;
/*      */         else
/*  957 */           this.stateMask &= -6;
/*      */       }
/*      */     });
/*  962 */     localJButton.addFocusListener(new FocusAdapter() {
/*      */       public void focusGained(FocusEvent paramAnonymousFocusEvent) {
/*  964 */         this.val$result.getModel().setRollover(true);
/*      */       }
/*      */ 
/*      */       public void focusLost(FocusEvent paramAnonymousFocusEvent) {
/*  968 */         this.val$result.getModel().setRollover(false);
/*      */       }
/*      */     });
/*  972 */     return localJButton;
/*      */   }
/*      */ 
/*      */   protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser)
/*      */   {
/* 1029 */     return new DirectoryComboBoxModel();
/*      */   }
/*      */ 
/*      */   protected FilterComboBoxRenderer createFilterComboBoxRenderer()
/*      */   {
/* 1161 */     return new FilterComboBoxRenderer();
/*      */   }
/*      */ 
/*      */   protected FilterComboBoxModel createFilterComboBoxModel()
/*      */   {
/* 1186 */     return new FilterComboBoxModel();
/*      */   }
/*      */ 
/*      */   public void valueChanged(ListSelectionEvent paramListSelectionEvent)
/*      */   {
/* 1259 */     JFileChooser localJFileChooser = getFileChooser();
/* 1260 */     File localFile = localJFileChooser.getSelectedFile();
/* 1261 */     if ((!paramListSelectionEvent.getValueIsAdjusting()) && (localFile != null) && (!getFileChooser().isTraversable(localFile)))
/* 1262 */       setFileName(fileNameString(localFile));
/*      */   }
/*      */ 
/*      */   protected JButton getApproveButton(JFileChooser paramJFileChooser)
/*      */   {
/* 1281 */     return this.approveButton;
/*      */   }
/*      */ 
/*      */   public FileView getFileView(JFileChooser paramJFileChooser) {
/* 1285 */     return this.fileView;
/*      */   }
/*      */ 
/*      */   protected class DirectoryComboBoxAction
/*      */     implements ActionListener
/*      */   {
/*      */     protected DirectoryComboBoxAction()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 1275 */       File localFile = (File)WindowsFileChooserUI.this.directoryComboBox.getSelectedItem();
/* 1276 */       WindowsFileChooserUI.this.getFileChooser().setCurrentDirectory(localFile);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class DirectoryComboBoxModel extends AbstractListModel
/*      */     implements ComboBoxModel
/*      */   {
/* 1036 */     Vector<File> directories = new Vector();
/* 1037 */     int[] depths = null;
/* 1038 */     File selectedDirectory = null;
/* 1039 */     JFileChooser chooser = WindowsFileChooserUI.this.getFileChooser();
/* 1040 */     FileSystemView fsv = this.chooser.getFileSystemView();
/*      */ 
/*      */     public DirectoryComboBoxModel()
/*      */     {
/* 1045 */       File localFile = WindowsFileChooserUI.this.getFileChooser().getCurrentDirectory();
/* 1046 */       if (localFile != null)
/* 1047 */         addItem(localFile);
/*      */     }
/*      */ 
/*      */     private void addItem(File paramFile)
/*      */     {
/* 1058 */       if (paramFile == null) {
/* 1059 */         return;
/*      */       }
/*      */ 
/* 1062 */       boolean bool = FilePane.usesShellFolder(this.chooser);
/*      */ 
/* 1064 */       this.directories.clear();
/*      */       File[] arrayOfFile;
/* 1067 */       if (bool)
/* 1068 */         arrayOfFile = (File[])AccessController.doPrivileged(new PrivilegedAction() {
/*      */           public File[] run() {
/* 1070 */             return (File[])ShellFolder.get("fileChooserComboBoxFolders");
/*      */           }
/*      */         });
/*      */       else {
/* 1074 */         arrayOfFile = this.fsv.getRoots();
/*      */       }
/*      */ 
/* 1076 */       this.directories.addAll(Arrays.asList(arrayOfFile));
/*      */       File localFile1;
/*      */       try
/*      */       {
/* 1083 */         localFile1 = paramFile.getCanonicalFile();
/*      */       }
/*      */       catch (IOException localIOException) {
/* 1086 */         localFile1 = paramFile;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1091 */         File localFile2 = bool ? ShellFolder.getShellFolder(localFile1) : localFile1;
/*      */ 
/* 1093 */         File localFile3 = localFile2;
/* 1094 */         Vector localVector = new Vector(10);
/*      */         do
/* 1096 */           localVector.addElement(localFile3);
/* 1097 */         while ((localFile3 = localFile3.getParentFile()) != null);
/*      */ 
/* 1099 */         int i = localVector.size();
/*      */ 
/* 1101 */         for (int j = 0; j < i; j++) {
/* 1102 */           localFile3 = (File)localVector.get(j);
/* 1103 */           if (this.directories.contains(localFile3)) {
/* 1104 */             int k = this.directories.indexOf(localFile3);
/* 1105 */             for (int m = j - 1; m >= 0; m--) {
/* 1106 */               this.directories.insertElementAt(localVector.get(m), k + j - m);
/*      */             }
/* 1108 */             break;
/*      */           }
/*      */         }
/* 1111 */         calculateDepths();
/* 1112 */         setSelectedItem(localFile2);
/*      */       } catch (FileNotFoundException localFileNotFoundException) {
/* 1114 */         calculateDepths();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void calculateDepths() {
/* 1119 */       this.depths = new int[this.directories.size()];
/* 1120 */       for (int i = 0; i < this.depths.length; i++) {
/* 1121 */         File localFile1 = (File)this.directories.get(i);
/* 1122 */         File localFile2 = localFile1.getParentFile();
/* 1123 */         this.depths[i] = 0;
/* 1124 */         if (localFile2 != null)
/* 1125 */           for (int j = i - 1; j >= 0; j--)
/* 1126 */             if (localFile2.equals(this.directories.get(j))) {
/* 1127 */               this.depths[j] += 1;
/* 1128 */               break;
/*      */             }
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getDepth(int paramInt)
/*      */     {
/* 1136 */       return (this.depths != null) && (paramInt >= 0) && (paramInt < this.depths.length) ? this.depths[paramInt] : 0;
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object paramObject) {
/* 1140 */       this.selectedDirectory = ((File)paramObject);
/* 1141 */       fireContentsChanged(this, -1, -1);
/*      */     }
/*      */ 
/*      */     public Object getSelectedItem() {
/* 1145 */       return this.selectedDirectory;
/*      */     }
/*      */ 
/*      */     public int getSize() {
/* 1149 */       return this.directories.size();
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt) {
/* 1153 */       return this.directories.elementAt(paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   class DirectoryComboBoxRenderer extends DefaultListCellRenderer
/*      */   {
/*  979 */     WindowsFileChooserUI.IndentIcon ii = new WindowsFileChooserUI.IndentIcon(WindowsFileChooserUI.this);
/*      */ 
/*      */     DirectoryComboBoxRenderer() {
/*      */     }
/*      */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
/*  984 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*      */ 
/*  986 */       if (paramObject == null) {
/*  987 */         setText("");
/*  988 */         return this;
/*      */       }
/*  990 */       File localFile = (File)paramObject;
/*  991 */       setText(WindowsFileChooserUI.this.getFileChooser().getName(localFile));
/*  992 */       Icon localIcon = WindowsFileChooserUI.this.getFileChooser().getIcon(localFile);
/*  993 */       this.ii.icon = localIcon;
/*  994 */       this.ii.depth = WindowsFileChooserUI.this.directoryComboBoxModel.getDepth(paramInt);
/*  995 */       setIcon(this.ii);
/*      */ 
/*  997 */       return this;
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
/*      */   protected class FilterComboBoxModel extends AbstractListModel
/*      */     implements ComboBoxModel, PropertyChangeListener
/*      */   {
/*      */     protected FileFilter[] filters;
/*      */ 
/*      */     protected FilterComboBoxModel()
/*      */     {
/* 1196 */       this.filters = WindowsFileChooserUI.this.getFileChooser().getChoosableFileFilters();
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 1200 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 1201 */       if (str == "ChoosableFileFilterChangedProperty") {
/* 1202 */         this.filters = ((FileFilter[])paramPropertyChangeEvent.getNewValue());
/* 1203 */         fireContentsChanged(this, -1, -1);
/* 1204 */       } else if (str == "fileFilterChanged") {
/* 1205 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object paramObject) {
/* 1210 */       if (paramObject != null) {
/* 1211 */         WindowsFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)paramObject);
/* 1212 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object getSelectedItem()
/*      */     {
/* 1222 */       FileFilter localFileFilter1 = WindowsFileChooserUI.this.getFileChooser().getFileFilter();
/* 1223 */       int i = 0;
/* 1224 */       if (localFileFilter1 != null) {
/* 1225 */         for (FileFilter localFileFilter2 : this.filters) {
/* 1226 */           if (localFileFilter2 == localFileFilter1) {
/* 1227 */             i = 1;
/*      */           }
/*      */         }
/* 1230 */         if (i == 0) {
/* 1231 */           WindowsFileChooserUI.this.getFileChooser().addChoosableFileFilter(localFileFilter1);
/*      */         }
/*      */       }
/* 1234 */       return WindowsFileChooserUI.this.getFileChooser().getFileFilter();
/*      */     }
/*      */ 
/*      */     public int getSize() {
/* 1238 */       if (this.filters != null) {
/* 1239 */         return this.filters.length;
/*      */       }
/* 1241 */       return 0;
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt)
/*      */     {
/* 1246 */       if (paramInt > getSize() - 1)
/*      */       {
/* 1248 */         return WindowsFileChooserUI.this.getFileChooser().getFileFilter();
/*      */       }
/* 1250 */       if (this.filters != null) {
/* 1251 */         return this.filters[paramInt];
/*      */       }
/* 1253 */       return null;
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
/* 1172 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*      */ 
/* 1174 */       if ((paramObject != null) && ((paramObject instanceof FileFilter))) {
/* 1175 */         setText(((FileFilter)paramObject).getDescription());
/*      */       }
/*      */ 
/* 1178 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   class IndentIcon
/*      */     implements Icon
/*      */   {
/* 1004 */     Icon icon = null;
/* 1005 */     int depth = 0;
/*      */ 
/*      */     IndentIcon() {  } 
/* 1008 */     public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) { if (paramComponent.getComponentOrientation().isLeftToRight())
/* 1009 */         this.icon.paintIcon(paramComponent, paramGraphics, paramInt1 + this.depth * 10, paramInt2);
/*      */       else
/* 1011 */         this.icon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public int getIconWidth()
/*      */     {
/* 1016 */       return this.icon.getIconWidth() + this.depth * 10;
/*      */     }
/*      */ 
/*      */     public int getIconHeight() {
/* 1020 */       return this.icon.getIconHeight();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class SingleClickListener extends MouseAdapter
/*      */   {
/*      */     protected SingleClickListener()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private class WindowsFileChooserUIAccessor
/*      */     implements FilePane.FileChooserUIAccessor
/*      */   {
/*      */     private WindowsFileChooserUIAccessor()
/*      */     {
/*      */     }
/*      */ 
/*      */     public JFileChooser getFileChooser()
/*      */     {
/*  159 */       return WindowsFileChooserUI.this.getFileChooser();
/*      */     }
/*      */ 
/*      */     public BasicDirectoryModel getModel() {
/*  163 */       return WindowsFileChooserUI.this.getModel();
/*      */     }
/*      */ 
/*      */     public JPanel createList() {
/*  167 */       return WindowsFileChooserUI.this.createList(getFileChooser());
/*      */     }
/*      */ 
/*      */     public JPanel createDetailsView() {
/*  171 */       return WindowsFileChooserUI.this.createDetailsView(getFileChooser());
/*      */     }
/*      */ 
/*      */     public boolean isDirectorySelected() {
/*  175 */       return WindowsFileChooserUI.this.isDirectorySelected();
/*      */     }
/*      */ 
/*      */     public File getDirectory() {
/*  179 */       return WindowsFileChooserUI.this.getDirectory();
/*      */     }
/*      */ 
/*      */     public Action getChangeToParentDirectoryAction() {
/*  183 */       return WindowsFileChooserUI.this.getChangeToParentDirectoryAction();
/*      */     }
/*      */ 
/*      */     public Action getApproveSelectionAction() {
/*  187 */       return WindowsFileChooserUI.this.getApproveSelectionAction();
/*      */     }
/*      */ 
/*      */     public Action getNewFolderAction() {
/*  191 */       return WindowsFileChooserUI.this.getNewFolderAction();
/*      */     }
/*      */ 
/*      */     public MouseListener createDoubleClickListener(JList paramJList) {
/*  195 */       return WindowsFileChooserUI.this.createDoubleClickListener(getFileChooser(), paramJList);
/*      */     }
/*      */ 
/*      */     public ListSelectionListener createListSelectionListener()
/*      */     {
/*  200 */       return WindowsFileChooserUI.this.createListSelectionListener(getFileChooser());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class WindowsFileView extends BasicFileChooserUI.BasicFileView
/*      */   {
/*      */     protected WindowsFileView()
/*      */     {
/* 1291 */       super();
/*      */     }
/*      */ 
/*      */     public Icon getIcon(File paramFile) {
/* 1295 */       Icon localIcon = getCachedIcon(paramFile);
/* 1296 */       if (localIcon != null) {
/* 1297 */         return localIcon;
/*      */       }
/* 1299 */       if (paramFile != null) {
/* 1300 */         localIcon = WindowsFileChooserUI.this.getFileChooser().getFileSystemView().getSystemIcon(paramFile);
/*      */       }
/* 1302 */       if (localIcon == null) {
/* 1303 */         localIcon = super.getIcon(paramFile);
/*      */       }
/* 1305 */       cacheIcon(paramFile, localIcon);
/* 1306 */       return localIcon;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class WindowsNewFolderAction extends BasicFileChooserUI.NewFolderAction
/*      */   {
/*      */     protected WindowsNewFolderAction()
/*      */     {
/*  588 */       super();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.windows.WindowsFileChooserUI
 * JD-Core Version:    0.6.2
 */