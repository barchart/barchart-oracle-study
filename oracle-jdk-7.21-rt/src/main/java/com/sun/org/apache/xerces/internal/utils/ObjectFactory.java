/*     */ package com.sun.org.apache.xerces.internal.utils;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public final class ObjectFactory
/*     */ {
/*     */   private static final String DEFAULT_INTERNAL_CLASSES = "com.sun.org.apache.";
/*     */   private static final String DEFAULT_PROPERTIES_FILENAME = "xerces.properties";
/*  57 */   private static final boolean DEBUG = isDebugEnabled();
/*     */   private static final int DEFAULT_LINE_LENGTH = 80;
/*  69 */   private static Properties fXercesProperties = null;
/*     */ 
/*  76 */   private static long fLastModified = -1L;
/*     */ 
/*     */   public static Object createObject(String factoryId, String fallbackClassName)
/*     */     throws ConfigurationError
/*     */   {
/* 102 */     return createObject(factoryId, null, fallbackClassName);
/*     */   }
/*     */ 
/*     */   public static Object createObject(String factoryId, String propertiesFilename, String fallbackClassName)
/*     */     throws ConfigurationError
/*     */   {
/* 132 */     if (DEBUG) debugPrintln("debug is on");
/*     */ 
/* 134 */     ClassLoader cl = findClassLoader();
/*     */     try
/*     */     {
/* 138 */       String systemProp = SecuritySupport.getSystemProperty(factoryId);
/* 139 */       if ((systemProp != null) && (systemProp.length() > 0)) {
/* 140 */         if (DEBUG) debugPrintln("found system property, value=" + systemProp);
/* 141 */         return newInstance(systemProp, cl, true);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SecurityException se)
/*     */     {
/*     */     }
/*     */ 
/* 151 */     if (fallbackClassName == null) {
/* 152 */       throw new ConfigurationError("Provider for " + factoryId + " cannot be found", null);
/*     */     }
/*     */ 
/* 156 */     if (DEBUG) debugPrintln("using fallback, value=" + fallbackClassName);
/* 157 */     return newInstance(fallbackClassName, cl, true);
/*     */   }
/*     */ 
/*     */   private static boolean isDebugEnabled()
/*     */   {
/*     */     try
/*     */     {
/* 168 */       String val = SecuritySupport.getSystemProperty("xerces.debug");
/*     */ 
/* 170 */       return (val != null) && (!"false".equals(val));
/*     */     } catch (SecurityException se) {
/*     */     }
/* 173 */     return false;
/*     */   }
/*     */ 
/*     */   private static void debugPrintln(String msg)
/*     */   {
/* 178 */     if (DEBUG)
/* 179 */       System.err.println("XERCES: " + msg);
/*     */   }
/*     */ 
/*     */   public static ClassLoader findClassLoader()
/*     */     throws ConfigurationError
/*     */   {
/* 190 */     if (System.getSecurityManager() != null)
/*     */     {
/* 192 */       return null;
/*     */     }
/*     */ 
/* 196 */     ClassLoader context = SecuritySupport.getContextClassLoader();
/* 197 */     ClassLoader system = SecuritySupport.getSystemClassLoader();
/*     */ 
/* 199 */     ClassLoader chain = system;
/*     */     while (true) {
/* 201 */       if (context == chain)
/*     */       {
/* 210 */         ClassLoader current = ObjectFactory.class.getClassLoader();
/*     */ 
/* 212 */         chain = system;
/*     */         while (true) {
/* 214 */           if (current == chain)
/*     */           {
/* 217 */             return system;
/*     */           }
/* 219 */           if (chain == null) {
/*     */             break;
/*     */           }
/* 222 */           chain = SecuritySupport.getParentClassLoader(chain);
/*     */         }
/*     */ 
/* 227 */         return current;
/*     */       }
/*     */ 
/* 230 */       if (chain == null)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 237 */       chain = SecuritySupport.getParentClassLoader(chain);
/*     */     }
/*     */ 
/* 242 */     return context;
/*     */   }
/*     */ 
/*     */   public static Object newInstance(String className, boolean doFallback)
/*     */     throws ConfigurationError
/*     */   {
/* 252 */     if (System.getSecurityManager() != null) {
/* 253 */       return newInstance(className, null, doFallback);
/*     */     }
/* 255 */     return newInstance(className, findClassLoader(), doFallback);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(String className, ClassLoader cl, boolean doFallback)
/*     */     throws ConfigurationError
/*     */   {
/*     */     try
/*     */     {
/* 269 */       Class providerClass = findProviderClass(className, cl, doFallback);
/* 270 */       Object instance = providerClass.newInstance();
/* 271 */       if (DEBUG) debugPrintln("created new instance of " + providerClass + " using ClassLoader: " + cl);
/*     */ 
/* 273 */       return instance;
/*     */     } catch (ClassNotFoundException x) {
/* 275 */       throw new ConfigurationError("Provider " + className + " not found", x);
/*     */     }
/*     */     catch (Exception x) {
/* 278 */       throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x, x);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Class findProviderClass(String className, boolean doFallback)
/*     */     throws ClassNotFoundException, ConfigurationError
/*     */   {
/* 291 */     if (System.getSecurityManager() != null) {
/* 292 */       return Class.forName(className);
/*     */     }
/* 294 */     return findProviderClass(className, findClassLoader(), doFallback);
/*     */   }
/*     */ 
/*     */   public static Class findProviderClass(String className, ClassLoader cl, boolean doFallback)
/*     */     throws ClassNotFoundException, ConfigurationError
/*     */   {
/* 307 */     SecurityManager security = System.getSecurityManager();
/* 308 */     if (security != null)
/* 309 */       if (className.startsWith("com.sun.org.apache.")) {
/* 310 */         cl = null;
/*     */       } else {
/* 312 */         int lastDot = className.lastIndexOf(".");
/* 313 */         String packageName = className;
/* 314 */         if (lastDot != -1) packageName = className.substring(0, lastDot);
/* 315 */         security.checkPackageAccess(packageName);
/*     */       }
/*     */     Class providerClass;
/* 319 */     if (cl == null)
/*     */     {
/* 321 */       providerClass = Class.forName(className);
/*     */     }
/*     */     else try {
/* 324 */         providerClass = cl.loadClass(className);
/*     */       }
/*     */       catch (ClassNotFoundException x)
/*     */       {
/*     */         Class providerClass;
/*     */         Class providerClass;
/* 326 */         if (doFallback)
/*     */         {
/* 328 */           ClassLoader current = ObjectFactory.class.getClassLoader();
/* 329 */           if (current == null) {
/* 330 */             providerClass = Class.forName(className);
/*     */           }
/*     */           else
/*     */           {
/*     */             Class providerClass;
/* 331 */             if (cl != current) {
/* 332 */               cl = current;
/* 333 */               providerClass = cl.loadClass(className);
/*     */             } else {
/* 335 */               throw x;
/*     */             }
/*     */           }
/*     */         } else { throw x; }
/*     */ 
/*     */       }
/*     */     Class providerClass;
/* 343 */     return providerClass;
/*     */   }
/*     */ 
/*     */   private static Object findJarServiceProvider(String factoryId)
/*     */     throws ConfigurationError
/*     */   {
/* 354 */     String serviceId = "META-INF/services/" + factoryId;
/* 355 */     InputStream is = null;
/*     */ 
/* 358 */     ClassLoader cl = findClassLoader();
/*     */ 
/* 360 */     is = SecuritySupport.getResourceAsStream(cl, serviceId);
/*     */ 
/* 363 */     if (is == null) {
/* 364 */       ClassLoader current = ObjectFactory.class.getClassLoader();
/* 365 */       if (cl != current) {
/* 366 */         cl = current;
/* 367 */         is = SecuritySupport.getResourceAsStream(cl, serviceId);
/*     */       }
/*     */     }
/*     */ 
/* 371 */     if (is == null)
/*     */     {
/* 373 */       return null;
/*     */     }
/*     */ 
/* 376 */     if (DEBUG) debugPrintln("found jar resource=" + serviceId + " using ClassLoader: " + cl);
/*     */ 
/*     */     BufferedReader rd;
/*     */     try
/*     */     {
/* 397 */       rd = new BufferedReader(new InputStreamReader(is, "UTF-8"), 80);
/*     */     } catch (UnsupportedEncodingException e) {
/* 399 */       rd = new BufferedReader(new InputStreamReader(is), 80);
/*     */     }
/*     */ 
/* 402 */     String factoryClassName = null;
/*     */     try
/*     */     {
/* 406 */       factoryClassName = rd.readLine();
/*     */     }
/*     */     catch (IOException x) {
/* 409 */       return null;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 414 */         rd.close();
/*     */       }
/*     */       catch (IOException exc)
/*     */       {
/*     */       }
/*     */     }
/* 420 */     if ((factoryClassName != null) && (!"".equals(factoryClassName)))
/*     */     {
/* 422 */       if (DEBUG) debugPrintln("found in resource, value=" + factoryClassName);
/*     */ 
/* 429 */       return newInstance(factoryClassName, cl, false);
/*     */     }
/*     */ 
/* 433 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.utils.ObjectFactory
 * JD-Core Version:    0.6.2
 */