/*     */ package com.sun.org.apache.xml.internal.resolver.readers;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.resolver.Catalog;
/*     */ import com.sun.org.apache.xml.internal.resolver.CatalogException;
/*     */ import com.sun.org.apache.xml.internal.resolver.CatalogManager;
/*     */ import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
/*     */ import com.sun.org.apache.xml.internal.resolver.helpers.Namespaces;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Hashtable;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class DOMCatalogReader
/*     */   implements CatalogReader
/*     */ {
/*  83 */   protected Hashtable namespaceMap = new Hashtable();
/*     */ 
/*     */   public void setCatalogParser(String namespaceURI, String rootElement, String parserClass)
/*     */   {
/*  99 */     if (namespaceURI == null)
/* 100 */       this.namespaceMap.put(rootElement, parserClass);
/*     */     else
/* 102 */       this.namespaceMap.put("{" + namespaceURI + "}" + rootElement, parserClass);
/*     */   }
/*     */ 
/*     */   public String getCatalogParser(String namespaceURI, String rootElement)
/*     */   {
/* 118 */     if (namespaceURI == null) {
/* 119 */       return (String)this.namespaceMap.get(rootElement);
/*     */     }
/* 121 */     return (String)this.namespaceMap.get("{" + namespaceURI + "}" + rootElement);
/*     */   }
/*     */ 
/*     */   public void readCatalog(Catalog catalog, InputStream is)
/*     */     throws IOException, CatalogException
/*     */   {
/* 159 */     DocumentBuilderFactory factory = null;
/* 160 */     DocumentBuilder builder = null;
/*     */ 
/* 162 */     factory = DocumentBuilderFactory.newInstance();
/* 163 */     factory.setNamespaceAware(false);
/* 164 */     factory.setValidating(false);
/*     */     try {
/* 166 */       builder = factory.newDocumentBuilder();
/*     */     } catch (ParserConfigurationException pce) {
/* 168 */       throw new CatalogException(6);
/*     */     }
/*     */ 
/* 171 */     Document doc = null;
/*     */     try
/*     */     {
/* 174 */       doc = builder.parse(is);
/*     */     } catch (SAXException se) {
/* 176 */       throw new CatalogException(5);
/*     */     }
/*     */ 
/* 179 */     Element root = doc.getDocumentElement();
/*     */ 
/* 181 */     String namespaceURI = Namespaces.getNamespaceURI(root);
/* 182 */     String localName = Namespaces.getLocalName(root);
/*     */ 
/* 184 */     String domParserClass = getCatalogParser(namespaceURI, localName);
/*     */ 
/* 187 */     if (domParserClass == null) {
/* 188 */       if (namespaceURI == null) {
/* 189 */         catalog.getCatalogManager().debug.message(1, "No Catalog parser for " + localName);
/*     */       }
/*     */       else {
/* 192 */         catalog.getCatalogManager().debug.message(1, "No Catalog parser for {" + namespaceURI + "}" + localName);
/*     */       }
/*     */ 
/* 196 */       return;
/*     */     }
/*     */ 
/* 199 */     DOMCatalogParser domParser = null;
/*     */     try
/*     */     {
/* 202 */       domParser = (DOMCatalogParser)Class.forName(domParserClass).newInstance();
/*     */     } catch (ClassNotFoundException cnfe) {
/* 204 */       catalog.getCatalogManager().debug.message(1, "Cannot load XML Catalog Parser class", domParserClass);
/* 205 */       throw new CatalogException(6);
/*     */     } catch (InstantiationException ie) {
/* 207 */       catalog.getCatalogManager().debug.message(1, "Cannot instantiate XML Catalog Parser class", domParserClass);
/* 208 */       throw new CatalogException(6);
/*     */     } catch (IllegalAccessException iae) {
/* 210 */       catalog.getCatalogManager().debug.message(1, "Cannot access XML Catalog Parser class", domParserClass);
/* 211 */       throw new CatalogException(6);
/*     */     } catch (ClassCastException cce) {
/* 213 */       catalog.getCatalogManager().debug.message(1, "Cannot cast XML Catalog Parser class", domParserClass);
/* 214 */       throw new CatalogException(6);
/*     */     }
/*     */ 
/* 217 */     Node node = root.getFirstChild();
/* 218 */     while (node != null) {
/* 219 */       domParser.parseCatalogEntry(catalog, node);
/* 220 */       node = node.getNextSibling();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readCatalog(Catalog catalog, String fileUrl)
/*     */     throws MalformedURLException, IOException, CatalogException
/*     */   {
/* 242 */     URL url = new URL(fileUrl);
/* 243 */     URLConnection urlCon = url.openConnection();
/* 244 */     readCatalog(catalog, urlCon.getInputStream());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.resolver.readers.DOMCatalogReader
 * JD-Core Version:    0.6.2
 */