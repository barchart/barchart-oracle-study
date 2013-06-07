/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ class Separator extends Canvas
/*     */ {
/*     */   public static final int HORIZONTAL = 0;
/*     */   public static final int VERTICAL = 1;
/*     */   int orientation;
/*     */ 
/*     */   public Separator(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 853 */     this.orientation = paramInt3;
/* 854 */     if (paramInt3 == 0) {
/* 855 */       resize(paramInt1, paramInt2);
/*     */     }
/*     */     else
/* 858 */       resize(paramInt2, paramInt1);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/* 864 */     Rectangle localRectangle = bounds();
/* 865 */     Color localColor1 = getBackground();
/* 866 */     Color localColor2 = localColor1.brighter();
/* 867 */     Color localColor3 = localColor1.darker();
/*     */     int i;
/*     */     int k;
/*     */     int m;
/*     */     int j;
/* 869 */     if (this.orientation == 0) {
/* 870 */       i = 0;
/* 871 */       k = localRectangle.width - 1;
/* 872 */       j = m = localRectangle.height / 2 - 1;
/*     */     }
/*     */     else
/*     */     {
/* 876 */       i = k = localRectangle.width / 2 - 1;
/* 877 */       j = 0;
/* 878 */       m = localRectangle.height - 1;
/*     */     }
/* 880 */     paramGraphics.setColor(localColor3);
/* 881 */     paramGraphics.drawLine(i, m, k, m);
/* 882 */     paramGraphics.setColor(localColor2);
/* 883 */     if (this.orientation == 0)
/* 884 */       paramGraphics.drawLine(i, m + 1, k, m + 1);
/*     */     else
/* 886 */       paramGraphics.drawLine(i + 1, m, k + 1, m);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.Separator
 * JD-Core Version:    0.6.2
 */