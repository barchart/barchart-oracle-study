/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class AASTORE extends ArrayInstruction
/*    */   implements StackConsumer
/*    */ {
/*    */   public AASTORE()
/*    */   {
/* 71 */     super((short)83);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 84 */     v.visitStackConsumer(this);
/* 85 */     v.visitExceptionThrower(this);
/* 86 */     v.visitTypedInstruction(this);
/* 87 */     v.visitArrayInstruction(this);
/* 88 */     v.visitAASTORE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.AASTORE
 * JD-Core Version:    0.6.2
 */