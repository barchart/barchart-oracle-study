/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import sun.reflect.ConstantPool;
/*     */ import sun.reflect.generics.factory.CoreReflectionFactory;
/*     */ import sun.reflect.generics.parser.SignatureParser;
/*     */ import sun.reflect.generics.scope.ClassScope;
/*     */ import sun.reflect.generics.tree.TypeSignature;
/*     */ import sun.reflect.generics.visitor.Reifier;
/*     */ 
/*     */ public class AnnotationParser
/*     */ {
/* 165 */   private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
/*     */ 
/* 801 */   private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
/*     */ 
/*     */   public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/*  66 */     if (paramArrayOfByte == null)
/*  67 */       return Collections.emptyMap();
/*     */     try
/*     */     {
/*  70 */       return parseAnnotations2(paramArrayOfByte, paramConstantPool, paramClass);
/*     */     } catch (BufferUnderflowException localBufferUnderflowException) {
/*  72 */       throw new AnnotationFormatError("Unexpected end of annotations.");
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/*  75 */       throw new AnnotationFormatError(localIllegalArgumentException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Map<Class<? extends Annotation>, Annotation> parseAnnotations2(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/*  83 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap();
/*     */ 
/*  85 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/*  86 */     int i = localByteBuffer.getShort() & 0xFFFF;
/*  87 */     for (int j = 0; j < i; j++) {
/*  88 */       Annotation localAnnotation = parseAnnotation(localByteBuffer, paramConstantPool, paramClass, false);
/*  89 */       if (localAnnotation != null) {
/*  90 */         Class localClass = localAnnotation.annotationType();
/*  91 */         AnnotationType localAnnotationType = AnnotationType.getInstance(localClass);
/*  92 */         if ((localAnnotationType.retention() == RetentionPolicy.RUNTIME) && 
/*  93 */           (localLinkedHashMap.put(localClass, localAnnotation) != null)) {
/*  94 */           throw new AnnotationFormatError("Duplicate annotation for class: " + localClass + ": " + localAnnotation);
/*     */         }
/*     */       }
/*     */     }
/*  98 */     return localLinkedHashMap;
/*     */   }
/*     */ 
/*     */   public static Annotation[][] parseParameterAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/*     */     try
/*     */     {
/* 129 */       return parseParameterAnnotations2(paramArrayOfByte, paramConstantPool, paramClass);
/*     */     } catch (BufferUnderflowException localBufferUnderflowException) {
/* 131 */       throw new AnnotationFormatError("Unexpected end of parameter annotations.");
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException)
/*     */     {
/* 135 */       throw new AnnotationFormatError(localIllegalArgumentException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Annotation[][] parseParameterAnnotations2(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 143 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/* 144 */     int i = localByteBuffer.get() & 0xFF;
/* 145 */     Annotation[][] arrayOfAnnotation; = new Annotation[i][];
/*     */ 
/* 147 */     for (int j = 0; j < i; j++) {
/* 148 */       int k = localByteBuffer.getShort() & 0xFFFF;
/* 149 */       ArrayList localArrayList = new ArrayList(k);
/*     */ 
/* 151 */       for (int m = 0; m < k; m++) {
/* 152 */         Annotation localAnnotation = parseAnnotation(localByteBuffer, paramConstantPool, paramClass, false);
/* 153 */         if (localAnnotation != null) {
/* 154 */           AnnotationType localAnnotationType = AnnotationType.getInstance(localAnnotation.annotationType());
/*     */ 
/* 156 */           if (localAnnotationType.retention() == RetentionPolicy.RUNTIME)
/* 157 */             localArrayList.add(localAnnotation);
/*     */         }
/*     */       }
/* 160 */       arrayOfAnnotation;[j] = ((Annotation[])localArrayList.toArray(EMPTY_ANNOTATIONS_ARRAY));
/*     */     }
/* 162 */     return arrayOfAnnotation;;
/*     */   }
/*     */ 
/*     */   private static Annotation parseAnnotation(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass, boolean paramBoolean)
/*     */   {
/* 194 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 195 */     Class localClass1 = null;
/* 196 */     String str1 = "[unknown]";
/*     */     try {
/*     */       try {
/* 199 */         str1 = paramConstantPool.getUTF8At(i);
/* 200 */         localClass1 = parseSig(str1, paramClass);
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException1) {
/* 203 */         localClass1 = paramConstantPool.getClassAt(i);
/*     */       }
/*     */     } catch (NoClassDefFoundError localNoClassDefFoundError) {
/* 206 */       if (paramBoolean)
/*     */       {
/* 209 */         throw new TypeNotPresentException(str1, localNoClassDefFoundError);
/* 210 */       }skipAnnotation(paramByteBuffer, false);
/* 211 */       return null;
/*     */     }
/*     */     catch (TypeNotPresentException localTypeNotPresentException) {
/* 214 */       if (paramBoolean)
/* 215 */         throw localTypeNotPresentException;
/* 216 */       skipAnnotation(paramByteBuffer, false);
/* 217 */       return null;
/*     */     }
/* 219 */     AnnotationType localAnnotationType = null;
/*     */     try {
/* 221 */       localAnnotationType = AnnotationType.getInstance(localClass1);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException2) {
/* 223 */       skipAnnotation(paramByteBuffer, false);
/* 224 */       return null;
/*     */     }
/*     */ 
/* 227 */     Map localMap = localAnnotationType.memberTypes();
/* 228 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap(localAnnotationType.memberDefaults());
/*     */ 
/* 231 */     int j = paramByteBuffer.getShort() & 0xFFFF;
/* 232 */     for (int k = 0; k < j; k++) {
/* 233 */       int m = paramByteBuffer.getShort() & 0xFFFF;
/* 234 */       String str2 = paramConstantPool.getUTF8At(m);
/* 235 */       Class localClass2 = (Class)localMap.get(str2);
/*     */ 
/* 237 */       if (localClass2 == null)
/*     */       {
/* 239 */         skipMemberValue(paramByteBuffer);
/*     */       } else {
/* 241 */         Object localObject = parseMemberValue(localClass2, paramByteBuffer, paramConstantPool, paramClass);
/* 242 */         if ((localObject instanceof AnnotationTypeMismatchExceptionProxy)) {
/* 243 */           ((AnnotationTypeMismatchExceptionProxy)localObject).setMember((Method)localAnnotationType.members().get(str2));
/*     */         }
/* 245 */         localLinkedHashMap.put(str2, localObject);
/*     */       }
/*     */     }
/* 248 */     return annotationForMap(localClass1, localLinkedHashMap);
/*     */   }
/*     */ 
/*     */   public static Annotation annotationForMap(Class<? extends Annotation> paramClass, Map<String, Object> paramMap)
/*     */   {
/* 258 */     return (Annotation)Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, new AnnotationInvocationHandler(paramClass, paramMap));
/*     */   }
/*     */ 
/*     */   public static Object parseMemberValue(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2)
/*     */   {
/* 294 */     Object localObject = null;
/* 295 */     int i = paramByteBuffer.get();
/* 296 */     switch (i) {
/*     */     case 101:
/* 298 */       return parseEnumValue(paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
/*     */     case 99:
/* 300 */       localObject = parseClassValue(paramByteBuffer, paramConstantPool, paramClass2);
/* 301 */       break;
/*     */     case 64:
/* 303 */       localObject = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass2, true);
/* 304 */       break;
/*     */     case 91:
/* 306 */       return parseArray(paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
/*     */     default:
/* 308 */       localObject = parseConst(i, paramByteBuffer, paramConstantPool);
/*     */     }
/*     */ 
/* 311 */     if ((!(localObject instanceof ExceptionProxy)) && (!paramClass1.isInstance(localObject)))
/*     */     {
/* 313 */       localObject = new AnnotationTypeMismatchExceptionProxy(localObject.getClass() + "[" + localObject + "]");
/*     */     }
/* 315 */     return localObject;
/*     */   }
/*     */ 
/*     */   private static Object parseConst(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 330 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 331 */     switch (paramInt) {
/*     */     case 66:
/* 333 */       return Byte.valueOf((byte)paramConstantPool.getIntAt(i));
/*     */     case 67:
/* 335 */       return Character.valueOf((char)paramConstantPool.getIntAt(i));
/*     */     case 68:
/* 337 */       return Double.valueOf(paramConstantPool.getDoubleAt(i));
/*     */     case 70:
/* 339 */       return Float.valueOf(paramConstantPool.getFloatAt(i));
/*     */     case 73:
/* 341 */       return Integer.valueOf(paramConstantPool.getIntAt(i));
/*     */     case 74:
/* 343 */       return Long.valueOf(paramConstantPool.getLongAt(i));
/*     */     case 83:
/* 345 */       return Short.valueOf((short)paramConstantPool.getIntAt(i));
/*     */     case 90:
/* 347 */       return Boolean.valueOf(paramConstantPool.getIntAt(i) != 0);
/*     */     case 115:
/* 349 */       return paramConstantPool.getUTF8At(i);
/*     */     }
/* 351 */     throw new AnnotationFormatError("Invalid member-value tag in annotation: " + paramInt);
/*     */   }
/*     */ 
/*     */   private static Object parseClassValue(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 367 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/*     */     try
/*     */     {
/* 370 */       String str = paramConstantPool.getUTF8At(i);
/* 371 */       return parseSig(str, paramClass);
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 374 */       return paramConstantPool.getClassAt(i);
/*     */     }
/*     */     catch (NoClassDefFoundError localNoClassDefFoundError) {
/* 377 */       return new TypeNotPresentExceptionProxy("[unknown]", localNoClassDefFoundError);
/*     */     }
/*     */     catch (TypeNotPresentException localTypeNotPresentException) {
/* 380 */       return new TypeNotPresentExceptionProxy(localTypeNotPresentException.typeName(), localTypeNotPresentException.getCause());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Class<?> parseSig(String paramString, Class<?> paramClass) {
/* 385 */     if (paramString.equals("V")) return Void.TYPE;
/* 386 */     SignatureParser localSignatureParser = SignatureParser.make();
/* 387 */     TypeSignature localTypeSignature = localSignatureParser.parseTypeSig(paramString);
/* 388 */     CoreReflectionFactory localCoreReflectionFactory = CoreReflectionFactory.make(paramClass, ClassScope.make(paramClass));
/* 389 */     Reifier localReifier = Reifier.make(localCoreReflectionFactory);
/* 390 */     localTypeSignature.accept(localReifier);
/* 391 */     Type localType = localReifier.getResult();
/* 392 */     return toClass(localType);
/*     */   }
/*     */   static Class<?> toClass(Type paramType) {
/* 395 */     if ((paramType instanceof GenericArrayType)) {
/* 396 */       return Array.newInstance(toClass(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass();
/*     */     }
/*     */ 
/* 399 */     return (Class)paramType;
/*     */   }
/*     */ 
/*     */   private static Object parseEnumValue(Class<? extends Enum> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
/*     */   {
/* 417 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 418 */     String str1 = paramConstantPool.getUTF8At(i);
/* 419 */     int j = paramByteBuffer.getShort() & 0xFFFF;
/* 420 */     String str2 = paramConstantPool.getUTF8At(j);
/*     */ 
/* 422 */     if (!str1.endsWith(";"))
/*     */     {
/* 424 */       if (!paramClass.getName().equals(str1))
/* 425 */         return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2);
/*     */     }
/* 427 */     else if (paramClass != parseSig(str1, paramClass1)) {
/* 428 */       return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 433 */       return Enum.valueOf(paramClass, str2); } catch (IllegalArgumentException localIllegalArgumentException) {
/*     */     }
/* 435 */     return new EnumConstantNotPresentExceptionProxy(paramClass, str2);
/*     */   }
/*     */ 
/*     */   private static Object parseArray(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2)
/*     */   {
/* 458 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 459 */     Class localClass = paramClass1.getComponentType();
/*     */ 
/* 461 */     if (localClass == Byte.TYPE)
/* 462 */       return parseByteArray(i, paramByteBuffer, paramConstantPool);
/* 463 */     if (localClass == Character.TYPE)
/* 464 */       return parseCharArray(i, paramByteBuffer, paramConstantPool);
/* 465 */     if (localClass == Double.TYPE)
/* 466 */       return parseDoubleArray(i, paramByteBuffer, paramConstantPool);
/* 467 */     if (localClass == Float.TYPE)
/* 468 */       return parseFloatArray(i, paramByteBuffer, paramConstantPool);
/* 469 */     if (localClass == Integer.TYPE)
/* 470 */       return parseIntArray(i, paramByteBuffer, paramConstantPool);
/* 471 */     if (localClass == Long.TYPE)
/* 472 */       return parseLongArray(i, paramByteBuffer, paramConstantPool);
/* 473 */     if (localClass == Short.TYPE)
/* 474 */       return parseShortArray(i, paramByteBuffer, paramConstantPool);
/* 475 */     if (localClass == Boolean.TYPE)
/* 476 */       return parseBooleanArray(i, paramByteBuffer, paramConstantPool);
/* 477 */     if (localClass == String.class)
/* 478 */       return parseStringArray(i, paramByteBuffer, paramConstantPool);
/* 479 */     if (localClass == Class.class)
/* 480 */       return parseClassArray(i, paramByteBuffer, paramConstantPool, paramClass2);
/* 481 */     if (localClass.isEnum()) {
/* 482 */       return parseEnumArray(i, localClass, paramByteBuffer, paramConstantPool, paramClass2);
/*     */     }
/*     */ 
/* 485 */     assert (localClass.isAnnotation());
/* 486 */     return parseAnnotationArray(i, localClass, paramByteBuffer, paramConstantPool, paramClass2);
/*     */   }
/*     */ 
/*     */   private static Object parseByteArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 493 */     byte[] arrayOfByte = new byte[paramInt];
/* 494 */     int i = 0;
/* 495 */     int j = 0;
/*     */ 
/* 497 */     for (int k = 0; k < paramInt; k++) {
/* 498 */       j = paramByteBuffer.get();
/* 499 */       if (j == 66) {
/* 500 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 501 */         arrayOfByte[k] = ((byte)paramConstantPool.getIntAt(m));
/*     */       } else {
/* 503 */         skipMemberValue(j, paramByteBuffer);
/* 504 */         i = 1;
/*     */       }
/*     */     }
/* 507 */     return i != 0 ? exceptionProxy(j) : arrayOfByte;
/*     */   }
/*     */ 
/*     */   private static Object parseCharArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 512 */     char[] arrayOfChar = new char[paramInt];
/* 513 */     int i = 0;
/* 514 */     int j = 0;
/*     */ 
/* 516 */     for (int k = 0; k < paramInt; k++) {
/* 517 */       j = paramByteBuffer.get();
/* 518 */       if (j == 67) {
/* 519 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 520 */         arrayOfChar[k] = ((char)paramConstantPool.getIntAt(m));
/*     */       } else {
/* 522 */         skipMemberValue(j, paramByteBuffer);
/* 523 */         i = 1;
/*     */       }
/*     */     }
/* 526 */     return i != 0 ? exceptionProxy(j) : arrayOfChar;
/*     */   }
/*     */ 
/*     */   private static Object parseDoubleArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 531 */     double[] arrayOfDouble = new double[paramInt];
/* 532 */     int i = 0;
/* 533 */     int j = 0;
/*     */ 
/* 535 */     for (int k = 0; k < paramInt; k++) {
/* 536 */       j = paramByteBuffer.get();
/* 537 */       if (j == 68) {
/* 538 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 539 */         arrayOfDouble[k] = paramConstantPool.getDoubleAt(m);
/*     */       } else {
/* 541 */         skipMemberValue(j, paramByteBuffer);
/* 542 */         i = 1;
/*     */       }
/*     */     }
/* 545 */     return i != 0 ? exceptionProxy(j) : arrayOfDouble;
/*     */   }
/*     */ 
/*     */   private static Object parseFloatArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 550 */     float[] arrayOfFloat = new float[paramInt];
/* 551 */     int i = 0;
/* 552 */     int j = 0;
/*     */ 
/* 554 */     for (int k = 0; k < paramInt; k++) {
/* 555 */       j = paramByteBuffer.get();
/* 556 */       if (j == 70) {
/* 557 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 558 */         arrayOfFloat[k] = paramConstantPool.getFloatAt(m);
/*     */       } else {
/* 560 */         skipMemberValue(j, paramByteBuffer);
/* 561 */         i = 1;
/*     */       }
/*     */     }
/* 564 */     return i != 0 ? exceptionProxy(j) : arrayOfFloat;
/*     */   }
/*     */ 
/*     */   private static Object parseIntArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 569 */     int[] arrayOfInt = new int[paramInt];
/* 570 */     int i = 0;
/* 571 */     int j = 0;
/*     */ 
/* 573 */     for (int k = 0; k < paramInt; k++) {
/* 574 */       j = paramByteBuffer.get();
/* 575 */       if (j == 73) {
/* 576 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 577 */         arrayOfInt[k] = paramConstantPool.getIntAt(m);
/*     */       } else {
/* 579 */         skipMemberValue(j, paramByteBuffer);
/* 580 */         i = 1;
/*     */       }
/*     */     }
/* 583 */     return i != 0 ? exceptionProxy(j) : arrayOfInt;
/*     */   }
/*     */ 
/*     */   private static Object parseLongArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 588 */     long[] arrayOfLong = new long[paramInt];
/* 589 */     int i = 0;
/* 590 */     int j = 0;
/*     */ 
/* 592 */     for (int k = 0; k < paramInt; k++) {
/* 593 */       j = paramByteBuffer.get();
/* 594 */       if (j == 74) {
/* 595 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 596 */         arrayOfLong[k] = paramConstantPool.getLongAt(m);
/*     */       } else {
/* 598 */         skipMemberValue(j, paramByteBuffer);
/* 599 */         i = 1;
/*     */       }
/*     */     }
/* 602 */     return i != 0 ? exceptionProxy(j) : arrayOfLong;
/*     */   }
/*     */ 
/*     */   private static Object parseShortArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 607 */     short[] arrayOfShort = new short[paramInt];
/* 608 */     int i = 0;
/* 609 */     int j = 0;
/*     */ 
/* 611 */     for (int k = 0; k < paramInt; k++) {
/* 612 */       j = paramByteBuffer.get();
/* 613 */       if (j == 83) {
/* 614 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 615 */         arrayOfShort[k] = ((short)paramConstantPool.getIntAt(m));
/*     */       } else {
/* 617 */         skipMemberValue(j, paramByteBuffer);
/* 618 */         i = 1;
/*     */       }
/*     */     }
/* 621 */     return i != 0 ? exceptionProxy(j) : arrayOfShort;
/*     */   }
/*     */ 
/*     */   private static Object parseBooleanArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 626 */     boolean[] arrayOfBoolean = new boolean[paramInt];
/* 627 */     int i = 0;
/* 628 */     int j = 0;
/*     */ 
/* 630 */     for (int k = 0; k < paramInt; k++) {
/* 631 */       j = paramByteBuffer.get();
/* 632 */       if (j == 90) {
/* 633 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 634 */         arrayOfBoolean[k] = (paramConstantPool.getIntAt(m) != 0 ? 1 : false);
/*     */       } else {
/* 636 */         skipMemberValue(j, paramByteBuffer);
/* 637 */         i = 1;
/*     */       }
/*     */     }
/* 640 */     return i != 0 ? exceptionProxy(j) : arrayOfBoolean;
/*     */   }
/*     */ 
/*     */   private static Object parseStringArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 645 */     String[] arrayOfString = new String[paramInt];
/* 646 */     int i = 0;
/* 647 */     int j = 0;
/*     */ 
/* 649 */     for (int k = 0; k < paramInt; k++) {
/* 650 */       j = paramByteBuffer.get();
/* 651 */       if (j == 115) {
/* 652 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 653 */         arrayOfString[k] = paramConstantPool.getUTF8At(m);
/*     */       } else {
/* 655 */         skipMemberValue(j, paramByteBuffer);
/* 656 */         i = 1;
/*     */       }
/*     */     }
/* 659 */     return i != 0 ? exceptionProxy(j) : arrayOfString;
/*     */   }
/*     */ 
/*     */   private static Object parseClassArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 666 */     Class[] arrayOfClass = new Class[paramInt];
/* 667 */     int i = 0;
/* 668 */     int j = 0;
/*     */ 
/* 670 */     for (int k = 0; k < paramInt; k++) {
/* 671 */       j = paramByteBuffer.get();
/* 672 */       if (j == 99) {
/* 673 */         arrayOfClass[k] = parseClassValue(paramByteBuffer, paramConstantPool, paramClass);
/*     */       } else {
/* 675 */         skipMemberValue(j, paramByteBuffer);
/* 676 */         i = 1;
/*     */       }
/*     */     }
/* 679 */     return i != 0 ? exceptionProxy(j) : arrayOfClass;
/*     */   }
/*     */ 
/*     */   private static Object parseEnumArray(int paramInt, Class<? extends Enum> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
/*     */   {
/* 686 */     Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
/* 687 */     int i = 0;
/* 688 */     int j = 0;
/*     */ 
/* 690 */     for (int k = 0; k < paramInt; k++) {
/* 691 */       j = paramByteBuffer.get();
/* 692 */       if (j == 101) {
/* 693 */         arrayOfObject[k] = parseEnumValue(paramClass, paramByteBuffer, paramConstantPool, paramClass1);
/*     */       } else {
/* 695 */         skipMemberValue(j, paramByteBuffer);
/* 696 */         i = 1;
/*     */       }
/*     */     }
/* 699 */     return i != 0 ? exceptionProxy(j) : arrayOfObject;
/*     */   }
/*     */ 
/*     */   private static Object parseAnnotationArray(int paramInt, Class<? extends Annotation> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
/*     */   {
/* 707 */     Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
/* 708 */     int i = 0;
/* 709 */     int j = 0;
/*     */ 
/* 711 */     for (int k = 0; k < paramInt; k++) {
/* 712 */       j = paramByteBuffer.get();
/* 713 */       if (j == 64) {
/* 714 */         arrayOfObject[k] = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass1, true);
/*     */       } else {
/* 716 */         skipMemberValue(j, paramByteBuffer);
/* 717 */         i = 1;
/*     */       }
/*     */     }
/* 720 */     return i != 0 ? exceptionProxy(j) : arrayOfObject;
/*     */   }
/*     */ 
/*     */   private static ExceptionProxy exceptionProxy(int paramInt)
/*     */   {
/* 728 */     return new AnnotationTypeMismatchExceptionProxy("Array with component tag: " + paramInt);
/*     */   }
/*     */ 
/*     */   private static void skipAnnotation(ByteBuffer paramByteBuffer, boolean paramBoolean)
/*     */   {
/* 742 */     if (paramBoolean)
/* 743 */       paramByteBuffer.getShort();
/* 744 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 745 */     for (int j = 0; j < i; j++) {
/* 746 */       paramByteBuffer.getShort();
/* 747 */       skipMemberValue(paramByteBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void skipMemberValue(ByteBuffer paramByteBuffer)
/*     */   {
/* 757 */     int i = paramByteBuffer.get();
/* 758 */     skipMemberValue(i, paramByteBuffer);
/*     */   }
/*     */ 
/*     */   private static void skipMemberValue(int paramInt, ByteBuffer paramByteBuffer)
/*     */   {
/* 767 */     switch (paramInt) {
/*     */     case 101:
/* 769 */       paramByteBuffer.getInt();
/* 770 */       break;
/*     */     case 64:
/* 772 */       skipAnnotation(paramByteBuffer, true);
/* 773 */       break;
/*     */     case 91:
/* 775 */       skipArray(paramByteBuffer);
/* 776 */       break;
/*     */     default:
/* 779 */       paramByteBuffer.getShort();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void skipArray(ByteBuffer paramByteBuffer)
/*     */   {
/* 789 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 790 */     for (int j = 0; j < i; j++)
/* 791 */       skipMemberValue(paramByteBuffer);
/*     */   }
/*     */ 
/*     */   public static Annotation[] toArray(Map<Class<? extends Annotation>, Annotation> paramMap)
/*     */   {
/* 803 */     return (Annotation[])paramMap.values().toArray(EMPTY_ANNOTATION_ARRAY);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.annotation.AnnotationParser
 * JD-Core Version:    0.6.2
 */