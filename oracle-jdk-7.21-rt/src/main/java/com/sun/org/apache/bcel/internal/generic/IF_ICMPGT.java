/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IF_ICMPGT extends IfInstruction
/*    */ {
/*    */   IF_ICMPGT()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IF_ICMPGT(InstructionHandle target)
/*    */   {
/* 76 */     super((short)163, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IF_ICMPLE(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 96 */     v.visitStackConsumer(this);
/* 97 */     v.visitBranchInstruction(this);
/* 98 */     v.visitIfInstruction(this);
/* 99 */     v.visitIF_ICMPGT(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IF_ICMPGT
 * JD-Core Version:    0.6.2
 */