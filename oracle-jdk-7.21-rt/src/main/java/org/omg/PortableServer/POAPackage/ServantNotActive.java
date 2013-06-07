/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class ServantNotActive extends UserException
/*    */ {
/*    */   public ServantNotActive()
/*    */   {
/* 16 */     super(ServantNotActiveHelper.id());
/*    */   }
/*    */ 
/*    */   public ServantNotActive(String paramString)
/*    */   {
/* 22 */     super(ServantNotActiveHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.ServantNotActive
 * JD-Core Version:    0.6.2
 */