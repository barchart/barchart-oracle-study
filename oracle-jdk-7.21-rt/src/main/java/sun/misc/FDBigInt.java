/*      */ package sun.misc;
/*      */ 
/*      */ class FDBigInt
/*      */ {
/*      */   int nWords;
/*      */   int[] data;
/*      */ 
/*      */   public FDBigInt(int paramInt)
/*      */   {
/* 2423 */     this.nWords = 1;
/* 2424 */     this.data = new int[1];
/* 2425 */     this.data[0] = paramInt;
/*      */   }
/*      */ 
/*      */   public FDBigInt(long paramLong) {
/* 2429 */     this.data = new int[2];
/* 2430 */     this.data[0] = ((int)paramLong);
/* 2431 */     this.data[1] = ((int)(paramLong >>> 32));
/* 2432 */     this.nWords = (this.data[1] == 0 ? 1 : 2);
/*      */   }
/*      */ 
/*      */   public FDBigInt(FDBigInt paramFDBigInt) {
/* 2436 */     this.data = new int[this.nWords = paramFDBigInt.nWords];
/* 2437 */     System.arraycopy(paramFDBigInt.data, 0, this.data, 0, this.nWords);
/*      */   }
/*      */ 
/*      */   private FDBigInt(int[] paramArrayOfInt, int paramInt) {
/* 2441 */     this.data = paramArrayOfInt;
/* 2442 */     this.nWords = paramInt;
/*      */   }
/*      */ 
/*      */   public FDBigInt(long paramLong, char[] paramArrayOfChar, int paramInt1, int paramInt2) {
/* 2446 */     int i = (paramInt2 + 8) / 9;
/* 2447 */     if (i < 2) i = 2;
/* 2448 */     this.data = new int[i];
/* 2449 */     this.data[0] = ((int)paramLong);
/* 2450 */     this.data[1] = ((int)(paramLong >>> 32));
/* 2451 */     this.nWords = (this.data[1] == 0 ? 1 : 2);
/* 2452 */     int j = paramInt1;
/* 2453 */     int k = paramInt2 - 5;
/*      */ 
/* 2455 */     while (j < k) {
/* 2456 */       n = j + 5;
/* 2457 */       m = paramArrayOfChar[(j++)] - '0';
/* 2458 */       while (j < n) {
/* 2459 */         m = 10 * m + paramArrayOfChar[(j++)] - 48;
/*      */       }
/* 2461 */       multaddMe(100000, m);
/*      */     }
/* 2463 */     int n = 1;
/* 2464 */     int m = 0;
/* 2465 */     while (j < paramInt2) {
/* 2466 */       m = 10 * m + paramArrayOfChar[(j++)] - 48;
/* 2467 */       n *= 10;
/*      */     }
/* 2469 */     if (n != 1)
/* 2470 */       multaddMe(n, m);
/*      */   }
/*      */ 
/*      */   public void lshiftMe(int paramInt)
/*      */     throws IllegalArgumentException
/*      */   {
/* 2480 */     if (paramInt <= 0) {
/* 2481 */       if (paramInt == 0) {
/* 2482 */         return;
/*      */       }
/* 2484 */       throw new IllegalArgumentException("negative shift count");
/*      */     }
/* 2486 */     int i = paramInt >> 5;
/* 2487 */     int j = paramInt & 0x1F;
/* 2488 */     int k = 32 - j;
/* 2489 */     int[] arrayOfInt1 = this.data;
/* 2490 */     int[] arrayOfInt2 = this.data;
/* 2491 */     if (this.nWords + i + 1 > arrayOfInt1.length)
/*      */     {
/* 2493 */       arrayOfInt1 = new int[this.nWords + i + 1];
/*      */     }
/* 2495 */     int m = this.nWords + i;
/* 2496 */     int n = this.nWords - 1;
/* 2497 */     if (j == 0)
/*      */     {
/* 2499 */       System.arraycopy(arrayOfInt2, 0, arrayOfInt1, i, this.nWords);
/* 2500 */       m = i - 1;
/*      */     } else {
/* 2502 */       arrayOfInt1[(m--)] = (arrayOfInt2[n] >>> k);
/* 2503 */       while (n >= 1) {
/* 2504 */         arrayOfInt1[(m--)] = (arrayOfInt2[n] << j | arrayOfInt2[(--n)] >>> k);
/*      */       }
/* 2506 */       arrayOfInt1[(m--)] = (arrayOfInt2[n] << j);
/*      */     }
/* 2508 */     while (m >= 0) {
/* 2509 */       arrayOfInt1[(m--)] = 0;
/*      */     }
/* 2511 */     this.data = arrayOfInt1;
/* 2512 */     this.nWords += i + 1;
/*      */ 
/* 2515 */     while ((this.nWords > 1) && (this.data[(this.nWords - 1)] == 0))
/* 2516 */       this.nWords -= 1;
/*      */   }
/*      */ 
/*      */   public int normalizeMe()
/*      */     throws IllegalArgumentException
/*      */   {
/* 2533 */     int j = 0;
/* 2534 */     int k = 0;
/* 2535 */     int m = 0;
/* 2536 */     for (int i = this.nWords - 1; (i >= 0) && ((m = this.data[i]) == 0); i--) {
/* 2537 */       j++;
/*      */     }
/* 2539 */     if (i < 0)
/*      */     {
/* 2541 */       throw new IllegalArgumentException("zero value");
/*      */     }
/*      */ 
/* 2549 */     this.nWords -= j;
/*      */ 
/* 2555 */     if ((m & 0xF0000000) != 0)
/*      */     {
/* 2558 */       for (k = 32; (m & 0xF0000000) != 0; k--)
/* 2559 */         m >>>= 1;
/*      */     }
/* 2561 */     while (m <= 1048575)
/*      */     {
/* 2563 */       m <<= 8;
/* 2564 */       k += 8;
/*      */     }
/* 2566 */     while (m <= 134217727) {
/* 2567 */       m <<= 1;
/* 2568 */       k++;
/*      */     }
/*      */ 
/* 2571 */     if (k != 0)
/* 2572 */       lshiftMe(k);
/* 2573 */     return k;
/*      */   }
/*      */ 
/*      */   public FDBigInt mult(int paramInt)
/*      */   {
/* 2582 */     long l1 = paramInt;
/*      */ 
/* 2587 */     int[] arrayOfInt = new int[l1 * (this.data[(this.nWords - 1)] & 0xFFFFFFFF) > 268435455L ? this.nWords + 1 : this.nWords];
/* 2588 */     long l2 = 0L;
/* 2589 */     for (int i = 0; i < this.nWords; i++) {
/* 2590 */       l2 += l1 * (this.data[i] & 0xFFFFFFFF);
/* 2591 */       arrayOfInt[i] = ((int)l2);
/* 2592 */       l2 >>>= 32;
/*      */     }
/* 2594 */     if (l2 == 0L) {
/* 2595 */       return new FDBigInt(arrayOfInt, this.nWords);
/*      */     }
/* 2597 */     arrayOfInt[this.nWords] = ((int)l2);
/* 2598 */     return new FDBigInt(arrayOfInt, this.nWords + 1);
/*      */   }
/*      */ 
/*      */   public void multaddMe(int paramInt1, int paramInt2)
/*      */   {
/* 2609 */     long l1 = paramInt1;
/*      */ 
/* 2613 */     long l2 = l1 * (this.data[0] & 0xFFFFFFFF) + (paramInt2 & 0xFFFFFFFF);
/* 2614 */     this.data[0] = ((int)l2);
/* 2615 */     l2 >>>= 32;
/* 2616 */     for (int i = 1; i < this.nWords; i++) {
/* 2617 */       l2 += l1 * (this.data[i] & 0xFFFFFFFF);
/* 2618 */       this.data[i] = ((int)l2);
/* 2619 */       l2 >>>= 32;
/*      */     }
/* 2621 */     if (l2 != 0L) {
/* 2622 */       this.data[this.nWords] = ((int)l2);
/* 2623 */       this.nWords += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public FDBigInt mult(FDBigInt paramFDBigInt)
/*      */   {
/* 2634 */     int[] arrayOfInt = new int[this.nWords + paramFDBigInt.nWords];
/*      */ 
/* 2638 */     for (int i = 0; i < this.nWords; i++) {
/* 2639 */       long l1 = this.data[i] & 0xFFFFFFFF;
/* 2640 */       long l2 = 0L;
/*      */ 
/* 2642 */       for (int j = 0; j < paramFDBigInt.nWords; j++) {
/* 2643 */         l2 += (arrayOfInt[(i + j)] & 0xFFFFFFFF) + l1 * (paramFDBigInt.data[j] & 0xFFFFFFFF);
/* 2644 */         arrayOfInt[(i + j)] = ((int)l2);
/* 2645 */         l2 >>>= 32;
/*      */       }
/* 2647 */       arrayOfInt[(i + j)] = ((int)l2);
/*      */     }
/*      */ 
/* 2650 */     for (i = arrayOfInt.length - 1; (i > 0) && 
/* 2651 */       (arrayOfInt[i] == 0); i--);
/* 2653 */     return new FDBigInt(arrayOfInt, i + 1);
/*      */   }
/*      */ 
/*      */   public FDBigInt add(FDBigInt paramFDBigInt)
/*      */   {
/* 2664 */     long l = 0L;
/*      */     int[] arrayOfInt1;
/*      */     int j;
/*      */     int[] arrayOfInt2;
/*      */     int k;
/* 2667 */     if (this.nWords >= paramFDBigInt.nWords) {
/* 2668 */       arrayOfInt1 = this.data;
/* 2669 */       j = this.nWords;
/* 2670 */       arrayOfInt2 = paramFDBigInt.data;
/* 2671 */       k = paramFDBigInt.nWords;
/*      */     } else {
/* 2673 */       arrayOfInt1 = paramFDBigInt.data;
/* 2674 */       j = paramFDBigInt.nWords;
/* 2675 */       arrayOfInt2 = this.data;
/* 2676 */       k = this.nWords;
/*      */     }
/* 2678 */     int[] arrayOfInt3 = new int[j];
/* 2679 */     for (int i = 0; i < j; i++) {
/* 2680 */       l += (arrayOfInt1[i] & 0xFFFFFFFF);
/* 2681 */       if (i < k) {
/* 2682 */         l += (arrayOfInt2[i] & 0xFFFFFFFF);
/*      */       }
/* 2684 */       arrayOfInt3[i] = ((int)l);
/* 2685 */       l >>= 32;
/*      */     }
/* 2687 */     if (l != 0L)
/*      */     {
/* 2689 */       int[] arrayOfInt4 = new int[arrayOfInt3.length + 1];
/* 2690 */       System.arraycopy(arrayOfInt3, 0, arrayOfInt4, 0, arrayOfInt3.length);
/* 2691 */       arrayOfInt4[(i++)] = ((int)l);
/* 2692 */       return new FDBigInt(arrayOfInt4, i);
/*      */     }
/* 2694 */     return new FDBigInt(arrayOfInt3, i);
/*      */   }
/*      */ 
/*      */   public FDBigInt sub(FDBigInt paramFDBigInt)
/*      */   {
/* 2703 */     int[] arrayOfInt = new int[this.nWords];
/*      */ 
/* 2705 */     int j = this.nWords;
/* 2706 */     int k = paramFDBigInt.nWords;
/* 2707 */     int m = 0;
/* 2708 */     long l = 0L;
/* 2709 */     for (int i = 0; i < j; i++) {
/* 2710 */       l += (this.data[i] & 0xFFFFFFFF);
/* 2711 */       if (i < k) {
/* 2712 */         l -= (paramFDBigInt.data[i] & 0xFFFFFFFF);
/*      */       }
/* 2714 */       if ((arrayOfInt[i] = (int)l) == 0)
/* 2715 */         m++;
/*      */       else
/* 2717 */         m = 0;
/* 2718 */       l >>= 32;
/*      */     }
/* 2720 */     assert (l == 0L) : l;
/* 2721 */     assert (dataInRangeIsZero(i, k, paramFDBigInt));
/* 2722 */     return new FDBigInt(arrayOfInt, j - m);
/*      */   }
/*      */ 
/*      */   private static boolean dataInRangeIsZero(int paramInt1, int paramInt2, FDBigInt paramFDBigInt) {
/* 2726 */     while (paramInt1 < paramInt2)
/* 2727 */       if (paramFDBigInt.data[(paramInt1++)] != 0)
/* 2728 */         return false;
/* 2729 */     return true;
/*      */   }
/*      */ 
/*      */   public int cmp(FDBigInt paramFDBigInt)
/*      */   {
/*      */     int i;
/* 2741 */     if (this.nWords > paramFDBigInt.nWords)
/*      */     {
/* 2744 */       j = paramFDBigInt.nWords - 1;
/* 2745 */       for (i = this.nWords - 1; i > j; i--)
/* 2746 */         if (this.data[i] != 0) return 1; 
/*      */     }
/* 2747 */     else if (this.nWords < paramFDBigInt.nWords)
/*      */     {
/* 2750 */       j = this.nWords - 1;
/* 2751 */       for (i = paramFDBigInt.nWords - 1; i > j; i--)
/* 2752 */         if (paramFDBigInt.data[i] != 0) return -1; 
/*      */     }
/* 2754 */     else { i = this.nWords - 1; }
/*      */ 
/* 2756 */     while ((i > 0) && 
/* 2757 */       (this.data[i] == paramFDBigInt.data[i])) {
/* 2756 */       i--;
/*      */     }
/*      */ 
/* 2761 */     int j = this.data[i];
/* 2762 */     int k = paramFDBigInt.data[i];
/* 2763 */     if (j < 0)
/*      */     {
/* 2765 */       if (k < 0) {
/* 2766 */         return j - k;
/*      */       }
/* 2768 */       return 1;
/*      */     }
/*      */ 
/* 2772 */     if (k < 0)
/*      */     {
/* 2774 */       return -1;
/*      */     }
/* 2776 */     return j - k;
/*      */   }
/*      */ 
/*      */   public int quoRemIteration(FDBigInt paramFDBigInt)
/*      */     throws IllegalArgumentException
/*      */   {
/* 2797 */     if (this.nWords != paramFDBigInt.nWords) {
/* 2798 */       throw new IllegalArgumentException("disparate values");
/*      */     }
/*      */ 
/* 2803 */     int i = this.nWords - 1;
/* 2804 */     long l1 = (this.data[i] & 0xFFFFFFFF) / paramFDBigInt.data[i];
/* 2805 */     long l2 = 0L;
/* 2806 */     for (int j = 0; j <= i; j++) {
/* 2807 */       l2 += (this.data[j] & 0xFFFFFFFF) - l1 * (paramFDBigInt.data[j] & 0xFFFFFFFF);
/* 2808 */       this.data[j] = ((int)l2);
/* 2809 */       l2 >>= 32;
/*      */     }
/* 2811 */     if (l2 != 0L)
/*      */     {
/* 2815 */       l3 = 0L;
/* 2816 */       while (l3 == 0L) {
/* 2817 */         l3 = 0L;
/* 2818 */         for (k = 0; k <= i; k++) {
/* 2819 */           l3 += (this.data[k] & 0xFFFFFFFF) + (paramFDBigInt.data[k] & 0xFFFFFFFF);
/* 2820 */           this.data[k] = ((int)l3);
/* 2821 */           l3 >>= 32;
/*      */         }
/*      */ 
/* 2832 */         assert ((l3 == 0L) || (l3 == 1L)) : l3;
/* 2833 */         l1 -= 1L;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2839 */     long l3 = 0L;
/* 2840 */     for (int k = 0; k <= i; k++) {
/* 2841 */       l3 += 10L * (this.data[k] & 0xFFFFFFFF);
/* 2842 */       this.data[k] = ((int)l3);
/* 2843 */       l3 >>= 32;
/*      */     }
/* 2845 */     assert (l3 == 0L) : l3;
/* 2846 */     return (int)l1;
/*      */   }
/*      */ 
/*      */   public long longValue()
/*      */   {
/* 2852 */     assert (this.nWords > 0) : this.nWords;
/*      */ 
/* 2854 */     if (this.nWords == 1) {
/* 2855 */       return this.data[0] & 0xFFFFFFFF;
/*      */     }
/* 2857 */     assert (dataInRangeIsZero(2, this.nWords, this));
/* 2858 */     assert (this.data[1] >= 0);
/* 2859 */     return this.data[1] << 32 | this.data[0] & 0xFFFFFFFF;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 2864 */     StringBuffer localStringBuffer = new StringBuffer(30);
/* 2865 */     localStringBuffer.append('[');
/* 2866 */     int i = Math.min(this.nWords - 1, this.data.length - 1);
/* 2867 */     if (this.nWords > this.data.length) {
/* 2868 */       localStringBuffer.append("(" + this.data.length + "<" + this.nWords + "!)");
/*      */     }
/* 2870 */     for (; i > 0; i--) {
/* 2871 */       localStringBuffer.append(Integer.toHexString(this.data[i]));
/* 2872 */       localStringBuffer.append(' ');
/*      */     }
/* 2874 */     localStringBuffer.append(Integer.toHexString(this.data[0]));
/* 2875 */     localStringBuffer.append(']');
/* 2876 */     return new String(localStringBuffer);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.FDBigInt
 * JD-Core Version:    0.6.2
 */