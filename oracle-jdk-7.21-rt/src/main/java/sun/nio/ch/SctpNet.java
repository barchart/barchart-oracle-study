/*     */ package sun.nio.ch;
/*     */ 
/*     */ import com.sun.nio.sctp.SctpSocketOption;
/*     */ import com.sun.nio.sctp.SctpStandardSocketOptions;
/*     */ import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.security.AccessController;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class SctpNet
/*     */ {
/*  41 */   static final String osName = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*     */ 
/*     */   private static boolean IPv4MappedAddresses()
/*     */   {
/*  47 */     if ("SunOS".equals(osName))
/*     */     {
/*  49 */       return true;
/*     */     }
/*     */ 
/*  53 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean throwAlreadyBoundException() throws IOException {
/*  57 */     throw new AlreadyBoundException();
/*     */   }
/*     */ 
/*     */   static void listen(int paramInt1, int paramInt2) throws IOException {
/*  61 */     listen0(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   static int connect(int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException
/*     */   {
/*  66 */     return connect0(paramInt1, paramInetAddress, paramInt2);
/*     */   }
/*     */ 
/*     */   static void close(int paramInt) throws IOException {
/*  70 */     close0(paramInt);
/*     */   }
/*     */ 
/*     */   static void preClose(int paramInt) throws IOException {
/*  74 */     preClose0(paramInt);
/*     */   }
/*     */ 
/*     */   static FileDescriptor socket(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  83 */     int i = socket0(paramBoolean);
/*  84 */     return IOUtil.newFD(i);
/*     */   }
/*     */ 
/*     */   static void bindx(int paramInt1, InetAddress[] paramArrayOfInetAddress, int paramInt2, boolean paramBoolean) throws IOException
/*     */   {
/*  89 */     bindx(paramInt1, paramArrayOfInetAddress, paramInt2, paramArrayOfInetAddress.length, paramBoolean, IPv4MappedAddresses());
/*     */   }
/*     */ 
/*     */   static Set<SocketAddress> getLocalAddresses(int paramInt)
/*     */     throws IOException
/*     */   {
/*  95 */     HashSet localHashSet = null;
/*  96 */     SocketAddress[] arrayOfSocketAddress1 = getLocalAddresses0(paramInt);
/*     */ 
/*  98 */     if (arrayOfSocketAddress1 != null) {
/*  99 */       localHashSet = new HashSet(arrayOfSocketAddress1.length);
/* 100 */       for (SocketAddress localSocketAddress : arrayOfSocketAddress1) {
/* 101 */         localHashSet.add(localSocketAddress);
/*     */       }
/*     */     }
/* 104 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   static Set<SocketAddress> getRemoteAddresses(int paramInt1, int paramInt2) throws IOException
/*     */   {
/* 109 */     HashSet localHashSet = null;
/* 110 */     SocketAddress[] arrayOfSocketAddress1 = getRemoteAddresses0(paramInt1, paramInt2);
/*     */ 
/* 112 */     if (arrayOfSocketAddress1 != null) {
/* 113 */       localHashSet = new HashSet(arrayOfSocketAddress1.length);
/* 114 */       for (SocketAddress localSocketAddress : arrayOfSocketAddress1) {
/* 115 */         localHashSet.add(localSocketAddress);
/*     */       }
/*     */     }
/* 118 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   static void setSocketOption(int paramInt1, SctpSocketOption paramSctpSocketOption, Object paramObject, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 126 */     if (paramObject == null) {
/* 127 */       throw new IllegalArgumentException("Invalid option value");
/*     */     }
/* 129 */     Class localClass = paramSctpSocketOption.type();
/* 130 */     if (!localClass.isInstance(paramObject))
/* 131 */       throw new IllegalArgumentException("Invalid option value");
/*     */     Object localObject;
/* 133 */     if (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS)) {
/* 134 */       localObject = (SctpStandardSocketOptions.InitMaxStreams)paramObject;
/* 135 */       setInitMsgOption0(paramInt1, ((SctpStandardSocketOptions.InitMaxStreams)localObject).maxInStreams(), ((SctpStandardSocketOptions.InitMaxStreams)localObject).maxOutStreams());
/*     */     }
/* 137 */     else if ((paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR)))
/*     */     {
/* 140 */       localObject = (SocketAddress)paramObject;
/* 141 */       if (localObject == null) {
/* 142 */         throw new IllegalArgumentException("Invalid option value");
/*     */       }
/* 144 */       Net.checkAddress((SocketAddress)localObject);
/* 145 */       InetSocketAddress localInetSocketAddress = (InetSocketAddress)localObject;
/*     */ 
/* 147 */       if (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR)) {
/* 148 */         setPrimAddrOption0(paramInt1, paramInt2, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*     */       }
/*     */       else
/*     */       {
/* 153 */         setPeerPrimAddrOption0(paramInt1, paramInt2, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort(), IPv4MappedAddresses());
/*     */       }
/*     */ 
/*     */     }
/* 159 */     else if ((paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_DISABLE_FRAGMENTS)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_EXPLICIT_COMPLETE)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_NODELAY)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SO_SNDBUF)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SO_RCVBUF)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SO_LINGER)))
/*     */     {
/* 166 */       setIntOption(paramInt1, paramSctpSocketOption, paramObject);
/*     */     } else {
/* 168 */       throw new AssertionError("Unknown socket option");
/*     */     }
/*     */   }
/*     */ 
/*     */   static Object getSocketOption(int paramInt1, SctpSocketOption paramSctpSocketOption, int paramInt2) throws IOException
/*     */   {
/* 174 */     if (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_SET_PEER_PRIMARY_ADDR)) {
/* 175 */       throw new IllegalArgumentException("SCTP_SET_PEER_PRIMARY_ADDR cannot be retrieved");
/*     */     }
/* 177 */     if (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS))
/*     */     {
/* 179 */       int[] arrayOfInt = new int[2];
/* 180 */       getInitMsgOption0(paramInt1, arrayOfInt);
/* 181 */       return SctpStandardSocketOptions.InitMaxStreams.create(arrayOfInt[0], arrayOfInt[1]);
/* 182 */     }if (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR))
/* 183 */       return getPrimAddrOption0(paramInt1, paramInt2);
/* 184 */     if ((paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_DISABLE_FRAGMENTS)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_EXPLICIT_COMPLETE)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_NODELAY)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SO_SNDBUF)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SO_RCVBUF)) || (paramSctpSocketOption.equals(SctpStandardSocketOptions.SO_LINGER)))
/*     */     {
/* 191 */       return getIntOption(paramInt1, paramSctpSocketOption);
/*     */     }
/* 193 */     throw new AssertionError("Unknown socket option");
/*     */   }
/*     */ 
/*     */   static void setIntOption(int paramInt, SctpSocketOption paramSctpSocketOption, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 199 */     if (paramObject == null) {
/* 200 */       throw new IllegalArgumentException("Invalid option value");
/*     */     }
/* 202 */     Class localClass = paramSctpSocketOption.type();
/* 203 */     if ((localClass != Integer.class) && (localClass != Boolean.class))
/* 204 */       throw new AssertionError("Should not reach here");
/*     */     int i;
/* 206 */     if ((paramSctpSocketOption == SctpStandardSocketOptions.SO_RCVBUF) || (paramSctpSocketOption == SctpStandardSocketOptions.SO_SNDBUF))
/*     */     {
/* 209 */       i = ((Integer)paramObject).intValue();
/* 210 */       if (i < 0)
/* 211 */         throw new IllegalArgumentException("Invalid send/receive buffer size");
/*     */     }
/* 213 */     else if (paramSctpSocketOption == SctpStandardSocketOptions.SO_LINGER) {
/* 214 */       i = ((Integer)paramObject).intValue();
/* 215 */       if (i < 0)
/* 216 */         paramObject = Integer.valueOf(-1);
/* 217 */       if (i > 65535)
/* 218 */         paramObject = Integer.valueOf(65535);
/* 219 */     } else if (paramSctpSocketOption.equals(SctpStandardSocketOptions.SCTP_FRAGMENT_INTERLEAVE)) {
/* 220 */       i = ((Integer)paramObject).intValue();
/* 221 */       if ((i < 0) || (i > 2)) {
/* 222 */         throw new IllegalArgumentException("Invalid value for SCTP_FRAGMENT_INTERLEAVE");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 227 */     if (localClass == Integer.class) {
/* 228 */       i = ((Integer)paramObject).intValue();
/*     */     } else {
/* 230 */       boolean bool = ((Boolean)paramObject).booleanValue();
/* 231 */       i = bool ? 1 : 0;
/*     */     }
/*     */ 
/* 234 */     setIntOption0(paramInt, ((SctpStdSocketOption)paramSctpSocketOption).constValue(), i);
/*     */   }
/*     */ 
/*     */   static Object getIntOption(int paramInt, SctpSocketOption paramSctpSocketOption) throws IOException
/*     */   {
/* 239 */     Class localClass = paramSctpSocketOption.type();
/*     */ 
/* 241 */     if ((localClass != Integer.class) && (localClass != Boolean.class)) {
/* 242 */       throw new AssertionError("Should not reach here");
/*     */     }
/* 244 */     if (!(paramSctpSocketOption instanceof SctpStdSocketOption)) {
/* 245 */       throw new AssertionError("Should not reach here");
/*     */     }
/* 247 */     int i = getIntOption0(paramInt, ((SctpStdSocketOption)paramSctpSocketOption).constValue());
/*     */ 
/* 250 */     if (localClass == Integer.class) {
/* 251 */       return Integer.valueOf(i);
/*     */     }
/* 253 */     return i == 0 ? Boolean.FALSE : Boolean.TRUE;
/*     */   }
/*     */ 
/*     */   static void shutdown(int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 259 */     shutdown0(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   static FileDescriptor branch(int paramInt1, int paramInt2) throws IOException {
/* 263 */     int i = branch0(paramInt1, paramInt2);
/* 264 */     return IOUtil.newFD(i);
/*     */   }
/*     */ 
/*     */   static native int socket0(boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   static native void listen0(int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   static native int connect0(int paramInt1, InetAddress paramInetAddress, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   static native void close0(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native void preClose0(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native void bindx(int paramInt1, InetAddress[] paramArrayOfInetAddress, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
/*     */     throws IOException;
/*     */ 
/*     */   static native int getIntOption0(int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   static native void setIntOption0(int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException;
/*     */ 
/*     */   static native SocketAddress[] getLocalAddresses0(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native SocketAddress[] getRemoteAddresses0(int paramInt1, int paramInt2) throws IOException;
/*     */ 
/*     */   static native int branch0(int paramInt1, int paramInt2) throws IOException;
/*     */ 
/*     */   static native void setPrimAddrOption0(int paramInt1, int paramInt2, InetAddress paramInetAddress, int paramInt3) throws IOException;
/*     */ 
/*     */   static native void setPeerPrimAddrOption0(int paramInt1, int paramInt2, InetAddress paramInetAddress, int paramInt3, boolean paramBoolean) throws IOException;
/*     */ 
/*     */   static native SocketAddress getPrimAddrOption0(int paramInt1, int paramInt2) throws IOException;
/*     */ 
/*     */   static native void getInitMsgOption0(int paramInt, int[] paramArrayOfInt) throws IOException;
/*     */ 
/*     */   static native void setInitMsgOption0(int paramInt1, int paramInt2, int paramInt3) throws IOException;
/*     */ 
/*     */   static native void shutdown0(int paramInt1, int paramInt2);
/*     */ 
/*     */   static native void init();
/*     */ 
/*     */   static
/*     */   {
/* 314 */     init();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpNet
 * JD-Core Version:    0.6.2
 */