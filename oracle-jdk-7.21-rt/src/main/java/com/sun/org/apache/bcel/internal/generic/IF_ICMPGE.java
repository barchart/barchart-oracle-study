/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IF_ICMPGE extends IfInstruction
/*    */ {
/*    */   IF_ICMPGE()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IF_ICMPGE(InstructionHandle target)
/*    */   {
/* 76 */     super((short)162, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IF_ICMPLT(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 96 */     v.visitStackConsumer(this);
/* 97 */     v.visitBranchInstruction(this);
/* 98 */     v.visitIfInstruction(this);
/* 99 */     v.visitIF_ICMPGE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IF_ICMPGE
 * JD-Core Version:    0.6.2
 */