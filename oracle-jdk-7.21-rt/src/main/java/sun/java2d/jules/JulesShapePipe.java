/*    */ package sun.java2d.jules;
/*    */ 
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Shape;
/*    */ import java.awt.Stroke;
/*    */ import sun.awt.SunToolkit;
/*    */ import sun.java2d.SunGraphics2D;
/*    */ import sun.java2d.pipe.ShapeDrawPipe;
/*    */ import sun.java2d.xr.XRCompositeManager;
/*    */ import sun.java2d.xr.XRSurfaceData;
/*    */ 
/*    */ public class JulesShapePipe
/*    */   implements ShapeDrawPipe
/*    */ {
/*    */   XRCompositeManager compMan;
/* 37 */   JulesPathBuf buf = new JulesPathBuf();
/*    */ 
/*    */   public JulesShapePipe(XRCompositeManager paramXRCompositeManager) {
/* 40 */     this.compMan = paramXRCompositeManager;
/*    */   }
/*    */ 
/*    */   private final void validateSurface(SunGraphics2D paramSunGraphics2D)
/*    */   {
/* 48 */     XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/* 49 */     localXRSurfaceData.validateAsDestination(paramSunGraphics2D, paramSunGraphics2D.getCompClip());
/* 50 */     localXRSurfaceData.maskBuffer.validateCompositeState(paramSunGraphics2D.composite, paramSunGraphics2D.transform, paramSunGraphics2D.paint, paramSunGraphics2D);
/*    */   }
/*    */ 
/*    */   public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
/*    */   {
/*    */     try {
/* 56 */       SunToolkit.awtLock();
/* 57 */       validateSurface(paramSunGraphics2D);
/* 58 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/*    */       BasicStroke localBasicStroke;
/* 62 */       if ((paramSunGraphics2D.stroke instanceof BasicStroke)) {
/* 63 */         localBasicStroke = (BasicStroke)paramSunGraphics2D.stroke;
/*    */       } else {
/* 65 */         paramShape = paramSunGraphics2D.stroke.createStrokedShape(paramShape);
/* 66 */         localBasicStroke = null;
/*    */       }
/*    */ 
/* 69 */       boolean bool1 = (localBasicStroke != null) && (paramSunGraphics2D.strokeHint != 2);
/*    */ 
/* 71 */       boolean bool2 = paramSunGraphics2D.strokeState <= 1;
/*    */ 
/* 73 */       TrapezoidList localTrapezoidList = this.buf.tesselateStroke(paramShape, localBasicStroke, bool2, bool1, true, paramSunGraphics2D.transform, paramSunGraphics2D.getCompClip());
/*    */ 
/* 76 */       this.compMan.XRCompositeTraps(localXRSurfaceData.picture, paramSunGraphics2D.transX, paramSunGraphics2D.transY, localTrapezoidList);
/*    */ 
/* 79 */       this.buf.clear();
/*    */     }
/*    */     finally {
/* 82 */       SunToolkit.awtUnlock();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
/*    */     try {
/* 88 */       SunToolkit.awtLock();
/* 89 */       validateSurface(paramSunGraphics2D);
/*    */ 
/* 91 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/*    */ 
/* 93 */       TrapezoidList localTrapezoidList = this.buf.tesselateFill(paramShape, paramSunGraphics2D.transform, paramSunGraphics2D.getCompClip());
/*    */ 
/* 95 */       this.compMan.XRCompositeTraps(localXRSurfaceData.picture, 0, 0, localTrapezoidList);
/*    */ 
/* 97 */       this.buf.clear();
/*    */     } finally {
/* 99 */       SunToolkit.awtUnlock();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.jules.JulesShapePipe
 * JD-Core Version:    0.6.2
 */