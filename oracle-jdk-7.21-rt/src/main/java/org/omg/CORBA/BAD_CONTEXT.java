/*    */ package org.omg.CORBA;
/*    */ 
/*    */ public final class BAD_CONTEXT extends SystemException
/*    */ {
/*    */   public BAD_CONTEXT()
/*    */   {
/* 48 */     this("");
/*    */   }
/*    */ 
/*    */   public BAD_CONTEXT(String paramString)
/*    */   {
/* 58 */     this(paramString, 0, CompletionStatus.COMPLETED_NO);
/*    */   }
/*    */ 
/*    */   public BAD_CONTEXT(int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 70 */     this("", paramInt, paramCompletionStatus);
/*    */   }
/*    */ 
/*    */   public BAD_CONTEXT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 84 */     super(paramString, paramInt, paramCompletionStatus);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.BAD_CONTEXT
 * JD-Core Version:    0.6.2
 */