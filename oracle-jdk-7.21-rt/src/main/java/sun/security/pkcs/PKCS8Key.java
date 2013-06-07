/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyRep;
/*     */ import java.security.KeyRep.Type;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Provider;
/*     */ import java.security.Security;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.security.spec.PKCS8EncodedKeySpec;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ 
/*     */ public class PKCS8Key
/*     */   implements PrivateKey
/*     */ {
/*     */   private static final long serialVersionUID = -3836890099307167124L;
/*     */   protected AlgorithmId algid;
/*     */   protected byte[] key;
/*     */   protected byte[] encodedKey;
/*  67 */   public static final BigInteger version = BigInteger.ZERO;
/*     */ 
/*     */   public PKCS8Key()
/*     */   {
/*     */   }
/*     */ 
/*     */   private PKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
/*     */     throws InvalidKeyException
/*     */   {
/*  83 */     this.algid = paramAlgorithmId;
/*  84 */     this.key = paramArrayOfByte;
/*  85 */     encode();
/*     */   }
/*     */ 
/*     */   public static PKCS8Key parse(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/*  94 */     PrivateKey localPrivateKey = parseKey(paramDerValue);
/*  95 */     if ((localPrivateKey instanceof PKCS8Key)) {
/*  96 */       return (PKCS8Key)localPrivateKey;
/*     */     }
/*  98 */     throw new IOException("Provider did not return PKCS8Key");
/*     */   }
/*     */ 
/*     */   public static PrivateKey parseKey(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 120 */     if (paramDerValue.tag != 48) {
/* 121 */       throw new IOException("corrupt private key");
/*     */     }
/* 123 */     BigInteger localBigInteger = paramDerValue.data.getBigInteger();
/* 124 */     if (!version.equals(localBigInteger)) {
/* 125 */       throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(localBigInteger));
/*     */     }
/*     */ 
/* 131 */     AlgorithmId localAlgorithmId = AlgorithmId.parse(paramDerValue.data.getDerValue());
/*     */     PrivateKey localPrivateKey;
/*     */     try
/*     */     {
/* 134 */       localPrivateKey = buildPKCS8Key(localAlgorithmId, paramDerValue.data.getOctetString());
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 137 */       throw new IOException("corrupt private key");
/*     */     }
/*     */ 
/* 140 */     if (paramDerValue.data.available() != 0)
/* 141 */       throw new IOException("excess private key");
/* 142 */     return localPrivateKey;
/*     */   }
/*     */ 
/*     */   protected void parseKeyBits()
/*     */     throws IOException, InvalidKeyException
/*     */   {
/* 160 */     encode();
/*     */   }
/*     */ 
/*     */   static PrivateKey buildPKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
/*     */     throws IOException, InvalidKeyException
/*     */   {
/* 176 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 177 */     encode(localDerOutputStream, paramAlgorithmId, paramArrayOfByte);
/* 178 */     PKCS8EncodedKeySpec localPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(localDerOutputStream.toByteArray());
/*     */     try
/*     */     {
/* 183 */       KeyFactory localKeyFactory = KeyFactory.getInstance(paramAlgorithmId.getName());
/*     */ 
/* 186 */       return localKeyFactory.generatePrivate(localPKCS8EncodedKeySpec);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */     {
/*     */     }
/*     */     catch (InvalidKeySpecException localInvalidKeySpecException)
/*     */     {
/*     */     }
/*     */ 
/* 196 */     String str = "";
/*     */     try
/*     */     {
/* 202 */       Provider localProvider = Security.getProvider("SUN");
/* 203 */       if (localProvider == null)
/* 204 */         throw new InstantiationException();
/* 205 */       str = localProvider.getProperty("PrivateKey.PKCS#8." + paramAlgorithmId.getName());
/*     */ 
/* 207 */       if (str == null) {
/* 208 */         throw new InstantiationException();
/* 211 */       }
/*     */ Class localClass = null;
/*     */       Object localObject2;
/*     */       try { localClass = Class.forName(str);
/*     */       } catch (ClassNotFoundException localClassNotFoundException2) {
/* 215 */         localObject2 = ClassLoader.getSystemClassLoader();
/* 216 */         if (localObject2 != null) {
/* 217 */           localClass = ((ClassLoader)localObject2).loadClass(str);
/*     */         }
/*     */       }
/*     */ 
/* 221 */       Object localObject1 = null;
/*     */ 
/* 224 */       if (localClass != null)
/* 225 */         localObject1 = localClass.newInstance();
/* 226 */       if ((localObject1 instanceof PKCS8Key)) {
/* 227 */         localObject2 = (PKCS8Key)localObject1;
/* 228 */         ((PKCS8Key)localObject2).algid = paramAlgorithmId;
/* 229 */         ((PKCS8Key)localObject2).key = paramArrayOfByte;
/* 230 */         ((PKCS8Key)localObject2).parseKeyBits();
/* 231 */         return localObject2;
/*     */       }
/*     */     } catch (ClassNotFoundException localClassNotFoundException1) {
/*     */     } catch (InstantiationException localInstantiationException) {
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 237 */       throw new IOException(str + " [internal error]");
/*     */     }
/*     */ 
/* 240 */     PKCS8Key localPKCS8Key = new PKCS8Key();
/* 241 */     localPKCS8Key.algid = paramAlgorithmId;
/* 242 */     localPKCS8Key.key = paramArrayOfByte;
/* 243 */     return localPKCS8Key;
/*     */   }
/*     */ 
/*     */   public String getAlgorithm()
/*     */   {
/* 250 */     return this.algid.getName();
/*     */   }
/*     */ 
/*     */   public AlgorithmId getAlgorithmId()
/*     */   {
/* 256 */     return this.algid;
/*     */   }
/*     */ 
/*     */   public final void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 263 */     encode(paramDerOutputStream, this.algid, this.key);
/*     */   }
/*     */ 
/*     */   public synchronized byte[] getEncoded()
/*     */   {
/* 270 */     byte[] arrayOfByte = null;
/*     */     try {
/* 272 */       arrayOfByte = encode();
/*     */     } catch (InvalidKeyException localInvalidKeyException) {
/*     */     }
/* 275 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public String getFormat()
/*     */   {
/* 282 */     return "PKCS#8";
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */     throws InvalidKeyException
/*     */   {
/* 291 */     if (this.encodedKey == null)
/*     */     {
/*     */       try
/*     */       {
/* 295 */         DerOutputStream localDerOutputStream = new DerOutputStream();
/* 296 */         encode(localDerOutputStream);
/* 297 */         this.encodedKey = localDerOutputStream.toByteArray();
/*     */       }
/*     */       catch (IOException localIOException) {
/* 300 */         throw new InvalidKeyException("IOException : " + localIOException.getMessage());
/*     */       }
/*     */     }
/*     */ 
/* 304 */     return (byte[])this.encodedKey.clone();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 312 */     HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*     */ 
/* 314 */     return "algorithm = " + this.algid.toString() + ", unparsed keybits = \n" + localHexDumpEncoder.encodeBuffer(this.key);
/*     */   }
/*     */ 
/*     */   public void decode(InputStream paramInputStream)
/*     */     throws InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 339 */       DerValue localDerValue = new DerValue(paramInputStream);
/* 340 */       if (localDerValue.tag != 48) {
/* 341 */         throw new InvalidKeyException("invalid key format");
/*     */       }
/*     */ 
/* 344 */       BigInteger localBigInteger = localDerValue.data.getBigInteger();
/* 345 */       if (!localBigInteger.equals(version)) {
/* 346 */         throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(localBigInteger));
/*     */       }
/*     */ 
/* 351 */       this.algid = AlgorithmId.parse(localDerValue.data.getDerValue());
/* 352 */       this.key = localDerValue.data.getOctetString();
/* 353 */       parseKeyBits();
/*     */ 
/* 355 */       if (localDerValue.data.available() == 0);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 361 */       throw new InvalidKeyException("IOException : " + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void decode(byte[] paramArrayOfByte) throws InvalidKeyException
/*     */   {
/* 367 */     decode(new ByteArrayInputStream(paramArrayOfByte));
/*     */   }
/*     */ 
/*     */   protected Object writeReplace() throws ObjectStreamException {
/* 371 */     return new KeyRep(KeyRep.Type.PRIVATE, getAlgorithm(), getFormat(), getEncoded());
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 385 */       decode(paramObjectInputStream);
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 388 */       localInvalidKeyException.printStackTrace();
/* 389 */       throw new IOException("deserialized key is invalid: " + localInvalidKeyException.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   static void encode(DerOutputStream paramDerOutputStream, AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 399 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 400 */     localDerOutputStream.putInteger(version);
/* 401 */     paramAlgorithmId.encode(localDerOutputStream);
/* 402 */     localDerOutputStream.putOctetString(paramArrayOfByte);
/* 403 */     paramDerOutputStream.write((byte)48, localDerOutputStream);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 417 */     if (this == paramObject) {
/* 418 */       return true;
/*     */     }
/*     */ 
/* 421 */     if ((paramObject instanceof Key))
/*     */     {
/*     */       byte[] arrayOfByte1;
/* 425 */       if (this.encodedKey != null)
/* 426 */         arrayOfByte1 = this.encodedKey;
/*     */       else {
/* 428 */         arrayOfByte1 = getEncoded();
/*     */       }
/*     */ 
/* 432 */       byte[] arrayOfByte2 = ((Key)paramObject).getEncoded();
/*     */ 
/* 436 */       if (arrayOfByte1.length != arrayOfByte2.length)
/* 437 */         return false;
/* 438 */       for (int i = 0; i < arrayOfByte1.length; i++) {
/* 439 */         if (arrayOfByte1[i] != arrayOfByte2[i]) {
/* 440 */           return false;
/*     */         }
/*     */       }
/* 443 */       return true;
/*     */     }
/*     */ 
/* 446 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 454 */     int i = 0;
/* 455 */     byte[] arrayOfByte = getEncoded();
/*     */ 
/* 457 */     for (int j = 1; j < arrayOfByte.length; j++) {
/* 458 */       i += arrayOfByte[j] * j;
/*     */     }
/* 460 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.pkcs.PKCS8Key
 * JD-Core Version:    0.6.2
 */