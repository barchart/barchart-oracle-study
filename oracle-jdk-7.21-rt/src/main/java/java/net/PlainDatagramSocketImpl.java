/*    */ package java.net;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ class PlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl
/*    */ {
/*    */   protected synchronized native void bind0(int paramInt, InetAddress paramInetAddress)
/*    */     throws SocketException;
/*    */ 
/*    */   protected native void send(DatagramPacket paramDatagramPacket)
/*    */     throws IOException;
/*    */ 
/*    */   protected synchronized native int peek(InetAddress paramInetAddress)
/*    */     throws IOException;
/*    */ 
/*    */   protected synchronized native int peekData(DatagramPacket paramDatagramPacket)
/*    */     throws IOException;
/*    */ 
/*    */   protected synchronized native void receive0(DatagramPacket paramDatagramPacket)
/*    */     throws IOException;
/*    */ 
/*    */   protected native void setTimeToLive(int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   protected native int getTimeToLive()
/*    */     throws IOException;
/*    */ 
/*    */   protected native void setTTL(byte paramByte)
/*    */     throws IOException;
/*    */ 
/*    */   protected native byte getTTL()
/*    */     throws IOException;
/*    */ 
/*    */   protected native void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
/*    */     throws IOException;
/*    */ 
/*    */   protected native void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
/*    */     throws IOException;
/*    */ 
/*    */   protected native void datagramSocketCreate()
/*    */     throws SocketException;
/*    */ 
/*    */   protected native void datagramSocketClose();
/*    */ 
/*    */   protected native void socketSetOption(int paramInt, Object paramObject)
/*    */     throws SocketException;
/*    */ 
/*    */   protected native Object socketGetOption(int paramInt)
/*    */     throws SocketException;
/*    */ 
/*    */   protected native void connect0(InetAddress paramInetAddress, int paramInt)
/*    */     throws SocketException;
/*    */ 
/*    */   protected native void disconnect0(int paramInt);
/*    */ 
/*    */   private static native void init();
/*    */ 
/*    */   static
/*    */   {
/* 38 */     init();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.PlainDatagramSocketImpl
 * JD-Core Version:    0.6.2
 */