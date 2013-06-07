/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import sun.net.NetHooks;
/*     */ 
/*     */ class ServerSocketChannelImpl extends ServerSocketChannel
/*     */   implements SelChImpl
/*     */ {
/* 383 */   private static NativeDispatcher nd = new SocketDispatcher();
/*     */   private final FileDescriptor fd;
/*     */   private int fdVal;
/*  57 */   private volatile long thread = 0L;
/*     */ 
/*  60 */   private final Object lock = new Object();
/*     */ 
/*  64 */   private final Object stateLock = new Object();
/*     */   private static final int ST_UNINITIALIZED = -1;
/*     */   private static final int ST_INUSE = 0;
/*     */   private static final int ST_KILLED = 1;
/*  72 */   private int state = -1;
/*     */   private SocketAddress localAddress;
/*     */   ServerSocket socket;
/*     */ 
/*     */   ServerSocketChannelImpl(SelectorProvider paramSelectorProvider)
/*     */     throws IOException
/*     */   {
/*  84 */     super(paramSelectorProvider);
/*  85 */     this.fd = Net.serverSocket(true);
/*  86 */     this.fdVal = IOUtil.fdVal(this.fd);
/*  87 */     this.state = 0;
/*     */   }
/*     */ 
/*     */   ServerSocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  95 */     super(paramSelectorProvider);
/*  96 */     this.fd = paramFileDescriptor;
/*  97 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/*  98 */     this.state = 0;
/*  99 */     if (paramBoolean)
/* 100 */       this.localAddress = Net.localAddress(paramFileDescriptor);
/*     */   }
/*     */ 
/*     */   public ServerSocket socket() {
/* 104 */     synchronized (this.stateLock) {
/* 105 */       if (this.socket == null)
/* 106 */         this.socket = ServerSocketAdaptor.create(this);
/* 107 */       return this.socket;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketAddress getLocalAddress() throws IOException
/*     */   {
/* 113 */     synchronized (this.stateLock) {
/* 114 */       if (!isOpen())
/* 115 */         throw new ClosedChannelException();
/* 116 */       return this.localAddress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> ServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/* 124 */     if (paramSocketOption == null)
/* 125 */       throw new NullPointerException();
/* 126 */     if (!supportedOptions().contains(paramSocketOption)) {
/* 127 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     }
/* 129 */     synchronized (this.stateLock) {
/* 130 */       if (!isOpen()) {
/* 131 */         throw new ClosedChannelException();
/*     */       }
/*     */ 
/* 134 */       Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/* 135 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T getOption(SocketOption<T> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 144 */     if (paramSocketOption == null)
/* 145 */       throw new NullPointerException();
/* 146 */     if (!supportedOptions().contains(paramSocketOption)) {
/* 147 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     }
/* 149 */     synchronized (this.stateLock) {
/* 150 */       if (!isOpen()) {
/* 151 */         throw new ClosedChannelException();
/*     */       }
/*     */ 
/* 154 */       return Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Set<SocketOption<?>> supportedOptions()
/*     */   {
/* 171 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */ 
/*     */   public boolean isBound() {
/* 175 */     synchronized (this.stateLock) {
/* 176 */       return this.localAddress != null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketAddress localAddress() {
/* 181 */     synchronized (this.stateLock) {
/* 182 */       return this.localAddress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException
/*     */   {
/* 188 */     synchronized (this.lock) {
/* 189 */       if (!isOpen())
/* 190 */         throw new ClosedChannelException();
/* 191 */       if (isBound())
/* 192 */         throw new AlreadyBoundException();
/* 193 */       InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*     */ 
/* 195 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 196 */       if (localSecurityManager != null)
/* 197 */         localSecurityManager.checkListen(localInetSocketAddress.getPort());
/* 198 */       NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 199 */       Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 200 */       Net.listen(this.fd, paramInt < 1 ? 50 : paramInt);
/* 201 */       synchronized (this.stateLock) {
/* 202 */         this.localAddress = Net.localAddress(this.fd);
/*     */       }
/*     */     }
/* 205 */     return this;
/*     */   }
/*     */ 
/*     */   public SocketChannel accept() throws IOException {
/* 209 */     synchronized (this.lock) {
/* 210 */       if (!isOpen())
/* 211 */         throw new ClosedChannelException();
/* 212 */       if (!isBound())
/* 213 */         throw new NotYetBoundException();
/* 214 */       SocketChannelImpl localSocketChannelImpl = null;
/*     */ 
/* 216 */       int i = 0;
/* 217 */       FileDescriptor localFileDescriptor = new FileDescriptor();
/* 218 */       InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
/*     */       try
/*     */       {
/* 221 */         begin();
/* 222 */         if (!isOpen()) {
/* 223 */           localObject1 = null;
/*     */ 
/* 232 */           this.thread = 0L;
/* 233 */           end(i > 0);
/* 234 */           assert (IOStatus.check(i)); return localObject1;
/*     */         }
/* 224 */         this.thread = NativeThread.current();
/*     */         while (true) {
/* 226 */           i = accept0(this.fd, localFileDescriptor, arrayOfInetSocketAddress);
/* 227 */           if ((i != -3) || (!isOpen()))
/*     */             break;
/*     */         }
/*     */       }
/*     */       finally {
/* 232 */         this.thread = 0L;
/* 233 */         end(i > 0);
/* 234 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*     */       }
/*     */ 
/* 237 */       if (i < 1) {
/* 238 */         return null;
/*     */       }
/* 240 */       IOUtil.configureBlocking(localFileDescriptor, true);
/* 241 */       Object localObject1 = arrayOfInetSocketAddress[0];
/* 242 */       localSocketChannelImpl = new SocketChannelImpl(provider(), localFileDescriptor, (InetSocketAddress)localObject1);
/* 243 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 244 */       if (localSecurityManager != null) {
/*     */         try {
/* 246 */           localSecurityManager.checkAccept(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1).getPort());
/*     */         }
/*     */         catch (SecurityException localSecurityException) {
/* 249 */           localSocketChannelImpl.close();
/* 250 */           throw localSecurityException;
/*     */         }
/*     */       }
/* 253 */       return localSocketChannelImpl;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException
/*     */   {
/* 259 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*     */   }
/*     */ 
/*     */   protected void implCloseSelectableChannel() throws IOException {
/* 263 */     synchronized (this.stateLock) {
/* 264 */       if (this.state != 1)
/* 265 */         nd.preClose(this.fd);
/* 266 */       long l = this.thread;
/* 267 */       if (l != 0L)
/* 268 */         NativeThread.signal(l);
/* 269 */       if (!isRegistered())
/* 270 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void kill() throws IOException {
/* 275 */     synchronized (this.stateLock) {
/* 276 */       if (this.state == 1)
/* 277 */         return;
/* 278 */       if (this.state == -1) {
/* 279 */         this.state = 1;
/* 280 */         return;
/*     */       }
/* 282 */       assert ((!isOpen()) && (!isRegistered()));
/* 283 */       nd.close(this.fd);
/* 284 */       this.state = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 293 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 294 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 295 */     int k = paramInt2;
/*     */ 
/* 297 */     if ((paramInt1 & 0x20) != 0)
/*     */     {
/* 301 */       return false;
/*     */     }
/*     */ 
/* 304 */     if ((paramInt1 & 0x18) != 0)
/*     */     {
/* 306 */       k = i;
/* 307 */       paramSelectionKeyImpl.nioReadyOps(k);
/* 308 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */ 
/* 311 */     if (((paramInt1 & 0x1) != 0) && ((i & 0x10) != 0))
/*     */     {
/* 313 */       k |= 16;
/*     */     }
/* 315 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 316 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */ 
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 320 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 324 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 331 */     int i = 0;
/*     */ 
/* 334 */     if ((paramInt & 0x10) != 0) {
/* 335 */       i |= 1;
/*     */     }
/* 337 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*     */   }
/*     */ 
/*     */   public FileDescriptor getFD() {
/* 341 */     return this.fd;
/*     */   }
/*     */ 
/*     */   public int getFDVal() {
/* 345 */     return this.fdVal;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 349 */     StringBuffer localStringBuffer = new StringBuffer();
/* 350 */     localStringBuffer.append(getClass().getName());
/* 351 */     localStringBuffer.append('[');
/* 352 */     if (!isOpen())
/* 353 */       localStringBuffer.append("closed");
/*     */     else {
/* 355 */       synchronized (this.stateLock) {
/* 356 */         if (localAddress() == null)
/* 357 */           localStringBuffer.append("unbound");
/*     */         else {
/* 359 */           localStringBuffer.append(localAddress().toString());
/*     */         }
/*     */       }
/*     */     }
/* 363 */     localStringBuffer.append(']');
/* 364 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private native int accept0(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   static
/*     */   {
/* 381 */     Util.load();
/* 382 */     initIDs();
/*     */   }
/*     */ 
/*     */   private static class DefaultOptionsHolder
/*     */   {
/* 159 */     static final Set<SocketOption<?>> defaultOptions = defaultOptions();
/*     */ 
/*     */     private static Set<SocketOption<?>> defaultOptions() {
/* 162 */       HashSet localHashSet = new HashSet(2);
/* 163 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/* 164 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/* 165 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.ServerSocketChannelImpl
 * JD-Core Version:    0.6.2
 */