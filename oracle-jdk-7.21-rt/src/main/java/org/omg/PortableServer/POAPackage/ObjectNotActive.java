/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class ObjectNotActive extends UserException
/*    */ {
/*    */   public ObjectNotActive()
/*    */   {
/* 16 */     super(ObjectNotActiveHelper.id());
/*    */   }
/*    */ 
/*    */   public ObjectNotActive(String paramString)
/*    */   {
/* 22 */     super(ObjectNotActiveHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.ObjectNotActive
 * JD-Core Version:    0.6.2
 */