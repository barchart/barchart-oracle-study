/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class NOP extends Instruction
/*    */ {
/*    */   public NOP()
/*    */   {
/* 68 */     super((short)0, (short)1);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 81 */     v.visitNOP(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.NOP
 * JD-Core Version:    0.6.2
 */