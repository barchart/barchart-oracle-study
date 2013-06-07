/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class FMUL extends ArithmeticInstruction
/*    */ {
/*    */   public FMUL()
/*    */   {
/* 71 */     super((short)106);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitTypedInstruction(this);
/* 85 */     v.visitStackProducer(this);
/* 86 */     v.visitStackConsumer(this);
/* 87 */     v.visitArithmeticInstruction(this);
/* 88 */     v.visitFMUL(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.FMUL
 * JD-Core Version:    0.6.2
 */