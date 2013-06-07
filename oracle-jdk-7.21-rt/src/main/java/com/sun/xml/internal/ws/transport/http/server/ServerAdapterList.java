/*    */ package com.sun.xml.internal.ws.transport.http.server;
/*    */ 
/*    */ import com.sun.xml.internal.ws.api.server.WSEndpoint;
/*    */ import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
/*    */ 
/*    */ public class ServerAdapterList extends HttpAdapterList<ServerAdapter>
/*    */ {
/*    */   protected ServerAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint)
/*    */   {
/* 34 */     return new ServerAdapter(name, urlPattern, endpoint, this);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.transport.http.server.ServerAdapterList
 * JD-Core Version:    0.6.2
 */