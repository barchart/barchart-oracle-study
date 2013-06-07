/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Registry
/*     */ {
/*     */   private static final String STCLIENT_SOLARIS = "/usr/bin/stclient";
/*     */   private static final String STCLIENT_LINUX = "/opt/sun/servicetag/bin/stclient";
/*     */   private static final int ST_ERR_NOT_AUTH = 245;
/*     */   private static final int ST_ERR_REC_NOT_FOUND = 225;
/*     */   private static final String INSTANCE_URN_DESC = "Product instance URN=";
/*  69 */   private static boolean initialized = false;
/*  70 */   private static File stclient = null;
/*  71 */   private static String stclientPath = null;
/*  72 */   private static Registry registry = new Registry();
/*     */ 
/*  75 */   private static String SVCTAG_STCLIENT_CMD = "servicetag.stclient.cmd";
/*  76 */   private static String SVCTAG_STHELPER_SUPPORTED = "servicetag.sthelper.supported";
/*     */ 
/*     */   private static synchronized String getSTclient()
/*     */   {
/*  82 */     if (!initialized)
/*     */     {
/*  84 */       String str1 = System.getProperty("os.name");
/*  85 */       if (str1.equals("SunOS"))
/*  86 */         stclient = new File("/usr/bin/stclient");
/*  87 */       else if (str1.equals("Linux"))
/*  88 */         stclient = new File("/opt/sun/servicetag/bin/stclient");
/*  89 */       else if (str1.startsWith("Windows")) {
/*  90 */         stclient = Util.getWindowsStClientFile();
/*     */       }
/*  92 */       else if (Util.isVerbose()) {
/*  93 */         System.out.println("Running on unsupported platform");
/*     */       }
/*     */ 
/*  96 */       initialized = true;
/*     */     }
/*     */ 
/*  99 */     boolean bool = true;
/* 100 */     if (System.getProperty(SVCTAG_STHELPER_SUPPORTED) != null)
/*     */     {
/* 102 */       bool = Boolean.getBoolean(SVCTAG_STHELPER_SUPPORTED);
/*     */     }
/*     */ 
/* 105 */     if (!bool)
/*     */     {
/* 107 */       return null;
/*     */     }
/*     */ 
/* 111 */     String str2 = System.getProperty(SVCTAG_STCLIENT_CMD);
/* 112 */     if (str2 != null) {
/* 113 */       return str2;
/*     */     }
/*     */ 
/* 119 */     if ((stclientPath == null) && (stclient != null) && (stclient.exists())) {
/* 120 */       stclientPath = stclient.getAbsolutePath();
/*     */     }
/* 122 */     return stclientPath;
/*     */   }
/*     */ 
/*     */   public static Registry getSystemRegistry()
/*     */   {
/* 136 */     if (isSupported()) {
/* 137 */       return registry;
/*     */     }
/* 139 */     throw new UnsupportedOperationException("Registry class is not supported");
/*     */   }
/*     */ 
/*     */   public static synchronized boolean isSupported()
/*     */   {
/* 150 */     return getSTclient() != null;
/*     */   }
/*     */ 
/*     */   private static List<String> getCommandList()
/*     */   {
/* 155 */     ArrayList localArrayList = new ArrayList();
/* 156 */     if (System.getProperty(SVCTAG_STCLIENT_CMD) != null)
/*     */     {
/* 169 */       String str1 = getSTclient();
/* 170 */       int i = str1.length();
/* 171 */       int j = 0;
/*     */ 
/* 195 */       for (; j < i; 
/* 195 */         goto 124)
/*     */       {
/* 173 */         int k = 32;
/* 174 */         if (str1.charAt(j) == '"') {
/* 175 */           k = 34;
/* 176 */           j++;
/*     */         }
/*     */ 
/* 180 */         for (int m = j + 1; (m < i) && 
/* 181 */           (str1.charAt(m) != k); m++);
/* 186 */         if (j == m - 1)
/*     */         {
/* 188 */           localArrayList.add("\"\"");
/*     */         }
/*     */         else {
/* 191 */           localArrayList.add(str1.substring(j, m));
/*     */         }
/*     */ 
/* 195 */         j = m + 1; if ((j < i) && 
/* 196 */           (Character.isSpaceChar(str1.charAt(j)))) {
/* 195 */           j++;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 201 */       if (Util.isVerbose()) {
/* 202 */         System.out.println("Command list:");
/* 203 */         for (String str2 : localArrayList)
/* 204 */           System.out.println(str2);
/*     */       }
/*     */     }
/*     */     else {
/* 208 */       localArrayList.add(getSTclient());
/*     */     }
/* 210 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private static ServiceTag checkReturnError(int paramInt, String paramString, ServiceTag paramServiceTag)
/*     */     throws IOException
/*     */   {
/* 219 */     switch (paramInt) {
/*     */     case 225:
/* 221 */       return null;
/*     */     case 245:
/* 223 */       if (paramServiceTag != null) {
/* 224 */         throw new UnauthorizedAccessException("Not authorized to access " + paramServiceTag.getInstanceURN() + " installer_uid=" + paramServiceTag.getInstallerUID());
/*     */       }
/*     */ 
/* 228 */       throw new UnauthorizedAccessException("Not authorized:" + paramString);
/*     */     }
/*     */ 
/* 232 */     throw new IOException("stclient exits with error (" + paramInt + ")\n" + paramString);
/*     */   }
/*     */ 
/*     */   public ServiceTag addServiceTag(ServiceTag paramServiceTag)
/*     */     throws IOException
/*     */   {
/* 255 */     List localList = getCommandList();
/* 256 */     localList.add("-a");
/* 257 */     if (paramServiceTag.getInstanceURN().length() > 0) {
/* 258 */       localObject1 = getServiceTag(paramServiceTag.getInstanceURN());
/* 259 */       if (localObject1 != null) {
/* 260 */         throw new IllegalArgumentException("Instance_urn = " + paramServiceTag.getInstanceURN() + " already exists");
/*     */       }
/*     */ 
/* 263 */       localList.add("-i");
/* 264 */       localList.add(paramServiceTag.getInstanceURN());
/*     */     }
/* 266 */     localList.add("-p");
/* 267 */     localList.add(paramServiceTag.getProductName());
/* 268 */     localList.add("-e");
/* 269 */     localList.add(paramServiceTag.getProductVersion());
/* 270 */     localList.add("-t");
/* 271 */     localList.add(paramServiceTag.getProductURN());
/* 272 */     if (paramServiceTag.getProductParentURN().length() > 0) {
/* 273 */       localList.add("-F");
/* 274 */       localList.add(paramServiceTag.getProductParentURN());
/*     */     }
/* 276 */     localList.add("-P");
/* 277 */     localList.add(paramServiceTag.getProductParent());
/* 278 */     if (paramServiceTag.getProductDefinedInstanceID().length() > 0) {
/* 279 */       localList.add("-I");
/* 280 */       localList.add(paramServiceTag.getProductDefinedInstanceID());
/*     */     }
/* 282 */     localList.add("-m");
/* 283 */     localList.add(paramServiceTag.getProductVendor());
/* 284 */     localList.add("-A");
/* 285 */     localList.add(paramServiceTag.getPlatformArch());
/* 286 */     localList.add("-z");
/* 287 */     localList.add(paramServiceTag.getContainer());
/* 288 */     localList.add("-S");
/* 289 */     localList.add(paramServiceTag.getSource());
/*     */ 
/* 291 */     Object localObject1 = null;
/*     */     try {
/* 293 */       ProcessBuilder localProcessBuilder = new ProcessBuilder(localList);
/* 294 */       Process localProcess = localProcessBuilder.start();
/* 295 */       String str1 = Util.commandOutput(localProcess);
/* 296 */       if (Util.isVerbose()) {
/* 297 */         System.out.println("Output from stclient -a command:");
/* 298 */         System.out.println(str1);
/*     */       }
/* 300 */       String str2 = "";
/*     */       Object localObject2;
/* 301 */       if (localProcess.exitValue() == 0)
/*     */       {
/* 303 */         localObject1 = new BufferedReader(new StringReader(str1));
/* 304 */         localObject2 = null;
/* 305 */         while ((localObject2 = ((BufferedReader)localObject1).readLine()) != null) {
/* 306 */           localObject2 = ((String)localObject2).trim();
/* 307 */           if (((String)localObject2).startsWith("Product instance URN=")) {
/* 308 */             str2 = ((String)localObject2).substring("Product instance URN=".length());
/*     */           }
/*     */         }
/*     */ 
/* 312 */         if (str2.length() == 0) {
/* 313 */           throw new IOException("Error in creating service tag:\n" + str1);
/*     */         }
/*     */ 
/* 316 */         return getServiceTag(str2);
/*     */       }
/* 318 */       return checkReturnError(localProcess.exitValue(), str1, paramServiceTag);
/*     */     }
/*     */     finally {
/* 321 */       if (localObject1 != null)
/* 322 */         ((BufferedReader)localObject1).close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServiceTag removeServiceTag(String paramString)
/*     */     throws IOException
/*     */   {
/* 344 */     ServiceTag localServiceTag = getServiceTag(paramString);
/* 345 */     if (localServiceTag == null) {
/* 346 */       return null;
/*     */     }
/*     */ 
/* 349 */     List localList = getCommandList();
/* 350 */     localList.add("-d");
/* 351 */     localList.add("-i");
/* 352 */     localList.add(paramString);
/*     */ 
/* 354 */     ProcessBuilder localProcessBuilder = new ProcessBuilder(localList);
/* 355 */     Process localProcess = localProcessBuilder.start();
/* 356 */     String str = Util.commandOutput(localProcess);
/* 357 */     if (Util.isVerbose()) {
/* 358 */       System.out.println("Output from stclient -d command:");
/* 359 */       System.out.println(str);
/*     */     }
/* 361 */     if (localProcess.exitValue() == 0) {
/* 362 */       return localServiceTag;
/*     */     }
/* 364 */     return checkReturnError(localProcess.exitValue(), str, localServiceTag);
/*     */   }
/*     */ 
/*     */   public ServiceTag updateServiceTag(String paramString1, String paramString2)
/*     */     throws IOException
/*     */   {
/* 388 */     ServiceTag localServiceTag = getServiceTag(paramString1);
/* 389 */     if (localServiceTag == null) {
/* 390 */       return null;
/*     */     }
/*     */ 
/* 393 */     List localList = getCommandList();
/* 394 */     localList.add("-u");
/* 395 */     localList.add("-i");
/* 396 */     localList.add(paramString1);
/* 397 */     localList.add("-I");
/* 398 */     if (paramString2.length() > 0)
/* 399 */       localList.add(paramString2);
/*     */     else {
/* 401 */       localList.add("\"\"");
/*     */     }
/*     */ 
/* 404 */     ProcessBuilder localProcessBuilder = new ProcessBuilder(localList);
/* 405 */     Process localProcess = localProcessBuilder.start();
/* 406 */     String str = Util.commandOutput(localProcess);
/* 407 */     if (Util.isVerbose()) {
/* 408 */       System.out.println("Output from stclient -u command:");
/* 409 */       System.out.println(str);
/*     */     }
/*     */ 
/* 412 */     if (localProcess.exitValue() == 0) {
/* 413 */       return getServiceTag(paramString1);
/*     */     }
/* 415 */     return checkReturnError(localProcess.exitValue(), str, localServiceTag);
/*     */   }
/*     */ 
/*     */   public ServiceTag getServiceTag(String paramString)
/*     */     throws IOException
/*     */   {
/* 430 */     if (paramString == null) {
/* 431 */       throw new NullPointerException("instanceURN is null");
/*     */     }
/*     */ 
/* 434 */     List localList = getCommandList();
/* 435 */     localList.add("-g");
/* 436 */     localList.add("-i");
/* 437 */     localList.add(paramString);
/*     */ 
/* 439 */     ProcessBuilder localProcessBuilder = new ProcessBuilder(localList);
/* 440 */     Process localProcess = localProcessBuilder.start();
/* 441 */     String str = Util.commandOutput(localProcess);
/* 442 */     if (Util.isVerbose()) {
/* 443 */       System.out.println("Output from stclient -g command:");
/* 444 */       System.out.println(str);
/*     */     }
/* 446 */     if (localProcess.exitValue() == 0) {
/* 447 */       return parseServiceTag(str);
/*     */     }
/* 449 */     return checkReturnError(localProcess.exitValue(), str, null);
/*     */   }
/*     */ 
/*     */   private ServiceTag parseServiceTag(String paramString) throws IOException
/*     */   {
/* 454 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 456 */       Properties localProperties = new Properties();
/*     */ 
/* 458 */       localBufferedReader = new BufferedReader(new StringReader(paramString));
/* 459 */       String str1 = null;
/* 460 */       while ((str1 = localBufferedReader.readLine()) != null) {
/* 461 */         if ((str1 = str1.trim()).length() > 0) {
/* 462 */           localObject1 = str1.trim().split("=", 2);
/* 463 */           if (localObject1.length == 2)
/* 464 */             localProperties.setProperty(localObject1[0].trim(), localObject1[1].trim());
/*     */           else {
/* 466 */             localProperties.setProperty(localObject1[0].trim(), "");
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 471 */       Object localObject1 = localProperties.getProperty("instance_urn");
/* 472 */       String str2 = localProperties.getProperty("product_name");
/* 473 */       String str3 = localProperties.getProperty("product_version");
/* 474 */       String str4 = localProperties.getProperty("product_urn");
/* 475 */       String str5 = localProperties.getProperty("product_parent");
/* 476 */       String str6 = localProperties.getProperty("product_parent_urn");
/* 477 */       String str7 = localProperties.getProperty("product_defined_inst_id");
/*     */ 
/* 479 */       String str8 = localProperties.getProperty("product_vendor");
/* 480 */       String str9 = localProperties.getProperty("platform_arch");
/* 481 */       String str10 = localProperties.getProperty("container");
/* 482 */       String str11 = localProperties.getProperty("source");
/* 483 */       int i = Util.getIntValue(localProperties.getProperty("installer_uid"));
/*     */ 
/* 485 */       Date localDate = Util.parseTimestamp(localProperties.getProperty("timestamp"));
/*     */ 
/* 488 */       return new ServiceTag((String)localObject1, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, i, localDate);
/*     */     }
/*     */     finally
/*     */     {
/* 502 */       if (localBufferedReader != null)
/* 503 */         localBufferedReader.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<ServiceTag> findServiceTags(String paramString)
/*     */     throws IOException
/*     */   {
/* 520 */     if (paramString == null) {
/* 521 */       throw new NullPointerException("productURN is null");
/*     */     }
/*     */ 
/* 524 */     List localList = getCommandList();
/* 525 */     localList.add("-f");
/* 526 */     localList.add("-t");
/* 527 */     localList.add(paramString);
/*     */ 
/* 529 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 531 */       ProcessBuilder localProcessBuilder = new ProcessBuilder(localList);
/* 532 */       Process localProcess = localProcessBuilder.start();
/* 533 */       String str1 = Util.commandOutput(localProcess);
/*     */ 
/* 535 */       HashSet localHashSet = new HashSet();
/*     */       Object localObject1;
/* 536 */       if (localProcess.exitValue() == 0)
/*     */       {
/* 538 */         localBufferedReader = new BufferedReader(new StringReader(str1));
/* 539 */         localObject1 = null;
/* 540 */         while ((localObject1 = localBufferedReader.readLine()) != null) {
/* 541 */           String str2 = ((String)localObject1).trim();
/* 542 */           if (str2.startsWith("urn:st:"))
/* 543 */             localHashSet.add(getServiceTag(str2));
/*     */         }
/*     */       }
/*     */       else {
/* 547 */         checkReturnError(localProcess.exitValue(), str1, null);
/*     */       }
/* 549 */       return localHashSet;
/*     */     } finally {
/* 551 */       if (localBufferedReader != null)
/* 552 */         localBufferedReader.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.Registry
 * JD-Core Version:    0.6.2
 */