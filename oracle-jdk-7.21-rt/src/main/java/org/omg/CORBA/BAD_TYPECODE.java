/*    */ package org.omg.CORBA;
/*    */ 
/*    */ public final class BAD_TYPECODE extends SystemException
/*    */ {
/*    */   public BAD_TYPECODE()
/*    */   {
/* 47 */     this("");
/*    */   }
/*    */ 
/*    */   public BAD_TYPECODE(String paramString)
/*    */   {
/* 57 */     this(paramString, 0, CompletionStatus.COMPLETED_NO);
/*    */   }
/*    */ 
/*    */   public BAD_TYPECODE(int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 68 */     this("", paramInt, paramCompletionStatus);
/*    */   }
/*    */ 
/*    */   public BAD_TYPECODE(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
/*    */   {
/* 81 */     super(paramString, paramInt, paramCompletionStatus);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.BAD_TYPECODE
 * JD-Core Version:    0.6.2
 */