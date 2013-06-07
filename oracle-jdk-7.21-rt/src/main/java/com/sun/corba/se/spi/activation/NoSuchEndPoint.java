/*    */ package com.sun.corba.se.spi.activation;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class NoSuchEndPoint extends UserException
/*    */ {
/*    */   public NoSuchEndPoint()
/*    */   {
/* 16 */     super(NoSuchEndPointHelper.id());
/*    */   }
/*    */ 
/*    */   public NoSuchEndPoint(String paramString)
/*    */   {
/* 22 */     super(NoSuchEndPointHelper.id() + "  " + paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.NoSuchEndPoint
 * JD-Core Version:    0.6.2
 */