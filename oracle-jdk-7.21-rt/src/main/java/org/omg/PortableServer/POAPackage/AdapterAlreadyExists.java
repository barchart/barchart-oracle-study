/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class AdapterAlreadyExists extends UserException
/*    */ {
/*    */   public AdapterAlreadyExists()
/*    */   {
/* 16 */     super(AdapterAlreadyExistsHelper.id());
/*    */   }
/*    */ 
/*    */   public AdapterAlreadyExists(String paramString)
/*    */   {
/* 22 */     super(AdapterAlreadyExistsHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.AdapterAlreadyExists
 * JD-Core Version:    0.6.2
 */