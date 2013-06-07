/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class SignatureProperties extends SignatureElementProxy
/*     */ {
/*     */   public SignatureProperties(Document paramDocument)
/*     */   {
/*  50 */     super(paramDocument);
/*     */ 
/*  52 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public SignatureProperties(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/*  63 */     super(paramElement, paramString);
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  73 */     Element[] arrayOfElement = XMLUtils.selectDsNodes(this._constructionElement, "SignatureProperty");
/*     */ 
/*  78 */     return arrayOfElement.length;
/*     */   }
/*     */ 
/*     */   public SignatureProperty item(int paramInt)
/*     */     throws XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/*  91 */       Element localElement = XMLUtils.selectDsNode(this._constructionElement, "SignatureProperty", paramInt);
/*     */ 
/*  96 */       if (localElement == null) {
/*  97 */         return null;
/*     */       }
/*  99 */       return new SignatureProperty(localElement, this._baseURI);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 101 */       throw new XMLSignatureException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setId(String paramString)
/*     */   {
/* 112 */     if (paramString != null) {
/* 113 */       this._constructionElement.setAttributeNS(null, "Id", paramString);
/* 114 */       IdResolver.registerElementById(this._constructionElement, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 124 */     return this._constructionElement.getAttributeNS(null, "Id");
/*     */   }
/*     */ 
/*     */   public void addSignatureProperty(SignatureProperty paramSignatureProperty)
/*     */   {
/* 133 */     this._constructionElement.appendChild(paramSignatureProperty.getElement());
/* 134 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 139 */     return "SignatureProperties";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.SignatureProperties
 * JD-Core Version:    0.6.2
 */