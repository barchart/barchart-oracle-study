/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class AdapterNonExistent extends UserException
/*    */ {
/*    */   public AdapterNonExistent()
/*    */   {
/* 16 */     super(AdapterNonExistentHelper.id());
/*    */   }
/*    */ 
/*    */   public AdapterNonExistent(String paramString)
/*    */   {
/* 22 */     super(AdapterNonExistentHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.AdapterNonExistent
 * JD-Core Version:    0.6.2
 */