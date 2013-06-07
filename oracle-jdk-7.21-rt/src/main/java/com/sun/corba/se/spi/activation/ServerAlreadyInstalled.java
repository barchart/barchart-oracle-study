/*    */ package com.sun.corba.se.spi.activation;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class ServerAlreadyInstalled extends UserException
/*    */ {
/* 13 */   public int serverId = 0;
/*    */ 
/*    */   public ServerAlreadyInstalled()
/*    */   {
/* 17 */     super(ServerAlreadyInstalledHelper.id());
/*    */   }
/*    */ 
/*    */   public ServerAlreadyInstalled(int paramInt)
/*    */   {
/* 22 */     super(ServerAlreadyInstalledHelper.id());
/* 23 */     this.serverId = paramInt;
/*    */   }
/*    */ 
/*    */   public ServerAlreadyInstalled(String paramString, int paramInt)
/*    */   {
/* 29 */     super(ServerAlreadyInstalledHelper.id() + "  " + paramString);
/* 30 */     this.serverId = paramInt;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.ServerAlreadyInstalled
 * JD-Core Version:    0.6.2
 */