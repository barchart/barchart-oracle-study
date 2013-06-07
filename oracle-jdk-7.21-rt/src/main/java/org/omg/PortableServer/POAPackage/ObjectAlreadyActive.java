/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class ObjectAlreadyActive extends UserException
/*    */ {
/*    */   public ObjectAlreadyActive()
/*    */   {
/* 16 */     super(ObjectAlreadyActiveHelper.id());
/*    */   }
/*    */ 
/*    */   public ObjectAlreadyActive(String paramString)
/*    */   {
/* 22 */     super(ObjectAlreadyActiveHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.ObjectAlreadyActive
 * JD-Core Version:    0.6.2
 */