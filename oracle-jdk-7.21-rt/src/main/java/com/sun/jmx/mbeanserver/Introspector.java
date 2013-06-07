/*     */ package com.sun.jmx.mbeanserver;
/*     */ 
/*     */ import com.sun.jmx.remote.util.EnvHelp;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.Descriptor;
/*     */ import javax.management.DescriptorKey;
/*     */ import javax.management.DynamicMBean;
/*     */ import javax.management.ImmutableDescriptor;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.NotCompliantMBeanException;
/*     */ import javax.management.openmbean.CompositeData;
/*     */ import sun.reflect.misc.MethodUtil;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public class Introspector
/*     */ {
/*     */   public static final boolean isDynamic(Class<?> paramClass)
/*     */   {
/* 112 */     return DynamicMBean.class.isAssignableFrom(paramClass);
/*     */   }
/*     */ 
/*     */   public static void testCreation(Class<?> paramClass)
/*     */     throws NotCompliantMBeanException
/*     */   {
/* 132 */     int i = paramClass.getModifiers();
/* 133 */     if ((Modifier.isAbstract(i)) || (Modifier.isInterface(i))) {
/* 134 */       throw new NotCompliantMBeanException("MBean class must be concrete");
/*     */     }
/*     */ 
/* 138 */     Constructor[] arrayOfConstructor = paramClass.getConstructors();
/* 139 */     if (arrayOfConstructor.length == 0)
/* 140 */       throw new NotCompliantMBeanException("MBean class must have public constructor");
/*     */   }
/*     */ 
/*     */   public static void checkCompliance(Class<?> paramClass)
/*     */     throws NotCompliantMBeanException
/*     */   {
/* 148 */     if (DynamicMBean.class.isAssignableFrom(paramClass)) {
/* 149 */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 154 */       getStandardMBeanInterface(paramClass);
/* 155 */       return;
/*     */     } catch (NotCompliantMBeanException localNotCompliantMBeanException2) {
/* 157 */       NotCompliantMBeanException localNotCompliantMBeanException1 = localNotCompliantMBeanException2;
/*     */       try
/*     */       {
/* 163 */         getMXBeanInterface(paramClass);
/* 164 */         return;
/*     */       } catch (NotCompliantMBeanException localNotCompliantMBeanException3) {
/* 166 */         Object localObject = localNotCompliantMBeanException3;
/*     */ 
/* 168 */         String str = "MBean class " + paramClass.getName() + " does not implement " + "DynamicMBean, and neither follows the Standard MBean conventions (" + localNotCompliantMBeanException1.toString() + ") nor the MXBean conventions (" + localObject.toString() + ")";
/*     */ 
/* 173 */         throw new NotCompliantMBeanException(str);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 178 */   public static <T> DynamicMBean makeDynamicMBean(T paramT) throws NotCompliantMBeanException { if ((paramT instanceof DynamicMBean))
/* 179 */       return (DynamicMBean)paramT;
/* 180 */     Class localClass1 = paramT.getClass();
/* 181 */     Class localClass2 = null;
/*     */     try {
/* 183 */       localClass2 = (Class)Util.cast(getStandardMBeanInterface(localClass1));
/*     */     }
/*     */     catch (NotCompliantMBeanException localNotCompliantMBeanException1)
/*     */     {
/*     */     }
/* 188 */     if (localClass2 != null)
/* 189 */       return new StandardMBeanSupport(paramT, localClass2);
/*     */     try
/*     */     {
/* 192 */       localClass2 = (Class)Util.cast(getMXBeanInterface(localClass1));
/*     */     }
/*     */     catch (NotCompliantMBeanException localNotCompliantMBeanException2)
/*     */     {
/*     */     }
/*     */ 
/* 198 */     if (localClass2 != null)
/* 199 */       return new MXBeanSupport(paramT, localClass2);
/* 200 */     checkCompliance(localClass1);
/* 201 */     throw new NotCompliantMBeanException("Not compliant");
/*     */   }
/*     */ 
/*     */   public static MBeanInfo testCompliance(Class<?> paramClass)
/*     */     throws NotCompliantMBeanException
/*     */   {
/* 222 */     if (isDynamic(paramClass)) {
/* 223 */       return null;
/*     */     }
/* 225 */     return testCompliance(paramClass, null);
/*     */   }
/*     */ 
/*     */   public static void testComplianceMXBeanInterface(Class<?> paramClass) throws NotCompliantMBeanException
/*     */   {
/* 230 */     MXBeanIntrospector.getInstance().getAnalyzer(paramClass);
/*     */   }
/*     */ 
/*     */   public static synchronized MBeanInfo testCompliance(Class<?> paramClass1, Class<?> paramClass2)
/*     */     throws NotCompliantMBeanException
/*     */   {
/* 251 */     if (paramClass2 == null)
/* 252 */       paramClass2 = getStandardMBeanInterface(paramClass1);
/* 253 */     StandardMBeanIntrospector localStandardMBeanIntrospector = StandardMBeanIntrospector.getInstance();
/* 254 */     return getClassMBeanInfo(localStandardMBeanIntrospector, paramClass1, paramClass2);
/*     */   }
/*     */ 
/*     */   private static <M> MBeanInfo getClassMBeanInfo(MBeanIntrospector<M> paramMBeanIntrospector, Class<?> paramClass1, Class<?> paramClass2)
/*     */     throws NotCompliantMBeanException
/*     */   {
/* 261 */     PerInterface localPerInterface = paramMBeanIntrospector.getPerInterface(paramClass2);
/* 262 */     return paramMBeanIntrospector.getClassMBeanInfo(paramClass1, localPerInterface);
/*     */   }
/*     */ 
/*     */   public static Class<?> getMBeanInterface(Class<?> paramClass)
/*     */   {
/* 279 */     if (isDynamic(paramClass)) return null; try
/*     */     {
/* 281 */       return getStandardMBeanInterface(paramClass); } catch (NotCompliantMBeanException localNotCompliantMBeanException) {
/*     */     }
/* 283 */     return null;
/*     */   }
/*     */ 
/*     */   public static <T> Class<? super T> getStandardMBeanInterface(Class<T> paramClass)
/*     */     throws NotCompliantMBeanException
/*     */   {
/* 299 */     Object localObject = paramClass;
/* 300 */     Class localClass = null;
/* 301 */     while (localObject != null) {
/* 302 */       localClass = findMBeanInterface((Class)localObject, ((Class)localObject).getName());
/*     */ 
/* 304 */       if (localClass != null) break;
/* 305 */       localObject = ((Class)localObject).getSuperclass();
/*     */     }
/* 307 */     if (localClass != null) {
/* 308 */       return localClass;
/*     */     }
/* 310 */     String str = "Class " + paramClass.getName() + " is not a JMX compliant Standard MBean";
/*     */ 
/* 313 */     throw new NotCompliantMBeanException(str);
/*     */   }
/*     */ 
/*     */   public static <T> Class<? super T> getMXBeanInterface(Class<T> paramClass)
/*     */     throws NotCompliantMBeanException
/*     */   {
/*     */     try
/*     */     {
/* 330 */       return MXBeanSupport.findMXBeanInterface(paramClass);
/*     */     } catch (Exception localException) {
/* 332 */       throw throwException(paramClass, localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static <T> Class<? super T> findMBeanInterface(Class<T> paramClass, String paramString)
/*     */   {
/* 349 */     Object localObject = paramClass;
/* 350 */     while (localObject != null) {
/* 351 */       Class[] arrayOfClass = ((Class)localObject).getInterfaces();
/* 352 */       int i = arrayOfClass.length;
/* 353 */       for (int j = 0; j < i; j++) {
/* 354 */         Class localClass = (Class)Util.cast(arrayOfClass[j]);
/* 355 */         localClass = implementsMBean(localClass, paramString);
/* 356 */         if (localClass != null) return localClass;
/*     */       }
/* 358 */       localObject = ((Class)localObject).getSuperclass();
/*     */     }
/* 360 */     return null;
/*     */   }
/*     */ 
/*     */   public static Descriptor descriptorForElement(AnnotatedElement paramAnnotatedElement) {
/* 364 */     if (paramAnnotatedElement == null)
/* 365 */       return ImmutableDescriptor.EMPTY_DESCRIPTOR;
/* 366 */     Annotation[] arrayOfAnnotation = paramAnnotatedElement.getAnnotations();
/* 367 */     return descriptorForAnnotations(arrayOfAnnotation);
/*     */   }
/*     */ 
/*     */   public static Descriptor descriptorForAnnotations(Annotation[] paramArrayOfAnnotation) {
/* 371 */     if (paramArrayOfAnnotation.length == 0)
/* 372 */       return ImmutableDescriptor.EMPTY_DESCRIPTOR;
/* 373 */     HashMap localHashMap = new HashMap();
/* 374 */     for (Annotation localAnnotation : paramArrayOfAnnotation) {
/* 375 */       Class localClass = localAnnotation.annotationType();
/* 376 */       Method[] arrayOfMethod1 = localClass.getMethods();
/* 377 */       for (Method localMethod : arrayOfMethod1) {
/* 378 */         DescriptorKey localDescriptorKey = (DescriptorKey)localMethod.getAnnotation(DescriptorKey.class);
/* 379 */         if (localDescriptorKey != null) {
/* 380 */           String str1 = localDescriptorKey.value();
/*     */           try
/*     */           {
/* 383 */             localObject1 = localMethod.invoke(localAnnotation, new Object[0]);
/*     */           }
/*     */           catch (RuntimeException localRuntimeException)
/*     */           {
/* 390 */             throw localRuntimeException;
/*     */           }
/*     */           catch (Exception localException) {
/* 393 */             throw new UndeclaredThrowableException(localException);
/*     */           }
/* 395 */           Object localObject1 = annotationToField(localObject1);
/* 396 */           Object localObject2 = localHashMap.put(str1, localObject1);
/* 397 */           if ((localObject2 != null) && (!equals(localObject2, localObject1))) {
/* 398 */             String str2 = "Inconsistent values for descriptor field " + str1 + " from annotations: " + localObject1 + " :: " + localObject2;
/*     */ 
/* 401 */             throw new IllegalArgumentException(str2);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 407 */     if (localHashMap.isEmpty()) {
/* 408 */       return ImmutableDescriptor.EMPTY_DESCRIPTOR;
/*     */     }
/* 410 */     return new ImmutableDescriptor(localHashMap);
/*     */   }
/*     */ 
/*     */   static NotCompliantMBeanException throwException(Class<?> paramClass, Throwable paramThrowable)
/*     */     throws NotCompliantMBeanException, SecurityException
/*     */   {
/* 427 */     if ((paramThrowable instanceof SecurityException))
/* 428 */       throw ((SecurityException)paramThrowable);
/* 429 */     if ((paramThrowable instanceof NotCompliantMBeanException))
/* 430 */       throw ((NotCompliantMBeanException)paramThrowable);
/* 431 */     String str1 = paramClass == null ? "null class" : paramClass.getName();
/*     */ 
/* 433 */     String str2 = paramThrowable == null ? "Not compliant" : paramThrowable.getMessage();
/*     */ 
/* 435 */     NotCompliantMBeanException localNotCompliantMBeanException = new NotCompliantMBeanException(str1 + ": " + str2);
/*     */ 
/* 437 */     localNotCompliantMBeanException.initCause(paramThrowable);
/* 438 */     throw localNotCompliantMBeanException;
/*     */   }
/*     */ 
/*     */   private static Object annotationToField(Object paramObject)
/*     */   {
/* 446 */     if (paramObject == null)
/* 447 */       return null;
/* 448 */     if (((paramObject instanceof Number)) || ((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof Boolean)) || ((paramObject instanceof String[])))
/*     */     {
/* 451 */       return paramObject;
/*     */     }
/*     */ 
/* 454 */     Class localClass = paramObject.getClass();
/* 455 */     if (localClass.isArray()) {
/* 456 */       if (localClass.getComponentType().isPrimitive())
/* 457 */         return paramObject;
/* 458 */       Object[] arrayOfObject = (Object[])paramObject;
/* 459 */       String[] arrayOfString = new String[arrayOfObject.length];
/* 460 */       for (int i = 0; i < arrayOfObject.length; i++)
/* 461 */         arrayOfString[i] = ((String)annotationToField(arrayOfObject[i]));
/* 462 */       return arrayOfString;
/*     */     }
/* 464 */     if ((paramObject instanceof Class))
/* 465 */       return ((Class)paramObject).getName();
/* 466 */     if ((paramObject instanceof Enum)) {
/* 467 */       return ((Enum)paramObject).name();
/*     */     }
/*     */ 
/* 474 */     if (Proxy.isProxyClass(localClass))
/* 475 */       localClass = localClass.getInterfaces()[0];
/* 476 */     throw new IllegalArgumentException("Illegal type for annotation element using @DescriptorKey: " + localClass.getName());
/*     */   }
/*     */ 
/*     */   private static boolean equals(Object paramObject1, Object paramObject2)
/*     */   {
/* 484 */     return Arrays.deepEquals(new Object[] { paramObject1 }, new Object[] { paramObject2 });
/*     */   }
/*     */ 
/*     */   private static <T> Class<? super T> implementsMBean(Class<T> paramClass, String paramString)
/*     */   {
/* 494 */     String str = paramString + "MBean";
/* 495 */     if (paramClass.getName().equals(str)) {
/* 496 */       return paramClass;
/*     */     }
/* 498 */     Class[] arrayOfClass = paramClass.getInterfaces();
/* 499 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 500 */       if (arrayOfClass[i].getName().equals(str)) {
/* 501 */         return (Class)Util.cast(arrayOfClass[i]);
/*     */       }
/*     */     }
/* 504 */     return null;
/*     */   }
/*     */ 
/*     */   public static Object elementFromComplex(Object paramObject, String paramString) throws AttributeNotFoundException
/*     */   {
/*     */     try {
/* 510 */       if ((paramObject.getClass().isArray()) && (paramString.equals("length")))
/* 511 */         return Integer.valueOf(Array.getLength(paramObject));
/* 512 */       if ((paramObject instanceof CompositeData)) {
/* 513 */         return ((CompositeData)paramObject).get(paramString);
/*     */       }
/*     */ 
/* 517 */       Class localClass = paramObject.getClass();
/* 518 */       Method localMethod = null;
/* 519 */       if (BeansHelper.isAvailable()) {
/* 520 */         Object localObject1 = BeansHelper.getBeanInfo(localClass);
/* 521 */         Object[] arrayOfObject1 = BeansHelper.getPropertyDescriptors(localObject1);
/* 522 */         for (Object localObject2 : arrayOfObject1) {
/* 523 */           if (BeansHelper.getPropertyName(localObject2).equals(paramString)) {
/* 524 */             localMethod = BeansHelper.getReadMethod(localObject2);
/* 525 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 531 */         localMethod = SimpleIntrospector.getReadMethod(localClass, paramString);
/*     */       }
/* 533 */       if (localMethod != null) {
/* 534 */         ReflectUtil.checkPackageAccess(localMethod.getDeclaringClass());
/* 535 */         return MethodUtil.invoke(localMethod, paramObject, new Class[0]);
/*     */       }
/*     */ 
/* 538 */       throw new AttributeNotFoundException("Could not find the getter method for the property " + paramString + " using the Java Beans introspector");
/*     */     }
/*     */     catch (InvocationTargetException localInvocationTargetException)
/*     */     {
/* 543 */       throw new IllegalArgumentException(localInvocationTargetException);
/*     */     } catch (AttributeNotFoundException localAttributeNotFoundException) {
/* 545 */       throw localAttributeNotFoundException;
/*     */     } catch (Exception localException) {
/* 547 */       throw ((AttributeNotFoundException)EnvHelp.initCause(new AttributeNotFoundException(localException.getMessage()), localException));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class BeansHelper
/*     */   {
/* 671 */     private static final Class<?> introspectorClass = getClass("java.beans.Introspector");
/*     */ 
/* 673 */     private static final Class<?> beanInfoClass = introspectorClass == null ? null : getClass("java.beans.BeanInfo");
/*     */ 
/* 675 */     private static final Class<?> getPropertyDescriptorClass = beanInfoClass == null ? null : getClass("java.beans.PropertyDescriptor");
/*     */ 
/* 678 */     private static final Method getBeanInfo = getMethod(introspectorClass, "getBeanInfo", new Class[] { Class.class });
/*     */ 
/* 680 */     private static final Method getPropertyDescriptors = getMethod(beanInfoClass, "getPropertyDescriptors", new Class[0]);
/*     */ 
/* 682 */     private static final Method getPropertyName = getMethod(getPropertyDescriptorClass, "getName", new Class[0]);
/*     */ 
/* 684 */     private static final Method getReadMethod = getMethod(getPropertyDescriptorClass, "getReadMethod", new Class[0]);
/*     */ 
/*     */     private static Class<?> getClass(String paramString)
/*     */     {
/*     */       try {
/* 689 */         return Class.forName(paramString, true, null); } catch (ClassNotFoundException localClassNotFoundException) {
/*     */       }
/* 691 */       return null;
/*     */     }
/*     */ 
/*     */     private static Method getMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
/*     */     {
/* 698 */       if (paramClass != null) {
/*     */         try {
/* 700 */           return paramClass.getMethod(paramString, paramArrayOfClass);
/*     */         } catch (NoSuchMethodException localNoSuchMethodException) {
/* 702 */           throw new AssertionError(localNoSuchMethodException);
/*     */         }
/*     */       }
/* 705 */       return null;
/*     */     }
/*     */ 
/*     */     static boolean isAvailable()
/*     */     {
/* 715 */       return introspectorClass != null;
/*     */     }
/*     */ 
/*     */     static Object getBeanInfo(Class<?> paramClass)
/*     */       throws Exception
/*     */     {
/*     */       try
/*     */       {
/* 723 */         return getBeanInfo.invoke(null, new Object[] { paramClass });
/*     */       } catch (InvocationTargetException localInvocationTargetException) {
/* 725 */         Throwable localThrowable = localInvocationTargetException.getCause();
/* 726 */         if ((localThrowable instanceof Exception))
/* 727 */           throw ((Exception)localThrowable);
/* 728 */         throw new AssertionError(localInvocationTargetException);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 730 */         throw new AssertionError(localIllegalAccessException);
/*     */       }
/*     */     }
/*     */ 
/*     */     static Object[] getPropertyDescriptors(Object paramObject)
/*     */     {
/*     */       try
/*     */       {
/* 739 */         return (Object[])getPropertyDescriptors.invoke(paramObject, new Object[0]);
/*     */       } catch (InvocationTargetException localInvocationTargetException) {
/* 741 */         Throwable localThrowable = localInvocationTargetException.getCause();
/* 742 */         if ((localThrowable instanceof RuntimeException))
/* 743 */           throw ((RuntimeException)localThrowable);
/* 744 */         throw new AssertionError(localInvocationTargetException);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 746 */         throw new AssertionError(localIllegalAccessException);
/*     */       }
/*     */     }
/*     */ 
/*     */     static String getPropertyName(Object paramObject)
/*     */     {
/*     */       try
/*     */       {
/* 755 */         return (String)getPropertyName.invoke(paramObject, new Object[0]);
/*     */       } catch (InvocationTargetException localInvocationTargetException) {
/* 757 */         Throwable localThrowable = localInvocationTargetException.getCause();
/* 758 */         if ((localThrowable instanceof RuntimeException))
/* 759 */           throw ((RuntimeException)localThrowable);
/* 760 */         throw new AssertionError(localInvocationTargetException);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 762 */         throw new AssertionError(localIllegalAccessException);
/*     */       }
/*     */     }
/*     */ 
/*     */     static Method getReadMethod(Object paramObject)
/*     */     {
/*     */       try
/*     */       {
/* 771 */         return (Method)getReadMethod.invoke(paramObject, new Object[0]);
/*     */       } catch (InvocationTargetException localInvocationTargetException) {
/* 773 */         Throwable localThrowable = localInvocationTargetException.getCause();
/* 774 */         if ((localThrowable instanceof RuntimeException))
/* 775 */           throw ((RuntimeException)localThrowable);
/* 776 */         throw new AssertionError(localInvocationTargetException);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 778 */         throw new AssertionError(localIllegalAccessException);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SimpleIntrospector
/*     */   {
/*     */     private static final String GET_METHOD_PREFIX = "get";
/*     */     private static final String IS_METHOD_PREFIX = "is";
/* 565 */     private static final Map<Class<?>, SoftReference<List<Method>>> cache = Collections.synchronizedMap(new WeakHashMap());
/*     */ 
/*     */     private static List<Method> getCachedMethods(Class<?> paramClass)
/*     */     {
/* 575 */       SoftReference localSoftReference = (SoftReference)cache.get(paramClass);
/* 576 */       if (localSoftReference != null) {
/* 577 */         List localList = (List)localSoftReference.get();
/* 578 */         if (localList != null)
/* 579 */           return localList;
/*     */       }
/* 581 */       return null;
/*     */     }
/*     */ 
/*     */     static boolean isReadMethod(Method paramMethod)
/*     */     {
/* 591 */       int i = paramMethod.getModifiers();
/* 592 */       if (Modifier.isStatic(i)) {
/* 593 */         return false;
/*     */       }
/* 595 */       String str = paramMethod.getName();
/* 596 */       Class[] arrayOfClass = paramMethod.getParameterTypes();
/* 597 */       int j = arrayOfClass.length;
/*     */ 
/* 599 */       if ((j == 0) && (str.length() > 2))
/*     */       {
/* 601 */         if (str.startsWith("is")) {
/* 602 */           return paramMethod.getReturnType() == Boolean.TYPE;
/*     */         }
/* 604 */         if ((str.length() > 3) && (str.startsWith("get")))
/* 605 */           return paramMethod.getReturnType() != Void.TYPE;
/*     */       }
/* 607 */       return false;
/*     */     }
/*     */ 
/*     */     static List<Method> getReadMethods(Class<?> paramClass)
/*     */     {
/* 617 */       List localList1 = getCachedMethods(paramClass);
/* 618 */       if (localList1 != null) {
/* 619 */         return localList1;
/*     */       }
/*     */ 
/* 623 */       List localList2 = StandardMBeanIntrospector.getInstance().getMethods(paramClass);
/*     */ 
/* 625 */       localList2 = MBeanAnalyzer.eliminateCovariantMethods(localList2);
/*     */ 
/* 628 */       LinkedList localLinkedList = new LinkedList();
/* 629 */       for (Method localMethod : localList2) {
/* 630 */         if (isReadMethod(localMethod))
/*     */         {
/* 632 */           if (localMethod.getName().startsWith("is"))
/* 633 */             localLinkedList.add(0, localMethod);
/*     */           else {
/* 635 */             localLinkedList.add(localMethod);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 641 */       cache.put(paramClass, new SoftReference(localLinkedList));
/*     */ 
/* 643 */       return localLinkedList;
/*     */     }
/*     */ 
/*     */     static Method getReadMethod(Class<?> paramClass, String paramString)
/*     */     {
/* 652 */       paramString = paramString.substring(0, 1).toUpperCase(Locale.ENGLISH) + paramString.substring(1);
/*     */ 
/* 654 */       String str1 = "get" + paramString;
/* 655 */       String str2 = "is" + paramString;
/* 656 */       for (Method localMethod : getReadMethods(paramClass)) {
/* 657 */         String str3 = localMethod.getName();
/* 658 */         if ((str3.equals(str2)) || (str3.equals(str1))) {
/* 659 */           return localMethod;
/*     */         }
/*     */       }
/* 662 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.Introspector
 * JD-Core Version:    0.6.2
 */