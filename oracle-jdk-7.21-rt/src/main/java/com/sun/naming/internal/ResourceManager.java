/*     */ package com.sun.naming.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.naming.ConfigurationException;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.NamingEnumeration;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public final class ResourceManager
/*     */ {
/*     */   private static final String PROVIDER_RESOURCE_FILE_NAME = "jndiprovider.properties";
/*     */   private static final String APP_RESOURCE_FILE_NAME = "jndi.properties";
/*     */   private static final String JRELIB_PROPERTY_FILE_NAME = "jndi.properties";
/*  74 */   private static final String[] listProperties = { "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.factory.control" };
/*     */ 
/*  82 */   private static final VersionHelper helper = VersionHelper.getVersionHelper();
/*     */ 
/*  92 */   private static final WeakHashMap propertiesCache = new WeakHashMap(11);
/*     */ 
/* 102 */   private static final WeakHashMap factoryCache = new WeakHashMap(11);
/*     */ 
/* 113 */   private static final WeakHashMap urlFactoryCache = new WeakHashMap(11);
/* 114 */   private static final WeakReference NO_FACTORY = new WeakReference(null);
/*     */ 
/*     */   public static Hashtable getInitialEnvironment(Hashtable paramHashtable)
/*     */     throws NamingException
/*     */   {
/* 189 */     String[] arrayOfString1 = VersionHelper.PROPS;
/* 190 */     if (paramHashtable == null) {
/* 191 */       paramHashtable = new Hashtable(11);
/*     */     }
/* 193 */     Object localObject1 = paramHashtable.get("java.naming.applet");
/*     */ 
/* 202 */     String[] arrayOfString2 = helper.getJndiProperties();
/* 203 */     for (int i = 0; i < arrayOfString1.length; i++) {
/* 204 */       Object localObject2 = paramHashtable.get(arrayOfString1[i]);
/* 205 */       if (localObject2 == null) {
/* 206 */         if (localObject1 != null) {
/* 207 */           localObject2 = AppletParameter.get(localObject1, arrayOfString1[i]);
/*     */         }
/* 209 */         if (localObject2 == null)
/*     */         {
/* 211 */           localObject2 = arrayOfString2 != null ? arrayOfString2[i] : helper.getJndiProperty(i);
/*     */         }
/*     */ 
/* 215 */         if (localObject2 != null) {
/* 216 */           paramHashtable.put(arrayOfString1[i], localObject2);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 223 */     mergeTables(paramHashtable, getApplicationResources());
/* 224 */     return paramHashtable;
/*     */   }
/*     */ 
/*     */   public static String getProperty(String paramString, Hashtable paramHashtable, Context paramContext, boolean paramBoolean)
/*     */     throws NamingException
/*     */   {
/* 251 */     String str1 = paramHashtable != null ? (String)paramHashtable.get(paramString) : null;
/* 252 */     if ((paramContext == null) || ((str1 != null) && (!paramBoolean)))
/*     */     {
/* 254 */       return str1;
/*     */     }
/* 256 */     String str2 = (String)getProviderResource(paramContext).get(paramString);
/* 257 */     if (str1 == null)
/* 258 */       return str2;
/* 259 */     if ((str2 == null) || (!paramBoolean)) {
/* 260 */       return str1;
/*     */     }
/* 262 */     return str1 + ":" + str2;
/*     */   }
/*     */ 
/*     */   public static FactoryEnumeration getFactories(String paramString, Hashtable paramHashtable, Context paramContext)
/*     */     throws NamingException
/*     */   {
/* 311 */     String str1 = getProperty(paramString, paramHashtable, paramContext, true);
/* 312 */     if (str1 == null) {
/* 313 */       return null;
/*     */     }
/*     */ 
/* 316 */     ClassLoader localClassLoader = helper.getContextClassLoader();
/*     */ 
/* 318 */     Object localObject1 = null;
/* 319 */     synchronized (factoryCache) {
/* 320 */       localObject1 = (Map)factoryCache.get(localClassLoader);
/* 321 */       if (localObject1 == null) {
/* 322 */         localObject1 = new HashMap(11);
/* 323 */         factoryCache.put(localClassLoader, localObject1);
/*     */       }
/*     */     }
/*     */ 
/* 327 */     synchronized (localObject1) {
/* 328 */       Object localObject3 = (List)((Map)localObject1).get(str1);
/* 329 */       if (localObject3 != null)
/*     */       {
/* 331 */         return ((List)localObject3).size() == 0 ? null : new FactoryEnumeration((List)localObject3, localClassLoader);
/*     */       }
/*     */ 
/* 336 */       StringTokenizer localStringTokenizer = new StringTokenizer(str1, ":");
/* 337 */       localObject3 = new ArrayList(5);
/* 338 */       while (localStringTokenizer.hasMoreTokens()) {
/*     */         try
/*     */         {
/* 341 */           String str2 = localStringTokenizer.nextToken();
/* 342 */           Class localClass = helper.loadClass(str2, localClassLoader);
/* 343 */           ((List)localObject3).add(new NamedWeakReference(localClass, str2));
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/*     */         }
/*     */       }
/* 349 */       ((Map)localObject1).put(str1, localObject3);
/* 350 */       return new FactoryEnumeration((List)localObject3, localClassLoader);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Object getFactory(String paramString1, Hashtable paramHashtable, Context paramContext, String paramString2, String paramString3)
/*     */     throws NamingException
/*     */   {
/* 395 */     String str1 = getProperty(paramString1, paramHashtable, paramContext, true);
/* 396 */     if (str1 != null)
/* 397 */       str1 = str1 + ":" + paramString3;
/*     */     else {
/* 399 */       str1 = paramString3;
/*     */     }
/*     */ 
/* 403 */     ClassLoader localClassLoader = helper.getContextClassLoader();
/* 404 */     String str2 = paramString2 + " " + str1;
/*     */ 
/* 406 */     Object localObject1 = null;
/* 407 */     synchronized (urlFactoryCache) {
/* 408 */       localObject1 = (Map)urlFactoryCache.get(localClassLoader);
/* 409 */       if (localObject1 == null) {
/* 410 */         localObject1 = new HashMap(11);
/* 411 */         urlFactoryCache.put(localClassLoader, localObject1);
/*     */       }
/*     */     }
/*     */ 
/* 415 */     synchronized (localObject1) {
/* 416 */       Object localObject3 = null;
/*     */ 
/* 418 */       WeakReference localWeakReference = (WeakReference)((Map)localObject1).get(str2);
/* 419 */       if (localWeakReference == NO_FACTORY)
/* 420 */         return null;
/* 421 */       if (localWeakReference != null) {
/* 422 */         localObject3 = localWeakReference.get();
/* 423 */         if (localObject3 != null) {
/* 424 */           return localObject3;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 429 */       StringTokenizer localStringTokenizer = new StringTokenizer(str1, ":");
/*     */ 
/* 431 */       while ((localObject3 == null) && (localStringTokenizer.hasMoreTokens())) {
/* 432 */         String str3 = localStringTokenizer.nextToken() + paramString2;
/*     */         try
/*     */         {
/* 435 */           localObject3 = helper.loadClass(str3, localClassLoader).newInstance();
/*     */         } catch (InstantiationException localInstantiationException) {
/* 437 */           localNamingException = new NamingException("Cannot instantiate " + str3);
/*     */ 
/* 439 */           localNamingException.setRootCause(localInstantiationException);
/* 440 */           throw localNamingException;
/*     */         } catch (IllegalAccessException localIllegalAccessException) {
/* 442 */           NamingException localNamingException = new NamingException("Cannot access " + str3);
/*     */ 
/* 444 */           localNamingException.setRootCause(localIllegalAccessException);
/* 445 */           throw localNamingException;
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 453 */       ((Map)localObject1).put(str2, localObject3 != null ? new WeakReference(localObject3) : NO_FACTORY);
/*     */ 
/* 456 */       return localObject3;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Hashtable getProviderResource(Object paramObject)
/*     */     throws NamingException
/*     */   {
/* 474 */     if (paramObject == null) {
/* 475 */       return new Hashtable(1);
/*     */     }
/* 477 */     synchronized (propertiesCache) {
/* 478 */       Class localClass = paramObject.getClass();
/*     */ 
/* 480 */       Object localObject1 = (Hashtable)propertiesCache.get(localClass);
/* 481 */       if (localObject1 != null) {
/* 482 */         return localObject1;
/*     */       }
/* 484 */       localObject1 = new Properties();
/*     */ 
/* 486 */       InputStream localInputStream = helper.getResourceAsStream(localClass, "jndiprovider.properties");
/*     */ 
/* 489 */       if (localInputStream != null) {
/*     */         try {
/* 491 */           ((Properties)localObject1).load(localInputStream);
/*     */         } catch (IOException localIOException) {
/* 493 */           ConfigurationException localConfigurationException = new ConfigurationException("Error reading provider resource file for " + localClass);
/*     */ 
/* 495 */           localConfigurationException.setRootCause(localIOException);
/* 496 */           throw localConfigurationException;
/*     */         }
/*     */       }
/* 499 */       propertiesCache.put(localClass, localObject1);
/* 500 */       return localObject1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Hashtable getApplicationResources()
/*     */     throws NamingException
/*     */   {
/* 523 */     ClassLoader localClassLoader = helper.getContextClassLoader();
/*     */ 
/* 525 */     synchronized (propertiesCache) {
/* 526 */       Object localObject1 = (Hashtable)propertiesCache.get(localClassLoader);
/* 527 */       if (localObject1 != null) {
/* 528 */         return localObject1;
/*     */       }
/*     */       try
/*     */       {
/* 532 */         NamingEnumeration localNamingEnumeration = helper.getResources(localClassLoader, "jndi.properties");
/*     */ 
/* 534 */         while (localNamingEnumeration.hasMore()) {
/* 535 */           localObject2 = new Properties();
/* 536 */           ((Properties)localObject2).load((InputStream)localNamingEnumeration.next());
/*     */ 
/* 538 */           if (localObject1 == null)
/* 539 */             localObject1 = localObject2;
/*     */           else {
/* 541 */             mergeTables((Hashtable)localObject1, (Hashtable)localObject2);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 546 */         localObject2 = helper.getJavaHomeLibStream("jndi.properties");
/*     */ 
/* 548 */         if (localObject2 != null) {
/* 549 */           Properties localProperties = new Properties();
/* 550 */           localProperties.load((InputStream)localObject2);
/*     */ 
/* 552 */           if (localObject1 == null)
/* 553 */             localObject1 = localProperties;
/*     */           else
/* 555 */             mergeTables((Hashtable)localObject1, localProperties);
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 560 */         Object localObject2 = new ConfigurationException("Error reading application resource file");
/*     */ 
/* 562 */         ((NamingException)localObject2).setRootCause(localIOException);
/* 563 */         throw ((Throwable)localObject2);
/*     */       }
/* 565 */       if (localObject1 == null) {
/* 566 */         localObject1 = new Hashtable(11);
/*     */       }
/* 568 */       propertiesCache.put(localClassLoader, localObject1);
/* 569 */       return localObject1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void mergeTables(Hashtable paramHashtable1, Hashtable paramHashtable2)
/*     */   {
/* 581 */     Enumeration localEnumeration = paramHashtable2.keys();
/*     */ 
/* 583 */     while (localEnumeration.hasMoreElements()) {
/* 584 */       String str1 = (String)localEnumeration.nextElement();
/* 585 */       Object localObject = paramHashtable1.get(str1);
/* 586 */       if (localObject == null) {
/* 587 */         paramHashtable1.put(str1, paramHashtable2.get(str1));
/* 588 */       } else if (isListProperty(str1)) {
/* 589 */         String str2 = (String)paramHashtable2.get(str1);
/* 590 */         paramHashtable1.put(str1, (String)localObject + ":" + str2);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isListProperty(String paramString)
/*     */   {
/* 600 */     paramString = paramString.intern();
/* 601 */     for (int i = 0; i < listProperties.length; i++) {
/* 602 */       if (paramString == listProperties[i]) {
/* 603 */         return true;
/*     */       }
/*     */     }
/* 606 */     return false;
/*     */   }
/*     */ 
/*     */   private static class AppletParameter
/*     */   {
/* 121 */     private static final Class<?> clazz = getClass("java.applet.Applet");
/* 122 */     private static final Method getMethod = getMethod(clazz, "getParameter", new Class[] { String.class });
/*     */ 
/*     */     private static Class<?> getClass(String paramString) {
/*     */       try {
/* 126 */         return Class.forName(paramString, true, null); } catch (ClassNotFoundException localClassNotFoundException) {
/*     */       }
/* 128 */       return null;
/*     */     }
/*     */ 
/*     */     private static Method getMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
/*     */     {
/* 135 */       if (paramClass != null) {
/*     */         try {
/* 137 */           return paramClass.getMethod(paramString, paramArrayOfClass);
/*     */         } catch (NoSuchMethodException localNoSuchMethodException) {
/* 139 */           throw new AssertionError(localNoSuchMethodException);
/*     */         }
/*     */       }
/* 142 */       return null;
/*     */     }
/*     */ 
/*     */     static Object get(Object paramObject, String paramString)
/*     */     {
/* 151 */       if ((clazz == null) || (!clazz.isInstance(paramObject)))
/* 152 */         throw new ClassCastException(paramObject.getClass().getName());
/*     */       try {
/* 154 */         return getMethod.invoke(paramObject, new Object[] { paramString });
/*     */       } catch (InvocationTargetException localInvocationTargetException) {
/* 156 */         throw new AssertionError(localInvocationTargetException);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 158 */         throw new AssertionError(localIllegalAccessException);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.naming.internal.ResourceManager
 * JD-Core Version:    0.6.2
 */