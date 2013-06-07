/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.ProtocolFamily;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardProtocolFamily;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.nio.channels.UnresolvedAddressException;
/*     */ import java.nio.channels.UnsupportedAddressTypeException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ 
/*     */ class Net
/*     */ {
/*  41 */   static final ProtocolFamily UNSPEC = new ProtocolFamily() {
/*     */     public String name() {
/*  43 */       return "UNSPEC"; }  } ;
/*     */ 
/*  49 */   private static volatile boolean checkedIPv6 = false;
/*     */   private static volatile boolean isIPv6Available;
/*     */   public static final int SHUT_RD = 0;
/*     */   public static final int SHUT_WR = 1;
/*     */   public static final int SHUT_RDWR = 2;
/*     */ 
/*  56 */   static boolean isIPv6Available() { if (!checkedIPv6) {
/*  57 */       isIPv6Available = isIPv6Available0();
/*  58 */       checkedIPv6 = true;
/*     */     }
/*  60 */     return isIPv6Available;
/*     */   }
/*     */ 
/*     */   static boolean canIPv6SocketJoinIPv4Group()
/*     */   {
/*  67 */     return canIPv6SocketJoinIPv4Group0();
/*     */   }
/*     */ 
/*     */   static boolean canJoin6WithIPv4Group()
/*     */   {
/*  75 */     return canJoin6WithIPv4Group0();
/*     */   }
/*     */ 
/*     */   static InetSocketAddress checkAddress(SocketAddress paramSocketAddress) {
/*  79 */     if (paramSocketAddress == null)
/*  80 */       throw new NullPointerException();
/*  81 */     if (!(paramSocketAddress instanceof InetSocketAddress))
/*  82 */       throw new UnsupportedAddressTypeException();
/*  83 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/*  84 */     if (localInetSocketAddress.isUnresolved())
/*  85 */       throw new UnresolvedAddressException();
/*  86 */     InetAddress localInetAddress = localInetSocketAddress.getAddress();
/*  87 */     if ((!(localInetAddress instanceof Inet4Address)) && (!(localInetAddress instanceof Inet6Address)))
/*  88 */       throw new IllegalArgumentException("Invalid address type");
/*  89 */     return localInetSocketAddress;
/*     */   }
/*     */ 
/*     */   static InetSocketAddress asInetSocketAddress(SocketAddress paramSocketAddress) {
/*  93 */     if (!(paramSocketAddress instanceof InetSocketAddress))
/*  94 */       throw new UnsupportedAddressTypeException();
/*  95 */     return (InetSocketAddress)paramSocketAddress;
/*     */   }
/*     */ 
/*     */   static void translateToSocketException(Exception paramException)
/*     */     throws SocketException
/*     */   {
/* 101 */     if ((paramException instanceof SocketException))
/* 102 */       throw ((SocketException)paramException);
/* 103 */     Object localObject = paramException;
/* 104 */     if ((paramException instanceof ClosedChannelException))
/* 105 */       localObject = new SocketException("Socket is closed");
/* 106 */     else if ((paramException instanceof NotYetConnectedException))
/* 107 */       localObject = new SocketException("Socket is not connected");
/* 108 */     else if ((paramException instanceof AlreadyBoundException))
/* 109 */       localObject = new SocketException("Already bound");
/* 110 */     else if ((paramException instanceof NotYetBoundException))
/* 111 */       localObject = new SocketException("Socket is not bound yet");
/* 112 */     else if ((paramException instanceof UnsupportedAddressTypeException))
/* 113 */       localObject = new SocketException("Unsupported address type");
/* 114 */     else if ((paramException instanceof UnresolvedAddressException)) {
/* 115 */       localObject = new SocketException("Unresolved address");
/*     */     }
/* 117 */     if (localObject != paramException) {
/* 118 */       ((Exception)localObject).initCause(paramException);
/*     */     }
/* 120 */     if ((localObject instanceof SocketException))
/* 121 */       throw ((SocketException)localObject);
/* 122 */     if ((localObject instanceof RuntimeException)) {
/* 123 */       throw ((RuntimeException)localObject);
/*     */     }
/* 125 */     throw new Error("Untranslated exception", (Throwable)localObject);
/*     */   }
/*     */ 
/*     */   static void translateException(Exception paramException, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 132 */     if ((paramException instanceof IOException)) {
/* 133 */       throw ((IOException)paramException);
/*     */     }
/*     */ 
/* 136 */     if ((paramBoolean) && ((paramException instanceof UnresolvedAddressException)))
/*     */     {
/* 139 */       throw new UnknownHostException();
/*     */     }
/* 141 */     translateToSocketException(paramException);
/*     */   }
/*     */ 
/*     */   static void translateException(Exception paramException)
/*     */     throws IOException
/*     */   {
/* 147 */     translateException(paramException, false);
/*     */   }
/*     */ 
/*     */   static Inet4Address anyInet4Address(NetworkInterface paramNetworkInterface)
/*     */   {
/* 155 */     return (Inet4Address)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Inet4Address run() {
/* 157 */         Enumeration localEnumeration = this.val$interf.getInetAddresses();
/* 158 */         while (localEnumeration.hasMoreElements()) {
/* 159 */           InetAddress localInetAddress = (InetAddress)localEnumeration.nextElement();
/* 160 */           if ((localInetAddress instanceof Inet4Address)) {
/* 161 */             return (Inet4Address)localInetAddress;
/*     */           }
/*     */         }
/* 164 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static int inet4AsInt(InetAddress paramInetAddress)
/*     */   {
/* 173 */     if ((paramInetAddress instanceof Inet4Address)) {
/* 174 */       byte[] arrayOfByte = paramInetAddress.getAddress();
/* 175 */       int i = arrayOfByte[3] & 0xFF;
/* 176 */       i |= arrayOfByte[2] << 8 & 0xFF00;
/* 177 */       i |= arrayOfByte[1] << 16 & 0xFF0000;
/* 178 */       i |= arrayOfByte[0] << 24 & 0xFF000000;
/* 179 */       return i;
/*     */     }
/* 181 */     throw new AssertionError("Should not reach here");
/*     */   }
/*     */ 
/*     */   static InetAddress inet4FromInt(int paramInt)
/*     */   {
/* 189 */     byte[] arrayOfByte = new byte[4];
/* 190 */     arrayOfByte[0] = ((byte)(paramInt >>> 24 & 0xFF));
/* 191 */     arrayOfByte[1] = ((byte)(paramInt >>> 16 & 0xFF));
/* 192 */     arrayOfByte[2] = ((byte)(paramInt >>> 8 & 0xFF));
/* 193 */     arrayOfByte[3] = ((byte)(paramInt & 0xFF));
/*     */     try {
/* 195 */       return InetAddress.getByAddress(arrayOfByte); } catch (UnknownHostException localUnknownHostException) {
/*     */     }
/* 197 */     throw new AssertionError("Should not reach here");
/*     */   }
/*     */ 
/*     */   static byte[] inet6AsByteArray(InetAddress paramInetAddress)
/*     */   {
/* 205 */     if ((paramInetAddress instanceof Inet6Address)) {
/* 206 */       return paramInetAddress.getAddress();
/*     */     }
/*     */ 
/* 210 */     if ((paramInetAddress instanceof Inet4Address)) {
/* 211 */       byte[] arrayOfByte1 = paramInetAddress.getAddress();
/* 212 */       byte[] arrayOfByte2 = new byte[16];
/* 213 */       arrayOfByte2[10] = -1;
/* 214 */       arrayOfByte2[11] = -1;
/* 215 */       arrayOfByte2[12] = arrayOfByte1[0];
/* 216 */       arrayOfByte2[13] = arrayOfByte1[1];
/* 217 */       arrayOfByte2[14] = arrayOfByte1[2];
/* 218 */       arrayOfByte2[15] = arrayOfByte1[3];
/* 219 */       return arrayOfByte2;
/*     */     }
/*     */ 
/* 222 */     throw new AssertionError("Should not reach here");
/*     */   }
/*     */ 
/*     */   static void setSocketOption(FileDescriptor paramFileDescriptor, ProtocolFamily paramProtocolFamily, SocketOption<?> paramSocketOption, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 231 */     if (paramObject == null) {
/* 232 */       throw new IllegalArgumentException("Invalid option value");
/*     */     }
/*     */ 
/* 235 */     Class localClass = paramSocketOption.type();
/* 236 */     if ((localClass != Integer.class) && (localClass != Boolean.class))
/* 237 */       throw new AssertionError("Should not reach here");
/*     */     int i;
/* 240 */     if ((paramSocketOption == StandardSocketOptions.SO_RCVBUF) || (paramSocketOption == StandardSocketOptions.SO_SNDBUF))
/*     */     {
/* 243 */       i = ((Integer)paramObject).intValue();
/* 244 */       if (i < 0)
/* 245 */         throw new IllegalArgumentException("Invalid send/receive buffer size");
/*     */     }
/* 247 */     if (paramSocketOption == StandardSocketOptions.SO_LINGER) {
/* 248 */       i = ((Integer)paramObject).intValue();
/* 249 */       if (i < 0)
/* 250 */         paramObject = Integer.valueOf(-1);
/* 251 */       if (i > 65535)
/* 252 */         paramObject = Integer.valueOf(65535);
/*     */     }
/* 254 */     if (paramSocketOption == StandardSocketOptions.IP_TOS) {
/* 255 */       i = ((Integer)paramObject).intValue();
/* 256 */       if ((i < 0) || (i > 255))
/* 257 */         throw new IllegalArgumentException("Invalid IP_TOS value");
/*     */     }
/* 259 */     if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) {
/* 260 */       i = ((Integer)paramObject).intValue();
/* 261 */       if ((i < 0) || (i > 255)) {
/* 262 */         throw new IllegalArgumentException("Invalid TTL/hop value");
/*     */       }
/*     */     }
/*     */ 
/* 266 */     OptionKey localOptionKey = SocketOptionRegistry.findOption(paramSocketOption, paramProtocolFamily);
/* 267 */     if (localOptionKey == null)
/* 268 */       throw new AssertionError("Option not found");
/*     */     int j;
/* 271 */     if (localClass == Integer.class) {
/* 272 */       j = ((Integer)paramObject).intValue();
/*     */     } else {
/* 274 */       bool = ((Boolean)paramObject).booleanValue();
/* 275 */       j = bool ? 1 : 0;
/*     */     }
/*     */ 
/* 278 */     boolean bool = paramProtocolFamily == UNSPEC;
/* 279 */     setIntOption0(paramFileDescriptor, bool, localOptionKey.level(), localOptionKey.name(), j);
/*     */   }
/*     */ 
/*     */   static Object getSocketOption(FileDescriptor paramFileDescriptor, ProtocolFamily paramProtocolFamily, SocketOption<?> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 286 */     Class localClass = paramSocketOption.type();
/*     */ 
/* 289 */     if ((localClass != Integer.class) && (localClass != Boolean.class)) {
/* 290 */       throw new AssertionError("Should not reach here");
/*     */     }
/*     */ 
/* 293 */     OptionKey localOptionKey = SocketOptionRegistry.findOption(paramSocketOption, paramProtocolFamily);
/* 294 */     if (localOptionKey == null) {
/* 295 */       throw new AssertionError("Option not found");
/*     */     }
/* 297 */     boolean bool = paramProtocolFamily == UNSPEC;
/* 298 */     int i = getIntOption0(paramFileDescriptor, bool, localOptionKey.level(), localOptionKey.name());
/*     */ 
/* 300 */     if (localClass == Integer.class) {
/* 301 */       return Integer.valueOf(i);
/*     */     }
/* 303 */     return i == 0 ? Boolean.FALSE : Boolean.TRUE;
/*     */   }
/*     */ 
/*     */   private static native boolean isIPv6Available0();
/*     */ 
/*     */   private static native boolean canIPv6SocketJoinIPv4Group0();
/*     */ 
/*     */   private static native boolean canJoin6WithIPv4Group0();
/*     */ 
/*     */   static FileDescriptor socket(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 316 */     return socket(UNSPEC, paramBoolean);
/*     */   }
/*     */ 
/*     */   static FileDescriptor socket(ProtocolFamily paramProtocolFamily, boolean paramBoolean) throws IOException
/*     */   {
/* 321 */     boolean bool = (isIPv6Available()) && (paramProtocolFamily != StandardProtocolFamily.INET);
/*     */ 
/* 323 */     return IOUtil.newFD(socket0(bool, paramBoolean, false));
/*     */   }
/*     */ 
/*     */   static FileDescriptor serverSocket(boolean paramBoolean) {
/* 327 */     return IOUtil.newFD(socket0(isIPv6Available(), paramBoolean, true));
/*     */   }
/*     */ 
/*     */   private static native int socket0(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
/*     */ 
/*     */   static void bind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 336 */     bind(UNSPEC, paramFileDescriptor, paramInetAddress, paramInt);
/*     */   }
/*     */ 
/*     */   static void bind(ProtocolFamily paramProtocolFamily, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 342 */     boolean bool = (isIPv6Available()) && (paramProtocolFamily != StandardProtocolFamily.INET);
/*     */ 
/* 344 */     bind0(bool, paramFileDescriptor, paramInetAddress, paramInt);
/*     */   }
/*     */ 
/*     */   private static native void bind0(boolean paramBoolean, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native void listen(FileDescriptor paramFileDescriptor, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static int connect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 356 */     return connect(UNSPEC, paramFileDescriptor, paramInetAddress, paramInt);
/*     */   }
/*     */ 
/*     */   static int connect(ProtocolFamily paramProtocolFamily, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 362 */     boolean bool = (isIPv6Available()) && (paramProtocolFamily != StandardProtocolFamily.INET);
/*     */ 
/* 364 */     return connect0(bool, paramFileDescriptor, paramInetAddress, paramInt);
/*     */   }
/*     */ 
/*     */   private static native int connect0(boolean paramBoolean, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native void shutdown(FileDescriptor paramFileDescriptor, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private static native int localPort(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */ 
/*     */   private static native InetAddress localInetAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */ 
/*     */   static InetSocketAddress localAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException
/*     */   {
/* 389 */     return new InetSocketAddress(localInetAddress(paramFileDescriptor), localPort(paramFileDescriptor));
/*     */   }
/*     */ 
/*     */   private static native int remotePort(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */ 
/*     */   private static native InetAddress remoteInetAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */ 
/*     */   static InetSocketAddress remoteAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException
/*     */   {
/* 401 */     return new InetSocketAddress(remoteInetAddress(paramFileDescriptor), remotePort(paramFileDescriptor));
/*     */   }
/*     */ 
/*     */   private static native int getIntOption0(FileDescriptor paramFileDescriptor, boolean paramBoolean, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void setIntOption0(FileDescriptor paramFileDescriptor, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException;
/*     */ 
/*     */   static int join4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 421 */     return joinOrDrop4(true, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */ 
/*     */   static void drop4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 430 */     joinOrDrop4(false, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */ 
/*     */   private static native int joinOrDrop4(boolean paramBoolean, FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException;
/*     */ 
/*     */   static int block4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 442 */     return blockOrUnblock4(true, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */ 
/*     */   static void unblock4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 451 */     blockOrUnblock4(false, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */ 
/*     */   private static native int blockOrUnblock4(boolean paramBoolean, FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException;
/*     */ 
/*     */   static int join6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 464 */     return joinOrDrop6(true, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */ 
/*     */   static void drop6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 473 */     joinOrDrop6(false, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */ 
/*     */   private static native int joinOrDrop6(boolean paramBoolean, FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException;
/*     */ 
/*     */   static int block6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 485 */     return blockOrUnblock6(true, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */ 
/*     */   static void unblock6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 494 */     blockOrUnblock6(false, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */ 
/*     */   static native int blockOrUnblock6(boolean paramBoolean, FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) throws IOException;
/*     */ 
/*     */   static native void setInterface4(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;
/*     */ 
/*     */   static native int getInterface4(FileDescriptor paramFileDescriptor) throws IOException;
/*     */ 
/*     */   static native void setInterface6(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;
/*     */ 
/*     */   static native int getInterface6(FileDescriptor paramFileDescriptor) throws IOException;
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   static
/*     */   {
/* 511 */     Util.load();
/* 512 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.Net
 * JD-Core Version:    0.6.2
 */