/*      */ package com.sun.org.apache.xml.internal.security.keys;
/*      */ 
/*      */ import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
/*      */ import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
/*      */ import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
/*      */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.PGPData;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.SPKIData;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
/*      */ import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
/*      */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
/*      */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
/*      */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
/*      */ import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
/*      */ import com.sun.org.apache.xml.internal.security.transforms.Transforms;
/*      */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*      */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*      */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*      */ import java.security.PublicKey;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.crypto.SecretKey;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class KeyInfo extends SignatureElementProxy
/*      */ {
/*   98 */   static Logger log = Logger.getLogger(KeyInfo.class.getName());
/*      */ 
/*  100 */   List x509Datas = null;
/*  101 */   List encryptedKeys = null;
/*      */ 
/*  107 */   static final List nullList = Collections.unmodifiableList(localArrayList);
/*      */ 
/* 1015 */   List _internalKeyResolvers = null;
/*      */ 
/* 1051 */   List _storageResolvers = nullList;
/*      */ 
/* 1067 */   static boolean _alreadyInitialized = false;
/*      */ 
/*      */   public KeyInfo(Document paramDocument)
/*      */   {
/*  116 */     super(paramDocument);
/*      */ 
/*  118 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public KeyInfo(Element paramElement, String paramString)
/*      */     throws XMLSecurityException
/*      */   {
/*  130 */     super(paramElement, paramString);
/*      */   }
/*      */ 
/*      */   public void setId(String paramString)
/*      */   {
/*  142 */     if (paramString != null) {
/*  143 */       this._constructionElement.setAttributeNS(null, "Id", paramString);
/*  144 */       IdResolver.registerElementById(this._constructionElement, paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getId()
/*      */   {
/*  154 */     return this._constructionElement.getAttributeNS(null, "Id");
/*      */   }
/*      */ 
/*      */   public void addKeyName(String paramString)
/*      */   {
/*  163 */     add(new KeyName(this._doc, paramString));
/*      */   }
/*      */ 
/*      */   public void add(KeyName paramKeyName)
/*      */   {
/*  173 */     this._constructionElement.appendChild(paramKeyName.getElement());
/*  174 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public void addKeyValue(PublicKey paramPublicKey)
/*      */   {
/*  183 */     add(new KeyValue(this._doc, paramPublicKey));
/*      */   }
/*      */ 
/*      */   public void addKeyValue(Element paramElement)
/*      */   {
/*  192 */     add(new KeyValue(this._doc, paramElement));
/*      */   }
/*      */ 
/*      */   public void add(DSAKeyValue paramDSAKeyValue)
/*      */   {
/*  201 */     add(new KeyValue(this._doc, paramDSAKeyValue));
/*      */   }
/*      */ 
/*      */   public void add(RSAKeyValue paramRSAKeyValue)
/*      */   {
/*  210 */     add(new KeyValue(this._doc, paramRSAKeyValue));
/*      */   }
/*      */ 
/*      */   public void add(PublicKey paramPublicKey)
/*      */   {
/*  219 */     add(new KeyValue(this._doc, paramPublicKey));
/*      */   }
/*      */ 
/*      */   public void add(KeyValue paramKeyValue)
/*      */   {
/*  228 */     this._constructionElement.appendChild(paramKeyValue.getElement());
/*  229 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public void addMgmtData(String paramString)
/*      */   {
/*  238 */     add(new MgmtData(this._doc, paramString));
/*      */   }
/*      */ 
/*      */   public void add(MgmtData paramMgmtData)
/*      */   {
/*  247 */     this._constructionElement.appendChild(paramMgmtData.getElement());
/*  248 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public void add(PGPData paramPGPData)
/*      */   {
/*  257 */     this._constructionElement.appendChild(paramPGPData.getElement());
/*  258 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public void addRetrievalMethod(String paramString1, Transforms paramTransforms, String paramString2)
/*      */   {
/*  270 */     add(new RetrievalMethod(this._doc, paramString1, paramTransforms, paramString2));
/*      */   }
/*      */ 
/*      */   public void add(RetrievalMethod paramRetrievalMethod)
/*      */   {
/*  279 */     this._constructionElement.appendChild(paramRetrievalMethod.getElement());
/*  280 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public void add(SPKIData paramSPKIData)
/*      */   {
/*  289 */     this._constructionElement.appendChild(paramSPKIData.getElement());
/*  290 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public void add(X509Data paramX509Data)
/*      */   {
/*  299 */     if (this.x509Datas == null)
/*  300 */       this.x509Datas = new ArrayList();
/*  301 */     this.x509Datas.add(paramX509Data);
/*  302 */     this._constructionElement.appendChild(paramX509Data.getElement());
/*  303 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public void add(EncryptedKey paramEncryptedKey)
/*      */     throws XMLEncryptionException
/*      */   {
/*  315 */     if (this.encryptedKeys == null)
/*  316 */       this.encryptedKeys = new ArrayList();
/*  317 */     this.encryptedKeys.add(paramEncryptedKey);
/*  318 */     XMLCipher localXMLCipher = XMLCipher.getInstance();
/*  319 */     this._constructionElement.appendChild(localXMLCipher.martial(paramEncryptedKey));
/*      */   }
/*      */ 
/*      */   public void addUnknownElement(Element paramElement)
/*      */   {
/*  328 */     this._constructionElement.appendChild(paramElement);
/*  329 */     XMLUtils.addReturnToElement(this._constructionElement);
/*      */   }
/*      */ 
/*      */   public int lengthKeyName()
/*      */   {
/*  338 */     return length("http://www.w3.org/2000/09/xmldsig#", "KeyName");
/*      */   }
/*      */ 
/*      */   public int lengthKeyValue()
/*      */   {
/*  347 */     return length("http://www.w3.org/2000/09/xmldsig#", "KeyValue");
/*      */   }
/*      */ 
/*      */   public int lengthMgmtData()
/*      */   {
/*  356 */     return length("http://www.w3.org/2000/09/xmldsig#", "MgmtData");
/*      */   }
/*      */ 
/*      */   public int lengthPGPData()
/*      */   {
/*  365 */     return length("http://www.w3.org/2000/09/xmldsig#", "PGPData");
/*      */   }
/*      */ 
/*      */   public int lengthRetrievalMethod()
/*      */   {
/*  374 */     return length("http://www.w3.org/2000/09/xmldsig#", "RetrievalMethod");
/*      */   }
/*      */ 
/*      */   public int lengthSPKIData()
/*      */   {
/*  384 */     return length("http://www.w3.org/2000/09/xmldsig#", "SPKIData");
/*      */   }
/*      */ 
/*      */   public int lengthX509Data()
/*      */   {
/*  393 */     if (this.x509Datas != null) {
/*  394 */       return this.x509Datas.size();
/*      */     }
/*  396 */     return length("http://www.w3.org/2000/09/xmldsig#", "X509Data");
/*      */   }
/*      */ 
/*      */   public int lengthUnknownElement()
/*      */   {
/*  406 */     int i = 0;
/*  407 */     NodeList localNodeList = this._constructionElement.getChildNodes();
/*      */ 
/*  409 */     for (int j = 0; j < localNodeList.getLength(); j++) {
/*  410 */       Node localNode = localNodeList.item(j);
/*      */ 
/*  416 */       if ((localNode.getNodeType() == 1) && (localNode.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")))
/*      */       {
/*  419 */         i++;
/*      */       }
/*      */     }
/*      */ 
/*  423 */     return i;
/*      */   }
/*      */ 
/*      */   public KeyName itemKeyName(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  435 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "KeyName", paramInt);
/*      */ 
/*  438 */     if (localElement != null) {
/*  439 */       return new KeyName(localElement, this._baseURI);
/*      */     }
/*  441 */     return null;
/*      */   }
/*      */ 
/*      */   public KeyValue itemKeyValue(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  453 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "KeyValue", paramInt);
/*      */ 
/*  456 */     if (localElement != null) {
/*  457 */       return new KeyValue(localElement, this._baseURI);
/*      */     }
/*  459 */     return null;
/*      */   }
/*      */ 
/*      */   public MgmtData itemMgmtData(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  471 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "MgmtData", paramInt);
/*      */ 
/*  474 */     if (localElement != null) {
/*  475 */       return new MgmtData(localElement, this._baseURI);
/*      */     }
/*  477 */     return null;
/*      */   }
/*      */ 
/*      */   public PGPData itemPGPData(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  489 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "PGPData", paramInt);
/*      */ 
/*  492 */     if (localElement != null) {
/*  493 */       return new PGPData(localElement, this._baseURI);
/*      */     }
/*  495 */     return null;
/*      */   }
/*      */ 
/*      */   public RetrievalMethod itemRetrievalMethod(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  508 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "RetrievalMethod", paramInt);
/*      */ 
/*  511 */     if (localElement != null) {
/*  512 */       return new RetrievalMethod(localElement, this._baseURI);
/*      */     }
/*  514 */     return null;
/*      */   }
/*      */ 
/*      */   public SPKIData itemSPKIData(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  526 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "SPKIData", paramInt);
/*      */ 
/*  529 */     if (localElement != null) {
/*  530 */       return new SPKIData(localElement, this._baseURI);
/*      */     }
/*  532 */     return null;
/*      */   }
/*      */ 
/*      */   public X509Data itemX509Data(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  543 */     if (this.x509Datas != null) {
/*  544 */       return (X509Data)this.x509Datas.get(paramInt);
/*      */     }
/*  546 */     Element localElement = XMLUtils.selectDsNode(this._constructionElement.getFirstChild(), "X509Data", paramInt);
/*      */ 
/*  549 */     if (localElement != null) {
/*  550 */       return new X509Data(localElement, this._baseURI);
/*      */     }
/*  552 */     return null;
/*      */   }
/*      */ 
/*      */   public EncryptedKey itemEncryptedKey(int paramInt)
/*      */     throws XMLSecurityException
/*      */   {
/*  564 */     if (this.encryptedKeys != null) {
/*  565 */       return (EncryptedKey)this.encryptedKeys.get(paramInt);
/*      */     }
/*  567 */     Element localElement = XMLUtils.selectXencNode(this._constructionElement.getFirstChild(), "EncryptedKey", paramInt);
/*      */ 
/*  571 */     if (localElement != null) {
/*  572 */       XMLCipher localXMLCipher = XMLCipher.getInstance();
/*  573 */       localXMLCipher.init(4, null);
/*  574 */       return localXMLCipher.loadEncryptedKey(localElement);
/*      */     }
/*  576 */     return null;
/*      */   }
/*      */ 
/*      */   public Element itemUnknownElement(int paramInt)
/*      */   {
/*  587 */     NodeList localNodeList = this._constructionElement.getChildNodes();
/*  588 */     int i = 0;
/*      */ 
/*  590 */     for (int j = 0; j < localNodeList.getLength(); j++) {
/*  591 */       Node localNode = localNodeList.item(j);
/*      */ 
/*  597 */       if ((localNode.getNodeType() == 1) && (localNode.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")))
/*      */       {
/*  600 */         i++;
/*      */ 
/*  602 */         if (i == paramInt) {
/*  603 */           return (Element)localNode;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  608 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  617 */     return this._constructionElement.getFirstChild() == null;
/*      */   }
/*      */ 
/*      */   public boolean containsKeyName()
/*      */   {
/*  626 */     return lengthKeyName() > 0;
/*      */   }
/*      */ 
/*      */   public boolean containsKeyValue()
/*      */   {
/*  635 */     return lengthKeyValue() > 0;
/*      */   }
/*      */ 
/*      */   public boolean containsMgmtData()
/*      */   {
/*  644 */     return lengthMgmtData() > 0;
/*      */   }
/*      */ 
/*      */   public boolean containsPGPData()
/*      */   {
/*  653 */     return lengthPGPData() > 0;
/*      */   }
/*      */ 
/*      */   public boolean containsRetrievalMethod()
/*      */   {
/*  662 */     return lengthRetrievalMethod() > 0;
/*      */   }
/*      */ 
/*      */   public boolean containsSPKIData()
/*      */   {
/*  671 */     return lengthSPKIData() > 0;
/*      */   }
/*      */ 
/*      */   public boolean containsUnknownElement()
/*      */   {
/*  680 */     return lengthUnknownElement() > 0;
/*      */   }
/*      */ 
/*      */   public boolean containsX509Data()
/*      */   {
/*  689 */     return lengthX509Data() > 0;
/*      */   }
/*      */ 
/*      */   public PublicKey getPublicKey()
/*      */     throws KeyResolverException
/*      */   {
/*  701 */     PublicKey localPublicKey = getPublicKeyFromInternalResolvers();
/*      */ 
/*  703 */     if (localPublicKey != null) {
/*  704 */       log.log(Level.FINE, "I could find a key using the per-KeyInfo key resolvers");
/*      */ 
/*  706 */       return localPublicKey;
/*      */     }
/*  708 */     log.log(Level.FINE, "I couldn't find a key using the per-KeyInfo key resolvers");
/*      */ 
/*  710 */     localPublicKey = getPublicKeyFromStaticResolvers();
/*      */ 
/*  712 */     if (localPublicKey != null) {
/*  713 */       log.log(Level.FINE, "I could find a key using the system-wide key resolvers");
/*      */ 
/*  715 */       return localPublicKey;
/*      */     }
/*  717 */     log.log(Level.FINE, "I couldn't find a key using the system-wide key resolvers");
/*      */ 
/*  719 */     return null;
/*      */   }
/*      */ 
/*      */   PublicKey getPublicKeyFromStaticResolvers()
/*      */     throws KeyResolverException
/*      */   {
/*  729 */     int i = KeyResolver.length();
/*  730 */     int j = this._storageResolvers.size();
/*  731 */     Iterator localIterator = KeyResolver.iterator();
/*  732 */     for (int k = 0; k < i; k++) {
/*  733 */       KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator.next();
/*  734 */       Node localNode = this._constructionElement.getFirstChild();
/*  735 */       String str = getBaseURI();
/*  736 */       while (localNode != null) {
/*  737 */         if (localNode.getNodeType() == 1) {
/*  738 */           for (int m = 0; m < j; m++) {
/*  739 */             StorageResolver localStorageResolver = (StorageResolver)this._storageResolvers.get(m);
/*      */ 
/*  742 */             PublicKey localPublicKey = localKeyResolverSpi.engineLookupAndResolvePublicKey((Element)localNode, str, localStorageResolver);
/*      */ 
/*  747 */             if (localPublicKey != null) {
/*  748 */               KeyResolver.hit(localIterator);
/*  749 */               return localPublicKey;
/*      */             }
/*      */           }
/*      */         }
/*  753 */         localNode = localNode.getNextSibling();
/*      */       }
/*      */     }
/*  756 */     return null;
/*      */   }
/*      */ 
/*      */   PublicKey getPublicKeyFromInternalResolvers()
/*      */     throws KeyResolverException
/*      */   {
/*  766 */     int i = lengthInternalKeyResolver();
/*  767 */     int j = this._storageResolvers.size();
/*  768 */     for (int k = 0; k < i; k++) {
/*  769 */       KeyResolverSpi localKeyResolverSpi = itemInternalKeyResolver(k);
/*  770 */       if (log.isLoggable(Level.FINE)) {
/*  771 */         log.log(Level.FINE, "Try " + localKeyResolverSpi.getClass().getName());
/*      */       }
/*  773 */       Node localNode = this._constructionElement.getFirstChild();
/*  774 */       String str = getBaseURI();
/*  775 */       while (localNode != null) {
/*  776 */         if (localNode.getNodeType() == 1) {
/*  777 */           for (int m = 0; m < j; m++) {
/*  778 */             StorageResolver localStorageResolver = (StorageResolver)this._storageResolvers.get(m);
/*      */ 
/*  780 */             PublicKey localPublicKey = localKeyResolverSpi.engineLookupAndResolvePublicKey((Element)localNode, str, localStorageResolver);
/*      */ 
/*  783 */             if (localPublicKey != null) {
/*  784 */               return localPublicKey;
/*      */             }
/*      */           }
/*      */         }
/*  788 */         localNode = localNode.getNextSibling();
/*      */       }
/*      */     }
/*      */ 
/*  792 */     return null;
/*      */   }
/*      */ 
/*      */   public X509Certificate getX509Certificate()
/*      */     throws KeyResolverException
/*      */   {
/*  804 */     X509Certificate localX509Certificate = getX509CertificateFromInternalResolvers();
/*      */ 
/*  806 */     if (localX509Certificate != null) {
/*  807 */       log.log(Level.FINE, "I could find a X509Certificate using the per-KeyInfo key resolvers");
/*      */ 
/*  810 */       return localX509Certificate;
/*      */     }
/*  812 */     log.log(Level.FINE, "I couldn't find a X509Certificate using the per-KeyInfo key resolvers");
/*      */ 
/*  817 */     localX509Certificate = getX509CertificateFromStaticResolvers();
/*      */ 
/*  819 */     if (localX509Certificate != null) {
/*  820 */       log.log(Level.FINE, "I could find a X509Certificate using the system-wide key resolvers");
/*      */ 
/*  823 */       return localX509Certificate;
/*      */     }
/*  825 */     log.log(Level.FINE, "I couldn't find a X509Certificate using the system-wide key resolvers");
/*      */ 
/*  829 */     return null;
/*      */   }
/*      */ 
/*      */   X509Certificate getX509CertificateFromStaticResolvers()
/*      */     throws KeyResolverException
/*      */   {
/*  842 */     if (log.isLoggable(Level.FINE)) {
/*  843 */       log.log(Level.FINE, "Start getX509CertificateFromStaticResolvers() with " + KeyResolver.length() + " resolvers");
/*      */     }
/*  845 */     String str = getBaseURI();
/*  846 */     int i = KeyResolver.length();
/*  847 */     int j = this._storageResolvers.size();
/*  848 */     Iterator localIterator = KeyResolver.iterator();
/*  849 */     for (int k = 0; k < i; k++) {
/*  850 */       KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator.next();
/*  851 */       X509Certificate localX509Certificate = applyCurrentResolver(str, j, localKeyResolverSpi);
/*  852 */       if (localX509Certificate != null) {
/*  853 */         KeyResolver.hit(localIterator);
/*  854 */         return localX509Certificate;
/*      */       }
/*      */     }
/*  857 */     return null;
/*      */   }
/*      */ 
/*      */   private X509Certificate applyCurrentResolver(String paramString, int paramInt, KeyResolverSpi paramKeyResolverSpi) throws KeyResolverException {
/*  861 */     Node localNode = this._constructionElement.getFirstChild();
/*  862 */     while (localNode != null) {
/*  863 */       if (localNode.getNodeType() == 1) {
/*  864 */         for (int i = 0; i < paramInt; i++) {
/*  865 */           StorageResolver localStorageResolver = (StorageResolver)this._storageResolvers.get(i);
/*      */ 
/*  868 */           X509Certificate localX509Certificate = paramKeyResolverSpi.engineLookupResolveX509Certificate((Element)localNode, paramString, localStorageResolver);
/*      */ 
/*  872 */           if (localX509Certificate != null) {
/*  873 */             return localX509Certificate;
/*      */           }
/*      */         }
/*      */       }
/*  877 */       localNode = localNode.getNextSibling();
/*      */     }
/*  879 */     return null;
/*      */   }
/*      */ 
/*      */   X509Certificate getX509CertificateFromInternalResolvers()
/*      */     throws KeyResolverException
/*      */   {
/*  890 */     if (log.isLoggable(Level.FINE)) {
/*  891 */       log.log(Level.FINE, "Start getX509CertificateFromInternalResolvers() with " + lengthInternalKeyResolver() + " resolvers");
/*      */     }
/*  893 */     String str = getBaseURI();
/*  894 */     int i = this._storageResolvers.size();
/*  895 */     for (int j = 0; j < lengthInternalKeyResolver(); j++) {
/*  896 */       KeyResolverSpi localKeyResolverSpi = itemInternalKeyResolver(j);
/*  897 */       if (log.isLoggable(Level.FINE))
/*  898 */         log.log(Level.FINE, "Try " + localKeyResolverSpi.getClass().getName());
/*  899 */       X509Certificate localX509Certificate = applyCurrentResolver(str, i, localKeyResolverSpi);
/*  900 */       if (localX509Certificate != null) {
/*  901 */         return localX509Certificate;
/*      */       }
/*      */     }
/*      */ 
/*  905 */     return null;
/*      */   }
/*      */ 
/*      */   public SecretKey getSecretKey()
/*      */     throws KeyResolverException
/*      */   {
/*  914 */     SecretKey localSecretKey = getSecretKeyFromInternalResolvers();
/*      */ 
/*  916 */     if (localSecretKey != null) {
/*  917 */       log.log(Level.FINE, "I could find a secret key using the per-KeyInfo key resolvers");
/*      */ 
/*  919 */       return localSecretKey;
/*      */     }
/*  921 */     log.log(Level.FINE, "I couldn't find a secret key using the per-KeyInfo key resolvers");
/*      */ 
/*  924 */     localSecretKey = getSecretKeyFromStaticResolvers();
/*      */ 
/*  926 */     if (localSecretKey != null) {
/*  927 */       log.log(Level.FINE, "I could find a secret key using the system-wide key resolvers");
/*      */ 
/*  929 */       return localSecretKey;
/*      */     }
/*  931 */     log.log(Level.FINE, "I couldn't find a secret key using the system-wide key resolvers");
/*      */ 
/*  934 */     return null;
/*      */   }
/*      */ 
/*      */   SecretKey getSecretKeyFromStaticResolvers()
/*      */     throws KeyResolverException
/*      */   {
/*  945 */     int i = KeyResolver.length();
/*  946 */     int j = this._storageResolvers.size();
/*  947 */     Iterator localIterator = KeyResolver.iterator();
/*  948 */     for (int k = 0; k < i; k++) {
/*  949 */       KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator.next();
/*      */ 
/*  951 */       Node localNode = this._constructionElement.getFirstChild();
/*  952 */       String str = getBaseURI();
/*  953 */       while (localNode != null) {
/*  954 */         if (localNode.getNodeType() == 1) {
/*  955 */           for (int m = 0; m < j; m++) {
/*  956 */             StorageResolver localStorageResolver = (StorageResolver)this._storageResolvers.get(m);
/*      */ 
/*  959 */             SecretKey localSecretKey = localKeyResolverSpi.engineLookupAndResolveSecretKey((Element)localNode, str, localStorageResolver);
/*      */ 
/*  964 */             if (localSecretKey != null) {
/*  965 */               return localSecretKey;
/*      */             }
/*      */           }
/*      */         }
/*  969 */         localNode = localNode.getNextSibling();
/*      */       }
/*      */     }
/*  972 */     return null;
/*      */   }
/*      */ 
/*      */   SecretKey getSecretKeyFromInternalResolvers()
/*      */     throws KeyResolverException
/*      */   {
/*  983 */     int i = this._storageResolvers.size();
/*  984 */     for (int j = 0; j < lengthInternalKeyResolver(); j++) {
/*  985 */       KeyResolverSpi localKeyResolverSpi = itemInternalKeyResolver(j);
/*  986 */       if (log.isLoggable(Level.FINE)) {
/*  987 */         log.log(Level.FINE, "Try " + localKeyResolverSpi.getClass().getName());
/*      */       }
/*  989 */       Node localNode = this._constructionElement.getFirstChild();
/*  990 */       String str = getBaseURI();
/*  991 */       while (localNode != null) {
/*  992 */         if (localNode.getNodeType() == 1) {
/*  993 */           for (int k = 0; k < i; k++) {
/*  994 */             StorageResolver localStorageResolver = (StorageResolver)this._storageResolvers.get(k);
/*      */ 
/*  997 */             SecretKey localSecretKey = localKeyResolverSpi.engineLookupAndResolveSecretKey((Element)localNode, str, localStorageResolver);
/*      */ 
/* 1000 */             if (localSecretKey != null) {
/* 1001 */               return localSecretKey;
/*      */             }
/*      */           }
/*      */         }
/* 1005 */         localNode = localNode.getNextSibling();
/*      */       }
/*      */     }
/*      */ 
/* 1009 */     return null;
/*      */   }
/*      */ 
/*      */   public void registerInternalKeyResolver(KeyResolverSpi paramKeyResolverSpi)
/*      */   {
/* 1024 */     if (this._internalKeyResolvers == null) {
/* 1025 */       this._internalKeyResolvers = new ArrayList();
/*      */     }
/* 1027 */     this._internalKeyResolvers.add(paramKeyResolverSpi);
/*      */   }
/*      */ 
/*      */   int lengthInternalKeyResolver()
/*      */   {
/* 1035 */     if (this._internalKeyResolvers == null)
/* 1036 */       return 0;
/* 1037 */     return this._internalKeyResolvers.size();
/*      */   }
/*      */ 
/*      */   KeyResolverSpi itemInternalKeyResolver(int paramInt)
/*      */   {
/* 1047 */     return (KeyResolverSpi)this._internalKeyResolvers.get(paramInt);
/*      */   }
/*      */ 
/*      */   public void addStorageResolver(StorageResolver paramStorageResolver)
/*      */   {
/* 1059 */     if (this._storageResolvers == nullList) {
/* 1060 */       this._storageResolvers = new ArrayList();
/*      */     }
/* 1062 */     this._storageResolvers.add(paramStorageResolver);
/*      */   }
/*      */ 
/*      */   public static void init()
/*      */   {
/* 1071 */     if (!_alreadyInitialized) {
/* 1072 */       if (log == null)
/*      */       {
/* 1078 */         log = Logger.getLogger(KeyInfo.class.getName());
/*      */ 
/* 1081 */         log.log(Level.SEVERE, "Had to assign log in the init() function");
/*      */       }
/*      */ 
/* 1085 */       _alreadyInitialized = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getBaseLocalName()
/*      */   {
/* 1091 */     return "KeyInfo";
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  105 */     ArrayList localArrayList = new ArrayList();
/*  106 */     localArrayList.add(null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.keys.KeyInfo
 * JD-Core Version:    0.6.2
 */