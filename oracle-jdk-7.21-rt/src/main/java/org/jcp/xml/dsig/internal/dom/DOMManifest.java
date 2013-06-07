/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import java.security.Provider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dsig.Manifest;
/*     */ import javax.xml.crypto.dsig.Reference;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public final class DOMManifest extends DOMStructure
/*     */   implements Manifest
/*     */ {
/*     */   private final List references;
/*     */   private final String id;
/*     */ 
/*     */   public DOMManifest(List paramList, String paramString)
/*     */   {
/*  63 */     if (paramList == null) {
/*  64 */       throw new NullPointerException("references cannot be null");
/*     */     }
/*  66 */     ArrayList localArrayList = new ArrayList(paramList);
/*  67 */     if (localArrayList.isEmpty()) {
/*  68 */       throw new IllegalArgumentException("list of references must contain at least one entry");
/*     */     }
/*     */ 
/*  71 */     int i = 0; for (int j = localArrayList.size(); i < j; i++) {
/*  72 */       if (!(localArrayList.get(i) instanceof Reference)) {
/*  73 */         throw new ClassCastException("references[" + i + "] is not a valid type");
/*     */       }
/*     */     }
/*     */ 
/*  77 */     this.references = Collections.unmodifiableList(localArrayList);
/*  78 */     this.id = paramString;
/*     */   }
/*     */ 
/*     */   public DOMManifest(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/*  88 */     this.id = DOMUtils.getAttributeValue(paramElement, "Id");
/*  89 */     Element localElement = DOMUtils.getFirstChildElement(paramElement);
/*  90 */     ArrayList localArrayList = new ArrayList();
/*  91 */     while (localElement != null) {
/*  92 */       localArrayList.add(new DOMReference(localElement, paramXMLCryptoContext, paramProvider));
/*  93 */       localElement = DOMUtils.getNextSiblingElement(localElement);
/*     */     }
/*  95 */     this.references = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */ 
/*     */   public String getId() {
/*  99 */     return this.id;
/*     */   }
/*     */ 
/*     */   public List getReferences() {
/* 103 */     return this.references;
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 108 */     Document localDocument = DOMUtils.getOwnerDocument(paramNode);
/*     */ 
/* 110 */     Element localElement = DOMUtils.createElement(localDocument, "Manifest", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 113 */     DOMUtils.setAttributeID(localElement, "Id", this.id);
/*     */ 
/* 116 */     int i = 0; for (int j = this.references.size(); i < j; i++) {
/* 117 */       DOMReference localDOMReference = (DOMReference)this.references.get(i);
/* 118 */       localDOMReference.marshal(localElement, paramString, paramDOMCryptoContext);
/*     */     }
/* 120 */     paramNode.appendChild(localElement);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 124 */     if (this == paramObject) {
/* 125 */       return true;
/*     */     }
/*     */ 
/* 128 */     if (!(paramObject instanceof Manifest)) {
/* 129 */       return false;
/*     */     }
/* 131 */     Manifest localManifest = (Manifest)paramObject;
/*     */ 
/* 133 */     boolean bool = this.id == null ? false : localManifest.getId() == null ? true : this.id.equals(localManifest.getId());
/*     */ 
/* 136 */     return (bool) && (this.references.equals(localManifest.getReferences()));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMManifest
 * JD-Core Version:    0.6.2
 */