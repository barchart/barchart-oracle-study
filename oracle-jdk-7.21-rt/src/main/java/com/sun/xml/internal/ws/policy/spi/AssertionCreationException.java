/*    */ package com.sun.xml.internal.ws.policy.spi;
/*    */ 
/*    */ import com.sun.xml.internal.ws.policy.PolicyException;
/*    */ import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
/*    */ 
/*    */ public final class AssertionCreationException extends PolicyException
/*    */ {
/*    */   private final AssertionData assertionData;
/*    */ 
/*    */   public AssertionCreationException(AssertionData assertionData, String message)
/*    */   {
/* 50 */     super(message);
/* 51 */     this.assertionData = assertionData;
/*    */   }
/*    */ 
/*    */   public AssertionCreationException(AssertionData assertionData, String message, Throwable cause)
/*    */   {
/* 65 */     super(message, cause);
/* 66 */     this.assertionData = assertionData;
/*    */   }
/*    */ 
/*    */   public AssertionCreationException(AssertionData assertionData, Throwable cause)
/*    */   {
/* 76 */     super(cause);
/* 77 */     this.assertionData = assertionData;
/*    */   }
/*    */ 
/*    */   public AssertionData getAssertionData()
/*    */   {
/* 86 */     return this.assertionData;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.policy.spi.AssertionCreationException
 * JD-Core Version:    0.6.2
 */