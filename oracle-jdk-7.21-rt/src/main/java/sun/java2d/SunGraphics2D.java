/*      */ package sun.java2d;
/*      */ 
/*      */ import java.awt.AlphaComposite;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Composite;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.GradientPaint;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.Image;
/*      */ import java.awt.LinearGradientPaint;
/*      */ import java.awt.Paint;
/*      */ import java.awt.RadialGradientPaint;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.RenderingHints.Key;
/*      */ import java.awt.Shape;
/*      */ import java.awt.Stroke;
/*      */ import java.awt.TexturePaint;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.font.GlyphVector;
/*      */ import java.awt.font.TextLayout;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Area;
/*      */ import java.awt.geom.GeneralPath;
/*      */ import java.awt.geom.NoninvertibleTransformException;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Double;
/*      */ import java.awt.geom.Rectangle2D.Float;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.BufferedImageOp;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.awt.image.renderable.RenderContext;
/*      */ import java.awt.image.renderable.RenderableImage;
/*      */ import java.text.AttributedCharacterIterator;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import sun.awt.ConstrainableGraphics;
/*      */ import sun.awt.SunHints;
/*      */ import sun.awt.SunHints.Key;
/*      */ import sun.awt.SunHints.Value;
/*      */ import sun.font.Font2D;
/*      */ import sun.font.FontDesignMetrics;
/*      */ import sun.font.FontUtilities;
/*      */ import sun.java2d.loops.Blit;
/*      */ import sun.java2d.loops.CompositeType;
/*      */ import sun.java2d.loops.FontInfo;
/*      */ import sun.java2d.loops.MaskFill;
/*      */ import sun.java2d.loops.RenderLoops;
/*      */ import sun.java2d.loops.SurfaceType;
/*      */ import sun.java2d.loops.XORComposite;
/*      */ import sun.java2d.pipe.DrawImagePipe;
/*      */ import sun.java2d.pipe.LoopPipe;
/*      */ import sun.java2d.pipe.PixelDrawPipe;
/*      */ import sun.java2d.pipe.PixelFillPipe;
/*      */ import sun.java2d.pipe.Region;
/*      */ import sun.java2d.pipe.RenderingEngine;
/*      */ import sun.java2d.pipe.ShapeDrawPipe;
/*      */ import sun.java2d.pipe.ShapeSpanIterator;
/*      */ import sun.java2d.pipe.TextPipe;
/*      */ import sun.java2d.pipe.ValidatePipe;
/*      */ import sun.misc.PerformanceLogger;
/*      */ 
/*      */ public final class SunGraphics2D extends Graphics2D
/*      */   implements ConstrainableGraphics, Cloneable, DestSurfaceProvider
/*      */ {
/*      */   public static final int PAINT_CUSTOM = 6;
/*      */   public static final int PAINT_TEXTURE = 5;
/*      */   public static final int PAINT_RAD_GRADIENT = 4;
/*      */   public static final int PAINT_LIN_GRADIENT = 3;
/*      */   public static final int PAINT_GRADIENT = 2;
/*      */   public static final int PAINT_ALPHACOLOR = 1;
/*      */   public static final int PAINT_OPAQUECOLOR = 0;
/*      */   public static final int COMP_CUSTOM = 3;
/*      */   public static final int COMP_XOR = 2;
/*      */   public static final int COMP_ALPHA = 1;
/*      */   public static final int COMP_ISCOPY = 0;
/*      */   public static final int STROKE_CUSTOM = 3;
/*      */   public static final int STROKE_WIDE = 2;
/*      */   public static final int STROKE_THINDASHED = 1;
/*      */   public static final int STROKE_THIN = 0;
/*      */   public static final int TRANSFORM_GENERIC = 4;
/*      */   public static final int TRANSFORM_TRANSLATESCALE = 3;
/*      */   public static final int TRANSFORM_ANY_TRANSLATE = 2;
/*      */   public static final int TRANSFORM_INT_TRANSLATE = 1;
/*      */   public static final int TRANSFORM_ISIDENT = 0;
/*      */   public static final int CLIP_SHAPE = 2;
/*      */   public static final int CLIP_RECTANGULAR = 1;
/*      */   public static final int CLIP_DEVICE = 0;
/*      */   public int eargb;
/*      */   public int pixel;
/*      */   public SurfaceData surfaceData;
/*      */   public PixelDrawPipe drawpipe;
/*      */   public PixelFillPipe fillpipe;
/*      */   public DrawImagePipe imagepipe;
/*      */   public ShapeDrawPipe shapepipe;
/*      */   public TextPipe textpipe;
/*      */   public MaskFill alphafill;
/*      */   public RenderLoops loops;
/*      */   public CompositeType imageComp;
/*      */   public int paintState;
/*      */   public int compositeState;
/*      */   public int strokeState;
/*      */   public int transformState;
/*      */   public int clipState;
/*      */   public Color foregroundColor;
/*      */   public Color backgroundColor;
/*      */   public AffineTransform transform;
/*      */   public int transX;
/*      */   public int transY;
/*  178 */   protected static final Stroke defaultStroke = new BasicStroke();
/*  179 */   protected static final Composite defaultComposite = AlphaComposite.SrcOver;
/*  180 */   private static final Font defaultFont = new Font("Dialog", 0, 12);
/*      */   public Paint paint;
/*      */   public Stroke stroke;
/*      */   public Composite composite;
/*      */   protected Font font;
/*      */   protected FontMetrics fontMetrics;
/*      */   public int renderHint;
/*      */   public int antialiasHint;
/*      */   public int textAntialiasHint;
/*      */   protected int fractionalMetricsHint;
/*      */   public int lcdTextContrast;
/*  196 */   private static int lcdTextContrastDefaultValue = 140;
/*      */   private int interpolationHint;
/*      */   public int strokeHint;
/*      */   public int interpolationType;
/*      */   public RenderingHints hints;
/*      */   public Region constrainClip;
/*      */   public int constrainX;
/*      */   public int constrainY;
/*      */   public Region clipRegion;
/*      */   public Shape usrClip;
/*      */   protected Region devClip;
/*      */   private boolean validFontInfo;
/*      */   private FontInfo fontInfo;
/*      */   private FontInfo glyphVectorFontInfo;
/*      */   private FontRenderContext glyphVectorFRC;
/*      */   private static final int slowTextTransformMask = 120;
/*      */   protected static ValidatePipe invalidpipe;
/*      */   private static final double[] IDENT_MATRIX;
/*      */   private static final AffineTransform IDENT_ATX;
/*      */   private static final int MINALLOCATED = 8;
/*      */   private static final int TEXTARRSIZE = 17;
/*      */   private static double[][] textTxArr;
/*      */   private static AffineTransform[] textAtArr;
/*      */   static final int NON_UNIFORM_SCALE_MASK = 36;
/*  974 */   public static final double MinPenSizeAA = RenderingEngine.getInstance().getMinimumAAPenSize();
/*      */ 
/*  976 */   public static final double MinPenSizeAASquared = MinPenSizeAA * MinPenSizeAA;
/*      */   public static final double MinPenSizeSquared = 1.000000001D;
/*      */   static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
/*      */   Blit lastCAblit;
/*      */   Composite lastCAcomp;
/*      */   private FontRenderContext cachedFRC;
/*      */ 
/*      */   public SunGraphics2D(SurfaceData paramSurfaceData, Color paramColor1, Color paramColor2, Font paramFont)
/*      */   {
/*  232 */     this.surfaceData = paramSurfaceData;
/*  233 */     this.foregroundColor = paramColor1;
/*  234 */     this.backgroundColor = paramColor2;
/*      */ 
/*  236 */     this.transform = new AffineTransform();
/*  237 */     this.stroke = defaultStroke;
/*  238 */     this.composite = defaultComposite;
/*  239 */     this.paint = this.foregroundColor;
/*      */ 
/*  241 */     this.imageComp = CompositeType.SrcOverNoEa;
/*      */ 
/*  243 */     this.renderHint = 0;
/*  244 */     this.antialiasHint = 1;
/*  245 */     this.textAntialiasHint = 0;
/*  246 */     this.fractionalMetricsHint = 1;
/*  247 */     this.lcdTextContrast = lcdTextContrastDefaultValue;
/*  248 */     this.interpolationHint = -1;
/*  249 */     this.strokeHint = 0;
/*      */ 
/*  251 */     this.interpolationType = 1;
/*      */ 
/*  253 */     validateColor();
/*      */ 
/*  255 */     this.font = paramFont;
/*  256 */     if (this.font == null) {
/*  257 */       this.font = defaultFont;
/*      */     }
/*      */ 
/*  260 */     setDevClip(paramSurfaceData.getBounds());
/*  261 */     invalidatePipe();
/*      */   }
/*      */ 
/*      */   protected Object clone() {
/*      */     try {
/*  266 */       SunGraphics2D localSunGraphics2D = (SunGraphics2D)super.clone();
/*  267 */       localSunGraphics2D.transform = new AffineTransform(this.transform);
/*  268 */       if (this.hints != null) {
/*  269 */         localSunGraphics2D.hints = ((RenderingHints)this.hints.clone());
/*      */       }
/*      */ 
/*  277 */       if (this.fontInfo != null) {
/*  278 */         if (this.validFontInfo)
/*  279 */           localSunGraphics2D.fontInfo = ((FontInfo)this.fontInfo.clone());
/*      */         else {
/*  281 */           localSunGraphics2D.fontInfo = null;
/*      */         }
/*      */       }
/*  284 */       if (this.glyphVectorFontInfo != null) {
/*  285 */         localSunGraphics2D.glyphVectorFontInfo = ((FontInfo)this.glyphVectorFontInfo.clone());
/*      */ 
/*  287 */         localSunGraphics2D.glyphVectorFRC = this.glyphVectorFRC;
/*      */       }
/*      */ 
/*  290 */       return localSunGraphics2D;
/*      */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*      */     }
/*  293 */     return null;
/*      */   }
/*      */ 
/*      */   public Graphics create()
/*      */   {
/*  300 */     return (Graphics)clone();
/*      */   }
/*      */ 
/*      */   public void setDevClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  304 */     Region localRegion = this.constrainClip;
/*  305 */     if (localRegion == null)
/*  306 */       this.devClip = Region.getInstanceXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     else {
/*  308 */       this.devClip = localRegion.getIntersectionXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*  310 */     validateCompClip();
/*      */   }
/*      */ 
/*      */   public void setDevClip(Rectangle paramRectangle) {
/*  314 */     setDevClip(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  330 */     if ((paramInt1 | paramInt2) != 0) {
/*  331 */       translate(paramInt1, paramInt2);
/*      */     }
/*  333 */     if (this.transformState >= 3) {
/*  334 */       clipRect(0, 0, paramInt3, paramInt4);
/*  335 */       return;
/*      */     }
/*  337 */     paramInt1 = this.constrainX = this.transX;
/*  338 */     paramInt2 = this.constrainY = this.transY;
/*  339 */     paramInt3 = Region.dimAdd(paramInt1, paramInt3);
/*  340 */     paramInt4 = Region.dimAdd(paramInt2, paramInt4);
/*  341 */     Region localRegion = this.constrainClip;
/*  342 */     if (localRegion == null) {
/*  343 */       localRegion = Region.getInstanceXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } else {
/*  345 */       localRegion = localRegion.getIntersectionXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
/*  346 */       if (localRegion == this.constrainClip)
/*      */       {
/*  348 */         return;
/*      */       }
/*      */     }
/*  351 */     this.constrainClip = localRegion;
/*  352 */     if (!this.devClip.isInsideQuickCheck(localRegion)) {
/*  353 */       this.devClip = this.devClip.getIntersection(localRegion);
/*  354 */       validateCompClip();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void invalidatePipe()
/*      */   {
/*  364 */     this.drawpipe = invalidpipe;
/*  365 */     this.fillpipe = invalidpipe;
/*  366 */     this.shapepipe = invalidpipe;
/*  367 */     this.textpipe = invalidpipe;
/*  368 */     this.imagepipe = invalidpipe;
/*  369 */     this.loops = null;
/*      */   }
/*      */ 
/*      */   public void validatePipe()
/*      */   {
/*  380 */     if (!this.surfaceData.isValid()) {
/*  381 */       throw new InvalidPipeException("attempt to validate Pipe with invalid SurfaceData");
/*      */     }
/*      */ 
/*  384 */     this.surfaceData.validatePipe(this);
/*      */   }
/*      */ 
/*      */   Shape intersectShapes(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  395 */     if (((paramShape1 instanceof Rectangle)) && ((paramShape2 instanceof Rectangle))) {
/*  396 */       return ((Rectangle)paramShape1).intersection((Rectangle)paramShape2);
/*      */     }
/*  398 */     if ((paramShape1 instanceof Rectangle2D))
/*  399 */       return intersectRectShape((Rectangle2D)paramShape1, paramShape2, paramBoolean1, paramBoolean2);
/*  400 */     if ((paramShape2 instanceof Rectangle2D)) {
/*  401 */       return intersectRectShape((Rectangle2D)paramShape2, paramShape1, paramBoolean2, paramBoolean1);
/*      */     }
/*  403 */     return intersectByArea(paramShape1, paramShape2, paramBoolean1, paramBoolean2);
/*      */   }
/*      */ 
/*      */   Shape intersectRectShape(Rectangle2D paramRectangle2D, Shape paramShape, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  415 */     if ((paramShape instanceof Rectangle2D)) {
/*  416 */       Rectangle2D localRectangle2D = (Rectangle2D)paramShape;
/*      */       Object localObject;
/*  418 */       if (!paramBoolean1)
/*  419 */         localObject = paramRectangle2D;
/*  420 */       else if (!paramBoolean2)
/*  421 */         localObject = localRectangle2D;
/*      */       else {
/*  423 */         localObject = new Rectangle2D.Float();
/*      */       }
/*  425 */       double d1 = Math.max(paramRectangle2D.getX(), localRectangle2D.getX());
/*  426 */       double d2 = Math.min(paramRectangle2D.getX() + paramRectangle2D.getWidth(), localRectangle2D.getX() + localRectangle2D.getWidth());
/*      */ 
/*  428 */       double d3 = Math.max(paramRectangle2D.getY(), localRectangle2D.getY());
/*  429 */       double d4 = Math.min(paramRectangle2D.getY() + paramRectangle2D.getHeight(), localRectangle2D.getY() + localRectangle2D.getHeight());
/*      */ 
/*  432 */       if ((d2 - d1 < 0.0D) || (d4 - d3 < 0.0D))
/*      */       {
/*  434 */         ((Rectangle2D)localObject).setFrameFromDiagonal(0.0D, 0.0D, 0.0D, 0.0D);
/*      */       }
/*  436 */       else ((Rectangle2D)localObject).setFrameFromDiagonal(d1, d3, d2, d4);
/*  437 */       return localObject;
/*      */     }
/*  439 */     if (paramRectangle2D.contains(paramShape.getBounds2D())) {
/*  440 */       if (paramBoolean2) {
/*  441 */         paramShape = cloneShape(paramShape);
/*      */       }
/*  443 */       return paramShape;
/*      */     }
/*  445 */     return intersectByArea(paramRectangle2D, paramShape, paramBoolean1, paramBoolean2);
/*      */   }
/*      */ 
/*      */   protected static Shape cloneShape(Shape paramShape) {
/*  449 */     return new GeneralPath(paramShape);
/*      */   }
/*      */ 
/*      */   Shape intersectByArea(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*      */     Area localArea1;
/*  466 */     if ((!paramBoolean1) && ((paramShape1 instanceof Area))) {
/*  467 */       localArea1 = (Area)paramShape1;
/*  468 */     } else if ((!paramBoolean2) && ((paramShape2 instanceof Area))) {
/*  469 */       localArea1 = (Area)paramShape2;
/*  470 */       paramShape2 = paramShape1;
/*      */     } else {
/*  472 */       localArea1 = new Area(paramShape1);
/*      */     }
/*      */     Area localArea2;
/*  475 */     if ((paramShape2 instanceof Area))
/*  476 */       localArea2 = (Area)paramShape2;
/*      */     else {
/*  478 */       localArea2 = new Area(paramShape2);
/*      */     }
/*      */ 
/*  481 */     localArea1.intersect(localArea2);
/*  482 */     if (localArea1.isRectangular()) {
/*  483 */       return localArea1.getBounds();
/*      */     }
/*      */ 
/*  486 */     return localArea1;
/*      */   }
/*      */ 
/*      */   public Region getCompClip()
/*      */   {
/*  494 */     if (!this.surfaceData.isValid())
/*      */     {
/*  496 */       revalidateAll();
/*      */     }
/*      */ 
/*  499 */     return this.clipRegion;
/*      */   }
/*      */ 
/*      */   public Font getFont() {
/*  503 */     if (this.font == null) {
/*  504 */       this.font = defaultFont;
/*      */     }
/*  506 */     return this.font;
/*      */   }
/*      */ 
/*      */   public FontInfo checkFontInfo(FontInfo paramFontInfo, Font paramFont, FontRenderContext paramFontRenderContext)
/*      */   {
/*  533 */     if (paramFontInfo == null) {
/*  534 */       paramFontInfo = new FontInfo();
/*      */     }
/*      */ 
/*  537 */     float f = paramFont.getSize2D();
/*      */ 
/*  539 */     AffineTransform localAffineTransform2 = null;
/*      */     int i;
/*      */     AffineTransform localAffineTransform1;
/*      */     double d3;
/*  540 */     if (paramFont.isTransformed()) {
/*  541 */       localAffineTransform2 = paramFont.getTransform();
/*  542 */       localAffineTransform2.scale(f, f);
/*  543 */       i = localAffineTransform2.getType();
/*  544 */       paramFontInfo.originX = ((float)localAffineTransform2.getTranslateX());
/*  545 */       paramFontInfo.originY = ((float)localAffineTransform2.getTranslateY());
/*  546 */       localAffineTransform2.translate(-paramFontInfo.originX, -paramFontInfo.originY);
/*  547 */       if (this.transformState >= 3) {
/*  548 */         this.transform.getMatrix(paramFontInfo.devTx = new double[4]);
/*  549 */         localAffineTransform1 = new AffineTransform(paramFontInfo.devTx);
/*  550 */         localAffineTransform2.preConcatenate(localAffineTransform1);
/*      */       } else {
/*  552 */         paramFontInfo.devTx = IDENT_MATRIX;
/*  553 */         localAffineTransform1 = IDENT_ATX;
/*      */       }
/*  555 */       localAffineTransform2.getMatrix(paramFontInfo.glyphTx = new double[4]);
/*  556 */       double d1 = localAffineTransform2.getShearX();
/*  557 */       d3 = localAffineTransform2.getScaleY();
/*  558 */       if (d1 != 0.0D) {
/*  559 */         d3 = Math.sqrt(d1 * d1 + d3 * d3);
/*      */       }
/*  561 */       paramFontInfo.pixelHeight = ((int)(Math.abs(d3) + 0.5D));
/*      */     } else {
/*  563 */       i = 0;
/*  564 */       paramFontInfo.originX = (paramFontInfo.originY = 0.0F);
/*  565 */       if (this.transformState >= 3) {
/*  566 */         this.transform.getMatrix(paramFontInfo.devTx = new double[4]);
/*  567 */         localAffineTransform1 = new AffineTransform(paramFontInfo.devTx);
/*  568 */         paramFontInfo.glyphTx = new double[4];
/*  569 */         for (int j = 0; j < 4; j++) {
/*  570 */           paramFontInfo.glyphTx[j] = (paramFontInfo.devTx[j] * f);
/*      */         }
/*  572 */         localAffineTransform2 = new AffineTransform(paramFontInfo.glyphTx);
/*  573 */         double d2 = this.transform.getShearX();
/*  574 */         d3 = this.transform.getScaleY();
/*  575 */         if (d2 != 0.0D) {
/*  576 */           d3 = Math.sqrt(d2 * d2 + d3 * d3);
/*      */         }
/*  578 */         paramFontInfo.pixelHeight = ((int)(Math.abs(d3 * f) + 0.5D));
/*      */       }
/*      */       else
/*      */       {
/*  588 */         k = (int)f;
/*  589 */         if ((f == k) && (k >= 8) && (k < 17))
/*      */         {
/*  591 */           paramFontInfo.glyphTx = textTxArr[k];
/*  592 */           localAffineTransform2 = textAtArr[k];
/*  593 */           paramFontInfo.pixelHeight = k;
/*      */         } else {
/*  595 */           paramFontInfo.pixelHeight = ((int)(f + 0.5D));
/*      */         }
/*  597 */         if (localAffineTransform2 == null) {
/*  598 */           paramFontInfo.glyphTx = new double[] { f, 0.0D, 0.0D, f };
/*  599 */           localAffineTransform2 = new AffineTransform(paramFontInfo.glyphTx);
/*      */         }
/*      */ 
/*  602 */         paramFontInfo.devTx = IDENT_MATRIX;
/*  603 */         localAffineTransform1 = IDENT_ATX;
/*      */       }
/*      */     }
/*      */ 
/*  607 */     paramFontInfo.font2D = FontUtilities.getFont2D(paramFont);
/*      */ 
/*  609 */     int k = this.fractionalMetricsHint;
/*  610 */     if (k == 0) {
/*  611 */       k = 1;
/*      */     }
/*  613 */     paramFontInfo.lcdSubPixPos = false;
/*      */     int m;
/*  638 */     if (paramFontRenderContext == null)
/*  639 */       m = this.textAntialiasHint;
/*      */     else {
/*  641 */       m = ((SunHints.Value)paramFontRenderContext.getAntiAliasingHint()).getIndex();
/*      */     }
/*  643 */     if (m == 0) {
/*  644 */       if (this.antialiasHint == 2)
/*  645 */         m = 2;
/*      */       else {
/*  647 */         m = 1;
/*      */       }
/*      */ 
/*      */     }
/*  656 */     else if (m == 3) {
/*  657 */       if (paramFontInfo.font2D.useAAForPtSize(paramFontInfo.pixelHeight))
/*  658 */         m = 2;
/*      */       else
/*  660 */         m = 1;
/*      */     }
/*  662 */     else if (m >= 4)
/*      */     {
/*  678 */       if (!this.surfaceData.canRenderLCDText(this))
/*      */       {
/*  684 */         m = 2;
/*      */       } else {
/*  686 */         paramFontInfo.lcdRGBOrder = true;
/*      */ 
/*  694 */         if (m == 5) {
/*  695 */           m = 4;
/*  696 */           paramFontInfo.lcdRGBOrder = false;
/*  697 */         } else if (m == 7)
/*      */         {
/*  699 */           m = 6;
/*  700 */           paramFontInfo.lcdRGBOrder = false;
/*      */         }
/*      */ 
/*  705 */         paramFontInfo.lcdSubPixPos = ((k == 2) && (m == 4));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  711 */     paramFontInfo.aaHint = m;
/*  712 */     paramFontInfo.fontStrike = paramFontInfo.font2D.getStrike(paramFont, localAffineTransform1, localAffineTransform2, m, k);
/*      */ 
/*  714 */     return paramFontInfo;
/*      */   }
/*      */ 
/*      */   public static boolean isRotated(double[] paramArrayOfDouble) {
/*  718 */     if ((paramArrayOfDouble[0] == paramArrayOfDouble[3]) && (paramArrayOfDouble[1] == 0.0D) && (paramArrayOfDouble[2] == 0.0D) && (paramArrayOfDouble[0] > 0.0D))
/*      */     {
/*  723 */       return false;
/*      */     }
/*      */ 
/*  726 */     return true;
/*      */   }
/*      */ 
/*      */   public void setFont(Font paramFont)
/*      */   {
/*  735 */     if ((paramFont != null) && (paramFont != this.font))
/*      */     {
/*  750 */       if ((this.textAntialiasHint == 3) && (this.textpipe != invalidpipe)) if ((this.transformState <= 2) && (!paramFont.isTransformed()) && (this.fontInfo != null))
/*      */         {
/*  750 */           if ((this.fontInfo.aaHint == 2) == FontUtilities.getFont2D(paramFont).useAAForPtSize(paramFont.getSize()));
/*      */         }
/*      */         else
/*      */         {
/*  758 */           this.textpipe = invalidpipe;
/*      */         }
/*  760 */       this.font = paramFont;
/*  761 */       this.fontMetrics = null;
/*  762 */       this.validFontInfo = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public FontInfo getFontInfo() {
/*  767 */     if (!this.validFontInfo) {
/*  768 */       this.fontInfo = checkFontInfo(this.fontInfo, this.font, null);
/*  769 */       this.validFontInfo = true;
/*      */     }
/*  771 */     return this.fontInfo;
/*      */   }
/*      */ 
/*      */   public FontInfo getGVFontInfo(Font paramFont, FontRenderContext paramFontRenderContext)
/*      */   {
/*  776 */     if ((this.glyphVectorFontInfo != null) && (this.glyphVectorFontInfo.font == paramFont) && (this.glyphVectorFRC == paramFontRenderContext))
/*      */     {
/*  779 */       return this.glyphVectorFontInfo;
/*      */     }
/*  781 */     this.glyphVectorFRC = paramFontRenderContext;
/*  782 */     return this.glyphVectorFontInfo = checkFontInfo(this.glyphVectorFontInfo, paramFont, paramFontRenderContext);
/*      */   }
/*      */ 
/*      */   public FontMetrics getFontMetrics()
/*      */   {
/*  788 */     if (this.fontMetrics != null) {
/*  789 */       return this.fontMetrics;
/*      */     }
/*      */ 
/*  792 */     return this.fontMetrics = FontDesignMetrics.getMetrics(this.font, getFontRenderContext());
/*      */   }
/*      */ 
/*      */   public FontMetrics getFontMetrics(Font paramFont)
/*      */   {
/*  797 */     if ((this.fontMetrics != null) && (paramFont == this.font)) {
/*  798 */       return this.fontMetrics;
/*      */     }
/*  800 */     FontDesignMetrics localFontDesignMetrics = FontDesignMetrics.getMetrics(paramFont, getFontRenderContext());
/*      */ 
/*  803 */     if (this.font == paramFont) {
/*  804 */       this.fontMetrics = localFontDesignMetrics;
/*      */     }
/*  806 */     return localFontDesignMetrics;
/*      */   }
/*      */ 
/*      */   public boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean)
/*      */   {
/*  827 */     if (paramBoolean) {
/*  828 */       paramShape = this.stroke.createStrokedShape(paramShape);
/*      */     }
/*      */ 
/*  831 */     paramShape = transformShape(paramShape);
/*  832 */     if ((this.constrainX | this.constrainY) != 0) {
/*  833 */       paramRectangle = new Rectangle(paramRectangle);
/*  834 */       paramRectangle.translate(this.constrainX, this.constrainY);
/*      */     }
/*      */ 
/*  837 */     return paramShape.intersects(paramRectangle);
/*      */   }
/*      */ 
/*      */   public ColorModel getDeviceColorModel()
/*      */   {
/*  844 */     return this.surfaceData.getColorModel();
/*      */   }
/*      */ 
/*      */   public GraphicsConfiguration getDeviceConfiguration()
/*      */   {
/*  851 */     return this.surfaceData.getDeviceConfiguration();
/*      */   }
/*      */ 
/*      */   public final SurfaceData getSurfaceData()
/*      */   {
/*  859 */     return this.surfaceData;
/*      */   }
/*      */ 
/*      */   public void setComposite(Composite paramComposite)
/*      */   {
/*  873 */     if (this.composite == paramComposite)
/*      */       return;
/*      */     CompositeType localCompositeType;
/*      */     int i;
/*  878 */     if ((paramComposite instanceof AlphaComposite)) {
/*  879 */       AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
/*  880 */       localCompositeType = CompositeType.forAlphaComposite(localAlphaComposite);
/*  881 */       if (localCompositeType == CompositeType.SrcOverNoEa) {
/*  882 */         if ((this.paintState == 0) || ((this.paintState > 1) && (this.paint.getTransparency() == 1)))
/*      */         {
/*  886 */           i = 0;
/*      */         }
/*  888 */         else i = 1;
/*      */       }
/*  890 */       else if ((localCompositeType == CompositeType.SrcNoEa) || (localCompositeType == CompositeType.Src) || (localCompositeType == CompositeType.Clear))
/*      */       {
/*  894 */         i = 0;
/*  895 */       } else if ((this.surfaceData.getTransparency() == 1) && (localCompositeType == CompositeType.SrcIn))
/*      */       {
/*  898 */         i = 0;
/*      */       }
/*  900 */       else i = 1;
/*      */     }
/*  902 */     else if ((paramComposite instanceof XORComposite)) {
/*  903 */       i = 2;
/*  904 */       localCompositeType = CompositeType.Xor; } else {
/*  905 */       if (paramComposite == null) {
/*  906 */         throw new IllegalArgumentException("null Composite");
/*      */       }
/*  908 */       this.surfaceData.checkCustomComposite();
/*  909 */       i = 3;
/*  910 */       localCompositeType = CompositeType.General;
/*      */     }
/*  912 */     if ((this.compositeState != i) || (this.imageComp != localCompositeType))
/*      */     {
/*  915 */       this.compositeState = i;
/*  916 */       this.imageComp = localCompositeType;
/*  917 */       invalidatePipe();
/*  918 */       this.validFontInfo = false;
/*      */     }
/*  920 */     this.composite = paramComposite;
/*  921 */     if (this.paintState <= 1)
/*  922 */       validateColor();
/*      */   }
/*      */ 
/*      */   public void setPaint(Paint paramPaint)
/*      */   {
/*  935 */     if ((paramPaint instanceof Color)) {
/*  936 */       setColor((Color)paramPaint);
/*  937 */       return;
/*      */     }
/*  939 */     if ((paramPaint == null) || (this.paint == paramPaint)) {
/*  940 */       return;
/*      */     }
/*  942 */     this.paint = paramPaint;
/*  943 */     if (this.imageComp == CompositeType.SrcOverNoEa)
/*      */     {
/*  945 */       if (paramPaint.getTransparency() == 1) {
/*  946 */         if (this.compositeState != 0) {
/*  947 */           this.compositeState = 0;
/*      */         }
/*      */       }
/*  950 */       else if (this.compositeState == 0) {
/*  951 */         this.compositeState = 1;
/*      */       }
/*      */     }
/*      */ 
/*  955 */     Class localClass = paramPaint.getClass();
/*  956 */     if (localClass == GradientPaint.class)
/*  957 */       this.paintState = 2;
/*  958 */     else if (localClass == LinearGradientPaint.class)
/*  959 */       this.paintState = 3;
/*  960 */     else if (localClass == RadialGradientPaint.class)
/*  961 */       this.paintState = 4;
/*  962 */     else if (localClass == TexturePaint.class)
/*  963 */       this.paintState = 5;
/*      */     else {
/*  965 */       this.paintState = 6;
/*      */     }
/*  967 */     this.validFontInfo = false;
/*  968 */     invalidatePipe();
/*      */   }
/*      */ 
/*      */   private void validateBasicStroke(BasicStroke paramBasicStroke)
/*      */   {
/*  986 */     int i = this.antialiasHint == 2 ? 1 : 0;
/*  987 */     if (this.transformState < 3) {
/*  988 */       if (i != 0) {
/*  989 */         if (paramBasicStroke.getLineWidth() <= MinPenSizeAA) {
/*  990 */           if (paramBasicStroke.getDashArray() == null)
/*  991 */             this.strokeState = 0;
/*      */           else
/*  993 */             this.strokeState = 1;
/*      */         }
/*      */         else {
/*  996 */           this.strokeState = 2;
/*      */         }
/*      */       }
/*  999 */       else if (paramBasicStroke == defaultStroke)
/* 1000 */         this.strokeState = 0;
/* 1001 */       else if (paramBasicStroke.getLineWidth() <= 1.0F) {
/* 1002 */         if (paramBasicStroke.getDashArray() == null)
/* 1003 */           this.strokeState = 0;
/*      */         else
/* 1005 */           this.strokeState = 1;
/*      */       }
/*      */       else
/* 1008 */         this.strokeState = 2;
/*      */     }
/*      */     else
/*      */     {
/*      */       double d1;
/* 1013 */       if ((this.transform.getType() & 0x24) == 0)
/*      */       {
/* 1015 */         d1 = Math.abs(this.transform.getDeterminant());
/*      */       }
/*      */       else {
/* 1018 */         double d2 = this.transform.getScaleX();
/* 1019 */         double d3 = this.transform.getShearX();
/* 1020 */         double d4 = this.transform.getShearY();
/* 1021 */         double d5 = this.transform.getScaleY();
/*      */ 
/* 1037 */         double d6 = d2 * d2 + d4 * d4;
/* 1038 */         double d7 = 2.0D * (d2 * d3 + d4 * d5);
/* 1039 */         double d8 = d3 * d3 + d5 * d5;
/*      */ 
/* 1063 */         double d9 = Math.sqrt(d7 * d7 + (d6 - d8) * (d6 - d8));
/*      */ 
/* 1066 */         d1 = (d6 + d8 + d9) / 2.0D;
/*      */       }
/* 1068 */       if (paramBasicStroke != defaultStroke) {
/* 1069 */         d1 *= paramBasicStroke.getLineWidth() * paramBasicStroke.getLineWidth();
/*      */       }
/* 1071 */       if (d1 <= (i != 0 ? MinPenSizeAASquared : 1.000000001D))
/*      */       {
/* 1074 */         if (paramBasicStroke.getDashArray() == null)
/* 1075 */           this.strokeState = 0;
/*      */         else
/* 1077 */           this.strokeState = 1;
/*      */       }
/*      */       else
/* 1080 */         this.strokeState = 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setStroke(Stroke paramStroke)
/*      */   {
/* 1092 */     if (paramStroke == null) {
/* 1093 */       throw new IllegalArgumentException("null Stroke");
/*      */     }
/* 1095 */     int i = this.strokeState;
/* 1096 */     this.stroke = paramStroke;
/* 1097 */     if ((paramStroke instanceof BasicStroke))
/* 1098 */       validateBasicStroke((BasicStroke)paramStroke);
/*      */     else {
/* 1100 */       this.strokeState = 3;
/*      */     }
/* 1102 */     if (this.strokeState != i)
/* 1103 */       invalidatePipe();
/*      */   }
/*      */ 
/*      */   public void setRenderingHint(RenderingHints.Key paramKey, Object paramObject)
/*      */   {
/* 1124 */     if (!paramKey.isCompatibleValue(paramObject)) {
/* 1125 */       throw new IllegalArgumentException(paramObject + " is not compatible with " + paramKey);
/*      */     }
/*      */ 
/* 1128 */     if ((paramKey instanceof SunHints.Key))
/*      */     {
/* 1130 */       int j = 0;
/* 1131 */       int k = 1;
/* 1132 */       SunHints.Key localKey = (SunHints.Key)paramKey;
/*      */       int m;
/* 1134 */       if (localKey == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST)
/* 1135 */         m = ((Integer)paramObject).intValue();
/*      */       else
/* 1137 */         m = ((SunHints.Value)paramObject).getIndex();
/*      */       int i;
/* 1139 */       switch (localKey.getIndex()) {
/*      */       case 0:
/* 1141 */         i = this.renderHint != m ? 1 : 0;
/* 1142 */         if (i != 0) {
/* 1143 */           this.renderHint = m;
/* 1144 */           if (this.interpolationHint == -1)
/* 1145 */             this.interpolationType = (m == 2 ? 2 : 1);  } break;
/*      */       case 1:
/* 1153 */         i = this.antialiasHint != m ? 1 : 0;
/* 1154 */         this.antialiasHint = m;
/* 1155 */         if (i != 0) {
/* 1156 */           j = this.textAntialiasHint == 0 ? 1 : 0;
/*      */ 
/* 1159 */           if (this.strokeState != 3)
/* 1160 */             validateBasicStroke((BasicStroke)this.stroke);  } break;
/*      */       case 2:
/* 1165 */         i = this.textAntialiasHint != m ? 1 : 0;
/* 1166 */         j = i;
/* 1167 */         this.textAntialiasHint = m;
/* 1168 */         break;
/*      */       case 3:
/* 1170 */         i = this.fractionalMetricsHint != m ? 1 : 0;
/* 1171 */         j = i;
/* 1172 */         this.fractionalMetricsHint = m;
/* 1173 */         break;
/*      */       case 100:
/* 1175 */         i = 0;
/*      */ 
/* 1177 */         this.lcdTextContrast = m;
/* 1178 */         break;
/*      */       case 5:
/* 1180 */         this.interpolationHint = m;
/* 1181 */         switch (m) {
/*      */         case 2:
/* 1183 */           m = 3;
/* 1184 */           break;
/*      */         case 1:
/* 1186 */           m = 2;
/* 1187 */           break;
/*      */         case 0:
/*      */         default:
/* 1190 */           m = 1;
/*      */         }
/*      */ 
/* 1193 */         i = this.interpolationType != m ? 1 : 0;
/* 1194 */         this.interpolationType = m;
/* 1195 */         break;
/*      */       case 8:
/* 1197 */         i = this.strokeHint != m ? 1 : 0;
/* 1198 */         this.strokeHint = m;
/* 1199 */         break;
/*      */       default:
/* 1201 */         k = 0;
/* 1202 */         i = 0;
/*      */       }
/*      */ 
/* 1205 */       if (k != 0) {
/* 1206 */         if (i != 0) {
/* 1207 */           invalidatePipe();
/* 1208 */           if (j != 0) {
/* 1209 */             this.fontMetrics = null;
/* 1210 */             this.cachedFRC = null;
/* 1211 */             this.validFontInfo = false;
/* 1212 */             this.glyphVectorFontInfo = null;
/*      */           }
/*      */         }
/* 1215 */         if (this.hints != null) {
/* 1216 */           this.hints.put(paramKey, paramObject);
/*      */         }
/* 1218 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1222 */     if (this.hints == null) {
/* 1223 */       this.hints = makeHints(null);
/*      */     }
/* 1225 */     this.hints.put(paramKey, paramObject);
/*      */   }
/*      */ 
/*      */   public Object getRenderingHint(RenderingHints.Key paramKey)
/*      */   {
/* 1238 */     if (this.hints != null) {
/* 1239 */       return this.hints.get(paramKey);
/*      */     }
/* 1241 */     if (!(paramKey instanceof SunHints.Key)) {
/* 1242 */       return null;
/*      */     }
/* 1244 */     int i = ((SunHints.Key)paramKey).getIndex();
/* 1245 */     switch (i) {
/*      */     case 0:
/* 1247 */       return SunHints.Value.get(0, this.renderHint);
/*      */     case 1:
/* 1250 */       return SunHints.Value.get(1, this.antialiasHint);
/*      */     case 2:
/* 1253 */       return SunHints.Value.get(2, this.textAntialiasHint);
/*      */     case 3:
/* 1256 */       return SunHints.Value.get(3, this.fractionalMetricsHint);
/*      */     case 100:
/* 1259 */       return new Integer(this.lcdTextContrast);
/*      */     case 5:
/* 1261 */       switch (this.interpolationHint) {
/*      */       case 0:
/* 1263 */         return SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
/*      */       case 1:
/* 1265 */         return SunHints.VALUE_INTERPOLATION_BILINEAR;
/*      */       case 2:
/* 1267 */         return SunHints.VALUE_INTERPOLATION_BICUBIC;
/*      */       }
/* 1269 */       return null;
/*      */     case 8:
/* 1271 */       return SunHints.Value.get(8, this.strokeHint);
/*      */     }
/*      */ 
/* 1274 */     return null;
/*      */   }
/*      */ 
/*      */   public void setRenderingHints(Map<?, ?> paramMap)
/*      */   {
/* 1285 */     this.hints = null;
/* 1286 */     this.renderHint = 0;
/* 1287 */     this.antialiasHint = 1;
/* 1288 */     this.textAntialiasHint = 0;
/* 1289 */     this.fractionalMetricsHint = 1;
/* 1290 */     this.lcdTextContrast = lcdTextContrastDefaultValue;
/* 1291 */     this.interpolationHint = -1;
/* 1292 */     this.interpolationType = 1;
/* 1293 */     int i = 0;
/* 1294 */     Iterator localIterator = paramMap.keySet().iterator();
/* 1295 */     while (localIterator.hasNext()) {
/* 1296 */       Object localObject = localIterator.next();
/* 1297 */       if ((localObject == SunHints.KEY_RENDERING) || (localObject == SunHints.KEY_ANTIALIASING) || (localObject == SunHints.KEY_TEXT_ANTIALIASING) || (localObject == SunHints.KEY_FRACTIONALMETRICS) || (localObject == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) || (localObject == SunHints.KEY_STROKE_CONTROL) || (localObject == SunHints.KEY_INTERPOLATION))
/*      */       {
/* 1305 */         setRenderingHint((RenderingHints.Key)localObject, paramMap.get(localObject));
/*      */       }
/* 1307 */       else i = 1;
/*      */     }
/*      */ 
/* 1310 */     if (i != 0) {
/* 1311 */       this.hints = makeHints(paramMap);
/*      */     }
/* 1313 */     invalidatePipe();
/*      */   }
/*      */ 
/*      */   public void addRenderingHints(Map<?, ?> paramMap)
/*      */   {
/* 1324 */     int i = 0;
/* 1325 */     Iterator localIterator = paramMap.keySet().iterator();
/* 1326 */     while (localIterator.hasNext()) {
/* 1327 */       Object localObject = localIterator.next();
/* 1328 */       if ((localObject == SunHints.KEY_RENDERING) || (localObject == SunHints.KEY_ANTIALIASING) || (localObject == SunHints.KEY_TEXT_ANTIALIASING) || (localObject == SunHints.KEY_FRACTIONALMETRICS) || (localObject == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) || (localObject == SunHints.KEY_STROKE_CONTROL) || (localObject == SunHints.KEY_INTERPOLATION))
/*      */       {
/* 1336 */         setRenderingHint((RenderingHints.Key)localObject, paramMap.get(localObject));
/*      */       }
/* 1338 */       else i = 1;
/*      */     }
/*      */ 
/* 1341 */     if (i != 0)
/* 1342 */       if (this.hints == null)
/* 1343 */         this.hints = makeHints(paramMap);
/*      */       else
/* 1345 */         this.hints.putAll(paramMap);
/*      */   }
/*      */ 
/*      */   public RenderingHints getRenderingHints()
/*      */   {
/* 1357 */     if (this.hints == null) {
/* 1358 */       return makeHints(null);
/*      */     }
/* 1360 */     return (RenderingHints)this.hints.clone();
/*      */   }
/*      */ 
/*      */   RenderingHints makeHints(Map paramMap)
/*      */   {
/* 1365 */     RenderingHints localRenderingHints = new RenderingHints(paramMap);
/* 1366 */     localRenderingHints.put(SunHints.KEY_RENDERING, SunHints.Value.get(0, this.renderHint));
/*      */ 
/* 1369 */     localRenderingHints.put(SunHints.KEY_ANTIALIASING, SunHints.Value.get(1, this.antialiasHint));
/*      */ 
/* 1372 */     localRenderingHints.put(SunHints.KEY_TEXT_ANTIALIASING, SunHints.Value.get(2, this.textAntialiasHint));
/*      */ 
/* 1375 */     localRenderingHints.put(SunHints.KEY_FRACTIONALMETRICS, SunHints.Value.get(3, this.fractionalMetricsHint));
/*      */ 
/* 1378 */     localRenderingHints.put(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, Integer.valueOf(this.lcdTextContrast));
/*      */     Object localObject;
/* 1381 */     switch (this.interpolationHint) {
/*      */     case 0:
/* 1383 */       localObject = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
/* 1384 */       break;
/*      */     case 1:
/* 1386 */       localObject = SunHints.VALUE_INTERPOLATION_BILINEAR;
/* 1387 */       break;
/*      */     case 2:
/* 1389 */       localObject = SunHints.VALUE_INTERPOLATION_BICUBIC;
/* 1390 */       break;
/*      */     default:
/* 1392 */       localObject = null;
/*      */     }
/*      */ 
/* 1395 */     if (localObject != null) {
/* 1396 */       localRenderingHints.put(SunHints.KEY_INTERPOLATION, localObject);
/*      */     }
/* 1398 */     localRenderingHints.put(SunHints.KEY_STROKE_CONTROL, SunHints.Value.get(8, this.strokeHint));
/*      */ 
/* 1401 */     return localRenderingHints;
/*      */   }
/*      */ 
/*      */   public void translate(double paramDouble1, double paramDouble2)
/*      */   {
/* 1416 */     this.transform.translate(paramDouble1, paramDouble2);
/* 1417 */     invalidateTransform();
/*      */   }
/*      */ 
/*      */   public void rotate(double paramDouble)
/*      */   {
/* 1435 */     this.transform.rotate(paramDouble);
/* 1436 */     invalidateTransform();
/*      */   }
/*      */ 
/*      */   public void rotate(double paramDouble1, double paramDouble2, double paramDouble3)
/*      */   {
/* 1455 */     this.transform.rotate(paramDouble1, paramDouble2, paramDouble3);
/* 1456 */     invalidateTransform();
/*      */   }
/*      */ 
/*      */   public void scale(double paramDouble1, double paramDouble2)
/*      */   {
/* 1471 */     this.transform.scale(paramDouble1, paramDouble2);
/* 1472 */     invalidateTransform();
/*      */   }
/*      */ 
/*      */   public void shear(double paramDouble1, double paramDouble2)
/*      */   {
/* 1491 */     this.transform.shear(paramDouble1, paramDouble2);
/* 1492 */     invalidateTransform();
/*      */   }
/*      */ 
/*      */   public void transform(AffineTransform paramAffineTransform)
/*      */   {
/* 1513 */     this.transform.concatenate(paramAffineTransform);
/* 1514 */     invalidateTransform();
/*      */   }
/*      */ 
/*      */   public void translate(int paramInt1, int paramInt2)
/*      */   {
/* 1521 */     this.transform.translate(paramInt1, paramInt2);
/* 1522 */     if (this.transformState <= 1) {
/* 1523 */       this.transX += paramInt1;
/* 1524 */       this.transY += paramInt2;
/* 1525 */       this.transformState = ((this.transX | this.transY) == 0 ? 0 : 1);
/*      */     }
/*      */     else {
/* 1528 */       invalidateTransform();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTransform(AffineTransform paramAffineTransform)
/*      */   {
/* 1540 */     if ((this.constrainX | this.constrainY) == 0) {
/* 1541 */       this.transform.setTransform(paramAffineTransform);
/*      */     } else {
/* 1543 */       this.transform.setToTranslation(this.constrainX, this.constrainY);
/* 1544 */       this.transform.concatenate(paramAffineTransform);
/*      */     }
/* 1546 */     invalidateTransform();
/*      */   }
/*      */ 
/*      */   protected void invalidateTransform() {
/* 1550 */     int i = this.transform.getType();
/* 1551 */     int j = this.transformState;
/* 1552 */     if (i == 0) {
/* 1553 */       this.transformState = 0;
/* 1554 */       this.transX = (this.transY = 0);
/* 1555 */     } else if (i == 1) {
/* 1556 */       double d1 = this.transform.getTranslateX();
/* 1557 */       double d2 = this.transform.getTranslateY();
/* 1558 */       this.transX = ((int)Math.floor(d1 + 0.5D));
/* 1559 */       this.transY = ((int)Math.floor(d2 + 0.5D));
/* 1560 */       if ((d1 == this.transX) && (d2 == this.transY))
/* 1561 */         this.transformState = 1;
/*      */       else
/* 1563 */         this.transformState = 2;
/*      */     }
/* 1565 */     else if ((i & 0x78) == 0)
/*      */     {
/* 1569 */       this.transformState = 3;
/* 1570 */       this.transX = (this.transY = 0);
/*      */     } else {
/* 1572 */       this.transformState = 4;
/* 1573 */       this.transX = (this.transY = 0);
/*      */     }
/*      */ 
/* 1576 */     if ((this.transformState >= 3) || (j >= 3))
/*      */     {
/* 1582 */       this.cachedFRC = null;
/* 1583 */       this.validFontInfo = false;
/* 1584 */       this.fontMetrics = null;
/* 1585 */       this.glyphVectorFontInfo = null;
/*      */ 
/* 1587 */       if (this.transformState != j) {
/* 1588 */         invalidatePipe();
/*      */       }
/*      */     }
/* 1591 */     if (this.strokeState != 3)
/* 1592 */       validateBasicStroke((BasicStroke)this.stroke);
/*      */   }
/*      */ 
/*      */   public AffineTransform getTransform()
/*      */   {
/* 1602 */     if ((this.constrainX | this.constrainY) == 0) {
/* 1603 */       return new AffineTransform(this.transform);
/*      */     }
/* 1605 */     AffineTransform localAffineTransform = AffineTransform.getTranslateInstance(-this.constrainX, -this.constrainY);
/*      */ 
/* 1607 */     localAffineTransform.concatenate(this.transform);
/* 1608 */     return localAffineTransform;
/*      */   }
/*      */ 
/*      */   public AffineTransform cloneTransform()
/*      */   {
/* 1616 */     return new AffineTransform(this.transform);
/*      */   }
/*      */ 
/*      */   public Paint getPaint()
/*      */   {
/* 1625 */     return this.paint;
/*      */   }
/*      */ 
/*      */   public Composite getComposite()
/*      */   {
/* 1633 */     return this.composite;
/*      */   }
/*      */ 
/*      */   public Color getColor() {
/* 1637 */     return this.foregroundColor;
/*      */   }
/*      */ 
/*      */   final void validateColor()
/*      */   {
/*      */     int i;
/* 1662 */     if (this.imageComp == CompositeType.Clear) {
/* 1663 */       i = 0;
/*      */     } else {
/* 1665 */       i = this.foregroundColor.getRGB();
/* 1666 */       if ((this.compositeState <= 1) && (this.imageComp != CompositeType.SrcNoEa) && (this.imageComp != CompositeType.SrcOverNoEa))
/*      */       {
/* 1670 */         AlphaComposite localAlphaComposite = (AlphaComposite)this.composite;
/* 1671 */         int j = Math.round(localAlphaComposite.getAlpha() * (i >>> 24));
/* 1672 */         i = i & 0xFFFFFF | j << 24;
/*      */       }
/*      */     }
/* 1675 */     this.eargb = i;
/* 1676 */     this.pixel = this.surfaceData.pixelFor(i);
/*      */   }
/*      */ 
/*      */   public void setColor(Color paramColor) {
/* 1680 */     if ((paramColor == null) || (paramColor == this.paint)) {
/* 1681 */       return;
/*      */     }
/* 1683 */     this.paint = (this.foregroundColor = paramColor);
/* 1684 */     validateColor();
/* 1685 */     if (this.eargb >> 24 == -1) {
/* 1686 */       if (this.paintState == 0) {
/* 1687 */         return;
/*      */       }
/* 1689 */       this.paintState = 0;
/* 1690 */       if (this.imageComp == CompositeType.SrcOverNoEa)
/*      */       {
/* 1692 */         this.compositeState = 0;
/*      */       }
/*      */     } else {
/* 1695 */       if (this.paintState == 1) {
/* 1696 */         return;
/*      */       }
/* 1698 */       this.paintState = 1;
/* 1699 */       if (this.imageComp == CompositeType.SrcOverNoEa)
/*      */       {
/* 1701 */         this.compositeState = 1;
/*      */       }
/*      */     }
/* 1704 */     this.validFontInfo = false;
/* 1705 */     invalidatePipe();
/*      */   }
/*      */ 
/*      */   public void setBackground(Color paramColor)
/*      */   {
/* 1721 */     this.backgroundColor = paramColor;
/*      */   }
/*      */ 
/*      */   public Color getBackground()
/*      */   {
/* 1729 */     return this.backgroundColor;
/*      */   }
/*      */ 
/*      */   public Stroke getStroke()
/*      */   {
/* 1737 */     return this.stroke;
/*      */   }
/*      */ 
/*      */   public Rectangle getClipBounds()
/*      */   {
/*      */     Rectangle localRectangle;
/* 1742 */     if (this.clipState == 0) {
/* 1743 */       localRectangle = null;
/* 1744 */     } else if (this.transformState <= 1) {
/* 1745 */       if ((this.usrClip instanceof Rectangle))
/* 1746 */         localRectangle = new Rectangle((Rectangle)this.usrClip);
/*      */       else {
/* 1748 */         localRectangle = this.usrClip.getBounds();
/*      */       }
/* 1750 */       localRectangle.translate(-this.transX, -this.transY);
/*      */     } else {
/* 1752 */       localRectangle = getClip().getBounds();
/*      */     }
/* 1754 */     return localRectangle;
/*      */   }
/*      */ 
/*      */   public Rectangle getClipBounds(Rectangle paramRectangle) {
/* 1758 */     if (this.clipState != 0) {
/* 1759 */       if (this.transformState <= 1) {
/* 1760 */         if ((this.usrClip instanceof Rectangle))
/* 1761 */           paramRectangle.setBounds((Rectangle)this.usrClip);
/*      */         else {
/* 1763 */           paramRectangle.setBounds(this.usrClip.getBounds());
/*      */         }
/* 1765 */         paramRectangle.translate(-this.transX, -this.transY);
/*      */       } else {
/* 1767 */         paramRectangle.setBounds(getClip().getBounds());
/*      */       }
/* 1769 */     } else if (paramRectangle == null) {
/* 1770 */       throw new NullPointerException("null rectangle parameter");
/*      */     }
/* 1772 */     return paramRectangle;
/*      */   }
/*      */ 
/*      */   public boolean hitClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 1776 */     if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/* 1777 */       return false;
/*      */     }
/* 1779 */     if (this.transformState > 1)
/*      */     {
/* 1797 */       double[] arrayOfDouble = { paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2, paramInt1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4 };
/*      */ 
/* 1803 */       this.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/* 1804 */       paramInt1 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[0], arrayOfDouble[2]), Math.min(arrayOfDouble[4], arrayOfDouble[6])));
/*      */ 
/* 1806 */       paramInt2 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[1], arrayOfDouble[3]), Math.min(arrayOfDouble[5], arrayOfDouble[7])));
/*      */ 
/* 1808 */       paramInt3 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[0], arrayOfDouble[2]), Math.max(arrayOfDouble[4], arrayOfDouble[6])));
/*      */ 
/* 1810 */       paramInt4 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[1], arrayOfDouble[3]), Math.max(arrayOfDouble[5], arrayOfDouble[7])));
/*      */     }
/*      */     else {
/* 1813 */       paramInt1 += this.transX;
/* 1814 */       paramInt2 += this.transY;
/* 1815 */       paramInt3 += paramInt1;
/* 1816 */       paramInt4 += paramInt2;
/*      */     }
/*      */     try
/*      */     {
/* 1820 */       if (!getCompClip().intersectsQuickCheckXYXY(paramInt1, paramInt2, paramInt3, paramInt4))
/* 1821 */         return false;
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException) {
/* 1824 */       return false;
/*      */     }
/*      */ 
/* 1831 */     return true;
/*      */   }
/*      */ 
/*      */   protected void validateCompClip() {
/* 1835 */     int i = this.clipState;
/* 1836 */     if (this.usrClip == null) {
/* 1837 */       this.clipState = 0;
/* 1838 */       this.clipRegion = this.devClip;
/* 1839 */     } else if ((this.usrClip instanceof Rectangle2D)) {
/* 1840 */       this.clipState = 1;
/* 1841 */       if ((this.usrClip instanceof Rectangle))
/* 1842 */         this.clipRegion = this.devClip.getIntersection((Rectangle)this.usrClip);
/*      */       else
/* 1844 */         this.clipRegion = this.devClip.getIntersection(this.usrClip.getBounds());
/*      */     }
/*      */     else {
/* 1847 */       PathIterator localPathIterator = this.usrClip.getPathIterator(null);
/* 1848 */       int[] arrayOfInt = new int[4];
/* 1849 */       ShapeSpanIterator localShapeSpanIterator = LoopPipe.getFillSSI(this);
/*      */       try {
/* 1851 */         localShapeSpanIterator.setOutputArea(this.devClip);
/* 1852 */         localShapeSpanIterator.appendPath(localPathIterator);
/* 1853 */         localShapeSpanIterator.getPathBox(arrayOfInt);
/* 1854 */         Region localRegion = Region.getInstance(arrayOfInt);
/* 1855 */         localRegion.appendSpans(localShapeSpanIterator);
/* 1856 */         this.clipRegion = localRegion;
/* 1857 */         this.clipState = (localRegion.isRectangular() ? 1 : 2);
/*      */       }
/*      */       finally {
/* 1860 */         localShapeSpanIterator.dispose();
/*      */       }
/*      */     }
/* 1863 */     if ((i != this.clipState) && ((this.clipState == 2) || (i == 2)))
/*      */     {
/* 1866 */       this.validFontInfo = false;
/* 1867 */       invalidatePipe();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Shape transformShape(Shape paramShape)
/*      */   {
/* 1876 */     if (paramShape == null) {
/* 1877 */       return null;
/*      */     }
/* 1879 */     if (this.transformState > 1) {
/* 1880 */       return transformShape(this.transform, paramShape);
/*      */     }
/* 1882 */     return transformShape(this.transX, this.transY, paramShape);
/*      */   }
/*      */ 
/*      */   public Shape untransformShape(Shape paramShape)
/*      */   {
/* 1887 */     if (paramShape == null) {
/* 1888 */       return null;
/*      */     }
/* 1890 */     if (this.transformState > 1) {
/*      */       try {
/* 1892 */         return transformShape(this.transform.createInverse(), paramShape);
/*      */       } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 1894 */         return null;
/*      */       }
/*      */     }
/* 1897 */     return transformShape(-this.transX, -this.transY, paramShape);
/*      */   }
/*      */ 
/*      */   protected static Shape transformShape(int paramInt1, int paramInt2, Shape paramShape)
/*      */   {
/* 1902 */     if (paramShape == null) {
/* 1903 */       return null;
/*      */     }
/*      */ 
/* 1906 */     if ((paramShape instanceof Rectangle)) {
/* 1907 */       localObject = paramShape.getBounds();
/* 1908 */       ((Rectangle)localObject).translate(paramInt1, paramInt2);
/* 1909 */       return localObject;
/*      */     }
/* 1911 */     if ((paramShape instanceof Rectangle2D)) {
/* 1912 */       localObject = (Rectangle2D)paramShape;
/* 1913 */       return new Rectangle2D.Double(((Rectangle2D)localObject).getX() + paramInt1, ((Rectangle2D)localObject).getY() + paramInt2, ((Rectangle2D)localObject).getWidth(), ((Rectangle2D)localObject).getHeight());
/*      */     }
/*      */ 
/* 1919 */     if ((paramInt1 == 0) && (paramInt2 == 0)) {
/* 1920 */       return cloneShape(paramShape);
/*      */     }
/*      */ 
/* 1923 */     Object localObject = AffineTransform.getTranslateInstance(paramInt1, paramInt2);
/* 1924 */     return ((AffineTransform)localObject).createTransformedShape(paramShape);
/*      */   }
/*      */ 
/*      */   protected static Shape transformShape(AffineTransform paramAffineTransform, Shape paramShape) {
/* 1928 */     if (paramShape == null) {
/* 1929 */       return null;
/*      */     }
/*      */ 
/* 1932 */     if (((paramShape instanceof Rectangle2D)) && ((paramAffineTransform.getType() & 0x30) == 0))
/*      */     {
/* 1935 */       Object localObject = (Rectangle2D)paramShape;
/* 1936 */       double[] arrayOfDouble = new double[4];
/* 1937 */       arrayOfDouble[0] = ((Rectangle2D)localObject).getX();
/* 1938 */       arrayOfDouble[1] = ((Rectangle2D)localObject).getY();
/* 1939 */       arrayOfDouble[2] = (arrayOfDouble[0] + ((Rectangle2D)localObject).getWidth());
/* 1940 */       arrayOfDouble[3] = (arrayOfDouble[1] + ((Rectangle2D)localObject).getHeight());
/* 1941 */       paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
/* 1942 */       localObject = new Rectangle2D.Float();
/* 1943 */       ((Rectangle2D)localObject).setFrameFromDiagonal(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3]);
/*      */ 
/* 1945 */       return localObject;
/*      */     }
/*      */ 
/* 1948 */     if (paramAffineTransform.isIdentity()) {
/* 1949 */       return cloneShape(paramShape);
/*      */     }
/*      */ 
/* 1952 */     return paramAffineTransform.createTransformedShape(paramShape);
/*      */   }
/*      */ 
/*      */   public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 1956 */     clip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
/*      */   }
/*      */ 
/*      */   public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 1960 */     setClip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
/*      */   }
/*      */ 
/*      */   public Shape getClip() {
/* 1964 */     return untransformShape(this.usrClip);
/*      */   }
/*      */ 
/*      */   public void setClip(Shape paramShape) {
/* 1968 */     this.usrClip = transformShape(paramShape);
/* 1969 */     validateCompClip();
/*      */   }
/*      */ 
/*      */   public void clip(Shape paramShape)
/*      */   {
/* 1981 */     paramShape = transformShape(paramShape);
/* 1982 */     if (this.usrClip != null) {
/* 1983 */       paramShape = intersectShapes(this.usrClip, paramShape, true, true);
/*      */     }
/* 1985 */     this.usrClip = paramShape;
/* 1986 */     validateCompClip();
/*      */   }
/*      */ 
/*      */   public void setPaintMode() {
/* 1990 */     setComposite(AlphaComposite.SrcOver);
/*      */   }
/*      */ 
/*      */   public void setXORMode(Color paramColor) {
/* 1994 */     if (paramColor == null) {
/* 1995 */       throw new IllegalArgumentException("null XORColor");
/*      */     }
/* 1997 */     setComposite(new XORComposite(paramColor, this.surfaceData));
/*      */   }
/*      */ 
/*      */   public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*      */     try
/*      */     {
/* 2005 */       doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2008 */         revalidateAll();
/* 2009 */         doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2016 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doCopyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/* 2021 */     if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/* 2022 */       return;
/*      */     }
/* 2024 */     SurfaceData localSurfaceData = this.surfaceData;
/* 2025 */     if (localSurfaceData.copyArea(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)) {
/* 2026 */       return;
/*      */     }
/* 2028 */     if (this.transformState >= 3) {
/* 2029 */       throw new InternalError("transformed copyArea not implemented yet");
/*      */     }
/*      */ 
/* 2034 */     Region localRegion = getCompClip();
/*      */ 
/* 2036 */     Composite localComposite = this.composite;
/* 2037 */     if (this.lastCAcomp != localComposite) {
/* 2038 */       localObject = localSurfaceData.getSurfaceType();
/* 2039 */       CompositeType localCompositeType = this.imageComp;
/* 2040 */       if ((CompositeType.SrcOverNoEa.equals(localCompositeType)) && (localSurfaceData.getTransparency() == 1))
/*      */       {
/* 2043 */         localCompositeType = CompositeType.SrcNoEa;
/*      */       }
/* 2045 */       this.lastCAblit = Blit.locate((SurfaceType)localObject, localCompositeType, (SurfaceType)localObject);
/* 2046 */       this.lastCAcomp = localComposite;
/*      */     }
/*      */ 
/* 2049 */     paramInt1 += this.transX;
/* 2050 */     paramInt2 += this.transY;
/*      */ 
/* 2052 */     Object localObject = this.lastCAblit;
/*      */     int i;
/*      */     int j;
/* 2053 */     if ((paramInt6 == 0) && (paramInt5 > 0) && (paramInt5 < paramInt3)) {
/* 2054 */       while (paramInt3 > 0) {
/* 2055 */         i = Math.min(paramInt3, paramInt5);
/* 2056 */         paramInt3 -= i;
/* 2057 */         j = paramInt1 + paramInt3;
/* 2058 */         ((Blit)localObject).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, j, paramInt2, j + paramInt5, paramInt2 + paramInt6, i, paramInt4);
/*      */       }
/*      */ 
/* 2061 */       return;
/*      */     }
/* 2063 */     if ((paramInt6 > 0) && (paramInt6 < paramInt4) && (paramInt5 > -paramInt3) && (paramInt5 < paramInt3)) {
/* 2064 */       while (paramInt4 > 0) {
/* 2065 */         i = Math.min(paramInt4, paramInt6);
/* 2066 */         paramInt4 -= i;
/* 2067 */         j = paramInt2 + paramInt4;
/* 2068 */         ((Blit)localObject).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, paramInt1, j, paramInt1 + paramInt5, j + paramInt6, paramInt3, i);
/*      */       }
/*      */ 
/* 2071 */       return;
/*      */     }
/* 2073 */     ((Blit)localObject).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*      */     try
/*      */     {
/* 2137 */       this.drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2140 */         revalidateAll();
/* 2141 */         this.drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2148 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/*      */     try {
/* 2154 */       this.drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2157 */         revalidateAll();
/* 2158 */         this.drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2165 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/*      */     try {
/* 2171 */       this.fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2174 */         revalidateAll();
/* 2175 */         this.fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2182 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2188 */       this.drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2191 */         revalidateAll();
/* 2192 */         this.drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2199 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2205 */       this.fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2208 */         revalidateAll();
/* 2209 */         this.fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2216 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*      */     try {
/* 2223 */       this.drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2226 */         revalidateAll();
/* 2227 */         this.drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2234 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*      */     try {
/* 2241 */       this.fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2244 */         revalidateAll();
/* 2245 */         this.fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2252 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
/*      */     try {
/* 2258 */       this.drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2261 */         revalidateAll();
/* 2262 */         this.drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2269 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
/*      */     try {
/* 2275 */       this.drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2278 */         revalidateAll();
/* 2279 */         this.drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2286 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
/*      */     try {
/* 2292 */       this.fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2295 */         revalidateAll();
/* 2296 */         this.fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2303 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2309 */       this.drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2312 */         revalidateAll();
/* 2313 */         this.drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2320 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2326 */       this.fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2329 */         revalidateAll();
/* 2330 */         this.fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2337 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void revalidateAll()
/*      */   {
/*      */     try
/*      */     {
/* 2348 */       this.surfaceData = this.surfaceData.getReplacement();
/* 2349 */       if (this.surfaceData == null) {
/* 2350 */         this.surfaceData = NullSurfaceData.theInstance;
/*      */       }
/*      */ 
/* 2354 */       setDevClip(this.surfaceData.getBounds());
/*      */ 
/* 2356 */       if (this.paintState <= 1) {
/* 2357 */         validateColor();
/*      */       }
/* 2359 */       if ((this.composite instanceof XORComposite)) {
/* 2360 */         Color localColor = ((XORComposite)this.composite).getXorColor();
/* 2361 */         setComposite(new XORComposite(localColor, this.surfaceData));
/*      */       }
/* 2363 */       validatePipe();
/*      */     }
/*      */     finally
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 2373 */     Composite localComposite = this.composite;
/* 2374 */     Paint localPaint = this.paint;
/* 2375 */     setComposite(AlphaComposite.Src);
/* 2376 */     setColor(getBackground());
/* 2377 */     fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
/* 2378 */     setPaint(localPaint);
/* 2379 */     setComposite(localComposite);
/*      */   }
/*      */ 
/*      */   public void draw(Shape paramShape)
/*      */   {
/*      */     try
/*      */     {
/* 2398 */       this.shapepipe.draw(this, paramShape);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2401 */         revalidateAll();
/* 2402 */         this.shapepipe.draw(this, paramShape);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2409 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fill(Shape paramShape)
/*      */   {
/*      */     try
/*      */     {
/* 2428 */       this.shapepipe.fill(this, paramShape);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2431 */         revalidateAll();
/* 2432 */         this.shapepipe.fill(this, paramShape);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2439 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean isIntegerTranslation(AffineTransform paramAffineTransform)
/*      */   {
/* 2448 */     if (paramAffineTransform.isIdentity()) {
/* 2449 */       return true;
/*      */     }
/* 2451 */     if (paramAffineTransform.getType() == 1) {
/* 2452 */       double d1 = paramAffineTransform.getTranslateX();
/* 2453 */       double d2 = paramAffineTransform.getTranslateY();
/* 2454 */       return (d1 == (int)d1) && (d2 == (int)d2);
/*      */     }
/* 2456 */     return false;
/*      */   }
/*      */ 
/*      */   private static int getTileIndex(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 2464 */     paramInt1 -= paramInt2;
/* 2465 */     if (paramInt1 < 0) {
/* 2466 */       paramInt1 += 1 - paramInt3;
/*      */     }
/* 2468 */     return paramInt1 / paramInt3;
/*      */   }
/*      */ 
/*      */   private static Rectangle getImageRegion(RenderedImage paramRenderedImage, Region paramRegion, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2)
/*      */   {
/* 2483 */     Rectangle localRectangle1 = new Rectangle(paramRenderedImage.getMinX(), paramRenderedImage.getMinY(), paramRenderedImage.getWidth(), paramRenderedImage.getHeight());
/*      */ 
/* 2487 */     Rectangle localRectangle2 = null;
/*      */     try {
/* 2489 */       double[] arrayOfDouble = new double[8];
/*      */       double tmp53_52 = paramRegion.getLoX(); arrayOfDouble[2] = tmp53_52; arrayOfDouble[0] = tmp53_52;
/*      */       double tmp68_67 = paramRegion.getHiX(); arrayOfDouble[6] = tmp68_67; arrayOfDouble[4] = tmp68_67;
/*      */       double tmp82_81 = paramRegion.getLoY(); arrayOfDouble[5] = tmp82_81; arrayOfDouble[1] = tmp82_81;
/*      */       double tmp97_96 = paramRegion.getHiY(); arrayOfDouble[7] = tmp97_96; arrayOfDouble[3] = tmp97_96;
/*      */ 
/* 2496 */       paramAffineTransform1.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/* 2497 */       paramAffineTransform2.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/*      */       double d2;
/* 2501 */       double d1 = d2 = arrayOfDouble[0];
/*      */       double d4;
/* 2502 */       double d3 = d4 = arrayOfDouble[1];
/*      */ 
/* 2504 */       for (int i = 2; i < 8; ) {
/* 2505 */         double d5 = arrayOfDouble[(i++)];
/* 2506 */         if (d5 < d1)
/* 2507 */           d1 = d5;
/* 2508 */         else if (d5 > d2) {
/* 2509 */           d2 = d5;
/*      */         }
/* 2511 */         d5 = arrayOfDouble[(i++)];
/* 2512 */         if (d5 < d3)
/* 2513 */           d3 = d5;
/* 2514 */         else if (d5 > d4) {
/* 2515 */           d4 = d5;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2521 */       i = (int)d1 - paramInt1;
/* 2522 */       int j = (int)(d2 - d1 + 2 * paramInt1);
/* 2523 */       int k = (int)d3 - paramInt2;
/* 2524 */       int m = (int)(d4 - d3 + 2 * paramInt2);
/*      */ 
/* 2526 */       Rectangle localRectangle3 = new Rectangle(i, k, j, m);
/* 2527 */       localRectangle2 = localRectangle3.intersection(localRectangle1);
/*      */     }
/*      */     catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 2530 */       localRectangle2 = localRectangle1;
/*      */     }
/*      */ 
/* 2533 */     return localRectangle2;
/*      */   }
/*      */ 
/*      */   public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform)
/*      */   {
/* 2557 */     if (paramRenderedImage == null) {
/* 2558 */       return;
/*      */     }
/*      */ 
/* 2562 */     if ((paramRenderedImage instanceof BufferedImage)) {
/* 2563 */       BufferedImage localBufferedImage1 = (BufferedImage)paramRenderedImage;
/* 2564 */       drawImage(localBufferedImage1, paramAffineTransform, null);
/* 2565 */       return;
/*      */     }
/*      */ 
/* 2571 */     int i = (this.transformState <= 1) && (isIntegerTranslation(paramAffineTransform)) ? 1 : 0;
/*      */ 
/* 2576 */     int j = i != 0 ? 0 : 3;
/*      */     Region localRegion;
/*      */     try
/*      */     {
/* 2580 */       localRegion = getCompClip();
/*      */     } catch (InvalidPipeException localInvalidPipeException) {
/* 2582 */       return;
/*      */     }
/*      */ 
/* 2587 */     Rectangle localRectangle = getImageRegion(paramRenderedImage, localRegion, this.transform, paramAffineTransform, j, j);
/*      */ 
/* 2592 */     if ((localRectangle.width <= 0) || (localRectangle.height <= 0)) {
/* 2593 */       return;
/*      */     }
/*      */ 
/* 2601 */     if (i != 0)
/*      */     {
/* 2608 */       drawTranslatedRenderedImage(paramRenderedImage, localRectangle, (int)paramAffineTransform.getTranslateX(), (int)paramAffineTransform.getTranslateY());
/*      */ 
/* 2611 */       return;
/*      */     }
/*      */ 
/* 2615 */     Raster localRaster = paramRenderedImage.getData(localRectangle);
/*      */ 
/* 2620 */     WritableRaster localWritableRaster = Raster.createWritableRaster(localRaster.getSampleModel(), localRaster.getDataBuffer(), null);
/*      */ 
/* 2630 */     int k = localRaster.getMinX();
/* 2631 */     int m = localRaster.getMinY();
/* 2632 */     int n = localRaster.getWidth();
/* 2633 */     int i1 = localRaster.getHeight();
/* 2634 */     int i2 = k - localRaster.getSampleModelTranslateX();
/* 2635 */     int i3 = m - localRaster.getSampleModelTranslateY();
/* 2636 */     if ((i2 != 0) || (i3 != 0) || (n != localWritableRaster.getWidth()) || (i1 != localWritableRaster.getHeight()))
/*      */     {
/* 2638 */       localWritableRaster = localWritableRaster.createWritableChild(i2, i3, n, i1, 0, 0, null);
/*      */     }
/*      */ 
/* 2651 */     AffineTransform localAffineTransform = (AffineTransform)paramAffineTransform.clone();
/* 2652 */     localAffineTransform.translate(k, m);
/*      */ 
/* 2654 */     ColorModel localColorModel = paramRenderedImage.getColorModel();
/* 2655 */     BufferedImage localBufferedImage2 = new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
/*      */ 
/* 2659 */     drawImage(localBufferedImage2, localAffineTransform, null);
/*      */   }
/*      */ 
/*      */   private boolean clipTo(Rectangle paramRectangle1, Rectangle paramRectangle2)
/*      */   {
/* 2668 */     int i = Math.max(paramRectangle1.x, paramRectangle2.x);
/* 2669 */     int j = Math.min(paramRectangle1.x + paramRectangle1.width, paramRectangle2.x + paramRectangle2.width);
/* 2670 */     int k = Math.max(paramRectangle1.y, paramRectangle2.y);
/* 2671 */     int m = Math.min(paramRectangle1.y + paramRectangle1.height, paramRectangle2.y + paramRectangle2.height);
/* 2672 */     if ((j - i < 0) || (m - k < 0)) {
/* 2673 */       paramRectangle1.width = -1;
/* 2674 */       paramRectangle1.height = -1;
/* 2675 */       return false;
/*      */     }
/* 2677 */     paramRectangle1.x = i;
/* 2678 */     paramRectangle1.y = k;
/* 2679 */     paramRectangle1.width = (j - i);
/* 2680 */     paramRectangle1.height = (m - k);
/* 2681 */     return true;
/*      */   }
/*      */ 
/*      */   private void drawTranslatedRenderedImage(RenderedImage paramRenderedImage, Rectangle paramRectangle, int paramInt1, int paramInt2)
/*      */   {
/* 2695 */     int i = paramRenderedImage.getTileGridXOffset();
/* 2696 */     int j = paramRenderedImage.getTileGridYOffset();
/* 2697 */     int k = paramRenderedImage.getTileWidth();
/* 2698 */     int m = paramRenderedImage.getTileHeight();
/*      */ 
/* 2701 */     int n = getTileIndex(paramRectangle.x, i, k);
/*      */ 
/* 2703 */     int i1 = getTileIndex(paramRectangle.y, j, m);
/*      */ 
/* 2705 */     int i2 = getTileIndex(paramRectangle.x + paramRectangle.width - 1, i, k);
/*      */ 
/* 2708 */     int i3 = getTileIndex(paramRectangle.y + paramRectangle.height - 1, j, m);
/*      */ 
/* 2713 */     ColorModel localColorModel = paramRenderedImage.getColorModel();
/*      */ 
/* 2716 */     Rectangle localRectangle = new Rectangle();
/*      */ 
/* 2718 */     for (int i4 = i1; i4 <= i3; i4++)
/* 2719 */       for (int i5 = n; i5 <= i2; i5++)
/*      */       {
/* 2721 */         Raster localRaster = paramRenderedImage.getTile(i5, i4);
/*      */ 
/* 2724 */         localRectangle.x = (i5 * k + i);
/* 2725 */         localRectangle.y = (i4 * m + j);
/* 2726 */         localRectangle.width = k;
/* 2727 */         localRectangle.height = m;
/*      */ 
/* 2732 */         clipTo(localRectangle, paramRectangle);
/*      */ 
/* 2735 */         WritableRaster localWritableRaster = null;
/* 2736 */         if ((localRaster instanceof WritableRaster)) {
/* 2737 */           localWritableRaster = (WritableRaster)localRaster;
/*      */         }
/*      */         else
/*      */         {
/* 2741 */           localWritableRaster = Raster.createWritableRaster(localRaster.getSampleModel(), localRaster.getDataBuffer(), null);
/*      */         }
/*      */ 
/* 2749 */         localWritableRaster = localWritableRaster.createWritableChild(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, 0, 0, null);
/*      */ 
/* 2756 */         BufferedImage localBufferedImage = new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
/*      */ 
/* 2767 */         copyImage(localBufferedImage, localRectangle.x + paramInt1, localRectangle.y + paramInt2, 0, 0, localRectangle.width, localRectangle.height, null, null);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform)
/*      */   {
/* 2777 */     if (paramRenderableImage == null) {
/* 2778 */       return; } 
/*      */ AffineTransform localAffineTransform1 = this.transform;
/* 2782 */     AffineTransform localAffineTransform2 = new AffineTransform(paramAffineTransform);
/* 2783 */     localAffineTransform2.concatenate(localAffineTransform1);
/*      */ 
/* 2786 */     RenderContext localRenderContext = new RenderContext(localAffineTransform2);
/*      */     AffineTransform localAffineTransform3;
/*      */     try { localAffineTransform3 = localAffineTransform1.createInverse();
/*      */     } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 2791 */       localRenderContext = new RenderContext(localAffineTransform1);
/* 2792 */       localAffineTransform3 = new AffineTransform();
/*      */     }
/*      */ 
/* 2795 */     RenderedImage localRenderedImage = paramRenderableImage.createRendering(localRenderContext);
/* 2796 */     drawRenderedImage(localRenderedImage, localAffineTransform3);
/*      */   }
/*      */ 
/*      */   protected Rectangle transformBounds(Rectangle paramRectangle, AffineTransform paramAffineTransform)
/*      */   {
/* 2806 */     if (paramAffineTransform.isIdentity()) {
/* 2807 */       return paramRectangle;
/*      */     }
/*      */ 
/* 2810 */     Shape localShape = transformShape(paramAffineTransform, paramRectangle);
/* 2811 */     return localShape.getBounds();
/*      */   }
/*      */ 
/*      */   public void drawString(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 2816 */     if (paramString == null) {
/* 2817 */       throw new NullPointerException("String is null");
/*      */     }
/*      */ 
/* 2820 */     if (this.font.hasLayoutAttributes()) {
/* 2821 */       if (paramString.length() == 0) {
/* 2822 */         return;
/*      */       }
/* 2824 */       new TextLayout(paramString, this.font, getFontRenderContext()).draw(this, paramInt1, paramInt2);
/* 2825 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2829 */       this.textpipe.drawString(this, paramString, paramInt1, paramInt2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2832 */         revalidateAll();
/* 2833 */         this.textpipe.drawString(this, paramString, paramInt1, paramInt2);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2840 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawString(String paramString, float paramFloat1, float paramFloat2) {
/* 2845 */     if (paramString == null) {
/* 2846 */       throw new NullPointerException("String is null");
/*      */     }
/*      */ 
/* 2849 */     if (this.font.hasLayoutAttributes()) {
/* 2850 */       if (paramString.length() == 0) {
/* 2851 */         return;
/*      */       }
/* 2853 */       new TextLayout(paramString, this.font, getFontRenderContext()).draw(this, paramFloat1, paramFloat2);
/* 2854 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2858 */       this.textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2861 */         revalidateAll();
/* 2862 */         this.textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2869 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
/*      */   {
/* 2875 */     if (paramAttributedCharacterIterator == null) {
/* 2876 */       throw new NullPointerException("AttributedCharacterIterator is null");
/*      */     }
/* 2878 */     if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex()) {
/* 2879 */       return;
/*      */     }
/* 2881 */     TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
/* 2882 */     localTextLayout.draw(this, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2)
/*      */   {
/* 2887 */     if (paramAttributedCharacterIterator == null) {
/* 2888 */       throw new NullPointerException("AttributedCharacterIterator is null");
/*      */     }
/* 2890 */     if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex()) {
/* 2891 */       return;
/*      */     }
/* 2893 */     TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
/* 2894 */     localTextLayout.draw(this, paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
/*      */   {
/* 2899 */     if (paramGlyphVector == null) {
/* 2900 */       throw new NullPointerException("GlyphVector is null");
/*      */     }
/*      */     try
/*      */     {
/* 2904 */       this.textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2907 */         revalidateAll();
/* 2908 */         this.textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2915 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 2921 */     if (paramArrayOfChar == null) {
/* 2922 */       throw new NullPointerException("char data is null");
/*      */     }
/* 2924 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length)) {
/* 2925 */       throw new ArrayIndexOutOfBoundsException("bad offset/length");
/*      */     }
/* 2927 */     if (this.font.hasLayoutAttributes()) {
/* 2928 */       if (paramArrayOfChar.length == 0) {
/* 2929 */         return;
/*      */       }
/* 2931 */       new TextLayout(new String(paramArrayOfChar, paramInt1, paramInt2), this.font, getFontRenderContext()).draw(this, paramInt3, paramInt4);
/*      */ 
/* 2933 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2937 */       this.textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2940 */         revalidateAll();
/* 2941 */         this.textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2948 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 2953 */     if (paramArrayOfByte == null) {
/* 2954 */       throw new NullPointerException("byte data is null");
/*      */     }
/* 2956 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
/* 2957 */       throw new ArrayIndexOutOfBoundsException("bad offset/length");
/*      */     }
/*      */ 
/* 2960 */     char[] arrayOfChar = new char[paramInt2];
/* 2961 */     for (int i = paramInt2; i-- > 0; ) {
/* 2962 */       arrayOfChar[i] = ((char)(paramArrayOfByte[(i + paramInt1)] & 0xFF));
/*      */     }
/* 2964 */     if (this.font.hasLayoutAttributes()) {
/* 2965 */       if (paramArrayOfByte.length == 0) {
/* 2966 */         return;
/*      */       }
/* 2968 */       new TextLayout(new String(arrayOfChar), this.font, getFontRenderContext()).draw(this, paramInt3, paramInt4);
/*      */ 
/* 2970 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2974 */       this.textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2977 */         revalidateAll();
/* 2978 */         this.textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 2985 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
/*      */   {
/* 2996 */     return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, null, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public boolean copyImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/*      */     try
/*      */     {
/* 3010 */       return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3014 */         revalidateAll();
/* 3015 */         return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/* 3021 */         return false;
/*      */       }
/*      */     } finally {
/* 3024 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 3035 */     if (paramImage == null) {
/* 3036 */       return true;
/*      */     }
/*      */ 
/* 3039 */     if ((paramInt3 == 0) || (paramInt4 == 0)) {
/* 3040 */       return true;
/*      */     }
/* 3042 */     if ((paramInt3 == paramImage.getWidth(null)) && (paramInt4 == paramImage.getHeight(null))) {
/* 3043 */       return copyImage(paramImage, paramInt1, paramInt2, 0, 0, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */     }
/*      */     try
/*      */     {
/* 3047 */       return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3051 */         revalidateAll();
/* 3052 */         return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/* 3058 */         return false;
/*      */       }
/*      */     } finally {
/* 3061 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*      */   {
/* 3069 */     return drawImage(paramImage, paramInt1, paramInt2, null, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 3079 */     if (paramImage == null) {
/* 3080 */       return true;
/*      */     }
/*      */     try
/*      */     {
/* 3084 */       return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3087 */         revalidateAll();
/* 3088 */         return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/* 3093 */         return false;
/*      */       }
/*      */     } finally {
/* 3096 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver)
/*      */   {
/* 3108 */     return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 3121 */     if (paramImage == null) {
/* 3122 */       return true;
/*      */     }
/*      */ 
/* 3125 */     if ((paramInt1 == paramInt3) || (paramInt2 == paramInt4) || (paramInt5 == paramInt7) || (paramInt6 == paramInt8))
/*      */     {
/* 3128 */       return true;
/*      */     }
/*      */     int k;
/* 3131 */     if ((paramInt7 - paramInt5 == paramInt3 - paramInt1) && (paramInt8 - paramInt6 == paramInt4 - paramInt2))
/*      */     {
/*      */       int n;
/*      */       int i;
/* 3136 */       if (paramInt7 > paramInt5) {
/* 3137 */         n = paramInt7 - paramInt5;
/* 3138 */         i = paramInt5;
/* 3139 */         k = paramInt1;
/*      */       } else {
/* 3141 */         n = paramInt5 - paramInt7;
/* 3142 */         i = paramInt7;
/* 3143 */         k = paramInt3;
/*      */       }
/*      */       int i1;
/*      */       int j;
/*      */       int m;
/* 3145 */       if (paramInt8 > paramInt6) {
/* 3146 */         i1 = paramInt8 - paramInt6;
/* 3147 */         j = paramInt6;
/* 3148 */         m = paramInt2;
/*      */       } else {
/* 3150 */         i1 = paramInt6 - paramInt8;
/* 3151 */         j = paramInt8;
/* 3152 */         m = paramInt4;
/*      */       }
/* 3154 */       return copyImage(paramImage, k, m, i, j, n, i1, paramColor, paramImageObserver);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3159 */       return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException1)
/*      */     {
/*      */       try {
/* 3164 */         revalidateAll();
/* 3165 */         return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/* 3172 */         return 0;
/*      */       }
/*      */     } finally {
/* 3175 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
/*      */   {
/* 3201 */     if (paramImage == null) {
/* 3202 */       return true;
/*      */     }
/*      */ 
/* 3205 */     if ((paramAffineTransform == null) || (paramAffineTransform.isIdentity())) {
/* 3206 */       return drawImage(paramImage, 0, 0, null, paramImageObserver);
/*      */     }
/*      */     try
/*      */     {
/* 3210 */       return this.imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3213 */         revalidateAll();
/* 3214 */         return this.imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/* 3219 */         return false;
/*      */       }
/*      */     } finally {
/* 3222 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
/*      */   {
/* 3231 */     if (paramBufferedImage == null) {
/* 3232 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3236 */       this.imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3239 */         revalidateAll();
/* 3240 */         this.imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */       }
/*      */     }
/*      */     finally {
/* 3247 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public FontRenderContext getFontRenderContext()
/*      */   {
/* 3256 */     if (this.cachedFRC == null) {
/* 3257 */       int i = this.textAntialiasHint;
/* 3258 */       if ((i == 0) && (this.antialiasHint == 2))
/*      */       {
/* 3260 */         i = 2;
/*      */       }
/*      */ 
/* 3263 */       AffineTransform localAffineTransform = null;
/* 3264 */       if (this.transformState >= 3) {
/* 3265 */         if ((this.transform.getTranslateX() == 0.0D) && (this.transform.getTranslateY() == 0.0D))
/*      */         {
/* 3267 */           localAffineTransform = this.transform;
/*      */         }
/* 3269 */         else localAffineTransform = new AffineTransform(this.transform.getScaleX(), this.transform.getShearY(), this.transform.getShearX(), this.transform.getScaleY(), 0.0D, 0.0D);
/*      */ 
/*      */       }
/*      */ 
/* 3276 */       this.cachedFRC = new FontRenderContext(localAffineTransform, SunHints.Value.get(2, i), SunHints.Value.get(3, this.fractionalMetricsHint));
/*      */     }
/*      */ 
/* 3281 */     return this.cachedFRC;
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/* 3295 */     this.surfaceData = NullSurfaceData.theInstance;
/* 3296 */     invalidatePipe();
/*      */   }
/*      */ 
/*      */   public void finalize()
/*      */   {
/*      */   }
/*      */ 
/*      */   public Object getDestination()
/*      */   {
/* 3320 */     return this.surfaceData.getDestination();
/*      */   }
/*      */ 
/*      */   public Surface getDestSurface()
/*      */   {
/* 3330 */     return this.surfaceData;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  226 */     if (PerformanceLogger.loggingEnabled()) {
/*  227 */       PerformanceLogger.setTime("SunGraphics2D static initialization");
/*      */     }
/*      */ 
/*  358 */     invalidpipe = new ValidatePipe();
/*      */ 
/*  509 */     IDENT_MATRIX = new double[] { 1.0D, 0.0D, 0.0D, 1.0D };
/*  510 */     IDENT_ATX = new AffineTransform();
/*      */ 
/*  515 */     textTxArr = new double[17][];
/*  516 */     textAtArr = new AffineTransform[17];
/*      */ 
/*  520 */     for (int i = 8; i < 17; i++) {
/*  521 */       textTxArr[i] = { i, 0.0D, 0.0D, i };
/*  522 */       textAtArr[i] = new AffineTransform(textTxArr[i]);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.SunGraphics2D
 * JD-Core Version:    0.6.2
 */