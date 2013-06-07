/*     */ package java.lang.invoke;
/*     */ 
/*     */ import sun.invoke.util.Wrapper;
/*     */ 
/*     */ class MethodTypeForm
/*     */ {
/*     */   final int[] argToSlotTable;
/*     */   final int[] slotToArgTable;
/*     */   final long argCounts;
/*     */   final long primCounts;
/*     */   final int vmslots;
/*     */   private Object vmlayout;
/*     */   final MethodType erasedType;
/*     */   MethodType primsAsBoxes;
/*     */   MethodType primArgsAsBoxes;
/*     */   MethodType primsAsInts;
/*     */   MethodType primsAsLongs;
/*     */   MethodType primsAtEnd;
/*     */   ToGeneric toGeneric;
/*     */   FromGeneric fromGeneric;
/*     */   SpreadGeneric[] spreadGeneric;
/*     */   FilterGeneric filterGeneric;
/*     */   MethodHandle genericInvoker;
/*     */   public static final int NO_CHANGE = 0;
/*     */   public static final int ERASE = 1;
/*     */   public static final int WRAP = 2;
/*     */   public static final int UNWRAP = 3;
/*     */   public static final int INTS = 4;
/*     */   public static final int LONGS = 5;
/*     */   public static final int RAW_RETURN = 6;
/*     */ 
/*     */   public MethodType erasedType()
/*     */   {
/*  66 */     return this.erasedType;
/*     */   }
/*     */ 
/*     */   protected MethodTypeForm(MethodType paramMethodType) {
/*  70 */     this.erasedType = paramMethodType;
/*     */ 
/*  72 */     Class[] arrayOfClass1 = paramMethodType.ptypes();
/*  73 */     int i = arrayOfClass1.length;
/*  74 */     int j = i;
/*  75 */     int k = 1;
/*  76 */     int m = 1;
/*     */ 
/*  78 */     int[] arrayOfInt1 = null; int[] arrayOfInt2 = null;
/*     */ 
/*  81 */     int n = 0; int i1 = 0; int i2 = 0; int i3 = 0;
/*  82 */     Class[] arrayOfClass2 = arrayOfClass1;
/*  83 */     for (int i4 = 0; i4 < arrayOfClass2.length; i4++) {
/*  84 */       Class localClass2 = arrayOfClass2[i4];
/*  85 */       if (localClass2 != Object.class) {
/*  86 */         assert (localClass2.isPrimitive());
/*  87 */         n++;
/*  88 */         if (hasTwoArgSlots(localClass2)) i1++;
/*     */       }
/*     */     }
/*  91 */     j += i1;
/*  92 */     Class localClass1 = paramMethodType.returnType();
/*  93 */     if (localClass1 != Object.class) {
/*  94 */       i2++;
/*  95 */       if (hasTwoArgSlots(localClass1)) i3++;
/*     */ 
/*  97 */       if (localClass1 == Void.TYPE)
/*  98 */         k = m = 0;
/*     */       else
/* 100 */         m += i3;
/*     */     }
/*     */     int i5;
/*     */     int i6;
/* 102 */     if (i1 != 0) {
/* 103 */       i5 = i + i1;
/* 104 */       arrayOfInt2 = new int[i5 + 1];
/* 105 */       arrayOfInt1 = new int[1 + i];
/* 106 */       arrayOfInt1[0] = i5;
/* 107 */       for (i6 = 0; i6 < arrayOfClass2.length; i6++) {
/* 108 */         Class localClass3 = arrayOfClass2[i6];
/* 109 */         if (hasTwoArgSlots(localClass3)) i5--;
/* 110 */         i5--;
/* 111 */         arrayOfInt2[i5] = (i6 + 1);
/* 112 */         arrayOfInt1[(1 + i6)] = i5;
/*     */       }
/* 114 */       assert (i5 == 0);
/*     */     }
/* 116 */     this.primCounts = pack(i3, i2, i1, n);
/* 117 */     this.argCounts = pack(m, k, j, i);
/* 118 */     if (arrayOfInt2 == null) {
/* 119 */       i5 = i;
/* 120 */       arrayOfInt2 = new int[i5 + 1];
/* 121 */       arrayOfInt1 = new int[1 + i];
/* 122 */       arrayOfInt1[0] = i5;
/* 123 */       for (i6 = 0; i6 < i; i6++) {
/* 124 */         i5--;
/* 125 */         arrayOfInt2[i5] = (i6 + 1);
/* 126 */         arrayOfInt1[(1 + i6)] = i5;
/*     */       }
/*     */     }
/* 129 */     this.argToSlotTable = arrayOfInt1;
/* 130 */     this.slotToArgTable = arrayOfInt2;
/*     */ 
/* 132 */     if (j >= 256) throw MethodHandleStatics.newIllegalArgumentException("too many arguments");
/*     */ 
/* 135 */     this.vmslots = parameterSlotCount();
/*     */ 
/* 138 */     if (!hasPrimitives()) {
/* 139 */       this.primsAsBoxes = paramMethodType;
/* 140 */       this.primArgsAsBoxes = paramMethodType;
/* 141 */       this.primsAsInts = paramMethodType;
/* 142 */       this.primsAsLongs = paramMethodType;
/* 143 */       this.primsAtEnd = paramMethodType;
/*     */     }
/*     */   }
/*     */ 
/*     */   public MethodType primsAsBoxes()
/*     */   {
/* 150 */     Object localObject = this.primsAsBoxes;
/* 151 */     if (localObject != null) return localObject;
/* 152 */     MethodType localMethodType = this.erasedType;
/* 153 */     localObject = canonicalize(this.erasedType, 2, 2);
/* 154 */     if (localObject == null) localObject = localMethodType;
/* 155 */     return this.primsAsBoxes = localObject;
/*     */   }
/*     */ 
/*     */   public MethodType primArgsAsBoxes()
/*     */   {
/* 162 */     Object localObject = this.primArgsAsBoxes;
/* 163 */     if (localObject != null) return localObject;
/* 164 */     MethodType localMethodType = this.erasedType;
/* 165 */     localObject = canonicalize(this.erasedType, 6, 2);
/* 166 */     if (localObject == null) localObject = localMethodType;
/* 167 */     return this.primArgsAsBoxes = localObject;
/*     */   }
/*     */ 
/*     */   public MethodType primsAsInts()
/*     */   {
/* 176 */     Object localObject = this.primsAsInts;
/* 177 */     if (localObject != null) return localObject;
/* 178 */     MethodType localMethodType = this.erasedType;
/* 179 */     localObject = canonicalize(localMethodType, 6, 4);
/* 180 */     if (localObject == null) localObject = localMethodType;
/* 181 */     return this.primsAsInts = localObject;
/*     */   }
/*     */ 
/*     */   public MethodType primsAsLongs()
/*     */   {
/* 190 */     Object localObject = this.primsAsLongs;
/* 191 */     if (localObject != null) return localObject;
/* 192 */     MethodType localMethodType = this.erasedType;
/* 193 */     localObject = canonicalize(localMethodType, 6, 5);
/* 194 */     if (localObject == null) localObject = localMethodType;
/* 195 */     return this.primsAsLongs = localObject;
/*     */   }
/*     */ 
/*     */   public MethodType primsAtEnd()
/*     */   {
/* 200 */     MethodType localMethodType1 = this.primsAtEnd;
/* 201 */     if (localMethodType1 != null) return localMethodType1;
/* 202 */     MethodType localMethodType2 = this.erasedType;
/*     */ 
/* 204 */     int i = primitiveParameterCount();
/* 205 */     if (i == 0) {
/* 206 */       return this.primsAtEnd = localMethodType2;
/*     */     }
/* 208 */     int j = parameterCount();
/* 209 */     int k = longPrimitiveParameterCount();
/* 210 */     if ((i == j) && ((k == 0) || (k == j))) {
/* 211 */       return this.primsAtEnd = localMethodType2;
/*     */     }
/*     */ 
/* 214 */     int[] arrayOfInt = primsAtEndOrder(localMethodType2);
/* 215 */     localMethodType1 = reorderParameters(localMethodType2, arrayOfInt, null);
/*     */ 
/* 217 */     return this.primsAtEnd = localMethodType1;
/*     */   }
/*     */ 
/*     */   public static int[] primsAtEndOrder(MethodType paramMethodType)
/*     */   {
/* 234 */     MethodTypeForm localMethodTypeForm = paramMethodType.form();
/* 235 */     if (localMethodTypeForm.primsAtEnd == localMethodTypeForm.erasedType)
/*     */     {
/* 237 */       return null;
/*     */     }
/* 239 */     int i = localMethodTypeForm.parameterCount();
/* 240 */     int[] arrayOfInt = new int[i];
/*     */ 
/* 243 */     int j = localMethodTypeForm.primitiveParameterCount();
/* 244 */     int k = localMethodTypeForm.longPrimitiveParameterCount();
/* 245 */     int m = 0; int n = i - j; int i1 = i - k;
/*     */ 
/* 247 */     Class[] arrayOfClass = paramMethodType.ptypes();
/* 248 */     int i2 = 0;
/* 249 */     for (int i3 = 0; i3 < arrayOfClass.length; i3++) {
/* 250 */       Class localClass = arrayOfClass[i3];
/*     */       int i4;
/* 252 */       if (!localClass.isPrimitive()) i4 = m++;
/* 253 */       else if (!hasTwoArgSlots(localClass)) i4 = n++; else
/* 254 */         i4 = i1++;
/* 255 */       if (i4 != i3) i2 = 1;
/* 256 */       assert (arrayOfInt[i4] == 0);
/* 257 */       arrayOfInt[i4] = i3;
/*     */     }
/* 259 */     assert ((m == i - j) && (n == i - k) && (i1 == i));
/* 260 */     if (i2 == 0) {
/* 261 */       localMethodTypeForm.primsAtEnd = localMethodTypeForm.erasedType;
/* 262 */       return null;
/*     */     }
/* 264 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public static MethodType reorderParameters(MethodType paramMethodType, int[] paramArrayOfInt, Class<?>[] paramArrayOfClass)
/*     */   {
/* 273 */     if (paramArrayOfInt == null) return paramMethodType;
/* 274 */     Class[] arrayOfClass1 = paramMethodType.ptypes();
/* 275 */     Class[] arrayOfClass2 = new Class[paramArrayOfInt.length];
/* 276 */     int i = arrayOfClass1.length + (paramArrayOfClass == null ? 0 : paramArrayOfClass.length);
/* 277 */     int j = arrayOfClass2.length != arrayOfClass1.length ? 1 : 0;
/* 278 */     for (int k = 0; k < paramArrayOfInt.length; k++) {
/* 279 */       int m = paramArrayOfInt[k];
/* 280 */       if (m != k) j = 1;
/* 282 */       Object localObject;
/* 282 */       if (m < arrayOfClass1.length) localObject = arrayOfClass1[m];
/* 283 */       else if (m == i) localObject = paramMethodType.returnType(); else
/* 284 */         localObject = paramArrayOfClass[(m - arrayOfClass1.length)];
/* 285 */       arrayOfClass2[k] = localObject;
/*     */     }
/* 287 */     if (j == 0) return paramMethodType;
/* 288 */     return MethodType.makeImpl(paramMethodType.returnType(), arrayOfClass2, true);
/*     */   }
/*     */ 
/*     */   private static boolean hasTwoArgSlots(Class<?> paramClass) {
/* 292 */     return (paramClass == Long.TYPE) || (paramClass == Double.TYPE);
/*     */   }
/*     */ 
/*     */   private static long pack(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 296 */     assert (((paramInt1 | paramInt2 | paramInt3 | paramInt4) & 0xFFFF0000) == 0);
/* 297 */     long l1 = paramInt1 << 16 | paramInt2; long l2 = paramInt3 << 16 | paramInt4;
/* 298 */     return l1 << 32 | l2;
/*     */   }
/*     */   private static char unpack(long paramLong, int paramInt) {
/* 301 */     assert (paramInt <= 3);
/* 302 */     return (char)(int)(paramLong >> (3 - paramInt) * 16);
/*     */   }
/*     */ 
/*     */   public int parameterCount() {
/* 306 */     return unpack(this.argCounts, 3);
/*     */   }
/*     */   public int parameterSlotCount() {
/* 309 */     return unpack(this.argCounts, 2);
/*     */   }
/*     */   public int returnCount() {
/* 312 */     return unpack(this.argCounts, 1);
/*     */   }
/*     */   public int returnSlotCount() {
/* 315 */     return unpack(this.argCounts, 0);
/*     */   }
/*     */   public int primitiveParameterCount() {
/* 318 */     return unpack(this.primCounts, 3);
/*     */   }
/*     */   public int longPrimitiveParameterCount() {
/* 321 */     return unpack(this.primCounts, 2);
/*     */   }
/*     */   public int primitiveReturnCount() {
/* 324 */     return unpack(this.primCounts, 1);
/*     */   }
/*     */   public int longPrimitiveReturnCount() {
/* 327 */     return unpack(this.primCounts, 0);
/*     */   }
/*     */   public boolean hasPrimitives() {
/* 330 */     return this.primCounts != 0L;
/*     */   }
/*     */ 
/*     */   public boolean hasLongPrimitives()
/*     */   {
/* 338 */     return (longPrimitiveParameterCount() | longPrimitiveReturnCount()) != 0;
/*     */   }
/*     */   public int parameterToArgSlot(int paramInt) {
/* 341 */     return this.argToSlotTable[(1 + paramInt)];
/*     */   }
/*     */ 
/*     */   public int argSlotToParameter(int paramInt)
/*     */   {
/* 347 */     return this.slotToArgTable[paramInt] - 1;
/*     */   }
/*     */ 
/*     */   static MethodTypeForm findForm(MethodType paramMethodType) {
/* 351 */     MethodType localMethodType = canonicalize(paramMethodType, 1, 1);
/* 352 */     if (localMethodType == null)
/*     */     {
/* 354 */       return new MethodTypeForm(paramMethodType);
/*     */     }
/*     */ 
/* 357 */     return localMethodType.form();
/*     */   }
/*     */ 
/*     */   public static MethodType canonicalize(MethodType paramMethodType, int paramInt1, int paramInt2)
/*     */   {
/* 378 */     Class[] arrayOfClass1 = paramMethodType.ptypes();
/* 379 */     Class[] arrayOfClass2 = canonicalizes(arrayOfClass1, paramInt2);
/* 380 */     Class localClass1 = paramMethodType.returnType();
/* 381 */     Class localClass2 = canonicalize(localClass1, paramInt1);
/* 382 */     if ((arrayOfClass2 == null) && (localClass2 == null))
/*     */     {
/* 384 */       return null;
/*     */     }
/*     */ 
/* 387 */     if (localClass2 == null) localClass2 = localClass1;
/* 388 */     if (arrayOfClass2 == null) arrayOfClass2 = arrayOfClass1;
/* 389 */     return MethodType.makeImpl(localClass2, arrayOfClass2, true);
/*     */   }
/*     */ 
/*     */   static Class<?> canonicalize(Class<?> paramClass, int paramInt)
/*     */   {
/* 397 */     if (paramClass != Object.class)
/*     */     {
/* 399 */       if (!paramClass.isPrimitive())
/* 400 */         switch (paramInt) {
/*     */         case 3:
/* 402 */           Class localClass = Wrapper.asPrimitiveType(paramClass);
/* 403 */           if (localClass != paramClass) return localClass;
/*     */           break;
/*     */         case 1:
/*     */         case 6:
/* 407 */           return Object.class;
/*     */         }
/* 409 */       else if (paramClass == Void.TYPE)
/*     */       {
/* 411 */         switch (paramInt) {
/*     */         case 6:
/* 413 */           return Integer.TYPE;
/*     */         case 2:
/* 415 */           return Void.class;
/*     */         }
/*     */       }
/*     */       else
/* 419 */         switch (paramInt) {
/*     */         case 2:
/* 421 */           return Wrapper.asWrapperType(paramClass);
/*     */         case 4:
/* 423 */           if ((paramClass == Integer.TYPE) || (paramClass == Long.TYPE))
/* 424 */             return null;
/* 425 */           if (paramClass == Double.TYPE)
/* 426 */             return Long.TYPE;
/* 427 */           return Integer.TYPE;
/*     */         case 5:
/* 429 */           if (paramClass == Long.TYPE)
/* 430 */             return null;
/* 431 */           return Long.TYPE;
/*     */         case 6:
/* 433 */           if ((paramClass == Integer.TYPE) || (paramClass == Long.TYPE) || (paramClass == Float.TYPE) || (paramClass == Double.TYPE))
/*     */           {
/* 435 */             return null;
/*     */           }
/* 437 */           return Integer.TYPE;
/*     */         case 3:
/*     */         }
/*     */     }
/* 441 */     return null;
/*     */   }
/*     */ 
/*     */   static Class<?>[] canonicalizes(Class<?>[] paramArrayOfClass, int paramInt)
/*     */   {
/* 448 */     Class[] arrayOfClass = null;
/* 449 */     int i = paramArrayOfClass.length; for (int j = 0; j < i; j++) {
/* 450 */       Class localClass = canonicalize(paramArrayOfClass[j], paramInt);
/* 451 */       if (localClass == Void.TYPE)
/* 452 */         localClass = null;
/* 453 */       if (localClass != null) {
/* 454 */         if (arrayOfClass == null)
/* 455 */           arrayOfClass = (Class[])paramArrayOfClass.clone();
/* 456 */         arrayOfClass[j] = localClass;
/*     */       }
/*     */     }
/* 459 */     return arrayOfClass;
/*     */   }
/*     */ 
/*     */   void notifyGenericMethodType() {
/* 463 */     if (this.genericInvoker != null) return;
/*     */     try
/*     */     {
/* 466 */       this.genericInvoker = InvokeGeneric.generalInvokerOf(this.erasedType);
/*     */     } catch (Exception localException) {
/* 468 */       InternalError localInternalError = new InternalError("Exception while resolving inexact invoke");
/* 469 */       localInternalError.initCause(localException);
/* 470 */       throw localInternalError;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 476 */     return "Form" + this.erasedType;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodTypeForm
 * JD-Core Version:    0.6.2
 */