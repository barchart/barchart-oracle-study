/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public abstract class StackInstruction extends Instruction
/*    */ {
/*    */   StackInstruction()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected StackInstruction(short opcode)
/*    */   {
/* 77 */     super(opcode, (short)1);
/*    */   }
/*    */ 
/*    */   public Type getType(ConstantPoolGen cp)
/*    */   {
/* 83 */     return Type.UNKNOWN;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.StackInstruction
 * JD-Core Version:    0.6.2
 */