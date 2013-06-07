/*      */ package sun.java2d.pipe;
/*      */ 
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.RectangularShape;
/*      */ 
/*      */ public class Region
/*      */ {
/*      */   static final int INIT_SIZE = 50;
/*      */   static final int GROW_SIZE = 50;
/*   83 */   public static final Region EMPTY_REGION = new ImmutableRegion(0, 0, 0, 0);
/*   84 */   public static final Region WHOLE_REGION = new ImmutableRegion(-2147483648, -2147483648, 2147483647, 2147483647);
/*      */   int lox;
/*      */   int loy;
/*      */   int hix;
/*      */   int hiy;
/*      */   int endIndex;
/*      */   int[] bands;
/*      */   static final int INCLUDE_A = 1;
/*      */   static final int INCLUDE_B = 2;
/*      */   static final int INCLUDE_COMMON = 4;
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   public static int dimAdd(int paramInt1, int paramInt2)
/*      */   {
/*  112 */     if (paramInt2 <= 0) return paramInt1;
/*  113 */     if (paramInt2 += paramInt1 < paramInt1) return 2147483647;
/*  114 */     return paramInt2;
/*      */   }
/*      */ 
/*      */   public static int clipAdd(int paramInt1, int paramInt2)
/*      */   {
/*  127 */     int i = paramInt1 + paramInt2;
/*  128 */     if ((i > paramInt1 ? 1 : 0) != (paramInt2 > 0 ? 1 : 0)) {
/*  129 */       i = paramInt2 < 0 ? -2147483648 : 2147483647;
/*      */     }
/*  131 */     return i;
/*      */   }
/*      */ 
/*      */   protected Region(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  135 */     this.lox = paramInt1;
/*  136 */     this.loy = paramInt2;
/*  137 */     this.hix = paramInt3;
/*  138 */     this.hiy = paramInt4;
/*      */   }
/*      */ 
/*      */   public static Region getInstance(Shape paramShape, AffineTransform paramAffineTransform)
/*      */   {
/*  154 */     return getInstance(WHOLE_REGION, false, paramShape, paramAffineTransform);
/*      */   }
/*      */ 
/*      */   public static Region getInstance(Region paramRegion, Shape paramShape, AffineTransform paramAffineTransform)
/*      */   {
/*  182 */     return getInstance(paramRegion, false, paramShape, paramAffineTransform);
/*      */   }
/*      */ 
/*      */   public static Region getInstance(Region paramRegion, boolean paramBoolean, Shape paramShape, AffineTransform paramAffineTransform)
/*      */   {
/*  216 */     if (((paramShape instanceof RectangularShape)) && (((RectangularShape)paramShape).isEmpty()))
/*      */     {
/*  219 */       return EMPTY_REGION;
/*      */     }
/*      */ 
/*  222 */     int[] arrayOfInt = new int[4];
/*  223 */     ShapeSpanIterator localShapeSpanIterator = new ShapeSpanIterator(paramBoolean);
/*      */     try {
/*  225 */       localShapeSpanIterator.setOutputArea(paramRegion);
/*  226 */       localShapeSpanIterator.appendPath(paramShape.getPathIterator(paramAffineTransform));
/*  227 */       localShapeSpanIterator.getPathBox(arrayOfInt);
/*  228 */       Region localRegion1 = getInstance(arrayOfInt);
/*  229 */       localRegion1.appendSpans(localShapeSpanIterator);
/*  230 */       return localRegion1;
/*      */     } finally {
/*  232 */       localShapeSpanIterator.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Region getInstance(Rectangle paramRectangle)
/*      */   {
/*  244 */     return getInstanceXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public static Region getInstanceXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  255 */     return getInstanceXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */ 
/*      */   public static Region getInstance(int[] paramArrayOfInt)
/*      */   {
/*  266 */     return new Region(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]);
/*      */   }
/*      */ 
/*      */   public static Region getInstanceXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  277 */     return new Region(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */ 
/*      */   public void setOutputArea(Rectangle paramRectangle)
/*      */   {
/*  288 */     setOutputAreaXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  301 */     setOutputAreaXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */ 
/*      */   public void setOutputArea(int[] paramArrayOfInt)
/*      */   {
/*  312 */     this.lox = paramArrayOfInt[0];
/*  313 */     this.loy = paramArrayOfInt[1];
/*  314 */     this.hix = paramArrayOfInt[2];
/*  315 */     this.hiy = paramArrayOfInt[3];
/*      */   }
/*      */ 
/*      */   public void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  327 */     this.lox = paramInt1;
/*  328 */     this.loy = paramInt2;
/*  329 */     this.hix = paramInt3;
/*  330 */     this.hiy = paramInt4;
/*      */   }
/*      */ 
/*      */   public void appendSpans(SpanIterator paramSpanIterator)
/*      */   {
/*  341 */     int[] arrayOfInt = new int[6];
/*      */ 
/*  343 */     while (paramSpanIterator.nextSpan(arrayOfInt)) {
/*  344 */       appendSpan(arrayOfInt);
/*      */     }
/*      */ 
/*  347 */     endRow(arrayOfInt);
/*  348 */     calcBBox();
/*      */   }
/*      */ 
/*      */   public Region getTranslatedRegion(int paramInt1, int paramInt2)
/*      */   {
/*  357 */     if ((paramInt1 | paramInt2) == 0) {
/*  358 */       return this;
/*      */     }
/*  360 */     int i = this.lox + paramInt1;
/*  361 */     int j = this.loy + paramInt2;
/*  362 */     int k = this.hix + paramInt1;
/*  363 */     int m = this.hiy + paramInt2;
/*  364 */     if ((i > this.lox ? 1 : 0) == (paramInt1 > 0 ? 1 : 0)) if ((j > this.loy ? 1 : 0) == (paramInt2 > 0 ? 1 : 0)) if ((k > this.hix ? 1 : 0) == (paramInt1 > 0 ? 1 : 0)) if ((m > this.hiy ? 1 : 0) == (paramInt2 > 0 ? 1 : 0))
/*      */           {
/*      */             break label149;
/*      */           }
/*      */ 
/*  369 */     return getSafeTranslatedRegion(paramInt1, paramInt2);
/*      */ 
/*  371 */     label149: Region localRegion = new Region(i, j, k, m);
/*  372 */     int[] arrayOfInt1 = this.bands;
/*  373 */     if (arrayOfInt1 != null) {
/*  374 */       int n = this.endIndex;
/*  375 */       localRegion.endIndex = n;
/*  376 */       int[] arrayOfInt2 = new int[n];
/*  377 */       localRegion.bands = arrayOfInt2;
/*  378 */       int i1 = 0;
/*      */ 
/*  380 */       while (i1 < n) {
/*  381 */         arrayOfInt1[i1] += paramInt2; i1++;
/*  382 */         arrayOfInt1[i1] += paramInt2; i1++;
/*      */         int tmp251_250 = arrayOfInt1[i1]; int i2 = tmp251_250; arrayOfInt2[i1] = tmp251_250; i1++;
/*      */         while (true) { i2--; if (i2 < 0) break;
/*  385 */           arrayOfInt1[i1] += paramInt1; i1++;
/*  386 */           arrayOfInt1[i1] += paramInt1; i1++;
/*      */         }
/*      */       }
/*      */     }
/*  390 */     return localRegion;
/*      */   }
/*      */ 
/*      */   private Region getSafeTranslatedRegion(int paramInt1, int paramInt2) {
/*  394 */     int i = clipAdd(this.lox, paramInt1);
/*  395 */     int j = clipAdd(this.loy, paramInt2);
/*  396 */     int k = clipAdd(this.hix, paramInt1);
/*  397 */     int m = clipAdd(this.hiy, paramInt2);
/*  398 */     Region localRegion = new Region(i, j, k, m);
/*  399 */     int[] arrayOfInt1 = this.bands;
/*  400 */     if (arrayOfInt1 != null) {
/*  401 */       int n = this.endIndex;
/*  402 */       int[] arrayOfInt2 = new int[n];
/*  403 */       int i1 = 0;
/*  404 */       int i2 = 0;
/*      */ 
/*  406 */       while (i1 < n)
/*      */       {
/*      */         int tmp110_107 = clipAdd(arrayOfInt1[(i1++)], paramInt2); int i4 = tmp110_107; arrayOfInt2[(i2++)] = tmp110_107;
/*      */         int tmp133_130 = clipAdd(arrayOfInt1[(i1++)], paramInt2); int i5 = tmp133_130; arrayOfInt2[(i2++)] = tmp133_130;
/*      */         int tmp152_151 = arrayOfInt1[(i1++)]; int i3 = tmp152_151; arrayOfInt2[(i2++)] = tmp152_151;
/*  411 */         int i6 = i2;
/*  412 */         if (i4 < i5) while (true) {
/*  413 */             i3--; if (i3 < 0) break;
/*  414 */             int i7 = clipAdd(arrayOfInt1[(i1++)], paramInt1);
/*  415 */             int i8 = clipAdd(arrayOfInt1[(i1++)], paramInt1);
/*  416 */             if (i7 < i8) {
/*  417 */               arrayOfInt2[(i2++)] = i7;
/*  418 */               arrayOfInt2[(i2++)] = i8;
/*      */             }
/*      */           }
/*      */ 
/*  422 */         i1 += i3 * 2;
/*      */ 
/*  425 */         if (i2 > i6)
/*  426 */           arrayOfInt2[(i6 - 1)] = ((i2 - i6) / 2);
/*      */         else {
/*  428 */           i2 = i6 - 3;
/*      */         }
/*      */       }
/*  431 */       if (i2 <= 5) {
/*  432 */         if (i2 < 5)
/*      */         {
/*  434 */           localRegion.lox = (localRegion.loy = localRegion.hix = localRegion.hiy = 0);
/*      */         }
/*      */         else {
/*  437 */           localRegion.loy = arrayOfInt2[0];
/*  438 */           localRegion.hiy = arrayOfInt2[1];
/*  439 */           localRegion.lox = arrayOfInt2[3];
/*  440 */           localRegion.hix = arrayOfInt2[4];
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  447 */         localRegion.endIndex = i2;
/*  448 */         localRegion.bands = arrayOfInt2;
/*      */       }
/*      */     }
/*  451 */     return localRegion;
/*      */   }
/*      */ 
/*      */   public Region getIntersection(Rectangle paramRectangle)
/*      */   {
/*  460 */     return getIntersectionXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public Region getIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  469 */     return getIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */ 
/*      */   public Region getIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  478 */     if (isInsideXYXY(paramInt1, paramInt2, paramInt3, paramInt4)) {
/*  479 */       return this;
/*      */     }
/*  481 */     Region localRegion = new Region(paramInt1 < this.lox ? this.lox : paramInt1, paramInt2 < this.loy ? this.loy : paramInt2, paramInt3 > this.hix ? this.hix : paramInt3, paramInt4 > this.hiy ? this.hiy : paramInt4);
/*      */ 
/*  485 */     if (this.bands != null) {
/*  486 */       localRegion.appendSpans(getSpanIterator());
/*      */     }
/*  488 */     return localRegion;
/*      */   }
/*      */ 
/*      */   public Region getIntersection(Region paramRegion)
/*      */   {
/*  504 */     if (isInsideQuickCheck(paramRegion)) {
/*  505 */       return this;
/*      */     }
/*  507 */     if (paramRegion.isInsideQuickCheck(this)) {
/*  508 */       return paramRegion;
/*      */     }
/*  510 */     Region localRegion = new Region(paramRegion.lox < this.lox ? this.lox : paramRegion.lox, paramRegion.loy < this.loy ? this.loy : paramRegion.loy, paramRegion.hix > this.hix ? this.hix : paramRegion.hix, paramRegion.hiy > this.hiy ? this.hiy : paramRegion.hiy);
/*      */ 
/*  514 */     if (!localRegion.isEmpty()) {
/*  515 */       localRegion.filterSpans(this, paramRegion, 4);
/*      */     }
/*  517 */     return localRegion;
/*      */   }
/*      */ 
/*      */   public Region getUnion(Region paramRegion)
/*      */   {
/*  533 */     if ((paramRegion.isEmpty()) || (paramRegion.isInsideQuickCheck(this))) {
/*  534 */       return this;
/*      */     }
/*  536 */     if ((isEmpty()) || (isInsideQuickCheck(paramRegion))) {
/*  537 */       return paramRegion;
/*      */     }
/*  539 */     Region localRegion = new Region(paramRegion.lox > this.lox ? this.lox : paramRegion.lox, paramRegion.loy > this.loy ? this.loy : paramRegion.loy, paramRegion.hix < this.hix ? this.hix : paramRegion.hix, paramRegion.hiy < this.hiy ? this.hiy : paramRegion.hiy);
/*      */ 
/*  543 */     localRegion.filterSpans(this, paramRegion, 7);
/*  544 */     return localRegion;
/*      */   }
/*      */ 
/*      */   public Region getDifference(Region paramRegion)
/*      */   {
/*  560 */     if (!paramRegion.intersectsQuickCheck(this)) {
/*  561 */       return this;
/*      */     }
/*  563 */     if (isInsideQuickCheck(paramRegion)) {
/*  564 */       return EMPTY_REGION;
/*      */     }
/*  566 */     Region localRegion = new Region(this.lox, this.loy, this.hix, this.hiy);
/*  567 */     localRegion.filterSpans(this, paramRegion, 1);
/*  568 */     return localRegion;
/*      */   }
/*      */ 
/*      */   public Region getExclusiveOr(Region paramRegion)
/*      */   {
/*  584 */     if (paramRegion.isEmpty()) {
/*  585 */       return this;
/*      */     }
/*  587 */     if (isEmpty()) {
/*  588 */       return paramRegion;
/*      */     }
/*  590 */     Region localRegion = new Region(paramRegion.lox > this.lox ? this.lox : paramRegion.lox, paramRegion.loy > this.loy ? this.loy : paramRegion.loy, paramRegion.hix < this.hix ? this.hix : paramRegion.hix, paramRegion.hiy < this.hiy ? this.hiy : paramRegion.hiy);
/*      */ 
/*  594 */     localRegion.filterSpans(this, paramRegion, 3);
/*  595 */     return localRegion;
/*      */   }
/*      */ 
/*      */   private void filterSpans(Region paramRegion1, Region paramRegion2, int paramInt)
/*      */   {
/*  603 */     int[] arrayOfInt1 = paramRegion1.bands;
/*  604 */     int[] arrayOfInt2 = paramRegion2.bands;
/*  605 */     if (arrayOfInt1 == null) {
/*  606 */       arrayOfInt1 = new int[] { paramRegion1.loy, paramRegion1.hiy, 1, paramRegion1.lox, paramRegion1.hix };
/*      */     }
/*  608 */     if (arrayOfInt2 == null) {
/*  609 */       arrayOfInt2 = new int[] { paramRegion2.loy, paramRegion2.hiy, 1, paramRegion2.lox, paramRegion2.hix };
/*      */     }
/*  611 */     int[] arrayOfInt3 = new int[6];
/*  612 */     int i = 0;
/*  613 */     int j = arrayOfInt1[(i++)];
/*  614 */     int k = arrayOfInt1[(i++)];
/*  615 */     int m = arrayOfInt1[(i++)];
/*  616 */     m = i + 2 * m;
/*  617 */     int n = 0;
/*  618 */     int i1 = arrayOfInt2[(n++)];
/*  619 */     int i2 = arrayOfInt2[(n++)];
/*  620 */     int i3 = arrayOfInt2[(n++)];
/*  621 */     i3 = n + 2 * i3;
/*  622 */     int i4 = this.loy;
/*  623 */     while (i4 < this.hiy)
/*  624 */       if (i4 >= k) {
/*  625 */         if (m < paramRegion1.endIndex) {
/*  626 */           i = m;
/*  627 */           j = arrayOfInt1[(i++)];
/*  628 */           k = arrayOfInt1[(i++)];
/*  629 */           m = arrayOfInt1[(i++)];
/*  630 */           m = i + 2 * m;
/*      */         } else {
/*  632 */           if ((paramInt & 0x2) == 0) break;
/*  633 */           j = k = this.hiy;
/*      */         }
/*      */ 
/*      */       }
/*  637 */       else if (i4 >= i2) {
/*  638 */         if (i3 < paramRegion2.endIndex) {
/*  639 */           n = i3;
/*  640 */           i1 = arrayOfInt2[(n++)];
/*  641 */           i2 = arrayOfInt2[(n++)];
/*  642 */           i3 = arrayOfInt2[(n++)];
/*  643 */           i3 = n + 2 * i3;
/*      */         } else {
/*  645 */           if ((paramInt & 0x1) == 0) break;
/*  646 */           i1 = i2 = this.hiy;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*      */         int i5;
/*      */         int i6;
/*  651 */         if (i4 < i1) {
/*  652 */           if (i4 < j) {
/*  653 */             i4 = Math.min(j, i1);
/*      */           }
/*      */           else
/*      */           {
/*  657 */             i5 = Math.min(k, i1);
/*  658 */             if ((paramInt & 0x1) != 0) {
/*  659 */               arrayOfInt3[1] = i4;
/*  660 */               arrayOfInt3[3] = i5;
/*  661 */               i6 = i;
/*  662 */               while (i6 < m) {
/*  663 */                 arrayOfInt3[0] = arrayOfInt1[(i6++)];
/*  664 */                 arrayOfInt3[2] = arrayOfInt1[(i6++)];
/*  665 */                 appendSpan(arrayOfInt3);
/*      */               }
/*      */             }
/*      */           } } else { if (i4 < j)
/*      */           {
/*  670 */             i5 = Math.min(i2, j);
/*  671 */             if ((paramInt & 0x2) != 0) {
/*  672 */               arrayOfInt3[1] = i4;
/*  673 */               arrayOfInt3[3] = i5;
/*  674 */               i6 = n;
/*  675 */               while (i6 < i3) {
/*  676 */                 arrayOfInt3[0] = arrayOfInt2[(i6++)];
/*  677 */                 arrayOfInt3[2] = arrayOfInt2[(i6++)];
/*  678 */                 appendSpan(arrayOfInt3);
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/*  683 */             i5 = Math.min(k, i2);
/*  684 */             arrayOfInt3[1] = i4;
/*  685 */             arrayOfInt3[3] = i5;
/*  686 */             i6 = i;
/*  687 */             int i7 = n;
/*  688 */             int i8 = arrayOfInt1[(i6++)];
/*  689 */             int i9 = arrayOfInt1[(i6++)];
/*  690 */             int i10 = arrayOfInt2[(i7++)];
/*  691 */             int i11 = arrayOfInt2[(i7++)];
/*  692 */             int i12 = Math.min(i8, i10);
/*  693 */             if (i12 < this.lox) i12 = this.lox;
/*  694 */             while (i12 < this.hix)
/*  695 */               if (i12 >= i9) {
/*  696 */                 if (i6 < m) {
/*  697 */                   i8 = arrayOfInt1[(i6++)];
/*  698 */                   i9 = arrayOfInt1[(i6++)];
/*      */                 } else {
/*  700 */                   if ((paramInt & 0x2) == 0) break;
/*  701 */                   i8 = i9 = this.hix;
/*      */                 }
/*      */ 
/*      */               }
/*  705 */               else if (i12 >= i11) {
/*  706 */                 if (i7 < i3) {
/*  707 */                   i10 = arrayOfInt2[(i7++)];
/*  708 */                   i11 = arrayOfInt2[(i7++)];
/*      */                 } else {
/*  710 */                   if ((paramInt & 0x1) == 0) break;
/*  711 */                   i10 = i11 = this.hix;
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*      */                 int i13;
/*      */                 int i14;
/*  717 */                 if (i12 < i10) {
/*  718 */                   if (i12 < i8) {
/*  719 */                     i13 = Math.min(i8, i10);
/*  720 */                     i14 = 0;
/*      */                   } else {
/*  722 */                     i13 = Math.min(i9, i10);
/*  723 */                     i14 = (paramInt & 0x1) != 0 ? 1 : 0;
/*      */                   }
/*  725 */                 } else if (i12 < i8) {
/*  726 */                   i13 = Math.min(i8, i11);
/*  727 */                   i14 = (paramInt & 0x2) != 0 ? 1 : 0;
/*      */                 } else {
/*  729 */                   i13 = Math.min(i9, i11);
/*  730 */                   i14 = (paramInt & 0x4) != 0 ? 1 : 0;
/*      */                 }
/*  732 */                 if (i14 != 0) {
/*  733 */                   arrayOfInt3[0] = i12;
/*  734 */                   arrayOfInt3[2] = i13;
/*  735 */                   appendSpan(arrayOfInt3);
/*      */                 }
/*  737 */                 i12 = i13;
/*      */               }
/*      */           }
/*  740 */           i4 = i5; }
/*      */       }
/*  742 */     endRow(arrayOfInt3);
/*  743 */     calcBBox();
/*      */   }
/*      */ 
/*      */   public Region getBoundsIntersection(Rectangle paramRectangle)
/*      */   {
/*  755 */     return getBoundsIntersectionXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */ 
/*      */   public Region getBoundsIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  767 */     return getBoundsIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */ 
/*      */   public Region getBoundsIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  781 */     if ((this.bands == null) && (this.lox >= paramInt1) && (this.loy >= paramInt2) && (this.hix <= paramInt3) && (this.hiy <= paramInt4))
/*      */     {
/*  785 */       return this;
/*      */     }
/*  787 */     return new Region(paramInt1 < this.lox ? this.lox : paramInt1, paramInt2 < this.loy ? this.loy : paramInt2, paramInt3 > this.hix ? this.hix : paramInt3, paramInt4 > this.hiy ? this.hiy : paramInt4);
/*      */   }
/*      */ 
/*      */   public Region getBoundsIntersection(Region paramRegion)
/*      */   {
/*  802 */     if (encompasses(paramRegion)) {
/*  803 */       return paramRegion;
/*      */     }
/*  805 */     if (paramRegion.encompasses(this)) {
/*  806 */       return this;
/*      */     }
/*  808 */     return new Region(paramRegion.lox < this.lox ? this.lox : paramRegion.lox, paramRegion.loy < this.loy ? this.loy : paramRegion.loy, paramRegion.hix > this.hix ? this.hix : paramRegion.hix, paramRegion.hiy > this.hiy ? this.hiy : paramRegion.hiy);
/*      */   }
/*      */ 
/*      */   private void appendSpan(int[] paramArrayOfInt)
/*      */   {
/*  824 */     int i;
/*  824 */     if ((i = paramArrayOfInt[0]) < this.lox) i = this.lox;
/*  825 */     int j;
/*  825 */     if ((j = paramArrayOfInt[1]) < this.loy) j = this.loy;
/*  826 */     int k;
/*  826 */     if ((k = paramArrayOfInt[2]) > this.hix) k = this.hix;
/*  827 */     int m;
/*  827 */     if ((m = paramArrayOfInt[3]) > this.hiy) m = this.hiy;
/*  828 */     if ((k <= i) || (m <= j)) {
/*  829 */       return;
/*      */     }
/*      */ 
/*  832 */     int n = paramArrayOfInt[4];
/*  833 */     if ((this.endIndex == 0) || (j >= this.bands[(n + 1)])) {
/*  834 */       if (this.bands == null) {
/*  835 */         this.bands = new int[50];
/*      */       } else {
/*  837 */         needSpace(5);
/*  838 */         endRow(paramArrayOfInt);
/*  839 */         n = paramArrayOfInt[4];
/*      */       }
/*  841 */       this.bands[(this.endIndex++)] = j;
/*  842 */       this.bands[(this.endIndex++)] = m;
/*  843 */       this.bands[(this.endIndex++)] = 0;
/*  844 */     } else if ((j == this.bands[n]) && (m == this.bands[(n + 1)]) && (i >= this.bands[(this.endIndex - 1)]))
/*      */     {
/*  847 */       if (i == this.bands[(this.endIndex - 1)]) {
/*  848 */         this.bands[(this.endIndex - 1)] = k;
/*  849 */         return;
/*      */       }
/*  851 */       needSpace(2);
/*      */     } else {
/*  853 */       throw new InternalError("bad span");
/*      */     }
/*  855 */     this.bands[(this.endIndex++)] = i;
/*  856 */     this.bands[(this.endIndex++)] = k;
/*  857 */     this.bands[(n + 2)] += 1;
/*      */   }
/*      */ 
/*      */   private void needSpace(int paramInt) {
/*  861 */     if (this.endIndex + paramInt >= this.bands.length) {
/*  862 */       int[] arrayOfInt = new int[this.bands.length + 50];
/*  863 */       System.arraycopy(this.bands, 0, arrayOfInt, 0, this.endIndex);
/*  864 */       this.bands = arrayOfInt;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void endRow(int[] paramArrayOfInt) {
/*  869 */     int i = paramArrayOfInt[4];
/*  870 */     int j = paramArrayOfInt[5];
/*  871 */     if (i > j) {
/*  872 */       int[] arrayOfInt = this.bands;
/*  873 */       if ((arrayOfInt[(j + 1)] == arrayOfInt[i]) && (arrayOfInt[(j + 2)] == arrayOfInt[(i + 2)]))
/*      */       {
/*  876 */         int k = arrayOfInt[(i + 2)] * 2;
/*  877 */         i += 3;
/*  878 */         j += 3;
/*  879 */         while ((k > 0) && 
/*  880 */           (arrayOfInt[(i++)] == arrayOfInt[(j++)]))
/*      */         {
/*  883 */           k--;
/*      */         }
/*  885 */         if (k == 0)
/*      */         {
/*  887 */           arrayOfInt[(paramArrayOfInt[5] + 1)] = arrayOfInt[(j + 1)];
/*  888 */           this.endIndex = j;
/*  889 */           return;
/*      */         }
/*      */       }
/*      */     }
/*  893 */     paramArrayOfInt[5] = paramArrayOfInt[4];
/*  894 */     paramArrayOfInt[4] = this.endIndex;
/*      */   }
/*      */ 
/*      */   private void calcBBox() {
/*  898 */     int[] arrayOfInt = this.bands;
/*  899 */     if (this.endIndex <= 5) {
/*  900 */       if (this.endIndex == 0) {
/*  901 */         this.lox = (this.loy = this.hix = this.hiy = 0);
/*      */       } else {
/*  903 */         this.loy = arrayOfInt[0];
/*  904 */         this.hiy = arrayOfInt[1];
/*  905 */         this.lox = arrayOfInt[3];
/*  906 */         this.hix = arrayOfInt[4];
/*  907 */         this.endIndex = 0;
/*      */       }
/*  909 */       this.bands = null;
/*  910 */       return;
/*      */     }
/*  912 */     int i = this.hix;
/*  913 */     int j = this.lox;
/*  914 */     int k = 0;
/*      */ 
/*  916 */     int m = 0;
/*  917 */     while (m < this.endIndex) {
/*  918 */       k = m;
/*  919 */       int n = arrayOfInt[(m + 2)];
/*  920 */       m += 3;
/*  921 */       if (i > arrayOfInt[m]) {
/*  922 */         i = arrayOfInt[m];
/*      */       }
/*  924 */       m += n * 2;
/*  925 */       if (j < arrayOfInt[(m - 1)]) {
/*  926 */         j = arrayOfInt[(m - 1)];
/*      */       }
/*      */     }
/*      */ 
/*  930 */     this.lox = i;
/*  931 */     this.loy = arrayOfInt[0];
/*  932 */     this.hix = j;
/*  933 */     this.hiy = arrayOfInt[(k + 1)];
/*      */   }
/*      */ 
/*      */   public final int getLoX()
/*      */   {
/*  940 */     return this.lox;
/*      */   }
/*      */ 
/*      */   public final int getLoY()
/*      */   {
/*  947 */     return this.loy;
/*      */   }
/*      */ 
/*      */   public final int getHiX()
/*      */   {
/*  954 */     return this.hix;
/*      */   }
/*      */ 
/*      */   public final int getHiY()
/*      */   {
/*  961 */     return this.hiy;
/*      */   }
/*      */ 
/*      */   public final int getWidth()
/*      */   {
/*  968 */     if (this.hix < this.lox) return 0;
/*      */     int i;
/*  970 */     if ((i = this.hix - this.lox) < 0) {
/*  971 */       i = 2147483647;
/*      */     }
/*  973 */     return i;
/*      */   }
/*      */ 
/*      */   public final int getHeight()
/*      */   {
/*  980 */     if (this.hiy < this.loy) return 0;
/*      */     int i;
/*  982 */     if ((i = this.hiy - this.loy) < 0) {
/*  983 */       i = 2147483647;
/*      */     }
/*  985 */     return i;
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  992 */     return (this.hix <= this.lox) || (this.hiy <= this.loy);
/*      */   }
/*      */ 
/*      */   public boolean isRectangular()
/*      */   {
/* 1000 */     return this.bands == null;
/*      */   }
/*      */ 
/*      */   public boolean contains(int paramInt1, int paramInt2)
/*      */   {
/* 1007 */     if ((paramInt1 < this.lox) || (paramInt1 >= this.hix) || (paramInt2 < this.loy) || (paramInt2 >= this.hiy)) return false;
/* 1008 */     if (this.bands == null) return true;
/* 1009 */     int i = 0;
/* 1010 */     while (i < this.endIndex) {
/* 1011 */       if (paramInt2 < this.bands[(i++)])
/* 1012 */         return false;
/*      */       int j;
/* 1014 */       if (paramInt2 >= this.bands[(i++)]) {
/* 1015 */         j = this.bands[(i++)];
/* 1016 */         i += j * 2;
/*      */       } else {
/* 1018 */         j = this.bands[(i++)];
/* 1019 */         j = i + j * 2;
/* 1020 */         while (i < j) {
/* 1021 */           if (paramInt1 < this.bands[(i++)]) return false;
/* 1022 */           if (paramInt1 < this.bands[(i++)]) return true;
/*      */         }
/* 1024 */         return false;
/*      */       }
/*      */     }
/* 1027 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isInsideXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1036 */     return isInsideXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */ 
/*      */   public boolean isInsideXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1044 */     return (this.lox >= paramInt1) && (this.loy >= paramInt2) && (this.hix <= paramInt3) && (this.hiy <= paramInt4);
/*      */   }
/*      */ 
/*      */   public boolean isInsideQuickCheck(Region paramRegion)
/*      */   {
/* 1057 */     return (paramRegion.bands == null) && (paramRegion.lox <= this.lox) && (paramRegion.loy <= this.loy) && (paramRegion.hix >= this.hix) && (paramRegion.hiy >= this.hiy);
/*      */   }
/*      */ 
/*      */   public boolean intersectsQuickCheckXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1073 */     return (paramInt3 > this.lox) && (paramInt1 < this.hix) && (paramInt4 > this.loy) && (paramInt2 < this.hiy);
/*      */   }
/*      */ 
/*      */   public boolean intersectsQuickCheck(Region paramRegion)
/*      */   {
/* 1086 */     return (paramRegion.hix > this.lox) && (paramRegion.lox < this.hix) && (paramRegion.hiy > this.loy) && (paramRegion.loy < this.hiy);
/*      */   }
/*      */ 
/*      */   public boolean encompasses(Region paramRegion)
/*      */   {
/* 1098 */     return (this.bands == null) && (this.lox <= paramRegion.lox) && (this.loy <= paramRegion.loy) && (this.hix >= paramRegion.hix) && (this.hiy >= paramRegion.hiy);
/*      */   }
/*      */ 
/*      */   public boolean encompassesXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1111 */     return encompassesXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */ 
/*      */   public boolean encompassesXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1122 */     return (this.bands == null) && (this.lox <= paramInt1) && (this.loy <= paramInt2) && (this.hix >= paramInt3) && (this.hiy >= paramInt4);
/*      */   }
/*      */ 
/*      */   public void getBounds(int[] paramArrayOfInt)
/*      */   {
/* 1131 */     paramArrayOfInt[0] = this.lox;
/* 1132 */     paramArrayOfInt[1] = this.loy;
/* 1133 */     paramArrayOfInt[2] = this.hix;
/* 1134 */     paramArrayOfInt[3] = this.hiy;
/*      */   }
/*      */ 
/*      */   public void clipBoxToBounds(int[] paramArrayOfInt)
/*      */   {
/* 1141 */     if (paramArrayOfInt[0] < this.lox) paramArrayOfInt[0] = this.lox;
/* 1142 */     if (paramArrayOfInt[1] < this.loy) paramArrayOfInt[1] = this.loy;
/* 1143 */     if (paramArrayOfInt[2] > this.hix) paramArrayOfInt[2] = this.hix;
/* 1144 */     if (paramArrayOfInt[3] > this.hiy) paramArrayOfInt[3] = this.hiy;
/*      */   }
/*      */ 
/*      */   public RegionIterator getIterator()
/*      */   {
/* 1151 */     return new RegionIterator(this);
/*      */   }
/*      */ 
/*      */   public SpanIterator getSpanIterator()
/*      */   {
/* 1158 */     return new RegionSpanIterator(this);
/*      */   }
/*      */ 
/*      */   public SpanIterator getSpanIterator(int[] paramArrayOfInt)
/*      */   {
/* 1166 */     SpanIterator localSpanIterator = getSpanIterator();
/* 1167 */     localSpanIterator.intersectClipBox(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]);
/* 1168 */     return localSpanIterator;
/*      */   }
/*      */ 
/*      */   public SpanIterator filter(SpanIterator paramSpanIterator)
/*      */   {
/* 1176 */     if (this.bands == null)
/* 1177 */       paramSpanIterator.intersectClipBox(this.lox, this.loy, this.hix, this.hiy);
/*      */     else {
/* 1179 */       paramSpanIterator = new RegionClipSpanIterator(this, paramSpanIterator);
/*      */     }
/* 1181 */     return paramSpanIterator;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1185 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1186 */     localStringBuffer.append("Region[[");
/* 1187 */     localStringBuffer.append(this.lox);
/* 1188 */     localStringBuffer.append(", ");
/* 1189 */     localStringBuffer.append(this.loy);
/* 1190 */     localStringBuffer.append(" => ");
/* 1191 */     localStringBuffer.append(this.hix);
/* 1192 */     localStringBuffer.append(", ");
/* 1193 */     localStringBuffer.append(this.hiy);
/* 1194 */     localStringBuffer.append("]");
/* 1195 */     if (this.bands != null) {
/* 1196 */       int i = 0;
/* 1197 */       while (i < this.endIndex) {
/* 1198 */         localStringBuffer.append("y{");
/* 1199 */         localStringBuffer.append(this.bands[(i++)]);
/* 1200 */         localStringBuffer.append(",");
/* 1201 */         localStringBuffer.append(this.bands[(i++)]);
/* 1202 */         localStringBuffer.append("}[");
/* 1203 */         int j = this.bands[(i++)];
/* 1204 */         j = i + j * 2;
/* 1205 */         while (i < j) {
/* 1206 */           localStringBuffer.append("x(");
/* 1207 */           localStringBuffer.append(this.bands[(i++)]);
/* 1208 */           localStringBuffer.append(", ");
/* 1209 */           localStringBuffer.append(this.bands[(i++)]);
/* 1210 */           localStringBuffer.append(")");
/*      */         }
/* 1212 */         localStringBuffer.append("]");
/*      */       }
/*      */     }
/* 1215 */     localStringBuffer.append("]");
/* 1216 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   public int hashCode() {
/* 1220 */     return isEmpty() ? 0 : this.lox * 3 + this.loy * 5 + this.hix * 7 + this.hiy * 9;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject) {
/* 1224 */     if (!(paramObject instanceof Region)) {
/* 1225 */       return false;
/*      */     }
/* 1227 */     Region localRegion = (Region)paramObject;
/* 1228 */     if (isEmpty())
/* 1229 */       return localRegion.isEmpty();
/* 1230 */     if (localRegion.isEmpty()) {
/* 1231 */       return false;
/*      */     }
/* 1233 */     if ((localRegion.lox != this.lox) || (localRegion.loy != this.loy) || (localRegion.hix != this.hix) || (localRegion.hiy != this.hiy))
/*      */     {
/* 1236 */       return false;
/*      */     }
/* 1238 */     if (this.bands == null)
/* 1239 */       return localRegion.bands == null;
/* 1240 */     if (localRegion.bands == null) {
/* 1241 */       return false;
/*      */     }
/* 1243 */     if (this.endIndex != localRegion.endIndex) {
/* 1244 */       return false;
/*      */     }
/* 1246 */     int[] arrayOfInt1 = this.bands;
/* 1247 */     int[] arrayOfInt2 = localRegion.bands;
/* 1248 */     for (int i = 0; i < this.endIndex; i++) {
/* 1249 */       if (arrayOfInt1[i] != arrayOfInt2[i]) {
/* 1250 */         return false;
/*      */       }
/*      */     }
/* 1253 */     return true;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  101 */     initIDs();
/*      */   }
/*      */ 
/*      */   private static final class ImmutableRegion extends Region
/*      */   {
/*      */     protected ImmutableRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/*   72 */       super(paramInt2, paramInt3, paramInt4);
/*      */     }
/*      */ 
/*      */     public void appendSpans(SpanIterator paramSpanIterator)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setOutputArea(Rectangle paramRectangle)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setOutputArea(int[] paramArrayOfInt)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pipe.Region
 * JD-Core Version:    0.6.2
 */