/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class FSTORE extends StoreInstruction
/*    */ {
/*    */   FSTORE()
/*    */   {
/* 73 */     super((short)56, (short)67);
/*    */   }
/*    */ 
/*    */   public FSTORE(int n)
/*    */   {
/* 80 */     super((short)56, (short)67, n);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 92 */     super.accept(v);
/* 93 */     v.visitFSTORE(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.FSTORE
 * JD-Core Version:    0.6.2
 */