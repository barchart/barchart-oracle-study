/*     */ package com.sun.org.apache.xml.internal.security.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class JavaUtils
/*     */ {
/*  37 */   static Logger log = Logger.getLogger(JavaUtils.class.getName());
/*     */ 
/*     */   public static byte[] getBytesFromFile(String paramString)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/*  56 */     byte[] arrayOfByte1 = null;
/*     */ 
/*  58 */     FileInputStream localFileInputStream = new FileInputStream(paramString);
/*     */     try {
/*  60 */       UnsyncByteArrayOutputStream localUnsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
/*  61 */       byte[] arrayOfByte2 = new byte[1024];
/*     */       int i;
/*  64 */       while ((i = localFileInputStream.read(arrayOfByte2)) > 0) {
/*  65 */         localUnsyncByteArrayOutputStream.write(arrayOfByte2, 0, i);
/*     */       }
/*     */ 
/*  68 */       arrayOfByte1 = localUnsyncByteArrayOutputStream.toByteArray();
/*     */     } finally {
/*  70 */       localFileInputStream.close();
/*     */     }
/*     */ 
/*  73 */     return arrayOfByte1;
/*     */   }
/*     */ 
/*     */   public static void writeBytesToFilename(String paramString, byte[] paramArrayOfByte)
/*     */   {
/*  84 */     FileOutputStream localFileOutputStream = null;
/*     */     try {
/*  86 */       if ((paramString != null) && (paramArrayOfByte != null)) {
/*  87 */         File localFile = new File(paramString);
/*     */ 
/*  89 */         localFileOutputStream = new FileOutputStream(localFile);
/*     */ 
/*  91 */         localFileOutputStream.write(paramArrayOfByte);
/*  92 */         localFileOutputStream.close();
/*     */       } else {
/*  94 */         log.log(Level.FINE, "writeBytesToFilename got null byte[] pointed");
/*     */       }
/*     */     } catch (IOException localIOException1) {
/*  97 */       if (localFileOutputStream != null)
/*     */         try {
/*  99 */           localFileOutputStream.close();
/*     */         }
/*     */         catch (IOException localIOException2)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesFromStream(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 118 */     byte[] arrayOfByte1 = null;
/*     */ 
/* 120 */     UnsyncByteArrayOutputStream localUnsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
/* 121 */     byte[] arrayOfByte2 = new byte[1024];
/*     */     int i;
/* 124 */     while ((i = paramInputStream.read(arrayOfByte2)) > 0) {
/* 125 */       localUnsyncByteArrayOutputStream.write(arrayOfByte2, 0, i);
/*     */     }
/*     */ 
/* 128 */     arrayOfByte1 = localUnsyncByteArrayOutputStream.toByteArray();
/* 129 */     return arrayOfByte1;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.JavaUtils
 * JD-Core Version:    0.6.2
 */