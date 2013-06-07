/*    */ package com.sun.xml.internal.messaging.saaj.soap;
/*    */ 
/*    */ import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
/*    */ 
/*    */ public class SOAPVersionMismatchException extends SOAPExceptionImpl
/*    */ {
/*    */   public SOAPVersionMismatchException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SOAPVersionMismatchException(String reason)
/*    */   {
/* 46 */     super(reason);
/*    */   }
/*    */ 
/*    */   public SOAPVersionMismatchException(String reason, Throwable cause)
/*    */   {
/* 60 */     super(reason, cause);
/*    */   }
/*    */ 
/*    */   public SOAPVersionMismatchException(Throwable cause)
/*    */   {
/* 68 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.messaging.saaj.soap.SOAPVersionMismatchException
 * JD-Core Version:    0.6.2
 */