/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardProtocolFamily;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.AlreadyConnectedException;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.ConnectionPendingException;
/*     */ import java.nio.channels.NoConnectionPendingException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import sun.net.NetHooks;
/*     */ 
/*     */ class SocketChannelImpl extends SocketChannel
/*     */   implements SelChImpl
/*     */ {
/* 988 */   private static NativeDispatcher nd = new SocketDispatcher();
/*     */   private final FileDescriptor fd;
/*     */   private final int fdVal;
/*  58 */   private volatile long readerThread = 0L;
/*  59 */   private volatile long writerThread = 0L;
/*     */ 
/*  62 */   private final Object readLock = new Object();
/*     */ 
/*  65 */   private final Object writeLock = new Object();
/*     */ 
/*  69 */   private final Object stateLock = new Object();
/*     */   private static final int ST_UNINITIALIZED = -1;
/*     */   private static final int ST_UNCONNECTED = 0;
/*     */   private static final int ST_PENDING = 1;
/*     */   private static final int ST_CONNECTED = 2;
/*     */   private static final int ST_KILLPENDING = 3;
/*     */   private static final int ST_KILLED = 4;
/*  80 */   private int state = -1;
/*     */   private SocketAddress localAddress;
/*     */   private SocketAddress remoteAddress;
/*  87 */   private boolean isInputOpen = true;
/*  88 */   private boolean isOutputOpen = true;
/*  89 */   private boolean readyToConnect = false;
/*     */   private Socket socket;
/*     */ 
/*     */   SocketChannelImpl(SelectorProvider paramSelectorProvider)
/*     */     throws IOException
/*     */   {
/* 100 */     super(paramSelectorProvider);
/* 101 */     this.fd = Net.socket(true);
/* 102 */     this.fdVal = IOUtil.fdVal(this.fd);
/* 103 */     this.state = 0;
/*     */   }
/*     */ 
/*     */   SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 111 */     super(paramSelectorProvider);
/* 112 */     this.fd = paramFileDescriptor;
/* 113 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/* 114 */     this.state = 0;
/* 115 */     if (paramBoolean)
/* 116 */       this.localAddress = Net.localAddress(paramFileDescriptor);
/*     */   }
/*     */ 
/*     */   SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
/*     */     throws IOException
/*     */   {
/* 125 */     super(paramSelectorProvider);
/* 126 */     this.fd = paramFileDescriptor;
/* 127 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/* 128 */     this.state = 2;
/* 129 */     this.localAddress = Net.localAddress(paramFileDescriptor);
/* 130 */     this.remoteAddress = paramInetSocketAddress;
/*     */   }
/*     */ 
/*     */   public Socket socket() {
/* 134 */     synchronized (this.stateLock) {
/* 135 */       if (this.socket == null)
/* 136 */         this.socket = SocketAdaptor.create(this);
/* 137 */       return this.socket;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketAddress getLocalAddress() throws IOException
/*     */   {
/* 143 */     synchronized (this.stateLock) {
/* 144 */       if (!isOpen())
/* 145 */         throw new ClosedChannelException();
/* 146 */       return this.localAddress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketAddress getRemoteAddress() throws IOException
/*     */   {
/* 152 */     synchronized (this.stateLock) {
/* 153 */       if (!isOpen())
/* 154 */         throw new ClosedChannelException();
/* 155 */       return this.remoteAddress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> SocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/* 163 */     if (paramSocketOption == null)
/* 164 */       throw new NullPointerException();
/* 165 */     if (!supportedOptions().contains(paramSocketOption)) {
/* 166 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     }
/* 168 */     synchronized (this.stateLock) {
/* 169 */       if (!isOpen()) {
/* 170 */         throw new ClosedChannelException();
/*     */       }
/*     */ 
/* 173 */       if (paramSocketOption == StandardSocketOptions.IP_TOS) {
/* 174 */         if (!Net.isIPv6Available())
/* 175 */           Net.setSocketOption(this.fd, StandardProtocolFamily.INET, paramSocketOption, paramT);
/* 176 */         return this;
/*     */       }
/*     */ 
/* 180 */       Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/* 181 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T getOption(SocketOption<T> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 190 */     if (paramSocketOption == null)
/* 191 */       throw new NullPointerException();
/* 192 */     if (!supportedOptions().contains(paramSocketOption)) {
/* 193 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     }
/* 195 */     synchronized (this.stateLock) {
/* 196 */       if (!isOpen()) {
/* 197 */         throw new ClosedChannelException();
/*     */       }
/*     */ 
/* 200 */       if (paramSocketOption == StandardSocketOptions.IP_TOS) {
/* 201 */         return Net.isIPv6Available() ? Integer.valueOf(0) : Net.getSocketOption(this.fd, StandardProtocolFamily.INET, paramSocketOption);
/*     */       }
/*     */ 
/* 206 */       return Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Set<SocketOption<?>> supportedOptions()
/*     */   {
/* 230 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */ 
/*     */   private boolean ensureReadOpen() throws ClosedChannelException {
/* 234 */     synchronized (this.stateLock) {
/* 235 */       if (!isOpen())
/* 236 */         throw new ClosedChannelException();
/* 237 */       if (!isConnected())
/* 238 */         throw new NotYetConnectedException();
/* 239 */       if (!this.isInputOpen) {
/* 240 */         return false;
/*     */       }
/* 242 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void ensureWriteOpen() throws ClosedChannelException {
/* 247 */     synchronized (this.stateLock) {
/* 248 */       if (!isOpen())
/* 249 */         throw new ClosedChannelException();
/* 250 */       if (!this.isOutputOpen)
/* 251 */         throw new ClosedChannelException();
/* 252 */       if (!isConnected())
/* 253 */         throw new NotYetConnectedException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readerCleanup() throws IOException {
/* 258 */     synchronized (this.stateLock) {
/* 259 */       this.readerThread = 0L;
/* 260 */       if (this.state == 3)
/* 261 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writerCleanup() throws IOException {
/* 266 */     synchronized (this.stateLock) {
/* 267 */       this.writerThread = 0L;
/* 268 */       if (this.state == 3)
/* 269 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read(ByteBuffer paramByteBuffer) throws IOException
/*     */   {
/* 275 */     if (paramByteBuffer == null) {
/* 276 */       throw new NullPointerException();
/*     */     }
/* 278 */     synchronized (this.readLock) {
/* 279 */       if (!ensureReadOpen())
/* 280 */         return -1;
/* 281 */       int i = 0;
/*     */       try
/*     */       {
/* 287 */         begin();
/*     */ 
/* 289 */         synchronized (this.stateLock) {
/* 290 */           if (!isOpen())
/*     */           {
/* 298 */             int k = 0;
/*     */ 
/* 369 */             readerCleanup();
/*     */ 
/* 386 */             end((i > 0) || (i == -2));
/*     */ 
/* 390 */             synchronized (this.stateLock) {
/* 391 */               if ((i <= 0) && (!this.isInputOpen)) {
/* 392 */                 return -1;
/*     */               }
/*     */             }
/* 395 */             assert (IOStatus.check(i)); return k;
/*     */           }
/* 305 */           this.readerThread = NativeThread.current();
/*     */         }
/*     */ 
/*     */         do
/*     */         {
/* 359 */           i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd, this.readLock);
/* 360 */         }while ((i == -3) && (isOpen()));
/*     */ 
/* 365 */         int j = IOStatus.normalize(i);
/*     */ 
/* 369 */         readerCleanup();
/*     */ 
/* 386 */         end((i > 0) || (i == -2));
/*     */ 
/* 390 */         synchronized (this.stateLock) {
/* 391 */           if ((i <= 0) && (!this.isInputOpen)) {
/* 392 */             return -1;
/*     */           }
/*     */         }
/* 395 */         assert (IOStatus.check(i)); return j;
/*     */       }
/*     */       finally
/*     */       {
/* 369 */         readerCleanup();
/*     */ 
/* 386 */         end((i > 0) || (i == -2));
/*     */ 
/* 390 */         synchronized (this.stateLock) {
/* 391 */           if ((i <= 0) && (!this.isInputOpen)) {
/* 392 */             return -1;
/*     */           }
/*     */         }
/* 395 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 404 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 405 */       throw new IndexOutOfBoundsException();
/* 406 */     synchronized (this.readLock) {
/* 407 */       if (!ensureReadOpen())
/* 408 */         return -1L;
/* 409 */       long l1 = 0L;
/*     */       try {
/* 411 */         begin();
/* 412 */         synchronized (this.stateLock) {
/* 413 */           if (!isOpen()) {
/* 414 */             long l3 = 0L;
/*     */ 
/* 425 */             readerCleanup();
/* 426 */             end((l1 > 0L) || (l1 == -2L));
/* 427 */             synchronized (this.stateLock) {
/* 428 */               if ((l1 <= 0L) && (!this.isInputOpen))
/* 429 */                 return -1L;
/*     */             }
/* 431 */             assert (IOStatus.check(l1)); return l3;
/*     */           }
/* 415 */           this.readerThread = NativeThread.current();
/*     */         }
/*     */         do
/*     */         {
/* 419 */           l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/* 420 */         }while ((l1 == -3L) && (isOpen()));
/*     */ 
/* 422 */         long l2 = IOStatus.normalize(l1);
/*     */ 
/* 425 */         readerCleanup();
/* 426 */         end((l1 > 0L) || (l1 == -2L));
/* 427 */         synchronized (this.stateLock) {
/* 428 */           if ((l1 <= 0L) && (!this.isInputOpen))
/* 429 */             return -1L;
/*     */         }
/* 431 */         assert (IOStatus.check(l1)); return l2;
/*     */       }
/*     */       finally
/*     */       {
/* 425 */         readerCleanup();
/* 426 */         end((l1 > 0L) || (l1 == -2L));
/* 427 */         synchronized (this.stateLock) {
/* 428 */           if ((l1 <= 0L) && (!this.isInputOpen))
/* 429 */             return -1L;
/*     */         }
/* 431 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError(); 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/* 437 */     if (paramByteBuffer == null)
/* 438 */       throw new NullPointerException();
/* 439 */     synchronized (this.writeLock) {
/* 440 */       ensureWriteOpen();
/* 441 */       int i = 0;
/*     */       try {
/* 443 */         begin();
/* 444 */         synchronized (this.stateLock) {
/* 445 */           if (!isOpen()) {
/* 446 */             int k = 0;
/*     */ 
/* 456 */             writerCleanup();
/* 457 */             end((i > 0) || (i == -2));
/* 458 */             synchronized (this.stateLock) {
/* 459 */               if ((i <= 0) && (!this.isOutputOpen))
/*     */                 throw new AsynchronousCloseException();
/*     */             }
/* 462 */             assert (IOStatus.check(i)); return k;
/*     */           }
/* 447 */           this.writerThread = NativeThread.current();
/*     */         }
/*     */         do
/* 450 */           i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd, this.writeLock);
/* 451 */         while ((i == -3) && (isOpen()));
/*     */ 
/* 453 */         int j = IOStatus.normalize(i);
/*     */ 
/* 456 */         writerCleanup();
/* 457 */         end((i > 0) || (i == -2));
/* 458 */         synchronized (this.stateLock) {
/* 459 */           if ((i <= 0) && (!this.isOutputOpen))
/*     */             throw new AsynchronousCloseException();
/*     */         }
/* 462 */         assert (IOStatus.check(i)); return j;
/*     */       }
/*     */       finally
/*     */       {
/* 456 */         writerCleanup();
/* 457 */         end((i > 0) || (i == -2));
/* 458 */         synchronized (this.stateLock) {
/* 459 */           if ((i <= 0) && (!this.isOutputOpen))
/* 460 */             throw new AsynchronousCloseException();
/*     */         }
/* 462 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 470 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 471 */       throw new IndexOutOfBoundsException();
/* 472 */     synchronized (this.writeLock) {
/* 473 */       ensureWriteOpen();
/* 474 */       long l1 = 0L;
/*     */       try {
/* 476 */         begin();
/* 477 */         synchronized (this.stateLock) {
/* 478 */           if (!isOpen()) {
/* 479 */             long l3 = 0L;
/*     */ 
/* 489 */             writerCleanup();
/* 490 */             end((l1 > 0L) || (l1 == -2L));
/* 491 */             synchronized (this.stateLock) {
/* 492 */               if ((l1 <= 0L) && (!this.isOutputOpen))
/*     */                 throw new AsynchronousCloseException();
/*     */             }
/* 495 */             assert (IOStatus.check(l1)); return l3;
/*     */           }
/* 480 */           this.writerThread = NativeThread.current();
/*     */         }
/*     */         do
/* 483 */           l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/* 484 */         while ((l1 == -3L) && (isOpen()));
/*     */ 
/* 486 */         long l2 = IOStatus.normalize(l1);
/*     */ 
/* 489 */         writerCleanup();
/* 490 */         end((l1 > 0L) || (l1 == -2L));
/* 491 */         synchronized (this.stateLock) {
/* 492 */           if ((l1 <= 0L) && (!this.isOutputOpen))
/*     */             throw new AsynchronousCloseException();
/*     */         }
/* 495 */         assert (IOStatus.check(l1)); return l2;
/*     */       }
/*     */       finally
/*     */       {
/* 489 */         writerCleanup();
/* 490 */         end((l1 > 0L) || (l1 == -2L));
/* 491 */         synchronized (this.stateLock) {
/* 492 */           if ((l1 <= 0L) && (!this.isOutputOpen))
/* 493 */             throw new AsynchronousCloseException();
/*     */         }
/* 495 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError(); 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   int sendOutOfBandData(byte paramByte)
/*     */     throws IOException
/*     */   {
/* 502 */     synchronized (this.writeLock) {
/* 503 */       ensureWriteOpen();
/* 504 */       int i = 0;
/*     */       try {
/* 506 */         begin();
/* 507 */         synchronized (this.stateLock) {
/* 508 */           if (!isOpen()) {
/* 509 */             int k = 0;
/*     */ 
/* 519 */             writerCleanup();
/* 520 */             end((i > 0) || (i == -2));
/* 521 */             synchronized (this.stateLock) {
/* 522 */               if ((i <= 0) && (!this.isOutputOpen))
/*     */                 throw new AsynchronousCloseException();
/*     */             }
/* 525 */             assert (IOStatus.check(i)); return k;
/*     */           }
/* 510 */           this.writerThread = NativeThread.current();
/*     */         }
/*     */         do
/* 513 */           i = sendOutOfBandData(this.fd, paramByte);
/* 514 */         while ((i == -3) && (isOpen()));
/*     */ 
/* 516 */         int j = IOStatus.normalize(i);
/*     */ 
/* 519 */         writerCleanup();
/* 520 */         end((i > 0) || (i == -2));
/* 521 */         synchronized (this.stateLock) {
/* 522 */           if ((i <= 0) && (!this.isOutputOpen))
/*     */             throw new AsynchronousCloseException();
/*     */         }
/* 525 */         assert (IOStatus.check(i)); return j;
/*     */       }
/*     */       finally
/*     */       {
/* 519 */         writerCleanup();
/* 520 */         end((i > 0) || (i == -2));
/* 521 */         synchronized (this.stateLock) {
/* 522 */           if ((i <= 0) && (!this.isOutputOpen))
/* 523 */             throw new AsynchronousCloseException();
/*     */         }
/* 525 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError(); 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
/* 531 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*     */   }
/*     */ 
/*     */   public SocketAddress localAddress() {
/* 535 */     synchronized (this.stateLock) {
/* 536 */       return this.localAddress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketAddress remoteAddress() {
/* 541 */     synchronized (this.stateLock) {
/* 542 */       return this.remoteAddress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketChannel bind(SocketAddress paramSocketAddress) throws IOException
/*     */   {
/* 548 */     synchronized (this.readLock) {
/* 549 */       synchronized (this.writeLock) {
/* 550 */         synchronized (this.stateLock) {
/* 551 */           if (!isOpen())
/* 552 */             throw new ClosedChannelException();
/* 553 */           if (this.state == 1)
/* 554 */             throw new ConnectionPendingException();
/* 555 */           if (this.localAddress != null)
/* 556 */             throw new AlreadyBoundException();
/* 557 */           InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*     */ 
/* 559 */           NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 560 */           Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 561 */           this.localAddress = Net.localAddress(this.fd);
/*     */         }
/*     */       }
/*     */     }
/* 565 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean isConnected() {
/* 569 */     synchronized (this.stateLock) {
/* 570 */       return this.state == 2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isConnectionPending() {
/* 575 */     synchronized (this.stateLock) {
/* 576 */       return this.state == 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   void ensureOpenAndUnconnected() throws IOException {
/* 581 */     synchronized (this.stateLock) {
/* 582 */       if (!isOpen())
/* 583 */         throw new ClosedChannelException();
/* 584 */       if (this.state == 2)
/* 585 */         throw new AlreadyConnectedException();
/* 586 */       if (this.state == 1)
/* 587 */         throw new ConnectionPendingException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean connect(SocketAddress paramSocketAddress) throws IOException {
/* 592 */     int i = 0;
/*     */ 
/* 594 */     synchronized (this.readLock) {
/* 595 */       synchronized (this.writeLock) {
/* 596 */         ensureOpenAndUnconnected();
/* 597 */         InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/* 598 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 599 */         if (localSecurityManager != null) {
/* 600 */           localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
/*     */         }
/* 602 */         synchronized (blockingLock()) {
/* 603 */           int j = 0;
/*     */           try {
/*     */             try {
/* 606 */               begin();
/* 607 */               synchronized (this.stateLock) {
/* 608 */                 if (!isOpen()) {
/* 609 */                   boolean bool = false;
/*     */ 
/* 633 */                   readerCleanup();
/* 634 */                   end((j > 0) || (j == -2));
/* 635 */                   assert (IOStatus.check(j)); return bool;
/*     */                 }
/* 612 */                 if (this.localAddress == null) {
/* 613 */                   NetHooks.beforeTcpConnect(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*     */                 }
/*     */ 
/* 617 */                 this.readerThread = NativeThread.current();
/*     */               }
/*     */               while (true) {
/* 620 */                 ??? = localInetSocketAddress.getAddress();
/* 621 */                 if (???.isAnyLocalAddress())
/* 622 */                   ??? = InetAddress.getLocalHost();
/* 623 */                 j = Net.connect(this.fd, ???, localInetSocketAddress.getPort());
/*     */ 
/* 626 */                 if ((j != -3) || (!isOpen())) {
/*     */                   break;
/*     */                 }
/*     */               }
/*     */             }
/*     */             finally
/*     */             {
/* 633 */               readerCleanup();
/* 634 */               end((j > 0) || (j == -2));
/* 635 */               if ((!$assertionsDisabled) && (!IOStatus.check(j))) throw new AssertionError();
/*     */             }
/*     */ 
/*     */           }
/*     */           catch (IOException )
/*     */           {
/* 641 */             close();
/* 642 */             throw ???;
/*     */           }
/* 644 */           synchronized (this.stateLock) {
/* 645 */             this.remoteAddress = localInetSocketAddress;
/* 646 */             if (j > 0)
/*     */             {
/* 650 */               this.state = 2;
/* 651 */               if (isOpen())
/* 652 */                 this.localAddress = Net.localAddress(this.fd);
/* 653 */               return true;
/*     */             }
/*     */ 
/* 657 */             if (!isBlocking()) {
/* 658 */               this.state = 1;
/*     */             }
/* 660 */             else if (!$assertionsDisabled) throw new AssertionError();
/*     */           }
/*     */         }
/* 663 */         return false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean finishConnect() throws IOException {
/* 669 */     synchronized (this.readLock) {
/* 670 */       synchronized (this.writeLock) {
/* 671 */         synchronized (this.stateLock) {
/* 672 */           if (!isOpen())
/* 673 */             throw new ClosedChannelException();
/* 674 */           if (this.state == 2)
/* 675 */             return true;
/* 676 */           if (this.state != 1)
/* 677 */             throw new NoConnectionPendingException();
/*     */         }
/* 679 */         int i = 0;
/*     */         try {
/*     */           try {
/* 682 */             begin();
/* 683 */             synchronized (blockingLock()) {
/* 684 */               synchronized (this.stateLock) {
/* 685 */                 if (!isOpen()) {
/* 686 */                   boolean bool = false;
/*     */ 
/* 716 */                   synchronized (this.stateLock) {
/* 717 */                     this.readerThread = 0L;
/* 718 */                     if (this.state == 3) {
/* 719 */                       kill();
/*     */ 
/* 725 */                       i = 0;
/*     */                     }
/*     */                   }
/* 728 */                   end((i > 0) || (i == -2));
/* 729 */                   assert (IOStatus.check(i)); return bool;
/*     */                 }
/* 688 */                 this.readerThread = NativeThread.current();
/*     */               }
/* 690 */               if (!isBlocking()) {
/*     */                 while (true) {
/* 692 */                   i = checkConnect(this.fd, false, this.readyToConnect);
/*     */ 
/* 694 */                   if ((i != -3) || (!isOpen())) {
/*     */                     break;
/*     */                   }
/*     */                 }
/*     */               }
/*     */               while (true)
/*     */               {
/* 701 */                 i = checkConnect(this.fd, true, this.readyToConnect);
/*     */ 
/* 703 */                 if (i != 0)
/*     */                 {
/* 708 */                   if ((i != -3) || (!isOpen()))
/*     */                     break;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 716 */             synchronized (this.stateLock) {
/* 717 */               this.readerThread = 0L;
/* 718 */               if (this.state == 3) {
/* 719 */                 kill();
/*     */ 
/* 725 */                 i = 0;
/*     */               }
/*     */             }
/* 728 */             end((i > 0) || (i == -2));
/* 729 */             if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (IOException localObject1)
/*     */         {
/* 735 */           close();
/* 736 */           throw ???;
/*     */         }
/* 738 */         if (i > 0) {
/* 739 */           synchronized (this.stateLock) {
/* 740 */             this.state = 2;
/* 741 */             if (isOpen())
/* 742 */               this.localAddress = Net.localAddress(this.fd);
/*     */           }
/* 744 */           return true;
/*     */         }
/* 746 */         return false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketChannel shutdownInput() throws IOException
/*     */   {
/* 753 */     synchronized (this.stateLock) {
/* 754 */       if (!isOpen())
/* 755 */         throw new ClosedChannelException();
/* 756 */       if (!isConnected())
/* 757 */         throw new NotYetConnectedException();
/* 758 */       if (this.isInputOpen) {
/* 759 */         Net.shutdown(this.fd, 0);
/* 760 */         if (this.readerThread != 0L)
/* 761 */           NativeThread.signal(this.readerThread);
/* 762 */         this.isInputOpen = false;
/*     */       }
/* 764 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public SocketChannel shutdownOutput() throws IOException
/*     */   {
/* 770 */     synchronized (this.stateLock) {
/* 771 */       if (!isOpen())
/* 772 */         throw new ClosedChannelException();
/* 773 */       if (!isConnected())
/* 774 */         throw new NotYetConnectedException();
/* 775 */       if (this.isOutputOpen) {
/* 776 */         Net.shutdown(this.fd, 1);
/* 777 */         if (this.writerThread != 0L)
/* 778 */           NativeThread.signal(this.writerThread);
/* 779 */         this.isOutputOpen = false;
/*     */       }
/* 781 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isInputOpen() {
/* 786 */     synchronized (this.stateLock) {
/* 787 */       return this.isInputOpen;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOutputOpen() {
/* 792 */     synchronized (this.stateLock) {
/* 793 */       return this.isOutputOpen;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implCloseSelectableChannel()
/*     */     throws IOException
/*     */   {
/* 803 */     synchronized (this.stateLock) {
/* 804 */       this.isInputOpen = false;
/* 805 */       this.isOutputOpen = false;
/*     */ 
/* 812 */       if (this.state != 4) {
/* 813 */         nd.preClose(this.fd);
/*     */       }
/*     */ 
/* 819 */       if (this.readerThread != 0L) {
/* 820 */         NativeThread.signal(this.readerThread);
/*     */       }
/* 822 */       if (this.writerThread != 0L) {
/* 823 */         NativeThread.signal(this.writerThread);
/*     */       }
/*     */ 
/* 835 */       if (!isRegistered())
/* 836 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void kill() throws IOException {
/* 841 */     synchronized (this.stateLock) {
/* 842 */       if (this.state == 4)
/* 843 */         return;
/* 844 */       if (this.state == -1) {
/* 845 */         this.state = 4;
/* 846 */         return;
/*     */       }
/* 848 */       assert ((!isOpen()) && (!isRegistered()));
/*     */ 
/* 853 */       if ((this.readerThread == 0L) && (this.writerThread == 0L)) {
/* 854 */         nd.close(this.fd);
/* 855 */         this.state = 4;
/*     */       } else {
/* 857 */         this.state = 3;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 867 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 868 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 869 */     int k = paramInt2;
/*     */ 
/* 871 */     if ((paramInt1 & 0x20) != 0)
/*     */     {
/* 875 */       return false;
/*     */     }
/*     */ 
/* 878 */     if ((paramInt1 & 0x18) != 0)
/*     */     {
/* 880 */       k = i;
/* 881 */       paramSelectionKeyImpl.nioReadyOps(k);
/*     */ 
/* 884 */       this.readyToConnect = true;
/* 885 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */ 
/* 888 */     if (((paramInt1 & 0x1) != 0) && ((i & 0x1) != 0) && (this.state == 2))
/*     */     {
/* 891 */       k |= 1;
/*     */     }
/* 893 */     if (((paramInt1 & 0x4) != 0) && ((i & 0x8) != 0) && ((this.state == 0) || (this.state == 1)))
/*     */     {
/* 896 */       k |= 8;
/* 897 */       this.readyToConnect = true;
/*     */     }
/*     */ 
/* 900 */     if (((paramInt1 & 0x4) != 0) && ((i & 0x4) != 0) && (this.state == 2))
/*     */     {
/* 903 */       k |= 4;
/*     */     }
/* 905 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 906 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */ 
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 910 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 914 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 921 */     int i = 0;
/* 922 */     if ((paramInt & 0x1) != 0)
/* 923 */       i |= 1;
/* 924 */     if ((paramInt & 0x4) != 0)
/* 925 */       i |= 4;
/* 926 */     if ((paramInt & 0x8) != 0)
/* 927 */       i |= 4;
/* 928 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*     */   }
/*     */ 
/*     */   public FileDescriptor getFD() {
/* 932 */     return this.fd;
/*     */   }
/*     */ 
/*     */   public int getFDVal() {
/* 936 */     return this.fdVal;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 940 */     StringBuffer localStringBuffer = new StringBuffer();
/* 941 */     localStringBuffer.append(getClass().getSuperclass().getName());
/* 942 */     localStringBuffer.append('[');
/* 943 */     if (!isOpen())
/* 944 */       localStringBuffer.append("closed");
/*     */     else {
/* 946 */       synchronized (this.stateLock) {
/* 947 */         switch (this.state) {
/*     */         case 0:
/* 949 */           localStringBuffer.append("unconnected");
/* 950 */           break;
/*     */         case 1:
/* 952 */           localStringBuffer.append("connection-pending");
/* 953 */           break;
/*     */         case 2:
/* 955 */           localStringBuffer.append("connected");
/* 956 */           if (!this.isInputOpen)
/* 957 */             localStringBuffer.append(" ishut");
/* 958 */           if (!this.isOutputOpen)
/* 959 */             localStringBuffer.append(" oshut");
/*     */           break;
/*     */         }
/* 962 */         if (localAddress() != null) {
/* 963 */           localStringBuffer.append(" local=");
/* 964 */           localStringBuffer.append(localAddress().toString());
/*     */         }
/* 966 */         if (remoteAddress() != null) {
/* 967 */           localStringBuffer.append(" remote=");
/* 968 */           localStringBuffer.append(remoteAddress().toString());
/*     */         }
/*     */       }
/*     */     }
/* 972 */     localStringBuffer.append(']');
/* 973 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private static native int checkConnect(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2)
/*     */     throws IOException;
/*     */ 
/*     */   private static native int sendOutOfBandData(FileDescriptor paramFileDescriptor, byte paramByte)
/*     */     throws IOException;
/*     */ 
/*     */   static
/*     */   {
/* 987 */     Util.load();
/*     */   }
/*     */ 
/*     */   private static class DefaultOptionsHolder
/*     */   {
/* 211 */     static final Set<SocketOption<?>> defaultOptions = defaultOptions();
/*     */ 
/*     */     private static Set<SocketOption<?>> defaultOptions() {
/* 214 */       HashSet localHashSet = new HashSet(8);
/* 215 */       localHashSet.add(StandardSocketOptions.SO_SNDBUF);
/* 216 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/* 217 */       localHashSet.add(StandardSocketOptions.SO_KEEPALIVE);
/* 218 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/* 219 */       localHashSet.add(StandardSocketOptions.SO_LINGER);
/* 220 */       localHashSet.add(StandardSocketOptions.TCP_NODELAY);
/*     */ 
/* 222 */       localHashSet.add(StandardSocketOptions.IP_TOS);
/* 223 */       localHashSet.add(ExtendedSocketOption.SO_OOBINLINE);
/* 224 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SocketChannelImpl
 * JD-Core Version:    0.6.2
 */