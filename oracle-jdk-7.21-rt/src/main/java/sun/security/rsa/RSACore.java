/*     */ package sun.security.rsa;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.interfaces.RSAKey;
/*     */ import java.security.interfaces.RSAPrivateCrtKey;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import sun.security.jca.JCAUtil;
/*     */ 
/*     */ public final class RSACore
/*     */ {
/*     */   private static final boolean ENABLE_BLINDING = true;
/*     */   private static final int BLINDING_MAX_REUSE = 50;
/* 221 */   private static final Map<BigInteger, BlindingParameters> blindingCache = new WeakHashMap();
/*     */ 
/*     */   public static int getByteLength(BigInteger paramBigInteger)
/*     */   {
/*  63 */     int i = paramBigInteger.bitLength();
/*  64 */     return i + 7 >> 3;
/*     */   }
/*     */ 
/*     */   public static int getByteLength(RSAKey paramRSAKey)
/*     */   {
/*  72 */     return getByteLength(paramRSAKey.getModulus());
/*     */   }
/*     */ 
/*     */   public static byte[] convert(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/*  77 */     if ((paramInt1 == 0) && (paramInt2 == paramArrayOfByte.length)) {
/*  78 */       return paramArrayOfByte;
/*     */     }
/*  80 */     byte[] arrayOfByte = new byte[paramInt2];
/*  81 */     System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
/*  82 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public static byte[] rsa(byte[] paramArrayOfByte, RSAPublicKey paramRSAPublicKey)
/*     */     throws BadPaddingException
/*     */   {
/*  91 */     return crypt(paramArrayOfByte, paramRSAPublicKey.getModulus(), paramRSAPublicKey.getPublicExponent());
/*     */   }
/*     */ 
/*     */   public static byte[] rsa(byte[] paramArrayOfByte, RSAPrivateKey paramRSAPrivateKey)
/*     */     throws BadPaddingException
/*     */   {
/* 100 */     if ((paramRSAPrivateKey instanceof RSAPrivateCrtKey)) {
/* 101 */       return crtCrypt(paramArrayOfByte, (RSAPrivateCrtKey)paramRSAPrivateKey);
/*     */     }
/* 103 */     return crypt(paramArrayOfByte, paramRSAPrivateKey.getModulus(), paramRSAPrivateKey.getPrivateExponent());
/*     */   }
/*     */ 
/*     */   private static byte[] crypt(byte[] paramArrayOfByte, BigInteger paramBigInteger1, BigInteger paramBigInteger2)
/*     */     throws BadPaddingException
/*     */   {
/* 112 */     BigInteger localBigInteger1 = parseMsg(paramArrayOfByte, paramBigInteger1);
/* 113 */     BigInteger localBigInteger2 = localBigInteger1.modPow(paramBigInteger2, paramBigInteger1);
/* 114 */     return toByteArray(localBigInteger2, getByteLength(paramBigInteger1));
/*     */   }
/*     */ 
/*     */   private static byte[] crtCrypt(byte[] paramArrayOfByte, RSAPrivateCrtKey paramRSAPrivateCrtKey)
/*     */     throws BadPaddingException
/*     */   {
/* 137 */     BigInteger localBigInteger1 = paramRSAPrivateCrtKey.getModulus();
/* 138 */     BigInteger localBigInteger2 = parseMsg(paramArrayOfByte, localBigInteger1);
/* 139 */     BigInteger localBigInteger3 = paramRSAPrivateCrtKey.getPrimeP();
/* 140 */     BigInteger localBigInteger4 = paramRSAPrivateCrtKey.getPrimeQ();
/* 141 */     BigInteger localBigInteger5 = paramRSAPrivateCrtKey.getPrimeExponentP();
/* 142 */     BigInteger localBigInteger6 = paramRSAPrivateCrtKey.getPrimeExponentQ();
/* 143 */     BigInteger localBigInteger7 = paramRSAPrivateCrtKey.getCrtCoefficient();
/*     */ 
/* 147 */     BlindingParameters localBlindingParameters = getBlindingParameters(paramRSAPrivateCrtKey);
/* 148 */     localBigInteger2 = localBigInteger2.multiply(localBlindingParameters.re).mod(localBigInteger1);
/*     */ 
/* 154 */     BigInteger localBigInteger8 = localBigInteger2.modPow(localBigInteger5, localBigInteger3);
/*     */ 
/* 156 */     BigInteger localBigInteger9 = localBigInteger2.modPow(localBigInteger6, localBigInteger4);
/*     */ 
/* 159 */     BigInteger localBigInteger10 = localBigInteger8.subtract(localBigInteger9);
/* 160 */     if (localBigInteger10.signum() < 0) {
/* 161 */       localBigInteger10 = localBigInteger10.add(localBigInteger3);
/*     */     }
/* 163 */     BigInteger localBigInteger11 = localBigInteger10.multiply(localBigInteger7).mod(localBigInteger3);
/*     */ 
/* 166 */     BigInteger localBigInteger12 = localBigInteger11.multiply(localBigInteger4).add(localBigInteger9);
/*     */ 
/* 168 */     if (localBlindingParameters != null) {
/* 169 */       localBigInteger12 = localBigInteger12.multiply(localBlindingParameters.rInv).mod(localBigInteger1);
/*     */     }
/*     */ 
/* 172 */     return toByteArray(localBigInteger12, getByteLength(localBigInteger1));
/*     */   }
/*     */ 
/*     */   private static BigInteger parseMsg(byte[] paramArrayOfByte, BigInteger paramBigInteger)
/*     */     throws BadPaddingException
/*     */   {
/* 180 */     BigInteger localBigInteger = new BigInteger(1, paramArrayOfByte);
/* 181 */     if (localBigInteger.compareTo(paramBigInteger) >= 0) {
/* 182 */       throw new BadPaddingException("Message is larger than modulus");
/*     */     }
/* 184 */     return localBigInteger;
/*     */   }
/*     */ 
/*     */   private static byte[] toByteArray(BigInteger paramBigInteger, int paramInt)
/*     */   {
/* 193 */     byte[] arrayOfByte1 = paramBigInteger.toByteArray();
/* 194 */     int i = arrayOfByte1.length;
/* 195 */     if (i == paramInt) {
/* 196 */       return arrayOfByte1;
/*     */     }
/*     */ 
/* 199 */     if ((i == paramInt + 1) && (arrayOfByte1[0] == 0)) {
/* 200 */       arrayOfByte2 = new byte[paramInt];
/* 201 */       System.arraycopy(arrayOfByte1, 1, arrayOfByte2, 0, paramInt);
/* 202 */       return arrayOfByte2;
/*     */     }
/*     */ 
/* 205 */     assert (i < paramInt);
/* 206 */     byte[] arrayOfByte2 = new byte[paramInt];
/* 207 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, paramInt - i, i);
/* 208 */     return arrayOfByte2;
/*     */   }
/*     */ 
/*     */   private static BlindingParameters getBlindingParameters(RSAPrivateCrtKey paramRSAPrivateCrtKey)
/*     */   {
/* 263 */     BigInteger localBigInteger1 = paramRSAPrivateCrtKey.getModulus();
/* 264 */     BigInteger localBigInteger2 = paramRSAPrivateCrtKey.getPublicExponent();
/*     */ 
/* 271 */     synchronized (blindingCache) {
/* 272 */       localBlindingParameters = (BlindingParameters)blindingCache.get(localBigInteger1);
/*     */     }
/* 274 */     if ((localBlindingParameters != null) && (localBlindingParameters.valid(localBigInteger2))) {
/* 275 */       return localBlindingParameters;
/*     */     }
/* 277 */     int i = localBigInteger1.bitLength();
/* 278 */     SecureRandom localSecureRandom = JCAUtil.getSecureRandom();
/* 279 */     BigInteger localBigInteger3 = new BigInteger(i, localSecureRandom).mod(localBigInteger1);
/* 280 */     BigInteger localBigInteger4 = localBigInteger3.modPow(localBigInteger2, localBigInteger1);
/* 281 */     BigInteger localBigInteger5 = localBigInteger3.modInverse(localBigInteger1);
/* 282 */     BlindingParameters localBlindingParameters = new BlindingParameters(localBigInteger2, localBigInteger4, localBigInteger5);
/* 283 */     synchronized (blindingCache) {
/* 284 */       blindingCache.put(localBigInteger1, localBlindingParameters);
/*     */     }
/* 286 */     return localBlindingParameters;
/*     */   }
/*     */ 
/*     */   private static final class BlindingParameters
/*     */   {
/*     */     final BigInteger e;
/*     */     final BigInteger re;
/*     */     final BigInteger rInv;
/*     */     private volatile int remainingUses;
/*     */ 
/*     */     BlindingParameters(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
/*     */     {
/* 244 */       this.e = paramBigInteger1;
/* 245 */       this.re = paramBigInteger2;
/* 246 */       this.rInv = paramBigInteger3;
/*     */ 
/* 248 */       this.remainingUses = 49;
/*     */     }
/*     */     boolean valid(BigInteger paramBigInteger) {
/* 251 */       int i = this.remainingUses--;
/* 252 */       return (i > 0) && (this.e.equals(paramBigInteger));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.rsa.RSACore
 * JD-Core Version:    0.6.2
 */