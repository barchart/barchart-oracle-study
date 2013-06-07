/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Principal;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CRLImpl;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ import sun.security.x509.X509CertInfo;
/*     */ 
/*     */ public class PKCS7
/*     */ {
/*     */   private ObjectIdentifier contentType;
/*  60 */   private BigInteger version = null;
/*  61 */   private AlgorithmId[] digestAlgorithmIds = null;
/*  62 */   private ContentInfo contentInfo = null;
/*  63 */   private X509Certificate[] certificates = null;
/*  64 */   private X509CRL[] crls = null;
/*  65 */   private SignerInfo[] signerInfos = null;
/*     */ 
/*  67 */   private boolean oldStyle = false;
/*     */   private Principal[] certIssuerNames;
/*     */ 
/*     */   public PKCS7(InputStream paramInputStream)
/*     */     throws ParsingException, IOException
/*     */   {
/*  80 */     DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/*  81 */     byte[] arrayOfByte = new byte[localDataInputStream.available()];
/*  82 */     localDataInputStream.readFully(arrayOfByte);
/*     */ 
/*  84 */     parse(new DerInputStream(arrayOfByte));
/*     */   }
/*     */ 
/*     */   public PKCS7(DerInputStream paramDerInputStream)
/*     */     throws ParsingException
/*     */   {
/*  95 */     parse(paramDerInputStream);
/*     */   }
/*     */ 
/*     */   public PKCS7(byte[] paramArrayOfByte)
/*     */     throws ParsingException
/*     */   {
/*     */     try
/*     */     {
/* 107 */       DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte);
/* 108 */       parse(localDerInputStream);
/*     */     } catch (IOException localIOException) {
/* 110 */       ParsingException localParsingException = new ParsingException("Unable to parse the encoded bytes");
/*     */ 
/* 112 */       localParsingException.initCause(localIOException);
/* 113 */       throw localParsingException;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parse(DerInputStream paramDerInputStream)
/*     */     throws ParsingException
/*     */   {
/*     */     try
/*     */     {
/* 124 */       paramDerInputStream.mark(paramDerInputStream.available());
/*     */ 
/* 126 */       parse(paramDerInputStream, false);
/*     */     } catch (IOException localIOException1) {
/*     */       try {
/* 129 */         paramDerInputStream.reset();
/*     */ 
/* 131 */         parse(paramDerInputStream, true);
/* 132 */         this.oldStyle = true;
/*     */       } catch (IOException localIOException2) {
/* 134 */         ParsingException localParsingException = new ParsingException(localIOException2.getMessage());
/*     */ 
/* 136 */         localParsingException.initCause(localIOException2);
/* 137 */         throw localParsingException;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parse(DerInputStream paramDerInputStream, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 152 */     this.contentInfo = new ContentInfo(paramDerInputStream, paramBoolean);
/* 153 */     this.contentType = this.contentInfo.contentType;
/* 154 */     DerValue localDerValue = this.contentInfo.getContent();
/*     */ 
/* 156 */     if (this.contentType.equals(ContentInfo.SIGNED_DATA_OID))
/* 157 */       parseSignedData(localDerValue);
/* 158 */     else if (this.contentType.equals(ContentInfo.OLD_SIGNED_DATA_OID))
/*     */     {
/* 160 */       parseOldSignedData(localDerValue);
/* 161 */     } else if (this.contentType.equals(ContentInfo.NETSCAPE_CERT_SEQUENCE_OID))
/* 162 */       parseNetscapeCertChain(localDerValue);
/*     */     else
/* 164 */       throw new ParsingException("content type " + this.contentType + " not supported.");
/*     */   }
/*     */ 
/*     */   public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, X509CRL[] paramArrayOfX509CRL, SignerInfo[] paramArrayOfSignerInfo)
/*     */   {
/* 184 */     this.version = BigInteger.ONE;
/* 185 */     this.digestAlgorithmIds = paramArrayOfAlgorithmId;
/* 186 */     this.contentInfo = paramContentInfo;
/* 187 */     this.certificates = paramArrayOfX509Certificate;
/* 188 */     this.crls = paramArrayOfX509CRL;
/* 189 */     this.signerInfos = paramArrayOfSignerInfo;
/*     */   }
/*     */ 
/*     */   public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, SignerInfo[] paramArrayOfSignerInfo)
/*     */   {
/* 196 */     this(paramArrayOfAlgorithmId, paramContentInfo, paramArrayOfX509Certificate, null, paramArrayOfSignerInfo);
/*     */   }
/*     */ 
/*     */   private void parseNetscapeCertChain(DerValue paramDerValue) throws ParsingException, IOException
/*     */   {
/* 201 */     DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
/* 202 */     DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
/* 203 */     this.certificates = new X509Certificate[arrayOfDerValue.length];
/*     */ 
/* 205 */     CertificateFactory localCertificateFactory = null;
/*     */     try {
/* 207 */       localCertificateFactory = CertificateFactory.getInstance("X.509");
/*     */     }
/*     */     catch (CertificateException localCertificateException1)
/*     */     {
/*     */     }
/* 212 */     for (int i = 0; i < arrayOfDerValue.length; i++) {
/* 213 */       ByteArrayInputStream localByteArrayInputStream = null;
/*     */       try {
/* 215 */         if (localCertificateFactory == null) {
/* 216 */           this.certificates[i] = new X509CertImpl(arrayOfDerValue[i]);
/*     */         } else {
/* 218 */           byte[] arrayOfByte = arrayOfDerValue[i].toByteArray();
/* 219 */           localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/* 220 */           this.certificates[i] = ((X509Certificate)localCertificateFactory.generateCertificate(localByteArrayInputStream));
/*     */ 
/* 222 */           localByteArrayInputStream.close();
/* 223 */           localByteArrayInputStream = null;
/*     */         }
/*     */       } catch (CertificateException localCertificateException2) {
/* 226 */         localParsingException = new ParsingException(localCertificateException2.getMessage());
/* 227 */         localParsingException.initCause(localCertificateException2);
/* 228 */         throw localParsingException;
/*     */       } catch (IOException localIOException) {
/* 230 */         ParsingException localParsingException = new ParsingException(localIOException.getMessage());
/* 231 */         localParsingException.initCause(localIOException);
/* 232 */         throw localParsingException;
/*     */       } finally {
/* 234 */         if (localByteArrayInputStream != null)
/* 235 */           localByteArrayInputStream.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parseSignedData(DerValue paramDerValue)
/*     */     throws ParsingException, IOException
/*     */   {
/* 243 */     DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
/*     */ 
/* 246 */     this.version = localDerInputStream.getBigInteger();
/*     */ 
/* 249 */     DerValue[] arrayOfDerValue1 = localDerInputStream.getSet(1);
/* 250 */     int i = arrayOfDerValue1.length;
/* 251 */     this.digestAlgorithmIds = new AlgorithmId[i];
/*     */     try {
/* 253 */       for (int j = 0; j < i; j++) {
/* 254 */         localObject1 = arrayOfDerValue1[j];
/* 255 */         this.digestAlgorithmIds[j] = AlgorithmId.parse((DerValue)localObject1);
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException1) {
/* 259 */       Object localObject1 = new ParsingException("Error parsing digest AlgorithmId IDs: " + localIOException1.getMessage());
/*     */ 
/* 262 */       ((ParsingException)localObject1).initCause(localIOException1);
/* 263 */       throw ((Throwable)localObject1);
/*     */     }
/*     */ 
/* 266 */     this.contentInfo = new ContentInfo(localDerInputStream);
/*     */ 
/* 268 */     CertificateFactory localCertificateFactory = null;
/*     */     try {
/* 270 */       localCertificateFactory = CertificateFactory.getInstance("X.509");
/*     */     }
/*     */     catch (CertificateException localCertificateException1)
/*     */     {
/*     */     }
/*     */     Object localObject2;
/*     */     ParsingException localParsingException;
/* 279 */     if ((byte)localDerInputStream.peekByte() == -96) {
/* 280 */       arrayOfDerValue2 = localDerInputStream.getSet(2, true);
/*     */ 
/* 282 */       i = arrayOfDerValue2.length;
/* 283 */       this.certificates = new X509Certificate[i];
/*     */ 
/* 285 */       for (k = 0; k < i; k++) {
/* 286 */         localObject2 = null;
/*     */         try {
/* 288 */           if (localCertificateFactory == null) {
/* 289 */             this.certificates[k] = new X509CertImpl(arrayOfDerValue2[k]);
/*     */           } else {
/* 291 */             byte[] arrayOfByte1 = arrayOfDerValue2[k].toByteArray();
/* 292 */             localObject2 = new ByteArrayInputStream(arrayOfByte1);
/* 293 */             this.certificates[k] = ((X509Certificate)localCertificateFactory.generateCertificate((InputStream)localObject2));
/*     */ 
/* 295 */             ((ByteArrayInputStream)localObject2).close();
/* 296 */             localObject2 = null;
/*     */           }
/*     */         } catch (CertificateException localCertificateException2) {
/* 299 */           localParsingException = new ParsingException(localCertificateException2.getMessage());
/* 300 */           localParsingException.initCause(localCertificateException2);
/* 301 */           throw localParsingException;
/*     */         } catch (IOException localIOException2) {
/* 303 */           localParsingException = new ParsingException(localIOException2.getMessage());
/* 304 */           localParsingException.initCause(localIOException2);
/* 305 */           throw localParsingException;
/*     */         } finally {
/* 307 */           if (localObject2 != null) {
/* 308 */             ((ByteArrayInputStream)localObject2).close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 314 */     if ((byte)localDerInputStream.peekByte() == -95) {
/* 315 */       arrayOfDerValue2 = localDerInputStream.getSet(1, true);
/*     */ 
/* 317 */       i = arrayOfDerValue2.length;
/* 318 */       this.crls = new X509CRL[i];
/*     */ 
/* 320 */       for (k = 0; k < i; k++) {
/* 321 */         localObject2 = null;
/*     */         try {
/* 323 */           if (localCertificateFactory == null) {
/* 324 */             this.crls[k] = new X509CRLImpl(arrayOfDerValue2[k]);
/*     */           } else {
/* 326 */             byte[] arrayOfByte2 = arrayOfDerValue2[k].toByteArray();
/* 327 */             localObject2 = new ByteArrayInputStream(arrayOfByte2);
/* 328 */             this.crls[k] = ((X509CRL)localCertificateFactory.generateCRL((InputStream)localObject2));
/* 329 */             ((ByteArrayInputStream)localObject2).close();
/* 330 */             localObject2 = null;
/*     */           }
/*     */         } catch (CRLException localCRLException) {
/* 333 */           localParsingException = new ParsingException(localCRLException.getMessage());
/*     */ 
/* 335 */           localParsingException.initCause(localCRLException);
/* 336 */           throw localParsingException;
/*     */         } finally {
/* 338 */           if (localObject2 != null) {
/* 339 */             ((ByteArrayInputStream)localObject2).close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 345 */     DerValue[] arrayOfDerValue2 = localDerInputStream.getSet(1);
/*     */ 
/* 347 */     i = arrayOfDerValue2.length;
/* 348 */     this.signerInfos = new SignerInfo[i];
/*     */ 
/* 350 */     for (int k = 0; k < i; k++) {
/* 351 */       localObject2 = arrayOfDerValue2[k].toDerInputStream();
/* 352 */       this.signerInfos[k] = new SignerInfo((DerInputStream)localObject2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parseOldSignedData(DerValue paramDerValue)
/*     */     throws ParsingException, IOException
/*     */   {
/* 363 */     DerInputStream localDerInputStream1 = paramDerValue.toDerInputStream();
/*     */ 
/* 366 */     this.version = localDerInputStream1.getBigInteger();
/*     */ 
/* 369 */     DerValue[] arrayOfDerValue1 = localDerInputStream1.getSet(1);
/* 370 */     int i = arrayOfDerValue1.length;
/*     */ 
/* 372 */     this.digestAlgorithmIds = new AlgorithmId[i];
/*     */     try {
/* 374 */       for (int j = 0; j < i; j++) {
/* 375 */         DerValue localDerValue = arrayOfDerValue1[j];
/* 376 */         this.digestAlgorithmIds[j] = AlgorithmId.parse(localDerValue);
/*     */       }
/*     */     } catch (IOException localIOException1) {
/* 379 */       throw new ParsingException("Error parsing digest AlgorithmId IDs");
/*     */     }
/*     */ 
/* 383 */     this.contentInfo = new ContentInfo(localDerInputStream1, true);
/*     */ 
/* 386 */     CertificateFactory localCertificateFactory = null;
/*     */     try {
/* 388 */       localCertificateFactory = CertificateFactory.getInstance("X.509");
/*     */     }
/*     */     catch (CertificateException localCertificateException1) {
/*     */     }
/* 392 */     DerValue[] arrayOfDerValue2 = localDerInputStream1.getSet(2);
/* 393 */     i = arrayOfDerValue2.length;
/* 394 */     this.certificates = new X509Certificate[i];
/*     */ 
/* 396 */     for (int k = 0; k < i; k++) {
/* 397 */       ByteArrayInputStream localByteArrayInputStream = null;
/*     */       try {
/* 399 */         if (localCertificateFactory == null) {
/* 400 */           this.certificates[k] = new X509CertImpl(arrayOfDerValue2[k]);
/*     */         } else {
/* 402 */           byte[] arrayOfByte = arrayOfDerValue2[k].toByteArray();
/* 403 */           localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/* 404 */           this.certificates[k] = ((X509Certificate)localCertificateFactory.generateCertificate(localByteArrayInputStream));
/*     */ 
/* 406 */           localByteArrayInputStream.close();
/* 407 */           localByteArrayInputStream = null;
/*     */         }
/*     */       } catch (CertificateException localCertificateException2) {
/* 410 */         localParsingException = new ParsingException(localCertificateException2.getMessage());
/* 411 */         localParsingException.initCause(localCertificateException2);
/* 412 */         throw localParsingException;
/*     */       } catch (IOException localIOException2) {
/* 414 */         ParsingException localParsingException = new ParsingException(localIOException2.getMessage());
/* 415 */         localParsingException.initCause(localIOException2);
/* 416 */         throw localParsingException;
/*     */       } finally {
/* 418 */         if (localByteArrayInputStream != null) {
/* 419 */           localByteArrayInputStream.close();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 424 */     localDerInputStream1.getSet(0);
/*     */ 
/* 427 */     DerValue[] arrayOfDerValue3 = localDerInputStream1.getSet(1);
/* 428 */     i = arrayOfDerValue3.length;
/* 429 */     this.signerInfos = new SignerInfo[i];
/* 430 */     for (int m = 0; m < i; m++) {
/* 431 */       DerInputStream localDerInputStream2 = arrayOfDerValue3[m].toDerInputStream();
/* 432 */       this.signerInfos[m] = new SignerInfo(localDerInputStream2, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void encodeSignedData(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 443 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 444 */     encodeSignedData(localDerOutputStream);
/* 445 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */ 
/*     */   public void encodeSignedData(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 457 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */ 
/* 460 */     localDerOutputStream.putInteger(this.version);
/*     */ 
/* 463 */     localDerOutputStream.putOrderedSetOf((byte)49, this.digestAlgorithmIds);
/*     */ 
/* 466 */     this.contentInfo.encode(localDerOutputStream);
/*     */ 
/* 469 */     if ((this.certificates != null) && (this.certificates.length != 0))
/*     */     {
/* 471 */       localObject1 = new X509CertImpl[this.certificates.length];
/* 472 */       for (int i = 0; i < this.certificates.length; i++) {
/* 473 */         if ((this.certificates[i] instanceof X509CertImpl))
/* 474 */           localObject1[i] = ((X509CertImpl)this.certificates[i]);
/*     */         else {
/*     */           try {
/* 477 */             byte[] arrayOfByte1 = this.certificates[i].getEncoded();
/* 478 */             localObject1[i] = new X509CertImpl(arrayOfByte1);
/*     */           } catch (CertificateException localCertificateException) {
/* 480 */             IOException localIOException1 = new IOException(localCertificateException.getMessage());
/* 481 */             localIOException1.initCause(localCertificateException);
/* 482 */             throw localIOException1;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 489 */       localDerOutputStream.putOrderedSetOf((byte)-96, (DerEncoder[])localObject1);
/*     */     }
/*     */ 
/* 493 */     if ((this.crls != null) && (this.crls.length != 0))
/*     */     {
/* 495 */       localObject1 = new HashSet(this.crls.length);
/* 496 */       for (Object localObject3 : this.crls) {
/* 497 */         if ((localObject3 instanceof X509CRLImpl))
/* 498 */           ((Set)localObject1).add((X509CRLImpl)localObject3);
/*     */         else {
/*     */           try {
/* 501 */             byte[] arrayOfByte2 = localObject3.getEncoded();
/* 502 */             ((Set)localObject1).add(new X509CRLImpl(arrayOfByte2));
/*     */           } catch (CRLException localCRLException) {
/* 504 */             IOException localIOException2 = new IOException(localCRLException.getMessage());
/* 505 */             localIOException2.initCause(localCRLException);
/* 506 */             throw localIOException2;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 513 */       localDerOutputStream.putOrderedSetOf((byte)-95, (DerEncoder[])((Set)localObject1).toArray(new X509CRLImpl[((Set)localObject1).size()]));
/*     */     }
/*     */ 
/* 518 */     localDerOutputStream.putOrderedSetOf((byte)49, this.signerInfos);
/*     */ 
/* 521 */     Object localObject1 = new DerValue((byte)48, localDerOutputStream.toByteArray());
/*     */ 
/* 525 */     ??? = new ContentInfo(ContentInfo.SIGNED_DATA_OID, (DerValue)localObject1);
/*     */ 
/* 529 */     ((ContentInfo)???).encode(paramDerOutputStream);
/*     */   }
/*     */ 
/*     */   public SignerInfo verify(SignerInfo paramSignerInfo, byte[] paramArrayOfByte)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 543 */     return paramSignerInfo.verify(this, paramArrayOfByte);
/*     */   }
/*     */ 
/*     */   public SignerInfo[] verify(byte[] paramArrayOfByte)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 557 */     Vector localVector = new Vector();
/* 558 */     for (int i = 0; i < this.signerInfos.length; i++)
/*     */     {
/* 560 */       SignerInfo localSignerInfo = verify(this.signerInfos[i], paramArrayOfByte);
/* 561 */       if (localSignerInfo != null) {
/* 562 */         localVector.addElement(localSignerInfo);
/*     */       }
/*     */     }
/* 565 */     if (localVector.size() != 0)
/*     */     {
/* 567 */       SignerInfo[] arrayOfSignerInfo = new SignerInfo[localVector.size()];
/* 568 */       localVector.copyInto(arrayOfSignerInfo);
/* 569 */       return arrayOfSignerInfo;
/*     */     }
/* 571 */     return null;
/*     */   }
/*     */ 
/*     */   public SignerInfo[] verify()
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 582 */     return verify(null);
/*     */   }
/*     */ 
/*     */   public BigInteger getVersion()
/*     */   {
/* 591 */     return this.version;
/*     */   }
/*     */ 
/*     */   public AlgorithmId[] getDigestAlgorithmIds()
/*     */   {
/* 600 */     return this.digestAlgorithmIds;
/*     */   }
/*     */ 
/*     */   public ContentInfo getContentInfo()
/*     */   {
/* 607 */     return this.contentInfo;
/*     */   }
/*     */ 
/*     */   public X509Certificate[] getCertificates()
/*     */   {
/* 616 */     if (this.certificates != null) {
/* 617 */       return (X509Certificate[])this.certificates.clone();
/*     */     }
/* 619 */     return null;
/*     */   }
/*     */ 
/*     */   public X509CRL[] getCRLs()
/*     */   {
/* 628 */     if (this.crls != null) {
/* 629 */       return (X509CRL[])this.crls.clone();
/*     */     }
/* 631 */     return null;
/*     */   }
/*     */ 
/*     */   public SignerInfo[] getSignerInfos()
/*     */   {
/* 640 */     return this.signerInfos;
/*     */   }
/*     */ 
/*     */   public X509Certificate getCertificate(BigInteger paramBigInteger, X500Name paramX500Name)
/*     */   {
/* 652 */     if (this.certificates != null) {
/* 653 */       if (this.certIssuerNames == null)
/* 654 */         populateCertIssuerNames();
/* 655 */       for (int i = 0; i < this.certificates.length; i++) {
/* 656 */         X509Certificate localX509Certificate = this.certificates[i];
/* 657 */         BigInteger localBigInteger = localX509Certificate.getSerialNumber();
/* 658 */         if ((paramBigInteger.equals(localBigInteger)) && (paramX500Name.equals(this.certIssuerNames[i])))
/*     */         {
/* 661 */           return localX509Certificate;
/*     */         }
/*     */       }
/*     */     }
/* 665 */     return null;
/*     */   }
/*     */ 
/*     */   private void populateCertIssuerNames()
/*     */   {
/* 673 */     if (this.certificates == null) {
/* 674 */       return;
/*     */     }
/* 676 */     this.certIssuerNames = new Principal[this.certificates.length];
/* 677 */     for (int i = 0; i < this.certificates.length; i++) {
/* 678 */       X509Certificate localX509Certificate = this.certificates[i];
/* 679 */       Principal localPrincipal = localX509Certificate.getIssuerDN();
/* 680 */       if (!(localPrincipal instanceof X500Name))
/*     */       {
/*     */         try
/*     */         {
/* 686 */           X509CertInfo localX509CertInfo = new X509CertInfo(localX509Certificate.getTBSCertificate());
/*     */ 
/* 688 */           localPrincipal = (Principal)localX509CertInfo.get("issuer.dname");
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 696 */       this.certIssuerNames[i] = localPrincipal;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 704 */     String str = "";
/*     */ 
/* 706 */     str = str + this.contentInfo + "\n";
/* 707 */     if (this.version != null)
/* 708 */       str = str + "PKCS7 :: version: " + Debug.toHexString(this.version) + "\n";
/*     */     int i;
/* 709 */     if (this.digestAlgorithmIds != null) {
/* 710 */       str = str + "PKCS7 :: digest AlgorithmIds: \n";
/* 711 */       for (i = 0; i < this.digestAlgorithmIds.length; i++)
/* 712 */         str = str + "\t" + this.digestAlgorithmIds[i] + "\n";
/*     */     }
/* 714 */     if (this.certificates != null) {
/* 715 */       str = str + "PKCS7 :: certificates: \n";
/* 716 */       for (i = 0; i < this.certificates.length; i++)
/* 717 */         str = str + "\t" + i + ".   " + this.certificates[i] + "\n";
/*     */     }
/* 719 */     if (this.crls != null) {
/* 720 */       str = str + "PKCS7 :: crls: \n";
/* 721 */       for (i = 0; i < this.crls.length; i++)
/* 722 */         str = str + "\t" + i + ".   " + this.crls[i] + "\n";
/*     */     }
/* 724 */     if (this.signerInfos != null) {
/* 725 */       str = str + "PKCS7 :: signer infos: \n";
/* 726 */       for (i = 0; i < this.signerInfos.length; i++)
/* 727 */         str = str + "\t" + i + ".  " + this.signerInfos[i] + "\n";
/*     */     }
/* 729 */     return str;
/*     */   }
/*     */ 
/*     */   public boolean isOldStyle()
/*     */   {
/* 737 */     return this.oldStyle;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.pkcs.PKCS7
 * JD-Core Version:    0.6.2
 */