/*    */ package com.sun.xml.internal.fastinfoset.util;
/*    */ 
/*    */ public abstract class ValueArray
/*    */ {
/*    */   public static final int DEFAULT_CAPACITY = 10;
/*    */   public static final int MAXIMUM_CAPACITY = 2147483647;
/*    */   protected int _size;
/*    */   protected int _readOnlyArraySize;
/*    */   protected int _maximumCapacity;
/*    */ 
/*    */   public int getSize()
/*    */   {
/* 41 */     return this._size;
/*    */   }
/*    */ 
/*    */   public int getMaximumCapacity() {
/* 45 */     return this._maximumCapacity;
/*    */   }
/*    */ 
/*    */   public void setMaximumCapacity(int maximumCapacity) {
/* 49 */     this._maximumCapacity = maximumCapacity;
/*    */   }
/*    */ 
/*    */   public abstract void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean);
/*    */ 
/*    */   public abstract void clear();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.fastinfoset.util.ValueArray
 * JD-Core Version:    0.6.2
 */