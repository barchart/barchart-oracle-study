/*    */ package com.sun.istack.internal;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public final class FinalArrayList<T> extends ArrayList<T>
/*    */ {
/*    */   public FinalArrayList(int initialCapacity)
/*    */   {
/* 41 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   public FinalArrayList() {
/*    */   }
/*    */ 
/*    */   public FinalArrayList(Collection<? extends T> ts) {
/* 48 */     super(ts);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.istack.internal.FinalArrayList
 * JD-Core Version:    0.6.2
 */