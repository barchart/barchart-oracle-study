/*     */ package com.sun.org.apache.xerces.internal.jaxp.validation;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
/*     */ import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
/*     */ import com.sun.org.apache.xerces.internal.util.DOMInputSource;
/*     */ import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
/*     */ import com.sun.org.apache.xerces.internal.util.SAXInputSource;
/*     */ import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*     */ import com.sun.org.apache.xerces.internal.util.StAXInputSource;
/*     */ import com.sun.org.apache.xerces.internal.util.Status;
/*     */ import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
/*     */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import javax.xml.stream.XMLEventReader;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import javax.xml.validation.Schema;
/*     */ import javax.xml.validation.SchemaFactory;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.ls.LSResourceResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ public final class XMLSchemaFactory extends SchemaFactory
/*     */ {
/*     */   private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
/*     */   private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*     */   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*  90 */   private final XMLSchemaLoader fXMLSchemaLoader = new XMLSchemaLoader();
/*     */   private ErrorHandler fErrorHandler;
/*     */   private LSResourceResolver fLSResourceResolver;
/*     */   private final DOMEntityResolverWrapper fDOMEntityResolverWrapper;
/*     */   private ErrorHandlerWrapper fErrorHandlerWrapper;
/*     */   private SecurityManager fSecurityManager;
/*     */   private XMLGrammarPoolWrapper fXMLGrammarPoolWrapper;
/*     */   private final boolean fUseServicesMechanism;
/*     */ 
/*     */   public XMLSchemaFactory()
/*     */   {
/* 117 */     this(true);
/*     */   }
/*     */   public static XMLSchemaFactory newXMLSchemaFactoryNoServiceLoader() {
/* 120 */     return new XMLSchemaFactory(false);
/*     */   }
/*     */   private XMLSchemaFactory(boolean useServicesMechanism) {
/* 123 */     this.fUseServicesMechanism = useServicesMechanism;
/* 124 */     this.fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
/* 125 */     this.fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
/* 126 */     this.fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
/* 127 */     this.fXMLSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
/* 128 */     this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fXMLGrammarPoolWrapper);
/* 129 */     this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
/* 130 */     this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
/*     */ 
/* 133 */     this.fSecurityManager = new SecurityManager();
/* 134 */     this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
/*     */   }
/*     */ 
/*     */   public boolean isSchemaLanguageSupported(String schemaLanguage)
/*     */   {
/* 150 */     if (schemaLanguage == null) {
/* 151 */       throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageNull", null));
/*     */     }
/*     */ 
/* 154 */     if (schemaLanguage.length() == 0) {
/* 155 */       throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageLengthZero", null));
/*     */     }
/*     */ 
/* 159 */     return schemaLanguage.equals("http://www.w3.org/2001/XMLSchema");
/*     */   }
/*     */ 
/*     */   public LSResourceResolver getResourceResolver() {
/* 163 */     return this.fLSResourceResolver;
/*     */   }
/*     */ 
/*     */   public void setResourceResolver(LSResourceResolver resourceResolver) {
/* 167 */     this.fLSResourceResolver = resourceResolver;
/* 168 */     this.fDOMEntityResolverWrapper.setEntityResolver(resourceResolver);
/* 169 */     this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
/*     */   }
/*     */ 
/*     */   public ErrorHandler getErrorHandler() {
/* 173 */     return this.fErrorHandler;
/*     */   }
/*     */ 
/*     */   public void setErrorHandler(ErrorHandler errorHandler) {
/* 177 */     this.fErrorHandler = errorHandler;
/* 178 */     this.fErrorHandlerWrapper.setErrorHandler(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance());
/* 179 */     this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
/*     */   }
/*     */ 
/*     */   public Schema newSchema(Source[] schemas)
/*     */     throws SAXException
/*     */   {
/* 185 */     XMLGrammarPoolImplExtension pool = new XMLGrammarPoolImplExtension();
/* 186 */     this.fXMLGrammarPoolWrapper.setGrammarPool(pool);
/*     */ 
/* 188 */     XMLInputSource[] xmlInputSources = new XMLInputSource[schemas.length];
/*     */ 
/* 191 */     for (int i = 0; i < schemas.length; i++) {
/* 192 */       Source source = schemas[i];
/* 193 */       if ((source instanceof StreamSource)) {
/* 194 */         StreamSource streamSource = (StreamSource)source;
/* 195 */         String publicId = streamSource.getPublicId();
/* 196 */         String systemId = streamSource.getSystemId();
/* 197 */         InputStream inputStream = streamSource.getInputStream();
/* 198 */         Reader reader = streamSource.getReader();
/* 199 */         xmlInputSources[i] = new XMLInputSource(publicId, systemId, null);
/* 200 */         xmlInputSources[i].setByteStream(inputStream);
/* 201 */         xmlInputSources[i].setCharacterStream(reader);
/*     */       }
/* 203 */       else if ((source instanceof SAXSource)) {
/* 204 */         SAXSource saxSource = (SAXSource)source;
/* 205 */         InputSource inputSource = saxSource.getInputSource();
/* 206 */         if (inputSource == null) {
/* 207 */           throw new SAXException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SAXSourceNullInputSource", null));
/*     */         }
/*     */ 
/* 210 */         xmlInputSources[i] = new SAXInputSource(saxSource.getXMLReader(), inputSource);
/*     */       }
/* 212 */       else if ((source instanceof DOMSource)) {
/* 213 */         DOMSource domSource = (DOMSource)source;
/* 214 */         Node node = domSource.getNode();
/* 215 */         String systemID = domSource.getSystemId();
/* 216 */         xmlInputSources[i] = new DOMInputSource(node, systemID);
/*     */       }
/* 218 */       else if ((source instanceof StAXSource)) {
/* 219 */         StAXSource staxSource = (StAXSource)source;
/* 220 */         XMLEventReader eventReader = staxSource.getXMLEventReader();
/* 221 */         if (eventReader != null) {
/* 222 */           xmlInputSources[i] = new StAXInputSource(eventReader);
/*     */         }
/*     */         else
/* 225 */           xmlInputSources[i] = new StAXInputSource(staxSource.getXMLStreamReader());
/*     */       }
/*     */       else {
/* 228 */         if (source == null) {
/* 229 */           throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaSourceArrayMemberNull", null));
/*     */         }
/*     */ 
/* 233 */         throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaFactorySourceUnrecognized", new Object[] { source.getClass().getName() }));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 240 */       this.fXMLSchemaLoader.loadGrammar(xmlInputSources);
/*     */     }
/*     */     catch (XNIException e)
/*     */     {
/* 244 */       throw Util.toSAXException(e);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 248 */       SAXParseException se = new SAXParseException(e.getMessage(), null, e);
/* 249 */       this.fErrorHandler.error(se);
/* 250 */       throw se;
/*     */     }
/*     */ 
/* 254 */     this.fXMLGrammarPoolWrapper.setGrammarPool(null);
/*     */ 
/* 257 */     int grammarCount = pool.getGrammarCount();
/* 258 */     AbstractXMLSchema schema = null;
/* 259 */     if (grammarCount > 1) {
/* 260 */       schema = new XMLSchema(new ReadOnlyGrammarPool(pool));
/*     */     }
/* 262 */     else if (grammarCount == 1) {
/* 263 */       Grammar[] grammars = pool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
/* 264 */       schema = new SimpleXMLSchema(grammars[0]);
/*     */     }
/*     */     else {
/* 267 */       schema = new EmptyXMLSchema();
/*     */     }
/* 269 */     propagateFeatures(schema);
/* 270 */     return schema;
/*     */   }
/*     */ 
/*     */   public Schema newSchema() throws SAXException
/*     */   {
/* 275 */     AbstractXMLSchema schema = new WeakReferenceXMLSchema();
/* 276 */     propagateFeatures(schema);
/* 277 */     return schema; } 
/*     */   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException { // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ifnonnull +24 -> 25
/*     */     //   4: new 224	java/lang/NullPointerException
/*     */     //   7: dup
/*     */     //   8: aload_0
/*     */     //   9: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   12: invokevirtual 380	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getLocale	()Ljava/util/Locale;
/*     */     //   15: ldc 1
/*     */     //   17: aconst_null
/*     */     //   18: invokestatic 386	com/sun/org/apache/xerces/internal/jaxp/validation/JAXPValidationMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   21: invokespecial 417	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
/*     */     //   24: athrow
/*     */     //   25: aload_1
/*     */     //   26: ldc 13
/*     */     //   28: invokevirtual 420	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   31: ifeq +16 -> 47
/*     */     //   34: aload_0
/*     */     //   35: getfield 369	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fSecurityManager	Lcom/sun/org/apache/xerces/internal/util/SecurityManager;
/*     */     //   38: ifnull +7 -> 45
/*     */     //   41: iconst_1
/*     */     //   42: goto +4 -> 46
/*     */     //   45: iconst_0
/*     */     //   46: ireturn
/*     */     //   47: aload_0
/*     */     //   48: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   51: aload_1
/*     */     //   52: invokevirtual 378	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getFeature	(Ljava/lang/String;)Z
/*     */     //   55: ireturn
/*     */     //   56: astore_2
/*     */     //   57: aload_2
/*     */     //   58: invokevirtual 410	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getIdentifier	()Ljava/lang/String;
/*     */     //   61: astore_3
/*     */     //   62: aload_2
/*     */     //   63: invokevirtual 409	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getType	()Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   66: getstatic 372	com/sun/org/apache/xerces/internal/util/Status:NOT_RECOGNIZED	Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   69: if_acmpne +31 -> 100
/*     */     //   72: new 238	org/xml/sax/SAXNotRecognizedException
/*     */     //   75: dup
/*     */     //   76: aload_0
/*     */     //   77: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   80: invokevirtual 380	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getLocale	()Ljava/util/Locale;
/*     */     //   83: ldc 8
/*     */     //   85: iconst_1
/*     */     //   86: anewarray 225	java/lang/Object
/*     */     //   89: dup
/*     */     //   90: iconst_0
/*     */     //   91: aload_3
/*     */     //   92: aastore
/*     */     //   93: invokestatic 405	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   96: invokespecial 434	org/xml/sax/SAXNotRecognizedException:<init>	(Ljava/lang/String;)V
/*     */     //   99: athrow
/*     */     //   100: new 239	org/xml/sax/SAXNotSupportedException
/*     */     //   103: dup
/*     */     //   104: aload_0
/*     */     //   105: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   108: invokevirtual 380	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getLocale	()Ljava/util/Locale;
/*     */     //   111: ldc 9
/*     */     //   113: iconst_1
/*     */     //   114: anewarray 225	java/lang/Object
/*     */     //   117: dup
/*     */     //   118: iconst_0
/*     */     //   119: aload_3
/*     */     //   120: aastore
/*     */     //   121: invokestatic 405	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   124: invokespecial 435	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*     */     //   127: athrow
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   47	55	56	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException } 
/*     */   public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException { // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ifnonnull +24 -> 25
/*     */     //   4: new 224	java/lang/NullPointerException
/*     */     //   7: dup
/*     */     //   8: aload_0
/*     */     //   9: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   12: invokevirtual 380	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getLocale	()Ljava/util/Locale;
/*     */     //   15: ldc 2
/*     */     //   17: aconst_null
/*     */     //   18: invokestatic 386	com/sun/org/apache/xerces/internal/jaxp/validation/JAXPValidationMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   21: invokespecial 417	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
/*     */     //   24: athrow
/*     */     //   25: aload_1
/*     */     //   26: ldc 12
/*     */     //   28: invokevirtual 420	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   31: ifeq +8 -> 39
/*     */     //   34: aload_0
/*     */     //   35: getfield 369	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fSecurityManager	Lcom/sun/org/apache/xerces/internal/util/SecurityManager;
/*     */     //   38: areturn
/*     */     //   39: aload_1
/*     */     //   40: ldc 11
/*     */     //   42: invokevirtual 420	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   45: ifeq +31 -> 76
/*     */     //   48: new 239	org/xml/sax/SAXNotSupportedException
/*     */     //   51: dup
/*     */     //   52: aload_0
/*     */     //   53: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   56: invokevirtual 380	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getLocale	()Ljava/util/Locale;
/*     */     //   59: ldc 18
/*     */     //   61: iconst_1
/*     */     //   62: anewarray 225	java/lang/Object
/*     */     //   65: dup
/*     */     //   66: iconst_0
/*     */     //   67: aload_1
/*     */     //   68: aastore
/*     */     //   69: invokestatic 405	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   72: invokespecial 435	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*     */     //   75: athrow
/*     */     //   76: aload_0
/*     */     //   77: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   80: aload_1
/*     */     //   81: invokevirtual 381	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getProperty	(Ljava/lang/String;)Ljava/lang/Object;
/*     */     //   84: areturn
/*     */     //   85: astore_2
/*     */     //   86: aload_2
/*     */     //   87: invokevirtual 410	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getIdentifier	()Ljava/lang/String;
/*     */     //   90: astore_3
/*     */     //   91: aload_2
/*     */     //   92: invokevirtual 409	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getType	()Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   95: getstatic 372	com/sun/org/apache/xerces/internal/util/Status:NOT_RECOGNIZED	Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   98: if_acmpne +31 -> 129
/*     */     //   101: new 238	org/xml/sax/SAXNotRecognizedException
/*     */     //   104: dup
/*     */     //   105: aload_0
/*     */     //   106: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   109: invokevirtual 380	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getLocale	()Ljava/util/Locale;
/*     */     //   112: ldc 17
/*     */     //   114: iconst_1
/*     */     //   115: anewarray 225	java/lang/Object
/*     */     //   118: dup
/*     */     //   119: iconst_0
/*     */     //   120: aload_3
/*     */     //   121: aastore
/*     */     //   122: invokestatic 405	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   125: invokespecial 434	org/xml/sax/SAXNotRecognizedException:<init>	(Ljava/lang/String;)V
/*     */     //   128: athrow
/*     */     //   129: new 239	org/xml/sax/SAXNotSupportedException
/*     */     //   132: dup
/*     */     //   133: aload_0
/*     */     //   134: getfield 365	com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory:fXMLSchemaLoader	Lcom/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader;
/*     */     //   137: invokevirtual 380	com/sun/org/apache/xerces/internal/impl/xs/XMLSchemaLoader:getLocale	()Ljava/util/Locale;
/*     */     //   140: ldc 18
/*     */     //   142: iconst_1
/*     */     //   143: anewarray 225	java/lang/Object
/*     */     //   146: dup
/*     */     //   147: iconst_0
/*     */     //   148: aload_3
/*     */     //   149: aastore
/*     */     //   150: invokestatic 405	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   153: invokespecial 435	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*     */     //   156: athrow
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   76	84	85	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException } 
/* 341 */   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException { if (name == null) {
/* 342 */       throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", null));
/*     */     }
/*     */ 
/* 345 */     if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/* 346 */       if ((System.getSecurityManager() != null) && (!value)) {
/* 347 */         throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
/*     */       }
/*     */ 
/* 351 */       this.fSecurityManager = (value ? new SecurityManager() : null);
/* 352 */       this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
/* 353 */       return;
/* 354 */     }if (name.equals("http://www.oracle.com/feature/use-service-mechanism"))
/*     */     {
/* 356 */       if (System.getSecurityManager() != null)
/* 357 */         return;
/*     */     }
/*     */     try {
/* 360 */       this.fXMLSchemaLoader.setFeature(name, value);
/*     */     }
/*     */     catch (XMLConfigurationException e) {
/* 363 */       String identifier = e.getIdentifier();
/* 364 */       if (e.getType() == Status.NOT_RECOGNIZED) {
/* 365 */         throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[] { identifier }));
/*     */       }
/*     */ 
/* 370 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[] { identifier }));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setProperty(String name, Object object)
/*     */     throws SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/* 379 */     if (name == null) {
/* 380 */       throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", null));
/*     */     }
/*     */ 
/* 383 */     if (name.equals("http://apache.org/xml/properties/security-manager")) {
/* 384 */       this.fSecurityManager = ((SecurityManager)object);
/* 385 */       this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
/* 386 */       return;
/*     */     }
/* 388 */     if (name.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
/* 389 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { name }));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 394 */       this.fXMLSchemaLoader.setProperty(name, object);
/*     */     }
/*     */     catch (XMLConfigurationException e) {
/* 397 */       String identifier = e.getIdentifier();
/* 398 */       if (e.getType() == Status.NOT_RECOGNIZED) {
/* 399 */         throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[] { identifier }));
/*     */       }
/*     */ 
/* 404 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { identifier }));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void propagateFeatures(AbstractXMLSchema schema)
/*     */   {
/* 412 */     schema.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this.fSecurityManager != null);
/* 413 */     schema.setFeature("http://www.oracle.com/feature/use-service-mechanism", this.fUseServicesMechanism);
/* 414 */     String[] features = this.fXMLSchemaLoader.getRecognizedFeatures();
/* 415 */     for (int i = 0; i < features.length; i++) {
/* 416 */       boolean state = this.fXMLSchemaLoader.getFeature(features[i]);
/* 417 */       schema.setFeature(features[i], state);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class XMLGrammarPoolImplExtension extends XMLGrammarPoolImpl
/*     */   {
/*     */     public XMLGrammarPoolImplExtension()
/*     */     {
/*     */     }
/*     */ 
/*     */     public XMLGrammarPoolImplExtension(int initialCapacity)
/*     */     {
/* 434 */       super();
/*     */     }
/*     */ 
/*     */     int getGrammarCount()
/*     */     {
/* 439 */       return this.fGrammarCount;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class XMLGrammarPoolWrapper
/*     */     implements XMLGrammarPool
/*     */   {
/*     */     private XMLGrammarPool fGrammarPool;
/*     */ 
/*     */     public Grammar[] retrieveInitialGrammarSet(String grammarType)
/*     */     {
/* 456 */       return this.fGrammarPool.retrieveInitialGrammarSet(grammarType);
/*     */     }
/*     */ 
/*     */     public void cacheGrammars(String grammarType, Grammar[] grammars) {
/* 460 */       this.fGrammarPool.cacheGrammars(grammarType, grammars);
/*     */     }
/*     */ 
/*     */     public Grammar retrieveGrammar(XMLGrammarDescription desc) {
/* 464 */       return this.fGrammarPool.retrieveGrammar(desc);
/*     */     }
/*     */ 
/*     */     public void lockPool() {
/* 468 */       this.fGrammarPool.lockPool();
/*     */     }
/*     */ 
/*     */     public void unlockPool() {
/* 472 */       this.fGrammarPool.unlockPool();
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 476 */       this.fGrammarPool.clear();
/*     */     }
/*     */ 
/*     */     void setGrammarPool(XMLGrammarPool grammarPool)
/*     */     {
/* 484 */       this.fGrammarPool = grammarPool;
/*     */     }
/*     */ 
/*     */     XMLGrammarPool getGrammarPool() {
/* 488 */       return this.fGrammarPool;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory
 * JD-Core Version:    0.6.2
 */