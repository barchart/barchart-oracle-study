/*     */ package javax.swing.plaf.basic;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Container;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.JColorChooser;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.LookAndFeel;
/*     */ import javax.swing.TransferHandler;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.colorchooser.AbstractColorChooserPanel;
/*     */ import javax.swing.colorchooser.ColorChooserComponentFactory;
/*     */ import javax.swing.colorchooser.ColorSelectionModel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.plaf.ColorChooserUI;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.UIResource;
/*     */ import sun.swing.DefaultLookup;
/*     */ 
/*     */ public class BasicColorChooserUI extends ColorChooserUI
/*     */ {
/*     */   protected JColorChooser chooser;
/*     */   JTabbedPane tabbedPane;
/*     */   JPanel singlePanel;
/*     */   JPanel previewPanelHolder;
/*     */   JComponent previewPanel;
/*     */   boolean isMultiPanel;
/*  62 */   private static TransferHandler defaultTransferHandler = new ColorTransferHandler();
/*     */   protected AbstractColorChooserPanel[] defaultChoosers;
/*     */   protected ChangeListener previewListener;
/*     */   protected PropertyChangeListener propertyChangeListener;
/*     */   private Handler handler;
/*     */ 
/*     */   public BasicColorChooserUI()
/*     */   {
/*  61 */     this.isMultiPanel = false;
/*     */   }
/*     */ 
/*     */   public static ComponentUI createUI(JComponent paramJComponent)
/*     */   {
/*  71 */     return new BasicColorChooserUI();
/*     */   }
/*     */ 
/*     */   protected AbstractColorChooserPanel[] createDefaultChoosers() {
/*  75 */     AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = ColorChooserComponentFactory.getDefaultChooserPanels();
/*  76 */     return arrayOfAbstractColorChooserPanel;
/*     */   }
/*     */ 
/*     */   protected void uninstallDefaultChoosers() {
/*  80 */     AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = this.chooser.getChooserPanels();
/*  81 */     for (int i = 0; i < arrayOfAbstractColorChooserPanel.length; i++)
/*  82 */       this.chooser.removeChooserPanel(arrayOfAbstractColorChooserPanel[i]);
/*     */   }
/*     */ 
/*     */   public void installUI(JComponent paramJComponent)
/*     */   {
/*  87 */     this.chooser = ((JColorChooser)paramJComponent);
/*     */ 
/*  89 */     super.installUI(paramJComponent);
/*     */ 
/*  91 */     installDefaults();
/*  92 */     installListeners();
/*     */ 
/*  94 */     this.tabbedPane = new JTabbedPane();
/*  95 */     this.tabbedPane.setName("ColorChooser.tabPane");
/*  96 */     this.tabbedPane.setInheritsPopupMenu(true);
/*  97 */     this.singlePanel = new JPanel(new CenterLayout());
/*  98 */     this.singlePanel.setName("ColorChooser.panel");
/*  99 */     this.singlePanel.setInheritsPopupMenu(true);
/*     */ 
/* 101 */     this.chooser.setLayout(new BorderLayout());
/*     */ 
/* 103 */     this.defaultChoosers = createDefaultChoosers();
/* 104 */     this.chooser.setChooserPanels(this.defaultChoosers);
/*     */ 
/* 106 */     this.previewPanelHolder = new JPanel(new CenterLayout());
/* 107 */     this.previewPanelHolder.setName("ColorChooser.previewPanelHolder");
/*     */ 
/* 109 */     if (DefaultLookup.getBoolean(this.chooser, this, "ColorChooser.showPreviewPanelText", true))
/*     */     {
/* 111 */       String str = UIManager.getString("ColorChooser.previewText", this.chooser.getLocale());
/*     */ 
/* 113 */       this.previewPanelHolder.setBorder(new TitledBorder(str));
/*     */     }
/* 115 */     this.previewPanelHolder.setInheritsPopupMenu(true);
/*     */ 
/* 117 */     installPreviewPanel();
/* 118 */     this.chooser.applyComponentOrientation(paramJComponent.getComponentOrientation());
/*     */   }
/*     */ 
/*     */   public void uninstallUI(JComponent paramJComponent) {
/* 122 */     this.chooser.remove(this.tabbedPane);
/* 123 */     this.chooser.remove(this.singlePanel);
/* 124 */     this.chooser.remove(this.previewPanelHolder);
/*     */ 
/* 126 */     uninstallDefaultChoosers();
/* 127 */     uninstallListeners();
/* 128 */     uninstallPreviewPanel();
/* 129 */     uninstallDefaults();
/*     */ 
/* 131 */     this.previewPanelHolder = null;
/* 132 */     this.previewPanel = null;
/* 133 */     this.defaultChoosers = null;
/* 134 */     this.chooser = null;
/* 135 */     this.tabbedPane = null;
/*     */ 
/* 137 */     this.handler = null;
/*     */   }
/*     */ 
/*     */   protected void installPreviewPanel() {
/* 141 */     JComponent localJComponent = this.chooser.getPreviewPanel();
/* 142 */     if (localJComponent == null) {
/* 143 */       localJComponent = ColorChooserComponentFactory.getPreviewPanel();
/*     */     }
/* 145 */     else if ((JPanel.class.equals(localJComponent.getClass())) && (0 == localJComponent.getComponentCount())) {
/* 146 */       localJComponent = null;
/*     */     }
/* 148 */     this.previewPanel = localJComponent;
/* 149 */     if (localJComponent != null) {
/* 150 */       this.chooser.add(this.previewPanelHolder, "South");
/* 151 */       localJComponent.setForeground(this.chooser.getColor());
/* 152 */       this.previewPanelHolder.add(localJComponent);
/* 153 */       localJComponent.addMouseListener(getHandler());
/* 154 */       localJComponent.setInheritsPopupMenu(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void uninstallPreviewPanel()
/*     */   {
/* 164 */     if (this.previewPanel != null) {
/* 165 */       this.previewPanel.removeMouseListener(getHandler());
/* 166 */       this.previewPanelHolder.remove(this.previewPanel);
/*     */     }
/* 168 */     this.chooser.remove(this.previewPanelHolder);
/*     */   }
/*     */ 
/*     */   protected void installDefaults() {
/* 172 */     LookAndFeel.installColorsAndFont(this.chooser, "ColorChooser.background", "ColorChooser.foreground", "ColorChooser.font");
/*     */ 
/* 175 */     LookAndFeel.installProperty(this.chooser, "opaque", Boolean.TRUE);
/* 176 */     TransferHandler localTransferHandler = this.chooser.getTransferHandler();
/* 177 */     if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource)))
/* 178 */       this.chooser.setTransferHandler(defaultTransferHandler);
/*     */   }
/*     */ 
/*     */   protected void uninstallDefaults()
/*     */   {
/* 183 */     if ((this.chooser.getTransferHandler() instanceof UIResource))
/* 184 */       this.chooser.setTransferHandler(null);
/*     */   }
/*     */ 
/*     */   protected void installListeners()
/*     */   {
/* 190 */     this.propertyChangeListener = createPropertyChangeListener();
/* 191 */     this.chooser.addPropertyChangeListener(this.propertyChangeListener);
/*     */ 
/* 193 */     this.previewListener = getHandler();
/* 194 */     this.chooser.getSelectionModel().addChangeListener(this.previewListener);
/*     */   }
/*     */ 
/*     */   private Handler getHandler() {
/* 198 */     if (this.handler == null) {
/* 199 */       this.handler = new Handler(null);
/*     */     }
/* 201 */     return this.handler;
/*     */   }
/*     */ 
/*     */   protected PropertyChangeListener createPropertyChangeListener() {
/* 205 */     return getHandler();
/*     */   }
/*     */ 
/*     */   protected void uninstallListeners() {
/* 209 */     this.chooser.removePropertyChangeListener(this.propertyChangeListener);
/* 210 */     this.chooser.getSelectionModel().removeChangeListener(this.previewListener);
/* 211 */     this.previewListener = null;
/*     */   }
/*     */ 
/*     */   private void selectionChanged(ColorSelectionModel paramColorSelectionModel) {
/* 215 */     JComponent localJComponent = this.chooser.getPreviewPanel();
/* 216 */     if (localJComponent != null) {
/* 217 */       localJComponent.setForeground(paramColorSelectionModel.getSelectedColor());
/* 218 */       localJComponent.repaint();
/*     */     }
/* 220 */     AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel1 = this.chooser.getChooserPanels();
/* 221 */     if (arrayOfAbstractColorChooserPanel1 != null)
/* 222 */       for (AbstractColorChooserPanel localAbstractColorChooserPanel : arrayOfAbstractColorChooserPanel1)
/* 223 */         if (localAbstractColorChooserPanel != null)
/* 224 */           localAbstractColorChooserPanel.updateChooser();
/*     */   }
/*     */ 
/*     */   static class ColorTransferHandler extends TransferHandler
/*     */     implements UIResource
/*     */   {
/*     */     ColorTransferHandler()
/*     */     {
/* 350 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Handler
/*     */     implements ChangeListener, MouseListener, PropertyChangeListener
/*     */   {
/*     */     private Handler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void stateChanged(ChangeEvent paramChangeEvent)
/*     */     {
/* 236 */       BasicColorChooserUI.this.selectionChanged((ColorSelectionModel)paramChangeEvent.getSource());
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent paramMouseEvent)
/*     */     {
/* 242 */       if (BasicColorChooserUI.this.chooser.getDragEnabled()) {
/* 243 */         TransferHandler localTransferHandler = BasicColorChooserUI.this.chooser.getTransferHandler();
/* 244 */         localTransferHandler.exportAsDrag(BasicColorChooserUI.this.chooser, paramMouseEvent, 1);
/*     */       }
/*     */     }
/*     */     public void mouseReleased(MouseEvent paramMouseEvent) {
/*     */     }
/*     */     public void mouseClicked(MouseEvent paramMouseEvent) {
/*     */     }
/*     */     public void mouseEntered(MouseEvent paramMouseEvent) {
/*     */     }
/*     */     public void mouseExited(MouseEvent paramMouseEvent) {
/*     */     }
/*     */ 
/* 256 */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { String str1 = paramPropertyChangeEvent.getPropertyName();
/*     */       Object localObject1;
/*     */       Object localObject2;
/* 258 */       if (str1 == "chooserPanels") {
/* 259 */         localObject1 = (AbstractColorChooserPanel[])paramPropertyChangeEvent.getOldValue();
/*     */ 
/* 261 */         localObject2 = (AbstractColorChooserPanel[])paramPropertyChangeEvent.getNewValue();
/*     */         Object localObject3;
/*     */         Object localObject4;
/* 264 */         for (int i = 0; i < localObject1.length; i++) {
/* 265 */           localObject3 = localObject1[i].getParent();
/* 266 */           if (localObject3 != null) {
/* 267 */             localObject4 = ((Container)localObject3).getParent();
/* 268 */             if (localObject4 != null)
/* 269 */               ((Container)localObject4).remove((Component)localObject3);
/* 270 */             localObject1[i].uninstallChooserPanel(BasicColorChooserUI.this.chooser);
/*     */           }
/*     */         }
/*     */ 
/* 274 */         i = localObject2.length;
/* 275 */         if (i == 0) {
/* 276 */           BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
/* 277 */           return;
/*     */         }
/* 279 */         if (i == 1) {
/* 280 */           BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
/* 281 */           localObject3 = new JPanel(new CenterLayout());
/* 282 */           ((JPanel)localObject3).setInheritsPopupMenu(true);
/* 283 */           ((JPanel)localObject3).add(localObject2[0]);
/* 284 */           BasicColorChooserUI.this.singlePanel.add((Component)localObject3, "Center");
/* 285 */           BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.singlePanel);
/*     */         }
/*     */         else {
/* 288 */           if (localObject1.length < 2) {
/* 289 */             BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.singlePanel);
/* 290 */             BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.tabbedPane, "Center");
/*     */           }
/*     */ 
/* 293 */           for (j = 0; j < localObject2.length; j++) {
/* 294 */             localObject4 = new JPanel(new CenterLayout());
/* 295 */             ((JPanel)localObject4).setInheritsPopupMenu(true);
/* 296 */             String str2 = localObject2[j].getDisplayName();
/* 297 */             int k = localObject2[j].getMnemonic();
/* 298 */             ((JPanel)localObject4).add(localObject2[j]);
/* 299 */             BasicColorChooserUI.this.tabbedPane.addTab(str2, (Component)localObject4);
/* 300 */             if (k > 0) {
/* 301 */               BasicColorChooserUI.this.tabbedPane.setMnemonicAt(j, k);
/* 302 */               int m = localObject2[j].getDisplayedMnemonicIndex();
/* 303 */               if (m >= 0) {
/* 304 */                 BasicColorChooserUI.this.tabbedPane.setDisplayedMnemonicIndexAt(j, m);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 309 */         BasicColorChooserUI.this.chooser.applyComponentOrientation(BasicColorChooserUI.this.chooser.getComponentOrientation());
/* 310 */         for (int j = 0; j < localObject2.length; j++) {
/* 311 */           localObject2[j].installChooserPanel(BasicColorChooserUI.this.chooser);
/*     */         }
/*     */       }
/* 314 */       else if (str1 == "previewPanel") {
/* 315 */         BasicColorChooserUI.this.uninstallPreviewPanel();
/* 316 */         BasicColorChooserUI.this.installPreviewPanel();
/*     */       }
/* 318 */       else if (str1 == "selectionModel") {
/* 319 */         localObject1 = (ColorSelectionModel)paramPropertyChangeEvent.getOldValue();
/* 320 */         ((ColorSelectionModel)localObject1).removeChangeListener(BasicColorChooserUI.this.previewListener);
/* 321 */         localObject2 = (ColorSelectionModel)paramPropertyChangeEvent.getNewValue();
/* 322 */         ((ColorSelectionModel)localObject2).addChangeListener(BasicColorChooserUI.this.previewListener);
/* 323 */         BasicColorChooserUI.this.selectionChanged((ColorSelectionModel)localObject2);
/*     */       }
/* 325 */       else if (str1 == "componentOrientation") {
/* 326 */         localObject1 = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
/*     */ 
/* 328 */         localObject2 = (JColorChooser)paramPropertyChangeEvent.getSource();
/* 329 */         if (localObject1 != (ComponentOrientation)paramPropertyChangeEvent.getOldValue()) {
/* 330 */           ((JColorChooser)localObject2).applyComponentOrientation((ComponentOrientation)localObject1);
/* 331 */           ((JColorChooser)localObject2).updateUI();
/*     */         }
/*     */       } }
/*     */   }
/*     */ 
/*     */   public class PropertyHandler implements PropertyChangeListener
/*     */   {
/*     */     public PropertyHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 343 */       BasicColorChooserUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.basic.BasicColorChooserUI
 * JD-Core Version:    0.6.2
 */