/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class POP extends StackInstruction
/*    */   implements PopInstruction
/*    */ {
/*    */   public POP()
/*    */   {
/* 70 */     super((short)87);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 83 */     v.visitStackConsumer(this);
/* 84 */     v.visitPopInstruction(this);
/* 85 */     v.visitStackInstruction(this);
/* 86 */     v.visitPOP(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.POP
 * JD-Core Version:    0.6.2
 */