/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class ServantAlreadyActive extends UserException
/*    */ {
/*    */   public ServantAlreadyActive()
/*    */   {
/* 16 */     super(ServantAlreadyActiveHelper.id());
/*    */   }
/*    */ 
/*    */   public ServantAlreadyActive(String paramString)
/*    */   {
/* 22 */     super(ServantAlreadyActiveHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.ServantAlreadyActive
 * JD-Core Version:    0.6.2
 */