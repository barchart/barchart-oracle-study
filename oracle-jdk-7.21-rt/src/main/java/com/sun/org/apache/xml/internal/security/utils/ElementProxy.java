/*     */ package com.sun.org.apache.xml.internal.security.utils;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.HashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public abstract class ElementProxy
/*     */ {
/*  47 */   static Logger log = Logger.getLogger(ElementProxy.class.getName());
/*     */ 
/*  65 */   protected Element _constructionElement = null;
/*     */ 
/*  68 */   protected String _baseURI = null;
/*     */ 
/*  71 */   protected Document _doc = null;
/*     */ 
/* 252 */   static ElementChecker checker = new ElementCheckerImpl.InternedNsChecker();
/*     */ 
/* 496 */   static HashMap _prefixMappings = new HashMap();
/* 497 */   static HashMap _prefixMappingsBindings = new HashMap();
/*     */ 
/*     */   public abstract String getBaseNamespace();
/*     */ 
/*     */   public abstract String getBaseLocalName();
/*     */ 
/*     */   public ElementProxy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ElementProxy(Document paramDocument)
/*     */   {
/*  86 */     if (paramDocument == null) {
/*  87 */       throw new RuntimeException("Document is null");
/*     */     }
/*     */ 
/*  90 */     this._doc = paramDocument;
/*  91 */     this._constructionElement = createElementForFamilyLocal(this._doc, getBaseNamespace(), getBaseLocalName());
/*     */   }
/*     */ 
/*     */   protected Element createElementForFamilyLocal(Document paramDocument, String paramString1, String paramString2)
/*     */   {
/*  96 */     Element localElement = null;
/*  97 */     if (paramString1 == null) {
/*  98 */       localElement = paramDocument.createElementNS(null, paramString2);
/*     */     } else {
/* 100 */       String str1 = getBaseNamespace();
/* 101 */       String str2 = getDefaultPrefix(str1);
/* 102 */       if ((str2 == null) || (str2.length() == 0)) {
/* 103 */         localElement = paramDocument.createElementNS(paramString1, paramString2);
/*     */ 
/* 105 */         localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", paramString1);
/*     */       }
/*     */       else {
/* 108 */         String str3 = null;
/* 109 */         String str4 = getDefaultPrefixBindings(str1);
/* 110 */         StringBuffer localStringBuffer = new StringBuffer(str2);
/* 111 */         localStringBuffer.append(':');
/* 112 */         localStringBuffer.append(paramString2);
/* 113 */         str3 = localStringBuffer.toString();
/* 114 */         localElement = paramDocument.createElementNS(paramString1, str3);
/*     */ 
/* 116 */         localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str4, paramString1);
/*     */       }
/*     */     }
/*     */ 
/* 120 */     return localElement;
/*     */   }
/*     */ 
/*     */   public static Element createElementForFamily(Document paramDocument, String paramString1, String paramString2)
/*     */   {
/* 139 */     Element localElement = null;
/* 140 */     String str = getDefaultPrefix(paramString1);
/*     */ 
/* 142 */     if (paramString1 == null) {
/* 143 */       localElement = paramDocument.createElementNS(null, paramString2);
/*     */     }
/* 145 */     else if ((str == null) || (str.length() == 0)) {
/* 146 */       localElement = paramDocument.createElementNS(paramString1, paramString2);
/*     */ 
/* 148 */       localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", paramString1);
/*     */     }
/*     */     else {
/* 151 */       localElement = paramDocument.createElementNS(paramString1, str + ":" + paramString2);
/*     */ 
/* 153 */       localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", getDefaultPrefixBindings(paramString1), paramString1);
/*     */     }
/*     */ 
/* 158 */     return localElement;
/*     */   }
/*     */ 
/*     */   public void setElement(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/* 171 */     if (paramElement == null) {
/* 172 */       throw new XMLSecurityException("ElementProxy.nullElement");
/*     */     }
/*     */ 
/* 175 */     if (log.isLoggable(Level.FINE)) {
/* 176 */       log.log(Level.FINE, "setElement(" + paramElement.getTagName() + ", \"" + paramString + "\"");
/*     */     }
/*     */ 
/* 179 */     this._doc = paramElement.getOwnerDocument();
/* 180 */     this._constructionElement = paramElement;
/* 181 */     this._baseURI = paramString;
/*     */   }
/*     */ 
/*     */   public ElementProxy(Element paramElement, String paramString)
/*     */     throws XMLSecurityException
/*     */   {
/* 193 */     if (paramElement == null) {
/* 194 */       throw new XMLSecurityException("ElementProxy.nullElement");
/*     */     }
/*     */ 
/* 197 */     if (log.isLoggable(Level.FINE)) {
/* 198 */       log.log(Level.FINE, "setElement(\"" + paramElement.getTagName() + "\", \"" + paramString + "\")");
/*     */     }
/*     */ 
/* 202 */     this._doc = paramElement.getOwnerDocument();
/* 203 */     this._constructionElement = paramElement;
/* 204 */     this._baseURI = paramString;
/*     */ 
/* 206 */     guaranteeThatElementInCorrectSpace();
/*     */   }
/*     */ 
/*     */   public final Element getElement()
/*     */   {
/* 215 */     return this._constructionElement;
/*     */   }
/*     */ 
/*     */   public final NodeList getElementPlusReturns()
/*     */   {
/* 225 */     HelperNodeList localHelperNodeList = new HelperNodeList();
/*     */ 
/* 227 */     localHelperNodeList.appendChild(this._doc.createTextNode("\n"));
/* 228 */     localHelperNodeList.appendChild(getElement());
/* 229 */     localHelperNodeList.appendChild(this._doc.createTextNode("\n"));
/*     */ 
/* 231 */     return localHelperNodeList;
/*     */   }
/*     */ 
/*     */   public Document getDocument()
/*     */   {
/* 240 */     return this._doc;
/*     */   }
/*     */ 
/*     */   public String getBaseURI()
/*     */   {
/* 249 */     return this._baseURI;
/*     */   }
/*     */ 
/*     */   void guaranteeThatElementInCorrectSpace()
/*     */     throws XMLSecurityException
/*     */   {
/* 262 */     checker.guaranteeThatElementInCorrectSpace(this, this._constructionElement);
/*     */   }
/*     */ 
/*     */   public void addBigIntegerElement(BigInteger paramBigInteger, String paramString)
/*     */   {
/* 274 */     if (paramBigInteger != null) {
/* 275 */       Element localElement = XMLUtils.createElementInSignatureSpace(this._doc, paramString);
/*     */ 
/* 278 */       Base64.fillElementWithBigInteger(localElement, paramBigInteger);
/* 279 */       this._constructionElement.appendChild(localElement);
/* 280 */       XMLUtils.addReturnToElement(this._constructionElement);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addBase64Element(byte[] paramArrayOfByte, String paramString)
/*     */   {
/* 292 */     if (paramArrayOfByte != null)
/*     */     {
/* 294 */       Element localElement = Base64.encodeToElement(this._doc, paramString, paramArrayOfByte);
/*     */ 
/* 296 */       this._constructionElement.appendChild(localElement);
/* 297 */       if (!XMLUtils.ignoreLineBreaks())
/* 298 */         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addTextElement(String paramString1, String paramString2)
/*     */   {
/* 311 */     Element localElement = XMLUtils.createElementInSignatureSpace(this._doc, paramString2);
/* 312 */     Text localText = this._doc.createTextNode(paramString1);
/*     */ 
/* 314 */     localElement.appendChild(localText);
/* 315 */     this._constructionElement.appendChild(localElement);
/* 316 */     XMLUtils.addReturnToElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public void addBase64Text(byte[] paramArrayOfByte)
/*     */   {
/* 326 */     if (paramArrayOfByte != null) {
/* 327 */       Text localText = XMLUtils.ignoreLineBreaks() ? this._doc.createTextNode(Base64.encode(paramArrayOfByte)) : this._doc.createTextNode("\n" + Base64.encode(paramArrayOfByte) + "\n");
/*     */ 
/* 330 */       this._constructionElement.appendChild(localText);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addText(String paramString)
/*     */   {
/* 341 */     if (paramString != null) {
/* 342 */       Text localText = this._doc.createTextNode(paramString);
/*     */ 
/* 344 */       this._constructionElement.appendChild(localText);
/*     */     }
/*     */   }
/*     */ 
/*     */   public BigInteger getBigIntegerFromChildElement(String paramString1, String paramString2)
/*     */     throws Base64DecodingException
/*     */   {
/* 359 */     return Base64.decodeBigIntegerFromText(XMLUtils.selectNodeText(this._constructionElement.getFirstChild(), paramString2, paramString1, 0));
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public byte[] getBytesFromChildElement(String paramString1, String paramString2)
/*     */     throws XMLSecurityException
/*     */   {
/* 376 */     Element localElement = XMLUtils.selectNode(this._constructionElement.getFirstChild(), paramString2, paramString1, 0);
/*     */ 
/* 383 */     return Base64.decode(localElement);
/*     */   }
/*     */ 
/*     */   public String getTextFromChildElement(String paramString1, String paramString2)
/*     */   {
/* 395 */     Text localText = (Text)XMLUtils.selectNode(this._constructionElement.getFirstChild(), paramString2, paramString1, 0).getFirstChild();
/*     */ 
/* 402 */     return localText.getData();
/*     */   }
/*     */ 
/*     */   public byte[] getBytesFromTextChild()
/*     */     throws XMLSecurityException
/*     */   {
/* 412 */     return Base64.decode(XMLUtils.getFullTextChildrenFromElement(this._constructionElement));
/*     */   }
/*     */ 
/*     */   public String getTextFromTextChild()
/*     */   {
/* 423 */     return XMLUtils.getFullTextChildrenFromElement(this._constructionElement);
/*     */   }
/*     */ 
/*     */   public int length(String paramString1, String paramString2)
/*     */   {
/* 434 */     int i = 0;
/* 435 */     Node localNode = this._constructionElement.getFirstChild();
/* 436 */     while (localNode != null) {
/* 437 */       if ((paramString2.equals(localNode.getLocalName())) && (paramString1 == localNode.getNamespaceURI()))
/*     */       {
/* 440 */         i++;
/*     */       }
/* 442 */       localNode = localNode.getNextSibling();
/*     */     }
/* 444 */     return i;
/*     */   }
/*     */ 
/*     */   public void setXPathNamespaceContext(String paramString1, String paramString2)
/*     */     throws XMLSecurityException
/*     */   {
/* 465 */     if ((paramString1 == null) || (paramString1.length() == 0))
/* 466 */       throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
/* 467 */     if (paramString1.equals("xmlns"))
/* 468 */       throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
/*     */     String str;
/* 469 */     if (paramString1.startsWith("xmlns:"))
/* 470 */       str = paramString1;
/*     */     else {
/* 472 */       str = "xmlns:" + paramString1;
/*     */     }
/*     */ 
/* 477 */     Attr localAttr = this._constructionElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", str);
/*     */ 
/* 479 */     if (localAttr != null) {
/* 480 */       if (!localAttr.getNodeValue().equals(paramString2)) {
/* 481 */         Object[] arrayOfObject = { str, this._constructionElement.getAttributeNS(null, str) };
/*     */ 
/* 485 */         throw new XMLSecurityException("namespacePrefixAlreadyUsedByOtherURI", arrayOfObject);
/*     */       }
/*     */ 
/* 488 */       return;
/*     */     }
/*     */ 
/* 491 */     this._constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString2);
/*     */   }
/*     */ 
/*     */   public static void setDefaultPrefix(String paramString1, String paramString2)
/*     */     throws XMLSecurityException
/*     */   {
/* 509 */     if (_prefixMappings.containsValue(paramString2))
/*     */     {
/* 511 */       Object localObject = _prefixMappings.get(paramString1);
/* 512 */       if (!localObject.equals(paramString2)) {
/* 513 */         Object[] arrayOfObject = { paramString2, paramString1, localObject };
/*     */ 
/* 515 */         throw new XMLSecurityException("prefix.AlreadyAssigned", arrayOfObject);
/*     */       }
/*     */     }
/* 518 */     if ("http://www.w3.org/2000/09/xmldsig#".equals(paramString1)) {
/* 519 */       XMLUtils.dsPrefix = paramString2;
/*     */     }
/* 521 */     _prefixMappings.put(paramString1, paramString2.intern());
/* 522 */     if (paramString2.length() == 0)
/* 523 */       _prefixMappingsBindings.put(paramString1, "xmlns");
/*     */     else
/* 525 */       _prefixMappingsBindings.put(paramString1, ("xmlns:" + paramString2).intern());
/*     */   }
/*     */ 
/*     */   public static String getDefaultPrefix(String paramString)
/*     */   {
/* 536 */     return (String)_prefixMappings.get(paramString);
/*     */   }
/*     */ 
/*     */   public static String getDefaultPrefixBindings(String paramString) {
/* 540 */     return (String)_prefixMappingsBindings.get(paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.ElementProxy
 * JD-Core Version:    0.6.2
 */