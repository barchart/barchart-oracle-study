/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
/*     */ import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transforms;
/*     */ import com.sun.org.apache.xml.internal.security.utils.Base64;
/*     */ import com.sun.org.apache.xml.internal.security.utils.I18n;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignerOutputStream;
/*     */ import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.Key;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.crypto.SecretKey;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public final class XMLSignature extends SignatureElementProxy
/*     */ {
/*  83 */   static Logger log = Logger.getLogger(XMLSignature.class.getName());
/*     */   public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
/*     */   public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
/*     */   public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
/*     */   public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
/*     */   public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
/*     */   public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
/*     */   public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
/*     */   public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
/*     */   public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
/*     */   public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
/*     */   public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
/*     */   public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
/*     */   public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
/*     */   public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
/*     */   public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
/* 125 */   private SignedInfo _signedInfo = null;
/*     */ 
/* 128 */   private KeyInfo _keyInfo = null;
/*     */ 
/* 135 */   private boolean _followManifestsDuringValidation = false;
/*     */   private Element signatureValueElement;
/*     */ 
/*     */   public XMLSignature(Document paramDocument, String paramString1, String paramString2)
/*     */     throws XMLSecurityException
/*     */   {
/* 153 */     this(paramDocument, paramString1, paramString2, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
/*     */   }
/*     */ 
/*     */   public XMLSignature(Document paramDocument, String paramString1, String paramString2, int paramInt)
/*     */     throws XMLSecurityException
/*     */   {
/* 169 */     this(paramDocument, paramString1, paramString2, paramInt, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
/*     */   }
/*     */ 
/*     */   public XMLSignature(Document paramDocument, String paramString1, String paramString2, String paramString3)
/*     */     throws XMLSecurityException
/*     */   {
/* 185 */     this(paramDocument, paramString1, paramString2, 0, paramString3);
/*     */   }
/*     */ 
/*     */   public XMLSignature(Document paramDocument, String paramString1, String paramString2, int paramInt, String paramString3)
/*     */     throws XMLSecurityException
/*     */   {
/* 202 */     super(paramDocument);
/*     */ 
/* 204 */     String str = getDefaultPrefixBindings("http://www.w3.org/2000/09/xmldsig#");
/*     */ 
/* 206 */     if (str == null) {
/* 207 */       this._constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */     else {
/* 210 */       this._constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str, "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */ 
/* 213 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 215 */     this._baseURI = paramString1;
/* 216 */     this._signedInfo = new SignedInfo(this._doc, paramString2, paramInt, paramString3);
/*     */ 
/* 220 */     this._constructionElement.appendChild(this._signedInfo.getElement());
/* 221 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 224 */     this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this._doc, "SignatureValue");
/*     */ 
/* 228 */     this._constructionElement.appendChild(this.signatureValueElement);
/* 229 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public XMLSignature(Document paramDocument, String paramString, Element paramElement1, Element paramElement2)
/*     */     throws XMLSecurityException
/*     */   {
/* 243 */     super(paramDocument);
/*     */ 
/* 245 */     String str = getDefaultPrefixBindings("http://www.w3.org/2000/09/xmldsig#");
/*     */ 
/* 247 */     if (str == null) {
/* 248 */       this._constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */     else {
/* 251 */       this._constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str, "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */ 
/* 254 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 256 */     this._baseURI = paramString;
/* 257 */     this._signedInfo = new SignedInfo(this._doc, paramElement1, paramElement2);
/*     */ 
/* 259 */     this._constructionElement.appendChild(this._signedInfo.getElement());
/* 260 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 263 */     this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this._doc, "SignatureValue");
/*     */ 
/* 267 */     this._constructionElement.appendChild(this.signatureValueElement);
/* 268 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public XMLSignature(Element paramElement, String paramString)
/*     */     throws XMLSignatureException, XMLSecurityException
/*     */   {
/* 283 */     super(paramElement, paramString);
/*     */ 
/* 286 */     Element localElement = XMLUtils.getNextElement(paramElement.getFirstChild());
/*     */ 
/* 290 */     if (localElement == null) {
/* 291 */       localObject = new Object[] { "SignedInfo", "Signature" };
/*     */ 
/* 294 */       throw new XMLSignatureException("xml.WrongContent", (Object[])localObject);
/*     */     }
/*     */ 
/* 298 */     this._signedInfo = new SignedInfo(localElement, paramString);
/*     */ 
/* 301 */     this.signatureValueElement = XMLUtils.getNextElement(localElement.getNextSibling());
/*     */ 
/* 305 */     if (this.signatureValueElement == null) {
/* 306 */       localObject = new Object[] { "SignatureValue", "Signature" };
/*     */ 
/* 309 */       throw new XMLSignatureException("xml.WrongContent", (Object[])localObject);
/*     */     }
/*     */ 
/* 313 */     Object localObject = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling());
/*     */ 
/* 317 */     if ((localObject != null) && (((Element)localObject).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) && (((Element)localObject).getLocalName().equals("KeyInfo")))
/*     */     {
/* 319 */       this._keyInfo = new KeyInfo((Element)localObject, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setId(String paramString)
/*     */   {
/* 330 */     if (paramString != null) {
/* 331 */       this._constructionElement.setAttributeNS(null, "Id", paramString);
/* 332 */       IdResolver.registerElementById(this._constructionElement, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 342 */     return this._constructionElement.getAttributeNS(null, "Id");
/*     */   }
/*     */ 
/*     */   public SignedInfo getSignedInfo()
/*     */   {
/* 351 */     return this._signedInfo;
/*     */   }
/*     */ 
/*     */   public byte[] getSignatureValue()
/*     */     throws XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 364 */       return Base64.decode(this.signatureValueElement);
/*     */     }
/*     */     catch (Base64DecodingException localBase64DecodingException)
/*     */     {
/* 368 */       throw new XMLSignatureException("empty", localBase64DecodingException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setSignatureValueElement(byte[] paramArrayOfByte)
/*     */   {
/* 380 */     while (this.signatureValueElement.hasChildNodes()) {
/* 381 */       this.signatureValueElement.removeChild(this.signatureValueElement.getFirstChild());
/*     */     }
/*     */ 
/* 385 */     String str = Base64.encode(paramArrayOfByte);
/*     */ 
/* 387 */     if ((str.length() > 76) && (!XMLUtils.ignoreLineBreaks())) {
/* 388 */       str = "\n" + str + "\n";
/*     */     }
/*     */ 
/* 391 */     Text localText = this._doc.createTextNode(str);
/* 392 */     this.signatureValueElement.appendChild(localText);
/*     */   }
/*     */ 
/*     */   public KeyInfo getKeyInfo()
/*     */   {
/* 406 */     if (this._keyInfo == null)
/*     */     {
/* 409 */       this._keyInfo = new KeyInfo(this._doc);
/*     */ 
/* 412 */       Element localElement1 = this._keyInfo.getElement();
/* 413 */       Element localElement2 = null;
/* 414 */       Node localNode = this._constructionElement.getFirstChild();
/* 415 */       localElement2 = XMLUtils.selectDsNode(localNode, "Object", 0);
/*     */ 
/* 417 */       if (localElement2 != null)
/*     */       {
/* 420 */         this._constructionElement.insertBefore(localElement1, localElement2);
/*     */ 
/* 422 */         XMLUtils.addReturnBeforeChild(this._constructionElement, localElement2);
/*     */       }
/*     */       else
/*     */       {
/* 426 */         this._constructionElement.appendChild(localElement1);
/* 427 */         XMLUtils.addReturnToElement(this._constructionElement);
/*     */       }
/*     */     }
/*     */ 
/* 431 */     return this._keyInfo;
/*     */   }
/*     */ 
/*     */   public void appendObject(ObjectContainer paramObjectContainer)
/*     */     throws XMLSignatureException
/*     */   {
/* 451 */     this._constructionElement.appendChild(paramObjectContainer.getElement());
/* 452 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public ObjectContainer getObjectItem(int paramInt)
/*     */   {
/* 467 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "Object", paramInt);
/*     */     try
/*     */     {
/* 471 */       return new ObjectContainer(localElement, this._baseURI); } catch (XMLSecurityException localXMLSecurityException) {
/*     */     }
/* 473 */     return null;
/*     */   }
/*     */ 
/*     */   public int getObjectLength()
/*     */   {
/* 483 */     return length("http://www.w3.org/2000/09/xmldsig#", "Object");
/*     */   }
/*     */ 
/*     */   public void sign(Key paramKey)
/*     */     throws XMLSignatureException
/*     */   {
/* 495 */     if ((paramKey instanceof PublicKey)) {
/* 496 */       throw new IllegalArgumentException(I18n.translate("algorithms.operationOnlyVerification"));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 503 */       SignedInfo localSignedInfo = getSignedInfo();
/* 504 */       SignatureAlgorithm localSignatureAlgorithm = localSignedInfo.getSignatureAlgorithm();
/*     */ 
/* 506 */       localSignatureAlgorithm.initSign(paramKey);
/*     */ 
/* 509 */       localSignedInfo.generateDigestValues();
/* 510 */       UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(new SignerOutputStream(localSignatureAlgorithm));
/*     */       try {
/* 512 */         localUnsyncBufferedOutputStream.close();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/* 517 */       localSignedInfo.signInOctectStream(localUnsyncBufferedOutputStream);
/*     */ 
/* 519 */       byte[] arrayOfByte = localSignatureAlgorithm.sign();
/*     */ 
/* 522 */       setSignatureValueElement(arrayOfByte);
/*     */     }
/*     */     catch (CanonicalizationException localCanonicalizationException) {
/* 525 */       throw new XMLSignatureException("empty", localCanonicalizationException);
/*     */     } catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/* 527 */       throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 529 */       throw new XMLSignatureException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addResourceResolver(ResourceResolver paramResourceResolver)
/*     */   {
/* 539 */     getSignedInfo().addResourceResolver(paramResourceResolver);
/*     */   }
/*     */ 
/*     */   public void addResourceResolver(ResourceResolverSpi paramResourceResolverSpi)
/*     */   {
/* 548 */     getSignedInfo().addResourceResolver(paramResourceResolverSpi);
/*     */   }
/*     */ 
/*     */   public boolean checkSignatureValue(X509Certificate paramX509Certificate)
/*     */     throws XMLSignatureException
/*     */   {
/* 565 */     if (paramX509Certificate != null)
/*     */     {
/* 568 */       return checkSignatureValue(paramX509Certificate.getPublicKey());
/*     */     }
/*     */ 
/* 571 */     Object[] arrayOfObject = { "Didn't get a certificate" };
/* 572 */     throw new XMLSignatureException("empty", arrayOfObject);
/*     */   }
/*     */ 
/*     */   public boolean checkSignatureValue(Key paramKey)
/*     */     throws XMLSignatureException
/*     */   {
/*     */     Object localObject;
/* 589 */     if (paramKey == null) {
/* 590 */       localObject = new Object[] { "Didn't get a key" };
/*     */ 
/* 592 */       throw new XMLSignatureException("empty", (Object[])localObject);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 600 */       localObject = getSignedInfo();
/*     */ 
/* 603 */       SignatureAlgorithm localSignatureAlgorithm = ((SignedInfo)localObject).getSignatureAlgorithm();
/* 604 */       if (log.isLoggable(Level.FINE)) {
/* 605 */         log.log(Level.FINE, "SignatureMethodURI = " + localSignatureAlgorithm.getAlgorithmURI());
/* 606 */         log.log(Level.FINE, "jceSigAlgorithm    = " + localSignatureAlgorithm.getJCEAlgorithmString());
/* 607 */         log.log(Level.FINE, "jceSigProvider     = " + localSignatureAlgorithm.getJCEProviderName());
/* 608 */         log.log(Level.FINE, "PublicKey = " + paramKey);
/*     */       }
/* 610 */       localSignatureAlgorithm.initVerify(paramKey);
/*     */ 
/* 613 */       SignerOutputStream localSignerOutputStream = new SignerOutputStream(localSignatureAlgorithm);
/* 614 */       UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(localSignerOutputStream);
/* 615 */       ((SignedInfo)localObject).signInOctectStream(localUnsyncBufferedOutputStream);
/*     */       try {
/* 617 */         localUnsyncBufferedOutputStream.close();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */ 
/* 623 */       byte[] arrayOfByte = getSignatureValue();
/*     */ 
/* 627 */       if (!localSignatureAlgorithm.verify(arrayOfByte)) {
/* 628 */         log.log(Level.WARNING, "Signature verification failed.");
/* 629 */         return false;
/*     */       }
/*     */ 
/* 632 */       return ((SignedInfo)localObject).verify(this._followManifestsDuringValidation);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 634 */       throw new XMLSignatureException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addDocument(String paramString1, Transforms paramTransforms, String paramString2, String paramString3, String paramString4)
/*     */     throws XMLSignatureException
/*     */   {
/* 654 */     this._signedInfo.addDocument(this._baseURI, paramString1, paramTransforms, paramString2, paramString3, paramString4);
/*     */   }
/*     */ 
/*     */   public void addDocument(String paramString1, Transforms paramTransforms, String paramString2)
/*     */     throws XMLSignatureException
/*     */   {
/* 670 */     this._signedInfo.addDocument(this._baseURI, paramString1, paramTransforms, paramString2, null, null);
/*     */   }
/*     */ 
/*     */   public void addDocument(String paramString, Transforms paramTransforms)
/*     */     throws XMLSignatureException
/*     */   {
/* 684 */     this._signedInfo.addDocument(this._baseURI, paramString, paramTransforms, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
/*     */   }
/*     */ 
/*     */   public void addDocument(String paramString)
/*     */     throws XMLSignatureException
/*     */   {
/* 696 */     this._signedInfo.addDocument(this._baseURI, paramString, null, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
/*     */   }
/*     */ 
/*     */   public void addKeyInfo(X509Certificate paramX509Certificate)
/*     */     throws XMLSecurityException
/*     */   {
/* 709 */     X509Data localX509Data = new X509Data(this._doc);
/*     */ 
/* 711 */     localX509Data.addCertificate(paramX509Certificate);
/* 712 */     getKeyInfo().add(localX509Data);
/*     */   }
/*     */ 
/*     */   public void addKeyInfo(PublicKey paramPublicKey)
/*     */   {
/* 722 */     getKeyInfo().add(paramPublicKey);
/*     */   }
/*     */ 
/*     */   public SecretKey createSecretKey(byte[] paramArrayOfByte)
/*     */   {
/* 736 */     return getSignedInfo().createSecretKey(paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public void setFollowNestedManifests(boolean paramBoolean)
/*     */   {
/* 749 */     this._followManifestsDuringValidation = paramBoolean;
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 758 */     return "Signature";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.XMLSignature
 * JD-Core Version:    0.6.2
 */