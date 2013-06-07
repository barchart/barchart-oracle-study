/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ 
/*     */ class InheritedChannel
/*     */ {
/*     */   private static final int UNKNOWN = -1;
/*     */   private static final int SOCK_STREAM = 1;
/*     */   private static final int SOCK_DGRAM = 2;
/*     */   private static final int O_RDONLY = 0;
/*     */   private static final int O_WRONLY = 1;
/*     */   private static final int O_RDWR = 2;
/*     */   private static int devnull;
/*     */   private static boolean haveChannel;
/*     */   private static Channel channel;
/*     */ 
/*     */   private static void detachIOStreams()
/*     */   {
/*     */     try
/*     */     {
/*  62 */       dup2(devnull, 0);
/*  63 */       dup2(devnull, 1);
/*  64 */       dup2(devnull, 2);
/*     */     }
/*     */     catch (IOException localIOException) {
/*  67 */       throw new InternalError();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void checkAccess(Channel paramChannel)
/*     */   {
/* 130 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 131 */     if (localSecurityManager != null)
/* 132 */       localSecurityManager.checkPermission(new RuntimePermission("inheritedChannel"));
/*     */   }
/*     */ 
/*     */   private static Channel createChannel()
/*     */     throws IOException
/*     */   {
/* 151 */     int i = dup(0);
/*     */ 
/* 157 */     int j = soType0(i);
/* 158 */     if ((j != 1) && (j != 2)) {
/* 159 */       close0(i);
/* 160 */       return null;
/*     */     }
/*     */ 
/* 168 */     Class[] arrayOfClass = { Integer.TYPE };
/* 169 */     Constructor localConstructor = Reflect.lookupConstructor("java.io.FileDescriptor", arrayOfClass);
/*     */ 
/* 171 */     Object[] arrayOfObject = { new Integer(i) };
/* 172 */     FileDescriptor localFileDescriptor = (FileDescriptor)Reflect.invoke(localConstructor, arrayOfObject);
/*     */ 
/* 180 */     SelectorProvider localSelectorProvider = SelectorProvider.provider();
/* 181 */     assert ((localSelectorProvider instanceof SelectorProviderImpl));
/*     */     Object localObject;
/* 184 */     if (j == 1) {
/* 185 */       InetAddress localInetAddress = peerAddress0(i);
/* 186 */       if (localInetAddress == null) {
/* 187 */         localObject = new InheritedServerSocketChannelImpl(localSelectorProvider, localFileDescriptor);
/*     */       } else {
/* 189 */         int k = peerPort0(i);
/* 190 */         assert (k > 0);
/* 191 */         InetSocketAddress localInetSocketAddress = new InetSocketAddress(localInetAddress, k);
/* 192 */         localObject = new InheritedSocketChannelImpl(localSelectorProvider, localFileDescriptor, localInetSocketAddress);
/*     */       }
/*     */     } else {
/* 195 */       localObject = new InheritedDatagramChannelImpl(localSelectorProvider, localFileDescriptor);
/*     */     }
/* 197 */     return localObject;
/*     */   }
/*     */ 
/*     */   public static synchronized Channel getChannel()
/*     */     throws IOException
/*     */   {
/* 208 */     if (devnull < 0) {
/* 209 */       devnull = open0("/dev/null", 2);
/*     */     }
/*     */ 
/* 213 */     if (!haveChannel) {
/* 214 */       channel = createChannel();
/* 215 */       haveChannel = true;
/*     */     }
/*     */ 
/* 220 */     if (channel != null) {
/* 221 */       checkAccess(channel);
/*     */     }
/* 223 */     return channel;
/*     */   }
/*     */ 
/*     */   private static native int dup(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void dup2(int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   private static native int open0(String paramString, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private static native void close0(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private static native int soType0(int paramInt);
/*     */ 
/*     */   private static native InetAddress peerAddress0(int paramInt);
/*     */ 
/*     */   private static native int peerPort0(int paramInt);
/*     */ 
/*     */   static
/*     */   {
/*  58 */     devnull = -1;
/*     */ 
/* 200 */     haveChannel = false;
/* 201 */     channel = null;
/*     */ 
/* 238 */     Util.load();
/*     */   }
/*     */ 
/*     */   public static class InheritedDatagramChannelImpl extends DatagramChannelImpl
/*     */   {
/*     */     InheritedDatagramChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor)
/*     */       throws IOException
/*     */     {
/* 116 */       super(paramFileDescriptor);
/*     */     }
/*     */ 
/*     */     protected void implCloseSelectableChannel() throws IOException {
/* 120 */       super.implCloseSelectableChannel();
/* 121 */       InheritedChannel.access$000();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class InheritedServerSocketChannelImpl extends ServerSocketChannelImpl
/*     */   {
/*     */     InheritedServerSocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor)
/*     */       throws IOException
/*     */     {
/*  99 */       super(paramFileDescriptor, true);
/*     */     }
/*     */ 
/*     */     protected void implCloseSelectableChannel() throws IOException {
/* 103 */       super.implCloseSelectableChannel();
/* 104 */       InheritedChannel.access$000();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class InheritedSocketChannelImpl extends SocketChannelImpl
/*     */   {
/*     */     InheritedSocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
/*     */       throws IOException
/*     */     {
/*  83 */       super(paramFileDescriptor, paramInetSocketAddress);
/*     */     }
/*     */ 
/*     */     protected void implCloseSelectableChannel() throws IOException {
/*  87 */       super.implCloseSelectableChannel();
/*  88 */       InheritedChannel.access$000();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.InheritedChannel
 * JD-Core Version:    0.6.2
 */