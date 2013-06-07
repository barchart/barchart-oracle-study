/*     */ package com.sun.org.apache.xerces.internal.jaxp;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl;
/*     */ import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
/*     */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*     */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
/*     */ import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
/*     */ import com.sun.org.apache.xerces.internal.parsers.DOMParser;
/*     */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*     */ import java.io.IOException;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.validation.Schema;
/*     */ import org.w3c.dom.DOMImplementation;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ 
/*     */ public class DocumentBuilderImpl extends DocumentBuilder
/*     */   implements JAXPConstants
/*     */ {
/*     */   private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
/*     */   private static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
/*     */   private static final String CREATE_ENTITY_REF_NODES_FEATURE = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
/*     */   private static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
/*     */   private static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
/*     */   private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
/*     */   private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
/*     */   private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
/*     */   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*     */   private final DOMParser domParser;
/*     */   private final Schema grammar;
/*     */   private final XMLComponent fSchemaValidator;
/*     */   private final XMLComponentManager fSchemaValidatorComponentManager;
/*     */   private final ValidationManager fSchemaValidationManager;
/*     */   private final UnparsedEntityHandler fUnparsedEntityHandler;
/*     */   private final ErrorHandler fInitErrorHandler;
/*     */   private final EntityResolver fInitEntityResolver;
/*     */ 
/*     */   DocumentBuilderImpl(DocumentBuilderFactoryImpl dbf, Hashtable dbfAttrs, Hashtable features)
/*     */     throws SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/* 114 */     this(dbf, dbfAttrs, features, false);
/*     */   }
/*     */ 
/*     */   DocumentBuilderImpl(DocumentBuilderFactoryImpl dbf, Hashtable dbfAttrs, Hashtable features, boolean secureProcessing)
/*     */     throws SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/* 120 */     this.domParser = new DOMParser();
/*     */ 
/* 125 */     if (dbf.isValidating()) {
/* 126 */       this.fInitErrorHandler = new DefaultValidationErrorHandler();
/* 127 */       setErrorHandler(this.fInitErrorHandler);
/*     */     }
/*     */     else {
/* 130 */       this.fInitErrorHandler = this.domParser.getErrorHandler();
/*     */     }
/*     */ 
/* 133 */     this.domParser.setFeature("http://xml.org/sax/features/validation", dbf.isValidating());
/*     */ 
/* 136 */     this.domParser.setFeature("http://xml.org/sax/features/namespaces", dbf.isNamespaceAware());
/*     */ 
/* 139 */     this.domParser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", !dbf.isIgnoringElementContentWhitespace());
/*     */ 
/* 141 */     this.domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", !dbf.isExpandEntityReferences());
/*     */ 
/* 143 */     this.domParser.setFeature("http://apache.org/xml/features/include-comments", !dbf.isIgnoringComments());
/*     */ 
/* 145 */     this.domParser.setFeature("http://apache.org/xml/features/create-cdata-nodes", !dbf.isCoalescing());
/*     */ 
/* 151 */     if (dbf.isXIncludeAware()) {
/* 152 */       this.domParser.setFeature("http://apache.org/xml/features/xinclude", true);
/*     */     }
/*     */ 
/* 156 */     if (secureProcessing) {
/* 157 */       this.domParser.setProperty("http://apache.org/xml/properties/security-manager", new SecurityManager());
/*     */     }
/*     */ 
/* 160 */     this.grammar = dbf.getSchema();
/* 161 */     if (this.grammar != null) {
/* 162 */       XMLParserConfiguration config = this.domParser.getXMLParserConfiguration();
/* 163 */       XMLComponent validatorComponent = null;
/*     */ 
/* 165 */       if ((this.grammar instanceof XSGrammarPoolContainer)) {
/* 166 */         validatorComponent = new XMLSchemaValidator();
/* 167 */         this.fSchemaValidationManager = new ValidationManager();
/* 168 */         this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager);
/* 169 */         config.setDTDHandler(this.fUnparsedEntityHandler);
/* 170 */         this.fUnparsedEntityHandler.setDTDHandler(this.domParser);
/* 171 */         this.domParser.setDTDSource(this.fUnparsedEntityHandler);
/* 172 */         this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(config, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
/*     */       }
/*     */       else
/*     */       {
/* 177 */         validatorComponent = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
/* 178 */         this.fSchemaValidationManager = null;
/* 179 */         this.fUnparsedEntityHandler = null;
/* 180 */         this.fSchemaValidatorComponentManager = config;
/*     */       }
/* 182 */       config.addRecognizedFeatures(validatorComponent.getRecognizedFeatures());
/* 183 */       config.addRecognizedProperties(validatorComponent.getRecognizedProperties());
/* 184 */       setFeatures(features);
/* 185 */       config.setDocumentHandler((XMLDocumentHandler)validatorComponent);
/* 186 */       ((XMLDocumentSource)validatorComponent).setDocumentHandler(this.domParser);
/* 187 */       this.domParser.setDocumentSource((XMLDocumentSource)validatorComponent);
/* 188 */       this.fSchemaValidator = validatorComponent;
/*     */     }
/*     */     else {
/* 191 */       this.fSchemaValidationManager = null;
/* 192 */       this.fUnparsedEntityHandler = null;
/* 193 */       this.fSchemaValidatorComponentManager = null;
/* 194 */       this.fSchemaValidator = null;
/* 195 */       setFeatures(features);
/*     */     }
/*     */ 
/* 199 */     setDocumentBuilderFactoryAttributes(dbfAttrs);
/*     */ 
/* 202 */     this.fInitEntityResolver = this.domParser.getEntityResolver();
/*     */   }
/*     */ 
/*     */   private void setFeatures(Hashtable features) throws SAXNotSupportedException, SAXNotRecognizedException
/*     */   {
/* 207 */     if (features != null) {
/* 208 */       Iterator entries = features.entrySet().iterator();
/* 209 */       while (entries.hasNext()) {
/* 210 */         Map.Entry entry = (Map.Entry)entries.next();
/* 211 */         String feature = (String)entry.getKey();
/* 212 */         boolean value = ((Boolean)entry.getValue()).booleanValue();
/* 213 */         this.domParser.setFeature(feature, value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setDocumentBuilderFactoryAttributes(Hashtable dbfAttrs)
/*     */     throws SAXNotSupportedException, SAXNotRecognizedException
/*     */   {
/* 228 */     if (dbfAttrs == null)
/*     */     {
/* 230 */       return;
/*     */     }
/*     */ 
/* 233 */     Iterator entries = dbfAttrs.entrySet().iterator();
/* 234 */     while (entries.hasNext()) {
/* 235 */       Map.Entry entry = (Map.Entry)entries.next();
/* 236 */       String name = (String)entry.getKey();
/* 237 */       Object val = entry.getValue();
/* 238 */       if ((val instanceof Boolean))
/*     */       {
/* 240 */         this.domParser.setFeature(name, ((Boolean)val).booleanValue());
/*     */       }
/* 243 */       else if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name))
/*     */       {
/* 246 */         if (("http://www.w3.org/2001/XMLSchema".equals(val)) && 
/* 247 */           (isValidating())) {
/* 248 */           this.domParser.setFeature("http://apache.org/xml/features/validation/schema", true);
/*     */ 
/* 251 */           this.domParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
/*     */         }
/*     */       }
/* 254 */       else if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(name)) {
/* 255 */         if (isValidating()) {
/* 256 */           String value = (String)dbfAttrs.get("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
/* 257 */           if ((value != null) && ("http://www.w3.org/2001/XMLSchema".equals(value)))
/* 258 */             this.domParser.setProperty(name, val);
/*     */           else {
/* 260 */             throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 268 */         this.domParser.setProperty(name, val);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Document newDocument()
/*     */   {
/* 280 */     return new DocumentImpl();
/*     */   }
/*     */ 
/*     */   public DOMImplementation getDOMImplementation() {
/* 284 */     return DOMImplementationImpl.getDOMImplementation();
/*     */   }
/*     */ 
/*     */   public Document parse(InputSource is) throws SAXException, IOException {
/* 288 */     if (is == null) {
/* 289 */       throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-null-input-source", null));
/*     */     }
/*     */ 
/* 293 */     if (this.fSchemaValidator != null) {
/* 294 */       if (this.fSchemaValidationManager != null) {
/* 295 */         this.fSchemaValidationManager.reset();
/* 296 */         this.fUnparsedEntityHandler.reset();
/*     */       }
/* 298 */       resetSchemaValidator();
/*     */     }
/* 300 */     this.domParser.parse(is);
/* 301 */     Document doc = this.domParser.getDocument();
/* 302 */     this.domParser.dropDocumentReferences();
/* 303 */     return doc;
/*     */   }
/*     */ 
/*     */   public boolean isNamespaceAware() {
/*     */     try {
/* 308 */       return this.domParser.getFeature("http://xml.org/sax/features/namespaces");
/*     */     }
/*     */     catch (SAXException x) {
/* 311 */       throw new IllegalStateException(x.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isValidating() {
/*     */     try {
/* 317 */       return this.domParser.getFeature("http://xml.org/sax/features/validation");
/*     */     }
/*     */     catch (SAXException x) {
/* 320 */       throw new IllegalStateException(x.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isXIncludeAware()
/*     */   {
/*     */     try
/*     */     {
/* 330 */       return this.domParser.getFeature("http://apache.org/xml/features/xinclude");
/*     */     } catch (SAXException exc) {
/*     */     }
/* 333 */     return false;
/*     */   }
/*     */ 
/*     */   public void setEntityResolver(EntityResolver er)
/*     */   {
/* 338 */     this.domParser.setEntityResolver(er);
/*     */   }
/*     */ 
/*     */   public void setErrorHandler(ErrorHandler eh) {
/* 342 */     this.domParser.setErrorHandler(eh);
/*     */   }
/*     */ 
/*     */   public Schema getSchema() {
/* 346 */     return this.grammar;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 351 */     if (this.domParser.getErrorHandler() != this.fInitErrorHandler) {
/* 352 */       this.domParser.setErrorHandler(this.fInitErrorHandler);
/*     */     }
/*     */ 
/* 355 */     if (this.domParser.getEntityResolver() != this.fInitEntityResolver)
/* 356 */       this.domParser.setEntityResolver(this.fInitEntityResolver);
/*     */   }
/*     */ 
/*     */   DOMParser getDOMParser()
/*     */   {
/* 362 */     return this.domParser;
/*     */   }
/*     */ 
/*     */   private void resetSchemaValidator() throws SAXException {
/*     */     try {
/* 367 */       this.fSchemaValidator.reset(this.fSchemaValidatorComponentManager);
/*     */     }
/*     */     catch (XMLConfigurationException e)
/*     */     {
/* 371 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderImpl
 * JD-Core Version:    0.6.2
 */