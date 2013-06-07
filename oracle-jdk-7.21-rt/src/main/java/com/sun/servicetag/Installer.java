/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringReader;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Installer
/*     */ {
/*  42 */   private static String SVCTAG_DIR_PATH = "servicetag.dir.path";
/*     */ 
/*  44 */   private static String SVCTAG_ENABLE_REGISTRATION = "servicetag.registration.enabled";
/*     */   private static final String ORACLE = "Oracle";
/*     */   private static final String SUN = "Sun Microsystems";
/*     */   private static final String REGISTRATION_XML = "registration.xml";
/*     */   private static final String SERVICE_TAG_FILE = "servicetag";
/*     */   private static final String REGISTRATION_HTML_NAME = "register";
/*  52 */   private static final Locale[] knownSupportedLocales = { Locale.ENGLISH, Locale.JAPANESE, Locale.SIMPLIFIED_CHINESE };
/*     */ 
/*  57 */   private static final String javaHome = System.getProperty("java.home");
/*     */   private static File svcTagDir;
/*     */   private static File serviceTagFile;
/*     */   private static File regXmlFile;
/*     */   private static RegistrationData registration;
/*     */   private static boolean supportRegistration;
/*     */   private static String registerHtmlParent;
/*  64 */   private static Set<Locale> supportedLocales = new HashSet();
/*  65 */   private static Properties swordfishProps = null;
/*  66 */   private static String[] jreArchs = null;
/*     */   private static final String JDK_HEADER_PNG_KEY = "@@JDK_HEADER_PNG@@";
/*     */   private static final String JDK_VERSION_KEY = "@@JDK_VERSION@@";
/*     */   private static final String REGISTRATION_URL_KEY = "@@REGISTRATION_URL@@";
/*     */   private static final String REGISTRATION_PAYLOAD_KEY = "@@REGISTRATION_PAYLOAD@@";
/*     */   private static final int MAX_SOURCE_LEN = 63;
/*     */ 
/*     */   static ServiceTag getJavaServiceTag(String paramString)
/*     */     throws IOException
/*     */   {
/*  88 */     String str = System.getProperty("java.vendor", "");
/*  89 */     if ((!str.startsWith("Sun Microsystems")) && (!str.startsWith("Oracle")))
/*     */     {
/*  92 */       return null;
/*     */     }
/*  94 */     int i = 0;
/*     */     try
/*     */     {
/*  97 */       if (loadSwordfishEntries() == null) {
/*  98 */         return null;
/*     */       }
/*     */ 
/* 101 */       ServiceTag localServiceTag1 = getJavaServiceTag();
/*     */       ServiceTag localServiceTag2;
/* 103 */       if ((localServiceTag1 != null) && (localServiceTag1.getSource().equals(paramString)))
/*     */       {
/* 106 */         if (Registry.isSupported()) {
/* 107 */           installSystemServiceTag();
/*     */         }
/* 109 */         return localServiceTag1;
/*     */       }
/*     */ 
/* 113 */       i = 1;
/*     */ 
/* 117 */       deleteRegistrationData();
/* 118 */       i = 0;
/*     */ 
/* 121 */       return createServiceTag(paramString);
/*     */     } finally {
/* 123 */       if (i != 0) {
/* 124 */         if (regXmlFile.exists()) {
/* 125 */           regXmlFile.delete();
/*     */         }
/* 127 */         if (serviceTagFile.exists())
/* 128 */           serviceTagFile.delete();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized RegistrationData getRegistrationData()
/*     */     throws IOException
/*     */   {
/* 143 */     if (registration != null) {
/* 144 */       return registration;
/*     */     }
/* 146 */     if (regXmlFile.exists()) {
/* 147 */       BufferedInputStream localBufferedInputStream = null;
/*     */       try {
/* 149 */         localBufferedInputStream = new BufferedInputStream(new FileInputStream(regXmlFile));
/* 150 */         registration = RegistrationData.loadFromXML(localBufferedInputStream);
/*     */       } catch (IllegalArgumentException localIllegalArgumentException) {
/* 152 */         System.err.println("Error: Bad registration data \"" + regXmlFile + "\":" + localIllegalArgumentException.getMessage());
/*     */ 
/* 154 */         throw localIllegalArgumentException;
/*     */       } finally {
/* 156 */         if (localBufferedInputStream != null)
/* 157 */           localBufferedInputStream.close();
/*     */       }
/*     */     }
/*     */     else {
/* 161 */       registration = new RegistrationData();
/*     */     }
/* 163 */     return registration;
/*     */   }
/*     */ 
/*     */   private static synchronized void writeRegistrationXml()
/*     */     throws IOException
/*     */   {
/* 176 */     if (!svcTagDir.exists())
/*     */     {
/* 180 */       if (!svcTagDir.mkdir()) {
/* 181 */         throw new IOException("Failed to create directory: " + svcTagDir);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 186 */     deleteRegistrationHtmlPage();
/* 187 */     getRegistrationHtmlPage();
/*     */ 
/* 189 */     BufferedOutputStream localBufferedOutputStream = null;
/*     */     try {
/* 191 */       localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(regXmlFile));
/* 192 */       getRegistrationData().storeToXML(localBufferedOutputStream);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 194 */       System.err.println("Error: Bad registration data \"" + regXmlFile + "\":" + localIllegalArgumentException.getMessage());
/*     */ 
/* 196 */       throw localIllegalArgumentException;
/*     */     } finally {
/* 198 */       if (localBufferedOutputStream != null)
/* 199 */         localBufferedOutputStream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Set<String> getInstalledURNs()
/*     */     throws IOException
/*     */   {
/* 209 */     HashSet localHashSet = new HashSet();
/* 210 */     if (serviceTagFile.exists()) {
/* 211 */       BufferedReader localBufferedReader = null;
/*     */       try {
/* 213 */         localBufferedReader = new BufferedReader(new FileReader(serviceTagFile));
/*     */         String str;
/* 215 */         while ((str = localBufferedReader.readLine()) != null) {
/* 216 */           str = str.trim();
/* 217 */           if (str.length() > 0)
/* 218 */             localHashSet.add(str);
/*     */         }
/*     */       }
/*     */       finally {
/* 222 */         if (localBufferedReader != null) {
/* 223 */           localBufferedReader.close();
/*     */         }
/*     */       }
/*     */     }
/* 227 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   private static ServiceTag[] getJavaServiceTagArray()
/*     */     throws IOException
/*     */   {
/* 238 */     RegistrationData localRegistrationData = getRegistrationData();
/* 239 */     Set localSet = localRegistrationData.getServiceTags();
/* 240 */     HashSet localHashSet = new HashSet();
/*     */ 
/* 242 */     Properties localProperties = loadSwordfishEntries();
/* 243 */     String str1 = localProperties.getProperty("servicetag.jdk.urn");
/* 244 */     String str2 = localProperties.getProperty("servicetag.jre.urn");
/* 245 */     for (ServiceTag localServiceTag : localSet) {
/* 246 */       if ((localServiceTag.getProductURN().equals(str1)) || (localServiceTag.getProductURN().equals(str2)))
/*     */       {
/* 248 */         localHashSet.add(localServiceTag);
/*     */       }
/*     */     }
/* 251 */     return (ServiceTag[])localHashSet.toArray(new ServiceTag[0]);
/*     */   }
/*     */ 
/*     */   private static ServiceTag getJavaServiceTag()
/*     */     throws IOException
/*     */   {
/* 261 */     String str = getProductDefinedId();
/* 262 */     for (ServiceTag localServiceTag : getJavaServiceTagArray()) {
/* 263 */       if (localServiceTag.getProductDefinedInstanceID().equals(str)) {
/* 264 */         return localServiceTag;
/*     */       }
/*     */     }
/* 267 */     return null;
/*     */   }
/*     */ 
/*     */   private static ServiceTag createServiceTag(String paramString)
/*     */     throws IOException
/*     */   {
/* 294 */     ServiceTag localServiceTag1 = null;
/* 295 */     if (getJavaServiceTag() == null) {
/* 296 */       localServiceTag1 = newServiceTag(paramString);
/*     */     }
/*     */ 
/* 300 */     if (localServiceTag1 != null) {
/* 301 */       RegistrationData localRegistrationData = getRegistrationData();
/*     */ 
/* 304 */       localServiceTag1 = localRegistrationData.addServiceTag(localServiceTag1);
/*     */ 
/* 307 */       ServiceTag localServiceTag2 = SolarisServiceTag.getServiceTag();
/* 308 */       if ((localServiceTag2 != null) && (localRegistrationData.getServiceTag(localServiceTag2.getInstanceURN()) == null)) {
/* 309 */         localRegistrationData.addServiceTag(localServiceTag2);
/*     */       }
/*     */ 
/* 312 */       writeRegistrationXml();
/*     */     }
/*     */ 
/* 316 */     if (Registry.isSupported()) {
/* 317 */       installSystemServiceTag();
/*     */     }
/* 319 */     return localServiceTag1;
/*     */   }
/*     */ 
/*     */   private static void installSystemServiceTag()
/*     */     throws IOException
/*     */   {
/* 325 */     if (((!serviceTagFile.exists()) && (!svcTagDir.canWrite())) || ((serviceTagFile.exists()) && (!serviceTagFile.canWrite())))
/*     */     {
/* 327 */       return;
/*     */     }
/*     */ 
/* 330 */     Set localSet = getInstalledURNs();
/* 331 */     ServiceTag[] arrayOfServiceTag1 = getJavaServiceTagArray();
/* 332 */     if (localSet.size() < arrayOfServiceTag1.length) {
/* 333 */       for (ServiceTag localServiceTag : arrayOfServiceTag1)
/*     */       {
/* 336 */         String str = localServiceTag.getInstanceURN();
/* 337 */         if (!localSet.contains(str)) {
/* 338 */           Registry.getSystemRegistry().addServiceTag(localServiceTag);
/*     */         }
/*     */       }
/*     */     }
/* 342 */     writeInstalledUrns();
/*     */   }
/*     */ 
/*     */   private static ServiceTag newServiceTag(String paramString) throws IOException
/*     */   {
/* 347 */     Properties localProperties = loadSwordfishEntries();
/*     */     String str1;
/*     */     String str2;
/* 353 */     if (Util.isJdk())
/*     */     {
/* 355 */       str1 = localProperties.getProperty("servicetag.jdk.urn");
/* 356 */       str2 = localProperties.getProperty("servicetag.jdk.name");
/*     */     }
/*     */     else {
/* 359 */       str1 = localProperties.getProperty("servicetag.jre.urn");
/* 360 */       str2 = localProperties.getProperty("servicetag.jre.name");
/*     */     }
/*     */ 
/* 363 */     return ServiceTag.newInstance(ServiceTag.generateInstanceURN(), str2, System.getProperty("java.version"), str1, localProperties.getProperty("servicetag.parent.name"), localProperties.getProperty("servicetag.parent.urn"), getProductDefinedId(), System.getProperty("java.vendor"), System.getProperty("os.arch"), getZoneName(), paramString);
/*     */   }
/*     */ 
/*     */   private static synchronized void deleteRegistrationData()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 386 */       deleteRegistrationHtmlPage();
/*     */ 
/* 389 */       Set localSet = getInstalledURNs();
/* 390 */       if ((localSet.size() > 0) && (Registry.isSupported())) {
/* 391 */         for (String str : localSet) {
/* 392 */           Registry.getSystemRegistry().removeServiceTag(str);
/*     */         }
/*     */       }
/* 395 */       registration = null;
/*     */     }
/*     */     finally {
/* 398 */       if ((regXmlFile.exists()) && 
/* 399 */         (!regXmlFile.delete())) {
/* 400 */         throw new IOException("Failed to delete " + regXmlFile);
/*     */       }
/*     */ 
/* 403 */       if ((serviceTagFile.exists()) && 
/* 404 */         (!serviceTagFile.delete()))
/* 405 */         throw new IOException("Failed to delete " + serviceTagFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void updateRegistrationData(String paramString)
/*     */     throws IOException
/*     */   {
/* 417 */     RegistrationData localRegistrationData = getRegistrationData();
/* 418 */     ServiceTag localServiceTag1 = newServiceTag(paramString);
/*     */ 
/* 420 */     ServiceTag[] arrayOfServiceTag1 = getJavaServiceTagArray();
/* 421 */     Set localSet = getInstalledURNs();
/* 422 */     for (ServiceTag localServiceTag2 : arrayOfServiceTag1) {
/* 423 */       if (!localServiceTag2.getProductDefinedInstanceID().equals(localServiceTag1.getProductDefinedInstanceID())) {
/* 424 */         String str = localServiceTag2.getInstanceURN();
/* 425 */         localRegistrationData.removeServiceTag(str);
/*     */ 
/* 428 */         if ((localSet.contains(str)) && (Registry.isSupported())) {
/* 429 */           Registry.getSystemRegistry().removeServiceTag(str);
/*     */         }
/*     */       }
/*     */     }
/* 433 */     writeRegistrationXml();
/* 434 */     writeInstalledUrns();
/*     */   }
/*     */ 
/*     */   private static void writeInstalledUrns()
/*     */     throws IOException
/*     */   {
/* 440 */     if ((!Registry.isSupported()) && (serviceTagFile.exists())) {
/* 441 */       serviceTagFile.delete();
/* 442 */       return;
/*     */     }
/*     */ 
/* 445 */     PrintWriter localPrintWriter = null;
/*     */     try {
/* 447 */       localPrintWriter = new PrintWriter(serviceTagFile);
/*     */ 
/* 449 */       ServiceTag[] arrayOfServiceTag1 = getJavaServiceTagArray();
/* 450 */       for (ServiceTag localServiceTag : arrayOfServiceTag1)
/*     */       {
/* 452 */         String str = localServiceTag.getInstanceURN();
/* 453 */         localPrintWriter.println(str);
/*     */       }
/*     */     } finally {
/* 456 */       if (localPrintWriter != null)
/* 457 */         localPrintWriter.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized Properties loadSwordfishEntries()
/*     */     throws IOException
/*     */   {
/* 470 */     if (swordfishProps != null) {
/* 471 */       return swordfishProps;
/*     */     }
/*     */ 
/* 476 */     int i = Util.getJdkVersion();
/*     */ 
/* 478 */     String str = "/com/sun/servicetag/resources/javase_" + i + "_swordfish.properties";
/*     */ 
/* 480 */     InputStream localInputStream = Installer.class.getResourceAsStream(str);
/* 481 */     if (localInputStream == null) {
/* 482 */       return null;
/*     */     }
/* 484 */     swordfishProps = new Properties();
/*     */     try {
/* 486 */       swordfishProps.load(localInputStream);
/*     */     } finally {
/* 488 */       localInputStream.close();
/*     */     }
/* 490 */     return swordfishProps;
/*     */   }
/*     */ 
/*     */   private static String getProductDefinedId()
/*     */   {
/* 514 */     StringBuilder localStringBuilder = new StringBuilder();
/* 515 */     localStringBuilder.append("id=");
/* 516 */     localStringBuilder.append(System.getProperty("java.runtime.version"));
/*     */ 
/* 518 */     String[] arrayOfString = getJreArchs();
/* 519 */     for (String str : arrayOfString) {
/* 520 */       localStringBuilder.append(" " + str);
/*     */     }
/*     */ 
/* 523 */     ??? = ",dir=" + javaHome;
/* 524 */     if (localStringBuilder.length() + ((String)???).length() < 256) {
/* 525 */       localStringBuilder.append(",dir=");
/* 526 */       localStringBuilder.append(javaHome);
/*     */     }
/* 529 */     else if (Util.isVerbose()) {
/* 530 */       System.err.println("Warning: Product defined instance ID exceeds the field limit:");
/*     */     }
/*     */ 
/* 534 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   private static synchronized String[] getJreArchs()
/*     */   {
/* 545 */     if (jreArchs != null) {
/* 546 */       return jreArchs;
/*     */     }
/*     */ 
/* 549 */     HashSet localHashSet = new HashSet();
/*     */ 
/* 551 */     String str1 = System.getProperty("os.name");
/* 552 */     if ((str1.equals("SunOS")) || (str1.equals("Linux")))
/*     */     {
/* 556 */       File localFile1 = new File(Util.getJrePath() + File.separator + "lib");
/* 557 */       if (localFile1.isDirectory()) {
/* 558 */         String[] arrayOfString1 = localFile1.list();
/* 559 */         for (String str2 : arrayOfString1) {
/* 560 */           File localFile2 = new File(localFile1, str2 + File.separator + "libjava.so");
/* 561 */           if (localFile2.exists())
/* 562 */             localHashSet.add(str2);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 568 */       localHashSet.add(System.getProperty("os.arch"));
/*     */     }
/* 570 */     jreArchs = (String[])localHashSet.toArray(new String[0]);
/* 571 */     return jreArchs;
/*     */   }
/*     */ 
/*     */   private static String getZoneName()
/*     */     throws IOException
/*     */   {
/* 579 */     String str1 = "global";
/*     */ 
/* 581 */     String str2 = "/usr/bin/zonename";
/* 582 */     File localFile = new File(str2);
/*     */ 
/* 586 */     if (localFile.exists()) {
/* 587 */       ProcessBuilder localProcessBuilder = new ProcessBuilder(new String[] { str2 });
/* 588 */       Process localProcess = localProcessBuilder.start();
/* 589 */       String str3 = Util.commandOutput(localProcess);
/* 590 */       if (localProcess.exitValue() == 0) {
/* 591 */         str1 = str3.trim();
/*     */       }
/*     */     }
/*     */ 
/* 595 */     return str1;
/*     */   }
/*     */ 
/*     */   private static synchronized String getRegisterHtmlParent() throws IOException {
/* 599 */     if (registerHtmlParent == null)
/*     */     {
/*     */       File localFile1;
/* 601 */       if (Util.getJrePath().endsWith(File.separator + "jre")) {
/* 602 */         localFile1 = new File(Util.getJrePath(), "..");
/*     */       }
/*     */       else {
/* 605 */         localFile1 = new File(Util.getJrePath());
/*     */       }
/*     */ 
/* 609 */       initSupportedLocales(localFile1);
/*     */ 
/* 612 */       String str = System.getProperty(SVCTAG_DIR_PATH);
/* 613 */       if (str == null)
/*     */       {
/* 615 */         registerHtmlParent = localFile1.getCanonicalPath();
/*     */       } else {
/* 617 */         File localFile2 = new File(str);
/* 618 */         registerHtmlParent = localFile2.getCanonicalPath();
/* 619 */         if (!localFile2.isDirectory()) {
/* 620 */           throw new InternalError("Path " + str + " set in \"" + SVCTAG_DIR_PATH + "\" property is not a directory");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 625 */     return registerHtmlParent;
/*     */   }
/*     */ 
/*     */   static synchronized File getRegistrationHtmlPage()
/*     */     throws IOException
/*     */   {
/* 633 */     if (!supportRegistration)
/*     */     {
/* 635 */       return null;
/*     */     }
/*     */ 
/* 638 */     String str1 = getRegisterHtmlParent();
/*     */ 
/* 641 */     File localFile1 = new File(str1, "register.html");
/* 642 */     if (!localFile1.exists())
/*     */     {
/* 644 */       generateRegisterHtml(str1);
/*     */     }
/*     */ 
/* 647 */     String str2 = "register";
/* 648 */     Locale localLocale = getDefaultLocale();
/* 649 */     if ((!localLocale.equals(Locale.ENGLISH)) && (supportedLocales.contains(localLocale)))
/*     */     {
/* 653 */       str2 = "register_" + localLocale.toString();
/*     */     }
/* 655 */     File localFile2 = new File(str1, str2 + ".html");
/* 656 */     if (Util.isVerbose()) {
/* 657 */       System.out.print("Offline registration page: " + localFile2);
/* 658 */       System.out.println(localFile2.exists() ? "" : " not exist. Use register.html");
/*     */     }
/*     */ 
/* 661 */     if (localFile2.exists()) {
/* 662 */       return localFile2;
/*     */     }
/* 664 */     return new File(str1, "register.html");
/*     */   }
/*     */ 
/*     */   private static Locale getDefaultLocale()
/*     */   {
/* 670 */     List localList = getCandidateLocales(Locale.getDefault());
/* 671 */     for (Locale localLocale : localList) {
/* 672 */       if (supportedLocales.contains(localLocale)) {
/* 673 */         return localLocale;
/*     */       }
/*     */     }
/* 676 */     return Locale.getDefault();
/*     */   }
/*     */ 
/*     */   private static List<Locale> getCandidateLocales(Locale paramLocale) {
/* 680 */     String str1 = paramLocale.getLanguage();
/* 681 */     String str2 = paramLocale.getCountry();
/* 682 */     String str3 = paramLocale.getVariant();
/*     */ 
/* 684 */     ArrayList localArrayList = new ArrayList(3);
/* 685 */     if (str3.length() > 0) {
/* 686 */       localArrayList.add(paramLocale);
/*     */     }
/* 688 */     if (str2.length() > 0) {
/* 689 */       localArrayList.add(localArrayList.size() == 0 ? paramLocale : new Locale(str1, str2, ""));
/*     */     }
/*     */ 
/* 692 */     if (str1.length() > 0) {
/* 693 */       localArrayList.add(localArrayList.size() == 0 ? paramLocale : new Locale(str1, "", ""));
/*     */     }
/*     */ 
/* 696 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private static void deleteRegistrationHtmlPage() throws IOException
/*     */   {
/* 701 */     String str1 = getRegisterHtmlParent();
/* 702 */     if (str1 == null) {
/* 703 */       return;
/*     */     }
/*     */ 
/* 706 */     for (Locale localLocale : supportedLocales) {
/* 707 */       String str2 = "register";
/* 708 */       if (!localLocale.equals(Locale.ENGLISH)) {
/* 709 */         str2 = str2 + "_" + localLocale.toString();
/*     */       }
/* 711 */       File localFile = new File(str1, str2 + ".html");
/* 712 */       if ((localFile.exists()) && 
/* 713 */         (!localFile.delete()))
/* 714 */         throw new IOException("Failed to delete " + localFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void initSupportedLocales(File paramFile)
/*     */   {
/* 721 */     if (supportedLocales.isEmpty())
/*     */     {
/* 723 */       for (Object localObject3 : knownSupportedLocales) {
/* 724 */         supportedLocales.add(localObject3);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 732 */     ??? = new FilenameFilter() {
/*     */       public boolean accept(File paramAnonymousFile, String paramAnonymousString) {
/* 734 */         String str = paramAnonymousString.toLowerCase();
/* 735 */         if ((str.startsWith("readme")) && (str.endsWith(".html"))) {
/* 736 */           return true;
/*     */         }
/* 738 */         return false;
/*     */       }
/*     */     };
/* 742 */     String[] arrayOfString1 = paramFile.list((FilenameFilter)???);
/* 743 */     for (Object localObject4 : arrayOfString1) {
/* 744 */       String str = localObject4.substring(0, localObject4.length() - ".html".length());
/* 745 */       String[] arrayOfString2 = str.split("_");
/* 746 */       switch (arrayOfString2.length)
/*     */       {
/*     */       case 1:
/* 749 */         break;
/*     */       case 2:
/* 751 */         supportedLocales.add(new Locale(arrayOfString2[1]));
/* 752 */         break;
/*     */       case 3:
/* 754 */         supportedLocales.add(new Locale(arrayOfString2[1], arrayOfString2[2]));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 761 */     if (Util.isVerbose()) {
/* 762 */       System.out.println("Supported locales: ");
/* 763 */       for (??? = supportedLocales.iterator(); ((Iterator)???).hasNext(); ) { Locale localLocale = (Locale)((Iterator)???).next();
/* 764 */         System.out.println(localLocale);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void generateRegisterHtml(String paramString)
/*     */     throws IOException
/*     */   {
/* 776 */     int i = Util.getJdkVersion();
/* 777 */     int j = Util.getUpdateVersion();
/* 778 */     String str1 = "Version " + i;
/* 779 */     if (j > 0)
/*     */     {
/* 781 */       str1 = str1 + " Update " + j;
/*     */     }
/* 783 */     RegistrationData localRegistrationData = getRegistrationData();
/*     */ 
/* 785 */     File localFile1 = new File(svcTagDir.getCanonicalPath(), "jdk_header.png");
/* 786 */     String str2 = localFile1.toURI().toString();
/*     */ 
/* 789 */     StringBuilder localStringBuilder = new StringBuilder();
/* 790 */     String str3 = localRegistrationData.toString().replaceAll("\"", "%22");
/* 791 */     BufferedReader localBufferedReader1 = new BufferedReader(new StringReader(str3));
/*     */     try {
/* 793 */       str4 = null;
/* 794 */       while ((str4 = localBufferedReader1.readLine()) != null)
/* 795 */         localStringBuilder.append(str4.trim());
/*     */     }
/*     */     finally {
/* 798 */       localBufferedReader1.close();
/*     */     }
/*     */ 
/* 801 */     String str4 = "/com/sun/servicetag/resources/register";
/* 802 */     for (Locale localLocale : supportedLocales) {
/* 803 */       String str5 = "register";
/* 804 */       String str6 = str4;
/* 805 */       if (!localLocale.equals(Locale.ENGLISH)) {
/* 806 */         str5 = str5 + "_" + localLocale.toString();
/* 807 */         str6 = str6 + "_" + localLocale.toString();
/*     */       }
/* 809 */       File localFile2 = new File(paramString, str5 + ".html");
/* 810 */       InputStream localInputStream = null;
/* 811 */       BufferedReader localBufferedReader2 = null;
/* 812 */       PrintWriter localPrintWriter = null;
/* 813 */       String str7 = SunConnection.getRegistrationURL(localRegistrationData.getRegistrationURN(), localLocale, String.valueOf(i)).toString();
/*     */       try
/*     */       {
/* 818 */         localInputStream = Installer.class.getResourceAsStream(str6 + ".html");
/* 819 */         if (localInputStream == null)
/*     */         {
/* 821 */           if (Util.isVerbose()) {
/* 822 */             System.out.println("Missing resouce file: " + str6 + ".html");
/*     */           }
/*     */ 
/* 860 */           if (localInputStream != null)
/* 861 */             localInputStream.close();
/*     */         }
/*     */         else
/*     */         {
/* 826 */           if (Util.isVerbose()) {
/* 827 */             System.out.println("Generating " + localFile2 + " from " + str6 + ".html");
/*     */           }
/*     */           try
/*     */           {
/* 831 */             localBufferedReader2 = new BufferedReader(new InputStreamReader(localInputStream, "UTF-8"));
/* 832 */             localPrintWriter = new PrintWriter(localFile2, "UTF-8");
/* 833 */             String str8 = null;
/* 834 */             while ((str8 = localBufferedReader2.readLine()) != null) {
/* 835 */               String str9 = str8;
/* 836 */               if (str8.contains("@@JDK_VERSION@@"))
/* 837 */                 str9 = str8.replace("@@JDK_VERSION@@", str1);
/* 838 */               else if (str8.contains("@@JDK_HEADER_PNG@@"))
/* 839 */                 str9 = str8.replace("@@JDK_HEADER_PNG@@", str2);
/* 840 */               else if (str8.contains("@@REGISTRATION_URL@@"))
/* 841 */                 str9 = str8.replace("@@REGISTRATION_URL@@", str7);
/* 842 */               else if (str8.contains("@@REGISTRATION_PAYLOAD@@")) {
/* 843 */                 str9 = str8.replace("@@REGISTRATION_PAYLOAD@@", localStringBuilder.toString());
/*     */               }
/* 845 */               localPrintWriter.println(str9);
/*     */             }
/* 847 */             localFile2.setReadOnly();
/* 848 */             localPrintWriter.flush();
/*     */           }
/*     */           finally
/*     */           {
/* 852 */             if (localPrintWriter != null) {
/* 853 */               localPrintWriter.close();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 860 */         if (localInputStream != null)
/* 861 */           localInputStream.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */   {
/* 879 */     String str1 = "Manual ";
/* 880 */     String str2 = System.getProperty("java.runtime.name");
/* 881 */     if (str2.startsWith("OpenJDK")) {
/* 882 */       str1 = "OpenJDK ";
/*     */     }
/* 884 */     str1 = str1 + System.getProperty("java.runtime.version");
/* 885 */     if (str1.length() > 63) {
/* 886 */       str1 = str1.substring(0, 63);
/*     */     }
/*     */ 
/* 890 */     int i = 0;
/* 891 */     int j = 0;
/* 892 */     int k = 0;
/* 893 */     int m = 0;
/*     */     Object localObject;
/* 894 */     while (m < paramArrayOfString.length) {
/* 895 */       localObject = paramArrayOfString[m];
/* 896 */       if (((String)localObject).trim().length() == 0)
/*     */       {
/* 898 */         m++;
/*     */       }
/*     */       else
/*     */       {
/* 902 */         if (((String)localObject).equals("-source")) {
/* 903 */           str1 = paramArrayOfString[(++m)];
/* 904 */         } else if (((String)localObject).equals("-delete")) {
/* 905 */           i = 1;
/* 906 */         } else if (((String)localObject).equals("-register")) {
/* 907 */           k = 1;
/*     */         } else {
/* 909 */           usage();
/* 910 */           return;
/*     */         }
/* 912 */         m++;
/*     */       }
/*     */     }
/*     */     try { if (i != 0) {
/* 916 */         deleteRegistrationData();
/*     */       } else {
/* 918 */         localObject = getJavaServiceTagArray();
/* 919 */         String[] arrayOfString = getJreArchs();
/* 920 */         if (localObject.length > arrayOfString.length)
/*     */         {
/* 923 */           updateRegistrationData(str1);
/*     */         }
/*     */         else {
/* 926 */           createServiceTag(str1);
/*     */         }
/*     */       }
/*     */ 
/* 930 */       if (k != 0)
/*     */       {
/* 934 */         localObject = getRegistrationData();
/* 935 */         if ((supportRegistration) && (!((RegistrationData)localObject).getServiceTags().isEmpty())) {
/* 936 */           SunConnection.register((RegistrationData)localObject, getDefaultLocale(), String.valueOf(Util.getJdkVersion()));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 941 */       System.exit(0);
/*     */     } catch (IOException localIOException) {
/* 943 */       System.err.println("I/O Error: " + localIOException.getMessage());
/* 944 */       if (Util.isVerbose())
/* 945 */         localIOException.printStackTrace();
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 948 */       if (Util.isVerbose())
/* 949 */         localIllegalArgumentException.printStackTrace();
/*     */     }
/*     */     catch (Exception localException) {
/* 952 */       System.err.println("Error: " + localException.getMessage());
/* 953 */       if (Util.isVerbose()) {
/* 954 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 957 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   private static void usage() {
/* 961 */     System.out.println("Usage:");
/* 962 */     System.out.print("    " + Installer.class.getName());
/* 963 */     System.out.println(" [-delete|-source <source>|-register]");
/* 964 */     System.out.println("       to create a service tag for the Java platform");
/* 965 */     System.out.println("");
/* 966 */     System.out.println("Internal Options:");
/* 967 */     System.out.println("    -source: to specify the source of the service tag to be created");
/* 968 */     System.out.println("    -delete: to delete the service tag ");
/* 969 */     System.out.println("    -register: to register the JDK");
/* 970 */     System.out.println("    -help:   to print this help message");
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  68 */     String str = System.getProperty(SVCTAG_DIR_PATH);
/*  69 */     if (str == null)
/*  70 */       svcTagDir = new File(Util.getJrePath(), "lib" + File.separator + "servicetag");
/*     */     else {
/*  72 */       svcTagDir = new File(str);
/*     */     }
/*  74 */     serviceTagFile = new File(svcTagDir, "servicetag");
/*  75 */     regXmlFile = new File(svcTagDir, "registration.xml");
/*  76 */     if (System.getProperty(SVCTAG_ENABLE_REGISTRATION) == null)
/*  77 */       supportRegistration = Util.isJdk();
/*     */     else
/*  79 */       supportRegistration = true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.Installer
 * JD-Core Version:    0.6.2
 */