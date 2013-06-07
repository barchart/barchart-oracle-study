/*     */ package sun.java2d.loops;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import java.awt.image.BufferedImage;
/*     */ import sun.awt.image.BufImgSurfaceData;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ 
/*     */ public class MaskFill extends GraphicsPrimitive
/*     */ {
/*  52 */   public static final String methodSignature = "MaskFill(...)".toString();
/*  53 */   public static final String fillPgramSignature = "FillAAPgram(...)".toString();
/*     */ 
/*  55 */   public static final String drawPgramSignature = "DrawAAPgram(...)".toString();
/*     */ 
/*  58 */   public static final int primTypeID = makePrimTypeID();
/*     */ 
/*  60 */   private static RenderCache fillcache = new RenderCache(10);
/*     */ 
/*     */   public static MaskFill locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  66 */     return (MaskFill)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public static MaskFill locatePrim(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  75 */     return (MaskFill)GraphicsPrimitiveMgr.locatePrim(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public static MaskFill getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  88 */     Object localObject = fillcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*  89 */     if (localObject != null) {
/*  90 */       return (MaskFill)localObject;
/*     */     }
/*  92 */     MaskFill localMaskFill = locatePrim(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*  93 */     if (localMaskFill != null) {
/*  94 */       fillcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, localMaskFill);
/*     */     }
/*  96 */     return localMaskFill;
/*     */   }
/*     */ 
/*     */   protected MaskFill(String paramString, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/* 104 */     super(paramString, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   protected MaskFill(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/* 111 */     super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public MaskFill(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/* 119 */     super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public native void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6);
/*     */ 
/*     */   public native void FillAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
/*     */ 
/*     */   public native void DrawAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
/*     */ 
/*     */   public boolean canDoParallelograms()
/*     */   {
/* 144 */     return getNativePrim() != 0L;
/*     */   }
/*     */ 
/*     */   public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/* 155 */     if ((SurfaceType.OpaqueColor.equals(paramSurfaceType1)) || (SurfaceType.AnyColor.equals(paramSurfaceType1)))
/*     */     {
/* 158 */       if (CompositeType.Xor.equals(paramCompositeType)) {
/* 159 */         throw new InternalError("Cannot construct MaskFill for XOR mode");
/*     */       }
/*     */ 
/* 162 */       return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */     }
/*     */ 
/* 165 */     throw new InternalError("MaskFill can only fill with colors");
/*     */   }
/*     */ 
/*     */   public GraphicsPrimitive traceWrap()
/*     */   {
/* 209 */     return new TraceMaskFill(this);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 148 */     GraphicsPrimitiveMgr.registerGeneral(new MaskFill(null, null, null));
/*     */   }
/*     */ 
/*     */   private static class General extends MaskFill
/*     */   {
/*     */     FillRect fillop;
/*     */     MaskBlit maskop;
/*     */ 
/*     */     public General(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */     {
/* 177 */       super(paramCompositeType, paramSurfaceType2);
/* 178 */       this.fillop = FillRect.locate(paramSurfaceType1, CompositeType.SrcNoEa, SurfaceType.IntArgb);
/*     */ 
/* 181 */       this.maskop = MaskBlit.locate(SurfaceType.IntArgb, paramCompositeType, paramSurfaceType2);
/*     */     }
/*     */ 
/*     */     public void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
/*     */     {
/* 190 */       BufferedImage localBufferedImage = new BufferedImage(paramInt3, paramInt4, 2);
/*     */ 
/* 192 */       SurfaceData localSurfaceData = BufImgSurfaceData.createData(localBufferedImage);
/*     */ 
/* 197 */       int i = paramSunGraphics2D.pixel;
/* 198 */       paramSunGraphics2D.pixel = localSurfaceData.pixelFor(paramSunGraphics2D.getColor());
/* 199 */       this.fillop.FillRect(paramSunGraphics2D, localSurfaceData, 0, 0, paramInt3, paramInt4);
/* 200 */       paramSunGraphics2D.pixel = i;
/*     */ 
/* 202 */       this.maskop.MaskBlit(localSurfaceData, paramSurfaceData, paramComposite, null, 0, 0, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5, paramInt6);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class TraceMaskFill extends MaskFill
/*     */   {
/*     */     MaskFill target;
/*     */     MaskFill fillPgramTarget;
/*     */     MaskFill drawPgramTarget;
/*     */ 
/*     */     public TraceMaskFill(MaskFill paramMaskFill)
/*     */     {
/* 218 */       super(paramMaskFill.getCompositeType(), paramMaskFill.getDestType());
/*     */ 
/* 221 */       this.target = paramMaskFill;
/* 222 */       this.fillPgramTarget = new MaskFill(fillPgramSignature, paramMaskFill.getSourceType(), paramMaskFill.getCompositeType(), paramMaskFill.getDestType());
/*     */ 
/* 226 */       this.drawPgramTarget = new MaskFill(drawPgramSignature, paramMaskFill.getSourceType(), paramMaskFill.getCompositeType(), paramMaskFill.getDestType());
/*     */     }
/*     */ 
/*     */     public GraphicsPrimitive traceWrap()
/*     */     {
/* 233 */       return this;
/*     */     }
/*     */ 
/*     */     public void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
/*     */     {
/* 241 */       tracePrimitive(this.target);
/* 242 */       this.target.MaskFill(paramSunGraphics2D, paramSurfaceData, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5, paramInt6);
/*     */     }
/*     */ 
/*     */     public void FillAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
/*     */     {
/* 252 */       tracePrimitive(this.fillPgramTarget);
/* 253 */       this.target.FillAAPgram(paramSunGraphics2D, paramSurfaceData, paramComposite, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6);
/*     */     }
/*     */ 
/*     */     public void DrawAAPgram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8)
/*     */     {
/* 264 */       tracePrimitive(this.drawPgramTarget);
/* 265 */       this.target.DrawAAPgram(paramSunGraphics2D, paramSurfaceData, paramComposite, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8);
/*     */     }
/*     */ 
/*     */     public boolean canDoParallelograms()
/*     */     {
/* 270 */       return this.target.canDoParallelograms();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.loops.MaskFill
 * JD-Core Version:    0.6.2
 */