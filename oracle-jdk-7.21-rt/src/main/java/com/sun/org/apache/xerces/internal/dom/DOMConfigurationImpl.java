/*      */ package com.sun.org.apache.xerces.internal.dom;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.Constants;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
/*      */ import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.MessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
/*      */ import com.sun.org.apache.xerces.internal.util.PropertyState;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import org.w3c.dom.DOMConfiguration;
/*      */ import org.w3c.dom.DOMErrorHandler;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.DOMStringList;
/*      */ import org.w3c.dom.ls.LSResourceResolver;
/*      */ 
/*      */ public class DOMConfigurationImpl extends ParserConfigurationSettings
/*      */   implements XMLParserConfiguration, DOMConfiguration
/*      */ {
/*      */   protected static final String XERCES_VALIDATION = "http://xml.org/sax/features/validation";
/*      */   protected static final String XERCES_NAMESPACES = "http://xml.org/sax/features/namespaces";
/*      */   protected static final String SCHEMA = "http://apache.org/xml/features/validation/schema";
/*      */   protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
/*      */   protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
/*      */   protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
/*      */   protected static final String SEND_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
/*      */   protected static final String DTD_VALIDATOR_FACTORY_PROPERTY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
/*      */   protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
/*      */   protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
/*      */   protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*      */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
/*      */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*      */   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
/*      */   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*      */   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
/*      */   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
/*      */   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*      */   protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
/*      */   XMLDocumentHandler fDocumentHandler;
/*  167 */   protected short features = 0;
/*      */   protected static final short NAMESPACES = 1;
/*      */   protected static final short DTNORMALIZATION = 2;
/*      */   protected static final short ENTITIES = 4;
/*      */   protected static final short CDATA = 8;
/*      */   protected static final short SPLITCDATA = 16;
/*      */   protected static final short COMMENTS = 32;
/*      */   protected static final short VALIDATE = 64;
/*      */   protected static final short PSVI = 128;
/*      */   protected static final short WELLFORMED = 256;
/*      */   protected static final short NSDECL = 512;
/*      */   protected static final short INFOSET_TRUE_PARAMS = 801;
/*      */   protected static final short INFOSET_FALSE_PARAMS = 14;
/*      */   protected static final short INFOSET_MASK = 815;
/*      */   protected SymbolTable fSymbolTable;
/*      */   protected ArrayList fComponents;
/*      */   protected ValidationManager fValidationManager;
/*      */   protected Locale fLocale;
/*      */   protected XMLErrorReporter fErrorReporter;
/*  200 */   protected final DOMErrorHandlerWrapper fErrorHandlerWrapper = new DOMErrorHandlerWrapper();
/*      */   private DOMStringList fRecognizedParameters;
/*      */ 
/*      */   protected DOMConfigurationImpl()
/*      */   {
/*  214 */     this(null, null);
/*      */   }
/*      */ 
/*      */   protected DOMConfigurationImpl(SymbolTable symbolTable)
/*      */   {
/*  223 */     this(symbolTable, null);
/*      */   }
/*      */ 
/*      */   protected DOMConfigurationImpl(SymbolTable symbolTable, XMLComponentManager parentSettings)
/*      */   {
/*  235 */     super(parentSettings);
/*      */ 
/*  239 */     this.fFeatures = new HashMap();
/*  240 */     this.fProperties = new HashMap();
/*      */ 
/*  243 */     String[] recognizedFeatures = { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates" };
/*      */ 
/*  254 */     addRecognizedFeatures(recognizedFeatures);
/*      */ 
/*  257 */     setFeature("http://xml.org/sax/features/validation", false);
/*  258 */     setFeature("http://apache.org/xml/features/validation/schema", false);
/*  259 */     setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
/*  260 */     setFeature("http://apache.org/xml/features/validation/dynamic", false);
/*  261 */     setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
/*  262 */     setFeature("http://xml.org/sax/features/namespaces", true);
/*  263 */     setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
/*  264 */     setFeature("http://apache.org/xml/features/namespace-growth", false);
/*      */ 
/*  267 */     String[] recognizedProperties = { "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation/schema/dv-factory" };
/*      */ 
/*  281 */     addRecognizedProperties(recognizedProperties);
/*      */ 
/*  284 */     this.features = ((short)(this.features | 0x1));
/*  285 */     this.features = ((short)(this.features | 0x4));
/*  286 */     this.features = ((short)(this.features | 0x20));
/*  287 */     this.features = ((short)(this.features | 0x8));
/*  288 */     this.features = ((short)(this.features | 0x10));
/*  289 */     this.features = ((short)(this.features | 0x100));
/*  290 */     this.features = ((short)(this.features | 0x200));
/*      */ 
/*  292 */     if (symbolTable == null) {
/*  293 */       symbolTable = new SymbolTable();
/*      */     }
/*  295 */     this.fSymbolTable = symbolTable;
/*      */ 
/*  297 */     this.fComponents = new ArrayList();
/*      */ 
/*  299 */     setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
/*  300 */     this.fErrorReporter = new XMLErrorReporter();
/*  301 */     setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/*  302 */     addComponent(this.fErrorReporter);
/*      */ 
/*  304 */     setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", DTDDVFactory.getInstance());
/*      */ 
/*  306 */     XMLEntityManager manager = new XMLEntityManager();
/*  307 */     setProperty("http://apache.org/xml/properties/internal/entity-manager", manager);
/*  308 */     addComponent(manager);
/*      */ 
/*  310 */     this.fValidationManager = createValidationManager();
/*  311 */     setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
/*      */ 
/*  315 */     if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
/*  316 */       XMLMessageFormatter xmft = new XMLMessageFormatter();
/*  317 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
/*  318 */       this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
/*      */     }
/*      */ 
/*  324 */     if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
/*  325 */       MessageFormatter xmft = null;
/*      */       try {
/*  327 */         xmft = (MessageFormatter)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter", true);
/*      */       }
/*      */       catch (Exception exception)
/*      */       {
/*      */       }
/*  332 */       if (xmft != null) {
/*  333 */         this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  340 */       setLocale(Locale.getDefault());
/*      */     }
/*      */     catch (XNIException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void parse(XMLInputSource inputSource)
/*      */     throws XNIException, IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setDocumentHandler(XMLDocumentHandler documentHandler)
/*      */   {
/*  391 */     this.fDocumentHandler = documentHandler;
/*      */   }
/*      */ 
/*      */   public XMLDocumentHandler getDocumentHandler()
/*      */   {
/*  396 */     return this.fDocumentHandler;
/*      */   }
/*      */ 
/*      */   public void setDTDHandler(XMLDTDHandler dtdHandler)
/*      */   {
/*      */   }
/*      */ 
/*      */   public XMLDTDHandler getDTDHandler()
/*      */   {
/*  410 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDTDContentModelHandler(XMLDTDContentModelHandler handler)
/*      */   {
/*      */   }
/*      */ 
/*      */   public XMLDTDContentModelHandler getDTDContentModelHandler()
/*      */   {
/*  425 */     return null;
/*      */   }
/*      */ 
/*      */   public void setEntityResolver(XMLEntityResolver resolver)
/*      */   {
/*  436 */     if (resolver != null)
/*  437 */       this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
/*      */   }
/*      */ 
/*      */   public XMLEntityResolver getEntityResolver()
/*      */   {
/*  449 */     return (XMLEntityResolver)this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
/*      */   }
/*      */ 
/*      */   public void setErrorHandler(XMLErrorHandler errorHandler)
/*      */   {
/*  471 */     if (errorHandler != null)
/*  472 */       this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
/*      */   }
/*      */ 
/*      */   public XMLErrorHandler getErrorHandler()
/*      */   {
/*  484 */     return (XMLErrorHandler)this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/*  504 */     super.setFeature(featureId, state);
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */     throws XMLConfigurationException
/*      */   {
/*  518 */     super.setProperty(propertyId, value);
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale)
/*      */     throws XNIException
/*      */   {
/*  531 */     this.fLocale = locale;
/*  532 */     this.fErrorReporter.setLocale(locale);
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  538 */     return this.fLocale;
/*      */   }
/*      */ 
/*      */   public void setParameter(String name, Object value)
/*      */     throws DOMException
/*      */   {
/*  546 */     boolean found = true;
/*      */ 
/*  550 */     if ((value instanceof Boolean)) {
/*  551 */       boolean state = ((Boolean)value).booleanValue();
/*      */ 
/*  553 */       if (name.equalsIgnoreCase("comments")) {
/*  554 */         this.features = ((short)(state ? this.features | 0x20 : this.features & 0xFFFFFFDF));
/*      */       }
/*  556 */       else if (name.equalsIgnoreCase("datatype-normalization")) {
/*  557 */         setFeature("http://apache.org/xml/features/validation/schema/normalized-value", state);
/*  558 */         this.features = ((short)(state ? this.features | 0x2 : this.features & 0xFFFFFFFD));
/*      */ 
/*  560 */         if (state) {
/*  561 */           this.features = ((short)(this.features | 0x40));
/*      */         }
/*      */       }
/*  564 */       else if (name.equalsIgnoreCase("namespaces")) {
/*  565 */         this.features = ((short)(state ? this.features | 0x1 : this.features & 0xFFFFFFFE));
/*      */       }
/*  567 */       else if (name.equalsIgnoreCase("cdata-sections")) {
/*  568 */         this.features = ((short)(state ? this.features | 0x8 : this.features & 0xFFFFFFF7));
/*      */       }
/*  570 */       else if (name.equalsIgnoreCase("entities")) {
/*  571 */         this.features = ((short)(state ? this.features | 0x4 : this.features & 0xFFFFFFFB));
/*      */       }
/*  573 */       else if (name.equalsIgnoreCase("split-cdata-sections")) {
/*  574 */         this.features = ((short)(state ? this.features | 0x10 : this.features & 0xFFFFFFEF));
/*      */       }
/*  576 */       else if (name.equalsIgnoreCase("validate")) {
/*  577 */         this.features = ((short)(state ? this.features | 0x40 : this.features & 0xFFFFFFBF));
/*      */       }
/*  579 */       else if (name.equalsIgnoreCase("well-formed")) {
/*  580 */         this.features = ((short)(state ? this.features | 0x100 : this.features & 0xFFFFFEFF));
/*      */       }
/*  582 */       else if (name.equalsIgnoreCase("namespace-declarations")) {
/*  583 */         this.features = ((short)(state ? this.features | 0x200 : this.features & 0xFFFFFDFF));
/*      */       }
/*  585 */       else if (name.equalsIgnoreCase("infoset"))
/*      */       {
/*  587 */         if (state) {
/*  588 */           this.features = ((short)(this.features | 0x321));
/*  589 */           this.features = ((short)(this.features & 0xFFFFFFF1));
/*  590 */           setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
/*      */         }
/*      */       }
/*  593 */       else if ((name.equalsIgnoreCase("normalize-characters")) || (name.equalsIgnoreCase("canonical-form")) || (name.equalsIgnoreCase("validate-if-schema")) || (name.equalsIgnoreCase("check-character-normalization")))
/*      */       {
/*  598 */         if (state) {
/*  599 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/*  604 */           throw new DOMException((short)9, msg);
/*      */         }
/*      */       }
/*  607 */       else if (name.equalsIgnoreCase("element-content-whitespace")) {
/*  608 */         if (!state) {
/*  609 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/*  614 */           throw new DOMException((short)9, msg);
/*      */         }
/*      */       }
/*  617 */       else if (name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi"))
/*      */       {
/*  621 */         if (!state) {
/*  622 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/*  627 */           throw new DOMException((short)9, msg);
/*      */         }
/*      */       }
/*  630 */       else if (name.equalsIgnoreCase("psvi")) {
/*  631 */         this.features = ((short)(state ? this.features | 0x80 : this.features & 0xFFFFFF7F));
/*      */       }
/*      */       else {
/*  634 */         found = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  647 */     if ((!found) || (!(value instanceof Boolean))) {
/*  648 */       found = true;
/*      */ 
/*  650 */       if (name.equalsIgnoreCase("error-handler")) {
/*  651 */         if (((value instanceof DOMErrorHandler)) || (value == null)) {
/*  652 */           this.fErrorHandlerWrapper.setErrorHandler((DOMErrorHandler)value);
/*  653 */           setErrorHandler(this.fErrorHandlerWrapper);
/*      */         }
/*      */         else
/*      */         {
/*  658 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
/*      */ 
/*  663 */           throw new DOMException((short)17, msg);
/*      */         }
/*      */       }
/*  666 */       else if (name.equalsIgnoreCase("resource-resolver")) {
/*  667 */         if (((value instanceof LSResourceResolver)) || (value == null)) {
/*      */           try {
/*  669 */             setEntityResolver(new DOMEntityResolverWrapper((LSResourceResolver)value));
/*      */           }
/*      */           catch (XMLConfigurationException e) {
/*      */           }
/*      */         }
/*      */         else {
/*  675 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
/*      */ 
/*  680 */           throw new DOMException((short)17, msg);
/*      */         }
/*      */ 
/*      */       }
/*  684 */       else if (name.equalsIgnoreCase("schema-location")) {
/*  685 */         if (((value instanceof String)) || (value == null))
/*      */         {
/*      */           try {
/*  688 */             setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", value);
/*      */           }
/*      */           catch (XMLConfigurationException e)
/*      */           {
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  696 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
/*      */ 
/*  701 */           throw new DOMException((short)17, msg);
/*      */         }
/*      */ 
/*      */       }
/*  705 */       else if (name.equalsIgnoreCase("schema-type")) {
/*  706 */         if (((value instanceof String)) || (value == null)) {
/*      */           try {
/*  708 */             if (value == null) {
/*  709 */               setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
/*      */             }
/*  713 */             else if (value.equals(Constants.NS_XMLSCHEMA))
/*      */             {
/*  715 */               setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
/*      */             }
/*  719 */             else if (value.equals(Constants.NS_DTD))
/*      */             {
/*  721 */               setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
/*      */             }
/*      */           }
/*      */           catch (XMLConfigurationException e) {
/*      */           }
/*      */         }
/*      */         else {
/*  728 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
/*      */ 
/*  733 */           throw new DOMException((short)17, msg);
/*      */         }
/*      */ 
/*      */       }
/*  737 */       else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table"))
/*      */       {
/*  739 */         if ((value instanceof SymbolTable)) {
/*  740 */           setProperty("http://apache.org/xml/properties/internal/symbol-table", value);
/*      */         }
/*      */         else
/*      */         {
/*  744 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
/*      */ 
/*  749 */           throw new DOMException((short)17, msg);
/*      */         }
/*      */       }
/*  752 */       else if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
/*  753 */         if ((value instanceof XMLGrammarPool)) {
/*  754 */           setProperty("http://apache.org/xml/properties/internal/grammar-pool", value);
/*      */         }
/*      */         else
/*      */         {
/*  758 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
/*      */ 
/*  763 */           throw new DOMException((short)17, msg);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  770 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
/*      */ 
/*  775 */         throw new DOMException((short)8, msg);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getParameter(String name)
/*      */     throws DOMException
/*      */   {
/*  791 */     if (name.equalsIgnoreCase("comments")) {
/*  792 */       return (this.features & 0x20) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  794 */     if (name.equalsIgnoreCase("namespaces")) {
/*  795 */       return (this.features & 0x1) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  797 */     if (name.equalsIgnoreCase("datatype-normalization"))
/*      */     {
/*  799 */       return (this.features & 0x2) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  801 */     if (name.equalsIgnoreCase("cdata-sections")) {
/*  802 */       return (this.features & 0x8) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  804 */     if (name.equalsIgnoreCase("entities")) {
/*  805 */       return (this.features & 0x4) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  807 */     if (name.equalsIgnoreCase("split-cdata-sections")) {
/*  808 */       return (this.features & 0x10) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  810 */     if (name.equalsIgnoreCase("validate")) {
/*  811 */       return (this.features & 0x40) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  813 */     if (name.equalsIgnoreCase("well-formed")) {
/*  814 */       return (this.features & 0x100) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  816 */     if (name.equalsIgnoreCase("namespace-declarations")) {
/*  817 */       return (this.features & 0x200) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  819 */     if (name.equalsIgnoreCase("infoset")) {
/*  820 */       return (this.features & 0x32F) == 801 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  822 */     if ((name.equalsIgnoreCase("normalize-characters")) || (name.equalsIgnoreCase("canonical-form")) || (name.equalsIgnoreCase("validate-if-schema")) || (name.equalsIgnoreCase("check-character-normalization")))
/*      */     {
/*  827 */       return Boolean.FALSE;
/*      */     }
/*  829 */     if (name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) {
/*  830 */       return Boolean.TRUE;
/*      */     }
/*  832 */     if (name.equalsIgnoreCase("psvi")) {
/*  833 */       return (this.features & 0x80) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*      */     }
/*  835 */     if (name.equalsIgnoreCase("element-content-whitespace")) {
/*  836 */       return Boolean.TRUE;
/*      */     }
/*  838 */     if (name.equalsIgnoreCase("error-handler")) {
/*  839 */       return this.fErrorHandlerWrapper.getErrorHandler();
/*      */     }
/*  841 */     if (name.equalsIgnoreCase("resource-resolver")) {
/*  842 */       XMLEntityResolver entityResolver = getEntityResolver();
/*  843 */       if ((entityResolver != null) && ((entityResolver instanceof DOMEntityResolverWrapper))) {
/*  844 */         return ((DOMEntityResolverWrapper)entityResolver).getEntityResolver();
/*      */       }
/*  846 */       return null;
/*      */     }
/*  848 */     if (name.equalsIgnoreCase("schema-type")) {
/*  849 */       return getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
/*      */     }
/*  851 */     if (name.equalsIgnoreCase("schema-location")) {
/*  852 */       return getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
/*      */     }
/*  854 */     if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
/*  855 */       return getProperty("http://apache.org/xml/properties/internal/symbol-table");
/*      */     }
/*  857 */     if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
/*  858 */       return getProperty("http://apache.org/xml/properties/internal/grammar-pool");
/*      */     }
/*      */ 
/*  861 */     String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
/*      */ 
/*  866 */     throw new DOMException((short)8, msg);
/*      */   }
/*      */ 
/*      */   public boolean canSetParameter(String name, Object value)
/*      */   {
/*  886 */     if (value == null)
/*      */     {
/*  893 */       return true;
/*      */     }
/*  895 */     if ((value instanceof Boolean))
/*      */     {
/*  899 */       if ((name.equalsIgnoreCase("comments")) || (name.equalsIgnoreCase("datatype-normalization")) || (name.equalsIgnoreCase("cdata-sections")) || (name.equalsIgnoreCase("entities")) || (name.equalsIgnoreCase("split-cdata-sections")) || (name.equalsIgnoreCase("namespaces")) || (name.equalsIgnoreCase("validate")) || (name.equalsIgnoreCase("well-formed")) || (name.equalsIgnoreCase("infoset")) || (name.equalsIgnoreCase("namespace-declarations")))
/*      */       {
/*  910 */         return true;
/*      */       }
/*  912 */       if ((name.equalsIgnoreCase("normalize-characters")) || (name.equalsIgnoreCase("canonical-form")) || (name.equalsIgnoreCase("validate-if-schema")) || (name.equalsIgnoreCase("check-character-normalization")))
/*      */       {
/*  918 */         return !value.equals(Boolean.TRUE);
/*      */       }
/*  920 */       if ((name.equalsIgnoreCase("element-content-whitespace")) || (name.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")))
/*      */       {
/*  923 */         return value.equals(Boolean.TRUE);
/*      */       }
/*      */ 
/*  926 */       return false;
/*      */     }
/*      */ 
/*  929 */     if (name.equalsIgnoreCase("error-handler")) {
/*  930 */       return (value instanceof DOMErrorHandler);
/*      */     }
/*  932 */     if (name.equalsIgnoreCase("resource-resolver")) {
/*  933 */       return (value instanceof LSResourceResolver);
/*      */     }
/*  935 */     if (name.equalsIgnoreCase("schema-location")) {
/*  936 */       return (value instanceof String);
/*      */     }
/*  938 */     if (name.equalsIgnoreCase("schema-type"))
/*      */     {
/*  941 */       return ((value instanceof String)) && (value.equals(Constants.NS_XMLSCHEMA));
/*      */     }
/*  943 */     if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table"))
/*      */     {
/*  945 */       return (value instanceof SymbolTable);
/*      */     }
/*  947 */     if (name.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
/*  948 */       return (value instanceof XMLGrammarPool);
/*      */     }
/*      */ 
/*  952 */     return false;
/*      */   }
/*      */ 
/*      */   public DOMStringList getParameterNames()
/*      */   {
/*  966 */     if (this.fRecognizedParameters == null) {
/*  967 */       Vector parameters = new Vector();
/*      */ 
/*  972 */       parameters.add("comments");
/*  973 */       parameters.add("datatype-normalization");
/*  974 */       parameters.add("cdata-sections");
/*  975 */       parameters.add("entities");
/*  976 */       parameters.add("split-cdata-sections");
/*  977 */       parameters.add("namespaces");
/*  978 */       parameters.add("validate");
/*      */ 
/*  980 */       parameters.add("infoset");
/*  981 */       parameters.add("normalize-characters");
/*  982 */       parameters.add("canonical-form");
/*  983 */       parameters.add("validate-if-schema");
/*  984 */       parameters.add("check-character-normalization");
/*  985 */       parameters.add("well-formed");
/*      */ 
/*  987 */       parameters.add("namespace-declarations");
/*  988 */       parameters.add("element-content-whitespace");
/*      */ 
/*  990 */       parameters.add("error-handler");
/*  991 */       parameters.add("schema-type");
/*  992 */       parameters.add("schema-location");
/*  993 */       parameters.add("resource-resolver");
/*      */ 
/*  996 */       parameters.add("http://apache.org/xml/properties/internal/grammar-pool");
/*  997 */       parameters.add("http://apache.org/xml/properties/internal/symbol-table");
/*  998 */       parameters.add("http://apache.org/xml/features/validation/schema/augment-psvi");
/*      */ 
/* 1000 */       this.fRecognizedParameters = new DOMStringListImpl(parameters);
/*      */     }
/*      */ 
/* 1004 */     return this.fRecognizedParameters;
/*      */   }
/*      */ 
/*      */   protected void reset()
/*      */     throws XNIException
/*      */   {
/* 1016 */     if (this.fValidationManager != null) {
/* 1017 */       this.fValidationManager.reset();
/*      */     }
/* 1019 */     int count = this.fComponents.size();
/* 1020 */     for (int i = 0; i < count; i++) {
/* 1021 */       XMLComponent c = (XMLComponent)this.fComponents.get(i);
/* 1022 */       c.reset(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected PropertyState checkProperty(String propertyId)
/*      */     throws XMLConfigurationException
/*      */   {
/* 1040 */     if (propertyId.startsWith("http://xml.org/sax/properties/")) {
/* 1041 */       int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
/*      */ 
/* 1053 */       if ((suffixLength == "xml-string".length()) && (propertyId.endsWith("xml-string")))
/*      */       {
/* 1058 */         return PropertyState.NOT_SUPPORTED;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1063 */     return super.checkProperty(propertyId);
/*      */   }
/*      */ 
/*      */   protected void addComponent(XMLComponent component)
/*      */   {
/* 1071 */     if (this.fComponents.contains(component)) {
/* 1072 */       return;
/*      */     }
/* 1074 */     this.fComponents.add(component);
/*      */ 
/* 1077 */     String[] recognizedFeatures = component.getRecognizedFeatures();
/* 1078 */     addRecognizedFeatures(recognizedFeatures);
/*      */ 
/* 1081 */     String[] recognizedProperties = component.getRecognizedProperties();
/* 1082 */     addRecognizedProperties(recognizedProperties);
/*      */   }
/*      */ 
/*      */   protected ValidationManager createValidationManager()
/*      */   {
/* 1087 */     return new ValidationManager();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.dom.DOMConfigurationImpl
 * JD-Core Version:    0.6.2
 */