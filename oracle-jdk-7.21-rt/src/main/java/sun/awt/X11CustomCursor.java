/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public abstract class X11CustomCursor extends CustomCursor
/*     */ {
/*     */   public X11CustomCursor(Image paramImage, Point paramPoint, String paramString)
/*     */     throws IndexOutOfBoundsException
/*     */   {
/*  43 */     super(paramImage, paramPoint, paramString);
/*     */   }
/*     */ 
/*     */   protected void createNativeCursor(Image paramImage, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  63 */     int[] arrayOfInt = new int[paramArrayOfInt.length];
/*  64 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/*  65 */       if ((paramArrayOfInt[i] & 0xFF000000) == 0)
/*  66 */         arrayOfInt[i] = -1;
/*     */       else {
/*  68 */         paramArrayOfInt[i] &= 16777215;
/*     */       }
/*     */     }
/*  71 */     Arrays.sort(arrayOfInt);
/*     */ 
/*  73 */     i = 0;
/*  74 */     int j = 16777215;
/*  75 */     Comparable arrayOf1CCount = new 1CCount[paramArrayOfInt.length];
/*     */ 
/*  77 */     int k = 0;
/*  78 */     int m = 0;
/*  79 */     while (k < paramArrayOfInt.length) {
/*  80 */       if (arrayOfInt[k] != -1) {
/*  81 */         arrayOf1CCount[(m++)] = new Comparable()
/*     */         {
/*     */           int color;
/*     */           int count;
/*     */ 
/*     */           public int compareTo(Object paramAnonymousObject)
/*     */           {
/*  59 */             return ((1CCount)paramAnonymousObject).count - this.count;
/*     */           }
/*     */         };
/*  82 */         break;
/*     */       }
/*  84 */       k++;
/*     */     }
/*     */ 
/*  87 */     for (int n = k + 1; n < paramArrayOfInt.length; n++) {
/*  88 */       if (arrayOfInt[n] != arrayOf1CCount[(m - 1)].color) {
/*  89 */         arrayOf1CCount[(m++)] = new Comparable()
/*     */         {
/*     */           int color;
/*     */           int count;
/*     */ 
/*     */           public int compareTo(Object paramAnonymousObject)
/*     */           {
/*  59 */             return ((1CCount)paramAnonymousObject).count - this.count;
/*     */           }
/*     */ 
/*     */         };
/*     */       }
/*     */       else
/*     */       {
/*  91 */         arrayOf1CCount[(m - 1)].count += 1;
/*     */       }
/*     */     }
/*  94 */     Arrays.sort(arrayOf1CCount, 0, m);
/*     */ 
/*  96 */     if (m > 0) i = arrayOf1CCount[0].color;
/*  97 */     n = i >> 16 & 0xFF;
/*  98 */     int i1 = i >> 8 & 0xFF;
/*  99 */     int i2 = i >> 0 & 0xFF;
/*     */ 
/* 101 */     int i3 = 0;
/* 102 */     int i4 = 0;
/* 103 */     int i5 = 0;
/* 104 */     for (int i6 = 1; i6 < m; i6++) {
/* 105 */       i7 = arrayOf1CCount[i6].color >> 16 & 0xFF;
/* 106 */       i8 = arrayOf1CCount[i6].color >> 8 & 0xFF;
/* 107 */       i9 = arrayOf1CCount[i6].color >> 0 & 0xFF;
/* 108 */       i3 += arrayOf1CCount[i6].count * i7;
/* 109 */       i4 += arrayOf1CCount[i6].count * i8;
/* 110 */       i5 += arrayOf1CCount[i6].count * i9;
/*     */     }
/* 112 */     i6 = paramArrayOfInt.length - (m > 0 ? arrayOf1CCount[0].count : 0);
/*     */ 
/* 114 */     if (i6 > 0) {
/* 115 */       i3 = i3 / i6 - n;
/* 116 */       i4 = i4 / i6 - i1;
/* 117 */       i5 = i5 / i6 - i2;
/*     */     }
/* 119 */     i3 = (i3 * i3 + i4 * i4 + i5 * i5) / 2;
/*     */ 
/* 122 */     for (int i7 = 1; i7 < m; i7++) {
/* 123 */       i8 = arrayOf1CCount[i7].color >> 16 & 0xFF;
/* 124 */       i9 = arrayOf1CCount[i7].color >> 8 & 0xFF;
/* 125 */       i10 = arrayOf1CCount[i7].color >> 0 & 0xFF;
/*     */ 
/* 127 */       if ((i8 - n) * (i8 - n) + (i9 - i1) * (i9 - i1) + (i10 - i2) * (i10 - i2) >= i3)
/*     */       {
/* 129 */         j = arrayOf1CCount[i7].color;
/* 130 */         break;
/*     */       }
/*     */     }
/* 133 */     i7 = j >> 16 & 0xFF;
/* 134 */     int i8 = j >> 8 & 0xFF;
/* 135 */     int i9 = j >> 0 & 0xFF;
/*     */ 
/* 144 */     int i10 = (paramInt1 + 7) / 8;
/* 145 */     int i11 = i10 * paramInt2;
/* 146 */     byte[] arrayOfByte1 = new byte[i11];
/* 147 */     byte[] arrayOfByte2 = new byte[i11];
/*     */ 
/* 149 */     for (int i12 = 0; i12 < paramInt1; i12++) {
/* 150 */       int i13 = 1 << i12 % 8;
/* 151 */       for (int i14 = 0; i14 < paramInt2; i14++) {
/* 152 */         int i15 = i14 * paramInt1 + i12;
/* 153 */         int i16 = i14 * i10 + i12 / 8;
/*     */ 
/* 155 */         if ((paramArrayOfInt[i15] & 0xFF000000) != 0)
/*     */         {
/*     */           int tmp700_698 = i16;
/*     */           byte[] tmp700_696 = arrayOfByte2; tmp700_696[tmp700_698] = ((byte)(tmp700_696[tmp700_698] | i13));
/*     */         }
/*     */ 
/* 159 */         int i17 = paramArrayOfInt[i15] >> 16 & 0xFF;
/* 160 */         int i18 = paramArrayOfInt[i15] >> 8 & 0xFF;
/* 161 */         int i19 = paramArrayOfInt[i15] >> 0 & 0xFF;
/* 162 */         if ((i17 - n) * (i17 - n) + (i18 - i1) * (i18 - i1) + (i19 - i2) * (i19 - i2) <= (i17 - i7) * (i17 - i7) + (i18 - i8) * (i18 - i8) + (i19 - i9) * (i19 - i9))
/*     */         {
/*     */           int tmp822_820 = i16;
/*     */           byte[] tmp822_818 = arrayOfByte1; tmp822_818[tmp822_820] = ((byte)(tmp822_818[tmp822_820] | i13));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 170 */     createCursor(arrayOfByte1, arrayOfByte2, 8 * i10, paramInt2, i, j, paramInt3, paramInt4);
/*     */   }
/*     */ 
/*     */   protected abstract void createCursor(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11CustomCursor
 * JD-Core Version:    0.6.2
 */