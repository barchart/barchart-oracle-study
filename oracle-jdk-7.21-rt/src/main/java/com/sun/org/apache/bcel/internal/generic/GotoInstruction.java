/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public abstract class GotoInstruction extends BranchInstruction
/*    */   implements UnconditionalBranch
/*    */ {
/*    */   GotoInstruction(short opcode, InstructionHandle target)
/*    */   {
/* 70 */     super(opcode, target);
/*    */   }
/*    */ 
/*    */   GotoInstruction()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.GotoInstruction
 * JD-Core Version:    0.6.2
 */