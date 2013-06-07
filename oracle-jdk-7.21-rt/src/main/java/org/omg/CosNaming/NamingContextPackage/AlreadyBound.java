/*    */ package org.omg.CosNaming.NamingContextPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class AlreadyBound extends UserException
/*    */ {
/*    */   public AlreadyBound()
/*    */   {
/* 16 */     super(AlreadyBoundHelper.id());
/*    */   }
/*    */ 
/*    */   public AlreadyBound(String paramString)
/*    */   {
/* 22 */     super(AlreadyBoundHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CosNaming.NamingContextPackage.AlreadyBound
 * JD-Core Version:    0.6.2
 */