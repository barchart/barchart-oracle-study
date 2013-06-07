/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IMUL extends ArithmeticInstruction
/*    */ {
/*    */   public IMUL()
/*    */   {
/* 71 */     super((short)104);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitTypedInstruction(this);
/* 85 */     v.visitStackProducer(this);
/* 86 */     v.visitStackConsumer(this);
/* 87 */     v.visitArithmeticInstruction(this);
/* 88 */     v.visitIMUL(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IMUL
 * JD-Core Version:    0.6.2
 */