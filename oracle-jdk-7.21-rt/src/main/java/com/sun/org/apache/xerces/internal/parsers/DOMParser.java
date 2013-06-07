/*     */ package com.sun.org.apache.xerces.internal.parsers;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
/*     */ import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
/*     */ import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
/*     */ import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.util.Status;
/*     */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*     */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*     */ import java.io.IOException;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.ext.EntityResolver2;
/*     */ import org.xml.sax.helpers.LocatorImpl;
/*     */ 
/*     */ public class DOMParser extends AbstractDOMParser
/*     */ {
/*     */   protected static final String USE_ENTITY_RESOLVER2 = "http://xml.org/sax/features/use-entity-resolver2";
/*     */   protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
/*  78 */   private static final String[] RECOGNIZED_FEATURES = { "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace" };
/*     */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*     */   protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*  93 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
/*     */ 
/* 105 */   protected boolean fUseEntityResolver2 = true;
/*     */ 
/*     */   public DOMParser(XMLParserConfiguration config)
/*     */   {
/* 115 */     super(config);
/*     */   }
/*     */ 
/*     */   public DOMParser()
/*     */   {
/* 122 */     this(null, null);
/*     */   }
/*     */ 
/*     */   public DOMParser(SymbolTable symbolTable)
/*     */   {
/* 129 */     this(symbolTable, null);
/*     */   }
/*     */ 
/*     */   public DOMParser(SymbolTable symbolTable, XMLGrammarPool grammarPool)
/*     */   {
/* 138 */     super(new XIncludeAwareParserConfiguration());
/*     */ 
/* 141 */     this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
/* 142 */     if (symbolTable != null) {
/* 143 */       this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
/*     */     }
/* 145 */     if (grammarPool != null) {
/* 146 */       this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
/*     */     }
/*     */ 
/* 149 */     this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
/*     */   }
/*     */ 
/*     */   public void parse(String systemId)
/*     */     throws SAXException, IOException
/*     */   {
/* 173 */     XMLInputSource source = new XMLInputSource(null, systemId, null);
/*     */     try {
/* 175 */       parse(source);
/*     */     }
/*     */     catch (XMLParseException e)
/*     */     {
/* 180 */       Exception ex = e.getException();
/* 181 */       if (ex == null)
/*     */       {
/* 184 */         LocatorImpl locatorImpl = new LocatorImpl();
/* 185 */         locatorImpl.setPublicId(e.getPublicId());
/* 186 */         locatorImpl.setSystemId(e.getExpandedSystemId());
/* 187 */         locatorImpl.setLineNumber(e.getLineNumber());
/* 188 */         locatorImpl.setColumnNumber(e.getColumnNumber());
/* 189 */         throw new SAXParseException(e.getMessage(), locatorImpl);
/*     */       }
/* 191 */       if ((ex instanceof SAXException))
/*     */       {
/* 193 */         throw ((SAXException)ex);
/*     */       }
/* 195 */       if ((ex instanceof IOException)) {
/* 196 */         throw ((IOException)ex);
/*     */       }
/* 198 */       throw new SAXException(ex);
/*     */     }
/*     */     catch (XNIException e) {
/* 201 */       e.printStackTrace();
/* 202 */       Exception ex = e.getException();
/* 203 */       if (ex == null) {
/* 204 */         throw new SAXException(e.getMessage());
/*     */       }
/* 206 */       if ((ex instanceof SAXException)) {
/* 207 */         throw ((SAXException)ex);
/*     */       }
/* 209 */       if ((ex instanceof IOException)) {
/* 210 */         throw ((IOException)ex);
/*     */       }
/* 212 */       throw new SAXException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void parse(InputSource inputSource)
/*     */     throws SAXException, IOException
/*     */   {
/*     */     try
/*     */     {
/* 230 */       XMLInputSource xmlInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
/*     */ 
/* 234 */       xmlInputSource.setByteStream(inputSource.getByteStream());
/* 235 */       xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
/* 236 */       xmlInputSource.setEncoding(inputSource.getEncoding());
/* 237 */       parse(xmlInputSource);
/*     */     }
/*     */     catch (XMLParseException e)
/*     */     {
/* 242 */       Exception ex = e.getException();
/* 243 */       if (ex == null)
/*     */       {
/* 246 */         LocatorImpl locatorImpl = new LocatorImpl();
/* 247 */         locatorImpl.setPublicId(e.getPublicId());
/* 248 */         locatorImpl.setSystemId(e.getExpandedSystemId());
/* 249 */         locatorImpl.setLineNumber(e.getLineNumber());
/* 250 */         locatorImpl.setColumnNumber(e.getColumnNumber());
/* 251 */         throw new SAXParseException(e.getMessage(), locatorImpl);
/*     */       }
/* 253 */       if ((ex instanceof SAXException))
/*     */       {
/* 255 */         throw ((SAXException)ex);
/*     */       }
/* 257 */       if ((ex instanceof IOException)) {
/* 258 */         throw ((IOException)ex);
/*     */       }
/* 260 */       throw new SAXException(ex);
/*     */     }
/*     */     catch (XNIException e) {
/* 263 */       Exception ex = e.getException();
/* 264 */       if (ex == null) {
/* 265 */         throw new SAXException(e.getMessage());
/*     */       }
/* 267 */       if ((ex instanceof SAXException)) {
/* 268 */         throw ((SAXException)ex);
/*     */       }
/* 270 */       if ((ex instanceof IOException)) {
/* 271 */         throw ((IOException)ex);
/*     */       }
/* 273 */       throw new SAXException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setEntityResolver(EntityResolver resolver)
/*     */   {
/*     */     try
/*     */     {
/* 288 */       XMLEntityResolver xer = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
/* 289 */       if ((this.fUseEntityResolver2) && ((resolver instanceof EntityResolver2))) {
/* 290 */         if ((xer instanceof EntityResolver2Wrapper)) {
/* 291 */           EntityResolver2Wrapper er2w = (EntityResolver2Wrapper)xer;
/* 292 */           er2w.setEntityResolver((EntityResolver2)resolver);
/*     */         }
/*     */         else {
/* 295 */           this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)resolver));
/*     */         }
/*     */ 
/*     */       }
/* 300 */       else if ((xer instanceof EntityResolverWrapper)) {
/* 301 */         EntityResolverWrapper erw = (EntityResolverWrapper)xer;
/* 302 */         erw.setEntityResolver(resolver);
/*     */       }
/*     */       else {
/* 305 */         this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(resolver));
/*     */       }
/*     */     }
/*     */     catch (XMLConfigurationException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public EntityResolver getEntityResolver()
/*     */   {
/* 325 */     EntityResolver entityResolver = null;
/*     */     try {
/* 327 */       XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
/*     */ 
/* 329 */       if (xmlEntityResolver != null) {
/* 330 */         if ((xmlEntityResolver instanceof EntityResolverWrapper)) {
/* 331 */           entityResolver = ((EntityResolverWrapper)xmlEntityResolver).getEntityResolver();
/*     */         }
/* 334 */         else if ((xmlEntityResolver instanceof EntityResolver2Wrapper)) {
/* 335 */           entityResolver = ((EntityResolver2Wrapper)xmlEntityResolver).getEntityResolver();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (XMLConfigurationException e)
/*     */     {
/*     */     }
/*     */ 
/* 343 */     return entityResolver;
/*     */   }
/*     */ 
/*     */   public void setErrorHandler(ErrorHandler errorHandler)
/*     */   {
/*     */     try
/*     */     {
/* 368 */       XMLErrorHandler xeh = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
/* 369 */       if ((xeh instanceof ErrorHandlerWrapper)) {
/* 370 */         ErrorHandlerWrapper ehw = (ErrorHandlerWrapper)xeh;
/* 371 */         ehw.setErrorHandler(errorHandler);
/*     */       }
/*     */       else {
/* 374 */         this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
/*     */       }
/*     */     }
/*     */     catch (XMLConfigurationException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public ErrorHandler getErrorHandler()
/*     */   {
/* 393 */     ErrorHandler errorHandler = null;
/*     */     try {
/* 395 */       XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
/*     */ 
/* 397 */       if ((xmlErrorHandler != null) && ((xmlErrorHandler instanceof ErrorHandlerWrapper)))
/*     */       {
/* 399 */         errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
/*     */       }
/*     */     }
/*     */     catch (XMLConfigurationException e)
/*     */     {
/*     */     }
/* 405 */     return errorHandler;
/*     */   }
/*     */ 
/*     */   public void setFeature(String featureId, boolean state)
/*     */     throws SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/*     */     try
/*     */     {
/* 432 */       if (featureId.equals("http://xml.org/sax/features/use-entity-resolver2")) {
/* 433 */         if (state != this.fUseEntityResolver2) {
/* 434 */           this.fUseEntityResolver2 = state;
/*     */ 
/* 436 */           setEntityResolver(getEntityResolver());
/*     */         }
/* 438 */         return;
/*     */       }
/*     */ 
/* 445 */       this.fConfiguration.setFeature(featureId, state);
/*     */     }
/*     */     catch (XMLConfigurationException e) {
/* 448 */       String identifier = e.getIdentifier();
/* 449 */       if (e.getType() == Status.NOT_RECOGNIZED) {
/* 450 */         throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
/*     */       }
/*     */ 
/* 455 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier })); }  } 
/*     */   public boolean getFeature(String featureId) throws SAXNotRecognizedException, SAXNotSupportedException { // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ldc 11
/*     */     //   3: invokevirtual 321	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   6: ifeq +8 -> 14
/*     */     //   9: aload_0
/*     */     //   10: getfield 283	com/sun/org/apache/xerces/internal/parsers/DOMParser:fUseEntityResolver2	Z
/*     */     //   13: ireturn
/*     */     //   14: aload_0
/*     */     //   15: getfield 284	com/sun/org/apache/xerces/internal/parsers/DOMParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*     */     //   18: aload_1
/*     */     //   19: invokeinterface 337 2 0
/*     */     //   24: ireturn
/*     */     //   25: astore_2
/*     */     //   26: aload_2
/*     */     //   27: invokevirtual 310	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getIdentifier	()Ljava/lang/String;
/*     */     //   30: astore_3
/*     */     //   31: aload_2
/*     */     //   32: invokevirtual 309	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getType	()Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   35: getstatic 288	com/sun/org/apache/xerces/internal/util/Status:NOT_RECOGNIZED	Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   38: if_acmpne +33 -> 71
/*     */     //   41: new 181	org/xml/sax/SAXNotRecognizedException
/*     */     //   44: dup
/*     */     //   45: aload_0
/*     */     //   46: getfield 284	com/sun/org/apache/xerces/internal/parsers/DOMParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*     */     //   49: invokeinterface 341 1 0
/*     */     //   54: ldc 2
/*     */     //   56: iconst_1
/*     */     //   57: anewarray 174	java/lang/Object
/*     */     //   60: dup
/*     */     //   61: iconst_0
/*     */     //   62: aload_3
/*     */     //   63: aastore
/*     */     //   64: invokestatic 305	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   67: invokespecial 329	org/xml/sax/SAXNotRecognizedException:<init>	(Ljava/lang/String;)V
/*     */     //   70: athrow
/*     */     //   71: new 182	org/xml/sax/SAXNotSupportedException
/*     */     //   74: dup
/*     */     //   75: aload_0
/*     */     //   76: getfield 284	com/sun/org/apache/xerces/internal/parsers/DOMParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*     */     //   79: invokeinterface 341 1 0
/*     */     //   84: ldc 3
/*     */     //   86: iconst_1
/*     */     //   87: anewarray 174	java/lang/Object
/*     */     //   90: dup
/*     */     //   91: iconst_0
/*     */     //   92: aload_3
/*     */     //   93: aastore
/*     */     //   94: invokestatic 305	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   97: invokespecial 330	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*     */     //   100: athrow
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	13	25	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*     */     //   14	24	25	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException } 
/*     */   public void setProperty(String propertyId, Object value) throws SAXNotRecognizedException, SAXNotSupportedException { try { this.fConfiguration.setProperty(propertyId, value);
/*     */     } catch (XMLConfigurationException e)
/*     */     {
/* 534 */       String identifier = e.getIdentifier();
/* 535 */       if (e.getType() == Status.NOT_RECOGNIZED) {
/* 536 */         throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
/*     */       }
/*     */ 
/* 541 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier })); }  } 
/*     */   public Object getProperty(String propertyId) throws SAXNotRecognizedException, SAXNotSupportedException { // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ldc 5
/*     */     //   3: invokevirtual 321	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   6: ifeq +59 -> 65
/*     */     //   9: iconst_0
/*     */     //   10: istore_2
/*     */     //   11: aload_0
/*     */     //   12: ldc 4
/*     */     //   14: invokevirtual 291	com/sun/org/apache/xerces/internal/parsers/DOMParser:getFeature	(Ljava/lang/String;)Z
/*     */     //   17: istore_2
/*     */     //   18: goto +4 -> 22
/*     */     //   21: astore_3
/*     */     //   22: iload_2
/*     */     //   23: ifeq +13 -> 36
/*     */     //   26: new 182	org/xml/sax/SAXNotSupportedException
/*     */     //   29: dup
/*     */     //   30: ldc 1
/*     */     //   32: invokespecial 330	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*     */     //   35: athrow
/*     */     //   36: aload_0
/*     */     //   37: getfield 287	com/sun/org/apache/xerces/internal/parsers/DOMParser:fCurrentNode	Lorg/w3c/dom/Node;
/*     */     //   40: ifnull +23 -> 63
/*     */     //   43: aload_0
/*     */     //   44: getfield 287	com/sun/org/apache/xerces/internal/parsers/DOMParser:fCurrentNode	Lorg/w3c/dom/Node;
/*     */     //   47: invokeinterface 344 1 0
/*     */     //   52: iconst_1
/*     */     //   53: if_icmpne +10 -> 63
/*     */     //   56: aload_0
/*     */     //   57: getfield 287	com/sun/org/apache/xerces/internal/parsers/DOMParser:fCurrentNode	Lorg/w3c/dom/Node;
/*     */     //   60: goto +4 -> 64
/*     */     //   63: aconst_null
/*     */     //   64: areturn
/*     */     //   65: aload_0
/*     */     //   66: getfield 284	com/sun/org/apache/xerces/internal/parsers/DOMParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*     */     //   69: aload_1
/*     */     //   70: invokeinterface 342 2 0
/*     */     //   75: areturn
/*     */     //   76: astore_2
/*     */     //   77: aload_2
/*     */     //   78: invokevirtual 310	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getIdentifier	()Ljava/lang/String;
/*     */     //   81: astore_3
/*     */     //   82: aload_2
/*     */     //   83: invokevirtual 309	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getType	()Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   86: getstatic 288	com/sun/org/apache/xerces/internal/util/Status:NOT_RECOGNIZED	Lcom/sun/org/apache/xerces/internal/util/Status;
/*     */     //   89: if_acmpne +33 -> 122
/*     */     //   92: new 181	org/xml/sax/SAXNotRecognizedException
/*     */     //   95: dup
/*     */     //   96: aload_0
/*     */     //   97: getfield 284	com/sun/org/apache/xerces/internal/parsers/DOMParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*     */     //   100: invokeinterface 341 1 0
/*     */     //   105: ldc 12
/*     */     //   107: iconst_1
/*     */     //   108: anewarray 174	java/lang/Object
/*     */     //   111: dup
/*     */     //   112: iconst_0
/*     */     //   113: aload_3
/*     */     //   114: aastore
/*     */     //   115: invokestatic 305	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   118: invokespecial 329	org/xml/sax/SAXNotRecognizedException:<init>	(Ljava/lang/String;)V
/*     */     //   121: athrow
/*     */     //   122: new 182	org/xml/sax/SAXNotSupportedException
/*     */     //   125: dup
/*     */     //   126: aload_0
/*     */     //   127: getfield 284	com/sun/org/apache/xerces/internal/parsers/DOMParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*     */     //   130: invokeinterface 341 1 0
/*     */     //   135: ldc 13
/*     */     //   137: iconst_1
/*     */     //   138: anewarray 174	java/lang/Object
/*     */     //   141: dup
/*     */     //   142: iconst_0
/*     */     //   143: aload_3
/*     */     //   144: aastore
/*     */     //   145: invokestatic 305	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   148: invokespecial 330	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*     */     //   151: athrow
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   11	18	21	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*     */     //   65	75	76	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException } 
/* 604 */   public XMLParserConfiguration getXMLParserConfiguration() { return this.fConfiguration; }
/*     */ 
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.DOMParser
 * JD-Core Version:    0.6.2
 */