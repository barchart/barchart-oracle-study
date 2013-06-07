/*     */ package sun.security.rsa;
/*     */ 
/*     */ import java.security.DigestException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.MGF1ParameterSpec;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import javax.crypto.spec.OAEPParameterSpec;
/*     */ import javax.crypto.spec.PSource;
/*     */ import javax.crypto.spec.PSource.PSpecified;
/*     */ import sun.security.jca.JCAUtil;
/*     */ 
/*     */ public final class RSAPadding
/*     */ {
/*     */   public static final int PAD_BLOCKTYPE_1 = 1;
/*     */   public static final int PAD_BLOCKTYPE_2 = 2;
/*     */   public static final int PAD_NONE = 3;
/*     */   public static final int PAD_OAEP_MGF1 = 4;
/*     */   private final int type;
/*     */   private final int paddedSize;
/*     */   private SecureRandom random;
/*     */   private final int maxDataSize;
/*     */   private MessageDigest md;
/*     */   private MessageDigest mgfMd;
/*     */   private byte[] lHash;
/* 189 */   private static final Map<String, byte[]> emptyHashes = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   public static RSAPadding getInstance(int paramInt1, int paramInt2)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 105 */     return new RSAPadding(paramInt1, paramInt2, null, null);
/*     */   }
/*     */ 
/*     */   public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 115 */     return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, null);
/*     */   }
/*     */ 
/*     */   public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 125 */     return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, paramOAEPParameterSpec);
/*     */   }
/*     */ 
/*     */   private RSAPadding(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 132 */     this.type = paramInt1;
/* 133 */     this.paddedSize = paramInt2;
/* 134 */     this.random = paramSecureRandom;
/* 135 */     if (paramInt2 < 64)
/*     */     {
/* 137 */       throw new InvalidKeyException("Padded size must be at least 64");
/*     */     }
/* 139 */     switch (paramInt1) {
/*     */     case 1:
/*     */     case 2:
/* 142 */       this.maxDataSize = (paramInt2 - 11);
/* 143 */       break;
/*     */     case 3:
/* 145 */       this.maxDataSize = paramInt2;
/* 146 */       break;
/*     */     case 4:
/* 148 */       String str1 = "SHA-1";
/* 149 */       String str2 = "SHA-1";
/* 150 */       byte[] arrayOfByte = null;
/*     */       try {
/* 152 */         if (paramOAEPParameterSpec != null) {
/* 153 */           str1 = paramOAEPParameterSpec.getDigestAlgorithm();
/* 154 */           String str3 = paramOAEPParameterSpec.getMGFAlgorithm();
/* 155 */           if (!str3.equalsIgnoreCase("MGF1")) {
/* 156 */             throw new InvalidAlgorithmParameterException("Unsupported MGF algo: " + str3);
/*     */           }
/*     */ 
/* 159 */           str2 = ((MGF1ParameterSpec)paramOAEPParameterSpec.getMGFParameters()).getDigestAlgorithm();
/* 160 */           PSource localPSource = paramOAEPParameterSpec.getPSource();
/* 161 */           String str4 = localPSource.getAlgorithm();
/* 162 */           if (!str4.equalsIgnoreCase("PSpecified")) {
/* 163 */             throw new InvalidAlgorithmParameterException("Unsupported pSource algo: " + str4);
/*     */           }
/*     */ 
/* 166 */           arrayOfByte = ((PSource.PSpecified)localPSource).getValue();
/*     */         }
/* 168 */         this.md = MessageDigest.getInstance(str1);
/* 169 */         this.mgfMd = MessageDigest.getInstance(str2);
/*     */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 171 */         throw new InvalidKeyException("Digest " + str1 + " not available", localNoSuchAlgorithmException);
/*     */       }
/*     */ 
/* 174 */       this.lHash = getInitialHash(this.md, arrayOfByte);
/* 175 */       int i = this.lHash.length;
/* 176 */       this.maxDataSize = (paramInt2 - 2 - 2 * i);
/* 177 */       if (this.maxDataSize <= 0) {
/* 178 */         throw new InvalidKeyException("Key is too short for encryption using OAEPPadding with " + str1 + " and MGF1" + str2);
/*     */       }
/*     */ 
/*     */       break;
/*     */     default:
/* 184 */       throw new InvalidKeyException("Invalid padding: " + paramInt1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static byte[] getInitialHash(MessageDigest paramMessageDigest, byte[] paramArrayOfByte)
/*     */   {
/* 201 */     byte[] arrayOfByte = null;
/* 202 */     if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
/* 203 */       String str = paramMessageDigest.getAlgorithm();
/* 204 */       arrayOfByte = (byte[])emptyHashes.get(str);
/* 205 */       if (arrayOfByte == null) {
/* 206 */         arrayOfByte = paramMessageDigest.digest();
/* 207 */         emptyHashes.put(str, arrayOfByte);
/*     */       }
/*     */     } else {
/* 210 */       arrayOfByte = paramMessageDigest.digest(paramArrayOfByte);
/*     */     }
/* 212 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public int getMaxDataSize()
/*     */   {
/* 220 */     return this.maxDataSize;
/*     */   }
/*     */ 
/*     */   public byte[] pad(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws BadPaddingException
/*     */   {
/* 228 */     return pad(RSACore.convert(paramArrayOfByte, paramInt1, paramInt2));
/*     */   }
/*     */ 
/*     */   public byte[] pad(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 235 */     if (paramArrayOfByte.length > this.maxDataSize) {
/* 236 */       throw new BadPaddingException("Data must be shorter than " + (this.maxDataSize + 1) + " bytes");
/*     */     }
/*     */ 
/* 239 */     switch (this.type) {
/*     */     case 3:
/* 241 */       return paramArrayOfByte;
/*     */     case 1:
/*     */     case 2:
/* 244 */       return padV15(paramArrayOfByte);
/*     */     case 4:
/* 246 */       return padOAEP(paramArrayOfByte);
/*     */     }
/* 248 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public byte[] unpad(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws BadPaddingException
/*     */   {
/* 257 */     return unpad(RSACore.convert(paramArrayOfByte, paramInt1, paramInt2));
/*     */   }
/*     */ 
/*     */   public byte[] unpad(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 264 */     if (paramArrayOfByte.length != this.paddedSize) {
/* 265 */       throw new BadPaddingException("Padded length must be " + this.paddedSize);
/*     */     }
/* 267 */     switch (this.type) {
/*     */     case 3:
/* 269 */       return paramArrayOfByte;
/*     */     case 1:
/*     */     case 2:
/* 272 */       return unpadV15(paramArrayOfByte);
/*     */     case 4:
/* 274 */       return unpadOAEP(paramArrayOfByte);
/*     */     }
/* 276 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   private byte[] padV15(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 284 */     byte[] arrayOfByte1 = new byte[this.paddedSize];
/* 285 */     System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, this.paddedSize - paramArrayOfByte.length, paramArrayOfByte.length);
/* 286 */     int i = this.paddedSize - 3 - paramArrayOfByte.length;
/* 287 */     int j = 0;
/* 288 */     arrayOfByte1[(j++)] = 0;
/* 289 */     arrayOfByte1[(j++)] = ((byte)this.type);
/* 290 */     if (this.type == 1)
/*     */     {
/* 292 */       while (i-- > 0) {
/* 293 */         arrayOfByte1[(j++)] = -1;
/*     */       }
/*     */     }
/*     */ 
/* 297 */     if (this.random == null) {
/* 298 */       this.random = JCAUtil.getSecureRandom();
/*     */     }
/*     */ 
/* 302 */     byte[] arrayOfByte2 = new byte[64];
/* 303 */     int k = -1;
/* 304 */     while (i-- > 0) {
/*     */       int m;
/*     */       do {
/* 307 */         if (k < 0) {
/* 308 */           this.random.nextBytes(arrayOfByte2);
/* 309 */           k = arrayOfByte2.length - 1;
/*     */         }
/* 311 */         m = arrayOfByte2[(k--)] & 0xFF;
/* 312 */       }while (m == 0);
/* 313 */       arrayOfByte1[(j++)] = ((byte)m);
/*     */     }
/*     */ 
/* 316 */     return arrayOfByte1;
/*     */   }
/*     */ 
/*     */   private byte[] unpadV15(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 323 */     int i = 0;
/* 324 */     if (paramArrayOfByte[(i++)] != 0) {
/* 325 */       throw new BadPaddingException("Data must start with zero");
/*     */     }
/* 327 */     if (paramArrayOfByte[(i++)] != this.type)
/* 328 */       throw new BadPaddingException("Blocktype mismatch: " + paramArrayOfByte[1]);
/*     */     while (true)
/*     */     {
/* 331 */       j = paramArrayOfByte[(i++)] & 0xFF;
/* 332 */       if (j == 0) {
/*     */         break;
/*     */       }
/* 335 */       if (i == paramArrayOfByte.length) {
/* 336 */         throw new BadPaddingException("Padding string not terminated");
/*     */       }
/* 338 */       if ((this.type == 1) && (j != 255)) {
/* 339 */         throw new BadPaddingException("Padding byte not 0xff: " + j);
/*     */       }
/*     */     }
/* 342 */     int j = paramArrayOfByte.length - i;
/* 343 */     if (j > this.maxDataSize) {
/* 344 */       throw new BadPaddingException("Padding string too short");
/*     */     }
/* 346 */     byte[] arrayOfByte = new byte[j];
/* 347 */     System.arraycopy(paramArrayOfByte, paramArrayOfByte.length - j, arrayOfByte, 0, j);
/* 348 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private byte[] padOAEP(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 356 */     if (this.random == null) {
/* 357 */       this.random = JCAUtil.getSecureRandom();
/*     */     }
/* 359 */     int i = this.lHash.length;
/*     */ 
/* 363 */     byte[] arrayOfByte1 = new byte[i];
/* 364 */     this.random.nextBytes(arrayOfByte1);
/*     */ 
/* 367 */     byte[] arrayOfByte2 = new byte[this.paddedSize];
/*     */ 
/* 370 */     int j = 1;
/* 371 */     int k = i;
/*     */ 
/* 374 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, j, k);
/*     */ 
/* 378 */     int m = i + 1;
/* 379 */     int n = arrayOfByte2.length - m;
/*     */ 
/* 382 */     int i1 = this.paddedSize - paramArrayOfByte.length;
/*     */ 
/* 389 */     System.arraycopy(this.lHash, 0, arrayOfByte2, m, i);
/* 390 */     arrayOfByte2[(i1 - 1)] = 1;
/* 391 */     System.arraycopy(paramArrayOfByte, 0, arrayOfByte2, i1, paramArrayOfByte.length);
/*     */ 
/* 394 */     mgf1(arrayOfByte2, j, k, arrayOfByte2, m, n);
/*     */ 
/* 397 */     mgf1(arrayOfByte2, m, n, arrayOfByte2, j, k);
/*     */ 
/* 399 */     return arrayOfByte2;
/*     */   }
/*     */ 
/*     */   private byte[] unpadOAEP(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 406 */     byte[] arrayOfByte1 = paramArrayOfByte;
/* 407 */     int i = this.lHash.length;
/*     */ 
/* 409 */     if (arrayOfByte1[0] != 0) {
/* 410 */       throw new BadPaddingException("Data must start with zero");
/*     */     }
/*     */ 
/* 413 */     int j = 1;
/* 414 */     int k = i;
/*     */ 
/* 416 */     int m = i + 1;
/* 417 */     int n = arrayOfByte1.length - m;
/*     */ 
/* 419 */     mgf1(arrayOfByte1, m, n, arrayOfByte1, j, k);
/* 420 */     mgf1(arrayOfByte1, j, k, arrayOfByte1, m, n);
/*     */ 
/* 423 */     for (int i1 = 0; i1 < i; i1++) {
/* 424 */       if (this.lHash[i1] != arrayOfByte1[(m + i1)]) {
/* 425 */         throw new BadPaddingException("lHash mismatch");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 430 */     i1 = m + i;
/* 431 */     while (arrayOfByte1[i1] == 0) {
/* 432 */       i1++;
/* 433 */       if (i1 >= arrayOfByte1.length) {
/* 434 */         throw new BadPaddingException("Padding string not terminated");
/*     */       }
/*     */     }
/*     */ 
/* 438 */     if (arrayOfByte1[(i1++)] != 1) {
/* 439 */       throw new BadPaddingException("Padding string not terminated by 0x01 byte");
/*     */     }
/*     */ 
/* 443 */     int i2 = arrayOfByte1.length - i1;
/* 444 */     byte[] arrayOfByte2 = new byte[i2];
/* 445 */     System.arraycopy(arrayOfByte1, i1, arrayOfByte2, 0, i2);
/*     */ 
/* 447 */     return arrayOfByte2;
/*     */   }
/*     */ 
/*     */   private void mgf1(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
/*     */     throws BadPaddingException
/*     */   {
/* 460 */     byte[] arrayOfByte1 = new byte[4];
/* 461 */     byte[] arrayOfByte2 = new byte[20];
/* 462 */     while (paramInt4 > 0) {
/* 463 */       this.mgfMd.update(paramArrayOfByte1, paramInt1, paramInt2);
/* 464 */       this.mgfMd.update(arrayOfByte1);
/*     */       try {
/* 466 */         this.mgfMd.digest(arrayOfByte2, 0, arrayOfByte2.length);
/*     */       }
/*     */       catch (DigestException localDigestException) {
/* 469 */         throw new BadPaddingException(localDigestException.toString());
/*     */       }
/* 471 */       for (int i = 0; (i < arrayOfByte2.length) && (paramInt4 > 0); paramInt4--)
/*     */       {
/*     */         int tmp90_87 = (paramInt3++);
/*     */         byte[] tmp90_83 = paramArrayOfByte2; tmp90_83[tmp90_87] = ((byte)(tmp90_83[tmp90_87] ^ arrayOfByte2[(i++)]));
/*     */       }
/* 474 */       if (paramInt4 > 0)
/*     */       {
/* 476 */         for (i = arrayOfByte1.length - 1; ; i--)
/*     */         {
/*     */           int tmp125_123 = i;
/*     */           byte[] tmp125_121 = arrayOfByte1; if (((tmp125_121[tmp125_123] = (byte)(tmp125_121[tmp125_123] + 1)) != 0) || (i <= 0))
/*     */             break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.rsa.RSAPadding
 * JD-Core Version:    0.6.2
 */