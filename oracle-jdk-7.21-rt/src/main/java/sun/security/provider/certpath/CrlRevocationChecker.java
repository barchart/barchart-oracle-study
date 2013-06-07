/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CRLReason;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathBuilder;
/*     */ import java.security.cert.CertPathBuilderException;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorException.BasicReason;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateRevokedException;
/*     */ import java.security.cert.CollectionCertStoreParameters;
/*     */ import java.security.cert.PKIXBuilderParameters;
/*     */ import java.security.cert.PKIXCertPathBuilderResult;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXParameters;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509CRLEntry;
/*     */ import java.security.cert.X509CRLSelector;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.interfaces.DSAPublicKey;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.AuthorityInfoAccessExtension;
/*     */ import sun.security.x509.CRLDistributionPointsExtension;
/*     */ import sun.security.x509.DistributionPoint;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.GeneralNameInterface;
/*     */ import sun.security.x509.GeneralNames;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CRLEntryImpl;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ class CrlRevocationChecker extends PKIXCertPathChecker
/*     */ {
/*  69 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   private final TrustAnchor mAnchor;
/*     */   private final List<CertStore> mStores;
/*     */   private final String mSigProvider;
/*     */   private final Date mCurrentTime;
/*     */   private PublicKey mPrevPubKey;
/*     */   private boolean mCRLSignFlag;
/*     */   private HashSet<X509CRL> mPossibleCRLs;
/*     */   private HashSet<X509CRL> mApprovedCRLs;
/*     */   private final PKIXParameters mParams;
/*  79 */   private static final boolean[] mCrlSignUsage = { false, false, false, false, false, false, true };
/*     */ 
/*  81 */   private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
/*     */ 
/*  83 */   private boolean mOnlyEECert = false;
/*     */   private static final long MAX_CLOCK_SKEW = 900000L;
/*     */ 
/*     */   CrlRevocationChecker(TrustAnchor paramTrustAnchor, PKIXParameters paramPKIXParameters)
/*     */     throws CertPathValidatorException
/*     */   {
/*  99 */     this(paramTrustAnchor, paramPKIXParameters, null);
/*     */   }
/*     */ 
/*     */   CrlRevocationChecker(TrustAnchor paramTrustAnchor, PKIXParameters paramPKIXParameters, Collection<X509Certificate> paramCollection)
/*     */     throws CertPathValidatorException
/*     */   {
/* 118 */     this(paramTrustAnchor, paramPKIXParameters, paramCollection, false);
/*     */   }
/*     */ 
/*     */   CrlRevocationChecker(TrustAnchor paramTrustAnchor, PKIXParameters paramPKIXParameters, Collection<X509Certificate> paramCollection, boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/* 124 */     this.mAnchor = paramTrustAnchor;
/* 125 */     this.mParams = paramPKIXParameters;
/* 126 */     this.mStores = new ArrayList(paramPKIXParameters.getCertStores());
/* 127 */     this.mSigProvider = paramPKIXParameters.getSigProvider();
/* 128 */     if (paramCollection != null) {
/*     */       try {
/* 130 */         this.mStores.add(CertStore.getInstance("Collection", new CollectionCertStoreParameters(paramCollection)));
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 135 */         if (debug != null) {
/* 136 */           debug.println("CrlRevocationChecker: error creating Collection CertStore: " + localException);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 141 */     Date localDate = paramPKIXParameters.getDate();
/* 142 */     this.mCurrentTime = (localDate != null ? localDate : new Date());
/* 143 */     this.mOnlyEECert = paramBoolean;
/* 144 */     init(false);
/*     */   }
/*     */ 
/*     */   public void init(boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/* 153 */     if (!paramBoolean) {
/* 154 */       if (this.mAnchor != null) {
/* 155 */         if (this.mAnchor.getCAPublicKey() != null)
/* 156 */           this.mPrevPubKey = this.mAnchor.getCAPublicKey();
/*     */         else
/* 158 */           this.mPrevPubKey = this.mAnchor.getTrustedCert().getPublicKey();
/*     */       }
/*     */       else {
/* 161 */         this.mPrevPubKey = null;
/*     */       }
/* 163 */       this.mCRLSignFlag = true;
/*     */     } else {
/* 165 */       throw new CertPathValidatorException("forward checking not supported");
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isForwardCheckingSupported()
/*     */   {
/* 171 */     return false;
/*     */   }
/*     */ 
/*     */   public Set<String> getSupportedExtensions() {
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   public void check(Certificate paramCertificate, Collection<String> paramCollection)
/*     */     throws CertPathValidatorException
/*     */   {
/* 191 */     X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
/* 192 */     verifyRevocationStatus(localX509Certificate, this.mPrevPubKey, this.mCRLSignFlag, true);
/*     */ 
/* 195 */     PublicKey localPublicKey = localX509Certificate.getPublicKey();
/* 196 */     if (((localPublicKey instanceof DSAPublicKey)) && (((DSAPublicKey)localPublicKey).getParams() == null))
/*     */     {
/* 199 */       localPublicKey = BasicChecker.makeInheritedParamsKey(localPublicKey, this.mPrevPubKey);
/*     */     }
/* 201 */     this.mPrevPubKey = localPublicKey;
/* 202 */     this.mCRLSignFlag = certCanSignCrl(localX509Certificate);
/*     */   }
/*     */ 
/*     */   public boolean check(X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/* 222 */     verifyRevocationStatus(paramX509Certificate, paramPublicKey, paramBoolean, true);
/* 223 */     return certCanSignCrl(paramX509Certificate);
/*     */   }
/*     */ 
/*     */   static boolean certCanSignCrl(X509Certificate paramX509Certificate)
/*     */   {
/* 237 */     boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
/* 238 */     if (arrayOfBoolean != null) {
/* 239 */       return arrayOfBoolean[6];
/*     */     }
/* 241 */     return false;
/*     */   }
/*     */ 
/*     */   private void verifyRevocationStatus(X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean1, boolean paramBoolean2)
/*     */     throws CertPathValidatorException
/*     */   {
/* 251 */     verifyRevocationStatus(paramX509Certificate, paramPublicKey, paramBoolean1, paramBoolean2, null, this.mParams.getTrustAnchors());
/*     */   }
/*     */ 
/*     */   private void verifyRevocationStatus(X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean1, boolean paramBoolean2, Set<X509Certificate> paramSet, Set<TrustAnchor> paramSet1)
/*     */     throws CertPathValidatorException
/*     */   {
/* 270 */     String str = "revocation status";
/* 271 */     if (debug != null) {
/* 272 */       debug.println("CrlRevocationChecker.verifyRevocationStatus() ---checking " + str + "...");
/*     */     }
/*     */ 
/* 276 */     if ((this.mOnlyEECert) && (paramX509Certificate.getBasicConstraints() != -1)) {
/* 277 */       if (debug != null) {
/* 278 */         debug.println("Skipping revocation check, not end entity cert");
/*     */       }
/* 280 */       return;
/*     */     }
/*     */ 
/* 286 */     if ((paramSet != null) && (paramSet.contains(paramX509Certificate))) {
/* 287 */       if (debug != null) {
/* 288 */         debug.println("CrlRevocationChecker.verifyRevocationStatus() circular dependency");
/*     */       }
/*     */ 
/* 291 */       throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
/* 297 */     }
/*     */ this.mPossibleCRLs = new HashSet();
/* 298 */     this.mApprovedCRLs = new HashSet();
/* 299 */     boolean[] arrayOfBoolean = new boolean[9];
/*     */     Object localObject4;
/*     */     Object localObject5;
/*     */     try { X509CRLSelector localX509CRLSelector = new X509CRLSelector();
/* 303 */       localX509CRLSelector.setCertificateChecking(paramX509Certificate);
/* 304 */       CertPathHelper.setDateAndTime(localX509CRLSelector, this.mCurrentTime, 900000L);
/*     */ 
/* 306 */       for (localObject2 = this.mStores.iterator(); ((Iterator)localObject2).hasNext(); ) { localObject3 = (CertStore)((Iterator)localObject2).next();
/* 307 */         for (localObject4 = ((CertStore)localObject3).getCRLs(localX509CRLSelector).iterator(); ((Iterator)localObject4).hasNext(); ) { localObject5 = (CRL)((Iterator)localObject4).next();
/* 308 */           this.mPossibleCRLs.add((X509CRL)localObject5);
/*     */         }
/*     */       }
/* 311 */       localObject2 = DistributionPointFetcher.getInstance();
/*     */ 
/* 314 */       this.mApprovedCRLs.addAll(((DistributionPointFetcher)localObject2).getCRLs(localX509CRLSelector, paramBoolean1, paramPublicKey, this.mSigProvider, this.mStores, arrayOfBoolean, paramSet1, this.mParams.getDate()));
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 318 */       if (debug != null) {
/* 319 */         debug.println("CrlRevocationChecker.verifyRevocationStatus() unexpected exception: " + localException.getMessage());
/*     */       }
/*     */ 
/* 322 */       throw new CertPathValidatorException(localException);
/*     */     }
/*     */ 
/* 325 */     if (debug != null) {
/* 326 */       debug.println("CrlRevocationChecker.verifyRevocationStatus() crls.size() = " + this.mPossibleCRLs.size());
/*     */     }
/*     */ 
/* 329 */     if (!this.mPossibleCRLs.isEmpty())
/*     */     {
/* 332 */       this.mApprovedCRLs.addAll(verifyPossibleCRLs(this.mPossibleCRLs, paramX509Certificate, paramBoolean1, paramPublicKey, arrayOfBoolean, paramSet1));
/*     */     }
/*     */ 
/* 335 */     if (debug != null) {
/* 336 */       debug.println("CrlRevocationChecker.verifyRevocationStatus() approved crls.size() = " + this.mApprovedCRLs.size());
/*     */     }
/*     */ 
/* 342 */     if ((this.mApprovedCRLs.isEmpty()) || (!Arrays.equals(arrayOfBoolean, ALL_REASONS)))
/*     */     {
/* 344 */       if (paramBoolean2) {
/* 345 */         verifyWithSeparateSigningKey(paramX509Certificate, paramPublicKey, paramBoolean1, paramSet);
/*     */ 
/* 347 */         return;
/*     */       }
/* 349 */       throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*     */     }
/*     */ 
/* 356 */     if (debug != null) {
/* 357 */       localObject1 = paramX509Certificate.getSerialNumber();
/* 358 */       debug.println("CrlRevocationChecker.verifyRevocationStatus() starting the final sweep...");
/*     */ 
/* 360 */       debug.println("CrlRevocationChecker.verifyRevocationStatus cert SN: " + ((BigInteger)localObject1).toString());
/*     */     }
/*     */ 
/* 364 */     Object localObject1 = CRLReason.UNSPECIFIED;
/* 365 */     Object localObject2 = null;
/* 366 */     for (Object localObject3 = this.mApprovedCRLs.iterator(); ((Iterator)localObject3).hasNext(); ) { localObject4 = (X509CRL)((Iterator)localObject3).next();
/* 367 */       localObject5 = ((X509CRL)localObject4).getRevokedCertificate(paramX509Certificate);
/* 368 */       if (localObject5 != null) {
/*     */         try {
/* 370 */           localObject2 = X509CRLEntryImpl.toImpl((X509CRLEntry)localObject5);
/*     */         } catch (CRLException localCRLException) {
/* 372 */           throw new CertPathValidatorException(localCRLException);
/*     */         }
/* 374 */         if (debug != null) {
/* 375 */           debug.println("CrlRevocationChecker.verifyRevocationStatus CRL entry: " + ((X509CRLEntryImpl)localObject2).toString());
/*     */         }
/*     */ 
/* 384 */         Set localSet = ((X509CRLEntryImpl)localObject2).getCriticalExtensionOIDs();
/* 385 */         if ((localSet != null) && (!localSet.isEmpty()))
/*     */         {
/* 387 */           localSet.remove(PKIXExtensions.ReasonCode_Id.toString());
/*     */ 
/* 389 */           localSet.remove(PKIXExtensions.CertificateIssuer_Id.toString());
/*     */ 
/* 391 */           if (!localSet.isEmpty()) {
/* 392 */             if (debug != null) {
/* 393 */               debug.println("Unrecognized critical extension(s) in revoked CRL entry: " + localSet);
/*     */             }
/*     */ 
/* 397 */             throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 403 */         localObject1 = ((X509CRLEntryImpl)localObject2).getRevocationReason();
/* 404 */         if (localObject1 == null) {
/* 405 */           localObject1 = CRLReason.UNSPECIFIED;
/*     */         }
/* 407 */         CertificateRevokedException localCertificateRevokedException = new CertificateRevokedException(((X509CRLEntryImpl)localObject2).getRevocationDate(), (CRLReason)localObject1, ((X509CRL)localObject4).getIssuerX500Principal(), ((X509CRLEntryImpl)localObject2).getExtensions());
/*     */ 
/* 410 */         throw new CertPathValidatorException(localCertificateRevokedException.getMessage(), localCertificateRevokedException, null, -1, CertPathValidatorException.BasicReason.REVOKED);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void verifyWithSeparateSigningKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean, Set<X509Certificate> paramSet)
/*     */     throws CertPathValidatorException
/*     */   {
/* 441 */     String str = "revocation status";
/* 442 */     if (debug != null) {
/* 443 */       debug.println("CrlRevocationChecker.verifyWithSeparateSigningKey() ---checking " + str + "...");
/*     */     }
/*     */ 
/* 451 */     if ((paramSet != null) && (paramSet.contains(paramX509Certificate))) {
/* 452 */       if (debug != null) {
/* 453 */         debug.println("CrlRevocationChecker.verifyWithSeparateSigningKey() circular dependency");
/*     */       }
/*     */ 
/* 457 */       throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*     */     }
/*     */ 
/* 464 */     if (!paramBoolean) {
/* 465 */       paramPublicKey = null;
/*     */     }
/*     */ 
/* 470 */     buildToNewKey(paramX509Certificate, paramPublicKey, paramSet);
/*     */   }
/*     */ 
/*     */   private void buildToNewKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, Set<X509Certificate> paramSet)
/*     */     throws CertPathValidatorException
/*     */   {
/* 491 */     if (debug != null) {
/* 492 */       debug.println("CrlRevocationChecker.buildToNewKey() starting work");
/*     */     }
/*     */ 
/* 495 */     HashSet localHashSet = new HashSet();
/* 496 */     if (paramPublicKey != null) {
/* 497 */       localHashSet.add(paramPublicKey);
/*     */     }
/* 499 */     RejectKeySelector localRejectKeySelector = new RejectKeySelector(localHashSet);
/* 500 */     localRejectKeySelector.setSubject(paramX509Certificate.getIssuerX500Principal());
/* 501 */     localRejectKeySelector.setKeyUsage(mCrlSignUsage);
/*     */ 
/* 503 */     Set localSet = this.mAnchor == null ? this.mParams.getTrustAnchors() : Collections.singleton(this.mAnchor);
/*     */     PKIXBuilderParameters localPKIXBuilderParameters;
/* 508 */     if ((this.mParams instanceof PKIXBuilderParameters)) {
/* 509 */       localPKIXBuilderParameters = (PKIXBuilderParameters)this.mParams.clone();
/* 510 */       localPKIXBuilderParameters.setTargetCertConstraints(localRejectKeySelector);
/*     */ 
/* 513 */       localPKIXBuilderParameters.setPolicyQualifiersRejected(true);
/*     */       try {
/* 515 */         localPKIXBuilderParameters.setTrustAnchors(localSet);
/*     */       } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException1) {
/* 517 */         throw new RuntimeException(localInvalidAlgorithmParameterException1);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 526 */         localPKIXBuilderParameters = new PKIXBuilderParameters(localSet, localRejectKeySelector);
/*     */       } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException2) {
/* 528 */         throw new RuntimeException(localInvalidAlgorithmParameterException2);
/*     */       }
/* 530 */       localPKIXBuilderParameters.setInitialPolicies(this.mParams.getInitialPolicies());
/* 531 */       localPKIXBuilderParameters.setCertStores(this.mStores);
/* 532 */       localPKIXBuilderParameters.setExplicitPolicyRequired(this.mParams.isExplicitPolicyRequired());
/*     */ 
/* 534 */       localPKIXBuilderParameters.setPolicyMappingInhibited(this.mParams.isPolicyMappingInhibited());
/*     */ 
/* 536 */       localPKIXBuilderParameters.setAnyPolicyInhibited(this.mParams.isAnyPolicyInhibited());
/*     */ 
/* 540 */       localPKIXBuilderParameters.setDate(this.mParams.getDate());
/* 541 */       localPKIXBuilderParameters.setCertPathCheckers(this.mParams.getCertPathCheckers());
/* 542 */       localPKIXBuilderParameters.setSigProvider(this.mParams.getSigProvider());
/*     */     }
/*     */ 
/* 548 */     localPKIXBuilderParameters.setRevocationEnabled(false);
/*     */     Object localObject2;
/*     */     Object localObject3;
/* 551 */     if (Builder.USE_AIA == true) {
/* 552 */       localObject1 = null;
/*     */       try {
/* 554 */         localObject1 = X509CertImpl.toImpl(paramX509Certificate);
/*     */       }
/*     */       catch (CertificateException localCertificateException) {
/* 557 */         if (debug != null) {
/* 558 */           debug.println("CrlRevocationChecker.buildToNewKey: error decoding cert: " + localCertificateException);
/*     */         }
/*     */       }
/*     */ 
/* 562 */       AuthorityInfoAccessExtension localAuthorityInfoAccessExtension = null;
/* 563 */       if (localObject1 != null) {
/* 564 */         localAuthorityInfoAccessExtension = ((X509CertImpl)localObject1).getAuthorityInfoAccessExtension();
/*     */       }
/* 566 */       if (localAuthorityInfoAccessExtension != null) {
/* 567 */         localObject2 = localAuthorityInfoAccessExtension.getAccessDescriptions();
/* 568 */         if (localObject2 != null)
/* 569 */           for (localObject3 = ((List)localObject2).iterator(); ((Iterator)localObject3).hasNext(); ) { AccessDescription localAccessDescription = (AccessDescription)((Iterator)localObject3).next();
/* 570 */             localObject4 = URICertStore.getInstance(localAccessDescription);
/* 571 */             if (localObject4 != null) {
/* 572 */               if (debug != null) {
/* 573 */                 debug.println("adding AIAext CertStore");
/*     */               }
/* 575 */               localPKIXBuilderParameters.addCertStore((CertStore)localObject4);
/*     */             }
/*     */           }
/*     */       }
/*     */     }
/*     */     Object localObject4;
/* 582 */     Object localObject1 = null;
/*     */     try {
/* 584 */       localObject1 = CertPathBuilder.getInstance("PKIX");
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 586 */       throw new CertPathValidatorException(localNoSuchAlgorithmException);
/*     */     }
/*     */     try {
/*     */       while (true) {
/* 590 */         if (debug != null) {
/* 591 */           debug.println("CrlRevocationChecker.buildToNewKey() about to try build ...");
/*     */         }
/*     */ 
/* 594 */         PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)((CertPathBuilder)localObject1).build(localPKIXBuilderParameters);
/*     */ 
/* 597 */         if (debug != null) {
/* 598 */           debug.println("CrlRevocationChecker.buildToNewKey() about to check revocation ...");
/*     */         }
/*     */ 
/* 603 */         if (paramSet == null) {
/* 604 */           paramSet = new HashSet();
/*     */         }
/* 606 */         paramSet.add(paramX509Certificate);
/* 607 */         localObject2 = localPKIXCertPathBuilderResult.getTrustAnchor();
/* 608 */         localObject3 = ((TrustAnchor)localObject2).getCAPublicKey();
/* 609 */         if (localObject3 == null) {
/* 610 */           localObject3 = ((TrustAnchor)localObject2).getTrustedCert().getPublicKey();
/*     */         }
/* 612 */         boolean bool = true;
/* 613 */         localObject4 = localPKIXCertPathBuilderResult.getCertPath().getCertificates();
/*     */         try
/*     */         {
/* 616 */           for (int i = ((List)localObject4).size() - 1; i >= 0; i--) {
/* 617 */             X509Certificate localX509Certificate = (X509Certificate)((List)localObject4).get(i);
/*     */ 
/* 619 */             if (debug != null) {
/* 620 */               debug.println("CrlRevocationChecker.buildToNewKey() index " + i + " checking " + localX509Certificate);
/*     */             }
/*     */ 
/* 623 */             verifyRevocationStatus(localX509Certificate, (PublicKey)localObject3, bool, true, paramSet, localSet);
/*     */ 
/* 625 */             bool = certCanSignCrl(localX509Certificate);
/* 626 */             localObject3 = localX509Certificate.getPublicKey();
/*     */           }
/*     */         }
/*     */         catch (CertPathValidatorException localCertPathValidatorException1) {
/* 630 */           localHashSet.add(localPKIXCertPathBuilderResult.getPublicKey());
/* 631 */         }continue;
/*     */ 
/* 634 */         if (debug != null) {
/* 635 */           debug.println("CrlRevocationChecker.buildToNewKey() got key " + localPKIXCertPathBuilderResult.getPublicKey());
/*     */         }
/*     */ 
/* 641 */         PublicKey localPublicKey = localPKIXCertPathBuilderResult.getPublicKey();
/*     */         try {
/* 643 */           verifyRevocationStatus(paramX509Certificate, localPublicKey, true, false);
/*     */ 
/* 645 */           return;
/*     */         }
/*     */         catch (CertPathValidatorException localCertPathValidatorException2) {
/* 648 */           if (localCertPathValidatorException2.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
/* 649 */             throw localCertPathValidatorException2;
/*     */           }
/*     */ 
/* 654 */           localHashSet.add(localPublicKey);
/*     */         }
/*     */       } } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException3) { throw new CertPathValidatorException(localInvalidAlgorithmParameterException3); } catch (CertPathBuilderException localCertPathBuilderException) {
/*     */     }
/* 658 */     throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*     */   }
/*     */ 
/*     */   private Collection<X509CRL> verifyPossibleCRLs(Set<X509CRL> paramSet, X509Certificate paramX509Certificate, boolean paramBoolean, PublicKey paramPublicKey, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet1)
/*     */     throws CertPathValidatorException
/*     */   {
/*     */     try
/*     */     {
/* 742 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/* 743 */       if (debug != null) {
/* 744 */         debug.println("CRLRevocationChecker.verifyPossibleCRLs: Checking CRLDPs for " + localX509CertImpl.getSubjectX500Principal());
/*     */       }
/*     */ 
/* 748 */       CRLDistributionPointsExtension localCRLDistributionPointsExtension = localX509CertImpl.getCRLDistributionPointsExtension();
/*     */ 
/* 750 */       List localList = null;
/* 751 */       if (localCRLDistributionPointsExtension == null)
/*     */       {
/* 755 */         localObject1 = (X500Name)localX509CertImpl.getIssuerDN();
/* 756 */         localObject2 = new DistributionPoint(new GeneralNames().add(new GeneralName((GeneralNameInterface)localObject1)), null, null);
/*     */ 
/* 759 */         localList = Collections.singletonList(localObject2);
/*     */       } else {
/* 761 */         localList = (List)localCRLDistributionPointsExtension.get("points");
/*     */       }
/*     */ 
/* 764 */       Object localObject1 = new HashSet();
/* 765 */       Object localObject2 = DistributionPointFetcher.getInstance();
/*     */ 
/* 767 */       Iterator localIterator1 = localList.iterator();
/*     */       DistributionPoint localDistributionPoint;
/* 768 */       while ((localIterator1.hasNext()) && (!Arrays.equals(paramArrayOfBoolean, ALL_REASONS))) {
/* 769 */         localDistributionPoint = (DistributionPoint)localIterator1.next();
/* 770 */         for (X509CRL localX509CRL : paramSet) {
/* 771 */           if (((DistributionPointFetcher)localObject2).verifyCRL(localX509CertImpl, localDistributionPoint, localX509CRL, paramArrayOfBoolean, paramBoolean, paramPublicKey, this.mSigProvider, paramSet1, this.mStores, this.mParams.getDate()))
/*     */           {
/* 774 */             ((Set)localObject1).add(localX509CRL);
/*     */           }
/*     */         }
/*     */       }
/* 778 */       return localObject1;
/*     */     } catch (Exception localException) {
/* 780 */       if (debug != null) {
/* 781 */         debug.println("Exception while verifying CRL: " + localException.getMessage());
/* 782 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 784 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   private static class RejectKeySelector extends X509CertSelector
/*     */   {
/*     */     private final Set<PublicKey> badKeySet;
/*     */ 
/*     */     RejectKeySelector(Set<PublicKey> paramSet)
/*     */     {
/* 683 */       this.badKeySet = paramSet;
/*     */     }
/*     */ 
/*     */     public boolean match(Certificate paramCertificate)
/*     */     {
/* 694 */       if (!super.match(paramCertificate)) {
/* 695 */         return false;
/*     */       }
/* 697 */       if (this.badKeySet.contains(paramCertificate.getPublicKey())) {
/* 698 */         if (CrlRevocationChecker.debug != null)
/* 699 */           CrlRevocationChecker.debug.println("RejectCertSelector.match: bad key");
/* 700 */         return false;
/*     */       }
/*     */ 
/* 703 */       if (CrlRevocationChecker.debug != null)
/* 704 */         CrlRevocationChecker.debug.println("RejectCertSelector.match: returning true");
/* 705 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 715 */       StringBuilder localStringBuilder = new StringBuilder();
/* 716 */       localStringBuilder.append("RejectCertSelector: [\n");
/* 717 */       localStringBuilder.append(super.toString());
/* 718 */       localStringBuilder.append(this.badKeySet);
/* 719 */       localStringBuilder.append("]");
/* 720 */       return localStringBuilder.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.CrlRevocationChecker
 * JD-Core Version:    0.6.2
 */