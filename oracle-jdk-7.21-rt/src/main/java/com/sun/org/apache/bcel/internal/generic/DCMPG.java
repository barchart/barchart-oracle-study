/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class DCMPG extends Instruction
/*    */   implements TypedInstruction, StackProducer, StackConsumer
/*    */ {
/*    */   public DCMPG()
/*    */   {
/* 72 */     super((short)152, (short)1);
/*    */   }
/*    */ 
/*    */   public Type getType(ConstantPoolGen cp)
/*    */   {
/* 78 */     return Type.DOUBLE;
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 91 */     v.visitTypedInstruction(this);
/* 92 */     v.visitStackProducer(this);
/* 93 */     v.visitStackConsumer(this);
/* 94 */     v.visitDCMPG(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.DCMPG
 * JD-Core Version:    0.6.2
 */