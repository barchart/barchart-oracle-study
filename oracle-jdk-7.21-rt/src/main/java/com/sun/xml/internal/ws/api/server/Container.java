/*    */ package com.sun.xml.internal.ws.api.server;
/*    */ 
/*    */ public abstract class Container
/*    */ {
/* 92 */   public static final Container NONE = new Container() {
/*    */     public <T> T getSPI(Class<T> spiType) {
/* 94 */       return null;
/*    */     }
/* 92 */   };
/*    */ 
/*    */   public abstract <T> T getSPI(Class<T> paramClass);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.Container
 * JD-Core Version:    0.6.2
 */