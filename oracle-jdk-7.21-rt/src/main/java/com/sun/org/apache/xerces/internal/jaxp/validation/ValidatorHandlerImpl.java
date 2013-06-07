/*      */ package com.sun.org.apache.xerces.internal.jaxp.validation;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
/*      */ import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.AttributesProxy;
/*      */ import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*      */ import com.sun.org.apache.xerces.internal.util.Status;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
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
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
/*      */ import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
/*      */ import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
/*      */ import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
/*      */ import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
/*      */ import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.util.HashMap;
/*      */ import javax.xml.parsers.FactoryConfigurationError;
/*      */ import javax.xml.parsers.SAXParserFactory;
/*      */ import javax.xml.transform.Result;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.sax.SAXResult;
/*      */ import javax.xml.transform.sax.SAXSource;
/*      */ import javax.xml.validation.TypeInfoProvider;
/*      */ import javax.xml.validation.ValidatorHandler;
/*      */ import org.w3c.dom.TypeInfo;
/*      */ import org.w3c.dom.ls.LSInput;
/*      */ import org.w3c.dom.ls.LSResourceResolver;
/*      */ import org.xml.sax.Attributes;
/*      */ import org.xml.sax.ContentHandler;
/*      */ import org.xml.sax.DTDHandler;
/*      */ import org.xml.sax.ErrorHandler;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.Locator;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.SAXNotRecognizedException;
/*      */ import org.xml.sax.SAXNotSupportedException;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.ext.Attributes2;
/*      */ import org.xml.sax.ext.EntityResolver2;
/*      */ 
/*      */ final class ValidatorHandlerImpl extends ValidatorHandler
/*      */   implements DTDHandler, EntityState, PSVIProvider, ValidatorHelper, XMLDocumentHandler
/*      */ {
/*      */   private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
/*      */   protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
/*      */   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
/*      */   private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
/*      */   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*      */   private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*      */   private XMLErrorReporter fErrorReporter;
/*      */   private NamespaceContext fNamespaceContext;
/*      */   private XMLSchemaValidator fSchemaValidator;
/*      */   private SymbolTable fSymbolTable;
/*      */   private ValidationManager fValidationManager;
/*      */   private XMLSchemaValidatorComponentManager fComponentManager;
/*  160 */   private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();
/*      */ 
/*  163 */   private boolean fNeedPushNSContext = true;
/*      */ 
/*  166 */   private HashMap fUnparsedEntities = null;
/*      */ 
/*  169 */   private boolean fStringsInternalized = false;
/*      */ 
/*  172 */   private final QName fElementQName = new QName();
/*  173 */   private final QName fAttributeQName = new QName();
/*  174 */   private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
/*  175 */   private final AttributesProxy fAttrAdapter = new AttributesProxy(this.fAttributes);
/*  176 */   private final XMLString fTempString = new XMLString();
/*      */ 
/*  182 */   private ContentHandler fContentHandler = null;
/*      */ 
/*  810 */   private final XMLSchemaTypeInfoProvider fTypeInfoProvider = new XMLSchemaTypeInfoProvider(null);
/*      */ 
/*  960 */   private final ResolutionForwarder fResolutionForwarder = new ResolutionForwarder(null);
/*      */ 
/*      */   public ValidatorHandlerImpl(XSGrammarPoolContainer grammarContainer)
/*      */   {
/*  189 */     this(new XMLSchemaValidatorComponentManager(grammarContainer));
/*  190 */     this.fComponentManager.addRecognizedFeatures(new String[] { "http://xml.org/sax/features/namespace-prefixes" });
/*  191 */     this.fComponentManager.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
/*  192 */     setErrorHandler(null);
/*  193 */     setResourceResolver(null);
/*      */   }
/*      */ 
/*      */   public ValidatorHandlerImpl(XMLSchemaValidatorComponentManager componentManager) {
/*  197 */     this.fComponentManager = componentManager;
/*  198 */     this.fErrorReporter = ((XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*  199 */     this.fNamespaceContext = ((NamespaceContext)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context"));
/*  200 */     this.fSchemaValidator = ((XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema"));
/*  201 */     this.fSymbolTable = ((SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/*  202 */     this.fValidationManager = ((ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
/*      */   }
/*      */ 
/*      */   public void setContentHandler(ContentHandler receiver)
/*      */   {
/*  210 */     this.fContentHandler = receiver;
/*      */   }
/*      */ 
/*      */   public ContentHandler getContentHandler() {
/*  214 */     return this.fContentHandler;
/*      */   }
/*      */ 
/*      */   public void setErrorHandler(ErrorHandler errorHandler) {
/*  218 */     this.fComponentManager.setErrorHandler(errorHandler);
/*      */   }
/*      */ 
/*      */   public ErrorHandler getErrorHandler() {
/*  222 */     return this.fComponentManager.getErrorHandler();
/*      */   }
/*      */ 
/*      */   public void setResourceResolver(LSResourceResolver resourceResolver) {
/*  226 */     this.fComponentManager.setResourceResolver(resourceResolver);
/*      */   }
/*      */ 
/*      */   public LSResourceResolver getResourceResolver() {
/*  230 */     return this.fComponentManager.getResourceResolver();
/*      */   }
/*      */ 
/*      */   public TypeInfoProvider getTypeInfoProvider() {
/*  234 */     return this.fTypeInfoProvider;
/*      */   }
/*      */ 
/*      */   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/*  239 */     if (name == null)
/*  240 */       throw new NullPointerException();
/*      */     try
/*      */     {
/*  243 */       return this.fComponentManager.getFeature(name);
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/*  246 */       String identifier = e.getIdentifier();
/*  247 */       String key = e.getType() == Status.NOT_RECOGNIZED ? "feature-not-recognized" : "feature-not-supported";
/*      */ 
/*  249 */       throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFeature(String name, boolean value)
/*      */     throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/*  257 */     if (name == null)
/*  258 */       throw new NullPointerException();
/*      */     try
/*      */     {
/*  261 */       this.fComponentManager.setFeature(name, value);
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/*  264 */       String identifier = e.getIdentifier();
/*      */ 
/*  266 */       if (e.getType() == Status.NOT_ALLOWED)
/*      */       {
/*  268 */         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", null));
/*      */       }
/*      */       String key;
/*      */       String key;
/*  271 */       if (e.getType() == Status.NOT_RECOGNIZED)
/*  272 */         key = "feature-not-recognized";
/*      */       else {
/*  274 */         key = "feature-not-supported";
/*      */       }
/*  276 */       throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getProperty(String name)
/*      */     throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/*  284 */     if (name == null)
/*  285 */       throw new NullPointerException();
/*      */     try
/*      */     {
/*  288 */       return this.fComponentManager.getProperty(name);
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/*  291 */       String identifier = e.getIdentifier();
/*  292 */       String key = e.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
/*      */ 
/*  294 */       throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setProperty(String name, Object object)
/*      */     throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/*  302 */     if (name == null)
/*  303 */       throw new NullPointerException();
/*      */     try
/*      */     {
/*  306 */       this.fComponentManager.setProperty(name, object);
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/*  309 */       String identifier = e.getIdentifier();
/*  310 */       String key = e.getType() == Status.NOT_RECOGNIZED ? "property-not-recognized" : "property-not-supported";
/*      */ 
/*  312 */       throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isEntityDeclared(String name)
/*      */   {
/*  323 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isEntityUnparsed(String name) {
/*  327 */     if (this.fUnparsedEntities != null) {
/*  328 */       return this.fUnparsedEntities.containsKey(name);
/*      */     }
/*  330 */     return false;
/*      */   }
/*      */ 
/*      */   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  340 */     if (this.fContentHandler != null)
/*      */       try {
/*  342 */         this.fContentHandler.startDocument();
/*      */       }
/*      */       catch (SAXException e) {
/*  345 */         throw new XNIException(e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
/*      */   }
/*      */ 
/*      */   public void comment(XMLString text, Augmentations augs) throws XNIException {
/*      */   }
/*      */ 
/*      */   public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
/*  360 */     if (this.fContentHandler != null)
/*      */       try {
/*  362 */         this.fContentHandler.processingInstruction(target, data.toString());
/*      */       }
/*      */       catch (SAXException e) {
/*  365 */         throw new XNIException(e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  372 */     if (this.fContentHandler != null)
/*      */       try {
/*  374 */         this.fTypeInfoProvider.beginStartElement(augs, attributes);
/*  375 */         this.fContentHandler.startElement(element.uri != null ? element.uri : XMLSymbols.EMPTY_STRING, element.localpart, element.rawname, this.fAttrAdapter);
/*      */       }
/*      */       catch (SAXException e)
/*      */       {
/*  379 */         throw new XNIException(e);
/*      */       }
/*      */       finally {
/*  382 */         this.fTypeInfoProvider.finishStartElement();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  390 */     startElement(element, attributes, augs);
/*  391 */     endElement(element, augs);
/*      */   }
/*      */ 
/*      */   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void textDecl(String version, String encoding, Augmentations augs) throws XNIException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
/*      */   }
/*      */ 
/*      */   public void characters(XMLString text, Augmentations augs) throws XNIException {
/*  406 */     if (this.fContentHandler != null)
/*      */     {
/*  409 */       if (text.length == 0)
/*  410 */         return;
/*      */       try
/*      */       {
/*  413 */         this.fContentHandler.characters(text.ch, text.offset, text.length);
/*      */       }
/*      */       catch (SAXException e) {
/*  416 */         throw new XNIException(e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException
/*      */   {
/*  423 */     if (this.fContentHandler != null)
/*      */       try {
/*  425 */         this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
/*      */       }
/*      */       catch (SAXException e) {
/*  428 */         throw new XNIException(e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void endElement(QName element, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  435 */     if (this.fContentHandler != null)
/*      */       try {
/*  437 */         this.fTypeInfoProvider.beginEndElement(augs);
/*  438 */         this.fContentHandler.endElement(element.uri != null ? element.uri : XMLSymbols.EMPTY_STRING, element.localpart, element.rawname);
/*      */       }
/*      */       catch (SAXException e)
/*      */       {
/*  442 */         throw new XNIException(e);
/*      */       }
/*      */       finally {
/*  445 */         this.fTypeInfoProvider.finishEndElement();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void startCDATA(Augmentations augs) throws XNIException {
/*      */   }
/*      */ 
/*      */   public void endCDATA(Augmentations augs) throws XNIException {
/*      */   }
/*      */ 
/*  455 */   public void endDocument(Augmentations augs) throws XNIException { if (this.fContentHandler != null)
/*      */       try {
/*  457 */         this.fContentHandler.endDocument();
/*      */       }
/*      */       catch (SAXException e) {
/*  460 */         throw new XNIException(e);
/*      */       } }
/*      */ 
/*      */   public void setDocumentSource(XMLDocumentSource source)
/*      */   {
/*      */   }
/*      */ 
/*      */   public XMLDocumentSource getDocumentSource()
/*      */   {
/*  469 */     return this.fSchemaValidator;
/*      */   }
/*      */ 
/*      */   public void setDocumentLocator(Locator locator)
/*      */   {
/*  477 */     this.fSAXLocatorWrapper.setLocator(locator);
/*  478 */     if (this.fContentHandler != null)
/*  479 */       this.fContentHandler.setDocumentLocator(locator);
/*      */   }
/*      */ 
/*      */   public void startDocument() throws SAXException
/*      */   {
/*  484 */     this.fComponentManager.reset();
/*  485 */     this.fSchemaValidator.setDocumentHandler(this);
/*  486 */     this.fValidationManager.setEntityState(this);
/*  487 */     this.fTypeInfoProvider.finishStartElement();
/*  488 */     this.fNeedPushNSContext = true;
/*  489 */     if ((this.fUnparsedEntities != null) && (!this.fUnparsedEntities.isEmpty()))
/*      */     {
/*  491 */       this.fUnparsedEntities.clear();
/*      */     }
/*  493 */     this.fErrorReporter.setDocumentLocator(this.fSAXLocatorWrapper);
/*      */     try {
/*  495 */       this.fSchemaValidator.startDocument(this.fSAXLocatorWrapper, this.fSAXLocatorWrapper.getEncoding(), this.fNamespaceContext, null);
/*      */     }
/*      */     catch (XMLParseException e) {
/*  498 */       throw Util.toSAXParseException(e);
/*      */     }
/*      */     catch (XNIException e) {
/*  501 */       throw Util.toSAXException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endDocument() throws SAXException {
/*  506 */     this.fSAXLocatorWrapper.setLocator(null);
/*      */     try {
/*  508 */       this.fSchemaValidator.endDocument(null);
/*      */     }
/*      */     catch (XMLParseException e) {
/*  511 */       throw Util.toSAXParseException(e);
/*      */     }
/*      */     catch (XNIException e) {
/*  514 */       throw Util.toSAXException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startPrefixMapping(String prefix, String uri)
/*      */     throws SAXException
/*      */   {
/*      */     String uriSymbol;
/*      */     String prefixSymbol;
/*      */     String uriSymbol;
/*  522 */     if (!this.fStringsInternalized) {
/*  523 */       String prefixSymbol = prefix != null ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
/*  524 */       uriSymbol = (uri != null) && (uri.length() > 0) ? this.fSymbolTable.addSymbol(uri) : null;
/*      */     }
/*      */     else {
/*  527 */       prefixSymbol = prefix != null ? prefix : XMLSymbols.EMPTY_STRING;
/*  528 */       uriSymbol = (uri != null) && (uri.length() > 0) ? uri : null;
/*      */     }
/*  530 */     if (this.fNeedPushNSContext) {
/*  531 */       this.fNeedPushNSContext = false;
/*  532 */       this.fNamespaceContext.pushContext();
/*      */     }
/*  534 */     this.fNamespaceContext.declarePrefix(prefixSymbol, uriSymbol);
/*  535 */     if (this.fContentHandler != null)
/*  536 */       this.fContentHandler.startPrefixMapping(prefix, uri);
/*      */   }
/*      */ 
/*      */   public void endPrefixMapping(String prefix) throws SAXException
/*      */   {
/*  541 */     if (this.fContentHandler != null)
/*  542 */       this.fContentHandler.endPrefixMapping(prefix);
/*      */   }
/*      */ 
/*      */   public void startElement(String uri, String localName, String qName, Attributes atts)
/*      */     throws SAXException
/*      */   {
/*  548 */     if (this.fNeedPushNSContext) {
/*  549 */       this.fNamespaceContext.pushContext();
/*      */     }
/*  551 */     this.fNeedPushNSContext = true;
/*      */ 
/*  554 */     fillQName(this.fElementQName, uri, localName, qName);
/*      */ 
/*  557 */     if ((atts instanceof Attributes2)) {
/*  558 */       fillXMLAttributes2((Attributes2)atts);
/*      */     }
/*      */     else {
/*  561 */       fillXMLAttributes(atts);
/*      */     }
/*      */     try
/*      */     {
/*  565 */       this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
/*      */     }
/*      */     catch (XMLParseException e) {
/*  568 */       throw Util.toSAXParseException(e);
/*      */     }
/*      */     catch (XNIException e) {
/*  571 */       throw Util.toSAXException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endElement(String uri, String localName, String qName) throws SAXException
/*      */   {
/*  577 */     fillQName(this.fElementQName, uri, localName, qName);
/*      */     try {
/*  579 */       this.fSchemaValidator.endElement(this.fElementQName, null);
/*      */     }
/*      */     catch (XMLParseException e) {
/*  582 */       throw Util.toSAXParseException(e);
/*      */     }
/*      */     catch (XNIException e) {
/*  585 */       throw Util.toSAXException(e);
/*      */     }
/*      */     finally {
/*  588 */       this.fNamespaceContext.popContext();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void characters(char[] ch, int start, int length) throws SAXException
/*      */   {
/*      */     try {
/*  595 */       this.fTempString.setValues(ch, start, length);
/*  596 */       this.fSchemaValidator.characters(this.fTempString, null);
/*      */     }
/*      */     catch (XMLParseException e) {
/*  599 */       throw Util.toSAXParseException(e);
/*      */     }
/*      */     catch (XNIException e) {
/*  602 */       throw Util.toSAXException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
/*      */   {
/*      */     try {
/*  609 */       this.fTempString.setValues(ch, start, length);
/*  610 */       this.fSchemaValidator.ignorableWhitespace(this.fTempString, null);
/*      */     }
/*      */     catch (XMLParseException e) {
/*  613 */       throw Util.toSAXParseException(e);
/*      */     }
/*      */     catch (XNIException e) {
/*  616 */       throw Util.toSAXException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void processingInstruction(String target, String data)
/*      */     throws SAXException
/*      */   {
/*  627 */     if (this.fContentHandler != null)
/*  628 */       this.fContentHandler.processingInstruction(target, data);
/*      */   }
/*      */ 
/*      */   public void skippedEntity(String name)
/*      */     throws SAXException
/*      */   {
/*  635 */     if (this.fContentHandler != null)
/*  636 */       this.fContentHandler.skippedEntity(name);
/*      */   }
/*      */ 
/*      */   public void notationDecl(String name, String publicId, String systemId)
/*      */     throws SAXException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
/*      */     throws SAXException
/*      */   {
/*  649 */     if (this.fUnparsedEntities == null) {
/*  650 */       this.fUnparsedEntities = new HashMap();
/*      */     }
/*  652 */     this.fUnparsedEntities.put(name, name);
/*      */   }
/*      */ 
/*      */   public void validate(Source source, Result result)
/*      */     throws SAXException, IOException
/*      */   {
/*  661 */     if (((result instanceof SAXResult)) || (result == null)) {
/*  662 */       SAXSource saxSource = (SAXSource)source;
/*  663 */       SAXResult saxResult = (SAXResult)result;
/*      */ 
/*  665 */       if (result != null) {
/*  666 */         setContentHandler(saxResult.getHandler());
/*      */       }
/*      */       try
/*      */       {
/*  670 */         XMLReader reader = saxSource.getXMLReader();
/*  671 */         if (reader == null)
/*      */         {
/*  673 */           SAXParserFactory spf = this.fComponentManager.getFeature("http://www.oracle.com/feature/use-service-mechanism") ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
/*      */ 
/*  675 */           spf.setNamespaceAware(true);
/*      */           try {
/*  677 */             reader = spf.newSAXParser().getXMLReader();
/*      */ 
/*  679 */             if ((reader instanceof com.sun.org.apache.xerces.internal.parsers.SAXParser)) {
/*  680 */               SecurityManager securityManager = (SecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
/*  681 */               if (securityManager != null)
/*      */                 try {
/*  683 */                   reader.setProperty("http://apache.org/xml/properties/security-manager", securityManager);
/*      */                 }
/*      */                 catch (SAXException exc)
/*      */                 {
/*      */                 }
/*      */             }
/*      */           }
/*      */           catch (Exception e) {
/*  691 */             throw new FactoryConfigurationError(e);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  698 */           this.fStringsInternalized = reader.getFeature("http://xml.org/sax/features/string-interning");
/*      */         }
/*      */         catch (SAXException exc)
/*      */         {
/*  703 */           this.fStringsInternalized = false;
/*      */         }
/*      */ 
/*  706 */         ErrorHandler errorHandler = this.fComponentManager.getErrorHandler();
/*  707 */         reader.setErrorHandler(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance());
/*  708 */         reader.setEntityResolver(this.fResolutionForwarder);
/*  709 */         this.fResolutionForwarder.setEntityResolver(this.fComponentManager.getResourceResolver());
/*  710 */         reader.setContentHandler(this);
/*  711 */         reader.setDTDHandler(this);
/*      */ 
/*  713 */         InputSource is = saxSource.getInputSource();
/*  714 */         reader.parse(is);
/*      */       }
/*      */       finally
/*      */       {
/*  718 */         setContentHandler(null);
/*      */       }
/*  720 */       return;
/*      */     }
/*  722 */     throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
/*      */   }
/*      */ 
/*      */   public ElementPSVI getElementPSVI()
/*      */   {
/*  732 */     return this.fTypeInfoProvider.getElementPSVI();
/*      */   }
/*      */ 
/*      */   public AttributePSVI getAttributePSVI(int index) {
/*  736 */     return this.fTypeInfoProvider.getAttributePSVI(index);
/*      */   }
/*      */ 
/*      */   public AttributePSVI getAttributePSVIByName(String uri, String localname) {
/*  740 */     return this.fTypeInfoProvider.getAttributePSVIByName(uri, localname);
/*      */   }
/*      */ 
/*      */   private void fillQName(QName toFill, String uri, String localpart, String raw)
/*      */   {
/*  751 */     if (!this.fStringsInternalized) {
/*  752 */       uri = (uri != null) && (uri.length() > 0) ? this.fSymbolTable.addSymbol(uri) : null;
/*  753 */       localpart = localpart != null ? this.fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING;
/*  754 */       raw = raw != null ? this.fSymbolTable.addSymbol(raw) : XMLSymbols.EMPTY_STRING;
/*      */     }
/*      */     else {
/*  757 */       if ((uri != null) && (uri.length() == 0)) {
/*  758 */         uri = null;
/*      */       }
/*  760 */       if (localpart == null) {
/*  761 */         localpart = XMLSymbols.EMPTY_STRING;
/*      */       }
/*  763 */       if (raw == null) {
/*  764 */         raw = XMLSymbols.EMPTY_STRING;
/*      */       }
/*      */     }
/*  767 */     String prefix = XMLSymbols.EMPTY_STRING;
/*  768 */     int prefixIdx = raw.indexOf(':');
/*  769 */     if (prefixIdx != -1) {
/*  770 */       prefix = this.fSymbolTable.addSymbol(raw.substring(0, prefixIdx));
/*      */     }
/*  772 */     toFill.setValues(prefix, localpart, raw, uri);
/*      */   }
/*      */ 
/*      */   private void fillXMLAttributes(Attributes att)
/*      */   {
/*  777 */     this.fAttributes.removeAllAttributes();
/*  778 */     int len = att.getLength();
/*  779 */     for (int i = 0; i < len; i++) {
/*  780 */       fillXMLAttribute(att, i);
/*  781 */       this.fAttributes.setSpecified(i, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fillXMLAttributes2(Attributes2 att)
/*      */   {
/*  787 */     this.fAttributes.removeAllAttributes();
/*  788 */     int len = att.getLength();
/*  789 */     for (int i = 0; i < len; i++) {
/*  790 */       fillXMLAttribute(att, i);
/*  791 */       this.fAttributes.setSpecified(i, att.isSpecified(i));
/*  792 */       if (att.isDeclared(i))
/*  793 */         this.fAttributes.getAugmentations(i).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fillXMLAttribute(Attributes att, int index)
/*      */   {
/*  800 */     fillQName(this.fAttributeQName, att.getURI(index), att.getLocalName(index), att.getQName(index));
/*  801 */     String type = att.getType(index);
/*  802 */     this.fAttributes.addAttributeNS(this.fAttributeQName, type != null ? type : XMLSymbols.fCDATASymbol, att.getValue(index));
/*      */   }
/*      */ 
/*      */   static final class ResolutionForwarder
/*      */     implements EntityResolver2
/*      */   {
/*      */     private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
/*      */     protected LSResourceResolver fEntityResolver;
/*      */ 
/*      */     public ResolutionForwarder()
/*      */     {
/*      */     }
/*      */ 
/*      */     public ResolutionForwarder(LSResourceResolver entityResolver)
/*      */     {
/*  983 */       setEntityResolver(entityResolver);
/*      */     }
/*      */ 
/*      */     public void setEntityResolver(LSResourceResolver entityResolver)
/*      */     {
/*  992 */       this.fEntityResolver = entityResolver;
/*      */     }
/*      */ 
/*      */     public LSResourceResolver getEntityResolver()
/*      */     {
/*  997 */       return this.fEntityResolver;
/*      */     }
/*      */ 
/*      */     public InputSource getExternalSubset(String name, String baseURI)
/*      */       throws SAXException, IOException
/*      */     {
/* 1005 */       return null;
/*      */     }
/*      */ 
/*      */     public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId)
/*      */       throws SAXException, IOException
/*      */     {
/* 1014 */       if (this.fEntityResolver != null) {
/* 1015 */         LSInput lsInput = this.fEntityResolver.resolveResource("http://www.w3.org/TR/REC-xml", null, publicId, systemId, baseURI);
/* 1016 */         if (lsInput != null) {
/* 1017 */           String pubId = lsInput.getPublicId();
/* 1018 */           String sysId = lsInput.getSystemId();
/* 1019 */           String baseSystemId = lsInput.getBaseURI();
/* 1020 */           Reader charStream = lsInput.getCharacterStream();
/* 1021 */           InputStream byteStream = lsInput.getByteStream();
/* 1022 */           String data = lsInput.getStringData();
/* 1023 */           String encoding = lsInput.getEncoding();
/*      */ 
/* 1032 */           InputSource inputSource = new InputSource();
/* 1033 */           inputSource.setPublicId(pubId);
/* 1034 */           inputSource.setSystemId(baseSystemId != null ? resolveSystemId(systemId, baseSystemId) : systemId);
/*      */ 
/* 1036 */           if (charStream != null) {
/* 1037 */             inputSource.setCharacterStream(charStream);
/*      */           }
/* 1039 */           else if (byteStream != null) {
/* 1040 */             inputSource.setByteStream(byteStream);
/*      */           }
/* 1042 */           else if ((data != null) && (data.length() != 0)) {
/* 1043 */             inputSource.setCharacterStream(new StringReader(data));
/*      */           }
/* 1045 */           inputSource.setEncoding(encoding);
/* 1046 */           return inputSource;
/*      */         }
/*      */       }
/* 1049 */       return null;
/*      */     }
/*      */ 
/*      */     public InputSource resolveEntity(String publicId, String systemId)
/*      */       throws SAXException, IOException
/*      */     {
/* 1055 */       return resolveEntity(null, publicId, null, systemId);
/*      */     }
/*      */ 
/*      */     private String resolveSystemId(String systemId, String baseURI)
/*      */     {
/*      */       try {
/* 1061 */         return XMLEntityManager.expandSystemId(systemId, baseURI, false);
/*      */       }
/*      */       catch (URI.MalformedURIException ex)
/*      */       {
/*      */       }
/*      */ 
/* 1067 */       return systemId;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class XMLSchemaTypeInfoProvider extends TypeInfoProvider
/*      */   {
/*      */     private Augmentations fElementAugs;
/*      */     private XMLAttributes fAttributes;
/*  820 */     private boolean fInStartElement = false;
/*      */ 
/*  823 */     private boolean fInEndElement = false;
/*      */ 
/*      */     private XMLSchemaTypeInfoProvider() {
/*      */     }
/*  827 */     void beginStartElement(Augmentations elementAugs, XMLAttributes attributes) { this.fInStartElement = true;
/*  828 */       this.fElementAugs = elementAugs;
/*  829 */       this.fAttributes = attributes;
/*      */     }
/*      */ 
/*      */     void finishStartElement()
/*      */     {
/*  834 */       this.fInStartElement = false;
/*  835 */       this.fElementAugs = null;
/*  836 */       this.fAttributes = null;
/*      */     }
/*      */ 
/*      */     void beginEndElement(Augmentations elementAugs)
/*      */     {
/*  841 */       this.fInEndElement = true;
/*  842 */       this.fElementAugs = elementAugs;
/*      */     }
/*      */ 
/*      */     void finishEndElement()
/*      */     {
/*  847 */       this.fInEndElement = false;
/*  848 */       this.fElementAugs = null;
/*      */     }
/*      */ 
/*      */     private void checkState(boolean forElementInfo)
/*      */     {
/*  857 */       if ((!this.fInStartElement) && ((!this.fInEndElement) || (!forElementInfo)))
/*  858 */         throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalState", null));
/*      */     }
/*      */ 
/*      */     public TypeInfo getAttributeTypeInfo(int index)
/*      */     {
/*  864 */       checkState(false);
/*  865 */       return getAttributeType(index);
/*      */     }
/*      */ 
/*      */     private TypeInfo getAttributeType(int index) {
/*  869 */       checkState(false);
/*  870 */       if ((index < 0) || (this.fAttributes.getLength() <= index))
/*  871 */         throw new IndexOutOfBoundsException(Integer.toString(index));
/*  872 */       Augmentations augs = this.fAttributes.getAugmentations(index);
/*  873 */       if (augs == null) return null;
/*  874 */       AttributePSVI psvi = (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
/*  875 */       return getTypeInfoFromPSVI(psvi);
/*      */     }
/*      */ 
/*      */     public TypeInfo getAttributeTypeInfo(String attributeUri, String attributeLocalName) {
/*  879 */       checkState(false);
/*  880 */       return getAttributeTypeInfo(this.fAttributes.getIndex(attributeUri, attributeLocalName));
/*      */     }
/*      */ 
/*      */     public TypeInfo getAttributeTypeInfo(String attributeQName) {
/*  884 */       checkState(false);
/*  885 */       return getAttributeTypeInfo(this.fAttributes.getIndex(attributeQName));
/*      */     }
/*      */ 
/*      */     public TypeInfo getElementTypeInfo() {
/*  889 */       checkState(true);
/*  890 */       if (this.fElementAugs == null) return null;
/*  891 */       ElementPSVI psvi = (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI");
/*  892 */       return getTypeInfoFromPSVI(psvi);
/*      */     }
/*      */ 
/*      */     private TypeInfo getTypeInfoFromPSVI(ItemPSVI psvi) {
/*  896 */       if (psvi == null) return null;
/*      */ 
/*  902 */       if (psvi.getValidity() == 2) {
/*  903 */         XSTypeDefinition t = psvi.getMemberTypeDefinition();
/*  904 */         if (t != null) {
/*  905 */           return (t instanceof TypeInfo) ? (TypeInfo)t : null;
/*      */         }
/*      */       }
/*      */ 
/*  909 */       XSTypeDefinition t = psvi.getTypeDefinition();
/*      */ 
/*  911 */       if (t != null) {
/*  912 */         return (t instanceof TypeInfo) ? (TypeInfo)t : null;
/*      */       }
/*  914 */       return null;
/*      */     }
/*      */ 
/*      */     public boolean isIdAttribute(int index) {
/*  918 */       checkState(false);
/*  919 */       XSSimpleType type = (XSSimpleType)getAttributeType(index);
/*  920 */       if (type == null) return false;
/*  921 */       return type.isIDType();
/*      */     }
/*      */ 
/*      */     public boolean isSpecified(int index) {
/*  925 */       checkState(false);
/*  926 */       return this.fAttributes.isSpecified(index);
/*      */     }
/*      */ 
/*      */     ElementPSVI getElementPSVI()
/*      */     {
/*  935 */       return this.fElementAugs != null ? (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI") : null;
/*      */     }
/*      */ 
/*      */     AttributePSVI getAttributePSVI(int index) {
/*  939 */       if (this.fAttributes != null) {
/*  940 */         Augmentations augs = this.fAttributes.getAugmentations(index);
/*  941 */         if (augs != null) {
/*  942 */           return (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
/*      */         }
/*      */       }
/*  945 */       return null;
/*      */     }
/*      */ 
/*      */     AttributePSVI getAttributePSVIByName(String uri, String localname) {
/*  949 */       if (this.fAttributes != null) {
/*  950 */         Augmentations augs = this.fAttributes.getAugmentations(uri, localname);
/*  951 */         if (augs != null) {
/*  952 */           return (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
/*      */         }
/*      */       }
/*  955 */       return null;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.validation.ValidatorHandlerImpl
 * JD-Core Version:    0.6.2
 */