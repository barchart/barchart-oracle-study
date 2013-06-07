/*     */ package sun.swing.plaf.synth;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.File;
/*     */ import java.util.Vector;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.border.AbstractBorder;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.event.ListDataEvent;
/*     */ import javax.swing.event.ListDataListener;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import javax.swing.plaf.basic.BasicDirectoryModel;
/*     */ import javax.swing.plaf.basic.BasicFileChooserUI;
/*     */ import javax.swing.plaf.synth.ColorType;
/*     */ import javax.swing.plaf.synth.Region;
/*     */ import javax.swing.plaf.synth.SynthContext;
/*     */ import javax.swing.plaf.synth.SynthLookAndFeel;
/*     */ import javax.swing.plaf.synth.SynthPainter;
/*     */ import javax.swing.plaf.synth.SynthStyle;
/*     */ import javax.swing.plaf.synth.SynthStyleFactory;
/*     */ import javax.swing.plaf.synth.SynthUI;
/*     */ 
/*     */ public abstract class SynthFileChooserUI extends BasicFileChooserUI
/*     */   implements SynthUI
/*     */ {
/*     */   private JButton approveButton;
/*     */   private JButton cancelButton;
/*     */   private SynthStyle style;
/*  65 */   private Action fileNameCompletionAction = new FileNameCompletionAction();
/*     */ 
/*  67 */   private FileFilter actualFileFilter = null;
/*  68 */   private GlobFilter globFilter = null;
/*     */   private String fileNameCompletionString;
/*     */ 
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/*  71 */     return new SynthFileChooserUIImpl((JFileChooser)paramJComponent);
/*     */   }
/*     */ 
/*     */   public SynthFileChooserUI(JFileChooser paramJFileChooser) {
/*  75 */     super(paramJFileChooser);
/*     */   }
/*     */ 
/*     */   public SynthContext getContext(JComponent paramJComponent) {
/*  79 */     return new SynthContext(paramJComponent, Region.FILE_CHOOSER, this.style, getComponentState(paramJComponent));
/*     */   }
/*     */ 
/*     */   protected SynthContext getContext(JComponent paramJComponent, int paramInt)
/*     */   {
/*  84 */     Region localRegion = SynthLookAndFeel.getRegion(paramJComponent);
/*  85 */     return new SynthContext(paramJComponent, Region.FILE_CHOOSER, this.style, paramInt);
/*     */   }
/*     */ 
/*     */   private Region getRegion(JComponent paramJComponent) {
/*  89 */     return SynthLookAndFeel.getRegion(paramJComponent);
/*     */   }
/*     */ 
/*     */   private int getComponentState(JComponent paramJComponent) {
/*  93 */     if (paramJComponent.isEnabled()) {
/*  94 */       if (paramJComponent.isFocusOwner()) {
/*  95 */         return 257;
/*     */       }
/*  97 */       return 1;
/*     */     }
/*  99 */     return 8;
/*     */   }
/*     */ 
/*     */   private void updateStyle(JComponent paramJComponent) {
/* 103 */     SynthStyle localSynthStyle = SynthLookAndFeel.getStyleFactory().getStyle(paramJComponent, Region.FILE_CHOOSER);
/*     */ 
/* 105 */     if (localSynthStyle != this.style) {
/* 106 */       if (this.style != null) {
/* 107 */         this.style.uninstallDefaults(getContext(paramJComponent, 1));
/*     */       }
/* 109 */       this.style = localSynthStyle;
/* 110 */       SynthContext localSynthContext = getContext(paramJComponent, 1);
/* 111 */       this.style.installDefaults(localSynthContext);
/* 112 */       Border localBorder = paramJComponent.getBorder();
/* 113 */       if ((localBorder == null) || ((localBorder instanceof UIResource))) {
/* 114 */         paramJComponent.setBorder(new UIBorder(this.style.getInsets(localSynthContext, null)));
/*     */       }
/*     */ 
/* 117 */       this.directoryIcon = this.style.getIcon(localSynthContext, "FileView.directoryIcon");
/* 118 */       this.fileIcon = this.style.getIcon(localSynthContext, "FileView.fileIcon");
/* 119 */       this.computerIcon = this.style.getIcon(localSynthContext, "FileView.computerIcon");
/* 120 */       this.hardDriveIcon = this.style.getIcon(localSynthContext, "FileView.hardDriveIcon");
/* 121 */       this.floppyDriveIcon = this.style.getIcon(localSynthContext, "FileView.floppyDriveIcon");
/*     */ 
/* 123 */       this.newFolderIcon = this.style.getIcon(localSynthContext, "FileChooser.newFolderIcon");
/* 124 */       this.upFolderIcon = this.style.getIcon(localSynthContext, "FileChooser.upFolderIcon");
/* 125 */       this.homeFolderIcon = this.style.getIcon(localSynthContext, "FileChooser.homeFolderIcon");
/* 126 */       this.detailsViewIcon = this.style.getIcon(localSynthContext, "FileChooser.detailsViewIcon");
/* 127 */       this.listViewIcon = this.style.getIcon(localSynthContext, "FileChooser.listViewIcon");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void installUI(JComponent paramJComponent) {
/* 132 */     super.installUI(paramJComponent);
/* 133 */     SwingUtilities.replaceUIActionMap(paramJComponent, createActionMap());
/*     */   }
/*     */ 
/*     */   public void installComponents(JFileChooser paramJFileChooser) {
/* 137 */     SynthContext localSynthContext = getContext(paramJFileChooser, 1);
/*     */ 
/* 139 */     this.cancelButton = new JButton(this.cancelButtonText);
/* 140 */     this.cancelButton.setName("SynthFileChooser.cancelButton");
/* 141 */     this.cancelButton.setIcon(localSynthContext.getStyle().getIcon(localSynthContext, "FileChooser.cancelIcon"));
/* 142 */     this.cancelButton.setMnemonic(this.cancelButtonMnemonic);
/* 143 */     this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
/* 144 */     this.cancelButton.addActionListener(getCancelSelectionAction());
/*     */ 
/* 146 */     this.approveButton = new JButton(getApproveButtonText(paramJFileChooser));
/* 147 */     this.approveButton.setName("SynthFileChooser.approveButton");
/* 148 */     this.approveButton.setIcon(localSynthContext.getStyle().getIcon(localSynthContext, "FileChooser.okIcon"));
/* 149 */     this.approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
/* 150 */     this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
/* 151 */     this.approveButton.addActionListener(getApproveSelectionAction());
/*     */   }
/*     */ 
/*     */   public void uninstallComponents(JFileChooser paramJFileChooser)
/*     */   {
/* 156 */     paramJFileChooser.removeAll();
/*     */   }
/*     */ 
/*     */   protected void installListeners(JFileChooser paramJFileChooser) {
/* 160 */     super.installListeners(paramJFileChooser);
/*     */ 
/* 162 */     getModel().addListDataListener(new ListDataListener()
/*     */     {
/*     */       public void contentsChanged(ListDataEvent paramAnonymousListDataEvent) {
/* 165 */         new SynthFileChooserUI.DelayedSelectionUpdater(SynthFileChooserUI.this);
/*     */       }
/*     */       public void intervalAdded(ListDataEvent paramAnonymousListDataEvent) {
/* 168 */         new SynthFileChooserUI.DelayedSelectionUpdater(SynthFileChooserUI.this);
/*     */       }
/*     */ 
/*     */       public void intervalRemoved(ListDataEvent paramAnonymousListDataEvent)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected abstract ActionMap createActionMap();
/*     */ 
/*     */   protected void installDefaults(JFileChooser paramJFileChooser)
/*     */   {
/* 190 */     super.installDefaults(paramJFileChooser);
/* 191 */     updateStyle(paramJFileChooser);
/*     */   }
/*     */ 
/*     */   protected void uninstallDefaults(JFileChooser paramJFileChooser) {
/* 195 */     super.uninstallDefaults(paramJFileChooser);
/*     */ 
/* 197 */     SynthContext localSynthContext = getContext(getFileChooser(), 1);
/* 198 */     this.style.uninstallDefaults(localSynthContext);
/* 199 */     this.style = null;
/*     */   }
/*     */ 
/*     */   protected void installIcons(JFileChooser paramJFileChooser)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void update(Graphics paramGraphics, JComponent paramJComponent) {
/* 207 */     SynthContext localSynthContext = getContext(paramJComponent);
/*     */ 
/* 209 */     if (paramJComponent.isOpaque()) {
/* 210 */       paramGraphics.setColor(this.style.getColor(localSynthContext, ColorType.BACKGROUND));
/* 211 */       paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
/*     */     }
/*     */ 
/* 214 */     this.style.getPainter(localSynthContext).paintFileChooserBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
/*     */ 
/* 216 */     paint(localSynthContext, paramGraphics);
/*     */   }
/*     */ 
/*     */   public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics, JComponent paramJComponent) {
/* 223 */     SynthContext localSynthContext = getContext(paramJComponent);
/*     */ 
/* 225 */     paint(localSynthContext, paramGraphics);
/*     */   }
/*     */   protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
/*     */   }
/*     */ 
/*     */   public abstract void setFileName(String paramString);
/*     */ 
/*     */   public abstract String getFileName();
/*     */ 
/*     */   protected void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*     */   }
/*     */ 
/*     */   protected void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*     */   }
/*     */ 
/*     */   protected void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*     */   }
/*     */ 
/*     */   protected void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*     */   }
/*     */ 
/*     */   protected void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*     */   }
/*     */ 
/*     */   protected void doMultiSelectionChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/* 250 */     if (!getFileChooser().isMultiSelectionEnabled())
/* 251 */       getFileChooser().setSelectedFiles(null);
/*     */   }
/*     */ 
/*     */   protected void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*     */   {
/* 256 */     if (getFileChooser().getControlButtonsAreShown()) {
/* 257 */       this.approveButton.setText(getApproveButtonText(getFileChooser()));
/* 258 */       this.approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doAncestorChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*     */   }
/*     */ 
/*     */   public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser) {
/* 266 */     return new SynthFCPropertyChangeListener(null);
/*     */   }
/*     */ 
/*     */   private void updateFileNameCompletion()
/*     */   {
/* 348 */     if ((this.fileNameCompletionString != null) && 
/* 349 */       (this.fileNameCompletionString.equals(getFileName()))) {
/* 350 */       File[] arrayOfFile = (File[])getModel().getFiles().toArray(new File[0]);
/* 351 */       String str = getCommonStartString(arrayOfFile);
/* 352 */       if ((str != null) && (str.startsWith(this.fileNameCompletionString))) {
/* 353 */         setFileName(str);
/*     */       }
/* 355 */       this.fileNameCompletionString = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getCommonStartString(File[] paramArrayOfFile)
/*     */   {
/* 361 */     Object localObject = null;
/* 362 */     String str1 = null;
/* 363 */     int i = 0;
/* 364 */     if (paramArrayOfFile.length == 0)
/* 365 */       return null;
/*     */     while (true)
/*     */     {
/* 368 */       for (int j = 0; j < paramArrayOfFile.length; j++) {
/* 369 */         String str2 = paramArrayOfFile[j].getName();
/* 370 */         if (j == 0) {
/* 371 */           if (str2.length() == i) {
/* 372 */             return localObject;
/*     */           }
/* 374 */           str1 = str2.substring(0, i + 1);
/*     */         }
/* 376 */         if (!str2.startsWith(str1)) {
/* 377 */           return localObject;
/*     */         }
/*     */       }
/* 380 */       localObject = str1;
/* 381 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resetGlobFilter() {
/* 386 */     if (this.actualFileFilter != null) {
/* 387 */       JFileChooser localJFileChooser = getFileChooser();
/* 388 */       FileFilter localFileFilter = localJFileChooser.getFileFilter();
/* 389 */       if ((localFileFilter != null) && (localFileFilter.equals(this.globFilter))) {
/* 390 */         localJFileChooser.setFileFilter(this.actualFileFilter);
/* 391 */         localJFileChooser.removeChoosableFileFilter(this.globFilter);
/*     */       }
/* 393 */       this.actualFileFilter = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isGlobPattern(String paramString) {
/* 398 */     return ((File.separatorChar == '\\') && (paramString.indexOf('*') >= 0)) || ((File.separatorChar == '/') && ((paramString.indexOf('*') >= 0) || (paramString.indexOf('?') >= 0) || (paramString.indexOf('[') >= 0)));
/*     */   }
/*     */ 
/*     */   public Action getFileNameCompletionAction()
/*     */   {
/* 523 */     return this.fileNameCompletionAction;
/*     */   }
/*     */ 
/*     */   protected JButton getApproveButton(JFileChooser paramJFileChooser)
/*     */   {
/* 528 */     return this.approveButton;
/*     */   }
/*     */ 
/*     */   protected JButton getCancelButton(JFileChooser paramJFileChooser) {
/* 532 */     return this.cancelButton;
/*     */   }
/*     */ 
/*     */   public void clearIconCache()
/*     */   {
/*     */   }
/*     */ 
/*     */   private class DelayedSelectionUpdater
/*     */     implements Runnable
/*     */   {
/*     */     DelayedSelectionUpdater()
/*     */     {
/* 178 */       SwingUtilities.invokeLater(this);
/*     */     }
/*     */ 
/*     */     public void run() {
/* 182 */       SynthFileChooserUI.this.updateFileNameCompletion();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FileNameCompletionAction extends AbstractAction
/*     */   {
/*     */     protected FileNameCompletionAction()
/*     */     {
/* 307 */       super();
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 311 */       JFileChooser localJFileChooser = SynthFileChooserUI.this.getFileChooser();
/*     */ 
/* 313 */       String str = SynthFileChooserUI.this.getFileName();
/*     */ 
/* 315 */       if (str != null)
/*     */       {
/* 317 */         str = str.trim();
/*     */       }
/*     */ 
/* 320 */       SynthFileChooserUI.this.resetGlobFilter();
/*     */ 
/* 322 */       if ((str == null) || (str.equals("")) || ((localJFileChooser.isMultiSelectionEnabled()) && (str.startsWith("\""))))
/*     */       {
/* 324 */         return;
/*     */       }
/*     */ 
/* 327 */       FileFilter localFileFilter = localJFileChooser.getFileFilter();
/* 328 */       if (SynthFileChooserUI.this.globFilter == null)
/* 329 */         SynthFileChooserUI.this.globFilter = new SynthFileChooserUI.GlobFilter(SynthFileChooserUI.this);
/*     */       try
/*     */       {
/* 332 */         SynthFileChooserUI.this.globFilter.setPattern(!SynthFileChooserUI.isGlobPattern(str) ? str + "*" : str);
/* 333 */         if (!(localFileFilter instanceof SynthFileChooserUI.GlobFilter)) {
/* 334 */           SynthFileChooserUI.this.actualFileFilter = localFileFilter;
/*     */         }
/* 336 */         localJFileChooser.setFileFilter(null);
/* 337 */         localJFileChooser.setFileFilter(SynthFileChooserUI.this.globFilter);
/* 338 */         SynthFileChooserUI.this.fileNameCompletionString = str;
/*     */       }
/*     */       catch (PatternSyntaxException localPatternSyntaxException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class GlobFilter extends FileFilter
/*     */   {
/*     */     Pattern pattern;
/*     */     String globPattern;
/*     */ 
/*     */     GlobFilter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setPattern(String paramString)
/*     */     {
/* 413 */       char[] arrayOfChar1 = paramString.toCharArray();
/* 414 */       char[] arrayOfChar2 = new char[arrayOfChar1.length * 2];
/* 415 */       int i = File.separatorChar == '\\' ? 1 : 0;
/* 416 */       int j = 0;
/* 417 */       int k = 0;
/*     */ 
/* 419 */       this.globPattern = paramString;
/*     */       int m;
/* 421 */       if (i != 0)
/*     */       {
/* 423 */         m = arrayOfChar1.length;
/* 424 */         if (paramString.endsWith("*.*")) {
/* 425 */           m -= 2;
/*     */         }
/* 427 */         for (int n = 0; n < m; n++) {
/* 428 */           if (arrayOfChar1[n] == '*') {
/* 429 */             arrayOfChar2[(k++)] = '.';
/*     */           }
/* 431 */           arrayOfChar2[(k++)] = arrayOfChar1[n];
/*     */         }
/*     */       } else {
/* 434 */         for (m = 0; m < arrayOfChar1.length; m++) {
/* 435 */           switch (arrayOfChar1[m]) {
/*     */           case '*':
/* 437 */             if (j == 0) {
/* 438 */               arrayOfChar2[(k++)] = '.';
/*     */             }
/* 440 */             arrayOfChar2[(k++)] = '*';
/* 441 */             break;
/*     */           case '?':
/* 444 */             arrayOfChar2[(k++)] = (j != 0 ? 63 : '.');
/* 445 */             break;
/*     */           case '[':
/* 448 */             j = 1;
/* 449 */             arrayOfChar2[(k++)] = arrayOfChar1[m];
/*     */ 
/* 451 */             if (m < arrayOfChar1.length - 1)
/* 452 */               switch (arrayOfChar1[(m + 1)]) {
/*     */               case '!':
/*     */               case '^':
/* 455 */                 arrayOfChar2[(k++)] = '^';
/* 456 */                 m++;
/* 457 */                 break;
/*     */               case ']':
/* 460 */                 arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
/*     */               }
/* 461 */             break;
/*     */           case ']':
/* 467 */             arrayOfChar2[(k++)] = arrayOfChar1[m];
/* 468 */             j = 0;
/* 469 */             break;
/*     */           case '\\':
/* 472 */             if ((m == 0) && (arrayOfChar1.length > 1) && (arrayOfChar1[1] == '~')) {
/* 473 */               arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
/*     */             } else {
/* 475 */               arrayOfChar2[(k++)] = '\\';
/* 476 */               if ((m < arrayOfChar1.length - 1) && ("*?[]".indexOf(arrayOfChar1[(m + 1)]) >= 0))
/* 477 */                 arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
/*     */               else {
/* 479 */                 arrayOfChar2[(k++)] = '\\';
/*     */               }
/*     */             }
/* 482 */             break;
/*     */           default:
/* 486 */             if (!Character.isLetterOrDigit(arrayOfChar1[m])) {
/* 487 */               arrayOfChar2[(k++)] = '\\';
/*     */             }
/* 489 */             arrayOfChar2[(k++)] = arrayOfChar1[m];
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 494 */       this.pattern = Pattern.compile(new String(arrayOfChar2, 0, k), 2);
/*     */     }
/*     */ 
/*     */     public boolean accept(File paramFile) {
/* 498 */       if (paramFile == null) {
/* 499 */         return false;
/*     */       }
/* 501 */       if (paramFile.isDirectory()) {
/* 502 */         return true;
/*     */       }
/* 504 */       return this.pattern.matcher(paramFile.getName()).matches();
/*     */     }
/*     */ 
/*     */     public String getDescription() {
/* 508 */       return this.globPattern;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class SynthFCPropertyChangeListener
/*     */     implements PropertyChangeListener
/*     */   {
/*     */     private SynthFCPropertyChangeListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*     */     {
/* 271 */       String str = paramPropertyChangeEvent.getPropertyName();
/* 272 */       if (str.equals("fileSelectionChanged")) {
/* 273 */         SynthFileChooserUI.this.doFileSelectionModeChanged(paramPropertyChangeEvent);
/* 274 */       } else if (str.equals("SelectedFileChangedProperty")) {
/* 275 */         SynthFileChooserUI.this.doSelectedFileChanged(paramPropertyChangeEvent);
/* 276 */       } else if (str.equals("SelectedFilesChangedProperty")) {
/* 277 */         SynthFileChooserUI.this.doSelectedFilesChanged(paramPropertyChangeEvent);
/* 278 */       } else if (str.equals("directoryChanged")) {
/* 279 */         SynthFileChooserUI.this.doDirectoryChanged(paramPropertyChangeEvent);
/* 280 */       } else if (str == "MultiSelectionEnabledChangedProperty") {
/* 281 */         SynthFileChooserUI.this.doMultiSelectionChanged(paramPropertyChangeEvent);
/* 282 */       } else if (str == "AccessoryChangedProperty") {
/* 283 */         SynthFileChooserUI.this.doAccessoryChanged(paramPropertyChangeEvent);
/* 284 */       } else if ((str == "ApproveButtonTextChangedProperty") || (str == "ApproveButtonToolTipTextChangedProperty") || (str == "DialogTypeChangedProperty") || (str == "ControlButtonsAreShownChangedProperty"))
/*     */       {
/* 288 */         SynthFileChooserUI.this.doControlButtonsChanged(paramPropertyChangeEvent);
/* 289 */       } else if (str.equals("componentOrientation")) {
/* 290 */         ComponentOrientation localComponentOrientation = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
/* 291 */         JFileChooser localJFileChooser = (JFileChooser)paramPropertyChangeEvent.getSource();
/* 292 */         if (localComponentOrientation != (ComponentOrientation)paramPropertyChangeEvent.getOldValue())
/* 293 */           localJFileChooser.applyComponentOrientation(localComponentOrientation);
/*     */       }
/* 295 */       else if (str.equals("ancestor")) {
/* 296 */         SynthFileChooserUI.this.doAncestorChanged(paramPropertyChangeEvent);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class UIBorder extends AbstractBorder
/*     */     implements UIResource
/*     */   {
/*     */     private Insets _insets;
/*     */ 
/*     */     UIBorder(Insets arg2)
/*     */     {
/*     */       Object localObject;
/* 543 */       if (localObject != null) {
/* 544 */         this._insets = new Insets(localObject.top, localObject.left, localObject.bottom, localObject.right);
/*     */       }
/*     */       else
/*     */       {
/* 548 */         this._insets = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 554 */       if (!(paramComponent instanceof JComponent)) {
/* 555 */         return;
/*     */       }
/* 557 */       JComponent localJComponent = (JComponent)paramComponent;
/* 558 */       SynthContext localSynthContext = SynthFileChooserUI.this.getContext(localJComponent);
/* 559 */       SynthStyle localSynthStyle = localSynthContext.getStyle();
/* 560 */       if (localSynthStyle != null)
/* 561 */         localSynthStyle.getPainter(localSynthContext).paintFileChooserBorder(localSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
/*     */     {
/* 567 */       if (paramInsets == null) {
/* 568 */         paramInsets = new Insets(0, 0, 0, 0);
/*     */       }
/* 570 */       if (this._insets != null) {
/* 571 */         paramInsets.top = this._insets.top;
/* 572 */         paramInsets.bottom = this._insets.bottom;
/* 573 */         paramInsets.left = this._insets.left;
/* 574 */         paramInsets.right = this._insets.right;
/*     */       }
/*     */       else {
/* 577 */         paramInsets.top = (paramInsets.bottom = paramInsets.right = paramInsets.left = 0);
/*     */       }
/* 579 */       return paramInsets;
/*     */     }
/*     */     public boolean isBorderOpaque() {
/* 582 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.swing.plaf.synth.SynthFileChooserUI
 * JD-Core Version:    0.6.2
 */