/*      */ package sun.font;
/*      */ 
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.font.GlyphJustificationInfo;
/*      */ import java.awt.font.LineMetrics;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Float;
/*      */ import java.io.PrintStream;
/*      */ import java.util.Map;
/*      */ 
/*      */ class ExtendedTextSourceLabel extends ExtendedTextLabel
/*      */   implements Decoration.Label
/*      */ {
/*      */   TextSource source;
/*      */   private Decoration decorator;
/*      */   private Font font;
/*      */   private AffineTransform baseTX;
/*      */   private CoreMetrics cm;
/*      */   Rectangle2D lb;
/*      */   Rectangle2D ab;
/*      */   Rectangle2D vb;
/*      */   Rectangle2D ib;
/*      */   StandardGlyphVector gv;
/*      */   float[] charinfo;
/*      */   private static final int posx = 0;
/*      */   private static final int posy = 1;
/*      */   private static final int advx = 2;
/*      */   private static final int advy = 3;
/*      */   private static final int visx = 4;
/*      */   private static final int visy = 5;
/*      */   private static final int visw = 6;
/*      */   private static final int vish = 7;
/*      */   private static final int numvals = 8;
/*      */ 
/*      */   public ExtendedTextSourceLabel(TextSource paramTextSource, Decoration paramDecoration)
/*      */   {
/*   78 */     this.source = paramTextSource;
/*   79 */     this.decorator = paramDecoration;
/*   80 */     finishInit();
/*      */   }
/*      */ 
/*      */   public ExtendedTextSourceLabel(TextSource paramTextSource, ExtendedTextSourceLabel paramExtendedTextSourceLabel, int paramInt)
/*      */   {
/*   90 */     this.source = paramTextSource;
/*   91 */     this.decorator = paramExtendedTextSourceLabel.decorator;
/*   92 */     finishInit();
/*      */   }
/*      */ 
/*      */   private void finishInit() {
/*   96 */     this.font = this.source.getFont();
/*      */ 
/*   98 */     Map localMap = this.font.getAttributes();
/*   99 */     this.baseTX = AttributeValues.getBaselineTransform(localMap);
/*  100 */     if (this.baseTX == null) {
/*  101 */       this.cm = this.source.getCoreMetrics();
/*      */     } else {
/*  103 */       AffineTransform localAffineTransform = AttributeValues.getCharTransform(localMap);
/*  104 */       if (localAffineTransform == null) {
/*  105 */         localAffineTransform = new AffineTransform();
/*      */       }
/*  107 */       this.font = this.font.deriveFont(localAffineTransform);
/*      */ 
/*  109 */       LineMetrics localLineMetrics = this.font.getLineMetrics(this.source.getChars(), this.source.getStart(), this.source.getStart() + this.source.getLength(), this.source.getFRC());
/*      */ 
/*  111 */       this.cm = CoreMetrics.get(localLineMetrics);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Rectangle2D getLogicalBounds()
/*      */   {
/*  119 */     return getLogicalBounds(0.0F, 0.0F);
/*      */   }
/*      */ 
/*      */   public Rectangle2D getLogicalBounds(float paramFloat1, float paramFloat2) {
/*  123 */     if (this.lb == null) {
/*  124 */       this.lb = createLogicalBounds();
/*      */     }
/*  126 */     return new Rectangle2D.Float((float)(this.lb.getX() + paramFloat1), (float)(this.lb.getY() + paramFloat2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
/*      */   }
/*      */ 
/*      */   public float getAdvance()
/*      */   {
/*  133 */     if (this.lb == null) {
/*  134 */       this.lb = createLogicalBounds();
/*      */     }
/*  136 */     return (float)this.lb.getWidth();
/*      */   }
/*      */ 
/*      */   public Rectangle2D getVisualBounds(float paramFloat1, float paramFloat2) {
/*  140 */     if (this.vb == null) {
/*  141 */       this.vb = this.decorator.getVisualBounds(this);
/*      */     }
/*  143 */     return new Rectangle2D.Float((float)(this.vb.getX() + paramFloat1), (float)(this.vb.getY() + paramFloat2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
/*      */   }
/*      */ 
/*      */   public Rectangle2D getAlignBounds(float paramFloat1, float paramFloat2)
/*      */   {
/*  150 */     if (this.ab == null) {
/*  151 */       this.ab = createAlignBounds();
/*      */     }
/*  153 */     return new Rectangle2D.Float((float)(this.ab.getX() + paramFloat1), (float)(this.ab.getY() + paramFloat2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
/*      */   }
/*      */ 
/*      */   public Rectangle2D getItalicBounds(float paramFloat1, float paramFloat2)
/*      */   {
/*  161 */     if (this.ib == null) {
/*  162 */       this.ib = createItalicBounds();
/*      */     }
/*  164 */     return new Rectangle2D.Float((float)(this.ib.getX() + paramFloat1), (float)(this.ib.getY() + paramFloat2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
/*      */   }
/*      */ 
/*      */   public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
/*      */   {
/*  172 */     return getGV().getPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   public boolean isSimple() {
/*  176 */     return (this.decorator == Decoration.getPlainDecoration()) && (this.baseTX == null);
/*      */   }
/*      */ 
/*      */   public AffineTransform getBaselineTransform()
/*      */   {
/*  181 */     return this.baseTX;
/*      */   }
/*      */ 
/*      */   public Shape handleGetOutline(float paramFloat1, float paramFloat2) {
/*  185 */     return getGV().getOutline(paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   public Shape getOutline(float paramFloat1, float paramFloat2) {
/*  189 */     return this.decorator.getOutline(this, paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   public void handleDraw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) {
/*  193 */     paramGraphics2D.drawGlyphVector(getGV(), paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) {
/*  197 */     this.decorator.drawTextAndDecorations(this, paramGraphics2D, paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   protected Rectangle2D createLogicalBounds()
/*      */   {
/*  225 */     return getGV().getLogicalBounds();
/*      */   }
/*      */ 
/*      */   public Rectangle2D handleGetVisualBounds() {
/*  229 */     return getGV().getVisualBounds();
/*      */   }
/*      */ 
/*      */   protected Rectangle2D createAlignBounds()
/*      */   {
/*  243 */     float[] arrayOfFloat = getCharinfo();
/*      */ 
/*  245 */     float f1 = 0.0F;
/*  246 */     float f2 = -this.cm.ascent;
/*  247 */     float f3 = 0.0F;
/*  248 */     float f4 = this.cm.ascent + this.cm.descent;
/*      */ 
/*  250 */     int i = (this.source.getLayoutFlags() & 0x8) == 0 ? 1 : 0;
/*  251 */     int j = arrayOfFloat.length - 8;
/*  252 */     if (i != 0) {
/*  253 */       while ((j > 0) && (arrayOfFloat[(j + 6)] == 0.0F)) {
/*  254 */         j -= 8;
/*      */       }
/*      */     }
/*      */ 
/*  258 */     if (j >= 0) {
/*  259 */       int k = 0;
/*  260 */       while ((k < j) && ((arrayOfFloat[(k + 2)] == 0.0F) || ((i == 0) && (arrayOfFloat[(k + 6)] == 0.0F)))) {
/*  261 */         k += 8;
/*      */       }
/*      */ 
/*  264 */       f1 = Math.max(0.0F, arrayOfFloat[(k + 0)]);
/*  265 */       f3 = arrayOfFloat[(j + 0)] + arrayOfFloat[(j + 2)] - f1;
/*      */     }
/*      */ 
/*  286 */     return new Rectangle2D.Float(f1, f2, f3, f4);
/*      */   }
/*      */ 
/*      */   public Rectangle2D createItalicBounds() {
/*  290 */     float f1 = this.cm.italicAngle;
/*      */ 
/*  292 */     Rectangle2D localRectangle2D = getLogicalBounds();
/*  293 */     float f2 = (float)localRectangle2D.getMinX();
/*  294 */     float f3 = -this.cm.ascent;
/*  295 */     float f4 = (float)localRectangle2D.getMaxX();
/*  296 */     float f5 = this.cm.descent;
/*  297 */     if (f1 != 0.0F) {
/*  298 */       if (f1 > 0.0F) {
/*  299 */         f2 -= f1 * (f5 - this.cm.ssOffset);
/*  300 */         f4 -= f1 * (f3 - this.cm.ssOffset);
/*      */       } else {
/*  302 */         f2 -= f1 * (f3 - this.cm.ssOffset);
/*  303 */         f4 -= f1 * (f5 - this.cm.ssOffset);
/*      */       }
/*      */     }
/*  306 */     return new Rectangle2D.Float(f2, f3, f4 - f2, f5 - f3);
/*      */   }
/*      */ 
/*      */   private final StandardGlyphVector getGV() {
/*  310 */     if (this.gv == null) {
/*  311 */       this.gv = createGV();
/*      */     }
/*      */ 
/*  314 */     return this.gv;
/*      */   }
/*      */ 
/*      */   protected StandardGlyphVector createGV() {
/*  318 */     FontRenderContext localFontRenderContext = this.source.getFRC();
/*  319 */     int i = this.source.getLayoutFlags();
/*  320 */     char[] arrayOfChar = this.source.getChars();
/*  321 */     int j = this.source.getStart();
/*  322 */     int k = this.source.getLength();
/*      */ 
/*  324 */     GlyphLayout localGlyphLayout = GlyphLayout.get(null);
/*  325 */     this.gv = localGlyphLayout.layout(this.font, localFontRenderContext, arrayOfChar, j, k, i, null);
/*  326 */     GlyphLayout.done(localGlyphLayout);
/*      */ 
/*  328 */     return this.gv;
/*      */   }
/*      */ 
/*      */   public int getNumCharacters()
/*      */   {
/*  344 */     return this.source.getLength();
/*      */   }
/*      */ 
/*      */   public CoreMetrics getCoreMetrics() {
/*  348 */     return this.cm;
/*      */   }
/*      */ 
/*      */   public float getCharX(int paramInt) {
/*  352 */     validate(paramInt);
/*  353 */     return getCharinfo()[(l2v(paramInt) * 8 + 0)];
/*      */   }
/*      */ 
/*      */   public float getCharY(int paramInt) {
/*  357 */     validate(paramInt);
/*  358 */     return getCharinfo()[(l2v(paramInt) * 8 + 1)];
/*      */   }
/*      */ 
/*      */   public float getCharAdvance(int paramInt) {
/*  362 */     validate(paramInt);
/*  363 */     return getCharinfo()[(l2v(paramInt) * 8 + 2)];
/*      */   }
/*      */ 
/*      */   public Rectangle2D handleGetCharVisualBounds(int paramInt) {
/*  367 */     validate(paramInt);
/*  368 */     float[] arrayOfFloat = getCharinfo();
/*  369 */     paramInt = l2v(paramInt) * 8;
/*  370 */     return new Rectangle2D.Float(arrayOfFloat[(paramInt + 4)], arrayOfFloat[(paramInt + 5)], arrayOfFloat[(paramInt + 6)], arrayOfFloat[(paramInt + 7)]);
/*      */   }
/*      */ 
/*      */   public Rectangle2D getCharVisualBounds(int paramInt, float paramFloat1, float paramFloat2)
/*      */   {
/*  379 */     Rectangle2D localRectangle2D = this.decorator.getCharVisualBounds(this, paramInt);
/*  380 */     if ((paramFloat1 != 0.0F) || (paramFloat2 != 0.0F)) {
/*  381 */       localRectangle2D.setRect(localRectangle2D.getX() + paramFloat1, localRectangle2D.getY() + paramFloat2, localRectangle2D.getWidth(), localRectangle2D.getHeight());
/*      */     }
/*      */ 
/*  386 */     return localRectangle2D;
/*      */   }
/*      */ 
/*      */   private void validate(int paramInt) {
/*  390 */     if (paramInt < 0)
/*  391 */       throw new IllegalArgumentException("index " + paramInt + " < 0");
/*  392 */     if (paramInt >= this.source.getLength())
/*  393 */       throw new IllegalArgumentException("index " + paramInt + " < " + this.source.getLength());
/*      */   }
/*      */ 
/*      */   public int logicalToVisual(int paramInt)
/*      */   {
/*  445 */     validate(paramInt);
/*  446 */     return l2v(paramInt);
/*      */   }
/*      */ 
/*      */   public int visualToLogical(int paramInt) {
/*  450 */     validate(paramInt);
/*  451 */     return v2l(paramInt);
/*      */   }
/*      */ 
/*      */   public int getLineBreakIndex(int paramInt, float paramFloat) {
/*  455 */     float[] arrayOfFloat = getCharinfo();
/*  456 */     int i = this.source.getLength();
/*  457 */     paramInt--;
/*  458 */     while (paramFloat >= 0.0F) { paramInt++; if (paramInt >= i) break;
/*  459 */       float f = arrayOfFloat[(l2v(paramInt) * 8 + 2)];
/*  460 */       paramFloat -= f;
/*      */     }
/*      */ 
/*  463 */     return paramInt;
/*      */   }
/*      */ 
/*      */   public float getAdvanceBetween(int paramInt1, int paramInt2) {
/*  467 */     float f = 0.0F;
/*      */ 
/*  469 */     float[] arrayOfFloat = getCharinfo();
/*  470 */     paramInt1--;
/*      */     while (true) { paramInt1++; if (paramInt1 >= paramInt2) break;
/*  472 */       f += arrayOfFloat[(l2v(paramInt1) * 8 + 2)];
/*      */     }
/*      */ 
/*  475 */     return f;
/*      */   }
/*      */ 
/*      */   public boolean caretAtOffsetIsValid(int paramInt)
/*      */   {
/*  487 */     if ((paramInt == 0) || (paramInt == this.source.getLength())) {
/*  488 */       return true;
/*      */     }
/*  490 */     int i = this.source.getChars()[(this.source.getStart() + paramInt)];
/*  491 */     if ((i == 9) || (i == 10) || (i == 13)) {
/*  492 */       return true;
/*      */     }
/*  494 */     int j = l2v(paramInt);
/*      */ 
/*  504 */     return getCharinfo()[(j * 8 + 2)] != 0.0F;
/*      */   }
/*      */ 
/*      */   private final float[] getCharinfo() {
/*  508 */     if (this.charinfo == null) {
/*  509 */       this.charinfo = createCharinfo();
/*      */     }
/*  511 */     return this.charinfo;
/*      */   }
/*      */ 
/*      */   protected float[] createCharinfo()
/*      */   {
/*  580 */     StandardGlyphVector localStandardGlyphVector = getGV();
/*  581 */     float[] arrayOfFloat = null;
/*      */     try {
/*  583 */       arrayOfFloat = localStandardGlyphVector.getGlyphInfo();
/*      */     }
/*      */     catch (Exception localException) {
/*  586 */       System.out.println(this.source);
/*      */     }
/*      */ 
/*  595 */     int i = localStandardGlyphVector.getNumGlyphs();
/*  596 */     int[] arrayOfInt = localStandardGlyphVector.getGlyphCharIndices(0, i, null);
/*      */ 
/*  598 */     int j = 0;
/*  599 */     if (j != 0) {
/*  600 */       System.err.println("number of glyphs: " + i);
/*  601 */       for (f1 = 0; f1 < i; f1++) {
/*  602 */         System.err.println("g: " + f1 + ", x: " + arrayOfFloat[(f1 * 8 + 0)] + ", a: " + arrayOfFloat[(f1 * 8 + 2)] + ", n: " + arrayOfInt[f1]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  609 */     float f1 = arrayOfInt[0];
/*  610 */     int k = f1;
/*  611 */     int m = 0;
/*  612 */     int n = 0;
/*  613 */     int i1 = 0;
/*  614 */     int i2 = 0;
/*  615 */     int i3 = 0;
/*  616 */     int i4 = i;
/*  617 */     int i5 = 8;
/*  618 */     int i6 = 1;
/*      */ 
/*  620 */     int i7 = (this.source.getLayoutFlags() & 0x1) == 0 ? 1 : 0;
/*  621 */     if (i7 == 0) {
/*  622 */       f1 = arrayOfInt[(i - 1)];
/*  623 */       k = f1;
/*  624 */       m = 0;
/*  625 */       n = arrayOfFloat.length - 8;
/*  626 */       i1 = 0;
/*  627 */       i2 = arrayOfFloat.length - 8;
/*  628 */       i3 = i - 1;
/*  629 */       i4 = -1;
/*  630 */       i5 = -8;
/*  631 */       i6 = -1;
/*      */     }
/*      */ 
/*  651 */     float f2 = 0.0F; float f3 = 0.0F; float f4 = 0.0F; float f5 = 0.0F; float f6 = 0.0F; float f7 = 0.0F;
/*  652 */     float f8 = 0.0F;
/*      */ 
/*  655 */     int i8 = 0;
/*      */     int i10;
/*      */     int i13;
/*      */     int i12;
/*  657 */     while (i3 != i4)
/*      */     {
/*  659 */       int i9 = 0;
/*  660 */       i10 = 0;
/*      */ 
/*  662 */       f1 = arrayOfInt[i3];
/*  663 */       k = f1;
/*      */ 
/*  666 */       i3 += i6;
/*  667 */       i2 += i5;
/*      */       float f9;
/*  673 */       while ((i3 != i4) && ((arrayOfFloat[(i2 + 2)] == 0.0F) || (f1 != m) || (arrayOfInt[i3] <= k) || (k - f1 > i10)))
/*      */       {
/*  679 */         if (i9 == 0) {
/*  680 */           int i11 = i2 - i5;
/*      */ 
/*  682 */           f2 = arrayOfFloat[(i11 + 0)];
/*  683 */           f3 = f2 + arrayOfFloat[(i11 + 2)];
/*  684 */           f4 = arrayOfFloat[(i11 + 4)];
/*  685 */           f5 = arrayOfFloat[(i11 + 5)];
/*  686 */           f6 = f4 + arrayOfFloat[(i11 + 6)];
/*  687 */           f7 = f5 + arrayOfFloat[(i11 + 7)];
/*      */ 
/*  689 */           i9 = 1;
/*      */         }
/*      */ 
/*  693 */         i10++;
/*      */ 
/*  696 */         f9 = arrayOfFloat[(i2 + 2)];
/*  697 */         if (f9 != 0.0F) {
/*  698 */           f10 = arrayOfFloat[(i2 + 0)];
/*  699 */           f2 = Math.min(f2, f10);
/*  700 */           f3 = Math.max(f3, f10 + f9);
/*      */         }
/*      */ 
/*  704 */         float f10 = arrayOfFloat[(i2 + 6)];
/*  705 */         if (f10 != 0.0F) {
/*  706 */           float f11 = arrayOfFloat[(i2 + 4)];
/*  707 */           float f12 = arrayOfFloat[(i2 + 5)];
/*  708 */           f4 = Math.min(f4, f11);
/*  709 */           f5 = Math.min(f5, f12);
/*  710 */           f6 = Math.max(f6, f11 + f10);
/*  711 */           f7 = Math.max(f7, f12 + arrayOfFloat[(i2 + 7)]);
/*      */         }
/*      */ 
/*  715 */         f1 = Math.min(f1, arrayOfInt[i3]);
/*  716 */         k = Math.max(k, arrayOfInt[i3]);
/*      */ 
/*  719 */         i3 += i6;
/*  720 */         i2 += i5;
/*      */       }
/*      */ 
/*  724 */       if (j != 0) {
/*  725 */         System.out.println("minIndex = " + f1 + ", maxIndex = " + k);
/*      */       }
/*      */ 
/*  728 */       m = k + 1;
/*      */ 
/*  731 */       arrayOfFloat[(n + 1)] = f8;
/*  732 */       arrayOfFloat[(n + 3)] = 0.0F;
/*      */ 
/*  734 */       if (i9 != 0)
/*      */       {
/*  736 */         arrayOfFloat[(n + 0)] = f2;
/*  737 */         arrayOfFloat[(n + 2)] = (f3 - f2);
/*  738 */         arrayOfFloat[(n + 4)] = f4;
/*  739 */         arrayOfFloat[(n + 5)] = f5;
/*  740 */         arrayOfFloat[(n + 6)] = (f6 - f4);
/*  741 */         arrayOfFloat[(n + 7)] = (f7 - f5);
/*      */ 
/*  746 */         if (k - f1 < i10) {
/*  747 */           i8 = 1;
/*      */         }
/*      */ 
/*  753 */         if (f1 < k) {
/*  754 */           if (i7 == 0)
/*      */           {
/*  756 */             f3 = f2;
/*      */           }
/*  758 */           f6 -= f4;
/*  759 */           f7 -= f5;
/*      */ 
/*  761 */           f9 = f1; i13 = n / 8;
/*      */ 
/*  763 */           while (f1 < k) {
/*  764 */             f1++;
/*  765 */             i1 += i6;
/*  766 */             n += i5;
/*      */ 
/*  768 */             if (((n < 0) || (n >= arrayOfFloat.length)) && 
/*  769 */               (j != 0)) System.out.println("minIndex = " + f9 + ", maxIndex = " + k + ", cp = " + i13);
/*      */ 
/*  772 */             arrayOfFloat[(n + 0)] = f3;
/*  773 */             arrayOfFloat[(n + 1)] = f8;
/*  774 */             arrayOfFloat[(n + 2)] = 0.0F;
/*  775 */             arrayOfFloat[(n + 3)] = 0.0F;
/*  776 */             arrayOfFloat[(n + 4)] = f4;
/*  777 */             arrayOfFloat[(n + 5)] = f5;
/*  778 */             arrayOfFloat[(n + 6)] = f6;
/*  779 */             arrayOfFloat[(n + 7)] = f7;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  784 */         i9 = 0;
/*  785 */       } else if (i8 != 0)
/*      */       {
/*  787 */         i12 = i2 - i5;
/*      */ 
/*  789 */         arrayOfFloat[(n + 0)] = arrayOfFloat[(i12 + 0)];
/*  790 */         arrayOfFloat[(n + 2)] = arrayOfFloat[(i12 + 2)];
/*  791 */         arrayOfFloat[(n + 4)] = arrayOfFloat[(i12 + 4)];
/*  792 */         arrayOfFloat[(n + 5)] = arrayOfFloat[(i12 + 5)];
/*  793 */         arrayOfFloat[(n + 6)] = arrayOfFloat[(i12 + 6)];
/*  794 */         arrayOfFloat[(n + 7)] = arrayOfFloat[(i12 + 7)];
/*      */       }
/*      */ 
/*  799 */       n += i5;
/*  800 */       i1 += i6;
/*      */     }
/*      */ 
/*  803 */     if ((i8 != 0) && (i7 == 0))
/*      */     {
/*  806 */       n -= i5;
/*  807 */       System.arraycopy(arrayOfFloat, n, arrayOfFloat, 0, arrayOfFloat.length - n);
/*      */     }
/*      */     char[] arrayOfChar;
/*  810 */     if (j != 0) {
/*  811 */       arrayOfChar = this.source.getChars();
/*  812 */       i10 = this.source.getStart();
/*  813 */       i12 = this.source.getLength();
/*  814 */       System.out.println("char info for " + i12 + " characters");
/*  815 */       for (i13 = 0; i13 < i12 * 8; ) {
/*  816 */         System.out.println(" ch: " + Integer.toHexString(arrayOfChar[(i10 + v2l(i13 / 8))]) + " x: " + arrayOfFloat[(i13++)] + " y: " + arrayOfFloat[(i13++)] + " xa: " + arrayOfFloat[(i13++)] + " ya: " + arrayOfFloat[(i13++)] + " l: " + arrayOfFloat[(i13++)] + " t: " + arrayOfFloat[(i13++)] + " w: " + arrayOfFloat[(i13++)] + " h: " + arrayOfFloat[(i13++)]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  828 */     return arrayOfFloat;
/*      */   }
/*      */ 
/*      */   protected int l2v(int paramInt)
/*      */   {
/*  837 */     return (this.source.getLayoutFlags() & 0x1) == 0 ? paramInt : this.source.getLength() - 1 - paramInt;
/*      */   }
/*      */ 
/*      */   protected int v2l(int paramInt)
/*      */   {
/*  846 */     return (this.source.getLayoutFlags() & 0x1) == 0 ? paramInt : this.source.getLength() - 1 - paramInt;
/*      */   }
/*      */ 
/*      */   public TextLineComponent getSubset(int paramInt1, int paramInt2, int paramInt3) {
/*  850 */     return new ExtendedTextSourceLabel(this.source.getSubSource(paramInt1, paramInt2 - paramInt1, paramInt3), this.decorator);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  855 */     return this.source.toString(false);
/*      */   }
/*      */ 
/*      */   public int getNumJustificationInfos()
/*      */   {
/*  889 */     return getGV().getNumGlyphs();
/*      */   }
/*      */ 
/*      */   public void getJustificationInfos(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  899 */     StandardGlyphVector localStandardGlyphVector = getGV();
/*      */ 
/*  901 */     float[] arrayOfFloat = getCharinfo();
/*      */ 
/*  903 */     float f = localStandardGlyphVector.getFont().getSize2D();
/*      */ 
/*  905 */     GlyphJustificationInfo localGlyphJustificationInfo1 = new GlyphJustificationInfo(0.0F, false, 3, 0.0F, 0.0F, false, 3, 0.0F, 0.0F);
/*      */ 
/*  910 */     GlyphJustificationInfo localGlyphJustificationInfo2 = new GlyphJustificationInfo(f, true, 1, 0.0F, f, true, 1, 0.0F, f / 4.0F);
/*      */ 
/*  915 */     GlyphJustificationInfo localGlyphJustificationInfo3 = new GlyphJustificationInfo(f, true, 2, f, f, false, 3, 0.0F, 0.0F);
/*      */ 
/*  920 */     char[] arrayOfChar = this.source.getChars();
/*  921 */     int i = this.source.getStart();
/*      */ 
/*  925 */     int j = localStandardGlyphVector.getNumGlyphs();
/*  926 */     int k = 0;
/*  927 */     int m = j;
/*  928 */     int n = (this.source.getLayoutFlags() & 0x1) == 0 ? 1 : 0;
/*  929 */     if ((paramInt2 != 0) || (paramInt3 != this.source.getLength())) {
/*  930 */       if (n != 0) {
/*  931 */         k = paramInt2;
/*  932 */         m = paramInt3;
/*      */       } else {
/*  934 */         k = j - paramInt3;
/*  935 */         m = j - paramInt2;
/*      */       }
/*      */     }
/*      */ 
/*  939 */     for (int i1 = 0; i1 < j; i1++) {
/*  940 */       GlyphJustificationInfo localGlyphJustificationInfo4 = null;
/*  941 */       if ((i1 >= k) && (i1 < m)) {
/*  942 */         if (arrayOfFloat[(i1 * 8 + 2)] == 0.0F) {
/*  943 */           localGlyphJustificationInfo4 = localGlyphJustificationInfo1;
/*      */         } else {
/*  945 */           int i2 = v2l(i1);
/*  946 */           int i3 = arrayOfChar[(i + i2)];
/*  947 */           if (Character.isWhitespace(i3)) {
/*  948 */             localGlyphJustificationInfo4 = localGlyphJustificationInfo2;
/*      */           }
/*  950 */           else if (((i3 >= 19968) && (i3 < 40960)) || ((i3 >= 44032) && (i3 < 55216)) || ((i3 >= 63744) && (i3 < 64256)))
/*      */           {
/*  954 */             localGlyphJustificationInfo4 = localGlyphJustificationInfo3;
/*      */           }
/*  956 */           else localGlyphJustificationInfo4 = localGlyphJustificationInfo1;
/*      */         }
/*      */       }
/*      */ 
/*  960 */       paramArrayOfGlyphJustificationInfo[(paramInt1 + i1)] = localGlyphJustificationInfo4;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TextLineComponent applyJustificationDeltas(float[] paramArrayOfFloat, int paramInt, boolean[] paramArrayOfBoolean)
/*      */   {
/*  969 */     float[] arrayOfFloat1 = (float[])getCharinfo().clone();
/*      */ 
/*  972 */     paramArrayOfBoolean[0] = false;
/*      */ 
/*  976 */     StandardGlyphVector localStandardGlyphVector = (StandardGlyphVector)getGV().clone();
/*  977 */     float[] arrayOfFloat2 = localStandardGlyphVector.getGlyphPositions(null);
/*  978 */     int i = localStandardGlyphVector.getNumGlyphs();
/*      */ 
/*  993 */     char[] arrayOfChar = this.source.getChars();
/*  994 */     int j = this.source.getStart();
/*      */ 
/* 1000 */     float f1 = 0.0F;
/* 1001 */     for (int k = 0; k < i; k++) {
/* 1002 */       if (Character.isWhitespace(arrayOfChar[(j + v2l(k))])) {
/* 1003 */         arrayOfFloat2[(k * 2)] += f1;
/*      */ 
/* 1005 */         float f2 = paramArrayOfFloat[(paramInt + k * 2)] + paramArrayOfFloat[(paramInt + k * 2 + 1)];
/*      */ 
/* 1007 */         arrayOfFloat1[(k * 8 + 0)] += f1;
/* 1008 */         arrayOfFloat1[(k * 8 + 4)] += f1;
/* 1009 */         arrayOfFloat1[(k * 8 + 2)] += f2;
/*      */ 
/* 1011 */         f1 += f2;
/*      */       } else {
/* 1013 */         f1 += paramArrayOfFloat[(paramInt + k * 2)];
/*      */ 
/* 1015 */         arrayOfFloat2[(k * 2)] += f1;
/* 1016 */         arrayOfFloat1[(k * 8 + 0)] += f1;
/* 1017 */         arrayOfFloat1[(k * 8 + 4)] += f1;
/*      */ 
/* 1019 */         f1 += paramArrayOfFloat[(paramInt + k * 2 + 1)];
/*      */       }
/*      */     }
/* 1022 */     arrayOfFloat2[(i * 2)] += f1;
/*      */ 
/* 1024 */     localStandardGlyphVector.setGlyphPositions(arrayOfFloat2);
/*      */ 
/* 1034 */     ExtendedTextSourceLabel localExtendedTextSourceLabel = new ExtendedTextSourceLabel(this.source, this.decorator);
/* 1035 */     localExtendedTextSourceLabel.gv = localStandardGlyphVector;
/* 1036 */     localExtendedTextSourceLabel.charinfo = arrayOfFloat1;
/*      */ 
/* 1038 */     return localExtendedTextSourceLabel;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.ExtendedTextSourceLabel
 * JD-Core Version:    0.6.2
 */