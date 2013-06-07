/*    */ package org.omg.PortableInterceptor.ORBInitInfoPackage;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class InvalidName extends UserException
/*    */ {
/*    */   public InvalidName()
/*    */   {
/* 16 */     super(InvalidNameHelper.id());
/*    */   }
/*    */ 
/*    */   public InvalidName(String paramString)
/*    */   {
/* 22 */     super(InvalidNameHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName
 * JD-Core Version:    0.6.2
 */