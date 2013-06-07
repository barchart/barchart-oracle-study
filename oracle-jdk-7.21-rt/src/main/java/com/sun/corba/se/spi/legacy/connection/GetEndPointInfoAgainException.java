/*    */ package com.sun.corba.se.spi.legacy.connection;
/*    */ 
/*    */ import com.sun.corba.se.spi.transport.SocketInfo;
/*    */ 
/*    */ public class GetEndPointInfoAgainException extends Exception
/*    */ {
/*    */   private SocketInfo socketInfo;
/*    */ 
/*    */   public GetEndPointInfoAgainException(SocketInfo paramSocketInfo)
/*    */   {
/* 45 */     this.socketInfo = paramSocketInfo;
/*    */   }
/*    */ 
/*    */   public SocketInfo getEndPointInfo()
/*    */   {
/* 50 */     return this.socketInfo;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException
 * JD-Core Version:    0.6.2
 */