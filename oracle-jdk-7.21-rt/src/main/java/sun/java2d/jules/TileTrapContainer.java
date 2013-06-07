/*     */ package sun.java2d.jules;
/*     */ 
/*     */ import sun.java2d.xr.GrowableIntArray;
/*     */ 
/*     */ class TileTrapContainer
/*     */ {
/*     */   int tileAlpha;
/*     */   GrowableIntArray traps;
/*     */ 
/*     */   public TileTrapContainer(GrowableIntArray paramGrowableIntArray)
/*     */   {
/* 335 */     this.traps = paramGrowableIntArray;
/*     */   }
/*     */ 
/*     */   public void setTileAlpha(int paramInt) {
/* 339 */     this.tileAlpha = paramInt;
/*     */   }
/*     */ 
/*     */   public int getTileAlpha() {
/* 343 */     return this.tileAlpha;
/*     */   }
/*     */ 
/*     */   public GrowableIntArray getTraps() {
/* 347 */     return this.traps;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.jules.TileTrapContainer
 * JD-Core Version:    0.6.2
 */