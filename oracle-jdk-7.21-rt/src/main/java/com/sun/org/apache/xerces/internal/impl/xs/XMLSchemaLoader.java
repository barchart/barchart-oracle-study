/*      */ package com.sun.org.apache.xerces.internal.impl.xs;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.traversers.XSDHandler;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
/*      */ import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
/*      */ import com.sun.org.apache.xerces.internal.util.Status;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.org.apache.xerces.internal.xs.LSInputList;
/*      */ import com.sun.org.apache.xerces.internal.xs.StringList;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSLoader;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSModel;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import org.w3c.dom.DOMConfiguration;
/*      */ import org.w3c.dom.DOMErrorHandler;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.DOMStringList;
/*      */ import org.w3c.dom.ls.LSInput;
/*      */ import org.w3c.dom.ls.LSResourceResolver;
/*      */ import org.xml.sax.InputSource;
/*      */ 
/*      */ public class XMLSchemaLoader
/*      */   implements XMLGrammarLoader, XMLComponent, XSLoader, DOMConfiguration
/*      */ {
/*      */   protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
/*      */   protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
/*      */   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
/*      */   protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
/*      */   protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
/*      */   protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
/*      */   protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
/*      */   protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
/*      */   protected static final String AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
/*      */   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
/*      */   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
/*      */   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
/*      */   protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
/*      */   protected static final String USE_SERVICE_MECHANISM = "http://www.oracle.com/feature/use-service-mechanism";
/*  160 */   private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/disallow-doctype-decl", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://www.oracle.com/feature/use-service-mechanism" };
/*      */   public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
/*      */   public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*      */   public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*      */   protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
/*      */   protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
/*      */   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
/*      */   protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*      */   protected static final String LOCALE = "http://apache.org/xml/properties/locale";
/*      */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*  220 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory" };
/*      */ 
/*  238 */   private ParserConfigurationSettings fLoaderConfig = new ParserConfigurationSettings();
/*  239 */   private SymbolTable fSymbolTable = null;
/*  240 */   private XMLErrorReporter fErrorReporter = new XMLErrorReporter();
/*  241 */   private XMLEntityManager fEntityManager = null;
/*  242 */   private XMLEntityResolver fUserEntityResolver = null;
/*  243 */   private XMLGrammarPool fGrammarPool = null;
/*  244 */   private String fExternalSchemas = null;
/*  245 */   private String fExternalNoNSSchema = null;
/*      */ 
/*  247 */   private Object fJAXPSource = null;
/*      */ 
/*  249 */   private boolean fIsCheckedFully = false;
/*      */ 
/*  251 */   private boolean fJAXPProcessed = false;
/*      */ 
/*  253 */   private boolean fSettingsChanged = true;
/*      */   private XSDHandler fSchemaHandler;
/*      */   private XSGrammarBucket fGrammarBucket;
/*  258 */   private XSDeclarationPool fDeclPool = null;
/*      */   private SubstitutionGroupHandler fSubGroupHandler;
/*  260 */   private final CMNodeFactory fNodeFactory = new CMNodeFactory();
/*      */   private CMBuilder fCMBuilder;
/*  262 */   private XSDDescription fXSDDescription = new XSDDescription();
/*      */   private Map fJAXPCache;
/*  265 */   private Locale fLocale = Locale.getDefault();
/*      */ 
/*  268 */   private DOMStringList fRecognizedParameters = null;
/*      */ 
/*  271 */   private DOMErrorHandlerWrapper fErrorHandler = null;
/*      */ 
/*  274 */   private DOMEntityResolverWrapper fResourceResolver = null;
/*      */ 
/*      */   public XMLSchemaLoader()
/*      */   {
/*  278 */     this(new SymbolTable(), null, new XMLEntityManager(), null, null, null);
/*      */   }
/*      */ 
/*      */   public XMLSchemaLoader(SymbolTable symbolTable) {
/*  282 */     this(symbolTable, null, new XMLEntityManager(), null, null, null);
/*      */   }
/*      */ 
/*      */   XMLSchemaLoader(XMLErrorReporter errorReporter, XSGrammarBucket grammarBucket, SubstitutionGroupHandler sHandler, CMBuilder builder)
/*      */   {
/*  296 */     this(null, errorReporter, null, grammarBucket, sHandler, builder);
/*      */   }
/*      */ 
/*      */   XMLSchemaLoader(SymbolTable symbolTable, XMLErrorReporter errorReporter, XMLEntityManager entityResolver, XSGrammarBucket grammarBucket, SubstitutionGroupHandler sHandler, CMBuilder builder)
/*      */   {
/*  307 */     this.fLoaderConfig.addRecognizedFeatures(RECOGNIZED_FEATURES);
/*  308 */     this.fLoaderConfig.addRecognizedProperties(RECOGNIZED_PROPERTIES);
/*  309 */     if (symbolTable != null) {
/*  310 */       this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
/*      */     }
/*      */ 
/*  313 */     if (errorReporter == null) {
/*  314 */       errorReporter = new XMLErrorReporter();
/*  315 */       errorReporter.setLocale(this.fLocale);
/*  316 */       errorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", new DefaultErrorHandler());
/*      */     }
/*      */ 
/*  319 */     this.fErrorReporter = errorReporter;
/*      */ 
/*  321 */     if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
/*  322 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
/*      */     }
/*  324 */     this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/*  325 */     this.fEntityManager = entityResolver;
/*      */ 
/*  327 */     if (this.fEntityManager != null) {
/*  328 */       this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
/*      */     }
/*      */ 
/*  332 */     this.fLoaderConfig.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
/*      */ 
/*  334 */     if (grammarBucket == null) {
/*  335 */       grammarBucket = new XSGrammarBucket();
/*      */     }
/*  337 */     this.fGrammarBucket = grammarBucket;
/*  338 */     if (sHandler == null) {
/*  339 */       sHandler = new SubstitutionGroupHandler(this.fGrammarBucket);
/*      */     }
/*  341 */     this.fSubGroupHandler = sHandler;
/*      */ 
/*  343 */     if (builder == null) {
/*  344 */       builder = new CMBuilder(this.fNodeFactory);
/*      */     }
/*  346 */     this.fCMBuilder = builder;
/*  347 */     this.fSchemaHandler = new XSDHandler(this.fGrammarBucket);
/*  348 */     if (this.fDeclPool != null) {
/*  349 */       this.fDeclPool.reset();
/*      */     }
/*  351 */     this.fJAXPCache = new HashMap();
/*      */ 
/*  353 */     this.fSettingsChanged = true;
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedFeatures()
/*      */   {
/*  362 */     return (String[])RECOGNIZED_FEATURES.clone();
/*      */   }
/*      */ 
/*      */   public boolean getFeature(String featureId)
/*      */     throws XMLConfigurationException
/*      */   {
/*  374 */     return this.fLoaderConfig.getFeature(featureId);
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/*  388 */     this.fSettingsChanged = true;
/*  389 */     if (featureId.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
/*  390 */       this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", state);
/*      */     }
/*  392 */     else if (featureId.equals("http://apache.org/xml/features/generate-synthetic-annotations")) {
/*  393 */       this.fSchemaHandler.setGenerateSyntheticAnnotations(state);
/*      */     }
/*  395 */     this.fLoaderConfig.setFeature(featureId, state);
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedProperties()
/*      */   {
/*  404 */     return (String[])RECOGNIZED_PROPERTIES.clone();
/*      */   }
/*      */ 
/*      */   public Object getProperty(String propertyId)
/*      */     throws XMLConfigurationException
/*      */   {
/*  416 */     return this.fLoaderConfig.getProperty(propertyId);
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object state)
/*      */     throws XMLConfigurationException
/*      */   {
/*  430 */     this.fSettingsChanged = true;
/*  431 */     this.fLoaderConfig.setProperty(propertyId, state);
/*  432 */     if (propertyId.equals("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
/*  433 */       this.fJAXPSource = state;
/*  434 */       this.fJAXPProcessed = false;
/*      */     }
/*  436 */     else if (propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
/*  437 */       this.fGrammarPool = ((XMLGrammarPool)state);
/*      */     }
/*  439 */     else if (propertyId.equals("http://apache.org/xml/properties/schema/external-schemaLocation")) {
/*  440 */       this.fExternalSchemas = ((String)state);
/*      */     }
/*  442 */     else if (propertyId.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation")) {
/*  443 */       this.fExternalNoNSSchema = ((String)state);
/*      */     }
/*  445 */     else if (propertyId.equals("http://apache.org/xml/properties/locale")) {
/*  446 */       setLocale((Locale)state);
/*      */     }
/*  448 */     else if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
/*  449 */       this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", state);
/*      */     }
/*  451 */     else if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
/*  452 */       this.fErrorReporter = ((XMLErrorReporter)state);
/*  453 */       if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null)
/*  454 */         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale)
/*      */   {
/*  468 */     this.fLocale = locale;
/*  469 */     this.fErrorReporter.setLocale(locale);
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  474 */     return this.fLocale;
/*      */   }
/*      */ 
/*      */   public void setErrorHandler(XMLErrorHandler errorHandler)
/*      */   {
/*  483 */     this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", errorHandler);
/*      */   }
/*      */ 
/*      */   public XMLErrorHandler getErrorHandler()
/*      */   {
/*  488 */     return this.fErrorReporter.getErrorHandler();
/*      */   }
/*      */ 
/*      */   public void setEntityResolver(XMLEntityResolver entityResolver)
/*      */   {
/*  497 */     this.fUserEntityResolver = entityResolver;
/*  498 */     this.fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver);
/*  499 */     this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver);
/*      */   }
/*      */ 
/*      */   public XMLEntityResolver getEntityResolver()
/*      */   {
/*  504 */     return this.fUserEntityResolver;
/*      */   }
/*      */ 
/*      */   public void loadGrammar(XMLInputSource[] source)
/*      */     throws IOException, XNIException
/*      */   {
/*  519 */     int numSource = source.length;
/*  520 */     for (int i = 0; i < numSource; i++)
/*  521 */       loadGrammar(source[i]);
/*      */   }
/*      */ 
/*      */   public Grammar loadGrammar(XMLInputSource source)
/*      */     throws IOException, XNIException
/*      */   {
/*  542 */     reset(this.fLoaderConfig);
/*  543 */     this.fSettingsChanged = false;
/*  544 */     XSDDescription desc = new XSDDescription();
/*  545 */     desc.fContextType = 3;
/*  546 */     desc.setBaseSystemId(source.getBaseSystemId());
/*  547 */     desc.setLiteralSystemId(source.getSystemId());
/*      */ 
/*  549 */     Map locationPairs = new HashMap();
/*      */ 
/*  553 */     processExternalHints(this.fExternalSchemas, this.fExternalNoNSSchema, locationPairs, this.fErrorReporter);
/*      */ 
/*  555 */     SchemaGrammar grammar = loadSchema(desc, source, locationPairs);
/*      */ 
/*  557 */     if ((grammar != null) && (this.fGrammarPool != null)) {
/*  558 */       this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", this.fGrammarBucket.getGrammars());
/*      */ 
/*  561 */       if ((this.fIsCheckedFully) && (this.fJAXPCache.get(grammar) != grammar)) {
/*  562 */         XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
/*      */       }
/*      */     }
/*  565 */     return grammar;
/*      */   }
/*      */ 
/*      */   SchemaGrammar loadSchema(XSDDescription desc, XMLInputSource source, Map locationPairs)
/*      */     throws IOException, XNIException
/*      */   {
/*  585 */     if (!this.fJAXPProcessed) {
/*  586 */       processJAXPSchemaSource(locationPairs);
/*      */     }
/*  588 */     SchemaGrammar grammar = this.fSchemaHandler.parseSchema(source, desc, locationPairs);
/*      */ 
/*  590 */     return grammar;
/*      */   }
/*      */ 
/*      */   public static XMLInputSource resolveDocument(XSDDescription desc, Map locationPairs, XMLEntityResolver entityResolver)
/*      */     throws IOException
/*      */   {
/*  607 */     String loc = null;
/*      */ 
/*  609 */     if ((desc.getContextType() == 2) || (desc.fromInstance()))
/*      */     {
/*  612 */       String namespace = desc.getTargetNamespace();
/*  613 */       String ns = namespace == null ? XMLSymbols.EMPTY_STRING : namespace;
/*      */ 
/*  615 */       LocationArray tempLA = (LocationArray)locationPairs.get(ns);
/*  616 */       if (tempLA != null) {
/*  617 */         loc = tempLA.getFirstLocation();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  622 */     if (loc == null) {
/*  623 */       String[] hints = desc.getLocationHints();
/*  624 */       if ((hints != null) && (hints.length > 0)) {
/*  625 */         loc = hints[0];
/*      */       }
/*      */     }
/*  628 */     String expandedLoc = XMLEntityManager.expandSystemId(loc, desc.getBaseSystemId(), false);
/*  629 */     desc.setLiteralSystemId(loc);
/*  630 */     desc.setExpandedSystemId(expandedLoc);
/*  631 */     return entityResolver.resolveEntity(desc);
/*      */   }
/*      */ 
/*      */   public static void processExternalHints(String sl, String nsl, Map locations, XMLErrorReporter er)
/*      */   {
/*  638 */     if (sl != null)
/*      */     {
/*      */       try
/*      */       {
/*  643 */         XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
/*      */ 
/*  645 */         attrDecl.fType.validate(sl, null, null);
/*  646 */         if (!tokenizeSchemaLocationStr(sl, locations))
/*      */         {
/*  648 */           er.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { sl }, (short)0);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (InvalidDatatypeValueException ex)
/*      */       {
/*  656 */         er.reportError("http://www.w3.org/TR/xml-schema-1", ex.getKey(), ex.getArgs(), (short)0);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  662 */     if (nsl != null)
/*      */       try
/*      */       {
/*  665 */         XSAttributeDecl attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
/*  666 */         attrDecl.fType.validate(nsl, null, null);
/*  667 */         LocationArray la = (LocationArray)locations.get(XMLSymbols.EMPTY_STRING);
/*  668 */         if (la == null) {
/*  669 */           la = new LocationArray();
/*  670 */           locations.put(XMLSymbols.EMPTY_STRING, la);
/*      */         }
/*  672 */         la.addLocation(nsl);
/*      */       }
/*      */       catch (InvalidDatatypeValueException ex)
/*      */       {
/*  676 */         er.reportError("http://www.w3.org/TR/xml-schema-1", ex.getKey(), ex.getArgs(), (short)0);
/*      */       }
/*      */   }
/*      */ 
/*      */   public static boolean tokenizeSchemaLocationStr(String schemaStr, Map locations)
/*      */   {
/*  690 */     if (schemaStr != null) {
/*  691 */       StringTokenizer t = new StringTokenizer(schemaStr, " \n\t\r");
/*      */ 
/*  693 */       while (t.hasMoreTokens()) {
/*  694 */         String namespace = t.nextToken();
/*  695 */         if (!t.hasMoreTokens()) {
/*  696 */           return false;
/*      */         }
/*  698 */         String location = t.nextToken();
/*  699 */         LocationArray la = (LocationArray)locations.get(namespace);
/*  700 */         if (la == null) {
/*  701 */           la = new LocationArray();
/*  702 */           locations.put(namespace, la);
/*      */         }
/*  704 */         la.addLocation(location);
/*      */       }
/*      */     }
/*  707 */     return true;
/*      */   }
/*      */ 
/*      */   private void processJAXPSchemaSource(Map locationPairs)
/*      */     throws IOException
/*      */   {
/*  721 */     this.fJAXPProcessed = true;
/*  722 */     if (this.fJAXPSource == null) {
/*  723 */       return;
/*      */     }
/*      */ 
/*  726 */     Class componentType = this.fJAXPSource.getClass().getComponentType();
/*  727 */     XMLInputSource xis = null;
/*  728 */     String sid = null;
/*  729 */     if (componentType == null)
/*      */     {
/*  731 */       if (((this.fJAXPSource instanceof InputStream)) || ((this.fJAXPSource instanceof InputSource)))
/*      */       {
/*  733 */         SchemaGrammar g = (SchemaGrammar)this.fJAXPCache.get(this.fJAXPSource);
/*  734 */         if (g != null) {
/*  735 */           this.fGrammarBucket.putGrammar(g);
/*  736 */           return;
/*      */         }
/*      */       }
/*  739 */       this.fXSDDescription.reset();
/*  740 */       xis = xsdToXMLInputSource(this.fJAXPSource);
/*  741 */       sid = xis.getSystemId();
/*  742 */       this.fXSDDescription.fContextType = 3;
/*  743 */       if (sid != null) {
/*  744 */         this.fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
/*  745 */         this.fXSDDescription.setLiteralSystemId(sid);
/*  746 */         this.fXSDDescription.setExpandedSystemId(sid);
/*  747 */         this.fXSDDescription.fLocationHints = new String[] { sid };
/*      */       }
/*  749 */       SchemaGrammar g = loadSchema(this.fXSDDescription, xis, locationPairs);
/*      */ 
/*  751 */       if (g != null) {
/*  752 */         if (((this.fJAXPSource instanceof InputStream)) || ((this.fJAXPSource instanceof InputSource)))
/*      */         {
/*  754 */           this.fJAXPCache.put(this.fJAXPSource, g);
/*  755 */           if (this.fIsCheckedFully) {
/*  756 */             XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
/*      */           }
/*      */         }
/*  759 */         this.fGrammarBucket.putGrammar(g);
/*      */       }
/*  761 */       return;
/*  762 */     }if ((componentType != Object.class) && (componentType != String.class) && (componentType != File.class) && (componentType != InputStream.class) && (componentType != InputSource.class))
/*      */     {
/*  769 */       throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have an array of type {" + componentType.getName() + "}. Possible types of the array supported are Object, String, File, " + "InputStream, InputSource.");
/*      */     }
/*      */ 
/*  778 */     Object[] objArr = (Object[])this.fJAXPSource;
/*      */ 
/*  780 */     Vector jaxpSchemaSourceNamespaces = new Vector();
/*  781 */     for (int i = 0; i < objArr.length; i++) {
/*  782 */       if (((objArr[i] instanceof InputStream)) || ((objArr[i] instanceof InputSource)))
/*      */       {
/*  784 */         SchemaGrammar g = (SchemaGrammar)this.fJAXPCache.get(objArr[i]);
/*  785 */         if (g != null) {
/*  786 */           this.fGrammarBucket.putGrammar(g);
/*  787 */           continue;
/*      */         }
/*      */       }
/*  790 */       this.fXSDDescription.reset();
/*  791 */       xis = xsdToXMLInputSource(objArr[i]);
/*  792 */       sid = xis.getSystemId();
/*  793 */       this.fXSDDescription.fContextType = 3;
/*  794 */       if (sid != null) {
/*  795 */         this.fXSDDescription.setBaseSystemId(xis.getBaseSystemId());
/*  796 */         this.fXSDDescription.setLiteralSystemId(sid);
/*  797 */         this.fXSDDescription.setExpandedSystemId(sid);
/*  798 */         this.fXSDDescription.fLocationHints = new String[] { sid };
/*      */       }
/*  800 */       String targetNamespace = null;
/*      */ 
/*  802 */       SchemaGrammar grammar = this.fSchemaHandler.parseSchema(xis, this.fXSDDescription, locationPairs);
/*      */ 
/*  804 */       if (this.fIsCheckedFully) {
/*  805 */         XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
/*      */       }
/*  807 */       if (grammar != null) {
/*  808 */         targetNamespace = grammar.getTargetNamespace();
/*  809 */         if (jaxpSchemaSourceNamespaces.contains(targetNamespace))
/*      */         {
/*  811 */           throw new IllegalArgumentException(" When using array of Objects as the value of SCHEMA_SOURCE property , no two Schemas should share the same targetNamespace. ");
/*      */         }
/*      */ 
/*  816 */         jaxpSchemaSourceNamespaces.add(targetNamespace);
/*      */ 
/*  818 */         if (((objArr[i] instanceof InputStream)) || ((objArr[i] instanceof InputSource)))
/*      */         {
/*  820 */           this.fJAXPCache.put(objArr[i], grammar);
/*      */         }
/*  822 */         this.fGrammarBucket.putGrammar(grammar);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private XMLInputSource xsdToXMLInputSource(Object val)
/*      */   {
/*  833 */     if ((val instanceof String))
/*      */     {
/*  836 */       String loc = (String)val;
/*  837 */       this.fXSDDescription.reset();
/*  838 */       this.fXSDDescription.setValues(null, loc, null, null);
/*  839 */       XMLInputSource xis = null;
/*      */       try {
/*  841 */         xis = this.fEntityManager.resolveEntity(this.fXSDDescription);
/*      */       } catch (IOException ex) {
/*  843 */         this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { loc }, (short)1);
/*      */       }
/*      */ 
/*  847 */       if (xis == null)
/*      */       {
/*  850 */         return new XMLInputSource(null, loc, null);
/*      */       }
/*  852 */       return xis;
/*  853 */     }if ((val instanceof InputSource))
/*  854 */       return saxToXMLInputSource((InputSource)val);
/*  855 */     if ((val instanceof InputStream)) {
/*  856 */       return new XMLInputSource(null, null, null, (InputStream)val, null);
/*      */     }
/*  858 */     if ((val instanceof File)) {
/*  859 */       File file = (File)val;
/*  860 */       InputStream is = null;
/*      */       try {
/*  862 */         is = new BufferedInputStream(new FileInputStream(file));
/*      */       } catch (FileNotFoundException ex) {
/*  864 */         this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { file.toString() }, (short)1);
/*      */       }
/*      */ 
/*  868 */       return new XMLInputSource(null, null, null, is, null);
/*      */     }
/*  870 */     throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have a value of type {" + val.getClass().getName() + "}. Possible types of the value supported are String, File, InputStream, " + "InputSource OR an array of these types.");
/*      */   }
/*      */ 
/*      */   private static XMLInputSource saxToXMLInputSource(InputSource sis)
/*      */   {
/*  881 */     String publicId = sis.getPublicId();
/*  882 */     String systemId = sis.getSystemId();
/*      */ 
/*  884 */     Reader charStream = sis.getCharacterStream();
/*  885 */     if (charStream != null) {
/*  886 */       return new XMLInputSource(publicId, systemId, null, charStream, null);
/*      */     }
/*      */ 
/*  890 */     InputStream byteStream = sis.getByteStream();
/*  891 */     if (byteStream != null) {
/*  892 */       return new XMLInputSource(publicId, systemId, null, byteStream, sis.getEncoding());
/*      */     }
/*      */ 
/*  896 */     return new XMLInputSource(publicId, systemId, null);
/*      */   }
/*      */ 
/*      */   public Boolean getFeatureDefault(String featureId)
/*      */   {
/*  939 */     if (featureId.equals("http://apache.org/xml/features/validation/schema/augment-psvi")) {
/*  940 */       return Boolean.TRUE;
/*      */     }
/*  942 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getPropertyDefault(String propertyId)
/*      */   {
/*  950 */     return null;
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/*  958 */     this.fGrammarBucket.reset();
/*      */ 
/*  960 */     this.fSubGroupHandler.reset();
/*      */ 
/*  962 */     boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
/*      */ 
/*  964 */     if ((!parser_settings) || (!this.fSettingsChanged))
/*      */     {
/*  966 */       this.fJAXPProcessed = false;
/*      */ 
/*  968 */       initGrammarBucket();
/*  969 */       return;
/*      */     }
/*      */ 
/*  973 */     this.fNodeFactory.reset(componentManager);
/*      */ 
/*  978 */     this.fEntityManager = ((XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
/*      */ 
/*  981 */     this.fErrorReporter = ((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*      */ 
/*  984 */     SchemaDVFactory dvFactory = null;
/*  985 */     dvFactory = this.fSchemaHandler.getDVFactory();
/*  986 */     if (dvFactory == null) {
/*  987 */       dvFactory = SchemaDVFactory.getInstance();
/*  988 */       this.fSchemaHandler.setDVFactory(dvFactory);
/*      */     }
/*      */ 
/*  991 */     boolean psvi = componentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", false);
/*      */ 
/*  993 */     if (!psvi) {
/*  994 */       if (this.fDeclPool != null) {
/*  995 */         this.fDeclPool.reset();
/*      */       }
/*      */       else {
/*  998 */         this.fDeclPool = new XSDeclarationPool();
/*      */       }
/* 1000 */       this.fCMBuilder.setDeclPool(this.fDeclPool);
/* 1001 */       this.fSchemaHandler.setDeclPool(this.fDeclPool);
/* 1002 */       if ((dvFactory instanceof SchemaDVFactoryImpl)) {
/* 1003 */         this.fDeclPool.setDVFactory((SchemaDVFactoryImpl)dvFactory);
/* 1004 */         ((SchemaDVFactoryImpl)dvFactory).setDeclPool(this.fDeclPool);
/*      */       }
/*      */     } else {
/* 1007 */       this.fCMBuilder.setDeclPool(null);
/* 1008 */       this.fSchemaHandler.setDeclPool(null);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1013 */       this.fExternalSchemas = ((String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation"));
/* 1014 */       this.fExternalNoNSSchema = ((String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation"));
/*      */     } catch (XMLConfigurationException e) {
/* 1016 */       this.fExternalSchemas = null;
/* 1017 */       this.fExternalNoNSSchema = null;
/*      */     }
/*      */ 
/* 1021 */     this.fJAXPSource = componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
/* 1022 */     this.fJAXPProcessed = false;
/*      */ 
/* 1025 */     this.fGrammarPool = ((XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null));
/* 1026 */     initGrammarBucket();
/*      */     try
/*      */     {
/* 1029 */       boolean fatalError = componentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
/* 1030 */       if (!fatalError)
/* 1031 */         this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", fatalError);
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/* 1036 */     this.fIsCheckedFully = componentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
/*      */ 
/* 1039 */     this.fSchemaHandler.setGenerateSyntheticAnnotations(componentManager.getFeature("http://apache.org/xml/features/generate-synthetic-annotations", false));
/* 1040 */     this.fSchemaHandler.reset(componentManager);
/*      */   }
/*      */ 
/*      */   private void initGrammarBucket() {
/* 1044 */     if (this.fGrammarPool != null) {
/* 1045 */       Grammar[] initialGrammars = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
/* 1046 */       for (int i = 0; i < initialGrammars.length; i++)
/*      */       {
/* 1049 */         if (!this.fGrammarBucket.putGrammar((SchemaGrammar)initialGrammars[i], true))
/*      */         {
/* 1052 */           this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public DOMConfiguration getConfig()
/*      */   {
/* 1065 */     return this;
/*      */   }
/*      */ 
/*      */   public XSModel load(LSInput is)
/*      */   {
/*      */     try
/*      */     {
/* 1073 */       Grammar g = loadGrammar(dom2xmlInputSource(is));
/* 1074 */       return ((XSGrammar)g).toXSModel();
/*      */     } catch (Exception e) {
/* 1076 */       reportDOMFatalError(e);
/* 1077 */     }return null;
/*      */   }
/*      */ 
/*      */   public XSModel loadInputList(LSInputList is)
/*      */   {
/* 1085 */     int length = is.getLength();
/* 1086 */     SchemaGrammar[] gs = new SchemaGrammar[length];
/* 1087 */     for (int i = 0; i < length; i++) {
/*      */       try {
/* 1089 */         gs[i] = ((SchemaGrammar)loadGrammar(dom2xmlInputSource(is.item(i))));
/*      */       } catch (Exception e) {
/* 1091 */         reportDOMFatalError(e);
/* 1092 */         return null;
/*      */       }
/*      */     }
/* 1095 */     return new XSModelImpl(gs);
/*      */   }
/*      */ 
/*      */   public XSModel loadURI(String uri)
/*      */   {
/*      */     try
/*      */     {
/* 1103 */       Grammar g = loadGrammar(new XMLInputSource(null, uri, null));
/* 1104 */       return ((XSGrammar)g).toXSModel();
/*      */     }
/*      */     catch (Exception e) {
/* 1107 */       reportDOMFatalError(e);
/* 1108 */     }return null;
/*      */   }
/*      */ 
/*      */   public XSModel loadURIList(StringList uriList)
/*      */   {
/* 1116 */     int length = uriList.getLength();
/* 1117 */     SchemaGrammar[] gs = new SchemaGrammar[length];
/* 1118 */     for (int i = 0; i < length; i++) {
/*      */       try {
/* 1120 */         gs[i] = ((SchemaGrammar)loadGrammar(new XMLInputSource(null, uriList.item(i), null)));
/*      */       }
/*      */       catch (Exception e) {
/* 1123 */         reportDOMFatalError(e);
/* 1124 */         return null;
/*      */       }
/*      */     }
/* 1127 */     return new XSModelImpl(gs);
/*      */   }
/*      */ 
/*      */   void reportDOMFatalError(Exception e) {
/* 1131 */     if (this.fErrorHandler != null) {
/* 1132 */       DOMErrorImpl error = new DOMErrorImpl();
/* 1133 */       error.fException = e;
/* 1134 */       error.fMessage = e.getMessage();
/* 1135 */       error.fSeverity = 3;
/* 1136 */       this.fErrorHandler.getErrorHandler().handleError(error);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean canSetParameter(String name, Object value)
/*      */   {
/* 1144 */     if ((value instanceof Boolean)) {
/* 1145 */       if ((name.equals("validate")) || (name.equals("http://apache.org/xml/features/validation/schema-full-checking")) || (name.equals("http://apache.org/xml/features/validate-annotations")) || (name.equals("http://apache.org/xml/features/continue-after-fatal-error")) || (name.equals("http://apache.org/xml/features/allow-java-encodings")) || (name.equals("http://apache.org/xml/features/standard-uri-conformant")) || (name.equals("http://apache.org/xml/features/generate-synthetic-annotations")) || (name.equals("http://apache.org/xml/features/honour-all-schemaLocations")) || (name.equals("http://apache.org/xml/features/namespace-growth")) || (name.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) || (name.equals("http://www.oracle.com/feature/use-service-mechanism")))
/*      */       {
/* 1156 */         return true;
/*      */       }
/*      */ 
/* 1159 */       return false;
/*      */     }
/* 1161 */     if ((name.equals("error-handler")) || (name.equals("resource-resolver")) || (name.equals("http://apache.org/xml/properties/internal/symbol-table")) || (name.equals("http://apache.org/xml/properties/internal/error-reporter")) || (name.equals("http://apache.org/xml/properties/internal/error-handler")) || (name.equals("http://apache.org/xml/properties/internal/entity-resolver")) || (name.equals("http://apache.org/xml/properties/internal/grammar-pool")) || (name.equals("http://apache.org/xml/properties/schema/external-schemaLocation")) || (name.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation")) || (name.equals("http://java.sun.com/xml/jaxp/properties/schemaSource")) || (name.equals("http://apache.org/xml/properties/internal/validation/schema/dv-factory")))
/*      */     {
/* 1172 */       return true;
/*      */     }
/* 1174 */     return false;
/*      */   }
/*      */ 
/*      */   public Object getParameter(String name)
/*      */     throws DOMException
/*      */   {
/* 1182 */     if (name.equals("error-handler")) {
/* 1183 */       return this.fErrorHandler != null ? this.fErrorHandler.getErrorHandler() : null;
/*      */     }
/* 1185 */     if (name.equals("resource-resolver")) {
/* 1186 */       return this.fResourceResolver != null ? this.fResourceResolver.getEntityResolver() : null;
/*      */     }
/*      */     try
/*      */     {
/* 1190 */       boolean feature = getFeature(name);
/* 1191 */       return feature ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*      */     catch (Exception e) {
/*      */       try {
/* 1195 */         return getProperty(name);
/*      */       }
/*      */       catch (Exception ex) {
/* 1198 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/* 1203 */         throw new DOMException((short)9, msg);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public DOMStringList getParameterNames()
/*      */   {
/* 1212 */     if (this.fRecognizedParameters == null) {
/* 1213 */       Vector v = new Vector();
/* 1214 */       v.add("validate");
/* 1215 */       v.add("error-handler");
/* 1216 */       v.add("resource-resolver");
/* 1217 */       v.add("http://apache.org/xml/properties/internal/symbol-table");
/* 1218 */       v.add("http://apache.org/xml/properties/internal/error-reporter");
/* 1219 */       v.add("http://apache.org/xml/properties/internal/error-handler");
/* 1220 */       v.add("http://apache.org/xml/properties/internal/entity-resolver");
/* 1221 */       v.add("http://apache.org/xml/properties/internal/grammar-pool");
/* 1222 */       v.add("http://apache.org/xml/properties/schema/external-schemaLocation");
/* 1223 */       v.add("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
/* 1224 */       v.add("http://java.sun.com/xml/jaxp/properties/schemaSource");
/* 1225 */       v.add("http://apache.org/xml/features/validation/schema-full-checking");
/* 1226 */       v.add("http://apache.org/xml/features/continue-after-fatal-error");
/* 1227 */       v.add("http://apache.org/xml/features/allow-java-encodings");
/* 1228 */       v.add("http://apache.org/xml/features/standard-uri-conformant");
/* 1229 */       v.add("http://apache.org/xml/features/validate-annotations");
/* 1230 */       v.add("http://apache.org/xml/features/generate-synthetic-annotations");
/* 1231 */       v.add("http://apache.org/xml/features/honour-all-schemaLocations");
/* 1232 */       v.add("http://apache.org/xml/features/namespace-growth");
/* 1233 */       v.add("http://apache.org/xml/features/internal/tolerate-duplicates");
/* 1234 */       v.add("http://www.oracle.com/feature/use-service-mechanism");
/* 1235 */       this.fRecognizedParameters = new DOMStringListImpl(v);
/*      */     }
/* 1237 */     return this.fRecognizedParameters;
/*      */   }
/*      */ 
/*      */   public void setParameter(String name, Object value)
/*      */     throws DOMException
/*      */   {
/* 1244 */     if ((value instanceof Boolean)) {
/* 1245 */       boolean state = ((Boolean)value).booleanValue();
/* 1246 */       if ((name.equals("validate")) && (state))
/* 1247 */         return;
/*      */       try
/*      */       {
/* 1250 */         setFeature(name, state);
/*      */       } catch (Exception e) {
/* 1252 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/* 1257 */         throw new DOMException((short)9, msg);
/*      */       }
/* 1259 */       return;
/*      */     }
/* 1261 */     if (name.equals("error-handler")) {
/* 1262 */       if ((value instanceof DOMErrorHandler)) {
/*      */         try {
/* 1264 */           this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)value);
/* 1265 */           setErrorHandler(this.fErrorHandler);
/*      */         } catch (XMLConfigurationException e) {
/*      */         }
/*      */       }
/*      */       else {
/* 1270 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/* 1275 */         throw new DOMException((short)9, msg);
/*      */       }
/* 1277 */       return;
/*      */     }
/*      */ 
/* 1280 */     if (name.equals("resource-resolver")) {
/* 1281 */       if ((value instanceof LSResourceResolver)) {
/*      */         try {
/* 1283 */           this.fResourceResolver = new DOMEntityResolverWrapper((LSResourceResolver)value);
/* 1284 */           setEntityResolver(this.fResourceResolver);
/*      */         } catch (XMLConfigurationException e) {
/*      */         }
/*      */       }
/*      */       else {
/* 1289 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/* 1294 */         throw new DOMException((short)9, msg);
/*      */       }
/* 1296 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1300 */       setProperty(name, value);
/*      */     }
/*      */     catch (Exception ex) {
/* 1303 */       String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/* 1308 */       throw new DOMException((short)9, msg);
/*      */     }
/*      */   }
/*      */ 
/*      */   XMLInputSource dom2xmlInputSource(LSInput is)
/*      */   {
/* 1316 */     XMLInputSource xis = null;
/*      */ 
/* 1327 */     if (is.getCharacterStream() != null) {
/* 1328 */       xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getCharacterStream(), "UTF-16");
/*      */     }
/* 1333 */     else if (is.getByteStream() != null) {
/* 1334 */       xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), is.getByteStream(), is.getEncoding());
/*      */     }
/* 1340 */     else if ((is.getStringData() != null) && (is.getStringData().length() != 0)) {
/* 1341 */       xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI(), new StringReader(is.getStringData()), "UTF-16");
/*      */     }
/*      */     else
/*      */     {
/* 1347 */       xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), is.getBaseURI());
/*      */     }
/*      */ 
/* 1351 */     return xis;
/*      */   }
/*      */ 
/*      */   static class LocationArray
/*      */   {
/*      */     int length;
/*  902 */     String[] locations = new String[2];
/*      */ 
/*      */     public void resize(int oldLength, int newLength) {
/*  905 */       String[] temp = new String[newLength];
/*  906 */       System.arraycopy(this.locations, 0, temp, 0, Math.min(oldLength, newLength));
/*  907 */       this.locations = temp;
/*  908 */       this.length = Math.min(oldLength, newLength);
/*      */     }
/*      */ 
/*      */     public void addLocation(String location) {
/*  912 */       if (this.length >= this.locations.length) {
/*  913 */         resize(this.length, Math.max(1, this.length * 2));
/*      */       }
/*  915 */       this.locations[(this.length++)] = location;
/*      */     }
/*      */ 
/*      */     public String[] getLocationArray() {
/*  919 */       if (this.length < this.locations.length) {
/*  920 */         resize(this.locations.length, this.length);
/*      */       }
/*  922 */       return this.locations;
/*      */     }
/*      */ 
/*      */     public String getFirstLocation() {
/*  926 */       return this.length > 0 ? this.locations[0] : null;
/*      */     }
/*      */ 
/*      */     public int getLength() {
/*  930 */       return this.length;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader
 * JD-Core Version:    0.6.2
 */