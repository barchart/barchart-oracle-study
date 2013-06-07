/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CRLReason;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ public final class OCSPResponse
/*     */ {
/* 128 */   private static ResponseStatus[] rsvalues = ResponseStatus.values();
/*     */ 
/* 130 */   private static final Debug DEBUG = Debug.getInstance("certpath");
/* 131 */   private static final boolean dump = Debug.isOn("ocsp");
/* 132 */   private static final ObjectIdentifier OCSP_BASIC_RESPONSE_OID = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 1 });
/*     */ 
/* 134 */   private static final ObjectIdentifier OCSP_NONCE_EXTENSION_OID = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 2 });
/*     */   private static final int CERT_STATUS_GOOD = 0;
/*     */   private static final int CERT_STATUS_REVOKED = 1;
/*     */   private static final int CERT_STATUS_UNKNOWN = 2;
/*     */   private static final int NAME_TAG = 1;
/*     */   private static final int KEY_TAG = 2;
/*     */   private static final String KP_OCSP_SIGNING_OID = "1.3.6.1.5.5.7.3.9";
/*     */   private final ResponseStatus responseStatus;
/*     */   private final Map<CertId, SingleResponse> singleResponseMap;
/*     */   private static final long MAX_CLOCK_SKEW = 900000L;
/* 156 */   private static CRLReason[] values = CRLReason.values();
/*     */ 
/*     */   OCSPResponse(byte[] paramArrayOfByte, Date paramDate, List<X509Certificate> paramList)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/* 166 */     if (dump) {
/* 167 */       localObject1 = new HexDumpEncoder();
/* 168 */       DEBUG.println("\nOCSPResponse bytes...");
/* 169 */       DEBUG.println(((HexDumpEncoder)localObject1).encode(paramArrayOfByte) + "\n");
/*     */     }
/* 171 */     Object localObject1 = new DerValue(paramArrayOfByte);
/* 172 */     if (((DerValue)localObject1).tag != 48) {
/* 173 */       throw new IOException("Bad encoding in OCSP response: expected ASN.1 SEQUENCE tag.");
/*     */     }
/*     */ 
/* 176 */     DerInputStream localDerInputStream1 = ((DerValue)localObject1).getData();
/*     */ 
/* 179 */     int i = localDerInputStream1.getEnumerated();
/* 180 */     if ((i >= 0) && (i < rsvalues.length)) {
/* 181 */       this.responseStatus = rsvalues[i];
/*     */     }
/*     */     else {
/* 184 */       throw new IOException("Unknown OCSPResponse status: " + i);
/*     */     }
/* 186 */     if (DEBUG != null) {
/* 187 */       DEBUG.println("OCSP response status: " + this.responseStatus);
/*     */     }
/* 189 */     if (this.responseStatus != ResponseStatus.SUCCESSFUL)
/*     */     {
/* 191 */       this.singleResponseMap = Collections.emptyMap();
/* 192 */       return;
/*     */     }
/*     */ 
/* 196 */     localObject1 = localDerInputStream1.getDerValue();
/* 197 */     if (!((DerValue)localObject1).isContextSpecific((byte)0)) {
/* 198 */       throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 context specific tag 0.");
/*     */     }
/*     */ 
/* 201 */     DerValue localDerValue1 = ((DerValue)localObject1).data.getDerValue();
/* 202 */     if (localDerValue1.tag != 48) {
/* 203 */       throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 SEQUENCE tag.");
/*     */     }
/*     */ 
/* 208 */     localDerInputStream1 = localDerValue1.data;
/* 209 */     ObjectIdentifier localObjectIdentifier = localDerInputStream1.getOID();
/* 210 */     if (localObjectIdentifier.equals(OCSP_BASIC_RESPONSE_OID)) {
/* 211 */       if (DEBUG != null)
/* 212 */         DEBUG.println("OCSP response type: basic");
/*     */     }
/*     */     else {
/* 215 */       if (DEBUG != null) {
/* 216 */         DEBUG.println("OCSP response type: " + localObjectIdentifier);
/*     */       }
/* 218 */       throw new IOException("Unsupported OCSP response type: " + localObjectIdentifier);
/*     */     }
/*     */ 
/* 223 */     DerInputStream localDerInputStream2 = new DerInputStream(localDerInputStream1.getOctetString());
/*     */ 
/* 226 */     DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
/* 227 */     if (arrayOfDerValue1.length < 3) {
/* 228 */       throw new IOException("Unexpected BasicOCSPResponse value");
/*     */     }
/*     */ 
/* 231 */     DerValue localDerValue2 = arrayOfDerValue1[0];
/*     */ 
/* 234 */     byte[] arrayOfByte1 = arrayOfDerValue1[0].toByteArray();
/*     */ 
/* 237 */     if (localDerValue2.tag != 48) {
/* 238 */       throw new IOException("Bad encoding in tbsResponseData element of OCSP response: expected ASN.1 SEQUENCE tag.");
/*     */     }
/*     */ 
/* 241 */     DerInputStream localDerInputStream3 = localDerValue2.data;
/* 242 */     DerValue localDerValue3 = localDerInputStream3.getDerValue();
/*     */ 
/* 245 */     if (localDerValue3.isContextSpecific((byte)0))
/*     */     {
/* 247 */       if ((localDerValue3.isConstructed()) && (localDerValue3.isContextSpecific()))
/*     */       {
/* 249 */         localDerValue3 = localDerValue3.data.getDerValue();
/* 250 */         j = localDerValue3.getInteger();
/* 251 */         if (localDerValue3.data.available() != 0) {
/* 252 */           throw new IOException("Bad encoding in version  element of OCSP response: bad format");
/*     */         }
/*     */ 
/* 255 */         localDerValue3 = localDerInputStream3.getDerValue();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 260 */     int j = (short)(byte)(localDerValue3.tag & 0x1F);
/* 261 */     if (j == 1) {
/* 262 */       if (DEBUG != null) {
/* 263 */         localObject2 = new X500Name(localDerValue3.getData());
/* 264 */         DEBUG.println("OCSP Responder name: " + localObject2);
/*     */       }
/* 266 */     } else if (j == 2) {
/* 267 */       localDerValue3 = localDerValue3.data.getDerValue();
/* 268 */       if (DEBUG != null) {
/* 269 */         localObject2 = localDerValue3.getOctetString();
/* 270 */         DEBUG.println("OCSP Responder key ID: " + String.format(new StringBuilder().append("0x%0").append(localObject2.length * 2).append("x").toString(), new Object[] { new BigInteger(1, (byte[])localObject2) }));
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 276 */       throw new IOException("Bad encoding in responderID element of OCSP response: expected ASN.1 context specific tag 1 or 2");
/*     */     }
/*     */ 
/* 281 */     localDerValue3 = localDerInputStream3.getDerValue();
/* 282 */     if (DEBUG != null) {
/* 283 */       localObject2 = localDerValue3.getGeneralizedTime();
/* 284 */       DEBUG.println("OCSP response produced at: " + localObject2);
/*     */     }
/*     */ 
/* 288 */     Object localObject2 = localDerInputStream3.getSequence(1);
/* 289 */     this.singleResponseMap = new HashMap(localObject2.length);
/*     */ 
/* 291 */     if (DEBUG != null) {
/* 292 */       DEBUG.println("OCSP number of SingleResponses: " + localObject2.length);
/*     */     }
/*     */ 
/* 295 */     for (int k = 0; k < localObject2.length; k++) {
/* 296 */       SingleResponse localSingleResponse = new SingleResponse(localObject2[k], null);
/*     */ 
/* 298 */       this.singleResponseMap.put(localSingleResponse.getCertId(), localSingleResponse);
/*     */     }
/*     */ 
/* 302 */     if (localDerInputStream3.available() > 0) {
/* 303 */       localDerValue3 = localDerInputStream3.getDerValue();
/* 304 */       if (localDerValue3.isContextSpecific((byte)1)) {
/* 305 */         localObject3 = localDerValue3.data.getSequence(3);
/* 306 */         for (int m = 0; m < localObject3.length; m++) {
/* 307 */           localObject4 = new sun.security.x509.Extension(localObject3[m]);
/*     */ 
/* 309 */           if (DEBUG != null) {
/* 310 */             DEBUG.println("OCSP extension: " + localObject4);
/*     */           }
/* 312 */           if (!((sun.security.x509.Extension)localObject4).getExtensionId().equals(OCSP_NONCE_EXTENSION_OID))
/*     */           {
/* 318 */             if (((sun.security.x509.Extension)localObject4).isCritical()) {
/* 319 */               throw new IOException("Unsupported OCSP critical extension: " + ((sun.security.x509.Extension)localObject4).getExtensionId());
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 328 */     Object localObject3 = AlgorithmId.parse(arrayOfDerValue1[1]);
/*     */ 
/* 331 */     byte[] arrayOfByte2 = arrayOfDerValue1[2].getBitString();
/* 332 */     Object localObject4 = null;
/*     */     DerValue[] arrayOfDerValue2;
/* 335 */     if (arrayOfDerValue1.length > 3)
/*     */     {
/* 337 */       localObject5 = arrayOfDerValue1[3];
/* 338 */       if (!((DerValue)localObject5).isContextSpecific((byte)0)) {
/* 339 */         throw new IOException("Bad encoding in certs element of OCSP response: expected ASN.1 context specific tag 0.");
/*     */       }
/*     */ 
/* 342 */       arrayOfDerValue2 = ((DerValue)localObject5).getData().getSequence(3);
/* 343 */       localObject4 = new X509CertImpl[arrayOfDerValue2.length];
/*     */       try {
/* 345 */         for (int n = 0; n < arrayOfDerValue2.length; n++)
/* 346 */           localObject4[n] = new X509CertImpl(arrayOfDerValue2[n].toByteArray());
/*     */       }
/*     */       catch (CertificateException localCertificateException) {
/* 349 */         throw new IOException("Bad encoding in X509 Certificate", localCertificateException);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 355 */     Object localObject5 = (X509Certificate)paramList.get(0);
/*     */     byte[] arrayOfByte3;
/* 358 */     if ((localObject4 != null) && (localObject4[0] != null)) {
/* 359 */       arrayOfDerValue2 = localObject4[0];
/*     */ 
/* 361 */       if (DEBUG != null) {
/* 362 */         DEBUG.println("Signer certificate name: " + arrayOfDerValue2.getSubjectX500Principal());
/*     */ 
/* 365 */         arrayOfByte3 = arrayOfDerValue2.getSubjectKeyIdentifier();
/* 366 */         if (arrayOfByte3 != null) {
/* 367 */           DEBUG.println("Signer certificate key ID: " + String.format(new StringBuilder().append("0x%0").append(arrayOfByte3.length * 2).append("x").toString(), new Object[] { new BigInteger(1, arrayOfByte3) }));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 373 */       arrayOfByte3 = null;
/*     */ 
/* 375 */       for (X509Certificate localX509Certificate : paramList)
/*     */       {
/* 378 */         if (arrayOfDerValue2.equals(localX509Certificate))
/*     */         {
/* 381 */           localObject5 = localX509Certificate;
/* 382 */           if (DEBUG == null) break;
/* 383 */           DEBUG.println("Signer certificate is a trusted responder"); break;
/*     */         }
/*     */ 
/* 390 */         if (arrayOfDerValue2.getIssuerX500Principal().equals(localX509Certificate.getSubjectX500Principal()))
/*     */         {
/* 394 */           if (arrayOfByte3 == null) {
/* 395 */             arrayOfByte3 = arrayOfDerValue2.getIssuerKeyIdentifier();
/* 396 */             if ((arrayOfByte3 == null) && 
/* 397 */               (DEBUG != null)) {
/* 398 */               DEBUG.println("No issuer key identifier (AKID) in the signer certificate");
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 405 */           byte[] arrayOfByte4 = null;
/* 406 */           if ((arrayOfByte3 != null) && ((arrayOfByte4 = OCSPChecker.getKeyId(localX509Certificate)) != null))
/*     */           {
/* 409 */             if (Arrays.equals(arrayOfByte3, arrayOfByte4))
/*     */             {
/* 413 */               if (DEBUG != null) {
/* 414 */                 DEBUG.println("Issuer certificate key ID: " + String.format(new StringBuilder().append("0x%0").append(arrayOfByte3.length * 2).append("x").toString(), new Object[] { new BigInteger(1, arrayOfByte3) }));
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */             try
/*     */             {
/* 423 */               List localList = arrayOfDerValue2.getExtendedKeyUsage();
/*     */ 
/* 425 */               if ((localList == null) || (!localList.contains("1.3.6.1.5.5.7.3.9")))
/*     */               {
/* 428 */                 continue;
/*     */               }
/*     */             } catch (CertificateParsingException localCertificateParsingException) {
/*     */             }
/* 432 */             continue;
/*     */ 
/* 437 */             AlgorithmChecker localAlgorithmChecker = new AlgorithmChecker(new TrustAnchor(localX509Certificate, null));
/*     */ 
/* 439 */             localAlgorithmChecker.init(false);
/* 440 */             localAlgorithmChecker.check(arrayOfDerValue2, Collections.emptySet());
/*     */             try
/*     */             {
/* 445 */               arrayOfDerValue2.checkValidity();
/*     */             } catch (GeneralSecurityException localGeneralSecurityException1) {
/* 447 */               if (DEBUG != null) {
/* 448 */                 DEBUG.println("Responder's certificate not within the validity period " + localGeneralSecurityException1);
/*     */               }
/*     */             }
/* 451 */             continue;
/*     */ 
/* 461 */             sun.security.x509.Extension localExtension = arrayOfDerValue2.getExtension(PKIXExtensions.OCSPNoCheck_Id);
/*     */ 
/* 463 */             if ((localExtension != null) && 
/* 464 */               (DEBUG != null)) {
/* 465 */               DEBUG.println("Responder's certificate includes the extension id-pkix-ocsp-nocheck.");
/*     */             }
/*     */ 
/*     */             try
/*     */             {
/* 475 */               arrayOfDerValue2.verify(localX509Certificate.getPublicKey());
/* 476 */               localObject5 = arrayOfDerValue2;
/*     */ 
/* 478 */               if (DEBUG != null) {
/* 479 */                 DEBUG.println("Signer certificate was issued by a trusted responder");
/*     */               }
/*     */ 
/*     */             }
/*     */             catch (GeneralSecurityException localGeneralSecurityException2)
/*     */             {
/* 485 */               localObject5 = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 493 */     if (localObject5 != null)
/*     */     {
/* 496 */       AlgorithmChecker.check(((X509Certificate)localObject5).getPublicKey(), (AlgorithmId)localObject3);
/*     */ 
/* 499 */       if (!verifyResponse(arrayOfByte1, (X509Certificate)localObject5, (AlgorithmId)localObject3, arrayOfByte2))
/*     */       {
/* 501 */         throw new CertPathValidatorException("Error verifying OCSP Responder's signature");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 506 */       throw new CertPathValidatorException("Responder's certificate is not trusted for signing OCSP responses");
/*     */     }
/*     */   }
/*     */ 
/*     */   ResponseStatus getResponseStatus()
/*     */   {
/* 516 */     return this.responseStatus;
/*     */   }
/*     */ 
/*     */   private boolean verifyResponse(byte[] paramArrayOfByte1, X509Certificate paramX509Certificate, AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte2)
/*     */     throws CertPathValidatorException
/*     */   {
/*     */     try
/*     */     {
/* 528 */       Signature localSignature = Signature.getInstance(paramAlgorithmId.getName());
/* 529 */       localSignature.initVerify(paramX509Certificate);
/* 530 */       localSignature.update(paramArrayOfByte1);
/*     */ 
/* 532 */       if (localSignature.verify(paramArrayOfByte2)) {
/* 533 */         if (DEBUG != null) {
/* 534 */           DEBUG.println("Verified signature of OCSP Responder");
/*     */         }
/* 536 */         return true;
/*     */       }
/*     */ 
/* 539 */       if (DEBUG != null) {
/* 540 */         DEBUG.println("Error verifying signature of OCSP Responder");
/*     */       }
/*     */ 
/* 543 */       return false;
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 546 */       throw new CertPathValidatorException(localInvalidKeyException);
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 548 */       throw new CertPathValidatorException(localNoSuchAlgorithmException);
/*     */     } catch (SignatureException localSignatureException) {
/* 550 */       throw new CertPathValidatorException(localSignatureException);
/*     */     }
/*     */   }
/*     */ 
/*     */   SingleResponse getSingleResponse(CertId paramCertId)
/*     */   {
/* 559 */     return (SingleResponse)this.singleResponseMap.get(paramCertId);
/*     */   }
/*     */ 
/*     */   public static enum ResponseStatus
/*     */   {
/* 120 */     SUCCESSFUL, 
/* 121 */     MALFORMED_REQUEST, 
/* 122 */     INTERNAL_ERROR, 
/* 123 */     TRY_LATER, 
/* 124 */     UNUSED, 
/* 125 */     SIG_REQUIRED, 
/* 126 */     UNAUTHORIZED;
/*     */   }
/*     */ 
/*     */   static final class SingleResponse
/*     */     implements OCSP.RevocationStatus
/*     */   {
/*     */     private final CertId certId;
/*     */     private final OCSP.RevocationStatus.CertStatus certStatus;
/*     */     private final Date thisUpdate;
/*     */     private final Date nextUpdate;
/*     */     private final Date revocationTime;
/*     */     private final CRLReason revocationReason;
/*     */     private final Map<String, java.security.cert.Extension> singleExtensions;
/*     */ 
/*     */     private SingleResponse(DerValue paramDerValue)
/*     */       throws IOException
/*     */     {
/* 575 */       if (paramDerValue.tag != 48) {
/* 576 */         throw new IOException("Bad ASN.1 encoding in SingleResponse");
/*     */       }
/* 578 */       DerInputStream localDerInputStream = paramDerValue.data;
/*     */ 
/* 580 */       this.certId = new CertId(localDerInputStream.getDerValue().data);
/* 581 */       DerValue localDerValue = localDerInputStream.getDerValue();
/* 582 */       int i = (short)(byte)(localDerValue.tag & 0x1F);
/*     */       Object localObject1;
/*     */       int j;
/* 583 */       if (i == 1) {
/* 584 */         this.certStatus = OCSP.RevocationStatus.CertStatus.REVOKED;
/* 585 */         this.revocationTime = localDerValue.data.getGeneralizedTime();
/* 586 */         if (localDerValue.data.available() != 0) {
/* 587 */           localObject1 = localDerValue.data.getDerValue();
/* 588 */           i = (short)(byte)(((DerValue)localObject1).tag & 0x1F);
/* 589 */           if (i == 0) {
/* 590 */             j = ((DerValue)localObject1).data.getEnumerated();
/*     */ 
/* 592 */             if ((j >= 0) && (j < OCSPResponse.values.length))
/* 593 */               this.revocationReason = OCSPResponse.values[j];
/*     */             else
/* 595 */               this.revocationReason = CRLReason.UNSPECIFIED;
/*     */           }
/*     */           else {
/* 598 */             this.revocationReason = CRLReason.UNSPECIFIED;
/*     */           }
/*     */         } else {
/* 601 */           this.revocationReason = CRLReason.UNSPECIFIED;
/*     */         }
/*     */ 
/* 604 */         if (OCSPResponse.DEBUG != null) {
/* 605 */           OCSPResponse.DEBUG.println("Revocation time: " + this.revocationTime);
/* 606 */           OCSPResponse.DEBUG.println("Revocation reason: " + this.revocationReason);
/*     */         }
/*     */       } else {
/* 609 */         this.revocationTime = null;
/* 610 */         this.revocationReason = CRLReason.UNSPECIFIED;
/* 611 */         if (i == 0)
/* 612 */           this.certStatus = OCSP.RevocationStatus.CertStatus.GOOD;
/* 613 */         else if (i == 2)
/* 614 */           this.certStatus = OCSP.RevocationStatus.CertStatus.UNKNOWN;
/*     */         else {
/* 616 */           throw new IOException("Invalid certificate status");
/*     */         }
/*     */       }
/*     */ 
/* 620 */       this.thisUpdate = localDerInputStream.getGeneralizedTime();
/*     */ 
/* 622 */       if (localDerInputStream.available() == 0)
/*     */       {
/* 624 */         this.nextUpdate = null;
/*     */       } else {
/* 626 */         localDerValue = localDerInputStream.getDerValue();
/* 627 */         i = (short)(byte)(localDerValue.tag & 0x1F);
/* 628 */         if (i == 0)
/*     */         {
/* 630 */           this.nextUpdate = localDerValue.data.getGeneralizedTime();
/*     */ 
/* 632 */           if (localDerInputStream.available() != 0)
/*     */           {
/* 635 */             localDerValue = localDerInputStream.getDerValue();
/* 636 */             i = (short)(byte)(localDerValue.tag & 0x1F);
/*     */           }
/*     */         } else {
/* 639 */           this.nextUpdate = null;
/*     */         }
/*     */       }
/*     */ 
/* 643 */       if (localDerInputStream.available() > 0) {
/* 644 */         localDerValue = localDerInputStream.getDerValue();
/* 645 */         if (localDerValue.isContextSpecific((byte)1)) {
/* 646 */           localObject1 = localDerValue.data.getSequence(3);
/* 647 */           this.singleExtensions = new HashMap(localObject1.length);
/*     */ 
/* 650 */           for (j = 0; j < localObject1.length; j++) {
/* 651 */             localObject2 = new sun.security.x509.Extension(localObject1[j]);
/* 652 */             if (OCSPResponse.DEBUG != null) {
/* 653 */               OCSPResponse.DEBUG.println("OCSP single extension: " + localObject2);
/*     */             }
/*     */ 
/* 658 */             if (((sun.security.x509.Extension)localObject2).isCritical()) {
/* 659 */               throw new IOException("Unsupported OCSP critical extension: " + ((sun.security.x509.Extension)localObject2).getExtensionId());
/*     */             }
/*     */ 
/* 663 */             this.singleExtensions.put(((sun.security.x509.Extension)localObject2).getId(), localObject2);
/*     */           }
/*     */         } else {
/* 666 */           this.singleExtensions = Collections.emptyMap();
/*     */         }
/*     */       } else {
/* 669 */         this.singleExtensions = Collections.emptyMap();
/*     */       }
/*     */ 
/* 672 */       long l = System.currentTimeMillis();
/* 673 */       Object localObject2 = new Date(l + 900000L);
/* 674 */       Date localDate = new Date(l - 900000L);
/* 675 */       if (OCSPResponse.DEBUG != null) {
/* 676 */         String str = "";
/* 677 */         if (this.nextUpdate != null) {
/* 678 */           str = " until " + this.nextUpdate;
/*     */         }
/* 680 */         OCSPResponse.DEBUG.println("Response's validity interval is from " + this.thisUpdate + str);
/*     */       }
/*     */ 
/* 684 */       if (((this.thisUpdate != null) && (((Date)localObject2).before(this.thisUpdate))) || ((this.nextUpdate != null) && (localDate.after(this.nextUpdate))))
/*     */       {
/* 687 */         if (OCSPResponse.DEBUG != null) {
/* 688 */           OCSPResponse.DEBUG.println("Response is unreliable: its validity interval is out-of-date");
/*     */         }
/*     */ 
/* 691 */         throw new IOException("Response is unreliable: its validity interval is out-of-date");
/*     */       }
/*     */     }
/*     */ 
/*     */     public OCSP.RevocationStatus.CertStatus getCertStatus()
/*     */     {
/* 700 */       return this.certStatus;
/*     */     }
/*     */ 
/*     */     private CertId getCertId() {
/* 704 */       return this.certId;
/*     */     }
/*     */ 
/*     */     public Date getRevocationTime() {
/* 708 */       return (Date)this.revocationTime.clone();
/*     */     }
/*     */ 
/*     */     public CRLReason getRevocationReason() {
/* 712 */       return this.revocationReason;
/*     */     }
/*     */ 
/*     */     public Map<String, java.security.cert.Extension> getSingleExtensions()
/*     */     {
/* 717 */       return Collections.unmodifiableMap(this.singleExtensions);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 724 */       StringBuilder localStringBuilder = new StringBuilder();
/* 725 */       localStringBuilder.append("SingleResponse:  \n");
/* 726 */       localStringBuilder.append(this.certId);
/* 727 */       localStringBuilder.append("\nCertStatus: " + this.certStatus + "\n");
/* 728 */       if (this.certStatus == OCSP.RevocationStatus.CertStatus.REVOKED) {
/* 729 */         localStringBuilder.append("revocationTime is " + this.revocationTime + "\n");
/* 730 */         localStringBuilder.append("revocationReason is " + this.revocationReason + "\n");
/*     */       }
/* 732 */       localStringBuilder.append("thisUpdate is " + this.thisUpdate + "\n");
/* 733 */       if (this.nextUpdate != null) {
/* 734 */         localStringBuilder.append("nextUpdate is " + this.nextUpdate + "\n");
/*     */       }
/* 736 */       return localStringBuilder.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.OCSPResponse
 * JD-Core Version:    0.6.2
 */