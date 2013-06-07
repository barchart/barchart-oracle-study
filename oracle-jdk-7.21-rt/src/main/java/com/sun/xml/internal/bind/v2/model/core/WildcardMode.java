/*    */ package com.sun.xml.internal.bind.v2.model.core;
/*    */ 
/*    */ public enum WildcardMode
/*    */ {
/* 34 */   STRICT(false, true), SKIP(true, false), LAX(true, true);
/*    */ 
/*    */   public final boolean allowDom;
/*    */   public final boolean allowTypedObject;
/*    */ 
/* 40 */   private WildcardMode(boolean allowDom, boolean allowTypedObject) { this.allowDom = allowDom;
/* 41 */     this.allowTypedObject = allowTypedObject;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.WildcardMode
 * JD-Core Version:    0.6.2
 */