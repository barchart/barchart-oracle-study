/*     */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*     */ 
/*     */ import com.sun.xml.internal.bind.WhiteSpaceProcessor;
/*     */ import com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.bind.UnmarshallerHandler;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.Locator;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public final class SAXConnector
/*     */   implements UnmarshallerHandler
/*     */ {
/*     */   private LocatorEx loc;
/*  51 */   private final StringBuilder buffer = new StringBuilder();
/*     */   private final XmlVisitor next;
/*     */   private final UnmarshallingContext context;
/*     */   private final XmlVisitor.TextPredictor predictor;
/*  64 */   private final TagNameImpl tagName = new TagNameImpl(null);
/*     */ 
/*     */   public SAXConnector(XmlVisitor next, LocatorEx externalLocator)
/*     */   {
/*  73 */     this.next = next;
/*  74 */     this.context = next.getContext();
/*  75 */     this.predictor = next.getPredictor();
/*  76 */     this.loc = externalLocator;
/*     */   }
/*     */ 
/*     */   public Object getResult() throws JAXBException, IllegalStateException {
/*  80 */     return this.context.getResult();
/*     */   }
/*     */ 
/*     */   public UnmarshallingContext getContext() {
/*  84 */     return this.context;
/*     */   }
/*     */ 
/*     */   public void setDocumentLocator(Locator locator) {
/*  88 */     if (this.loc != null) {
/*  89 */       return;
/*     */     }
/*  91 */     this.loc = new LocatorExWrapper(locator);
/*     */   }
/*     */ 
/*     */   public void startDocument() throws SAXException {
/*  95 */     this.next.startDocument(this.loc, null);
/*     */   }
/*     */ 
/*     */   public void endDocument() throws SAXException {
/*  99 */     this.next.endDocument();
/*     */   }
/*     */ 
/*     */   public void startPrefixMapping(String prefix, String uri) throws SAXException {
/* 103 */     this.next.startPrefixMapping(prefix, uri);
/*     */   }
/*     */ 
/*     */   public void endPrefixMapping(String prefix) throws SAXException {
/* 107 */     this.next.endPrefixMapping(prefix);
/*     */   }
/*     */ 
/*     */   public void startElement(String uri, String local, String qname, Attributes atts) throws SAXException
/*     */   {
/* 112 */     if ((uri == null) || (uri.length() == 0))
/* 113 */       uri = "";
/* 114 */     if ((local == null) || (local.length() == 0))
/* 115 */       local = qname;
/* 116 */     if ((qname == null) || (qname.length() == 0)) {
/* 117 */       qname = local;
/*     */     }
/*     */ 
/* 120 */     boolean ignorable = true;
/*     */     StructureLoader sl;
/* 125 */     if ((sl = this.context.getStructureLoader()) != null) {
/* 126 */       ignorable = ((ClassBeanInfoImpl)sl.getBeanInfo()).hasElementOnlyContentModel();
/*     */     }
/*     */ 
/* 129 */     processText(ignorable);
/*     */ 
/* 131 */     this.tagName.uri = uri;
/* 132 */     this.tagName.local = local;
/* 133 */     this.tagName.qname = qname;
/* 134 */     this.tagName.atts = atts;
/* 135 */     this.next.startElement(this.tagName);
/*     */   }
/*     */ 
/*     */   public void endElement(String uri, String localName, String qName) throws SAXException {
/* 139 */     processText(false);
/* 140 */     this.tagName.uri = uri;
/* 141 */     this.tagName.local = localName;
/* 142 */     this.tagName.qname = qName;
/* 143 */     this.next.endElement(this.tagName);
/*     */   }
/*     */ 
/*     */   public final void characters(char[] buf, int start, int len)
/*     */   {
/* 148 */     if (this.predictor.expectText())
/* 149 */       this.buffer.append(buf, start, len);
/*     */   }
/*     */ 
/*     */   public final void ignorableWhitespace(char[] buf, int start, int len) {
/* 153 */     characters(buf, start, len);
/*     */   }
/*     */ 
/*     */   public void processingInstruction(String target, String data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void skippedEntity(String name)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void processText(boolean ignorable) throws SAXException {
/* 165 */     if ((this.predictor.expectText()) && ((!ignorable) || (!WhiteSpaceProcessor.isWhiteSpace(this.buffer))))
/* 166 */       this.next.text(this.buffer);
/* 167 */     this.buffer.setLength(0);
/*     */   }
/*     */ 
/*     */   private static final class TagNameImpl extends TagName
/*     */   {
/*     */     String qname;
/*     */ 
/*     */     public String getQname()
/*     */     {
/*  60 */       return this.qname;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.SAXConnector
 * JD-Core Version:    0.6.2
 */