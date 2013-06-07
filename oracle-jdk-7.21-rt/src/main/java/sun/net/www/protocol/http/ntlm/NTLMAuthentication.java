/*     */ package sun.net.www.protocol.http.ntlm;
/*     */ 
/*     */ import com.sun.security.ntlm.Client;
/*     */ import com.sun.security.ntlm.NTLMException;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Random;
/*     */ import sun.misc.BASE64Decoder;
/*     */ import sun.net.www.HeaderParser;
/*     */ import sun.net.www.protocol.http.AuthScheme;
/*     */ import sun.net.www.protocol.http.AuthenticationInfo;
/*     */ import sun.net.www.protocol.http.HttpURLConnection;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class NTLMAuthentication extends AuthenticationInfo
/*     */ {
/*     */   private static final long serialVersionUID = 170L;
/*  71 */   private static final NTLMAuthenticationCallback NTLMAuthCallback = NTLMAuthenticationCallback.getNTLMAuthenticationCallback();
/*     */   private String hostname;
/*  78 */   private static String defaultDomain = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.ntlm.domain", "domain"));
/*     */   PasswordAuthentication pw;
/*     */   Client client;
/*     */ 
/*     */   public static boolean supportsTransparentAuth()
/*     */   {
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isTrustedSite(URL paramURL)
/*     */   {
/*  92 */     return NTLMAuthCallback.isTrustedSite(paramURL);
/*     */   }
/*     */ 
/*     */   private void init0()
/*     */   {
/*  97 */     this.hostname = ((String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run() {
/*     */         String str;
/*     */         try {
/* 102 */           str = InetAddress.getLocalHost().getHostName().toUpperCase();
/*     */         } catch (UnknownHostException localUnknownHostException) {
/* 104 */           str = "localhost";
/*     */         }
/* 106 */         return str;
/*     */       }
/*     */     }));
/* 109 */     int i = this.hostname.indexOf('.');
/* 110 */     if (i != -1)
/* 111 */       this.hostname = this.hostname.substring(0, i);
/*     */   }
/*     */ 
/*     */   public NTLMAuthentication(boolean paramBoolean, URL paramURL, PasswordAuthentication paramPasswordAuthentication)
/*     */   {
/* 125 */     super(paramBoolean ? 'p' : 's', AuthScheme.NTLM, paramURL, "");
/*     */ 
/* 129 */     init(paramPasswordAuthentication);
/*     */   }
/*     */ 
/*     */   private void init(PasswordAuthentication paramPasswordAuthentication)
/*     */   {
/* 136 */     this.pw = paramPasswordAuthentication;
/* 137 */     String str3 = paramPasswordAuthentication.getUserName();
/* 138 */     int i = str3.indexOf('\\');
/*     */     String str1;
/*     */     String str2;
/* 139 */     if (i == -1) {
/* 140 */       str1 = str3;
/* 141 */       str2 = defaultDomain;
/*     */     } else {
/* 143 */       str2 = str3.substring(0, i).toUpperCase();
/* 144 */       str1 = str3.substring(i + 1);
/*     */     }
/* 146 */     char[] arrayOfChar = paramPasswordAuthentication.getPassword();
/* 147 */     init0();
/*     */     try {
/* 149 */       this.client = new Client(System.getProperty("ntlm.version"), this.hostname, str1, str2, arrayOfChar);
/*     */     }
/*     */     catch (NTLMException localNTLMException1) {
/*     */       try {
/* 153 */         this.client = new Client(null, this.hostname, str1, str2, arrayOfChar);
/*     */       }
/*     */       catch (NTLMException localNTLMException2) {
/* 156 */         throw new AssertionError("Really?");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public NTLMAuthentication(boolean paramBoolean, String paramString, int paramInt, PasswordAuthentication paramPasswordAuthentication)
/*     */   {
/* 166 */     super(paramBoolean ? 'p' : 's', AuthScheme.NTLM, paramString, paramInt, "");
/*     */ 
/* 171 */     init(paramPasswordAuthentication);
/*     */   }
/*     */ 
/*     */   public boolean supportsPreemptiveAuthorization()
/*     */   {
/* 179 */     return false;
/*     */   }
/*     */ 
/*     */   public String getHeaderValue(URL paramURL, String paramString)
/*     */   {
/* 187 */     throw new RuntimeException("getHeaderValue not supported");
/*     */   }
/*     */ 
/*     */   public boolean isAuthorizationStale(String paramString)
/*     */   {
/* 200 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
/*     */   {
/*     */     try
/*     */     {
/*     */       String str1;
/* 216 */       if (paramString.length() < 6) {
/* 217 */         str1 = buildType1Msg();
/*     */       } else {
/* 219 */         String str2 = paramString.substring(5);
/* 220 */         str1 = buildType3Msg(str2);
/*     */       }
/* 222 */       paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str1);
/* 223 */       return true;
/*     */     } catch (IOException localIOException) {
/* 225 */       return false; } catch (GeneralSecurityException localGeneralSecurityException) {
/*     */     }
/* 227 */     return false;
/*     */   }
/*     */ 
/*     */   private String buildType1Msg()
/*     */   {
/* 232 */     byte[] arrayOfByte = this.client.type1();
/* 233 */     String str = "NTLM " + new B64Encoder().encode(arrayOfByte);
/* 234 */     return str;
/*     */   }
/*     */ 
/*     */   private String buildType3Msg(String paramString)
/*     */     throws GeneralSecurityException, IOException
/*     */   {
/* 242 */     byte[] arrayOfByte1 = new BASE64Decoder().decodeBuffer(paramString);
/* 243 */     byte[] arrayOfByte2 = new byte[8];
/* 244 */     new Random().nextBytes(arrayOfByte2);
/* 245 */     byte[] arrayOfByte3 = this.client.type3(arrayOfByte1, arrayOfByte2);
/* 246 */     String str = "NTLM " + new B64Encoder().encode(arrayOfByte3);
/* 247 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.http.ntlm.NTLMAuthentication
 * JD-Core Version:    0.6.2
 */