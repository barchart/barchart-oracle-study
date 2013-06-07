/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class IMPDEP2 extends Instruction
/*    */ {
/*    */   public IMPDEP2()
/*    */   {
/* 68 */     super((short)255, (short)1);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 81 */     v.visitIMPDEP2(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.IMPDEP2
 * JD-Core Version:    0.6.2
 */