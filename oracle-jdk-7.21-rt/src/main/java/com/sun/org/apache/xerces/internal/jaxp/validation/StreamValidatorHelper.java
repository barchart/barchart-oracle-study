/*     */ package com.sun.org.apache.xerces.internal.jaxp.validation;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*     */ import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
/*     */ import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
/*     */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.SoftReference;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.TransformerFactoryConfigurationError;
/*     */ import javax.xml.transform.sax.SAXTransformerFactory;
/*     */ import javax.xml.transform.sax.TransformerHandler;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ final class StreamValidatorHelper
/*     */   implements ValidatorHelper
/*     */ {
/*     */   private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
/*     */   private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*     */   private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
/*     */   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*     */   private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
/*     */   private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*     */   private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*     */   private static final String DEFAULT_TRANSFORMER_IMPL = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
/*  93 */   private SoftReference fConfiguration = new SoftReference(null);
/*     */   private XMLSchemaValidator fSchemaValidator;
/*     */   private XMLSchemaValidatorComponentManager fComponentManager;
/* 101 */   private ValidatorHandlerImpl handler = null;
/*     */ 
/*     */   public StreamValidatorHelper(XMLSchemaValidatorComponentManager componentManager) {
/* 104 */     this.fComponentManager = componentManager;
/* 105 */     this.fSchemaValidator = ((XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema"));
/*     */   }
/*     */ 
/*     */   public void validate(Source source, Result result) throws SAXException, IOException
/*     */   {
/* 110 */     if ((result == null) || ((result instanceof StreamResult))) {
/* 111 */       StreamSource streamSource = (StreamSource)source;
/*     */ 
/* 114 */       if (result != null) {
/*     */         TransformerHandler identityTransformerHandler;
/*     */         try { SAXTransformerFactory tf = this.fComponentManager.getFeature("http://www.oracle.com/feature/use-service-mechanism") ? (SAXTransformerFactory)SAXTransformerFactory.newInstance() : (SAXTransformerFactory)TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", StreamValidatorHelper.class.getClassLoader());
/*     */ 
/* 119 */           identityTransformerHandler = tf.newTransformerHandler();
/*     */         } catch (TransformerConfigurationException e) {
/* 121 */           throw new TransformerFactoryConfigurationError(e);
/*     */         }
/*     */ 
/* 124 */         this.handler = new ValidatorHandlerImpl(this.fComponentManager);
/* 125 */         this.handler.setContentHandler(identityTransformerHandler);
/* 126 */         identityTransformerHandler.setResult(result);
/*     */       }
/*     */ 
/* 129 */       XMLInputSource input = new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), null);
/* 130 */       input.setByteStream(streamSource.getInputStream());
/* 131 */       input.setCharacterStream(streamSource.getReader());
/*     */ 
/* 135 */       XMLParserConfiguration config = (XMLParserConfiguration)this.fConfiguration.get();
/* 136 */       if (config == null) {
/* 137 */         config = initialize();
/*     */       }
/* 140 */       else if (this.fComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
/* 141 */         config.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
/* 142 */         config.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
/*     */       }
/*     */ 
/* 146 */       this.fComponentManager.reset();
/* 147 */       this.fSchemaValidator.setDocumentHandler(this.handler);
/*     */       try
/*     */       {
/* 150 */         config.parse(input);
/*     */       }
/*     */       catch (XMLParseException e) {
/* 153 */         throw Util.toSAXParseException(e);
/*     */       }
/*     */       catch (XNIException e) {
/* 156 */         throw Util.toSAXException(e);
/*     */       }
/* 158 */       return;
/*     */     }
/* 160 */     throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
/*     */   }
/*     */ 
/*     */   private XMLParserConfiguration initialize()
/*     */   {
/* 166 */     XML11Configuration config = new XML11Configuration();
/* 167 */     config.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
/* 168 */     config.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
/* 169 */     XMLErrorReporter errorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
/* 170 */     config.setProperty("http://apache.org/xml/properties/internal/error-reporter", errorReporter);
/*     */ 
/* 172 */     if (errorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
/* 173 */       XMLMessageFormatter xmft = new XMLMessageFormatter();
/* 174 */       errorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
/* 175 */       errorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
/*     */     }
/* 177 */     config.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/* 178 */     config.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
/* 179 */     config.setDocumentHandler(this.fSchemaValidator);
/* 180 */     config.setDTDHandler(null);
/* 181 */     config.setDTDContentModelHandler(null);
/* 182 */     this.fConfiguration = new SoftReference(config);
/* 183 */     return config;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.validation.StreamValidatorHelper
 * JD-Core Version:    0.6.2
 */