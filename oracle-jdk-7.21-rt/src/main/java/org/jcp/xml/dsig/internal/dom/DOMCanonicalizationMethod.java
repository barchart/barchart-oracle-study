/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.Provider;
/*     */ import javax.xml.crypto.Data;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.dsig.CanonicalizationMethod;
/*     */ import javax.xml.crypto.dsig.TransformException;
/*     */ import javax.xml.crypto.dsig.TransformService;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class DOMCanonicalizationMethod extends DOMTransform
/*     */   implements CanonicalizationMethod
/*     */ {
/*     */   public DOMCanonicalizationMethod(TransformService paramTransformService)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/*  53 */     super(paramTransformService);
/*     */   }
/*     */ 
/*     */   public DOMCanonicalizationMethod(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/*  65 */     super(paramElement, paramXMLCryptoContext, paramProvider);
/*     */   }
/*     */ 
/*     */   public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext)
/*     */     throws TransformException
/*     */   {
/*  83 */     return transform(paramData, paramXMLCryptoContext);
/*     */   }
/*     */ 
/*     */   public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream) throws TransformException
/*     */   {
/*  88 */     return transform(paramData, paramXMLCryptoContext, paramOutputStream);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/*  92 */     if (this == paramObject) {
/*  93 */       return true;
/*     */     }
/*     */ 
/*  96 */     if (!(paramObject instanceof CanonicalizationMethod)) {
/*  97 */       return false;
/*     */     }
/*  99 */     CanonicalizationMethod localCanonicalizationMethod = (CanonicalizationMethod)paramObject;
/*     */ 
/* 101 */     return (getAlgorithm().equals(localCanonicalizationMethod.getAlgorithm())) && (DOMUtils.paramsEqual(getParameterSpec(), localCanonicalizationMethod.getParameterSpec()));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMCanonicalizationMethod
 * JD-Core Version:    0.6.2
 */