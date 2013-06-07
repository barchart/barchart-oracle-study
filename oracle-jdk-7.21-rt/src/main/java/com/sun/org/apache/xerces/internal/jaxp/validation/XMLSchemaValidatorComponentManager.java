/*     */ package com.sun.org.apache.xerces.internal.jaxp.validation;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*     */ import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
/*     */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*     */ import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
/*     */ import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
/*     */ import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
/*     */ import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
/*     */ import com.sun.org.apache.xerces.internal.util.FeatureState;
/*     */ import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
/*     */ import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
/*     */ import com.sun.org.apache.xerces.internal.util.PropertyState;
/*     */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*     */ import com.sun.org.apache.xerces.internal.util.Status;
/*     */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*     */ import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
/*     */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.w3c.dom.ls.LSResourceResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ 
/*     */ final class XMLSchemaValidatorComponentManager extends ParserConfigurationSettings
/*     */   implements XMLComponentManager
/*     */ {
/*     */   private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
/*     */   private static final String VALIDATION = "http://xml.org/sax/features/validation";
/*     */   private static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
/*     */   private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
/*     */   private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
/*     */   private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*     */   private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
/*     */   private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*     */   private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
/*     */   private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
/*     */   private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*     */   private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*     */   private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*     */   private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*     */   private static final String LOCALE = "http://apache.org/xml/properties/locale";
/* 132 */   private boolean _isSecureMode = false;
/*     */ 
/* 138 */   private boolean fConfigUpdated = true;
/*     */   private boolean fUseGrammarPoolOnly;
/* 147 */   private final HashMap fComponents = new HashMap();
/*     */   private XMLEntityManager fEntityManager;
/*     */   private XMLErrorReporter fErrorReporter;
/*     */   private NamespaceContext fNamespaceContext;
/*     */   private XMLSchemaValidator fSchemaValidator;
/*     */   private ValidationManager fValidationManager;
/* 173 */   private final HashMap fInitFeatures = new HashMap();
/*     */ 
/* 176 */   private final HashMap fInitProperties = new HashMap();
/*     */   private final SecurityManager fInitSecurityManager;
/* 186 */   private ErrorHandler fErrorHandler = null;
/*     */ 
/* 189 */   private LSResourceResolver fResourceResolver = null;
/*     */ 
/* 192 */   private Locale fLocale = null;
/*     */ 
/*     */   public XMLSchemaValidatorComponentManager(XSGrammarPoolContainer grammarContainer)
/*     */   {
/* 197 */     this.fEntityManager = new XMLEntityManager();
/* 198 */     this.fComponents.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
/*     */ 
/* 200 */     this.fErrorReporter = new XMLErrorReporter();
/* 201 */     this.fComponents.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
/*     */ 
/* 203 */     this.fNamespaceContext = new NamespaceSupport();
/* 204 */     this.fComponents.put("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
/*     */ 
/* 206 */     this.fSchemaValidator = new XMLSchemaValidator();
/* 207 */     this.fComponents.put("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
/*     */ 
/* 209 */     this.fValidationManager = new ValidationManager();
/* 210 */     this.fComponents.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
/*     */ 
/* 213 */     this.fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
/* 214 */     this.fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
/*     */ 
/* 216 */     if (System.getSecurityManager() != null) {
/* 217 */       this._isSecureMode = true;
/* 218 */       setProperty("http://apache.org/xml/properties/security-manager", new SecurityManager());
/*     */     } else {
/* 220 */       this.fComponents.put("http://apache.org/xml/properties/security-manager", null);
/*     */     }
/* 222 */     this.fComponents.put("http://apache.org/xml/properties/internal/symbol-table", new SymbolTable());
/*     */ 
/* 225 */     this.fComponents.put("http://apache.org/xml/properties/internal/grammar-pool", grammarContainer.getGrammarPool());
/* 226 */     this.fUseGrammarPoolOnly = grammarContainer.isFullyComposed();
/*     */ 
/* 229 */     this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
/*     */ 
/* 232 */     addRecognizedParamsAndSetDefaults(this.fEntityManager, grammarContainer);
/* 233 */     addRecognizedParamsAndSetDefaults(this.fErrorReporter, grammarContainer);
/* 234 */     addRecognizedParamsAndSetDefaults(this.fSchemaValidator, grammarContainer);
/*     */ 
/* 237 */     Boolean secureProcessing = grammarContainer.getFeature("http://javax.xml.XMLConstants/feature/secure-processing");
/* 238 */     if (Boolean.TRUE.equals(secureProcessing)) {
/* 239 */       this.fInitSecurityManager = new SecurityManager();
/*     */     }
/*     */     else {
/* 242 */       this.fInitSecurityManager = null;
/*     */     }
/* 244 */     this.fComponents.put("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
/*     */   }
/*     */ 
/*     */   public FeatureState getFeatureState(String featureId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 262 */     if ("http://apache.org/xml/features/internal/parser-settings".equals(featureId)) {
/* 263 */       return FeatureState.is(this.fConfigUpdated);
/*     */     }
/* 265 */     if (("http://xml.org/sax/features/validation".equals(featureId)) || ("http://apache.org/xml/features/validation/schema".equals(featureId))) {
/* 266 */       return FeatureState.is(true);
/*     */     }
/* 268 */     if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(featureId)) {
/* 269 */       return FeatureState.is(this.fUseGrammarPoolOnly);
/*     */     }
/* 271 */     if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(featureId)) {
/* 272 */       return FeatureState.is(getProperty("http://apache.org/xml/properties/security-manager") != null);
/*     */     }
/* 274 */     if ("http://apache.org/xml/features/validation/schema/element-default".equals(featureId)) {
/* 275 */       return FeatureState.is(true);
/*     */     }
/* 277 */     return super.getFeatureState(featureId);
/*     */   }
/*     */ 
/*     */   public void setFeature(String featureId, boolean value)
/*     */     throws XMLConfigurationException
/*     */   {
/* 289 */     if ("http://apache.org/xml/features/internal/parser-settings".equals(featureId)) {
/* 290 */       throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
/*     */     }
/* 292 */     if ((!value) && (("http://xml.org/sax/features/validation".equals(featureId)) || ("http://apache.org/xml/features/validation/schema".equals(featureId)))) {
/* 293 */       throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
/*     */     }
/* 295 */     if (("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(featureId)) && (value != this.fUseGrammarPoolOnly)) {
/* 296 */       throw new XMLConfigurationException(Status.NOT_SUPPORTED, featureId);
/*     */     }
/* 298 */     if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(featureId)) {
/* 299 */       if ((this._isSecureMode) && (!value)) {
/* 300 */         throw new XMLConfigurationException(Status.NOT_ALLOWED, "http://javax.xml.XMLConstants/feature/secure-processing");
/*     */       }
/* 302 */       setProperty("http://apache.org/xml/properties/security-manager", value ? new SecurityManager() : null);
/* 303 */       return;
/*     */     }
/* 305 */     this.fConfigUpdated = true;
/* 306 */     this.fEntityManager.setFeature(featureId, value);
/* 307 */     this.fErrorReporter.setFeature(featureId, value);
/* 308 */     this.fSchemaValidator.setFeature(featureId, value);
/* 309 */     if (!this.fInitFeatures.containsKey(featureId)) {
/* 310 */       boolean current = super.getFeature(featureId);
/* 311 */       this.fInitFeatures.put(featureId, current ? Boolean.TRUE : Boolean.FALSE);
/*     */     }
/* 313 */     super.setFeature(featureId, value);
/*     */   }
/*     */ 
/*     */   public PropertyState getPropertyState(String propertyId)
/*     */     throws XMLConfigurationException
/*     */   {
/* 330 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/* 331 */       return PropertyState.is(getLocale());
/*     */     }
/* 333 */     Object component = this.fComponents.get(propertyId);
/* 334 */     if (component != null) {
/* 335 */       return PropertyState.is(component);
/*     */     }
/* 337 */     if (this.fComponents.containsKey(propertyId)) {
/* 338 */       return PropertyState.is(null);
/*     */     }
/* 340 */     return super.getPropertyState(propertyId);
/*     */   }
/*     */ 
/*     */   public void setProperty(String propertyId, Object value)
/*     */     throws XMLConfigurationException
/*     */   {
/* 352 */     if (("http://apache.org/xml/properties/internal/entity-manager".equals(propertyId)) || ("http://apache.org/xml/properties/internal/error-reporter".equals(propertyId)) || ("http://apache.org/xml/properties/internal/namespace-context".equals(propertyId)) || ("http://apache.org/xml/properties/internal/validator/schema".equals(propertyId)) || ("http://apache.org/xml/properties/internal/symbol-table".equals(propertyId)) || ("http://apache.org/xml/properties/internal/validation-manager".equals(propertyId)) || ("http://apache.org/xml/properties/internal/grammar-pool".equals(propertyId)))
/*     */     {
/* 356 */       throw new XMLConfigurationException(Status.NOT_SUPPORTED, propertyId);
/*     */     }
/* 358 */     this.fConfigUpdated = true;
/* 359 */     this.fEntityManager.setProperty(propertyId, value);
/* 360 */     this.fErrorReporter.setProperty(propertyId, value);
/* 361 */     this.fSchemaValidator.setProperty(propertyId, value);
/* 362 */     if (("http://apache.org/xml/properties/internal/entity-resolver".equals(propertyId)) || ("http://apache.org/xml/properties/internal/error-handler".equals(propertyId)) || ("http://apache.org/xml/properties/security-manager".equals(propertyId)))
/*     */     {
/* 364 */       this.fComponents.put(propertyId, value);
/* 365 */       return;
/*     */     }
/* 367 */     if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
/* 368 */       setLocale((Locale)value);
/* 369 */       this.fComponents.put(propertyId, value);
/* 370 */       return;
/*     */     }
/* 372 */     if (!this.fInitProperties.containsKey(propertyId)) {
/* 373 */       this.fInitProperties.put(propertyId, super.getProperty(propertyId));
/*     */     }
/* 375 */     super.setProperty(propertyId, value);
/*     */   }
/*     */ 
/*     */   public void addRecognizedParamsAndSetDefaults(XMLComponent component, XSGrammarPoolContainer grammarContainer)
/*     */   {
/* 390 */     String[] recognizedFeatures = component.getRecognizedFeatures();
/* 391 */     addRecognizedFeatures(recognizedFeatures);
/*     */ 
/* 394 */     String[] recognizedProperties = component.getRecognizedProperties();
/* 395 */     addRecognizedProperties(recognizedProperties);
/*     */ 
/* 398 */     setFeatureDefaults(component, recognizedFeatures, grammarContainer);
/* 399 */     setPropertyDefaults(component, recognizedProperties);
/*     */   }
/*     */ 
/*     */   public void reset() throws XNIException
/*     */   {
/* 404 */     this.fNamespaceContext.reset();
/* 405 */     this.fValidationManager.reset();
/* 406 */     this.fEntityManager.reset(this);
/* 407 */     this.fErrorReporter.reset(this);
/* 408 */     this.fSchemaValidator.reset(this);
/*     */ 
/* 410 */     this.fConfigUpdated = false;
/*     */   }
/*     */ 
/*     */   void setErrorHandler(ErrorHandler errorHandler) {
/* 414 */     this.fErrorHandler = errorHandler;
/* 415 */     setProperty("http://apache.org/xml/properties/internal/error-handler", errorHandler != null ? new ErrorHandlerWrapper(errorHandler) : new ErrorHandlerWrapper(DraconianErrorHandler.getInstance()));
/*     */   }
/*     */ 
/*     */   ErrorHandler getErrorHandler()
/*     */   {
/* 420 */     return this.fErrorHandler;
/*     */   }
/*     */ 
/*     */   void setResourceResolver(LSResourceResolver resourceResolver) {
/* 424 */     this.fResourceResolver = resourceResolver;
/* 425 */     setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper(resourceResolver));
/*     */   }
/*     */ 
/*     */   LSResourceResolver getResourceResolver() {
/* 429 */     return this.fResourceResolver;
/*     */   }
/*     */ 
/*     */   void setLocale(Locale locale) {
/* 433 */     this.fLocale = locale;
/* 434 */     this.fErrorReporter.setLocale(locale);
/*     */   }
/*     */ 
/*     */   Locale getLocale() {
/* 438 */     return this.fLocale;
/*     */   }
/*     */ 
/*     */   void restoreInitialState()
/*     */   {
/* 443 */     this.fConfigUpdated = true;
/*     */ 
/* 446 */     this.fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
/* 447 */     this.fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
/*     */ 
/* 450 */     setLocale(null);
/* 451 */     this.fComponents.put("http://apache.org/xml/properties/locale", null);
/*     */ 
/* 454 */     this.fComponents.put("http://apache.org/xml/properties/security-manager", this.fInitSecurityManager);
/*     */ 
/* 457 */     setLocale(null);
/* 458 */     this.fComponents.put("http://apache.org/xml/properties/locale", null);
/*     */ 
/* 461 */     if (!this.fInitFeatures.isEmpty()) {
/* 462 */       Iterator iter = this.fInitFeatures.entrySet().iterator();
/* 463 */       while (iter.hasNext()) {
/* 464 */         Map.Entry entry = (Map.Entry)iter.next();
/* 465 */         String name = (String)entry.getKey();
/* 466 */         boolean value = ((Boolean)entry.getValue()).booleanValue();
/* 467 */         super.setFeature(name, value);
/*     */       }
/* 469 */       this.fInitFeatures.clear();
/*     */     }
/* 471 */     if (!this.fInitProperties.isEmpty()) {
/* 472 */       Iterator iter = this.fInitProperties.entrySet().iterator();
/* 473 */       while (iter.hasNext()) {
/* 474 */         Map.Entry entry = (Map.Entry)iter.next();
/* 475 */         String name = (String)entry.getKey();
/* 476 */         Object value = entry.getValue();
/* 477 */         super.setProperty(name, value);
/*     */       }
/* 479 */       this.fInitProperties.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setFeatureDefaults(XMLComponent component, String[] recognizedFeatures, XSGrammarPoolContainer grammarContainer)
/*     */   {
/* 486 */     if (recognizedFeatures != null)
/* 487 */       for (int i = 0; i < recognizedFeatures.length; i++) {
/* 488 */         String featureId = recognizedFeatures[i];
/* 489 */         Boolean state = grammarContainer.getFeature(featureId);
/* 490 */         if (state == null) {
/* 491 */           state = component.getFeatureDefault(featureId);
/*     */         }
/* 493 */         if (state != null)
/*     */         {
/* 495 */           if (!this.fFeatures.containsKey(featureId)) {
/* 496 */             this.fFeatures.put(featureId, state);
/*     */ 
/* 501 */             this.fConfigUpdated = true;
/*     */           }
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private void setPropertyDefaults(XMLComponent component, String[] recognizedProperties)
/*     */   {
/* 510 */     if (recognizedProperties != null)
/* 511 */       for (int i = 0; i < recognizedProperties.length; i++) {
/* 512 */         String propertyId = recognizedProperties[i];
/* 513 */         Object value = component.getPropertyDefault(propertyId);
/* 514 */         if (value != null)
/*     */         {
/* 516 */           if (!this.fProperties.containsKey(propertyId)) {
/* 517 */             this.fProperties.put(propertyId, value);
/*     */ 
/* 522 */             this.fConfigUpdated = true;
/*     */           }
/*     */         }
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaValidatorComponentManager
 * JD-Core Version:    0.6.2
 */