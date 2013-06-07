/*    */ package com.sun.corba.se.spi.protocol;
/*    */ 
/*    */ public enum RetryType
/*    */ {
/* 38 */   NONE(false), 
/* 39 */   BEFORE_RESPONSE(true), 
/* 40 */   AFTER_RESPONSE(true);
/*    */ 
/*    */   private final boolean isRetry;
/*    */ 
/*    */   private RetryType(boolean paramBoolean) {
/* 45 */     this.isRetry = paramBoolean;
/*    */   }
/*    */ 
/*    */   public boolean isRetry() {
/* 49 */     return this.isRetry;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.protocol.RetryType
 * JD-Core Version:    0.6.2
 */