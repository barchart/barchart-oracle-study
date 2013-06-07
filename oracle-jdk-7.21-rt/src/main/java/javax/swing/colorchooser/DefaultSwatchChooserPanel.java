/*     */ package javax.swing.colorchooser;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.io.Serializable;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JColorChooser;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.CompoundBorder;
/*     */ import javax.swing.border.LineBorder;
/*     */ 
/*     */ class DefaultSwatchChooserPanel extends AbstractColorChooserPanel
/*     */ {
/*     */   SwatchPanel swatchPanel;
/*     */   RecentSwatchPanel recentSwatchPanel;
/*     */   MouseListener mainSwatchListener;
/*     */   MouseListener recentSwatchListener;
/*     */ 
/*     */   public DefaultSwatchChooserPanel()
/*     */   {
/*  63 */     setInheritsPopupMenu(true);
/*     */   }
/*     */ 
/*     */   public String getDisplayName() {
/*  67 */     return UIManager.getString("ColorChooser.swatchesNameText", getLocale());
/*     */   }
/*     */ 
/*     */   public int getMnemonic()
/*     */   {
/*  90 */     return getInt("ColorChooser.swatchesMnemonic", -1);
/*     */   }
/*     */ 
/*     */   public int getDisplayedMnemonicIndex()
/*     */   {
/* 118 */     return getInt("ColorChooser.swatchesDisplayedMnemonicIndex", -1);
/*     */   }
/*     */ 
/*     */   public Icon getSmallDisplayIcon() {
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   public Icon getLargeDisplayIcon() {
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public void installChooserPanel(JColorChooser paramJColorChooser)
/*     */   {
/* 134 */     super.installChooserPanel(paramJColorChooser);
/*     */   }
/*     */ 
/*     */   protected void buildChooser()
/*     */   {
/* 139 */     String str = UIManager.getString("ColorChooser.swatchesRecentText", getLocale());
/*     */ 
/* 141 */     GridBagLayout localGridBagLayout = new GridBagLayout();
/* 142 */     GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/* 143 */     JPanel localJPanel1 = new JPanel(localGridBagLayout);
/*     */ 
/* 145 */     this.swatchPanel = new MainSwatchPanel();
/* 146 */     this.swatchPanel.putClientProperty("AccessibleName", getDisplayName());
/*     */ 
/* 148 */     this.swatchPanel.setInheritsPopupMenu(true);
/*     */ 
/* 150 */     this.recentSwatchPanel = new RecentSwatchPanel();
/* 151 */     this.recentSwatchPanel.putClientProperty("AccessibleName", str);
/*     */ 
/* 154 */     this.mainSwatchListener = new MainSwatchListener();
/* 155 */     this.swatchPanel.addMouseListener(this.mainSwatchListener);
/* 156 */     this.recentSwatchListener = new RecentSwatchListener();
/* 157 */     this.recentSwatchPanel.addMouseListener(this.recentSwatchListener);
/*     */ 
/* 159 */     JPanel localJPanel2 = new JPanel(new BorderLayout());
/* 160 */     CompoundBorder localCompoundBorder = new CompoundBorder(new LineBorder(Color.black), new LineBorder(Color.white));
/*     */ 
/* 162 */     localJPanel2.setBorder(localCompoundBorder);
/* 163 */     localJPanel2.add(this.swatchPanel, "Center");
/*     */ 
/* 165 */     localGridBagConstraints.anchor = 25;
/* 166 */     localGridBagConstraints.gridwidth = 1;
/* 167 */     localGridBagConstraints.gridheight = 2;
/* 168 */     Insets localInsets = localGridBagConstraints.insets;
/* 169 */     localGridBagConstraints.insets = new Insets(0, 0, 0, 10);
/* 170 */     localJPanel1.add(localJPanel2, localGridBagConstraints);
/* 171 */     localGridBagConstraints.insets = localInsets;
/*     */ 
/* 173 */     this.recentSwatchPanel.setInheritsPopupMenu(true);
/* 174 */     JPanel localJPanel3 = new JPanel(new BorderLayout());
/* 175 */     localJPanel3.setBorder(localCompoundBorder);
/* 176 */     localJPanel3.setInheritsPopupMenu(true);
/* 177 */     localJPanel3.add(this.recentSwatchPanel, "Center");
/*     */ 
/* 179 */     JLabel localJLabel = new JLabel(str);
/* 180 */     localJLabel.setLabelFor(this.recentSwatchPanel);
/*     */ 
/* 182 */     localGridBagConstraints.gridwidth = 0;
/* 183 */     localGridBagConstraints.gridheight = 1;
/* 184 */     localGridBagConstraints.weighty = 1.0D;
/* 185 */     localJPanel1.add(localJLabel, localGridBagConstraints);
/*     */ 
/* 187 */     localGridBagConstraints.weighty = 0.0D;
/* 188 */     localGridBagConstraints.gridheight = 0;
/* 189 */     localGridBagConstraints.insets = new Insets(0, 0, 0, 2);
/* 190 */     localJPanel1.add(localJPanel3, localGridBagConstraints);
/* 191 */     localJPanel1.setInheritsPopupMenu(true);
/*     */ 
/* 193 */     add(localJPanel1);
/*     */   }
/*     */ 
/*     */   public void uninstallChooserPanel(JColorChooser paramJColorChooser) {
/* 197 */     super.uninstallChooserPanel(paramJColorChooser);
/* 198 */     this.swatchPanel.removeMouseListener(this.mainSwatchListener);
/* 199 */     this.recentSwatchPanel.removeMouseListener(this.recentSwatchListener);
/* 200 */     this.swatchPanel = null;
/* 201 */     this.recentSwatchPanel = null;
/* 202 */     this.mainSwatchListener = null;
/* 203 */     this.recentSwatchListener = null;
/* 204 */     removeAll();
/*     */   }
/*     */ 
/*     */   public void updateChooser()
/*     */   {
/*     */   }
/*     */ 
/*     */   class MainSwatchListener extends MouseAdapter
/*     */     implements Serializable
/*     */   {
/*     */     MainSwatchListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent paramMouseEvent)
/*     */     {
/* 223 */       if (DefaultSwatchChooserPanel.this.isEnabled()) {
/* 224 */         Color localColor = DefaultSwatchChooserPanel.this.swatchPanel.getColorForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
/* 225 */         DefaultSwatchChooserPanel.this.setSelectedColor(localColor);
/* 226 */         DefaultSwatchChooserPanel.this.recentSwatchPanel.setMostRecentColor(localColor);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class RecentSwatchListener extends MouseAdapter
/*     */     implements Serializable
/*     */   {
/*     */     RecentSwatchListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent paramMouseEvent)
/*     */     {
/* 214 */       if (DefaultSwatchChooserPanel.this.isEnabled()) {
/* 215 */         Color localColor = DefaultSwatchChooserPanel.this.recentSwatchPanel.getColorForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
/* 216 */         DefaultSwatchChooserPanel.this.setSelectedColor(localColor);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.colorchooser.DefaultSwatchChooserPanel
 * JD-Core Version:    0.6.2
 */