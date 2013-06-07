/*     */ package sun.java2d.xr;
/*     */ 
/*     */ public class MaskTile
/*     */ {
/*     */   GrowableRectArray rects;
/*     */   DirtyRegion dirtyArea;
/*     */ 
/*     */   public MaskTile()
/*     */   {
/*  40 */     this.rects = new GrowableRectArray(128);
/*  41 */     this.dirtyArea = new DirtyRegion();
/*     */   }
/*     */ 
/*     */   public void addRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  45 */     int i = this.rects.getNextIndex();
/*  46 */     this.rects.setX(i, paramInt1);
/*  47 */     this.rects.setY(i, paramInt2);
/*  48 */     this.rects.setWidth(i, paramInt3);
/*  49 */     this.rects.setHeight(i, paramInt4);
/*     */   }
/*     */ 
/*     */   public void addLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  58 */     DirtyRegion localDirtyRegion = new DirtyRegion();
/*  59 */     localDirtyRegion.setDirtyLineRegion(paramInt1, paramInt2, paramInt3, paramInt4);
/*  60 */     int i = localDirtyRegion.x2 - localDirtyRegion.x;
/*  61 */     int j = localDirtyRegion.y2 - localDirtyRegion.y;
/*     */ 
/*  63 */     if ((i == 0) || (j == 0)) {
/*  64 */       addRect(localDirtyRegion.x, localDirtyRegion.y, localDirtyRegion.x2 - localDirtyRegion.x + 1, localDirtyRegion.y2 - localDirtyRegion.y + 1);
/*     */     }
/*  66 */     else if ((i == 1) && (j == 1)) {
/*  67 */       addRect(paramInt1, paramInt2, 1, 1);
/*  68 */       addRect(paramInt3, paramInt4, 1, 1);
/*     */     } else {
/*  70 */       lineToRects(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void lineToRects(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  78 */     int m = paramInt3 - paramInt1;
/*  79 */     int n = paramInt4 - paramInt2;
/*     */ 
/*  82 */     int i1 = m < 0 ? -1 : m > 0 ? 1 : 0;
/*  83 */     int i2 = n < 0 ? -1 : n > 0 ? 1 : 0;
/*  84 */     if (m < 0)
/*  85 */       m = -m;
/*  86 */     if (n < 0)
/*  87 */       n = -n;
/*     */     int i3;
/*     */     int i4;
/*     */     int i5;
/*     */     int i6;
/*     */     int i7;
/*     */     int i8;
/*  90 */     if (m > n)
/*     */     {
/*  92 */       i3 = i1;
/*  93 */       i4 = 0;
/*  94 */       i5 = i1;
/*  95 */       i6 = i2;
/*  96 */       i7 = n;
/*  97 */       i8 = m;
/*     */     }
/*     */     else {
/* 100 */       i3 = 0;
/* 101 */       i4 = i2;
/* 102 */       i5 = i1;
/* 103 */       i6 = i2;
/* 104 */       i7 = m;
/* 105 */       i8 = n;
/*     */     }
/*     */ 
/* 109 */     int i = paramInt1;
/* 110 */     int j = paramInt2;
/* 111 */     int i9 = i8 / 2;
/* 112 */     addRect(i, j, 1, 1);
/*     */ 
/* 115 */     for (int k = 0; k < i8; k++)
/*     */     {
/* 118 */       i9 -= i7;
/* 119 */       if (i9 < 0)
/*     */       {
/* 121 */         i9 += i8;
/*     */ 
/* 123 */         i += i5;
/* 124 */         j += i6;
/*     */       }
/*     */       else {
/* 127 */         i += i3;
/* 128 */         j += i4;
/*     */       }
/* 130 */       addRect(i, j, 1, 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void calculateDirtyAreas()
/*     */   {
/* 138 */     for (int i = 0; i < this.rects.getSize(); i++) {
/* 139 */       int j = this.rects.getX(i);
/* 140 */       int k = this.rects.getY(i);
/* 141 */       this.dirtyArea.growDirtyRegion(j, k, j + this.rects.getWidth(i), k + this.rects.getHeight(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 148 */     this.rects.clear();
/* 149 */     this.dirtyArea.clear();
/*     */   }
/*     */ 
/*     */   public void translate(int paramInt1, int paramInt2) {
/* 153 */     if (this.rects.getSize() > 0) {
/* 154 */       this.dirtyArea.translate(paramInt1, paramInt2);
/*     */     }
/* 156 */     this.rects.translateRects(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public GrowableRectArray getRects() {
/* 160 */     return this.rects;
/*     */   }
/*     */ 
/*     */   public DirtyRegion getDirtyArea() {
/* 164 */     return this.dirtyArea;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.MaskTile
 * JD-Core Version:    0.6.2
 */