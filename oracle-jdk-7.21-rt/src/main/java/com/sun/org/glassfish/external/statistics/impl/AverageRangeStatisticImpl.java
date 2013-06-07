/*     */ package com.sun.org.glassfish.external.statistics.impl;
/*     */ 
/*     */ import com.sun.org.glassfish.external.statistics.AverageRangeStatistic;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class AverageRangeStatisticImpl extends StatisticImpl
/*     */   implements AverageRangeStatistic, InvocationHandler
/*     */ {
/*  46 */   private long currentVal = 0L;
/*  47 */   private long highWaterMark = -9223372036854775808L;
/*  48 */   private long lowWaterMark = 9223372036854775807L;
/*  49 */   private long numberOfSamples = 0L;
/*  50 */   private long runningTotal = 0L;
/*     */   private final long initCurrentVal;
/*     */   private final long initHighWaterMark;
/*     */   private final long initLowWaterMark;
/*     */   private final long initNumberOfSamples;
/*     */   private final long initRunningTotal;
/*  58 */   private final AverageRangeStatistic as = (InvocationHandler)Proxy.newProxyInstance(InvocationHandler.class.getClassLoader(), new Class[] { InvocationHandler.class }, this);
/*     */ 
/*     */   public AverageRangeStatisticImpl(long curVal, long highMark, long lowMark, String name, String unit, String desc, long startTime, long sampleTime)
/*     */   {
/*  67 */     super(name, unit, desc, startTime, sampleTime);
/*  68 */     this.currentVal = curVal;
/*  69 */     this.initCurrentVal = curVal;
/*  70 */     this.highWaterMark = highMark;
/*  71 */     this.initHighWaterMark = highMark;
/*  72 */     this.lowWaterMark = lowMark;
/*  73 */     this.initLowWaterMark = lowMark;
/*  74 */     this.numberOfSamples = 0L;
/*  75 */     this.initNumberOfSamples = this.numberOfSamples;
/*  76 */     this.runningTotal = 0L;
/*  77 */     this.initRunningTotal = this.runningTotal;
/*     */   }
/*     */ 
/*     */   public synchronized AverageRangeStatistic getStatistic() {
/*  81 */     return this.as;
/*     */   }
/*     */ 
/*     */   public synchronized String toString() {
/*  85 */     return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark() + NEWLINE + "Average:" + getAverage();
/*     */   }
/*     */ 
/*     */   public synchronized Map getStaticAsMap()
/*     */   {
/*  93 */     Map m = super.getStaticAsMap();
/*  94 */     m.put("current", Long.valueOf(getCurrent()));
/*  95 */     m.put("lowwatermark", Long.valueOf(getLowWaterMark()));
/*  96 */     m.put("highwatermark", Long.valueOf(getHighWaterMark()));
/*  97 */     m.put("average", Long.valueOf(getAverage()));
/*  98 */     return m;
/*     */   }
/*     */ 
/*     */   public synchronized void reset() {
/* 102 */     super.reset();
/* 103 */     this.currentVal = this.initCurrentVal;
/* 104 */     this.highWaterMark = this.initHighWaterMark;
/* 105 */     this.lowWaterMark = this.initLowWaterMark;
/* 106 */     this.numberOfSamples = this.initNumberOfSamples;
/* 107 */     this.runningTotal = this.initRunningTotal;
/* 108 */     this.sampleTime = -1L;
/*     */   }
/*     */ 
/*     */   public synchronized long getAverage() {
/* 112 */     if (this.numberOfSamples == 0L) {
/* 113 */       return -1L;
/*     */     }
/* 115 */     return this.runningTotal / this.numberOfSamples;
/*     */   }
/*     */ 
/*     */   public synchronized long getCurrent()
/*     */   {
/* 120 */     return this.currentVal;
/*     */   }
/*     */ 
/*     */   public synchronized void setCurrent(long curVal) {
/* 124 */     this.currentVal = curVal;
/* 125 */     this.lowWaterMark = (curVal >= this.lowWaterMark ? this.lowWaterMark : curVal);
/* 126 */     this.highWaterMark = (curVal >= this.highWaterMark ? curVal : this.highWaterMark);
/* 127 */     this.numberOfSamples += 1L;
/* 128 */     this.runningTotal += curVal;
/* 129 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized long getHighWaterMark() {
/* 133 */     return this.highWaterMark;
/*     */   }
/*     */ 
/*     */   public synchronized long getLowWaterMark() {
/* 137 */     return this.lowWaterMark;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */   {
/*     */     Object result;
/*     */     try {
/* 144 */       result = method.invoke(this, args); } catch (InvocationTargetException e) {
/* 145 */       e = 
/* 151 */         e;
/*     */ 
/* 146 */       throw e.getTargetException(); } catch (Exception e) {
/* 147 */       e = e;
/*     */ 
/* 148 */       throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
/*     */     }
/*     */     finally {
/*     */     }
/* 152 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.impl.AverageRangeStatisticImpl
 * JD-Core Version:    0.6.2
 */