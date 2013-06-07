/*    */ package com.sun.management.jmx;
/*    */ 
/*    */ import javax.management.MBeanInfo;
/*    */ import javax.management.NotCompliantMBeanException;
/*    */ 
/*    */ @Deprecated
/*    */ public class Introspector
/*    */ {
/*    */   @Deprecated
/*    */   public static synchronized MBeanInfo testCompliance(Class paramClass)
/*    */     throws NotCompliantMBeanException
/*    */   {
/* 51 */     return com.sun.jmx.mbeanserver.Introspector.testCompliance(paramClass);
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public static synchronized Class getMBeanInterface(Class paramClass)
/*    */   {
/* 67 */     return com.sun.jmx.mbeanserver.Introspector.getMBeanInterface(paramClass);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.management.jmx.Introspector
 * JD-Core Version:    0.6.2
 */