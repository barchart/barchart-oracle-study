/*    */ package java.net;
/*    */ 
/*    */ import java.io.FileDescriptor;
/*    */ import java.io.IOException;
/*    */ 
/*    */ class PlainSocketImpl extends AbstractPlainSocketImpl
/*    */ {
/*    */   PlainSocketImpl()
/*    */   {
/*    */   }
/*    */ 
/*    */   PlainSocketImpl(FileDescriptor paramFileDescriptor)
/*    */   {
/* 51 */     this.fd = paramFileDescriptor;
/*    */   }
/*    */ 
/*    */   native void socketCreate(boolean paramBoolean)
/*    */     throws IOException;
/*    */ 
/*    */   native void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2)
/*    */     throws IOException;
/*    */ 
/*    */   native void socketBind(InetAddress paramInetAddress, int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   native void socketListen(int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   native void socketAccept(SocketImpl paramSocketImpl)
/*    */     throws IOException;
/*    */ 
/*    */   native int socketAvailable()
/*    */     throws IOException;
/*    */ 
/*    */   native void socketClose0(boolean paramBoolean)
/*    */     throws IOException;
/*    */ 
/*    */   native void socketShutdown(int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   static native void initProto();
/*    */ 
/*    */   native void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject)
/*    */     throws SocketException;
/*    */ 
/*    */   native int socketGetOption(int paramInt, Object paramObject)
/*    */     throws SocketException;
/*    */ 
/*    */   native void socketSendUrgentData(int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   static
/*    */   {
/* 39 */     initProto();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.PlainSocketImpl
 * JD-Core Version:    0.6.2
 */