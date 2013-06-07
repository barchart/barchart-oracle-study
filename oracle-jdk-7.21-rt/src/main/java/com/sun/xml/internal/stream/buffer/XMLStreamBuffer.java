/*     */ package com.sun.xml.internal.stream.buffer;
/*     */ 
/*     */ import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
/*     */ import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
/*     */ import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferProcessor;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.stream.XMLStreamWriter;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.ContentHandler;
/*     */ import org.xml.sax.DTDHandler;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ import org.xml.sax.ext.LexicalHandler;
/*     */ 
/*     */ public abstract class XMLStreamBuffer
/*     */ {
/*  87 */   protected Map<String, String> _inscopeNamespaces = Collections.emptyMap();
/*     */   protected boolean _hasInternedStrings;
/*     */   protected FragmentedArray<byte[]> _structure;
/*     */   protected int _structurePtr;
/*     */   protected FragmentedArray<String[]> _structureStrings;
/*     */   protected int _structureStringsPtr;
/*     */   protected FragmentedArray<char[]> _contentCharactersBuffer;
/*     */   protected int _contentCharactersBufferPtr;
/*     */   protected FragmentedArray<Object[]> _contentObjects;
/*     */   protected int _contentObjectsPtr;
/*     */   protected int treeCount;
/*     */   protected String systemId;
/* 373 */   private static final TransformerFactory trnsformerFactory = TransformerFactory.newInstance();
/*     */ 
/*     */   public final boolean isCreated()
/*     */   {
/* 145 */     return ((byte[])this._structure.getArray())[0] != 144;
/*     */   }
/*     */ 
/*     */   public final boolean isFragment()
/*     */   {
/* 156 */     return (isCreated()) && ((((byte[])this._structure.getArray())[this._structurePtr] & 0xF0) != 16);
/*     */   }
/*     */ 
/*     */   public final boolean isElementFragment()
/*     */   {
/* 169 */     return (isCreated()) && ((((byte[])this._structure.getArray())[this._structurePtr] & 0xF0) == 32);
/*     */   }
/*     */ 
/*     */   public final boolean isForest()
/*     */   {
/* 178 */     return (isCreated()) && (this.treeCount > 1);
/*     */   }
/*     */ 
/*     */   public final String getSystemId()
/*     */   {
/* 186 */     return this.systemId;
/*     */   }
/*     */ 
/*     */   public final Map<String, String> getInscopeNamespaces()
/*     */   {
/* 208 */     return this._inscopeNamespaces;
/*     */   }
/*     */ 
/*     */   public final boolean hasInternedStrings()
/*     */   {
/* 231 */     return this._hasInternedStrings;
/*     */   }
/*     */ 
/*     */   public final StreamReaderBufferProcessor readAsXMLStreamReader()
/*     */     throws XMLStreamException
/*     */   {
/* 241 */     return new StreamReaderBufferProcessor(this);
/*     */   }
/*     */ 
/*     */   public final void writeToXMLStreamWriter(XMLStreamWriter writer, boolean writeAsFragment)
/*     */     throws XMLStreamException
/*     */   {
/* 259 */     StreamWriterBufferProcessor p = new StreamWriterBufferProcessor(this, writeAsFragment);
/* 260 */     p.process(writer);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final void writeToXMLStreamWriter(XMLStreamWriter writer)
/*     */     throws XMLStreamException
/*     */   {
/* 268 */     writeToXMLStreamWriter(writer, isFragment());
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final SAXBufferProcessor readAsXMLReader()
/*     */   {
/* 280 */     return new SAXBufferProcessor(this, isFragment());
/*     */   }
/*     */ 
/*     */   public final SAXBufferProcessor readAsXMLReader(boolean produceFragmentEvent)
/*     */   {
/* 293 */     return new SAXBufferProcessor(this, produceFragmentEvent);
/*     */   }
/*     */ 
/*     */   public final void writeTo(ContentHandler handler, boolean produceFragmentEvent)
/*     */     throws SAXException
/*     */   {
/* 314 */     SAXBufferProcessor p = readAsXMLReader(produceFragmentEvent);
/* 315 */     p.setContentHandler(handler);
/* 316 */     if ((p instanceof LexicalHandler)) {
/* 317 */       p.setLexicalHandler((LexicalHandler)handler);
/*     */     }
/* 319 */     if ((p instanceof DTDHandler)) {
/* 320 */       p.setDTDHandler((DTDHandler)handler);
/*     */     }
/* 322 */     if ((p instanceof ErrorHandler)) {
/* 323 */       p.setErrorHandler((ErrorHandler)handler);
/*     */     }
/* 325 */     p.process();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final void writeTo(ContentHandler handler)
/*     */     throws SAXException
/*     */   {
/* 333 */     writeTo(handler, isFragment());
/*     */   }
/*     */ 
/*     */   public final void writeTo(ContentHandler handler, ErrorHandler errorHandler, boolean produceFragmentEvent)
/*     */     throws SAXException
/*     */   {
/* 355 */     SAXBufferProcessor p = readAsXMLReader(produceFragmentEvent);
/* 356 */     p.setContentHandler(handler);
/* 357 */     if ((p instanceof LexicalHandler)) {
/* 358 */       p.setLexicalHandler((LexicalHandler)handler);
/*     */     }
/* 360 */     if ((p instanceof DTDHandler)) {
/* 361 */       p.setDTDHandler((DTDHandler)handler);
/*     */     }
/*     */ 
/* 364 */     p.setErrorHandler(errorHandler);
/*     */ 
/* 366 */     p.process();
/*     */   }
/*     */ 
/*     */   public final void writeTo(ContentHandler handler, ErrorHandler errorHandler) throws SAXException {
/* 370 */     writeTo(handler, errorHandler, isFragment());
/*     */   }
/*     */ 
/*     */   public final Node writeTo(Node n)
/*     */     throws XMLStreamBufferException
/*     */   {
/*     */     try
/*     */     {
/* 385 */       Transformer t = trnsformerFactory.newTransformer();
/* 386 */       t.transform(new XMLStreamBufferSource(this), new DOMResult(n));
/* 387 */       return n.getLastChild();
/*     */     } catch (TransformerException e) {
/* 389 */       throw new XMLStreamBufferException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static XMLStreamBuffer createNewBufferFromXMLStreamReader(XMLStreamReader reader)
/*     */     throws XMLStreamException
/*     */   {
/* 403 */     MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
/* 404 */     b.createFromXMLStreamReader(reader);
/* 405 */     return b;
/*     */   }
/*     */ 
/*     */   public static XMLStreamBuffer createNewBufferFromXMLReader(XMLReader reader, InputStream in)
/*     */     throws SAXException, IOException
/*     */   {
/* 419 */     MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
/* 420 */     b.createFromXMLReader(reader, in);
/* 421 */     return b;
/*     */   }
/*     */ 
/*     */   public static XMLStreamBuffer createNewBufferFromXMLReader(XMLReader reader, InputStream in, String systemId)
/*     */     throws SAXException, IOException
/*     */   {
/* 438 */     MutableXMLStreamBuffer b = new MutableXMLStreamBuffer();
/* 439 */     b.createFromXMLReader(reader, in, systemId);
/* 440 */     return b;
/*     */   }
/*     */ 
/*     */   protected final FragmentedArray<byte[]> getStructure() {
/* 444 */     return this._structure;
/*     */   }
/*     */ 
/*     */   protected final int getStructurePtr() {
/* 448 */     return this._structurePtr;
/*     */   }
/*     */ 
/*     */   protected final FragmentedArray<String[]> getStructureStrings() {
/* 452 */     return this._structureStrings;
/*     */   }
/*     */ 
/*     */   protected final int getStructureStringsPtr() {
/* 456 */     return this._structureStringsPtr;
/*     */   }
/*     */ 
/*     */   protected final FragmentedArray<char[]> getContentCharactersBuffer() {
/* 460 */     return this._contentCharactersBuffer;
/*     */   }
/*     */ 
/*     */   protected final int getContentCharactersBufferPtr() {
/* 464 */     return this._contentCharactersBufferPtr;
/*     */   }
/*     */ 
/*     */   protected final FragmentedArray<Object[]> getContentObjects() {
/* 468 */     return this._contentObjects;
/*     */   }
/*     */ 
/*     */   protected final int getContentObjectsPtr() {
/* 472 */     return this._contentObjectsPtr;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.stream.buffer.XMLStreamBuffer
 * JD-Core Version:    0.6.2
 */