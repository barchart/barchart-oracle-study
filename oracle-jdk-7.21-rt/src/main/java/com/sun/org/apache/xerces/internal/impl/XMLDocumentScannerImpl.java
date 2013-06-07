/*      */ package com.sun.org.apache.xerces.internal.impl;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*      */ import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.xml.internal.stream.Entity;
/*      */ import com.sun.xml.internal.stream.Entity.ScannedEntity;
/*      */ import com.sun.xml.internal.stream.StaxXMLInputSource;
/*      */ import com.sun.xml.internal.stream.XMLEntityStorage;
/*      */ import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.util.NoSuchElementException;
/*      */ 
/*      */ public class XMLDocumentScannerImpl extends XMLDocumentFragmentScannerImpl
/*      */ {
/*      */   protected static final int SCANNER_STATE_XML_DECL = 42;
/*      */   protected static final int SCANNER_STATE_PROLOG = 43;
/*      */   protected static final int SCANNER_STATE_TRAILING_MISC = 44;
/*      */   protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 45;
/*      */   protected static final int SCANNER_STATE_DTD_EXTERNAL = 46;
/*      */   protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 47;
/*      */   protected static final int SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION = 48;
/*      */   protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
/*      */   protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
/*      */   protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
/*      */   protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
/*      */   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*      */   protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
/*  132 */   private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/disallow-doctype-decl" };
/*      */ 
/*  138 */   private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, Boolean.FALSE };
/*      */ 
/*  144 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validation-manager" };
/*      */ 
/*  150 */   private static final Object[] PROPERTY_DEFAULTS = { null, null };
/*      */ 
/*  162 */   protected XMLDTDScanner fDTDScanner = null;
/*      */   protected ValidationManager fValidationManager;
/*  168 */   protected XMLStringBuffer fDTDDecl = null;
/*  169 */   protected boolean fReadingDTD = false;
/*  170 */   protected boolean fAddedListener = false;
/*      */   protected String fDoctypeName;
/*      */   protected String fDoctypePublicId;
/*      */   protected String fDoctypeSystemId;
/*  186 */   protected NamespaceContext fNamespaceContext = new NamespaceSupport();
/*      */ 
/*  191 */   protected boolean fLoadExternalDTD = true;
/*      */   protected boolean fSeenDoctypeDecl;
/*      */   protected boolean fScanEndElement;
/*  205 */   protected XMLDocumentFragmentScannerImpl.Driver fXMLDeclDriver = new XMLDeclDriver();
/*      */ 
/*  208 */   protected XMLDocumentFragmentScannerImpl.Driver fPrologDriver = new PrologDriver();
/*      */ 
/*  211 */   protected XMLDocumentFragmentScannerImpl.Driver fDTDDriver = null;
/*      */ 
/*  214 */   protected XMLDocumentFragmentScannerImpl.Driver fTrailingMiscDriver = new TrailingMiscDriver();
/*  215 */   protected int fStartPos = 0;
/*  216 */   protected int fEndPos = 0;
/*  217 */   protected boolean fSeenInternalSubset = false;
/*      */ 
/*  221 */   private String[] fStrings = new String[3];
/*      */ 
/*  224 */   private XMLInputSource fExternalSubsetSource = null;
/*      */ 
/*  227 */   private final XMLDTDDescription fDTDDescription = new XMLDTDDescription(null, null, null, null, null);
/*      */ 
/*  230 */   private XMLString fString = new XMLString();
/*      */ 
/*  232 */   private static final char[] DOCTYPE = { 'D', 'O', 'C', 'T', 'Y', 'P', 'E' };
/*  233 */   private static final char[] COMMENTSTRING = { '-', '-' };
/*      */ 
/*      */   public void setInputSource(XMLInputSource inputSource)
/*      */     throws IOException
/*      */   {
/*  256 */     this.fEntityManager.setEntityHandler(this);
/*      */ 
/*  258 */     this.fEntityManager.startDocumentEntity(inputSource);
/*      */ 
/*  260 */     setScannerState(7);
/*      */   }
/*      */ 
/*      */   public int getScannetState()
/*      */   {
/*  267 */     return this.fScannerState;
/*      */   }
/*      */ 
/*      */   public void reset(PropertyManager propertyManager)
/*      */   {
/*  274 */     super.reset(propertyManager);
/*      */ 
/*  276 */     this.fDoctypeName = null;
/*  277 */     this.fDoctypePublicId = null;
/*  278 */     this.fDoctypeSystemId = null;
/*  279 */     this.fSeenDoctypeDecl = false;
/*  280 */     this.fNamespaceContext.reset();
/*  281 */     this.fSupportDTD = ((Boolean)propertyManager.getProperty("javax.xml.stream.supportDTD")).booleanValue();
/*      */ 
/*  284 */     this.fLoadExternalDTD = (!((Boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd")).booleanValue());
/*  285 */     setScannerState(7);
/*  286 */     setDriver(this.fXMLDeclDriver);
/*  287 */     this.fSeenInternalSubset = false;
/*  288 */     if (this.fDTDScanner != null) {
/*  289 */       ((XMLDTDScannerImpl)this.fDTDScanner).reset(propertyManager);
/*      */     }
/*  291 */     this.fEndPos = 0;
/*  292 */     this.fStartPos = 0;
/*  293 */     if (this.fDTDDecl != null)
/*  294 */       this.fDTDDecl.clear();
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/*  316 */     super.reset(componentManager);
/*      */ 
/*  319 */     this.fDoctypeName = null;
/*  320 */     this.fDoctypePublicId = null;
/*  321 */     this.fDoctypeSystemId = null;
/*  322 */     this.fSeenDoctypeDecl = false;
/*  323 */     this.fExternalSubsetSource = null;
/*      */ 
/*  326 */     this.fLoadExternalDTD = componentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
/*  327 */     this.fDisallowDoctype = componentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
/*      */ 
/*  329 */     this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
/*      */ 
/*  331 */     this.fSeenInternalSubset = false;
/*      */ 
/*  333 */     this.fDTDScanner = ((XMLDTDScanner)componentManager.getProperty("http://apache.org/xml/properties/internal/dtd-scanner"));
/*      */ 
/*  335 */     this.fValidationManager = ((ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null));
/*      */     try
/*      */     {
/*  338 */       this.fNamespaceContext = ((NamespaceContext)componentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context"));
/*      */     } catch (XMLConfigurationException e) {
/*      */     }
/*  341 */     if (this.fNamespaceContext == null) {
/*  342 */       this.fNamespaceContext = new NamespaceSupport();
/*      */     }
/*  344 */     this.fNamespaceContext.reset();
/*      */ 
/*  346 */     this.fEndPos = 0;
/*  347 */     this.fStartPos = 0;
/*  348 */     if (this.fDTDDecl != null) {
/*  349 */       this.fDTDDecl.clear();
/*      */     }
/*      */ 
/*  355 */     setScannerState(42);
/*  356 */     setDriver(this.fXMLDeclDriver);
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedFeatures()
/*      */   {
/*  367 */     String[] featureIds = super.getRecognizedFeatures();
/*  368 */     int length = featureIds != null ? featureIds.length : 0;
/*  369 */     String[] combinedFeatureIds = new String[length + RECOGNIZED_FEATURES.length];
/*  370 */     if (featureIds != null) {
/*  371 */       System.arraycopy(featureIds, 0, combinedFeatureIds, 0, featureIds.length);
/*      */     }
/*  373 */     System.arraycopy(RECOGNIZED_FEATURES, 0, combinedFeatureIds, length, RECOGNIZED_FEATURES.length);
/*  374 */     return combinedFeatureIds;
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/*  395 */     super.setFeature(featureId, state);
/*      */ 
/*  398 */     if (featureId.startsWith("http://apache.org/xml/features/")) {
/*  399 */       int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
/*      */ 
/*  401 */       if ((suffixLength == "nonvalidating/load-external-dtd".length()) && (featureId.endsWith("nonvalidating/load-external-dtd")))
/*      */       {
/*  403 */         this.fLoadExternalDTD = state;
/*  404 */         return;
/*      */       }
/*  406 */       if ((suffixLength == "disallow-doctype-decl".length()) && (featureId.endsWith("disallow-doctype-decl")))
/*      */       {
/*  408 */         this.fDisallowDoctype = state;
/*  409 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedProperties()
/*      */   {
/*  421 */     String[] propertyIds = super.getRecognizedProperties();
/*  422 */     int length = propertyIds != null ? propertyIds.length : 0;
/*  423 */     String[] combinedPropertyIds = new String[length + RECOGNIZED_PROPERTIES.length];
/*  424 */     if (propertyIds != null) {
/*  425 */       System.arraycopy(propertyIds, 0, combinedPropertyIds, 0, propertyIds.length);
/*      */     }
/*  427 */     System.arraycopy(RECOGNIZED_PROPERTIES, 0, combinedPropertyIds, length, RECOGNIZED_PROPERTIES.length);
/*  428 */     return combinedPropertyIds;
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */     throws XMLConfigurationException
/*      */   {
/*  449 */     super.setProperty(propertyId, value);
/*      */ 
/*  452 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/*  453 */       int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
/*      */ 
/*  455 */       if ((suffixLength == "internal/dtd-scanner".length()) && (propertyId.endsWith("internal/dtd-scanner")))
/*      */       {
/*  457 */         this.fDTDScanner = ((XMLDTDScanner)value);
/*      */       }
/*  459 */       if ((suffixLength == "internal/namespace-context".length()) && (propertyId.endsWith("internal/namespace-context")))
/*      */       {
/*  461 */         if (value != null) {
/*  462 */           this.fNamespaceContext = ((NamespaceContext)value);
/*      */         }
/*      */       }
/*      */ 
/*  466 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Boolean getFeatureDefault(String featureId)
/*      */   {
/*  482 */     for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
/*  483 */       if (RECOGNIZED_FEATURES[i].equals(featureId)) {
/*  484 */         return FEATURE_DEFAULTS[i];
/*      */       }
/*      */     }
/*  487 */     return super.getFeatureDefault(featureId);
/*      */   }
/*      */ 
/*      */   public Object getPropertyDefault(String propertyId)
/*      */   {
/*  500 */     for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
/*  501 */       if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
/*  502 */         return PROPERTY_DEFAULTS[i];
/*      */       }
/*      */     }
/*  505 */     return super.getPropertyDefault(propertyId);
/*      */   }
/*      */ 
/*      */   public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  531 */     super.startEntity(name, identifier, encoding, augs);
/*      */ 
/*  534 */     this.fEntityScanner.registerListener(this);
/*      */ 
/*  537 */     if ((!name.equals("[xml]")) && (this.fEntityScanner.isExternal()))
/*      */     {
/*  539 */       if ((augs == null) || (!((Boolean)augs.getItem("ENTITY_SKIPPED")).booleanValue())) {
/*  540 */         setScannerState(36);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  546 */     if ((this.fDocumentHandler != null) && (name.equals("[xml]")))
/*  547 */       this.fDocumentHandler.startDocument(this.fEntityScanner, encoding, this.fNamespaceContext, null);
/*      */   }
/*      */ 
/*      */   public void endEntity(String name, Augmentations augs)
/*      */     throws IOException, XNIException
/*      */   {
/*  564 */     super.endEntity(name, augs);
/*      */ 
/*  566 */     if (name.equals("[xml]"))
/*      */     {
/*  571 */       if ((this.fMarkupDepth == 0) && (this.fDriver == this.fTrailingMiscDriver))
/*      */       {
/*  573 */         setScannerState(34);
/*      */       }
/*      */       else
/*      */       {
/*  577 */         throw new EOFException();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public XMLStringBuffer getDTDDecl()
/*      */   {
/*  590 */     Entity entity = this.fEntityScanner.getCurrentEntity();
/*  591 */     this.fDTDDecl.append(((Entity.ScannedEntity)entity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
/*  592 */     if (this.fSeenInternalSubset)
/*  593 */       this.fDTDDecl.append("]>");
/*  594 */     return this.fDTDDecl;
/*      */   }
/*      */ 
/*      */   public String getCharacterEncodingScheme() {
/*  598 */     return this.fDeclaredEncoding;
/*      */   }
/*      */ 
/*      */   public int next()
/*      */     throws IOException, XNIException
/*      */   {
/*  607 */     return this.fDriver.next();
/*      */   }
/*      */ 
/*      */   public NamespaceContext getNamespaceContext()
/*      */   {
/*  612 */     return this.fNamespaceContext;
/*      */   }
/*      */ 
/*      */   protected XMLDocumentFragmentScannerImpl.Driver createContentDriver()
/*      */   {
/*  625 */     return new ContentDriver();
/*      */   }
/*      */ 
/*      */   protected boolean scanDoctypeDecl(boolean supportDTD)
/*      */     throws IOException, XNIException
/*      */   {
/*  634 */     if (!this.fEntityScanner.skipSpaces()) {
/*  635 */       reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL", null);
/*      */     }
/*      */ 
/*  640 */     this.fDoctypeName = this.fEntityScanner.scanName();
/*  641 */     if (this.fDoctypeName == null) {
/*  642 */       reportFatalError("MSG_ROOT_ELEMENT_TYPE_REQUIRED", null);
/*      */     }
/*      */ 
/*  646 */     if (this.fEntityScanner.skipSpaces()) {
/*  647 */       scanExternalID(this.fStrings, false);
/*  648 */       this.fDoctypeSystemId = this.fStrings[0];
/*  649 */       this.fDoctypePublicId = this.fStrings[1];
/*  650 */       this.fEntityScanner.skipSpaces();
/*      */     }
/*      */ 
/*  653 */     this.fHasExternalDTD = (this.fDoctypeSystemId != null);
/*      */ 
/*  656 */     if ((supportDTD) && (!this.fHasExternalDTD) && (this.fExternalSubsetResolver != null)) {
/*  657 */       this.fDTDDescription.setValues(null, null, this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
/*  658 */       this.fDTDDescription.setRootName(this.fDoctypeName);
/*  659 */       this.fExternalSubsetSource = this.fExternalSubsetResolver.getExternalSubset(this.fDTDDescription);
/*  660 */       this.fHasExternalDTD = (this.fExternalSubsetSource != null);
/*      */     }
/*      */ 
/*  664 */     if ((supportDTD) && (this.fDocumentHandler != null))
/*      */     {
/*  670 */       if (this.fExternalSubsetSource == null) {
/*  671 */         this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fDoctypePublicId, this.fDoctypeSystemId, null);
/*      */       }
/*      */       else {
/*  674 */         this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fExternalSubsetSource.getPublicId(), this.fExternalSubsetSource.getSystemId(), null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  679 */     boolean internalSubset = true;
/*  680 */     if (!this.fEntityScanner.skipChar(91)) {
/*  681 */       internalSubset = false;
/*  682 */       this.fEntityScanner.skipSpaces();
/*  683 */       if (!this.fEntityScanner.skipChar(62)) {
/*  684 */         reportFatalError("DoctypedeclUnterminated", new Object[] { this.fDoctypeName });
/*      */       }
/*  686 */       this.fMarkupDepth -= 1;
/*      */     }
/*  688 */     return internalSubset;
/*      */   }
/*      */ 
/*      */   protected void setEndDTDScanState()
/*      */   {
/*  697 */     setScannerState(43);
/*  698 */     setDriver(this.fPrologDriver);
/*  699 */     this.fEntityManager.setEntityHandler(this);
/*  700 */     this.fReadingDTD = false;
/*      */   }
/*      */ 
/*      */   protected String getScannerStateName(int state)
/*      */   {
/*  706 */     switch (state) { case 42:
/*  707 */       return "SCANNER_STATE_XML_DECL";
/*      */     case 43:
/*  708 */       return "SCANNER_STATE_PROLOG";
/*      */     case 44:
/*  709 */       return "SCANNER_STATE_TRAILING_MISC";
/*      */     case 45:
/*  710 */       return "SCANNER_STATE_DTD_INTERNAL_DECLS";
/*      */     case 46:
/*  711 */       return "SCANNER_STATE_DTD_EXTERNAL";
/*      */     case 47:
/*  712 */       return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
/*      */     }
/*  714 */     return super.getScannerStateName(state);
/*      */   }
/*      */ 
/*      */   public void refresh(int refreshPosition)
/*      */   {
/* 1475 */     super.refresh(refreshPosition);
/* 1476 */     if (this.fReadingDTD) {
/* 1477 */       Entity entity = this.fEntityScanner.getCurrentEntity();
/* 1478 */       if ((entity instanceof Entity.ScannedEntity)) {
/* 1479 */         this.fEndPos = ((Entity.ScannedEntity)entity).position;
/*      */       }
/* 1481 */       this.fDTDDecl.append(((Entity.ScannedEntity)entity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
/* 1482 */       this.fStartPos = refreshPosition;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class ContentDriver extends XMLDocumentFragmentScannerImpl.FragmentContentDriver
/*      */   {
/*      */     protected ContentDriver()
/*      */     {
/* 1208 */       super();
/*      */     }
/*      */ 
/*      */     protected boolean scanForDoctypeHook()
/*      */       throws IOException, XNIException
/*      */     {
/* 1231 */       if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE)) {
/* 1232 */         XMLDocumentScannerImpl.this.setScannerState(24);
/*      */ 
/* 1234 */         return true;
/*      */       }
/* 1236 */       return false;
/*      */     }
/*      */ 
/*      */     protected boolean elementDepthIsZeroHook()
/*      */       throws IOException, XNIException
/*      */     {
/* 1256 */       XMLDocumentScannerImpl.this.setScannerState(44);
/* 1257 */       XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
/* 1258 */       return true;
/*      */     }
/*      */ 
/*      */     protected boolean scanRootElementHook()
/*      */       throws IOException, XNIException
/*      */     {
/* 1277 */       if (XMLDocumentScannerImpl.this.scanStartElement()) {
/* 1278 */         XMLDocumentScannerImpl.this.setScannerState(44);
/* 1279 */         XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
/* 1280 */         return true;
/*      */       }
/* 1282 */       return false;
/*      */     }
/*      */ 
/*      */     protected void endOfFileHook(EOFException e)
/*      */       throws IOException, XNIException
/*      */     {
/* 1296 */       XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
/*      */     }
/*      */ 
/*      */     protected void resolveExternalSubsetAndRead()
/*      */       throws IOException, XNIException
/*      */     {
/* 1305 */       XMLDocumentScannerImpl.this.fDTDDescription.setValues(null, null, XMLDocumentScannerImpl.this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
/* 1306 */       XMLDocumentScannerImpl.this.fDTDDescription.setRootName(XMLDocumentScannerImpl.this.fElementQName.rawname);
/* 1307 */       XMLInputSource src = XMLDocumentScannerImpl.this.fExternalSubsetResolver.getExternalSubset(XMLDocumentScannerImpl.this.fDTDDescription);
/*      */ 
/* 1309 */       if (src != null) {
/* 1310 */         XMLDocumentScannerImpl.this.fDoctypeName = XMLDocumentScannerImpl.this.fElementQName.rawname;
/* 1311 */         XMLDocumentScannerImpl.this.fDoctypePublicId = src.getPublicId();
/* 1312 */         XMLDocumentScannerImpl.this.fDoctypeSystemId = src.getSystemId();
/*      */ 
/* 1314 */         if (XMLDocumentScannerImpl.this.fDocumentHandler != null)
/*      */         {
/* 1317 */           XMLDocumentScannerImpl.this.fDocumentHandler.doctypeDecl(XMLDocumentScannerImpl.this.fDoctypeName, XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null);
/*      */         }
/*      */         try { XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(src);
/* 1321 */           while (XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(true));
/*      */         } finally {
/* 1323 */           XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final class DTDDriver
/*      */     implements XMLDocumentFragmentScannerImpl.Driver
/*      */   {
/*      */     protected DTDDriver()
/*      */     {
/*      */     }
/*      */ 
/*      */     public int next()
/*      */       throws IOException, XNIException
/*      */     {
/* 1049 */       dispatch(true);
/*      */ 
/* 1057 */       if (XMLDocumentScannerImpl.this.fPropertyManager != null) {
/* 1058 */         XMLDocumentScannerImpl.this.dtdGrammarUtil = new DTDGrammarUtil(((XMLDTDScannerImpl)XMLDocumentScannerImpl.this.fDTDScanner).getGrammar(), XMLDocumentScannerImpl.this.fSymbolTable, XMLDocumentScannerImpl.this.fNamespaceContext);
/*      */       }
/*      */ 
/* 1061 */       return 11;
/*      */     }
/*      */ 
/*      */     public boolean dispatch(boolean complete)
/*      */       throws IOException, XNIException
/*      */     {
/* 1078 */       XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(null);
/*      */       try
/*      */       {
/* 1081 */         resourceIdentifier = new XMLResourceIdentifierImpl();
/* 1082 */         if (XMLDocumentScannerImpl.this.fDTDScanner == null)
/*      */         {
/* 1084 */           if ((XMLDocumentScannerImpl.this.fEntityManager.getEntityScanner() instanceof XML11EntityScanner)) {
/* 1085 */             XMLDocumentScannerImpl.this.fDTDScanner = new XML11DTDScannerImpl();
/*      */           }
/*      */           else {
/* 1088 */             XMLDocumentScannerImpl.this.fDTDScanner = new XMLDTDScannerImpl();
/*      */           }
/* 1090 */           ((XMLDTDScannerImpl)XMLDocumentScannerImpl.this.fDTDScanner).reset(XMLDocumentScannerImpl.this.fPropertyManager);
/*      */         }boolean again;
/*      */         do {
/* 1093 */           again = false;
/*      */           Entity entity;
/*      */           boolean completeDTD;
/* 1094 */           switch (XMLDocumentScannerImpl.this.fScannerState)
/*      */           {
/*      */           case 45:
/* 1098 */             boolean completeDTD = true;
/*      */ 
/* 1100 */             boolean moreToScan = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDInternalSubset(completeDTD, XMLDocumentScannerImpl.this.fStandalone, (XMLDocumentScannerImpl.this.fHasExternalDTD) && (XMLDocumentScannerImpl.this.fLoadExternalDTD));
/* 1101 */             entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
/* 1102 */             if ((entity instanceof Entity.ScannedEntity)) {
/* 1103 */               XMLDocumentScannerImpl.this.fEndPos = ((Entity.ScannedEntity)entity).position;
/*      */             }
/* 1105 */             XMLDocumentScannerImpl.this.fReadingDTD = false;
/* 1106 */             if (!moreToScan)
/*      */             {
/* 1108 */               if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(93)) {
/* 1109 */                 XMLDocumentScannerImpl.this.reportFatalError("EXPECTED_SQUARE_BRACKET_TO_CLOSE_INTERNAL_SUBSET", null);
/*      */               }
/*      */ 
/* 1112 */               XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
/* 1113 */               if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(62)) {
/* 1114 */                 XMLDocumentScannerImpl.this.reportFatalError("DoctypedeclUnterminated", new Object[] { XMLDocumentScannerImpl.this.fDoctypeName });
/*      */               }
/* 1116 */               XMLDocumentScannerImpl.this.fMarkupDepth -= 1;
/*      */ 
/* 1118 */               if (!XMLDocumentScannerImpl.this.fSupportDTD)
/*      */               {
/* 1121 */                 XMLDocumentScannerImpl.this.fEntityStore = XMLDocumentScannerImpl.this.fEntityManager.getEntityStore();
/* 1122 */                 XMLDocumentScannerImpl.this.fEntityStore.reset();
/*      */               }
/* 1125 */               else if ((XMLDocumentScannerImpl.this.fDoctypeSystemId != null) && ((XMLDocumentScannerImpl.this.fValidation) || (XMLDocumentScannerImpl.this.fLoadExternalDTD))) {
/* 1126 */                 XMLDocumentScannerImpl.this.setScannerState(46);
/* 1127 */                 continue;
/*      */               }
/*      */ 
/* 1131 */               XMLDocumentScannerImpl.this.setEndDTDScanState();
/* 1132 */               return true;
/*      */             }
/*      */ 
/*      */             break;
/*      */           case 46:
/* 1149 */             resourceIdentifier.setValues(XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null, null);
/* 1150 */             XMLInputSource xmlInputSource = null;
/* 1151 */             StaxXMLInputSource staxInputSource = XMLDocumentScannerImpl.this.fEntityManager.resolveEntityAsPerStax(resourceIdentifier);
/* 1152 */             xmlInputSource = staxInputSource.getXMLInputSource();
/* 1153 */             XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(xmlInputSource);
/* 1154 */             XMLDocumentScannerImpl.this.setScannerState(47);
/* 1155 */             again = true;
/* 1156 */             break;
/*      */           case 47:
/* 1161 */             completeDTD = true;
/* 1162 */             boolean moreToScan = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(completeDTD);
/* 1163 */             if (!moreToScan) {
/* 1164 */               XMLDocumentScannerImpl.this.setEndDTDScanState();
/* 1165 */               return 1;
/*      */             }
/*      */ 
/*      */             break;
/*      */           case 43:
/* 1171 */             XMLDocumentScannerImpl.this.setEndDTDScanState();
/* 1172 */             return true;
/*      */           case 44:
/*      */           default:
/* 1175 */             throw new XNIException("DTDDriver#dispatch: scanner state=" + XMLDocumentScannerImpl.this.fScannerState + " (" + XMLDocumentScannerImpl.this.getScannerStateName(XMLDocumentScannerImpl.this.fScannerState) + ')');
/*      */           }
/*      */         }
/* 1178 */         while ((complete) || (again));
/*      */       }
/*      */       catch (EOFException e)
/*      */       {
/*      */         XMLResourceIdentifierImpl resourceIdentifier;
/* 1183 */         e.printStackTrace();
/* 1184 */         XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
/* 1185 */         return 0;
/*      */       }
/*      */       finally
/*      */       {
/* 1191 */         XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
/*      */       }
/*      */ 
/* 1194 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final class PrologDriver
/*      */     implements XMLDocumentFragmentScannerImpl.Driver
/*      */   {
/*      */     protected PrologDriver()
/*      */     {
/*      */     }
/*      */ 
/*      */     public int next()
/*      */       throws IOException, XNIException
/*      */     {
/*      */       try
/*      */       {
/*      */         do {
/*  837 */           switch (XMLDocumentScannerImpl.this.fScannerState) {
/*      */           case 43:
/*  839 */             XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
/*  840 */             if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60))
/*  841 */               XMLDocumentScannerImpl.this.setScannerState(21);
/*  842 */             else if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(38))
/*  843 */               XMLDocumentScannerImpl.this.setScannerState(28);
/*      */             else {
/*  845 */               XMLDocumentScannerImpl.this.setScannerState(22);
/*      */             }
/*  847 */             break;
/*      */           case 21:
/*  851 */             XMLDocumentScannerImpl.this.fMarkupDepth += 1;
/*      */ 
/*  853 */             if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
/*  854 */               XMLDocumentScannerImpl.this.setScannerState(23);
/*  855 */             } else if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
/*  856 */               if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45)) {
/*  857 */                 if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45)) {
/*  858 */                   XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
/*      */                 }
/*      */ 
/*  861 */                 XMLDocumentScannerImpl.this.setScannerState(27);
/*  862 */               } else if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE)) {
/*  863 */                 XMLDocumentScannerImpl.this.setScannerState(24);
/*  864 */                 Entity entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
/*  865 */                 if ((entity instanceof Entity.ScannedEntity)) {
/*  866 */                   XMLDocumentScannerImpl.this.fStartPos = ((Entity.ScannedEntity)entity).position;
/*      */                 }
/*  868 */                 XMLDocumentScannerImpl.this.fReadingDTD = true;
/*  869 */                 if (XMLDocumentScannerImpl.this.fDTDDecl == null)
/*  870 */                   XMLDocumentScannerImpl.this.fDTDDecl = new XMLStringBuffer();
/*  871 */                 XMLDocumentScannerImpl.this.fDTDDecl.append("<!DOCTYPE");
/*      */               }
/*      */               else {
/*  874 */                 XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
/*      */               }
/*      */             } else {
/*  877 */               if (XMLChar.isNameStart(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
/*  878 */                 XMLDocumentScannerImpl.this.setScannerState(26);
/*  879 */                 XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
/*      */ 
/*  881 */                 return XMLDocumentScannerImpl.this.fContentDriver.next();
/*      */               }
/*      */ 
/*  884 */               XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
/*      */             }
/*      */             break;
/*      */           }
/*      */         }
/*      */ 
/*  890 */         while ((XMLDocumentScannerImpl.this.fScannerState == 43) || (XMLDocumentScannerImpl.this.fScannerState == 21));
/*      */ 
/*  892 */         switch (XMLDocumentScannerImpl.this.fScannerState)
/*      */         {
/*      */         case 27:
/*  905 */           XMLDocumentScannerImpl.this.scanComment();
/*  906 */           XMLDocumentScannerImpl.this.setScannerState(43);
/*  907 */           return 5;
/*      */         case 23:
/*  912 */           XMLDocumentScannerImpl.this.fContentBuffer.clear();
/*  913 */           XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
/*  914 */           XMLDocumentScannerImpl.this.setScannerState(43);
/*  915 */           return 3;
/*      */         case 24:
/*  919 */           if (XMLDocumentScannerImpl.this.fDisallowDoctype) {
/*  920 */             XMLDocumentScannerImpl.this.reportFatalError("DoctypeNotAllowed", null);
/*      */           }
/*      */ 
/*  924 */           if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
/*  925 */             XMLDocumentScannerImpl.this.reportFatalError("AlreadySeenDoctype", null);
/*      */           }
/*  927 */           XMLDocumentScannerImpl.this.fSeenDoctypeDecl = true;
/*      */ 
/*  931 */           if (XMLDocumentScannerImpl.this.scanDoctypeDecl(XMLDocumentScannerImpl.this.fSupportDTD))
/*      */           {
/*  933 */             XMLDocumentScannerImpl.this.setScannerState(45);
/*  934 */             XMLDocumentScannerImpl.this.fSeenInternalSubset = true;
/*  935 */             if (XMLDocumentScannerImpl.this.fDTDDriver == null) {
/*  936 */               XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this);
/*      */             }
/*  938 */             XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
/*      */ 
/*  940 */             return XMLDocumentScannerImpl.this.fDTDDriver.next();
/*      */           }
/*      */ 
/*  943 */           if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
/*  944 */             Entity entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
/*  945 */             if ((entity instanceof Entity.ScannedEntity)) {
/*  946 */               XMLDocumentScannerImpl.this.fEndPos = ((Entity.ScannedEntity)entity).position;
/*      */             }
/*  948 */             XMLDocumentScannerImpl.this.fReadingDTD = false;
/*      */           }
/*      */ 
/*  952 */           if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
/*  953 */             if (((XMLDocumentScannerImpl.this.fValidation) || (XMLDocumentScannerImpl.this.fLoadExternalDTD)) && ((XMLDocumentScannerImpl.this.fValidationManager == null) || (!XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())))
/*      */             {
/*  955 */               if (XMLDocumentScannerImpl.this.fSupportDTD)
/*  956 */                 XMLDocumentScannerImpl.this.setScannerState(46);
/*      */               else
/*  958 */                 XMLDocumentScannerImpl.this.setScannerState(43);
/*  959 */               XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
/*  960 */               if (XMLDocumentScannerImpl.this.fDTDDriver == null)
/*  961 */                 XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this);
/*  962 */               return XMLDocumentScannerImpl.this.fDTDDriver.next();
/*      */             }
/*      */ 
/*      */           }
/*  966 */           else if ((XMLDocumentScannerImpl.this.fExternalSubsetSource != null) && 
/*  967 */             ((XMLDocumentScannerImpl.this.fValidation) || (XMLDocumentScannerImpl.this.fLoadExternalDTD)) && (
/*  967 */             (XMLDocumentScannerImpl.this.fValidationManager == null) || (!XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())))
/*      */           {
/*  970 */             XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
/*  971 */             XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
/*  972 */             if (XMLDocumentScannerImpl.this.fSupportDTD)
/*  973 */               XMLDocumentScannerImpl.this.setScannerState(47);
/*      */             else
/*  975 */               XMLDocumentScannerImpl.this.setScannerState(43);
/*  976 */             XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
/*  977 */             if (XMLDocumentScannerImpl.this.fDTDDriver == null)
/*  978 */               XMLDocumentScannerImpl.this.fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this);
/*  979 */             return XMLDocumentScannerImpl.this.fDTDDriver.next();
/*      */           }
/*      */ 
/*  990 */           if (XMLDocumentScannerImpl.this.fDTDScanner != null) {
/*  991 */             XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null);
/*      */           }
/*  993 */           XMLDocumentScannerImpl.this.setScannerState(43);
/*  994 */           return 11;
/*      */         case 22:
/*  998 */           XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInProlog", null);
/*  999 */           XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
/*      */         case 28:
/* 1002 */           XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInProlog", null);
/*      */         case 25:
/*      */         case 26:
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (EOFException e)
/*      */       {
/* 1018 */         XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
/*      */ 
/* 1020 */         return -1;
/*      */       }
/*      */ 
/* 1024 */       return -1;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final class TrailingMiscDriver
/*      */     implements XMLDocumentFragmentScannerImpl.Driver
/*      */   {
/*      */     protected TrailingMiscDriver()
/*      */     {
/*      */     }
/*      */ 
/*      */     public int next()
/*      */       throws IOException, XNIException
/*      */     {
/* 1347 */       if (XMLDocumentScannerImpl.this.fEmptyElement) {
/* 1348 */         XMLDocumentScannerImpl.this.fEmptyElement = false;
/* 1349 */         return 2;
/*      */       }
/*      */       try
/*      */       {
/* 1353 */         if (XMLDocumentScannerImpl.this.fScannerState == 34)
/* 1354 */           return 8;
/*      */         do {
/* 1356 */           switch (XMLDocumentScannerImpl.this.fScannerState)
/*      */           {
/*      */           case 44:
/* 1359 */             XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
/*      */ 
/* 1362 */             if (XMLDocumentScannerImpl.this.fScannerState == 34) {
/* 1363 */               return 8;
/*      */             }
/* 1365 */             if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60))
/* 1366 */               XMLDocumentScannerImpl.this.setScannerState(21);
/*      */             else {
/* 1368 */               XMLDocumentScannerImpl.this.setScannerState(22);
/*      */             }
/* 1370 */             break;
/*      */           case 21:
/* 1373 */             XMLDocumentScannerImpl.this.fMarkupDepth += 1;
/* 1374 */             if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
/* 1375 */               XMLDocumentScannerImpl.this.setScannerState(23);
/* 1376 */             } else if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
/* 1377 */               XMLDocumentScannerImpl.this.setScannerState(27);
/* 1378 */             } else if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(47)) {
/* 1379 */               XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
/*      */             }
/* 1381 */             else if (XMLChar.isNameStart(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
/* 1382 */               XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
/*      */ 
/* 1384 */               XMLDocumentScannerImpl.this.scanStartElement();
/* 1385 */               XMLDocumentScannerImpl.this.setScannerState(22);
/*      */             } else {
/* 1387 */               XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
/*      */             }
/*      */             break;
/*      */           }
/*      */         }
/*      */ 
/* 1393 */         while ((XMLDocumentScannerImpl.this.fScannerState == 21) || (XMLDocumentScannerImpl.this.fScannerState == 44));
/*      */ 
/* 1397 */         switch (XMLDocumentScannerImpl.this.fScannerState) {
/*      */         case 23:
/* 1399 */           XMLDocumentScannerImpl.this.fContentBuffer.clear();
/* 1400 */           XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
/* 1401 */           XMLDocumentScannerImpl.this.setScannerState(44);
/* 1402 */           return 3;
/*      */         case 27:
/* 1405 */           if (!XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.COMMENTSTRING)) {
/* 1406 */             XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
/*      */           }
/* 1408 */           XMLDocumentScannerImpl.this.scanComment();
/* 1409 */           XMLDocumentScannerImpl.this.setScannerState(44);
/* 1410 */           return 5;
/*      */         case 22:
/* 1413 */           int ch = XMLDocumentScannerImpl.this.fEntityScanner.peekChar();
/* 1414 */           if (ch == -1) {
/* 1415 */             XMLDocumentScannerImpl.this.setScannerState(34);
/* 1416 */             return 8;
/*      */           }
/* 1418 */           XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInTrailingMisc", null);
/*      */ 
/* 1420 */           XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
/* 1421 */           XMLDocumentScannerImpl.this.setScannerState(44);
/* 1422 */           return 4;
/*      */         case 28:
/* 1427 */           XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInTrailingMisc", null);
/*      */ 
/* 1429 */           XMLDocumentScannerImpl.this.setScannerState(44);
/* 1430 */           return 9;
/*      */         case 34:
/* 1435 */           XMLDocumentScannerImpl.this.setScannerState(48);
/*      */ 
/* 1437 */           return 8;
/*      */         case 48:
/* 1440 */           throw new NoSuchElementException("No more events to be parsed");
/*      */         }
/* 1442 */         throw new XNIException("Scanner State " + XMLDocumentScannerImpl.this.fScannerState + " not Recognized ");
/*      */       }
/*      */       catch (EOFException e)
/*      */       {
/* 1449 */         if (XMLDocumentScannerImpl.this.fMarkupDepth != 0) {
/* 1450 */           XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
/* 1451 */           return -1;
/*      */         }
/*      */ 
/* 1455 */         XMLDocumentScannerImpl.this.setScannerState(34);
/*      */       }
/*      */ 
/* 1458 */       return 8;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final class XMLDeclDriver
/*      */     implements XMLDocumentFragmentScannerImpl.Driver
/*      */   {
/*      */     protected XMLDeclDriver()
/*      */     {
/*      */     }
/*      */ 
/*      */     public int next()
/*      */       throws IOException, XNIException
/*      */     {
/*  748 */       XMLDocumentScannerImpl.this.setScannerState(43);
/*  749 */       XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fPrologDriver);
/*      */       try
/*      */       {
/*  754 */         if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.xmlDecl)) {
/*  755 */           XMLDocumentScannerImpl.this.fMarkupDepth += 1;
/*      */ 
/*  758 */           if (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
/*  759 */             XMLDocumentScannerImpl.this.fStringBuffer.clear();
/*  760 */             XMLDocumentScannerImpl.this.fStringBuffer.append("xml");
/*  761 */             while (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
/*  762 */               XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar());
/*      */             }
/*  764 */             String target = XMLDocumentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentScannerImpl.this.fStringBuffer.ch, XMLDocumentScannerImpl.this.fStringBuffer.offset, XMLDocumentScannerImpl.this.fStringBuffer.length);
/*      */ 
/*  766 */             XMLDocumentScannerImpl.this.fContentBuffer.clear();
/*  767 */             XMLDocumentScannerImpl.this.scanPIData(target, XMLDocumentScannerImpl.this.fContentBuffer);
/*      */ 
/*  769 */             XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
/*      */ 
/*  771 */             return 3;
/*      */           }
/*      */ 
/*  775 */           XMLDocumentScannerImpl.this.scanXMLDeclOrTextDecl(false);
/*      */ 
/*  777 */           XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
/*  778 */           return 7;
/*      */         }
/*      */ 
/*  782 */         XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
/*      */ 
/*  785 */         return 7;
/*      */       }
/*      */       catch (EOFException e)
/*      */       {
/*  796 */         XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
/*  797 */       }return -1;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl
 * JD-Core Version:    0.6.2
 */