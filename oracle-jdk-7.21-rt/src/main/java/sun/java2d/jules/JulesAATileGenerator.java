/*     */ package sun.java2d.jules;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import sun.java2d.pipe.AATileGenerator;
/*     */ import sun.java2d.pipe.Region;
/*     */ import sun.java2d.xr.GrowableIntArray;
/*     */ import sun.java2d.xr.XRUtils;
/*     */ 
/*     */ public class JulesAATileGenerator
/*     */   implements AATileGenerator
/*     */ {
/*  36 */   static final ExecutorService rasterThreadPool = Executors.newCachedThreadPool();
/*     */ 
/*  38 */   static final int CPU_CNT = Runtime.getRuntime().availableProcessors();
/*     */   static final boolean ENABLE_THREADING = false;
/*     */   static final int THREAD_MIN = 16;
/*     */   static final int THREAD_BEGIN = 16;
/*     */   IdleTileCache tileCache;
/*     */   TileWorker worker;
/*  46 */   boolean threaded = false;
/*     */   int rasterTileCnt;
/*     */   static final int TILE_SIZE = 32;
/*     */   static final int TILE_SIZE_FP = 2097152;
/*     */   int left;
/*     */   int right;
/*     */   int top;
/*     */   int bottom;
/*     */   int width;
/*     */   int height;
/*     */   int leftFP;
/*     */   int topFP;
/*     */   int tileCnt;
/*     */   int tilesX;
/*     */   int tilesY;
/*  55 */   int currTilePos = 0;
/*     */   TrapezoidList traps;
/*     */   TileTrapContainer[] tiledTrapArray;
/*     */   JulesTile mainTile;
/*     */ 
/*     */   public JulesAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt)
/*     */   {
/*  63 */     JulesPathBuf localJulesPathBuf = new JulesPathBuf();
/*     */ 
/*  65 */     if (paramBasicStroke == null)
/*  66 */       this.traps = localJulesPathBuf.tesselateFill(paramShape, paramAffineTransform, paramRegion);
/*     */     else {
/*  68 */       this.traps = localJulesPathBuf.tesselateStroke(paramShape, paramBasicStroke, paramBoolean1, false, true, paramAffineTransform, paramRegion);
/*     */     }
/*     */ 
/*  71 */     calculateArea(paramArrayOfInt);
/*  72 */     bucketSortTraps();
/*  73 */     calculateTypicalAlpha();
/*     */ 
/*  75 */     this.threaded = false;
/*     */ 
/*  77 */     if (this.threaded) {
/*  78 */       this.tileCache = new IdleTileCache();
/*  79 */       this.worker = new TileWorker(this, 16, this.tileCache);
/*  80 */       rasterThreadPool.execute(this.worker);
/*     */     }
/*     */ 
/*  83 */     this.mainTile = new JulesTile();
/*     */   }
/*     */ 
/*     */   private static native long rasterizeTrapezoidsNative(long paramLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
/*     */ 
/*     */   private static native void freePixmanImgPtr(long paramLong);
/*     */ 
/*     */   private void calculateArea(int[] paramArrayOfInt)
/*     */   {
/*  94 */     this.tilesX = 0;
/*  95 */     this.tilesY = 0;
/*  96 */     this.tileCnt = 0;
/*  97 */     paramArrayOfInt[0] = 0;
/*  98 */     paramArrayOfInt[1] = 0;
/*  99 */     paramArrayOfInt[2] = 0;
/* 100 */     paramArrayOfInt[3] = 0;
/*     */ 
/* 102 */     if (this.traps.getSize() > 0) {
/* 103 */       this.left = this.traps.getLeft();
/* 104 */       this.right = this.traps.getRight();
/* 105 */       this.top = this.traps.getTop();
/* 106 */       this.bottom = this.traps.getBottom();
/* 107 */       this.leftFP = (this.left << 16);
/* 108 */       this.topFP = (this.top << 16);
/*     */ 
/* 110 */       paramArrayOfInt[0] = this.left;
/* 111 */       paramArrayOfInt[1] = this.top;
/* 112 */       paramArrayOfInt[2] = this.right;
/* 113 */       paramArrayOfInt[3] = this.bottom;
/*     */ 
/* 115 */       this.width = (this.right - this.left);
/* 116 */       this.height = (this.bottom - this.top);
/*     */ 
/* 118 */       if ((this.width > 0) && (this.height > 0)) {
/* 119 */         this.tilesX = ((int)Math.ceil(this.width / 32.0D));
/* 120 */         this.tilesY = ((int)Math.ceil(this.height / 32.0D));
/* 121 */         this.tileCnt = (this.tilesY * this.tilesX);
/* 122 */         this.tiledTrapArray = new TileTrapContainer[this.tileCnt];
/*     */       }
/*     */       else
/*     */       {
/* 126 */         this.traps.setSize(0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void bucketSortTraps()
/*     */   {
/* 134 */     for (int i = 0; i < this.traps.getSize(); i++) {
/* 135 */       int j = this.traps.getTop(i) - XRUtils.XDoubleToFixed(this.top);
/* 136 */       int k = this.traps.getBottom(i) - this.topFP;
/* 137 */       int m = this.traps.getP1XLeft(i) - this.leftFP;
/* 138 */       int n = this.traps.getP2XLeft(i) - this.leftFP;
/* 139 */       int i1 = this.traps.getP1XRight(i) - this.leftFP;
/* 140 */       int i2 = this.traps.getP2XRight(i) - this.leftFP;
/*     */ 
/* 142 */       int i3 = Math.min(m, n);
/* 143 */       int i4 = Math.max(i1, i2);
/*     */ 
/* 145 */       i4 = i4 > 0 ? i4 - 1 : i4;
/* 146 */       k = k > 0 ? k - 1 : k;
/*     */ 
/* 148 */       int i5 = j / 2097152;
/* 149 */       int i6 = k / 2097152;
/* 150 */       int i7 = i3 / 2097152;
/* 151 */       int i8 = i4 / 2097152;
/*     */ 
/* 153 */       for (int i9 = i5; i9 <= i6; i9++)
/*     */       {
/* 155 */         for (int i10 = i7; i10 <= i8; i10++) {
/* 156 */           int i11 = i9 * this.tilesX + i10;
/* 157 */           TileTrapContainer localTileTrapContainer = this.tiledTrapArray[i11];
/* 158 */           if (localTileTrapContainer == null) {
/* 159 */             localTileTrapContainer = new TileTrapContainer(new GrowableIntArray(1, 16));
/* 160 */             this.tiledTrapArray[i11] = localTileTrapContainer;
/*     */           }
/*     */ 
/* 163 */           localTileTrapContainer.getTraps().addInt(i);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/* 170 */     JulesTile localJulesTile = null;
/*     */ 
/* 172 */     if (this.threaded) {
/* 173 */       localJulesTile = this.worker.getPreRasterizedTile(this.currTilePos);
/*     */     }
/*     */ 
/* 176 */     if (localJulesTile != null) {
/* 177 */       System.arraycopy(localJulesTile.getImgBuffer(), 0, paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */ 
/* 179 */       this.tileCache.releaseTile(localJulesTile);
/*     */     } else {
/* 181 */       this.mainTile.setImgBuffer(paramArrayOfByte);
/* 182 */       rasterizeTile(this.currTilePos, this.mainTile);
/*     */     }
/*     */ 
/* 185 */     nextTile();
/*     */   }
/*     */ 
/*     */   public void calculateTypicalAlpha() {
/* 189 */     this.rasterTileCnt = 0;
/*     */ 
/* 191 */     for (int i = 0; i < this.tileCnt; i++)
/*     */     {
/* 193 */       TileTrapContainer localTileTrapContainer = this.tiledTrapArray[i];
/* 194 */       if (localTileTrapContainer != null) {
/* 195 */         GrowableIntArray localGrowableIntArray = localTileTrapContainer.getTraps();
/*     */ 
/* 197 */         int j = 127;
/* 198 */         if ((localGrowableIntArray == null) || (localGrowableIntArray.getSize() == 0))
/* 199 */           j = 0;
/* 200 */         else if (doTrapsCoverTile(localGrowableIntArray, i)) {
/* 201 */           j = 255;
/*     */         }
/*     */ 
/* 204 */         if ((j == 127) || (j == 255)) {
/* 205 */           this.rasterTileCnt += 1;
/*     */         }
/*     */ 
/* 208 */         localTileTrapContainer.setTileAlpha(j);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean doTrapsCoverTile(GrowableIntArray paramGrowableIntArray, int paramInt)
/*     */   {
/* 225 */     if (paramGrowableIntArray.getSize() > 32) {
/* 226 */       return false;
/*     */     }
/*     */ 
/* 229 */     int i = getXPos(paramInt) * 2097152 + this.leftFP;
/* 230 */     int j = getYPos(paramInt) * 2097152 + this.topFP;
/* 231 */     int k = i + 2097152;
/* 232 */     int m = j + 2097152;
/*     */ 
/* 235 */     int n = this.traps.getTop(paramGrowableIntArray.getInt(0));
/* 236 */     int i1 = this.traps.getBottom(paramGrowableIntArray.getInt(0));
/* 237 */     if ((n > j) || (i1 < j)) {
/* 238 */       return false;
/*     */     }
/*     */ 
/* 243 */     int i2 = n;
/*     */ 
/* 245 */     for (int i3 = 0; i3 < paramGrowableIntArray.getSize(); i3++) {
/* 246 */       int i4 = paramGrowableIntArray.getInt(i3);
/* 247 */       if ((this.traps.getP1XLeft(i4) > i) || (this.traps.getP2XLeft(i4) > i) || (this.traps.getP1XRight(i4) < k) || (this.traps.getP2XRight(i4) < k) || (this.traps.getTop(i4) != i2))
/*     */       {
/* 253 */         return false;
/*     */       }
/* 255 */       i2 = this.traps.getBottom(i4);
/*     */     }
/*     */ 
/* 260 */     return i2 >= m;
/*     */   }
/*     */ 
/*     */   public int getTypicalAlpha() {
/* 264 */     if (this.tiledTrapArray[this.currTilePos] == null) {
/* 265 */       return 0;
/*     */     }
/* 267 */     return this.tiledTrapArray[this.currTilePos].getTileAlpha();
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 272 */     freePixmanImgPtr(this.mainTile.getPixmanImgPtr());
/*     */ 
/* 274 */     if (this.threaded) {
/* 275 */       this.tileCache.disposeConsumerResources();
/* 276 */       this.worker.disposeConsumerResources();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected JulesTile rasterizeTile(int paramInt, JulesTile paramJulesTile) {
/* 281 */     int i = this.left + getXPos(paramInt) * 32;
/* 282 */     int j = this.top + getYPos(paramInt) * 32;
/* 283 */     TileTrapContainer localTileTrapContainer = this.tiledTrapArray[paramInt];
/* 284 */     GrowableIntArray localGrowableIntArray = localTileTrapContainer.getTraps();
/*     */ 
/* 286 */     if (localTileTrapContainer.getTileAlpha() == 127) {
/* 287 */       long l = rasterizeTrapezoidsNative(paramJulesTile.getPixmanImgPtr(), this.traps.getTrapArray(), localGrowableIntArray.getArray(), localGrowableIntArray.getSize(), paramJulesTile.getImgBuffer(), i, j);
/*     */ 
/* 294 */       paramJulesTile.setPixmanImgPtr(l);
/*     */     }
/*     */ 
/* 297 */     paramJulesTile.setTilePos(paramInt);
/* 298 */     return paramJulesTile;
/*     */   }
/*     */ 
/*     */   protected int getXPos(int paramInt) {
/* 302 */     return paramInt % this.tilesX;
/*     */   }
/*     */ 
/*     */   protected int getYPos(int paramInt) {
/* 306 */     return paramInt / this.tilesX;
/*     */   }
/*     */ 
/*     */   public void nextTile() {
/* 310 */     this.currTilePos += 1;
/*     */   }
/*     */ 
/*     */   public int getTileHeight() {
/* 314 */     return 32;
/*     */   }
/*     */ 
/*     */   public int getTileWidth() {
/* 318 */     return 32;
/*     */   }
/*     */ 
/*     */   public int getTileCount() {
/* 322 */     return this.tileCnt;
/*     */   }
/*     */ 
/*     */   public TileTrapContainer getTrapContainer(int paramInt) {
/* 326 */     return this.tiledTrapArray[paramInt];
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.jules.JulesAATileGenerator
 * JD-Core Version:    0.6.2
 */