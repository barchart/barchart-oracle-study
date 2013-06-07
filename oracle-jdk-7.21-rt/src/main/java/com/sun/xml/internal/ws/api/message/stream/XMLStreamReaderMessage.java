/*    */ package com.sun.xml.internal.ws.api.message.stream;
/*    */ 
/*    */ import com.sun.xml.internal.ws.api.message.AttachmentSet;
/*    */ import com.sun.xml.internal.ws.api.message.Packet;
/*    */ import javax.xml.stream.XMLStreamReader;
/*    */ 
/*    */ public class XMLStreamReaderMessage extends StreamBasedMessage
/*    */ {
/*    */   public final XMLStreamReader msg;
/*    */ 
/*    */   public XMLStreamReaderMessage(Packet properties, XMLStreamReader msg)
/*    */   {
/* 53 */     super(properties);
/* 54 */     this.msg = msg;
/*    */   }
/*    */ 
/*    */   public XMLStreamReaderMessage(Packet properties, AttachmentSet attachments, XMLStreamReader msg)
/*    */   {
/* 71 */     super(properties, attachments);
/* 72 */     this.msg = msg;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.message.stream.XMLStreamReaderMessage
 * JD-Core Version:    0.6.2
 */