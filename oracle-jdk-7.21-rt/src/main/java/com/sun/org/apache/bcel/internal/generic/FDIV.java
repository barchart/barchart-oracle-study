/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class FDIV extends ArithmeticInstruction
/*    */ {
/*    */   public FDIV()
/*    */   {
/* 71 */     super((short)110);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitTypedInstruction(this);
/* 85 */     v.visitStackProducer(this);
/* 86 */     v.visitStackConsumer(this);
/* 87 */     v.visitArithmeticInstruction(this);
/* 88 */     v.visitFDIV(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.FDIV
 * JD-Core Version:    0.6.2
 */