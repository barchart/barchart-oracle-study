/*     */ package com.sun.java.swing.plaf.motif;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
/*     */ import javax.swing.AbstractListModel;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ComboBoxModel;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.DefaultListSelectionModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollBar;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.ListModel;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.event.ListDataEvent;
/*     */ import javax.swing.event.ListDataListener;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.basic.BasicDirectoryModel;
/*     */ import javax.swing.plaf.basic.BasicFileChooserUI;
/*     */ import sun.awt.shell.ShellFolder;
/*     */ import sun.swing.SwingUtilities2;
/*     */ 
/*     */ public class MotifFileChooserUI extends BasicFileChooserUI
/*     */ {
/*     */   private FilterComboBoxModel filterComboBoxModel;
/*  52 */   protected JList directoryList = null;
/*  53 */   protected JList fileList = null;
/*     */ 
/*  55 */   protected JTextField pathField = null;
/*  56 */   protected JComboBox filterComboBox = null;
/*  57 */   protected JTextField filenameTextField = null;
/*     */ 
/*  59 */   private static final Dimension hstrut10 = new Dimension(10, 1);
/*  60 */   private static final Dimension vstrut10 = new Dimension(1, 10);
/*     */ 
/*  62 */   private static final Insets insets = new Insets(10, 10, 10, 10);
/*     */ 
/*  64 */   private static Dimension prefListSize = new Dimension(75, 150);
/*     */ 
/*  66 */   private static Dimension WITH_ACCELERATOR_PREF_SIZE = new Dimension(650, 450);
/*  67 */   private static Dimension PREF_SIZE = new Dimension(350, 450);
/*  68 */   private static Dimension MIN_SIZE = new Dimension(200, 300);
/*     */ 
/*  70 */   private static Dimension PREF_ACC_SIZE = new Dimension(10, 10);
/*  71 */   private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);
/*     */ 
/*  73 */   private static Dimension MAX_SIZE = new Dimension(32767, 32767);
/*     */ 
/*  75 */   private static final Insets buttonMargin = new Insets(3, 3, 3, 3);
/*     */   private JPanel bottomPanel;
/*     */   protected JButton approveButton;
/*  81 */   private String enterFolderNameLabelText = null;
/*  82 */   private int enterFolderNameLabelMnemonic = 0;
/*  83 */   private String enterFileNameLabelText = null;
/*  84 */   private int enterFileNameLabelMnemonic = 0;
/*     */ 
/*  86 */   private String filesLabelText = null;
/*  87 */   private int filesLabelMnemonic = 0;
/*     */ 
/*  89 */   private String foldersLabelText = null;
/*  90 */   private int foldersLabelMnemonic = 0;
/*     */ 
/*  92 */   private String pathLabelText = null;
/*  93 */   private int pathLabelMnemonic = 0;
/*     */ 
/*  95 */   private String filterLabelText = null;
/*  96 */   private int filterLabelMnemonic = 0;
/*     */   private JLabel fileNameLabel;
/*     */ 
/*     */   private void populateFileNameLabel()
/*     */   {
/* 101 */     if (getFileChooser().getFileSelectionMode() == 1) {
/* 102 */       this.fileNameLabel.setText(this.enterFolderNameLabelText);
/* 103 */       this.fileNameLabel.setDisplayedMnemonic(this.enterFolderNameLabelMnemonic);
/*     */     } else {
/* 105 */       this.fileNameLabel.setText(this.enterFileNameLabelText);
/* 106 */       this.fileNameLabel.setDisplayedMnemonic(this.enterFileNameLabelMnemonic);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String fileNameString(File paramFile) {
/* 111 */     if (paramFile == null) {
/* 112 */       return null;
/*     */     }
/* 114 */     JFileChooser localJFileChooser = getFileChooser();
/* 115 */     if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) {
/* 116 */       return paramFile.getPath();
/*     */     }
/* 118 */     return paramFile.getName();
/*     */   }
/*     */ 
/*     */   private String fileNameString(File[] paramArrayOfFile)
/*     */   {
/* 124 */     StringBuffer localStringBuffer = new StringBuffer();
/* 125 */     for (int i = 0; (paramArrayOfFile != null) && (i < paramArrayOfFile.length); i++) {
/* 126 */       if (i > 0) {
/* 127 */         localStringBuffer.append(" ");
/*     */       }
/* 129 */       if (paramArrayOfFile.length > 1) {
/* 130 */         localStringBuffer.append("\"");
/*     */       }
/* 132 */       localStringBuffer.append(fileNameString(paramArrayOfFile[i]));
/* 133 */       if (paramArrayOfFile.length > 1) {
/* 134 */         localStringBuffer.append("\"");
/*     */       }
/*     */     }
/* 137 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   public MotifFileChooserUI(JFileChooser paramJFileChooser) {
/* 141 */     super(paramJFileChooser);
/*     */   }
/*     */ 
/*     */   public String getFileName() {
/* 145 */     if (this.filenameTextField != null) {
/* 146 */       return this.filenameTextField.getText();
/*     */     }
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFileName(String paramString)
/*     */   {
/* 153 */     if (this.filenameTextField != null)
/* 154 */       this.filenameTextField.setText(paramString);
/*     */   }
/*     */ 
/*     */   public String getDirectoryName()
/*     */   {
/* 159 */     return this.pathField.getText();
/*     */   }
/*     */ 
/*     */   public void setDirectoryName(String paramString) {
/* 163 */     this.pathField.setText(paramString);
/*     */   }
/*     */ 
/*     */   public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {
/* 171 */     getModel().validateFileCache();
/*     */   }
/*     */ 
/*     */   public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser) {
/* 175 */     return new PropertyChangeListener() {
/*     */       public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/* 177 */         String str = paramAnonymousPropertyChangeEvent.getPropertyName();
/*     */         Object localObject1;
/* 178 */         if (str.equals("SelectedFileChangedProperty")) {
/* 179 */           localObject1 = (File)paramAnonymousPropertyChangeEvent.getNewValue();
/* 180 */           if (localObject1 != null)
/* 181 */             MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getFileChooser().getName((File)localObject1));
/*     */         }
/*     */         else
/*     */         {
/*     */           Object localObject2;
/* 183 */           if (str.equals("SelectedFilesChangedProperty")) {
/* 184 */             localObject1 = (File[])paramAnonymousPropertyChangeEvent.getNewValue();
/* 185 */             localObject2 = MotifFileChooserUI.this.getFileChooser();
/* 186 */             if ((localObject1 != null) && (localObject1.length > 0) && ((localObject1.length > 1) || (((JFileChooser)localObject2).isDirectorySelectionEnabled()) || (!localObject1[0].isDirectory())))
/*     */             {
/* 188 */               MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.fileNameString((File[])localObject1));
/*     */             }
/* 190 */           } else if (str.equals("fileFilterChanged")) {
/* 191 */             MotifFileChooserUI.this.fileList.clearSelection();
/* 192 */           } else if (str.equals("directoryChanged")) {
/* 193 */             MotifFileChooserUI.this.directoryList.clearSelection();
/* 194 */             localObject1 = MotifFileChooserUI.this.directoryList.getSelectionModel();
/* 195 */             if ((localObject1 instanceof DefaultListSelectionModel)) {
/* 196 */               ((DefaultListSelectionModel)localObject1).moveLeadSelectionIndex(0);
/* 197 */               ((ListSelectionModel)localObject1).setAnchorSelectionIndex(0);
/*     */             }
/* 199 */             MotifFileChooserUI.this.fileList.clearSelection();
/* 200 */             localObject1 = MotifFileChooserUI.this.fileList.getSelectionModel();
/* 201 */             if ((localObject1 instanceof DefaultListSelectionModel)) {
/* 202 */               ((DefaultListSelectionModel)localObject1).moveLeadSelectionIndex(0);
/* 203 */               ((ListSelectionModel)localObject1).setAnchorSelectionIndex(0);
/*     */             }
/* 205 */             localObject2 = MotifFileChooserUI.this.getFileChooser().getCurrentDirectory();
/* 206 */             if (localObject2 != null) {
/*     */               try {
/* 208 */                 MotifFileChooserUI.this.setDirectoryName(ShellFolder.getNormalizedFile((File)paramAnonymousPropertyChangeEvent.getNewValue()).getPath());
/*     */               } catch (IOException localIOException) {
/* 210 */                 MotifFileChooserUI.this.setDirectoryName(((File)paramAnonymousPropertyChangeEvent.getNewValue()).getAbsolutePath());
/*     */               }
/* 212 */               if ((MotifFileChooserUI.this.getFileChooser().getFileSelectionMode() == 1) && (!MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()))
/* 213 */                 MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getDirectoryName());
/*     */             }
/*     */           }
/* 216 */           else if (str.equals("fileSelectionChanged")) {
/* 217 */             if (MotifFileChooserUI.this.fileNameLabel != null) {
/* 218 */               MotifFileChooserUI.this.populateFileNameLabel();
/*     */             }
/* 220 */             MotifFileChooserUI.this.directoryList.clearSelection();
/* 221 */           } else if (str.equals("MultiSelectionEnabledChangedProperty")) {
/* 222 */             if (MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
/* 223 */               MotifFileChooserUI.this.fileList.setSelectionMode(2);
/*     */             } else {
/* 225 */               MotifFileChooserUI.this.fileList.setSelectionMode(0);
/* 226 */               MotifFileChooserUI.this.fileList.clearSelection();
/* 227 */               MotifFileChooserUI.this.getFileChooser().setSelectedFiles(null);
/*     */             }
/* 229 */           } else if (str.equals("AccessoryChangedProperty")) {
/* 230 */             if (MotifFileChooserUI.this.getAccessoryPanel() != null) {
/* 231 */               if (paramAnonymousPropertyChangeEvent.getOldValue() != null) {
/* 232 */                 MotifFileChooserUI.this.getAccessoryPanel().remove((JComponent)paramAnonymousPropertyChangeEvent.getOldValue());
/*     */               }
/* 234 */               localObject1 = (JComponent)paramAnonymousPropertyChangeEvent.getNewValue();
/* 235 */               if (localObject1 != null) {
/* 236 */                 MotifFileChooserUI.this.getAccessoryPanel().add((Component)localObject1, "Center");
/* 237 */                 MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(MotifFileChooserUI.PREF_ACC_SIZE);
/* 238 */                 MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(MotifFileChooserUI.MAX_SIZE);
/*     */               } else {
/* 240 */                 MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(MotifFileChooserUI.ZERO_ACC_SIZE);
/* 241 */                 MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(MotifFileChooserUI.ZERO_ACC_SIZE);
/*     */               }
/*     */             }
/* 244 */           } else if ((str.equals("ApproveButtonTextChangedProperty")) || (str.equals("ApproveButtonToolTipTextChangedProperty")) || (str.equals("DialogTypeChangedProperty")))
/*     */           {
/* 247 */             MotifFileChooserUI.this.approveButton.setText(MotifFileChooserUI.this.getApproveButtonText(MotifFileChooserUI.this.getFileChooser()));
/* 248 */             MotifFileChooserUI.this.approveButton.setToolTipText(MotifFileChooserUI.this.getApproveButtonToolTipText(MotifFileChooserUI.this.getFileChooser()));
/* 249 */           } else if (str.equals("ControlButtonsAreShownChangedProperty")) {
/* 250 */             MotifFileChooserUI.this.doControlButtonsChanged(paramAnonymousPropertyChangeEvent);
/* 251 */           } else if (str.equals("componentOrientation")) {
/* 252 */             localObject1 = (ComponentOrientation)paramAnonymousPropertyChangeEvent.getNewValue();
/* 253 */             localObject2 = (JFileChooser)paramAnonymousPropertyChangeEvent.getSource();
/* 254 */             if (localObject1 != (ComponentOrientation)paramAnonymousPropertyChangeEvent.getOldValue())
/* 255 */               ((JFileChooser)localObject2).applyComponentOrientation((ComponentOrientation)localObject1);
/*     */           }
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/* 266 */     return new MotifFileChooserUI((JFileChooser)paramJComponent);
/*     */   }
/*     */ 
/*     */   public void installUI(JComponent paramJComponent) {
/* 270 */     super.installUI(paramJComponent);
/*     */   }
/*     */ 
/*     */   public void uninstallUI(JComponent paramJComponent) {
/* 274 */     paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
/* 275 */     this.approveButton.removeActionListener(getApproveSelectionAction());
/* 276 */     this.filenameTextField.removeActionListener(getApproveSelectionAction());
/* 277 */     super.uninstallUI(paramJComponent);
/*     */   }
/*     */ 
/*     */   public void installComponents(JFileChooser paramJFileChooser) {
/* 281 */     paramJFileChooser.setLayout(new BorderLayout(10, 10));
/* 282 */     paramJFileChooser.setAlignmentX(0.5F);
/*     */ 
/* 284 */     JPanel local2 = new JPanel() {
/*     */       public Insets getInsets() {
/* 286 */         return MotifFileChooserUI.insets;
/*     */       }
/*     */     };
/* 289 */     local2.setInheritsPopupMenu(true);
/* 290 */     align(local2);
/* 291 */     local2.setLayout(new BoxLayout(local2, 3));
/*     */ 
/* 293 */     paramJFileChooser.add(local2, "Center");
/*     */ 
/* 296 */     JLabel localJLabel = new JLabel(this.pathLabelText);
/* 297 */     localJLabel.setDisplayedMnemonic(this.pathLabelMnemonic);
/* 298 */     align(localJLabel);
/* 299 */     local2.add(localJLabel);
/*     */ 
/* 301 */     File localFile = paramJFileChooser.getCurrentDirectory();
/* 302 */     String str = null;
/* 303 */     if (localFile != null) {
/* 304 */       str = localFile.getPath();
/*     */     }
/* 306 */     this.pathField = new JTextField(str) {
/*     */       public Dimension getMaximumSize() {
/* 308 */         Dimension localDimension = super.getMaximumSize();
/* 309 */         localDimension.height = getPreferredSize().height;
/* 310 */         return localDimension;
/*     */       }
/*     */     };
/* 313 */     this.pathField.setInheritsPopupMenu(true);
/* 314 */     localJLabel.setLabelFor(this.pathField);
/* 315 */     align(this.pathField);
/*     */ 
/* 318 */     this.pathField.addActionListener(getUpdateAction());
/* 319 */     local2.add(this.pathField);
/*     */ 
/* 321 */     local2.add(Box.createRigidArea(vstrut10));
/*     */ 
/* 325 */     JPanel localJPanel1 = new JPanel();
/* 326 */     localJPanel1.setLayout(new BoxLayout(localJPanel1, 2));
/* 327 */     align(localJPanel1);
/*     */ 
/* 330 */     JPanel localJPanel2 = new JPanel();
/* 331 */     localJPanel2.setLayout(new BoxLayout(localJPanel2, 3));
/* 332 */     align(localJPanel2);
/*     */ 
/* 335 */     localJLabel = new JLabel(this.filterLabelText);
/* 336 */     localJLabel.setDisplayedMnemonic(this.filterLabelMnemonic);
/* 337 */     align(localJLabel);
/* 338 */     localJPanel2.add(localJLabel);
/*     */ 
/* 340 */     this.filterComboBox = new JComboBox() {
/*     */       public Dimension getMaximumSize() {
/* 342 */         Dimension localDimension = super.getMaximumSize();
/* 343 */         localDimension.height = getPreferredSize().height;
/* 344 */         return localDimension;
/*     */       }
/*     */     };
/* 347 */     this.filterComboBox.setInheritsPopupMenu(true);
/* 348 */     localJLabel.setLabelFor(this.filterComboBox);
/* 349 */     this.filterComboBoxModel = createFilterComboBoxModel();
/* 350 */     this.filterComboBox.setModel(this.filterComboBoxModel);
/* 351 */     this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
/* 352 */     paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
/* 353 */     align(this.filterComboBox);
/* 354 */     localJPanel2.add(this.filterComboBox);
/*     */ 
/* 359 */     localJLabel = new JLabel(this.foldersLabelText);
/* 360 */     localJLabel.setDisplayedMnemonic(this.foldersLabelMnemonic);
/* 361 */     align(localJLabel);
/* 362 */     localJPanel2.add(localJLabel);
/* 363 */     JScrollPane localJScrollPane = createDirectoryList();
/* 364 */     localJScrollPane.getVerticalScrollBar().setFocusable(false);
/* 365 */     localJScrollPane.getHorizontalScrollBar().setFocusable(false);
/* 366 */     localJScrollPane.setInheritsPopupMenu(true);
/* 367 */     localJLabel.setLabelFor(localJScrollPane.getViewport().getView());
/* 368 */     localJPanel2.add(localJScrollPane);
/* 369 */     localJPanel2.setInheritsPopupMenu(true);
/*     */ 
/* 373 */     JPanel localJPanel3 = new JPanel();
/* 374 */     align(localJPanel3);
/* 375 */     localJPanel3.setLayout(new BoxLayout(localJPanel3, 3));
/* 376 */     localJPanel3.setInheritsPopupMenu(true);
/*     */ 
/* 378 */     localJLabel = new JLabel(this.filesLabelText);
/* 379 */     localJLabel.setDisplayedMnemonic(this.filesLabelMnemonic);
/* 380 */     align(localJLabel);
/* 381 */     localJPanel3.add(localJLabel);
/* 382 */     localJScrollPane = createFilesList();
/* 383 */     localJLabel.setLabelFor(localJScrollPane.getViewport().getView());
/* 384 */     localJPanel3.add(localJScrollPane);
/* 385 */     localJScrollPane.setInheritsPopupMenu(true);
/*     */ 
/* 387 */     localJPanel1.add(localJPanel2);
/* 388 */     localJPanel1.add(Box.createRigidArea(hstrut10));
/* 389 */     localJPanel1.add(localJPanel3);
/* 390 */     localJPanel1.setInheritsPopupMenu(true);
/*     */ 
/* 392 */     JPanel localJPanel4 = getAccessoryPanel();
/* 393 */     JComponent localJComponent = paramJFileChooser.getAccessory();
/* 394 */     if (localJPanel4 != null) {
/* 395 */       if (localJComponent == null) {
/* 396 */         localJPanel4.setPreferredSize(ZERO_ACC_SIZE);
/* 397 */         localJPanel4.setMaximumSize(ZERO_ACC_SIZE);
/*     */       } else {
/* 399 */         getAccessoryPanel().add(localJComponent, "Center");
/* 400 */         localJPanel4.setPreferredSize(PREF_ACC_SIZE);
/* 401 */         localJPanel4.setMaximumSize(MAX_SIZE);
/*     */       }
/* 403 */       align(localJPanel4);
/* 404 */       localJPanel1.add(localJPanel4);
/* 405 */       localJPanel4.setInheritsPopupMenu(true);
/*     */     }
/* 407 */     local2.add(localJPanel1);
/* 408 */     local2.add(Box.createRigidArea(vstrut10));
/*     */ 
/* 411 */     this.fileNameLabel = new JLabel();
/* 412 */     populateFileNameLabel();
/* 413 */     align(this.fileNameLabel);
/* 414 */     local2.add(this.fileNameLabel);
/*     */ 
/* 416 */     this.filenameTextField = new JTextField() {
/*     */       public Dimension getMaximumSize() {
/* 418 */         Dimension localDimension = super.getMaximumSize();
/* 419 */         localDimension.height = getPreferredSize().height;
/* 420 */         return localDimension;
/*     */       }
/*     */     };
/* 423 */     this.filenameTextField.setInheritsPopupMenu(true);
/* 424 */     this.fileNameLabel.setLabelFor(this.filenameTextField);
/* 425 */     this.filenameTextField.addActionListener(getApproveSelectionAction());
/* 426 */     align(this.filenameTextField);
/* 427 */     this.filenameTextField.setAlignmentX(0.0F);
/* 428 */     local2.add(this.filenameTextField);
/*     */ 
/* 430 */     this.bottomPanel = getBottomPanel();
/* 431 */     this.bottomPanel.add(new JSeparator(), "North");
/*     */ 
/* 434 */     JPanel localJPanel5 = new JPanel();
/* 435 */     align(localJPanel5);
/* 436 */     localJPanel5.setLayout(new BoxLayout(localJPanel5, 2));
/* 437 */     localJPanel5.add(Box.createGlue());
/*     */ 
/* 439 */     this.approveButton = new JButton(getApproveButtonText(paramJFileChooser)) {
/*     */       public Dimension getMaximumSize() {
/* 441 */         return new Dimension(MotifFileChooserUI.MAX_SIZE.width, getPreferredSize().height);
/*     */       }
/*     */     };
/* 444 */     this.approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
/* 445 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
/* 446 */     this.approveButton.setInheritsPopupMenu(true);
/* 447 */     align(this.approveButton);
/* 448 */     this.approveButton.setMargin(buttonMargin);
/* 449 */     this.approveButton.addActionListener(getApproveSelectionAction());
/* 450 */     localJPanel5.add(this.approveButton);
/* 451 */     localJPanel5.add(Box.createGlue());
/*     */ 
/* 453 */     JButton local7 = new JButton(this.updateButtonText) {
/*     */       public Dimension getMaximumSize() {
/* 455 */         return new Dimension(MotifFileChooserUI.MAX_SIZE.width, getPreferredSize().height);
/*     */       }
/*     */     };
/* 458 */     local7.setMnemonic(this.updateButtonMnemonic);
/* 459 */     local7.setToolTipText(this.updateButtonToolTipText);
/* 460 */     local7.setInheritsPopupMenu(true);
/* 461 */     align(local7);
/* 462 */     local7.setMargin(buttonMargin);
/* 463 */     local7.addActionListener(getUpdateAction());
/* 464 */     localJPanel5.add(local7);
/* 465 */     localJPanel5.add(Box.createGlue());
/*     */ 
/* 467 */     JButton local8 = new JButton(this.cancelButtonText) {
/*     */       public Dimension getMaximumSize() {
/* 469 */         return new Dimension(MotifFileChooserUI.MAX_SIZE.width, getPreferredSize().height);
/*     */       }
/*     */     };
/* 472 */     local8.setMnemonic(this.cancelButtonMnemonic);
/* 473 */     local8.setToolTipText(this.cancelButtonToolTipText);
/* 474 */     local8.setInheritsPopupMenu(true);
/* 475 */     align(local8);
/* 476 */     local8.setMargin(buttonMargin);
/* 477 */     local8.addActionListener(getCancelSelectionAction());
/* 478 */     localJPanel5.add(local8);
/* 479 */     localJPanel5.add(Box.createGlue());
/*     */ 
/* 481 */     JButton local9 = new JButton(this.helpButtonText) {
/*     */       public Dimension getMaximumSize() {
/* 483 */         return new Dimension(MotifFileChooserUI.MAX_SIZE.width, getPreferredSize().height);
/*     */       }
/*     */     };
/* 486 */     local9.setMnemonic(this.helpButtonMnemonic);
/* 487 */     local9.setToolTipText(this.helpButtonToolTipText);
/* 488 */     align(local9);
/* 489 */     local9.setMargin(buttonMargin);
/* 490 */     local9.setEnabled(false);
/* 491 */     local9.setInheritsPopupMenu(true);
/* 492 */     localJPanel5.add(local9);
/* 493 */     localJPanel5.add(Box.createGlue());
/* 494 */     localJPanel5.setInheritsPopupMenu(true);
/*     */ 
/* 496 */     this.bottomPanel.add(localJPanel5, "South");
/* 497 */     this.bottomPanel.setInheritsPopupMenu(true);
/* 498 */     if (paramJFileChooser.getControlButtonsAreShown())
/* 499 */       paramJFileChooser.add(this.bottomPanel, "South");
/*     */   }
/*     */ 
/*     */   protected JPanel getBottomPanel()
/*     */   {
/* 504 */     if (this.bottomPanel == null) {
/* 505 */       this.bottomPanel = new JPanel(new BorderLayout(0, 4));
/*     */     }
/* 507 */     return this.bottomPanel;
/*     */   }
/*     */ 
/*     */   private void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/* 511 */     if (getFileChooser().getControlButtonsAreShown())
/* 512 */       getFileChooser().add(this.bottomPanel, "South");
/*     */     else
/* 514 */       getFileChooser().remove(getBottomPanel());
/*     */   }
/*     */ 
/*     */   public void uninstallComponents(JFileChooser paramJFileChooser)
/*     */   {
/* 519 */     paramJFileChooser.removeAll();
/* 520 */     this.bottomPanel = null;
/* 521 */     if (this.filterComboBoxModel != null)
/* 522 */       paramJFileChooser.removePropertyChangeListener(this.filterComboBoxModel);
/*     */   }
/*     */ 
/*     */   protected void installStrings(JFileChooser paramJFileChooser)
/*     */   {
/* 527 */     super.installStrings(paramJFileChooser);
/*     */ 
/* 529 */     Locale localLocale = paramJFileChooser.getLocale();
/*     */ 
/* 531 */     this.enterFolderNameLabelText = UIManager.getString("FileChooser.enterFolderNameLabelText", localLocale);
/* 532 */     this.enterFolderNameLabelMnemonic = UIManager.getInt("FileChooser.enterFolderNameLabelMnemonic");
/* 533 */     this.enterFileNameLabelText = UIManager.getString("FileChooser.enterFileNameLabelText", localLocale);
/* 534 */     this.enterFileNameLabelMnemonic = UIManager.getInt("FileChooser.enterFileNameLabelMnemonic");
/*     */ 
/* 536 */     this.filesLabelText = UIManager.getString("FileChooser.filesLabelText", localLocale);
/* 537 */     this.filesLabelMnemonic = UIManager.getInt("FileChooser.filesLabelMnemonic");
/*     */ 
/* 539 */     this.foldersLabelText = UIManager.getString("FileChooser.foldersLabelText", localLocale);
/* 540 */     this.foldersLabelMnemonic = UIManager.getInt("FileChooser.foldersLabelMnemonic");
/*     */ 
/* 542 */     this.pathLabelText = UIManager.getString("FileChooser.pathLabelText", localLocale);
/* 543 */     this.pathLabelMnemonic = UIManager.getInt("FileChooser.pathLabelMnemonic");
/*     */ 
/* 545 */     this.filterLabelText = UIManager.getString("FileChooser.filterLabelText", localLocale);
/* 546 */     this.filterLabelMnemonic = UIManager.getInt("FileChooser.filterLabelMnemonic");
/*     */   }
/*     */ 
/*     */   protected void installIcons(JFileChooser paramJFileChooser)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void uninstallIcons(JFileChooser paramJFileChooser)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected JScrollPane createFilesList()
/*     */   {
/* 560 */     this.fileList = new JList();
/*     */ 
/* 562 */     if (getFileChooser().isMultiSelectionEnabled())
/* 563 */       this.fileList.setSelectionMode(2);
/*     */     else {
/* 565 */       this.fileList.setSelectionMode(0);
/*     */     }
/*     */ 
/* 568 */     this.fileList.setModel(new MotifFileListModel());
/* 569 */     this.fileList.getSelectionModel().removeSelectionInterval(0, 0);
/* 570 */     this.fileList.setCellRenderer(new FileCellRenderer());
/* 571 */     this.fileList.addListSelectionListener(createListSelectionListener(getFileChooser()));
/* 572 */     this.fileList.addMouseListener(createDoubleClickListener(getFileChooser(), this.fileList));
/* 573 */     this.fileList.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent paramAnonymousMouseEvent) {
/* 575 */         JFileChooser localJFileChooser = MotifFileChooserUI.this.getFileChooser();
/* 576 */         if ((SwingUtilities.isLeftMouseButton(paramAnonymousMouseEvent)) && (!localJFileChooser.isMultiSelectionEnabled())) {
/* 577 */           int i = SwingUtilities2.loc2IndexFileList(MotifFileChooserUI.this.fileList, paramAnonymousMouseEvent.getPoint());
/* 578 */           if (i >= 0) {
/* 579 */             File localFile = (File)MotifFileChooserUI.this.fileList.getModel().getElementAt(i);
/* 580 */             MotifFileChooserUI.this.setFileName(localJFileChooser.getName(localFile));
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/* 585 */     align(this.fileList);
/* 586 */     JScrollPane localJScrollPane = new JScrollPane(this.fileList);
/* 587 */     localJScrollPane.setPreferredSize(prefListSize);
/* 588 */     localJScrollPane.setMaximumSize(MAX_SIZE);
/* 589 */     align(localJScrollPane);
/* 590 */     this.fileList.setInheritsPopupMenu(true);
/* 591 */     localJScrollPane.setInheritsPopupMenu(true);
/* 592 */     return localJScrollPane;
/*     */   }
/*     */ 
/*     */   protected JScrollPane createDirectoryList() {
/* 596 */     this.directoryList = new JList();
/* 597 */     align(this.directoryList);
/*     */ 
/* 599 */     this.directoryList.setCellRenderer(new DirectoryCellRenderer());
/* 600 */     this.directoryList.setModel(new MotifDirectoryListModel());
/* 601 */     this.directoryList.getSelectionModel().removeSelectionInterval(0, 0);
/* 602 */     this.directoryList.addMouseListener(createDoubleClickListener(getFileChooser(), this.directoryList));
/* 603 */     this.directoryList.addListSelectionListener(createListSelectionListener(getFileChooser()));
/* 604 */     this.directoryList.setInheritsPopupMenu(true);
/*     */ 
/* 606 */     JScrollPane localJScrollPane = new JScrollPane(this.directoryList);
/* 607 */     localJScrollPane.setMaximumSize(MAX_SIZE);
/* 608 */     localJScrollPane.setPreferredSize(prefListSize);
/* 609 */     localJScrollPane.setInheritsPopupMenu(true);
/* 610 */     align(localJScrollPane);
/* 611 */     return localJScrollPane;
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize(JComponent paramJComponent) {
/* 615 */     Dimension localDimension1 = getFileChooser().getAccessory() != null ? WITH_ACCELERATOR_PREF_SIZE : PREF_SIZE;
/*     */ 
/* 617 */     Dimension localDimension2 = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
/* 618 */     if (localDimension2 != null) {
/* 619 */       return new Dimension(localDimension2.width < localDimension1.width ? localDimension1.width : localDimension2.width, localDimension2.height < localDimension1.height ? localDimension1.height : localDimension2.height);
/*     */     }
/*     */ 
/* 622 */     return localDimension1;
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize(JComponent paramJComponent)
/*     */   {
/* 627 */     return MIN_SIZE;
/*     */   }
/*     */ 
/*     */   public Dimension getMaximumSize(JComponent paramJComponent) {
/* 631 */     return new Dimension(2147483647, 2147483647);
/*     */   }
/*     */ 
/*     */   protected void align(JComponent paramJComponent) {
/* 635 */     paramJComponent.setAlignmentX(0.0F);
/* 636 */     paramJComponent.setAlignmentY(0.0F);
/*     */   }
/*     */ 
/*     */   protected FilterComboBoxModel createFilterComboBoxModel()
/*     */   {
/* 744 */     return new FilterComboBoxModel();
/*     */   }
/*     */ 
/*     */   protected FilterComboBoxRenderer createFilterComboBoxRenderer()
/*     */   {
/* 751 */     return new FilterComboBoxRenderer();
/*     */   }
/*     */ 
/*     */   protected JButton getApproveButton(JFileChooser paramJFileChooser)
/*     */   {
/* 843 */     return this.approveButton;
/*     */   }
/*     */ 
/*     */   protected class DirectoryCellRenderer extends DefaultListCellRenderer
/*     */   {
/*     */     protected DirectoryCellRenderer()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */     {
/* 654 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/* 655 */       setText(MotifFileChooserUI.this.getFileChooser().getName((File)paramObject));
/* 656 */       setInheritsPopupMenu(true);
/* 657 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class FileCellRenderer extends DefaultListCellRenderer
/*     */   {
/*     */     protected FileCellRenderer()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */     {
/* 643 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/* 644 */       setText(MotifFileChooserUI.this.getFileChooser().getName((File)paramObject));
/* 645 */       setInheritsPopupMenu(true);
/* 646 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class FilterComboBoxModel extends AbstractListModel
/*     */     implements ComboBoxModel, PropertyChangeListener
/*     */   {
/*     */     protected FileFilter[] filters;
/*     */ 
/*     */     protected FilterComboBoxModel()
/*     */     {
/* 780 */       this.filters = MotifFileChooserUI.this.getFileChooser().getChoosableFileFilters();
/*     */     }
/*     */ 
/*     */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 784 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 785 */       if (str.equals("ChoosableFileFilterChangedProperty")) {
/* 786 */         this.filters = ((FileFilter[])paramPropertyChangeEvent.getNewValue());
/* 787 */         fireContentsChanged(this, -1, -1);
/* 788 */       } else if (str.equals("fileFilterChanged")) {
/* 789 */         fireContentsChanged(this, -1, -1);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setSelectedItem(Object paramObject) {
/* 794 */       if (paramObject != null) {
/* 795 */         MotifFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)paramObject);
/* 796 */         fireContentsChanged(this, -1, -1);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object getSelectedItem()
/*     */     {
/* 806 */       FileFilter localFileFilter1 = MotifFileChooserUI.this.getFileChooser().getFileFilter();
/* 807 */       int i = 0;
/* 808 */       if (localFileFilter1 != null) {
/* 809 */         for (FileFilter localFileFilter2 : this.filters) {
/* 810 */           if (localFileFilter2 == localFileFilter1) {
/* 811 */             i = 1;
/*     */           }
/*     */         }
/* 814 */         if (i == 0) {
/* 815 */           MotifFileChooserUI.this.getFileChooser().addChoosableFileFilter(localFileFilter1);
/*     */         }
/*     */       }
/* 818 */       return MotifFileChooserUI.this.getFileChooser().getFileFilter();
/*     */     }
/*     */ 
/*     */     public int getSize() {
/* 822 */       if (this.filters != null) {
/* 823 */         return this.filters.length;
/*     */       }
/* 825 */       return 0;
/*     */     }
/*     */ 
/*     */     public Object getElementAt(int paramInt)
/*     */     {
/* 830 */       if (paramInt > getSize() - 1)
/*     */       {
/* 832 */         return MotifFileChooserUI.this.getFileChooser().getFileFilter();
/*     */       }
/* 834 */       if (this.filters != null) {
/* 835 */         return this.filters[paramInt];
/*     */       }
/* 837 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class FilterComboBoxRenderer extends DefaultListCellRenderer
/*     */   {
/*     */     public FilterComboBoxRenderer()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */     {
/* 763 */       super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
/*     */ 
/* 765 */       if ((paramObject != null) && ((paramObject instanceof FileFilter))) {
/* 766 */         setText(((FileFilter)paramObject).getDescription());
/*     */       }
/*     */ 
/* 769 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class MotifDirectoryListModel extends AbstractListModel
/*     */     implements ListDataListener
/*     */   {
/*     */     public MotifDirectoryListModel()
/*     */     {
/* 663 */       MotifFileChooserUI.this.getModel().addListDataListener(this);
/*     */     }
/*     */ 
/*     */     public int getSize() {
/* 667 */       return MotifFileChooserUI.this.getModel().getDirectories().size();
/*     */     }
/*     */ 
/*     */     public Object getElementAt(int paramInt) {
/* 671 */       return MotifFileChooserUI.this.getModel().getDirectories().elementAt(paramInt);
/*     */     }
/*     */ 
/*     */     public void intervalAdded(ListDataEvent paramListDataEvent) {
/* 675 */       fireIntervalAdded(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*     */     }
/*     */ 
/*     */     public void intervalRemoved(ListDataEvent paramListDataEvent) {
/* 679 */       fireIntervalRemoved(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*     */     }
/*     */ 
/*     */     public void fireContentsChanged()
/*     */     {
/* 686 */       fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getDirectories().size() - 1);
/*     */     }
/*     */ 
/*     */     public void contentsChanged(ListDataEvent paramListDataEvent)
/*     */     {
/* 692 */       fireContentsChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class MotifFileListModel extends AbstractListModel implements ListDataListener
/*     */   {
/*     */     public MotifFileListModel() {
/* 699 */       MotifFileChooserUI.this.getModel().addListDataListener(this);
/*     */     }
/*     */ 
/*     */     public int getSize() {
/* 703 */       return MotifFileChooserUI.this.getModel().getFiles().size();
/*     */     }
/*     */ 
/*     */     public boolean contains(Object paramObject) {
/* 707 */       return MotifFileChooserUI.this.getModel().getFiles().contains(paramObject);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object paramObject) {
/* 711 */       return MotifFileChooserUI.this.getModel().getFiles().indexOf(paramObject);
/*     */     }
/*     */ 
/*     */     public Object getElementAt(int paramInt) {
/* 715 */       return MotifFileChooserUI.this.getModel().getFiles().elementAt(paramInt);
/*     */     }
/*     */ 
/*     */     public void intervalAdded(ListDataEvent paramListDataEvent) {
/* 719 */       fireIntervalAdded(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*     */     }
/*     */ 
/*     */     public void intervalRemoved(ListDataEvent paramListDataEvent) {
/* 723 */       fireIntervalRemoved(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
/*     */     }
/*     */ 
/*     */     public void fireContentsChanged()
/*     */     {
/* 730 */       fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getFiles().size() - 1);
/*     */     }
/*     */ 
/*     */     public void contentsChanged(ListDataEvent paramListDataEvent)
/*     */     {
/* 735 */       fireContentsChanged();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.motif.MotifFileChooserUI
 * JD-Core Version:    0.6.2
 */