/*     */ package sun.java2d.xr;
/*     */ 
/*     */ public class DirtyRegion
/*     */   implements Cloneable
/*     */ {
/*     */   int x;
/*     */   int y;
/*     */   int x2;
/*     */   int y2;
/*     */ 
/*     */   public DirtyRegion()
/*     */   {
/*  42 */     clear();
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  46 */     this.x = 2147483647;
/*  47 */     this.y = 2147483647;
/*  48 */     this.x2 = -2147483648;
/*  49 */     this.y2 = -2147483648;
/*     */   }
/*     */ 
/*     */   public void growDirtyRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  53 */     this.x = Math.min(paramInt1, this.x);
/*  54 */     this.y = Math.min(paramInt2, this.y);
/*  55 */     this.x2 = Math.max(paramInt3, this.x2);
/*  56 */     this.y2 = Math.max(paramInt4, this.y2);
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/*  60 */     return this.x2 - this.x;
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/*  64 */     return this.y2 - this.y;
/*     */   }
/*     */ 
/*     */   public void growDirtyRegionTileLimit(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  68 */     if (paramInt1 < this.x) {
/*  69 */       this.x = Math.max(paramInt1, 0);
/*     */     }
/*  71 */     if (paramInt2 < this.y) {
/*  72 */       this.y = Math.max(paramInt2, 0);
/*     */     }
/*  74 */     if (paramInt3 > this.x2) {
/*  75 */       this.x2 = Math.min(paramInt3, 256);
/*     */     }
/*  77 */     if (paramInt4 > this.y2)
/*  78 */       this.y2 = Math.min(paramInt4, 256);
/*     */   }
/*     */ 
/*     */   public static DirtyRegion combineRegion(DirtyRegion paramDirtyRegion1, DirtyRegion paramDirtyRegion2)
/*     */   {
/*  84 */     DirtyRegion localDirtyRegion = new DirtyRegion();
/*  85 */     localDirtyRegion.x = Math.min(paramDirtyRegion1.x, paramDirtyRegion2.x);
/*  86 */     localDirtyRegion.y = Math.min(paramDirtyRegion1.y, paramDirtyRegion2.y);
/*  87 */     localDirtyRegion.x2 = Math.max(paramDirtyRegion1.x2, paramDirtyRegion2.x2);
/*  88 */     localDirtyRegion.y2 = Math.max(paramDirtyRegion1.y2, paramDirtyRegion2.y2);
/*  89 */     return localDirtyRegion;
/*     */   }
/*     */ 
/*     */   public void setDirtyLineRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  93 */     if (paramInt1 < paramInt3) {
/*  94 */       this.x = paramInt1;
/*  95 */       this.x2 = paramInt3;
/*     */     } else {
/*  97 */       this.x = paramInt3;
/*  98 */       this.x2 = paramInt1;
/*     */     }
/*     */ 
/* 101 */     if (paramInt2 < paramInt4) {
/* 102 */       this.y = paramInt2;
/* 103 */       this.y2 = paramInt4;
/*     */     } else {
/* 105 */       this.y = paramInt4;
/* 106 */       this.y2 = paramInt2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void translate(int paramInt1, int paramInt2) {
/* 111 */     if (this.x != 2147483647) {
/* 112 */       this.x += paramInt1;
/* 113 */       this.x2 += paramInt1;
/* 114 */       this.y += paramInt2;
/* 115 */       this.y2 += paramInt2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 120 */     return getClass().getName() + "(x: " + this.x + ", y:" + this.y + ", x2:" + this.x2 + ", y2:" + this.y2 + ")";
/*     */   }
/*     */ 
/*     */   public DirtyRegion cloneRegion()
/*     */   {
/*     */     try {
/* 126 */       return (DirtyRegion)clone();
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 128 */       localCloneNotSupportedException.printStackTrace();
/*     */     }
/*     */ 
/* 131 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.DirtyRegion
 * JD-Core Version:    0.6.2
 */