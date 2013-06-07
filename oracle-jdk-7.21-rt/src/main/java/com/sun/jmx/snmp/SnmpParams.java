/*    */ package com.sun.jmx.snmp;
/*    */ 
/*    */ public abstract class SnmpParams
/*    */   implements SnmpDefinitions
/*    */ {
/* 36 */   private int protocolVersion = 0;
/*    */ 
/* 38 */   SnmpParams(int paramInt) { this.protocolVersion = paramInt; }
/*    */ 
/*    */ 
/*    */   SnmpParams()
/*    */   {
/*    */   }
/*    */ 
/*    */   public abstract boolean allowSnmpSets();
/*    */ 
/*    */   public int getProtocolVersion()
/*    */   {
/* 58 */     return this.protocolVersion;
/*    */   }
/*    */ 
/*    */   public void setProtocolVersion(int paramInt)
/*    */   {
/* 74 */     this.protocolVersion = paramInt;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpParams
 * JD-Core Version:    0.6.2
 */