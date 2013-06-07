/*      */ package sun.nio.ch;
/*      */ 
/*      */ import com.sun.nio.sctp.AbstractNotificationHandler;
/*      */ import com.sun.nio.sctp.Association;
/*      */ import com.sun.nio.sctp.AssociationChangeNotification;
/*      */ import com.sun.nio.sctp.AssociationChangeNotification.AssocChangeEvent;
/*      */ import com.sun.nio.sctp.HandlerResult;
/*      */ import com.sun.nio.sctp.IllegalReceiveException;
/*      */ import com.sun.nio.sctp.IllegalUnbindException;
/*      */ import com.sun.nio.sctp.InvalidStreamException;
/*      */ import com.sun.nio.sctp.MessageInfo;
/*      */ import com.sun.nio.sctp.NotificationHandler;
/*      */ import com.sun.nio.sctp.SctpChannel;
/*      */ import com.sun.nio.sctp.SctpSocketOption;
/*      */ import com.sun.nio.sctp.SctpStandardSocketOptions;
/*      */ import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.AlreadyConnectedException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.ConnectionPendingException;
/*      */ import java.nio.channels.NoConnectionPendingException;
/*      */ import java.nio.channels.NotYetBoundException;
/*      */ import java.nio.channels.NotYetConnectedException;
/*      */ import java.nio.channels.spi.SelectorProvider;
/*      */ import java.security.AccessController;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ import sun.security.action.LoadLibraryAction;
/*      */ 
/*      */ public class SctpChannelImpl extends SctpChannel
/*      */   implements SelChImpl
/*      */ {
/*      */   private final FileDescriptor fd;
/*      */   private final int fdVal;
/*   75 */   private volatile long receiverThread = 0L;
/*   76 */   private volatile long senderThread = 0L;
/*      */ 
/*   79 */   private final Object receiveLock = new Object();
/*      */ 
/*   82 */   private final Object sendLock = new Object();
/*      */ 
/*   84 */   private final ThreadLocal<Boolean> receiveInvoked = new ThreadLocal()
/*      */   {
/*      */     protected Boolean initialValue() {
/*   87 */       return Boolean.FALSE;
/*      */     }
/*   84 */   };
/*      */ 
/*   93 */   private final Object stateLock = new Object();
/*      */ 
/*  104 */   private ChannelState state = ChannelState.UNINITIALIZED;
/*      */ 
/*  107 */   int port = -1;
/*  108 */   private HashSet<InetSocketAddress> localAddresses = new HashSet();
/*      */   private boolean wildcard;
/*      */   private boolean readyToConnect;
/*      */   private boolean isShutdown;
/*      */   private Association association;
/*  121 */   private Set<SocketAddress> remoteAddresses = Collections.EMPTY_SET;
/*      */ 
/*  856 */   private InternalNotificationHandler<?> internalNotificationHandler = new InternalNotificationHandler(null);
/*      */ 
/*      */   public SctpChannelImpl(SelectorProvider paramSelectorProvider)
/*      */     throws IOException
/*      */   {
/*  130 */     super(paramSelectorProvider);
/*  131 */     this.fd = SctpNet.socket(true);
/*  132 */     this.fdVal = IOUtil.fdVal(this.fd);
/*  133 */     this.state = ChannelState.UNCONNECTED;
/*      */   }
/*      */ 
/*      */   public SctpChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor)
/*      */     throws IOException
/*      */   {
/*  141 */     this(paramSelectorProvider, paramFileDescriptor, null);
/*      */   }
/*      */ 
/*      */   public SctpChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, Association paramAssociation)
/*      */     throws IOException
/*      */   {
/*  151 */     super(paramSelectorProvider);
/*  152 */     this.fd = paramFileDescriptor;
/*  153 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/*  154 */     this.state = ChannelState.CONNECTED;
/*  155 */     this.port = Net.localAddress(paramFileDescriptor).getPort();
/*      */ 
/*  157 */     if (paramAssociation != null) {
/*  158 */       this.association = paramAssociation;
/*      */     }
/*      */     else {
/*  161 */       ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(50);
/*      */       try {
/*  163 */         receive(localByteBuffer, null, null, true);
/*      */       } finally {
/*  165 */         Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public SctpChannel bind(SocketAddress paramSocketAddress)
/*      */     throws IOException
/*      */   {
/*  175 */     synchronized (this.receiveLock) {
/*  176 */       synchronized (this.sendLock) {
/*  177 */         synchronized (this.stateLock) {
/*  178 */           ensureOpenAndUnconnected();
/*  179 */           if (isBound())
/*  180 */             SctpNet.throwAlreadyBoundException();
/*  181 */           InetSocketAddress localInetSocketAddress1 = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*      */ 
/*  183 */           Net.bind(this.fd, localInetSocketAddress1.getAddress(), localInetSocketAddress1.getPort());
/*  184 */           InetSocketAddress localInetSocketAddress2 = Net.localAddress(this.fd);
/*  185 */           this.port = localInetSocketAddress2.getPort();
/*  186 */           this.localAddresses.add(localInetSocketAddress1);
/*  187 */           if (localInetSocketAddress1.getAddress().isAnyLocalAddress())
/*  188 */             this.wildcard = true;
/*      */         }
/*      */       }
/*      */     }
/*  192 */     return this;
/*      */   }
/*      */ 
/*      */   public SctpChannel bindAddress(InetAddress paramInetAddress)
/*      */     throws IOException
/*      */   {
/*  198 */     bindUnbindAddress(paramInetAddress, true);
/*  199 */     this.localAddresses.add(new InetSocketAddress(paramInetAddress, this.port));
/*  200 */     return this;
/*      */   }
/*      */ 
/*      */   public SctpChannel unbindAddress(InetAddress paramInetAddress)
/*      */     throws IOException
/*      */   {
/*  206 */     bindUnbindAddress(paramInetAddress, false);
/*  207 */     this.localAddresses.remove(new InetSocketAddress(paramInetAddress, this.port));
/*  208 */     return this;
/*      */   }
/*      */ 
/*      */   private SctpChannel bindUnbindAddress(InetAddress paramInetAddress, boolean paramBoolean) throws IOException
/*      */   {
/*  213 */     if (paramInetAddress == null) {
/*  214 */       throw new IllegalArgumentException();
/*      */     }
/*  216 */     synchronized (this.receiveLock) {
/*  217 */       synchronized (this.sendLock)
/*      */       {
/*      */         Object localObject1;
/*      */         Iterator localIterator2;
/*  218 */         synchronized (this.stateLock) {
/*  219 */           if (!isOpen())
/*  220 */             throw new ClosedChannelException();
/*  221 */           if (!isBound())
/*  222 */             throw new NotYetBoundException();
/*  223 */           if (this.wildcard) {
/*  224 */             throw new IllegalStateException("Cannot add or remove addresses from a channel that is bound to the wildcard address");
/*      */           }
/*  226 */           if (paramInetAddress.isAnyLocalAddress())
/*  227 */             throw new IllegalArgumentException("Cannot add or remove the wildcard address");
/*      */           Iterator localIterator1;
/*  229 */           if (paramBoolean) {
/*  230 */             for (localIterator1 = this.localAddresses.iterator(); localIterator1.hasNext(); ) { localObject1 = (InetSocketAddress)localIterator1.next();
/*  231 */               if (((InetSocketAddress)localObject1).getAddress().equals(paramInetAddress)) {
/*  232 */                 SctpNet.throwAlreadyBoundException();
/*      */               }
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  238 */             if (this.localAddresses.size() <= 1)
/*  239 */               throw new IllegalUnbindException("Cannot remove address from a channel with only one address bound");
/*  240 */             int i = 0;
/*  241 */             for (localObject1 = this.localAddresses.iterator(); ((Iterator)localObject1).hasNext(); ) { InetSocketAddress localInetSocketAddress = (InetSocketAddress)((Iterator)localObject1).next();
/*  242 */               if (localInetSocketAddress.getAddress().equals(paramInetAddress)) {
/*  243 */                 i = 1;
/*  244 */                 break;
/*      */               }
/*      */             }
/*  247 */             if (i == 0) {
/*  248 */               throw new IllegalUnbindException("Cannot remove address from a channel that is not bound to that address");
/*      */             }
/*      */           }
/*  251 */           SctpNet.bindx(this.fdVal, new InetAddress[] { paramInetAddress }, this.port, paramBoolean);
/*      */ 
/*  254 */           if (paramBoolean)
/*  255 */             this.localAddresses.add(new InetSocketAddress(paramInetAddress, this.port));
/*      */           else {
/*  257 */             for (localIterator2 = this.localAddresses.iterator(); localIterator2.hasNext(); ) { localObject1 = (InetSocketAddress)localIterator2.next();
/*  258 */               if (((InetSocketAddress)localObject1).getAddress().equals(paramInetAddress)) {
/*  259 */                 this.localAddresses.remove(localObject1);
/*  260 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  267 */     return this;
/*      */   }
/*      */ 
/*      */   private boolean isBound() {
/*  271 */     synchronized (this.stateLock) {
/*  272 */       return this.port != -1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isConnected() {
/*  277 */     synchronized (this.stateLock) {
/*  278 */       return this.state == ChannelState.CONNECTED;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void ensureOpenAndUnconnected() throws IOException {
/*  283 */     synchronized (this.stateLock) {
/*  284 */       if (!isOpen())
/*  285 */         throw new ClosedChannelException();
/*  286 */       if (isConnected())
/*  287 */         throw new AlreadyConnectedException();
/*  288 */       if (this.state == ChannelState.PENDING)
/*  289 */         throw new ConnectionPendingException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean ensureReceiveOpen() throws ClosedChannelException {
/*  294 */     synchronized (this.stateLock) {
/*  295 */       if (!isOpen())
/*  296 */         throw new ClosedChannelException();
/*  297 */       if (!isConnected()) {
/*  298 */         throw new NotYetConnectedException();
/*      */       }
/*  300 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void ensureSendOpen() throws ClosedChannelException {
/*  305 */     synchronized (this.stateLock) {
/*  306 */       if (!isOpen())
/*  307 */         throw new ClosedChannelException();
/*  308 */       if (this.isShutdown)
/*  309 */         throw new ClosedChannelException();
/*  310 */       if (!isConnected())
/*  311 */         throw new NotYetConnectedException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void receiverCleanup() throws IOException {
/*  316 */     synchronized (this.stateLock) {
/*  317 */       this.receiverThread = 0L;
/*  318 */       if (this.state == ChannelState.KILLPENDING)
/*  319 */         kill();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void senderCleanup() throws IOException {
/*  324 */     synchronized (this.stateLock) {
/*  325 */       this.senderThread = 0L;
/*  326 */       if (this.state == ChannelState.KILLPENDING)
/*  327 */         kill();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Association association() throws ClosedChannelException
/*      */   {
/*  333 */     synchronized (this.stateLock) {
/*  334 */       if (!isOpen())
/*  335 */         throw new ClosedChannelException();
/*  336 */       if (!isConnected()) {
/*  337 */         return null;
/*      */       }
/*  339 */       return this.association;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean connect(SocketAddress paramSocketAddress) throws IOException
/*      */   {
/*  345 */     synchronized (this.receiveLock) {
/*  346 */       synchronized (this.sendLock) {
/*  347 */         ensureOpenAndUnconnected();
/*  348 */         InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*  349 */         SecurityManager localSecurityManager = System.getSecurityManager();
/*  350 */         if (localSecurityManager != null) {
/*  351 */           localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
/*      */         }
/*  353 */         synchronized (blockingLock()) {
/*  354 */           int i = 0;
/*      */           try {
/*      */             try {
/*  357 */               begin();
/*  358 */               synchronized (this.stateLock) {
/*  359 */                 if (!isOpen()) {
/*  360 */                   boolean bool = false;
/*      */ 
/*  375 */                   receiverCleanup();
/*  376 */                   end((i > 0) || (i == -2));
/*  377 */                   assert (IOStatus.check(i)); return bool;
/*      */                 }
/*  362 */                 this.receiverThread = NativeThread.current();
/*      */               }
/*      */               while (true) {
/*  365 */                 ??? = localInetSocketAddress.getAddress();
/*  366 */                 if (???.isAnyLocalAddress())
/*  367 */                   ??? = InetAddress.getLocalHost();
/*  368 */                 i = SctpNet.connect(this.fdVal, ???, localInetSocketAddress.getPort());
/*  369 */                 if ((i != -3) || (!isOpen()))
/*      */                   break;
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/*  375 */               receiverCleanup();
/*  376 */               end((i > 0) || (i == -2));
/*  377 */               if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */             }
/*      */ 
/*      */           }
/*      */           catch (IOException )
/*      */           {
/*  383 */             close();
/*  384 */             throw ???;
/*      */           }
/*      */ 
/*  387 */           if (i > 0) {
/*  388 */             synchronized (this.stateLock)
/*      */             {
/*  390 */               this.state = ChannelState.CONNECTED;
/*  391 */               if (!isBound()) {
/*  392 */                 localObject1 = Net.localAddress(this.fd);
/*      */ 
/*  394 */                 this.port = ((InetSocketAddress)localObject1).getPort();
/*      */               }
/*      */ 
/*  398 */               Object localObject1 = Util.getTemporaryDirectBuffer(50);
/*      */               try {
/*  400 */                 receive((ByteBuffer)localObject1, null, null, true);
/*      */               } finally {
/*  402 */                 Util.releaseTemporaryDirectBuffer((ByteBuffer)localObject1);
/*      */               }
/*      */ 
/*      */               try
/*      */               {
/*  407 */                 this.remoteAddresses = getRemoteAddresses();
/*      */               } catch (IOException localIOException) {
/*      */               }
/*  410 */               return true;
/*      */             }
/*      */           }
/*  413 */           synchronized (this.stateLock)
/*      */           {
/*  416 */             if (!isBlocking()) {
/*  417 */               this.state = ChannelState.PENDING;
/*      */             }
/*  419 */             else if (!$assertionsDisabled) throw new AssertionError();
/*      */           }
/*      */         }
/*      */ 
/*  423 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean connect(SocketAddress paramSocketAddress, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  433 */     ensureOpenAndUnconnected();
/*  434 */     return setOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS, SctpStandardSocketOptions.InitMaxStreams.create(paramInt2, paramInt1)).connect(paramSocketAddress);
/*      */   }
/*      */ 
/*      */   public boolean isConnectionPending()
/*      */   {
/*  441 */     synchronized (this.stateLock) {
/*  442 */       return this.state == ChannelState.PENDING;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean finishConnect() throws IOException
/*      */   {
/*  448 */     synchronized (this.receiveLock) {
/*  449 */       synchronized (this.sendLock) {
/*  450 */         synchronized (this.stateLock) {
/*  451 */           if (!isOpen())
/*  452 */             throw new ClosedChannelException();
/*  453 */           if (isConnected())
/*  454 */             return true;
/*  455 */           if (this.state != ChannelState.PENDING)
/*  456 */             throw new NoConnectionPendingException();
/*      */         }
/*  458 */         int i = 0;
/*      */         try {
/*      */           try {
/*  461 */             begin();
/*  462 */             synchronized (blockingLock()) {
/*  463 */               synchronized (this.stateLock) {
/*  464 */                 if (!isOpen()) {
/*  465 */                   boolean bool = false;
/*      */ 
/*  493 */                   synchronized (this.stateLock) {
/*  494 */                     this.receiverThread = 0L;
/*  495 */                     if (this.state == ChannelState.KILLPENDING) {
/*  496 */                       kill();
/*      */ 
/*  502 */                       i = 0;
/*      */                     }
/*      */                   }
/*  505 */                   end((i > 0) || (i == -2));
/*  506 */                   assert (IOStatus.check(i)); return bool;
/*      */                 }
/*  467 */                 this.receiverThread = NativeThread.current();
/*      */               }
/*  469 */               if (!isBlocking()) {
/*      */                 while (true) {
/*  471 */                   i = checkConnect(this.fd, false, this.readyToConnect);
/*  472 */                   if ((i != -3) || (!isOpen())) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               while (true)
/*      */               {
/*  479 */                 i = checkConnect(this.fd, true, this.readyToConnect);
/*  480 */                 if (i != 0)
/*      */                 {
/*  485 */                   if ((i != -3) || (!isOpen()))
/*      */                     break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/*  493 */             synchronized (this.stateLock) {
/*  494 */               this.receiverThread = 0L;
/*  495 */               if (this.state == ChannelState.KILLPENDING) {
/*  496 */                 kill();
/*      */ 
/*  502 */                 i = 0;
/*      */               }
/*      */             }
/*  505 */             end((i > 0) || (i == -2));
/*  506 */             if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IOException localObject1)
/*      */         {
/*  512 */           close();
/*  513 */           throw ???;
/*      */         }
/*      */ 
/*  516 */         if (i > 0) {
/*  517 */           synchronized (this.stateLock) {
/*  518 */             this.state = ChannelState.CONNECTED;
/*  519 */             if (!isBound()) {
/*  520 */               ??? = Net.localAddress(this.fd);
/*      */ 
/*  522 */               this.port = ((InetSocketAddress)???).getPort();
/*      */             }
/*      */ 
/*  526 */             ??? = Util.getTemporaryDirectBuffer(50);
/*      */             try {
/*  528 */               receive((ByteBuffer)???, null, null, true);
/*      */             } finally {
/*  530 */               Util.releaseTemporaryDirectBuffer((ByteBuffer)???);
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/*  535 */               this.remoteAddresses = getRemoteAddresses();
/*      */             } catch (IOException localIOException) {
/*      */             }
/*  538 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  543 */     return false;
/*      */   }
/*      */ 
/*      */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException
/*      */   {
/*  548 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*      */   }
/*      */ 
/*      */   public void implCloseSelectableChannel() throws IOException
/*      */   {
/*  553 */     synchronized (this.stateLock) {
/*  554 */       SctpNet.preClose(this.fdVal);
/*      */ 
/*  556 */       if (this.receiverThread != 0L) {
/*  557 */         NativeThread.signal(this.receiverThread);
/*      */       }
/*  559 */       if (this.senderThread != 0L) {
/*  560 */         NativeThread.signal(this.senderThread);
/*      */       }
/*  562 */       if (!isRegistered())
/*  563 */         kill();
/*      */     }
/*      */   }
/*      */ 
/*      */   public FileDescriptor getFD()
/*      */   {
/*  569 */     return this.fd;
/*      */   }
/*      */ 
/*      */   public int getFDVal()
/*      */   {
/*  574 */     return this.fdVal;
/*      */   }
/*      */ 
/*      */   private boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/*  581 */     int i = paramSelectionKeyImpl.nioInterestOps();
/*  582 */     int j = paramSelectionKeyImpl.nioReadyOps();
/*  583 */     int k = paramInt2;
/*      */ 
/*  585 */     if ((paramInt1 & 0x20) != 0)
/*      */     {
/*  589 */       return false;
/*      */     }
/*      */ 
/*  592 */     if ((paramInt1 & 0x18) != 0)
/*      */     {
/*  594 */       k = i;
/*  595 */       paramSelectionKeyImpl.nioReadyOps(k);
/*      */ 
/*  598 */       this.readyToConnect = true;
/*  599 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */     }
/*      */ 
/*  602 */     if (((paramInt1 & 0x1) != 0) && ((i & 0x1) != 0) && (isConnected()))
/*      */     {
/*  605 */       k |= 1;
/*      */     }
/*  607 */     if (((paramInt1 & 0x4) != 0) && ((i & 0x8) != 0) && ((this.state == ChannelState.UNCONNECTED) || (this.state == ChannelState.PENDING)))
/*      */     {
/*  610 */       k |= 8;
/*  611 */       this.readyToConnect = true;
/*      */     }
/*      */ 
/*  614 */     if (((paramInt1 & 0x4) != 0) && ((i & 0x4) != 0) && (isConnected()))
/*      */     {
/*  617 */       k |= 4;
/*      */     }
/*  619 */     paramSelectionKeyImpl.nioReadyOps(k);
/*  620 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */   }
/*      */ 
/*      */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/*  625 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*      */   }
/*      */ 
/*      */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/*  631 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*      */   }
/*      */ 
/*      */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/*  636 */     int i = 0;
/*  637 */     if ((paramInt & 0x1) != 0)
/*  638 */       i |= 1;
/*  639 */     if ((paramInt & 0x4) != 0)
/*  640 */       i |= 4;
/*  641 */     if ((paramInt & 0x8) != 0)
/*  642 */       i |= 4;
/*  643 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*      */   }
/*      */ 
/*      */   public void kill() throws IOException
/*      */   {
/*  648 */     synchronized (this.stateLock) {
/*  649 */       if (this.state == ChannelState.KILLED)
/*  650 */         return;
/*  651 */       if (this.state == ChannelState.UNINITIALIZED) {
/*  652 */         this.state = ChannelState.KILLED;
/*  653 */         return;
/*      */       }
/*  655 */       assert ((!isOpen()) && (!isRegistered()));
/*      */ 
/*  659 */       if ((this.receiverThread == 0L) && (this.senderThread == 0L)) {
/*  660 */         SctpNet.close(this.fdVal);
/*  661 */         this.state = ChannelState.KILLED;
/*      */       } else {
/*  663 */         this.state = ChannelState.KILLPENDING;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public <T> SctpChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT)
/*      */     throws IOException
/*      */   {
/*  671 */     if (paramSctpSocketOption == null)
/*  672 */       throw new NullPointerException();
/*  673 */     if (!supportedOptions().contains(paramSctpSocketOption)) {
/*  674 */       throw new UnsupportedOperationException("'" + paramSctpSocketOption + "' not supported");
/*      */     }
/*  676 */     synchronized (this.stateLock) {
/*  677 */       if (!isOpen()) {
/*  678 */         throw new ClosedChannelException();
/*      */       }
/*  680 */       SctpNet.setSocketOption(this.fdVal, paramSctpSocketOption, paramT, 0);
/*      */     }
/*  682 */     return this;
/*      */   }
/*      */ 
/*      */   public <T> T getOption(SctpSocketOption<T> paramSctpSocketOption)
/*      */     throws IOException
/*      */   {
/*  688 */     if (paramSctpSocketOption == null)
/*  689 */       throw new NullPointerException();
/*  690 */     if (!supportedOptions().contains(paramSctpSocketOption)) {
/*  691 */       throw new UnsupportedOperationException("'" + paramSctpSocketOption + "' not supported");
/*      */     }
/*  693 */     synchronized (this.stateLock) {
/*  694 */       if (!isOpen()) {
/*  695 */         throw new ClosedChannelException();
/*      */       }
/*  697 */       return SctpNet.getSocketOption(this.fdVal, paramSctpSocketOption, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final Set<SctpSocketOption<?>> supportedOptions()
/*      */   {
/*  722 */     return DefaultOptionsHolder.defaultOptions;
/*      */   }
/*      */ 
/*      */   public <T> MessageInfo receive(ByteBuffer paramByteBuffer, T paramT, NotificationHandler<T> paramNotificationHandler)
/*      */     throws IOException
/*      */   {
/*  730 */     return receive(paramByteBuffer, paramT, paramNotificationHandler, false);
/*      */   }
/*      */ 
/*      */   private <T> MessageInfo receive(ByteBuffer paramByteBuffer, T paramT, NotificationHandler<T> paramNotificationHandler, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  738 */     if (paramByteBuffer == null) {
/*  739 */       throw new IllegalArgumentException("buffer cannot be null");
/*      */     }
/*  741 */     if (paramByteBuffer.isReadOnly()) {
/*  742 */       throw new IllegalArgumentException("Read-only buffer");
/*      */     }
/*  744 */     if (((Boolean)this.receiveInvoked.get()).booleanValue()) {
/*  745 */       throw new IllegalReceiveException("cannot invoke receive from handler");
/*      */     }
/*  747 */     this.receiveInvoked.set(Boolean.TRUE);
/*      */     try
/*      */     {
/*  750 */       SctpResultContainer localSctpResultContainer = new SctpResultContainer();
/*      */       do {
/*  752 */         localSctpResultContainer.clear();
/*  753 */         synchronized (this.receiveLock) {
/*  754 */           if (!ensureReceiveOpen()) {
/*  755 */             return null;
/*      */           }
/*  757 */           int i = 0;
/*      */           try {
/*  759 */             begin();
/*      */ 
/*  761 */             synchronized (this.stateLock) {
/*  762 */               if (!isOpen()) {
/*  763 */                 MessageInfo localMessageInfo2 = null;
/*      */ 
/*  771 */                 receiverCleanup();
/*  772 */                 end((i > 0) || (i == -2));
/*  773 */                 assert (IOStatus.check(i));
/*      */ 
/*  811 */                 return localMessageInfo2;
/*      */               }
/*  764 */               this.receiverThread = NativeThread.current();
/*      */             }
/*      */             do
/*      */             {
/*  768 */               i = receive(this.fdVal, paramByteBuffer, localSctpResultContainer, paramBoolean);
/*  769 */               if (i != -3) break;  } while (isOpen());
/*      */           } finally {
/*  771 */             receiverCleanup();
/*  772 */             end((i > 0) || (i == -2));
/*  773 */             if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */           }
/*      */ 
/*  776 */           if (!localSctpResultContainer.isNotification())
/*      */           {
/*  778 */             if (localSctpResultContainer.hasSomething())
/*      */             {
/*  780 */               ??? = localSctpResultContainer.getMessageInfo();
/*      */ 
/*  782 */               synchronized (this.stateLock) {
/*  783 */                 assert (this.association != null);
/*  784 */                 ???.setAssociation(this.association);
/*      */               }
/*  786 */               return ???;
/*      */             }
/*      */ 
/*  789 */             return null;
/*      */           }
/*  791 */           synchronized (this.stateLock) {
/*  792 */             handleNotificationInternal(localSctpResultContainer);
/*      */           }
/*      */ 
/*  797 */           if (paramBoolean)
/*      */           {
/*  802 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*  805 */       while ((paramNotificationHandler == null) || (invokeNotificationHandler(localSctpResultContainer, paramNotificationHandler, paramT) == HandlerResult.CONTINUE));
/*      */ 
/*  809 */       return null;
/*      */     } finally {
/*  811 */       this.receiveInvoked.set(Boolean.FALSE);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int receive(int paramInt, ByteBuffer paramByteBuffer, SctpResultContainer paramSctpResultContainer, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  820 */     int i = paramByteBuffer.position();
/*  821 */     int j = paramByteBuffer.limit();
/*  822 */     assert (i <= j);
/*  823 */     int k = i <= j ? j - i : 0;
/*  824 */     if (((paramByteBuffer instanceof DirectBuffer)) && (k > 0)) {
/*  825 */       return receiveIntoNativeBuffer(paramInt, paramSctpResultContainer, paramByteBuffer, k, i, paramBoolean);
/*      */     }
/*      */ 
/*  828 */     int m = Math.max(k, 1);
/*  829 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(m);
/*      */     try {
/*  831 */       int n = receiveIntoNativeBuffer(paramInt, paramSctpResultContainer, localByteBuffer, m, 0, paramBoolean);
/*  832 */       localByteBuffer.flip();
/*  833 */       if ((n > 0) && (k > 0))
/*  834 */         paramByteBuffer.put(localByteBuffer);
/*  835 */       return n;
/*      */     } finally {
/*  837 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int receiveIntoNativeBuffer(int paramInt1, SctpResultContainer paramSctpResultContainer, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  849 */     int i = receive0(paramInt1, paramSctpResultContainer, ((DirectBuffer)paramByteBuffer).address() + paramInt3, paramInt2, paramBoolean);
/*      */ 
/*  851 */     if (i > 0)
/*  852 */       paramByteBuffer.position(paramInt3 + i);
/*  853 */     return i;
/*      */   }
/*      */ 
/*      */   private void handleNotificationInternal(SctpResultContainer paramSctpResultContainer)
/*      */   {
/*  861 */     invokeNotificationHandler(paramSctpResultContainer, this.internalNotificationHandler, null);
/*      */   }
/*      */ 
/*      */   private <T> HandlerResult invokeNotificationHandler(SctpResultContainer paramSctpResultContainer, NotificationHandler<T> paramNotificationHandler, T paramT)
/*      */   {
/*  886 */     SctpNotification localSctpNotification = paramSctpResultContainer.notification();
/*  887 */     synchronized (this.stateLock) {
/*  888 */       localSctpNotification.setAssociation(this.association);
/*      */     }
/*      */ 
/*  891 */     if (!(paramNotificationHandler instanceof AbstractNotificationHandler)) {
/*  892 */       return paramNotificationHandler.handleNotification(localSctpNotification, paramT);
/*      */     }
/*      */ 
/*  896 */     ??? = (AbstractNotificationHandler)paramNotificationHandler;
/*      */ 
/*  898 */     switch (paramSctpResultContainer.type()) {
/*      */     case 3:
/*  900 */       return ((AbstractNotificationHandler)???).handleNotification(paramSctpResultContainer.getAssociationChanged(), paramT);
/*      */     case 4:
/*  903 */       return ((AbstractNotificationHandler)???).handleNotification(paramSctpResultContainer.getPeerAddressChanged(), paramT);
/*      */     case 2:
/*  906 */       return ((AbstractNotificationHandler)???).handleNotification(paramSctpResultContainer.getSendFailed(), paramT);
/*      */     case 5:
/*  909 */       return ((AbstractNotificationHandler)???).handleNotification(paramSctpResultContainer.getShutdown(), paramT);
/*      */     }
/*      */ 
/*  913 */     return ((AbstractNotificationHandler)???).handleNotification(paramSctpResultContainer.notification(), paramT);
/*      */   }
/*      */ 
/*      */   private void checkAssociation(Association paramAssociation)
/*      */   {
/*  919 */     synchronized (this.stateLock) {
/*  920 */       if ((paramAssociation != null) && (!paramAssociation.equals(this.association)))
/*  921 */         throw new IllegalArgumentException("Cannot send to another association");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkStreamNumber(int paramInt)
/*      */   {
/*  928 */     synchronized (this.stateLock) {
/*  929 */       if ((this.association != null) && (
/*  930 */         (paramInt < 0) || (paramInt >= this.association.maxOutboundStreams())))
/*      */       {
/*  932 */         throw new InvalidStreamException();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int send(ByteBuffer paramByteBuffer, MessageInfo paramMessageInfo)
/*      */     throws IOException
/*      */   {
/*  944 */     if (paramByteBuffer == null) {
/*  945 */       throw new IllegalArgumentException("buffer cannot be null");
/*      */     }
/*  947 */     if (paramMessageInfo == null) {
/*  948 */       throw new IllegalArgumentException("messageInfo cannot be null");
/*      */     }
/*  950 */     checkAssociation(paramMessageInfo.association());
/*  951 */     checkStreamNumber(paramMessageInfo.streamNumber());
/*      */ 
/*  953 */     synchronized (this.sendLock) {
/*  954 */       ensureSendOpen();
/*      */ 
/*  956 */       int i = 0;
/*      */       try {
/*  958 */         begin();
/*      */ 
/*  960 */         synchronized (this.stateLock) {
/*  961 */           if (!isOpen()) {
/*  962 */             int k = 0;
/*      */ 
/*  972 */             senderCleanup();
/*  973 */             end((i > 0) || (i == -2));
/*  974 */             assert (IOStatus.check(i)); return k;
/*      */           }
/*  963 */           this.senderThread = NativeThread.current();
/*      */         }
/*      */         do
/*      */         {
/*  967 */           i = send(this.fdVal, paramByteBuffer, paramMessageInfo);
/*  968 */         }while ((i == -3) && (isOpen()));
/*      */ 
/*  970 */         int j = IOStatus.normalize(i);
/*      */ 
/*  972 */         senderCleanup();
/*  973 */         end((i > 0) || (i == -2));
/*  974 */         assert (IOStatus.check(i)); return j;
/*      */       }
/*      */       finally
/*      */       {
/*  972 */         senderCleanup();
/*  973 */         end((i > 0) || (i == -2));
/*  974 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int send(int paramInt, ByteBuffer paramByteBuffer, MessageInfo paramMessageInfo)
/*      */     throws IOException
/*      */   {
/*  981 */     int i = paramMessageInfo.streamNumber();
/*  982 */     SocketAddress localSocketAddress = paramMessageInfo.address();
/*  983 */     boolean bool = paramMessageInfo.isUnordered();
/*  984 */     int j = paramMessageInfo.payloadProtocolID();
/*      */ 
/*  986 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/*  987 */       return sendFromNativeBuffer(paramInt, paramByteBuffer, localSocketAddress, i, bool, j);
/*      */     }
/*      */ 
/*  991 */     int k = paramByteBuffer.position();
/*  992 */     int m = paramByteBuffer.limit();
/*  993 */     assert ((k <= m) && (i >= 0));
/*      */ 
/*  995 */     int n = k <= m ? m - k : 0;
/*  996 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(n);
/*      */     try {
/*  998 */       localByteBuffer.put(paramByteBuffer);
/*  999 */       localByteBuffer.flip();
/*      */ 
/* 1001 */       paramByteBuffer.position(k);
/*      */ 
/* 1003 */       int i1 = sendFromNativeBuffer(paramInt, localByteBuffer, localSocketAddress, i, bool, j);
/*      */ 
/* 1005 */       if (i1 > 0)
/*      */       {
/* 1007 */         paramByteBuffer.position(k + i1);
/*      */       }
/* 1009 */       return i1;
/*      */     } finally {
/* 1011 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int sendFromNativeBuffer(int paramInt1, ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress, int paramInt2, boolean paramBoolean, int paramInt3)
/*      */     throws IOException
/*      */   {
/* 1022 */     InetAddress localInetAddress = null;
/* 1023 */     int i = 0;
/* 1024 */     if (paramSocketAddress != null) {
/* 1025 */       InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/* 1026 */       localInetAddress = localInetSocketAddress.getAddress();
/* 1027 */       i = localInetSocketAddress.getPort();
/*      */     }
/*      */ 
/* 1030 */     int j = paramByteBuffer.position();
/* 1031 */     int k = paramByteBuffer.limit();
/* 1032 */     assert (j <= k);
/* 1033 */     int m = j <= k ? k - j : 0;
/*      */ 
/* 1035 */     int n = send0(paramInt1, ((DirectBuffer)paramByteBuffer).address() + j, m, localInetAddress, i, -1, paramInt2, paramBoolean, paramInt3);
/*      */ 
/* 1037 */     if (n > 0)
/* 1038 */       paramByteBuffer.position(j + n);
/* 1039 */     return n;
/*      */   }
/*      */ 
/*      */   public SctpChannel shutdown() throws IOException
/*      */   {
/* 1044 */     synchronized (this.stateLock) {
/* 1045 */       if (this.isShutdown) {
/* 1046 */         return this;
/*      */       }
/* 1048 */       ensureSendOpen();
/* 1049 */       SctpNet.shutdown(this.fdVal, -1);
/* 1050 */       if (this.senderThread != 0L)
/* 1051 */         NativeThread.signal(this.senderThread);
/* 1052 */       this.isShutdown = true;
/*      */     }
/* 1054 */     return this;
/*      */   }
/*      */ 
/*      */   public Set<SocketAddress> getAllLocalAddresses()
/*      */     throws IOException
/*      */   {
/* 1060 */     synchronized (this.stateLock) {
/* 1061 */       if (!isOpen())
/* 1062 */         throw new ClosedChannelException();
/* 1063 */       if (!isBound()) {
/* 1064 */         return Collections.EMPTY_SET;
/*      */       }
/* 1066 */       return SctpNet.getLocalAddresses(this.fdVal);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set<SocketAddress> getRemoteAddresses()
/*      */     throws IOException
/*      */   {
/* 1073 */     synchronized (this.stateLock) {
/* 1074 */       if (!isOpen())
/* 1075 */         throw new ClosedChannelException();
/* 1076 */       if ((!isConnected()) || (this.isShutdown))
/* 1077 */         return Collections.EMPTY_SET;
/*      */       try
/*      */       {
/* 1080 */         return SctpNet.getRemoteAddresses(this.fdVal, 0);
/*      */       }
/*      */       catch (SocketException localSocketException) {
/* 1083 */         return this.remoteAddresses;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static native void initIDs();
/*      */ 
/*      */   static native int receive0(int paramInt1, SctpResultContainer paramSctpResultContainer, long paramLong, int paramInt2, boolean paramBoolean)
/*      */     throws IOException;
/*      */ 
/*      */   static native int send0(int paramInt1, long paramLong, int paramInt2, InetAddress paramInetAddress, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6)
/*      */     throws IOException;
/*      */ 
/*      */   private static native int checkConnect(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws IOException;
/*      */ 
/*      */   static
/*      */   {
/* 1102 */     Util.load();
/* 1103 */     AccessController.doPrivileged(new LoadLibraryAction("sctp"));
/*      */ 
/* 1105 */     initIDs();
/*      */   }
/*      */ 
/*      */   private static enum ChannelState
/*      */   {
/*   96 */     UNINITIALIZED, 
/*   97 */     UNCONNECTED, 
/*   98 */     PENDING, 
/*   99 */     CONNECTED, 
/*  100 */     KILLPENDING, 
/*  101 */     KILLED;
/*      */   }
/*      */ 
/*      */   private static class DefaultOptionsHolder
/*      */   {
/*  702 */     static final Set<SctpSocketOption<?>> defaultOptions = defaultOptions();
/*      */ 
/*      */     private static Set<SctpSocketOption<?>> defaultOptions() {
/*  705 */       HashSet localHashSet = new HashSet(10);
/*  706 */       localHashSet.add(SctpStandardSocketOptions.SCTP_DISABLE_FRAGMENTS);
/*  707 */       localHashSet.add(SctpStandardSocketOptions.SCTP_EXPLICIT_COMPLETE);
/*  708 */       localHashSet.add(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE);
/*  709 */       localHashSet.add(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS);
/*  710 */       localHashSet.add(SctpStandardSocketOptions.SCTP_NODELAY);
/*  711 */       localHashSet.add(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR);
/*  712 */       localHashSet.add(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR);
/*  713 */       localHashSet.add(SctpStandardSocketOptions.SO_SNDBUF);
/*  714 */       localHashSet.add(SctpStandardSocketOptions.SO_RCVBUF);
/*  715 */       localHashSet.add(SctpStandardSocketOptions.SO_LINGER);
/*  716 */       return Collections.unmodifiableSet(localHashSet);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class InternalNotificationHandler<T> extends AbstractNotificationHandler<T>
/*      */   {
/*      */     private InternalNotificationHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public HandlerResult handleNotification(AssociationChangeNotification paramAssociationChangeNotification, T paramT)
/*      */     {
/*  871 */       if ((paramAssociationChangeNotification.event().equals(AssociationChangeNotification.AssocChangeEvent.COMM_UP)) && (SctpChannelImpl.this.association == null))
/*      */       {
/*  874 */         SctpAssocChange localSctpAssocChange = (SctpAssocChange)paramAssociationChangeNotification;
/*  875 */         SctpChannelImpl.this.association = new SctpAssociationImpl(localSctpAssocChange.assocId(), localSctpAssocChange.maxInStreams(), localSctpAssocChange.maxOutStreams());
/*      */       }
/*      */ 
/*  878 */       return HandlerResult.CONTINUE;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpChannelImpl
 * JD-Core Version:    0.6.2
 */