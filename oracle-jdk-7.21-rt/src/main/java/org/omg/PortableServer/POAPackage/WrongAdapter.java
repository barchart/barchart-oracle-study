/*    */ package org.omg.PortableServer.POAPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class WrongAdapter extends UserException
/*    */ {
/*    */   public WrongAdapter()
/*    */   {
/* 16 */     super(WrongAdapterHelper.id());
/*    */   }
/*    */ 
/*    */   public WrongAdapter(String paramString)
/*    */   {
/* 22 */     super(WrongAdapterHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.POAPackage.WrongAdapter
 * JD-Core Version:    0.6.2
 */