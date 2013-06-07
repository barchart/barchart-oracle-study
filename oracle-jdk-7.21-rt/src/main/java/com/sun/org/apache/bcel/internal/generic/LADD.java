/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class LADD extends ArithmeticInstruction
/*    */ {
/*    */   public LADD()
/*    */   {
/* 70 */     super((short)97);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 83 */     v.visitTypedInstruction(this);
/* 84 */     v.visitStackProducer(this);
/* 85 */     v.visitStackConsumer(this);
/* 86 */     v.visitArithmeticInstruction(this);
/* 87 */     v.visitLADD(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.LADD
 * JD-Core Version:    0.6.2
 */