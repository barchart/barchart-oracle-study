/*     */ package com.sun.org.glassfish.external.statistics.impl;
/*     */ 
/*     */ import com.sun.org.glassfish.external.statistics.TimeStatistic;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class TimeStatisticImpl extends StatisticImpl
/*     */   implements TimeStatistic, InvocationHandler
/*     */ {
/*  41 */   private long count = 0L;
/*  42 */   private long maxTime = 0L;
/*  43 */   private long minTime = 0L;
/*  44 */   private long totTime = 0L;
/*     */   private final long initCount;
/*     */   private final long initMaxTime;
/*     */   private final long initMinTime;
/*     */   private final long initTotTime;
/*  50 */   private final TimeStatistic ts = (InvocationHandler)Proxy.newProxyInstance(InvocationHandler.class.getClassLoader(), new Class[] { InvocationHandler.class }, this);
/*     */ 
/*     */   public final synchronized String toString()
/*     */   {
/*  57 */     return super.toString() + NEWLINE + "Count: " + getCount() + NEWLINE + "MinTime: " + getMinTime() + NEWLINE + "MaxTime: " + getMaxTime() + NEWLINE + "TotalTime: " + getTotalTime();
/*     */   }
/*     */ 
/*     */   public TimeStatisticImpl(long counter, long maximumTime, long minimumTime, long totalTime, String name, String unit, String desc, long startTime, long sampleTime)
/*     */   {
/*  67 */     super(name, unit, desc, startTime, sampleTime);
/*  68 */     this.count = counter;
/*  69 */     this.initCount = counter;
/*  70 */     this.maxTime = maximumTime;
/*  71 */     this.initMaxTime = maximumTime;
/*  72 */     this.minTime = minimumTime;
/*  73 */     this.initMinTime = minimumTime;
/*  74 */     this.totTime = totalTime;
/*  75 */     this.initTotTime = totalTime;
/*     */   }
/*     */ 
/*     */   public synchronized TimeStatistic getStatistic() {
/*  79 */     return this.ts;
/*     */   }
/*     */ 
/*     */   public synchronized Map getStaticAsMap() {
/*  83 */     Map m = super.getStaticAsMap();
/*  84 */     m.put("count", Long.valueOf(getCount()));
/*  85 */     m.put("maxtime", Long.valueOf(getMaxTime()));
/*  86 */     m.put("mintime", Long.valueOf(getMinTime()));
/*  87 */     m.put("totaltime", Long.valueOf(getTotalTime()));
/*  88 */     return m;
/*     */   }
/*     */ 
/*     */   public synchronized void incrementCount(long current) {
/*  92 */     if (this.count == 0L) {
/*  93 */       this.totTime = current;
/*  94 */       this.maxTime = current;
/*  95 */       this.minTime = current;
/*     */     } else {
/*  97 */       this.totTime += current;
/*  98 */       this.maxTime = (current >= this.maxTime ? current : this.maxTime);
/*  99 */       this.minTime = (current >= this.minTime ? this.minTime : current);
/*     */     }
/* 101 */     this.count += 1L;
/* 102 */     this.sampleTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public synchronized long getCount()
/*     */   {
/* 109 */     return this.count;
/*     */   }
/*     */ 
/*     */   public synchronized long getMaxTime()
/*     */   {
/* 117 */     return this.maxTime;
/*     */   }
/*     */ 
/*     */   public synchronized long getMinTime()
/*     */   {
/* 125 */     return this.minTime;
/*     */   }
/*     */ 
/*     */   public synchronized long getTotalTime()
/*     */   {
/* 133 */     return this.totTime;
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 138 */     super.reset();
/* 139 */     this.count = this.initCount;
/* 140 */     this.maxTime = this.initMaxTime;
/* 141 */     this.minTime = this.initMinTime;
/* 142 */     this.totTime = this.initTotTime;
/* 143 */     this.sampleTime = -1L;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
/*     */   {
/*     */     Object result;
/*     */     try {
/* 150 */       result = m.invoke(this, args); } catch (InvocationTargetException e) {
/* 151 */       e = 
/* 157 */         e;
/*     */ 
/* 152 */       throw e.getTargetException(); } catch (Exception e) {
/* 153 */       e = e;
/*     */ 
/* 154 */       throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
/*     */     }
/*     */     finally {
/*     */     }
/* 158 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.impl.TimeStatisticImpl
 * JD-Core Version:    0.6.2
 */