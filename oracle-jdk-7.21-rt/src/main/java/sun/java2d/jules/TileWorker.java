/*     */ package sun.java2d.jules;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class TileWorker
/*     */   implements Runnable
/*     */ {
/*     */   static final int RASTERIZED_TILE_SYNC_GRANULARITY = 8;
/*  32 */   final ArrayList<JulesTile> rasterizedTileConsumerCache = new ArrayList();
/*     */ 
/*  34 */   final LinkedList<JulesTile> rasterizedBuffers = new LinkedList();
/*     */   IdleTileCache tileCache;
/*     */   JulesAATileGenerator tileGenerator;
/*     */   int workerStartIndex;
/*  39 */   volatile int consumerPos = 0;
/*     */ 
/*  42 */   int mainThreadCnt = 0;
/*  43 */   int workerCnt = 0;
/*  44 */   int doubled = 0;
/*     */ 
/*     */   public TileWorker(JulesAATileGenerator paramJulesAATileGenerator, int paramInt, IdleTileCache paramIdleTileCache) {
/*  47 */     this.tileGenerator = paramJulesAATileGenerator;
/*  48 */     this.workerStartIndex = paramInt;
/*  49 */     this.tileCache = paramIdleTileCache;
/*     */   }
/*     */ 
/*     */   public void run() {
/*  53 */     ArrayList localArrayList = new ArrayList(16);
/*     */ 
/*  55 */     for (int i = this.workerStartIndex; i < this.tileGenerator.getTileCount(); i++) {
/*  56 */       TileTrapContainer localTileTrapContainer = this.tileGenerator.getTrapContainer(i);
/*     */ 
/*  58 */       if ((localTileTrapContainer != null) && (localTileTrapContainer.getTileAlpha() == 127)) {
/*  59 */         JulesTile localJulesTile = this.tileGenerator.rasterizeTile(i, this.tileCache.getIdleTileWorker(this.tileGenerator.getTileCount() - i - 1));
/*     */ 
/*  63 */         localArrayList.add(localJulesTile);
/*     */ 
/*  65 */         if (localArrayList.size() > 8) {
/*  66 */           addRasterizedTiles(localArrayList);
/*  67 */           localArrayList.clear();
/*     */         }
/*     */       }
/*     */ 
/*  71 */       i = Math.max(i, this.consumerPos + 4);
/*     */     }
/*  73 */     addRasterizedTiles(localArrayList);
/*     */ 
/*  75 */     this.tileCache.disposeRasterizerResources();
/*     */   }
/*     */ 
/*     */   public JulesTile getPreRasterizedTile(int paramInt)
/*     */   {
/*  84 */     Object localObject1 = null;
/*     */ 
/*  86 */     if ((this.rasterizedTileConsumerCache.size() == 0) && (paramInt >= this.workerStartIndex))
/*     */     {
/*  89 */       synchronized (this.rasterizedBuffers) {
/*  90 */         this.rasterizedTileConsumerCache.addAll(this.rasterizedBuffers);
/*  91 */         this.rasterizedBuffers.clear();
/*     */       }
/*     */     }
/*     */ 
/*  95 */     while ((localObject1 == null) && (this.rasterizedTileConsumerCache.size() > 0)) {
/*  96 */       ??? = (JulesTile)this.rasterizedTileConsumerCache.get(0);
/*     */ 
/*  98 */       if (((JulesTile)???).getTilePos() > paramInt)
/*     */       {
/*     */         break;
/*     */       }
/* 102 */       if (((JulesTile)???).getTilePos() < paramInt) {
/* 103 */         this.tileCache.releaseTile((JulesTile)???);
/* 104 */         this.doubled += 1;
/*     */       }
/*     */ 
/* 107 */       if (((JulesTile)???).getTilePos() <= paramInt) {
/* 108 */         this.rasterizedTileConsumerCache.remove(0);
/*     */       }
/*     */ 
/* 111 */       if (((JulesTile)???).getTilePos() == paramInt) {
/* 112 */         localObject1 = ???;
/*     */       }
/*     */     }
/*     */ 
/* 116 */     if (localObject1 == null) {
/* 117 */       this.mainThreadCnt += 1;
/*     */ 
/* 121 */       this.consumerPos = paramInt;
/*     */     } else {
/* 123 */       this.workerCnt += 1;
/*     */     }
/*     */ 
/* 126 */     return localObject1;
/*     */   }
/*     */ 
/*     */   private void addRasterizedTiles(ArrayList<JulesTile> paramArrayList) {
/* 130 */     synchronized (this.rasterizedBuffers) {
/* 131 */       this.rasterizedBuffers.addAll(paramArrayList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disposeConsumerResources()
/*     */   {
/* 140 */     synchronized (this.rasterizedBuffers) {
/* 141 */       this.tileCache.releaseTiles(this.rasterizedBuffers);
/*     */     }
/*     */ 
/* 144 */     this.tileCache.releaseTiles(this.rasterizedTileConsumerCache);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.jules.TileWorker
 * JD-Core Version:    0.6.2
 */