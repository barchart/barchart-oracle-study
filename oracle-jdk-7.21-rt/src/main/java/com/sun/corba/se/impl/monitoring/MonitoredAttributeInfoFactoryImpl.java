/*    */ package com.sun.corba.se.impl.monitoring;
/*    */ 
/*    */ import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;
/*    */ import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfoFactory;
/*    */ 
/*    */ public class MonitoredAttributeInfoFactoryImpl
/*    */   implements MonitoredAttributeInfoFactory
/*    */ {
/*    */   public MonitoredAttributeInfo createMonitoredAttributeInfo(String paramString, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
/*    */   {
/* 38 */     return new MonitoredAttributeInfoImpl(paramString, paramClass, paramBoolean1, paramBoolean2);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.monitoring.MonitoredAttributeInfoFactoryImpl
 * JD-Core Version:    0.6.2
 */