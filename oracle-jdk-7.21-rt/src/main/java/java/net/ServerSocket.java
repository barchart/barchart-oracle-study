/*     */ package java.net;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ 
/*     */ public class ServerSocket
/*     */   implements Closeable
/*     */ {
/*  56 */   private boolean created = false;
/*  57 */   private boolean bound = false;
/*  58 */   private boolean closed = false;
/*  59 */   private Object closeLock = new Object();
/*     */   private SocketImpl impl;
/*  69 */   private boolean oldImpl = false;
/*     */ 
/* 734 */   private static SocketImplFactory factory = null;
/*     */ 
/*     */   ServerSocket(SocketImpl paramSocketImpl)
/*     */   {
/*  76 */     this.impl = paramSocketImpl;
/*  77 */     paramSocketImpl.setServerSocket(this);
/*     */   }
/*     */ 
/*     */   public ServerSocket()
/*     */     throws IOException
/*     */   {
/*  87 */     setImpl();
/*     */   }
/*     */ 
/*     */   public ServerSocket(int paramInt)
/*     */     throws IOException
/*     */   {
/* 128 */     this(paramInt, 50, null);
/*     */   }
/*     */ 
/*     */   public ServerSocket(int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 181 */     this(paramInt1, paramInt2, null);
/*     */   }
/*     */ 
/*     */   public ServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/* 230 */     setImpl();
/* 231 */     if ((paramInt1 < 0) || (paramInt1 > 65535)) {
/* 232 */       throw new IllegalArgumentException("Port value out of range: " + paramInt1);
/*     */     }
/* 234 */     if (paramInt2 < 1)
/* 235 */       paramInt2 = 50;
/*     */     try {
/* 237 */       bind(new InetSocketAddress(paramInetAddress, paramInt1), paramInt2);
/*     */     } catch (SecurityException localSecurityException) {
/* 239 */       close();
/* 240 */       throw localSecurityException;
/*     */     } catch (IOException localIOException) {
/* 242 */       close();
/* 243 */       throw localIOException;
/*     */     }
/*     */   }
/*     */ 
/*     */   SocketImpl getImpl()
/*     */     throws SocketException
/*     */   {
/* 256 */     if (!this.created)
/* 257 */       createImpl();
/* 258 */     return this.impl;
/*     */   }
/*     */ 
/*     */   private void checkOldImpl() {
/* 262 */     if (this.impl == null) {
/* 263 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 267 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Void run() throws NoSuchMethodException {
/* 270 */           Class[] arrayOfClass = new Class[2];
/* 271 */           arrayOfClass[0] = SocketAddress.class;
/* 272 */           arrayOfClass[1] = Integer.TYPE;
/* 273 */           ServerSocket.this.impl.getClass().getDeclaredMethod("connect", arrayOfClass);
/* 274 */           return null;
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/* 278 */       this.oldImpl = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setImpl() {
/* 283 */     if (factory != null) {
/* 284 */       this.impl = factory.createSocketImpl();
/* 285 */       checkOldImpl();
/*     */     }
/*     */     else
/*     */     {
/* 289 */       this.impl = new SocksSocketImpl();
/*     */     }
/* 291 */     if (this.impl != null)
/* 292 */       this.impl.setServerSocket(this);
/*     */   }
/*     */ 
/*     */   void createImpl()
/*     */     throws SocketException
/*     */   {
/* 302 */     if (this.impl == null)
/* 303 */       setImpl();
/*     */     try {
/* 305 */       this.impl.create(true);
/* 306 */       this.created = true;
/*     */     } catch (IOException localIOException) {
/* 308 */       throw new SocketException(localIOException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void bind(SocketAddress paramSocketAddress)
/*     */     throws IOException
/*     */   {
/* 330 */     bind(paramSocketAddress, 50);
/*     */   }
/*     */ 
/*     */   public void bind(SocketAddress paramSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 359 */     if (isClosed())
/* 360 */       throw new SocketException("Socket is closed");
/* 361 */     if ((!this.oldImpl) && (isBound()))
/* 362 */       throw new SocketException("Already bound");
/* 363 */     if (paramSocketAddress == null)
/* 364 */       paramSocketAddress = new InetSocketAddress(0);
/* 365 */     if (!(paramSocketAddress instanceof InetSocketAddress))
/* 366 */       throw new IllegalArgumentException("Unsupported address type");
/* 367 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/* 368 */     if (localInetSocketAddress.isUnresolved())
/* 369 */       throw new SocketException("Unresolved address");
/* 370 */     if (paramInt < 1)
/* 371 */       paramInt = 50;
/*     */     try {
/* 373 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 374 */       if (localSecurityManager != null)
/* 375 */         localSecurityManager.checkListen(localInetSocketAddress.getPort());
/* 376 */       getImpl().bind(localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 377 */       getImpl().listen(paramInt);
/* 378 */       this.bound = true;
/*     */     } catch (SecurityException localSecurityException) {
/* 380 */       this.bound = false;
/* 381 */       throw localSecurityException;
/*     */     } catch (IOException localIOException) {
/* 383 */       this.bound = false;
/* 384 */       throw localIOException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public InetAddress getInetAddress()
/*     */   {
/* 399 */     if (!isBound())
/* 400 */       return null;
/*     */     try {
/* 402 */       return getImpl().getInetAddress();
/*     */     }
/*     */     catch (SocketException localSocketException)
/*     */     {
/*     */     }
/*     */ 
/* 408 */     return null;
/*     */   }
/*     */ 
/*     */   public int getLocalPort()
/*     */   {
/* 422 */     if (!isBound())
/* 423 */       return -1;
/*     */     try {
/* 425 */       return getImpl().getLocalPort();
/*     */     }
/*     */     catch (SocketException localSocketException)
/*     */     {
/*     */     }
/*     */ 
/* 431 */     return -1;
/*     */   }
/*     */ 
/*     */   public SocketAddress getLocalSocketAddress()
/*     */   {
/* 451 */     if (!isBound())
/* 452 */       return null;
/* 453 */     return new InetSocketAddress(getInetAddress(), getLocalPort());
/*     */   }
/*     */ 
/*     */   public Socket accept()
/*     */     throws IOException
/*     */   {
/* 485 */     if (isClosed())
/* 486 */       throw new SocketException("Socket is closed");
/* 487 */     if (!isBound())
/* 488 */       throw new SocketException("Socket is not bound yet");
/* 489 */     Socket localSocket = new Socket((SocketImpl)null);
/* 490 */     implAccept(localSocket);
/* 491 */     return localSocket;
/*     */   }
/*     */ 
/*     */   protected final void implAccept(Socket paramSocket)
/*     */     throws IOException
/*     */   {
/* 511 */     SocketImpl localSocketImpl = null;
/*     */     try {
/* 513 */       if (paramSocket.impl == null)
/* 514 */         paramSocket.setImpl();
/*     */       else {
/* 516 */         paramSocket.impl.reset();
/*     */       }
/* 518 */       localSocketImpl = paramSocket.impl;
/* 519 */       paramSocket.impl = null;
/* 520 */       localSocketImpl.address = new InetAddress();
/* 521 */       localSocketImpl.fd = new FileDescriptor();
/* 522 */       getImpl().accept(localSocketImpl);
/*     */ 
/* 524 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 525 */       if (localSecurityManager != null)
/* 526 */         localSecurityManager.checkAccept(localSocketImpl.getInetAddress().getHostAddress(), localSocketImpl.getPort());
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 530 */       if (localSocketImpl != null)
/* 531 */         localSocketImpl.reset();
/* 532 */       paramSocket.impl = localSocketImpl;
/* 533 */       throw localIOException;
/*     */     } catch (SecurityException localSecurityException) {
/* 535 */       if (localSocketImpl != null)
/* 536 */         localSocketImpl.reset();
/* 537 */       paramSocket.impl = localSocketImpl;
/* 538 */       throw localSecurityException;
/*     */     }
/* 540 */     paramSocket.impl = localSocketImpl;
/* 541 */     paramSocket.postAccept();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 558 */     synchronized (this.closeLock) {
/* 559 */       if (isClosed())
/* 560 */         return;
/* 561 */       if (this.created)
/* 562 */         this.impl.close();
/* 563 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServerSocketChannel getChannel()
/*     */   {
/* 584 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isBound()
/*     */   {
/* 595 */     return (this.bound) || (this.oldImpl);
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 605 */     synchronized (this.closeLock) {
/* 606 */       return this.closed;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setSoTimeout(int paramInt)
/*     */     throws SocketException
/*     */   {
/* 627 */     if (isClosed())
/* 628 */       throw new SocketException("Socket is closed");
/* 629 */     getImpl().setOption(4102, new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   public synchronized int getSoTimeout()
/*     */     throws IOException
/*     */   {
/* 641 */     if (isClosed())
/* 642 */       throw new SocketException("Socket is closed");
/* 643 */     Object localObject = getImpl().getOption(4102);
/*     */ 
/* 645 */     if ((localObject instanceof Integer)) {
/* 646 */       return ((Integer)localObject).intValue();
/*     */     }
/* 648 */     return 0;
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean paramBoolean)
/*     */     throws SocketException
/*     */   {
/* 689 */     if (isClosed())
/* 690 */       throw new SocketException("Socket is closed");
/* 691 */     getImpl().setOption(4, Boolean.valueOf(paramBoolean));
/*     */   }
/*     */ 
/*     */   public boolean getReuseAddress()
/*     */     throws SocketException
/*     */   {
/* 704 */     if (isClosed())
/* 705 */       throw new SocketException("Socket is closed");
/* 706 */     return ((Boolean)getImpl().getOption(4)).booleanValue();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 716 */     if (!isBound())
/* 717 */       return "ServerSocket[unbound]";
/* 718 */     return "ServerSocket[addr=" + this.impl.getInetAddress() + ",port=" + this.impl.getPort() + ",localport=" + this.impl.getLocalPort() + "]";
/*     */   }
/*     */ 
/*     */   void setBound()
/*     */   {
/* 724 */     this.bound = true;
/*     */   }
/*     */ 
/*     */   void setCreated() {
/* 728 */     this.created = true;
/*     */   }
/*     */ 
/*     */   public static synchronized void setSocketFactory(SocketImplFactory paramSocketImplFactory)
/*     */     throws IOException
/*     */   {
/* 762 */     if (factory != null) {
/* 763 */       throw new SocketException("factory already defined");
/*     */     }
/* 765 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 766 */     if (localSecurityManager != null) {
/* 767 */       localSecurityManager.checkSetFactory();
/*     */     }
/* 769 */     factory = paramSocketImplFactory;
/*     */   }
/*     */ 
/*     */   public synchronized void setReceiveBufferSize(int paramInt)
/*     */     throws SocketException
/*     */   {
/* 808 */     if (paramInt <= 0) {
/* 809 */       throw new IllegalArgumentException("negative receive size");
/*     */     }
/* 811 */     if (isClosed())
/* 812 */       throw new SocketException("Socket is closed");
/* 813 */     getImpl().setOption(4098, new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   public synchronized int getReceiveBufferSize()
/*     */     throws SocketException
/*     */   {
/* 831 */     if (isClosed())
/* 832 */       throw new SocketException("Socket is closed");
/* 833 */     int i = 0;
/* 834 */     Object localObject = getImpl().getOption(4098);
/* 835 */     if ((localObject instanceof Integer)) {
/* 836 */       i = ((Integer)localObject).intValue();
/*     */     }
/* 838 */     return i;
/*     */   }
/*     */ 
/*     */   public void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.ServerSocket
 * JD-Core Version:    0.6.2
 */