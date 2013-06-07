/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class MaskTileManager
/*     */ {
/*     */   public static final int MASK_SIZE = 256;
/*  45 */   MaskTile mainTile = new MaskTile();
/*     */   ArrayList<MaskTile> tileList;
/*  48 */   int allocatedTiles = 0;
/*     */   int xTiles;
/*     */   int yTiles;
/*     */   XRCompositeManager xrMgr;
/*     */   XRBackend con;
/*     */   int maskPixmap;
/*     */   int maskPicture;
/*     */   long maskGC;
/*     */   int lineMaskPixmap;
/*     */   int lineMaskPicture;
/*     */   long drawLineGC;
/*     */   long clearLineGC;
/*     */ 
/*     */   public MaskTileManager(XRCompositeManager paramXRCompositeManager, int paramInt)
/*     */   {
/*  63 */     this.tileList = new ArrayList();
/*  64 */     this.xrMgr = paramXRCompositeManager;
/*  65 */     this.con = paramXRCompositeManager.getBackend();
/*     */ 
/*  67 */     this.maskPixmap = this.con.createPixmap(paramInt, 8, 256, 256);
/*  68 */     this.maskPicture = this.con.createPicture(this.maskPixmap, 2);
/*  69 */     this.con.renderRectangle(this.maskPicture, (byte)0, new XRColor(Color.black), 0, 0, 256, 256);
/*     */ 
/*  72 */     this.maskGC = this.con.createGC(this.maskPixmap);
/*  73 */     this.con.setGCExposures(this.maskGC, false);
/*     */ 
/*  75 */     this.lineMaskPixmap = this.con.createPixmap(paramInt, 8, 256, 256);
/*  76 */     this.lineMaskPicture = this.con.createPicture(this.lineMaskPixmap, 2);
/*     */ 
/*  78 */     this.con.renderRectangle(this.lineMaskPicture, (byte)0, new XRColor(Color.black), 0, 0, 256, 256);
/*     */ 
/*  81 */     this.drawLineGC = this.con.createGC(this.lineMaskPixmap);
/*  82 */     this.con.setGCExposures(this.drawLineGC, false);
/*  83 */     this.con.setGCForeground(this.drawLineGC, 255);
/*     */ 
/*  85 */     this.clearLineGC = this.con.createGC(this.lineMaskPixmap);
/*  86 */     this.con.setGCExposures(this.clearLineGC, false);
/*  87 */     this.con.setGCForeground(this.clearLineGC, 0);
/*     */   }
/*     */ 
/*     */   public void addRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  94 */     this.mainTile.addRect(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   public void addLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 101 */     this.mainTile.addLine(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   public void fillMask(XRSurfaceData paramXRSurfaceData)
/*     */   {
/* 110 */     boolean bool = this.xrMgr.maskRequired();
/*     */ 
/* 112 */     if (bool) {
/* 113 */       this.mainTile.calculateDirtyAreas();
/* 114 */       DirtyRegion localDirtyRegion = this.mainTile.getDirtyArea().cloneRegion();
/* 115 */       this.mainTile.translate(-localDirtyRegion.x, -localDirtyRegion.y);
/*     */ 
/* 117 */       XRColor localXRColor = this.xrMgr.getMaskColor();
/*     */ 
/* 120 */       if ((localDirtyRegion.getWidth() <= 256) && (localDirtyRegion.getHeight() <= 256))
/*     */       {
/* 123 */         compositeSingleTile(paramXRSurfaceData, this.mainTile, localDirtyRegion, bool, 0, 0, localXRColor);
/*     */       }
/*     */       else {
/* 126 */         allocTiles(localDirtyRegion);
/* 127 */         tileRects();
/*     */ 
/* 129 */         for (int i = 0; i < this.yTiles; i++)
/* 130 */           for (int j = 0; j < this.xTiles; j++) {
/* 131 */             MaskTile localMaskTile = (MaskTile)this.tileList.get(i * this.xTiles + j);
/*     */ 
/* 133 */             int k = j * 256;
/* 134 */             int m = i * 256;
/* 135 */             compositeSingleTile(paramXRSurfaceData, localMaskTile, localDirtyRegion, bool, k, m, localXRColor);
/*     */           }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 141 */       this.xrMgr.XRRenderRectangles(paramXRSurfaceData, this.mainTile.getRects());
/*     */     }
/*     */ 
/* 144 */     this.mainTile.reset();
/*     */   }
/*     */ 
/*     */   public int uploadMask(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
/*     */   {
/* 151 */     int i = 0;
/*     */ 
/* 153 */     if (paramArrayOfByte != null) {
/* 154 */       float f = this.xrMgr.isTexturePaintActive() ? this.xrMgr.getExtraAlpha() : 1.0F;
/*     */ 
/* 156 */       this.con.putMaskImage(this.maskPixmap, this.maskGC, paramArrayOfByte, 0, 0, 0, 0, paramInt1, paramInt2, paramInt4, paramInt3, f);
/*     */ 
/* 158 */       i = this.maskPicture;
/* 159 */     } else if (this.xrMgr.isTexturePaintActive()) {
/* 160 */       i = this.xrMgr.getExtraAlphaMask();
/*     */     }
/*     */ 
/* 163 */     return i;
/*     */   }
/*     */ 
/*     */   public void clearUploadMask(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 170 */     if (paramInt1 == this.maskPicture)
/* 171 */       this.con.renderRectangle(this.maskPicture, (byte)0, XRColor.NO_ALPHA, 0, 0, paramInt2, paramInt3);
/*     */   }
/*     */ 
/*     */   protected void compositeSingleTile(XRSurfaceData paramXRSurfaceData, MaskTile paramMaskTile, DirtyRegion paramDirtyRegion, boolean paramBoolean, int paramInt1, int paramInt2, XRColor paramXRColor)
/*     */   {
/* 186 */     if (paramMaskTile.rects.getSize() > 0) {
/* 187 */       DirtyRegion localDirtyRegion = paramMaskTile.getDirtyArea();
/*     */ 
/* 189 */       int i = localDirtyRegion.x + paramInt1 + paramDirtyRegion.x;
/* 190 */       int j = localDirtyRegion.y + paramInt2 + paramDirtyRegion.y;
/* 191 */       int k = localDirtyRegion.x2 - localDirtyRegion.x;
/* 192 */       int m = localDirtyRegion.y2 - localDirtyRegion.y;
/* 193 */       k = Math.min(k, 256);
/* 194 */       m = Math.min(m, 256);
/*     */ 
/* 196 */       int n = paramMaskTile.rects.getSize();
/*     */ 
/* 198 */       if (paramBoolean) {
/* 199 */         int i1 = 0;
/*     */ 
/* 205 */         if (n > 1) {
/* 206 */           this.con.renderRectangles(this.maskPicture, (byte)1, paramXRColor, paramMaskTile.rects);
/*     */ 
/* 208 */           i1 = this.maskPicture;
/*     */         }
/* 210 */         else if (this.xrMgr.isTexturePaintActive()) {
/* 211 */           i1 = this.xrMgr.getExtraAlphaMask();
/*     */         }
/*     */ 
/* 215 */         this.xrMgr.XRComposite(0, i1, paramXRSurfaceData.getPicture(), i, j, localDirtyRegion.x, localDirtyRegion.y, i, j, k, m);
/*     */ 
/* 220 */         if (n > 1) {
/* 221 */           this.con.renderRectangle(this.maskPicture, (byte)0, XRColor.NO_ALPHA, localDirtyRegion.x, localDirtyRegion.y, k, m);
/*     */         }
/*     */ 
/* 227 */         paramMaskTile.reset();
/* 228 */       } else if (n > 0) {
/* 229 */         paramMaskTile.rects.translateRects(paramInt1 + paramDirtyRegion.x, paramInt2 + paramDirtyRegion.y);
/*     */ 
/* 231 */         this.xrMgr.XRRenderRectangles(paramXRSurfaceData, paramMaskTile.rects);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void allocTiles(DirtyRegion paramDirtyRegion)
/*     */   {
/* 242 */     this.xTiles = (paramDirtyRegion.getWidth() / 256 + 1);
/* 243 */     this.yTiles = (paramDirtyRegion.getHeight() / 256 + 1);
/* 244 */     int i = this.xTiles * this.yTiles;
/*     */ 
/* 246 */     if (i > this.allocatedTiles) {
/* 247 */       for (int j = 0; j < i; j++) {
/* 248 */         if (j < this.allocatedTiles)
/* 249 */           ((MaskTile)this.tileList.get(j)).reset();
/*     */         else {
/* 251 */           this.tileList.add(new MaskTile());
/*     */         }
/*     */       }
/*     */ 
/* 255 */       this.allocatedTiles = i;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void tileRects()
/*     */   {
/* 263 */     GrowableRectArray localGrowableRectArray1 = this.mainTile.rects;
/*     */ 
/* 265 */     for (int i = 0; i < localGrowableRectArray1.getSize(); i++) {
/* 266 */       int j = localGrowableRectArray1.getX(i) / 256;
/* 267 */       int k = localGrowableRectArray1.getY(i) / 256;
/* 268 */       int m = (localGrowableRectArray1.getX(i) + localGrowableRectArray1.getWidth(i)) / 256 + 1 - j;
/*     */ 
/* 271 */       int n = (localGrowableRectArray1.getY(i) + localGrowableRectArray1.getHeight(i)) / 256 + 1 - k;
/*     */ 
/* 275 */       for (int i1 = 0; i1 < n; i1++)
/* 276 */         for (int i2 = 0; i2 < m; i2++)
/*     */         {
/* 278 */           int i3 = this.xTiles * (k + i1) + j + i2;
/*     */ 
/* 280 */           MaskTile localMaskTile = (MaskTile)this.tileList.get(i3);
/*     */ 
/* 282 */           GrowableRectArray localGrowableRectArray2 = localMaskTile.getRects();
/* 283 */           int i4 = localGrowableRectArray2.getNextIndex();
/*     */ 
/* 285 */           int i5 = (j + i2) * 256;
/* 286 */           int i6 = (k + i1) * 256;
/*     */ 
/* 288 */           localGrowableRectArray2.setX(i4, localGrowableRectArray1.getX(i) - i5);
/* 289 */           localGrowableRectArray2.setY(i4, localGrowableRectArray1.getY(i) - i6);
/* 290 */           localGrowableRectArray2.setWidth(i4, localGrowableRectArray1.getWidth(i));
/* 291 */           localGrowableRectArray2.setHeight(i4, localGrowableRectArray1.getHeight(i));
/*     */ 
/* 293 */           limitRectCoords(localGrowableRectArray2, i4);
/*     */ 
/* 295 */           localMaskTile.getDirtyArea().growDirtyRegion(localGrowableRectArray2.getX(i4), localGrowableRectArray2.getY(i4), localGrowableRectArray2.getWidth(i4) + localGrowableRectArray2.getX(i4), localGrowableRectArray2.getHeight(i4) + localGrowableRectArray2.getY(i4));
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void limitRectCoords(GrowableRectArray paramGrowableRectArray, int paramInt)
/*     */   {
/* 312 */     if (paramGrowableRectArray.getX(paramInt) + paramGrowableRectArray.getWidth(paramInt) > 256) {
/* 313 */       paramGrowableRectArray.setWidth(paramInt, 256 - paramGrowableRectArray.getX(paramInt));
/*     */     }
/* 315 */     if (paramGrowableRectArray.getY(paramInt) + paramGrowableRectArray.getHeight(paramInt) > 256) {
/* 316 */       paramGrowableRectArray.setHeight(paramInt, 256 - paramGrowableRectArray.getY(paramInt));
/*     */     }
/* 318 */     if (paramGrowableRectArray.getX(paramInt) < 0) {
/* 319 */       paramGrowableRectArray.setWidth(paramInt, paramGrowableRectArray.getWidth(paramInt) + paramGrowableRectArray.getX(paramInt));
/* 320 */       paramGrowableRectArray.setX(paramInt, 0);
/*     */     }
/* 322 */     if (paramGrowableRectArray.getY(paramInt) < 0) {
/* 323 */       paramGrowableRectArray.setHeight(paramInt, paramGrowableRectArray.getHeight(paramInt) + paramGrowableRectArray.getY(paramInt));
/* 324 */       paramGrowableRectArray.setY(paramInt, 0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.MaskTileManager
 * JD-Core Version:    0.6.2
 */