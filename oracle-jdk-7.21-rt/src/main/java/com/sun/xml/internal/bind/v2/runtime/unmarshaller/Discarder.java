/*    */ package com.sun.xml.internal.bind.v2.runtime.unmarshaller;
/*    */ 
/*    */ public final class Discarder extends Loader
/*    */ {
/* 39 */   public static final Loader INSTANCE = new Discarder();
/*    */ 
/*    */   private Discarder() {
/* 42 */     super(false);
/*    */   }
/*    */ 
/*    */   public void childElement(UnmarshallingContext.State state, TagName ea)
/*    */   {
/* 47 */     state.target = null;
/*    */ 
/* 49 */     state.loader = this;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.Discarder
 * JD-Core Version:    0.6.2
 */