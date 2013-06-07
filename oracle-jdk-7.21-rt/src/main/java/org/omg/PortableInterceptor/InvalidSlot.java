/*    */ package org.omg.PortableInterceptor;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class InvalidSlot extends UserException
/*    */ {
/*    */   public InvalidSlot()
/*    */   {
/* 16 */     super(InvalidSlotHelper.id());
/*    */   }
/*    */ 
/*    */   public InvalidSlot(String paramString)
/*    */   {
/* 22 */     super(InvalidSlotHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableInterceptor.InvalidSlot
 * JD-Core Version:    0.6.2
 */