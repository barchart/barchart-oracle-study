/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.utils.Base64;
/*     */ import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.security.Provider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.crypto.Data;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dsig.CanonicalizationMethod;
/*     */ import javax.xml.crypto.dsig.Reference;
/*     */ import javax.xml.crypto.dsig.SignatureMethod;
/*     */ import javax.xml.crypto.dsig.SignedInfo;
/*     */ import javax.xml.crypto.dsig.TransformException;
/*     */ import javax.xml.crypto.dsig.XMLSignatureException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public final class DOMSignedInfo extends DOMStructure
/*     */   implements SignedInfo
/*     */ {
/*  58 */   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
/*     */   private List references;
/*     */   private CanonicalizationMethod canonicalizationMethod;
/*     */   private SignatureMethod signatureMethod;
/*     */   private String id;
/*     */   private Document ownerDoc;
/*     */   private Element localSiElem;
/*     */   private InputStream canonData;
/*     */ 
/*     */   public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List paramList)
/*     */   {
/*  83 */     if ((paramCanonicalizationMethod == null) || (paramSignatureMethod == null) || (paramList == null)) {
/*  84 */       throw new NullPointerException();
/*     */     }
/*  86 */     this.canonicalizationMethod = paramCanonicalizationMethod;
/*  87 */     this.signatureMethod = paramSignatureMethod;
/*  88 */     this.references = Collections.unmodifiableList(new ArrayList(paramList));
/*     */ 
/*  90 */     if (this.references.isEmpty()) {
/*  91 */       throw new IllegalArgumentException("list of references must contain at least one entry");
/*     */     }
/*     */ 
/*  94 */     int i = 0; for (int j = this.references.size(); i < j; i++) {
/*  95 */       Object localObject = this.references.get(i);
/*  96 */       if (!(localObject instanceof Reference))
/*  97 */         throw new ClassCastException("list of references contains an illegal type");
/*     */     }
/*     */   }
/*     */ 
/*     */   public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List paramList, String paramString)
/*     */   {
/* 120 */     this(paramCanonicalizationMethod, paramSignatureMethod, paramList);
/* 121 */     this.id = paramString;
/*     */   }
/*     */ 
/*     */   public DOMSignedInfo(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/* 131 */     this.localSiElem = paramElement;
/* 132 */     this.ownerDoc = paramElement.getOwnerDocument();
/*     */ 
/* 135 */     this.id = DOMUtils.getAttributeValue(paramElement, "Id");
/*     */ 
/* 138 */     Element localElement1 = DOMUtils.getFirstChildElement(paramElement);
/* 139 */     this.canonicalizationMethod = new DOMCanonicalizationMethod(localElement1, paramXMLCryptoContext, paramProvider);
/*     */ 
/* 143 */     Element localElement2 = DOMUtils.getNextSiblingElement(localElement1);
/* 144 */     this.signatureMethod = DOMSignatureMethod.unmarshal(localElement2);
/*     */ 
/* 147 */     ArrayList localArrayList = new ArrayList(5);
/* 148 */     Element localElement3 = DOMUtils.getNextSiblingElement(localElement2);
/* 149 */     while (localElement3 != null) {
/* 150 */       localArrayList.add(new DOMReference(localElement3, paramXMLCryptoContext, paramProvider));
/* 151 */       localElement3 = DOMUtils.getNextSiblingElement(localElement3);
/*     */     }
/* 153 */     this.references = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public CanonicalizationMethod getCanonicalizationMethod() {
/* 157 */     return this.canonicalizationMethod;
/*     */   }
/*     */ 
/*     */   public SignatureMethod getSignatureMethod() {
/* 161 */     return this.signatureMethod;
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 165 */     return this.id;
/*     */   }
/*     */ 
/*     */   public List getReferences() {
/* 169 */     return this.references;
/*     */   }
/*     */ 
/*     */   public InputStream getCanonicalizedData() {
/* 173 */     return this.canonData;
/*     */   }
/*     */ 
/*     */   public void canonicalize(XMLCryptoContext paramXMLCryptoContext, ByteArrayOutputStream paramByteArrayOutputStream)
/*     */     throws XMLSignatureException
/*     */   {
/* 179 */     if (paramXMLCryptoContext == null) {
/* 180 */       throw new NullPointerException("context cannot be null");
/*     */     }
/*     */ 
/* 183 */     UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(paramByteArrayOutputStream);
/*     */     try {
/* 185 */       localUnsyncBufferedOutputStream.close();
/*     */     }
/*     */     catch (IOException localIOException1)
/*     */     {
/*     */     }
/* 190 */     DOMSubTreeData localDOMSubTreeData = new DOMSubTreeData(this.localSiElem, true);
/*     */     try
/*     */     {
/* 193 */       Data localData = ((DOMCanonicalizationMethod)this.canonicalizationMethod).canonicalize(localDOMSubTreeData, paramXMLCryptoContext, localUnsyncBufferedOutputStream);
/*     */     }
/*     */     catch (TransformException localTransformException) {
/* 196 */       throw new XMLSignatureException(localTransformException);
/*     */     }
/*     */ 
/* 199 */     byte[] arrayOfByte = paramByteArrayOutputStream.toByteArray();
/*     */ 
/* 202 */     if (log.isLoggable(Level.FINE)) {
/* 203 */       InputStreamReader localInputStreamReader = new InputStreamReader(new ByteArrayInputStream(arrayOfByte));
/*     */ 
/* 205 */       char[] arrayOfChar = new char[arrayOfByte.length];
/*     */       try {
/* 207 */         localInputStreamReader.read(arrayOfChar);
/* 208 */         log.log(Level.FINE, "Canonicalized SignedInfo:\n" + new String(arrayOfChar));
/*     */       }
/*     */       catch (IOException localIOException2) {
/* 211 */         log.log(Level.FINE, "IOException reading SignedInfo bytes");
/*     */       }
/* 213 */       log.log(Level.FINE, "Data to be signed/verified:" + Base64.encode(arrayOfByte));
/*     */     }
/*     */ 
/* 217 */     this.canonData = new ByteArrayInputStream(arrayOfByte);
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 222 */     this.ownerDoc = DOMUtils.getOwnerDocument(paramNode);
/*     */ 
/* 224 */     Element localElement = DOMUtils.createElement(this.ownerDoc, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 228 */     DOMCanonicalizationMethod localDOMCanonicalizationMethod = (DOMCanonicalizationMethod)this.canonicalizationMethod;
/*     */ 
/* 230 */     localDOMCanonicalizationMethod.marshal(localElement, paramString, paramDOMCryptoContext);
/*     */ 
/* 233 */     ((DOMSignatureMethod)this.signatureMethod).marshal(localElement, paramString, paramDOMCryptoContext);
/*     */ 
/* 237 */     int i = 0; for (int j = this.references.size(); i < j; i++) {
/* 238 */       DOMReference localDOMReference = (DOMReference)this.references.get(i);
/* 239 */       localDOMReference.marshal(localElement, paramString, paramDOMCryptoContext);
/*     */     }
/*     */ 
/* 243 */     DOMUtils.setAttributeID(localElement, "Id", this.id);
/*     */ 
/* 245 */     paramNode.appendChild(localElement);
/* 246 */     this.localSiElem = localElement;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 250 */     if (this == paramObject) {
/* 251 */       return true;
/*     */     }
/*     */ 
/* 254 */     if (!(paramObject instanceof SignedInfo)) {
/* 255 */       return false;
/*     */     }
/* 257 */     SignedInfo localSignedInfo = (SignedInfo)paramObject;
/*     */ 
/* 259 */     boolean bool = this.id == null ? false : localSignedInfo.getId() == null ? true : this.id.equals(localSignedInfo.getId());
/*     */ 
/* 262 */     return (this.canonicalizationMethod.equals(localSignedInfo.getCanonicalizationMethod())) && (this.signatureMethod.equals(localSignedInfo.getSignatureMethod())) && (this.references.equals(localSignedInfo.getReferences())) && (bool);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMSignedInfo
 * JD-Core Version:    0.6.2
 */