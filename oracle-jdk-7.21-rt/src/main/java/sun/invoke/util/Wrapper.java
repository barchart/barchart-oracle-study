/*     */ package sun.invoke.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public enum Wrapper
/*     */ {
/*     */   private final Class<?> wrapperType;
/*     */   private final Class<?> primitiveType;
/*     */   private final char basicTypeChar;
/*     */   private final Object zero;
/*     */   private final Object emptyArray;
/*     */   private final int format;
/*     */   private final String simpleName;
/*     */   private static final Wrapper[] FROM_PRIM;
/*     */   private static final Wrapper[] FROM_WRAP;
/*     */   private static final Wrapper[] FROM_CHAR;
/*     */ 
/*     */   private Wrapper(Class<?> paramClass1, Class<?> paramClass2, char paramChar, Object paramObject1, Object paramObject2, int paramInt)
/*     */   {
/*  53 */     this.wrapperType = paramClass1;
/*  54 */     this.primitiveType = paramClass2;
/*  55 */     this.basicTypeChar = paramChar;
/*  56 */     this.zero = paramObject1;
/*  57 */     this.emptyArray = paramObject2;
/*  58 */     this.format = paramInt;
/*  59 */     this.simpleName = paramClass1.getSimpleName();
/*     */   }
/*     */ 
/*     */   public String detailString()
/*     */   {
/*  64 */     return this.simpleName + Arrays.asList(new Object[] { this.wrapperType, this.primitiveType, Character.valueOf(this.basicTypeChar), this.zero, "0x" + Integer.toHexString(this.format) });
/*     */   }
/*     */ 
/*     */   public int bitWidth()
/*     */   {
/* 108 */     return this.format >> 2 & 0x3FF;
/*     */   }
/* 110 */   public int stackSlots() { return this.format >> 0 & 0x3; } 
/*     */   public boolean isSingleWord() {
/* 112 */     return (this.format & 0x1) != 0;
/*     */   }
/* 114 */   public boolean isDoubleWord() { return (this.format & 0x2) != 0; } 
/*     */   public boolean isNumeric() {
/* 116 */     return (this.format & 0xFFFFFFFC) != 0;
/*     */   }
/* 118 */   public boolean isIntegral() { return (isNumeric()) && (this.format < 4225); } 
/*     */   public boolean isSubwordOrInt() {
/* 120 */     return (isIntegral()) && (isSingleWord());
/*     */   }
/* 122 */   public boolean isSigned() { return this.format < 0; } 
/*     */   public boolean isUnsigned() {
/* 124 */     return (this.format >= 5) && (this.format < 4225);
/*     */   }
/* 126 */   public boolean isFloating() { return this.format >= 4225; } 
/*     */   public boolean isOther() {
/* 128 */     return (this.format & 0xFFFFFFFC) == 0;
/*     */   }
/*     */ 
/*     */   public boolean isConvertibleFrom(Wrapper paramWrapper)
/*     */   {
/* 141 */     if (this == paramWrapper) return true;
/* 142 */     if (compareTo(paramWrapper) < 0)
/*     */     {
/* 144 */       return false;
/*     */     }
/*     */ 
/* 148 */     int i = (this.format & paramWrapper.format & 0xFFFFF000) != 0 ? 1 : 0;
/* 149 */     if (i == 0) {
/* 150 */       if (isOther()) return true;
/*     */ 
/* 152 */       if (paramWrapper.format == 65) return true;
/*     */ 
/* 154 */       return false;
/*     */     }
/*     */ 
/* 157 */     assert ((isFloating()) || (isSigned()));
/* 158 */     assert ((paramWrapper.isFloating()) || (paramWrapper.isSigned()));
/* 159 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean checkConvertibleFrom()
/*     */   {
/* 165 */     for (Wrapper localWrapper1 : values()) {
/* 166 */       assert (localWrapper1.isConvertibleFrom(localWrapper1));
/* 167 */       assert (VOID.isConvertibleFrom(localWrapper1));
/* 168 */       if (localWrapper1 != VOID) {
/* 169 */         assert (OBJECT.isConvertibleFrom(localWrapper1));
/* 170 */         assert (!localWrapper1.isConvertibleFrom(VOID));
/*     */       }
/*     */ 
/* 173 */       if (localWrapper1 != CHAR) {
/* 174 */         assert (!CHAR.isConvertibleFrom(localWrapper1));
/* 175 */         if ((!localWrapper1.isConvertibleFrom(INT)) && 
/* 176 */           (!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(CHAR))) throw new AssertionError();
/*     */       }
/* 178 */       if (localWrapper1 != BOOLEAN) {
/* 179 */         assert (!BOOLEAN.isConvertibleFrom(localWrapper1));
/* 180 */         if ((localWrapper1 != VOID) && (localWrapper1 != OBJECT) && 
/* 181 */           (!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(BOOLEAN))) throw new AssertionError();
/*     */       }
/*     */       Wrapper localWrapper2;
/* 184 */       if (localWrapper1.isSigned()) {
/* 185 */         for (localWrapper2 : values()) {
/* 186 */           if (localWrapper1 != localWrapper2) {
/* 187 */             if (localWrapper2.isFloating()) {
/* 188 */               if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError(); 
/*     */             }
/* 189 */             else if (localWrapper2.isSigned()) {
/* 190 */               if (localWrapper1.compareTo(localWrapper2) < 0) {
/* 191 */                 if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError(); 
/*     */               }
/*     */               else
/* 193 */                 assert (localWrapper1.isConvertibleFrom(localWrapper2));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 198 */       if (localWrapper1.isFloating()) {
/* 199 */         for (localWrapper2 : values()) {
/* 200 */           if (localWrapper1 != localWrapper2) {
/* 201 */             if (localWrapper2.isSigned()) {
/* 202 */               if ((!$assertionsDisabled) && (!localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError(); 
/*     */             }
/* 203 */             else if (localWrapper2.isFloating())
/* 204 */               if (localWrapper1.compareTo(localWrapper2) < 0) {
/* 205 */                 if ((!$assertionsDisabled) && (localWrapper1.isConvertibleFrom(localWrapper2))) throw new AssertionError(); 
/*     */               }
/*     */               else
/* 207 */                 assert (localWrapper1.isConvertibleFrom(localWrapper2));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 212 */     return true;
/*     */   }
/*     */ 
/*     */   public Object zero()
/*     */   {
/* 223 */     return this.zero;
/*     */   }
/*     */ 
/*     */   public <T> T zero(Class<T> paramClass)
/*     */   {
/* 229 */     return convert(this.zero, paramClass);
/*     */   }
/*     */ 
/*     */   public static Wrapper forPrimitiveType(Class<?> paramClass)
/*     */   {
/* 245 */     Wrapper localWrapper = findPrimitiveType(paramClass);
/* 246 */     if (localWrapper != null) return localWrapper;
/* 247 */     if (paramClass.isPrimitive())
/* 248 */       throw new InternalError();
/* 249 */     throw newIllegalArgumentException("not primitive: " + paramClass);
/*     */   }
/*     */ 
/*     */   static Wrapper findPrimitiveType(Class<?> paramClass) {
/* 253 */     Wrapper localWrapper = FROM_PRIM[hashPrim(paramClass)];
/* 254 */     if ((localWrapper != null) && (localWrapper.primitiveType == paramClass)) {
/* 255 */       return localWrapper;
/*     */     }
/* 257 */     return null;
/*     */   }
/*     */ 
/*     */   public static Wrapper forWrapperType(Class<?> paramClass)
/*     */   {
/* 267 */     Wrapper localWrapper1 = findWrapperType(paramClass);
/* 268 */     if (localWrapper1 != null) return localWrapper1;
/* 269 */     for (Wrapper localWrapper2 : values())
/* 270 */       if (localWrapper2.wrapperType == paramClass)
/* 271 */         throw new InternalError();
/* 272 */     throw newIllegalArgumentException("not wrapper: " + paramClass);
/*     */   }
/*     */ 
/*     */   static Wrapper findWrapperType(Class<?> paramClass) {
/* 276 */     Wrapper localWrapper = FROM_WRAP[hashWrap(paramClass)];
/* 277 */     if ((localWrapper != null) && (localWrapper.wrapperType == paramClass)) {
/* 278 */       return localWrapper;
/*     */     }
/* 280 */     return null;
/*     */   }
/*     */ 
/*     */   public static Wrapper forBasicType(char paramChar)
/*     */   {
/* 288 */     Wrapper localWrapper1 = FROM_CHAR[hashChar(paramChar)];
/* 289 */     if ((localWrapper1 != null) && (localWrapper1.basicTypeChar == paramChar)) {
/* 290 */       return localWrapper1;
/*     */     }
/* 292 */     for (Wrapper localWrapper2 : values())
/* 293 */       if (localWrapper1.basicTypeChar == paramChar)
/* 294 */         throw new InternalError();
/* 295 */     throw newIllegalArgumentException("not basic type char: " + paramChar);
/*     */   }
/*     */ 
/*     */   public static Wrapper forBasicType(Class<?> paramClass)
/*     */   {
/* 302 */     if (paramClass.isPrimitive())
/* 303 */       return forPrimitiveType(paramClass);
/* 304 */     return OBJECT;
/*     */   }
/*     */ 
/*     */   private static int hashPrim(Class<?> paramClass)
/*     */   {
/* 317 */     String str = paramClass.getName();
/* 318 */     if (str.length() < 3) return 0;
/* 319 */     return (str.charAt(0) + str.charAt(2)) % 16;
/*     */   }
/*     */   private static int hashWrap(Class<?> paramClass) {
/* 322 */     String str = paramClass.getName();
/* 323 */     assert (10 == "java.lang.".length());
/* 324 */     if (str.length() < 13) return 0;
/* 325 */     return ('\003' * str.charAt(11) + str.charAt(12)) % 16;
/*     */   }
/*     */   private static int hashChar(char paramChar) {
/* 328 */     return (paramChar + (paramChar >> '\001')) % 16;
/*     */   }
/*     */ 
/*     */   public Class<?> primitiveType()
/*     */   {
/* 346 */     return this.primitiveType;
/*     */   }
/*     */   public Class<?> wrapperType() {
/* 349 */     return this.wrapperType;
/*     */   }
/*     */ 
/*     */   public <T> Class<T> wrapperType(Class<T> paramClass)
/*     */   {
/* 359 */     if (paramClass == this.wrapperType)
/* 360 */       return paramClass;
/* 361 */     if ((paramClass == this.primitiveType) || (this.wrapperType == Object.class) || (paramClass.isInterface()))
/*     */     {
/* 364 */       return forceType(this.wrapperType, paramClass);
/*     */     }
/* 366 */     throw newClassCastException(paramClass, this.primitiveType);
/*     */   }
/*     */ 
/*     */   private static ClassCastException newClassCastException(Class<?> paramClass1, Class<?> paramClass2) {
/* 370 */     return new ClassCastException(paramClass1 + " is not compatible with " + paramClass2);
/*     */   }
/*     */ 
/*     */   public static <T> Class<T> asWrapperType(Class<T> paramClass)
/*     */   {
/* 377 */     if (paramClass.isPrimitive()) {
/* 378 */       return forPrimitiveType(paramClass).wrapperType(paramClass);
/*     */     }
/* 380 */     return paramClass;
/*     */   }
/*     */ 
/*     */   public static <T> Class<T> asPrimitiveType(Class<T> paramClass)
/*     */   {
/* 387 */     Wrapper localWrapper = findWrapperType(paramClass);
/* 388 */     if (localWrapper != null) {
/* 389 */       return forceType(localWrapper.primitiveType(), paramClass);
/*     */     }
/* 391 */     return paramClass;
/*     */   }
/*     */ 
/*     */   public static boolean isWrapperType(Class<?> paramClass)
/*     */   {
/* 396 */     return findWrapperType(paramClass) != null;
/*     */   }
/*     */ 
/*     */   public static boolean isPrimitiveType(Class<?> paramClass)
/*     */   {
/* 401 */     return paramClass.isPrimitive();
/*     */   }
/*     */ 
/*     */   public static char basicTypeChar(Class<?> paramClass)
/*     */   {
/* 408 */     if (!paramClass.isPrimitive()) {
/* 409 */       return 'L';
/*     */     }
/* 411 */     return forPrimitiveType(paramClass).basicTypeChar();
/*     */   }
/*     */ 
/*     */   public char basicTypeChar()
/*     */   {
/* 417 */     return this.basicTypeChar;
/*     */   }
/*     */ 
/*     */   public String simpleName() {
/* 421 */     return this.simpleName;
/*     */   }
/*     */ 
/*     */   public <T> T cast(Object paramObject, Class<T> paramClass)
/*     */   {
/* 441 */     return convert(paramObject, paramClass, true);
/*     */   }
/*     */ 
/*     */   public <T> T convert(Object paramObject, Class<T> paramClass)
/*     */   {
/* 450 */     return convert(paramObject, paramClass, false);
/*     */   }
/*     */ 
/*     */   private <T> T convert(Object paramObject, Class<T> paramClass, boolean paramBoolean) {
/* 454 */     if (this == OBJECT)
/*     */     {
/* 459 */       assert (!paramClass.isPrimitive());
/* 460 */       if (!paramClass.isInterface()) {
/* 461 */         paramClass.cast(paramObject);
/*     */       }
/* 463 */       localObject1 = paramObject;
/* 464 */       return localObject1;
/*     */     }
/* 466 */     Object localObject1 = wrapperType(paramClass);
/* 467 */     if (((Class)localObject1).isInstance(paramObject))
/*     */     {
/* 469 */       localObject2 = paramObject;
/* 470 */       return localObject2;
/*     */     }
/* 472 */     Object localObject2 = paramObject.getClass();
/* 473 */     if (!paramBoolean) {
/* 474 */       localObject3 = findWrapperType((Class)localObject2);
/* 475 */       if ((localObject3 == null) || (!isConvertibleFrom((Wrapper)localObject3))) {
/* 476 */         throw newClassCastException((Class)localObject1, (Class)localObject2);
/*     */       }
/*     */     }
/*     */ 
/* 480 */     Object localObject3 = wrap(paramObject);
/* 481 */     assert (localObject3.getClass() == localObject1);
/* 482 */     return localObject3;
/*     */   }
/*     */ 
/*     */   static <T> Class<T> forceType(Class<?> paramClass, Class<T> paramClass1)
/*     */   {
/* 491 */     int i = (paramClass == paramClass1) || ((paramClass.isPrimitive()) && (forPrimitiveType(paramClass) == findWrapperType(paramClass1))) || ((paramClass1.isPrimitive()) && (forPrimitiveType(paramClass1) == findWrapperType(paramClass))) || ((paramClass == Object.class) && (!paramClass1.isPrimitive())) ? 1 : 0;
/*     */ 
/* 495 */     if (i == 0)
/* 496 */       System.out.println(paramClass + " <= " + paramClass1);
/* 497 */     assert ((paramClass == paramClass1) || ((paramClass.isPrimitive()) && (forPrimitiveType(paramClass) == findWrapperType(paramClass1))) || ((paramClass1.isPrimitive()) && (forPrimitiveType(paramClass1) == findWrapperType(paramClass))) || ((paramClass == Object.class) && (!paramClass1.isPrimitive())));
/*     */ 
/* 502 */     Class<?> localClass = paramClass;
/* 503 */     return localClass;
/*     */   }
/*     */ 
/*     */   public Object wrap(Object paramObject)
/*     */   {
/* 516 */     switch (this.basicTypeChar) { case 'L':
/* 517 */       return paramObject;
/*     */     case 'V':
/* 518 */       return null;
/*     */     }
/* 520 */     Number localNumber = numberValue(paramObject);
/* 521 */     switch (this.basicTypeChar) { case 'I':
/* 522 */       return Integer.valueOf(localNumber.intValue());
/*     */     case 'J':
/* 523 */       return Long.valueOf(localNumber.longValue());
/*     */     case 'F':
/* 524 */       return Float.valueOf(localNumber.floatValue());
/*     */     case 'D':
/* 525 */       return Double.valueOf(localNumber.doubleValue());
/*     */     case 'S':
/* 526 */       return Short.valueOf((short)localNumber.intValue());
/*     */     case 'B':
/* 527 */       return Byte.valueOf((byte)localNumber.intValue());
/*     */     case 'C':
/* 528 */       return Character.valueOf((char)localNumber.intValue());
/*     */     case 'Z':
/* 529 */       return Boolean.valueOf(boolValue(localNumber.longValue()));
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'L':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'V':
/*     */     case 'W':
/*     */     case 'X':
/* 531 */     case 'Y': } throw new InternalError("bad wrapper");
/*     */   }
/*     */ 
/*     */   public Object wrap(int paramInt)
/*     */   {
/* 541 */     if (this.basicTypeChar == 'L') return Integer.valueOf(paramInt);
/* 542 */     switch (this.basicTypeChar) { case 'L':
/* 543 */       throw newIllegalArgumentException("cannot wrap to object type");
/*     */     case 'V':
/* 544 */       return null;
/*     */     case 'I':
/* 545 */       return Integer.valueOf(paramInt);
/*     */     case 'J':
/* 546 */       return Long.valueOf(paramInt);
/*     */     case 'F':
/* 547 */       return Float.valueOf(paramInt);
/*     */     case 'D':
/* 548 */       return Double.valueOf(paramInt);
/*     */     case 'S':
/* 549 */       return Short.valueOf((short)paramInt);
/*     */     case 'B':
/* 550 */       return Byte.valueOf((byte)paramInt);
/*     */     case 'C':
/* 551 */       return Character.valueOf((char)paramInt);
/*     */     case 'Z':
/* 552 */       return Boolean.valueOf(boolValue(paramInt));
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'W':
/*     */     case 'X':
/* 554 */     case 'Y': } throw new InternalError("bad wrapper");
/*     */   }
/*     */ 
/*     */   public Object wrapRaw(long paramLong)
/*     */   {
/* 564 */     switch (this.basicTypeChar) { case 'F':
/* 565 */       return Float.valueOf(Float.intBitsToFloat((int)paramLong));
/*     */     case 'D':
/* 566 */       return Double.valueOf(Double.longBitsToDouble(paramLong));
/*     */     case 'J':
/*     */     case 'L':
/* 568 */       return Long.valueOf(paramLong);
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'I':
/* 572 */     case 'K': } return wrap((int)paramLong);
/*     */   }
/*     */ 
/*     */   public long unwrapRaw(Object paramObject)
/*     */   {
/* 580 */     switch (this.basicTypeChar) { case 'F':
/* 581 */       return Float.floatToRawIntBits(((Float)paramObject).floatValue());
/*     */     case 'D':
/* 582 */       return Double.doubleToRawLongBits(((Double)paramObject).doubleValue());
/*     */     case 'L':
/* 584 */       throw newIllegalArgumentException("cannot unwrap from sobject type");
/*     */     case 'V':
/* 585 */       return 0L;
/*     */     case 'I':
/* 586 */       return ((Integer)paramObject).intValue();
/*     */     case 'J':
/* 587 */       return ((Long)paramObject).longValue();
/*     */     case 'S':
/* 588 */       return ((Short)paramObject).shortValue();
/*     */     case 'B':
/* 589 */       return ((Byte)paramObject).byteValue();
/*     */     case 'C':
/* 590 */       return ((Character)paramObject).charValue();
/*     */     case 'Z':
/* 591 */       return ((Boolean)paramObject).booleanValue() ? 1L : 0L;
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'W':
/*     */     case 'X':
/* 593 */     case 'Y': } throw new InternalError("bad wrapper");
/*     */   }
/*     */ 
/*     */   public Class<?> rawPrimitiveType()
/*     */   {
/* 598 */     return rawPrimitive().primitiveType();
/*     */   }
/*     */ 
/*     */   public Wrapper rawPrimitive()
/*     */   {
/* 606 */     switch (this.basicTypeChar) { case 'B':
/*     */     case 'C':
/*     */     case 'F':
/*     */     case 'S':
/*     */     case 'V':
/*     */     case 'Z':
/* 611 */       return INT;
/*     */     case 'D':
/* 613 */       return LONG;
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'I':
/*     */     case 'J':
/*     */     case 'K':
/*     */     case 'L':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'W':
/*     */     case 'X':
/* 615 */     case 'Y': } return this;
/*     */   }
/*     */ 
/*     */   private static Number numberValue(Object paramObject) {
/* 619 */     if ((paramObject instanceof Number)) return (Number)paramObject;
/* 620 */     if ((paramObject instanceof Character)) return Integer.valueOf(((Character)paramObject).charValue());
/* 621 */     if ((paramObject instanceof Boolean)) return Integer.valueOf(((Boolean)paramObject).booleanValue() ? 1 : 0);
/*     */ 
/* 623 */     return (Number)paramObject;
/*     */   }
/*     */ 
/*     */   private static boolean boolValue(long paramLong) {
/* 627 */     paramLong &= 1L;
/* 628 */     return paramLong != 0L;
/*     */   }
/*     */ 
/*     */   private static RuntimeException newIllegalArgumentException(String paramString, Object paramObject) {
/* 632 */     return newIllegalArgumentException(paramString + paramObject);
/*     */   }
/*     */   private static RuntimeException newIllegalArgumentException(String paramString) {
/* 635 */     return new IllegalArgumentException(paramString);
/*     */   }
/*     */ 
/*     */   public Object makeArray(int paramInt)
/*     */   {
/* 640 */     return Array.newInstance(this.primitiveType, paramInt);
/*     */   }
/*     */   public Class<?> arrayType() {
/* 643 */     return this.emptyArray.getClass();
/*     */   }
/*     */   public void copyArrayUnboxing(Object[] paramArrayOfObject, int paramInt1, Object paramObject, int paramInt2, int paramInt3) {
/* 646 */     if (paramObject.getClass() != arrayType())
/* 647 */       arrayType().cast(paramObject);
/* 648 */     for (int i = 0; i < paramInt3; i++) {
/* 649 */       Object localObject = paramArrayOfObject[(i + paramInt1)];
/* 650 */       localObject = convert(localObject, this.primitiveType);
/* 651 */       Array.set(paramObject, i + paramInt2, localObject);
/*     */     }
/*     */   }
/*     */ 
/* 655 */   public void copyArrayBoxing(Object paramObject, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3) { if (paramObject.getClass() != arrayType())
/* 656 */       arrayType().cast(paramObject);
/* 657 */     for (int i = 0; i < paramInt3; i++) {
/* 658 */       Object localObject = Array.get(paramObject, i + paramInt1);
/*     */ 
/* 660 */       assert (localObject.getClass() == this.wrapperType);
/* 661 */       paramArrayOfObject[(i + paramInt2)] = localObject;
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  29 */     BOOLEAN = new Wrapper("BOOLEAN", 0, Boolean.class, Boolean.TYPE, 'Z', Boolean.valueOf(false), new boolean[0], Format.unsigned(1));
/*     */ 
/*  31 */     BYTE = new Wrapper("BYTE", 1, Byte.class, Byte.TYPE, 'B', Byte.valueOf((byte)0), new byte[0], Format.signed(8));
/*  32 */     SHORT = new Wrapper("SHORT", 2, Short.class, Short.TYPE, 'S', Short.valueOf((short)0), new short[0], Format.signed(16));
/*  33 */     CHAR = new Wrapper("CHAR", 3, Character.class, Character.TYPE, 'C', Character.valueOf('\000'), new char[0], Format.unsigned(16));
/*  34 */     INT = new Wrapper("INT", 4, Integer.class, Integer.TYPE, 'I', Integer.valueOf(0), new int[0], Format.signed(32));
/*  35 */     LONG = new Wrapper("LONG", 5, Long.class, Long.TYPE, 'J', Long.valueOf(0L), new long[0], Format.signed(64));
/*  36 */     FLOAT = new Wrapper("FLOAT", 6, Float.class, Float.TYPE, 'F', Float.valueOf(0.0F), new float[0], Format.floating(32));
/*  37 */     DOUBLE = new Wrapper("DOUBLE", 7, Double.class, Double.TYPE, 'D', Double.valueOf(0.0D), new double[0], Format.floating(64));
/*     */ 
/*  39 */     OBJECT = new Wrapper("OBJECT", 8, Object.class, Object.class, 'L', null, new Object[0], Format.other(1));
/*     */ 
/*  41 */     VOID = new Wrapper("VOID", 9, Void.class, Void.TYPE, 'V', null, null, Format.other(0));
/*     */ 
/*  28 */     $VALUES = new Wrapper[] { BOOLEAN, BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE, OBJECT, VOID };
/*     */ 
/* 162 */     assert (checkConvertibleFrom());
/*     */ 
/* 313 */     FROM_PRIM = new Wrapper[16];
/* 314 */     FROM_WRAP = new Wrapper[16];
/* 315 */     FROM_CHAR = new Wrapper[16];
/*     */ 
/* 331 */     for (Wrapper localWrapper : values()) {
/* 332 */       int k = hashPrim(localWrapper.primitiveType);
/* 333 */       int m = hashWrap(localWrapper.wrapperType);
/* 334 */       int n = hashChar(localWrapper.basicTypeChar);
/* 335 */       assert (FROM_PRIM[k] == null);
/* 336 */       assert (FROM_WRAP[m] == null);
/* 337 */       assert (FROM_CHAR[n] == null);
/* 338 */       FROM_PRIM[k] = localWrapper;
/* 339 */       FROM_WRAP[m] = localWrapper;
/* 340 */       FROM_CHAR[n] = localWrapper;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class Format
/*     */   {
/*     */     static final int SLOT_SHIFT = 0;
/*     */     static final int SIZE_SHIFT = 2;
/*     */     static final int KIND_SHIFT = 12;
/*     */     static final int SIGNED = -4096;
/*     */     static final int UNSIGNED = 0;
/*     */     static final int FLOATING = 4096;
/*     */     static final int SLOT_MASK = 3;
/*     */     static final int SIZE_MASK = 1023;
/*     */     static final int INT = -3967;
/*     */     static final int SHORT = -4031;
/*     */     static final int BOOLEAN = 5;
/*     */     static final int CHAR = 65;
/*     */     static final int FLOAT = 4225;
/*     */     static final int VOID = 0;
/*     */     static final int NUM_MASK = -4;
/*     */ 
/*     */     static int format(int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/*  80 */       assert (paramInt1 >> 12 << 12 == paramInt1);
/*  81 */       assert ((paramInt2 & paramInt2 - 1) == 0);
/*  82 */       assert (paramInt1 == -4096 ? paramInt2 > 0 : paramInt1 == 0 ? paramInt2 > 0 : (paramInt1 == 4096) && ((paramInt2 == 32) || (paramInt2 == 64)));
/*     */ 
/*  86 */       assert (paramInt3 == 2 ? paramInt2 != 64 : (paramInt3 == 1) && (paramInt2 <= 32));
/*     */ 
/*  89 */       return paramInt1 | paramInt2 << 2 | paramInt3 << 0;
/*     */     }
/*     */ 
/*     */     static int signed(int paramInt)
/*     */     {
/*  99 */       return format(-4096, paramInt, paramInt > 32 ? 2 : 1); } 
/* 100 */     static int unsigned(int paramInt) { return format(0, paramInt, paramInt > 32 ? 2 : 1); } 
/* 101 */     static int floating(int paramInt) { return format(4096, paramInt, paramInt > 32 ? 2 : 1); } 
/* 102 */     static int other(int paramInt) { return paramInt << 0; }
/*     */ 
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.invoke.util.Wrapper
 * JD-Core Version:    0.6.2
 */