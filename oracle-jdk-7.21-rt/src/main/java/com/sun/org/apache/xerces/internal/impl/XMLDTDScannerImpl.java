/*      */ package com.sun.org.apache.xerces.internal.impl;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.xml.internal.stream.Entity.ScannedEntity;
/*      */ import com.sun.xml.internal.stream.XMLEntityStorage;
/*      */ import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ 
/*      */ public class XMLDTDScannerImpl extends XMLScanner
/*      */   implements XMLDTDScanner, XMLComponent, XMLEntityHandler
/*      */ {
/*      */   protected static final int SCANNER_STATE_END_OF_INPUT = 0;
/*      */   protected static final int SCANNER_STATE_TEXT_DECL = 1;
/*      */   protected static final int SCANNER_STATE_MARKUP_DECL = 2;
/*   93 */   private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-char-refs" };
/*      */ 
/*   99 */   private static final Boolean[] FEATURE_DEFAULTS = { null, Boolean.FALSE };
/*      */ 
/*  105 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager" };
/*      */ 
/*  112 */   private static final Object[] PROPERTY_DEFAULTS = { null, null, null };
/*      */   private static final boolean DEBUG_SCANNER_STATE = false;
/*  130 */   public XMLDTDHandler fDTDHandler = null;
/*      */   protected XMLDTDContentModelHandler fDTDContentModelHandler;
/*      */   protected int fScannerState;
/*      */   protected boolean fStandalone;
/*      */   protected boolean fSeenExternalDTD;
/*      */   protected boolean fSeenExternalPE;
/*      */   private boolean fStartDTDCalled;
/*  155 */   private XMLAttributesImpl fAttributes = new XMLAttributesImpl();
/*      */ 
/*  161 */   private int[] fContentStack = new int[5];
/*      */   private int fContentDepth;
/*  167 */   private int[] fPEStack = new int[5];
/*      */ 
/*  171 */   private boolean[] fPEReport = new boolean[5];
/*      */   private int fPEDepth;
/*      */   private int fMarkUpDepth;
/*      */   private int fExtEntityDepth;
/*      */   private int fIncludeSectDepth;
/*  188 */   private String[] fStrings = new String[3];
/*      */ 
/*  191 */   private XMLString fString = new XMLString();
/*      */ 
/*  194 */   private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
/*      */ 
/*  197 */   private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
/*      */ 
/*  200 */   private XMLString fLiteral = new XMLString();
/*      */ 
/*  203 */   private XMLString fLiteral2 = new XMLString();
/*      */ 
/*  206 */   private String[] fEnumeration = new String[5];
/*      */   private int fEnumerationCount;
/*  212 */   private XMLStringBuffer fIgnoreConditionalBuffer = new XMLStringBuffer(128);
/*      */ 
/*  215 */   DTDGrammar nvGrammarInfo = null;
/*      */ 
/*  217 */   boolean nonValidatingMode = false;
/*      */ 
/*      */   public XMLDTDScannerImpl()
/*      */   {
/*      */   }
/*      */ 
/*      */   public XMLDTDScannerImpl(SymbolTable symbolTable, XMLErrorReporter errorReporter, XMLEntityManager entityManager)
/*      */   {
/*  229 */     this.fSymbolTable = symbolTable;
/*  230 */     this.fErrorReporter = errorReporter;
/*  231 */     this.fEntityManager = entityManager;
/*  232 */     entityManager.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
/*      */   }
/*      */ 
/*      */   public void setInputSource(XMLInputSource inputSource)
/*      */     throws IOException
/*      */   {
/*  247 */     if (inputSource == null)
/*      */     {
/*  249 */       if (this.fDTDHandler != null) {
/*  250 */         this.fDTDHandler.startDTD(null, null);
/*  251 */         this.fDTDHandler.endDTD(null);
/*      */       }
/*  253 */       if (this.nonValidatingMode) {
/*  254 */         this.nvGrammarInfo.startDTD(null, null);
/*  255 */         this.nvGrammarInfo.endDTD(null);
/*      */       }
/*  257 */       return;
/*      */     }
/*  259 */     this.fEntityManager.setEntityHandler(this);
/*  260 */     this.fEntityManager.startDTDEntity(inputSource);
/*      */   }
/*      */ 
/*      */   public boolean scanDTDExternalSubset(boolean complete)
/*      */     throws IOException, XNIException
/*      */   {
/*  279 */     this.fEntityManager.setEntityHandler(this);
/*  280 */     if (this.fScannerState == 1) {
/*  281 */       this.fSeenExternalDTD = true;
/*  282 */       boolean textDecl = scanTextDecl();
/*  283 */       if (this.fScannerState == 0) {
/*  284 */         return false;
/*      */       }
/*      */ 
/*  289 */       setScannerState(2);
/*  290 */       if ((textDecl) && (!complete)) {
/*  291 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*      */     do
/*      */     {
/*  297 */       if (!scanDecls(complete))
/*  298 */         return false;
/*      */     }
/*  300 */     while (complete);
/*      */ 
/*  303 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean scanDTDInternalSubset(boolean complete, boolean standalone, boolean hasExternalSubset)
/*      */     throws IOException, XNIException
/*      */   {
/*  334 */     this.fEntityScanner = this.fEntityManager.getEntityScanner();
/*  335 */     this.fEntityManager.setEntityHandler(this);
/*  336 */     this.fStandalone = standalone;
/*      */ 
/*  338 */     if (this.fScannerState == 1)
/*      */     {
/*  340 */       if (this.fDTDHandler != null) {
/*  341 */         this.fDTDHandler.startDTD(this.fEntityScanner, null);
/*  342 */         this.fStartDTDCalled = true;
/*      */       }
/*      */ 
/*  345 */       if (this.nonValidatingMode) {
/*  346 */         this.fStartDTDCalled = true;
/*  347 */         this.nvGrammarInfo.startDTD(this.fEntityScanner, null);
/*      */       }
/*      */ 
/*  350 */       setScannerState(2);
/*      */     }
/*      */     do
/*      */     {
/*  354 */       if (!scanDecls(complete))
/*      */       {
/*  356 */         if ((this.fDTDHandler != null) && (!hasExternalSubset)) {
/*  357 */           this.fDTDHandler.endDTD(null);
/*      */         }
/*  359 */         if ((this.nonValidatingMode) && (!hasExternalSubset)) {
/*  360 */           this.nvGrammarInfo.endDTD(null);
/*      */         }
/*      */ 
/*  363 */         setScannerState(1);
/*  364 */         return false;
/*      */       }
/*      */     }
/*  366 */     while (complete);
/*      */ 
/*  369 */     return true;
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/*  385 */     super.reset(componentManager);
/*  386 */     init();
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*  392 */     super.reset();
/*  393 */     init();
/*      */   }
/*      */ 
/*      */   public void reset(PropertyManager props)
/*      */   {
/*  398 */     setPropertyManager(props);
/*  399 */     super.reset(props);
/*  400 */     init();
/*  401 */     this.nonValidatingMode = true;
/*      */ 
/*  403 */     this.nvGrammarInfo = new DTDGrammar(this.fSymbolTable);
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedFeatures()
/*      */   {
/*  411 */     return (String[])RECOGNIZED_FEATURES.clone();
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedProperties()
/*      */   {
/*  420 */     return (String[])RECOGNIZED_PROPERTIES.clone();
/*      */   }
/*      */ 
/*      */   public Boolean getFeatureDefault(String featureId)
/*      */   {
/*  433 */     for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
/*  434 */       if (RECOGNIZED_FEATURES[i].equals(featureId)) {
/*  435 */         return FEATURE_DEFAULTS[i];
/*      */       }
/*      */     }
/*  438 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getPropertyDefault(String propertyId)
/*      */   {
/*  451 */     for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
/*  452 */       if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
/*  453 */         return PROPERTY_DEFAULTS[i];
/*      */       }
/*      */     }
/*  456 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDTDHandler(XMLDTDHandler dtdHandler)
/*      */   {
/*  469 */     this.fDTDHandler = dtdHandler;
/*      */   }
/*      */ 
/*      */   public XMLDTDHandler getDTDHandler()
/*      */   {
/*  478 */     return this.fDTDHandler;
/*      */   }
/*      */ 
/*      */   public void setDTDContentModelHandler(XMLDTDContentModelHandler dtdContentModelHandler)
/*      */   {
/*  492 */     this.fDTDContentModelHandler = dtdContentModelHandler;
/*      */   }
/*      */ 
/*      */   public XMLDTDContentModelHandler getDTDContentModelHandler()
/*      */   {
/*  501 */     return this.fDTDContentModelHandler;
/*      */   }
/*      */ 
/*      */   public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  528 */     super.startEntity(name, identifier, encoding, augs);
/*      */ 
/*  530 */     boolean dtdEntity = name.equals("[dtd]");
/*  531 */     if (dtdEntity)
/*      */     {
/*  533 */       if ((this.fDTDHandler != null) && (!this.fStartDTDCalled)) {
/*  534 */         this.fDTDHandler.startDTD(this.fEntityScanner, null);
/*      */       }
/*  536 */       if (this.fDTDHandler != null) {
/*  537 */         this.fDTDHandler.startExternalSubset(identifier, null);
/*      */       }
/*  539 */       this.fEntityManager.startExternalSubset();
/*  540 */       this.fEntityStore.startExternalSubset();
/*  541 */       this.fExtEntityDepth += 1;
/*      */     }
/*  543 */     else if (name.charAt(0) == '%') {
/*  544 */       pushPEStack(this.fMarkUpDepth, this.fReportEntity);
/*  545 */       if (this.fEntityScanner.isExternal()) {
/*  546 */         this.fExtEntityDepth += 1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  551 */     if ((this.fDTDHandler != null) && (!dtdEntity) && (this.fReportEntity))
/*  552 */       this.fDTDHandler.startParameterEntity(name, identifier, encoding, null);
/*      */   }
/*      */ 
/*      */   public void endEntity(String name, Augmentations augs)
/*      */     throws XNIException, IOException
/*      */   {
/*  569 */     super.endEntity(name, augs);
/*      */ 
/*  573 */     if (this.fScannerState == 0) {
/*  574 */       return;
/*      */     }
/*      */ 
/*  577 */     boolean reportEntity = this.fReportEntity;
/*  578 */     if (name.startsWith("%")) {
/*  579 */       reportEntity = peekReportEntity();
/*      */ 
/*  581 */       int startMarkUpDepth = popPEStack();
/*      */ 
/*  584 */       if ((startMarkUpDepth == 0) && (startMarkUpDepth < this.fMarkUpDepth))
/*      */       {
/*  586 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ILL_FORMED_PARAMETER_ENTITY_WHEN_USED_IN_DECL", new Object[] { this.fEntityManager.fCurrentEntity.name }, (short)2);
/*      */       }
/*      */ 
/*  591 */       if (startMarkUpDepth != this.fMarkUpDepth) {
/*  592 */         reportEntity = false;
/*  593 */         if (this.fValidation)
/*      */         {
/*  596 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ImproperDeclarationNesting", new Object[] { name }, (short)1);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  602 */       if (this.fEntityScanner.isExternal()) {
/*  603 */         this.fExtEntityDepth -= 1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  608 */     boolean dtdEntity = name.equals("[dtd]");
/*  609 */     if ((this.fDTDHandler != null) && (!dtdEntity) && (reportEntity)) {
/*  610 */       this.fDTDHandler.endParameterEntity(name, null);
/*      */     }
/*      */ 
/*  614 */     if (dtdEntity) {
/*  615 */       if (this.fIncludeSectDepth != 0) {
/*  616 */         reportFatalError("IncludeSectUnterminated", null);
/*      */       }
/*  618 */       this.fScannerState = 0;
/*      */ 
/*  620 */       this.fEntityManager.endExternalSubset();
/*  621 */       this.fEntityStore.endExternalSubset();
/*      */ 
/*  623 */       if (this.fDTDHandler != null) {
/*  624 */         this.fDTDHandler.endExternalSubset(null);
/*  625 */         this.fDTDHandler.endDTD(null);
/*      */       }
/*  627 */       this.fExtEntityDepth -= 1;
/*      */     }
/*      */ 
/*  637 */     if ((augs != null) && (Boolean.TRUE.equals(augs.getItem("LAST_ENTITY"))) && ((this.fMarkUpDepth != 0) || (this.fExtEntityDepth != 0) || (this.fIncludeSectDepth != 0)))
/*      */     {
/*  639 */       throw new EOFException();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final void setScannerState(int state)
/*      */   {
/*  653 */     this.fScannerState = state;
/*      */   }
/*      */ 
/*      */   private static String getScannerStateName(int state)
/*      */   {
/*  677 */     return "??? (" + state + ')';
/*      */   }
/*      */ 
/*      */   protected final boolean scanningInternalSubset()
/*      */   {
/*  682 */     return this.fExtEntityDepth == 0;
/*      */   }
/*      */ 
/*      */   protected void startPE(String name, boolean literal)
/*      */     throws IOException, XNIException
/*      */   {
/*  693 */     int depth = this.fPEDepth;
/*  694 */     String pName = "%" + name;
/*  695 */     if ((this.fValidation) && (!this.fEntityStore.isDeclaredEntity(pName))) {
/*  696 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { name }, (short)1);
/*      */     }
/*      */ 
/*  699 */     this.fEntityManager.startEntity(this.fSymbolTable.addSymbol(pName), literal);
/*      */ 
/*  703 */     if ((depth != this.fPEDepth) && (this.fEntityScanner.isExternal()))
/*  704 */       scanTextDecl();
/*      */   }
/*      */ 
/*      */   protected final boolean scanTextDecl()
/*      */     throws IOException, XNIException
/*      */   {
/*  724 */     boolean textDecl = false;
/*  725 */     if (this.fEntityScanner.skipString("<?xml")) {
/*  726 */       this.fMarkUpDepth += 1;
/*      */ 
/*  729 */       if (isValidNameChar(this.fEntityScanner.peekChar())) {
/*  730 */         this.fStringBuffer.clear();
/*  731 */         this.fStringBuffer.append("xml");
/*  732 */         while (isValidNameChar(this.fEntityScanner.peekChar())) {
/*  733 */           this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
/*      */         }
/*  735 */         String target = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
/*      */ 
/*  739 */         scanPIData(target, this.fString);
/*      */       }
/*      */       else
/*      */       {
/*  745 */         String version = null;
/*  746 */         String encoding = null;
/*      */ 
/*  748 */         scanXMLDeclOrTextDecl(true, this.fStrings);
/*  749 */         textDecl = true;
/*  750 */         this.fMarkUpDepth -= 1;
/*      */ 
/*  752 */         version = this.fStrings[0];
/*  753 */         encoding = this.fStrings[1];
/*      */ 
/*  755 */         this.fEntityScanner.setEncoding(encoding);
/*      */ 
/*  758 */         if (this.fDTDHandler != null) {
/*  759 */           this.fDTDHandler.textDecl(version, encoding, null);
/*      */         }
/*      */       }
/*      */     }
/*  763 */     this.fEntityManager.fCurrentEntity.mayReadChunks = true;
/*      */ 
/*  765 */     return textDecl;
/*      */   }
/*      */ 
/*      */   protected final void scanPIData(String target, XMLString data)
/*      */     throws IOException, XNIException
/*      */   {
/*  781 */     this.fMarkUpDepth -= 1;
/*      */ 
/*  784 */     if (this.fDTDHandler != null)
/*  785 */       this.fDTDHandler.processingInstruction(target, data, null);
/*      */   }
/*      */ 
/*      */   protected final void scanComment()
/*      */     throws IOException, XNIException
/*      */   {
/*  801 */     this.fReportEntity = false;
/*  802 */     scanComment(this.fStringBuffer);
/*  803 */     this.fMarkUpDepth -= 1;
/*      */ 
/*  806 */     if (this.fDTDHandler != null) {
/*  807 */       this.fDTDHandler.comment(this.fStringBuffer, null);
/*      */     }
/*  809 */     this.fReportEntity = true;
/*      */   }
/*      */ 
/*      */   protected final void scanElementDecl()
/*      */     throws IOException, XNIException
/*      */   {
/*  826 */     this.fReportEntity = false;
/*  827 */     if (!skipSeparator(true, !scanningInternalSubset())) {
/*  828 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL", null);
/*      */     }
/*      */ 
/*  833 */     String name = this.fEntityScanner.scanName();
/*  834 */     if (name == null) {
/*  835 */       reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL", null);
/*      */     }
/*      */ 
/*  840 */     if (!skipSeparator(true, !scanningInternalSubset())) {
/*  841 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL", new Object[] { name });
/*      */     }
/*      */ 
/*  846 */     if (this.fDTDContentModelHandler != null) {
/*  847 */       this.fDTDContentModelHandler.startContentModel(name, null);
/*      */     }
/*  849 */     String contentModel = null;
/*  850 */     this.fReportEntity = true;
/*  851 */     if (this.fEntityScanner.skipString("EMPTY")) {
/*  852 */       contentModel = "EMPTY";
/*      */ 
/*  854 */       if (this.fDTDContentModelHandler != null) {
/*  855 */         this.fDTDContentModelHandler.empty(null);
/*      */       }
/*      */     }
/*  858 */     else if (this.fEntityScanner.skipString("ANY")) {
/*  859 */       contentModel = "ANY";
/*      */ 
/*  861 */       if (this.fDTDContentModelHandler != null)
/*  862 */         this.fDTDContentModelHandler.any(null);
/*      */     }
/*      */     else
/*      */     {
/*  866 */       if (!this.fEntityScanner.skipChar(40)) {
/*  867 */         reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[] { name });
/*      */       }
/*      */ 
/*  870 */       if (this.fDTDContentModelHandler != null) {
/*  871 */         this.fDTDContentModelHandler.startGroup(null);
/*      */       }
/*  873 */       this.fStringBuffer.clear();
/*  874 */       this.fStringBuffer.append('(');
/*  875 */       this.fMarkUpDepth += 1;
/*  876 */       skipSeparator(false, !scanningInternalSubset());
/*      */ 
/*  879 */       if (this.fEntityScanner.skipString("#PCDATA")) {
/*  880 */         scanMixed(name);
/*      */       }
/*      */       else {
/*  883 */         scanChildren(name);
/*      */       }
/*  885 */       contentModel = this.fStringBuffer.toString();
/*      */     }
/*      */ 
/*  889 */     if (this.fDTDContentModelHandler != null) {
/*  890 */       this.fDTDContentModelHandler.endContentModel(null);
/*      */     }
/*      */ 
/*  893 */     this.fReportEntity = false;
/*  894 */     skipSeparator(false, !scanningInternalSubset());
/*      */ 
/*  896 */     if (!this.fEntityScanner.skipChar(62)) {
/*  897 */       reportFatalError("ElementDeclUnterminated", new Object[] { name });
/*      */     }
/*  899 */     this.fReportEntity = true;
/*  900 */     this.fMarkUpDepth -= 1;
/*      */ 
/*  903 */     if (this.fDTDHandler != null) {
/*  904 */       this.fDTDHandler.elementDecl(name, contentModel, null);
/*      */     }
/*  906 */     if (this.nonValidatingMode) this.nvGrammarInfo.elementDecl(name, contentModel, null);
/*      */   }
/*      */ 
/*      */   private final void scanMixed(String elName)
/*      */     throws IOException, XNIException
/*      */   {
/*  925 */     String childName = null;
/*      */ 
/*  927 */     this.fStringBuffer.append("#PCDATA");
/*      */ 
/*  929 */     if (this.fDTDContentModelHandler != null) {
/*  930 */       this.fDTDContentModelHandler.pcdata(null);
/*      */     }
/*  932 */     skipSeparator(false, !scanningInternalSubset());
/*  933 */     while (this.fEntityScanner.skipChar(124)) {
/*  934 */       this.fStringBuffer.append('|');
/*      */ 
/*  936 */       if (this.fDTDContentModelHandler != null) {
/*  937 */         this.fDTDContentModelHandler.separator((short)0, null);
/*      */       }
/*      */ 
/*  940 */       skipSeparator(false, !scanningInternalSubset());
/*      */ 
/*  942 */       childName = this.fEntityScanner.scanName();
/*  943 */       if (childName == null) {
/*  944 */         reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT", new Object[] { elName });
/*      */       }
/*      */ 
/*  947 */       this.fStringBuffer.append(childName);
/*      */ 
/*  949 */       if (this.fDTDContentModelHandler != null) {
/*  950 */         this.fDTDContentModelHandler.element(childName, null);
/*      */       }
/*  952 */       skipSeparator(false, !scanningInternalSubset());
/*      */     }
/*      */ 
/*  958 */     if (this.fEntityScanner.skipString(")*")) {
/*  959 */       this.fStringBuffer.append(")*");
/*      */ 
/*  961 */       if (this.fDTDContentModelHandler != null) {
/*  962 */         this.fDTDContentModelHandler.endGroup(null);
/*  963 */         this.fDTDContentModelHandler.occurrence((short)3, null);
/*      */       }
/*      */ 
/*      */     }
/*  967 */     else if (childName != null) {
/*  968 */       reportFatalError("MixedContentUnterminated", new Object[] { elName });
/*      */     }
/*  971 */     else if (this.fEntityScanner.skipChar(41)) {
/*  972 */       this.fStringBuffer.append(')');
/*      */ 
/*  974 */       if (this.fDTDContentModelHandler != null)
/*  975 */         this.fDTDContentModelHandler.endGroup(null);
/*      */     }
/*      */     else
/*      */     {
/*  979 */       reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[] { elName });
/*      */     }
/*      */ 
/*  982 */     this.fMarkUpDepth -= 1;
/*      */   }
/*      */ 
/*      */   private final void scanChildren(String elName)
/*      */     throws IOException, XNIException
/*      */   {
/* 1004 */     this.fContentDepth = 0;
/* 1005 */     pushContentStack(0);
/* 1006 */     int currentOp = 0;
/*      */     while (true)
/*      */     {
/* 1009 */       if (this.fEntityScanner.skipChar(40)) {
/* 1010 */         this.fMarkUpDepth += 1;
/* 1011 */         this.fStringBuffer.append('(');
/*      */ 
/* 1013 */         if (this.fDTDContentModelHandler != null) {
/* 1014 */           this.fDTDContentModelHandler.startGroup(null);
/*      */         }
/*      */ 
/* 1017 */         pushContentStack(currentOp);
/* 1018 */         currentOp = 0;
/* 1019 */         skipSeparator(false, !scanningInternalSubset());
/*      */       }
/*      */       else {
/* 1022 */         skipSeparator(false, !scanningInternalSubset());
/* 1023 */         String childName = this.fEntityScanner.scanName();
/* 1024 */         if (childName == null) {
/* 1025 */           reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[] { elName });
/*      */ 
/* 1027 */           return;
/*      */         }
/*      */ 
/* 1030 */         if (this.fDTDContentModelHandler != null) {
/* 1031 */           this.fDTDContentModelHandler.element(childName, null);
/*      */         }
/* 1033 */         this.fStringBuffer.append(childName);
/* 1034 */         int c = this.fEntityScanner.peekChar();
/* 1035 */         if ((c == 63) || (c == 42) || (c == 43))
/*      */         {
/* 1037 */           if (this.fDTDContentModelHandler != null)
/*      */           {
/*      */             short oc;
/*      */             short oc;
/* 1039 */             if (c == 63) {
/* 1040 */               oc = 2;
/*      */             }
/*      */             else
/*      */             {
/*      */               short oc;
/* 1042 */               if (c == 42) {
/* 1043 */                 oc = 3;
/*      */               }
/*      */               else
/* 1046 */                 oc = 4;
/*      */             }
/* 1048 */             this.fDTDContentModelHandler.occurrence(oc, null);
/*      */           }
/* 1050 */           this.fEntityScanner.scanChar();
/* 1051 */           this.fStringBuffer.append((char)c);
/*      */         }
/*      */         do {
/* 1054 */           skipSeparator(false, !scanningInternalSubset());
/* 1055 */           c = this.fEntityScanner.peekChar();
/* 1056 */           if ((c == 44) && (currentOp != 124)) {
/* 1057 */             currentOp = c;
/*      */ 
/* 1059 */             if (this.fDTDContentModelHandler != null) {
/* 1060 */               this.fDTDContentModelHandler.separator((short)1, null);
/*      */             }
/*      */ 
/* 1063 */             this.fEntityScanner.scanChar();
/* 1064 */             this.fStringBuffer.append(',');
/* 1065 */             break;
/*      */           }
/* 1067 */           if ((c == 124) && (currentOp != 44)) {
/* 1068 */             currentOp = c;
/*      */ 
/* 1070 */             if (this.fDTDContentModelHandler != null) {
/* 1071 */               this.fDTDContentModelHandler.separator((short)0, null);
/*      */             }
/*      */ 
/* 1074 */             this.fEntityScanner.scanChar();
/* 1075 */             this.fStringBuffer.append('|');
/* 1076 */             break;
/*      */           }
/* 1078 */           if (c != 41) {
/* 1079 */             reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[] { elName });
/*      */           }
/*      */ 
/* 1083 */           if (this.fDTDContentModelHandler != null) {
/* 1084 */             this.fDTDContentModelHandler.endGroup(null);
/*      */           }
/*      */ 
/* 1087 */           currentOp = popContentStack();
/*      */ 
/* 1094 */           if (this.fEntityScanner.skipString(")?")) {
/* 1095 */             this.fStringBuffer.append(")?");
/*      */ 
/* 1097 */             if (this.fDTDContentModelHandler != null) {
/* 1098 */               short oc = 2;
/* 1099 */               this.fDTDContentModelHandler.occurrence(oc, null);
/*      */             }
/*      */           }
/* 1102 */           else if (this.fEntityScanner.skipString(")+")) {
/* 1103 */             this.fStringBuffer.append(")+");
/*      */ 
/* 1105 */             if (this.fDTDContentModelHandler != null) {
/* 1106 */               short oc = 4;
/* 1107 */               this.fDTDContentModelHandler.occurrence(oc, null);
/*      */             }
/*      */           }
/* 1110 */           else if (this.fEntityScanner.skipString(")*")) {
/* 1111 */             this.fStringBuffer.append(")*");
/*      */ 
/* 1113 */             if (this.fDTDContentModelHandler != null) {
/* 1114 */               short oc = 3;
/* 1115 */               this.fDTDContentModelHandler.occurrence(oc, null);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1120 */             this.fEntityScanner.scanChar();
/* 1121 */             this.fStringBuffer.append(')');
/*      */           }
/* 1123 */           this.fMarkUpDepth -= 1;
/* 1124 */         }while (this.fContentDepth != 0);
/* 1125 */         return;
/*      */ 
/* 1128 */         skipSeparator(false, !scanningInternalSubset());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final void scanAttlistDecl()
/*      */     throws IOException, XNIException
/*      */   {
/* 1145 */     this.fReportEntity = false;
/* 1146 */     if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1147 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL", null);
/*      */     }
/*      */ 
/* 1152 */     String elName = this.fEntityScanner.scanName();
/* 1153 */     if (elName == null) {
/* 1154 */       reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL", null);
/*      */     }
/*      */ 
/* 1159 */     if (this.fDTDHandler != null) {
/* 1160 */       this.fDTDHandler.startAttlist(elName, null);
/*      */     }
/*      */ 
/* 1164 */     if (!skipSeparator(true, !scanningInternalSubset()))
/*      */     {
/* 1166 */       if (this.fEntityScanner.skipChar(62))
/*      */       {
/* 1169 */         if (this.fDTDHandler != null) {
/* 1170 */           this.fDTDHandler.endAttlist(null);
/*      */         }
/* 1172 */         this.fMarkUpDepth -= 1;
/* 1173 */         return;
/*      */       }
/*      */ 
/* 1176 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF", new Object[] { elName });
/*      */     }
/*      */ 
/* 1182 */     while (!this.fEntityScanner.skipChar(62)) {
/* 1183 */       String name = this.fEntityScanner.scanName();
/* 1184 */       if (name == null) {
/* 1185 */         reportFatalError("AttNameRequiredInAttDef", new Object[] { elName });
/*      */       }
/*      */ 
/* 1189 */       if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1190 */         reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF", new Object[] { elName, name });
/*      */       }
/*      */ 
/* 1194 */       String type = scanAttType(elName, name);
/*      */ 
/* 1197 */       if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1198 */         reportFatalError("MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF", new Object[] { elName, name });
/*      */       }
/*      */ 
/* 1203 */       String defaultType = scanAttDefaultDecl(elName, name, type, this.fLiteral, this.fLiteral2);
/*      */ 
/* 1209 */       String[] enumr = null;
/* 1210 */       if (((this.fDTDHandler != null) || (this.nonValidatingMode)) && 
/* 1211 */         (this.fEnumerationCount != 0)) {
/* 1212 */         enumr = new String[this.fEnumerationCount];
/* 1213 */         System.arraycopy(this.fEnumeration, 0, enumr, 0, this.fEnumerationCount);
/*      */       }
/*      */ 
/* 1220 */       if ((defaultType != null) && ((defaultType.equals("#REQUIRED")) || (defaultType.equals("#IMPLIED"))))
/*      */       {
/* 1222 */         if (this.fDTDHandler != null) {
/* 1223 */           this.fDTDHandler.attributeDecl(elName, name, type, enumr, defaultType, null, null, null);
/*      */         }
/*      */ 
/* 1226 */         if (this.nonValidatingMode) {
/* 1227 */           this.nvGrammarInfo.attributeDecl(elName, name, type, enumr, defaultType, null, null, null);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1233 */         if (this.fDTDHandler != null) {
/* 1234 */           this.fDTDHandler.attributeDecl(elName, name, type, enumr, defaultType, this.fLiteral, this.fLiteral2, null);
/*      */         }
/*      */ 
/* 1237 */         if (this.nonValidatingMode) {
/* 1238 */           this.nvGrammarInfo.attributeDecl(elName, name, type, enumr, defaultType, this.fLiteral, this.fLiteral2, null);
/*      */         }
/*      */       }
/*      */ 
/* 1242 */       skipSeparator(false, !scanningInternalSubset());
/*      */     }
/*      */ 
/* 1246 */     if (this.fDTDHandler != null) {
/* 1247 */       this.fDTDHandler.endAttlist(null);
/*      */     }
/* 1249 */     this.fMarkUpDepth -= 1;
/* 1250 */     this.fReportEntity = true;
/*      */   }
/*      */ 
/*      */   private final String scanAttType(String elName, String atName)
/*      */     throws IOException, XNIException
/*      */   {
/* 1280 */     String type = null;
/* 1281 */     this.fEnumerationCount = 0;
/*      */ 
/* 1287 */     if (this.fEntityScanner.skipString("CDATA")) {
/* 1288 */       type = "CDATA";
/*      */     }
/* 1290 */     else if (this.fEntityScanner.skipString("IDREFS")) {
/* 1291 */       type = "IDREFS";
/*      */     }
/* 1293 */     else if (this.fEntityScanner.skipString("IDREF")) {
/* 1294 */       type = "IDREF";
/*      */     }
/* 1296 */     else if (this.fEntityScanner.skipString("ID")) {
/* 1297 */       type = "ID";
/*      */     }
/* 1299 */     else if (this.fEntityScanner.skipString("ENTITY")) {
/* 1300 */       type = "ENTITY";
/*      */     }
/* 1302 */     else if (this.fEntityScanner.skipString("ENTITIES")) {
/* 1303 */       type = "ENTITIES";
/*      */     }
/* 1305 */     else if (this.fEntityScanner.skipString("NMTOKENS")) {
/* 1306 */       type = "NMTOKENS";
/*      */     }
/* 1308 */     else if (this.fEntityScanner.skipString("NMTOKEN")) {
/* 1309 */       type = "NMTOKEN";
/*      */     }
/* 1311 */     else if (this.fEntityScanner.skipString("NOTATION")) {
/* 1312 */       type = "NOTATION";
/*      */ 
/* 1314 */       if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1315 */         reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE", new Object[] { elName, atName });
/*      */       }
/*      */ 
/* 1319 */       int c = this.fEntityScanner.scanChar();
/* 1320 */       if (c != 40) {
/* 1321 */         reportFatalError("MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE", new Object[] { elName, atName });
/*      */       }
/*      */ 
/* 1324 */       this.fMarkUpDepth += 1;
/*      */       do {
/* 1326 */         skipSeparator(false, !scanningInternalSubset());
/* 1327 */         String aName = this.fEntityScanner.scanName();
/* 1328 */         if (aName == null) {
/* 1329 */           reportFatalError("MSG_NAME_REQUIRED_IN_NOTATIONTYPE", new Object[] { elName, atName });
/*      */         }
/*      */ 
/* 1332 */         ensureEnumerationSize(this.fEnumerationCount + 1);
/* 1333 */         this.fEnumeration[(this.fEnumerationCount++)] = aName;
/* 1334 */         skipSeparator(false, !scanningInternalSubset());
/* 1335 */         c = this.fEntityScanner.scanChar();
/* 1336 */       }while (c == 124);
/* 1337 */       if (c != 41) {
/* 1338 */         reportFatalError("NotationTypeUnterminated", new Object[] { elName, atName });
/*      */       }
/*      */ 
/* 1341 */       this.fMarkUpDepth -= 1;
/*      */     }
/*      */     else {
/* 1344 */       type = "ENUMERATION";
/*      */ 
/* 1346 */       int c = this.fEntityScanner.scanChar();
/* 1347 */       if (c != 40)
/*      */       {
/* 1349 */         reportFatalError("AttTypeRequiredInAttDef", new Object[] { elName, atName });
/*      */       }
/*      */ 
/* 1352 */       this.fMarkUpDepth += 1;
/*      */       do {
/* 1354 */         skipSeparator(false, !scanningInternalSubset());
/* 1355 */         String token = this.fEntityScanner.scanNmtoken();
/* 1356 */         if (token == null) {
/* 1357 */           reportFatalError("MSG_NMTOKEN_REQUIRED_IN_ENUMERATION", new Object[] { elName, atName });
/*      */         }
/*      */ 
/* 1360 */         ensureEnumerationSize(this.fEnumerationCount + 1);
/* 1361 */         this.fEnumeration[(this.fEnumerationCount++)] = token;
/* 1362 */         skipSeparator(false, !scanningInternalSubset());
/* 1363 */         c = this.fEntityScanner.scanChar();
/* 1364 */       }while (c == 124);
/* 1365 */       if (c != 41) {
/* 1366 */         reportFatalError("EnumerationUnterminated", new Object[] { elName, atName });
/*      */       }
/*      */ 
/* 1369 */       this.fMarkUpDepth -= 1;
/*      */     }
/* 1371 */     return type;
/*      */   }
/*      */ 
/*      */   protected final String scanAttDefaultDecl(String elName, String atName, String type, XMLString defaultVal, XMLString nonNormalizedDefaultVal)
/*      */     throws IOException, XNIException
/*      */   {
/* 1392 */     String defaultType = null;
/* 1393 */     this.fString.clear();
/* 1394 */     defaultVal.clear();
/* 1395 */     if (this.fEntityScanner.skipString("#REQUIRED")) {
/* 1396 */       defaultType = "#REQUIRED";
/*      */     }
/* 1398 */     else if (this.fEntityScanner.skipString("#IMPLIED")) {
/* 1399 */       defaultType = "#IMPLIED";
/*      */     }
/*      */     else {
/* 1402 */       if (this.fEntityScanner.skipString("#FIXED")) {
/* 1403 */         defaultType = "#FIXED";
/*      */ 
/* 1405 */         if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1406 */           reportFatalError("MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL", new Object[] { elName, atName });
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1411 */       boolean isVC = (!this.fStandalone) && ((this.fSeenExternalDTD) || (this.fSeenExternalPE));
/* 1412 */       scanAttributeValue(defaultVal, nonNormalizedDefaultVal, atName, this.fAttributes, 0, isVC);
/*      */     }
/*      */ 
/* 1415 */     return defaultType;
/*      */   }
/*      */ 
/*      */   private final void scanEntityDecl()
/*      */     throws IOException, XNIException
/*      */   {
/* 1437 */     boolean isPEDecl = false;
/* 1438 */     boolean sawPERef = false;
/* 1439 */     this.fReportEntity = false;
/* 1440 */     if (this.fEntityScanner.skipSpaces()) {
/* 1441 */       if (!this.fEntityScanner.skipChar(37)) {
/* 1442 */         isPEDecl = false;
/*      */       }
/* 1444 */       else if (skipSeparator(true, !scanningInternalSubset()))
/*      */       {
/* 1446 */         isPEDecl = true;
/*      */       }
/* 1448 */       else if (scanningInternalSubset()) {
/* 1449 */         reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", null);
/*      */ 
/* 1451 */         isPEDecl = true;
/*      */       }
/* 1453 */       else if (this.fEntityScanner.peekChar() == 37)
/*      */       {
/* 1455 */         skipSeparator(false, !scanningInternalSubset());
/* 1456 */         isPEDecl = true;
/*      */       }
/*      */       else {
/* 1459 */         sawPERef = true;
/*      */       }
/*      */     }
/* 1462 */     else if ((scanningInternalSubset()) || (!this.fEntityScanner.skipChar(37)))
/*      */     {
/* 1464 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", null);
/*      */ 
/* 1466 */       isPEDecl = false;
/*      */     }
/* 1468 */     else if (this.fEntityScanner.skipSpaces())
/*      */     {
/* 1470 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL", null);
/*      */ 
/* 1472 */       isPEDecl = false;
/*      */     }
/*      */     else {
/* 1475 */       sawPERef = true;
/*      */     }
/* 1477 */     if (sawPERef) {
/*      */       while (true) {
/* 1479 */         String peName = this.fEntityScanner.scanName();
/* 1480 */         if (peName == null) {
/* 1481 */           reportFatalError("NameRequiredInPEReference", null);
/*      */         }
/* 1483 */         else if (!this.fEntityScanner.skipChar(59)) {
/* 1484 */           reportFatalError("SemicolonRequiredInPEReference", new Object[] { peName });
/*      */         }
/*      */         else
/*      */         {
/* 1488 */           startPE(peName, false);
/*      */         }
/* 1490 */         this.fEntityScanner.skipSpaces();
/* 1491 */         if (!this.fEntityScanner.skipChar(37))
/*      */           break;
/* 1493 */         if (!isPEDecl) {
/* 1494 */           if (skipSeparator(true, !scanningInternalSubset())) {
/* 1495 */             isPEDecl = true;
/* 1496 */             break;
/*      */           }
/* 1498 */           isPEDecl = this.fEntityScanner.skipChar(37);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1504 */     String name = this.fEntityScanner.scanName();
/* 1505 */     if (name == null) {
/* 1506 */       reportFatalError("MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL", null);
/*      */     }
/*      */ 
/* 1510 */     if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1511 */       reportFatalError("MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL", new Object[] { name });
/*      */     }
/*      */ 
/* 1516 */     scanExternalID(this.fStrings, false);
/* 1517 */     String systemId = this.fStrings[0];
/* 1518 */     String publicId = this.fStrings[1];
/*      */ 
/* 1520 */     if ((isPEDecl) && (systemId != null)) {
/* 1521 */       this.fSeenExternalPE = true;
/*      */     }
/*      */ 
/* 1524 */     String notation = null;
/*      */ 
/* 1526 */     boolean sawSpace = skipSeparator(true, !scanningInternalSubset());
/* 1527 */     if ((!isPEDecl) && (this.fEntityScanner.skipString("NDATA")))
/*      */     {
/* 1529 */       if (!sawSpace) {
/* 1530 */         reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL", new Object[] { name });
/*      */       }
/*      */ 
/* 1535 */       if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1536 */         reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL", new Object[] { name });
/*      */       }
/*      */ 
/* 1539 */       notation = this.fEntityScanner.scanName();
/* 1540 */       if (notation == null) {
/* 1541 */         reportFatalError("MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL", new Object[] { name });
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1547 */     if (systemId == null) {
/* 1548 */       scanEntityValue(this.fLiteral, this.fLiteral2);
/*      */ 
/* 1551 */       this.fStringBuffer.clear();
/* 1552 */       this.fStringBuffer2.clear();
/* 1553 */       this.fStringBuffer.append(this.fLiteral.ch, this.fLiteral.offset, this.fLiteral.length);
/* 1554 */       this.fStringBuffer2.append(this.fLiteral2.ch, this.fLiteral2.offset, this.fLiteral2.length);
/*      */     }
/*      */ 
/* 1558 */     skipSeparator(false, !scanningInternalSubset());
/*      */ 
/* 1561 */     if (!this.fEntityScanner.skipChar(62)) {
/* 1562 */       reportFatalError("EntityDeclUnterminated", new Object[] { name });
/*      */     }
/* 1564 */     this.fMarkUpDepth -= 1;
/*      */ 
/* 1567 */     if (isPEDecl) {
/* 1568 */       name = "%" + name;
/*      */     }
/* 1570 */     if (systemId != null) {
/* 1571 */       String baseSystemId = this.fEntityScanner.getBaseSystemId();
/* 1572 */       if (notation != null) {
/* 1573 */         this.fEntityStore.addUnparsedEntity(name, publicId, systemId, baseSystemId, notation);
/*      */       }
/*      */       else {
/* 1576 */         this.fEntityStore.addExternalEntity(name, publicId, systemId, baseSystemId);
/*      */       }
/*      */ 
/* 1579 */       if (this.fDTDHandler != null)
/*      */       {
/* 1581 */         this.fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId));
/*      */ 
/* 1583 */         if (notation != null) {
/* 1584 */           this.fDTDHandler.unparsedEntityDecl(name, this.fResourceIdentifier, notation, null);
/*      */         }
/*      */         else
/*      */         {
/* 1588 */           this.fDTDHandler.externalEntityDecl(name, this.fResourceIdentifier, null);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1593 */       this.fEntityStore.addInternalEntity(name, this.fStringBuffer.toString());
/* 1594 */       if (this.fDTDHandler != null) {
/* 1595 */         this.fDTDHandler.internalEntityDecl(name, this.fStringBuffer, this.fStringBuffer2, null);
/*      */       }
/*      */     }
/* 1598 */     this.fReportEntity = true;
/*      */   }
/*      */ 
/*      */   protected final void scanEntityValue(XMLString value, XMLString nonNormalizedValue)
/*      */     throws IOException, XNIException
/*      */   {
/* 1616 */     int quote = this.fEntityScanner.scanChar();
/* 1617 */     if ((quote != 39) && (quote != 34)) {
/* 1618 */       reportFatalError("OpenQuoteMissingInDecl", null);
/*      */     }
/*      */ 
/* 1621 */     int entityDepth = this.fEntityDepth;
/*      */ 
/* 1623 */     XMLString literal = this.fString;
/* 1624 */     XMLString literal2 = this.fString;
/* 1625 */     if (this.fEntityScanner.scanLiteral(quote, this.fString) != quote) {
/* 1626 */       this.fStringBuffer.clear();
/* 1627 */       this.fStringBuffer2.clear();
/*      */       do {
/* 1629 */         this.fStringBuffer.append(this.fString);
/* 1630 */         this.fStringBuffer2.append(this.fString);
/* 1631 */         if (this.fEntityScanner.skipChar(38)) {
/* 1632 */           if (this.fEntityScanner.skipChar(35)) {
/* 1633 */             this.fStringBuffer2.append("&#");
/* 1634 */             scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2);
/*      */           }
/*      */           else {
/* 1637 */             this.fStringBuffer.append('&');
/* 1638 */             this.fStringBuffer2.append('&');
/* 1639 */             String eName = this.fEntityScanner.scanName();
/* 1640 */             if (eName == null) {
/* 1641 */               reportFatalError("NameRequiredInReference", null);
/*      */             }
/*      */             else
/*      */             {
/* 1645 */               this.fStringBuffer.append(eName);
/* 1646 */               this.fStringBuffer2.append(eName);
/*      */             }
/* 1648 */             if (!this.fEntityScanner.skipChar(59)) {
/* 1649 */               reportFatalError("SemicolonRequiredInReference", new Object[] { eName });
/*      */             }
/*      */             else
/*      */             {
/* 1653 */               this.fStringBuffer.append(';');
/* 1654 */               this.fStringBuffer2.append(';');
/*      */             }
/*      */           }
/*      */         } else {
/* 1658 */           if (this.fEntityScanner.skipChar(37)) {
/*      */             while (true) {
/* 1660 */               this.fStringBuffer2.append('%');
/* 1661 */               String peName = this.fEntityScanner.scanName();
/* 1662 */               if (peName == null) {
/* 1663 */                 reportFatalError("NameRequiredInPEReference", null);
/*      */               }
/* 1666 */               else if (!this.fEntityScanner.skipChar(59)) {
/* 1667 */                 reportFatalError("SemicolonRequiredInPEReference", new Object[] { peName });
/*      */               }
/*      */               else
/*      */               {
/* 1671 */                 if (scanningInternalSubset()) {
/* 1672 */                   reportFatalError("PEReferenceWithinMarkup", new Object[] { peName });
/*      */                 }
/*      */ 
/* 1675 */                 this.fStringBuffer2.append(peName);
/* 1676 */                 this.fStringBuffer2.append(';');
/*      */               }
/* 1678 */               startPE(peName, true);
/*      */ 
/* 1682 */               this.fEntityScanner.skipSpaces();
/* 1683 */               if (!this.fEntityScanner.skipChar(37)) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/* 1688 */           int c = this.fEntityScanner.peekChar();
/* 1689 */           if (XMLChar.isHighSurrogate(c)) {
/* 1690 */             scanSurrogates(this.fStringBuffer2);
/*      */           }
/* 1692 */           else if (isInvalidLiteral(c)) {
/* 1693 */             reportFatalError("InvalidCharInLiteral", new Object[] { Integer.toHexString(c) });
/*      */ 
/* 1695 */             this.fEntityScanner.scanChar();
/*      */           }
/* 1700 */           else if ((c != quote) || (entityDepth != this.fEntityDepth)) {
/* 1701 */             this.fStringBuffer.append((char)c);
/* 1702 */             this.fStringBuffer2.append((char)c);
/* 1703 */             this.fEntityScanner.scanChar();
/*      */           }
/*      */         }
/*      */       }
/* 1706 */       while (this.fEntityScanner.scanLiteral(quote, this.fString) != quote);
/* 1707 */       this.fStringBuffer.append(this.fString);
/* 1708 */       this.fStringBuffer2.append(this.fString);
/* 1709 */       literal = this.fStringBuffer;
/* 1710 */       literal2 = this.fStringBuffer2;
/*      */     }
/* 1712 */     value.setValues(literal);
/* 1713 */     nonNormalizedValue.setValues(literal2);
/* 1714 */     if (!this.fEntityScanner.skipChar(quote))
/* 1715 */       reportFatalError("CloseQuoteMissingInDecl", null);
/*      */   }
/*      */ 
/*      */   private final void scanNotationDecl()
/*      */     throws IOException, XNIException
/*      */   {
/* 1732 */     this.fReportEntity = false;
/* 1733 */     if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1734 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL", null);
/*      */     }
/*      */ 
/* 1739 */     String name = this.fEntityScanner.scanName();
/* 1740 */     if (name == null) {
/* 1741 */       reportFatalError("MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL", null);
/*      */     }
/*      */ 
/* 1746 */     if (!skipSeparator(true, !scanningInternalSubset())) {
/* 1747 */       reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL", new Object[] { name });
/*      */     }
/*      */ 
/* 1752 */     scanExternalID(this.fStrings, true);
/* 1753 */     String systemId = this.fStrings[0];
/* 1754 */     String publicId = this.fStrings[1];
/* 1755 */     String baseSystemId = this.fEntityScanner.getBaseSystemId();
/*      */ 
/* 1757 */     if ((systemId == null) && (publicId == null)) {
/* 1758 */       reportFatalError("ExternalIDorPublicIDRequired", new Object[] { name });
/*      */     }
/*      */ 
/* 1763 */     skipSeparator(false, !scanningInternalSubset());
/*      */ 
/* 1766 */     if (!this.fEntityScanner.skipChar(62)) {
/* 1767 */       reportFatalError("NotationDeclUnterminated", new Object[] { name });
/*      */     }
/* 1769 */     this.fMarkUpDepth -= 1;
/*      */ 
/* 1771 */     this.fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId));
/* 1772 */     if (this.nonValidatingMode) this.nvGrammarInfo.notationDecl(name, this.fResourceIdentifier, null);
/*      */ 
/* 1774 */     if (this.fDTDHandler != null)
/*      */     {
/* 1777 */       this.fDTDHandler.notationDecl(name, this.fResourceIdentifier, null);
/*      */     }
/* 1779 */     this.fReportEntity = true;
/*      */   }
/*      */ 
/*      */   private final void scanConditionalSect(int currPEDepth)
/*      */     throws IOException, XNIException
/*      */   {
/* 1802 */     this.fReportEntity = false;
/* 1803 */     skipSeparator(false, !scanningInternalSubset());
/*      */ 
/* 1805 */     if (this.fEntityScanner.skipString("INCLUDE")) {
/* 1806 */       skipSeparator(false, !scanningInternalSubset());
/* 1807 */       if ((currPEDepth != this.fPEDepth) && (this.fValidation)) {
/* 1808 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[] { this.fEntityManager.fCurrentEntity.name }, (short)1);
/*      */       }
/*      */ 
/* 1814 */       if (!this.fEntityScanner.skipChar(91)) {
/* 1815 */         reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
/*      */       }
/*      */ 
/* 1818 */       if (this.fDTDHandler != null) {
/* 1819 */         this.fDTDHandler.startConditional((short)0, null);
/*      */       }
/*      */ 
/* 1822 */       this.fIncludeSectDepth += 1;
/*      */ 
/* 1824 */       this.fReportEntity = true;
/*      */     } else {
/* 1826 */       if (this.fEntityScanner.skipString("IGNORE")) {
/* 1827 */         skipSeparator(false, !scanningInternalSubset());
/* 1828 */         if ((currPEDepth != this.fPEDepth) && (this.fValidation)) {
/* 1829 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[] { this.fEntityManager.fCurrentEntity.name }, (short)1);
/*      */         }
/*      */ 
/* 1835 */         if (this.fDTDHandler != null) {
/* 1836 */           this.fDTDHandler.startConditional((short)1, null);
/*      */         }
/*      */ 
/* 1839 */         if (!this.fEntityScanner.skipChar(91)) {
/* 1840 */           reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
/*      */         }
/* 1842 */         this.fReportEntity = true;
/* 1843 */         int initialDepth = ++this.fIncludeSectDepth;
/* 1844 */         if (this.fDTDHandler != null) {
/* 1845 */           this.fIgnoreConditionalBuffer.clear();
/*      */         }
/*      */         while (true) {
/* 1848 */           if (this.fEntityScanner.skipChar(60)) {
/* 1849 */             if (this.fDTDHandler != null) {
/* 1850 */               this.fIgnoreConditionalBuffer.append('<');
/*      */             }
/*      */ 
/* 1856 */             if (this.fEntityScanner.skipChar(33)) {
/* 1857 */               if (this.fEntityScanner.skipChar(91)) {
/* 1858 */                 if (this.fDTDHandler != null) {
/* 1859 */                   this.fIgnoreConditionalBuffer.append("![");
/*      */                 }
/* 1861 */                 this.fIncludeSectDepth += 1;
/*      */               }
/* 1863 */               else if (this.fDTDHandler != null) {
/* 1864 */                 this.fIgnoreConditionalBuffer.append("!");
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/* 1869 */           else if (this.fEntityScanner.skipChar(93)) {
/* 1870 */             if (this.fDTDHandler != null) {
/* 1871 */               this.fIgnoreConditionalBuffer.append(']');
/*      */             }
/*      */ 
/* 1876 */             if (this.fEntityScanner.skipChar(93)) {
/* 1877 */               if (this.fDTDHandler != null) {
/* 1878 */                 this.fIgnoreConditionalBuffer.append(']');
/*      */               }
/* 1880 */               while (this.fEntityScanner.skipChar(93))
/*      */               {
/* 1882 */                 if (this.fDTDHandler != null) {
/* 1883 */                   this.fIgnoreConditionalBuffer.append(']');
/*      */                 }
/*      */               }
/* 1886 */               if (this.fEntityScanner.skipChar(62)) {
/* 1887 */                 if (this.fIncludeSectDepth-- == initialDepth) {
/* 1888 */                   this.fMarkUpDepth -= 1;
/*      */ 
/* 1890 */                   if (this.fDTDHandler != null) {
/* 1891 */                     this.fLiteral.setValues(this.fIgnoreConditionalBuffer.ch, 0, this.fIgnoreConditionalBuffer.length - 2);
/*      */ 
/* 1893 */                     this.fDTDHandler.ignoredCharacters(this.fLiteral, null);
/* 1894 */                     this.fDTDHandler.endConditional(null);
/*      */                   }
/* 1896 */                   return;
/* 1897 */                 }if (this.fDTDHandler != null)
/* 1898 */                   this.fIgnoreConditionalBuffer.append('>');
/*      */               }
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1904 */             int c = this.fEntityScanner.scanChar();
/* 1905 */             if (this.fScannerState == 0) {
/* 1906 */               reportFatalError("IgnoreSectUnterminated", null);
/* 1907 */               return;
/*      */             }
/* 1909 */             if (this.fDTDHandler != null) {
/* 1910 */               this.fIgnoreConditionalBuffer.append((char)c);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1916 */       reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final boolean scanDecls(boolean complete)
/*      */     throws IOException, XNIException
/*      */   {
/* 1936 */     skipSeparator(false, true);
/* 1937 */     boolean again = true;
/*      */ 
/* 1939 */     while ((again) && (this.fScannerState == 2)) {
/* 1940 */       again = complete;
/* 1941 */       if (this.fEntityScanner.skipChar(60)) {
/* 1942 */         this.fMarkUpDepth += 1;
/* 1943 */         if (this.fEntityScanner.skipChar(63)) {
/* 1944 */           this.fStringBuffer.clear();
/* 1945 */           scanPI(this.fStringBuffer);
/* 1946 */           this.fMarkUpDepth -= 1;
/*      */         }
/* 1948 */         else if (this.fEntityScanner.skipChar(33)) {
/* 1949 */           if (this.fEntityScanner.skipChar(45)) {
/* 1950 */             if (!this.fEntityScanner.skipChar(45)) {
/* 1951 */               reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
/*      */             }
/*      */             else {
/* 1954 */               scanComment();
/*      */             }
/*      */           }
/* 1957 */           else if (this.fEntityScanner.skipString("ELEMENT")) {
/* 1958 */             scanElementDecl();
/*      */           }
/* 1960 */           else if (this.fEntityScanner.skipString("ATTLIST")) {
/* 1961 */             scanAttlistDecl();
/*      */           }
/* 1963 */           else if (this.fEntityScanner.skipString("ENTITY")) {
/* 1964 */             scanEntityDecl();
/*      */           }
/* 1966 */           else if (this.fEntityScanner.skipString("NOTATION")) {
/* 1967 */             scanNotationDecl();
/*      */           }
/* 1969 */           else if ((this.fEntityScanner.skipChar(91)) && (!scanningInternalSubset()))
/*      */           {
/* 1971 */             scanConditionalSect(this.fPEDepth);
/*      */           }
/*      */           else {
/* 1974 */             this.fMarkUpDepth -= 1;
/* 1975 */             reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1980 */           this.fMarkUpDepth -= 1;
/* 1981 */           reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
/*      */         }
/*      */       }
/* 1984 */       else if ((this.fIncludeSectDepth > 0) && (this.fEntityScanner.skipChar(93)))
/*      */       {
/* 1986 */         if ((!this.fEntityScanner.skipChar(93)) || (!this.fEntityScanner.skipChar(62)))
/*      */         {
/* 1988 */           reportFatalError("IncludeSectUnterminated", null);
/*      */         }
/*      */ 
/* 1991 */         if (this.fDTDHandler != null) {
/* 1992 */           this.fDTDHandler.endConditional(null);
/*      */         }
/*      */ 
/* 1995 */         this.fIncludeSectDepth -= 1;
/* 1996 */         this.fMarkUpDepth -= 1;
/*      */       } else {
/* 1998 */         if ((scanningInternalSubset()) && (this.fEntityScanner.peekChar() == 93))
/*      */         {
/* 2001 */           return false;
/*      */         }
/* 2003 */         if (!this.fEntityScanner.skipSpaces())
/*      */         {
/* 2007 */           reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
/*      */         }
/*      */       }
/* 2009 */       skipSeparator(false, true);
/*      */     }
/* 2011 */     return this.fScannerState != 0;
/*      */   }
/*      */ 
/*      */   private boolean skipSeparator(boolean spaceRequired, boolean lookForPERefs)
/*      */     throws IOException, XNIException
/*      */   {
/* 2032 */     int depth = this.fPEDepth;
/* 2033 */     boolean sawSpace = this.fEntityScanner.skipSpaces();
/* 2034 */     if ((!lookForPERefs) || (!this.fEntityScanner.skipChar(37)))
/* 2035 */       return (!spaceRequired) || (sawSpace) || (depth != this.fPEDepth);
/*      */     while (true)
/*      */     {
/* 2038 */       String name = this.fEntityScanner.scanName();
/* 2039 */       if (name == null) {
/* 2040 */         reportFatalError("NameRequiredInPEReference", null);
/*      */       }
/* 2042 */       else if (!this.fEntityScanner.skipChar(59)) {
/* 2043 */         reportFatalError("SemicolonRequiredInPEReference", new Object[] { name });
/*      */       }
/*      */ 
/* 2046 */       startPE(name, false);
/* 2047 */       this.fEntityScanner.skipSpaces();
/* 2048 */       if (!this.fEntityScanner.skipChar(37))
/* 2049 */         return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void pushContentStack(int c)
/*      */   {
/* 2058 */     if (this.fContentStack.length == this.fContentDepth) {
/* 2059 */       int[] newStack = new int[this.fContentDepth * 2];
/* 2060 */       System.arraycopy(this.fContentStack, 0, newStack, 0, this.fContentDepth);
/* 2061 */       this.fContentStack = newStack;
/*      */     }
/* 2063 */     this.fContentStack[(this.fContentDepth++)] = c;
/*      */   }
/*      */ 
/*      */   private final int popContentStack() {
/* 2067 */     return this.fContentStack[(--this.fContentDepth)];
/*      */   }
/*      */ 
/*      */   private final void pushPEStack(int depth, boolean report)
/*      */   {
/* 2075 */     if (this.fPEStack.length == this.fPEDepth) {
/* 2076 */       int[] newIntStack = new int[this.fPEDepth * 2];
/* 2077 */       System.arraycopy(this.fPEStack, 0, newIntStack, 0, this.fPEDepth);
/* 2078 */       this.fPEStack = newIntStack;
/*      */ 
/* 2080 */       boolean[] newBooleanStack = new boolean[this.fPEDepth * 2];
/* 2081 */       System.arraycopy(this.fPEReport, 0, newBooleanStack, 0, this.fPEDepth);
/* 2082 */       this.fPEReport = newBooleanStack;
/*      */     }
/*      */ 
/* 2085 */     this.fPEReport[this.fPEDepth] = report;
/* 2086 */     this.fPEStack[(this.fPEDepth++)] = depth;
/*      */   }
/*      */ 
/*      */   private final int popPEStack()
/*      */   {
/* 2091 */     return this.fPEStack[(--this.fPEDepth)];
/*      */   }
/*      */ 
/*      */   private final boolean peekReportEntity()
/*      */   {
/* 2096 */     return this.fPEReport[(this.fPEDepth - 1)];
/*      */   }
/*      */ 
/*      */   private final void ensureEnumerationSize(int size)
/*      */   {
/* 2104 */     if (this.fEnumeration.length == size) {
/* 2105 */       String[] newEnum = new String[size * 2];
/* 2106 */       System.arraycopy(this.fEnumeration, 0, newEnum, 0, size);
/* 2107 */       this.fEnumeration = newEnum;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void init()
/*      */   {
/* 2114 */     this.fStartDTDCalled = false;
/* 2115 */     this.fExtEntityDepth = 0;
/* 2116 */     this.fIncludeSectDepth = 0;
/* 2117 */     this.fMarkUpDepth = 0;
/* 2118 */     this.fPEDepth = 0;
/*      */ 
/* 2120 */     this.fStandalone = false;
/* 2121 */     this.fSeenExternalDTD = false;
/* 2122 */     this.fSeenExternalPE = false;
/*      */ 
/* 2125 */     setScannerState(1);
/*      */   }
/*      */ 
/*      */   public DTDGrammar getGrammar()
/*      */   {
/* 2130 */     return this.nvGrammarInfo;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl
 * JD-Core Version:    0.6.2
 */