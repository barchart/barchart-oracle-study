/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class NoServant extends UserException
/*    */ {
/*    */   public NoServant()
/*    */   {
/* 16 */     super(NoServantHelper.id());
/*    */   }
/*    */ 
/*    */   public NoServant(String paramString)
/*    */   {
/* 22 */     super(NoServantHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.NoServant
 * JD-Core Version:    0.6.2
 */