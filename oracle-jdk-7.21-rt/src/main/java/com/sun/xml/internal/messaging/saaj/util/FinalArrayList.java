/*    */ package com.sun.xml.internal.messaging.saaj.util;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public final class FinalArrayList extends ArrayList
/*    */ {
/*    */   public FinalArrayList(int initialCapacity)
/*    */   {
/* 37 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   public FinalArrayList() {
/*    */   }
/*    */ 
/*    */   public FinalArrayList(Collection collection) {
/* 44 */     super(collection);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.messaging.saaj.util.FinalArrayList
 * JD-Core Version:    0.6.2
 */