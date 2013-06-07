/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class SignedInfo extends Manifest
/*     */ {
/*  53 */   private SignatureAlgorithm _signatureAlgorithm = null;
/*     */ 
/*  56 */   private byte[] _c14nizedBytes = null;
/*     */   private Element c14nMethod;
/*     */   private Element signatureMethod;
/*     */ 
/*     */   public SignedInfo(Document paramDocument)
/*     */     throws XMLSecurityException
/*     */   {
/*  70 */     this(paramDocument, "http://www.w3.org/2000/09/xmldsig#dsa-sha1", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
/*     */   }
/*     */ 
/*     */   public SignedInfo(Document paramDocument, String paramString1, String paramString2)
/*     */     throws XMLSecurityException
/*     */   {
/*  88 */     this(paramDocument, paramString1, 0, paramString2);
/*     */   }
/*     */ 
/*     */   public SignedInfo(Document paramDocument, String paramString1, int paramInt, String paramString2)
/*     */     throws XMLSecurityException
/*     */   {
/* 106 */     super(paramDocument);
/*     */ 
/* 108 */     this.c14nMethod = XMLUtils.createElementInSignatureSpace(this._doc, "CanonicalizationMethod");
/*     */ 
/* 111 */     this.c14nMethod.setAttributeNS(null, "Algorithm", paramString2);
/*     */ 
/* 113 */     this._constructionElement.appendChild(this.c14nMethod);
/* 114 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 116 */     if (paramInt > 0) {
/* 117 */       this._signatureAlgorithm = new SignatureAlgorithm(this._doc, paramString1, paramInt);
/*     */     }
/*     */     else {
/* 120 */       this._signatureAlgorithm = new SignatureAlgorithm(this._doc, paramString1);
/*     */     }
/*     */ 
/* 124 */     this.signatureMethod = this._signatureAlgorithm.getElement();
/* 125 */     this._constructionElement.appendChild(this.signatureMethod);
/* 126 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public SignedInfo(Document paramDocument, Element paramElement1, Element paramElement2)
/*     */     throws XMLSecurityException
/*     */   {
/* 138 */     super(paramDocument);
/*     */ 
/* 140 */     this.c14nMethod = paramElement2;
/* 141 */     this._constructionElement.appendChild(this.c14nMethod);
/* 142 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 144 */     this._signatureAlgorithm = new SignatureAlgorithm(paramElement1, null);
/*     */ 
/* 147 */     this.signatureMethod = this._signatureAlgorithm.getElement();
/* 148 */     this._constructionElement.appendChild(this.signatureMethod);
/*     */ 
/* 150 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public SignedInfo(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/* 166 */     super(paramElement, paramString);
/*     */ 
/* 172 */     this.c14nMethod = XMLUtils.getNextElement(paramElement.getFirstChild());
/* 173 */     String str = getCanonicalizationMethodURI();
/* 174 */     if ((!str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")) && (!str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")) && (!str.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) && (!str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")))
/*     */     {
/*     */       try
/*     */       {
/* 181 */         Canonicalizer localCanonicalizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
/*     */ 
/* 184 */         this._c14nizedBytes = localCanonicalizer.canonicalizeSubtree(this._constructionElement);
/*     */ 
/* 186 */         DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 188 */         localDocumentBuilderFactory.setNamespaceAware(true);
/* 189 */         DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/* 190 */         Document localDocument = localDocumentBuilder.parse(new ByteArrayInputStream(this._c14nizedBytes));
/*     */ 
/* 192 */         Node localNode = this._doc.importNode(localDocument.getDocumentElement(), true);
/*     */ 
/* 195 */         this._constructionElement.getParentNode().replaceChild(localNode, this._constructionElement);
/*     */ 
/* 198 */         this._constructionElement = ((Element)localNode);
/*     */       } catch (ParserConfigurationException localParserConfigurationException) {
/* 200 */         throw new XMLSecurityException("empty", localParserConfigurationException);
/*     */       } catch (IOException localIOException) {
/* 202 */         throw new XMLSecurityException("empty", localIOException);
/*     */       } catch (SAXException localSAXException) {
/* 204 */         throw new XMLSecurityException("empty", localSAXException);
/*     */       }
/*     */     }
/* 207 */     this.signatureMethod = XMLUtils.getNextElement(this.c14nMethod.getNextSibling());
/* 208 */     this._signatureAlgorithm = new SignatureAlgorithm(this.signatureMethod, getBaseURI());
/*     */   }
/*     */ 
/*     */   public boolean verify()
/*     */     throws MissingResourceFailureException, XMLSecurityException
/*     */   {
/* 221 */     return super.verifyReferences(false);
/*     */   }
/*     */ 
/*     */   public boolean verify(boolean paramBoolean)
/*     */     throws MissingResourceFailureException, XMLSecurityException
/*     */   {
/* 234 */     return super.verifyReferences(paramBoolean);
/*     */   }
/*     */ 
/*     */   public byte[] getCanonicalizedOctetStream()
/*     */     throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException
/*     */   {
/* 249 */     if (this._c14nizedBytes == null)
/*     */     {
/* 251 */       localObject = Canonicalizer.getInstance(getCanonicalizationMethodURI());
/*     */ 
/* 254 */       this._c14nizedBytes = ((Canonicalizer)localObject).canonicalizeSubtree(this._constructionElement);
/*     */     }
/*     */ 
/* 259 */     Object localObject = new byte[this._c14nizedBytes.length];
/*     */ 
/* 261 */     System.arraycopy(this._c14nizedBytes, 0, localObject, 0, localObject.length);
/*     */ 
/* 263 */     return localObject;
/*     */   }
/*     */ 
/*     */   public void signInOctectStream(OutputStream paramOutputStream)
/*     */     throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException
/*     */   {
/* 277 */     if (this._c14nizedBytes == null) {
/* 278 */       Canonicalizer localCanonicalizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
/*     */ 
/* 280 */       localCanonicalizer.setWriter(paramOutputStream);
/* 281 */       String str = getInclusiveNamespaces();
/*     */ 
/* 283 */       if (str == null)
/* 284 */         localCanonicalizer.canonicalizeSubtree(this._constructionElement);
/*     */       else
/* 286 */         localCanonicalizer.canonicalizeSubtree(this._constructionElement, str);
/*     */     } else {
/*     */       try {
/* 289 */         paramOutputStream.write(this._c14nizedBytes);
/*     */       } catch (IOException localIOException) {
/* 291 */         throw new RuntimeException("" + localIOException);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getCanonicalizationMethodURI()
/*     */   {
/* 304 */     return this.c14nMethod.getAttributeNS(null, "Algorithm");
/*     */   }
/*     */ 
/*     */   public String getSignatureMethodURI()
/*     */   {
/* 314 */     Element localElement = getSignatureMethodElement();
/*     */ 
/* 316 */     if (localElement != null) {
/* 317 */       return localElement.getAttributeNS(null, "Algorithm");
/*     */     }
/*     */ 
/* 320 */     return null;
/*     */   }
/*     */ 
/*     */   public Element getSignatureMethodElement()
/*     */   {
/* 329 */     return this.signatureMethod;
/*     */   }
/*     */ 
/*     */   public SecretKey createSecretKey(byte[] paramArrayOfByte)
/*     */   {
/* 342 */     return new SecretKeySpec(paramArrayOfByte, this._signatureAlgorithm.getJCEAlgorithmString());
/*     */   }
/*     */ 
/*     */   protected SignatureAlgorithm getSignatureAlgorithm()
/*     */   {
/* 348 */     return this._signatureAlgorithm;
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 356 */     return "SignedInfo";
/*     */   }
/*     */ 
/*     */   public String getInclusiveNamespaces()
/*     */   {
/* 363 */     String str1 = this.c14nMethod.getAttributeNS(null, "Algorithm");
/* 364 */     if ((!str1.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) && (!str1.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")))
/*     */     {
/* 366 */       return null;
/*     */     }
/*     */ 
/* 369 */     Element localElement = XMLUtils.getNextElement(this.c14nMethod.getFirstChild());
/*     */ 
/* 372 */     if (localElement != null)
/*     */     {
/*     */       try
/*     */       {
/* 376 */         return new InclusiveNamespaces(localElement, "http://www.w3.org/2001/10/xml-exc-c14n#").getInclusiveNamespaces();
/*     */       }
/*     */       catch (XMLSecurityException localXMLSecurityException)
/*     */       {
/* 382 */         return null;
/*     */       }
/*     */     }
/* 385 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.SignedInfo
 * JD-Core Version:    0.6.2
 */