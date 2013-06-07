/*    */ package com.sun.org.glassfish.external.statistics.impl;
/*    */ 
/*    */ import com.sun.org.glassfish.external.statistics.BoundaryStatistic;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.lang.reflect.Proxy;
/*    */ import java.util.Map;
/*    */ 
/*    */ public final class BoundaryStatisticImpl extends StatisticImpl
/*    */   implements BoundaryStatistic, InvocationHandler
/*    */ {
/*    */   private final long lowerBound;
/*    */   private final long upperBound;
/* 43 */   private final BoundaryStatistic bs = (InvocationHandler)Proxy.newProxyInstance(InvocationHandler.class.getClassLoader(), new Class[] { InvocationHandler.class }, this);
/*    */ 
/*    */   public BoundaryStatisticImpl(long lower, long upper, String name, String unit, String desc, long startTime, long sampleTime)
/*    */   {
/* 52 */     super(name, unit, desc, startTime, sampleTime);
/* 53 */     this.upperBound = upper;
/* 54 */     this.lowerBound = lower;
/*    */   }
/*    */ 
/*    */   public synchronized BoundaryStatistic getStatistic() {
/* 58 */     return this.bs;
/*    */   }
/*    */ 
/*    */   public synchronized Map getStaticAsMap() {
/* 62 */     Map m = super.getStaticAsMap();
/* 63 */     m.put("lowerbound", Long.valueOf(getLowerBound()));
/* 64 */     m.put("upperbound", Long.valueOf(getUpperBound()));
/* 65 */     return m;
/*    */   }
/*    */ 
/*    */   public synchronized long getLowerBound() {
/* 69 */     return this.lowerBound;
/*    */   }
/*    */ 
/*    */   public synchronized long getUpperBound() {
/* 73 */     return this.upperBound;
/*    */   }
/*    */ 
/*    */   public synchronized void reset()
/*    */   {
/* 78 */     super.reset();
/* 79 */     this.sampleTime = -1L;
/*    */   }
/*    */ 
/*    */   public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
/*    */   {
/*    */     Object result;
/*    */     try {
/* 86 */       result = m.invoke(this, args); } catch (InvocationTargetException e) {
/* 87 */       e = 
/* 93 */         e;
/*    */ 
/* 88 */       throw e.getTargetException(); } catch (Exception e) {
/* 89 */       e = e;
/*    */ 
/* 90 */       throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
/*    */     }
/*    */     finally {
/*    */     }
/* 94 */     return result;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.impl.BoundaryStatisticImpl
 * JD-Core Version:    0.6.2
 */