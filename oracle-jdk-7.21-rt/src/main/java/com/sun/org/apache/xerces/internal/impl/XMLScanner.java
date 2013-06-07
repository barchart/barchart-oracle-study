/*      */ package com.sun.org.apache.xerces.internal.impl;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.util.Status;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.xml.internal.stream.Entity.ScannedEntity;
/*      */ import com.sun.xml.internal.stream.XMLEntityStorage;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import javax.xml.stream.events.XMLEvent;
/*      */ 
/*      */ public abstract class XMLScanner
/*      */   implements XMLComponent
/*      */ {
/*      */   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
/*      */   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
/*      */   protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
/*      */   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
/*      */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*      */   protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
/*  117 */   private boolean fNeedNonNormalizedValue = false;
/*      */ 
/*  119 */   protected ArrayList attributeValueCache = new ArrayList();
/*  120 */   protected ArrayList stringBufferCache = new ArrayList();
/*  121 */   protected int fStringBufferIndex = 0;
/*  122 */   protected boolean fAttributeCacheInitDone = false;
/*  123 */   protected int fAttributeCacheUsedCount = 0;
/*      */ 
/*  135 */   protected boolean fValidation = false;
/*      */   protected boolean fNamespaces;
/*  141 */   protected boolean fNotifyCharRefs = false;
/*      */ 
/*  144 */   protected boolean fParserSettings = true;
/*      */ 
/*  148 */   protected PropertyManager fPropertyManager = null;
/*      */   protected SymbolTable fSymbolTable;
/*      */   protected XMLErrorReporter fErrorReporter;
/*  157 */   protected XMLEntityManager fEntityManager = null;
/*      */ 
/*  160 */   protected XMLEntityStorage fEntityStore = null;
/*      */   protected XMLEvent fEvent;
/*  168 */   protected XMLEntityScanner fEntityScanner = null;
/*      */   protected int fEntityDepth;
/*  174 */   protected String fCharRefLiteral = null;
/*      */   protected boolean fScanningAttribute;
/*      */   protected boolean fReportEntity;
/*  185 */   protected static final String fVersionSymbol = "version".intern();
/*      */ 
/*  188 */   protected static final String fEncodingSymbol = "encoding".intern();
/*      */ 
/*  191 */   protected static final String fStandaloneSymbol = "standalone".intern();
/*      */ 
/*  194 */   protected static final String fAmpSymbol = "amp".intern();
/*      */ 
/*  197 */   protected static final String fLtSymbol = "lt".intern();
/*      */ 
/*  200 */   protected static final String fGtSymbol = "gt".intern();
/*      */ 
/*  203 */   protected static final String fQuotSymbol = "quot".intern();
/*      */ 
/*  206 */   protected static final String fAposSymbol = "apos".intern();
/*      */ 
/*  217 */   private XMLString fString = new XMLString();
/*      */ 
/*  220 */   private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
/*      */ 
/*  223 */   private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
/*      */ 
/*  226 */   private XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
/*      */ 
/*  229 */   protected XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
/*  230 */   int initialCacheCount = 6;
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/*  246 */     this.fParserSettings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
/*      */ 
/*  248 */     if (!this.fParserSettings)
/*      */     {
/*  250 */       init();
/*  251 */       return;
/*      */     }
/*      */ 
/*  256 */     this.fSymbolTable = ((SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/*  257 */     this.fErrorReporter = ((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*  258 */     this.fEntityManager = ((XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
/*      */ 
/*  261 */     this.fEntityStore = this.fEntityManager.getEntityStore();
/*      */ 
/*  264 */     this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
/*  265 */     this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
/*  266 */     this.fNotifyCharRefs = componentManager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs", false);
/*      */ 
/*  268 */     init();
/*      */   }
/*      */ 
/*      */   protected void setPropertyManager(PropertyManager propertyManager) {
/*  272 */     this.fPropertyManager = propertyManager;
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */     throws XMLConfigurationException
/*      */   {
/*  285 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/*  286 */       String property = propertyId.substring("http://apache.org/xml/properties/".length());
/*      */ 
/*  288 */       if (property.equals("internal/symbol-table"))
/*  289 */         this.fSymbolTable = ((SymbolTable)value);
/*  290 */       else if (property.equals("internal/error-reporter"))
/*  291 */         this.fErrorReporter = ((XMLErrorReporter)value);
/*  292 */       else if (property.equals("internal/entity-manager"))
/*  293 */         this.fEntityManager = ((XMLEntityManager)value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean value)
/*      */     throws XMLConfigurationException
/*      */   {
/*  310 */     if ("http://xml.org/sax/features/validation".equals(featureId))
/*  311 */       this.fValidation = value;
/*  312 */     else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId))
/*  313 */       this.fNotifyCharRefs = value;
/*      */   }
/*      */ 
/*      */   public boolean getFeature(String featureId)
/*      */     throws XMLConfigurationException
/*      */   {
/*  323 */     if ("http://xml.org/sax/features/validation".equals(featureId))
/*  324 */       return this.fValidation;
/*  325 */     if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId)) {
/*  326 */       return this.fNotifyCharRefs;
/*      */     }
/*  328 */     throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
/*      */   }
/*      */ 
/*      */   protected void reset()
/*      */   {
/*  337 */     init();
/*      */ 
/*  340 */     this.fValidation = true;
/*  341 */     this.fNotifyCharRefs = false;
/*      */   }
/*      */ 
/*      */   public void reset(PropertyManager propertyManager)
/*      */   {
/*  346 */     init();
/*      */ 
/*  348 */     this.fSymbolTable = ((SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/*      */ 
/*  350 */     this.fErrorReporter = ((XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*      */ 
/*  352 */     this.fEntityManager = ((XMLEntityManager)propertyManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
/*  353 */     this.fEntityStore = this.fEntityManager.getEntityStore();
/*  354 */     this.fEntityScanner = this.fEntityManager.getEntityScanner();
/*      */ 
/*  357 */     this.fValidation = false;
/*  358 */     this.fNotifyCharRefs = false;
/*      */   }
/*      */ 
/*      */   protected void scanXMLDeclOrTextDecl(boolean scanningTextDecl, String[] pseudoAttributeValues)
/*      */     throws IOException, XNIException
/*      */   {
/*  392 */     String version = null;
/*  393 */     String encoding = null;
/*  394 */     String standalone = null;
/*      */ 
/*  397 */     int STATE_VERSION = 0;
/*  398 */     int STATE_ENCODING = 1;
/*  399 */     int STATE_STANDALONE = 2;
/*  400 */     int STATE_DONE = 3;
/*  401 */     int state = 0;
/*      */ 
/*  403 */     boolean dataFoundForTarget = false;
/*  404 */     boolean sawSpace = this.fEntityScanner.skipSpaces();
/*      */ 
/*  412 */     Entity.ScannedEntity currEnt = this.fEntityManager.getCurrentEntity();
/*  413 */     boolean currLiteral = currEnt.literal;
/*  414 */     currEnt.literal = false;
/*  415 */     while (this.fEntityScanner.peekChar() != 63) {
/*  416 */       dataFoundForTarget = true;
/*  417 */       String name = scanPseudoAttribute(scanningTextDecl, this.fString);
/*  418 */       switch (state) {
/*      */       case 0:
/*  420 */         if (name.equals(fVersionSymbol)) {
/*  421 */           if (!sawSpace) {
/*  422 */             reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", null);
/*      */           }
/*      */ 
/*  427 */           version = this.fString.toString();
/*  428 */           state = 1;
/*  429 */           if (!versionSupported(version)) {
/*  430 */             reportFatalError("VersionNotSupported", new Object[] { version });
/*      */           }
/*      */ 
/*  434 */           if (version.equals("1.1")) {
/*  435 */             Entity.ScannedEntity top = this.fEntityManager.getTopLevelEntity();
/*  436 */             if ((top != null) && ((top.version == null) || (top.version.equals("1.0")))) {
/*  437 */               reportFatalError("VersionMismatch", null);
/*      */             }
/*  439 */             this.fEntityManager.setScannerVersion((short)2);
/*      */           }
/*      */         }
/*  442 */         else if (name.equals(fEncodingSymbol)) {
/*  443 */           if (!scanningTextDecl) {
/*  444 */             reportFatalError("VersionInfoRequired", null);
/*      */           }
/*  446 */           if (!sawSpace) {
/*  447 */             reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
/*      */           }
/*      */ 
/*  452 */           encoding = this.fString.toString();
/*  453 */           state = scanningTextDecl ? 3 : 2;
/*      */         }
/*  455 */         else if (scanningTextDecl) {
/*  456 */           reportFatalError("EncodingDeclRequired", null);
/*      */         } else {
/*  458 */           reportFatalError("VersionInfoRequired", null);
/*      */         }
/*      */ 
/*  461 */         break;
/*      */       case 1:
/*  464 */         if (name.equals(fEncodingSymbol)) {
/*  465 */           if (!sawSpace) {
/*  466 */             reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
/*      */           }
/*      */ 
/*  471 */           encoding = this.fString.toString();
/*  472 */           state = scanningTextDecl ? 3 : 2;
/*      */         }
/*  475 */         else if ((!scanningTextDecl) && (name.equals(fStandaloneSymbol))) {
/*  476 */           if (!sawSpace) {
/*  477 */             reportFatalError("SpaceRequiredBeforeStandalone", null);
/*      */           }
/*      */ 
/*  480 */           standalone = this.fString.toString();
/*  481 */           state = 3;
/*  482 */           if ((!standalone.equals("yes")) && (!standalone.equals("no")))
/*  483 */             reportFatalError("SDDeclInvalid", new Object[] { standalone });
/*      */         }
/*      */         else {
/*  486 */           reportFatalError("EncodingDeclRequired", null);
/*      */         }
/*  488 */         break;
/*      */       case 2:
/*  491 */         if (name.equals(fStandaloneSymbol)) {
/*  492 */           if (!sawSpace) {
/*  493 */             reportFatalError("SpaceRequiredBeforeStandalone", null);
/*      */           }
/*      */ 
/*  496 */           standalone = this.fString.toString();
/*  497 */           state = 3;
/*  498 */           if ((!standalone.equals("yes")) && (!standalone.equals("no")))
/*  499 */             reportFatalError("SDDeclInvalid", new Object[] { standalone });
/*      */         }
/*      */         else {
/*  502 */           reportFatalError("EncodingDeclRequired", null);
/*      */         }
/*  504 */         break;
/*      */       default:
/*  507 */         reportFatalError("NoMorePseudoAttributes", null);
/*      */       }
/*      */ 
/*  510 */       sawSpace = this.fEntityScanner.skipSpaces();
/*      */     }
/*      */ 
/*  513 */     if (currLiteral) {
/*  514 */       currEnt.literal = true;
/*      */     }
/*  516 */     if ((scanningTextDecl) && (state != 3)) {
/*  517 */       reportFatalError("MorePseudoAttributes", null);
/*      */     }
/*      */ 
/*  522 */     if (scanningTextDecl) {
/*  523 */       if ((!dataFoundForTarget) && (encoding == null)) {
/*  524 */         reportFatalError("EncodingDeclRequired", null);
/*      */       }
/*      */     }
/*  527 */     else if ((!dataFoundForTarget) && (version == null)) {
/*  528 */       reportFatalError("VersionInfoRequired", null);
/*      */     }
/*      */ 
/*  533 */     if (!this.fEntityScanner.skipChar(63)) {
/*  534 */       reportFatalError("XMLDeclUnterminated", null);
/*      */     }
/*  536 */     if (!this.fEntityScanner.skipChar(62)) {
/*  537 */       reportFatalError("XMLDeclUnterminated", null);
/*      */     }
/*      */ 
/*  542 */     pseudoAttributeValues[0] = version;
/*  543 */     pseudoAttributeValues[1] = encoding;
/*  544 */     pseudoAttributeValues[2] = standalone;
/*      */   }
/*      */ 
/*      */   public String scanPseudoAttribute(boolean scanningTextDecl, XMLString value)
/*      */     throws IOException, XNIException
/*      */   {
/*  567 */     String name = this.fEntityScanner.scanName();
/*      */ 
/*  570 */     if (name == null) {
/*  571 */       reportFatalError("PseudoAttrNameExpected", null);
/*      */     }
/*  573 */     this.fEntityScanner.skipSpaces();
/*  574 */     if (!this.fEntityScanner.skipChar(61)) {
/*  575 */       reportFatalError(scanningTextDecl ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[] { name });
/*      */     }
/*      */ 
/*  578 */     this.fEntityScanner.skipSpaces();
/*  579 */     int quote = this.fEntityScanner.peekChar();
/*  580 */     if ((quote != 39) && (quote != 34)) {
/*  581 */       reportFatalError(scanningTextDecl ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[] { name });
/*      */     }
/*      */ 
/*  584 */     this.fEntityScanner.scanChar();
/*  585 */     int c = this.fEntityScanner.scanLiteral(quote, value);
/*  586 */     if (c != quote) {
/*  587 */       this.fStringBuffer2.clear();
/*      */       do {
/*  589 */         this.fStringBuffer2.append(value);
/*  590 */         if (c != -1) {
/*  591 */           if ((c == 38) || (c == 37) || (c == 60) || (c == 93)) {
/*  592 */             this.fStringBuffer2.append((char)this.fEntityScanner.scanChar());
/*  593 */           } else if (XMLChar.isHighSurrogate(c)) {
/*  594 */             scanSurrogates(this.fStringBuffer2);
/*  595 */           } else if (isInvalidLiteral(c)) {
/*  596 */             String key = scanningTextDecl ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
/*      */ 
/*  598 */             reportFatalError(key, new Object[] { Integer.toString(c, 16) });
/*      */ 
/*  600 */             this.fEntityScanner.scanChar();
/*      */           }
/*      */         }
/*  603 */         c = this.fEntityScanner.scanLiteral(quote, value);
/*  604 */       }while (c != quote);
/*  605 */       this.fStringBuffer2.append(value);
/*  606 */       value.setValues(this.fStringBuffer2);
/*      */     }
/*  608 */     if (!this.fEntityScanner.skipChar(quote)) {
/*  609 */       reportFatalError(scanningTextDecl ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[] { name });
/*      */     }
/*      */ 
/*  615 */     return name;
/*      */   }
/*      */ 
/*      */   protected void scanPI(XMLStringBuffer data)
/*      */     throws IOException, XNIException
/*      */   {
/*  636 */     this.fReportEntity = false;
/*  637 */     String target = this.fEntityScanner.scanName();
/*  638 */     if (target == null) {
/*  639 */       reportFatalError("PITargetRequired", null);
/*      */     }
/*      */ 
/*  643 */     scanPIData(target, data);
/*  644 */     this.fReportEntity = true;
/*      */   }
/*      */ 
/*      */   protected void scanPIData(String target, XMLStringBuffer data)
/*      */     throws IOException, XNIException
/*      */   {
/*  670 */     if (target.length() == 3) {
/*  671 */       char c0 = Character.toLowerCase(target.charAt(0));
/*  672 */       char c1 = Character.toLowerCase(target.charAt(1));
/*  673 */       char c2 = Character.toLowerCase(target.charAt(2));
/*  674 */       if ((c0 == 'x') && (c1 == 'm') && (c2 == 'l')) {
/*  675 */         reportFatalError("ReservedPITarget", null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  680 */     if (!this.fEntityScanner.skipSpaces()) {
/*  681 */       if (this.fEntityScanner.skipString("?>"))
/*      */       {
/*  683 */         return;
/*      */       }
/*      */ 
/*  686 */       reportFatalError("SpaceRequiredInPI", null);
/*      */     }
/*      */ 
/*  693 */     if (this.fEntityScanner.scanData("?>", data))
/*      */       do {
/*  695 */         int c = this.fEntityScanner.peekChar();
/*  696 */         if (c != -1)
/*  697 */           if (XMLChar.isHighSurrogate(c)) {
/*  698 */             scanSurrogates(data);
/*  699 */           } else if (isInvalidLiteral(c)) {
/*  700 */             reportFatalError("InvalidCharInPI", new Object[] { Integer.toHexString(c) });
/*      */ 
/*  702 */             this.fEntityScanner.scanChar();
/*      */           }
/*      */       }
/*  705 */       while (this.fEntityScanner.scanData("?>", data));
/*      */   }
/*      */ 
/*      */   protected void scanComment(XMLStringBuffer text)
/*      */     throws IOException, XNIException
/*      */   {
/*  729 */     text.clear();
/*  730 */     while (this.fEntityScanner.scanData("--", text)) {
/*  731 */       int c = this.fEntityScanner.peekChar();
/*      */ 
/*  736 */       if (c != -1) {
/*  737 */         if (XMLChar.isHighSurrogate(c)) {
/*  738 */           scanSurrogates(text);
/*      */         }
/*  740 */         if (isInvalidLiteral(c)) {
/*  741 */           reportFatalError("InvalidCharInComment", new Object[] { Integer.toHexString(c) });
/*      */ 
/*  743 */           this.fEntityScanner.scanChar();
/*      */         }
/*      */       }
/*      */     }
/*  747 */     if (!this.fEntityScanner.skipChar(62))
/*  748 */       reportFatalError("DashDashInComment", null);
/*      */   }
/*      */ 
/*      */   protected void scanAttributeValue(XMLString value, XMLString nonNormalizedValue, String atName, XMLAttributes attributes, int attrIndex, boolean checkEntities)
/*      */     throws IOException, XNIException
/*      */   {
/*  777 */     XMLStringBuffer stringBuffer = null;
/*      */ 
/*  779 */     int quote = this.fEntityScanner.peekChar();
/*  780 */     if ((quote != 39) && (quote != 34)) {
/*  781 */       reportFatalError("OpenQuoteExpected", new Object[] { atName });
/*      */     }
/*      */ 
/*  784 */     this.fEntityScanner.scanChar();
/*  785 */     int entityDepth = this.fEntityDepth;
/*      */ 
/*  787 */     int c = this.fEntityScanner.scanLiteral(quote, value);
/*      */ 
/*  792 */     if (this.fNeedNonNormalizedValue) {
/*  793 */       this.fStringBuffer2.clear();
/*  794 */       this.fStringBuffer2.append(value);
/*      */     }
/*  796 */     if (this.fEntityScanner.whiteSpaceLen > 0) {
/*  797 */       normalizeWhitespace(value);
/*      */     }
/*      */ 
/*  802 */     if (c != quote) {
/*  803 */       this.fScanningAttribute = true;
/*  804 */       stringBuffer = getStringBuffer();
/*  805 */       stringBuffer.clear();
/*      */       do {
/*  807 */         stringBuffer.append(value);
/*      */ 
/*  812 */         if (c == 38) {
/*  813 */           this.fEntityScanner.skipChar(38);
/*  814 */           if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue)) {
/*  815 */             this.fStringBuffer2.append('&');
/*      */           }
/*  817 */           if (this.fEntityScanner.skipChar(35)) {
/*  818 */             if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue))
/*  819 */               this.fStringBuffer2.append('#');
/*      */             int ch;
/*      */             int ch;
/*  822 */             if (this.fNeedNonNormalizedValue)
/*  823 */               ch = scanCharReferenceValue(stringBuffer, this.fStringBuffer2);
/*      */             else {
/*  825 */               ch = scanCharReferenceValue(stringBuffer, null);
/*      */             }
/*      */ 
/*  827 */             if (ch == -1);
/*      */           }
/*      */           else
/*      */           {
/*  835 */             String entityName = this.fEntityScanner.scanName();
/*  836 */             if (entityName == null)
/*  837 */               reportFatalError("NameRequiredInReference", null);
/*  838 */             else if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue)) {
/*  839 */               this.fStringBuffer2.append(entityName);
/*      */             }
/*  841 */             if (!this.fEntityScanner.skipChar(59)) {
/*  842 */               reportFatalError("SemicolonRequiredInReference", new Object[] { entityName });
/*      */             }
/*  844 */             else if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue)) {
/*  845 */               this.fStringBuffer2.append(';');
/*      */             }
/*  847 */             if (entityName == fAmpSymbol) {
/*  848 */               stringBuffer.append('&');
/*      */             }
/*  854 */             else if (entityName == fAposSymbol) {
/*  855 */               stringBuffer.append('\'');
/*      */             }
/*  861 */             else if (entityName == fLtSymbol) {
/*  862 */               stringBuffer.append('<');
/*      */             }
/*  868 */             else if (entityName == fGtSymbol) {
/*  869 */               stringBuffer.append('>');
/*      */             }
/*  875 */             else if (entityName == fQuotSymbol) {
/*  876 */               stringBuffer.append('"');
/*      */             }
/*  883 */             else if (this.fEntityStore.isExternalEntity(entityName)) {
/*  884 */               reportFatalError("ReferenceToExternalEntity", new Object[] { entityName });
/*      */             }
/*      */             else {
/*  887 */               if (!this.fEntityStore.isDeclaredEntity(entityName))
/*      */               {
/*  889 */                 if (checkEntities) {
/*  890 */                   if (this.fValidation) {
/*  891 */                     this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { entityName }, (short)1);
/*      */                   }
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*  897 */                   reportFatalError("EntityNotDeclared", new Object[] { entityName });
/*      */                 }
/*      */               }
/*      */ 
/*  901 */               this.fEntityManager.startEntity(entityName, true);
/*      */             }
/*      */           }
/*      */         }
/*  905 */         else if (c == 60) {
/*  906 */           reportFatalError("LessthanInAttValue", new Object[] { null, atName });
/*      */ 
/*  908 */           this.fEntityScanner.scanChar();
/*  909 */           if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue))
/*  910 */             this.fStringBuffer2.append((char)c);
/*      */         }
/*  912 */         else if ((c == 37) || (c == 93)) {
/*  913 */           this.fEntityScanner.scanChar();
/*  914 */           stringBuffer.append((char)c);
/*  915 */           if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue)) {
/*  916 */             this.fStringBuffer2.append((char)c);
/*      */           }
/*      */ 
/*      */         }
/*  922 */         else if ((c == 10) || (c == 13)) {
/*  923 */           this.fEntityScanner.scanChar();
/*  924 */           stringBuffer.append(' ');
/*  925 */           if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue))
/*  926 */             this.fStringBuffer2.append('\n');
/*      */         }
/*  928 */         else if ((c != -1) && (XMLChar.isHighSurrogate(c))) {
/*  929 */           if (scanSurrogates(this.fStringBuffer3)) {
/*  930 */             stringBuffer.append(this.fStringBuffer3);
/*  931 */             if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue)) {
/*  932 */               this.fStringBuffer2.append(this.fStringBuffer3);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*  940 */         else if ((c != -1) && (isInvalidLiteral(c))) {
/*  941 */           reportFatalError("InvalidCharInAttValue", new Object[] { Integer.toString(c, 16) });
/*      */ 
/*  943 */           this.fEntityScanner.scanChar();
/*  944 */           if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue)) {
/*  945 */             this.fStringBuffer2.append((char)c);
/*      */           }
/*      */         }
/*  948 */         c = this.fEntityScanner.scanLiteral(quote, value);
/*  949 */         if ((entityDepth == this.fEntityDepth) && (this.fNeedNonNormalizedValue)) {
/*  950 */           this.fStringBuffer2.append(value);
/*      */         }
/*  952 */         if (this.fEntityScanner.whiteSpaceLen > 0) {
/*  953 */           normalizeWhitespace(value);
/*      */         }
/*      */       }
/*  956 */       while ((c != quote) || (entityDepth != this.fEntityDepth));
/*  957 */       stringBuffer.append(value);
/*      */ 
/*  962 */       value.setValues(stringBuffer);
/*  963 */       this.fScanningAttribute = false;
/*      */     }
/*  965 */     if (this.fNeedNonNormalizedValue) {
/*  966 */       nonNormalizedValue.setValues(this.fStringBuffer2);
/*      */     }
/*      */ 
/*  969 */     int cquote = this.fEntityScanner.scanChar();
/*  970 */     if (cquote != quote)
/*  971 */       reportFatalError("CloseQuoteExpected", new Object[] { atName });
/*      */   }
/*      */ 
/*      */   protected void scanExternalID(String[] identifiers, boolean optionalSystemId)
/*      */     throws IOException, XNIException
/*      */   {
/*  990 */     String systemId = null;
/*  991 */     String publicId = null;
/*  992 */     if (this.fEntityScanner.skipString("PUBLIC")) {
/*  993 */       if (!this.fEntityScanner.skipSpaces()) {
/*  994 */         reportFatalError("SpaceRequiredAfterPUBLIC", null);
/*      */       }
/*  996 */       scanPubidLiteral(this.fString);
/*  997 */       publicId = this.fString.toString();
/*      */ 
/*  999 */       if ((!this.fEntityScanner.skipSpaces()) && (!optionalSystemId)) {
/* 1000 */         reportFatalError("SpaceRequiredBetweenPublicAndSystem", null);
/*      */       }
/*      */     }
/*      */ 
/* 1004 */     if ((publicId != null) || (this.fEntityScanner.skipString("SYSTEM"))) {
/* 1005 */       if ((publicId == null) && (!this.fEntityScanner.skipSpaces())) {
/* 1006 */         reportFatalError("SpaceRequiredAfterSYSTEM", null);
/*      */       }
/* 1008 */       int quote = this.fEntityScanner.peekChar();
/* 1009 */       if ((quote != 39) && (quote != 34)) {
/* 1010 */         if ((publicId != null) && (optionalSystemId))
/*      */         {
/* 1013 */           identifiers[0] = null;
/* 1014 */           identifiers[1] = publicId;
/* 1015 */           return;
/*      */         }
/* 1017 */         reportFatalError("QuoteRequiredInSystemID", null);
/*      */       }
/* 1019 */       this.fEntityScanner.scanChar();
/* 1020 */       XMLString ident = this.fString;
/* 1021 */       if (this.fEntityScanner.scanLiteral(quote, ident) != quote) {
/* 1022 */         this.fStringBuffer.clear();
/*      */         do {
/* 1024 */           this.fStringBuffer.append(ident);
/* 1025 */           int c = this.fEntityScanner.peekChar();
/* 1026 */           if ((XMLChar.isMarkup(c)) || (c == 93))
/* 1027 */             this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
/* 1028 */           else if ((c != -1) && (isInvalidLiteral(c))) {
/* 1029 */             reportFatalError("InvalidCharInSystemID", new Object[] { Integer.toString(c, 16) });
/*      */           }
/*      */         }
/* 1032 */         while (this.fEntityScanner.scanLiteral(quote, ident) != quote);
/* 1033 */         this.fStringBuffer.append(ident);
/* 1034 */         ident = this.fStringBuffer;
/*      */       }
/* 1036 */       systemId = ident.toString();
/* 1037 */       if (!this.fEntityScanner.skipChar(quote)) {
/* 1038 */         reportFatalError("SystemIDUnterminated", null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1043 */     identifiers[0] = systemId;
/* 1044 */     identifiers[1] = publicId;
/*      */   }
/*      */ 
/*      */   protected boolean scanPubidLiteral(XMLString literal)
/*      */     throws IOException, XNIException
/*      */   {
/* 1069 */     int quote = this.fEntityScanner.scanChar();
/* 1070 */     if ((quote != 39) && (quote != 34)) {
/* 1071 */       reportFatalError("QuoteRequiredInPublicID", null);
/* 1072 */       return false;
/*      */     }
/*      */ 
/* 1075 */     this.fStringBuffer.clear();
/*      */ 
/* 1077 */     boolean skipSpace = true;
/* 1078 */     boolean dataok = true;
/*      */     while (true) {
/* 1080 */       int c = this.fEntityScanner.scanChar();
/* 1081 */       if ((c == 32) || (c == 10) || (c == 13)) {
/* 1082 */         if (!skipSpace)
/*      */         {
/* 1084 */           this.fStringBuffer.append(' ');
/* 1085 */           skipSpace = true;
/*      */         }
/*      */       } else { if (c == quote) {
/* 1088 */           if (skipSpace)
/*      */           {
/* 1090 */             this.fStringBuffer.length -= 1;
/*      */           }
/* 1092 */           literal.setValues(this.fStringBuffer);
/* 1093 */           break;
/* 1094 */         }if (XMLChar.isPubid(c)) {
/* 1095 */           this.fStringBuffer.append((char)c);
/* 1096 */           skipSpace = false; } else {
/* 1097 */           if (c == -1) {
/* 1098 */             reportFatalError("PublicIDUnterminated", null);
/* 1099 */             return false;
/*      */           }
/* 1101 */           dataok = false;
/* 1102 */           reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(c) });
/*      */         }
/*      */       }
/*      */     }
/* 1106 */     return dataok;
/*      */   }
/*      */ 
/*      */   protected void normalizeWhitespace(XMLString value)
/*      */   {
/* 1115 */     int i = 0;
/* 1116 */     int j = 0;
/* 1117 */     int[] buff = this.fEntityScanner.whiteSpaceLookup;
/* 1118 */     int buffLen = this.fEntityScanner.whiteSpaceLen;
/* 1119 */     int end = value.offset + value.length;
/* 1120 */     while (i < buffLen) {
/* 1121 */       j = buff[i];
/* 1122 */       if (j < end) {
/* 1123 */         value.ch[j] = ' ';
/*      */       }
/* 1125 */       i++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1154 */     this.fEntityDepth += 1;
/*      */ 
/* 1156 */     this.fEntityScanner = this.fEntityManager.getEntityScanner();
/* 1157 */     this.fEntityStore = this.fEntityManager.getEntityStore();
/*      */   }
/*      */ 
/*      */   public void endEntity(String name, Augmentations augs)
/*      */     throws IOException, XNIException
/*      */   {
/* 1173 */     this.fEntityDepth -= 1;
/*      */   }
/*      */ 
/*      */   protected int scanCharReferenceValue(XMLStringBuffer buf, XMLStringBuffer buf2)
/*      */     throws IOException, XNIException
/*      */   {
/* 1197 */     boolean hex = false;
/* 1198 */     if (this.fEntityScanner.skipChar(120)) {
/* 1199 */       if (buf2 != null) buf2.append('x');
/* 1200 */       hex = true;
/* 1201 */       this.fStringBuffer3.clear();
/* 1202 */       boolean digit = true;
/*      */ 
/* 1204 */       int c = this.fEntityScanner.peekChar();
/* 1205 */       digit = ((c >= 48) && (c <= 57)) || ((c >= 97) && (c <= 102)) || ((c >= 65) && (c <= 70));
/*      */ 
/* 1208 */       if (digit) {
/* 1209 */         if (buf2 != null) buf2.append((char)c);
/* 1210 */         this.fEntityScanner.scanChar();
/* 1211 */         this.fStringBuffer3.append((char)c);
/*      */         do
/*      */         {
/* 1214 */           c = this.fEntityScanner.peekChar();
/* 1215 */           digit = ((c >= 48) && (c <= 57)) || ((c >= 97) && (c <= 102)) || ((c >= 65) && (c <= 70));
/*      */ 
/* 1218 */           if (digit) {
/* 1219 */             if (buf2 != null) buf2.append((char)c);
/* 1220 */             this.fEntityScanner.scanChar();
/* 1221 */             this.fStringBuffer3.append((char)c);
/*      */           }
/*      */         }
/* 1223 */         while (digit);
/*      */       } else {
/* 1225 */         reportFatalError("HexdigitRequiredInCharRef", null);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1231 */       this.fStringBuffer3.clear();
/* 1232 */       boolean digit = true;
/*      */ 
/* 1234 */       int c = this.fEntityScanner.peekChar();
/* 1235 */       digit = (c >= 48) && (c <= 57);
/* 1236 */       if (digit) {
/* 1237 */         if (buf2 != null) buf2.append((char)c);
/* 1238 */         this.fEntityScanner.scanChar();
/* 1239 */         this.fStringBuffer3.append((char)c);
/*      */         do
/*      */         {
/* 1242 */           c = this.fEntityScanner.peekChar();
/* 1243 */           digit = (c >= 48) && (c <= 57);
/* 1244 */           if (digit) {
/* 1245 */             if (buf2 != null) buf2.append((char)c);
/* 1246 */             this.fEntityScanner.scanChar();
/* 1247 */             this.fStringBuffer3.append((char)c);
/*      */           }
/*      */         }
/* 1249 */         while (digit);
/*      */       } else {
/* 1251 */         reportFatalError("DigitRequiredInCharRef", null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1256 */     if (!this.fEntityScanner.skipChar(59)) {
/* 1257 */       reportFatalError("SemicolonRequiredInCharRef", null);
/*      */     }
/* 1259 */     if (buf2 != null) buf2.append(';');
/*      */ 
/* 1262 */     int value = -1;
/*      */     try {
/* 1264 */       value = Integer.parseInt(this.fStringBuffer3.toString(), hex ? 16 : 10);
/*      */ 
/* 1268 */       if (isInvalid(value)) {
/* 1269 */         StringBuffer errorBuf = new StringBuffer(this.fStringBuffer3.length + 1);
/* 1270 */         if (hex) errorBuf.append('x');
/* 1271 */         errorBuf.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
/* 1272 */         reportFatalError("InvalidCharRef", new Object[] { errorBuf.toString() });
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (NumberFormatException e)
/*      */     {
/* 1278 */       StringBuffer errorBuf = new StringBuffer(this.fStringBuffer3.length + 1);
/* 1279 */       if (hex) errorBuf.append('x');
/* 1280 */       errorBuf.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
/* 1281 */       reportFatalError("InvalidCharRef", new Object[] { errorBuf.toString() });
/*      */     }
/*      */ 
/* 1286 */     if (!XMLChar.isSupplemental(value)) {
/* 1287 */       buf.append((char)value);
/*      */     }
/*      */     else {
/* 1290 */       buf.append(XMLChar.highSurrogate(value));
/* 1291 */       buf.append(XMLChar.lowSurrogate(value));
/*      */     }
/*      */ 
/* 1295 */     if ((this.fNotifyCharRefs) && (value != -1)) {
/* 1296 */       String literal = "#" + (hex ? "x" : "") + this.fStringBuffer3.toString();
/* 1297 */       if (!this.fScanningAttribute) {
/* 1298 */         this.fCharRefLiteral = literal;
/*      */       }
/*      */     }
/*      */ 
/* 1302 */     return value;
/*      */   }
/*      */ 
/*      */   protected boolean isInvalid(int value)
/*      */   {
/* 1308 */     return XMLChar.isInvalid(value);
/*      */   }
/*      */ 
/*      */   protected boolean isInvalidLiteral(int value)
/*      */   {
/* 1315 */     return XMLChar.isInvalid(value);
/*      */   }
/*      */ 
/*      */   protected boolean isValidNameChar(int value)
/*      */   {
/* 1322 */     return XMLChar.isName(value);
/*      */   }
/*      */ 
/*      */   protected boolean isValidNCName(int value)
/*      */   {
/* 1329 */     return XMLChar.isNCName(value);
/*      */   }
/*      */ 
/*      */   protected boolean isValidNameStartChar(int value)
/*      */   {
/* 1336 */     return XMLChar.isNameStart(value);
/*      */   }
/*      */ 
/*      */   protected boolean versionSupported(String version) {
/* 1340 */     return (version.equals("1.0")) || (version.equals("1.1"));
/*      */   }
/*      */ 
/*      */   protected boolean scanSurrogates(XMLStringBuffer buf)
/*      */     throws IOException, XNIException
/*      */   {
/* 1355 */     int high = this.fEntityScanner.scanChar();
/* 1356 */     int low = this.fEntityScanner.peekChar();
/* 1357 */     if (!XMLChar.isLowSurrogate(low)) {
/* 1358 */       reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(high, 16) });
/*      */ 
/* 1360 */       return false;
/*      */     }
/* 1362 */     this.fEntityScanner.scanChar();
/*      */ 
/* 1365 */     int c = XMLChar.supplemental((char)high, (char)low);
/*      */ 
/* 1368 */     if (isInvalid(c)) {
/* 1369 */       reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(c, 16) });
/*      */ 
/* 1371 */       return false;
/*      */     }
/*      */ 
/* 1375 */     buf.append((char)high);
/* 1376 */     buf.append((char)low);
/*      */ 
/* 1378 */     return true;
/*      */   }
/*      */ 
/*      */   protected void reportFatalError(String msgId, Object[] args)
/*      */     throws XNIException
/*      */   {
/* 1388 */     this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", msgId, args, (short)2);
/*      */   }
/*      */ 
/*      */   private void init()
/*      */   {
/* 1396 */     this.fEntityScanner = null;
/*      */ 
/* 1398 */     this.fEntityDepth = 0;
/* 1399 */     this.fReportEntity = true;
/* 1400 */     this.fResourceIdentifier.clear();
/*      */ 
/* 1402 */     if (!this.fAttributeCacheInitDone) {
/* 1403 */       for (int i = 0; i < this.initialCacheCount; i++) {
/* 1404 */         this.attributeValueCache.add(new XMLString());
/* 1405 */         this.stringBufferCache.add(new XMLStringBuffer());
/*      */       }
/* 1407 */       this.fAttributeCacheInitDone = true;
/*      */     }
/* 1409 */     this.fStringBufferIndex = 0;
/* 1410 */     this.fAttributeCacheUsedCount = 0;
/*      */   }
/*      */ 
/*      */   XMLStringBuffer getStringBuffer()
/*      */   {
/* 1415 */     if ((this.fStringBufferIndex < this.initialCacheCount) || (this.fStringBufferIndex < this.stringBufferCache.size())) {
/* 1416 */       return (XMLStringBuffer)this.stringBufferCache.get(this.fStringBufferIndex++);
/*      */     }
/* 1418 */     XMLStringBuffer tmpObj = new XMLStringBuffer();
/* 1419 */     this.fStringBufferIndex += 1;
/* 1420 */     this.stringBufferCache.add(tmpObj);
/* 1421 */     return tmpObj;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XMLScanner
 * JD-Core Version:    0.6.2
 */