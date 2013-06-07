/*    */ package com.sun.xml.internal.ws.api.server;
/*    */ 
/*    */ import com.sun.istack.internal.NotNull;
/*    */ import com.sun.xml.internal.ws.server.DefaultResourceInjector;
/*    */ 
/*    */ public abstract class ResourceInjector
/*    */ {
/* 72 */   public static final ResourceInjector STANDALONE = new DefaultResourceInjector();
/*    */ 
/*    */   public abstract void inject(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull Object paramObject);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.ResourceInjector
 * JD-Core Version:    0.6.2
 */