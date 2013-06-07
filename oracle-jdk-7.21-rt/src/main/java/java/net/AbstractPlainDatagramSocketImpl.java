/*     */ package java.net;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import sun.net.ResourceManager;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.action.LoadLibraryAction;
/*     */ 
/*     */ abstract class AbstractPlainDatagramSocketImpl extends DatagramSocketImpl
/*     */ {
/*  47 */   int timeout = 0;
/*  48 */   boolean connected = false;
/*  49 */   private int trafficClass = 0;
/*  50 */   private InetAddress connectedAddress = null;
/*  51 */   private int connectedPort = -1;
/*     */ 
/*  54 */   private int multicastInterface = 0;
/*  55 */   private boolean loopbackMode = true;
/*  56 */   private int ttl = -1;
/*     */ 
/*  58 */   private static final String os = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*     */ 
/*  65 */   private static final boolean connectDisabled = os.contains("OS X");
/*     */ 
/*     */   protected synchronized void create()
/*     */     throws SocketException
/*     */   {
/*  79 */     ResourceManager.beforeUdpCreate();
/*  80 */     this.fd = new FileDescriptor();
/*     */     try {
/*  82 */       datagramSocketCreate();
/*     */     } catch (SocketException localSocketException) {
/*  84 */       ResourceManager.afterUdpClose();
/*  85 */       this.fd = null;
/*  86 */       throw localSocketException;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void bind(int paramInt, InetAddress paramInetAddress)
/*     */     throws SocketException
/*     */   {
/*  95 */     bind0(paramInt, paramInetAddress);
/*     */   }
/*     */ 
/*     */   protected abstract void bind0(int paramInt, InetAddress paramInetAddress)
/*     */     throws SocketException;
/*     */ 
/*     */   protected abstract void send(DatagramPacket paramDatagramPacket)
/*     */     throws IOException;
/*     */ 
/*     */   protected void connect(InetAddress paramInetAddress, int paramInt)
/*     */     throws SocketException
/*     */   {
/* 116 */     connect0(paramInetAddress, paramInt);
/* 117 */     this.connectedAddress = paramInetAddress;
/* 118 */     this.connectedPort = paramInt;
/* 119 */     this.connected = true;
/*     */   }
/*     */ 
/*     */   protected void disconnect()
/*     */   {
/* 127 */     disconnect0(this.connectedAddress.holder().getFamily());
/* 128 */     this.connected = false;
/* 129 */     this.connectedAddress = null;
/* 130 */     this.connectedPort = -1;
/*     */   }
/*     */ 
/*     */   protected abstract int peek(InetAddress paramInetAddress)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract int peekData(DatagramPacket paramDatagramPacket)
/*     */     throws IOException;
/*     */ 
/*     */   protected synchronized void receive(DatagramPacket paramDatagramPacket)
/*     */     throws IOException
/*     */   {
/* 145 */     receive0(paramDatagramPacket);
/*     */   }
/*     */ 
/*     */   protected abstract void receive0(DatagramPacket paramDatagramPacket)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void setTimeToLive(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract int getTimeToLive()
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract void setTTL(byte paramByte)
/*     */     throws IOException;
/*     */ 
/*     */   protected abstract byte getTTL()
/*     */     throws IOException;
/*     */ 
/*     */   protected void join(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/* 178 */     join(paramInetAddress, null);
/*     */   }
/*     */ 
/*     */   protected void leave(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/* 186 */     leave(paramInetAddress, null);
/*     */   }
/*     */ 
/*     */   protected void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
/*     */     throws IOException
/*     */   {
/* 200 */     if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress)))
/* 201 */       throw new IllegalArgumentException("Unsupported address type");
/* 202 */     join(((InetSocketAddress)paramSocketAddress).getAddress(), paramNetworkInterface);
/*     */   }
/*     */ 
/*     */   protected abstract void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
/*     */     throws IOException;
/*     */ 
/*     */   protected void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
/*     */     throws IOException
/*     */   {
/* 218 */     if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress)))
/* 219 */       throw new IllegalArgumentException("Unsupported address type");
/* 220 */     leave(((InetSocketAddress)paramSocketAddress).getAddress(), paramNetworkInterface);
/*     */   }
/*     */ 
/*     */   protected abstract void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
/*     */     throws IOException;
/*     */ 
/*     */   protected void close()
/*     */   {
/* 230 */     if (this.fd != null) {
/* 231 */       datagramSocketClose();
/* 232 */       ResourceManager.afterUdpClose();
/* 233 */       this.fd = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isClosed() {
/* 238 */     return this.fd == null;
/*     */   }
/*     */ 
/*     */   protected void finalize() {
/* 242 */     close();
/*     */   }
/*     */ 
/*     */   public void setOption(int paramInt, Object paramObject)
/*     */     throws SocketException
/*     */   {
/* 251 */     if (isClosed()) {
/* 252 */       throw new SocketException("Socket Closed");
/*     */     }
/* 254 */     switch (paramInt)
/*     */     {
/*     */     case 4102:
/* 260 */       if ((paramObject == null) || (!(paramObject instanceof Integer))) {
/* 261 */         throw new SocketException("bad argument for SO_TIMEOUT");
/*     */       }
/* 263 */       int i = ((Integer)paramObject).intValue();
/* 264 */       if (i < 0)
/* 265 */         throw new IllegalArgumentException("timeout < 0");
/* 266 */       this.timeout = i;
/* 267 */       return;
/*     */     case 3:
/* 269 */       if ((paramObject == null) || (!(paramObject instanceof Integer))) {
/* 270 */         throw new SocketException("bad argument for IP_TOS");
/*     */       }
/* 272 */       this.trafficClass = ((Integer)paramObject).intValue();
/* 273 */       break;
/*     */     case 4:
/* 275 */       if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
/* 276 */         throw new SocketException("bad argument for SO_REUSEADDR");
/*     */       }
/*     */       break;
/*     */     case 32:
/* 280 */       if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
/* 281 */         throw new SocketException("bad argument for SO_BROADCAST");
/*     */       }
/*     */       break;
/*     */     case 15:
/* 285 */       throw new SocketException("Cannot re-bind Socket");
/*     */     case 4097:
/*     */     case 4098:
/* 288 */       if ((paramObject == null) || (!(paramObject instanceof Integer)) || (((Integer)paramObject).intValue() < 0))
/*     */       {
/* 290 */         throw new SocketException("bad argument for SO_SNDBUF or SO_RCVBUF");
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 16:
/* 295 */       if ((paramObject == null) || (!(paramObject instanceof InetAddress)))
/* 296 */         throw new SocketException("bad argument for IP_MULTICAST_IF");
/*     */       break;
/*     */     case 31:
/* 299 */       if ((paramObject == null) || (!(paramObject instanceof NetworkInterface)))
/* 300 */         throw new SocketException("bad argument for IP_MULTICAST_IF2");
/*     */       break;
/*     */     case 18:
/* 303 */       if ((paramObject == null) || (!(paramObject instanceof Boolean)))
/* 304 */         throw new SocketException("bad argument for IP_MULTICAST_LOOP");
/*     */       break;
/*     */     default:
/* 307 */       throw new SocketException("invalid option: " + paramInt);
/*     */     }
/* 309 */     socketSetOption(paramInt, paramObject);
/*     */   }
/*     */ 
/*     */   public Object getOption(int paramInt)
/*     */     throws SocketException
/*     */   {
/* 317 */     if (isClosed())
/* 318 */       throw new SocketException("Socket Closed");
/*     */     Object localObject;
/* 323 */     switch (paramInt) {
/*     */     case 4102:
/* 325 */       localObject = new Integer(this.timeout);
/* 326 */       break;
/*     */     case 3:
/* 329 */       localObject = socketGetOption(paramInt);
/* 330 */       if (((Integer)localObject).intValue() == -1)
/* 331 */         localObject = new Integer(this.trafficClass); break;
/*     */     case 4:
/*     */     case 15:
/*     */     case 16:
/*     */     case 18:
/*     */     case 31:
/*     */     case 32:
/*     */     case 4097:
/*     */     case 4098:
/* 343 */       localObject = socketGetOption(paramInt);
/* 344 */       break;
/*     */     default:
/* 347 */       throw new SocketException("invalid option: " + paramInt);
/*     */     }
/*     */ 
/* 350 */     return localObject; } 
/*     */   protected abstract void datagramSocketCreate() throws SocketException;
/*     */ 
/*     */   protected abstract void datagramSocketClose();
/*     */ 
/*     */   protected abstract void socketSetOption(int paramInt, Object paramObject) throws SocketException;
/*     */ 
/*     */   protected abstract Object socketGetOption(int paramInt) throws SocketException;
/*     */ 
/*     */   protected abstract void connect0(InetAddress paramInetAddress, int paramInt) throws SocketException;
/*     */ 
/*     */   protected abstract void disconnect0(int paramInt);
/*     */ 
/* 363 */   protected boolean nativeConnectDisabled() { return connectDisabled; }
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  71 */     AccessController.doPrivileged(new LoadLibraryAction("net"));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.AbstractPlainDatagramSocketImpl
 * JD-Core Version:    0.6.2
 */