/*     */ package sun.java2d.x11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Transparency;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.SurfaceDataProxy;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ 
/*     */ public abstract class X11SurfaceDataProxy extends SurfaceDataProxy
/*     */   implements Transparency
/*     */ {
/*     */   X11GraphicsConfig x11gc;
/*     */ 
/*     */   public static SurfaceDataProxy createProxy(SurfaceData paramSurfaceData, X11GraphicsConfig paramX11GraphicsConfig)
/*     */   {
/*  54 */     if ((paramSurfaceData instanceof X11SurfaceData))
/*     */     {
/*  57 */       return UNCACHED;
/*     */     }
/*     */ 
/*  60 */     ColorModel localColorModel = paramSurfaceData.getColorModel();
/*  61 */     int i = localColorModel.getTransparency();
/*     */ 
/*  63 */     if (i == 1)
/*  64 */       return new Opaque(paramX11GraphicsConfig);
/*  65 */     if (i == 2)
/*     */     {
/*  67 */       if (((localColorModel instanceof IndexColorModel)) && (localColorModel.getPixelSize() == 8)) {
/*  68 */         return new Bitmask(paramX11GraphicsConfig);
/*     */       }
/*     */ 
/*  73 */       if ((localColorModel instanceof DirectColorModel)) {
/*  74 */         DirectColorModel localDirectColorModel = (DirectColorModel)localColorModel;
/*  75 */         int j = localDirectColorModel.getRedMask() | localDirectColorModel.getGreenMask() | localDirectColorModel.getBlueMask();
/*     */ 
/*  78 */         int k = localDirectColorModel.getAlphaMask();
/*     */ 
/*  80 */         if (((j & 0xFF000000) == 0) && ((k & 0xFF000000) != 0))
/*     */         {
/*  83 */           return new Bitmask(paramX11GraphicsConfig);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  90 */     return UNCACHED;
/*     */   }
/*     */ 
/*     */   public X11SurfaceDataProxy(X11GraphicsConfig paramX11GraphicsConfig)
/*     */   {
/*  96 */     this.x11gc = paramX11GraphicsConfig;
/*     */   }
/*     */ 
/*     */   public SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2)
/*     */   {
/* 104 */     if (paramSurfaceData2 == null)
/*     */     {
/* 106 */       paramSurfaceData2 = X11SurfaceData.createData(this.x11gc, paramInt1, paramInt2, this.x11gc.getColorModel(), null, 0L, getTransparency());
/*     */     }
/*     */ 
/* 110 */     return paramSurfaceData2;
/*     */   }
/*     */ 
/*     */   public static class Bitmask extends X11SurfaceDataProxy
/*     */   {
/*     */     public Bitmask(X11GraphicsConfig paramX11GraphicsConfig)
/*     */     {
/* 145 */       super();
/*     */     }
/*     */ 
/*     */     public int getTransparency() {
/* 149 */       return 2;
/*     */     }
/*     */ 
/*     */     public boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor)
/*     */     {
/* 163 */       if (paramInt >= 3) {
/* 164 */         return false;
/*     */       }
/*     */ 
/* 167 */       if ((paramColor != null) && (paramColor.getTransparency() != 1))
/*     */       {
/* 170 */         return false;
/*     */       }
/*     */ 
/* 177 */       if ((CompositeType.SrcOverNoEa.equals(paramCompositeType)) || ((CompositeType.SrcNoEa.equals(paramCompositeType)) && (paramColor != null)))
/*     */       {
/* 181 */         return true;
/*     */       }
/*     */ 
/* 184 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Opaque extends X11SurfaceDataProxy
/*     */   {
/*     */     public Opaque(X11GraphicsConfig paramX11GraphicsConfig)
/*     */     {
/* 119 */       super();
/*     */     }
/*     */ 
/*     */     public int getTransparency() {
/* 123 */       return 1;
/*     */     }
/*     */ 
/*     */     public boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor)
/*     */     {
/* 132 */       return (paramInt < 3) && ((CompositeType.SrcOverNoEa.equals(paramCompositeType)) || (CompositeType.SrcNoEa.equals(paramCompositeType)));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.x11.X11SurfaceDataProxy
 * JD-Core Version:    0.6.2
 */