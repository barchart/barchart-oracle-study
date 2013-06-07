/*    */ package com.sun.corba.se.spi.activation.LocatorPackage;
/*    */ 
/*    */ import com.sun.corba.se.spi.activation.ORBPortInfo;
/*    */ import org.omg.CORBA.portable.IDLEntity;
/*    */ 
/*    */ public final class ServerLocation
/*    */   implements IDLEntity
/*    */ {
/* 13 */   public String hostname = null;
/* 14 */   public ORBPortInfo[] ports = null;
/*    */ 
/*    */   public ServerLocation()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ServerLocation(String paramString, ORBPortInfo[] paramArrayOfORBPortInfo)
/*    */   {
/* 22 */     this.hostname = paramString;
/* 23 */     this.ports = paramArrayOfORBPortInfo;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation
 * JD-Core Version:    0.6.2
 */