/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.IllegalBlockingModeException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ServerSocketAdaptor extends ServerSocket
/*     */ {
/*     */   private final ServerSocketChannelImpl ssc;
/*  48 */   private volatile int timeout = 0;
/*     */ 
/*     */   public static ServerSocket create(ServerSocketChannelImpl paramServerSocketChannelImpl) {
/*     */     try {
/*  52 */       return new ServerSocketAdaptor(paramServerSocketChannelImpl);
/*     */     } catch (IOException localIOException) {
/*  54 */       throw new Error(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private ServerSocketAdaptor(ServerSocketChannelImpl paramServerSocketChannelImpl)
/*     */     throws IOException
/*     */   {
/*  62 */     this.ssc = paramServerSocketChannelImpl;
/*     */   }
/*     */ 
/*     */   public void bind(SocketAddress paramSocketAddress) throws IOException
/*     */   {
/*  67 */     bind(paramSocketAddress, 50);
/*     */   }
/*     */ 
/*     */   public void bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
/*  71 */     if (paramSocketAddress == null)
/*  72 */       paramSocketAddress = new InetSocketAddress(0);
/*     */     try {
/*  74 */       this.ssc.bind(paramSocketAddress, paramInt);
/*     */     } catch (Exception localException) {
/*  76 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public InetAddress getInetAddress() {
/*  81 */     if (!this.ssc.isBound())
/*  82 */       return null;
/*  83 */     return Net.asInetSocketAddress(this.ssc.localAddress()).getAddress();
/*     */   }
/*     */ 
/*     */   public int getLocalPort() {
/*  87 */     if (!this.ssc.isBound())
/*  88 */       return -1;
/*  89 */     return Net.asInetSocketAddress(this.ssc.localAddress()).getPort();
/*     */   }
/*     */ 
/*     */   public Socket accept() throws IOException
/*     */   {
/*  94 */     synchronized (this.ssc.blockingLock()) {
/*  95 */       if (!this.ssc.isBound())
/*  96 */         throw new IllegalBlockingModeException();
/*     */       try {
/*  98 */         if (this.timeout == 0) {
/*  99 */           localObject1 = this.ssc.accept();
/* 100 */           if ((localObject1 == null) && (!this.ssc.isBlocking()))
/* 101 */             throw new IllegalBlockingModeException();
/* 102 */           return ((SocketChannel)localObject1).socket();
/*     */         }
/*     */ 
/* 106 */         Object localObject1 = null;
/* 107 */         Selector localSelector = null;
/* 108 */         this.ssc.configureBlocking(false);
/*     */         try
/*     */         {
/*     */           SocketChannel localSocketChannel;
/* 111 */           if ((localSocketChannel = this.ssc.accept()) != null) {
/* 112 */             Socket localSocket1 = localSocketChannel.socket();
/*     */ 
/* 130 */             if (localObject1 != null)
/* 131 */               ((SelectionKey)localObject1).cancel();
/* 132 */             if (this.ssc.isOpen())
/* 133 */               this.ssc.configureBlocking(true);
/* 134 */             if (localSelector != null)
/* 135 */               Util.releaseTemporarySelector(localSelector); return localSocket1;
/*     */           }
/* 113 */           localSelector = Util.getTemporarySelector(this.ssc);
/* 114 */           localObject1 = this.ssc.register(localSelector, 16);
/* 115 */           long l1 = this.timeout;
/*     */           while (true) {
/* 117 */             if (!this.ssc.isOpen())
/* 118 */               throw new ClosedChannelException();
/* 119 */             long l2 = System.currentTimeMillis();
/* 120 */             int i = localSelector.select(l1);
/* 121 */             if ((i > 0) && (((SelectionKey)localObject1).isAcceptable()) && ((localSocketChannel = this.ssc.accept()) != null))
/*     */             {
/* 123 */               Socket localSocket2 = localSocketChannel.socket();
/*     */ 
/* 130 */               if (localObject1 != null)
/* 131 */                 ((SelectionKey)localObject1).cancel();
/* 132 */               if (this.ssc.isOpen())
/* 133 */                 this.ssc.configureBlocking(true);
/* 134 */               if (localSelector != null)
/* 135 */                 Util.releaseTemporarySelector(localSelector); return localSocket2;
/*     */             }
/* 124 */             localSelector.selectedKeys().remove(localObject1);
/* 125 */             l1 -= System.currentTimeMillis() - l2;
/* 126 */             if (l1 <= 0L)
/* 127 */               throw new SocketTimeoutException();
/*     */           }
/*     */         } finally {
/* 130 */           if (localObject1 != null)
/* 131 */             ((SelectionKey)localObject1).cancel();
/* 132 */           if (this.ssc.isOpen())
/* 133 */             this.ssc.configureBlocking(true);
/* 134 */           if (localSelector != null)
/* 135 */             Util.releaseTemporarySelector(localSelector);
/*     */         }
/*     */       }
/*     */       catch (Exception localException) {
/* 139 */         Net.translateException(localException);
/* 140 */         if (!$assertionsDisabled) throw new AssertionError();
/* 141 */         return null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 147 */     this.ssc.close();
/*     */   }
/*     */ 
/*     */   public ServerSocketChannel getChannel() {
/* 151 */     return this.ssc;
/*     */   }
/*     */ 
/*     */   public boolean isBound() {
/* 155 */     return this.ssc.isBound();
/*     */   }
/*     */ 
/*     */   public boolean isClosed() {
/* 159 */     return !this.ssc.isOpen();
/*     */   }
/*     */ 
/*     */   public void setSoTimeout(int paramInt) throws SocketException {
/* 163 */     this.timeout = paramInt;
/*     */   }
/*     */ 
/*     */   public int getSoTimeout() throws SocketException {
/* 167 */     return this.timeout;
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean paramBoolean) throws SocketException {
/*     */     try {
/* 172 */       this.ssc.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.valueOf(paramBoolean));
/*     */     } catch (IOException localIOException) {
/* 174 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getReuseAddress() throws SocketException {
/*     */     try {
/* 180 */       return ((Boolean)this.ssc.getOption(StandardSocketOptions.SO_REUSEADDR)).booleanValue();
/*     */     } catch (IOException localIOException) {
/* 182 */       Net.translateToSocketException(localIOException);
/* 183 */     }return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 188 */     if (!isBound())
/* 189 */       return "ServerSocket[unbound]";
/* 190 */     return "ServerSocket[addr=" + getInetAddress() + ",localport=" + getLocalPort() + "]";
/*     */   }
/*     */ 
/*     */   public void setReceiveBufferSize(int paramInt)
/*     */     throws SocketException
/*     */   {
/* 197 */     if (paramInt <= 0)
/* 198 */       throw new IllegalArgumentException("size cannot be 0 or negative");
/*     */     try {
/* 200 */       this.ssc.setOption(StandardSocketOptions.SO_RCVBUF, Integer.valueOf(paramInt));
/*     */     } catch (IOException localIOException) {
/* 202 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getReceiveBufferSize() throws SocketException {
/*     */     try {
/* 208 */       return ((Integer)this.ssc.getOption(StandardSocketOptions.SO_RCVBUF)).intValue();
/*     */     } catch (IOException localIOException) {
/* 210 */       Net.translateToSocketException(localIOException);
/* 211 */     }return -1;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.ServerSocketAdaptor
 * JD-Core Version:    0.6.2
 */