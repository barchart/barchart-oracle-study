/*     */ package javax.management.remote;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.remote.util.ClassLogger;
/*     */ import com.sun.jmx.remote.util.EnvHelp;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class JMXConnectorFactory
/*     */ {
/*     */   public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
/*     */   public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
/*     */   public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
/*     */   private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
/* 196 */   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorFactory");
/*     */ 
/*     */   public static JMXConnector connect(JMXServiceURL paramJMXServiceURL)
/*     */     throws IOException
/*     */   {
/* 226 */     return connect(paramJMXServiceURL, null);
/*     */   }
/*     */ 
/*     */   public static JMXConnector connect(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
/*     */     throws IOException
/*     */   {
/* 264 */     if (paramJMXServiceURL == null)
/* 265 */       throw new NullPointerException("Null JMXServiceURL");
/* 266 */     JMXConnector localJMXConnector = newJMXConnector(paramJMXServiceURL, paramMap);
/* 267 */     localJMXConnector.connect(paramMap);
/* 268 */     return localJMXConnector;
/*     */   }
/*     */ 
/*     */   private static <K, V> Map<K, V> newHashMap() {
/* 272 */     return new HashMap();
/*     */   }
/*     */ 
/*     */   private static <K> Map<K, Object> newHashMap(Map<K, ?> paramMap) {
/* 276 */     return new HashMap(paramMap);
/*     */   }
/*     */ 
/*     */   public static JMXConnector newJMXConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
/*     */     throws IOException
/*     */   {
/*     */     Map localMap;
/* 313 */     if (paramMap == null) {
/* 314 */       localMap = newHashMap();
/*     */     } else {
/* 316 */       EnvHelp.checkAttributes(paramMap);
/* 317 */       localMap = newHashMap(paramMap);
/*     */     }
/*     */ 
/* 320 */     ClassLoader localClassLoader = resolveClassLoader(localMap);
/* 321 */     JMXConnectorProvider localJMXConnectorProvider1 = JMXConnectorProvider.class;
/*     */ 
/* 323 */     String str = paramJMXServiceURL.getProtocol();
/*     */ 
/* 325 */     JMXServiceURL localJMXServiceURL = paramJMXServiceURL;
/*     */ 
/* 327 */     JMXConnectorProvider localJMXConnectorProvider2 = (JMXConnectorProvider)getProvider(localJMXServiceURL, localMap, "ClientProvider", localJMXConnectorProvider1, localClassLoader);
/*     */ 
/* 332 */     Object localObject1 = null;
/* 333 */     if (localJMXConnectorProvider2 == null)
/*     */     {
/* 338 */       if (localClassLoader != null) {
/*     */         try {
/* 340 */           JMXConnector localJMXConnector = getConnectorAsService(localClassLoader, localJMXServiceURL, localMap);
/*     */ 
/* 342 */           if (localJMXConnector != null)
/* 343 */             return localJMXConnector;
/*     */         } catch (JMXProviderException localJMXProviderException) {
/* 345 */           throw localJMXProviderException;
/*     */         } catch (IOException localIOException) {
/* 347 */           localObject1 = localIOException;
/*     */         }
/*     */       }
/* 350 */       localJMXConnectorProvider2 = (JMXConnectorProvider)getProvider(str, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ClientProvider", localJMXConnectorProvider1);
/*     */     }
/*     */ 
/* 355 */     if (localJMXConnectorProvider2 == null) {
/* 356 */       localObject2 = new MalformedURLException("Unsupported protocol: " + str);
/*     */ 
/* 358 */       if (localObject1 == null) {
/* 359 */         throw ((Throwable)localObject2);
/*     */       }
/* 361 */       throw ((MalformedURLException)EnvHelp.initCause((Throwable)localObject2, localObject1));
/*     */     }
/*     */ 
/* 365 */     Object localObject2 = Collections.unmodifiableMap(localMap);
/*     */ 
/* 368 */     return localJMXConnectorProvider2.newJMXConnector(paramJMXServiceURL, (Map)localObject2);
/*     */   }
/*     */ 
/*     */   private static String resolvePkgs(Map<String, ?> paramMap)
/*     */     throws JMXProviderException
/*     */   {
/* 374 */     Object localObject = null;
/*     */ 
/* 376 */     if (paramMap != null) {
/* 377 */       localObject = paramMap.get("jmx.remote.protocol.provider.pkgs");
/*     */     }
/* 379 */     if (localObject == null) {
/* 380 */       localObject = AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public String run() {
/* 383 */           return System.getProperty("jmx.remote.protocol.provider.pkgs");
/*     */         }
/*     */       });
/*     */     }
/* 387 */     if (localObject == null) {
/* 388 */       return null;
/*     */     }
/* 390 */     if (!(localObject instanceof String)) {
/* 391 */       str1 = "Value of jmx.remote.protocol.provider.pkgs parameter is not a String: " + localObject.getClass().getName();
/*     */ 
/* 394 */       throw new JMXProviderException(str1);
/*     */     }
/*     */ 
/* 397 */     String str1 = (String)localObject;
/* 398 */     if (str1.trim().equals("")) {
/* 399 */       return null;
/*     */     }
/*     */ 
/* 402 */     if ((str1.startsWith("|")) || (str1.endsWith("|")) || (str1.indexOf("||") >= 0))
/*     */     {
/* 404 */       String str2 = "Value of jmx.remote.protocol.provider.pkgs contains an empty element: " + str1;
/*     */ 
/* 406 */       throw new JMXProviderException(str2);
/*     */     }
/*     */ 
/* 409 */     return str1;
/*     */   }
/*     */ 
/*     */   static <T> T getProvider(JMXServiceURL paramJMXServiceURL, Map<String, Object> paramMap, String paramString, Class<T> paramClass, ClassLoader paramClassLoader)
/*     */     throws IOException
/*     */   {
/* 419 */     String str1 = paramJMXServiceURL.getProtocol();
/*     */ 
/* 421 */     String str2 = resolvePkgs(paramMap);
/*     */ 
/* 423 */     Object localObject = null;
/*     */ 
/* 425 */     if (str2 != null) {
/* 426 */       paramMap.put("jmx.remote.protocol.provider.class.loader", paramClassLoader);
/*     */ 
/* 428 */       localObject = getProvider(str1, str2, paramClassLoader, paramString, paramClass);
/*     */     }
/*     */ 
/* 433 */     return localObject;
/*     */   }
/*     */ 
/*     */   static <T> Iterator<T> getProviderIterator(Class<T> paramClass, ClassLoader paramClassLoader)
/*     */   {
/* 438 */     ServiceLoader localServiceLoader = ServiceLoader.load(paramClass, paramClassLoader);
/*     */ 
/* 440 */     return localServiceLoader.iterator();
/*     */   }
/*     */ 
/*     */   private static JMXConnector getConnectorAsService(ClassLoader paramClassLoader, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
/*     */     throws IOException
/*     */   {
/* 448 */     Iterator localIterator = getProviderIterator(JMXConnectorProvider.class, paramClassLoader);
/*     */ 
/* 451 */     IOException localIOException = null;
/* 452 */     while (localIterator.hasNext()) {
/* 453 */       JMXConnectorProvider localJMXConnectorProvider = (JMXConnectorProvider)localIterator.next();
/*     */       try {
/* 455 */         return localJMXConnectorProvider.newJMXConnector(paramJMXServiceURL, paramMap);
/*     */       }
/*     */       catch (JMXProviderException localJMXProviderException) {
/* 458 */         throw localJMXProviderException;
/*     */       } catch (Exception localException) {
/* 460 */         if (logger.traceOn()) {
/* 461 */           logger.trace("getConnectorAsService", "URL[" + paramJMXServiceURL + "] Service provider exception: " + localException);
/*     */         }
/*     */ 
/* 464 */         if ((!(localException instanceof MalformedURLException)) && 
/* 465 */           (localIOException == null)) {
/* 466 */           if ((localException instanceof IOException))
/* 467 */             localIOException = (IOException)localException;
/*     */           else {
/* 469 */             localIOException = (IOException)EnvHelp.initCause(new IOException(localException.getMessage()), localException);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 477 */     if (localIOException == null) {
/* 478 */       return null;
/*     */     }
/* 480 */     throw localIOException;
/*     */   }
/*     */ 
/*     */   static <T> T getProvider(String paramString1, String paramString2, ClassLoader paramClassLoader, String paramString3, Class<T> paramClass)
/*     */     throws IOException
/*     */   {
/* 490 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, "|");
/*     */ 
/* 492 */     while (localStringTokenizer.hasMoreTokens()) { String str1 = localStringTokenizer.nextToken();
/* 494 */       String str2 = str1 + "." + protocol2package(paramString1) + "." + paramString3;
/*     */       Class localClass;
/*     */       try {
/* 498 */         localClass = Class.forName(str2, true, paramClassLoader);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/*     */       }
/* 501 */       continue;
/*     */ 
/* 504 */       if (!paramClass.isAssignableFrom(localClass)) {
/* 505 */         localObject = "Provider class does not implement " + paramClass.getName() + ": " + localClass.getName();
/*     */ 
/* 509 */         throw new JMXProviderException((String)localObject);
/*     */       }
/*     */ 
/* 513 */       Object localObject = (Class)Util.cast(localClass);
/*     */       try {
/* 515 */         return ((Class)localObject).newInstance();
/*     */       } catch (Exception localException) {
/* 517 */         String str3 = "Exception when instantiating provider [" + str2 + "]";
/*     */ 
/* 520 */         throw new JMXProviderException(str3, localException);
/*     */       }
/*     */     }
/*     */ 
/* 524 */     return null;
/*     */   }
/*     */ 
/*     */   static ClassLoader resolveClassLoader(Map<String, ?> paramMap) {
/* 528 */     ClassLoader localClassLoader = null;
/*     */ 
/* 530 */     if (paramMap != null) {
/*     */       try {
/* 532 */         localClassLoader = (ClassLoader)paramMap.get("jmx.remote.protocol.provider.class.loader");
/*     */       }
/*     */       catch (ClassCastException localClassCastException)
/*     */       {
/* 539 */         throw new IllegalArgumentException("The ClassLoader supplied in the environment map using the jmx.remote.protocol.provider.class.loader attribute is not an instance of java.lang.ClassLoader");
/*     */       }
/*     */     }
/*     */ 
/* 543 */     if (localClassLoader == null) {
/* 544 */       localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public ClassLoader run() {
/* 547 */           return Thread.currentThread().getContextClassLoader();
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/* 552 */     return localClassLoader;
/*     */   }
/*     */ 
/*     */   private static String protocol2package(String paramString) {
/* 556 */     return paramString.replace('+', '.').replace('-', '_');
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.JMXConnectorFactory
 * JD-Core Version:    0.6.2
 */