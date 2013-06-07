/*     */ package org.xml.sax.helpers;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ public final class XMLReaderFactory
/*     */ {
/*     */   private static final String property = "org.xml.sax.driver";
/*  86 */   private static SecuritySupport ss = new SecuritySupport();
/*     */ 
/*  88 */   private static boolean _jarread = false;
/*     */ 
/*     */   public static XMLReader createXMLReader()
/*     */     throws SAXException
/*     */   {
/* 134 */     String className = null;
/* 135 */     ClassLoader cl = ss.getContextClassLoader();
/*     */     try
/*     */     {
/* 139 */       className = ss.getSystemProperty("org.xml.sax.driver");
/*     */     }
/*     */     catch (RuntimeException e)
/*     */     {
/*     */     }
/* 144 */     if ((className == null) && 
/* 145 */       (!_jarread)) {
/* 146 */       _jarread = true;
/* 147 */       String service = "META-INF/services/org.xml.sax.driver";
/*     */       try
/*     */       {
/*     */         InputStream in;
/* 152 */         if (cl != null) {
/* 153 */           InputStream in = ss.getResourceAsStream(cl, service);
/*     */ 
/* 156 */           if (in == null) {
/* 157 */             cl = null;
/* 158 */             in = ss.getResourceAsStream(cl, service);
/*     */           }
/*     */         }
/*     */         else {
/* 162 */           in = ss.getResourceAsStream(cl, service);
/*     */         }
/*     */ 
/* 165 */         if (in != null) {
/* 166 */           BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF8"));
/*     */ 
/* 168 */           className = reader.readLine();
/* 169 */           in.close();
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/* 177 */     if (className == null)
/*     */     {
/* 183 */       className = "com.sun.org.apache.xerces.internal.parsers.SAXParser";
/*     */     }
/*     */ 
/* 189 */     if (className != null) {
/* 190 */       return loadClass(cl, className);
/*     */     }
/*     */     try
/*     */     {
/* 194 */       return new ParserAdapter(ParserFactory.makeParser()); } catch (Exception e) {
/*     */     }
/* 196 */     throw new SAXException("Can't create default XMLReader; is system property org.xml.sax.driver set?");
/*     */   }
/*     */ 
/*     */   public static XMLReader createXMLReader(String className)
/*     */     throws SAXException
/*     */   {
/* 220 */     return loadClass(ss.getContextClassLoader(), className);
/*     */   }
/*     */ 
/*     */   private static XMLReader loadClass(ClassLoader loader, String className) throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 227 */       return (XMLReader)NewInstance.newInstance(loader, className);
/*     */     } catch (ClassNotFoundException e1) {
/* 229 */       throw new SAXException("SAX2 driver class " + className + " not found", e1);
/*     */     }
/*     */     catch (IllegalAccessException e2) {
/* 232 */       throw new SAXException("SAX2 driver class " + className + " found but cannot be loaded", e2);
/*     */     }
/*     */     catch (InstantiationException e3) {
/* 235 */       throw new SAXException("SAX2 driver class " + className + " loaded but cannot be instantiated (no empty public constructor?)", e3);
/*     */     }
/*     */     catch (ClassCastException e4)
/*     */     {
/* 239 */       throw new SAXException("SAX2 driver class " + className + " does not implement XMLReader", e4);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.xml.sax.helpers.XMLReaderFactory
 * JD-Core Version:    0.6.2
 */