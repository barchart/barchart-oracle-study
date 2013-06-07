/*     */ package javax.swing.colorchooser;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ class SwatchPanel extends JPanel
/*     */ {
/*     */   protected Color[] colors;
/*     */   protected Dimension swatchSize;
/*     */   protected Dimension numSwatches;
/*     */   protected Dimension gap;
/*     */ 
/*     */   public SwatchPanel()
/*     */   {
/* 243 */     initValues();
/* 244 */     initColors();
/* 245 */     setToolTipText("");
/* 246 */     setOpaque(true);
/* 247 */     setBackground(Color.white);
/* 248 */     setRequestFocusEnabled(false);
/* 249 */     setInheritsPopupMenu(true);
/*     */   }
/*     */ 
/*     */   public boolean isFocusTraversable() {
/* 253 */     return false;
/*     */   }
/*     */ 
/*     */   protected void initValues()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics paramGraphics) {
/* 261 */     paramGraphics.setColor(getBackground());
/* 262 */     paramGraphics.fillRect(0, 0, getWidth(), getHeight());
/* 263 */     for (int i = 0; i < this.numSwatches.height; i++) {
/* 264 */       int j = i * (this.swatchSize.height + this.gap.height);
/* 265 */       for (int k = 0; k < this.numSwatches.width; k++)
/*     */       {
/* 267 */         paramGraphics.setColor(getColorForCell(k, i));
/*     */         int m;
/* 269 */         if ((!getComponentOrientation().isLeftToRight()) && ((this instanceof RecentSwatchPanel)))
/*     */         {
/* 271 */           m = (this.numSwatches.width - k - 1) * (this.swatchSize.width + this.gap.width);
/*     */         }
/* 273 */         else m = k * (this.swatchSize.width + this.gap.width);
/*     */ 
/* 275 */         paramGraphics.fillRect(m, j, this.swatchSize.width, this.swatchSize.height);
/* 276 */         paramGraphics.setColor(Color.black);
/* 277 */         paramGraphics.drawLine(m + this.swatchSize.width - 1, j, m + this.swatchSize.width - 1, j + this.swatchSize.height - 1);
/* 278 */         paramGraphics.drawLine(m, j + this.swatchSize.height - 1, m + this.swatchSize.width - 1, j + this.swatchSize.height - 1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 284 */     int i = this.numSwatches.width * (this.swatchSize.width + this.gap.width) - 1;
/* 285 */     int j = this.numSwatches.height * (this.swatchSize.height + this.gap.height) - 1;
/* 286 */     return new Dimension(i, j);
/*     */   }
/*     */ 
/*     */   protected void initColors()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getToolTipText(MouseEvent paramMouseEvent)
/*     */   {
/* 295 */     Color localColor = getColorForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
/* 296 */     return localColor.getRed() + ", " + localColor.getGreen() + ", " + localColor.getBlue();
/*     */   }
/*     */ 
/*     */   public Color getColorForLocation(int paramInt1, int paramInt2)
/*     */   {
/*     */     int i;
/* 301 */     if ((!getComponentOrientation().isLeftToRight()) && ((this instanceof RecentSwatchPanel)))
/*     */     {
/* 303 */       i = this.numSwatches.width - paramInt1 / (this.swatchSize.width + this.gap.width) - 1;
/*     */     }
/* 305 */     else i = paramInt1 / (this.swatchSize.width + this.gap.width);
/*     */ 
/* 307 */     int j = paramInt2 / (this.swatchSize.height + this.gap.height);
/* 308 */     return getColorForCell(i, j);
/*     */   }
/*     */ 
/*     */   private Color getColorForCell(int paramInt1, int paramInt2) {
/* 312 */     return this.colors[(paramInt2 * this.numSwatches.width + paramInt1)];
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.colorchooser.SwatchPanel
 * JD-Core Version:    0.6.2
 */