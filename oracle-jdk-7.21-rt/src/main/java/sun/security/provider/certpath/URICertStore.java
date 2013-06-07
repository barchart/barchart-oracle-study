/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.AccessController;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CRLSelector;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertStoreParameters;
/*     */ import java.security.cert.CertStoreSpi;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509CRLSelector;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import sun.security.util.Cache;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.GeneralNameInterface;
/*     */ import sun.security.x509.URIName;
/*     */ 
/*     */ class URICertStore extends CertStoreSpi
/*     */ {
/*  92 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   private static final int CHECK_INTERVAL = 30000;
/*     */   private static final int CACHE_SIZE = 185;
/*     */   private final CertificateFactory factory;
/* 105 */   private Collection<X509Certificate> certs = Collections.emptySet();
/*     */   private X509CRL crl;
/*     */   private long lastChecked;
/*     */   private long lastModified;
/*     */   private URI uri;
/* 122 */   private boolean ldap = false;
/*     */   private CertStore ldapCertStore;
/*     */   private String ldapPath;
/* 188 */   private static final Cache certStoreCache = Cache.newSoftMemoryCache(185);
/*     */ 
/*     */   URICertStore(CertStoreParameters paramCertStoreParameters)
/*     */     throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
/*     */   {
/* 159 */     super(paramCertStoreParameters);
/* 160 */     if (!(paramCertStoreParameters instanceof URICertStoreParameters)) {
/* 161 */       throw new InvalidAlgorithmParameterException("params must be instanceof URICertStoreParameters");
/*     */     }
/*     */ 
/* 164 */     this.uri = ((URICertStoreParameters)paramCertStoreParameters).uri;
/*     */ 
/* 166 */     if (this.uri.getScheme().toLowerCase(Locale.ENGLISH).equals("ldap")) {
/* 167 */       if (LDAP.helper() == null)
/* 168 */         throw new NoSuchAlgorithmException("LDAP not present");
/* 169 */       this.ldap = true;
/* 170 */       this.ldapCertStore = LDAP.helper().getCertStore(this.uri);
/* 171 */       this.ldapPath = this.uri.getPath();
/*     */ 
/* 173 */       if (this.ldapPath.charAt(0) == '/')
/* 174 */         this.ldapPath = this.ldapPath.substring(1);
/*     */     }
/*     */     try
/*     */     {
/* 178 */       this.factory = CertificateFactory.getInstance("X.509");
/*     */     } catch (CertificateException localCertificateException) {
/* 180 */       throw new RuntimeException();
/*     */     }
/*     */   }
/*     */ 
/*     */   static synchronized CertStore getInstance(URICertStoreParameters paramURICertStoreParameters)
/*     */     throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
/*     */   {
/* 192 */     if (debug != null) {
/* 193 */       debug.println("CertStore URI:" + paramURICertStoreParameters.uri);
/*     */     }
/* 195 */     Object localObject = (CertStore)certStoreCache.get(paramURICertStoreParameters);
/* 196 */     if (localObject == null) {
/* 197 */       localObject = new UCS(new URICertStore(paramURICertStoreParameters), null, "URI", paramURICertStoreParameters);
/* 198 */       certStoreCache.put(paramURICertStoreParameters, localObject);
/*     */     }
/* 200 */     else if (debug != null) {
/* 201 */       debug.println("URICertStore.getInstance: cache hit");
/*     */     }
/*     */ 
/* 204 */     return localObject;
/*     */   }
/*     */ 
/*     */   static CertStore getInstance(AccessDescription paramAccessDescription)
/*     */   {
/* 212 */     if (!paramAccessDescription.getAccessMethod().equals(AccessDescription.Ad_CAISSUERS_Id)) {
/* 213 */       return null;
/*     */     }
/* 215 */     GeneralNameInterface localGeneralNameInterface = paramAccessDescription.getAccessLocation().getName();
/* 216 */     if (!(localGeneralNameInterface instanceof URIName)) {
/* 217 */       return null;
/*     */     }
/* 219 */     URI localURI = ((URIName)localGeneralNameInterface).getURI();
/*     */     try {
/* 221 */       return getInstance(new URICertStoreParameters(localURI));
/*     */     }
/*     */     catch (Exception localException) {
/* 224 */       if (debug != null) {
/* 225 */         debug.println("exception creating CertStore: " + localException);
/* 226 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 228 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector)
/*     */     throws CertStoreException
/*     */   {
/* 249 */     if (this.ldap) {
/* 250 */       X509CertSelector localX509CertSelector = (X509CertSelector)paramCertSelector;
/*     */       try {
/* 252 */         localX509CertSelector = LDAP.helper().wrap(localX509CertSelector, localX509CertSelector.getSubject(), this.ldapPath);
/*     */       } catch (IOException localIOException1) {
/* 254 */         throw new CertStoreException(localIOException1);
/*     */       }
/*     */ 
/* 258 */       return this.ldapCertStore.getCertificates(localX509CertSelector);
/*     */     }
/*     */ 
/* 265 */     long l1 = System.currentTimeMillis();
/* 266 */     if (l1 - this.lastChecked < 30000L) {
/* 267 */       if (debug != null) {
/* 268 */         debug.println("Returning certificates from cache");
/*     */       }
/* 270 */       return getMatchingCerts(this.certs, paramCertSelector);
/*     */     }
/* 272 */     this.lastChecked = l1;
/* 273 */     InputStream localInputStream = null;
/*     */     try {
/* 275 */       URLConnection localURLConnection = this.uri.toURL().openConnection();
/* 276 */       if (this.lastModified != 0L) {
/* 277 */         localURLConnection.setIfModifiedSince(this.lastModified);
/*     */       }
/* 279 */       localInputStream = localURLConnection.getInputStream();
/* 280 */       long l2 = this.lastModified;
/* 281 */       this.lastModified = localURLConnection.getLastModified();
/*     */       Object localObject1;
/* 282 */       if (l2 != 0L) {
/* 283 */         if (l2 == this.lastModified) {
/* 284 */           if (debug != null) {
/* 285 */             debug.println("Not modified, using cached copy");
/*     */           }
/* 287 */           return getMatchingCerts(this.certs, paramCertSelector);
/* 288 */         }if ((localURLConnection instanceof HttpURLConnection))
/*     */         {
/* 290 */           localObject1 = (HttpURLConnection)localURLConnection;
/* 291 */           if (((HttpURLConnection)localObject1).getResponseCode() == 304)
/*     */           {
/* 293 */             if (debug != null) {
/* 294 */               debug.println("Not modified, using cached copy");
/*     */             }
/* 296 */             return getMatchingCerts(this.certs, paramCertSelector);
/*     */           }
/*     */         }
/*     */       }
/* 300 */       if (debug != null) {
/* 301 */         debug.println("Downloading new certificates...");
/*     */       }
/* 303 */       this.certs = this.factory.generateCertificates(localInputStream);
/*     */ 
/* 305 */       return getMatchingCerts(this.certs, paramCertSelector);
/*     */     } catch (IOException localIOException2) {
/* 307 */       if (debug != null) {
/* 308 */         debug.println("Exception fetching certificates:");
/* 309 */         localIOException2.printStackTrace();
/*     */       }
/*     */     } catch (CertificateException localCertificateException) {
/* 312 */       if (debug != null) {
/* 313 */         debug.println("Exception fetching certificates:");
/* 314 */         localCertificateException.printStackTrace();
/*     */       }
/*     */     } finally {
/* 317 */       if (localInputStream != null) {
/*     */         try {
/* 319 */           localInputStream.close();
/*     */         }
/*     */         catch (IOException localIOException8)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 326 */     this.lastModified = 0L;
/* 327 */     this.certs = Collections.emptySet();
/* 328 */     return this.certs;
/*     */   }
/*     */ 
/*     */   private static Collection<X509Certificate> getMatchingCerts(Collection<X509Certificate> paramCollection, CertSelector paramCertSelector)
/*     */   {
/* 339 */     if (paramCertSelector == null) {
/* 340 */       return paramCollection;
/*     */     }
/* 342 */     ArrayList localArrayList = new ArrayList(paramCollection.size());
/*     */ 
/* 344 */     for (X509Certificate localX509Certificate : paramCollection) {
/* 345 */       if (paramCertSelector.match(localX509Certificate)) {
/* 346 */         localArrayList.add(localX509Certificate);
/*     */       }
/*     */     }
/* 349 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
/*     */     throws CertStoreException
/*     */   {
/* 369 */     if (this.ldap) {
/* 370 */       X509CRLSelector localX509CRLSelector = (X509CRLSelector)paramCRLSelector;
/*     */       try {
/* 372 */         localX509CRLSelector = LDAP.helper().wrap(localX509CRLSelector, null, this.ldapPath);
/*     */       } catch (IOException localIOException1) {
/* 374 */         throw new CertStoreException(localIOException1);
/*     */       }
/*     */ 
/* 378 */       return this.ldapCertStore.getCRLs(localX509CRLSelector);
/*     */     }
/*     */ 
/* 384 */     long l1 = System.currentTimeMillis();
/* 385 */     if (l1 - this.lastChecked < 30000L) {
/* 386 */       if (debug != null) {
/* 387 */         debug.println("Returning CRL from cache");
/*     */       }
/* 389 */       return getMatchingCRLs(this.crl, paramCRLSelector);
/*     */     }
/* 391 */     this.lastChecked = l1;
/* 392 */     InputStream localInputStream = null;
/*     */     try {
/* 394 */       URLConnection localURLConnection = this.uri.toURL().openConnection();
/* 395 */       if (this.lastModified != 0L) {
/* 396 */         localURLConnection.setIfModifiedSince(this.lastModified);
/*     */       }
/* 398 */       localInputStream = localURLConnection.getInputStream();
/* 399 */       long l2 = this.lastModified;
/* 400 */       this.lastModified = localURLConnection.getLastModified();
/*     */       Object localObject1;
/* 401 */       if (l2 != 0L) {
/* 402 */         if (l2 == this.lastModified) {
/* 403 */           if (debug != null) {
/* 404 */             debug.println("Not modified, using cached copy");
/*     */           }
/* 406 */           return getMatchingCRLs(this.crl, paramCRLSelector);
/* 407 */         }if ((localURLConnection instanceof HttpURLConnection))
/*     */         {
/* 409 */           localObject1 = (HttpURLConnection)localURLConnection;
/* 410 */           if (((HttpURLConnection)localObject1).getResponseCode() == 304)
/*     */           {
/* 412 */             if (debug != null) {
/* 413 */               debug.println("Not modified, using cached copy");
/*     */             }
/* 415 */             return getMatchingCRLs(this.crl, paramCRLSelector);
/*     */           }
/*     */         }
/*     */       }
/* 419 */       if (debug != null) {
/* 420 */         debug.println("Downloading new CRL...");
/*     */       }
/* 422 */       this.crl = ((X509CRL)this.factory.generateCRL(localInputStream));
/* 423 */       return getMatchingCRLs(this.crl, paramCRLSelector);
/*     */     } catch (IOException localIOException2) {
/* 425 */       if (debug != null) {
/* 426 */         debug.println("Exception fetching CRL:");
/* 427 */         localIOException2.printStackTrace();
/*     */       }
/*     */     } catch (CRLException localCRLException) {
/* 430 */       if (debug != null) {
/* 431 */         debug.println("Exception fetching CRL:");
/* 432 */         localCRLException.printStackTrace();
/*     */       }
/*     */     } finally {
/* 435 */       if (localInputStream != null) {
/*     */         try {
/* 437 */           localInputStream.close();
/*     */         }
/*     */         catch (IOException localIOException8)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 444 */     this.lastModified = 0L;
/* 445 */     this.crl = null;
/* 446 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   private static Collection<X509CRL> getMatchingCRLs(X509CRL paramX509CRL, CRLSelector paramCRLSelector)
/*     */   {
/* 455 */     if ((paramCRLSelector == null) || ((paramX509CRL != null) && (paramCRLSelector.match(paramX509CRL)))) {
/* 456 */       return Collections.singletonList(paramX509CRL);
/*     */     }
/* 458 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   private static class LDAP
/*     */   {
/*     */     private static final String CERT_STORE_HELPER = "sun.security.provider.certpath.ldap.LDAPCertStoreHelper";
/* 132 */     private static final CertStoreHelper helper = (CertStoreHelper)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public CertStoreHelper run()
/*     */       {
/*     */         try {
/* 137 */           Class localClass = Class.forName("sun.security.provider.certpath.ldap.LDAPCertStoreHelper", true, null);
/* 138 */           return (CertStoreHelper)localClass.newInstance();
/*     */         } catch (ClassNotFoundException localClassNotFoundException) {
/* 140 */           return null;
/*     */         } catch (InstantiationException localInstantiationException) {
/* 142 */           throw new AssertionError(localInstantiationException);
/*     */         } catch (IllegalAccessException localIllegalAccessException) {
/* 144 */           throw new AssertionError(localIllegalAccessException);
/*     */         }
/*     */       }
/*     */     });
/*     */ 
/*     */     static CertStoreHelper helper()
/*     */     {
/* 148 */       return helper;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class UCS extends CertStore
/*     */   {
/*     */     protected UCS(CertStoreSpi paramCertStoreSpi, Provider paramProvider, String paramString, CertStoreParameters paramCertStoreParameters)
/*     */     {
/* 502 */       super(paramProvider, paramString, paramCertStoreParameters);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class URICertStoreParameters
/*     */     implements CertStoreParameters
/*     */   {
/*     */     private final URI uri;
/* 467 */     private volatile int hashCode = 0;
/*     */ 
/* 469 */     URICertStoreParameters(URI paramURI) { this.uri = paramURI; }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 472 */       if (!(paramObject instanceof URICertStoreParameters)) {
/* 473 */         return false;
/*     */       }
/* 475 */       URICertStoreParameters localURICertStoreParameters = (URICertStoreParameters)paramObject;
/* 476 */       return this.uri.equals(localURICertStoreParameters.uri);
/*     */     }
/*     */     public int hashCode() {
/* 479 */       if (this.hashCode == 0) {
/* 480 */         int i = 17;
/* 481 */         i = 37 * i + this.uri.hashCode();
/* 482 */         this.hashCode = i;
/*     */       }
/* 484 */       return this.hashCode;
/*     */     }
/*     */     public Object clone() {
/*     */       try {
/* 488 */         return super.clone();
/*     */       }
/*     */       catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 491 */         throw new InternalError(localCloneNotSupportedException.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.URICertStore
 * JD-Core Version:    0.6.2
 */