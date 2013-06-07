/*     */ package com.sun.org.glassfish.external.statistics.impl;
/*     */ 
/*     */ import com.sun.org.glassfish.external.statistics.StringStatistic;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class StringStatisticImpl extends StatisticImpl
/*     */   implements StringStatistic, InvocationHandler
/*     */ {
/*  39 */   private volatile String str = null;
/*     */   private final String initStr;
/*  42 */   private final StringStatistic ss = (InvocationHandler)Proxy.newProxyInstance(InvocationHandler.class.getClassLoader(), new Class[] { InvocationHandler.class }, this);
/*     */ 
/*     */   public StringStatisticImpl(String str, String name, String unit, String desc, long sampleTime, long startTime)
/*     */   {
/*  50 */     super(name, unit, desc, startTime, sampleTime);
/*  51 */     this.str = str;
/*  52 */     this.initStr = str;
/*     */   }
/*     */ 
/*     */   public StringStatisticImpl(String name, String unit, String desc) {
/*  56 */     this("", name, unit, desc, System.currentTimeMillis(), System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   public synchronized StringStatistic getStatistic() {
/*  60 */     return this.ss;
/*     */   }
/*     */ 
/*     */   public synchronized Map getStaticAsMap() {
/*  64 */     Map m = super.getStaticAsMap();
/*  65 */     if (getCurrent() != null) {
/*  66 */       m.put("current", getCurrent());
/*     */     }
/*  68 */     return m;
/*     */   }
/*     */ 
/*     */   public synchronized String toString() {
/*  72 */     return super.toString() + NEWLINE + "Current-value: " + getCurrent();
/*     */   }
/*     */ 
/*     */   public String getCurrent() {
/*  76 */     return this.str;
/*     */   }
/*     */ 
/*     */   public void setCurrent(String str) {
/*  80 */     this.str = str;
/*  81 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/*  86 */     super.reset();
/*  87 */     this.str = this.initStr;
/*  88 */     this.sampleTime = -1L;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
/*     */   {
/*     */     Object result;
/*     */     try {
/*  95 */       result = m.invoke(this, args); } catch (InvocationTargetException e) {
/*  96 */       e = 
/* 102 */         e;
/*     */ 
/*  97 */       throw e.getTargetException(); } catch (Exception e) {
/*  98 */       e = e;
/*     */ 
/*  99 */       throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
/*     */     }
/*     */     finally {
/*     */     }
/* 103 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.impl.StringStatisticImpl
 * JD-Core Version:    0.6.2
 */