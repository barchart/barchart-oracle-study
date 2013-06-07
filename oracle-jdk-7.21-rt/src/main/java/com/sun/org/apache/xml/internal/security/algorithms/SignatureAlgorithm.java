/*     */ package com.sun.org.apache.xml.internal.security.algorithms;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
/*     */ import java.security.Key;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class SignatureAlgorithm extends Algorithm
/*     */ {
/*  47 */   static Logger log = Logger.getLogger(SignatureAlgorithm.class.getName());
/*     */ 
/*  51 */   static boolean _alreadyInitialized = false;
/*     */ 
/*  54 */   static HashMap _algorithmHash = null;
/*     */ 
/*  56 */   static ThreadLocal instancesSigning = new ThreadLocal() {
/*     */     protected Object initialValue() {
/*  58 */       return new HashMap();
/*     */     }
/*  56 */   };
/*     */ 
/*  62 */   static ThreadLocal instancesVerify = new ThreadLocal() {
/*     */     protected Object initialValue() {
/*  64 */       return new HashMap();
/*     */     }
/*  62 */   };
/*     */ 
/*  68 */   static ThreadLocal keysSigning = new ThreadLocal() {
/*     */     protected Object initialValue() {
/*  70 */       return new HashMap();
/*     */     }
/*  68 */   };
/*     */ 
/*  73 */   static ThreadLocal keysVerify = new ThreadLocal() {
/*     */     protected Object initialValue() {
/*  75 */       return new HashMap();
/*     */     }
/*  73 */   };
/*     */ 
/*  81 */   protected SignatureAlgorithmSpi _signatureAlgorithm = null;
/*     */   private String algorithmURI;
/*     */ 
/*     */   public SignatureAlgorithm(Document paramDocument, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/*  94 */     super(paramDocument, paramString);
/*  95 */     this.algorithmURI = paramString;
/*     */   }
/*     */ 
/*     */   private void initializeAlgorithm(boolean paramBoolean) throws XMLSignatureException
/*     */   {
/* 100 */     if (this._signatureAlgorithm != null) {
/* 101 */       return;
/*     */     }
/* 103 */     this._signatureAlgorithm = (paramBoolean ? getInstanceForSigning(this.algorithmURI) : getInstanceForVerify(this.algorithmURI));
/* 104 */     this._signatureAlgorithm.engineGetContextFromElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   private static SignatureAlgorithmSpi getInstanceForSigning(String paramString) throws XMLSignatureException {
/* 108 */     SignatureAlgorithmSpi localSignatureAlgorithmSpi = (SignatureAlgorithmSpi)((Map)instancesSigning.get()).get(paramString);
/* 109 */     if (localSignatureAlgorithmSpi != null) {
/* 110 */       localSignatureAlgorithmSpi.reset();
/* 111 */       return localSignatureAlgorithmSpi;
/*     */     }
/* 113 */     localSignatureAlgorithmSpi = buildSigner(paramString, localSignatureAlgorithmSpi);
/* 114 */     ((Map)instancesSigning.get()).put(paramString, localSignatureAlgorithmSpi);
/* 115 */     return localSignatureAlgorithmSpi;
/*     */   }
/*     */   private static SignatureAlgorithmSpi getInstanceForVerify(String paramString) throws XMLSignatureException {
/* 118 */     SignatureAlgorithmSpi localSignatureAlgorithmSpi = (SignatureAlgorithmSpi)((Map)instancesVerify.get()).get(paramString);
/* 119 */     if (localSignatureAlgorithmSpi != null) {
/* 120 */       localSignatureAlgorithmSpi.reset();
/* 121 */       return localSignatureAlgorithmSpi;
/*     */     }
/* 123 */     localSignatureAlgorithmSpi = buildSigner(paramString, localSignatureAlgorithmSpi);
/* 124 */     ((Map)instancesVerify.get()).put(paramString, localSignatureAlgorithmSpi);
/* 125 */     return localSignatureAlgorithmSpi;
/*     */   }
/*     */ 
/*     */   private static SignatureAlgorithmSpi buildSigner(String paramString, SignatureAlgorithmSpi paramSignatureAlgorithmSpi) throws XMLSignatureException {
/*     */     try {
/* 130 */       Class localClass = getImplementingClass(paramString);
/*     */ 
/* 132 */       if (log.isLoggable(Level.FINE)) {
/* 133 */         log.log(Level.FINE, "Create URI \"" + paramString + "\" class \"" + localClass + "\"");
/*     */       }
/* 135 */       return (SignatureAlgorithmSpi)localClass.newInstance();
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 138 */       arrayOfObject = new Object[] { paramString, localIllegalAccessException.getMessage() };
/*     */ 
/* 140 */       throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localIllegalAccessException);
/*     */     }
/*     */     catch (InstantiationException localInstantiationException) {
/* 143 */       arrayOfObject = new Object[] { paramString, localInstantiationException.getMessage() };
/*     */ 
/* 145 */       throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localInstantiationException);
/*     */     }
/*     */     catch (NullPointerException localNullPointerException) {
/* 148 */       Object[] arrayOfObject = { paramString, localNullPointerException.getMessage() };
/*     */ 
/* 150 */       throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localNullPointerException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SignatureAlgorithm(Document paramDocument, String paramString, int paramInt)
/*     */     throws XMLSecurityException
/*     */   {
/* 167 */     this(paramDocument, paramString);
/* 168 */     this.algorithmURI = paramString;
/* 169 */     initializeAlgorithm(true);
/* 170 */     this._signatureAlgorithm.engineSetHMACOutputLength(paramInt);
/* 171 */     ((IntegrityHmac)this._signatureAlgorithm).engineAddContextToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public SignatureAlgorithm(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/* 185 */     super(paramElement, paramString);
/* 186 */     this.algorithmURI = getURI();
/*     */   }
/*     */ 
/*     */   public byte[] sign()
/*     */     throws XMLSignatureException
/*     */   {
/* 197 */     return this._signatureAlgorithm.engineSign();
/*     */   }
/*     */ 
/*     */   public String getJCEAlgorithmString()
/*     */   {
/*     */     try
/*     */     {
/* 208 */       return getInstanceForVerify(this.algorithmURI).engineGetJCEAlgorithmString();
/*     */     } catch (XMLSignatureException localXMLSignatureException) {
/*     */     }
/* 211 */     return null;
/*     */   }
/*     */ 
/*     */   public String getJCEProviderName()
/*     */   {
/*     */     try
/*     */     {
/* 222 */       return getInstanceForVerify(this.algorithmURI).engineGetJCEProviderName(); } catch (XMLSignatureException localXMLSignatureException) {
/*     */     }
/* 224 */     return null;
/*     */   }
/*     */ 
/*     */   public void update(byte[] paramArrayOfByte)
/*     */     throws XMLSignatureException
/*     */   {
/* 236 */     this._signatureAlgorithm.engineUpdate(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public void update(byte paramByte)
/*     */     throws XMLSignatureException
/*     */   {
/* 247 */     this._signatureAlgorithm.engineUpdate(paramByte);
/*     */   }
/*     */ 
/*     */   public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws XMLSignatureException
/*     */   {
/* 261 */     this._signatureAlgorithm.engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public void initSign(Key paramKey)
/*     */     throws XMLSignatureException
/*     */   {
/* 272 */     initializeAlgorithm(true);
/* 273 */     Map localMap = (Map)keysSigning.get();
/* 274 */     if (localMap.get(this.algorithmURI) == paramKey) {
/* 275 */       return;
/*     */     }
/* 277 */     localMap.put(this.algorithmURI, paramKey);
/* 278 */     this._signatureAlgorithm.engineInitSign(paramKey);
/*     */   }
/*     */ 
/*     */   public void initSign(Key paramKey, SecureRandom paramSecureRandom)
/*     */     throws XMLSignatureException
/*     */   {
/* 291 */     initializeAlgorithm(true);
/* 292 */     this._signatureAlgorithm.engineInitSign(paramKey, paramSecureRandom);
/*     */   }
/*     */ 
/*     */   public void initSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec)
/*     */     throws XMLSignatureException
/*     */   {
/* 306 */     initializeAlgorithm(true);
/* 307 */     this._signatureAlgorithm.engineInitSign(paramKey, paramAlgorithmParameterSpec);
/*     */   }
/*     */ 
/*     */   public void setParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
/*     */     throws XMLSignatureException
/*     */   {
/* 320 */     this._signatureAlgorithm.engineSetParameter(paramAlgorithmParameterSpec);
/*     */   }
/*     */ 
/*     */   public void initVerify(Key paramKey)
/*     */     throws XMLSignatureException
/*     */   {
/* 331 */     initializeAlgorithm(false);
/* 332 */     Map localMap = (Map)keysVerify.get();
/* 333 */     if (localMap.get(this.algorithmURI) == paramKey) {
/* 334 */       return;
/*     */     }
/* 336 */     localMap.put(this.algorithmURI, paramKey);
/* 337 */     this._signatureAlgorithm.engineInitVerify(paramKey);
/*     */   }
/*     */ 
/*     */   public boolean verify(byte[] paramArrayOfByte)
/*     */     throws XMLSignatureException
/*     */   {
/* 350 */     return this._signatureAlgorithm.engineVerify(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public final String getURI()
/*     */   {
/* 359 */     return this._constructionElement.getAttributeNS(null, "Algorithm");
/*     */   }
/*     */ 
/*     */   public static void providerInit()
/*     */   {
/* 369 */     if (log == null) {
/* 370 */       log = Logger.getLogger(SignatureAlgorithm.class.getName());
/*     */     }
/*     */ 
/* 375 */     log.log(Level.FINE, "Init() called");
/*     */ 
/* 377 */     if (!_alreadyInitialized) {
/* 378 */       _algorithmHash = new HashMap(10);
/* 379 */       _alreadyInitialized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void register(String paramString1, String paramString2)
/*     */     throws AlgorithmAlreadyRegisteredException, XMLSignatureException
/*     */   {
/* 395 */     if (log.isLoggable(Level.FINE)) {
/* 396 */       log.log(Level.FINE, "Try to register " + paramString1 + " " + paramString2);
/*     */     }
/*     */ 
/* 399 */     Class localClass = getImplementingClass(paramString1);
/*     */     Object[] arrayOfObject;
/* 401 */     if (localClass != null) {
/* 402 */       String str = localClass.getName();
/*     */ 
/* 404 */       if ((str != null) && (str.length() != 0)) {
/* 405 */         arrayOfObject = new Object[] { paramString1, str };
/*     */ 
/* 407 */         throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 412 */       _algorithmHash.put(paramString1, Class.forName(paramString2));
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 414 */       arrayOfObject = new Object[] { paramString1, localClassNotFoundException.getMessage() };
/*     */ 
/* 416 */       throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localClassNotFoundException);
/*     */     }
/*     */     catch (NullPointerException localNullPointerException) {
/* 419 */       arrayOfObject = new Object[] { paramString1, localNullPointerException.getMessage() };
/*     */ 
/* 421 */       throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, localNullPointerException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Class getImplementingClass(String paramString)
/*     */   {
/* 436 */     if (_algorithmHash == null) {
/* 437 */       return null;
/*     */     }
/*     */ 
/* 440 */     return (Class)_algorithmHash.get(paramString);
/*     */   }
/*     */ 
/*     */   public String getBaseNamespace()
/*     */   {
/* 449 */     return "http://www.w3.org/2000/09/xmldsig#";
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 458 */     return "SignatureMethod";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm
 * JD-Core Version:    0.6.2
 */