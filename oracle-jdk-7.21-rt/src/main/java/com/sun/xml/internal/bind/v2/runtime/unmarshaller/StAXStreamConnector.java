/*     */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*     */ 
/*     */ import com.sun.xml.internal.bind.WhiteSpaceProcessor;
/*     */ import com.sun.xml.internal.bind.v2.util.ClassLoaderRetriever;
/*     */ import java.lang.reflect.Constructor;
/*     */ import javax.xml.stream.Location;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ class StAXStreamConnector extends StAXConnector
/*     */ {
/*     */   private final XMLStreamReader staxStreamReader;
/* 126 */   protected final StringBuilder buffer = new StringBuilder();
/*     */ 
/* 132 */   protected boolean textReported = false;
/*     */ 
/* 238 */   private final Attributes attributes = new Attributes() {
/*     */     public int getLength() {
/* 240 */       return StAXStreamConnector.this.staxStreamReader.getAttributeCount();
/*     */     }
/*     */ 
/*     */     public String getURI(int index) {
/* 244 */       String uri = StAXStreamConnector.this.staxStreamReader.getAttributeNamespace(index);
/* 245 */       if (uri == null) return "";
/* 246 */       return uri;
/*     */     }
/*     */ 
/*     */     public String getLocalName(int index) {
/* 250 */       return StAXStreamConnector.this.staxStreamReader.getAttributeLocalName(index);
/*     */     }
/*     */ 
/*     */     public String getQName(int index) {
/* 254 */       String prefix = StAXStreamConnector.this.staxStreamReader.getAttributePrefix(index);
/* 255 */       if ((prefix == null) || (prefix.length() == 0)) {
/* 256 */         return getLocalName(index);
/*     */       }
/* 258 */       return prefix + ':' + getLocalName(index);
/*     */     }
/*     */ 
/*     */     public String getType(int index) {
/* 262 */       return StAXStreamConnector.this.staxStreamReader.getAttributeType(index);
/*     */     }
/*     */ 
/*     */     public String getValue(int index) {
/* 266 */       return StAXStreamConnector.this.staxStreamReader.getAttributeValue(index);
/*     */     }
/*     */ 
/*     */     public int getIndex(String uri, String localName) {
/* 270 */       for (int i = getLength() - 1; i >= 0; i--)
/* 271 */         if ((localName.equals(getLocalName(i))) && (uri.equals(getURI(i))))
/* 272 */           return i;
/* 273 */       return -1;
/*     */     }
/*     */ 
/*     */     public int getIndex(String qName)
/*     */     {
/* 279 */       for (int i = getLength() - 1; i >= 0; i--) {
/* 280 */         if (qName.equals(getQName(i)))
/* 281 */           return i;
/*     */       }
/* 283 */       return -1;
/*     */     }
/*     */ 
/*     */     public String getType(String uri, String localName) {
/* 287 */       int index = getIndex(uri, localName);
/* 288 */       if (index < 0) return null;
/* 289 */       return getType(index);
/*     */     }
/*     */ 
/*     */     public String getType(String qName) {
/* 293 */       int index = getIndex(qName);
/* 294 */       if (index < 0) return null;
/* 295 */       return getType(index);
/*     */     }
/*     */ 
/*     */     public String getValue(String uri, String localName) {
/* 299 */       int index = getIndex(uri, localName);
/* 300 */       if (index < 0) return null;
/* 301 */       return getValue(index);
/*     */     }
/*     */ 
/*     */     public String getValue(String qName) {
/* 305 */       int index = getIndex(qName);
/* 306 */       if (index < 0) return null;
/* 307 */       return getValue(index);
/*     */     }
/* 238 */   };
/*     */ 
/* 335 */   private static final Class FI_STAX_READER_CLASS = initFIStAXReaderClass();
/* 336 */   private static final Constructor<? extends StAXConnector> FI_CONNECTOR_CTOR = initFastInfosetConnectorClass();
/*     */ 
/* 369 */   private static final Class STAX_EX_READER_CLASS = initStAXExReader();
/* 370 */   private static final Constructor<? extends StAXConnector> STAX_EX_CONNECTOR_CTOR = initStAXExConnector();
/*     */ 
/*     */   public static StAXConnector create(XMLStreamReader reader, XmlVisitor visitor)
/*     */   {
/*  63 */     Class readerClass = reader.getClass();
/*  64 */     if ((FI_STAX_READER_CLASS != null) && (FI_STAX_READER_CLASS.isAssignableFrom(readerClass)) && (FI_CONNECTOR_CTOR != null)) {
/*     */       try {
/*  66 */         return (StAXConnector)FI_CONNECTOR_CTOR.newInstance(new Object[] { reader, visitor });
/*     */       }
/*     */       catch (Exception t)
/*     */       {
/*     */       }
/*     */     }
/*  72 */     boolean isZephyr = readerClass.getName().equals("com.sun.xml.internal.stream.XMLReaderImpl");
/*  73 */     if ((!getBoolProp(reader, "org.codehaus.stax2.internNames")) || (!getBoolProp(reader, "org.codehaus.stax2.internNsUris")))
/*     */     {
/*  77 */       if (!isZephyr)
/*     */       {
/*  80 */         if (!checkImplementaionNameOfSjsxp(reader))
/*     */         {
/*  83 */           visitor = new InterningXmlVisitor(visitor);
/*     */         }
/*     */       }
/*     */     }
/*  85 */     if ((STAX_EX_READER_CLASS != null) && (STAX_EX_READER_CLASS.isAssignableFrom(readerClass)))
/*     */       try {
/*  87 */         return (StAXConnector)STAX_EX_CONNECTOR_CTOR.newInstance(new Object[] { reader, visitor });
/*     */       }
/*     */       catch (Exception t)
/*     */       {
/*     */       }
/*  92 */     return new StAXStreamConnector(reader, visitor);
/*     */   }
/*     */ 
/*     */   private static boolean checkImplementaionNameOfSjsxp(XMLStreamReader reader) {
/*     */     try {
/*  97 */       Object name = reader.getProperty("http://java.sun.com/xml/stream/properties/implementation-name");
/*  98 */       return (name != null) && (name.equals("sjsxp"));
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean getBoolProp(XMLStreamReader r, String n)
/*     */   {
/*     */     try {
/* 108 */       Object o = r.getProperty(n);
/* 109 */       if ((o instanceof Boolean)) return ((Boolean)o).booleanValue();
/* 110 */       return false;
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 114 */     return false;
/*     */   }
/*     */ 
/*     */   protected StAXStreamConnector(XMLStreamReader staxStreamReader, XmlVisitor visitor)
/*     */   {
/* 135 */     super(visitor);
/* 136 */     this.staxStreamReader = staxStreamReader;
/*     */   }
/*     */ 
/*     */   public void bridge() throws XMLStreamException
/*     */   {
/*     */     try
/*     */     {
/* 143 */       int depth = 0;
/*     */ 
/* 146 */       int event = this.staxStreamReader.getEventType();
/* 147 */       if (event == 7)
/*     */       {
/* 149 */         while (!this.staxStreamReader.isStartElement()) {
/* 150 */           event = this.staxStreamReader.next();
/*     */         }
/*     */       }
/*     */ 
/* 154 */       if (event != 1) {
/* 155 */         throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);
/*     */       }
/* 157 */       handleStartDocument(this.staxStreamReader.getNamespaceContext());
/*     */       while (true)
/*     */       {
/* 164 */         switch (event) {
/*     */         case 1:
/* 166 */           handleStartElement();
/* 167 */           depth++;
/* 168 */           break;
/*     */         case 2:
/* 170 */           depth--;
/* 171 */           handleEndElement();
/* 172 */           if (depth != 0) break; break;
/*     */         case 4:
/*     */         case 6:
/*     */         case 12:
/* 177 */           handleCharacters();
/*     */         case 3:
/*     */         case 5:
/*     */         case 7:
/*     */         case 8:
/*     */         case 9:
/*     */         case 10:
/* 182 */         case 11: } event = this.staxStreamReader.next();
/*     */       }
/*     */ 
/* 185 */       this.staxStreamReader.next();
/*     */ 
/* 187 */       handleEndDocument();
/*     */     } catch (SAXException e) {
/* 189 */       throw new XMLStreamException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Location getCurrentLocation() {
/* 194 */     return this.staxStreamReader.getLocation();
/*     */   }
/*     */ 
/*     */   protected String getCurrentQName() {
/* 198 */     return getQName(this.staxStreamReader.getPrefix(), this.staxStreamReader.getLocalName());
/*     */   }
/*     */ 
/*     */   private void handleEndElement() throws SAXException {
/* 202 */     processText(false);
/*     */ 
/* 205 */     this.tagName.uri = fixNull(this.staxStreamReader.getNamespaceURI());
/* 206 */     this.tagName.local = this.staxStreamReader.getLocalName();
/* 207 */     this.visitor.endElement(this.tagName);
/*     */ 
/* 210 */     int nsCount = this.staxStreamReader.getNamespaceCount();
/* 211 */     for (int i = nsCount - 1; i >= 0; i--)
/* 212 */       this.visitor.endPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(i)));
/*     */   }
/*     */ 
/*     */   private void handleStartElement() throws SAXException
/*     */   {
/* 217 */     processText(true);
/*     */ 
/* 220 */     int nsCount = this.staxStreamReader.getNamespaceCount();
/* 221 */     for (int i = 0; i < nsCount; i++) {
/* 222 */       this.visitor.startPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(i)), fixNull(this.staxStreamReader.getNamespaceURI(i)));
/*     */     }
/*     */ 
/* 228 */     this.tagName.uri = fixNull(this.staxStreamReader.getNamespaceURI());
/* 229 */     this.tagName.local = this.staxStreamReader.getLocalName();
/* 230 */     this.tagName.atts = this.attributes;
/*     */ 
/* 232 */     this.visitor.startElement(this.tagName);
/*     */   }
/*     */ 
/*     */   protected void handleCharacters()
/*     */     throws XMLStreamException, SAXException
/*     */   {
/* 312 */     if (this.predictor.expectText())
/* 313 */       this.buffer.append(this.staxStreamReader.getTextCharacters(), this.staxStreamReader.getTextStart(), this.staxStreamReader.getTextLength());
/*     */   }
/*     */ 
/*     */   private void processText(boolean ignorable)
/*     */     throws SAXException
/*     */   {
/* 320 */     if ((this.predictor.expectText()) && ((!ignorable) || (!WhiteSpaceProcessor.isWhiteSpace(this.buffer)))) {
/* 321 */       if (this.textReported)
/* 322 */         this.textReported = false;
/*     */       else {
/* 324 */         this.visitor.text(this.buffer);
/*     */       }
/*     */     }
/* 327 */     this.buffer.setLength(0);
/*     */   }
/*     */ 
/*     */   private static Class initFIStAXReaderClass()
/*     */   {
/*     */     try
/*     */     {
/* 340 */       ClassLoader cl = getClassLoader();
/* 341 */       Class fisr = cl.loadClass("com.sun.xml.internal.org.jvnet.fastinfoset.stax.FastInfosetStreamReader");
/* 342 */       Class sdp = cl.loadClass("com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser");
/*     */ 
/* 344 */       if (fisr.isAssignableFrom(sdp)) {
/* 345 */         return sdp;
/*     */       }
/* 347 */       return null; } catch (Throwable e) {
/*     */     }
/* 349 */     return null;
/*     */   }
/*     */ 
/*     */   private static Constructor<? extends StAXConnector> initFastInfosetConnectorClass()
/*     */   {
/*     */     try {
/* 355 */       if (FI_STAX_READER_CLASS == null) {
/* 356 */         return null;
/*     */       }
/* 358 */       Class c = getClassLoader().loadClass("com.sun.xml.internal.bind.v2.runtime.unmarshaller.FastInfosetConnector");
/*     */ 
/* 360 */       return c.getConstructor(new Class[] { FI_STAX_READER_CLASS, XmlVisitor.class }); } catch (Throwable e) {
/*     */     }
/* 362 */     return null;
/*     */   }
/*     */ 
/*     */   private static Class initStAXExReader()
/*     */   {
/*     */     try
/*     */     {
/* 374 */       return getClassLoader().loadClass("com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx"); } catch (Throwable e) {
/*     */     }
/* 376 */     return null;
/*     */   }
/*     */ 
/*     */   private static Constructor<? extends StAXConnector> initStAXExConnector()
/*     */   {
/*     */     try {
/* 382 */       Class c = getClassLoader().loadClass("com.sun.xml.internal.bind.v2.runtime.unmarshaller.StAXExConnector");
/* 383 */       return c.getConstructor(new Class[] { STAX_EX_READER_CLASS, XmlVisitor.class }); } catch (Throwable e) {
/*     */     }
/* 385 */     return null;
/*     */   }
/*     */ 
/*     */   public static ClassLoader getClassLoader()
/*     */   {
/* 390 */     return ClassLoaderRetriever.getClassLoader();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.StAXStreamConnector
 * JD-Core Version:    0.6.2
 */