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
/*     */ import javax.xml.crypto.dsig.keyinfo.KeyInfo;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public final class DOMKeyInfo extends DOMStructure
/*     */   implements KeyInfo
/*     */ {
/*     */   private final String id;
/*     */   private final List keyInfoTypes;
/*     */ 
/*     */   public DOMKeyInfo(List paramList, String paramString)
/*     */   {
/*  65 */     if (paramList == null) {
/*  66 */       throw new NullPointerException("content cannot be null");
/*     */     }
/*  68 */     ArrayList localArrayList = new ArrayList(paramList);
/*  69 */     if (localArrayList.isEmpty()) {
/*  70 */       throw new IllegalArgumentException("content cannot be empty");
/*     */     }
/*  72 */     int i = 0; for (int j = localArrayList.size(); i < j; i++) {
/*  73 */       if (!(localArrayList.get(i) instanceof XMLStructure)) {
/*  74 */         throw new ClassCastException("content[" + i + "] is not a valid KeyInfo type");
/*     */       }
/*     */     }
/*     */ 
/*  78 */     this.keyInfoTypes = Collections.unmodifiableList(localArrayList);
/*  79 */     this.id = paramString;
/*     */   }
/*     */ 
/*     */   public DOMKeyInfo(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/*  90 */     this.id = DOMUtils.getAttributeValue(paramElement, "Id");
/*     */ 
/*  93 */     NodeList localNodeList = paramElement.getChildNodes();
/*  94 */     int i = localNodeList.getLength();
/*  95 */     if (i < 1) {
/*  96 */       throw new MarshalException("KeyInfo must contain at least one type");
/*     */     }
/*     */ 
/*  99 */     ArrayList localArrayList = new ArrayList(i);
/* 100 */     for (int j = 0; j < i; j++) {
/* 101 */       Node localNode = localNodeList.item(j);
/*     */ 
/* 103 */       if (localNode.getNodeType() == 1)
/*     */       {
/* 106 */         Element localElement = (Element)localNode;
/* 107 */         String str = localElement.getLocalName();
/* 108 */         if (str.equals("X509Data"))
/* 109 */           localArrayList.add(new DOMX509Data(localElement));
/* 110 */         else if (str.equals("KeyName"))
/* 111 */           localArrayList.add(new DOMKeyName(localElement));
/* 112 */         else if (str.equals("KeyValue"))
/* 113 */           localArrayList.add(new DOMKeyValue(localElement));
/* 114 */         else if (str.equals("RetrievalMethod")) {
/* 115 */           localArrayList.add(new DOMRetrievalMethod(localElement, paramXMLCryptoContext, paramProvider));
/*     */         }
/* 117 */         else if (str.equals("PGPData"))
/* 118 */           localArrayList.add(new DOMPGPData(localElement));
/*     */         else
/* 120 */           localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localElement));
/*     */       }
/*     */     }
/* 123 */     this.keyInfoTypes = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 127 */     return this.id;
/*     */   }
/*     */ 
/*     */   public List getContent() {
/* 131 */     return this.keyInfoTypes;
/*     */   }
/*     */ 
/*     */   public void marshal(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws MarshalException
/*     */   {
/* 136 */     if (paramXMLStructure == null) {
/* 137 */       throw new NullPointerException("parent is null");
/*     */     }
/*     */ 
/* 140 */     Node localNode = ((javax.xml.crypto.dom.DOMStructure)paramXMLStructure).getNode();
/* 141 */     String str = DOMUtils.getSignaturePrefix(paramXMLCryptoContext);
/* 142 */     Element localElement = DOMUtils.createElement(DOMUtils.getOwnerDocument(localNode), "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", str);
/*     */ 
/* 145 */     if ((str == null) || (str.length() == 0)) {
/* 146 */       localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */     else {
/* 149 */       localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */ 
/* 153 */     marshal(localNode, localElement, null, str, (DOMCryptoContext)paramXMLCryptoContext);
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 158 */     marshal(paramNode, null, paramString, paramDOMCryptoContext);
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode1, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 163 */     Document localDocument = DOMUtils.getOwnerDocument(paramNode1);
/*     */ 
/* 165 */     Element localElement = DOMUtils.createElement(localDocument, "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 167 */     marshal(paramNode1, localElement, paramNode2, paramString, paramDOMCryptoContext);
/*     */   }
/*     */ 
/*     */   private void marshal(Node paramNode1, Element paramElement, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext)
/*     */     throws MarshalException
/*     */   {
/* 173 */     int i = 0; for (int j = this.keyInfoTypes.size(); i < j; i++) {
/* 174 */       XMLStructure localXMLStructure = (XMLStructure)this.keyInfoTypes.get(i);
/* 175 */       if ((localXMLStructure instanceof DOMStructure))
/* 176 */         ((DOMStructure)localXMLStructure).marshal(paramElement, paramString, paramDOMCryptoContext);
/*     */       else {
/* 178 */         DOMUtils.appendChild(paramElement, ((javax.xml.crypto.dom.DOMStructure)localXMLStructure).getNode());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 184 */     DOMUtils.setAttributeID(paramElement, "Id", this.id);
/*     */ 
/* 186 */     paramNode1.insertBefore(paramElement, paramNode2);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 190 */     if (this == paramObject) {
/* 191 */       return true;
/*     */     }
/*     */ 
/* 194 */     if (!(paramObject instanceof KeyInfo)) {
/* 195 */       return false;
/*     */     }
/* 197 */     KeyInfo localKeyInfo = (KeyInfo)paramObject;
/*     */ 
/* 199 */     boolean bool = this.id == null ? false : localKeyInfo.getId() == null ? true : this.id.equals(localKeyInfo.getId());
/*     */ 
/* 202 */     return (this.keyInfoTypes.equals(localKeyInfo.getContent())) && (bool);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMKeyInfo
 * JD-Core Version:    0.6.2
 */