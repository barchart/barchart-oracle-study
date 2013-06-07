/*    */ package org.omg.CORBA;
/*    */ 
/*    */ public final class UNKNOWN extends SystemException
/*    */ {
/*    */   public UNKNOWN()
/*    */   {
/* 56 */     this("");
/*    */   }
/*    */ 
/*    */   public UNKNOWN(String paramString)
/*    */   {
/* 65 */     this(paramString, 0, CompletionStatus.COMPLETED_NO);
/*    */   }
/*    */ 
/*    */   public UNKNOWN(int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 75 */     this("", paramInt, paramCompletionStatus);
/*    */   }
/*    */ 
/*    */   public UNKNOWN(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 86 */     super(paramString, paramInt, paramCompletionStatus);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.UNKNOWN
 * JD-Core Version:    0.6.2
 */