/*    */ package sun.net.httpserver;
/*    */ 
/*    */ import com.sun.net.httpserver.HttpServer;
/*    */ import com.sun.net.httpserver.HttpsServer;
/*    */ import com.sun.net.httpserver.spi.HttpServerProvider;
/*    */ import java.io.IOException;
/*    */ import java.net.InetSocketAddress;
/*    */ 
/*    */ public class DefaultHttpServerProvider extends HttpServerProvider
/*    */ {
/*    */   public HttpServer createHttpServer(InetSocketAddress paramInetSocketAddress, int paramInt)
/*    */     throws IOException
/*    */   {
/* 35 */     return new HttpServerImpl(paramInetSocketAddress, paramInt);
/*    */   }
/*    */ 
/*    */   public HttpsServer createHttpsServer(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException {
/* 39 */     return new HttpsServerImpl(paramInetSocketAddress, paramInt);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.httpserver.DefaultHttpServerProvider
 * JD-Core Version:    0.6.2
 */