/*     */ package com.sun.org.apache.xerces.internal.parsers;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLNamespaceBinder;
/*     */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
/*     */ import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
/*     */ import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
/*     */ import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*     */ import com.sun.org.apache.xerces.internal.util.FeatureState;
/*     */ import com.sun.org.apache.xerces.internal.util.PropertyState;
/*     */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLLocator;
/*     */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DTDConfiguration extends BasicParserConfiguration
/*     */   implements XMLPullParserConfiguration
/*     */ {
/*     */   protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
/*     */   protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
/*     */   protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
/*     */   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
/*     */   protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
/*     */   protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
/*     */   protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
/*     */   protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
/*     */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*     */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*     */   protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
/*     */   protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
/*     */   protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*     */   protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
/*     */   protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
/*     */   protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
/*     */   protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
/*     */   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*     */   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
/*     */   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
/*     */   protected static final String LOCALE = "http://apache.org/xml/properties/locale";
/*     */   protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
/*     */   protected XMLGrammarPool fGrammarPool;
/*     */   protected DTDDVFactory fDatatypeValidatorFactory;
/*     */   protected XMLErrorReporter fErrorReporter;
/*     */   protected XMLEntityManager fEntityManager;
/*     */   protected XMLDocumentScanner fScanner;
/*     */   protected XMLInputSource fInputSource;
/*     */   protected XMLDTDScanner fDTDScanner;
/*     */   protected XMLDTDProcessor fDTDProcessor;
/*     */   protected XMLDTDValidator fDTDValidator;
/*     */   protected XMLNamespaceBinder fNamespaceBinder;
/*     */   protected ValidationManager fValidationManager;
/*     */   protected XMLLocator fLocator;
/* 241 */   protected boolean fParseInProgress = false;
/*     */ 
/*     */   public DTDConfiguration()
/*     */   {
/* 249 */     this(null, null, null);
/*     */   }
/*     */ 
/*     */   public DTDConfiguration(SymbolTable symbolTable)
/*     */   {
/* 258 */     this(symbolTable, null, null);
/*     */   }
/*     */ 
/*     */   public DTDConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool)
/*     */   {
/* 274 */     this(symbolTable, grammarPool, null);
/*     */   }
/*     */ 
/*     */   public DTDConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings)
/*     */   {
/* 292 */     super(symbolTable, parentSettings);
/*     */ 
/* 295 */     String[] recognizedFeatures = { "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd" };
/*     */ 
/* 305 */     addRecognizedFeatures(recognizedFeatures);
/*     */ 
/* 311 */     setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
/* 312 */     setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
/*     */ 
/* 318 */     String[] recognizedProperties = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/locale" };
/*     */ 
/* 333 */     addRecognizedProperties(recognizedProperties);
/*     */ 
/* 335 */     this.fGrammarPool = grammarPool;
/* 336 */     if (this.fGrammarPool != null) {
/* 337 */       setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
/*     */     }
/*     */ 
/* 340 */     this.fEntityManager = createEntityManager();
/* 341 */     setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
/* 342 */     addComponent(this.fEntityManager);
/*     */ 
/* 344 */     this.fErrorReporter = createErrorReporter();
/* 345 */     this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
/* 346 */     setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/* 347 */     addComponent(this.fErrorReporter);
/*     */ 
/* 349 */     this.fScanner = createDocumentScanner();
/* 350 */     setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fScanner);
/* 351 */     if ((this.fScanner instanceof XMLComponent)) {
/* 352 */       addComponent((XMLComponent)this.fScanner);
/*     */     }
/*     */ 
/* 355 */     this.fDTDScanner = createDTDScanner();
/* 356 */     if (this.fDTDScanner != null) {
/* 357 */       setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
/* 358 */       if ((this.fDTDScanner instanceof XMLComponent)) {
/* 359 */         addComponent((XMLComponent)this.fDTDScanner);
/*     */       }
/*     */     }
/*     */ 
/* 363 */     this.fDTDProcessor = createDTDProcessor();
/* 364 */     if (this.fDTDProcessor != null) {
/* 365 */       setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
/* 366 */       if ((this.fDTDProcessor instanceof XMLComponent)) {
/* 367 */         addComponent(this.fDTDProcessor);
/*     */       }
/*     */     }
/*     */ 
/* 371 */     this.fDTDValidator = createDTDValidator();
/* 372 */     if (this.fDTDValidator != null) {
/* 373 */       setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
/* 374 */       addComponent(this.fDTDValidator);
/*     */     }
/*     */ 
/* 377 */     this.fNamespaceBinder = createNamespaceBinder();
/* 378 */     if (this.fNamespaceBinder != null) {
/* 379 */       setProperty("http://apache.org/xml/properties/internal/namespace-binder", this.fNamespaceBinder);
/* 380 */       addComponent(this.fNamespaceBinder);
/*     */     }
/*     */ 
/* 383 */     this.fDatatypeValidatorFactory = createDatatypeValidatorFactory();
/* 384 */     if (this.fDatatypeValidatorFactory != null) {
/* 385 */       setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
/*     */     }
/*     */ 
/* 388 */     this.fValidationManager = createValidationManager();
/*     */ 
/* 390 */     if (this.fValidationManager != null) {
/* 391 */       setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
/*     */     }
/*     */ 
/* 394 */     if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
/* 395 */       XMLMessageFormatter xmft = new XMLMessageFormatter();
/* 396 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
/* 397 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 402 */       setLocale(Locale.getDefault());
/*     */     }
/*     */     catch (XNIException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public PropertyState getPropertyState(String propertyId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 417 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/* 418 */       return PropertyState.is(getLocale());
/*     */     }
/* 420 */     return super.getPropertyState(propertyId);
/*     */   }
/*     */ 
/*     */   public void setProperty(String propertyId, Object value) throws XMLConfigurationException
/*     */   {
/* 425 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/* 426 */       setLocale((Locale)value);
/*     */     }
/* 428 */     super.setProperty(propertyId, value);
/*     */   }
/*     */ 
/*     */   public void setLocale(Locale locale)
/*     */     throws XNIException
/*     */   {
/* 440 */     super.setLocale(locale);
/* 441 */     this.fErrorReporter.setLocale(locale);
/*     */   }
/*     */ 
/*     */   public void setInputSource(XMLInputSource inputSource)
/*     */     throws XMLConfigurationException, IOException
/*     */   {
/* 470 */     this.fInputSource = inputSource;
/*     */   }
/*     */ 
/*     */   public boolean parse(boolean complete)
/*     */     throws XNIException, IOException
/*     */   {
/* 493 */     if (this.fInputSource != null) {
/*     */       try
/*     */       {
/* 496 */         reset();
/* 497 */         this.fScanner.setInputSource(this.fInputSource);
/* 498 */         this.fInputSource = null;
/*     */       }
/*     */       catch (XNIException ex)
/*     */       {
/* 503 */         throw ex;
/*     */       }
/*     */       catch (IOException ex)
/*     */       {
/* 508 */         throw ex;
/*     */       }
/*     */       catch (RuntimeException ex)
/*     */       {
/* 513 */         throw ex;
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/* 518 */         throw new XNIException(ex);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 523 */       return this.fScanner.scanDocument(complete);
/*     */     }
/*     */     catch (XNIException ex)
/*     */     {
/* 528 */       throw ex;
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 533 */       throw ex;
/*     */     }
/*     */     catch (RuntimeException ex)
/*     */     {
/* 538 */       throw ex;
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 543 */       throw new XNIException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cleanup()
/*     */   {
/* 554 */     this.fEntityManager.closeReaders();
/*     */   }
/*     */ 
/*     */   public void parse(XMLInputSource source)
/*     */     throws XNIException, IOException
/*     */   {
/* 571 */     if (this.fParseInProgress)
/*     */     {
/* 573 */       throw new XNIException("FWK005 parse may not be called while parsing.");
/*     */     }
/* 575 */     this.fParseInProgress = true;
/*     */     try
/*     */     {
/* 578 */       setInputSource(source);
/* 579 */       parse(true);
/*     */     }
/*     */     catch (XNIException ex)
/*     */     {
/* 584 */       throw ex;
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 589 */       throw ex;
/*     */     }
/*     */     catch (RuntimeException ex)
/*     */     {
/* 594 */       throw ex;
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 599 */       throw new XNIException(ex);
/*     */     }
/*     */     finally {
/* 602 */       this.fParseInProgress = false;
/*     */ 
/* 604 */       cleanup();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */     throws XNIException
/*     */   {
/* 620 */     if (this.fValidationManager != null) {
/* 621 */       this.fValidationManager.reset();
/*     */     }
/* 623 */     configurePipeline();
/* 624 */     super.reset();
/*     */   }
/*     */ 
/*     */   protected void configurePipeline()
/*     */   {
/* 638 */     if (this.fDTDValidator != null) {
/* 639 */       this.fScanner.setDocumentHandler(this.fDTDValidator);
/* 640 */       if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE)
/*     */       {
/* 643 */         this.fDTDValidator.setDocumentHandler(this.fNamespaceBinder);
/* 644 */         this.fDTDValidator.setDocumentSource(this.fScanner);
/* 645 */         this.fNamespaceBinder.setDocumentHandler(this.fDocumentHandler);
/* 646 */         this.fNamespaceBinder.setDocumentSource(this.fDTDValidator);
/* 647 */         this.fLastComponent = this.fNamespaceBinder;
/*     */       }
/*     */       else {
/* 650 */         this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
/* 651 */         this.fDTDValidator.setDocumentSource(this.fScanner);
/* 652 */         this.fLastComponent = this.fDTDValidator;
/*     */       }
/*     */ 
/*     */     }
/* 656 */     else if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
/* 657 */       this.fScanner.setDocumentHandler(this.fNamespaceBinder);
/* 658 */       this.fNamespaceBinder.setDocumentHandler(this.fDocumentHandler);
/* 659 */       this.fNamespaceBinder.setDocumentSource(this.fScanner);
/* 660 */       this.fLastComponent = this.fNamespaceBinder;
/*     */     }
/*     */     else {
/* 663 */       this.fScanner.setDocumentHandler(this.fDocumentHandler);
/* 664 */       this.fLastComponent = this.fScanner;
/*     */     }
/*     */ 
/* 668 */     configureDTDPipeline();
/*     */   }
/*     */ 
/*     */   protected void configureDTDPipeline()
/*     */   {
/* 674 */     if (this.fDTDScanner != null) {
/* 675 */       this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
/* 676 */       if (this.fDTDProcessor != null) {
/* 677 */         this.fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
/* 678 */         this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
/* 679 */         this.fDTDProcessor.setDTDSource(this.fDTDScanner);
/* 680 */         this.fDTDProcessor.setDTDHandler(this.fDTDHandler);
/* 681 */         if (this.fDTDHandler != null) {
/* 682 */           this.fDTDHandler.setDTDSource(this.fDTDProcessor);
/*     */         }
/*     */ 
/* 685 */         this.fDTDScanner.setDTDContentModelHandler(this.fDTDProcessor);
/* 686 */         this.fDTDProcessor.setDTDContentModelSource(this.fDTDScanner);
/* 687 */         this.fDTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
/* 688 */         if (this.fDTDContentModelHandler != null)
/* 689 */           this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDProcessor);
/*     */       }
/*     */       else
/*     */       {
/* 693 */         this.fDTDScanner.setDTDHandler(this.fDTDHandler);
/* 694 */         if (this.fDTDHandler != null) {
/* 695 */           this.fDTDHandler.setDTDSource(this.fDTDScanner);
/*     */         }
/* 697 */         this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
/* 698 */         if (this.fDTDContentModelHandler != null)
/* 699 */           this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDScanner);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected FeatureState checkFeature(String featureId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 728 */     if (featureId.startsWith("http://apache.org/xml/features/")) {
/* 729 */       int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
/*     */ 
/* 737 */       if ((suffixLength == "validation/dynamic".length()) && (featureId.endsWith("validation/dynamic")))
/*     */       {
/* 739 */         return FeatureState.RECOGNIZED;
/*     */       }
/*     */ 
/* 745 */       if ((suffixLength == "validation/default-attribute-values".length()) && (featureId.endsWith("validation/default-attribute-values")))
/*     */       {
/* 748 */         return FeatureState.NOT_SUPPORTED;
/*     */       }
/*     */ 
/* 753 */       if ((suffixLength == "validation/validate-content-models".length()) && (featureId.endsWith("validation/validate-content-models")))
/*     */       {
/* 756 */         return FeatureState.NOT_SUPPORTED;
/*     */       }
/*     */ 
/* 761 */       if ((suffixLength == "nonvalidating/load-dtd-grammar".length()) && (featureId.endsWith("nonvalidating/load-dtd-grammar")))
/*     */       {
/* 763 */         return FeatureState.RECOGNIZED;
/*     */       }
/*     */ 
/* 768 */       if ((suffixLength == "nonvalidating/load-external-dtd".length()) && (featureId.endsWith("nonvalidating/load-external-dtd")))
/*     */       {
/* 770 */         return FeatureState.RECOGNIZED;
/*     */       }
/*     */ 
/* 776 */       if ((suffixLength == "validation/validate-datatypes".length()) && (featureId.endsWith("validation/validate-datatypes")))
/*     */       {
/* 778 */         return FeatureState.NOT_SUPPORTED;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 786 */     return super.checkFeature(featureId);
/*     */   }
/*     */ 
/*     */   protected PropertyState checkProperty(String propertyId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 810 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/* 811 */       int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
/*     */ 
/* 813 */       if ((suffixLength == "internal/dtd-scanner".length()) && (propertyId.endsWith("internal/dtd-scanner")))
/*     */       {
/* 815 */         return PropertyState.RECOGNIZED;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 823 */     return super.checkProperty(propertyId);
/*     */   }
/*     */ 
/*     */   protected XMLEntityManager createEntityManager()
/*     */   {
/* 831 */     return new XMLEntityManager();
/*     */   }
/*     */ 
/*     */   protected XMLErrorReporter createErrorReporter()
/*     */   {
/* 836 */     return new XMLErrorReporter();
/*     */   }
/*     */ 
/*     */   protected XMLDocumentScanner createDocumentScanner()
/*     */   {
/* 841 */     return new XMLDocumentScannerImpl();
/*     */   }
/*     */ 
/*     */   protected XMLDTDScanner createDTDScanner()
/*     */   {
/* 846 */     return new XMLDTDScannerImpl();
/*     */   }
/*     */ 
/*     */   protected XMLDTDProcessor createDTDProcessor()
/*     */   {
/* 851 */     return new XMLDTDProcessor();
/*     */   }
/*     */ 
/*     */   protected XMLDTDValidator createDTDValidator()
/*     */   {
/* 856 */     return new XMLDTDValidator();
/*     */   }
/*     */ 
/*     */   protected XMLNamespaceBinder createNamespaceBinder()
/*     */   {
/* 861 */     return new XMLNamespaceBinder();
/*     */   }
/*     */ 
/*     */   protected DTDDVFactory createDatatypeValidatorFactory()
/*     */   {
/* 866 */     return DTDDVFactory.getInstance();
/*     */   }
/*     */   protected ValidationManager createValidationManager() {
/* 869 */     return new ValidationManager();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.DTDConfiguration
 * JD-Core Version:    0.6.2
 */