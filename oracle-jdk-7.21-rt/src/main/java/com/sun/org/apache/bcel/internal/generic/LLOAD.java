/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class LLOAD extends LoadInstruction
/*    */ {
/*    */   LLOAD()
/*    */   {
/* 73 */     super((short)22, (short)30);
/*    */   }
/*    */ 
/*    */   public LLOAD(int n) {
/* 77 */     super((short)22, (short)30, n);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 89 */     super.accept(v);
/* 90 */     v.visitLLOAD(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.LLOAD
 * JD-Core Version:    0.6.2
 */