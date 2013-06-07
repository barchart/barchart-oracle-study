/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import java.security.Provider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.XMLStructure;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dsig.XMLObject;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public final class DOMXMLObject extends DOMStructure
/*     */   implements XMLObject
/*     */ {
/*     */   private final String id;
/*     */   private final String mimeType;
/*     */   private final String encoding;
/*     */   private final List content;
/*     */ 
/*     */   public DOMXMLObject(List paramList, String paramString1, String paramString2, String paramString3)
/*     */   {
/*  67 */     if ((paramList == null) || (paramList.isEmpty())) {
/*  68 */       this.content = Collections.EMPTY_LIST;
/*     */     } else {
/*  70 */       ArrayList localArrayList = new ArrayList(paramList);
/*  71 */       int i = 0; for (int j = localArrayList.size(); i < j; i++) {
/*  72 */         if (!(localArrayList.get(i) instanceof XMLStructure)) {
/*  73 */           throw new ClassCastException("content[" + i + "] is not a valid type");
/*     */         }
/*     */       }
/*     */ 
/*  77 */       this.content = Collections.unmodifiableList(localArrayList);
/*     */     }
/*  79 */     this.id = paramString1;
/*  80 */     this.mimeType = paramString2;
/*  81 */     this.encoding = paramString3;
/*     */   }
/*     */ 
/*     */   public DOMXMLObject(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/*  93 */     this.encoding = DOMUtils.getAttributeValue(paramElement, "Encoding");
/*  94 */     this.id = DOMUtils.getAttributeValue(paramElement, "Id");
/*  95 */     this.mimeType = DOMUtils.getAttributeValue(paramElement, "MimeType");
/*     */ 
/*  97 */     NodeList localNodeList = paramElement.getChildNodes();
/*  98 */     int i = localNodeList.getLength();
/*  99 */     ArrayList localArrayList = new ArrayList(i);
/* 100 */     for (int j = 0; j < i; j++) {
/* 101 */       Node localNode = localNodeList.item(j);
/* 102 */       if (localNode.getNodeType() == 1) {
/* 103 */         Element localElement = (Element)localNode;
/* 104 */         String str = localElement.getLocalName();
/* 105 */         if (str.equals("Manifest")) {
/* 106 */           localArrayList.add(new DOMManifest(localElement, paramXMLCryptoContext, paramProvider));
/* 107 */           continue;
/* 108 */         }if (str.equals("SignatureProperties")) {
/* 109 */           localArrayList.add(new DOMSignatureProperties(localElement));
/* 110 */           continue;
/* 111 */         }if (str.equals("X509Data")) {
/* 112 */           localArrayList.add(new DOMX509Data(localElement));
/* 113 */           continue;
/*     */         }
/*     */       }
/*     */ 
/* 117 */       localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localNode));
/*     */     }
/* 119 */     if (localArrayList.isEmpty())
/* 120 */       this.content = Collections.EMPTY_LIST;
/*     */     else
/* 122 */       this.content = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public List getContent()
/*     */   {
/* 127 */     return this.content;
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 131 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getMimeType() {
/* 135 */     return this.mimeType;
/*     */   }
/*     */ 
/*     */   public String getEncoding() {
/* 139 */     return this.encoding;
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 144 */     Document localDocument = DOMUtils.getOwnerDocument(paramNode);
/*     */ 
/* 146 */     Element localElement = DOMUtils.createElement(localDocument, "Object", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 150 */     DOMUtils.setAttributeID(localElement, "Id", this.id);
/* 151 */     DOMUtils.setAttribute(localElement, "MimeType", this.mimeType);
/* 152 */     DOMUtils.setAttribute(localElement, "Encoding", this.encoding);
/*     */ 
/* 155 */     int i = 0; for (int j = this.content.size(); i < j; i++) {
/* 156 */       XMLStructure localXMLStructure = (XMLStructure)this.content.get(i);
/* 157 */       if ((localXMLStructure instanceof DOMStructure)) {
/* 158 */         ((DOMStructure)localXMLStructure).marshal(localElement, paramString, paramDOMCryptoContext);
/*     */       } else {
/* 160 */         javax.xml.crypto.dom.DOMStructure localDOMStructure = (javax.xml.crypto.dom.DOMStructure)localXMLStructure;
/*     */ 
/* 162 */         DOMUtils.appendChild(localElement, localDOMStructure.getNode());
/*     */       }
/*     */     }
/*     */ 
/* 166 */     paramNode.appendChild(localElement);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 170 */     if (this == paramObject) {
/* 171 */       return true;
/*     */     }
/*     */ 
/* 174 */     if (!(paramObject instanceof XMLObject)) {
/* 175 */       return false;
/*     */     }
/* 177 */     XMLObject localXMLObject = (XMLObject)paramObject;
/*     */ 
/* 179 */     boolean bool1 = this.id == null ? false : localXMLObject.getId() == null ? true : this.id.equals(localXMLObject.getId());
/*     */ 
/* 181 */     boolean bool2 = this.encoding == null ? false : localXMLObject.getEncoding() == null ? true : this.encoding.equals(localXMLObject.getEncoding());
/*     */ 
/* 183 */     boolean bool3 = this.mimeType == null ? false : localXMLObject.getMimeType() == null ? true : this.mimeType.equals(localXMLObject.getMimeType());
/*     */ 
/* 186 */     return (bool1) && (bool2) && (bool3) && (equalsContent(localXMLObject.getContent()));
/*     */   }
/*     */ 
/*     */   private boolean equalsContent(List paramList)
/*     */   {
/* 191 */     if (this.content.size() != paramList.size()) {
/* 192 */       return false;
/*     */     }
/* 194 */     int i = 0; for (int j = paramList.size(); i < j; i++) {
/* 195 */       XMLStructure localXMLStructure1 = (XMLStructure)paramList.get(i);
/* 196 */       XMLStructure localXMLStructure2 = (XMLStructure)this.content.get(i);
/* 197 */       if ((localXMLStructure1 instanceof javax.xml.crypto.dom.DOMStructure)) {
/* 198 */         if (!(localXMLStructure2 instanceof javax.xml.crypto.dom.DOMStructure)) {
/* 199 */           return false;
/*     */         }
/* 201 */         Node localNode1 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure1).getNode();
/*     */ 
/* 203 */         Node localNode2 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure2).getNode();
/*     */ 
/* 205 */         if (!DOMUtils.nodesEqual(localNode2, localNode1)) {
/* 206 */           return false;
/*     */         }
/*     */       }
/* 209 */       else if (!localXMLStructure2.equals(localXMLStructure1)) {
/* 210 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 215 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMXMLObject
 * JD-Core Version:    0.6.2
 */