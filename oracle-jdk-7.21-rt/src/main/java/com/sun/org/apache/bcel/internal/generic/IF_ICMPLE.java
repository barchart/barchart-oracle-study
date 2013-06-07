/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IF_ICMPLE extends IfInstruction
/*    */ {
/*    */   IF_ICMPLE()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IF_ICMPLE(InstructionHandle target)
/*    */   {
/* 76 */     super((short)164, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IF_ICMPGT(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 96 */     v.visitStackConsumer(this);
/* 97 */     v.visitBranchInstruction(this);
/* 98 */     v.visitIfInstruction(this);
/* 99 */     v.visitIF_ICMPLE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IF_ICMPLE
 * JD-Core Version:    0.6.2
 */