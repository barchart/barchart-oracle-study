/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IFEQ extends IfInstruction
/*    */ {
/*    */   IFEQ()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IFEQ(InstructionHandle target)
/*    */   {
/* 76 */     super((short)153, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IFNE(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 96 */     v.visitStackConsumer(this);
/* 97 */     v.visitBranchInstruction(this);
/* 98 */     v.visitIfInstruction(this);
/* 99 */     v.visitIFEQ(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IFEQ
 * JD-Core Version:    0.6.2
 */