/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.XMLStructure;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dsig.SignatureProperty;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public final class DOMSignatureProperty extends DOMStructure
/*     */   implements SignatureProperty
/*     */ {
/*     */   private final String id;
/*     */   private final String target;
/*     */   private final List content;
/*     */ 
/*     */   public DOMSignatureProperty(List paramList, String paramString1, String paramString2)
/*     */   {
/*  66 */     if (paramString1 == null)
/*  67 */       throw new NullPointerException("target cannot be null");
/*  68 */     if (paramList == null)
/*  69 */       throw new NullPointerException("content cannot be null");
/*  70 */     if (paramList.isEmpty()) {
/*  71 */       throw new IllegalArgumentException("content cannot be empty");
/*     */     }
/*  73 */     ArrayList localArrayList = new ArrayList(paramList);
/*  74 */     int i = 0; for (int j = localArrayList.size(); i < j; i++) {
/*  75 */       if (!(localArrayList.get(i) instanceof XMLStructure)) {
/*  76 */         throw new ClassCastException("content[" + i + "] is not a valid type");
/*     */       }
/*     */     }
/*     */ 
/*  80 */     this.content = Collections.unmodifiableList(localArrayList);
/*     */ 
/*  82 */     this.target = paramString1;
/*  83 */     this.id = paramString2;
/*     */   }
/*     */ 
/*     */   public DOMSignatureProperty(Element paramElement)
/*     */     throws MarshalException
/*     */   {
/*  93 */     this.target = DOMUtils.getAttributeValue(paramElement, "Target");
/*  94 */     if (this.target == null) {
/*  95 */       throw new MarshalException("target cannot be null");
/*     */     }
/*  97 */     this.id = DOMUtils.getAttributeValue(paramElement, "Id");
/*     */ 
/*  99 */     NodeList localNodeList = paramElement.getChildNodes();
/* 100 */     int i = localNodeList.getLength();
/* 101 */     ArrayList localArrayList = new ArrayList(i);
/* 102 */     for (int j = 0; j < i; j++) {
/* 103 */       localArrayList.add(new javax.xml.crypto.dom.DOMStructure(localNodeList.item(j)));
/*     */     }
/* 105 */     if (localArrayList.isEmpty()) {
/* 106 */       throw new MarshalException("content cannot be empty");
/*     */     }
/* 108 */     this.content = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public List getContent()
/*     */   {
/* 113 */     return this.content;
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 117 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getTarget() {
/* 121 */     return this.target;
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 126 */     Document localDocument = DOMUtils.getOwnerDocument(paramNode);
/*     */ 
/* 128 */     Element localElement = DOMUtils.createElement(localDocument, "SignatureProperty", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 132 */     DOMUtils.setAttributeID(localElement, "Id", this.id);
/* 133 */     DOMUtils.setAttribute(localElement, "Target", this.target);
/*     */ 
/* 136 */     int i = 0; for (int j = this.content.size(); i < j; i++) {
/* 137 */       javax.xml.crypto.dom.DOMStructure localDOMStructure = (javax.xml.crypto.dom.DOMStructure)this.content.get(i);
/*     */ 
/* 139 */       DOMUtils.appendChild(localElement, localDOMStructure.getNode());
/*     */     }
/*     */ 
/* 142 */     paramNode.appendChild(localElement);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 146 */     if (this == paramObject) {
/* 147 */       return true;
/*     */     }
/*     */ 
/* 150 */     if (!(paramObject instanceof SignatureProperty)) {
/* 151 */       return false;
/*     */     }
/* 153 */     SignatureProperty localSignatureProperty = (SignatureProperty)paramObject;
/*     */ 
/* 155 */     boolean bool = this.id == null ? false : localSignatureProperty.getId() == null ? true : this.id.equals(localSignatureProperty.getId());
/*     */ 
/* 158 */     return (equalsContent(localSignatureProperty.getContent())) && (this.target.equals(localSignatureProperty.getTarget())) && (bool);
/*     */   }
/*     */ 
/*     */   private boolean equalsContent(List paramList)
/*     */   {
/* 163 */     int i = paramList.size();
/* 164 */     if (this.content.size() != i) {
/* 165 */       return false;
/*     */     }
/* 167 */     for (int j = 0; j < i; j++) {
/* 168 */       XMLStructure localXMLStructure1 = (XMLStructure)paramList.get(j);
/* 169 */       XMLStructure localXMLStructure2 = (XMLStructure)this.content.get(j);
/* 170 */       if ((localXMLStructure1 instanceof javax.xml.crypto.dom.DOMStructure)) {
/* 171 */         if (!(localXMLStructure2 instanceof javax.xml.crypto.dom.DOMStructure)) {
/* 172 */           return false;
/*     */         }
/* 174 */         Node localNode1 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure1).getNode();
/*     */ 
/* 176 */         Node localNode2 = ((javax.xml.crypto.dom.DOMStructure)localXMLStructure2).getNode();
/*     */ 
/* 178 */         if (!DOMUtils.nodesEqual(localNode2, localNode1)) {
/* 179 */           return false;
/*     */         }
/*     */       }
/* 182 */       else if (!localXMLStructure2.equals(localXMLStructure1)) {
/* 183 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 188 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMSignatureProperty
 * JD-Core Version:    0.6.2
 */