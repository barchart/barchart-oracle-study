/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Security;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorException.BasicReason;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateRevokedException;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXParameters;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.AuthorityInfoAccessExtension;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.URIName;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ class OCSPChecker extends PKIXCertPathChecker
/*     */ {
/*     */   static final String OCSP_ENABLE_PROP = "ocsp.enable";
/*     */   static final String OCSP_URL_PROP = "ocsp.responderURL";
/*     */   static final String OCSP_CERT_SUBJECT_PROP = "ocsp.responderCertSubjectName";
/*     */   static final String OCSP_CERT_ISSUER_PROP = "ocsp.responderCertIssuerName";
/*     */   static final String OCSP_CERT_NUMBER_PROP = "ocsp.responderCertSerialNumber";
/*     */   private static final String HEX_DIGITS = "0123456789ABCDEFabcdef";
/*  63 */   private static final Debug DEBUG = Debug.getInstance("certpath");
/*     */   private static final boolean dump = false;
/*     */   private int remainingCerts;
/*     */   private X509Certificate[] certs;
/*     */   private CertPath cp;
/*     */   private PKIXParameters pkixParams;
/*  74 */   private boolean onlyEECert = false;
/*     */ 
/*     */   OCSPChecker(CertPath paramCertPath, PKIXParameters paramPKIXParameters)
/*     */     throws CertPathValidatorException
/*     */   {
/*  85 */     this(paramCertPath, paramPKIXParameters, false);
/*     */   }
/*     */ 
/*     */   OCSPChecker(CertPath paramCertPath, PKIXParameters paramPKIXParameters, boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/*  91 */     this.cp = paramCertPath;
/*  92 */     this.pkixParams = paramPKIXParameters;
/*  93 */     this.onlyEECert = paramBoolean;
/*  94 */     List localList = this.cp.getCertificates();
/*  95 */     this.certs = ((X509Certificate[])localList.toArray(new X509Certificate[localList.size()]));
/*  96 */     init(false);
/*     */   }
/*     */ 
/*     */   public void init(boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/* 105 */     if (!paramBoolean)
/* 106 */       this.remainingCerts = (this.certs.length + 1);
/*     */     else
/* 108 */       throw new CertPathValidatorException("Forward checking not supported");
/*     */   }
/*     */ 
/*     */   public boolean isForwardCheckingSupported()
/*     */   {
/* 114 */     return false;
/*     */   }
/*     */ 
/*     */   public Set<String> getSupportedExtensions() {
/* 118 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   public void check(Certificate paramCertificate, Collection<String> paramCollection)
/*     */     throws CertPathValidatorException
/*     */   {
/* 135 */     this.remainingCerts -= 1;
/*     */ 
/* 137 */     X509CertImpl localX509CertImpl = null;
/*     */     try {
/* 139 */       localX509CertImpl = X509CertImpl.toImpl((X509Certificate)paramCertificate);
/*     */     } catch (CertificateException localCertificateException) {
/* 141 */       throw new CertPathValidatorException(localCertificateException);
/*     */     }
/*     */ 
/* 144 */     if ((this.onlyEECert) && (localX509CertImpl.getBasicConstraints() != -1)) {
/* 145 */       if (DEBUG != null) {
/* 146 */         DEBUG.println("Skipping revocation check, not end entity cert");
/*     */       }
/* 148 */       return;
/*     */     }
/*     */ 
/* 159 */     String[] arrayOfString = getOCSPProperties();
/*     */ 
/* 162 */     URI localURI = getOCSPServerURI(localX509CertImpl, arrayOfString[0]);
/*     */ 
/* 166 */     X500Principal localX500Principal1 = null;
/* 167 */     X500Principal localX500Principal2 = null;
/* 168 */     BigInteger localBigInteger = null;
/* 169 */     if (arrayOfString[1] != null) {
/* 170 */       localX500Principal1 = new X500Principal(arrayOfString[1]);
/* 171 */     } else if ((arrayOfString[2] != null) && (arrayOfString[3] != null)) {
/* 172 */       localX500Principal2 = new X500Principal(arrayOfString[2]);
/*     */ 
/* 174 */       String str = stripOutSeparators(arrayOfString[3]);
/* 175 */       localBigInteger = new BigInteger(str, 16);
/* 176 */     } else if ((arrayOfString[2] != null) || (arrayOfString[3] != null)) {
/* 177 */       throw new CertPathValidatorException("Must specify both ocsp.responderCertIssuerName and ocsp.responderCertSerialNumber properties");
/*     */     }
/*     */ 
/* 185 */     int i = 0;
/* 186 */     if ((localX500Principal1 != null) || (localX500Principal2 != null)) {
/* 187 */       i = 1;
/*     */     }
/*     */ 
/* 192 */     Object localObject1 = null;
/* 193 */     int j = 1;
/* 194 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 196 */     if (this.remainingCerts < this.certs.length) {
/* 197 */       localObject1 = this.certs[this.remainingCerts];
/* 198 */       j = 0;
/*     */ 
/* 202 */       if (i == 0) {
/* 203 */         localArrayList.add(localObject1);
/* 204 */         if (DEBUG != null)
/* 205 */           DEBUG.println("Responder's certificate is the same as the issuer of the certificate being validated");
/*     */       }
/*     */     }
/*     */     Object localObject5;
/*     */     Object localObject6;
/*     */     Object localObject7;
/* 214 */     if ((j != 0) || (i != 0))
/*     */     {
/* 216 */       if ((DEBUG != null) && (i != 0)) {
/* 217 */         DEBUG.println("Searching trust anchors for issuer or responder certificate");
/*     */       }
/*     */ 
/* 222 */       localObject2 = this.pkixParams.getTrustAnchors().iterator();
/*     */ 
/* 224 */       if (!((Iterator)localObject2).hasNext()) {
/* 225 */         throw new CertPathValidatorException("Must specify at least one trust anchor");
/*     */       }
/*     */ 
/* 229 */       localObject3 = localX509CertImpl.getIssuerX500Principal();
/*     */ 
/* 231 */       byte[] arrayOfByte = null;
/*     */ 
/* 233 */       while ((((Iterator)localObject2).hasNext()) && ((j != 0) || (i != 0)))
/*     */       {
/* 235 */         localObject4 = (TrustAnchor)((Iterator)localObject2).next();
/* 236 */         localObject5 = ((TrustAnchor)localObject4).getTrustedCert();
/* 237 */         localObject6 = ((X509Certificate)localObject5).getSubjectX500Principal();
/*     */ 
/* 246 */         if ((j != 0) && (((X500Principal)localObject3).equals(localObject6)))
/*     */         {
/* 250 */           if (arrayOfByte == null) {
/* 251 */             arrayOfByte = localX509CertImpl.getIssuerKeyIdentifier();
/* 252 */             if ((arrayOfByte == null) && 
/* 253 */               (DEBUG != null)) {
/* 254 */               DEBUG.println("No issuer key identifier (AKID) in the certificate being validated");
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 261 */           localObject7 = null;
/* 262 */           if ((arrayOfByte != null) && ((localObject7 = getKeyId((X509Certificate)localObject5)) != null))
/*     */           {
/* 265 */             if (Arrays.equals(arrayOfByte, (byte[])localObject7))
/*     */             {
/* 269 */               if (DEBUG != null) {
/* 270 */                 DEBUG.println("Issuer certificate key ID: " + String.format(new StringBuilder().append("0x%0").append(arrayOfByte.length * 2).append("x").toString(), new Object[] { new BigInteger(1, arrayOfByte) }));
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 277 */             localObject1 = localObject5;
/* 278 */             j = 0;
/*     */ 
/* 282 */             if ((i == 0) && (localArrayList.isEmpty())) {
/* 283 */               localArrayList.add(localObject5);
/* 284 */               if (DEBUG != null) {
/* 285 */                 DEBUG.println("Responder's certificate is the same as the issuer of the certificate being validated");
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/* 293 */         else if (i != 0)
/*     */         {
/* 297 */           if (((localX500Principal1 != null) && (localX500Principal1.equals(localObject6))) || ((localX500Principal2 != null) && (localBigInteger != null) && (localX500Principal2.equals(((X509Certificate)localObject5).getIssuerX500Principal())) && (localBigInteger.equals(((X509Certificate)localObject5).getSerialNumber()))))
/*     */           {
/* 306 */             localArrayList.add(localObject5);
/*     */           }
/*     */         }
/*     */       }
/* 310 */       if (localObject1 == null) {
/* 311 */         throw new CertPathValidatorException("No trusted certificate for " + localX509CertImpl.getIssuerDN());
/*     */       }
/*     */ 
/* 316 */       if (i != 0) {
/* 317 */         if (DEBUG != null) {
/* 318 */           DEBUG.println("Searching cert stores for responder's certificate");
/*     */         }
/*     */ 
/* 321 */         localObject4 = null;
/* 322 */         if (localX500Principal1 != null) {
/* 323 */           localObject4 = new X509CertSelector();
/* 324 */           ((X509CertSelector)localObject4).setSubject(localX500Principal1);
/* 325 */         } else if ((localX500Principal2 != null) && (localBigInteger != null))
/*     */         {
/* 327 */           localObject4 = new X509CertSelector();
/* 328 */           ((X509CertSelector)localObject4).setIssuer(localX500Principal2);
/* 329 */           ((X509CertSelector)localObject4).setSerialNumber(localBigInteger);
/*     */         }
/* 331 */         if (localObject4 != null) {
/* 332 */           localObject5 = this.pkixParams.getCertStores();
/* 333 */           for (localObject6 = ((List)localObject5).iterator(); ((Iterator)localObject6).hasNext(); ) { localObject7 = (CertStore)((Iterator)localObject6).next();
/*     */             try {
/* 335 */               localArrayList.addAll(((CertStore)localObject7).getCertificates((CertSelector)localObject4));
/*     */             }
/*     */             catch (CertStoreException localCertStoreException)
/*     */             {
/* 340 */               if (DEBUG != null) {
/* 341 */                 DEBUG.println("CertStore exception:" + localCertStoreException);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 351 */     if ((i != 0) && (localArrayList.isEmpty())) {
/* 352 */       throw new CertPathValidatorException("Cannot find the responder's certificate (set using the OCSP security properties).");
/*     */     }
/*     */ 
/* 357 */     if (DEBUG != null) {
/* 358 */       DEBUG.println("Located " + localArrayList.size() + " trusted responder certificate(s)");
/*     */     }
/*     */ 
/* 366 */     Object localObject2 = null;
/* 367 */     Object localObject3 = null;
/*     */     try {
/* 369 */       localObject2 = new CertId((X509Certificate)localObject1, localX509CertImpl.getSerialNumberObject());
/*     */ 
/* 371 */       localObject3 = OCSP.check(Collections.singletonList(localObject2), localURI, localArrayList, this.pkixParams.getDate());
/*     */     }
/*     */     catch (Exception localException) {
/* 374 */       if ((localException instanceof CertPathValidatorException)) {
/* 375 */         throw ((CertPathValidatorException)localException);
/*     */       }
/*     */ 
/* 379 */       throw new CertPathValidatorException(localException);
/*     */     }
/*     */ 
/* 383 */     OCSPResponse.SingleResponse localSingleResponse = ((OCSPResponse)localObject3).getSingleResponse((CertId)localObject2);
/* 384 */     Object localObject4 = localSingleResponse.getCertStatus();
/* 385 */     if (localObject4 == OCSP.RevocationStatus.CertStatus.REVOKED) {
/* 386 */       localObject5 = new CertificateRevokedException(localSingleResponse.getRevocationTime(), localSingleResponse.getRevocationReason(), ((X509Certificate)localArrayList.get(0)).getSubjectX500Principal(), localSingleResponse.getSingleExtensions());
/*     */ 
/* 390 */       throw new CertPathValidatorException(((Throwable)localObject5).getMessage(), (Throwable)localObject5, null, -1, CertPathValidatorException.BasicReason.REVOKED);
/*     */     }
/* 392 */     if (localObject4 == OCSP.RevocationStatus.CertStatus.UNKNOWN)
/* 393 */       throw new CertPathValidatorException("Certificate's revocation status is unknown", null, this.cp, this.remainingCerts - 1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*     */   }
/*     */ 
/*     */   private static URI getOCSPServerURI(X509CertImpl paramX509CertImpl, String paramString)
/*     */     throws CertPathValidatorException
/*     */   {
/* 410 */     if (paramString != null) {
/*     */       try {
/* 412 */         return new URI(paramString);
/*     */       } catch (URISyntaxException localURISyntaxException) {
/* 414 */         throw new CertPathValidatorException(localURISyntaxException);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 419 */     AuthorityInfoAccessExtension localAuthorityInfoAccessExtension = paramX509CertImpl.getAuthorityInfoAccessExtension();
/*     */ 
/* 421 */     if (localAuthorityInfoAccessExtension == null) {
/* 422 */       throw new CertPathValidatorException("Must specify the location of an OCSP Responder");
/*     */     }
/*     */ 
/* 426 */     List localList = localAuthorityInfoAccessExtension.getAccessDescriptions();
/* 427 */     for (AccessDescription localAccessDescription : localList) {
/* 428 */       if (localAccessDescription.getAccessMethod().equals(AccessDescription.Ad_OCSP_Id))
/*     */       {
/* 431 */         GeneralName localGeneralName = localAccessDescription.getAccessLocation();
/* 432 */         if (localGeneralName.getType() == 6) {
/* 433 */           URIName localURIName = (URIName)localGeneralName.getName();
/* 434 */           return localURIName.getURI();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 439 */     throw new CertPathValidatorException("Cannot find the location of the OCSP Responder");
/*     */   }
/*     */ 
/*     */   private static String[] getOCSPProperties()
/*     */   {
/* 447 */     String[] arrayOfString = new String[4];
/*     */ 
/* 449 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 452 */         this.val$properties[0] = Security.getProperty("ocsp.responderURL");
/* 453 */         this.val$properties[1] = Security.getProperty("ocsp.responderCertSubjectName");
/*     */ 
/* 455 */         this.val$properties[2] = Security.getProperty("ocsp.responderCertIssuerName");
/*     */ 
/* 457 */         this.val$properties[3] = Security.getProperty("ocsp.responderCertSerialNumber");
/*     */ 
/* 459 */         return null;
/*     */       }
/*     */     });
/* 463 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   private static String stripOutSeparators(String paramString)
/*     */   {
/* 470 */     char[] arrayOfChar = paramString.toCharArray();
/* 471 */     StringBuilder localStringBuilder = new StringBuilder();
/* 472 */     for (int i = 0; i < arrayOfChar.length; i++) {
/* 473 */       if ("0123456789ABCDEFabcdef".indexOf(arrayOfChar[i]) != -1) {
/* 474 */         localStringBuilder.append(arrayOfChar[i]);
/*     */       }
/*     */     }
/* 477 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   static byte[] getKeyId(X509Certificate paramX509Certificate)
/*     */   {
/* 484 */     X509CertImpl localX509CertImpl = null;
/* 485 */     byte[] arrayOfByte = null;
/*     */     try
/*     */     {
/* 488 */       localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/* 489 */       arrayOfByte = localX509CertImpl.getSubjectKeyIdentifier();
/*     */ 
/* 491 */       if ((arrayOfByte == null) && 
/* 492 */         (DEBUG != null)) {
/* 493 */         DEBUG.println("No subject key identifier (SKID) in the certificate (Subject: " + paramX509Certificate.getSubjectX500Principal() + ")");
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (CertificateException localCertificateException)
/*     */     {
/* 501 */       if (DEBUG != null) {
/* 502 */         DEBUG.println("Error parsing X.509 certificate (Subject: " + paramX509Certificate.getSubjectX500Principal() + ") " + localCertificateException);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 507 */     return arrayOfByte;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.OCSPChecker
 * JD-Core Version:    0.6.2
 */