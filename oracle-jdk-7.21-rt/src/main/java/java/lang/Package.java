/*     */ package java.lang;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.Manifest;
/*     */ import sun.net.www.ParseUtil;
/*     */ 
/*     */ public class Package
/*     */   implements AnnotatedElement
/*     */ {
/* 588 */   private static Map<String, Package> pkgs = new HashMap(31);
/*     */ 
/* 591 */   private static Map<String, URL> urls = new HashMap(10);
/*     */ 
/* 594 */   private static Map<String, Manifest> mans = new HashMap(10);
/*     */   private final String pkgName;
/*     */   private final String specTitle;
/*     */   private final String specVersion;
/*     */   private final String specVendor;
/*     */   private final String implTitle;
/*     */   private final String implVersion;
/*     */   private final String implVendor;
/*     */   private final URL sealBase;
/*     */   private final transient ClassLoader loader;
/*     */   private transient Class packageInfo;
/*     */ 
/*     */   public String getName()
/*     */   {
/* 117 */     return this.pkgName;
/*     */   }
/*     */ 
/*     */   public String getSpecificationTitle()
/*     */   {
/* 126 */     return this.specTitle;
/*     */   }
/*     */ 
/*     */   public String getSpecificationVersion()
/*     */   {
/* 139 */     return this.specVersion;
/*     */   }
/*     */ 
/*     */   public String getSpecificationVendor()
/*     */   {
/* 149 */     return this.specVendor;
/*     */   }
/*     */ 
/*     */   public String getImplementationTitle()
/*     */   {
/* 157 */     return this.implTitle;
/*     */   }
/*     */ 
/*     */   public String getImplementationVersion()
/*     */   {
/* 170 */     return this.implVersion;
/*     */   }
/*     */ 
/*     */   public String getImplementationVendor()
/*     */   {
/* 179 */     return this.implVendor;
/*     */   }
/*     */ 
/*     */   public boolean isSealed()
/*     */   {
/* 188 */     return this.sealBase != null;
/*     */   }
/*     */ 
/*     */   public boolean isSealed(URL paramURL)
/*     */   {
/* 199 */     return paramURL.equals(this.sealBase);
/*     */   }
/*     */ 
/*     */   public boolean isCompatibleWith(String paramString)
/*     */     throws NumberFormatException
/*     */   {
/* 227 */     if ((this.specVersion == null) || (this.specVersion.length() < 1)) {
/* 228 */       throw new NumberFormatException("Empty version string");
/*     */     }
/*     */ 
/* 231 */     String[] arrayOfString1 = this.specVersion.split("\\.", -1);
/* 232 */     int[] arrayOfInt1 = new int[arrayOfString1.length];
/* 233 */     for (int i = 0; i < arrayOfString1.length; i++) {
/* 234 */       arrayOfInt1[i] = Integer.parseInt(arrayOfString1[i]);
/* 235 */       if (arrayOfInt1[i] < 0) {
/* 236 */         throw NumberFormatException.forInputString("" + arrayOfInt1[i]);
/*     */       }
/*     */     }
/* 239 */     String[] arrayOfString2 = paramString.split("\\.", -1);
/* 240 */     int[] arrayOfInt2 = new int[arrayOfString2.length];
/* 241 */     for (int j = 0; j < arrayOfString2.length; j++) {
/* 242 */       arrayOfInt2[j] = Integer.parseInt(arrayOfString2[j]);
/* 243 */       if (arrayOfInt2[j] < 0) {
/* 244 */         throw NumberFormatException.forInputString("" + arrayOfInt2[j]);
/*     */       }
/*     */     }
/* 247 */     j = Math.max(arrayOfInt2.length, arrayOfInt1.length);
/* 248 */     for (int k = 0; k < j; k++) {
/* 249 */       int m = k < arrayOfInt2.length ? arrayOfInt2[k] : 0;
/* 250 */       int n = k < arrayOfInt1.length ? arrayOfInt1[k] : 0;
/* 251 */       if (n < m)
/* 252 */         return false;
/* 253 */       if (n > m)
/* 254 */         return true;
/*     */     }
/* 256 */     return true;
/*     */   }
/*     */ 
/*     */   public static Package getPackage(String paramString)
/*     */   {
/* 276 */     ClassLoader localClassLoader = ClassLoader.getCallerClassLoader();
/* 277 */     if (localClassLoader != null) {
/* 278 */       return localClassLoader.getPackage(paramString);
/*     */     }
/* 280 */     return getSystemPackage(paramString);
/*     */   }
/*     */ 
/*     */   public static Package[] getPackages()
/*     */   {
/* 297 */     ClassLoader localClassLoader = ClassLoader.getCallerClassLoader();
/* 298 */     if (localClassLoader != null) {
/* 299 */       return localClassLoader.getPackages();
/*     */     }
/* 301 */     return getSystemPackages();
/*     */   }
/*     */ 
/*     */   static Package getPackage(Class<?> paramClass)
/*     */   {
/* 323 */     String str = paramClass.getName();
/* 324 */     int i = str.lastIndexOf('.');
/* 325 */     if (i != -1) {
/* 326 */       str = str.substring(0, i);
/* 327 */       ClassLoader localClassLoader = paramClass.getClassLoader();
/* 328 */       if (localClassLoader != null) {
/* 329 */         return localClassLoader.getPackage(str);
/*     */       }
/* 331 */       return getSystemPackage(str);
/*     */     }
/*     */ 
/* 334 */     return null;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 343 */     return this.pkgName.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 354 */     String str1 = this.specTitle;
/* 355 */     String str2 = this.specVersion;
/* 356 */     if ((str1 != null) && (str1.length() > 0))
/* 357 */       str1 = ", " + str1;
/*     */     else
/* 359 */       str1 = "";
/* 360 */     if ((str2 != null) && (str2.length() > 0))
/* 361 */       str2 = ", version " + str2;
/*     */     else
/* 363 */       str2 = "";
/* 364 */     return "package " + this.pkgName + str1 + str2;
/*     */   }
/*     */ 
/*     */   private Class<?> getPackageInfo() {
/* 368 */     if (this.packageInfo == null) {
/*     */       try {
/* 370 */         this.packageInfo = Class.forName(this.pkgName + ".package-info", false, this.loader);
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException)
/*     */       {
/* 374 */         this.packageInfo = 1PackageInfoProxy.class;
/*     */       }
/*     */     }
/* 377 */     return this.packageInfo;
/*     */   }
/*     */ 
/*     */   public <A extends Annotation> A getAnnotation(Class<A> paramClass)
/*     */   {
/* 385 */     return getPackageInfo().getAnnotation(paramClass);
/*     */   }
/*     */ 
/*     */   public boolean isAnnotationPresent(Class<? extends Annotation> paramClass)
/*     */   {
/* 394 */     return getPackageInfo().isAnnotationPresent(paramClass);
/*     */   }
/*     */ 
/*     */   public Annotation[] getAnnotations()
/*     */   {
/* 401 */     return getPackageInfo().getAnnotations();
/*     */   }
/*     */ 
/*     */   public Annotation[] getDeclaredAnnotations()
/*     */   {
/* 408 */     return getPackageInfo().getDeclaredAnnotations();
/*     */   }
/*     */ 
/*     */   Package(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, URL paramURL, ClassLoader paramClassLoader)
/*     */   {
/* 428 */     this.pkgName = paramString1;
/* 429 */     this.implTitle = paramString5;
/* 430 */     this.implVersion = paramString6;
/* 431 */     this.implVendor = paramString7;
/* 432 */     this.specTitle = paramString2;
/* 433 */     this.specVersion = paramString3;
/* 434 */     this.specVendor = paramString4;
/* 435 */     this.sealBase = paramURL;
/* 436 */     this.loader = paramClassLoader;
/*     */   }
/*     */ 
/*     */   private Package(String paramString, Manifest paramManifest, URL paramURL, ClassLoader paramClassLoader)
/*     */   {
/* 447 */     String str1 = paramString.replace('.', '/').concat("/");
/* 448 */     String str2 = null;
/* 449 */     String str3 = null;
/* 450 */     String str4 = null;
/* 451 */     String str5 = null;
/* 452 */     String str6 = null;
/* 453 */     String str7 = null;
/* 454 */     String str8 = null;
/* 455 */     URL localURL = null;
/* 456 */     Attributes localAttributes = paramManifest.getAttributes(str1);
/* 457 */     if (localAttributes != null) {
/* 458 */       str3 = localAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
/* 459 */       str4 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
/* 460 */       str5 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
/* 461 */       str6 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
/* 462 */       str7 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
/* 463 */       str8 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
/* 464 */       str2 = localAttributes.getValue(Attributes.Name.SEALED);
/*     */     }
/* 466 */     localAttributes = paramManifest.getMainAttributes();
/* 467 */     if (localAttributes != null) {
/* 468 */       if (str3 == null) {
/* 469 */         str3 = localAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
/*     */       }
/* 471 */       if (str4 == null) {
/* 472 */         str4 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
/*     */       }
/* 474 */       if (str5 == null) {
/* 475 */         str5 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
/*     */       }
/* 477 */       if (str6 == null) {
/* 478 */         str6 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
/*     */       }
/* 480 */       if (str7 == null) {
/* 481 */         str7 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
/*     */       }
/* 483 */       if (str8 == null) {
/* 484 */         str8 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
/*     */       }
/* 486 */       if (str2 == null) {
/* 487 */         str2 = localAttributes.getValue(Attributes.Name.SEALED);
/*     */       }
/*     */     }
/* 490 */     if ("true".equalsIgnoreCase(str2)) {
/* 491 */       localURL = paramURL;
/*     */     }
/* 493 */     this.pkgName = paramString;
/* 494 */     this.specTitle = str3;
/* 495 */     this.specVersion = str4;
/* 496 */     this.specVendor = str5;
/* 497 */     this.implTitle = str6;
/* 498 */     this.implVersion = str7;
/* 499 */     this.implVendor = str8;
/* 500 */     this.sealBase = localURL;
/* 501 */     this.loader = paramClassLoader;
/*     */   }
/*     */ 
/*     */   static Package getSystemPackage(String paramString)
/*     */   {
/* 508 */     synchronized (pkgs) {
/* 509 */       Package localPackage = (Package)pkgs.get(paramString);
/* 510 */       if (localPackage == null) {
/* 511 */         paramString = paramString.replace('.', '/').concat("/");
/* 512 */         String str = getSystemPackage0(paramString);
/* 513 */         if (str != null) {
/* 514 */           localPackage = defineSystemPackage(paramString, str);
/*     */         }
/*     */       }
/* 517 */       return localPackage;
/*     */     }
/*     */   }
/*     */ 
/*     */   static Package[] getSystemPackages()
/*     */   {
/* 526 */     String[] arrayOfString = getSystemPackages0();
/* 527 */     synchronized (pkgs) {
/* 528 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 529 */         defineSystemPackage(arrayOfString[i], getSystemPackage0(arrayOfString[i]));
/*     */       }
/* 531 */       return (Package[])pkgs.values().toArray(new Package[pkgs.size()]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Package defineSystemPackage(String paramString1, final String paramString2)
/*     */   {
/* 538 */     return (Package)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Package run() {
/* 540 */         String str = this.val$iname;
/*     */ 
/* 542 */         URL localURL = (URL)Package.urls.get(paramString2);
/*     */         Object localObject;
/* 543 */         if (localURL == null)
/*     */         {
/* 545 */           localObject = new File(paramString2);
/*     */           try {
/* 547 */             localURL = ParseUtil.fileToEncodedURL((File)localObject);
/*     */           } catch (MalformedURLException localMalformedURLException) {
/*     */           }
/* 550 */           if (localURL != null) {
/* 551 */             Package.urls.put(paramString2, localURL);
/*     */ 
/* 553 */             if (((File)localObject).isFile()) {
/* 554 */               Package.mans.put(paramString2, Package.loadManifest(paramString2));
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 559 */         str = str.substring(0, str.length() - 1).replace('/', '.');
/*     */ 
/* 561 */         Manifest localManifest = (Manifest)Package.mans.get(paramString2);
/* 562 */         if (localManifest != null)
/* 563 */           localObject = new Package(str, localManifest, localURL, null, null);
/*     */         else {
/* 565 */           localObject = new Package(str, null, null, null, null, null, null, null, null);
/*     */         }
/*     */ 
/* 568 */         Package.pkgs.put(str, localObject);
/* 569 */         return localObject;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static Manifest loadManifest(String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 578 */       FileInputStream localFileInputStream = new FileInputStream(paramString); Object localObject1 = null;
/*     */       try { JarInputStream localJarInputStream = new JarInputStream(localFileInputStream, false);
/*     */ 
/* 578 */         Object localObject2 = null;
/*     */         try
/*     */         {
/* 581 */           return localJarInputStream.getManifest();
/*     */         }
/*     */         catch (Throwable localThrowable2)
/*     */         {
/* 578 */           localObject2 = localThrowable2; throw localThrowable2; } finally {  } } catch (Throwable localThrowable1) { localObject1 = localThrowable1; throw localThrowable1;
/*     */       }
/*     */       finally
/*     */       {
/* 582 */         if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable6) { localObject1.addSuppressed(localThrowable6); } else localFileInputStream.close();   }  } catch (IOException localIOException) {  }
/*     */ 
/* 583 */     return null;
/*     */   }
/*     */ 
/*     */   private static native String getSystemPackage0(String paramString);
/*     */ 
/*     */   private static native String[] getSystemPackages0();
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Package
 * JD-Core Version:    0.6.2
 */