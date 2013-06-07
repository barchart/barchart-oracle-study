/*     */ package com.sun.xml.internal.bind.v2.runtime;
/*     */ 
/*     */ import com.sun.istack.internal.FinalArrayList;
/*     */ import com.sun.istack.internal.SAXException2;
/*     */ import java.io.IOException;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ final class ContentHandlerAdaptor extends DefaultHandler
/*     */ {
/*  49 */   private final FinalArrayList<String> prefixMap = new FinalArrayList();
/*     */   private final XMLSerializer serializer;
/*  54 */   private final StringBuffer text = new StringBuffer();
/*     */ 
/*     */   ContentHandlerAdaptor(XMLSerializer _serializer)
/*     */   {
/*  58 */     this.serializer = _serializer;
/*     */   }
/*     */ 
/*     */   public void startDocument() {
/*  62 */     this.prefixMap.clear();
/*     */   }
/*     */ 
/*     */   public void startPrefixMapping(String prefix, String uri) {
/*  66 */     this.prefixMap.add(prefix);
/*  67 */     this.prefixMap.add(uri);
/*     */   }
/*     */ 
/*     */   private boolean containsPrefixMapping(String prefix, String uri) {
/*  71 */     for (int i = 0; i < this.prefixMap.size(); i += 2)
/*  72 */       if ((((String)this.prefixMap.get(i)).equals(prefix)) && (((String)this.prefixMap.get(i + 1)).equals(uri)))
/*     */       {
/*  74 */         return true;
/*     */       }
/*  76 */     return false;
/*     */   }
/*     */ 
/*     */   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
/*     */   {
/*     */     try {
/*  82 */       flushText();
/*     */ 
/*  84 */       int len = atts.getLength();
/*     */ 
/*  86 */       String p = getPrefix(qName);
/*     */ 
/*  89 */       if (containsPrefixMapping(p, namespaceURI))
/*  90 */         this.serializer.startElementForce(namespaceURI, localName, p, null);
/*     */       else {
/*  92 */         this.serializer.startElement(namespaceURI, localName, p, null);
/*     */       }
/*     */ 
/*  95 */       for (int i = 0; i < this.prefixMap.size(); i += 2)
/*     */       {
/* 100 */         this.serializer.getNamespaceContext().force((String)this.prefixMap.get(i + 1), (String)this.prefixMap.get(i));
/*     */       }
/*     */ 
/* 104 */       for (int i = 0; i < len; i++) {
/* 105 */         String qname = atts.getQName(i);
/* 106 */         if ((!qname.startsWith("xmlns")) && (atts.getURI(i).length() != 0))
/*     */         {
/* 108 */           String prefix = getPrefix(qname);
/*     */ 
/* 110 */           this.serializer.getNamespaceContext().declareNamespace(atts.getURI(i), prefix, true);
/*     */         }
/*     */       }
/*     */ 
/* 114 */       this.serializer.endNamespaceDecls(null);
/*     */ 
/* 116 */       for (int i = 0; i < len; i++)
/*     */       {
/* 118 */         if (!atts.getQName(i).startsWith("xmlns"))
/*     */         {
/* 120 */           this.serializer.attribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
/*     */         }
/*     */       }
/* 122 */       this.prefixMap.clear();
/* 123 */       this.serializer.endAttributes();
/*     */     } catch (IOException e) {
/* 125 */       throw new SAXException2(e);
/*     */     } catch (XMLStreamException e) {
/* 127 */       throw new SAXException2(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getPrefix(String qname) {
/* 132 */     int idx = qname.indexOf(':');
/* 133 */     String prefix = idx == -1 ? qname : qname.substring(0, idx);
/* 134 */     return prefix;
/*     */   }
/*     */ 
/*     */   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
/*     */     try {
/* 139 */       flushText();
/* 140 */       this.serializer.endElement();
/*     */     } catch (IOException e) {
/* 142 */       throw new SAXException2(e);
/*     */     } catch (XMLStreamException e) {
/* 144 */       throw new SAXException2(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void flushText() throws SAXException, IOException, XMLStreamException {
/* 149 */     if (this.text.length() != 0) {
/* 150 */       this.serializer.text(this.text.toString(), null);
/* 151 */       this.text.setLength(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void characters(char[] ch, int start, int length) {
/* 156 */     this.text.append(ch, start, length);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.ContentHandlerAdaptor
 * JD-Core Version:    0.6.2
 */