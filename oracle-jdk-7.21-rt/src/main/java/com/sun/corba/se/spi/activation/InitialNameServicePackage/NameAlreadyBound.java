/*    */ package com.sun.corba.se.spi.activation.InitialNameServicePackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class NameAlreadyBound extends UserException
/*    */ {
/*    */   public NameAlreadyBound()
/*    */   {
/* 16 */     super(NameAlreadyBoundHelper.id());
/*    */   }
/*    */ 
/*    */   public NameAlreadyBound(String paramString)
/*    */   {
/* 22 */     super(NameAlreadyBoundHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound
 * JD-Core Version:    0.6.2
 */