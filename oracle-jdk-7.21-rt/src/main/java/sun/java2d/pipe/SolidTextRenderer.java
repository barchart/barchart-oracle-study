/*    */ package sun.java2d.pipe;
/*    */ 
/*    */ import sun.font.GlyphList;
/*    */ import sun.java2d.SunGraphics2D;
/*    */ import sun.java2d.loops.DrawGlyphList;
/*    */ import sun.java2d.loops.RenderLoops;
/*    */ 
/*    */ public class SolidTextRenderer extends GlyphListLoopPipe
/*    */   implements LoopBasedPipe
/*    */ {
/*    */   protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
/*    */   {
/* 42 */     paramSunGraphics2D.loops.drawGlyphListLoop.DrawGlyphList(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pipe.SolidTextRenderer
 * JD-Core Version:    0.6.2
 */