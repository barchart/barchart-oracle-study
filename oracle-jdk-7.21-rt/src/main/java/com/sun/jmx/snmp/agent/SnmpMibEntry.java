/*     */ package com.sun.jmx.snmp.agent;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public abstract class SnmpMibEntry extends SnmpMibNode
/*     */   implements Serializable
/*     */ {
/*     */   public abstract boolean isVariable(long paramLong);
/*     */ 
/*     */   public abstract boolean isReadable(long paramLong);
/*     */ 
/*     */   public long getNextVarId(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/*  82 */     long l = super.getNextVarId(paramLong, paramObject);
/*  83 */     while (!isReadable(l))
/*  84 */       l = super.getNextVarId(l, paramObject);
/*  85 */     return l;
/*     */   }
/*     */ 
/*     */   public void validateVarId(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 102 */     if (!isVariable(paramLong)) throw noSuchNameException;
/*     */   }
/*     */ 
/*     */   public abstract void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException;
/*     */ 
/*     */   public abstract void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException;
/*     */ 
/*     */   public abstract void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException;
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.agent.SnmpMibEntry
 * JD-Core Version:    0.6.2
 */