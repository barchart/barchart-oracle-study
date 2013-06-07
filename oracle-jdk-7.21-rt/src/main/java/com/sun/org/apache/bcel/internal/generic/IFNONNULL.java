/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IFNONNULL extends IfInstruction
/*    */ {
/*    */   IFNONNULL()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IFNONNULL(InstructionHandle target)
/*    */   {
/* 76 */     super((short)199, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IFNULL(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 95 */     v.visitStackConsumer(this);
/* 96 */     v.visitBranchInstruction(this);
/* 97 */     v.visitIfInstruction(this);
/* 98 */     v.visitIFNONNULL(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IFNONNULL
 * JD-Core Version:    0.6.2
 */