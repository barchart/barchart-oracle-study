/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class ILOAD extends LoadInstruction
/*    */ {
/*    */   ILOAD()
/*    */   {
/* 73 */     super((short)21, (short)26);
/*    */   }
/*    */ 
/*    */   public ILOAD(int n)
/*    */   {
/* 80 */     super((short)21, (short)26, n);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 92 */     super.accept(v);
/* 93 */     v.visitILOAD(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.ILOAD
 * JD-Core Version:    0.6.2
 */