/*    */ package com.sun.xml.internal.ws.client.sei;
/*    */ 
/*    */ import javax.xml.ws.Holder;
/*    */ 
/*    */  enum ValueGetter
/*    */ {
/* 51 */   PLAIN, 
/*    */ 
/* 64 */   HOLDER;
/*    */ 
/*    */   abstract Object get(Object paramObject);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.client.sei.ValueGetter
 * JD-Core Version:    0.6.2
 */