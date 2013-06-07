/*    */ package sun.java2d.xr;
/*    */ 
/*    */ public class GrowablePointArray extends GrowableIntArray
/*    */ {
/*    */   private static final int POINT_SIZE = 2;
/*    */ 
/*    */   public GrowablePointArray(int paramInt)
/*    */   {
/* 40 */     super(2, paramInt);
/*    */   }
/*    */ 
/*    */   public final int getX(int paramInt)
/*    */   {
/* 45 */     return this.array[getCellIndex(paramInt)];
/*    */   }
/*    */ 
/*    */   public final int getY(int paramInt)
/*    */   {
/* 50 */     return this.array[(getCellIndex(paramInt) + 1)];
/*    */   }
/*    */ 
/*    */   public final void setX(int paramInt1, int paramInt2)
/*    */   {
/* 55 */     this.array[getCellIndex(paramInt1)] = paramInt2;
/*    */   }
/*    */ 
/*    */   public final void setY(int paramInt1, int paramInt2)
/*    */   {
/* 60 */     this.array[(getCellIndex(paramInt1) + 1)] = paramInt2;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.GrowablePointArray
 * JD-Core Version:    0.6.2
 */