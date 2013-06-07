/*    */ package sun.java2d.jules;
/*    */ 
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.AffineTransform;
/*    */ import sun.java2d.pipe.AATileGenerator;
/*    */ import sun.java2d.pipe.Region;
/*    */ import sun.java2d.pisces.PiscesRenderingEngine;
/*    */ 
/*    */ public class JulesRenderingEngine extends PiscesRenderingEngine
/*    */ {
/*    */   public AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt)
/*    */   {
/* 42 */     if (JulesPathBuf.isCairoAvailable()) {
/* 43 */       return new JulesAATileGenerator(paramShape, paramAffineTransform, paramRegion, paramBasicStroke, paramBoolean1, paramBoolean2, paramArrayOfInt);
/*    */     }
/*    */ 
/* 46 */     return super.getAATileGenerator(paramShape, paramAffineTransform, paramRegion, paramBasicStroke, paramBoolean1, paramBoolean2, paramArrayOfInt);
/*    */   }
/*    */ 
/*    */   public float getMinimumAAPenSize()
/*    */   {
/* 52 */     return 0.5F;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.jules.JulesRenderingEngine
 * JD-Core Version:    0.6.2
 */