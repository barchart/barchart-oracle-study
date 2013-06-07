/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Date;
/*     */ import java.util.Random;
/*     */ import sun.security.pkcs.PKCS10;
/*     */ 
/*     */ public final class CertAndKeyGen
/*     */ {
/*     */   private SecureRandom prng;
/*     */   private String sigAlg;
/*     */   private KeyPairGenerator keyGen;
/*     */   private PublicKey publicKey;
/*     */   private PrivateKey privateKey;
/*     */ 
/*     */   public CertAndKeyGen(String paramString1, String paramString2)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  75 */     this.keyGen = KeyPairGenerator.getInstance(paramString1);
/*  76 */     this.sigAlg = paramString2;
/*     */   }
/*     */ 
/*     */   public CertAndKeyGen(String paramString1, String paramString2, String paramString3)
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException
/*     */   {
/*  93 */     if (paramString3 == null)
/*  94 */       this.keyGen = KeyPairGenerator.getInstance(paramString1);
/*     */     else {
/*     */       try {
/*  97 */         this.keyGen = KeyPairGenerator.getInstance(paramString1, paramString3);
/*     */       }
/*     */       catch (Exception localException) {
/* 100 */         this.keyGen = KeyPairGenerator.getInstance(paramString1);
/*     */       }
/*     */     }
/* 103 */     this.sigAlg = paramString2;
/*     */   }
/*     */ 
/*     */   public void setRandom(SecureRandom paramSecureRandom)
/*     */   {
/* 116 */     this.prng = paramSecureRandom;
/*     */   }
/*     */ 
/*     */   public void generate(int paramInt)
/*     */     throws InvalidKeyException
/*     */   {
/*     */     KeyPair localKeyPair;
/*     */     try
/*     */     {
/* 144 */       if (this.prng == null) {
/* 145 */         this.prng = new SecureRandom();
/*     */       }
/* 147 */       this.keyGen.initialize(paramInt, this.prng);
/* 148 */       localKeyPair = this.keyGen.generateKeyPair();
/*     */     }
/*     */     catch (Exception localException) {
/* 151 */       throw new IllegalArgumentException(localException.getMessage());
/*     */     }
/*     */ 
/* 154 */     this.publicKey = localKeyPair.getPublic();
/* 155 */     this.privateKey = localKeyPair.getPrivate();
/*     */   }
/*     */ 
/*     */   public X509Key getPublicKey()
/*     */   {
/* 171 */     if (!(this.publicKey instanceof X509Key)) {
/* 172 */       return null;
/*     */     }
/* 174 */     return (X509Key)this.publicKey;
/*     */   }
/*     */ 
/*     */   public PrivateKey getPrivateKey()
/*     */   {
/* 188 */     return this.privateKey;
/*     */   }
/*     */ 
/*     */   public X509Certificate getSelfCertificate(X500Name paramX500Name, Date paramDate, long paramLong)
/*     */     throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
/*     */   {
/*     */     try
/*     */     {
/* 220 */       Date localDate = new Date();
/* 221 */       localDate.setTime(paramDate.getTime() + paramLong * 1000L);
/*     */ 
/* 223 */       CertificateValidity localCertificateValidity = new CertificateValidity(paramDate, localDate);
/*     */ 
/* 226 */       X509CertInfo localX509CertInfo = new X509CertInfo();
/*     */ 
/* 228 */       localX509CertInfo.set("version", new CertificateVersion(2));
/*     */ 
/* 230 */       localX509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & 0x7FFFFFFF));
/*     */ 
/* 232 */       AlgorithmId localAlgorithmId = AlgorithmId.getAlgorithmId(this.sigAlg);
/* 233 */       localX509CertInfo.set("algorithmID", new CertificateAlgorithmId(localAlgorithmId));
/*     */ 
/* 235 */       localX509CertInfo.set("subject", new CertificateSubjectName(paramX500Name));
/* 236 */       localX509CertInfo.set("key", new CertificateX509Key(this.publicKey));
/* 237 */       localX509CertInfo.set("validity", localCertificateValidity);
/* 238 */       localX509CertInfo.set("issuer", new CertificateIssuerName(paramX500Name));
/*     */ 
/* 240 */       X509CertImpl localX509CertImpl = new X509CertImpl(localX509CertInfo);
/* 241 */       localX509CertImpl.sign(this.privateKey, this.sigAlg);
/*     */ 
/* 243 */       return localX509CertImpl;
/*     */     }
/*     */     catch (IOException localIOException) {
/* 246 */       throw new CertificateEncodingException("getSelfCert: " + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public X509Certificate getSelfCertificate(X500Name paramX500Name, long paramLong)
/*     */     throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
/*     */   {
/* 256 */     return getSelfCertificate(paramX500Name, new Date(), paramLong);
/*     */   }
/*     */ 
/*     */   public PKCS10 getCertRequest(X500Name paramX500Name)
/*     */     throws InvalidKeyException, SignatureException
/*     */   {
/* 276 */     PKCS10 localPKCS10 = new PKCS10(this.publicKey);
/*     */     try
/*     */     {
/* 279 */       Signature localSignature = Signature.getInstance(this.sigAlg);
/* 280 */       localSignature.initSign(this.privateKey);
/* 281 */       localPKCS10.encodeAndSign(paramX500Name, localSignature);
/*     */     }
/*     */     catch (CertificateException localCertificateException) {
/* 284 */       throw new SignatureException(this.sigAlg + " CertificateException");
/*     */     }
/*     */     catch (IOException localIOException) {
/* 287 */       throw new SignatureException(this.sigAlg + " IOException");
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */     {
/* 291 */       throw new SignatureException(this.sigAlg + " unavailable?");
/*     */     }
/* 293 */     return localPKCS10;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.x509.CertAndKeyGen
 * JD-Core Version:    0.6.2
 */