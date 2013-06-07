/*     */ package sun.java2d.x11;
/*     */ 
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Path2D.Float;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.GraphicsPrimitive;
/*     */ import sun.java2d.pipe.LoopPipe;
/*     */ import sun.java2d.pipe.PixelDrawPipe;
/*     */ import sun.java2d.pipe.PixelFillPipe;
/*     */ import sun.java2d.pipe.Region;
/*     */ import sun.java2d.pipe.ShapeDrawPipe;
/*     */ import sun.java2d.pipe.ShapeSpanIterator;
/*     */ import sun.java2d.pipe.SpanIterator;
/*     */ 
/*     */ public class X11Renderer
/*     */   implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe
/*     */ {
/*     */   public static X11Renderer getInstance()
/*     */   {
/*  52 */     return GraphicsPrimitive.tracingEnabled() ? new X11TracingRenderer() : new X11Renderer();
/*     */   }
/*     */ 
/*     */   private final long validate(SunGraphics2D paramSunGraphics2D)
/*     */   {
/*  75 */     X11SurfaceData localX11SurfaceData = (X11SurfaceData)paramSunGraphics2D.surfaceData;
/*  76 */     return localX11SurfaceData.getRenderGC(paramSunGraphics2D.getCompClip(), paramSunGraphics2D.compositeState, paramSunGraphics2D.composite, paramSunGraphics2D.pixel);
/*     */   }
/*     */ 
/*     */   native void XDrawLine(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  85 */     SunToolkit.awtLock();
/*     */     try {
/*  87 */       long l = validate(paramSunGraphics2D);
/*  88 */       int i = paramSunGraphics2D.transX;
/*  89 */       int j = paramSunGraphics2D.transY;
/*  90 */       XDrawLine(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
/*     */     }
/*     */     finally {
/*  93 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XDrawRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 103 */     SunToolkit.awtLock();
/*     */     try {
/* 105 */       long l = validate(paramSunGraphics2D);
/* 106 */       XDrawRect(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
/*     */     }
/*     */     finally {
/* 109 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XDrawRoundRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 121 */     SunToolkit.awtLock();
/*     */     try {
/* 123 */       long l = validate(paramSunGraphics2D);
/* 124 */       XDrawRoundRect(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */     finally
/*     */     {
/* 128 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XDrawOval(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 138 */     SunToolkit.awtLock();
/*     */     try {
/* 140 */       long l = validate(paramSunGraphics2D);
/* 141 */       XDrawOval(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
/*     */     }
/*     */     finally {
/* 144 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XDrawArc(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 156 */     SunToolkit.awtLock();
/*     */     try {
/* 158 */       long l = validate(paramSunGraphics2D);
/* 159 */       XDrawArc(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */     finally
/*     */     {
/* 163 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XDrawPoly(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3, boolean paramBoolean);
/*     */ 
/*     */   public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 176 */     SunToolkit.awtLock();
/*     */     try {
/* 178 */       long l = validate(paramSunGraphics2D);
/* 179 */       XDrawPoly(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramSunGraphics2D.transX, paramSunGraphics2D.transY, paramArrayOfInt1, paramArrayOfInt2, paramInt, false);
/*     */     }
/*     */     finally
/*     */     {
/* 183 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 191 */     SunToolkit.awtLock();
/*     */     try {
/* 193 */       long l = validate(paramSunGraphics2D);
/* 194 */       XDrawPoly(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramSunGraphics2D.transX, paramSunGraphics2D.transY, paramArrayOfInt1, paramArrayOfInt2, paramInt, true);
/*     */     }
/*     */     finally
/*     */     {
/* 198 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XFillRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 208 */     SunToolkit.awtLock();
/*     */     try {
/* 210 */       long l = validate(paramSunGraphics2D);
/* 211 */       XFillRect(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
/*     */     }
/*     */     finally {
/* 214 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XFillRoundRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 226 */     SunToolkit.awtLock();
/*     */     try {
/* 228 */       long l = validate(paramSunGraphics2D);
/* 229 */       XFillRoundRect(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */     finally
/*     */     {
/* 233 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XFillOval(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 243 */     SunToolkit.awtLock();
/*     */     try {
/* 245 */       long l = validate(paramSunGraphics2D);
/* 246 */       XFillOval(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
/*     */     }
/*     */     finally {
/* 249 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XFillArc(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 261 */     SunToolkit.awtLock();
/*     */     try {
/* 263 */       long l = validate(paramSunGraphics2D);
/* 264 */       XFillArc(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */     finally
/*     */     {
/* 268 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XFillPoly(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3);
/*     */ 
/*     */   public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 281 */     SunToolkit.awtLock();
/*     */     try {
/* 283 */       long l = validate(paramSunGraphics2D);
/* 284 */       XFillPoly(paramSunGraphics2D.surfaceData.getNativeOps(), l, paramSunGraphics2D.transX, paramSunGraphics2D.transY, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*     */     }
/*     */     finally {
/* 287 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void XFillSpans(long paramLong1, long paramLong2, SpanIterator paramSpanIterator, long paramLong3, int paramInt1, int paramInt2);
/*     */ 
/*     */   native void XDoPath(SunGraphics2D paramSunGraphics2D, long paramLong1, long paramLong2, int paramInt1, int paramInt2, Path2D.Float paramFloat, boolean paramBoolean);
/*     */ 
/*     */   private void doPath(SunGraphics2D paramSunGraphics2D, Shape paramShape, boolean paramBoolean)
/*     */   {
/*     */     Path2D.Float localFloat;
/*     */     int i;
/*     */     int j;
/* 302 */     if (paramSunGraphics2D.transformState <= 1) {
/* 303 */       if ((paramShape instanceof Path2D.Float))
/* 304 */         localFloat = (Path2D.Float)paramShape;
/*     */       else {
/* 306 */         localFloat = new Path2D.Float(paramShape);
/*     */       }
/* 308 */       i = paramSunGraphics2D.transX;
/* 309 */       j = paramSunGraphics2D.transY;
/*     */     } else {
/* 311 */       localFloat = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
/* 312 */       i = 0;
/* 313 */       j = 0;
/*     */     }
/* 315 */     SunToolkit.awtLock();
/*     */     try {
/* 317 */       long l = validate(paramSunGraphics2D);
/* 318 */       XDoPath(paramSunGraphics2D, paramSunGraphics2D.surfaceData.getNativeOps(), l, i, j, localFloat, paramBoolean);
/*     */     }
/*     */     finally {
/* 321 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
/*     */   {
/*     */     Object localObject1;
/* 326 */     if (paramSunGraphics2D.strokeState == 0)
/*     */     {
/* 328 */       if (((paramShape instanceof Polygon)) && (paramSunGraphics2D.transformState < 3))
/*     */       {
/* 331 */         localObject1 = (Polygon)paramShape;
/* 332 */         drawPolygon(paramSunGraphics2D, ((Polygon)localObject1).xpoints, ((Polygon)localObject1).ypoints, ((Polygon)localObject1).npoints);
/* 333 */         return;
/*     */       }
/*     */ 
/* 338 */       doPath(paramSunGraphics2D, paramShape, false);
/* 339 */     } else if (paramSunGraphics2D.strokeState < 3)
/*     */     {
/* 343 */       localObject1 = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
/*     */       try {
/* 345 */         SunToolkit.awtLock();
/*     */         try {
/* 347 */           long l = validate(paramSunGraphics2D);
/* 348 */           XFillSpans(paramSunGraphics2D.surfaceData.getNativeOps(), l, (SpanIterator)localObject1, ((ShapeSpanIterator)localObject1).getNativeIterator(), 0, 0);
/*     */         }
/*     */         finally
/*     */         {
/* 352 */           SunToolkit.awtUnlock();
/*     */         }
/*     */       } finally {
/* 355 */         ((ShapeSpanIterator)localObject1).dispose();
/*     */       }
/*     */     } else {
/* 358 */       fill(paramSunGraphics2D, paramSunGraphics2D.stroke.createStrokedShape(paramShape));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
/*     */   {
/*     */     Object localObject1;
/* 363 */     if (paramSunGraphics2D.strokeState == 0)
/*     */     {
/* 365 */       if (((paramShape instanceof Polygon)) && (paramSunGraphics2D.transformState < 3))
/*     */       {
/* 368 */         localObject1 = (Polygon)paramShape;
/* 369 */         fillPolygon(paramSunGraphics2D, ((Polygon)localObject1).xpoints, ((Polygon)localObject1).ypoints, ((Polygon)localObject1).npoints);
/* 370 */         return;
/*     */       }
/*     */ 
/* 375 */       doPath(paramSunGraphics2D, paramShape, true);
/*     */       return;
/*     */     }
/*     */     int i;
/*     */     int j;
/* 381 */     if (paramSunGraphics2D.transformState < 3)
/*     */     {
/* 383 */       localObject1 = null;
/* 384 */       i = paramSunGraphics2D.transX;
/* 385 */       j = paramSunGraphics2D.transY;
/*     */     }
/*     */     else {
/* 388 */       localObject1 = paramSunGraphics2D.transform;
/* 389 */       i = j = 0;
/*     */     }
/*     */ 
/* 392 */     ShapeSpanIterator localShapeSpanIterator = LoopPipe.getFillSSI(paramSunGraphics2D);
/*     */     try
/*     */     {
/* 396 */       Region localRegion = paramSunGraphics2D.getCompClip();
/* 397 */       localShapeSpanIterator.setOutputAreaXYXY(localRegion.getLoX() - i, localRegion.getLoY() - j, localRegion.getHiX() - i, localRegion.getHiY() - j);
/*     */ 
/* 401 */       localShapeSpanIterator.appendPath(paramShape.getPathIterator((AffineTransform)localObject1));
/* 402 */       SunToolkit.awtLock();
/*     */       try {
/* 404 */         long l = validate(paramSunGraphics2D);
/* 405 */         XFillSpans(paramSunGraphics2D.surfaceData.getNativeOps(), l, localShapeSpanIterator, localShapeSpanIterator.getNativeIterator(), i, j);
/*     */       }
/*     */       finally
/*     */       {
/* 409 */         SunToolkit.awtUnlock();
/*     */       }
/*     */     } finally {
/* 412 */       localShapeSpanIterator.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   native void devCopyArea(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ 
/*     */   public static class X11TracingRenderer extends X11Renderer
/*     */   {
/*     */     void XDrawLine(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 425 */       GraphicsPrimitive.tracePrimitive("X11DrawLine");
/* 426 */       super.XDrawLine(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     void XDrawRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 431 */       GraphicsPrimitive.tracePrimitive("X11DrawRect");
/* 432 */       super.XDrawRect(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     void XDrawRoundRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */     {
/* 438 */       GraphicsPrimitive.tracePrimitive("X11DrawRoundRect");
/* 439 */       super.XDrawRoundRect(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */ 
/*     */     void XDrawOval(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 444 */       GraphicsPrimitive.tracePrimitive("X11DrawOval");
/* 445 */       super.XDrawOval(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     void XDrawArc(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */     {
/* 451 */       GraphicsPrimitive.tracePrimitive("X11DrawArc");
/* 452 */       super.XDrawArc(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */ 
/*     */     void XDrawPoly(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3, boolean paramBoolean)
/*     */     {
/* 460 */       GraphicsPrimitive.tracePrimitive("X11DrawPoly");
/* 461 */       super.XDrawPoly(paramLong1, paramLong2, paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3, paramBoolean);
/*     */     }
/*     */ 
/*     */     void XDoPath(SunGraphics2D paramSunGraphics2D, long paramLong1, long paramLong2, int paramInt1, int paramInt2, Path2D.Float paramFloat, boolean paramBoolean)
/*     */     {
/* 468 */       GraphicsPrimitive.tracePrimitive(paramBoolean ? "X11FillPath" : "X11DrawPath");
/*     */ 
/* 471 */       super.XDoPath(paramSunGraphics2D, paramLong1, paramLong2, paramInt1, paramInt2, paramFloat, paramBoolean);
/*     */     }
/*     */ 
/*     */     void XFillRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 476 */       GraphicsPrimitive.tracePrimitive("X11FillRect");
/* 477 */       super.XFillRect(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     void XFillRoundRect(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */     {
/* 483 */       GraphicsPrimitive.tracePrimitive("X11FillRoundRect");
/* 484 */       super.XFillRoundRect(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */ 
/*     */     void XFillOval(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     {
/* 489 */       GraphicsPrimitive.tracePrimitive("X11FillOval");
/* 490 */       super.XFillOval(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */ 
/*     */     void XFillArc(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */     {
/* 496 */       GraphicsPrimitive.tracePrimitive("X11FillArc");
/* 497 */       super.XFillArc(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */ 
/*     */     void XFillPoly(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3)
/*     */     {
/* 505 */       GraphicsPrimitive.tracePrimitive("X11FillPoly");
/* 506 */       super.XFillPoly(paramLong1, paramLong2, paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3);
/*     */     }
/*     */ 
/*     */     void XFillSpans(long paramLong1, long paramLong2, SpanIterator paramSpanIterator, long paramLong3, int paramInt1, int paramInt2)
/*     */     {
/* 512 */       GraphicsPrimitive.tracePrimitive("X11FillSpans");
/* 513 */       super.XFillSpans(paramLong1, paramLong2, paramSpanIterator, paramLong3, paramInt1, paramInt2);
/*     */     }
/*     */ 
/*     */     void devCopyArea(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */     {
/* 521 */       GraphicsPrimitive.tracePrimitive("X11CopyArea");
/* 522 */       super.devCopyArea(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.x11.X11Renderer
 * JD-Core Version:    0.6.2
 */