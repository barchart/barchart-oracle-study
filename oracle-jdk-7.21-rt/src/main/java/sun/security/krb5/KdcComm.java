/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Security;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import sun.security.krb5.internal.KRBError;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.NetClient;
/*     */ 
/*     */ public final class KdcComm
/*     */ {
/*     */   private static int defaultKdcRetryLimit;
/*     */   private static int defaultKdcTimeout;
/*     */   private static int defaultUdpPrefLimit;
/*  74 */   private static final boolean DEBUG = Krb5.DEBUG;
/*     */   private static final String BAD_POLICY_KEY = "krb5.kdc.bad.policy";
/*  86 */   private static int tryLessMaxRetries = 1;
/*  87 */   private static int tryLessTimeout = 5000;
/*     */   private static BpType badPolicy;
/*     */   private String realm;
/*     */ 
/*     */   public static void initStatic()
/*     */   {
/*  99 */     String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run() {
/* 102 */         return Security.getProperty("krb5.kdc.bad.policy");
/*     */       }
/*     */     });
/* 105 */     if (str1 != null) {
/* 106 */       str1 = str1.toLowerCase(Locale.ENGLISH);
/* 107 */       String[] arrayOfString1 = str1.split(":");
/* 108 */       if ("tryless".equals(arrayOfString1[0])) {
/* 109 */         if (arrayOfString1.length > 1) {
/* 110 */           String[] arrayOfString2 = arrayOfString1[1].split(",");
/*     */           try {
/* 112 */             int k = Integer.parseInt(arrayOfString2[0]);
/* 113 */             if (arrayOfString2.length > 1) {
/* 114 */               tryLessTimeout = Integer.parseInt(arrayOfString2[1]);
/*     */             }
/*     */ 
/* 117 */             tryLessMaxRetries = k;
/*     */           }
/*     */           catch (NumberFormatException localNumberFormatException)
/*     */           {
/* 121 */             if (DEBUG) {
/* 122 */               System.out.println("Invalid krb5.kdc.bad.policy parameter for tryLess: " + str1 + ", use default");
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 128 */         badPolicy = BpType.TRY_LESS;
/* 129 */       } else if ("trylast".equals(arrayOfString1[0])) {
/* 130 */         badPolicy = BpType.TRY_LAST;
/*     */       } else {
/* 132 */         badPolicy = BpType.NONE;
/*     */       }
/*     */     } else {
/* 135 */       badPolicy = BpType.NONE;
/*     */     }
/*     */ 
/* 139 */     int i = -1;
/* 140 */     int j = -1;
/* 141 */     int m = -1;
/*     */     try
/*     */     {
/* 144 */       Config localConfig = Config.getInstance();
/* 145 */       String str2 = localConfig.getDefault("kdc_timeout", "libdefaults");
/* 146 */       i = parsePositiveIntString(str2);
/* 147 */       str2 = localConfig.getDefault("max_retries", "libdefaults");
/* 148 */       j = parsePositiveIntString(str2);
/* 149 */       str2 = localConfig.getDefault("udp_preference_limit", "libdefaults");
/* 150 */       m = parsePositiveIntString(str2);
/*     */     }
/*     */     catch (Exception localException) {
/* 153 */       if (DEBUG) {
/* 154 */         System.out.println("Exception in getting KDC communication settings, using default value " + localException.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 159 */     defaultKdcTimeout = i > 0 ? i : 30000;
/* 160 */     defaultKdcRetryLimit = j > 0 ? j : 3;
/*     */ 
/* 162 */     defaultUdpPrefLimit = m;
/*     */ 
/* 164 */     KdcAccessibility.access$000();
/*     */   }
/*     */ 
/*     */   public KdcComm(String paramString)
/*     */     throws KrbException
/*     */   {
/* 173 */     if (paramString == null) {
/* 174 */       paramString = Config.getInstance().getDefaultRealm();
/* 175 */       if (paramString == null) {
/* 176 */         throw new KrbException(60, "Cannot find default realm");
/*     */       }
/*     */     }
/*     */ 
/* 180 */     this.realm = paramString;
/*     */   }
/*     */ 
/*     */   public byte[] send(byte[] paramArrayOfByte) throws IOException, KrbException
/*     */   {
/* 185 */     int i = getRealmSpecificValue(this.realm, "udp_preference_limit", defaultUdpPrefLimit);
/*     */ 
/* 188 */     boolean bool = (i > 0) && (paramArrayOfByte != null) && (paramArrayOfByte.length > i);
/*     */ 
/* 191 */     return send(paramArrayOfByte, bool);
/*     */   }
/*     */ 
/*     */   private byte[] send(byte[] paramArrayOfByte, boolean paramBoolean)
/*     */     throws IOException, KrbException
/*     */   {
/* 197 */     if (paramArrayOfByte == null)
/* 198 */       return null;
/* 199 */     Object localObject1 = null;
/* 200 */     Config localConfig = Config.getInstance();
/*     */ 
/* 202 */     if (this.realm == null) {
/* 203 */       this.realm = localConfig.getDefaultRealm();
/* 204 */       if (this.realm == null) {
/* 205 */         throw new KrbException(60, "Cannot find default realm");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 210 */     String str1 = localConfig.getKDCList(this.realm);
/* 211 */     if (str1 == null) {
/* 212 */       throw new KrbException("Cannot get kdc for realm " + this.realm);
/*     */     }
/* 214 */     Object localObject2 = null;
/* 215 */     byte[] arrayOfByte = null;
/* 216 */     for (String str2 : KdcAccessibility.list(str1)) {
/* 217 */       localObject2 = str2;
/*     */       try {
/* 219 */         arrayOfByte = send(paramArrayOfByte, localObject2, paramBoolean);
/* 220 */         KRBError localKRBError = null;
/*     */         try {
/* 222 */           localKRBError = new KRBError(arrayOfByte);
/*     */         }
/*     */         catch (Exception localException2) {
/*     */         }
/* 226 */         if ((localKRBError != null) && (localKRBError.getErrorCode() == 52))
/*     */         {
/* 228 */           arrayOfByte = send(paramArrayOfByte, localObject2, true);
/*     */         }
/* 230 */         KdcAccessibility.removeBad(localObject2);
/*     */       }
/*     */       catch (Exception localException1) {
/* 233 */         if (DEBUG) {
/* 234 */           System.out.println(">>> KrbKdcReq send: error trying " + localObject2);
/*     */ 
/* 236 */           localException1.printStackTrace(System.out);
/*     */         }
/* 238 */         KdcAccessibility.addBad(localObject2);
/* 239 */         localObject1 = localException1;
/*     */       }
/*     */     }
/* 242 */     if ((arrayOfByte == null) && (localObject1 != null)) {
/* 243 */       if ((localObject1 instanceof IOException)) {
/* 244 */         throw ((IOException)localObject1);
/*     */       }
/* 246 */       throw ((KrbException)localObject1);
/*     */     }
/*     */ 
/* 249 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private byte[] send(byte[] paramArrayOfByte, String paramString, boolean paramBoolean)
/*     */     throws IOException, KrbException
/*     */   {
/* 257 */     if (paramArrayOfByte == null) {
/* 258 */       return null;
/*     */     }
/* 260 */     int i = 88;
/* 261 */     int j = getRealmSpecificValue(this.realm, "max_retries", defaultKdcRetryLimit);
/*     */ 
/* 263 */     int k = getRealmSpecificValue(this.realm, "kdc_timeout", defaultKdcTimeout);
/*     */ 
/* 265 */     if ((badPolicy == BpType.TRY_LESS) && (KdcAccessibility.isBad(paramString)))
/*     */     {
/* 267 */       if (j > tryLessMaxRetries) {
/* 268 */         j = tryLessMaxRetries;
/*     */       }
/* 270 */       if (k > tryLessTimeout) {
/* 271 */         k = tryLessTimeout;
/*     */       }
/*     */     }
/*     */ 
/* 275 */     String str1 = null;
/* 276 */     String str2 = null;
/*     */     int m;
/* 278 */     if (paramString.charAt(0) == '[') {
/* 279 */       m = paramString.indexOf(']', 1);
/* 280 */       if (m == -1) {
/* 281 */         throw new IOException("Illegal KDC: " + paramString);
/*     */       }
/* 283 */       str1 = paramString.substring(1, m);
/* 284 */       if (m != paramString.length() - 1) {
/* 285 */         if (paramString.charAt(m + 1) != ':') {
/* 286 */           throw new IOException("Illegal KDC: " + paramString);
/*     */         }
/* 288 */         str2 = paramString.substring(m + 2);
/*     */       }
/*     */     } else {
/* 291 */       m = paramString.indexOf(':');
/* 292 */       if (m == -1) {
/* 293 */         str1 = paramString;
/*     */       } else {
/* 295 */         int n = paramString.indexOf(':', m + 1);
/* 296 */         if (n > 0) {
/* 297 */           str1 = paramString;
/*     */         } else {
/* 299 */           str1 = paramString.substring(0, m);
/* 300 */           str2 = paramString.substring(m + 1);
/*     */         }
/*     */       }
/*     */     }
/* 304 */     if (str2 != null) {
/* 305 */       m = parsePositiveIntString(str2);
/* 306 */       if (m > 0) {
/* 307 */         i = m;
/*     */       }
/*     */     }
/* 310 */     if (DEBUG) {
/* 311 */       System.out.println(">>> KrbKdcReq send: kdc=" + str1 + (paramBoolean ? " TCP:" : " UDP:") + i + ", timeout=" + k + ", number of retries =" + j + ", #bytes=" + paramArrayOfByte.length);
/*     */     }
/*     */ 
/* 320 */     KdcCommunication localKdcCommunication = new KdcCommunication(str1, i, paramBoolean, k, j, paramArrayOfByte);
/*     */     try
/*     */     {
/* 323 */       byte[] arrayOfByte = (byte[])AccessController.doPrivileged(localKdcCommunication);
/* 324 */       if (DEBUG) {
/* 325 */         System.out.println(">>> KrbKdcReq send: #bytes read=" + (arrayOfByte != null ? arrayOfByte.length : 0));
/*     */       }
/*     */ 
/* 328 */       return arrayOfByte;
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 330 */       Exception localException = localPrivilegedActionException.getException();
/* 331 */       if ((localException instanceof IOException)) {
/* 332 */         throw ((IOException)localException);
/*     */       }
/* 334 */       throw ((KrbException)localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getRealmSpecificValue(String paramString1, String paramString2, int paramInt)
/*     */   {
/* 418 */     int i = paramInt;
/*     */ 
/* 420 */     if (paramString1 == null) return i;
/*     */ 
/* 422 */     int j = -1;
/*     */     try {
/* 424 */       String str = Config.getInstance().getDefault(paramString2, paramString1);
/*     */ 
/* 426 */       j = parsePositiveIntString(str);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/* 431 */     if (j > 0) i = j;
/*     */ 
/* 433 */     return i;
/*     */   }
/*     */ 
/*     */   private static int parsePositiveIntString(String paramString) {
/* 437 */     if (paramString == null) {
/* 438 */       return -1;
/*     */     }
/* 440 */     int i = -1;
/*     */     try
/*     */     {
/* 443 */       i = Integer.parseInt(paramString);
/*     */     } catch (Exception localException) {
/* 445 */       return -1;
/*     */     }
/*     */ 
/* 448 */     if (i >= 0) {
/* 449 */       return i;
/*     */     }
/* 451 */     return -1;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  92 */     initStatic();
/*     */   }
/*     */ 
/*     */   private static enum BpType
/*     */   {
/*  84 */     NONE, TRY_LAST, TRY_LESS;
/*     */   }
/*     */ 
/*     */   static class KdcAccessibility
/*     */   {
/* 465 */     private static Set<String> bads = new HashSet();
/*     */ 
/*     */     private static synchronized void addBad(String paramString) {
/* 468 */       if (KdcComm.DEBUG) {
/* 469 */         System.out.println(">>> KdcAccessibility: add " + paramString);
/*     */       }
/* 471 */       bads.add(paramString);
/*     */     }
/*     */ 
/*     */     private static synchronized void removeBad(String paramString) {
/* 475 */       if (KdcComm.DEBUG) {
/* 476 */         System.out.println(">>> KdcAccessibility: remove " + paramString);
/*     */       }
/* 478 */       bads.remove(paramString);
/*     */     }
/*     */ 
/*     */     private static synchronized boolean isBad(String paramString) {
/* 482 */       return bads.contains(paramString);
/*     */     }
/*     */ 
/*     */     private static synchronized void reset() {
/* 486 */       if (KdcComm.DEBUG) {
/* 487 */         System.out.println(">>> KdcAccessibility: reset");
/*     */       }
/* 489 */       bads.clear();
/*     */     }
/*     */ 
/*     */     private static synchronized String[] list(String paramString)
/*     */     {
/* 494 */       StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 495 */       ArrayList localArrayList1 = new ArrayList();
/* 496 */       if (KdcComm.badPolicy == KdcComm.BpType.TRY_LAST) {
/* 497 */         ArrayList localArrayList2 = new ArrayList();
/* 498 */         while (localStringTokenizer.hasMoreTokens()) {
/* 499 */           String str = localStringTokenizer.nextToken();
/* 500 */           if (bads.contains(str)) localArrayList2.add(str); else {
/* 501 */             localArrayList1.add(str);
/*     */           }
/*     */         }
/* 504 */         localArrayList1.addAll(localArrayList2);
/*     */       }
/*     */       else
/*     */       {
/* 508 */         while (localStringTokenizer.hasMoreTokens()) {
/* 509 */           localArrayList1.add(localStringTokenizer.nextToken());
/*     */         }
/*     */       }
/* 512 */       return (String[])localArrayList1.toArray(new String[localArrayList1.size()]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class KdcCommunication
/*     */     implements PrivilegedExceptionAction<byte[]>
/*     */   {
/*     */     private String kdc;
/*     */     private int port;
/*     */     private boolean useTCP;
/*     */     private int timeout;
/*     */     private int retries;
/*     */     private byte[] obuf;
/*     */ 
/*     */     public KdcCommunication(String paramString, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
/*     */     {
/* 351 */       this.kdc = paramString;
/* 352 */       this.port = paramInt1;
/* 353 */       this.useTCP = paramBoolean;
/* 354 */       this.timeout = paramInt2;
/* 355 */       this.retries = paramInt3;
/* 356 */       this.obuf = paramArrayOfByte;
/*     */     }
/*     */ 
/*     */     public byte[] run()
/*     */       throws IOException, KrbException
/*     */     {
/* 364 */       byte[] arrayOfByte = null;
/*     */ 
/* 366 */       for (int i = 1; i <= this.retries; ) {
/* 367 */         String str = this.useTCP ? "TCP" : "UDP";
/* 368 */         NetClient localNetClient = NetClient.getInstance(str, this.kdc, this.port, this.timeout);
/*     */ 
/* 370 */         if (KdcComm.DEBUG) {
/* 371 */           System.out.println(">>> KDCCommunication: kdc=" + this.kdc + " " + str + ":" + this.port + ", timeout=" + this.timeout + ",Attempt =" + i + ", #bytes=" + this.obuf.length);
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 382 */           localNetClient.send(this.obuf);
/*     */ 
/* 386 */           arrayOfByte = localNetClient.receive();
/*     */ 
/* 398 */           localNetClient.close();
/*     */         }
/*     */         catch (SocketTimeoutException localSocketTimeoutException)
/*     */         {
/* 389 */           if (KdcComm.DEBUG) {
/* 390 */             System.out.println("SocketTimeOutException with attempt: " + i);
/*     */           }
/*     */ 
/* 393 */           if (i == this.retries) {
/* 394 */             arrayOfByte = null;
/* 395 */             throw localSocketTimeoutException;
/*     */           }
/*     */ 
/* 398 */           localNetClient.close(); } finally { localNetClient.close(); }
/*     */ 
/*     */       }
/* 401 */       return arrayOfByte;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.KdcComm
 * JD-Core Version:    0.6.2
 */