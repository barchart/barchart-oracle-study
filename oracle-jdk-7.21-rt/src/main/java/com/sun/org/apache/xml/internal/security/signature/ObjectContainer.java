/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class ObjectContainer extends SignatureElementProxy
/*     */ {
/*     */   public ObjectContainer(Document paramDocument)
/*     */   {
/*  48 */     super(paramDocument);
/*     */   }
/*     */ 
/*     */   public ObjectContainer(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/*  61 */     super(paramElement, paramString);
/*     */   }
/*     */ 
/*     */   public void setId(String paramString)
/*     */   {
/*  71 */     if (paramString != null) {
/*  72 */       this._constructionElement.setAttributeNS(null, "Id", paramString);
/*  73 */       IdResolver.registerElementById(this._constructionElement, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  83 */     return this._constructionElement.getAttributeNS(null, "Id");
/*     */   }
/*     */ 
/*     */   public void setMimeType(String paramString)
/*     */   {
/*  93 */     if (paramString != null)
/*  94 */       this._constructionElement.setAttributeNS(null, "MimeType", paramString);
/*     */   }
/*     */ 
/*     */   public String getMimeType()
/*     */   {
/* 105 */     return this._constructionElement.getAttributeNS(null, "MimeType");
/*     */   }
/*     */ 
/*     */   public void setEncoding(String paramString)
/*     */   {
/* 115 */     if (paramString != null)
/* 116 */       this._constructionElement.setAttributeNS(null, "Encoding", paramString);
/*     */   }
/*     */ 
/*     */   public String getEncoding()
/*     */   {
/* 127 */     return this._constructionElement.getAttributeNS(null, "Encoding");
/*     */   }
/*     */ 
/*     */   public Node appendChild(Node paramNode)
/*     */   {
/* 138 */     Node localNode = null;
/*     */ 
/* 140 */     localNode = this._constructionElement.appendChild(paramNode);
/*     */ 
/* 142 */     return localNode;
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 147 */     return "Object";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.ObjectContainer
 * JD-Core Version:    0.6.2
 */