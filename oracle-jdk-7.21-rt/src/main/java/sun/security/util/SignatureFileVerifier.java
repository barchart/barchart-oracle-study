/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.CodeSigner;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SignatureException;
/*     */ import java.security.Timestamp;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarException;
/*     */ import java.util.jar.Manifest;
/*     */ import sun.misc.BASE64Decoder;
/*     */ import sun.security.jca.Providers;
/*     */ import sun.security.pkcs.ContentInfo;
/*     */ import sun.security.pkcs.PKCS7;
/*     */ import sun.security.pkcs.PKCS9Attribute;
/*     */ import sun.security.pkcs.PKCS9Attributes;
/*     */ import sun.security.pkcs.SignerInfo;
/*     */ import sun.security.timestamp.TimestampToken;
/*     */ 
/*     */ public class SignatureFileVerifier
/*     */ {
/*  46 */   private static final Debug debug = Debug.getInstance("jar");
/*     */   private ArrayList<CodeSigner[]> signerCache;
/*  51 */   private static final String ATTR_DIGEST = "-DIGEST-Manifest-Main-Attributes".toUpperCase(Locale.ENGLISH);
/*     */   private PKCS7 block;
/*     */   private byte[] sfBytes;
/*     */   private String name;
/*     */   private ManifestDigester md;
/*     */   private HashMap<String, MessageDigest> createdDigests;
/*  73 */   private boolean workaround = false;
/*     */ 
/*  76 */   private CertificateFactory certificateFactory = null;
/*     */ 
/* 560 */   private static final char[] hexc = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */   public SignatureFileVerifier(ArrayList<CodeSigner[]> paramArrayList, ManifestDigester paramManifestDigester, String paramString, byte[] paramArrayOfByte)
/*     */     throws IOException, CertificateException
/*     */   {
/*  93 */     Object localObject1 = null;
/*     */     try {
/*  95 */       localObject1 = Providers.startJarVerification();
/*  96 */       this.block = new PKCS7(paramArrayOfByte);
/*  97 */       this.sfBytes = this.block.getContentInfo().getData();
/*  98 */       this.certificateFactory = CertificateFactory.getInstance("X509");
/*     */     } finally {
/* 100 */       Providers.stopJarVerification(localObject1);
/*     */     }
/* 102 */     this.name = paramString.substring(0, paramString.lastIndexOf(".")).toUpperCase(Locale.ENGLISH);
/*     */ 
/* 104 */     this.md = paramManifestDigester;
/* 105 */     this.signerCache = paramArrayList;
/*     */   }
/*     */ 
/*     */   public boolean needSignatureFileBytes()
/*     */   {
/* 114 */     return this.sfBytes == null;
/*     */   }
/*     */ 
/*     */   public boolean needSignatureFile(String paramString)
/*     */   {
/* 126 */     return this.name.equalsIgnoreCase(paramString);
/*     */   }
/*     */ 
/*     */   public void setSignatureFile(byte[] paramArrayOfByte)
/*     */   {
/* 135 */     this.sfBytes = paramArrayOfByte;
/*     */   }
/*     */ 
/*     */   public static boolean isBlockOrSF(String paramString)
/*     */   {
/* 149 */     if ((paramString.endsWith(".SF")) || (paramString.endsWith(".DSA")) || (paramString.endsWith(".RSA")) || (paramString.endsWith(".EC")))
/*     */     {
/* 151 */       return true;
/*     */     }
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */   private MessageDigest getDigest(String paramString)
/*     */   {
/* 160 */     if (this.createdDigests == null) {
/* 161 */       this.createdDigests = new HashMap();
/*     */     }
/* 163 */     MessageDigest localMessageDigest = (MessageDigest)this.createdDigests.get(paramString);
/*     */ 
/* 165 */     if (localMessageDigest == null)
/*     */       try {
/* 167 */         localMessageDigest = MessageDigest.getInstance(paramString);
/* 168 */         this.createdDigests.put(paramString, localMessageDigest);
/*     */       }
/*     */       catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */       {
/*     */       }
/* 173 */     return localMessageDigest;
/*     */   }
/*     */ 
/*     */   public void process(Hashtable<String, CodeSigner[]> paramHashtable, List paramList)
/*     */     throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException
/*     */   {
/* 190 */     Object localObject1 = null;
/*     */     try {
/* 192 */       localObject1 = Providers.startJarVerification();
/* 193 */       processImpl(paramHashtable, paramList);
/*     */     } finally {
/* 195 */       Providers.stopJarVerification(localObject1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processImpl(Hashtable<String, CodeSigner[]> paramHashtable, List paramList)
/*     */     throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException
/*     */   {
/* 205 */     Manifest localManifest = new Manifest();
/* 206 */     localManifest.read(new ByteArrayInputStream(this.sfBytes));
/*     */ 
/* 208 */     String str1 = localManifest.getMainAttributes().getValue(Attributes.Name.SIGNATURE_VERSION);
/*     */ 
/* 211 */     if ((str1 == null) || (!str1.equalsIgnoreCase("1.0")))
/*     */     {
/* 214 */       return;
/*     */     }
/*     */ 
/* 217 */     SignerInfo[] arrayOfSignerInfo = this.block.verify(this.sfBytes);
/*     */ 
/* 219 */     if (arrayOfSignerInfo == null) {
/* 220 */       throw new SecurityException("cannot verify signature block file " + this.name);
/*     */     }
/*     */ 
/* 224 */     BASE64Decoder localBASE64Decoder = new BASE64Decoder();
/*     */ 
/* 226 */     CodeSigner[] arrayOfCodeSigner = getSigners(arrayOfSignerInfo, this.block);
/*     */ 
/* 229 */     if (arrayOfCodeSigner == null) {
/* 230 */       return;
/*     */     }
/* 232 */     Iterator localIterator = localManifest.getEntries().entrySet().iterator();
/*     */ 
/* 236 */     boolean bool = verifyManifestHash(localManifest, this.md, localBASE64Decoder, paramList);
/*     */ 
/* 239 */     if ((!bool) && (!verifyManifestMainAttrs(localManifest, this.md, localBASE64Decoder))) {
/* 240 */       throw new SecurityException("Invalid signature file digest for Manifest main attributes");
/*     */     }
/*     */ 
/* 245 */     while (localIterator.hasNext())
/*     */     {
/* 247 */       Map.Entry localEntry = (Map.Entry)localIterator.next();
/* 248 */       String str2 = (String)localEntry.getKey();
/*     */ 
/* 250 */       if ((bool) || (verifySection((Attributes)localEntry.getValue(), str2, this.md, localBASE64Decoder)))
/*     */       {
/* 253 */         if (str2.startsWith("./")) {
/* 254 */           str2 = str2.substring(2);
/*     */         }
/* 256 */         if (str2.startsWith("/")) {
/* 257 */           str2 = str2.substring(1);
/*     */         }
/* 259 */         updateSigners(arrayOfCodeSigner, paramHashtable, str2);
/*     */ 
/* 261 */         if (debug != null) {
/* 262 */           debug.println("processSignature signed name = " + str2);
/*     */         }
/*     */       }
/* 265 */       else if (debug != null) {
/* 266 */         debug.println("processSignature unsigned name = " + str2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 271 */     updateSigners(arrayOfCodeSigner, paramHashtable, "META-INF/MANIFEST.MF");
/*     */   }
/*     */ 
/*     */   private boolean verifyManifestHash(Manifest paramManifest, ManifestDigester paramManifestDigester, BASE64Decoder paramBASE64Decoder, List paramList)
/*     */     throws IOException
/*     */   {
/* 283 */     Attributes localAttributes = paramManifest.getMainAttributes();
/* 284 */     boolean bool = false;
/*     */ 
/* 287 */     for (Map.Entry localEntry : localAttributes.entrySet())
/*     */     {
/* 289 */       String str1 = localEntry.getKey().toString();
/*     */ 
/* 291 */       if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST"))
/*     */       {
/* 293 */         String str2 = str1.substring(0, str1.length() - 16);
/*     */ 
/* 295 */         paramList.add(str1);
/* 296 */         paramList.add(localEntry.getValue());
/* 297 */         MessageDigest localMessageDigest = getDigest(str2);
/* 298 */         if (localMessageDigest != null) {
/* 299 */           byte[] arrayOfByte1 = paramManifestDigester.manifestDigest(localMessageDigest);
/* 300 */           byte[] arrayOfByte2 = paramBASE64Decoder.decodeBuffer((String)localEntry.getValue());
/*     */ 
/* 303 */           if (debug != null) {
/* 304 */             debug.println("Signature File: Manifest digest " + localMessageDigest.getAlgorithm());
/*     */ 
/* 306 */             debug.println("  sigfile  " + toHex(arrayOfByte2));
/* 307 */             debug.println("  computed " + toHex(arrayOfByte1));
/* 308 */             debug.println();
/*     */           }
/*     */ 
/* 311 */           if (MessageDigest.isEqual(arrayOfByte1, arrayOfByte2))
/*     */           {
/* 313 */             bool = true;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 320 */     return bool;
/*     */   }
/*     */ 
/*     */   private boolean verifyManifestMainAttrs(Manifest paramManifest, ManifestDigester paramManifestDigester, BASE64Decoder paramBASE64Decoder)
/*     */     throws IOException
/*     */   {
/* 328 */     Attributes localAttributes = paramManifest.getMainAttributes();
/* 329 */     boolean bool = true;
/*     */ 
/* 333 */     for (Map.Entry localEntry : localAttributes.entrySet()) {
/* 334 */       String str1 = localEntry.getKey().toString();
/*     */ 
/* 336 */       if (str1.toUpperCase(Locale.ENGLISH).endsWith(ATTR_DIGEST)) {
/* 337 */         String str2 = str1.substring(0, str1.length() - ATTR_DIGEST.length());
/*     */ 
/* 340 */         MessageDigest localMessageDigest = getDigest(str2);
/* 341 */         if (localMessageDigest != null) {
/* 342 */           ManifestDigester.Entry localEntry1 = paramManifestDigester.get("Manifest-Main-Attributes", false);
/*     */ 
/* 344 */           byte[] arrayOfByte1 = localEntry1.digest(localMessageDigest);
/* 345 */           byte[] arrayOfByte2 = paramBASE64Decoder.decodeBuffer((String)localEntry.getValue());
/*     */ 
/* 348 */           if (debug != null) {
/* 349 */             debug.println("Signature File: Manifest Main Attributes digest " + localMessageDigest.getAlgorithm());
/*     */ 
/* 352 */             debug.println("  sigfile  " + toHex(arrayOfByte2));
/* 353 */             debug.println("  computed " + toHex(arrayOfByte1));
/* 354 */             debug.println();
/*     */           }
/*     */ 
/* 357 */           if (!MessageDigest.isEqual(arrayOfByte1, arrayOfByte2))
/*     */           {
/* 362 */             bool = false;
/* 363 */             if (debug == null) break;
/* 364 */             debug.println("Verification of Manifest main attributes failed");
/*     */ 
/* 366 */             debug.println(); break;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 377 */     return bool;
/*     */   }
/*     */ 
/*     */   private boolean verifySection(Attributes paramAttributes, String paramString, ManifestDigester paramManifestDigester, BASE64Decoder paramBASE64Decoder)
/*     */     throws IOException
/*     */   {
/* 395 */     boolean bool = false;
/* 396 */     ManifestDigester.Entry localEntry = paramManifestDigester.get(paramString, this.block.isOldStyle());
/*     */ 
/* 398 */     if (localEntry == null) {
/* 399 */       throw new SecurityException("no manifiest section for signature file entry " + paramString);
/*     */     }
/*     */ 
/* 403 */     if (paramAttributes != null)
/*     */     {
/* 409 */       for (Map.Entry localEntry1 : paramAttributes.entrySet()) {
/* 410 */         String str1 = localEntry1.getKey().toString();
/*     */ 
/* 412 */         if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST"))
/*     */         {
/* 414 */           String str2 = str1.substring(0, str1.length() - 7);
/*     */ 
/* 416 */           MessageDigest localMessageDigest = getDigest(str2);
/*     */ 
/* 418 */           if (localMessageDigest != null) {
/* 419 */             int i = 0;
/*     */ 
/* 421 */             byte[] arrayOfByte1 = paramBASE64Decoder.decodeBuffer((String)localEntry1.getValue());
/*     */             byte[] arrayOfByte2;
/* 424 */             if (this.workaround)
/* 425 */               arrayOfByte2 = localEntry.digestWorkaround(localMessageDigest);
/*     */             else {
/* 427 */               arrayOfByte2 = localEntry.digest(localMessageDigest);
/*     */             }
/*     */ 
/* 430 */             if (debug != null) {
/* 431 */               debug.println("Signature Block File: " + paramString + " digest=" + localMessageDigest.getAlgorithm());
/*     */ 
/* 433 */               debug.println("  expected " + toHex(arrayOfByte1));
/* 434 */               debug.println("  computed " + toHex(arrayOfByte2));
/* 435 */               debug.println();
/*     */             }
/*     */ 
/* 438 */             if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
/* 439 */               bool = true;
/* 440 */               i = 1;
/*     */             }
/* 443 */             else if (!this.workaround) {
/* 444 */               arrayOfByte2 = localEntry.digestWorkaround(localMessageDigest);
/* 445 */               if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
/* 446 */                 if (debug != null) {
/* 447 */                   debug.println("  re-computed " + toHex(arrayOfByte2));
/* 448 */                   debug.println();
/*     */                 }
/* 450 */                 this.workaround = true;
/* 451 */                 bool = true;
/* 452 */                 i = 1;
/*     */               }
/*     */             }
/*     */ 
/* 456 */             if (i == 0) {
/* 457 */               throw new SecurityException("invalid " + localMessageDigest.getAlgorithm() + " signature file digest for " + paramString);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 465 */     return bool;
/*     */   }
/*     */ 
/*     */   private CodeSigner[] getSigners(SignerInfo[] paramArrayOfSignerInfo, PKCS7 paramPKCS7)
/*     */     throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException
/*     */   {
/* 477 */     ArrayList localArrayList1 = null;
/*     */ 
/* 479 */     for (int i = 0; i < paramArrayOfSignerInfo.length; i++)
/*     */     {
/* 481 */       SignerInfo localSignerInfo = paramArrayOfSignerInfo[i];
/* 482 */       ArrayList localArrayList2 = localSignerInfo.getCertificateChain(paramPKCS7);
/* 483 */       CertPath localCertPath = this.certificateFactory.generateCertPath(localArrayList2);
/* 484 */       if (localArrayList1 == null) {
/* 485 */         localArrayList1 = new ArrayList();
/*     */       }
/*     */ 
/* 488 */       localArrayList1.add(new CodeSigner(localCertPath, getTimestamp(localSignerInfo)));
/*     */ 
/* 490 */       if (debug != null) {
/* 491 */         debug.println("Signature Block Certificate: " + localArrayList2.get(0));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 496 */     if (localArrayList1 != null) {
/* 497 */       return (CodeSigner[])localArrayList1.toArray(new CodeSigner[localArrayList1.size()]);
/*     */     }
/* 499 */     return null;
/*     */   }
/*     */ 
/*     */   private Timestamp getTimestamp(SignerInfo paramSignerInfo)
/*     */     throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException
/*     */   {
/* 527 */     Timestamp localTimestamp = null;
/*     */ 
/* 530 */     PKCS9Attributes localPKCS9Attributes = paramSignerInfo.getUnauthenticatedAttributes();
/* 531 */     if (localPKCS9Attributes != null) {
/* 532 */       PKCS9Attribute localPKCS9Attribute = localPKCS9Attributes.getAttribute("signatureTimestampToken");
/*     */ 
/* 534 */       if (localPKCS9Attribute != null) {
/* 535 */         PKCS7 localPKCS7 = new PKCS7((byte[])localPKCS9Attribute.getValue());
/*     */ 
/* 538 */         byte[] arrayOfByte = localPKCS7.getContentInfo().getData();
/*     */ 
/* 542 */         SignerInfo[] arrayOfSignerInfo = localPKCS7.verify(arrayOfByte);
/*     */ 
/* 545 */         ArrayList localArrayList = arrayOfSignerInfo[0].getCertificateChain(localPKCS7);
/*     */ 
/* 547 */         CertPath localCertPath = this.certificateFactory.generateCertPath(localArrayList);
/*     */ 
/* 549 */         TimestampToken localTimestampToken = new TimestampToken(arrayOfByte);
/*     */ 
/* 552 */         localTimestamp = new Timestamp(localTimestampToken.getDate(), localCertPath);
/*     */       }
/*     */     }
/*     */ 
/* 556 */     return localTimestamp;
/*     */   }
/*     */ 
/*     */   static String toHex(byte[] paramArrayOfByte)
/*     */   {
/* 570 */     StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
/*     */ 
/* 572 */     for (int i = 0; i < paramArrayOfByte.length; i++) {
/* 573 */       localStringBuffer.append(hexc[(paramArrayOfByte[i] >> 4 & 0xF)]);
/* 574 */       localStringBuffer.append(hexc[(paramArrayOfByte[i] & 0xF)]);
/*     */     }
/* 576 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   static boolean contains(CodeSigner[] paramArrayOfCodeSigner, CodeSigner paramCodeSigner)
/*     */   {
/* 582 */     for (int i = 0; i < paramArrayOfCodeSigner.length; i++) {
/* 583 */       if (paramArrayOfCodeSigner[i].equals(paramCodeSigner))
/* 584 */         return true;
/*     */     }
/* 586 */     return false;
/*     */   }
/*     */ 
/*     */   static boolean isSubSet(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2)
/*     */   {
/* 593 */     if (paramArrayOfCodeSigner2 == paramArrayOfCodeSigner1) {
/* 594 */       return true;
/*     */     }
/*     */ 
/* 597 */     for (int i = 0; i < paramArrayOfCodeSigner1.length; i++) {
/* 598 */       if (!contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[i]))
/* 599 */         return false;
/*     */     }
/* 601 */     return true;
/*     */   }
/*     */ 
/*     */   static boolean matches(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2, CodeSigner[] paramArrayOfCodeSigner3)
/*     */   {
/* 613 */     if ((paramArrayOfCodeSigner2 == null) && (paramArrayOfCodeSigner1 == paramArrayOfCodeSigner3)) {
/* 614 */       return true;
/*     */     }
/*     */ 
/* 619 */     if ((paramArrayOfCodeSigner2 != null) && (!isSubSet(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1))) {
/* 620 */       return false;
/*     */     }
/*     */ 
/* 623 */     if (!isSubSet(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1)) {
/* 624 */       return false;
/*     */     }
/*     */ 
/* 630 */     for (int i = 0; i < paramArrayOfCodeSigner1.length; i++) {
/* 631 */       int j = ((paramArrayOfCodeSigner2 != null) && (contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[i]))) || (contains(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1[i])) ? 1 : 0;
/*     */ 
/* 634 */       if (j == 0)
/* 635 */         return false;
/*     */     }
/* 637 */     return true;
/*     */   }
/*     */ 
/*     */   void updateSigners(CodeSigner[] paramArrayOfCodeSigner, Hashtable<String, CodeSigner[]> paramHashtable, String paramString)
/*     */   {
/* 643 */     CodeSigner[] arrayOfCodeSigner1 = (CodeSigner[])paramHashtable.get(paramString);
/*     */     CodeSigner[] arrayOfCodeSigner2;
/* 650 */     for (int i = this.signerCache.size() - 1; i != -1; i--) {
/* 651 */       arrayOfCodeSigner2 = (CodeSigner[])this.signerCache.get(i);
/* 652 */       if (matches(arrayOfCodeSigner2, arrayOfCodeSigner1, paramArrayOfCodeSigner)) {
/* 653 */         paramHashtable.put(paramString, arrayOfCodeSigner2);
/* 654 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 658 */     if (arrayOfCodeSigner1 == null) {
/* 659 */       arrayOfCodeSigner2 = paramArrayOfCodeSigner;
/*     */     } else {
/* 661 */       arrayOfCodeSigner2 = new CodeSigner[arrayOfCodeSigner1.length + paramArrayOfCodeSigner.length];
/*     */ 
/* 663 */       System.arraycopy(arrayOfCodeSigner1, 0, arrayOfCodeSigner2, 0, arrayOfCodeSigner1.length);
/*     */ 
/* 665 */       System.arraycopy(paramArrayOfCodeSigner, 0, arrayOfCodeSigner2, arrayOfCodeSigner1.length, paramArrayOfCodeSigner.length);
/*     */     }
/*     */ 
/* 668 */     this.signerCache.add(arrayOfCodeSigner2);
/* 669 */     paramHashtable.put(paramString, arrayOfCodeSigner2);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.util.SignatureFileVerifier
 * JD-Core Version:    0.6.2
 */