/*    */ package com.sun.corba.se.impl.transport;
/*    */ 
/*    */ import com.sun.corba.se.pept.transport.Acceptor;
/*    */ import com.sun.corba.se.spi.orb.ORB;
/*    */ import com.sun.corba.se.spi.orb.ORBData;
/*    */ import com.sun.corba.se.spi.transport.ORBSocketFactory;
/*    */ import java.io.IOException;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketException;
/*    */ import java.nio.channels.ServerSocketChannel;
/*    */ import java.nio.channels.SocketChannel;
/*    */ 
/*    */ public class DefaultSocketFactoryImpl
/*    */   implements ORBSocketFactory
/*    */ {
/*    */   private ORB orb;
/*    */ 
/*    */   public void setORB(ORB paramORB)
/*    */   {
/* 50 */     this.orb = paramORB;
/*    */   }
/*    */ 
/*    */   public ServerSocket createServerSocket(String paramString, InetSocketAddress paramInetSocketAddress)
/*    */     throws IOException
/*    */   {
/* 57 */     ServerSocketChannel localServerSocketChannel = null;
/* 58 */     ServerSocket localServerSocket = null;
/*    */ 
/* 60 */     if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
/* 61 */       localServerSocketChannel = ServerSocketChannel.open();
/* 62 */       localServerSocket = localServerSocketChannel.socket();
/*    */     } else {
/* 64 */       localServerSocket = new ServerSocket();
/*    */     }
/* 66 */     localServerSocket.bind(paramInetSocketAddress);
/* 67 */     return localServerSocket;
/*    */   }
/*    */ 
/*    */   public Socket createSocket(String paramString, InetSocketAddress paramInetSocketAddress)
/*    */     throws IOException
/*    */   {
/* 74 */     SocketChannel localSocketChannel = null;
/* 75 */     Socket localSocket = null;
/*    */ 
/* 77 */     if (this.orb.getORBData().connectionSocketType().equals("SocketChannel")) {
/* 78 */       localSocketChannel = SocketChannel.open(paramInetSocketAddress);
/* 79 */       localSocket = localSocketChannel.socket();
/*    */     } else {
/* 81 */       localSocket = new Socket(paramInetSocketAddress.getHostName(), paramInetSocketAddress.getPort());
/*    */     }
/*    */ 
/* 86 */     localSocket.setTcpNoDelay(true);
/*    */ 
/* 88 */     return localSocket;
/*    */   }
/*    */ 
/*    */   public void setAcceptedSocketOptions(Acceptor paramAcceptor, ServerSocket paramServerSocket, Socket paramSocket)
/*    */     throws SocketException
/*    */   {
/* 97 */     paramSocket.setTcpNoDelay(true);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.transport.DefaultSocketFactoryImpl
 * JD-Core Version:    0.6.2
 */