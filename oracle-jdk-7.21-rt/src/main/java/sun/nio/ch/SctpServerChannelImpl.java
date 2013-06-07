/*     */ package sun.nio.ch;
/*     */ 
/*     */ import com.sun.nio.sctp.IllegalUnbindException;
/*     */ import com.sun.nio.sctp.SctpChannel;
/*     */ import com.sun.nio.sctp.SctpServerChannel;
/*     */ import com.sun.nio.sctp.SctpSocketOption;
/*     */ import com.sun.nio.sctp.SctpStandardSocketOptions;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.security.AccessController;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import sun.security.action.LoadLibraryAction;
/*     */ 
/*     */ public class SctpServerChannelImpl extends SctpServerChannel
/*     */   implements SelChImpl
/*     */ {
/*     */   private final FileDescriptor fd;
/*     */   private final int fdVal;
/*  56 */   private volatile long thread = 0L;
/*     */ 
/*  59 */   private final Object lock = new Object();
/*     */ 
/*  63 */   private final Object stateLock = new Object();
/*     */ 
/*  72 */   private ChannelState state = ChannelState.UNINITIALIZED;
/*     */ 
/*  75 */   int port = -1;
/*  76 */   private HashSet<InetSocketAddress> localAddresses = new HashSet();
/*     */   private boolean wildcard;
/*     */ 
/*     */   public SctpServerChannelImpl(SelectorProvider paramSelectorProvider)
/*     */     throws IOException
/*     */   {
/*  88 */     super(paramSelectorProvider);
/*  89 */     this.fd = SctpNet.socket(true);
/*  90 */     this.fdVal = IOUtil.fdVal(this.fd);
/*  91 */     this.state = ChannelState.INUSE;
/*     */   }
/*     */ 
/*     */   public SctpServerChannel bind(SocketAddress paramSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/*  97 */     synchronized (this.lock) {
/*  98 */       synchronized (this.stateLock) {
/*  99 */         if (!isOpen())
/* 100 */           throw new ClosedChannelException();
/* 101 */         if (isBound()) {
/* 102 */           SctpNet.throwAlreadyBoundException();
/*     */         }
/* 104 */         InetSocketAddress localInetSocketAddress1 = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*     */ 
/* 106 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 107 */         if (localSecurityManager != null)
/* 108 */           localSecurityManager.checkListen(localInetSocketAddress1.getPort());
/* 109 */         Net.bind(this.fd, localInetSocketAddress1.getAddress(), localInetSocketAddress1.getPort());
/*     */ 
/* 111 */         InetSocketAddress localInetSocketAddress2 = Net.localAddress(this.fd);
/* 112 */         this.port = localInetSocketAddress2.getPort();
/* 113 */         this.localAddresses.add(localInetSocketAddress1);
/* 114 */         if (localInetSocketAddress1.getAddress().isAnyLocalAddress()) {
/* 115 */           this.wildcard = true;
/*     */         }
/* 117 */         SctpNet.listen(this.fdVal, paramInt < 1 ? 50 : paramInt);
/*     */       }
/*     */     }
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   public SctpServerChannel bindAddress(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/* 126 */     return bindUnbindAddress(paramInetAddress, true);
/*     */   }
/*     */ 
/*     */   public SctpServerChannel unbindAddress(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/* 132 */     return bindUnbindAddress(paramInetAddress, false);
/*     */   }
/*     */ 
/*     */   private SctpServerChannel bindUnbindAddress(InetAddress paramInetAddress, boolean paramBoolean) throws IOException
/*     */   {
/* 137 */     if (paramInetAddress == null) {
/* 138 */       throw new IllegalArgumentException();
/*     */     }
/* 140 */     synchronized (this.lock)
/*     */     {
/*     */       Object localObject1;
/*     */       Iterator localIterator2;
/* 141 */       synchronized (this.stateLock) {
/* 142 */         if (!isOpen())
/* 143 */           throw new ClosedChannelException();
/* 144 */         if (!isBound())
/* 145 */           throw new NotYetBoundException();
/* 146 */         if (this.wildcard) {
/* 147 */           throw new IllegalStateException("Cannot add or remove addresses from a channel that is bound to the wildcard address");
/*     */         }
/* 149 */         if (paramInetAddress.isAnyLocalAddress())
/* 150 */           throw new IllegalArgumentException("Cannot add or remove the wildcard address");
/*     */         Iterator localIterator1;
/* 152 */         if (paramBoolean) {
/* 153 */           for (localIterator1 = this.localAddresses.iterator(); localIterator1.hasNext(); ) { localObject1 = (InetSocketAddress)localIterator1.next();
/* 154 */             if (((InetSocketAddress)localObject1).getAddress().equals(paramInetAddress)) {
/* 155 */               SctpNet.throwAlreadyBoundException();
/*     */             }
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 161 */           if (this.localAddresses.size() <= 1)
/* 162 */             throw new IllegalUnbindException("Cannot remove address from a channel with only one address bound");
/* 163 */           int i = 0;
/* 164 */           for (localObject1 = this.localAddresses.iterator(); ((Iterator)localObject1).hasNext(); ) { InetSocketAddress localInetSocketAddress = (InetSocketAddress)((Iterator)localObject1).next();
/* 165 */             if (localInetSocketAddress.getAddress().equals(paramInetAddress)) {
/* 166 */               i = 1;
/* 167 */               break;
/*     */             }
/*     */           }
/* 170 */           if (i == 0) {
/* 171 */             throw new IllegalUnbindException("Cannot remove address from a channel that is not bound to that address");
/*     */           }
/*     */         }
/* 174 */         SctpNet.bindx(this.fdVal, new InetAddress[] { paramInetAddress }, this.port, paramBoolean);
/*     */ 
/* 177 */         if (paramBoolean)
/* 178 */           this.localAddresses.add(new InetSocketAddress(paramInetAddress, this.port));
/*     */         else {
/* 180 */           for (localIterator2 = this.localAddresses.iterator(); localIterator2.hasNext(); ) { localObject1 = (InetSocketAddress)localIterator2.next();
/* 181 */             if (((InetSocketAddress)localObject1).getAddress().equals(paramInetAddress)) {
/* 182 */               this.localAddresses.remove(localObject1);
/* 183 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 189 */     return this;
/*     */   }
/*     */ 
/*     */   private boolean isBound() {
/* 193 */     synchronized (this.stateLock) {
/* 194 */       return this.port != -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void acceptCleanup() throws IOException {
/* 199 */     synchronized (this.stateLock) {
/* 200 */       this.thread = 0L;
/* 201 */       if (this.state == ChannelState.KILLPENDING)
/* 202 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public SctpChannel accept() throws IOException
/*     */   {
/* 208 */     synchronized (this.lock) {
/* 209 */       if (!isOpen())
/* 210 */         throw new ClosedChannelException();
/* 211 */       if (!isBound())
/* 212 */         throw new NotYetBoundException();
/* 213 */       SctpChannelImpl localSctpChannelImpl = null;
/*     */ 
/* 215 */       int i = 0;
/* 216 */       FileDescriptor localFileDescriptor = new FileDescriptor();
/* 217 */       InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
/*     */       try
/*     */       {
/* 220 */         begin();
/* 221 */         if (!isOpen()) {
/* 222 */           localObject1 = null;
/*     */ 
/* 231 */           acceptCleanup();
/* 232 */           end(i > 0);
/* 233 */           assert (IOStatus.check(i)); return localObject1;
/*     */         }
/* 223 */         this.thread = NativeThread.current();
/*     */         while (true) {
/* 225 */           i = accept0(this.fd, localFileDescriptor, arrayOfInetSocketAddress);
/* 226 */           if ((i != -3) || (!isOpen()))
/*     */             break;
/*     */         }
/*     */       }
/*     */       finally {
/* 231 */         acceptCleanup();
/* 232 */         end(i > 0);
/* 233 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*     */       }
/*     */ 
/* 236 */       if (i < 1) {
/* 237 */         return null;
/*     */       }
/* 239 */       IOUtil.configureBlocking(localFileDescriptor, true);
/* 240 */       Object localObject1 = arrayOfInetSocketAddress[0];
/* 241 */       localSctpChannelImpl = new SctpChannelImpl(provider(), localFileDescriptor);
/*     */ 
/* 243 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 244 */       if (localSecurityManager != null) {
/* 245 */         localSecurityManager.checkAccept(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1).getPort());
/*     */       }
/*     */ 
/* 248 */       return localSctpChannelImpl;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException
/*     */   {
/* 254 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void implCloseSelectableChannel() throws IOException
/*     */   {
/* 259 */     synchronized (this.stateLock) {
/* 260 */       SctpNet.preClose(this.fdVal);
/* 261 */       if (this.thread != 0L)
/* 262 */         NativeThread.signal(this.thread);
/* 263 */       if (!isRegistered())
/* 264 */         kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void kill() throws IOException
/*     */   {
/* 270 */     synchronized (this.stateLock) {
/* 271 */       if (this.state == ChannelState.KILLED)
/* 272 */         return;
/* 273 */       if (this.state == ChannelState.UNINITIALIZED) {
/* 274 */         this.state = ChannelState.KILLED;
/* 275 */         return;
/*     */       }
/* 277 */       assert ((!isOpen()) && (!isRegistered()));
/*     */ 
/* 280 */       if (this.thread == 0L) {
/* 281 */         SctpNet.close(this.fdVal);
/* 282 */         this.state = ChannelState.KILLED;
/*     */       } else {
/* 284 */         this.state = ChannelState.KILLPENDING;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public FileDescriptor getFD()
/*     */   {
/* 291 */     return this.fd;
/*     */   }
/*     */ 
/*     */   public int getFDVal()
/*     */   {
/* 296 */     return this.fdVal;
/*     */   }
/*     */ 
/*     */   private boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 304 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 305 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 306 */     int k = paramInt2;
/*     */ 
/* 308 */     if ((paramInt1 & 0x20) != 0)
/*     */     {
/* 312 */       return false;
/*     */     }
/*     */ 
/* 315 */     if ((paramInt1 & 0x18) != 0)
/*     */     {
/* 317 */       k = i;
/* 318 */       paramSelectionKeyImpl.nioReadyOps(k);
/* 319 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */ 
/* 322 */     if (((paramInt1 & 0x1) != 0) && ((i & 0x10) != 0))
/*     */     {
/* 324 */       k |= 16;
/*     */     }
/* 326 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 327 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */ 
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 332 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 337 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */ 
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 342 */     int i = 0;
/*     */ 
/* 345 */     if ((paramInt & 0x10) != 0) {
/* 346 */       i |= 1;
/*     */     }
/* 348 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*     */   }
/*     */ 
/*     */   public <T> SctpServerChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/* 355 */     if (paramSctpSocketOption == null)
/* 356 */       throw new NullPointerException();
/* 357 */     if (!supportedOptions().contains(paramSctpSocketOption)) {
/* 358 */       throw new UnsupportedOperationException("'" + paramSctpSocketOption + "' not supported");
/*     */     }
/* 360 */     synchronized (this.stateLock) {
/* 361 */       if (!isOpen()) {
/* 362 */         throw new ClosedChannelException();
/*     */       }
/* 364 */       SctpNet.setSocketOption(this.fdVal, paramSctpSocketOption, paramT, 0);
/* 365 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T getOption(SctpSocketOption<T> paramSctpSocketOption) throws IOException
/*     */   {
/* 371 */     if (paramSctpSocketOption == null)
/* 372 */       throw new NullPointerException();
/* 373 */     if (!supportedOptions().contains(paramSctpSocketOption)) {
/* 374 */       throw new UnsupportedOperationException("'" + paramSctpSocketOption + "' not supported");
/*     */     }
/* 376 */     synchronized (this.stateLock) {
/* 377 */       if (!isOpen()) {
/* 378 */         throw new ClosedChannelException();
/*     */       }
/* 380 */       return SctpNet.getSocketOption(this.fdVal, paramSctpSocketOption, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Set<SctpSocketOption<?>> supportedOptions()
/*     */   {
/* 396 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */ 
/*     */   public Set<SocketAddress> getAllLocalAddresses()
/*     */     throws IOException
/*     */   {
/* 402 */     synchronized (this.stateLock) {
/* 403 */       if (!isOpen())
/* 404 */         throw new ClosedChannelException();
/* 405 */       if (!isBound()) {
/* 406 */         return Collections.EMPTY_SET;
/*     */       }
/* 408 */       return SctpNet.getLocalAddresses(this.fdVal);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   private static native int accept0(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
/*     */     throws IOException;
/*     */ 
/*     */   static
/*     */   {
/* 419 */     Util.load();
/* 420 */     AccessController.doPrivileged(new LoadLibraryAction("sctp"));
/*     */ 
/* 422 */     initIDs();
/*     */   }
/*     */ 
/*     */   private static enum ChannelState
/*     */   {
/*  66 */     UNINITIALIZED, 
/*  67 */     INUSE, 
/*  68 */     KILLPENDING, 
/*  69 */     KILLED;
/*     */   }
/*     */ 
/*     */   private static class DefaultOptionsHolder
/*     */   {
/* 385 */     static final Set<SctpSocketOption<?>> defaultOptions = defaultOptions();
/*     */ 
/*     */     private static Set<SctpSocketOption<?>> defaultOptions() {
/* 388 */       HashSet localHashSet = new HashSet(1);
/* 389 */       localHashSet.add(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS);
/* 390 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpServerChannelImpl
 * JD-Core Version:    0.6.2
 */