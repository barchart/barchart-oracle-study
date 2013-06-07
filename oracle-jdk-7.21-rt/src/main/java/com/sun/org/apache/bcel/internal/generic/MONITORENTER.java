/*    */ package com.sun.org.apache.bcel.internal.generic;
/*    */ 
/*    */ import com.sun.org.apache.bcel.internal.ExceptionConstants;
/*    */ 
/*    */ public class MONITORENTER extends Instruction
/*    */   implements ExceptionThrower, StackConsumer
/*    */ {
/*    */   public MONITORENTER()
/*    */   {
/* 70 */     super((short)194, (short)1);
/*    */   }
/*    */ 
/*    */   public Class[] getExceptions() {
/* 74 */     return new Class[] { ExceptionConstants.NULL_POINTER_EXCEPTION };
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v)
/*    */   {
/* 87 */     v.visitExceptionThrower(this);
/* 88 */     v.visitStackConsumer(this);
/* 89 */     v.visitMONITORENTER(this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.MONITORENTER
 * JD-Core Version:    0.6.2
 */