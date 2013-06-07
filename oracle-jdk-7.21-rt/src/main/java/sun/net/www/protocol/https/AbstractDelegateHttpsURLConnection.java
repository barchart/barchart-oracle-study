/*     */ package sun.net.www.protocol.https;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Proxy;
/*     */ import java.net.SecureCacheResponse;
/*     */ import java.net.URL;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.security.cert.X509Certificate;
/*     */ import sun.net.www.http.HttpClient;
/*     */ import sun.net.www.protocol.http.Handler;
/*     */ import sun.net.www.protocol.http.HttpURLConnection;
/*     */ 
/*     */ public abstract class AbstractDelegateHttpsURLConnection extends HttpURLConnection
/*     */ {
/*     */   protected AbstractDelegateHttpsURLConnection(URL paramURL, Handler paramHandler)
/*     */     throws IOException
/*     */   {
/*  50 */     this(paramURL, null, paramHandler);
/*     */   }
/*     */ 
/*     */   protected AbstractDelegateHttpsURLConnection(URL paramURL, Proxy paramProxy, Handler paramHandler) throws IOException
/*     */   {
/*  55 */     super(paramURL, paramProxy, paramHandler);
/*     */   }
/*     */ 
/*     */   protected abstract SSLSocketFactory getSSLSocketFactory();
/*     */ 
/*     */   protected abstract HostnameVerifier getHostnameVerifier();
/*     */ 
/*     */   public void setNewClient(URL paramURL)
/*     */     throws IOException
/*     */   {
/*  80 */     setNewClient(paramURL, false);
/*     */   }
/*     */ 
/*     */   public void setNewClient(URL paramURL, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  96 */     this.http = HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramBoolean);
/*     */ 
/* 100 */     ((HttpsClient)this.http).afterConnect();
/*     */   }
/*     */ 
/*     */   public void setProxiedClient(URL paramURL, String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 118 */     setProxiedClient(paramURL, paramString, paramInt, false);
/*     */   }
/*     */ 
/*     */   public void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 138 */     proxiedConnect(paramURL, paramString, paramInt, paramBoolean);
/* 139 */     if (!this.http.isCachedConnection()) {
/* 140 */       doTunneling();
/*     */     }
/* 142 */     ((HttpsClient)this.http).afterConnect();
/*     */   }
/*     */ 
/*     */   protected void proxiedConnect(URL paramURL, String paramString, int paramInt, boolean paramBoolean) throws IOException
/*     */   {
/* 147 */     if (this.connected)
/* 148 */       return;
/* 149 */     this.http = HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramString, paramInt, paramBoolean);
/*     */ 
/* 153 */     this.connected = true;
/*     */   }
/*     */ 
/*     */   public boolean isConnected()
/*     */   {
/* 160 */     return this.connected;
/*     */   }
/*     */ 
/*     */   public void setConnected(boolean paramBoolean)
/*     */   {
/* 167 */     this.connected = paramBoolean;
/*     */   }
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/* 175 */     if (this.connected)
/* 176 */       return;
/* 177 */     plainConnect();
/* 178 */     if (this.cachedResponse != null)
/*     */     {
/* 180 */       return;
/*     */     }
/* 182 */     if ((!this.http.isCachedConnection()) && (this.http.needsTunneling())) {
/* 183 */       doTunneling();
/*     */     }
/* 185 */     ((HttpsClient)this.http).afterConnect();
/*     */   }
/*     */ 
/*     */   protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt)
/*     */     throws IOException
/*     */   {
/* 191 */     return HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramProxy, true, paramInt);
/*     */   }
/*     */ 
/*     */   protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 199 */     return HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramProxy, paramBoolean, paramInt);
/*     */   }
/*     */ 
/*     */   public String getCipherSuite()
/*     */   {
/* 208 */     if (this.cachedResponse != null) {
/* 209 */       return ((SecureCacheResponse)this.cachedResponse).getCipherSuite();
/*     */     }
/* 211 */     if (this.http == null) {
/* 212 */       throw new IllegalStateException("connection not yet open");
/*     */     }
/* 214 */     return ((HttpsClient)this.http).getCipherSuite();
/*     */   }
/*     */ 
/*     */   public Certificate[] getLocalCertificates()
/*     */   {
/* 223 */     if (this.cachedResponse != null) {
/* 224 */       List localList = ((SecureCacheResponse)this.cachedResponse).getLocalCertificateChain();
/* 225 */       if (localList == null) {
/* 226 */         return null;
/*     */       }
/* 228 */       return (Certificate[])localList.toArray();
/*     */     }
/*     */ 
/* 231 */     if (this.http == null) {
/* 232 */       throw new IllegalStateException("connection not yet open");
/*     */     }
/* 234 */     return ((HttpsClient)this.http).getLocalCertificates();
/*     */   }
/*     */ 
/*     */   public Certificate[] getServerCertificates()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 245 */     if (this.cachedResponse != null) {
/* 246 */       List localList = ((SecureCacheResponse)this.cachedResponse).getServerCertificateChain();
/* 247 */       if (localList == null) {
/* 248 */         return null;
/*     */       }
/* 250 */       return (Certificate[])localList.toArray();
/*     */     }
/*     */ 
/* 254 */     if (this.http == null) {
/* 255 */       throw new IllegalStateException("connection not yet open");
/*     */     }
/* 257 */     return ((HttpsClient)this.http).getServerCertificates();
/*     */   }
/*     */ 
/*     */   public X509Certificate[] getServerCertificateChain()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 267 */     if (this.cachedResponse != null) {
/* 268 */       throw new UnsupportedOperationException("this method is not supported when using cache");
/*     */     }
/* 270 */     if (this.http == null) {
/* 271 */       throw new IllegalStateException("connection not yet open");
/*     */     }
/* 273 */     return ((HttpsClient)this.http).getServerCertificateChain();
/*     */   }
/*     */ 
/*     */   Principal getPeerPrincipal()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 284 */     if (this.cachedResponse != null) {
/* 285 */       return ((SecureCacheResponse)this.cachedResponse).getPeerPrincipal();
/*     */     }
/*     */ 
/* 288 */     if (this.http == null) {
/* 289 */       throw new IllegalStateException("connection not yet open");
/*     */     }
/* 291 */     return ((HttpsClient)this.http).getPeerPrincipal();
/*     */   }
/*     */ 
/*     */   Principal getLocalPrincipal()
/*     */   {
/* 301 */     if (this.cachedResponse != null) {
/* 302 */       return ((SecureCacheResponse)this.cachedResponse).getLocalPrincipal();
/*     */     }
/*     */ 
/* 305 */     if (this.http == null) {
/* 306 */       throw new IllegalStateException("connection not yet open");
/*     */     }
/* 308 */     return ((HttpsClient)this.http).getLocalPrincipal();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection
 * JD-Core Version:    0.6.2
 */