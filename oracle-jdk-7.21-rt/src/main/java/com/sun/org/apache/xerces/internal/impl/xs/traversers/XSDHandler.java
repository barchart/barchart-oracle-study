/*      */ package com.sun.org.apache.xerces.internal.impl.xs.traversers;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar.BuiltinSchemaGrammar;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar.Schema4Annotations;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSDDescription;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSGrammarBucket;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSGroupDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSNotationDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOM;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaParsingConfig;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.util.XSInputSource;
/*      */ import com.sun.org.apache.xerces.internal.parsers.SAXParser;
/*      */ import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMInputSource;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMUtil;
/*      */ import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
/*      */ import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.SAXInputSource;
/*      */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*      */ import com.sun.org.apache.xerces.internal.util.StAXInputSource;
/*      */ import com.sun.org.apache.xerces.internal.util.StAXLocationWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolHash;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
/*      */ import com.sun.org.apache.xerces.internal.xs.StringList;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSObject;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSObjectList;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSParticle;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSTerm;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
/*      */ import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
/*      */ import java.io.IOException;
/*      */ import java.io.StringReader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Stack;
/*      */ import java.util.Vector;
/*      */ import javax.xml.stream.XMLEventReader;
/*      */ import javax.xml.stream.XMLStreamException;
/*      */ import javax.xml.stream.XMLStreamReader;
/*      */ import javax.xml.stream.events.XMLEvent;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.SAXParseException;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.helpers.XMLReaderFactory;
/*      */ 
/*      */ public class XSDHandler
/*      */ {
/*      */   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
/*      */   protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
/*      */   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
/*      */   protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
/*      */   protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
/*      */   protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
/*      */   protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
/*      */   protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
/*      */   protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
/*      */   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
/*      */   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
/*      */   private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
/*      */   protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
/*      */   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
/*      */   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
/*      */   public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*      */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*      */   public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*      */   public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*      */   private static final String SECURE_PROCESSING = "http://apache.org/xml/properties/security-manager";
/*      */   protected static final String LOCALE = "http://apache.org/xml/properties/locale";
/*      */   protected static final boolean DEBUG_NODE_POOL = false;
/*      */   static final int ATTRIBUTE_TYPE = 1;
/*      */   static final int ATTRIBUTEGROUP_TYPE = 2;
/*      */   static final int ELEMENT_TYPE = 3;
/*      */   static final int GROUP_TYPE = 4;
/*      */   static final int IDENTITYCONSTRAINT_TYPE = 5;
/*      */   static final int NOTATION_TYPE = 6;
/*      */   static final int TYPEDECL_TYPE = 7;
/*      */   public static final String REDEF_IDENTIFIER = "_fn3dktizrknc9pi";
/*  245 */   protected XSDeclarationPool fDeclPool = null;
/*      */ 
/*  252 */   protected SecurityManager fSecureProcessing = null;
/*      */ 
/*  261 */   private boolean registryEmpty = true;
/*  262 */   private Map<String, Element> fUnparsedAttributeRegistry = new HashMap();
/*  263 */   private Map<String, Element> fUnparsedAttributeGroupRegistry = new HashMap();
/*  264 */   private Map<String, Element> fUnparsedElementRegistry = new HashMap();
/*  265 */   private Map<String, Element> fUnparsedGroupRegistry = new HashMap();
/*  266 */   private Map<String, Element> fUnparsedIdentityConstraintRegistry = new HashMap();
/*  267 */   private Map<String, Element> fUnparsedNotationRegistry = new HashMap();
/*  268 */   private Map<String, Element> fUnparsedTypeRegistry = new HashMap();
/*      */ 
/*  272 */   private Map<String, XSDocumentInfo> fUnparsedAttributeRegistrySub = new HashMap();
/*  273 */   private Map<String, XSDocumentInfo> fUnparsedAttributeGroupRegistrySub = new HashMap();
/*  274 */   private Map<String, XSDocumentInfo> fUnparsedElementRegistrySub = new HashMap();
/*  275 */   private Map<String, XSDocumentInfo> fUnparsedGroupRegistrySub = new HashMap();
/*  276 */   private Map<String, XSDocumentInfo> fUnparsedIdentityConstraintRegistrySub = new HashMap();
/*  277 */   private Map<String, XSDocumentInfo> fUnparsedNotationRegistrySub = new HashMap();
/*  278 */   private Map<String, XSDocumentInfo> fUnparsedTypeRegistrySub = new HashMap();
/*      */ 
/*  282 */   private Map[] fUnparsedRegistriesExt = { null, null, null, null, null, null, null, null };
/*      */ 
/*  296 */   private Map<XSDocumentInfo, Vector> fDependencyMap = new HashMap();
/*      */ 
/*  302 */   private Map<String, Vector> fImportMap = new HashMap();
/*      */ 
/*  306 */   private Vector fAllTNSs = new Vector();
/*      */ 
/*  308 */   private Map fLocationPairs = null;
/*  309 */   private static final Map EMPTY_TABLE = new HashMap();
/*      */ 
/*  312 */   Hashtable fHiddenNodes = null;
/*      */ 
/*  338 */   private Map fTraversed = new HashMap();
/*      */ 
/*  342 */   private Map fDoc2SystemId = new HashMap();
/*      */ 
/*  345 */   private XSDocumentInfo fRoot = null;
/*      */ 
/*  349 */   private Map fDoc2XSDocumentMap = new HashMap();
/*      */ 
/*  353 */   private Map fRedefine2XSDMap = null;
/*      */ 
/*  356 */   private Map fRedefine2NSSupport = null;
/*      */ 
/*  363 */   private Map fRedefinedRestrictedAttributeGroupRegistry = new HashMap();
/*  364 */   private Map fRedefinedRestrictedGroupRegistry = new HashMap();
/*      */   private boolean fLastSchemaWasDuplicate;
/*  371 */   private boolean fValidateAnnotations = false;
/*      */ 
/*  374 */   private boolean fHonourAllSchemaLocations = false;
/*      */ 
/*  377 */   boolean fNamespaceGrowth = false;
/*      */ 
/*  380 */   boolean fTolerateDuplicates = false;
/*      */   private XMLErrorReporter fErrorReporter;
/*      */   private XMLEntityResolver fEntityResolver;
/*      */   private XSAttributeChecker fAttributeChecker;
/*      */   private SymbolTable fSymbolTable;
/*      */   private XSGrammarBucket fGrammarBucket;
/*      */   private XSDDescription fSchemaGrammarDescription;
/*      */   private XMLGrammarPool fGrammarPool;
/*      */   XSDAttributeGroupTraverser fAttributeGroupTraverser;
/*      */   XSDAttributeTraverser fAttributeTraverser;
/*      */   XSDComplexTypeTraverser fComplexTypeTraverser;
/*      */   XSDElementTraverser fElementTraverser;
/*      */   XSDGroupTraverser fGroupTraverser;
/*      */   XSDKeyrefTraverser fKeyrefTraverser;
/*      */   XSDNotationTraverser fNotationTraverser;
/*      */   XSDSimpleTypeTraverser fSimpleTypeTraverser;
/*      */   XSDUniqueOrKeyTraverser fUniqueOrKeyTraverser;
/*      */   XSDWildcardTraverser fWildCardTraverser;
/*      */   SchemaDVFactory fDVFactory;
/*      */   SchemaDOMParser fSchemaParser;
/*      */   SchemaContentHandler fXSContentHandler;
/*      */   StAXSchemaParser fStAXSchemaParser;
/*      */   XML11Configuration fAnnotationValidator;
/*      */   XSAnnotationGrammarPool fGrammarBucketAdapter;
/*      */   private static final int INIT_STACK_SIZE = 30;
/*      */   private static final int INC_STACK_SIZE = 10;
/*  428 */   private int fLocalElemStackPos = 0;
/*      */ 
/*  430 */   private XSParticleDecl[] fParticle = new XSParticleDecl[30];
/*  431 */   private Element[] fLocalElementDecl = new Element[30];
/*  432 */   private XSDocumentInfo[] fLocalElementDecl_schema = new XSDocumentInfo[30];
/*  433 */   private int[] fAllContext = new int[30];
/*  434 */   private XSObject[] fParent = new XSObject[30];
/*  435 */   private String[][] fLocalElemNamespaceContext = new String[30][1];
/*      */   private static final int INIT_KEYREF_STACK = 2;
/*      */   private static final int INC_KEYREF_STACK_AMOUNT = 2;
/*  445 */   private int fKeyrefStackPos = 0;
/*      */ 
/*  447 */   private Element[] fKeyrefs = new Element[2];
/*  448 */   private XSDocumentInfo[] fKeyrefsMapXSDocumentInfo = new XSDocumentInfo[2];
/*  449 */   private XSElementDecl[] fKeyrefElems = new XSElementDecl[2];
/*  450 */   private String[][] fKeyrefNamespaceContext = new String[2][1];
/*      */ 
/*  453 */   SymbolHash fGlobalAttrDecls = new SymbolHash();
/*  454 */   SymbolHash fGlobalAttrGrpDecls = new SymbolHash();
/*  455 */   SymbolHash fGlobalElemDecls = new SymbolHash();
/*  456 */   SymbolHash fGlobalGroupDecls = new SymbolHash();
/*  457 */   SymbolHash fGlobalNotationDecls = new SymbolHash();
/*  458 */   SymbolHash fGlobalIDConstraintDecls = new SymbolHash();
/*  459 */   SymbolHash fGlobalTypeDecls = new SymbolHash();
/*      */ 
/*  737 */   private static final String[][] NS_ERROR_CODES = { { "src-include.2.1", "src-include.2.1" }, { "src-redefine.3.1", "src-redefine.3.1" }, { "src-import.3.1", "src-import.3.2" }, null, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" }, { "TargetNamespace.1", "TargetNamespace.2" } };
/*      */ 
/*  748 */   private static final String[] ELE_ERROR_CODES = { "src-include.1", "src-redefine.2", "src-import.2", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4" };
/*      */ 
/* 1472 */   private Vector fReportedTNS = null;
/*      */ 
/* 1485 */   private static final String[] COMP_TYPE = { null, "attribute declaration", "attribute group", "element declaration", "group", "identity constraint", "notation", "type definition" };
/*      */ 
/* 1496 */   private static final String[] CIRCULAR_CODES = { "Internal-Error", "Internal-Error", "src-attribute_group.3", "e-props-correct.6", "mg-props-correct.2", "Internal-Error", "Internal-Error", "st-props-correct.2" };
/*      */ 
/* 4050 */   private SimpleLocator xl = new SimpleLocator();
/*      */ 
/*      */   private String null2EmptyString(String ns)
/*      */   {
/*  316 */     return ns == null ? XMLSymbols.EMPTY_STRING : ns;
/*      */   }
/*      */   private String emptyString2Null(String ns) {
/*  319 */     return ns == XMLSymbols.EMPTY_STRING ? null : ns;
/*      */   }
/*      */ 
/*      */   private String doc2SystemId(Element ele) {
/*  323 */     String documentURI = null;
/*      */ 
/*  327 */     if ((ele.getOwnerDocument() instanceof SchemaDOM)) {
/*  328 */       documentURI = ((SchemaDOM)ele.getOwnerDocument()).getDocumentURI();
/*      */     }
/*  330 */     return documentURI != null ? documentURI : (String)this.fDoc2SystemId.get(ele);
/*      */   }
/*      */ 
/*      */   public XSDHandler()
/*      */   {
/*  463 */     this.fHiddenNodes = new Hashtable();
/*  464 */     this.fSchemaParser = new SchemaDOMParser(new SchemaParsingConfig());
/*      */   }
/*      */ 
/*      */   public XSDHandler(XSGrammarBucket gBucket)
/*      */   {
/*  471 */     this();
/*  472 */     this.fGrammarBucket = gBucket;
/*      */ 
/*  477 */     this.fSchemaGrammarDescription = new XSDDescription();
/*      */   }
/*      */ 
/*      */   public SchemaGrammar parseSchema(XMLInputSource is, XSDDescription desc, Map locationPairs)
/*      */     throws IOException
/*      */   {
/*  494 */     this.fLocationPairs = locationPairs;
/*  495 */     this.fSchemaParser.resetNodePool();
/*  496 */     SchemaGrammar grammar = null;
/*  497 */     String schemaNamespace = null;
/*  498 */     short referType = desc.getContextType();
/*      */ 
/*  506 */     if (referType != 3)
/*      */     {
/*  508 */       if ((this.fHonourAllSchemaLocations) && (referType == 2) && (isExistingGrammar(desc, this.fNamespaceGrowth))) {
/*  509 */         grammar = this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
/*      */       }
/*      */       else {
/*  512 */         grammar = findGrammar(desc, this.fNamespaceGrowth);
/*      */       }
/*  514 */       if (grammar != null) {
/*  515 */         if (!this.fNamespaceGrowth) {
/*  516 */           return grammar;
/*      */         }
/*      */         try
/*      */         {
/*  520 */           if (grammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(is.getSystemId(), is.getBaseSystemId(), false))) {
/*  521 */             return grammar;
/*      */           }
/*      */         }
/*      */         catch (URI.MalformedURIException e)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  530 */       schemaNamespace = desc.getTargetNamespace();
/*      */ 
/*  532 */       if (schemaNamespace != null) {
/*  533 */         schemaNamespace = this.fSymbolTable.addSymbol(schemaNamespace);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  539 */     prepareForParse();
/*      */ 
/*  541 */     Element schemaRoot = null;
/*      */ 
/*  543 */     if ((is instanceof DOMInputSource)) {
/*  544 */       schemaRoot = getSchemaDocument(schemaNamespace, (DOMInputSource)is, referType == 3, referType, null);
/*      */     }
/*  548 */     else if ((is instanceof SAXInputSource)) {
/*  549 */       schemaRoot = getSchemaDocument(schemaNamespace, (SAXInputSource)is, referType == 3, referType, null);
/*      */     }
/*  553 */     else if ((is instanceof StAXInputSource)) {
/*  554 */       schemaRoot = getSchemaDocument(schemaNamespace, (StAXInputSource)is, referType == 3, referType, null);
/*      */     }
/*  558 */     else if ((is instanceof XSInputSource)) {
/*  559 */       schemaRoot = getSchemaDocument((XSInputSource)is, desc);
/*      */     }
/*      */     else {
/*  562 */       schemaRoot = getSchemaDocument(schemaNamespace, is, referType == 3, referType, null);
/*      */     }
/*      */ 
/*  568 */     if (schemaRoot == null)
/*      */     {
/*  570 */       if ((is instanceof XSInputSource)) {
/*  571 */         return this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
/*      */       }
/*  573 */       return grammar;
/*      */     }
/*      */ 
/*  576 */     if (referType == 3) {
/*  577 */       Element schemaElem = schemaRoot;
/*  578 */       schemaNamespace = DOMUtil.getAttrValue(schemaElem, SchemaSymbols.ATT_TARGETNAMESPACE);
/*  579 */       if ((schemaNamespace != null) && (schemaNamespace.length() > 0))
/*      */       {
/*  582 */         schemaNamespace = this.fSymbolTable.addSymbol(schemaNamespace);
/*  583 */         desc.setTargetNamespace(schemaNamespace);
/*      */       }
/*      */       else {
/*  586 */         schemaNamespace = null;
/*      */       }
/*  588 */       grammar = findGrammar(desc, this.fNamespaceGrowth);
/*  589 */       String schemaId = XMLEntityManager.expandSystemId(is.getSystemId(), is.getBaseSystemId(), false);
/*  590 */       if (grammar != null)
/*      */       {
/*  593 */         if ((!this.fNamespaceGrowth) || ((schemaId != null) && (grammar.getDocumentLocations().contains(schemaId)))) {
/*  594 */           return grammar;
/*      */         }
/*      */       }
/*      */ 
/*  598 */       XSDKey key = new XSDKey(schemaId, referType, schemaNamespace);
/*  599 */       this.fTraversed.put(key, schemaRoot);
/*  600 */       if (schemaId != null) {
/*  601 */         this.fDoc2SystemId.put(schemaRoot, schemaId);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  607 */     prepareForTraverse();
/*      */ 
/*  609 */     this.fRoot = constructTrees(schemaRoot, is.getSystemId(), desc, grammar != null);
/*  610 */     if (this.fRoot == null) {
/*  611 */       return null;
/*      */     }
/*      */ 
/*  615 */     buildGlobalNameRegistries();
/*      */ 
/*  618 */     ArrayList annotationInfo = this.fValidateAnnotations ? new ArrayList() : null;
/*  619 */     traverseSchemas(annotationInfo);
/*      */ 
/*  622 */     traverseLocalElements();
/*      */ 
/*  625 */     resolveKeyRefs();
/*      */ 
/*  633 */     for (int i = this.fAllTNSs.size() - 1; i >= 0; i--)
/*      */     {
/*  635 */       String tns = (String)this.fAllTNSs.elementAt(i);
/*      */ 
/*  637 */       Vector ins = (Vector)this.fImportMap.get(tns);
/*      */ 
/*  639 */       SchemaGrammar sg = this.fGrammarBucket.getGrammar(emptyString2Null(tns));
/*  640 */       if (sg != null)
/*      */       {
/*  644 */         int count = 0;
/*  645 */         for (int j = 0; j < ins.size(); j++)
/*      */         {
/*  647 */           SchemaGrammar isg = this.fGrammarBucket.getGrammar((String)ins.elementAt(j));
/*      */ 
/*  649 */           if (isg != null)
/*  650 */             ins.setElementAt(isg, count++);
/*      */         }
/*  652 */         ins.setSize(count);
/*      */ 
/*  654 */         sg.setImportedGrammars(ins);
/*      */       }
/*      */     }
/*      */ 
/*  658 */     if ((this.fValidateAnnotations) && (annotationInfo.size() > 0)) {
/*  659 */       validateAnnotations(annotationInfo);
/*      */     }
/*      */ 
/*  663 */     return this.fGrammarBucket.getGrammar(this.fRoot.fTargetNamespace);
/*      */   }
/*      */ 
/*      */   private void validateAnnotations(ArrayList annotationInfo) {
/*  667 */     if (this.fAnnotationValidator == null) {
/*  668 */       createAnnotationValidator();
/*      */     }
/*  670 */     int size = annotationInfo.size();
/*  671 */     XMLInputSource src = new XMLInputSource(null, null, null);
/*  672 */     this.fGrammarBucketAdapter.refreshGrammars(this.fGrammarBucket);
/*  673 */     for (int i = 0; i < size; i += 2) {
/*  674 */       src.setSystemId((String)annotationInfo.get(i));
/*  675 */       XSAnnotationInfo annotation = (XSAnnotationInfo)annotationInfo.get(i + 1);
/*  676 */       while (annotation != null) {
/*  677 */         src.setCharacterStream(new StringReader(annotation.fAnnotation));
/*      */         try {
/*  679 */           this.fAnnotationValidator.parse(src);
/*      */         } catch (IOException exc) {
/*      */         }
/*  682 */         annotation = annotation.next;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void createAnnotationValidator() {
/*  688 */     this.fAnnotationValidator = new XML11Configuration();
/*  689 */     this.fGrammarBucketAdapter = new XSAnnotationGrammarPool(null);
/*  690 */     this.fAnnotationValidator.setFeature("http://xml.org/sax/features/validation", true);
/*  691 */     this.fAnnotationValidator.setFeature("http://apache.org/xml/features/validation/schema", true);
/*  692 */     this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarBucketAdapter);
/*      */ 
/*  694 */     XMLErrorHandler errorHandler = this.fErrorReporter.getErrorHandler();
/*  695 */     this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", errorHandler != null ? errorHandler : new DefaultErrorHandler());
/*      */ 
/*  697 */     Locale locale = this.fErrorReporter.getLocale();
/*  698 */     this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", locale);
/*      */   }
/*      */ 
/*      */   SchemaGrammar getGrammar(String tns)
/*      */   {
/*  706 */     return this.fGrammarBucket.getGrammar(tns);
/*      */   }
/*      */ 
/*      */   protected SchemaGrammar findGrammar(XSDDescription desc, boolean ignoreConflict)
/*      */   {
/*  715 */     SchemaGrammar sg = this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
/*  716 */     if ((sg == null) && 
/*  717 */       (this.fGrammarPool != null)) {
/*  718 */       sg = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(desc);
/*  719 */       if (sg != null)
/*      */       {
/*  722 */         if (!this.fGrammarBucket.putGrammar(sg, true, ignoreConflict))
/*      */         {
/*  725 */           reportSchemaWarning("GrammarConflict", null, null);
/*  726 */           sg = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  731 */     return sg;
/*      */   }
/*      */ 
/*      */   protected XSDocumentInfo constructTrees(Element schemaRoot, String locationHint, XSDDescription desc, boolean nsCollision)
/*      */   {
/*  764 */     if (schemaRoot == null) return null;
/*  765 */     String callerTNS = desc.getTargetNamespace();
/*  766 */     short referType = desc.getContextType();
/*      */ 
/*  768 */     XSDocumentInfo currSchemaInfo = null;
/*      */     try
/*      */     {
/*  771 */       currSchemaInfo = new XSDocumentInfo(schemaRoot, this.fAttributeChecker, this.fSymbolTable);
/*      */     } catch (XMLSchemaException se) {
/*  773 */       reportSchemaError(ELE_ERROR_CODES[referType], new Object[] { locationHint }, schemaRoot);
/*      */ 
/*  776 */       return null;
/*      */     }
/*      */ 
/*  779 */     if ((currSchemaInfo.fTargetNamespace != null) && (currSchemaInfo.fTargetNamespace.length() == 0))
/*      */     {
/*  781 */       reportSchemaWarning("EmptyTargetNamespace", new Object[] { locationHint }, schemaRoot);
/*      */ 
/*  784 */       currSchemaInfo.fTargetNamespace = null;
/*      */     }
/*      */ 
/*  787 */     if (callerTNS != null)
/*      */     {
/*  790 */       int secondIdx = 0;
/*      */ 
/*  792 */       if ((referType == 0) || (referType == 1))
/*      */       {
/*  796 */         if (currSchemaInfo.fTargetNamespace == null) {
/*  797 */           currSchemaInfo.fTargetNamespace = callerTNS;
/*  798 */           currSchemaInfo.fIsChameleonSchema = true;
/*      */         }
/*  802 */         else if (callerTNS != currSchemaInfo.fTargetNamespace) {
/*  803 */           reportSchemaError(NS_ERROR_CODES[referType][secondIdx], new Object[] { callerTNS, currSchemaInfo.fTargetNamespace }, schemaRoot);
/*      */ 
/*  806 */           return null;
/*      */         }
/*      */ 
/*      */       }
/*  810 */       else if ((referType != 3) && (callerTNS != currSchemaInfo.fTargetNamespace)) {
/*  811 */         reportSchemaError(NS_ERROR_CODES[referType][secondIdx], new Object[] { callerTNS, currSchemaInfo.fTargetNamespace }, schemaRoot);
/*      */ 
/*  814 */         return null;
/*      */       }
/*      */ 
/*      */     }
/*  819 */     else if (currSchemaInfo.fTargetNamespace != null)
/*      */     {
/*  821 */       if (referType == 3) {
/*  822 */         desc.setTargetNamespace(currSchemaInfo.fTargetNamespace);
/*  823 */         callerTNS = currSchemaInfo.fTargetNamespace;
/*      */       }
/*      */       else
/*      */       {
/*  828 */         int secondIdx = 1;
/*  829 */         reportSchemaError(NS_ERROR_CODES[referType][secondIdx], new Object[] { callerTNS, currSchemaInfo.fTargetNamespace }, schemaRoot);
/*      */ 
/*  832 */         return null;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  839 */     currSchemaInfo.addAllowedNS(currSchemaInfo.fTargetNamespace);
/*      */ 
/*  841 */     SchemaGrammar sg = null;
/*      */ 
/*  844 */     if (nsCollision) {
/*  845 */       SchemaGrammar sg2 = this.fGrammarBucket.getGrammar(currSchemaInfo.fTargetNamespace);
/*  846 */       if (sg2.isImmutable()) {
/*  847 */         sg = new SchemaGrammar(sg2);
/*  848 */         this.fGrammarBucket.putGrammar(sg);
/*      */ 
/*  850 */         updateImportListWith(sg);
/*      */       }
/*      */       else {
/*  853 */         sg = sg2;
/*      */       }
/*      */ 
/*  857 */       updateImportListFor(sg);
/*      */     }
/*  859 */     else if ((referType == 0) || (referType == 1))
/*      */     {
/*  861 */       sg = this.fGrammarBucket.getGrammar(currSchemaInfo.fTargetNamespace);
/*      */     }
/*  863 */     else if ((this.fHonourAllSchemaLocations) && (referType == 2)) {
/*  864 */       sg = findGrammar(desc, false);
/*  865 */       if (sg == null) {
/*  866 */         sg = new SchemaGrammar(currSchemaInfo.fTargetNamespace, desc.makeClone(), this.fSymbolTable);
/*  867 */         this.fGrammarBucket.putGrammar(sg);
/*      */       }
/*      */     }
/*      */     else {
/*  871 */       sg = new SchemaGrammar(currSchemaInfo.fTargetNamespace, desc.makeClone(), this.fSymbolTable);
/*  872 */       this.fGrammarBucket.putGrammar(sg);
/*      */     }
/*      */ 
/*  877 */     sg.addDocument(null, (String)this.fDoc2SystemId.get(currSchemaInfo.fSchemaElement));
/*      */ 
/*  879 */     this.fDoc2XSDocumentMap.put(schemaRoot, currSchemaInfo);
/*  880 */     Vector dependencies = new Vector();
/*  881 */     Element rootNode = schemaRoot;
/*      */ 
/*  883 */     Element newSchemaRoot = null;
/*  884 */     for (Element child = DOMUtil.getFirstChildElement(rootNode); 
/*  885 */       child != null; 
/*  886 */       child = DOMUtil.getNextSiblingElement(child)) {
/*  887 */       String schemaNamespace = null;
/*  888 */       String schemaHint = null;
/*  889 */       String localName = DOMUtil.getLocalName(child);
/*      */ 
/*  891 */       short refType = -1;
/*  892 */       boolean importCollision = false;
/*      */ 
/*  894 */       if (!localName.equals(SchemaSymbols.ELT_ANNOTATION))
/*      */       {
/*  896 */         if (localName.equals(SchemaSymbols.ELT_IMPORT)) {
/*  897 */           refType = 2;
/*      */ 
/*  900 */           Object[] importAttrs = this.fAttributeChecker.checkAttributes(child, true, currSchemaInfo);
/*  901 */           schemaHint = (String)importAttrs[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
/*  902 */           schemaNamespace = (String)importAttrs[XSAttributeChecker.ATTIDX_NAMESPACE];
/*  903 */           if (schemaNamespace != null) {
/*  904 */             schemaNamespace = this.fSymbolTable.addSymbol(schemaNamespace);
/*      */           }
/*      */ 
/*  907 */           Element importChild = DOMUtil.getFirstChildElement(child);
/*  908 */           if (importChild != null) {
/*  909 */             String importComponentType = DOMUtil.getLocalName(importChild);
/*  910 */             if (importComponentType.equals(SchemaSymbols.ELT_ANNOTATION))
/*      */             {
/*  912 */               sg.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(importChild, importAttrs, true, currSchemaInfo));
/*      */             }
/*      */             else {
/*  915 */               reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", importComponentType }, child);
/*      */             }
/*  917 */             if (DOMUtil.getNextSiblingElement(importChild) != null)
/*  918 */               reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(importChild)) }, child);
/*      */           }
/*      */           else
/*      */           {
/*  922 */             String text = DOMUtil.getSyntheticAnnotation(child);
/*  923 */             if (text != null) {
/*  924 */               sg.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(child, text, importAttrs, true, currSchemaInfo));
/*      */             }
/*      */           }
/*  927 */           this.fAttributeChecker.returnAttrArray(importAttrs, currSchemaInfo);
/*      */ 
/*  930 */           if (schemaNamespace == currSchemaInfo.fTargetNamespace) {
/*  931 */             reportSchemaError(schemaNamespace != null ? "src-import.1.1" : "src-import.1.2", new Object[] { schemaNamespace }, child);
/*      */ 
/*  933 */             continue;
/*      */           }
/*      */ 
/*  938 */           if (currSchemaInfo.isAllowedNS(schemaNamespace)) {
/*  939 */             if ((!this.fHonourAllSchemaLocations) && (!this.fNamespaceGrowth))
/*  940 */               continue;
/*      */           }
/*      */           else {
/*  943 */             currSchemaInfo.addAllowedNS(schemaNamespace);
/*      */           }
/*      */ 
/*  947 */           String tns = null2EmptyString(currSchemaInfo.fTargetNamespace);
/*      */ 
/*  949 */           Vector ins = (Vector)this.fImportMap.get(tns);
/*      */ 
/*  951 */           if (ins == null)
/*      */           {
/*  953 */             this.fAllTNSs.addElement(tns);
/*  954 */             ins = new Vector();
/*  955 */             this.fImportMap.put(tns, ins);
/*  956 */             ins.addElement(schemaNamespace);
/*      */           }
/*  958 */           else if (!ins.contains(schemaNamespace)) {
/*  959 */             ins.addElement(schemaNamespace);
/*      */           }
/*      */ 
/*  962 */           this.fSchemaGrammarDescription.reset();
/*  963 */           this.fSchemaGrammarDescription.setContextType((short)2);
/*  964 */           this.fSchemaGrammarDescription.setBaseSystemId(doc2SystemId(schemaRoot));
/*  965 */           this.fSchemaGrammarDescription.setLiteralSystemId(schemaHint);
/*  966 */           this.fSchemaGrammarDescription.setLocationHints(new String[] { schemaHint });
/*  967 */           this.fSchemaGrammarDescription.setTargetNamespace(schemaNamespace);
/*      */ 
/*  971 */           SchemaGrammar isg = findGrammar(this.fSchemaGrammarDescription, this.fNamespaceGrowth);
/*  972 */           if (isg != null) {
/*  973 */             if (this.fNamespaceGrowth)
/*      */               try {
/*  975 */                 if (isg.getDocumentLocations().contains(XMLEntityManager.expandSystemId(schemaHint, this.fSchemaGrammarDescription.getBaseSystemId(), false)))
/*      */                 {
/*      */                   continue;
/*      */                 }
/*  979 */                 importCollision = true;
/*      */               }
/*      */               catch (URI.MalformedURIException e)
/*      */               {
/*      */               }
/*      */             else {
/*  985 */               if ((!this.fHonourAllSchemaLocations) || (isExistingGrammar(this.fSchemaGrammarDescription, false)))
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  997 */           newSchemaRoot = resolveSchema(this.fSchemaGrammarDescription, false, child, isg == null);
/*      */         } else {
/*  999 */           if ((!localName.equals(SchemaSymbols.ELT_INCLUDE)) && (!localName.equals(SchemaSymbols.ELT_REDEFINE)))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1004 */           Object[] includeAttrs = this.fAttributeChecker.checkAttributes(child, true, currSchemaInfo);
/* 1005 */           schemaHint = (String)includeAttrs[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
/*      */ 
/* 1007 */           if (localName.equals(SchemaSymbols.ELT_REDEFINE)) {
/* 1008 */             if (this.fRedefine2NSSupport == null) this.fRedefine2NSSupport = new HashMap();
/* 1009 */             this.fRedefine2NSSupport.put(child, new SchemaNamespaceSupport(currSchemaInfo.fNamespaceSupport));
/*      */           }
/*      */ 
/* 1014 */           if (localName.equals(SchemaSymbols.ELT_INCLUDE)) {
/* 1015 */             Element includeChild = DOMUtil.getFirstChildElement(child);
/* 1016 */             if (includeChild != null) {
/* 1017 */               String includeComponentType = DOMUtil.getLocalName(includeChild);
/* 1018 */               if (includeComponentType.equals(SchemaSymbols.ELT_ANNOTATION))
/*      */               {
/* 1020 */                 sg.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(includeChild, includeAttrs, true, currSchemaInfo));
/*      */               }
/*      */               else {
/* 1023 */                 reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", includeComponentType }, child);
/*      */               }
/* 1025 */               if (DOMUtil.getNextSiblingElement(includeChild) != null)
/* 1026 */                 reportSchemaError("s4s-elt-must-match.1", new Object[] { localName, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(includeChild)) }, child);
/*      */             }
/*      */             else
/*      */             {
/* 1030 */               String text = DOMUtil.getSyntheticAnnotation(child);
/* 1031 */               if (text != null)
/* 1032 */                 sg.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(child, text, includeAttrs, true, currSchemaInfo));
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1037 */             for (Element redefinedChild = DOMUtil.getFirstChildElement(child); 
/* 1038 */               redefinedChild != null; 
/* 1039 */               redefinedChild = DOMUtil.getNextSiblingElement(redefinedChild)) {
/* 1040 */               String redefinedComponentType = DOMUtil.getLocalName(redefinedChild);
/* 1041 */               if (redefinedComponentType.equals(SchemaSymbols.ELT_ANNOTATION))
/*      */               {
/* 1043 */                 sg.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(redefinedChild, includeAttrs, true, currSchemaInfo));
/*      */ 
/* 1045 */                 DOMUtil.setHidden(redefinedChild, this.fHiddenNodes);
/*      */               }
/*      */               else {
/* 1048 */                 String text = DOMUtil.getSyntheticAnnotation(child);
/* 1049 */                 if (text != null) {
/* 1050 */                   sg.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(child, text, includeAttrs, true, currSchemaInfo));
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 1056 */           this.fAttributeChecker.returnAttrArray(includeAttrs, currSchemaInfo);
/*      */ 
/* 1058 */           if (schemaHint == null) {
/* 1059 */             reportSchemaError("s4s-att-must-appear", new Object[] { "<include> or <redefine>", "schemaLocation" }, child);
/*      */           }
/*      */ 
/* 1064 */           boolean mustResolve = false;
/* 1065 */           refType = 0;
/* 1066 */           if (localName.equals(SchemaSymbols.ELT_REDEFINE)) {
/* 1067 */             mustResolve = nonAnnotationContent(child);
/* 1068 */             refType = 1;
/*      */           }
/* 1070 */           this.fSchemaGrammarDescription.reset();
/* 1071 */           this.fSchemaGrammarDescription.setContextType(refType);
/* 1072 */           this.fSchemaGrammarDescription.setBaseSystemId(doc2SystemId(schemaRoot));
/* 1073 */           this.fSchemaGrammarDescription.setLocationHints(new String[] { schemaHint });
/* 1074 */           this.fSchemaGrammarDescription.setTargetNamespace(callerTNS);
/*      */ 
/* 1076 */           boolean alreadyTraversed = false;
/* 1077 */           XMLInputSource schemaSource = resolveSchemaSource(this.fSchemaGrammarDescription, mustResolve, child, true);
/* 1078 */           if ((this.fNamespaceGrowth) && (refType == 0)) {
/*      */             try {
/* 1080 */               String schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
/* 1081 */               alreadyTraversed = sg.getDocumentLocations().contains(schemaId);
/*      */             }
/*      */             catch (URI.MalformedURIException e)
/*      */             {
/*      */             }
/*      */           }
/*      */ 
/* 1088 */           if (!alreadyTraversed) {
/* 1089 */             newSchemaRoot = resolveSchema(schemaSource, this.fSchemaGrammarDescription, mustResolve, child);
/* 1090 */             schemaNamespace = currSchemaInfo.fTargetNamespace;
/*      */           }
/*      */           else {
/* 1093 */             this.fLastSchemaWasDuplicate = true;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1104 */         XSDocumentInfo newSchemaInfo = null;
/* 1105 */         if (this.fLastSchemaWasDuplicate) {
/* 1106 */           newSchemaInfo = newSchemaRoot == null ? null : (XSDocumentInfo)this.fDoc2XSDocumentMap.get(newSchemaRoot);
/*      */         }
/*      */         else {
/* 1109 */           newSchemaInfo = constructTrees(newSchemaRoot, schemaHint, this.fSchemaGrammarDescription, importCollision);
/*      */         }
/*      */ 
/* 1112 */         if ((localName.equals(SchemaSymbols.ELT_REDEFINE)) && (newSchemaInfo != null))
/*      */         {
/* 1116 */           if (this.fRedefine2XSDMap == null) this.fRedefine2XSDMap = new HashMap();
/* 1117 */           this.fRedefine2XSDMap.put(child, newSchemaInfo);
/*      */         }
/* 1119 */         if (newSchemaRoot != null) {
/* 1120 */           if (newSchemaInfo != null)
/* 1121 */             dependencies.addElement(newSchemaInfo);
/* 1122 */           newSchemaRoot = null;
/*      */         }
/*      */       }
/*      */     }
/* 1126 */     this.fDependencyMap.put(currSchemaInfo, dependencies);
/* 1127 */     return currSchemaInfo;
/*      */   }
/*      */ 
/*      */   private boolean isExistingGrammar(XSDDescription desc, boolean ignoreConflict) {
/* 1131 */     SchemaGrammar sg = this.fGrammarBucket.getGrammar(desc.getTargetNamespace());
/* 1132 */     if (sg == null) {
/* 1133 */       return findGrammar(desc, ignoreConflict) != null;
/*      */     }
/* 1135 */     if (sg.isImmutable()) {
/* 1136 */       return true;
/*      */     }
/*      */     try
/*      */     {
/* 1140 */       return sg.getDocumentLocations().contains(XMLEntityManager.expandSystemId(desc.getLiteralSystemId(), desc.getBaseSystemId(), false));
/*      */     } catch (URI.MalformedURIException e) {
/*      */     }
/* 1143 */     return false;
/*      */   }
/*      */ 
/*      */   private void updateImportListFor(SchemaGrammar grammar)
/*      */   {
/* 1157 */     Vector importedGrammars = grammar.getImportedGrammars();
/* 1158 */     if (importedGrammars != null)
/* 1159 */       for (int i = 0; i < importedGrammars.size(); i++) {
/* 1160 */         SchemaGrammar isg1 = (SchemaGrammar)importedGrammars.elementAt(i);
/* 1161 */         SchemaGrammar isg2 = this.fGrammarBucket.getGrammar(isg1.getTargetNamespace());
/* 1162 */         if ((isg2 != null) && (isg1 != isg2))
/* 1163 */           importedGrammars.set(i, isg2);
/*      */       }
/*      */   }
/*      */ 
/*      */   private void updateImportListWith(SchemaGrammar newGrammar)
/*      */   {
/* 1179 */     SchemaGrammar[] schemaGrammars = this.fGrammarBucket.getGrammars();
/* 1180 */     for (int i = 0; i < schemaGrammars.length; i++) {
/* 1181 */       SchemaGrammar sg = schemaGrammars[i];
/* 1182 */       if (sg != newGrammar) {
/* 1183 */         Vector importedGrammars = sg.getImportedGrammars();
/* 1184 */         if (importedGrammars != null)
/* 1185 */           for (int j = 0; j < importedGrammars.size(); j++) {
/* 1186 */             SchemaGrammar isg = (SchemaGrammar)importedGrammars.elementAt(j);
/* 1187 */             if (null2EmptyString(isg.getTargetNamespace()).equals(null2EmptyString(newGrammar.getTargetNamespace()))) {
/* 1188 */               if (isg == newGrammar) break;
/* 1189 */               importedGrammars.set(j, newGrammar); break;
/*      */             }
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void buildGlobalNameRegistries()
/*      */   {
/* 1206 */     this.registryEmpty = false;
/*      */ 
/* 1217 */     Stack schemasToProcess = new Stack();
/* 1218 */     schemasToProcess.push(this.fRoot);
/*      */ 
/* 1220 */     while (!schemasToProcess.empty()) {
/* 1221 */       XSDocumentInfo currSchemaDoc = (XSDocumentInfo)schemasToProcess.pop();
/*      */ 
/* 1223 */       Element currDoc = currSchemaDoc.fSchemaElement;
/* 1224 */       if (!DOMUtil.isHidden(currDoc, this.fHiddenNodes))
/*      */       {
/* 1229 */         Element currRoot = currDoc;
/*      */ 
/* 1231 */         boolean dependenciesCanOccur = true;
/* 1232 */         for (Element globalComp = DOMUtil.getFirstChildElement(currRoot); 
/* 1234 */           globalComp != null; 
/* 1235 */           globalComp = DOMUtil.getNextSiblingElement(globalComp))
/*      */         {
/* 1238 */           if (!DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_ANNOTATION))
/*      */           {
/* 1242 */             if ((DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_INCLUDE)) || (DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_IMPORT)))
/*      */             {
/* 1244 */               if (!dependenciesCanOccur) {
/* 1245 */                 reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName(globalComp) }, globalComp);
/*      */               }
/* 1247 */               DOMUtil.setHidden(globalComp, this.fHiddenNodes);
/*      */             }
/* 1249 */             else if (DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_REDEFINE)) {
/* 1250 */               if (!dependenciesCanOccur) {
/* 1251 */                 reportSchemaError("s4s-elt-invalid-content.3", new Object[] { DOMUtil.getLocalName(globalComp) }, globalComp);
/*      */               }
/* 1253 */               for (Element redefineComp = DOMUtil.getFirstChildElement(globalComp); 
/* 1254 */                 redefineComp != null; 
/* 1255 */                 redefineComp = DOMUtil.getNextSiblingElement(redefineComp)) {
/* 1256 */                 String lName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME);
/* 1257 */                 if (lName.length() != 0)
/*      */                 {
/* 1259 */                   String qName = currSchemaDoc.fTargetNamespace + "," + lName;
/*      */ 
/* 1262 */                   String componentType = DOMUtil.getLocalName(redefineComp);
/* 1263 */                   if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
/* 1264 */                     checkForDuplicateNames(qName, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, redefineComp, currSchemaDoc);
/*      */ 
/* 1266 */                     String targetLName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
/*      */ 
/* 1268 */                     renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_ATTRIBUTEGROUP, lName, targetLName);
/*      */                   }
/* 1271 */                   else if ((componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) || (componentType.equals(SchemaSymbols.ELT_SIMPLETYPE)))
/*      */                   {
/* 1273 */                     checkForDuplicateNames(qName, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, redefineComp, currSchemaDoc);
/*      */ 
/* 1275 */                     String targetLName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
/*      */ 
/* 1277 */                     if (componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
/* 1278 */                       renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_COMPLEXTYPE, lName, targetLName);
/*      */                     }
/*      */                     else
/*      */                     {
/* 1282 */                       renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_SIMPLETYPE, lName, targetLName);
/*      */                     }
/*      */ 
/*      */                   }
/* 1286 */                   else if (componentType.equals(SchemaSymbols.ELT_GROUP)) {
/* 1287 */                     checkForDuplicateNames(qName, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, redefineComp, currSchemaDoc);
/*      */ 
/* 1289 */                     String targetLName = DOMUtil.getAttrValue(redefineComp, SchemaSymbols.ATT_NAME) + "_fn3dktizrknc9pi";
/*      */ 
/* 1291 */                     renameRedefiningComponents(currSchemaDoc, redefineComp, SchemaSymbols.ELT_GROUP, lName, targetLName);
/*      */                   }
/*      */                 }
/*      */               }
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 1299 */               dependenciesCanOccur = false;
/* 1300 */               String lName = DOMUtil.getAttrValue(globalComp, SchemaSymbols.ATT_NAME);
/* 1301 */               if (lName.length() != 0)
/*      */               {
/* 1303 */                 String qName = currSchemaDoc.fTargetNamespace + "," + lName;
/*      */ 
/* 1306 */                 String componentType = DOMUtil.getLocalName(globalComp);
/*      */ 
/* 1308 */                 if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
/* 1309 */                   checkForDuplicateNames(qName, 1, this.fUnparsedAttributeRegistry, this.fUnparsedAttributeRegistrySub, globalComp, currSchemaDoc);
/*      */                 }
/* 1311 */                 else if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
/* 1312 */                   checkForDuplicateNames(qName, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, globalComp, currSchemaDoc);
/*      */                 }
/* 1314 */                 else if ((componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) || (componentType.equals(SchemaSymbols.ELT_SIMPLETYPE)))
/*      */                 {
/* 1316 */                   checkForDuplicateNames(qName, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, globalComp, currSchemaDoc);
/*      */                 }
/* 1318 */                 else if (componentType.equals(SchemaSymbols.ELT_ELEMENT)) {
/* 1319 */                   checkForDuplicateNames(qName, 3, this.fUnparsedElementRegistry, this.fUnparsedElementRegistrySub, globalComp, currSchemaDoc);
/*      */                 }
/* 1321 */                 else if (componentType.equals(SchemaSymbols.ELT_GROUP)) {
/* 1322 */                   checkForDuplicateNames(qName, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, globalComp, currSchemaDoc);
/*      */                 }
/* 1324 */                 else if (componentType.equals(SchemaSymbols.ELT_NOTATION)) {
/* 1325 */                   checkForDuplicateNames(qName, 6, this.fUnparsedNotationRegistry, this.fUnparsedNotationRegistrySub, globalComp, currSchemaDoc);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1331 */         DOMUtil.setHidden(currDoc, this.fHiddenNodes);
/*      */ 
/* 1333 */         Vector currSchemaDepends = (Vector)this.fDependencyMap.get(currSchemaDoc);
/* 1334 */         for (int i = 0; i < currSchemaDepends.size(); i++)
/* 1335 */           schemasToProcess.push(currSchemaDepends.elementAt(i));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void traverseSchemas(ArrayList annotationInfo)
/*      */   {
/* 1357 */     setSchemasVisible(this.fRoot);
/* 1358 */     Stack schemasToProcess = new Stack();
/* 1359 */     schemasToProcess.push(this.fRoot);
/* 1360 */     while (!schemasToProcess.empty()) {
/* 1361 */       XSDocumentInfo currSchemaDoc = (XSDocumentInfo)schemasToProcess.pop();
/*      */ 
/* 1363 */       Element currDoc = currSchemaDoc.fSchemaElement;
/*      */ 
/* 1365 */       SchemaGrammar currSG = this.fGrammarBucket.getGrammar(currSchemaDoc.fTargetNamespace);
/*      */ 
/* 1367 */       if (!DOMUtil.isHidden(currDoc, this.fHiddenNodes))
/*      */       {
/* 1371 */         Element currRoot = currDoc;
/* 1372 */         boolean sawAnnotation = false;
/*      */ 
/* 1374 */         for (Element globalComp = DOMUtil.getFirstVisibleChildElement(currRoot, this.fHiddenNodes); 
/* 1376 */           globalComp != null; 
/* 1377 */           globalComp = DOMUtil.getNextVisibleSiblingElement(globalComp, this.fHiddenNodes)) {
/* 1378 */           DOMUtil.setHidden(globalComp, this.fHiddenNodes);
/* 1379 */           String componentType = DOMUtil.getLocalName(globalComp);
/*      */ 
/* 1381 */           if (DOMUtil.getLocalName(globalComp).equals(SchemaSymbols.ELT_REDEFINE))
/*      */           {
/* 1383 */             currSchemaDoc.backupNSSupport(this.fRedefine2NSSupport != null ? (SchemaNamespaceSupport)this.fRedefine2NSSupport.get(globalComp) : null);
/* 1384 */             for (Element redefinedComp = DOMUtil.getFirstVisibleChildElement(globalComp, this.fHiddenNodes); 
/* 1385 */               redefinedComp != null; 
/* 1386 */               redefinedComp = DOMUtil.getNextVisibleSiblingElement(redefinedComp, this.fHiddenNodes)) {
/* 1387 */               String redefinedComponentType = DOMUtil.getLocalName(redefinedComp);
/* 1388 */               DOMUtil.setHidden(redefinedComp, this.fHiddenNodes);
/* 1389 */               if (redefinedComponentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
/* 1390 */                 this.fAttributeGroupTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
/*      */               }
/* 1392 */               else if (redefinedComponentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
/* 1393 */                 this.fComplexTypeTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
/*      */               }
/* 1395 */               else if (redefinedComponentType.equals(SchemaSymbols.ELT_GROUP)) {
/* 1396 */                 this.fGroupTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
/*      */               }
/* 1398 */               else if (redefinedComponentType.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
/* 1399 */                 this.fSimpleTypeTraverser.traverseGlobal(redefinedComp, currSchemaDoc, currSG);
/*      */               }
/*      */               else
/*      */               {
/* 1407 */                 reportSchemaError("s4s-elt-must-match.1", new Object[] { DOMUtil.getLocalName(globalComp), "(annotation | (simpleType | complexType | group | attributeGroup))*", redefinedComponentType }, redefinedComp);
/*      */               }
/*      */             }
/* 1410 */             currSchemaDoc.restoreNSSupport();
/*      */           }
/* 1412 */           else if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
/* 1413 */             this.fAttributeTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
/*      */           }
/* 1415 */           else if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
/* 1416 */             this.fAttributeGroupTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
/*      */           }
/* 1418 */           else if (componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
/* 1419 */             this.fComplexTypeTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
/*      */           }
/* 1421 */           else if (componentType.equals(SchemaSymbols.ELT_ELEMENT)) {
/* 1422 */             this.fElementTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
/*      */           }
/* 1424 */           else if (componentType.equals(SchemaSymbols.ELT_GROUP)) {
/* 1425 */             this.fGroupTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
/*      */           }
/* 1427 */           else if (componentType.equals(SchemaSymbols.ELT_NOTATION)) {
/* 1428 */             this.fNotationTraverser.traverse(globalComp, currSchemaDoc, currSG);
/*      */           }
/* 1430 */           else if (componentType.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
/* 1431 */             this.fSimpleTypeTraverser.traverseGlobal(globalComp, currSchemaDoc, currSG);
/*      */           }
/* 1433 */           else if (componentType.equals(SchemaSymbols.ELT_ANNOTATION)) {
/* 1434 */             currSG.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(globalComp, currSchemaDoc.getSchemaAttrs(), true, currSchemaDoc));
/* 1435 */             sawAnnotation = true;
/*      */           }
/*      */           else {
/* 1438 */             reportSchemaError("s4s-elt-invalid-content.1", new Object[] { SchemaSymbols.ELT_SCHEMA, DOMUtil.getLocalName(globalComp) }, globalComp);
/*      */           }
/*      */         }
/*      */ 
/* 1442 */         if (!sawAnnotation) {
/* 1443 */           String text = DOMUtil.getSyntheticAnnotation(currRoot);
/* 1444 */           if (text != null) {
/* 1445 */             currSG.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(currRoot, text, currSchemaDoc.getSchemaAttrs(), true, currSchemaDoc));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1450 */         if (annotationInfo != null) {
/* 1451 */           XSAnnotationInfo info = currSchemaDoc.getAnnotations();
/*      */ 
/* 1453 */           if (info != null) {
/* 1454 */             annotationInfo.add(doc2SystemId(currDoc));
/* 1455 */             annotationInfo.add(info);
/*      */           }
/*      */         }
/*      */ 
/* 1459 */         currSchemaDoc.returnSchemaAttrs();
/* 1460 */         DOMUtil.setHidden(currDoc, this.fHiddenNodes);
/*      */ 
/* 1463 */         Vector currSchemaDepends = (Vector)this.fDependencyMap.get(currSchemaDoc);
/* 1464 */         for (int i = 0; i < currSchemaDepends.size(); i++)
/* 1465 */           schemasToProcess.push(currSchemaDepends.elementAt(i));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final boolean needReportTNSError(String uri)
/*      */   {
/* 1477 */     if (this.fReportedTNS == null)
/* 1478 */       this.fReportedTNS = new Vector();
/* 1479 */     else if (this.fReportedTNS.contains(uri))
/* 1480 */       return false;
/* 1481 */     this.fReportedTNS.addElement(uri);
/* 1482 */     return true;
/*      */   }
/*      */ 
/*      */   void addGlobalAttributeDecl(XSAttributeDecl decl)
/*      */   {
/* 1509 */     String namespace = decl.getNamespace();
/* 1510 */     String declKey = namespace + "," + decl.getName();
/*      */ 
/* 1513 */     if (this.fGlobalAttrDecls.get(declKey) == null)
/* 1514 */       this.fGlobalAttrDecls.put(declKey, decl);
/*      */   }
/*      */ 
/*      */   void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl)
/*      */   {
/* 1520 */     String namespace = decl.getNamespace();
/* 1521 */     String declKey = namespace + "," + decl.getName();
/*      */ 
/* 1524 */     if (this.fGlobalAttrGrpDecls.get(declKey) == null)
/* 1525 */       this.fGlobalAttrGrpDecls.put(declKey, decl);
/*      */   }
/*      */ 
/*      */   void addGlobalElementDecl(XSElementDecl decl)
/*      */   {
/* 1531 */     String namespace = decl.getNamespace();
/* 1532 */     String declKey = namespace + "," + decl.getName();
/*      */ 
/* 1535 */     if (this.fGlobalElemDecls.get(declKey) == null)
/* 1536 */       this.fGlobalElemDecls.put(declKey, decl);
/*      */   }
/*      */ 
/*      */   void addGlobalGroupDecl(XSGroupDecl decl)
/*      */   {
/* 1542 */     String namespace = decl.getNamespace();
/* 1543 */     String declKey = namespace + "," + decl.getName();
/*      */ 
/* 1546 */     if (this.fGlobalGroupDecls.get(declKey) == null)
/* 1547 */       this.fGlobalGroupDecls.put(declKey, decl);
/*      */   }
/*      */ 
/*      */   void addGlobalNotationDecl(XSNotationDecl decl)
/*      */   {
/* 1553 */     String namespace = decl.getNamespace();
/* 1554 */     String declKey = namespace + "," + decl.getName();
/*      */ 
/* 1557 */     if (this.fGlobalNotationDecls.get(declKey) == null)
/* 1558 */       this.fGlobalNotationDecls.put(declKey, decl);
/*      */   }
/*      */ 
/*      */   void addGlobalTypeDecl(XSTypeDefinition decl)
/*      */   {
/* 1564 */     String namespace = decl.getNamespace();
/* 1565 */     String declKey = namespace + "," + decl.getName();
/*      */ 
/* 1568 */     if (this.fGlobalTypeDecls.get(declKey) == null)
/* 1569 */       this.fGlobalTypeDecls.put(declKey, decl);
/*      */   }
/*      */ 
/*      */   void addIDConstraintDecl(IdentityConstraint decl)
/*      */   {
/* 1575 */     String namespace = decl.getNamespace();
/* 1576 */     String declKey = namespace + "," + decl.getIdentityConstraintName();
/*      */ 
/* 1579 */     if (this.fGlobalIDConstraintDecls.get(declKey) == null)
/* 1580 */       this.fGlobalIDConstraintDecls.put(declKey, decl);
/*      */   }
/*      */ 
/*      */   private XSAttributeDecl getGlobalAttributeDecl(String declKey)
/*      */   {
/* 1585 */     return (XSAttributeDecl)this.fGlobalAttrDecls.get(declKey);
/*      */   }
/*      */ 
/*      */   private XSAttributeGroupDecl getGlobalAttributeGroupDecl(String declKey) {
/* 1589 */     return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(declKey);
/*      */   }
/*      */ 
/*      */   private XSElementDecl getGlobalElementDecl(String declKey) {
/* 1593 */     return (XSElementDecl)this.fGlobalElemDecls.get(declKey);
/*      */   }
/*      */ 
/*      */   private XSGroupDecl getGlobalGroupDecl(String declKey) {
/* 1597 */     return (XSGroupDecl)this.fGlobalGroupDecls.get(declKey);
/*      */   }
/*      */ 
/*      */   private XSNotationDecl getGlobalNotationDecl(String declKey) {
/* 1601 */     return (XSNotationDecl)this.fGlobalNotationDecls.get(declKey);
/*      */   }
/*      */ 
/*      */   private XSTypeDefinition getGlobalTypeDecl(String declKey) {
/* 1605 */     return (XSTypeDefinition)this.fGlobalTypeDecls.get(declKey);
/*      */   }
/*      */ 
/*      */   private IdentityConstraint getIDConstraintDecl(String declKey) {
/* 1609 */     return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(declKey);
/*      */   }
/*      */ 
/*      */   protected Object getGlobalDecl(XSDocumentInfo currSchema, int declType, QName declToTraverse, Element elmNode)
/*      */   {
/* 1643 */     if ((declToTraverse.uri != null) && (declToTraverse.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA))
/*      */     {
/* 1645 */       if (declType == 7) {
/* 1646 */         Object retObj = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(declToTraverse.localpart);
/* 1647 */         if (retObj != null) {
/* 1648 */           return retObj;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1653 */     if (!currSchema.isAllowedNS(declToTraverse.uri))
/*      */     {
/* 1655 */       if (currSchema.needReportTNSError(declToTraverse.uri)) {
/* 1656 */         String code = declToTraverse.uri == null ? "src-resolve.4.1" : "src-resolve.4.2";
/* 1657 */         reportSchemaError(code, new Object[] { this.fDoc2SystemId.get(currSchema.fSchemaElement), declToTraverse.uri, declToTraverse.rawname }, elmNode);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1664 */     SchemaGrammar sGrammar = this.fGrammarBucket.getGrammar(declToTraverse.uri);
/* 1665 */     if (sGrammar == null) {
/* 1666 */       if (needReportTNSError(declToTraverse.uri))
/* 1667 */         reportSchemaError("src-resolve", new Object[] { declToTraverse.rawname, COMP_TYPE[declType] }, elmNode);
/* 1668 */       return null;
/*      */     }
/*      */ 
/* 1672 */     Object retObj = getGlobalDeclFromGrammar(sGrammar, declType, declToTraverse.localpart);
/* 1673 */     String declKey = declToTraverse.uri + "," + declToTraverse.localpart;
/*      */ 
/* 1677 */     if (!this.fTolerateDuplicates) {
/* 1678 */       if (retObj != null)
/* 1679 */         return retObj;
/*      */     }
/*      */     else
/*      */     {
/* 1683 */       Object retObj2 = getGlobalDecl(declKey, declType);
/* 1684 */       if (retObj2 != null) {
/* 1685 */         return retObj2;
/*      */       }
/*      */     }
/*      */ 
/* 1689 */     XSDocumentInfo schemaWithDecl = null;
/* 1690 */     Element decl = null;
/* 1691 */     XSDocumentInfo declDoc = null;
/*      */ 
/* 1694 */     switch (declType) {
/*      */     case 1:
/* 1696 */       decl = getElementFromMap(this.fUnparsedAttributeRegistry, declKey);
/* 1697 */       declDoc = getDocInfoFromMap(this.fUnparsedAttributeRegistrySub, declKey);
/* 1698 */       break;
/*      */     case 2:
/* 1700 */       decl = getElementFromMap(this.fUnparsedAttributeGroupRegistry, declKey);
/* 1701 */       declDoc = getDocInfoFromMap(this.fUnparsedAttributeGroupRegistrySub, declKey);
/* 1702 */       break;
/*      */     case 3:
/* 1704 */       decl = getElementFromMap(this.fUnparsedElementRegistry, declKey);
/* 1705 */       declDoc = getDocInfoFromMap(this.fUnparsedElementRegistrySub, declKey);
/* 1706 */       break;
/*      */     case 4:
/* 1708 */       decl = getElementFromMap(this.fUnparsedGroupRegistry, declKey);
/* 1709 */       declDoc = getDocInfoFromMap(this.fUnparsedGroupRegistrySub, declKey);
/* 1710 */       break;
/*      */     case 5:
/* 1712 */       decl = getElementFromMap(this.fUnparsedIdentityConstraintRegistry, declKey);
/* 1713 */       declDoc = getDocInfoFromMap(this.fUnparsedIdentityConstraintRegistrySub, declKey);
/* 1714 */       break;
/*      */     case 6:
/* 1716 */       decl = getElementFromMap(this.fUnparsedNotationRegistry, declKey);
/* 1717 */       declDoc = getDocInfoFromMap(this.fUnparsedNotationRegistrySub, declKey);
/* 1718 */       break;
/*      */     case 7:
/* 1720 */       decl = getElementFromMap(this.fUnparsedTypeRegistry, declKey);
/* 1721 */       declDoc = getDocInfoFromMap(this.fUnparsedTypeRegistrySub, declKey);
/* 1722 */       break;
/*      */     default:
/* 1724 */       reportSchemaError("Internal-Error", new Object[] { "XSDHandler asked to locate component of type " + declType + "; it does not recognize this type!" }, elmNode);
/*      */     }
/*      */ 
/* 1728 */     if (decl == null) {
/* 1729 */       if (retObj == null) {
/* 1730 */         reportSchemaError("src-resolve", new Object[] { declToTraverse.rawname, COMP_TYPE[declType] }, elmNode);
/*      */       }
/* 1732 */       return retObj;
/*      */     }
/*      */ 
/* 1738 */     schemaWithDecl = findXSDocumentForDecl(currSchema, decl, declDoc);
/* 1739 */     if (schemaWithDecl == null)
/*      */     {
/* 1741 */       if (retObj == null) {
/* 1742 */         String code = declToTraverse.uri == null ? "src-resolve.4.1" : "src-resolve.4.2";
/* 1743 */         reportSchemaError(code, new Object[] { this.fDoc2SystemId.get(currSchema.fSchemaElement), declToTraverse.uri, declToTraverse.rawname }, elmNode);
/*      */       }
/* 1745 */       return retObj;
/*      */     }
/*      */ 
/* 1751 */     if (DOMUtil.isHidden(decl, this.fHiddenNodes)) {
/* 1752 */       if (retObj == null) {
/* 1753 */         String code = CIRCULAR_CODES[declType];
/* 1754 */         if ((declType == 7) && 
/* 1755 */           (SchemaSymbols.ELT_COMPLEXTYPE.equals(DOMUtil.getLocalName(decl)))) {
/* 1756 */           code = "ct-props-correct.3";
/*      */         }
/*      */ 
/* 1760 */         reportSchemaError(code, new Object[] { declToTraverse.prefix + ":" + declToTraverse.localpart }, elmNode);
/*      */       }
/* 1762 */       return retObj;
/*      */     }
/*      */ 
/* 1765 */     return traverseGlobalDecl(declType, decl, schemaWithDecl, sGrammar);
/*      */   }
/*      */ 
/*      */   protected Object getGlobalDecl(String declKey, int declType)
/*      */   {
/* 1771 */     Object retObj = null;
/*      */ 
/* 1773 */     switch (declType) {
/*      */     case 1:
/* 1775 */       retObj = getGlobalAttributeDecl(declKey);
/* 1776 */       break;
/*      */     case 2:
/* 1778 */       retObj = getGlobalAttributeGroupDecl(declKey);
/* 1779 */       break;
/*      */     case 3:
/* 1781 */       retObj = getGlobalElementDecl(declKey);
/* 1782 */       break;
/*      */     case 4:
/* 1784 */       retObj = getGlobalGroupDecl(declKey);
/* 1785 */       break;
/*      */     case 5:
/* 1787 */       retObj = getIDConstraintDecl(declKey);
/* 1788 */       break;
/*      */     case 6:
/* 1790 */       retObj = getGlobalNotationDecl(declKey);
/* 1791 */       break;
/*      */     case 7:
/* 1793 */       retObj = getGlobalTypeDecl(declKey);
/*      */     }
/*      */ 
/* 1797 */     return retObj;
/*      */   }
/*      */ 
/*      */   protected Object getGlobalDeclFromGrammar(SchemaGrammar sGrammar, int declType, String localpart) {
/* 1801 */     Object retObj = null;
/*      */ 
/* 1803 */     switch (declType) {
/*      */     case 1:
/* 1805 */       retObj = sGrammar.getGlobalAttributeDecl(localpart);
/* 1806 */       break;
/*      */     case 2:
/* 1808 */       retObj = sGrammar.getGlobalAttributeGroupDecl(localpart);
/* 1809 */       break;
/*      */     case 3:
/* 1811 */       retObj = sGrammar.getGlobalElementDecl(localpart);
/* 1812 */       break;
/*      */     case 4:
/* 1814 */       retObj = sGrammar.getGlobalGroupDecl(localpart);
/* 1815 */       break;
/*      */     case 5:
/* 1817 */       retObj = sGrammar.getIDConstraintDecl(localpart);
/* 1818 */       break;
/*      */     case 6:
/* 1820 */       retObj = sGrammar.getGlobalNotationDecl(localpart);
/* 1821 */       break;
/*      */     case 7:
/* 1823 */       retObj = sGrammar.getGlobalTypeDecl(localpart);
/*      */     }
/*      */ 
/* 1827 */     return retObj;
/*      */   }
/*      */ 
/*      */   protected Object getGlobalDeclFromGrammar(SchemaGrammar sGrammar, int declType, String localpart, String schemaLoc) {
/* 1831 */     Object retObj = null;
/*      */ 
/* 1833 */     switch (declType) {
/*      */     case 1:
/* 1835 */       retObj = sGrammar.getGlobalAttributeDecl(localpart, schemaLoc);
/* 1836 */       break;
/*      */     case 2:
/* 1838 */       retObj = sGrammar.getGlobalAttributeGroupDecl(localpart, schemaLoc);
/* 1839 */       break;
/*      */     case 3:
/* 1841 */       retObj = sGrammar.getGlobalElementDecl(localpart, schemaLoc);
/* 1842 */       break;
/*      */     case 4:
/* 1844 */       retObj = sGrammar.getGlobalGroupDecl(localpart, schemaLoc);
/* 1845 */       break;
/*      */     case 5:
/* 1847 */       retObj = sGrammar.getIDConstraintDecl(localpart, schemaLoc);
/* 1848 */       break;
/*      */     case 6:
/* 1850 */       retObj = sGrammar.getGlobalNotationDecl(localpart, schemaLoc);
/* 1851 */       break;
/*      */     case 7:
/* 1853 */       retObj = sGrammar.getGlobalTypeDecl(localpart, schemaLoc);
/*      */     }
/*      */ 
/* 1857 */     return retObj;
/*      */   }
/*      */ 
/*      */   protected Object traverseGlobalDecl(int declType, Element decl, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
/* 1861 */     Object retObj = null;
/*      */ 
/* 1863 */     DOMUtil.setHidden(decl, this.fHiddenNodes);
/* 1864 */     SchemaNamespaceSupport nsSupport = null;
/*      */ 
/* 1866 */     Element parent = DOMUtil.getParent(decl);
/* 1867 */     if (DOMUtil.getLocalName(parent).equals(SchemaSymbols.ELT_REDEFINE)) {
/* 1868 */       nsSupport = this.fRedefine2NSSupport != null ? (SchemaNamespaceSupport)this.fRedefine2NSSupport.get(parent) : null;
/*      */     }
/*      */ 
/* 1871 */     schemaDoc.backupNSSupport(nsSupport);
/*      */ 
/* 1874 */     switch (declType) {
/*      */     case 7:
/* 1876 */       if (DOMUtil.getLocalName(decl).equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
/* 1877 */         retObj = this.fComplexTypeTraverser.traverseGlobal(decl, schemaDoc, grammar);
/*      */       }
/*      */       else {
/* 1880 */         retObj = this.fSimpleTypeTraverser.traverseGlobal(decl, schemaDoc, grammar);
/*      */       }
/* 1882 */       break;
/*      */     case 1:
/* 1884 */       retObj = this.fAttributeTraverser.traverseGlobal(decl, schemaDoc, grammar);
/* 1885 */       break;
/*      */     case 3:
/* 1887 */       retObj = this.fElementTraverser.traverseGlobal(decl, schemaDoc, grammar);
/* 1888 */       break;
/*      */     case 2:
/* 1890 */       retObj = this.fAttributeGroupTraverser.traverseGlobal(decl, schemaDoc, grammar);
/* 1891 */       break;
/*      */     case 4:
/* 1893 */       retObj = this.fGroupTraverser.traverseGlobal(decl, schemaDoc, grammar);
/* 1894 */       break;
/*      */     case 6:
/* 1896 */       retObj = this.fNotationTraverser.traverse(decl, schemaDoc, grammar);
/* 1897 */       break;
/*      */     case 5:
/*      */     }
/*      */ 
/* 1906 */     schemaDoc.restoreNSSupport();
/*      */ 
/* 1908 */     return retObj;
/*      */   }
/*      */ 
/*      */   public String schemaDocument2SystemId(XSDocumentInfo schemaDoc) {
/* 1912 */     return (String)this.fDoc2SystemId.get(schemaDoc.fSchemaElement);
/*      */   }
/*      */ 
/*      */   Object getGrpOrAttrGrpRedefinedByRestriction(int type, QName name, XSDocumentInfo currSchema, Element elmNode)
/*      */   {
/* 1925 */     String realName = "," + name.localpart;
/*      */ 
/* 1927 */     String nameToFind = null;
/* 1928 */     switch (type) {
/*      */     case 2:
/* 1930 */       nameToFind = (String)this.fRedefinedRestrictedAttributeGroupRegistry.get(realName);
/* 1931 */       break;
/*      */     case 4:
/* 1933 */       nameToFind = (String)this.fRedefinedRestrictedGroupRegistry.get(realName);
/* 1934 */       break;
/*      */     default:
/* 1936 */       return null;
/*      */     }
/* 1938 */     if (nameToFind == null) return null;
/* 1939 */     int commaPos = nameToFind.indexOf(",");
/* 1940 */     QName qNameToFind = new QName(XMLSymbols.EMPTY_STRING, nameToFind.substring(commaPos + 1), nameToFind.substring(commaPos), commaPos == 0 ? null : nameToFind.substring(0, commaPos));
/*      */ 
/* 1942 */     Object retObj = getGlobalDecl(currSchema, type, qNameToFind, elmNode);
/* 1943 */     if (retObj == null) {
/* 1944 */       switch (type) {
/*      */       case 2:
/* 1946 */         reportSchemaError("src-redefine.7.2.1", new Object[] { name.localpart }, elmNode);
/* 1947 */         break;
/*      */       case 4:
/* 1949 */         reportSchemaError("src-redefine.6.2.1", new Object[] { name.localpart }, elmNode);
/*      */       }
/*      */ 
/* 1952 */       return null;
/*      */     }
/* 1954 */     return retObj;
/*      */   }
/*      */ 
/*      */   protected void resolveKeyRefs()
/*      */   {
/* 1967 */     for (int i = 0; i < this.fKeyrefStackPos; i++) {
/* 1968 */       XSDocumentInfo keyrefSchemaDoc = this.fKeyrefsMapXSDocumentInfo[i];
/* 1969 */       keyrefSchemaDoc.fNamespaceSupport.makeGlobal();
/* 1970 */       keyrefSchemaDoc.fNamespaceSupport.setEffectiveContext(this.fKeyrefNamespaceContext[i]);
/* 1971 */       SchemaGrammar keyrefGrammar = this.fGrammarBucket.getGrammar(keyrefSchemaDoc.fTargetNamespace);
/*      */ 
/* 1974 */       DOMUtil.setHidden(this.fKeyrefs[i], this.fHiddenNodes);
/* 1975 */       this.fKeyrefTraverser.traverse(this.fKeyrefs[i], this.fKeyrefElems[i], keyrefSchemaDoc, keyrefGrammar);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Map getIDRegistry()
/*      */   {
/* 1982 */     return this.fUnparsedIdentityConstraintRegistry;
/*      */   }
/*      */ 
/*      */   protected Map getIDRegistry_sub() {
/* 1986 */     return this.fUnparsedIdentityConstraintRegistrySub;
/*      */   }
/*      */ 
/*      */   protected void storeKeyRef(Element keyrefToStore, XSDocumentInfo schemaDoc, XSElementDecl currElemDecl)
/*      */   {
/* 1995 */     String keyrefName = DOMUtil.getAttrValue(keyrefToStore, SchemaSymbols.ATT_NAME);
/* 1996 */     if (keyrefName.length() != 0) {
/* 1997 */       String keyrefQName = schemaDoc.fTargetNamespace + "," + keyrefName;
/*      */ 
/* 1999 */       checkForDuplicateNames(keyrefQName, 5, this.fUnparsedIdentityConstraintRegistry, this.fUnparsedIdentityConstraintRegistrySub, keyrefToStore, schemaDoc);
/*      */     }
/*      */ 
/* 2004 */     if (this.fKeyrefStackPos == this.fKeyrefs.length) {
/* 2005 */       Element[] elemArray = new Element[this.fKeyrefStackPos + 2];
/* 2006 */       System.arraycopy(this.fKeyrefs, 0, elemArray, 0, this.fKeyrefStackPos);
/* 2007 */       this.fKeyrefs = elemArray;
/* 2008 */       XSElementDecl[] declArray = new XSElementDecl[this.fKeyrefStackPos + 2];
/* 2009 */       System.arraycopy(this.fKeyrefElems, 0, declArray, 0, this.fKeyrefStackPos);
/* 2010 */       this.fKeyrefElems = declArray;
/* 2011 */       String[][] stringArray = new String[this.fKeyrefStackPos + 2][];
/* 2012 */       System.arraycopy(this.fKeyrefNamespaceContext, 0, stringArray, 0, this.fKeyrefStackPos);
/* 2013 */       this.fKeyrefNamespaceContext = stringArray;
/*      */ 
/* 2015 */       XSDocumentInfo[] xsDocumentInfo = new XSDocumentInfo[this.fKeyrefStackPos + 2];
/* 2016 */       System.arraycopy(this.fKeyrefsMapXSDocumentInfo, 0, xsDocumentInfo, 0, this.fKeyrefStackPos);
/* 2017 */       this.fKeyrefsMapXSDocumentInfo = xsDocumentInfo;
/*      */     }
/*      */ 
/* 2020 */     this.fKeyrefs[this.fKeyrefStackPos] = keyrefToStore;
/* 2021 */     this.fKeyrefElems[this.fKeyrefStackPos] = currElemDecl;
/* 2022 */     this.fKeyrefNamespaceContext[this.fKeyrefStackPos] = schemaDoc.fNamespaceSupport.getEffectiveLocalContext();
/*      */ 
/* 2024 */     this.fKeyrefsMapXSDocumentInfo[(this.fKeyrefStackPos++)] = schemaDoc;
/*      */   }
/*      */ 
/*      */   private Element resolveSchema(XSDDescription desc, boolean mustResolve, Element referElement, boolean usePairs)
/*      */   {
/* 2038 */     XMLInputSource schemaSource = null;
/*      */     try {
/* 2040 */       Map pairs = usePairs ? this.fLocationPairs : EMPTY_TABLE;
/* 2041 */       schemaSource = XMLSchemaLoader.resolveDocument(desc, pairs, this.fEntityResolver);
/*      */     }
/*      */     catch (IOException ex) {
/* 2044 */       if (mustResolve) {
/* 2045 */         reportSchemaError("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
/*      */       }
/*      */       else
/*      */       {
/* 2050 */         reportSchemaWarning("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2055 */     if ((schemaSource instanceof DOMInputSource)) {
/* 2056 */       return getSchemaDocument(desc.getTargetNamespace(), (DOMInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */     }
/* 2058 */     if ((schemaSource instanceof SAXInputSource)) {
/* 2059 */       return getSchemaDocument(desc.getTargetNamespace(), (SAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */     }
/* 2061 */     if ((schemaSource instanceof StAXInputSource)) {
/* 2062 */       return getSchemaDocument(desc.getTargetNamespace(), (StAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */     }
/* 2064 */     if ((schemaSource instanceof XSInputSource)) {
/* 2065 */       return getSchemaDocument((XSInputSource)schemaSource, desc);
/*      */     }
/* 2067 */     return getSchemaDocument(desc.getTargetNamespace(), schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */   }
/*      */ 
/*      */   private Element resolveSchema(XMLInputSource schemaSource, XSDDescription desc, boolean mustResolve, Element referElement)
/*      */   {
/* 2073 */     if ((schemaSource instanceof DOMInputSource)) {
/* 2074 */       return getSchemaDocument(desc.getTargetNamespace(), (DOMInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */     }
/* 2076 */     if ((schemaSource instanceof SAXInputSource)) {
/* 2077 */       return getSchemaDocument(desc.getTargetNamespace(), (SAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */     }
/* 2079 */     if ((schemaSource instanceof StAXInputSource)) {
/* 2080 */       return getSchemaDocument(desc.getTargetNamespace(), (StAXInputSource)schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */     }
/* 2082 */     if ((schemaSource instanceof XSInputSource)) {
/* 2083 */       return getSchemaDocument((XSInputSource)schemaSource, desc);
/*      */     }
/* 2085 */     return getSchemaDocument(desc.getTargetNamespace(), schemaSource, mustResolve, desc.getContextType(), referElement);
/*      */   }
/*      */ 
/*      */   private XMLInputSource resolveSchemaSource(XSDDescription desc, boolean mustResolve, Element referElement, boolean usePairs)
/*      */   {
/* 2091 */     XMLInputSource schemaSource = null;
/*      */     try {
/* 2093 */       Map pairs = usePairs ? this.fLocationPairs : EMPTY_TABLE;
/* 2094 */       schemaSource = XMLSchemaLoader.resolveDocument(desc, pairs, this.fEntityResolver);
/*      */     }
/*      */     catch (IOException ex) {
/* 2097 */       if (mustResolve) {
/* 2098 */         reportSchemaError("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
/*      */       }
/*      */       else
/*      */       {
/* 2103 */         reportSchemaWarning("schema_reference.4", new Object[] { desc.getLocationHints()[0] }, referElement);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2109 */     return schemaSource;
/*      */   }
/*      */ 
/*      */   private Element getSchemaDocument(String schemaNamespace, XMLInputSource schemaSource, boolean mustResolve, short referType, Element referElement)
/*      */   {
/* 2124 */     boolean hasInput = true;
/* 2125 */     IOException exception = null;
/*      */ 
/* 2127 */     Element schemaElement = null;
/*      */     try
/*      */     {
/* 2135 */       if ((schemaSource != null) && ((schemaSource.getSystemId() != null) || (schemaSource.getByteStream() != null) || (schemaSource.getCharacterStream() != null)))
/*      */       {
/* 2144 */         XSDKey key = null;
/* 2145 */         String schemaId = null;
/* 2146 */         if (referType != 3) {
/* 2147 */           schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
/* 2148 */           key = new XSDKey(schemaId, referType, schemaNamespace);
/* 2149 */           if ((schemaElement = (Element)this.fTraversed.get(key)) != null) {
/* 2150 */             this.fLastSchemaWasDuplicate = true;
/* 2151 */             return schemaElement;
/*      */           }
/*      */         }
/*      */ 
/* 2155 */         this.fSchemaParser.parse(schemaSource);
/* 2156 */         Document schemaDocument = this.fSchemaParser.getDocument();
/* 2157 */         schemaElement = schemaDocument != null ? DOMUtil.getRoot(schemaDocument) : null;
/* 2158 */         return getSchemaDocument0(key, schemaId, schemaElement);
/*      */       }
/*      */ 
/* 2161 */       hasInput = false;
/*      */     }
/*      */     catch (IOException ex)
/*      */     {
/* 2165 */       exception = ex;
/*      */     }
/* 2167 */     return getSchemaDocument1(mustResolve, hasInput, schemaSource, referElement, exception);
/*      */   }
/*      */ 
/*      */   private Element getSchemaDocument(String schemaNamespace, SAXInputSource schemaSource, boolean mustResolve, short referType, Element referElement)
/*      */   {
/* 2181 */     XMLReader parser = schemaSource.getXMLReader();
/* 2182 */     InputSource inputSource = schemaSource.getInputSource();
/* 2183 */     boolean hasInput = true;
/* 2184 */     IOException exception = null;
/* 2185 */     Element schemaElement = null;
/*      */     try {
/* 2187 */       if ((inputSource != null) && ((inputSource.getSystemId() != null) || (inputSource.getByteStream() != null) || (inputSource.getCharacterStream() != null)))
/*      */       {
/* 2194 */         XSDKey key = null;
/* 2195 */         String schemaId = null;
/* 2196 */         if (referType != 3) {
/* 2197 */           schemaId = XMLEntityManager.expandSystemId(inputSource.getSystemId(), schemaSource.getBaseSystemId(), false);
/* 2198 */           key = new XSDKey(schemaId, referType, schemaNamespace);
/* 2199 */           if ((schemaElement = (Element)this.fTraversed.get(key)) != null) {
/* 2200 */             this.fLastSchemaWasDuplicate = true;
/* 2201 */             return schemaElement;
/*      */           }
/*      */         }
/*      */ 
/* 2205 */         boolean namespacePrefixes = false;
/* 2206 */         if (parser != null) {
/*      */           try {
/* 2208 */             namespacePrefixes = parser.getFeature("http://xml.org/sax/features/namespace-prefixes");
/*      */           } catch (SAXException se) {
/*      */           }
/*      */         }
/*      */         else {
/*      */           try {
/* 2214 */             parser = XMLReaderFactory.createXMLReader();
/*      */           }
/*      */           catch (SAXException se)
/*      */           {
/* 2219 */             parser = new SAXParser();
/*      */           }
/*      */           try {
/* 2222 */             parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
/* 2223 */             namespacePrefixes = true;
/*      */ 
/* 2225 */             if ((parser instanceof SAXParser)) {
/* 2226 */               Object securityManager = this.fSchemaParser.getProperty("http://apache.org/xml/properties/security-manager");
/* 2227 */               if (securityManager != null) {
/* 2228 */                 parser.setProperty("http://apache.org/xml/properties/security-manager", securityManager);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (SAXException se)
/*      */           {
/*      */           }
/*      */         }
/* 2236 */         boolean stringsInternalized = false;
/*      */         try {
/* 2238 */           stringsInternalized = parser.getFeature("http://xml.org/sax/features/string-interning");
/*      */         }
/*      */         catch (SAXException exc)
/*      */         {
/*      */         }
/*      */ 
/* 2244 */         if (this.fXSContentHandler == null) {
/* 2245 */           this.fXSContentHandler = new SchemaContentHandler();
/*      */         }
/* 2247 */         this.fXSContentHandler.reset(this.fSchemaParser, this.fSymbolTable, namespacePrefixes, stringsInternalized);
/*      */ 
/* 2249 */         parser.setContentHandler(this.fXSContentHandler);
/* 2250 */         parser.setErrorHandler(this.fErrorReporter.getSAXErrorHandler());
/*      */ 
/* 2252 */         parser.parse(inputSource);
/*      */         try
/*      */         {
/* 2255 */           parser.setContentHandler(null);
/* 2256 */           parser.setErrorHandler(null);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */         }
/*      */ 
/* 2263 */         Document schemaDocument = this.fXSContentHandler.getDocument();
/* 2264 */         schemaElement = schemaDocument != null ? DOMUtil.getRoot(schemaDocument) : null;
/* 2265 */         return getSchemaDocument0(key, schemaId, schemaElement);
/*      */       }
/*      */ 
/* 2268 */       hasInput = false;
/*      */     }
/*      */     catch (SAXParseException spe)
/*      */     {
/* 2272 */       throw SAX2XNIUtil.createXMLParseException0(spe);
/*      */     }
/*      */     catch (SAXException se) {
/* 2275 */       throw SAX2XNIUtil.createXNIException0(se);
/*      */     }
/*      */     catch (IOException ioe) {
/* 2278 */       exception = ioe;
/*      */     }
/* 2280 */     return getSchemaDocument1(mustResolve, hasInput, schemaSource, referElement, exception);
/*      */   }
/*      */ 
/*      */   private Element getSchemaDocument(String schemaNamespace, DOMInputSource schemaSource, boolean mustResolve, short referType, Element referElement)
/*      */   {
/* 2294 */     boolean hasInput = true;
/* 2295 */     IOException exception = null;
/* 2296 */     Element schemaElement = null;
/* 2297 */     Element schemaRootElement = null;
/*      */ 
/* 2299 */     Node node = schemaSource.getNode();
/* 2300 */     short nodeType = -1;
/* 2301 */     if (node != null) {
/* 2302 */       nodeType = node.getNodeType();
/* 2303 */       if (nodeType == 9) {
/* 2304 */         schemaRootElement = DOMUtil.getRoot((Document)node);
/*      */       }
/* 2306 */       else if (nodeType == 1) {
/* 2307 */         schemaRootElement = (Element)node;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 2312 */       if (schemaRootElement != null)
/*      */       {
/* 2315 */         XSDKey key = null;
/* 2316 */         String schemaId = null;
/* 2317 */         if (referType != 3) {
/* 2318 */           schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
/* 2319 */           boolean isDocument = nodeType == 9;
/* 2320 */           if (!isDocument) {
/* 2321 */             Node parent = schemaRootElement.getParentNode();
/* 2322 */             if (parent != null) {
/* 2323 */               isDocument = parent.getNodeType() == 9;
/*      */             }
/*      */           }
/* 2326 */           if (isDocument) {
/* 2327 */             key = new XSDKey(schemaId, referType, schemaNamespace);
/* 2328 */             if ((schemaElement = (Element)this.fTraversed.get(key)) != null) {
/* 2329 */               this.fLastSchemaWasDuplicate = true;
/* 2330 */               return schemaElement;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 2335 */         schemaElement = schemaRootElement;
/* 2336 */         return getSchemaDocument0(key, schemaId, schemaElement);
/*      */       }
/*      */ 
/* 2339 */       hasInput = false;
/*      */     }
/*      */     catch (IOException ioe)
/*      */     {
/* 2343 */       exception = ioe;
/*      */     }
/* 2345 */     return getSchemaDocument1(mustResolve, hasInput, schemaSource, referElement, exception);
/*      */   }
/*      */ 
/*      */   private Element getSchemaDocument(String schemaNamespace, StAXInputSource schemaSource, boolean mustResolve, short referType, Element referElement)
/*      */   {
/* 2359 */     IOException exception = null;
/* 2360 */     Element schemaElement = null;
/*      */     try {
/* 2362 */       boolean consumeRemainingContent = schemaSource.shouldConsumeRemainingContent();
/* 2363 */       XMLStreamReader streamReader = schemaSource.getXMLStreamReader();
/* 2364 */       XMLEventReader eventReader = schemaSource.getXMLEventReader();
/*      */ 
/* 2368 */       XSDKey key = null;
/* 2369 */       String schemaId = null;
/* 2370 */       if (referType != 3) {
/* 2371 */         schemaId = XMLEntityManager.expandSystemId(schemaSource.getSystemId(), schemaSource.getBaseSystemId(), false);
/* 2372 */         boolean isDocument = consumeRemainingContent;
/* 2373 */         if (!isDocument) {
/* 2374 */           if (streamReader != null) {
/* 2375 */             isDocument = streamReader.getEventType() == 7;
/*      */           }
/*      */           else {
/* 2378 */             isDocument = eventReader.peek().isStartDocument();
/*      */           }
/*      */         }
/* 2381 */         if (isDocument) {
/* 2382 */           key = new XSDKey(schemaId, referType, schemaNamespace);
/* 2383 */           if ((schemaElement = (Element)this.fTraversed.get(key)) != null) {
/* 2384 */             this.fLastSchemaWasDuplicate = true;
/* 2385 */             return schemaElement;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2390 */       if (this.fStAXSchemaParser == null) {
/* 2391 */         this.fStAXSchemaParser = new StAXSchemaParser();
/*      */       }
/* 2393 */       this.fStAXSchemaParser.reset(this.fSchemaParser, this.fSymbolTable);
/*      */ 
/* 2395 */       if (streamReader != null) {
/* 2396 */         this.fStAXSchemaParser.parse(streamReader);
/* 2397 */         if (consumeRemainingContent) {
/* 2398 */           while (streamReader.hasNext())
/* 2399 */             streamReader.next();
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 2404 */         this.fStAXSchemaParser.parse(eventReader);
/* 2405 */         if (consumeRemainingContent) {
/* 2406 */           while (eventReader.hasNext()) {
/* 2407 */             eventReader.nextEvent();
/*      */           }
/*      */         }
/*      */       }
/* 2411 */       Document schemaDocument = this.fStAXSchemaParser.getDocument();
/* 2412 */       schemaElement = schemaDocument != null ? DOMUtil.getRoot(schemaDocument) : null;
/* 2413 */       return getSchemaDocument0(key, schemaId, schemaElement);
/*      */     }
/*      */     catch (XMLStreamException e) {
/* 2416 */       StAXLocationWrapper slw = new StAXLocationWrapper();
/* 2417 */       slw.setLocation(e.getLocation());
/* 2418 */       throw new XMLParseException(slw, e.getMessage(), e);
/*      */     }
/*      */     catch (IOException e) {
/* 2421 */       exception = e;
/*      */     }
/* 2423 */     return getSchemaDocument1(mustResolve, true, schemaSource, referElement, exception);
/*      */   }
/*      */ 
/*      */   private Element getSchemaDocument0(XSDKey key, String schemaId, Element schemaElement)
/*      */   {
/* 2433 */     if (key != null) {
/* 2434 */       this.fTraversed.put(key, schemaElement);
/*      */     }
/* 2436 */     if (schemaId != null) {
/* 2437 */       this.fDoc2SystemId.put(schemaElement, schemaId);
/*      */     }
/* 2439 */     this.fLastSchemaWasDuplicate = false;
/* 2440 */     return schemaElement;
/*      */   }
/*      */ 
/*      */   private Element getSchemaDocument1(boolean mustResolve, boolean hasInput, XMLInputSource schemaSource, Element referElement, IOException ioe)
/*      */   {
/* 2450 */     if (mustResolve) {
/* 2451 */       if (hasInput) {
/* 2452 */         reportSchemaError("schema_reference.4", new Object[] { schemaSource.getSystemId() }, referElement, ioe);
/*      */       }
/*      */       else
/*      */       {
/* 2457 */         reportSchemaError("schema_reference.4", new Object[] { schemaSource == null ? "" : schemaSource.getSystemId() }, referElement, ioe);
/*      */       }
/*      */ 
/*      */     }
/* 2462 */     else if (hasInput) {
/* 2463 */       reportSchemaWarning("schema_reference.4", new Object[] { schemaSource.getSystemId() }, referElement, ioe);
/*      */     }
/*      */ 
/* 2468 */     this.fLastSchemaWasDuplicate = false;
/* 2469 */     return null;
/*      */   }
/*      */ 
/*      */   private Element getSchemaDocument(XSInputSource schemaSource, XSDDescription desc)
/*      */   {
/* 2483 */     SchemaGrammar[] grammars = schemaSource.getGrammars();
/* 2484 */     short referType = desc.getContextType();
/*      */ 
/* 2486 */     if ((grammars != null) && (grammars.length > 0)) {
/* 2487 */       Vector expandedGrammars = expandGrammars(grammars);
/*      */ 
/* 2491 */       if ((this.fNamespaceGrowth) || (!existingGrammars(expandedGrammars))) {
/* 2492 */         addGrammars(expandedGrammars);
/* 2493 */         if (referType == 3)
/* 2494 */           desc.setTargetNamespace(grammars[0].getTargetNamespace());
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 2499 */       XSObject[] components = schemaSource.getComponents();
/* 2500 */       if ((components != null) && (components.length > 0)) {
/* 2501 */         Map importDependencies = new HashMap();
/* 2502 */         Vector expandedComponents = expandComponents(components, importDependencies);
/* 2503 */         if ((this.fNamespaceGrowth) || (canAddComponents(expandedComponents))) {
/* 2504 */           addGlobalComponents(expandedComponents, importDependencies);
/* 2505 */           if (referType == 3) {
/* 2506 */             desc.setTargetNamespace(components[0].getNamespace());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2511 */     return null;
/*      */   }
/*      */ 
/*      */   private Vector expandGrammars(SchemaGrammar[] grammars) {
/* 2515 */     Vector currGrammars = new Vector();
/*      */ 
/* 2517 */     for (int i = 0; i < grammars.length; i++) {
/* 2518 */       if (!currGrammars.contains(grammars[i])) {
/* 2519 */         currGrammars.add(grammars[i]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2526 */     for (int i = 0; i < currGrammars.size(); i++)
/*      */     {
/* 2528 */       SchemaGrammar sg1 = (SchemaGrammar)currGrammars.elementAt(i);
/*      */ 
/* 2530 */       Vector gs = sg1.getImportedGrammars();
/*      */ 
/* 2533 */       if (gs != null)
/*      */       {
/* 2537 */         for (int j = gs.size() - 1; j >= 0; j--) {
/* 2538 */           SchemaGrammar sg2 = (SchemaGrammar)gs.elementAt(j);
/* 2539 */           if (!currGrammars.contains(sg2)) {
/* 2540 */             currGrammars.addElement(sg2);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2545 */     return currGrammars;
/*      */   }
/*      */ 
/*      */   private boolean existingGrammars(Vector grammars) {
/* 2549 */     int length = grammars.size();
/* 2550 */     XSDDescription desc = new XSDDescription();
/*      */ 
/* 2552 */     for (int i = 0; i < length; i++) {
/* 2553 */       SchemaGrammar sg1 = (SchemaGrammar)grammars.elementAt(i);
/* 2554 */       desc.setNamespace(sg1.getTargetNamespace());
/*      */ 
/* 2556 */       SchemaGrammar sg2 = findGrammar(desc, false);
/* 2557 */       if (sg2 != null) {
/* 2558 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 2562 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean canAddComponents(Vector components) {
/* 2566 */     int size = components.size();
/* 2567 */     XSDDescription desc = new XSDDescription();
/* 2568 */     for (int i = 0; i < size; i++) {
/* 2569 */       XSObject component = (XSObject)components.elementAt(i);
/* 2570 */       if (!canAddComponent(component, desc)) {
/* 2571 */         return false;
/*      */       }
/*      */     }
/* 2574 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean canAddComponent(XSObject component, XSDDescription desc) {
/* 2578 */     desc.setNamespace(component.getNamespace());
/*      */ 
/* 2580 */     SchemaGrammar sg = findGrammar(desc, false);
/* 2581 */     if (sg == null) {
/* 2582 */       return true;
/*      */     }
/* 2584 */     if (sg.isImmutable()) {
/* 2585 */       return false;
/*      */     }
/*      */ 
/* 2588 */     short componentType = component.getType();
/* 2589 */     String name = component.getName();
/*      */ 
/* 2591 */     switch (componentType) {
/*      */     case 3:
/* 2593 */       if (sg.getGlobalTypeDecl(name) == component) {
/* 2594 */         return true;
/*      */       }
/*      */       break;
/*      */     case 1:
/* 2598 */       if (sg.getGlobalAttributeDecl(name) == component) {
/* 2599 */         return true;
/*      */       }
/*      */       break;
/*      */     case 5:
/* 2603 */       if (sg.getGlobalAttributeDecl(name) == component) {
/* 2604 */         return true;
/*      */       }
/*      */       break;
/*      */     case 2:
/* 2608 */       if (sg.getGlobalElementDecl(name) == component) {
/* 2609 */         return true;
/*      */       }
/*      */       break;
/*      */     case 6:
/* 2613 */       if (sg.getGlobalGroupDecl(name) == component) {
/* 2614 */         return true;
/*      */       }
/*      */       break;
/*      */     case 11:
/* 2618 */       if (sg.getGlobalNotationDecl(name) == component)
/* 2619 */         return true; break;
/*      */     case 4:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     default:
/* 2625 */       return true;
/*      */     }
/* 2627 */     return false;
/*      */   }
/*      */ 
/*      */   private void addGrammars(Vector grammars) {
/* 2631 */     int length = grammars.size();
/* 2632 */     XSDDescription desc = new XSDDescription();
/*      */ 
/* 2634 */     for (int i = 0; i < length; i++) {
/* 2635 */       SchemaGrammar sg1 = (SchemaGrammar)grammars.elementAt(i);
/* 2636 */       desc.setNamespace(sg1.getTargetNamespace());
/*      */ 
/* 2638 */       SchemaGrammar sg2 = findGrammar(desc, this.fNamespaceGrowth);
/* 2639 */       if (sg1 != sg2)
/* 2640 */         addGrammarComponents(sg1, sg2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addGrammarComponents(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2646 */     if (dstGrammar == null) {
/* 2647 */       createGrammarFrom(srcGrammar);
/* 2648 */       return;
/*      */     }
/*      */ 
/* 2651 */     SchemaGrammar tmpGrammar = dstGrammar;
/* 2652 */     if (tmpGrammar.isImmutable()) {
/* 2653 */       tmpGrammar = createGrammarFrom(dstGrammar);
/*      */     }
/*      */ 
/* 2657 */     addNewGrammarLocations(srcGrammar, tmpGrammar);
/*      */ 
/* 2660 */     addNewImportedGrammars(srcGrammar, tmpGrammar);
/*      */ 
/* 2663 */     addNewGrammarComponents(srcGrammar, tmpGrammar);
/*      */   }
/*      */ 
/*      */   private SchemaGrammar createGrammarFrom(SchemaGrammar grammar) {
/* 2667 */     SchemaGrammar newGrammar = new SchemaGrammar(grammar);
/* 2668 */     this.fGrammarBucket.putGrammar(newGrammar);
/*      */ 
/* 2670 */     updateImportListWith(newGrammar);
/*      */ 
/* 2672 */     updateImportListFor(newGrammar);
/* 2673 */     return newGrammar;
/*      */   }
/*      */ 
/*      */   private void addNewGrammarLocations(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar) {
/* 2677 */     StringList locations = srcGrammar.getDocumentLocations();
/* 2678 */     int locSize = locations.size();
/* 2679 */     StringList locations2 = dstGrammar.getDocumentLocations();
/*      */ 
/* 2681 */     for (int i = 0; i < locSize; i++) {
/* 2682 */       String loc = locations.item(i);
/* 2683 */       if (!locations2.contains(loc))
/* 2684 */         dstGrammar.addDocument(null, loc);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addNewImportedGrammars(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2690 */     Vector igs1 = srcGrammar.getImportedGrammars();
/* 2691 */     if (igs1 != null) {
/* 2692 */       Vector igs2 = dstGrammar.getImportedGrammars();
/*      */ 
/* 2694 */       if (igs2 == null) {
/* 2695 */         igs2 = (Vector)igs1.clone();
/* 2696 */         dstGrammar.setImportedGrammars(igs2);
/*      */       }
/*      */       else {
/* 2699 */         updateImportList(igs1, igs2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateImportList(Vector importedSrc, Vector importedDst)
/*      */   {
/* 2706 */     int size = importedSrc.size();
/*      */ 
/* 2708 */     for (int i = 0; i < size; i++) {
/* 2709 */       SchemaGrammar sg = (SchemaGrammar)importedSrc.elementAt(i);
/* 2710 */       if (!containedImportedGrammar(importedDst, sg))
/* 2711 */         importedDst.add(sg);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addNewGrammarComponents(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2717 */     dstGrammar.resetComponents();
/* 2718 */     addGlobalElementDecls(srcGrammar, dstGrammar);
/* 2719 */     addGlobalAttributeDecls(srcGrammar, dstGrammar);
/* 2720 */     addGlobalAttributeGroupDecls(srcGrammar, dstGrammar);
/* 2721 */     addGlobalGroupDecls(srcGrammar, dstGrammar);
/* 2722 */     addGlobalTypeDecls(srcGrammar, dstGrammar);
/* 2723 */     addGlobalNotationDecls(srcGrammar, dstGrammar);
/*      */   }
/*      */ 
/*      */   private void addGlobalElementDecls(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar) {
/* 2727 */     XSNamedMap components = srcGrammar.getComponents((short)2);
/* 2728 */     int len = components.getLength();
/*      */ 
/* 2732 */     for (int i = 0; i < len; i++) {
/* 2733 */       XSElementDecl srcDecl = (XSElementDecl)components.item(i);
/* 2734 */       XSElementDecl dstDecl = dstGrammar.getGlobalElementDecl(srcDecl.getName());
/* 2735 */       if (dstDecl == null) {
/* 2736 */         dstGrammar.addGlobalElementDecl(srcDecl);
/*      */       }
/* 2738 */       else if (dstDecl == srcDecl);
/*      */     }
/*      */ 
/* 2744 */     ObjectList componentsExt = srcGrammar.getComponentsExt((short)2);
/* 2745 */     len = componentsExt.getLength();
/*      */ 
/* 2747 */     for (int i = 0; i < len; i += 2) {
/* 2748 */       String key = (String)componentsExt.item(i);
/* 2749 */       int index = key.indexOf(',');
/* 2750 */       String location = key.substring(0, index);
/* 2751 */       String name = key.substring(index + 1, key.length());
/*      */ 
/* 2753 */       XSElementDecl srcDecl = (XSElementDecl)componentsExt.item(i + 1);
/* 2754 */       XSElementDecl dstDecl = dstGrammar.getGlobalElementDecl(name, location);
/* 2755 */       if (dstDecl == null) {
/* 2756 */         dstGrammar.addGlobalElementDecl(srcDecl, location);
/*      */       }
/* 2758 */       else if (dstDecl == srcDecl);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addGlobalAttributeDecls(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2765 */     XSNamedMap components = srcGrammar.getComponents((short)1);
/* 2766 */     int len = components.getLength();
/*      */ 
/* 2770 */     for (int i = 0; i < len; i++) {
/* 2771 */       XSAttributeDecl srcDecl = (XSAttributeDecl)components.item(i);
/* 2772 */       XSAttributeDecl dstDecl = dstGrammar.getGlobalAttributeDecl(srcDecl.getName());
/* 2773 */       if (dstDecl == null) {
/* 2774 */         dstGrammar.addGlobalAttributeDecl(srcDecl);
/*      */       }
/* 2776 */       else if ((dstDecl != srcDecl) && (!this.fTolerateDuplicates)) {
/* 2777 */         reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2782 */     ObjectList componentsExt = srcGrammar.getComponentsExt((short)1);
/* 2783 */     len = componentsExt.getLength();
/*      */ 
/* 2785 */     for (int i = 0; i < len; i += 2) {
/* 2786 */       String key = (String)componentsExt.item(i);
/* 2787 */       int index = key.indexOf(',');
/* 2788 */       String location = key.substring(0, index);
/* 2789 */       String name = key.substring(index + 1, key.length());
/*      */ 
/* 2791 */       XSAttributeDecl srcDecl = (XSAttributeDecl)componentsExt.item(i + 1);
/* 2792 */       XSAttributeDecl dstDecl = dstGrammar.getGlobalAttributeDecl(name, location);
/* 2793 */       if (dstDecl == null) {
/* 2794 */         dstGrammar.addGlobalAttributeDecl(srcDecl, location);
/*      */       }
/* 2797 */       else if (dstDecl == srcDecl);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addGlobalAttributeGroupDecls(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2803 */     XSNamedMap components = srcGrammar.getComponents((short)5);
/* 2804 */     int len = components.getLength();
/*      */ 
/* 2808 */     for (int i = 0; i < len; i++) {
/* 2809 */       XSAttributeGroupDecl srcDecl = (XSAttributeGroupDecl)components.item(i);
/* 2810 */       XSAttributeGroupDecl dstDecl = dstGrammar.getGlobalAttributeGroupDecl(srcDecl.getName());
/* 2811 */       if (dstDecl == null) {
/* 2812 */         dstGrammar.addGlobalAttributeGroupDecl(srcDecl);
/*      */       }
/* 2814 */       else if ((dstDecl != srcDecl) && (!this.fTolerateDuplicates)) {
/* 2815 */         reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2820 */     ObjectList componentsExt = srcGrammar.getComponentsExt((short)5);
/* 2821 */     len = componentsExt.getLength();
/*      */ 
/* 2823 */     for (int i = 0; i < len; i += 2) {
/* 2824 */       String key = (String)componentsExt.item(i);
/* 2825 */       int index = key.indexOf(',');
/* 2826 */       String location = key.substring(0, index);
/* 2827 */       String name = key.substring(index + 1, key.length());
/*      */ 
/* 2829 */       XSAttributeGroupDecl srcDecl = (XSAttributeGroupDecl)componentsExt.item(i + 1);
/* 2830 */       XSAttributeGroupDecl dstDecl = dstGrammar.getGlobalAttributeGroupDecl(name, location);
/* 2831 */       if (dstDecl == null) {
/* 2832 */         dstGrammar.addGlobalAttributeGroupDecl(srcDecl, location);
/*      */       }
/* 2835 */       else if (dstDecl == srcDecl);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addGlobalNotationDecls(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2841 */     XSNamedMap components = srcGrammar.getComponents((short)11);
/* 2842 */     int len = components.getLength();
/*      */ 
/* 2846 */     for (int i = 0; i < len; i++) {
/* 2847 */       XSNotationDecl srcDecl = (XSNotationDecl)components.item(i);
/* 2848 */       XSNotationDecl dstDecl = dstGrammar.getGlobalNotationDecl(srcDecl.getName());
/* 2849 */       if (dstDecl == null) {
/* 2850 */         dstGrammar.addGlobalNotationDecl(srcDecl);
/*      */       }
/* 2852 */       else if ((dstDecl != srcDecl) && (!this.fTolerateDuplicates)) {
/* 2853 */         reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2858 */     ObjectList componentsExt = srcGrammar.getComponentsExt((short)11);
/* 2859 */     len = componentsExt.getLength();
/*      */ 
/* 2861 */     for (int i = 0; i < len; i += 2) {
/* 2862 */       String key = (String)componentsExt.item(i);
/* 2863 */       int index = key.indexOf(',');
/* 2864 */       String location = key.substring(0, index);
/* 2865 */       String name = key.substring(index + 1, key.length());
/*      */ 
/* 2867 */       XSNotationDecl srcDecl = (XSNotationDecl)componentsExt.item(i + 1);
/* 2868 */       XSNotationDecl dstDecl = dstGrammar.getGlobalNotationDecl(name, location);
/* 2869 */       if (dstDecl == null) {
/* 2870 */         dstGrammar.addGlobalNotationDecl(srcDecl, location);
/*      */       }
/* 2873 */       else if (dstDecl == srcDecl);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addGlobalGroupDecls(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2879 */     XSNamedMap components = srcGrammar.getComponents((short)6);
/* 2880 */     int len = components.getLength();
/*      */ 
/* 2884 */     for (int i = 0; i < len; i++) {
/* 2885 */       XSGroupDecl srcDecl = (XSGroupDecl)components.item(i);
/* 2886 */       XSGroupDecl dstDecl = dstGrammar.getGlobalGroupDecl(srcDecl.getName());
/* 2887 */       if (dstDecl == null) {
/* 2888 */         dstGrammar.addGlobalGroupDecl(srcDecl);
/*      */       }
/* 2890 */       else if ((srcDecl != dstDecl) && (!this.fTolerateDuplicates)) {
/* 2891 */         reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2896 */     ObjectList componentsExt = srcGrammar.getComponentsExt((short)6);
/* 2897 */     len = componentsExt.getLength();
/*      */ 
/* 2899 */     for (int i = 0; i < len; i += 2) {
/* 2900 */       String key = (String)componentsExt.item(i);
/* 2901 */       int index = key.indexOf(',');
/* 2902 */       String location = key.substring(0, index);
/* 2903 */       String name = key.substring(index + 1, key.length());
/*      */ 
/* 2905 */       XSGroupDecl srcDecl = (XSGroupDecl)componentsExt.item(i + 1);
/* 2906 */       XSGroupDecl dstDecl = dstGrammar.getGlobalGroupDecl(name, location);
/* 2907 */       if (dstDecl == null) {
/* 2908 */         dstGrammar.addGlobalGroupDecl(srcDecl, location);
/*      */       }
/* 2911 */       else if (dstDecl == srcDecl);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addGlobalTypeDecls(SchemaGrammar srcGrammar, SchemaGrammar dstGrammar)
/*      */   {
/* 2917 */     XSNamedMap components = srcGrammar.getComponents((short)3);
/* 2918 */     int len = components.getLength();
/*      */ 
/* 2922 */     for (int i = 0; i < len; i++) {
/* 2923 */       XSTypeDefinition srcDecl = (XSTypeDefinition)components.item(i);
/* 2924 */       XSTypeDefinition dstDecl = dstGrammar.getGlobalTypeDecl(srcDecl.getName());
/* 2925 */       if (dstDecl == null) {
/* 2926 */         dstGrammar.addGlobalTypeDecl(srcDecl);
/*      */       }
/* 2928 */       else if ((dstDecl != srcDecl) && (!this.fTolerateDuplicates)) {
/* 2929 */         reportSharingError(srcDecl.getNamespace(), srcDecl.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2934 */     ObjectList componentsExt = srcGrammar.getComponentsExt((short)3);
/* 2935 */     len = componentsExt.getLength();
/*      */ 
/* 2937 */     for (int i = 0; i < len; i += 2) {
/* 2938 */       String key = (String)componentsExt.item(i);
/* 2939 */       int index = key.indexOf(',');
/* 2940 */       String location = key.substring(0, index);
/* 2941 */       String name = key.substring(index + 1, key.length());
/*      */ 
/* 2943 */       XSTypeDefinition srcDecl = (XSTypeDefinition)componentsExt.item(i + 1);
/* 2944 */       XSTypeDefinition dstDecl = dstGrammar.getGlobalTypeDecl(name, location);
/* 2945 */       if (dstDecl == null) {
/* 2946 */         dstGrammar.addGlobalTypeDecl(srcDecl, location);
/*      */       }
/* 2949 */       else if (dstDecl == srcDecl);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Vector expandComponents(XSObject[] components, Map<String, Vector> dependencies)
/*      */   {
/* 2955 */     Vector newComponents = new Vector();
/*      */ 
/* 2957 */     for (int i = 0; i < components.length; i++) {
/* 2958 */       if (!newComponents.contains(components[i])) {
/* 2959 */         newComponents.add(components[i]);
/*      */       }
/*      */     }
/*      */ 
/* 2963 */     for (int i = 0; i < newComponents.size(); i++) {
/* 2964 */       XSObject component = (XSObject)newComponents.elementAt(i);
/* 2965 */       expandRelatedComponents(component, newComponents, dependencies);
/*      */     }
/*      */ 
/* 2968 */     return newComponents;
/*      */   }
/*      */ 
/*      */   private void expandRelatedComponents(XSObject component, Vector componentList, Map<String, Vector> dependencies) {
/* 2972 */     short componentType = component.getType();
/* 2973 */     switch (componentType) {
/*      */     case 3:
/* 2975 */       expandRelatedTypeComponents((XSTypeDefinition)component, componentList, component.getNamespace(), dependencies);
/* 2976 */       break;
/*      */     case 1:
/* 2978 */       expandRelatedAttributeComponents((XSAttributeDeclaration)component, componentList, component.getNamespace(), dependencies);
/* 2979 */       break;
/*      */     case 5:
/* 2981 */       expandRelatedAttributeGroupComponents((XSAttributeGroupDefinition)component, componentList, component.getNamespace(), dependencies);
/*      */     case 2:
/* 2983 */       expandRelatedElementComponents((XSElementDeclaration)component, componentList, component.getNamespace(), dependencies);
/* 2984 */       break;
/*      */     case 6:
/* 2986 */       expandRelatedModelGroupDefinitionComponents((XSModelGroupDefinition)component, componentList, component.getNamespace(), dependencies);
/*      */     case 4:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     }
/*      */   }
/*      */ 
/*      */   private void expandRelatedAttributeComponents(XSAttributeDeclaration decl, Vector componentList, String namespace, Map<String, Vector> dependencies) {
/* 2997 */     addRelatedType(decl.getTypeDefinition(), componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedElementComponents(XSElementDeclaration decl, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3006 */     addRelatedType(decl.getTypeDefinition(), componentList, namespace, dependencies);
/*      */ 
/* 3013 */     XSElementDeclaration subElemDecl = decl.getSubstitutionGroupAffiliation();
/* 3014 */     if (subElemDecl != null)
/* 3015 */       addRelatedElement(subElemDecl, componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedTypeComponents(XSTypeDefinition type, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3020 */     if ((type instanceof XSComplexTypeDecl)) {
/* 3021 */       expandRelatedComplexTypeComponents((XSComplexTypeDecl)type, componentList, namespace, dependencies);
/*      */     }
/* 3023 */     else if ((type instanceof XSSimpleTypeDecl))
/* 3024 */       expandRelatedSimpleTypeComponents((XSSimpleTypeDefinition)type, componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedModelGroupDefinitionComponents(XSModelGroupDefinition modelGroupDef, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3030 */     expandRelatedModelGroupComponents(modelGroupDef.getModelGroup(), componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedAttributeGroupComponents(XSAttributeGroupDefinition attrGroup, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3035 */     expandRelatedAttributeUsesComponents(attrGroup.getAttributeUses(), componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedComplexTypeComponents(XSComplexTypeDecl type, Vector componentList, String namespace, Map<String, Vector> dependencies) {
/* 3039 */     addRelatedType(type.getBaseType(), componentList, namespace, dependencies);
/* 3040 */     expandRelatedAttributeUsesComponents(type.getAttributeUses(), componentList, namespace, dependencies);
/* 3041 */     XSParticle particle = type.getParticle();
/* 3042 */     if (particle != null)
/* 3043 */       expandRelatedParticleComponents(particle, componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedSimpleTypeComponents(XSSimpleTypeDefinition type, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3048 */     XSTypeDefinition baseType = type.getBaseType();
/* 3049 */     if (baseType != null) {
/* 3050 */       addRelatedType(baseType, componentList, namespace, dependencies);
/*      */     }
/*      */ 
/* 3053 */     XSTypeDefinition itemType = type.getItemType();
/* 3054 */     if (itemType != null) {
/* 3055 */       addRelatedType(itemType, componentList, namespace, dependencies);
/*      */     }
/*      */ 
/* 3058 */     XSTypeDefinition primitiveType = type.getPrimitiveType();
/* 3059 */     if (primitiveType != null) {
/* 3060 */       addRelatedType(primitiveType, componentList, namespace, dependencies);
/*      */     }
/*      */ 
/* 3063 */     XSObjectList memberTypes = type.getMemberTypes();
/* 3064 */     if (memberTypes.size() > 0)
/* 3065 */       for (int i = 0; i < memberTypes.size(); i++)
/* 3066 */         addRelatedType((XSTypeDefinition)memberTypes.item(i), componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedAttributeUsesComponents(XSObjectList attrUses, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3073 */     int attrUseSize = attrUses == null ? 0 : attrUses.size();
/* 3074 */     for (int i = 0; i < attrUseSize; i++)
/* 3075 */       expandRelatedAttributeUseComponents((XSAttributeUse)attrUses.item(i), componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedAttributeUseComponents(XSAttributeUse component, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3081 */     addRelatedAttribute(component.getAttrDeclaration(), componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void expandRelatedParticleComponents(XSParticle component, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3086 */     XSTerm term = component.getTerm();
/* 3087 */     switch (term.getType()) {
/*      */     case 2:
/* 3089 */       addRelatedElement((XSElementDeclaration)term, componentList, namespace, dependencies);
/* 3090 */       break;
/*      */     case 7:
/* 3092 */       expandRelatedModelGroupComponents((XSModelGroup)term, componentList, namespace, dependencies);
/* 3093 */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void expandRelatedModelGroupComponents(XSModelGroup modelGroup, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3101 */     XSObjectList particles = modelGroup.getParticles();
/* 3102 */     int length = particles == null ? 0 : particles.getLength();
/* 3103 */     for (int i = 0; i < length; i++)
/* 3104 */       expandRelatedParticleComponents((XSParticle)particles.item(i), componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void addRelatedType(XSTypeDefinition type, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3109 */     if (!type.getAnonymous()) {
/* 3110 */       if ((!type.getNamespace().equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && 
/* 3111 */         (!componentList.contains(type))) {
/* 3112 */         Vector importedNamespaces = findDependentNamespaces(namespace, dependencies);
/* 3113 */         addNamespaceDependency(namespace, type.getNamespace(), importedNamespaces);
/* 3114 */         componentList.add(type);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 3119 */       expandRelatedTypeComponents(type, componentList, namespace, dependencies);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addRelatedElement(XSElementDeclaration decl, Vector componentList, String namespace, Map<String, Vector> dependencies) {
/* 3124 */     if (decl.getScope() == 1) {
/* 3125 */       if (!componentList.contains(decl)) {
/* 3126 */         Vector importedNamespaces = findDependentNamespaces(namespace, dependencies);
/* 3127 */         addNamespaceDependency(namespace, decl.getNamespace(), importedNamespaces);
/* 3128 */         componentList.add(decl);
/*      */       }
/*      */     }
/*      */     else
/* 3132 */       expandRelatedElementComponents(decl, componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void addRelatedAttribute(XSAttributeDeclaration decl, Vector componentList, String namespace, Map<String, Vector> dependencies)
/*      */   {
/* 3137 */     if (decl.getScope() == 1) {
/* 3138 */       if (!componentList.contains(decl)) {
/* 3139 */         Vector importedNamespaces = findDependentNamespaces(namespace, dependencies);
/* 3140 */         addNamespaceDependency(namespace, decl.getNamespace(), importedNamespaces);
/* 3141 */         componentList.add(decl);
/*      */       }
/*      */     }
/*      */     else
/* 3145 */       expandRelatedAttributeComponents(decl, componentList, namespace, dependencies);
/*      */   }
/*      */ 
/*      */   private void addGlobalComponents(Vector components, Map<String, Vector> importDependencies)
/*      */   {
/* 3150 */     XSDDescription desc = new XSDDescription();
/* 3151 */     int size = components.size();
/*      */ 
/* 3153 */     for (int i = 0; i < size; i++) {
/* 3154 */       addGlobalComponent((XSObject)components.elementAt(i), desc);
/*      */     }
/* 3156 */     updateImportDependencies(importDependencies);
/*      */   }
/*      */ 
/*      */   private void addGlobalComponent(XSObject component, XSDDescription desc) {
/* 3160 */     String namespace = component.getNamespace();
/*      */ 
/* 3162 */     desc.setNamespace(namespace);
/* 3163 */     SchemaGrammar sg = getSchemaGrammar(desc);
/*      */ 
/* 3165 */     short componentType = component.getType();
/* 3166 */     String name = component.getName();
/*      */ 
/* 3168 */     switch (componentType) {
/*      */     case 3:
/* 3170 */       if (!((XSTypeDefinition)component).getAnonymous()) {
/* 3171 */         if (sg.getGlobalTypeDecl(name) == null) {
/* 3172 */           sg.addGlobalTypeDecl((XSTypeDefinition)component);
/*      */         }
/*      */ 
/* 3175 */         if (sg.getGlobalTypeDecl(name, "") == null)
/* 3176 */           sg.addGlobalTypeDecl((XSTypeDefinition)component, "");  } break;
/*      */     case 1:
/* 3181 */       if (((XSAttributeDecl)component).getScope() == 1) {
/* 3182 */         if (sg.getGlobalAttributeDecl(name) == null) {
/* 3183 */           sg.addGlobalAttributeDecl((XSAttributeDecl)component);
/*      */         }
/*      */ 
/* 3186 */         if (sg.getGlobalAttributeDecl(name, "") == null)
/* 3187 */           sg.addGlobalAttributeDecl((XSAttributeDecl)component, "");  } break;
/*      */     case 5:
/* 3192 */       if (sg.getGlobalAttributeDecl(name) == null) {
/* 3193 */         sg.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)component);
/*      */       }
/*      */ 
/* 3196 */       if (sg.getGlobalAttributeDecl(name, "") == null)
/* 3197 */         sg.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)component, ""); break;
/*      */     case 2:
/* 3201 */       if (((XSElementDecl)component).getScope() == 1) {
/* 3202 */         sg.addGlobalElementDeclAll((XSElementDecl)component);
/*      */ 
/* 3204 */         if (sg.getGlobalElementDecl(name) == null) {
/* 3205 */           sg.addGlobalElementDecl((XSElementDecl)component);
/*      */         }
/*      */ 
/* 3208 */         if (sg.getGlobalElementDecl(name, "") == null)
/* 3209 */           sg.addGlobalElementDecl((XSElementDecl)component, "");  } break;
/*      */     case 6:
/* 3214 */       if (sg.getGlobalGroupDecl(name) == null) {
/* 3215 */         sg.addGlobalGroupDecl((XSGroupDecl)component);
/*      */       }
/*      */ 
/* 3218 */       if (sg.getGlobalGroupDecl(name, "") == null)
/* 3219 */         sg.addGlobalGroupDecl((XSGroupDecl)component, ""); break;
/*      */     case 11:
/* 3223 */       if (sg.getGlobalNotationDecl(name) == null) {
/* 3224 */         sg.addGlobalNotationDecl((XSNotationDecl)component);
/*      */       }
/*      */ 
/* 3227 */       if (sg.getGlobalNotationDecl(name, "") == null)
/* 3228 */         sg.addGlobalNotationDecl((XSNotationDecl)component, ""); break;
/*      */     case 4:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateImportDependencies(Map<String, Vector> table)
/*      */   {
/* 3239 */     if (table == null) return;
/*      */ 
/* 3243 */     for (Map.Entry entry : table.entrySet()) {
/* 3244 */       String namespace = (String)entry.getKey();
/* 3245 */       Vector importList = (Vector)entry.getValue();
/* 3246 */       if (importList.size() > 0)
/* 3247 */         expandImportList(namespace, importList);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void expandImportList(String namespace, Vector namespaceList)
/*      */   {
/* 3253 */     SchemaGrammar sg = this.fGrammarBucket.getGrammar(namespace);
/*      */ 
/* 3255 */     if (sg != null) {
/* 3256 */       Vector isgs = sg.getImportedGrammars();
/* 3257 */       if (isgs == null) {
/* 3258 */         isgs = new Vector();
/* 3259 */         addImportList(sg, isgs, namespaceList);
/* 3260 */         sg.setImportedGrammars(isgs);
/*      */       }
/*      */       else {
/* 3263 */         updateImportList(sg, isgs, namespaceList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addImportList(SchemaGrammar sg, Vector importedGrammars, Vector namespaceList) {
/* 3269 */     int size = namespaceList.size();
/*      */ 
/* 3272 */     for (int i = 0; i < size; i++) {
/* 3273 */       SchemaGrammar isg = this.fGrammarBucket.getGrammar((String)namespaceList.elementAt(i));
/* 3274 */       if (isg != null)
/* 3275 */         importedGrammars.add(isg);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateImportList(SchemaGrammar sg, Vector importedGrammars, Vector namespaceList)
/*      */   {
/* 3284 */     int size = namespaceList.size();
/*      */ 
/* 3287 */     for (int i = 0; i < size; i++) {
/* 3288 */       SchemaGrammar isg = this.fGrammarBucket.getGrammar((String)namespaceList.elementAt(i));
/* 3289 */       if ((isg != null) && 
/* 3290 */         (!containedImportedGrammar(importedGrammars, isg)))
/* 3291 */         importedGrammars.add(isg);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean containedImportedGrammar(Vector importedGrammar, SchemaGrammar grammar)
/*      */   {
/* 3301 */     int size = importedGrammar.size();
/*      */ 
/* 3304 */     for (int i = 0; i < size; i++) {
/* 3305 */       SchemaGrammar sg = (SchemaGrammar)importedGrammar.elementAt(i);
/* 3306 */       if (null2EmptyString(sg.getTargetNamespace()).equals(null2EmptyString(grammar.getTargetNamespace()))) {
/* 3307 */         return true;
/*      */       }
/*      */     }
/* 3310 */     return false;
/*      */   }
/*      */ 
/*      */   private SchemaGrammar getSchemaGrammar(XSDDescription desc)
/*      */   {
/* 3316 */     SchemaGrammar sg = findGrammar(desc, this.fNamespaceGrowth);
/*      */ 
/* 3318 */     if (sg == null) {
/* 3319 */       sg = new SchemaGrammar(desc.getNamespace(), desc.makeClone(), this.fSymbolTable);
/* 3320 */       this.fGrammarBucket.putGrammar(sg);
/*      */     }
/* 3322 */     else if (sg.isImmutable()) {
/* 3323 */       sg = createGrammarFrom(sg);
/*      */     }
/*      */ 
/* 3326 */     return sg;
/*      */   }
/*      */ 
/*      */   private Vector findDependentNamespaces(String namespace, Map table) {
/* 3330 */     String ns = null2EmptyString(namespace);
/* 3331 */     Vector namespaceList = (Vector)getFromMap(table, ns);
/*      */ 
/* 3333 */     if (namespaceList == null) {
/* 3334 */       namespaceList = new Vector();
/* 3335 */       table.put(ns, namespaceList);
/*      */     }
/*      */ 
/* 3338 */     return namespaceList;
/*      */   }
/*      */ 
/*      */   private void addNamespaceDependency(String namespace1, String namespace2, Vector list) {
/* 3342 */     String ns1 = null2EmptyString(namespace1);
/* 3343 */     String ns2 = null2EmptyString(namespace2);
/* 3344 */     if ((!ns1.equals(ns2)) && 
/* 3345 */       (!list.contains(ns2)))
/* 3346 */       list.add(ns2);
/*      */   }
/*      */ 
/*      */   private void reportSharingError(String namespace, String name)
/*      */   {
/* 3352 */     String qName = namespace + "," + name;
/*      */ 
/* 3355 */     reportSchemaError("sch-props-correct.2", new Object[] { qName }, null);
/*      */   }
/*      */ 
/*      */   private void createTraversers()
/*      */   {
/* 3364 */     this.fAttributeChecker = new XSAttributeChecker(this);
/* 3365 */     this.fAttributeGroupTraverser = new XSDAttributeGroupTraverser(this, this.fAttributeChecker);
/* 3366 */     this.fAttributeTraverser = new XSDAttributeTraverser(this, this.fAttributeChecker);
/* 3367 */     this.fComplexTypeTraverser = new XSDComplexTypeTraverser(this, this.fAttributeChecker);
/* 3368 */     this.fElementTraverser = new XSDElementTraverser(this, this.fAttributeChecker);
/* 3369 */     this.fGroupTraverser = new XSDGroupTraverser(this, this.fAttributeChecker);
/* 3370 */     this.fKeyrefTraverser = new XSDKeyrefTraverser(this, this.fAttributeChecker);
/* 3371 */     this.fNotationTraverser = new XSDNotationTraverser(this, this.fAttributeChecker);
/* 3372 */     this.fSimpleTypeTraverser = new XSDSimpleTypeTraverser(this, this.fAttributeChecker);
/* 3373 */     this.fUniqueOrKeyTraverser = new XSDUniqueOrKeyTraverser(this, this.fAttributeChecker);
/* 3374 */     this.fWildCardTraverser = new XSDWildcardTraverser(this, this.fAttributeChecker);
/*      */   }
/*      */ 
/*      */   void prepareForParse()
/*      */   {
/* 3380 */     this.fTraversed.clear();
/* 3381 */     this.fDoc2SystemId.clear();
/* 3382 */     this.fHiddenNodes.clear();
/* 3383 */     this.fLastSchemaWasDuplicate = false;
/*      */   }
/*      */ 
/*      */   void prepareForTraverse()
/*      */   {
/* 3389 */     if (!this.registryEmpty) {
/* 3390 */       this.fUnparsedAttributeRegistry.clear();
/* 3391 */       this.fUnparsedAttributeGroupRegistry.clear();
/* 3392 */       this.fUnparsedElementRegistry.clear();
/* 3393 */       this.fUnparsedGroupRegistry.clear();
/* 3394 */       this.fUnparsedIdentityConstraintRegistry.clear();
/* 3395 */       this.fUnparsedNotationRegistry.clear();
/* 3396 */       this.fUnparsedTypeRegistry.clear();
/*      */ 
/* 3398 */       this.fUnparsedAttributeRegistrySub.clear();
/* 3399 */       this.fUnparsedAttributeGroupRegistrySub.clear();
/* 3400 */       this.fUnparsedElementRegistrySub.clear();
/* 3401 */       this.fUnparsedGroupRegistrySub.clear();
/* 3402 */       this.fUnparsedIdentityConstraintRegistrySub.clear();
/* 3403 */       this.fUnparsedNotationRegistrySub.clear();
/* 3404 */       this.fUnparsedTypeRegistrySub.clear();
/*      */     }
/*      */ 
/* 3407 */     for (int i = 1; i <= 7; i++) {
/* 3408 */       if (this.fUnparsedRegistriesExt[i] != null) {
/* 3409 */         this.fUnparsedRegistriesExt[i].clear();
/*      */       }
/*      */     }
/* 3412 */     this.fDependencyMap.clear();
/* 3413 */     this.fDoc2XSDocumentMap.clear();
/* 3414 */     if (this.fRedefine2XSDMap != null) this.fRedefine2XSDMap.clear();
/* 3415 */     if (this.fRedefine2NSSupport != null) this.fRedefine2NSSupport.clear();
/* 3416 */     this.fAllTNSs.removeAllElements();
/* 3417 */     this.fImportMap.clear();
/* 3418 */     this.fRoot = null;
/*      */ 
/* 3421 */     for (int i = 0; i < this.fLocalElemStackPos; i++) {
/* 3422 */       this.fParticle[i] = null;
/* 3423 */       this.fLocalElementDecl[i] = null;
/* 3424 */       this.fLocalElementDecl_schema[i] = null;
/* 3425 */       this.fLocalElemNamespaceContext[i] = null;
/*      */     }
/* 3427 */     this.fLocalElemStackPos = 0;
/*      */ 
/* 3430 */     for (int i = 0; i < this.fKeyrefStackPos; i++) {
/* 3431 */       this.fKeyrefs[i] = null;
/* 3432 */       this.fKeyrefElems[i] = null;
/* 3433 */       this.fKeyrefNamespaceContext[i] = null;
/* 3434 */       this.fKeyrefsMapXSDocumentInfo[i] = null;
/*      */     }
/* 3436 */     this.fKeyrefStackPos = 0;
/*      */ 
/* 3439 */     if (this.fAttributeChecker == null) {
/* 3440 */       createTraversers();
/*      */     }
/*      */ 
/* 3444 */     Locale locale = this.fErrorReporter.getLocale();
/* 3445 */     this.fAttributeChecker.reset(this.fSymbolTable);
/* 3446 */     this.fAttributeGroupTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3447 */     this.fAttributeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3448 */     this.fComplexTypeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3449 */     this.fElementTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3450 */     this.fGroupTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3451 */     this.fKeyrefTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3452 */     this.fNotationTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3453 */     this.fSimpleTypeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3454 */     this.fUniqueOrKeyTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/* 3455 */     this.fWildCardTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
/*      */ 
/* 3457 */     this.fRedefinedRestrictedAttributeGroupRegistry.clear();
/* 3458 */     this.fRedefinedRestrictedGroupRegistry.clear();
/*      */ 
/* 3460 */     this.fGlobalAttrDecls.clear();
/* 3461 */     this.fGlobalAttrGrpDecls.clear();
/* 3462 */     this.fGlobalElemDecls.clear();
/* 3463 */     this.fGlobalGroupDecls.clear();
/* 3464 */     this.fGlobalNotationDecls.clear();
/* 3465 */     this.fGlobalIDConstraintDecls.clear();
/* 3466 */     this.fGlobalTypeDecls.clear();
/*      */   }
/*      */   public void setDeclPool(XSDeclarationPool declPool) {
/* 3469 */     this.fDeclPool = declPool;
/*      */   }
/*      */   public void setDVFactory(SchemaDVFactory dvFactory) {
/* 3472 */     this.fDVFactory = dvFactory;
/*      */   }
/*      */   public SchemaDVFactory getDVFactory() {
/* 3475 */     return this.fDVFactory;
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */   {
/* 3481 */     this.fSymbolTable = ((SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/*      */ 
/* 3483 */     this.fSecureProcessing = null;
/* 3484 */     if (componentManager != null) {
/* 3485 */       this.fSecureProcessing = ((SecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null));
/*      */     }
/*      */ 
/* 3489 */     this.fEntityResolver = ((XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
/* 3490 */     XMLEntityResolver er = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
/* 3491 */     if (er != null) {
/* 3492 */       this.fSchemaParser.setEntityResolver(er);
/*      */     }
/*      */ 
/* 3495 */     this.fErrorReporter = ((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*      */     try
/*      */     {
/* 3498 */       XMLErrorHandler currErrorHandler = this.fErrorReporter.getErrorHandler();
/*      */ 
/* 3502 */       if (currErrorHandler != this.fSchemaParser.getProperty("http://apache.org/xml/properties/internal/error-handler")) {
/* 3503 */         this.fSchemaParser.setProperty("http://apache.org/xml/properties/internal/error-handler", currErrorHandler != null ? currErrorHandler : new DefaultErrorHandler());
/* 3504 */         if (this.fAnnotationValidator != null) {
/* 3505 */           this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/internal/error-handler", currErrorHandler != null ? currErrorHandler : new DefaultErrorHandler());
/*      */         }
/*      */       }
/* 3508 */       Locale currentLocale = this.fErrorReporter.getLocale();
/* 3509 */       if (currentLocale != this.fSchemaParser.getProperty("http://apache.org/xml/properties/locale")) {
/* 3510 */         this.fSchemaParser.setProperty("http://apache.org/xml/properties/locale", currentLocale);
/* 3511 */         if (this.fAnnotationValidator != null)
/* 3512 */           this.fAnnotationValidator.setProperty("http://apache.org/xml/properties/locale", currentLocale);
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/* 3518 */     this.fValidateAnnotations = componentManager.getFeature("http://apache.org/xml/features/validate-annotations", false);
/* 3519 */     this.fHonourAllSchemaLocations = componentManager.getFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
/* 3520 */     this.fNamespaceGrowth = componentManager.getFeature("http://apache.org/xml/features/namespace-growth", false);
/* 3521 */     this.fTolerateDuplicates = componentManager.getFeature("http://apache.org/xml/features/internal/tolerate-duplicates", false);
/*      */     try
/*      */     {
/* 3524 */       this.fSchemaParser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", this.fErrorReporter.getFeature("http://apache.org/xml/features/continue-after-fatal-error"));
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */     try
/*      */     {
/* 3531 */       if (componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false))
/* 3532 */         this.fSchemaParser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/*      */     }
/*      */     try {
/* 3537 */       if (componentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false))
/* 3538 */         this.fSchemaParser.setFeature("http://apache.org/xml/features/standard-uri-conformant", true);
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */     try {
/* 3544 */       this.fGrammarPool = ((XMLGrammarPool)componentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool"));
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/* 3547 */       this.fGrammarPool = null;
/*      */     }
/*      */     try
/*      */     {
/* 3551 */       if (componentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false))
/* 3552 */         this.fSchemaParser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/*      */     }
/*      */     try {
/* 3557 */       Object security = componentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
/* 3558 */       if (security != null)
/* 3559 */         this.fSchemaParser.setProperty("http://apache.org/xml/properties/security-manager", security);
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   void traverseLocalElements()
/*      */   {
/* 3572 */     this.fElementTraverser.fDeferTraversingLocalElements = false;
/*      */ 
/* 3574 */     for (int i = 0; i < this.fLocalElemStackPos; i++) {
/* 3575 */       Element currElem = this.fLocalElementDecl[i];
/*      */ 
/* 3578 */       XSDocumentInfo currSchema = this.fLocalElementDecl_schema[i];
/* 3579 */       SchemaGrammar currGrammar = this.fGrammarBucket.getGrammar(currSchema.fTargetNamespace);
/* 3580 */       this.fElementTraverser.traverseLocal(this.fParticle[i], currElem, currSchema, currGrammar, this.fAllContext[i], this.fParent[i], this.fLocalElemNamespaceContext[i]);
/*      */ 
/* 3582 */       if (this.fParticle[i].fType == 0) {
/* 3583 */         XSModelGroupImpl group = null;
/* 3584 */         if ((this.fParent[i] instanceof XSComplexTypeDecl)) {
/* 3585 */           XSParticle p = ((XSComplexTypeDecl)this.fParent[i]).getParticle();
/* 3586 */           if (p != null)
/* 3587 */             group = (XSModelGroupImpl)p.getTerm();
/*      */         }
/*      */         else {
/* 3590 */           group = ((XSGroupDecl)this.fParent[i]).fModelGroup;
/*      */         }
/* 3592 */         if (group != null)
/* 3593 */           removeParticle(group, this.fParticle[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean removeParticle(XSModelGroupImpl group, XSParticleDecl particle)
/*      */   {
/* 3600 */     for (int i = 0; i < group.fParticleCount; i++) {
/* 3601 */       XSParticleDecl member = group.fParticles[i];
/* 3602 */       if (member == particle) {
/* 3603 */         for (int j = i; j < group.fParticleCount - 1; j++)
/* 3604 */           group.fParticles[j] = group.fParticles[(j + 1)];
/* 3605 */         group.fParticleCount -= 1;
/* 3606 */         return true;
/*      */       }
/* 3608 */       if ((member.fType == 3) && 
/* 3609 */         (removeParticle((XSModelGroupImpl)member.fValue, particle))) {
/* 3610 */         return true;
/*      */       }
/*      */     }
/* 3613 */     return false;
/*      */   }
/*      */ 
/*      */   void fillInLocalElemInfo(Element elmDecl, XSDocumentInfo schemaDoc, int allContextFlags, XSObject parent, XSParticleDecl particle)
/*      */   {
/* 3625 */     if (this.fParticle.length == this.fLocalElemStackPos)
/*      */     {
/* 3627 */       XSParticleDecl[] newStackP = new XSParticleDecl[this.fLocalElemStackPos + 10];
/* 3628 */       System.arraycopy(this.fParticle, 0, newStackP, 0, this.fLocalElemStackPos);
/* 3629 */       this.fParticle = newStackP;
/* 3630 */       Element[] newStackE = new Element[this.fLocalElemStackPos + 10];
/* 3631 */       System.arraycopy(this.fLocalElementDecl, 0, newStackE, 0, this.fLocalElemStackPos);
/* 3632 */       this.fLocalElementDecl = newStackE;
/* 3633 */       XSDocumentInfo[] newStackE_schema = new XSDocumentInfo[this.fLocalElemStackPos + 10];
/* 3634 */       System.arraycopy(this.fLocalElementDecl_schema, 0, newStackE_schema, 0, this.fLocalElemStackPos);
/* 3635 */       this.fLocalElementDecl_schema = newStackE_schema;
/* 3636 */       int[] newStackI = new int[this.fLocalElemStackPos + 10];
/* 3637 */       System.arraycopy(this.fAllContext, 0, newStackI, 0, this.fLocalElemStackPos);
/* 3638 */       this.fAllContext = newStackI;
/* 3639 */       XSObject[] newStackC = new XSObject[this.fLocalElemStackPos + 10];
/* 3640 */       System.arraycopy(this.fParent, 0, newStackC, 0, this.fLocalElemStackPos);
/* 3641 */       this.fParent = newStackC;
/* 3642 */       String[][] newStackN = new String[this.fLocalElemStackPos + 10][];
/* 3643 */       System.arraycopy(this.fLocalElemNamespaceContext, 0, newStackN, 0, this.fLocalElemStackPos);
/* 3644 */       this.fLocalElemNamespaceContext = newStackN;
/*      */     }
/*      */ 
/* 3647 */     this.fParticle[this.fLocalElemStackPos] = particle;
/* 3648 */     this.fLocalElementDecl[this.fLocalElemStackPos] = elmDecl;
/* 3649 */     this.fLocalElementDecl_schema[this.fLocalElemStackPos] = schemaDoc;
/* 3650 */     this.fAllContext[this.fLocalElemStackPos] = allContextFlags;
/* 3651 */     this.fParent[this.fLocalElemStackPos] = parent;
/* 3652 */     this.fLocalElemNamespaceContext[(this.fLocalElemStackPos++)] = schemaDoc.fNamespaceSupport.getEffectiveLocalContext();
/*      */   }
/*      */ 
/*      */   void checkForDuplicateNames(String qName, int declType, Map<String, Element> registry, Map<String, XSDocumentInfo> registry_sub, Element currComp, XSDocumentInfo currSchema)
/*      */   {
/* 3666 */     Object objElem = null;
/*      */ 
/* 3669 */     if ((objElem = registry.get(qName)) == null)
/*      */     {
/* 3672 */       if ((this.fNamespaceGrowth) && (!this.fTolerateDuplicates)) {
/* 3673 */         checkForDuplicateNames(qName, declType, currComp);
/*      */       }
/*      */ 
/* 3676 */       registry.put(qName, currComp);
/* 3677 */       registry_sub.put(qName, currSchema);
/*      */     }
/*      */     else {
/* 3680 */       Element collidingElem = (Element)objElem;
/* 3681 */       XSDocumentInfo collidingElemSchema = (XSDocumentInfo)registry_sub.get(qName);
/* 3682 */       if (collidingElem == currComp) return;
/* 3683 */       Element elemParent = null;
/* 3684 */       XSDocumentInfo redefinedSchema = null;
/*      */ 
/* 3687 */       boolean collidedWithRedefine = true;
/* 3688 */       if (DOMUtil.getLocalName(elemParent = DOMUtil.getParent(collidingElem)).equals(SchemaSymbols.ELT_REDEFINE)) {
/* 3689 */         redefinedSchema = this.fRedefine2XSDMap != null ? (XSDocumentInfo)this.fRedefine2XSDMap.get(elemParent) : null;
/*      */       }
/* 3692 */       else if (DOMUtil.getLocalName(DOMUtil.getParent(currComp)).equals(SchemaSymbols.ELT_REDEFINE)) {
/* 3693 */         redefinedSchema = collidingElemSchema;
/* 3694 */         collidedWithRedefine = false;
/*      */       }
/* 3696 */       if (redefinedSchema != null)
/*      */       {
/* 3699 */         if (collidingElemSchema == currSchema) {
/* 3700 */           reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
/* 3701 */           return;
/*      */         }
/*      */ 
/* 3704 */         String newName = qName.substring(qName.lastIndexOf(',') + 1) + "_fn3dktizrknc9pi";
/* 3705 */         if (redefinedSchema == currSchema)
/*      */         {
/* 3707 */           currComp.setAttribute(SchemaSymbols.ATT_NAME, newName);
/* 3708 */           if (currSchema.fTargetNamespace == null) {
/* 3709 */             registry.put("," + newName, currComp);
/* 3710 */             registry_sub.put("," + newName, currSchema);
/*      */           }
/*      */           else {
/* 3713 */             registry.put(currSchema.fTargetNamespace + "," + newName, currComp);
/* 3714 */             registry_sub.put(currSchema.fTargetNamespace + "," + newName, currSchema);
/*      */           }
/*      */ 
/* 3717 */           if (currSchema.fTargetNamespace == null)
/* 3718 */             checkForDuplicateNames("," + newName, declType, registry, registry_sub, currComp, currSchema);
/*      */           else {
/* 3720 */             checkForDuplicateNames(currSchema.fTargetNamespace + "," + newName, declType, registry, registry_sub, currComp, currSchema);
/*      */           }
/*      */         }
/* 3723 */         else if (collidedWithRedefine) {
/* 3724 */           if (currSchema.fTargetNamespace == null)
/* 3725 */             checkForDuplicateNames("," + newName, declType, registry, registry_sub, currComp, currSchema);
/*      */           else
/* 3727 */             checkForDuplicateNames(currSchema.fTargetNamespace + "," + newName, declType, registry, registry_sub, currComp, currSchema);
/*      */         }
/*      */         else
/*      */         {
/* 3731 */           reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
/*      */         }
/*      */ 
/*      */       }
/* 3739 */       else if (!this.fTolerateDuplicates) {
/* 3740 */         reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
/* 3741 */       } else if ((this.fUnparsedRegistriesExt[declType] != null) && 
/* 3742 */         (this.fUnparsedRegistriesExt[declType].get(qName) == currSchema)) {
/* 3743 */         reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3750 */     if (this.fTolerateDuplicates) {
/* 3751 */       if (this.fUnparsedRegistriesExt[declType] == null)
/* 3752 */         this.fUnparsedRegistriesExt[declType] = new HashMap();
/* 3753 */       this.fUnparsedRegistriesExt[declType].put(qName, currSchema);
/*      */     }
/*      */   }
/*      */ 
/*      */   void checkForDuplicateNames(String qName, int declType, Element currComp)
/*      */   {
/* 3759 */     int namespaceEnd = qName.indexOf(',');
/* 3760 */     String namespace = qName.substring(0, namespaceEnd);
/* 3761 */     SchemaGrammar grammar = this.fGrammarBucket.getGrammar(emptyString2Null(namespace));
/*      */ 
/* 3763 */     if (grammar != null) {
/* 3764 */       Object obj = getGlobalDeclFromGrammar(grammar, declType, qName.substring(namespaceEnd + 1));
/* 3765 */       if (obj != null)
/* 3766 */         reportSchemaError("sch-props-correct.2", new Object[] { qName }, currComp);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void renameRedefiningComponents(XSDocumentInfo currSchema, Element child, String componentType, String oldName, String newName)
/*      */   {
/* 3780 */     if (componentType.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
/* 3781 */       Element grandKid = DOMUtil.getFirstChildElement(child);
/* 3782 */       if (grandKid == null) {
/* 3783 */         reportSchemaError("src-redefine.5.a.a", null, child);
/*      */       }
/*      */       else {
/* 3786 */         String grandKidName = DOMUtil.getLocalName(grandKid);
/* 3787 */         if (grandKidName.equals(SchemaSymbols.ELT_ANNOTATION)) {
/* 3788 */           grandKid = DOMUtil.getNextSiblingElement(grandKid);
/*      */         }
/* 3790 */         if (grandKid == null) {
/* 3791 */           reportSchemaError("src-redefine.5.a.a", null, child);
/*      */         }
/*      */         else {
/* 3794 */           grandKidName = DOMUtil.getLocalName(grandKid);
/* 3795 */           if (!grandKidName.equals(SchemaSymbols.ELT_RESTRICTION)) {
/* 3796 */             reportSchemaError("src-redefine.5.a.b", new Object[] { grandKidName }, child);
/*      */           }
/*      */           else {
/* 3799 */             Object[] attrs = this.fAttributeChecker.checkAttributes(grandKid, false, currSchema);
/* 3800 */             QName derivedBase = (QName)attrs[XSAttributeChecker.ATTIDX_BASE];
/* 3801 */             if ((derivedBase == null) || (derivedBase.uri != currSchema.fTargetNamespace) || (!derivedBase.localpart.equals(oldName)))
/*      */             {
/* 3804 */               reportSchemaError("src-redefine.5.a.c", new Object[] { grandKidName, (currSchema.fTargetNamespace == null ? "" : currSchema.fTargetNamespace) + "," + oldName }, child);
/*      */             }
/* 3812 */             else if ((derivedBase.prefix != null) && (derivedBase.prefix.length() > 0)) {
/* 3813 */               grandKid.setAttribute(SchemaSymbols.ATT_BASE, derivedBase.prefix + ":" + newName);
/*      */             }
/*      */             else {
/* 3816 */               grandKid.setAttribute(SchemaSymbols.ATT_BASE, newName);
/*      */             }
/*      */ 
/* 3819 */             this.fAttributeChecker.returnAttrArray(attrs, currSchema);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 3824 */     else if (componentType.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
/* 3825 */       Element grandKid = DOMUtil.getFirstChildElement(child);
/* 3826 */       if (grandKid == null) {
/* 3827 */         reportSchemaError("src-redefine.5.b.a", null, child);
/*      */       }
/*      */       else {
/* 3830 */         if (DOMUtil.getLocalName(grandKid).equals(SchemaSymbols.ELT_ANNOTATION)) {
/* 3831 */           grandKid = DOMUtil.getNextSiblingElement(grandKid);
/*      */         }
/* 3833 */         if (grandKid == null) {
/* 3834 */           reportSchemaError("src-redefine.5.b.a", null, child);
/*      */         }
/*      */         else
/*      */         {
/* 3838 */           Element greatGrandKid = DOMUtil.getFirstChildElement(grandKid);
/* 3839 */           if (greatGrandKid == null) {
/* 3840 */             reportSchemaError("src-redefine.5.b.b", null, grandKid);
/*      */           }
/*      */           else {
/* 3843 */             String greatGrandKidName = DOMUtil.getLocalName(greatGrandKid);
/* 3844 */             if (greatGrandKidName.equals(SchemaSymbols.ELT_ANNOTATION)) {
/* 3845 */               greatGrandKid = DOMUtil.getNextSiblingElement(greatGrandKid);
/*      */             }
/* 3847 */             if (greatGrandKid == null) {
/* 3848 */               reportSchemaError("src-redefine.5.b.b", null, grandKid);
/*      */             }
/*      */             else {
/* 3851 */               greatGrandKidName = DOMUtil.getLocalName(greatGrandKid);
/* 3852 */               if ((!greatGrandKidName.equals(SchemaSymbols.ELT_RESTRICTION)) && (!greatGrandKidName.equals(SchemaSymbols.ELT_EXTENSION)))
/*      */               {
/* 3854 */                 reportSchemaError("src-redefine.5.b.c", new Object[] { greatGrandKidName }, greatGrandKid);
/*      */               }
/*      */               else {
/* 3857 */                 Object[] attrs = this.fAttributeChecker.checkAttributes(greatGrandKid, false, currSchema);
/* 3858 */                 QName derivedBase = (QName)attrs[XSAttributeChecker.ATTIDX_BASE];
/* 3859 */                 if ((derivedBase == null) || (derivedBase.uri != currSchema.fTargetNamespace) || (!derivedBase.localpart.equals(oldName)))
/*      */                 {
/* 3862 */                   reportSchemaError("src-redefine.5.b.d", new Object[] { greatGrandKidName, (currSchema.fTargetNamespace == null ? "" : currSchema.fTargetNamespace) + "," + oldName }, greatGrandKid);
/*      */                 }
/* 3870 */                 else if ((derivedBase.prefix != null) && (derivedBase.prefix.length() > 0)) {
/* 3871 */                   greatGrandKid.setAttribute(SchemaSymbols.ATT_BASE, derivedBase.prefix + ":" + newName);
/*      */                 }
/*      */                 else {
/* 3874 */                   greatGrandKid.setAttribute(SchemaSymbols.ATT_BASE, newName);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/* 3884 */     else if (componentType.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
/* 3885 */       String processedBaseName = currSchema.fTargetNamespace + "," + oldName;
/*      */ 
/* 3887 */       int attGroupRefsCount = changeRedefineGroup(processedBaseName, componentType, newName, child, currSchema);
/* 3888 */       if (attGroupRefsCount > 1) {
/* 3889 */         reportSchemaError("src-redefine.7.1", new Object[] { new Integer(attGroupRefsCount) }, child);
/*      */       }
/* 3891 */       else if (attGroupRefsCount != 1)
/*      */       {
/* 3895 */         if (currSchema.fTargetNamespace == null)
/* 3896 */           this.fRedefinedRestrictedAttributeGroupRegistry.put(processedBaseName, "," + newName);
/*      */         else
/* 3898 */           this.fRedefinedRestrictedAttributeGroupRegistry.put(processedBaseName, currSchema.fTargetNamespace + "," + newName);
/*      */       }
/* 3900 */     } else if (componentType.equals(SchemaSymbols.ELT_GROUP)) {
/* 3901 */       String processedBaseName = currSchema.fTargetNamespace + "," + oldName;
/*      */ 
/* 3903 */       int groupRefsCount = changeRedefineGroup(processedBaseName, componentType, newName, child, currSchema);
/* 3904 */       if (groupRefsCount > 1) {
/* 3905 */         reportSchemaError("src-redefine.6.1.1", new Object[] { new Integer(groupRefsCount) }, child);
/*      */       }
/* 3907 */       else if (groupRefsCount != 1)
/*      */       {
/* 3911 */         if (currSchema.fTargetNamespace == null)
/* 3912 */           this.fRedefinedRestrictedGroupRegistry.put(processedBaseName, "," + newName);
/*      */         else
/* 3914 */           this.fRedefinedRestrictedGroupRegistry.put(processedBaseName, currSchema.fTargetNamespace + "," + newName);
/*      */       }
/*      */     }
/*      */     else {
/* 3918 */       reportSchemaError("Internal-Error", new Object[] { "could not handle this particular <redefine>; please submit your schemas and instance document in a bug report!" }, child);
/*      */     }
/*      */   }
/*      */ 
/*      */   private String findQName(String name, XSDocumentInfo schemaDoc)
/*      */   {
/* 3936 */     SchemaNamespaceSupport currNSMap = schemaDoc.fNamespaceSupport;
/* 3937 */     int colonPtr = name.indexOf(':');
/* 3938 */     String prefix = XMLSymbols.EMPTY_STRING;
/* 3939 */     if (colonPtr > 0)
/* 3940 */       prefix = name.substring(0, colonPtr);
/* 3941 */     String uri = currNSMap.getURI(this.fSymbolTable.addSymbol(prefix));
/* 3942 */     String localpart = colonPtr == 0 ? name : name.substring(colonPtr + 1);
/* 3943 */     if ((prefix == XMLSymbols.EMPTY_STRING) && (uri == null) && (schemaDoc.fIsChameleonSchema))
/* 3944 */       uri = schemaDoc.fTargetNamespace;
/* 3945 */     if (uri == null)
/* 3946 */       return "," + localpart;
/* 3947 */     return uri + "," + localpart;
/*      */   }
/*      */ 
/*      */   private int changeRedefineGroup(String originalQName, String elementSought, String newName, Element curr, XSDocumentInfo schemaDoc)
/*      */   {
/* 3959 */     int result = 0;
/* 3960 */     for (Element child = DOMUtil.getFirstChildElement(curr); 
/* 3961 */       child != null; child = DOMUtil.getNextSiblingElement(child)) {
/* 3962 */       String name = DOMUtil.getLocalName(child);
/* 3963 */       if (!name.equals(elementSought)) {
/* 3964 */         result += changeRedefineGroup(originalQName, elementSought, newName, child, schemaDoc);
/*      */       } else {
/* 3966 */         String ref = child.getAttribute(SchemaSymbols.ATT_REF);
/* 3967 */         if (ref.length() != 0) {
/* 3968 */           String processedRef = findQName(ref, schemaDoc);
/* 3969 */           if (originalQName.equals(processedRef)) {
/* 3970 */             String prefix = XMLSymbols.EMPTY_STRING;
/* 3971 */             int colonptr = ref.indexOf(":");
/* 3972 */             if (colonptr > 0) {
/* 3973 */               prefix = ref.substring(0, colonptr);
/* 3974 */               child.setAttribute(SchemaSymbols.ATT_REF, prefix + ":" + newName);
/*      */             }
/*      */             else {
/* 3977 */               child.setAttribute(SchemaSymbols.ATT_REF, newName);
/* 3978 */             }result++;
/* 3979 */             if (elementSought.equals(SchemaSymbols.ELT_GROUP)) {
/* 3980 */               String minOccurs = child.getAttribute(SchemaSymbols.ATT_MINOCCURS);
/* 3981 */               String maxOccurs = child.getAttribute(SchemaSymbols.ATT_MAXOCCURS);
/* 3982 */               if (((maxOccurs.length() != 0) && (!maxOccurs.equals("1"))) || ((minOccurs.length() != 0) && (!minOccurs.equals("1"))))
/*      */               {
/* 3984 */                 reportSchemaError("src-redefine.6.1.2", new Object[] { ref }, child);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 3991 */     return result;
/*      */   }
/*      */ 
/*      */   private XSDocumentInfo findXSDocumentForDecl(XSDocumentInfo currSchema, Element decl, XSDocumentInfo decl_Doc)
/*      */   {
/* 4008 */     Object temp = decl_Doc;
/* 4009 */     if (temp == null)
/*      */     {
/* 4011 */       return null;
/*      */     }
/* 4013 */     XSDocumentInfo declDocInfo = (XSDocumentInfo)temp;
/* 4014 */     return declDocInfo;
/*      */   }
/*      */ 
/*      */   private boolean nonAnnotationContent(Element elem)
/*      */   {
/* 4032 */     for (Element child = DOMUtil.getFirstChildElement(elem); child != null; child = DOMUtil.getNextSiblingElement(child)) {
/* 4033 */       if (!DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) return true;
/*      */     }
/* 4035 */     return false;
/*      */   }
/*      */ 
/*      */   private void setSchemasVisible(XSDocumentInfo startSchema) {
/* 4039 */     if (DOMUtil.isHidden(startSchema.fSchemaElement, this.fHiddenNodes))
/*      */     {
/* 4041 */       DOMUtil.setVisible(startSchema.fSchemaElement, this.fHiddenNodes);
/* 4042 */       Vector dependingSchemas = (Vector)this.fDependencyMap.get(startSchema);
/* 4043 */       for (int i = 0; i < dependingSchemas.size(); i++)
/* 4044 */         setSchemasVisible((XSDocumentInfo)dependingSchemas.elementAt(i));
/*      */     }
/*      */   }
/*      */ 
/*      */   public SimpleLocator element2Locator(Element e)
/*      */   {
/* 4058 */     if (!(e instanceof ElementImpl)) {
/* 4059 */       return null;
/*      */     }
/* 4061 */     SimpleLocator l = new SimpleLocator();
/* 4062 */     return element2Locator(e, l) ? l : null;
/*      */   }
/*      */ 
/*      */   public boolean element2Locator(Element e, SimpleLocator l)
/*      */   {
/* 4071 */     if (l == null)
/* 4072 */       return false;
/* 4073 */     if ((e instanceof ElementImpl)) {
/* 4074 */       ElementImpl ele = (ElementImpl)e;
/*      */ 
/* 4076 */       Document doc = ele.getOwnerDocument();
/* 4077 */       String sid = (String)this.fDoc2SystemId.get(DOMUtil.getRoot(doc));
/*      */ 
/* 4079 */       int line = ele.getLineNumber();
/* 4080 */       int column = ele.getColumnNumber();
/* 4081 */       l.setValues(sid, sid, line, column, ele.getCharacterOffset());
/* 4082 */       return true;
/*      */     }
/* 4084 */     return false;
/*      */   }
/*      */ 
/*      */   private Element getElementFromMap(Map<String, Element> registry, String declKey) {
/* 4088 */     if (registry == null) return null;
/* 4089 */     return (Element)registry.get(declKey);
/*      */   }
/*      */ 
/*      */   private XSDocumentInfo getDocInfoFromMap(Map<String, XSDocumentInfo> registry, String declKey) {
/* 4093 */     if (registry == null) return null;
/* 4094 */     return (XSDocumentInfo)registry.get(declKey);
/*      */   }
/*      */ 
/*      */   private Object getFromMap(Map registry, String key) {
/* 4098 */     if (registry == null) return null;
/* 4099 */     return registry.get(key);
/*      */   }
/*      */ 
/*      */   void reportSchemaFatalError(String key, Object[] args, Element ele) {
/* 4103 */     reportSchemaErr(key, args, ele, (short)2, null);
/*      */   }
/*      */ 
/*      */   void reportSchemaError(String key, Object[] args, Element ele) {
/* 4107 */     reportSchemaErr(key, args, ele, (short)1, null);
/*      */   }
/*      */ 
/*      */   void reportSchemaError(String key, Object[] args, Element ele, Exception exception) {
/* 4111 */     reportSchemaErr(key, args, ele, (short)1, exception);
/*      */   }
/*      */ 
/*      */   void reportSchemaWarning(String key, Object[] args, Element ele) {
/* 4115 */     reportSchemaErr(key, args, ele, (short)0, null);
/*      */   }
/*      */ 
/*      */   void reportSchemaWarning(String key, Object[] args, Element ele, Exception exception) {
/* 4119 */     reportSchemaErr(key, args, ele, (short)0, exception);
/*      */   }
/*      */ 
/*      */   void reportSchemaErr(String key, Object[] args, Element ele, short type, Exception exception) {
/* 4123 */     if (element2Locator(ele, this.xl)) {
/* 4124 */       this.fErrorReporter.reportError(this.xl, "http://www.w3.org/TR/xml-schema-1", key, args, type, exception);
/*      */     }
/*      */     else
/*      */     {
/* 4128 */       this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", key, args, type, exception);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setGenerateSyntheticAnnotations(boolean state)
/*      */   {
/* 4301 */     this.fSchemaParser.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", state);
/*      */   }
/*      */ 
/*      */   private static final class SAX2XNIUtil extends ErrorHandlerWrapper
/*      */   {
/*      */     public static XMLParseException createXMLParseException0(SAXParseException exception)
/*      */     {
/* 4290 */       return createXMLParseException(exception);
/*      */     }
/*      */     public static XNIException createXNIException0(SAXException exception) {
/* 4293 */       return createXNIException(exception);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class XSAnnotationGrammarPool
/*      */     implements XMLGrammarPool
/*      */   {
/*      */     private XSGrammarBucket fGrammarBucket;
/*      */     private Grammar[] fInitialGrammarSet;
/*      */ 
/*      */     public Grammar[] retrieveInitialGrammarSet(String grammarType)
/*      */     {
/* 4145 */       if (grammarType == "http://www.w3.org/2001/XMLSchema") {
/* 4146 */         if (this.fInitialGrammarSet == null) {
/* 4147 */           if (this.fGrammarBucket == null) {
/* 4148 */             this.fInitialGrammarSet = new Grammar[] { SchemaGrammar.Schema4Annotations.INSTANCE };
/*      */           }
/*      */           else {
/* 4151 */             SchemaGrammar[] schemaGrammars = this.fGrammarBucket.getGrammars();
/*      */ 
/* 4157 */             for (int i = 0; i < schemaGrammars.length; i++) {
/* 4158 */               if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(schemaGrammars[i].getTargetNamespace())) {
/* 4159 */                 this.fInitialGrammarSet = schemaGrammars;
/* 4160 */                 return this.fInitialGrammarSet;
/*      */               }
/*      */             }
/* 4163 */             Grammar[] grammars = new Grammar[schemaGrammars.length + 1];
/* 4164 */             System.arraycopy(schemaGrammars, 0, grammars, 0, schemaGrammars.length);
/* 4165 */             grammars[(grammars.length - 1)] = SchemaGrammar.Schema4Annotations.INSTANCE;
/* 4166 */             this.fInitialGrammarSet = grammars;
/*      */           }
/*      */         }
/* 4169 */         return this.fInitialGrammarSet;
/*      */       }
/* 4171 */       return new Grammar[0];
/*      */     }
/*      */ 
/*      */     public void cacheGrammars(String grammarType, Grammar[] grammars)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Grammar retrieveGrammar(XMLGrammarDescription desc) {
/* 4179 */       if (desc.getGrammarType() == "http://www.w3.org/2001/XMLSchema") {
/* 4180 */         String tns = ((XMLSchemaDescription)desc).getTargetNamespace();
/* 4181 */         if (this.fGrammarBucket != null) {
/* 4182 */           Grammar grammar = this.fGrammarBucket.getGrammar(tns);
/* 4183 */           if (grammar != null) {
/* 4184 */             return grammar;
/*      */           }
/*      */         }
/* 4187 */         if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(tns)) {
/* 4188 */           return SchemaGrammar.Schema4Annotations.INSTANCE;
/*      */         }
/*      */       }
/* 4191 */       return null;
/*      */     }
/*      */ 
/*      */     public void refreshGrammars(XSGrammarBucket gBucket) {
/* 4195 */       this.fGrammarBucket = gBucket;
/* 4196 */       this.fInitialGrammarSet = null;
/*      */     }
/*      */ 
/*      */     public void lockPool()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void unlockPool()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class XSDKey
/*      */   {
/*      */     String systemId;
/*      */     short referType;
/*      */     String referNS;
/*      */ 
/*      */     XSDKey(String systemId, short referType, String referNS)
/*      */     {
/* 4251 */       this.systemId = systemId;
/* 4252 */       this.referType = referType;
/* 4253 */       this.referNS = referNS;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 4259 */       return this.referNS == null ? 0 : this.referNS.hashCode();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object obj) {
/* 4263 */       if (!(obj instanceof XSDKey)) {
/* 4264 */         return false;
/*      */       }
/* 4266 */       XSDKey key = (XSDKey)obj;
/*      */ 
/* 4276 */       if (this.referNS != key.referNS) {
/* 4277 */         return false;
/*      */       }
/*      */ 
/* 4280 */       if ((this.systemId == null) || (!this.systemId.equals(key.systemId))) {
/* 4281 */         return false;
/*      */       }
/*      */ 
/* 4284 */       return true;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.traversers.XSDHandler
 * JD-Core Version:    0.6.2
 */