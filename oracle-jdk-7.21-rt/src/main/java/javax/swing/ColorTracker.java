/*     */ package javax.swing;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ class ColorTracker
/*     */   implements ActionListener, Serializable
/*     */ {
/*     */   JColorChooser chooser;
/*     */   Color color;
/*     */ 
/*     */   public ColorTracker(JColorChooser paramJColorChooser)
/*     */   {
/* 749 */     this.chooser = paramJColorChooser;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 753 */     this.color = this.chooser.getColor();
/*     */   }
/*     */ 
/*     */   public Color getColor() {
/* 757 */     return this.color;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.ColorTracker
 * JD-Core Version:    0.6.2
 */