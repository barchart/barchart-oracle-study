/*    */ package com.sun.xml.internal.ws.api.server;
/*    */ 
/*    */ import com.sun.istack.internal.NotNull;
/*    */ import com.sun.xml.internal.ws.transport.http.HttpAdapter;
/*    */ 
/*    */ public abstract class HttpEndpoint
/*    */ {
/*    */   public static HttpEndpoint create(@NotNull WSEndpoint endpoint)
/*    */   {
/* 48 */     return new com.sun.xml.internal.ws.transport.http.server.HttpEndpoint(null, HttpAdapter.createAlone(endpoint));
/*    */   }
/*    */ 
/*    */   public abstract void publish(@NotNull String paramString);
/*    */ 
/*    */   public abstract void stop();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.HttpEndpoint
 * JD-Core Version:    0.6.2
 */