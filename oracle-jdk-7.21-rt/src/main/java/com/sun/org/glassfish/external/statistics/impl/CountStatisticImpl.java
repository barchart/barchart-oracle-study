/*     */ package com.sun.org.glassfish.external.statistics.impl;
/*     */ 
/*     */ import com.sun.org.glassfish.external.statistics.CountStatistic;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class CountStatisticImpl extends StatisticImpl
/*     */   implements CountStatistic, InvocationHandler
/*     */ {
/*  39 */   private long count = 0L;
/*     */   private final long initCount;
/*  42 */   private final CountStatistic cs = (InvocationHandler)Proxy.newProxyInstance(InvocationHandler.class.getClassLoader(), new Class[] { InvocationHandler.class }, this);
/*     */ 
/*     */   public CountStatisticImpl(long countVal, String name, String unit, String desc, long sampleTime, long startTime)
/*     */   {
/*  50 */     super(name, unit, desc, startTime, sampleTime);
/*  51 */     this.count = countVal;
/*  52 */     this.initCount = countVal;
/*     */   }
/*     */ 
/*     */   public CountStatisticImpl(String name, String unit, String desc) {
/*  56 */     this(0L, name, unit, desc, -1L, System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   public synchronized CountStatistic getStatistic() {
/*  60 */     return this.cs;
/*     */   }
/*     */ 
/*     */   public synchronized Map getStaticAsMap() {
/*  64 */     Map m = super.getStaticAsMap();
/*  65 */     m.put("count", Long.valueOf(getCount()));
/*  66 */     return m;
/*     */   }
/*     */ 
/*     */   public synchronized String toString() {
/*  70 */     return super.toString() + NEWLINE + "Count: " + getCount();
/*     */   }
/*     */ 
/*     */   public synchronized long getCount() {
/*  74 */     return this.count;
/*     */   }
/*     */ 
/*     */   public synchronized void setCount(long countVal) {
/*  78 */     this.count = countVal;
/*  79 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized void increment() {
/*  83 */     this.count += 1L;
/*  84 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized void increment(long delta) {
/*  88 */     this.count += delta;
/*  89 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized void decrement() {
/*  93 */     this.count -= 1L;
/*  94 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/*  99 */     super.reset();
/* 100 */     this.count = this.initCount;
/* 101 */     this.sampleTime = -1L;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
/*     */   {
/*     */     Object result;
/*     */     try {
/* 108 */       result = m.invoke(this, args); } catch (InvocationTargetException e) {
/* 109 */       e = 
/* 115 */         e;
/*     */ 
/* 110 */       throw e.getTargetException(); } catch (Exception e) {
/* 111 */       e = e;
/*     */ 
/* 112 */       throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
/*     */     }
/*     */     finally {
/*     */     }
/* 116 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.impl.CountStatisticImpl
 * JD-Core Version:    0.6.2
 */