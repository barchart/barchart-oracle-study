/*    */ package com.sun.jmx.snmp.IPAcl;
/*    */ 
/*    */ class JDMHostInform extends SimpleNode
/*    */ {
/* 31 */   protected String name = "";
/*    */ 
/*    */   JDMHostInform(int paramInt) {
/* 34 */     super(paramInt);
/*    */   }
/*    */ 
/*    */   JDMHostInform(Parser paramParser, int paramInt) {
/* 38 */     super(paramParser, paramInt);
/*    */   }
/*    */ 
/*    */   public static Node jjtCreate(int paramInt) {
/* 42 */     return new JDMHostInform(paramInt);
/*    */   }
/*    */ 
/*    */   public static Node jjtCreate(Parser paramParser, int paramInt) {
/* 46 */     return new JDMHostInform(paramParser, paramInt);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.IPAcl.JDMHostInform
 * JD-Core Version:    0.6.2
 */