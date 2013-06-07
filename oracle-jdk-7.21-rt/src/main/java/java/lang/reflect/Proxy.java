/*     */ package java.lang.reflect;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.security.AccessController;
/*     */ import java.security.Permission;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import sun.misc.ProxyGenerator;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ import sun.security.util.SecurityConstants;
/*     */ 
/*     */ public class Proxy
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2222568056686623797L;
/*     */   private static final String proxyClassNamePrefix = "$Proxy";
/* 236 */   private static final Class[] constructorParams = { InvocationHandler.class };
/*     */ 
/* 240 */   private static Map<ClassLoader, Map<List<String>, Object>> loaderToCache = new WeakHashMap();
/*     */ 
/* 244 */   private static Object pendingGenerationMarker = new Object();
/*     */ 
/* 247 */   private static long nextUniqueNumber = 0L;
/* 248 */   private static Object nextUniqueNumberLock = new Object();
/*     */ 
/* 251 */   private static Map<Class<?>, Void> proxyClasses = Collections.synchronizedMap(new WeakHashMap());
/*     */   protected InvocationHandler h;
/*     */ 
/*     */   private Proxy()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected Proxy(InvocationHandler paramInvocationHandler)
/*     */   {
/* 274 */     doNewInstanceCheck();
/* 275 */     this.h = paramInvocationHandler;
/*     */   }
/*     */ 
/*     */   private void doNewInstanceCheck()
/*     */   {
/* 325 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 326 */     Class localClass = getClass();
/* 327 */     if ((localSecurityManager != null) && (ProxyAccessHelper.needsNewInstanceCheck(localClass)))
/*     */       try {
/* 329 */         localSecurityManager.checkPermission(ProxyAccessHelper.PROXY_PERMISSION);
/*     */       } catch (SecurityException localSecurityException) {
/* 331 */         throw new SecurityException("Not allowed to construct a Proxy instance that implements a non-public interface", localSecurityException);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static Class<?> getProxyClass(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass)
/*     */     throws IllegalArgumentException
/*     */   {
/* 415 */     return getProxyClass0(paramClassLoader, paramArrayOfClass);
/*     */   }
/*     */ 
/*     */   private static void checkProxyLoader(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */   {
/* 421 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 422 */     if ((localSecurityManager != null) && 
/* 423 */       (paramClassLoader2 == null) && (paramClassLoader1 != null) && 
/* 424 */       (!ProxyAccessHelper.allowNullLoader))
/* 425 */       localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*     */   }
/*     */ 
/*     */   private static Class<?> getProxyClass0(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass)
/*     */   {
/* 451 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 452 */     if (localSecurityManager != null)
/*     */     {
/* 454 */       localObject1 = Reflection.getCallerClass(3);
/* 455 */       localObject2 = ((Class)localObject1).getClassLoader();
/* 456 */       checkProxyLoader((ClassLoader)localObject2, paramClassLoader);
/* 457 */       ReflectUtil.checkProxyPackageAccess((ClassLoader)localObject2, paramArrayOfClass);
/*     */     }
/*     */ 
/* 460 */     if (paramArrayOfClass.length > 65535) {
/* 461 */       throw new IllegalArgumentException("interface limit exceeded");
/*     */     }
/*     */ 
/* 464 */     Class localClass1 = null;
/*     */ 
/* 467 */     Object localObject1 = new String[paramArrayOfClass.length];
/*     */ 
/* 470 */     Object localObject2 = new HashSet();
/*     */     Object localObject3;
/* 472 */     for (int i = 0; i < paramArrayOfClass.length; i++)
/*     */     {
/* 477 */       localObject3 = paramArrayOfClass[i].getName();
/* 478 */       Class localClass2 = null;
/*     */       try {
/* 480 */         localClass2 = Class.forName((String)localObject3, false, paramClassLoader);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/*     */       }
/* 483 */       if (localClass2 != paramArrayOfClass[i]) {
/* 484 */         throw new IllegalArgumentException(paramArrayOfClass[i] + " is not visible from class loader");
/*     */       }
/*     */ 
/* 492 */       if (!localClass2.isInterface()) {
/* 493 */         throw new IllegalArgumentException(localClass2.getName() + " is not an interface");
/*     */       }
/*     */ 
/* 500 */       if (((Set)localObject2).contains(localClass2)) {
/* 501 */         throw new IllegalArgumentException("repeated interface: " + localClass2.getName());
/*     */       }
/*     */ 
/* 504 */       ((Set)localObject2).add(localClass2);
/*     */ 
/* 506 */       localObject1[i] = localObject3;
/*     */     }
/*     */ 
/* 518 */     List localList = Arrays.asList((Object[])localObject1);
/*     */ 
/* 524 */     synchronized (loaderToCache) {
/* 525 */       localObject3 = (Map)loaderToCache.get(paramClassLoader);
/* 526 */       if (localObject3 == null) {
/* 527 */         localObject3 = new HashMap();
/* 528 */         loaderToCache.put(paramClassLoader, localObject3);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 548 */     synchronized (localObject3)
/*     */     {
/*     */       while (true)
/*     */       {
/* 557 */         Object localObject4 = ((Map)localObject3).get(localList);
/* 558 */         if ((localObject4 instanceof Reference)) {
/* 559 */           localClass1 = (Class)((Reference)localObject4).get();
/*     */         }
/* 561 */         if (localClass1 != null)
/*     */         {
/* 563 */           return localClass1;
/* 564 */         }if (localObject4 != pendingGenerationMarker)
/*     */           break;
/*     */         try {
/* 567 */           localObject3.wait();
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 582 */       ((Map)localObject3).put(localList, pendingGenerationMarker);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 589 */       ??? = null;
/*     */ 
/* 596 */       for (int j = 0; j < paramArrayOfClass.length; j++) {
/* 597 */         int k = paramArrayOfClass[j].getModifiers();
/* 598 */         if (!Modifier.isPublic(k)) {
/* 599 */           String str1 = paramArrayOfClass[j].getName();
/* 600 */           int m = str1.lastIndexOf('.');
/* 601 */           String str2 = m == -1 ? "" : str1.substring(0, m + 1);
/* 602 */           if (??? == null)
/* 603 */             ??? = str2;
/* 604 */           else if (!str2.equals(???)) {
/* 605 */             throw new IllegalArgumentException("non-public interfaces from different packages");
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 611 */       if (??? == null)
/*     */       {
/* 613 */         ??? = "com.sun.proxy.";
/*     */       }
/*     */       long l;
/* 621 */       synchronized (nextUniqueNumberLock) {
/* 622 */         l = nextUniqueNumber++;
/*     */       }
/* 624 */       ??? = (String)??? + "$Proxy" + l;
/*     */ 
/* 633 */       byte[] arrayOfByte = ProxyGenerator.generateProxyClass((String)???, paramArrayOfClass);
/*     */       try
/*     */       {
/* 636 */         localClass1 = defineClass0(paramClassLoader, (String)???, arrayOfByte, 0, arrayOfByte.length);
/*     */       }
/*     */       catch (ClassFormatError localClassFormatError)
/*     */       {
/* 646 */         throw new IllegalArgumentException(localClassFormatError.toString());
/*     */       }
/*     */ 
/* 650 */       proxyClasses.put(localClass1, null);
/*     */     }
/*     */     finally
/*     */     {
/* 660 */       synchronized (localObject3) {
/* 661 */         if (localClass1 != null)
/* 662 */           ((Map)localObject3).put(localList, new WeakReference(localClass1));
/*     */         else {
/* 664 */           ((Map)localObject3).remove(localList);
/*     */         }
/* 666 */         localObject3.notifyAll();
/*     */       }
/*     */     }
/* 669 */     return localClass1;
/*     */   }
/*     */ 
/*     */   public static Object newProxyInstance(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass, InvocationHandler paramInvocationHandler)
/*     */     throws IllegalArgumentException
/*     */   {
/* 706 */     if (paramInvocationHandler == null) {
/* 707 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 713 */     Class localClass = getProxyClass0(paramClassLoader, paramArrayOfClass);
/*     */     try
/*     */     {
/* 719 */       Constructor localConstructor = localClass.getConstructor(constructorParams);
/* 720 */       final InvocationHandler localInvocationHandler = paramInvocationHandler;
/* 721 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 722 */       if ((localSecurityManager != null) && (ProxyAccessHelper.needsNewInstanceCheck(localClass)))
/*     */       {
/* 725 */         return AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public Object run() {
/* 727 */             return Proxy.newInstance(this.val$cons, localInvocationHandler);
/*     */           }
/*     */         });
/*     */       }
/* 731 */       return newInstance(localConstructor, localInvocationHandler);
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException) {
/* 734 */       throw new InternalError(localNoSuchMethodException.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Object newInstance(Constructor<?> paramConstructor, InvocationHandler paramInvocationHandler) {
/*     */     try {
/* 740 */       return paramConstructor.newInstance(new Object[] { paramInvocationHandler });
/*     */     } catch (IllegalAccessException|InstantiationException localIllegalAccessException) {
/* 742 */       throw new InternalError(localIllegalAccessException.toString());
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 744 */       Throwable localThrowable = localInvocationTargetException.getCause();
/* 745 */       if ((localThrowable instanceof RuntimeException)) {
/* 746 */         throw ((RuntimeException)localThrowable);
/*     */       }
/* 748 */       throw new InternalError(localThrowable.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isProxyClass(Class<?> paramClass)
/*     */   {
/* 768 */     if (paramClass == null) {
/* 769 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 772 */     return proxyClasses.containsKey(paramClass);
/*     */   }
/*     */ 
/*     */   public static InvocationHandler getInvocationHandler(Object paramObject)
/*     */     throws IllegalArgumentException
/*     */   {
/* 789 */     if (!isProxyClass(paramObject.getClass())) {
/* 790 */       throw new IllegalArgumentException("not a proxy instance");
/*     */     }
/*     */ 
/* 793 */     Proxy localProxy = (Proxy)paramObject;
/* 794 */     return localProxy.h;
/*     */   }
/*     */ 
/*     */   private static native Class defineClass0(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   private static class ProxyAccessHelper
/*     */   {
/* 280 */     static final Permission PROXY_PERMISSION = new ReflectPermission("proxyConstructorNewInstance");
/*     */ 
/* 287 */     static final boolean allowNewInstance = getBooleanProperty("sun.reflect.proxy.allowsNewInstance");
/* 288 */     static final boolean allowNullLoader = getBooleanProperty("sun.reflect.proxy.allowsNullLoader");
/*     */ 
/*     */     private static boolean getBooleanProperty(String paramString)
/*     */     {
/* 292 */       String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public String run() {
/* 294 */           return System.getProperty(this.val$key);
/*     */         }
/*     */       });
/* 297 */       return Boolean.valueOf(str).booleanValue();
/*     */     }
/*     */ 
/*     */     static boolean needsNewInstanceCheck(Class<?> paramClass) {
/* 301 */       if ((!Proxy.isProxyClass(paramClass)) || (allowNewInstance)) {
/* 302 */         return false;
/*     */       }
/*     */ 
/* 305 */       if (paramClass.getName().startsWith("com.sun.proxy."))
/*     */       {
/* 307 */         return false;
/*     */       }
/* 309 */       for (Class localClass : paramClass.getInterfaces()) {
/* 310 */         if (!Modifier.isPublic(localClass.getModifiers())) {
/* 311 */           return true;
/*     */         }
/*     */       }
/* 314 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.reflect.Proxy
 * JD-Core Version:    0.6.2
 */