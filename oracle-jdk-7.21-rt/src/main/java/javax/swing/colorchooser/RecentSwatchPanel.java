/*     */ package javax.swing.colorchooser;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import javax.swing.UIManager;
/*     */ 
/*     */ class RecentSwatchPanel extends SwatchPanel
/*     */ {
/*     */   protected void initValues()
/*     */   {
/* 322 */     this.swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize", getLocale());
/* 323 */     this.numSwatches = new Dimension(5, 7);
/* 324 */     this.gap = new Dimension(1, 1);
/*     */   }
/*     */ 
/*     */   protected void initColors()
/*     */   {
/* 329 */     Color localColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor", getLocale());
/* 330 */     int i = this.numSwatches.width * this.numSwatches.height;
/*     */ 
/* 332 */     this.colors = new Color[i];
/* 333 */     for (int j = 0; j < i; j++)
/* 334 */       this.colors[j] = localColor;
/*     */   }
/*     */ 
/*     */   public void setMostRecentColor(Color paramColor)
/*     */   {
/* 340 */     System.arraycopy(this.colors, 0, this.colors, 1, this.colors.length - 1);
/* 341 */     this.colors[0] = paramColor;
/* 342 */     repaint();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.colorchooser.RecentSwatchPanel
 * JD-Core Version:    0.6.2
 */