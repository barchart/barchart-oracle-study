/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ 
/*     */ public class SystemEnvironment
/*     */ {
/*     */   private String hostname;
/*     */   private String hostId;
/*     */   private String osName;
/*     */   private String osVersion;
/*     */   private String osArchitecture;
/*     */   private String systemModel;
/*     */   private String systemManufacturer;
/*     */   private String cpuManufacturer;
/*     */   private String serialNumber;
/*  57 */   private static SystemEnvironment sysEnv = null;
/*     */ 
/*     */   public static synchronized SystemEnvironment getSystemEnvironment() {
/*  60 */     if (sysEnv == null) {
/*  61 */       String str = System.getProperty("os.name");
/*  62 */       if (str.equals("SunOS"))
/*  63 */         sysEnv = new SolarisSystemEnvironment();
/*  64 */       else if (str.equals("Linux"))
/*  65 */         sysEnv = new LinuxSystemEnvironment();
/*  66 */       else if (str.startsWith("Windows"))
/*  67 */         sysEnv = new WindowsSystemEnvironment();
/*     */       else {
/*  69 */         sysEnv = new SystemEnvironment();
/*     */       }
/*     */     }
/*  72 */     return sysEnv;
/*     */   }
/*     */ 
/*     */   SystemEnvironment()
/*     */   {
/*     */     try {
/*  78 */       this.hostname = InetAddress.getLocalHost().getHostName();
/*     */     } catch (UnknownHostException localUnknownHostException) {
/*  80 */       this.hostname = "Unknown host";
/*     */     }
/*  82 */     this.hostId = "";
/*  83 */     this.osName = System.getProperty("os.name");
/*  84 */     this.osVersion = System.getProperty("os.version");
/*  85 */     this.osArchitecture = System.getProperty("os.arch");
/*  86 */     this.systemModel = "";
/*  87 */     this.systemManufacturer = "";
/*  88 */     this.cpuManufacturer = "";
/*  89 */     this.serialNumber = "";
/*     */   }
/*     */ 
/*     */   public void setHostname(String paramString)
/*     */   {
/*  98 */     this.hostname = paramString;
/*     */   }
/*     */ 
/*     */   public void setOsName(String paramString)
/*     */   {
/* 106 */     this.osName = paramString;
/*     */   }
/*     */ 
/*     */   public void setOsVersion(String paramString)
/*     */   {
/* 114 */     this.osVersion = paramString;
/*     */   }
/*     */ 
/*     */   public void setOsArchitecture(String paramString)
/*     */   {
/* 122 */     this.osArchitecture = paramString;
/*     */   }
/*     */ 
/*     */   public void setSystemModel(String paramString)
/*     */   {
/* 130 */     this.systemModel = paramString;
/*     */   }
/*     */ 
/*     */   public void setSystemManufacturer(String paramString)
/*     */   {
/* 138 */     this.systemManufacturer = paramString;
/*     */   }
/*     */ 
/*     */   public void setCpuManufacturer(String paramString)
/*     */   {
/* 146 */     this.cpuManufacturer = paramString;
/*     */   }
/*     */ 
/*     */   public void setSerialNumber(String paramString)
/*     */   {
/* 154 */     this.serialNumber = paramString;
/*     */   }
/*     */ 
/*     */   public void setHostId(String paramString)
/*     */   {
/* 162 */     if ((paramString == null) || (paramString.equals("null"))) {
/* 163 */       paramString = "";
/*     */     }
/* 165 */     if (paramString.length() > 16) {
/* 166 */       paramString = paramString.substring(0, 16);
/*     */     }
/* 168 */     this.hostId = paramString;
/*     */   }
/*     */ 
/*     */   public String getHostname()
/*     */   {
/* 176 */     return this.hostname;
/*     */   }
/*     */ 
/*     */   public String getOsName()
/*     */   {
/* 184 */     return this.osName;
/*     */   }
/*     */ 
/*     */   public String getOsVersion()
/*     */   {
/* 192 */     return this.osVersion;
/*     */   }
/*     */ 
/*     */   public String getOsArchitecture()
/*     */   {
/* 200 */     return this.osArchitecture;
/*     */   }
/*     */ 
/*     */   public String getSystemModel()
/*     */   {
/* 208 */     return this.systemModel;
/*     */   }
/*     */ 
/*     */   public String getSystemManufacturer()
/*     */   {
/* 216 */     return this.systemManufacturer;
/*     */   }
/*     */ 
/*     */   public String getSerialNumber()
/*     */   {
/* 224 */     return this.serialNumber;
/*     */   }
/*     */ 
/*     */   public String getHostId()
/*     */   {
/* 232 */     return this.hostId;
/*     */   }
/*     */ 
/*     */   public String getCpuManufacturer()
/*     */   {
/* 240 */     return this.cpuManufacturer;
/*     */   }
/*     */ 
/*     */   protected String getCommandOutput(String[] paramArrayOfString) {
/* 244 */     StringBuilder localStringBuilder = new StringBuilder();
/* 245 */     BufferedReader localBufferedReader = null;
/* 246 */     Process localProcess = null;
/*     */     try {
/* 248 */       ProcessBuilder localProcessBuilder = new ProcessBuilder(paramArrayOfString);
/* 249 */       localProcess = localProcessBuilder.start();
/* 250 */       localProcess.waitFor();
/*     */ 
/* 252 */       if (localProcess.exitValue() == 0) {
/* 253 */         localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
/* 254 */         str = null;
/* 255 */         while ((str = localBufferedReader.readLine()) != null) {
/* 256 */           str = str.trim();
/* 257 */           if (str.length() > 0) {
/* 258 */             if (localStringBuilder.length() > 0) {
/* 259 */               localStringBuilder.append("\n");
/*     */             }
/* 261 */             localStringBuilder.append(str);
/*     */           }
/*     */         }
/*     */       }
/* 265 */       return localStringBuilder.toString();
/*     */     }
/*     */     catch (InterruptedException localInterruptedException) {
/* 268 */       if (localProcess != null) {
/* 269 */         localProcess.destroy();
/*     */       }
/* 271 */       return "";
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */       String str;
/* 274 */       return "";
/*     */     } finally {
/* 276 */       if (localProcess != null) {
/*     */         try {
/* 278 */           localProcess.getErrorStream().close();
/*     */         }
/*     */         catch (IOException localIOException13) {
/*     */         }
/*     */         try {
/* 283 */           localProcess.getInputStream().close();
/*     */         }
/*     */         catch (IOException localIOException14) {
/*     */         }
/*     */         try {
/* 288 */           localProcess.getOutputStream().close();
/*     */         }
/*     */         catch (IOException localIOException15) {
/*     */         }
/* 292 */         localProcess = null;
/*     */       }
/* 294 */       if (localBufferedReader != null)
/*     */         try {
/* 296 */           localBufferedReader.close();
/*     */         }
/*     */         catch (IOException localIOException16)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getFileContent(String paramString) {
/* 305 */     File localFile = new File(paramString);
/* 306 */     if (!localFile.exists()) {
/* 307 */       return "";
/*     */     }
/*     */ 
/* 310 */     StringBuilder localStringBuilder = new StringBuilder();
/* 311 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 313 */       localBufferedReader = new BufferedReader(new FileReader(localFile));
/* 314 */       String str1 = null;
/* 315 */       while ((str1 = localBufferedReader.readLine()) != null) {
/* 316 */         str1 = str1.trim();
/* 317 */         if (str1.length() > 0) {
/* 318 */           if (localStringBuilder.length() > 0) {
/* 319 */             localStringBuilder.append("\n");
/*     */           }
/* 321 */           localStringBuilder.append(str1);
/*     */         }
/*     */       }
/* 324 */       return localStringBuilder.toString();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */       String str2;
/* 327 */       return "";
/*     */     } finally {
/* 329 */       if (localBufferedReader != null)
/*     */         try {
/* 331 */           localBufferedReader.close();
/*     */         }
/*     */         catch (IOException localIOException3)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.SystemEnvironment
 * JD-Core Version:    0.6.2
 */