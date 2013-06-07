/*    */ package sun.font;
/*    */ 
/*    */ import sun.awt.SunToolkit;
/*    */ import sun.java2d.SunGraphics2D;
/*    */ import sun.java2d.pipe.GlyphListPipe;
/*    */ import sun.java2d.xr.GrowableEltArray;
/*    */ import sun.java2d.xr.GrowableIntArray;
/*    */ import sun.java2d.xr.XRBackend;
/*    */ import sun.java2d.xr.XRCompositeManager;
/*    */ import sun.java2d.xr.XRSurfaceData;
/*    */ 
/*    */ public class XRTextRenderer extends GlyphListPipe
/*    */ {
/*    */   XRGlyphCache glyphCache;
/*    */   XRCompositeManager maskBuffer;
/*    */   XRBackend backend;
/*    */   GrowableEltArray eltList;
/*    */ 
/*    */   public XRTextRenderer(XRCompositeManager paramXRCompositeManager)
/*    */   {
/* 47 */     this.glyphCache = new XRGlyphCache(paramXRCompositeManager);
/* 48 */     this.maskBuffer = paramXRCompositeManager;
/* 49 */     this.backend = paramXRCompositeManager.getBackend();
/* 50 */     this.eltList = new GrowableEltArray(64);
/*    */   }
/*    */ 
/*    */   protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList) {
/* 54 */     if (paramGlyphList.getNumGlyphs() == 0) {
/* 55 */       return;
/*    */     }
/*    */     try
/*    */     {
/* 59 */       SunToolkit.awtLock();
/*    */ 
/* 61 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/* 62 */       localXRSurfaceData.validateAsDestination(null, paramSunGraphics2D.getCompClip());
/* 63 */       localXRSurfaceData.maskBuffer.validateCompositeState(paramSunGraphics2D.composite, paramSunGraphics2D.transform, paramSunGraphics2D.paint, paramSunGraphics2D);
/*    */ 
/* 65 */       float f1 = paramGlyphList.getX();
/* 66 */       float f2 = paramGlyphList.getY();
/* 67 */       int i = 0; int j = 0;
/*    */ 
/* 69 */       if (paramGlyphList.isSubPixPos()) {
/* 70 */         f1 += 0.166667F;
/* 71 */         f2 += 0.166667F;
/*    */       } else {
/* 73 */         f1 += 0.5F;
/* 74 */         f2 += 0.5F;
/*    */       }
/*    */ 
/* 77 */       XRGlyphCacheEntry[] arrayOfXRGlyphCacheEntry = this.glyphCache.cacheGlyphs(paramGlyphList);
/* 78 */       int k = 0;
/* 79 */       int m = arrayOfXRGlyphCacheEntry[0].getGlyphSet();
/*    */ 
/* 81 */       int n = -1;
/* 82 */       paramGlyphList.getBounds();
/* 83 */       float[] arrayOfFloat = paramGlyphList.getPositions();
/* 84 */       for (int i1 = 0; i1 < paramGlyphList.getNumGlyphs(); i1++) {
/* 85 */         paramGlyphList.setGlyphIndex(i1);
/* 86 */         XRGlyphCacheEntry localXRGlyphCacheEntry = arrayOfXRGlyphCacheEntry[i1];
/*    */ 
/* 88 */         this.eltList.getGlyphs().addInt(localXRGlyphCacheEntry.getGlyphID());
/* 89 */         int i2 = localXRGlyphCacheEntry.getGlyphSet();
/*    */ 
/* 91 */         k |= (i2 == this.glyphCache.lcdGlyphSet ? 1 : 0);
/*    */ 
/* 93 */         int i3 = 0; int i4 = 0;
/* 94 */         if ((paramGlyphList.usePositions()) || (localXRGlyphCacheEntry.getXAdvance() != localXRGlyphCacheEntry.getXOff()) || (localXRGlyphCacheEntry.getYAdvance() != localXRGlyphCacheEntry.getYOff()) || (n < 0) || (i2 != m))
/*    */         {
/* 98 */           n = this.eltList.getNextIndex();
/* 99 */           this.eltList.setCharCnt(n, 1);
/* 100 */           m = i2;
/* 101 */           this.eltList.setGlyphSet(n, i2);
/*    */ 
/* 103 */           if (paramGlyphList.usePositions())
/*    */           {
/* 105 */             float f3 = arrayOfFloat[(i1 * 2)] + f1;
/* 106 */             float f4 = arrayOfFloat[(i1 * 2 + 1)] + f2;
/* 107 */             i3 = (int)Math.floor(f3);
/* 108 */             i4 = (int)Math.floor(f4);
/* 109 */             f1 -= localXRGlyphCacheEntry.getXOff();
/* 110 */             f2 -= localXRGlyphCacheEntry.getYOff();
/*    */           }
/*    */           else
/*    */           {
/* 120 */             i3 = (int)Math.floor(f1);
/* 121 */             i4 = (int)Math.floor(f2);
/*    */ 
/* 126 */             f1 += localXRGlyphCacheEntry.getXAdvance() - localXRGlyphCacheEntry.getXOff();
/* 127 */             f2 += localXRGlyphCacheEntry.getYAdvance() - localXRGlyphCacheEntry.getYOff();
/*    */           }
/*    */ 
/* 133 */           this.eltList.setXOff(n, i3 - i);
/* 134 */           this.eltList.setYOff(n, i4 - j);
/*    */ 
/* 136 */           i = i3;
/* 137 */           j = i4;
/*    */         }
/*    */         else {
/* 140 */           this.eltList.setCharCnt(n, this.eltList.getCharCnt(n) + 1);
/*    */         }
/*    */       }
/*    */ 
/* 144 */       i1 = k != 0 ? 0 : 2;
/* 145 */       this.maskBuffer.compositeText(localXRSurfaceData.picture, 0, i1, this.eltList);
/*    */ 
/* 147 */       this.eltList.clear();
/*    */     } finally {
/* 149 */       SunToolkit.awtUnlock();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.XRTextRenderer
 * JD-Core Version:    0.6.2
 */