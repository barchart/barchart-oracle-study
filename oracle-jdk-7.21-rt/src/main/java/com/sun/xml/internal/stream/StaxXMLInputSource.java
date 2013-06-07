/*    */ package com.sun.xml.internal.stream;
/*    */ 
/*    */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*    */ import javax.xml.stream.XMLEventReader;
/*    */ import javax.xml.stream.XMLStreamReader;
/*    */ 
/*    */ public class StaxXMLInputSource
/*    */ {
/*    */   XMLStreamReader fStreamReader;
/*    */   XMLEventReader fEventReader;
/*    */   XMLInputSource fInputSource;
/*    */ 
/*    */   public StaxXMLInputSource(XMLStreamReader streamReader)
/*    */   {
/* 48 */     this.fStreamReader = streamReader;
/*    */   }
/*    */ 
/*    */   public StaxXMLInputSource(XMLEventReader eventReader)
/*    */   {
/* 53 */     this.fEventReader = eventReader;
/*    */   }
/*    */ 
/*    */   public StaxXMLInputSource(XMLInputSource inputSource) {
/* 57 */     this.fInputSource = inputSource;
/*    */   }
/*    */ 
/*    */   public XMLStreamReader getXMLStreamReader() {
/* 61 */     return this.fStreamReader;
/*    */   }
/*    */ 
/*    */   public XMLEventReader getXMLEventReader() {
/* 65 */     return this.fEventReader;
/*    */   }
/*    */ 
/*    */   public XMLInputSource getXMLInputSource() {
/* 69 */     return this.fInputSource;
/*    */   }
/*    */ 
/*    */   public boolean hasXMLStreamOrXMLEventReader() {
/* 73 */     return (this.fStreamReader != null) || (this.fEventReader != null);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.stream.StaxXMLInputSource
 * JD-Core Version:    0.6.2
 */