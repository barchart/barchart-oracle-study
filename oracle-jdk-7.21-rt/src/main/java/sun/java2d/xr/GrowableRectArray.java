/*    */ package sun.java2d.xr;
/*    */ 
/*    */ public class GrowableRectArray extends GrowableIntArray
/*    */ {
/*    */   private static final int RECT_SIZE = 4;
/*    */ 
/*    */   public GrowableRectArray(int paramInt)
/*    */   {
/* 38 */     super(4, paramInt);
/*    */   }
/*    */ 
/*    */   public final void setX(int paramInt1, int paramInt2) {
/* 42 */     this.array[getCellIndex(paramInt1)] = paramInt2;
/*    */   }
/*    */ 
/*    */   public final void setY(int paramInt1, int paramInt2) {
/* 46 */     this.array[(getCellIndex(paramInt1) + 1)] = paramInt2;
/*    */   }
/*    */ 
/*    */   public final void setWidth(int paramInt1, int paramInt2) {
/* 50 */     this.array[(getCellIndex(paramInt1) + 2)] = paramInt2;
/*    */   }
/*    */ 
/*    */   public final void setHeight(int paramInt1, int paramInt2) {
/* 54 */     this.array[(getCellIndex(paramInt1) + 3)] = paramInt2;
/*    */   }
/*    */ 
/*    */   public final int getX(int paramInt) {
/* 58 */     return this.array[getCellIndex(paramInt)];
/*    */   }
/*    */ 
/*    */   public final int getY(int paramInt) {
/* 62 */     return this.array[(getCellIndex(paramInt) + 1)];
/*    */   }
/*    */ 
/*    */   public final int getWidth(int paramInt) {
/* 66 */     return this.array[(getCellIndex(paramInt) + 2)];
/*    */   }
/*    */ 
/*    */   public final int getHeight(int paramInt) {
/* 70 */     return this.array[(getCellIndex(paramInt) + 3)];
/*    */   }
/*    */ 
/*    */   public final void translateRects(int paramInt1, int paramInt2) {
/* 74 */     for (int i = 0; i < getSize(); i++) {
/* 75 */       setX(i, getX(i) + paramInt1);
/* 76 */       setY(i, getY(i) + paramInt2);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.GrowableRectArray
 * JD-Core Version:    0.6.2
 */