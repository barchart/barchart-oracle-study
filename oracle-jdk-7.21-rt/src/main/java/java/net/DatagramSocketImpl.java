/*     */ package java.net;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public abstract class DatagramSocketImpl
/*     */   implements SocketOptions
/*     */ {
/*     */   protected int localPort;
/*     */   protected FileDescriptor fd;
/*     */ 
/*     */   protected abstract void create()
/*     */     throws SocketException;
/*     */ 
/*     */   protected abstract void bind(int paramInt, InetAddress paramInetAddress)
/*     */     throws SocketException;
/*     */ 
/*     */   protected abstract void send(DatagramPacket paramDatagramPacket)
/*     */     throws IOException;
/*     */ 
/*     */   protected void connect(InetAddress paramInetAddress, int paramInt)
/*     */     throws SocketException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void disconnect()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract int peek(InetAddress paramInetAddress)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract int peekData(DatagramPacket paramDatagramPacket)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void receive(DatagramPacket paramDatagramPacket)
/*     */     throws IOException;
/*     */ 
/*     */   @Deprecated
/*     */   protected abstract void setTTL(byte paramByte)
/*     */     throws IOException;
/*     */ 
/*     */   @Deprecated
/*     */   protected abstract byte getTTL()
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void setTimeToLive(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract int getTimeToLive()
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void join(InetAddress paramInetAddress)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void leave(InetAddress paramInetAddress)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void close();
/*     */ 
/*     */   protected int getLocalPort()
/*     */   {
/* 233 */     return this.localPort;
/*     */   }
/*     */ 
/*     */   protected FileDescriptor getFileDescriptor()
/*     */   {
/* 242 */     return this.fd;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.DatagramSocketImpl
 * JD-Core Version:    0.6.2
 */