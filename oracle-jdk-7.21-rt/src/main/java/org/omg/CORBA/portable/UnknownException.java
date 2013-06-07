/*    */ package org.omg.CORBA.portable;
/*    */ 
/*    */ import org.omg.CORBA.CompletionStatus;
/*    */ import org.omg.CORBA.SystemException;
/*    */ 
/*    */ public class UnknownException extends SystemException
/*    */ {
/*    */   public Throwable originalEx;
/*    */ 
/*    */   public UnknownException(Throwable paramThrowable)
/*    */   {
/* 55 */     super("", 0, CompletionStatus.COMPLETED_MAYBE);
/* 56 */     this.originalEx = paramThrowable;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.portable.UnknownException
 * JD-Core Version:    0.6.2
 */