/*    */ package com.sun.corba.se.impl.corba;
/*    */ 
/*    */ import org.omg.CORBA.Environment;
/*    */ 
/*    */ public class EnvironmentImpl extends Environment
/*    */ {
/*    */   private Exception _exc;
/*    */ 
/*    */   public Exception exception()
/*    */   {
/* 48 */     return this._exc;
/*    */   }
/*    */ 
/*    */   public void exception(Exception paramException)
/*    */   {
/* 53 */     this._exc = paramException;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 58 */     this._exc = null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.corba.EnvironmentImpl
 * JD-Core Version:    0.6.2
 */