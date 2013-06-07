/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.krb5.internal.AuthorizationData;
/*     */ import sun.security.krb5.internal.CredentialsUtil;
/*     */ import sun.security.krb5.internal.HostAddresses;
/*     */ import sun.security.krb5.internal.KDCOptions;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.Ticket;
/*     */ import sun.security.krb5.internal.TicketFlags;
/*     */ import sun.security.krb5.internal.ccache.CredentialsCache;
/*     */ import sun.security.krb5.internal.crypto.EType;
/*     */ 
/*     */ public class Credentials
/*     */ {
/*     */   Ticket ticket;
/*     */   PrincipalName client;
/*     */   PrincipalName server;
/*     */   EncryptionKey key;
/*     */   TicketFlags flags;
/*     */   KerberosTime authTime;
/*     */   KerberosTime startTime;
/*     */   KerberosTime endTime;
/*     */   KerberosTime renewTill;
/*     */   HostAddresses cAddr;
/*     */   EncryptionKey serviceKey;
/*     */   AuthorizationData authzData;
/*  61 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */   private static CredentialsCache cache;
/*  63 */   static boolean alreadyLoaded = false;
/*  64 */   private static boolean alreadyTried = false;
/*     */ 
/*     */   private static native Credentials acquireDefaultNativeCreds();
/*     */ 
/*     */   public Credentials(Ticket paramTicket, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData)
/*     */   {
/*  78 */     this(paramTicket, paramPrincipalName1, paramPrincipalName2, paramEncryptionKey, paramTicketFlags, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, paramKerberosTime4, paramHostAddresses);
/*     */ 
/*  80 */     this.authzData = paramAuthorizationData;
/*     */   }
/*     */ 
/*     */   public Credentials(Ticket paramTicket, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses)
/*     */   {
/*  93 */     this.ticket = paramTicket;
/*  94 */     this.client = paramPrincipalName1;
/*  95 */     this.server = paramPrincipalName2;
/*  96 */     this.key = paramEncryptionKey;
/*  97 */     this.flags = paramTicketFlags;
/*  98 */     this.authTime = paramKerberosTime1;
/*  99 */     this.startTime = paramKerberosTime2;
/* 100 */     this.endTime = paramKerberosTime3;
/* 101 */     this.renewTill = paramKerberosTime4;
/* 102 */     this.cAddr = paramHostAddresses;
/*     */   }
/*     */ 
/*     */   public Credentials(byte[] paramArrayOfByte1, String paramString1, String paramString2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
/*     */     throws KrbException, IOException
/*     */   {
/* 116 */     this(new Ticket(paramArrayOfByte1), new PrincipalName(paramString1, 1), new PrincipalName(paramString2, 2), new EncryptionKey(paramInt, paramArrayOfByte2), paramArrayOfBoolean == null ? null : new TicketFlags(paramArrayOfBoolean), paramDate1 == null ? null : new KerberosTime(paramDate1), paramDate2 == null ? null : new KerberosTime(paramDate2), paramDate3 == null ? null : new KerberosTime(paramDate3), paramDate4 == null ? null : new KerberosTime(paramDate4), null);
/*     */   }
/*     */ 
/*     */   public final PrincipalName getClient()
/*     */   {
/* 140 */     return this.client;
/*     */   }
/*     */ 
/*     */   public final PrincipalName getServer() {
/* 144 */     return this.server;
/*     */   }
/*     */ 
/*     */   public final EncryptionKey getSessionKey() {
/* 148 */     return this.key;
/*     */   }
/*     */ 
/*     */   public final Date getAuthTime() {
/* 152 */     if (this.authTime != null) {
/* 153 */       return this.authTime.toDate();
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   public final Date getStartTime()
/*     */   {
/* 160 */     if (this.startTime != null)
/*     */     {
/* 162 */       return this.startTime.toDate();
/*     */     }
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */   public final Date getEndTime() {
/* 168 */     if (this.endTime != null)
/*     */     {
/* 170 */       return this.endTime.toDate();
/*     */     }
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */   public final Date getRenewTill() {
/* 176 */     if (this.renewTill != null)
/*     */     {
/* 178 */       return this.renewTill.toDate();
/*     */     }
/* 180 */     return null;
/*     */   }
/*     */ 
/*     */   public final boolean[] getFlags() {
/* 184 */     if (this.flags == null)
/* 185 */       return null;
/* 186 */     return this.flags.toBooleanArray();
/*     */   }
/*     */ 
/*     */   public final InetAddress[] getClientAddresses()
/*     */   {
/* 191 */     if (this.cAddr == null) {
/* 192 */       return null;
/*     */     }
/* 194 */     return this.cAddr.getInetAddresses();
/*     */   }
/*     */ 
/*     */   public final byte[] getEncoded() {
/* 198 */     byte[] arrayOfByte = null;
/*     */     try {
/* 200 */       arrayOfByte = this.ticket.asn1Encode();
/*     */     } catch (Asn1Exception localAsn1Exception) {
/* 202 */       if (DEBUG)
/* 203 */         System.out.println(localAsn1Exception);
/*     */     } catch (IOException localIOException) {
/* 205 */       if (DEBUG)
/* 206 */         System.out.println(localIOException);
/*     */     }
/* 208 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public boolean isForwardable() {
/* 212 */     return this.flags.get(1);
/*     */   }
/*     */ 
/*     */   public boolean isRenewable() {
/* 216 */     return this.flags.get(8);
/*     */   }
/*     */ 
/*     */   public Ticket getTicket() {
/* 220 */     return this.ticket;
/*     */   }
/*     */ 
/*     */   public TicketFlags getTicketFlags() {
/* 224 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public AuthorizationData getAuthzData() {
/* 228 */     return this.authzData;
/*     */   }
/*     */ 
/*     */   public boolean checkDelegate()
/*     */   {
/* 236 */     return this.flags.get(13);
/*     */   }
/*     */ 
/*     */   public void resetDelegate()
/*     */   {
/* 248 */     this.flags.set(13, false);
/*     */   }
/*     */ 
/*     */   public Credentials renew() throws KrbException, IOException {
/* 252 */     KDCOptions localKDCOptions = new KDCOptions();
/* 253 */     localKDCOptions.set(30, true);
/*     */ 
/* 257 */     localKDCOptions.set(8, true);
/*     */ 
/* 259 */     return new KrbTgsReq(localKDCOptions, this, this.server, null, null, null, null, this.cAddr, null, null, null).sendAndGetCreds();
/*     */   }
/*     */ 
/*     */   public static Credentials acquireTGTFromCache(PrincipalName paramPrincipalName, String paramString)
/*     */     throws KrbException, IOException
/*     */   {
/* 287 */     if (paramString == null)
/*     */     {
/* 289 */       localObject1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*     */ 
/* 291 */       if ((((String)localObject1).toUpperCase(Locale.ENGLISH).startsWith("WINDOWS")) || (((String)localObject1).toUpperCase(Locale.ENGLISH).contains("OS X")))
/*     */       {
/* 293 */         localObject2 = acquireDefaultCreds();
/* 294 */         if (localObject2 == null) {
/* 295 */           if (DEBUG) {
/* 296 */             System.out.println(">>> Found no TGT's in LSA");
/*     */           }
/* 298 */           return null;
/*     */         }
/* 300 */         if (paramPrincipalName != null) {
/* 301 */           if (((Credentials)localObject2).getClient().equals(paramPrincipalName)) {
/* 302 */             if (DEBUG) {
/* 303 */               System.out.println(">>> Obtained TGT from LSA: " + localObject2);
/*     */             }
/*     */ 
/* 306 */             return localObject2;
/*     */           }
/* 308 */           if (DEBUG) {
/* 309 */             System.out.println(">>> LSA contains TGT for " + ((Credentials)localObject2).getClient() + " not " + paramPrincipalName);
/*     */           }
/*     */ 
/* 314 */           return null;
/*     */         }
/*     */ 
/* 317 */         if (DEBUG) {
/* 318 */           System.out.println(">>> Obtained TGT from LSA: " + localObject2);
/*     */         }
/*     */ 
/* 321 */         return localObject2;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 330 */     Object localObject1 = CredentialsCache.getInstance(paramPrincipalName, paramString);
/*     */ 
/* 333 */     if (localObject1 == null) {
/* 334 */       return null;
/*     */     }
/* 336 */     Object localObject2 = ((CredentialsCache)localObject1).getDefaultCreds();
/*     */ 
/* 339 */     if (EType.isSupported(((sun.security.krb5.internal.ccache.Credentials)localObject2).getEType())) {
/* 340 */       return ((sun.security.krb5.internal.ccache.Credentials)localObject2).setKrbCreds();
/*     */     }
/* 342 */     if (DEBUG) {
/* 343 */       System.out.println(">>> unsupported key type found the default TGT: " + ((sun.security.krb5.internal.ccache.Credentials)localObject2).getEType());
/*     */     }
/*     */ 
/* 347 */     return null;
/*     */   }
/*     */ 
/*     */   public static synchronized Credentials acquireDefaultCreds()
/*     */   {
/* 372 */     Credentials localCredentials = null;
/*     */ 
/* 374 */     if (cache == null) {
/* 375 */       cache = CredentialsCache.getInstance();
/*     */     }
/* 377 */     if (cache != null) {
/* 378 */       if (DEBUG) {
/* 379 */         System.out.println(">>> KrbCreds found the default ticket granting ticket in credential cache.");
/*     */       }
/*     */ 
/* 382 */       sun.security.krb5.internal.ccache.Credentials localCredentials1 = cache.getDefaultCreds();
/*     */ 
/* 384 */       if (EType.isSupported(localCredentials1.getEType())) {
/* 385 */         localCredentials = localCredentials1.setKrbCreds();
/*     */       }
/* 387 */       else if (DEBUG) {
/* 388 */         System.out.println(">>> unsupported key type found the default TGT: " + localCredentials1.getEType());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 394 */     if (localCredentials == null)
/*     */     {
/* 398 */       if (!alreadyTried) {
/*     */         try
/*     */         {
/* 401 */           ensureLoaded();
/*     */         } catch (Exception localException) {
/* 403 */           if (DEBUG) {
/* 404 */             System.out.println("Can not load credentials cache");
/* 405 */             localException.printStackTrace();
/*     */           }
/* 407 */           alreadyTried = true;
/*     */         }
/*     */       }
/* 410 */       if (alreadyLoaded)
/*     */       {
/* 412 */         if (DEBUG)
/* 413 */           System.out.println(">> Acquire default native Credentials");
/* 414 */         localCredentials = acquireDefaultNativeCreds();
/*     */       }
/*     */     }
/*     */ 
/* 418 */     return localCredentials;
/*     */   }
/*     */ 
/*     */   public static Credentials acquireServiceCreds(String paramString, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/* 442 */     return CredentialsUtil.acquireServiceCreds(paramString, paramCredentials);
/*     */   }
/*     */ 
/*     */   public CredentialsCache getCache() {
/* 446 */     return cache;
/*     */   }
/*     */ 
/*     */   public EncryptionKey getServiceKey() {
/* 450 */     return this.serviceKey;
/*     */   }
/*     */ 
/*     */   public static void printDebug(Credentials paramCredentials)
/*     */   {
/* 457 */     System.out.println(">>> DEBUG: ----Credentials----");
/* 458 */     System.out.println("\tclient: " + paramCredentials.client.toString());
/* 459 */     System.out.println("\tserver: " + paramCredentials.server.toString());
/* 460 */     System.out.println("\tticket: realm: " + paramCredentials.ticket.realm.toString());
/* 461 */     System.out.println("\t        sname: " + paramCredentials.ticket.sname.toString());
/* 462 */     if (paramCredentials.startTime != null) {
/* 463 */       System.out.println("\tstartTime: " + paramCredentials.startTime.getTime());
/*     */     }
/* 465 */     System.out.println("\tendTime: " + paramCredentials.endTime.getTime());
/* 466 */     System.out.println("        ----Credentials end----");
/*     */   }
/*     */ 
/*     */   static void ensureLoaded()
/*     */   {
/* 471 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 474 */         if (System.getProperty("os.name").contains("OS X"))
/* 475 */           System.loadLibrary("osxkrb5");
/*     */         else {
/* 477 */           System.loadLibrary("w2k_lsa_auth");
/*     */         }
/* 479 */         return null;
/*     */       }
/*     */     });
/* 482 */     alreadyLoaded = true;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 486 */     StringBuffer localStringBuffer = new StringBuffer("Credentials:");
/* 487 */     localStringBuffer.append("\nclient=").append(this.client);
/* 488 */     localStringBuffer.append("\nserver=").append(this.server);
/* 489 */     if (this.authTime != null) {
/* 490 */       localStringBuffer.append("\nauthTime=").append(this.authTime);
/*     */     }
/* 492 */     if (this.startTime != null) {
/* 493 */       localStringBuffer.append("\nstartTime=").append(this.startTime);
/*     */     }
/* 495 */     localStringBuffer.append("\nendTime=").append(this.endTime);
/* 496 */     localStringBuffer.append("\nrenewTill=").append(this.renewTill);
/* 497 */     localStringBuffer.append("\nflags: ").append(this.flags);
/* 498 */     localStringBuffer.append("\nEType (int): ").append(this.key.getEType());
/* 499 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.Credentials
 * JD-Core Version:    0.6.2
 */