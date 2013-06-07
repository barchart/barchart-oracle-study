/*      */ package java.security;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.security.spec.AlgorithmParameterSpec;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import javax.crypto.BadPaddingException;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.IllegalBlockSizeException;
/*      */ import javax.crypto.NoSuchPaddingException;
/*      */ import sun.security.jca.GetInstance;
/*      */ import sun.security.jca.GetInstance.Instance;
/*      */ import sun.security.jca.ServiceId;
/*      */ import sun.security.util.Debug;
/*      */ 
/*      */ public abstract class Signature extends SignatureSpi
/*      */ {
/*  121 */   private static final Debug debug = Debug.getInstance("jca", "Signature");
/*      */   private String algorithm;
/*      */   Provider provider;
/*      */   protected static final int UNINITIALIZED = 0;
/*      */   protected static final int SIGN = 2;
/*      */   protected static final int VERIFY = 3;
/*  155 */   protected int state = 0;
/*      */   private static final String RSA_SIGNATURE = "NONEwithRSA";
/*      */   private static final String RSA_CIPHER = "RSA/ECB/PKCS1Padding";
/*  177 */   private static final List<ServiceId> rsaIds = Arrays.asList(new ServiceId[] { new ServiceId("Signature", "NONEwithRSA"), new ServiceId("Cipher", "RSA/ECB/PKCS1Padding"), new ServiceId("Cipher", "RSA/ECB"), new ServiceId("Cipher", "RSA//PKCS1Padding"), new ServiceId("Cipher", "RSA") });
/*      */ 
/*  262 */   private static final Map<String, Boolean> signatureInfo = new ConcurrentHashMap();
/*      */ 
/*      */   protected Signature(String paramString)
/*      */   {
/*  167 */     this.algorithm = paramString;
/*      */   }
/*      */ 
/*      */   public static Signature getInstance(String paramString)
/*      */     throws NoSuchAlgorithmException
/*      */   {
/*      */     List localList;
/*  217 */     if (paramString.equalsIgnoreCase("NONEwithRSA"))
/*  218 */       localList = GetInstance.getServices(rsaIds);
/*      */     else {
/*  220 */       localList = GetInstance.getServices("Signature", paramString);
/*      */     }
/*  222 */     Iterator localIterator = localList.iterator();
/*  223 */     if (!localIterator.hasNext()) {
/*  224 */       throw new NoSuchAlgorithmException(paramString + " Signature not available");
/*      */     }
/*      */ 
/*      */     NoSuchAlgorithmException localNoSuchAlgorithmException1;
/*      */     do
/*      */     {
/*  230 */       Provider.Service localService = (Provider.Service)localIterator.next();
/*  231 */       if (isSpi(localService)) {
/*  232 */         return new Delegate(localService, localIterator, paramString);
/*      */       }
/*      */       try
/*      */       {
/*  236 */         GetInstance.Instance localInstance = GetInstance.getInstance(localService, SignatureSpi.class);
/*      */ 
/*  238 */         return getInstance(localInstance, paramString);
/*      */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException2) {
/*  240 */         localNoSuchAlgorithmException1 = localNoSuchAlgorithmException2;
/*      */       }
/*      */     }
/*  243 */     while (localIterator.hasNext());
/*  244 */     throw localNoSuchAlgorithmException1;
/*      */   }
/*      */ 
/*      */   private static Signature getInstance(GetInstance.Instance paramInstance, String paramString)
/*      */   {
/*      */     Object localObject;
/*  249 */     if ((paramInstance.impl instanceof Signature)) {
/*  250 */       localObject = (Signature)paramInstance.impl;
/*      */     } else {
/*  252 */       SignatureSpi localSignatureSpi = (SignatureSpi)paramInstance.impl;
/*  253 */       localObject = new Delegate(localSignatureSpi, paramString);
/*      */     }
/*  255 */     ((Signature)localObject).provider = paramInstance.provider;
/*  256 */     return localObject;
/*      */   }
/*      */ 
/*      */   private static boolean isSpi(Provider.Service paramService)
/*      */   {
/*  278 */     if (paramService.getType().equals("Cipher"))
/*      */     {
/*  280 */       return true;
/*      */     }
/*  282 */     String str = paramService.getClassName();
/*  283 */     Boolean localBoolean = (Boolean)signatureInfo.get(str);
/*  284 */     if (localBoolean == null) {
/*      */       try {
/*  286 */         Object localObject = paramService.newInstance(null);
/*      */ 
/*  290 */         boolean bool = ((localObject instanceof SignatureSpi)) && (!(localObject instanceof Signature));
/*      */ 
/*  292 */         if ((debug != null) && (!bool)) {
/*  293 */           debug.println("Not a SignatureSpi " + str);
/*  294 */           debug.println("Delayed provider selection may not be available for algorithm " + paramService.getAlgorithm());
/*      */         }
/*      */ 
/*  297 */         localBoolean = Boolean.valueOf(bool);
/*  298 */         signatureInfo.put(str, localBoolean);
/*      */       }
/*      */       catch (Exception localException) {
/*  301 */         return false;
/*      */       }
/*      */     }
/*  304 */     return localBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   public static Signature getInstance(String paramString1, String paramString2)
/*      */     throws NoSuchAlgorithmException, NoSuchProviderException
/*      */   {
/*  343 */     if (paramString1.equalsIgnoreCase("NONEwithRSA"))
/*      */     {
/*  345 */       if ((paramString2 == null) || (paramString2.length() == 0)) {
/*  346 */         throw new IllegalArgumentException("missing provider");
/*      */       }
/*  348 */       localObject = Security.getProvider(paramString2);
/*  349 */       if (localObject == null) {
/*  350 */         throw new NoSuchProviderException("no such provider: " + paramString2);
/*      */       }
/*      */ 
/*  353 */       return getInstanceRSA((Provider)localObject);
/*      */     }
/*  355 */     Object localObject = GetInstance.getInstance("Signature", SignatureSpi.class, paramString1, paramString2);
/*      */ 
/*  357 */     return getInstance((GetInstance.Instance)localObject, paramString1);
/*      */   }
/*      */ 
/*      */   public static Signature getInstance(String paramString, Provider paramProvider)
/*      */     throws NoSuchAlgorithmException
/*      */   {
/*  391 */     if (paramString.equalsIgnoreCase("NONEwithRSA"))
/*      */     {
/*  393 */       if (paramProvider == null) {
/*  394 */         throw new IllegalArgumentException("missing provider");
/*      */       }
/*  396 */       return getInstanceRSA(paramProvider);
/*      */     }
/*  398 */     GetInstance.Instance localInstance = GetInstance.getInstance("Signature", SignatureSpi.class, paramString, paramProvider);
/*      */ 
/*  400 */     return getInstance(localInstance, paramString);
/*      */   }
/*      */ 
/*      */   private static Signature getInstanceRSA(Provider paramProvider)
/*      */     throws NoSuchAlgorithmException
/*      */   {
/*  408 */     Provider.Service localService = paramProvider.getService("Signature", "NONEwithRSA");
/*      */     Object localObject;
/*  409 */     if (localService != null) {
/*  410 */       localObject = GetInstance.getInstance(localService, SignatureSpi.class);
/*  411 */       return getInstance((GetInstance.Instance)localObject, "NONEwithRSA");
/*      */     }
/*      */     try
/*      */     {
/*  415 */       localObject = Cipher.getInstance("RSA/ECB/PKCS1Padding", paramProvider);
/*  416 */       return new Delegate(new CipherAdapter((Cipher)localObject), "NONEwithRSA");
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException)
/*      */     {
/*  420 */       throw new NoSuchAlgorithmException("no such algorithm: NONEwithRSA for provider " + paramProvider.getName(), localGeneralSecurityException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final Provider getProvider()
/*      */   {
/*  431 */     chooseFirstProvider();
/*  432 */     return this.provider;
/*      */   }
/*      */ 
/*      */   void chooseFirstProvider()
/*      */   {
/*      */   }
/*      */ 
/*      */   public final void initVerify(PublicKey paramPublicKey)
/*      */     throws InvalidKeyException
/*      */   {
/*  451 */     engineInitVerify(paramPublicKey);
/*  452 */     this.state = 3;
/*      */   }
/*      */ 
/*      */   public final void initVerify(Certificate paramCertificate)
/*      */     throws InvalidKeyException
/*      */   {
/*  478 */     if ((paramCertificate instanceof X509Certificate))
/*      */     {
/*  482 */       localObject = (X509Certificate)paramCertificate;
/*  483 */       Set localSet = ((X509Certificate)localObject).getCriticalExtensionOIDs();
/*      */ 
/*  485 */       if ((localSet != null) && (!localSet.isEmpty()) && (localSet.contains("2.5.29.15")))
/*      */       {
/*  487 */         boolean[] arrayOfBoolean = ((X509Certificate)localObject).getKeyUsage();
/*      */ 
/*  489 */         if ((arrayOfBoolean != null) && (arrayOfBoolean[0] == 0)) {
/*  490 */           throw new InvalidKeyException("Wrong key usage");
/*      */         }
/*      */       }
/*      */     }
/*  494 */     Object localObject = paramCertificate.getPublicKey();
/*  495 */     engineInitVerify((PublicKey)localObject);
/*  496 */     this.state = 3;
/*      */   }
/*      */ 
/*      */   public final void initSign(PrivateKey paramPrivateKey)
/*      */     throws InvalidKeyException
/*      */   {
/*  511 */     engineInitSign(paramPrivateKey);
/*  512 */     this.state = 2;
/*      */   }
/*      */ 
/*      */   public final void initSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
/*      */     throws InvalidKeyException
/*      */   {
/*  529 */     engineInitSign(paramPrivateKey, paramSecureRandom);
/*  530 */     this.state = 2;
/*      */   }
/*      */ 
/*      */   public final byte[] sign()
/*      */     throws SignatureException
/*      */   {
/*  552 */     if (this.state == 2) {
/*  553 */       return engineSign();
/*      */     }
/*  555 */     throw new SignatureException("object not initialized for signing");
/*      */   }
/*      */ 
/*      */   public final int sign(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws SignatureException
/*      */   {
/*  589 */     if (paramArrayOfByte == null) {
/*  590 */       throw new IllegalArgumentException("No output buffer given");
/*      */     }
/*  592 */     if (paramArrayOfByte.length - paramInt1 < paramInt2) {
/*  593 */       throw new IllegalArgumentException("Output buffer too small for specified offset and length");
/*      */     }
/*      */ 
/*  596 */     if (this.state != 2) {
/*  597 */       throw new SignatureException("object not initialized for signing");
/*      */     }
/*      */ 
/*  600 */     return engineSign(paramArrayOfByte, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public final boolean verify(byte[] paramArrayOfByte)
/*      */     throws SignatureException
/*      */   {
/*  622 */     if (this.state == 3) {
/*  623 */       return engineVerify(paramArrayOfByte);
/*      */     }
/*  625 */     throw new SignatureException("object not initialized for verification");
/*      */   }
/*      */ 
/*      */   public final boolean verify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws SignatureException
/*      */   {
/*  659 */     if (this.state == 3) {
/*  660 */       if ((paramArrayOfByte == null) || (paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length))
/*      */       {
/*  662 */         throw new IllegalArgumentException("Bad arguments");
/*      */       }
/*      */ 
/*  665 */       return engineVerify(paramArrayOfByte, paramInt1, paramInt2);
/*      */     }
/*  667 */     throw new SignatureException("object not initialized for verification");
/*      */   }
/*      */ 
/*      */   public final void update(byte paramByte)
/*      */     throws SignatureException
/*      */   {
/*  680 */     if ((this.state == 3) || (this.state == 2))
/*  681 */       engineUpdate(paramByte);
/*      */     else
/*  683 */       throw new SignatureException("object not initialized for signature or verification");
/*      */   }
/*      */ 
/*      */   public final void update(byte[] paramArrayOfByte)
/*      */     throws SignatureException
/*      */   {
/*  698 */     update(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */   }
/*      */ 
/*      */   public final void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws SignatureException
/*      */   {
/*  714 */     if ((this.state == 2) || (this.state == 3))
/*  715 */       engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
/*      */     else
/*  717 */       throw new SignatureException("object not initialized for signature or verification");
/*      */   }
/*      */ 
/*      */   public final void update(ByteBuffer paramByteBuffer)
/*      */     throws SignatureException
/*      */   {
/*  736 */     if ((this.state != 2) && (this.state != 3)) {
/*  737 */       throw new SignatureException("object not initialized for signature or verification");
/*      */     }
/*      */ 
/*  740 */     if (paramByteBuffer == null) {
/*  741 */       throw new NullPointerException();
/*      */     }
/*  743 */     engineUpdate(paramByteBuffer);
/*      */   }
/*      */ 
/*      */   public final String getAlgorithm()
/*      */   {
/*  752 */     return this.algorithm;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  763 */     String str = "";
/*  764 */     switch (this.state) {
/*      */     case 0:
/*  766 */       str = "<not initialized>";
/*  767 */       break;
/*      */     case 3:
/*  769 */       str = "<initialized for verifying>";
/*  770 */       break;
/*      */     case 2:
/*  772 */       str = "<initialized for signing>";
/*      */     case 1:
/*      */     }
/*  775 */     return "Signature object: " + getAlgorithm() + str;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public final void setParameter(String paramString, Object paramObject)
/*      */     throws InvalidParameterException
/*      */   {
/*  806 */     engineSetParameter(paramString, paramObject);
/*      */   }
/*      */ 
/*      */   public final void setParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
/*      */     throws InvalidAlgorithmParameterException
/*      */   {
/*  821 */     engineSetParameter(paramAlgorithmParameterSpec);
/*      */   }
/*      */ 
/*      */   public final AlgorithmParameters getParameters()
/*      */   {
/*  840 */     return engineGetParameters();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public final Object getParameter(String paramString)
/*      */     throws InvalidParameterException
/*      */   {
/*  869 */     return engineGetParameter(paramString);
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */     throws CloneNotSupportedException
/*      */   {
/*  881 */     if ((this instanceof Cloneable)) {
/*  882 */       return super.clone();
/*      */     }
/*  884 */     throw new CloneNotSupportedException();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  263 */     Boolean localBoolean = Boolean.TRUE;
/*      */ 
/*  265 */     signatureInfo.put("sun.security.provider.DSA$RawDSA", localBoolean);
/*  266 */     signatureInfo.put("sun.security.provider.DSA$SHA1withDSA", localBoolean);
/*  267 */     signatureInfo.put("sun.security.rsa.RSASignature$MD2withRSA", localBoolean);
/*  268 */     signatureInfo.put("sun.security.rsa.RSASignature$MD5withRSA", localBoolean);
/*  269 */     signatureInfo.put("sun.security.rsa.RSASignature$SHA1withRSA", localBoolean);
/*  270 */     signatureInfo.put("sun.security.rsa.RSASignature$SHA256withRSA", localBoolean);
/*  271 */     signatureInfo.put("sun.security.rsa.RSASignature$SHA384withRSA", localBoolean);
/*  272 */     signatureInfo.put("sun.security.rsa.RSASignature$SHA512withRSA", localBoolean);
/*  273 */     signatureInfo.put("com.sun.net.ssl.internal.ssl.RSASignature", localBoolean);
/*  274 */     signatureInfo.put("sun.security.pkcs11.P11Signature", localBoolean);
/*      */   }
/*      */ 
/*      */   private static class CipherAdapter extends SignatureSpi
/*      */   {
/*      */     private final Cipher cipher;
/*      */     private ByteArrayOutputStream data;
/*      */ 
/*      */     CipherAdapter(Cipher paramCipher)
/*      */     {
/* 1213 */       this.cipher = paramCipher;
/*      */     }
/*      */ 
/*      */     protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException
/*      */     {
/* 1218 */       this.cipher.init(2, paramPublicKey);
/* 1219 */       if (this.data == null)
/* 1220 */         this.data = new ByteArrayOutputStream(128);
/*      */       else
/* 1222 */         this.data.reset();
/*      */     }
/*      */ 
/*      */     protected void engineInitSign(PrivateKey paramPrivateKey)
/*      */       throws InvalidKeyException
/*      */     {
/* 1228 */       this.cipher.init(1, paramPrivateKey);
/* 1229 */       this.data = null;
/*      */     }
/*      */ 
/*      */     protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException
/*      */     {
/* 1234 */       this.cipher.init(1, paramPrivateKey, paramSecureRandom);
/* 1235 */       this.data = null;
/*      */     }
/*      */ 
/*      */     protected void engineUpdate(byte paramByte) throws SignatureException {
/* 1239 */       engineUpdate(new byte[] { paramByte }, 0, 1);
/*      */     }
/*      */ 
/*      */     protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SignatureException
/*      */     {
/* 1244 */       if (this.data != null) {
/* 1245 */         this.data.write(paramArrayOfByte, paramInt1, paramInt2);
/* 1246 */         return;
/*      */       }
/* 1248 */       byte[] arrayOfByte = this.cipher.update(paramArrayOfByte, paramInt1, paramInt2);
/* 1249 */       if ((arrayOfByte != null) && (arrayOfByte.length != 0))
/* 1250 */         throw new SignatureException("Cipher unexpectedly returned data");
/*      */     }
/*      */ 
/*      */     protected byte[] engineSign() throws SignatureException
/*      */     {
/*      */       try
/*      */       {
/* 1257 */         return this.cipher.doFinal();
/*      */       } catch (IllegalBlockSizeException localIllegalBlockSizeException) {
/* 1259 */         throw new SignatureException("doFinal() failed", localIllegalBlockSizeException);
/*      */       } catch (BadPaddingException localBadPaddingException) {
/* 1261 */         throw new SignatureException("doFinal() failed", localBadPaddingException);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected boolean engineVerify(byte[] paramArrayOfByte) throws SignatureException
/*      */     {
/*      */       try {
/* 1268 */         byte[] arrayOfByte1 = this.cipher.doFinal(paramArrayOfByte);
/* 1269 */         byte[] arrayOfByte2 = this.data.toByteArray();
/* 1270 */         this.data.reset();
/* 1271 */         return Arrays.equals(arrayOfByte1, arrayOfByte2);
/*      */       }
/*      */       catch (BadPaddingException localBadPaddingException)
/*      */       {
/* 1275 */         return false;
/*      */       } catch (IllegalBlockSizeException localIllegalBlockSizeException) {
/* 1277 */         throw new SignatureException("doFinal() failed", localIllegalBlockSizeException);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void engineSetParameter(String paramString, Object paramObject) throws InvalidParameterException
/*      */     {
/* 1283 */       throw new InvalidParameterException("Parameters not supported");
/*      */     }
/*      */ 
/*      */     protected Object engineGetParameter(String paramString) throws InvalidParameterException
/*      */     {
/* 1288 */       throw new InvalidParameterException("Parameters not supported");
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Delegate extends Signature
/*      */   {
/*      */     private SignatureSpi sigSpi;
/*      */     private final Object lock;
/*      */     private Provider.Service firstService;
/*      */     private Iterator<Provider.Service> serviceIterator;
/*  980 */     private static int warnCount = 10;
/*      */     private static final int I_PUB = 1;
/*      */     private static final int I_PRIV = 2;
/*      */     private static final int I_PRIV_SR = 3;
/*      */ 
/*      */     Delegate(SignatureSpi paramSignatureSpi, String paramString)
/*      */     {
/*  921 */       super();
/*  922 */       this.sigSpi = paramSignatureSpi;
/*  923 */       this.lock = null;
/*      */     }
/*      */ 
/*      */     Delegate(Provider.Service paramService, Iterator<Provider.Service> paramIterator, String paramString)
/*      */     {
/*  929 */       super();
/*  930 */       this.firstService = paramService;
/*  931 */       this.serviceIterator = paramIterator;
/*  932 */       this.lock = new Object();
/*      */     }
/*      */ 
/*      */     public Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  944 */       chooseFirstProvider();
/*  945 */       if ((this.sigSpi instanceof Cloneable)) {
/*  946 */         SignatureSpi localSignatureSpi = (SignatureSpi)this.sigSpi.clone();
/*      */ 
/*  950 */         Delegate localDelegate = new Delegate(localSignatureSpi, this.algorithm);
/*      */ 
/*  952 */         localDelegate.provider = this.provider;
/*  953 */         return localDelegate;
/*      */       }
/*  955 */       throw new CloneNotSupportedException();
/*      */     }
/*      */ 
/*      */     private static SignatureSpi newInstance(Provider.Service paramService)
/*      */       throws NoSuchAlgorithmException
/*      */     {
/*  961 */       if (paramService.getType().equals("Cipher")) {
/*      */         try
/*      */         {
/*  964 */           Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", paramService.getProvider());
/*  965 */           return new Signature.CipherAdapter(localCipher);
/*      */         } catch (NoSuchPaddingException localNoSuchPaddingException) {
/*  967 */           throw new NoSuchAlgorithmException(localNoSuchPaddingException);
/*      */         }
/*      */       }
/*  970 */       Object localObject = paramService.newInstance(null);
/*  971 */       if (!(localObject instanceof SignatureSpi)) {
/*  972 */         throw new NoSuchAlgorithmException("Not a SignatureSpi: " + localObject.getClass().getName());
/*      */       }
/*      */ 
/*  975 */       return (SignatureSpi)localObject;
/*      */     }
/*      */ 
/*      */     void chooseFirstProvider()
/*      */     {
/*  988 */       if (this.sigSpi != null) {
/*  989 */         return;
/*      */       }
/*  991 */       synchronized (this.lock) {
/*  992 */         if (this.sigSpi != null) {
/*  993 */           return;
/*      */         }
/*  995 */         if (Signature.debug != null) {
/*  996 */           int i = --warnCount;
/*  997 */           if (i >= 0) {
/*  998 */             Signature.debug.println("Signature.init() not first method called, disabling delayed provider selection");
/*      */ 
/* 1000 */             if (i == 0) {
/* 1001 */               Signature.debug.println("Further warnings of this type will be suppressed");
/*      */             }
/*      */ 
/* 1004 */             new Exception("Call trace").printStackTrace();
/*      */           }
/*      */         }
/* 1007 */         Object localObject1 = null;
/* 1008 */         while ((this.firstService != null) || (this.serviceIterator.hasNext()))
/*      */         {
/* 1010 */           if (this.firstService != null) {
/* 1011 */             localObject2 = this.firstService;
/* 1012 */             this.firstService = null;
/*      */           } else {
/* 1014 */             localObject2 = (Provider.Service)this.serviceIterator.next();
/*      */           }
/* 1016 */           if (Signature.isSpi((Provider.Service)localObject2))
/*      */           {
/*      */             try
/*      */             {
/* 1020 */               this.sigSpi = newInstance((Provider.Service)localObject2);
/* 1021 */               this.provider = ((Provider.Service)localObject2).getProvider();
/*      */ 
/* 1023 */               this.firstService = null;
/* 1024 */               this.serviceIterator = null;
/* 1025 */               return;
/*      */             } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 1027 */               localObject1 = localNoSuchAlgorithmException;
/*      */             }
/*      */           }
/*      */         }
/* 1030 */         Object localObject2 = new ProviderException("Could not construct SignatureSpi instance");
/*      */ 
/* 1032 */         if (localObject1 != null) {
/* 1033 */           ((ProviderException)localObject2).initCause(localObject1);
/*      */         }
/* 1035 */         throw ((Throwable)localObject2);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void chooseProvider(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException
/*      */     {
/* 1041 */       synchronized (this.lock) {
/* 1042 */         if (this.sigSpi != null) {
/* 1043 */           init(this.sigSpi, paramInt, paramKey, paramSecureRandom);
/* 1044 */           return;
/*      */         }
/* 1046 */         Object localObject1 = null;
/* 1047 */         while ((this.firstService != null) || (this.serviceIterator.hasNext()))
/*      */         {
/* 1049 */           if (this.firstService != null) {
/* 1050 */             localObject2 = this.firstService;
/* 1051 */             this.firstService = null;
/*      */           } else {
/* 1053 */             localObject2 = (Provider.Service)this.serviceIterator.next();
/*      */           }
/*      */ 
/* 1056 */           if ((((Provider.Service)localObject2).supportsParameter(paramKey)) && 
/* 1060 */             (Signature.isSpi((Provider.Service)localObject2)))
/*      */           {
/*      */             try
/*      */             {
/* 1064 */               SignatureSpi localSignatureSpi = newInstance((Provider.Service)localObject2);
/* 1065 */               init(localSignatureSpi, paramInt, paramKey, paramSecureRandom);
/* 1066 */               this.provider = ((Provider.Service)localObject2).getProvider();
/* 1067 */               this.sigSpi = localSignatureSpi;
/* 1068 */               this.firstService = null;
/* 1069 */               this.serviceIterator = null;
/* 1070 */               return;
/*      */             }
/*      */             catch (Exception localException)
/*      */             {
/* 1075 */               if (localObject1 == null) {
/* 1076 */                 localObject1 = localException;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1081 */         if ((localObject1 instanceof InvalidKeyException)) {
/* 1082 */           throw ((InvalidKeyException)localObject1);
/*      */         }
/* 1084 */         if ((localObject1 instanceof RuntimeException)) {
/* 1085 */           throw ((RuntimeException)localObject1);
/*      */         }
/* 1087 */         Object localObject2 = paramKey != null ? paramKey.getClass().getName() : "(null)";
/* 1088 */         throw new InvalidKeyException("No installed provider supports this key: " + (String)localObject2, localObject1);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void init(SignatureSpi paramSignatureSpi, int paramInt, Key paramKey, SecureRandom paramSecureRandom)
/*      */       throws InvalidKeyException
/*      */     {
/* 1100 */       switch (paramInt) {
/*      */       case 1:
/* 1102 */         paramSignatureSpi.engineInitVerify((PublicKey)paramKey);
/* 1103 */         break;
/*      */       case 2:
/* 1105 */         paramSignatureSpi.engineInitSign((PrivateKey)paramKey);
/* 1106 */         break;
/*      */       case 3:
/* 1108 */         paramSignatureSpi.engineInitSign((PrivateKey)paramKey, paramSecureRandom);
/* 1109 */         break;
/*      */       default:
/* 1111 */         throw new AssertionError("Internal error: " + paramInt);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException
/*      */     {
/* 1117 */       if (this.sigSpi != null)
/* 1118 */         this.sigSpi.engineInitVerify(paramPublicKey);
/*      */       else
/* 1120 */         chooseProvider(1, paramPublicKey, null);
/*      */     }
/*      */ 
/*      */     protected void engineInitSign(PrivateKey paramPrivateKey)
/*      */       throws InvalidKeyException
/*      */     {
/* 1126 */       if (this.sigSpi != null)
/* 1127 */         this.sigSpi.engineInitSign(paramPrivateKey);
/*      */       else
/* 1129 */         chooseProvider(2, paramPrivateKey, null);
/*      */     }
/*      */ 
/*      */     protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
/*      */       throws InvalidKeyException
/*      */     {
/* 1135 */       if (this.sigSpi != null)
/* 1136 */         this.sigSpi.engineInitSign(paramPrivateKey, paramSecureRandom);
/*      */       else
/* 1138 */         chooseProvider(3, paramPrivateKey, paramSecureRandom);
/*      */     }
/*      */ 
/*      */     protected void engineUpdate(byte paramByte) throws SignatureException
/*      */     {
/* 1143 */       chooseFirstProvider();
/* 1144 */       this.sigSpi.engineUpdate(paramByte);
/*      */     }
/*      */ 
/*      */     protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SignatureException
/*      */     {
/* 1149 */       chooseFirstProvider();
/* 1150 */       this.sigSpi.engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     protected void engineUpdate(ByteBuffer paramByteBuffer) {
/* 1154 */       chooseFirstProvider();
/* 1155 */       this.sigSpi.engineUpdate(paramByteBuffer);
/*      */     }
/*      */ 
/*      */     protected byte[] engineSign() throws SignatureException {
/* 1159 */       chooseFirstProvider();
/* 1160 */       return this.sigSpi.engineSign();
/*      */     }
/*      */ 
/*      */     protected int engineSign(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SignatureException
/*      */     {
/* 1165 */       chooseFirstProvider();
/* 1166 */       return this.sigSpi.engineSign(paramArrayOfByte, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     protected boolean engineVerify(byte[] paramArrayOfByte) throws SignatureException
/*      */     {
/* 1171 */       chooseFirstProvider();
/* 1172 */       return this.sigSpi.engineVerify(paramArrayOfByte);
/*      */     }
/*      */ 
/*      */     protected boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SignatureException
/*      */     {
/* 1177 */       chooseFirstProvider();
/* 1178 */       return this.sigSpi.engineVerify(paramArrayOfByte, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     protected void engineSetParameter(String paramString, Object paramObject) throws InvalidParameterException
/*      */     {
/* 1183 */       chooseFirstProvider();
/* 1184 */       this.sigSpi.engineSetParameter(paramString, paramObject);
/*      */     }
/*      */ 
/*      */     protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException
/*      */     {
/* 1189 */       chooseFirstProvider();
/* 1190 */       this.sigSpi.engineSetParameter(paramAlgorithmParameterSpec);
/*      */     }
/*      */ 
/*      */     protected Object engineGetParameter(String paramString) throws InvalidParameterException
/*      */     {
/* 1195 */       chooseFirstProvider();
/* 1196 */       return this.sigSpi.engineGetParameter(paramString);
/*      */     }
/*      */ 
/*      */     protected AlgorithmParameters engineGetParameters() {
/* 1200 */       chooseFirstProvider();
/* 1201 */       return this.sigSpi.engineGetParameters();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.Signature
 * JD-Core Version:    0.6.2
 */