/*      */ package sun.nio.ch;
/*      */ 
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.IOException;
/*      */ import java.net.DatagramSocket;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NetworkInterface;
/*      */ import java.net.PortUnreachableException;
/*      */ import java.net.ProtocolFamily;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketOption;
/*      */ import java.net.StandardProtocolFamily;
/*      */ import java.net.StandardSocketOptions;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.AlreadyBoundException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.DatagramChannel;
/*      */ import java.nio.channels.MembershipKey;
/*      */ import java.nio.channels.NotYetConnectedException;
/*      */ import java.nio.channels.UnsupportedAddressTypeException;
/*      */ import java.nio.channels.spi.SelectorProvider;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ import sun.net.ResourceManager;
/*      */ 
/*      */ class DatagramChannelImpl extends DatagramChannel
/*      */   implements SelChImpl
/*      */ {
/*      */   private static NativeDispatcher nd;
/*      */   private final FileDescriptor fd;
/*      */   private final int fdVal;
/*      */   private final ProtocolFamily family;
/*   61 */   private volatile long readerThread = 0L;
/*   62 */   private volatile long writerThread = 0L;
/*      */   private InetAddress cachedSenderInetAddress;
/*      */   private int cachedSenderPort;
/*   70 */   private final Object readLock = new Object();
/*      */ 
/*   73 */   private final Object writeLock = new Object();
/*      */ 
/*   77 */   private final Object stateLock = new Object();
/*      */   private static final int ST_UNINITIALIZED = -1;
/*      */   private static final int ST_UNCONNECTED = 0;
/*      */   private static final int ST_CONNECTED = 1;
/*      */   private static final int ST_KILLED = 2;
/*   86 */   private int state = -1;
/*      */   private SocketAddress localAddress;
/*      */   private SocketAddress remoteAddress;
/*      */   private DatagramSocket socket;
/*      */   private MembershipRegistry registry;
/*      */   private SocketAddress sender;
/*      */ 
/*      */   public DatagramChannelImpl(SelectorProvider paramSelectorProvider)
/*      */     throws IOException
/*      */   {
/*  104 */     super(paramSelectorProvider);
/*  105 */     ResourceManager.beforeUdpCreate();
/*      */     try {
/*  107 */       this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
/*      */ 
/*  109 */       this.fd = Net.socket(this.family, false);
/*  110 */       this.fdVal = IOUtil.fdVal(this.fd);
/*  111 */       this.state = 0;
/*      */     } catch (IOException localIOException) {
/*  113 */       ResourceManager.afterUdpClose();
/*  114 */       throw localIOException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public DatagramChannelImpl(SelectorProvider paramSelectorProvider, ProtocolFamily paramProtocolFamily)
/*      */     throws IOException
/*      */   {
/*  121 */     super(paramSelectorProvider);
/*  122 */     if ((paramProtocolFamily != StandardProtocolFamily.INET) && (paramProtocolFamily != StandardProtocolFamily.INET6))
/*      */     {
/*  125 */       if (paramProtocolFamily == null) {
/*  126 */         throw new NullPointerException("'family' is null");
/*      */       }
/*  128 */       throw new UnsupportedOperationException("Protocol family not supported");
/*      */     }
/*  130 */     if ((paramProtocolFamily == StandardProtocolFamily.INET6) && 
/*  131 */       (!Net.isIPv6Available())) {
/*  132 */       throw new UnsupportedOperationException("IPv6 not available");
/*      */     }
/*      */ 
/*  135 */     this.family = paramProtocolFamily;
/*  136 */     this.fd = Net.socket(paramProtocolFamily, false);
/*  137 */     this.fdVal = IOUtil.fdVal(this.fd);
/*  138 */     this.state = 0;
/*      */   }
/*      */ 
/*      */   public DatagramChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor)
/*      */     throws IOException
/*      */   {
/*  144 */     super(paramSelectorProvider);
/*  145 */     this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
/*      */ 
/*  147 */     this.fd = paramFileDescriptor;
/*  148 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/*  149 */     this.state = 0;
/*  150 */     this.localAddress = Net.localAddress(paramFileDescriptor);
/*      */   }
/*      */ 
/*      */   public DatagramSocket socket() {
/*  154 */     synchronized (this.stateLock) {
/*  155 */       if (this.socket == null)
/*  156 */         this.socket = DatagramSocketAdaptor.create(this);
/*  157 */       return this.socket;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SocketAddress getLocalAddress() throws IOException
/*      */   {
/*  163 */     synchronized (this.stateLock) {
/*  164 */       if (!isOpen())
/*  165 */         throw new ClosedChannelException();
/*  166 */       return this.localAddress;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SocketAddress getRemoteAddress() throws IOException
/*      */   {
/*  172 */     synchronized (this.stateLock) {
/*  173 */       if (!isOpen())
/*  174 */         throw new ClosedChannelException();
/*  175 */       return this.remoteAddress;
/*      */     }
/*      */   }
/*      */ 
/*      */   public <T> DatagramChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*      */     throws IOException
/*      */   {
/*  183 */     if (paramSocketOption == null)
/*  184 */       throw new NullPointerException();
/*  185 */     if (!supportedOptions().contains(paramSocketOption)) {
/*  186 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*      */     }
/*  188 */     synchronized (this.stateLock) {
/*  189 */       ensureOpen();
/*      */ 
/*  191 */       if (paramSocketOption == StandardSocketOptions.IP_TOS)
/*      */       {
/*  193 */         if (this.family == StandardProtocolFamily.INET) {
/*  194 */           Net.setSocketOption(this.fd, this.family, paramSocketOption, paramT);
/*      */         }
/*  196 */         return this;
/*      */       }
/*      */ 
/*  199 */       if ((paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP))
/*      */       {
/*  203 */         Net.setSocketOption(this.fd, this.family, paramSocketOption, paramT);
/*  204 */         return this;
/*      */       }
/*      */ 
/*  207 */       if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF) {
/*  208 */         if (paramT == null)
/*  209 */           throw new IllegalArgumentException("Cannot set IP_MULTICAST_IF to 'null'");
/*  210 */         NetworkInterface localNetworkInterface = (NetworkInterface)paramT;
/*  211 */         if (this.family == StandardProtocolFamily.INET6) {
/*  212 */           int i = localNetworkInterface.getIndex();
/*  213 */           if (i == -1)
/*  214 */             throw new IOException("Network interface cannot be identified");
/*  215 */           Net.setInterface6(this.fd, i);
/*      */         }
/*      */         else {
/*  218 */           Inet4Address localInet4Address = Net.anyInet4Address(localNetworkInterface);
/*  219 */           if (localInet4Address == null)
/*  220 */             throw new IOException("Network interface not configured for IPv4");
/*  221 */           int j = Net.inet4AsInt(localInet4Address);
/*  222 */           Net.setInterface4(this.fd, j);
/*      */         }
/*  224 */         return this;
/*      */       }
/*      */ 
/*  228 */       Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/*  229 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   public <T> T getOption(SocketOption<T> paramSocketOption)
/*      */     throws IOException
/*      */   {
/*  238 */     if (paramSocketOption == null)
/*  239 */       throw new NullPointerException();
/*  240 */     if (!supportedOptions().contains(paramSocketOption)) {
/*  241 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*      */     }
/*  243 */     synchronized (this.stateLock) {
/*  244 */       ensureOpen();
/*      */ 
/*  246 */       if (paramSocketOption == StandardSocketOptions.IP_TOS)
/*      */       {
/*  248 */         if (this.family == StandardProtocolFamily.INET) {
/*  249 */           return Net.getSocketOption(this.fd, this.family, paramSocketOption);
/*      */         }
/*  251 */         return Integer.valueOf(0);
/*      */       }
/*      */ 
/*  255 */       if ((paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP))
/*      */       {
/*  258 */         return Net.getSocketOption(this.fd, this.family, paramSocketOption);
/*      */       }
/*      */ 
/*  261 */       if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF) {
/*  262 */         if (this.family == StandardProtocolFamily.INET) {
/*  263 */           i = Net.getInterface4(this.fd);
/*  264 */           if (i == 0) {
/*  265 */             return null;
/*      */           }
/*  267 */           localObject1 = Net.inet4FromInt(i);
/*  268 */           NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress((InetAddress)localObject1);
/*  269 */           if (localNetworkInterface == null)
/*  270 */             throw new IOException("Unable to map address to interface");
/*  271 */           return localNetworkInterface;
/*      */         }
/*  273 */         int i = Net.getInterface6(this.fd);
/*  274 */         if (i == 0) {
/*  275 */           return null;
/*      */         }
/*  277 */         Object localObject1 = NetworkInterface.getByIndex(i);
/*  278 */         if (localObject1 == null)
/*  279 */           throw new IOException("Unable to map index to interface");
/*  280 */         return localObject1;
/*      */       }
/*      */ 
/*  285 */       return Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final Set<SocketOption<?>> supportedOptions()
/*      */   {
/*  308 */     return DefaultOptionsHolder.defaultOptions;
/*      */   }
/*      */ 
/*      */   private void ensureOpen() throws ClosedChannelException {
/*  312 */     if (!isOpen())
/*  313 */       throw new ClosedChannelException();
/*      */   }
/*      */ 
/*      */   public SocketAddress receive(ByteBuffer paramByteBuffer)
/*      */     throws IOException
/*      */   {
/*  319 */     if (paramByteBuffer.isReadOnly())
/*  320 */       throw new IllegalArgumentException("Read-only buffer");
/*  321 */     if (paramByteBuffer == null)
/*  322 */       throw new NullPointerException();
/*  323 */     synchronized (this.readLock) {
/*  324 */       ensureOpen();
/*      */ 
/*  326 */       if (localAddress() == null)
/*  327 */         bind(null);
/*  328 */       int i = 0;
/*  329 */       ByteBuffer localByteBuffer = null;
/*      */       try {
/*  331 */         begin();
/*  332 */         if (!isOpen()) {
/*  333 */           localObject1 = null;
/*      */ 
/*  368 */           if (localByteBuffer != null)
/*  369 */             Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  370 */           this.readerThread = 0L;
/*  371 */           end((i > 0) || (i == -2));
/*  372 */           assert (IOStatus.check(i)); return localObject1;
/*      */         }
/*  334 */         Object localObject1 = System.getSecurityManager();
/*  335 */         this.readerThread = NativeThread.current();
/*  336 */         if ((isConnected()) || (localObject1 == null)) {
/*      */           do
/*  338 */             i = receive(this.fd, paramByteBuffer);
/*  339 */           while ((i == -3) && (isOpen()));
/*  340 */           if (i == -2) {
/*  341 */             localObject2 = null;
/*      */ 
/*  368 */             if (localByteBuffer != null)
/*  369 */               Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  370 */             this.readerThread = 0L;
/*  371 */             end((i > 0) || (i == -2));
/*  372 */             assert (IOStatus.check(i)); return localObject2;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  343 */           localByteBuffer = Util.getTemporaryDirectBuffer(paramByteBuffer.remaining());
/*      */           while (true)
/*      */           {
/*  346 */             i = receive(this.fd, localByteBuffer);
/*  347 */             if ((i != -3) || (!isOpen())) {
/*  348 */               if (i == -2) {
/*  349 */                 localObject2 = null;
/*      */ 
/*  368 */                 if (localByteBuffer != null)
/*  369 */                   Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  370 */                 this.readerThread = 0L;
/*  371 */                 end((i > 0) || (i == -2));
/*  372 */                 assert (IOStatus.check(i)); return localObject2;
/*      */               }
/*  350 */               localObject2 = (InetSocketAddress)this.sender;
/*      */               try {
/*  352 */                 ((SecurityManager)localObject1).checkAccept(((InetSocketAddress)localObject2).getAddress().getHostAddress(), ((InetSocketAddress)localObject2).getPort());
/*      */               }
/*      */               catch (SecurityException localSecurityException)
/*      */               {
/*  357 */                 localByteBuffer.clear();
/*  358 */                 i = 0;
/*      */               }
/*      */             }
/*      */           }
/*  361 */           localByteBuffer.flip();
/*  362 */           paramByteBuffer.put(localByteBuffer);
/*      */         }
/*      */ 
/*  366 */         Object localObject2 = this.sender;
/*      */ 
/*  368 */         if (localByteBuffer != null)
/*  369 */           Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  370 */         this.readerThread = 0L;
/*  371 */         end((i > 0) || (i == -2));
/*  372 */         assert (IOStatus.check(i)); return localObject2;
/*      */       }
/*      */       finally
/*      */       {
/*  368 */         if (localByteBuffer != null)
/*  369 */           Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  370 */         this.readerThread = 0L;
/*  371 */         end((i > 0) || (i == -2));
/*  372 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int receive(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer)
/*      */     throws IOException
/*      */   {
/*  380 */     int i = paramByteBuffer.position();
/*  381 */     int j = paramByteBuffer.limit();
/*  382 */     assert (i <= j);
/*  383 */     int k = i <= j ? j - i : 0;
/*  384 */     if (((paramByteBuffer instanceof DirectBuffer)) && (k > 0)) {
/*  385 */       return receiveIntoNativeBuffer(paramFileDescriptor, paramByteBuffer, k, i);
/*      */     }
/*      */ 
/*  390 */     int m = Math.max(k, 1);
/*  391 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(m);
/*      */     try {
/*  393 */       int n = receiveIntoNativeBuffer(paramFileDescriptor, localByteBuffer, m, 0);
/*  394 */       localByteBuffer.flip();
/*  395 */       if ((n > 0) && (k > 0))
/*  396 */         paramByteBuffer.put(localByteBuffer);
/*  397 */       return n;
/*      */     } finally {
/*  399 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int receiveIntoNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  407 */     int i = receive0(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + paramInt2, paramInt1, isConnected());
/*      */ 
/*  409 */     if (i > 0)
/*  410 */       paramByteBuffer.position(paramInt2 + i);
/*  411 */     return i;
/*      */   }
/*      */ 
/*      */   public int send(ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress)
/*      */     throws IOException
/*      */   {
/*  417 */     if (paramByteBuffer == null) {
/*  418 */       throw new NullPointerException();
/*      */     }
/*  420 */     synchronized (this.writeLock) {
/*  421 */       ensureOpen();
/*  422 */       InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*  423 */       InetAddress localInetAddress = localInetSocketAddress.getAddress();
/*  424 */       if (localInetAddress == null)
/*  425 */         throw new IOException("Target address not resolved");
/*  426 */       synchronized (this.stateLock) {
/*  427 */         if (!isConnected()) {
/*  428 */           if (paramSocketAddress == null)
/*  429 */             throw new NullPointerException();
/*  430 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  431 */           if (localSecurityManager != null) {
/*  432 */             if (localInetAddress.isMulticastAddress())
/*  433 */               localSecurityManager.checkMulticast(localInetAddress);
/*      */             else
/*  435 */               localSecurityManager.checkConnect(localInetAddress.getHostAddress(), localInetSocketAddress.getPort());
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  440 */           if (!paramSocketAddress.equals(this.remoteAddress)) {
/*  441 */             throw new IllegalArgumentException("Connected address not equal to target address");
/*      */           }
/*      */ 
/*  444 */           return write(paramByteBuffer);
/*      */         }
/*      */       }
/*      */ 
/*  448 */       int i = 0;
/*      */       try {
/*  450 */         begin();
/*  451 */         if (!isOpen()) {
/*  452 */           int j = 0;
/*      */ 
/*  465 */           this.writerThread = 0L;
/*  466 */           end((i > 0) || (i == -2));
/*  467 */           assert (IOStatus.check(i)); return j;
/*      */         }
/*  453 */         this.writerThread = NativeThread.current();
/*      */         do
/*  455 */           i = send(this.fd, paramByteBuffer, localInetSocketAddress);
/*  456 */         while ((i == -3) && (isOpen()));
/*      */ 
/*  458 */         synchronized (this.stateLock) {
/*  459 */           if ((isOpen()) && (this.localAddress == null)) {
/*  460 */             this.localAddress = Net.localAddress(this.fd);
/*      */           }
/*      */         }
/*  463 */         int k = IOStatus.normalize(i);
/*      */ 
/*  465 */         this.writerThread = 0L;
/*  466 */         end((i > 0) || (i == -2));
/*  467 */         assert (IOStatus.check(i)); return k;
/*      */       }
/*      */       finally
/*      */       {
/*  465 */         this.writerThread = 0L;
/*  466 */         end((i > 0) || (i == -2));
/*  467 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int send(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress)
/*      */     throws IOException
/*      */   {
/*  475 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/*  476 */       return sendFromNativeBuffer(paramFileDescriptor, paramByteBuffer, paramInetSocketAddress);
/*      */     }
/*      */ 
/*  479 */     int i = paramByteBuffer.position();
/*  480 */     int j = paramByteBuffer.limit();
/*  481 */     assert (i <= j);
/*  482 */     int k = i <= j ? j - i : 0;
/*      */ 
/*  484 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(k);
/*      */     try {
/*  486 */       localByteBuffer.put(paramByteBuffer);
/*  487 */       localByteBuffer.flip();
/*      */ 
/*  489 */       paramByteBuffer.position(i);
/*      */ 
/*  491 */       int m = sendFromNativeBuffer(paramFileDescriptor, localByteBuffer, paramInetSocketAddress);
/*  492 */       if (m > 0)
/*      */       {
/*  494 */         paramByteBuffer.position(i + m);
/*      */       }
/*  496 */       return m;
/*      */     } finally {
/*  498 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */   private int sendFromNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress) throws IOException {
/*  506 */     int i = paramByteBuffer.position();
/*  507 */     int j = paramByteBuffer.limit();
/*  508 */     assert (i <= j);
/*  509 */     int k = i <= j ? j - i : 0;
/*      */ 
/*  511 */     boolean bool = this.family != StandardProtocolFamily.INET;
/*      */     int m;
/*      */     try { m = send0(bool, paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
/*      */     } catch (PortUnreachableException localPortUnreachableException)
/*      */     {
/*  517 */       if (isConnected())
/*  518 */         throw localPortUnreachableException;
/*  519 */       m = k;
/*      */     }
/*  521 */     if (m > 0)
/*  522 */       paramByteBuffer.position(i + m);
/*  523 */     return m;
/*      */   }
/*      */ 
/*      */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/*  527 */     if (paramByteBuffer == null)
/*  528 */       throw new NullPointerException();
/*  529 */     synchronized (this.readLock) {
/*  530 */       synchronized (this.stateLock) {
/*  531 */         ensureOpen();
/*  532 */         if (!isConnected())
/*  533 */           throw new NotYetConnectedException();
/*      */       }
/*  535 */       int i = 0;
/*      */       try {
/*  537 */         begin();
/*  538 */         if (!isOpen()) {
/*  539 */           j = 0;
/*      */ 
/*  546 */           this.readerThread = 0L;
/*  547 */           end((i > 0) || (i == -2));
/*  548 */           assert (IOStatus.check(i)); return j;
/*      */         }
/*  540 */         this.readerThread = NativeThread.current();
/*      */         do
/*  542 */           i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd, this.readLock);
/*  543 */         while ((i == -3) && (isOpen()));
/*  544 */         int j = IOStatus.normalize(i);
/*      */ 
/*  546 */         this.readerThread = 0L;
/*  547 */         end((i > 0) || (i == -2));
/*  548 */         assert (IOStatus.check(i)); return j;
/*      */       }
/*      */       finally
/*      */       {
/*  546 */         this.readerThread = 0L;
/*  547 */         end((i > 0) || (i == -2));
/*  548 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  556 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  557 */       throw new IndexOutOfBoundsException();
/*  558 */     synchronized (this.readLock) {
/*  559 */       synchronized (this.stateLock) {
/*  560 */         ensureOpen();
/*  561 */         if (!isConnected())
/*  562 */           throw new NotYetConnectedException();
/*      */       }
/*  564 */       long l1 = 0L;
/*      */       try {
/*  566 */         begin();
/*  567 */         if (!isOpen()) {
/*  568 */           l2 = 0L;
/*      */ 
/*  575 */           this.readerThread = 0L;
/*  576 */           end((l1 > 0L) || (l1 == -2L));
/*  577 */           assert (IOStatus.check(l1)); return l2;
/*      */         }
/*  569 */         this.readerThread = NativeThread.current();
/*      */         do
/*  571 */           l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/*  572 */         while ((l1 == -3L) && (isOpen()));
/*  573 */         long l2 = IOStatus.normalize(l1);
/*      */ 
/*  575 */         this.readerThread = 0L;
/*  576 */         end((l1 > 0L) || (l1 == -2L));
/*  577 */         assert (IOStatus.check(l1)); return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  575 */         this.readerThread = 0L;
/*  576 */         end((l1 > 0L) || (l1 == -2L));
/*  577 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*  583 */     if (paramByteBuffer == null)
/*  584 */       throw new NullPointerException();
/*  585 */     synchronized (this.writeLock) {
/*  586 */       synchronized (this.stateLock) {
/*  587 */         ensureOpen();
/*  588 */         if (!isConnected())
/*  589 */           throw new NotYetConnectedException();
/*      */       }
/*  591 */       int i = 0;
/*      */       try {
/*  593 */         begin();
/*  594 */         if (!isOpen()) {
/*  595 */           j = 0;
/*      */ 
/*  602 */           this.writerThread = 0L;
/*  603 */           end((i > 0) || (i == -2));
/*  604 */           assert (IOStatus.check(i)); return j;
/*      */         }
/*  596 */         this.writerThread = NativeThread.current();
/*      */         do
/*  598 */           i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd, this.writeLock);
/*  599 */         while ((i == -3) && (isOpen()));
/*  600 */         int j = IOStatus.normalize(i);
/*      */ 
/*  602 */         this.writerThread = 0L;
/*  603 */         end((i > 0) || (i == -2));
/*  604 */         assert (IOStatus.check(i)); return j;
/*      */       }
/*      */       finally
/*      */       {
/*  602 */         this.writerThread = 0L;
/*  603 */         end((i > 0) || (i == -2));
/*  604 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  612 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  613 */       throw new IndexOutOfBoundsException();
/*  614 */     synchronized (this.writeLock) {
/*  615 */       synchronized (this.stateLock) {
/*  616 */         ensureOpen();
/*  617 */         if (!isConnected())
/*  618 */           throw new NotYetConnectedException();
/*      */       }
/*  620 */       long l1 = 0L;
/*      */       try {
/*  622 */         begin();
/*  623 */         if (!isOpen()) {
/*  624 */           l2 = 0L;
/*      */ 
/*  631 */           this.writerThread = 0L;
/*  632 */           end((l1 > 0L) || (l1 == -2L));
/*  633 */           assert (IOStatus.check(l1)); return l2;
/*      */         }
/*  625 */         this.writerThread = NativeThread.current();
/*      */         do
/*  627 */           l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/*  628 */         while ((l1 == -3L) && (isOpen()));
/*  629 */         long l2 = IOStatus.normalize(l1);
/*      */ 
/*  631 */         this.writerThread = 0L;
/*  632 */         end((l1 > 0L) || (l1 == -2L));
/*  633 */         assert (IOStatus.check(l1)); return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  631 */         this.writerThread = 0L;
/*  632 */         end((l1 > 0L) || (l1 == -2L));
/*  633 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
/*  639 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*      */   }
/*      */ 
/*      */   public SocketAddress localAddress() {
/*  643 */     synchronized (this.stateLock) {
/*  644 */       return this.localAddress;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SocketAddress remoteAddress() {
/*  649 */     synchronized (this.stateLock) {
/*  650 */       return this.remoteAddress;
/*      */     }
/*      */   }
/*      */ 
/*      */   public DatagramChannel bind(SocketAddress paramSocketAddress) throws IOException
/*      */   {
/*  656 */     synchronized (this.readLock) {
/*  657 */       synchronized (this.writeLock) {
/*  658 */         synchronized (this.stateLock) {
/*  659 */           ensureOpen();
/*  660 */           if (this.localAddress != null)
/*  661 */             throw new AlreadyBoundException();
/*      */           InetSocketAddress localInetSocketAddress;
/*  663 */           if (paramSocketAddress == null)
/*      */           {
/*  665 */             if (this.family == StandardProtocolFamily.INET)
/*  666 */               localInetSocketAddress = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
/*      */             else
/*  668 */               localInetSocketAddress = new InetSocketAddress(0);
/*      */           }
/*      */           else {
/*  671 */             localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*      */ 
/*  674 */             if (this.family == StandardProtocolFamily.INET) {
/*  675 */               localObject1 = localInetSocketAddress.getAddress();
/*  676 */               if (!(localObject1 instanceof Inet4Address))
/*  677 */                 throw new UnsupportedAddressTypeException();
/*      */             }
/*      */           }
/*  680 */           Object localObject1 = System.getSecurityManager();
/*  681 */           if (localObject1 != null) {
/*  682 */             ((SecurityManager)localObject1).checkListen(localInetSocketAddress.getPort());
/*      */           }
/*  684 */           Net.bind(this.family, this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*  685 */           this.localAddress = Net.localAddress(this.fd);
/*      */         }
/*      */       }
/*      */     }
/*  689 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean isConnected() {
/*  693 */     synchronized (this.stateLock) {
/*  694 */       return this.state == 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   void ensureOpenAndUnconnected() throws IOException {
/*  699 */     synchronized (this.stateLock) {
/*  700 */       if (!isOpen())
/*  701 */         throw new ClosedChannelException();
/*  702 */       if (this.state != 0)
/*  703 */         throw new IllegalStateException("Connect already invoked");
/*      */     }
/*      */   }
/*      */ 
/*      */   public DatagramChannel connect(SocketAddress paramSocketAddress) throws IOException {
/*  708 */     int i = 0;
/*      */ 
/*  710 */     synchronized (this.readLock) {
/*  711 */       synchronized (this.writeLock) {
/*  712 */         synchronized (this.stateLock) {
/*  713 */           ensureOpenAndUnconnected();
/*  714 */           InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*  715 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  716 */           if (localSecurityManager != null) {
/*  717 */             localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
/*      */           }
/*  719 */           int j = Net.connect(this.family, this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*      */ 
/*  723 */           if (j <= 0) {
/*  724 */             throw new Error();
/*      */           }
/*      */ 
/*  727 */           this.state = 1;
/*  728 */           this.remoteAddress = paramSocketAddress;
/*  729 */           this.sender = localInetSocketAddress;
/*  730 */           this.cachedSenderInetAddress = localInetSocketAddress.getAddress();
/*  731 */           this.cachedSenderPort = localInetSocketAddress.getPort();
/*      */ 
/*  734 */           this.localAddress = Net.localAddress(this.fd);
/*      */         }
/*      */       }
/*      */     }
/*  738 */     return this;
/*      */   }
/*      */ 
/*      */   public DatagramChannel disconnect() throws IOException {
/*  742 */     synchronized (this.readLock) {
/*  743 */       synchronized (this.writeLock) {
/*  744 */         synchronized (this.stateLock) {
/*  745 */           if ((!isConnected()) || (!isOpen()))
/*  746 */             return this;
/*  747 */           InetSocketAddress localInetSocketAddress = (InetSocketAddress)this.remoteAddress;
/*  748 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  749 */           if (localSecurityManager != null) {
/*  750 */             localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
/*      */           }
/*  752 */           disconnect0(this.fd);
/*  753 */           this.remoteAddress = null;
/*  754 */           this.state = 0;
/*      */ 
/*  757 */           this.localAddress = Net.localAddress(this.fd);
/*      */         }
/*      */       }
/*      */     }
/*  761 */     return this;
/*      */   }
/*      */ 
/*      */   private MembershipKey innerJoin(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
/*      */     throws IOException
/*      */   {
/*  773 */     if (!paramInetAddress1.isMulticastAddress()) {
/*  774 */       throw new IllegalArgumentException("Group not a multicast address");
/*      */     }
/*      */ 
/*  777 */     if ((paramInetAddress1 instanceof Inet4Address)) {
/*  778 */       if ((this.family == StandardProtocolFamily.INET6) && (!Net.canIPv6SocketJoinIPv4Group()))
/*  779 */         throw new IllegalArgumentException("IPv6 socket cannot join IPv4 multicast group");
/*  780 */     } else if ((paramInetAddress1 instanceof Inet6Address)) {
/*  781 */       if (this.family != StandardProtocolFamily.INET6)
/*  782 */         throw new IllegalArgumentException("Only IPv6 sockets can join IPv6 multicast group");
/*      */     }
/*  784 */     else throw new IllegalArgumentException("Address type not supported");
/*      */ 
/*  788 */     if (paramInetAddress2 != null) {
/*  789 */       if (paramInetAddress2.isAnyLocalAddress())
/*  790 */         throw new IllegalArgumentException("Source address is a wildcard address");
/*  791 */       if (paramInetAddress2.isMulticastAddress())
/*  792 */         throw new IllegalArgumentException("Source address is multicast address");
/*  793 */       if (paramInetAddress2.getClass() != paramInetAddress1.getClass()) {
/*  794 */         throw new IllegalArgumentException("Source address is different type to group");
/*      */       }
/*      */     }
/*  797 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  798 */     if (localSecurityManager != null) {
/*  799 */       localSecurityManager.checkMulticast(paramInetAddress1);
/*      */     }
/*  801 */     synchronized (this.stateLock) {
/*  802 */       if (!isOpen())
/*  803 */         throw new ClosedChannelException();
/*      */       Object localObject1;
/*  806 */       if (this.registry == null) {
/*  807 */         this.registry = new MembershipRegistry();
/*      */       }
/*      */       else {
/*  810 */         localObject1 = this.registry.checkMembership(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
/*  811 */         if (localObject1 != null)
/*  812 */           return localObject1;
/*      */       }
/*      */       int m;
/*  816 */       if ((this.family == StandardProtocolFamily.INET6) && (((paramInetAddress1 instanceof Inet6Address)) || (Net.canJoin6WithIPv4Group())))
/*      */       {
/*  819 */         int i = paramNetworkInterface.getIndex();
/*  820 */         if (i == -1) {
/*  821 */           throw new IOException("Network interface cannot be identified");
/*      */         }
/*      */ 
/*  824 */         byte[] arrayOfByte1 = Net.inet6AsByteArray(paramInetAddress1);
/*  825 */         byte[] arrayOfByte2 = paramInetAddress2 == null ? null : Net.inet6AsByteArray(paramInetAddress2);
/*      */ 
/*  829 */         m = Net.join6(this.fd, arrayOfByte1, i, arrayOfByte2);
/*  830 */         if (m == -2) {
/*  831 */           throw new UnsupportedOperationException();
/*      */         }
/*  833 */         localObject1 = new MembershipKeyImpl.Type6(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, arrayOfByte1, i, arrayOfByte2);
/*      */       }
/*      */       else
/*      */       {
/*  838 */         Inet4Address localInet4Address = Net.anyInet4Address(paramNetworkInterface);
/*  839 */         if (localInet4Address == null) {
/*  840 */           throw new IOException("Network interface not configured for IPv4");
/*      */         }
/*  842 */         int j = Net.inet4AsInt(paramInetAddress1);
/*  843 */         int k = Net.inet4AsInt(localInet4Address);
/*  844 */         m = paramInetAddress2 == null ? 0 : Net.inet4AsInt(paramInetAddress2);
/*      */ 
/*  847 */         int n = Net.join4(this.fd, j, k, m);
/*  848 */         if (n == -2) {
/*  849 */           throw new UnsupportedOperationException();
/*      */         }
/*  851 */         localObject1 = new MembershipKeyImpl.Type4(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, j, k, m);
/*      */       }
/*      */ 
/*  855 */       this.registry.add((MembershipKeyImpl)localObject1);
/*  856 */       return localObject1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public MembershipKey join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
/*      */     throws IOException
/*      */   {
/*  865 */     return innerJoin(paramInetAddress, paramNetworkInterface, null);
/*      */   }
/*      */ 
/*      */   public MembershipKey join(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
/*      */     throws IOException
/*      */   {
/*  874 */     if (paramInetAddress2 == null)
/*  875 */       throw new NullPointerException("source address is null");
/*  876 */     return innerJoin(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
/*      */   }
/*      */ 
/*      */   void drop(MembershipKeyImpl paramMembershipKeyImpl)
/*      */   {
/*  881 */     assert (paramMembershipKeyImpl.channel() == this);
/*      */ 
/*  883 */     synchronized (this.stateLock) {
/*  884 */       if (!paramMembershipKeyImpl.isValid())
/*  885 */         return;
/*      */       try
/*      */       {
/*      */         Object localObject1;
/*  888 */         if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6)) {
/*  889 */           localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
/*      */ 
/*  891 */           Net.drop6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), ((MembershipKeyImpl.Type6)localObject1).source());
/*      */         } else {
/*  893 */           localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
/*  894 */           Net.drop4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), ((MembershipKeyImpl.Type4)localObject1).source());
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  899 */         throw new AssertionError(localIOException);
/*      */       }
/*      */ 
/*  902 */       paramMembershipKeyImpl.invalidate();
/*  903 */       this.registry.remove(paramMembershipKeyImpl);
/*      */     }
/*      */   }
/*      */ 
/*      */   void block(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress)
/*      */     throws IOException
/*      */   {
/*  914 */     assert (paramMembershipKeyImpl.channel() == this);
/*  915 */     assert (paramMembershipKeyImpl.sourceAddress() == null);
/*      */ 
/*  917 */     synchronized (this.stateLock) {
/*  918 */       if (!paramMembershipKeyImpl.isValid())
/*  919 */         throw new IllegalStateException("key is no longer valid");
/*  920 */       if (paramInetAddress.isAnyLocalAddress())
/*  921 */         throw new IllegalArgumentException("Source address is a wildcard address");
/*  922 */       if (paramInetAddress.isMulticastAddress())
/*  923 */         throw new IllegalArgumentException("Source address is multicast address");
/*  924 */       if (paramInetAddress.getClass() != paramMembershipKeyImpl.group().getClass())
/*  925 */         throw new IllegalArgumentException("Source address is different type to group");
/*      */       Object localObject1;
/*      */       int i;
/*  928 */       if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6)) {
/*  929 */         localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
/*      */ 
/*  931 */         i = Net.block6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), Net.inet6AsByteArray(paramInetAddress));
/*      */       }
/*      */       else {
/*  934 */         localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
/*      */ 
/*  936 */         i = Net.block4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), Net.inet4AsInt(paramInetAddress));
/*      */       }
/*      */ 
/*  939 */       if (i == -2)
/*      */       {
/*  941 */         throw new UnsupportedOperationException();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void unblock(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress)
/*      */   {
/*  950 */     assert (paramMembershipKeyImpl.channel() == this);
/*  951 */     assert (paramMembershipKeyImpl.sourceAddress() == null);
/*      */ 
/*  953 */     synchronized (this.stateLock) {
/*  954 */       if (!paramMembershipKeyImpl.isValid())
/*  955 */         throw new IllegalStateException("key is no longer valid");
/*      */       try
/*      */       {
/*      */         Object localObject1;
/*  958 */         if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6)) {
/*  959 */           localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
/*      */ 
/*  961 */           Net.unblock6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), Net.inet6AsByteArray(paramInetAddress));
/*      */         }
/*      */         else {
/*  964 */           localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
/*      */ 
/*  966 */           Net.unblock4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), Net.inet4AsInt(paramInetAddress));
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  971 */         throw new AssertionError(localIOException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void implCloseSelectableChannel() throws IOException {
/*  977 */     synchronized (this.stateLock) {
/*  978 */       if (this.state != 2)
/*  979 */         nd.preClose(this.fd);
/*  980 */       ResourceManager.afterUdpClose();
/*      */ 
/*  983 */       if (this.registry != null)
/*  984 */         this.registry.invalidateAll();
/*      */       long l;
/*  987 */       if ((l = this.readerThread) != 0L)
/*  988 */         NativeThread.signal(l);
/*  989 */       if ((l = this.writerThread) != 0L)
/*  990 */         NativeThread.signal(l);
/*  991 */       if (!isRegistered())
/*  992 */         kill();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void kill() throws IOException {
/*  997 */     synchronized (this.stateLock) {
/*  998 */       if (this.state == 2)
/*  999 */         return;
/* 1000 */       if (this.state == -1) {
/* 1001 */         this.state = 2;
/* 1002 */         return;
/*      */       }
/* 1004 */       assert ((!isOpen()) && (!isRegistered()));
/* 1005 */       nd.close(this.fd);
/* 1006 */       this.state = 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void finalize() throws IOException
/*      */   {
/* 1012 */     if (this.fd != null)
/* 1013 */       close();
/*      */   }
/*      */ 
/*      */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/* 1021 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 1022 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 1023 */     int k = paramInt2;
/*      */ 
/* 1025 */     if ((paramInt1 & 0x20) != 0)
/*      */     {
/* 1029 */       return false;
/*      */     }
/*      */ 
/* 1032 */     if ((paramInt1 & 0x18) != 0)
/*      */     {
/* 1034 */       k = i;
/* 1035 */       paramSelectionKeyImpl.nioReadyOps(k);
/* 1036 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */     }
/*      */ 
/* 1039 */     if (((paramInt1 & 0x1) != 0) && ((i & 0x1) != 0))
/*      */     {
/* 1041 */       k |= 1;
/*      */     }
/* 1043 */     if (((paramInt1 & 0x4) != 0) && ((i & 0x4) != 0))
/*      */     {
/* 1045 */       k |= 4;
/*      */     }
/* 1047 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 1048 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */   }
/*      */ 
/*      */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 1052 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*      */   }
/*      */ 
/*      */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 1056 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*      */   }
/*      */ 
/*      */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/* 1063 */     int i = 0;
/*      */ 
/* 1065 */     if ((paramInt & 0x1) != 0)
/* 1066 */       i |= 1;
/* 1067 */     if ((paramInt & 0x4) != 0)
/* 1068 */       i |= 4;
/* 1069 */     if ((paramInt & 0x8) != 0)
/* 1070 */       i |= 1;
/* 1071 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*      */   }
/*      */ 
/*      */   public FileDescriptor getFD() {
/* 1075 */     return this.fd;
/*      */   }
/*      */ 
/*      */   public int getFDVal() {
/* 1079 */     return this.fdVal;
/*      */   }
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   private static native void disconnect0(FileDescriptor paramFileDescriptor)
/*      */     throws IOException;
/*      */ 
/*      */   private native int receive0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean)
/*      */     throws IOException;
/*      */ 
/*      */   private native int send0(boolean paramBoolean, FileDescriptor paramFileDescriptor, long paramLong, int paramInt1, InetAddress paramInetAddress, int paramInt2)
/*      */     throws IOException;
/*      */ 
/*      */   static
/*      */   {
/*   48 */     nd = new DatagramDispatcher();
/*      */ 
/* 1099 */     Util.load();
/* 1100 */     initIDs();
/*      */   }
/*      */ 
/*      */   private static class DefaultOptionsHolder
/*      */   {
/*  290 */     static final Set<SocketOption<?>> defaultOptions = defaultOptions();
/*      */ 
/*      */     private static Set<SocketOption<?>> defaultOptions() {
/*  293 */       HashSet localHashSet = new HashSet(8);
/*  294 */       localHashSet.add(StandardSocketOptions.SO_SNDBUF);
/*  295 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/*  296 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/*  297 */       localHashSet.add(StandardSocketOptions.SO_BROADCAST);
/*  298 */       localHashSet.add(StandardSocketOptions.IP_TOS);
/*  299 */       localHashSet.add(StandardSocketOptions.IP_MULTICAST_IF);
/*  300 */       localHashSet.add(StandardSocketOptions.IP_MULTICAST_TTL);
/*  301 */       localHashSet.add(StandardSocketOptions.IP_MULTICAST_LOOP);
/*  302 */       return Collections.unmodifiableSet(localHashSet);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.DatagramChannelImpl
 * JD-Core Version:    0.6.2
 */