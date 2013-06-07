/*     */ package sun.nio.ch;
/*     */ 
/*     */ import com.sun.nio.sctp.AbstractNotificationHandler;
/*     */ import com.sun.nio.sctp.Association;
/*     */ import com.sun.nio.sctp.AssociationChangeNotification;
/*     */ import com.sun.nio.sctp.HandlerResult;
/*     */ import com.sun.nio.sctp.IllegalReceiveException;
/*     */ import com.sun.nio.sctp.IllegalUnbindException;
/*     */ import com.sun.nio.sctp.InvalidStreamException;
/*     */ import com.sun.nio.sctp.MessageInfo;
/*     */ import com.sun.nio.sctp.NotificationHandler;
/*     */ import com.sun.nio.sctp.SctpChannel;
/*     */ import com.sun.nio.sctp.SctpMultiChannel;
/*     */ import com.sun.nio.sctp.SctpSocketOption;
/*     */ import com.sun.nio.sctp.SctpStandardSocketOptions;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.security.AccessController;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import sun.security.action.LoadLibraryAction;
/*     */ 
/*     */ public class SctpMultiChannelImpl extends SctpMultiChannel
/*     */   implements SelChImpl
/*     */ {
/*     */   private final FileDescriptor fd;
/*     */   private final int fdVal;
/*  70 */   private volatile long receiverThread = 0L;
/*  71 */   private volatile long senderThread = 0L;
/*     */ 
/*  74 */   private final Object receiveLock = new Object();
/*     */ 
/*  77 */   private final Object sendLock = new Object();
/*     */ 
/*  81 */   private final Object stateLock = new Object();
/*     */ 
/*  90 */   private ChannelState state = ChannelState.UNINITIALIZED;
/*     */ 
/*  93 */   int port = -1;
/*  94 */   private HashSet<InetSocketAddress> localAddresses = new HashSet();
/*     */   private boolean wildcard;
/*  99 */   private HashMap<SocketAddress, Association> addressMap = new HashMap();
/*     */ 
/* 101 */   private HashMap<Association, Set<SocketAddress>> associationMap = new HashMap();
/*     */ 
/* 108 */   private final ThreadLocal<Association> associationToRemove = new ThreadLocal()
/*     */   {
/*     */     protected Association initialValue() {
/* 111 */       return null;
/*     */     }
/* 108 */   };
/*     */ 
/* 116 */   private final ThreadLocal<Boolean> receiveInvoked = new ThreadLocal()
/*     */   {
/*     */     protected Boolean initialValue() {
/* 119 */       return Boolean.FALSE;
/*     */     }
/* 116 */   };
/*     */ 
/* 580 */   private InternalNotificationHandler internalNotificationHandler = new InternalNotificationHandler(null);
/*     */ 
/*     */   public SctpMultiChannelImpl(SelectorProvider paramSelectorProvider)
/*     */     throws IOException
/*     */   {
/* 126 */     super(paramSelectorProvider);
/* 127 */     this.fd = SctpNet.socket(false);
/* 128 */     this.fdVal = IOUtil.fdVal(this.fd);
/*     */   }
/*     */ 
/*     */   public SctpMultiChannel bind(SocketAddress paramSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 134 */     synchronized (this.receiveLock) {
/* 135 */       synchronized (this.sendLock) {
/* 136 */         synchronized (this.stateLock) {
/* 137 */           ensureOpen();
/* 138 */           if (isBound())
/* 139 */             SctpNet.throwAlreadyBoundException();
/* 140 */           InetSocketAddress localInetSocketAddress1 = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*     */ 
/* 143 */           SecurityManager localSecurityManager = System.getSecurityManager();
/* 144 */           if (localSecurityManager != null)
/* 145 */             localSecurityManager.checkListen(localInetSocketAddress1.getPort());
/* 146 */           Net.bind(this.fd, localInetSocketAddress1.getAddress(), localInetSocketAddress1.getPort());
/*     */ 
/* 148 */           InetSocketAddress localInetSocketAddress2 = Net.localAddress(this.fd);
/* 149 */           this.port = localInetSocketAddress2.getPort();
/* 150 */           this.localAddresses.add(localInetSocketAddress1);
/* 151 */           if (localInetSocketAddress1.getAddress().isAnyLocalAddress()) {
/* 152 */             this.wildcard = true;
/*     */           }
/* 154 */           SctpNet.listen(this.fdVal, paramInt < 1 ? 50 : paramInt);
/*     */         }
/*     */       }
/*     */     }
/* 158 */     return this;
/*     */   }
/*     */ 
/*     */   public SctpMultiChannel bindAddress(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/* 164 */     return bindUnbindAddress(paramInetAddress, true);
/*     */   }
/*     */ 
/*     */   public SctpMultiChannel unbindAddress(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/* 170 */     return bindUnbindAddress(paramInetAddress, false);
/*     */   }
/*     */ 
/*     */   private SctpMultiChannel bindUnbindAddress(InetAddress paramInetAddress, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 176 */     if (paramInetAddress == null) {
/* 177 */       throw new IllegalArgumentException();
/*     */     }
/* 179 */     synchronized (this.receiveLock) {
/* 180 */       synchronized (this.sendLock)
/*     */       {
/*     */         Object localObject1;
/*     */         Iterator localIterator2;
/* 181 */         synchronized (this.stateLock) {
/* 182 */           if (!isOpen())
/* 183 */             throw new ClosedChannelException();
/* 184 */           if (!isBound())
/* 185 */             throw new NotYetBoundException();
/* 186 */           if (this.wildcard) {
/* 187 */             throw new IllegalStateException("Cannot add or remove addresses from a channel that is bound to the wildcard address");
/*     */           }
/* 189 */           if (paramInetAddress.isAnyLocalAddress())
/* 190 */             throw new IllegalArgumentException("Cannot add or remove the wildcard address");
/*     */           Iterator localIterator1;
/* 192 */           if (paramBoolean) {
/* 193 */             for (localIterator1 = this.localAddresses.iterator(); localIterator1.hasNext(); ) { localObject1 = (InetSocketAddress)localIterator1.next();
/* 194 */               if (((InetSocketAddress)localObject1).getAddress().equals(paramInetAddress)) {
/* 195 */                 SctpNet.throwAlreadyBoundException();
/*     */               }
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 201 */             if (this.localAddresses.size() <= 1)
/* 202 */               throw new IllegalUnbindException("Cannot remove address from a channel with only one address bound");
/* 203 */             int i = 0;
/* 204 */             for (localObject1 = this.localAddresses.iterator(); ((Iterator)localObject1).hasNext(); ) { InetSocketAddress localInetSocketAddress = (InetSocketAddress)((Iterator)localObject1).next();
/* 205 */               if (localInetSocketAddress.getAddress().equals(paramInetAddress)) {
/* 206 */                 i = 1;
/* 207 */                 break;
/*     */               }
/*     */             }
/* 210 */             if (i == 0) {
/* 211 */               throw new IllegalUnbindException("Cannot remove address from a channel that is not bound to that address");
/*     */             }
/*     */           }
/* 214 */           SctpNet.bindx(this.fdVal, new InetAddress[] { paramInetAddress }, this.port, paramBoolean);
/*     */ 
/* 217 */           if (paramBoolean)
/* 218 */             this.localAddresses.add(new InetSocketAddress(paramInetAddress, this.port));
/*     */           else {
/* 220 */             for (localIterator2 = this.localAddresses.iterator(); localIterator2.hasNext(); ) { localObject1 = (InetSocketAddress)localIterator2.next();
/* 221 */               if (((InetSocketAddress)localObject1).getAddress().equals(paramInetAddress)) {
/* 222 */                 this.localAddresses.remove(localObject1);
/* 223 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 230 */     return this;
/*     */   }
/*     */ 
/*     */   public Set<Association> associations()
/*     */     throws ClosedChannelException, NotYetBoundException
/*     */   {
/* 236 */     synchronized (this.stateLock) {
/* 237 */       if (!isOpen())
/* 238 */         throw new ClosedChannelException();
/* 239 */       if (!isBound()) {
/* 240 */         throw new NotYetBoundException();
/*     */       }
/* 242 */       return Collections.unmodifiableSet(this.associationMap.keySet());
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isBound() {
/* 247 */     synchronized (this.stateLock) {
/* 248 */       return this.port != -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void ensureOpen() throws IOException {
/* 253 */     synchronized (this.stateLock) {
/* 254 */       if (!isOpen())
/* 255 */         throw new ClosedChannelException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void receiverCleanup() throws IOException {
/* 260 */     synchronized (this.stateLock) {
/* 261 */       this.receiverThread = 0L;
/* 262 */       if (this.state == ChannelState.KILLPENDING)
/* 263 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void senderCleanup() throws IOException {
/* 268 */     synchronized (this.stateLock) {
/* 269 */       this.senderThread = 0L;
/* 270 */       if (this.state == ChannelState.KILLPENDING)
/* 271 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException
/*     */   {
/* 277 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void implCloseSelectableChannel() throws IOException
/*     */   {
/* 282 */     synchronized (this.stateLock) {
/* 283 */       SctpNet.preClose(this.fdVal);
/*     */ 
/* 285 */       if (this.receiverThread != 0L) {
/* 286 */         NativeThread.signal(this.receiverThread);
/*     */       }
/* 288 */       if (this.senderThread != 0L) {
/* 289 */         NativeThread.signal(this.senderThread);
/*     */       }
/* 291 */       if (!isRegistered())
/* 292 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public FileDescriptor getFD()
/*     */   {
/* 298 */     return this.fd;
/*     */   }
/*     */ 
/*     */   public int getFDVal()
/*     */   {
/* 303 */     return this.fdVal;
/*     */   }
/*     */ 
/*     */   private boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 311 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 312 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 313 */     int k = paramInt2;
/*     */ 
/* 315 */     if ((paramInt1 & 0x20) != 0)
/*     */     {
/* 319 */       return false;
/*     */     }
/*     */ 
/* 322 */     if ((paramInt1 & 0x18) != 0)
/*     */     {
/* 324 */       k = i;
/* 325 */       paramSelectionKeyImpl.nioReadyOps(k);
/* 326 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */ 
/* 329 */     if (((paramInt1 & 0x1) != 0) && ((i & 0x1) != 0))
/*     */     {
/* 331 */       k |= 1;
/*     */     }
/* 333 */     if (((paramInt1 & 0x4) != 0) && ((i & 0x4) != 0))
/*     */     {
/* 335 */       k |= 4;
/*     */     }
/* 337 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 338 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */ 
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 343 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 348 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 353 */     int i = 0;
/* 354 */     if ((paramInt & 0x1) != 0)
/* 355 */       i |= 1;
/* 356 */     if ((paramInt & 0x4) != 0)
/* 357 */       i |= 4;
/* 358 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*     */   }
/*     */ 
/*     */   public void kill() throws IOException
/*     */   {
/* 363 */     synchronized (this.stateLock) {
/* 364 */       if (this.state == ChannelState.KILLED)
/* 365 */         return;
/* 366 */       if (this.state == ChannelState.UNINITIALIZED) {
/* 367 */         this.state = ChannelState.KILLED;
/* 368 */         return;
/*     */       }
/* 370 */       assert ((!isOpen()) && (!isRegistered()));
/*     */ 
/* 373 */       if ((this.receiverThread == 0L) && (this.senderThread == 0L)) {
/* 374 */         SctpNet.close(this.fdVal);
/* 375 */         this.state = ChannelState.KILLED;
/*     */       } else {
/* 377 */         this.state = ChannelState.KILLPENDING;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> SctpMultiChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT, Association paramAssociation)
/*     */     throws IOException
/*     */   {
/* 387 */     if (paramSctpSocketOption == null)
/* 388 */       throw new NullPointerException();
/* 389 */     if (!supportedOptions().contains(paramSctpSocketOption)) {
/* 390 */       throw new UnsupportedOperationException("'" + paramSctpSocketOption + "' not supported");
/*     */     }
/* 392 */     synchronized (this.stateLock) {
/* 393 */       if ((paramAssociation != null) && ((paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR))))
/*     */       {
/* 395 */         checkAssociation(paramAssociation);
/*     */       }
/* 397 */       if (!isOpen()) {
/* 398 */         throw new ClosedChannelException();
/*     */       }
/* 400 */       int i = paramAssociation == null ? 0 : paramAssociation.associationID();
/* 401 */       SctpNet.setSocketOption(this.fdVal, paramSctpSocketOption, paramT, i);
/*     */     }
/* 403 */     return this;
/*     */   }
/*     */ 
/*     */   public <T> T getOption(SctpSocketOption<T> paramSctpSocketOption, Association paramAssociation)
/*     */     throws IOException
/*     */   {
/* 410 */     if (paramSctpSocketOption == null)
/* 411 */       throw new NullPointerException();
/* 412 */     if (!supportedOptions().contains(paramSctpSocketOption)) {
/* 413 */       throw new UnsupportedOperationException("'" + paramSctpSocketOption + "' not supported");
/*     */     }
/* 415 */     synchronized (this.stateLock) {
/* 416 */       if ((paramAssociation != null) && ((paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR))))
/*     */       {
/* 418 */         checkAssociation(paramAssociation);
/*     */       }
/* 420 */       if (!isOpen()) {
/* 421 */         throw new ClosedChannelException();
/*     */       }
/* 423 */       int i = paramAssociation == null ? 0 : paramAssociation.associationID();
/* 424 */       return SctpNet.getSocketOption(this.fdVal, paramSctpSocketOption, i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Set<SctpSocketOption<?>> supportedOptions()
/*     */   {
/* 449 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */ 
/*     */   public <T> MessageInfo receive(ByteBuffer paramByteBuffer, T paramT, NotificationHandler<T> paramNotificationHandler)
/*     */     throws IOException
/*     */   {
/* 457 */     if (paramByteBuffer == null) {
/* 458 */       throw new IllegalArgumentException("buffer cannot be null");
/*     */     }
/* 460 */     if (paramByteBuffer.isReadOnly()) {
/* 461 */       throw new IllegalArgumentException("Read-only buffer");
/*     */     }
/* 463 */     if (((Boolean)this.receiveInvoked.get()).booleanValue()) {
/* 464 */       throw new IllegalReceiveException("cannot invoke receive from handler");
/*     */     }
/* 466 */     this.receiveInvoked.set(Boolean.TRUE);
/*     */     try
/*     */     {
/* 469 */       SctpResultContainer localSctpResultContainer = new SctpResultContainer();
/*     */       do {
/* 471 */         localSctpResultContainer.clear();
/* 472 */         synchronized (this.receiveLock) {
/* 473 */           ensureOpen();
/* 474 */           if (!isBound()) {
/* 475 */             throw new NotYetBoundException();
/* 477 */           }int i = 0;
/*     */           Object localObject1;
/*     */           try { begin();
/*     */ 
/* 481 */             synchronized (this.stateLock) {
/* 482 */               if (!isOpen()) {
/* 483 */                 localObject1 = null;
/*     */ 
/* 492 */                 receiverCleanup();
/* 493 */                 end((i > 0) || (i == -2));
/* 494 */                 assert (IOStatus.check(i));
/*     */ 
/* 537 */                 return localObject1;
/*     */               }
/* 484 */               this.receiverThread = NativeThread.current();
/*     */             }
/*     */             do
/*     */             {
/* 488 */               i = receive(this.fdVal, paramByteBuffer, localSctpResultContainer);
/* 489 */               if (i != -3) break;  } while (isOpen());
/*     */           } finally
/*     */           {
/* 492 */             receiverCleanup();
/* 493 */             end((i > 0) || (i == -2));
/* 494 */             if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*     */           }
/*     */ 
/* 497 */           if (!localSctpResultContainer.isNotification())
/*     */           {
/* 499 */             if (localSctpResultContainer.hasSomething())
/*     */             {
/* 501 */               ??? = localSctpResultContainer.getMessageInfo();
/*     */ 
/* 503 */               ???.setAssociation(lookupAssociation(???.associationID()));
/*     */ 
/* 505 */               localObject1 = System.getSecurityManager();
/*     */               Object localObject3;
/* 506 */               if (localObject1 != null) {
/* 507 */                 localObject3 = (InetSocketAddress)???.address();
/* 508 */                 if (!this.addressMap.containsKey(localObject3)) {
/*     */                   try
/*     */                   {
/* 511 */                     ((SecurityManager)localObject1).checkAccept(((InetSocketAddress)localObject3).getAddress().getHostAddress(), ((InetSocketAddress)localObject3).getPort());
/*     */                   }
/*     */                   catch (SecurityException localSecurityException) {
/* 514 */                     paramByteBuffer.clear();
/* 515 */                     throw localSecurityException;
/*     */                   }
/*     */                 }
/*     */               }
/*     */ 
/* 520 */               assert (???.association() != null);
/* 521 */               return ???;
/*     */             }
/*     */ 
/* 524 */             return null;
/*     */           }
/*     */ 
/* 527 */           synchronized (this.stateLock) {
/* 528 */             handleNotificationInternal(localSctpResultContainer);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 533 */       while ((paramNotificationHandler == null) || (invokeNotificationHandler(localSctpResultContainer, paramNotificationHandler, paramT) == HandlerResult.CONTINUE));
/*     */     }
/*     */     finally
/*     */     {
/* 537 */       this.receiveInvoked.set(Boolean.FALSE);
/*     */     }
/*     */ 
/* 540 */     return null;
/*     */   }
/*     */ 
/*     */   private int receive(int paramInt, ByteBuffer paramByteBuffer, SctpResultContainer paramSctpResultContainer)
/*     */     throws IOException
/*     */   {
/* 547 */     int i = paramByteBuffer.position();
/* 548 */     int j = paramByteBuffer.limit();
/* 549 */     assert (i <= j);
/* 550 */     int k = i <= j ? j - i : 0;
/* 551 */     if (((paramByteBuffer instanceof DirectBuffer)) && (k > 0)) {
/* 552 */       return receiveIntoNativeBuffer(paramInt, paramSctpResultContainer, paramByteBuffer, k, i);
/*     */     }
/*     */ 
/* 555 */     int m = Math.max(k, 1);
/* 556 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(m);
/*     */     try {
/* 558 */       int n = receiveIntoNativeBuffer(paramInt, paramSctpResultContainer, localByteBuffer, m, 0);
/* 559 */       localByteBuffer.flip();
/* 560 */       if ((n > 0) && (k > 0))
/* 561 */         paramByteBuffer.put(localByteBuffer);
/* 562 */       return n;
/*     */     } finally {
/* 564 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int receiveIntoNativeBuffer(int paramInt1, SctpResultContainer paramSctpResultContainer, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 574 */     int i = receive0(paramInt1, paramSctpResultContainer, ((DirectBuffer)paramByteBuffer).address() + paramInt3, paramInt2);
/* 575 */     if (i > 0)
/* 576 */       paramByteBuffer.position(paramInt3 + i);
/* 577 */     return i;
/*     */   }
/*     */ 
/*     */   private void handleNotificationInternal(SctpResultContainer paramSctpResultContainer)
/*     */   {
/* 585 */     invokeNotificationHandler(paramSctpResultContainer, this.internalNotificationHandler, null);
/*     */   }
/*     */ 
/*     */   private <T> HandlerResult invokeNotificationHandler(SctpResultContainer paramSctpResultContainer, NotificationHandler<T> paramNotificationHandler, T paramT)
/*     */   {
/* 619 */     SctpNotification localSctpNotification = paramSctpResultContainer.notification();
/* 620 */     localSctpNotification.setAssociation(lookupAssociation(localSctpNotification.assocId()));
/*     */     HandlerResult localHandlerResult;
/*     */     Object localObject;
/* 622 */     if (!(paramNotificationHandler instanceof AbstractNotificationHandler)) {
/* 623 */       localHandlerResult = paramNotificationHandler.handleNotification(localSctpNotification, paramT);
/*     */     } else {
/* 625 */       localObject = (AbstractNotificationHandler)paramNotificationHandler;
/*     */ 
/* 627 */       switch (paramSctpResultContainer.type()) {
/*     */       case 3:
/* 629 */         localHandlerResult = ((AbstractNotificationHandler)localObject).handleNotification(paramSctpResultContainer.getAssociationChanged(), paramT);
/*     */ 
/* 631 */         break;
/*     */       case 4:
/* 633 */         localHandlerResult = ((AbstractNotificationHandler)localObject).handleNotification(paramSctpResultContainer.getPeerAddressChanged(), paramT);
/*     */ 
/* 635 */         break;
/*     */       case 2:
/* 637 */         localHandlerResult = ((AbstractNotificationHandler)localObject).handleNotification(paramSctpResultContainer.getSendFailed(), paramT);
/*     */ 
/* 639 */         break;
/*     */       case 5:
/* 641 */         localHandlerResult = ((AbstractNotificationHandler)localObject).handleNotification(paramSctpResultContainer.getShutdown(), paramT);
/*     */ 
/* 643 */         break;
/*     */       default:
/* 646 */         localHandlerResult = ((AbstractNotificationHandler)localObject).handleNotification(paramSctpResultContainer.notification(), paramT);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 651 */     if (!(paramNotificationHandler instanceof InternalNotificationHandler))
/*     */     {
/* 654 */       localObject = (Association)this.associationToRemove.get();
/* 655 */       if (localObject != null) {
/* 656 */         removeAssociation((Association)localObject);
/* 657 */         this.associationToRemove.set(null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 662 */     return localHandlerResult;
/*     */   }
/*     */ 
/*     */   private Association lookupAssociation(int paramInt)
/*     */   {
/* 667 */     synchronized (this.stateLock) {
/* 668 */       Set localSet = this.associationMap.keySet();
/* 669 */       for (Association localAssociation : localSet) {
/* 670 */         if (localAssociation.associationID() == paramInt) {
/* 671 */           return localAssociation;
/*     */         }
/*     */       }
/*     */     }
/* 675 */     return null;
/*     */   }
/*     */ 
/*     */   private void addAssociation(Association paramAssociation) {
/* 679 */     synchronized (this.stateLock) {
/* 680 */       int i = paramAssociation.associationID();
/* 681 */       Set localSet = null;
/*     */       try
/*     */       {
/* 684 */         localSet = SctpNet.getRemoteAddresses(this.fdVal, i);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */ 
/* 690 */       this.associationMap.put(paramAssociation, localSet);
/* 691 */       if (localSet != null)
/* 692 */         for (SocketAddress localSocketAddress : localSet)
/* 693 */           this.addressMap.put(localSocketAddress, paramAssociation);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void removeAssociation(Association paramAssociation)
/*     */   {
/* 699 */     synchronized (this.stateLock) {
/* 700 */       int i = paramAssociation.associationID();
/* 701 */       Set localSet1 = null;
/*     */       try
/*     */       {
/* 704 */         localSet1 = SctpNet.getRemoteAddresses(this.fdVal, i);
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */ 
/* 710 */       Set localSet2 = this.associationMap.keySet();
/* 711 */       for (Object localObject1 = localSet2.iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (Association)((Iterator)localObject1).next();
/* 712 */         if (((Association)localObject2).associationID() == i) {
/* 713 */           this.associationMap.remove(localObject2);
/* 714 */           break;
/*     */         }
/*     */       }
/*     */       Object localObject2;
/* 717 */       if (localSet1 != null) {
/* 718 */         for (localObject1 = localSet1.iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (SocketAddress)((Iterator)localObject1).next();
/* 719 */           this.addressMap.remove(localObject2); }
/*     */       }
/*     */       else {
/* 722 */         localObject1 = this.addressMap.entrySet();
/*     */ 
/* 724 */         localObject2 = ((Set)localObject1).iterator();
/* 725 */         while (((Iterator)localObject2).hasNext()) {
/* 726 */           Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
/* 727 */           if (((Association)localEntry.getValue()).equals(paramAssociation))
/* 728 */             ((Iterator)localObject2).remove();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean checkAssociation(Association paramAssociation)
/*     */   {
/* 743 */     synchronized (this.stateLock) {
/* 744 */       for (Association localAssociation : this.associationMap.keySet()) {
/* 745 */         if (paramAssociation.equals(localAssociation)) {
/* 746 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 750 */     throw new IllegalArgumentException("Given Association is not controlled by this channel");
/*     */   }
/*     */ 
/*     */   private void checkStreamNumber(Association paramAssociation, int paramInt)
/*     */   {
/* 755 */     synchronized (this.stateLock) {
/* 756 */       if ((paramInt < 0) || (paramInt >= paramAssociation.maxOutboundStreams()))
/* 757 */         throw new InvalidStreamException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int send(ByteBuffer paramByteBuffer, MessageInfo paramMessageInfo)
/*     */     throws IOException
/*     */   {
/* 768 */     if (paramByteBuffer == null) {
/* 769 */       throw new IllegalArgumentException("buffer cannot be null");
/*     */     }
/* 771 */     if (paramMessageInfo == null) {
/* 772 */       throw new IllegalArgumentException("messageInfo cannot be null");
/*     */     }
/* 774 */     synchronized (this.sendLock) {
/* 775 */       ensureOpen();
/*     */ 
/* 777 */       if (!isBound()) {
/* 778 */         bind(null, 0);
/*     */       }
/* 780 */       int i = 0;
/*     */       try {
/* 782 */         int j = -1;
/* 783 */         Object localObject1 = null;
/* 784 */         begin();
/*     */ 
/* 786 */         synchronized (this.stateLock) {
/* 787 */           if (!isOpen()) {
/* 788 */             int m = 0;
/*     */ 
/* 829 */             senderCleanup();
/* 830 */             end((i > 0) || (i == -2));
/* 831 */             assert (IOStatus.check(i)); return m;
/*     */           }
/* 789 */           this.senderThread = NativeThread.current();
/*     */ 
/* 792 */           Association localAssociation1 = paramMessageInfo.association();
/* 793 */           InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramMessageInfo.address();
/* 794 */           if (localAssociation1 != null) {
/* 795 */             checkAssociation(localAssociation1);
/* 796 */             checkStreamNumber(localAssociation1, paramMessageInfo.streamNumber());
/* 797 */             j = localAssociation1.associationID();
/*     */ 
/* 799 */             if (localInetSocketAddress != null) {
/* 800 */               if (!localAssociation1.equals(this.addressMap.get(localInetSocketAddress)))
/* 801 */                 throw new IllegalArgumentException("given preferred address is not part of this association");
/* 802 */               localObject1 = localInetSocketAddress;
/*     */             }
/* 804 */           } else if (localInetSocketAddress != null) {
/* 805 */             localObject1 = localInetSocketAddress;
/* 806 */             Association localAssociation2 = (Association)this.addressMap.get(localInetSocketAddress);
/* 807 */             if (localAssociation2 != null) {
/* 808 */               checkStreamNumber(localAssociation2, paramMessageInfo.streamNumber());
/* 809 */               j = localAssociation2.associationID();
/*     */             }
/*     */             else {
/* 812 */               SecurityManager localSecurityManager = System.getSecurityManager();
/* 813 */               if (localSecurityManager != null)
/* 814 */                 localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
/*     */             }
/*     */           }
/*     */           else {
/* 818 */             throw new AssertionError("Both association and address cannot be null");
/*     */           }
/*     */         }
/*     */ 
/*     */         do
/*     */         {
/* 824 */           i = send(this.fdVal, paramByteBuffer, j, localObject1, paramMessageInfo);
/* 825 */         }while ((i == -3) && (isOpen()));
/*     */ 
/* 827 */         int k = IOStatus.normalize(i);
/*     */ 
/* 829 */         senderCleanup();
/* 830 */         end((i > 0) || (i == -2));
/* 831 */         assert (IOStatus.check(i)); return k;
/*     */       }
/*     */       finally
/*     */       {
/* 829 */         senderCleanup();
/* 830 */         end((i > 0) || (i == -2));
/* 831 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int send(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, SocketAddress paramSocketAddress, MessageInfo paramMessageInfo)
/*     */     throws IOException
/*     */   {
/* 842 */     int i = paramMessageInfo.streamNumber();
/* 843 */     boolean bool = paramMessageInfo.isUnordered();
/* 844 */     int j = paramMessageInfo.payloadProtocolID();
/*     */ 
/* 846 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/* 847 */       return sendFromNativeBuffer(paramInt1, paramByteBuffer, paramSocketAddress, paramInt2, i, bool, j);
/*     */     }
/*     */ 
/* 851 */     int k = paramByteBuffer.position();
/* 852 */     int m = paramByteBuffer.limit();
/* 853 */     assert ((k <= m) && (i >= 0));
/*     */ 
/* 855 */     int n = k <= m ? m - k : 0;
/* 856 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(n);
/*     */     try {
/* 858 */       localByteBuffer.put(paramByteBuffer);
/* 859 */       localByteBuffer.flip();
/*     */ 
/* 861 */       paramByteBuffer.position(k);
/*     */ 
/* 863 */       int i1 = sendFromNativeBuffer(paramInt1, localByteBuffer, paramSocketAddress, paramInt2, i, bool, j);
/*     */ 
/* 865 */       if (i1 > 0)
/*     */       {
/* 867 */         paramByteBuffer.position(k + i1);
/*     */       }
/* 869 */       return i1;
/*     */     } finally {
/* 871 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int sendFromNativeBuffer(int paramInt1, ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4)
/*     */     throws IOException
/*     */   {
/* 883 */     InetAddress localInetAddress = null;
/* 884 */     int i = 0;
/* 885 */     if (paramSocketAddress != null) {
/* 886 */       InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/* 887 */       localInetAddress = localInetSocketAddress.getAddress();
/* 888 */       i = localInetSocketAddress.getPort();
/*     */     }
/* 890 */     int j = paramByteBuffer.position();
/* 891 */     int k = paramByteBuffer.limit();
/* 892 */     assert (j <= k);
/* 893 */     int m = j <= k ? k - j : 0;
/*     */ 
/* 895 */     int n = send0(paramInt1, ((DirectBuffer)paramByteBuffer).address() + j, m, localInetAddress, i, paramInt2, paramInt3, paramBoolean, paramInt4);
/*     */ 
/* 897 */     if (n > 0)
/* 898 */       paramByteBuffer.position(j + n);
/* 899 */     return n;
/*     */   }
/*     */ 
/*     */   public SctpMultiChannel shutdown(Association paramAssociation)
/*     */     throws IOException
/*     */   {
/* 905 */     synchronized (this.stateLock) {
/* 906 */       checkAssociation(paramAssociation);
/* 907 */       if (!isOpen()) {
/* 908 */         throw new ClosedChannelException();
/*     */       }
/* 910 */       SctpNet.shutdown(this.fdVal, paramAssociation.associationID());
/*     */     }
/* 912 */     return this;
/*     */   }
/*     */ 
/*     */   public Set<SocketAddress> getAllLocalAddresses()
/*     */     throws IOException
/*     */   {
/* 918 */     synchronized (this.stateLock) {
/* 919 */       if (!isOpen())
/* 920 */         throw new ClosedChannelException();
/* 921 */       if (!isBound()) {
/* 922 */         return Collections.EMPTY_SET;
/*     */       }
/* 924 */       return SctpNet.getLocalAddresses(this.fdVal);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<SocketAddress> getRemoteAddresses(Association paramAssociation)
/*     */     throws IOException
/*     */   {
/* 931 */     synchronized (this.stateLock) {
/* 932 */       checkAssociation(paramAssociation);
/* 933 */       if (!isOpen())
/* 934 */         throw new ClosedChannelException();
/*     */       try
/*     */       {
/* 937 */         return SctpNet.getRemoteAddresses(this.fdVal, paramAssociation.associationID());
/*     */       }
/*     */       catch (SocketException localSocketException) {
/* 940 */         Set localSet = (Set)this.associationMap.get(paramAssociation);
/* 941 */         return localSet != null ? localSet : Collections.EMPTY_SET;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public SctpChannel branch(Association paramAssociation)
/*     */     throws IOException
/*     */   {
/* 949 */     synchronized (this.stateLock) {
/* 950 */       checkAssociation(paramAssociation);
/* 951 */       if (!isOpen()) {
/* 952 */         throw new ClosedChannelException();
/*     */       }
/* 954 */       FileDescriptor localFileDescriptor = SctpNet.branch(this.fdVal, paramAssociation.associationID());
/*     */ 
/* 957 */       removeAssociation(paramAssociation);
/*     */ 
/* 959 */       return new SctpChannelImpl(provider(), localFileDescriptor, paramAssociation);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int receive0(int paramInt1, SctpResultContainer paramSctpResultContainer, long paramLong, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 970 */     return SctpChannelImpl.receive0(paramInt1, paramSctpResultContainer, paramLong, paramInt2, false);
/*     */   }
/*     */ 
/*     */   private static int send0(int paramInt1, long paramLong, int paramInt2, InetAddress paramInetAddress, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6)
/*     */     throws IOException
/*     */   {
/* 984 */     return SctpChannelImpl.send0(paramInt1, paramLong, paramInt2, paramInetAddress, paramInt3, paramInt4, paramInt5, paramBoolean, paramInt6);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 989 */     Util.load();
/* 990 */     AccessController.doPrivileged(new LoadLibraryAction("sctp"));
/*     */   }
/*     */ 
/*     */   private static enum ChannelState
/*     */   {
/*  84 */     UNINITIALIZED, 
/*  85 */     KILLPENDING, 
/*  86 */     KILLED;
/*     */   }
/*     */ 
/*     */   private static class DefaultOptionsHolder
/*     */   {
/* 429 */     static final Set<SctpSocketOption<?>> defaultOptions = defaultOptions();
/*     */ 
/*     */     private static Set<SctpSocketOption<?>> defaultOptions() {
/* 432 */       HashSet localHashSet = new HashSet(10);
/* 433 */       localHashSet.add(SctpStandardSocketOptions.SCTP_DISABLE_FRAGMENTS);
/* 434 */       localHashSet.add(SctpStandardSocketOptions.SCTP_EXPLICIT_COMPLETE);
/* 435 */       localHashSet.add(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE);
/* 436 */       localHashSet.add(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS);
/* 437 */       localHashSet.add(SctpStandardSocketOptions.SCTP_NODELAY);
/* 438 */       localHashSet.add(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR);
/* 439 */       localHashSet.add(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR);
/* 440 */       localHashSet.add(SctpStandardSocketOptions.SO_SNDBUF);
/* 441 */       localHashSet.add(SctpStandardSocketOptions.SO_RCVBUF);
/* 442 */       localHashSet.add(SctpStandardSocketOptions.SO_LINGER);
/* 443 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class InternalNotificationHandler<T> extends AbstractNotificationHandler<T>
/*     */   {
/*     */     private InternalNotificationHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public HandlerResult handleNotification(AssociationChangeNotification paramAssociationChangeNotification, T paramT)
/*     */     {
/* 595 */       SctpAssocChange localSctpAssocChange = (SctpAssocChange)paramAssociationChangeNotification;
/*     */ 
/* 598 */       switch (SctpMultiChannelImpl.3.$SwitchMap$com$sun$nio$sctp$AssociationChangeNotification$AssocChangeEvent[paramAssociationChangeNotification.event().ordinal()]) {
/*     */       case 1:
/* 600 */         SctpAssociationImpl localSctpAssociationImpl = new SctpAssociationImpl(localSctpAssocChange.assocId(), localSctpAssocChange.maxInStreams(), localSctpAssocChange.maxOutStreams());
/*     */ 
/* 602 */         SctpMultiChannelImpl.this.addAssociation(localSctpAssociationImpl);
/* 603 */         break;
/*     */       case 2:
/*     */       case 3:
/* 608 */         SctpMultiChannelImpl.this.associationToRemove.set(SctpMultiChannelImpl.this.lookupAssociation(localSctpAssocChange.assocId()));
/*     */       }
/* 610 */       return HandlerResult.CONTINUE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpMultiChannelImpl
 * JD-Core Version:    0.6.2
 */