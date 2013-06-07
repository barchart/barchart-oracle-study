/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.security.Provider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.crypto.Data;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.URIDereferencer;
/*     */ import javax.xml.crypto.URIReferenceException;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.XMLStructure;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dom.DOMURIReference;
/*     */ import javax.xml.crypto.dsig.Transform;
/*     */ import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public final class DOMRetrievalMethod extends DOMStructure
/*     */   implements RetrievalMethod, DOMURIReference
/*     */ {
/*     */   private final List transforms;
/*     */   private String uri;
/*     */   private String type;
/*     */   private Attr here;
/*     */ 
/*     */   public DOMRetrievalMethod(String paramString1, String paramString2, List paramList)
/*     */   {
/*  86 */     if (paramString1 == null) {
/*  87 */       throw new NullPointerException("uri cannot be null");
/*     */     }
/*  89 */     if ((paramList == null) || (paramList.isEmpty())) {
/*  90 */       this.transforms = Collections.EMPTY_LIST;
/*     */     } else {
/*  92 */       ArrayList localArrayList = new ArrayList(paramList);
/*  93 */       int i = 0; for (int j = localArrayList.size(); i < j; i++) {
/*  94 */         if (!(localArrayList.get(i) instanceof Transform)) {
/*  95 */           throw new ClassCastException("transforms[" + i + "] is not a valid type");
/*     */         }
/*     */       }
/*     */ 
/*  99 */       this.transforms = Collections.unmodifiableList(localArrayList);
/*     */     }
/* 101 */     this.uri = paramString1;
/* 102 */     if ((paramString1 != null) && (!paramString1.equals(""))) {
/*     */       try {
/* 104 */         new URI(paramString1);
/*     */       } catch (URISyntaxException localURISyntaxException) {
/* 106 */         throw new IllegalArgumentException(localURISyntaxException.getMessage());
/*     */       }
/*     */     }
/*     */ 
/* 110 */     this.type = paramString2;
/*     */   }
/*     */ 
/*     */   public DOMRetrievalMethod(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/* 121 */     this.uri = DOMUtils.getAttributeValue(paramElement, "URI");
/* 122 */     this.type = DOMUtils.getAttributeValue(paramElement, "Type");
/*     */ 
/* 125 */     this.here = paramElement.getAttributeNodeNS(null, "URI");
/*     */ 
/* 128 */     ArrayList localArrayList = new ArrayList();
/* 129 */     Element localElement1 = DOMUtils.getFirstChildElement(paramElement);
/* 130 */     if (localElement1 != null) {
/* 131 */       Element localElement2 = DOMUtils.getFirstChildElement(localElement1);
/*     */ 
/* 133 */       while (localElement2 != null) {
/* 134 */         localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
/*     */ 
/* 136 */         localElement2 = DOMUtils.getNextSiblingElement(localElement2);
/*     */       }
/*     */     }
/* 139 */     if (localArrayList.isEmpty())
/* 140 */       this.transforms = Collections.EMPTY_LIST;
/*     */     else
/* 142 */       this.transforms = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public String getURI()
/*     */   {
/* 147 */     return this.uri;
/*     */   }
/*     */ 
/*     */   public String getType() {
/* 151 */     return this.type;
/*     */   }
/*     */ 
/*     */   public List getTransforms() {
/* 155 */     return this.transforms;
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 160 */     Document localDocument = DOMUtils.getOwnerDocument(paramNode);
/*     */ 
/* 162 */     Element localElement1 = DOMUtils.createElement(localDocument, "RetrievalMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 166 */     DOMUtils.setAttribute(localElement1, "URI", this.uri);
/* 167 */     DOMUtils.setAttribute(localElement1, "Type", this.type);
/*     */ 
/* 170 */     if (!this.transforms.isEmpty()) {
/* 171 */       Element localElement2 = DOMUtils.createElement(localDocument, "Transforms", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 173 */       localElement1.appendChild(localElement2);
/* 174 */       int i = 0; for (int j = this.transforms.size(); i < j; i++) {
/* 175 */         ((DOMTransform)this.transforms.get(i)).marshal(localElement2, paramString, paramDOMCryptoContext);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 180 */     paramNode.appendChild(localElement1);
/*     */ 
/* 183 */     this.here = localElement1.getAttributeNodeNS(null, "URI");
/*     */   }
/*     */ 
/*     */   public Node getHere() {
/* 187 */     return this.here;
/*     */   }
/*     */ 
/*     */   public Data dereference(XMLCryptoContext paramXMLCryptoContext)
/*     */     throws URIReferenceException
/*     */   {
/* 193 */     if (paramXMLCryptoContext == null) {
/* 194 */       throw new NullPointerException("context cannot be null");
/*     */     }
/*     */ 
/* 201 */     URIDereferencer localURIDereferencer = paramXMLCryptoContext.getURIDereferencer();
/* 202 */     if (localURIDereferencer == null) {
/* 203 */       localURIDereferencer = DOMURIDereferencer.INSTANCE;
/*     */     }
/*     */ 
/* 206 */     Data localData = localURIDereferencer.dereference(this, paramXMLCryptoContext);
/*     */     try
/*     */     {
/* 210 */       int i = 0; for (int j = this.transforms.size(); i < j; i++) {
/* 211 */         Transform localTransform = (Transform)this.transforms.get(i);
/* 212 */         localData = ((DOMTransform)localTransform).transform(localData, paramXMLCryptoContext);
/*     */       }
/*     */     } catch (Exception localException) {
/* 215 */       throw new URIReferenceException(localException);
/*     */     }
/* 217 */     return localData;
/*     */   }
/*     */ 
/*     */   public XMLStructure dereferenceAsXMLStructure(XMLCryptoContext paramXMLCryptoContext) throws URIReferenceException
/*     */   {
/*     */     try
/*     */     {
/* 224 */       ApacheData localApacheData = (ApacheData)dereference(paramXMLCryptoContext);
/* 225 */       DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/* 226 */       localDocumentBuilderFactory.setNamespaceAware(true);
/* 227 */       DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/* 228 */       Document localDocument = localDocumentBuilder.parse(new ByteArrayInputStream(localApacheData.getXMLSignatureInput().getBytes()));
/*     */ 
/* 230 */       Element localElement = localDocument.getDocumentElement();
/* 231 */       if (localElement.getLocalName().equals("X509Data")) {
/* 232 */         return new DOMX509Data(localElement);
/*     */       }
/* 234 */       return null;
/*     */     }
/*     */     catch (Exception localException) {
/* 237 */       throw new URIReferenceException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 242 */     if (this == paramObject) {
/* 243 */       return true;
/*     */     }
/* 245 */     if (!(paramObject instanceof DOMURIReference)) {
/* 246 */       return false;
/*     */     }
/* 248 */     RetrievalMethod localRetrievalMethod = (DOMURIReference)paramObject;
/*     */ 
/* 250 */     boolean bool = this.type == null ? false : localRetrievalMethod.getType() == null ? true : this.type.equals(localRetrievalMethod.getType());
/*     */ 
/* 253 */     return (this.uri.equals(localRetrievalMethod.getURI())) && (this.transforms.equals(localRetrievalMethod.getTransforms())) && (bool);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMRetrievalMethod
 * JD-Core Version:    0.6.2
 */