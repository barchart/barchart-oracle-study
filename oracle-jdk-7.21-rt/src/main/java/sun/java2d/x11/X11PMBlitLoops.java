/*     */ package sun.java2d.x11;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.Blit;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.GraphicsPrimitive;
/*     */ import sun.java2d.loops.GraphicsPrimitiveMgr;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ public class X11PMBlitLoops extends Blit
/*     */ {
/*     */   public static void register()
/*     */   {
/*  54 */     GraphicsPrimitive[] arrayOfGraphicsPrimitive = { new X11PMBlitLoops(X11SurfaceData.IntBgrX11, X11SurfaceData.IntBgrX11, false), new X11PMBlitLoops(X11SurfaceData.IntRgbX11, X11SurfaceData.IntRgbX11, false), new X11PMBlitLoops(X11SurfaceData.ThreeByteBgrX11, X11SurfaceData.ThreeByteBgrX11, false), new X11PMBlitLoops(X11SurfaceData.ThreeByteRgbX11, X11SurfaceData.ThreeByteRgbX11, false), new X11PMBlitLoops(X11SurfaceData.ByteIndexedOpaqueX11, X11SurfaceData.ByteIndexedOpaqueX11, false), new X11PMBlitLoops(X11SurfaceData.ByteGrayX11, X11SurfaceData.ByteGrayX11, false), new X11PMBlitLoops(X11SurfaceData.Index8GrayX11, X11SurfaceData.Index8GrayX11, false), new X11PMBlitLoops(X11SurfaceData.UShort555RgbX11, X11SurfaceData.UShort555RgbX11, false), new X11PMBlitLoops(X11SurfaceData.UShort565RgbX11, X11SurfaceData.UShort565RgbX11, false), new X11PMBlitLoops(X11SurfaceData.UShortIndexedX11, X11SurfaceData.UShortIndexedX11, false), new X11PMBlitLoops(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.IntBgrX11, true), new X11PMBlitLoops(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntRgbX11, true), new X11PMBlitLoops(X11SurfaceData.ThreeByteBgrX11_BM, X11SurfaceData.ThreeByteBgrX11, true), new X11PMBlitLoops(X11SurfaceData.ThreeByteRgbX11_BM, X11SurfaceData.ThreeByteRgbX11, true), new X11PMBlitLoops(X11SurfaceData.ByteIndexedX11_BM, X11SurfaceData.ByteIndexedOpaqueX11, true), new X11PMBlitLoops(X11SurfaceData.ByteGrayX11_BM, X11SurfaceData.ByteGrayX11, true), new X11PMBlitLoops(X11SurfaceData.Index8GrayX11_BM, X11SurfaceData.Index8GrayX11, true), new X11PMBlitLoops(X11SurfaceData.UShort555RgbX11_BM, X11SurfaceData.UShort555RgbX11, true), new X11PMBlitLoops(X11SurfaceData.UShort565RgbX11_BM, X11SurfaceData.UShort565RgbX11, true), new X11PMBlitLoops(X11SurfaceData.UShortIndexedX11_BM, X11SurfaceData.UShortIndexedX11, true), new X11PMBlitLoops(X11SurfaceData.IntRgbX11, X11SurfaceData.IntArgbPreX11, true), new X11PMBlitLoops(X11SurfaceData.IntRgbX11, X11SurfaceData.IntArgbPreX11, false), new X11PMBlitLoops(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntArgbPreX11, true), new X11PMBlitLoops(X11SurfaceData.IntBgrX11, X11SurfaceData.FourByteAbgrPreX11, true), new X11PMBlitLoops(X11SurfaceData.IntBgrX11, X11SurfaceData.FourByteAbgrPreX11, false), new X11PMBlitLoops(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.FourByteAbgrPreX11, true), new DelegateBlitLoop(X11SurfaceData.IntBgrX11_BM, X11SurfaceData.IntBgrX11), new DelegateBlitLoop(X11SurfaceData.IntRgbX11_BM, X11SurfaceData.IntRgbX11), new DelegateBlitLoop(X11SurfaceData.ThreeByteBgrX11_BM, X11SurfaceData.ThreeByteBgrX11), new DelegateBlitLoop(X11SurfaceData.ThreeByteRgbX11_BM, X11SurfaceData.ThreeByteRgbX11), new DelegateBlitLoop(X11SurfaceData.ByteIndexedX11_BM, X11SurfaceData.ByteIndexedOpaqueX11), new DelegateBlitLoop(X11SurfaceData.ByteGrayX11_BM, X11SurfaceData.ByteGrayX11), new DelegateBlitLoop(X11SurfaceData.Index8GrayX11_BM, X11SurfaceData.Index8GrayX11), new DelegateBlitLoop(X11SurfaceData.UShort555RgbX11_BM, X11SurfaceData.UShort555RgbX11), new DelegateBlitLoop(X11SurfaceData.UShort565RgbX11_BM, X11SurfaceData.UShort565RgbX11), new DelegateBlitLoop(X11SurfaceData.UShortIndexedX11_BM, X11SurfaceData.UShortIndexedX11) };
/*     */ 
/* 137 */     GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
/*     */   }
/*     */ 
/*     */   public X11PMBlitLoops(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, boolean paramBoolean)
/*     */   {
/* 142 */     super(paramSurfaceType1, paramBoolean ? CompositeType.SrcOverNoEa : CompositeType.SrcNoEa, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 153 */     SunToolkit.awtLock();
/*     */     try {
/* 155 */       X11SurfaceData localX11SurfaceData = (X11SurfaceData)paramSurfaceData2;
/*     */ 
/* 158 */       long l = localX11SurfaceData.getBlitGC(null, false);
/* 159 */       nativeBlit(paramSurfaceData1.getNativeOps(), paramSurfaceData2.getNativeOps(), l, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */     finally {
/* 162 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private native void nativeBlit(long paramLong1, long paramLong2, long paramLong3, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   private static native void updateBitmask(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, boolean paramBoolean);
/*     */ 
/*     */   static class DelegateBlitLoop extends Blit
/*     */   {
/*     */     SurfaceType dstType;
/*     */ 
/*     */     public DelegateBlitLoop(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2)
/*     */     {
/* 193 */       super(CompositeType.SrcNoEa, paramSurfaceType1);
/* 194 */       this.dstType = paramSurfaceType2;
/*     */     }
/*     */ 
/*     */     public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */     {
/* 201 */       Blit localBlit = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.SrcNoEa, this.dstType);
/*     */ 
/* 204 */       localBlit.Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/* 205 */       X11PMBlitLoops.updateBitmask(paramSurfaceData1, paramSurfaceData2, paramSurfaceData1.getColorModel() instanceof IndexColorModel);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.x11.X11PMBlitLoops
 * JD-Core Version:    0.6.2
 */