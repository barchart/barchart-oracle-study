/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transform;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transforms;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
/*     */ import com.sun.org.apache.xml.internal.security.utils.Base64;
/*     */ import com.sun.org.apache.xml.internal.security.utils.DigesterOutputStream;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public class Reference extends SignatureElementProxy
/*     */ {
/* 111 */   private static boolean useC14N11 = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Boolean run() {
/* 114 */       return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
/*     */     }
/*     */   })).booleanValue();
/*     */   public static final boolean CacheSignedNodes = false;
/* 133 */   static Logger log = Logger.getLogger(Reference.class.getName());
/*     */   public static final String OBJECT_URI = "http://www.w3.org/2000/09/xmldsig#Object";
/*     */   public static final String MANIFEST_URI = "http://www.w3.org/2000/09/xmldsig#Manifest";
/* 144 */   Manifest _manifest = null;
/*     */   XMLSignatureInput _transformsOutput;
/*     */   private Transforms transforms;
/*     */   private Element digestMethodElem;
/*     */   private Element digestValueElement;
/*     */ 
/*     */   protected Reference(Document paramDocument, String paramString1, String paramString2, Manifest paramManifest, Transforms paramTransforms, String paramString3)
/*     */     throws XMLSignatureException
/*     */   {
/* 169 */     super(paramDocument);
/*     */ 
/* 171 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 173 */     this._baseURI = paramString1;
/* 174 */     this._manifest = paramManifest;
/*     */ 
/* 176 */     setURI(paramString2);
/*     */ 
/* 183 */     if (paramTransforms != null) {
/* 184 */       this.transforms = paramTransforms;
/* 185 */       this._constructionElement.appendChild(paramTransforms.getElement());
/* 186 */       XMLUtils.addReturnToElement(this._constructionElement);
/*     */     }
/*     */ 
/* 189 */     MessageDigestAlgorithm localMessageDigestAlgorithm = MessageDigestAlgorithm.getInstance(this._doc, paramString3);
/*     */ 
/* 193 */     this.digestMethodElem = localMessageDigestAlgorithm.getElement();
/* 194 */     this._constructionElement.appendChild(this.digestMethodElem);
/* 195 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */ 
/* 198 */     this.digestValueElement = XMLUtils.createElementInSignatureSpace(this._doc, "DigestValue");
/*     */ 
/* 202 */     this._constructionElement.appendChild(this.digestValueElement);
/* 203 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   protected Reference(Element paramElement, String paramString, Manifest paramManifest)
/*     */     throws XMLSecurityException
/*     */   {
/* 219 */     super(paramElement, paramString);
/* 220 */     this._baseURI = paramString;
/* 221 */     Element localElement = XMLUtils.getNextElement(paramElement.getFirstChild());
/* 222 */     if (("Transforms".equals(localElement.getLocalName())) && ("http://www.w3.org/2000/09/xmldsig#".equals(localElement.getNamespaceURI())))
/*     */     {
/* 224 */       this.transforms = new Transforms(localElement, this._baseURI);
/* 225 */       localElement = XMLUtils.getNextElement(localElement.getNextSibling());
/*     */     }
/* 227 */     this.digestMethodElem = localElement;
/* 228 */     this.digestValueElement = XMLUtils.getNextElement(this.digestMethodElem.getNextSibling());
/* 229 */     this._manifest = paramManifest;
/*     */   }
/*     */ 
/*     */   public MessageDigestAlgorithm getMessageDigestAlgorithm()
/*     */     throws XMLSignatureException
/*     */   {
/* 243 */     if (this.digestMethodElem == null) {
/* 244 */       return null;
/*     */     }
/*     */ 
/* 247 */     String str = this.digestMethodElem.getAttributeNS(null, "Algorithm");
/*     */ 
/* 250 */     if (str == null) {
/* 251 */       return null;
/*     */     }
/*     */ 
/* 254 */     return MessageDigestAlgorithm.getInstance(this._doc, str);
/*     */   }
/*     */ 
/*     */   public void setURI(String paramString)
/*     */   {
/* 264 */     if (paramString != null)
/* 265 */       this._constructionElement.setAttributeNS(null, "URI", paramString);
/*     */   }
/*     */ 
/*     */   public String getURI()
/*     */   {
/* 276 */     return this._constructionElement.getAttributeNS(null, "URI");
/*     */   }
/*     */ 
/*     */   public void setId(String paramString)
/*     */   {
/* 286 */     if (paramString != null) {
/* 287 */       this._constructionElement.setAttributeNS(null, "Id", paramString);
/* 288 */       IdResolver.registerElementById(this._constructionElement, paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 298 */     return this._constructionElement.getAttributeNS(null, "Id");
/*     */   }
/*     */ 
/*     */   public void setType(String paramString)
/*     */   {
/* 308 */     if (paramString != null)
/* 309 */       this._constructionElement.setAttributeNS(null, "Type", paramString);
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 320 */     return this._constructionElement.getAttributeNS(null, "Type");
/*     */   }
/*     */ 
/*     */   public boolean typeIsReferenceToObject()
/*     */   {
/* 334 */     if ("http://www.w3.org/2000/09/xmldsig#Object".equals(getType())) {
/* 335 */       return true;
/*     */     }
/*     */ 
/* 338 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean typeIsReferenceToManifest()
/*     */   {
/* 351 */     if ("http://www.w3.org/2000/09/xmldsig#Manifest".equals(getType())) {
/* 352 */       return true;
/*     */     }
/*     */ 
/* 355 */     return false;
/*     */   }
/*     */ 
/*     */   private void setDigestValueElement(byte[] paramArrayOfByte)
/*     */   {
/* 365 */     Node localNode = this.digestValueElement.getFirstChild();
/* 366 */     while (localNode != null) {
/* 367 */       this.digestValueElement.removeChild(localNode);
/* 368 */       localNode = localNode.getNextSibling();
/*     */     }
/*     */ 
/* 371 */     String str = Base64.encode(paramArrayOfByte);
/* 372 */     Text localText = this._doc.createTextNode(str);
/*     */ 
/* 374 */     this.digestValueElement.appendChild(localText);
/*     */   }
/*     */ 
/*     */   public void generateDigestValue()
/*     */     throws XMLSignatureException, ReferenceNotInitializedException
/*     */   {
/* 385 */     setDigestValueElement(calculateDigest(false));
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput getContentsBeforeTransformation()
/*     */     throws ReferenceNotInitializedException
/*     */   {
/*     */     try
/*     */     {
/* 398 */       Attr localAttr = this._constructionElement.getAttributeNodeNS(null, "URI");
/*     */       String str;
/* 402 */       if (localAttr == null)
/* 403 */         str = null;
/*     */       else {
/* 405 */         str = localAttr.getNodeValue();
/*     */       }
/*     */ 
/* 408 */       ResourceResolver localResourceResolver = ResourceResolver.getInstance(localAttr, this._baseURI, this._manifest._perManifestResolvers);
/*     */       Object localObject;
/* 411 */       if (localResourceResolver == null) {
/* 412 */         localObject = new Object[] { str };
/*     */ 
/* 414 */         throw new ReferenceNotInitializedException("signature.Verification.Reference.NoInput", (Object[])localObject);
/*     */       }
/*     */ 
/* 418 */       localResourceResolver.addProperties(this._manifest._resolverProperties);
/*     */ 
/* 420 */       return localResourceResolver.resolve(localAttr, this._baseURI);
/*     */     }
/*     */     catch (ResourceResolverException localResourceResolverException)
/*     */     {
/* 425 */       throw new ReferenceNotInitializedException("empty", localResourceResolverException);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 427 */       throw new ReferenceNotInitializedException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public XMLSignatureInput getTransformsInput()
/*     */     throws ReferenceNotInitializedException
/*     */   {
/* 441 */     XMLSignatureInput localXMLSignatureInput1 = getContentsBeforeTransformation();
/*     */     XMLSignatureInput localXMLSignatureInput2;
/*     */     try
/*     */     {
/* 444 */       localXMLSignatureInput2 = new XMLSignatureInput(localXMLSignatureInput1.getBytes());
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 446 */       throw new ReferenceNotInitializedException("empty", localCanonicalizationException);
/*     */     } catch (IOException localIOException) {
/* 448 */       throw new ReferenceNotInitializedException("empty", localIOException);
/*     */     }
/* 450 */     localXMLSignatureInput2.setSourceURI(localXMLSignatureInput1.getSourceURI());
/* 451 */     return localXMLSignatureInput2;
/*     */   }
/*     */ 
/*     */   private XMLSignatureInput getContentsAfterTransformation(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream)
/*     */     throws XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 459 */       Transforms localTransforms = getTransforms();
/* 460 */       XMLSignatureInput localXMLSignatureInput = null;
/*     */ 
/* 462 */       if (localTransforms != null) {
/* 463 */         localXMLSignatureInput = localTransforms.performTransforms(paramXMLSignatureInput, paramOutputStream);
/* 464 */         this._transformsOutput = localXMLSignatureInput;
/*     */       }
/*     */ 
/* 468 */       return paramXMLSignatureInput;
/*     */     }
/*     */     catch (ResourceResolverException localResourceResolverException)
/*     */     {
/* 473 */       throw new XMLSignatureException("empty", localResourceResolverException);
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 475 */       throw new XMLSignatureException("empty", localCanonicalizationException);
/*     */     } catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/* 477 */       throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
/*     */     } catch (TransformationException localTransformationException) {
/* 479 */       throw new XMLSignatureException("empty", localTransformationException);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 481 */       throw new XMLSignatureException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput getContentsAfterTransformation()
/*     */     throws XMLSignatureException
/*     */   {
/* 493 */     XMLSignatureInput localXMLSignatureInput = getContentsBeforeTransformation();
/*     */ 
/* 495 */     return getContentsAfterTransformation(localXMLSignatureInput, null);
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput getNodesetBeforeFirstCanonicalization()
/*     */     throws XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 509 */       XMLSignatureInput localXMLSignatureInput1 = getContentsBeforeTransformation();
/* 510 */       XMLSignatureInput localXMLSignatureInput2 = localXMLSignatureInput1;
/* 511 */       Transforms localTransforms = getTransforms();
/*     */ 
/* 513 */       if (localTransforms != null) {
/* 514 */         for (int i = 0; i < localTransforms.getLength(); i++) {
/* 515 */           Transform localTransform = localTransforms.item(i);
/* 516 */           String str = localTransform.getURI();
/*     */ 
/* 518 */           if ((str.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) || (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) || (str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")) || (str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")))
/*     */           {
/*     */             break;
/*     */           }
/*     */ 
/* 530 */           localXMLSignatureInput2 = localTransform.performTransform(localXMLSignatureInput2, null);
/*     */         }
/*     */ 
/* 533 */         localXMLSignatureInput2.setSourceURI(localXMLSignatureInput1.getSourceURI());
/*     */       }
/* 535 */       return localXMLSignatureInput2;
/*     */     } catch (IOException localIOException) {
/* 537 */       throw new XMLSignatureException("empty", localIOException);
/*     */     } catch (ResourceResolverException localResourceResolverException) {
/* 539 */       throw new XMLSignatureException("empty", localResourceResolverException);
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 541 */       throw new XMLSignatureException("empty", localCanonicalizationException);
/*     */     } catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/* 543 */       throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
/*     */     } catch (TransformationException localTransformationException) {
/* 545 */       throw new XMLSignatureException("empty", localTransformationException);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 547 */       throw new XMLSignatureException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getHTMLRepresentation()
/*     */     throws XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 559 */       XMLSignatureInput localXMLSignatureInput = getNodesetBeforeFirstCanonicalization();
/* 560 */       Object localObject1 = new HashSet();
/*     */ 
/* 563 */       Transforms localTransforms = getTransforms();
/* 564 */       Object localObject2 = null;
/*     */ 
/* 566 */       if (localTransforms != null) {
/* 567 */         for (int i = 0; i < localTransforms.getLength(); i++) {
/* 568 */           Transform localTransform = localTransforms.item(i);
/* 569 */           String str = localTransform.getURI();
/*     */ 
/* 571 */           if ((str.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) || (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")))
/*     */           {
/* 574 */             localObject2 = localTransform;
/*     */ 
/* 576 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 581 */       if (localObject2 != null)
/*     */       {
/* 583 */         if (localObject2.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1)
/*     */         {
/* 589 */           InclusiveNamespaces localInclusiveNamespaces = new InclusiveNamespaces(XMLUtils.selectNode(localObject2.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), getBaseURI());
/*     */ 
/* 595 */           localObject1 = InclusiveNamespaces.prefixStr2Set(localInclusiveNamespaces.getInclusiveNamespaces());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 601 */       return localXMLSignatureInput.getHTMLRepresentation((Set)localObject1);
/*     */     } catch (TransformationException localTransformationException) {
/* 603 */       throw new XMLSignatureException("empty", localTransformationException);
/*     */     } catch (InvalidTransformException localInvalidTransformException) {
/* 605 */       throw new XMLSignatureException("empty", localInvalidTransformException);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 607 */       throw new XMLSignatureException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput getTransformsOutput()
/*     */   {
/* 616 */     return this._transformsOutput;
/*     */   }
/*     */ 
/*     */   protected XMLSignatureInput dereferenceURIandPerformTransforms(OutputStream paramOutputStream)
/*     */     throws XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 632 */       XMLSignatureInput localXMLSignatureInput1 = getContentsBeforeTransformation();
/* 633 */       XMLSignatureInput localXMLSignatureInput2 = getContentsAfterTransformation(localXMLSignatureInput1, paramOutputStream);
/*     */ 
/* 642 */       this._transformsOutput = localXMLSignatureInput2;
/*     */ 
/* 646 */       return localXMLSignatureInput2;
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 648 */       throw new ReferenceNotInitializedException("empty", localXMLSecurityException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Transforms getTransforms()
/*     */     throws XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException
/*     */   {
/* 665 */     return this.transforms;
/*     */   }
/*     */ 
/*     */   public byte[] getReferencedBytes()
/*     */     throws ReferenceNotInitializedException, XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 678 */       XMLSignatureInput localXMLSignatureInput = dereferenceURIandPerformTransforms(null);
/*     */ 
/* 680 */       return localXMLSignatureInput.getBytes();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 684 */       throw new ReferenceNotInitializedException("empty", localIOException);
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 686 */       throw new ReferenceNotInitializedException("empty", localCanonicalizationException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] calculateDigest(boolean paramBoolean)
/*     */     throws ReferenceNotInitializedException, XMLSignatureException
/*     */   {
/*     */     try
/*     */     {
/* 705 */       MessageDigestAlgorithm localMessageDigestAlgorithm = getMessageDigestAlgorithm();
/*     */ 
/* 707 */       localMessageDigestAlgorithm.reset();
/* 708 */       DigesterOutputStream localDigesterOutputStream = new DigesterOutputStream(localMessageDigestAlgorithm);
/* 709 */       UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(localDigesterOutputStream);
/* 710 */       XMLSignatureInput localXMLSignatureInput = dereferenceURIandPerformTransforms(localUnsyncBufferedOutputStream);
/*     */ 
/* 713 */       if ((useC14N11) && (!paramBoolean) && (!localXMLSignatureInput.isOutputStreamSet()) && (!localXMLSignatureInput.isOctetStream()))
/*     */       {
/* 715 */         if (this.transforms == null) {
/* 716 */           this.transforms = new Transforms(this._doc);
/* 717 */           this._constructionElement.insertBefore(this.transforms.getElement(), this.digestMethodElem);
/*     */         }
/*     */ 
/* 720 */         this.transforms.addTransform("http://www.w3.org/2006/12/xml-c14n11");
/* 721 */         localXMLSignatureInput.updateOutputStream(localUnsyncBufferedOutputStream, true);
/*     */       } else {
/* 723 */         localXMLSignatureInput.updateOutputStream(localUnsyncBufferedOutputStream);
/*     */       }
/* 725 */       localUnsyncBufferedOutputStream.flush();
/*     */ 
/* 729 */       return localDigesterOutputStream.getDigestValue();
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 731 */       throw new ReferenceNotInitializedException("empty", localXMLSecurityException);
/*     */     } catch (IOException localIOException) {
/* 733 */       throw new ReferenceNotInitializedException("empty", localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] getDigestValue()
/*     */     throws Base64DecodingException, XMLSecurityException
/*     */   {
/* 745 */     if (this.digestValueElement == null)
/*     */     {
/* 747 */       localObject = new Object[] { "DigestValue", "http://www.w3.org/2000/09/xmldsig#" };
/*     */ 
/* 749 */       throw new XMLSecurityException("signature.Verification.NoSignatureElement", (Object[])localObject);
/*     */     }
/*     */ 
/* 753 */     Object localObject = Base64.decode(this.digestValueElement);
/* 754 */     return localObject;
/*     */   }
/*     */ 
/*     */   public boolean verify()
/*     */     throws ReferenceNotInitializedException, XMLSecurityException
/*     */   {
/* 768 */     byte[] arrayOfByte1 = getDigestValue();
/* 769 */     byte[] arrayOfByte2 = calculateDigest(true);
/* 770 */     boolean bool = MessageDigestAlgorithm.isEqual(arrayOfByte1, arrayOfByte2);
/*     */ 
/* 772 */     if (!bool) {
/* 773 */       log.log(Level.WARNING, "Verification failed for URI \"" + getURI() + "\"");
/* 774 */       log.log(Level.WARNING, "Expected Digest: " + Base64.encode(arrayOfByte1));
/* 775 */       log.log(Level.WARNING, "Actual Digest: " + Base64.encode(arrayOfByte2));
/*     */     } else {
/* 777 */       log.log(Level.INFO, "Verification successful for URI \"" + getURI() + "\"");
/*     */     }
/*     */ 
/* 780 */     return bool;
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 789 */     return "Reference";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.Reference
 * JD-Core Version:    0.6.2
 */