/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class LSTORE extends StoreInstruction
/*    */ {
/*    */   LSTORE()
/*    */   {
/* 73 */     super((short)55, (short)63);
/*    */   }
/*    */ 
/*    */   public LSTORE(int n) {
/* 77 */     super((short)55, (short)63, n);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 89 */     super.accept(v);
/* 90 */     v.visitLSTORE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.LSTORE
 * JD-Core Version:    0.6.2
 */