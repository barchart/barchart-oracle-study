/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IFGE extends IfInstruction
/*    */ {
/*    */   IFGE()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IFGE(InstructionHandle target)
/*    */   {
/* 76 */     super((short)156, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IFLT(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 96 */     v.visitStackConsumer(this);
/* 97 */     v.visitBranchInstruction(this);
/* 98 */     v.visitIfInstruction(this);
/* 99 */     v.visitIFGE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IFGE
 * JD-Core Version:    0.6.2
 */