/*     */ package com.sun.org.apache.xpath.internal.jaxp;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.res.XSLMessages;
/*     */ import javax.xml.xpath.XPath;
/*     */ import javax.xml.xpath.XPathFactory;
/*     */ import javax.xml.xpath.XPathFactoryConfigurationException;
/*     */ import javax.xml.xpath.XPathFunctionResolver;
/*     */ import javax.xml.xpath.XPathVariableResolver;
/*     */ 
/*     */ public class XPathFactoryImpl extends XPathFactory
/*     */ {
/*     */   private static final String CLASS_NAME = "XPathFactoryImpl";
/*  50 */   private XPathFunctionResolver xPathFunctionResolver = null;
/*     */ 
/*  55 */   private XPathVariableResolver xPathVariableResolver = null;
/*     */ 
/*  60 */   private boolean _isNotSecureProcessing = true;
/*     */ 
/*  64 */   private boolean _isSecureMode = false;
/*     */ 
/*  69 */   private boolean _useServicesMechanism = true;
/*     */ 
/*     */   public XPathFactoryImpl() {
/*  72 */     this(true);
/*     */   }
/*     */ 
/*     */   public static XPathFactory newXPathFactoryNoServiceLoader() {
/*  76 */     return new XPathFactoryImpl(false);
/*     */   }
/*     */ 
/*     */   public XPathFactoryImpl(boolean useServicesMechanism) {
/*  80 */     if (System.getSecurityManager() != null) {
/*  81 */       this._isSecureMode = true;
/*  82 */       this._isNotSecureProcessing = false;
/*     */     }
/*  84 */     this._useServicesMechanism = useServicesMechanism;
/*     */   }
/*     */ 
/*     */   public boolean isObjectModelSupported(String objectModel)
/*     */   {
/* 101 */     if (objectModel == null) {
/* 102 */       String fmsg = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_NULL", new Object[] { getClass().getName() });
/*     */ 
/* 106 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 109 */     if (objectModel.length() == 0) {
/* 110 */       String fmsg = XSLMessages.createXPATHMessage("ER_OBJECT_MODEL_EMPTY", new Object[] { getClass().getName() });
/*     */ 
/* 113 */       throw new IllegalArgumentException(fmsg);
/*     */     }
/*     */ 
/* 117 */     if (objectModel.equals("http://java.sun.com/jaxp/xpath/dom")) {
/* 118 */       return true;
/*     */     }
/*     */ 
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   public XPath newXPath()
/*     */   {
/* 132 */     return new XPathImpl(this.xPathVariableResolver, this.xPathFunctionResolver, !this._isNotSecureProcessing, this._useServicesMechanism);
/*     */   }
/*     */ 
/*     */   public void setFeature(String name, boolean value)
/*     */     throws XPathFactoryConfigurationException
/*     */   {
/* 167 */     if (name == null) {
/* 168 */       String fmsg = XSLMessages.createXPATHMessage("ER_FEATURE_NAME_NULL", new Object[] { "XPathFactoryImpl", new Boolean(value) });
/*     */ 
/* 171 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 175 */     if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/* 176 */       if ((this._isSecureMode) && (!value)) {
/* 177 */         String fmsg = XSLMessages.createXPATHMessage("ER_SECUREPROCESSING_FEATURE", new Object[] { name, "XPathFactoryImpl", new Boolean(value) });
/*     */ 
/* 180 */         throw new XPathFactoryConfigurationException(fmsg);
/*     */       }
/*     */ 
/* 183 */       this._isNotSecureProcessing = (!value);
/*     */ 
/* 186 */       return;
/*     */     }
/* 188 */     if (name.equals("http://www.oracle.com/feature/use-service-mechanism"))
/*     */     {
/* 190 */       if (!this._isSecureMode)
/* 191 */         this._useServicesMechanism = value;
/* 192 */       return;
/*     */     }
/*     */ 
/* 196 */     String fmsg = XSLMessages.createXPATHMessage("ER_FEATURE_UNKNOWN", new Object[] { name, "XPathFactoryImpl", new Boolean(value) });
/*     */ 
/* 199 */     throw new XPathFactoryConfigurationException(fmsg);
/*     */   }
/*     */ 
/*     */   public boolean getFeature(String name)
/*     */     throws XPathFactoryConfigurationException
/*     */   {
/* 229 */     if (name == null) {
/* 230 */       String fmsg = XSLMessages.createXPATHMessage("ER_GETTING_NULL_FEATURE", new Object[] { "XPathFactoryImpl" });
/*     */ 
/* 233 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 237 */     if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/* 238 */       return !this._isNotSecureProcessing;
/*     */     }
/* 240 */     if (name.equals("http://www.oracle.com/feature/use-service-mechanism")) {
/* 241 */       return this._useServicesMechanism;
/*     */     }
/*     */ 
/* 244 */     String fmsg = XSLMessages.createXPATHMessage("ER_GETTING_UNKNOWN_FEATURE", new Object[] { name, "XPathFactoryImpl" });
/*     */ 
/* 248 */     throw new XPathFactoryConfigurationException(fmsg);
/*     */   }
/*     */ 
/*     */   public void setXPathFunctionResolver(XPathFunctionResolver resolver)
/*     */   {
/* 268 */     if (resolver == null) {
/* 269 */       String fmsg = XSLMessages.createXPATHMessage("ER_NULL_XPATH_FUNCTION_RESOLVER", new Object[] { "XPathFactoryImpl" });
/*     */ 
/* 272 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 275 */     this.xPathFunctionResolver = resolver;
/*     */   }
/*     */ 
/*     */   public void setXPathVariableResolver(XPathVariableResolver resolver)
/*     */   {
/* 294 */     if (resolver == null) {
/* 295 */       String fmsg = XSLMessages.createXPATHMessage("ER_NULL_XPATH_VARIABLE_RESOLVER", new Object[] { "XPathFactoryImpl" });
/*     */ 
/* 298 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 301 */     this.xPathVariableResolver = resolver;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl
 * JD-Core Version:    0.6.2
 */