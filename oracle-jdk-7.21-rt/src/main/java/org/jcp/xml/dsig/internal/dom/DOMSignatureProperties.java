/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dsig.SignatureProperties;
/*     */ import javax.xml.crypto.dsig.SignatureProperty;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public final class DOMSignatureProperties extends DOMStructure
/*     */   implements SignatureProperties
/*     */ {
/*     */   private final String id;
/*     */   private final List properties;
/*     */ 
/*     */   public DOMSignatureProperties(List paramList, String paramString)
/*     */   {
/*  64 */     if (paramList == null)
/*  65 */       throw new NullPointerException("properties cannot be null");
/*  66 */     if (paramList.isEmpty()) {
/*  67 */       throw new IllegalArgumentException("properties cannot be empty");
/*     */     }
/*  69 */     ArrayList localArrayList = new ArrayList(paramList);
/*  70 */     int i = 0; for (int j = localArrayList.size(); i < j; i++) {
/*  71 */       if (!(localArrayList.get(i) instanceof SignatureProperty)) {
/*  72 */         throw new ClassCastException("properties[" + i + "] is not a valid type");
/*     */       }
/*     */     }
/*     */ 
/*  76 */     this.properties = Collections.unmodifiableList(localArrayList);
/*     */ 
/*  78 */     this.id = paramString;
/*     */   }
/*     */ 
/*     */   public DOMSignatureProperties(Element paramElement)
/*     */     throws MarshalException
/*     */   {
/*  89 */     this.id = DOMUtils.getAttributeValue(paramElement, "Id");
/*     */ 
/*  91 */     NodeList localNodeList = paramElement.getChildNodes();
/*  92 */     int i = localNodeList.getLength();
/*  93 */     ArrayList localArrayList = new ArrayList(i);
/*  94 */     for (int j = 0; j < i; j++) {
/*  95 */       Node localNode = localNodeList.item(j);
/*  96 */       if (localNode.getNodeType() == 1) {
/*  97 */         localArrayList.add(new DOMSignatureProperty((Element)localNode));
/*     */       }
/*     */     }
/* 100 */     if (localArrayList.isEmpty()) {
/* 101 */       throw new MarshalException("properties cannot be empty");
/*     */     }
/* 103 */     this.properties = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public List getProperties()
/*     */   {
/* 108 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 112 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 117 */     Document localDocument = DOMUtils.getOwnerDocument(paramNode);
/*     */ 
/* 119 */     Element localElement = DOMUtils.createElement(localDocument, "SignatureProperties", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 123 */     DOMUtils.setAttributeID(localElement, "Id", this.id);
/*     */ 
/* 126 */     int i = 0; for (int j = this.properties.size(); i < j; i++) {
/* 127 */       DOMSignatureProperty localDOMSignatureProperty = (DOMSignatureProperty)this.properties.get(i);
/*     */ 
/* 129 */       localDOMSignatureProperty.marshal(localElement, paramString, paramDOMCryptoContext);
/*     */     }
/*     */ 
/* 132 */     paramNode.appendChild(localElement);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 136 */     if (this == paramObject) {
/* 137 */       return true;
/*     */     }
/*     */ 
/* 140 */     if (!(paramObject instanceof SignatureProperties)) {
/* 141 */       return false;
/*     */     }
/* 143 */     SignatureProperties localSignatureProperties = (SignatureProperties)paramObject;
/*     */ 
/* 145 */     boolean bool = this.id == null ? false : localSignatureProperties.getId() == null ? true : this.id.equals(localSignatureProperties.getId());
/*     */ 
/* 148 */     return (this.properties.equals(localSignatureProperties.getProperties())) && (bool);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMSignatureProperties
 * JD-Core Version:    0.6.2
 */