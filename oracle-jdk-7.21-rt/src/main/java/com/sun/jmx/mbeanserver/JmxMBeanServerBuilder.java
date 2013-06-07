/*     */ package com.sun.jmx.mbeanserver;
/*     */ 
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.MBeanServerBuilder;
/*     */ import javax.management.MBeanServerDelegate;
/*     */ 
/*     */ public class JmxMBeanServerBuilder extends MBeanServerBuilder
/*     */ {
/*     */   public MBeanServerDelegate newMBeanServerDelegate()
/*     */   {
/*  66 */     return JmxMBeanServer.newMBeanServerDelegate();
/*     */   }
/*     */ 
/*     */   public MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate)
/*     */   {
/* 110 */     return JmxMBeanServer.newMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, true);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.JmxMBeanServerBuilder
 * JD-Core Version:    0.6.2
 */