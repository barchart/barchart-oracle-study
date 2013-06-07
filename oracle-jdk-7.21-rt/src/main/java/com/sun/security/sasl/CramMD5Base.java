/*     */ package com.sun.security.sasl;
/*     */ 
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.logging.Logger;
/*     */ import javax.security.sasl.SaslException;
/*     */ 
/*     */ abstract class CramMD5Base
/*     */ {
/*  44 */   protected boolean completed = false;
/*  45 */   protected boolean aborted = false;
/*     */   protected byte[] pw;
/*     */   private static final int MD5_BLOCKSIZE = 64;
/*     */   private static final String SASL_LOGGER_NAME = "javax.security.sasl";
/*     */   protected static Logger logger;
/*     */ 
/*     */   protected CramMD5Base()
/*     */   {
/*  49 */     initLogger();
/*     */   }
/*     */ 
/*     */   public String getMechanismName()
/*     */   {
/*  58 */     return "CRAM-MD5";
/*     */   }
/*     */ 
/*     */   public boolean isComplete()
/*     */   {
/*  68 */     return this.completed;
/*     */   }
/*     */ 
/*     */   public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws SaslException
/*     */   {
/*  78 */     if (this.completed) {
/*  79 */       throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
/*     */     }
/*     */ 
/*  82 */     throw new IllegalStateException("CRAM-MD5 authentication not completed");
/*     */   }
/*     */ 
/*     */   public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws SaslException
/*     */   {
/*  93 */     if (this.completed) {
/*  94 */       throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
/*     */     }
/*     */ 
/*  97 */     throw new IllegalStateException("CRAM-MD5 authentication not completed");
/*     */   }
/*     */ 
/*     */   public Object getNegotiatedProperty(String paramString)
/*     */   {
/* 112 */     if (this.completed) {
/* 113 */       if (paramString.equals("javax.security.sasl.qop")) {
/* 114 */         return "auth";
/*     */       }
/* 116 */       return null;
/*     */     }
/*     */ 
/* 119 */     throw new IllegalStateException("CRAM-MD5 authentication not completed");
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */     throws SaslException
/*     */   {
/* 125 */     clearPassword();
/*     */   }
/*     */ 
/*     */   protected void clearPassword() {
/* 129 */     if (this.pw != null)
/*     */     {
/* 131 */       for (int i = 0; i < this.pw.length; i++) {
/* 132 */         this.pw[i] = 0;
/*     */       }
/* 134 */       this.pw = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize() {
/* 139 */     clearPassword();
/*     */   }
/*     */ 
/*     */   static final String HMAC_MD5(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 159 */     MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
/*     */ 
/* 162 */     if (paramArrayOfByte1.length > 64) {
/* 163 */       paramArrayOfByte1 = localMessageDigest.digest(paramArrayOfByte1);
/*     */     }
/*     */ 
/* 166 */     byte[] arrayOfByte1 = new byte[64];
/* 167 */     byte[] arrayOfByte2 = new byte[64];
/*     */ 
/* 172 */     for (int i = 0; i < 64; i++) {
/* 173 */       for (; i < paramArrayOfByte1.length; i++) {
/* 174 */         arrayOfByte1[i] = paramArrayOfByte1[i];
/* 175 */         arrayOfByte2[i] = paramArrayOfByte1[i];
/*     */       }
/* 177 */       arrayOfByte1[i] = 0;
/* 178 */       arrayOfByte2[i] = 0;
/*     */     }
/*     */ 
/* 182 */     for (i = 0; i < 64; i++)
/*     */     {
/*     */       int tmp100_98 = i;
/*     */       byte[] tmp100_97 = arrayOfByte1; tmp100_97[tmp100_98] = ((byte)(tmp100_97[tmp100_98] ^ 0x36));
/*     */       int tmp111_109 = i;
/*     */       byte[] tmp111_107 = arrayOfByte2; tmp111_107[tmp111_109] = ((byte)(tmp111_107[tmp111_109] ^ 0x5C));
/*     */     }
/*     */ 
/* 188 */     localMessageDigest.update(arrayOfByte1);
/* 189 */     localMessageDigest.update(paramArrayOfByte2);
/* 190 */     byte[] arrayOfByte3 = localMessageDigest.digest();
/*     */ 
/* 193 */     localMessageDigest.update(arrayOfByte2);
/* 194 */     localMessageDigest.update(arrayOfByte3);
/* 195 */     arrayOfByte3 = localMessageDigest.digest();
/*     */ 
/* 198 */     StringBuffer localStringBuffer = new StringBuffer();
/*     */ 
/* 200 */     for (i = 0; i < arrayOfByte3.length; i++) {
/* 201 */       if ((arrayOfByte3[i] & 0xFF) < 16) {
/* 202 */         localStringBuffer.append("0" + Integer.toHexString(arrayOfByte3[i] & 0xFF));
/*     */       }
/*     */       else {
/* 205 */         localStringBuffer.append(Integer.toHexString(arrayOfByte3[i] & 0xFF));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 210 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private static synchronized void initLogger()
/*     */   {
/* 217 */     if (logger == null)
/* 218 */       logger = Logger.getLogger("javax.security.sasl");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.sasl.CramMD5Base
 * JD-Core Version:    0.6.2
 */