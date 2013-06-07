/*     */ package com.sun.org.apache.xerces.internal.jaxp;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
/*     */ import java.util.Hashtable;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import javax.xml.validation.Schema;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ public class SAXParserFactoryImpl extends SAXParserFactory
/*     */ {
/*     */   private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
/*     */   private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
/*     */   private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
/*     */   private Hashtable features;
/*     */   private Schema grammar;
/*     */   private boolean isXIncludeAware;
/*  69 */   private boolean fSecureProcess = true;
/*     */ 
/*     */   public SAXParser newSAXParser()
/*     */     throws ParserConfigurationException
/*     */   {
/*     */     SAXParser saxParserImpl;
/*     */     try
/*     */     {
/*  81 */       saxParserImpl = new SAXParserImpl(this, this.features, this.fSecureProcess);
/*     */     }
/*     */     catch (SAXException se) {
/*  84 */       throw new ParserConfigurationException(se.getMessage());
/*     */     }
/*  86 */     return saxParserImpl;
/*     */   }
/*     */ 
/*     */   private SAXParserImpl newSAXParserImpl()
/*     */     throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/*     */     SAXParserImpl saxParserImpl;
/*     */     try
/*     */     {
/*  98 */       saxParserImpl = new SAXParserImpl(this, this.features);
/*     */     } catch (SAXNotSupportedException e) {
/* 100 */       throw e;
/*     */     } catch (SAXNotRecognizedException e) {
/* 102 */       throw e;
/*     */     } catch (SAXException se) {
/* 104 */       throw new ParserConfigurationException(se.getMessage());
/*     */     }
/* 106 */     return saxParserImpl;
/*     */   }
/*     */ 
/*     */   public void setFeature(String name, boolean value)
/*     */     throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/* 116 */     if (name == null) {
/* 117 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 120 */     if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/* 121 */       if ((System.getSecurityManager() != null) && (!value)) {
/* 122 */         throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
/*     */       }
/*     */ 
/* 126 */       this.fSecureProcess = value;
/* 127 */       return;
/*     */     }
/*     */ 
/* 132 */     putInFeatures(name, value);
/*     */     try
/*     */     {
/* 135 */       newSAXParserImpl();
/*     */     } catch (SAXNotSupportedException e) {
/* 137 */       this.features.remove(name);
/* 138 */       throw e;
/*     */     } catch (SAXNotRecognizedException e) {
/* 140 */       this.features.remove(name);
/* 141 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getFeature(String name)
/*     */     throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
/*     */   {
/* 152 */     if (name == null) {
/* 153 */       throw new NullPointerException();
/*     */     }
/* 155 */     if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/* 156 */       return this.fSecureProcess;
/*     */     }
/*     */ 
/* 160 */     return newSAXParserImpl().getXMLReader().getFeature(name);
/*     */   }
/*     */ 
/*     */   public Schema getSchema() {
/* 164 */     return this.grammar;
/*     */   }
/*     */ 
/*     */   public void setSchema(Schema grammar) {
/* 168 */     this.grammar = grammar;
/*     */   }
/*     */ 
/*     */   public boolean isXIncludeAware() {
/* 172 */     return getFromFeatures("http://apache.org/xml/features/xinclude");
/*     */   }
/*     */ 
/*     */   public void setXIncludeAware(boolean state) {
/* 176 */     putInFeatures("http://apache.org/xml/features/xinclude", state);
/*     */   }
/*     */ 
/*     */   public void setValidating(boolean validating)
/*     */   {
/* 181 */     putInFeatures("http://xml.org/sax/features/validation", validating);
/*     */   }
/*     */ 
/*     */   public boolean isValidating() {
/* 185 */     return getFromFeatures("http://xml.org/sax/features/validation");
/*     */   }
/*     */ 
/*     */   private void putInFeatures(String name, boolean value) {
/* 189 */     if (this.features == null) {
/* 190 */       this.features = new Hashtable();
/*     */     }
/* 192 */     this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
/*     */   }
/*     */ 
/*     */   private boolean getFromFeatures(String name) {
/* 196 */     if (this.features == null) {
/* 197 */       return false;
/*     */     }
/*     */ 
/* 200 */     Object value = this.features.get(name);
/* 201 */     return value == null ? false : Boolean.valueOf(value.toString()).booleanValue();
/*     */   }
/*     */ 
/*     */   public boolean isNamespaceAware()
/*     */   {
/* 206 */     return getFromFeatures("http://xml.org/sax/features/namespaces");
/*     */   }
/*     */ 
/*     */   public void setNamespaceAware(boolean awareness) {
/* 210 */     putInFeatures("http://xml.org/sax/features/namespaces", awareness);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl
 * JD-Core Version:    0.6.2
 */