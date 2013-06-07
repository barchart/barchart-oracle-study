/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class LRETURN extends ReturnInstruction
/*    */ {
/*    */   public LRETURN()
/*    */   {
/* 69 */     super((short)173);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 82 */     v.visitExceptionThrower(this);
/* 83 */     v.visitTypedInstruction(this);
/* 84 */     v.visitStackConsumer(this);
/* 85 */     v.visitReturnInstruction(this);
/* 86 */     v.visitLRETURN(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.LRETURN
 * JD-Core Version:    0.6.2
 */