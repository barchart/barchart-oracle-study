/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Arc2D.Float;
/*     */ import java.awt.geom.Ellipse2D.Float;
/*     */ import java.awt.geom.Path2D.Float;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.awt.geom.RoundRectangle2D.Float;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.loops.ProcessPath;
/*     */ import sun.java2d.loops.ProcessPath.DrawHandler;
/*     */ import sun.java2d.pipe.LoopPipe;
/*     */ import sun.java2d.pipe.PixelDrawPipe;
/*     */ import sun.java2d.pipe.PixelFillPipe;
/*     */ import sun.java2d.pipe.Region;
/*     */ import sun.java2d.pipe.ShapeDrawPipe;
/*     */ import sun.java2d.pipe.ShapeSpanIterator;
/*     */ import sun.java2d.pipe.SpanIterator;
/*     */ 
/*     */ public class XRRenderer
/*     */   implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe
/*     */ {
/*     */   XRDrawHandler drawHandler;
/*     */   MaskTileManager tileManager;
/*     */ 
/*     */   public XRRenderer(MaskTileManager paramMaskTileManager)
/*     */   {
/*  56 */     this.tileManager = paramMaskTileManager;
/*  57 */     this.drawHandler = new XRDrawHandler();
/*     */   }
/*     */ 
/*     */   private final void validateSurface(SunGraphics2D paramSunGraphics2D)
/*     */   {
/*  65 */     XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/*  66 */     localXRSurfaceData.validateAsDestination(paramSunGraphics2D, paramSunGraphics2D.getCompClip());
/*  67 */     localXRSurfaceData.maskBuffer.validateCompositeState(paramSunGraphics2D.composite, paramSunGraphics2D.transform, paramSunGraphics2D.paint, paramSunGraphics2D);
/*     */   }
/*     */ 
/*     */   public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*     */     try {
/*  73 */       SunToolkit.awtLock();
/*     */ 
/*  75 */       validateSurface(paramSunGraphics2D);
/*  76 */       int i = paramSunGraphics2D.transX;
/*  77 */       int j = paramSunGraphics2D.transY;
/*     */ 
/*  79 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/*     */ 
/*  81 */       this.tileManager.addLine(paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
/*     */ 
/*  83 */       this.tileManager.fillMask(localXRSurfaceData);
/*     */     } finally {
/*  85 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  91 */     draw(paramSunGraphics2D, new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/*     */   }
/*     */ 
/*     */   public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/*  96 */     Path2D.Float localFloat = new Path2D.Float();
/*  97 */     if (paramInt > 1) {
/*  98 */       localFloat.moveTo(paramArrayOfInt1[0], paramArrayOfInt2[0]);
/*  99 */       for (int i = 1; i < paramInt; i++) {
/* 100 */         localFloat.lineTo(paramArrayOfInt1[i], paramArrayOfInt2[i]);
/*     */       }
/*     */     }
/*     */ 
/* 104 */     draw(paramSunGraphics2D, localFloat);
/*     */   }
/*     */ 
/*     */   public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 109 */     draw(paramSunGraphics2D, new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt));
/*     */   }
/*     */ 
/*     */   public synchronized void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 114 */     SunToolkit.awtLock();
/*     */     try {
/* 116 */       validateSurface(paramSunGraphics2D);
/*     */ 
/* 118 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.surfaceData;
/*     */ 
/* 120 */       paramInt1 = (int)(paramInt1 + paramSunGraphics2D.transform.getTranslateX());
/* 121 */       paramInt2 = (int)(paramInt2 + paramSunGraphics2D.transform.getTranslateY());
/*     */ 
/* 123 */       this.tileManager.addRect(paramInt1, paramInt2, paramInt3, paramInt4);
/* 124 */       this.tileManager.fillMask(localXRSurfaceData);
/*     */     }
/*     */     finally {
/* 127 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 133 */     fill(paramSunGraphics2D, new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt));
/*     */   }
/*     */ 
/*     */   public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 139 */     draw(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
/*     */   }
/*     */ 
/*     */   public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 146 */     fill(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
/*     */   }
/*     */ 
/*     */   public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 152 */     draw(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/*     */   }
/*     */ 
/*     */   public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 157 */     fill(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/*     */   }
/*     */ 
/*     */   public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 163 */     draw(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0));
/*     */   }
/*     */ 
/*     */   public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 170 */     fill(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2));
/*     */   }
/*     */ 
/*     */   protected void drawPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2)
/*     */   {
/* 208 */     SunToolkit.awtLock();
/*     */     try {
/* 210 */       validateSurface(paramSunGraphics2D);
/* 211 */       this.drawHandler.validate(paramSunGraphics2D);
/* 212 */       ProcessPath.drawPath(this.drawHandler, paramFloat, paramInt1, paramInt2);
/* 213 */       this.tileManager.fillMask((XRSurfaceData)paramSunGraphics2D.surfaceData);
/*     */     } finally {
/* 215 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void fillPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2)
/*     */   {
/* 221 */     SunToolkit.awtLock();
/*     */     try {
/* 223 */       validateSurface(paramSunGraphics2D);
/* 224 */       this.drawHandler.validate(paramSunGraphics2D);
/* 225 */       ProcessPath.fillPath(this.drawHandler, paramFloat, paramInt1, paramInt2);
/* 226 */       this.tileManager.fillMask((XRSurfaceData)paramSunGraphics2D.surfaceData);
/*     */     } finally {
/* 228 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void fillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator, int paramInt1, int paramInt2)
/*     */   {
/* 234 */     SunToolkit.awtLock();
/*     */     try {
/* 236 */       validateSurface(paramSunGraphics2D);
/* 237 */       int[] arrayOfInt = new int[4];
/* 238 */       while (paramSpanIterator.nextSpan(arrayOfInt)) {
/* 239 */         this.tileManager.addRect(arrayOfInt[0] + paramInt1, arrayOfInt[1] + paramInt2, arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
/*     */       }
/*     */ 
/* 244 */       this.tileManager.fillMask((XRSurfaceData)paramSunGraphics2D.surfaceData);
/*     */     } finally {
/* 246 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
/*     */   {
/*     */     Object localObject1;
/* 251 */     if (paramSunGraphics2D.strokeState == 0)
/*     */     {
/*     */       int i;
/*     */       int j;
/* 254 */       if (paramSunGraphics2D.transformState <= 1) {
/* 255 */         if ((paramShape instanceof Path2D.Float))
/* 256 */           localObject1 = (Path2D.Float)paramShape;
/*     */         else {
/* 258 */           localObject1 = new Path2D.Float(paramShape);
/*     */         }
/* 260 */         i = paramSunGraphics2D.transX;
/* 261 */         j = paramSunGraphics2D.transY;
/*     */       } else {
/* 263 */         localObject1 = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
/* 264 */         i = 0;
/* 265 */         j = 0;
/*     */       }
/* 267 */       drawPath(paramSunGraphics2D, (Path2D.Float)localObject1, i, j);
/* 268 */     } else if (paramSunGraphics2D.strokeState < 3) {
/* 269 */       localObject1 = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
/*     */       try {
/* 271 */         fillSpans(paramSunGraphics2D, (SpanIterator)localObject1, 0, 0);
/*     */       } finally {
/* 273 */         ((ShapeSpanIterator)localObject1).dispose();
/*     */       }
/*     */     } else {
/* 276 */       fill(paramSunGraphics2D, paramSunGraphics2D.stroke.createStrokedShape(paramShape));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
/*     */   {
/*     */     Object localObject1;
/*     */     int i;
/*     */     int j;
/* 283 */     if (paramSunGraphics2D.strokeState == 0)
/*     */     {
/* 287 */       if (paramSunGraphics2D.transformState <= 1) {
/* 288 */         if ((paramShape instanceof Path2D.Float))
/* 289 */           localObject1 = (Path2D.Float)paramShape;
/*     */         else {
/* 291 */           localObject1 = new Path2D.Float(paramShape);
/*     */         }
/* 293 */         i = paramSunGraphics2D.transX;
/* 294 */         j = paramSunGraphics2D.transY;
/*     */       } else {
/* 296 */         localObject1 = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
/* 297 */         i = 0;
/* 298 */         j = 0;
/*     */       }
/* 300 */       fillPath(paramSunGraphics2D, (Path2D.Float)localObject1, i, j);
/* 301 */       return;
/*     */     }
/*     */ 
/* 305 */     if (paramSunGraphics2D.transformState <= 1)
/*     */     {
/* 307 */       localObject1 = null;
/* 308 */       i = paramSunGraphics2D.transX;
/* 309 */       j = paramSunGraphics2D.transY;
/*     */     }
/*     */     else {
/* 312 */       localObject1 = paramSunGraphics2D.transform;
/* 313 */       i = j = 0;
/*     */     }
/*     */ 
/* 316 */     ShapeSpanIterator localShapeSpanIterator = LoopPipe.getFillSSI(paramSunGraphics2D);
/*     */     try
/*     */     {
/* 320 */       Region localRegion = paramSunGraphics2D.getCompClip();
/* 321 */       localShapeSpanIterator.setOutputAreaXYXY(localRegion.getLoX() - i, localRegion.getLoY() - j, localRegion.getHiX() - i, localRegion.getHiY() - j);
/*     */ 
/* 325 */       localShapeSpanIterator.appendPath(paramShape.getPathIterator((AffineTransform)localObject1));
/* 326 */       fillSpans(paramSunGraphics2D, localShapeSpanIterator, i, j);
/*     */     } finally {
/* 328 */       localShapeSpanIterator.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class XRDrawHandler extends ProcessPath.DrawHandler
/*     */   {
/*     */     XRDrawHandler()
/*     */     {
/* 179 */       super(0, 0, 0);
/*     */     }
/*     */ 
/*     */     void validate(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 187 */       Region localRegion = paramSunGraphics2D.getCompClip();
/* 188 */       setBounds(localRegion.getLoX(), localRegion.getLoY(), localRegion.getHiX(), localRegion.getHiY(), paramSunGraphics2D.strokeHint);
/*     */ 
/* 190 */       XRRenderer.this.validateSurface(paramSunGraphics2D);
/*     */     }
/*     */ 
/*     */     public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 194 */       XRRenderer.this.tileManager.addLine(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     public void drawPixel(int paramInt1, int paramInt2) {
/* 198 */       XRRenderer.this.tileManager.addRect(paramInt1, paramInt2, 1, 1);
/*     */     }
/*     */ 
/*     */     public void drawScanline(int paramInt1, int paramInt2, int paramInt3) {
/* 202 */       XRRenderer.this.tileManager.addRect(paramInt1, paramInt3, paramInt2 - paramInt1 + 1, 1);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRRenderer
 * JD-Core Version:    0.6.2
 */