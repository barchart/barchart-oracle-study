/*      */ package java.net;
/*      */ 
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import sun.net.SocksProxy;
/*      */ import sun.net.www.ParseUtil;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ class SocksSocketImpl extends PlainSocketImpl
/*      */   implements SocksConsts
/*      */ {
/*   43 */   private String server = null;
/*   44 */   private int serverPort = 1080;
/*      */   private InetSocketAddress external_address;
/*   46 */   private boolean useV4 = false;
/*   47 */   private Socket cmdsock = null;
/*   48 */   private InputStream cmdIn = null;
/*   49 */   private OutputStream cmdOut = null;
/*      */   private boolean applicationSetProxy;
/*      */ 
/*      */   SocksSocketImpl()
/*      */   {
/*      */   }
/*      */ 
/*      */   SocksSocketImpl(String paramString, int paramInt)
/*      */   {
/*   59 */     this.server = paramString;
/*   60 */     this.serverPort = (paramInt == -1 ? 1080 : paramInt);
/*      */   }
/*      */ 
/*      */   SocksSocketImpl(Proxy paramProxy) {
/*   64 */     SocketAddress localSocketAddress = paramProxy.address();
/*   65 */     if ((localSocketAddress instanceof InetSocketAddress)) {
/*   66 */       InetSocketAddress localInetSocketAddress = (InetSocketAddress)localSocketAddress;
/*      */ 
/*   68 */       this.server = localInetSocketAddress.getHostString();
/*   69 */       this.serverPort = localInetSocketAddress.getPort();
/*      */     }
/*      */   }
/*      */ 
/*      */   void setV4() {
/*   74 */     this.useV4 = true;
/*      */   }
/*      */ 
/*      */   private synchronized void privilegedConnect(final String paramString, final int paramInt1, final int paramInt2)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*   83 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Void run() throws IOException {
/*   86 */           SocksSocketImpl.this.superConnectServer(paramString, paramInt1, paramInt2);
/*   87 */           SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.getInputStream();
/*   88 */           SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.getOutputStream();
/*   89 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*   93 */       throw ((IOException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void superConnectServer(String paramString, int paramInt1, int paramInt2) throws IOException
/*      */   {
/*   99 */     super.connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
/*      */   }
/*      */ 
/*      */   private static int remainingMillis(long paramLong) throws IOException {
/*  103 */     if (paramLong == 0L) {
/*  104 */       return 0;
/*      */     }
/*  106 */     long l = paramLong - System.currentTimeMillis();
/*  107 */     if (l > 0L) {
/*  108 */       return (int)l;
/*      */     }
/*  110 */     throw new SocketTimeoutException();
/*      */   }
/*      */ 
/*      */   private int readSocksReply(InputStream paramInputStream, byte[] paramArrayOfByte) throws IOException {
/*  114 */     return readSocksReply(paramInputStream, paramArrayOfByte, 0L);
/*      */   }
/*      */ 
/*      */   private int readSocksReply(InputStream paramInputStream, byte[] paramArrayOfByte, long paramLong) throws IOException {
/*  118 */     int i = paramArrayOfByte.length;
/*  119 */     int j = 0;
/*  120 */     for (int k = 0; (j < i) && (k < 3); k++) {
/*      */       int m;
/*      */       try {
/*  123 */         m = ((SocketInputStream)paramInputStream).read(paramArrayOfByte, j, i - j, remainingMillis(paramLong));
/*      */       } catch (SocketTimeoutException localSocketTimeoutException) {
/*  125 */         throw new SocketTimeoutException("Connect timed out");
/*      */       }
/*  127 */       if (m < 0)
/*  128 */         throw new SocketException("Malformed reply from SOCKS server");
/*  129 */       j += m;
/*      */     }
/*  131 */     return j;
/*      */   }
/*      */ 
/*      */   private boolean authenticate(byte paramByte, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream)
/*      */     throws IOException
/*      */   {
/*  139 */     return authenticate(paramByte, paramInputStream, paramBufferedOutputStream, 0L);
/*      */   }
/*      */ 
/*      */   private boolean authenticate(byte paramByte, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, long paramLong)
/*      */     throws IOException
/*      */   {
/*  146 */     if (paramByte == 0) {
/*  147 */       return true;
/*      */     }
/*      */ 
/*  153 */     if (paramByte == 2)
/*      */     {
/*  155 */       String str2 = null;
/*  156 */       final InetAddress localInetAddress = InetAddress.getByName(this.server);
/*  157 */       PasswordAuthentication localPasswordAuthentication = (PasswordAuthentication)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public PasswordAuthentication run()
/*      */         {
/*  161 */           return Authenticator.requestPasswordAuthentication(SocksSocketImpl.this.server, localInetAddress, SocksSocketImpl.this.serverPort, "SOCKS5", "SOCKS authentication", null);
/*      */         }
/*      */       });
/*      */       String str1;
/*  165 */       if (localPasswordAuthentication != null) {
/*  166 */         str1 = localPasswordAuthentication.getUserName();
/*  167 */         str2 = new String(localPasswordAuthentication.getPassword());
/*      */       } else {
/*  169 */         str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
/*      */       }
/*      */ 
/*  172 */       if (str1 == null)
/*  173 */         return false;
/*  174 */       paramBufferedOutputStream.write(1);
/*  175 */       paramBufferedOutputStream.write(str1.length());
/*      */       try {
/*  177 */         paramBufferedOutputStream.write(str1.getBytes("ISO-8859-1"));
/*      */       } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/*  179 */         if (!$assertionsDisabled) throw new AssertionError();
/*      */       }
/*  181 */       if (str2 != null) {
/*  182 */         paramBufferedOutputStream.write(str2.length());
/*      */         try {
/*  184 */           paramBufferedOutputStream.write(str2.getBytes("ISO-8859-1"));
/*      */         } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/*  186 */           if (!$assertionsDisabled) throw new AssertionError(); 
/*      */         }
/*      */       }
/*  189 */       else { paramBufferedOutputStream.write(0); }
/*  190 */       paramBufferedOutputStream.flush();
/*  191 */       byte[] arrayOfByte = new byte[2];
/*  192 */       int i = readSocksReply(paramInputStream, arrayOfByte, paramLong);
/*  193 */       if ((i != 2) || (arrayOfByte[1] != 0))
/*      */       {
/*  196 */         paramBufferedOutputStream.close();
/*  197 */         paramInputStream.close();
/*  198 */         return false;
/*      */       }
/*      */ 
/*  201 */       return true;
/*      */     }
/*      */ 
/*  257 */     return false;
/*      */   }
/*      */ 
/*      */   private void connectV4(InputStream paramInputStream, OutputStream paramOutputStream, InetSocketAddress paramInetSocketAddress, long paramLong)
/*      */     throws IOException
/*      */   {
/*  263 */     if (!(paramInetSocketAddress.getAddress() instanceof Inet4Address)) {
/*  264 */       throw new SocketException("SOCKS V4 requires IPv4 only addresses");
/*      */     }
/*  266 */     paramOutputStream.write(4);
/*  267 */     paramOutputStream.write(1);
/*  268 */     paramOutputStream.write(paramInetSocketAddress.getPort() >> 8 & 0xFF);
/*  269 */     paramOutputStream.write(paramInetSocketAddress.getPort() >> 0 & 0xFF);
/*  270 */     paramOutputStream.write(paramInetSocketAddress.getAddress().getAddress());
/*  271 */     String str = getUserName();
/*      */     try {
/*  273 */       paramOutputStream.write(str.getBytes("ISO-8859-1"));
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  275 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */     }
/*  277 */     paramOutputStream.write(0);
/*  278 */     paramOutputStream.flush();
/*  279 */     byte[] arrayOfByte = new byte[8];
/*  280 */     int i = readSocksReply(paramInputStream, arrayOfByte, paramLong);
/*  281 */     if (i != 8)
/*  282 */       throw new SocketException("Reply from SOCKS server has bad length: " + i);
/*  283 */     if ((arrayOfByte[0] != 0) && (arrayOfByte[0] != 4))
/*  284 */       throw new SocketException("Reply from SOCKS server has bad version");
/*  285 */     SocketException localSocketException = null;
/*  286 */     switch (arrayOfByte[1])
/*      */     {
/*      */     case 90:
/*  289 */       this.external_address = paramInetSocketAddress;
/*  290 */       break;
/*      */     case 91:
/*  292 */       localSocketException = new SocketException("SOCKS request rejected");
/*  293 */       break;
/*      */     case 92:
/*  295 */       localSocketException = new SocketException("SOCKS server couldn't reach destination");
/*  296 */       break;
/*      */     case 93:
/*  298 */       localSocketException = new SocketException("SOCKS authentication failed");
/*  299 */       break;
/*      */     default:
/*  301 */       localSocketException = new SocketException("Reply from SOCKS server contains bad status");
/*      */     }
/*      */ 
/*  304 */     if (localSocketException != null) {
/*  305 */       paramInputStream.close();
/*  306 */       paramOutputStream.close();
/*  307 */       throw localSocketException;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void connect(SocketAddress paramSocketAddress, int paramInt)
/*      */     throws IOException
/*      */   {
/*      */     long l1;
/*  329 */     if (paramInt == 0) {
/*  330 */       l1 = 0L;
/*      */     } else {
/*  332 */       long l2 = System.currentTimeMillis() + paramInt;
/*  333 */       l1 = l2 < 0L ? 9223372036854775807L : l2;
/*      */     }
/*      */ 
/*  336 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  337 */     if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress)))
/*  338 */       throw new IllegalArgumentException("Unsupported address type");
/*  339 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/*  340 */     if (localSecurityManager != null) {
/*  341 */       if (localInetSocketAddress.isUnresolved()) {
/*  342 */         localSecurityManager.checkConnect(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
/*      */       }
/*      */       else {
/*  345 */         localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
/*      */       }
/*      */     }
/*  348 */     if (this.server == null)
/*      */     {
/*  352 */       ProxySelector localProxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public ProxySelector run() {
/*  355 */           return ProxySelector.getDefault();
/*      */         }
/*      */       });
/*  358 */       if (localProxySelector == null)
/*      */       {
/*  362 */         super.connect(localInetSocketAddress, remainingMillis(l1));
/*  363 */         return;
/*      */       }
/*      */ 
/*  367 */       localObject2 = localInetSocketAddress.getHostString();
/*      */ 
/*  369 */       if (((localInetSocketAddress.getAddress() instanceof Inet6Address)) && (!((String)localObject2).startsWith("[")) && (((String)localObject2).indexOf(":") >= 0))
/*      */       {
/*  371 */         localObject2 = "[" + (String)localObject2 + "]";
/*      */       }
/*      */       try {
/*  374 */         localObject1 = new URI("socket://" + ParseUtil.encodePath((String)localObject2) + ":" + localInetSocketAddress.getPort());
/*      */       }
/*      */       catch (URISyntaxException localURISyntaxException) {
/*  377 */         if (!$assertionsDisabled) throw new AssertionError(localURISyntaxException);
/*  378 */         localObject1 = null;
/*      */       }
/*  380 */       Proxy localProxy = null;
/*  381 */       Object localObject3 = null;
/*  382 */       Iterator localIterator = null;
/*  383 */       localIterator = localProxySelector.select((URI)localObject1).iterator();
/*  384 */       if ((localIterator == null) || (!localIterator.hasNext())) {
/*  385 */         super.connect(localInetSocketAddress, remainingMillis(l1));
/*  386 */         return;
/*      */       }
/*  388 */       while (localIterator.hasNext()) {
/*  389 */         localProxy = (Proxy)localIterator.next();
/*  390 */         if ((localProxy == null) || (localProxy == Proxy.NO_PROXY)) {
/*  391 */           super.connect(localInetSocketAddress, remainingMillis(l1));
/*  392 */           return;
/*      */         }
/*  394 */         if (localProxy.type() != Proxy.Type.SOCKS)
/*  395 */           throw new SocketException("Unknown proxy type : " + localProxy.type());
/*  396 */         if (!(localProxy.address() instanceof InetSocketAddress)) {
/*  397 */           throw new SocketException("Unknow address type for proxy: " + localProxy);
/*      */         }
/*  399 */         this.server = ((InetSocketAddress)localProxy.address()).getHostString();
/*  400 */         this.serverPort = ((InetSocketAddress)localProxy.address()).getPort();
/*  401 */         if (((localProxy instanceof SocksProxy)) && 
/*  402 */           (((SocksProxy)localProxy).protocolVersion() == 4)) {
/*  403 */           this.useV4 = true;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  409 */           privilegedConnect(this.server, this.serverPort, remainingMillis(l1));
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/*  414 */           localProxySelector.connectFailed((URI)localObject1, localProxy.address(), localIOException2);
/*  415 */           this.server = null;
/*  416 */           this.serverPort = -1;
/*  417 */           localObject3 = localIOException2;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  426 */       if (this.server == null)
/*  427 */         throw new SocketException("Can't connect to SOCKS proxy:" + localObject3.getMessage());
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/*  433 */         privilegedConnect(this.server, this.serverPort, remainingMillis(l1));
/*      */       } catch (IOException localIOException1) {
/*  435 */         throw new SocketException(localIOException1.getMessage());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  440 */     BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(this.cmdOut, 512);
/*  441 */     Object localObject1 = this.cmdIn;
/*      */ 
/*  443 */     if (this.useV4)
/*      */     {
/*  446 */       if (localInetSocketAddress.isUnresolved())
/*  447 */         throw new UnknownHostException(localInetSocketAddress.toString());
/*  448 */       connectV4((InputStream)localObject1, localBufferedOutputStream, localInetSocketAddress, l1);
/*  449 */       return;
/*      */     }
/*      */ 
/*  453 */     localBufferedOutputStream.write(5);
/*  454 */     localBufferedOutputStream.write(2);
/*  455 */     localBufferedOutputStream.write(0);
/*  456 */     localBufferedOutputStream.write(2);
/*  457 */     localBufferedOutputStream.flush();
/*  458 */     Object localObject2 = new byte[2];
/*  459 */     int i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
/*  460 */     if ((i != 2) || (localObject2[0] != 5))
/*      */     {
/*  465 */       if (localInetSocketAddress.isUnresolved())
/*  466 */         throw new UnknownHostException(localInetSocketAddress.toString());
/*  467 */       connectV4((InputStream)localObject1, localBufferedOutputStream, localInetSocketAddress, l1);
/*  468 */       return;
/*      */     }
/*  470 */     if (localObject2[1] == -1)
/*  471 */       throw new SocketException("SOCKS : No acceptable methods");
/*  472 */     if (!authenticate(localObject2[1], (InputStream)localObject1, localBufferedOutputStream, l1)) {
/*  473 */       throw new SocketException("SOCKS : authentication failed");
/*      */     }
/*  475 */     localBufferedOutputStream.write(5);
/*  476 */     localBufferedOutputStream.write(1);
/*  477 */     localBufferedOutputStream.write(0);
/*      */ 
/*  479 */     if (localInetSocketAddress.isUnresolved()) {
/*  480 */       localBufferedOutputStream.write(3);
/*  481 */       localBufferedOutputStream.write(localInetSocketAddress.getHostName().length());
/*      */       try {
/*  483 */         localBufferedOutputStream.write(localInetSocketAddress.getHostName().getBytes("ISO-8859-1"));
/*      */       } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  485 */         if (!$assertionsDisabled) throw new AssertionError();
/*      */       }
/*  487 */       localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 8 & 0xFF);
/*  488 */       localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 0 & 0xFF);
/*  489 */     } else if ((localInetSocketAddress.getAddress() instanceof Inet6Address)) {
/*  490 */       localBufferedOutputStream.write(4);
/*  491 */       localBufferedOutputStream.write(localInetSocketAddress.getAddress().getAddress());
/*  492 */       localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 8 & 0xFF);
/*  493 */       localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 0 & 0xFF);
/*      */     } else {
/*  495 */       localBufferedOutputStream.write(1);
/*  496 */       localBufferedOutputStream.write(localInetSocketAddress.getAddress().getAddress());
/*  497 */       localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 8 & 0xFF);
/*  498 */       localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 0 & 0xFF);
/*      */     }
/*  500 */     localBufferedOutputStream.flush();
/*  501 */     localObject2 = new byte[4];
/*  502 */     i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
/*  503 */     if (i != 4)
/*  504 */       throw new SocketException("Reply from SOCKS server has bad length");
/*  505 */     SocketException localSocketException = null;
/*      */ 
/*  508 */     switch (localObject2[1])
/*      */     {
/*      */     case 0:
/*      */       byte[] arrayOfByte1;
/*      */       int j;
/*  511 */       switch (localObject2[3]) {
/*      */       case 1:
/*  513 */         arrayOfByte1 = new byte[4];
/*  514 */         i = readSocksReply((InputStream)localObject1, arrayOfByte1, l1);
/*  515 */         if (i != 4)
/*  516 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  517 */         localObject2 = new byte[2];
/*  518 */         i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
/*  519 */         if (i != 2)
/*  520 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*      */         break;
/*      */       case 3:
/*  523 */         j = localObject2[1];
/*  524 */         byte[] arrayOfByte2 = new byte[j];
/*  525 */         i = readSocksReply((InputStream)localObject1, arrayOfByte2, l1);
/*  526 */         if (i != j)
/*  527 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  528 */         localObject2 = new byte[2];
/*  529 */         i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
/*  530 */         if (i != 2)
/*  531 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*      */         break;
/*      */       case 4:
/*  534 */         j = localObject2[1];
/*  535 */         arrayOfByte1 = new byte[j];
/*  536 */         i = readSocksReply((InputStream)localObject1, arrayOfByte1, l1);
/*  537 */         if (i != j)
/*  538 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  539 */         localObject2 = new byte[2];
/*  540 */         i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
/*  541 */         if (i != 2)
/*  542 */           throw new SocketException("Reply from SOCKS server badly formatted"); break;
/*      */       case 2:
/*      */       default:
/*  545 */         localSocketException = new SocketException("Reply from SOCKS server contains wrong code");
/*      */       }
/*      */ 
/*  548 */       break;
/*      */     case 1:
/*  550 */       localSocketException = new SocketException("SOCKS server general failure");
/*  551 */       break;
/*      */     case 2:
/*  553 */       localSocketException = new SocketException("SOCKS: Connection not allowed by ruleset");
/*  554 */       break;
/*      */     case 3:
/*  556 */       localSocketException = new SocketException("SOCKS: Network unreachable");
/*  557 */       break;
/*      */     case 4:
/*  559 */       localSocketException = new SocketException("SOCKS: Host unreachable");
/*  560 */       break;
/*      */     case 5:
/*  562 */       localSocketException = new SocketException("SOCKS: Connection refused");
/*  563 */       break;
/*      */     case 6:
/*  565 */       localSocketException = new SocketException("SOCKS: TTL expired");
/*  566 */       break;
/*      */     case 7:
/*  568 */       localSocketException = new SocketException("SOCKS: Command not supported");
/*  569 */       break;
/*      */     case 8:
/*  571 */       localSocketException = new SocketException("SOCKS: address type not supported");
/*      */     }
/*      */ 
/*  574 */     if (localSocketException != null) {
/*  575 */       ((InputStream)localObject1).close();
/*  576 */       localBufferedOutputStream.close();
/*  577 */       throw localSocketException;
/*      */     }
/*  579 */     this.external_address = localInetSocketAddress;
/*      */   }
/*      */ 
/*      */   private void bindV4(InputStream paramInputStream, OutputStream paramOutputStream, InetAddress paramInetAddress, int paramInt)
/*      */     throws IOException
/*      */   {
/*  585 */     if (!(paramInetAddress instanceof Inet4Address)) {
/*  586 */       throw new SocketException("SOCKS V4 requires IPv4 only addresses");
/*      */     }
/*  588 */     super.bind(paramInetAddress, paramInt);
/*  589 */     byte[] arrayOfByte1 = paramInetAddress.getAddress();
/*      */ 
/*  591 */     InetAddress localInetAddress = paramInetAddress;
/*  592 */     if (localInetAddress.isAnyLocalAddress()) {
/*  593 */       localInetAddress = this.cmdsock.getLocalAddress();
/*  594 */       arrayOfByte1 = localInetAddress.getAddress();
/*      */     }
/*  596 */     paramOutputStream.write(4);
/*  597 */     paramOutputStream.write(2);
/*  598 */     paramOutputStream.write(super.getLocalPort() >> 8 & 0xFF);
/*  599 */     paramOutputStream.write(super.getLocalPort() >> 0 & 0xFF);
/*  600 */     paramOutputStream.write(arrayOfByte1);
/*  601 */     String str = getUserName();
/*      */     try {
/*  603 */       paramOutputStream.write(str.getBytes("ISO-8859-1"));
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  605 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */     }
/*  607 */     paramOutputStream.write(0);
/*  608 */     paramOutputStream.flush();
/*  609 */     byte[] arrayOfByte2 = new byte[8];
/*  610 */     int i = readSocksReply(paramInputStream, arrayOfByte2);
/*  611 */     if (i != 8)
/*  612 */       throw new SocketException("Reply from SOCKS server has bad length: " + i);
/*  613 */     if ((arrayOfByte2[0] != 0) && (arrayOfByte2[0] != 4))
/*  614 */       throw new SocketException("Reply from SOCKS server has bad version");
/*  615 */     SocketException localSocketException = null;
/*  616 */     switch (arrayOfByte2[1])
/*      */     {
/*      */     case 90:
/*  619 */       this.external_address = new InetSocketAddress(paramInetAddress, paramInt);
/*  620 */       break;
/*      */     case 91:
/*  622 */       localSocketException = new SocketException("SOCKS request rejected");
/*  623 */       break;
/*      */     case 92:
/*  625 */       localSocketException = new SocketException("SOCKS server couldn't reach destination");
/*  626 */       break;
/*      */     case 93:
/*  628 */       localSocketException = new SocketException("SOCKS authentication failed");
/*  629 */       break;
/*      */     default:
/*  631 */       localSocketException = new SocketException("Reply from SOCKS server contains bad status");
/*      */     }
/*      */ 
/*  634 */     if (localSocketException != null) {
/*  635 */       paramInputStream.close();
/*  636 */       paramOutputStream.close();
/*  637 */       throw localSocketException;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void socksBind(InetSocketAddress paramInetSocketAddress)
/*      */     throws IOException
/*      */   {
/*  651 */     if (this.socket != null)
/*      */     {
/*  654 */       return;
/*      */     }
/*      */ 
/*  659 */     if (this.server == null)
/*      */     {
/*  663 */       ProxySelector localProxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public ProxySelector run() {
/*  666 */           return ProxySelector.getDefault();
/*      */         }
/*      */       });
/*  669 */       if (localProxySelector == null)
/*      */       {
/*  673 */         return;
/*      */       }
/*      */ 
/*  677 */       localObject2 = paramInetSocketAddress.getHostString();
/*      */ 
/*  679 */       if (((paramInetSocketAddress.getAddress() instanceof Inet6Address)) && (!((String)localObject2).startsWith("[")) && (((String)localObject2).indexOf(":") >= 0))
/*      */       {
/*  681 */         localObject2 = "[" + (String)localObject2 + "]";
/*      */       }
/*      */       try {
/*  684 */         localObject1 = new URI("serversocket://" + ParseUtil.encodePath((String)localObject2) + ":" + paramInetSocketAddress.getPort());
/*      */       }
/*      */       catch (URISyntaxException localURISyntaxException) {
/*  687 */         if (!$assertionsDisabled) throw new AssertionError(localURISyntaxException);
/*  688 */         localObject1 = null;
/*      */       }
/*  690 */       Proxy localProxy = null;
/*  691 */       Object localObject3 = null;
/*  692 */       Iterator localIterator = null;
/*  693 */       localIterator = localProxySelector.select((URI)localObject1).iterator();
/*  694 */       if ((localIterator == null) || (!localIterator.hasNext())) {
/*  695 */         return;
/*      */       }
/*  697 */       while (localIterator.hasNext()) {
/*  698 */         localProxy = (Proxy)localIterator.next();
/*  699 */         if ((localProxy == null) || (localProxy == Proxy.NO_PROXY)) {
/*  700 */           return;
/*      */         }
/*  702 */         if (localProxy.type() != Proxy.Type.SOCKS)
/*  703 */           throw new SocketException("Unknown proxy type : " + localProxy.type());
/*  704 */         if (!(localProxy.address() instanceof InetSocketAddress)) {
/*  705 */           throw new SocketException("Unknow address type for proxy: " + localProxy);
/*      */         }
/*  707 */         this.server = ((InetSocketAddress)localProxy.address()).getHostString();
/*  708 */         this.serverPort = ((InetSocketAddress)localProxy.address()).getPort();
/*  709 */         if (((localProxy instanceof SocksProxy)) && 
/*  710 */           (((SocksProxy)localProxy).protocolVersion() == 4)) {
/*  711 */           this.useV4 = true;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  717 */           AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */           {
/*      */             public Void run() throws Exception {
/*  720 */               SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
/*  721 */               SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
/*  722 */               SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
/*  723 */               SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
/*  724 */               return null;
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (Exception localException2) {
/*  729 */           localProxySelector.connectFailed((URI)localObject1, localProxy.address(), new SocketException(localException2.getMessage()));
/*  730 */           this.server = null;
/*  731 */           this.serverPort = -1;
/*  732 */           this.cmdsock = null;
/*  733 */           localObject3 = localException2;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  742 */       if ((this.server == null) || (this.cmdsock == null))
/*  743 */         throw new SocketException("Can't connect to SOCKS proxy:" + localObject3.getMessage());
/*      */     }
/*      */     else
/*      */     {
/*      */       try {
/*  748 */         AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */         {
/*      */           public Void run() throws Exception {
/*  751 */             SocksSocketImpl.this.cmdsock = new Socket(new PlainSocketImpl());
/*  752 */             SocksSocketImpl.this.cmdsock.connect(new InetSocketAddress(SocksSocketImpl.this.server, SocksSocketImpl.this.serverPort));
/*  753 */             SocksSocketImpl.this.cmdIn = SocksSocketImpl.this.cmdsock.getInputStream();
/*  754 */             SocksSocketImpl.this.cmdOut = SocksSocketImpl.this.cmdsock.getOutputStream();
/*  755 */             return null;
/*      */           } } );
/*      */       }
/*      */       catch (Exception localException1) {
/*  759 */         throw new SocketException(localException1.getMessage());
/*      */       }
/*      */     }
/*  762 */     BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(this.cmdOut, 512);
/*  763 */     Object localObject1 = this.cmdIn;
/*  764 */     if (this.useV4) {
/*  765 */       bindV4((InputStream)localObject1, localBufferedOutputStream, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
/*  766 */       return;
/*      */     }
/*  768 */     localBufferedOutputStream.write(5);
/*  769 */     localBufferedOutputStream.write(2);
/*  770 */     localBufferedOutputStream.write(0);
/*  771 */     localBufferedOutputStream.write(2);
/*  772 */     localBufferedOutputStream.flush();
/*  773 */     Object localObject2 = new byte[2];
/*  774 */     int i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
/*  775 */     if ((i != 2) || (localObject2[0] != 5))
/*      */     {
/*  778 */       bindV4((InputStream)localObject1, localBufferedOutputStream, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
/*  779 */       return;
/*      */     }
/*  781 */     if (localObject2[1] == -1)
/*  782 */       throw new SocketException("SOCKS : No acceptable methods");
/*  783 */     if (!authenticate(localObject2[1], (InputStream)localObject1, localBufferedOutputStream)) {
/*  784 */       throw new SocketException("SOCKS : authentication failed");
/*      */     }
/*      */ 
/*  787 */     localBufferedOutputStream.write(5);
/*  788 */     localBufferedOutputStream.write(2);
/*  789 */     localBufferedOutputStream.write(0);
/*  790 */     int j = paramInetSocketAddress.getPort();
/*  791 */     if (paramInetSocketAddress.isUnresolved()) {
/*  792 */       localBufferedOutputStream.write(3);
/*  793 */       localBufferedOutputStream.write(paramInetSocketAddress.getHostName().length());
/*      */       try {
/*  795 */         localBufferedOutputStream.write(paramInetSocketAddress.getHostName().getBytes("ISO-8859-1"));
/*      */       } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  797 */         if (!$assertionsDisabled) throw new AssertionError();
/*      */       }
/*  799 */       localBufferedOutputStream.write(j >> 8 & 0xFF);
/*  800 */       localBufferedOutputStream.write(j >> 0 & 0xFF);
/*  801 */     } else if ((paramInetSocketAddress.getAddress() instanceof Inet4Address)) {
/*  802 */       localObject4 = paramInetSocketAddress.getAddress().getAddress();
/*  803 */       localBufferedOutputStream.write(1);
/*  804 */       localBufferedOutputStream.write((byte[])localObject4);
/*  805 */       localBufferedOutputStream.write(j >> 8 & 0xFF);
/*  806 */       localBufferedOutputStream.write(j >> 0 & 0xFF);
/*  807 */       localBufferedOutputStream.flush();
/*  808 */     } else if ((paramInetSocketAddress.getAddress() instanceof Inet6Address)) {
/*  809 */       localObject4 = paramInetSocketAddress.getAddress().getAddress();
/*  810 */       localBufferedOutputStream.write(4);
/*  811 */       localBufferedOutputStream.write((byte[])localObject4);
/*  812 */       localBufferedOutputStream.write(j >> 8 & 0xFF);
/*  813 */       localBufferedOutputStream.write(j >> 0 & 0xFF);
/*  814 */       localBufferedOutputStream.flush();
/*      */     } else {
/*  816 */       this.cmdsock.close();
/*  817 */       throw new SocketException("unsupported address type : " + paramInetSocketAddress);
/*      */     }
/*  819 */     localObject2 = new byte[4];
/*  820 */     i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
/*  821 */     Object localObject4 = null;
/*      */ 
/*  824 */     switch (localObject2[1])
/*      */     {
/*      */     case 0:
/*      */       byte[] arrayOfByte1;
/*      */       int m;
/*      */       int k;
/*  827 */       switch (localObject2[3]) {
/*      */       case 1:
/*  829 */         arrayOfByte1 = new byte[4];
/*  830 */         i = readSocksReply((InputStream)localObject1, arrayOfByte1);
/*  831 */         if (i != 4)
/*  832 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  833 */         localObject2 = new byte[2];
/*  834 */         i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
/*  835 */         if (i != 2)
/*  836 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  837 */         m = (localObject2[0] & 0xFF) << 8;
/*  838 */         m += (localObject2[1] & 0xFF);
/*  839 */         this.external_address = new InetSocketAddress(new Inet4Address("", arrayOfByte1), m);
/*      */ 
/*  841 */         break;
/*      */       case 3:
/*  843 */         k = localObject2[1];
/*  844 */         byte[] arrayOfByte2 = new byte[k];
/*  845 */         i = readSocksReply((InputStream)localObject1, arrayOfByte2);
/*  846 */         if (i != k)
/*  847 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  848 */         localObject2 = new byte[2];
/*  849 */         i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
/*  850 */         if (i != 2)
/*  851 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  852 */         m = (localObject2[0] & 0xFF) << 8;
/*  853 */         m += (localObject2[1] & 0xFF);
/*  854 */         this.external_address = new InetSocketAddress(new String(arrayOfByte2), m);
/*  855 */         break;
/*      */       case 4:
/*  857 */         k = localObject2[1];
/*  858 */         arrayOfByte1 = new byte[k];
/*  859 */         i = readSocksReply((InputStream)localObject1, arrayOfByte1);
/*  860 */         if (i != k)
/*  861 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  862 */         localObject2 = new byte[2];
/*  863 */         i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
/*  864 */         if (i != 2)
/*  865 */           throw new SocketException("Reply from SOCKS server badly formatted");
/*  866 */         m = (localObject2[0] & 0xFF) << 8;
/*  867 */         m += (localObject2[1] & 0xFF);
/*  868 */         this.external_address = new InetSocketAddress(new Inet6Address("", arrayOfByte1), m);
/*      */       case 2:
/*      */       }
/*      */ 
/*  872 */       break;
/*      */     case 1:
/*  874 */       localObject4 = new SocketException("SOCKS server general failure");
/*  875 */       break;
/*      */     case 2:
/*  877 */       localObject4 = new SocketException("SOCKS: Bind not allowed by ruleset");
/*  878 */       break;
/*      */     case 3:
/*  880 */       localObject4 = new SocketException("SOCKS: Network unreachable");
/*  881 */       break;
/*      */     case 4:
/*  883 */       localObject4 = new SocketException("SOCKS: Host unreachable");
/*  884 */       break;
/*      */     case 5:
/*  886 */       localObject4 = new SocketException("SOCKS: Connection refused");
/*  887 */       break;
/*      */     case 6:
/*  889 */       localObject4 = new SocketException("SOCKS: TTL expired");
/*  890 */       break;
/*      */     case 7:
/*  892 */       localObject4 = new SocketException("SOCKS: Command not supported");
/*  893 */       break;
/*      */     case 8:
/*  895 */       localObject4 = new SocketException("SOCKS: address type not supported");
/*      */     }
/*      */ 
/*  898 */     if (localObject4 != null) {
/*  899 */       ((InputStream)localObject1).close();
/*  900 */       localBufferedOutputStream.close();
/*  901 */       this.cmdsock.close();
/*  902 */       this.cmdsock = null;
/*  903 */       throw ((Throwable)localObject4);
/*      */     }
/*  905 */     this.cmdIn = ((InputStream)localObject1);
/*  906 */     this.cmdOut = localBufferedOutputStream;
/*      */   }
/*      */ 
/*      */   protected void acceptFrom(SocketImpl paramSocketImpl, InetSocketAddress paramInetSocketAddress)
/*      */     throws IOException
/*      */   {
/*  919 */     if (this.cmdsock == null)
/*      */     {
/*  921 */       return;
/*      */     }
/*  923 */     InputStream localInputStream = this.cmdIn;
/*      */ 
/*  925 */     socksBind(paramInetSocketAddress);
/*  926 */     localInputStream.read();
/*  927 */     int i = localInputStream.read();
/*  928 */     localInputStream.read();
/*  929 */     SocketException localSocketException = null;
/*      */ 
/*  932 */     InetSocketAddress localInetSocketAddress = null;
/*  933 */     switch (i)
/*      */     {
/*      */     case 0:
/*  936 */       i = localInputStream.read();
/*      */       byte[] arrayOfByte;
/*      */       int j;
/*  937 */       switch (i) {
/*      */       case 1:
/*  939 */         arrayOfByte = new byte[4];
/*  940 */         readSocksReply(localInputStream, arrayOfByte);
/*  941 */         j = localInputStream.read() << 8;
/*  942 */         j += localInputStream.read();
/*  943 */         localInetSocketAddress = new InetSocketAddress(new Inet4Address("", arrayOfByte), j);
/*      */ 
/*  945 */         break;
/*      */       case 3:
/*  947 */         int k = localInputStream.read();
/*  948 */         arrayOfByte = new byte[k];
/*  949 */         readSocksReply(localInputStream, arrayOfByte);
/*  950 */         j = localInputStream.read() << 8;
/*  951 */         j += localInputStream.read();
/*  952 */         localInetSocketAddress = new InetSocketAddress(new String(arrayOfByte), j);
/*  953 */         break;
/*      */       case 4:
/*  955 */         arrayOfByte = new byte[16];
/*  956 */         readSocksReply(localInputStream, arrayOfByte);
/*  957 */         j = localInputStream.read() << 8;
/*  958 */         j += localInputStream.read();
/*  959 */         localInetSocketAddress = new InetSocketAddress(new Inet6Address("", arrayOfByte), j);
/*      */       case 2:
/*      */       }
/*      */ 
/*  963 */       break;
/*      */     case 1:
/*  965 */       localSocketException = new SocketException("SOCKS server general failure");
/*  966 */       break;
/*      */     case 2:
/*  968 */       localSocketException = new SocketException("SOCKS: Accept not allowed by ruleset");
/*  969 */       break;
/*      */     case 3:
/*  971 */       localSocketException = new SocketException("SOCKS: Network unreachable");
/*  972 */       break;
/*      */     case 4:
/*  974 */       localSocketException = new SocketException("SOCKS: Host unreachable");
/*  975 */       break;
/*      */     case 5:
/*  977 */       localSocketException = new SocketException("SOCKS: Connection refused");
/*  978 */       break;
/*      */     case 6:
/*  980 */       localSocketException = new SocketException("SOCKS: TTL expired");
/*  981 */       break;
/*      */     case 7:
/*  983 */       localSocketException = new SocketException("SOCKS: Command not supported");
/*  984 */       break;
/*      */     case 8:
/*  986 */       localSocketException = new SocketException("SOCKS: address type not supported");
/*      */     }
/*      */ 
/*  989 */     if (localSocketException != null) {
/*  990 */       this.cmdIn.close();
/*  991 */       this.cmdOut.close();
/*  992 */       this.cmdsock.close();
/*  993 */       this.cmdsock = null;
/*  994 */       throw localSocketException;
/*      */     }
/*      */ 
/* 1002 */     if ((paramSocketImpl instanceof SocksSocketImpl)) {
/* 1003 */       ((SocksSocketImpl)paramSocketImpl).external_address = localInetSocketAddress;
/*      */     }
/* 1005 */     if ((paramSocketImpl instanceof PlainSocketImpl)) {
/* 1006 */       PlainSocketImpl localPlainSocketImpl = (PlainSocketImpl)paramSocketImpl;
/* 1007 */       localPlainSocketImpl.setInputStream((SocketInputStream)localInputStream);
/* 1008 */       localPlainSocketImpl.setFileDescriptor(this.cmdsock.getImpl().getFileDescriptor());
/* 1009 */       localPlainSocketImpl.setAddress(this.cmdsock.getImpl().getInetAddress());
/* 1010 */       localPlainSocketImpl.setPort(this.cmdsock.getImpl().getPort());
/* 1011 */       localPlainSocketImpl.setLocalPort(this.cmdsock.getImpl().getLocalPort());
/*      */     } else {
/* 1013 */       paramSocketImpl.fd = this.cmdsock.getImpl().fd;
/* 1014 */       paramSocketImpl.address = this.cmdsock.getImpl().address;
/* 1015 */       paramSocketImpl.port = this.cmdsock.getImpl().port;
/* 1016 */       paramSocketImpl.localport = this.cmdsock.getImpl().localport;
/*      */     }
/*      */ 
/* 1023 */     this.cmdsock = null;
/*      */   }
/*      */ 
/*      */   protected InetAddress getInetAddress()
/*      */   {
/* 1035 */     if (this.external_address != null) {
/* 1036 */       return this.external_address.getAddress();
/*      */     }
/* 1038 */     return super.getInetAddress();
/*      */   }
/*      */ 
/*      */   protected int getPort()
/*      */   {
/* 1049 */     if (this.external_address != null) {
/* 1050 */       return this.external_address.getPort();
/*      */     }
/* 1052 */     return super.getPort();
/*      */   }
/*      */ 
/*      */   protected int getLocalPort()
/*      */   {
/* 1057 */     if (this.socket != null)
/* 1058 */       return super.getLocalPort();
/* 1059 */     if (this.external_address != null) {
/* 1060 */       return this.external_address.getPort();
/*      */     }
/* 1062 */     return super.getLocalPort();
/*      */   }
/*      */ 
/*      */   protected void close() throws IOException
/*      */   {
/* 1067 */     if (this.cmdsock != null)
/* 1068 */       this.cmdsock.close();
/* 1069 */     this.cmdsock = null;
/* 1070 */     super.close();
/*      */   }
/*      */ 
/*      */   private String getUserName() {
/* 1074 */     String str = "";
/* 1075 */     if (this.applicationSetProxy)
/*      */       try {
/* 1077 */         str = System.getProperty("user.name");
/*      */       } catch (SecurityException localSecurityException) {
/*      */       }
/* 1080 */     else str = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
/*      */ 
/* 1083 */     return str;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.SocksSocketImpl
 * JD-Core Version:    0.6.2
 */