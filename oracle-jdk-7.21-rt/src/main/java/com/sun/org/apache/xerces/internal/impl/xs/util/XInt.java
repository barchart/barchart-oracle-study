/*    */ package com.sun.org.apache.xerces.internal.impl.xs.util;
/*    */ 
/*    */ public final class XInt
/*    */ {
/*    */   private int fValue;
/*    */ 
/*    */   XInt(int value)
/*    */   {
/* 34 */     this.fValue = value;
/*    */   }
/*    */ 
/*    */   public final int intValue() {
/* 38 */     return this.fValue;
/*    */   }
/*    */ 
/*    */   public final short shortValue() {
/* 42 */     return (short)this.fValue;
/*    */   }
/*    */ 
/*    */   public final boolean equals(XInt compareVal) {
/* 46 */     return this.fValue == compareVal.fValue;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 50 */     return Integer.toString(this.fValue);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.util.XInt
 * JD-Core Version:    0.6.2
 */