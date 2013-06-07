/*      */ package sun.security.provider.certpath.ldap;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URI;
/*      */ import java.security.AccessController;
/*      */ import java.security.InvalidAlgorithmParameterException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PublicKey;
/*      */ import java.security.cert.CRL;
/*      */ import java.security.cert.CRLException;
/*      */ import java.security.cert.CRLSelector;
/*      */ import java.security.cert.CertSelector;
/*      */ import java.security.cert.CertStore;
/*      */ import java.security.cert.CertStoreException;
/*      */ import java.security.cert.CertStoreParameters;
/*      */ import java.security.cert.CertStoreSpi;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.LDAPCertStoreParameters;
/*      */ import java.security.cert.X509CRL;
/*      */ import java.security.cert.X509CRLSelector;
/*      */ import java.security.cert.X509CertSelector;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.naming.NameNotFoundException;
/*      */ import javax.naming.NamingEnumeration;
/*      */ import javax.naming.NamingException;
/*      */ import javax.naming.directory.Attribute;
/*      */ import javax.naming.directory.Attributes;
/*      */ import javax.naming.directory.BasicAttributes;
/*      */ import javax.naming.directory.DirContext;
/*      */ import javax.naming.directory.InitialDirContext;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.provider.certpath.X509CertificatePair;
/*      */ import sun.security.util.Cache;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.x509.X500Name;
/*      */ 
/*      */ public class LDAPCertStore extends CertStoreSpi
/*      */ {
/*  108 */   private static final Debug debug = Debug.getInstance("certpath");
/*      */   private static final boolean DEBUG = false;
/*      */   private static final String USER_CERT = "userCertificate;binary";
/*      */   private static final String CA_CERT = "cACertificate;binary";
/*      */   private static final String CROSS_CERT = "crossCertificatePair;binary";
/*      */   private static final String CRL = "certificateRevocationList;binary";
/*      */   private static final String ARL = "authorityRevocationList;binary";
/*      */   private static final String DELTA_CRL = "deltaRevocationList;binary";
/*  123 */   private static final String[] STRING0 = new String[0];
/*      */ 
/*  125 */   private static final byte[][] BB0 = new byte[0][];
/*      */ 
/*  127 */   private static final Attributes EMPTY_ATTRIBUTES = new BasicAttributes();
/*      */   private static final int DEFAULT_CACHE_SIZE = 750;
/*      */   private static final int DEFAULT_CACHE_LIFETIME = 30;
/*      */   private static final int LIFETIME;
/*      */   private static final String PROP_LIFETIME = "sun.security.certpath.ldap.cache.lifetime";
/*      */   private CertificateFactory cf;
/*      */   private DirContext ctx;
/*  161 */   private boolean prefetchCRLs = false;
/*      */   private final Cache valueCache;
/*  165 */   private int cacheHits = 0;
/*  166 */   private int cacheMisses = 0;
/*  167 */   private int requests = 0;
/*      */ 
/*  210 */   private static final Cache certStoreCache = Cache.newSoftMemoryCache(185);
/*      */ 
/*      */   public LDAPCertStore(CertStoreParameters paramCertStoreParameters)
/*      */     throws InvalidAlgorithmParameterException
/*      */   {
/*  180 */     super(paramCertStoreParameters);
/*  181 */     if (!(paramCertStoreParameters instanceof LDAPCertStoreParameters)) {
/*  182 */       throw new InvalidAlgorithmParameterException("parameters must be LDAPCertStoreParameters");
/*      */     }
/*      */ 
/*  185 */     LDAPCertStoreParameters localLDAPCertStoreParameters = (LDAPCertStoreParameters)paramCertStoreParameters;
/*      */ 
/*  188 */     createInitialDirContext(localLDAPCertStoreParameters.getServerName(), localLDAPCertStoreParameters.getPort());
/*      */     try
/*      */     {
/*  192 */       this.cf = CertificateFactory.getInstance("X.509");
/*      */     } catch (CertificateException localCertificateException) {
/*  194 */       throw new InvalidAlgorithmParameterException("unable to create CertificateFactory for X.509");
/*      */     }
/*      */ 
/*  197 */     if (LIFETIME == 0)
/*  198 */       this.valueCache = Cache.newNullCache();
/*  199 */     else if (LIFETIME < 0)
/*  200 */       this.valueCache = Cache.newSoftMemoryCache(750);
/*      */     else
/*  202 */       this.valueCache = Cache.newSoftMemoryCache(750, LIFETIME);
/*      */   }
/*      */ 
/*      */   static synchronized CertStore getInstance(LDAPCertStoreParameters paramLDAPCertStoreParameters)
/*      */     throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
/*      */   {
/*  213 */     CertStore localCertStore = (CertStore)certStoreCache.get(paramLDAPCertStoreParameters);
/*  214 */     if (localCertStore == null) {
/*  215 */       localCertStore = CertStore.getInstance("LDAP", paramLDAPCertStoreParameters);
/*  216 */       certStoreCache.put(paramLDAPCertStoreParameters, localCertStore);
/*      */     }
/*  218 */     else if (debug != null) {
/*  219 */       debug.println("LDAPCertStore.getInstance: cache hit");
/*      */     }
/*      */ 
/*  222 */     return localCertStore;
/*      */   }
/*      */ 
/*      */   private void createInitialDirContext(String paramString, int paramInt)
/*      */     throws InvalidAlgorithmParameterException
/*      */   {
/*  234 */     String str = "ldap://" + paramString + ":" + paramInt;
/*  235 */     Hashtable localHashtable1 = new Hashtable();
/*  236 */     localHashtable1.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
/*      */ 
/*  238 */     localHashtable1.put("java.naming.provider.url", str);
/*      */     try {
/*  240 */       this.ctx = new InitialDirContext(localHashtable1);
/*      */ 
/*  245 */       Hashtable localHashtable2 = this.ctx.getEnvironment();
/*  246 */       if (localHashtable2.get("java.naming.referral") == null)
/*  247 */         this.ctx.addToEnvironment("java.naming.referral", "follow");
/*      */     }
/*      */     catch (NamingException localNamingException) {
/*  250 */       if (debug != null) {
/*  251 */         debug.println("LDAPCertStore.engineInit about to throw InvalidAlgorithmParameterException");
/*      */ 
/*  253 */         localNamingException.printStackTrace();
/*      */       }
/*  255 */       InvalidAlgorithmParameterException localInvalidAlgorithmParameterException = new InvalidAlgorithmParameterException("unable to create InitialDirContext using supplied parameters");
/*      */ 
/*  257 */       localInvalidAlgorithmParameterException.initCause(localNamingException);
/*  258 */       throw ((InvalidAlgorithmParameterException)localInvalidAlgorithmParameterException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Collection<X509Certificate> getCertificates(LDAPRequest paramLDAPRequest, String paramString, X509CertSelector paramX509CertSelector)
/*      */     throws CertStoreException
/*      */   {
/*      */     byte[][] arrayOfByte;
/*      */     try
/*      */     {
/*  425 */       arrayOfByte = paramLDAPRequest.getValues(paramString);
/*      */     } catch (NamingException localNamingException) {
/*  427 */       throw new CertStoreException(localNamingException);
/*      */     }
/*      */ 
/*  430 */     int i = arrayOfByte.length;
/*  431 */     if (i == 0) {
/*  432 */       return Collections.emptySet();
/*      */     }
/*      */ 
/*  435 */     ArrayList localArrayList = new ArrayList(i);
/*      */ 
/*  437 */     for (int j = 0; j < i; j++) {
/*  438 */       ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte[j]);
/*      */       try {
/*  440 */         Certificate localCertificate = this.cf.generateCertificate(localByteArrayInputStream);
/*  441 */         if (paramX509CertSelector.match(localCertificate))
/*  442 */           localArrayList.add((X509Certificate)localCertificate);
/*      */       }
/*      */       catch (CertificateException localCertificateException) {
/*  445 */         if (debug != null) {
/*  446 */           debug.println("LDAPCertStore.getCertificates() encountered exception while parsing cert, skipping the bad data: ");
/*      */ 
/*  448 */           HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  449 */           debug.println("[ " + localHexDumpEncoder.encodeBuffer(arrayOfByte[j]) + " ]");
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  455 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   private Collection<X509CertificatePair> getCertPairs(LDAPRequest paramLDAPRequest, String paramString)
/*      */     throws CertStoreException
/*      */   {
/*      */     byte[][] arrayOfByte;
/*      */     try
/*      */     {
/*  473 */       arrayOfByte = paramLDAPRequest.getValues(paramString);
/*      */     } catch (NamingException localNamingException) {
/*  475 */       throw new CertStoreException(localNamingException);
/*      */     }
/*      */ 
/*  478 */     int i = arrayOfByte.length;
/*  479 */     if (i == 0) {
/*  480 */       return Collections.emptySet();
/*      */     }
/*      */ 
/*  483 */     ArrayList localArrayList = new ArrayList(i);
/*      */ 
/*  486 */     for (int j = 0; j < i; j++) {
/*      */       try {
/*  488 */         X509CertificatePair localX509CertificatePair = X509CertificatePair.generateCertificatePair(arrayOfByte[j]);
/*      */ 
/*  490 */         localArrayList.add(localX509CertificatePair);
/*      */       } catch (CertificateException localCertificateException) {
/*  492 */         if (debug != null) {
/*  493 */           debug.println("LDAPCertStore.getCertPairs() encountered exception while parsing cert, skipping the bad data: ");
/*      */ 
/*  496 */           HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  497 */           debug.println("[ " + localHexDumpEncoder.encodeBuffer(arrayOfByte[j]) + " ]");
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  503 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   private Collection<X509Certificate> getMatchingCrossCerts(LDAPRequest paramLDAPRequest, X509CertSelector paramX509CertSelector1, X509CertSelector paramX509CertSelector2)
/*      */     throws CertStoreException
/*      */   {
/*  527 */     Collection localCollection = getCertPairs(paramLDAPRequest, "crossCertificatePair;binary");
/*      */ 
/*  531 */     ArrayList localArrayList = new ArrayList();
/*      */ 
/*  533 */     for (X509CertificatePair localX509CertificatePair : localCollection)
/*      */     {
/*      */       X509Certificate localX509Certificate;
/*  535 */       if (paramX509CertSelector1 != null) {
/*  536 */         localX509Certificate = localX509CertificatePair.getForward();
/*  537 */         if ((localX509Certificate != null) && (paramX509CertSelector1.match(localX509Certificate))) {
/*  538 */           localArrayList.add(localX509Certificate);
/*      */         }
/*      */       }
/*  541 */       if (paramX509CertSelector2 != null) {
/*  542 */         localX509Certificate = localX509CertificatePair.getReverse();
/*  543 */         if ((localX509Certificate != null) && (paramX509CertSelector2.match(localX509Certificate))) {
/*  544 */           localArrayList.add(localX509Certificate);
/*      */         }
/*      */       }
/*      */     }
/*  548 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector)
/*      */     throws CertStoreException
/*      */   {
/*  574 */     if (debug != null) {
/*  575 */       debug.println("LDAPCertStore.engineGetCertificates() selector: " + String.valueOf(paramCertSelector));
/*      */     }
/*      */ 
/*  579 */     if (paramCertSelector == null) {
/*  580 */       paramCertSelector = new X509CertSelector();
/*      */     }
/*  582 */     if (!(paramCertSelector instanceof X509CertSelector)) {
/*  583 */       throw new CertStoreException("LDAPCertStore needs an X509CertSelector to find certs");
/*      */     }
/*      */ 
/*  586 */     X509CertSelector localX509CertSelector = (X509CertSelector)paramCertSelector;
/*  587 */     int i = localX509CertSelector.getBasicConstraints();
/*  588 */     String str1 = localX509CertSelector.getSubjectAsString();
/*  589 */     String str2 = localX509CertSelector.getIssuerAsString();
/*  590 */     HashSet localHashSet = new HashSet();
/*  591 */     if (debug != null)
/*  592 */       debug.println("LDAPCertStore.engineGetCertificates() basicConstraints: " + i);
/*      */     LDAPRequest localLDAPRequest;
/*  601 */     if (str1 != null) {
/*  602 */       if (debug != null) {
/*  603 */         debug.println("LDAPCertStore.engineGetCertificates() subject is not null");
/*      */       }
/*      */ 
/*  606 */       localLDAPRequest = new LDAPRequest(str1);
/*  607 */       if (i > -2) {
/*  608 */         localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
/*  609 */         localLDAPRequest.addRequestedAttribute("cACertificate;binary");
/*  610 */         localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
/*  611 */         if (this.prefetchCRLs) {
/*  612 */           localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*      */         }
/*      */       }
/*  615 */       if (i < 0) {
/*  616 */         localLDAPRequest.addRequestedAttribute("userCertificate;binary");
/*      */       }
/*      */ 
/*  619 */       if (i > -2) {
/*  620 */         localHashSet.addAll(getMatchingCrossCerts(localLDAPRequest, localX509CertSelector, null));
/*  621 */         if (debug != null) {
/*  622 */           debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(subject,xsel,null),certs.size(): " + localHashSet.size());
/*      */         }
/*      */ 
/*  626 */         localHashSet.addAll(getCertificates(localLDAPRequest, "cACertificate;binary", localX509CertSelector));
/*  627 */         if (debug != null) {
/*  628 */           debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,CA_CERT,xsel),certs.size(): " + localHashSet.size());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  633 */       if (i < 0) {
/*  634 */         localHashSet.addAll(getCertificates(localLDAPRequest, "userCertificate;binary", localX509CertSelector));
/*  635 */         if (debug != null) {
/*  636 */           debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(subject,USER_CERT, xsel),certs.size(): " + localHashSet.size());
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  642 */       if (debug != null) {
/*  643 */         debug.println("LDAPCertStore.engineGetCertificates() subject is null");
/*      */       }
/*      */ 
/*  646 */       if (i == -2) {
/*  647 */         throw new CertStoreException("need subject to find EE certs");
/*      */       }
/*  649 */       if (str2 == null) {
/*  650 */         throw new CertStoreException("need subject or issuer to find certs");
/*      */       }
/*      */     }
/*  653 */     if (debug != null) {
/*  654 */       debug.println("LDAPCertStore.engineGetCertificates() about to getMatchingCrossCerts...");
/*      */     }
/*      */ 
/*  657 */     if ((str2 != null) && (i > -2)) {
/*  658 */       localLDAPRequest = new LDAPRequest(str2);
/*  659 */       localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
/*  660 */       localLDAPRequest.addRequestedAttribute("cACertificate;binary");
/*  661 */       localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
/*  662 */       if (this.prefetchCRLs) {
/*  663 */         localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*      */       }
/*      */ 
/*  666 */       localHashSet.addAll(getMatchingCrossCerts(localLDAPRequest, null, localX509CertSelector));
/*  667 */       if (debug != null) {
/*  668 */         debug.println("LDAPCertStore.engineGetCertificates() after getMatchingCrossCerts(issuer,null,xsel),certs.size(): " + localHashSet.size());
/*      */       }
/*      */ 
/*  672 */       localHashSet.addAll(getCertificates(localLDAPRequest, "cACertificate;binary", localX509CertSelector));
/*  673 */       if (debug != null) {
/*  674 */         debug.println("LDAPCertStore.engineGetCertificates() after getCertificates(issuer,CA_CERT,xsel),certs.size(): " + localHashSet.size());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  679 */     if (debug != null) {
/*  680 */       debug.println("LDAPCertStore.engineGetCertificates() returning certs");
/*      */     }
/*  682 */     return localHashSet;
/*      */   }
/*      */ 
/*      */   private Collection<X509CRL> getCRLs(LDAPRequest paramLDAPRequest, String paramString, X509CRLSelector paramX509CRLSelector)
/*      */     throws CertStoreException
/*      */   {
/*      */     byte[][] arrayOfByte;
/*      */     try
/*      */     {
/*  702 */       arrayOfByte = paramLDAPRequest.getValues(paramString);
/*      */     } catch (NamingException localNamingException) {
/*  704 */       throw new CertStoreException(localNamingException);
/*      */     }
/*      */ 
/*  707 */     int i = arrayOfByte.length;
/*  708 */     if (i == 0) {
/*  709 */       return Collections.emptySet();
/*      */     }
/*      */ 
/*  712 */     ArrayList localArrayList = new ArrayList(i);
/*      */ 
/*  714 */     for (int j = 0; j < i; j++) {
/*      */       try {
/*  716 */         CRL localCRL = this.cf.generateCRL(new ByteArrayInputStream(arrayOfByte[j]));
/*  717 */         if (paramX509CRLSelector.match(localCRL))
/*  718 */           localArrayList.add((X509CRL)localCRL);
/*      */       }
/*      */       catch (CRLException localCRLException) {
/*  721 */         if (debug != null) {
/*  722 */           debug.println("LDAPCertStore.getCRLs() encountered exception while parsing CRL, skipping the bad data: ");
/*      */ 
/*  724 */           HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  725 */           debug.println("[ " + localHexDumpEncoder.encodeBuffer(arrayOfByte[j]) + " ]");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  730 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
/*      */     throws CertStoreException
/*      */   {
/*  756 */     if (debug != null) {
/*  757 */       debug.println("LDAPCertStore.engineGetCRLs() selector: " + paramCRLSelector);
/*      */     }
/*      */ 
/*  761 */     if (paramCRLSelector == null) {
/*  762 */       paramCRLSelector = new X509CRLSelector();
/*      */     }
/*  764 */     if (!(paramCRLSelector instanceof X509CRLSelector)) {
/*  765 */       throw new CertStoreException("need X509CRLSelector to find CRLs");
/*      */     }
/*  767 */     X509CRLSelector localX509CRLSelector = (X509CRLSelector)paramCRLSelector;
/*  768 */     HashSet localHashSet = new HashSet();
/*      */ 
/*  772 */     X509Certificate localX509Certificate = localX509CRLSelector.getCertificateChecking();
/*      */     Object localObject1;
/*  773 */     if (localX509Certificate != null) {
/*  774 */       localObject1 = new HashSet();
/*  775 */       localObject2 = localX509Certificate.getIssuerX500Principal();
/*  776 */       ((Collection)localObject1).add(((X500Principal)localObject2).getName("RFC2253"));
/*      */     }
/*      */     else
/*      */     {
/*  780 */       localObject1 = localX509CRLSelector.getIssuerNames();
/*  781 */       if (localObject1 == null) {
/*  782 */         throw new CertStoreException("need issuerNames or certChecking to find CRLs");
/*      */       }
/*      */     }
/*      */ 
/*  786 */     for (Object localObject2 = ((Collection)localObject1).iterator(); ((Iterator)localObject2).hasNext(); ) { Object localObject3 = ((Iterator)localObject2).next();
/*      */       String str;
/*  788 */       if ((localObject3 instanceof byte[])) {
/*      */         try {
/*  790 */           X500Principal localX500Principal = new X500Principal((byte[])localObject3);
/*  791 */           str = localX500Principal.getName("RFC2253");
/*      */         } catch (IllegalArgumentException localIllegalArgumentException) {
/*      */         }
/*      */       }
/*      */       else {
/*  796 */         str = (String)localObject3;
/*      */ 
/*  799 */         Object localObject4 = Collections.emptySet();
/*      */         LDAPRequest localLDAPRequest;
/*  800 */         if ((localX509Certificate == null) || (localX509Certificate.getBasicConstraints() != -1)) {
/*  801 */           localLDAPRequest = new LDAPRequest(str);
/*  802 */           localLDAPRequest.addRequestedAttribute("crossCertificatePair;binary");
/*  803 */           localLDAPRequest.addRequestedAttribute("cACertificate;binary");
/*  804 */           localLDAPRequest.addRequestedAttribute("authorityRevocationList;binary");
/*  805 */           if (this.prefetchCRLs)
/*  806 */             localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*      */           try
/*      */           {
/*  809 */             localObject4 = getCRLs(localLDAPRequest, "authorityRevocationList;binary", localX509CRLSelector);
/*  810 */             if (((Collection)localObject4).isEmpty())
/*      */             {
/*  813 */               this.prefetchCRLs = true;
/*      */             }
/*  815 */             else localHashSet.addAll((Collection)localObject4); 
/*      */           }
/*      */           catch (CertStoreException localCertStoreException)
/*      */           {
/*  818 */             if (debug != null) {
/*  819 */               debug.println("LDAPCertStore.engineGetCRLs non-fatal error retrieving ARLs:" + localCertStoreException);
/*      */ 
/*  821 */               localCertStoreException.printStackTrace();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  828 */         if ((((Collection)localObject4).isEmpty()) || (localX509Certificate == null)) {
/*  829 */           localLDAPRequest = new LDAPRequest(str);
/*  830 */           localLDAPRequest.addRequestedAttribute("certificateRevocationList;binary");
/*  831 */           localObject4 = getCRLs(localLDAPRequest, "certificateRevocationList;binary", localX509CRLSelector);
/*  832 */           localHashSet.addAll((Collection)localObject4);
/*      */         }
/*      */       } }
/*  835 */     return localHashSet;
/*      */   }
/*      */ 
/*      */   static LDAPCertStoreParameters getParameters(URI paramURI)
/*      */   {
/*  840 */     String str = paramURI.getHost();
/*  841 */     if (str == null) {
/*  842 */       return new SunLDAPCertStoreParameters();
/*      */     }
/*  844 */     int i = paramURI.getPort();
/*  845 */     return i == -1 ? new SunLDAPCertStoreParameters(str) : new SunLDAPCertStoreParameters(str, i);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  139 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.certpath.ldap.cache.lifetime"));
/*      */ 
/*  141 */     if (str != null)
/*  142 */       LIFETIME = Integer.parseInt(str);
/*      */     else
/*  144 */       LIFETIME = 30;
/*      */   }
/*      */ 
/*      */   static class LDAPCRLSelector extends X509CRLSelector
/*      */   {
/*      */     private X509CRLSelector selector;
/*      */     private Collection<X500Principal> certIssuers;
/*      */     private Collection<X500Principal> issuers;
/*      */     private HashSet<Object> issuerNames;
/*      */ 
/*      */     LDAPCRLSelector(X509CRLSelector paramX509CRLSelector, Collection<X500Principal> paramCollection, String paramString)
/*      */       throws IOException
/*      */     {
/* 1028 */       this.selector = (paramX509CRLSelector == null ? new X509CRLSelector() : paramX509CRLSelector);
/* 1029 */       this.certIssuers = paramCollection;
/* 1030 */       this.issuerNames = new HashSet();
/* 1031 */       this.issuerNames.add(paramString);
/* 1032 */       this.issuers = new HashSet();
/* 1033 */       this.issuers.add(new X500Name(paramString).asX500Principal());
/*      */     }
/*      */ 
/*      */     public Collection<X500Principal> getIssuers()
/*      */     {
/* 1039 */       return Collections.unmodifiableCollection(this.issuers);
/*      */     }
/*      */ 
/*      */     public Collection<Object> getIssuerNames() {
/* 1043 */       return Collections.unmodifiableCollection(this.issuerNames);
/*      */     }
/*      */     public BigInteger getMinCRL() {
/* 1046 */       return this.selector.getMinCRL();
/*      */     }
/*      */     public BigInteger getMaxCRL() {
/* 1049 */       return this.selector.getMaxCRL();
/*      */     }
/*      */     public Date getDateAndTime() {
/* 1052 */       return this.selector.getDateAndTime();
/*      */     }
/*      */     public X509Certificate getCertificateChecking() {
/* 1055 */       return this.selector.getCertificateChecking();
/*      */     }
/*      */ 
/*      */     public boolean match(CRL paramCRL)
/*      */     {
/* 1060 */       this.selector.setIssuers(this.certIssuers);
/* 1061 */       boolean bool = this.selector.match(paramCRL);
/* 1062 */       this.selector.setIssuers(this.issuers);
/* 1063 */       return bool;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LDAPCertSelector extends X509CertSelector
/*      */   {
/*      */     private X500Principal certSubject;
/*      */     private X509CertSelector selector;
/*      */     private X500Principal subject;
/*      */ 
/*      */     LDAPCertSelector(X509CertSelector paramX509CertSelector, X500Principal paramX500Principal, String paramString)
/*      */       throws IOException
/*      */     {
/*  919 */       this.selector = (paramX509CertSelector == null ? new X509CertSelector() : paramX509CertSelector);
/*  920 */       this.certSubject = paramX500Principal;
/*  921 */       this.subject = new X500Name(paramString).asX500Principal();
/*      */     }
/*      */ 
/*      */     public X509Certificate getCertificate()
/*      */     {
/*  927 */       return this.selector.getCertificate();
/*      */     }
/*      */     public BigInteger getSerialNumber() {
/*  930 */       return this.selector.getSerialNumber();
/*      */     }
/*      */     public X500Principal getIssuer() {
/*  933 */       return this.selector.getIssuer();
/*      */     }
/*      */     public String getIssuerAsString() {
/*  936 */       return this.selector.getIssuerAsString();
/*      */     }
/*      */     public byte[] getIssuerAsBytes() throws IOException {
/*  939 */       return this.selector.getIssuerAsBytes();
/*      */     }
/*      */ 
/*      */     public X500Principal getSubject() {
/*  943 */       return this.subject;
/*      */     }
/*      */ 
/*      */     public String getSubjectAsString() {
/*  947 */       return this.subject.getName();
/*      */     }
/*      */ 
/*      */     public byte[] getSubjectAsBytes() throws IOException {
/*  951 */       return this.subject.getEncoded();
/*      */     }
/*      */     public byte[] getSubjectKeyIdentifier() {
/*  954 */       return this.selector.getSubjectKeyIdentifier();
/*      */     }
/*      */     public byte[] getAuthorityKeyIdentifier() {
/*  957 */       return this.selector.getAuthorityKeyIdentifier();
/*      */     }
/*      */     public Date getCertificateValid() {
/*  960 */       return this.selector.getCertificateValid();
/*      */     }
/*      */     public Date getPrivateKeyValid() {
/*  963 */       return this.selector.getPrivateKeyValid();
/*      */     }
/*      */     public String getSubjectPublicKeyAlgID() {
/*  966 */       return this.selector.getSubjectPublicKeyAlgID();
/*      */     }
/*      */     public PublicKey getSubjectPublicKey() {
/*  969 */       return this.selector.getSubjectPublicKey();
/*      */     }
/*      */     public boolean[] getKeyUsage() {
/*  972 */       return this.selector.getKeyUsage();
/*      */     }
/*      */     public Set<String> getExtendedKeyUsage() {
/*  975 */       return this.selector.getExtendedKeyUsage();
/*      */     }
/*      */     public boolean getMatchAllSubjectAltNames() {
/*  978 */       return this.selector.getMatchAllSubjectAltNames();
/*      */     }
/*      */     public Collection<List<?>> getSubjectAlternativeNames() {
/*  981 */       return this.selector.getSubjectAlternativeNames();
/*      */     }
/*      */     public byte[] getNameConstraints() {
/*  984 */       return this.selector.getNameConstraints();
/*      */     }
/*      */     public int getBasicConstraints() {
/*  987 */       return this.selector.getBasicConstraints();
/*      */     }
/*      */     public Set<String> getPolicy() {
/*  990 */       return this.selector.getPolicy();
/*      */     }
/*      */     public Collection<List<?>> getPathToNames() {
/*  993 */       return this.selector.getPathToNames();
/*      */     }
/*      */ 
/*      */     public boolean match(Certificate paramCertificate)
/*      */     {
/*  999 */       this.selector.setSubject(this.certSubject);
/* 1000 */       boolean bool = this.selector.match(paramCertificate);
/* 1001 */       this.selector.setSubject(this.subject);
/* 1002 */       return bool;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class LDAPRequest
/*      */   {
/*      */     private final String name;
/*      */     private Map<String, byte[][]> valueMap;
/*      */     private final List<String> requestedAttributes;
/*      */ 
/*      */     LDAPRequest(String arg2)
/*      */     {
/*      */       Object localObject;
/*  285 */       this.name = localObject;
/*  286 */       this.requestedAttributes = new ArrayList(5);
/*      */     }
/*      */ 
/*      */     String getName() {
/*  290 */       return this.name;
/*      */     }
/*      */ 
/*      */     void addRequestedAttribute(String paramString) {
/*  294 */       if (this.valueMap != null) {
/*  295 */         throw new IllegalStateException("Request already sent");
/*      */       }
/*  297 */       this.requestedAttributes.add(paramString);
/*      */     }
/*      */ 
/*      */     byte[][] getValues(String paramString)
/*      */       throws NamingException
/*      */     {
/*  313 */       String str = this.name + "|" + paramString;
/*  314 */       byte[][] arrayOfByte = (byte[][])LDAPCertStore.this.valueCache.get(str);
/*  315 */       if (arrayOfByte != null) {
/*  316 */         LDAPCertStore.access$108(LDAPCertStore.this);
/*  317 */         return arrayOfByte;
/*      */       }
/*  319 */       LDAPCertStore.access$208(LDAPCertStore.this);
/*  320 */       Map localMap = getValueMap();
/*  321 */       arrayOfByte = (byte[][])localMap.get(paramString);
/*  322 */       return arrayOfByte;
/*      */     }
/*      */ 
/*      */     private Map<String, byte[][]> getValueMap()
/*      */       throws NamingException
/*      */     {
/*  340 */       if (this.valueMap != null) {
/*  341 */         return this.valueMap;
/*      */       }
/*      */ 
/*  350 */       this.valueMap = new HashMap(8);
/*  351 */       String[] arrayOfString = (String[])this.requestedAttributes.toArray(LDAPCertStore.STRING0);
/*      */       Attributes localAttributes;
/*      */       try
/*      */       {
/*  354 */         localAttributes = LDAPCertStore.this.ctx.getAttributes(this.name, arrayOfString);
/*      */       }
/*      */       catch (NameNotFoundException localNameNotFoundException)
/*      */       {
/*  358 */         localAttributes = LDAPCertStore.EMPTY_ATTRIBUTES;
/*      */       }
/*  360 */       for (String str : this.requestedAttributes) {
/*  361 */         Attribute localAttribute = localAttributes.get(str);
/*  362 */         byte[][] arrayOfByte = getAttributeValues(localAttribute);
/*  363 */         cacheAttribute(str, arrayOfByte);
/*  364 */         this.valueMap.put(str, arrayOfByte);
/*      */       }
/*  366 */       return this.valueMap;
/*      */     }
/*      */ 
/*      */     private void cacheAttribute(String paramString, byte[][] paramArrayOfByte)
/*      */     {
/*  373 */       String str = this.name + "|" + paramString;
/*  374 */       LDAPCertStore.this.valueCache.put(str, paramArrayOfByte);
/*      */     }
/*      */ 
/*      */     private byte[][] getAttributeValues(Attribute paramAttribute)
/*      */       throws NamingException
/*      */     {
/*      */       Object localObject1;
/*  385 */       if (paramAttribute == null) {
/*  386 */         localObject1 = LDAPCertStore.BB0;
/*      */       } else {
/*  388 */         localObject1 = new byte[paramAttribute.size()][];
/*  389 */         int i = 0;
/*  390 */         NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
/*  391 */         while (localNamingEnumeration.hasMore()) {
/*  392 */           Object localObject2 = localNamingEnumeration.next();
/*  393 */           if ((LDAPCertStore.debug != null) && 
/*  394 */             ((localObject2 instanceof String))) {
/*  395 */             LDAPCertStore.debug.println("LDAPCertStore.getAttrValues() enum.next is a string!: " + localObject2);
/*      */           }
/*      */ 
/*  399 */           byte[] arrayOfByte = (byte[])localObject2;
/*  400 */           localObject1[(i++)] = arrayOfByte;
/*      */         }
/*      */       }
/*  403 */       return localObject1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SunLDAPCertStoreParameters extends LDAPCertStoreParameters
/*      */   {
/*  859 */     private volatile int hashCode = 0;
/*      */ 
/*      */     SunLDAPCertStoreParameters(String paramString, int paramInt) {
/*  862 */       super(paramInt);
/*      */     }
/*      */     SunLDAPCertStoreParameters(String paramString) {
/*  865 */       super();
/*      */     }
/*      */     SunLDAPCertStoreParameters() {
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/*  871 */       if (!(paramObject instanceof LDAPCertStoreParameters)) {
/*  872 */         return false;
/*      */       }
/*  874 */       LDAPCertStoreParameters localLDAPCertStoreParameters = (LDAPCertStoreParameters)paramObject;
/*  875 */       return (getPort() == localLDAPCertStoreParameters.getPort()) && (getServerName().equalsIgnoreCase(localLDAPCertStoreParameters.getServerName()));
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  879 */       if (this.hashCode == 0) {
/*  880 */         int i = 17;
/*  881 */         i = 37 * i + getPort();
/*  882 */         i = 37 * i + getServerName().toLowerCase().hashCode();
/*  883 */         this.hashCode = i;
/*      */       }
/*  885 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.ldap.LDAPCertStore
 * JD-Core Version:    0.6.2
 */