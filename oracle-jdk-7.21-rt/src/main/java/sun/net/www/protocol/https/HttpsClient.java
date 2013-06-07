/*     */ package sun.net.www.protocol.https;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.net.ssl.HandshakeCompletedEvent;
/*     */ import javax.net.ssl.HandshakeCompletedListener;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import sun.net.www.http.HttpClient;
/*     */ import sun.net.www.http.KeepAliveCache;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.ssl.SSLSocketImpl;
/*     */ import sun.security.util.HostnameChecker;
/*     */ 
/*     */ final class HttpsClient extends HttpClient
/*     */   implements HandshakeCompletedListener
/*     */ {
/*     */   private static final int httpsPortNumber = 443;
/*     */   private static final String defaultHVCanonicalName = "javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier";
/*     */   private HostnameVerifier hv;
/*     */   private SSLSocketFactory sslSocketFactory;
/*     */   private SSLSession session;
/*     */ 
/*     */   protected int getDefaultPort()
/*     */   {
/* 117 */     return 443;
/*     */   }
/*     */ 
/*     */   private String[] getCipherSuites()
/*     */   {
/* 137 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.cipherSuites"));
/*     */     String[] arrayOfString;
/* 140 */     if ((str == null) || ("".equals(str))) {
/* 141 */       arrayOfString = null;
/*     */     }
/*     */     else {
/* 144 */       Vector localVector = new Vector();
/*     */ 
/* 146 */       StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
/* 147 */       while (localStringTokenizer.hasMoreTokens())
/* 148 */         localVector.addElement(localStringTokenizer.nextToken());
/* 149 */       arrayOfString = new String[localVector.size()];
/* 150 */       for (int i = 0; i < arrayOfString.length; i++)
/* 151 */         arrayOfString[i] = ((String)localVector.elementAt(i));
/*     */     }
/* 153 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   private String[] getProtocols()
/*     */   {
/* 161 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.protocols"));
/*     */     String[] arrayOfString;
/* 164 */     if ((str == null) || ("".equals(str))) {
/* 165 */       arrayOfString = null;
/*     */     }
/*     */     else {
/* 168 */       Vector localVector = new Vector();
/*     */ 
/* 170 */       StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
/* 171 */       while (localStringTokenizer.hasMoreTokens())
/* 172 */         localVector.addElement(localStringTokenizer.nextToken());
/* 173 */       arrayOfString = new String[localVector.size()];
/* 174 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 175 */         arrayOfString[i] = ((String)localVector.elementAt(i));
/*     */       }
/*     */     }
/* 178 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   private String getUserAgent() {
/* 182 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.agent"));
/*     */ 
/* 184 */     if ((str == null) || (str.length() == 0)) {
/* 185 */       str = "JSSE";
/*     */     }
/* 187 */     return str;
/*     */   }
/*     */ 
/*     */   private static Proxy newHttpProxy(String paramString, int paramInt)
/*     */   {
/* 192 */     InetSocketAddress localInetSocketAddress = null;
/* 193 */     String str = paramString;
/* 194 */     final int i = paramInt < 0 ? 443 : paramInt;
/*     */     try {
/* 196 */       localInetSocketAddress = (InetSocketAddress)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public InetSocketAddress run() {
/* 199 */           return new InetSocketAddress(this.val$phost, i);
/*     */         } } );
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/*     */     }
/* 203 */     return new Proxy(Proxy.Type.HTTP, localInetSocketAddress);
/*     */   }
/*     */ 
/*     */   private HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL)
/*     */     throws IOException
/*     */   {
/* 228 */     this(paramSSLSocketFactory, paramURL, (String)null, -1);
/*     */   }
/*     */ 
/*     */   HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 237 */     this(paramSSLSocketFactory, paramURL, paramString, paramInt, -1);
/*     */   }
/*     */ 
/*     */   HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 247 */     this(paramSSLSocketFactory, paramURL, paramString == null ? null : newHttpProxy(paramString, paramInt1), paramInt2);
/*     */   }
/*     */ 
/*     */   HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, Proxy paramProxy, int paramInt)
/*     */     throws IOException
/*     */   {
/* 259 */     this.proxy = paramProxy;
/* 260 */     setSSLSocketFactory(paramSSLSocketFactory);
/* 261 */     this.proxyDisabled = true;
/*     */ 
/* 263 */     this.host = paramURL.getHost();
/* 264 */     this.url = paramURL;
/* 265 */     this.port = paramURL.getPort();
/* 266 */     if (this.port == -1) {
/* 267 */       this.port = getDefaultPort();
/*     */     }
/* 269 */     setConnectTimeout(paramInt);
/* 270 */     openServer();
/*     */   }
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier)
/*     */     throws IOException
/*     */   {
/* 279 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, true);
/*     */   }
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 285 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, (String)null, -1, paramBoolean);
/*     */   }
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 294 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, true);
/*     */   }
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 300 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, paramBoolean, -1);
/*     */   }
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt1, boolean paramBoolean, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 308 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString == null ? null : newHttpProxy(paramString, paramInt1), paramBoolean, paramInt2);
/*     */   }
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, Proxy paramProxy, boolean paramBoolean, int paramInt)
/*     */     throws IOException
/*     */   {
/* 318 */     HttpsClient localHttpsClient = null;
/* 319 */     if (paramBoolean)
/*     */     {
/* 321 */       localHttpsClient = (HttpsClient)kac.get(paramURL, paramSSLSocketFactory);
/* 322 */       if (localHttpsClient != null) {
/* 323 */         localHttpsClient.cachedHttpClient = true;
/*     */       }
/*     */     }
/* 326 */     if (localHttpsClient == null) {
/* 327 */       localHttpsClient = new HttpsClient(paramSSLSocketFactory, paramURL, paramProxy, paramInt);
/*     */     } else {
/* 329 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 330 */       if (localSecurityManager != null) {
/* 331 */         localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*     */       }
/* 333 */       localHttpsClient.url = paramURL;
/*     */     }
/* 335 */     localHttpsClient.setHostnameVerifier(paramHostnameVerifier);
/*     */ 
/* 337 */     return localHttpsClient;
/*     */   }
/*     */ 
/*     */   void setHostnameVerifier(HostnameVerifier paramHostnameVerifier)
/*     */   {
/* 342 */     this.hv = paramHostnameVerifier;
/*     */   }
/*     */ 
/*     */   void setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory) {
/* 346 */     this.sslSocketFactory = paramSSLSocketFactory;
/*     */   }
/*     */ 
/*     */   SSLSocketFactory getSSLSocketFactory() {
/* 350 */     return this.sslSocketFactory;
/*     */   }
/*     */ 
/*     */   protected Socket createSocket()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 361 */       return this.sslSocketFactory.createSocket();
/*     */     }
/*     */     catch (SocketException localSocketException)
/*     */     {
/* 369 */       Throwable localThrowable = localSocketException.getCause();
/* 370 */       if ((localThrowable != null) && ((localThrowable instanceof UnsupportedOperationException))) {
/* 371 */         return super.createSocket();
/*     */       }
/* 373 */       throw localSocketException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean needsTunneling()
/*     */   {
/* 381 */     return (this.proxy != null) && (this.proxy.type() != Proxy.Type.DIRECT) && (this.proxy.type() != Proxy.Type.SOCKS);
/*     */   }
/*     */ 
/*     */   public void afterConnect()
/*     */     throws IOException, UnknownHostException
/*     */   {
/* 387 */     if (!isCachedConnection()) {
/* 388 */       SSLSocket localSSLSocket = null;
/* 389 */       SSLSocketFactory localSSLSocketFactory = this.sslSocketFactory;
/*     */       try {
/* 391 */         if (!(this.serverSocket instanceof SSLSocket)) {
/* 392 */           localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(this.serverSocket, this.host, this.port, true);
/*     */         }
/*     */         else {
/* 395 */           localSSLSocket = (SSLSocket)this.serverSocket;
/* 396 */           if ((localSSLSocket instanceof SSLSocketImpl)) {
/* 397 */             ((SSLSocketImpl)localSSLSocket).setHost(this.host);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException1)
/*     */       {
/*     */         try
/*     */         {
/* 405 */           localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(this.host, this.port);
/*     */         } catch (IOException localIOException2) {
/* 407 */           throw localIOException1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 416 */       String[] arrayOfString1 = getProtocols();
/* 417 */       String[] arrayOfString2 = getCipherSuites();
/* 418 */       if (arrayOfString1 != null) {
/* 419 */         localSSLSocket.setEnabledProtocols(arrayOfString1);
/*     */       }
/* 421 */       if (arrayOfString2 != null) {
/* 422 */         localSSLSocket.setEnabledCipherSuites(arrayOfString2);
/*     */       }
/* 424 */       localSSLSocket.addHandshakeCompletedListener(this);
/*     */ 
/* 474 */       int i = 1;
/* 475 */       String str = localSSLSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
/*     */ 
/* 477 */       if ((str != null) && (str.length() != 0)) {
/* 478 */         if (str.equalsIgnoreCase("HTTPS"))
/*     */         {
/* 482 */           i = 0;
/*     */         }
/*     */       }
/*     */       else {
/* 486 */         int j = 0;
/*     */         Object localObject;
/* 491 */         if (this.hv != null) {
/* 492 */           localObject = this.hv.getClass().getCanonicalName();
/* 493 */           if ((localObject != null) && (((String)localObject).equalsIgnoreCase("javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier")))
/*     */           {
/* 495 */             j = 1;
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 501 */           j = 1;
/*     */         }
/*     */ 
/* 504 */         if (j != 0)
/*     */         {
/* 507 */           localObject = localSSLSocket.getSSLParameters();
/* 508 */           ((SSLParameters)localObject).setEndpointIdentificationAlgorithm("HTTPS");
/* 509 */           localSSLSocket.setSSLParameters((SSLParameters)localObject);
/*     */ 
/* 511 */           i = 0;
/*     */         }
/*     */       }
/*     */ 
/* 515 */       localSSLSocket.startHandshake();
/* 516 */       this.session = localSSLSocket.getSession();
/*     */ 
/* 518 */       this.serverSocket = localSSLSocket;
/*     */       try {
/* 520 */         this.serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), false, encoding);
/*     */       }
/*     */       catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */       {
/* 524 */         throw new InternalError(encoding + " encoding not found");
/*     */       }
/*     */ 
/* 528 */       if (i != 0) {
/* 529 */         checkURLSpoofing(this.hv);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 535 */       this.session = ((SSLSocket)this.serverSocket).getSession();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkURLSpoofing(HostnameVerifier paramHostnameVerifier)
/*     */     throws IOException
/*     */   {
/* 546 */     String str1 = this.url.getHost();
/*     */ 
/* 549 */     if ((str1 != null) && (str1.startsWith("[")) && (str1.endsWith("]"))) {
/* 550 */       str1 = str1.substring(1, str1.length() - 1);
/*     */     }
/*     */ 
/* 553 */     Certificate[] arrayOfCertificate = null;
/* 554 */     String str2 = this.session.getCipherSuite();
/*     */     try {
/* 556 */       HostnameChecker localHostnameChecker = HostnameChecker.getInstance((byte)1);
/*     */ 
/* 560 */       if (str2.startsWith("TLS_KRB5")) {
/* 561 */         if (!HostnameChecker.match(str1, getPeerPrincipal())) {
/* 562 */           throw new SSLPeerUnverifiedException("Hostname checker failed for Kerberos");
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 568 */         arrayOfCertificate = this.session.getPeerCertificates();
/*     */         java.security.cert.X509Certificate localX509Certificate;
/* 571 */         if ((arrayOfCertificate[0] instanceof java.security.cert.X509Certificate))
/*     */         {
/* 573 */           localX509Certificate = (java.security.cert.X509Certificate)arrayOfCertificate[0];
/*     */         }
/* 575 */         else throw new SSLPeerUnverifiedException("");
/*     */ 
/* 577 */         localHostnameChecker.match(str1, localX509Certificate);
/*     */       }
/*     */ 
/* 581 */       return;
/*     */     }
/*     */     catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException)
/*     */     {
/*     */     }
/*     */     catch (CertificateException localCertificateException)
/*     */     {
/*     */     }
/*     */ 
/* 594 */     if ((str2 != null) && (str2.indexOf("_anon_") != -1))
/* 595 */       return;
/* 596 */     if ((paramHostnameVerifier != null) && (paramHostnameVerifier.verify(str1, this.session)))
/*     */     {
/* 598 */       return;
/*     */     }
/*     */ 
/* 601 */     this.serverSocket.close();
/* 602 */     this.session.invalidate();
/*     */ 
/* 604 */     throw new IOException("HTTPS hostname wrong:  should be <" + this.url.getHost() + ">");
/*     */   }
/*     */ 
/*     */   protected void putInKeepAliveCache()
/*     */   {
/* 610 */     kac.put(this.url, this.sslSocketFactory, this);
/*     */   }
/*     */ 
/*     */   public void closeIdleConnection()
/*     */   {
/* 618 */     HttpClient localHttpClient = kac.get(this.url, this.sslSocketFactory);
/* 619 */     if (localHttpClient != null)
/* 620 */       localHttpClient.closeServer();
/*     */   }
/*     */ 
/*     */   String getCipherSuite()
/*     */   {
/* 628 */     return this.session.getCipherSuite();
/*     */   }
/*     */ 
/*     */   public Certificate[] getLocalCertificates()
/*     */   {
/* 636 */     return this.session.getLocalCertificates();
/*     */   }
/*     */ 
/*     */   Certificate[] getServerCertificates()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 647 */     return this.session.getPeerCertificates();
/*     */   }
/*     */ 
/*     */   javax.security.cert.X509Certificate[] getServerCertificateChain()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 657 */     return this.session.getPeerCertificateChain();
/*     */   }
/*     */ 
/*     */   Principal getPeerPrincipal()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/*     */     Object localObject;
/*     */     try
/*     */     {
/* 670 */       localObject = this.session.getPeerPrincipal();
/*     */     }
/*     */     catch (AbstractMethodError localAbstractMethodError)
/*     */     {
/* 674 */       Certificate[] arrayOfCertificate = this.session.getPeerCertificates();
/*     */ 
/* 676 */       localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
/*     */     }
/*     */ 
/* 679 */     return localObject;
/*     */   }
/*     */ 
/*     */   Principal getLocalPrincipal()
/*     */   {
/*     */     Object localObject;
/*     */     try
/*     */     {
/* 690 */       localObject = this.session.getLocalPrincipal();
/*     */     } catch (AbstractMethodError localAbstractMethodError) {
/* 692 */       localObject = null;
/*     */ 
/* 695 */       Certificate[] arrayOfCertificate = this.session.getLocalCertificates();
/*     */ 
/* 697 */       if (arrayOfCertificate != null) {
/* 698 */         localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
/*     */       }
/*     */     }
/*     */ 
/* 702 */     return localObject;
/*     */   }
/*     */ 
/*     */   public void handshakeCompleted(HandshakeCompletedEvent paramHandshakeCompletedEvent)
/*     */   {
/* 715 */     this.session = paramHandshakeCompletedEvent.getSession();
/*     */   }
/*     */ 
/*     */   public String getProxyHostUsed()
/*     */   {
/* 724 */     if (!needsTunneling()) {
/* 725 */       return null;
/*     */     }
/* 727 */     return super.getProxyHostUsed();
/*     */   }
/*     */ 
/*     */   public int getProxyPortUsed()
/*     */   {
/* 737 */     return (this.proxy == null) || (this.proxy.type() == Proxy.Type.DIRECT) || (this.proxy.type() == Proxy.Type.SOCKS) ? -1 : ((InetSocketAddress)this.proxy.address()).getPort();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.https.HttpsClient
 * JD-Core Version:    0.6.2
 */