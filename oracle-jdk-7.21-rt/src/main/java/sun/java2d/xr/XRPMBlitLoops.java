/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.lang.ref.WeakReference;
/*     */ import sun.awt.image.SunVolatileImage;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.Blit;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.GraphicsPrimitive;
/*     */ import sun.java2d.loops.GraphicsPrimitiveMgr;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ 
/*     */ public class XRPMBlitLoops
/*     */ {
/*  39 */   static WeakReference<SunVolatileImage> argbTmpPM = new WeakReference(null);
/*  40 */   static WeakReference<SunVolatileImage> rgbTmpPM = new WeakReference(null);
/*     */ 
/*     */   public static void register()
/*     */   {
/*  46 */     GraphicsPrimitive[] arrayOfGraphicsPrimitive = { new XRPMBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntRgbX11), new XRPMBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntArgbPreX11), new XRPMBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntRgbX11), new XRPMBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntArgbPreX11), new XRPMScaledBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntRgbX11), new XRPMScaledBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntArgbPreX11), new XRPMScaledBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntRgbX11), new XRPMScaledBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntArgbPreX11), new XRPMTransformedBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntRgbX11), new XRPMTransformedBlit(XRSurfaceData.IntRgbX11, XRSurfaceData.IntArgbPreX11), new XRPMTransformedBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntRgbX11), new XRPMTransformedBlit(XRSurfaceData.IntArgbPreX11, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.IntArgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.IntRgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.IntBgr, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntRgbX11), new XrSwToPMBlit(SurfaceType.IntArgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.IntRgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.IntBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.IntArgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.IntRgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.IntBgr, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntRgbX11), new XrSwToPMScaledBlit(SurfaceType.IntArgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.IntRgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.IntBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMScaledBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.IntArgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.IntRgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.IntBgr, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntRgbX11), new XrSwToPMTransformedBlit(SurfaceType.IntArgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.IntRgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.IntBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.ThreeByteBgr, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort565Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.Ushort555Rgb, XRSurfaceData.IntArgbPreX11), new XrSwToPMTransformedBlit(SurfaceType.ByteIndexed, XRSurfaceData.IntArgbPreX11) };
/*     */ 
/* 111 */     GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
/*     */   }
/*     */ 
/*     */   protected static XRSurfaceData cacheToTmpSurface(SurfaceData paramSurfaceData, XRSurfaceData paramXRSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*     */     SunVolatileImage localSunVolatileImage;
/*     */     SurfaceType localSurfaceType;
/* 122 */     if (paramSurfaceData.getTransparency() == 1) {
/* 123 */       localSunVolatileImage = (SunVolatileImage)rgbTmpPM.get();
/* 124 */       localSurfaceType = SurfaceType.IntRgb;
/*     */     } else {
/* 126 */       localSunVolatileImage = (SunVolatileImage)argbTmpPM.get();
/* 127 */       localSurfaceType = SurfaceType.IntArgbPre;
/*     */     }
/*     */ 
/* 130 */     if ((localSunVolatileImage == null) || (localSunVolatileImage.getWidth() < paramInt1) || (localSunVolatileImage.getHeight() < paramInt2)) {
/* 131 */       if (localSunVolatileImage != null) {
/* 132 */         localSunVolatileImage.flush();
/*     */       }
/* 134 */       localSunVolatileImage = (SunVolatileImage)paramXRSurfaceData.getGraphicsConfig().createCompatibleVolatileImage(paramInt1, paramInt2, paramSurfaceData.getTransparency());
/* 135 */       localSunVolatileImage.setAccelerationPriority(1.0F);
/*     */ 
/* 137 */       if (paramSurfaceData.getTransparency() == 1)
/* 138 */         rgbTmpPM = new WeakReference(localSunVolatileImage);
/*     */       else {
/* 140 */         argbTmpPM = new WeakReference(localSunVolatileImage);
/*     */       }
/*     */     }
/*     */ 
/* 144 */     Blit localBlit = Blit.getFromCache(paramSurfaceData.getSurfaceType(), CompositeType.SrcNoEa, localSurfaceType);
/* 145 */     XRSurfaceData localXRSurfaceData = (XRSurfaceData)localSunVolatileImage.getDestSurface();
/* 146 */     localBlit.Blit(paramSurfaceData, localXRSurfaceData, AlphaComposite.Src, null, paramInt3, paramInt4, 0, 0, paramInt1, paramInt2);
/*     */ 
/* 149 */     return localXRSurfaceData;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRPMBlitLoops
 * JD-Core Version:    0.6.2
 */