/*    */ package org.omg.PortableServer.CurrentPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class NoContext extends UserException
/*    */ {
/*    */   public NoContext()
/*    */   {
/* 16 */     super(NoContextHelper.id());
/*    */   }
/*    */ 
/*    */   public NoContext(String paramString)
/*    */   {
/* 22 */     super(NoContextHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.CurrentPackage.NoContext
 * JD-Core Version:    0.6.2
 */