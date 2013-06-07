/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class ISUB extends ArithmeticInstruction
/*    */ {
/*    */   public ISUB()
/*    */   {
/* 71 */     super((short)100);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitTypedInstruction(this);
/* 85 */     v.visitStackProducer(this);
/* 86 */     v.visitStackConsumer(this);
/* 87 */     v.visitArithmeticInstruction(this);
/* 88 */     v.visitISUB(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.ISUB
 * JD-Core Version:    0.6.2
 */