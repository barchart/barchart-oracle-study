/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.Init;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transform;
/*     */ import java.io.OutputStream;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.crypto.Data;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.NodeSetData;
/*     */ import javax.xml.crypto.OctetStreamData;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.XMLStructure;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dom.DOMStructure;
/*     */ import javax.xml.crypto.dsig.TransformException;
/*     */ import javax.xml.crypto.dsig.TransformService;
/*     */ import javax.xml.crypto.dsig.spec.TransformParameterSpec;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public abstract class ApacheTransform extends TransformService
/*     */ {
/*  60 */   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
/*     */   private Transform apacheTransform;
/*     */   protected Document ownerDoc;
/*     */   protected Element transformElem;
/*     */   protected TransformParameterSpec params;
/*     */ 
/*     */   public final AlgorithmParameterSpec getParameterSpec()
/*     */   {
/*  67 */     return this.params;
/*     */   }
/*     */ 
/*     */   public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws InvalidAlgorithmParameterException
/*     */   {
/*  72 */     if ((paramXMLCryptoContext != null) && (!(paramXMLCryptoContext instanceof DOMCryptoContext))) {
/*  73 */       throw new ClassCastException("context must be of type DOMCryptoContext");
/*     */     }
/*     */ 
/*  76 */     this.transformElem = ((Element)((DOMStructure)paramXMLStructure).getNode());
/*     */ 
/*  78 */     this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
/*     */   }
/*     */ 
/*     */   public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws MarshalException
/*     */   {
/*  83 */     if ((paramXMLCryptoContext != null) && (!(paramXMLCryptoContext instanceof DOMCryptoContext))) {
/*  84 */       throw new ClassCastException("context must be of type DOMCryptoContext");
/*     */     }
/*     */ 
/*  87 */     this.transformElem = ((Element)((DOMStructure)paramXMLStructure).getNode());
/*     */ 
/*  89 */     this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
/*     */   }
/*     */ 
/*     */   public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext) throws TransformException
/*     */   {
/*  94 */     if (paramData == null) {
/*  95 */       throw new NullPointerException("data must not be null");
/*     */     }
/*  97 */     return transformIt(paramData, paramXMLCryptoContext, (OutputStream)null);
/*     */   }
/*     */ 
/*     */   public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream) throws TransformException
/*     */   {
/* 102 */     if (paramData == null) {
/* 103 */       throw new NullPointerException("data must not be null");
/*     */     }
/* 105 */     if (paramOutputStream == null) {
/* 106 */       throw new NullPointerException("output stream must not be null");
/*     */     }
/* 108 */     return transformIt(paramData, paramXMLCryptoContext, paramOutputStream);
/*     */   }
/*     */ 
/*     */   private Data transformIt(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream)
/*     */     throws TransformException
/*     */   {
/* 114 */     if (this.ownerDoc == null) {
/* 115 */       throw new TransformException("transform must be marshalled");
/*     */     }
/*     */ 
/* 118 */     if (this.apacheTransform == null)
/*     */       try {
/* 120 */         this.apacheTransform = Transform.getInstance(this.ownerDoc, getAlgorithm(), this.transformElem.getChildNodes());
/*     */ 
/* 122 */         this.apacheTransform.setElement(this.transformElem, paramXMLCryptoContext.getBaseURI());
/* 123 */         if (log.isLoggable(Level.FINE))
/* 124 */           log.log(Level.FINE, "Created transform for algorithm: " + getAlgorithm());
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/* 128 */         throw new TransformException("Couldn't find Transform for: " + getAlgorithm(), localException1);
/*     */       }
/*     */     XMLSignatureInput localXMLSignatureInput;
/* 134 */     if ((paramData instanceof ApacheData)) {
/* 135 */       if (log.isLoggable(Level.FINE)) {
/* 136 */         log.log(Level.FINE, "ApacheData = true");
/*     */       }
/* 138 */       localXMLSignatureInput = ((ApacheData)paramData).getXMLSignatureInput();
/* 139 */     } else if ((paramData instanceof NodeSetData)) {
/* 140 */       if (log.isLoggable(Level.FINE))
/* 141 */         log.log(Level.FINE, "isNodeSet() = true");
/*     */       Object localObject;
/* 143 */       if ((paramData instanceof DOMSubTreeData)) {
/* 144 */         if (log.isLoggable(Level.FINE)) {
/* 145 */           log.log(Level.FINE, "DOMSubTreeData = true");
/*     */         }
/* 147 */         localObject = (DOMSubTreeData)paramData;
/* 148 */         localXMLSignatureInput = new XMLSignatureInput(((DOMSubTreeData)localObject).getRoot());
/* 149 */         localXMLSignatureInput.setExcludeComments(((DOMSubTreeData)localObject).excludeComments());
/*     */       } else {
/* 151 */         localObject = Utils.toNodeSet(((NodeSetData)paramData).iterator());
/*     */ 
/* 153 */         localXMLSignatureInput = new XMLSignatureInput((Set)localObject);
/*     */       }
/*     */     } else {
/* 156 */       if (log.isLoggable(Level.FINE))
/* 157 */         log.log(Level.FINE, "isNodeSet() = false");
/*     */       try
/*     */       {
/* 160 */         localXMLSignatureInput = new XMLSignatureInput(((OctetStreamData)paramData).getOctetStream());
/*     */       }
/*     */       catch (Exception localException2) {
/* 163 */         throw new TransformException(localException2);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 168 */       if (paramOutputStream != null) {
/* 169 */         localXMLSignatureInput = this.apacheTransform.performTransform(localXMLSignatureInput, paramOutputStream);
/* 170 */         if ((!localXMLSignatureInput.isNodeSet()) && (!localXMLSignatureInput.isElement()))
/* 171 */           return null;
/*     */       }
/*     */       else {
/* 174 */         localXMLSignatureInput = this.apacheTransform.performTransform(localXMLSignatureInput);
/*     */       }
/* 176 */       if (localXMLSignatureInput.isOctetStream()) {
/* 177 */         return new ApacheOctetStreamData(localXMLSignatureInput);
/*     */       }
/* 179 */       return new ApacheNodeSetData(localXMLSignatureInput);
/*     */     }
/*     */     catch (Exception localException3) {
/* 182 */       throw new TransformException(localException3);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final boolean isFeatureSupported(String paramString) {
/* 187 */     if (paramString == null) {
/* 188 */       throw new NullPointerException();
/*     */     }
/* 190 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  57 */     Init.init();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.ApacheTransform
 * JD-Core Version:    0.6.2
 */