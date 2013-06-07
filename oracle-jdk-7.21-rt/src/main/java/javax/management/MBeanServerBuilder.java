/*     */ package javax.management;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.JmxMBeanServer;
/*     */ 
/*     */ public class MBeanServerBuilder
/*     */ {
/*     */   public MBeanServerDelegate newMBeanServerDelegate()
/*     */   {
/*  66 */     return JmxMBeanServer.newMBeanServerDelegate();
/*     */   }
/*     */ 
/*     */   public MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate)
/*     */   {
/* 110 */     return JmxMBeanServer.newMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, false);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanServerBuilder
 * JD-Core Version:    0.6.2
 */