/*    */ package com.sun.jmx.snmp.IPAcl;
/*    */ 
/*    */ class JDMInformInterestedHost extends SimpleNode
/*    */ {
/*    */   JDMInformInterestedHost(int paramInt)
/*    */   {
/* 32 */     super(paramInt);
/*    */   }
/*    */ 
/*    */   JDMInformInterestedHost(Parser paramParser, int paramInt) {
/* 36 */     super(paramParser, paramInt);
/*    */   }
/*    */ 
/*    */   public static Node jjtCreate(int paramInt) {
/* 40 */     return new JDMInformInterestedHost(paramInt);
/*    */   }
/*    */ 
/*    */   public static Node jjtCreate(Parser paramParser, int paramInt) {
/* 44 */     return new JDMInformInterestedHost(paramParser, paramInt);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.IPAcl.JDMInformInterestedHost
 * JD-Core Version:    0.6.2
 */