/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Principal;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.KeyUsageExtension;
/*     */ import sun.security.x509.X500Name;
/*     */ 
/*     */ public class SignerInfo
/*     */   implements DerEncoder
/*     */ {
/*     */   BigInteger version;
/*     */   X500Name issuerName;
/*     */   BigInteger certificateSerialNumber;
/*     */   AlgorithmId digestAlgorithmId;
/*     */   AlgorithmId digestEncryptionAlgorithmId;
/*     */   byte[] encryptedDigest;
/*     */   PKCS9Attributes authenticatedAttributes;
/*     */   PKCS9Attributes unauthenticatedAttributes;
/*     */ 
/*     */   public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte)
/*     */   {
/*  64 */     this.version = BigInteger.ONE;
/*  65 */     this.issuerName = paramX500Name;
/*  66 */     this.certificateSerialNumber = paramBigInteger;
/*  67 */     this.digestAlgorithmId = paramAlgorithmId1;
/*  68 */     this.digestEncryptionAlgorithmId = paramAlgorithmId2;
/*  69 */     this.encryptedDigest = paramArrayOfByte;
/*     */   }
/*     */ 
/*     */   public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, PKCS9Attributes paramPKCS9Attributes1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte, PKCS9Attributes paramPKCS9Attributes2)
/*     */   {
/*  79 */     this.version = BigInteger.ONE;
/*  80 */     this.issuerName = paramX500Name;
/*  81 */     this.certificateSerialNumber = paramBigInteger;
/*  82 */     this.digestAlgorithmId = paramAlgorithmId1;
/*  83 */     this.authenticatedAttributes = paramPKCS9Attributes1;
/*  84 */     this.digestEncryptionAlgorithmId = paramAlgorithmId2;
/*  85 */     this.encryptedDigest = paramArrayOfByte;
/*  86 */     this.unauthenticatedAttributes = paramPKCS9Attributes2;
/*     */   }
/*     */ 
/*     */   public SignerInfo(DerInputStream paramDerInputStream)
/*     */     throws IOException, ParsingException
/*     */   {
/*  95 */     this(paramDerInputStream, false);
/*     */   }
/*     */ 
/*     */   public SignerInfo(DerInputStream paramDerInputStream, boolean paramBoolean)
/*     */     throws IOException, ParsingException
/*     */   {
/* 112 */     this.version = paramDerInputStream.getBigInteger();
/*     */ 
/* 115 */     DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
/* 116 */     byte[] arrayOfByte = arrayOfDerValue[0].toByteArray();
/* 117 */     this.issuerName = new X500Name(new DerValue((byte)48, arrayOfByte));
/*     */ 
/* 119 */     this.certificateSerialNumber = arrayOfDerValue[1].getBigInteger();
/*     */ 
/* 122 */     DerValue localDerValue = paramDerInputStream.getDerValue();
/*     */ 
/* 124 */     this.digestAlgorithmId = AlgorithmId.parse(localDerValue);
/*     */ 
/* 127 */     if (paramBoolean)
/*     */     {
/* 130 */       paramDerInputStream.getSet(0);
/*     */     }
/* 134 */     else if ((byte)paramDerInputStream.peekByte() == -96) {
/* 135 */       this.authenticatedAttributes = new PKCS9Attributes(paramDerInputStream);
/*     */     }
/*     */ 
/* 141 */     localDerValue = paramDerInputStream.getDerValue();
/*     */ 
/* 143 */     this.digestEncryptionAlgorithmId = AlgorithmId.parse(localDerValue);
/*     */ 
/* 146 */     this.encryptedDigest = paramDerInputStream.getOctetString();
/*     */ 
/* 149 */     if (paramBoolean)
/*     */     {
/* 152 */       paramDerInputStream.getSet(0);
/*     */     }
/* 156 */     else if ((paramDerInputStream.available() != 0) && ((byte)paramDerInputStream.peekByte() == -95))
/*     */     {
/* 158 */       this.unauthenticatedAttributes = new PKCS9Attributes(paramDerInputStream, true);
/*     */     }
/*     */ 
/* 164 */     if (paramDerInputStream.available() != 0)
/* 165 */       throw new ParsingException("extra data at the end");
/*     */   }
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 171 */     derEncode(paramDerOutputStream);
/*     */   }
/*     */ 
/*     */   public void derEncode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 184 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 185 */     localDerOutputStream1.putInteger(this.version);
/* 186 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 187 */     this.issuerName.encode(localDerOutputStream2);
/* 188 */     localDerOutputStream2.putInteger(this.certificateSerialNumber);
/* 189 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*     */ 
/* 191 */     this.digestAlgorithmId.encode(localDerOutputStream1);
/*     */ 
/* 194 */     if (this.authenticatedAttributes != null) {
/* 195 */       this.authenticatedAttributes.encode((byte)-96, localDerOutputStream1);
/*     */     }
/* 197 */     this.digestEncryptionAlgorithmId.encode(localDerOutputStream1);
/*     */ 
/* 199 */     localDerOutputStream1.putOctetString(this.encryptedDigest);
/*     */ 
/* 202 */     if (this.unauthenticatedAttributes != null) {
/* 203 */       this.unauthenticatedAttributes.encode((byte)-95, localDerOutputStream1);
/*     */     }
/* 205 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 206 */     localDerOutputStream3.write((byte)48, localDerOutputStream1);
/*     */ 
/* 208 */     paramOutputStream.write(localDerOutputStream3.toByteArray());
/*     */   }
/*     */ 
/*     */   public X509Certificate getCertificate(PKCS7 paramPKCS7)
/*     */     throws IOException
/*     */   {
/* 219 */     return paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName);
/*     */   }
/*     */ 
/*     */   public ArrayList<X509Certificate> getCertificateChain(PKCS7 paramPKCS7)
/*     */     throws IOException
/*     */   {
/* 229 */     X509Certificate localX509Certificate1 = paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName);
/* 230 */     if (localX509Certificate1 == null) {
/* 231 */       return null;
/*     */     }
/* 233 */     ArrayList localArrayList = new ArrayList();
/* 234 */     localArrayList.add(localX509Certificate1);
/*     */ 
/* 236 */     X509Certificate[] arrayOfX509Certificate = paramPKCS7.getCertificates();
/* 237 */     if ((arrayOfX509Certificate == null) || (localX509Certificate1.getSubjectDN().equals(localX509Certificate1.getIssuerDN())))
/*     */     {
/* 239 */       return localArrayList;
/*     */     }
/*     */ 
/* 242 */     Principal localPrincipal = localX509Certificate1.getIssuerDN();
/* 243 */     int i = 0;
/*     */     while (true) {
/* 245 */       int j = 0;
/* 246 */       int k = i;
/* 247 */       while (k < arrayOfX509Certificate.length) {
/* 248 */         if (localPrincipal.equals(arrayOfX509Certificate[k].getSubjectDN()))
/*     */         {
/* 250 */           localArrayList.add(arrayOfX509Certificate[k]);
/*     */ 
/* 253 */           if (arrayOfX509Certificate[k].getSubjectDN().equals(arrayOfX509Certificate[k].getIssuerDN()))
/*     */           {
/* 255 */             i = arrayOfX509Certificate.length;
/*     */           } else {
/* 257 */             localPrincipal = arrayOfX509Certificate[k].getIssuerDN();
/* 258 */             X509Certificate localX509Certificate2 = arrayOfX509Certificate[i];
/* 259 */             arrayOfX509Certificate[i] = arrayOfX509Certificate[k];
/* 260 */             arrayOfX509Certificate[k] = localX509Certificate2;
/* 261 */             i++;
/*     */           }
/* 263 */           j = 1;
/* 264 */           break;
/*     */         }
/* 266 */         k++;
/*     */       }
/*     */ 
/* 269 */       if (j == 0) {
/*     */         break;
/*     */       }
/*     */     }
/* 273 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   private static String convertToStandardName(String paramString)
/*     */   {
/* 278 */     if (paramString.equals("SHA"))
/* 279 */       return "SHA-1";
/* 280 */     if (paramString.equals("SHA224"))
/* 281 */       return "SHA-224";
/* 282 */     if (paramString.equals("SHA256"))
/* 283 */       return "SHA-256";
/* 284 */     if (paramString.equals("SHA384"))
/* 285 */       return "SHA-384";
/* 286 */     if (paramString.equals("SHA512")) {
/* 287 */       return "SHA-512";
/*     */     }
/* 289 */     return paramString;
/*     */   }
/*     */ 
/*     */   SignerInfo verify(PKCS7 paramPKCS7, byte[] paramArrayOfByte)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/*     */     try
/*     */     {
/* 301 */       ContentInfo localContentInfo = paramPKCS7.getContentInfo();
/* 302 */       if (paramArrayOfByte == null) {
/* 303 */         paramArrayOfByte = localContentInfo.getContentBytes();
/*     */       }
/*     */ 
/* 306 */       String str = getDigestAlgorithmId().getName();
/*     */       byte[] arrayOfByte;
/* 312 */       if (this.authenticatedAttributes == null) {
/* 313 */         arrayOfByte = paramArrayOfByte;
/*     */       }
/*     */       else
/*     */       {
/* 317 */         localObject1 = (ObjectIdentifier)this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.CONTENT_TYPE_OID);
/*     */ 
/* 320 */         if ((localObject1 == null) || (!((ObjectIdentifier)localObject1).equals(localContentInfo.contentType)))
/*     */         {
/* 322 */           return null;
/*     */         }
/*     */ 
/* 325 */         localObject2 = (byte[])this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.MESSAGE_DIGEST_OID);
/*     */ 
/* 329 */         if (localObject2 == null) {
/* 330 */           return null;
/*     */         }
/* 332 */         localObject3 = MessageDigest.getInstance(convertToStandardName(str));
/*     */ 
/* 334 */         localObject4 = ((MessageDigest)localObject3).digest(paramArrayOfByte);
/*     */ 
/* 336 */         if (localObject2.length != localObject4.length)
/* 337 */           return null;
/* 338 */         for (int i = 0; i < localObject2.length; i++) {
/* 339 */           if (localObject2[i] != localObject4[i]) {
/* 340 */             return null;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 349 */         arrayOfByte = this.authenticatedAttributes.getDerEncoding();
/*     */       }
/*     */ 
/* 354 */       Object localObject1 = getDigestEncryptionAlgorithmId().getName();
/*     */ 
/* 359 */       Object localObject2 = AlgorithmId.getEncAlgFromSigAlg((String)localObject1);
/* 360 */       if (localObject2 != null) localObject1 = localObject2;
/* 361 */       Object localObject3 = AlgorithmId.makeSigAlg(str, (String)localObject1);
/*     */ 
/* 364 */       Object localObject4 = Signature.getInstance((String)localObject3);
/* 365 */       X509Certificate localX509Certificate = getCertificate(paramPKCS7);
/*     */ 
/* 367 */       if (localX509Certificate == null) {
/* 368 */         return null;
/*     */       }
/* 370 */       if (localX509Certificate.hasUnsupportedCriticalExtension()) {
/* 371 */         throw new SignatureException("Certificate has unsupported critical extension(s)");
/*     */       }
/*     */ 
/* 379 */       boolean[] arrayOfBoolean = localX509Certificate.getKeyUsage();
/* 380 */       if (arrayOfBoolean != null)
/*     */       {
/*     */         try
/*     */         {
/* 387 */           localObject5 = new KeyUsageExtension(arrayOfBoolean);
/*     */         } catch (IOException localIOException2) {
/* 389 */           throw new SignatureException("Failed to parse keyUsage extension");
/*     */         }
/*     */ 
/* 393 */         boolean bool1 = ((Boolean)((KeyUsageExtension)localObject5).get("digital_signature")).booleanValue();
/*     */ 
/* 396 */         boolean bool2 = ((Boolean)((KeyUsageExtension)localObject5).get("non_repudiation")).booleanValue();
/*     */ 
/* 399 */         if ((!bool1) && (!bool2)) {
/* 400 */           throw new SignatureException("Key usage restricted: cannot be used for digital signatures");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 406 */       Object localObject5 = localX509Certificate.getPublicKey();
/* 407 */       ((Signature)localObject4).initVerify((PublicKey)localObject5);
/*     */ 
/* 409 */       ((Signature)localObject4).update(arrayOfByte);
/*     */ 
/* 411 */       if (((Signature)localObject4).verify(this.encryptedDigest))
/* 412 */         return this;
/*     */     }
/*     */     catch (IOException localIOException1)
/*     */     {
/* 416 */       throw new SignatureException("IO error verifying signature:\n" + localIOException1.getMessage());
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException)
/*     */     {
/* 420 */       throw new SignatureException("InvalidKey: " + localInvalidKeyException.getMessage());
/*     */     }
/*     */ 
/* 423 */     return null;
/*     */   }
/*     */ 
/*     */   SignerInfo verify(PKCS7 paramPKCS7)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 429 */     return verify(paramPKCS7, null);
/*     */   }
/*     */ 
/*     */   public BigInteger getVersion()
/*     */   {
/* 434 */     return this.version;
/*     */   }
/*     */ 
/*     */   public X500Name getIssuerName() {
/* 438 */     return this.issuerName;
/*     */   }
/*     */ 
/*     */   public BigInteger getCertificateSerialNumber() {
/* 442 */     return this.certificateSerialNumber;
/*     */   }
/*     */ 
/*     */   public AlgorithmId getDigestAlgorithmId() {
/* 446 */     return this.digestAlgorithmId;
/*     */   }
/*     */ 
/*     */   public PKCS9Attributes getAuthenticatedAttributes() {
/* 450 */     return this.authenticatedAttributes;
/*     */   }
/*     */ 
/*     */   public AlgorithmId getDigestEncryptionAlgorithmId() {
/* 454 */     return this.digestEncryptionAlgorithmId;
/*     */   }
/*     */ 
/*     */   public byte[] getEncryptedDigest() {
/* 458 */     return this.encryptedDigest;
/*     */   }
/*     */ 
/*     */   public PKCS9Attributes getUnauthenticatedAttributes() {
/* 462 */     return this.unauthenticatedAttributes;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 466 */     HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*     */ 
/* 468 */     String str = "";
/*     */ 
/* 470 */     str = str + "Signer Info for (issuer): " + this.issuerName + "\n";
/* 471 */     str = str + "\tversion: " + Debug.toHexString(this.version) + "\n";
/* 472 */     str = str + "\tcertificateSerialNumber: " + Debug.toHexString(this.certificateSerialNumber) + "\n";
/*     */ 
/* 474 */     str = str + "\tdigestAlgorithmId: " + this.digestAlgorithmId + "\n";
/* 475 */     if (this.authenticatedAttributes != null) {
/* 476 */       str = str + "\tauthenticatedAttributes: " + this.authenticatedAttributes + "\n";
/*     */     }
/*     */ 
/* 479 */     str = str + "\tdigestEncryptionAlgorithmId: " + this.digestEncryptionAlgorithmId + "\n";
/*     */ 
/* 482 */     str = str + "\tencryptedDigest: \n" + localHexDumpEncoder.encodeBuffer(this.encryptedDigest) + "\n";
/*     */ 
/* 484 */     if (this.unauthenticatedAttributes != null) {
/* 485 */       str = str + "\tunauthenticatedAttributes: " + this.unauthenticatedAttributes + "\n";
/*     */     }
/*     */ 
/* 488 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.pkcs.SignerInfo
 * JD-Core Version:    0.6.2
 */