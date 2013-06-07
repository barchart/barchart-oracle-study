/*     */ package javax.xml.stream;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ class FactoryFinder
/*     */ {
/*     */   private static final String DEFAULT_PACKAGE = "com.sun.xml.internal.";
/*  50 */   private static boolean debug = false;
/*     */ 
/*  55 */   static Properties cacheProps = new Properties();
/*     */ 
/*  61 */   static volatile boolean firstTime = true;
/*     */ 
/*  67 */   static SecuritySupport ss = new SecuritySupport();
/*     */ 
/*     */   private static void dPrint(String msg)
/*     */   {
/*  84 */     if (debug)
/*  85 */       System.err.println("JAXP: " + msg);
/*     */   }
/*     */ 
/*     */   private static Class getProviderClass(String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 104 */       if (cl == null) {
/* 105 */         if (useBSClsLoader) {
/* 106 */           return Class.forName(className, true, FactoryFinder.class.getClassLoader());
/*     */         }
/* 108 */         cl = ss.getContextClassLoader();
/* 109 */         if (cl == null) {
/* 110 */           throw new ClassNotFoundException();
/*     */         }
/*     */ 
/* 113 */         return cl.loadClass(className);
/*     */       }
/*     */ 
/* 118 */       return cl.loadClass(className);
/*     */     }
/*     */     catch (ClassNotFoundException e1)
/*     */     {
/* 122 */       if (doFallback)
/*     */       {
/* 124 */         return Class.forName(className, true, FactoryFinder.class.getClassLoader());
/*     */       }
/*     */ 
/* 127 */       throw e1;
/*     */     }
/*     */   }
/*     */ 
/*     */   static Object newInstance(String className, ClassLoader cl, boolean doFallback)
/*     */     throws FactoryFinder.ConfigurationError
/*     */   {
/* 148 */     return newInstance(className, cl, doFallback, false);
/*     */   }
/*     */ 
/*     */   static Object newInstance(String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader)
/*     */     throws FactoryFinder.ConfigurationError
/*     */   {
/* 171 */     if ((System.getSecurityManager() != null) && 
/* 172 */       (className != null) && (className.startsWith("com.sun.xml.internal."))) {
/* 173 */       cl = null;
/* 174 */       useBSClsLoader = true;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 179 */       Class providerClass = getProviderClass(className, cl, doFallback, useBSClsLoader);
/* 180 */       Object instance = providerClass.newInstance();
/* 181 */       if (debug) {
/* 182 */         dPrint("created new instance of " + providerClass + " using ClassLoader: " + cl);
/*     */       }
/*     */ 
/* 185 */       return instance;
/*     */     }
/*     */     catch (ClassNotFoundException x) {
/* 188 */       throw new ConfigurationError("Provider " + className + " not found", x);
/*     */     }
/*     */     catch (Exception x)
/*     */     {
/* 192 */       throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x, x);
/*     */     }
/*     */   }
/*     */ 
/*     */   static Object find(String factoryId, String fallbackClassName)
/*     */     throws FactoryFinder.ConfigurationError
/*     */   {
/* 213 */     return find(factoryId, null, fallbackClassName);
/*     */   }
/*     */ 
/*     */   static Object find(String factoryId, ClassLoader cl, String fallbackClassName)
/*     */     throws FactoryFinder.ConfigurationError
/*     */   {
/* 235 */     dPrint("find factoryId =" + factoryId);
/*     */     try
/*     */     {
/* 239 */       String systemProp = ss.getSystemProperty(factoryId);
/* 240 */       if (systemProp != null) {
/* 241 */         dPrint("found system property, value=" + systemProp);
/* 242 */         return newInstance(systemProp, null, true);
/*     */       }
/*     */     }
/*     */     catch (SecurityException se) {
/* 246 */       if (debug) se.printStackTrace();
/*     */ 
/*     */     }
/*     */ 
/* 251 */     String configFile = null;
/*     */     try {
/* 253 */       String factoryClassName = null;
/* 254 */       if (firstTime) {
/* 255 */         synchronized (cacheProps) {
/* 256 */           if (firstTime) {
/* 257 */             configFile = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "stax.properties";
/*     */ 
/* 259 */             File f = new File(configFile);
/* 260 */             firstTime = false;
/* 261 */             if (ss.doesFileExist(f)) {
/* 262 */               dPrint("Read properties file " + f);
/* 263 */               cacheProps.load(ss.getFileInputStream(f));
/*     */             }
/*     */             else {
/* 266 */               configFile = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
/*     */ 
/* 268 */               f = new File(configFile);
/* 269 */               if (ss.doesFileExist(f)) {
/* 270 */                 dPrint("Read properties file " + f);
/* 271 */                 cacheProps.load(ss.getFileInputStream(f));
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 277 */       factoryClassName = cacheProps.getProperty(factoryId);
/*     */ 
/* 279 */       if (factoryClassName != null) {
/* 280 */         dPrint("found in " + configFile + " value=" + factoryClassName);
/* 281 */         return newInstance(factoryClassName, null, true);
/*     */       }
/*     */     }
/*     */     catch (Exception ex) {
/* 285 */       if (debug) ex.printStackTrace();
/*     */ 
/*     */     }
/*     */ 
/* 289 */     Object provider = findJarServiceProvider(factoryId);
/* 290 */     if (provider != null) {
/* 291 */       return provider;
/*     */     }
/* 293 */     if (fallbackClassName == null) {
/* 294 */       throw new ConfigurationError("Provider for " + factoryId + " cannot be found", null);
/*     */     }
/*     */ 
/* 298 */     dPrint("loaded from fallback value: " + fallbackClassName);
/* 299 */     return newInstance(fallbackClassName, cl, true);
/*     */   }
/*     */ 
/*     */   private static Object findJarServiceProvider(String factoryId)
/*     */     throws FactoryFinder.ConfigurationError
/*     */   {
/* 310 */     String serviceId = "META-INF/services/" + factoryId;
/* 311 */     InputStream is = null;
/*     */ 
/* 314 */     ClassLoader cl = ss.getContextClassLoader();
/* 315 */     boolean useBSClsLoader = false;
/* 316 */     if (cl != null) {
/* 317 */       is = ss.getResourceAsStream(cl, serviceId);
/*     */ 
/* 320 */       if (is == null) {
/* 321 */         cl = FactoryFinder.class.getClassLoader();
/* 322 */         is = ss.getResourceAsStream(cl, serviceId);
/* 323 */         useBSClsLoader = true;
/*     */       }
/*     */     }
/*     */     else {
/* 327 */       cl = FactoryFinder.class.getClassLoader();
/* 328 */       is = ss.getResourceAsStream(cl, serviceId);
/* 329 */       useBSClsLoader = true;
/*     */     }
/*     */ 
/* 332 */     if (is == null)
/*     */     {
/* 334 */       return null;
/*     */     }
/*     */ 
/* 337 */     if (debug) {
/* 338 */       dPrint("found jar resource=" + serviceId + " using ClassLoader: " + cl);
/*     */     }
/*     */     BufferedReader rd;
/*     */     try
/*     */     {
/* 343 */       rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 346 */       rd = new BufferedReader(new InputStreamReader(is));
/*     */     }
/*     */ 
/* 349 */     String factoryClassName = null;
/*     */     try
/*     */     {
/* 353 */       factoryClassName = rd.readLine();
/* 354 */       rd.close();
/*     */     }
/*     */     catch (IOException x) {
/* 357 */       return null;
/*     */     }
/*     */ 
/* 360 */     if ((factoryClassName != null) && (!"".equals(factoryClassName))) {
/* 361 */       dPrint("found in resource, value=" + factoryClassName);
/*     */ 
/* 367 */       return newInstance(factoryClassName, cl, false, useBSClsLoader);
/*     */     }
/*     */ 
/* 371 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  74 */       String val = ss.getSystemProperty("jaxp.debug");
/*     */ 
/*  76 */       debug = (val != null) && (!"false".equals(val));
/*     */     }
/*     */     catch (SecurityException se) {
/*  79 */       debug = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConfigurationError extends Error
/*     */   {
/*     */     private Exception exception;
/*     */ 
/*     */     ConfigurationError(String msg, Exception x)
/*     */     {
/* 382 */       super();
/* 383 */       this.exception = x;
/*     */     }
/*     */ 
/*     */     Exception getException() {
/* 387 */       return this.exception;
/*     */     }
/*     */ 
/*     */     public Throwable getCause()
/*     */     {
/* 394 */       return this.exception;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.stream.FactoryFinder
 * JD-Core Version:    0.6.2
 */