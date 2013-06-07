/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class BASTORE extends ArrayInstruction
/*    */   implements StackConsumer
/*    */ {
/*    */   public BASTORE()
/*    */   {
/* 71 */     super((short)84);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitStackConsumer(this);
/* 85 */     v.visitExceptionThrower(this);
/* 86 */     v.visitTypedInstruction(this);
/* 87 */     v.visitArrayInstruction(this);
/* 88 */     v.visitBASTORE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.BASTORE
 * JD-Core Version:    0.6.2
 */