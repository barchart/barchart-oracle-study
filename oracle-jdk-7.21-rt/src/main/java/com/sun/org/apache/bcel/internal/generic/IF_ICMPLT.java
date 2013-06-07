/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IF_ICMPLT extends IfInstruction
/*    */ {
/*    */   IF_ICMPLT()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IF_ICMPLT(InstructionHandle target)
/*    */   {
/* 76 */     super((short)161, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IF_ICMPGE(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 96 */     v.visitStackConsumer(this);
/* 97 */     v.visitBranchInstruction(this);
/* 98 */     v.visitIfInstruction(this);
/* 99 */     v.visitIF_ICMPLT(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IF_ICMPLT
 * JD-Core Version:    0.6.2
 */