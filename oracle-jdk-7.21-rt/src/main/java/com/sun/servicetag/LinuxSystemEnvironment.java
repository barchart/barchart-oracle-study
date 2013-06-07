/*     */ package com.sun.servicetag;
/*     */ 
/*     */ class LinuxSystemEnvironment extends SystemEnvironment
/*     */ {
/*  54 */   private String dmiInfo = null;
/*     */   private static final int SN = 1;
/*     */   private static final int SYS = 2;
/*     */   private static final int CPU = 3;
/*     */ 
/*     */   LinuxSystemEnvironment()
/*     */   {
/*  48 */     setHostId(getLinuxHostId());
/*  49 */     setSystemModel(getCommandOutput(new String[] { "/bin/uname", "-i" }));
/*  50 */     setSystemManufacturer(getLinuxSystemManufacturer());
/*  51 */     setCpuManufacturer(getLinuxCpuManufacturer());
/*  52 */     setSerialNumber(getLinuxSN());
/*     */   }
/*     */ 
/*     */   private String getLinuxHostId()
/*     */   {
/*  61 */     String str = getCommandOutput(new String[] { "/usr/bin/hostid" });
/*     */ 
/*  63 */     if (str.startsWith("0x")) {
/*  64 */       str = str.substring(2);
/*     */     }
/*  66 */     return str;
/*     */   }
/*     */ 
/*     */   private String getLinuxCpuManufacturer()
/*     */   {
/*  74 */     String str1 = getLinuxPSNInfo(3);
/*  75 */     if (str1.length() > 0) {
/*  76 */       return str1;
/*     */     }
/*     */ 
/*  79 */     String str2 = getFileContent("/proc/cpuinfo");
/*  80 */     for (String str3 : str2.split("\n")) {
/*  81 */       if (str3.contains("vendor_id")) {
/*  82 */         String[] arrayOfString2 = str3.split(":", 2);
/*  83 */         if (arrayOfString2.length > 1) {
/*  84 */           return arrayOfString2[1].trim();
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  90 */     return getLinuxDMIInfo("dmi type 4", "manufacturer");
/*     */   }
/*     */ 
/*     */   private String getLinuxSystemManufacturer()
/*     */   {
/*  99 */     String str = getLinuxPSNInfo(2);
/* 100 */     if (str.length() > 0) {
/* 101 */       return str;
/*     */     }
/*     */ 
/* 105 */     return getLinuxDMIInfo("dmi type 1", "manufacturer");
/*     */   }
/*     */ 
/*     */   private String getLinuxSN()
/*     */   {
/* 113 */     String str = getLinuxPSNInfo(1);
/* 114 */     if (str.length() > 0) {
/* 115 */       return str;
/*     */     }
/*     */ 
/* 119 */     return getLinuxDMIInfo("dmi type 1", "serial number");
/*     */   }
/*     */ 
/*     */   private String getLinuxPSNInfo(int paramInt)
/*     */   {
/* 124 */     String str = getFileContent("/var/run/psn");
/* 125 */     String[] arrayOfString = str.split("\n");
/* 126 */     if (paramInt <= arrayOfString.length) {
/* 127 */       return arrayOfString[(paramInt - 1)];
/*     */     }
/*     */ 
/* 131 */     return "";
/*     */   }
/*     */ 
/*     */   private synchronized String getLinuxDMIInfo(String paramString1, String paramString2)
/*     */   {
/* 151 */     if (this.dmiInfo == null) {
/* 152 */       Thread local1 = new Thread() {
/*     */         public void run() {
/* 154 */           LinuxSystemEnvironment.this.dmiInfo = LinuxSystemEnvironment.this.getCommandOutput(new String[] { "/usr/sbin/dmidecode" });
/*     */         }
/*     */       };
/* 157 */       local1.start();
/*     */       try
/*     */       {
/* 160 */         local1.join(2000L);
/* 161 */         if (local1.isAlive()) {
/* 162 */           local1.interrupt();
/* 163 */           this.dmiInfo = "";
/*     */         }
/*     */       } catch (InterruptedException localInterruptedException) {
/* 166 */         local1.interrupt();
/*     */       }
/*     */     }
/*     */ 
/* 170 */     if (this.dmiInfo.length() == 0) {
/* 171 */       return "";
/*     */     }
/* 173 */     int i = 0;
/* 174 */     for (String str1 : this.dmiInfo.split("\n")) {
/* 175 */       String str2 = str1.toLowerCase();
/* 176 */       if (i != 0) {
/* 177 */         if (str2.contains(paramString2)) {
/* 178 */           String str3 = paramString2 + ":";
/* 179 */           int m = str2.indexOf(str3) + str3.length();
/* 180 */           if ((str2.contains(str3)) && (m < str2.length())) {
/* 181 */             return str2.substring(m).trim();
/*     */           }
/* 183 */           String[] arrayOfString2 = str2.split(":");
/* 184 */           return arrayOfString2[(arrayOfString2.length - 1)];
/*     */         }
/* 186 */       } else if (str2.contains(paramString1)) {
/* 187 */         i = 1;
/*     */       }
/*     */     }
/* 190 */     return "";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.LinuxSystemEnvironment
 * JD-Core Version:    0.6.2
 */