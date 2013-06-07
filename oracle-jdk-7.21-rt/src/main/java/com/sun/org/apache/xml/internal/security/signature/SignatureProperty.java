/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class SignatureProperty extends SignatureElementProxy
/*     */ {
/*     */   public SignatureProperty(Document paramDocument, String paramString)
/*     */   {
/*  47 */     this(paramDocument, paramString, null);
/*     */   }
/*     */ 
/*     */   public SignatureProperty(Document paramDocument, String paramString1, String paramString2)
/*     */   {
/*  59 */     super(paramDocument);
/*     */ 
/*  61 */     setTarget(paramString1);
/*  62 */     setId(paramString2);
/*     */   }
/*     */ 
/*     */   public SignatureProperty(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/*  73 */     super(paramElement, paramString);
/*     */   }
/*     */ 
/*     */   public void setId(String paramString)
/*     */   {
/*  83 */     if (paramString != null) {
/*  84 */       this._constructionElement.setAttributeNS(null, "Id", paramString);
/*  85 */       IdResolver.registerElementById(this._constructionElement, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  95 */     return this._constructionElement.getAttributeNS(null, "Id");
/*     */   }
/*     */ 
/*     */   public void setTarget(String paramString)
/*     */   {
/* 105 */     if (paramString != null)
/* 106 */       this._constructionElement.setAttributeNS(null, "Target", paramString);
/*     */   }
/*     */ 
/*     */   public String getTarget()
/*     */   {
/* 116 */     return this._constructionElement.getAttributeNS(null, "Target");
/*     */   }
/*     */ 
/*     */   public Node appendChild(Node paramNode)
/*     */   {
/* 126 */     return this._constructionElement.appendChild(paramNode);
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 131 */     return "SignatureProperty";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.SignatureProperty
 * JD-Core Version:    0.6.2
 */