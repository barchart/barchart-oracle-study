/*    */ package org.omg.CORBA;
/*    */ 
/*    */ import org.omg.CORBA.portable.IDLEntity;
/*    */ 
/*    */ public abstract class UserException extends Exception
/*    */   implements IDLEntity
/*    */ {
/*    */   protected UserException()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected UserException(String paramString)
/*    */   {
/* 55 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.UserException
 * JD-Core Version:    0.6.2
 */