/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class WrongPolicy extends UserException
/*    */ {
/*    */   public WrongPolicy()
/*    */   {
/* 16 */     super(WrongPolicyHelper.id());
/*    */   }
/*    */ 
/*    */   public WrongPolicy(String paramString)
/*    */   {
/* 22 */     super(WrongPolicyHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.WrongPolicy
 * JD-Core Version:    0.6.2
 */