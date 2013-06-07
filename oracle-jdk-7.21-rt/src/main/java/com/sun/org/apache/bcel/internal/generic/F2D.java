/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class F2D extends ConversionInstruction
/*    */ {
/*    */   public F2D()
/*    */   {
/* 71 */     super((short)141);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitTypedInstruction(this);
/* 85 */     v.visitStackProducer(this);
/* 86 */     v.visitStackConsumer(this);
/* 87 */     v.visitConversionInstruction(this);
/* 88 */     v.visitF2D(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.F2D
 * JD-Core Version:    0.6.2
 */