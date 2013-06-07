/*    */ package com.sun.xml.internal.txw2;
/*    */ 
/*    */ public class IllegalSignatureException extends TxwException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public IllegalSignatureException(String message)
/*    */   {
/* 35 */     super(message);
/*    */   }
/*    */ 
/*    */   public IllegalSignatureException(String message, Throwable cause) {
/* 39 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public IllegalSignatureException(Throwable cause) {
/* 43 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.txw2.IllegalSignatureException
 * JD-Core Version:    0.6.2
 */