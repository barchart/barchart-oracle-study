/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ import com.sun.org.apache.bcel.internal.ExceptionConstants;
/*    */ 
/*    */ public class ATHROW extends Instruction
/*    */   implements UnconditionalBranch, ExceptionThrower
/*    */ {
/*    */   public ATHROW()
/*    */   {
/* 72 */     super((short)191, (short)1);
/*    */   }
/*    */ 
/*    */   public Class[] getExceptions()
/*    */   {
/* 78 */     return new Class[] { ExceptionConstants.THROWABLE };
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 91 */     v.visitUnconditionalBranch(this);
/* 92 */     v.visitExceptionThrower(this);
/* 93 */     v.visitATHROW(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.ATHROW
 * JD-Core Version:    0.6.2
 */