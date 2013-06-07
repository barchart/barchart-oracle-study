/*     */ package com.sun.jndi.dns;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ 
/*     */ class Tcp
/*     */ {
/*     */   private Socket sock;
/*     */   InputStream in;
/*     */   OutputStream out;
/*     */ 
/*     */   Tcp(InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 642 */     this.sock = new Socket(paramInetAddress, paramInt);
/* 643 */     this.sock.setTcpNoDelay(true);
/* 644 */     this.out = new BufferedOutputStream(this.sock.getOutputStream());
/* 645 */     this.in = new BufferedInputStream(this.sock.getInputStream());
/*     */   }
/*     */ 
/*     */   void close() throws IOException {
/* 649 */     this.sock.close();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.dns.Tcp
 * JD-Core Version:    0.6.2
 */