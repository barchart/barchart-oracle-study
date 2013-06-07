/*     */ package com.sun.org.apache.xerces.internal.parsers;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*     */ import java.io.IOException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ 
/*     */ public abstract class XMLParser
/*     */ {
/*     */   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*     */   protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
/*  69 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-handler" };
/*     */   protected XMLParserConfiguration fConfiguration;
/*     */ 
/*     */   public boolean getFeature(String featureId)
/*     */     throws SAXNotSupportedException, SAXNotRecognizedException
/*     */   {
/*  90 */     return this.fConfiguration.getFeature(featureId);
/*     */   }
/*     */ 
/*     */   protected XMLParser(XMLParserConfiguration config)
/*     */   {
/* 100 */     this.fConfiguration = config;
/*     */ 
/* 103 */     this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
/*     */   }
/*     */ 
/*     */   public void parse(XMLInputSource inputSource)
/*     */     throws XNIException, IOException
/*     */   {
/* 122 */     reset();
/* 123 */     this.fConfiguration.parse(inputSource);
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */     throws XNIException
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.XMLParser
 * JD-Core Version:    0.6.2
 */