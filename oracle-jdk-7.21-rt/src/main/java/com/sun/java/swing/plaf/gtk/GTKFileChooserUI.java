/*      */ package com.sun.java.swing.plaf.gtk;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.AbstractListModel;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ComboBoxModel;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.DefaultListSelectionModel;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListModel;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.TransferHandler;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.event.ListDataEvent;
/*      */ import javax.swing.event.ListDataListener;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.filechooser.FileFilter;
/*      */ import javax.swing.filechooser.FileSystemView;
/*      */ import javax.swing.filechooser.FileView;
/*      */ import javax.swing.plaf.ActionMapUIResource;
/*      */ import javax.swing.plaf.ComponentUI;
/*      */ import javax.swing.plaf.UIResource;
/*      */ import javax.swing.plaf.basic.BasicDirectoryModel;
/*      */ import javax.swing.plaf.basic.BasicFileChooserUI.ApproveSelectionAction;
/*      */ import javax.swing.plaf.basic.BasicFileChooserUI.BasicFileView;
/*      */ import javax.swing.table.JTableHeader;
/*      */ import javax.swing.table.TableCellRenderer;
/*      */ import sun.awt.shell.ShellFolder;
/*      */ import sun.swing.SwingUtilities2;
/*      */ import sun.swing.plaf.synth.SynthFileChooserUI;
/*      */ 
/*      */ class GTKFileChooserUI extends SynthFileChooserUI
/*      */ {
/*   59 */   private JPanel accessoryPanel = null;
/*      */ 
/*   61 */   private String newFolderButtonText = null;
/*   62 */   private String newFolderErrorSeparator = null;
/*   63 */   private String newFolderErrorText = null;
/*   64 */   private String newFolderDialogText = null;
/*   65 */   private String newFolderNoDirectoryErrorTitleText = null;
/*   66 */   private String newFolderNoDirectoryErrorText = null;
/*      */ 
/*   68 */   private String deleteFileButtonText = null;
/*   69 */   private String renameFileButtonText = null;
/*      */ 
/*   71 */   private String newFolderButtonToolTipText = null;
/*   72 */   private String deleteFileButtonToolTipText = null;
/*   73 */   private String renameFileButtonToolTipText = null;
/*      */ 
/*   75 */   private int newFolderButtonMnemonic = 0;
/*   76 */   private int deleteFileButtonMnemonic = 0;
/*   77 */   private int renameFileButtonMnemonic = 0;
/*   78 */   private int foldersLabelMnemonic = 0;
/*   79 */   private int filesLabelMnemonic = 0;
/*      */ 
/*   81 */   private String renameFileDialogText = null;
/*   82 */   private String renameFileErrorTitle = null;
/*   83 */   private String renameFileErrorText = null;
/*      */   private JComboBox filterComboBox;
/*      */   private FilterComboBoxModel filterComboBoxModel;
/*      */   private JPanel rightPanel;
/*      */   private JList directoryList;
/*      */   private JList fileList;
/*      */   private JLabel pathField;
/*      */   private JTextField fileNameTextField;
/*   97 */   private static final Dimension hstrut3 = new Dimension(3, 1);
/*   98 */   private static final Dimension vstrut10 = new Dimension(1, 10);
/*      */ 
/*  100 */   private static Dimension prefListSize = new Dimension(75, 150);
/*      */ 
/*  102 */   private static Dimension PREF_SIZE = new Dimension(435, 360);
/*  103 */   private static Dimension MIN_SIZE = new Dimension(200, 300);
/*      */ 
/*  105 */   private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);
/*      */ 
/*  107 */   private static Dimension MAX_SIZE = new Dimension(32767, 32767);
/*      */ 
/*  109 */   private static final Insets buttonMargin = new Insets(3, 3, 3, 3);
/*      */ 
/*  111 */   private String filesLabelText = null;
/*  112 */   private String foldersLabelText = null;
/*  113 */   private String pathLabelText = null;
/*  114 */   private String filterLabelText = null;
/*      */ 
/*  116 */   private int pathLabelMnemonic = 0;
/*  117 */   private int filterLabelMnemonic = 0;
/*      */   private JComboBox directoryComboBox;
/*      */   private DirectoryComboBoxModel directoryComboBoxModel;
/*  121 */   private Action directoryComboBoxAction = new DirectoryComboBoxAction();
/*      */   private JPanel bottomButtonPanel;
/*  123 */   private GTKDirectoryModel model = null;
/*      */   private Action newFolderAction;
/*      */   private boolean readOnly;
/*      */   private boolean showDirectoryIcons;
/*      */   private boolean showFileIcons;
/*  128 */   private GTKFileView fileView = new GTKFileView();
/*      */   private PropertyChangeListener gtkFCPropertyChangeListener;
/*  130 */   private Action approveSelectionAction = new GTKApproveSelectionAction(null);
/*      */   private GTKDirectoryListModel directoryListModel;
/*      */ 
/*      */   public GTKFileChooserUI(JFileChooser paramJFileChooser)
/*      */   {
/*  134 */     super(paramJFileChooser);
/*      */   }
/*      */ 
/*      */   protected ActionMap createActionMap() {
/*  138 */     ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
/*  139 */     localActionMapUIResource.put("approveSelection", getApproveSelectionAction());
/*  140 */     localActionMapUIResource.put("cancelSelection", getCancelSelectionAction());
/*  141 */     localActionMapUIResource.put("Go Up", getChangeToParentDirectoryAction());
/*  142 */     localActionMapUIResource.put("fileNameCompletion", getFileNameCompletionAction());
/*  143 */     return localActionMapUIResource;
/*      */   }
/*      */ 
/*      */   public String getFileName() {
/*  147 */     JFileChooser localJFileChooser = getFileChooser();
/*  148 */     String str = this.fileNameTextField != null ? this.fileNameTextField.getText() : null;
/*      */ 
/*  151 */     if (!localJFileChooser.isMultiSelectionEnabled()) {
/*  152 */       return str;
/*      */     }
/*      */ 
/*  155 */     int i = localJFileChooser.getFileSelectionMode();
/*  156 */     JList localJList = i == 1 ? this.directoryList : this.fileList;
/*      */ 
/*  158 */     Object[] arrayOfObject = localJList.getSelectedValues();
/*  159 */     int j = arrayOfObject.length;
/*  160 */     Vector localVector = new Vector(j + 1);
/*      */ 
/*  163 */     for (int k = 0; k < j; k++) {
/*  164 */       File localFile = (File)arrayOfObject[k];
/*  165 */       localVector.add(localFile.getName());
/*      */     }
/*      */ 
/*  168 */     if ((str != null) && (!localVector.contains(str))) {
/*  169 */       localVector.add(str);
/*      */     }
/*      */ 
/*  172 */     StringBuffer localStringBuffer = new StringBuffer();
/*  173 */     j = localVector.size();
/*      */ 
/*  176 */     for (int m = 0; m < j; m++) {
/*  177 */       if (m > 0) {
/*  178 */         localStringBuffer.append(" ");
/*      */       }
/*  180 */       if (j > 1) {
/*  181 */         localStringBuffer.append("\"");
/*      */       }
/*  183 */       localStringBuffer.append((String)localVector.get(m));
/*  184 */       if (j > 1) {
/*  185 */         localStringBuffer.append("\"");
/*      */       }
/*      */     }
/*  188 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public void setFileName(String paramString) {
/*  192 */     if (this.fileNameTextField != null)
/*  193 */       this.fileNameTextField.setText(paramString);
/*      */   }
/*      */ 
/*      */   public void setDirectoryName(String paramString)
/*      */   {
/*  202 */     this.pathField.setText(paramString);
/*      */   }
/*      */ 
/*      */   public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {
/*  210 */     getModel().validateFileCache();
/*      */   }
/*      */ 
/*      */   public JPanel getAccessoryPanel() {
/*  214 */     return this.accessoryPanel;
/*      */   }
/*      */ 
/*      */   public FileView getFileView(JFileChooser paramJFileChooser)
/*      */   {
/*  222 */     return this.fileView;
/*      */   }
/*      */ 
/*      */   private void updateDefaultButton()
/*      */   {
/*  247 */     JFileChooser localJFileChooser = getFileChooser();
/*  248 */     JRootPane localJRootPane = SwingUtilities.getRootPane(localJFileChooser);
/*  249 */     if (localJRootPane == null) {
/*  250 */       return;
/*      */     }
/*      */ 
/*  253 */     if (localJFileChooser.getControlButtonsAreShown()) {
/*  254 */       if (localJRootPane.getDefaultButton() == null) {
/*  255 */         localJRootPane.setDefaultButton(getApproveButton(localJFileChooser));
/*  256 */         getCancelButton(localJFileChooser).setDefaultCapable(false);
/*      */       }
/*      */     }
/*  259 */     else if (localJRootPane.getDefaultButton() == getApproveButton(localJFileChooser))
/*  260 */       localJRootPane.setDefaultButton(null);
/*      */   }
/*      */ 
/*      */   protected void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  266 */     super.doSelectedFileChanged(paramPropertyChangeEvent);
/*  267 */     File localFile = (File)paramPropertyChangeEvent.getNewValue();
/*  268 */     if (localFile != null)
/*  269 */       setFileName(getFileChooser().getName(localFile));
/*      */   }
/*      */ 
/*      */   protected void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  274 */     this.directoryList.clearSelection();
/*  275 */     ListSelectionModel localListSelectionModel = this.directoryList.getSelectionModel();
/*  276 */     if ((localListSelectionModel instanceof DefaultListSelectionModel)) {
/*  277 */       ((DefaultListSelectionModel)localListSelectionModel).moveLeadSelectionIndex(0);
/*  278 */       localListSelectionModel.setAnchorSelectionIndex(0);
/*      */     }
/*  280 */     this.fileList.clearSelection();
/*  281 */     localListSelectionModel = this.fileList.getSelectionModel();
/*  282 */     if ((localListSelectionModel instanceof DefaultListSelectionModel)) {
/*  283 */       ((DefaultListSelectionModel)localListSelectionModel).moveLeadSelectionIndex(0);
/*  284 */       localListSelectionModel.setAnchorSelectionIndex(0);
/*      */     }
/*      */ 
/*  287 */     File localFile = getFileChooser().getCurrentDirectory();
/*  288 */     if (localFile != null) {
/*      */       try {
/*  290 */         setDirectoryName(ShellFolder.getNormalizedFile((File)paramPropertyChangeEvent.getNewValue()).getPath());
/*      */       } catch (IOException localIOException) {
/*  292 */         setDirectoryName(((File)paramPropertyChangeEvent.getNewValue()).getAbsolutePath());
/*      */       }
/*  294 */       if ((getFileChooser().getFileSelectionMode() == 1) && (!getFileChooser().isMultiSelectionEnabled())) {
/*  295 */         setFileName(this.pathField.getText());
/*      */       }
/*  297 */       this.directoryComboBoxModel.addItem(localFile);
/*  298 */       this.directoryListModel.directoryChanged();
/*      */     }
/*  300 */     super.doDirectoryChanged(paramPropertyChangeEvent);
/*      */   }
/*      */ 
/*      */   protected void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  304 */     if (getAccessoryPanel() != null) {
/*  305 */       if (paramPropertyChangeEvent.getOldValue() != null) {
/*  306 */         getAccessoryPanel().remove((JComponent)paramPropertyChangeEvent.getOldValue());
/*      */       }
/*  308 */       JComponent localJComponent = (JComponent)paramPropertyChangeEvent.getNewValue();
/*  309 */       if (localJComponent != null) {
/*  310 */         getAccessoryPanel().add(localJComponent, "Center");
/*  311 */         getAccessoryPanel().setPreferredSize(localJComponent.getPreferredSize());
/*  312 */         getAccessoryPanel().setMaximumSize(MAX_SIZE);
/*      */       } else {
/*  314 */         getAccessoryPanel().setPreferredSize(ZERO_ACC_SIZE);
/*  315 */         getAccessoryPanel().setMaximumSize(ZERO_ACC_SIZE);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  321 */     this.directoryList.clearSelection();
/*  322 */     this.rightPanel.setVisible(((Integer)paramPropertyChangeEvent.getNewValue()).intValue() != 1);
/*      */ 
/*  324 */     super.doFileSelectionModeChanged(paramPropertyChangeEvent);
/*      */   }
/*      */ 
/*      */   protected void doMultiSelectionChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  328 */     if (getFileChooser().isMultiSelectionEnabled()) {
/*  329 */       this.fileList.setSelectionMode(2);
/*      */     } else {
/*  331 */       this.fileList.setSelectionMode(0);
/*  332 */       this.fileList.clearSelection();
/*      */     }
/*      */ 
/*  335 */     super.doMultiSelectionChanged(paramPropertyChangeEvent);
/*      */   }
/*      */ 
/*      */   protected void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  339 */     super.doControlButtonsChanged(paramPropertyChangeEvent);
/*      */ 
/*  341 */     JFileChooser localJFileChooser = getFileChooser();
/*  342 */     if (localJFileChooser.getControlButtonsAreShown())
/*  343 */       localJFileChooser.add(this.bottomButtonPanel, "South");
/*      */     else {
/*  345 */       localJFileChooser.remove(this.bottomButtonPanel);
/*      */     }
/*  347 */     updateDefaultButton();
/*      */   }
/*      */ 
/*      */   protected void doAncestorChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  351 */     if ((paramPropertyChangeEvent.getOldValue() == null) && (paramPropertyChangeEvent.getNewValue() != null))
/*      */     {
/*  353 */       this.fileNameTextField.selectAll();
/*  354 */       this.fileNameTextField.requestFocus();
/*  355 */       updateDefaultButton();
/*      */     }
/*      */ 
/*  358 */     super.doAncestorChanged(paramPropertyChangeEvent);
/*      */   }
/*      */ 
/*      */   public ListSelectionListener createListSelectionListener(JFileChooser paramJFileChooser)
/*      */   {
/*  368 */     return new SelectionListener();
/*      */   }
/*      */ 
/*      */   protected MouseListener createDoubleClickListener(JFileChooser paramJFileChooser, JList paramJList)
/*      */   {
/*  417 */     return new DoubleClickListener(paramJList);
/*      */   }
/*      */ 
/*      */   public static ComponentUI createUI(JComponent paramJComponent)
/*      */   {
/*  481 */     return new GTKFileChooserUI((JFileChooser)paramJComponent);
/*      */   }
/*      */ 
/*      */   public void installUI(JComponent paramJComponent) {
/*  485 */     this.accessoryPanel = new JPanel(new BorderLayout(10, 10));
/*  486 */     this.accessoryPanel.setName("GTKFileChooser.accessoryPanel");
/*      */ 
/*  488 */     super.installUI(paramJComponent);
/*      */   }
/*      */ 
/*      */   public void uninstallUI(JComponent paramJComponent) {
/*  492 */     paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
/*  493 */     super.uninstallUI(paramJComponent);
/*      */ 
/*  495 */     if (this.accessoryPanel != null) {
/*  496 */       this.accessoryPanel.removeAll();
/*      */     }
/*  498 */     this.accessoryPanel = null;
/*  499 */     getFileChooser().removeAll();
/*      */   }
/*      */ 
/*      */   public void installComponents(JFileChooser paramJFileChooser) {
/*  503 */     super.installComponents(paramJFileChooser);
/*      */ 
/*  505 */     boolean bool = paramJFileChooser.getComponentOrientation().isLeftToRight();
/*      */ 
/*  507 */     paramJFileChooser.setLayout(new BorderLayout());
/*  508 */     paramJFileChooser.setAlignmentX(0.5F);
/*      */ 
/*  511 */     JPanel localJPanel1 = new JPanel(new FlowLayout(3, 0, 0));
/*  512 */     localJPanel1.setBorder(new EmptyBorder(10, 10, 0, 10));
/*  513 */     localJPanel1.setName("GTKFileChooser.topButtonPanel");
/*      */ 
/*  515 */     if (!UIManager.getBoolean("FileChooser.readOnly")) {
/*  516 */       localJButton1 = new JButton(getNewFolderAction());
/*  517 */       localJButton1.setName("GTKFileChooser.newFolderButton");
/*  518 */       localJButton1.setMnemonic(this.newFolderButtonMnemonic);
/*  519 */       localJButton1.setToolTipText(this.newFolderButtonToolTipText);
/*  520 */       localJButton1.setText(this.newFolderButtonText);
/*  521 */       localJPanel1.add(localJButton1);
/*      */     }
/*  523 */     JButton localJButton1 = new JButton(this.deleteFileButtonText);
/*  524 */     localJButton1.setName("GTKFileChooser.deleteFileButton");
/*  525 */     localJButton1.setMnemonic(this.deleteFileButtonMnemonic);
/*  526 */     localJButton1.setToolTipText(this.deleteFileButtonToolTipText);
/*  527 */     localJButton1.setEnabled(false);
/*  528 */     localJPanel1.add(localJButton1);
/*      */ 
/*  530 */     RenameFileAction localRenameFileAction = new RenameFileAction();
/*  531 */     JButton localJButton2 = new JButton(localRenameFileAction);
/*  532 */     if (this.readOnly) {
/*  533 */       localRenameFileAction.setEnabled(false);
/*      */     }
/*  535 */     localJButton2.setText(this.renameFileButtonText);
/*  536 */     localJButton2.setName("GTKFileChooser.renameFileButton");
/*  537 */     localJButton2.setMnemonic(this.renameFileButtonMnemonic);
/*  538 */     localJButton2.setToolTipText(this.renameFileButtonToolTipText);
/*  539 */     localJPanel1.add(localJButton2);
/*      */ 
/*  541 */     paramJFileChooser.add(localJPanel1, "North");
/*      */ 
/*  544 */     JPanel localJPanel2 = new JPanel();
/*  545 */     localJPanel2.setBorder(new EmptyBorder(0, 10, 10, 10));
/*  546 */     localJPanel2.setName("GTKFileChooser.interiorPanel");
/*  547 */     align(localJPanel2);
/*  548 */     localJPanel2.setLayout(new BoxLayout(localJPanel2, 3));
/*      */ 
/*  550 */     paramJFileChooser.add(localJPanel2, "Center");
/*      */ 
/*  552 */     JPanel localJPanel3 = new JPanel(new FlowLayout(1, 0, 0)
/*      */     {
/*      */       public void layoutContainer(Container paramAnonymousContainer) {
/*  555 */         super.layoutContainer(paramAnonymousContainer);
/*  556 */         JComboBox localJComboBox = GTKFileChooserUI.this.directoryComboBox;
/*  557 */         if (localJComboBox.getWidth() > paramAnonymousContainer.getWidth())
/*  558 */           localJComboBox.setBounds(0, localJComboBox.getY(), paramAnonymousContainer.getWidth(), localJComboBox.getHeight());
/*      */       }
/*      */     });
/*  563 */     localJPanel3.setBorder(new EmptyBorder(0, 0, 4, 0));
/*  564 */     localJPanel3.setName("GTKFileChooser.directoryComboBoxPanel");
/*      */ 
/*  566 */     this.directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
/*  567 */     this.directoryComboBox = new JComboBox(this.directoryComboBoxModel);
/*  568 */     this.directoryComboBox.setName("GTKFileChooser.directoryComboBox");
/*  569 */     this.directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
/*  570 */     this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
/*  571 */     this.directoryComboBox.setMaximumRowCount(8);
/*  572 */     localJPanel3.add(this.directoryComboBox);
/*  573 */     localJPanel2.add(localJPanel3);
/*      */ 
/*  577 */     JPanel localJPanel4 = new JPanel(new BorderLayout());
/*  578 */     localJPanel4.setName("GTKFileChooser.centerPanel");
/*      */ 
/*  581 */     JSplitPane localJSplitPane = new JSplitPane();
/*  582 */     localJSplitPane.setName("GTKFileChooser.splitPanel");
/*  583 */     localJSplitPane.setDividerLocation((PREF_SIZE.width - 8) / 2);
/*      */ 
/*  586 */     JPanel localJPanel5 = new JPanel(new GridBagLayout());
/*  587 */     localJPanel5.setName("GTKFileChooser.directoryListPanel");
/*      */ 
/*  591 */     TableCellRenderer localTableCellRenderer = new JTableHeader().getDefaultRenderer();
/*  592 */     JLabel localJLabel1 = (JLabel)localTableCellRenderer.getTableCellRendererComponent(null, this.foldersLabelText, false, false, 0, 0);
/*      */ 
/*  595 */     localJLabel1.setName("GTKFileChooser.directoryListLabel");
/*  596 */     localJPanel5.add(localJLabel1, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
/*      */ 
/*  600 */     localJPanel5.add(createDirectoryList(), new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
/*      */ 
/*  604 */     localJLabel1.setDisplayedMnemonic(this.foldersLabelMnemonic);
/*  605 */     localJLabel1.setLabelFor(this.directoryList);
/*      */ 
/*  608 */     this.rightPanel = new JPanel(new GridBagLayout());
/*  609 */     this.rightPanel.setName("GTKFileChooser.fileListPanel");
/*      */ 
/*  611 */     localTableCellRenderer = new JTableHeader().getDefaultRenderer();
/*  612 */     JLabel localJLabel2 = (JLabel)localTableCellRenderer.getTableCellRendererComponent(null, this.filesLabelText, false, false, 0, 0);
/*      */ 
/*  615 */     localJLabel2.setName("GTKFileChooser.fileListLabel");
/*  616 */     this.rightPanel.add(localJLabel2, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
/*      */ 
/*  620 */     this.rightPanel.add(createFilesList(), new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
/*      */ 
/*  624 */     localJLabel2.setDisplayedMnemonic(this.filesLabelMnemonic);
/*  625 */     localJLabel2.setLabelFor(this.fileList);
/*      */ 
/*  627 */     localJSplitPane.add(localJPanel5, bool ? "left" : "right");
/*  628 */     localJSplitPane.add(this.rightPanel, bool ? "right" : "left");
/*  629 */     localJPanel4.add(localJSplitPane, "Center");
/*      */ 
/*  631 */     JPanel localJPanel6 = getAccessoryPanel();
/*  632 */     JComponent localJComponent = paramJFileChooser.getAccessory();
/*  633 */     if (localJPanel6 != null) {
/*  634 */       if (localJComponent == null) {
/*  635 */         localJPanel6.setPreferredSize(ZERO_ACC_SIZE);
/*  636 */         localJPanel6.setMaximumSize(ZERO_ACC_SIZE);
/*      */       } else {
/*  638 */         getAccessoryPanel().add(localJComponent, "Center");
/*  639 */         localJPanel6.setPreferredSize(localJComponent.getPreferredSize());
/*  640 */         localJPanel6.setMaximumSize(MAX_SIZE);
/*      */       }
/*  642 */       align(localJPanel6);
/*  643 */       localJPanel4.add(localJPanel6, "After");
/*      */     }
/*  645 */     localJPanel2.add(localJPanel4);
/*  646 */     localJPanel2.add(Box.createRigidArea(vstrut10));
/*      */ 
/*  648 */     JPanel localJPanel7 = new JPanel(new FlowLayout(3, 0, 0));
/*      */ 
/*  650 */     localJPanel7.setBorder(new EmptyBorder(0, 0, 4, 0));
/*  651 */     JLabel localJLabel3 = new JLabel(this.pathLabelText);
/*  652 */     localJLabel3.setName("GTKFileChooser.pathFieldLabel");
/*  653 */     localJLabel3.setDisplayedMnemonic(this.pathLabelMnemonic);
/*  654 */     align(localJLabel3);
/*  655 */     localJPanel7.add(localJLabel3);
/*  656 */     localJPanel7.add(Box.createRigidArea(hstrut3));
/*      */ 
/*  658 */     File localFile = paramJFileChooser.getCurrentDirectory();
/*  659 */     String str = null;
/*  660 */     if (localFile != null) {
/*  661 */       str = localFile.getPath();
/*      */     }
/*  663 */     this.pathField = new JLabel(str) {
/*      */       public Dimension getMaximumSize() {
/*  665 */         Dimension localDimension = super.getMaximumSize();
/*  666 */         localDimension.height = getPreferredSize().height;
/*  667 */         return localDimension;
/*      */       }
/*      */     };
/*  670 */     this.pathField.setName("GTKFileChooser.pathField");
/*  671 */     align(this.pathField);
/*  672 */     localJPanel7.add(this.pathField);
/*  673 */     localJPanel2.add(localJPanel7);
/*      */ 
/*  676 */     this.fileNameTextField = new JTextField() {
/*      */       public Dimension getMaximumSize() {
/*  678 */         Dimension localDimension = super.getMaximumSize();
/*  679 */         localDimension.height = getPreferredSize().height;
/*  680 */         return localDimension;
/*      */       }
/*      */     };
/*  684 */     localJLabel3.setLabelFor(this.fileNameTextField);
/*      */ 
/*  686 */     Object localObject = this.fileNameTextField.getFocusTraversalKeys(0);
/*      */ 
/*  688 */     localObject = new HashSet((Collection)localObject);
/*  689 */     ((Set)localObject).remove(KeyStroke.getKeyStroke(9, 0));
/*  690 */     this.fileNameTextField.setFocusTraversalKeys(0, (Set)localObject);
/*      */ 
/*  692 */     this.fileNameTextField.setName("GTKFileChooser.fileNameTextField");
/*  693 */     this.fileNameTextField.getActionMap().put("fileNameCompletionAction", getFileNameCompletionAction());
/*  694 */     this.fileNameTextField.getInputMap().put(KeyStroke.getKeyStroke(9, 0), "fileNameCompletionAction");
/*  695 */     localJPanel2.add(this.fileNameTextField);
/*      */ 
/*  698 */     JPanel localJPanel8 = new JPanel();
/*  699 */     localJPanel8.setLayout(new FlowLayout(3, 0, 0));
/*  700 */     localJPanel8.setBorder(new EmptyBorder(0, 0, 4, 0));
/*  701 */     JLabel localJLabel4 = new JLabel(this.filterLabelText);
/*  702 */     localJLabel4.setName("GTKFileChooser.filterLabel");
/*  703 */     localJLabel4.setDisplayedMnemonic(this.filterLabelMnemonic);
/*  704 */     localJPanel8.add(localJLabel4);
/*      */ 
/*  706 */     this.filterComboBoxModel = createFilterComboBoxModel();
/*  707 */     paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
/*  708 */     this.filterComboBox = new JComboBox(this.filterComboBoxModel);
/*  709 */     this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
/*  710 */     localJLabel4.setLabelFor(this.filterComboBox);
/*      */ 
/*  712 */     localJPanel2.add(Box.createRigidArea(vstrut10));
/*  713 */     localJPanel2.add(localJPanel8);
/*  714 */     localJPanel2.add(this.filterComboBox);
/*      */ 
/*  717 */     this.bottomButtonPanel = new JPanel(new FlowLayout(4));
/*  718 */     this.bottomButtonPanel.setName("GTKFileChooser.bottomButtonPanel");
/*  719 */     align(this.bottomButtonPanel);
/*      */ 
/*  721 */     JPanel localJPanel9 = new JPanel(new GridLayout(1, 2, 5, 0));
/*      */ 
/*  723 */     JButton localJButton3 = getCancelButton(paramJFileChooser);
/*  724 */     align(localJButton3);
/*  725 */     localJButton3.setMargin(buttonMargin);
/*  726 */     localJPanel9.add(localJButton3);
/*      */ 
/*  728 */     JButton localJButton4 = getApproveButton(paramJFileChooser);
/*  729 */     align(localJButton4);
/*  730 */     localJButton4.setMargin(buttonMargin);
/*  731 */     localJPanel9.add(localJButton4);
/*      */ 
/*  733 */     this.bottomButtonPanel.add(localJPanel9);
/*      */ 
/*  735 */     if (paramJFileChooser.getControlButtonsAreShown())
/*  736 */       paramJFileChooser.add(this.bottomButtonPanel, "South");
/*      */   }
/*      */ 
/*      */   protected void installListeners(JFileChooser paramJFileChooser)
/*      */   {
/*  741 */     super.installListeners(paramJFileChooser);
/*      */ 
/*  743 */     this.gtkFCPropertyChangeListener = new GTKFCPropertyChangeListener(null);
/*  744 */     paramJFileChooser.addPropertyChangeListener(this.gtkFCPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   private int getMnemonic(String paramString, Locale paramLocale) {
/*  748 */     return SwingUtilities2.getUIDefaultsInt(paramString, paramLocale);
/*      */   }
/*      */ 
/*      */   protected void uninstallListeners(JFileChooser paramJFileChooser) {
/*  752 */     super.uninstallListeners(paramJFileChooser);
/*      */ 
/*  754 */     if (this.gtkFCPropertyChangeListener != null)
/*  755 */       paramJFileChooser.removePropertyChangeListener(this.gtkFCPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   protected void installDefaults(JFileChooser paramJFileChooser)
/*      */   {
/*  771 */     super.installDefaults(paramJFileChooser);
/*  772 */     this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
/*  773 */     this.showDirectoryIcons = Boolean.TRUE.equals(paramJFileChooser.getClientProperty("GTKFileChooser.showDirectoryIcons"));
/*      */ 
/*  775 */     this.showFileIcons = Boolean.TRUE.equals(paramJFileChooser.getClientProperty("GTKFileChooser.showFileIcons"));
/*      */   }
/*      */ 
/*      */   protected void installIcons(JFileChooser paramJFileChooser)
/*      */   {
/*  780 */     this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
/*  781 */     this.fileIcon = UIManager.getIcon("FileView.fileIcon");
/*      */   }
/*      */ 
/*      */   protected void installStrings(JFileChooser paramJFileChooser) {
/*  785 */     super.installStrings(paramJFileChooser);
/*      */ 
/*  787 */     Locale localLocale = paramJFileChooser.getLocale();
/*      */ 
/*  789 */     this.newFolderDialogText = UIManager.getString("FileChooser.newFolderDialogText", localLocale);
/*  790 */     this.newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText", localLocale);
/*  791 */     this.newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator", localLocale);
/*  792 */     this.newFolderButtonText = UIManager.getString("FileChooser.newFolderButtonText", localLocale);
/*  793 */     this.newFolderNoDirectoryErrorTitleText = UIManager.getString("FileChooser.newFolderNoDirectoryErrorTitleText", localLocale);
/*  794 */     this.newFolderNoDirectoryErrorText = UIManager.getString("FileChooser.newFolderNoDirectoryErrorText", localLocale);
/*  795 */     this.deleteFileButtonText = UIManager.getString("FileChooser.deleteFileButtonText", localLocale);
/*  796 */     this.renameFileButtonText = UIManager.getString("FileChooser.renameFileButtonText", localLocale);
/*      */ 
/*  798 */     this.newFolderButtonMnemonic = getMnemonic("FileChooser.newFolderButtonMnemonic", localLocale);
/*  799 */     this.deleteFileButtonMnemonic = getMnemonic("FileChooser.deleteFileButtonMnemonic", localLocale);
/*  800 */     this.renameFileButtonMnemonic = getMnemonic("FileChooser.renameFileButtonMnemonic", localLocale);
/*      */ 
/*  802 */     this.newFolderButtonToolTipText = UIManager.getString("FileChooser.newFolderButtonToolTipText", localLocale);
/*  803 */     this.deleteFileButtonToolTipText = UIManager.getString("FileChooser.deleteFileButtonToolTipText", localLocale);
/*  804 */     this.renameFileButtonToolTipText = UIManager.getString("FileChooser.renameFileButtonToolTipText", localLocale);
/*      */ 
/*  806 */     this.renameFileDialogText = UIManager.getString("FileChooser.renameFileDialogText", localLocale);
/*  807 */     this.renameFileErrorTitle = UIManager.getString("FileChooser.renameFileErrorTitle", localLocale);
/*  808 */     this.renameFileErrorText = UIManager.getString("FileChooser.renameFileErrorText", localLocale);
/*      */ 
/*  810 */     this.foldersLabelText = UIManager.getString("FileChooser.foldersLabelText", localLocale);
/*  811 */     this.foldersLabelMnemonic = getMnemonic("FileChooser.foldersLabelMnemonic", localLocale);
/*      */ 
/*  813 */     this.filesLabelText = UIManager.getString("FileChooser.filesLabelText", localLocale);
/*  814 */     this.filesLabelMnemonic = getMnemonic("FileChooser.filesLabelMnemonic", localLocale);
/*      */ 
/*  816 */     this.pathLabelText = UIManager.getString("FileChooser.pathLabelText", localLocale);
/*  817 */     this.pathLabelMnemonic = getMnemonic("FileChooser.pathLabelMnemonic", localLocale);
/*      */ 
/*  819 */     this.filterLabelText = UIManager.getString("FileChooser.filterLabelText", localLocale);
/*  820 */     this.filterLabelMnemonic = UIManager.getInt("FileChooser.filterLabelMnemonic");
/*      */   }
/*      */ 
/*      */   protected void uninstallStrings(JFileChooser paramJFileChooser) {
/*  824 */     super.uninstallStrings(paramJFileChooser);
/*      */ 
/*  826 */     this.newFolderButtonText = null;
/*  827 */     this.deleteFileButtonText = null;
/*  828 */     this.renameFileButtonText = null;
/*      */ 
/*  830 */     this.newFolderButtonToolTipText = null;
/*  831 */     this.deleteFileButtonToolTipText = null;
/*  832 */     this.renameFileButtonToolTipText = null;
/*      */ 
/*  834 */     this.renameFileDialogText = null;
/*  835 */     this.renameFileErrorTitle = null;
/*  836 */     this.renameFileErrorText = null;
/*      */ 
/*  838 */     this.foldersLabelText = null;
/*  839 */     this.filesLabelText = null;
/*      */ 
/*  841 */     this.pathLabelText = null;
/*      */ 
/*  843 */     this.newFolderDialogText = null;
/*  844 */     this.newFolderErrorText = null;
/*  845 */     this.newFolderErrorSeparator = null;
/*      */   }
/*      */ 
/*      */   protected JScrollPane createFilesList() {
/*  849 */     this.fileList = new JList();
/*  850 */     this.fileList.setName("GTKFileChooser.fileList");
/*  851 */     this.fileList.putClientProperty("AccessibleName", this.filesLabelText);
/*      */ 
/*  853 */     if (getFileChooser().isMultiSelectionEnabled())
/*  854 */       this.fileList.setSelectionMode(2);
/*      */     else {
/*  856 */       this.fileList.setSelectionMode(0);
/*      */     }
/*      */ 
/*  859 */     this.fileList.setModel(new GTKFileListModel());
/*  860 */     this.fileList.getSelectionModel().removeSelectionInterval(0, 0);
/*  861 */     this.fileList.setCellRenderer(new FileCellRenderer());
/*  862 */     this.fileList.addListSelectionListener(createListSelectionListener(getFileChooser()));
/*  863 */     this.fileList.addMouseListener(createDoubleClickListener(getFileChooser(), this.fileList));
/*  864 */     align(this.fileList);
/*  865 */     JScrollPane localJScrollPane = new JScrollPane(this.fileList);
/*  866 */     localJScrollPane.setVerticalScrollBarPolicy(22);
/*  867 */     localJScrollPane.setName("GTKFileChooser.fileListScrollPane");
/*  868 */     localJScrollPane.setPreferredSize(prefListSize);
/*  869 */     localJScrollPane.setMaximumSize(MAX_SIZE);
/*  870 */     align(localJScrollPane);
/*  871 */     return localJScrollPane;
/*      */   }
/*      */ 
/*      */   protected JScrollPane createDirectoryList() {
/*  875 */     this.directoryList = new JList();
/*  876 */     this.directoryList.setName("GTKFileChooser.directoryList");
/*  877 */     this.directoryList.putClientProperty("AccessibleName", this.foldersLabelText);
/*  878 */     align(this.directoryList);
/*      */ 
/*  880 */     this.directoryList.setCellRenderer(new DirectoryCellRenderer());
/*  881 */     this.directoryListModel = new GTKDirectoryListModel();
/*  882 */     this.directoryList.getSelectionModel().removeSelectionInterval(0, 0);
/*  883 */     this.directoryList.setModel(this.directoryListModel);
/*  884 */     this.directoryList.addMouseListener(createDoubleClickListener(getFileChooser(), this.directoryList));
/*  885 */     this.directoryList.addListSelectionListener(createListSelectionListener(getFileChooser()));
/*      */ 
/*  887 */     JScrollPane localJScrollPane = new JScrollPane(this.directoryList);
/*  888 */     localJScrollPane.setVerticalScrollBarPolicy(22);
/*  889 */     localJScrollPane.setName("GTKFileChooser.directoryListScrollPane");
/*  890 */     localJScrollPane.setMaximumSize(MAX_SIZE);
/*  891 */     localJScrollPane.setPreferredSize(prefListSize);
/*  892 */     align(localJScrollPane);
/*  893 */     return localJScrollPane;
/*      */   }
/*      */ 
/*      */   protected void createModel() {
/*  897 */     this.model = new GTKDirectoryModel();
/*      */   }
/*      */ 
/*      */   public BasicDirectoryModel getModel() {
/*  901 */     return this.model;
/*      */   }
/*      */ 
/*      */   public Action getApproveSelectionAction() {
/*  905 */     return this.approveSelectionAction;
/*      */   }
/*      */ 
/*      */   public Dimension getPreferredSize(JComponent paramJComponent)
/*      */   {
/* 1042 */     Dimension localDimension1 = new Dimension(PREF_SIZE);
/* 1043 */     JComponent localJComponent = getFileChooser().getAccessory();
/* 1044 */     if (localJComponent != null) {
/* 1045 */       localDimension1.width += localJComponent.getPreferredSize().width + 20;
/*      */     }
/* 1047 */     Dimension localDimension2 = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
/* 1048 */     if (localDimension2 != null) {
/* 1049 */       return new Dimension(localDimension2.width < localDimension1.width ? localDimension1.width : localDimension2.width, localDimension2.height < localDimension1.height ? localDimension1.height : localDimension2.height);
/*      */     }
/*      */ 
/* 1052 */     return localDimension1;
/*      */   }
/*      */ 
/*      */   public Dimension getMinimumSize(JComponent paramJComponent)
/*      */   {
/* 1057 */     return new Dimension(MIN_SIZE);
/*      */   }
/*      */ 
/*      */   public Dimension getMaximumSize(JComponent paramJComponent) {
/* 1061 */     return new Dimension(2147483647, 2147483647);
/*      */   }
/*      */ 
/*      */   protected void align(JComponent paramJComponent) {
/* 1065 */     paramJComponent.setAlignmentX(0.0F);
/* 1066 */     paramJComponent.setAlignmentY(0.0F);
/*      */   }
/*      */ 
/*      */   public Action getNewFolderAction() {
/* 1070 */     if (this.newFolderAction == null) {
/* 1071 */       this.newFolderAction = new NewFolderAction();
/* 1072 */       this.newFolderAction.setEnabled(!this.readOnly);
/*      */     }
/* 1074 */     return this.newFolderAction;
/*      */   }
/*      */ 
/*      */   protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser)
/*      */   {
/* 1081 */     return new DirectoryComboBoxModel();
/*      */   }
/*      */ 
/*      */   protected FilterComboBoxRenderer createFilterComboBoxRenderer()
/*      */   {
/* 1282 */     return new FilterComboBoxRenderer();
/*      */   }
/*      */ 
/*      */   protected FilterComboBoxModel createFilterComboBoxModel()
/*      */   {
/* 1325 */     return new FilterComboBoxModel();
/*      */   }
/*      */ 
/*      */   protected class DirectoryCellRenderer extends DefaultListCellRenderer
/*      */   {
/*      */     protected DirectoryCellRenderer()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 1029 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*      */ 
/* 1031 */       if (GTKFileChooserUI.this.showDirectoryIcons) {
/* 1032 */         setIcon(GTKFileChooserUI.this.getFileChooser().getIcon((File)paramObject));
/* 1033 */         setText(GTKFileChooserUI.this.getFileChooser().getName((File)paramObject));
/*      */       } else {
/* 1035 */         setText(GTKFileChooserUI.this.getFileChooser().getName((File)paramObject) + "/");
/*      */       }
/* 1037 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class DirectoryComboBoxAction extends AbstractAction
/*      */   {
/*      */     protected DirectoryComboBoxAction()
/*      */     {
/* 1165 */       super();
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1169 */       File localFile = (File)GTKFileChooserUI.this.directoryComboBox.getSelectedItem();
/* 1170 */       GTKFileChooserUI.this.getFileChooser().setCurrentDirectory(localFile);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class DirectoryComboBoxModel extends AbstractListModel
/*      */     implements ComboBoxModel
/*      */   {
/* 1088 */     Vector<File> directories = new Vector();
/* 1089 */     File selectedDirectory = null;
/* 1090 */     JFileChooser chooser = GTKFileChooserUI.this.getFileChooser();
/* 1091 */     FileSystemView fsv = this.chooser.getFileSystemView();
/*      */ 
/*      */     public DirectoryComboBoxModel()
/*      */     {
/* 1096 */       File localFile = GTKFileChooserUI.this.getFileChooser().getCurrentDirectory();
/* 1097 */       if (localFile != null)
/* 1098 */         addItem(localFile);
/*      */     }
/*      */ 
/*      */     private void addItem(File paramFile)
/*      */     {
/* 1109 */       if (paramFile == null) {
/* 1110 */         return;
/*      */       }
/*      */ 
/* 1113 */       int i = this.directories.size();
/* 1114 */       this.directories.clear();
/* 1115 */       if (i > 0) {
/* 1116 */         fireIntervalRemoved(this, 0, i);
/*      */       }
/*      */ 
/*      */       File localFile1;
/*      */       try
/*      */       {
/* 1124 */         localFile1 = this.fsv.createFileObject(ShellFolder.getNormalizedFile(paramFile).getPath());
/*      */       }
/*      */       catch (IOException localIOException) {
/* 1127 */         localFile1 = paramFile;
/*      */       }
/*      */ 
/* 1131 */       File localFile2 = localFile1;
/*      */       do
/* 1133 */         this.directories.add(localFile2);
/* 1134 */       while ((localFile2 = localFile2.getParentFile()) != null);
/* 1135 */       int j = this.directories.size();
/* 1136 */       if (j > 0) {
/* 1137 */         fireIntervalAdded(this, 0, j);
/*      */       }
/* 1139 */       setSelectedItem(localFile1);
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object paramObject) {
/* 1143 */       this.selectedDirectory = ((File)paramObject);
/* 1144 */       fireContentsChanged(this, -1, -1);
/*      */     }
/*      */ 
/*      */     public Object getSelectedItem() {
/* 1148 */       return this.selectedDirectory;
/*      */     }
/*      */ 
/*      */     public int getSize() {
/* 1152 */       return this.directories.size();
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt) {
/* 1156 */       return this.directories.elementAt(paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   class DoubleClickListener extends MouseAdapter
/*      */   {
/*      */     JList list;
/*      */ 
/*      */     public DoubleClickListener(JList arg2)
/*      */     {
/*      */       Object localObject;
/*  374 */       this.list = localObject;
/*      */     }
/*      */ 
/*      */     public void mouseClicked(MouseEvent paramMouseEvent) {
/*  378 */       if ((SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (paramMouseEvent.getClickCount() == 2)) {
/*  379 */         int i = this.list.locationToIndex(paramMouseEvent.getPoint());
/*  380 */         if (i >= 0) {
/*  381 */           File localFile = (File)this.list.getModel().getElementAt(i);
/*      */           try
/*      */           {
/*  384 */             localFile = ShellFolder.getNormalizedFile(localFile);
/*      */           }
/*      */           catch (IOException localIOException) {
/*      */           }
/*  388 */           if (GTKFileChooserUI.this.getFileChooser().isTraversable(localFile)) {
/*  389 */             this.list.clearSelection();
/*  390 */             if (GTKFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(localFile))
/*  391 */               GTKFileChooserUI.this.rescanCurrentDirectory(GTKFileChooserUI.this.getFileChooser());
/*      */             else
/*  393 */               GTKFileChooserUI.this.getFileChooser().setCurrentDirectory(localFile);
/*      */           }
/*      */           else {
/*  396 */             GTKFileChooserUI.this.getFileChooser().approveSelection();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void mouseEntered(MouseEvent paramMouseEvent) {
/*  403 */       if (this.list != null) {
/*  404 */         TransferHandler localTransferHandler1 = GTKFileChooserUI.this.getFileChooser().getTransferHandler();
/*  405 */         TransferHandler localTransferHandler2 = this.list.getTransferHandler();
/*  406 */         if (localTransferHandler1 != localTransferHandler2) {
/*  407 */           this.list.setTransferHandler(localTransferHandler1);
/*      */         }
/*  409 */         if (GTKFileChooserUI.this.getFileChooser().getDragEnabled() != this.list.getDragEnabled())
/*  410 */           this.list.setDragEnabled(GTKFileChooserUI.this.getFileChooser().getDragEnabled());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class FileCellRenderer extends DefaultListCellRenderer
/*      */   {
/*      */     protected FileCellRenderer()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 1016 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/* 1017 */       setText(GTKFileChooserUI.this.getFileChooser().getName((File)paramObject));
/* 1018 */       if (GTKFileChooserUI.this.showFileIcons) {
/* 1019 */         setIcon(GTKFileChooserUI.this.getFileChooser().getIcon((File)paramObject));
/*      */       }
/* 1021 */       return this;
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
/* 1337 */       this.filters = GTKFileChooserUI.this.getFileChooser().getChoosableFileFilters();
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 1341 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 1342 */       if (str == "ChoosableFileFilterChangedProperty") {
/* 1343 */         this.filters = ((FileFilter[])paramPropertyChangeEvent.getNewValue());
/* 1344 */         fireContentsChanged(this, -1, -1);
/* 1345 */       } else if (str == "fileFilterChanged") {
/* 1346 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object paramObject) {
/* 1351 */       if (paramObject != null) {
/* 1352 */         GTKFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)paramObject);
/* 1353 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object getSelectedItem()
/*      */     {
/* 1363 */       FileFilter localFileFilter1 = GTKFileChooserUI.this.getFileChooser().getFileFilter();
/* 1364 */       int i = 0;
/* 1365 */       if (localFileFilter1 != null) {
/* 1366 */         for (FileFilter localFileFilter2 : this.filters) {
/* 1367 */           if (localFileFilter2 == localFileFilter1) {
/* 1368 */             i = 1;
/*      */           }
/*      */         }
/* 1371 */         if (i == 0) {
/* 1372 */           GTKFileChooserUI.this.getFileChooser().addChoosableFileFilter(localFileFilter1);
/*      */         }
/*      */       }
/* 1375 */       return GTKFileChooserUI.this.getFileChooser().getFileFilter();
/*      */     }
/*      */ 
/*      */     public int getSize() {
/* 1379 */       if (this.filters != null) {
/* 1380 */         return this.filters.length;
/*      */       }
/* 1382 */       return 0;
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt)
/*      */     {
/* 1387 */       if (paramInt > getSize() - 1)
/*      */       {
/* 1389 */         return GTKFileChooserUI.this.getFileChooser().getFileFilter();
/*      */       }
/* 1391 */       if (this.filters != null) {
/* 1392 */         return this.filters[paramInt];
/*      */       }
/* 1394 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class FilterComboBoxRenderer extends DefaultListCellRenderer
/*      */     implements UIResource
/*      */   {
/*      */     public FilterComboBoxRenderer()
/*      */     {
/*      */     }
/*      */ 
/*      */     public String getName()
/*      */     {
/* 1294 */       String str = super.getName();
/* 1295 */       if (str == null) {
/* 1296 */         return "ComboBox.renderer";
/*      */       }
/* 1298 */       return str;
/*      */     }
/*      */ 
/*      */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 1305 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*      */ 
/* 1307 */       setName("ComboBox.listRenderer");
/*      */ 
/* 1309 */       if (paramObject != null) {
/* 1310 */         if ((paramObject instanceof FileFilter))
/* 1311 */           setText(((FileFilter)paramObject).getDescription());
/*      */       }
/*      */       else {
/* 1314 */         setText("");
/*      */       }
/*      */ 
/* 1317 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class GTKApproveSelectionAction extends BasicFileChooserUI.ApproveSelectionAction
/*      */   {
/*      */     private GTKApproveSelectionAction()
/*      */     {
/* 1212 */       super();
/*      */     }
/* 1214 */     public void actionPerformed(ActionEvent paramActionEvent) { if (GTKFileChooserUI.this.isDirectorySelected()) {
/* 1215 */         File localFile = GTKFileChooserUI.this.getDirectory();
/*      */         try
/*      */         {
/* 1218 */           if (localFile != null)
/* 1219 */             localFile = ShellFolder.getNormalizedFile(localFile);
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/* 1224 */         if (GTKFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(localFile)) {
/* 1225 */           GTKFileChooserUI.this.directoryList.clearSelection();
/* 1226 */           GTKFileChooserUI.this.fileList.clearSelection();
/* 1227 */           ListSelectionModel localListSelectionModel = GTKFileChooserUI.this.fileList.getSelectionModel();
/* 1228 */           if ((localListSelectionModel instanceof DefaultListSelectionModel)) {
/* 1229 */             ((DefaultListSelectionModel)localListSelectionModel).moveLeadSelectionIndex(0);
/* 1230 */             localListSelectionModel.setAnchorSelectionIndex(0);
/*      */           }
/* 1232 */           GTKFileChooserUI.this.rescanCurrentDirectory(GTKFileChooserUI.this.getFileChooser());
/* 1233 */           return;
/*      */         }
/*      */       }
/* 1236 */       super.actionPerformed(paramActionEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class GTKDirectoryListModel extends AbstractListModel
/*      */     implements ListDataListener
/*      */   {
/*      */     File curDir;
/*      */ 
/*      */     public GTKDirectoryListModel()
/*      */     {
/*  929 */       GTKFileChooserUI.this.getModel().addListDataListener(this);
/*  930 */       directoryChanged();
/*      */     }
/*      */ 
/*      */     public int getSize() {
/*  934 */       return GTKFileChooserUI.this.getModel().getDirectories().size() + 1;
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt) {
/*  938 */       return paramInt > 0 ? (File)GTKFileChooserUI.this.getModel().getDirectories().elementAt(paramInt - 1) : this.curDir;
/*      */     }
/*      */ 
/*      */     public void intervalAdded(ListDataEvent paramListDataEvent)
/*      */     {
/*  943 */       fireIntervalAdded(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*      */     }
/*      */ 
/*      */     public void intervalRemoved(ListDataEvent paramListDataEvent) {
/*  947 */       fireIntervalRemoved(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*      */     }
/*      */ 
/*      */     public void fireContentsChanged()
/*      */     {
/*  954 */       fireContentsChanged(this, 0, GTKFileChooserUI.this.getModel().getDirectories().size() - 1);
/*      */     }
/*      */ 
/*      */     public void contentsChanged(ListDataEvent paramListDataEvent)
/*      */     {
/*  960 */       fireContentsChanged();
/*      */     }
/*      */ 
/*      */     private void directoryChanged() {
/*  964 */       this.curDir = GTKFileChooserUI.this.getFileChooser().getFileSystemView().createFileObject(GTKFileChooserUI.this.getFileChooser().getCurrentDirectory(), ".");
/*      */     }
/*      */   }
/*      */ 
/*      */   private class GTKDirectoryModel extends BasicDirectoryModel
/*      */   {
/*      */     FileSystemView fsv;
/*  910 */     private Comparator<File> fileComparator = new Comparator() {
/*      */       public int compare(File paramAnonymousFile1, File paramAnonymousFile2) {
/*  912 */         return GTKFileChooserUI.GTKDirectoryModel.this.fsv.getSystemDisplayName(paramAnonymousFile1).compareTo(GTKFileChooserUI.GTKDirectoryModel.this.fsv.getSystemDisplayName(paramAnonymousFile2));
/*      */       }
/*  910 */     };
/*      */ 
/*      */     public GTKDirectoryModel()
/*      */     {
/*  917 */       super();
/*      */     }
/*      */ 
/*      */     protected void sort(Vector<? extends File> paramVector) {
/*  921 */       this.fsv = GTKFileChooserUI.this.getFileChooser().getFileSystemView();
/*  922 */       Collections.sort(paramVector, this.fileComparator);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class GTKFCPropertyChangeListener
/*      */     implements PropertyChangeListener
/*      */   {
/*      */     private GTKFCPropertyChangeListener()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*      */     {
/*  761 */       String str = paramPropertyChangeEvent.getPropertyName();
/*  762 */       if (str.equals("GTKFileChooser.showDirectoryIcons"))
/*  763 */         GTKFileChooserUI.this.showDirectoryIcons = Boolean.TRUE.equals(paramPropertyChangeEvent.getNewValue());
/*  764 */       else if (str.equals("GTKFileChooser.showFileIcons"))
/*  765 */         GTKFileChooserUI.this.showFileIcons = Boolean.TRUE.equals(paramPropertyChangeEvent.getNewValue());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class GTKFileListModel extends AbstractListModel
/*      */     implements ListDataListener
/*      */   {
/*      */     public GTKFileListModel()
/*      */     {
/*  971 */       GTKFileChooserUI.this.getModel().addListDataListener(this);
/*      */     }
/*      */ 
/*      */     public int getSize() {
/*  975 */       return GTKFileChooserUI.this.getModel().getFiles().size();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object paramObject) {
/*  979 */       return GTKFileChooserUI.this.getModel().getFiles().contains(paramObject);
/*      */     }
/*      */ 
/*      */     public int indexOf(Object paramObject) {
/*  983 */       return GTKFileChooserUI.this.getModel().getFiles().indexOf(paramObject);
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int paramInt) {
/*  987 */       return GTKFileChooserUI.this.getModel().getFiles().elementAt(paramInt);
/*      */     }
/*      */ 
/*      */     public void intervalAdded(ListDataEvent paramListDataEvent) {
/*  991 */       fireIntervalAdded(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*      */     }
/*      */ 
/*      */     public void intervalRemoved(ListDataEvent paramListDataEvent) {
/*  995 */       fireIntervalRemoved(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*      */     }
/*      */ 
/*      */     public void fireContentsChanged()
/*      */     {
/* 1002 */       fireContentsChanged(this, 0, GTKFileChooserUI.this.getModel().getFiles().size() - 1);
/*      */     }
/*      */ 
/*      */     public void contentsChanged(ListDataEvent paramListDataEvent)
/*      */     {
/* 1007 */       fireContentsChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class GTKFileView extends BasicFileChooserUI.BasicFileView
/*      */   {
/*      */     public GTKFileView()
/*      */     {
/*  226 */       super();
/*  227 */       this.iconCache = null;
/*      */     }
/*      */ 
/*      */     public void clearIconCache() {
/*      */     }
/*      */ 
/*      */     public Icon getCachedIcon(File paramFile) {
/*  234 */       return null;
/*      */     }
/*      */ 
/*      */     public void cacheIcon(File paramFile, Icon paramIcon) {
/*      */     }
/*      */ 
/*      */     public Icon getIcon(File paramFile) {
/*  241 */       return (paramFile != null) && (paramFile.isDirectory()) ? GTKFileChooserUI.this.directoryIcon : GTKFileChooserUI.this.fileIcon;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class NewFolderAction extends AbstractAction
/*      */   {
/*      */     protected NewFolderAction()
/*      */     {
/* 1179 */       super();
/*      */     }
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1182 */       if (GTKFileChooserUI.this.readOnly) {
/* 1183 */         return;
/*      */       }
/* 1185 */       JFileChooser localJFileChooser = GTKFileChooserUI.this.getFileChooser();
/* 1186 */       File localFile1 = localJFileChooser.getCurrentDirectory();
/* 1187 */       String str = JOptionPane.showInputDialog(localJFileChooser, GTKFileChooserUI.this.newFolderDialogText, GTKFileChooserUI.this.newFolderButtonText, -1);
/*      */ 
/* 1191 */       if (str != null) {
/* 1192 */         if (!localFile1.exists()) {
/* 1193 */           JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(GTKFileChooserUI.this.newFolderNoDirectoryErrorText, new Object[] { str }), GTKFileChooserUI.this.newFolderNoDirectoryErrorTitleText, 0);
/*      */ 
/* 1196 */           return;
/*      */         }
/*      */ 
/* 1199 */         File localFile2 = localJFileChooser.getFileSystemView().createFileObject(localFile1, str);
/*      */ 
/* 1201 */         if ((localFile2 == null) || (!localFile2.mkdir())) {
/* 1202 */           JOptionPane.showMessageDialog(localJFileChooser, GTKFileChooserUI.this.newFolderErrorText + GTKFileChooserUI.this.newFolderErrorSeparator + " \"" + str + "\"", GTKFileChooserUI.this.newFolderErrorText, 0);
/*      */         }
/*      */ 
/* 1207 */         localJFileChooser.rescanCurrentDirectory();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RenameFileAction extends AbstractAction
/*      */   {
/*      */     protected RenameFileAction()
/*      */     {
/* 1245 */       super();
/*      */     }
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1248 */       if (GTKFileChooserUI.this.getFileName().equals("")) {
/* 1249 */         return;
/*      */       }
/* 1251 */       JFileChooser localJFileChooser = GTKFileChooserUI.this.getFileChooser();
/* 1252 */       File localFile1 = localJFileChooser.getCurrentDirectory();
/* 1253 */       String str = (String)JOptionPane.showInputDialog(localJFileChooser, new MessageFormat(GTKFileChooserUI.this.renameFileDialogText).format(new Object[] { GTKFileChooserUI.this.getFileName() }), GTKFileChooserUI.this.renameFileButtonText, -1, null, null, GTKFileChooserUI.this.getFileName());
/*      */ 
/* 1259 */       if (str != null) {
/* 1260 */         File localFile2 = localJFileChooser.getFileSystemView().createFileObject(localFile1, GTKFileChooserUI.this.getFileName());
/*      */ 
/* 1262 */         File localFile3 = localJFileChooser.getFileSystemView().createFileObject(localFile1, str);
/*      */ 
/* 1264 */         if ((localFile2 == null) || (localFile3 == null) || (!GTKFileChooserUI.this.getModel().renameFile(localFile2, localFile3)))
/*      */         {
/* 1266 */           JOptionPane.showMessageDialog(localJFileChooser, new MessageFormat(GTKFileChooserUI.this.renameFileErrorText).format(new Object[] { GTKFileChooserUI.this.getFileName(), str }), GTKFileChooserUI.this.renameFileErrorTitle, 0);
/*      */         }
/*      */         else
/*      */         {
/* 1271 */           GTKFileChooserUI.this.setFileName(GTKFileChooserUI.this.getFileChooser().getName(localFile3));
/* 1272 */           localJFileChooser.rescanCurrentDirectory();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class SelectionListener
/*      */     implements ListSelectionListener
/*      */   {
/*      */     protected SelectionListener()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void valueChanged(ListSelectionEvent paramListSelectionEvent)
/*      */     {
/*  424 */       if (!paramListSelectionEvent.getValueIsAdjusting()) {
/*  425 */         JFileChooser localJFileChooser = GTKFileChooserUI.this.getFileChooser();
/*  426 */         JList localJList = (JList)paramListSelectionEvent.getSource();
/*      */         Object localObject1;
/*  428 */         if (localJFileChooser.isMultiSelectionEnabled()) {
/*  429 */           localObject1 = null;
/*  430 */           Object[] arrayOfObject1 = localJList.getSelectedValues();
/*  431 */           if (arrayOfObject1 != null) {
/*  432 */             if ((arrayOfObject1.length == 1) && (((File)arrayOfObject1[0]).isDirectory()) && (localJFileChooser.isTraversable((File)arrayOfObject1[0])) && ((localJFileChooser.getFileSelectionMode() != 1) || (!localJFileChooser.getFileSystemView().isFileSystem((File)arrayOfObject1[0]))))
/*      */             {
/*  437 */               GTKFileChooserUI.this.setDirectorySelected(true);
/*  438 */               GTKFileChooserUI.this.setDirectory((File)arrayOfObject1[0]);
/*      */             } else {
/*  440 */               ArrayList localArrayList = new ArrayList(arrayOfObject1.length);
/*  441 */               for (Object localObject2 : arrayOfObject1) {
/*  442 */                 File localFile = (File)localObject2;
/*  443 */                 if (((localJFileChooser.isFileSelectionEnabled()) && (localFile.isFile())) || ((localJFileChooser.isDirectorySelectionEnabled()) && (localFile.isDirectory())))
/*      */                 {
/*  445 */                   localArrayList.add(localFile);
/*      */                 }
/*      */               }
/*  448 */               if (localArrayList.size() > 0) {
/*  449 */                 localObject1 = (File[])localArrayList.toArray(new File[localArrayList.size()]);
/*      */               }
/*  451 */               GTKFileChooserUI.this.setDirectorySelected(false);
/*      */             }
/*      */           }
/*  454 */           localJFileChooser.setSelectedFiles((File[])localObject1);
/*      */         } else {
/*  456 */           localObject1 = (File)localJList.getSelectedValue();
/*  457 */           if ((localObject1 != null) && (((File)localObject1).isDirectory()) && (localJFileChooser.isTraversable((File)localObject1)) && ((localJFileChooser.getFileSelectionMode() == 0) || (!localJFileChooser.getFileSystemView().isFileSystem((File)localObject1))))
/*      */           {
/*  463 */             GTKFileChooserUI.this.setDirectorySelected(true);
/*  464 */             GTKFileChooserUI.this.setDirectory((File)localObject1);
/*      */           } else {
/*  466 */             GTKFileChooserUI.this.setDirectorySelected(false);
/*  467 */             if (localObject1 != null)
/*  468 */               localJFileChooser.setSelectedFile((File)localObject1);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKFileChooserUI
 * JD-Core Version:    0.6.2
 */