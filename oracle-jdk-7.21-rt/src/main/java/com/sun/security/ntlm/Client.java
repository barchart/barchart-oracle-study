/*     */ package com.sun.security.ntlm;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public final class Client extends NTLM
/*     */ {
/*     */   private final String hostname;
/*     */   private final String username;
/*     */   private String domain;
/*     */   private byte[] pw1;
/*     */   private byte[] pw2;
/*     */ 
/*     */   public Client(String paramString1, String paramString2, String paramString3, String paramString4, char[] paramArrayOfChar)
/*     */     throws NTLMException
/*     */   {
/*  78 */     super(paramString1);
/*  79 */     if ((paramString3 == null) || (paramArrayOfChar == null)) {
/*  80 */       throw new NTLMException(6, "username/password cannot be null");
/*     */     }
/*     */ 
/*  83 */     this.hostname = paramString2;
/*  84 */     this.username = paramString3;
/*  85 */     this.domain = paramString4;
/*  86 */     this.pw1 = getP1(paramArrayOfChar);
/*  87 */     this.pw2 = getP2(paramArrayOfChar);
/*  88 */     debug("NTLM Client: (h,u,t,version(v)) = (%s,%s,%s,%s(%s))\n", new Object[] { paramString2, paramString3, paramString4, paramString1, this.v.toString() });
/*     */   }
/*     */ 
/*     */   public byte[] type1()
/*     */   {
/*  97 */     NTLM.Writer localWriter = new NTLM.Writer(1, 32);
/*  98 */     int i = 33283;
/*  99 */     if (this.hostname != null) {
/* 100 */       i |= 8192;
/*     */     }
/* 102 */     if (this.domain != null) {
/* 103 */       i |= 4096;
/*     */     }
/* 105 */     if (this.v != Version.NTLM) {
/* 106 */       i |= 524288;
/*     */     }
/* 108 */     localWriter.writeInt(12, i);
/* 109 */     localWriter.writeSecurityBuffer(24, this.hostname, false);
/* 110 */     localWriter.writeSecurityBuffer(16, this.domain, false);
/* 111 */     debug("NTLM Client: Type 1 created\n", new Object[0]);
/* 112 */     debug(localWriter.getBytes());
/* 113 */     return localWriter.getBytes();
/*     */   }
/*     */ 
/*     */   public byte[] type3(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */     throws NTLMException
/*     */   {
/* 126 */     if ((paramArrayOfByte1 == null) || ((this.v != Version.NTLM) && (paramArrayOfByte2 == null))) {
/* 127 */       throw new NTLMException(6, "type2 and nonce cannot be null");
/*     */     }
/*     */ 
/* 130 */     debug("NTLM Client: Type 2 received\n", new Object[0]);
/* 131 */     debug(paramArrayOfByte1);
/* 132 */     NTLM.Reader localReader = new NTLM.Reader(paramArrayOfByte1);
/* 133 */     byte[] arrayOfByte1 = localReader.readBytes(24, 8);
/* 134 */     int i = localReader.readInt(20);
/* 135 */     boolean bool = (i & 0x1) == 1;
/* 136 */     String str = localReader.readSecurityBuffer(12, bool);
/* 137 */     if (str != null) {
/* 138 */       this.domain = str;
/*     */     }
/* 140 */     if (this.domain == null) {
/* 141 */       throw new NTLMException(2, "No domain info");
/*     */     }
/*     */ 
/* 145 */     int j = 0x88200 | i & 0x3;
/* 146 */     NTLM.Writer localWriter = new NTLM.Writer(3, 64);
/* 147 */     byte[] arrayOfByte2 = null; byte[] arrayOfByte3 = null;
/*     */ 
/* 149 */     localWriter.writeSecurityBuffer(28, this.domain, bool);
/* 150 */     localWriter.writeSecurityBuffer(36, this.username, bool);
/* 151 */     localWriter.writeSecurityBuffer(44, this.hostname, bool);
/*     */     byte[] arrayOfByte4;
/*     */     byte[] arrayOfByte5;
/* 153 */     if (this.v == Version.NTLM) {
/* 154 */       arrayOfByte4 = calcLMHash(this.pw1);
/* 155 */       arrayOfByte5 = calcNTHash(this.pw2);
/* 156 */       if (this.writeLM) arrayOfByte2 = calcResponse(arrayOfByte4, arrayOfByte1);
/* 157 */       if (this.writeNTLM) arrayOfByte3 = calcResponse(arrayOfByte5, arrayOfByte1); 
/*     */     }
/* 158 */     else if (this.v == Version.NTLM2) {
/* 159 */       arrayOfByte4 = calcNTHash(this.pw2);
/* 160 */       arrayOfByte2 = ntlm2LM(paramArrayOfByte2);
/* 161 */       arrayOfByte3 = ntlm2NTLM(arrayOfByte4, paramArrayOfByte2, arrayOfByte1);
/*     */     } else {
/* 163 */       arrayOfByte4 = calcNTHash(this.pw2);
/* 164 */       if (this.writeLM) arrayOfByte2 = calcV2(arrayOfByte4, this.username.toUpperCase(Locale.US) + this.domain, paramArrayOfByte2, arrayOfByte1);
/*     */ 
/* 166 */       if (this.writeNTLM) {
/* 167 */         arrayOfByte5 = paramArrayOfByte1.length > 48 ? localReader.readSecurityBuffer(40) : new byte[0];
/*     */ 
/* 169 */         byte[] arrayOfByte6 = new byte[32 + arrayOfByte5.length];
/* 170 */         System.arraycopy(new byte[] { 1, 1, 0, 0, 0, 0, 0, 0 }, 0, arrayOfByte6, 0, 8);
/*     */ 
/* 172 */         byte[] arrayOfByte7 = BigInteger.valueOf(new Date().getTime()).add(new BigInteger("11644473600000")).multiply(BigInteger.valueOf(10000L)).toByteArray();
/*     */ 
/* 176 */         for (int k = 0; k < arrayOfByte7.length; k++) {
/* 177 */           arrayOfByte6[(8 + arrayOfByte7.length - k - 1)] = arrayOfByte7[k];
/*     */         }
/* 179 */         System.arraycopy(paramArrayOfByte2, 0, arrayOfByte6, 16, 8);
/* 180 */         System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, arrayOfByte6, 24, 4);
/* 181 */         System.arraycopy(arrayOfByte5, 0, arrayOfByte6, 28, arrayOfByte5.length);
/* 182 */         System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, arrayOfByte6, 28 + arrayOfByte5.length, 4);
/*     */ 
/* 184 */         arrayOfByte3 = calcV2(arrayOfByte4, this.username.toUpperCase(Locale.US) + this.domain, arrayOfByte6, arrayOfByte1);
/*     */       }
/*     */     }
/*     */ 
/* 188 */     localWriter.writeSecurityBuffer(12, arrayOfByte2);
/* 189 */     localWriter.writeSecurityBuffer(20, arrayOfByte3);
/* 190 */     localWriter.writeSecurityBuffer(52, new byte[0]);
/*     */ 
/* 192 */     localWriter.writeInt(60, j);
/* 193 */     debug("NTLM Client: Type 3 created\n", new Object[0]);
/* 194 */     debug(localWriter.getBytes());
/* 195 */     return localWriter.getBytes();
/*     */   }
/*     */ 
/*     */   public String getDomain()
/*     */   {
/* 204 */     return this.domain;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 211 */     Arrays.fill(this.pw1, (byte)0);
/* 212 */     Arrays.fill(this.pw2, (byte)0);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.ntlm.Client
 * JD-Core Version:    0.6.2
 */