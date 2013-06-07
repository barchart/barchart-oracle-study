/*      */ package sun.misc;
/*      */ 
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ 
/*      */ public class FloatingDecimal
/*      */ {
/*      */   boolean isExceptional;
/*      */   boolean isNegative;
/*      */   int decExponent;
/*      */   char[] digits;
/*      */   int nDigits;
/*      */   int bigIntExp;
/*      */   int bigIntNBits;
/*   41 */   boolean mustSetRoundDir = false;
/*   42 */   boolean fromHex = false;
/*   43 */   int roundDir = 0;
/*      */   static final long signMask = -9223372036854775808L;
/*      */   static final long expMask = 9218868437227405312L;
/*      */   static final long fractMask = 4503599627370495L;
/*      */   static final int expShift = 52;
/*      */   static final int expBias = 1023;
/*      */   static final long fractHOB = 4503599627370496L;
/*      */   static final long expOne = 4607182418800017408L;
/*      */   static final int maxSmallBinExp = 62;
/*      */   static final int minSmallBinExp = -21;
/*      */   static final int maxDecimalDigits = 15;
/*      */   static final int maxDecimalExponent = 308;
/*      */   static final int minDecimalExponent = -324;
/*      */   static final int bigDecimalExponent = 324;
/*      */   static final long highbyte = -72057594037927936L;
/*      */   static final long highbit = -9223372036854775808L;
/*      */   static final long lowbytes = 72057594037927935L;
/*      */   static final int singleSignMask = -2147483648;
/*      */   static final int singleExpMask = 2139095040;
/*      */   static final int singleFractMask = 8388607;
/*      */   static final int singleExpShift = 23;
/*      */   static final int singleFractHOB = 8388608;
/*      */   static final int singleExpBias = 127;
/*      */   static final int singleMaxDecimalDigits = 7;
/*      */   static final int singleMaxDecimalExponent = 38;
/*      */   static final int singleMinDecimalExponent = -45;
/*      */   static final int intDecimalDigits = 9;
/*      */   private static FDBigInt[] b5p;
/*  982 */   private static ThreadLocal perThreadBuffer = new ThreadLocal() {
/*      */     protected synchronized Object initialValue() {
/*  984 */       return new char[26];
/*      */     }
/*  982 */   };
/*      */ 
/* 1756 */   private static final double[] small10pow = { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 10000000.0D, 100000000.0D, 1000000000.0D, 10000000000.0D, 100000000000.0D, 1000000000000.0D, 10000000000000.0D, 100000000000000.0D, 1000000000000000.0D, 10000000000000000.0D, 1.0E+17D, 1.0E+18D, 1.0E+19D, 1.0E+20D, 1.0E+21D, 1.0E+22D };
/*      */ 
/* 1765 */   private static final float[] singleSmall10pow = { 1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 10000000.0F, 1.0E+08F, 1.0E+09F, 1.0E+10F };
/*      */ 
/* 1771 */   private static final double[] big10pow = { 10000000000000000.0D, 1.E+32D, 1.0E+64D, 1.E+128D, 1.0E+256D };
/*      */ 
/* 1773 */   private static final double[] tiny10pow = { 1.0E-16D, 1.E-32D, 1.0E-64D, 1.E-128D, 1.0E-256D };
/*      */ 
/* 1776 */   private static final int maxSmallTen = small10pow.length - 1;
/* 1777 */   private static final int singleMaxSmallTen = singleSmall10pow.length - 1;
/*      */ 
/* 1779 */   private static final int[] small5pow = { 1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125 };
/*      */ 
/* 1797 */   private static final long[] long5pow = { 1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L };
/*      */ 
/* 1828 */   private static final int[] n5bits = { 0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61 };
/*      */ 
/* 1858 */   private static final char[] infinity = { 'I', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
/* 1859 */   private static final char[] notANumber = { 'N', 'a', 'N' };
/* 1860 */   private static final char[] zero = { '0', '0', '0', '0', '0', '0', '0', '0' };
/*      */ 
/* 1867 */   private static Pattern hexFloatPattern = null;
/*      */ 
/*      */   private FloatingDecimal(boolean paramBoolean1, int paramInt1, char[] paramArrayOfChar, int paramInt2, boolean paramBoolean2)
/*      */   {
/*   47 */     this.isNegative = paramBoolean1;
/*   48 */     this.isExceptional = paramBoolean2;
/*   49 */     this.decExponent = paramInt1;
/*   50 */     this.digits = paramArrayOfChar;
/*   51 */     this.nDigits = paramInt2;
/*      */   }
/*      */ 
/*      */   private static int countBits(long paramLong)
/*      */   {
/*  101 */     if (paramLong == 0L) return 0;
/*      */ 
/*  103 */     while ((paramLong & 0x0) == 0L) {
/*  104 */       paramLong <<= 8;
/*      */     }
/*  106 */     while (paramLong > 0L) {
/*  107 */       paramLong <<= 1;
/*      */     }
/*      */ 
/*  110 */     int i = 0;
/*  111 */     while ((paramLong & 0xFFFFFFFF) != 0L) {
/*  112 */       paramLong <<= 8;
/*  113 */       i += 8;
/*      */     }
/*  115 */     while (paramLong != 0L) {
/*  116 */       paramLong <<= 1;
/*  117 */       i++;
/*      */     }
/*  119 */     return i;
/*      */   }
/*      */ 
/*      */   private static synchronized FDBigInt big5pow(int paramInt)
/*      */   {
/*  129 */     assert (paramInt >= 0) : paramInt;
/*  130 */     if (b5p == null) {
/*  131 */       b5p = new FDBigInt[paramInt + 1];
/*  132 */     } else if (b5p.length <= paramInt) {
/*  133 */       FDBigInt[] arrayOfFDBigInt = new FDBigInt[paramInt + 1];
/*  134 */       System.arraycopy(b5p, 0, arrayOfFDBigInt, 0, b5p.length);
/*  135 */       b5p = arrayOfFDBigInt;
/*      */     }
/*  137 */     if (b5p[paramInt] != null)
/*  138 */       return b5p[paramInt];
/*  139 */     if (paramInt < small5pow.length)
/*  140 */       return b5p[paramInt] =  = new FDBigInt(small5pow[paramInt]);
/*  141 */     if (paramInt < long5pow.length) {
/*  142 */       return b5p[paramInt] =  = new FDBigInt(long5pow[paramInt]);
/*      */     }
/*      */ 
/*  151 */     int i = paramInt >> 1;
/*  152 */     int j = paramInt - i;
/*  153 */     FDBigInt localFDBigInt1 = b5p[i];
/*  154 */     if (localFDBigInt1 == null)
/*  155 */       localFDBigInt1 = big5pow(i);
/*  156 */     if (j < small5pow.length) {
/*  157 */       return b5p[paramInt] =  = localFDBigInt1.mult(small5pow[j]);
/*      */     }
/*  159 */     FDBigInt localFDBigInt2 = b5p[j];
/*  160 */     if (localFDBigInt2 == null)
/*  161 */       localFDBigInt2 = big5pow(j);
/*  162 */     return b5p[paramInt] =  = localFDBigInt1.mult(localFDBigInt2);
/*      */   }
/*      */ 
/*      */   private static FDBigInt multPow52(FDBigInt paramFDBigInt, int paramInt1, int paramInt2)
/*      */   {
/*  172 */     if (paramInt1 != 0) {
/*  173 */       if (paramInt1 < small5pow.length)
/*  174 */         paramFDBigInt = paramFDBigInt.mult(small5pow[paramInt1]);
/*      */       else {
/*  176 */         paramFDBigInt = paramFDBigInt.mult(big5pow(paramInt1));
/*      */       }
/*      */     }
/*  179 */     if (paramInt2 != 0) {
/*  180 */       paramFDBigInt.lshiftMe(paramInt2);
/*      */     }
/*  182 */     return paramFDBigInt;
/*      */   }
/*      */ 
/*      */   private static FDBigInt constructPow52(int paramInt1, int paramInt2)
/*      */   {
/*  190 */     FDBigInt localFDBigInt = new FDBigInt(big5pow(paramInt1));
/*  191 */     if (paramInt2 != 0) {
/*  192 */       localFDBigInt.lshiftMe(paramInt2);
/*      */     }
/*  194 */     return localFDBigInt;
/*      */   }
/*      */ 
/*      */   private FDBigInt doubleToBigInt(double paramDouble)
/*      */   {
/*  209 */     long l = Double.doubleToLongBits(paramDouble) & 0xFFFFFFFF;
/*  210 */     int i = (int)(l >>> 52);
/*  211 */     l &= 4503599627370495L;
/*  212 */     if (i > 0) {
/*  213 */       l |= 4503599627370496L;
/*      */     } else {
/*  215 */       assert (l != 0L) : l;
/*  216 */       i++;
/*  217 */       while ((l & 0x0) == 0L) {
/*  218 */         l <<= 1;
/*  219 */         i--;
/*      */       }
/*      */     }
/*  222 */     i -= 1023;
/*  223 */     int j = countBits(l);
/*      */ 
/*  228 */     int k = 53 - j;
/*  229 */     l >>>= k;
/*      */ 
/*  231 */     this.bigIntExp = (i + 1 - j);
/*  232 */     this.bigIntNBits = j;
/*  233 */     return new FDBigInt(l);
/*      */   }
/*      */ 
/*      */   private static double ulp(double paramDouble, boolean paramBoolean)
/*      */   {
/*  243 */     long l = Double.doubleToLongBits(paramDouble) & 0xFFFFFFFF;
/*  244 */     int i = (int)(l >>> 52);
/*      */ 
/*  246 */     if ((paramBoolean) && (i >= 52) && ((l & 0xFFFFFFFF) == 0L))
/*      */     {
/*  249 */       i--;
/*      */     }
/*      */     double d;
/*  251 */     if (i > 52)
/*  252 */       d = Double.longBitsToDouble(i - 52 << 52);
/*  253 */     else if (i == 0)
/*  254 */       d = 4.9E-324D;
/*      */     else {
/*  256 */       d = Double.longBitsToDouble(1L << i - 1);
/*      */     }
/*  258 */     if (paramBoolean) d = -d;
/*      */ 
/*  260 */     return d;
/*      */   }
/*      */ 
/*      */   float stickyRound(double paramDouble)
/*      */   {
/*  274 */     long l1 = Double.doubleToLongBits(paramDouble);
/*  275 */     long l2 = l1 & 0x0;
/*  276 */     if ((l2 == 0L) || (l2 == 9218868437227405312L))
/*      */     {
/*  279 */       return (float)paramDouble;
/*      */     }
/*  281 */     l1 += this.roundDir;
/*  282 */     return (float)Double.longBitsToDouble(l1);
/*      */   }
/*      */ 
/*      */   private void developLongDigits(int paramInt, long paramLong1, long paramLong2)
/*      */   {
/*  311 */     for (int m = 0; paramLong2 >= 10L; m++)
/*  312 */       paramLong2 /= 10L;
/*  313 */     if (m != 0) {
/*  314 */       long l1 = long5pow[m] << m;
/*  315 */       long l2 = paramLong1 % l1;
/*  316 */       paramLong1 /= l1;
/*  317 */       paramInt += m;
/*  318 */       if (l2 >= l1 >> 1)
/*      */       {
/*  320 */         paramLong1 += 1L;
/*      */       }
/*      */     }
/*      */     int i;
/*      */     char[] arrayOfChar1;
/*      */     int j;
/*      */     int k;
/*  323 */     if (paramLong1 <= 2147483647L) {
/*  324 */       assert (paramLong1 > 0L) : paramLong1;
/*      */ 
/*  327 */       int n = (int)paramLong1;
/*  328 */       i = 10;
/*  329 */       arrayOfChar1 = (char[])perThreadBuffer.get();
/*  330 */       j = i - 1;
/*  331 */       k = n % 10;
/*  332 */       n /= 10;
/*  333 */       while (k == 0) {
/*  334 */         paramInt++;
/*  335 */         k = n % 10;
/*  336 */         n /= 10;
/*      */       }
/*  338 */       while (n != 0) {
/*  339 */         arrayOfChar1[(j--)] = ((char)(k + 48));
/*  340 */         paramInt++;
/*  341 */         k = n % 10;
/*  342 */         n /= 10;
/*      */       }
/*  344 */       arrayOfChar1[j] = ((char)(k + 48));
/*      */     }
/*      */     else
/*      */     {
/*  348 */       i = 20;
/*  349 */       arrayOfChar1 = (char[])perThreadBuffer.get();
/*  350 */       j = i - 1;
/*  351 */       k = (int)(paramLong1 % 10L);
/*  352 */       paramLong1 /= 10L;
/*  353 */       while (k == 0) {
/*  354 */         paramInt++;
/*  355 */         k = (int)(paramLong1 % 10L);
/*  356 */         paramLong1 /= 10L;
/*      */       }
/*  358 */       while (paramLong1 != 0L) {
/*  359 */         arrayOfChar1[(j--)] = ((char)(k + 48));
/*  360 */         paramInt++;
/*  361 */         k = (int)(paramLong1 % 10L);
/*  362 */         paramLong1 /= 10L;
/*      */       }
/*  364 */       arrayOfChar1[j] = ((char)(k + 48));
/*      */     }
/*      */ 
/*  367 */     i -= j;
/*  368 */     char[] arrayOfChar2 = new char[i];
/*  369 */     System.arraycopy(arrayOfChar1, j, arrayOfChar2, 0, i);
/*  370 */     this.digits = arrayOfChar2;
/*  371 */     this.decExponent = (paramInt + 1);
/*  372 */     this.nDigits = i;
/*      */   }
/*      */ 
/*      */   private void roundup()
/*      */   {
/*      */     int i;
/*  385 */     int j = this.digits[(i = this.nDigits - 1)];
/*  386 */     if (j == 57) {
/*  387 */       while ((j == 57) && (i > 0)) {
/*  388 */         this.digits[i] = '0';
/*  389 */         j = this.digits[(--i)];
/*      */       }
/*  391 */       if (j == 57)
/*      */       {
/*  393 */         this.decExponent += 1;
/*  394 */         this.digits[0] = '1';
/*  395 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  399 */     this.digits[i] = ((char)(j + 1));
/*      */   }
/*      */ 
/*      */   public FloatingDecimal(double paramDouble)
/*      */   {
/*  407 */     long l1 = Double.doubleToLongBits(paramDouble);
/*      */ 
/*  413 */     if ((l1 & 0x0) != 0L) {
/*  414 */       this.isNegative = true;
/*  415 */       l1 ^= -9223372036854775808L;
/*      */     } else {
/*  417 */       this.isNegative = false;
/*      */     }
/*      */ 
/*  421 */     int i = (int)((l1 & 0x0) >> 52);
/*  422 */     long l2 = l1 & 0xFFFFFFFF;
/*  423 */     if (i == 2047) {
/*  424 */       this.isExceptional = true;
/*  425 */       if (l2 == 0L) {
/*  426 */         this.digits = infinity;
/*      */       } else {
/*  428 */         this.digits = notANumber;
/*  429 */         this.isNegative = false;
/*      */       }
/*  431 */       this.nDigits = this.digits.length;
/*  432 */       return;
/*      */     }
/*  434 */     this.isExceptional = false;
/*      */     int j;
/*  439 */     if (i == 0) {
/*  440 */       if (l2 == 0L)
/*      */       {
/*  442 */         this.decExponent = 0;
/*  443 */         this.digits = zero;
/*  444 */         this.nDigits = 1;
/*  445 */         return;
/*      */       }
/*  447 */       while ((l2 & 0x0) == 0L) {
/*  448 */         l2 <<= 1;
/*  449 */         i--;
/*      */       }
/*  451 */       j = 52 + i + 1;
/*  452 */       i++;
/*      */     } else {
/*  454 */       l2 |= 4503599627370496L;
/*  455 */       j = 53;
/*      */     }
/*  457 */     i -= 1023;
/*      */ 
/*  459 */     dtoa(i, l2, j);
/*      */   }
/*      */ 
/*      */   public FloatingDecimal(float paramFloat)
/*      */   {
/*  467 */     int i = Float.floatToIntBits(paramFloat);
/*      */ 
/*  473 */     if ((i & 0x80000000) != 0) {
/*  474 */       this.isNegative = true;
/*  475 */       i ^= -2147483648;
/*      */     } else {
/*  477 */       this.isNegative = false;
/*      */     }
/*      */ 
/*  481 */     int k = (i & 0x7F800000) >> 23;
/*  482 */     int j = i & 0x7FFFFF;
/*  483 */     if (k == 255) {
/*  484 */       this.isExceptional = true;
/*  485 */       if (j == 0L) {
/*  486 */         this.digits = infinity;
/*      */       } else {
/*  488 */         this.digits = notANumber;
/*  489 */         this.isNegative = false;
/*      */       }
/*  491 */       this.nDigits = this.digits.length;
/*  492 */       return;
/*      */     }
/*  494 */     this.isExceptional = false;
/*      */     int m;
/*  499 */     if (k == 0) {
/*  500 */       if (j == 0)
/*      */       {
/*  502 */         this.decExponent = 0;
/*  503 */         this.digits = zero;
/*  504 */         this.nDigits = 1;
/*  505 */         return;
/*      */       }
/*  507 */       while ((j & 0x800000) == 0) {
/*  508 */         j <<= 1;
/*  509 */         k--;
/*      */       }
/*  511 */       m = 23 + k + 1;
/*  512 */       k++;
/*      */     } else {
/*  514 */       j |= 8388608;
/*  515 */       m = 24;
/*      */     }
/*  517 */     k -= 127;
/*      */ 
/*  519 */     dtoa(k, j << 29, m);
/*      */   }
/*      */ 
/*      */   private void dtoa(int paramInt1, long paramLong, int paramInt2)
/*      */   {
/*  532 */     int i = countBits(paramLong);
/*  533 */     int j = Math.max(0, i - paramInt1 - 1);
/*  534 */     if ((paramInt1 <= 62) && (paramInt1 >= -21))
/*      */     {
/*  538 */       if ((j < long5pow.length) && (i + n5bits[j] < 64))
/*      */       {
/*  555 */         if (j == 0)
/*      */         {
/*      */           long l1;
/*  556 */           if (paramInt1 > paramInt2)
/*  557 */             l1 = 1L << paramInt1 - paramInt2 - 1;
/*      */           else {
/*  559 */             l1 = 0L;
/*      */           }
/*  561 */           if (paramInt1 >= 52)
/*  562 */             paramLong <<= paramInt1 - 52;
/*      */           else {
/*  564 */             paramLong >>>= 52 - paramInt1;
/*      */           }
/*  566 */           developLongDigits(0, paramLong, l1);
/*  567 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  618 */     double d = Double.longBitsToDouble(0x0 | paramLong & 0xFFFFFFFF);
/*      */ 
/*  620 */     int k = (int)Math.floor((d - 1.5D) * 0.289529654D + 0.176091259D + paramInt1 * 0.301029995663981D);
/*      */ 
/*  629 */     int n = Math.max(0, -k);
/*  630 */     int m = n + j + paramInt1;
/*      */ 
/*  632 */     int i2 = Math.max(0, k);
/*  633 */     int i1 = i2 + j;
/*      */ 
/*  635 */     int i4 = n;
/*  636 */     int i3 = m - paramInt2;
/*      */ 
/*  646 */     paramLong >>>= 53 - i;
/*  647 */     m -= i - 1;
/*  648 */     int i7 = Math.min(m, i1);
/*  649 */     m -= i7;
/*  650 */     i1 -= i7;
/*  651 */     i3 -= i7;
/*      */ 
/*  659 */     if (i == 1) {
/*  660 */       i3--;
/*      */     }
/*  662 */     if (i3 < 0)
/*      */     {
/*  666 */       m -= i3;
/*  667 */       i1 -= i3;
/*  668 */       i3 = 0;
/*      */     }
/*      */ 
/*  678 */     char[] arrayOfChar = this.digits = new char[18];
/*  679 */     int i8 = 0;
/*      */ 
/*  699 */     int i5 = i + m + (n < n5bits.length ? n5bits[n] : n * 3);
/*  700 */     int i6 = i1 + 1 + (i2 + 1 < n5bits.length ? n5bits[(i2 + 1)] : (i2 + 1) * 3);
/*      */     int i13;
/*      */     int i11;
/*      */     int i9;
/*      */     int i10;
/*      */     long l2;
/*  701 */     if ((i5 < 64) && (i6 < 64)) {
/*  702 */       if ((i5 < 32) && (i6 < 32))
/*      */       {
/*  704 */         int i12 = (int)paramLong * small5pow[n] << m;
/*  705 */         i13 = small5pow[i2] << i1;
/*  706 */         int i14 = small5pow[i4] << i3;
/*  707 */         int i15 = i13 * 10;
/*      */ 
/*  713 */         i8 = 0;
/*  714 */         i11 = i12 / i13;
/*  715 */         i12 = 10 * (i12 % i13);
/*  716 */         i14 *= 10;
/*  717 */         i9 = i12 < i14 ? 1 : 0;
/*  718 */         i10 = i12 + i14 > i15 ? 1 : 0;
/*  719 */         assert (i11 < 10) : i11;
/*  720 */         if ((i11 == 0) && (i10 == 0))
/*      */         {
/*  722 */           k--;
/*      */         }
/*  724 */         else arrayOfChar[(i8++)] = ((char)(48 + i11));
/*      */ 
/*  732 */         if ((k < -3) || (k >= 8)) {
/*  733 */           i10 = i9 = 0;
/*      */         }
/*  735 */         while ((i9 == 0) && (i10 == 0)) {
/*  736 */           i11 = i12 / i13;
/*  737 */           i12 = 10 * (i12 % i13);
/*  738 */           i14 *= 10;
/*  739 */           assert (i11 < 10) : i11;
/*  740 */           if (i14 > 0L) {
/*  741 */             i9 = i12 < i14 ? 1 : 0;
/*  742 */             i10 = i12 + i14 > i15 ? 1 : 0;
/*      */           }
/*      */           else
/*      */           {
/*  749 */             i9 = 1;
/*  750 */             i10 = 1;
/*      */           }
/*  752 */           arrayOfChar[(i8++)] = ((char)(48 + i11));
/*      */         }
/*  754 */         l2 = (i12 << 1) - i15;
/*      */       }
/*      */       else {
/*  757 */         long l3 = paramLong * long5pow[n] << m;
/*  758 */         long l4 = long5pow[i2] << i1;
/*  759 */         long l5 = long5pow[i4] << i3;
/*  760 */         long l6 = l4 * 10L;
/*      */ 
/*  766 */         i8 = 0;
/*  767 */         i11 = (int)(l3 / l4);
/*  768 */         l3 = 10L * (l3 % l4);
/*  769 */         l5 *= 10L;
/*  770 */         i9 = l3 < l5 ? 1 : 0;
/*  771 */         i10 = l3 + l5 > l6 ? 1 : 0;
/*  772 */         assert (i11 < 10) : i11;
/*  773 */         if ((i11 == 0) && (i10 == 0))
/*      */         {
/*  775 */           k--;
/*      */         }
/*  777 */         else arrayOfChar[(i8++)] = ((char)(48 + i11));
/*      */ 
/*  785 */         if ((k < -3) || (k >= 8)) {
/*  786 */           i10 = i9 = 0;
/*      */         }
/*  788 */         while ((i9 == 0) && (i10 == 0)) {
/*  789 */           i11 = (int)(l3 / l4);
/*  790 */           l3 = 10L * (l3 % l4);
/*  791 */           l5 *= 10L;
/*  792 */           assert (i11 < 10) : i11;
/*  793 */           if (l5 > 0L) {
/*  794 */             i9 = l3 < l5 ? 1 : 0;
/*  795 */             i10 = l3 + l5 > l6 ? 1 : 0;
/*      */           }
/*      */           else
/*      */           {
/*  802 */             i9 = 1;
/*  803 */             i10 = 1;
/*      */           }
/*  805 */           arrayOfChar[(i8++)] = ((char)(48 + i11));
/*      */         }
/*  807 */         l2 = (l3 << 1) - l6;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  817 */       FDBigInt localFDBigInt2 = multPow52(new FDBigInt(paramLong), n, m);
/*  818 */       FDBigInt localFDBigInt1 = constructPow52(i2, i1);
/*  819 */       FDBigInt localFDBigInt3 = constructPow52(i4, i3);
/*      */ 
/*  823 */       localFDBigInt2.lshiftMe(i13 = localFDBigInt1.normalizeMe());
/*  824 */       localFDBigInt3.lshiftMe(i13);
/*  825 */       FDBigInt localFDBigInt4 = localFDBigInt1.mult(10);
/*      */ 
/*  831 */       i8 = 0;
/*  832 */       i11 = localFDBigInt2.quoRemIteration(localFDBigInt1);
/*  833 */       localFDBigInt3 = localFDBigInt3.mult(10);
/*  834 */       i9 = localFDBigInt2.cmp(localFDBigInt3) < 0 ? 1 : 0;
/*  835 */       i10 = localFDBigInt2.add(localFDBigInt3).cmp(localFDBigInt4) > 0 ? 1 : 0;
/*  836 */       assert (i11 < 10) : i11;
/*  837 */       if ((i11 == 0) && (i10 == 0))
/*      */       {
/*  839 */         k--;
/*      */       }
/*  841 */       else arrayOfChar[(i8++)] = ((char)(48 + i11));
/*      */ 
/*  849 */       if ((k < -3) || (k >= 8)) {
/*  850 */         i10 = i9 = 0;
/*      */       }
/*  852 */       while ((i9 == 0) && (i10 == 0)) {
/*  853 */         i11 = localFDBigInt2.quoRemIteration(localFDBigInt1);
/*  854 */         localFDBigInt3 = localFDBigInt3.mult(10);
/*  855 */         assert (i11 < 10) : i11;
/*  856 */         i9 = localFDBigInt2.cmp(localFDBigInt3) < 0 ? 1 : 0;
/*  857 */         i10 = localFDBigInt2.add(localFDBigInt3).cmp(localFDBigInt4) > 0 ? 1 : 0;
/*  858 */         arrayOfChar[(i8++)] = ((char)(48 + i11));
/*      */       }
/*  860 */       if ((i10 != 0) && (i9 != 0)) {
/*  861 */         localFDBigInt2.lshiftMe(1);
/*  862 */         l2 = localFDBigInt2.cmp(localFDBigInt4);
/*      */       } else {
/*  864 */         l2 = 0L;
/*      */       }
/*      */     }
/*  866 */     this.decExponent = (k + 1);
/*  867 */     this.digits = arrayOfChar;
/*  868 */     this.nDigits = i8;
/*      */ 
/*  872 */     if (i10 != 0)
/*  873 */       if (i9 != 0) {
/*  874 */         if (l2 == 0L)
/*      */         {
/*  877 */           if ((arrayOfChar[(this.nDigits - 1)] & 0x1) != 0) roundup(); 
/*      */         }
/*  878 */         else if (l2 > 0L)
/*  879 */           roundup();
/*      */       }
/*      */       else
/*  882 */         roundup();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  890 */     StringBuffer localStringBuffer = new StringBuffer(this.nDigits + 8);
/*  891 */     if (this.isNegative) localStringBuffer.append('-');
/*  892 */     if (this.isExceptional) {
/*  893 */       localStringBuffer.append(this.digits, 0, this.nDigits);
/*      */     } else {
/*  895 */       localStringBuffer.append("0.");
/*  896 */       localStringBuffer.append(this.digits, 0, this.nDigits);
/*  897 */       localStringBuffer.append('e');
/*  898 */       localStringBuffer.append(this.decExponent);
/*      */     }
/*  900 */     return new String(localStringBuffer);
/*      */   }
/*      */ 
/*      */   public String toJavaFormatString() {
/*  904 */     char[] arrayOfChar = (char[])perThreadBuffer.get();
/*  905 */     int i = getChars(arrayOfChar);
/*  906 */     return new String(arrayOfChar, 0, i);
/*      */   }
/*      */ 
/*      */   private int getChars(char[] paramArrayOfChar) {
/*  910 */     assert (this.nDigits <= 19) : this.nDigits;
/*  911 */     int i = 0;
/*  912 */     if (this.isNegative) { paramArrayOfChar[0] = '-'; i = 1; }
/*  913 */     if (this.isExceptional) {
/*  914 */       System.arraycopy(this.digits, 0, paramArrayOfChar, i, this.nDigits);
/*  915 */       i += this.nDigits;
/*      */     }
/*      */     else
/*      */     {
/*      */       int j;
/*  917 */       if ((this.decExponent > 0) && (this.decExponent < 8))
/*      */       {
/*  919 */         j = Math.min(this.nDigits, this.decExponent);
/*  920 */         System.arraycopy(this.digits, 0, paramArrayOfChar, i, j);
/*  921 */         i += j;
/*  922 */         if (j < this.decExponent) {
/*  923 */           j = this.decExponent - j;
/*  924 */           System.arraycopy(zero, 0, paramArrayOfChar, i, j);
/*  925 */           i += j;
/*  926 */           paramArrayOfChar[(i++)] = '.';
/*  927 */           paramArrayOfChar[(i++)] = '0';
/*      */         } else {
/*  929 */           paramArrayOfChar[(i++)] = '.';
/*  930 */           if (j < this.nDigits) {
/*  931 */             int k = this.nDigits - j;
/*  932 */             System.arraycopy(this.digits, j, paramArrayOfChar, i, k);
/*  933 */             i += k;
/*      */           } else {
/*  935 */             paramArrayOfChar[(i++)] = '0';
/*      */           }
/*      */         }
/*  938 */       } else if ((this.decExponent <= 0) && (this.decExponent > -3)) {
/*  939 */         paramArrayOfChar[(i++)] = '0';
/*  940 */         paramArrayOfChar[(i++)] = '.';
/*  941 */         if (this.decExponent != 0) {
/*  942 */           System.arraycopy(zero, 0, paramArrayOfChar, i, -this.decExponent);
/*  943 */           i -= this.decExponent;
/*      */         }
/*  945 */         System.arraycopy(this.digits, 0, paramArrayOfChar, i, this.nDigits);
/*  946 */         i += this.nDigits;
/*      */       } else {
/*  948 */         paramArrayOfChar[(i++)] = this.digits[0];
/*  949 */         paramArrayOfChar[(i++)] = '.';
/*  950 */         if (this.nDigits > 1) {
/*  951 */           System.arraycopy(this.digits, 1, paramArrayOfChar, i, this.nDigits - 1);
/*  952 */           i += this.nDigits - 1;
/*      */         } else {
/*  954 */           paramArrayOfChar[(i++)] = '0';
/*      */         }
/*  956 */         paramArrayOfChar[(i++)] = 'E';
/*      */ 
/*  958 */         if (this.decExponent <= 0) {
/*  959 */           paramArrayOfChar[(i++)] = '-';
/*  960 */           j = -this.decExponent + 1;
/*      */         } else {
/*  962 */           j = this.decExponent - 1;
/*      */         }
/*      */ 
/*  965 */         if (j <= 9) {
/*  966 */           paramArrayOfChar[(i++)] = ((char)(j + 48));
/*  967 */         } else if (j <= 99) {
/*  968 */           paramArrayOfChar[(i++)] = ((char)(j / 10 + 48));
/*  969 */           paramArrayOfChar[(i++)] = ((char)(j % 10 + 48));
/*      */         } else {
/*  971 */           paramArrayOfChar[(i++)] = ((char)(j / 100 + 48));
/*  972 */           j %= 100;
/*  973 */           paramArrayOfChar[(i++)] = ((char)(j / 10 + 48));
/*  974 */           paramArrayOfChar[(i++)] = ((char)(j % 10 + 48));
/*      */         }
/*      */       }
/*      */     }
/*  978 */     return i;
/*      */   }
/*      */ 
/*      */   public void appendTo(Appendable paramAppendable)
/*      */   {
/*  989 */     char[] arrayOfChar = (char[])perThreadBuffer.get();
/*  990 */     int i = getChars(arrayOfChar);
/*  991 */     if ((paramAppendable instanceof StringBuilder))
/*  992 */       ((StringBuilder)paramAppendable).append(arrayOfChar, 0, i);
/*  993 */     else if ((paramAppendable instanceof StringBuffer)) {
/*  994 */       ((StringBuffer)paramAppendable).append(arrayOfChar, 0, i);
/*      */     }
/*  996 */     else if (!$assertionsDisabled) throw new AssertionError(); 
/*      */   }
/*      */ 
/*      */   public static FloatingDecimal readJavaFormatString(String paramString)
/*      */     throws NumberFormatException
/*      */   {
/* 1001 */     boolean bool = false;
/* 1002 */     int i = 0;
/*      */     try
/*      */     {
/* 1008 */       paramString = paramString.trim();
/*      */ 
/* 1010 */       int m = paramString.length();
/* 1011 */       if (m == 0) throw new NumberFormatException("empty String");
/* 1012 */       int n = 0;
/* 1013 */       switch (k = paramString.charAt(n)) {
/*      */       case '-':
/* 1015 */         bool = true;
/*      */       case '+':
/* 1018 */         n++;
/* 1019 */         i = 1;
/*      */       }
/*      */ 
/* 1023 */       int k = paramString.charAt(n);
/*      */       int i1;
/*      */       int i3;
/* 1024 */       if ((k == 78) || (k == 73)) {
/* 1025 */         i1 = 0;
/* 1026 */         char[] arrayOfChar2 = null;
/*      */ 
/* 1028 */         if (k == 78) {
/* 1029 */           arrayOfChar2 = notANumber;
/* 1030 */           i1 = 1;
/*      */         } else {
/* 1032 */           arrayOfChar2 = infinity;
/*      */         }
/*      */ 
/* 1036 */         i3 = 0;
/* 1037 */         while ((n < m) && (i3 < arrayOfChar2.length)) {
/* 1038 */           if (paramString.charAt(n) == arrayOfChar2[i3]) {
/* 1039 */             n++; i3++;
/*      */           }
/*      */           else {
/* 1042 */             break label824;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1049 */         if ((i3 == arrayOfChar2.length) && (n == m)) {
/* 1050 */           return i1 != 0 ? new FloatingDecimal((0.0D / 0.0D)) : new FloatingDecimal(bool ? (-1.0D / 0.0D) : (1.0D / 0.0D));
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1059 */         if ((k == 48) && 
/* 1060 */           (m > n + 1)) {
/* 1061 */           i1 = paramString.charAt(n + 1);
/* 1062 */           if ((i1 == 120) || (i1 == 88)) {
/* 1063 */             return parseHexString(paramString);
/*      */           }
/*      */         }
/*      */ 
/* 1067 */         char[] arrayOfChar1 = new char[m];
/* 1068 */         int i2 = 0;
/* 1069 */         i3 = 0;
/* 1070 */         int i4 = 0;
/* 1071 */         int i5 = 0;
/* 1072 */         int i6 = 0;
/*      */ 
/* 1074 */         while (n < m) {
/* 1075 */           switch (k = paramString.charAt(n)) {
/*      */           case '0':
/* 1077 */             if (i2 > 0)
/* 1078 */               i6++;
/*      */             else {
/* 1080 */               i5++;
/*      */             }
/* 1082 */             break;
/*      */           case '1':
/*      */           case '2':
/*      */           case '3':
/*      */           case '4':
/*      */           case '5':
/*      */           case '6':
/*      */           case '7':
/*      */           case '8':
/*      */           case '9':
/* 1092 */             while (i6 > 0) {
/* 1093 */               arrayOfChar1[(i2++)] = '0';
/* 1094 */               i6--;
/*      */             }
/* 1096 */             arrayOfChar1[(i2++)] = k;
/* 1097 */             break;
/*      */           case '.':
/* 1099 */             if (i3 != 0)
/*      */             {
/* 1101 */               throw new NumberFormatException("multiple points");
/*      */             }
/* 1103 */             i4 = n;
/* 1104 */             if (i != 0) {
/* 1105 */               i4--;
/*      */             }
/* 1107 */             i3 = 1;
/* 1108 */             break;
/*      */           case '/':
/*      */           default:
/* 1110 */             break;
/*      */           }
/* 1112 */           n++;
/*      */         }
/*      */ 
/* 1133 */         if (i2 == 0) {
/* 1134 */           arrayOfChar1 = zero;
/* 1135 */           i2 = 1;
/* 1136 */           if (i5 == 0);
/*      */         }
/*      */         else
/*      */         {
/*      */           int j;
/* 1149 */           if (i3 != 0)
/* 1150 */             j = i4 - i5;
/*      */           else {
/* 1152 */             j = i2 + i6;
/*      */           }
/*      */ 
/* 1158 */           if ((n < m) && (((k = paramString.charAt(n)) == 'e') || (k == 69))) {
/* 1159 */             int i7 = 1;
/* 1160 */             int i8 = 0;
/* 1161 */             int i9 = 214748364;
/* 1162 */             int i10 = 0;
/* 1163 */             switch (paramString.charAt(++n)) {
/*      */             case '-':
/* 1165 */               i7 = -1;
/*      */             case '+':
/* 1168 */               n++;
/*      */             }
/* 1170 */             int i11 = n;
/*      */ 
/* 1172 */             while (n < m) {
/* 1173 */               if (i8 >= i9)
/*      */               {
/* 1176 */                 i10 = 1;
/*      */               }
/* 1178 */               switch (k = paramString.charAt(n++)) {
/*      */               case '0':
/*      */               case '1':
/*      */               case '2':
/*      */               case '3':
/*      */               case '4':
/*      */               case '5':
/*      */               case '6':
/*      */               case '7':
/*      */               case '8':
/*      */               case '9':
/* 1189 */                 i8 = i8 * 10 + (k - 48);
/* 1190 */                 break;
/*      */               default:
/* 1192 */                 n--;
/*      */               }
/*      */             }
/*      */ 
/* 1196 */             int i12 = 324 + i2 + i6;
/* 1197 */             if ((i10 != 0) || (i8 > i12))
/*      */             {
/* 1210 */               j = i7 * i12;
/*      */             }
/*      */             else
/*      */             {
/* 1214 */               j += i7 * i8;
/*      */             }
/*      */ 
/* 1223 */             if (n == i11);
/*      */           }
/* 1230 */           else if ((n >= m) || ((n != m - 1) || ((paramString.charAt(n) == 'f') || (paramString.charAt(n) == 'F') || (paramString.charAt(n) == 'd') || (paramString.charAt(n) == 'D'))))
/*      */           {
/* 1239 */             return new FloatingDecimal(bool, j, arrayOfChar1, i2, false); }  } 
/*      */       } } catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {  }
/*      */ 
/* 1241 */     label824: throw new NumberFormatException("For input string: \"" + paramString + "\"");
/*      */   }
/*      */ 
/*      */   public strictfp double doubleValue()
/*      */   {
/* 1254 */     int i = Math.min(this.nDigits, 16);
/*      */ 
/* 1260 */     if ((this.digits == infinity) || (this.digits == notANumber)) {
/* 1261 */       if (this.digits == notANumber) {
/* 1262 */         return (0.0D / 0.0D);
/*      */       }
/* 1264 */       return this.isNegative ? (-1.0D / 0.0D) : (1.0D / 0.0D);
/*      */     }
/*      */ 
/* 1267 */     if (this.mustSetRoundDir) {
/* 1268 */       this.roundDir = 0;
/*      */     }
/*      */ 
/* 1274 */     int j = this.digits[0] - '0';
/* 1275 */     int k = Math.min(i, 9);
/* 1276 */     for (int m = 1; m < k; m++) {
/* 1277 */       j = j * 10 + this.digits[m] - 48;
/*      */     }
/* 1279 */     long l = j;
/* 1280 */     for (m = k; m < i; m++) {
/* 1281 */       l = l * 10L + (this.digits[m] - '0');
/*      */     }
/* 1283 */     double d1 = l;
/* 1284 */     m = this.decExponent - i;
/*      */     int n;
/* 1291 */     if (this.nDigits <= 15)
/*      */     {
/* 1302 */       if ((m == 0) || (d1 == 0.0D))
/* 1303 */         return this.isNegative ? -d1 : d1;
/*      */       double d2;
/*      */       double d3;
/* 1304 */       if (m >= 0) {
/* 1305 */         if (m <= maxSmallTen)
/*      */         {
/* 1310 */           d2 = d1 * small10pow[m];
/* 1311 */           if (this.mustSetRoundDir) {
/* 1312 */             d3 = d2 / small10pow[m];
/* 1313 */             this.roundDir = (d3 < d1 ? 1 : d3 == d1 ? 0 : -1);
/*      */           }
/*      */ 
/* 1317 */           return this.isNegative ? -d2 : d2;
/*      */         }
/* 1319 */         n = 15 - i;
/* 1320 */         if (m <= maxSmallTen + n)
/*      */         {
/* 1327 */           d1 *= small10pow[n];
/* 1328 */           d2 = d1 * small10pow[(m - n)];
/*      */ 
/* 1330 */           if (this.mustSetRoundDir) {
/* 1331 */             d3 = d2 / small10pow[(m - n)];
/* 1332 */             this.roundDir = (d3 < d1 ? 1 : d3 == d1 ? 0 : -1);
/*      */           }
/*      */ 
/* 1336 */           return this.isNegative ? -d2 : d2;
/*      */         }
/*      */ 
/*      */       }
/* 1342 */       else if (m >= -maxSmallTen)
/*      */       {
/* 1346 */         d2 = d1 / small10pow[(-m)];
/* 1347 */         d3 = d2 * small10pow[(-m)];
/* 1348 */         if (this.mustSetRoundDir) {
/* 1349 */           this.roundDir = (d3 < d1 ? 1 : d3 == d1 ? 0 : -1);
/*      */         }
/*      */ 
/* 1353 */         return this.isNegative ? -d2 : d2;
/*      */       }
/*      */     }
/*      */     double d4;
/* 1369 */     if (m > 0) {
/* 1370 */       if (this.decExponent > 309)
/*      */       {
/* 1375 */         return this.isNegative ? (-1.0D / 0.0D) : (1.0D / 0.0D);
/*      */       }
/* 1377 */       if ((m & 0xF) != 0) {
/* 1378 */         d1 *= small10pow[(m & 0xF)];
/*      */       }
/* 1380 */       if (m >>= 4 != 0)
/*      */       {
/* 1382 */         for (n = 0; m > 1; m >>= 1) {
/* 1383 */           if ((m & 0x1) != 0)
/* 1384 */             d1 *= big10pow[n];
/* 1382 */           n++;
/*      */         }
/*      */ 
/* 1392 */         d4 = d1 * big10pow[n];
/* 1393 */         if (Double.isInfinite(d4))
/*      */         {
/* 1406 */           d4 = d1 / 2.0D;
/* 1407 */           d4 *= big10pow[n];
/* 1408 */           if (Double.isInfinite(d4)) {
/* 1409 */             return this.isNegative ? (-1.0D / 0.0D) : (1.0D / 0.0D);
/*      */           }
/* 1411 */           d4 = 1.7976931348623157E+308D;
/*      */         }
/* 1413 */         d1 = d4;
/*      */       }
/* 1415 */     } else if (m < 0) {
/* 1416 */       m = -m;
/* 1417 */       if (this.decExponent < -325)
/*      */       {
/* 1422 */         return this.isNegative ? -0.0D : 0.0D;
/*      */       }
/* 1424 */       if ((m & 0xF) != 0) {
/* 1425 */         d1 /= small10pow[(m & 0xF)];
/*      */       }
/* 1427 */       if (m >>= 4 != 0)
/*      */       {
/* 1429 */         for (n = 0; m > 1; m >>= 1) {
/* 1430 */           if ((m & 0x1) != 0)
/* 1431 */             d1 *= tiny10pow[n];
/* 1429 */           n++;
/*      */         }
/*      */ 
/* 1439 */         d4 = d1 * tiny10pow[n];
/* 1440 */         if (d4 == 0.0D)
/*      */         {
/* 1453 */           d4 = d1 * 2.0D;
/* 1454 */           d4 *= tiny10pow[n];
/* 1455 */           if (d4 == 0.0D) {
/* 1456 */             return this.isNegative ? -0.0D : 0.0D;
/*      */           }
/* 1458 */           d4 = 4.9E-324D;
/*      */         }
/* 1460 */         d1 = d4;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1471 */     FDBigInt localFDBigInt1 = new FDBigInt(l, this.digits, i, this.nDigits);
/* 1472 */     m = this.decExponent - this.nDigits;
/*      */     while (true)
/*      */     {
/* 1479 */       FDBigInt localFDBigInt2 = doubleToBigInt(d1);
/*      */       int i2;
/*      */       int i1;
/*      */       int i4;
/*      */       int i3;
/* 1493 */       if (m >= 0) {
/* 1494 */         i1 = i2 = 0;
/* 1495 */         i3 = i4 = m;
/*      */       } else {
/* 1497 */         i1 = i2 = -m;
/* 1498 */         i3 = i4 = 0;
/*      */       }
/* 1500 */       if (this.bigIntExp >= 0)
/* 1501 */         i1 += this.bigIntExp;
/*      */       else {
/* 1503 */         i3 -= this.bigIntExp;
/*      */       }
/* 1505 */       int i5 = i1;
/*      */       int i6;
/* 1509 */       if (this.bigIntExp + this.bigIntNBits <= -1022)
/*      */       {
/* 1513 */         i6 = this.bigIntExp + 1023 + 52;
/*      */       }
/* 1515 */       else i6 = 54 - this.bigIntNBits;
/*      */ 
/* 1517 */       i1 += i6;
/* 1518 */       i3 += i6;
/*      */ 
/* 1521 */       int i7 = Math.min(i1, Math.min(i3, i5));
/* 1522 */       i1 -= i7;
/* 1523 */       i3 -= i7;
/* 1524 */       i5 -= i7;
/*      */ 
/* 1526 */       localFDBigInt2 = multPow52(localFDBigInt2, i2, i1);
/* 1527 */       FDBigInt localFDBigInt3 = multPow52(new FDBigInt(localFDBigInt1), i4, i3);
/*      */       int i8;
/*      */       boolean bool;
/*      */       FDBigInt localFDBigInt4;
/* 1545 */       if ((i8 = localFDBigInt2.cmp(localFDBigInt3)) > 0) {
/* 1546 */         bool = true;
/* 1547 */         localFDBigInt4 = localFDBigInt2.sub(localFDBigInt3);
/* 1548 */         if ((this.bigIntNBits == 1) && (this.bigIntExp > -1022))
/*      */         {
/* 1553 */           i5--;
/* 1554 */           if (i5 < 0)
/*      */           {
/* 1557 */             i5 = 0;
/* 1558 */             localFDBigInt4.lshiftMe(1);
/*      */           }
/*      */         }
/*      */       } else { if (i8 >= 0) break;
/* 1562 */         bool = false;
/* 1563 */         localFDBigInt4 = localFDBigInt3.sub(localFDBigInt2);
/*      */       }
/*      */ 
/* 1569 */       FDBigInt localFDBigInt5 = constructPow52(i2, i5);
/* 1570 */       if ((i8 = localFDBigInt4.cmp(localFDBigInt5)) < 0)
/*      */       {
/* 1573 */         if (this.mustSetRoundDir) {
/* 1574 */           this.roundDir = (bool ? -1 : 1);
/*      */         }
/*      */       }
/* 1577 */       else if (i8 == 0)
/*      */       {
/* 1580 */         d1 += 0.5D * ulp(d1, bool);
/*      */ 
/* 1582 */         if (this.mustSetRoundDir) {
/* 1583 */           this.roundDir = (bool ? -1 : 1);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1591 */         d1 += ulp(d1, bool);
/* 1592 */         if (d1 != 0.0D) if (d1 == (1.0D / 0.0D)) {
/* 1593 */             break;
/*      */           }
/*      */       }
/*      */     }
/*      */ 
/* 1598 */     return this.isNegative ? -d1 : d1;
/*      */   }
/*      */ 
/*      */   public strictfp float floatValue()
/*      */   {
/* 1613 */     int i = Math.min(this.nDigits, 8);
/*      */ 
/* 1618 */     if ((this.digits == infinity) || (this.digits == notANumber)) {
/* 1619 */       if (this.digits == notANumber) {
/* 1620 */         return (0.0F / 0.0F);
/*      */       }
/* 1622 */       return this.isNegative ? (1.0F / -1.0F) : (1.0F / 1.0F);
/*      */     }
/*      */ 
/* 1628 */     int j = this.digits[0] - '0';
/* 1629 */     for (int k = 1; k < i; k++) {
/* 1630 */       j = j * 10 + this.digits[k] - 48;
/*      */     }
/* 1632 */     float f = j;
/* 1633 */     k = this.decExponent - i;
/*      */ 
/* 1640 */     if (this.nDigits <= 7)
/*      */     {
/* 1651 */       if ((k == 0) || (f == 0.0F))
/* 1652 */         return this.isNegative ? -f : f;
/* 1653 */       if (k >= 0) {
/* 1654 */         if (k <= singleMaxSmallTen)
/*      */         {
/* 1659 */           f *= singleSmall10pow[k];
/* 1660 */           return this.isNegative ? -f : f;
/*      */         }
/* 1662 */         int m = 7 - i;
/* 1663 */         if (k <= singleMaxSmallTen + m)
/*      */         {
/* 1670 */           f *= singleSmall10pow[m];
/* 1671 */           f *= singleSmall10pow[(k - m)];
/* 1672 */           return this.isNegative ? -f : f;
/*      */         }
/*      */ 
/*      */       }
/* 1678 */       else if (k >= -singleMaxSmallTen)
/*      */       {
/* 1682 */         f /= singleSmall10pow[(-k)];
/* 1683 */         return this.isNegative ? -f : f;
/*      */       }
/*      */ 
/*      */     }
/* 1689 */     else if ((this.decExponent >= this.nDigits) && (this.nDigits + this.decExponent <= 15))
/*      */     {
/* 1699 */       long l = j;
/* 1700 */       for (int n = i; n < this.nDigits; n++) {
/* 1701 */         l = l * 10L + (this.digits[n] - '0');
/*      */       }
/* 1703 */       double d2 = l;
/* 1704 */       k = this.decExponent - this.nDigits;
/* 1705 */       d2 *= small10pow[k];
/* 1706 */       f = (float)d2;
/* 1707 */       return this.isNegative ? -f : f;
/*      */     }
/*      */ 
/* 1719 */     if (this.decExponent > 39)
/*      */     {
/* 1724 */       return this.isNegative ? (1.0F / -1.0F) : (1.0F / 1.0F);
/* 1725 */     }if (this.decExponent < -46)
/*      */     {
/* 1730 */       return this.isNegative ? -0.0F : 0.0F;
/*      */     }
/*      */ 
/* 1745 */     this.mustSetRoundDir = (!this.fromHex);
/* 1746 */     double d1 = doubleValue();
/* 1747 */     return stickyRound(d1);
/*      */   }
/*      */ 
/*      */   private static synchronized Pattern getHexFloatPattern()
/*      */   {
/* 1869 */     if (hexFloatPattern == null) {
/* 1870 */       hexFloatPattern = Pattern.compile("([-+])?0[xX](((\\p{XDigit}+)\\.?)|((\\p{XDigit}*)\\.(\\p{XDigit}+)))[pP]([-+])?(\\p{Digit}+)[fFdD]?");
/*      */     }
/*      */ 
/* 1875 */     return hexFloatPattern;
/*      */   }
/*      */ 
/*      */   static FloatingDecimal parseHexString(String paramString)
/*      */   {
/* 1886 */     Matcher localMatcher = getHexFloatPattern().matcher(paramString);
/* 1887 */     boolean bool = localMatcher.matches();
/*      */ 
/* 1889 */     if (!bool)
/*      */     {
/* 1891 */       throw new NumberFormatException("For input string: \"" + paramString + "\"");
/*      */     }
/*      */ 
/* 1918 */     String str1 = localMatcher.group(1);
/* 1919 */     double d = (str1 == null) || (str1.equals("+")) ? 1.0D : -1.0D;
/*      */ 
/* 1951 */     String str2 = null;
/* 1952 */     int i = 0;
/* 1953 */     int j = 0;
/*      */ 
/* 1955 */     int k = 0;
/*      */ 
/* 1958 */     int m = 0;
/*      */     String str4;
/* 1972 */     if ((str4 = localMatcher.group(4)) != null)
/*      */     {
/* 1974 */       str2 = stripLeadingZeros(str4);
/* 1975 */       k = str2.length();
/*      */     }
/*      */     else
/*      */     {
/* 1980 */       String str5 = stripLeadingZeros(localMatcher.group(6));
/* 1981 */       k = str5.length();
/*      */ 
/* 1984 */       String str6 = localMatcher.group(7);
/* 1985 */       m = str6.length();
/*      */ 
/* 1988 */       str2 = (str5 == null ? "" : str5) + str6;
/*      */     }
/*      */ 
/* 1994 */     str2 = stripLeadingZeros(str2);
/* 1995 */     i = str2.length();
/*      */ 
/* 2000 */     if (k >= 1)
/* 2001 */       j = 4 * (k - 1);
/*      */     else {
/* 2003 */       j = -4 * (m - i + 1);
/*      */     }
/*      */ 
/* 2009 */     if (i == 0) {
/* 2010 */       return new FloatingDecimal(d * 0.0D);
/*      */     }
/*      */ 
/* 2022 */     String str3 = localMatcher.group(8);
/* 2023 */     m = (str3 == null) || (str3.equals("+")) ? 1 : 0;
/*      */     long l1;
/*      */     try
/*      */     {
/* 2026 */       l1 = Integer.parseInt(localMatcher.group(9));
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 2042 */       return new FloatingDecimal(d * (m != 0 ? (1.0D / 0.0D) : 0.0D));
/*      */     }
/*      */ 
/* 2046 */     long l2 = (m != 0 ? 1L : -1L) * l1;
/*      */ 
/* 2051 */     long l3 = l2 + j;
/*      */ 
/* 2057 */     int n = 0;
/* 2058 */     int i1 = 0;
/* 2059 */     int i2 = 0;
/* 2060 */     int i3 = 0;
/* 2061 */     long l4 = 0L;
/*      */ 
/* 2068 */     long l5 = getHexDigit(str2, 0);
/*      */ 
/* 2079 */     if (l5 == 1L) {
/* 2080 */       l4 |= l5 << 52;
/* 2081 */       i3 = 48;
/*      */     }
/* 2083 */     else if (l5 <= 3L) {
/* 2084 */       l4 |= l5 << 51;
/* 2085 */       i3 = 47;
/* 2086 */       l3 += 1L;
/*      */     }
/* 2088 */     else if (l5 <= 7L) {
/* 2089 */       l4 |= l5 << 50;
/* 2090 */       i3 = 46;
/* 2091 */       l3 += 2L;
/*      */     }
/* 2093 */     else if (l5 <= 15L) {
/* 2094 */       l4 |= l5 << 49;
/* 2095 */       i3 = 45;
/* 2096 */       l3 += 3L;
/*      */     } else {
/* 2098 */       throw new AssertionError("Result from digit conversion too large!");
/*      */     }
/*      */ 
/* 2120 */     int i4 = 0;
/*      */     long l6;
/* 2121 */     for (i4 = 1; 
/* 2122 */       (i4 < i) && (i3 >= 0); 
/* 2123 */       i4++) {
/* 2124 */       l6 = getHexDigit(str2, i4);
/* 2125 */       l4 |= l6 << i3;
/* 2126 */       i3 -= 4;
/*      */     }
/*      */ 
/* 2134 */     if (i4 < i) {
/* 2135 */       l6 = getHexDigit(str2, i4);
/*      */ 
/* 2139 */       switch (i3)
/*      */       {
/*      */       case -1:
/* 2143 */         l4 |= (l6 & 0xE) >> 1;
/* 2144 */         n = (l6 & 1L) != 0L ? 1 : 0;
/* 2145 */         break;
/*      */       case -2:
/* 2150 */         l4 |= (l6 & 0xC) >> 2;
/* 2151 */         n = (l6 & 0x2) != 0L ? 1 : 0;
/* 2152 */         i1 = (l6 & 1L) != 0L ? 1 : 0;
/* 2153 */         break;
/*      */       case -3:
/* 2157 */         l4 |= (l6 & 0x8) >> 3;
/*      */ 
/* 2159 */         n = (l6 & 0x4) != 0L ? 1 : 0;
/* 2160 */         i1 = (l6 & 0x3) != 0L ? 1 : 0;
/* 2161 */         break;
/*      */       case -4:
/* 2166 */         n = (l6 & 0x8) != 0L ? 1 : 0;
/*      */ 
/* 2168 */         i1 = (l6 & 0x7) != 0L ? 1 : 0;
/* 2169 */         break;
/*      */       default:
/* 2172 */         throw new AssertionError("Unexpected shift distance remainder.");
/*      */       }
/*      */ 
/* 2181 */       i4++;
/* 2182 */       while ((i4 < i) && (i1 == 0)) {
/* 2183 */         l6 = getHexDigit(str2, i4);
/* 2184 */         i1 = (i1 != 0) || (l6 != 0L) ? 1 : 0;
/* 2185 */         i4++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2195 */     if (l3 > 1023L)
/*      */     {
/* 2197 */       return new FloatingDecimal(d * (1.0D / 0.0D));
/*      */     }
/* 2199 */     if ((l3 <= 1023L) && (l3 >= -1022L))
/*      */     {
/* 2213 */       l4 = l3 + 1023L << 52 & 0x0 | 0xFFFFFFFF & l4;
/*      */     }
/*      */     else
/*      */     {
/* 2222 */       if (l3 < -1075L)
/*      */       {
/* 2226 */         return new FloatingDecimal(d * 0.0D);
/*      */       }
/*      */ 
/* 2234 */       i1 = (i1 != 0) || (n != 0) ? 1 : 0;
/* 2235 */       n = 0;
/*      */ 
/* 2243 */       i5 = 53 - ((int)l3 - -1074 + 1);
/*      */ 
/* 2245 */       assert ((i5 >= 1) && (i5 <= 53));
/*      */ 
/* 2249 */       n = (l4 & 1L << i5 - 1) != 0L ? 1 : 0;
/* 2250 */       if (i5 > 1)
/*      */       {
/* 2253 */         long l7 = -1L << i5 - 1 ^ 0xFFFFFFFF;
/* 2254 */         i1 = (i1 != 0) || ((l4 & l7) != 0L) ? 1 : 0;
/*      */       }
/*      */ 
/* 2258 */       l4 >>= i5;
/*      */ 
/* 2260 */       l4 = 0L | 0xFFFFFFFF & l4;
/*      */     }
/*      */ 
/* 2292 */     int i5 = 0;
/* 2293 */     int i6 = (l4 & 1L) == 0L ? 1 : 0;
/* 2294 */     if (((i6 != 0) && (n != 0) && (i1 != 0)) || ((i6 == 0) && (n != 0)))
/*      */     {
/* 2296 */       i5 = 1;
/* 2297 */       l4 += 1L;
/*      */     }
/*      */ 
/* 2300 */     FloatingDecimal localFloatingDecimal = new FloatingDecimal(FpUtils.rawCopySign(Double.longBitsToDouble(l4), d));
/*      */ 
/* 2327 */     if ((l3 >= -150L) && (l3 <= 127L))
/*      */     {
/* 2346 */       if ((l4 & 0xFFFFFFF) == 0L)
/*      */       {
/* 2355 */         if ((n != 0) || (i1 != 0)) {
/* 2356 */           if (i6 != 0)
/*      */           {
/* 2365 */             if ((n ^ i1) != 0) {
/* 2366 */               localFloatingDecimal.roundDir = 1;
/*      */             }
/*      */ 
/*      */           }
/* 2377 */           else if (n != 0) {
/* 2378 */             localFloatingDecimal.roundDir = -1;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2384 */     localFloatingDecimal.fromHex = true;
/* 2385 */     return localFloatingDecimal;
/*      */   }
/*      */ 
/*      */   static String stripLeadingZeros(String paramString)
/*      */   {
/* 2394 */     return paramString.replaceFirst("^0+", "");
/*      */   }
/*      */ 
/*      */   static int getHexDigit(String paramString, int paramInt)
/*      */   {
/* 2402 */     int i = Character.digit(paramString.charAt(paramInt), 16);
/* 2403 */     if ((i <= -1) || (i >= 16)) {
/* 2404 */       throw new AssertionError("Unexpected failure of digit conversion of " + paramString.charAt(paramInt));
/*      */     }
/*      */ 
/* 2407 */     return i;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.FloatingDecimal
 * JD-Core Version:    0.6.2
 */