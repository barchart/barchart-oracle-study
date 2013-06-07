/*     */ package com.sun.org.apache.xml.internal.utils;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*     */ import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
/*     */ import java.util.HashMap;
/*     */ import javax.xml.parsers.FactoryConfigurationError;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.XMLReaderFactory;
/*     */ 
/*     */ public class XMLReaderManager
/*     */ {
/*     */   private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
/*     */   private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
/*  47 */   private static final XMLReaderManager m_singletonManager = new XMLReaderManager();
/*     */   private static final String property = "org.xml.sax.driver";
/*     */   private static SAXParserFactory m_parserFactory;
/*     */   private ThreadLocal m_readers;
/*     */   private HashMap m_inUse;
/*  65 */   private boolean m_useServicesMechanism = true;
/*     */ 
/*     */   public static XMLReaderManager getInstance(boolean useServicesMechanism)
/*     */   {
/*  76 */     m_singletonManager.setServicesMechnism(useServicesMechanism);
/*  77 */     return m_singletonManager;
/*     */   }
/*     */ 
/*     */   public synchronized XMLReader getXMLReader()
/*     */     throws SAXException
/*     */   {
/*  89 */     if (this.m_readers == null)
/*     */     {
/*  92 */       this.m_readers = new ThreadLocal();
/*     */     }
/*     */ 
/*  95 */     if (this.m_inUse == null) {
/*  96 */       this.m_inUse = new HashMap();
/*     */     }
/*     */ 
/* 102 */     XMLReader reader = (XMLReader)this.m_readers.get();
/* 103 */     boolean threadHasReader = reader != null;
/* 104 */     String factory = SecuritySupport.getSystemProperty("org.xml.sax.driver");
/* 105 */     if ((threadHasReader) && (this.m_inUse.get(reader) != Boolean.TRUE) && ((factory == null) || (reader.getClass().getName().equals(factory))))
/*     */     {
/* 107 */       this.m_inUse.put(reader, Boolean.TRUE);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/*     */         try
/*     */         {
/* 115 */           reader = XMLReaderFactory.createXMLReader();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */           try
/*     */           {
/* 121 */             if (m_parserFactory == null) {
/* 122 */               m_parserFactory = FactoryImpl.getSAXFactory(this.m_useServicesMechanism);
/* 123 */               m_parserFactory.setNamespaceAware(true);
/*     */             }
/*     */ 
/* 126 */             reader = m_parserFactory.newSAXParser().getXMLReader();
/*     */           } catch (ParserConfigurationException pce) {
/* 128 */             throw pce;
/*     */           }
/*     */         }
/*     */         try {
/* 132 */           reader.setFeature("http://xml.org/sax/features/namespaces", true);
/* 133 */           reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
/*     */         }
/*     */         catch (SAXException se) {
/*     */         }
/*     */       }
/*     */       catch (ParserConfigurationException ex) {
/* 139 */         throw new SAXException(ex);
/*     */       } catch (FactoryConfigurationError ex1) {
/* 141 */         throw new SAXException(ex1.toString());
/*     */       }
/*     */       catch (NoSuchMethodError ex2)
/*     */       {
/*     */       }
/*     */       catch (AbstractMethodError ame) {
/*     */       }
/* 148 */       if (!threadHasReader) {
/* 149 */         this.m_readers.set(reader);
/* 150 */         this.m_inUse.put(reader, Boolean.TRUE);
/*     */       }
/*     */     }
/*     */ 
/* 154 */     return reader;
/*     */   }
/*     */ 
/*     */   public synchronized void releaseXMLReader(XMLReader reader)
/*     */   {
/* 166 */     if ((this.m_readers.get() == reader) && (reader != null))
/* 167 */       this.m_inUse.remove(reader);
/*     */   }
/*     */ 
/*     */   public boolean useServicesMechnism()
/*     */   {
/* 174 */     return this.m_useServicesMechanism;
/*     */   }
/*     */ 
/*     */   public void setServicesMechnism(boolean flag)
/*     */   {
/* 181 */     this.m_useServicesMechanism = flag;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.utils.XMLReaderManager
 * JD-Core Version:    0.6.2
 */