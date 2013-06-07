/*     */ package com.sun.org.apache.xalan.internal.xsltc.trax;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import javax.xml.stream.XMLEventReader;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.helpers.XMLReaderFactory;
/*     */ 
/*     */ public final class Util
/*     */ {
/*     */   public static String baseName(String name)
/*     */   {
/*  64 */     return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.baseName(name);
/*     */   }
/*     */ 
/*     */   public static String noExtName(String name) {
/*  68 */     return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.noExtName(name);
/*     */   }
/*     */ 
/*     */   public static String toJavaName(String name) {
/*  72 */     return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util.toJavaName(name);
/*     */   }
/*     */ 
/*     */   public static InputSource getInputSource(XSLTC xsltc, Source source)
/*     */     throws TransformerConfigurationException
/*     */   {
/*  84 */     InputSource input = null;
/*     */ 
/*  86 */     String systemId = source.getSystemId();
/*     */     try
/*     */     {
/*  90 */       if ((source instanceof SAXSource)) {
/*  91 */         SAXSource sax = (SAXSource)source;
/*  92 */         input = sax.getInputSource();
/*     */         try
/*     */         {
/*  95 */           XMLReader reader = sax.getXMLReader();
/*     */ 
/* 105 */           if (reader == null) {
/*     */             try {
/* 107 */               reader = XMLReaderFactory.createXMLReader();
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/*     */               try
/*     */               {
/* 113 */                 SAXParserFactory parserFactory = FactoryImpl.getSAXFactory(xsltc.useServicesMechnism());
/* 114 */                 parserFactory.setNamespaceAware(true);
/*     */ 
/* 116 */                 if (xsltc.isSecureProcessing()) {
/*     */                   try {
/* 118 */                     parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
/*     */                   }
/*     */                   catch (SAXException se)
/*     */                   {
/*     */                   }
/*     */                 }
/* 124 */                 reader = parserFactory.newSAXParser().getXMLReader();
/*     */               }
/*     */               catch (ParserConfigurationException pce)
/*     */               {
/* 129 */                 throw new TransformerConfigurationException("ParserConfigurationException", pce);
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 134 */           reader.setFeature("http://xml.org/sax/features/namespaces", true);
/*     */ 
/* 136 */           reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
/*     */ 
/* 139 */           xsltc.setXMLReader(reader);
/*     */         } catch (SAXNotRecognizedException snre) {
/* 141 */           throw new TransformerConfigurationException("SAXNotRecognizedException ", snre);
/*     */         }
/*     */         catch (SAXNotSupportedException snse) {
/* 144 */           throw new TransformerConfigurationException("SAXNotSupportedException ", snse);
/*     */         }
/*     */         catch (SAXException se) {
/* 147 */           throw new TransformerConfigurationException("SAXException ", se);
/*     */         }
/*     */ 
/*     */       }
/* 153 */       else if ((source instanceof DOMSource)) {
/* 154 */         DOMSource domsrc = (DOMSource)source;
/* 155 */         Document dom = (Document)domsrc.getNode();
/* 156 */         DOM2SAX dom2sax = new DOM2SAX(dom);
/* 157 */         xsltc.setXMLReader(dom2sax);
/*     */ 
/* 160 */         input = SAXSource.sourceToInputSource(source);
/* 161 */         if (input == null) {
/* 162 */           input = new InputSource(domsrc.getSystemId());
/*     */         }
/*     */ 
/*     */       }
/* 167 */       else if ((source instanceof StAXSource)) {
/* 168 */         StAXSource staxSource = (StAXSource)source;
/* 169 */         StAXEvent2SAX staxevent2sax = null;
/* 170 */         StAXStream2SAX staxStream2SAX = null;
/* 171 */         if (staxSource.getXMLEventReader() != null) {
/* 172 */           XMLEventReader xmlEventReader = staxSource.getXMLEventReader();
/* 173 */           staxevent2sax = new StAXEvent2SAX(xmlEventReader);
/* 174 */           xsltc.setXMLReader(staxevent2sax);
/* 175 */         } else if (staxSource.getXMLStreamReader() != null) {
/* 176 */           XMLStreamReader xmlStreamReader = staxSource.getXMLStreamReader();
/* 177 */           staxStream2SAX = new StAXStream2SAX(xmlStreamReader);
/* 178 */           xsltc.setXMLReader(staxStream2SAX);
/*     */         }
/*     */ 
/* 182 */         input = SAXSource.sourceToInputSource(source);
/* 183 */         if (input == null) {
/* 184 */           input = new InputSource(staxSource.getSystemId());
/*     */         }
/*     */ 
/*     */       }
/* 189 */       else if ((source instanceof StreamSource)) {
/* 190 */         StreamSource stream = (StreamSource)source;
/* 191 */         InputStream istream = stream.getInputStream();
/* 192 */         Reader reader = stream.getReader();
/* 193 */         xsltc.setXMLReader(null);
/*     */ 
/* 196 */         if (istream != null) {
/* 197 */           input = new InputSource(istream);
/*     */         }
/* 199 */         else if (reader != null) {
/* 200 */           input = new InputSource(reader);
/*     */         }
/*     */         else
/* 203 */           input = new InputSource(systemId);
/*     */       }
/*     */       else
/*     */       {
/* 207 */         ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_SOURCE_ERR");
/* 208 */         throw new TransformerConfigurationException(err.toString());
/*     */       }
/* 210 */       input.setSystemId(systemId);
/*     */     }
/*     */     catch (NullPointerException e) {
/* 213 */       ErrorMsg err = new ErrorMsg("JAXP_NO_SOURCE_ERR", "TransformerFactory.newTemplates()");
/*     */ 
/* 215 */       throw new TransformerConfigurationException(err.toString());
/*     */     }
/*     */     catch (SecurityException e) {
/* 218 */       ErrorMsg err = new ErrorMsg("FILE_ACCESS_ERR", systemId);
/* 219 */       throw new TransformerConfigurationException(err.toString());
/*     */     }
/* 221 */     return input;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.trax.Util
 * JD-Core Version:    0.6.2
 */