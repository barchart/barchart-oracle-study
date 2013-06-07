/*    */ package com.sun.jmx.snmp;
/*    */ 
/*    */ public class SnmpTooBigException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 4754796246674803969L;
/*    */   private int varBindCount;
/*    */ 
/*    */   public SnmpTooBigException()
/*    */   {
/* 54 */     this.varBindCount = 0;
/*    */   }
/*    */ 
/*    */   public SnmpTooBigException(int paramInt)
/*    */   {
/* 63 */     this.varBindCount = paramInt;
/*    */   }
/*    */ 
/*    */   public int getVarBindCount()
/*    */   {
/* 74 */     return this.varBindCount;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpTooBigException
 * JD-Core Version:    0.6.2
 */