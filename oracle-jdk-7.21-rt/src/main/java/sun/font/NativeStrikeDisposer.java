/*    */ package sun.font;
/*    */ 
/*    */ class NativeStrikeDisposer extends FontStrikeDisposer
/*    */ {
/*    */   long pNativeScalerContext;
/*    */ 
/*    */   public NativeStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong, int[] paramArrayOfInt)
/*    */   {
/* 56 */     super(paramFont2D, paramFontStrikeDesc, 0L, paramArrayOfInt);
/* 57 */     this.pNativeScalerContext = paramLong;
/*    */   }
/*    */ 
/*    */   public NativeStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong, long[] paramArrayOfLong)
/*    */   {
/* 62 */     super(paramFont2D, paramFontStrikeDesc, 0L, paramArrayOfLong);
/* 63 */     this.pNativeScalerContext = paramLong;
/*    */   }
/*    */ 
/*    */   public NativeStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc, long paramLong)
/*    */   {
/* 68 */     super(paramFont2D, paramFontStrikeDesc, 0L);
/* 69 */     this.pNativeScalerContext = paramLong;
/*    */   }
/*    */ 
/*    */   public NativeStrikeDisposer(Font2D paramFont2D, FontStrikeDesc paramFontStrikeDesc) {
/* 73 */     super(paramFont2D, paramFontStrikeDesc);
/*    */   }
/*    */ 
/*    */   public synchronized void dispose() {
/* 77 */     if (!this.disposed) {
/* 78 */       if (this.pNativeScalerContext != 0L) {
/* 79 */         freeNativeScalerContext(this.pNativeScalerContext);
/*    */       }
/* 81 */       super.dispose();
/*    */     }
/*    */   }
/*    */ 
/*    */   private native void freeNativeScalerContext(long paramLong);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.NativeStrikeDisposer
 * JD-Core Version:    0.6.2
 */