/*     */ package sun.java2d.pisces;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import sun.java2d.pipe.AATileGenerator;
/*     */ 
/*     */ final class PiscesTileGenerator
/*     */   implements AATileGenerator
/*     */ {
/*     */   public static final int TILE_SIZE = 32;
/*  40 */   private static final Map<Integer, byte[]> alphaMapsCache = new ConcurrentHashMap();
/*     */   PiscesCache cache;
/*     */   int x;
/*     */   int y;
/*     */   final int maxalpha;
/*     */   private final int maxTileAlphaSum;
/*     */   byte[] alphaMap;
/*     */ 
/*     */   public PiscesTileGenerator(Renderer paramRenderer, int paramInt)
/*     */   {
/*  54 */     this.cache = paramRenderer.getCache();
/*  55 */     this.x = this.cache.bboxX0;
/*  56 */     this.y = this.cache.bboxY0;
/*  57 */     this.alphaMap = getAlphaMap(paramInt);
/*  58 */     this.maxalpha = paramInt;
/*  59 */     this.maxTileAlphaSum = (1024 * paramInt);
/*     */   }
/*     */ 
/*     */   private static byte[] buildAlphaMap(int paramInt) {
/*  63 */     byte[] arrayOfByte = new byte[paramInt + 1];
/*  64 */     int i = paramInt >> 2;
/*  65 */     for (int j = 0; j <= paramInt; j++) {
/*  66 */       arrayOfByte[j] = ((byte)((j * 255 + i) / paramInt));
/*     */     }
/*  68 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public static byte[] getAlphaMap(int paramInt) {
/*  72 */     if (!alphaMapsCache.containsKey(Integer.valueOf(paramInt))) {
/*  73 */       alphaMapsCache.put(Integer.valueOf(paramInt), buildAlphaMap(paramInt));
/*     */     }
/*  75 */     return (byte[])alphaMapsCache.get(Integer.valueOf(paramInt));
/*     */   }
/*     */ 
/*     */   public void getBbox(int[] paramArrayOfInt) {
/*  79 */     paramArrayOfInt[0] = this.cache.bboxX0;
/*  80 */     paramArrayOfInt[1] = this.cache.bboxY0;
/*  81 */     paramArrayOfInt[2] = this.cache.bboxX1;
/*  82 */     paramArrayOfInt[3] = this.cache.bboxY1;
/*     */   }
/*     */ 
/*     */   public int getTileWidth()
/*     */   {
/*  91 */     return 32;
/*     */   }
/*     */ 
/*     */   public int getTileHeight()
/*     */   {
/*  99 */     return 32;
/*     */   }
/*     */ 
/*     */   public int getTypicalAlpha()
/*     */   {
/* 113 */     int i = this.cache.alphaSumInTile(this.x, this.y);
/*     */ 
/* 129 */     return i == this.maxTileAlphaSum ? 255 : i == 0 ? 0 : 128;
/*     */   }
/*     */ 
/*     */   public void nextTile()
/*     */   {
/* 139 */     if (this.x += 32 >= this.cache.bboxX1) {
/* 140 */       this.x = this.cache.bboxX0;
/* 141 */       this.y += 32;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 156 */     RuntimeException localRuntimeException1 = this.x;
/* 157 */     RuntimeException localRuntimeException2 = localRuntimeException1 + 32;
/* 158 */     int i = this.y;
/* 159 */     int j = i + 32;
/* 160 */     if (localRuntimeException2 > this.cache.bboxX1) localRuntimeException2 = this.cache.bboxX1;
/* 161 */     if (j > this.cache.bboxY1) j = this.cache.bboxY1;
/* 162 */     i -= this.cache.bboxY0;
/* 163 */     j -= this.cache.bboxY0;
/*     */ 
/* 165 */     int k = paramInt1;
/* 166 */     for (int m = i; m < j; m++) {
/* 167 */       int[] arrayOfInt = this.cache.rowAARLE[m];
/* 168 */       assert (arrayOfInt != null);
/* 169 */       RuntimeException localRuntimeException3 = this.cache.minTouched(m);
/* 170 */       if (localRuntimeException3 > localRuntimeException2) localRuntimeException3 = localRuntimeException2;
/*     */ 
/* 172 */       for (int n = localRuntimeException1; n < localRuntimeException3; n++) {
/* 173 */         paramArrayOfByte[(k++)] = 0;
/*     */       }
/*     */ 
/* 176 */       n = 2;
/* 177 */       while ((localRuntimeException3 < localRuntimeException2) && (n < arrayOfInt[1])) { int i2 = 0;
/* 180 */         assert (arrayOfInt[1] > 2);
/*     */         int i1;
/*     */         try { i1 = this.alphaMap[arrayOfInt[n]];
/* 183 */           i2 = arrayOfInt[(n + 1)];
/* 184 */           if ((!$assertionsDisabled) && (i2 <= 0)) throw new AssertionError();  } catch (RuntimeException localRuntimeException4)
/*     */         {
/* 186 */           System.out.println("maxalpha = " + this.maxalpha);
/* 187 */           System.out.println("tile[" + localRuntimeException1 + ", " + i + " => " + localRuntimeException2 + ", " + j + "]");
/*     */ 
/* 189 */           System.out.println("cx = " + localRuntimeException3 + ", cy = " + m);
/* 190 */           System.out.println("idx = " + k + ", pos = " + n);
/* 191 */           System.out.println("len = " + i2);
/* 192 */           System.out.print(this.cache.toString());
/* 193 */           localRuntimeException4.printStackTrace();
/* 194 */           throw localRuntimeException4;
/*     */         }
/*     */ 
/* 197 */         localRuntimeException4 = localRuntimeException3;
/* 198 */         localRuntimeException3 += i2;
/* 199 */         int i4 = localRuntimeException3;
/*     */         int i3;
/* 200 */         if (localRuntimeException4 < localRuntimeException1) i3 = localRuntimeException1;
/* 201 */         if (i4 > localRuntimeException2) i4 = localRuntimeException2;
/* 202 */         i2 = i4 - i3;
/*     */         while (true) {
/* 204 */           i2--; if (i2 >= 0)
/*     */             try {
/* 206 */               paramArrayOfByte[(k++)] = i1;
/*     */             } catch (RuntimeException localRuntimeException5) {
/* 208 */               System.out.println("maxalpha = " + this.maxalpha);
/* 209 */               System.out.println("tile[" + localRuntimeException1 + ", " + i + " => " + localRuntimeException2 + ", " + j + "]");
/*     */ 
/* 211 */               System.out.println("cx = " + localRuntimeException3 + ", cy = " + m);
/* 212 */               System.out.println("idx = " + k + ", pos = " + n);
/* 213 */               System.out.println("rx0 = " + i3 + ", rx1 = " + i4);
/* 214 */               System.out.println("len = " + i2);
/* 215 */               System.out.print(this.cache.toString());
/* 216 */               localRuntimeException5.printStackTrace();
/* 217 */               throw localRuntimeException5;
/*     */             }
/*     */         }
/* 220 */         n += 2;
/*     */       }
/* 222 */       if (localRuntimeException3 < localRuntimeException1) localRuntimeException3 = localRuntimeException1;
/* 223 */       while (localRuntimeException3 < localRuntimeException2) {
/* 224 */         paramArrayOfByte[(k++)] = 0;
/* 225 */         localRuntimeException3++;
/*     */       }
/*     */ 
/* 233 */       k += paramInt2 - (localRuntimeException2 - localRuntimeException1);
/*     */     }
/* 235 */     nextTile();
/*     */   }
/*     */ 
/*     */   static String hex(int paramInt1, int paramInt2) {
/* 239 */     String str = Integer.toHexString(paramInt1);
/* 240 */     while (str.length() < paramInt2) {
/* 241 */       str = "0" + str;
/*     */     }
/* 243 */     return str.substring(0, paramInt2);
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pisces.PiscesTileGenerator
 * JD-Core Version:    0.6.2
 */