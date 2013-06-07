/*     */ package com.sun.org.apache.xerces.internal.jaxp.validation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.TransformerFactoryConfigurationError;
/*     */ import javax.xml.transform.sax.SAXResult;
/*     */ import javax.xml.transform.sax.SAXTransformerFactory;
/*     */ import javax.xml.transform.sax.TransformerHandler;
/*     */ import javax.xml.transform.stax.StAXResult;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public final class StAXValidatorHelper
/*     */   implements ValidatorHelper
/*     */ {
/*     */   private static final String DEFAULT_TRANSFORMER_IMPL = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
/*     */   private XMLSchemaValidatorComponentManager fComponentManager;
/*  57 */   private Transformer identityTransformer1 = null;
/*  58 */   private TransformerHandler identityTransformer2 = null;
/*  59 */   private ValidatorHandlerImpl handler = null;
/*     */ 
/*     */   public StAXValidatorHelper(XMLSchemaValidatorComponentManager componentManager)
/*     */   {
/*  63 */     this.fComponentManager = componentManager;
/*     */   }
/*     */ 
/*     */   public void validate(Source source, Result result)
/*     */     throws SAXException, IOException
/*     */   {
/*  69 */     if ((result == null) || ((result instanceof StAXResult)))
/*     */     {
/*  71 */       if (this.identityTransformer1 == null) {
/*     */         try {
/*  73 */           SAXTransformerFactory tf = this.fComponentManager.getFeature("http://www.oracle.com/feature/use-service-mechanism") ? (SAXTransformerFactory)SAXTransformerFactory.newInstance() : (SAXTransformerFactory)TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", StAXValidatorHelper.class.getClassLoader());
/*     */ 
/*  76 */           this.identityTransformer1 = tf.newTransformer();
/*  77 */           this.identityTransformer2 = tf.newTransformerHandler();
/*     */         }
/*     */         catch (TransformerConfigurationException e) {
/*  80 */           throw new TransformerFactoryConfigurationError(e);
/*     */         }
/*     */       }
/*     */ 
/*  84 */       this.handler = new ValidatorHandlerImpl(this.fComponentManager);
/*  85 */       if (result != null) {
/*  86 */         this.handler.setContentHandler(this.identityTransformer2);
/*  87 */         this.identityTransformer2.setResult(result);
/*     */       }
/*     */       try
/*     */       {
/*  91 */         this.identityTransformer1.transform(source, new SAXResult(this.handler));
/*     */       } catch (TransformerException e) {
/*  93 */         if ((e.getException() instanceof SAXException))
/*  94 */           throw ((SAXException)e.getException());
/*  95 */         throw new SAXException(e);
/*     */       } finally {
/*  97 */         this.handler.setContentHandler(null);
/*     */       }
/*  99 */       return;
/*     */     }
/* 101 */     throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.validation.StAXValidatorHelper
 * JD-Core Version:    0.6.2
 */