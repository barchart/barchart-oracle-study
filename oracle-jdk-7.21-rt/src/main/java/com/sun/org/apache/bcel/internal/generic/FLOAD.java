/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class FLOAD extends LoadInstruction
/*    */ {
/*    */   FLOAD()
/*    */   {
/* 73 */     super((short)23, (short)34);
/*    */   }
/*    */ 
/*    */   public FLOAD(int n)
/*    */   {
/* 80 */     super((short)23, (short)34, n);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 92 */     super.accept(v);
/* 93 */     v.visitFLOAD(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.FLOAD
 * JD-Core Version:    0.6.2
 */