/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transforms;
/*     */ import com.sun.org.apache.xml.internal.security.utils.I18n;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class Manifest extends SignatureElementProxy
/*     */ {
/*  61 */   static Logger log = Logger.getLogger(Manifest.class.getName());
/*     */   List _references;
/*     */   Element[] _referencesEl;
/*  69 */   private boolean[] verificationResults = null;
/*     */ 
/*  72 */   HashMap _resolverProperties = null;
/*     */ 
/*  75 */   List _perManifestResolvers = null;
/*     */ 
/*     */   public Manifest(Document paramDocument)
/*     */   {
/*  84 */     super(paramDocument);
/*     */ 
/*  86 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/*  88 */     this._references = new ArrayList();
/*     */   }
/*     */ 
/*     */   public Manifest(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/* 101 */     super(paramElement, paramString);
/*     */ 
/* 104 */     this._referencesEl = XMLUtils.selectDsNodes(this._constructionElement.getFirstChild(), "Reference");
/*     */ 
/* 106 */     int i = this._referencesEl.length;
/*     */ 
/* 108 */     if (i == 0)
/*     */     {
/* 111 */       Object[] arrayOfObject = { "Reference", "Manifest" };
/*     */ 
/* 114 */       throw new DOMException((short)4, I18n.translate("xml.WrongContent", arrayOfObject));
/*     */     }
/*     */ 
/* 120 */     this._references = new ArrayList(i);
/*     */ 
/* 122 */     for (int j = 0; j < i; j++)
/* 123 */       this._references.add(null);
/*     */   }
/*     */ 
/*     */   public void addDocument(String paramString1, String paramString2, Transforms paramTransforms, String paramString3, String paramString4, String paramString5)
/*     */     throws XMLSignatureException
/*     */   {
/* 145 */     Reference localReference = new Reference(this._doc, paramString1, paramString2, this, paramTransforms, paramString3);
/*     */ 
/* 148 */     if (paramString4 != null) {
/* 149 */       localReference.setId(paramString4);
/*     */     }
/*     */ 
/* 152 */     if (paramString5 != null) {
/* 153 */       localReference.setType(paramString5);
/*     */     }
/*     */ 
/* 157 */     this._references.add(localReference);
/*     */ 
/* 160 */     this._constructionElement.appendChild(localReference.getElement());
/* 161 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public void generateDigestValues()
/*     */     throws XMLSignatureException, ReferenceNotInitializedException
/*     */   {
/* 175 */     for (int i = 0; i < getLength(); i++)
/*     */     {
/* 178 */       Reference localReference = (Reference)this._references.get(i);
/*     */ 
/* 180 */       localReference.generateDigestValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 190 */     return this._references.size();
/*     */   }
/*     */ 
/*     */   public Reference item(int paramInt)
/*     */     throws XMLSecurityException
/*     */   {
/* 203 */     if (this._references.get(paramInt) == null)
/*     */     {
/* 206 */       Reference localReference = new Reference(this._referencesEl[paramInt], this._baseURI, this);
/*     */ 
/* 208 */       this._references.set(paramInt, localReference);
/*     */     }
/*     */ 
/* 211 */     return (Reference)this._references.get(paramInt);
/*     */   }
/*     */ 
/*     */   public void setId(String paramString)
/*     */   {
/* 222 */     if (paramString != null) {
/* 223 */       this._constructionElement.setAttributeNS(null, "Id", paramString);
/* 224 */       IdResolver.registerElementById(this._constructionElement, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 234 */     return this._constructionElement.getAttributeNS(null, "Id");
/*     */   }
/*     */ 
/*     */   public boolean verifyReferences()
/*     */     throws MissingResourceFailureException, XMLSecurityException
/*     */   {
/* 256 */     return verifyReferences(false);
/*     */   }
/*     */ 
/*     */   public boolean verifyReferences(boolean paramBoolean)
/*     */     throws MissingResourceFailureException, XMLSecurityException
/*     */   {
/* 279 */     if (this._referencesEl == null) {
/* 280 */       this._referencesEl = XMLUtils.selectDsNodes(this._constructionElement.getFirstChild(), "Reference");
/*     */     }
/*     */ 
/* 284 */     if (log.isLoggable(Level.FINE)) {
/* 285 */       log.log(Level.FINE, "verify " + this._referencesEl.length + " References");
/* 286 */       log.log(Level.FINE, "I am " + (paramBoolean ? "" : "not") + " requested to follow nested Manifests");
/*     */     }
/*     */ 
/* 290 */     boolean bool1 = true;
/*     */ 
/* 292 */     if (this._referencesEl.length == 0) {
/* 293 */       throw new XMLSecurityException("empty");
/*     */     }
/*     */ 
/* 296 */     this.verificationResults = new boolean[this._referencesEl.length];
/*     */ 
/* 299 */     for (int i = 0; 
/* 300 */       i < this._referencesEl.length; i++) {
/* 301 */       Reference localReference = new Reference(this._referencesEl[i], this._baseURI, this);
/*     */ 
/* 304 */       this._references.set(i, localReference);
/*     */       try
/*     */       {
/* 308 */         boolean bool2 = localReference.verify();
/*     */ 
/* 310 */         setVerificationResult(i, bool2);
/*     */ 
/* 312 */         if (!bool2) {
/* 313 */           bool1 = false;
/*     */         }
/* 315 */         if (log.isLoggable(Level.FINE)) {
/* 316 */           log.log(Level.FINE, "The Reference has Type " + localReference.getType());
/*     */         }
/*     */ 
/* 319 */         if ((bool1) && (paramBoolean) && (localReference.typeIsReferenceToManifest()))
/*     */         {
/* 321 */           log.log(Level.FINE, "We have to follow a nested Manifest");
/*     */           try
/*     */           {
/* 324 */             XMLSignatureInput localXMLSignatureInput = localReference.dereferenceURIandPerformTransforms(null);
/*     */ 
/* 326 */             Set localSet = localXMLSignatureInput.getNodeSet();
/* 327 */             Manifest localManifest = null;
/* 328 */             Iterator localIterator = localSet.iterator();
/*     */ 
/* 330 */             while (localIterator.hasNext()) {
/* 331 */               Node localNode = (Node)localIterator.next();
/*     */ 
/* 333 */               if ((localNode.getNodeType() == 1) && (((Element)localNode).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) && (((Element)localNode).getLocalName().equals("Manifest")))
/*     */               {
/*     */                 try
/*     */                 {
/* 338 */                   localManifest = new Manifest((Element)localNode, localXMLSignatureInput.getSourceURI());
/*     */                 }
/*     */                 catch (XMLSecurityException localXMLSecurityException)
/*     */                 {
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 350 */             if (localManifest == null)
/*     */             {
/* 354 */               throw new MissingResourceFailureException("empty", localReference);
/*     */             }
/*     */ 
/* 358 */             localManifest._perManifestResolvers = this._perManifestResolvers;
/*     */ 
/* 360 */             localManifest._resolverProperties = this._resolverProperties;
/*     */ 
/* 363 */             boolean bool3 = localManifest.verifyReferences(paramBoolean);
/*     */ 
/* 366 */             if (!bool3) {
/* 367 */               bool1 = false;
/*     */ 
/* 369 */               log.log(Level.WARNING, "The nested Manifest was invalid (bad)");
/*     */             } else {
/* 371 */               log.log(Level.FINE, "The nested Manifest was valid (good)");
/*     */             }
/*     */           } catch (IOException localIOException) {
/* 374 */             throw new ReferenceNotInitializedException("empty", localIOException);
/*     */           } catch (ParserConfigurationException localParserConfigurationException) {
/* 376 */             throw new ReferenceNotInitializedException("empty", localParserConfigurationException);
/*     */           } catch (SAXException localSAXException) {
/* 378 */             throw new ReferenceNotInitializedException("empty", localSAXException);
/*     */           }
/*     */         }
/*     */       } catch (ReferenceNotInitializedException localReferenceNotInitializedException) {
/* 382 */         Object[] arrayOfObject = { localReference.getURI() };
/*     */ 
/* 384 */         throw new MissingResourceFailureException("signature.Verification.Reference.NoInput", arrayOfObject, localReferenceNotInitializedException, localReference);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 390 */     return bool1;
/*     */   }
/*     */ 
/*     */   private void setVerificationResult(int paramInt, boolean paramBoolean)
/*     */   {
/* 402 */     if (this.verificationResults == null) {
/* 403 */       this.verificationResults = new boolean[getLength()];
/*     */     }
/*     */ 
/* 406 */     this.verificationResults[paramInt] = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean getVerificationResult(int paramInt)
/*     */     throws XMLSecurityException
/*     */   {
/* 420 */     if ((paramInt < 0) || (paramInt > getLength() - 1)) {
/* 421 */       Object[] arrayOfObject = { Integer.toString(paramInt), Integer.toString(getLength()) };
/*     */ 
/* 423 */       IndexOutOfBoundsException localIndexOutOfBoundsException = new IndexOutOfBoundsException(I18n.translate("signature.Verification.IndexOutOfBounds", arrayOfObject));
/*     */ 
/* 427 */       throw new XMLSecurityException("generic.EmptyMessage", localIndexOutOfBoundsException);
/*     */     }
/*     */ 
/* 430 */     if (this.verificationResults == null) {
/*     */       try {
/* 432 */         verifyReferences();
/*     */       } catch (Exception localException) {
/* 434 */         throw new XMLSecurityException("generic.EmptyMessage", localException);
/*     */       }
/*     */     }
/*     */ 
/* 438 */     return this.verificationResults[paramInt];
/*     */   }
/*     */ 
/*     */   public void addResourceResolver(ResourceResolver paramResourceResolver)
/*     */   {
/* 448 */     if (paramResourceResolver == null) {
/* 449 */       return;
/*     */     }
/* 451 */     if (this._perManifestResolvers == null)
/* 452 */       this._perManifestResolvers = new ArrayList();
/* 453 */     this._perManifestResolvers.add(paramResourceResolver);
/*     */   }
/*     */ 
/*     */   public void addResourceResolver(ResourceResolverSpi paramResourceResolverSpi)
/*     */   {
/* 464 */     if (paramResourceResolverSpi == null) {
/* 465 */       return;
/*     */     }
/* 467 */     if (this._perManifestResolvers == null)
/* 468 */       this._perManifestResolvers = new ArrayList();
/* 469 */     this._perManifestResolvers.add(new ResourceResolver(paramResourceResolverSpi));
/*     */   }
/*     */ 
/*     */   public void setResolverProperty(String paramString1, String paramString2)
/*     */   {
/* 481 */     if (this._resolverProperties == null) {
/* 482 */       this._resolverProperties = new HashMap(10);
/*     */     }
/* 484 */     this._resolverProperties.put(paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public String getResolverProperty(String paramString)
/*     */   {
/* 494 */     return (String)this._resolverProperties.get(paramString);
/*     */   }
/*     */ 
/*     */   public byte[] getSignedContentItem(int paramInt)
/*     */     throws XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 508 */       return getReferencedContentAfterTransformsItem(paramInt).getBytes();
/*     */     } catch (IOException localIOException) {
/* 510 */       throw new XMLSignatureException("empty", localIOException);
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 512 */       throw new XMLSignatureException("empty", localCanonicalizationException);
/*     */     } catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/* 514 */       throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 516 */       throw new XMLSignatureException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput getReferencedContentBeforeTransformsItem(int paramInt)
/*     */     throws XMLSecurityException
/*     */   {
/* 529 */     return item(paramInt).getContentsBeforeTransformation();
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput getReferencedContentAfterTransformsItem(int paramInt)
/*     */     throws XMLSecurityException
/*     */   {
/* 541 */     return item(paramInt).getContentsAfterTransformation();
/*     */   }
/*     */ 
/*     */   public int getSignedContentLength()
/*     */   {
/* 550 */     return getLength();
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 559 */     return "Manifest";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.Manifest
 * JD-Core Version:    0.6.2
 */