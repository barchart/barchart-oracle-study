/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class FALOAD extends ArrayInstruction
/*    */   implements StackProducer
/*    */ {
/*    */   public FALOAD()
/*    */   {
/* 71 */     super((short)48);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitStackProducer(this);
/* 85 */     v.visitExceptionThrower(this);
/* 86 */     v.visitTypedInstruction(this);
/* 87 */     v.visitArrayInstruction(this);
/* 88 */     v.visitFALOAD(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.FALOAD
 * JD-Core Version:    0.6.2
 */