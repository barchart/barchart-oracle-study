/*    */ package com.sun.org.apache.xerces.internal.impl.xs.util;
/*    */ 
/*    */ public final class XIntPool
/*    */ {
/*    */   private static final short POOL_SIZE = 10;
/* 30 */   private static final XInt[] fXIntPool = new XInt[10];
/*    */ 
/*    */   public final XInt getXInt(int value)
/*    */   {
/* 38 */     if ((value >= 0) && (value < fXIntPool.length)) {
/* 39 */       return fXIntPool[value];
/*    */     }
/* 41 */     return new XInt(value);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 33 */     for (int i = 0; i < 10; i++)
/* 34 */       fXIntPool[i] = new XInt(i);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.util.XIntPool
 * JD-Core Version:    0.6.2
 */