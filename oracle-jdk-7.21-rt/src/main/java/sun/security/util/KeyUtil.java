/*     */ package sun.security.util;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.interfaces.DSAKey;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.interfaces.ECKey;
/*     */ import java.security.interfaces.RSAKey;
/*     */ import java.security.spec.ECParameterSpec;
/*     */ import java.security.spec.KeySpec;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.interfaces.DHKey;
/*     */ import javax.crypto.interfaces.DHPublicKey;
/*     */ import javax.crypto.spec.DHParameterSpec;
/*     */ import javax.crypto.spec.DHPublicKeySpec;
/*     */ 
/*     */ public final class KeyUtil
/*     */ {
/*     */   public static final int getKeySize(Key paramKey)
/*     */   {
/*  56 */     int i = -1;
/*     */ 
/*  58 */     if ((paramKey instanceof Length)) {
/*     */       try {
/*  60 */         Length localLength = (Length)paramKey;
/*  61 */         i = localLength.length();
/*     */       }
/*     */       catch (UnsupportedOperationException localUnsupportedOperationException)
/*     */       {
/*     */       }
/*  66 */       if (i >= 0)
/*  67 */         return i;
/*     */     }
/*     */     Object localObject;
/*  72 */     if ((paramKey instanceof SecretKey)) {
/*  73 */       localObject = (SecretKey)paramKey;
/*  74 */       String str = ((SecretKey)localObject).getFormat();
/*  75 */       if (("RAW".equals(str)) && (((SecretKey)localObject).getEncoded() != null)) {
/*  76 */         i = ((SecretKey)localObject).getEncoded().length * 8;
/*     */       }
/*     */     }
/*  79 */     else if ((paramKey instanceof RSAKey)) {
/*  80 */       localObject = (RSAKey)paramKey;
/*  81 */       i = ((RSAKey)localObject).getModulus().bitLength();
/*  82 */     } else if ((paramKey instanceof ECKey)) {
/*  83 */       localObject = (ECKey)paramKey;
/*  84 */       i = ((ECKey)localObject).getParams().getOrder().bitLength();
/*  85 */     } else if ((paramKey instanceof DSAKey)) {
/*  86 */       localObject = (DSAKey)paramKey;
/*  87 */       i = ((DSAKey)localObject).getParams().getP().bitLength();
/*  88 */     } else if ((paramKey instanceof DHKey)) {
/*  89 */       localObject = (DHKey)paramKey;
/*  90 */       i = ((DHKey)localObject).getParams().getP().bitLength();
/*     */     }
/*     */ 
/*  94 */     return i;
/*     */   }
/*     */ 
/*     */   public static final void validate(Key paramKey)
/*     */     throws InvalidKeyException
/*     */   {
/* 110 */     if (paramKey == null) {
/* 111 */       throw new NullPointerException("The key to be validated cannot be null");
/*     */     }
/*     */ 
/* 115 */     if ((paramKey instanceof DHPublicKey))
/* 116 */       validateDHPublicKey((DHPublicKey)paramKey);
/*     */   }
/*     */ 
/*     */   public static final void validate(KeySpec paramKeySpec)
/*     */     throws InvalidKeyException
/*     */   {
/* 134 */     if (paramKeySpec == null) {
/* 135 */       throw new NullPointerException("The key spec to be validated cannot be null");
/*     */     }
/*     */ 
/* 139 */     if ((paramKeySpec instanceof DHPublicKeySpec))
/* 140 */       validateDHPublicKey((DHPublicKeySpec)paramKeySpec);
/*     */   }
/*     */ 
/*     */   public static final boolean isOracleJCEProvider(String paramString)
/*     */   {
/* 155 */     return (paramString != null) && ((paramString.equals("SunJCE")) || (paramString.startsWith("SunPKCS11")));
/*     */   }
/*     */ 
/*     */   private static void validateDHPublicKey(DHPublicKey paramDHPublicKey)
/*     */     throws InvalidKeyException
/*     */   {
/* 171 */     DHParameterSpec localDHParameterSpec = paramDHPublicKey.getParams();
/*     */ 
/* 173 */     BigInteger localBigInteger1 = localDHParameterSpec.getP();
/* 174 */     BigInteger localBigInteger2 = localDHParameterSpec.getG();
/* 175 */     BigInteger localBigInteger3 = paramDHPublicKey.getY();
/*     */ 
/* 177 */     validateDHPublicKey(localBigInteger1, localBigInteger2, localBigInteger3);
/*     */   }
/*     */ 
/*     */   private static void validateDHPublicKey(DHPublicKeySpec paramDHPublicKeySpec) throws InvalidKeyException
/*     */   {
/* 182 */     validateDHPublicKey(paramDHPublicKeySpec.getP(), paramDHPublicKeySpec.getG(), paramDHPublicKeySpec.getY());
/*     */   }
/*     */ 
/*     */   private static void validateDHPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
/*     */     throws InvalidKeyException
/*     */   {
/* 190 */     BigInteger localBigInteger1 = BigInteger.ONE;
/* 191 */     BigInteger localBigInteger2 = paramBigInteger1.subtract(BigInteger.ONE);
/* 192 */     if (paramBigInteger3.compareTo(localBigInteger1) <= 0) {
/* 193 */       throw new InvalidKeyException("Diffie-Hellman public key is too small");
/*     */     }
/*     */ 
/* 196 */     if (paramBigInteger3.compareTo(localBigInteger2) >= 0)
/* 197 */       throw new InvalidKeyException("Diffie-Hellman public key is too large");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.util.KeyUtil
 * JD-Core Version:    0.6.2
 */