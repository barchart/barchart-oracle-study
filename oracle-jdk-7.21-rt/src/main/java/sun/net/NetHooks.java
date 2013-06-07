/*    */ package sun.net;
/*    */ 
/*    */ import java.io.FileDescriptor;
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import sun.net.sdp.SdpProvider;
/*    */ 
/*    */ public final class NetHooks
/*    */ {
/* 76 */   private static final Provider provider = new SdpProvider();
/*    */ 
/*    */   public static void beforeTcpBind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*    */     throws IOException
/*    */   {
/* 86 */     provider.implBeforeTcpBind(paramFileDescriptor, paramInetAddress, paramInt);
/*    */   }
/*    */ 
/*    */   public static void beforeTcpConnect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*    */     throws IOException
/*    */   {
/* 97 */     provider.implBeforeTcpConnect(paramFileDescriptor, paramInetAddress, paramInt);
/*    */   }
/*    */ 
/*    */   public static abstract class Provider
/*    */   {
/*    */     public abstract void implBeforeTcpBind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*    */       throws IOException;
/*    */ 
/*    */     public abstract void implBeforeTcpConnect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*    */       throws IOException;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.NetHooks
 * JD-Core Version:    0.6.2
 */