/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ class XHorizontalScrollbar extends XScrollbar
/*    */ {
/*    */   public XHorizontalScrollbar(XScrollbarClient paramXScrollbarClient)
/*    */   {
/* 37 */     super(2, paramXScrollbarClient);
/*    */   }
/*    */ 
/*    */   public void setSize(int paramInt1, int paramInt2) {
/* 41 */     super.setSize(paramInt1, paramInt2);
/* 42 */     this.barWidth = paramInt2;
/* 43 */     this.barLength = paramInt1;
/* 44 */     calculateArrowWidth();
/* 45 */     rebuildArrows();
/*    */   }
/*    */   protected void rebuildArrows() {
/* 48 */     this.firstArrow = createArrowShape(false, true);
/* 49 */     this.secondArrow = createArrowShape(false, false);
/*    */   }
/*    */ 
/*    */   boolean beforeThumb(int paramInt1, int paramInt2) {
/* 53 */     Rectangle localRectangle = calculateThumbRect();
/* 54 */     return paramInt1 < localRectangle.x;
/*    */   }
/*    */ 
/*    */   protected Rectangle getThumbArea() {
/* 58 */     return new Rectangle(getArrowAreaWidth(), 2, this.width - 2 * getArrowAreaWidth(), this.height - 4);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XHorizontalScrollbar
 * JD-Core Version:    0.6.2
 */