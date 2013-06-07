/*     */ package com.sun.org.apache.xerces.internal.jaxp;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*     */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
/*     */ import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
/*     */ import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*     */ import com.sun.org.apache.xerces.internal.util.Status;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*     */ import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
/*     */ import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
/*     */ import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.validation.Schema;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.HandlerBase;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.Parser;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class SAXParserImpl extends javax.xml.parsers.SAXParser
/*     */   implements JAXPConstants, PSVIProvider
/*     */ {
/*     */   private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
/*     */   private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
/*     */   private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
/*     */   private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
/*     */   private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
/*     */   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*     */   private final JAXPSAXParser xmlReader;
/*  96 */   private String schemaLanguage = null;
/*     */   private final Schema grammar;
/*     */   private final XMLComponent fSchemaValidator;
/*     */   private final XMLComponentManager fSchemaValidatorComponentManager;
/*     */   private final ValidationManager fSchemaValidationManager;
/*     */   private final UnparsedEntityHandler fUnparsedEntityHandler;
/*     */   private final ErrorHandler fInitErrorHandler;
/*     */   private final EntityResolver fInitEntityResolver;
/*     */ 
/*     */   SAXParserImpl(SAXParserFactoryImpl spf, Hashtable features)
/*     */     throws SAXException
/*     */   {
/* 116 */     this(spf, features, false);
/*     */   }
/*     */ 
/*     */   SAXParserImpl(SAXParserFactoryImpl spf, Hashtable features, boolean secureProcessing)
/*     */     throws SAXException
/*     */   {
/* 127 */     this.xmlReader = new JAXPSAXParser(this);
/*     */ 
/* 132 */     this.xmlReader.setFeature0("http://xml.org/sax/features/namespaces", spf.isNamespaceAware());
/*     */ 
/* 137 */     this.xmlReader.setFeature0("http://xml.org/sax/features/namespace-prefixes", !spf.isNamespaceAware());
/*     */ 
/* 142 */     if (spf.isXIncludeAware()) {
/* 143 */       this.xmlReader.setFeature0("http://apache.org/xml/features/xinclude", true);
/*     */     }
/*     */ 
/* 147 */     if (secureProcessing) {
/* 148 */       this.xmlReader.setProperty0("http://apache.org/xml/properties/security-manager", new SecurityManager());
/*     */     }
/*     */ 
/* 152 */     setFeatures(features);
/*     */ 
/* 157 */     if (spf.isValidating()) {
/* 158 */       this.fInitErrorHandler = new DefaultValidationErrorHandler();
/* 159 */       this.xmlReader.setErrorHandler(this.fInitErrorHandler);
/*     */     }
/*     */     else {
/* 162 */       this.fInitErrorHandler = this.xmlReader.getErrorHandler();
/*     */     }
/* 164 */     this.xmlReader.setFeature0("http://xml.org/sax/features/validation", spf.isValidating());
/*     */ 
/* 167 */     this.grammar = spf.getSchema();
/* 168 */     if (this.grammar != null) {
/* 169 */       XMLParserConfiguration config = this.xmlReader.getXMLParserConfiguration();
/* 170 */       XMLComponent validatorComponent = null;
/*     */ 
/* 172 */       if ((this.grammar instanceof XSGrammarPoolContainer)) {
/* 173 */         validatorComponent = new XMLSchemaValidator();
/* 174 */         this.fSchemaValidationManager = new ValidationManager();
/* 175 */         this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager);
/* 176 */         config.setDTDHandler(this.fUnparsedEntityHandler);
/* 177 */         this.fUnparsedEntityHandler.setDTDHandler(this.xmlReader);
/* 178 */         this.xmlReader.setDTDSource(this.fUnparsedEntityHandler);
/* 179 */         this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(config, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
/*     */       }
/*     */       else
/*     */       {
/* 184 */         validatorComponent = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
/* 185 */         this.fSchemaValidationManager = null;
/* 186 */         this.fUnparsedEntityHandler = null;
/* 187 */         this.fSchemaValidatorComponentManager = config;
/*     */       }
/* 189 */       config.addRecognizedFeatures(validatorComponent.getRecognizedFeatures());
/* 190 */       config.addRecognizedProperties(validatorComponent.getRecognizedProperties());
/* 191 */       config.setDocumentHandler((XMLDocumentHandler)validatorComponent);
/* 192 */       ((XMLDocumentSource)validatorComponent).setDocumentHandler(this.xmlReader);
/* 193 */       this.xmlReader.setDocumentSource((XMLDocumentSource)validatorComponent);
/* 194 */       this.fSchemaValidator = validatorComponent;
/*     */     }
/*     */     else {
/* 197 */       this.fSchemaValidationManager = null;
/* 198 */       this.fUnparsedEntityHandler = null;
/* 199 */       this.fSchemaValidatorComponentManager = null;
/* 200 */       this.fSchemaValidator = null;
/*     */     }
/*     */ 
/* 204 */     this.fInitEntityResolver = this.xmlReader.getEntityResolver();
/*     */   }
/*     */ 
/*     */   private void setFeatures(Hashtable features)
/*     */     throws SAXNotSupportedException, SAXNotRecognizedException
/*     */   {
/* 216 */     if (features != null) {
/* 217 */       Iterator entries = features.entrySet().iterator();
/* 218 */       while (entries.hasNext()) {
/* 219 */         Map.Entry entry = (Map.Entry)entries.next();
/* 220 */         String feature = (String)entry.getKey();
/* 221 */         boolean value = ((Boolean)entry.getValue()).booleanValue();
/* 222 */         this.xmlReader.setFeature0(feature, value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Parser getParser()
/*     */     throws SAXException
/*     */   {
/* 230 */     return this.xmlReader;
/*     */   }
/*     */ 
/*     */   public XMLReader getXMLReader()
/*     */   {
/* 238 */     return this.xmlReader;
/*     */   }
/*     */ 
/*     */   public boolean isNamespaceAware() {
/*     */     try {
/* 243 */       return this.xmlReader.getFeature("http://xml.org/sax/features/namespaces");
/*     */     }
/*     */     catch (SAXException x) {
/* 246 */       throw new IllegalStateException(x.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isValidating() {
/*     */     try {
/* 252 */       return this.xmlReader.getFeature("http://xml.org/sax/features/validation");
/*     */     }
/*     */     catch (SAXException x) {
/* 255 */       throw new IllegalStateException(x.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isXIncludeAware()
/*     */   {
/*     */     try
/*     */     {
/* 265 */       return this.xmlReader.getFeature("http://apache.org/xml/features/xinclude");
/*     */     } catch (SAXException exc) {
/*     */     }
/* 268 */     return false;
/*     */   }
/*     */ 
/*     */   public void setProperty(String name, Object value)
/*     */     throws SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/* 278 */     this.xmlReader.setProperty(name, value);
/*     */   }
/*     */ 
/*     */   public Object getProperty(String name)
/*     */     throws SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/* 287 */     return this.xmlReader.getProperty(name);
/*     */   }
/*     */ 
/*     */   public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException
/*     */   {
/* 292 */     if (is == null) {
/* 293 */       throw new IllegalArgumentException();
/*     */     }
/* 295 */     if (dh != null) {
/* 296 */       this.xmlReader.setContentHandler(dh);
/* 297 */       this.xmlReader.setEntityResolver(dh);
/* 298 */       this.xmlReader.setErrorHandler(dh);
/* 299 */       this.xmlReader.setDTDHandler(dh);
/* 300 */       this.xmlReader.setDocumentHandler(null);
/*     */     }
/* 302 */     this.xmlReader.parse(is);
/*     */   }
/*     */ 
/*     */   public void parse(InputSource is, HandlerBase hb) throws SAXException, IOException
/*     */   {
/* 307 */     if (is == null) {
/* 308 */       throw new IllegalArgumentException();
/*     */     }
/* 310 */     if (hb != null) {
/* 311 */       this.xmlReader.setDocumentHandler(hb);
/* 312 */       this.xmlReader.setEntityResolver(hb);
/* 313 */       this.xmlReader.setErrorHandler(hb);
/* 314 */       this.xmlReader.setDTDHandler(hb);
/* 315 */       this.xmlReader.setContentHandler(null);
/*     */     }
/* 317 */     this.xmlReader.parse(is);
/*     */   }
/*     */ 
/*     */   public Schema getSchema() {
/* 321 */     return this.grammar;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*     */     try {
/* 327 */       this.xmlReader.restoreInitState();
/*     */     }
/*     */     catch (SAXException exc)
/*     */     {
/*     */     }
/*     */ 
/* 335 */     this.xmlReader.setContentHandler(null);
/* 336 */     this.xmlReader.setDTDHandler(null);
/* 337 */     if (this.xmlReader.getErrorHandler() != this.fInitErrorHandler) {
/* 338 */       this.xmlReader.setErrorHandler(this.fInitErrorHandler);
/*     */     }
/* 340 */     if (this.xmlReader.getEntityResolver() != this.fInitEntityResolver)
/* 341 */       this.xmlReader.setEntityResolver(this.fInitEntityResolver);
/*     */   }
/*     */ 
/*     */   public ElementPSVI getElementPSVI()
/*     */   {
/* 350 */     return this.xmlReader.getElementPSVI();
/*     */   }
/*     */ 
/*     */   public AttributePSVI getAttributePSVI(int index) {
/* 354 */     return this.xmlReader.getAttributePSVI(index);
/*     */   }
/*     */ 
/*     */   public AttributePSVI getAttributePSVIByName(String uri, String localname) {
/* 358 */     return this.xmlReader.getAttributePSVIByName(uri, localname);
/*     */   }
/*     */ 
/*     */   public static class JAXPSAXParser extends com.sun.org.apache.xerces.internal.parsers.SAXParser
/*     */   {
/* 368 */     private final HashMap fInitFeatures = new HashMap();
/* 369 */     private final HashMap fInitProperties = new HashMap();
/*     */     private final SAXParserImpl fSAXParser;
/*     */ 
/*     */     public JAXPSAXParser()
/*     */     {
/* 373 */       this(null);
/*     */     }
/*     */ 
/*     */     JAXPSAXParser(SAXParserImpl saxParser)
/*     */     {
/* 378 */       this.fSAXParser = saxParser;
/*     */     }
/*     */ 
/*     */     public synchronized void setFeature(String name, boolean value)
/*     */       throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 388 */       if (name == null)
/*     */       {
/* 390 */         throw new NullPointerException();
/*     */       }
/* 392 */       if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/*     */         try {
/* 394 */           setProperty("http://apache.org/xml/properties/security-manager", value ? new SecurityManager() : null);
/*     */         }
/*     */         catch (SAXNotRecognizedException exc)
/*     */         {
/* 399 */           if (value) {
/* 400 */             throw exc;
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (SAXNotSupportedException exc)
/*     */         {
/* 406 */           if (value) {
/* 407 */             throw exc;
/*     */           }
/*     */         }
/* 410 */         return;
/*     */       }
/* 412 */       if (!this.fInitFeatures.containsKey(name)) {
/* 413 */         boolean current = super.getFeature(name);
/* 414 */         this.fInitFeatures.put(name, current ? Boolean.TRUE : Boolean.FALSE);
/*     */       }
/*     */ 
/* 417 */       if ((this.fSAXParser != null) && (this.fSAXParser.fSchemaValidator != null)) {
/* 418 */         setSchemaValidatorFeature(name, value);
/*     */       }
/* 420 */       super.setFeature(name, value);
/*     */     }
/*     */ 
/*     */     public synchronized boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 425 */       if (name == null)
/*     */       {
/* 427 */         throw new NullPointerException();
/*     */       }
/* 429 */       if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/*     */         try {
/* 431 */           return super.getProperty("http://apache.org/xml/properties/security-manager") != null;
/*     */         }
/*     */         catch (SAXException exc)
/*     */         {
/* 435 */           return false;
/*     */         }
/*     */       }
/* 438 */       return super.getFeature(name);
/*     */     }
/*     */ 
/*     */     public synchronized void setProperty(String name, Object value)
/*     */       throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 448 */       if (name == null)
/*     */       {
/* 450 */         throw new NullPointerException();
/*     */       }
/* 452 */       if (this.fSAXParser != null)
/*     */       {
/* 454 */         if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name))
/*     */         {
/* 457 */           if (this.fSAXParser.grammar != null) {
/* 458 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { name }));
/*     */           }
/*     */ 
/* 461 */           if ("http://www.w3.org/2001/XMLSchema".equals(value))
/*     */           {
/* 463 */             if (this.fSAXParser.isValidating()) {
/* 464 */               this.fSAXParser.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
/* 465 */               setFeature("http://apache.org/xml/features/validation/schema", true);
/*     */ 
/* 468 */               if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage")) {
/* 469 */                 this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage"));
/*     */               }
/* 471 */               super.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
/*     */             }
/*     */ 
/*     */           }
/* 475 */           else if (value == null) {
/* 476 */             this.fSAXParser.schemaLanguage = null;
/* 477 */             setFeature("http://apache.org/xml/features/validation/schema", false);
/*     */           }
/*     */           else
/*     */           {
/* 483 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-not-supported", null));
/*     */           }
/*     */ 
/* 486 */           return;
/*     */         }
/* 488 */         if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(name))
/*     */         {
/* 491 */           if (this.fSAXParser.grammar != null) {
/* 492 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { name }));
/*     */           }
/*     */ 
/* 495 */           String val = (String)getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
/* 496 */           if ((val != null) && ("http://www.w3.org/2001/XMLSchema".equals(val))) {
/* 497 */             if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
/* 498 */               this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaSource", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource"));
/*     */             }
/* 500 */             super.setProperty(name, value);
/*     */           }
/*     */           else {
/* 503 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
/*     */           }
/*     */ 
/* 508 */           return;
/*     */         }
/*     */       }
/* 511 */       if (!this.fInitProperties.containsKey(name)) {
/* 512 */         this.fInitProperties.put(name, super.getProperty(name));
/*     */       }
/*     */ 
/* 515 */       if ((this.fSAXParser != null) && (this.fSAXParser.fSchemaValidator != null)) {
/* 516 */         setSchemaValidatorProperty(name, value);
/*     */       }
/* 518 */       super.setProperty(name, value);
/*     */     }
/*     */ 
/*     */     public synchronized Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 523 */       if (name == null)
/*     */       {
/* 525 */         throw new NullPointerException();
/*     */       }
/* 527 */       if ((this.fSAXParser != null) && ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name)))
/*     */       {
/* 529 */         return this.fSAXParser.schemaLanguage;
/*     */       }
/* 531 */       return super.getProperty(name);
/*     */     }
/*     */ 
/*     */     synchronized void restoreInitState()
/*     */       throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 537 */       if (!this.fInitFeatures.isEmpty()) {
/* 538 */         Iterator iter = this.fInitFeatures.entrySet().iterator();
/* 539 */         while (iter.hasNext()) {
/* 540 */           Map.Entry entry = (Map.Entry)iter.next();
/* 541 */           String name = (String)entry.getKey();
/* 542 */           boolean value = ((Boolean)entry.getValue()).booleanValue();
/* 543 */           super.setFeature(name, value);
/*     */         }
/* 545 */         this.fInitFeatures.clear();
/*     */       }
/* 547 */       if (!this.fInitProperties.isEmpty()) {
/* 548 */         Iterator iter = this.fInitProperties.entrySet().iterator();
/* 549 */         while (iter.hasNext()) {
/* 550 */           Map.Entry entry = (Map.Entry)iter.next();
/* 551 */           String name = (String)entry.getKey();
/* 552 */           Object value = entry.getValue();
/* 553 */           super.setProperty(name, value);
/*     */         }
/* 555 */         this.fInitProperties.clear();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void parse(InputSource inputSource) throws SAXException, IOException
/*     */     {
/* 561 */       if ((this.fSAXParser != null) && (this.fSAXParser.fSchemaValidator != null)) {
/* 562 */         if (this.fSAXParser.fSchemaValidationManager != null) {
/* 563 */           this.fSAXParser.fSchemaValidationManager.reset();
/* 564 */           this.fSAXParser.fUnparsedEntityHandler.reset();
/*     */         }
/* 566 */         resetSchemaValidator();
/*     */       }
/* 568 */       super.parse(inputSource);
/*     */     }
/*     */ 
/*     */     public void parse(String systemId) throws SAXException, IOException
/*     */     {
/* 573 */       if ((this.fSAXParser != null) && (this.fSAXParser.fSchemaValidator != null)) {
/* 574 */         if (this.fSAXParser.fSchemaValidationManager != null) {
/* 575 */           this.fSAXParser.fSchemaValidationManager.reset();
/* 576 */           this.fSAXParser.fUnparsedEntityHandler.reset();
/*     */         }
/* 578 */         resetSchemaValidator();
/*     */       }
/* 580 */       super.parse(systemId);
/*     */     }
/*     */ 
/*     */     XMLParserConfiguration getXMLParserConfiguration() {
/* 584 */       return this.fConfiguration;
/*     */     }
/*     */ 
/*     */     void setFeature0(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 589 */       super.setFeature(name, value);
/*     */     }
/*     */ 
/*     */     boolean getFeature0(String name) throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 594 */       return super.getFeature(name);
/*     */     }
/*     */ 
/*     */     void setProperty0(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 599 */       super.setProperty(name, value);
/*     */     }
/*     */ 
/*     */     Object getProperty0(String name) throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/* 604 */       return super.getProperty(name);
/*     */     }
/*     */ 
/*     */     private void setSchemaValidatorFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/*     */       try {
/* 610 */         this.fSAXParser.fSchemaValidator.setFeature(name, value);
/*     */       }
/*     */       catch (XMLConfigurationException e)
/*     */       {
/* 614 */         String identifier = e.getIdentifier();
/* 615 */         if (e.getType() == Status.NOT_RECOGNIZED) {
/* 616 */           throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
/*     */         }
/*     */ 
/* 621 */         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
/*     */       }
/*     */     }
/*     */ 
/*     */     private void setSchemaValidatorProperty(String name, Object value)
/*     */       throws SAXNotRecognizedException, SAXNotSupportedException
/*     */     {
/*     */       try
/*     */       {
/* 631 */         this.fSAXParser.fSchemaValidator.setProperty(name, value);
/*     */       }
/*     */       catch (XMLConfigurationException e)
/*     */       {
/* 635 */         String identifier = e.getIdentifier();
/* 636 */         if (e.getType() == Status.NOT_RECOGNIZED) {
/* 637 */           throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
/*     */         }
/*     */ 
/* 642 */         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
/*     */       }
/*     */     }
/*     */ 
/*     */     private void resetSchemaValidator()
/*     */       throws SAXException
/*     */     {
/*     */       try
/*     */       {
/* 651 */         this.fSAXParser.fSchemaValidator.reset(this.fSAXParser.fSchemaValidatorComponentManager);
/*     */       }
/*     */       catch (XMLConfigurationException e)
/*     */       {
/* 655 */         throw new SAXException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl
 * JD-Core Version:    0.6.2
 */