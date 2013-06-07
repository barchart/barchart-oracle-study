/*      */ package com.sun.org.apache.xerces.internal.parsers;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XML11DocumentScannerImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
/*      */ import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
/*      */ import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDValidator;
/*      */ import com.sun.org.apache.xerces.internal.impl.dtd.XML11NSDTDValidator;
/*      */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
/*      */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
/*      */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
/*      */ import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.util.FeatureState;
/*      */ import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
/*      */ import com.sun.org.apache.xerces.internal.util.PropertyState;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLLocator;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ 
/*      */ public class XML11Configuration extends ParserConfigurationSettings
/*      */   implements XMLPullParserConfiguration, XML11Configurable
/*      */ {
/*      */   protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
/*      */   protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
/*      */   protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
/*      */   protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
/*      */   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
/*      */   protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
/*      */   protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
/*      */   protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
/*      */   protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
/*      */   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
/*      */   protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
/*      */   protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
/*      */   protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
/*      */   protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
/*      */   protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
/*      */   protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
/*      */   protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
/*      */   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
/*      */   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
/*      */   protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
/*      */   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
/*      */   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
/*      */   protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
/*      */   protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
/*      */   protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
/*      */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
/*      */   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*      */   protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
/*      */   protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
/*      */   protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
/*      */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*      */   protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
/*      */   protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
/*      */   protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*      */   protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
/*      */   protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
/*      */   protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
/*      */   protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
/*      */   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*      */   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
/*      */   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
/*      */   protected static final String LOCALE = "http://apache.org/xml/properties/locale";
/*      */   protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
/*      */   protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
/*      */   protected SymbolTable fSymbolTable;
/*      */   protected XMLInputSource fInputSource;
/*      */   protected ValidationManager fValidationManager;
/*      */   protected XMLVersionDetector fVersionDetector;
/*      */   protected XMLLocator fLocator;
/*      */   protected Locale fLocale;
/*      */   protected ArrayList fComponents;
/*  297 */   protected ArrayList fXML11Components = null;
/*      */ 
/*  300 */   protected ArrayList fCommonComponents = null;
/*      */   protected XMLDocumentHandler fDocumentHandler;
/*      */   protected XMLDTDHandler fDTDHandler;
/*      */   protected XMLDTDContentModelHandler fDTDContentModelHandler;
/*      */   protected XMLDocumentSource fLastComponent;
/*  319 */   protected boolean fParseInProgress = false;
/*      */ 
/*  324 */   protected boolean fConfigUpdated = false;
/*      */   protected DTDDVFactory fDatatypeValidatorFactory;
/*      */   protected XMLNSDocumentScannerImpl fNamespaceScanner;
/*      */   protected XMLDocumentScannerImpl fNonNSScanner;
/*      */   protected XMLDTDValidator fDTDValidator;
/*      */   protected XMLDTDValidator fNonNSDTDValidator;
/*      */   protected XMLDTDScanner fDTDScanner;
/*      */   protected XMLDTDProcessor fDTDProcessor;
/*  351 */   protected DTDDVFactory fXML11DatatypeFactory = null;
/*      */ 
/*  354 */   protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
/*      */ 
/*  357 */   protected XML11DocumentScannerImpl fXML11DocScanner = null;
/*      */ 
/*  360 */   protected XML11NSDTDValidator fXML11NSDTDValidator = null;
/*      */ 
/*  363 */   protected XML11DTDValidator fXML11DTDValidator = null;
/*      */ 
/*  366 */   protected XML11DTDScannerImpl fXML11DTDScanner = null;
/*      */ 
/*  368 */   protected XML11DTDProcessor fXML11DTDProcessor = null;
/*      */   protected XMLGrammarPool fGrammarPool;
/*      */   protected XMLErrorReporter fErrorReporter;
/*      */   protected XMLEntityManager fEntityManager;
/*      */   protected XMLSchemaValidator fSchemaValidator;
/*      */   protected XMLDocumentScanner fCurrentScanner;
/*      */   protected DTDDVFactory fCurrentDVFactory;
/*      */   protected XMLDTDScanner fCurrentDTDScanner;
/*  394 */   private boolean f11Initialized = false;
/*      */ 
/*      */   public XML11Configuration()
/*      */   {
/*  402 */     this(null, null, null);
/*      */   }
/*      */ 
/*      */   public XML11Configuration(SymbolTable symbolTable)
/*      */   {
/*  411 */     this(symbolTable, null, null);
/*      */   }
/*      */ 
/*      */   public XML11Configuration(SymbolTable symbolTable, XMLGrammarPool grammarPool)
/*      */   {
/*  426 */     this(symbolTable, grammarPool, null);
/*      */   }
/*      */ 
/*      */   public XML11Configuration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings)
/*      */   {
/*  446 */     super(parentSettings);
/*      */ 
/*  450 */     this.fComponents = new ArrayList();
/*      */ 
/*  452 */     this.fXML11Components = new ArrayList();
/*      */ 
/*  454 */     this.fCommonComponents = new ArrayList();
/*      */ 
/*  457 */     this.fFeatures = new HashMap();
/*  458 */     this.fProperties = new HashMap();
/*      */ 
/*  461 */     String[] recognizedFeatures = { "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings" };
/*      */ 
/*  480 */     addRecognizedFeatures(recognizedFeatures);
/*      */ 
/*  482 */     this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
/*  483 */     this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
/*  484 */     this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
/*  485 */     this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
/*  486 */     this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
/*  487 */     this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
/*  488 */     this.fFeatures.put("http://apache.org/xml/features/validation/schema/element-default", Boolean.TRUE);
/*  489 */     this.fFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.TRUE);
/*  490 */     this.fFeatures.put("http://apache.org/xml/features/validation/schema/augment-psvi", Boolean.TRUE);
/*  491 */     this.fFeatures.put("http://apache.org/xml/features/generate-synthetic-annotations", Boolean.FALSE);
/*  492 */     this.fFeatures.put("http://apache.org/xml/features/validate-annotations", Boolean.FALSE);
/*  493 */     this.fFeatures.put("http://apache.org/xml/features/honour-all-schemaLocations", Boolean.FALSE);
/*  494 */     this.fFeatures.put("http://apache.org/xml/features/namespace-growth", Boolean.FALSE);
/*  495 */     this.fFeatures.put("http://apache.org/xml/features/internal/tolerate-duplicates", Boolean.FALSE);
/*  496 */     this.fFeatures.put("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", Boolean.FALSE);
/*  497 */     this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
/*      */ 
/*  500 */     String[] recognizedProperties = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/validator/schema", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory" };
/*      */ 
/*  527 */     addRecognizedProperties(recognizedProperties);
/*      */ 
/*  529 */     if (symbolTable == null) {
/*  530 */       symbolTable = new SymbolTable();
/*      */     }
/*  532 */     this.fSymbolTable = symbolTable;
/*  533 */     this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
/*      */ 
/*  535 */     this.fGrammarPool = grammarPool;
/*  536 */     if (this.fGrammarPool != null) {
/*  537 */       this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
/*      */     }
/*      */ 
/*  540 */     this.fEntityManager = new XMLEntityManager();
/*  541 */     this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
/*  542 */     addCommonComponent(this.fEntityManager);
/*      */ 
/*  544 */     this.fErrorReporter = new XMLErrorReporter();
/*  545 */     this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
/*  546 */     this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/*  547 */     addCommonComponent(this.fErrorReporter);
/*      */ 
/*  549 */     this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
/*  550 */     this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
/*  551 */     addComponent(this.fNamespaceScanner);
/*      */ 
/*  553 */     this.fDTDScanner = new XMLDTDScannerImpl();
/*  554 */     this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
/*  555 */     addComponent((XMLComponent)this.fDTDScanner);
/*      */ 
/*  557 */     this.fDTDProcessor = new XMLDTDProcessor();
/*  558 */     this.fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
/*  559 */     addComponent(this.fDTDProcessor);
/*      */ 
/*  561 */     this.fDTDValidator = new XMLNSDTDValidator();
/*  562 */     this.fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
/*  563 */     addComponent(this.fDTDValidator);
/*      */ 
/*  565 */     this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
/*  566 */     this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
/*      */ 
/*  568 */     this.fValidationManager = new ValidationManager();
/*  569 */     this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
/*      */ 
/*  571 */     this.fVersionDetector = new XMLVersionDetector();
/*      */ 
/*  574 */     if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
/*  575 */       XMLMessageFormatter xmft = new XMLMessageFormatter();
/*  576 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
/*  577 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  582 */       setLocale(Locale.getDefault());
/*      */     }
/*      */     catch (XNIException e)
/*      */     {
/*      */     }
/*      */ 
/*  588 */     this.fConfigUpdated = false;
/*      */   }
/*      */ 
/*      */   public void setInputSource(XMLInputSource inputSource)
/*      */     throws XMLConfigurationException, IOException
/*      */   {
/*  615 */     this.fInputSource = inputSource;
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale)
/*      */     throws XNIException
/*      */   {
/*  628 */     this.fLocale = locale;
/*  629 */     this.fErrorReporter.setLocale(locale);
/*      */   }
/*      */ 
/*      */   public void setDocumentHandler(XMLDocumentHandler documentHandler)
/*      */   {
/*  638 */     this.fDocumentHandler = documentHandler;
/*  639 */     if (this.fLastComponent != null) {
/*  640 */       this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
/*  641 */       if (this.fDocumentHandler != null)
/*  642 */         this.fDocumentHandler.setDocumentSource(this.fLastComponent);
/*      */     }
/*      */   }
/*      */ 
/*      */   public XMLDocumentHandler getDocumentHandler()
/*      */   {
/*  649 */     return this.fDocumentHandler;
/*      */   }
/*      */ 
/*      */   public void setDTDHandler(XMLDTDHandler dtdHandler)
/*      */   {
/*  658 */     this.fDTDHandler = dtdHandler;
/*      */   }
/*      */ 
/*      */   public XMLDTDHandler getDTDHandler()
/*      */   {
/*  663 */     return this.fDTDHandler;
/*      */   }
/*      */ 
/*      */   public void setDTDContentModelHandler(XMLDTDContentModelHandler handler)
/*      */   {
/*  672 */     this.fDTDContentModelHandler = handler;
/*      */   }
/*      */ 
/*      */   public XMLDTDContentModelHandler getDTDContentModelHandler()
/*      */   {
/*  677 */     return this.fDTDContentModelHandler;
/*      */   }
/*      */ 
/*      */   public void setEntityResolver(XMLEntityResolver resolver)
/*      */   {
/*  688 */     this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
/*      */   }
/*      */ 
/*      */   public XMLEntityResolver getEntityResolver()
/*      */   {
/*  699 */     return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
/*      */   }
/*      */ 
/*      */   public void setErrorHandler(XMLErrorHandler errorHandler)
/*      */   {
/*  721 */     this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
/*      */   }
/*      */ 
/*      */   public XMLErrorHandler getErrorHandler()
/*      */   {
/*  733 */     return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
/*      */   }
/*      */ 
/*      */   public void cleanup()
/*      */   {
/*  743 */     this.fEntityManager.closeReaders();
/*      */   }
/*      */ 
/*      */   public void parse(XMLInputSource source)
/*      */     throws XNIException, IOException
/*      */   {
/*  756 */     if (this.fParseInProgress)
/*      */     {
/*  758 */       throw new XNIException("FWK005 parse may not be called while parsing.");
/*      */     }
/*  760 */     this.fParseInProgress = true;
/*      */     try
/*      */     {
/*  763 */       setInputSource(source);
/*  764 */       parse(true);
/*      */     }
/*      */     catch (XNIException ex)
/*      */     {
/*  768 */       throw ex;
/*      */     }
/*      */     catch (IOException ex)
/*      */     {
/*  772 */       throw ex;
/*      */     }
/*      */     catch (RuntimeException ex)
/*      */     {
/*  776 */       throw ex;
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*  780 */       throw new XNIException(ex);
/*      */     } finally {
/*  782 */       this.fParseInProgress = false;
/*      */ 
/*  784 */       cleanup();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean parse(boolean complete)
/*      */     throws XNIException, IOException
/*      */   {
/*  792 */     if (this.fInputSource != null) {
/*      */       try {
/*  794 */         this.fValidationManager.reset();
/*  795 */         this.fVersionDetector.reset(this);
/*  796 */         this.fConfigUpdated = true;
/*  797 */         resetCommon();
/*      */ 
/*  799 */         short version = this.fVersionDetector.determineDocVersion(this.fInputSource);
/*  800 */         if (version == 2) {
/*  801 */           initXML11Components();
/*  802 */           configureXML11Pipeline();
/*  803 */           resetXML11();
/*      */         } else {
/*  805 */           configurePipeline();
/*  806 */           reset();
/*      */         }
/*      */ 
/*  810 */         this.fConfigUpdated = false;
/*      */ 
/*  813 */         this.fVersionDetector.startDocumentParsing((XMLEntityHandler)this.fCurrentScanner, version);
/*  814 */         this.fInputSource = null;
/*      */       }
/*      */       catch (XNIException ex)
/*      */       {
/*  818 */         throw ex;
/*      */       }
/*      */       catch (IOException ex)
/*      */       {
/*  822 */         throw ex;
/*      */       }
/*      */       catch (RuntimeException ex)
/*      */       {
/*  826 */         throw ex;
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/*  830 */         throw new XNIException(ex);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  835 */       return this.fCurrentScanner.scanDocument(complete);
/*      */     }
/*      */     catch (XNIException ex)
/*      */     {
/*  839 */       throw ex;
/*      */     }
/*      */     catch (IOException ex)
/*      */     {
/*  843 */       throw ex;
/*      */     }
/*      */     catch (RuntimeException ex)
/*      */     {
/*  847 */       throw ex;
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*  851 */       throw new XNIException(ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public FeatureState getFeatureState(String featureId)
/*      */     throws XMLConfigurationException
/*      */   {
/*  871 */     if (featureId.equals("http://apache.org/xml/features/internal/parser-settings")) {
/*  872 */       return FeatureState.is(this.fConfigUpdated);
/*      */     }
/*  874 */     return super.getFeatureState(featureId);
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/*  893 */     this.fConfigUpdated = true;
/*      */ 
/*  895 */     int count = this.fComponents.size();
/*  896 */     for (int i = 0; i < count; i++) {
/*  897 */       XMLComponent c = (XMLComponent)this.fComponents.get(i);
/*  898 */       c.setFeature(featureId, state);
/*      */     }
/*      */ 
/*  901 */     count = this.fCommonComponents.size();
/*  902 */     for (int i = 0; i < count; i++) {
/*  903 */       XMLComponent c = (XMLComponent)this.fCommonComponents.get(i);
/*  904 */       c.setFeature(featureId, state);
/*      */     }
/*      */ 
/*  908 */     count = this.fXML11Components.size();
/*  909 */     for (int i = 0; i < count; i++) {
/*  910 */       XMLComponent c = (XMLComponent)this.fXML11Components.get(i);
/*      */       try {
/*  912 */         c.setFeature(featureId, state);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/*  919 */     super.setFeature(featureId, state);
/*      */   }
/*      */ 
/*      */   public PropertyState getPropertyState(String propertyId)
/*      */     throws XMLConfigurationException
/*      */   {
/*  937 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/*  938 */       return PropertyState.is(getLocale());
/*      */     }
/*  940 */     return super.getPropertyState(propertyId);
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */     throws XMLConfigurationException
/*      */   {
/*  951 */     this.fConfigUpdated = true;
/*  952 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/*  953 */       setLocale((Locale)value);
/*      */     }
/*      */ 
/*  956 */     int count = this.fComponents.size();
/*  957 */     for (int i = 0; i < count; i++) {
/*  958 */       XMLComponent c = (XMLComponent)this.fComponents.get(i);
/*  959 */       c.setProperty(propertyId, value);
/*      */     }
/*      */ 
/*  962 */     count = this.fCommonComponents.size();
/*  963 */     for (int i = 0; i < count; i++) {
/*  964 */       XMLComponent c = (XMLComponent)this.fCommonComponents.get(i);
/*  965 */       c.setProperty(propertyId, value);
/*      */     }
/*      */ 
/*  968 */     count = this.fXML11Components.size();
/*  969 */     for (int i = 0; i < count; i++) {
/*  970 */       XMLComponent c = (XMLComponent)this.fXML11Components.get(i);
/*      */       try {
/*  972 */         c.setProperty(propertyId, value);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  980 */     super.setProperty(propertyId, value);
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  987 */     return this.fLocale;
/*      */   }
/*      */ 
/*      */   protected void reset()
/*      */     throws XNIException
/*      */   {
/*  994 */     int count = this.fComponents.size();
/*  995 */     for (int i = 0; i < count; i++) {
/*  996 */       XMLComponent c = (XMLComponent)this.fComponents.get(i);
/*  997 */       c.reset(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void resetCommon()
/*      */     throws XNIException
/*      */   {
/* 1007 */     int count = this.fCommonComponents.size();
/* 1008 */     for (int i = 0; i < count; i++) {
/* 1009 */       XMLComponent c = (XMLComponent)this.fCommonComponents.get(i);
/* 1010 */       c.reset(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void resetXML11()
/*      */     throws XNIException
/*      */   {
/* 1021 */     int count = this.fXML11Components.size();
/* 1022 */     for (int i = 0; i < count; i++) {
/* 1023 */       XMLComponent c = (XMLComponent)this.fXML11Components.get(i);
/* 1024 */       c.reset(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void configureXML11Pipeline()
/*      */   {
/* 1035 */     if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
/* 1036 */       this.fCurrentDVFactory = this.fXML11DatatypeFactory;
/* 1037 */       setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
/*      */     }
/* 1039 */     if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
/* 1040 */       this.fCurrentDTDScanner = this.fXML11DTDScanner;
/* 1041 */       setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
/* 1042 */       setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fXML11DTDProcessor);
/*      */     }
/*      */ 
/* 1045 */     this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
/* 1046 */     this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
/* 1047 */     this.fXML11DTDProcessor.setDTDHandler(this.fDTDHandler);
/* 1048 */     if (this.fDTDHandler != null) {
/* 1049 */       this.fDTDHandler.setDTDSource(this.fXML11DTDProcessor);
/*      */     }
/*      */ 
/* 1052 */     this.fXML11DTDScanner.setDTDContentModelHandler(this.fXML11DTDProcessor);
/* 1053 */     this.fXML11DTDProcessor.setDTDContentModelSource(this.fXML11DTDScanner);
/* 1054 */     this.fXML11DTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
/* 1055 */     if (this.fDTDContentModelHandler != null) {
/* 1056 */       this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDProcessor);
/*      */     }
/*      */ 
/* 1060 */     if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
/* 1061 */       if (this.fCurrentScanner != this.fXML11NSDocScanner) {
/* 1062 */         this.fCurrentScanner = this.fXML11NSDocScanner;
/* 1063 */         setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11NSDocScanner);
/* 1064 */         setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11NSDTDValidator);
/*      */       }
/*      */ 
/* 1067 */       this.fXML11NSDocScanner.setDTDValidator(this.fXML11NSDTDValidator);
/* 1068 */       this.fXML11NSDocScanner.setDocumentHandler(this.fXML11NSDTDValidator);
/* 1069 */       this.fXML11NSDTDValidator.setDocumentSource(this.fXML11NSDocScanner);
/* 1070 */       this.fXML11NSDTDValidator.setDocumentHandler(this.fDocumentHandler);
/*      */ 
/* 1072 */       if (this.fDocumentHandler != null) {
/* 1073 */         this.fDocumentHandler.setDocumentSource(this.fXML11NSDTDValidator);
/*      */       }
/* 1075 */       this.fLastComponent = this.fXML11NSDTDValidator;
/*      */     }
/*      */     else
/*      */     {
/* 1079 */       if (this.fXML11DocScanner == null)
/*      */       {
/* 1081 */         this.fXML11DocScanner = new XML11DocumentScannerImpl();
/* 1082 */         addXML11Component(this.fXML11DocScanner);
/* 1083 */         this.fXML11DTDValidator = new XML11DTDValidator();
/* 1084 */         addXML11Component(this.fXML11DTDValidator);
/*      */       }
/* 1086 */       if (this.fCurrentScanner != this.fXML11DocScanner) {
/* 1087 */         this.fCurrentScanner = this.fXML11DocScanner;
/* 1088 */         setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11DocScanner);
/* 1089 */         setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11DTDValidator);
/*      */       }
/* 1091 */       this.fXML11DocScanner.setDocumentHandler(this.fXML11DTDValidator);
/* 1092 */       this.fXML11DTDValidator.setDocumentSource(this.fXML11DocScanner);
/* 1093 */       this.fXML11DTDValidator.setDocumentHandler(this.fDocumentHandler);
/*      */ 
/* 1095 */       if (this.fDocumentHandler != null) {
/* 1096 */         this.fDocumentHandler.setDocumentSource(this.fXML11DTDValidator);
/*      */       }
/* 1098 */       this.fLastComponent = this.fXML11DTDValidator;
/*      */     }
/*      */ 
/* 1102 */     if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE)
/*      */     {
/* 1104 */       if (this.fSchemaValidator == null) {
/* 1105 */         this.fSchemaValidator = new XMLSchemaValidator();
/*      */ 
/* 1107 */         setProperty("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
/* 1108 */         addCommonComponent(this.fSchemaValidator);
/* 1109 */         this.fSchemaValidator.reset(this);
/*      */ 
/* 1111 */         if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
/* 1112 */           XSMessageFormatter xmft = new XSMessageFormatter();
/* 1113 */           this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
/*      */         }
/*      */       }
/*      */ 
/* 1117 */       this.fLastComponent.setDocumentHandler(this.fSchemaValidator);
/* 1118 */       this.fSchemaValidator.setDocumentSource(this.fLastComponent);
/* 1119 */       this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
/* 1120 */       if (this.fDocumentHandler != null) {
/* 1121 */         this.fDocumentHandler.setDocumentSource(this.fSchemaValidator);
/*      */       }
/* 1123 */       this.fLastComponent = this.fSchemaValidator;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void configurePipeline()
/*      */   {
/* 1130 */     if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
/* 1131 */       this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
/*      */ 
/* 1133 */       setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory);
/*      */     }
/*      */ 
/* 1137 */     if (this.fCurrentDTDScanner != this.fDTDScanner) {
/* 1138 */       this.fCurrentDTDScanner = this.fDTDScanner;
/* 1139 */       setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner);
/* 1140 */       setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
/*      */     }
/* 1142 */     this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
/* 1143 */     this.fDTDProcessor.setDTDSource(this.fDTDScanner);
/* 1144 */     this.fDTDProcessor.setDTDHandler(this.fDTDHandler);
/* 1145 */     if (this.fDTDHandler != null) {
/* 1146 */       this.fDTDHandler.setDTDSource(this.fDTDProcessor);
/*      */     }
/*      */ 
/* 1149 */     this.fDTDScanner.setDTDContentModelHandler(this.fDTDProcessor);
/* 1150 */     this.fDTDProcessor.setDTDContentModelSource(this.fDTDScanner);
/* 1151 */     this.fDTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
/* 1152 */     if (this.fDTDContentModelHandler != null) {
/* 1153 */       this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDProcessor);
/*      */     }
/*      */ 
/* 1157 */     if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
/* 1158 */       if (this.fCurrentScanner != this.fNamespaceScanner) {
/* 1159 */         this.fCurrentScanner = this.fNamespaceScanner;
/* 1160 */         setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
/* 1161 */         setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
/*      */       }
/* 1163 */       this.fNamespaceScanner.setDTDValidator(this.fDTDValidator);
/* 1164 */       this.fNamespaceScanner.setDocumentHandler(this.fDTDValidator);
/* 1165 */       this.fDTDValidator.setDocumentSource(this.fNamespaceScanner);
/* 1166 */       this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
/* 1167 */       if (this.fDocumentHandler != null) {
/* 1168 */         this.fDocumentHandler.setDocumentSource(this.fDTDValidator);
/*      */       }
/* 1170 */       this.fLastComponent = this.fDTDValidator;
/*      */     }
/*      */     else {
/* 1173 */       if (this.fNonNSScanner == null) {
/* 1174 */         this.fNonNSScanner = new XMLDocumentScannerImpl();
/* 1175 */         this.fNonNSDTDValidator = new XMLDTDValidator();
/*      */ 
/* 1177 */         addComponent(this.fNonNSScanner);
/* 1178 */         addComponent(this.fNonNSDTDValidator);
/*      */       }
/* 1180 */       if (this.fCurrentScanner != this.fNonNSScanner) {
/* 1181 */         this.fCurrentScanner = this.fNonNSScanner;
/* 1182 */         setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
/* 1183 */         setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fNonNSDTDValidator);
/*      */       }
/*      */ 
/* 1186 */       this.fNonNSScanner.setDocumentHandler(this.fNonNSDTDValidator);
/* 1187 */       this.fNonNSDTDValidator.setDocumentSource(this.fNonNSScanner);
/* 1188 */       this.fNonNSDTDValidator.setDocumentHandler(this.fDocumentHandler);
/* 1189 */       if (this.fDocumentHandler != null) {
/* 1190 */         this.fDocumentHandler.setDocumentSource(this.fNonNSDTDValidator);
/*      */       }
/* 1192 */       this.fLastComponent = this.fNonNSDTDValidator;
/*      */     }
/*      */ 
/* 1196 */     if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE)
/*      */     {
/* 1198 */       if (this.fSchemaValidator == null) {
/* 1199 */         this.fSchemaValidator = new XMLSchemaValidator();
/*      */ 
/* 1201 */         setProperty("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
/* 1202 */         addCommonComponent(this.fSchemaValidator);
/* 1203 */         this.fSchemaValidator.reset(this);
/*      */ 
/* 1205 */         if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
/* 1206 */           XSMessageFormatter xmft = new XSMessageFormatter();
/* 1207 */           this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
/*      */         }
/*      */       }
/*      */ 
/* 1211 */       this.fLastComponent.setDocumentHandler(this.fSchemaValidator);
/* 1212 */       this.fSchemaValidator.setDocumentSource(this.fLastComponent);
/* 1213 */       this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
/* 1214 */       if (this.fDocumentHandler != null) {
/* 1215 */         this.fDocumentHandler.setDocumentSource(this.fSchemaValidator);
/*      */       }
/* 1217 */       this.fLastComponent = this.fSchemaValidator;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected FeatureState checkFeature(String featureId)
/*      */     throws XMLConfigurationException
/*      */   {
/* 1242 */     if (featureId.startsWith("http://apache.org/xml/features/")) {
/* 1243 */       int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
/*      */ 
/* 1251 */       if ((suffixLength == "validation/dynamic".length()) && (featureId.endsWith("validation/dynamic")))
/*      */       {
/* 1253 */         return FeatureState.RECOGNIZED;
/*      */       }
/*      */ 
/* 1259 */       if ((suffixLength == "validation/default-attribute-values".length()) && (featureId.endsWith("validation/default-attribute-values")))
/*      */       {
/* 1262 */         return FeatureState.NOT_SUPPORTED;
/*      */       }
/*      */ 
/* 1267 */       if ((suffixLength == "validation/validate-content-models".length()) && (featureId.endsWith("validation/validate-content-models")))
/*      */       {
/* 1270 */         return FeatureState.NOT_SUPPORTED;
/*      */       }
/*      */ 
/* 1275 */       if ((suffixLength == "nonvalidating/load-dtd-grammar".length()) && (featureId.endsWith("nonvalidating/load-dtd-grammar")))
/*      */       {
/* 1277 */         return FeatureState.RECOGNIZED;
/*      */       }
/*      */ 
/* 1282 */       if ((suffixLength == "nonvalidating/load-external-dtd".length()) && (featureId.endsWith("nonvalidating/load-external-dtd")))
/*      */       {
/* 1284 */         return FeatureState.RECOGNIZED;
/*      */       }
/*      */ 
/* 1290 */       if ((suffixLength == "validation/validate-datatypes".length()) && (featureId.endsWith("validation/validate-datatypes")))
/*      */       {
/* 1292 */         return FeatureState.NOT_SUPPORTED;
/*      */       }
/*      */ 
/* 1299 */       if ((suffixLength == "validation/schema".length()) && (featureId.endsWith("validation/schema")))
/*      */       {
/* 1301 */         return FeatureState.RECOGNIZED;
/*      */       }
/*      */ 
/* 1304 */       if ((suffixLength == "validation/schema-full-checking".length()) && (featureId.endsWith("validation/schema-full-checking")))
/*      */       {
/* 1306 */         return FeatureState.RECOGNIZED;
/*      */       }
/*      */ 
/* 1310 */       if ((suffixLength == "validation/schema/normalized-value".length()) && (featureId.endsWith("validation/schema/normalized-value")))
/*      */       {
/* 1312 */         return FeatureState.RECOGNIZED;
/*      */       }
/*      */ 
/* 1316 */       if ((suffixLength == "validation/schema/element-default".length()) && (featureId.endsWith("validation/schema/element-default")))
/*      */       {
/* 1318 */         return FeatureState.RECOGNIZED;
/*      */       }
/*      */ 
/* 1322 */       if ((suffixLength == "internal/parser-settings".length()) && (featureId.endsWith("internal/parser-settings")))
/*      */       {
/* 1324 */         return FeatureState.NOT_SUPPORTED;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1333 */     return super.checkFeature(featureId);
/*      */   }
/*      */ 
/*      */   protected PropertyState checkProperty(String propertyId)
/*      */     throws XMLConfigurationException
/*      */   {
/* 1356 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/* 1357 */       int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
/*      */ 
/* 1359 */       if ((suffixLength == "internal/dtd-scanner".length()) && (propertyId.endsWith("internal/dtd-scanner")))
/*      */       {
/* 1361 */         return PropertyState.RECOGNIZED;
/*      */       }
/* 1363 */       if ((suffixLength == "schema/external-schemaLocation".length()) && (propertyId.endsWith("schema/external-schemaLocation")))
/*      */       {
/* 1365 */         return PropertyState.RECOGNIZED;
/*      */       }
/* 1367 */       if ((suffixLength == "schema/external-noNamespaceSchemaLocation".length()) && (propertyId.endsWith("schema/external-noNamespaceSchemaLocation")))
/*      */       {
/* 1369 */         return PropertyState.RECOGNIZED;
/*      */       }
/*      */     }
/*      */ 
/* 1373 */     if (propertyId.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
/* 1374 */       int suffixLength = propertyId.length() - "http://java.sun.com/xml/jaxp/properties/".length();
/*      */ 
/* 1376 */       if ((suffixLength == "schemaSource".length()) && (propertyId.endsWith("schemaSource")))
/*      */       {
/* 1378 */         return PropertyState.RECOGNIZED;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1383 */     if (propertyId.startsWith("http://xml.org/sax/properties/")) {
/* 1384 */       int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
/*      */ 
/* 1396 */       if ((suffixLength == "xml-string".length()) && (propertyId.endsWith("xml-string")))
/*      */       {
/* 1401 */         return PropertyState.NOT_SUPPORTED;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1409 */     return super.checkProperty(propertyId);
/*      */   }
/*      */ 
/*      */   protected void addComponent(XMLComponent component)
/*      */   {
/* 1424 */     if (this.fComponents.contains(component)) {
/* 1425 */       return;
/*      */     }
/* 1427 */     this.fComponents.add(component);
/* 1428 */     addRecognizedParamsAndSetDefaults(component);
/*      */   }
/*      */ 
/*      */   protected void addCommonComponent(XMLComponent component)
/*      */   {
/* 1442 */     if (this.fCommonComponents.contains(component)) {
/* 1443 */       return;
/*      */     }
/* 1445 */     this.fCommonComponents.add(component);
/* 1446 */     addRecognizedParamsAndSetDefaults(component);
/*      */   }
/*      */ 
/*      */   protected void addXML11Component(XMLComponent component)
/*      */   {
/* 1460 */     if (this.fXML11Components.contains(component)) {
/* 1461 */       return;
/*      */     }
/* 1463 */     this.fXML11Components.add(component);
/* 1464 */     addRecognizedParamsAndSetDefaults(component);
/*      */   }
/*      */ 
/*      */   protected void addRecognizedParamsAndSetDefaults(XMLComponent component)
/*      */   {
/* 1480 */     String[] recognizedFeatures = component.getRecognizedFeatures();
/* 1481 */     addRecognizedFeatures(recognizedFeatures);
/*      */ 
/* 1484 */     String[] recognizedProperties = component.getRecognizedProperties();
/* 1485 */     addRecognizedProperties(recognizedProperties);
/*      */ 
/* 1488 */     if (recognizedFeatures != null) {
/* 1489 */       for (int i = 0; i < recognizedFeatures.length; i++) {
/* 1490 */         String featureId = recognizedFeatures[i];
/* 1491 */         Boolean state = component.getFeatureDefault(featureId);
/* 1492 */         if (state != null)
/*      */         {
/* 1494 */           if (!this.fFeatures.containsKey(featureId)) {
/* 1495 */             this.fFeatures.put(featureId, state);
/*      */ 
/* 1500 */             this.fConfigUpdated = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1505 */     if (recognizedProperties != null)
/* 1506 */       for (int i = 0; i < recognizedProperties.length; i++) {
/* 1507 */         String propertyId = recognizedProperties[i];
/* 1508 */         Object value = component.getPropertyDefault(propertyId);
/* 1509 */         if (value != null)
/*      */         {
/* 1511 */           if (!this.fProperties.containsKey(propertyId)) {
/* 1512 */             this.fProperties.put(propertyId, value);
/*      */ 
/* 1517 */             this.fConfigUpdated = true;
/*      */           }
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   private void initXML11Components()
/*      */   {
/* 1525 */     if (!this.f11Initialized)
/*      */     {
/* 1528 */       this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
/*      */ 
/* 1531 */       this.fXML11DTDScanner = new XML11DTDScannerImpl();
/* 1532 */       addXML11Component(this.fXML11DTDScanner);
/* 1533 */       this.fXML11DTDProcessor = new XML11DTDProcessor();
/* 1534 */       addXML11Component(this.fXML11DTDProcessor);
/*      */ 
/* 1537 */       this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
/* 1538 */       addXML11Component(this.fXML11NSDocScanner);
/* 1539 */       this.fXML11NSDTDValidator = new XML11NSDTDValidator();
/* 1540 */       addXML11Component(this.fXML11NSDTDValidator);
/*      */ 
/* 1542 */       this.f11Initialized = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   FeatureState getFeatureState0(String featureId)
/*      */     throws XMLConfigurationException
/*      */   {
/* 1553 */     return super.getFeatureState(featureId);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.XML11Configuration
 * JD-Core Version:    0.6.2
 */