/*    */ package com.sun.xml.internal.stream.buffer;
/*    */ 
/*    */ public class XMLStreamBufferException extends Exception
/*    */ {
/*    */   public XMLStreamBufferException(String message)
/*    */   {
/* 31 */     super(message);
/*    */   }
/*    */ 
/*    */   public XMLStreamBufferException(String message, Exception e) {
/* 35 */     super(message, e);
/*    */   }
/*    */ 
/*    */   public XMLStreamBufferException(Exception e) {
/* 39 */     super(e);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.stream.buffer.XMLStreamBufferException
 * JD-Core Version:    0.6.2
 */