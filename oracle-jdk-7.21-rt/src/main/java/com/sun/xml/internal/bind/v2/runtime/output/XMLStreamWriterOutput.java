/*     */ package com.sun.xml.internal.bind.v2.runtime.output;
/*     */ 
/*     */ import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
/*     */ import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
/*     */ import com.sun.xml.internal.bind.v2.util.ClassLoaderRetriever;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamWriter;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class XMLStreamWriterOutput extends XmlOutputAbstractImpl
/*     */ {
/*     */   private final XMLStreamWriter out;
/*  79 */   protected final char[] buf = new char[256];
/*     */ 
/* 160 */   private static final Class FI_STAX_WRITER_CLASS = initFIStAXWriterClass();
/* 161 */   private static final Constructor<? extends XmlOutput> FI_OUTPUT_CTOR = initFastInfosetOutputClass();
/*     */ 
/* 193 */   private static final Class STAXEX_WRITER_CLASS = initStAXExWriterClass();
/* 194 */   private static final Constructor<? extends XmlOutput> STAXEX_OUTPUT_CTOR = initStAXExOutputClass();
/*     */ 
/*     */   public static XmlOutput create(XMLStreamWriter out, JAXBContextImpl context)
/*     */   {
/*  58 */     Class writerClass = out.getClass();
/*  59 */     if (writerClass == FI_STAX_WRITER_CLASS)
/*     */       try {
/*  61 */         return (XmlOutput)FI_OUTPUT_CTOR.newInstance(new Object[] { out, context });
/*     */       }
/*     */       catch (Exception e) {
/*     */       }
/*  65 */     if ((STAXEX_WRITER_CLASS != null) && (STAXEX_WRITER_CLASS.isAssignableFrom(writerClass))) {
/*     */       try {
/*  67 */         return (XmlOutput)STAXEX_OUTPUT_CTOR.newInstance(new Object[] { out });
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*  73 */     return new XMLStreamWriterOutput(out);
/*     */   }
/*     */ 
/*     */   protected XMLStreamWriterOutput(XMLStreamWriter out)
/*     */   {
/*  82 */     this.out = out;
/*     */   }
/*     */ 
/*     */   public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext)
/*     */     throws IOException, SAXException, XMLStreamException
/*     */   {
/*  88 */     super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
/*  89 */     if (!fragment)
/*  90 */       this.out.writeStartDocument();
/*     */   }
/*     */ 
/*     */   public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException
/*     */   {
/*  95 */     if (!fragment) {
/*  96 */       this.out.writeEndDocument();
/*  97 */       this.out.flush();
/*     */     }
/*  99 */     super.endDocument(fragment);
/*     */   }
/*     */ 
/*     */   public void beginStartTag(int prefix, String localName) throws IOException, XMLStreamException {
/* 103 */     this.out.writeStartElement(this.nsContext.getPrefix(prefix), localName, this.nsContext.getNamespaceURI(prefix));
/*     */ 
/* 108 */     NamespaceContextImpl.Element nse = this.nsContext.getCurrent();
/* 109 */     if (nse.count() > 0)
/* 110 */       for (int i = nse.count() - 1; i >= 0; i--) {
/* 111 */         String uri = nse.getNsUri(i);
/* 112 */         if ((uri.length() != 0) || (nse.getBase() != 1))
/*     */         {
/* 114 */           this.out.writeNamespace(nse.getPrefix(i), uri);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public void attribute(int prefix, String localName, String value) throws IOException, XMLStreamException {
/* 120 */     if (prefix == -1)
/* 121 */       this.out.writeAttribute(localName, value);
/*     */     else
/* 123 */       this.out.writeAttribute(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName, value);
/*     */   }
/*     */ 
/*     */   public void endStartTag()
/*     */     throws IOException, SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endTag(int prefix, String localName)
/*     */     throws IOException, SAXException, XMLStreamException
/*     */   {
/* 134 */     this.out.writeEndElement();
/*     */   }
/*     */ 
/*     */   public void text(String value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
/* 138 */     if (needsSeparatingWhitespace)
/* 139 */       this.out.writeCharacters(" ");
/* 140 */     this.out.writeCharacters(value);
/*     */   }
/*     */ 
/*     */   public void text(Pcdata value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
/* 144 */     if (needsSeparatingWhitespace) {
/* 145 */       this.out.writeCharacters(" ");
/*     */     }
/* 147 */     int len = value.length();
/* 148 */     if (len < this.buf.length) {
/* 149 */       value.writeTo(this.buf, 0);
/* 150 */       this.out.writeCharacters(this.buf, 0, len);
/*     */     } else {
/* 152 */       this.out.writeCharacters(value.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Class initFIStAXWriterClass()
/*     */   {
/*     */     try
/*     */     {
/* 165 */       ClassLoader loader = ClassLoaderRetriever.getClassLoader();
/* 166 */       Class llfisw = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter", true, loader);
/* 167 */       Class sds = loader.loadClass("com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer");
/*     */ 
/* 169 */       if (llfisw.isAssignableFrom(sds)) {
/* 170 */         return sds;
/*     */       }
/* 172 */       return null; } catch (Throwable e) {
/*     */     }
/* 174 */     return null;
/*     */   }
/*     */ 
/*     */   private static Constructor<? extends XmlOutput> initFastInfosetOutputClass()
/*     */   {
/*     */     try {
/* 180 */       if (FI_STAX_WRITER_CLASS == null)
/* 181 */         return null;
/* 182 */       ClassLoader loader = ClassLoaderRetriever.getClassLoader();
/* 183 */       Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.FastInfosetStreamWriterOutput", true, loader);
/* 184 */       return c.getConstructor(new Class[] { FI_STAX_WRITER_CLASS, JAXBContextImpl.class }); } catch (Throwable e) {
/*     */     }
/* 186 */     return null;
/*     */   }
/*     */ 
/*     */   private static Class initStAXExWriterClass()
/*     */   {
/*     */     try
/*     */     {
/* 198 */       ClassLoader loader = ClassLoaderRetriever.getClassLoader();
/* 199 */       return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx", true, loader); } catch (Throwable e) {
/*     */     }
/* 201 */     return null;
/*     */   }
/*     */ 
/*     */   private static Constructor<? extends XmlOutput> initStAXExOutputClass()
/*     */   {
/*     */     try {
/* 207 */       ClassLoader loader = ClassLoaderRetriever.getClassLoader();
/* 208 */       Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput", true, loader);
/* 209 */       return c.getConstructor(new Class[] { STAXEX_WRITER_CLASS }); } catch (Throwable e) {
/*     */     }
/* 211 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput
 * JD-Core Version:    0.6.2
 */