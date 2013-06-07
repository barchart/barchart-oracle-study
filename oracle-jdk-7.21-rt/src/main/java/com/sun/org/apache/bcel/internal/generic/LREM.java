/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ import com.sun.org.apache.bcel.internal.ExceptionConstants;
/*    */ 
/*    */ public class LREM extends ArithmeticInstruction
/*    */   implements ExceptionThrower
/*    */ {
/*    */   public LREM()
/*    */   {
/* 69 */     super((short)113);
/*    */   }
/*    */   public Class[] getExceptions() {
/* 72 */     return new Class[] { ExceptionConstants.ARITHMETIC_EXCEPTION };
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitExceptionThrower(this);
/* 85 */     v.visitTypedInstruction(this);
/* 86 */     v.visitStackProducer(this);
/* 87 */     v.visitStackConsumer(this);
/* 88 */     v.visitArithmeticInstruction(this);
/* 89 */     v.visitLREM(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.LREM
 * JD-Core Version:    0.6.2
 */