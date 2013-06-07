/*     */ package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
/*     */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
/*     */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
/*     */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
/*     */ import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transforms;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class RetrievalMethodResolver extends KeyResolverSpi
/*     */ {
/*  72 */   static Logger log = Logger.getLogger(RetrievalMethodResolver.class.getName());
/*     */ 
/*     */   public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */   {
/*  87 */     if (!XMLUtils.elementIsInSignatureSpace(paramElement, "RetrievalMethod"))
/*     */     {
/*  89 */       return null;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  94 */       RetrievalMethod localRetrievalMethod = new RetrievalMethod(paramElement, paramString);
/*  95 */       String str = localRetrievalMethod.getType();
/*  96 */       XMLSignatureInput localXMLSignatureInput = resolveInput(localRetrievalMethod, paramString);
/*  97 */       if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(str))
/*     */       {
/*  99 */         localObject = getRawCertificate(localXMLSignatureInput);
/* 100 */         if (localObject != null) {
/* 101 */           return ((X509Certificate)localObject).getPublicKey();
/*     */         }
/* 103 */         return null;
/*     */       }
/* 105 */       Object localObject = obtainRefrenceElement(localXMLSignatureInput);
/* 106 */       return resolveKey((Element)localObject, paramString, paramStorageResolver);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 108 */       log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
/*     */     } catch (CertificateException localCertificateException) {
/* 110 */       log.log(Level.FINE, "CertificateException", localCertificateException);
/*     */     } catch (IOException localIOException) {
/* 112 */       log.log(Level.FINE, "IOException", localIOException);
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 114 */       log.log(Level.FINE, "ParserConfigurationException", localParserConfigurationException);
/*     */     } catch (SAXException localSAXException) {
/* 116 */       log.log(Level.FINE, "SAXException", localSAXException);
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   private static Element obtainRefrenceElement(XMLSignatureInput paramXMLSignatureInput)
/*     */     throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException
/*     */   {
/*     */     Element localElement;
/* 123 */     if (paramXMLSignatureInput.isElement()) {
/* 124 */       localElement = (Element)paramXMLSignatureInput.getSubNode();
/* 125 */     } else if (paramXMLSignatureInput.isNodeSet())
/*     */     {
/* 127 */       localElement = getDocumentElement(paramXMLSignatureInput.getNodeSet());
/*     */     }
/*     */     else {
/* 130 */       byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
/* 131 */       localElement = getDocFromBytes(arrayOfByte);
/*     */ 
/* 133 */       if (log.isLoggable(Level.FINE))
/* 134 */         log.log(Level.FINE, "we have to parse " + arrayOfByte.length + " bytes");
/*     */     }
/* 136 */     return localElement;
/*     */   }
/*     */ 
/*     */   public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */   {
/* 150 */     if (!XMLUtils.elementIsInSignatureSpace(paramElement, "RetrievalMethod"))
/*     */     {
/* 152 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 156 */       RetrievalMethod localRetrievalMethod = new RetrievalMethod(paramElement, paramString);
/* 157 */       String str = localRetrievalMethod.getType();
/* 158 */       XMLSignatureInput localXMLSignatureInput = resolveInput(localRetrievalMethod, paramString);
/* 159 */       if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(str)) {
/* 160 */         return getRawCertificate(localXMLSignatureInput);
/*     */       }
/*     */ 
/* 163 */       Object localObject = obtainRefrenceElement(localXMLSignatureInput);
/* 164 */       return resolveCertificate((Element)localObject, paramString, paramStorageResolver);
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 166 */       log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
/*     */     } catch (CertificateException localCertificateException) {
/* 168 */       log.log(Level.FINE, "CertificateException", localCertificateException);
/*     */     } catch (IOException localIOException) {
/* 170 */       log.log(Level.FINE, "IOException", localIOException);
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 172 */       log.log(Level.FINE, "ParserConfigurationException", localParserConfigurationException);
/*     */     } catch (SAXException localSAXException) {
/* 174 */       log.log(Level.FINE, "SAXException", localSAXException);
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   private static X509Certificate resolveCertificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */     throws KeyResolverException
/*     */   {
/* 188 */     if (log.isLoggable(Level.FINE)) {
/* 189 */       log.log(Level.FINE, "Now we have a {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName() + " Element");
/*     */     }
/* 191 */     if (paramElement != null) {
/* 192 */       return KeyResolver.getX509Certificate(paramElement, paramString, paramStorageResolver);
/*     */     }
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */   private static PublicKey resolveKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */     throws KeyResolverException
/*     */   {
/* 206 */     if (log.isLoggable(Level.FINE)) {
/* 207 */       log.log(Level.FINE, "Now we have a {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName() + " Element");
/*     */     }
/* 209 */     if (paramElement != null) {
/* 210 */       return KeyResolver.getPublicKey(paramElement, paramString, paramStorageResolver);
/*     */     }
/* 212 */     return null;
/*     */   }
/*     */ 
/*     */   private static X509Certificate getRawCertificate(XMLSignatureInput paramXMLSignatureInput) throws CanonicalizationException, IOException, CertificateException {
/* 216 */     byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
/*     */ 
/* 218 */     CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
/* 219 */     X509Certificate localX509Certificate = (X509Certificate)localCertificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfByte));
/* 220 */     return localX509Certificate;
/*     */   }
/*     */ 
/*     */   private static XMLSignatureInput resolveInput(RetrievalMethod paramRetrievalMethod, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/* 228 */     Attr localAttr = paramRetrievalMethod.getURIAttr();
/*     */ 
/* 230 */     Transforms localTransforms = paramRetrievalMethod.getTransforms();
/* 231 */     ResourceResolver localResourceResolver = ResourceResolver.getInstance(localAttr, paramString);
/* 232 */     if (localResourceResolver != null) {
/* 233 */       XMLSignatureInput localXMLSignatureInput = localResourceResolver.resolve(localAttr, paramString);
/* 234 */       if (localTransforms != null) {
/* 235 */         log.log(Level.FINE, "We have Transforms");
/* 236 */         localXMLSignatureInput = localTransforms.performTransforms(localXMLSignatureInput);
/*     */       }
/* 238 */       return localXMLSignatureInput;
/*     */     }
/* 240 */     return null;
/*     */   }
/*     */ 
/*     */   static Element getDocFromBytes(byte[] paramArrayOfByte)
/*     */     throws KeyResolverException
/*     */   {
/*     */     try
/*     */     {
/* 252 */       DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/* 253 */       localDocumentBuilderFactory.setNamespaceAware(true);
/* 254 */       DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/* 255 */       Document localDocument = localDocumentBuilder.parse(new ByteArrayInputStream(paramArrayOfByte));
/*     */ 
/* 257 */       return localDocument.getDocumentElement();
/*     */     } catch (SAXException localSAXException) {
/* 259 */       throw new KeyResolverException("empty", localSAXException);
/*     */     } catch (IOException localIOException) {
/* 261 */       throw new KeyResolverException("empty", localIOException);
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 263 */       throw new KeyResolverException("empty", localParserConfigurationException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
/*     */   {
/* 278 */     return null;
/*     */   }
/*     */ 
/*     */   static Element getDocumentElement(Set paramSet) {
/* 282 */     Iterator localIterator = paramSet.iterator();
/* 283 */     Element localElement1 = null;
/* 284 */     while (localIterator.hasNext()) {
/* 285 */       localObject1 = (Node)localIterator.next();
/* 286 */       if ((localObject1 != null) && (((Node)localObject1).getNodeType() == 1)) {
/* 287 */         localElement1 = (Element)localObject1;
/* 288 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 292 */     Object localObject1 = new ArrayList(10);
/*     */ 
/* 295 */     while (localElement1 != null) {
/* 296 */       ((List)localObject1).add(localElement1);
/* 297 */       localObject2 = localElement1.getParentNode();
/* 298 */       if ((localObject2 == null) || (((Node)localObject2).getNodeType() != 1)) {
/*     */         break;
/*     */       }
/* 301 */       localElement1 = (Element)localObject2;
/*     */     }
/*     */ 
/* 304 */     Object localObject2 = ((List)localObject1).listIterator(((List)localObject1).size() - 1);
/* 305 */     Element localElement2 = null;
/* 306 */     while (((ListIterator)localObject2).hasPrevious()) {
/* 307 */       localElement2 = (Element)((ListIterator)localObject2).previous();
/* 308 */       if (paramSet.contains(localElement2)) {
/* 309 */         return localElement2;
/*     */       }
/*     */     }
/* 312 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RetrievalMethodResolver
 * JD-Core Version:    0.6.2
 */