/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ class XVerticalScrollbar extends XScrollbar
/*    */ {
/*    */   public XVerticalScrollbar(XScrollbarClient paramXScrollbarClient)
/*    */   {
/* 35 */     super(1, paramXScrollbarClient);
/*    */   }
/*    */ 
/*    */   public void setSize(int paramInt1, int paramInt2) {
/* 39 */     super.setSize(paramInt1, paramInt2);
/* 40 */     this.barWidth = paramInt1;
/* 41 */     this.barLength = paramInt2;
/* 42 */     calculateArrowWidth();
/* 43 */     rebuildArrows();
/*    */   }
/*    */ 
/*    */   protected void rebuildArrows() {
/* 47 */     this.firstArrow = createArrowShape(true, true);
/* 48 */     this.secondArrow = createArrowShape(true, false);
/*    */   }
/*    */ 
/*    */   boolean beforeThumb(int paramInt1, int paramInt2) {
/* 52 */     Rectangle localRectangle = calculateThumbRect();
/* 53 */     return paramInt2 < localRectangle.y;
/*    */   }
/*    */ 
/*    */   protected Rectangle getThumbArea() {
/* 57 */     return new Rectangle(2, getArrowAreaWidth(), this.width - 4, this.height - 2 * getArrowAreaWidth());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XVerticalScrollbar
 * JD-Core Version:    0.6.2
 */