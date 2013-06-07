/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IFLE extends IfInstruction
/*    */ {
/*    */   IFLE()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IFLE(InstructionHandle target)
/*    */   {
/* 76 */     super((short)158, target);
/*    */   }
/*    */ 
/*    */   public IfInstruction negate()
/*    */   {
/* 83 */     return new IFGT(this.target);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 96 */     v.visitStackConsumer(this);
/* 97 */     v.visitBranchInstruction(this);
/* 98 */     v.visitIfInstruction(this);
/* 99 */     v.visitIFLE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IFLE
 * JD-Core Version:    0.6.2
 */