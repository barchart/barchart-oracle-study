/*      */ package sun.java2d.pisces;
/*      */ 
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import sun.awt.geom.PathConsumer2D;
/*      */ 
/*      */ final class Stroker
/*      */   implements PathConsumer2D
/*      */ {
/*      */   private static final int MOVE_TO = 0;
/*      */   private static final int DRAWING_OP_TO = 1;
/*      */   private static final int CLOSE = 2;
/*      */   public static final int JOIN_MITER = 0;
/*      */   public static final int JOIN_ROUND = 1;
/*      */   public static final int JOIN_BEVEL = 2;
/*      */   public static final int CAP_BUTT = 0;
/*      */   public static final int CAP_ROUND = 1;
/*      */   public static final int CAP_SQUARE = 2;
/*      */   private final PathConsumer2D out;
/*      */   private final int capStyle;
/*      */   private final int joinStyle;
/*      */   private final float lineWidth2;
/*   81 */   private final float[][] offset = new float[3][2];
/*   82 */   private final float[] miter = new float[2];
/*      */   private final float miterLimitSq;
/*      */   private int prev;
/*      */   private float sx0;
/*      */   private float sy0;
/*      */   private float sdx;
/*      */   private float sdy;
/*      */   private float cx0;
/*      */   private float cy0;
/*      */   private float cdx;
/*      */   private float cdy;
/*      */   private float smx;
/*      */   private float smy;
/*      */   private float cmx;
/*      */   private float cmy;
/*   99 */   private final PolyStack reverse = new PolyStack();
/*      */   private static final float ROUND_JOIN_THRESHOLD = 0.01525879F;
/*  770 */   private float[] middle = new float[16];
/*  771 */   private float[] lp = new float[8];
/*  772 */   private float[] rp = new float[8];
/*      */   private static final int MAX_N_CURVES = 11;
/*  774 */   private float[] subdivTs = new float[10];
/*      */ 
/*  885 */   private static Curve c = new Curve();
/*      */ 
/*      */   public Stroker(PathConsumer2D paramPathConsumer2D, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2)
/*      */   {
/*  120 */     this.out = paramPathConsumer2D;
/*      */ 
/*  122 */     this.lineWidth2 = (paramFloat1 / 2.0F);
/*  123 */     this.capStyle = paramInt1;
/*  124 */     this.joinStyle = paramInt2;
/*      */ 
/*  126 */     float f = paramFloat2 * this.lineWidth2;
/*  127 */     this.miterLimitSq = (f * f);
/*      */ 
/*  129 */     this.prev = 2;
/*      */   }
/*      */ 
/*      */   private static void computeOffset(float paramFloat1, float paramFloat2, float paramFloat3, float[] paramArrayOfFloat)
/*      */   {
/*  135 */     float f = (float)Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2);
/*  136 */     if (f == 0.0F)
/*      */     {
/*      */       float tmp26_25 = 0.0F; paramArrayOfFloat[1] = tmp26_25; paramArrayOfFloat[0] = tmp26_25;
/*      */     } else {
/*  139 */       paramArrayOfFloat[0] = (paramFloat2 * paramFloat3 / f);
/*  140 */       paramArrayOfFloat[1] = (-(paramFloat1 * paramFloat3) / f);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean isCW(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*      */   {
/*  155 */     return paramFloat1 * paramFloat4 <= paramFloat2 * paramFloat3;
/*      */   }
/*      */ 
/*      */   private void drawRoundJoin(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, boolean paramBoolean, float paramFloat7)
/*      */   {
/*  168 */     if (((paramFloat3 == 0.0F) && (paramFloat4 == 0.0F)) || ((paramFloat5 == 0.0F) && (paramFloat6 == 0.0F))) {
/*  169 */       return;
/*      */     }
/*      */ 
/*  172 */     float f1 = paramFloat3 - paramFloat5;
/*  173 */     float f2 = paramFloat4 - paramFloat6;
/*  174 */     float f3 = f1 * f1 + f2 * f2;
/*  175 */     if (f3 < paramFloat7) {
/*  176 */       return;
/*      */     }
/*      */ 
/*  179 */     if (paramBoolean) {
/*  180 */       paramFloat3 = -paramFloat3;
/*  181 */       paramFloat4 = -paramFloat4;
/*  182 */       paramFloat5 = -paramFloat5;
/*  183 */       paramFloat6 = -paramFloat6;
/*      */     }
/*  185 */     drawRoundJoin(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramBoolean);
/*      */   }
/*      */ 
/*      */   private void drawRoundJoin(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, boolean paramBoolean)
/*      */   {
/*  196 */     double d = paramFloat3 * paramFloat5 + paramFloat4 * paramFloat6;
/*      */ 
/*  200 */     int i = d >= 0.0D ? 1 : 2;
/*      */ 
/*  202 */     switch (i) {
/*      */     case 1:
/*  204 */       drawBezApproxForArc(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramBoolean);
/*  205 */       break;
/*      */     case 2:
/*  221 */       float f1 = paramFloat6 - paramFloat4; float f2 = paramFloat3 - paramFloat5;
/*  222 */       float f3 = (float)Math.sqrt(f1 * f1 + f2 * f2);
/*  223 */       float f4 = this.lineWidth2 / f3;
/*  224 */       float f5 = f1 * f4; float f6 = f2 * f4;
/*      */ 
/*  229 */       if (paramBoolean) {
/*  230 */         f5 = -f5;
/*  231 */         f6 = -f6;
/*      */       }
/*  233 */       drawBezApproxForArc(paramFloat1, paramFloat2, paramFloat3, paramFloat4, f5, f6, paramBoolean);
/*  234 */       drawBezApproxForArc(paramFloat1, paramFloat2, f5, f6, paramFloat5, paramFloat6, paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void drawBezApproxForArc(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, boolean paramBoolean)
/*      */   {
/*  245 */     float f1 = (paramFloat3 * paramFloat5 + paramFloat4 * paramFloat6) / (2.0F * this.lineWidth2 * this.lineWidth2);
/*      */ 
/*  251 */     float f2 = (float)(1.333333333333333D * Math.sqrt(0.5D - f1) / (1.0D + Math.sqrt(f1 + 0.5D)));
/*      */ 
/*  254 */     if (paramBoolean) {
/*  255 */       f2 = -f2;
/*      */     }
/*  257 */     float f3 = paramFloat1 + paramFloat3;
/*  258 */     float f4 = paramFloat2 + paramFloat4;
/*  259 */     float f5 = f3 - f2 * paramFloat4;
/*  260 */     float f6 = f4 + f2 * paramFloat3;
/*      */ 
/*  262 */     float f7 = paramFloat1 + paramFloat5;
/*  263 */     float f8 = paramFloat2 + paramFloat6;
/*  264 */     float f9 = f7 + f2 * paramFloat6;
/*  265 */     float f10 = f8 - f2 * paramFloat5;
/*      */ 
/*  267 */     emitCurveTo(f3, f4, f5, f6, f9, f10, f7, f8, paramBoolean);
/*      */   }
/*      */ 
/*      */   private void drawRoundCap(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*      */   {
/*  277 */     emitCurveTo(paramFloat1 + paramFloat3, paramFloat2 + paramFloat4, paramFloat1 + paramFloat3 - 0.5522848F * paramFloat4, paramFloat2 + paramFloat4 + 0.5522848F * paramFloat3, paramFloat1 - paramFloat4 + 0.5522848F * paramFloat3, paramFloat2 + paramFloat3 + 0.5522848F * paramFloat4, paramFloat1 - paramFloat4, paramFloat2 + paramFloat3, false);
/*      */ 
/*  282 */     emitCurveTo(paramFloat1 - paramFloat4, paramFloat2 + paramFloat3, paramFloat1 - paramFloat4 - 0.5522848F * paramFloat3, paramFloat2 + paramFloat3 - 0.5522848F * paramFloat4, paramFloat1 - paramFloat3 - 0.5522848F * paramFloat4, paramFloat2 - paramFloat4 + 0.5522848F * paramFloat3, paramFloat1 - paramFloat3, paramFloat2 - paramFloat4, false);
/*      */   }
/*      */ 
/*      */   private void computeIntersection(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float[] paramArrayOfFloat, int paramInt)
/*      */   {
/*  298 */     float f1 = paramFloat3 - paramFloat1;
/*  299 */     float f2 = paramFloat4 - paramFloat2;
/*  300 */     float f3 = paramFloat7 - paramFloat5;
/*  301 */     float f4 = paramFloat8 - paramFloat6;
/*      */ 
/*  303 */     float f5 = f1 * f4 - f3 * f2;
/*  304 */     float f6 = f3 * (paramFloat2 - paramFloat6) - f4 * (paramFloat1 - paramFloat5);
/*  305 */     f6 /= f5;
/*  306 */     paramArrayOfFloat[(paramInt++)] = (paramFloat1 + f6 * f1);
/*  307 */     paramArrayOfFloat[paramInt] = (paramFloat2 + f6 * f2);
/*      */   }
/*      */ 
/*      */   private void drawMiter(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10, boolean paramBoolean)
/*      */   {
/*  316 */     if (((paramFloat9 == paramFloat7) && (paramFloat10 == paramFloat8)) || ((paramFloat1 == 0.0F) && (paramFloat2 == 0.0F)) || ((paramFloat5 == 0.0F) && (paramFloat6 == 0.0F)))
/*      */     {
/*  320 */       return;
/*      */     }
/*      */ 
/*  323 */     if (paramBoolean) {
/*  324 */       paramFloat7 = -paramFloat7;
/*  325 */       paramFloat8 = -paramFloat8;
/*  326 */       paramFloat9 = -paramFloat9;
/*  327 */       paramFloat10 = -paramFloat10;
/*      */     }
/*      */ 
/*  330 */     computeIntersection(paramFloat3 - paramFloat1 + paramFloat7, paramFloat4 - paramFloat2 + paramFloat8, paramFloat3 + paramFloat7, paramFloat4 + paramFloat8, paramFloat5 + paramFloat3 + paramFloat9, paramFloat6 + paramFloat4 + paramFloat10, paramFloat3 + paramFloat9, paramFloat4 + paramFloat10, this.miter, 0);
/*      */ 
/*  334 */     float f = (this.miter[0] - paramFloat3) * (this.miter[0] - paramFloat3) + (this.miter[1] - paramFloat4) * (this.miter[1] - paramFloat4);
/*      */ 
/*  341 */     if (f < this.miterLimitSq)
/*  342 */       emitLineTo(this.miter[0], this.miter[1], paramBoolean);
/*      */   }
/*      */ 
/*      */   public void moveTo(float paramFloat1, float paramFloat2)
/*      */   {
/*  347 */     if (this.prev == 1) {
/*  348 */       finish();
/*      */     }
/*  350 */     this.sx0 = (this.cx0 = paramFloat1);
/*  351 */     this.sy0 = (this.cy0 = paramFloat2);
/*  352 */     this.cdx = (this.sdx = 1.0F);
/*  353 */     this.cdy = (this.sdy = 0.0F);
/*  354 */     this.prev = 0;
/*      */   }
/*      */ 
/*      */   public void lineTo(float paramFloat1, float paramFloat2) {
/*  358 */     float f1 = paramFloat1 - this.cx0;
/*  359 */     float f2 = paramFloat2 - this.cy0;
/*  360 */     if ((f1 == 0.0F) && (f2 == 0.0F)) {
/*  361 */       f1 = 1.0F;
/*      */     }
/*  363 */     computeOffset(f1, f2, this.lineWidth2, this.offset[0]);
/*  364 */     float f3 = this.offset[0][0];
/*  365 */     float f4 = this.offset[0][1];
/*      */ 
/*  367 */     drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, f1, f2, this.cmx, this.cmy, f3, f4);
/*      */ 
/*  369 */     emitLineTo(this.cx0 + f3, this.cy0 + f4);
/*  370 */     emitLineTo(paramFloat1 + f3, paramFloat2 + f4);
/*      */ 
/*  372 */     emitLineTo(this.cx0 - f3, this.cy0 - f4, true);
/*  373 */     emitLineTo(paramFloat1 - f3, paramFloat2 - f4, true);
/*      */ 
/*  375 */     this.cmx = f3;
/*  376 */     this.cmy = f4;
/*  377 */     this.cdx = f1;
/*  378 */     this.cdy = f2;
/*  379 */     this.cx0 = paramFloat1;
/*  380 */     this.cy0 = paramFloat2;
/*  381 */     this.prev = 1;
/*      */   }
/*      */ 
/*      */   public void closePath() {
/*  385 */     if (this.prev != 1) {
/*  386 */       if (this.prev == 2) {
/*  387 */         return;
/*      */       }
/*  389 */       emitMoveTo(this.cx0, this.cy0 - this.lineWidth2);
/*  390 */       this.cmx = (this.smx = 0.0F);
/*  391 */       this.cmy = (this.smy = -this.lineWidth2);
/*  392 */       this.cdx = (this.sdx = 1.0F);
/*  393 */       this.cdy = (this.sdy = 0.0F);
/*  394 */       finish();
/*  395 */       return;
/*      */     }
/*      */ 
/*  398 */     if ((this.cx0 != this.sx0) || (this.cy0 != this.sy0)) {
/*  399 */       lineTo(this.sx0, this.sy0);
/*      */     }
/*      */ 
/*  402 */     drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, this.sdx, this.sdy, this.cmx, this.cmy, this.smx, this.smy);
/*      */ 
/*  404 */     emitLineTo(this.sx0 + this.smx, this.sy0 + this.smy);
/*      */ 
/*  406 */     emitMoveTo(this.sx0 - this.smx, this.sy0 - this.smy);
/*  407 */     emitReverse();
/*      */ 
/*  409 */     this.prev = 2;
/*  410 */     emitClose();
/*      */   }
/*      */ 
/*      */   private void emitReverse() {
/*  414 */     while (!this.reverse.isEmpty())
/*  415 */       this.reverse.pop(this.out);
/*      */   }
/*      */ 
/*      */   public void pathDone()
/*      */   {
/*  420 */     if (this.prev == 1) {
/*  421 */       finish();
/*      */     }
/*      */ 
/*  424 */     this.out.pathDone();
/*      */ 
/*  427 */     this.prev = 2;
/*      */   }
/*      */ 
/*      */   private void finish() {
/*  431 */     if (this.capStyle == 1) {
/*  432 */       drawRoundCap(this.cx0, this.cy0, this.cmx, this.cmy);
/*  433 */     } else if (this.capStyle == 2) {
/*  434 */       emitLineTo(this.cx0 - this.cmy + this.cmx, this.cy0 + this.cmx + this.cmy);
/*  435 */       emitLineTo(this.cx0 - this.cmy - this.cmx, this.cy0 + this.cmx - this.cmy);
/*      */     }
/*      */ 
/*  438 */     emitReverse();
/*      */ 
/*  440 */     if (this.capStyle == 1) {
/*  441 */       drawRoundCap(this.sx0, this.sy0, -this.smx, -this.smy);
/*  442 */     } else if (this.capStyle == 2) {
/*  443 */       emitLineTo(this.sx0 + this.smy - this.smx, this.sy0 - this.smx - this.smy);
/*  444 */       emitLineTo(this.sx0 + this.smy + this.smx, this.sy0 - this.smx + this.smy);
/*      */     }
/*      */ 
/*  447 */     emitClose();
/*      */   }
/*      */ 
/*      */   private void emitMoveTo(float paramFloat1, float paramFloat2) {
/*  451 */     this.out.moveTo(paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   private void emitLineTo(float paramFloat1, float paramFloat2) {
/*  455 */     this.out.lineTo(paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   private void emitLineTo(float paramFloat1, float paramFloat2, boolean paramBoolean)
/*      */   {
/*  461 */     if (paramBoolean)
/*  462 */       this.reverse.pushLine(paramFloat1, paramFloat2);
/*      */     else
/*  464 */       emitLineTo(paramFloat1, paramFloat2);
/*      */   }
/*      */ 
/*      */   private void emitQuadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, boolean paramBoolean)
/*      */   {
/*  472 */     if (paramBoolean)
/*  473 */       this.reverse.pushQuad(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
/*      */     else
/*  475 */       this.out.quadTo(paramFloat3, paramFloat4, paramFloat5, paramFloat6);
/*      */   }
/*      */ 
/*      */   private void emitCurveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, boolean paramBoolean)
/*      */   {
/*  484 */     if (paramBoolean)
/*  485 */       this.reverse.pushCubic(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
/*      */     else
/*  487 */       this.out.curveTo(paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8);
/*      */   }
/*      */ 
/*      */   private void emitClose()
/*      */   {
/*  492 */     this.out.closePath();
/*      */   }
/*      */ 
/*      */   private void drawJoin(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10)
/*      */   {
/*  501 */     if (this.prev != 1) {
/*  502 */       emitMoveTo(paramFloat3 + paramFloat9, paramFloat4 + paramFloat10);
/*  503 */       this.sdx = paramFloat5;
/*  504 */       this.sdy = paramFloat6;
/*  505 */       this.smx = paramFloat9;
/*  506 */       this.smy = paramFloat10;
/*      */     } else {
/*  508 */       boolean bool = isCW(paramFloat1, paramFloat2, paramFloat5, paramFloat6);
/*  509 */       if (this.joinStyle == 0)
/*  510 */         drawMiter(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8, paramFloat9, paramFloat10, bool);
/*  511 */       else if (this.joinStyle == 1) {
/*  512 */         drawRoundJoin(paramFloat3, paramFloat4, paramFloat7, paramFloat8, paramFloat9, paramFloat10, bool, 0.01525879F);
/*      */       }
/*      */ 
/*  517 */       emitLineTo(paramFloat3, paramFloat4, !bool);
/*      */     }
/*  519 */     this.prev = 1;
/*      */   }
/*      */ 
/*      */   private static boolean within(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
/*      */   {
/*  526 */     assert (paramFloat5 > 0.0F) : "";
/*      */ 
/*  529 */     return (Helpers.within(paramFloat1, paramFloat3, paramFloat5)) && (Helpers.within(paramFloat2, paramFloat4, paramFloat5));
/*      */   }
/*      */ 
/*      */   private void getLineOffsets(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
/*      */   {
/*  536 */     computeOffset(paramFloat3 - paramFloat1, paramFloat4 - paramFloat2, this.lineWidth2, this.offset[0]);
/*  537 */     paramArrayOfFloat1[0] = (paramFloat1 + this.offset[0][0]);
/*  538 */     paramArrayOfFloat1[1] = (paramFloat2 + this.offset[0][1]);
/*  539 */     paramArrayOfFloat1[2] = (paramFloat3 + this.offset[0][0]);
/*  540 */     paramArrayOfFloat1[3] = (paramFloat4 + this.offset[0][1]);
/*  541 */     paramArrayOfFloat2[0] = (paramFloat1 - this.offset[0][0]);
/*  542 */     paramArrayOfFloat2[1] = (paramFloat2 - this.offset[0][1]);
/*  543 */     paramArrayOfFloat2[2] = (paramFloat3 - this.offset[0][0]);
/*  544 */     paramArrayOfFloat2[3] = (paramFloat4 - this.offset[0][1]);
/*      */   }
/*      */ 
/*      */   private int computeOffsetCubic(float[] paramArrayOfFloat1, int paramInt, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
/*      */   {
/*  557 */     float f1 = paramArrayOfFloat1[(paramInt + 0)]; float f2 = paramArrayOfFloat1[(paramInt + 1)];
/*  558 */     float f3 = paramArrayOfFloat1[(paramInt + 2)]; float f4 = paramArrayOfFloat1[(paramInt + 3)];
/*  559 */     float f5 = paramArrayOfFloat1[(paramInt + 4)]; float f6 = paramArrayOfFloat1[(paramInt + 5)];
/*  560 */     float f7 = paramArrayOfFloat1[(paramInt + 6)]; float f8 = paramArrayOfFloat1[(paramInt + 7)];
/*      */ 
/*  562 */     float f9 = f7 - f5;
/*  563 */     float f10 = f8 - f6;
/*  564 */     float f11 = f3 - f1;
/*  565 */     float f12 = f4 - f2;
/*      */ 
/*  569 */     boolean bool1 = within(f1, f2, f3, f4, 6.0F * Math.ulp(f4));
/*  570 */     boolean bool2 = within(f5, f6, f7, f8, 6.0F * Math.ulp(f8));
/*  571 */     if ((bool1) && (bool2)) {
/*  572 */       getLineOffsets(f1, f2, f7, f8, paramArrayOfFloat2, paramArrayOfFloat3);
/*  573 */       return 4;
/*  574 */     }if (bool1) {
/*  575 */       f11 = f5 - f1;
/*  576 */       f12 = f6 - f2;
/*  577 */     } else if (bool2) {
/*  578 */       f9 = f7 - f3;
/*  579 */       f10 = f8 - f4;
/*      */     }
/*      */ 
/*  583 */     float f13 = f11 * f9 + f12 * f10;
/*  584 */     f13 *= f13;
/*  585 */     float f14 = f11 * f11 + f12 * f12; float f15 = f9 * f9 + f10 * f10;
/*  586 */     if (Helpers.within(f13, f14 * f15, 4.0F * Math.ulp(f13))) {
/*  587 */       getLineOffsets(f1, f2, f7, f8, paramArrayOfFloat2, paramArrayOfFloat3);
/*  588 */       return 4;
/*      */     }
/*      */ 
/*  638 */     float f16 = 0.125F * (f1 + 3.0F * (f3 + f5) + f7);
/*  639 */     float f17 = 0.125F * (f2 + 3.0F * (f4 + f6) + f8);
/*      */ 
/*  642 */     float f18 = f5 + f7 - f1 - f3; float f19 = f6 + f8 - f2 - f4;
/*      */ 
/*  647 */     computeOffset(f11, f12, this.lineWidth2, this.offset[0]);
/*  648 */     computeOffset(f18, f19, this.lineWidth2, this.offset[1]);
/*  649 */     computeOffset(f9, f10, this.lineWidth2, this.offset[2]);
/*  650 */     float f20 = f1 + this.offset[0][0];
/*  651 */     float f21 = f2 + this.offset[0][1];
/*  652 */     float f22 = f16 + this.offset[1][0];
/*  653 */     float f23 = f17 + this.offset[1][1];
/*  654 */     float f24 = f7 + this.offset[2][0];
/*  655 */     float f25 = f8 + this.offset[2][1];
/*      */ 
/*  657 */     float f26 = 4.0F / (3.0F * (f11 * f10 - f12 * f9));
/*      */ 
/*  659 */     float f27 = 2.0F * f22 - f20 - f24;
/*  660 */     float f28 = 2.0F * f23 - f21 - f25;
/*  661 */     float f29 = f26 * (f10 * f27 - f9 * f28);
/*  662 */     float f30 = f26 * (f11 * f28 - f12 * f27);
/*      */ 
/*  665 */     float f31 = f20 + f29 * f11;
/*  666 */     float f32 = f21 + f29 * f12;
/*  667 */     float f33 = f24 + f30 * f9;
/*  668 */     float f34 = f25 + f30 * f10;
/*      */ 
/*  670 */     paramArrayOfFloat2[0] = f20; paramArrayOfFloat2[1] = f21;
/*  671 */     paramArrayOfFloat2[2] = f31; paramArrayOfFloat2[3] = f32;
/*  672 */     paramArrayOfFloat2[4] = f33; paramArrayOfFloat2[5] = f34;
/*  673 */     paramArrayOfFloat2[6] = f24; paramArrayOfFloat2[7] = f25;
/*      */ 
/*  675 */     f20 = f1 - this.offset[0][0]; f21 = f2 - this.offset[0][1];
/*  676 */     f22 -= 2.0F * this.offset[1][0]; f23 -= 2.0F * this.offset[1][1];
/*  677 */     f24 = f7 - this.offset[2][0]; f25 = f8 - this.offset[2][1];
/*      */ 
/*  679 */     f27 = 2.0F * f22 - f20 - f24;
/*  680 */     f28 = 2.0F * f23 - f21 - f25;
/*  681 */     f29 = f26 * (f10 * f27 - f9 * f28);
/*  682 */     f30 = f26 * (f11 * f28 - f12 * f27);
/*      */ 
/*  684 */     f31 = f20 + f29 * f11;
/*  685 */     f32 = f21 + f29 * f12;
/*  686 */     f33 = f24 + f30 * f9;
/*  687 */     f34 = f25 + f30 * f10;
/*      */ 
/*  689 */     paramArrayOfFloat3[0] = f20; paramArrayOfFloat3[1] = f21;
/*  690 */     paramArrayOfFloat3[2] = f31; paramArrayOfFloat3[3] = f32;
/*  691 */     paramArrayOfFloat3[4] = f33; paramArrayOfFloat3[5] = f34;
/*  692 */     paramArrayOfFloat3[6] = f24; paramArrayOfFloat3[7] = f25;
/*  693 */     return 8;
/*      */   }
/*      */ 
/*      */   private int computeOffsetQuad(float[] paramArrayOfFloat1, int paramInt, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
/*      */   {
/*  700 */     float f1 = paramArrayOfFloat1[(paramInt + 0)]; float f2 = paramArrayOfFloat1[(paramInt + 1)];
/*  701 */     float f3 = paramArrayOfFloat1[(paramInt + 2)]; float f4 = paramArrayOfFloat1[(paramInt + 3)];
/*  702 */     float f5 = paramArrayOfFloat1[(paramInt + 4)]; float f6 = paramArrayOfFloat1[(paramInt + 5)];
/*      */ 
/*  704 */     float f7 = f5 - f3;
/*  705 */     float f8 = f6 - f4;
/*  706 */     float f9 = f3 - f1;
/*  707 */     float f10 = f4 - f2;
/*      */ 
/*  710 */     computeOffset(f9, f10, this.lineWidth2, this.offset[0]);
/*  711 */     computeOffset(f7, f8, this.lineWidth2, this.offset[1]);
/*      */ 
/*  713 */     paramArrayOfFloat2[0] = (f1 + this.offset[0][0]); paramArrayOfFloat2[1] = (f2 + this.offset[0][1]);
/*  714 */     paramArrayOfFloat2[4] = (f5 + this.offset[1][0]); paramArrayOfFloat2[5] = (f6 + this.offset[1][1]);
/*  715 */     paramArrayOfFloat3[0] = (f1 - this.offset[0][0]); paramArrayOfFloat3[1] = (f2 - this.offset[0][1]);
/*  716 */     paramArrayOfFloat3[4] = (f5 - this.offset[1][0]); paramArrayOfFloat3[5] = (f6 - this.offset[1][1]);
/*      */ 
/*  718 */     float f11 = paramArrayOfFloat2[0];
/*  719 */     float f12 = paramArrayOfFloat2[1];
/*  720 */     float f13 = paramArrayOfFloat2[4];
/*  721 */     float f14 = paramArrayOfFloat2[5];
/*      */ 
/*  732 */     computeIntersection(f11, f12, f11 + f9, f12 + f10, f13, f14, f13 - f7, f14 - f8, paramArrayOfFloat2, 2);
/*  733 */     float f15 = paramArrayOfFloat2[2];
/*  734 */     float f16 = paramArrayOfFloat2[3];
/*      */ 
/*  736 */     if ((!isFinite(f15)) || (!isFinite(f16)))
/*      */     {
/*  738 */       f11 = paramArrayOfFloat3[0];
/*  739 */       f12 = paramArrayOfFloat3[1];
/*  740 */       f13 = paramArrayOfFloat3[4];
/*  741 */       f14 = paramArrayOfFloat3[5];
/*  742 */       computeIntersection(f11, f12, f11 + f9, f12 + f10, f13, f14, f13 - f7, f14 - f8, paramArrayOfFloat3, 2);
/*  743 */       f15 = paramArrayOfFloat3[2];
/*  744 */       f16 = paramArrayOfFloat3[3];
/*  745 */       if ((!isFinite(f15)) || (!isFinite(f16)))
/*      */       {
/*  747 */         getLineOffsets(f1, f2, f5, f6, paramArrayOfFloat2, paramArrayOfFloat3);
/*  748 */         return 4;
/*      */       }
/*      */ 
/*  751 */       paramArrayOfFloat2[2] = (2.0F * f3 - f15);
/*  752 */       paramArrayOfFloat2[3] = (2.0F * f4 - f16);
/*  753 */       return 6;
/*      */     }
/*      */ 
/*  758 */     paramArrayOfFloat3[2] = (2.0F * f3 - f15);
/*  759 */     paramArrayOfFloat3[3] = (2.0F * f4 - f16);
/*  760 */     return 6;
/*      */   }
/*      */ 
/*      */   private static boolean isFinite(float paramFloat) {
/*  764 */     return ((1.0F / -1.0F) < paramFloat) && (paramFloat < (1.0F / 1.0F));
/*      */   }
/*      */ 
/*      */   private static int findSubdivPoints(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt, float paramFloat)
/*      */   {
/*  888 */     float f1 = paramArrayOfFloat1[2] - paramArrayOfFloat1[0];
/*  889 */     float f2 = paramArrayOfFloat1[3] - paramArrayOfFloat1[1];
/*      */ 
/*  892 */     if ((f2 != 0.0F) && (f1 != 0.0F))
/*      */     {
/*  896 */       float f3 = (float)Math.sqrt(f1 * f1 + f2 * f2);
/*  897 */       float f4 = f1 / f3;
/*  898 */       float f5 = f2 / f3;
/*  899 */       float f6 = f4 * paramArrayOfFloat1[0] + f5 * paramArrayOfFloat1[1];
/*  900 */       float f7 = f4 * paramArrayOfFloat1[1] - f5 * paramArrayOfFloat1[0];
/*  901 */       float f8 = f4 * paramArrayOfFloat1[2] + f5 * paramArrayOfFloat1[3];
/*  902 */       float f9 = f4 * paramArrayOfFloat1[3] - f5 * paramArrayOfFloat1[2];
/*  903 */       float f10 = f4 * paramArrayOfFloat1[4] + f5 * paramArrayOfFloat1[5];
/*  904 */       float f11 = f4 * paramArrayOfFloat1[5] - f5 * paramArrayOfFloat1[4];
/*  905 */       switch (paramInt) {
/*      */       case 8:
/*  907 */         float f12 = f4 * paramArrayOfFloat1[6] + f5 * paramArrayOfFloat1[7];
/*  908 */         float f13 = f4 * paramArrayOfFloat1[7] - f5 * paramArrayOfFloat1[6];
/*  909 */         c.set(f6, f7, f8, f9, f10, f11, f12, f13);
/*  910 */         break;
/*      */       case 6:
/*  912 */         c.set(f6, f7, f8, f9, f10, f11);
/*      */       }
/*      */     }
/*      */     else {
/*  916 */       c.set(paramArrayOfFloat1, paramInt);
/*      */     }
/*      */ 
/*  919 */     int i = 0;
/*      */ 
/*  922 */     i += c.dxRoots(paramArrayOfFloat2, i);
/*  923 */     i += c.dyRoots(paramArrayOfFloat2, i);
/*      */ 
/*  925 */     if (paramInt == 8)
/*      */     {
/*  927 */       i += c.infPoints(paramArrayOfFloat2, i);
/*      */     }
/*      */ 
/*  932 */     i += c.rootsOfROCMinusW(paramArrayOfFloat2, i, paramFloat, 1.0E-04F);
/*      */ 
/*  934 */     i = Helpers.filterOutNotInAB(paramArrayOfFloat2, 0, i, 1.0E-04F, 0.9999F);
/*  935 */     Helpers.isort(paramArrayOfFloat2, 0, i);
/*  936 */     return i;
/*      */   }
/*      */ 
/*      */   public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*      */   {
/*  943 */     this.middle[0] = this.cx0; this.middle[1] = this.cy0;
/*  944 */     this.middle[2] = paramFloat1; this.middle[3] = paramFloat2;
/*  945 */     this.middle[4] = paramFloat3; this.middle[5] = paramFloat4;
/*  946 */     this.middle[6] = paramFloat5; this.middle[7] = paramFloat6;
/*      */ 
/*  952 */     float f1 = this.middle[6]; float f2 = this.middle[7];
/*  953 */     float f3 = this.middle[2] - this.middle[0];
/*  954 */     float f4 = this.middle[3] - this.middle[1];
/*  955 */     float f5 = this.middle[6] - this.middle[4];
/*  956 */     float f6 = this.middle[7] - this.middle[5];
/*      */ 
/*  958 */     int i = (f3 == 0.0F) && (f4 == 0.0F) ? 1 : 0;
/*  959 */     int j = (f5 == 0.0F) && (f6 == 0.0F) ? 1 : 0;
/*  960 */     if (i != 0) {
/*  961 */       f3 = this.middle[4] - this.middle[0];
/*  962 */       f4 = this.middle[5] - this.middle[1];
/*  963 */       if ((f3 == 0.0F) && (f4 == 0.0F)) {
/*  964 */         f3 = this.middle[6] - this.middle[0];
/*  965 */         f4 = this.middle[7] - this.middle[1];
/*      */       }
/*      */     }
/*  968 */     if (j != 0) {
/*  969 */       f5 = this.middle[6] - this.middle[2];
/*  970 */       f6 = this.middle[7] - this.middle[3];
/*  971 */       if ((f5 == 0.0F) && (f6 == 0.0F)) {
/*  972 */         f5 = this.middle[6] - this.middle[0];
/*  973 */         f6 = this.middle[7] - this.middle[1];
/*      */       }
/*      */     }
/*  976 */     if ((f3 == 0.0F) && (f4 == 0.0F))
/*      */     {
/*  978 */       lineTo(this.middle[0], this.middle[1]);
/*  979 */       return;
/*      */     }
/*      */ 
/*  984 */     if ((Math.abs(f3) < 0.1F) && (Math.abs(f4) < 0.1F)) {
/*  985 */       f7 = (float)Math.sqrt(f3 * f3 + f4 * f4);
/*  986 */       f3 /= f7;
/*  987 */       f4 /= f7;
/*      */     }
/*  989 */     if ((Math.abs(f5) < 0.1F) && (Math.abs(f6) < 0.1F)) {
/*  990 */       f7 = (float)Math.sqrt(f5 * f5 + f6 * f6);
/*  991 */       f5 /= f7;
/*  992 */       f6 /= f7;
/*      */     }
/*      */ 
/*  995 */     computeOffset(f3, f4, this.lineWidth2, this.offset[0]);
/*  996 */     float f7 = this.offset[0][0];
/*  997 */     float f8 = this.offset[0][1];
/*  998 */     drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, f3, f4, this.cmx, this.cmy, f7, f8);
/*      */ 
/* 1000 */     int k = findSubdivPoints(this.middle, this.subdivTs, 8, this.lineWidth2);
/*      */ 
/* 1002 */     int m = 0;
/* 1003 */     Iterator localIterator = Curve.breakPtsAtTs(this.middle, 8, this.subdivTs, k);
/* 1004 */     while (localIterator.hasNext()) {
/* 1005 */       int n = ((Integer)localIterator.next()).intValue();
/*      */ 
/* 1007 */       m = computeOffsetCubic(this.middle, n, this.lp, this.rp);
/* 1008 */       emitLineTo(this.lp[0], this.lp[1]);
/* 1009 */       switch (m) {
/*      */       case 8:
/* 1011 */         emitCurveTo(this.lp[0], this.lp[1], this.lp[2], this.lp[3], this.lp[4], this.lp[5], this.lp[6], this.lp[7], false);
/* 1012 */         emitCurveTo(this.rp[0], this.rp[1], this.rp[2], this.rp[3], this.rp[4], this.rp[5], this.rp[6], this.rp[7], true);
/* 1013 */         break;
/*      */       case 4:
/* 1015 */         emitLineTo(this.lp[2], this.lp[3]);
/* 1016 */         emitLineTo(this.rp[0], this.rp[1], true);
/*      */       }
/*      */ 
/* 1019 */       emitLineTo(this.rp[(m - 2)], this.rp[(m - 1)], true);
/*      */     }
/*      */ 
/* 1022 */     this.cmx = ((this.lp[(m - 2)] - this.rp[(m - 2)]) / 2.0F);
/* 1023 */     this.cmy = ((this.lp[(m - 1)] - this.rp[(m - 1)]) / 2.0F);
/* 1024 */     this.cdx = f5;
/* 1025 */     this.cdy = f6;
/* 1026 */     this.cx0 = f1;
/* 1027 */     this.cy0 = f2;
/* 1028 */     this.prev = 1;
/*      */   }
/*      */ 
/*      */   public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
/* 1032 */     this.middle[0] = this.cx0; this.middle[1] = this.cy0;
/* 1033 */     this.middle[2] = paramFloat1; this.middle[3] = paramFloat2;
/* 1034 */     this.middle[4] = paramFloat3; this.middle[5] = paramFloat4;
/*      */ 
/* 1040 */     float f1 = this.middle[4]; float f2 = this.middle[5];
/* 1041 */     float f3 = this.middle[2] - this.middle[0];
/* 1042 */     float f4 = this.middle[3] - this.middle[1];
/* 1043 */     float f5 = this.middle[4] - this.middle[2];
/* 1044 */     float f6 = this.middle[5] - this.middle[3];
/* 1045 */     if (((f3 == 0.0F) && (f4 == 0.0F)) || ((f5 == 0.0F) && (f6 == 0.0F))) {
/* 1046 */       f3 = f5 = this.middle[4] - this.middle[0];
/* 1047 */       f4 = f6 = this.middle[5] - this.middle[1];
/*      */     }
/* 1049 */     if ((f3 == 0.0F) && (f4 == 0.0F))
/*      */     {
/* 1051 */       lineTo(this.middle[0], this.middle[1]);
/* 1052 */       return;
/*      */     }
/*      */ 
/* 1056 */     if ((Math.abs(f3) < 0.1F) && (Math.abs(f4) < 0.1F)) {
/* 1057 */       f7 = (float)Math.sqrt(f3 * f3 + f4 * f4);
/* 1058 */       f3 /= f7;
/* 1059 */       f4 /= f7;
/*      */     }
/* 1061 */     if ((Math.abs(f5) < 0.1F) && (Math.abs(f6) < 0.1F)) {
/* 1062 */       f7 = (float)Math.sqrt(f5 * f5 + f6 * f6);
/* 1063 */       f5 /= f7;
/* 1064 */       f6 /= f7;
/*      */     }
/*      */ 
/* 1067 */     computeOffset(f3, f4, this.lineWidth2, this.offset[0]);
/* 1068 */     float f7 = this.offset[0][0];
/* 1069 */     float f8 = this.offset[0][1];
/* 1070 */     drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, f3, f4, this.cmx, this.cmy, f7, f8);
/*      */ 
/* 1072 */     int i = findSubdivPoints(this.middle, this.subdivTs, 6, this.lineWidth2);
/*      */ 
/* 1074 */     int j = 0;
/* 1075 */     Iterator localIterator = Curve.breakPtsAtTs(this.middle, 6, this.subdivTs, i);
/* 1076 */     while (localIterator.hasNext()) {
/* 1077 */       int k = ((Integer)localIterator.next()).intValue();
/*      */ 
/* 1079 */       j = computeOffsetQuad(this.middle, k, this.lp, this.rp);
/* 1080 */       emitLineTo(this.lp[0], this.lp[1]);
/* 1081 */       switch (j) {
/*      */       case 6:
/* 1083 */         emitQuadTo(this.lp[0], this.lp[1], this.lp[2], this.lp[3], this.lp[4], this.lp[5], false);
/* 1084 */         emitQuadTo(this.rp[0], this.rp[1], this.rp[2], this.rp[3], this.rp[4], this.rp[5], true);
/* 1085 */         break;
/*      */       case 4:
/* 1087 */         emitLineTo(this.lp[2], this.lp[3]);
/* 1088 */         emitLineTo(this.rp[0], this.rp[1], true);
/*      */       }
/*      */ 
/* 1091 */       emitLineTo(this.rp[(j - 2)], this.rp[(j - 1)], true);
/*      */     }
/*      */ 
/* 1094 */     this.cmx = ((this.lp[(j - 2)] - this.rp[(j - 2)]) / 2.0F);
/* 1095 */     this.cmy = ((this.lp[(j - 1)] - this.rp[(j - 1)]) / 2.0F);
/* 1096 */     this.cdx = f5;
/* 1097 */     this.cdy = f6;
/* 1098 */     this.cx0 = f1;
/* 1099 */     this.cy0 = f2;
/* 1100 */     this.prev = 1;
/*      */   }
/*      */ 
/*      */   public long getNativeConsumer() {
/* 1104 */     throw new InternalError("Stroker doesn't use a native consumer");
/*      */   }
/*      */   private static final class PolyStack {
/*      */     float[] curves;
/*      */     int end;
/*      */     int[] curveTypes;
/*      */     int numCurves;
/*      */     private static final int INIT_SIZE = 50;
/*      */ 
/*      */     PolyStack() {
/* 1118 */       this.curves = new float[400];
/* 1119 */       this.curveTypes = new int[50];
/* 1120 */       this.end = 0;
/* 1121 */       this.numCurves = 0;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 1125 */       return this.numCurves == 0;
/*      */     }
/*      */ 
/*      */     private void ensureSpace(int paramInt)
/*      */     {
/*      */       int i;
/* 1129 */       if (this.end + paramInt >= this.curves.length) {
/* 1130 */         i = (this.end + paramInt) * 2;
/* 1131 */         this.curves = Arrays.copyOf(this.curves, i);
/*      */       }
/* 1133 */       if (this.numCurves >= this.curveTypes.length) {
/* 1134 */         i = this.numCurves * 2;
/* 1135 */         this.curveTypes = Arrays.copyOf(this.curveTypes, i);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void pushCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*      */     {
/* 1143 */       ensureSpace(6);
/* 1144 */       this.curveTypes[(this.numCurves++)] = 8;
/*      */ 
/* 1148 */       this.curves[(this.end++)] = paramFloat5; this.curves[(this.end++)] = paramFloat6;
/* 1149 */       this.curves[(this.end++)] = paramFloat3; this.curves[(this.end++)] = paramFloat4;
/* 1150 */       this.curves[(this.end++)] = paramFloat1; this.curves[(this.end++)] = paramFloat2;
/*      */     }
/*      */ 
/*      */     public void pushQuad(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*      */     {
/* 1156 */       ensureSpace(4);
/* 1157 */       this.curveTypes[(this.numCurves++)] = 6;
/*      */ 
/* 1159 */       this.curves[(this.end++)] = paramFloat3; this.curves[(this.end++)] = paramFloat4;
/* 1160 */       this.curves[(this.end++)] = paramFloat1; this.curves[(this.end++)] = paramFloat2;
/*      */     }
/*      */ 
/*      */     public void pushLine(float paramFloat1, float paramFloat2) {
/* 1164 */       ensureSpace(2);
/* 1165 */       this.curveTypes[(this.numCurves++)] = 4;
/*      */ 
/* 1167 */       this.curves[(this.end++)] = paramFloat1; this.curves[(this.end++)] = paramFloat2;
/*      */     }
/*      */ 
/*      */     public int pop(float[] paramArrayOfFloat)
/*      */     {
/* 1172 */       int i = this.curveTypes[(this.numCurves - 1)];
/* 1173 */       this.numCurves -= 1;
/* 1174 */       this.end -= i - 2;
/* 1175 */       System.arraycopy(this.curves, this.end, paramArrayOfFloat, 0, i - 2);
/* 1176 */       return i;
/*      */     }
/*      */ 
/*      */     public void pop(PathConsumer2D paramPathConsumer2D) {
/* 1180 */       this.numCurves -= 1;
/* 1181 */       int i = this.curveTypes[this.numCurves];
/* 1182 */       this.end -= i - 2;
/* 1183 */       switch (i) {
/*      */       case 8:
/* 1185 */         paramPathConsumer2D.curveTo(this.curves[(this.end + 0)], this.curves[(this.end + 1)], this.curves[(this.end + 2)], this.curves[(this.end + 3)], this.curves[(this.end + 4)], this.curves[(this.end + 5)]);
/*      */ 
/* 1188 */         break;
/*      */       case 6:
/* 1190 */         paramPathConsumer2D.quadTo(this.curves[(this.end + 0)], this.curves[(this.end + 1)], this.curves[(this.end + 2)], this.curves[(this.end + 3)]);
/*      */ 
/* 1192 */         break;
/*      */       case 4:
/* 1194 */         paramPathConsumer2D.lineTo(this.curves[this.end], this.curves[(this.end + 1)]);
/*      */       case 5:
/*      */       case 7:
/*      */       }
/*      */     }
/*      */ 
/* 1200 */     public String toString() { String str = "";
/* 1201 */       int i = this.numCurves;
/* 1202 */       int j = this.end;
/* 1203 */       while (i > 0) {
/* 1204 */         i--;
/* 1205 */         int k = this.curveTypes[this.numCurves];
/* 1206 */         j -= k - 2;
/* 1207 */         switch (k) {
/*      */         case 8:
/* 1209 */           str = str + "cubic: ";
/* 1210 */           break;
/*      */         case 6:
/* 1212 */           str = str + "quad: ";
/* 1213 */           break;
/*      */         case 4:
/* 1215 */           str = str + "line: ";
/*      */         case 5:
/*      */         case 7:
/* 1218 */         }str = str + Arrays.toString(Arrays.copyOfRange(this.curves, j, j + k - 2)) + "\n";
/*      */       }
/* 1220 */       return str;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pisces.Stroker
 * JD-Core Version:    0.6.2
 */