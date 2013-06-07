/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class I2F extends ConversionInstruction
/*    */ {
/*    */   public I2F()
/*    */   {
/* 71 */     super((short)134);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitTypedInstruction(this);
/* 85 */     v.visitStackProducer(this);
/* 86 */     v.visitStackConsumer(this);
/* 87 */     v.visitConversionInstruction(this);
/* 88 */     v.visitI2F(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.I2F
 * JD-Core Version:    0.6.2
 */