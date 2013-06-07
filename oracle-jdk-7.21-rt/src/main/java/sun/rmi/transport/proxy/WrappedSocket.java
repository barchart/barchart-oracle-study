/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketImpl;
/*     */ 
/*     */ class WrappedSocket extends Socket
/*     */ {
/*     */   protected Socket socket;
/*  45 */   protected InputStream in = null;
/*     */ 
/*  48 */   protected OutputStream out = null;
/*     */ 
/*     */   public WrappedSocket(Socket paramSocket, InputStream paramInputStream, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/*  63 */     super((SocketImpl)null);
/*  64 */     this.socket = paramSocket;
/*  65 */     this.in = paramInputStream;
/*  66 */     this.out = paramOutputStream;
/*     */   }
/*     */ 
/*     */   public InetAddress getInetAddress()
/*     */   {
/*  74 */     return this.socket.getInetAddress();
/*     */   }
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/*  81 */     return this.socket.getLocalAddress();
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  89 */     return this.socket.getPort();
/*     */   }
/*     */ 
/*     */   public int getLocalPort()
/*     */   {
/*  97 */     return this.socket.getLocalPort();
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 105 */     if (this.in == null)
/* 106 */       this.in = this.socket.getInputStream();
/* 107 */     return this.in;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/* 115 */     if (this.out == null)
/* 116 */       this.out = this.socket.getOutputStream();
/* 117 */     return this.out;
/*     */   }
/*     */ 
/*     */   public void setTcpNoDelay(boolean paramBoolean)
/*     */     throws SocketException
/*     */   {
/* 125 */     this.socket.setTcpNoDelay(paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getTcpNoDelay()
/*     */     throws SocketException
/*     */   {
/* 133 */     return this.socket.getTcpNoDelay();
/*     */   }
/*     */ 
/*     */   public void setSoLinger(boolean paramBoolean, int paramInt)
/*     */     throws SocketException
/*     */   {
/* 141 */     this.socket.setSoLinger(paramBoolean, paramInt);
/*     */   }
/*     */ 
/*     */   public int getSoLinger()
/*     */     throws SocketException
/*     */   {
/* 149 */     return this.socket.getSoLinger();
/*     */   }
/*     */ 
/*     */   public synchronized void setSoTimeout(int paramInt)
/*     */     throws SocketException
/*     */   {
/* 157 */     this.socket.setSoTimeout(paramInt);
/*     */   }
/*     */ 
/*     */   public synchronized int getSoTimeout()
/*     */     throws SocketException
/*     */   {
/* 165 */     return this.socket.getSoTimeout();
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws IOException
/*     */   {
/* 173 */     this.socket.close();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 181 */     return "Wrapped" + this.socket.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.WrappedSocket
 * JD-Core Version:    0.6.2
 */