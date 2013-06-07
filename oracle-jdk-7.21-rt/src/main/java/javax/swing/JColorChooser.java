/*     */ package javax.swing;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import javax.accessibility.Accessible;
/*     */ import javax.accessibility.AccessibleContext;
/*     */ import javax.accessibility.AccessibleRole;
/*     */ import javax.swing.colorchooser.AbstractColorChooserPanel;
/*     */ import javax.swing.colorchooser.ColorChooserComponentFactory;
/*     */ import javax.swing.colorchooser.ColorSelectionModel;
/*     */ import javax.swing.colorchooser.DefaultColorSelectionModel;
/*     */ import javax.swing.plaf.ColorChooserUI;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ 
/*     */ public class JColorChooser extends JComponent
/*     */   implements Accessible
/*     */ {
/*     */   private static final String uiClassID = "ColorChooserUI";
/*     */   private ColorSelectionModel selectionModel;
/*  96 */   private JComponent previewPanel = ColorChooserComponentFactory.getPreviewPanel();
/*     */ 
/*  98 */   private AbstractColorChooserPanel[] chooserPanels = new AbstractColorChooserPanel[0];
/*     */   private boolean dragEnabled;
/*     */   public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
/*     */   public static final String PREVIEW_PANEL_PROPERTY = "previewPanel";
/*     */   public static final String CHOOSER_PANELS_PROPERTY = "chooserPanels";
/* 560 */   protected AccessibleContext accessibleContext = null;
/*     */ 
/*     */   public static Color showDialog(Component paramComponent, String paramString, Color paramColor)
/*     */     throws HeadlessException
/*     */   {
/* 137 */     JColorChooser localJColorChooser = new JColorChooser(paramColor != null ? paramColor : Color.white);
/*     */ 
/* 140 */     ColorTracker localColorTracker = new ColorTracker(localJColorChooser);
/* 141 */     JDialog localJDialog = createDialog(paramComponent, paramString, true, localJColorChooser, localColorTracker, null);
/*     */ 
/* 143 */     localJDialog.addComponentListener(new ColorChooserDialog.DisposeOnClose());
/*     */ 
/* 145 */     localJDialog.show();
/*     */ 
/* 147 */     return localColorTracker.getColor();
/*     */   }
/*     */ 
/*     */   public static JDialog createDialog(Component paramComponent, String paramString, boolean paramBoolean, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2)
/*     */     throws HeadlessException
/*     */   {
/* 176 */     Window localWindow = JOptionPane.getWindowForComponent(paramComponent);
/*     */     ColorChooserDialog localColorChooserDialog;
/* 178 */     if ((localWindow instanceof Frame)) {
/* 179 */       localColorChooserDialog = new ColorChooserDialog((Frame)localWindow, paramString, paramBoolean, paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
/*     */     }
/*     */     else {
/* 182 */       localColorChooserDialog = new ColorChooserDialog((Dialog)localWindow, paramString, paramBoolean, paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
/*     */     }
/*     */ 
/* 185 */     return localColorChooserDialog;
/*     */   }
/*     */ 
/*     */   public JColorChooser()
/*     */   {
/* 192 */     this(Color.white);
/*     */   }
/*     */ 
/*     */   public JColorChooser(Color paramColor)
/*     */   {
/* 201 */     this(new DefaultColorSelectionModel(paramColor));
/*     */   }
/*     */ 
/*     */   public JColorChooser(ColorSelectionModel paramColorSelectionModel)
/*     */   {
/* 212 */     this.selectionModel = paramColorSelectionModel;
/* 213 */     updateUI();
/* 214 */     this.dragEnabled = false;
/*     */   }
/*     */ 
/*     */   public ColorChooserUI getUI()
/*     */   {
/* 224 */     return (ColorChooserUI)this.ui;
/*     */   }
/*     */ 
/*     */   public void setUI(ColorChooserUI paramColorChooserUI)
/*     */   {
/* 239 */     super.setUI(paramColorChooserUI);
/*     */   }
/*     */ 
/*     */   public void updateUI()
/*     */   {
/* 250 */     setUI((ColorChooserUI)UIManager.getUI(this));
/*     */   }
/*     */ 
/*     */   public String getUIClassID()
/*     */   {
/* 261 */     return "ColorChooserUI";
/*     */   }
/*     */ 
/*     */   public Color getColor()
/*     */   {
/* 271 */     return this.selectionModel.getSelectedColor();
/*     */   }
/*     */ 
/*     */   public void setColor(Color paramColor)
/*     */   {
/* 286 */     this.selectionModel.setSelectedColor(paramColor);
/*     */   }
/*     */ 
/*     */   public void setColor(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 302 */     setColor(new Color(paramInt1, paramInt2, paramInt3));
/*     */   }
/*     */ 
/*     */   public void setColor(int paramInt)
/*     */   {
/* 315 */     setColor(paramInt >> 16 & 0xFF, paramInt >> 8 & 0xFF, paramInt & 0xFF);
/*     */   }
/*     */ 
/*     */   public void setDragEnabled(boolean paramBoolean)
/*     */   {
/* 358 */     if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
/* 359 */       throw new HeadlessException();
/*     */     }
/* 361 */     this.dragEnabled = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean getDragEnabled()
/*     */   {
/* 372 */     return this.dragEnabled;
/*     */   }
/*     */ 
/*     */   public void setPreviewPanel(JComponent paramJComponent)
/*     */   {
/* 390 */     if (this.previewPanel != paramJComponent) {
/* 391 */       JComponent localJComponent = this.previewPanel;
/* 392 */       this.previewPanel = paramJComponent;
/* 393 */       firePropertyChange("previewPanel", localJComponent, paramJComponent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public JComponent getPreviewPanel()
/*     */   {
/* 403 */     return this.previewPanel;
/*     */   }
/*     */ 
/*     */   public void addChooserPanel(AbstractColorChooserPanel paramAbstractColorChooserPanel)
/*     */   {
/* 412 */     AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel1 = getChooserPanels();
/* 413 */     AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel2 = new AbstractColorChooserPanel[arrayOfAbstractColorChooserPanel1.length + 1];
/* 414 */     System.arraycopy(arrayOfAbstractColorChooserPanel1, 0, arrayOfAbstractColorChooserPanel2, 0, arrayOfAbstractColorChooserPanel1.length);
/* 415 */     arrayOfAbstractColorChooserPanel2[(arrayOfAbstractColorChooserPanel2.length - 1)] = paramAbstractColorChooserPanel;
/* 416 */     setChooserPanels(arrayOfAbstractColorChooserPanel2);
/*     */   }
/*     */ 
/*     */   public AbstractColorChooserPanel removeChooserPanel(AbstractColorChooserPanel paramAbstractColorChooserPanel)
/*     */   {
/* 430 */     int i = -1;
/*     */ 
/* 432 */     for (int j = 0; j < this.chooserPanels.length; j++) {
/* 433 */       if (this.chooserPanels[j] == paramAbstractColorChooserPanel) {
/* 434 */         i = j;
/* 435 */         break;
/*     */       }
/*     */     }
/* 438 */     if (i == -1) {
/* 439 */       throw new IllegalArgumentException("chooser panel not in this chooser");
/*     */     }
/*     */ 
/* 442 */     AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = new AbstractColorChooserPanel[this.chooserPanels.length - 1];
/*     */ 
/* 444 */     if (i == this.chooserPanels.length - 1) {
/* 445 */       System.arraycopy(this.chooserPanels, 0, arrayOfAbstractColorChooserPanel, 0, arrayOfAbstractColorChooserPanel.length);
/*     */     }
/* 447 */     else if (i == 0) {
/* 448 */       System.arraycopy(this.chooserPanels, 1, arrayOfAbstractColorChooserPanel, 0, arrayOfAbstractColorChooserPanel.length);
/*     */     }
/*     */     else {
/* 451 */       System.arraycopy(this.chooserPanels, 0, arrayOfAbstractColorChooserPanel, 0, i);
/* 452 */       System.arraycopy(this.chooserPanels, i + 1, arrayOfAbstractColorChooserPanel, i, this.chooserPanels.length - i - 1);
/*     */     }
/*     */ 
/* 456 */     setChooserPanels(arrayOfAbstractColorChooserPanel);
/*     */ 
/* 458 */     return paramAbstractColorChooserPanel;
/*     */   }
/*     */ 
/*     */   public void setChooserPanels(AbstractColorChooserPanel[] paramArrayOfAbstractColorChooserPanel)
/*     */   {
/* 474 */     AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = this.chooserPanels;
/* 475 */     this.chooserPanels = paramArrayOfAbstractColorChooserPanel;
/* 476 */     firePropertyChange("chooserPanels", arrayOfAbstractColorChooserPanel, paramArrayOfAbstractColorChooserPanel);
/*     */   }
/*     */ 
/*     */   public AbstractColorChooserPanel[] getChooserPanels()
/*     */   {
/* 485 */     return this.chooserPanels;
/*     */   }
/*     */ 
/*     */   public ColorSelectionModel getSelectionModel()
/*     */   {
/* 494 */     return this.selectionModel;
/*     */   }
/*     */ 
/*     */   public void setSelectionModel(ColorSelectionModel paramColorSelectionModel)
/*     */   {
/* 509 */     ColorSelectionModel localColorSelectionModel = this.selectionModel;
/* 510 */     this.selectionModel = paramColorSelectionModel;
/* 511 */     firePropertyChange("selectionModel", localColorSelectionModel, paramColorSelectionModel);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 521 */     paramObjectOutputStream.defaultWriteObject();
/* 522 */     if (getUIClassID().equals("ColorChooserUI")) {
/* 523 */       byte b = JComponent.getWriteObjCounter(this);
/* 524 */       b = (byte)(b - 1); JComponent.setWriteObjCounter(this, b);
/* 525 */       if ((b == 0) && (this.ui != null))
/* 526 */         this.ui.installUI(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String paramString()
/*     */   {
/* 543 */     StringBuffer localStringBuffer = new StringBuffer("");
/* 544 */     for (int i = 0; i < this.chooserPanels.length; i++) {
/* 545 */       localStringBuffer.append("[" + this.chooserPanels[i].toString() + "]");
/*     */     }
/*     */ 
/* 548 */     String str = this.previewPanel != null ? this.previewPanel.toString() : "";
/*     */ 
/* 551 */     return super.paramString() + ",chooserPanels=" + localStringBuffer.toString() + ",previewPanel=" + str;
/*     */   }
/*     */ 
/*     */   public AccessibleContext getAccessibleContext()
/*     */   {
/* 572 */     if (this.accessibleContext == null) {
/* 573 */       this.accessibleContext = new AccessibleJColorChooser();
/*     */     }
/* 575 */     return this.accessibleContext;
/*     */   }
/*     */ 
/*     */   protected class AccessibleJColorChooser extends JComponent.AccessibleJComponent
/*     */   {
/*     */     protected AccessibleJColorChooser()
/*     */     {
/* 584 */       super();
/*     */     }
/*     */ 
/*     */     public AccessibleRole getAccessibleRole()
/*     */     {
/* 594 */       return AccessibleRole.COLOR_CHOOSER;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.JColorChooser
 * JD-Core Version:    0.6.2
 */