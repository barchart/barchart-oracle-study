/*    */ package com.sun.corba.se.spi.activation;
/*    */ 
/*    */ import org.omg.CORBA.UserException;
/*    */ 
/*    */ public final class ServerAlreadyUninstalled extends UserException
/*    */ {
/* 13 */   public int serverId = 0;
/*    */ 
/*    */   public ServerAlreadyUninstalled()
/*    */   {
/* 17 */     super(ServerAlreadyUninstalledHelper.id());
/*    */   }
/*    */ 
/*    */   public ServerAlreadyUninstalled(int paramInt)
/*    */   {
/* 22 */     super(ServerAlreadyUninstalledHelper.id());
/* 23 */     this.serverId = paramInt;
/*    */   }
/*    */ 
/*    */   public ServerAlreadyUninstalled(String paramString, int paramInt)
/*    */   {
/* 29 */     super(ServerAlreadyUninstalledHelper.id() + "  " + paramString);
/* 30 */     this.serverId = paramInt;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.ServerAlreadyUninstalled
 * JD-Core Version:    0.6.2
 */