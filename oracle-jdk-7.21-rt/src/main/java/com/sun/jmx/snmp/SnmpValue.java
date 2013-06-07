/*    */ package com.sun.jmx.snmp;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public abstract class SnmpValue
/*    */   implements Cloneable, Serializable, SnmpDataTypeEnums
/*    */ {
/*    */   public String toAsn1String()
/*    */   {
/* 49 */     return "[" + getTypeName() + "] " + toString();
/*    */   }
/*    */ 
/*    */   public abstract SnmpOid toOid();
/*    */ 
/*    */   public abstract String getTypeName();
/*    */ 
/*    */   public abstract SnmpValue duplicate();
/*    */ 
/*    */   public boolean isNoSuchObjectValue()
/*    */   {
/* 78 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isNoSuchInstanceValue()
/*    */   {
/* 86 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isEndOfMibViewValue()
/*    */   {
/* 94 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpValue
 * JD-Core Version:    0.6.2
 */