/*    */ package sun.font;
/*    */ 
/*    */ import java.awt.font.FontRenderContext;
/*    */ import java.awt.font.GlyphVector;
/*    */ import sun.awt.SunToolkit;
/*    */ import sun.java2d.SunGraphics2D;
/*    */ import sun.java2d.SurfaceData;
/*    */ import sun.java2d.loops.FontInfo;
/*    */ import sun.java2d.loops.GraphicsPrimitive;
/*    */ import sun.java2d.pipe.GlyphListPipe;
/*    */ import sun.java2d.pipe.Region;
/*    */ import sun.java2d.pipe.TextPipe;
/*    */ import sun.java2d.x11.X11SurfaceData;
/*    */ 
/*    */ public class X11TextRenderer extends GlyphListPipe
/*    */ {
/*    */   public void drawGlyphVector(SunGraphics2D paramSunGraphics2D, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
/*    */   {
/* 53 */     FontRenderContext localFontRenderContext = paramGlyphVector.getFontRenderContext();
/* 54 */     FontInfo localFontInfo = paramSunGraphics2D.getGVFontInfo(paramGlyphVector.getFont(), localFontRenderContext);
/* 55 */     switch (localFontInfo.aaHint) {
/*    */     case 1:
/* 57 */       super.drawGlyphVector(paramSunGraphics2D, paramGlyphVector, paramFloat1, paramFloat2);
/* 58 */       return;
/*    */     case 2:
/* 60 */       SurfaceData.aaTextRenderer.drawGlyphVector(paramSunGraphics2D, paramGlyphVector, paramFloat1, paramFloat2);
/* 61 */       return;
/*    */     case 4:
/*    */     case 6:
/* 64 */       SurfaceData.lcdTextRenderer.drawGlyphVector(paramSunGraphics2D, paramGlyphVector, paramFloat1, paramFloat2);
/* 65 */       return;
/*    */     case 3:
/*    */     case 5:
/*    */     }
/*    */   }
/*    */ 
/*    */   native void doDrawGlyphList(long paramLong1, long paramLong2, Region paramRegion, GlyphList paramGlyphList);
/*    */ 
/*    */   protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList) {
/* 74 */     SunToolkit.awtLock();
/*    */     try {
/* 76 */       X11SurfaceData localX11SurfaceData = (X11SurfaceData)paramSunGraphics2D.surfaceData;
/* 77 */       Region localRegion = paramSunGraphics2D.getCompClip();
/* 78 */       long l = localX11SurfaceData.getRenderGC(localRegion, 0, null, paramSunGraphics2D.pixel);
/*    */ 
/* 80 */       doDrawGlyphList(localX11SurfaceData.getNativeOps(), l, localRegion, paramGlyphList);
/*    */     } finally {
/* 82 */       SunToolkit.awtUnlock();
/*    */     }
/*    */   }
/*    */ 
/*    */   public X11TextRenderer traceWrap() {
/* 87 */     return new Tracer();
/*    */   }
/*    */ 
/*    */   public static class Tracer extends X11TextRenderer
/*    */   {
/*    */     void doDrawGlyphList(long paramLong1, long paramLong2, Region paramRegion, GlyphList paramGlyphList)
/*    */     {
/* 94 */       GraphicsPrimitive.tracePrimitive("X11DrawGlyphs");
/* 95 */       super.doDrawGlyphList(paramLong1, paramLong2, paramRegion, paramGlyphList);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.X11TextRenderer
 * JD-Core Version:    0.6.2
 */