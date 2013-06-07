/*    */ package com.sun.jmx.snmp;
/*    */ 
/*    */ public class SnmpPduTrap extends SnmpPduPacket
/*    */ {
/*    */   private static final long serialVersionUID = -3670886636491433011L;
/*    */   public SnmpOid enterprise;
/*    */   public SnmpIpAddress agentAddr;
/*    */   public int genericTrap;
/*    */   public int specificTrap;
/*    */   public long timeStamp;
/*    */ 
/*    */   public SnmpPduTrap()
/*    */   {
/* 90 */     this.type = 164;
/* 91 */     this.version = 0;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpPduTrap
 * JD-Core Version:    0.6.2
 */