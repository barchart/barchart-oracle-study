/*    */ package com.sun.corba.se.spi.monitoring;
/*    */ 
/*    */ public abstract class LongMonitoredAttributeBase extends MonitoredAttributeBase
/*    */ {
/*    */   public LongMonitoredAttributeBase(String paramString1, String paramString2)
/*    */   {
/* 61 */     super(paramString1);
/* 62 */     MonitoredAttributeInfoFactory localMonitoredAttributeInfoFactory = MonitoringFactories.getMonitoredAttributeInfoFactory();
/*    */ 
/* 64 */     MonitoredAttributeInfo localMonitoredAttributeInfo = localMonitoredAttributeInfoFactory.createMonitoredAttributeInfo(paramString2, Long.class, false, false);
/*    */ 
/* 66 */     setMonitoredAttributeInfo(localMonitoredAttributeInfo);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase
 * JD-Core Version:    0.6.2
 */