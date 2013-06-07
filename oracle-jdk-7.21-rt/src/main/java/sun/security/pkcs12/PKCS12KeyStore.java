/*      */ package sun.security.pkcs12;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.security.AlgorithmParameters;
/*      */ import java.security.Key;
/*      */ import java.security.KeyFactory;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.KeyStoreSpi;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.SecureRandom;
/*      */ import java.security.UnrecoverableKeyException;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.security.spec.AlgorithmParameterSpec;
/*      */ import java.security.spec.KeySpec;
/*      */ import java.security.spec.PKCS8EncodedKeySpec;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.LinkedHashMap;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.Mac;
/*      */ import javax.crypto.SecretKey;
/*      */ import javax.crypto.SecretKeyFactory;
/*      */ import javax.crypto.spec.PBEKeySpec;
/*      */ import javax.crypto.spec.PBEParameterSpec;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.security.pkcs.ContentInfo;
/*      */ import sun.security.pkcs.EncryptedPrivateKeyInfo;
/*      */ import sun.security.util.DerInputStream;
/*      */ import sun.security.util.DerOutputStream;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ import sun.security.x509.AlgorithmId;
/*      */ 
/*      */ public final class PKCS12KeyStore extends KeyStoreSpi
/*      */ {
/*      */   public static final int VERSION_3 = 3;
/*  125 */   private static final int[] keyBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 2 };
/*  126 */   private static final int[] certBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 3 };
/*      */ 
/*  128 */   private static final int[] pkcs9Name = { 1, 2, 840, 113549, 1, 9, 20 };
/*  129 */   private static final int[] pkcs9KeyId = { 1, 2, 840, 113549, 1, 9, 21 };
/*      */ 
/*  131 */   private static final int[] pkcs9certType = { 1, 2, 840, 113549, 1, 9, 22, 1 };
/*      */ 
/*  133 */   private static final int[] pbeWithSHAAnd40BitRC2CBC = { 1, 2, 840, 113549, 1, 12, 1, 6 };
/*      */ 
/*  135 */   private static final int[] pbeWithSHAAnd3KeyTripleDESCBC = { 1, 2, 840, 113549, 1, 12, 1, 3 };
/*      */   private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
/*      */   private static ObjectIdentifier CertBag_OID;
/*      */   private static ObjectIdentifier PKCS9FriendlyName_OID;
/*      */   private static ObjectIdentifier PKCS9LocalKeyId_OID;
/*      */   private static ObjectIdentifier PKCS9CertType_OID;
/*      */   private static ObjectIdentifier pbeWithSHAAnd40BitRC2CBC_OID;
/*      */   private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
/*  146 */   private int counter = 0;
/*      */   private static final int iterationCount = 1024;
/*      */   private static final int SALT_LEN = 20;
/*  153 */   private int privateKeyCount = 0;
/*      */   private SecureRandom random;
/*  199 */   private Hashtable<String, KeyEntry> entries = new Hashtable();
/*      */ 
/*  202 */   private ArrayList<KeyEntry> keyList = new ArrayList();
/*  203 */   private LinkedHashMap<X500Principal, X509Certificate> certsMap = new LinkedHashMap();
/*      */ 
/*  205 */   private ArrayList<CertEntry> certEntries = new ArrayList();
/*      */ 
/*      */   public Key engineGetKey(String paramString, char[] paramArrayOfChar)
/*      */     throws NoSuchAlgorithmException, UnrecoverableKeyException
/*      */   {
/*  225 */     KeyEntry localKeyEntry = (KeyEntry)this.entries.get(paramString.toLowerCase());
/*  226 */     PrivateKey localPrivateKey = null;
/*      */ 
/*  228 */     if (localKeyEntry == null) {
/*  229 */       return null; } 
/*      */ byte[] arrayOfByte1 = localKeyEntry.protectedPrivKey;
/*      */     byte[] arrayOfByte2;
/*      */     Object localObject1;
/*      */     Object localObject3;
/*      */     ObjectIdentifier localObjectIdentifier;
/*      */     AlgorithmParameters localAlgorithmParameters;
/*      */     try { EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte1);
/*      */ 
/*  242 */       arrayOfByte2 = localEncryptedPrivateKeyInfo.getEncryptedData();
/*      */ 
/*  245 */       localObject1 = new DerValue(localEncryptedPrivateKeyInfo.getAlgorithm().encode());
/*  246 */       localObject3 = ((DerValue)localObject1).toDerInputStream();
/*  247 */       localObjectIdentifier = ((DerInputStream)localObject3).getOID();
/*  248 */       localAlgorithmParameters = parseAlgParameters((DerInputStream)localObject3);
/*      */     } catch (IOException localIOException)
/*      */     {
/*  251 */       localObject1 = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException);
/*      */ 
/*  254 */       ((UnrecoverableKeyException)localObject1).initCause(localIOException);
/*  255 */       throw ((Throwable)localObject1);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*      */       byte[] arrayOfByte3;
/*      */       try
/*      */       {
/*  263 */         localObject1 = getPBEKey(paramArrayOfChar);
/*  264 */         localObject3 = Cipher.getInstance(localObjectIdentifier.toString());
/*  265 */         ((Cipher)localObject3).init(2, (Key)localObject1, localAlgorithmParameters);
/*  266 */         arrayOfByte3 = ((Cipher)localObject3).doFinal(arrayOfByte2);
/*      */       }
/*      */       catch (Exception localException2) {
/*  269 */         while (paramArrayOfChar.length == 0)
/*      */         {
/*  272 */           paramArrayOfChar = new char[1];
/*      */         }
/*      */ 
/*  275 */         throw localException2;
/*      */       }
/*      */ 
/*  279 */       localObject2 = new PKCS8EncodedKeySpec(arrayOfByte3);
/*      */ 
/*  285 */       localObject3 = new DerValue(arrayOfByte3);
/*  286 */       DerInputStream localDerInputStream = ((DerValue)localObject3).toDerInputStream();
/*  287 */       int i = localDerInputStream.getInteger();
/*  288 */       DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
/*  289 */       AlgorithmId localAlgorithmId = new AlgorithmId(arrayOfDerValue[0].getOID());
/*  290 */       String str = localAlgorithmId.getName();
/*      */ 
/*  292 */       KeyFactory localKeyFactory = KeyFactory.getInstance(str);
/*  293 */       localPrivateKey = localKeyFactory.generatePrivate((KeySpec)localObject2);
/*      */     } catch (Exception localException1) {
/*  295 */       Object localObject2 = new UnrecoverableKeyException("Get Key failed: " + localException1.getMessage());
/*      */ 
/*  298 */       ((UnrecoverableKeyException)localObject2).initCause(localException1);
/*  299 */       throw ((Throwable)localObject2);
/*      */     }
/*  301 */     return localPrivateKey;
/*      */   }
/*      */ 
/*      */   public Certificate[] engineGetCertificateChain(String paramString)
/*      */   {
/*  316 */     KeyEntry localKeyEntry = (KeyEntry)this.entries.get(paramString.toLowerCase());
/*  317 */     if (localKeyEntry != null) {
/*  318 */       if (localKeyEntry.chain == null) {
/*  319 */         return null;
/*      */       }
/*  321 */       return (Certificate[])localKeyEntry.chain.clone();
/*      */     }
/*      */ 
/*  324 */     return null;
/*      */   }
/*      */ 
/*      */   public Certificate engineGetCertificate(String paramString)
/*      */   {
/*  344 */     KeyEntry localKeyEntry = (KeyEntry)this.entries.get(paramString.toLowerCase());
/*  345 */     if (localKeyEntry != null) {
/*  346 */       if (localKeyEntry.chain == null) {
/*  347 */         return null;
/*      */       }
/*  349 */       return localKeyEntry.chain[0];
/*      */     }
/*      */ 
/*  352 */     return null;
/*      */   }
/*      */ 
/*      */   public Date engineGetCreationDate(String paramString)
/*      */   {
/*  365 */     KeyEntry localKeyEntry = (KeyEntry)this.entries.get(paramString.toLowerCase());
/*  366 */     if (localKeyEntry != null) {
/*  367 */       return new Date(localKeyEntry.date.getTime());
/*      */     }
/*  369 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
/*      */     throws KeyStoreException
/*      */   {
/*      */     try
/*      */     {
/*  400 */       KeyEntry localKeyEntry = new KeyEntry(null);
/*  401 */       localKeyEntry.date = new Date();
/*      */ 
/*  403 */       if ((paramKey instanceof PrivateKey)) {
/*  404 */         if ((paramKey.getFormat().equals("PKCS#8")) || (paramKey.getFormat().equals("PKCS8")))
/*      */         {
/*  407 */           localKeyEntry.protectedPrivKey = encryptPrivateKey(paramKey.getEncoded(), paramArrayOfChar);
/*      */         }
/*      */         else {
/*  410 */           throw new KeyStoreException("Private key is not encodedas PKCS#8");
/*      */         }
/*      */       }
/*      */       else {
/*  414 */         throw new KeyStoreException("Key is not a PrivateKey");
/*      */       }
/*      */ 
/*  418 */       if (paramArrayOfCertificate != null)
/*      */       {
/*  420 */         if ((paramArrayOfCertificate.length > 1) && (!validateChain(paramArrayOfCertificate))) {
/*  421 */           throw new KeyStoreException("Certificate chain is not validate");
/*      */         }
/*  423 */         localKeyEntry.chain = ((Certificate[])paramArrayOfCertificate.clone());
/*      */       }
/*      */ 
/*  427 */       localKeyEntry.keyId = ("Time " + localKeyEntry.date.getTime()).getBytes("UTF8");
/*      */ 
/*  429 */       localKeyEntry.alias = paramString.toLowerCase();
/*      */ 
/*  432 */       this.entries.put(paramString.toLowerCase(), localKeyEntry);
/*      */     } catch (Exception localException) {
/*  434 */       KeyStoreException localKeyStoreException = new KeyStoreException("Key protection  algorithm not found: " + localException);
/*      */ 
/*  436 */       localKeyStoreException.initCause(localException);
/*  437 */       throw localKeyStoreException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
/*      */     throws KeyStoreException
/*      */   {
/*      */     try
/*      */     {
/*  471 */       new EncryptedPrivateKeyInfo(paramArrayOfByte);
/*      */     } catch (IOException localIOException) {
/*  473 */       KeyStoreException localKeyStoreException = new KeyStoreException("Private key is not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException);
/*      */ 
/*  475 */       localKeyStoreException.initCause(localIOException);
/*  476 */       throw localKeyStoreException;
/*      */     }
/*      */ 
/*  479 */     KeyEntry localKeyEntry = new KeyEntry(null);
/*  480 */     localKeyEntry.date = new Date();
/*      */     try
/*      */     {
/*  484 */       localKeyEntry.keyId = ("Time " + localKeyEntry.date.getTime()).getBytes("UTF8");
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*      */     {
/*      */     }
/*  489 */     localKeyEntry.alias = paramString.toLowerCase();
/*      */ 
/*  491 */     localKeyEntry.protectedPrivKey = ((byte[])paramArrayOfByte.clone());
/*  492 */     if (paramArrayOfCertificate != null) {
/*  493 */       localKeyEntry.chain = ((Certificate[])paramArrayOfCertificate.clone());
/*      */     }
/*      */ 
/*  497 */     this.entries.put(paramString.toLowerCase(), localKeyEntry);
/*      */   }
/*      */ 
/*      */   private byte[] getSalt()
/*      */   {
/*  507 */     byte[] arrayOfByte = new byte[20];
/*  508 */     if (this.random == null) {
/*  509 */       this.random = new SecureRandom();
/*      */     }
/*  511 */     this.random.nextBytes(arrayOfByte);
/*  512 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   private AlgorithmParameters getAlgorithmParameters(String paramString)
/*      */     throws IOException
/*      */   {
/*  521 */     AlgorithmParameters localAlgorithmParameters = null;
/*      */ 
/*  524 */     PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(getSalt(), 1024);
/*      */     try
/*      */     {
/*  527 */       localAlgorithmParameters = AlgorithmParameters.getInstance(paramString);
/*  528 */       localAlgorithmParameters.init(localPBEParameterSpec);
/*      */     } catch (Exception localException) {
/*  530 */       IOException localIOException = new IOException("getAlgorithmParameters failed: " + localException.getMessage());
/*      */ 
/*  533 */       localIOException.initCause(localException);
/*  534 */       throw localIOException;
/*      */     }
/*  536 */     return localAlgorithmParameters;
/*      */   }
/*      */ 
/*      */   private AlgorithmParameters parseAlgParameters(DerInputStream paramDerInputStream)
/*      */     throws IOException
/*      */   {
/*  545 */     AlgorithmParameters localAlgorithmParameters = null;
/*      */     try
/*      */     {
/*      */       DerValue localDerValue;
/*  548 */       if (paramDerInputStream.available() == 0) {
/*  549 */         localDerValue = null;
/*      */       } else {
/*  551 */         localDerValue = paramDerInputStream.getDerValue();
/*  552 */         if (localDerValue.tag == 5) {
/*  553 */           localDerValue = null;
/*      */         }
/*      */       }
/*  556 */       if (localDerValue != null) {
/*  557 */         localAlgorithmParameters = AlgorithmParameters.getInstance("PBE");
/*  558 */         localAlgorithmParameters.init(localDerValue.toByteArray());
/*      */       }
/*      */     } catch (Exception localException) {
/*  561 */       IOException localIOException = new IOException("parseAlgParameters failed: " + localException.getMessage());
/*      */ 
/*  564 */       localIOException.initCause(localException);
/*  565 */       throw localIOException;
/*      */     }
/*  567 */     return localAlgorithmParameters;
/*      */   }
/*      */ 
/*      */   private SecretKey getPBEKey(char[] paramArrayOfChar)
/*      */     throws IOException
/*      */   {
/*  575 */     SecretKey localSecretKey = null;
/*      */     try
/*      */     {
/*  578 */       PBEKeySpec localPBEKeySpec = new PBEKeySpec(paramArrayOfChar);
/*  579 */       localObject = SecretKeyFactory.getInstance("PBE");
/*  580 */       localSecretKey = ((SecretKeyFactory)localObject).generateSecret(localPBEKeySpec);
/*      */     } catch (Exception localException) {
/*  582 */       Object localObject = new IOException("getSecretKey failed: " + localException.getMessage());
/*      */ 
/*  584 */       ((IOException)localObject).initCause(localException);
/*  585 */       throw ((Throwable)localObject);
/*      */     }
/*  587 */     return localSecretKey;
/*      */   }
/*      */ 
/*      */   private byte[] encryptPrivateKey(byte[] paramArrayOfByte, char[] paramArrayOfChar)
/*      */     throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException
/*      */   {
/*  602 */     byte[] arrayOfByte1 = null;
/*      */     try
/*      */     {
/*  606 */       AlgorithmParameters localAlgorithmParameters = getAlgorithmParameters("PBEWithSHA1AndDESede");
/*      */ 
/*  610 */       localObject = getPBEKey(paramArrayOfChar);
/*  611 */       Cipher localCipher = Cipher.getInstance("PBEWithSHA1AndDESede");
/*  612 */       localCipher.init(1, (Key)localObject, localAlgorithmParameters);
/*  613 */       byte[] arrayOfByte2 = localCipher.doFinal(paramArrayOfByte);
/*      */ 
/*  617 */       AlgorithmId localAlgorithmId = new AlgorithmId(pbeWithSHAAnd3KeyTripleDESCBC_OID, localAlgorithmParameters);
/*      */ 
/*  619 */       EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(localAlgorithmId, arrayOfByte2);
/*      */ 
/*  621 */       arrayOfByte1 = localEncryptedPrivateKeyInfo.getEncoded();
/*      */     } catch (Exception localException) {
/*  623 */       Object localObject = new UnrecoverableKeyException("Encrypt Private Key failed: " + localException.getMessage());
/*      */ 
/*  626 */       ((UnrecoverableKeyException)localObject).initCause(localException);
/*  627 */       throw ((Throwable)localObject);
/*      */     }
/*      */ 
/*  630 */     return arrayOfByte1;
/*      */   }
/*      */ 
/*      */   public synchronized void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
/*      */     throws KeyStoreException
/*      */   {
/*  650 */     KeyEntry localKeyEntry = (KeyEntry)this.entries.get(paramString.toLowerCase());
/*  651 */     if (localKeyEntry != null) {
/*  652 */       throw new KeyStoreException("Cannot overwrite own certificate");
/*      */     }
/*  654 */     throw new KeyStoreException("TrustedCertEntry not supported");
/*      */   }
/*      */ 
/*      */   public synchronized void engineDeleteEntry(String paramString)
/*      */     throws KeyStoreException
/*      */   {
/*  667 */     this.entries.remove(paramString.toLowerCase());
/*      */   }
/*      */ 
/*      */   public Enumeration<String> engineAliases()
/*      */   {
/*  676 */     return this.entries.keys();
/*      */   }
/*      */ 
/*      */   public boolean engineContainsAlias(String paramString)
/*      */   {
/*  687 */     return this.entries.containsKey(paramString.toLowerCase());
/*      */   }
/*      */ 
/*      */   public int engineSize()
/*      */   {
/*  696 */     return this.entries.size();
/*      */   }
/*      */ 
/*      */   public boolean engineIsKeyEntry(String paramString)
/*      */   {
/*  707 */     KeyEntry localKeyEntry = (KeyEntry)this.entries.get(paramString.toLowerCase());
/*  708 */     if (localKeyEntry != null) {
/*  709 */       return true;
/*      */     }
/*  711 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean engineIsCertificateEntry(String paramString)
/*      */   {
/*  724 */     return false;
/*      */   }
/*      */ 
/*      */   public String engineGetCertificateAlias(Certificate paramCertificate)
/*      */   {
/*  744 */     Certificate localCertificate = null;
/*      */ 
/*  746 */     for (Enumeration localEnumeration = this.entries.keys(); localEnumeration.hasMoreElements(); ) {
/*  747 */       String str = (String)localEnumeration.nextElement();
/*  748 */       KeyEntry localKeyEntry = (KeyEntry)this.entries.get(str);
/*  749 */       if (localKeyEntry.chain != null) {
/*  750 */         localCertificate = localKeyEntry.chain[0];
/*      */       }
/*  752 */       if (localCertificate.equals(paramCertificate)) {
/*  753 */         return str;
/*      */       }
/*      */     }
/*  756 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
/*      */     throws IOException, NoSuchAlgorithmException, CertificateException
/*      */   {
/*  776 */     if (paramArrayOfChar == null) {
/*  777 */       throw new IllegalArgumentException("password can't be null");
/*      */     }
/*      */ 
/*  781 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*      */ 
/*  784 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  785 */     localDerOutputStream2.putInteger(3);
/*  786 */     byte[] arrayOfByte1 = localDerOutputStream2.toByteArray();
/*  787 */     localDerOutputStream1.write(arrayOfByte1);
/*      */ 
/*  790 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/*      */ 
/*  793 */     DerOutputStream localDerOutputStream4 = new DerOutputStream();
/*      */ 
/*  796 */     byte[] arrayOfByte2 = createSafeContent();
/*  797 */     ContentInfo localContentInfo1 = new ContentInfo(arrayOfByte2);
/*  798 */     localContentInfo1.encode(localDerOutputStream4);
/*      */ 
/*  801 */     byte[] arrayOfByte3 = createEncryptedData(paramArrayOfChar);
/*  802 */     ContentInfo localContentInfo2 = new ContentInfo(ContentInfo.ENCRYPTED_DATA_OID, new DerValue(arrayOfByte3));
/*      */ 
/*  805 */     localContentInfo2.encode(localDerOutputStream4);
/*      */ 
/*  808 */     DerOutputStream localDerOutputStream5 = new DerOutputStream();
/*  809 */     localDerOutputStream5.write((byte)48, localDerOutputStream4);
/*  810 */     byte[] arrayOfByte4 = localDerOutputStream5.toByteArray();
/*      */ 
/*  813 */     ContentInfo localContentInfo3 = new ContentInfo(arrayOfByte4);
/*  814 */     localContentInfo3.encode(localDerOutputStream3);
/*  815 */     byte[] arrayOfByte5 = localDerOutputStream3.toByteArray();
/*  816 */     localDerOutputStream1.write(arrayOfByte5);
/*      */ 
/*  819 */     byte[] arrayOfByte6 = calculateMac(paramArrayOfChar, arrayOfByte4);
/*  820 */     localDerOutputStream1.write(arrayOfByte6);
/*      */ 
/*  823 */     DerOutputStream localDerOutputStream6 = new DerOutputStream();
/*  824 */     localDerOutputStream6.write((byte)48, localDerOutputStream1);
/*  825 */     byte[] arrayOfByte7 = localDerOutputStream6.toByteArray();
/*  826 */     paramOutputStream.write(arrayOfByte7);
/*  827 */     paramOutputStream.flush();
/*      */   }
/*      */ 
/*      */   private byte[] generateHash(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  836 */     byte[] arrayOfByte = null;
/*      */     try
/*      */     {
/*  839 */       MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
/*  840 */       localMessageDigest.update(paramArrayOfByte);
/*  841 */       arrayOfByte = localMessageDigest.digest();
/*      */     } catch (Exception localException) {
/*  843 */       IOException localIOException = new IOException("generateHash failed: " + localException);
/*  844 */       localIOException.initCause(localException);
/*  845 */       throw localIOException;
/*      */     }
/*  847 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   private byte[] calculateMac(char[] paramArrayOfChar, byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  860 */     byte[] arrayOfByte1 = null;
/*  861 */     String str = "SHA1";
/*      */     try
/*      */     {
/*  865 */       byte[] arrayOfByte2 = getSalt();
/*      */ 
/*  868 */       localObject = Mac.getInstance("HmacPBESHA1");
/*  869 */       PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(arrayOfByte2, 1024);
/*      */ 
/*  871 */       SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
/*  872 */       ((Mac)localObject).init(localSecretKey, localPBEParameterSpec);
/*  873 */       ((Mac)localObject).update(paramArrayOfByte);
/*  874 */       byte[] arrayOfByte3 = ((Mac)localObject).doFinal();
/*      */ 
/*  877 */       MacData localMacData = new MacData(str, arrayOfByte3, arrayOfByte2, 1024);
/*      */ 
/*  879 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/*  880 */       localDerOutputStream.write(localMacData.getEncoded());
/*  881 */       arrayOfByte1 = localDerOutputStream.toByteArray();
/*      */     } catch (Exception localException) {
/*  883 */       Object localObject = new IOException("calculateMac failed: " + localException);
/*  884 */       ((IOException)localObject).initCause(localException);
/*  885 */       throw ((Throwable)localObject);
/*      */     }
/*  887 */     return arrayOfByte1;
/*      */   }
/*      */ 
/*      */   private boolean validateChain(Certificate[] paramArrayOfCertificate)
/*      */   {
/*  896 */     for (int i = 0; i < paramArrayOfCertificate.length - 1; i++) {
/*  897 */       X500Principal localX500Principal1 = ((X509Certificate)paramArrayOfCertificate[i]).getIssuerX500Principal();
/*      */ 
/*  899 */       X500Principal localX500Principal2 = ((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectX500Principal();
/*      */ 
/*  901 */       if (!localX500Principal1.equals(localX500Principal2))
/*  902 */         return false;
/*      */     }
/*  904 */     return true;
/*      */   }
/*      */ 
/*      */   private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  944 */     byte[] arrayOfByte1 = null;
/*  945 */     byte[] arrayOfByte2 = null;
/*      */ 
/*  948 */     if ((paramString == null) && (paramArrayOfByte == null)) {
/*  949 */       return null;
/*      */     }
/*      */ 
/*  953 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*      */     DerOutputStream localDerOutputStream3;
/*      */     DerOutputStream localDerOutputStream4;
/*  956 */     if (paramString != null) {
/*  957 */       localDerOutputStream2 = new DerOutputStream();
/*  958 */       localDerOutputStream2.putOID(PKCS9FriendlyName_OID);
/*  959 */       localDerOutputStream3 = new DerOutputStream();
/*  960 */       localDerOutputStream4 = new DerOutputStream();
/*  961 */       localDerOutputStream3.putBMPString(paramString);
/*  962 */       localDerOutputStream2.write((byte)49, localDerOutputStream3);
/*  963 */       localDerOutputStream4.write((byte)48, localDerOutputStream2);
/*  964 */       arrayOfByte2 = localDerOutputStream4.toByteArray();
/*      */     }
/*      */ 
/*  968 */     if (paramArrayOfByte != null) {
/*  969 */       localDerOutputStream2 = new DerOutputStream();
/*  970 */       localDerOutputStream2.putOID(PKCS9LocalKeyId_OID);
/*  971 */       localDerOutputStream3 = new DerOutputStream();
/*  972 */       localDerOutputStream4 = new DerOutputStream();
/*  973 */       localDerOutputStream3.putOctetString(paramArrayOfByte);
/*  974 */       localDerOutputStream2.write((byte)49, localDerOutputStream3);
/*  975 */       localDerOutputStream4.write((byte)48, localDerOutputStream2);
/*  976 */       arrayOfByte1 = localDerOutputStream4.toByteArray();
/*      */     }
/*      */ 
/*  979 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  980 */     if (arrayOfByte2 != null) {
/*  981 */       localDerOutputStream2.write(arrayOfByte2);
/*      */     }
/*  983 */     if (arrayOfByte1 != null) {
/*  984 */       localDerOutputStream2.write(arrayOfByte1);
/*      */     }
/*  986 */     localDerOutputStream1.write((byte)49, localDerOutputStream2);
/*  987 */     return localDerOutputStream1.toByteArray();
/*      */   }
/*      */ 
/*      */   private byte[] createEncryptedData(char[] paramArrayOfChar)
/*      */     throws CertificateException, IOException
/*      */   {
/* 1000 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 1001 */     for (Object localObject1 = this.entries.keys(); ((Enumeration)localObject1).hasMoreElements(); )
/*      */     {
/* 1003 */       localObject2 = (String)((Enumeration)localObject1).nextElement();
/* 1004 */       localObject3 = (KeyEntry)this.entries.get(localObject2);
/*      */       int i;
/* 1008 */       if (((KeyEntry)localObject3).chain == null)
/* 1009 */         i = 0;
/*      */       else {
/* 1011 */         i = ((KeyEntry)localObject3).chain.length;
/*      */       }
/*      */ 
/* 1014 */       for (int j = 0; j < i; j++)
/*      */       {
/* 1016 */         DerOutputStream localDerOutputStream4 = new DerOutputStream();
/* 1017 */         localDerOutputStream4.putOID(CertBag_OID);
/*      */ 
/* 1020 */         DerOutputStream localDerOutputStream5 = new DerOutputStream();
/* 1021 */         localDerOutputStream5.putOID(PKCS9CertType_OID);
/*      */ 
/* 1024 */         DerOutputStream localDerOutputStream6 = new DerOutputStream();
/* 1025 */         X509Certificate localX509Certificate = (X509Certificate)localObject3.chain[j];
/* 1026 */         localDerOutputStream6.putOctetString(localX509Certificate.getEncoded());
/* 1027 */         localDerOutputStream5.write(DerValue.createTag((byte)-128, true, (byte)0), localDerOutputStream6);
/*      */ 
/* 1031 */         DerOutputStream localDerOutputStream7 = new DerOutputStream();
/* 1032 */         localDerOutputStream7.write((byte)48, localDerOutputStream5);
/* 1033 */         byte[] arrayOfByte1 = localDerOutputStream7.toByteArray();
/*      */ 
/* 1036 */         DerOutputStream localDerOutputStream8 = new DerOutputStream();
/* 1037 */         localDerOutputStream8.write(arrayOfByte1);
/*      */ 
/* 1039 */         localDerOutputStream4.write(DerValue.createTag((byte)-128, true, (byte)0), localDerOutputStream8);
/*      */ 
/* 1045 */         byte[] arrayOfByte2 = null;
/* 1046 */         if (j == 0)
/*      */         {
/* 1048 */           arrayOfByte2 = getBagAttributes(((KeyEntry)localObject3).alias, ((KeyEntry)localObject3).keyId);
/*      */         }
/*      */         else
/*      */         {
/* 1056 */           arrayOfByte2 = getBagAttributes(localX509Certificate.getSubjectX500Principal().getName(), null);
/*      */         }
/*      */ 
/* 1059 */         if (arrayOfByte2 != null) {
/* 1060 */           localDerOutputStream4.write(arrayOfByte2);
/*      */         }
/*      */ 
/* 1064 */         localDerOutputStream1.write((byte)48, localDerOutputStream4);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1069 */     localObject1 = new DerOutputStream();
/* 1070 */     ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
/* 1071 */     Object localObject2 = ((DerOutputStream)localObject1).toByteArray();
/*      */ 
/* 1074 */     Object localObject3 = encryptContent((byte[])localObject2, paramArrayOfChar);
/*      */ 
/* 1077 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1078 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 1079 */     localDerOutputStream2.putInteger(0);
/* 1080 */     localDerOutputStream2.write((byte[])localObject3);
/* 1081 */     localDerOutputStream3.write((byte)48, localDerOutputStream2);
/* 1082 */     return localDerOutputStream3.toByteArray();
/*      */   }
/*      */ 
/*      */   private byte[] createSafeContent()
/*      */     throws CertificateException, IOException
/*      */   {
/* 1094 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 1095 */     for (Object localObject = this.entries.keys(); ((Enumeration)localObject).hasMoreElements(); )
/*      */     {
/* 1097 */       String str = (String)((Enumeration)localObject).nextElement();
/* 1098 */       KeyEntry localKeyEntry = (KeyEntry)this.entries.get(str);
/*      */ 
/* 1101 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1102 */       localDerOutputStream2.putOID(PKCS8ShroudedKeyBag_OID);
/*      */ 
/* 1105 */       byte[] arrayOfByte1 = localKeyEntry.protectedPrivKey;
/* 1106 */       EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = null;
/*      */       try {
/* 1108 */         localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte1);
/*      */       } catch (IOException localIOException) {
/* 1110 */         throw new IOException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo" + localIOException.getMessage());
/*      */       }
/*      */ 
/* 1115 */       DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 1116 */       localDerOutputStream3.write(localEncryptedPrivateKeyInfo.getEncoded());
/* 1117 */       localDerOutputStream2.write(DerValue.createTag((byte)-128, true, (byte)0), localDerOutputStream3);
/*      */ 
/* 1121 */       byte[] arrayOfByte2 = getBagAttributes(str, localKeyEntry.keyId);
/* 1122 */       localDerOutputStream2.write(arrayOfByte2);
/*      */ 
/* 1125 */       localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*      */     }
/*      */ 
/* 1129 */     localObject = new DerOutputStream();
/* 1130 */     ((DerOutputStream)localObject).write((byte)48, localDerOutputStream1);
/* 1131 */     return ((DerOutputStream)localObject).toByteArray();
/*      */   }
/*      */ 
/*      */   private byte[] encryptContent(byte[] paramArrayOfByte, char[] paramArrayOfChar)
/*      */     throws IOException
/*      */   {
/* 1147 */     byte[] arrayOfByte1 = null;
/*      */ 
/* 1150 */     AlgorithmParameters localAlgorithmParameters = getAlgorithmParameters("PBEWithSHA1AndRC2_40");
/*      */ 
/* 1152 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 1153 */     AlgorithmId localAlgorithmId = new AlgorithmId(pbeWithSHAAnd40BitRC2CBC_OID, localAlgorithmParameters);
/*      */ 
/* 1155 */     localAlgorithmId.encode(localDerOutputStream1);
/* 1156 */     byte[] arrayOfByte2 = localDerOutputStream1.toByteArray();
/*      */     try
/*      */     {
/* 1160 */       SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
/* 1161 */       localObject = Cipher.getInstance("PBEWithSHA1AndRC2_40");
/* 1162 */       ((Cipher)localObject).init(1, localSecretKey, localAlgorithmParameters);
/* 1163 */       arrayOfByte1 = ((Cipher)localObject).doFinal(paramArrayOfByte);
/*      */     }
/*      */     catch (Exception localException) {
/* 1166 */       localObject = new IOException("Failed to encrypt safe contents entry: " + localException);
/*      */ 
/* 1168 */       ((IOException)localObject).initCause(localException);
/* 1169 */       throw ((Throwable)localObject);
/*      */     }
/*      */ 
/* 1173 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1174 */     localDerOutputStream2.putOID(ContentInfo.DATA_OID);
/* 1175 */     localDerOutputStream2.write(arrayOfByte2);
/*      */ 
/* 1178 */     Object localObject = new DerOutputStream();
/* 1179 */     ((DerOutputStream)localObject).putOctetString(arrayOfByte1);
/* 1180 */     localDerOutputStream2.writeImplicit(DerValue.createTag((byte)-128, false, (byte)0), (DerOutputStream)localObject);
/*      */ 
/* 1184 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 1185 */     localDerOutputStream3.write((byte)48, localDerOutputStream2);
/* 1186 */     return localDerOutputStream3.toByteArray();
/*      */   }
/*      */ 
/*      */   public synchronized void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
/*      */     throws IOException, NoSuchAlgorithmException, CertificateException
/*      */   {
/* 1210 */     Object localObject1 = null;
/* 1211 */     Object localObject2 = null;
/* 1212 */     Object localObject3 = null;
/*      */ 
/* 1214 */     if (paramInputStream == null) {
/* 1215 */       return;
/*      */     }
/*      */ 
/* 1218 */     this.counter = 0;
/*      */ 
/* 1220 */     DerValue localDerValue = new DerValue(paramInputStream);
/* 1221 */     DerInputStream localDerInputStream1 = localDerValue.toDerInputStream();
/* 1222 */     int i = localDerInputStream1.getInteger();
/*      */ 
/* 1224 */     if (i != 3) {
/* 1225 */       throw new IOException("PKCS12 keystore not in version 3 format");
/*      */     }
/*      */ 
/* 1228 */     this.entries.clear();
/*      */ 
/* 1234 */     ContentInfo localContentInfo = new ContentInfo(localDerInputStream1);
/* 1235 */     ObjectIdentifier localObjectIdentifier1 = localContentInfo.getContentType();
/*      */     byte[] arrayOfByte;
/* 1237 */     if (localObjectIdentifier1.equals(ContentInfo.DATA_OID))
/* 1238 */       arrayOfByte = localContentInfo.getData();
/*      */     else {
/* 1240 */       throw new IOException("public key protected PKCS12 not supported");
/*      */     }
/*      */ 
/* 1243 */     DerInputStream localDerInputStream2 = new DerInputStream(arrayOfByte);
/* 1244 */     DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
/* 1245 */     int j = arrayOfDerValue1.length;
/*      */ 
/* 1248 */     this.privateKeyCount = 0;
/*      */     Object localObject8;
/*      */     Object localObject7;
/*      */     Object localObject6;
/*      */     Object localObject5;
/*      */     Object localObject9;
/* 1253 */     for (int k = 0; k < j; k++)
/*      */     {
/* 1257 */       localObject8 = null;
/*      */ 
/* 1259 */       localObject7 = new DerInputStream(arrayOfDerValue1[k].toByteArray());
/* 1260 */       localObject6 = new ContentInfo((DerInputStream)localObject7);
/* 1261 */       localObjectIdentifier1 = ((ContentInfo)localObject6).getContentType();
/* 1262 */       localObject5 = null;
/* 1263 */       if (localObjectIdentifier1.equals(ContentInfo.DATA_OID)) {
/* 1264 */         localObject5 = ((ContentInfo)localObject6).getData();
/* 1265 */       } else if (localObjectIdentifier1.equals(ContentInfo.ENCRYPTED_DATA_OID)) {
/* 1266 */         if (paramArrayOfChar == null) {
/*      */           continue;
/*      */         }
/* 1269 */         localObject9 = ((ContentInfo)localObject6).getContent().toDerInputStream();
/*      */ 
/* 1271 */         int n = ((DerInputStream)localObject9).getInteger();
/* 1272 */         DerValue[] arrayOfDerValue2 = ((DerInputStream)localObject9).getSequence(2);
/* 1273 */         ObjectIdentifier localObjectIdentifier2 = arrayOfDerValue2[0].getOID();
/* 1274 */         localObject8 = arrayOfDerValue2[1].toByteArray();
/* 1275 */         if (!arrayOfDerValue2[2].isContextSpecific((byte)0)) {
/* 1276 */           throw new IOException("encrypted content not present!");
/*      */         }
/* 1278 */         byte b = 4;
/* 1279 */         if (arrayOfDerValue2[2].isConstructed())
/* 1280 */           b = (byte)(b | 0x20);
/* 1281 */         arrayOfDerValue2[2].resetTag(b);
/* 1282 */         localObject5 = arrayOfDerValue2[2].getOctetString();
/*      */ 
/* 1285 */         DerInputStream localDerInputStream3 = arrayOfDerValue2[1].toDerInputStream();
/* 1286 */         ObjectIdentifier localObjectIdentifier3 = localDerInputStream3.getOID();
/* 1287 */         AlgorithmParameters localAlgorithmParameters = parseAlgParameters(localDerInputStream3);
/*      */         try
/*      */         {
/* 1292 */           SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
/* 1293 */           Cipher localCipher = Cipher.getInstance(localObjectIdentifier3.toString());
/* 1294 */           localCipher.init(2, localSecretKey, localAlgorithmParameters);
/* 1295 */           localObject5 = localCipher.doFinal((byte[])localObject5);
/*      */         }
/*      */         catch (Exception localException2) {
/* 1298 */           while (paramArrayOfChar.length == 0)
/*      */           {
/* 1301 */             paramArrayOfChar = new char[1];
/*      */           }
/*      */ 
/* 1304 */           throw new IOException("failed to decrypt safe contents entry: " + localException2, localException2);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1309 */         throw new IOException("public key protected PKCS12 not supported");
/*      */       }
/*      */ 
/* 1312 */       localObject9 = new DerInputStream((byte[])localObject5);
/* 1313 */       loadSafeContents((DerInputStream)localObject9, paramArrayOfChar);
/*      */     }
/*      */ 
/* 1317 */     if ((paramArrayOfChar != null) && (localDerInputStream1.available() > 0)) {
/* 1318 */       localObject4 = new MacData(localDerInputStream1);
/*      */       try {
/* 1320 */         localObject5 = ((MacData)localObject4).getDigestAlgName().toUpperCase();
/* 1321 */         if ((((String)localObject5).equals("SHA")) || (((String)localObject5).equals("SHA1")) || (((String)localObject5).equals("SHA-1")))
/*      */         {
/* 1324 */           localObject5 = "SHA1";
/*      */         }
/*      */ 
/* 1328 */         localObject6 = Mac.getInstance("HmacPBE" + (String)localObject5);
/* 1329 */         localObject7 = new PBEParameterSpec(((MacData)localObject4).getSalt(), ((MacData)localObject4).getIterations());
/*      */ 
/* 1332 */         localObject8 = getPBEKey(paramArrayOfChar);
/* 1333 */         ((Mac)localObject6).init((Key)localObject8, (AlgorithmParameterSpec)localObject7);
/* 1334 */         ((Mac)localObject6).update(arrayOfByte);
/* 1335 */         localObject9 = ((Mac)localObject6).doFinal();
/*      */ 
/* 1337 */         if (!Arrays.equals(((MacData)localObject4).getDigest(), (byte[])localObject9))
/* 1338 */           throw new SecurityException("Failed PKCS12 integrity checking");
/*      */       }
/*      */       catch (Exception localException1)
/*      */       {
/* 1342 */         localObject6 = new IOException("Integrity check failed: " + localException1);
/*      */ 
/* 1344 */         ((IOException)localObject6).initCause(localException1);
/* 1345 */         throw ((Throwable)localObject6);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1352 */     Object localObject4 = (KeyEntry[])this.keyList.toArray(new KeyEntry[this.keyList.size()]);
/* 1353 */     for (int m = 0; m < localObject4.length; m++) {
/* 1354 */       localObject6 = localObject4[m];
/* 1355 */       if (((KeyEntry)localObject6).keyId != null) {
/* 1356 */         localObject7 = new ArrayList();
/*      */ 
/* 1358 */         localObject8 = findMatchedCertificate((KeyEntry)localObject6);
/* 1359 */         while (localObject8 != null) {
/* 1360 */           ((ArrayList)localObject7).add(localObject8);
/* 1361 */           localObject9 = ((X509Certificate)localObject8).getIssuerX500Principal();
/* 1362 */           if (((X500Principal)localObject9).equals(((X509Certificate)localObject8).getSubjectX500Principal())) {
/*      */             break;
/*      */           }
/* 1365 */           localObject8 = (X509Certificate)this.certsMap.get(localObject9);
/*      */         }
/*      */ 
/* 1368 */         if (((ArrayList)localObject7).size() > 0)
/* 1369 */           ((KeyEntry)localObject6).chain = ((Certificate[])((ArrayList)localObject7).toArray(new Certificate[((ArrayList)localObject7).size()]));
/*      */       }
/*      */     }
/* 1372 */     this.certEntries.clear();
/* 1373 */     this.certsMap.clear();
/* 1374 */     this.keyList.clear();
/*      */   }
/*      */ 
/*      */   private X509Certificate findMatchedCertificate(KeyEntry paramKeyEntry)
/*      */   {
/* 1383 */     Object localObject1 = null;
/* 1384 */     Object localObject2 = null;
/* 1385 */     for (CertEntry localCertEntry : this.certEntries) {
/* 1386 */       if (Arrays.equals(paramKeyEntry.keyId, localCertEntry.keyId)) {
/* 1387 */         localObject1 = localCertEntry;
/* 1388 */         if (paramKeyEntry.alias.equalsIgnoreCase(localCertEntry.alias))
/*      */         {
/* 1390 */           return localCertEntry.cert;
/*      */         }
/* 1392 */       } else if (paramKeyEntry.alias.equalsIgnoreCase(localCertEntry.alias)) {
/* 1393 */         localObject2 = localCertEntry;
/*      */       }
/*      */     }
/*      */ 
/* 1397 */     if (localObject1 != null) return localObject1.cert;
/* 1398 */     if (localObject2 != null) return localObject2.cert;
/* 1399 */     return null;
/*      */   }
/*      */ 
/*      */   private void loadSafeContents(DerInputStream paramDerInputStream, char[] paramArrayOfChar)
/*      */     throws IOException, NoSuchAlgorithmException, CertificateException
/*      */   {
/* 1405 */     DerValue[] arrayOfDerValue1 = paramDerInputStream.getSequence(2);
/* 1406 */     int i = arrayOfDerValue1.length;
/*      */ 
/* 1411 */     for (int j = 0; j < i; j++)
/*      */     {
/* 1415 */       Object localObject1 = null;
/*      */ 
/* 1417 */       DerInputStream localDerInputStream1 = arrayOfDerValue1[j].toDerInputStream();
/* 1418 */       ObjectIdentifier localObjectIdentifier1 = localDerInputStream1.getOID();
/* 1419 */       DerValue localDerValue1 = localDerInputStream1.getDerValue();
/* 1420 */       if (!localDerValue1.isContextSpecific((byte)0)) {
/* 1421 */         throw new IOException("unsupported PKCS12 bag value type " + localDerValue1.tag);
/*      */       }
/*      */ 
/* 1424 */       localDerValue1 = localDerValue1.data.getDerValue();
/*      */       Object localObject2;
/*      */       Object localObject5;
/*      */       Object localObject6;
/* 1425 */       if (localObjectIdentifier1.equals(PKCS8ShroudedKeyBag_OID)) {
/* 1426 */         localObject2 = new KeyEntry(null);
/* 1427 */         ((KeyEntry)localObject2).protectedPrivKey = localDerValue1.toByteArray();
/* 1428 */         localObject1 = localObject2;
/* 1429 */         this.privateKeyCount += 1;
/* 1430 */       } else if (localObjectIdentifier1.equals(CertBag_OID)) {
/* 1431 */         localObject2 = new DerInputStream(localDerValue1.toByteArray());
/* 1432 */         DerValue[] arrayOfDerValue2 = ((DerInputStream)localObject2).getSequence(2);
/* 1433 */         localObject3 = arrayOfDerValue2[0].getOID();
/* 1434 */         if (!arrayOfDerValue2[1].isContextSpecific((byte)0)) {
/* 1435 */           throw new IOException("unsupported PKCS12 cert value type " + arrayOfDerValue2[1].tag);
/*      */         }
/*      */ 
/* 1438 */         DerValue localDerValue2 = arrayOfDerValue2[1].data.getDerValue();
/* 1439 */         localObject5 = CertificateFactory.getInstance("X509");
/*      */ 
/* 1441 */         localObject6 = (X509Certificate)((CertificateFactory)localObject5).generateCertificate(new ByteArrayInputStream(localDerValue2.getOctetString()));
/*      */ 
/* 1443 */         localObject1 = localObject6;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1450 */         localObject2 = localDerInputStream1.getSet(2);
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/* 1455 */         localObject2 = null;
/*      */       }
/*      */ 
/* 1458 */       String str = null;
/* 1459 */       Object localObject3 = null;
/*      */ 
/* 1461 */       if (localObject2 != null)
/* 1462 */         for (int k = 0; k < localObject2.length; k++) { localObject5 = new DerInputStream(localObject2[k].toByteArray());
/*      */ 
/* 1465 */           localObject6 = ((DerInputStream)localObject5).getSequence(2);
/* 1466 */           ObjectIdentifier localObjectIdentifier2 = localObject6[0].getOID();
/* 1467 */           DerInputStream localDerInputStream2 = new DerInputStream(localObject6[1].toByteArray());
/*      */           DerValue[] arrayOfDerValue3;
/*      */           try { arrayOfDerValue3 = localDerInputStream2.getSet(1);
/*      */           } catch (IOException localIOException2) {
/* 1473 */             throw new IOException("Attribute " + localObjectIdentifier2 + " should have a value " + localIOException2.getMessage());
/*      */           }
/*      */ 
/* 1476 */           if (localObjectIdentifier2.equals(PKCS9FriendlyName_OID))
/* 1477 */             str = arrayOfDerValue3[0].getBMPString();
/* 1478 */           else if (localObjectIdentifier2.equals(PKCS9LocalKeyId_OID))
/* 1479 */             localObject3 = arrayOfDerValue3[0].getOctetString();
/*      */         }
/*      */       Object localObject4;
/* 1495 */       if ((localObject1 instanceof KeyEntry)) {
/* 1496 */         localObject4 = (KeyEntry)localObject1;
/* 1497 */         if (localObject3 == null)
/*      */         {
/* 1502 */           if (this.privateKeyCount == 1) {
/* 1503 */             localObject3 = "01".getBytes("UTF8");
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1508 */           ((KeyEntry)localObject4).keyId = ((byte[])localObject3);
/*      */ 
/* 1510 */           localObject5 = new String((byte[])localObject3, "UTF8");
/* 1511 */           localObject6 = null;
/* 1512 */           if (((String)localObject5).startsWith("Time ")) {
/*      */             try {
/* 1514 */               localObject6 = new Date(Long.parseLong(((String)localObject5).substring(5)));
/*      */             }
/*      */             catch (Exception localException) {
/* 1517 */               localObject6 = null;
/*      */             }
/*      */           }
/* 1520 */           if (localObject6 == null) {
/* 1521 */             localObject6 = new Date();
/*      */           }
/* 1523 */           ((KeyEntry)localObject4).date = ((Date)localObject6);
/* 1524 */           this.keyList.add(localObject4);
/* 1525 */           if (str == null)
/* 1526 */             str = getUnfriendlyName();
/* 1527 */           ((KeyEntry)localObject4).alias = str;
/* 1528 */           this.entries.put(str.toLowerCase(), localObject4);
/*      */         } } else if ((localObject1 instanceof X509Certificate)) {
/* 1530 */         localObject4 = (X509Certificate)localObject1;
/*      */ 
/* 1535 */         if ((localObject3 == null) && (this.privateKeyCount == 1))
/*      */         {
/* 1537 */           if (j == 0) {
/* 1538 */             localObject3 = "01".getBytes("UTF8");
/*      */           }
/*      */         }
/* 1541 */         this.certEntries.add(new CertEntry((X509Certificate)localObject4, (byte[])localObject3, str));
/* 1542 */         localObject5 = ((X509Certificate)localObject4).getSubjectX500Principal();
/* 1543 */         if ((localObject5 != null) && 
/* 1544 */           (!this.certsMap.containsKey(localObject5)))
/* 1545 */           this.certsMap.put(localObject5, localObject4);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getUnfriendlyName()
/*      */   {
/* 1553 */     this.counter += 1;
/* 1554 */     return String.valueOf(this.counter);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  160 */       PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(keyBag);
/*  161 */       CertBag_OID = new ObjectIdentifier(certBag);
/*  162 */       PKCS9FriendlyName_OID = new ObjectIdentifier(pkcs9Name);
/*  163 */       PKCS9LocalKeyId_OID = new ObjectIdentifier(pkcs9KeyId);
/*  164 */       PKCS9CertType_OID = new ObjectIdentifier(pkcs9certType);
/*  165 */       pbeWithSHAAnd40BitRC2CBC_OID = new ObjectIdentifier(pbeWithSHAAnd40BitRC2CBC);
/*      */ 
/*  167 */       pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(pbeWithSHAAnd3KeyTripleDESCBC);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CertEntry
/*      */   {
/*      */     final X509Certificate cert;
/*      */     final byte[] keyId;
/*      */     final String alias;
/*      */ 
/*      */     CertEntry(X509Certificate paramX509Certificate, byte[] paramArrayOfByte, String paramString)
/*      */     {
/*  189 */       this.cert = paramX509Certificate;
/*  190 */       this.keyId = paramArrayOfByte;
/*  191 */       this.alias = paramString;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class KeyEntry
/*      */   {
/*      */     Date date;
/*      */     byte[] protectedPrivKey;
/*      */     Certificate[] chain;
/*      */     byte[] keyId;
/*      */     String alias;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.pkcs12.PKCS12KeyStore
 * JD-Core Version:    0.6.2
 */