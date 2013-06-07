/*    */ package com.sun.jmx.snmp.daemon;
/*    */ 
/*    */ final class SnmpRequestCounter
/*    */ {
/* 22 */   int reqid = 0;
/*    */ 
/*    */   public synchronized int getNewId()
/*    */   {
/* 31 */     if (++this.reqid < 0)
/* 32 */       this.reqid = 1;
/* 33 */     return this.reqid;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.daemon.SnmpRequestCounter
 * JD-Core Version:    0.6.2
 */