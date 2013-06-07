/*    */ package org.omg.CORBA;
/*    */ 
/*    */ import org.omg.CORBA.portable.ObjectImpl;
/*    */ 
/*    */ @Deprecated
/*    */ public class DynamicImplementation extends ObjectImpl
/*    */ {
/*    */   @Deprecated
/*    */   public void invoke(ServerRequest paramServerRequest)
/*    */   {
/* 41 */     throw new NO_IMPLEMENT();
/*    */   }
/*    */ 
/*    */   public String[] _ids() {
/* 45 */     throw new NO_IMPLEMENT();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.DynamicImplementation
 * JD-Core Version:    0.6.2
 */