/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.security.AccessController;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertPathBuilder;
/*     */ import java.security.cert.CertPathParameters;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.PKIXBuilderParameters;
/*     */ import java.security.cert.PKIXCertPathBuilderResult;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509CRLSelector;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AuthorityKeyIdentifierExtension;
/*     */ import sun.security.x509.CRLDistributionPointsExtension;
/*     */ import sun.security.x509.DistributionPoint;
/*     */ import sun.security.x509.DistributionPointName;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.GeneralNames;
/*     */ import sun.security.x509.IssuingDistributionPointExtension;
/*     */ import sun.security.x509.KeyIdentifier;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.RDN;
/*     */ import sun.security.x509.ReasonFlags;
/*     */ import sun.security.x509.SerialNumber;
/*     */ import sun.security.x509.URIName;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CRLImpl;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ class DistributionPointFetcher
/*     */ {
/*  55 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */ 
/*  57 */   private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
/*     */ 
/*  65 */   private static final boolean USE_CRLDP = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.security.enableCRLDP"))).booleanValue();
/*     */ 
/*  69 */   private static final DistributionPointFetcher INSTANCE = new DistributionPointFetcher();
/*     */ 
/*     */   static DistributionPointFetcher getInstance()
/*     */   {
/*  81 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, String paramString, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate)
/*     */     throws CertStoreException
/*     */   {
/*  96 */     if (!USE_CRLDP) {
/*  97 */       return Collections.emptySet();
/*     */     }
/*  99 */     X509Certificate localX509Certificate = paramX509CRLSelector.getCertificateChecking();
/* 100 */     if (localX509Certificate == null)
/* 101 */       return Collections.emptySet();
/*     */     try
/*     */     {
/* 104 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(localX509Certificate);
/* 105 */       if (debug != null) {
/* 106 */         debug.println("DistributionPointFetcher.getCRLs: Checking CRLDPs for " + localX509CertImpl.getSubjectX500Principal());
/*     */       }
/*     */ 
/* 109 */       CRLDistributionPointsExtension localCRLDistributionPointsExtension = localX509CertImpl.getCRLDistributionPointsExtension();
/*     */ 
/* 111 */       if (localCRLDistributionPointsExtension == null) {
/* 112 */         if (debug != null) {
/* 113 */           debug.println("No CRLDP ext");
/*     */         }
/* 115 */         return Collections.emptySet();
/*     */       }
/* 117 */       List localList = (List)localCRLDistributionPointsExtension.get("points");
/*     */ 
/* 119 */       HashSet localHashSet = new HashSet();
/* 120 */       Iterator localIterator = localList.iterator();
/* 121 */       while ((localIterator.hasNext()) && (!Arrays.equals(paramArrayOfBoolean, ALL_REASONS))) {
/* 122 */         DistributionPoint localDistributionPoint = (DistributionPoint)localIterator.next();
/* 123 */         Collection localCollection = getCRLs(paramX509CRLSelector, localX509CertImpl, localDistributionPoint, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramString, paramList, paramSet, paramDate);
/*     */ 
/* 126 */         localHashSet.addAll(localCollection);
/*     */       }
/* 128 */       if (debug != null) {
/* 129 */         debug.println("Returning " + localHashSet.size() + " CRLs");
/*     */       }
/* 131 */       return localHashSet;
/*     */     } catch (CertificateException localCertificateException) {
/* 133 */       return Collections.emptySet(); } catch (IOException localIOException) {
/*     */     }
/* 135 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   private Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, String paramString, List<CertStore> paramList, Set<TrustAnchor> paramSet, Date paramDate)
/*     */   {
/* 150 */     GeneralNames localGeneralNames1 = paramDistributionPoint.getFullName();
/* 151 */     if (localGeneralNames1 == null)
/*     */     {
/* 153 */       localObject1 = paramDistributionPoint.getRelativeName();
/* 154 */       if (localObject1 == null)
/* 155 */         return Collections.emptySet();
/*     */       try
/*     */       {
/* 158 */         GeneralNames localGeneralNames2 = paramDistributionPoint.getCRLIssuer();
/* 159 */         if (localGeneralNames2 == null) {
/* 160 */           localGeneralNames1 = getFullNames((X500Name)paramX509CertImpl.getIssuerDN(), (RDN)localObject1);
/*     */         }
/*     */         else
/*     */         {
/* 164 */           if (localGeneralNames2.size() != 1) {
/* 165 */             return Collections.emptySet();
/*     */           }
/* 167 */           localGeneralNames1 = getFullNames((X500Name)localGeneralNames2.get(0).getName(), (RDN)localObject1);
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 172 */         return Collections.emptySet();
/*     */       }
/*     */     }
/* 175 */     Object localObject1 = new ArrayList();
/* 176 */     ArrayList localArrayList = new ArrayList(2);
/* 177 */     for (Iterator localIterator = localGeneralNames1.iterator(); localIterator.hasNext(); ) {
/* 178 */       localObject2 = (GeneralName)localIterator.next();
/*     */       Object localObject3;
/* 179 */       if (((GeneralName)localObject2).getType() == 4) {
/* 180 */         localObject3 = (X500Name)((GeneralName)localObject2).getName();
/* 181 */         ((Collection)localObject1).addAll(getCRLs((X500Name)localObject3, paramX509CertImpl.getIssuerX500Principal(), paramList));
/*     */       }
/* 184 */       else if (((GeneralName)localObject2).getType() == 6) {
/* 185 */         localObject3 = (URIName)((GeneralName)localObject2).getName();
/* 186 */         X509CRL localX509CRL = getCRL((URIName)localObject3);
/* 187 */         if (localX509CRL != null)
/* 188 */           ((Collection)localObject1).add(localX509CRL);
/*     */       }
/*     */     }
/* 193 */     Object localObject2;
/* 193 */     for (localIterator = ((Collection)localObject1).iterator(); localIterator.hasNext(); ) { localObject2 = (X509CRL)localIterator.next();
/*     */       try
/*     */       {
/* 197 */         paramX509CRLSelector.setIssuerNames(null);
/* 198 */         if ((paramX509CRLSelector.match((CRL)localObject2)) && (verifyCRL(paramX509CertImpl, paramDistributionPoint, (X509CRL)localObject2, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramString, paramSet, paramList, paramDate)))
/*     */         {
/* 201 */           localArrayList.add(localObject2);
/*     */         }
/*     */       }
/*     */       catch (Exception localException) {
/* 205 */         if (debug != null) {
/* 206 */           debug.println("Exception verifying CRL: " + localException.getMessage());
/* 207 */           localException.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 211 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private X509CRL getCRL(URIName paramURIName)
/*     */   {
/* 218 */     URI localURI = paramURIName.getURI();
/* 219 */     if (debug != null)
/* 220 */       debug.println("Trying to fetch CRL from DP " + localURI);
/*     */     try
/*     */     {
/* 223 */       CertStore localCertStore = URICertStore.getInstance(new URICertStore.URICertStoreParameters(localURI));
/*     */ 
/* 225 */       Collection localCollection = localCertStore.getCRLs(null);
/* 226 */       if (localCollection.isEmpty()) {
/* 227 */         return null;
/*     */       }
/* 229 */       return (X509CRL)localCollection.iterator().next();
/*     */     }
/*     */     catch (Exception localException) {
/* 232 */       if (debug != null) {
/* 233 */         debug.println("Exception getting CRL from CertStore: " + localException);
/* 234 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 237 */     return null;
/*     */   }
/*     */ 
/*     */   private Collection<X509CRL> getCRLs(X500Name paramX500Name, X500Principal paramX500Principal, List<CertStore> paramList)
/*     */   {
/* 246 */     if (debug != null) {
/* 247 */       debug.println("Trying to fetch CRL from DP " + paramX500Name);
/*     */     }
/* 249 */     X509CRLSelector localX509CRLSelector = new X509CRLSelector();
/* 250 */     localX509CRLSelector.addIssuer(paramX500Name.asX500Principal());
/* 251 */     localX509CRLSelector.addIssuer(paramX500Principal);
/* 252 */     ArrayList localArrayList = new ArrayList();
/* 253 */     for (CertStore localCertStore : paramList) {
/*     */       try {
/* 255 */         for (CRL localCRL : localCertStore.getCRLs(localX509CRLSelector))
/* 256 */           localArrayList.add((X509CRL)localCRL);
/*     */       }
/*     */       catch (CertStoreException localCertStoreException)
/*     */       {
/* 260 */         if (debug != null) {
/* 261 */           debug.println("Non-fatal exception while retrieving CRLs: " + localCertStoreException);
/*     */ 
/* 263 */           localCertStoreException.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 267 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   boolean verifyCRL(X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, X509CRL paramX509CRL, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, String paramString, Set<TrustAnchor> paramSet, List<CertStore> paramList, Date paramDate)
/*     */     throws CRLException, IOException
/*     */   {
/* 294 */     int i = 0;
/* 295 */     X509CRLImpl localX509CRLImpl = X509CRLImpl.toImpl(paramX509CRL);
/* 296 */     IssuingDistributionPointExtension localIssuingDistributionPointExtension = localX509CRLImpl.getIssuingDistributionPointExtension();
/*     */ 
/* 298 */     X500Name localX500Name1 = (X500Name)paramX509CertImpl.getIssuerDN();
/* 299 */     X500Name localX500Name2 = (X500Name)localX509CRLImpl.getIssuerDN();
/*     */ 
/* 305 */     GeneralNames localGeneralNames = paramDistributionPoint.getCRLIssuer();
/* 306 */     X500Name localX500Name3 = null;
/*     */     Object localObject3;
/* 307 */     if (localGeneralNames != null) {
/* 308 */       if ((localIssuingDistributionPointExtension == null) || (((Boolean)localIssuingDistributionPointExtension.get("indirect_crl")).equals(Boolean.FALSE)))
/*     */       {
/* 312 */         return false;
/*     */       }
/* 314 */       int j = 0;
/* 315 */       localObject2 = localGeneralNames.iterator();
/* 316 */       while ((j == 0) && (((Iterator)localObject2).hasNext())) {
/* 317 */         localObject3 = ((GeneralName)((Iterator)localObject2).next()).getName();
/* 318 */         if (localX500Name2.equals(localObject3) == true) {
/* 319 */           localX500Name3 = (X500Name)localObject3;
/* 320 */           j = 1;
/*     */         }
/*     */       }
/* 323 */       if (j == 0) {
/* 324 */         return false;
/*     */       }
/*     */ 
/* 329 */       if (issues(paramX509CertImpl, localX509CRLImpl, paramString))
/*     */       {
/* 331 */         paramPublicKey = paramX509CertImpl.getPublicKey();
/*     */       }
/* 333 */       else i = 1; 
/*     */     }
/* 335 */     else { if (!localX500Name2.equals(localX500Name1)) {
/* 336 */         if (debug != null) {
/* 337 */           debug.println("crl issuer does not equal cert issuer");
/*     */         }
/* 339 */         return false;
/*     */       }
/*     */ 
/* 342 */       localObject1 = paramX509CertImpl.getExtensionValue(PKIXExtensions.AuthorityKey_Id.toString());
/*     */ 
/* 344 */       localObject2 = localX509CRLImpl.getExtensionValue(PKIXExtensions.AuthorityKey_Id.toString());
/*     */ 
/* 347 */       if ((localObject1 == null) || (localObject2 == null))
/*     */       {
/* 352 */         if (issues(paramX509CertImpl, localX509CRLImpl, paramString))
/*     */         {
/* 354 */           paramPublicKey = paramX509CertImpl.getPublicKey();
/*     */         }
/* 356 */       } else if (!Arrays.equals((byte[])localObject1, (byte[])localObject2))
/*     */       {
/* 359 */         if (issues(paramX509CertImpl, localX509CRLImpl, paramString))
/*     */         {
/* 361 */           paramPublicKey = paramX509CertImpl.getPublicKey();
/*     */         }
/* 363 */         else i = 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 368 */     if ((i == 0) && (!paramBoolean))
/*     */     {
/* 370 */       return false;
/*     */     }
/*     */     Object localObject6;
/*     */     Object localObject7;
/*     */     Object localObject8;
/*     */     Object localObject4;
/* 373 */     if (localIssuingDistributionPointExtension != null) {
/* 374 */       localObject1 = (DistributionPointName)localIssuingDistributionPointExtension.get("point");
/*     */ 
/* 376 */       if (localObject1 != null) {
/* 377 */         localObject2 = ((DistributionPointName)localObject1).getFullName();
/* 378 */         if (localObject2 == null) {
/* 379 */           localObject3 = ((DistributionPointName)localObject1).getRelativeName();
/* 380 */           if (localObject3 == null) {
/* 381 */             if (debug != null) {
/* 382 */               debug.println("IDP must be relative or full DN");
/*     */             }
/* 384 */             return false;
/*     */           }
/* 386 */           if (debug != null) {
/* 387 */             debug.println("IDP relativeName:" + localObject3);
/*     */           }
/* 389 */           localObject2 = getFullNames(localX500Name2, (RDN)localObject3);
/*     */         }
/*     */         Object localObject5;
/* 394 */         if ((paramDistributionPoint.getFullName() != null) || (paramDistributionPoint.getRelativeName() != null))
/*     */         {
/* 396 */           localObject3 = paramDistributionPoint.getFullName();
/* 397 */           if (localObject3 == null) {
/* 398 */             RDN localRDN = paramDistributionPoint.getRelativeName();
/* 399 */             if (localRDN == null) {
/* 400 */               if (debug != null) {
/* 401 */                 debug.println("DP must be relative or full DN");
/*     */               }
/* 403 */               return false;
/*     */             }
/* 405 */             if (debug != null) {
/* 406 */               debug.println("DP relativeName:" + localRDN);
/*     */             }
/* 408 */             if (i != 0) {
/* 409 */               if (localGeneralNames.size() != 1)
/*     */               {
/* 412 */                 if (debug != null) {
/* 413 */                   debug.println("must only be one CRL issuer when relative name present");
/*     */                 }
/*     */ 
/* 416 */                 return false;
/*     */               }
/* 418 */               localObject3 = getFullNames(localX500Name3, localRDN);
/*     */             }
/*     */             else {
/* 421 */               localObject3 = getFullNames(localX500Name1, localRDN);
/*     */             }
/*     */           }
/* 424 */           boolean bool2 = false;
/* 425 */           localObject5 = ((GeneralNames)localObject2).iterator();
/* 426 */           while ((!bool2) && (((Iterator)localObject5).hasNext())) {
/* 427 */             localObject6 = ((GeneralName)((Iterator)localObject5).next()).getName();
/* 428 */             if (debug != null) {
/* 429 */               debug.println("idpName: " + localObject6);
/*     */             }
/* 431 */             localObject7 = ((GeneralNames)localObject3).iterator();
/* 432 */             while ((!bool2) && (((Iterator)localObject7).hasNext())) {
/* 433 */               localObject8 = ((GeneralName)((Iterator)localObject7).next()).getName();
/* 434 */               if (debug != null) {
/* 435 */                 debug.println("pointName: " + localObject8);
/*     */               }
/* 437 */               bool2 = localObject6.equals(localObject8);
/*     */             }
/*     */           }
/* 440 */           if (!bool2) {
/* 441 */             if (debug != null) {
/* 442 */               debug.println("IDP name does not match DP name");
/*     */             }
/* 444 */             return false;
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 453 */           boolean bool1 = false;
/* 454 */           localObject4 = localGeneralNames.iterator();
/* 455 */           while ((!bool1) && (((Iterator)localObject4).hasNext())) {
/* 456 */             localObject5 = ((GeneralName)((Iterator)localObject4).next()).getName();
/* 457 */             localObject6 = ((GeneralNames)localObject2).iterator();
/* 458 */             while ((!bool1) && (((Iterator)localObject6).hasNext())) {
/* 459 */               localObject7 = ((GeneralName)((Iterator)localObject6).next()).getName();
/* 460 */               bool1 = localObject5.equals(localObject7);
/*     */             }
/*     */           }
/* 463 */           if (!bool1) {
/* 464 */             return false;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 471 */       localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_user_certs");
/*     */ 
/* 473 */       if ((((Boolean)localObject2).equals(Boolean.TRUE)) && (paramX509CertImpl.getBasicConstraints() != -1)) {
/* 474 */         if (debug != null) {
/* 475 */           debug.println("cert must be a EE cert");
/*     */         }
/* 477 */         return false;
/*     */       }
/*     */ 
/* 482 */       localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_ca_certs");
/*     */ 
/* 484 */       if ((((Boolean)localObject2).equals(Boolean.TRUE)) && (paramX509CertImpl.getBasicConstraints() == -1)) {
/* 485 */         if (debug != null) {
/* 486 */           debug.println("cert must be a CA cert");
/*     */         }
/* 488 */         return false;
/*     */       }
/*     */ 
/* 493 */       localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_attribute_certs");
/*     */ 
/* 495 */       if (((Boolean)localObject2).equals(Boolean.TRUE)) {
/* 496 */         if (debug != null) {
/* 497 */           debug.println("cert must not be an AA cert");
/*     */         }
/* 499 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 504 */     Object localObject1 = new boolean[9];
/* 505 */     Object localObject2 = null;
/* 506 */     if (localIssuingDistributionPointExtension != null) {
/* 507 */       localObject2 = (ReasonFlags)localIssuingDistributionPointExtension.get("reasons");
/*     */     }
/*     */ 
/* 511 */     boolean[] arrayOfBoolean = paramDistributionPoint.getReasonFlags();
/* 512 */     if (localObject2 != null) {
/* 513 */       if (arrayOfBoolean != null)
/*     */       {
/* 516 */         localObject4 = ((ReasonFlags)localObject2).getFlags();
/* 517 */         for (m = 0; m < localObject4.length; m++) {
/* 518 */           if ((localObject4[m] != 0) && (arrayOfBoolean[m] != 0)) {
/* 519 */             localObject1[m] = 1;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 526 */         localObject1 = (boolean[])((ReasonFlags)localObject2).getFlags().clone();
/*     */       }
/* 528 */     } else if ((localIssuingDistributionPointExtension == null) || (localObject2 == null)) {
/* 529 */       if (arrayOfBoolean != null)
/*     */       {
/* 531 */         localObject1 = (boolean[])arrayOfBoolean.clone();
/*     */       }
/*     */       else {
/* 534 */         localObject1 = new boolean[9];
/* 535 */         Arrays.fill((boolean[])localObject1, true);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 541 */     int k = 0;
/* 542 */     for (int m = 0; (m < localObject1.length) && (k == 0); m++) {
/* 543 */       if ((paramArrayOfBoolean[m] == 0) && (localObject1[m] != 0)) {
/* 544 */         k = 1;
/*     */       }
/*     */     }
/* 547 */     if (k == 0) {
/* 548 */       return false;
/*     */     }
/*     */ 
/* 554 */     if (i != 0) {
/* 555 */       X509CertSelector localX509CertSelector = new X509CertSelector();
/* 556 */       localX509CertSelector.setSubject(localX500Name2.asX500Principal());
/* 557 */       localObject6 = new boolean[] { false, false, false, false, false, false, true };
/* 558 */       localX509CertSelector.setKeyUsage((boolean[])localObject6);
/*     */ 
/* 570 */       localObject7 = localX509CRLImpl.getAuthKeyIdExtension();
/*     */ 
/* 572 */       if (localObject7 != null) {
/* 573 */         localObject8 = (KeyIdentifier)((AuthorityKeyIdentifierExtension)localObject7).get("key_id");
/* 574 */         if (localObject8 != null) {
/* 575 */           localObject9 = new DerOutputStream();
/* 576 */           ((DerOutputStream)localObject9).putOctetString(((KeyIdentifier)localObject8).getIdentifier());
/* 577 */           localX509CertSelector.setSubjectKeyIdentifier(((DerOutputStream)localObject9).toByteArray());
/*     */         }
/*     */ 
/* 580 */         localObject9 = (SerialNumber)((AuthorityKeyIdentifierExtension)localObject7).get("serial_number");
/*     */ 
/* 582 */         if (localObject9 != null) {
/* 583 */           localX509CertSelector.setSerialNumber(((SerialNumber)localObject9).getNumber());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 593 */       localObject8 = new HashSet(paramSet);
/*     */ 
/* 595 */       if (paramPublicKey != null)
/*     */       {
/* 597 */         localObject9 = paramX509CertImpl.getIssuerX500Principal();
/* 598 */         TrustAnchor localTrustAnchor = new TrustAnchor((X500Principal)localObject9, paramPublicKey, null);
/*     */ 
/* 600 */         ((Set)localObject8).add(localTrustAnchor);
/*     */       }
/*     */ 
/* 603 */       Object localObject9 = null;
/*     */       try {
/* 605 */         localObject9 = new PKIXBuilderParameters((Set)localObject8, localX509CertSelector);
/*     */       } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {
/* 607 */         throw new CRLException(localInvalidAlgorithmParameterException);
/*     */       }
/* 609 */       ((PKIXBuilderParameters)localObject9).setCertStores(paramList);
/* 610 */       ((PKIXBuilderParameters)localObject9).setSigProvider(paramString);
/* 611 */       ((PKIXBuilderParameters)localObject9).setDate(paramDate);
/*     */       try {
/* 613 */         CertPathBuilder localCertPathBuilder = CertPathBuilder.getInstance("PKIX");
/* 614 */         PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)localCertPathBuilder.build((CertPathParameters)localObject9);
/*     */ 
/* 616 */         paramPublicKey = localPKIXCertPathBuilderResult.getPublicKey();
/*     */       } catch (Exception localException2) {
/* 618 */         throw new CRLException(localException2);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 624 */       AlgorithmChecker.check(paramPublicKey, paramX509CRL);
/*     */     } catch (CertPathValidatorException localCertPathValidatorException) {
/* 626 */       if (debug != null) {
/* 627 */         debug.println("CRL signature algorithm check failed: " + localCertPathValidatorException);
/*     */       }
/* 629 */       return false;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 634 */       paramX509CRL.verify(paramPublicKey, paramString);
/*     */     } catch (Exception localException1) {
/* 636 */       if (debug != null) {
/* 637 */         debug.println("CRL signature failed to verify");
/*     */       }
/* 639 */       return false;
/*     */     }
/*     */ 
/* 643 */     Set localSet = paramX509CRL.getCriticalExtensionOIDs();
/*     */ 
/* 645 */     if (localSet != null) {
/* 646 */       localSet.remove(PKIXExtensions.IssuingDistributionPoint_Id.toString());
/*     */ 
/* 648 */       if (!localSet.isEmpty()) {
/* 649 */         if (debug != null) {
/* 650 */           debug.println("Unrecognized critical extension(s) in CRL: " + localSet);
/*     */ 
/* 652 */           localObject6 = localSet.iterator();
/* 653 */           while (((Iterator)localObject6).hasNext())
/* 654 */             debug.println((String)((Iterator)localObject6).next());
/*     */         }
/* 656 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 661 */     for (int n = 0; n < localObject1.length; n++) {
/* 662 */       if ((paramArrayOfBoolean[n] == 0) && (localObject1[n] != 0)) {
/* 663 */         paramArrayOfBoolean[n] = true;
/*     */       }
/*     */     }
/* 666 */     return true;
/*     */   }
/*     */ 
/*     */   private GeneralNames getFullNames(X500Name paramX500Name, RDN paramRDN)
/*     */     throws IOException
/*     */   {
/* 675 */     ArrayList localArrayList = new ArrayList(paramX500Name.rdns());
/* 676 */     localArrayList.add(paramRDN);
/* 677 */     X500Name localX500Name = new X500Name((RDN[])localArrayList.toArray(new RDN[0]));
/* 678 */     GeneralNames localGeneralNames = new GeneralNames();
/* 679 */     localGeneralNames.add(new GeneralName(localX500Name));
/* 680 */     return localGeneralNames;
/*     */   }
/*     */ 
/*     */   private static boolean issues(X509CertImpl paramX509CertImpl, X509CRLImpl paramX509CRLImpl, String paramString)
/*     */     throws IOException
/*     */   {
/* 692 */     boolean bool = false;
/*     */ 
/* 694 */     AdaptableX509CertSelector localAdaptableX509CertSelector = new AdaptableX509CertSelector();
/*     */ 
/* 698 */     boolean[] arrayOfBoolean = paramX509CertImpl.getKeyUsage();
/* 699 */     if (arrayOfBoolean != null) {
/* 700 */       arrayOfBoolean[6] = true;
/* 701 */       localAdaptableX509CertSelector.setKeyUsage(arrayOfBoolean);
/*     */     }
/*     */ 
/* 705 */     X500Principal localX500Principal = paramX509CRLImpl.getIssuerX500Principal();
/* 706 */     localAdaptableX509CertSelector.setSubject(localX500Principal);
/*     */ 
/* 716 */     AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = paramX509CRLImpl.getAuthKeyIdExtension();
/* 717 */     if (localAuthorityKeyIdentifierExtension != null) {
/* 718 */       localAdaptableX509CertSelector.parseAuthorityKeyIdentifierExtension(localAuthorityKeyIdentifierExtension);
/*     */     }
/*     */ 
/* 721 */     bool = localAdaptableX509CertSelector.match(paramX509CertImpl);
/*     */ 
/* 724 */     if ((bool) && ((localAuthorityKeyIdentifierExtension == null) || (paramX509CertImpl.getAuthorityKeyIdentifierExtension() == null))) {
/*     */       try
/*     */       {
/* 727 */         paramX509CRLImpl.verify(paramX509CertImpl.getPublicKey(), paramString);
/* 728 */         bool = true;
/*     */       } catch (Exception localException) {
/* 730 */         bool = false;
/*     */       }
/*     */     }
/*     */ 
/* 734 */     return bool;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.DistributionPointFetcher
 * JD-Core Version:    0.6.2
 */