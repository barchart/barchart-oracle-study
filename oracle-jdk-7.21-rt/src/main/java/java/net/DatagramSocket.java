/*      */ package java.net;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.nio.channels.DatagramChannel;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ 
/*      */ public class DatagramSocket
/*      */   implements Closeable
/*      */ {
/*   73 */   private boolean created = false;
/*   74 */   private boolean bound = false;
/*   75 */   private boolean closed = false;
/*   76 */   private Object closeLock = new Object();
/*      */   DatagramSocketImpl impl;
/*   86 */   boolean oldImpl = false;
/*      */   static final int ST_NOT_CONNECTED = 0;
/*      */   static final int ST_CONNECTED = 1;
/*      */   static final int ST_CONNECTED_NO_IMPL = 2;
/*   98 */   int connectState = 0;
/*      */ 
/*  103 */   InetAddress connectedAddress = null;
/*  104 */   int connectedPort = -1;
/*      */ 
/*  307 */   static Class implClass = null;
/*      */   static DatagramSocketImplFactory factory;
/*      */ 
/*      */   private synchronized void connectInternal(InetAddress paramInetAddress, int paramInt)
/*      */     throws SocketException
/*      */   {
/*  115 */     if ((paramInt < 0) || (paramInt > 65535)) {
/*  116 */       throw new IllegalArgumentException("connect: " + paramInt);
/*      */     }
/*  118 */     if (paramInetAddress == null) {
/*  119 */       throw new IllegalArgumentException("connect: null address");
/*      */     }
/*  121 */     checkAddress(paramInetAddress, "connect");
/*  122 */     if (isClosed())
/*  123 */       return;
/*  124 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  125 */     if (localSecurityManager != null) {
/*  126 */       if (paramInetAddress.isMulticastAddress()) {
/*  127 */         localSecurityManager.checkMulticast(paramInetAddress);
/*      */       } else {
/*  129 */         localSecurityManager.checkConnect(paramInetAddress.getHostAddress(), paramInt);
/*  130 */         localSecurityManager.checkAccept(paramInetAddress.getHostAddress(), paramInt);
/*      */       }
/*      */     }
/*      */ 
/*  134 */     if (!isBound()) {
/*  135 */       bind(new InetSocketAddress(0));
/*      */     }
/*      */ 
/*  138 */     if ((this.oldImpl) || (((this.impl instanceof AbstractPlainDatagramSocketImpl)) && (((AbstractPlainDatagramSocketImpl)this.impl).nativeConnectDisabled())))
/*      */     {
/*  140 */       this.connectState = 2;
/*      */     }
/*      */     else try {
/*  143 */         getImpl().connect(paramInetAddress, paramInt);
/*      */ 
/*  146 */         this.connectState = 1;
/*      */       }
/*      */       catch (SocketException localSocketException)
/*      */       {
/*  150 */         this.connectState = 2;
/*      */       }
/*      */ 
/*      */ 
/*  154 */     this.connectedAddress = paramInetAddress;
/*  155 */     this.connectedPort = paramInt;
/*      */   }
/*      */ 
/*      */   public DatagramSocket()
/*      */     throws SocketException
/*      */   {
/*  179 */     createImpl();
/*      */     try {
/*  181 */       bind(new InetSocketAddress(0));
/*      */     } catch (SocketException localSocketException) {
/*  183 */       throw localSocketException;
/*      */     } catch (IOException localIOException) {
/*  185 */       throw new SocketException(localIOException.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected DatagramSocket(DatagramSocketImpl paramDatagramSocketImpl)
/*      */   {
/*  198 */     if (paramDatagramSocketImpl == null)
/*  199 */       throw new NullPointerException();
/*  200 */     this.impl = paramDatagramSocketImpl;
/*  201 */     checkOldImpl();
/*      */   }
/*      */ 
/*      */   public DatagramSocket(SocketAddress paramSocketAddress)
/*      */     throws SocketException
/*      */   {
/*  229 */     createImpl();
/*  230 */     if (paramSocketAddress != null)
/*  231 */       bind(paramSocketAddress);
/*      */   }
/*      */ 
/*      */   public DatagramSocket(int paramInt)
/*      */     throws SocketException
/*      */   {
/*  256 */     this(paramInt, null);
/*      */   }
/*      */ 
/*      */   public DatagramSocket(int paramInt, InetAddress paramInetAddress)
/*      */     throws SocketException
/*      */   {
/*  284 */     this(new InetSocketAddress(paramInetAddress, paramInt));
/*      */   }
/*      */ 
/*      */   private void checkOldImpl() {
/*  288 */     if (this.impl == null) {
/*  289 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  293 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Void run() throws NoSuchMethodException {
/*  296 */           Class[] arrayOfClass = new Class[1];
/*  297 */           arrayOfClass[0] = DatagramPacket.class;
/*  298 */           DatagramSocket.this.impl.getClass().getDeclaredMethod("peekData", arrayOfClass);
/*  299 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  303 */       this.oldImpl = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   void createImpl()
/*      */     throws SocketException
/*      */   {
/*  310 */     if (this.impl == null) {
/*  311 */       if (factory != null) {
/*  312 */         this.impl = factory.createDatagramSocketImpl();
/*  313 */         checkOldImpl();
/*      */       } else {
/*  315 */         boolean bool = (this instanceof MulticastSocket);
/*  316 */         this.impl = DefaultDatagramSocketImplFactory.createDatagramSocketImpl(bool);
/*      */ 
/*  318 */         checkOldImpl();
/*      */       }
/*      */     }
/*      */ 
/*  322 */     this.impl.create();
/*  323 */     this.created = true;
/*      */   }
/*      */ 
/*      */   DatagramSocketImpl getImpl()
/*      */     throws SocketException
/*      */   {
/*  336 */     if (!this.created)
/*  337 */       createImpl();
/*  338 */     return this.impl;
/*      */   }
/*      */ 
/*      */   public synchronized void bind(SocketAddress paramSocketAddress)
/*      */     throws SocketException
/*      */   {
/*  357 */     if (isClosed())
/*  358 */       throw new SocketException("Socket is closed");
/*  359 */     if (isBound())
/*  360 */       throw new SocketException("already bound");
/*  361 */     if (paramSocketAddress == null)
/*  362 */       paramSocketAddress = new InetSocketAddress(0);
/*  363 */     if (!(paramSocketAddress instanceof InetSocketAddress))
/*  364 */       throw new IllegalArgumentException("Unsupported address type!");
/*  365 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/*  366 */     if (localInetSocketAddress.isUnresolved())
/*  367 */       throw new SocketException("Unresolved address");
/*  368 */     InetAddress localInetAddress = localInetSocketAddress.getAddress();
/*  369 */     int i = localInetSocketAddress.getPort();
/*  370 */     checkAddress(localInetAddress, "bind");
/*  371 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  372 */     if (localSecurityManager != null)
/*  373 */       localSecurityManager.checkListen(i);
/*      */     try
/*      */     {
/*  376 */       getImpl().bind(i, localInetAddress);
/*      */     } catch (SocketException localSocketException) {
/*  378 */       getImpl().close();
/*  379 */       throw localSocketException;
/*      */     }
/*  381 */     this.bound = true;
/*      */   }
/*      */ 
/*      */   void checkAddress(InetAddress paramInetAddress, String paramString) {
/*  385 */     if (paramInetAddress == null) {
/*  386 */       return;
/*      */     }
/*  388 */     if ((!(paramInetAddress instanceof Inet4Address)) && (!(paramInetAddress instanceof Inet6Address)))
/*  389 */       throw new IllegalArgumentException(paramString + ": invalid address type");
/*      */   }
/*      */ 
/*      */   public void connect(InetAddress paramInetAddress, int paramInt)
/*      */   {
/*      */     try
/*      */     {
/*  442 */       connectInternal(paramInetAddress, paramInt);
/*      */     } catch (SocketException localSocketException) {
/*  444 */       throw new Error("connect failed", localSocketException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void connect(SocketAddress paramSocketAddress)
/*      */     throws SocketException
/*      */   {
/*  471 */     if (paramSocketAddress == null)
/*  472 */       throw new IllegalArgumentException("Address can't be null");
/*  473 */     if (!(paramSocketAddress instanceof InetSocketAddress))
/*  474 */       throw new IllegalArgumentException("Unsupported address type");
/*  475 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/*  476 */     if (localInetSocketAddress.isUnresolved())
/*  477 */       throw new SocketException("Unresolved address");
/*  478 */     connectInternal(localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*      */   }
/*      */ 
/*      */   public void disconnect()
/*      */   {
/*  488 */     synchronized (this) {
/*  489 */       if (isClosed())
/*  490 */         return;
/*  491 */       if (this.connectState == 1) {
/*  492 */         this.impl.disconnect();
/*      */       }
/*  494 */       this.connectedAddress = null;
/*  495 */       this.connectedPort = -1;
/*  496 */       this.connectState = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isBound()
/*      */   {
/*  511 */     return this.bound;
/*      */   }
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/*  525 */     return this.connectState != 0;
/*      */   }
/*      */ 
/*      */   public InetAddress getInetAddress()
/*      */   {
/*  539 */     return this.connectedAddress;
/*      */   }
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  553 */     return this.connectedPort;
/*      */   }
/*      */ 
/*      */   public SocketAddress getRemoteSocketAddress()
/*      */   {
/*  573 */     if (!isConnected())
/*  574 */       return null;
/*  575 */     return new InetSocketAddress(getInetAddress(), getPort());
/*      */   }
/*      */ 
/*      */   public SocketAddress getLocalSocketAddress()
/*      */   {
/*  590 */     if (isClosed())
/*  591 */       return null;
/*  592 */     if (!isBound())
/*  593 */       return null;
/*  594 */     return new InetSocketAddress(getLocalAddress(), getLocalPort());
/*      */   }
/*      */ 
/*      */   public void send(DatagramPacket paramDatagramPacket)
/*      */     throws IOException
/*      */   {
/*  638 */     InetAddress localInetAddress = null;
/*  639 */     synchronized (paramDatagramPacket) {
/*  640 */       if (isClosed())
/*  641 */         throw new SocketException("Socket is closed");
/*  642 */       checkAddress(paramDatagramPacket.getAddress(), "send");
/*  643 */       if (this.connectState == 0)
/*      */       {
/*  645 */         SecurityManager localSecurityManager = System.getSecurityManager();
/*      */ 
/*  651 */         if (localSecurityManager != null) {
/*  652 */           if (paramDatagramPacket.getAddress().isMulticastAddress())
/*  653 */             localSecurityManager.checkMulticast(paramDatagramPacket.getAddress());
/*      */           else {
/*  655 */             localSecurityManager.checkConnect(paramDatagramPacket.getAddress().getHostAddress(), paramDatagramPacket.getPort());
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  661 */         localInetAddress = paramDatagramPacket.getAddress();
/*  662 */         if (localInetAddress == null) {
/*  663 */           paramDatagramPacket.setAddress(this.connectedAddress);
/*  664 */           paramDatagramPacket.setPort(this.connectedPort);
/*  665 */         } else if ((!localInetAddress.equals(this.connectedAddress)) || (paramDatagramPacket.getPort() != this.connectedPort))
/*      */         {
/*  667 */           throw new IllegalArgumentException("connected address and packet address differ");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  673 */       if (!isBound()) {
/*  674 */         bind(new InetSocketAddress(0));
/*      */       }
/*  676 */       getImpl().send(paramDatagramPacket);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void receive(DatagramPacket paramDatagramPacket)
/*      */     throws IOException
/*      */   {
/*  712 */     synchronized (paramDatagramPacket) {
/*  713 */       if (!isBound())
/*  714 */         bind(new InetSocketAddress(0));
/*      */       Object localObject1;
/*      */       int j;
/*  715 */       if (this.connectState == 0)
/*      */       {
/*  717 */         SecurityManager localSecurityManager = System.getSecurityManager();
/*  718 */         if (localSecurityManager != null) {
/*      */           while (true) {
/*  720 */             localObject1 = null;
/*  721 */             j = 0;
/*      */             Object localObject2;
/*  723 */             if (!this.oldImpl)
/*      */             {
/*  725 */               localObject2 = new DatagramPacket(new byte[1], 1);
/*  726 */               j = getImpl().peekData((DatagramPacket)localObject2);
/*  727 */               localObject1 = ((DatagramPacket)localObject2).getAddress().getHostAddress();
/*      */             } else {
/*  729 */               localObject2 = new InetAddress();
/*  730 */               j = getImpl().peek((InetAddress)localObject2);
/*  731 */               localObject1 = ((InetAddress)localObject2).getHostAddress();
/*      */             }
/*      */             try {
/*  734 */               localSecurityManager.checkAccept((String)localObject1, j);
/*      */             }
/*      */             catch (SecurityException localSecurityException)
/*      */             {
/*  741 */               DatagramPacket localDatagramPacket2 = new DatagramPacket(new byte[1], 1);
/*  742 */               getImpl().receive(localDatagramPacket2);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  755 */       if (this.connectState == 2)
/*      */       {
/*  759 */         int i = 0;
/*  760 */         while (i == 0) {
/*  761 */           localObject1 = null;
/*  762 */           j = -1;
/*      */           DatagramPacket localDatagramPacket1;
/*  764 */           if (!this.oldImpl)
/*      */           {
/*  766 */             localDatagramPacket1 = new DatagramPacket(new byte[1], 1);
/*  767 */             j = getImpl().peekData(localDatagramPacket1);
/*  768 */             localObject1 = localDatagramPacket1.getAddress();
/*      */           }
/*      */           else {
/*  771 */             localObject1 = new InetAddress();
/*  772 */             j = getImpl().peek((InetAddress)localObject1);
/*      */           }
/*  774 */           if ((!this.connectedAddress.equals(localObject1)) || (this.connectedPort != j))
/*      */           {
/*  777 */             localDatagramPacket1 = new DatagramPacket(new byte[1], 1);
/*  778 */             getImpl().receive(localDatagramPacket1);
/*      */           } else {
/*  780 */             i = 1;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  786 */       getImpl().receive(paramDatagramPacket);
/*      */     }
/*      */   }
/*      */ 
/*      */   public InetAddress getLocalAddress()
/*      */   {
/*  809 */     if (isClosed())
/*  810 */       return null;
/*  811 */     InetAddress localInetAddress = null;
/*      */     try {
/*  813 */       localInetAddress = (InetAddress)getImpl().getOption(15);
/*  814 */       if (localInetAddress.isAnyLocalAddress()) {
/*  815 */         localInetAddress = InetAddress.anyLocalAddress();
/*      */       }
/*  817 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  818 */       if (localSecurityManager != null)
/*  819 */         localSecurityManager.checkConnect(localInetAddress.getHostAddress(), -1);
/*      */     }
/*      */     catch (Exception localException) {
/*  822 */       localInetAddress = InetAddress.anyLocalAddress();
/*      */     }
/*  824 */     return localInetAddress;
/*      */   }
/*      */ 
/*      */   public int getLocalPort()
/*      */   {
/*  836 */     if (isClosed())
/*  837 */       return -1;
/*      */     try {
/*  839 */       return getImpl().getLocalPort(); } catch (Exception localException) {
/*      */     }
/*  841 */     return 0;
/*      */   }
/*      */ 
/*      */   public synchronized void setSoTimeout(int paramInt)
/*      */     throws SocketException
/*      */   {
/*  861 */     if (isClosed())
/*  862 */       throw new SocketException("Socket is closed");
/*  863 */     getImpl().setOption(4102, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public synchronized int getSoTimeout()
/*      */     throws SocketException
/*      */   {
/*  876 */     if (isClosed())
/*  877 */       throw new SocketException("Socket is closed");
/*  878 */     if (getImpl() == null)
/*  879 */       return 0;
/*  880 */     Object localObject = getImpl().getOption(4102);
/*      */ 
/*  882 */     if ((localObject instanceof Integer)) {
/*  883 */       return ((Integer)localObject).intValue();
/*      */     }
/*  885 */     return 0;
/*      */   }
/*      */ 
/*      */   public synchronized void setSendBufferSize(int paramInt)
/*      */     throws SocketException
/*      */   {
/*  920 */     if (paramInt <= 0) {
/*  921 */       throw new IllegalArgumentException("negative send size");
/*      */     }
/*  923 */     if (isClosed())
/*  924 */       throw new SocketException("Socket is closed");
/*  925 */     getImpl().setOption(4097, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public synchronized int getSendBufferSize()
/*      */     throws SocketException
/*      */   {
/*  938 */     if (isClosed())
/*  939 */       throw new SocketException("Socket is closed");
/*  940 */     int i = 0;
/*  941 */     Object localObject = getImpl().getOption(4097);
/*  942 */     if ((localObject instanceof Integer)) {
/*  943 */       i = ((Integer)localObject).intValue();
/*      */     }
/*  945 */     return i;
/*      */   }
/*      */ 
/*      */   public synchronized void setReceiveBufferSize(int paramInt)
/*      */     throws SocketException
/*      */   {
/*  978 */     if (paramInt <= 0) {
/*  979 */       throw new IllegalArgumentException("invalid receive size");
/*      */     }
/*  981 */     if (isClosed())
/*  982 */       throw new SocketException("Socket is closed");
/*  983 */     getImpl().setOption(4098, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public synchronized int getReceiveBufferSize()
/*      */     throws SocketException
/*      */   {
/*  996 */     if (isClosed())
/*  997 */       throw new SocketException("Socket is closed");
/*  998 */     int i = 0;
/*  999 */     Object localObject = getImpl().getOption(4098);
/* 1000 */     if ((localObject instanceof Integer)) {
/* 1001 */       i = ((Integer)localObject).intValue();
/*      */     }
/* 1003 */     return i;
/*      */   }
/*      */ 
/*      */   public synchronized void setReuseAddress(boolean paramBoolean)
/*      */     throws SocketException
/*      */   {
/* 1041 */     if (isClosed()) {
/* 1042 */       throw new SocketException("Socket is closed");
/*      */     }
/* 1044 */     if (this.oldImpl)
/* 1045 */       getImpl().setOption(4, new Integer(paramBoolean ? -1 : 0));
/*      */     else
/* 1047 */       getImpl().setOption(4, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */   public synchronized boolean getReuseAddress()
/*      */     throws SocketException
/*      */   {
/* 1060 */     if (isClosed())
/* 1061 */       throw new SocketException("Socket is closed");
/* 1062 */     Object localObject = getImpl().getOption(4);
/* 1063 */     return ((Boolean)localObject).booleanValue();
/*      */   }
/*      */ 
/*      */   public synchronized void setBroadcast(boolean paramBoolean)
/*      */     throws SocketException
/*      */   {
/* 1084 */     if (isClosed())
/* 1085 */       throw new SocketException("Socket is closed");
/* 1086 */     getImpl().setOption(32, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */   public synchronized boolean getBroadcast()
/*      */     throws SocketException
/*      */   {
/* 1098 */     if (isClosed())
/* 1099 */       throw new SocketException("Socket is closed");
/* 1100 */     return ((Boolean)getImpl().getOption(32)).booleanValue();
/*      */   }
/*      */ 
/*      */   public synchronized void setTrafficClass(int paramInt)
/*      */     throws SocketException
/*      */   {
/* 1141 */     if ((paramInt < 0) || (paramInt > 255)) {
/* 1142 */       throw new IllegalArgumentException("tc is not in range 0 -- 255");
/*      */     }
/* 1144 */     if (isClosed())
/* 1145 */       throw new SocketException("Socket is closed");
/* 1146 */     getImpl().setOption(3, new Integer(paramInt));
/*      */   }
/*      */ 
/*      */   public synchronized int getTrafficClass()
/*      */     throws SocketException
/*      */   {
/* 1166 */     if (isClosed())
/* 1167 */       throw new SocketException("Socket is closed");
/* 1168 */     return ((Integer)getImpl().getOption(3)).intValue();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/* 1184 */     synchronized (this.closeLock) {
/* 1185 */       if (isClosed())
/* 1186 */         return;
/* 1187 */       this.impl.close();
/* 1188 */       this.closed = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/* 1199 */     synchronized (this.closeLock) {
/* 1200 */       return this.closed;
/*      */     }
/*      */   }
/*      */ 
/*      */   public DatagramChannel getChannel()
/*      */   {
/* 1219 */     return null;
/*      */   }
/*      */ 
/*      */   public static synchronized void setDatagramSocketImplFactory(DatagramSocketImplFactory paramDatagramSocketImplFactory)
/*      */     throws IOException
/*      */   {
/* 1259 */     if (factory != null) {
/* 1260 */       throw new SocketException("factory already defined");
/*      */     }
/* 1262 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1263 */     if (localSecurityManager != null) {
/* 1264 */       localSecurityManager.checkSetFactory();
/*      */     }
/* 1266 */     factory = paramDatagramSocketImplFactory;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.DatagramSocket
 * JD-Core Version:    0.6.2
 */