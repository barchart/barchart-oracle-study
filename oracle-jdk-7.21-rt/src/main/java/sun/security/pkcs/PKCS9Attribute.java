/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.CertificateExtensions;
/*     */ 
/*     */ public class PKCS9Attribute
/*     */   implements DerEncoder
/*     */ {
/* 181 */   private static final Debug debug = Debug.getInstance("jar");
/*     */ 
/* 186 */   static final ObjectIdentifier[] PKCS9_OIDS = new ObjectIdentifier[18];
/*     */   public static final ObjectIdentifier EMAIL_ADDRESS_OID;
/*     */   public static final ObjectIdentifier UNSTRUCTURED_NAME_OID;
/*     */   public static final ObjectIdentifier CONTENT_TYPE_OID;
/*     */   public static final ObjectIdentifier MESSAGE_DIGEST_OID;
/*     */   public static final ObjectIdentifier SIGNING_TIME_OID;
/*     */   public static final ObjectIdentifier COUNTERSIGNATURE_OID;
/*     */   public static final ObjectIdentifier CHALLENGE_PASSWORD_OID;
/*     */   public static final ObjectIdentifier UNSTRUCTURED_ADDRESS_OID;
/*     */   public static final ObjectIdentifier EXTENDED_CERTIFICATE_ATTRIBUTES_OID;
/*     */   public static final ObjectIdentifier ISSUER_SERIALNUMBER_OID;
/*     */   public static final ObjectIdentifier EXTENSION_REQUEST_OID;
/*     */   public static final ObjectIdentifier SMIME_CAPABILITY_OID;
/*     */   public static final ObjectIdentifier SIGNING_CERTIFICATE_OID;
/*     */   public static final ObjectIdentifier SIGNATURE_TIMESTAMP_TOKEN_OID;
/*     */   public static final String EMAIL_ADDRESS_STR = "EmailAddress";
/*     */   public static final String UNSTRUCTURED_NAME_STR = "UnstructuredName";
/*     */   public static final String CONTENT_TYPE_STR = "ContentType";
/*     */   public static final String MESSAGE_DIGEST_STR = "MessageDigest";
/*     */   public static final String SIGNING_TIME_STR = "SigningTime";
/*     */   public static final String COUNTERSIGNATURE_STR = "Countersignature";
/*     */   public static final String CHALLENGE_PASSWORD_STR = "ChallengePassword";
/*     */   public static final String UNSTRUCTURED_ADDRESS_STR = "UnstructuredAddress";
/*     */   public static final String EXTENDED_CERTIFICATE_ATTRIBUTES_STR = "ExtendedCertificateAttributes";
/*     */   public static final String ISSUER_SERIALNUMBER_STR = "IssuerAndSerialNumber";
/*     */   private static final String RSA_PROPRIETARY_STR = "RSAProprietary";
/*     */   private static final String SMIME_SIGNING_DESC_STR = "SMIMESigningDesc";
/*     */   public static final String EXTENSION_REQUEST_STR = "ExtensionRequest";
/*     */   public static final String SMIME_CAPABILITY_STR = "SMIMECapability";
/*     */   public static final String SIGNING_CERTIFICATE_STR = "SigningCertificate";
/*     */   public static final String SIGNATURE_TIMESTAMP_TOKEN_STR = "SignatureTimestampToken";
/*     */   private static final Hashtable<String, ObjectIdentifier> NAME_OID_TABLE;
/*     */   private static final Hashtable<ObjectIdentifier, String> OID_NAME_TABLE;
/*     */   private static final Byte[][] PKCS9_VALUE_TAGS;
/*     */   private static final Class[] VALUE_CLASSES;
/* 360 */   private static final boolean[] SINGLE_VALUED = { false, false, false, true, true, true, false, true, false, false, true, false, false, false, true, true, true, true };
/*     */   private int index;
/*     */   private Object value;
/*     */ 
/*     */   public PKCS9Attribute(ObjectIdentifier paramObjectIdentifier, Object paramObject)
/*     */     throws IllegalArgumentException
/*     */   {
/* 406 */     init(paramObjectIdentifier, paramObject);
/*     */   }
/*     */ 
/*     */   public PKCS9Attribute(String paramString, Object paramObject)
/*     */     throws IllegalArgumentException
/*     */   {
/* 427 */     ObjectIdentifier localObjectIdentifier = getOID(paramString);
/*     */ 
/* 429 */     if (localObjectIdentifier == null) {
/* 430 */       throw new IllegalArgumentException("Unrecognized attribute name " + paramString + " constructing PKCS9Attribute.");
/*     */     }
/*     */ 
/* 434 */     init(localObjectIdentifier, paramObject);
/*     */   }
/*     */ 
/*     */   private void init(ObjectIdentifier paramObjectIdentifier, Object paramObject)
/*     */     throws IllegalArgumentException
/*     */   {
/* 440 */     this.index = indexOf(paramObjectIdentifier, PKCS9_OIDS, 1);
/*     */ 
/* 442 */     if (this.index == -1) {
/* 443 */       throw new IllegalArgumentException("Unsupported OID " + paramObjectIdentifier + " constructing PKCS9Attribute.");
/*     */     }
/*     */ 
/* 447 */     if (!VALUE_CLASSES[this.index].isInstance(paramObject)) {
/* 448 */       throw new IllegalArgumentException("Wrong value class  for attribute " + paramObjectIdentifier + " constructing PKCS9Attribute; was " + paramObject.getClass().toString() + ", should be " + VALUE_CLASSES[this.index].toString());
/*     */     }
/*     */ 
/* 455 */     this.value = paramObject;
/*     */   }
/*     */ 
/*     */   public PKCS9Attribute(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 468 */     DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
/* 469 */     DerValue[] arrayOfDerValue1 = localDerInputStream.getSequence(2);
/*     */ 
/* 471 */     if (localDerInputStream.available() != 0) {
/* 472 */       throw new IOException("Excess data parsing PKCS9Attribute");
/*     */     }
/* 474 */     if (arrayOfDerValue1.length != 2) {
/* 475 */       throw new IOException("PKCS9Attribute doesn't have two components");
/*     */     }
/*     */ 
/* 478 */     ObjectIdentifier localObjectIdentifier = arrayOfDerValue1[0].getOID();
/* 479 */     this.index = indexOf(localObjectIdentifier, PKCS9_OIDS, 1);
/* 480 */     if (this.index == -1) {
/* 481 */       if (debug != null) {
/* 482 */         debug.println("ignoring unsupported signer attribute: " + localObjectIdentifier);
/*     */       }
/* 484 */       throw new ParsingException("Unsupported PKCS9 attribute: " + localObjectIdentifier);
/*     */     }
/*     */ 
/* 487 */     DerValue[] arrayOfDerValue2 = new DerInputStream(arrayOfDerValue1[1].toByteArray()).getSet(1);
/*     */ 
/* 489 */     if ((SINGLE_VALUED[this.index] != 0) && (arrayOfDerValue2.length > 1)) {
/* 490 */       throwSingleValuedException();
/*     */     }
/*     */ 
/* 494 */     for (int i = 0; i < arrayOfDerValue2.length; i++) {
/* 495 */       Byte localByte = new Byte(arrayOfDerValue2[i].tag);
/*     */ 
/* 497 */       if (indexOf(localByte, PKCS9_VALUE_TAGS[this.index], 0) == -1)
/* 498 */         throwTagException(localByte);
/*     */     }
/*     */     Object localObject;
/*     */     int j;
/* 501 */     switch (this.index)
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 8:
/* 506 */       localObject = new String[arrayOfDerValue2.length];
/*     */ 
/* 508 */       for (j = 0; j < arrayOfDerValue2.length; j++)
/* 509 */         localObject[j] = arrayOfDerValue2[j].getAsString();
/* 510 */       this.value = localObject;
/*     */ 
/* 512 */       break;
/*     */     case 3:
/* 515 */       this.value = arrayOfDerValue2[0].getOID();
/* 516 */       break;
/*     */     case 4:
/* 519 */       this.value = arrayOfDerValue2[0].getOctetString();
/* 520 */       break;
/*     */     case 5:
/* 523 */       this.value = new DerInputStream(arrayOfDerValue2[0].toByteArray()).getUTCTime();
/* 524 */       break;
/*     */     case 6:
/* 528 */       localObject = new SignerInfo[arrayOfDerValue2.length];
/* 529 */       for (j = 0; j < arrayOfDerValue2.length; j++) {
/* 530 */         localObject[j] = new SignerInfo(arrayOfDerValue2[j].toDerInputStream());
/*     */       }
/* 532 */       this.value = localObject;
/*     */ 
/* 534 */       break;
/*     */     case 7:
/* 537 */       this.value = arrayOfDerValue2[0].getAsString();
/* 538 */       break;
/*     */     case 9:
/* 541 */       throw new IOException("PKCS9 extended-certificate attribute not supported.");
/*     */     case 10:
/* 545 */       throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
/*     */     case 11:
/*     */     case 12:
/* 550 */       throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
/*     */     case 13:
/* 554 */       throw new IOException("PKCS9 attribute #13 not supported.");
/*     */     case 14:
/* 558 */       this.value = new CertificateExtensions(new DerInputStream(arrayOfDerValue2[0].toByteArray()));
/*     */ 
/* 560 */       break;
/*     */     case 15:
/* 563 */       throw new IOException("PKCS9 SMIMECapability attribute not supported.");
/*     */     case 16:
/* 567 */       this.value = new SigningCertificateInfo(arrayOfDerValue2[0].toByteArray());
/* 568 */       break;
/*     */     case 17:
/* 571 */       this.value = arrayOfDerValue2[0].toByteArray();
/* 572 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void derEncode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 586 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 587 */     localDerOutputStream.putOID(getOID());
/*     */     Object localObject2;
/*     */     int i;
/* 588 */     switch (this.index)
/*     */     {
/*     */     case 1:
/*     */     case 2:
/* 592 */       localObject1 = (String[])this.value;
/* 593 */       localObject2 = new DerOutputStream[localObject1.length];
/*     */ 
/* 596 */       for (i = 0; i < localObject1.length; i++) {
/* 597 */         localObject2[i] = new DerOutputStream();
/* 598 */         localObject2[i].putIA5String(localObject1[i]);
/*     */       }
/* 600 */       localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])localObject2);
/*     */ 
/* 602 */       break;
/*     */     case 3:
/* 606 */       localObject1 = new DerOutputStream();
/* 607 */       ((DerOutputStream)localObject1).putOID((ObjectIdentifier)this.value);
/* 608 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */ 
/* 610 */       break;
/*     */     case 4:
/* 614 */       localObject1 = new DerOutputStream();
/* 615 */       ((DerOutputStream)localObject1).putOctetString((byte[])this.value);
/* 616 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */ 
/* 618 */       break;
/*     */     case 5:
/* 622 */       localObject1 = new DerOutputStream();
/* 623 */       ((DerOutputStream)localObject1).putUTCTime((Date)this.value);
/* 624 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */ 
/* 626 */       break;
/*     */     case 6:
/* 629 */       localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])this.value);
/* 630 */       break;
/*     */     case 7:
/* 634 */       localObject1 = new DerOutputStream();
/* 635 */       ((DerOutputStream)localObject1).putPrintableString((String)this.value);
/* 636 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */ 
/* 638 */       break;
/*     */     case 8:
/* 642 */       localObject1 = (String[])this.value;
/* 643 */       localObject2 = new DerOutputStream[localObject1.length];
/*     */ 
/* 646 */       for (i = 0; i < localObject1.length; i++) {
/* 647 */         localObject2[i] = new DerOutputStream();
/* 648 */         localObject2[i].putPrintableString(localObject1[i]);
/*     */       }
/* 650 */       localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])localObject2);
/*     */ 
/* 652 */       break;
/*     */     case 9:
/* 655 */       throw new IOException("PKCS9 extended-certificate attribute not supported.");
/*     */     case 10:
/* 659 */       throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
/*     */     case 11:
/*     */     case 12:
/* 664 */       throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
/*     */     case 13:
/* 668 */       throw new IOException("PKCS9 attribute #13 not supported.");
/*     */     case 14:
/* 673 */       localObject1 = new DerOutputStream();
/* 674 */       localObject2 = (CertificateExtensions)this.value;
/*     */       try {
/* 676 */         ((CertificateExtensions)localObject2).encode((OutputStream)localObject1, true);
/*     */       } catch (CertificateException localCertificateException) {
/* 678 */         throw new IOException(localCertificateException.toString());
/*     */       }
/* 680 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */ 
/* 682 */       break;
/*     */     case 15:
/* 684 */       throw new IOException("PKCS9 attribute #15 not supported.");
/*     */     case 16:
/* 688 */       throw new IOException("PKCS9 SigningCertificate attribute not supported.");
/*     */     case 17:
/* 693 */       localDerOutputStream.write((byte)49, (byte[])this.value);
/* 694 */       break;
/*     */     }
/*     */ 
/* 699 */     Object localObject1 = new DerOutputStream();
/* 700 */     ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream.toByteArray());
/*     */ 
/* 702 */     paramOutputStream.write(((DerOutputStream)localObject1).toByteArray());
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 717 */     return this.value;
/*     */   }
/*     */ 
/*     */   public boolean isSingleValued()
/*     */   {
/* 724 */     return SINGLE_VALUED[this.index];
/*     */   }
/*     */ 
/*     */   public ObjectIdentifier getOID()
/*     */   {
/* 731 */     return PKCS9_OIDS[this.index];
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 738 */     return (String)OID_NAME_TABLE.get(PKCS9_OIDS[this.index]);
/*     */   }
/*     */ 
/*     */   public static ObjectIdentifier getOID(String paramString)
/*     */   {
/* 746 */     return (ObjectIdentifier)NAME_OID_TABLE.get(paramString.toLowerCase(Locale.ENGLISH));
/*     */   }
/*     */ 
/*     */   public static String getName(ObjectIdentifier paramObjectIdentifier)
/*     */   {
/* 754 */     return (String)OID_NAME_TABLE.get(paramObjectIdentifier);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 761 */     StringBuffer localStringBuffer = new StringBuffer(100);
/*     */ 
/* 763 */     localStringBuffer.append("[");
/*     */ 
/* 765 */     localStringBuffer.append((String)OID_NAME_TABLE.get(PKCS9_OIDS[this.index]));
/* 766 */     localStringBuffer.append(": ");
/*     */ 
/* 768 */     if (SINGLE_VALUED[this.index] != 0) {
/* 769 */       if ((this.value instanceof byte[])) {
/* 770 */         HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/* 771 */         localStringBuffer.append(localHexDumpEncoder.encodeBuffer((byte[])this.value));
/*     */       } else {
/* 773 */         localStringBuffer.append(this.value.toString());
/*     */       }
/* 775 */       localStringBuffer.append("]");
/* 776 */       return localStringBuffer.toString();
/*     */     }
/* 778 */     int i = 1;
/* 779 */     Object[] arrayOfObject = (Object[])this.value;
/*     */ 
/* 781 */     for (int j = 0; j < arrayOfObject.length; j++) {
/* 782 */       if (i != 0)
/* 783 */         i = 0;
/*     */       else {
/* 785 */         localStringBuffer.append(", ");
/*     */       }
/* 787 */       localStringBuffer.append(arrayOfObject[j].toString());
/*     */     }
/* 789 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   static int indexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt)
/*     */   {
/* 800 */     for (int i = paramInt; i < paramArrayOfObject.length; i++) {
/* 801 */       if (paramObject.equals(paramArrayOfObject[i])) return i;
/*     */     }
/* 803 */     return -1;
/*     */   }
/*     */ 
/*     */   private void throwSingleValuedException()
/*     */     throws IOException
/*     */   {
/* 811 */     throw new IOException("Single-value attribute " + getOID() + " (" + getName() + ")" + " has multiple values.");
/*     */   }
/*     */ 
/*     */   private void throwTagException(Byte paramByte)
/*     */     throws IOException
/*     */   {
/* 822 */     Byte[] arrayOfByte = PKCS9_VALUE_TAGS[this.index];
/* 823 */     StringBuffer localStringBuffer = new StringBuffer(100);
/* 824 */     localStringBuffer.append("Value of attribute ");
/* 825 */     localStringBuffer.append(getOID().toString());
/* 826 */     localStringBuffer.append(" (");
/* 827 */     localStringBuffer.append(getName());
/* 828 */     localStringBuffer.append(") has wrong tag: ");
/* 829 */     localStringBuffer.append(paramByte.toString());
/* 830 */     localStringBuffer.append(".  Expected tags: ");
/*     */ 
/* 832 */     localStringBuffer.append(arrayOfByte[0].toString());
/*     */ 
/* 834 */     for (int i = 1; i < arrayOfByte.length; i++) {
/* 835 */       localStringBuffer.append(", ");
/* 836 */       localStringBuffer.append(arrayOfByte[i].toString());
/*     */     }
/* 838 */     localStringBuffer.append(".");
/* 839 */     throw new IOException(localStringBuffer.toString());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 189 */     for (int i = 1; i < PKCS9_OIDS.length - 2; i++) {
/* 190 */       PKCS9_OIDS[i] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, i });
/*     */     }
/*     */ 
/* 195 */     PKCS9_OIDS[(PKCS9_OIDS.length - 2)] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 12 });
/*     */ 
/* 197 */     PKCS9_OIDS[(PKCS9_OIDS.length - 1)] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 14 });
/*     */ 
/* 202 */     EMAIL_ADDRESS_OID = PKCS9_OIDS[1];
/* 203 */     UNSTRUCTURED_NAME_OID = PKCS9_OIDS[2];
/* 204 */     CONTENT_TYPE_OID = PKCS9_OIDS[3];
/* 205 */     MESSAGE_DIGEST_OID = PKCS9_OIDS[4];
/* 206 */     SIGNING_TIME_OID = PKCS9_OIDS[5];
/* 207 */     COUNTERSIGNATURE_OID = PKCS9_OIDS[6];
/* 208 */     CHALLENGE_PASSWORD_OID = PKCS9_OIDS[7];
/* 209 */     UNSTRUCTURED_ADDRESS_OID = PKCS9_OIDS[8];
/* 210 */     EXTENDED_CERTIFICATE_ATTRIBUTES_OID = PKCS9_OIDS[9];
/*     */ 
/* 212 */     ISSUER_SERIALNUMBER_OID = PKCS9_OIDS[10];
/*     */ 
/* 215 */     EXTENSION_REQUEST_OID = PKCS9_OIDS[14];
/* 216 */     SMIME_CAPABILITY_OID = PKCS9_OIDS[15];
/* 217 */     SIGNING_CERTIFICATE_OID = PKCS9_OIDS[16];
/* 218 */     SIGNATURE_TIMESTAMP_TOKEN_OID = PKCS9_OIDS[17];
/*     */ 
/* 246 */     NAME_OID_TABLE = new Hashtable(18);
/*     */ 
/* 250 */     NAME_OID_TABLE.put("emailaddress", PKCS9_OIDS[1]);
/* 251 */     NAME_OID_TABLE.put("unstructuredname", PKCS9_OIDS[2]);
/* 252 */     NAME_OID_TABLE.put("contenttype", PKCS9_OIDS[3]);
/* 253 */     NAME_OID_TABLE.put("messagedigest", PKCS9_OIDS[4]);
/* 254 */     NAME_OID_TABLE.put("signingtime", PKCS9_OIDS[5]);
/* 255 */     NAME_OID_TABLE.put("countersignature", PKCS9_OIDS[6]);
/* 256 */     NAME_OID_TABLE.put("challengepassword", PKCS9_OIDS[7]);
/* 257 */     NAME_OID_TABLE.put("unstructuredaddress", PKCS9_OIDS[8]);
/* 258 */     NAME_OID_TABLE.put("extendedcertificateattributes", PKCS9_OIDS[9]);
/* 259 */     NAME_OID_TABLE.put("issuerandserialnumber", PKCS9_OIDS[10]);
/* 260 */     NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[11]);
/* 261 */     NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[12]);
/* 262 */     NAME_OID_TABLE.put("signingdescription", PKCS9_OIDS[13]);
/* 263 */     NAME_OID_TABLE.put("extensionrequest", PKCS9_OIDS[14]);
/* 264 */     NAME_OID_TABLE.put("smimecapability", PKCS9_OIDS[15]);
/* 265 */     NAME_OID_TABLE.put("signingcertificate", PKCS9_OIDS[16]);
/* 266 */     NAME_OID_TABLE.put("signaturetimestamptoken", PKCS9_OIDS[17]);
/*     */ 
/* 273 */     OID_NAME_TABLE = new Hashtable(16);
/*     */ 
/* 276 */     OID_NAME_TABLE.put(PKCS9_OIDS[1], "EmailAddress");
/* 277 */     OID_NAME_TABLE.put(PKCS9_OIDS[2], "UnstructuredName");
/* 278 */     OID_NAME_TABLE.put(PKCS9_OIDS[3], "ContentType");
/* 279 */     OID_NAME_TABLE.put(PKCS9_OIDS[4], "MessageDigest");
/* 280 */     OID_NAME_TABLE.put(PKCS9_OIDS[5], "SigningTime");
/* 281 */     OID_NAME_TABLE.put(PKCS9_OIDS[6], "Countersignature");
/* 282 */     OID_NAME_TABLE.put(PKCS9_OIDS[7], "ChallengePassword");
/* 283 */     OID_NAME_TABLE.put(PKCS9_OIDS[8], "UnstructuredAddress");
/* 284 */     OID_NAME_TABLE.put(PKCS9_OIDS[9], "ExtendedCertificateAttributes");
/* 285 */     OID_NAME_TABLE.put(PKCS9_OIDS[10], "IssuerAndSerialNumber");
/* 286 */     OID_NAME_TABLE.put(PKCS9_OIDS[11], "RSAProprietary");
/* 287 */     OID_NAME_TABLE.put(PKCS9_OIDS[12], "RSAProprietary");
/* 288 */     OID_NAME_TABLE.put(PKCS9_OIDS[13], "SMIMESigningDesc");
/* 289 */     OID_NAME_TABLE.put(PKCS9_OIDS[14], "ExtensionRequest");
/* 290 */     OID_NAME_TABLE.put(PKCS9_OIDS[15], "SMIMECapability");
/* 291 */     OID_NAME_TABLE.put(PKCS9_OIDS[16], "SigningCertificate");
/* 292 */     OID_NAME_TABLE.put(PKCS9_OIDS[17], "SignatureTimestampToken");
/*     */ 
/* 300 */     PKCS9_VALUE_TAGS = new Byte[][] { null, { new Byte(22) }, { new Byte(22) }, { new Byte(6) }, { new Byte(4) }, { new Byte(23) }, { new Byte(48) }, { new Byte(19), new Byte(20) }, { new Byte(19), new Byte(20) }, { new Byte(49) }, { new Byte(48) }, null, null, null, { new Byte(48) }, { new Byte(48) }, { new Byte(48) }, { new Byte(48) } };
/*     */ 
/* 323 */     VALUE_CLASSES = new Class[18];
/*     */     try
/*     */     {
/* 327 */       Class localClass = Class.forName("[Ljava.lang.String;");
/*     */ 
/* 329 */       VALUE_CLASSES[0] = null;
/* 330 */       VALUE_CLASSES[1] = localClass;
/* 331 */       VALUE_CLASSES[2] = localClass;
/* 332 */       VALUE_CLASSES[3] = Class.forName("sun.security.util.ObjectIdentifier");
/*     */ 
/* 334 */       VALUE_CLASSES[4] = Class.forName("[B");
/* 335 */       VALUE_CLASSES[5] = Class.forName("java.util.Date");
/* 336 */       VALUE_CLASSES[6] = Class.forName("[Lsun.security.pkcs.SignerInfo;");
/*     */ 
/* 338 */       VALUE_CLASSES[7] = Class.forName("java.lang.String");
/*     */ 
/* 340 */       VALUE_CLASSES[8] = localClass;
/* 341 */       VALUE_CLASSES[9] = null;
/* 342 */       VALUE_CLASSES[10] = null;
/* 343 */       VALUE_CLASSES[11] = null;
/* 344 */       VALUE_CLASSES[12] = null;
/* 345 */       VALUE_CLASSES[13] = null;
/* 346 */       VALUE_CLASSES[14] = Class.forName("sun.security.x509.CertificateExtensions");
/*     */ 
/* 348 */       VALUE_CLASSES[15] = null;
/* 349 */       VALUE_CLASSES[16] = null;
/* 350 */       VALUE_CLASSES[17] = Class.forName("[B");
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 352 */       throw new ExceptionInInitializerError(localClassNotFoundException.toString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.pkcs.PKCS9Attribute
 * JD-Core Version:    0.6.2
 */