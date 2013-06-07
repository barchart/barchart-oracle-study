/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.File;
/*     */ 
/*     */ class SolarisSystemEnvironment extends SystemEnvironment
/*     */ {
/*     */   private static final String ORACLE = "Oracle Corporation";
/*     */ 
/*     */   SolarisSystemEnvironment()
/*     */   {
/*  49 */     setHostId(getCommandOutput(new String[] { "/usr/bin/hostid" }));
/*  50 */     setSystemModel(getCommandOutput(new String[] { "/usr/bin/uname", "-i" }));
/*  51 */     setSystemManufacturer(getSolarisSystemManufacturer());
/*  52 */     setCpuManufacturer(getSolarisCpuManufacturer());
/*  53 */     setSerialNumber(getSolarisSN());
/*     */   }
/*     */ 
/*     */   private String getSolarisCpuManufacturer()
/*     */   {
/*  62 */     if ("sparc".equalsIgnoreCase(System.getProperty("os.arch"))) {
/*  63 */       return "Oracle Corporation";
/*     */     }
/*     */ 
/*  67 */     return getSmbiosData("4", "Manufacturer: ");
/*     */   }
/*     */ 
/*     */   private String getSolarisSystemManufacturer()
/*     */   {
/*  76 */     if ("sparc".equalsIgnoreCase(System.getProperty("os.arch"))) {
/*  77 */       return "Oracle Corporation";
/*     */     }
/*     */ 
/*  81 */     return getSmbiosData("1", "Manufacturer: ");
/*     */   }
/*     */ 
/*     */   private String getSolarisSN()
/*     */   {
/*  90 */     String str1 = getFileContent("/var/run/psn");
/*  91 */     if (str1.length() > 0) {
/*  92 */       return str1.trim();
/*     */     }
/*     */ 
/*  96 */     String str2 = getSneepSN();
/*  97 */     if (str2.length() > 0) {
/*  98 */       return str2;
/*     */     }
/*     */ 
/* 102 */     str2 = getSmbiosData("1", "Serial Number: ");
/* 103 */     if (str2.length() > 0) {
/* 104 */       return str2;
/*     */     }
/*     */ 
/* 108 */     str2 = getSmbiosData("3", "Serial Number: ");
/* 109 */     if (str2.length() > 0) {
/* 110 */       return str2;
/*     */     }
/*     */ 
/* 114 */     return "";
/*     */   }
/*     */ 
/*     */   private String getSmbiosData(String paramString1, String paramString2)
/*     */   {
/* 126 */     String str1 = getCommandOutput(new String[] { "/usr/sbin/smbios", "-t", paramString1 });
/* 127 */     for (String str2 : str1.split("\n")) {
/* 128 */       if (str2.contains(paramString2)) {
/* 129 */         int k = str2.indexOf(paramString2) + paramString2.length();
/* 130 */         if (k < str2.length()) {
/* 131 */           String str3 = str2.substring(k).trim();
/* 132 */           String str4 = str3.toLowerCase();
/* 133 */           if ((!str4.startsWith("not available")) && (!str4.startsWith("to be filled by o.e.m")))
/*     */           {
/* 135 */             return str3;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 141 */     return "";
/*     */   }
/*     */ 
/*     */   private String getSneepSN() {
/* 145 */     String str1 = getCommandOutput(new String[] { "pkgparam", "SUNWsneep", "BASEDIR" });
/* 146 */     File localFile = new File(str1 + "/bin/sneep");
/* 147 */     if (localFile.exists()) {
/* 148 */       String str2 = getCommandOutput(new String[] { str1 + "/bin/sneep" });
/* 149 */       if (str2.equalsIgnoreCase("unknown")) {
/* 150 */         return "";
/*     */       }
/* 152 */       return str2;
/*     */     }
/*     */ 
/* 155 */     return "";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.SolarisSystemEnvironment
 * JD-Core Version:    0.6.2
 */