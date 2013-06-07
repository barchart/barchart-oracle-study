/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import java.security.AccessController;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ final class ByteArrayAccess
/*     */ {
/*  64 */   private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */ 
/*  85 */   private static final boolean littleEndianUnaligned = (i != 0) && (unaligned()) && (localByteOrder == ByteOrder.LITTLE_ENDIAN);
/*     */ 
/*  87 */   private static final boolean bigEndian = (i != 0) && (localByteOrder == ByteOrder.BIG_ENDIAN);
/*     */ 
/*  76 */   private static final int byteArrayOfs = unsafe.arrayBaseOffset([B.class);
/*     */ 
/*     */   private static boolean unaligned()
/*     */   {
/*  96 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.arch", ""));
/*     */ 
/*  98 */     return (str.equals("i386")) || (str.equals("x86")) || (str.equals("amd64")) || (str.equals("x86_64"));
/*     */   }
/*     */ 
/*     */   static void b2iLittle(byte[] paramArrayOfByte, int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3)
/*     */   {
/* 106 */     if (littleEndianUnaligned) {
/* 107 */       paramInt1 += byteArrayOfs;
/* 108 */       paramInt3 += paramInt1;
/* 109 */       while (paramInt1 < paramInt3) {
/* 110 */         paramArrayOfInt[(paramInt2++)] = unsafe.getInt(paramArrayOfByte, paramInt1);
/* 111 */         paramInt1 += 4;
/*     */       }
/*     */     }
/* 113 */     if ((bigEndian) && ((paramInt1 & 0x3) == 0)) {
/* 114 */       paramInt1 += byteArrayOfs;
/* 115 */       paramInt3 += paramInt1;
/* 116 */     }while (paramInt1 < paramInt3) {
/* 117 */       paramArrayOfInt[(paramInt2++)] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt1));
/* 118 */       paramInt1 += 4; continue;
/*     */ 
/* 121 */       paramInt3 += paramInt1;
/* 122 */       while (paramInt1 < paramInt3) {
/* 123 */         paramArrayOfInt[(paramInt2++)] = (paramArrayOfByte[paramInt1] & 0xFF | (paramArrayOfByte[(paramInt1 + 1)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt1 + 2)] & 0xFF) << 16 | paramArrayOfByte[(paramInt1 + 3)] << 24);
/*     */ 
/* 127 */         paramInt1 += 4;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void b2iLittle64(byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
/*     */   {
/* 134 */     if (littleEndianUnaligned) {
/* 135 */       paramInt += byteArrayOfs;
/* 136 */       paramArrayOfInt[0] = unsafe.getInt(paramArrayOfByte, paramInt);
/* 137 */       paramArrayOfInt[1] = unsafe.getInt(paramArrayOfByte, paramInt + 4);
/* 138 */       paramArrayOfInt[2] = unsafe.getInt(paramArrayOfByte, paramInt + 8);
/* 139 */       paramArrayOfInt[3] = unsafe.getInt(paramArrayOfByte, paramInt + 12);
/* 140 */       paramArrayOfInt[4] = unsafe.getInt(paramArrayOfByte, paramInt + 16);
/* 141 */       paramArrayOfInt[5] = unsafe.getInt(paramArrayOfByte, paramInt + 20);
/* 142 */       paramArrayOfInt[6] = unsafe.getInt(paramArrayOfByte, paramInt + 24);
/* 143 */       paramArrayOfInt[7] = unsafe.getInt(paramArrayOfByte, paramInt + 28);
/* 144 */       paramArrayOfInt[8] = unsafe.getInt(paramArrayOfByte, paramInt + 32);
/* 145 */       paramArrayOfInt[9] = unsafe.getInt(paramArrayOfByte, paramInt + 36);
/* 146 */       paramArrayOfInt[10] = unsafe.getInt(paramArrayOfByte, paramInt + 40);
/* 147 */       paramArrayOfInt[11] = unsafe.getInt(paramArrayOfByte, paramInt + 44);
/* 148 */       paramArrayOfInt[12] = unsafe.getInt(paramArrayOfByte, paramInt + 48);
/* 149 */       paramArrayOfInt[13] = unsafe.getInt(paramArrayOfByte, paramInt + 52);
/* 150 */       paramArrayOfInt[14] = unsafe.getInt(paramArrayOfByte, paramInt + 56);
/* 151 */       paramArrayOfInt[15] = unsafe.getInt(paramArrayOfByte, paramInt + 60);
/* 152 */     } else if ((bigEndian) && ((paramInt & 0x3) == 0)) {
/* 153 */       paramInt += byteArrayOfs;
/* 154 */       paramArrayOfInt[0] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt));
/* 155 */       paramArrayOfInt[1] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 4));
/* 156 */       paramArrayOfInt[2] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 8));
/* 157 */       paramArrayOfInt[3] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 12));
/* 158 */       paramArrayOfInt[4] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 16));
/* 159 */       paramArrayOfInt[5] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 20));
/* 160 */       paramArrayOfInt[6] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 24));
/* 161 */       paramArrayOfInt[7] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 28));
/* 162 */       paramArrayOfInt[8] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 32));
/* 163 */       paramArrayOfInt[9] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 36));
/* 164 */       paramArrayOfInt[10] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 40));
/* 165 */       paramArrayOfInt[11] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 44));
/* 166 */       paramArrayOfInt[12] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 48));
/* 167 */       paramArrayOfInt[13] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 52));
/* 168 */       paramArrayOfInt[14] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 56));
/* 169 */       paramArrayOfInt[15] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 60));
/*     */     } else {
/* 171 */       b2iLittle(paramArrayOfByte, paramInt, paramArrayOfInt, 0, 64);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void i2bLittle(int[] paramArrayOfInt, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */   {
/* 179 */     if (littleEndianUnaligned) {
/* 180 */       paramInt2 += byteArrayOfs;
/* 181 */       paramInt3 += paramInt2;
/* 182 */       while (paramInt2 < paramInt3) {
/* 183 */         unsafe.putInt(paramArrayOfByte, paramInt2, paramArrayOfInt[(paramInt1++)]);
/* 184 */         paramInt2 += 4;
/*     */       }
/*     */     }
/* 186 */     if ((bigEndian) && ((paramInt2 & 0x3) == 0)) {
/* 187 */       paramInt2 += byteArrayOfs;
/* 188 */       paramInt3 += paramInt2;
/* 189 */     }while (paramInt2 < paramInt3) {
/* 190 */       unsafe.putInt(paramArrayOfByte, paramInt2, Integer.reverseBytes(paramArrayOfInt[(paramInt1++)]));
/* 191 */       paramInt2 += 4; continue;
/*     */ 
/* 194 */       paramInt3 += paramInt2;
/* 195 */       while (paramInt2 < paramInt3) {
/* 196 */         int i = paramArrayOfInt[(paramInt1++)];
/* 197 */         paramArrayOfByte[(paramInt2++)] = ((byte)i);
/* 198 */         paramArrayOfByte[(paramInt2++)] = ((byte)(i >> 8));
/* 199 */         paramArrayOfByte[(paramInt2++)] = ((byte)(i >> 16));
/* 200 */         paramArrayOfByte[(paramInt2++)] = ((byte)(i >> 24));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void i2bLittle4(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */   {
/* 207 */     if (littleEndianUnaligned) {
/* 208 */       unsafe.putInt(paramArrayOfByte, byteArrayOfs + paramInt2, paramInt1);
/* 209 */     } else if ((bigEndian) && ((paramInt2 & 0x3) == 0)) {
/* 210 */       unsafe.putInt(paramArrayOfByte, byteArrayOfs + paramInt2, Integer.reverseBytes(paramInt1));
/*     */     } else {
/* 212 */       paramArrayOfByte[paramInt2] = ((byte)paramInt1);
/* 213 */       paramArrayOfByte[(paramInt2 + 1)] = ((byte)(paramInt1 >> 8));
/* 214 */       paramArrayOfByte[(paramInt2 + 2)] = ((byte)(paramInt1 >> 16));
/* 215 */       paramArrayOfByte[(paramInt2 + 3)] = ((byte)(paramInt1 >> 24));
/*     */     }
/*     */   }
/*     */ 
/*     */   static void b2iBig(byte[] paramArrayOfByte, int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3)
/*     */   {
/* 223 */     if (littleEndianUnaligned) {
/* 224 */       paramInt1 += byteArrayOfs;
/* 225 */       paramInt3 += paramInt1;
/* 226 */       while (paramInt1 < paramInt3) {
/* 227 */         paramArrayOfInt[(paramInt2++)] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt1));
/* 228 */         paramInt1 += 4;
/*     */       }
/*     */     }
/* 230 */     if ((bigEndian) && ((paramInt1 & 0x3) == 0)) {
/* 231 */       paramInt1 += byteArrayOfs;
/* 232 */       paramInt3 += paramInt1;
/* 233 */     }while (paramInt1 < paramInt3) {
/* 234 */       paramArrayOfInt[(paramInt2++)] = unsafe.getInt(paramArrayOfByte, paramInt1);
/* 235 */       paramInt1 += 4; continue;
/*     */ 
/* 238 */       paramInt3 += paramInt1;
/* 239 */       while (paramInt1 < paramInt3) {
/* 240 */         paramArrayOfInt[(paramInt2++)] = (paramArrayOfByte[(paramInt1 + 3)] & 0xFF | (paramArrayOfByte[(paramInt1 + 2)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt1 + 1)] & 0xFF) << 16 | paramArrayOfByte[paramInt1] << 24);
/*     */ 
/* 244 */         paramInt1 += 4;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void b2iBig64(byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
/*     */   {
/* 251 */     if (littleEndianUnaligned) {
/* 252 */       paramInt += byteArrayOfs;
/* 253 */       paramArrayOfInt[0] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt));
/* 254 */       paramArrayOfInt[1] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 4));
/* 255 */       paramArrayOfInt[2] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 8));
/* 256 */       paramArrayOfInt[3] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 12));
/* 257 */       paramArrayOfInt[4] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 16));
/* 258 */       paramArrayOfInt[5] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 20));
/* 259 */       paramArrayOfInt[6] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 24));
/* 260 */       paramArrayOfInt[7] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 28));
/* 261 */       paramArrayOfInt[8] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 32));
/* 262 */       paramArrayOfInt[9] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 36));
/* 263 */       paramArrayOfInt[10] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 40));
/* 264 */       paramArrayOfInt[11] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 44));
/* 265 */       paramArrayOfInt[12] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 48));
/* 266 */       paramArrayOfInt[13] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 52));
/* 267 */       paramArrayOfInt[14] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 56));
/* 268 */       paramArrayOfInt[15] = Integer.reverseBytes(unsafe.getInt(paramArrayOfByte, paramInt + 60));
/* 269 */     } else if ((bigEndian) && ((paramInt & 0x3) == 0)) {
/* 270 */       paramInt += byteArrayOfs;
/* 271 */       paramArrayOfInt[0] = unsafe.getInt(paramArrayOfByte, paramInt);
/* 272 */       paramArrayOfInt[1] = unsafe.getInt(paramArrayOfByte, paramInt + 4);
/* 273 */       paramArrayOfInt[2] = unsafe.getInt(paramArrayOfByte, paramInt + 8);
/* 274 */       paramArrayOfInt[3] = unsafe.getInt(paramArrayOfByte, paramInt + 12);
/* 275 */       paramArrayOfInt[4] = unsafe.getInt(paramArrayOfByte, paramInt + 16);
/* 276 */       paramArrayOfInt[5] = unsafe.getInt(paramArrayOfByte, paramInt + 20);
/* 277 */       paramArrayOfInt[6] = unsafe.getInt(paramArrayOfByte, paramInt + 24);
/* 278 */       paramArrayOfInt[7] = unsafe.getInt(paramArrayOfByte, paramInt + 28);
/* 279 */       paramArrayOfInt[8] = unsafe.getInt(paramArrayOfByte, paramInt + 32);
/* 280 */       paramArrayOfInt[9] = unsafe.getInt(paramArrayOfByte, paramInt + 36);
/* 281 */       paramArrayOfInt[10] = unsafe.getInt(paramArrayOfByte, paramInt + 40);
/* 282 */       paramArrayOfInt[11] = unsafe.getInt(paramArrayOfByte, paramInt + 44);
/* 283 */       paramArrayOfInt[12] = unsafe.getInt(paramArrayOfByte, paramInt + 48);
/* 284 */       paramArrayOfInt[13] = unsafe.getInt(paramArrayOfByte, paramInt + 52);
/* 285 */       paramArrayOfInt[14] = unsafe.getInt(paramArrayOfByte, paramInt + 56);
/* 286 */       paramArrayOfInt[15] = unsafe.getInt(paramArrayOfByte, paramInt + 60);
/*     */     } else {
/* 288 */       b2iBig(paramArrayOfByte, paramInt, paramArrayOfInt, 0, 64);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void i2bBig(int[] paramArrayOfInt, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */   {
/* 296 */     if (littleEndianUnaligned) {
/* 297 */       paramInt2 += byteArrayOfs;
/* 298 */       paramInt3 += paramInt2;
/* 299 */       while (paramInt2 < paramInt3) {
/* 300 */         unsafe.putInt(paramArrayOfByte, paramInt2, Integer.reverseBytes(paramArrayOfInt[(paramInt1++)]));
/* 301 */         paramInt2 += 4;
/*     */       }
/*     */     }
/* 303 */     if ((bigEndian) && ((paramInt2 & 0x3) == 0)) {
/* 304 */       paramInt2 += byteArrayOfs;
/* 305 */       paramInt3 += paramInt2;
/* 306 */     }while (paramInt2 < paramInt3) {
/* 307 */       unsafe.putInt(paramArrayOfByte, paramInt2, paramArrayOfInt[(paramInt1++)]);
/* 308 */       paramInt2 += 4; continue;
/*     */ 
/* 311 */       paramInt3 += paramInt2;
/* 312 */       while (paramInt2 < paramInt3) {
/* 313 */         int i = paramArrayOfInt[(paramInt1++)];
/* 314 */         paramArrayOfByte[(paramInt2++)] = ((byte)(i >> 24));
/* 315 */         paramArrayOfByte[(paramInt2++)] = ((byte)(i >> 16));
/* 316 */         paramArrayOfByte[(paramInt2++)] = ((byte)(i >> 8));
/* 317 */         paramArrayOfByte[(paramInt2++)] = ((byte)i);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void i2bBig4(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */   {
/* 324 */     if (littleEndianUnaligned) {
/* 325 */       unsafe.putInt(paramArrayOfByte, byteArrayOfs + paramInt2, Integer.reverseBytes(paramInt1));
/* 326 */     } else if ((bigEndian) && ((paramInt2 & 0x3) == 0)) {
/* 327 */       unsafe.putInt(paramArrayOfByte, byteArrayOfs + paramInt2, paramInt1);
/*     */     } else {
/* 329 */       paramArrayOfByte[paramInt2] = ((byte)(paramInt1 >> 24));
/* 330 */       paramArrayOfByte[(paramInt2 + 1)] = ((byte)(paramInt1 >> 16));
/* 331 */       paramArrayOfByte[(paramInt2 + 2)] = ((byte)(paramInt1 >> 8));
/* 332 */       paramArrayOfByte[(paramInt2 + 3)] = ((byte)paramInt1);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void b2lBig(byte[] paramArrayOfByte, int paramInt1, long[] paramArrayOfLong, int paramInt2, int paramInt3)
/*     */   {
/* 340 */     if (littleEndianUnaligned) {
/* 341 */       paramInt1 += byteArrayOfs;
/* 342 */       paramInt3 += paramInt1;
/* 343 */       while (paramInt1 < paramInt3) {
/* 344 */         paramArrayOfLong[(paramInt2++)] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt1));
/* 345 */         paramInt1 += 8;
/*     */       }
/*     */     }
/* 347 */     if ((bigEndian) && ((paramInt1 & 0x3) == 0))
/*     */     {
/* 353 */       paramInt1 += byteArrayOfs;
/* 354 */       paramInt3 += paramInt1;
/* 355 */     }while (paramInt1 < paramInt3) {
/* 356 */       paramArrayOfLong[(paramInt2++)] = (unsafe.getInt(paramArrayOfByte, paramInt1) << 32 | unsafe.getInt(paramArrayOfByte, paramInt1 + 4) & 0xFFFFFFFF);
/*     */ 
/* 359 */       paramInt1 += 8; continue;
/*     */ 
/* 362 */       paramInt3 += paramInt1;
/* 363 */       while (paramInt1 < paramInt3) {
/* 364 */         int i = paramArrayOfByte[(paramInt1 + 3)] & 0xFF | (paramArrayOfByte[(paramInt1 + 2)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt1 + 1)] & 0xFF) << 16 | paramArrayOfByte[paramInt1] << 24;
/*     */ 
/* 368 */         paramInt1 += 4;
/* 369 */         int j = paramArrayOfByte[(paramInt1 + 3)] & 0xFF | (paramArrayOfByte[(paramInt1 + 2)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt1 + 1)] & 0xFF) << 16 | paramArrayOfByte[paramInt1] << 24;
/*     */ 
/* 373 */         paramArrayOfLong[(paramInt2++)] = (i << 32 | j & 0xFFFFFFFF);
/* 374 */         paramInt1 += 4;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void b2lBig128(byte[] paramArrayOfByte, int paramInt, long[] paramArrayOfLong)
/*     */   {
/* 381 */     if (littleEndianUnaligned) {
/* 382 */       paramInt += byteArrayOfs;
/* 383 */       paramArrayOfLong[0] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt));
/* 384 */       paramArrayOfLong[1] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 8));
/* 385 */       paramArrayOfLong[2] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 16));
/* 386 */       paramArrayOfLong[3] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 24));
/* 387 */       paramArrayOfLong[4] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 32));
/* 388 */       paramArrayOfLong[5] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 40));
/* 389 */       paramArrayOfLong[6] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 48));
/* 390 */       paramArrayOfLong[7] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 56));
/* 391 */       paramArrayOfLong[8] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 64));
/* 392 */       paramArrayOfLong[9] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 72));
/* 393 */       paramArrayOfLong[10] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 80));
/* 394 */       paramArrayOfLong[11] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 88));
/* 395 */       paramArrayOfLong[12] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 96));
/* 396 */       paramArrayOfLong[13] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 104));
/* 397 */       paramArrayOfLong[14] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 112));
/* 398 */       paramArrayOfLong[15] = Long.reverseBytes(unsafe.getLong(paramArrayOfByte, paramInt + 120));
/*     */     }
/*     */     else {
/* 401 */       b2lBig(paramArrayOfByte, paramInt, paramArrayOfLong, 0, 128);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void l2bBig(long[] paramArrayOfLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */   {
/* 409 */     paramInt3 += paramInt2;
/* 410 */     while (paramInt2 < paramInt3) {
/* 411 */       long l = paramArrayOfLong[(paramInt1++)];
/* 412 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)(l >> 56));
/* 413 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)(l >> 48));
/* 414 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)(l >> 40));
/* 415 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)(l >> 32));
/* 416 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)(l >> 24));
/* 417 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)(l >> 16));
/* 418 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)(l >> 8));
/* 419 */       paramArrayOfByte[(paramInt2++)] = ((byte)(int)l);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  79 */     int i = (unsafe.arrayIndexScale([B.class) == 1) && (unsafe.arrayIndexScale([I.class) == 4) && (unsafe.arrayIndexScale([J.class) == 8) && ((byteArrayOfs & 0x3) == 0) ? 1 : 0;
/*     */ 
/*  84 */     ByteOrder localByteOrder = ByteOrder.nativeOrder();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.ByteArrayAccess
 * JD-Core Version:    0.6.2
 */