/*     */ package com.sun.org.apache.xml.internal.security.transforms;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class Transforms extends SignatureElementProxy
/*     */ {
/*  55 */   static Logger log = Logger.getLogger(Transforms.class.getName());
/*     */   public static final String TRANSFORM_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
/*     */   public static final String TRANSFORM_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
/*     */   public static final String TRANSFORM_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
/*     */   public static final String TRANSFORM_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
/*     */   public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
/*     */   public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
/*     */   public static final String TRANSFORM_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
/*     */   public static final String TRANSFORM_BASE64_DECODE = "http://www.w3.org/2000/09/xmldsig#base64";
/*     */   public static final String TRANSFORM_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
/*     */   public static final String TRANSFORM_ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
/*     */   public static final String TRANSFORM_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
/*     */   public static final String TRANSFORM_XPATH2FILTER04 = "http://www.w3.org/2002/04/xmldsig-filter2";
/*     */   public static final String TRANSFORM_XPATH2FILTER = "http://www.w3.org/2002/06/xmldsig-filter2";
/*     */   public static final String TRANSFORM_XPATHFILTERCHGP = "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
/*     */   Element[] transforms;
/*     */ 
/*     */   protected Transforms()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Transforms(Document paramDocument)
/*     */   {
/* 111 */     super(paramDocument);
/* 112 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public Transforms(Element paramElement, String paramString)
/*     */     throws DOMException, XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException
/*     */   {
/* 132 */     super(paramElement, paramString);
/*     */ 
/* 134 */     int i = getLength();
/*     */ 
/* 136 */     if (i == 0)
/*     */     {
/* 139 */       Object[] arrayOfObject = { "Transform", "Transforms" };
/*     */ 
/* 142 */       throw new TransformationException("xml.WrongContent", arrayOfObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addTransform(String paramString)
/*     */     throws TransformationException
/*     */   {
/*     */     try
/*     */     {
/* 158 */       if (log.isLoggable(Level.FINE)) {
/* 159 */         log.log(Level.FINE, "Transforms.addTransform(" + paramString + ")");
/*     */       }
/* 161 */       Transform localTransform = Transform.getInstance(this._doc, paramString);
/*     */ 
/* 164 */       addTransform(localTransform);
/*     */     } catch (InvalidTransformException localInvalidTransformException) {
/* 166 */       throw new TransformationException("empty", localInvalidTransformException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addTransform(String paramString, Element paramElement)
/*     */     throws TransformationException
/*     */   {
/*     */     try
/*     */     {
/* 184 */       if (log.isLoggable(Level.FINE)) {
/* 185 */         log.log(Level.FINE, "Transforms.addTransform(" + paramString + ")");
/*     */       }
/* 187 */       Transform localTransform = Transform.getInstance(this._doc, paramString, paramElement);
/*     */ 
/* 190 */       addTransform(localTransform);
/*     */     } catch (InvalidTransformException localInvalidTransformException) {
/* 192 */       throw new TransformationException("empty", localInvalidTransformException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addTransform(String paramString, NodeList paramNodeList)
/*     */     throws TransformationException
/*     */   {
/*     */     try
/*     */     {
/* 210 */       Transform localTransform = Transform.getInstance(this._doc, paramString, paramNodeList);
/*     */ 
/* 212 */       addTransform(localTransform);
/*     */     } catch (InvalidTransformException localInvalidTransformException) {
/* 214 */       throw new TransformationException("empty", localInvalidTransformException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addTransform(Transform paramTransform)
/*     */   {
/* 224 */     if (log.isLoggable(Level.FINE)) {
/* 225 */       log.log(Level.FINE, "Transforms.addTransform(" + paramTransform.getURI() + ")");
/*     */     }
/* 227 */     Element localElement = paramTransform.getElement();
/*     */ 
/* 229 */     this._constructionElement.appendChild(localElement);
/* 230 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput performTransforms(XMLSignatureInput paramXMLSignatureInput)
/*     */     throws TransformationException
/*     */   {
/* 243 */     return performTransforms(paramXMLSignatureInput, null);
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput performTransforms(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream)
/*     */     throws TransformationException
/*     */   {
/*     */     try
/*     */     {
/* 260 */       int i = getLength() - 1;
/* 261 */       for (int j = 0; j < i; j++) {
/* 262 */         Transform localTransform2 = item(j);
/* 263 */         if (log.isLoggable(Level.FINE)) {
/* 264 */           log.log(Level.FINE, "Perform the (" + j + ")th " + localTransform2.getURI() + " transform");
/*     */         }
/*     */ 
/* 267 */         paramXMLSignatureInput = localTransform2.performTransform(paramXMLSignatureInput);
/*     */       }
/*     */       Transform localTransform1;
/* 269 */       if (i >= 0)
/* 270 */         localTransform1 = item(i);
/* 271 */       return localTransform1.performTransform(paramXMLSignatureInput, paramOutputStream);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 276 */       throw new TransformationException("empty", localIOException);
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 278 */       throw new TransformationException("empty", localCanonicalizationException);
/*     */     } catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/* 280 */       throw new TransformationException("empty", localInvalidCanonicalizerException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 291 */     if (this.transforms == null) {
/* 292 */       this.transforms = XMLUtils.selectDsNodes(this._constructionElement.getFirstChild(), "Transform");
/*     */     }
/*     */ 
/* 295 */     return this.transforms.length;
/*     */   }
/*     */ 
/*     */   public Transform item(int paramInt)
/*     */     throws TransformationException
/*     */   {
/*     */     try
/*     */     {
/* 309 */       if (this.transforms == null) {
/* 310 */         this.transforms = XMLUtils.selectDsNodes(this._constructionElement.getFirstChild(), "Transform");
/*     */       }
/*     */ 
/* 313 */       return new Transform(this.transforms[paramInt], this._baseURI);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 315 */       throw new TransformationException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 321 */     return "Transforms";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.transforms.Transforms
 * JD-Core Version:    0.6.2
 */