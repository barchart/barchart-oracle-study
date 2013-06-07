/*     */ package com.sun.org.apache.xerces.internal.impl;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
/*     */ import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
/*     */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*     */ import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
/*     */ import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
/*     */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*     */ import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
/*     */ import com.sun.org.apache.xerces.internal.xni.QName;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*     */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
/*     */ import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class XMLNSDocumentScannerImpl extends XMLDocumentScannerImpl
/*     */ {
/*     */   protected boolean fBindNamespaces;
/*     */   protected boolean fPerformValidation;
/*     */   protected boolean fNotAddNSDeclAsAttribute;
/*     */   private XMLDTDValidatorFilter fDTDValidator;
/*     */   private boolean fXmlnsDeclared;
/*     */ 
/*     */   public XMLNSDocumentScannerImpl()
/*     */   {
/*  77 */     this.fNotAddNSDeclAsAttribute = false;
/*     */ 
/*  83 */     this.fXmlnsDeclared = false;
/*     */   }
/*     */ 
/*     */   public void reset(PropertyManager propertyManager)
/*     */   {
/*  88 */     setPropertyManager(propertyManager);
/*  89 */     super.reset(propertyManager);
/*  90 */     this.fBindNamespaces = false;
/*  91 */     this.fNotAddNSDeclAsAttribute = (!((Boolean)propertyManager.getProperty("add-namespacedecl-as-attrbiute")).booleanValue());
/*     */   }
/*     */ 
/*     */   public void reset(XMLComponentManager componentManager) throws XMLConfigurationException
/*     */   {
/*  96 */     super.reset(componentManager);
/*  97 */     this.fNotAddNSDeclAsAttribute = false;
/*  98 */     this.fPerformValidation = false;
/*  99 */     this.fBindNamespaces = false;
/*     */   }
/*     */ 
/*     */   public int next()
/*     */     throws IOException, XNIException
/*     */   {
/* 111 */     if ((this.fScannerLastState == 2) && (this.fBindNamespaces)) {
/* 112 */       this.fScannerLastState = -1;
/* 113 */       this.fNamespaceContext.popContext();
/*     */     }
/*     */ 
/* 116 */     return this.fScannerLastState = super.next();
/*     */   }
/*     */ 
/*     */   public void setDTDValidator(XMLDTDValidatorFilter dtd)
/*     */   {
/* 129 */     this.fDTDValidator = dtd;
/*     */   }
/*     */ 
/*     */   protected boolean scanStartElement()
/*     */     throws IOException, XNIException
/*     */   {
/* 160 */     if ((this.fSkip) && (!this.fAdd))
/*     */     {
/* 164 */       QName name = this.fElementStack.getNext();
/*     */ 
/* 171 */       this.fSkip = this.fEntityScanner.skipString(name.rawname);
/*     */ 
/* 173 */       if (this.fSkip)
/*     */       {
/* 177 */         this.fElementStack.push();
/* 178 */         this.fElementQName = name;
/*     */       }
/*     */       else {
/* 181 */         this.fElementStack.reposition();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 191 */     if ((!this.fSkip) || (this.fAdd))
/*     */     {
/* 193 */       this.fElementQName = this.fElementStack.nextElement();
/*     */ 
/* 196 */       if (this.fNamespaces) {
/* 197 */         this.fEntityScanner.scanQName(this.fElementQName);
/*     */       } else {
/* 199 */         String name = this.fEntityScanner.scanName();
/* 200 */         this.fElementQName.setValues(null, name, name, null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 213 */     if (this.fAdd)
/*     */     {
/* 215 */       this.fElementStack.matchElement(this.fElementQName);
/*     */     }
/*     */ 
/* 219 */     this.fCurrentElement = this.fElementQName;
/*     */ 
/* 221 */     String rawname = this.fElementQName.rawname;
/* 222 */     if (this.fBindNamespaces) {
/* 223 */       this.fNamespaceContext.pushContext();
/* 224 */       if ((this.fScannerState == 26) && 
/* 225 */         (this.fPerformValidation)) {
/* 226 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { rawname }, (short)1);
/*     */ 
/* 231 */         if ((this.fDoctypeName == null) || (!this.fDoctypeName.equals(rawname))) {
/* 232 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, rawname }, (short)1);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 242 */     this.fEmptyElement = false;
/* 243 */     this.fAttributes.removeAllAttributes();
/*     */ 
/* 245 */     if (!seekCloseOfStartTag()) {
/* 246 */       this.fReadingAttributes = true;
/* 247 */       this.fAttributeCacheUsedCount = 0;
/* 248 */       this.fStringBufferIndex = 0;
/* 249 */       this.fAddDefaultAttr = true;
/* 250 */       this.fXmlnsDeclared = false;
/*     */       do
/*     */       {
/* 253 */         scanAttribute(this.fAttributes);
/* 254 */         if ((this.fSecurityManager != null) && (this.fAttributes.getLength() > this.fElementAttributeLimit)) {
/* 255 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { rawname, new Integer(this.fAttributes.getLength()) }, (short)2);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 261 */       while (!seekCloseOfStartTag());
/* 262 */       this.fReadingAttributes = false;
/*     */     }
/*     */ 
/* 265 */     if (this.fBindNamespaces)
/*     */     {
/* 267 */       if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
/* 268 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2);
/*     */       }
/*     */ 
/* 275 */       String prefix = this.fElementQName.prefix != null ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 278 */       this.fElementQName.uri = this.fNamespaceContext.getURI(prefix);
/*     */ 
/* 280 */       this.fCurrentElement.uri = this.fElementQName.uri;
/*     */ 
/* 282 */       if ((this.fElementQName.prefix == null) && (this.fElementQName.uri != null)) {
/* 283 */         this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
/*     */       }
/* 285 */       if ((this.fElementQName.prefix != null) && (this.fElementQName.uri == null)) {
/* 286 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { this.fElementQName.prefix, this.fElementQName.rawname }, (short)2);
/*     */       }
/*     */ 
/* 293 */       int length = this.fAttributes.getLength();
/*     */ 
/* 295 */       for (int i = 0; i < length; i++) {
/* 296 */         this.fAttributes.getName(i, this.fAttributeQName);
/*     */ 
/* 298 */         String aprefix = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 300 */         String uri = this.fNamespaceContext.getURI(aprefix);
/*     */ 
/* 303 */         if ((this.fAttributeQName.uri == null) || (this.fAttributeQName.uri != uri))
/*     */         {
/* 307 */           if (aprefix != XMLSymbols.EMPTY_STRING) {
/* 308 */             this.fAttributeQName.uri = uri;
/* 309 */             if (uri == null) {
/* 310 */               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, aprefix }, (short)2);
/*     */             }
/*     */ 
/* 315 */             this.fAttributes.setURI(i, uri);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 320 */       if (length > 1) {
/* 321 */         QName name = this.fAttributes.checkDuplicatesNS();
/* 322 */         if (name != null) {
/* 323 */           if (name.uri != null) {
/* 324 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, name.localpart, name.uri }, (short)2);
/*     */           }
/*     */           else
/*     */           {
/* 329 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, name.rawname }, (short)2);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 339 */     if (this.fEmptyElement)
/*     */     {
/* 341 */       this.fMarkupDepth -= 1;
/*     */ 
/* 344 */       if (this.fMarkupDepth < this.fEntityStack[(this.fEntityDepth - 1)]) {
/* 345 */         reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
/*     */       }
/*     */ 
/* 349 */       if (this.fDocumentHandler != null)
/*     */       {
/* 353 */         this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
/*     */       }
/*     */ 
/* 358 */       this.fScanEndElement = true;
/*     */ 
/* 364 */       this.fElementStack.popElement();
/*     */     }
/*     */     else
/*     */     {
/* 368 */       if (this.dtdGrammarUtil != null)
/* 369 */         this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes);
/* 370 */       if (this.fDocumentHandler != null)
/*     */       {
/* 376 */         this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 382 */     return this.fEmptyElement;
/*     */   }
/*     */ 
/*     */   protected void scanAttribute(XMLAttributesImpl attributes)
/*     */     throws IOException, XNIException
/*     */   {
/* 410 */     this.fEntityScanner.scanQName(this.fAttributeQName);
/*     */ 
/* 413 */     this.fEntityScanner.skipSpaces();
/* 414 */     if (!this.fEntityScanner.skipChar(61)) {
/* 415 */       reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*     */     }
/*     */ 
/* 418 */     this.fEntityScanner.skipSpaces();
/*     */ 
/* 421 */     int attrIndex = 0;
/*     */ 
/* 425 */     boolean isVC = (this.fHasExternalDTD) && (!this.fStandalone);
/*     */ 
/* 435 */     XMLString tmpStr = getString();
/* 436 */     scanAttributeValue(tmpStr, this.fTempString2, this.fAttributeQName.rawname, attributes, attrIndex, isVC);
/*     */ 
/* 440 */     String value = null;
/*     */ 
/* 444 */     if (this.fBindNamespaces)
/*     */     {
/* 446 */       String localpart = this.fAttributeQName.localpart;
/* 447 */       String prefix = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 451 */       if ((prefix == XMLSymbols.PREFIX_XMLNS) || ((prefix == XMLSymbols.EMPTY_STRING) && (localpart == XMLSymbols.PREFIX_XMLNS)))
/*     */       {
/* 455 */         String uri = this.fSymbolTable.addSymbol(tmpStr.ch, tmpStr.offset, tmpStr.length);
/* 456 */         value = uri;
/*     */ 
/* 458 */         if ((prefix == XMLSymbols.PREFIX_XMLNS) && (localpart == XMLSymbols.PREFIX_XMLNS)) {
/* 459 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
/*     */         }
/*     */ 
/* 466 */         if (uri == NamespaceContext.XMLNS_URI) {
/* 467 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
/*     */         }
/*     */ 
/* 474 */         if (localpart == XMLSymbols.PREFIX_XML) {
/* 475 */           if (uri != NamespaceContext.XML_URI) {
/* 476 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
/*     */           }
/*     */ 
/*     */         }
/* 484 */         else if (uri == NamespaceContext.XML_URI) {
/* 485 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
/*     */         }
/*     */ 
/* 491 */         prefix = localpart != XMLSymbols.PREFIX_XMLNS ? localpart : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 495 */         if ((prefix == XMLSymbols.EMPTY_STRING) && (localpart == XMLSymbols.PREFIX_XMLNS)) {
/* 496 */           this.fAttributeQName.prefix = XMLSymbols.PREFIX_XMLNS;
/*     */         }
/*     */ 
/* 501 */         if ((uri == XMLSymbols.EMPTY_STRING) && (localpart != XMLSymbols.PREFIX_XMLNS)) {
/* 502 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { this.fAttributeQName }, (short)2);
/*     */         }
/*     */ 
/* 509 */         if (((NamespaceSupport)this.fNamespaceContext).containsPrefixInCurrentContext(prefix)) {
/* 510 */           reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*     */         }
/*     */ 
/* 516 */         boolean declared = this.fNamespaceContext.declarePrefix(prefix, uri.length() != 0 ? uri : null);
/*     */ 
/* 519 */         if (!declared)
/*     */         {
/* 521 */           if (this.fXmlnsDeclared) {
/* 522 */             reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*     */           }
/*     */ 
/* 528 */           this.fXmlnsDeclared = true;
/*     */         }
/*     */ 
/* 536 */         if (this.fNotAddNSDeclAsAttribute) {
/* 537 */           return;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 543 */     if (this.fBindNamespaces) {
/* 544 */       attrIndex = attributes.getLength();
/* 545 */       attributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
/*     */     } else {
/* 547 */       int oldLen = attributes.getLength();
/* 548 */       attrIndex = attributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
/*     */ 
/* 551 */       if (oldLen == attributes.getLength()) {
/* 552 */         reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 558 */     attributes.setValue(attrIndex, value, tmpStr);
/*     */ 
/* 561 */     attributes.setSpecified(attrIndex, true);
/*     */ 
/* 564 */     if (this.fAttributeQName.prefix != null)
/* 565 */       attributes.setURI(attrIndex, this.fNamespaceContext.getURI(this.fAttributeQName.prefix));
/*     */   }
/*     */ 
/*     */   protected XMLDocumentFragmentScannerImpl.Driver createContentDriver()
/*     */   {
/* 577 */     return new NSContentDriver();
/*     */   }
/*     */ 
/*     */   protected final class NSContentDriver extends XMLDocumentScannerImpl.ContentDriver
/*     */   {
/*     */     protected NSContentDriver() {
/* 583 */       super();
/*     */     }
/*     */ 
/*     */     protected boolean scanRootElementHook()
/*     */       throws IOException, XNIException
/*     */     {
/* 601 */       reconfigurePipeline();
/* 602 */       if (XMLNSDocumentScannerImpl.this.scanStartElement()) {
/* 603 */         XMLNSDocumentScannerImpl.this.setScannerState(44);
/* 604 */         XMLNSDocumentScannerImpl.this.setDriver(XMLNSDocumentScannerImpl.this.fTrailingMiscDriver);
/* 605 */         return true;
/*     */       }
/* 607 */       return false;
/*     */     }
/*     */ 
/*     */     private void reconfigurePipeline()
/*     */     {
/* 619 */       if ((XMLNSDocumentScannerImpl.this.fNamespaces) && (XMLNSDocumentScannerImpl.this.fDTDValidator == null)) {
/* 620 */         XMLNSDocumentScannerImpl.this.fBindNamespaces = true;
/*     */       }
/* 622 */       else if ((XMLNSDocumentScannerImpl.this.fNamespaces) && (!XMLNSDocumentScannerImpl.this.fDTDValidator.hasGrammar())) {
/* 623 */         XMLNSDocumentScannerImpl.this.fBindNamespaces = true;
/* 624 */         XMLNSDocumentScannerImpl.this.fPerformValidation = XMLNSDocumentScannerImpl.this.fDTDValidator.validate();
/*     */ 
/* 626 */         XMLDocumentSource source = XMLNSDocumentScannerImpl.this.fDTDValidator.getDocumentSource();
/* 627 */         XMLDocumentHandler handler = XMLNSDocumentScannerImpl.this.fDTDValidator.getDocumentHandler();
/* 628 */         source.setDocumentHandler(handler);
/* 629 */         if (handler != null)
/* 630 */           handler.setDocumentSource(source);
/* 631 */         XMLNSDocumentScannerImpl.this.fDTDValidator.setDocumentSource(null);
/* 632 */         XMLNSDocumentScannerImpl.this.fDTDValidator.setDocumentHandler(null);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl
 * JD-Core Version:    0.6.2
 */