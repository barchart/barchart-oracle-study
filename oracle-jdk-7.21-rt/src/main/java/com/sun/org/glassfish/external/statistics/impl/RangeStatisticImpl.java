/*     */ package com.sun.org.glassfish.external.statistics.impl;
/*     */ 
/*     */ import com.sun.org.glassfish.external.statistics.RangeStatistic;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class RangeStatisticImpl extends StatisticImpl
/*     */   implements RangeStatistic, InvocationHandler
/*     */ {
/*  40 */   private long currentVal = 0L;
/*  41 */   private long highWaterMark = -9223372036854775808L;
/*  42 */   private long lowWaterMark = 9223372036854775807L;
/*     */   private final long initCurrentVal;
/*     */   private final long initHighWaterMark;
/*     */   private final long initLowWaterMark;
/*  47 */   private final RangeStatistic rs = (InvocationHandler)Proxy.newProxyInstance(InvocationHandler.class.getClassLoader(), new Class[] { InvocationHandler.class }, this);
/*     */ 
/*     */   public RangeStatisticImpl(long curVal, long highMark, long lowMark, String name, String unit, String desc, long startTime, long sampleTime)
/*     */   {
/*  56 */     super(name, unit, desc, startTime, sampleTime);
/*  57 */     this.currentVal = curVal;
/*  58 */     this.initCurrentVal = curVal;
/*  59 */     this.highWaterMark = highMark;
/*  60 */     this.initHighWaterMark = highMark;
/*  61 */     this.lowWaterMark = lowMark;
/*  62 */     this.initLowWaterMark = lowMark;
/*     */   }
/*     */ 
/*     */   public synchronized RangeStatistic getStatistic() {
/*  66 */     return this.rs;
/*     */   }
/*     */ 
/*     */   public synchronized Map getStaticAsMap() {
/*  70 */     Map m = super.getStaticAsMap();
/*  71 */     m.put("current", Long.valueOf(getCurrent()));
/*  72 */     m.put("lowwatermark", Long.valueOf(getLowWaterMark()));
/*  73 */     m.put("highwatermark", Long.valueOf(getHighWaterMark()));
/*  74 */     return m;
/*     */   }
/*     */ 
/*     */   public synchronized long getCurrent() {
/*  78 */     return this.currentVal;
/*     */   }
/*     */ 
/*     */   public synchronized void setCurrent(long curVal) {
/*  82 */     this.currentVal = curVal;
/*  83 */     this.lowWaterMark = (curVal >= this.lowWaterMark ? this.lowWaterMark : curVal);
/*  84 */     this.highWaterMark = (curVal >= this.highWaterMark ? curVal : this.highWaterMark);
/*  85 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized long getHighWaterMark()
/*     */   {
/*  92 */     return this.highWaterMark;
/*     */   }
/*     */ 
/*     */   public synchronized void setHighWaterMark(long hwm) {
/*  96 */     this.highWaterMark = hwm;
/*     */   }
/*     */ 
/*     */   public synchronized long getLowWaterMark()
/*     */   {
/* 103 */     return this.lowWaterMark;
/*     */   }
/*     */ 
/*     */   public synchronized void setLowWaterMark(long lwm) {
/* 107 */     this.lowWaterMark = lwm;
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 112 */     super.reset();
/* 113 */     this.currentVal = this.initCurrentVal;
/* 114 */     this.highWaterMark = this.initHighWaterMark;
/* 115 */     this.lowWaterMark = this.initLowWaterMark;
/* 116 */     this.sampleTime = -1L;
/*     */   }
/*     */ 
/*     */   public synchronized String toString() {
/* 120 */     return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark();
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method m, Object[] args)
/*     */     throws Throwable
/*     */   {
/*     */     Object result;
/*     */     try
/*     */     {
/* 130 */       result = m.invoke(this, args); } catch (InvocationTargetException e) {
/* 131 */       e = 
/* 137 */         e;
/*     */ 
/* 132 */       throw e.getTargetException(); } catch (Exception e) {
/* 133 */       e = e;
/*     */ 
/* 134 */       throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
/*     */     }
/*     */     finally {
/*     */     }
/* 138 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.impl.RangeStatisticImpl
 * JD-Core Version:    0.6.2
 */