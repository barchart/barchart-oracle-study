/*     */ package sun.net.www.http;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.CacheRequest;
/*     */ import java.net.CookieHandler;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Locale;
/*     */ import sun.net.NetworkClient;
/*     */ import sun.net.ProgressSource;
/*     */ import sun.net.www.HeaderParser;
/*     */ import sun.net.www.MessageHeader;
/*     */ import sun.net.www.MeteredStream;
/*     */ import sun.net.www.ParseUtil;
/*     */ import sun.net.www.URLConnection;
/*     */ import sun.net.www.protocol.http.HttpURLConnection;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class HttpClient extends NetworkClient
/*     */ {
/*  46 */   protected boolean cachedHttpClient = false;
/*     */   private boolean inCache;
/*     */   MessageHeader requests;
/*  54 */   PosterOutputStream poster = null;
/*     */   boolean streaming;
/*  60 */   boolean failedOnce = false;
/*     */ 
/*  63 */   private boolean ignoreContinue = true;
/*     */   private static final int HTTP_CONTINUE = 100;
/*     */   static final int httpPortNumber = 80;
/*     */   protected boolean proxyDisabled;
/*  86 */   public boolean usingProxy = false;
/*     */   protected String host;
/*     */   protected int port;
/*     */   protected static KeepAliveCache kac;
/*     */   private static boolean keepAliveProp;
/*     */   private static boolean retryPostProp;
/* 100 */   volatile boolean keepingAlive = false;
/* 101 */   int keepAliveConnections = -1;
/*     */ 
/* 111 */   int keepAliveTimeout = 0;
/*     */ 
/* 114 */   private CacheRequest cacheRequest = null;
/*     */   protected URL url;
/* 120 */   public boolean reuse = false;
/*     */ 
/* 123 */   private HttpCapture capture = null;
/*     */ 
/*     */   protected int getDefaultPort()
/*     */   {
/*  70 */     return 80;
/*     */   }
/*     */   private static int getDefaultPort(String paramString) {
/*  73 */     if ("http".equalsIgnoreCase(paramString))
/*  74 */       return 80;
/*  75 */     if ("https".equalsIgnoreCase(paramString))
/*  76 */       return 443;
/*  77 */     return -1;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static synchronized void resetProperties()
/*     */   {
/*     */   }
/*     */ 
/*     */   int getKeepAliveTimeout()
/*     */   {
/* 134 */     return this.keepAliveTimeout;
/*     */   }
/*     */ 
/*     */   public boolean getHttpKeepAliveSet()
/*     */   {
/* 162 */     return keepAliveProp;
/*     */   }
/*     */ 
/*     */   protected HttpClient()
/*     */   {
/*     */   }
/*     */ 
/*     */   private HttpClient(URL paramURL) throws IOException
/*     */   {
/* 171 */     this(paramURL, (String)null, -1, false);
/*     */   }
/*     */ 
/*     */   protected HttpClient(URL paramURL, boolean paramBoolean) throws IOException
/*     */   {
/* 176 */     this(paramURL, null, -1, paramBoolean);
/*     */   }
/*     */ 
/*     */   public HttpClient(URL paramURL, String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 189 */     this(paramURL, paramString, paramInt, false);
/*     */   }
/*     */ 
/*     */   protected HttpClient(URL paramURL, Proxy paramProxy, int paramInt) throws IOException {
/* 193 */     this.proxy = (paramProxy == null ? Proxy.NO_PROXY : paramProxy);
/* 194 */     this.host = paramURL.getHost();
/* 195 */     this.url = paramURL;
/* 196 */     this.port = paramURL.getPort();
/* 197 */     if (this.port == -1) {
/* 198 */       this.port = getDefaultPort();
/*     */     }
/* 200 */     setConnectTimeout(paramInt);
/*     */ 
/* 202 */     this.capture = HttpCapture.getCapture(paramURL);
/* 203 */     openServer();
/*     */   }
/*     */ 
/*     */   protected static Proxy newHttpProxy(String paramString1, int paramInt, String paramString2)
/*     */   {
/* 208 */     if ((paramString1 == null) || (paramString2 == null))
/* 209 */       return Proxy.NO_PROXY;
/* 210 */     int i = paramInt < 0 ? getDefaultPort(paramString2) : paramInt;
/* 211 */     InetSocketAddress localInetSocketAddress = InetSocketAddress.createUnresolved(paramString1, i);
/* 212 */     return new Proxy(Proxy.Type.HTTP, localInetSocketAddress);
/*     */   }
/*     */ 
/*     */   private HttpClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 230 */     this(paramURL, paramBoolean ? Proxy.NO_PROXY : newHttpProxy(paramString, paramInt, "http"), -1);
/*     */   }
/*     */ 
/*     */   public HttpClient(URL paramURL, String paramString, int paramInt1, boolean paramBoolean, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 237 */     this(paramURL, paramBoolean ? Proxy.NO_PROXY : newHttpProxy(paramString, paramInt1, "http"), paramInt2);
/*     */   }
/*     */ 
/*     */   public static HttpClient New(URL paramURL)
/*     */     throws IOException
/*     */   {
/* 247 */     return New(paramURL, Proxy.NO_PROXY, -1, true);
/*     */   }
/*     */ 
/*     */   public static HttpClient New(URL paramURL, boolean paramBoolean) throws IOException
/*     */   {
/* 252 */     return New(paramURL, Proxy.NO_PROXY, -1, paramBoolean);
/*     */   }
/*     */ 
/*     */   public static HttpClient New(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean) throws IOException
/*     */   {
/* 257 */     if (paramProxy == null) {
/* 258 */       paramProxy = Proxy.NO_PROXY;
/*     */     }
/* 260 */     HttpClient localHttpClient = null;
/*     */ 
/* 262 */     if (paramBoolean) {
/* 263 */       localHttpClient = kac.get(paramURL, null);
/* 264 */       if (localHttpClient != null) {
/* 265 */         if (((localHttpClient.proxy != null) && (localHttpClient.proxy.equals(paramProxy))) || ((localHttpClient.proxy == null) && (paramProxy == null)))
/*     */         {
/* 267 */           synchronized (localHttpClient) {
/* 268 */             localHttpClient.cachedHttpClient = true;
/* 269 */             assert (localHttpClient.inCache);
/* 270 */             localHttpClient.inCache = false;
/* 271 */             PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
/* 272 */             if (localPlatformLogger.isLoggable(300)) {
/* 273 */               localPlatformLogger.finest("KeepAlive stream retrieved from the cache, " + localHttpClient);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 281 */           synchronized (localHttpClient) {
/* 282 */             localHttpClient.inCache = false;
/* 283 */             localHttpClient.closeServer();
/*     */           }
/* 285 */           localHttpClient = null;
/*     */         }
/*     */       }
/*     */     }
/* 289 */     if (localHttpClient == null) {
/* 290 */       localHttpClient = new HttpClient(paramURL, paramProxy, paramInt);
/*     */     } else {
/* 292 */       ??? = System.getSecurityManager();
/* 293 */       if (??? != null) {
/* 294 */         if ((localHttpClient.proxy == Proxy.NO_PROXY) || (localHttpClient.proxy == null))
/* 295 */           ((SecurityManager)???).checkConnect(InetAddress.getByName(paramURL.getHost()).getHostAddress(), paramURL.getPort());
/*     */         else {
/* 297 */           ((SecurityManager)???).checkConnect(paramURL.getHost(), paramURL.getPort());
/*     */         }
/*     */       }
/* 300 */       localHttpClient.url = paramURL;
/*     */     }
/* 302 */     return localHttpClient;
/*     */   }
/*     */ 
/*     */   public static HttpClient New(URL paramURL, Proxy paramProxy, int paramInt) throws IOException {
/* 306 */     return New(paramURL, paramProxy, paramInt, true);
/*     */   }
/*     */ 
/*     */   public static HttpClient New(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 312 */     return New(paramURL, newHttpProxy(paramString, paramInt, "http"), -1, paramBoolean);
/*     */   }
/*     */ 
/*     */   public static HttpClient New(URL paramURL, String paramString, int paramInt1, boolean paramBoolean, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 318 */     return New(paramURL, newHttpProxy(paramString, paramInt1, "http"), paramInt2, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void finished()
/*     */   {
/* 332 */     if (this.reuse)
/* 333 */       return;
/* 334 */     this.keepAliveConnections -= 1;
/* 335 */     this.poster = null;
/* 336 */     if ((this.keepAliveConnections > 0) && (isKeepingAlive()) && (!this.serverOutput.checkError()))
/*     */     {
/* 341 */       putInKeepAliveCache();
/*     */     }
/* 343 */     else closeServer();
/*     */   }
/*     */ 
/*     */   protected synchronized void putInKeepAliveCache()
/*     */   {
/* 348 */     if (this.inCache) {
/* 349 */       if (!$assertionsDisabled) throw new AssertionError("Duplicate put to keep alive cache");
/* 350 */       return;
/*     */     }
/* 352 */     this.inCache = true;
/* 353 */     kac.put(this.url, null, this);
/*     */   }
/*     */ 
/*     */   protected synchronized boolean isInKeepAliveCache() {
/* 357 */     return this.inCache;
/*     */   }
/*     */ 
/*     */   public void closeIdleConnection()
/*     */   {
/* 365 */     HttpClient localHttpClient = kac.get(this.url, null);
/* 366 */     if (localHttpClient != null)
/* 367 */       localHttpClient.closeServer();
/*     */   }
/*     */ 
/*     */   public void openServer(String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 378 */     this.serverSocket = doConnect(paramString, paramInt);
/*     */     try {
/* 380 */       Object localObject = this.serverSocket.getOutputStream();
/* 381 */       if (this.capture != null) {
/* 382 */         localObject = new HttpCaptureOutputStream((OutputStream)localObject, this.capture);
/*     */       }
/* 384 */       this.serverOutput = new PrintStream(new BufferedOutputStream((OutputStream)localObject), false, encoding);
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */     {
/* 388 */       throw new InternalError(encoding + " encoding not found");
/*     */     }
/* 390 */     this.serverSocket.setTcpNoDelay(true);
/*     */   }
/*     */ 
/*     */   public boolean needsTunneling()
/*     */   {
/* 398 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isCachedConnection()
/*     */   {
/* 405 */     return this.cachedHttpClient;
/*     */   }
/*     */ 
/*     */   public void afterConnect()
/*     */     throws IOException, UnknownHostException
/*     */   {
/*     */   }
/*     */ 
/*     */   private synchronized void privilegedOpenServer(final InetSocketAddress paramInetSocketAddress)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 427 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Void run() throws IOException {
/* 430 */           HttpClient.this.openServer(paramInetSocketAddress.getHostString(), paramInetSocketAddress.getPort());
/* 431 */           return null;
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/* 435 */       throw ((IOException)localPrivilegedActionException.getException());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void superOpenServer(String paramString, int paramInt)
/*     */     throws IOException, UnknownHostException
/*     */   {
/* 446 */     super.openServer(paramString, paramInt);
/*     */   }
/*     */ 
/*     */   protected synchronized void openServer()
/*     */     throws IOException
/*     */   {
/* 453 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*     */ 
/* 455 */     if (localSecurityManager != null) {
/* 456 */       localSecurityManager.checkConnect(this.host, this.port);
/*     */     }
/*     */ 
/* 459 */     if (this.keepingAlive) {
/* 460 */       return;
/*     */     }
/*     */ 
/* 463 */     if ((this.url.getProtocol().equals("http")) || (this.url.getProtocol().equals("https")))
/*     */     {
/* 466 */       if ((this.proxy != null) && (this.proxy.type() == Proxy.Type.HTTP)) {
/* 467 */         URLConnection.setProxiedHost(this.host);
/* 468 */         privilegedOpenServer((InetSocketAddress)this.proxy.address());
/* 469 */         this.usingProxy = true;
/* 470 */         return;
/*     */       }
/*     */ 
/* 473 */       openServer(this.host, this.port);
/* 474 */       this.usingProxy = false;
/* 475 */       return;
/*     */     }
/*     */ 
/* 482 */     if ((this.proxy != null) && (this.proxy.type() == Proxy.Type.HTTP)) {
/* 483 */       URLConnection.setProxiedHost(this.host);
/* 484 */       privilegedOpenServer((InetSocketAddress)this.proxy.address());
/* 485 */       this.usingProxy = true;
/* 486 */       return;
/*     */     }
/*     */ 
/* 489 */     super.openServer(this.host, this.port);
/* 490 */     this.usingProxy = false;
/*     */   }
/*     */ 
/*     */   public String getURLFile()
/*     */     throws IOException
/*     */   {
/* 498 */     String str = this.url.getFile();
/* 499 */     if ((str == null) || (str.length() == 0)) {
/* 500 */       str = "/";
/*     */     }
/*     */ 
/* 505 */     if ((this.usingProxy) && (!this.proxyDisabled))
/*     */     {
/* 509 */       StringBuffer localStringBuffer = new StringBuffer(128);
/* 510 */       localStringBuffer.append(this.url.getProtocol());
/* 511 */       localStringBuffer.append(":");
/* 512 */       if ((this.url.getAuthority() != null) && (this.url.getAuthority().length() > 0)) {
/* 513 */         localStringBuffer.append("//");
/* 514 */         localStringBuffer.append(this.url.getAuthority());
/*     */       }
/* 516 */       if (this.url.getPath() != null) {
/* 517 */         localStringBuffer.append(this.url.getPath());
/*     */       }
/* 519 */       if (this.url.getQuery() != null) {
/* 520 */         localStringBuffer.append('?');
/* 521 */         localStringBuffer.append(this.url.getQuery());
/*     */       }
/*     */ 
/* 524 */       str = localStringBuffer.toString();
/*     */     }
/* 526 */     if (str.indexOf('\n') == -1) {
/* 527 */       return str;
/*     */     }
/* 529 */     throw new MalformedURLException("Illegal character in URL");
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void writeRequests(MessageHeader paramMessageHeader)
/*     */   {
/* 537 */     this.requests = paramMessageHeader;
/* 538 */     this.requests.print(this.serverOutput);
/* 539 */     this.serverOutput.flush();
/*     */   }
/*     */ 
/*     */   public void writeRequests(MessageHeader paramMessageHeader, PosterOutputStream paramPosterOutputStream) throws IOException
/*     */   {
/* 544 */     this.requests = paramMessageHeader;
/* 545 */     this.requests.print(this.serverOutput);
/* 546 */     this.poster = paramPosterOutputStream;
/* 547 */     if (this.poster != null)
/* 548 */       this.poster.writeTo(this.serverOutput);
/* 549 */     this.serverOutput.flush();
/*     */   }
/*     */ 
/*     */   public void writeRequests(MessageHeader paramMessageHeader, PosterOutputStream paramPosterOutputStream, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 555 */     this.streaming = paramBoolean;
/* 556 */     writeRequests(paramMessageHeader, paramPosterOutputStream);
/*     */   }
/*     */ 
/*     */   public boolean parseHTTP(MessageHeader paramMessageHeader, ProgressSource paramProgressSource, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 574 */       this.serverInput = this.serverSocket.getInputStream();
/* 575 */       if (this.capture != null) {
/* 576 */         this.serverInput = new HttpCaptureInputStream(this.serverInput, this.capture);
/*     */       }
/* 578 */       this.serverInput = new BufferedInputStream(this.serverInput);
/* 579 */       return parseHTTPHeader(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
/*     */     }
/*     */     catch (SocketTimeoutException localSocketTimeoutException)
/*     */     {
/* 583 */       if (this.ignoreContinue) {
/* 584 */         closeServer();
/*     */       }
/* 586 */       throw localSocketTimeoutException;
/*     */     } catch (IOException localIOException) {
/* 588 */       closeServer();
/* 589 */       this.cachedHttpClient = false;
/* 590 */       if ((!this.failedOnce) && (this.requests != null)) {
/* 591 */         this.failedOnce = true;
/* 592 */         if ((!getRequestMethod().equals("CONNECT")) && ((!paramHttpURLConnection.getRequestMethod().equals("POST")) || ((retryPostProp) && (!this.streaming))))
/*     */         {
/* 598 */           openServer();
/* 599 */           if (needsTunneling()) {
/* 600 */             paramHttpURLConnection.doTunneling();
/*     */           }
/* 602 */           afterConnect();
/* 603 */           writeRequests(this.requests, this.poster);
/* 604 */           return parseHTTP(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
/*     */         }
/*     */       }
/* 607 */       throw localIOException;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean parseHTTPHeader(MessageHeader paramMessageHeader, ProgressSource paramProgressSource, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/* 623 */     this.keepAliveConnections = -1;
/* 624 */     this.keepAliveTimeout = 0;
/*     */ 
/* 626 */     boolean bool = false;
/* 627 */     byte[] arrayOfByte = new byte[8];
/*     */     String str1;
/*     */     try
/*     */     {
/* 630 */       int i = 0;
/* 631 */       this.serverInput.mark(10);
/* 632 */       while (i < 8) {
/* 633 */         int k = this.serverInput.read(arrayOfByte, i, 8 - i);
/* 634 */         if (k < 0) {
/*     */           break;
/*     */         }
/* 637 */         i += k;
/*     */       }
/* 639 */       str1 = null;
/* 640 */       bool = (arrayOfByte[0] == 72) && (arrayOfByte[1] == 84) && (arrayOfByte[2] == 84) && (arrayOfByte[3] == 80) && (arrayOfByte[4] == 47) && (arrayOfByte[5] == 49) && (arrayOfByte[6] == 46);
/*     */ 
/* 643 */       this.serverInput.reset();
/* 644 */       if (bool) {
/* 645 */         paramMessageHeader.parseHeader(this.serverInput);
/*     */ 
/* 649 */         CookieHandler localCookieHandler = paramHttpURLConnection.getCookieHandler();
/* 650 */         if (localCookieHandler != null) {
/* 651 */           localObject1 = ParseUtil.toURI(this.url);
/*     */ 
/* 656 */           if (localObject1 != null) {
/* 657 */             localCookieHandler.put((URI)localObject1, paramMessageHeader.getHeaders());
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 666 */         if (this.usingProxy) {
/* 667 */           str1 = paramMessageHeader.findValue("Proxy-Connection");
/*     */         }
/* 669 */         if (str1 == null) {
/* 670 */           str1 = paramMessageHeader.findValue("Connection");
/*     */         }
/* 672 */         if ((str1 != null) && (str1.toLowerCase(Locale.US).equals("keep-alive")))
/*     */         {
/* 676 */           localObject1 = new HeaderParser(paramMessageHeader.findValue("Keep-Alive"));
/*     */ 
/* 678 */           if (localObject1 != null)
/*     */           {
/* 680 */             this.keepAliveConnections = ((HeaderParser)localObject1).findInt("max", this.usingProxy ? 50 : 5);
/* 681 */             this.keepAliveTimeout = ((HeaderParser)localObject1).findInt("timeout", this.usingProxy ? 60 : 5);
/*     */           }
/* 683 */         } else if (arrayOfByte[7] != 48)
/*     */         {
/* 688 */           if (str1 != null)
/*     */           {
/* 694 */             this.keepAliveConnections = 1;
/*     */           }
/* 696 */           else this.keepAliveConnections = 5; 
/*     */         }
/*     */       }
/* 699 */       else { if (i != 8) {
/* 700 */           if ((!this.failedOnce) && (this.requests != null)) {
/* 701 */             this.failedOnce = true;
/* 702 */             if ((!getRequestMethod().equals("CONNECT")) && ((!paramHttpURLConnection.getRequestMethod().equals("POST")) || ((retryPostProp) && (!this.streaming))))
/*     */             {
/* 707 */               closeServer();
/* 708 */               this.cachedHttpClient = false;
/* 709 */               openServer();
/* 710 */               if (needsTunneling()) {
/* 711 */                 paramHttpURLConnection.doTunneling();
/*     */               }
/* 713 */               afterConnect();
/* 714 */               writeRequests(this.requests, this.poster);
/* 715 */               return parseHTTP(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
/*     */             }
/*     */           }
/* 718 */           throw new SocketException("Unexpected end of file from server");
/*     */         }
/*     */ 
/* 721 */         paramMessageHeader.set("Content-type", "unknown/unknown"); }
/*     */     }
/*     */     catch (IOException localIOException) {
/* 724 */       throw localIOException;
/*     */     }
/*     */ 
/* 727 */     int j = -1;
/*     */     try
/*     */     {
/* 730 */       str1 = paramMessageHeader.getValue(0);
/*     */ 
/* 736 */       int m = str1.indexOf(' ');
/* 737 */       while (str1.charAt(m) == ' ')
/* 738 */         m++;
/* 739 */       j = Integer.parseInt(str1.substring(m, m + 3));
/*     */     } catch (Exception localException) {
/*     */     }
/* 742 */     if ((j == 100) && (this.ignoreContinue)) {
/* 743 */       paramMessageHeader.reset();
/* 744 */       return parseHTTPHeader(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
/*     */     }
/*     */ 
/* 747 */     long l = -1L;
/*     */ 
/* 755 */     Object localObject1 = paramMessageHeader.findValue("Transfer-Encoding");
/*     */     Object localObject2;
/* 756 */     if ((localObject1 != null) && (((String)localObject1).equalsIgnoreCase("chunked"))) {
/* 757 */       this.serverInput = new ChunkedInputStream(this.serverInput, this, paramMessageHeader);
/*     */ 
/* 763 */       if (this.keepAliveConnections <= 1) {
/* 764 */         this.keepAliveConnections = 1;
/* 765 */         this.keepingAlive = false;
/*     */       } else {
/* 767 */         this.keepingAlive = true;
/*     */       }
/* 769 */       this.failedOnce = false;
/*     */     }
/*     */     else
/*     */     {
/* 779 */       localObject2 = paramMessageHeader.findValue("content-length");
/* 780 */       if (localObject2 != null) {
/*     */         try {
/* 782 */           l = Long.parseLong((String)localObject2);
/*     */         } catch (NumberFormatException localNumberFormatException) {
/* 784 */           l = -1L;
/*     */         }
/*     */       }
/* 787 */       String str2 = this.requests.getKey(0);
/*     */ 
/* 789 */       if (((str2 != null) && (str2.startsWith("HEAD"))) || (j == 304) || (j == 204))
/*     */       {
/* 793 */         l = 0L;
/*     */       }
/*     */ 
/* 796 */       if ((this.keepAliveConnections > 1) && ((l >= 0L) || (j == 304) || (j == 204)))
/*     */       {
/* 800 */         this.keepingAlive = true;
/* 801 */         this.failedOnce = false;
/* 802 */       } else if (this.keepingAlive)
/*     */       {
/* 807 */         this.keepingAlive = false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 813 */     if (l > 0L)
/*     */     {
/* 817 */       if (paramProgressSource != null)
/*     */       {
/* 819 */         paramProgressSource.setContentType(paramMessageHeader.findValue("content-type"));
/*     */       }
/*     */ 
/* 822 */       if (isKeepingAlive())
/*     */       {
/* 824 */         localObject2 = HttpURLConnection.getHttpLogger();
/* 825 */         if (((PlatformLogger)localObject2).isLoggable(300)) {
/* 826 */           ((PlatformLogger)localObject2).finest("KeepAlive stream used: " + this.url);
/*     */         }
/* 828 */         this.serverInput = new KeepAliveStream(this.serverInput, paramProgressSource, l, this);
/* 829 */         this.failedOnce = false;
/*     */       }
/*     */       else {
/* 832 */         this.serverInput = new MeteredStream(this.serverInput, paramProgressSource, l);
/*     */       }
/*     */     }
/* 835 */     else if (l == -1L)
/*     */     {
/* 840 */       if (paramProgressSource != null)
/*     */       {
/* 843 */         paramProgressSource.setContentType(paramMessageHeader.findValue("content-type"));
/*     */ 
/* 847 */         this.serverInput = new MeteredStream(this.serverInput, paramProgressSource, l);
/*     */       }
/*     */ 
/*     */     }
/* 857 */     else if (paramProgressSource != null) {
/* 858 */       paramProgressSource.finishTracking();
/*     */     }
/*     */ 
/* 861 */     return bool;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getInputStream() {
/* 865 */     return this.serverInput;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() {
/* 869 */     return this.serverOutput;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 874 */     return getClass().getName() + "(" + this.url + ")";
/*     */   }
/*     */ 
/*     */   public final boolean isKeepingAlive() {
/* 878 */     return (getHttpKeepAliveSet()) && (this.keepingAlive);
/*     */   }
/*     */ 
/*     */   public void setCacheRequest(CacheRequest paramCacheRequest) {
/* 882 */     this.cacheRequest = paramCacheRequest;
/*     */   }
/*     */ 
/*     */   CacheRequest getCacheRequest() {
/* 886 */     return this.cacheRequest;
/*     */   }
/*     */ 
/*     */   String getRequestMethod() {
/* 890 */     if (this.requests != null) {
/* 891 */       String str = this.requests.getKey(0);
/* 892 */       if (str != null) {
/* 893 */         return str.split("\\s+")[0];
/*     */       }
/*     */     }
/* 896 */     return "";
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setDoNotRetry(boolean paramBoolean)
/*     */   {
/* 907 */     this.failedOnce = paramBoolean;
/*     */   }
/*     */ 
/*     */   public void setIgnoreContinue(boolean paramBoolean) {
/* 911 */     this.ignoreContinue = paramBoolean;
/*     */   }
/*     */ 
/*     */   public void closeServer()
/*     */   {
/*     */     try
/*     */     {
/* 918 */       this.keepingAlive = false;
/* 919 */       this.serverSocket.close();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getProxyHostUsed()
/*     */   {
/* 928 */     if (!this.usingProxy) {
/* 929 */       return null;
/*     */     }
/* 931 */     return ((InetSocketAddress)this.proxy.address()).getHostString();
/*     */   }
/*     */ 
/*     */   public int getProxyPortUsed()
/*     */   {
/* 940 */     if (this.usingProxy)
/* 941 */       return ((InetSocketAddress)this.proxy.address()).getPort();
/* 942 */     return -1;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  92 */     kac = new KeepAliveCache();
/*     */ 
/*  94 */     keepAliveProp = true;
/*     */ 
/*  98 */     retryPostProp = true;
/*     */ 
/* 138 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("http.keepAlive"));
/*     */ 
/* 141 */     String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.net.http.retryPost"));
/*     */ 
/* 144 */     if (str1 != null)
/* 145 */       keepAliveProp = Boolean.valueOf(str1).booleanValue();
/*     */     else {
/* 147 */       keepAliveProp = true;
/*     */     }
/*     */ 
/* 150 */     if (str2 != null)
/* 151 */       retryPostProp = Boolean.valueOf(str2).booleanValue();
/*     */     else
/* 153 */       retryPostProp = true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.http.HttpClient
 * JD-Core Version:    0.6.2
 */