/*    */ package com.sun.management;
/*    */ 
/*    */ import java.lang.management.OperatingSystemMXBean;
/*    */ import sun.management.VMManagement;
/*    */ 
/*    */ public class OSMBeanFactory
/*    */ {
/* 42 */   private static UnixOperatingSystem osMBean = null;
/*    */ 
/*    */   public static synchronized OperatingSystemMXBean getOperatingSystemMXBean(VMManagement paramVMManagement)
/*    */   {
/* 47 */     if (osMBean == null) {
/* 48 */       osMBean = new UnixOperatingSystem(paramVMManagement);
/*    */     }
/* 50 */     return osMBean;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.management.OSMBeanFactory
 * JD-Core Version:    0.6.2
 */