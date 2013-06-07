/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.AsynchronousServerSocketChannel;
/*     */ import java.nio.channels.AsynchronousSocketChannel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import sun.net.NetHooks;
/*     */ 
/*     */ abstract class AsynchronousServerSocketChannelImpl extends AsynchronousServerSocketChannel
/*     */   implements Cancellable, Groupable
/*     */ {
/*     */   protected final FileDescriptor fd;
/*  54 */   protected volatile SocketAddress localAddress = null;
/*     */ 
/*  57 */   private final Object stateLock = new Object();
/*     */ 
/*  60 */   private ReadWriteLock closeLock = new ReentrantReadWriteLock();
/*  61 */   private volatile boolean open = true;
/*     */   private volatile boolean acceptKilled;
/*     */ 
/*     */   AsynchronousServerSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
/*     */   {
/*  68 */     super(paramAsynchronousChannelGroupImpl.provider());
/*  69 */     this.fd = Net.serverSocket(true);
/*     */   }
/*     */ 
/*     */   public final boolean isOpen()
/*     */   {
/*  74 */     return this.open;
/*     */   }
/*     */ 
/*     */   final void begin()
/*     */     throws IOException
/*     */   {
/*  81 */     this.closeLock.readLock().lock();
/*  82 */     if (!isOpen())
/*  83 */       throw new ClosedChannelException();
/*     */   }
/*     */ 
/*     */   final void end()
/*     */   {
/*  90 */     this.closeLock.readLock().unlock();
/*     */   }
/*     */ 
/*     */   abstract void implClose()
/*     */     throws IOException;
/*     */ 
/*     */   public final void close()
/*     */     throws IOException
/*     */   {
/* 101 */     this.closeLock.writeLock().lock();
/*     */     try {
/* 103 */       if (!this.open)
/*     */         return;
/* 105 */       this.open = false;
/*     */     } finally {
/* 107 */       this.closeLock.writeLock().unlock();
/*     */     }
/* 109 */     implClose();
/*     */   }
/*     */ 
/*     */   abstract Future<AsynchronousSocketChannel> implAccept(Object paramObject, CompletionHandler<AsynchronousSocketChannel, Object> paramCompletionHandler);
/*     */ 
/*     */   public final Future<AsynchronousSocketChannel> accept()
/*     */   {
/* 122 */     return implAccept(null, null);
/*     */   }
/*     */ 
/*     */   public final <A> void accept(A paramA, CompletionHandler<AsynchronousSocketChannel, ? super A> paramCompletionHandler)
/*     */   {
/* 130 */     if (paramCompletionHandler == null)
/* 131 */       throw new NullPointerException("'handler' is null");
/* 132 */     implAccept(paramA, paramCompletionHandler);
/*     */   }
/*     */ 
/*     */   final boolean isAcceptKilled() {
/* 136 */     return this.acceptKilled;
/*     */   }
/*     */ 
/*     */   public final void onCancel(PendingFuture<?, ?> paramPendingFuture)
/*     */   {
/* 141 */     this.acceptKilled = true;
/*     */   }
/*     */ 
/*     */   public final AsynchronousServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 148 */     InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*     */ 
/* 150 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 151 */     if (localSecurityManager != null)
/* 152 */       localSecurityManager.checkListen(localInetSocketAddress.getPort());
/*     */     try
/*     */     {
/* 155 */       begin();
/* 156 */       synchronized (this.stateLock) {
/* 157 */         if (this.localAddress != null)
/* 158 */           throw new AlreadyBoundException();
/* 159 */         NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 160 */         Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 161 */         Net.listen(this.fd, paramInt < 1 ? 50 : paramInt);
/* 162 */         this.localAddress = Net.localAddress(this.fd);
/*     */       }
/*     */     } finally {
/* 165 */       end();
/*     */     }
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */   public final SocketAddress getLocalAddress() throws IOException
/*     */   {
/* 172 */     if (!isOpen())
/* 173 */       throw new ClosedChannelException();
/* 174 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   public final <T> AsynchronousServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/* 182 */     if (paramSocketOption == null)
/* 183 */       throw new NullPointerException();
/* 184 */     if (!supportedOptions().contains(paramSocketOption))
/* 185 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     try
/*     */     {
/* 188 */       begin();
/* 189 */       Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/* 190 */       return this;
/*     */     } finally {
/* 192 */       end();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final <T> T getOption(SocketOption<T> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 199 */     if (paramSocketOption == null)
/* 200 */       throw new NullPointerException();
/* 201 */     if (!supportedOptions().contains(paramSocketOption))
/* 202 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     try
/*     */     {
/* 205 */       begin();
/* 206 */       return Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*     */     } finally {
/* 208 */       end();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Set<SocketOption<?>> supportedOptions()
/*     */   {
/* 225 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 230 */     StringBuilder localStringBuilder = new StringBuilder();
/* 231 */     localStringBuilder.append(getClass().getName());
/* 232 */     localStringBuilder.append('[');
/* 233 */     if (!isOpen()) {
/* 234 */       localStringBuilder.append("closed");
/*     */     }
/* 236 */     else if (this.localAddress == null)
/* 237 */       localStringBuilder.append("unbound");
/*     */     else {
/* 239 */       localStringBuilder.append(this.localAddress.toString());
/*     */     }
/*     */ 
/* 242 */     localStringBuilder.append(']');
/* 243 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   private static class DefaultOptionsHolder
/*     */   {
/* 213 */     static final Set<SocketOption<?>> defaultOptions = defaultOptions();
/*     */ 
/*     */     private static Set<SocketOption<?>> defaultOptions() {
/* 216 */       HashSet localHashSet = new HashSet(2);
/* 217 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/* 218 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/* 219 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.AsynchronousServerSocketChannelImpl
 * JD-Core Version:    0.6.2
 */