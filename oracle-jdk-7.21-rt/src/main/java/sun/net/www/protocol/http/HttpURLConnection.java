/*      */ package sun.net.www.protocol.http;
/*      */ 
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FilterInputStream;
/*      */ import java.io.FilterOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.net.Authenticator;
/*      */ import java.net.Authenticator.RequestorType;
/*      */ import java.net.CacheRequest;
/*      */ import java.net.CacheResponse;
/*      */ import java.net.CookieHandler;
/*      */ import java.net.HttpCookie;
/*      */ import java.net.HttpRetryException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.PasswordAuthentication;
/*      */ import java.net.ProtocolException;
/*      */ import java.net.Proxy;
/*      */ import java.net.Proxy.Type;
/*      */ import java.net.ProxySelector;
/*      */ import java.net.ResponseCache;
/*      */ import java.net.SecureCacheResponse;
/*      */ import java.net.SocketTimeoutException;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import sun.misc.JavaNetHttpCookieAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.net.ApplicationProxy;
/*      */ import sun.net.ProgressMonitor;
/*      */ import sun.net.ProgressSource;
/*      */ import sun.net.www.HeaderParser;
/*      */ import sun.net.www.MessageHeader;
/*      */ import sun.net.www.MeteredStream;
/*      */ import sun.net.www.ParseUtil;
/*      */ import sun.net.www.http.ChunkedInputStream;
/*      */ import sun.net.www.http.ChunkedOutputStream;
/*      */ import sun.net.www.http.HttpClient;
/*      */ import sun.net.www.http.PosterOutputStream;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetIntegerAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class HttpURLConnection extends java.net.HttpURLConnection
/*      */ {
/*   85 */   static String HTTP_CONNECT = "CONNECT";
/*      */   static final String version;
/*      */   public static final String userAgent;
/*      */   static final int defaultmaxRedirects = 20;
/*      */   static final int maxRedirects;
/*      */   static final boolean validateProxy;
/*      */   static final boolean validateServer;
/*      */   private StreamingOutputStream strOutputStream;
/*      */   private static final String RETRY_MSG1 = "cannot retry due to proxy authentication, in streaming mode";
/*      */   private static final String RETRY_MSG2 = "cannot retry due to server authentication, in streaming mode";
/*      */   private static final String RETRY_MSG3 = "cannot retry due to redirection, in streaming mode";
/*  139 */   private static boolean enableESBuffer = false;
/*      */ 
/*  143 */   private static int timeout4ESBuffer = 0;
/*      */ 
/*  147 */   private static int bufSize4ES = 0;
/*      */   private static final boolean allowRestrictedHeaders;
/*      */   private static final Set<String> restrictedHeaderSet;
/*  172 */   private static final String[] restrictedHeaders = { "Access-Control-Request-Headers", "Access-Control-Request-Method", "Connection", "Content-Length", "Content-Transfer-Encoding", "Host", "Keep-Alive", "Origin", "Trailer", "Transfer-Encoding", "Upgrade", "Via" };
/*      */   static final String httpVersion = "HTTP/1.1";
/*      */   static final String acceptString = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
/*  254 */   private static final String[] EXCLUDE_HEADERS = { "Proxy-Authorization", "Authorization" };
/*      */ 
/*  260 */   private static final String[] EXCLUDE_HEADERS2 = { "Proxy-Authorization", "Authorization", "Cookie", "Cookie2" };
/*      */   protected HttpClient http;
/*      */   protected Handler handler;
/*      */   protected Proxy instProxy;
/*      */   private CookieHandler cookieHandler;
/*      */   private ResponseCache cacheHandler;
/*      */   protected CacheResponse cachedResponse;
/*      */   private MessageHeader cachedHeaders;
/*      */   private InputStream cachedInputStream;
/*  280 */   protected PrintStream ps = null;
/*      */ 
/*  284 */   private InputStream errorStream = null;
/*      */ 
/*  287 */   private boolean setUserCookies = true;
/*  288 */   private String userCookies = null;
/*  289 */   private String userCookies2 = null;
/*      */   private static HttpAuthenticator defaultAuth;
/*      */   private MessageHeader requests;
/*      */   String domain;
/*      */   DigestAuthentication.Parameters digestparams;
/*  309 */   AuthenticationInfo currentProxyCredentials = null;
/*  310 */   AuthenticationInfo currentServerCredentials = null;
/*  311 */   boolean needToCheck = true;
/*  312 */   private boolean doingNTLM2ndStage = false;
/*  313 */   private boolean doingNTLMp2ndStage = false;
/*      */ 
/*  316 */   private boolean tryTransparentNTLMServer = true;
/*  317 */   private boolean tryTransparentNTLMProxy = true;
/*      */   private Object authObj;
/*      */   boolean isUserServerAuth;
/*      */   boolean isUserProxyAuth;
/*      */   String serverAuthKey;
/*      */   String proxyAuthKey;
/*      */   protected ProgressSource pi;
/*      */   private MessageHeader responses;
/*  334 */   private InputStream inputStream = null;
/*      */ 
/*  336 */   private PosterOutputStream poster = null;
/*      */ 
/*  339 */   private boolean setRequests = false;
/*      */ 
/*  342 */   private boolean failedOnce = false;
/*      */ 
/*  346 */   private Exception rememberedException = null;
/*      */ 
/*  349 */   private HttpClient reuseClient = null;
/*      */ 
/*  363 */   private TunnelState tunnelState = TunnelState.NONE;
/*      */ 
/*  368 */   private int connectTimeout = -1;
/*  369 */   private int readTimeout = -1;
/*      */ 
/*  372 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.net.www.protocol.http.HttpURLConnection");
/*      */ 
/* 2293 */   String requestURI = null;
/*      */ 
/* 2423 */   byte[] cdata = new byte[''];
/*      */   private static final String SET_COOKIE = "set-cookie";
/*      */   private static final String SET_COOKIE2 = "set-cookie2";
/*      */   private Map<String, List<String>> filteredHeaders;
/*      */ 
/*      */   private static PasswordAuthentication privilegedRequestPasswordAuthentication(String paramString1, final InetAddress paramInetAddress, final int paramInt, final String paramString2, final String paramString3, final String paramString4, final URL paramURL, final Authenticator.RequestorType paramRequestorType)
/*      */   {
/*  389 */     return (PasswordAuthentication)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public PasswordAuthentication run() {
/*  392 */         if (HttpURLConnection.logger.isLoggable(300)) {
/*  393 */           HttpURLConnection.logger.finest("Requesting Authentication: host =" + this.val$host + " url = " + paramURL);
/*      */         }
/*  395 */         PasswordAuthentication localPasswordAuthentication = Authenticator.requestPasswordAuthentication(this.val$host, paramInetAddress, paramInt, paramString2, paramString3, paramString4, paramURL, paramRequestorType);
/*      */ 
/*  398 */         if (HttpURLConnection.logger.isLoggable(300)) {
/*  399 */           HttpURLConnection.logger.finest("Authentication returned: " + (localPasswordAuthentication != null ? localPasswordAuthentication.toString() : "null"));
/*      */         }
/*  401 */         return localPasswordAuthentication;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private boolean isRestrictedHeader(String paramString1, String paramString2) {
/*  407 */     if (allowRestrictedHeaders) {
/*  408 */       return false;
/*      */     }
/*      */ 
/*  411 */     paramString1 = paramString1.toLowerCase();
/*  412 */     if (restrictedHeaderSet.contains(paramString1))
/*      */     {
/*  418 */       if ((paramString1.equals("connection")) && (paramString2.equalsIgnoreCase("close"))) {
/*  419 */         return false;
/*      */       }
/*  421 */       return true;
/*  422 */     }if (paramString1.startsWith("sec-")) {
/*  423 */       return true;
/*      */     }
/*  425 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isExternalMessageHeaderAllowed(String paramString1, String paramString2)
/*      */   {
/*  434 */     checkMessageHeader(paramString1, paramString2);
/*  435 */     if (!isRestrictedHeader(paramString1, paramString2)) {
/*  436 */       return true;
/*      */     }
/*  438 */     return false;
/*      */   }
/*      */ 
/*      */   public static PlatformLogger getHttpLogger()
/*      */   {
/*  443 */     return logger;
/*      */   }
/*      */ 
/*      */   public Object authObj()
/*      */   {
/*  448 */     return this.authObj;
/*      */   }
/*      */ 
/*      */   public void authObj(Object paramObject) {
/*  452 */     this.authObj = paramObject;
/*      */   }
/*      */ 
/*      */   private void checkMessageHeader(String paramString1, String paramString2)
/*      */   {
/*  460 */     int i = 10;
/*  461 */     int j = paramString1.indexOf(i);
/*  462 */     if (j != -1) {
/*  463 */       throw new IllegalArgumentException("Illegal character(s) in message header field: " + paramString1);
/*      */     }
/*      */ 
/*  467 */     if (paramString2 == null) {
/*  468 */       return;
/*      */     }
/*      */ 
/*  471 */     j = paramString2.indexOf(i);
/*  472 */     while (j != -1) {
/*  473 */       j++;
/*  474 */       if (j < paramString2.length()) {
/*  475 */         int k = paramString2.charAt(j);
/*  476 */         if ((k == 32) || (k == 9))
/*      */         {
/*  478 */           j = paramString2.indexOf(i, j);
/*      */         }
/*      */       }
/*      */       else {
/*  482 */         throw new IllegalArgumentException("Illegal character(s) in message header value: " + paramString2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeRequests()
/*      */     throws IOException
/*      */   {
/*  497 */     if ((this.http.usingProxy) && (tunnelState() != TunnelState.TUNNELING)) {
/*  498 */       setPreemptiveProxyAuthentication(this.requests);
/*      */     }
/*  500 */     if (!this.setRequests)
/*      */     {
/*  512 */       if (!this.failedOnce) {
/*  513 */         this.requests.prepend(this.method + " " + getRequestURI() + " " + "HTTP/1.1", null);
/*      */       }
/*  515 */       if (!getUseCaches()) {
/*  516 */         this.requests.setIfNotSet("Cache-Control", "no-cache");
/*  517 */         this.requests.setIfNotSet("Pragma", "no-cache");
/*      */       }
/*  519 */       this.requests.setIfNotSet("User-Agent", userAgent);
/*  520 */       int i = this.url.getPort();
/*  521 */       String str2 = this.url.getHost();
/*  522 */       if ((i != -1) && (i != this.url.getDefaultPort())) {
/*  523 */         str2 = str2 + ":" + String.valueOf(i);
/*      */       }
/*  525 */       this.requests.setIfNotSet("Host", str2);
/*  526 */       this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
/*      */ 
/*  536 */       if ((!this.failedOnce) && (this.http.getHttpKeepAliveSet())) {
/*  537 */         if ((this.http.usingProxy) && (tunnelState() != TunnelState.TUNNELING))
/*  538 */           this.requests.setIfNotSet("Proxy-Connection", "keep-alive");
/*      */         else {
/*  540 */           this.requests.setIfNotSet("Connection", "keep-alive");
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  549 */         this.requests.setIfNotSet("Connection", "close");
/*      */       }
/*      */ 
/*  552 */       long l = getIfModifiedSince();
/*  553 */       if (l != 0L) {
/*  554 */         localObject1 = new Date(l);
/*      */ 
/*  557 */         SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
/*      */ 
/*  559 */         localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  560 */         this.requests.setIfNotSet("If-Modified-Since", localSimpleDateFormat.format((Date)localObject1));
/*      */       }
/*      */ 
/*  563 */       Object localObject1 = AuthenticationInfo.getServerAuth(this.url);
/*  564 */       if ((localObject1 != null) && (((AuthenticationInfo)localObject1).supportsPreemptiveAuthorization()))
/*      */       {
/*  566 */         this.requests.setIfNotSet(((AuthenticationInfo)localObject1).getHeaderName(), ((AuthenticationInfo)localObject1).getHeaderValue(this.url, this.method));
/*  567 */         this.currentServerCredentials = ((AuthenticationInfo)localObject1);
/*      */       }
/*      */ 
/*  570 */       if ((!this.method.equals("PUT")) && ((this.poster != null) || (streaming()))) {
/*  571 */         this.requests.setIfNotSet("Content-type", "application/x-www-form-urlencoded");
/*      */       }
/*      */ 
/*  575 */       int k = 0;
/*      */ 
/*  577 */       if (streaming()) {
/*  578 */         if (this.chunkLength != -1) {
/*  579 */           this.requests.set("Transfer-Encoding", "chunked");
/*  580 */           k = 1;
/*      */         }
/*  582 */         else if (this.fixedContentLengthLong != -1L) {
/*  583 */           this.requests.set("Content-Length", String.valueOf(this.fixedContentLengthLong));
/*      */         }
/*  585 */         else if (this.fixedContentLength != -1) {
/*  586 */           this.requests.set("Content-Length", String.valueOf(this.fixedContentLength));
/*      */         }
/*      */ 
/*      */       }
/*  590 */       else if (this.poster != null)
/*      */       {
/*  592 */         synchronized (this.poster)
/*      */         {
/*  594 */           this.poster.close();
/*  595 */           this.requests.set("Content-Length", String.valueOf(this.poster.size()));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  600 */       if ((k == 0) && 
/*  601 */         (this.requests.findValue("Transfer-Encoding") != null)) {
/*  602 */         this.requests.remove("Transfer-Encoding");
/*  603 */         if (logger.isLoggable(900)) {
/*  604 */           logger.warning("use streaming mode for chunked encoding");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  612 */       setCookieHeader();
/*      */ 
/*  614 */       this.setRequests = true;
/*      */     }
/*  616 */     if (logger.isLoggable(500)) {
/*  617 */       logger.fine(this.requests.toString());
/*      */     }
/*  619 */     this.http.writeRequests(this.requests, this.poster, streaming());
/*  620 */     if (this.ps.checkError()) {
/*  621 */       String str1 = this.http.getProxyHostUsed();
/*  622 */       int j = this.http.getProxyPortUsed();
/*  623 */       disconnectInternal();
/*  624 */       if (this.failedOnce) {
/*  625 */         throw new IOException("Error writing to server");
/*      */       }
/*  627 */       this.failedOnce = true;
/*  628 */       if (str1 != null)
/*  629 */         setProxiedClient(this.url, str1, j);
/*      */       else {
/*  631 */         setNewClient(this.url);
/*      */       }
/*  633 */       this.ps = ((PrintStream)this.http.getOutputStream());
/*  634 */       this.connected = true;
/*  635 */       this.responses = new MessageHeader();
/*  636 */       this.setRequests = false;
/*  637 */       writeRequests();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setNewClient(URL paramURL)
/*      */     throws IOException
/*      */   {
/*  651 */     setNewClient(paramURL, false);
/*      */   }
/*      */ 
/*      */   protected void setNewClient(URL paramURL, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  663 */     this.http = HttpClient.New(paramURL, null, -1, paramBoolean, this.connectTimeout);
/*  664 */     this.http.setReadTimeout(this.readTimeout);
/*      */   }
/*      */ 
/*      */   protected void setProxiedClient(URL paramURL, String paramString, int paramInt)
/*      */     throws IOException
/*      */   {
/*  679 */     setProxiedClient(paramURL, paramString, paramInt, false);
/*      */   }
/*      */ 
/*      */   protected void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  697 */     proxiedConnect(paramURL, paramString, paramInt, paramBoolean);
/*      */   }
/*      */ 
/*      */   protected void proxiedConnect(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  704 */     this.http = HttpClient.New(paramURL, paramString, paramInt, paramBoolean, this.connectTimeout);
/*  705 */     this.http.setReadTimeout(this.readTimeout);
/*      */   }
/*      */ 
/*      */   protected HttpURLConnection(URL paramURL, Handler paramHandler)
/*      */     throws IOException
/*      */   {
/*  712 */     this(paramURL, null, paramHandler);
/*      */   }
/*      */ 
/*      */   public HttpURLConnection(URL paramURL, String paramString, int paramInt) {
/*  716 */     this(paramURL, new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(paramString, paramInt)));
/*      */   }
/*      */ 
/*      */   public HttpURLConnection(URL paramURL, Proxy paramProxy)
/*      */   {
/*  722 */     this(paramURL, paramProxy, new Handler());
/*      */   }
/*      */ 
/*      */   protected HttpURLConnection(URL paramURL, Proxy paramProxy, Handler paramHandler) {
/*  726 */     super(paramURL);
/*  727 */     this.requests = new MessageHeader();
/*  728 */     this.responses = new MessageHeader();
/*  729 */     this.handler = paramHandler;
/*  730 */     this.instProxy = paramProxy;
/*  731 */     if ((this.instProxy instanceof ApplicationProxy))
/*      */     {
/*      */       try
/*      */       {
/*  735 */         this.cookieHandler = CookieHandler.getDefault(); } catch (SecurityException localSecurityException) {
/*      */       }
/*      */     }
/*  738 */     else this.cookieHandler = ((CookieHandler)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public CookieHandler run() {
/*  741 */           return CookieHandler.getDefault();
/*      */         }
/*      */       }));
/*      */ 
/*  745 */     this.cacheHandler = ((ResponseCache)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public ResponseCache run() {
/*  748 */         return ResponseCache.getDefault();
/*      */       }
/*      */     }));
/*      */   }
/*      */ 
/*      */   public static void setDefaultAuthenticator(HttpAuthenticator paramHttpAuthenticator)
/*      */   {
/*  757 */     defaultAuth = paramHttpAuthenticator;
/*      */   }
/*      */ 
/*      */   public static InputStream openConnectionCheckRedirects(URLConnection paramURLConnection)
/*      */     throws IOException
/*      */   {
/*  767 */     int j = 0;
/*      */     InputStream localInputStream;
/*      */     int i;
/*      */     do
/*      */     {
/*  771 */       if ((paramURLConnection instanceof HttpURLConnection)) {
/*  772 */         ((HttpURLConnection)paramURLConnection).setInstanceFollowRedirects(false);
/*      */       }
/*      */ 
/*  778 */       localInputStream = paramURLConnection.getInputStream();
/*  779 */       i = 0;
/*      */ 
/*  781 */       if ((paramURLConnection instanceof HttpURLConnection)) {
/*  782 */         HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURLConnection;
/*  783 */         int k = localHttpURLConnection.getResponseCode();
/*  784 */         if ((k >= 300) && (k <= 307) && (k != 306) && (k != 304))
/*      */         {
/*  786 */           URL localURL1 = localHttpURLConnection.getURL();
/*  787 */           String str = localHttpURLConnection.getHeaderField("Location");
/*  788 */           URL localURL2 = null;
/*  789 */           if (str != null) {
/*  790 */             localURL2 = new URL(localURL1, str);
/*      */           }
/*  792 */           localHttpURLConnection.disconnect();
/*  793 */           if ((localURL2 == null) || (!localURL1.getProtocol().equals(localURL2.getProtocol())) || (localURL1.getPort() != localURL2.getPort()) || (!hostsEqual(localURL1, localURL2)) || (j >= 5))
/*      */           {
/*  799 */             throw new SecurityException("illegal URL redirect");
/*      */           }
/*  801 */           i = 1;
/*  802 */           paramURLConnection = localURL2.openConnection();
/*  803 */           j++;
/*      */         }
/*      */       }
/*      */     }
/*  806 */     while (i != 0);
/*  807 */     return localInputStream;
/*      */   }
/*      */ 
/*      */   private static boolean hostsEqual(URL paramURL1, URL paramURL2)
/*      */   {
/*  815 */     String str1 = paramURL1.getHost();
/*  816 */     final String str2 = paramURL2.getHost();
/*      */ 
/*  818 */     if (str1 == null)
/*  819 */       return str2 == null;
/*  820 */     if (str2 == null)
/*  821 */       return false;
/*  822 */     if (str1.equalsIgnoreCase(str2)) {
/*  823 */       return true;
/*      */     }
/*      */ 
/*  827 */     final boolean[] arrayOfBoolean = { false };
/*      */ 
/*  829 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Void run() {
/*      */         try {
/*  833 */           InetAddress localInetAddress1 = InetAddress.getByName(this.val$h1);
/*  834 */           InetAddress localInetAddress2 = InetAddress.getByName(str2);
/*  835 */           arrayOfBoolean[0] = localInetAddress1.equals(localInetAddress2);
/*      */         } catch (UnknownHostException localUnknownHostException) {
/*      */         } catch (SecurityException localSecurityException) {
/*      */         }
/*  839 */         return null;
/*      */       }
/*      */     });
/*  843 */     return arrayOfBoolean[0];
/*      */   }
/*      */ 
/*      */   public void connect()
/*      */     throws IOException
/*      */   {
/*  849 */     plainConnect();
/*      */   }
/*      */ 
/*      */   private boolean checkReuseConnection() {
/*  853 */     if (this.connected) {
/*  854 */       return true;
/*      */     }
/*  856 */     if (this.reuseClient != null) {
/*  857 */       this.http = this.reuseClient;
/*  858 */       this.http.setReadTimeout(getReadTimeout());
/*  859 */       this.http.reuse = false;
/*  860 */       this.reuseClient = null;
/*  861 */       this.connected = true;
/*  862 */       return true;
/*      */     }
/*  864 */     return false;
/*      */   }
/*      */ 
/*      */   protected void plainConnect() throws IOException {
/*  868 */     if (this.connected) {
/*  869 */       return;
/*      */     }
/*      */ 
/*  872 */     if ((this.cacheHandler != null) && (getUseCaches())) {
/*      */       try {
/*  874 */         URI localURI1 = ParseUtil.toURI(this.url);
/*  875 */         if (localURI1 != null) {
/*  876 */           this.cachedResponse = this.cacheHandler.get(localURI1, getRequestMethod(), this.requests.getHeaders(EXCLUDE_HEADERS));
/*  877 */           if (("https".equalsIgnoreCase(localURI1.getScheme())) && (!(this.cachedResponse instanceof SecureCacheResponse)))
/*      */           {
/*  879 */             this.cachedResponse = null;
/*      */           }
/*  881 */           if (logger.isLoggable(300)) {
/*  882 */             logger.finest("Cache Request for " + localURI1 + " / " + getRequestMethod());
/*  883 */             logger.finest("From cache: " + (this.cachedResponse != null ? this.cachedResponse.toString() : "null"));
/*      */           }
/*  885 */           if (this.cachedResponse != null) {
/*  886 */             this.cachedHeaders = mapToMessageHeader(this.cachedResponse.getHeaders());
/*  887 */             this.cachedInputStream = this.cachedResponse.getBody();
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException1) {
/*      */       }
/*  893 */       if ((this.cachedHeaders != null) && (this.cachedInputStream != null)) {
/*  894 */         this.connected = true;
/*  895 */         return;
/*      */       }
/*  897 */       this.cachedResponse = null;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  909 */       if (this.instProxy == null)
/*      */       {
/*  913 */         ProxySelector localProxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public ProxySelector run()
/*      */           {
/*  917 */             return ProxySelector.getDefault();
/*      */           }
/*      */         });
/*  920 */         if (localProxySelector != null) {
/*  921 */           URI localURI2 = ParseUtil.toURI(this.url);
/*  922 */           if (logger.isLoggable(300)) {
/*  923 */             logger.finest("ProxySelector Request for " + localURI2);
/*      */           }
/*  925 */           Iterator localIterator = localProxySelector.select(localURI2).iterator();
/*      */ 
/*  927 */           while (localIterator.hasNext()) {
/*  928 */             Proxy localProxy = (Proxy)localIterator.next();
/*      */             try {
/*  930 */               if (!this.failedOnce) {
/*  931 */                 this.http = getNewHttpClient(this.url, localProxy, this.connectTimeout);
/*  932 */                 this.http.setReadTimeout(this.readTimeout);
/*      */               }
/*      */               else
/*      */               {
/*  936 */                 this.http = getNewHttpClient(this.url, localProxy, this.connectTimeout, false);
/*  937 */                 this.http.setReadTimeout(this.readTimeout);
/*      */               }
/*  939 */               if ((logger.isLoggable(300)) && 
/*  940 */                 (localProxy != null)) {
/*  941 */                 logger.finest("Proxy used: " + localProxy.toString());
/*      */               }
/*      */             }
/*      */             catch (IOException localIOException3)
/*      */             {
/*  946 */               if (localProxy != Proxy.NO_PROXY) {
/*  947 */                 localProxySelector.connectFailed(localURI2, localProxy.address(), localIOException3);
/*  948 */                 if (!localIterator.hasNext())
/*      */                 {
/*  950 */                   this.http = getNewHttpClient(this.url, null, this.connectTimeout, false);
/*  951 */                   this.http.setReadTimeout(this.readTimeout);
/*  952 */                   break;
/*      */                 }
/*      */               } else {
/*  955 */                 throw localIOException3;
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*  962 */         else if (!this.failedOnce) {
/*  963 */           this.http = getNewHttpClient(this.url, null, this.connectTimeout);
/*  964 */           this.http.setReadTimeout(this.readTimeout);
/*      */         }
/*      */         else
/*      */         {
/*  968 */           this.http = getNewHttpClient(this.url, null, this.connectTimeout, false);
/*  969 */           this.http.setReadTimeout(this.readTimeout);
/*      */         }
/*      */ 
/*      */       }
/*  973 */       else if (!this.failedOnce) {
/*  974 */         this.http = getNewHttpClient(this.url, this.instProxy, this.connectTimeout);
/*  975 */         this.http.setReadTimeout(this.readTimeout);
/*      */       }
/*      */       else
/*      */       {
/*  979 */         this.http = getNewHttpClient(this.url, this.instProxy, this.connectTimeout, false);
/*  980 */         this.http.setReadTimeout(this.readTimeout);
/*      */       }
/*      */ 
/*  984 */       this.ps = ((PrintStream)this.http.getOutputStream());
/*      */     } catch (IOException localIOException2) {
/*  986 */       throw localIOException2;
/*      */     }
/*      */ 
/*  989 */     this.connected = true;
/*      */   }
/*      */ 
/*      */   protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt)
/*      */     throws IOException
/*      */   {
/*  995 */     return HttpClient.New(paramURL, paramProxy, paramInt);
/*      */   }
/*      */ 
/*      */   protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1002 */     return HttpClient.New(paramURL, paramProxy, paramInt, paramBoolean);
/*      */   }
/*      */ 
/*      */   private void expect100Continue()
/*      */     throws IOException
/*      */   {
/* 1008 */     int i = this.http.getReadTimeout();
/* 1009 */     int j = 0;
/* 1010 */     int k = 0;
/* 1011 */     if (i <= 0)
/*      */     {
/* 1014 */       this.http.setReadTimeout(5000);
/* 1015 */       j = 1;
/*      */     }
/*      */     try
/*      */     {
/* 1019 */       this.http.parseHTTP(this.responses, this.pi, this);
/*      */     } catch (SocketTimeoutException localSocketTimeoutException) {
/* 1021 */       if (j == 0) {
/* 1022 */         throw localSocketTimeoutException;
/*      */       }
/* 1024 */       k = 1;
/* 1025 */       this.http.setIgnoreContinue(true);
/*      */     }
/* 1027 */     if (k == 0)
/*      */     {
/* 1029 */       String str = this.responses.getValue(0);
/*      */ 
/* 1033 */       if ((str != null) && (str.startsWith("HTTP/"))) {
/* 1034 */         String[] arrayOfString = str.split("\\s+");
/* 1035 */         this.responseCode = -1;
/*      */         try
/*      */         {
/* 1038 */           if (arrayOfString.length > 1)
/* 1039 */             this.responseCode = Integer.parseInt(arrayOfString[1]);
/*      */         } catch (NumberFormatException localNumberFormatException) {
/*      */         }
/*      */       }
/* 1043 */       if (this.responseCode != 100) {
/* 1044 */         throw new ProtocolException("Server rejected operation");
/*      */       }
/*      */     }
/*      */ 
/* 1048 */     this.http.setReadTimeout(i);
/*      */ 
/* 1050 */     this.responseCode = -1;
/* 1051 */     this.responses.reset();
/*      */   }
/*      */ 
/*      */   public synchronized OutputStream getOutputStream()
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/* 1070 */       if (!this.doOutput) {
/* 1071 */         throw new ProtocolException("cannot write to a URLConnection if doOutput=false - call setDoOutput(true)");
/*      */       }
/*      */ 
/* 1075 */       if (this.method.equals("GET")) {
/* 1076 */         this.method = "POST";
/*      */       }
/* 1078 */       if ((!"POST".equals(this.method)) && (!"PUT".equals(this.method)) && ("http".equals(this.url.getProtocol())))
/*      */       {
/* 1080 */         throw new ProtocolException("HTTP method " + this.method + " doesn't support output");
/*      */       }
/*      */ 
/* 1085 */       if (this.inputStream != null) {
/* 1086 */         throw new ProtocolException("Cannot write output after reading input.");
/*      */       }
/*      */ 
/* 1089 */       if (!checkReuseConnection()) {
/* 1090 */         connect();
/*      */       }
/* 1092 */       int i = 0;
/* 1093 */       String str = this.requests.findValue("Expect");
/* 1094 */       if ("100-Continue".equalsIgnoreCase(str)) {
/* 1095 */         this.http.setIgnoreContinue(false);
/* 1096 */         i = 1;
/*      */       }
/*      */ 
/* 1099 */       if ((streaming()) && (this.strOutputStream == null)) {
/* 1100 */         writeRequests();
/*      */       }
/*      */ 
/* 1103 */       if (i != 0) {
/* 1104 */         expect100Continue();
/*      */       }
/* 1106 */       this.ps = ((PrintStream)this.http.getOutputStream());
/* 1107 */       if (streaming()) {
/* 1108 */         if (this.strOutputStream == null) {
/* 1109 */           if (this.chunkLength != -1) {
/* 1110 */             this.strOutputStream = new StreamingOutputStream(new ChunkedOutputStream(this.ps, this.chunkLength), -1L);
/*      */           }
/*      */           else {
/* 1113 */             long l = 0L;
/* 1114 */             if (this.fixedContentLengthLong != -1L)
/* 1115 */               l = this.fixedContentLengthLong;
/* 1116 */             else if (this.fixedContentLength != -1) {
/* 1117 */               l = this.fixedContentLength;
/*      */             }
/* 1119 */             this.strOutputStream = new StreamingOutputStream(this.ps, l);
/*      */           }
/*      */         }
/* 1122 */         return this.strOutputStream;
/*      */       }
/* 1124 */       if (this.poster == null) {
/* 1125 */         this.poster = new PosterOutputStream();
/*      */       }
/* 1127 */       return this.poster;
/*      */     }
/*      */     catch (RuntimeException localRuntimeException) {
/* 1130 */       disconnectInternal();
/* 1131 */       throw localRuntimeException;
/*      */     }
/*      */     catch (ProtocolException localProtocolException)
/*      */     {
/* 1135 */       int j = this.responseCode;
/* 1136 */       disconnectInternal();
/* 1137 */       this.responseCode = j;
/* 1138 */       throw localProtocolException;
/*      */     } catch (IOException localIOException) {
/* 1140 */       disconnectInternal();
/* 1141 */       throw localIOException;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean streaming() {
/* 1146 */     return (this.fixedContentLength != -1) || (this.fixedContentLengthLong != -1L) || (this.chunkLength != -1);
/*      */   }
/*      */ 
/*      */   private void setCookieHeader()
/*      */     throws IOException
/*      */   {
/* 1155 */     if (this.cookieHandler != null)
/*      */     {
/* 1159 */       synchronized (this) {
/* 1160 */         if (this.setUserCookies) {
/* 1161 */           int i = this.requests.getKey("Cookie");
/* 1162 */           if (i != -1)
/* 1163 */             this.userCookies = this.requests.getValue(i);
/* 1164 */           i = this.requests.getKey("Cookie2");
/* 1165 */           if (i != -1)
/* 1166 */             this.userCookies2 = this.requests.getValue(i);
/* 1167 */           this.setUserCookies = false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1172 */       this.requests.remove("Cookie");
/* 1173 */       this.requests.remove("Cookie2");
/*      */ 
/* 1175 */       ??? = ParseUtil.toURI(this.url);
/* 1176 */       if (??? != null) {
/* 1177 */         if (logger.isLoggable(300)) {
/* 1178 */           logger.finest("CookieHandler request for " + ???);
/*      */         }
/* 1180 */         Map localMap = this.cookieHandler.get((URI)???, this.requests.getHeaders(EXCLUDE_HEADERS));
/*      */ 
/* 1183 */         if (!localMap.isEmpty()) {
/* 1184 */           if (logger.isLoggable(300)) {
/* 1185 */             logger.finest("Cookies retrieved: " + localMap.toString());
/*      */           }
/*      */ 
/* 1188 */           for (Map.Entry localEntry : localMap.entrySet()) {
/* 1189 */             String str1 = (String)localEntry.getKey();
/*      */ 
/* 1192 */             if (("Cookie".equalsIgnoreCase(str1)) || ("Cookie2".equalsIgnoreCase(str1)))
/*      */             {
/* 1196 */               List localList = (List)localEntry.getValue();
/* 1197 */               if ((localList != null) && (!localList.isEmpty())) {
/* 1198 */                 StringBuilder localStringBuilder = new StringBuilder();
/* 1199 */                 for (String str2 : localList) {
/* 1200 */                   localStringBuilder.append(str2).append("; ");
/*      */                 }
/*      */                 try
/*      */                 {
/* 1204 */                   this.requests.add(str1, localStringBuilder.substring(0, localStringBuilder.length() - 2));
/*      */                 }
/*      */                 catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
/*      */                 {
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       int j;
/* 1212 */       if (this.userCookies != null)
/*      */       {
/* 1214 */         if ((j = this.requests.getKey("Cookie")) != -1)
/* 1215 */           this.requests.set("Cookie", this.requests.getValue(j) + ";" + this.userCookies);
/*      */         else
/* 1217 */           this.requests.set("Cookie", this.userCookies);
/*      */       }
/* 1219 */       if (this.userCookies2 != null)
/*      */       {
/* 1221 */         if ((j = this.requests.getKey("Cookie2")) != -1)
/* 1222 */           this.requests.set("Cookie2", this.requests.getValue(j) + ";" + this.userCookies2);
/*      */         else
/* 1224 */           this.requests.set("Cookie2", this.userCookies2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized InputStream getInputStream()
/*      */     throws IOException
/*      */   {
/* 1234 */     if (!this.doInput) {
/* 1235 */       throw new ProtocolException("Cannot read from URLConnection if doInput=false (call setDoInput(true))");
/*      */     }
/*      */ 
/* 1239 */     if (this.rememberedException != null) {
/* 1240 */       if ((this.rememberedException instanceof RuntimeException)) {
/* 1241 */         throw new RuntimeException(this.rememberedException);
/*      */       }
/* 1243 */       throw getChainedException((IOException)this.rememberedException);
/*      */     }
/*      */ 
/* 1247 */     if (this.inputStream != null) {
/* 1248 */       return this.inputStream;
/*      */     }
/*      */ 
/* 1251 */     if (streaming()) {
/* 1252 */       if (this.strOutputStream == null) {
/* 1253 */         getOutputStream();
/*      */       }
/*      */ 
/* 1256 */       this.strOutputStream.close();
/* 1257 */       if (!this.strOutputStream.writtenOK()) {
/* 1258 */         throw new IOException("Incomplete output stream");
/*      */       }
/*      */     }
/*      */ 
/* 1262 */     int i = 0;
/* 1263 */     int j = 0;
/* 1264 */     long l = -1L;
/* 1265 */     Object localObject1 = null;
/* 1266 */     AuthenticationInfo localAuthenticationInfo = null;
/* 1267 */     AuthenticationHeader localAuthenticationHeader = null;
/*      */ 
/* 1289 */     int k = 0;
/* 1290 */     int m = 0;
/*      */ 
/* 1293 */     this.isUserServerAuth = (this.requests.getKey("Authorization") != -1);
/* 1294 */     this.isUserProxyAuth = (this.requests.getKey("Proxy-Authorization") != -1);
/*      */     try
/*      */     {
/*      */       do {
/* 1298 */         if (!checkReuseConnection()) {
/* 1299 */           connect();
/*      */         }
/* 1301 */         if (this.cachedInputStream != null) {
/* 1302 */           return this.cachedInputStream;
/*      */         }
/*      */ 
/* 1306 */         boolean bool1 = ProgressMonitor.getDefault().shouldMeterInput(this.url, this.method);
/*      */ 
/* 1308 */         if (bool1) {
/* 1309 */           this.pi = new ProgressSource(this.url, this.method);
/* 1310 */           this.pi.beginTracking();
/*      */         }
/*      */ 
/* 1317 */         this.ps = ((PrintStream)this.http.getOutputStream());
/*      */ 
/* 1319 */         if (!streaming()) {
/* 1320 */           writeRequests();
/*      */         }
/* 1322 */         this.http.parseHTTP(this.responses, this.pi, this);
/* 1323 */         if (logger.isLoggable(500)) {
/* 1324 */           logger.fine(this.responses.toString());
/*      */         }
/*      */ 
/* 1327 */         boolean bool2 = this.responses.filterNTLMResponses("WWW-Authenticate");
/* 1328 */         boolean bool3 = this.responses.filterNTLMResponses("Proxy-Authenticate");
/* 1329 */         if (((bool2) || (bool3)) && 
/* 1330 */           (logger.isLoggable(500))) {
/* 1331 */           logger.fine(">>>> Headers are filtered");
/* 1332 */           logger.fine(this.responses.toString());
/*      */         }
/*      */ 
/* 1336 */         this.inputStream = this.http.getInputStream();
/*      */ 
/* 1338 */         j = getResponseCode();
/* 1339 */         if (j == -1) {
/* 1340 */           disconnectInternal();
/* 1341 */           throw new IOException("Invalid Http response");
/*      */         }
/*      */         boolean bool4;
/*      */         Object localObject4;
/*      */         Object localObject5;
/*      */         Object localObject6;
/* 1343 */         if (j == 407) {
/* 1344 */           if (streaming()) {
/* 1345 */             disconnectInternal();
/* 1346 */             throw new HttpRetryException("cannot retry due to proxy authentication, in streaming mode", 407);
/*      */           }
/*      */ 
/* 1351 */           bool4 = false;
/* 1352 */           localObject4 = this.responses.multiValueIterator("Proxy-Authenticate");
/* 1353 */           while (((Iterator)localObject4).hasNext()) {
/* 1354 */             localObject5 = ((String)((Iterator)localObject4).next()).trim();
/* 1355 */             if ((((String)localObject5).equalsIgnoreCase("Negotiate")) || (((String)localObject5).equalsIgnoreCase("Kerberos")))
/*      */             {
/* 1357 */               if (m == 0) {
/* 1358 */                 m = 1; break;
/*      */               }
/* 1360 */               bool4 = true;
/* 1361 */               this.doingNTLMp2ndStage = false;
/* 1362 */               localAuthenticationInfo = null;
/*      */ 
/* 1364 */               break;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1374 */           localObject5 = new AuthenticationHeader("Proxy-Authenticate", this.responses, new HttpCallerInfo(this.url, this.http.getProxyHostUsed(), this.http.getProxyPortUsed()), bool4);
/*      */ 
/* 1381 */           if (!this.doingNTLMp2ndStage) {
/* 1382 */             localAuthenticationInfo = resetProxyAuthentication(localAuthenticationInfo, (AuthenticationHeader)localObject5);
/*      */ 
/* 1384 */             if (localAuthenticationInfo != null) {
/* 1385 */               i++;
/* 1386 */               disconnectInternal();
/* 1387 */               continue;
/*      */             }
/*      */           }
/*      */           else {
/* 1391 */             localObject6 = this.responses.findValue("Proxy-Authenticate");
/* 1392 */             reset();
/* 1393 */             if (!localAuthenticationInfo.setHeaders(this, ((AuthenticationHeader)localObject5).headerParser(), (String)localObject6))
/*      */             {
/* 1395 */               disconnectInternal();
/* 1396 */               throw new IOException("Authentication failure");
/*      */             }
/* 1398 */             if ((localObject1 != null) && (localAuthenticationHeader != null) && (!((AuthenticationInfo)localObject1).setHeaders(this, localAuthenticationHeader.headerParser(), (String)localObject6)))
/*      */             {
/* 1401 */               disconnectInternal();
/* 1402 */               throw new IOException("Authentication failure");
/*      */             }
/* 1404 */             this.authObj = null;
/* 1405 */             this.doingNTLMp2ndStage = false;
/* 1406 */             continue;
/*      */           }
/*      */         } else {
/* 1409 */           m = 0;
/* 1410 */           this.doingNTLMp2ndStage = false;
/* 1411 */           if (!this.isUserProxyAuth) {
/* 1412 */             this.requests.remove("Proxy-Authorization");
/*      */           }
/*      */         }
/*      */ 
/* 1416 */         if (localAuthenticationInfo != null)
/*      */         {
/* 1418 */           localAuthenticationInfo.addToCache();
/*      */         }
/*      */ 
/* 1421 */         if (j == 401) {
/* 1422 */           if (streaming()) {
/* 1423 */             disconnectInternal();
/* 1424 */             throw new HttpRetryException("cannot retry due to server authentication, in streaming mode", 401);
/*      */           }
/*      */ 
/* 1429 */           bool4 = false;
/* 1430 */           localObject4 = this.responses.multiValueIterator("WWW-Authenticate");
/* 1431 */           while (((Iterator)localObject4).hasNext()) {
/* 1432 */             localObject5 = ((String)((Iterator)localObject4).next()).trim();
/* 1433 */             if ((((String)localObject5).equalsIgnoreCase("Negotiate")) || (((String)localObject5).equalsIgnoreCase("Kerberos")))
/*      */             {
/* 1435 */               if (k == 0) {
/* 1436 */                 k = 1; break;
/*      */               }
/* 1438 */               bool4 = true;
/* 1439 */               this.doingNTLM2ndStage = false;
/* 1440 */               localObject1 = null;
/*      */ 
/* 1442 */               break;
/*      */             }
/*      */           }
/*      */ 
/* 1446 */           localAuthenticationHeader = new AuthenticationHeader("WWW-Authenticate", this.responses, new HttpCallerInfo(this.url), bool4);
/*      */ 
/* 1452 */           localObject5 = localAuthenticationHeader.raw();
/* 1453 */           if (!this.doingNTLM2ndStage) {
/* 1454 */             if ((localObject1 != null) && (((AuthenticationInfo)localObject1).getAuthScheme() != AuthScheme.NTLM))
/*      */             {
/* 1456 */               if (((AuthenticationInfo)localObject1).isAuthorizationStale((String)localObject5))
/*      */               {
/* 1458 */                 disconnectWeb();
/* 1459 */                 i++;
/* 1460 */                 this.requests.set(((AuthenticationInfo)localObject1).getHeaderName(), ((AuthenticationInfo)localObject1).getHeaderValue(this.url, this.method));
/*      */ 
/* 1462 */                 this.currentServerCredentials = ((AuthenticationInfo)localObject1);
/* 1463 */                 setCookieHeader();
/* 1464 */                 continue;
/*      */               }
/* 1466 */               ((AuthenticationInfo)localObject1).removeFromCache();
/*      */             }
/*      */ 
/* 1469 */             localObject1 = getServerAuthentication(localAuthenticationHeader);
/* 1470 */             this.currentServerCredentials = ((AuthenticationInfo)localObject1);
/*      */ 
/* 1472 */             if (localObject1 != null) {
/* 1473 */               disconnectWeb();
/* 1474 */               i++;
/* 1475 */               setCookieHeader();
/* 1476 */               continue;
/*      */             }
/*      */           } else {
/* 1479 */             reset();
/*      */ 
/* 1481 */             if (!((AuthenticationInfo)localObject1).setHeaders(this, null, (String)localObject5)) {
/* 1482 */               disconnectWeb();
/* 1483 */               throw new IOException("Authentication failure");
/*      */             }
/* 1485 */             this.doingNTLM2ndStage = false;
/* 1486 */             this.authObj = null;
/* 1487 */             setCookieHeader();
/* 1488 */             continue;
/*      */           }
/*      */         }
/*      */ 
/* 1492 */         if (localObject1 != null)
/*      */         {
/*      */           Object localObject2;
/* 1494 */           if ((!(localObject1 instanceof DigestAuthentication)) || (this.domain == null))
/*      */           {
/* 1496 */             if ((localObject1 instanceof BasicAuthentication))
/*      */             {
/* 1498 */               localObject2 = AuthenticationInfo.reducePath(this.url.getPath());
/* 1499 */               localObject4 = ((AuthenticationInfo)localObject1).path;
/* 1500 */               if ((!((String)localObject4).startsWith((String)localObject2)) || (((String)localObject2).length() >= ((String)localObject4).length()))
/*      */               {
/* 1502 */                 localObject2 = BasicAuthentication.getRootPath((String)localObject4, (String)localObject2);
/*      */               }
/*      */ 
/* 1505 */               localObject5 = (BasicAuthentication)((AuthenticationInfo)localObject1).clone();
/*      */ 
/* 1507 */               ((AuthenticationInfo)localObject1).removeFromCache();
/* 1508 */               ((BasicAuthentication)localObject5).path = ((String)localObject2);
/* 1509 */               localObject1 = localObject5;
/*      */             }
/* 1511 */             ((AuthenticationInfo)localObject1).addToCache();
/*      */           }
/*      */           else {
/* 1514 */             localObject2 = (DigestAuthentication)localObject1;
/*      */ 
/* 1516 */             localObject4 = new StringTokenizer(this.domain, " ");
/* 1517 */             localObject5 = ((DigestAuthentication)localObject2).realm;
/* 1518 */             localObject6 = ((DigestAuthentication)localObject2).pw;
/* 1519 */             this.digestparams = ((DigestAuthentication)localObject2).params;
/* 1520 */             while (((StringTokenizer)localObject4).hasMoreTokens()) {
/* 1521 */               String str2 = ((StringTokenizer)localObject4).nextToken();
/*      */               try
/*      */               {
/* 1524 */                 URL localURL = new URL(this.url, str2);
/* 1525 */                 DigestAuthentication localDigestAuthentication = new DigestAuthentication(false, localURL, (String)localObject5, "Digest", (PasswordAuthentication)localObject6, this.digestparams);
/*      */ 
/* 1527 */                 localDigestAuthentication.addToCache();
/*      */               }
/*      */               catch (Exception localException2)
/*      */               {
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1536 */         k = 0;
/* 1537 */         m = 0;
/*      */ 
/* 1540 */         this.doingNTLMp2ndStage = false;
/* 1541 */         this.doingNTLM2ndStage = false;
/* 1542 */         if (!this.isUserServerAuth)
/* 1543 */           this.requests.remove("Authorization");
/* 1544 */         if (!this.isUserProxyAuth) {
/* 1545 */           this.requests.remove("Proxy-Authorization");
/*      */         }
/* 1547 */         if (j == 200)
/* 1548 */           checkResponseCredentials(false);
/*      */         else {
/* 1550 */           this.needToCheck = false;
/*      */         }
/*      */ 
/* 1554 */         this.needToCheck = true;
/*      */ 
/* 1556 */         if (followRedirect())
/*      */         {
/* 1561 */           i++;
/*      */ 
/* 1565 */           setCookieHeader();
/*      */         }
/*      */         else
/*      */         {
/*      */           try
/*      */           {
/* 1571 */             l = Long.parseLong(this.responses.findValue("content-length"));
/*      */           } catch (Exception localException1) {
/*      */           }
/* 1574 */           if ((this.method.equals("HEAD")) || (l == 0L) || (j == 304) || (j == 204))
/*      */           {
/* 1578 */             if (this.pi != null) {
/* 1579 */               this.pi.finishTracking();
/* 1580 */               this.pi = null;
/*      */             }
/* 1582 */             this.http.finished();
/* 1583 */             this.http = null;
/* 1584 */             this.inputStream = new EmptyInputStream();
/* 1585 */             this.connected = false;
/*      */           }
/*      */           Object localObject3;
/* 1588 */           if ((j == 200) || (j == 203) || (j == 206) || (j == 300) || (j == 301) || (j == 410))
/*      */           {
/* 1590 */             if (this.cacheHandler != null)
/*      */             {
/* 1592 */               localObject3 = ParseUtil.toURI(this.url);
/* 1593 */               if (localObject3 != null) {
/* 1594 */                 localObject4 = this;
/* 1595 */                 if ("https".equalsIgnoreCase(((URI)localObject3).getScheme()))
/*      */                 {
/*      */                   try
/*      */                   {
/* 1600 */                     localObject4 = (URLConnection)getClass().getField("httpsURLConnection").get(this);
/*      */                   }
/*      */                   catch (IllegalAccessException localIllegalAccessException) {
/*      */                   }
/*      */                   catch (NoSuchFieldException localNoSuchFieldException) {
/*      */                   }
/*      */                 }
/* 1607 */                 CacheRequest localCacheRequest = this.cacheHandler.put((URI)localObject3, (URLConnection)localObject4);
/*      */ 
/* 1609 */                 if ((localCacheRequest != null) && (this.http != null)) {
/* 1610 */                   this.http.setCacheRequest(localCacheRequest);
/* 1611 */                   this.inputStream = new HttpInputStream(this.inputStream, localCacheRequest);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 1617 */           if (!(this.inputStream instanceof HttpInputStream)) {
/* 1618 */             this.inputStream = new HttpInputStream(this.inputStream);
/*      */           }
/*      */ 
/* 1621 */           if (j >= 400) {
/* 1622 */             if ((j == 404) || (j == 410)) {
/* 1623 */               throw new FileNotFoundException(this.url.toString());
/*      */             }
/* 1625 */             throw new IOException("Server returned HTTP response code: " + j + " for URL: " + this.url.toString());
/*      */           }
/*      */ 
/* 1630 */           this.poster = null;
/* 1631 */           this.strOutputStream = null;
/* 1632 */           return this.inputStream; } 
/* 1633 */       }while (i < maxRedirects);
/*      */ 
/* 1635 */       throw new ProtocolException("Server redirected too many  times (" + i + ")");
/*      */     }
/*      */     catch (RuntimeException localRuntimeException) {
/* 1638 */       disconnectInternal();
/* 1639 */       this.rememberedException = localRuntimeException;
/* 1640 */       throw localRuntimeException;
/*      */     } catch (IOException localIOException) {
/* 1642 */       this.rememberedException = localIOException;
/*      */ 
/* 1646 */       String str1 = this.responses.findValue("Transfer-Encoding");
/* 1647 */       if ((this.http != null) && (this.http.isKeepingAlive()) && (enableESBuffer) && ((l > 0L) || ((str1 != null) && (str1.equalsIgnoreCase("chunked")))))
/*      */       {
/* 1649 */         this.errorStream = ErrorStream.getErrorStream(this.inputStream, l, this.http);
/*      */       }
/* 1651 */       throw localIOException;
/*      */     } finally {
/* 1653 */       if (this.proxyAuthKey != null) {
/* 1654 */         AuthenticationInfo.endAuthRequest(this.proxyAuthKey);
/*      */       }
/* 1656 */       if (this.serverAuthKey != null)
/* 1657 */         AuthenticationInfo.endAuthRequest(this.serverAuthKey);
/*      */     }
/*      */   }
/*      */ 
/*      */   private IOException getChainedException(final IOException paramIOException)
/*      */   {
/*      */     try
/*      */     {
/* 1669 */       final Object[] arrayOfObject = { paramIOException.getMessage() };
/* 1670 */       IOException localIOException = (IOException)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public IOException run() throws Exception
/*      */         {
/* 1674 */           return (IOException)paramIOException.getClass().getConstructor(new Class[] { String.class }).newInstance(arrayOfObject);
/*      */         }
/*      */       });
/* 1680 */       localIOException.initCause(paramIOException);
/* 1681 */       return localIOException; } catch (Exception localException) {
/*      */     }
/* 1683 */     return paramIOException;
/*      */   }
/*      */ 
/*      */   public InputStream getErrorStream()
/*      */   {
/* 1689 */     if ((this.connected) && (this.responseCode >= 400))
/*      */     {
/* 1691 */       if (this.errorStream != null)
/* 1692 */         return this.errorStream;
/* 1693 */       if (this.inputStream != null) {
/* 1694 */         return this.inputStream;
/*      */       }
/*      */     }
/* 1697 */     return null;
/*      */   }
/*      */ 
/*      */   private AuthenticationInfo resetProxyAuthentication(AuthenticationInfo paramAuthenticationInfo, AuthenticationHeader paramAuthenticationHeader)
/*      */     throws IOException
/*      */   {
/* 1708 */     if ((paramAuthenticationInfo != null) && (paramAuthenticationInfo.getAuthScheme() != AuthScheme.NTLM))
/*      */     {
/* 1710 */       String str1 = paramAuthenticationHeader.raw();
/* 1711 */       if (paramAuthenticationInfo.isAuthorizationStale(str1))
/*      */       {
/*      */         String str2;
/* 1714 */         if ((paramAuthenticationInfo instanceof DigestAuthentication)) {
/* 1715 */           DigestAuthentication localDigestAuthentication = (DigestAuthentication)paramAuthenticationInfo;
/*      */ 
/* 1717 */           if (tunnelState() == TunnelState.SETUP)
/* 1718 */             str2 = localDigestAuthentication.getHeaderValue(connectRequestURI(this.url), HTTP_CONNECT);
/*      */           else
/* 1720 */             str2 = localDigestAuthentication.getHeaderValue(getRequestURI(), this.method);
/*      */         }
/*      */         else {
/* 1723 */           str2 = paramAuthenticationInfo.getHeaderValue(this.url, this.method);
/*      */         }
/* 1725 */         this.requests.set(paramAuthenticationInfo.getHeaderName(), str2);
/* 1726 */         this.currentProxyCredentials = paramAuthenticationInfo;
/* 1727 */         return paramAuthenticationInfo;
/*      */       }
/* 1729 */       paramAuthenticationInfo.removeFromCache();
/*      */     }
/*      */ 
/* 1732 */     paramAuthenticationInfo = getHttpProxyAuthentication(paramAuthenticationHeader);
/* 1733 */     this.currentProxyCredentials = paramAuthenticationInfo;
/* 1734 */     return paramAuthenticationInfo;
/*      */   }
/*      */ 
/*      */   TunnelState tunnelState()
/*      */   {
/* 1743 */     return this.tunnelState;
/*      */   }
/*      */ 
/*      */   void setTunnelState(TunnelState paramTunnelState)
/*      */   {
/* 1752 */     this.tunnelState = paramTunnelState;
/*      */   }
/*      */ 
/*      */   public synchronized void doTunneling()
/*      */     throws IOException
/*      */   {
/* 1759 */     int i = 0;
/* 1760 */     String str1 = "";
/* 1761 */     int j = 0;
/* 1762 */     AuthenticationInfo localAuthenticationInfo = null;
/* 1763 */     String str2 = null;
/* 1764 */     int k = -1;
/*      */ 
/* 1767 */     MessageHeader localMessageHeader = this.requests;
/* 1768 */     this.requests = new MessageHeader();
/*      */ 
/* 1771 */     int m = 0;
/*      */     try
/*      */     {
/* 1775 */       setTunnelState(TunnelState.SETUP);
/*      */       do
/*      */       {
/* 1778 */         if (!checkReuseConnection()) {
/* 1779 */           proxiedConnect(this.url, str2, k, false);
/*      */         }
/*      */ 
/* 1783 */         sendCONNECTRequest();
/* 1784 */         this.responses.reset();
/*      */ 
/* 1788 */         this.http.parseHTTP(this.responses, null, this);
/*      */ 
/* 1791 */         if (logger.isLoggable(500)) {
/* 1792 */           logger.fine(this.responses.toString());
/*      */         }
/*      */ 
/* 1795 */         if ((this.responses.filterNTLMResponses("Proxy-Authenticate")) && 
/* 1796 */           (logger.isLoggable(500))) {
/* 1797 */           logger.fine(">>>> Headers are filtered");
/* 1798 */           logger.fine(this.responses.toString());
/*      */         }
/*      */ 
/* 1802 */         str1 = this.responses.getValue(0);
/* 1803 */         StringTokenizer localStringTokenizer = new StringTokenizer(str1);
/* 1804 */         localStringTokenizer.nextToken();
/* 1805 */         j = Integer.parseInt(localStringTokenizer.nextToken().trim());
/* 1806 */         if (j == 407)
/*      */         {
/* 1808 */           boolean bool = false;
/* 1809 */           Iterator localIterator = this.responses.multiValueIterator("Proxy-Authenticate");
/* 1810 */           while (localIterator.hasNext()) {
/* 1811 */             localObject1 = ((String)localIterator.next()).trim();
/* 1812 */             if ((((String)localObject1).equalsIgnoreCase("Negotiate")) || (((String)localObject1).equalsIgnoreCase("Kerberos")))
/*      */             {
/* 1814 */               if (m == 0) {
/* 1815 */                 m = 1; break;
/*      */               }
/* 1817 */               bool = true;
/* 1818 */               this.doingNTLMp2ndStage = false;
/* 1819 */               localAuthenticationInfo = null;
/*      */ 
/* 1821 */               break;
/*      */             }
/*      */           }
/*      */ 
/* 1825 */           Object localObject1 = new AuthenticationHeader("Proxy-Authenticate", this.responses, new HttpCallerInfo(this.url, this.http.getProxyHostUsed(), this.http.getProxyPortUsed()), bool);
/*      */           String str3;
/* 1831 */           if (!this.doingNTLMp2ndStage) {
/* 1832 */             localAuthenticationInfo = resetProxyAuthentication(localAuthenticationInfo, (AuthenticationHeader)localObject1);
/*      */ 
/* 1834 */             if (localAuthenticationInfo != null) {
/* 1835 */               str2 = this.http.getProxyHostUsed();
/* 1836 */               k = this.http.getProxyPortUsed();
/* 1837 */               disconnectInternal();
/* 1838 */               i++;
/* 1839 */               continue;
/*      */             }
/*      */           } else {
/* 1842 */             str3 = this.responses.findValue("Proxy-Authenticate");
/* 1843 */             reset();
/* 1844 */             if (!localAuthenticationInfo.setHeaders(this, ((AuthenticationHeader)localObject1).headerParser(), str3))
/*      */             {
/* 1846 */               disconnectInternal();
/* 1847 */               throw new IOException("Authentication failure");
/*      */             }
/* 1849 */             this.authObj = null;
/* 1850 */             this.doingNTLMp2ndStage = false;
/* 1851 */             continue;
/*      */           }
/*      */         }
/*      */ 
/* 1855 */         if (localAuthenticationInfo != null)
/*      */         {
/* 1857 */           localAuthenticationInfo.addToCache();
/*      */         }
/*      */ 
/* 1860 */         if (j == 200) {
/* 1861 */           setTunnelState(TunnelState.TUNNELING);
/* 1862 */           break;
/*      */         }
/*      */ 
/* 1866 */         disconnectInternal();
/* 1867 */         setTunnelState(TunnelState.NONE);
/* 1868 */         break;
/* 1869 */       }while (i < maxRedirects);
/*      */ 
/* 1871 */       if ((i >= maxRedirects) || (j != 200)) {
/* 1872 */         throw new IOException("Unable to tunnel through proxy. Proxy returns \"" + str1 + "\"");
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1877 */       if (this.proxyAuthKey != null) {
/* 1878 */         AuthenticationInfo.endAuthRequest(this.proxyAuthKey);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1883 */     this.requests = localMessageHeader;
/*      */ 
/* 1886 */     this.responses.reset();
/*      */   }
/*      */ 
/*      */   static String connectRequestURI(URL paramURL) {
/* 1890 */     String str = paramURL.getHost();
/* 1891 */     int i = paramURL.getPort();
/* 1892 */     i = i != -1 ? i : paramURL.getDefaultPort();
/*      */ 
/* 1894 */     return str + ":" + i;
/*      */   }
/*      */ 
/*      */   private void sendCONNECTRequest()
/*      */     throws IOException
/*      */   {
/* 1901 */     int i = this.url.getPort();
/*      */ 
/* 1903 */     this.requests.set(0, HTTP_CONNECT + " " + connectRequestURI(this.url) + " " + "HTTP/1.1", null);
/*      */ 
/* 1905 */     this.requests.setIfNotSet("User-Agent", userAgent);
/*      */ 
/* 1907 */     String str = this.url.getHost();
/* 1908 */     if ((i != -1) && (i != this.url.getDefaultPort())) {
/* 1909 */       str = str + ":" + String.valueOf(i);
/*      */     }
/* 1911 */     this.requests.setIfNotSet("Host", str);
/*      */ 
/* 1914 */     this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
/*      */ 
/* 1916 */     if (this.http.getHttpKeepAliveSet()) {
/* 1917 */       this.requests.setIfNotSet("Proxy-Connection", "keep-alive");
/*      */     }
/*      */ 
/* 1920 */     setPreemptiveProxyAuthentication(this.requests);
/*      */ 
/* 1923 */     if (logger.isLoggable(500)) {
/* 1924 */       logger.fine(this.requests.toString());
/*      */     }
/*      */ 
/* 1927 */     this.http.writeRequests(this.requests, null);
/*      */   }
/*      */ 
/*      */   private void setPreemptiveProxyAuthentication(MessageHeader paramMessageHeader)
/*      */     throws IOException
/*      */   {
/* 1934 */     AuthenticationInfo localAuthenticationInfo = AuthenticationInfo.getProxyAuth(this.http.getProxyHostUsed(), this.http.getProxyPortUsed());
/*      */ 
/* 1937 */     if ((localAuthenticationInfo != null) && (localAuthenticationInfo.supportsPreemptiveAuthorization()))
/*      */     {
/*      */       String str;
/* 1939 */       if ((localAuthenticationInfo instanceof DigestAuthentication)) {
/* 1940 */         DigestAuthentication localDigestAuthentication = (DigestAuthentication)localAuthenticationInfo;
/* 1941 */         if (tunnelState() == TunnelState.SETUP) {
/* 1942 */           str = localDigestAuthentication.getHeaderValue(connectRequestURI(this.url), HTTP_CONNECT);
/*      */         }
/*      */         else
/* 1945 */           str = localDigestAuthentication.getHeaderValue(getRequestURI(), this.method);
/*      */       }
/*      */       else {
/* 1948 */         str = localAuthenticationInfo.getHeaderValue(this.url, this.method);
/*      */       }
/*      */ 
/* 1952 */       paramMessageHeader.set(localAuthenticationInfo.getHeaderName(), str);
/* 1953 */       this.currentProxyCredentials = localAuthenticationInfo;
/*      */     }
/*      */   }
/*      */ 
/*      */   private AuthenticationInfo getHttpProxyAuthentication(AuthenticationHeader paramAuthenticationHeader)
/*      */   {
/* 1963 */     Object localObject1 = null;
/* 1964 */     String str1 = paramAuthenticationHeader.raw();
/* 1965 */     String str2 = this.http.getProxyHostUsed();
/* 1966 */     int i = this.http.getProxyPortUsed();
/* 1967 */     if ((str2 != null) && (paramAuthenticationHeader.isPresent())) {
/* 1968 */       HeaderParser localHeaderParser = paramAuthenticationHeader.headerParser();
/* 1969 */       String str3 = localHeaderParser.findValue("realm");
/* 1970 */       String str4 = paramAuthenticationHeader.scheme();
/* 1971 */       AuthScheme localAuthScheme = AuthScheme.UNKNOWN;
/* 1972 */       if ("basic".equalsIgnoreCase(str4)) {
/* 1973 */         localAuthScheme = AuthScheme.BASIC;
/* 1974 */       } else if ("digest".equalsIgnoreCase(str4)) {
/* 1975 */         localAuthScheme = AuthScheme.DIGEST;
/* 1976 */       } else if ("ntlm".equalsIgnoreCase(str4)) {
/* 1977 */         localAuthScheme = AuthScheme.NTLM;
/* 1978 */         this.doingNTLMp2ndStage = true;
/* 1979 */       } else if ("Kerberos".equalsIgnoreCase(str4)) {
/* 1980 */         localAuthScheme = AuthScheme.KERBEROS;
/* 1981 */         this.doingNTLMp2ndStage = true;
/* 1982 */       } else if ("Negotiate".equalsIgnoreCase(str4)) {
/* 1983 */         localAuthScheme = AuthScheme.NEGOTIATE;
/* 1984 */         this.doingNTLMp2ndStage = true;
/*      */       }
/*      */ 
/* 1987 */       if (str3 == null)
/* 1988 */         str3 = "";
/* 1989 */       this.proxyAuthKey = AuthenticationInfo.getProxyAuthKey(str2, i, str3, localAuthScheme);
/* 1990 */       localObject1 = AuthenticationInfo.getProxyAuth(this.proxyAuthKey);
/*      */       Object localObject2;
/*      */       Object localObject3;
/* 1991 */       if (localObject1 == null) {
/* 1992 */         switch (8.$SwitchMap$sun$net$www$protocol$http$AuthScheme[localAuthScheme.ordinal()]) {
/*      */         case 1:
/* 1994 */           localObject2 = null;
/*      */           try {
/* 1996 */             final String str5 = str2;
/* 1997 */             localObject2 = (InetAddress)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */             {
/*      */               public InetAddress run() throws UnknownHostException
/*      */               {
/* 2001 */                 return InetAddress.getByName(str5);
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (PrivilegedActionException localPrivilegedActionException) {
/*      */           }
/* 2007 */           localObject3 = privilegedRequestPasswordAuthentication(str2, (InetAddress)localObject2, i, "http", str3, str4, this.url, Authenticator.RequestorType.PROXY);
/*      */ 
/* 2011 */           if (localObject3 != null)
/* 2012 */             localObject1 = new BasicAuthentication(true, str2, i, str3, (PasswordAuthentication)localObject3); break;
/*      */         case 2:
/* 2016 */           localObject3 = privilegedRequestPasswordAuthentication(str2, null, i, this.url.getProtocol(), str3, str4, this.url, Authenticator.RequestorType.PROXY);
/*      */ 
/* 2019 */           if (localObject3 != null) {
/* 2020 */             DigestAuthentication.Parameters localParameters = new DigestAuthentication.Parameters();
/*      */ 
/* 2022 */             localObject1 = new DigestAuthentication(true, str2, i, str3, str4, (PasswordAuthentication)localObject3, localParameters);
/*      */           }
/* 2024 */           break;
/*      */         case 3:
/* 2027 */           if (NTLMAuthenticationProxy.supported)
/*      */           {
/* 2031 */             if (this.tryTransparentNTLMProxy) {
/* 2032 */               this.tryTransparentNTLMProxy = NTLMAuthenticationProxy.supportsTransparentAuth;
/*      */             }
/*      */ 
/* 2035 */             localObject3 = null;
/* 2036 */             if (this.tryTransparentNTLMProxy)
/* 2037 */               logger.finest("Trying Transparent NTLM authentication");
/*      */             else {
/* 2039 */               localObject3 = privilegedRequestPasswordAuthentication(str2, null, i, this.url.getProtocol(), "", str4, this.url, Authenticator.RequestorType.PROXY);
/*      */             }
/*      */ 
/* 2049 */             if ((this.tryTransparentNTLMProxy) || ((!this.tryTransparentNTLMProxy) && (localObject3 != null)))
/*      */             {
/* 2051 */               localObject1 = NTLMAuthenticationProxy.proxy.create(true, str2, i, (PasswordAuthentication)localObject3);
/*      */             }
/*      */ 
/* 2055 */             this.tryTransparentNTLMProxy = false; } break;
/*      */         case 4:
/* 2059 */           localObject1 = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Negotiate"));
/* 2060 */           break;
/*      */         case 5:
/* 2062 */           localObject1 = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Kerberos"));
/* 2063 */           break;
/*      */         case 6:
/* 2065 */           logger.finest("Unknown/Unsupported authentication scheme: " + str4);
/*      */         default:
/* 2067 */           throw new AssertionError("should not reach here");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2073 */       if ((localObject1 == null) && (defaultAuth != null) && (defaultAuth.schemeSupported(str4))) {
/*      */         try
/*      */         {
/* 2076 */           localObject2 = new URL("http", str2, i, "/");
/* 2077 */           localObject3 = defaultAuth.authString((URL)localObject2, str4, str3);
/* 2078 */           if (localObject3 != null)
/* 2079 */             localObject1 = new BasicAuthentication(true, str2, i, str3, (String)localObject3);
/*      */         }
/*      */         catch (MalformedURLException localMalformedURLException)
/*      */         {
/*      */         }
/*      */       }
/* 2085 */       if ((localObject1 != null) && 
/* 2086 */         (!((AuthenticationInfo)localObject1).setHeaders(this, localHeaderParser, str1))) {
/* 2087 */         localObject1 = null;
/*      */       }
/*      */     }
/*      */ 
/* 2091 */     if (logger.isLoggable(400)) {
/* 2092 */       logger.finer("Proxy Authentication for " + paramAuthenticationHeader.toString() + " returned " + (localObject1 != null ? localObject1.toString() : "null"));
/*      */     }
/* 2094 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private AuthenticationInfo getServerAuthentication(AuthenticationHeader paramAuthenticationHeader)
/*      */   {
/* 2105 */     Object localObject1 = null;
/* 2106 */     String str1 = paramAuthenticationHeader.raw();
/*      */ 
/* 2108 */     if (paramAuthenticationHeader.isPresent()) {
/* 2109 */       HeaderParser localHeaderParser = paramAuthenticationHeader.headerParser();
/* 2110 */       String str2 = localHeaderParser.findValue("realm");
/* 2111 */       String str3 = paramAuthenticationHeader.scheme();
/* 2112 */       AuthScheme localAuthScheme = AuthScheme.UNKNOWN;
/* 2113 */       if ("basic".equalsIgnoreCase(str3)) {
/* 2114 */         localAuthScheme = AuthScheme.BASIC;
/* 2115 */       } else if ("digest".equalsIgnoreCase(str3)) {
/* 2116 */         localAuthScheme = AuthScheme.DIGEST;
/* 2117 */       } else if ("ntlm".equalsIgnoreCase(str3)) {
/* 2118 */         localAuthScheme = AuthScheme.NTLM;
/* 2119 */         this.doingNTLM2ndStage = true;
/* 2120 */       } else if ("Kerberos".equalsIgnoreCase(str3)) {
/* 2121 */         localAuthScheme = AuthScheme.KERBEROS;
/* 2122 */         this.doingNTLM2ndStage = true;
/* 2123 */       } else if ("Negotiate".equalsIgnoreCase(str3)) {
/* 2124 */         localAuthScheme = AuthScheme.NEGOTIATE;
/* 2125 */         this.doingNTLM2ndStage = true;
/*      */       }
/*      */ 
/* 2128 */       this.domain = localHeaderParser.findValue("domain");
/* 2129 */       if (str2 == null)
/* 2130 */         str2 = "";
/* 2131 */       this.serverAuthKey = AuthenticationInfo.getServerAuthKey(this.url, str2, localAuthScheme);
/* 2132 */       localObject1 = AuthenticationInfo.getServerAuth(this.serverAuthKey);
/* 2133 */       InetAddress localInetAddress = null;
/* 2134 */       if (localObject1 == null) {
/*      */         try {
/* 2136 */           localInetAddress = InetAddress.getByName(this.url.getHost());
/*      */         }
/*      */         catch (UnknownHostException localUnknownHostException)
/*      */         {
/*      */         }
/*      */       }
/* 2142 */       int i = this.url.getPort();
/* 2143 */       if (i == -1)
/* 2144 */         i = this.url.getDefaultPort();
/*      */       Object localObject2;
/* 2146 */       if (localObject1 == null) {
/* 2147 */         switch (8.$SwitchMap$sun$net$www$protocol$http$AuthScheme[localAuthScheme.ordinal()]) {
/*      */         case 5:
/* 2149 */           localObject1 = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Kerberos"));
/* 2150 */           break;
/*      */         case 4:
/* 2152 */           localObject1 = new NegotiateAuthentication(new HttpCallerInfo(paramAuthenticationHeader.getHttpCallerInfo(), "Negotiate"));
/* 2153 */           break;
/*      */         case 1:
/* 2155 */           localObject2 = privilegedRequestPasswordAuthentication(this.url.getHost(), localInetAddress, i, this.url.getProtocol(), str2, str3, this.url, Authenticator.RequestorType.SERVER);
/*      */ 
/* 2159 */           if (localObject2 != null)
/* 2160 */             localObject1 = new BasicAuthentication(false, this.url, str2, (PasswordAuthentication)localObject2); break;
/*      */         case 2:
/* 2164 */           localObject2 = privilegedRequestPasswordAuthentication(this.url.getHost(), localInetAddress, i, this.url.getProtocol(), str2, str3, this.url, Authenticator.RequestorType.SERVER);
/*      */ 
/* 2167 */           if (localObject2 != null) {
/* 2168 */             this.digestparams = new DigestAuthentication.Parameters();
/* 2169 */             localObject1 = new DigestAuthentication(false, this.url, str2, str3, (PasswordAuthentication)localObject2, this.digestparams); } break;
/*      */         case 3:
/* 2173 */           if (NTLMAuthenticationProxy.supported) {
/*      */             URL localURL;
/*      */             try {
/* 2176 */               localURL = new URL(this.url, "/");
/*      */             } catch (Exception localException) {
/* 2178 */               localURL = this.url;
/*      */             }
/*      */ 
/* 2184 */             if (this.tryTransparentNTLMServer) {
/* 2185 */               this.tryTransparentNTLMServer = NTLMAuthenticationProxy.supportsTransparentAuth;
/*      */ 
/* 2190 */               if (this.tryTransparentNTLMServer) {
/* 2191 */                 this.tryTransparentNTLMServer = NTLMAuthenticationProxy.isTrustedSite(this.url);
/*      */               }
/*      */             }
/*      */ 
/* 2195 */             localObject2 = null;
/* 2196 */             if (this.tryTransparentNTLMServer)
/* 2197 */               logger.finest("Trying Transparent NTLM authentication");
/*      */             else {
/* 2199 */               localObject2 = privilegedRequestPasswordAuthentication(this.url.getHost(), localInetAddress, i, this.url.getProtocol(), "", str3, this.url, Authenticator.RequestorType.SERVER);
/*      */             }
/*      */ 
/* 2210 */             if ((this.tryTransparentNTLMServer) || ((!this.tryTransparentNTLMServer) && (localObject2 != null)))
/*      */             {
/* 2212 */               localObject1 = NTLMAuthenticationProxy.proxy.create(false, localURL, (PasswordAuthentication)localObject2);
/*      */             }
/*      */ 
/* 2216 */             this.tryTransparentNTLMServer = false;
/* 2217 */           }break;
/*      */         case 6:
/* 2220 */           logger.finest("Unknown/Unsupported authentication scheme: " + str3);
/*      */         default:
/* 2222 */           throw new AssertionError("should not reach here");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2229 */       if ((localObject1 == null) && (defaultAuth != null) && (defaultAuth.schemeSupported(str3)))
/*      */       {
/* 2231 */         localObject2 = defaultAuth.authString(this.url, str3, str2);
/* 2232 */         if (localObject2 != null) {
/* 2233 */           localObject1 = new BasicAuthentication(false, this.url, str2, (String)localObject2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2238 */       if ((localObject1 != null) && 
/* 2239 */         (!((AuthenticationInfo)localObject1).setHeaders(this, localHeaderParser, str1))) {
/* 2240 */         localObject1 = null;
/*      */       }
/*      */     }
/*      */ 
/* 2244 */     if (logger.isLoggable(400)) {
/* 2245 */       logger.finer("Server Authentication for " + paramAuthenticationHeader.toString() + " returned " + (localObject1 != null ? localObject1.toString() : "null"));
/*      */     }
/* 2247 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private void checkResponseCredentials(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/* 2257 */       if (!this.needToCheck)
/*      */         return;
/*      */       String str;
/*      */       DigestAuthentication localDigestAuthentication;
/* 2259 */       if ((validateProxy) && (this.currentProxyCredentials != null) && ((this.currentProxyCredentials instanceof DigestAuthentication)))
/*      */       {
/* 2261 */         str = this.responses.findValue("Proxy-Authentication-Info");
/* 2262 */         if ((paramBoolean) || (str != null)) {
/* 2263 */           localDigestAuthentication = (DigestAuthentication)this.currentProxyCredentials;
/*      */ 
/* 2265 */           localDigestAuthentication.checkResponse(str, this.method, getRequestURI());
/* 2266 */           this.currentProxyCredentials = null;
/*      */         }
/*      */       }
/* 2269 */       if ((validateServer) && (this.currentServerCredentials != null) && ((this.currentServerCredentials instanceof DigestAuthentication)))
/*      */       {
/* 2271 */         str = this.responses.findValue("Authentication-Info");
/* 2272 */         if ((paramBoolean) || (str != null)) {
/* 2273 */           localDigestAuthentication = (DigestAuthentication)this.currentServerCredentials;
/*      */ 
/* 2275 */           localDigestAuthentication.checkResponse(str, this.method, this.url);
/* 2276 */           this.currentServerCredentials = null;
/*      */         }
/*      */       }
/* 2279 */       if ((this.currentServerCredentials == null) && (this.currentProxyCredentials == null))
/* 2280 */         this.needToCheck = false;
/*      */     }
/*      */     catch (IOException localIOException) {
/* 2283 */       disconnectInternal();
/* 2284 */       this.connected = false;
/* 2285 */       throw localIOException;
/*      */     }
/*      */   }
/*      */ 
/*      */   String getRequestURI()
/*      */     throws IOException
/*      */   {
/* 2296 */     if (this.requestURI == null) {
/* 2297 */       this.requestURI = this.http.getURLFile();
/*      */     }
/* 2299 */     return this.requestURI;
/*      */   }
/*      */ 
/*      */   private boolean followRedirect()
/*      */     throws IOException
/*      */   {
/* 2308 */     if (!getInstanceFollowRedirects()) {
/* 2309 */       return false;
/*      */     }
/*      */ 
/* 2312 */     int i = getResponseCode();
/* 2313 */     if ((i < 300) || (i > 307) || (i == 306) || (i == 304))
/*      */     {
/* 2315 */       return false;
/*      */     }
/* 2317 */     String str1 = getHeaderField("Location");
/* 2318 */     if (str1 == null)
/*      */     {
/* 2322 */       return false;
/*      */     }
/*      */     URL localURL;
/*      */     try {
/* 2326 */       localURL = new URL(str1);
/* 2327 */       if (!this.url.getProtocol().equalsIgnoreCase(localURL.getProtocol())) {
/* 2328 */         return false;
/*      */       }
/*      */     }
/*      */     catch (MalformedURLException localMalformedURLException)
/*      */     {
/* 2333 */       localURL = new URL(this.url, str1);
/*      */     }
/* 2335 */     disconnectInternal();
/* 2336 */     if (streaming()) {
/* 2337 */       throw new HttpRetryException("cannot retry due to redirection, in streaming mode", i, str1);
/*      */     }
/* 2339 */     if (logger.isLoggable(500)) {
/* 2340 */       logger.fine("Redirected from " + this.url + " to " + localURL);
/*      */     }
/*      */ 
/* 2344 */     this.responses = new MessageHeader();
/* 2345 */     if (i == 305)
/*      */     {
/* 2354 */       String str2 = localURL.getHost();
/* 2355 */       int k = localURL.getPort();
/*      */ 
/* 2357 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 2358 */       if (localSecurityManager != null) {
/* 2359 */         localSecurityManager.checkConnect(str2, k);
/*      */       }
/*      */ 
/* 2362 */       setProxiedClient(this.url, str2, k);
/* 2363 */       this.requests.set(0, this.method + " " + getRequestURI() + " " + "HTTP/1.1", null);
/*      */ 
/* 2365 */       this.connected = true;
/*      */     }
/*      */     else
/*      */     {
/* 2369 */       this.url = localURL;
/* 2370 */       this.requestURI = null;
/* 2371 */       if ((this.method.equals("POST")) && (!Boolean.getBoolean("http.strictPostRedirect")) && (i != 307))
/*      */       {
/* 2389 */         this.requests = new MessageHeader();
/* 2390 */         this.setRequests = false;
/* 2391 */         setRequestMethod("GET");
/* 2392 */         this.poster = null;
/* 2393 */         if (!checkReuseConnection())
/* 2394 */           connect();
/*      */       } else {
/* 2396 */         if (!checkReuseConnection()) {
/* 2397 */           connect();
/*      */         }
/*      */ 
/* 2407 */         if (this.http != null) {
/* 2408 */           this.requests.set(0, this.method + " " + getRequestURI() + " " + "HTTP/1.1", null);
/*      */ 
/* 2410 */           int j = this.url.getPort();
/* 2411 */           String str3 = this.url.getHost();
/* 2412 */           if ((j != -1) && (j != this.url.getDefaultPort())) {
/* 2413 */             str3 = str3 + ":" + String.valueOf(j);
/*      */           }
/* 2415 */           this.requests.set("Host", str3);
/*      */         }
/*      */       }
/*      */     }
/* 2419 */     return true;
/*      */   }
/*      */ 
/*      */   private void reset()
/*      */     throws IOException
/*      */   {
/* 2429 */     this.http.reuse = true;
/*      */ 
/* 2431 */     this.reuseClient = this.http;
/* 2432 */     InputStream localInputStream = this.http.getInputStream();
/* 2433 */     if (!this.method.equals("HEAD"))
/*      */     {
/*      */       try
/*      */       {
/* 2439 */         if (((localInputStream instanceof ChunkedInputStream)) || ((localInputStream instanceof MeteredStream)));
/* 2442 */         while (localInputStream.read(this.cdata) > 0) { continue;
/*      */ 
/* 2447 */           long l1 = 0L;
/* 2448 */           i = 0;
/* 2449 */           String str = this.responses.findValue("Content-Length");
/* 2450 */           if (str != null) {
/*      */             try {
/* 2452 */               l1 = Long.parseLong(str);
/*      */             } catch (NumberFormatException localNumberFormatException) {
/* 2454 */               l1 = 0L;
/*      */             }
/*      */           }
/* 2457 */           for (l2 = 0L; (l2 < l1) && 
/* 2458 */             ((i = localInputStream.read(this.cdata)) != -1); )
/*      */           {
/* 2461 */             l2 += i;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/*      */         int i;
/*      */         long l2;
/* 2466 */         this.http.reuse = false;
/* 2467 */         this.reuseClient = null;
/* 2468 */         disconnectInternal();
/* 2469 */         return;
/*      */       }
/*      */       try {
/* 2472 */         if ((localInputStream instanceof MeteredStream))
/* 2473 */           localInputStream.close();
/*      */       } catch (IOException localIOException2) {
/*      */       }
/*      */     }
/* 2477 */     this.responseCode = -1;
/* 2478 */     this.responses = new MessageHeader();
/* 2479 */     this.connected = false;
/*      */   }
/*      */ 
/*      */   private void disconnectWeb()
/*      */     throws IOException
/*      */   {
/* 2488 */     if ((usingProxy()) && (this.http.isKeepingAlive())) {
/* 2489 */       this.responseCode = -1;
/*      */ 
/* 2492 */       reset();
/*      */     } else {
/* 2494 */       disconnectInternal();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void disconnectInternal()
/*      */   {
/* 2502 */     this.responseCode = -1;
/* 2503 */     this.inputStream = null;
/* 2504 */     if (this.pi != null) {
/* 2505 */       this.pi.finishTracking();
/* 2506 */       this.pi = null;
/*      */     }
/* 2508 */     if (this.http != null) {
/* 2509 */       this.http.closeServer();
/* 2510 */       this.http = null;
/* 2511 */       this.connected = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disconnect()
/*      */   {
/* 2520 */     this.responseCode = -1;
/* 2521 */     if (this.pi != null) {
/* 2522 */       this.pi.finishTracking();
/* 2523 */       this.pi = null;
/*      */     }
/*      */ 
/* 2526 */     if (this.http != null)
/*      */     {
/* 2552 */       if (this.inputStream != null) {
/* 2553 */         HttpClient localHttpClient = this.http;
/*      */ 
/* 2556 */         boolean bool = localHttpClient.isKeepingAlive();
/*      */         try
/*      */         {
/* 2559 */           this.inputStream.close();
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/*      */ 
/* 2567 */         if (bool) {
/* 2568 */           localHttpClient.closeIdleConnection();
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2576 */         this.http.setDoNotRetry(true);
/*      */ 
/* 2578 */         this.http.closeServer();
/*      */       }
/*      */ 
/* 2582 */       this.http = null;
/* 2583 */       this.connected = false;
/*      */     }
/* 2585 */     this.cachedInputStream = null;
/* 2586 */     if (this.cachedHeaders != null)
/* 2587 */       this.cachedHeaders.reset();
/*      */   }
/*      */ 
/*      */   public boolean usingProxy()
/*      */   {
/* 2592 */     if (this.http != null) {
/* 2593 */       return this.http.getProxyHostUsed() != null;
/*      */     }
/* 2595 */     return false;
/*      */   }
/*      */ 
/*      */   private String filterHeaderField(String paramString1, String paramString2)
/*      */   {
/* 2609 */     if (paramString2 == null) {
/* 2610 */       return null;
/*      */     }
/* 2612 */     if (("set-cookie".equalsIgnoreCase(paramString1)) || ("set-cookie2".equalsIgnoreCase(paramString1)))
/*      */     {
/* 2616 */       if (this.cookieHandler == null) {
/* 2617 */         return paramString2;
/*      */       }
/* 2619 */       JavaNetHttpCookieAccess localJavaNetHttpCookieAccess = SharedSecrets.getJavaNetHttpCookieAccess();
/*      */ 
/* 2621 */       StringBuilder localStringBuilder = new StringBuilder();
/* 2622 */       List localList = localJavaNetHttpCookieAccess.parse(paramString2);
/* 2623 */       int i = 0;
/* 2624 */       for (HttpCookie localHttpCookie : localList)
/*      */       {
/* 2626 */         if (!localHttpCookie.isHttpOnly())
/*      */         {
/* 2628 */           if (i != 0)
/* 2629 */             localStringBuilder.append(',');
/* 2630 */           localStringBuilder.append(localJavaNetHttpCookieAccess.header(localHttpCookie));
/* 2631 */           i = 1;
/*      */         }
/*      */       }
/* 2634 */       return localStringBuilder.length() == 0 ? null : localStringBuilder.toString();
/*      */     }
/*      */ 
/* 2637 */     return paramString2;
/*      */   }
/*      */ 
/*      */   private Map<String, List<String>> getFilteredHeaderFields()
/*      */   {
/* 2645 */     if (this.filteredHeaders != null) {
/* 2646 */       return this.filteredHeaders;
/*      */     }
/* 2648 */     HashMap localHashMap = new HashMap();
/*      */     Map localMap;
/* 2650 */     if (this.cachedHeaders != null)
/* 2651 */       localMap = this.cachedHeaders.getHeaders();
/*      */     else {
/* 2653 */       localMap = this.responses.getHeaders();
/*      */     }
/* 2655 */     for (Map.Entry localEntry : localMap.entrySet()) {
/* 2656 */       String str1 = (String)localEntry.getKey();
/* 2657 */       List localList = (List)localEntry.getValue(); ArrayList localArrayList = new ArrayList();
/* 2658 */       for (String str2 : localList) {
/* 2659 */         String str3 = filterHeaderField(str1, str2);
/* 2660 */         if (str3 != null)
/* 2661 */           localArrayList.add(str3);
/*      */       }
/* 2663 */       if (!localArrayList.isEmpty()) {
/* 2664 */         localHashMap.put(str1, Collections.unmodifiableList(localArrayList));
/*      */       }
/*      */     }
/* 2667 */     return this.filteredHeaders = Collections.unmodifiableMap(localHashMap);
/*      */   }
/*      */ 
/*      */   public String getHeaderField(String paramString)
/*      */   {
/*      */     try
/*      */     {
/* 2677 */       getInputStream();
/*      */     } catch (IOException localIOException) {
/*      */     }
/* 2680 */     if (this.cachedHeaders != null) {
/* 2681 */       return filterHeaderField(paramString, this.cachedHeaders.findValue(paramString));
/*      */     }
/*      */ 
/* 2684 */     return filterHeaderField(paramString, this.responses.findValue(paramString));
/*      */   }
/*      */ 
/*      */   public Map<String, List<String>> getHeaderFields()
/*      */   {
/*      */     try
/*      */     {
/* 2700 */       getInputStream();
/*      */     } catch (IOException localIOException) {
/*      */     }
/* 2703 */     return getFilteredHeaderFields();
/*      */   }
/*      */ 
/*      */   public String getHeaderField(int paramInt)
/*      */   {
/*      */     try
/*      */     {
/* 2713 */       getInputStream();
/*      */     } catch (IOException localIOException) {
/*      */     }
/* 2716 */     if (this.cachedHeaders != null) {
/* 2717 */       return filterHeaderField(this.cachedHeaders.getKey(paramInt), this.cachedHeaders.getValue(paramInt));
/*      */     }
/*      */ 
/* 2720 */     return filterHeaderField(this.responses.getKey(paramInt), this.responses.getValue(paramInt));
/*      */   }
/*      */ 
/*      */   public String getHeaderFieldKey(int paramInt)
/*      */   {
/*      */     try
/*      */     {
/* 2730 */       getInputStream();
/*      */     } catch (IOException localIOException) {
/*      */     }
/* 2733 */     if (this.cachedHeaders != null) {
/* 2734 */       return this.cachedHeaders.getKey(paramInt);
/*      */     }
/*      */ 
/* 2737 */     return this.responses.getKey(paramInt);
/*      */   }
/*      */ 
/*      */   public void setRequestProperty(String paramString1, String paramString2)
/*      */   {
/* 2747 */     if (this.connected)
/* 2748 */       throw new IllegalStateException("Already connected");
/* 2749 */     if (paramString1 == null) {
/* 2750 */       throw new NullPointerException("key is null");
/*      */     }
/* 2752 */     if (isExternalMessageHeaderAllowed(paramString1, paramString2))
/* 2753 */       this.requests.set(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public void addRequestProperty(String paramString1, String paramString2)
/*      */   {
/* 2770 */     if (this.connected)
/* 2771 */       throw new IllegalStateException("Already connected");
/* 2772 */     if (paramString1 == null) {
/* 2773 */       throw new NullPointerException("key is null");
/*      */     }
/* 2775 */     if (isExternalMessageHeaderAllowed(paramString1, paramString2))
/* 2776 */       this.requests.add(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public void setAuthenticationProperty(String paramString1, String paramString2)
/*      */   {
/* 2785 */     checkMessageHeader(paramString1, paramString2);
/* 2786 */     this.requests.set(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public synchronized String getRequestProperty(String paramString)
/*      */   {
/* 2791 */     if (paramString == null) {
/* 2792 */       return null;
/*      */     }
/*      */ 
/* 2796 */     for (int i = 0; i < EXCLUDE_HEADERS.length; i++) {
/* 2797 */       if (paramString.equalsIgnoreCase(EXCLUDE_HEADERS[i])) {
/* 2798 */         return null;
/*      */       }
/*      */     }
/* 2801 */     if (!this.setUserCookies) {
/* 2802 */       if (paramString.equalsIgnoreCase("Cookie")) {
/* 2803 */         return this.userCookies;
/*      */       }
/* 2805 */       if (paramString.equalsIgnoreCase("Cookie2")) {
/* 2806 */         return this.userCookies2;
/*      */       }
/*      */     }
/* 2809 */     return this.requests.findValue(paramString);
/*      */   }
/*      */ 
/*      */   public synchronized Map<String, List<String>> getRequestProperties()
/*      */   {
/* 2826 */     if (this.connected) {
/* 2827 */       throw new IllegalStateException("Already connected");
/*      */     }
/*      */ 
/* 2830 */     if (this.setUserCookies) {
/* 2831 */       return this.requests.getHeaders(EXCLUDE_HEADERS);
/*      */     }
/*      */ 
/* 2837 */     HashMap localHashMap = null;
/* 2838 */     if ((this.userCookies != null) || (this.userCookies2 != null)) {
/* 2839 */       localHashMap = new HashMap();
/* 2840 */       if (this.userCookies != null) {
/* 2841 */         localHashMap.put("Cookie", this.userCookies);
/*      */       }
/* 2843 */       if (this.userCookies2 != null) {
/* 2844 */         localHashMap.put("Cookie2", this.userCookies2);
/*      */       }
/*      */     }
/* 2847 */     return this.requests.filterAndAddHeaders(EXCLUDE_HEADERS2, localHashMap);
/*      */   }
/*      */ 
/*      */   public void setConnectTimeout(int paramInt)
/*      */   {
/* 2852 */     if (paramInt < 0)
/* 2853 */       throw new IllegalArgumentException("timeouts can't be negative");
/* 2854 */     this.connectTimeout = paramInt;
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout()
/*      */   {
/* 2872 */     return this.connectTimeout < 0 ? 0 : this.connectTimeout;
/*      */   }
/*      */ 
/*      */   public void setReadTimeout(int paramInt)
/*      */   {
/* 2897 */     if (paramInt < 0)
/* 2898 */       throw new IllegalArgumentException("timeouts can't be negative");
/* 2899 */     this.readTimeout = paramInt;
/*      */   }
/*      */ 
/*      */   public int getReadTimeout()
/*      */   {
/* 2915 */     return this.readTimeout < 0 ? 0 : this.readTimeout;
/*      */   }
/*      */ 
/*      */   public CookieHandler getCookieHandler() {
/* 2919 */     return this.cookieHandler;
/*      */   }
/*      */ 
/*      */   String getMethod() {
/* 2923 */     return this.method;
/*      */   }
/*      */ 
/*      */   private MessageHeader mapToMessageHeader(Map<String, List<String>> paramMap) {
/* 2927 */     MessageHeader localMessageHeader = new MessageHeader();
/* 2928 */     if ((paramMap == null) || (paramMap.isEmpty())) {
/* 2929 */       return localMessageHeader;
/*      */     }
/* 2931 */     for (Map.Entry localEntry : paramMap.entrySet()) {
/* 2932 */       str1 = (String)localEntry.getKey();
/* 2933 */       List localList = (List)localEntry.getValue();
/* 2934 */       for (String str2 : localList)
/* 2935 */         if (str1 == null)
/* 2936 */           localMessageHeader.prepend(str1, str2);
/*      */         else
/* 2938 */           localMessageHeader.add(str1, str2);
/*      */     }
/*      */     String str1;
/* 2942 */     return localMessageHeader;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  198 */     maxRedirects = ((Integer)AccessController.doPrivileged(new GetIntegerAction("http.maxRedirects", 20))).intValue();
/*      */ 
/*  201 */     version = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
/*      */ 
/*  203 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("http.agent"));
/*      */ 
/*  205 */     if (str == null)
/*  206 */       str = "Java/" + version;
/*      */     else {
/*  208 */       str = str + " Java/" + version;
/*      */     }
/*  210 */     userAgent = str;
/*  211 */     validateProxy = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("http.auth.digest.validateProxy"))).booleanValue();
/*      */ 
/*  214 */     validateServer = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("http.auth.digest.validateServer"))).booleanValue();
/*      */ 
/*  218 */     enableESBuffer = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.net.http.errorstream.enableBuffering"))).booleanValue();
/*      */ 
/*  221 */     timeout4ESBuffer = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.net.http.errorstream.timeout", 300))).intValue();
/*      */ 
/*  224 */     if (timeout4ESBuffer <= 0) {
/*  225 */       timeout4ESBuffer = 300;
/*      */     }
/*      */ 
/*  228 */     bufSize4ES = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.net.http.errorstream.bufferSize", 4096))).intValue();
/*      */ 
/*  231 */     if (bufSize4ES <= 0) {
/*  232 */       bufSize4ES = 4096;
/*      */     }
/*      */ 
/*  235 */     allowRestrictedHeaders = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.net.http.allowRestrictedHeaders"))).booleanValue();
/*      */ 
/*  238 */     if (!allowRestrictedHeaders) {
/*  239 */       restrictedHeaderSet = new HashSet(restrictedHeaders.length);
/*  240 */       for (int i = 0; i < restrictedHeaders.length; i++)
/*  241 */         restrictedHeaderSet.add(restrictedHeaders[i].toLowerCase());
/*      */     }
/*      */     else {
/*  244 */       restrictedHeaderSet = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ErrorStream extends InputStream
/*      */   {
/*      */     ByteBuffer buffer;
/*      */     InputStream is;
/*      */ 
/*      */     private ErrorStream(ByteBuffer paramByteBuffer)
/*      */     {
/* 3236 */       this.buffer = paramByteBuffer;
/* 3237 */       this.is = null;
/*      */     }
/*      */ 
/*      */     private ErrorStream(ByteBuffer paramByteBuffer, InputStream paramInputStream) {
/* 3241 */       this.buffer = paramByteBuffer;
/* 3242 */       this.is = paramInputStream;
/*      */     }
/*      */ 
/*      */     public static InputStream getErrorStream(InputStream paramInputStream, long paramLong, HttpClient paramHttpClient)
/*      */     {
/* 3250 */       if (paramLong == 0L) {
/* 3251 */         return null;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 3257 */         int i = paramHttpClient.getReadTimeout();
/* 3258 */         paramHttpClient.setReadTimeout(HttpURLConnection.timeout4ESBuffer / 5);
/*      */ 
/* 3260 */         long l = 0L;
/* 3261 */         int j = 0;
/*      */ 
/* 3263 */         if (paramLong < 0L) {
/* 3264 */           l = HttpURLConnection.bufSize4ES;
/* 3265 */           j = 1;
/*      */         } else {
/* 3267 */           l = paramLong;
/*      */         }
/* 3269 */         if (l <= HttpURLConnection.bufSize4ES) {
/* 3270 */           int k = (int)l;
/* 3271 */           byte[] arrayOfByte = new byte[k];
/* 3272 */           int m = 0; int n = 0; int i1 = 0;
/*      */           do
/*      */             try {
/* 3275 */               i1 = paramInputStream.read(arrayOfByte, m, arrayOfByte.length - m);
/*      */ 
/* 3277 */               if (i1 < 0) {
/* 3278 */                 if (j != 0)
/*      */                 {
/*      */                   break;
/*      */                 }
/*      */ 
/* 3285 */                 throw new IOException("the server closes before sending " + paramLong + " bytes of data");
/*      */               }
/*      */ 
/* 3289 */               m += i1;
/*      */             } catch (SocketTimeoutException localSocketTimeoutException) {
/* 3291 */               n += HttpURLConnection.timeout4ESBuffer / 5;
/*      */             }
/* 3293 */           while ((m < k) && (n < HttpURLConnection.timeout4ESBuffer));
/*      */ 
/* 3296 */           paramHttpClient.setReadTimeout(i);
/*      */ 
/* 3300 */           if (m == 0)
/*      */           {
/* 3304 */             return null;
/* 3305 */           }if (((m == l) && (j == 0)) || ((j != 0) && (i1 < 0)))
/*      */           {
/* 3308 */             paramInputStream.close();
/* 3309 */             return new ErrorStream(ByteBuffer.wrap(arrayOfByte, 0, m));
/*      */           }
/*      */ 
/* 3312 */           return new ErrorStream(ByteBuffer.wrap(arrayOfByte, 0, m), paramInputStream);
/*      */         }
/*      */ 
/* 3316 */         return null;
/*      */       } catch (IOException localIOException) {
/*      */       }
/* 3319 */       return null;
/*      */     }
/*      */ 
/*      */     public int available()
/*      */       throws IOException
/*      */     {
/* 3325 */       if (this.is == null) {
/* 3326 */         return this.buffer.remaining();
/*      */       }
/* 3328 */       return this.buffer.remaining() + this.is.available();
/*      */     }
/*      */ 
/*      */     public int read() throws IOException
/*      */     {
/* 3333 */       byte[] arrayOfByte = new byte[1];
/* 3334 */       int i = read(arrayOfByte);
/* 3335 */       return i == -1 ? i : arrayOfByte[0] & 0xFF;
/*      */     }
/*      */ 
/*      */     public int read(byte[] paramArrayOfByte) throws IOException
/*      */     {
/* 3340 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */     }
/*      */ 
/*      */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 3345 */       int i = this.buffer.remaining();
/* 3346 */       if (i > 0) {
/* 3347 */         int j = i < paramInt2 ? i : paramInt2;
/* 3348 */         this.buffer.get(paramArrayOfByte, paramInt1, j);
/* 3349 */         return j;
/*      */       }
/* 3351 */       if (this.is == null) {
/* 3352 */         return -1;
/*      */       }
/* 3354 */       return this.is.read(paramArrayOfByte, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     public void close()
/*      */       throws IOException
/*      */     {
/* 3361 */       this.buffer = null;
/* 3362 */       if (this.is != null)
/* 3363 */         this.is.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   class HttpInputStream extends FilterInputStream
/*      */   {
/*      */     private CacheRequest cacheRequest;
/*      */     private OutputStream outputStream;
/* 2952 */     private boolean marked = false;
/* 2953 */     private int inCache = 0;
/* 2954 */     private int markCount = 0;
/*      */     private byte[] skipBuffer;
/*      */     private static final int SKIP_BUFFER_SIZE = 8096;
/*      */ 
/*      */     public HttpInputStream(InputStream arg2)
/*      */     {
/* 2957 */       super();
/* 2958 */       this.cacheRequest = null;
/* 2959 */       this.outputStream = null;
/*      */     }
/*      */ 
/*      */     public HttpInputStream(InputStream paramCacheRequest, CacheRequest arg3) {
/* 2963 */       super();
/*      */       Object localObject;
/* 2964 */       this.cacheRequest = localObject;
/*      */       try {
/* 2966 */         this.outputStream = localObject.getBody();
/*      */       } catch (IOException localIOException) {
/* 2968 */         this.cacheRequest.abort();
/* 2969 */         this.cacheRequest = null;
/* 2970 */         this.outputStream = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     public synchronized void mark(int paramInt)
/*      */     {
/* 2993 */       super.mark(paramInt);
/* 2994 */       if (this.cacheRequest != null) {
/* 2995 */         this.marked = true;
/* 2996 */         this.markCount = 0;
/*      */       }
/*      */     }
/*      */ 
/*      */     public synchronized void reset()
/*      */       throws IOException
/*      */     {
/* 3023 */       super.reset();
/* 3024 */       if (this.cacheRequest != null) {
/* 3025 */         this.marked = false;
/* 3026 */         this.inCache += this.markCount;
/*      */       }
/*      */     }
/*      */ 
/*      */     public int read() throws IOException
/*      */     {
/*      */       try {
/* 3033 */         byte[] arrayOfByte = new byte[1];
/* 3034 */         int i = read(arrayOfByte);
/* 3035 */         return i == -1 ? i : arrayOfByte[0] & 0xFF;
/*      */       } catch (IOException localIOException) {
/* 3037 */         if (this.cacheRequest != null) {
/* 3038 */           this.cacheRequest.abort();
/*      */         }
/* 3040 */         throw localIOException;
/*      */       }
/*      */     }
/*      */ 
/*      */     public int read(byte[] paramArrayOfByte) throws IOException
/*      */     {
/* 3046 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */     }
/*      */ 
/*      */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*      */     {
/*      */       try {
/* 3052 */         int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
/*      */         int j;
/* 3055 */         if (this.inCache > 0) {
/* 3056 */           if (this.inCache >= i) {
/* 3057 */             this.inCache -= i;
/* 3058 */             j = 0;
/*      */           } else {
/* 3060 */             j = i - this.inCache;
/* 3061 */             this.inCache = 0;
/*      */           }
/*      */         }
/* 3064 */         else j = i;
/*      */ 
/* 3066 */         if ((j > 0) && (this.outputStream != null))
/* 3067 */           this.outputStream.write(paramArrayOfByte, paramInt1 + (i - j), j);
/* 3068 */         if (this.marked) {
/* 3069 */           this.markCount += i;
/*      */         }
/* 3071 */         return i;
/*      */       } catch (IOException localIOException) {
/* 3073 */         if (this.cacheRequest != null) {
/* 3074 */           this.cacheRequest.abort();
/*      */         }
/* 3076 */         throw localIOException;
/*      */       }
/*      */     }
/*      */ 
/*      */     public long skip(long paramLong)
/*      */       throws IOException
/*      */     {
/* 3089 */       long l = paramLong;
/*      */ 
/* 3091 */       if (this.skipBuffer == null) {
/* 3092 */         this.skipBuffer = new byte[8096];
/*      */       }
/* 3094 */       byte[] arrayOfByte = this.skipBuffer;
/*      */ 
/* 3096 */       if (paramLong <= 0L) {
/* 3097 */         return 0L;
/*      */       }
/*      */ 
/* 3100 */       while (l > 0L) {
/* 3101 */         int i = read(arrayOfByte, 0, (int)Math.min(8096L, l));
/*      */ 
/* 3103 */         if (i < 0) {
/*      */           break;
/*      */         }
/* 3106 */         l -= i;
/*      */       }
/*      */ 
/* 3109 */       return paramLong - l;
/*      */     }
/*      */ 
/*      */     public void close() throws IOException
/*      */     {
/*      */       try {
/* 3115 */         if (this.outputStream != null) {
/* 3116 */           if (read() != -1)
/* 3117 */             this.cacheRequest.abort();
/*      */           else {
/* 3119 */             this.outputStream.close();
/*      */           }
/*      */         }
/* 3122 */         super.close();
/*      */       } catch (IOException localIOException) {
/* 3124 */         if (this.cacheRequest != null) {
/* 3125 */           this.cacheRequest.abort();
/*      */         }
/* 3127 */         throw localIOException;
/*      */       } finally {
/* 3129 */         HttpURLConnection.this.http = null;
/* 3130 */         HttpURLConnection.this.checkResponseCredentials(true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class StreamingOutputStream extends FilterOutputStream
/*      */   {
/*      */     long expected;
/*      */     long written;
/*      */     boolean closed;
/*      */     boolean error;
/*      */     IOException errorExcp;
/*      */ 
/*      */     StreamingOutputStream(OutputStream paramLong, long arg3)
/*      */     {
/* 3150 */       super();
/*      */       Object localObject;
/* 3151 */       this.expected = localObject;
/* 3152 */       this.written = 0L;
/* 3153 */       this.closed = false;
/* 3154 */       this.error = false;
/*      */     }
/*      */ 
/*      */     public void write(int paramInt) throws IOException
/*      */     {
/* 3159 */       checkError();
/* 3160 */       this.written += 1L;
/* 3161 */       if ((this.expected != -1L) && (this.written > this.expected)) {
/* 3162 */         throw new IOException("too many bytes written");
/*      */       }
/* 3164 */       this.out.write(paramInt);
/*      */     }
/*      */ 
/*      */     public void write(byte[] paramArrayOfByte) throws IOException
/*      */     {
/* 3169 */       write(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */     }
/*      */ 
/*      */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*      */     {
/* 3174 */       checkError();
/* 3175 */       this.written += paramInt2;
/* 3176 */       if ((this.expected != -1L) && (this.written > this.expected)) {
/* 3177 */         this.out.close();
/* 3178 */         throw new IOException("too many bytes written");
/*      */       }
/* 3180 */       this.out.write(paramArrayOfByte, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     void checkError() throws IOException {
/* 3184 */       if (this.closed) {
/* 3185 */         throw new IOException("Stream is closed");
/*      */       }
/* 3187 */       if (this.error) {
/* 3188 */         throw this.errorExcp;
/*      */       }
/* 3190 */       if (((PrintStream)this.out).checkError())
/* 3191 */         throw new IOException("Error writing request body to server");
/*      */     }
/*      */ 
/*      */     boolean writtenOK()
/*      */     {
/* 3200 */       return (this.closed) && (!this.error);
/*      */     }
/*      */ 
/*      */     public void close() throws IOException
/*      */     {
/* 3205 */       if (this.closed) {
/* 3206 */         return;
/*      */       }
/* 3208 */       this.closed = true;
/* 3209 */       if (this.expected != -1L)
/*      */       {
/* 3211 */         if (this.written != this.expected) {
/* 3212 */           this.error = true;
/* 3213 */           this.errorExcp = new IOException("insufficient data written");
/* 3214 */           this.out.close();
/* 3215 */           throw this.errorExcp;
/*      */         }
/* 3217 */         super.flush();
/*      */       }
/*      */       else {
/* 3220 */         super.close();
/*      */ 
/* 3222 */         OutputStream localOutputStream = HttpURLConnection.this.http.getOutputStream();
/* 3223 */         localOutputStream.write(13);
/* 3224 */         localOutputStream.write(10);
/* 3225 */         localOutputStream.flush();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static enum TunnelState
/*      */   {
/*  354 */     NONE, 
/*      */ 
/*  357 */     SETUP, 
/*      */ 
/*  360 */     TUNNELING;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.http.HttpURLConnection
 * JD-Core Version:    0.6.2
 */