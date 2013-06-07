/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Enumeration;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ public class KeyUsageExtension extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.KeyUsage";
/*     */   public static final String NAME = "KeyUsage";
/*     */   public static final String DIGITAL_SIGNATURE = "digital_signature";
/*     */   public static final String NON_REPUDIATION = "non_repudiation";
/*     */   public static final String KEY_ENCIPHERMENT = "key_encipherment";
/*     */   public static final String DATA_ENCIPHERMENT = "data_encipherment";
/*     */   public static final String KEY_AGREEMENT = "key_agreement";
/*     */   public static final String KEY_CERTSIGN = "key_certsign";
/*     */   public static final String CRL_SIGN = "crl_sign";
/*     */   public static final String ENCIPHER_ONLY = "encipher_only";
/*     */   public static final String DECIPHER_ONLY = "decipher_only";
/*     */   private boolean[] bitString;
/*     */ 
/*     */   private void encodeThis()
/*     */     throws IOException
/*     */   {
/*  75 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*  76 */     localDerOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
/*  77 */     this.extensionValue = localDerOutputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean isSet(int paramInt)
/*     */   {
/*  86 */     return this.bitString[paramInt];
/*     */   }
/*     */ 
/*     */   private void set(int paramInt, boolean paramBoolean)
/*     */   {
/*  94 */     if (paramInt >= this.bitString.length) {
/*  95 */       boolean[] arrayOfBoolean = new boolean[paramInt + 1];
/*  96 */       System.arraycopy(this.bitString, 0, arrayOfBoolean, 0, this.bitString.length);
/*  97 */       this.bitString = arrayOfBoolean;
/*     */     }
/*  99 */     this.bitString[paramInt] = paramBoolean;
/*     */   }
/*     */ 
/*     */   public KeyUsageExtension(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 109 */     this.bitString = new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte).toBooleanArray();
/*     */ 
/* 111 */     this.extensionId = PKIXExtensions.KeyUsage_Id;
/* 112 */     this.critical = true;
/* 113 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public KeyUsageExtension(boolean[] paramArrayOfBoolean)
/*     */     throws IOException
/*     */   {
/* 123 */     this.bitString = paramArrayOfBoolean;
/* 124 */     this.extensionId = PKIXExtensions.KeyUsage_Id;
/* 125 */     this.critical = true;
/* 126 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public KeyUsageExtension(BitArray paramBitArray)
/*     */     throws IOException
/*     */   {
/* 136 */     this.bitString = paramBitArray.toBooleanArray();
/* 137 */     this.extensionId = PKIXExtensions.KeyUsage_Id;
/* 138 */     this.critical = true;
/* 139 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public KeyUsageExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 154 */     this.extensionId = PKIXExtensions.KeyUsage_Id;
/* 155 */     this.critical = paramBoolean.booleanValue();
/*     */ 
/* 165 */     byte[] arrayOfByte = (byte[])paramObject;
/* 166 */     if (arrayOfByte[0] == 4)
/* 167 */       this.extensionValue = new DerValue(arrayOfByte).getOctetString();
/*     */     else {
/* 169 */       this.extensionValue = arrayOfByte;
/*     */     }
/* 171 */     DerValue localDerValue = new DerValue(this.extensionValue);
/* 172 */     this.bitString = localDerValue.getUnalignedBitString().toBooleanArray();
/*     */   }
/*     */ 
/*     */   public KeyUsageExtension()
/*     */   {
/* 179 */     this.extensionId = PKIXExtensions.KeyUsage_Id;
/* 180 */     this.critical = true;
/* 181 */     this.bitString = new boolean[0];
/*     */   }
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 188 */     if (!(paramObject instanceof Boolean)) {
/* 189 */       throw new IOException("Attribute must be of type Boolean.");
/*     */     }
/* 191 */     boolean bool = ((Boolean)paramObject).booleanValue();
/* 192 */     if (paramString.equalsIgnoreCase("digital_signature"))
/* 193 */       set(0, bool);
/* 194 */     else if (paramString.equalsIgnoreCase("non_repudiation"))
/* 195 */       set(1, bool);
/* 196 */     else if (paramString.equalsIgnoreCase("key_encipherment"))
/* 197 */       set(2, bool);
/* 198 */     else if (paramString.equalsIgnoreCase("data_encipherment"))
/* 199 */       set(3, bool);
/* 200 */     else if (paramString.equalsIgnoreCase("key_agreement"))
/* 201 */       set(4, bool);
/* 202 */     else if (paramString.equalsIgnoreCase("key_certsign"))
/* 203 */       set(5, bool);
/* 204 */     else if (paramString.equalsIgnoreCase("crl_sign"))
/* 205 */       set(6, bool);
/* 206 */     else if (paramString.equalsIgnoreCase("encipher_only"))
/* 207 */       set(7, bool);
/* 208 */     else if (paramString.equalsIgnoreCase("decipher_only"))
/* 209 */       set(8, bool);
/*     */     else {
/* 211 */       throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
/*     */     }
/*     */ 
/* 214 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public Object get(String paramString)
/*     */     throws IOException
/*     */   {
/* 221 */     if (paramString.equalsIgnoreCase("digital_signature"))
/* 222 */       return Boolean.valueOf(isSet(0));
/* 223 */     if (paramString.equalsIgnoreCase("non_repudiation"))
/* 224 */       return Boolean.valueOf(isSet(1));
/* 225 */     if (paramString.equalsIgnoreCase("key_encipherment"))
/* 226 */       return Boolean.valueOf(isSet(2));
/* 227 */     if (paramString.equalsIgnoreCase("data_encipherment"))
/* 228 */       return Boolean.valueOf(isSet(3));
/* 229 */     if (paramString.equalsIgnoreCase("key_agreement"))
/* 230 */       return Boolean.valueOf(isSet(4));
/* 231 */     if (paramString.equalsIgnoreCase("key_certsign"))
/* 232 */       return Boolean.valueOf(isSet(5));
/* 233 */     if (paramString.equalsIgnoreCase("crl_sign"))
/* 234 */       return Boolean.valueOf(isSet(6));
/* 235 */     if (paramString.equalsIgnoreCase("encipher_only"))
/* 236 */       return Boolean.valueOf(isSet(7));
/* 237 */     if (paramString.equalsIgnoreCase("decipher_only")) {
/* 238 */       return Boolean.valueOf(isSet(8));
/*     */     }
/* 240 */     throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
/*     */   }
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 249 */     if (paramString.equalsIgnoreCase("digital_signature"))
/* 250 */       set(0, false);
/* 251 */     else if (paramString.equalsIgnoreCase("non_repudiation"))
/* 252 */       set(1, false);
/* 253 */     else if (paramString.equalsIgnoreCase("key_encipherment"))
/* 254 */       set(2, false);
/* 255 */     else if (paramString.equalsIgnoreCase("data_encipherment"))
/* 256 */       set(3, false);
/* 257 */     else if (paramString.equalsIgnoreCase("key_agreement"))
/* 258 */       set(4, false);
/* 259 */     else if (paramString.equalsIgnoreCase("key_certsign"))
/* 260 */       set(5, false);
/* 261 */     else if (paramString.equalsIgnoreCase("crl_sign"))
/* 262 */       set(6, false);
/* 263 */     else if (paramString.equalsIgnoreCase("encipher_only"))
/* 264 */       set(7, false);
/* 265 */     else if (paramString.equalsIgnoreCase("decipher_only"))
/* 266 */       set(8, false);
/*     */     else {
/* 268 */       throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
/*     */     }
/*     */ 
/* 271 */     encodeThis();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 278 */     String str = super.toString() + "KeyUsage [\n";
/*     */     try
/*     */     {
/* 281 */       if (isSet(0)) {
/* 282 */         str = str + "  DigitalSignature\n";
/*     */       }
/* 284 */       if (isSet(1)) {
/* 285 */         str = str + "  Non_repudiation\n";
/*     */       }
/* 287 */       if (isSet(2)) {
/* 288 */         str = str + "  Key_Encipherment\n";
/*     */       }
/* 290 */       if (isSet(3)) {
/* 291 */         str = str + "  Data_Encipherment\n";
/*     */       }
/* 293 */       if (isSet(4)) {
/* 294 */         str = str + "  Key_Agreement\n";
/*     */       }
/* 296 */       if (isSet(5)) {
/* 297 */         str = str + "  Key_CertSign\n";
/*     */       }
/* 299 */       if (isSet(6)) {
/* 300 */         str = str + "  Crl_Sign\n";
/*     */       }
/* 302 */       if (isSet(7)) {
/* 303 */         str = str + "  Encipher_Only\n";
/*     */       }
/* 305 */       if (isSet(8))
/* 306 */         str = str + "  Decipher_Only\n";
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/*     */     }
/* 310 */     str = str + "]\n";
/*     */ 
/* 312 */     return str;
/*     */   }
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 322 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */ 
/* 324 */     if (this.extensionValue == null) {
/* 325 */       this.extensionId = PKIXExtensions.KeyUsage_Id;
/* 326 */       this.critical = true;
/* 327 */       encodeThis();
/*     */     }
/* 329 */     super.encode(localDerOutputStream);
/* 330 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 338 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 339 */     localAttributeNameEnumeration.addElement("digital_signature");
/* 340 */     localAttributeNameEnumeration.addElement("non_repudiation");
/* 341 */     localAttributeNameEnumeration.addElement("key_encipherment");
/* 342 */     localAttributeNameEnumeration.addElement("data_encipherment");
/* 343 */     localAttributeNameEnumeration.addElement("key_agreement");
/* 344 */     localAttributeNameEnumeration.addElement("key_certsign");
/* 345 */     localAttributeNameEnumeration.addElement("crl_sign");
/* 346 */     localAttributeNameEnumeration.addElement("encipher_only");
/* 347 */     localAttributeNameEnumeration.addElement("decipher_only");
/*     */ 
/* 349 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */ 
/*     */   public boolean[] getBits()
/*     */   {
/* 354 */     return (boolean[])this.bitString.clone();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 361 */     return "KeyUsage";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.x509.KeyUsageExtension
 * JD-Core Version:    0.6.2
 */