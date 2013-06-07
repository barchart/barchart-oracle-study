/*     */ package com.sun.org.apache.xerces.internal.parsers;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
/*     */ import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
/*     */ import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*     */ import com.sun.org.apache.xerces.internal.util.FeatureState;
/*     */ import com.sun.org.apache.xerces.internal.util.PropertyState;
/*     */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
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
/*     */ public class NonValidatingConfiguration extends BasicParserConfiguration
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
/*     */   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
/*     */   protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
/*     */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*     */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*     */   protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
/*     */   protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
/*     */   protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*     */   protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
/*     */   protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
/*     */   protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
/*     */   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*     */   protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
/*     */   protected static final String LOCALE = "http://apache.org/xml/properties/locale";
/*     */   private static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
/*     */   protected XMLGrammarPool fGrammarPool;
/*     */   protected DTDDVFactory fDatatypeValidatorFactory;
/*     */   protected XMLErrorReporter fErrorReporter;
/*     */   protected XMLEntityManager fEntityManager;
/*     */   protected XMLDocumentScanner fScanner;
/*     */   protected XMLInputSource fInputSource;
/*     */   protected XMLDTDScanner fDTDScanner;
/*     */   protected ValidationManager fValidationManager;
/*     */   private XMLNSDocumentScannerImpl fNamespaceScanner;
/*     */   private XMLDocumentScannerImpl fNonNSScanner;
/* 209 */   protected boolean fConfigUpdated = false;
/*     */   protected XMLLocator fLocator;
/* 222 */   protected boolean fParseInProgress = false;
/*     */ 
/*     */   public NonValidatingConfiguration()
/*     */   {
/* 230 */     this(null, null, null);
/*     */   }
/*     */ 
/*     */   public NonValidatingConfiguration(SymbolTable symbolTable)
/*     */   {
/* 239 */     this(symbolTable, null, null);
/*     */   }
/*     */ 
/*     */   public NonValidatingConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool)
/*     */   {
/* 255 */     this(symbolTable, grammarPool, null);
/*     */   }
/*     */ 
/*     */   public NonValidatingConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings)
/*     */   {
/* 273 */     super(symbolTable, parentSettings);
/*     */ 
/* 276 */     String[] recognizedFeatures = { "http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/continue-after-fatal-error" };
/*     */ 
/* 288 */     addRecognizedFeatures(recognizedFeatures);
/*     */ 
/* 294 */     this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
/* 295 */     this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
/* 296 */     this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
/*     */ 
/* 303 */     String[] recognizedProperties = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/locale" };
/*     */ 
/* 315 */     addRecognizedProperties(recognizedProperties);
/*     */ 
/* 317 */     this.fGrammarPool = grammarPool;
/* 318 */     if (this.fGrammarPool != null) {
/* 319 */       this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
/*     */     }
/*     */ 
/* 322 */     this.fEntityManager = createEntityManager();
/* 323 */     this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
/* 324 */     addComponent(this.fEntityManager);
/*     */ 
/* 326 */     this.fErrorReporter = createErrorReporter();
/* 327 */     this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
/* 328 */     this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/* 329 */     addComponent(this.fErrorReporter);
/*     */ 
/* 334 */     this.fDTDScanner = createDTDScanner();
/* 335 */     if (this.fDTDScanner != null) {
/* 336 */       this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
/* 337 */       if ((this.fDTDScanner instanceof XMLComponent)) {
/* 338 */         addComponent((XMLComponent)this.fDTDScanner);
/*     */       }
/*     */     }
/*     */ 
/* 342 */     this.fDatatypeValidatorFactory = createDatatypeValidatorFactory();
/* 343 */     if (this.fDatatypeValidatorFactory != null) {
/* 344 */       this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
/*     */     }
/*     */ 
/* 347 */     this.fValidationManager = createValidationManager();
/*     */ 
/* 349 */     if (this.fValidationManager != null) {
/* 350 */       this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
/*     */     }
/*     */ 
/* 353 */     if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
/* 354 */       XMLMessageFormatter xmft = new XMLMessageFormatter();
/* 355 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
/* 356 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
/*     */     }
/*     */ 
/* 359 */     this.fConfigUpdated = false;
/*     */     try
/*     */     {
/* 363 */       setLocale(Locale.getDefault());
/*     */     }
/*     */     catch (XNIException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFeature(String featureId, boolean state)
/*     */     throws XMLConfigurationException
/*     */   {
/* 377 */     this.fConfigUpdated = true;
/* 378 */     super.setFeature(featureId, state);
/*     */   }
/*     */ 
/*     */   public PropertyState getPropertyState(String propertyId) throws XMLConfigurationException
/*     */   {
/* 383 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/* 384 */       return PropertyState.is(getLocale());
/*     */     }
/* 386 */     return super.getPropertyState(propertyId);
/*     */   }
/*     */ 
/*     */   public void setProperty(String propertyId, Object value) throws XMLConfigurationException
/*     */   {
/* 391 */     this.fConfigUpdated = true;
/* 392 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/* 393 */       setLocale((Locale)value);
/*     */     }
/* 395 */     super.setProperty(propertyId, value);
/*     */   }
/*     */ 
/*     */   public void setLocale(Locale locale)
/*     */     throws XNIException
/*     */   {
/* 407 */     super.setLocale(locale);
/* 408 */     this.fErrorReporter.setLocale(locale);
/*     */   }
/*     */ 
/*     */   public FeatureState getFeatureState(String featureId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 414 */     if (featureId.equals("http://apache.org/xml/features/internal/parser-settings")) {
/* 415 */       return FeatureState.is(this.fConfigUpdated);
/*     */     }
/* 417 */     return super.getFeatureState(featureId);
/*     */   }
/*     */ 
/*     */   public void setInputSource(XMLInputSource inputSource)
/*     */     throws XMLConfigurationException, IOException
/*     */   {
/* 446 */     this.fInputSource = inputSource;
/*     */   }
/*     */ 
/*     */   public boolean parse(boolean complete)
/*     */     throws XNIException, IOException
/*     */   {
/* 469 */     if (this.fInputSource != null) {
/*     */       try
/*     */       {
/* 472 */         reset();
/* 473 */         this.fScanner.setInputSource(this.fInputSource);
/* 474 */         this.fInputSource = null;
/*     */       }
/*     */       catch (XNIException ex)
/*     */       {
/* 479 */         throw ex;
/*     */       }
/*     */       catch (IOException ex)
/*     */       {
/* 484 */         throw ex;
/*     */       }
/*     */       catch (RuntimeException ex)
/*     */       {
/* 489 */         throw ex;
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/* 494 */         throw new XNIException(ex);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 499 */       return this.fScanner.scanDocument(complete);
/*     */     }
/*     */     catch (XNIException ex)
/*     */     {
/* 504 */       throw ex;
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 509 */       throw ex;
/*     */     }
/*     */     catch (RuntimeException ex)
/*     */     {
/* 514 */       throw ex;
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 519 */       throw new XNIException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cleanup()
/*     */   {
/* 530 */     this.fEntityManager.closeReaders();
/*     */   }
/*     */ 
/*     */   public void parse(XMLInputSource source)
/*     */     throws XNIException, IOException
/*     */   {
/* 547 */     if (this.fParseInProgress)
/*     */     {
/* 549 */       throw new XNIException("FWK005 parse may not be called while parsing.");
/*     */     }
/* 551 */     this.fParseInProgress = true;
/*     */     try
/*     */     {
/* 554 */       setInputSource(source);
/* 555 */       parse(true);
/*     */     }
/*     */     catch (XNIException ex)
/*     */     {
/* 560 */       throw ex;
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 565 */       throw ex;
/*     */     }
/*     */     catch (RuntimeException ex)
/*     */     {
/* 570 */       throw ex;
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 575 */       throw new XNIException(ex);
/*     */     }
/*     */     finally {
/* 578 */       this.fParseInProgress = false;
/*     */ 
/* 580 */       cleanup();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */     throws XNIException
/*     */   {
/* 596 */     if (this.fValidationManager != null) {
/* 597 */       this.fValidationManager.reset();
/*     */     }
/* 599 */     configurePipeline();
/* 600 */     super.reset();
/*     */   }
/*     */ 
/*     */   protected void configurePipeline()
/*     */   {
/* 608 */     if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
/* 609 */       if (this.fNamespaceScanner == null) {
/* 610 */         this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
/* 611 */         addComponent(this.fNamespaceScanner);
/*     */       }
/* 613 */       this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
/* 614 */       this.fNamespaceScanner.setDTDValidator(null);
/* 615 */       this.fScanner = this.fNamespaceScanner;
/*     */     }
/*     */     else {
/* 618 */       if (this.fNonNSScanner == null) {
/* 619 */         this.fNonNSScanner = new XMLDocumentScannerImpl();
/* 620 */         addComponent(this.fNonNSScanner);
/*     */       }
/* 622 */       this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
/* 623 */       this.fScanner = this.fNonNSScanner;
/*     */     }
/*     */ 
/* 626 */     this.fScanner.setDocumentHandler(this.fDocumentHandler);
/* 627 */     this.fLastComponent = this.fScanner;
/*     */ 
/* 629 */     if (this.fDTDScanner != null) {
/* 630 */       this.fDTDScanner.setDTDHandler(this.fDTDHandler);
/* 631 */       this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected FeatureState checkFeature(String featureId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 658 */     if (featureId.startsWith("http://apache.org/xml/features/")) {
/* 659 */       int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
/*     */ 
/* 667 */       if ((suffixLength == "validation/dynamic".length()) && (featureId.endsWith("validation/dynamic")))
/*     */       {
/* 669 */         return FeatureState.RECOGNIZED;
/*     */       }
/*     */ 
/* 674 */       if ((suffixLength == "validation/default-attribute-values".length()) && (featureId.endsWith("validation/default-attribute-values")))
/*     */       {
/* 677 */         return FeatureState.NOT_SUPPORTED;
/*     */       }
/*     */ 
/* 682 */       if ((suffixLength == "validation/validate-content-models".length()) && (featureId.endsWith("validation/validate-content-models")))
/*     */       {
/* 685 */         return FeatureState.NOT_SUPPORTED;
/*     */       }
/*     */ 
/* 690 */       if ((suffixLength == "nonvalidating/load-dtd-grammar".length()) && (featureId.endsWith("nonvalidating/load-dtd-grammar")))
/*     */       {
/* 692 */         return FeatureState.RECOGNIZED;
/*     */       }
/*     */ 
/* 697 */       if ((suffixLength == "nonvalidating/load-external-dtd".length()) && (featureId.endsWith("nonvalidating/load-external-dtd")))
/*     */       {
/* 699 */         return FeatureState.RECOGNIZED;
/*     */       }
/*     */ 
/* 705 */       if ((suffixLength == "validation/validate-datatypes".length()) && (featureId.endsWith("validation/validate-datatypes")))
/*     */       {
/* 707 */         return FeatureState.NOT_SUPPORTED;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 715 */     return super.checkFeature(featureId);
/*     */   }
/*     */ 
/*     */   protected PropertyState checkProperty(String propertyId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 739 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/* 740 */       int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
/*     */ 
/* 742 */       if ((suffixLength == "internal/dtd-scanner".length()) && (propertyId.endsWith("internal/dtd-scanner")))
/*     */       {
/* 744 */         return PropertyState.RECOGNIZED;
/*     */       }
/*     */     }
/*     */ 
/* 748 */     if (propertyId.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
/* 749 */       int suffixLength = propertyId.length() - "http://java.sun.com/xml/jaxp/properties/".length();
/*     */ 
/* 751 */       if ((suffixLength == "schemaSource".length()) && (propertyId.endsWith("schemaSource")))
/*     */       {
/* 753 */         return PropertyState.RECOGNIZED;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 761 */     return super.checkProperty(propertyId);
/*     */   }
/*     */ 
/*     */   protected XMLEntityManager createEntityManager()
/*     */   {
/* 769 */     return new XMLEntityManager();
/*     */   }
/*     */ 
/*     */   protected XMLErrorReporter createErrorReporter()
/*     */   {
/* 774 */     return new XMLErrorReporter();
/*     */   }
/*     */ 
/*     */   protected XMLDocumentScanner createDocumentScanner()
/*     */   {
/* 779 */     return null;
/*     */   }
/*     */ 
/*     */   protected XMLDTDScanner createDTDScanner()
/*     */   {
/* 784 */     return new XMLDTDScannerImpl();
/*     */   }
/*     */ 
/*     */   protected DTDDVFactory createDatatypeValidatorFactory()
/*     */   {
/* 789 */     return DTDDVFactory.getInstance();
/*     */   }
/*     */   protected ValidationManager createValidationManager() {
/* 792 */     return new ValidationManager();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.NonValidatingConfiguration
 * JD-Core Version:    0.6.2
 */