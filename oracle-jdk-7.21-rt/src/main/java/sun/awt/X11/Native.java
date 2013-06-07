/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Vector;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ class Native
/*     */ {
/*  40 */   private static Unsafe unsafe = XlibWrapper.unsafe;
/*     */   static int longSize;
/*     */   static int dataModel;
/*     */ 
/*     */   static boolean getBool(long paramLong)
/*     */   {
/*  73 */     return getInt(paramLong) != 0; } 
/*  74 */   static boolean getBool(long paramLong, int paramInt) { return getInt(paramLong, paramInt) != 0; } 
/*  75 */   static void putBool(long paramLong, boolean paramBoolean) { putInt(paramLong, paramBoolean ? 1 : 0); } 
/*  76 */   static void putBool(long paramLong, int paramInt, boolean paramBoolean) { putInt(paramLong, paramInt, paramBoolean ? 1 : 0); }
/*     */ 
/*     */ 
/*     */   static int getByteSize()
/*     */   {
/*  82 */     return 1; } 
/*  83 */   static byte getByte(long paramLong) { return unsafe.getByte(paramLong); }
/*     */ 
/*     */   static byte getByte(long paramLong, int paramInt) {
/*  86 */     return getByte(paramLong + paramInt);
/*     */   }
/*     */ 
/*     */   static void putByte(long paramLong, byte paramByte)
/*     */   {
/*  91 */     unsafe.putByte(paramLong, paramByte);
/*     */   }
/*     */   static void putByte(long paramLong, int paramInt, byte paramByte) {
/*  94 */     putByte(paramLong + paramInt, paramByte);
/*     */   }
/*     */ 
/*     */   static byte[] toBytes(long paramLong, int paramInt)
/*     */   {
/* 103 */     if (paramLong == 0L) {
/* 104 */       return null;
/*     */     }
/* 106 */     byte[] arrayOfByte = new byte[paramInt];
/* 107 */     for (int i = 0; i < paramInt; paramLong += 1L) {
/* 108 */       arrayOfByte[i] = getByte(paramLong);
/*     */ 
/* 107 */       i++;
/*     */     }
/*     */ 
/* 110 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   static long toData(byte[] paramArrayOfByte)
/*     */   {
/* 117 */     if (paramArrayOfByte == null) {
/* 118 */       return 0L;
/*     */     }
/* 120 */     long l = XlibWrapper.unsafe.allocateMemory(paramArrayOfByte.length);
/* 121 */     for (int i = 0; i < paramArrayOfByte.length; i++) {
/* 122 */       putByte(l + i, paramArrayOfByte[i]);
/*     */     }
/* 124 */     return l;
/*     */   }
/*     */ 
/*     */   static int getUByteSize()
/*     */   {
/* 130 */     return 1; } 
/* 131 */   static short getUByte(long paramLong) { return (short)(0xFF & unsafe.getByte(paramLong)); }
/*     */ 
/*     */   static short getUByte(long paramLong, int paramInt) {
/* 134 */     return getUByte(paramLong + paramInt);
/*     */   }
/*     */ 
/*     */   static void putUByte(long paramLong, short paramShort)
/*     */   {
/* 140 */     unsafe.putByte(paramLong, (byte)paramShort);
/*     */   }
/*     */   static void putUByte(long paramLong, int paramInt, short paramShort) {
/* 143 */     putUByte(paramLong + paramInt, paramShort);
/*     */   }
/*     */ 
/*     */   static short[] toUBytes(long paramLong, int paramInt)
/*     */   {
/* 154 */     if (paramLong == 0L) {
/* 155 */       return null;
/*     */     }
/* 157 */     short[] arrayOfShort = new short[paramInt];
/* 158 */     for (int i = 0; i < paramInt; paramLong += 1L) {
/* 159 */       arrayOfShort[i] = getUByte(paramLong);
/*     */ 
/* 158 */       i++;
/*     */     }
/*     */ 
/* 161 */     return arrayOfShort;
/*     */   }
/*     */ 
/*     */   static long toUData(short[] paramArrayOfShort)
/*     */   {
/* 169 */     if (paramArrayOfShort == null) {
/* 170 */       return 0L;
/*     */     }
/* 172 */     long l = XlibWrapper.unsafe.allocateMemory(paramArrayOfShort.length);
/* 173 */     for (int i = 0; i < paramArrayOfShort.length; i++) {
/* 174 */       putUByte(l + i, paramArrayOfShort[i]);
/*     */     }
/* 176 */     return l;
/*     */   }
/*     */ 
/*     */   static int getShortSize()
/*     */   {
/* 182 */     return 2; } 
/* 183 */   static short getShort(long paramLong) { return unsafe.getShort(paramLong); }
/*     */ 
/*     */   static void putShort(long paramLong, short paramShort)
/*     */   {
/* 187 */     unsafe.putShort(paramLong, paramShort);
/*     */   }
/* 189 */   static void putShort(long paramLong, int paramInt, short paramShort) { putShort(paramLong + paramInt * getShortSize(), paramShort); }
/*     */ 
/*     */   static long toData(short[] paramArrayOfShort) {
/* 192 */     if (paramArrayOfShort == null) {
/* 193 */       return 0L;
/*     */     }
/* 195 */     long l = XlibWrapper.unsafe.allocateMemory(paramArrayOfShort.length * getShortSize());
/* 196 */     for (int i = 0; i < paramArrayOfShort.length; i++) {
/* 197 */       putShort(l, i, paramArrayOfShort[i]);
/*     */     }
/* 199 */     return l;
/*     */   }
/*     */ 
/*     */   static int getUShortSize()
/*     */   {
/* 205 */     return 2;
/*     */   }
/* 207 */   static int getUShort(long paramLong) { return 0xFFFF & unsafe.getShort(paramLong); }
/*     */ 
/*     */   static void putUShort(long paramLong, int paramInt)
/*     */   {
/* 211 */     unsafe.putShort(paramLong, (short)paramInt);
/*     */   }
/* 213 */   static void putUShort(long paramLong, int paramInt1, int paramInt2) { putUShort(paramLong + paramInt1 * getShortSize(), paramInt2); }
/*     */ 
/*     */ 
/*     */   static long toUData(int[] paramArrayOfInt)
/*     */   {
/* 222 */     if (paramArrayOfInt == null) {
/* 223 */       return 0L;
/*     */     }
/* 225 */     long l = XlibWrapper.unsafe.allocateMemory(paramArrayOfInt.length * getShortSize());
/* 226 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/* 227 */       putUShort(l, i, paramArrayOfInt[i]);
/*     */     }
/* 229 */     return l;
/*     */   }
/*     */ 
/*     */   static int getIntSize()
/*     */   {
/* 235 */     return 4; } 
/* 236 */   static int getInt(long paramLong) { return unsafe.getInt(paramLong); } 
/* 237 */   static int getInt(long paramLong, int paramInt) { return getInt(paramLong + getIntSize() * paramInt); }
/*     */ 
/*     */   static void putInt(long paramLong, int paramInt)
/*     */   {
/* 241 */     unsafe.putInt(paramLong, paramInt);
/*     */   }
/* 243 */   static void putInt(long paramLong, int paramInt1, int paramInt2) { putInt(paramLong + paramInt1 * getIntSize(), paramInt2); }
/*     */ 
/*     */   static long toData(int[] paramArrayOfInt) {
/* 246 */     if (paramArrayOfInt == null) {
/* 247 */       return 0L;
/*     */     }
/* 249 */     long l = XlibWrapper.unsafe.allocateMemory(paramArrayOfInt.length * getIntSize());
/* 250 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/* 251 */       putInt(l, i, paramArrayOfInt[i]);
/*     */     }
/* 253 */     return l;
/*     */   }
/*     */ 
/*     */   static int getUIntSize()
/*     */   {
/* 259 */     return 4; } 
/* 260 */   static long getUInt(long paramLong) { return 0xFFFFFFFF & unsafe.getInt(paramLong); } 
/* 261 */   static long getUInt(long paramLong, int paramInt) { return getUInt(paramLong + getIntSize() * paramInt); }
/*     */ 
/*     */   static void putUInt(long paramLong1, long paramLong2)
/*     */   {
/* 265 */     unsafe.putInt(paramLong1, (int)paramLong2);
/*     */   }
/* 267 */   static void putUInt(long paramLong1, int paramInt, long paramLong2) { putUInt(paramLong1 + paramInt * getIntSize(), paramLong2); }
/*     */ 
/*     */ 
/*     */   static long toUData(long[] paramArrayOfLong)
/*     */   {
/* 276 */     if (paramArrayOfLong == null) {
/* 277 */       return 0L;
/*     */     }
/* 279 */     long l = XlibWrapper.unsafe.allocateMemory(paramArrayOfLong.length * getIntSize());
/* 280 */     for (int i = 0; i < paramArrayOfLong.length; i++) {
/* 281 */       putUInt(l, i, paramArrayOfLong[i]);
/*     */     }
/* 283 */     return l;
/*     */   }
/*     */ 
/*     */   static int getLongSize()
/*     */   {
/* 290 */     return longSize;
/*     */   }
/*     */   static long getLong(long paramLong) {
/* 293 */     if (XlibWrapper.dataModel == 32) {
/* 294 */       return unsafe.getInt(paramLong);
/*     */     }
/* 296 */     return unsafe.getLong(paramLong);
/*     */   }
/*     */ 
/*     */   static void putLong(long paramLong1, long paramLong2)
/*     */   {
/* 305 */     if (XlibWrapper.dataModel == 32)
/* 306 */       unsafe.putInt(paramLong1, (int)paramLong2);
/*     */     else
/* 308 */       unsafe.putLong(paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   static void putLong(long paramLong1, int paramInt, long paramLong2)
/*     */   {
/* 313 */     putLong(paramLong1 + paramInt * getLongSize(), paramLong2);
/*     */   }
/*     */ 
/*     */   static long getLong(long paramLong, int paramInt)
/*     */   {
/* 320 */     return getLong(paramLong + paramInt * getLongSize());
/*     */   }
/*     */ 
/*     */   static void put(long paramLong, long[] paramArrayOfLong)
/*     */   {
/* 327 */     for (int i = 0; i < paramArrayOfLong.length; paramLong += getLongSize()) {
/* 328 */       putLong(paramLong, paramArrayOfLong[i]);
/*     */ 
/* 327 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   static void putLong(long paramLong, Vector paramVector)
/*     */   {
/* 337 */     for (int i = 0; i < paramVector.size(); paramLong += getLongSize()) {
/* 338 */       putLong(paramLong, ((Long)paramVector.elementAt(i)).longValue());
/*     */ 
/* 337 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   static void putLongReverse(long paramLong, Vector paramVector)
/*     */   {
/* 347 */     for (int i = paramVector.size() - 1; i >= 0; paramLong += getLongSize()) {
/* 348 */       putLong(paramLong, ((Long)paramVector.elementAt(i)).longValue());
/*     */ 
/* 347 */       i--;
/*     */     }
/*     */   }
/*     */ 
/*     */   static long[] toLongs(long paramLong, int paramInt)
/*     */   {
/* 358 */     if (paramLong == 0L) {
/* 359 */       return null;
/*     */     }
/* 361 */     long[] arrayOfLong = new long[paramInt];
/* 362 */     for (int i = 0; i < paramInt; paramLong += getLongSize()) {
/* 363 */       arrayOfLong[i] = getLong(paramLong);
/*     */ 
/* 362 */       i++;
/*     */     }
/*     */ 
/* 365 */     return arrayOfLong;
/*     */   }
/*     */   static long toData(long[] paramArrayOfLong) {
/* 368 */     if (paramArrayOfLong == null) {
/* 369 */       return 0L;
/*     */     }
/* 371 */     long l = XlibWrapper.unsafe.allocateMemory(paramArrayOfLong.length * getLongSize());
/* 372 */     for (int i = 0; i < paramArrayOfLong.length; i++) {
/* 373 */       putLong(l, i, paramArrayOfLong[i]);
/*     */     }
/* 375 */     return l;
/*     */   }
/*     */ 
/*     */   static long getULong(long paramLong)
/*     */   {
/* 383 */     if (XlibWrapper.dataModel == 32)
/*     */     {
/* 385 */       return unsafe.getInt(paramLong) & 0xFFFFFFFF;
/*     */     }
/*     */ 
/* 388 */     return unsafe.getLong(paramLong);
/*     */   }
/*     */ 
/*     */   static void putULong(long paramLong1, long paramLong2)
/*     */   {
/* 393 */     putLong(paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   static long allocateLongArray(int paramInt)
/*     */   {
/* 400 */     return unsafe.allocateMemory(getLongSize() * paramInt);
/*     */   }
/*     */ 
/*     */   static long getWindow(long paramLong)
/*     */   {
/* 405 */     return getLong(paramLong);
/*     */   }
/*     */   static long getWindow(long paramLong, int paramInt) {
/* 408 */     return getLong(paramLong + getWindowSize() * paramInt);
/*     */   }
/*     */ 
/*     */   static void putWindow(long paramLong1, long paramLong2) {
/* 412 */     putLong(paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   static void putWindow(long paramLong1, int paramInt, long paramLong2) {
/* 416 */     putLong(paramLong1, paramInt, paramLong2);
/*     */   }
/*     */ 
/*     */   static int getWindowSize()
/*     */   {
/* 424 */     return getLongSize();
/*     */   }
/*     */ 
/*     */   static long getCard32(long paramLong)
/*     */   {
/* 435 */     return getLong(paramLong);
/*     */   }
/*     */   static void putCard32(long paramLong1, long paramLong2) {
/* 438 */     putLong(paramLong1, paramLong2);
/*     */   }
/*     */   static long getCard32(long paramLong, int paramInt) {
/* 441 */     return getLong(paramLong, paramInt);
/*     */   }
/*     */   static void putCard32(long paramLong1, int paramInt, long paramLong2) {
/* 444 */     putLong(paramLong1, paramInt, paramLong2);
/*     */   }
/*     */   static int getCard32Size() {
/* 447 */     return getLongSize();
/*     */   }
/*     */   static long[] card32ToArray(long paramLong, int paramInt) {
/* 450 */     return toLongs(paramLong, paramInt);
/*     */   }
/*     */   static long card32ToData(long[] paramArrayOfLong) {
/* 453 */     return toData(paramArrayOfLong);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*  50 */         return System.getProperty("sun.arch.data.model");
/*     */       }
/*     */     });
/*     */     try {
/*  54 */       dataModel = Integer.parseInt(str);
/*     */     } catch (Exception localException) {
/*  56 */       dataModel = 32;
/*     */     }
/*  58 */     if (dataModel == 32)
/*  59 */       longSize = 4;
/*     */     else
/*  61 */       longSize = 8;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.Native
 * JD-Core Version:    0.6.2
 */