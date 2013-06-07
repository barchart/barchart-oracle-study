/*     */ package sun.security.krb5.internal.ccache;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.Realm;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.LoginOptions;
/*     */ import sun.security.krb5.internal.TicketFlags;
/*     */ 
/*     */ public class FileCredentialsCache extends CredentialsCache
/*     */   implements FileCCacheConstants
/*     */ {
/*     */   public int version;
/*     */   public Tag tag;
/*     */   public PrincipalName primaryPrincipal;
/*     */   public Realm primaryRealm;
/*     */   private Vector<Credentials> credentialsList;
/*     */   private static String dir;
/*  65 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */ 
/*     */   public static synchronized FileCredentialsCache acquireInstance(PrincipalName paramPrincipalName, String paramString)
/*     */   {
/*     */     try {
/*  70 */       FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
/*  71 */       if (paramString == null)
/*  72 */         cacheName = getDefaultCacheName();
/*     */       else {
/*  74 */         cacheName = checkValidation(paramString);
/*     */       }
/*  76 */       if ((cacheName == null) || (!new File(cacheName).exists()))
/*     */       {
/*  78 */         return null;
/*     */       }
/*  80 */       if (paramPrincipalName != null) {
/*  81 */         localFileCredentialsCache.primaryPrincipal = paramPrincipalName;
/*  82 */         localFileCredentialsCache.primaryRealm = paramPrincipalName.getRealm();
/*     */       }
/*  84 */       localFileCredentialsCache.load(cacheName);
/*  85 */       return localFileCredentialsCache;
/*     */     }
/*     */     catch (IOException localIOException) {
/*  88 */       if (DEBUG)
/*  89 */         localIOException.printStackTrace();
/*     */     }
/*     */     catch (KrbException localKrbException)
/*     */     {
/*  93 */       if (DEBUG) {
/*  94 */         localKrbException.printStackTrace();
/*     */       }
/*     */     }
/*  97 */     return null;
/*     */   }
/*     */ 
/*     */   public static FileCredentialsCache acquireInstance() {
/* 101 */     return acquireInstance(null, null);
/*     */   }
/*     */ 
/*     */   static synchronized FileCredentialsCache New(PrincipalName paramPrincipalName, String paramString)
/*     */   {
/*     */     try {
/* 107 */       FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
/* 108 */       cacheName = checkValidation(paramString);
/* 109 */       if (cacheName == null)
/*     */       {
/* 111 */         return null;
/*     */       }
/* 113 */       localFileCredentialsCache.init(paramPrincipalName, cacheName);
/* 114 */       return localFileCredentialsCache;
/*     */     }
/*     */     catch (IOException localIOException) {
/*     */     }
/*     */     catch (KrbException localKrbException) {
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */   static synchronized FileCredentialsCache New(PrincipalName paramPrincipalName) {
/*     */     try {
/* 125 */       FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
/* 126 */       cacheName = getDefaultCacheName();
/* 127 */       localFileCredentialsCache.init(paramPrincipalName, cacheName);
/* 128 */       return localFileCredentialsCache;
/*     */     }
/*     */     catch (IOException localIOException) {
/* 131 */       if (DEBUG)
/* 132 */         localIOException.printStackTrace();
/*     */     }
/*     */     catch (KrbException localKrbException) {
/* 135 */       if (DEBUG) {
/* 136 */         localKrbException.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   boolean exists(String paramString)
/*     */   {
/* 147 */     File localFile = new File(paramString);
/* 148 */     if (localFile.exists())
/* 149 */       return true;
/* 150 */     return false;
/*     */   }
/*     */ 
/*     */   synchronized void init(PrincipalName paramPrincipalName, String paramString) throws IOException, KrbException
/*     */   {
/* 155 */     this.primaryPrincipal = paramPrincipalName;
/* 156 */     this.primaryRealm = paramPrincipalName.getRealm();
/* 157 */     CCacheOutputStream localCCacheOutputStream = new CCacheOutputStream(new FileOutputStream(paramString));
/*     */ 
/* 159 */     this.version = 1283;
/* 160 */     localCCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
/* 161 */     localCCacheOutputStream.close();
/* 162 */     load(paramString);
/*     */   }
/*     */ 
/*     */   synchronized void load(String paramString) throws IOException, KrbException
/*     */   {
/* 167 */     CCacheInputStream localCCacheInputStream = new CCacheInputStream(new FileInputStream(paramString));
/*     */ 
/* 169 */     this.version = localCCacheInputStream.readVersion();
/* 170 */     if (this.version == 1284) {
/* 171 */       this.tag = localCCacheInputStream.readTag();
/*     */     } else {
/* 173 */       this.tag = null;
/* 174 */       if ((this.version == 1281) || (this.version == 1282)) {
/* 175 */         localCCacheInputStream.setNativeByteOrder();
/*     */       }
/*     */     }
/* 178 */     PrincipalName localPrincipalName = localCCacheInputStream.readPrincipal(this.version);
/*     */ 
/* 180 */     if (this.primaryPrincipal != null) {
/* 181 */       if (!this.primaryPrincipal.match(localPrincipalName))
/* 182 */         throw new IOException("Primary principals don't match.");
/*     */     }
/*     */     else
/* 185 */       this.primaryPrincipal = localPrincipalName;
/* 186 */     this.primaryRealm = this.primaryPrincipal.getRealm();
/* 187 */     this.credentialsList = new Vector();
/* 188 */     while (localCCacheInputStream.available() > 0) {
/* 189 */       Credentials localCredentials = localCCacheInputStream.readCred(this.version);
/* 190 */       if (localCredentials != null) {
/* 191 */         this.credentialsList.addElement(localCredentials);
/*     */       }
/*     */     }
/* 194 */     localCCacheInputStream.close();
/*     */   }
/*     */ 
/*     */   public synchronized void update(Credentials paramCredentials)
/*     */   {
/* 206 */     if (this.credentialsList != null)
/* 207 */       if (this.credentialsList.isEmpty()) {
/* 208 */         this.credentialsList.addElement(paramCredentials);
/*     */       } else {
/* 210 */         Credentials localCredentials = null;
/* 211 */         int i = 0;
/*     */ 
/* 213 */         for (int j = 0; j < this.credentialsList.size(); j++) {
/* 214 */           localCredentials = (Credentials)this.credentialsList.elementAt(j);
/* 215 */           if ((match(paramCredentials.sname.getNameStrings(), localCredentials.sname.getNameStrings())) && (paramCredentials.sname.getRealmString().equalsIgnoreCase(localCredentials.sname.getRealmString())))
/*     */           {
/* 219 */             i = 1;
/* 220 */             if (paramCredentials.endtime.getTime() >= localCredentials.endtime.getTime()) {
/* 221 */               if (DEBUG) {
/* 222 */                 System.out.println(" >>> FileCredentialsCache Ticket matched, overwrite the old one.");
/*     */               }
/*     */ 
/* 226 */               this.credentialsList.removeElementAt(j);
/* 227 */               this.credentialsList.addElement(paramCredentials);
/*     */             }
/*     */           }
/*     */         }
/* 231 */         if (i == 0) {
/* 232 */           if (DEBUG) {
/* 233 */             System.out.println(" >>> FileCredentialsCache Ticket not exactly matched, add new one into cache.");
/*     */           }
/*     */ 
/* 238 */           this.credentialsList.addElement(paramCredentials);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public synchronized PrincipalName getPrimaryPrincipal()
/*     */   {
/* 245 */     return this.primaryPrincipal;
/*     */   }
/*     */ 
/*     */   public synchronized void save()
/*     */     throws IOException, Asn1Exception
/*     */   {
/* 253 */     CCacheOutputStream localCCacheOutputStream = new CCacheOutputStream(new FileOutputStream(cacheName));
/*     */ 
/* 255 */     localCCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
/* 256 */     Credentials[] arrayOfCredentials = null;
/* 257 */     if ((arrayOfCredentials = getCredsList()) != null) {
/* 258 */       for (int i = 0; i < arrayOfCredentials.length; i++) {
/* 259 */         localCCacheOutputStream.addCreds(arrayOfCredentials[i]);
/*     */       }
/*     */     }
/* 262 */     localCCacheOutputStream.close();
/*     */   }
/*     */ 
/*     */   boolean match(String[] paramArrayOfString1, String[] paramArrayOfString2) {
/* 266 */     if (paramArrayOfString1.length != paramArrayOfString2.length) {
/* 267 */       return false;
/*     */     }
/* 269 */     for (int i = 0; i < paramArrayOfString1.length; i++) {
/* 270 */       if (!paramArrayOfString1[i].equalsIgnoreCase(paramArrayOfString2[i])) {
/* 271 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 275 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized Credentials[] getCredsList()
/*     */   {
/* 282 */     if ((this.credentialsList == null) || (this.credentialsList.isEmpty())) {
/* 283 */       return null;
/*     */     }
/* 285 */     Credentials[] arrayOfCredentials = new Credentials[this.credentialsList.size()];
/* 286 */     for (int i = 0; i < this.credentialsList.size(); i++) {
/* 287 */       arrayOfCredentials[i] = ((Credentials)this.credentialsList.elementAt(i));
/*     */     }
/* 289 */     return arrayOfCredentials;
/*     */   }
/*     */ 
/*     */   public Credentials getCreds(LoginOptions paramLoginOptions, PrincipalName paramPrincipalName, Realm paramRealm)
/*     */   {
/* 296 */     if (paramLoginOptions == null) {
/* 297 */       return getCreds(paramPrincipalName, paramRealm);
/*     */     }
/* 299 */     Credentials[] arrayOfCredentials = getCredsList();
/* 300 */     if (arrayOfCredentials == null) {
/* 301 */       return null;
/*     */     }
/* 303 */     for (int i = 0; i < arrayOfCredentials.length; i++) {
/* 304 */       if ((paramPrincipalName.match(arrayOfCredentials[i].sname)) && (paramRealm.toString().equals(arrayOfCredentials[i].srealm.toString())))
/*     */       {
/* 306 */         if (arrayOfCredentials[i].flags.match(paramLoginOptions)) {
/* 307 */           return arrayOfCredentials[i];
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 312 */     return null;
/*     */   }
/*     */ 
/*     */   public Credentials getCreds(PrincipalName paramPrincipalName, Realm paramRealm)
/*     */   {
/* 323 */     Credentials[] arrayOfCredentials = getCredsList();
/* 324 */     if (arrayOfCredentials == null) {
/* 325 */       return null;
/*     */     }
/* 327 */     for (int i = 0; i < arrayOfCredentials.length; i++) {
/* 328 */       if ((paramPrincipalName.match(arrayOfCredentials[i].sname)) && (paramRealm.toString().equals(arrayOfCredentials[i].srealm.toString())))
/*     */       {
/* 330 */         return arrayOfCredentials[i];
/*     */       }
/*     */     }
/*     */ 
/* 334 */     return null;
/*     */   }
/*     */ 
/*     */   public Credentials getDefaultCreds() {
/* 338 */     Credentials[] arrayOfCredentials = getCredsList();
/* 339 */     if (arrayOfCredentials == null) {
/* 340 */       return null;
/*     */     }
/* 342 */     for (int i = arrayOfCredentials.length - 1; i >= 0; i--) {
/* 343 */       if (arrayOfCredentials[i].sname.toString().startsWith("krbtgt")) {
/* 344 */         String[] arrayOfString = arrayOfCredentials[i].sname.getNameStrings();
/*     */ 
/* 346 */         if (arrayOfString[1].equals(arrayOfCredentials[i].srealm.toString())) {
/* 347 */           return arrayOfCredentials[i];
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 352 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getDefaultCacheName()
/*     */   {
/* 367 */     String str1 = "krb5cc";
/*     */ 
/* 370 */     String str2 = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/* 374 */         return System.getenv("KRB5CCNAME");
/*     */       }
/*     */     });
/* 377 */     if (str2 != null) {
/* 378 */       if (DEBUG) {
/* 379 */         System.out.println(">>>KinitOptions cache name is " + str2);
/*     */       }
/* 381 */       return str2;
/*     */     }
/*     */ 
/* 385 */     String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
/*     */ 
/* 400 */     if (str3 != null) {
/* 401 */       str4 = null;
/* 402 */       str5 = null;
/* 403 */       long l = 0L;
/*     */ 
/* 405 */       if ((str3.startsWith("SunOS")) || (str3.startsWith("Linux"))) {
/*     */         try
/*     */         {
/* 408 */           Class localClass = Class.forName("com.sun.security.auth.module.UnixSystem");
/*     */ 
/* 410 */           Constructor localConstructor = localClass.getConstructor(new Class[0]);
/* 411 */           Object localObject = localConstructor.newInstance(new Object[0]);
/* 412 */           Method localMethod = localClass.getMethod("getUid", new Class[0]);
/* 413 */           l = ((Long)localMethod.invoke(localObject, new Object[0])).longValue();
/* 414 */           str2 = File.separator + "tmp" + File.separator + str1 + "_" + l;
/*     */ 
/* 416 */           if (DEBUG) {
/* 417 */             System.out.println(">>>KinitOptions cache name is " + str2);
/*     */           }
/*     */ 
/* 420 */           return str2;
/*     */         } catch (Exception localException) {
/* 422 */           if (DEBUG) {
/* 423 */             System.out.println("Exception in obtaining uid for Unix platforms Using user's home directory");
/*     */ 
/* 428 */             localException.printStackTrace();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 437 */     String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
/*     */ 
/* 441 */     String str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
/*     */ 
/* 445 */     if (str5 == null) {
/* 446 */       str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir"));
/*     */     }
/*     */ 
/* 451 */     if (str4 != null) {
/* 452 */       str2 = str5 + File.separator + str1 + "_" + str4;
/*     */     }
/*     */     else {
/* 455 */       str2 = str5 + File.separator + str1;
/*     */     }
/*     */ 
/* 458 */     if (DEBUG) {
/* 459 */       System.out.println(">>>KinitOptions cache name is " + str2);
/*     */     }
/*     */ 
/* 462 */     return str2;
/*     */   }
/*     */ 
/*     */   public static String checkValidation(String paramString) {
/* 466 */     String str = null;
/* 467 */     if (paramString == null) {
/* 468 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 472 */       str = new File(paramString).getCanonicalPath();
/* 473 */       File localFile1 = new File(str);
/* 474 */       if (!localFile1.exists())
/*     */       {
/* 476 */         File localFile2 = new File(localFile1.getParent());
/*     */ 
/* 478 */         if (!localFile2.isDirectory())
/* 479 */           str = null;
/* 480 */         localFile2 = null;
/*     */       }
/* 482 */       localFile1 = null;
/*     */     }
/*     */     catch (IOException localIOException) {
/* 485 */       str = null;
/*     */     }
/* 487 */     return str;
/*     */   }
/*     */ 
/*     */   private static String exec(String paramString)
/*     */   {
/* 492 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 493 */     Vector localVector = new Vector();
/* 494 */     while (localStringTokenizer.hasMoreTokens()) {
/* 495 */       localVector.addElement(localStringTokenizer.nextToken());
/*     */     }
/* 497 */     String[] arrayOfString = new String[localVector.size()];
/* 498 */     localVector.copyInto(arrayOfString);
/*     */     try
/*     */     {
/* 501 */       Process localProcess = (Process)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Process run()
/*     */         {
/*     */           try {
/* 506 */             return Runtime.getRuntime().exec(this.val$command);
/*     */           } catch (IOException localIOException) {
/* 508 */             if (FileCredentialsCache.DEBUG)
/* 509 */               localIOException.printStackTrace();
/*     */           }
/* 511 */           return null;
/*     */         }
/*     */       });
/* 515 */       if (localProcess == null)
/*     */       {
/* 517 */         return null;
/*     */       }
/*     */ 
/* 520 */       BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream(), "8859_1"));
/*     */ 
/* 523 */       String str = null;
/* 524 */       if ((arrayOfString.length == 1) && (arrayOfString[0].equals("/usr/bin/env")));
/* 526 */       while ((str = localBufferedReader.readLine()) != null)
/* 527 */         if ((str.length() >= 11) && 
/* 528 */           (str.substring(0, 11).equalsIgnoreCase("KRB5CCNAME=")))
/*     */         {
/* 530 */           str = str.substring(11);
/* 531 */           break;
/*     */ 
/* 535 */           str = localBufferedReader.readLine();
/*     */         }
/* 536 */       localBufferedReader.close();
/* 537 */       return str;
/*     */     } catch (Exception localException) {
/* 539 */       if (DEBUG) {
/* 540 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 543 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.ccache.FileCredentialsCache
 * JD-Core Version:    0.6.2
 */