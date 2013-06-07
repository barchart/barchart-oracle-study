/*    */ package org.omg.CORBA;
/*    */ 
/*    */ public final class TRANSIENT extends SystemException
/*    */ {
/*    */   public TRANSIENT()
/*    */   {
/* 50 */     this("");
/*    */   }
/*    */ 
/*    */   public TRANSIENT(String paramString)
/*    */   {
/* 59 */     this(paramString, 0, CompletionStatus.COMPLETED_NO);
/*    */   }
/*    */ 
/*    */   public TRANSIENT(int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 69 */     this("", paramInt, paramCompletionStatus);
/*    */   }
/*    */ 
/*    */   public TRANSIENT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 80 */     super(paramString, paramInt, paramCompletionStatus);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.TRANSIENT
 * JD-Core Version:    0.6.2
 */