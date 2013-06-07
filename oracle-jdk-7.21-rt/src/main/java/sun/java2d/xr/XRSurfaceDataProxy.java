/*    */ package sun.java2d.xr;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Transparency;
/*    */ import sun.java2d.SurfaceData;
/*    */ import sun.java2d.SurfaceDataProxy;
/*    */ import sun.java2d.loops.CompositeType;
/*    */ 
/*    */ public class XRSurfaceDataProxy extends SurfaceDataProxy
/*    */   implements Transparency
/*    */ {
/*    */   XRGraphicsConfig xrgc;
/*    */   int transparency;
/*    */ 
/*    */   public static SurfaceDataProxy createProxy(SurfaceData paramSurfaceData, XRGraphicsConfig paramXRGraphicsConfig)
/*    */   {
/* 44 */     if ((paramSurfaceData instanceof XRSurfaceData)) {
/* 45 */       return UNCACHED;
/*    */     }
/*    */ 
/* 48 */     return new XRSurfaceDataProxy(paramXRGraphicsConfig, paramSurfaceData.getTransparency());
/*    */   }
/*    */ 
/*    */   public XRSurfaceDataProxy(XRGraphicsConfig paramXRGraphicsConfig)
/*    */   {
/* 55 */     this.xrgc = paramXRGraphicsConfig;
/*    */   }
/*    */ 
/*    */   public SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2)
/*    */   {
/* 61 */     if (paramSurfaceData2 == null) {
/* 62 */       paramSurfaceData2 = XRSurfaceData.createData(this.xrgc, paramInt1, paramInt2, this.xrgc.getColorModel(), null, 0L, getTransparency());
/*    */     }
/*    */ 
/* 65 */     return paramSurfaceData2;
/*    */   }
/*    */ 
/*    */   public XRSurfaceDataProxy(XRGraphicsConfig paramXRGraphicsConfig, int paramInt) {
/* 69 */     this.xrgc = paramXRGraphicsConfig;
/* 70 */     this.transparency = paramInt;
/*    */   }
/*    */ 
/*    */   public boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor)
/*    */   {
/* 77 */     return (paramColor == null) || (this.transparency == 3);
/*    */   }
/*    */ 
/*    */   public int getTransparency() {
/* 81 */     return this.transparency;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRSurfaceDataProxy
 * JD-Core Version:    0.6.2
 */