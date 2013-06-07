/*      */ package com.sun.org.apache.xerces.internal.impl.xs;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.FieldActivator;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.KeyRef;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector.Matcher;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.ValueStore;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.XPathMatcher;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
/*      */ import com.sun.org.apache.xerces.internal.parsers.XMLParser;
/*      */ import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.IntStack;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLLocator;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.org.apache.xerces.internal.xs.ShortList;
/*      */ import com.sun.org.apache.xerces.internal.xs.StringList;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSObjectList;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class XMLSchemaValidator
/*      */   implements XMLComponent, XMLDocumentFilter, FieldActivator, RevalidationHandler
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
/*      */   protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
/*      */   protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
/*      */   protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
/*      */   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
/*      */   protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
/*      */   protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
/*      */   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
/*      */   protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
/*      */   protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
/*      */   protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
/*      */   protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
/*      */   protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
/*      */   protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
/*      */   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
/*      */   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
/*      */   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
/*      */   protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
/*      */   public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*      */   public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*      */   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*      */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*      */   protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
/*      */   protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
/*      */   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
/*      */   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
/*      */   protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
/*      */   protected static final String USE_SERVICE_MECHANISM = "http://www.oracle.com/feature/use-service-mechanism";
/*  241 */   private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://www.oracle.com/feature/use-service-mechanism" };
/*      */ 
/*  260 */   private static final Boolean[] FEATURE_DEFAULTS = { null, null, null, null, null, null, null, null, null, null, null, null, null, Boolean.TRUE };
/*      */ 
/*  283 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/validation/schema/dv-factory" };
/*      */ 
/*  297 */   private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null, null, null, null, null, null, null, null };
/*      */   protected static final int ID_CONSTRAINT_NUM = 1;
/*  306 */   private static final Hashtable EMPTY_TABLE = new Hashtable();
/*      */ 
/*  313 */   protected ElementPSVImpl fCurrentPSVI = new ElementPSVImpl();
/*      */ 
/*  320 */   protected final AugmentationsImpl fAugmentations = new AugmentationsImpl();
/*      */ 
/*  326 */   protected final HashMap fMayMatchFieldMap = new HashMap();
/*      */   protected XMLString fDefaultValue;
/*  332 */   protected boolean fDynamicValidation = false;
/*  333 */   protected boolean fSchemaDynamicValidation = false;
/*  334 */   protected boolean fDoValidation = false;
/*  335 */   protected boolean fFullChecking = false;
/*  336 */   protected boolean fNormalizeData = true;
/*  337 */   protected boolean fSchemaElementDefault = true;
/*  338 */   protected boolean fAugPSVI = true;
/*  339 */   protected boolean fIdConstraint = false;
/*  340 */   protected boolean fUseGrammarPoolOnly = false;
/*      */ 
/*  343 */   protected boolean fNamespaceGrowth = false;
/*      */ 
/*  346 */   private String fSchemaType = null;
/*      */ 
/*  349 */   protected boolean fEntityRef = false;
/*  350 */   protected boolean fInCDATA = false;
/*      */ 
/*  353 */   protected boolean fSawOnlyWhitespaceInElementContent = false;
/*      */   protected SymbolTable fSymbolTable;
/*      */   private XMLLocator fLocator;
/*  474 */   protected final XSIErrorReporter fXSIErrorReporter = new XSIErrorReporter();
/*      */   protected XMLEntityResolver fEntityResolver;
/*  480 */   protected ValidationManager fValidationManager = null;
/*  481 */   protected ValidationState fValidationState = new ValidationState();
/*      */   protected XMLGrammarPool fGrammarPool;
/*  485 */   protected String fExternalSchemas = null;
/*  486 */   protected String fExternalNoNamespaceSchema = null;
/*      */ 
/*  489 */   protected Object fJaxpSchemaSource = null;
/*      */ 
/*  492 */   protected final XSDDescription fXSDDescription = new XSDDescription();
/*  493 */   protected final Hashtable fLocationPairs = new Hashtable();
/*      */   protected XMLDocumentHandler fDocumentHandler;
/*      */   protected XMLDocumentSource fDocumentSource;
/*  503 */   boolean reportWhitespace = false;
/*      */   static final int INITIAL_STACK_SIZE = 8;
/*      */   static final int INC_STACK_SIZE = 8;
/*      */   private static final boolean DEBUG_NORMALIZATION = false;
/* 1113 */   private final XMLString fEmptyXMLStr = new XMLString(null, 0, -1);
/*      */   private static final int BUFFER_SIZE = 20;
/* 1116 */   private final XMLString fNormalizedStr = new XMLString();
/* 1117 */   private boolean fFirstChunk = true;
/*      */ 
/* 1119 */   private boolean fTrailing = false;
/* 1120 */   private short fWhiteSpace = -1;
/* 1121 */   private boolean fUnionType = false;
/*      */ 
/* 1124 */   private final XSGrammarBucket fGrammarBucket = new XSGrammarBucket();
/* 1125 */   private final SubstitutionGroupHandler fSubGroupHandler = new SubstitutionGroupHandler(this.fGrammarBucket);
/*      */ 
/* 1130 */   private final XSSimpleType fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
/*      */ 
/* 1133 */   private final CMNodeFactory nodeFactory = new CMNodeFactory();
/*      */ 
/* 1136 */   private final CMBuilder fCMBuilder = new CMBuilder(this.nodeFactory);
/*      */ 
/* 1139 */   private final XMLSchemaLoader fSchemaLoader = new XMLSchemaLoader(this.fXSIErrorReporter.fErrorReporter, this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder);
/*      */   private String fValidationRoot;
/*      */   private int fSkipValidationDepth;
/*      */   private int fNFullValidationDepth;
/*      */   private int fNNoneValidationDepth;
/*      */   private int fElementDepth;
/*      */   private boolean fSubElement;
/* 1168 */   private boolean[] fSubElementStack = new boolean[8];
/*      */   private XSElementDecl fCurrentElemDecl;
/* 1174 */   private XSElementDecl[] fElemDeclStack = new XSElementDecl[8];
/*      */   private boolean fNil;
/* 1180 */   private boolean[] fNilStack = new boolean[8];
/*      */   private XSNotationDecl fNotation;
/* 1186 */   private XSNotationDecl[] fNotationStack = new XSNotationDecl[8];
/*      */   private XSTypeDefinition fCurrentType;
/* 1192 */   private XSTypeDefinition[] fTypeStack = new XSTypeDefinition[8];
/*      */   private XSCMValidator fCurrentCM;
/* 1198 */   private XSCMValidator[] fCMStack = new XSCMValidator[8];
/*      */   private int[] fCurrCMState;
/* 1204 */   private int[][] fCMStateStack = new int[8][];
/*      */ 
/* 1207 */   private boolean fStrictAssess = true;
/*      */ 
/* 1210 */   private boolean[] fStrictAssessStack = new boolean[8];
/*      */ 
/* 1213 */   private final StringBuffer fBuffer = new StringBuffer();
/*      */ 
/* 1216 */   private boolean fAppendBuffer = true;
/*      */ 
/* 1219 */   private boolean fSawText = false;
/*      */ 
/* 1222 */   private boolean[] fSawTextStack = new boolean[8];
/*      */ 
/* 1225 */   private boolean fSawCharacters = false;
/*      */ 
/* 1228 */   private boolean[] fStringContent = new boolean[8];
/*      */ 
/* 1231 */   private final QName fTempQName = new QName();
/*      */ 
/* 1234 */   private ValidatedInfo fValidatedInfo = new ValidatedInfo();
/*      */ 
/* 1238 */   private ValidationState fState4XsiType = new ValidationState();
/*      */ 
/* 1242 */   private ValidationState fState4ApplyDefault = new ValidationState();
/*      */ 
/* 1260 */   protected XPathMatcherStack fMatcherStack = new XPathMatcherStack();
/*      */ 
/* 1263 */   protected ValueStoreCache fValueStoreCache = new ValueStoreCache();
/*      */ 
/*      */   public String[] getRecognizedFeatures()
/*      */   {
/*  515 */     return (String[])RECOGNIZED_FEATURES.clone();
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedProperties()
/*      */   {
/*  542 */     return (String[])RECOGNIZED_PROPERTIES.clone();
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */     throws XMLConfigurationException
/*      */   {
/*      */   }
/*      */ 
/*      */   public Boolean getFeatureDefault(String featureId)
/*      */   {
/*  573 */     for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
/*  574 */       if (RECOGNIZED_FEATURES[i].equals(featureId)) {
/*  575 */         return FEATURE_DEFAULTS[i];
/*      */       }
/*      */     }
/*  578 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getPropertyDefault(String propertyId)
/*      */   {
/*  591 */     for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
/*  592 */       if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
/*  593 */         return PROPERTY_DEFAULTS[i];
/*      */       }
/*      */     }
/*  596 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDocumentHandler(XMLDocumentHandler documentHandler)
/*      */   {
/*  605 */     this.fDocumentHandler = documentHandler;
/*      */ 
/*  608 */     if ((documentHandler instanceof XMLParser))
/*      */       try {
/*  610 */         this.reportWhitespace = ((XMLParser)documentHandler).getFeature("http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace");
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  614 */         this.reportWhitespace = false;
/*      */       }
/*      */   }
/*      */ 
/*      */   public XMLDocumentHandler getDocumentHandler()
/*      */   {
/*  621 */     return this.fDocumentHandler;
/*      */   }
/*      */ 
/*      */   public void setDocumentSource(XMLDocumentSource source)
/*      */   {
/*  630 */     this.fDocumentSource = source;
/*      */   }
/*      */ 
/*      */   public XMLDocumentSource getDocumentSource()
/*      */   {
/*  635 */     return this.fDocumentSource;
/*      */   }
/*      */ 
/*      */   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  667 */     this.fValidationState.setNamespaceSupport(namespaceContext);
/*  668 */     this.fState4XsiType.setNamespaceSupport(namespaceContext);
/*  669 */     this.fState4ApplyDefault.setNamespaceSupport(namespaceContext);
/*  670 */     this.fLocator = locator;
/*      */ 
/*  672 */     handleStartDocument(locator, encoding);
/*      */ 
/*  674 */     if (this.fDocumentHandler != null)
/*  675 */       this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
/*      */   }
/*      */ 
/*      */   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  697 */     if (this.fDocumentHandler != null)
/*  698 */       this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
/*      */   }
/*      */ 
/*      */   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  723 */     if (this.fDocumentHandler != null)
/*  724 */       this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
/*      */   }
/*      */ 
/*      */   public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  741 */     Augmentations modifiedAugs = handleStartElement(element, attributes, augs);
/*      */ 
/*  743 */     if (this.fDocumentHandler != null)
/*  744 */       this.fDocumentHandler.startElement(element, attributes, modifiedAugs);
/*      */   }
/*      */ 
/*      */   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  761 */     Augmentations modifiedAugs = handleStartElement(element, attributes, augs);
/*      */ 
/*  766 */     this.fDefaultValue = null;
/*      */ 
/*  769 */     if (this.fElementDepth != -2) {
/*  770 */       modifiedAugs = handleEndElement(element, modifiedAugs);
/*      */     }
/*      */ 
/*  773 */     if (this.fDocumentHandler != null)
/*  774 */       if ((!this.fSchemaElementDefault) || (this.fDefaultValue == null)) {
/*  775 */         this.fDocumentHandler.emptyElement(element, attributes, modifiedAugs);
/*      */       } else {
/*  777 */         this.fDocumentHandler.startElement(element, attributes, modifiedAugs);
/*  778 */         this.fDocumentHandler.characters(this.fDefaultValue, null);
/*  779 */         this.fDocumentHandler.endElement(element, modifiedAugs);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void characters(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  793 */     text = handleCharacters(text);
/*      */ 
/*  795 */     if (this.fSawOnlyWhitespaceInElementContent) {
/*  796 */       this.fSawOnlyWhitespaceInElementContent = false;
/*  797 */       if (!this.reportWhitespace) {
/*  798 */         ignorableWhitespace(text, augs);
/*  799 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  804 */     if (this.fDocumentHandler != null)
/*  805 */       if ((this.fNormalizeData) && (this.fUnionType))
/*      */       {
/*  810 */         if (augs != null)
/*  811 */           this.fDocumentHandler.characters(this.fEmptyXMLStr, augs);
/*      */       }
/*  813 */       else this.fDocumentHandler.characters(text, augs);
/*      */   }
/*      */ 
/*      */   public void ignorableWhitespace(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  833 */     handleIgnorableWhitespace(text);
/*      */ 
/*  835 */     if (this.fDocumentHandler != null)
/*  836 */       this.fDocumentHandler.ignorableWhitespace(text, augs);
/*      */   }
/*      */ 
/*      */   public void endElement(QName element, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  853 */     this.fDefaultValue = null;
/*  854 */     Augmentations modifiedAugs = handleEndElement(element, augs);
/*      */ 
/*  856 */     if (this.fDocumentHandler != null)
/*  857 */       if ((!this.fSchemaElementDefault) || (this.fDefaultValue == null)) {
/*  858 */         this.fDocumentHandler.endElement(element, modifiedAugs);
/*      */       } else {
/*  860 */         this.fDocumentHandler.characters(this.fDefaultValue, null);
/*  861 */         this.fDocumentHandler.endElement(element, modifiedAugs);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void startCDATA(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  876 */     this.fInCDATA = true;
/*      */ 
/*  878 */     if (this.fDocumentHandler != null)
/*  879 */       this.fDocumentHandler.startCDATA(augs);
/*      */   }
/*      */ 
/*      */   public void endCDATA(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  894 */     this.fInCDATA = false;
/*  895 */     if (this.fDocumentHandler != null)
/*  896 */       this.fDocumentHandler.endCDATA(augs);
/*      */   }
/*      */ 
/*      */   public void endDocument(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  910 */     handleEndDocument();
/*      */ 
/*  913 */     if (this.fDocumentHandler != null) {
/*  914 */       this.fDocumentHandler.endDocument(augs);
/*      */     }
/*  916 */     this.fLocator = null;
/*      */   }
/*      */ 
/*      */   public boolean characterData(String data, Augmentations augs)
/*      */   {
/*  930 */     this.fSawText = ((this.fSawText) || (data.length() > 0));
/*      */ 
/*  937 */     if ((this.fNormalizeData) && (this.fWhiteSpace != -1) && (this.fWhiteSpace != 0))
/*      */     {
/*  939 */       normalizeWhitespace(data, this.fWhiteSpace == 2);
/*  940 */       this.fBuffer.append(this.fNormalizedStr.ch, this.fNormalizedStr.offset, this.fNormalizedStr.length);
/*      */     }
/*  942 */     else if (this.fAppendBuffer) {
/*  943 */       this.fBuffer.append(data);
/*      */     }
/*      */ 
/*  948 */     boolean allWhiteSpace = true;
/*  949 */     if ((this.fCurrentType != null) && (this.fCurrentType.getTypeCategory() == 15))
/*      */     {
/*  951 */       XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/*  952 */       if (ctype.fContentType == 2)
/*      */       {
/*  954 */         for (int i = 0; i < data.length(); i++) {
/*  955 */           if (!XMLChar.isSpace(data.charAt(i))) {
/*  956 */             allWhiteSpace = false;
/*  957 */             this.fSawCharacters = true;
/*  958 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  964 */     return allWhiteSpace;
/*      */   }
/*      */ 
/*      */   public void elementDefault(String data)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1000 */     this.fEntityRef = true;
/*      */ 
/* 1002 */     if (this.fDocumentHandler != null)
/* 1003 */       this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
/*      */   }
/*      */ 
/*      */   public void textDecl(String version, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1028 */     if (this.fDocumentHandler != null)
/* 1029 */       this.fDocumentHandler.textDecl(version, encoding, augs);
/*      */   }
/*      */ 
/*      */   public void comment(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1045 */     if (this.fDocumentHandler != null)
/* 1046 */       this.fDocumentHandler.comment(text, augs);
/*      */   }
/*      */ 
/*      */   public void processingInstruction(String target, XMLString data, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1072 */     if (this.fDocumentHandler != null)
/* 1073 */       this.fDocumentHandler.processingInstruction(target, data, augs);
/*      */   }
/*      */ 
/*      */   public void endGeneralEntity(String name, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1093 */     this.fEntityRef = false;
/* 1094 */     if (this.fDocumentHandler != null)
/* 1095 */       this.fDocumentHandler.endGeneralEntity(name, augs);
/*      */   }
/*      */ 
/*      */   public XMLSchemaValidator()
/*      */   {
/* 1271 */     this.fState4XsiType.setExtraChecking(false);
/* 1272 */     this.fState4ApplyDefault.setFacetChecking(false);
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/* 1293 */     this.fIdConstraint = false;
/*      */ 
/* 1295 */     this.fLocationPairs.clear();
/*      */ 
/* 1298 */     this.fValidationState.resetIDTables();
/*      */ 
/* 1301 */     this.nodeFactory.reset(componentManager);
/*      */ 
/* 1304 */     this.fSchemaLoader.reset(componentManager);
/*      */ 
/* 1307 */     this.fCurrentElemDecl = null;
/* 1308 */     this.fCurrentCM = null;
/* 1309 */     this.fCurrCMState = null;
/* 1310 */     this.fSkipValidationDepth = -1;
/* 1311 */     this.fNFullValidationDepth = -1;
/* 1312 */     this.fNNoneValidationDepth = -1;
/* 1313 */     this.fElementDepth = -1;
/* 1314 */     this.fSubElement = false;
/* 1315 */     this.fSchemaDynamicValidation = false;
/*      */ 
/* 1318 */     this.fEntityRef = false;
/* 1319 */     this.fInCDATA = false;
/*      */ 
/* 1321 */     this.fMatcherStack.clear();
/*      */ 
/* 1323 */     if (!this.fMayMatchFieldMap.isEmpty())
/*      */     {
/* 1325 */       this.fMayMatchFieldMap.clear();
/*      */     }
/*      */ 
/* 1329 */     this.fXSIErrorReporter.reset((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*      */ 
/* 1331 */     boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
/*      */ 
/* 1333 */     if (!parser_settings)
/*      */     {
/* 1335 */       this.fValidationManager.addValidationState(this.fValidationState);
/*      */ 
/* 1337 */       XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
/*      */ 
/* 1342 */       return;
/*      */     }
/*      */ 
/* 1347 */     SymbolTable symbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
/* 1348 */     if (symbolTable != this.fSymbolTable) {
/* 1349 */       this.fSymbolTable = symbolTable;
/*      */     }
/*      */ 
/* 1352 */     this.fNamespaceGrowth = componentManager.getFeature("http://apache.org/xml/features/namespace-growth", false);
/* 1353 */     this.fDynamicValidation = componentManager.getFeature("http://apache.org/xml/features/validation/dynamic", false);
/*      */ 
/* 1355 */     if (this.fDynamicValidation)
/* 1356 */       this.fDoValidation = true;
/*      */     else {
/* 1358 */       this.fDoValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
/*      */     }
/*      */ 
/* 1361 */     if (this.fDoValidation) {
/* 1362 */       this.fDoValidation |= componentManager.getFeature("http://apache.org/xml/features/validation/schema", false);
/*      */     }
/*      */ 
/* 1365 */     this.fFullChecking = componentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
/* 1366 */     this.fNormalizeData = componentManager.getFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
/* 1367 */     this.fSchemaElementDefault = componentManager.getFeature("http://apache.org/xml/features/validation/schema/element-default", false);
/*      */ 
/* 1369 */     this.fAugPSVI = componentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
/*      */ 
/* 1371 */     this.fSchemaType = ((String)componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null));
/*      */ 
/* 1375 */     this.fUseGrammarPoolOnly = componentManager.getFeature("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", false);
/*      */ 
/* 1377 */     this.fEntityResolver = ((XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
/*      */ 
/* 1379 */     this.fValidationManager = ((ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
/* 1380 */     this.fValidationManager.addValidationState(this.fValidationState);
/* 1381 */     this.fValidationState.setSymbolTable(this.fSymbolTable);
/*      */     try
/*      */     {
/* 1386 */       this.fExternalSchemas = ((String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation"));
/* 1387 */       this.fExternalNoNamespaceSchema = ((String)componentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation"));
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/* 1390 */       this.fExternalSchemas = null;
/* 1391 */       this.fExternalNoNamespaceSchema = null;
/*      */     }
/*      */ 
/* 1398 */     XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
/*      */ 
/* 1404 */     this.fJaxpSchemaSource = componentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
/*      */ 
/* 1407 */     this.fGrammarPool = ((XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null));
/*      */ 
/* 1409 */     this.fState4XsiType.setSymbolTable(symbolTable);
/* 1410 */     this.fState4ApplyDefault.setSymbolTable(symbolTable);
/*      */   }
/*      */ 
/*      */   public void startValueScopeFor(IdentityConstraint identityConstraint, int initialDepth)
/*      */   {
/* 1427 */     ValueStoreBase valueStore = this.fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
/*      */ 
/* 1429 */     valueStore.startValueScope();
/*      */   }
/*      */ 
/*      */   public XPathMatcher activateField(Field field, int initialDepth)
/*      */   {
/* 1440 */     ValueStore valueStore = this.fValueStoreCache.getValueStoreFor(field.getIdentityConstraint(), initialDepth);
/*      */ 
/* 1442 */     setMayMatch(field, Boolean.TRUE);
/* 1443 */     XPathMatcher matcher = field.createMatcher(this, valueStore);
/* 1444 */     this.fMatcherStack.addMatcher(matcher);
/* 1445 */     matcher.startDocumentFragment();
/* 1446 */     return matcher;
/*      */   }
/*      */ 
/*      */   public void endValueScopeFor(IdentityConstraint identityConstraint, int initialDepth)
/*      */   {
/* 1456 */     ValueStoreBase valueStore = this.fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
/*      */ 
/* 1458 */     valueStore.endValueScope();
/*      */   }
/*      */ 
/*      */   public void setMayMatch(Field field, Boolean state)
/*      */   {
/* 1471 */     this.fMayMatchFieldMap.put(field, state);
/*      */   }
/*      */ 
/*      */   public Boolean mayMatch(Field field)
/*      */   {
/* 1481 */     return (Boolean)this.fMayMatchFieldMap.get(field);
/*      */   }
/*      */ 
/*      */   private void activateSelectorFor(IdentityConstraint ic)
/*      */   {
/* 1486 */     Selector selector = ic.getSelector();
/* 1487 */     FieldActivator activator = this;
/* 1488 */     if (selector == null)
/* 1489 */       return;
/* 1490 */     XPathMatcher matcher = selector.createMatcher(activator, this.fElementDepth);
/* 1491 */     this.fMatcherStack.addMatcher(matcher);
/* 1492 */     matcher.startDocumentFragment();
/*      */   }
/*      */ 
/*      */   void ensureStackCapacity()
/*      */   {
/* 1502 */     if (this.fElementDepth == this.fElemDeclStack.length) {
/* 1503 */       int newSize = this.fElementDepth + 8;
/* 1504 */       boolean[] newArrayB = new boolean[newSize];
/* 1505 */       System.arraycopy(this.fSubElementStack, 0, newArrayB, 0, this.fElementDepth);
/* 1506 */       this.fSubElementStack = newArrayB;
/*      */ 
/* 1508 */       XSElementDecl[] newArrayE = new XSElementDecl[newSize];
/* 1509 */       System.arraycopy(this.fElemDeclStack, 0, newArrayE, 0, this.fElementDepth);
/* 1510 */       this.fElemDeclStack = newArrayE;
/*      */ 
/* 1512 */       newArrayB = new boolean[newSize];
/* 1513 */       System.arraycopy(this.fNilStack, 0, newArrayB, 0, this.fElementDepth);
/* 1514 */       this.fNilStack = newArrayB;
/*      */ 
/* 1516 */       XSNotationDecl[] newArrayN = new XSNotationDecl[newSize];
/* 1517 */       System.arraycopy(this.fNotationStack, 0, newArrayN, 0, this.fElementDepth);
/* 1518 */       this.fNotationStack = newArrayN;
/*      */ 
/* 1520 */       XSTypeDefinition[] newArrayT = new XSTypeDefinition[newSize];
/* 1521 */       System.arraycopy(this.fTypeStack, 0, newArrayT, 0, this.fElementDepth);
/* 1522 */       this.fTypeStack = newArrayT;
/*      */ 
/* 1524 */       XSCMValidator[] newArrayC = new XSCMValidator[newSize];
/* 1525 */       System.arraycopy(this.fCMStack, 0, newArrayC, 0, this.fElementDepth);
/* 1526 */       this.fCMStack = newArrayC;
/*      */ 
/* 1528 */       newArrayB = new boolean[newSize];
/* 1529 */       System.arraycopy(this.fSawTextStack, 0, newArrayB, 0, this.fElementDepth);
/* 1530 */       this.fSawTextStack = newArrayB;
/*      */ 
/* 1532 */       newArrayB = new boolean[newSize];
/* 1533 */       System.arraycopy(this.fStringContent, 0, newArrayB, 0, this.fElementDepth);
/* 1534 */       this.fStringContent = newArrayB;
/*      */ 
/* 1536 */       newArrayB = new boolean[newSize];
/* 1537 */       System.arraycopy(this.fStrictAssessStack, 0, newArrayB, 0, this.fElementDepth);
/* 1538 */       this.fStrictAssessStack = newArrayB;
/*      */ 
/* 1540 */       int[][] newArrayIA = new int[newSize][];
/* 1541 */       System.arraycopy(this.fCMStateStack, 0, newArrayIA, 0, this.fElementDepth);
/* 1542 */       this.fCMStateStack = newArrayIA;
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleStartDocument(XMLLocator locator, String encoding)
/*      */   {
/* 1549 */     this.fValueStoreCache.startDocument();
/* 1550 */     if (this.fAugPSVI) {
/* 1551 */       this.fCurrentPSVI.fGrammars = null;
/* 1552 */       this.fCurrentPSVI.fSchemaInformation = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleEndDocument() {
/* 1557 */     this.fValueStoreCache.endDocument();
/*      */   }
/*      */ 
/*      */   XMLString handleCharacters(XMLString text)
/*      */   {
/* 1564 */     if (this.fSkipValidationDepth >= 0) {
/* 1565 */       return text;
/*      */     }
/* 1567 */     this.fSawText = ((this.fSawText) || (text.length > 0));
/*      */ 
/* 1572 */     if ((this.fNormalizeData) && (this.fWhiteSpace != -1) && (this.fWhiteSpace != 0))
/*      */     {
/* 1574 */       normalizeWhitespace(text, this.fWhiteSpace == 2);
/* 1575 */       text = this.fNormalizedStr;
/*      */     }
/* 1577 */     if (this.fAppendBuffer) {
/* 1578 */       this.fBuffer.append(text.ch, text.offset, text.length);
/*      */     }
/*      */ 
/* 1582 */     this.fSawOnlyWhitespaceInElementContent = false;
/* 1583 */     if ((this.fCurrentType != null) && (this.fCurrentType.getTypeCategory() == 15))
/*      */     {
/* 1585 */       XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/* 1586 */       if (ctype.fContentType == 2)
/*      */       {
/* 1588 */         for (int i = text.offset; i < text.offset + text.length; i++) {
/* 1589 */           if (!XMLChar.isSpace(text.ch[i])) {
/* 1590 */             this.fSawCharacters = true;
/* 1591 */             break;
/*      */           }
/* 1593 */           this.fSawOnlyWhitespaceInElementContent = (!this.fSawCharacters);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1598 */     return text;
/*      */   }
/*      */ 
/*      */   private void normalizeWhitespace(XMLString value, boolean collapse)
/*      */   {
/* 1608 */     boolean skipSpace = collapse;
/* 1609 */     boolean sawNonWS = false;
/* 1610 */     boolean leading = false;
/* 1611 */     boolean trailing = false;
/*      */ 
/* 1613 */     int size = value.offset + value.length;
/*      */ 
/* 1616 */     if ((this.fNormalizedStr.ch == null) || (this.fNormalizedStr.ch.length < value.length + 1)) {
/* 1617 */       this.fNormalizedStr.ch = new char[value.length + 1];
/*      */     }
/*      */ 
/* 1620 */     this.fNormalizedStr.offset = 1;
/* 1621 */     this.fNormalizedStr.length = 1;
/*      */ 
/* 1623 */     for (int i = value.offset; i < size; i++) {
/* 1624 */       char c = value.ch[i];
/* 1625 */       if (XMLChar.isSpace(c)) {
/* 1626 */         if (!skipSpace)
/*      */         {
/* 1628 */           this.fNormalizedStr.ch[(this.fNormalizedStr.length++)] = ' ';
/* 1629 */           skipSpace = collapse;
/*      */         }
/* 1631 */         if (!sawNonWS)
/*      */         {
/* 1633 */           leading = true;
/*      */         }
/*      */       } else {
/* 1636 */         this.fNormalizedStr.ch[(this.fNormalizedStr.length++)] = c;
/* 1637 */         skipSpace = false;
/* 1638 */         sawNonWS = true;
/*      */       }
/*      */     }
/* 1641 */     if (skipSpace) {
/* 1642 */       if (this.fNormalizedStr.length > 1)
/*      */       {
/* 1644 */         this.fNormalizedStr.length -= 1;
/* 1645 */         trailing = true;
/* 1646 */       } else if ((leading) && (!this.fFirstChunk))
/*      */       {
/* 1649 */         trailing = true;
/*      */       }
/*      */     }
/*      */ 
/* 1653 */     if ((this.fNormalizedStr.length > 1) && 
/* 1654 */       (!this.fFirstChunk) && (this.fWhiteSpace == 2)) {
/* 1655 */       if (this.fTrailing)
/*      */       {
/* 1658 */         this.fNormalizedStr.offset = 0;
/* 1659 */         this.fNormalizedStr.ch[0] = ' ';
/* 1660 */       } else if (leading)
/*      */       {
/* 1663 */         this.fNormalizedStr.offset = 0;
/* 1664 */         this.fNormalizedStr.ch[0] = ' ';
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1670 */     this.fNormalizedStr.length -= this.fNormalizedStr.offset;
/*      */ 
/* 1672 */     this.fTrailing = trailing;
/*      */ 
/* 1674 */     if ((trailing) || (sawNonWS))
/* 1675 */       this.fFirstChunk = false;
/*      */   }
/*      */ 
/*      */   private void normalizeWhitespace(String value, boolean collapse) {
/* 1679 */     boolean skipSpace = collapse;
/*      */ 
/* 1681 */     int size = value.length();
/*      */ 
/* 1684 */     if ((this.fNormalizedStr.ch == null) || (this.fNormalizedStr.ch.length < size)) {
/* 1685 */       this.fNormalizedStr.ch = new char[size];
/*      */     }
/* 1687 */     this.fNormalizedStr.offset = 0;
/* 1688 */     this.fNormalizedStr.length = 0;
/*      */ 
/* 1690 */     for (int i = 0; i < size; i++) {
/* 1691 */       char c = value.charAt(i);
/* 1692 */       if (XMLChar.isSpace(c)) {
/* 1693 */         if (!skipSpace)
/*      */         {
/* 1695 */           this.fNormalizedStr.ch[(this.fNormalizedStr.length++)] = ' ';
/* 1696 */           skipSpace = collapse;
/*      */         }
/*      */       } else {
/* 1699 */         this.fNormalizedStr.ch[(this.fNormalizedStr.length++)] = c;
/* 1700 */         skipSpace = false;
/*      */       }
/*      */     }
/* 1703 */     if ((skipSpace) && 
/* 1704 */       (this.fNormalizedStr.length != 0))
/*      */     {
/* 1706 */       this.fNormalizedStr.length -= 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   void handleIgnorableWhitespace(XMLString text)
/*      */   {
/* 1713 */     if (this.fSkipValidationDepth >= 0);
/*      */   }
/*      */ 
/*      */   Augmentations handleStartElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */   {
/* 1729 */     if ((this.fElementDepth == -1) && (this.fValidationManager.isGrammarFound()) && 
/* 1730 */       (this.fSchemaType == null))
/*      */     {
/* 1735 */       this.fSchemaDynamicValidation = true;
/*      */     }
/*      */ 
/* 1748 */     String sLocation = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_SCHEMALOCATION);
/*      */ 
/* 1750 */     String nsLocation = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
/*      */ 
/* 1755 */     storeLocations(sLocation, nsLocation);
/*      */ 
/* 1759 */     if (this.fSkipValidationDepth >= 0) {
/* 1760 */       this.fElementDepth += 1;
/* 1761 */       if (this.fAugPSVI)
/* 1762 */         augs = getEmptyAugs(augs);
/* 1763 */       return augs;
/*      */     }
/*      */ 
/* 1767 */     SchemaGrammar sGrammar = findSchemaGrammar((short)5, element.uri, null, element, attributes);
/*      */ 
/* 1780 */     Object decl = null;
/* 1781 */     if (this.fCurrentCM != null) {
/* 1782 */       decl = this.fCurrentCM.oneTransition(element, this.fCurrCMState, this.fSubGroupHandler);
/*      */ 
/* 1784 */       if (this.fCurrCMState[0] == -1) {
/* 1785 */         XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/*      */         Vector next;
/* 1788 */         if ((ctype.fParticle != null) && ((next = this.fCurrentCM.whatCanGoHere(this.fCurrCMState)).size() > 0))
/*      */         {
/* 1790 */           String expected = expectedStr(next);
/* 1791 */           reportSchemaError("cvc-complex-type.2.4.a", new Object[] { element.rawname, expected });
/*      */         }
/*      */         else
/*      */         {
/* 1795 */           reportSchemaError("cvc-complex-type.2.4.d", new Object[] { element.rawname });
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1801 */     if (this.fElementDepth != -1) {
/* 1802 */       ensureStackCapacity();
/* 1803 */       this.fSubElementStack[this.fElementDepth] = true;
/* 1804 */       this.fSubElement = false;
/* 1805 */       this.fElemDeclStack[this.fElementDepth] = this.fCurrentElemDecl;
/* 1806 */       this.fNilStack[this.fElementDepth] = this.fNil;
/* 1807 */       this.fNotationStack[this.fElementDepth] = this.fNotation;
/* 1808 */       this.fTypeStack[this.fElementDepth] = this.fCurrentType;
/* 1809 */       this.fStrictAssessStack[this.fElementDepth] = this.fStrictAssess;
/* 1810 */       this.fCMStack[this.fElementDepth] = this.fCurrentCM;
/* 1811 */       this.fCMStateStack[this.fElementDepth] = this.fCurrCMState;
/* 1812 */       this.fSawTextStack[this.fElementDepth] = this.fSawText;
/* 1813 */       this.fStringContent[this.fElementDepth] = this.fSawCharacters;
/*      */     }
/*      */ 
/* 1818 */     this.fElementDepth += 1;
/* 1819 */     this.fCurrentElemDecl = null;
/* 1820 */     XSWildcardDecl wildcard = null;
/* 1821 */     this.fCurrentType = null;
/* 1822 */     this.fStrictAssess = true;
/* 1823 */     this.fNil = false;
/* 1824 */     this.fNotation = null;
/*      */ 
/* 1827 */     this.fBuffer.setLength(0);
/* 1828 */     this.fSawText = false;
/* 1829 */     this.fSawCharacters = false;
/*      */ 
/* 1833 */     if (decl != null) {
/* 1834 */       if ((decl instanceof XSElementDecl))
/* 1835 */         this.fCurrentElemDecl = ((XSElementDecl)decl);
/*      */       else {
/* 1837 */         wildcard = (XSWildcardDecl)decl;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1842 */     if ((wildcard != null) && (wildcard.fProcessContents == 2)) {
/* 1843 */       this.fSkipValidationDepth = this.fElementDepth;
/* 1844 */       if (this.fAugPSVI)
/* 1845 */         augs = getEmptyAugs(augs);
/* 1846 */       return augs;
/*      */     }
/*      */ 
/* 1852 */     if ((this.fCurrentElemDecl == null) && 
/* 1853 */       (sGrammar != null)) {
/* 1854 */       this.fCurrentElemDecl = sGrammar.getGlobalElementDecl(element.localpart);
/*      */     }
/*      */ 
/* 1858 */     if (this.fCurrentElemDecl != null)
/*      */     {
/* 1860 */       this.fCurrentType = this.fCurrentElemDecl.fType;
/*      */     }
/*      */ 
/* 1864 */     String xsiType = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_TYPE);
/*      */ 
/* 1867 */     if ((this.fCurrentType == null) && (xsiType == null))
/*      */     {
/* 1871 */       if (this.fElementDepth == 0)
/*      */       {
/* 1874 */         if ((this.fDynamicValidation) || (this.fSchemaDynamicValidation))
/*      */         {
/* 1881 */           if (this.fDocumentSource != null) {
/* 1882 */             this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
/* 1883 */             if (this.fDocumentHandler != null) {
/* 1884 */               this.fDocumentHandler.setDocumentSource(this.fDocumentSource);
/*      */             }
/* 1886 */             this.fElementDepth = -2;
/* 1887 */             return augs;
/*      */           }
/*      */ 
/* 1890 */           this.fSkipValidationDepth = this.fElementDepth;
/* 1891 */           if (this.fAugPSVI)
/* 1892 */             augs = getEmptyAugs(augs);
/* 1893 */           return augs;
/*      */         }
/*      */ 
/* 1901 */         this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "cvc-elt.1", new Object[] { element.rawname }, (short)1);
/*      */       }
/* 1910 */       else if ((wildcard != null) && (wildcard.fProcessContents == 1))
/*      */       {
/* 1912 */         reportSchemaError("cvc-complex-type.2.4.c", new Object[] { element.rawname });
/*      */       }
/*      */ 
/* 1917 */       this.fCurrentType = SchemaGrammar.fAnyType;
/* 1918 */       this.fStrictAssess = false;
/* 1919 */       this.fNFullValidationDepth = this.fElementDepth;
/*      */ 
/* 1921 */       this.fAppendBuffer = false;
/*      */ 
/* 1926 */       this.fXSIErrorReporter.pushContext();
/*      */     }
/*      */     else
/*      */     {
/* 1931 */       this.fXSIErrorReporter.pushContext();
/*      */ 
/* 1934 */       if (xsiType != null) {
/* 1935 */         XSTypeDefinition oldType = this.fCurrentType;
/* 1936 */         this.fCurrentType = getAndCheckXsiType(element, xsiType, attributes);
/*      */ 
/* 1938 */         if (this.fCurrentType == null) {
/* 1939 */           if (oldType == null)
/* 1940 */             this.fCurrentType = SchemaGrammar.fAnyType;
/*      */           else {
/* 1942 */             this.fCurrentType = oldType;
/*      */           }
/*      */         }
/*      */       }
/* 1946 */       this.fNNoneValidationDepth = this.fElementDepth;
/*      */ 
/* 1948 */       if ((this.fCurrentElemDecl != null) && (this.fCurrentElemDecl.getConstraintType() == 2))
/*      */       {
/* 1950 */         this.fAppendBuffer = true;
/*      */       }
/* 1953 */       else if (this.fCurrentType.getTypeCategory() == 16) {
/* 1954 */         this.fAppendBuffer = true;
/*      */       }
/*      */       else {
/* 1957 */         XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/* 1958 */         this.fAppendBuffer = (ctype.fContentType == 1);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1964 */     if ((this.fCurrentElemDecl != null) && (this.fCurrentElemDecl.getAbstract())) {
/* 1965 */       reportSchemaError("cvc-elt.2", new Object[] { element.rawname });
/*      */     }
/*      */ 
/* 1968 */     if (this.fElementDepth == 0) {
/* 1969 */       this.fValidationRoot = element.rawname;
/*      */     }
/*      */ 
/* 1973 */     if (this.fNormalizeData)
/*      */     {
/* 1975 */       this.fFirstChunk = true;
/* 1976 */       this.fTrailing = false;
/* 1977 */       this.fUnionType = false;
/* 1978 */       this.fWhiteSpace = -1;
/*      */     }
/*      */ 
/* 1983 */     if (this.fCurrentType.getTypeCategory() == 15) {
/* 1984 */       XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/* 1985 */       if (ctype.getAbstract()) {
/* 1986 */         reportSchemaError("cvc-type.2", new Object[] { element.rawname });
/*      */       }
/* 1988 */       if (this.fNormalizeData)
/*      */       {
/* 1991 */         if (ctype.fContentType == 1) {
/* 1992 */           if (ctype.fXSSimpleType.getVariety() == 3)
/* 1993 */             this.fUnionType = true;
/*      */           else {
/*      */             try {
/* 1996 */               this.fWhiteSpace = ctype.fXSSimpleType.getWhitespace();
/*      */             }
/*      */             catch (DatatypeException e)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2005 */     else if (this.fNormalizeData)
/*      */     {
/* 2007 */       XSSimpleType dv = (XSSimpleType)this.fCurrentType;
/* 2008 */       if (dv.getVariety() == 3)
/* 2009 */         this.fUnionType = true;
/*      */       else {
/*      */         try {
/* 2012 */           this.fWhiteSpace = dv.getWhitespace();
/*      */         }
/*      */         catch (DatatypeException e)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2020 */     this.fCurrentCM = null;
/* 2021 */     if (this.fCurrentType.getTypeCategory() == 15) {
/* 2022 */       this.fCurrentCM = ((XSComplexTypeDecl)this.fCurrentType).getContentModel(this.fCMBuilder);
/*      */     }
/*      */ 
/* 2026 */     this.fCurrCMState = null;
/* 2027 */     if (this.fCurrentCM != null) {
/* 2028 */       this.fCurrCMState = this.fCurrentCM.startContentModel();
/*      */     }
/*      */ 
/* 2031 */     String xsiNil = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NIL);
/*      */ 
/* 2033 */     if ((xsiNil != null) && (this.fCurrentElemDecl != null)) {
/* 2034 */       this.fNil = getXsiNil(element, xsiNil);
/*      */     }
/*      */ 
/* 2038 */     XSAttributeGroupDecl attrGrp = null;
/* 2039 */     if (this.fCurrentType.getTypeCategory() == 15) {
/* 2040 */       XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/* 2041 */       attrGrp = ctype.getAttrGrp();
/*      */     }
/*      */ 
/* 2044 */     this.fValueStoreCache.startElement();
/* 2045 */     this.fMatcherStack.pushContext();
/* 2046 */     if ((this.fCurrentElemDecl != null) && (this.fCurrentElemDecl.fIDCPos > 0)) {
/* 2047 */       this.fIdConstraint = true;
/*      */ 
/* 2049 */       this.fValueStoreCache.initValueStoresFor(this.fCurrentElemDecl, this);
/*      */     }
/* 2051 */     processAttributes(element, attributes, attrGrp);
/*      */ 
/* 2054 */     if (attrGrp != null) {
/* 2055 */       addDefaultAttributes(element, attributes, attrGrp);
/*      */     }
/*      */ 
/* 2059 */     int count = this.fMatcherStack.getMatcherCount();
/* 2060 */     for (int i = 0; i < count; i++) {
/* 2061 */       XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
/* 2062 */       matcher.startElement(element, attributes);
/*      */     }
/*      */ 
/* 2065 */     if (this.fAugPSVI) {
/* 2066 */       augs = getEmptyAugs(augs);
/*      */ 
/* 2069 */       this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
/*      */ 
/* 2071 */       this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
/*      */ 
/* 2073 */       this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
/*      */ 
/* 2075 */       this.fCurrentPSVI.fNotation = this.fNotation;
/*      */     }
/*      */ 
/* 2078 */     return augs;
/*      */   }
/*      */ 
/*      */   Augmentations handleEndElement(QName element, Augmentations augs)
/*      */   {
/* 2093 */     if (this.fSkipValidationDepth >= 0)
/*      */     {
/* 2096 */       if ((this.fSkipValidationDepth == this.fElementDepth) && (this.fSkipValidationDepth > 0))
/*      */       {
/* 2098 */         this.fNFullValidationDepth = (this.fSkipValidationDepth - 1);
/* 2099 */         this.fSkipValidationDepth = -1;
/* 2100 */         this.fElementDepth -= 1;
/* 2101 */         this.fSubElement = this.fSubElementStack[this.fElementDepth];
/* 2102 */         this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
/* 2103 */         this.fNil = this.fNilStack[this.fElementDepth];
/* 2104 */         this.fNotation = this.fNotationStack[this.fElementDepth];
/* 2105 */         this.fCurrentType = this.fTypeStack[this.fElementDepth];
/* 2106 */         this.fCurrentCM = this.fCMStack[this.fElementDepth];
/* 2107 */         this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
/* 2108 */         this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
/* 2109 */         this.fSawText = this.fSawTextStack[this.fElementDepth];
/* 2110 */         this.fSawCharacters = this.fStringContent[this.fElementDepth];
/*      */       }
/*      */       else {
/* 2113 */         this.fElementDepth -= 1;
/*      */       }
/*      */ 
/* 2121 */       if ((this.fElementDepth == -1) && (this.fFullChecking)) {
/* 2122 */         XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
/*      */       }
/*      */ 
/* 2129 */       if (this.fAugPSVI)
/* 2130 */         augs = getEmptyAugs(augs);
/* 2131 */       return augs;
/*      */     }
/*      */ 
/* 2135 */     processElementContent(element);
/*      */ 
/* 2141 */     int oldCount = this.fMatcherStack.getMatcherCount();
/* 2142 */     for (int i = oldCount - 1; i >= 0; i--) {
/* 2143 */       XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
/* 2144 */       if (this.fCurrentElemDecl == null) {
/* 2145 */         matcher.endElement(element, null, false, this.fValidatedInfo.actualValue, this.fValidatedInfo.actualValueType, this.fValidatedInfo.itemValueTypes);
/*      */       }
/*      */       else {
/* 2148 */         matcher.endElement(element, this.fCurrentType, this.fCurrentElemDecl.getNillable(), this.fDefaultValue == null ? this.fValidatedInfo.actualValue : this.fCurrentElemDecl.fDefault.actualValue, this.fDefaultValue == null ? this.fValidatedInfo.actualValueType : this.fCurrentElemDecl.fDefault.actualValueType, this.fDefaultValue == null ? this.fValidatedInfo.itemValueTypes : this.fCurrentElemDecl.fDefault.itemValueTypes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2163 */     if (this.fMatcherStack.size() > 0) {
/* 2164 */       this.fMatcherStack.popContext();
/*      */     }
/*      */ 
/* 2167 */     int newCount = this.fMatcherStack.getMatcherCount();
/*      */ 
/* 2169 */     for (int i = oldCount - 1; i >= newCount; i--) {
/* 2170 */       XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
/* 2171 */       if ((matcher instanceof Selector.Matcher)) {
/* 2172 */         Selector.Matcher selMatcher = (Selector.Matcher)matcher;
/*      */         IdentityConstraint id;
/* 2174 */         if (((id = selMatcher.getIdentityConstraint()) != null) && (id.getCategory() != 2))
/*      */         {
/* 2176 */           this.fValueStoreCache.transplant(id, selMatcher.getInitialDepth());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2182 */     for (int i = oldCount - 1; i >= newCount; i--) {
/* 2183 */       XPathMatcher matcher = this.fMatcherStack.getMatcherAt(i);
/* 2184 */       if ((matcher instanceof Selector.Matcher)) {
/* 2185 */         Selector.Matcher selMatcher = (Selector.Matcher)matcher;
/*      */         IdentityConstraint id;
/* 2187 */         if (((id = selMatcher.getIdentityConstraint()) != null) && (id.getCategory() == 2))
/*      */         {
/* 2189 */           ValueStoreBase values = this.fValueStoreCache.getValueStoreFor(id, selMatcher.getInitialDepth());
/*      */ 
/* 2191 */           if (values != null)
/* 2192 */             values.endDocumentFragment();
/*      */         }
/*      */       }
/*      */     }
/* 2196 */     this.fValueStoreCache.endElement();
/*      */ 
/* 2198 */     SchemaGrammar[] grammars = null;
/*      */ 
/* 2200 */     if (this.fElementDepth == 0)
/*      */     {
/* 2202 */       String invIdRef = this.fValidationState.checkIDRefID();
/* 2203 */       this.fValidationState.resetIDTables();
/* 2204 */       if (invIdRef != null) {
/* 2205 */         reportSchemaError("cvc-id.1", new Object[] { invIdRef });
/*      */       }
/*      */ 
/* 2208 */       if (this.fFullChecking) {
/* 2209 */         XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
/*      */       }
/*      */ 
/* 2216 */       grammars = this.fGrammarBucket.getGrammars();
/*      */ 
/* 2218 */       if (this.fGrammarPool != null)
/*      */       {
/* 2220 */         for (int k = 0; k < grammars.length; k++) {
/* 2221 */           grammars[k].setImmutable(true);
/*      */         }
/* 2223 */         this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", grammars);
/*      */       }
/* 2225 */       augs = endElementPSVI(true, grammars, augs);
/*      */     } else {
/* 2227 */       augs = endElementPSVI(false, grammars, augs);
/*      */ 
/* 2230 */       this.fElementDepth -= 1;
/*      */ 
/* 2233 */       this.fSubElement = this.fSubElementStack[this.fElementDepth];
/* 2234 */       this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
/* 2235 */       this.fNil = this.fNilStack[this.fElementDepth];
/* 2236 */       this.fNotation = this.fNotationStack[this.fElementDepth];
/* 2237 */       this.fCurrentType = this.fTypeStack[this.fElementDepth];
/* 2238 */       this.fCurrentCM = this.fCMStack[this.fElementDepth];
/* 2239 */       this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
/* 2240 */       this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
/* 2241 */       this.fSawText = this.fSawTextStack[this.fElementDepth];
/* 2242 */       this.fSawCharacters = this.fStringContent[this.fElementDepth];
/*      */ 
/* 2249 */       this.fWhiteSpace = -1;
/*      */ 
/* 2252 */       this.fAppendBuffer = false;
/*      */ 
/* 2254 */       this.fUnionType = false;
/*      */     }
/*      */ 
/* 2257 */     return augs;
/*      */   }
/*      */ 
/*      */   final Augmentations endElementPSVI(boolean root, SchemaGrammar[] grammars, Augmentations augs)
/*      */   {
/* 2265 */     if (this.fAugPSVI) {
/* 2266 */       augs = getEmptyAugs(augs);
/*      */ 
/* 2269 */       this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
/* 2270 */       this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
/* 2271 */       this.fCurrentPSVI.fNotation = this.fNotation;
/* 2272 */       this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
/*      */ 
/* 2277 */       if (this.fElementDepth > this.fNFullValidationDepth) {
/* 2278 */         this.fCurrentPSVI.fValidationAttempted = 2;
/*      */       }
/* 2283 */       else if (this.fElementDepth > this.fNNoneValidationDepth) {
/* 2284 */         this.fCurrentPSVI.fValidationAttempted = 0;
/*      */       }
/*      */       else
/*      */       {
/* 2288 */         this.fCurrentPSVI.fValidationAttempted = 1;
/* 2289 */         this.fNFullValidationDepth = (this.fNNoneValidationDepth = this.fElementDepth - 1);
/*      */       }
/*      */ 
/* 2292 */       if (this.fDefaultValue != null)
/* 2293 */         this.fCurrentPSVI.fSpecified = true;
/* 2294 */       this.fCurrentPSVI.fNil = this.fNil;
/* 2295 */       this.fCurrentPSVI.fMemberType = this.fValidatedInfo.memberType;
/* 2296 */       this.fCurrentPSVI.fNormalizedValue = this.fValidatedInfo.normalizedValue;
/* 2297 */       this.fCurrentPSVI.fActualValue = this.fValidatedInfo.actualValue;
/* 2298 */       this.fCurrentPSVI.fActualValueType = this.fValidatedInfo.actualValueType;
/* 2299 */       this.fCurrentPSVI.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
/*      */ 
/* 2301 */       if (this.fStrictAssess)
/*      */       {
/* 2306 */         String[] errors = this.fXSIErrorReporter.mergeContext();
/*      */ 
/* 2309 */         this.fCurrentPSVI.fErrorCodes = errors;
/*      */ 
/* 2311 */         this.fCurrentPSVI.fValidity = (errors == null ? 2 : 1);
/*      */       }
/*      */       else
/*      */       {
/* 2315 */         this.fCurrentPSVI.fValidity = 0;
/*      */ 
/* 2319 */         this.fXSIErrorReporter.popContext();
/*      */       }
/*      */ 
/* 2322 */       if (root)
/*      */       {
/* 2324 */         this.fCurrentPSVI.fGrammars = grammars;
/* 2325 */         this.fCurrentPSVI.fSchemaInformation = null;
/*      */       }
/*      */     }
/*      */ 
/* 2329 */     return augs;
/*      */   }
/*      */ 
/*      */   Augmentations getEmptyAugs(Augmentations augs)
/*      */   {
/* 2334 */     if (augs == null) {
/* 2335 */       augs = this.fAugmentations;
/* 2336 */       augs.removeAllItems();
/*      */     }
/* 2338 */     augs.putItem("ELEMENT_PSVI", this.fCurrentPSVI);
/* 2339 */     this.fCurrentPSVI.reset();
/*      */ 
/* 2341 */     return augs;
/*      */   }
/*      */ 
/*      */   void storeLocations(String sLocation, String nsLocation) {
/* 2345 */     if ((sLocation != null) && 
/* 2346 */       (!XMLSchemaLoader.tokenizeSchemaLocationStr(sLocation, this.fLocationPairs)))
/*      */     {
/* 2348 */       this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { sLocation }, (short)0);
/*      */     }
/*      */ 
/* 2355 */     if (nsLocation != null) {
/* 2356 */       XMLSchemaLoader.LocationArray la = (XMLSchemaLoader.LocationArray)this.fLocationPairs.get(XMLSymbols.EMPTY_STRING);
/*      */ 
/* 2358 */       if (la == null) {
/* 2359 */         la = new XMLSchemaLoader.LocationArray();
/* 2360 */         this.fLocationPairs.put(XMLSymbols.EMPTY_STRING, la);
/*      */       }
/* 2362 */       la.addLocation(nsLocation);
/*      */     }
/*      */   }
/*      */ 
/*      */   SchemaGrammar findSchemaGrammar(short contextType, String namespace, QName enclosingElement, QName triggeringComponet, XMLAttributes attributes)
/*      */   {
/* 2376 */     SchemaGrammar grammar = null;
/*      */ 
/* 2378 */     grammar = this.fGrammarBucket.getGrammar(namespace);
/*      */ 
/* 2380 */     if (grammar == null) {
/* 2381 */       this.fXSDDescription.setNamespace(namespace);
/*      */ 
/* 2383 */       if (this.fGrammarPool != null) {
/* 2384 */         grammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(this.fXSDDescription);
/* 2385 */         if (grammar != null)
/*      */         {
/* 2388 */           if (!this.fGrammarBucket.putGrammar(grammar, true, this.fNamespaceGrowth))
/*      */           {
/* 2391 */             this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
/*      */ 
/* 2396 */             grammar = null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2401 */     if (((grammar == null) && (!this.fUseGrammarPoolOnly)) || (this.fNamespaceGrowth)) {
/* 2402 */       this.fXSDDescription.reset();
/* 2403 */       this.fXSDDescription.fContextType = contextType;
/* 2404 */       this.fXSDDescription.setNamespace(namespace);
/* 2405 */       this.fXSDDescription.fEnclosedElementName = enclosingElement;
/* 2406 */       this.fXSDDescription.fTriggeringComponent = triggeringComponet;
/* 2407 */       this.fXSDDescription.fAttributes = attributes;
/* 2408 */       if (this.fLocator != null) {
/* 2409 */         this.fXSDDescription.setBaseSystemId(this.fLocator.getExpandedSystemId());
/*      */       }
/*      */ 
/* 2412 */       Hashtable locationPairs = this.fLocationPairs;
/* 2413 */       Object locationArray = locationPairs.get(namespace == null ? XMLSymbols.EMPTY_STRING : namespace);
/*      */ 
/* 2415 */       if (locationArray != null) {
/* 2416 */         String[] temp = ((XMLSchemaLoader.LocationArray)locationArray).getLocationArray();
/* 2417 */         if (temp.length != 0) {
/* 2418 */           setLocationHints(this.fXSDDescription, temp, grammar);
/*      */         }
/*      */       }
/*      */ 
/* 2422 */       if ((grammar == null) || (this.fXSDDescription.fLocationHints != null)) {
/* 2423 */         boolean toParseSchema = true;
/* 2424 */         if (grammar != null)
/*      */         {
/* 2426 */           locationPairs = EMPTY_TABLE;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 2431 */           XMLInputSource xis = XMLSchemaLoader.resolveDocument(this.fXSDDescription, locationPairs, this.fEntityResolver);
/*      */ 
/* 2436 */           if ((grammar != null) && (this.fNamespaceGrowth))
/*      */           {
/*      */             try
/*      */             {
/* 2440 */               if (grammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xis.getSystemId(), xis.getBaseSystemId(), false)))
/* 2441 */                 toParseSchema = false;
/*      */             }
/*      */             catch (URI.MalformedURIException e)
/*      */             {
/*      */             }
/*      */           }
/* 2447 */           if (toParseSchema)
/* 2448 */             grammar = this.fSchemaLoader.loadSchema(this.fXSDDescription, xis, this.fLocationPairs);
/*      */         }
/*      */         catch (IOException ex) {
/* 2451 */           String[] locationHints = this.fXSDDescription.getLocationHints();
/* 2452 */           this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { locationHints != null ? locationHints[0] : XMLSymbols.EMPTY_STRING }, (short)0);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2461 */     return grammar;
/*      */   }
/*      */ 
/*      */   private void setLocationHints(XSDDescription desc, String[] locations, SchemaGrammar grammar) {
/* 2465 */     int length = locations.length;
/* 2466 */     if (grammar == null) {
/* 2467 */       this.fXSDDescription.fLocationHints = new String[length];
/* 2468 */       System.arraycopy(locations, 0, this.fXSDDescription.fLocationHints, 0, length);
/*      */     }
/*      */     else {
/* 2471 */       setLocationHints(desc, locations, grammar.getDocumentLocations());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setLocationHints(XSDDescription desc, String[] locations, StringList docLocations) {
/* 2476 */     int length = locations.length;
/* 2477 */     String[] hints = new String[length];
/* 2478 */     int counter = 0;
/*      */ 
/* 2480 */     for (int i = 0; i < length; i++) {
/*      */       try {
/* 2482 */         String id = XMLEntityManager.expandSystemId(locations[i], desc.getBaseSystemId(), false);
/* 2483 */         if (!docLocations.contains(id)) {
/* 2484 */           hints[(counter++)] = locations[i];
/*      */         }
/*      */       }
/*      */       catch (URI.MalformedURIException e)
/*      */       {
/*      */       }
/*      */     }
/* 2491 */     if (counter > 0)
/* 2492 */       if (counter == length) {
/* 2493 */         this.fXSDDescription.fLocationHints = hints;
/*      */       }
/*      */       else {
/* 2496 */         this.fXSDDescription.fLocationHints = new String[counter];
/* 2497 */         System.arraycopy(hints, 0, this.fXSDDescription.fLocationHints, 0, counter);
/*      */       }
/*      */   }
/*      */ 
/*      */   XSTypeDefinition getAndCheckXsiType(QName element, String xsiType, XMLAttributes attributes)
/*      */   {
/* 2510 */     QName typeName = null;
/*      */     try {
/* 2512 */       typeName = (QName)this.fQNameDV.validate(xsiType, this.fValidationState, null);
/*      */     } catch (InvalidDatatypeValueException e) {
/* 2514 */       reportSchemaError(e.getKey(), e.getArgs());
/* 2515 */       reportSchemaError("cvc-elt.4.1", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE, xsiType });
/*      */ 
/* 2521 */       return null;
/*      */     }
/*      */ 
/* 2525 */     XSTypeDefinition type = null;
/*      */ 
/* 2527 */     if (typeName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
/* 2528 */       type = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(typeName.localpart);
/*      */     }
/*      */ 
/* 2531 */     if (type == null)
/*      */     {
/* 2533 */       SchemaGrammar grammar = findSchemaGrammar((short)7, typeName.uri, element, typeName, attributes);
/*      */ 
/* 2541 */       if (grammar != null) {
/* 2542 */         type = grammar.getGlobalTypeDecl(typeName.localpart);
/*      */       }
/*      */     }
/* 2545 */     if (type == null) {
/* 2546 */       reportSchemaError("cvc-elt.4.2", new Object[] { element.rawname, xsiType });
/* 2547 */       return null;
/*      */     }
/*      */ 
/* 2552 */     if (this.fCurrentType != null)
/*      */     {
/* 2554 */       short block = this.fCurrentElemDecl.fBlock;
/* 2555 */       if (this.fCurrentType.getTypeCategory() == 15)
/* 2556 */         block = (short)(block | ((XSComplexTypeDecl)this.fCurrentType).fBlock);
/* 2557 */       if (!XSConstraints.checkTypeDerivationOk(type, this.fCurrentType, block)) {
/* 2558 */         reportSchemaError("cvc-elt.4.3", new Object[] { element.rawname, xsiType, this.fCurrentType.getName() });
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2563 */     return type;
/*      */   }
/*      */ 
/*      */   boolean getXsiNil(QName element, String xsiNil)
/*      */   {
/* 2570 */     if ((this.fCurrentElemDecl != null) && (!this.fCurrentElemDecl.getNillable())) {
/* 2571 */       reportSchemaError("cvc-elt.3.1", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
/*      */     }
/*      */     else
/*      */     {
/* 2580 */       String value = XMLChar.trim(xsiNil);
/* 2581 */       if ((value.equals("true")) || (value.equals("1")))
/*      */       {
/* 2583 */         if ((this.fCurrentElemDecl != null) && (this.fCurrentElemDecl.getConstraintType() == 2))
/*      */         {
/* 2585 */           reportSchemaError("cvc-elt.3.2.2", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
/*      */         }
/*      */ 
/* 2591 */         return true;
/*      */       }
/*      */     }
/* 2594 */     return false;
/*      */   }
/*      */ 
/*      */   void processAttributes(QName element, XMLAttributes attributes, XSAttributeGroupDecl attrGrp)
/*      */   {
/* 2604 */     String wildcardIDName = null;
/*      */ 
/* 2607 */     int attCount = attributes.getLength();
/*      */ 
/* 2609 */     Augmentations augs = null;
/* 2610 */     AttributePSVImpl attrPSVI = null;
/*      */ 
/* 2612 */     boolean isSimple = (this.fCurrentType == null) || (this.fCurrentType.getTypeCategory() == 16);
/*      */ 
/* 2615 */     XSObjectList attrUses = null;
/* 2616 */     int useCount = 0;
/* 2617 */     XSWildcardDecl attrWildcard = null;
/* 2618 */     if (!isSimple) {
/* 2619 */       attrUses = attrGrp.getAttributeUses();
/* 2620 */       useCount = attrUses.getLength();
/* 2621 */       attrWildcard = attrGrp.fAttributeWC;
/*      */     }
/*      */ 
/* 2627 */     for (int index = 0; index < attCount; index++)
/*      */     {
/* 2629 */       attributes.getName(index, this.fTempQName);
/*      */ 
/* 2635 */       if ((this.fAugPSVI) || (this.fIdConstraint)) {
/* 2636 */         augs = attributes.getAugmentations(index);
/* 2637 */         attrPSVI = (AttributePSVImpl)augs.getItem("ATTRIBUTE_PSVI");
/* 2638 */         if (attrPSVI != null) {
/* 2639 */           attrPSVI.reset();
/*      */         } else {
/* 2641 */           attrPSVI = new AttributePSVImpl();
/* 2642 */           augs.putItem("ATTRIBUTE_PSVI", attrPSVI);
/*      */         }
/*      */ 
/* 2645 */         attrPSVI.fValidationContext = this.fValidationRoot;
/*      */       }
/*      */ 
/* 2654 */       if (this.fTempQName.uri == SchemaSymbols.URI_XSI) {
/* 2655 */         XSAttributeDecl attrDecl = null;
/* 2656 */         if (this.fTempQName.localpart == SchemaSymbols.XSI_SCHEMALOCATION) {
/* 2657 */           attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
/*      */         }
/* 2660 */         else if (this.fTempQName.localpart == SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION) {
/* 2661 */           attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
/*      */         }
/* 2664 */         else if (this.fTempQName.localpart == SchemaSymbols.XSI_NIL)
/* 2665 */           attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NIL);
/* 2666 */         else if (this.fTempQName.localpart == SchemaSymbols.XSI_TYPE)
/* 2667 */           attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_TYPE);
/* 2668 */         if (attrDecl != null) {
/* 2669 */           processOneAttribute(element, attributes, index, attrDecl, null, attrPSVI);
/* 2670 */           continue;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2675 */       if ((this.fTempQName.rawname != XMLSymbols.PREFIX_XMLNS) && (!this.fTempQName.rawname.startsWith("xmlns:")))
/*      */       {
/* 2681 */         if (isSimple) {
/* 2682 */           reportSchemaError("cvc-type.3.1.1", new Object[] { element.rawname, this.fTempQName.rawname });
/*      */         }
/*      */         else
/*      */         {
/* 2689 */           XSAttributeUseImpl currUse = null;
/* 2690 */           for (int i = 0; i < useCount; i++) {
/* 2691 */             XSAttributeUseImpl oneUse = (XSAttributeUseImpl)attrUses.item(i);
/* 2692 */             if ((oneUse.fAttrDecl.fName == this.fTempQName.localpart) && (oneUse.fAttrDecl.fTargetNamespace == this.fTempQName.uri))
/*      */             {
/* 2694 */               currUse = oneUse;
/* 2695 */               break;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2704 */           if (currUse == null)
/*      */           {
/* 2707 */             if ((attrWildcard == null) || (!attrWildcard.allowNamespace(this.fTempQName.uri)))
/*      */             {
/* 2709 */               reportSchemaError("cvc-complex-type.3.2.2", new Object[] { element.rawname, this.fTempQName.rawname });
/*      */ 
/* 2712 */               continue;
/*      */             }
/*      */           }
/*      */ 
/* 2716 */           XSAttributeDecl currDecl = null;
/* 2717 */           if (currUse != null) {
/* 2718 */             currDecl = currUse.fAttrDecl;
/*      */           }
/*      */           else
/*      */           {
/* 2722 */             if (attrWildcard.fProcessContents == 2)
/*      */             {
/*      */               continue;
/*      */             }
/* 2726 */             SchemaGrammar grammar = findSchemaGrammar((short)6, this.fTempQName.uri, element, this.fTempQName, attributes);
/*      */ 
/* 2734 */             if (grammar != null) {
/* 2735 */               currDecl = grammar.getGlobalAttributeDecl(this.fTempQName.localpart);
/*      */             }
/*      */ 
/* 2739 */             if (currDecl == null)
/*      */             {
/* 2741 */               if (attrWildcard.fProcessContents != 1) continue;
/* 2742 */               reportSchemaError("cvc-complex-type.3.2.2", new Object[] { element.rawname, this.fTempQName.rawname }); continue;
/*      */             }
/*      */ 
/* 2752 */             if ((currDecl.fType.getTypeCategory() == 16) && (currDecl.fType.isIDType()))
/*      */             {
/* 2754 */               if (wildcardIDName != null) {
/* 2755 */                 reportSchemaError("cvc-complex-type.5.1", new Object[] { element.rawname, currDecl.fName, wildcardIDName });
/*      */               }
/*      */               else
/*      */               {
/* 2759 */                 wildcardIDName = currDecl.fName;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2764 */           processOneAttribute(element, attributes, index, currDecl, currUse, attrPSVI);
/*      */         }
/*      */       }
/*      */     }
/* 2768 */     if ((!isSimple) && (attrGrp.fIDAttrName != null) && (wildcardIDName != null))
/* 2769 */       reportSchemaError("cvc-complex-type.5.2", new Object[] { element.rawname, wildcardIDName, attrGrp.fIDAttrName });
/*      */   }
/*      */ 
/*      */   void processOneAttribute(QName element, XMLAttributes attributes, int index, XSAttributeDecl currDecl, XSAttributeUseImpl currUse, AttributePSVImpl attrPSVI)
/*      */   {
/* 2784 */     String attrValue = attributes.getValue(index);
/* 2785 */     this.fXSIErrorReporter.pushContext();
/*      */ 
/* 2793 */     XSSimpleType attDV = currDecl.fType;
/*      */ 
/* 2795 */     Object actualValue = null;
/*      */     try {
/* 2797 */       actualValue = attDV.validate(attrValue, this.fValidationState, this.fValidatedInfo);
/*      */ 
/* 2799 */       if (this.fNormalizeData)
/* 2800 */         attributes.setValue(index, this.fValidatedInfo.normalizedValue);
/* 2801 */       if ((attributes instanceof XMLAttributesImpl)) {
/* 2802 */         XMLAttributesImpl attrs = (XMLAttributesImpl)attributes;
/* 2803 */         boolean schemaId = this.fValidatedInfo.memberType != null ? this.fValidatedInfo.memberType.isIDType() : attDV.isIDType();
/*      */ 
/* 2807 */         attrs.setSchemaId(index, schemaId);
/*      */       }
/*      */ 
/* 2811 */       if ((attDV.getVariety() == 1) && (attDV.getPrimitiveKind() == 20))
/*      */       {
/* 2813 */         QName qName = (QName)actualValue;
/* 2814 */         SchemaGrammar grammar = this.fGrammarBucket.getGrammar(qName.uri);
/*      */ 
/* 2822 */         if (grammar != null)
/* 2823 */           this.fNotation = grammar.getGlobalNotationDecl(qName.localpart);
/*      */       }
/*      */     }
/*      */     catch (InvalidDatatypeValueException idve) {
/* 2827 */       reportSchemaError(idve.getKey(), idve.getArgs());
/* 2828 */       reportSchemaError("cvc-attribute.3", new Object[] { element.rawname, this.fTempQName.rawname, attrValue, attDV.getName() });
/*      */     }
/*      */ 
/* 2835 */     if ((actualValue != null) && (currDecl.getConstraintType() == 2) && (
/* 2836 */       (!isComparable(this.fValidatedInfo, currDecl.fDefault)) || (!actualValue.equals(currDecl.fDefault.actualValue)))) {
/* 2837 */       reportSchemaError("cvc-attribute.4", new Object[] { element.rawname, this.fTempQName.rawname, attrValue, currDecl.fDefault.stringValue() });
/*      */     }
/*      */ 
/* 2848 */     if ((actualValue != null) && (currUse != null) && (currUse.fConstraintType == 2))
/*      */     {
/* 2851 */       if ((!isComparable(this.fValidatedInfo, currUse.fDefault)) || (!actualValue.equals(currUse.fDefault.actualValue))) {
/* 2852 */         reportSchemaError("cvc-complex-type.3.1", new Object[] { element.rawname, this.fTempQName.rawname, attrValue, currUse.fDefault.stringValue() });
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2861 */     if (this.fIdConstraint) {
/* 2862 */       attrPSVI.fActualValue = actualValue;
/*      */     }
/*      */ 
/* 2865 */     if (this.fAugPSVI)
/*      */     {
/* 2867 */       attrPSVI.fDeclaration = currDecl;
/*      */ 
/* 2869 */       attrPSVI.fTypeDecl = attDV;
/*      */ 
/* 2872 */       attrPSVI.fMemberType = this.fValidatedInfo.memberType;
/*      */ 
/* 2877 */       attrPSVI.fNormalizedValue = this.fValidatedInfo.normalizedValue;
/* 2878 */       attrPSVI.fActualValue = this.fValidatedInfo.actualValue;
/* 2879 */       attrPSVI.fActualValueType = this.fValidatedInfo.actualValueType;
/* 2880 */       attrPSVI.fItemValueTypes = this.fValidatedInfo.itemValueTypes;
/*      */ 
/* 2885 */       attrPSVI.fValidationAttempted = 2;
/*      */ 
/* 2887 */       String[] errors = this.fXSIErrorReporter.mergeContext();
/*      */ 
/* 2889 */       attrPSVI.fErrorCodes = errors;
/*      */ 
/* 2891 */       attrPSVI.fValidity = (errors == null ? 2 : 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   void addDefaultAttributes(QName element, XMLAttributes attributes, XSAttributeGroupDecl attrGrp)
/*      */   {
/* 2909 */     XSObjectList attrUses = attrGrp.getAttributeUses();
/* 2910 */     int useCount = attrUses.getLength();
/*      */ 
/* 2918 */     for (int i = 0; i < useCount; i++)
/*      */     {
/* 2920 */       XSAttributeUseImpl currUse = (XSAttributeUseImpl)attrUses.item(i);
/* 2921 */       XSAttributeDecl currDecl = currUse.fAttrDecl;
/*      */ 
/* 2923 */       short constType = currUse.fConstraintType;
/* 2924 */       ValidatedInfo defaultValue = currUse.fDefault;
/* 2925 */       if (constType == 0) {
/* 2926 */         constType = currDecl.getConstraintType();
/* 2927 */         defaultValue = currDecl.fDefault;
/*      */       }
/*      */ 
/* 2930 */       boolean isSpecified = attributes.getValue(currDecl.fTargetNamespace, currDecl.fName) != null;
/*      */ 
/* 2936 */       if ((currUse.fUse == 1) && 
/* 2937 */         (!isSpecified)) {
/* 2938 */         reportSchemaError("cvc-complex-type.4", new Object[] { element.rawname, currDecl.fName });
/*      */       }
/*      */ 
/* 2943 */       if ((!isSpecified) && (constType != 0)) {
/* 2944 */         QName attName = new QName(null, currDecl.fName, currDecl.fName, currDecl.fTargetNamespace);
/*      */ 
/* 2946 */         String normalized = defaultValue != null ? defaultValue.stringValue() : "";
/* 2947 */         int attrIndex = attributes.addAttribute(attName, "CDATA", normalized);
/* 2948 */         if ((attributes instanceof XMLAttributesImpl)) {
/* 2949 */           XMLAttributesImpl attrs = (XMLAttributesImpl)attributes;
/* 2950 */           boolean schemaId = (defaultValue != null) && (defaultValue.memberType != null) ? defaultValue.memberType.isIDType() : currDecl.fType.isIDType();
/*      */ 
/* 2955 */           attrs.setSchemaId(attrIndex, schemaId);
/*      */         }
/*      */ 
/* 2958 */         if (this.fAugPSVI)
/*      */         {
/* 2961 */           Augmentations augs = attributes.getAugmentations(attrIndex);
/* 2962 */           AttributePSVImpl attrPSVI = new AttributePSVImpl();
/* 2963 */           augs.putItem("ATTRIBUTE_PSVI", attrPSVI);
/*      */ 
/* 2965 */           attrPSVI.fDeclaration = currDecl;
/* 2966 */           attrPSVI.fTypeDecl = currDecl.fType;
/* 2967 */           attrPSVI.fMemberType = defaultValue.memberType;
/* 2968 */           attrPSVI.fNormalizedValue = normalized;
/* 2969 */           attrPSVI.fActualValue = defaultValue.actualValue;
/* 2970 */           attrPSVI.fActualValueType = defaultValue.actualValueType;
/* 2971 */           attrPSVI.fItemValueTypes = defaultValue.itemValueTypes;
/* 2972 */           attrPSVI.fValidationContext = this.fValidationRoot;
/* 2973 */           attrPSVI.fValidity = 2;
/* 2974 */           attrPSVI.fValidationAttempted = 2;
/* 2975 */           attrPSVI.fSpecified = true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void processElementContent(QName element)
/*      */   {
/* 2989 */     if ((this.fCurrentElemDecl != null) && (this.fCurrentElemDecl.fDefault != null) && (!this.fSawText) && (!this.fSubElement) && (!this.fNil))
/*      */     {
/* 2995 */       String strv = this.fCurrentElemDecl.fDefault.stringValue();
/* 2996 */       int bufLen = strv.length();
/* 2997 */       if ((this.fNormalizedStr.ch == null) || (this.fNormalizedStr.ch.length < bufLen)) {
/* 2998 */         this.fNormalizedStr.ch = new char[bufLen];
/*      */       }
/* 3000 */       strv.getChars(0, bufLen, this.fNormalizedStr.ch, 0);
/* 3001 */       this.fNormalizedStr.offset = 0;
/* 3002 */       this.fNormalizedStr.length = bufLen;
/* 3003 */       this.fDefaultValue = this.fNormalizedStr;
/*      */     }
/*      */ 
/* 3007 */     this.fValidatedInfo.normalizedValue = null;
/*      */ 
/* 3011 */     if ((this.fNil) && (
/* 3012 */       (this.fSubElement) || (this.fSawText))) {
/* 3013 */       reportSchemaError("cvc-elt.3.2.1", new Object[] { element.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
/*      */     }
/*      */ 
/* 3021 */     this.fValidatedInfo.reset();
/*      */ 
/* 3025 */     if ((this.fCurrentElemDecl != null) && (this.fCurrentElemDecl.getConstraintType() != 0) && (!this.fSubElement) && (!this.fSawText) && (!this.fNil))
/*      */     {
/* 3031 */       if (this.fCurrentType != this.fCurrentElemDecl.fType)
/*      */       {
/* 3033 */         if (XSConstraints.ElementDefaultValidImmediate(this.fCurrentType, this.fCurrentElemDecl.fDefault.stringValue(), this.fState4XsiType, null) == null)
/*      */         {
/* 3040 */           reportSchemaError("cvc-elt.5.1.1", new Object[] { element.rawname, this.fCurrentType.getName(), this.fCurrentElemDecl.fDefault.stringValue() });
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3050 */       elementLocallyValidType(element, this.fCurrentElemDecl.fDefault.stringValue());
/*      */     }
/*      */     else
/*      */     {
/* 3057 */       Object actualValue = elementLocallyValidType(element, this.fBuffer);
/*      */ 
/* 3059 */       if ((this.fCurrentElemDecl != null) && (this.fCurrentElemDecl.getConstraintType() == 2) && (!this.fNil))
/*      */       {
/* 3062 */         String content = this.fBuffer.toString();
/*      */ 
/* 3064 */         if (this.fSubElement) {
/* 3065 */           reportSchemaError("cvc-elt.5.2.2.1", new Object[] { element.rawname });
/*      */         }
/* 3067 */         if (this.fCurrentType.getTypeCategory() == 15) {
/* 3068 */           XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/*      */ 
/* 3070 */           if (ctype.fContentType == 3)
/*      */           {
/* 3072 */             if (!this.fCurrentElemDecl.fDefault.normalizedValue.equals(content)) {
/* 3073 */               reportSchemaError("cvc-elt.5.2.2.2.1", new Object[] { element.rawname, content, this.fCurrentElemDecl.fDefault.normalizedValue });
/*      */             }
/*      */ 
/*      */           }
/* 3081 */           else if ((ctype.fContentType == 1) && 
/* 3082 */             (actualValue != null) && (
/* 3082 */             (!isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault)) || (!actualValue.equals(this.fCurrentElemDecl.fDefault.actualValue))))
/*      */           {
/* 3084 */             reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { element.rawname, content, this.fCurrentElemDecl.fDefault.stringValue() });
/*      */           }
/*      */ 
/*      */         }
/* 3092 */         else if ((this.fCurrentType.getTypeCategory() == 16) && 
/* 3093 */           (actualValue != null) && (
/* 3093 */           (!isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault)) || (!actualValue.equals(this.fCurrentElemDecl.fDefault.actualValue))))
/*      */         {
/* 3097 */           reportSchemaError("cvc-elt.5.2.2.2.2", new Object[] { element.rawname, content, this.fCurrentElemDecl.fDefault.stringValue() });
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3108 */     if ((this.fDefaultValue == null) && (this.fNormalizeData) && (this.fDocumentHandler != null) && (this.fUnionType))
/*      */     {
/* 3111 */       String content = this.fValidatedInfo.normalizedValue;
/* 3112 */       if (content == null) {
/* 3113 */         content = this.fBuffer.toString();
/*      */       }
/* 3115 */       int bufLen = content.length();
/* 3116 */       if ((this.fNormalizedStr.ch == null) || (this.fNormalizedStr.ch.length < bufLen)) {
/* 3117 */         this.fNormalizedStr.ch = new char[bufLen];
/*      */       }
/* 3119 */       content.getChars(0, bufLen, this.fNormalizedStr.ch, 0);
/* 3120 */       this.fNormalizedStr.offset = 0;
/* 3121 */       this.fNormalizedStr.length = bufLen;
/* 3122 */       this.fDocumentHandler.characters(this.fNormalizedStr, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   Object elementLocallyValidType(QName element, Object textContent) {
/* 3127 */     if (this.fCurrentType == null) {
/* 3128 */       return null;
/*      */     }
/* 3130 */     Object retValue = null;
/*      */ 
/* 3134 */     if (this.fCurrentType.getTypeCategory() == 16)
/*      */     {
/* 3136 */       if (this.fSubElement) {
/* 3137 */         reportSchemaError("cvc-type.3.1.2", new Object[] { element.rawname });
/*      */       }
/* 3139 */       if (!this.fNil) {
/* 3140 */         XSSimpleType dv = (XSSimpleType)this.fCurrentType;
/*      */         try {
/* 3142 */           if ((!this.fNormalizeData) || (this.fUnionType)) {
/* 3143 */             this.fValidationState.setNormalizationRequired(true);
/*      */           }
/* 3145 */           retValue = dv.validate(textContent, this.fValidationState, this.fValidatedInfo);
/*      */         } catch (InvalidDatatypeValueException e) {
/* 3147 */           reportSchemaError(e.getKey(), e.getArgs());
/* 3148 */           reportSchemaError("cvc-type.3.1.3", new Object[] { element.rawname, textContent });
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 3155 */       retValue = elementLocallyValidComplexType(element, textContent);
/*      */     }
/*      */ 
/* 3158 */     return retValue;
/*      */   }
/*      */ 
/*      */   Object elementLocallyValidComplexType(QName element, Object textContent) {
/* 3162 */     Object actualValue = null;
/* 3163 */     XSComplexTypeDecl ctype = (XSComplexTypeDecl)this.fCurrentType;
/*      */ 
/* 3169 */     if (!this.fNil)
/*      */     {
/* 3171 */       if ((ctype.fContentType == 0) && ((this.fSubElement) || (this.fSawText)))
/*      */       {
/* 3173 */         reportSchemaError("cvc-complex-type.2.1", new Object[] { element.rawname });
/*      */       }
/* 3176 */       else if (ctype.fContentType == 1) {
/* 3177 */         if (this.fSubElement)
/* 3178 */           reportSchemaError("cvc-complex-type.2.2", new Object[] { element.rawname });
/* 3179 */         XSSimpleType dv = ctype.fXSSimpleType;
/*      */         try {
/* 3181 */           if ((!this.fNormalizeData) || (this.fUnionType)) {
/* 3182 */             this.fValidationState.setNormalizationRequired(true);
/*      */           }
/* 3184 */           actualValue = dv.validate(textContent, this.fValidationState, this.fValidatedInfo);
/*      */         } catch (InvalidDatatypeValueException e) {
/* 3186 */           reportSchemaError(e.getKey(), e.getArgs());
/* 3187 */           reportSchemaError("cvc-complex-type.2.2", new Object[] { element.rawname });
/*      */         }
/*      */ 
/*      */       }
/* 3193 */       else if ((ctype.fContentType == 2) && 
/* 3194 */         (this.fSawCharacters)) {
/* 3195 */         reportSchemaError("cvc-complex-type.2.3", new Object[] { element.rawname });
/*      */       }
/*      */ 
/* 3199 */       if ((ctype.fContentType == 2) || (ctype.fContentType == 3))
/*      */       {
/* 3206 */         if ((this.fCurrCMState[0] >= 0) && (!this.fCurrentCM.endContentModel(this.fCurrCMState))) {
/* 3207 */           String expected = expectedStr(this.fCurrentCM.whatCanGoHere(this.fCurrCMState));
/* 3208 */           reportSchemaError("cvc-complex-type.2.4.b", new Object[] { element.rawname, expected });
/*      */         }
/*      */         else
/*      */         {
/* 3216 */           ArrayList errors = this.fCurrentCM.checkMinMaxBounds();
/* 3217 */           if (errors != null) {
/* 3218 */             for (int i = 0; i < errors.size(); i += 2) {
/* 3219 */               reportSchemaError((String)errors.get(i), new Object[] { element.rawname, errors.get(i + 1) });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3227 */     return actualValue;
/*      */   }
/*      */ 
/*      */   void reportSchemaError(String key, Object[] arguments) {
/* 3231 */     if (this.fDoValidation)
/* 3232 */       this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", key, arguments, (short)1);
/*      */   }
/*      */ 
/*      */   private boolean isComparable(ValidatedInfo info1, ValidatedInfo info2)
/*      */   {
/* 3241 */     short primitiveType1 = convertToPrimitiveKind(info1.actualValueType);
/* 3242 */     short primitiveType2 = convertToPrimitiveKind(info2.actualValueType);
/* 3243 */     if (primitiveType1 != primitiveType2) {
/* 3244 */       return ((primitiveType1 == 1) && (primitiveType2 == 2)) || ((primitiveType1 == 2) && (primitiveType2 == 1));
/*      */     }
/*      */ 
/* 3247 */     if ((primitiveType1 == 44) || (primitiveType1 == 43)) {
/* 3248 */       ShortList typeList1 = info1.itemValueTypes;
/* 3249 */       ShortList typeList2 = info2.itemValueTypes;
/* 3250 */       int typeList1Length = typeList1 != null ? typeList1.getLength() : 0;
/* 3251 */       int typeList2Length = typeList2 != null ? typeList2.getLength() : 0;
/* 3252 */       if (typeList1Length != typeList2Length) {
/* 3253 */         return false;
/*      */       }
/* 3255 */       for (int i = 0; i < typeList1Length; i++) {
/* 3256 */         short primitiveItem1 = convertToPrimitiveKind(typeList1.item(i));
/* 3257 */         short primitiveItem2 = convertToPrimitiveKind(typeList2.item(i));
/* 3258 */         if ((primitiveItem1 != primitiveItem2) && 
/* 3259 */           ((primitiveItem1 != 1) || (primitiveItem2 != 2)) && (
/* 3259 */           (primitiveItem1 != 2) || (primitiveItem2 != 1)))
/*      */         {
/* 3263 */           return false;
/*      */         }
/*      */       }
/*      */     }
/* 3267 */     return true;
/*      */   }
/*      */ 
/*      */   private short convertToPrimitiveKind(short valueType)
/*      */   {
/* 3272 */     if (valueType <= 20) {
/* 3273 */       return valueType;
/*      */     }
/*      */ 
/* 3276 */     if (valueType <= 29) {
/* 3277 */       return 2;
/*      */     }
/*      */ 
/* 3280 */     if (valueType <= 42) {
/* 3281 */       return 4;
/*      */     }
/*      */ 
/* 3284 */     return valueType;
/*      */   }
/*      */ 
/*      */   private String expectedStr(Vector expected) {
/* 3288 */     StringBuffer ret = new StringBuffer("{");
/* 3289 */     int size = expected.size();
/* 3290 */     for (int i = 0; i < size; i++) {
/* 3291 */       if (i > 0)
/* 3292 */         ret.append(", ");
/* 3293 */       ret.append(expected.elementAt(i).toString());
/*      */     }
/* 3295 */     ret.append('}');
/* 3296 */     return ret.toString();
/*      */   }
/*      */ 
/*      */   protected class KeyRefValueStore extends XMLSchemaValidator.ValueStoreBase
/*      */   {
/*      */     protected XMLSchemaValidator.ValueStoreBase fKeyValueStore;
/*      */ 
/*      */     public KeyRefValueStore(KeyRef keyRef, XMLSchemaValidator.KeyValueStore keyValueStore)
/*      */     {
/* 3920 */       super(keyRef);
/* 3921 */       this.fKeyValueStore = keyValueStore;
/*      */     }
/*      */ 
/*      */     public void endDocumentFragment()
/*      */     {
/* 3933 */       super.endDocumentFragment();
/*      */ 
/* 3937 */       this.fKeyValueStore = ((XMLSchemaValidator.ValueStoreBase)XMLSchemaValidator.this.fValueStoreCache.fGlobalIDConstraintMap.get(((KeyRef)this.fIdentityConstraint).getKey()));
/*      */ 
/* 3941 */       if (this.fKeyValueStore == null)
/*      */       {
/* 3943 */         String code = "KeyRefOutOfScope";
/* 3944 */         String value = this.fIdentityConstraint.toString();
/* 3945 */         XMLSchemaValidator.this.reportSchemaError(code, new Object[] { value });
/* 3946 */         return;
/*      */       }
/* 3948 */       int errorIndex = this.fKeyValueStore.contains(this);
/* 3949 */       if (errorIndex != -1) {
/* 3950 */         String code = "KeyNotFound";
/* 3951 */         String values = toString(this.fValues, errorIndex, this.fFieldCount);
/* 3952 */         String element = this.fIdentityConstraint.getElementName();
/* 3953 */         String name = this.fIdentityConstraint.getName();
/* 3954 */         XMLSchemaValidator.this.reportSchemaError(code, new Object[] { name, values, element });
/*      */       }
/*      */     }
/*      */ 
/*      */     public void endDocument()
/*      */     {
/* 3961 */       super.endDocument();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class KeyValueStore extends XMLSchemaValidator.ValueStoreBase
/*      */   {
/*      */     public KeyValueStore(UniqueOrKey key)
/*      */     {
/* 3878 */       super(key);
/*      */     }
/*      */ 
/*      */     protected void checkDuplicateValues()
/*      */     {
/* 3889 */       if (contains()) {
/* 3890 */         String code = "DuplicateKey";
/* 3891 */         String value = toString(this.fLocalValues);
/* 3892 */         String eName = this.fIdentityConstraint.getElementName();
/* 3893 */         String cName = this.fIdentityConstraint.getIdentityConstraintName();
/* 3894 */         XMLSchemaValidator.this.reportSchemaError(code, new Object[] { value, eName, cName });
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class LocalIDKey
/*      */   {
/*      */     public IdentityConstraint fId;
/*      */     public int fDepth;
/*      */ 
/*      */     public LocalIDKey()
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalIDKey(IdentityConstraint id, int depth)
/*      */     {
/* 4213 */       this.fId = id;
/* 4214 */       this.fDepth = depth;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 4219 */       return this.fId.hashCode() + this.fDepth;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object localIDKey) {
/* 4223 */       if ((localIDKey instanceof LocalIDKey)) {
/* 4224 */         LocalIDKey lIDKey = (LocalIDKey)localIDKey;
/* 4225 */         return (lIDKey.fId == this.fId) && (lIDKey.fDepth == this.fDepth);
/*      */       }
/* 4227 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static final class ShortVector
/*      */   {
/*      */     private int fLength;
/*      */     private short[] fData;
/*      */ 
/*      */     public ShortVector()
/*      */     {
/*      */     }
/*      */ 
/*      */     public ShortVector(int initialCapacity)
/*      */     {
/* 4253 */       this.fData = new short[initialCapacity];
/*      */     }
/*      */ 
/*      */     public int length()
/*      */     {
/* 4262 */       return this.fLength;
/*      */     }
/*      */ 
/*      */     public void add(short value)
/*      */     {
/* 4267 */       ensureCapacity(this.fLength + 1);
/* 4268 */       this.fData[(this.fLength++)] = value;
/*      */     }
/*      */ 
/*      */     public short valueAt(int position)
/*      */     {
/* 4273 */       return this.fData[position];
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 4278 */       this.fLength = 0;
/*      */     }
/*      */ 
/*      */     public boolean contains(short value)
/*      */     {
/* 4283 */       for (int i = 0; i < this.fLength; i++) {
/* 4284 */         if (this.fData[i] == value) {
/* 4285 */           return true;
/*      */         }
/*      */       }
/* 4288 */       return false;
/*      */     }
/*      */ 
/*      */     private void ensureCapacity(int size)
/*      */     {
/* 4297 */       if (this.fData == null) {
/* 4298 */         this.fData = new short[8];
/*      */       }
/* 4300 */       else if (this.fData.length <= size) {
/* 4301 */         short[] newdata = new short[this.fData.length * 2];
/* 4302 */         System.arraycopy(this.fData, 0, newdata, 0, this.fData.length);
/* 4303 */         this.fData = newdata;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class UniqueValueStore extends XMLSchemaValidator.ValueStoreBase
/*      */   {
/*      */     public UniqueValueStore(UniqueOrKey unique)
/*      */     {
/* 3840 */       super(unique);
/*      */     }
/*      */ 
/*      */     protected void checkDuplicateValues()
/*      */     {
/* 3852 */       if (contains()) {
/* 3853 */         String code = "DuplicateUnique";
/* 3854 */         String value = toString(this.fLocalValues);
/* 3855 */         String eName = this.fIdentityConstraint.getElementName();
/* 3856 */         String cName = this.fIdentityConstraint.getIdentityConstraintName();
/* 3857 */         XMLSchemaValidator.this.reportSchemaError(code, new Object[] { value, eName, cName });
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract class ValueStoreBase
/*      */     implements ValueStore
/*      */   {
/*      */     protected IdentityConstraint fIdentityConstraint;
/* 3405 */     protected int fFieldCount = 0;
/* 3406 */     protected Field[] fFields = null;
/*      */ 
/* 3408 */     protected Object[] fLocalValues = null;
/* 3409 */     protected short[] fLocalValueTypes = null;
/* 3410 */     protected ShortList[] fLocalItemValueTypes = null;
/*      */     protected int fValuesCount;
/* 3416 */     public final Vector fValues = new Vector();
/* 3417 */     public XMLSchemaValidator.ShortVector fValueTypes = null;
/* 3418 */     public Vector fItemValueTypes = null;
/*      */ 
/* 3420 */     private boolean fUseValueTypeVector = false;
/* 3421 */     private int fValueTypesLength = 0;
/* 3422 */     private short fValueType = 0;
/*      */ 
/* 3424 */     private boolean fUseItemValueTypeVector = false;
/* 3425 */     private int fItemValueTypesLength = 0;
/* 3426 */     private ShortList fItemValueType = null;
/*      */ 
/* 3429 */     final StringBuffer fTempBuffer = new StringBuffer();
/*      */ 
/*      */     protected ValueStoreBase(IdentityConstraint identityConstraint)
/*      */     {
/* 3437 */       this.fIdentityConstraint = identityConstraint;
/* 3438 */       this.fFieldCount = this.fIdentityConstraint.getFieldCount();
/* 3439 */       this.fFields = new Field[this.fFieldCount];
/* 3440 */       this.fLocalValues = new Object[this.fFieldCount];
/* 3441 */       this.fLocalValueTypes = new short[this.fFieldCount];
/* 3442 */       this.fLocalItemValueTypes = new ShortList[this.fFieldCount];
/* 3443 */       for (int i = 0; i < this.fFieldCount; i++)
/* 3444 */         this.fFields[i] = this.fIdentityConstraint.getFieldAt(i);
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3455 */       this.fValuesCount = 0;
/* 3456 */       this.fUseValueTypeVector = false;
/* 3457 */       this.fValueTypesLength = 0;
/* 3458 */       this.fValueType = 0;
/* 3459 */       this.fUseItemValueTypeVector = false;
/* 3460 */       this.fItemValueTypesLength = 0;
/* 3461 */       this.fItemValueType = null;
/* 3462 */       this.fValues.setSize(0);
/* 3463 */       if (this.fValueTypes != null) {
/* 3464 */         this.fValueTypes.clear();
/*      */       }
/* 3466 */       if (this.fItemValueTypes != null)
/* 3467 */         this.fItemValueTypes.setSize(0);
/*      */     }
/*      */ 
/*      */     public void append(ValueStoreBase newVal)
/*      */     {
/* 3473 */       for (int i = 0; i < newVal.fValues.size(); i++)
/* 3474 */         this.fValues.addElement(newVal.fValues.elementAt(i));
/*      */     }
/*      */ 
/*      */     public void startValueScope()
/*      */     {
/* 3480 */       this.fValuesCount = 0;
/* 3481 */       for (int i = 0; i < this.fFieldCount; i++) {
/* 3482 */         this.fLocalValues[i] = null;
/* 3483 */         this.fLocalValueTypes[i] = 0;
/* 3484 */         this.fLocalItemValueTypes[i] = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void endValueScope()
/*      */     {
/* 3491 */       if (this.fValuesCount == 0) {
/* 3492 */         if (this.fIdentityConstraint.getCategory() == 1) {
/* 3493 */           String code = "AbsentKeyValue";
/* 3494 */           String eName = this.fIdentityConstraint.getElementName();
/* 3495 */           String cName = this.fIdentityConstraint.getIdentityConstraintName();
/* 3496 */           XMLSchemaValidator.this.reportSchemaError(code, new Object[] { eName, cName });
/*      */         }
/* 3498 */         return;
/*      */       }
/*      */ 
/* 3507 */       if (this.fValuesCount != this.fFieldCount) {
/* 3508 */         if (this.fIdentityConstraint.getCategory() == 1) {
/* 3509 */           String code = "KeyNotEnoughValues";
/* 3510 */           UniqueOrKey key = (UniqueOrKey)this.fIdentityConstraint;
/* 3511 */           String eName = this.fIdentityConstraint.getElementName();
/* 3512 */           String cName = key.getIdentityConstraintName();
/* 3513 */           XMLSchemaValidator.this.reportSchemaError(code, new Object[] { eName, cName });
/*      */         }
/* 3515 */         return;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void endDocumentFragment()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void endDocument()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void reportError(String key, Object[] args)
/*      */     {
/* 3545 */       XMLSchemaValidator.this.reportSchemaError(key, args);
/*      */     }
/*      */ 
/*      */     public void addValue(Field field, Object actualValue, short valueType, ShortList itemValueType)
/*      */     {
/* 3558 */       for (int i = this.fFieldCount - 1; (i > -1) && 
/* 3559 */         (this.fFields[i] != field); i--);
/* 3564 */       if (i == -1) {
/* 3565 */         String code = "UnknownField";
/* 3566 */         String eName = this.fIdentityConstraint.getElementName();
/* 3567 */         String cName = this.fIdentityConstraint.getIdentityConstraintName();
/* 3568 */         XMLSchemaValidator.this.reportSchemaError(code, new Object[] { field.toString(), eName, cName });
/* 3569 */         return;
/*      */       }
/* 3571 */       if (Boolean.TRUE != XMLSchemaValidator.this.mayMatch(field)) {
/* 3572 */         String code = "FieldMultipleMatch";
/* 3573 */         String cName = this.fIdentityConstraint.getIdentityConstraintName();
/* 3574 */         XMLSchemaValidator.this.reportSchemaError(code, new Object[] { field.toString(), cName });
/*      */       } else {
/* 3576 */         this.fValuesCount += 1;
/*      */       }
/* 3578 */       this.fLocalValues[i] = actualValue;
/* 3579 */       this.fLocalValueTypes[i] = valueType;
/* 3580 */       this.fLocalItemValueTypes[i] = itemValueType;
/* 3581 */       if (this.fValuesCount == this.fFieldCount) {
/* 3582 */         checkDuplicateValues();
/*      */ 
/* 3584 */         for (i = 0; i < this.fFieldCount; i++) {
/* 3585 */           this.fValues.addElement(this.fLocalValues[i]);
/* 3586 */           addValueType(this.fLocalValueTypes[i]);
/* 3587 */           addItemValueType(this.fLocalItemValueTypes[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean contains()
/*      */     {
/* 3598 */       int next = 0;
/* 3599 */       int size = this.fValues.size();
/* 3600 */       label165: for (int i = 0; i < size; i = next) {
/* 3601 */         next = i + this.fFieldCount;
/* 3602 */         for (int j = 0; j < this.fFieldCount; j++) {
/* 3603 */           Object value1 = this.fLocalValues[j];
/* 3604 */           Object value2 = this.fValues.elementAt(i);
/* 3605 */           short valueType1 = this.fLocalValueTypes[j];
/* 3606 */           short valueType2 = getValueTypeAt(i);
/* 3607 */           if ((value1 == null) || (value2 == null) || (valueType1 != valueType2) || (!value1.equals(value2))) {
/*      */             break label165;
/*      */           }
/* 3610 */           if ((valueType1 == 44) || (valueType1 == 43)) {
/* 3611 */             ShortList list1 = this.fLocalItemValueTypes[j];
/* 3612 */             ShortList list2 = getItemValueTypeAt(i);
/* 3613 */             if ((list1 == null) || (list2 == null) || (!list1.equals(list2)))
/*      */               break label165;
/*      */           }
/* 3616 */           i++;
/*      */         }
/*      */ 
/* 3619 */         return true;
/*      */       }
/*      */ 
/* 3622 */       return false;
/*      */     }
/*      */ 
/*      */     public int contains(ValueStoreBase vsb)
/*      */     {
/* 3632 */       Vector values = vsb.fValues;
/* 3633 */       int size1 = values.size();
/* 3634 */       if (this.fFieldCount <= 1) {
/* 3635 */         for (int i = 0; i < size1; i++) {
/* 3636 */           short val = vsb.getValueTypeAt(i);
/* 3637 */           if ((!valueTypeContains(val)) || (!this.fValues.contains(values.elementAt(i)))) {
/* 3638 */             return i;
/*      */           }
/* 3640 */           if ((val == 44) || (val == 43)) {
/* 3641 */             ShortList list1 = vsb.getItemValueTypeAt(i);
/* 3642 */             if (!itemValueTypeContains(list1)) {
/* 3643 */               return i;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 3650 */         int size2 = this.fValues.size();
/*      */ 
/* 3652 */         label293: label308: for (int i = 0; i < size1; i += this.fFieldCount)
/*      */         {
/* 3654 */           for (int j = 0; j < size2; j += this.fFieldCount) {
/* 3655 */             for (int k = 0; k < this.fFieldCount; k++) {
/* 3656 */               Object value1 = values.elementAt(i + k);
/* 3657 */               Object value2 = this.fValues.elementAt(j + k);
/* 3658 */               short valueType1 = vsb.getValueTypeAt(i + k);
/* 3659 */               short valueType2 = getValueTypeAt(j + k);
/* 3660 */               if ((value1 != value2) && ((valueType1 != valueType2) || (value1 == null) || (!value1.equals(value2)))) {
/*      */                 break label293;
/*      */               }
/* 3663 */               if ((valueType1 == 44) || (valueType1 == 43)) {
/* 3664 */                 ShortList list1 = vsb.getItemValueTypeAt(i + k);
/* 3665 */                 ShortList list2 = getItemValueTypeAt(j + k);
/* 3666 */                 if ((list1 == null) || (list2 == null) || (!list1.equals(list2))) {
/*      */                   break label293;
/*      */                 }
/*      */               }
/*      */             }
/* 3671 */             break label308;
/*      */           }
/* 3673 */           return i;
/*      */         }
/*      */       }
/* 3676 */       return -1;
/*      */     }
/*      */ 
/*      */     protected void checkDuplicateValues()
/*      */     {
/*      */     }
/*      */ 
/*      */     protected String toString(Object[] values)
/*      */     {
/* 3692 */       int size = values.length;
/* 3693 */       if (size == 0) {
/* 3694 */         return "";
/*      */       }
/*      */ 
/* 3697 */       this.fTempBuffer.setLength(0);
/*      */ 
/* 3700 */       for (int i = 0; i < size; i++) {
/* 3701 */         if (i > 0) {
/* 3702 */           this.fTempBuffer.append(',');
/*      */         }
/* 3704 */         this.fTempBuffer.append(values[i]);
/*      */       }
/* 3706 */       return this.fTempBuffer.toString();
/*      */     }
/*      */ 
/*      */     protected String toString(Vector values, int start, int length)
/*      */     {
/* 3714 */       if (length == 0) {
/* 3715 */         return "";
/*      */       }
/*      */ 
/* 3719 */       if (length == 1) {
/* 3720 */         return String.valueOf(values.elementAt(start));
/*      */       }
/*      */ 
/* 3724 */       StringBuffer str = new StringBuffer();
/* 3725 */       for (int i = 0; i < length; i++) {
/* 3726 */         if (i > 0) {
/* 3727 */           str.append(',');
/*      */         }
/* 3729 */         str.append(values.elementAt(start + i));
/*      */       }
/* 3731 */       return str.toString();
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 3741 */       String s = super.toString();
/* 3742 */       int index1 = s.lastIndexOf('$');
/* 3743 */       if (index1 != -1) {
/* 3744 */         s = s.substring(index1 + 1);
/*      */       }
/* 3746 */       int index2 = s.lastIndexOf('.');
/* 3747 */       if (index2 != -1) {
/* 3748 */         s = s.substring(index2 + 1);
/*      */       }
/* 3750 */       return s + '[' + this.fIdentityConstraint + ']';
/*      */     }
/*      */ 
/*      */     private void addValueType(short type)
/*      */     {
/* 3758 */       if (this.fUseValueTypeVector) {
/* 3759 */         this.fValueTypes.add(type);
/*      */       }
/* 3761 */       else if (this.fValueTypesLength++ == 0) {
/* 3762 */         this.fValueType = type;
/*      */       }
/* 3764 */       else if (this.fValueType != type) {
/* 3765 */         this.fUseValueTypeVector = true;
/* 3766 */         if (this.fValueTypes == null) {
/* 3767 */           this.fValueTypes = new XMLSchemaValidator.ShortVector(this.fValueTypesLength * 2);
/*      */         }
/* 3769 */         for (int i = 1; i < this.fValueTypesLength; i++) {
/* 3770 */           this.fValueTypes.add(this.fValueType);
/*      */         }
/* 3772 */         this.fValueTypes.add(type);
/*      */       }
/*      */     }
/*      */ 
/*      */     private short getValueTypeAt(int index) {
/* 3777 */       if (this.fUseValueTypeVector) {
/* 3778 */         return this.fValueTypes.valueAt(index);
/*      */       }
/* 3780 */       return this.fValueType;
/*      */     }
/*      */ 
/*      */     private boolean valueTypeContains(short value) {
/* 3784 */       if (this.fUseValueTypeVector) {
/* 3785 */         return this.fValueTypes.contains(value);
/*      */       }
/* 3787 */       return this.fValueType == value;
/*      */     }
/*      */ 
/*      */     private void addItemValueType(ShortList itemValueType) {
/* 3791 */       if (this.fUseItemValueTypeVector) {
/* 3792 */         this.fItemValueTypes.add(itemValueType);
/*      */       }
/* 3794 */       else if (this.fItemValueTypesLength++ == 0) {
/* 3795 */         this.fItemValueType = itemValueType;
/*      */       }
/* 3797 */       else if ((this.fItemValueType != itemValueType) && ((this.fItemValueType == null) || (!this.fItemValueType.equals(itemValueType))))
/*      */       {
/* 3799 */         this.fUseItemValueTypeVector = true;
/* 3800 */         if (this.fItemValueTypes == null) {
/* 3801 */           this.fItemValueTypes = new Vector(this.fItemValueTypesLength * 2);
/*      */         }
/* 3803 */         for (int i = 1; i < this.fItemValueTypesLength; i++) {
/* 3804 */           this.fItemValueTypes.add(this.fItemValueType);
/*      */         }
/* 3806 */         this.fItemValueTypes.add(itemValueType);
/*      */       }
/*      */     }
/*      */ 
/*      */     private ShortList getItemValueTypeAt(int index) {
/* 3811 */       if (this.fUseItemValueTypeVector) {
/* 3812 */         return (ShortList)this.fItemValueTypes.elementAt(index);
/*      */       }
/* 3814 */       return this.fItemValueType;
/*      */     }
/*      */ 
/*      */     private boolean itemValueTypeContains(ShortList value) {
/* 3818 */       if (this.fUseItemValueTypeVector) {
/* 3819 */         return this.fItemValueTypes.contains(value);
/*      */       }
/* 3821 */       return (this.fItemValueType == value) || ((this.fItemValueType != null) && (this.fItemValueType.equals(value)));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class ValueStoreCache
/*      */   {
/* 3980 */     final XMLSchemaValidator.LocalIDKey fLocalId = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this);
/*      */ 
/* 3984 */     protected final Vector fValueStores = new Vector();
/*      */ 
/* 3995 */     protected final Hashtable fIdentityConstraint2ValueStoreMap = new Hashtable();
/*      */ 
/* 4016 */     protected final Stack fGlobalMapStack = new Stack();
/* 4017 */     protected final Hashtable fGlobalIDConstraintMap = new Hashtable();
/*      */ 
/*      */     public ValueStoreCache()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void startDocument()
/*      */     {
/* 4033 */       this.fValueStores.removeAllElements();
/* 4034 */       this.fIdentityConstraint2ValueStoreMap.clear();
/* 4035 */       this.fGlobalIDConstraintMap.clear();
/* 4036 */       this.fGlobalMapStack.removeAllElements();
/*      */     }
/*      */ 
/*      */     public void startElement()
/*      */     {
/* 4043 */       if (this.fGlobalIDConstraintMap.size() > 0)
/* 4044 */         this.fGlobalMapStack.push(this.fGlobalIDConstraintMap.clone());
/*      */       else
/* 4046 */         this.fGlobalMapStack.push(null);
/* 4047 */       this.fGlobalIDConstraintMap.clear();
/*      */     }
/*      */ 
/*      */     public void endElement()
/*      */     {
/* 4054 */       if (this.fGlobalMapStack.isEmpty()) {
/* 4055 */         return;
/*      */       }
/* 4057 */       Hashtable oldMap = (Hashtable)this.fGlobalMapStack.pop();
/*      */ 
/* 4059 */       if (oldMap == null) {
/* 4060 */         return;
/*      */       }
/*      */ 
/* 4063 */       Iterator entries = oldMap.entrySet().iterator();
/* 4064 */       while (entries.hasNext()) {
/* 4065 */         Map.Entry entry = (Map.Entry)entries.next();
/* 4066 */         IdentityConstraint id = (IdentityConstraint)entry.getKey();
/* 4067 */         XMLSchemaValidator.ValueStoreBase oldVal = (XMLSchemaValidator.ValueStoreBase)entry.getValue();
/* 4068 */         if (oldVal != null) {
/* 4069 */           XMLSchemaValidator.ValueStoreBase currVal = (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(id);
/* 4070 */           if (currVal == null) {
/* 4071 */             this.fGlobalIDConstraintMap.put(id, oldVal);
/*      */           }
/* 4073 */           else if (currVal != oldVal)
/* 4074 */             currVal.append(oldVal);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void initValueStoresFor(XSElementDecl eDecl, FieldActivator activator)
/*      */     {
/* 4086 */       IdentityConstraint[] icArray = eDecl.fIDConstraints;
/* 4087 */       int icCount = eDecl.fIDCPos;
/* 4088 */       for (int i = 0; i < icCount; i++)
/*      */       {
/*      */         XMLSchemaValidator.LocalIDKey toHash;
/* 4089 */         switch (icArray[i].getCategory())
/*      */         {
/*      */         case 3:
/* 4092 */           UniqueOrKey unique = (UniqueOrKey)icArray[i];
/* 4093 */           toHash = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this, unique, XMLSchemaValidator.this.fElementDepth);
/* 4094 */           XMLSchemaValidator.UniqueValueStore uniqueValueStore = (XMLSchemaValidator.UniqueValueStore)this.fIdentityConstraint2ValueStoreMap.get(toHash);
/*      */ 
/* 4096 */           if (uniqueValueStore == null) {
/* 4097 */             uniqueValueStore = new XMLSchemaValidator.UniqueValueStore(XMLSchemaValidator.this, unique);
/* 4098 */             this.fIdentityConstraint2ValueStoreMap.put(toHash, uniqueValueStore);
/*      */           } else {
/* 4100 */             uniqueValueStore.clear();
/*      */           }
/* 4102 */           this.fValueStores.addElement(uniqueValueStore);
/* 4103 */           XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
/* 4104 */           break;
/*      */         case 1:
/* 4107 */           UniqueOrKey key = (UniqueOrKey)icArray[i];
/* 4108 */           toHash = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this, key, XMLSchemaValidator.this.fElementDepth);
/* 4109 */           XMLSchemaValidator.KeyValueStore keyValueStore = (XMLSchemaValidator.KeyValueStore)this.fIdentityConstraint2ValueStoreMap.get(toHash);
/*      */ 
/* 4111 */           if (keyValueStore == null) {
/* 4112 */             keyValueStore = new XMLSchemaValidator.KeyValueStore(XMLSchemaValidator.this, key);
/* 4113 */             this.fIdentityConstraint2ValueStoreMap.put(toHash, keyValueStore);
/*      */           } else {
/* 4115 */             keyValueStore.clear();
/*      */           }
/* 4117 */           this.fValueStores.addElement(keyValueStore);
/* 4118 */           XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
/* 4119 */           break;
/*      */         case 2:
/* 4122 */           KeyRef keyRef = (KeyRef)icArray[i];
/* 4123 */           toHash = new XMLSchemaValidator.LocalIDKey(XMLSchemaValidator.this, keyRef, XMLSchemaValidator.this.fElementDepth);
/* 4124 */           XMLSchemaValidator.KeyRefValueStore keyRefValueStore = (XMLSchemaValidator.KeyRefValueStore)this.fIdentityConstraint2ValueStoreMap.get(toHash);
/*      */ 
/* 4126 */           if (keyRefValueStore == null) {
/* 4127 */             keyRefValueStore = new XMLSchemaValidator.KeyRefValueStore(XMLSchemaValidator.this, keyRef, null);
/* 4128 */             this.fIdentityConstraint2ValueStoreMap.put(toHash, keyRefValueStore);
/*      */           } else {
/* 4130 */             keyRefValueStore.clear();
/*      */           }
/* 4132 */           this.fValueStores.addElement(keyRefValueStore);
/* 4133 */           XMLSchemaValidator.this.activateSelectorFor(icArray[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public XMLSchemaValidator.ValueStoreBase getValueStoreFor(IdentityConstraint id, int initialDepth)
/*      */     {
/* 4141 */       this.fLocalId.fDepth = initialDepth;
/* 4142 */       this.fLocalId.fId = id;
/* 4143 */       return (XMLSchemaValidator.ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
/*      */     }
/*      */ 
/*      */     public XMLSchemaValidator.ValueStoreBase getGlobalValueStoreFor(IdentityConstraint id)
/*      */     {
/* 4148 */       return (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(id);
/*      */     }
/*      */ 
/*      */     public void transplant(IdentityConstraint id, int initialDepth)
/*      */     {
/* 4156 */       this.fLocalId.fDepth = initialDepth;
/* 4157 */       this.fLocalId.fId = id;
/* 4158 */       XMLSchemaValidator.ValueStoreBase newVals = (XMLSchemaValidator.ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
/*      */ 
/* 4160 */       if (id.getCategory() == 2)
/* 4161 */         return;
/* 4162 */       XMLSchemaValidator.ValueStoreBase currVals = (XMLSchemaValidator.ValueStoreBase)this.fGlobalIDConstraintMap.get(id);
/* 4163 */       if (currVals != null) {
/* 4164 */         currVals.append(newVals);
/* 4165 */         this.fGlobalIDConstraintMap.put(id, currVals);
/*      */       } else {
/* 4167 */         this.fGlobalIDConstraintMap.put(id, newVals);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void endDocument()
/*      */     {
/* 4174 */       int count = this.fValueStores.size();
/* 4175 */       for (int i = 0; i < count; i++) {
/* 4176 */         XMLSchemaValidator.ValueStoreBase valueStore = (XMLSchemaValidator.ValueStoreBase)this.fValueStores.elementAt(i);
/* 4177 */         valueStore.endDocument();
/*      */       }
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 4188 */       String s = super.toString();
/* 4189 */       int index1 = s.lastIndexOf('$');
/* 4190 */       if (index1 != -1) {
/* 4191 */         return s.substring(index1 + 1);
/*      */       }
/* 4193 */       int index2 = s.lastIndexOf('.');
/* 4194 */       if (index2 != -1) {
/* 4195 */         return s.substring(index2 + 1);
/*      */       }
/* 4197 */       return s;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static class XPathMatcherStack
/*      */   {
/* 3315 */     protected XPathMatcher[] fMatchers = new XPathMatcher[4];
/*      */     protected int fMatchersCount;
/* 3321 */     protected IntStack fContextStack = new IntStack();
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3336 */       for (int i = 0; i < this.fMatchersCount; i++) {
/* 3337 */         this.fMatchers[i] = null;
/*      */       }
/* 3339 */       this.fMatchersCount = 0;
/* 3340 */       this.fContextStack.clear();
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3345 */       return this.fContextStack.size();
/*      */     }
/*      */ 
/*      */     public int getMatcherCount()
/*      */     {
/* 3350 */       return this.fMatchersCount;
/*      */     }
/*      */ 
/*      */     public void addMatcher(XPathMatcher matcher)
/*      */     {
/* 3355 */       ensureMatcherCapacity();
/* 3356 */       this.fMatchers[(this.fMatchersCount++)] = matcher;
/*      */     }
/*      */ 
/*      */     public XPathMatcher getMatcherAt(int index)
/*      */     {
/* 3361 */       return this.fMatchers[index];
/*      */     }
/*      */ 
/*      */     public void pushContext()
/*      */     {
/* 3366 */       this.fContextStack.push(this.fMatchersCount);
/*      */     }
/*      */ 
/*      */     public void popContext()
/*      */     {
/* 3371 */       this.fMatchersCount = this.fContextStack.pop();
/*      */     }
/*      */ 
/*      */     private void ensureMatcherCapacity()
/*      */     {
/* 3380 */       if (this.fMatchersCount == this.fMatchers.length) {
/* 3381 */         XPathMatcher[] array = new XPathMatcher[this.fMatchers.length * 2];
/* 3382 */         System.arraycopy(this.fMatchers, 0, array, 0, this.fMatchers.length);
/* 3383 */         this.fMatchers = array;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final class XSIErrorReporter
/*      */   {
/*      */     XMLErrorReporter fErrorReporter;
/*  377 */     Vector fErrors = new Vector();
/*  378 */     int[] fContext = new int[8];
/*      */     int fContextCount;
/*      */ 
/*      */     protected XSIErrorReporter()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void reset(XMLErrorReporter errorReporter)
/*      */     {
/*  383 */       this.fErrorReporter = errorReporter;
/*  384 */       this.fErrors.removeAllElements();
/*  385 */       this.fContextCount = 0;
/*      */     }
/*      */ 
/*      */     public void pushContext()
/*      */     {
/*  391 */       if (!XMLSchemaValidator.this.fAugPSVI) {
/*  392 */         return;
/*      */       }
/*      */ 
/*  395 */       if (this.fContextCount == this.fContext.length) {
/*  396 */         int newSize = this.fContextCount + 8;
/*  397 */         int[] newArray = new int[newSize];
/*  398 */         System.arraycopy(this.fContext, 0, newArray, 0, this.fContextCount);
/*  399 */         this.fContext = newArray;
/*      */       }
/*      */ 
/*  402 */       this.fContext[(this.fContextCount++)] = this.fErrors.size();
/*      */     }
/*      */ 
/*      */     public String[] popContext()
/*      */     {
/*  407 */       if (!XMLSchemaValidator.this.fAugPSVI) {
/*  408 */         return null;
/*      */       }
/*      */ 
/*  411 */       int contextPos = this.fContext[(--this.fContextCount)];
/*      */ 
/*  413 */       int size = this.fErrors.size() - contextPos;
/*      */ 
/*  415 */       if (size == 0) {
/*  416 */         return null;
/*      */       }
/*  418 */       String[] errors = new String[size];
/*  419 */       for (int i = 0; i < size; i++) {
/*  420 */         errors[i] = ((String)this.fErrors.elementAt(contextPos + i));
/*      */       }
/*      */ 
/*  423 */       this.fErrors.setSize(contextPos);
/*  424 */       return errors;
/*      */     }
/*      */ 
/*      */     public String[] mergeContext()
/*      */     {
/*  431 */       if (!XMLSchemaValidator.this.fAugPSVI) {
/*  432 */         return null;
/*      */       }
/*      */ 
/*  435 */       int contextPos = this.fContext[(--this.fContextCount)];
/*      */ 
/*  437 */       int size = this.fErrors.size() - contextPos;
/*      */ 
/*  439 */       if (size == 0) {
/*  440 */         return null;
/*      */       }
/*  442 */       String[] errors = new String[size];
/*  443 */       for (int i = 0; i < size; i++) {
/*  444 */         errors[i] = ((String)this.fErrors.elementAt(contextPos + i));
/*      */       }
/*      */ 
/*  448 */       return errors;
/*      */     }
/*      */ 
/*      */     public void reportError(String domain, String key, Object[] arguments, short severity) throws XNIException
/*      */     {
/*  453 */       this.fErrorReporter.reportError(domain, key, arguments, severity);
/*  454 */       if (XMLSchemaValidator.this.fAugPSVI)
/*  455 */         this.fErrors.addElement(key);
/*      */     }
/*      */ 
/*      */     public void reportError(XMLLocator location, String domain, String key, Object[] arguments, short severity)
/*      */       throws XNIException
/*      */     {
/*  466 */       this.fErrorReporter.reportError(location, domain, key, arguments, severity);
/*  467 */       if (XMLSchemaValidator.this.fAugPSVI)
/*  468 */         this.fErrors.addElement(key);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator
 * JD-Core Version:    0.6.2
 */