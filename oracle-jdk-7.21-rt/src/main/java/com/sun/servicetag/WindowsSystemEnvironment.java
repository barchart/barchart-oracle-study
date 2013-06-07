/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ 
/*     */ class WindowsSystemEnvironment extends SystemEnvironment
/*     */ {
/*     */   WindowsSystemEnvironment()
/*     */   {
/*  55 */     getWmicResult("computersystem", "get", "model");
/*     */ 
/*  57 */     setSystemModel(getWmicResult("computersystem", "get", "model"));
/*  58 */     setSystemManufacturer(getWmicResult("computersystem", "get", "manufacturer"));
/*  59 */     setSerialNumber(getWmicResult("bios", "get", "serialnumber"));
/*     */ 
/*  61 */     String str = getWmicResult("cpu", "get", "manufacturer");
/*     */     Object localObject;
/*  64 */     if (str.length() == 0) {
/*  65 */       localObject = System.getenv("processor_identifer");
/*  66 */       if (localObject != null) {
/*  67 */         String[] arrayOfString = ((String)localObject).split(",");
/*  68 */         str = arrayOfString[(arrayOfString.length - 1)].trim();
/*     */       }
/*     */     }
/*  71 */     setCpuManufacturer(str);
/*     */     try
/*     */     {
/*  76 */       localObject = new File("TempWmicBatchFile.bat");
/*  77 */       if (((File)localObject).exists())
/*  78 */         ((File)localObject).delete();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getWmicResult(String paramString1, String paramString2, String paramString3)
/*     */   {
/* 103 */     Object localObject1 = "";
/* 104 */     BufferedReader localBufferedReader = null;
/*     */     try {
/* 106 */       ProcessBuilder localProcessBuilder = new ProcessBuilder(new String[] { "cmd", "/C", "WMIC", paramString1, paramString2, paramString3 });
/* 107 */       Process localProcess = localProcessBuilder.start();
/*     */ 
/* 110 */       BufferedWriter localBufferedWriter = null;
/*     */       try {
/* 112 */         localBufferedWriter = new BufferedWriter(new OutputStreamWriter(localProcess.getOutputStream()));
/*     */ 
/* 114 */         localBufferedWriter.write(13);
/* 115 */         localBufferedWriter.flush();
/*     */       } finally {
/* 117 */         if (localBufferedWriter != null) {
/* 118 */           localBufferedWriter.close();
/*     */         }
/*     */       }
/*     */ 
/* 122 */       localProcess.waitFor();
/* 123 */       if (localProcess.exitValue() == 0) {
/* 124 */         localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
/* 125 */         String str = null;
/* 126 */         while ((str = localBufferedReader.readLine()) != null) {
/* 127 */           str = str.trim();
/* 128 */           if (str.length() != 0)
/*     */           {
/* 131 */             localObject1 = str;
/*     */           }
/*     */         }
/* 134 */         return localObject1;
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/*     */     finally {
/* 140 */       if (localBufferedReader != null)
/*     */         try {
/* 142 */           localBufferedReader.close();
/*     */         }
/*     */         catch (IOException localIOException4)
/*     */         {
/*     */         }
/*     */     }
/* 148 */     return ((String)localObject1).trim();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.WindowsSystemEnvironment
 * JD-Core Version:    0.6.2
 */