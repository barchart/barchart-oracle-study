/*     */ package com.sun.org.apache.xerces.internal.impl;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
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
/*     */ public class XML11NSDocumentScannerImpl extends XML11DocumentScannerImpl
/*     */ {
/*     */   protected boolean fBindNamespaces;
/*     */   protected boolean fPerformValidation;
/*     */   private XMLDTDValidatorFilter fDTDValidator;
/*     */   private boolean fSawSpace;
/*     */ 
/*     */   public void setDTDValidator(XMLDTDValidatorFilter validator)
/*     */   {
/* 149 */     this.fDTDValidator = validator;
/*     */   }
/*     */ 
/*     */   protected boolean scanStartElement()
/*     */     throws IOException, XNIException
/*     */   {
/* 178 */     this.fEntityScanner.scanQName(this.fElementQName);
/*     */ 
/* 180 */     String rawname = this.fElementQName.rawname;
/* 181 */     if (this.fBindNamespaces) {
/* 182 */       this.fNamespaceContext.pushContext();
/* 183 */       if ((this.fScannerState == 26) && 
/* 184 */         (this.fPerformValidation)) {
/* 185 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { rawname }, (short)1);
/*     */ 
/* 191 */         if ((this.fDoctypeName == null) || (!this.fDoctypeName.equals(rawname)))
/*     */         {
/* 193 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, rawname }, (short)1);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 204 */     this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
/*     */ 
/* 207 */     boolean empty = false;
/* 208 */     this.fAttributes.removeAllAttributes();
/*     */     while (true)
/*     */     {
/* 211 */       boolean sawSpace = this.fEntityScanner.skipSpaces();
/*     */ 
/* 214 */       int c = this.fEntityScanner.peekChar();
/* 215 */       if (c == 62) {
/* 216 */         this.fEntityScanner.scanChar();
/* 217 */         break;
/* 218 */       }if (c == 47) {
/* 219 */         this.fEntityScanner.scanChar();
/* 220 */         if (!this.fEntityScanner.skipChar(62)) {
/* 221 */           reportFatalError("ElementUnterminated", new Object[] { rawname });
/*     */         }
/*     */ 
/* 225 */         empty = true;
/* 226 */         break;
/* 227 */       }if ((!isValidNameStartChar(c)) || (!sawSpace))
/*     */       {
/* 230 */         if ((!isValidNameStartHighSurrogate(c)) || (!sawSpace)) {
/* 231 */           reportFatalError("ElementUnterminated", new Object[] { rawname });
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 238 */       scanAttribute(this.fAttributes);
/* 239 */       if ((this.fSecurityManager != null) && (this.fAttributes.getLength() > this.fElementAttributeLimit)) {
/* 240 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { rawname, new Integer(this.fElementAttributeLimit) }, (short)2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 248 */     if (this.fBindNamespaces)
/*     */     {
/* 250 */       if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
/* 251 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2);
/*     */       }
/*     */ 
/* 259 */       String prefix = this.fElementQName.prefix != null ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 264 */       this.fElementQName.uri = this.fNamespaceContext.getURI(prefix);
/*     */ 
/* 266 */       this.fCurrentElement.uri = this.fElementQName.uri;
/*     */ 
/* 268 */       if ((this.fElementQName.prefix == null) && (this.fElementQName.uri != null)) {
/* 269 */         this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
/*     */ 
/* 271 */         this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
/*     */       }
/* 273 */       if ((this.fElementQName.prefix != null) && (this.fElementQName.uri == null)) {
/* 274 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { this.fElementQName.prefix, this.fElementQName.rawname }, (short)2);
/*     */       }
/*     */ 
/* 284 */       int length = this.fAttributes.getLength();
/* 285 */       for (int i = 0; i < length; i++) {
/* 286 */         this.fAttributes.getName(i, this.fAttributeQName);
/*     */ 
/* 288 */         String aprefix = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 292 */         String uri = this.fNamespaceContext.getURI(aprefix);
/*     */ 
/* 295 */         if ((this.fAttributeQName.uri == null) || (this.fAttributeQName.uri != uri))
/*     */         {
/* 299 */           if (aprefix != XMLSymbols.EMPTY_STRING) {
/* 300 */             this.fAttributeQName.uri = uri;
/* 301 */             if (uri == null) {
/* 302 */               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, aprefix }, (short)2);
/*     */             }
/*     */ 
/* 311 */             this.fAttributes.setURI(i, uri);
/*     */           }
/*     */         }
/*     */       }
/* 315 */       if (length > 1) {
/* 316 */         QName name = this.fAttributes.checkDuplicatesNS();
/* 317 */         if (name != null) {
/* 318 */           if (name.uri != null) {
/* 319 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, name.localpart, name.uri }, (short)2);
/*     */           }
/*     */           else
/*     */           {
/* 328 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, name.rawname }, (short)2);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 342 */     if (empty)
/*     */     {
/* 345 */       this.fMarkupDepth -= 1;
/*     */ 
/* 348 */       if (this.fMarkupDepth < this.fEntityStack[(this.fEntityDepth - 1)]) {
/* 349 */         reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
/*     */       }
/*     */ 
/* 354 */       this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
/*     */ 
/* 359 */       this.fScanEndElement = true;
/*     */ 
/* 362 */       this.fElementStack.popElement();
/*     */     }
/*     */     else {
/* 365 */       if (this.dtdGrammarUtil != null) {
/* 366 */         this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes);
/*     */       }
/* 368 */       if (this.fDocumentHandler != null) {
/* 369 */         this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 374 */     return empty;
/*     */   }
/*     */ 
/*     */   protected void scanStartElementName()
/*     */     throws IOException, XNIException
/*     */   {
/* 386 */     this.fEntityScanner.scanQName(this.fElementQName);
/*     */ 
/* 389 */     this.fSawSpace = this.fEntityScanner.skipSpaces();
/*     */   }
/*     */ 
/*     */   protected boolean scanStartElementAfterName()
/*     */     throws IOException, XNIException
/*     */   {
/* 402 */     String rawname = this.fElementQName.rawname;
/* 403 */     if (this.fBindNamespaces) {
/* 404 */       this.fNamespaceContext.pushContext();
/* 405 */       if ((this.fScannerState == 26) && 
/* 406 */         (this.fPerformValidation)) {
/* 407 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { rawname }, (short)1);
/*     */ 
/* 413 */         if ((this.fDoctypeName == null) || (!this.fDoctypeName.equals(rawname)))
/*     */         {
/* 415 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, rawname }, (short)1);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 426 */     this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
/*     */ 
/* 429 */     boolean empty = false;
/* 430 */     this.fAttributes.removeAllAttributes();
/*     */     while (true)
/*     */     {
/* 434 */       int c = this.fEntityScanner.peekChar();
/* 435 */       if (c == 62) {
/* 436 */         this.fEntityScanner.scanChar();
/* 437 */         break;
/* 438 */       }if (c == 47) {
/* 439 */         this.fEntityScanner.scanChar();
/* 440 */         if (!this.fEntityScanner.skipChar(62)) {
/* 441 */           reportFatalError("ElementUnterminated", new Object[] { rawname });
/*     */         }
/*     */ 
/* 445 */         empty = true;
/* 446 */         break;
/* 447 */       }if ((!isValidNameStartChar(c)) || (!this.fSawSpace))
/*     */       {
/* 450 */         if ((!isValidNameStartHighSurrogate(c)) || (!this.fSawSpace)) {
/* 451 */           reportFatalError("ElementUnterminated", new Object[] { rawname });
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 458 */       scanAttribute(this.fAttributes);
/*     */ 
/* 461 */       this.fSawSpace = this.fEntityScanner.skipSpaces();
/*     */     }
/*     */ 
/* 465 */     if (this.fBindNamespaces)
/*     */     {
/* 467 */       if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
/* 468 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2);
/*     */       }
/*     */ 
/* 476 */       String prefix = this.fElementQName.prefix != null ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 481 */       this.fElementQName.uri = this.fNamespaceContext.getURI(prefix);
/*     */ 
/* 483 */       this.fCurrentElement.uri = this.fElementQName.uri;
/*     */ 
/* 485 */       if ((this.fElementQName.prefix == null) && (this.fElementQName.uri != null)) {
/* 486 */         this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
/*     */ 
/* 488 */         this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
/*     */       }
/* 490 */       if ((this.fElementQName.prefix != null) && (this.fElementQName.uri == null)) {
/* 491 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { this.fElementQName.prefix, this.fElementQName.rawname }, (short)2);
/*     */       }
/*     */ 
/* 501 */       int length = this.fAttributes.getLength();
/* 502 */       for (int i = 0; i < length; i++) {
/* 503 */         this.fAttributes.getName(i, this.fAttributeQName);
/*     */ 
/* 505 */         String aprefix = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 509 */         String uri = this.fNamespaceContext.getURI(aprefix);
/*     */ 
/* 512 */         if ((this.fAttributeQName.uri == null) || (this.fAttributeQName.uri != uri))
/*     */         {
/* 516 */           if (aprefix != XMLSymbols.EMPTY_STRING) {
/* 517 */             this.fAttributeQName.uri = uri;
/* 518 */             if (uri == null) {
/* 519 */               this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, aprefix }, (short)2);
/*     */             }
/*     */ 
/* 528 */             this.fAttributes.setURI(i, uri);
/*     */           }
/*     */         }
/*     */       }
/* 532 */       if (length > 1) {
/* 533 */         QName name = this.fAttributes.checkDuplicatesNS();
/* 534 */         if (name != null) {
/* 535 */           if (name.uri != null) {
/* 536 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, name.localpart, name.uri }, (short)2);
/*     */           }
/*     */           else
/*     */           {
/* 545 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, name.rawname }, (short)2);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 558 */     if (this.fDocumentHandler != null) {
/* 559 */       if (empty)
/*     */       {
/* 562 */         this.fMarkupDepth -= 1;
/*     */ 
/* 565 */         if (this.fMarkupDepth < this.fEntityStack[(this.fEntityDepth - 1)]) {
/* 566 */           reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
/*     */         }
/*     */ 
/* 571 */         this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
/*     */ 
/* 573 */         if (this.fBindNamespaces) {
/* 574 */           this.fNamespaceContext.popContext();
/*     */         }
/*     */ 
/* 577 */         this.fElementStack.popElement();
/*     */       } else {
/* 579 */         this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 585 */     return empty;
/*     */   }
/*     */ 
/*     */   protected void scanAttribute(XMLAttributesImpl attributes)
/*     */     throws IOException, XNIException
/*     */   {
/* 612 */     this.fEntityScanner.scanQName(this.fAttributeQName);
/*     */ 
/* 615 */     this.fEntityScanner.skipSpaces();
/* 616 */     if (!this.fEntityScanner.skipChar(61)) {
/* 617 */       reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*     */     }
/*     */ 
/* 623 */     this.fEntityScanner.skipSpaces();
/*     */     int attrIndex;
/* 628 */     if (this.fBindNamespaces) {
/* 629 */       int attrIndex = attributes.getLength();
/* 630 */       attributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
/*     */     }
/*     */     else
/*     */     {
/* 635 */       int oldLen = attributes.getLength();
/* 636 */       attrIndex = attributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
/*     */ 
/* 643 */       if (oldLen == attributes.getLength()) {
/* 644 */         reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 653 */     boolean isVC = (this.fHasExternalDTD) && (!this.fStandalone);
/*     */ 
/* 656 */     scanAttributeValue(this.fTempString, this.fTempString2, this.fAttributeQName.rawname, isVC, this.fCurrentElement.rawname);
/*     */ 
/* 662 */     String value = this.fTempString.toString();
/* 663 */     attributes.setValue(attrIndex, value);
/* 664 */     attributes.setNonNormalizedValue(attrIndex, this.fTempString2.toString());
/* 665 */     attributes.setSpecified(attrIndex, true);
/*     */ 
/* 668 */     if (this.fBindNamespaces)
/*     */     {
/* 670 */       String localpart = this.fAttributeQName.localpart;
/* 671 */       String prefix = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 677 */       if ((prefix == XMLSymbols.PREFIX_XMLNS) || ((prefix == XMLSymbols.EMPTY_STRING) && (localpart == XMLSymbols.PREFIX_XMLNS)))
/*     */       {
/* 682 */         String uri = this.fSymbolTable.addSymbol(value);
/*     */ 
/* 685 */         if ((prefix == XMLSymbols.PREFIX_XMLNS) && (localpart == XMLSymbols.PREFIX_XMLNS))
/*     */         {
/* 687 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
/*     */         }
/*     */ 
/* 695 */         if (uri == NamespaceContext.XMLNS_URI) {
/* 696 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
/*     */         }
/*     */ 
/* 704 */         if (localpart == XMLSymbols.PREFIX_XML) {
/* 705 */           if (uri != NamespaceContext.XML_URI) {
/* 706 */             this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
/*     */           }
/*     */ 
/*     */         }
/* 715 */         else if (uri == NamespaceContext.XML_URI) {
/* 716 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
/*     */         }
/*     */ 
/* 724 */         prefix = localpart != XMLSymbols.PREFIX_XMLNS ? localpart : XMLSymbols.EMPTY_STRING;
/*     */ 
/* 732 */         this.fNamespaceContext.declarePrefix(prefix, uri.length() != 0 ? uri : null);
/*     */ 
/* 736 */         attributes.setURI(attrIndex, this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));
/*     */       }
/* 742 */       else if (this.fAttributeQName.prefix != null) {
/* 743 */         attributes.setURI(attrIndex, this.fNamespaceContext.getURI(this.fAttributeQName.prefix));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int scanEndElement()
/*     */     throws IOException, XNIException
/*     */   {
/* 773 */     QName endElementName = this.fElementStack.popElement();
/*     */ 
/* 785 */     if (!this.fEntityScanner.skipString(endElementName.rawname)) {
/* 786 */       reportFatalError("ETagRequired", new Object[] { endElementName.rawname });
/*     */     }
/*     */ 
/* 792 */     this.fEntityScanner.skipSpaces();
/* 793 */     if (!this.fEntityScanner.skipChar(62)) {
/* 794 */       reportFatalError("ETagUnterminated", new Object[] { endElementName.rawname });
/*     */     }
/*     */ 
/* 798 */     this.fMarkupDepth -= 1;
/*     */ 
/* 801 */     this.fMarkupDepth -= 1;
/*     */ 
/* 804 */     if (this.fMarkupDepth < this.fEntityStack[(this.fEntityDepth - 1)]) {
/* 805 */       reportFatalError("ElementEntityMismatch", new Object[] { endElementName.rawname });
/*     */     }
/*     */ 
/* 811 */     if (this.fDocumentHandler != null) {
/* 812 */       this.fDocumentHandler.endElement(endElementName, null);
/*     */     }
/*     */ 
/* 820 */     if (this.dtdGrammarUtil != null) {
/* 821 */       this.dtdGrammarUtil.endElement(endElementName);
/*     */     }
/* 823 */     return this.fMarkupDepth;
/*     */   }
/*     */ 
/*     */   public void reset(XMLComponentManager componentManager)
/*     */     throws XMLConfigurationException
/*     */   {
/* 830 */     super.reset(componentManager);
/* 831 */     this.fPerformValidation = false;
/* 832 */     this.fBindNamespaces = false;
/*     */   }
/*     */ 
/*     */   protected XMLDocumentFragmentScannerImpl.Driver createContentDriver()
/*     */   {
/* 837 */     return new NS11ContentDriver();
/*     */   }
/*     */ 
/*     */   public int next()
/*     */     throws IOException, XNIException
/*     */   {
/* 850 */     if ((this.fScannerLastState == 2) && (this.fBindNamespaces)) {
/* 851 */       this.fScannerLastState = -1;
/* 852 */       this.fNamespaceContext.popContext();
/*     */     }
/*     */ 
/* 855 */     return this.fScannerLastState = super.next();
/*     */   }
/*     */ 
/*     */   protected final class NS11ContentDriver extends XMLDocumentScannerImpl.ContentDriver
/*     */   {
/*     */     protected NS11ContentDriver()
/*     */     {
/* 862 */       super();
/*     */     }
/*     */ 
/*     */     protected boolean scanRootElementHook()
/*     */       throws IOException, XNIException
/*     */     {
/* 879 */       if ((XML11NSDocumentScannerImpl.this.fExternalSubsetResolver != null) && (!XML11NSDocumentScannerImpl.this.fSeenDoctypeDecl) && (!XML11NSDocumentScannerImpl.this.fDisallowDoctype) && ((XML11NSDocumentScannerImpl.this.fValidation) || (XML11NSDocumentScannerImpl.this.fLoadExternalDTD)))
/*     */       {
/* 881 */         XML11NSDocumentScannerImpl.this.scanStartElementName();
/* 882 */         resolveExternalSubsetAndRead();
/* 883 */         reconfigurePipeline();
/* 884 */         if (XML11NSDocumentScannerImpl.this.scanStartElementAfterName()) {
/* 885 */           XML11NSDocumentScannerImpl.this.setScannerState(44);
/* 886 */           XML11NSDocumentScannerImpl.this.setDriver(XML11NSDocumentScannerImpl.this.fTrailingMiscDriver);
/* 887 */           return true;
/*     */         }
/*     */       }
/*     */       else {
/* 891 */         reconfigurePipeline();
/* 892 */         if (XML11NSDocumentScannerImpl.this.scanStartElement()) {
/* 893 */           XML11NSDocumentScannerImpl.this.setScannerState(44);
/* 894 */           XML11NSDocumentScannerImpl.this.setDriver(XML11NSDocumentScannerImpl.this.fTrailingMiscDriver);
/* 895 */           return true;
/*     */         }
/*     */       }
/* 898 */       return false;
/*     */     }
/*     */ 
/*     */     private void reconfigurePipeline()
/*     */     {
/* 909 */       if (XML11NSDocumentScannerImpl.this.fDTDValidator == null) {
/* 910 */         XML11NSDocumentScannerImpl.this.fBindNamespaces = true;
/*     */       }
/* 912 */       else if (!XML11NSDocumentScannerImpl.this.fDTDValidator.hasGrammar()) {
/* 913 */         XML11NSDocumentScannerImpl.this.fBindNamespaces = true;
/* 914 */         XML11NSDocumentScannerImpl.this.fPerformValidation = XML11NSDocumentScannerImpl.this.fDTDValidator.validate();
/*     */ 
/* 916 */         XMLDocumentSource source = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentSource();
/* 917 */         XMLDocumentHandler handler = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentHandler();
/* 918 */         source.setDocumentHandler(handler);
/* 919 */         if (handler != null)
/* 920 */           handler.setDocumentSource(source);
/* 921 */         XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentSource(null);
/* 922 */         XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentHandler(null);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl
 * JD-Core Version:    0.6.2
 */