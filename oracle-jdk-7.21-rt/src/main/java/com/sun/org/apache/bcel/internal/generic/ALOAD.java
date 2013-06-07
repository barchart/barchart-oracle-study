/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ public class ALOAD extends LoadInstruction
/*    */ {
/*    */   ALOAD()
/*    */   {
/* 73 */     super((short)25, (short)42);
/*    */   }
/*    */ 
/*    */   public ALOAD(int n)
/*    */   {
/* 80 */     super((short)25, (short)42, n);
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 92 */     super.accept(v);
/* 93 */     v.visitALOAD(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.ALOAD
 * JD-Core Version:    0.6.2
 */