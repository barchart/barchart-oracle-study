/*    */ package sun.net;
/*    */ 
/*    */ import java.net.Proxy;
/*    */ 
/*    */ public final class ApplicationProxy extends Proxy
/*    */ {
/*    */   private ApplicationProxy(Proxy paramProxy)
/*    */   {
/* 37 */     super(paramProxy.type(), paramProxy.address());
/*    */   }
/*    */ 
/*    */   public static ApplicationProxy create(Proxy paramProxy) {
/* 41 */     return new ApplicationProxy(paramProxy);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.ApplicationProxy
 * JD-Core Version:    0.6.2
 */