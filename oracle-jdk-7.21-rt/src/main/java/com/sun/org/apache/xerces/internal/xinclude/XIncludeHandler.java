/*      */ package com.sun.org.apache.xerces.internal.xinclude;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.Constants;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*      */ import com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException;
/*      */ import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
/*      */ import com.sun.org.apache.xerces.internal.util.IntStack;
/*      */ import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
/*      */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.URI;
/*      */ import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*      */ import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLLocator;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*      */ import com.sun.org.apache.xerces.internal.xpointer.XPointerHandler;
/*      */ import com.sun.org.apache.xerces.internal.xpointer.XPointerProcessor;
/*      */ import java.io.CharConversionException;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Locale;
/*      */ import java.util.Stack;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ public class XIncludeHandler
/*      */   implements XMLComponent, XMLDocumentFilter, XMLDTDFilter
/*      */ {
/*      */   public static final String XINCLUDE_DEFAULT_CONFIGURATION = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
/*      */   public static final String HTTP_ACCEPT = "Accept";
/*      */   public static final String HTTP_ACCEPT_LANGUAGE = "Accept-Language";
/*      */   public static final String XPOINTER = "xpointer";
/*  128 */   public static final String XINCLUDE_NS_URI = "http://www.w3.org/2001/XInclude".intern();
/*      */ 
/*  130 */   public static final String XINCLUDE_INCLUDE = "include".intern();
/*  131 */   public static final String XINCLUDE_FALLBACK = "fallback".intern();
/*      */ 
/*  133 */   public static final String XINCLUDE_PARSE_XML = "xml".intern();
/*  134 */   public static final String XINCLUDE_PARSE_TEXT = "text".intern();
/*      */ 
/*  136 */   public static final String XINCLUDE_ATTR_HREF = "href".intern();
/*  137 */   public static final String XINCLUDE_ATTR_PARSE = "parse".intern();
/*  138 */   public static final String XINCLUDE_ATTR_ENCODING = "encoding".intern();
/*  139 */   public static final String XINCLUDE_ATTR_ACCEPT = "accept".intern();
/*  140 */   public static final String XINCLUDE_ATTR_ACCEPT_LANGUAGE = "accept-language".intern();
/*      */ 
/*  143 */   public static final String XINCLUDE_INCLUDED = "[included]".intern();
/*      */   public static final String CURRENT_BASE_URI = "currentBaseURI";
/*  149 */   public static final String XINCLUDE_BASE = "base".intern();
/*  150 */   public static final QName XML_BASE_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_BASE, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_BASE).intern(), NamespaceContext.XML_URI);
/*      */ 
/*  158 */   public static final String XINCLUDE_LANG = "lang".intern();
/*  159 */   public static final QName XML_LANG_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_LANG, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_LANG).intern(), NamespaceContext.XML_URI);
/*      */ 
/*  166 */   public static final QName NEW_NS_ATTR_QNAME = new QName(XMLSymbols.PREFIX_XMLNS, "", XMLSymbols.PREFIX_XMLNS + ":", NamespaceContext.XMLNS_URI);
/*      */   private static final int STATE_NORMAL_PROCESSING = 1;
/*      */   private static final int STATE_IGNORE = 2;
/*      */   private static final int STATE_EXPECT_FALLBACK = 3;
/*      */   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
/*      */   protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
/*      */   protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
/*      */   protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
/*      */   protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
/*      */   protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
/*      */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*      */   protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*      */   public static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
/*      */   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
/*  233 */   private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" };
/*      */ 
/*  237 */   private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, Boolean.TRUE, Boolean.TRUE };
/*      */ 
/*  240 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/input-buffer-size" };
/*      */ 
/*  244 */   private static final Object[] PROPERTY_DEFAULTS = { null, null, null, new Integer(8192) };
/*      */   protected XMLDocumentHandler fDocumentHandler;
/*      */   protected XMLDocumentSource fDocumentSource;
/*      */   protected XMLDTDHandler fDTDHandler;
/*      */   protected XMLDTDSource fDTDSource;
/*      */   protected XIncludeHandler fParentXIncludeHandler;
/*  260 */   protected int fBufferSize = 8192;
/*      */   protected String fParentRelativeURI;
/*      */   protected XMLParserConfiguration fChildConfig;
/*      */   protected XMLParserConfiguration fXIncludeChildConfig;
/*      */   protected XMLParserConfiguration fXPointerChildConfig;
/*  277 */   protected XPointerProcessor fXPtrProcessor = null;
/*      */   protected XMLLocator fDocLocation;
/*  280 */   protected XIncludeMessageFormatter fXIncludeMessageFormatter = new XIncludeMessageFormatter();
/*      */   protected XIncludeNamespaceSupport fNamespaceContext;
/*      */   protected SymbolTable fSymbolTable;
/*      */   protected XMLErrorReporter fErrorReporter;
/*      */   protected XMLEntityResolver fEntityResolver;
/*      */   protected SecurityManager fSecurityManager;
/*      */   protected XIncludeTextReader fXInclude10TextReader;
/*      */   protected XIncludeTextReader fXInclude11TextReader;
/*      */   protected XMLResourceIdentifier fCurrentBaseURI;
/*      */   protected IntStack fBaseURIScope;
/*      */   protected Stack fBaseURI;
/*      */   protected Stack fLiteralSystemID;
/*      */   protected Stack fExpandedSystemID;
/*      */   protected IntStack fLanguageScope;
/*      */   protected Stack fLanguageStack;
/*      */   protected String fCurrentLanguage;
/*      */   protected ParserConfigurationSettings fSettings;
/*      */   private int fDepth;
/*      */   private int fResultDepth;
/*      */   private static final int INITIAL_SIZE = 8;
/*  320 */   private boolean[] fSawInclude = new boolean[8];
/*      */ 
/*  325 */   private boolean[] fSawFallback = new boolean[8];
/*      */ 
/*  328 */   private int[] fState = new int[8];
/*      */   private ArrayList fNotations;
/*      */   private ArrayList fUnparsedEntities;
/*  335 */   private boolean fFixupBaseURIs = true;
/*  336 */   private boolean fFixupLanguage = true;
/*      */   private boolean fSendUEAndNotationEvents;
/*      */   private boolean fIsXML11;
/*      */   private boolean fInDTD;
/*      */   private boolean fSeenRootElement;
/*  352 */   private boolean fNeedCopyFeatures = true;
/*      */ 
/* 2899 */   private static boolean[] gNeedEscaping = new boolean[''];
/*      */ 
/* 2901 */   private static char[] gAfterEscaping1 = new char[''];
/*      */ 
/* 2903 */   private static char[] gAfterEscaping2 = new char[''];
/* 2904 */   private static char[] gHexChs = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*      */ 
/*      */   public XIncludeHandler()
/*      */   {
/*  357 */     this.fDepth = 0;
/*      */ 
/*  359 */     this.fSawFallback[this.fDepth] = false;
/*  360 */     this.fSawInclude[this.fDepth] = false;
/*  361 */     this.fState[this.fDepth] = 1;
/*  362 */     this.fNotations = new ArrayList();
/*  363 */     this.fUnparsedEntities = new ArrayList();
/*      */ 
/*  365 */     this.fBaseURIScope = new IntStack();
/*  366 */     this.fBaseURI = new Stack();
/*  367 */     this.fLiteralSystemID = new Stack();
/*  368 */     this.fExpandedSystemID = new Stack();
/*  369 */     this.fCurrentBaseURI = new XMLResourceIdentifierImpl();
/*      */ 
/*  371 */     this.fLanguageScope = new IntStack();
/*  372 */     this.fLanguageStack = new Stack();
/*  373 */     this.fCurrentLanguage = null;
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XNIException
/*      */   {
/*  380 */     this.fNamespaceContext = null;
/*  381 */     this.fDepth = 0;
/*  382 */     this.fResultDepth = (isRootDocument() ? 0 : this.fParentXIncludeHandler.getResultDepth());
/*  383 */     this.fNotations.clear();
/*  384 */     this.fUnparsedEntities.clear();
/*  385 */     this.fParentRelativeURI = null;
/*  386 */     this.fIsXML11 = false;
/*  387 */     this.fInDTD = false;
/*  388 */     this.fSeenRootElement = false;
/*      */ 
/*  390 */     this.fBaseURIScope.clear();
/*  391 */     this.fBaseURI.clear();
/*  392 */     this.fLiteralSystemID.clear();
/*  393 */     this.fExpandedSystemID.clear();
/*  394 */     this.fLanguageScope.clear();
/*  395 */     this.fLanguageStack.clear();
/*      */ 
/*  403 */     for (int i = 0; i < this.fState.length; i++) {
/*  404 */       this.fState[i] = 1;
/*      */     }
/*  406 */     for (int i = 0; i < this.fSawFallback.length; i++) {
/*  407 */       this.fSawFallback[i] = false;
/*      */     }
/*  409 */     for (int i = 0; i < this.fSawInclude.length; i++) {
/*  410 */       this.fSawInclude[i] = false;
/*      */     }
/*      */     try
/*      */     {
/*  414 */       if (!componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings"))
/*      */       {
/*  416 */         return;
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*  422 */     this.fNeedCopyFeatures = true;
/*      */     try
/*      */     {
/*  425 */       this.fSendUEAndNotationEvents = componentManager.getFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD");
/*      */ 
/*  427 */       if (this.fChildConfig != null) {
/*  428 */         this.fChildConfig.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", this.fSendUEAndNotationEvents);
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  437 */       this.fFixupBaseURIs = componentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-base-uris");
/*      */ 
/*  439 */       if (this.fChildConfig != null) {
/*  440 */         this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*  446 */       this.fFixupBaseURIs = true;
/*      */     }
/*      */     try
/*      */     {
/*  450 */       this.fFixupLanguage = componentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-language");
/*      */ 
/*  452 */       if (this.fChildConfig != null) {
/*  453 */         this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*  459 */       this.fFixupLanguage = true;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  464 */       SymbolTable value = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
/*      */ 
/*  466 */       if (value != null) {
/*  467 */         this.fSymbolTable = value;
/*  468 */         if (this.fChildConfig != null)
/*  469 */           this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", value);
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*  474 */       this.fSymbolTable = null;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  479 */       XMLErrorReporter value = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
/*      */ 
/*  481 */       if (value != null) {
/*  482 */         setErrorReporter(value);
/*  483 */         if (this.fChildConfig != null)
/*  484 */           this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", value);
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*  489 */       this.fErrorReporter = null;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  494 */       XMLEntityResolver value = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
/*      */ 
/*  498 */       if (value != null) {
/*  499 */         this.fEntityResolver = value;
/*  500 */         if (this.fChildConfig != null)
/*  501 */           this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", value);
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*  506 */       this.fEntityResolver = null;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  511 */       SecurityManager value = (SecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
/*      */ 
/*  515 */       if (value != null) {
/*  516 */         this.fSecurityManager = value;
/*  517 */         if (this.fChildConfig != null)
/*  518 */           this.fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", value);
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*  523 */       this.fSecurityManager = null;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  528 */       Integer value = (Integer)componentManager.getProperty("http://apache.org/xml/properties/input-buffer-size");
/*      */ 
/*  532 */       if ((value != null) && (value.intValue() > 0)) {
/*  533 */         this.fBufferSize = value.intValue();
/*  534 */         if (this.fChildConfig != null)
/*  535 */           this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", value);
/*      */       }
/*      */       else
/*      */       {
/*  539 */         this.fBufferSize = ((Integer)getPropertyDefault("http://apache.org/xml/properties/input-buffer-size")).intValue();
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/*  543 */       this.fBufferSize = ((Integer)getPropertyDefault("http://apache.org/xml/properties/input-buffer-size")).intValue();
/*      */     }
/*      */ 
/*  547 */     if (this.fXInclude10TextReader != null) {
/*  548 */       this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
/*      */     }
/*      */ 
/*  551 */     if (this.fXInclude11TextReader != null) {
/*  552 */       this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
/*      */     }
/*      */ 
/*  555 */     this.fSettings = new ParserConfigurationSettings();
/*  556 */     copyFeatures(componentManager, this.fSettings);
/*      */     try
/*      */     {
/*  565 */       if (componentManager.getFeature("http://apache.org/xml/features/validation/schema")) {
/*  566 */         this.fSettings.setFeature("http://apache.org/xml/features/validation/schema", false);
/*  567 */         if (componentManager.getFeature("http://xml.org/sax/features/validation"))
/*  568 */           this.fSettings.setFeature("http://apache.org/xml/features/validation/dynamic", true);
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedFeatures()
/*      */   {
/*  584 */     return (String[])RECOGNIZED_FEATURES.clone();
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/*  604 */     if (featureId.equals("http://xml.org/sax/features/allow-dtd-events-after-endDTD")) {
/*  605 */       this.fSendUEAndNotationEvents = state;
/*      */     }
/*  607 */     if (this.fSettings != null) {
/*  608 */       this.fNeedCopyFeatures = true;
/*  609 */       this.fSettings.setFeature(featureId, state);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedProperties()
/*      */   {
/*  619 */     return (String[])RECOGNIZED_PROPERTIES.clone();
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */     throws XMLConfigurationException
/*      */   {
/*  639 */     if (propertyId.equals("http://apache.org/xml/properties/internal/symbol-table")) {
/*  640 */       this.fSymbolTable = ((SymbolTable)value);
/*  641 */       if (this.fChildConfig != null) {
/*  642 */         this.fChildConfig.setProperty(propertyId, value);
/*      */       }
/*  644 */       return;
/*      */     }
/*  646 */     if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
/*  647 */       setErrorReporter((XMLErrorReporter)value);
/*  648 */       if (this.fChildConfig != null) {
/*  649 */         this.fChildConfig.setProperty(propertyId, value);
/*      */       }
/*  651 */       return;
/*      */     }
/*  653 */     if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
/*  654 */       this.fEntityResolver = ((XMLEntityResolver)value);
/*  655 */       if (this.fChildConfig != null) {
/*  656 */         this.fChildConfig.setProperty(propertyId, value);
/*      */       }
/*  658 */       return;
/*      */     }
/*  660 */     if (propertyId.equals("http://apache.org/xml/properties/security-manager")) {
/*  661 */       this.fSecurityManager = ((SecurityManager)value);
/*  662 */       if (this.fChildConfig != null) {
/*  663 */         this.fChildConfig.setProperty(propertyId, value);
/*      */       }
/*  665 */       return;
/*      */     }
/*  667 */     if (propertyId.equals("http://apache.org/xml/properties/input-buffer-size")) {
/*  668 */       Integer bufferSize = (Integer)value;
/*  669 */       if (this.fChildConfig != null) {
/*  670 */         this.fChildConfig.setProperty(propertyId, value);
/*      */       }
/*  672 */       if ((bufferSize != null) && (bufferSize.intValue() > 0)) {
/*  673 */         this.fBufferSize = bufferSize.intValue();
/*      */ 
/*  675 */         if (this.fXInclude10TextReader != null) {
/*  676 */           this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
/*      */         }
/*      */ 
/*  679 */         if (this.fXInclude11TextReader != null) {
/*  680 */           this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
/*      */         }
/*      */       }
/*  683 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Boolean getFeatureDefault(String featureId)
/*      */   {
/*  698 */     for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
/*  699 */       if (RECOGNIZED_FEATURES[i].equals(featureId)) {
/*  700 */         return FEATURE_DEFAULTS[i];
/*      */       }
/*      */     }
/*  703 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getPropertyDefault(String propertyId)
/*      */   {
/*  716 */     for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
/*  717 */       if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
/*  718 */         return PROPERTY_DEFAULTS[i];
/*      */       }
/*      */     }
/*  721 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDocumentHandler(XMLDocumentHandler handler) {
/*  725 */     this.fDocumentHandler = handler;
/*      */   }
/*      */ 
/*      */   public XMLDocumentHandler getDocumentHandler() {
/*  729 */     return this.fDocumentHandler;
/*      */   }
/*      */ 
/*      */   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  751 */     this.fErrorReporter.setDocumentLocator(locator);
/*      */ 
/*  753 */     if ((!isRootDocument()) && (this.fParentXIncludeHandler.searchForRecursiveIncludes(locator)))
/*      */     {
/*  755 */       reportFatalError("RecursiveInclude", new Object[] { locator.getExpandedSystemId() });
/*      */     }
/*      */ 
/*  760 */     if (!(namespaceContext instanceof XIncludeNamespaceSupport)) {
/*  761 */       reportFatalError("IncompatibleNamespaceContext");
/*      */     }
/*  763 */     this.fNamespaceContext = ((XIncludeNamespaceSupport)namespaceContext);
/*  764 */     this.fDocLocation = locator;
/*      */ 
/*  767 */     this.fCurrentBaseURI.setBaseSystemId(locator.getBaseSystemId());
/*  768 */     this.fCurrentBaseURI.setExpandedSystemId(locator.getExpandedSystemId());
/*  769 */     this.fCurrentBaseURI.setLiteralSystemId(locator.getLiteralSystemId());
/*  770 */     saveBaseURI();
/*  771 */     if (augs == null) {
/*  772 */       augs = new AugmentationsImpl();
/*      */     }
/*  774 */     augs.putItem("currentBaseURI", this.fCurrentBaseURI);
/*      */ 
/*  777 */     this.fCurrentLanguage = XMLSymbols.EMPTY_STRING;
/*  778 */     saveLanguage(this.fCurrentLanguage);
/*      */ 
/*  780 */     if ((isRootDocument()) && (this.fDocumentHandler != null))
/*  781 */       this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
/*      */   }
/*      */ 
/*      */   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  795 */     this.fIsXML11 = "1.1".equals(version);
/*  796 */     if ((isRootDocument()) && (this.fDocumentHandler != null))
/*  797 */       this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
/*      */   }
/*      */ 
/*      */   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  807 */     if ((isRootDocument()) && (this.fDocumentHandler != null))
/*  808 */       this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
/*      */   }
/*      */ 
/*      */   public void comment(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  814 */     if (!this.fInDTD) {
/*  815 */       if ((this.fDocumentHandler != null) && (getState() == 1))
/*      */       {
/*  817 */         this.fDepth += 1;
/*  818 */         augs = modifyAugmentations(augs);
/*  819 */         this.fDocumentHandler.comment(text, augs);
/*  820 */         this.fDepth -= 1;
/*      */       }
/*      */     }
/*  823 */     else if (this.fDTDHandler != null)
/*  824 */       this.fDTDHandler.comment(text, augs);
/*      */   }
/*      */ 
/*      */   public void processingInstruction(String target, XMLString data, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  833 */     if (!this.fInDTD) {
/*  834 */       if ((this.fDocumentHandler != null) && (getState() == 1))
/*      */       {
/*  837 */         this.fDepth += 1;
/*  838 */         augs = modifyAugmentations(augs);
/*  839 */         this.fDocumentHandler.processingInstruction(target, data, augs);
/*  840 */         this.fDepth -= 1;
/*      */       }
/*      */     }
/*  843 */     else if (this.fDTDHandler != null)
/*  844 */       this.fDTDHandler.processingInstruction(target, data, augs);
/*      */   }
/*      */ 
/*      */   public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  853 */     this.fDepth += 1;
/*  854 */     int lastState = getState(this.fDepth - 1);
/*      */ 
/*  858 */     if ((lastState == 3) && (getState(this.fDepth - 2) == 3)) {
/*  859 */       setState(2);
/*      */     }
/*      */     else {
/*  862 */       setState(lastState);
/*      */     }
/*      */ 
/*  867 */     processXMLBaseAttributes(attributes);
/*  868 */     if (this.fFixupLanguage) {
/*  869 */       processXMLLangAttributes(attributes);
/*      */     }
/*      */ 
/*  872 */     if (isIncludeElement(element)) {
/*  873 */       boolean success = handleIncludeElement(attributes);
/*  874 */       if (success) {
/*  875 */         setState(2);
/*      */       }
/*      */       else {
/*  878 */         setState(3);
/*      */       }
/*      */     }
/*  881 */     else if (isFallbackElement(element)) {
/*  882 */       handleFallbackElement();
/*      */     }
/*  884 */     else if (hasXIncludeNamespace(element)) {
/*  885 */       if (getSawInclude(this.fDepth - 1)) {
/*  886 */         reportFatalError("IncludeChild", new Object[] { element.rawname });
/*      */       }
/*      */ 
/*  890 */       if (getSawFallback(this.fDepth - 1)) {
/*  891 */         reportFatalError("FallbackChild", new Object[] { element.rawname });
/*      */       }
/*      */ 
/*  895 */       if (getState() == 1) {
/*  896 */         if (this.fResultDepth++ == 0) {
/*  897 */           checkMultipleRootElements();
/*      */         }
/*  899 */         if (this.fDocumentHandler != null) {
/*  900 */           augs = modifyAugmentations(augs);
/*  901 */           attributes = processAttributes(attributes);
/*  902 */           this.fDocumentHandler.startElement(element, attributes, augs);
/*      */         }
/*      */       }
/*      */     }
/*  906 */     else if (getState() == 1) {
/*  907 */       if (this.fResultDepth++ == 0) {
/*  908 */         checkMultipleRootElements();
/*      */       }
/*  910 */       if (this.fDocumentHandler != null) {
/*  911 */         augs = modifyAugmentations(augs);
/*  912 */         attributes = processAttributes(attributes);
/*  913 */         this.fDocumentHandler.startElement(element, attributes, augs);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  923 */     this.fDepth += 1;
/*  924 */     int lastState = getState(this.fDepth - 1);
/*      */ 
/*  928 */     if ((lastState == 3) && (getState(this.fDepth - 2) == 3)) {
/*  929 */       setState(2);
/*      */     }
/*      */     else {
/*  932 */       setState(lastState);
/*      */     }
/*      */ 
/*  937 */     processXMLBaseAttributes(attributes);
/*  938 */     if (this.fFixupLanguage) {
/*  939 */       processXMLLangAttributes(attributes);
/*      */     }
/*      */ 
/*  942 */     if (isIncludeElement(element)) {
/*  943 */       boolean success = handleIncludeElement(attributes);
/*  944 */       if (success) {
/*  945 */         setState(2);
/*      */       }
/*      */       else {
/*  948 */         reportFatalError("NoFallback", new Object[] { attributes.getValue(null, "href") });
/*      */       }
/*      */ 
/*      */     }
/*  952 */     else if (isFallbackElement(element)) {
/*  953 */       handleFallbackElement();
/*      */     }
/*  955 */     else if (hasXIncludeNamespace(element)) {
/*  956 */       if (getSawInclude(this.fDepth - 1)) {
/*  957 */         reportFatalError("IncludeChild", new Object[] { element.rawname });
/*      */       }
/*      */ 
/*  961 */       if (getSawFallback(this.fDepth - 1)) {
/*  962 */         reportFatalError("FallbackChild", new Object[] { element.rawname });
/*      */       }
/*      */ 
/*  966 */       if (getState() == 1) {
/*  967 */         if (this.fResultDepth == 0) {
/*  968 */           checkMultipleRootElements();
/*      */         }
/*  970 */         if (this.fDocumentHandler != null) {
/*  971 */           augs = modifyAugmentations(augs);
/*  972 */           attributes = processAttributes(attributes);
/*  973 */           this.fDocumentHandler.emptyElement(element, attributes, augs);
/*      */         }
/*      */       }
/*      */     }
/*  977 */     else if (getState() == 1) {
/*  978 */       if (this.fResultDepth == 0) {
/*  979 */         checkMultipleRootElements();
/*      */       }
/*  981 */       if (this.fDocumentHandler != null) {
/*  982 */         augs = modifyAugmentations(augs);
/*  983 */         attributes = processAttributes(attributes);
/*  984 */         this.fDocumentHandler.emptyElement(element, attributes, augs);
/*      */       }
/*      */     }
/*      */ 
/*  988 */     setSawFallback(this.fDepth + 1, false);
/*  989 */     setSawInclude(this.fDepth, false);
/*      */ 
/*  992 */     if ((this.fBaseURIScope.size() > 0) && (this.fDepth == this.fBaseURIScope.peek()))
/*      */     {
/*  994 */       restoreBaseURI();
/*      */     }
/*  996 */     this.fDepth -= 1;
/*      */   }
/*      */ 
/*      */   public void endElement(QName element, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1002 */     if (isIncludeElement(element))
/*      */     {
/* 1005 */       if ((getState() == 3) && (!getSawFallback(this.fDepth + 1)))
/*      */       {
/* 1007 */         reportFatalError("NoFallback", new Object[] { "unknown" });
/*      */       }
/*      */     }
/*      */ 
/* 1011 */     if (isFallbackElement(element))
/*      */     {
/* 1014 */       if (getState() == 1) {
/* 1015 */         setState(2);
/*      */       }
/*      */     }
/* 1018 */     else if (getState() == 1) {
/* 1019 */       this.fResultDepth -= 1;
/* 1020 */       if (this.fDocumentHandler != null) {
/* 1021 */         this.fDocumentHandler.endElement(element, augs);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1026 */     setSawFallback(this.fDepth + 1, false);
/* 1027 */     setSawInclude(this.fDepth, false);
/*      */ 
/* 1030 */     if ((this.fBaseURIScope.size() > 0) && (this.fDepth == this.fBaseURIScope.peek()))
/*      */     {
/* 1032 */       restoreBaseURI();
/*      */     }
/*      */ 
/* 1036 */     if ((this.fLanguageScope.size() > 0) && (this.fDepth == this.fLanguageScope.peek()))
/*      */     {
/* 1038 */       this.fCurrentLanguage = restoreLanguage();
/*      */     }
/*      */ 
/* 1041 */     this.fDepth -= 1;
/*      */   }
/*      */ 
/*      */   public void startGeneralEntity(String name, XMLResourceIdentifier resId, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1050 */     if (getState() == 1)
/* 1051 */       if (this.fResultDepth == 0) {
/* 1052 */         if ((augs != null) && (Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED")))) {
/* 1053 */           reportFatalError("UnexpandedEntityReferenceIllegal");
/*      */         }
/*      */       }
/* 1056 */       else if (this.fDocumentHandler != null)
/* 1057 */         this.fDocumentHandler.startGeneralEntity(name, resId, encoding, augs);
/*      */   }
/*      */ 
/*      */   public void textDecl(String version, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1064 */     if ((this.fDocumentHandler != null) && (getState() == 1))
/*      */     {
/* 1066 */       this.fDocumentHandler.textDecl(version, encoding, augs);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endGeneralEntity(String name, Augmentations augs) throws XNIException
/*      */   {
/* 1072 */     if ((this.fDocumentHandler != null) && (getState() == 1) && (this.fResultDepth != 0))
/*      */     {
/* 1075 */       this.fDocumentHandler.endGeneralEntity(name, augs);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void characters(XMLString text, Augmentations augs) throws XNIException
/*      */   {
/* 1081 */     if (getState() == 1)
/* 1082 */       if (this.fResultDepth == 0) {
/* 1083 */         checkWhitespace(text);
/*      */       }
/* 1085 */       else if (this.fDocumentHandler != null)
/*      */       {
/* 1087 */         this.fDepth += 1;
/* 1088 */         augs = modifyAugmentations(augs);
/* 1089 */         this.fDocumentHandler.characters(text, augs);
/* 1090 */         this.fDepth -= 1;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void ignorableWhitespace(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1097 */     if ((this.fDocumentHandler != null) && (getState() == 1) && (this.fResultDepth != 0))
/*      */     {
/* 1100 */       this.fDocumentHandler.ignorableWhitespace(text, augs);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startCDATA(Augmentations augs) throws XNIException {
/* 1105 */     if ((this.fDocumentHandler != null) && (getState() == 1) && (this.fResultDepth != 0))
/*      */     {
/* 1108 */       this.fDocumentHandler.startCDATA(augs);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endCDATA(Augmentations augs) throws XNIException {
/* 1113 */     if ((this.fDocumentHandler != null) && (getState() == 1) && (this.fResultDepth != 0))
/*      */     {
/* 1116 */       this.fDocumentHandler.endCDATA(augs);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endDocument(Augmentations augs) throws XNIException {
/* 1121 */     if (isRootDocument()) {
/* 1122 */       if (!this.fSeenRootElement) {
/* 1123 */         reportFatalError("RootElementRequired");
/*      */       }
/* 1125 */       if (this.fDocumentHandler != null)
/* 1126 */         this.fDocumentHandler.endDocument(augs);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDocumentSource(XMLDocumentSource source)
/*      */   {
/* 1132 */     this.fDocumentSource = source;
/*      */   }
/*      */ 
/*      */   public XMLDocumentSource getDocumentSource() {
/* 1136 */     return this.fDocumentSource;
/*      */   }
/*      */ 
/*      */   public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1156 */     if (this.fDTDHandler != null)
/* 1157 */       this.fDTDHandler.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augmentations);
/*      */   }
/*      */ 
/*      */   public void elementDecl(String name, String contentModel, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1177 */     if (this.fDTDHandler != null)
/* 1178 */       this.fDTDHandler.elementDecl(name, contentModel, augmentations);
/*      */   }
/*      */ 
/*      */   public void endAttlist(Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1186 */     if (this.fDTDHandler != null)
/* 1187 */       this.fDTDHandler.endAttlist(augmentations);
/*      */   }
/*      */ 
/*      */   public void endConditional(Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1196 */     if (this.fDTDHandler != null)
/* 1197 */       this.fDTDHandler.endConditional(augmentations);
/*      */   }
/*      */ 
/*      */   public void endDTD(Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1205 */     if (this.fDTDHandler != null) {
/* 1206 */       this.fDTDHandler.endDTD(augmentations);
/*      */     }
/* 1208 */     this.fInDTD = false;
/*      */   }
/*      */ 
/*      */   public void endExternalSubset(Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1216 */     if (this.fDTDHandler != null)
/* 1217 */       this.fDTDHandler.endExternalSubset(augmentations);
/*      */   }
/*      */ 
/*      */   public void endParameterEntity(String name, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1226 */     if (this.fDTDHandler != null)
/* 1227 */       this.fDTDHandler.endParameterEntity(name, augmentations);
/*      */   }
/*      */ 
/*      */   public void externalEntityDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1239 */     if (this.fDTDHandler != null)
/* 1240 */       this.fDTDHandler.externalEntityDecl(name, identifier, augmentations);
/*      */   }
/*      */ 
/*      */   public XMLDTDSource getDTDSource()
/*      */   {
/* 1248 */     return this.fDTDSource;
/*      */   }
/*      */ 
/*      */   public void ignoredCharacters(XMLString text, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1256 */     if (this.fDTDHandler != null)
/* 1257 */       this.fDTDHandler.ignoredCharacters(text, augmentations);
/*      */   }
/*      */ 
/*      */   public void internalEntityDecl(String name, XMLString text, XMLString nonNormalizedText, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1270 */     if (this.fDTDHandler != null)
/* 1271 */       this.fDTDHandler.internalEntityDecl(name, text, nonNormalizedText, augmentations);
/*      */   }
/*      */ 
/*      */   public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1287 */     addNotation(name, identifier, augmentations);
/* 1288 */     if (this.fDTDHandler != null)
/* 1289 */       this.fDTDHandler.notationDecl(name, identifier, augmentations);
/*      */   }
/*      */ 
/*      */   public void setDTDSource(XMLDTDSource source)
/*      */   {
/* 1297 */     this.fDTDSource = source;
/*      */   }
/*      */ 
/*      */   public void startAttlist(String elementName, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1305 */     if (this.fDTDHandler != null)
/* 1306 */       this.fDTDHandler.startAttlist(elementName, augmentations);
/*      */   }
/*      */ 
/*      */   public void startConditional(short type, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1315 */     if (this.fDTDHandler != null)
/* 1316 */       this.fDTDHandler.startConditional(type, augmentations);
/*      */   }
/*      */ 
/*      */   public void startDTD(XMLLocator locator, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1325 */     this.fInDTD = true;
/* 1326 */     if (this.fDTDHandler != null)
/* 1327 */       this.fDTDHandler.startDTD(locator, augmentations);
/*      */   }
/*      */ 
/*      */   public void startExternalSubset(XMLResourceIdentifier identifier, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1338 */     if (this.fDTDHandler != null)
/* 1339 */       this.fDTDHandler.startExternalSubset(identifier, augmentations);
/*      */   }
/*      */ 
/*      */   public void startParameterEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1352 */     if (this.fDTDHandler != null)
/* 1353 */       this.fDTDHandler.startParameterEntity(name, identifier, encoding, augmentations);
/*      */   }
/*      */ 
/*      */   public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier, String notation, Augmentations augmentations)
/*      */     throws XNIException
/*      */   {
/* 1370 */     addUnparsedEntity(name, identifier, notation, augmentations);
/* 1371 */     if (this.fDTDHandler != null)
/* 1372 */       this.fDTDHandler.unparsedEntityDecl(name, identifier, notation, augmentations);
/*      */   }
/*      */ 
/*      */   public XMLDTDHandler getDTDHandler()
/*      */   {
/* 1384 */     return this.fDTDHandler;
/*      */   }
/*      */ 
/*      */   public void setDTDHandler(XMLDTDHandler handler)
/*      */   {
/* 1391 */     this.fDTDHandler = handler;
/*      */   }
/*      */ 
/*      */   private void setErrorReporter(XMLErrorReporter reporter)
/*      */   {
/* 1397 */     this.fErrorReporter = reporter;
/* 1398 */     if (this.fErrorReporter != null) {
/* 1399 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", this.fXIncludeMessageFormatter);
/*      */ 
/* 1402 */       if (this.fDocLocation != null)
/* 1403 */         this.fErrorReporter.setDocumentLocator(this.fDocLocation);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleFallbackElement()
/*      */   {
/* 1409 */     if (!getSawInclude(this.fDepth - 1)) {
/* 1410 */       if (getState() == 2) {
/* 1411 */         return;
/*      */       }
/* 1413 */       reportFatalError("FallbackParent");
/*      */     }
/*      */ 
/* 1416 */     setSawInclude(this.fDepth, false);
/* 1417 */     this.fNamespaceContext.setContextInvalid();
/*      */ 
/* 1419 */     if (getSawFallback(this.fDepth)) {
/* 1420 */       reportFatalError("MultipleFallbacks");
/*      */     }
/*      */     else {
/* 1423 */       setSawFallback(this.fDepth, true);
/*      */     }
/*      */ 
/* 1429 */     if (getState() == 3)
/* 1430 */       setState(1);
/*      */   }
/*      */ 
/*      */   protected boolean handleIncludeElement(XMLAttributes attributes)
/*      */     throws XNIException
/*      */   {
/* 1436 */     if (getSawInclude(this.fDepth - 1)) {
/* 1437 */       reportFatalError("IncludeChild", new Object[] { XINCLUDE_INCLUDE });
/*      */     }
/* 1439 */     if (getState() == 2) {
/* 1440 */       return true;
/*      */     }
/* 1442 */     setSawInclude(this.fDepth, true);
/* 1443 */     this.fNamespaceContext.setContextInvalid();
/*      */ 
/* 1451 */     String href = attributes.getValue(XINCLUDE_ATTR_HREF);
/* 1452 */     String parse = attributes.getValue(XINCLUDE_ATTR_PARSE);
/* 1453 */     String xpointer = attributes.getValue("xpointer");
/* 1454 */     String accept = attributes.getValue(XINCLUDE_ATTR_ACCEPT);
/* 1455 */     String acceptLanguage = attributes.getValue(XINCLUDE_ATTR_ACCEPT_LANGUAGE);
/*      */ 
/* 1457 */     if (parse == null) {
/* 1458 */       parse = XINCLUDE_PARSE_XML;
/*      */     }
/* 1460 */     if (href == null) {
/* 1461 */       href = XMLSymbols.EMPTY_STRING;
/*      */     }
/* 1463 */     if ((href.length() == 0) && (XINCLUDE_PARSE_XML.equals(parse))) {
/* 1464 */       if (xpointer == null) {
/* 1465 */         reportFatalError("XpointerMissing");
/*      */       }
/*      */       else
/*      */       {
/* 1470 */         Locale locale = this.fErrorReporter != null ? this.fErrorReporter.getLocale() : null;
/* 1471 */         String reason = this.fXIncludeMessageFormatter.formatMessage(locale, "XPointerStreamability", null);
/* 1472 */         reportResourceError("XMLResourceError", new Object[] { href, reason });
/* 1473 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1477 */     URI hrefURI = null;
/*      */     try
/*      */     {
/* 1483 */       hrefURI = new URI(href, true);
/* 1484 */       if (hrefURI.getFragment() != null)
/* 1485 */         reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { href });
/*      */     }
/*      */     catch (URI.MalformedURIException exc)
/*      */     {
/* 1489 */       String newHref = escapeHref(href);
/* 1490 */       if (href != newHref) {
/* 1491 */         href = newHref;
/*      */         try {
/* 1493 */           hrefURI = new URI(href, true);
/* 1494 */           if (hrefURI.getFragment() != null)
/* 1495 */             reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { href });
/*      */         }
/*      */         catch (URI.MalformedURIException exc2)
/*      */         {
/* 1499 */           reportFatalError("HrefSyntacticallyInvalid", new Object[] { href });
/*      */         }
/*      */       }
/*      */       else {
/* 1503 */         reportFatalError("HrefSyntacticallyInvalid", new Object[] { href });
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1509 */     if ((accept != null) && (!isValidInHTTPHeader(accept))) {
/* 1510 */       reportFatalError("AcceptMalformed", null);
/* 1511 */       accept = null;
/*      */     }
/* 1513 */     if ((acceptLanguage != null) && (!isValidInHTTPHeader(acceptLanguage))) {
/* 1514 */       reportFatalError("AcceptLanguageMalformed", null);
/* 1515 */       acceptLanguage = null;
/*      */     }
/*      */ 
/* 1518 */     XMLInputSource includedSource = null;
/* 1519 */     if (this.fEntityResolver != null) {
/*      */       try {
/* 1521 */         XMLResourceIdentifier resourceIdentifier = new XMLResourceIdentifierImpl(null, href, this.fCurrentBaseURI.getExpandedSystemId(), XMLEntityManager.expandSystemId(href, this.fCurrentBaseURI.getExpandedSystemId(), false));
/*      */ 
/* 1531 */         includedSource = this.fEntityResolver.resolveEntity(resourceIdentifier);
/*      */ 
/* 1534 */         if ((includedSource != null) && (!(includedSource instanceof HTTPInputSource)) && ((accept != null) || (acceptLanguage != null)) && (includedSource.getCharacterStream() == null) && (includedSource.getByteStream() == null))
/*      */         {
/* 1540 */           includedSource = createInputSource(includedSource.getPublicId(), includedSource.getSystemId(), includedSource.getBaseSystemId(), accept, acceptLanguage);
/*      */         }
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1545 */         reportResourceError("XMLResourceError", new Object[] { href, e.getMessage() });
/*      */ 
/* 1548 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1552 */     if (includedSource == null)
/*      */     {
/* 1554 */       if ((accept != null) || (acceptLanguage != null)) {
/* 1555 */         includedSource = createInputSource(null, href, this.fCurrentBaseURI.getExpandedSystemId(), accept, acceptLanguage);
/*      */       }
/*      */       else {
/* 1558 */         includedSource = new XMLInputSource(null, href, this.fCurrentBaseURI.getExpandedSystemId());
/*      */       }
/*      */     }
/*      */ 
/* 1562 */     if (parse.equals(XINCLUDE_PARSE_XML))
/*      */     {
/* 1564 */       if (((xpointer != null) && (this.fXPointerChildConfig == null)) || ((xpointer == null) && (this.fXIncludeChildConfig == null)))
/*      */       {
/* 1567 */         String parserName = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
/* 1568 */         if (xpointer != null) {
/* 1569 */           parserName = "com.sun.org.apache.xerces.internal.parsers.XPointerParserConfiguration";
/*      */         }
/* 1571 */         this.fChildConfig = ((XMLParserConfiguration)ObjectFactory.newInstance(parserName, true));
/*      */ 
/* 1577 */         if (this.fSymbolTable != null) this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
/* 1578 */         if (this.fErrorReporter != null) this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/* 1579 */         if (this.fEntityResolver != null) this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fEntityResolver);
/* 1580 */         this.fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
/* 1581 */         this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(this.fBufferSize));
/*      */ 
/* 1584 */         this.fNeedCopyFeatures = true;
/*      */ 
/* 1587 */         this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
/*      */ 
/* 1592 */         this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
/*      */ 
/* 1596 */         this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
/*      */ 
/* 1602 */         if (xpointer != null)
/*      */         {
/* 1604 */           XPointerHandler newHandler = (XPointerHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xpointer-handler");
/*      */ 
/* 1609 */           this.fXPtrProcessor = newHandler;
/*      */ 
/* 1612 */           ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
/*      */ 
/* 1617 */           ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-base-uris", new Boolean(this.fFixupBaseURIs));
/*      */ 
/* 1620 */           ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-language", new Boolean(this.fFixupLanguage));
/*      */ 
/* 1624 */           if (this.fErrorReporter != null) {
/* 1625 */             ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/*      */           }
/*      */ 
/* 1628 */           newHandler.setParent(this);
/* 1629 */           newHandler.setDocumentHandler(getDocumentHandler());
/* 1630 */           this.fXPointerChildConfig = this.fChildConfig;
/*      */         } else {
/* 1632 */           XIncludeHandler newHandler = (XIncludeHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xinclude-handler");
/*      */ 
/* 1637 */           newHandler.setParent(this);
/* 1638 */           newHandler.setDocumentHandler(getDocumentHandler());
/* 1639 */           this.fXIncludeChildConfig = this.fChildConfig;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1644 */       if (xpointer != null) {
/* 1645 */         this.fChildConfig = this.fXPointerChildConfig;
/*      */         try
/*      */         {
/* 1649 */           this.fXPtrProcessor.parseXPointer(xpointer);
/*      */         }
/*      */         catch (XNIException ex)
/*      */         {
/* 1653 */           reportResourceError("XMLResourceError", new Object[] { href, ex.getMessage() });
/*      */ 
/* 1656 */           return false;
/*      */         }
/*      */       } else {
/* 1659 */         this.fChildConfig = this.fXIncludeChildConfig;
/*      */       }
/*      */ 
/* 1663 */       if (this.fNeedCopyFeatures) {
/* 1664 */         copyFeatures(this.fSettings, this.fChildConfig);
/*      */       }
/* 1666 */       this.fNeedCopyFeatures = false;
/*      */       try
/*      */       {
/* 1669 */         this.fNamespaceContext.pushScope();
/*      */ 
/* 1671 */         this.fChildConfig.parse(includedSource);
/*      */ 
/* 1673 */         if (this.fErrorReporter != null) {
/* 1674 */           this.fErrorReporter.setDocumentLocator(this.fDocLocation);
/*      */         }
/*      */ 
/* 1678 */         if (xpointer != null)
/*      */         {
/* 1680 */           if (!this.fXPtrProcessor.isXPointerResolved()) {
/* 1681 */             Locale locale = this.fErrorReporter != null ? this.fErrorReporter.getLocale() : null;
/* 1682 */             reason = this.fXIncludeMessageFormatter.formatMessage(locale, "XPointerResolutionUnsuccessful", null);
/* 1683 */             reportResourceError("XMLResourceError", new Object[] { href, reason });
/*      */ 
/* 1685 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (XNIException e)
/*      */       {
/* 1691 */         if (this.fErrorReporter != null) {
/* 1692 */           this.fErrorReporter.setDocumentLocator(this.fDocLocation);
/*      */         }
/* 1694 */         reportFatalError("XMLParseError", new Object[] { href });
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */         String reason;
/* 1698 */         if (this.fErrorReporter != null) {
/* 1699 */           this.fErrorReporter.setDocumentLocator(this.fDocLocation);
/*      */         }
/*      */ 
/* 1704 */         reportResourceError("XMLResourceError", new Object[] { href, e.getMessage() });
/*      */ 
/* 1707 */         return 0;
/*      */       }
/*      */       finally {
/* 1710 */         this.fNamespaceContext.popScope();
/*      */       }
/*      */     }
/* 1713 */     else if (parse.equals(XINCLUDE_PARSE_TEXT))
/*      */     {
/* 1715 */       String encoding = attributes.getValue(XINCLUDE_ATTR_ENCODING);
/* 1716 */       includedSource.setEncoding(encoding);
/* 1717 */       XIncludeTextReader textReader = null;
/*      */       try
/*      */       {
/* 1721 */         if (!this.fIsXML11) {
/* 1722 */           if (this.fXInclude10TextReader == null) {
/* 1723 */             this.fXInclude10TextReader = new XIncludeTextReader(includedSource, this, this.fBufferSize);
/*      */           }
/*      */           else {
/* 1726 */             this.fXInclude10TextReader.setInputSource(includedSource);
/*      */           }
/* 1728 */           textReader = this.fXInclude10TextReader;
/*      */         }
/*      */         else {
/* 1731 */           if (this.fXInclude11TextReader == null) {
/* 1732 */             this.fXInclude11TextReader = new XInclude11TextReader(includedSource, this, this.fBufferSize);
/*      */           }
/*      */           else {
/* 1735 */             this.fXInclude11TextReader.setInputSource(includedSource);
/*      */           }
/* 1737 */           textReader = this.fXInclude11TextReader;
/*      */         }
/* 1739 */         textReader.setErrorReporter(this.fErrorReporter);
/* 1740 */         textReader.parse();
/*      */       }
/*      */       catch (MalformedByteSequenceException ex)
/*      */       {
/* 1744 */         this.fErrorReporter.reportError(ex.getDomain(), ex.getKey(), ex.getArguments(), (short)2);
/*      */       }
/*      */       catch (CharConversionException e)
/*      */       {
/* 1748 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2);
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1752 */         reportResourceError("TextResourceError", new Object[] { href, e.getMessage() });
/*      */ 
/* 1755 */         return false;
/*      */       }
/*      */       finally {
/* 1758 */         if (textReader != null)
/*      */           try {
/* 1760 */             textReader.close();
/*      */           }
/*      */           catch (IOException e) {
/* 1763 */             reportResourceError("TextResourceError", new Object[] { href, e.getMessage() });
/*      */ 
/* 1766 */             return false;
/*      */           }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1772 */       reportFatalError("InvalidParseValue", new Object[] { parse });
/*      */     }
/* 1774 */     return true;
/*      */   }
/*      */ 
/*      */   protected boolean hasXIncludeNamespace(QName element)
/*      */   {
/* 1786 */     return (element.uri == XINCLUDE_NS_URI) || (this.fNamespaceContext.getURI(element.prefix) == XINCLUDE_NS_URI);
/*      */   }
/*      */ 
/*      */   protected boolean isIncludeElement(QName element)
/*      */   {
/* 1799 */     return (element.localpart.equals(XINCLUDE_INCLUDE)) && (hasXIncludeNamespace(element));
/*      */   }
/*      */ 
/*      */   protected boolean isFallbackElement(QName element)
/*      */   {
/* 1812 */     return (element.localpart.equals(XINCLUDE_FALLBACK)) && (hasXIncludeNamespace(element));
/*      */   }
/*      */ 
/*      */   protected boolean sameBaseURIAsIncludeParent()
/*      */   {
/* 1826 */     String parentBaseURI = getIncludeParentBaseURI();
/* 1827 */     String baseURI = this.fCurrentBaseURI.getExpandedSystemId();
/*      */ 
/* 1836 */     return (parentBaseURI != null) && (parentBaseURI.equals(baseURI));
/*      */   }
/*      */ 
/*      */   protected boolean sameLanguageAsIncludeParent()
/*      */   {
/* 1852 */     String parentLanguage = getIncludeParentLanguage();
/* 1853 */     return (parentLanguage != null) && (parentLanguage.equalsIgnoreCase(this.fCurrentLanguage));
/*      */   }
/*      */ 
/*      */   protected boolean searchForRecursiveIncludes(XMLLocator includedSource)
/*      */   {
/* 1863 */     String includedSystemId = includedSource.getExpandedSystemId();
/*      */ 
/* 1865 */     if (includedSystemId == null) {
/*      */       try {
/* 1867 */         includedSystemId = XMLEntityManager.expandSystemId(includedSource.getLiteralSystemId(), includedSource.getBaseSystemId(), false);
/*      */       }
/*      */       catch (URI.MalformedURIException e)
/*      */       {
/* 1874 */         reportFatalError("ExpandedSystemId");
/*      */       }
/*      */     }
/*      */ 
/* 1878 */     if (includedSystemId.equals(this.fCurrentBaseURI.getExpandedSystemId())) {
/* 1879 */       return true;
/*      */     }
/*      */ 
/* 1882 */     if (this.fParentXIncludeHandler == null) {
/* 1883 */       return false;
/*      */     }
/* 1885 */     return this.fParentXIncludeHandler.searchForRecursiveIncludes(includedSource);
/*      */   }
/*      */ 
/*      */   protected boolean isTopLevelIncludedItem()
/*      */   {
/* 1896 */     return (isTopLevelIncludedItemViaInclude()) || (isTopLevelIncludedItemViaFallback());
/*      */   }
/*      */ 
/*      */   protected boolean isTopLevelIncludedItemViaInclude()
/*      */   {
/* 1901 */     return (this.fDepth == 1) && (!isRootDocument());
/*      */   }
/*      */ 
/*      */   protected boolean isTopLevelIncludedItemViaFallback()
/*      */   {
/* 1909 */     return getSawFallback(this.fDepth - 1);
/*      */   }
/*      */ 
/*      */   protected XMLAttributes processAttributes(XMLAttributes attributes)
/*      */   {
/* 1927 */     if (isTopLevelIncludedItem())
/*      */     {
/* 1931 */       if ((this.fFixupBaseURIs) && (!sameBaseURIAsIncludeParent())) {
/* 1932 */         if (attributes == null) {
/* 1933 */           attributes = new XMLAttributesImpl();
/*      */         }
/*      */ 
/* 1938 */         String uri = null;
/*      */         try {
/* 1940 */           uri = getRelativeBaseURI();
/*      */         }
/*      */         catch (URI.MalformedURIException e)
/*      */         {
/* 1945 */           uri = this.fCurrentBaseURI.getExpandedSystemId();
/*      */         }
/* 1947 */         int index = attributes.addAttribute(XML_BASE_QNAME, XMLSymbols.fCDATASymbol, uri);
/*      */ 
/* 1952 */         attributes.setSpecified(index, true);
/*      */       }
/*      */ 
/* 1958 */       if ((this.fFixupLanguage) && (!sameLanguageAsIncludeParent())) {
/* 1959 */         if (attributes == null) {
/* 1960 */           attributes = new XMLAttributesImpl();
/*      */         }
/* 1962 */         int index = attributes.addAttribute(XML_LANG_QNAME, XMLSymbols.fCDATASymbol, this.fCurrentLanguage);
/*      */ 
/* 1967 */         attributes.setSpecified(index, true);
/*      */       }
/*      */ 
/* 1971 */       Enumeration inscopeNS = this.fNamespaceContext.getAllPrefixes();
/* 1972 */       while (inscopeNS.hasMoreElements()) {
/* 1973 */         String prefix = (String)inscopeNS.nextElement();
/* 1974 */         String parentURI = this.fNamespaceContext.getURIFromIncludeParent(prefix);
/*      */ 
/* 1976 */         String uri = this.fNamespaceContext.getURI(prefix);
/* 1977 */         if ((parentURI != uri) && (attributes != null)) {
/* 1978 */           if (prefix == XMLSymbols.EMPTY_STRING) {
/* 1979 */             if (attributes.getValue(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS) == null)
/*      */             {
/* 1984 */               if (attributes == null) {
/* 1985 */                 attributes = new XMLAttributesImpl();
/*      */               }
/*      */ 
/* 1988 */               QName ns = (QName)NEW_NS_ATTR_QNAME.clone();
/* 1989 */               ns.prefix = null;
/* 1990 */               ns.localpart = XMLSymbols.PREFIX_XMLNS;
/* 1991 */               ns.rawname = XMLSymbols.PREFIX_XMLNS;
/* 1992 */               int index = attributes.addAttribute(ns, XMLSymbols.fCDATASymbol, uri != null ? uri : XMLSymbols.EMPTY_STRING);
/*      */ 
/* 1997 */               attributes.setSpecified(index, true);
/*      */ 
/* 2001 */               this.fNamespaceContext.declarePrefix(prefix, uri);
/*      */             }
/*      */           }
/* 2004 */           else if (attributes.getValue(NamespaceContext.XMLNS_URI, prefix) == null)
/*      */           {
/* 2007 */             if (attributes == null) {
/* 2008 */               attributes = new XMLAttributesImpl();
/*      */             }
/*      */ 
/* 2011 */             QName ns = (QName)NEW_NS_ATTR_QNAME.clone();
/* 2012 */             ns.localpart = prefix;
/* 2013 */             ns.rawname += prefix;
/* 2014 */             ns.rawname = (this.fSymbolTable != null ? this.fSymbolTable.addSymbol(ns.rawname) : ns.rawname.intern());
/*      */ 
/* 2017 */             int index = attributes.addAttribute(ns, XMLSymbols.fCDATASymbol, uri != null ? uri : XMLSymbols.EMPTY_STRING);
/*      */ 
/* 2022 */             attributes.setSpecified(index, true);
/*      */ 
/* 2026 */             this.fNamespaceContext.declarePrefix(prefix, uri);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2032 */     if (attributes != null) {
/* 2033 */       int length = attributes.getLength();
/* 2034 */       for (int i = 0; i < length; i++) {
/* 2035 */         String type = attributes.getType(i);
/* 2036 */         String value = attributes.getValue(i);
/* 2037 */         if (type == XMLSymbols.fENTITYSymbol) {
/* 2038 */           checkUnparsedEntity(value);
/*      */         }
/* 2040 */         if (type == XMLSymbols.fENTITIESSymbol)
/*      */         {
/* 2042 */           StringTokenizer st = new StringTokenizer(value);
/* 2043 */           while (st.hasMoreTokens()) {
/* 2044 */             String entName = st.nextToken();
/* 2045 */             checkUnparsedEntity(entName);
/*      */           }
/*      */         }
/* 2048 */         else if (type == XMLSymbols.fNOTATIONSymbol)
/*      */         {
/* 2050 */           checkNotation(value);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2061 */     return attributes;
/*      */   }
/*      */ 
/*      */   protected String getRelativeBaseURI()
/*      */     throws URI.MalformedURIException
/*      */   {
/* 2071 */     int includeParentDepth = getIncludeParentDepth();
/* 2072 */     String relativeURI = getRelativeURI(includeParentDepth);
/* 2073 */     if (isRootDocument()) {
/* 2074 */       return relativeURI;
/*      */     }
/*      */ 
/* 2077 */     if (relativeURI.equals("")) {
/* 2078 */       relativeURI = this.fCurrentBaseURI.getLiteralSystemId();
/*      */     }
/*      */ 
/* 2081 */     if (includeParentDepth == 0) {
/* 2082 */       if (this.fParentRelativeURI == null) {
/* 2083 */         this.fParentRelativeURI = this.fParentXIncludeHandler.getRelativeBaseURI();
/*      */       }
/*      */ 
/* 2086 */       if (this.fParentRelativeURI.equals("")) {
/* 2087 */         return relativeURI;
/*      */       }
/*      */ 
/* 2090 */       URI base = new URI(this.fParentRelativeURI, true);
/* 2091 */       URI uri = new URI(base, relativeURI);
/*      */ 
/* 2094 */       String baseScheme = base.getScheme();
/* 2095 */       String literalScheme = uri.getScheme();
/* 2096 */       if (!isEqual(baseScheme, literalScheme)) {
/* 2097 */         return relativeURI;
/*      */       }
/*      */ 
/* 2101 */       String baseAuthority = base.getAuthority();
/* 2102 */       String literalAuthority = uri.getAuthority();
/* 2103 */       if (!isEqual(baseAuthority, literalAuthority)) {
/* 2104 */         return uri.getSchemeSpecificPart();
/*      */       }
/*      */ 
/* 2112 */       String literalPath = uri.getPath();
/* 2113 */       String literalQuery = uri.getQueryString();
/* 2114 */       String literalFragment = uri.getFragment();
/* 2115 */       if ((literalQuery != null) || (literalFragment != null)) {
/* 2116 */         StringBuffer buffer = new StringBuffer();
/* 2117 */         if (literalPath != null) {
/* 2118 */           buffer.append(literalPath);
/*      */         }
/* 2120 */         if (literalQuery != null) {
/* 2121 */           buffer.append('?');
/* 2122 */           buffer.append(literalQuery);
/*      */         }
/* 2124 */         if (literalFragment != null) {
/* 2125 */           buffer.append('#');
/* 2126 */           buffer.append(literalFragment);
/*      */         }
/* 2128 */         return buffer.toString();
/*      */       }
/* 2130 */       return literalPath;
/*      */     }
/*      */ 
/* 2133 */     return relativeURI;
/*      */   }
/*      */ 
/*      */   private String getIncludeParentBaseURI()
/*      */   {
/* 2143 */     int depth = getIncludeParentDepth();
/* 2144 */     if ((!isRootDocument()) && (depth == 0)) {
/* 2145 */       return this.fParentXIncludeHandler.getIncludeParentBaseURI();
/*      */     }
/*      */ 
/* 2148 */     return getBaseURI(depth);
/*      */   }
/*      */ 
/*      */   private String getIncludeParentLanguage()
/*      */   {
/* 2158 */     int depth = getIncludeParentDepth();
/* 2159 */     if ((!isRootDocument()) && (depth == 0)) {
/* 2160 */       return this.fParentXIncludeHandler.getIncludeParentLanguage();
/*      */     }
/*      */ 
/* 2163 */     return getLanguage(depth);
/*      */   }
/*      */ 
/*      */   private int getIncludeParentDepth()
/*      */   {
/* 2178 */     for (int i = this.fDepth - 1; i >= 0; i--)
/*      */     {
/* 2185 */       if ((!getSawInclude(i)) && (!getSawFallback(i))) {
/* 2186 */         return i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2191 */     return 0;
/*      */   }
/*      */ 
/*      */   private int getResultDepth()
/*      */   {
/* 2198 */     return this.fResultDepth;
/*      */   }
/*      */ 
/*      */   protected Augmentations modifyAugmentations(Augmentations augs)
/*      */   {
/* 2208 */     return modifyAugmentations(augs, false);
/*      */   }
/*      */ 
/*      */   protected Augmentations modifyAugmentations(Augmentations augs, boolean force)
/*      */   {
/* 2221 */     if ((force) || (isTopLevelIncludedItem())) {
/* 2222 */       if (augs == null) {
/* 2223 */         augs = new AugmentationsImpl();
/*      */       }
/* 2225 */       augs.putItem(XINCLUDE_INCLUDED, Boolean.TRUE);
/*      */     }
/* 2227 */     return augs;
/*      */   }
/*      */ 
/*      */   protected int getState(int depth) {
/* 2231 */     return this.fState[depth];
/*      */   }
/*      */ 
/*      */   protected int getState() {
/* 2235 */     return this.fState[this.fDepth];
/*      */   }
/*      */ 
/*      */   protected void setState(int state) {
/* 2239 */     if (this.fDepth >= this.fState.length) {
/* 2240 */       int[] newarray = new int[this.fDepth * 2];
/* 2241 */       System.arraycopy(this.fState, 0, newarray, 0, this.fState.length);
/* 2242 */       this.fState = newarray;
/*      */     }
/* 2244 */     this.fState[this.fDepth] = state;
/*      */   }
/*      */ 
/*      */   protected void setSawFallback(int depth, boolean val)
/*      */   {
/* 2256 */     if (depth >= this.fSawFallback.length) {
/* 2257 */       boolean[] newarray = new boolean[depth * 2];
/* 2258 */       System.arraycopy(this.fSawFallback, 0, newarray, 0, this.fSawFallback.length);
/* 2259 */       this.fSawFallback = newarray;
/*      */     }
/* 2261 */     this.fSawFallback[depth] = val;
/*      */   }
/*      */ 
/*      */   protected boolean getSawFallback(int depth)
/*      */   {
/* 2272 */     if (depth >= this.fSawFallback.length) {
/* 2273 */       return false;
/*      */     }
/* 2275 */     return this.fSawFallback[depth];
/*      */   }
/*      */ 
/*      */   protected void setSawInclude(int depth, boolean val)
/*      */   {
/* 2286 */     if (depth >= this.fSawInclude.length) {
/* 2287 */       boolean[] newarray = new boolean[depth * 2];
/* 2288 */       System.arraycopy(this.fSawInclude, 0, newarray, 0, this.fSawInclude.length);
/* 2289 */       this.fSawInclude = newarray;
/*      */     }
/* 2291 */     this.fSawInclude[depth] = val;
/*      */   }
/*      */ 
/*      */   protected boolean getSawInclude(int depth)
/*      */   {
/* 2302 */     if (depth >= this.fSawInclude.length) {
/* 2303 */       return false;
/*      */     }
/* 2305 */     return this.fSawInclude[depth];
/*      */   }
/*      */ 
/*      */   protected void reportResourceError(String key) {
/* 2309 */     reportFatalError(key, null);
/*      */   }
/*      */ 
/*      */   protected void reportResourceError(String key, Object[] args) {
/* 2313 */     reportError(key, args, (short)0);
/*      */   }
/*      */ 
/*      */   protected void reportFatalError(String key) {
/* 2317 */     reportFatalError(key, null);
/*      */   }
/*      */ 
/*      */   protected void reportFatalError(String key, Object[] args) {
/* 2321 */     reportError(key, args, (short)2);
/*      */   }
/*      */ 
/*      */   private void reportError(String key, Object[] args, short severity) {
/* 2325 */     if (this.fErrorReporter != null)
/* 2326 */       this.fErrorReporter.reportError("http://www.w3.org/TR/xinclude", key, args, severity);
/*      */   }
/*      */ 
/*      */   protected void setParent(XIncludeHandler parent)
/*      */   {
/* 2341 */     this.fParentXIncludeHandler = parent;
/*      */   }
/*      */ 
/*      */   protected boolean isRootDocument()
/*      */   {
/* 2346 */     return this.fParentXIncludeHandler == null;
/*      */   }
/*      */ 
/*      */   protected void addUnparsedEntity(String name, XMLResourceIdentifier identifier, String notation, Augmentations augmentations)
/*      */   {
/* 2360 */     UnparsedEntity ent = new UnparsedEntity();
/* 2361 */     ent.name = name;
/* 2362 */     ent.systemId = identifier.getLiteralSystemId();
/* 2363 */     ent.publicId = identifier.getPublicId();
/* 2364 */     ent.baseURI = identifier.getBaseSystemId();
/* 2365 */     ent.expandedSystemId = identifier.getExpandedSystemId();
/* 2366 */     ent.notation = notation;
/* 2367 */     ent.augmentations = augmentations;
/* 2368 */     this.fUnparsedEntities.add(ent);
/*      */   }
/*      */ 
/*      */   protected void addNotation(String name, XMLResourceIdentifier identifier, Augmentations augmentations)
/*      */   {
/* 2381 */     Notation not = new Notation();
/* 2382 */     not.name = name;
/* 2383 */     not.systemId = identifier.getLiteralSystemId();
/* 2384 */     not.publicId = identifier.getPublicId();
/* 2385 */     not.baseURI = identifier.getBaseSystemId();
/* 2386 */     not.expandedSystemId = identifier.getExpandedSystemId();
/* 2387 */     not.augmentations = augmentations;
/* 2388 */     this.fNotations.add(not);
/*      */   }
/*      */ 
/*      */   protected void checkUnparsedEntity(String entName)
/*      */   {
/* 2400 */     UnparsedEntity ent = new UnparsedEntity();
/* 2401 */     ent.name = entName;
/* 2402 */     int index = this.fUnparsedEntities.indexOf(ent);
/* 2403 */     if (index != -1) {
/* 2404 */       ent = (UnparsedEntity)this.fUnparsedEntities.get(index);
/*      */ 
/* 2406 */       checkNotation(ent.notation);
/* 2407 */       checkAndSendUnparsedEntity(ent);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkNotation(String notName)
/*      */   {
/* 2419 */     Notation not = new Notation();
/* 2420 */     not.name = notName;
/* 2421 */     int index = this.fNotations.indexOf(not);
/* 2422 */     if (index != -1) {
/* 2423 */       not = (Notation)this.fNotations.get(index);
/* 2424 */       checkAndSendNotation(not);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkAndSendUnparsedEntity(UnparsedEntity ent)
/*      */   {
/* 2436 */     if (isRootDocument()) {
/* 2437 */       int index = this.fUnparsedEntities.indexOf(ent);
/* 2438 */       if (index == -1)
/*      */       {
/* 2442 */         XMLResourceIdentifier id = new XMLResourceIdentifierImpl(ent.publicId, ent.systemId, ent.baseURI, ent.expandedSystemId);
/*      */ 
/* 2448 */         addUnparsedEntity(ent.name, id, ent.notation, ent.augmentations);
/*      */ 
/* 2453 */         if ((this.fSendUEAndNotationEvents) && (this.fDTDHandler != null)) {
/* 2454 */           this.fDTDHandler.unparsedEntityDecl(ent.name, id, ent.notation, ent.augmentations);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2462 */         UnparsedEntity localEntity = (UnparsedEntity)this.fUnparsedEntities.get(index);
/*      */ 
/* 2464 */         if (!ent.isDuplicate(localEntity)) {
/* 2465 */           reportFatalError("NonDuplicateUnparsedEntity", new Object[] { ent.name });
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2472 */       this.fParentXIncludeHandler.checkAndSendUnparsedEntity(ent);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkAndSendNotation(Notation not)
/*      */   {
/* 2484 */     if (isRootDocument()) {
/* 2485 */       int index = this.fNotations.indexOf(not);
/* 2486 */       if (index == -1)
/*      */       {
/* 2488 */         XMLResourceIdentifier id = new XMLResourceIdentifierImpl(not.publicId, not.systemId, not.baseURI, not.expandedSystemId);
/*      */ 
/* 2494 */         addNotation(not.name, id, not.augmentations);
/* 2495 */         if ((this.fSendUEAndNotationEvents) && (this.fDTDHandler != null))
/* 2496 */           this.fDTDHandler.notationDecl(not.name, id, not.augmentations);
/*      */       }
/*      */       else
/*      */       {
/* 2500 */         Notation localNotation = (Notation)this.fNotations.get(index);
/* 2501 */         if (!not.isDuplicate(localNotation)) {
/* 2502 */           reportFatalError("NonDuplicateNotation", new Object[] { not.name });
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2509 */       this.fParentXIncludeHandler.checkAndSendNotation(not);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkWhitespace(XMLString value)
/*      */   {
/* 2519 */     int end = value.offset + value.length;
/* 2520 */     for (int i = value.offset; i < end; i++)
/* 2521 */       if (!XMLChar.isSpace(value.ch[i])) {
/* 2522 */         reportFatalError("ContentIllegalAtTopLevel");
/* 2523 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   private void checkMultipleRootElements()
/*      */   {
/* 2532 */     if (getRootElementProcessed()) {
/* 2533 */       reportFatalError("MultipleRootElements");
/*      */     }
/* 2535 */     setRootElementProcessed(true);
/*      */   }
/*      */ 
/*      */   private void setRootElementProcessed(boolean seenRoot)
/*      */   {
/* 2542 */     if (isRootDocument()) {
/* 2543 */       this.fSeenRootElement = seenRoot;
/* 2544 */       return;
/*      */     }
/* 2546 */     this.fParentXIncludeHandler.setRootElementProcessed(seenRoot);
/*      */   }
/*      */ 
/*      */   private boolean getRootElementProcessed()
/*      */   {
/* 2553 */     return isRootDocument() ? this.fSeenRootElement : this.fParentXIncludeHandler.getRootElementProcessed();
/*      */   }
/*      */ 
/*      */   protected void copyFeatures(XMLComponentManager from, ParserConfigurationSettings to)
/*      */   {
/* 2561 */     Enumeration features = Constants.getXercesFeatures();
/* 2562 */     copyFeatures1(features, "http://apache.org/xml/features/", from, to);
/* 2563 */     features = Constants.getSAXFeatures();
/* 2564 */     copyFeatures1(features, "http://xml.org/sax/features/", from, to);
/*      */   }
/*      */ 
/*      */   protected void copyFeatures(XMLComponentManager from, XMLParserConfiguration to)
/*      */   {
/* 2570 */     Enumeration features = Constants.getXercesFeatures();
/* 2571 */     copyFeatures1(features, "http://apache.org/xml/features/", from, to);
/* 2572 */     features = Constants.getSAXFeatures();
/* 2573 */     copyFeatures1(features, "http://xml.org/sax/features/", from, to);
/*      */   }
/*      */ 
/*      */   private void copyFeatures1(Enumeration features, String featurePrefix, XMLComponentManager from, ParserConfigurationSettings to)
/*      */   {
/* 2581 */     while (features.hasMoreElements()) {
/* 2582 */       String featureId = featurePrefix + (String)features.nextElement();
/*      */ 
/* 2584 */       to.addRecognizedFeatures(new String[] { featureId });
/*      */       try
/*      */       {
/* 2587 */         to.setFeature(featureId, from.getFeature(featureId));
/*      */       }
/*      */       catch (XMLConfigurationException e)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void copyFeatures1(Enumeration features, String featurePrefix, XMLComponentManager from, XMLParserConfiguration to)
/*      */   {
/* 2601 */     while (features.hasMoreElements()) {
/* 2602 */       String featureId = featurePrefix + (String)features.nextElement();
/* 2603 */       boolean value = from.getFeature(featureId);
/*      */       try
/*      */       {
/* 2606 */         to.setFeature(featureId, value);
/*      */       }
/*      */       catch (XMLConfigurationException e)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void saveBaseURI()
/*      */   {
/* 2713 */     this.fBaseURIScope.push(this.fDepth);
/* 2714 */     this.fBaseURI.push(this.fCurrentBaseURI.getBaseSystemId());
/* 2715 */     this.fLiteralSystemID.push(this.fCurrentBaseURI.getLiteralSystemId());
/* 2716 */     this.fExpandedSystemID.push(this.fCurrentBaseURI.getExpandedSystemId());
/*      */   }
/*      */ 
/*      */   protected void restoreBaseURI()
/*      */   {
/* 2723 */     this.fBaseURI.pop();
/* 2724 */     this.fLiteralSystemID.pop();
/* 2725 */     this.fExpandedSystemID.pop();
/* 2726 */     this.fBaseURIScope.pop();
/* 2727 */     this.fCurrentBaseURI.setBaseSystemId((String)this.fBaseURI.peek());
/* 2728 */     this.fCurrentBaseURI.setLiteralSystemId((String)this.fLiteralSystemID.peek());
/* 2729 */     this.fCurrentBaseURI.setExpandedSystemId((String)this.fExpandedSystemID.peek());
/*      */   }
/*      */ 
/*      */   protected void saveLanguage(String language)
/*      */   {
/* 2740 */     this.fLanguageScope.push(this.fDepth);
/* 2741 */     this.fLanguageStack.push(language);
/*      */   }
/*      */ 
/*      */   public String restoreLanguage()
/*      */   {
/* 2748 */     this.fLanguageStack.pop();
/* 2749 */     this.fLanguageScope.pop();
/* 2750 */     return (String)this.fLanguageStack.peek();
/*      */   }
/*      */ 
/*      */   public String getBaseURI(int depth)
/*      */   {
/* 2759 */     int scope = scopeOfBaseURI(depth);
/* 2760 */     return (String)this.fExpandedSystemID.elementAt(scope);
/*      */   }
/*      */ 
/*      */   public String getLanguage(int depth)
/*      */   {
/* 2769 */     int scope = scopeOfLanguage(depth);
/* 2770 */     return (String)this.fLanguageStack.elementAt(scope);
/*      */   }
/*      */ 
/*      */   public String getRelativeURI(int depth)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 2785 */     int start = scopeOfBaseURI(depth) + 1;
/* 2786 */     if (start == this.fBaseURIScope.size())
/*      */     {
/* 2788 */       return "";
/*      */     }
/* 2790 */     URI uri = new URI("file", (String)this.fLiteralSystemID.elementAt(start));
/* 2791 */     for (int i = start + 1; i < this.fBaseURIScope.size(); i++) {
/* 2792 */       uri = new URI(uri, (String)this.fLiteralSystemID.elementAt(i));
/*      */     }
/* 2794 */     return uri.getPath();
/*      */   }
/*      */ 
/*      */   private int scopeOfBaseURI(int depth)
/*      */   {
/* 2801 */     for (int i = this.fBaseURIScope.size() - 1; i >= 0; i--) {
/* 2802 */       if (this.fBaseURIScope.elementAt(i) <= depth) {
/* 2803 */         return i;
/*      */       }
/*      */     }
/* 2806 */     return -1;
/*      */   }
/*      */ 
/*      */   private int scopeOfLanguage(int depth) {
/* 2810 */     for (int i = this.fLanguageScope.size() - 1; i >= 0; i--) {
/* 2811 */       if (this.fLanguageScope.elementAt(i) <= depth) {
/* 2812 */         return i;
/*      */       }
/*      */     }
/* 2815 */     return -1;
/*      */   }
/*      */ 
/*      */   protected void processXMLBaseAttributes(XMLAttributes attributes)
/*      */   {
/* 2823 */     String baseURIValue = attributes.getValue(NamespaceContext.XML_URI, "base");
/*      */ 
/* 2825 */     if (baseURIValue != null)
/*      */       try {
/* 2827 */         String expandedValue = XMLEntityManager.expandSystemId(baseURIValue, this.fCurrentBaseURI.getExpandedSystemId(), false);
/*      */ 
/* 2832 */         this.fCurrentBaseURI.setLiteralSystemId(baseURIValue);
/* 2833 */         this.fCurrentBaseURI.setBaseSystemId(this.fCurrentBaseURI.getExpandedSystemId());
/*      */ 
/* 2835 */         this.fCurrentBaseURI.setExpandedSystemId(expandedValue);
/*      */ 
/* 2838 */         saveBaseURI();
/*      */       }
/*      */       catch (URI.MalformedURIException e)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void processXMLLangAttributes(XMLAttributes attributes)
/*      */   {
/* 2851 */     String language = attributes.getValue(NamespaceContext.XML_URI, "lang");
/* 2852 */     if (language != null) {
/* 2853 */       this.fCurrentLanguage = language;
/* 2854 */       saveLanguage(this.fCurrentLanguage);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isValidInHTTPHeader(String value)
/*      */   {
/* 2868 */     for (int i = value.length() - 1; i >= 0; i--) {
/* 2869 */       char ch = value.charAt(i);
/* 2870 */       if ((ch < ' ') || (ch > '~')) {
/* 2871 */         return false;
/*      */       }
/*      */     }
/* 2874 */     return true;
/*      */   }
/*      */ 
/*      */   private XMLInputSource createInputSource(String publicId, String systemId, String baseSystemId, String accept, String acceptLanguage)
/*      */   {
/* 2884 */     HTTPInputSource httpSource = new HTTPInputSource(publicId, systemId, baseSystemId);
/* 2885 */     if ((accept != null) && (accept.length() > 0)) {
/* 2886 */       httpSource.setHTTPRequestProperty("Accept", accept);
/*      */     }
/* 2888 */     if ((acceptLanguage != null) && (acceptLanguage.length() > 0)) {
/* 2889 */       httpSource.setHTTPRequestProperty("Accept-Language", acceptLanguage);
/*      */     }
/* 2891 */     return httpSource;
/*      */   }
/*      */ 
/*      */   private boolean isEqual(String one, String two) {
/* 2895 */     return (one == two) || ((one != null) && (one.equals(two)));
/*      */   }
/*      */ 
/*      */   private String escapeHref(String href)
/*      */   {
/* 2932 */     int len = href.length();
/*      */ 
/* 2934 */     StringBuffer buffer = new StringBuffer(len * 3);
/*      */ 
/* 2937 */     for (int i = 0; 
/* 2938 */       i < len; i++) {
/* 2939 */       int ch = href.charAt(i);
/*      */ 
/* 2941 */       if (ch > 126)
/*      */       {
/*      */         break;
/*      */       }
/* 2945 */       if (ch < 32) {
/* 2946 */         return href;
/*      */       }
/* 2948 */       if (gNeedEscaping[ch] != 0) {
/* 2949 */         buffer.append('%');
/* 2950 */         buffer.append(gAfterEscaping1[ch]);
/* 2951 */         buffer.append(gAfterEscaping2[ch]);
/*      */       }
/*      */       else {
/* 2954 */         buffer.append((char)ch);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2959 */     if (i < len)
/*      */     {
/* 2961 */       for (int j = i; j < len; j++) {
/* 2962 */         int ch = href.charAt(j);
/* 2963 */         if (((ch < 32) || (ch > 126)) && ((ch < 160) || (ch > 55295)) && ((ch < 63744) || (ch > 64975)) && ((ch < 65008) || (ch > 65519)))
/*      */         {
/* 2969 */           if (XMLChar.isHighSurrogate(ch)) { j++; if (j < len) {
/* 2970 */               int ch2 = href.charAt(j);
/* 2971 */               if (XMLChar.isLowSurrogate(ch2)) {
/* 2972 */                 ch2 = XMLChar.supplemental((char)ch, (char)ch2);
/* 2973 */                 if ((ch2 < 983040) && ((ch2 & 0xFFFF) <= 65533)) {
/*      */                   continue;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 2979 */           return href;
/*      */         }
/*      */       }
/*      */ 
/* 2983 */       byte[] bytes = null;
/*      */       try
/*      */       {
/* 2986 */         bytes = href.substring(i).getBytes("UTF-8");
/*      */       }
/*      */       catch (UnsupportedEncodingException e) {
/* 2989 */         return href;
/*      */       }
/* 2991 */       len = bytes.length;
/*      */ 
/* 2994 */       for (i = 0; i < len; i++) {
/* 2995 */         byte b = bytes[i];
/*      */ 
/* 2997 */         if (b < 0) {
/* 2998 */           int ch = b + 256;
/* 2999 */           buffer.append('%');
/* 3000 */           buffer.append(gHexChs[(ch >> 4)]);
/* 3001 */           buffer.append(gHexChs[(ch & 0xF)]);
/*      */         }
/* 3003 */         else if (gNeedEscaping[b] != 0) {
/* 3004 */           buffer.append('%');
/* 3005 */           buffer.append(gAfterEscaping1[b]);
/* 3006 */           buffer.append(gAfterEscaping2[b]);
/*      */         }
/*      */         else {
/* 3009 */           buffer.append((char)b);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3016 */     if (buffer.length() != len) {
/* 3017 */       return buffer.toString();
/*      */     }
/*      */ 
/* 3020 */     return href;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 2908 */     char[] escChs = { ' ', '<', '>', '"', '{', '}', '|', '\\', '^', '`' };
/* 2909 */     int len = escChs.length;
/*      */ 
/* 2911 */     for (int i = 0; i < len; i++) {
/* 2912 */       char ch = escChs[i];
/* 2913 */       gNeedEscaping[ch] = true;
/* 2914 */       gAfterEscaping1[ch] = gHexChs[(ch >> '\004')];
/* 2915 */       gAfterEscaping2[ch] = gHexChs[(ch & 0xF)];
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static class Notation
/*      */   {
/*      */     public String name;
/*      */     public String systemId;
/*      */     public String baseURI;
/*      */     public String publicId;
/*      */     public String expandedSystemId;
/*      */     public Augmentations augmentations;
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/* 2628 */       if (obj == null) {
/* 2629 */         return false;
/*      */       }
/* 2631 */       if ((obj instanceof Notation)) {
/* 2632 */         Notation other = (Notation)obj;
/* 2633 */         return this.name.equals(other.name);
/*      */       }
/* 2635 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isDuplicate(Object obj)
/*      */     {
/* 2646 */       if ((obj != null) && ((obj instanceof Notation))) {
/* 2647 */         Notation other = (Notation)obj;
/* 2648 */         return (this.name.equals(other.name)) && (isEqual(this.publicId, other.publicId)) && (isEqual(this.expandedSystemId, other.expandedSystemId));
/*      */       }
/*      */ 
/* 2652 */       return false;
/*      */     }
/*      */ 
/*      */     private boolean isEqual(String one, String two) {
/* 2656 */       return (one == two) || ((one != null) && (one.equals(two)));
/*      */     }
/*      */   }
/*      */   protected static class UnparsedEntity {
/*      */     public String name;
/*      */     public String systemId;
/*      */     public String baseURI;
/*      */     public String publicId;
/*      */     public String expandedSystemId;
/*      */     public String notation;
/*      */     public Augmentations augmentations;
/*      */ 
/* 2674 */     public boolean equals(Object obj) { if (obj == null) {
/* 2675 */         return false;
/*      */       }
/* 2677 */       if ((obj instanceof UnparsedEntity)) {
/* 2678 */         UnparsedEntity other = (UnparsedEntity)obj;
/* 2679 */         return this.name.equals(other.name);
/*      */       }
/* 2681 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isDuplicate(Object obj)
/*      */     {
/* 2692 */       if ((obj != null) && ((obj instanceof UnparsedEntity))) {
/* 2693 */         UnparsedEntity other = (UnparsedEntity)obj;
/* 2694 */         return (this.name.equals(other.name)) && (isEqual(this.publicId, other.publicId)) && (isEqual(this.expandedSystemId, other.expandedSystemId)) && (isEqual(this.notation, other.notation));
/*      */       }
/*      */ 
/* 2699 */       return false;
/*      */     }
/*      */ 
/*      */     private boolean isEqual(String one, String two) {
/* 2703 */       return (one == two) || ((one != null) && (one.equals(two)));
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler
 * JD-Core Version:    0.6.2
 */