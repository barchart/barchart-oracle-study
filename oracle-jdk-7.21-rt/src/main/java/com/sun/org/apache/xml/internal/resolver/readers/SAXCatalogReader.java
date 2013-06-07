/*     */ package com.sun.org.apache.xml.internal.resolver.readers;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.resolver.Catalog;
/*     */ import com.sun.org.apache.xml.internal.resolver.CatalogException;
/*     */ import com.sun.org.apache.xml.internal.resolver.CatalogManager;
/*     */ import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Hashtable;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.AttributeList;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.ContentHandler;
/*     */ import org.xml.sax.DocumentHandler;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.Locator;
/*     */ import org.xml.sax.Parser;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class SAXCatalogReader
/*     */   implements CatalogReader, ContentHandler, DocumentHandler
/*     */ {
/*  84 */   protected SAXParserFactory parserFactory = null;
/*     */ 
/*  87 */   protected String parserClass = null;
/*     */ 
/*  96 */   protected Hashtable namespaceMap = new Hashtable();
/*     */ 
/*  99 */   private SAXCatalogParser saxParser = null;
/*     */ 
/* 104 */   private boolean abandonHope = false;
/*     */   private Catalog catalog;
/* 140 */   protected Debug debug = CatalogManager.getStaticManager().debug;
/*     */ 
/*     */   public void setParserFactory(SAXParserFactory parserFactory)
/*     */   {
/* 112 */     this.parserFactory = parserFactory;
/*     */   }
/*     */ 
/*     */   public void setParserClass(String parserClass)
/*     */   {
/* 118 */     this.parserClass = parserClass;
/*     */   }
/*     */ 
/*     */   public SAXParserFactory getParserFactory()
/*     */   {
/* 123 */     return this.parserFactory;
/*     */   }
/*     */ 
/*     */   public String getParserClass()
/*     */   {
/* 128 */     return this.parserClass;
/*     */   }
/*     */ 
/*     */   public SAXCatalogReader()
/*     */   {
/* 144 */     this.parserFactory = null;
/* 145 */     this.parserClass = null;
/*     */   }
/*     */ 
/*     */   public SAXCatalogReader(SAXParserFactory parserFactory)
/*     */   {
/* 150 */     this.parserFactory = parserFactory;
/*     */   }
/*     */ 
/*     */   public SAXCatalogReader(String parserClass)
/*     */   {
/* 155 */     this.parserClass = parserClass;
/*     */   }
/*     */ 
/*     */   public void setCatalogParser(String namespaceURI, String rootElement, String parserClass)
/*     */   {
/* 164 */     if (namespaceURI == null)
/* 165 */       this.namespaceMap.put(rootElement, parserClass);
/*     */     else
/* 167 */       this.namespaceMap.put("{" + namespaceURI + "}" + rootElement, parserClass);
/*     */   }
/*     */ 
/*     */   public String getCatalogParser(String namespaceURI, String rootElement)
/*     */   {
/* 176 */     if (namespaceURI == null) {
/* 177 */       return (String)this.namespaceMap.get(rootElement);
/*     */     }
/* 179 */     return (String)this.namespaceMap.get("{" + namespaceURI + "}" + rootElement);
/*     */   }
/*     */ 
/*     */   public void readCatalog(Catalog catalog, String fileUrl)
/*     */     throws MalformedURLException, IOException, CatalogException
/*     */   {
/* 196 */     URL url = null;
/*     */     try
/*     */     {
/* 199 */       url = new URL(fileUrl);
/*     */     } catch (MalformedURLException e) {
/* 201 */       url = new URL("file:///" + fileUrl);
/*     */     }
/*     */ 
/* 204 */     this.debug = catalog.getCatalogManager().debug;
/*     */     try
/*     */     {
/* 207 */       URLConnection urlCon = url.openConnection();
/* 208 */       readCatalog(catalog, urlCon.getInputStream());
/*     */     } catch (FileNotFoundException e) {
/* 210 */       catalog.getCatalogManager().debug.message(1, "Failed to load catalog, file not found", url.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readCatalog(Catalog catalog, InputStream is)
/*     */     throws IOException, CatalogException
/*     */   {
/* 229 */     if ((this.parserFactory == null) && (this.parserClass == null)) {
/* 230 */       this.debug.message(1, "Cannot read SAX catalog without a parser");
/* 231 */       throw new CatalogException(6);
/*     */     }
/*     */ 
/* 234 */     this.debug = catalog.getCatalogManager().debug;
/* 235 */     EntityResolver bResolver = catalog.getCatalogManager().getBootstrapResolver();
/*     */ 
/* 237 */     this.catalog = catalog;
/*     */     try
/*     */     {
/* 240 */       if (this.parserFactory != null) {
/* 241 */         SAXParser parser = this.parserFactory.newSAXParser();
/* 242 */         SAXParserHandler spHandler = new SAXParserHandler();
/* 243 */         spHandler.setContentHandler(this);
/* 244 */         if (bResolver != null) {
/* 245 */           spHandler.setEntityResolver(bResolver);
/*     */         }
/* 247 */         parser.parse(new InputSource(is), spHandler);
/*     */       } else {
/* 249 */         Parser parser = (Parser)Class.forName(this.parserClass).newInstance();
/* 250 */         parser.setDocumentHandler(this);
/* 251 */         if (bResolver != null) {
/* 252 */           parser.setEntityResolver(bResolver);
/*     */         }
/* 254 */         parser.parse(new InputSource(is));
/*     */       }
/*     */     } catch (ClassNotFoundException cnfe) {
/* 257 */       throw new CatalogException(6);
/*     */     } catch (IllegalAccessException iae) {
/* 259 */       throw new CatalogException(6);
/*     */     } catch (InstantiationException ie) {
/* 261 */       throw new CatalogException(6);
/*     */     } catch (ParserConfigurationException pce) {
/* 263 */       throw new CatalogException(5);
/*     */     } catch (SAXException se) {
/* 265 */       Exception e = se.getException();
/*     */ 
/* 267 */       UnknownHostException uhe = new UnknownHostException();
/* 268 */       FileNotFoundException fnfe = new FileNotFoundException();
/* 269 */       if (e != null) {
/* 270 */         if (e.getClass() == uhe.getClass()) {
/* 271 */           throw new CatalogException(7, e.toString());
/*     */         }
/* 273 */         if (e.getClass() == fnfe.getClass()) {
/* 274 */           throw new CatalogException(7, e.toString());
/*     */         }
/*     */       }
/*     */ 
/* 278 */       throw new CatalogException(se);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDocumentLocator(Locator locator)
/*     */   {
/* 287 */     if (this.saxParser != null)
/* 288 */       this.saxParser.setDocumentLocator(locator);
/*     */   }
/*     */ 
/*     */   public void startDocument()
/*     */     throws SAXException
/*     */   {
/* 294 */     this.saxParser = null;
/* 295 */     this.abandonHope = false;
/*     */   }
/*     */ 
/*     */   public void endDocument()
/*     */     throws SAXException
/*     */   {
/* 301 */     if (this.saxParser != null)
/* 302 */       this.saxParser.endDocument();
/*     */   }
/*     */ 
/*     */   public void startElement(String name, AttributeList atts)
/*     */     throws SAXException
/*     */   {
/* 316 */     if (this.abandonHope) {
/* 317 */       return;
/*     */     }
/*     */ 
/* 320 */     if (this.saxParser == null) {
/* 321 */       String prefix = "";
/* 322 */       if (name.indexOf(':') > 0) {
/* 323 */         prefix = name.substring(0, name.indexOf(':'));
/*     */       }
/*     */ 
/* 326 */       String localName = name;
/* 327 */       if (localName.indexOf(':') > 0) {
/* 328 */         localName = localName.substring(localName.indexOf(':') + 1);
/*     */       }
/*     */ 
/* 331 */       String namespaceURI = null;
/* 332 */       if (prefix.equals(""))
/* 333 */         namespaceURI = atts.getValue("xmlns");
/*     */       else {
/* 335 */         namespaceURI = atts.getValue("xmlns:" + prefix);
/*     */       }
/*     */ 
/* 338 */       String saxParserClass = getCatalogParser(namespaceURI, localName);
/*     */ 
/* 341 */       if (saxParserClass == null) {
/* 342 */         this.abandonHope = true;
/* 343 */         if (namespaceURI == null)
/* 344 */           this.debug.message(2, "No Catalog parser for " + name);
/*     */         else {
/* 346 */           this.debug.message(2, "No Catalog parser for {" + namespaceURI + "}" + name);
/*     */         }
/*     */ 
/* 350 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 354 */         this.saxParser = ((SAXCatalogParser)Class.forName(saxParserClass).newInstance());
/*     */ 
/* 357 */         this.saxParser.setCatalog(this.catalog);
/* 358 */         this.saxParser.startDocument();
/* 359 */         this.saxParser.startElement(name, atts);
/*     */       } catch (ClassNotFoundException cnfe) {
/* 361 */         this.saxParser = null;
/* 362 */         this.abandonHope = true;
/* 363 */         this.debug.message(2, cnfe.toString());
/*     */       } catch (InstantiationException ie) {
/* 365 */         this.saxParser = null;
/* 366 */         this.abandonHope = true;
/* 367 */         this.debug.message(2, ie.toString());
/*     */       } catch (IllegalAccessException iae) {
/* 369 */         this.saxParser = null;
/* 370 */         this.abandonHope = true;
/* 371 */         this.debug.message(2, iae.toString());
/*     */       } catch (ClassCastException cce) {
/* 373 */         this.saxParser = null;
/* 374 */         this.abandonHope = true;
/* 375 */         this.debug.message(2, cce.toString());
/*     */       }
/*     */     } else {
/* 378 */       this.saxParser.startElement(name, atts);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
/*     */     throws SAXException
/*     */   {
/* 394 */     if (this.abandonHope) {
/* 395 */       return;
/*     */     }
/*     */ 
/* 398 */     if (this.saxParser == null) {
/* 399 */       String saxParserClass = getCatalogParser(namespaceURI, localName);
/*     */ 
/* 402 */       if (saxParserClass == null) {
/* 403 */         this.abandonHope = true;
/* 404 */         if (namespaceURI == null)
/* 405 */           this.debug.message(2, "No Catalog parser for " + localName);
/*     */         else {
/* 407 */           this.debug.message(2, "No Catalog parser for {" + namespaceURI + "}" + localName);
/*     */         }
/*     */ 
/* 411 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 415 */         this.saxParser = ((SAXCatalogParser)Class.forName(saxParserClass).newInstance());
/*     */ 
/* 418 */         this.saxParser.setCatalog(this.catalog);
/* 419 */         this.saxParser.startDocument();
/* 420 */         this.saxParser.startElement(namespaceURI, localName, qName, atts);
/*     */       } catch (ClassNotFoundException cnfe) {
/* 422 */         this.saxParser = null;
/* 423 */         this.abandonHope = true;
/* 424 */         this.debug.message(2, cnfe.toString());
/*     */       } catch (InstantiationException ie) {
/* 426 */         this.saxParser = null;
/* 427 */         this.abandonHope = true;
/* 428 */         this.debug.message(2, ie.toString());
/*     */       } catch (IllegalAccessException iae) {
/* 430 */         this.saxParser = null;
/* 431 */         this.abandonHope = true;
/* 432 */         this.debug.message(2, iae.toString());
/*     */       } catch (ClassCastException cce) {
/* 434 */         this.saxParser = null;
/* 435 */         this.abandonHope = true;
/* 436 */         this.debug.message(2, cce.toString());
/*     */       }
/*     */     } else {
/* 439 */       this.saxParser.startElement(namespaceURI, localName, qName, atts);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endElement(String name) throws SAXException
/*     */   {
/* 445 */     if (this.saxParser != null)
/* 446 */       this.saxParser.endElement(name);
/*     */   }
/*     */ 
/*     */   public void endElement(String namespaceURI, String localName, String qName)
/*     */     throws SAXException
/*     */   {
/* 454 */     if (this.saxParser != null)
/* 455 */       this.saxParser.endElement(namespaceURI, localName, qName);
/*     */   }
/*     */ 
/*     */   public void characters(char[] ch, int start, int length)
/*     */     throws SAXException
/*     */   {
/* 462 */     if (this.saxParser != null)
/* 463 */       this.saxParser.characters(ch, start, length);
/*     */   }
/*     */ 
/*     */   public void ignorableWhitespace(char[] ch, int start, int length)
/*     */     throws SAXException
/*     */   {
/* 470 */     if (this.saxParser != null)
/* 471 */       this.saxParser.ignorableWhitespace(ch, start, length);
/*     */   }
/*     */ 
/*     */   public void processingInstruction(String target, String data)
/*     */     throws SAXException
/*     */   {
/* 478 */     if (this.saxParser != null)
/* 479 */       this.saxParser.processingInstruction(target, data);
/*     */   }
/*     */ 
/*     */   public void startPrefixMapping(String prefix, String uri)
/*     */     throws SAXException
/*     */   {
/* 486 */     if (this.saxParser != null)
/* 487 */       this.saxParser.startPrefixMapping(prefix, uri);
/*     */   }
/*     */ 
/*     */   public void endPrefixMapping(String prefix)
/*     */     throws SAXException
/*     */   {
/* 494 */     if (this.saxParser != null)
/* 495 */       this.saxParser.endPrefixMapping(prefix);
/*     */   }
/*     */ 
/*     */   public void skippedEntity(String name)
/*     */     throws SAXException
/*     */   {
/* 502 */     if (this.saxParser != null)
/* 503 */       this.saxParser.skippedEntity(name);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader
 * JD-Core Version:    0.6.2
 */