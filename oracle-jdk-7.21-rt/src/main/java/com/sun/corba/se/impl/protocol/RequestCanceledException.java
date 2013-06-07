/*    */ package com.sun.corba.se.impl.protocol;
/*    */ 
/*    */ public class RequestCanceledException extends RuntimeException
/*    */ {
/* 33 */   private int requestId = 0;
/*    */ 
/*    */   public RequestCanceledException(int paramInt) {
/* 36 */     this.requestId = paramInt;
/*    */   }
/*    */ 
/*    */   public int getRequestId() {
/* 40 */     return this.requestId;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.protocol.RequestCanceledException
 * JD-Core Version:    0.6.2
 */