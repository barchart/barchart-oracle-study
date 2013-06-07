/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketImpl;
/*     */ import java.net.SocketOption;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.IllegalBlockingModeException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SocketAdaptor extends Socket
/*     */ {
/*     */   private final SocketChannelImpl sc;
/*  58 */   private volatile int timeout = 0;
/*     */ 
/* 241 */   private InputStream socketInputStream = null;
/*     */ 
/*     */   private SocketAdaptor(SocketChannelImpl paramSocketChannelImpl)
/*     */     throws SocketException
/*     */   {
/*  61 */     super((SocketImpl)null);
/*  62 */     this.sc = paramSocketChannelImpl;
/*     */   }
/*     */ 
/*     */   public static Socket create(SocketChannelImpl paramSocketChannelImpl) {
/*     */     try {
/*  67 */       return new SocketAdaptor(paramSocketChannelImpl); } catch (SocketException localSocketException) {
/*     */     }
/*  69 */     throw new InternalError("Should not reach here");
/*     */   }
/*     */ 
/*     */   public SocketChannel getChannel()
/*     */   {
/*  74 */     return this.sc;
/*     */   }
/*     */ 
/*     */   public void connect(SocketAddress paramSocketAddress)
/*     */     throws IOException
/*     */   {
/*  80 */     connect(paramSocketAddress, 0);
/*     */   }
/*     */ 
/*     */   public void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
/*  84 */     if (paramSocketAddress == null)
/*  85 */       throw new IllegalArgumentException("connect: The address can't be null");
/*  86 */     if (paramInt < 0) {
/*  87 */       throw new IllegalArgumentException("connect: timeout can't be negative");
/*     */     }
/*  89 */     synchronized (this.sc.blockingLock()) {
/*  90 */       if (!this.sc.isBlocking()) {
/*  91 */         throw new IllegalBlockingModeException();
/*     */       }
/*     */       try
/*     */       {
/*  95 */         if (paramInt == 0) {
/*  96 */           this.sc.connect(paramSocketAddress);
/*  97 */           return;
/*     */         }
/*     */ 
/* 101 */         SelectionKey localSelectionKey = null;
/* 102 */         Selector localSelector = null;
/* 103 */         this.sc.configureBlocking(false);
/*     */         try {
/* 105 */           if (this.sc.connect(paramSocketAddress))
/*     */           {
/* 128 */             if (localSelectionKey != null)
/* 129 */               localSelectionKey.cancel();
/* 130 */             if (this.sc.isOpen())
/* 131 */               this.sc.configureBlocking(true);
/* 132 */             if (localSelector != null)
/* 133 */               Util.releaseTemporarySelector(localSelector);
/*     */           }
/*     */           else
/*     */           {
/* 107 */             localSelector = Util.getTemporarySelector(this.sc);
/* 108 */             localSelectionKey = this.sc.register(localSelector, 8);
/* 109 */             long l1 = paramInt;
/*     */             while (true) {
/* 111 */               if (!this.sc.isOpen())
/* 112 */                 throw new ClosedChannelException();
/* 113 */               long l2 = System.currentTimeMillis();
/* 114 */               int i = localSelector.select(l1);
/* 115 */               if ((i > 0) && (localSelectionKey.isConnectable()) && (this.sc.finishConnect())) {
/*     */                 break;
/*     */               }
/* 118 */               localSelector.selectedKeys().remove(localSelectionKey);
/* 119 */               l1 -= System.currentTimeMillis() - l2;
/* 120 */               if (l1 <= 0L) {
/*     */                 try {
/* 122 */                   this.sc.close(); } catch (IOException localIOException) {
/*     */                 }
/* 124 */                 throw new SocketTimeoutException();
/*     */               }
/*     */             }
/*     */           }
/*     */         } finally { if (localSelectionKey != null)
/* 129 */             localSelectionKey.cancel();
/* 130 */           if (this.sc.isOpen())
/* 131 */             this.sc.configureBlocking(true);
/* 132 */           if (localSelector != null)
/* 133 */             Util.releaseTemporarySelector(localSelector); }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 137 */         Net.translateException(localException, true);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void bind(SocketAddress paramSocketAddress) throws IOException
/*     */   {
/*     */     try {
/* 145 */       this.sc.bind(paramSocketAddress);
/*     */     } catch (Exception localException) {
/* 147 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public InetAddress getInetAddress() {
/* 152 */     SocketAddress localSocketAddress = this.sc.remoteAddress();
/* 153 */     if (localSocketAddress == null) {
/* 154 */       return null;
/*     */     }
/* 156 */     return ((InetSocketAddress)localSocketAddress).getAddress();
/*     */   }
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/* 161 */     if (this.sc.isOpen()) {
/* 162 */       SocketAddress localSocketAddress = this.sc.localAddress();
/* 163 */       if (localSocketAddress != null)
/* 164 */         return ((InetSocketAddress)localSocketAddress).getAddress();
/*     */     }
/* 166 */     return new InetSocketAddress(0).getAddress();
/*     */   }
/*     */ 
/*     */   public int getPort() {
/* 170 */     SocketAddress localSocketAddress = this.sc.remoteAddress();
/* 171 */     if (localSocketAddress == null) {
/* 172 */       return 0;
/*     */     }
/* 174 */     return ((InetSocketAddress)localSocketAddress).getPort();
/*     */   }
/*     */ 
/*     */   public int getLocalPort()
/*     */   {
/* 179 */     SocketAddress localSocketAddress = this.sc.localAddress();
/* 180 */     if (localSocketAddress == null) {
/* 181 */       return -1;
/*     */     }
/* 183 */     return ((InetSocketAddress)localSocketAddress).getPort();
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 244 */     if (!this.sc.isOpen())
/* 245 */       throw new SocketException("Socket is closed");
/* 246 */     if (!this.sc.isConnected())
/* 247 */       throw new SocketException("Socket is not connected");
/* 248 */     if (!this.sc.isInputOpen())
/* 249 */       throw new SocketException("Socket input is shutdown");
/* 250 */     if (this.socketInputStream == null) {
/*     */       try {
/* 252 */         this.socketInputStream = ((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public InputStream run() throws IOException {
/* 255 */             return new SocketAdaptor.SocketInputStream(SocketAdaptor.this, null);
/*     */           } } ));
/*     */       }
/*     */       catch (PrivilegedActionException localPrivilegedActionException) {
/* 259 */         throw ((IOException)localPrivilegedActionException.getException());
/*     */       }
/*     */     }
/* 262 */     return this.socketInputStream;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() throws IOException {
/* 266 */     if (!this.sc.isOpen())
/* 267 */       throw new SocketException("Socket is closed");
/* 268 */     if (!this.sc.isConnected())
/* 269 */       throw new SocketException("Socket is not connected");
/* 270 */     if (!this.sc.isOutputOpen())
/* 271 */       throw new SocketException("Socket output is shutdown");
/* 272 */     OutputStream localOutputStream = null;
/*     */     try {
/* 274 */       localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public OutputStream run() throws IOException {
/* 277 */           return Channels.newOutputStream(SocketAdaptor.this.sc);
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/* 281 */       throw ((IOException)localPrivilegedActionException.getException());
/*     */     }
/* 283 */     return localOutputStream;
/*     */   }
/*     */ 
/*     */   private void setBooleanOption(SocketOption<Boolean> paramSocketOption, boolean paramBoolean) throws SocketException
/*     */   {
/*     */     try
/*     */     {
/* 290 */       this.sc.setOption(paramSocketOption, Boolean.valueOf(paramBoolean));
/*     */     } catch (IOException localIOException) {
/* 292 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setIntOption(SocketOption<Integer> paramSocketOption, int paramInt) throws SocketException
/*     */   {
/*     */     try
/*     */     {
/* 300 */       this.sc.setOption(paramSocketOption, Integer.valueOf(paramInt));
/*     */     } catch (IOException localIOException) {
/* 302 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean getBooleanOption(SocketOption<Boolean> paramSocketOption) throws SocketException {
/*     */     try {
/* 308 */       return ((Boolean)this.sc.getOption(paramSocketOption)).booleanValue();
/*     */     } catch (IOException localIOException) {
/* 310 */       Net.translateToSocketException(localIOException);
/* 311 */     }return false;
/*     */   }
/*     */ 
/*     */   private int getIntOption(SocketOption<Integer> paramSocketOption) throws SocketException
/*     */   {
/*     */     try {
/* 317 */       return ((Integer)this.sc.getOption(paramSocketOption)).intValue();
/*     */     } catch (IOException localIOException) {
/* 319 */       Net.translateToSocketException(localIOException);
/* 320 */     }return -1;
/*     */   }
/*     */ 
/*     */   public void setTcpNoDelay(boolean paramBoolean) throws SocketException
/*     */   {
/* 325 */     setBooleanOption(StandardSocketOptions.TCP_NODELAY, paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getTcpNoDelay() throws SocketException {
/* 329 */     return getBooleanOption(StandardSocketOptions.TCP_NODELAY);
/*     */   }
/*     */ 
/*     */   public void setSoLinger(boolean paramBoolean, int paramInt) throws SocketException {
/* 333 */     if (!paramBoolean)
/* 334 */       paramInt = -1;
/* 335 */     setIntOption(StandardSocketOptions.SO_LINGER, paramInt);
/*     */   }
/*     */ 
/*     */   public int getSoLinger() throws SocketException {
/* 339 */     return getIntOption(StandardSocketOptions.SO_LINGER);
/*     */   }
/*     */ 
/*     */   public void sendUrgentData(int paramInt) throws IOException {
/* 343 */     synchronized (this.sc.blockingLock()) {
/* 344 */       if (!this.sc.isBlocking())
/* 345 */         throw new IllegalBlockingModeException();
/* 346 */       int i = this.sc.sendOutOfBandData((byte)paramInt);
/* 347 */       if ((!$assertionsDisabled) && (i != 1)) throw new AssertionError(); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOOBInline(boolean paramBoolean) throws SocketException {
/* 352 */     setBooleanOption(ExtendedSocketOption.SO_OOBINLINE, paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getOOBInline() throws SocketException {
/* 356 */     return getBooleanOption(ExtendedSocketOption.SO_OOBINLINE);
/*     */   }
/*     */ 
/*     */   public void setSoTimeout(int paramInt) throws SocketException {
/* 360 */     if (paramInt < 0)
/* 361 */       throw new IllegalArgumentException("timeout can't be negative");
/* 362 */     this.timeout = paramInt;
/*     */   }
/*     */ 
/*     */   public int getSoTimeout() throws SocketException {
/* 366 */     return this.timeout;
/*     */   }
/*     */ 
/*     */   public void setSendBufferSize(int paramInt) throws SocketException
/*     */   {
/* 371 */     if (paramInt <= 0)
/* 372 */       throw new IllegalArgumentException("Invalid send size");
/* 373 */     setIntOption(StandardSocketOptions.SO_SNDBUF, paramInt);
/*     */   }
/*     */ 
/*     */   public int getSendBufferSize() throws SocketException {
/* 377 */     return getIntOption(StandardSocketOptions.SO_SNDBUF);
/*     */   }
/*     */ 
/*     */   public void setReceiveBufferSize(int paramInt) throws SocketException
/*     */   {
/* 382 */     if (paramInt <= 0)
/* 383 */       throw new IllegalArgumentException("Invalid receive size");
/* 384 */     setIntOption(StandardSocketOptions.SO_RCVBUF, paramInt);
/*     */   }
/*     */ 
/*     */   public int getReceiveBufferSize() throws SocketException {
/* 388 */     return getIntOption(StandardSocketOptions.SO_RCVBUF);
/*     */   }
/*     */ 
/*     */   public void setKeepAlive(boolean paramBoolean) throws SocketException {
/* 392 */     setBooleanOption(StandardSocketOptions.SO_KEEPALIVE, paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getKeepAlive() throws SocketException {
/* 396 */     return getBooleanOption(StandardSocketOptions.SO_KEEPALIVE);
/*     */   }
/*     */ 
/*     */   public void setTrafficClass(int paramInt) throws SocketException {
/* 400 */     setIntOption(StandardSocketOptions.IP_TOS, paramInt);
/*     */   }
/*     */ 
/*     */   public int getTrafficClass() throws SocketException {
/* 404 */     return getIntOption(StandardSocketOptions.IP_TOS);
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean paramBoolean) throws SocketException {
/* 408 */     setBooleanOption(StandardSocketOptions.SO_REUSEADDR, paramBoolean);
/*     */   }
/*     */ 
/*     */   public boolean getReuseAddress() throws SocketException {
/* 412 */     return getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 416 */     this.sc.close();
/*     */   }
/*     */ 
/*     */   public void shutdownInput() throws IOException {
/*     */     try {
/* 421 */       this.sc.shutdownInput();
/*     */     } catch (Exception localException) {
/* 423 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shutdownOutput() throws IOException {
/*     */     try {
/* 429 */       this.sc.shutdownOutput();
/*     */     } catch (Exception localException) {
/* 431 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 436 */     if (this.sc.isConnected()) {
/* 437 */       return "Socket[addr=" + getInetAddress() + ",port=" + getPort() + ",localport=" + getLocalPort() + "]";
/*     */     }
/*     */ 
/* 440 */     return "Socket[unconnected]";
/*     */   }
/*     */ 
/*     */   public boolean isConnected() {
/* 444 */     return this.sc.isConnected();
/*     */   }
/*     */ 
/*     */   public boolean isBound() {
/* 448 */     return this.sc.localAddress() != null;
/*     */   }
/*     */ 
/*     */   public boolean isClosed() {
/* 452 */     return !this.sc.isOpen();
/*     */   }
/*     */ 
/*     */   public boolean isInputShutdown() {
/* 456 */     return !this.sc.isInputOpen();
/*     */   }
/*     */ 
/*     */   public boolean isOutputShutdown() {
/* 460 */     return !this.sc.isOutputOpen();
/*     */   }
/*     */ 
/*     */   private class SocketInputStream extends ChannelInputStream
/*     */   {
/*     */     private SocketInputStream()
/*     */     {
/* 191 */       super();
/*     */     }
/*     */ 
/*     */     protected int read(ByteBuffer paramByteBuffer)
/*     */       throws IOException
/*     */     {
/* 197 */       synchronized (SocketAdaptor.this.sc.blockingLock()) {
/* 198 */         if (!SocketAdaptor.this.sc.isBlocking())
/* 199 */           throw new IllegalBlockingModeException();
/* 200 */         if (SocketAdaptor.this.timeout == 0) {
/* 201 */           return SocketAdaptor.this.sc.read(paramByteBuffer);
/*     */         }
/*     */ 
/* 204 */         SelectionKey localSelectionKey = null;
/* 205 */         Selector localSelector = null;
/* 206 */         SocketAdaptor.this.sc.configureBlocking(false);
/*     */         try
/*     */         {
/*     */           int i;
/* 209 */           if ((i = SocketAdaptor.this.sc.read(paramByteBuffer)) != 0) {
/* 210 */             int j = i;
/*     */ 
/* 229 */             if (localSelectionKey != null)
/* 230 */               localSelectionKey.cancel();
/* 231 */             if (SocketAdaptor.this.sc.isOpen())
/* 232 */               SocketAdaptor.this.sc.configureBlocking(true);
/* 233 */             if (localSelector != null)
/* 234 */               Util.releaseTemporarySelector(localSelector); return j;
/*     */           }
/* 211 */           localSelector = Util.getTemporarySelector(SocketAdaptor.this.sc);
/* 212 */           localSelectionKey = SocketAdaptor.this.sc.register(localSelector, 1);
/* 213 */           long l1 = SocketAdaptor.this.timeout;
/*     */           while (true) {
/* 215 */             if (!SocketAdaptor.this.sc.isOpen())
/* 216 */               throw new ClosedChannelException();
/* 217 */             long l2 = System.currentTimeMillis();
/* 218 */             int k = localSelector.select(l1);
/* 219 */             if ((k > 0) && (localSelectionKey.isReadable()) && 
/* 220 */               ((i = SocketAdaptor.this.sc.read(paramByteBuffer)) != 0)) {
/* 221 */               int m = i;
/*     */ 
/* 229 */               if (localSelectionKey != null)
/* 230 */                 localSelectionKey.cancel();
/* 231 */               if (SocketAdaptor.this.sc.isOpen())
/* 232 */                 SocketAdaptor.this.sc.configureBlocking(true);
/* 233 */               if (localSelector != null)
/* 234 */                 Util.releaseTemporarySelector(localSelector); return m;
/*     */             }
/* 223 */             localSelector.selectedKeys().remove(localSelectionKey);
/* 224 */             l1 -= System.currentTimeMillis() - l2;
/* 225 */             if (l1 <= 0L)
/* 226 */               throw new SocketTimeoutException();
/*     */           }
/*     */         } finally {
/* 229 */           if (localSelectionKey != null)
/* 230 */             localSelectionKey.cancel();
/* 231 */           if (SocketAdaptor.this.sc.isOpen())
/* 232 */             SocketAdaptor.this.sc.configureBlocking(true);
/* 233 */           if (localSelector != null)
/* 234 */             Util.releaseTemporarySelector(localSelector);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SocketAdaptor
 * JD-Core Version:    0.6.2
 */