/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.PKIXBuilderParameters;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXReason;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.AuthorityInfoAccessExtension;
/*     */ import sun.security.x509.AuthorityKeyIdentifierExtension;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ class ForwardBuilder extends Builder
/*     */ {
/*  75 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   private final Set<X509Certificate> trustedCerts;
/*     */   private final Set<X500Principal> trustedSubjectDNs;
/*     */   private final Set<TrustAnchor> trustAnchors;
/*     */   private X509CertSelector eeSelector;
/*     */   private AdaptableX509CertSelector caSelector;
/*     */   private X509CertSelector caTargetSelector;
/*     */   TrustAnchor trustAnchor;
/*     */   private Comparator<X509Certificate> comparator;
/*  84 */   private boolean searchAllCertStores = true;
/*  85 */   private boolean onlyEECert = false;
/*     */ 
/*     */   ForwardBuilder(PKIXBuilderParameters paramPKIXBuilderParameters, X500Principal paramX500Principal, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  96 */     super(paramPKIXBuilderParameters, paramX500Principal);
/*     */ 
/*  99 */     this.trustAnchors = paramPKIXBuilderParameters.getTrustAnchors();
/* 100 */     this.trustedCerts = new HashSet(this.trustAnchors.size());
/* 101 */     this.trustedSubjectDNs = new HashSet(this.trustAnchors.size());
/* 102 */     for (TrustAnchor localTrustAnchor : this.trustAnchors) {
/* 103 */       X509Certificate localX509Certificate = localTrustAnchor.getTrustedCert();
/* 104 */       if (localX509Certificate != null) {
/* 105 */         this.trustedCerts.add(localX509Certificate);
/* 106 */         this.trustedSubjectDNs.add(localX509Certificate.getSubjectX500Principal());
/*     */       } else {
/* 108 */         this.trustedSubjectDNs.add(localTrustAnchor.getCA());
/*     */       }
/*     */     }
/* 111 */     this.comparator = new PKIXCertComparator(this.trustedSubjectDNs);
/* 112 */     this.searchAllCertStores = paramBoolean1;
/* 113 */     this.onlyEECert = paramBoolean2;
/*     */   }
/*     */ 
/*     */   Collection<X509Certificate> getMatchingCerts(State paramState, List<CertStore> paramList)
/*     */     throws CertStoreException, CertificateException, IOException
/*     */   {
/* 129 */     if (debug != null) {
/* 130 */       debug.println("ForwardBuilder.getMatchingCerts()...");
/*     */     }
/*     */ 
/* 133 */     ForwardState localForwardState = (ForwardState)paramState;
/*     */ 
/* 140 */     TreeSet localTreeSet = new TreeSet(this.comparator);
/*     */ 
/* 145 */     if (localForwardState.isInitial()) {
/* 146 */       getMatchingEECerts(localForwardState, paramList, localTreeSet);
/*     */     }
/* 148 */     getMatchingCACerts(localForwardState, paramList, localTreeSet);
/*     */ 
/* 150 */     return localTreeSet;
/*     */   }
/*     */ 
/*     */   private void getMatchingEECerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection)
/*     */     throws IOException
/*     */   {
/* 161 */     if (debug != null) {
/* 162 */       debug.println("ForwardBuilder.getMatchingEECerts()...");
/*     */     }
/*     */ 
/* 172 */     if (this.eeSelector == null) {
/* 173 */       this.eeSelector = ((X509CertSelector)this.targetCertConstraints.clone());
/*     */ 
/* 178 */       this.eeSelector.setCertificateValid(this.date);
/*     */ 
/* 183 */       if (this.buildParams.isExplicitPolicyRequired()) {
/* 184 */         this.eeSelector.setPolicy(getMatchingPolicies());
/*     */       }
/*     */ 
/* 189 */       this.eeSelector.setBasicConstraints(-2);
/*     */     }
/*     */ 
/* 193 */     addMatchingCerts(this.eeSelector, paramList, paramCollection, this.searchAllCertStores);
/*     */   }
/*     */ 
/*     */   private void getMatchingCACerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection)
/*     */     throws IOException
/*     */   {
/* 204 */     if (debug != null) {
/* 205 */       debug.println("ForwardBuilder.getMatchingCACerts()...");
/*     */     }
/* 207 */     int i = paramCollection.size();
/*     */ 
/* 213 */     Object localObject1 = null;
/*     */ 
/* 215 */     if (paramForwardState.isInitial()) {
/* 216 */       if (this.targetCertConstraints.getBasicConstraints() == -2)
/*     */       {
/* 218 */         return;
/*     */       }
/*     */ 
/* 224 */       if (debug != null) {
/* 225 */         debug.println("ForwardBuilder.getMatchingCACerts(): ca is target");
/*     */       }
/*     */ 
/* 228 */       if (this.caTargetSelector == null) {
/* 229 */         this.caTargetSelector = ((X509CertSelector)this.targetCertConstraints.clone());
/*     */ 
/* 242 */         if (this.buildParams.isExplicitPolicyRequired()) {
/* 243 */           this.caTargetSelector.setPolicy(getMatchingPolicies());
/*     */         }
/*     */       }
/* 246 */       localObject1 = this.caTargetSelector;
/*     */     }
/*     */     else {
/* 249 */       if (this.caSelector == null) {
/* 250 */         this.caSelector = new AdaptableX509CertSelector();
/*     */ 
/* 262 */         if (this.buildParams.isExplicitPolicyRequired()) {
/* 263 */           this.caSelector.setPolicy(getMatchingPolicies());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 269 */       this.caSelector.setSubject(paramForwardState.issuerDN);
/*     */ 
/* 276 */       CertPathHelper.setPathToNames(this.caSelector, paramForwardState.subjectNamesTraversed);
/*     */ 
/* 283 */       localObject2 = paramForwardState.cert.getAuthorityKeyIdentifierExtension();
/*     */ 
/* 285 */       this.caSelector.parseAuthorityKeyIdentifierExtension((AuthorityKeyIdentifierExtension)localObject2);
/*     */ 
/* 290 */       this.caSelector.setValidityPeriod(paramForwardState.cert.getNotBefore(), paramForwardState.cert.getNotAfter());
/*     */ 
/* 293 */       localObject1 = this.caSelector;
/*     */     }
/*     */ 
/* 302 */     ((X509CertSelector)localObject1).setBasicConstraints(-1);
/*     */ 
/* 304 */     for (Object localObject2 = this.trustedCerts.iterator(); ((Iterator)localObject2).hasNext(); ) { X509Certificate localX509Certificate = (X509Certificate)((Iterator)localObject2).next();
/* 305 */       if (((X509CertSelector)localObject1).match(localX509Certificate)) {
/* 306 */         if (debug != null) {
/* 307 */           debug.println("ForwardBuilder.getMatchingCACerts: found matching trust anchor");
/*     */         }
/*     */ 
/* 310 */         if ((paramCollection.add(localX509Certificate)) && (!this.searchAllCertStores)) {
/* 311 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 320 */     ((X509CertSelector)localObject1).setCertificateValid(this.date);
/*     */ 
/* 326 */     ((X509CertSelector)localObject1).setBasicConstraints(paramForwardState.traversedCACerts);
/*     */ 
/* 335 */     if ((paramForwardState.isInitial()) || (this.buildParams.getMaxPathLength() == -1) || (this.buildParams.getMaxPathLength() > paramForwardState.traversedCACerts))
/*     */     {
/* 339 */       if ((addMatchingCerts((X509CertSelector)localObject1, paramList, paramCollection, this.searchAllCertStores)) && (!this.searchAllCertStores))
/*     */       {
/* 341 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 345 */     if ((!paramForwardState.isInitial()) && (Builder.USE_AIA))
/*     */     {
/* 347 */       localObject2 = paramForwardState.cert.getAuthorityInfoAccessExtension();
/*     */ 
/* 349 */       if (localObject2 != null) {
/* 350 */         getCerts((AuthorityInfoAccessExtension)localObject2, paramCollection);
/*     */       }
/*     */     }
/*     */ 
/* 354 */     if (debug != null) {
/* 355 */       int j = paramCollection.size() - i;
/* 356 */       debug.println("ForwardBuilder.getMatchingCACerts: found " + j + " CA certs");
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean getCerts(AuthorityInfoAccessExtension paramAuthorityInfoAccessExtension, Collection<X509Certificate> paramCollection)
/*     */   {
/* 367 */     if (!Builder.USE_AIA) {
/* 368 */       return false;
/*     */     }
/* 370 */     List localList = paramAuthorityInfoAccessExtension.getAccessDescriptions();
/* 371 */     if ((localList == null) || (localList.isEmpty())) {
/* 372 */       return false;
/*     */     }
/*     */ 
/* 375 */     boolean bool = false;
/* 376 */     for (AccessDescription localAccessDescription : localList) {
/* 377 */       CertStore localCertStore = URICertStore.getInstance(localAccessDescription);
/*     */       try {
/* 379 */         if (paramCollection.addAll(localCertStore.getCertificates(this.caSelector)))
/*     */         {
/* 381 */           bool = true;
/* 382 */           if (!this.searchAllCertStores)
/* 383 */             return true;
/*     */         }
/*     */       }
/*     */       catch (CertStoreException localCertStoreException) {
/* 387 */         if (debug != null) {
/* 388 */           debug.println("exception getting certs from CertStore:");
/* 389 */           localCertStoreException.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 394 */     return bool;
/*     */   }
/*     */ 
/*     */   void verifyCert(X509Certificate paramX509Certificate, State paramState, List<X509Certificate> paramList)
/*     */     throws GeneralSecurityException
/*     */   {
/* 664 */     if (debug != null) {
/* 665 */       debug.println("ForwardBuilder.verifyCert(SN: " + Debug.toHexString(paramX509Certificate.getSerialNumber()) + "\n  Issuer: " + paramX509Certificate.getIssuerX500Principal() + ")" + "\n  Subject: " + paramX509Certificate.getSubjectX500Principal() + ")");
/*     */     }
/*     */ 
/* 671 */     ForwardState localForwardState = (ForwardState)paramState;
/*     */ 
/* 674 */     localForwardState.untrustedChecker.check(paramX509Certificate, Collections.emptySet());
/*     */     Object localObject1;
/* 683 */     if (paramList != null) {
/* 684 */       bool = false;
/* 685 */       for (localObject1 = paramList.iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (X509Certificate)((Iterator)localObject1).next();
/* 686 */         localObject3 = X509CertImpl.toImpl((X509Certificate)localObject2);
/* 687 */         localObject4 = ((X509CertImpl)localObject3).getPolicyMappingsExtension();
/*     */ 
/* 689 */         if (localObject4 != null) {
/* 690 */           bool = true;
/*     */         }
/* 692 */         if (debug != null) {
/* 693 */           debug.println("policyMappingFound = " + bool);
/*     */         }
/* 695 */         if ((paramX509Certificate.equals(localObject2)) && (
/* 696 */           (this.buildParams.isPolicyMappingInhibited()) || (!bool)))
/*     */         {
/* 698 */           if (debug != null) {
/* 699 */             debug.println("loop detected!!");
/*     */           }
/* 701 */           throw new CertPathValidatorException("loop detected");
/*     */         }
/*     */       }
/*     */     }
/*     */     Object localObject2;
/*     */     Object localObject3;
/*     */     Object localObject4;
/* 708 */     boolean bool = this.trustedCerts.contains(paramX509Certificate);
/*     */ 
/* 711 */     if (!bool)
/*     */     {
/* 717 */       localObject1 = paramX509Certificate.getCriticalExtensionOIDs();
/* 718 */       if (localObject1 == null) {
/* 719 */         localObject1 = Collections.emptySet();
/*     */       }
/* 721 */       for (localObject2 = localForwardState.forwardCheckers.iterator(); ((Iterator)localObject2).hasNext(); ) { localObject3 = (PKIXCertPathChecker)((Iterator)localObject2).next();
/* 722 */         ((PKIXCertPathChecker)localObject3).check(paramX509Certificate, (Collection)localObject1);
/*     */       }
/*     */ 
/* 731 */       for (localObject2 = this.buildParams.getCertPathCheckers().iterator(); ((Iterator)localObject2).hasNext(); ) { localObject3 = (PKIXCertPathChecker)((Iterator)localObject2).next();
/* 732 */         if (!((PKIXCertPathChecker)localObject3).isForwardCheckingSupported()) {
/* 733 */           localObject4 = ((PKIXCertPathChecker)localObject3).getSupportedExtensions();
/* 734 */           if (localObject4 != null) {
/* 735 */             ((Set)localObject1).removeAll((Collection)localObject4);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 744 */       if (!((Set)localObject1).isEmpty()) {
/* 745 */         ((Set)localObject1).remove(PKIXExtensions.BasicConstraints_Id.toString());
/*     */ 
/* 747 */         ((Set)localObject1).remove(PKIXExtensions.NameConstraints_Id.toString());
/*     */ 
/* 749 */         ((Set)localObject1).remove(PKIXExtensions.CertificatePolicies_Id.toString());
/*     */ 
/* 751 */         ((Set)localObject1).remove(PKIXExtensions.PolicyMappings_Id.toString());
/*     */ 
/* 753 */         ((Set)localObject1).remove(PKIXExtensions.PolicyConstraints_Id.toString());
/*     */ 
/* 755 */         ((Set)localObject1).remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
/*     */ 
/* 757 */         ((Set)localObject1).remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
/*     */ 
/* 759 */         ((Set)localObject1).remove(PKIXExtensions.KeyUsage_Id.toString());
/* 760 */         ((Set)localObject1).remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
/*     */ 
/* 763 */         if (!((Set)localObject1).isEmpty()) {
/* 764 */           throw new CertPathValidatorException("Unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 774 */     if (localForwardState.isInitial()) {
/* 775 */       return;
/*     */     }
/*     */ 
/* 779 */     if (!bool)
/*     */     {
/* 781 */       if (paramX509Certificate.getBasicConstraints() == -1) {
/* 782 */         throw new CertificateException("cert is NOT a CA cert");
/*     */       }
/*     */ 
/* 788 */       KeyChecker.verifyCAKeyUsage(paramX509Certificate);
/*     */     }
/*     */ 
/* 801 */     if (this.buildParams.isRevocationEnabled())
/*     */     {
/* 804 */       if (CrlRevocationChecker.certCanSignCrl(paramX509Certificate))
/*     */       {
/* 807 */         if (!localForwardState.keyParamsNeeded())
/*     */         {
/* 811 */           localForwardState.crlChecker.check(localForwardState.cert, paramX509Certificate.getPublicKey(), true);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 821 */     if (!localForwardState.keyParamsNeeded())
/* 822 */       localForwardState.cert.verify(paramX509Certificate.getPublicKey(), this.buildParams.getSigProvider());
/*     */   }
/*     */ 
/*     */   boolean isPathCompleted(X509Certificate paramX509Certificate)
/*     */   {
/* 840 */     for (TrustAnchor localTrustAnchor : this.trustAnchors)
/* 841 */       if (localTrustAnchor.getTrustedCert() != null) {
/* 842 */         if (paramX509Certificate.equals(localTrustAnchor.getTrustedCert())) {
/* 843 */           this.trustAnchor = localTrustAnchor;
/* 844 */           return true;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 849 */         Object localObject = localTrustAnchor.getCA();
/* 850 */         PublicKey localPublicKey = localTrustAnchor.getCAPublicKey();
/*     */ 
/* 852 */         if ((localObject != null) && (localPublicKey != null) && (((X500Principal)localObject).equals(paramX509Certificate.getSubjectX500Principal())))
/*     */         {
/* 854 */           if (localPublicKey.equals(paramX509Certificate.getPublicKey()))
/*     */           {
/* 856 */             this.trustAnchor = localTrustAnchor;
/* 857 */             return true;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 863 */         if ((localObject != null) && (((X500Principal)localObject).equals(paramX509Certificate.getIssuerX500Principal())))
/*     */         {
/* 870 */           if (this.buildParams.isRevocationEnabled()) {
/*     */             try {
/* 872 */               localObject = new CrlRevocationChecker(localTrustAnchor, this.buildParams, null, this.onlyEECert);
/*     */ 
/* 874 */               ((CrlRevocationChecker)localObject).check(paramX509Certificate, localTrustAnchor.getCAPublicKey(), true);
/*     */             } catch (CertPathValidatorException localCertPathValidatorException) {
/* 876 */               if (debug != null) {
/* 877 */                 debug.println("ForwardBuilder.isPathCompleted() cpve");
/* 878 */                 localCertPathValidatorException.printStackTrace();
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */             try
/*     */             {
/* 892 */               paramX509Certificate.verify(localTrustAnchor.getCAPublicKey(), this.buildParams.getSigProvider());
/*     */             }
/*     */             catch (InvalidKeyException localInvalidKeyException) {
/* 895 */               if (debug != null) {
/* 896 */                 debug.println("ForwardBuilder.isPathCompleted() invalid DSA key found");
/*     */               }
/*     */ 
/* 899 */               continue;
/*     */             } catch (Exception localException) {
/* 901 */               if (debug != null) {
/* 902 */                 debug.println("ForwardBuilder.isPathCompleted() unexpected exception");
/*     */ 
/* 904 */                 localException.printStackTrace();
/*     */               }
/*     */             }
/* 906 */             continue;
/*     */ 
/* 909 */             this.trustAnchor = localTrustAnchor;
/* 910 */             return true;
/*     */           }
/*     */         }
/*     */       }
/* 913 */     return false;
/*     */   }
/*     */ 
/*     */   void addCertToPath(X509Certificate paramX509Certificate, LinkedList<X509Certificate> paramLinkedList)
/*     */   {
/* 923 */     paramLinkedList.addFirst(paramX509Certificate);
/*     */   }
/*     */ 
/*     */   void removeFinalCertFromPath(LinkedList<X509Certificate> paramLinkedList)
/*     */   {
/* 931 */     paramLinkedList.removeFirst();
/*     */   }
/*     */ 
/*     */   static class PKIXCertComparator
/*     */     implements Comparator<X509Certificate>
/*     */   {
/*     */     static final String METHOD_NME = "PKIXCertComparator.compare()";
/*     */     private final Set<X500Principal> trustedSubjectDNs;
/*     */ 
/*     */     PKIXCertComparator(Set<X500Principal> paramSet)
/*     */     {
/* 441 */       this.trustedSubjectDNs = paramSet;
/*     */     }
/*     */ 
/*     */     public int compare(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
/*     */     {
/* 462 */       if (paramX509Certificate1.equals(paramX509Certificate2)) return 0;
/*     */ 
/* 464 */       X500Principal localX500Principal1 = paramX509Certificate1.getIssuerX500Principal();
/* 465 */       X500Principal localX500Principal2 = paramX509Certificate2.getIssuerX500Principal();
/* 466 */       X500Name localX500Name1 = X500Name.asX500Name(localX500Principal1);
/* 467 */       X500Name localX500Name2 = X500Name.asX500Name(localX500Principal2);
/*     */ 
/* 469 */       if (ForwardBuilder.debug != null) {
/* 470 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Issuer:  " + localX500Principal1);
/* 471 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Issuer:  " + localX500Principal2);
/*     */       }
/*     */ 
/* 477 */       if (ForwardBuilder.debug != null) {
/* 478 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() MATCH TRUSTED SUBJECT TEST...");
/*     */       }
/*     */ 
/* 481 */       boolean bool1 = this.trustedSubjectDNs.contains(localX500Principal1);
/* 482 */       boolean bool2 = this.trustedSubjectDNs.contains(localX500Principal2);
/* 483 */       if (ForwardBuilder.debug != null) {
/* 484 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() m1: " + bool1);
/* 485 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() m2: " + bool2);
/*     */       }
/* 487 */       if ((bool1) && (bool2))
/* 488 */         return -1;
/* 489 */       if (bool1)
/* 490 */         return -1;
/* 491 */       if (bool2) {
/* 492 */         return 1;
/*     */       }
/*     */ 
/* 498 */       if (ForwardBuilder.debug != null) {
/* 499 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING DESCENDANT TEST...");
/*     */       }
/* 501 */       for (Object localObject = this.trustedSubjectDNs.iterator(); ((Iterator)localObject).hasNext(); ) { localX500Principal3 = (X500Principal)((Iterator)localObject).next();
/* 502 */         localX500Name3 = X500Name.asX500Name(localX500Principal3);
/* 503 */         i = Builder.distance(localX500Name3, localX500Name1, -1);
/*     */ 
/* 505 */         j = Builder.distance(localX500Name3, localX500Name2, -1);
/*     */ 
/* 507 */         if (ForwardBuilder.debug != null) {
/* 508 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + i);
/* 509 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + j);
/*     */         }
/* 511 */         if ((i > 0) || (j > 0)) {
/* 512 */           if (i == j)
/* 513 */             return -1;
/* 514 */           if ((i > 0) && (j <= 0))
/* 515 */             return -1;
/* 516 */           if ((i <= 0) && (j > 0))
/* 517 */             return 1;
/* 518 */           if (i < j) {
/* 519 */             return -1;
/*     */           }
/* 521 */           return 1;
/*     */         }
/*     */       }
/*     */       int i;
/*     */       int j;
/* 529 */       if (ForwardBuilder.debug != null) {
/* 530 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING ANCESTOR TEST...");
/*     */       }
/* 532 */       for (localObject = this.trustedSubjectDNs.iterator(); ((Iterator)localObject).hasNext(); ) { localX500Principal3 = (X500Principal)((Iterator)localObject).next();
/* 533 */         localX500Name3 = X500Name.asX500Name(localX500Principal3);
/*     */ 
/* 535 */         i = Builder.distance(localX500Name3, localX500Name1, 2147483647);
/*     */ 
/* 537 */         j = Builder.distance(localX500Name3, localX500Name2, 2147483647);
/*     */ 
/* 539 */         if (ForwardBuilder.debug != null) {
/* 540 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + i);
/* 541 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + j);
/*     */         }
/* 543 */         if ((i < 0) || (j < 0)) {
/* 544 */           if (i == j)
/* 545 */             return -1;
/* 546 */           if ((i < 0) && (j >= 0))
/* 547 */             return -1;
/* 548 */           if ((i >= 0) && (j < 0))
/* 549 */             return 1;
/* 550 */           if (i > j) {
/* 551 */             return -1;
/*     */           }
/* 553 */           return 1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 562 */       if (ForwardBuilder.debug != null) {
/* 563 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() SAME NAMESPACE AS TRUSTED TEST...");
/*     */       }
/* 565 */       for (localObject = this.trustedSubjectDNs.iterator(); ((Iterator)localObject).hasNext(); ) { localX500Principal3 = (X500Principal)((Iterator)localObject).next();
/* 566 */         localX500Name3 = X500Name.asX500Name(localX500Principal3);
/* 567 */         localX500Name4 = localX500Name3.commonAncestor(localX500Name1);
/* 568 */         X500Name localX500Name5 = localX500Name3.commonAncestor(localX500Name2);
/* 569 */         if (ForwardBuilder.debug != null) {
/* 570 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo1: " + String.valueOf(localX500Name4));
/* 571 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo2: " + String.valueOf(localX500Name5));
/*     */         }
/* 573 */         if ((localX500Name4 != null) || (localX500Name5 != null)) {
/* 574 */           if ((localX500Name4 != null) && (localX500Name5 != null)) {
/* 575 */             m = Builder.hops(localX500Name3, localX500Name1, 2147483647);
/*     */ 
/* 577 */             int n = Builder.hops(localX500Name3, localX500Name2, 2147483647);
/*     */ 
/* 579 */             if (ForwardBuilder.debug != null) {
/* 580 */               ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto1: " + m);
/* 581 */               ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto2: " + n);
/*     */             }
/* 583 */             if (m != n) {
/* 584 */               if (m > n) {
/* 585 */                 return 1;
/*     */               }
/* 587 */               return -1;
/*     */             }
/*     */           } else { if (localX500Name4 == null) {
/* 590 */               return 1;
/*     */             }
/* 592 */             return -1;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 601 */       if (ForwardBuilder.debug != null) {
/* 602 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() CERT ISSUER/SUBJECT COMPARISON TEST...");
/*     */       }
/* 604 */       localObject = paramX509Certificate1.getSubjectX500Principal();
/* 605 */       X500Principal localX500Principal3 = paramX509Certificate2.getSubjectX500Principal();
/* 606 */       X500Name localX500Name3 = X500Name.asX500Name((X500Principal)localObject);
/* 607 */       X500Name localX500Name4 = X500Name.asX500Name(localX500Principal3);
/*     */ 
/* 609 */       if (ForwardBuilder.debug != null) {
/* 610 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Subject: " + localObject);
/* 611 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Subject: " + localX500Principal3);
/*     */       }
/* 613 */       int k = Builder.distance(localX500Name3, localX500Name1, 2147483647);
/*     */ 
/* 615 */       int m = Builder.distance(localX500Name4, localX500Name2, 2147483647);
/*     */ 
/* 617 */       if (ForwardBuilder.debug != null) {
/* 618 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI1: " + k);
/* 619 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI2: " + m);
/*     */       }
/* 621 */       if (m > k)
/* 622 */         return -1;
/* 623 */       if (m < k) {
/* 624 */         return 1;
/*     */       }
/*     */ 
/* 629 */       if (ForwardBuilder.debug != null) {
/* 630 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() no tests matched; RETURN 0");
/*     */       }
/* 632 */       return -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.ForwardBuilder
 * JD-Core Version:    0.6.2
 */