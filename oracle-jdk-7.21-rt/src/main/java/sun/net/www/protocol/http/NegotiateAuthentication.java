/*     */ package sun.net.www.protocol.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Authenticator.RequestorType;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import sun.misc.BASE64Decoder;
/*     */ import sun.misc.BASE64Encoder;
/*     */ import sun.net.www.HeaderParser;
/*     */ 
/*     */ class NegotiateAuthentication extends AuthenticationInfo
/*     */ {
/*     */   private static final long serialVersionUID = 100L;
/*     */   private final HttpCallerInfo hci;
/*  56 */   static HashMap<String, Boolean> supported = null;
/*  57 */   static HashMap<String, Negotiator> cache = null;
/*     */ 
/*  60 */   private Negotiator negotiator = null;
/*     */ 
/*     */   public NegotiateAuthentication(HttpCallerInfo paramHttpCallerInfo)
/*     */   {
/*  67 */     super(Authenticator.RequestorType.PROXY == paramHttpCallerInfo.authType ? 'p' : 's', paramHttpCallerInfo.scheme.equalsIgnoreCase("Negotiate") ? AuthScheme.NEGOTIATE : AuthScheme.KERBEROS, paramHttpCallerInfo.url, "");
/*     */ 
/*  71 */     this.hci = paramHttpCallerInfo;
/*     */   }
/*     */ 
/*     */   public boolean supportsPreemptiveAuthorization()
/*     */   {
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   public static synchronized boolean isSupported(HttpCallerInfo paramHttpCallerInfo)
/*     */   {
/*  94 */     if (supported == null) {
/*  95 */       supported = new HashMap();
/*  96 */       cache = new HashMap();
/*     */     }
/*  98 */     String str = paramHttpCallerInfo.host;
/*  99 */     str = str.toLowerCase();
/* 100 */     if (supported.containsKey(str)) {
/* 101 */       return ((Boolean)supported.get(str)).booleanValue();
/*     */     }
/*     */ 
/* 104 */     Negotiator localNegotiator = Negotiator.getNegotiator(paramHttpCallerInfo);
/* 105 */     if (localNegotiator != null) {
/* 106 */       supported.put(str, Boolean.valueOf(true));
/*     */ 
/* 109 */       cache.put(str, localNegotiator);
/* 110 */       return true;
/*     */     }
/* 112 */     supported.put(str, Boolean.valueOf(false));
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   public String getHeaderValue(URL paramURL, String paramString)
/*     */   {
/* 122 */     throw new RuntimeException("getHeaderValue not supported");
/*     */   }
/*     */ 
/*     */   public boolean isAuthorizationStale(String paramString)
/*     */   {
/* 135 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       byte[] arrayOfByte = null;
/* 152 */       String[] arrayOfString = paramString.split("\\s+");
/* 153 */       if (arrayOfString.length > 1) {
/* 154 */         arrayOfByte = new BASE64Decoder().decodeBuffer(arrayOfString[1]);
/*     */       }
/* 156 */       String str = this.hci.scheme + " " + new B64Encoder().encode(arrayOfByte == null ? firstToken() : nextToken(arrayOfByte));
/*     */ 
/* 159 */       paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str);
/* 160 */       return true; } catch (IOException localIOException) {
/*     */     }
/* 162 */     return false;
/*     */   }
/*     */ 
/*     */   private byte[] firstToken()
/*     */     throws IOException
/*     */   {
/* 173 */     this.negotiator = null;
/* 174 */     if (cache != null) {
/* 175 */       synchronized (cache) {
/* 176 */         this.negotiator = ((Negotiator)cache.get(getHost()));
/* 177 */         if (this.negotiator != null) {
/* 178 */           cache.remove(getHost());
/*     */         }
/*     */       }
/*     */     }
/* 182 */     if (this.negotiator == null) {
/* 183 */       this.negotiator = Negotiator.getNegotiator(this.hci);
/* 184 */       if (this.negotiator == null) {
/* 185 */         ??? = new IOException("Cannot initialize Negotiator");
/* 186 */         throw ((Throwable)???);
/*     */       }
/*     */     }
/*     */ 
/* 190 */     return this.negotiator.firstToken();
/*     */   }
/*     */ 
/*     */   private byte[] nextToken(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 201 */     return this.negotiator.nextToken(paramArrayOfByte);
/*     */   }
/*     */   class B64Encoder extends BASE64Encoder {
/*     */     B64Encoder() {
/*     */     }
/* 206 */     protected int bytesPerLine() { return 100000; }
/*     */ 
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.http.NegotiateAuthentication
 * JD-Core Version:    0.6.2
 */