/*     */ package com.sun.org.apache.xml.internal.serialize;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ final class ObjectFactory
/*     */ {
/*     */   private static final String DEFAULT_PROPERTIES_FILENAME = "xerces.properties";
/*     */   private static final boolean DEBUG = false;
/*     */   private static final int DEFAULT_LINE_LENGTH = 80;
/*  67 */   private static Properties fXercesProperties = null;
/*     */ 
/*  74 */   private static long fLastModified = -1L;
/*     */ 
/*     */   static Object createObject(String factoryId, String fallbackClassName)
/*     */     throws ObjectFactory.ConfigurationError
/*     */   {
/* 100 */     return createObject(factoryId, null, fallbackClassName);
/*     */   }
/*     */ 
/*     */   static Object createObject(String factoryId, String propertiesFilename, String fallbackClassName)
/*     */     throws ObjectFactory.ConfigurationError
/*     */   {
/* 132 */     SecuritySupport ss = SecuritySupport.getInstance();
/* 133 */     ClassLoader cl = findClassLoader();
/*     */     try
/*     */     {
/* 137 */       String systemProp = ss.getSystemProperty(factoryId);
/* 138 */       if (systemProp != null)
/*     */       {
/* 140 */         return newInstance(systemProp, cl, true);
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
/* 157 */     return newInstance(fallbackClassName, cl, true);
/*     */   }
/*     */ 
/*     */   private static void debugPrintln(String msg)
/*     */   {
/*     */   }
/*     */ 
/*     */   static ClassLoader findClassLoader()
/*     */     throws ObjectFactory.ConfigurationError
/*     */   {
/* 289 */     SecuritySupport ss = SecuritySupport.getInstance();
/*     */ 
/* 293 */     ClassLoader context = ss.getContextClassLoader();
/* 294 */     ClassLoader system = ss.getSystemClassLoader();
/*     */ 
/* 296 */     ClassLoader chain = system;
/*     */     while (true) {
/* 298 */       if (context == chain)
/*     */       {
/* 307 */         ClassLoader current = ObjectFactory.class.getClassLoader();
/*     */ 
/* 309 */         chain = system;
/*     */         while (true) {
/* 311 */           if (current == chain)
/*     */           {
/* 314 */             return system;
/*     */           }
/* 316 */           if (chain == null) {
/*     */             break;
/*     */           }
/* 319 */           chain = ss.getParentClassLoader(chain);
/*     */         }
/*     */ 
/* 324 */         return current;
/*     */       }
/*     */ 
/* 327 */       if (chain == null)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 334 */       chain = ss.getParentClassLoader(chain);
/*     */     }
/*     */ 
/* 339 */     return context;
/*     */   }
/*     */ 
/*     */   static Object newInstance(String className, ClassLoader cl, boolean doFallback)
/*     */     throws ObjectFactory.ConfigurationError
/*     */   {
/*     */     try
/*     */     {
/* 351 */       Class providerClass = findProviderClass(className, cl, doFallback);
/* 352 */       return providerClass.newInstance();
/*     */     }
/*     */     catch (ClassNotFoundException x)
/*     */     {
/* 357 */       throw new ConfigurationError("Provider " + className + " not found", x);
/*     */     }
/*     */     catch (Exception x) {
/* 360 */       throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x, x);
/*     */     }
/*     */   }
/*     */ 
/*     */   static Class findProviderClass(String className, ClassLoader cl, boolean doFallback)
/*     */     throws ClassNotFoundException, ObjectFactory.ConfigurationError
/*     */   {
/* 375 */     SecurityManager security = System.getSecurityManager();
/* 376 */     if (security != null) {
/* 377 */       int lastDot = className.lastIndexOf(".");
/* 378 */       String packageName = className;
/* 379 */       if (lastDot != -1) packageName = className.substring(0, lastDot);
/* 380 */       security.checkPackageAccess(packageName);
/*     */     }
/*     */     Class providerClass;
/* 383 */     if (cl == null)
/*     */     {
/* 393 */       providerClass = Class.forName(className);
/*     */     }
/*     */     else try {
/* 396 */         providerClass = cl.loadClass(className);
/*     */       }
/*     */       catch (ClassNotFoundException x)
/*     */       {
/*     */         Class providerClass;
/*     */         Class providerClass;
/* 398 */         if (doFallback)
/*     */         {
/* 400 */           ClassLoader current = ObjectFactory.class.getClassLoader();
/* 401 */           if (current == null) {
/* 402 */             providerClass = Class.forName(className);
/*     */           }
/*     */           else
/*     */           {
/*     */             Class providerClass;
/* 403 */             if (cl != current) {
/* 404 */               cl = current;
/* 405 */               providerClass = cl.loadClass(className);
/*     */             } else {
/* 407 */               throw x;
/*     */             }
/*     */           }
/*     */         } else { throw x; }
/*     */ 
/*     */       }
/*     */     Class providerClass;
/* 415 */     return providerClass;
/*     */   }
/*     */ 
/*     */   private static Object findJarServiceProvider(String factoryId)
/*     */     throws ObjectFactory.ConfigurationError
/*     */   {
/* 426 */     SecuritySupport ss = SecuritySupport.getInstance();
/* 427 */     String serviceId = "META-INF/services/" + factoryId;
/* 428 */     InputStream is = null;
/*     */ 
/* 431 */     ClassLoader cl = findClassLoader();
/*     */ 
/* 433 */     is = ss.getResourceAsStream(cl, serviceId);
/*     */ 
/* 436 */     if (is == null) {
/* 437 */       ClassLoader current = ObjectFactory.class.getClassLoader();
/* 438 */       if (cl != current) {
/* 439 */         cl = current;
/* 440 */         is = ss.getResourceAsStream(cl, serviceId);
/*     */       }
/*     */     }
/*     */ 
/* 444 */     if (is == null)
/*     */     {
/* 446 */       return null;
/*     */     }
/*     */ 
/*     */     BufferedReader rd;
/*     */     try
/*     */     {
/* 470 */       rd = new BufferedReader(new InputStreamReader(is, "UTF-8"), 80);
/*     */     } catch (UnsupportedEncodingException e) {
/* 472 */       rd = new BufferedReader(new InputStreamReader(is), 80);
/*     */     }
/*     */ 
/* 475 */     String factoryClassName = null;
/*     */     try
/*     */     {
/* 479 */       factoryClassName = rd.readLine();
/*     */     }
/*     */     catch (IOException x) {
/* 482 */       return null;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 487 */         rd.close();
/*     */       }
/*     */       catch (IOException exc)
/*     */       {
/*     */       }
/*     */     }
/* 493 */     if ((factoryClassName != null) && (!"".equals(factoryClassName)))
/*     */     {
/* 502 */       return newInstance(factoryClassName, cl, false);
/*     */     }
/*     */ 
/* 506 */     return null;
/*     */   }
/*     */ 
/*     */   static final class ConfigurationError extends Error
/*     */   {
/*     */     static final long serialVersionUID = 937647395548533254L;
/*     */     private Exception exception;
/*     */ 
/*     */     ConfigurationError(String msg, Exception x)
/*     */     {
/* 538 */       super();
/* 539 */       this.exception = x;
/*     */     }
/*     */ 
/*     */     Exception getException()
/*     */     {
/* 548 */       return this.exception;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.serialize.ObjectFactory
 * JD-Core Version:    0.6.2
 */