/*      */ package java.net;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.channels.SocketChannel;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import sun.net.ApplicationProxy;
/*      */ 
/*      */ public class Socket
/*      */   implements Closeable
/*      */ {
/*   59 */   private boolean created = false;
/*   60 */   private boolean bound = false;
/*   61 */   private boolean connected = false;
/*   62 */   private boolean closed = false;
/*   63 */   private Object closeLock = new Object();
/*   64 */   private boolean shutIn = false;
/*   65 */   private boolean shutOut = false;
/*      */   SocketImpl impl;
/*   75 */   private boolean oldImpl = false;
/*      */ 
/* 1580 */   private static SocketImplFactory factory = null;
/*      */ 
/*      */   public Socket()
/*      */   {
/*   85 */     setImpl();
/*      */   }
/*      */ 
/*      */   public Socket(Proxy paramProxy)
/*      */   {
/*  118 */     if (paramProxy == null) {
/*  119 */       throw new IllegalArgumentException("Invalid Proxy");
/*      */     }
/*  121 */     ApplicationProxy localApplicationProxy = paramProxy == Proxy.NO_PROXY ? Proxy.NO_PROXY : ApplicationProxy.create(paramProxy);
/*  122 */     if (localApplicationProxy.type() == Proxy.Type.SOCKS) {
/*  123 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  124 */       InetSocketAddress localInetSocketAddress = (InetSocketAddress)localApplicationProxy.address();
/*  125 */       if (localInetSocketAddress.getAddress() != null) {
/*  126 */         checkAddress(localInetSocketAddress.getAddress(), "Socket");
/*      */       }
/*  128 */       if (localSecurityManager != null) {
/*  129 */         if (localInetSocketAddress.isUnresolved())
/*  130 */           localInetSocketAddress = new InetSocketAddress(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
/*  131 */         if (localInetSocketAddress.isUnresolved())
/*  132 */           localSecurityManager.checkConnect(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
/*      */         else {
/*  134 */           localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
/*      */         }
/*      */       }
/*  137 */       this.impl = new SocksSocketImpl(localApplicationProxy);
/*  138 */       this.impl.setSocket(this);
/*      */     }
/*  140 */     else if (localApplicationProxy == Proxy.NO_PROXY) {
/*  141 */       if (factory == null) {
/*  142 */         this.impl = new PlainSocketImpl();
/*  143 */         this.impl.setSocket(this);
/*      */       } else {
/*  145 */         setImpl();
/*      */       }
/*      */     } else { throw new IllegalArgumentException("Invalid Proxy"); }
/*      */ 
/*      */   }
/*      */ 
/*      */   protected Socket(SocketImpl paramSocketImpl)
/*      */     throws SocketException
/*      */   {
/*  163 */     this.impl = paramSocketImpl;
/*  164 */     if (paramSocketImpl != null) {
/*  165 */       checkOldImpl();
/*  166 */       this.impl.setSocket(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Socket(String paramString, int paramInt)
/*      */     throws UnknownHostException, IOException
/*      */   {
/*  208 */     this(paramString != null ? new InetSocketAddress(paramString, paramInt) : new InetSocketAddress(InetAddress.getByName(null), paramInt), (SocketAddress)null, true);
/*      */   }
/*      */ 
/*      */   public Socket(InetAddress paramInetAddress, int paramInt)
/*      */     throws IOException
/*      */   {
/*  241 */     this(paramInetAddress != null ? new InetSocketAddress(paramInetAddress, paramInt) : null, (SocketAddress)null, true);
/*      */   }
/*      */ 
/*      */   public Socket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  280 */     this(paramString != null ? new InetSocketAddress(paramString, paramInt1) : new InetSocketAddress(InetAddress.getByName(null), paramInt1), new InetSocketAddress(paramInetAddress, paramInt2), true);
/*      */   }
/*      */ 
/*      */   public Socket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  319 */     this(paramInetAddress1 != null ? new InetSocketAddress(paramInetAddress1, paramInt1) : null, new InetSocketAddress(paramInetAddress2, paramInt2), true);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Socket(String paramString, int paramInt, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  365 */     this(paramString != null ? new InetSocketAddress(paramString, paramInt) : new InetSocketAddress(InetAddress.getByName(null), paramInt), (SocketAddress)null, paramBoolean);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Socket(InetAddress paramInetAddress, int paramInt, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  408 */     this(paramInetAddress != null ? new InetSocketAddress(paramInetAddress, paramInt) : null, new InetSocketAddress(0), paramBoolean);
/*      */   }
/*      */ 
/*      */   private Socket(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  414 */     setImpl();
/*      */ 
/*  417 */     if (paramSocketAddress1 == null)
/*  418 */       throw new NullPointerException();
/*      */     try
/*      */     {
/*  421 */       createImpl(paramBoolean);
/*  422 */       if (paramSocketAddress2 != null)
/*  423 */         bind(paramSocketAddress2);
/*  424 */       if (paramSocketAddress1 != null)
/*  425 */         connect(paramSocketAddress1);
/*      */     } catch (IOException localIOException) {
/*  427 */       close();
/*  428 */       throw localIOException;
/*      */     }
/*      */   }
/*      */ 
/*      */   void createImpl(boolean paramBoolean)
/*      */     throws SocketException
/*      */   {
/*  441 */     if (this.impl == null)
/*  442 */       setImpl();
/*      */     try {
/*  444 */       this.impl.create(paramBoolean);
/*  445 */       this.created = true;
/*      */     } catch (IOException localIOException) {
/*  447 */       throw new SocketException(localIOException.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkOldImpl() {
/*  452 */     if (this.impl == null) {
/*  453 */       return;
/*      */     }
/*      */ 
/*  457 */     this.oldImpl = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Boolean run() {
/*  460 */         Class[] arrayOfClass = new Class[2];
/*  461 */         arrayOfClass[0] = SocketAddress.class;
/*  462 */         arrayOfClass[1] = Integer.TYPE;
/*  463 */         Class localClass = Socket.this.impl.getClass();
/*      */         while (true)
/*      */           try {
/*  466 */             localClass.getDeclaredMethod("connect", arrayOfClass);
/*  467 */             return Boolean.FALSE;
/*      */           } catch (NoSuchMethodException localNoSuchMethodException) {
/*  469 */             localClass = localClass.getSuperclass();
/*      */ 
/*  473 */             if (localClass.equals(SocketImpl.class))
/*  474 */               return Boolean.TRUE;
/*      */           }
/*      */       }
/*      */     })).booleanValue();
/*      */   }
/*      */ 
/*      */   void setImpl()
/*      */   {
/*  487 */     if (factory != null) {
/*  488 */       this.impl = factory.createSocketImpl();
/*  489 */       checkOldImpl();
/*      */     }
/*      */     else
/*      */     {
/*  493 */       this.impl = new SocksSocketImpl();
/*      */     }
/*  495 */     if (this.impl != null)
/*  496 */       this.impl.setSocket(this);
/*      */   }
/*      */ 
/*      */   SocketImpl getImpl()
/*      */     throws SocketException
/*      */   {
/*  509 */     if (!this.created)
/*  510 */       createImpl(true);
/*  511 */     return this.impl;
/*      */   }
/*      */ 
/*      */   public void connect(SocketAddress paramSocketAddress)
/*      */     throws IOException
/*      */   {
/*  528 */     connect(paramSocketAddress, 0);
/*      */   }
/*      */ 
/*      */   public void connect(SocketAddress paramSocketAddress, int paramInt)
/*      */     throws IOException
/*      */   {
/*  549 */     if (paramSocketAddress == null) {
/*  550 */       throw new IllegalArgumentException("connect: The address can't be null");
/*      */     }
/*  552 */     if (paramInt < 0) {
/*  553 */       throw new IllegalArgumentException("connect: timeout can't be negative");
/*      */     }
/*  555 */     if (isClosed()) {
/*  556 */       throw new SocketException("Socket is closed");
/*      */     }
/*  558 */     if ((!this.oldImpl) && (isConnected())) {
/*  559 */       throw new SocketException("already connected");
/*      */     }
/*  561 */     if (!(paramSocketAddress instanceof InetSocketAddress)) {
/*  562 */       throw new IllegalArgumentException("Unsupported address type");
/*      */     }
/*  564 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/*  565 */     InetAddress localInetAddress = localInetSocketAddress.getAddress();
/*  566 */     int i = localInetSocketAddress.getPort();
/*  567 */     checkAddress(localInetAddress, "connect");
/*      */ 
/*  569 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  570 */     if (localSecurityManager != null) {
/*  571 */       if (localInetSocketAddress.isUnresolved())
/*  572 */         localSecurityManager.checkConnect(localInetSocketAddress.getHostName(), i);
/*      */       else
/*  574 */         localSecurityManager.checkConnect(localInetAddress.getHostAddress(), i);
/*      */     }
/*  576 */     if (!this.created)
/*  577 */       createImpl(true);
/*  578 */     if (!this.oldImpl)
/*  579 */       this.impl.connect(localInetSocketAddress, paramInt);
/*  580 */     else if (paramInt == 0) {
/*  581 */       if (localInetSocketAddress.isUnresolved())
/*  582 */         this.impl.connect(localInetAddress.getHostName(), i);
/*      */       else
/*  584 */         this.impl.connect(localInetAddress, i);
/*      */     }
/*  586 */     else throw new UnsupportedOperationException("SocketImpl.connect(addr, timeout)");
/*  587 */     this.connected = true;
/*      */ 
/*  592 */     this.bound = true;
/*      */   }
/*      */ 
/*      */   public void bind(SocketAddress paramSocketAddress)
/*      */     throws IOException
/*      */   {
/*  611 */     if (isClosed())
/*  612 */       throw new SocketException("Socket is closed");
/*  613 */     if ((!this.oldImpl) && (isBound())) {
/*  614 */       throw new SocketException("Already bound");
/*      */     }
/*  616 */     if ((paramSocketAddress != null) && (!(paramSocketAddress instanceof InetSocketAddress)))
/*  617 */       throw new IllegalArgumentException("Unsupported address type");
/*  618 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/*  619 */     if ((localInetSocketAddress != null) && (localInetSocketAddress.isUnresolved()))
/*  620 */       throw new SocketException("Unresolved address");
/*  621 */     if (localInetSocketAddress == null) {
/*  622 */       localInetSocketAddress = new InetSocketAddress(0);
/*      */     }
/*  624 */     InetAddress localInetAddress = localInetSocketAddress.getAddress();
/*  625 */     int i = localInetSocketAddress.getPort();
/*  626 */     checkAddress(localInetAddress, "bind");
/*  627 */     getImpl().bind(localInetAddress, i);
/*  628 */     this.bound = true;
/*      */   }
/*      */ 
/*      */   private void checkAddress(InetAddress paramInetAddress, String paramString) {
/*  632 */     if (paramInetAddress == null) {
/*  633 */       return;
/*      */     }
/*  635 */     if ((!(paramInetAddress instanceof Inet4Address)) && (!(paramInetAddress instanceof Inet6Address)))
/*  636 */       throw new IllegalArgumentException(paramString + ": invalid address type");
/*      */   }
/*      */ 
/*      */   final void postAccept()
/*      */   {
/*  644 */     this.connected = true;
/*  645 */     this.created = true;
/*  646 */     this.bound = true;
/*      */   }
/*      */ 
/*      */   void setCreated() {
/*  650 */     this.created = true;
/*      */   }
/*      */ 
/*      */   void setBound() {
/*  654 */     this.bound = true;
/*      */   }
/*      */ 
/*      */   void setConnected() {
/*  658 */     this.connected = true;
/*      */   }
/*      */ 
/*      */   public InetAddress getInetAddress()
/*      */   {
/*  672 */     if (!isConnected())
/*  673 */       return null;
/*      */     try {
/*  675 */       return getImpl().getInetAddress();
/*      */     } catch (SocketException localSocketException) {
/*      */     }
/*  678 */     return null;
/*      */   }
/*      */ 
/*      */   public InetAddress getLocalAddress()
/*      */   {
/*  691 */     if (!isBound())
/*  692 */       return InetAddress.anyLocalAddress();
/*  693 */     InetAddress localInetAddress = null;
/*      */     try {
/*  695 */       localInetAddress = (InetAddress)getImpl().getOption(15);
/*  696 */       if (localInetAddress.isAnyLocalAddress())
/*  697 */         localInetAddress = InetAddress.anyLocalAddress();
/*      */     }
/*      */     catch (Exception localException) {
/*  700 */       localInetAddress = InetAddress.anyLocalAddress();
/*      */     }
/*  702 */     return localInetAddress;
/*      */   }
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  716 */     if (!isConnected())
/*  717 */       return 0;
/*      */     try {
/*  719 */       return getImpl().getPort();
/*      */     }
/*      */     catch (SocketException localSocketException) {
/*      */     }
/*  723 */     return -1;
/*      */   }
/*      */ 
/*      */   public int getLocalPort()
/*      */   {
/*  737 */     if (!isBound())
/*  738 */       return -1;
/*      */     try {
/*  740 */       return getImpl().getLocalPort();
/*      */     }
/*      */     catch (SocketException localSocketException) {
/*      */     }
/*  744 */     return -1;
/*      */   }
/*      */ 
/*      */   public SocketAddress getRemoteSocketAddress()
/*      */   {
/*  765 */     if (!isConnected())
/*  766 */       return null;
/*  767 */     return new InetSocketAddress(getInetAddress(), getPort());
/*      */   }
/*      */ 
/*      */   public SocketAddress getLocalSocketAddress()
/*      */   {
/*  791 */     if (!isBound())
/*  792 */       return null;
/*  793 */     return new InetSocketAddress(getLocalAddress(), getLocalPort());
/*      */   }
/*      */ 
/*      */   public SocketChannel getChannel()
/*      */   {
/*  814 */     return null;
/*      */   }
/*      */ 
/*      */   public InputStream getInputStream()
/*      */     throws IOException
/*      */   {
/*  863 */     if (isClosed())
/*  864 */       throw new SocketException("Socket is closed");
/*  865 */     if (!isConnected())
/*  866 */       throw new SocketException("Socket is not connected");
/*  867 */     if (isInputShutdown())
/*  868 */       throw new SocketException("Socket input is shutdown");
/*  869 */     Socket localSocket = this;
/*  870 */     InputStream localInputStream = null;
/*      */     try {
/*  872 */       localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public InputStream run() throws IOException {
/*  875 */           return Socket.this.impl.getInputStream();
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  879 */       throw ((IOException)localPrivilegedActionException.getException());
/*      */     }
/*  881 */     return localInputStream;
/*      */   }
/*      */ 
/*      */   public OutputStream getOutputStream()
/*      */     throws IOException
/*      */   {
/*  903 */     if (isClosed())
/*  904 */       throw new SocketException("Socket is closed");
/*  905 */     if (!isConnected())
/*  906 */       throw new SocketException("Socket is not connected");
/*  907 */     if (isOutputShutdown())
/*  908 */       throw new SocketException("Socket output is shutdown");
/*  909 */     Socket localSocket = this;
/*  910 */     OutputStream localOutputStream = null;
/*      */     try {
/*  912 */       localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public OutputStream run() throws IOException {
/*  915 */           return Socket.this.impl.getOutputStream();
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  919 */       throw ((IOException)localPrivilegedActionException.getException());
/*      */     }
/*  921 */     return localOutputStream;
/*      */   }
/*      */ 
/*      */   public void setTcpNoDelay(boolean paramBoolean)
/*      */     throws SocketException
/*      */   {
/*  938 */     if (isClosed())
/*  939 */       throw new SocketException("Socket is closed");
/*  940 */     getImpl().setOption(1, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */   public boolean getTcpNoDelay()
/*      */     throws SocketException
/*      */   {
/*  953 */     if (isClosed())
/*  954 */       throw new SocketException("Socket is closed");
/*  955 */     return ((Boolean)getImpl().getOption(1)).booleanValue();
/*      */   }
/*      */ 
/*      */   public void setSoLinger(boolean paramBoolean, int paramInt)
/*      */     throws SocketException
/*      */   {
/*  973 */     if (isClosed())
/*  974 */       throw new SocketException("Socket is closed");
/*  975 */     if (!paramBoolean) {
/*  976 */       getImpl().setOption(128, new Boolean(paramBoolean));
/*      */     } else {
/*  978 */       if (paramInt < 0) {
/*  979 */         throw new IllegalArgumentException("invalid value for SO_LINGER");
/*      */       }
/*  981 */       if (paramInt > 65535)
/*  982 */         paramInt = 65535;
/*  983 */       getImpl().setOption(128, new Integer(paramInt));
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getSoLinger()
/*      */     throws SocketException
/*      */   {
/* 1000 */     if (isClosed())
/* 1001 */       throw new SocketException("Socket is closed");
/* 1002 */     Object localObject = getImpl().getOption(128);
/* 1003 */     if ((localObject instanceof Integer)) {
/* 1004 */       return ((Integer)localObject).intValue();
/*      */     }
/* 1006 */     return -1;
/*      */   }
/*      */ 
/*      */   public void sendUrgentData(int paramInt)
/*      */     throws IOException
/*      */   {
/* 1021 */     if (!getImpl().supportsUrgentData()) {
/* 1022 */       throw new SocketException("Urgent data not supported");
/*      */     }
/* 1024 */     getImpl().sendUrgentData(paramInt);
/*      */   }
/*      */ 
/*      */   public void setOOBInline(boolean paramBoolean)
/*      */     throws SocketException
/*      */   {
/* 1051 */     if (isClosed())
/* 1052 */       throw new SocketException("Socket is closed");
/* 1053 */     getImpl().setOption(4099, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */   public boolean getOOBInline()
/*      */     throws SocketException
/*      */   {
/* 1066 */     if (isClosed())
/* 1067 */       throw new SocketException("Socket is closed");
/* 1068 */     return ((Boolean)getImpl().getOption(4099)).booleanValue();
/*      */   }
/*      */ 
/*      */   public synchronized void setSoTimeout(int paramInt)
/*      */     throws SocketException
/*      */   {
/* 1088 */     if (isClosed())
/* 1089 */       throw new SocketException("Socket is closed");
/* 1090 */     if (paramInt < 0) {
/* 1091 */       throw new IllegalArgumentException("timeout can't be negative");
/*      */     }
/* 1093 */     getImpl().setOption(4102, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public synchronized int getSoTimeout()
/*      */     throws SocketException
/*      */   {
/* 1106 */     if (isClosed())
/* 1107 */       throw new SocketException("Socket is closed");
/* 1108 */     Object localObject = getImpl().getOption(4102);
/*      */ 
/* 1110 */     if ((localObject instanceof Integer)) {
/* 1111 */       return ((Integer)localObject).intValue();
/*      */     }
/* 1113 */     return 0;
/*      */   }
/*      */ 
/*      */   public synchronized void setSendBufferSize(int paramInt)
/*      */     throws SocketException
/*      */   {
/* 1141 */     if (paramInt <= 0) {
/* 1142 */       throw new IllegalArgumentException("negative send size");
/*      */     }
/* 1144 */     if (isClosed())
/* 1145 */       throw new SocketException("Socket is closed");
/* 1146 */     getImpl().setOption(4097, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public synchronized int getSendBufferSize()
/*      */     throws SocketException
/*      */   {
/* 1162 */     if (isClosed())
/* 1163 */       throw new SocketException("Socket is closed");
/* 1164 */     int i = 0;
/* 1165 */     Object localObject = getImpl().getOption(4097);
/* 1166 */     if ((localObject instanceof Integer)) {
/* 1167 */       i = ((Integer)localObject).intValue();
/*      */     }
/* 1169 */     return i;
/*      */   }
/*      */ 
/*      */   public synchronized void setReceiveBufferSize(int paramInt)
/*      */     throws SocketException
/*      */   {
/* 1213 */     if (paramInt <= 0) {
/* 1214 */       throw new IllegalArgumentException("invalid receive size");
/*      */     }
/* 1216 */     if (isClosed())
/* 1217 */       throw new SocketException("Socket is closed");
/* 1218 */     getImpl().setOption(4098, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public synchronized int getReceiveBufferSize()
/*      */     throws SocketException
/*      */   {
/* 1234 */     if (isClosed())
/* 1235 */       throw new SocketException("Socket is closed");
/* 1236 */     int i = 0;
/* 1237 */     Object localObject = getImpl().getOption(4098);
/* 1238 */     if ((localObject instanceof Integer)) {
/* 1239 */       i = ((Integer)localObject).intValue();
/*      */     }
/* 1241 */     return i;
/*      */   }
/*      */ 
/*      */   public void setKeepAlive(boolean paramBoolean)
/*      */     throws SocketException
/*      */   {
/* 1254 */     if (isClosed())
/* 1255 */       throw new SocketException("Socket is closed");
/* 1256 */     getImpl().setOption(8, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */   public boolean getKeepAlive()
/*      */     throws SocketException
/*      */   {
/* 1269 */     if (isClosed())
/* 1270 */       throw new SocketException("Socket is closed");
/* 1271 */     return ((Boolean)getImpl().getOption(8)).booleanValue();
/*      */   }
/*      */ 
/*      */   public void setTrafficClass(int paramInt)
/*      */     throws SocketException
/*      */   {
/* 1320 */     if ((paramInt < 0) || (paramInt > 255)) {
/* 1321 */       throw new IllegalArgumentException("tc is not in range 0 -- 255");
/*      */     }
/* 1323 */     if (isClosed())
/* 1324 */       throw new SocketException("Socket is closed");
/* 1325 */     getImpl().setOption(3, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public int getTrafficClass()
/*      */     throws SocketException
/*      */   {
/* 1344 */     return ((Integer)getImpl().getOption(3)).intValue();
/*      */   }
/*      */ 
/*      */   public void setReuseAddress(boolean paramBoolean)
/*      */     throws SocketException
/*      */   {
/* 1382 */     if (isClosed())
/* 1383 */       throw new SocketException("Socket is closed");
/* 1384 */     getImpl().setOption(4, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */   public boolean getReuseAddress()
/*      */     throws SocketException
/*      */   {
/* 1397 */     if (isClosed())
/* 1398 */       throw new SocketException("Socket is closed");
/* 1399 */     return ((Boolean)getImpl().getOption(4)).booleanValue();
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws IOException
/*      */   {
/* 1425 */     synchronized (this.closeLock) {
/* 1426 */       if (isClosed())
/* 1427 */         return;
/* 1428 */       if (this.created)
/* 1429 */         this.impl.close();
/* 1430 */       this.closed = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void shutdownInput()
/*      */     throws IOException
/*      */   {
/* 1453 */     if (isClosed())
/* 1454 */       throw new SocketException("Socket is closed");
/* 1455 */     if (!isConnected())
/* 1456 */       throw new SocketException("Socket is not connected");
/* 1457 */     if (isInputShutdown())
/* 1458 */       throw new SocketException("Socket input is already shutdown");
/* 1459 */     getImpl().shutdownInput();
/* 1460 */     this.shutIn = true;
/*      */   }
/*      */ 
/*      */   public void shutdownOutput()
/*      */     throws IOException
/*      */   {
/* 1483 */     if (isClosed())
/* 1484 */       throw new SocketException("Socket is closed");
/* 1485 */     if (!isConnected())
/* 1486 */       throw new SocketException("Socket is not connected");
/* 1487 */     if (isOutputShutdown())
/* 1488 */       throw new SocketException("Socket output is already shutdown");
/* 1489 */     getImpl().shutdownOutput();
/* 1490 */     this.shutOut = true;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*      */     try
/*      */     {
/* 1500 */       if (isConnected())
/* 1501 */         return "Socket[addr=" + getImpl().getInetAddress() + ",port=" + getImpl().getPort() + ",localport=" + getImpl().getLocalPort() + "]";
/*      */     }
/*      */     catch (SocketException localSocketException)
/*      */     {
/*      */     }
/* 1506 */     return "Socket[unconnected]";
/*      */   }
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/* 1522 */     return (this.connected) || (this.oldImpl);
/*      */   }
/*      */ 
/*      */   public boolean isBound()
/*      */   {
/* 1539 */     return (this.bound) || (this.oldImpl);
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/* 1550 */     synchronized (this.closeLock) {
/* 1551 */       return this.closed;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isInputShutdown()
/*      */   {
/* 1563 */     return this.shutIn;
/*      */   }
/*      */ 
/*      */   public boolean isOutputShutdown()
/*      */   {
/* 1574 */     return this.shutOut;
/*      */   }
/*      */ 
/*      */   public static synchronized void setSocketImplFactory(SocketImplFactory paramSocketImplFactory)
/*      */     throws IOException
/*      */   {
/* 1609 */     if (factory != null) {
/* 1610 */       throw new SocketException("factory already defined");
/*      */     }
/* 1612 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1613 */     if (localSecurityManager != null) {
/* 1614 */       localSecurityManager.checkSetFactory();
/*      */     }
/* 1616 */     factory = paramSocketImplFactory;
/*      */   }
/*      */ 
/*      */   public void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.Socket
 * JD-Core Version:    0.6.2
 */