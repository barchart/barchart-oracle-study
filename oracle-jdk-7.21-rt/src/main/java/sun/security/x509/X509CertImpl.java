/*      */ package sun.security.x509;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.NoSuchProviderException;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.PublicKey;
/*      */ import java.security.Signature;
/*      */ import java.security.SignatureException;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateEncodingException;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateExpiredException;
/*      */ import java.security.cert.CertificateNotYetValidException;
/*      */ import java.security.cert.CertificateParsingException;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.BASE64Decoder;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.provider.X509Factory;
/*      */ import sun.security.util.DerEncoder;
/*      */ import sun.security.util.DerInputStream;
/*      */ import sun.security.util.DerOutputStream;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ 
/*      */ public class X509CertImpl extends X509Certificate
/*      */   implements DerEncoder
/*      */ {
/*      */   private static final long serialVersionUID = -3457612960190864406L;
/*      */   private static final String DOT = ".";
/*      */   public static final String NAME = "x509";
/*      */   public static final String INFO = "info";
/*      */   public static final String ALG_ID = "algorithm";
/*      */   public static final String SIGNATURE = "signature";
/*      */   public static final String SIGNED_CERT = "signed_cert";
/*      */   public static final String SUBJECT_DN = "x509.info.subject.dname";
/*      */   public static final String ISSUER_DN = "x509.info.issuer.dname";
/*      */   public static final String SERIAL_ID = "x509.info.serialNumber.number";
/*      */   public static final String PUBLIC_KEY = "x509.info.key.value";
/*      */   public static final String VERSION = "x509.info.version.number";
/*      */   public static final String SIG_ALG = "x509.algorithm";
/*      */   public static final String SIG = "x509.signature";
/*  127 */   private boolean readOnly = false;
/*      */ 
/*  130 */   private byte[] signedCert = null;
/*  131 */   protected X509CertInfo info = null;
/*  132 */   protected AlgorithmId algId = null;
/*  133 */   protected byte[] signature = null;
/*      */   private static final String KEY_USAGE_OID = "2.5.29.15";
/*      */   private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";
/*      */   private static final String BASIC_CONSTRAINT_OID = "2.5.29.19";
/*      */   private static final String SUBJECT_ALT_NAME_OID = "2.5.29.17";
/*      */   private static final String ISSUER_ALT_NAME_OID = "2.5.29.18";
/*      */   private static final String AUTH_INFO_ACCESS_OID = "1.3.6.1.5.5.7.1.1";
/*      */   private static final int NUM_STANDARD_KEY_USAGE = 9;
/*      */   private Collection<List<?>> subjectAlternativeNames;
/*      */   private Collection<List<?>> issuerAlternativeNames;
/*      */   private List<String> extKeyUsage;
/*      */   private Set<AccessDescription> authInfoAccess;
/*      */   private PublicKey verifiedPublicKey;
/*      */   private String verifiedProvider;
/*      */   private boolean verificationResult;
/*  178 */   private byte[] subjectKeyId = null;
/*      */ 
/*  181 */   private byte[] issuerKeyId = null;
/*      */ 
/*      */   public X509CertImpl()
/*      */   {
/*      */   }
/*      */ 
/*      */   public X509CertImpl(byte[] paramArrayOfByte)
/*      */     throws CertificateException
/*      */   {
/*      */     try
/*      */     {
/*  202 */       parse(new DerValue(paramArrayOfByte));
/*      */     } catch (IOException localIOException) {
/*  204 */       this.signedCert = null;
/*  205 */       throw new CertificateException("Unable to initialize, " + localIOException, localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public X509CertImpl(InputStream paramInputStream)
/*      */     throws CertificateException
/*      */   {
/*  222 */     DerValue localDerValue = null;
/*      */ 
/*  224 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
/*      */     try
/*      */     {
/*  229 */       localBufferedInputStream.mark(2147483647);
/*  230 */       localDerValue = readRFC1421Cert(localBufferedInputStream);
/*      */     }
/*      */     catch (IOException localIOException1) {
/*      */       try {
/*  234 */         localBufferedInputStream.reset();
/*  235 */         localDerValue = new DerValue(localBufferedInputStream);
/*      */       } catch (IOException localIOException3) {
/*  237 */         throw new CertificateException("Input stream must be either DER-encoded bytes or RFC1421 hex-encoded DER-encoded bytes: " + localIOException3.getMessage(), localIOException3);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  245 */       parse(localDerValue);
/*      */     } catch (IOException localIOException2) {
/*  247 */       this.signedCert = null;
/*  248 */       throw new CertificateException("Unable to parse DER value of certificate, " + localIOException2, localIOException2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private DerValue readRFC1421Cert(InputStream paramInputStream)
/*      */     throws IOException
/*      */   {
/*  262 */     DerValue localDerValue = null;
/*  263 */     String str = null;
/*  264 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, "ASCII"));
/*      */     try
/*      */     {
/*  267 */       str = localBufferedReader.readLine();
/*      */     } catch (IOException localIOException1) {
/*  269 */       throw new IOException("Unable to read InputStream: " + localIOException1.getMessage());
/*      */     }
/*      */ 
/*  272 */     if (str.equals("-----BEGIN CERTIFICATE-----"))
/*      */     {
/*  274 */       BASE64Decoder localBASE64Decoder = new BASE64Decoder();
/*  275 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*      */       try {
/*  277 */         while ((str = localBufferedReader.readLine()) != null) {
/*  278 */           if (str.equals("-----END CERTIFICATE-----")) {
/*  279 */             localDerValue = new DerValue(localByteArrayOutputStream.toByteArray());
/*  280 */             break;
/*      */           }
/*  282 */           localByteArrayOutputStream.write(localBASE64Decoder.decodeBuffer(str));
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException2) {
/*  286 */         throw new IOException("Unable to read InputStream: " + localIOException2.getMessage());
/*      */       }
/*      */     }
/*      */     else {
/*  290 */       throw new IOException("InputStream is not RFC1421 hex-encoded DER bytes");
/*      */     }
/*      */ 
/*  293 */     return localDerValue;
/*      */   }
/*      */ 
/*      */   public X509CertImpl(X509CertInfo paramX509CertInfo)
/*      */   {
/*  304 */     this.info = paramX509CertInfo;
/*      */   }
/*      */ 
/*      */   public X509CertImpl(DerValue paramDerValue)
/*      */     throws CertificateException
/*      */   {
/*      */     try
/*      */     {
/*  317 */       parse(paramDerValue);
/*      */     } catch (IOException localIOException) {
/*  319 */       this.signedCert = null;
/*  320 */       throw new CertificateException("Unable to initialize, " + localIOException, localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void encode(OutputStream paramOutputStream)
/*      */     throws CertificateEncodingException
/*      */   {
/*  332 */     if (this.signedCert == null)
/*  333 */       throw new CertificateEncodingException("Null certificate to encode");
/*      */     try
/*      */     {
/*  336 */       paramOutputStream.write((byte[])this.signedCert.clone());
/*      */     } catch (IOException localIOException) {
/*  338 */       throw new CertificateEncodingException(localIOException.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void derEncode(OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*  351 */     if (this.signedCert == null)
/*  352 */       throw new IOException("Null certificate to encode");
/*  353 */     paramOutputStream.write((byte[])this.signedCert.clone());
/*      */   }
/*      */ 
/*      */   public byte[] getEncoded()
/*      */     throws CertificateEncodingException
/*      */   {
/*  365 */     return (byte[])getEncodedInternal().clone();
/*      */   }
/*      */ 
/*      */   public byte[] getEncodedInternal()
/*      */     throws CertificateEncodingException
/*      */   {
/*  374 */     if (this.signedCert == null) {
/*  375 */       throw new CertificateEncodingException("Null certificate to encode");
/*      */     }
/*      */ 
/*  378 */     return this.signedCert;
/*      */   }
/*      */ 
/*      */   public void verify(PublicKey paramPublicKey)
/*      */     throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*  400 */     verify(paramPublicKey, "");
/*      */   }
/*      */ 
/*      */   public synchronized void verify(PublicKey paramPublicKey, String paramString)
/*      */     throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*  422 */     if (paramString == null) {
/*  423 */       paramString = "";
/*      */     }
/*  425 */     if ((this.verifiedPublicKey != null) && (this.verifiedPublicKey.equals(paramPublicKey)))
/*      */     {
/*  428 */       if (paramString.equals(this.verifiedProvider)) {
/*  429 */         if (this.verificationResult) {
/*  430 */           return;
/*      */         }
/*  432 */         throw new SignatureException("Signature does not match.");
/*      */       }
/*      */     }
/*      */ 
/*  436 */     if (this.signedCert == null) {
/*  437 */       throw new CertificateEncodingException("Uninitialized certificate");
/*      */     }
/*      */ 
/*  440 */     Signature localSignature = null;
/*  441 */     if (paramString.length() == 0)
/*  442 */       localSignature = Signature.getInstance(this.algId.getName());
/*      */     else {
/*  444 */       localSignature = Signature.getInstance(this.algId.getName(), paramString);
/*      */     }
/*  446 */     localSignature.initVerify(paramPublicKey);
/*      */ 
/*  448 */     byte[] arrayOfByte = this.info.getEncodedInfo();
/*  449 */     localSignature.update(arrayOfByte, 0, arrayOfByte.length);
/*      */ 
/*  452 */     this.verificationResult = localSignature.verify(this.signature);
/*  453 */     this.verifiedPublicKey = paramPublicKey;
/*  454 */     this.verifiedProvider = paramString;
/*      */ 
/*  456 */     if (!this.verificationResult)
/*  457 */       throw new SignatureException("Signature does not match.");
/*      */   }
/*      */ 
/*      */   public void sign(PrivateKey paramPrivateKey, String paramString)
/*      */     throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*  480 */     sign(paramPrivateKey, paramString, null);
/*      */   }
/*      */ 
/*      */   public void sign(PrivateKey paramPrivateKey, String paramString1, String paramString2)
/*      */     throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*      */     try
/*      */     {
/*  504 */       if (this.readOnly) {
/*  505 */         throw new CertificateEncodingException("cannot over-write existing certificate");
/*      */       }
/*  507 */       Signature localSignature = null;
/*  508 */       if ((paramString2 == null) || (paramString2.length() == 0))
/*  509 */         localSignature = Signature.getInstance(paramString1);
/*      */       else {
/*  511 */         localSignature = Signature.getInstance(paramString1, paramString2);
/*      */       }
/*  513 */       localSignature.initSign(paramPrivateKey);
/*      */ 
/*  516 */       this.algId = AlgorithmId.get(localSignature.getAlgorithm());
/*      */ 
/*  518 */       DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  519 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*      */ 
/*  522 */       this.info.encode(localDerOutputStream2);
/*  523 */       byte[] arrayOfByte = localDerOutputStream2.toByteArray();
/*      */ 
/*  526 */       this.algId.encode(localDerOutputStream2);
/*      */ 
/*  529 */       localSignature.update(arrayOfByte, 0, arrayOfByte.length);
/*  530 */       this.signature = localSignature.sign();
/*  531 */       localDerOutputStream2.putBitString(this.signature);
/*      */ 
/*  534 */       localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*  535 */       this.signedCert = localDerOutputStream1.toByteArray();
/*  536 */       this.readOnly = true;
/*      */     }
/*      */     catch (IOException localIOException) {
/*  539 */       throw new CertificateEncodingException(localIOException.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void checkValidity()
/*      */     throws CertificateExpiredException, CertificateNotYetValidException
/*      */   {
/*  553 */     Date localDate = new Date();
/*  554 */     checkValidity(localDate);
/*      */   }
/*      */ 
/*      */   public void checkValidity(Date paramDate)
/*      */     throws CertificateExpiredException, CertificateNotYetValidException
/*      */   {
/*  573 */     CertificateValidity localCertificateValidity = null;
/*      */     try {
/*  575 */       localCertificateValidity = (CertificateValidity)this.info.get("validity");
/*      */     } catch (Exception localException) {
/*  577 */       throw new CertificateNotYetValidException("Incorrect validity period");
/*      */     }
/*  579 */     if (localCertificateValidity == null)
/*  580 */       throw new CertificateNotYetValidException("Null validity period");
/*  581 */     localCertificateValidity.valid(paramDate);
/*      */   }
/*      */ 
/*      */   public Object get(String paramString)
/*      */     throws CertificateParsingException
/*      */   {
/*  596 */     X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
/*  597 */     String str = localX509AttributeName.getPrefix();
/*  598 */     if (!str.equalsIgnoreCase("x509")) {
/*  599 */       throw new CertificateParsingException("Invalid root of attribute name, expected [x509], received [" + str + "]");
/*      */     }
/*      */ 
/*  603 */     localX509AttributeName = new X509AttributeName(localX509AttributeName.getSuffix());
/*  604 */     str = localX509AttributeName.getPrefix();
/*      */ 
/*  606 */     if (str.equalsIgnoreCase("info")) {
/*  607 */       if (this.info == null) {
/*  608 */         return null;
/*      */       }
/*  610 */       if (localX509AttributeName.getSuffix() != null) {
/*      */         try {
/*  612 */           return this.info.get(localX509AttributeName.getSuffix());
/*      */         } catch (IOException localIOException) {
/*  614 */           throw new CertificateParsingException(localIOException.toString());
/*      */         } catch (CertificateException localCertificateException) {
/*  616 */           throw new CertificateParsingException(localCertificateException.toString());
/*      */         }
/*      */       }
/*  619 */       return this.info;
/*      */     }
/*  621 */     if (str.equalsIgnoreCase("algorithm"))
/*  622 */       return this.algId;
/*  623 */     if (str.equalsIgnoreCase("signature")) {
/*  624 */       if (this.signature != null) {
/*  625 */         return this.signature.clone();
/*      */       }
/*  627 */       return null;
/*  628 */     }if (str.equalsIgnoreCase("signed_cert")) {
/*  629 */       if (this.signedCert != null) {
/*  630 */         return this.signedCert.clone();
/*      */       }
/*  632 */       return null;
/*      */     }
/*  634 */     throw new CertificateParsingException("Attribute name not recognized or get() not allowed for the same: " + str);
/*      */   }
/*      */ 
/*      */   public void set(String paramString, Object paramObject)
/*      */     throws CertificateException, IOException
/*      */   {
/*  650 */     if (this.readOnly) {
/*  651 */       throw new CertificateException("cannot over-write existing certificate");
/*      */     }
/*      */ 
/*  654 */     X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
/*  655 */     String str = localX509AttributeName.getPrefix();
/*  656 */     if (!str.equalsIgnoreCase("x509")) {
/*  657 */       throw new CertificateException("Invalid root of attribute name, expected [x509], received " + str);
/*      */     }
/*      */ 
/*  660 */     localX509AttributeName = new X509AttributeName(localX509AttributeName.getSuffix());
/*  661 */     str = localX509AttributeName.getPrefix();
/*      */ 
/*  663 */     if (str.equalsIgnoreCase("info")) {
/*  664 */       if (localX509AttributeName.getSuffix() == null) {
/*  665 */         if (!(paramObject instanceof X509CertInfo)) {
/*  666 */           throw new CertificateException("Attribute value should be of type X509CertInfo.");
/*      */         }
/*      */ 
/*  669 */         this.info = ((X509CertInfo)paramObject);
/*  670 */         this.signedCert = null;
/*      */       } else {
/*  672 */         this.info.set(localX509AttributeName.getSuffix(), paramObject);
/*  673 */         this.signedCert = null;
/*      */       }
/*      */     }
/*  676 */     else throw new CertificateException("Attribute name not recognized or set() not allowed for the same: " + str);
/*      */   }
/*      */ 
/*      */   public void delete(String paramString)
/*      */     throws CertificateException, IOException
/*      */   {
/*  691 */     if (this.readOnly) {
/*  692 */       throw new CertificateException("cannot over-write existing certificate");
/*      */     }
/*      */ 
/*  695 */     X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
/*  696 */     String str = localX509AttributeName.getPrefix();
/*  697 */     if (!str.equalsIgnoreCase("x509")) {
/*  698 */       throw new CertificateException("Invalid root of attribute name, expected [x509], received " + str);
/*      */     }
/*      */ 
/*  702 */     localX509AttributeName = new X509AttributeName(localX509AttributeName.getSuffix());
/*  703 */     str = localX509AttributeName.getPrefix();
/*      */ 
/*  705 */     if (str.equalsIgnoreCase("info")) {
/*  706 */       if (localX509AttributeName.getSuffix() != null)
/*  707 */         this.info = null;
/*      */       else
/*  709 */         this.info.delete(localX509AttributeName.getSuffix());
/*      */     }
/*  711 */     else if (str.equalsIgnoreCase("algorithm"))
/*  712 */       this.algId = null;
/*  713 */     else if (str.equalsIgnoreCase("signature"))
/*  714 */       this.signature = null;
/*  715 */     else if (str.equalsIgnoreCase("signed_cert"))
/*  716 */       this.signedCert = null;
/*      */     else
/*  718 */       throw new CertificateException("Attribute name not recognized or delete() not allowed for the same: " + str);
/*      */   }
/*      */ 
/*      */   public Enumeration<String> getElements()
/*      */   {
/*  728 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/*  729 */     localAttributeNameEnumeration.addElement("x509.info");
/*  730 */     localAttributeNameEnumeration.addElement("x509.algorithm");
/*  731 */     localAttributeNameEnumeration.addElement("x509.signature");
/*  732 */     localAttributeNameEnumeration.addElement("x509.signed_cert");
/*      */ 
/*  734 */     return localAttributeNameEnumeration.elements();
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  741 */     return "x509";
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  751 */     if ((this.info == null) || (this.algId == null) || (this.signature == null)) {
/*  752 */       return "";
/*      */     }
/*  754 */     StringBuilder localStringBuilder = new StringBuilder();
/*      */ 
/*  756 */     localStringBuilder.append("[\n");
/*  757 */     localStringBuilder.append(this.info.toString() + "\n");
/*  758 */     localStringBuilder.append("  Algorithm: [" + this.algId.toString() + "]\n");
/*      */ 
/*  760 */     HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  761 */     localStringBuilder.append("  Signature:\n" + localHexDumpEncoder.encodeBuffer(this.signature));
/*  762 */     localStringBuilder.append("\n]");
/*      */ 
/*  764 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   public PublicKey getPublicKey()
/*      */   {
/*  775 */     if (this.info == null)
/*  776 */       return null;
/*      */     try {
/*  778 */       return (PublicKey)this.info.get("key.value");
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/*  782 */     return null;
/*      */   }
/*      */ 
/*      */   public int getVersion()
/*      */   {
/*  792 */     if (this.info == null)
/*  793 */       return -1;
/*      */     try {
/*  795 */       int i = ((Integer)this.info.get("version.number")).intValue();
/*      */ 
/*  797 */       return i + 1; } catch (Exception localException) {
/*      */     }
/*  799 */     return -1;
/*      */   }
/*      */ 
/*      */   public BigInteger getSerialNumber()
/*      */   {
/*  809 */     SerialNumber localSerialNumber = getSerialNumberObject();
/*      */ 
/*  811 */     return localSerialNumber != null ? localSerialNumber.getNumber() : null;
/*      */   }
/*      */ 
/*      */   public SerialNumber getSerialNumberObject()
/*      */   {
/*  821 */     if (this.info == null)
/*  822 */       return null;
/*      */     try {
/*  824 */       return (SerialNumber)this.info.get("serialNumber.number");
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*  829 */     return null;
/*      */   }
/*      */ 
/*      */   public Principal getSubjectDN()
/*      */   {
/*  840 */     if (this.info == null)
/*  841 */       return null;
/*      */     try {
/*  843 */       return (Principal)this.info.get("subject.dname");
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*  848 */     return null;
/*      */   }
/*      */ 
/*      */   public X500Principal getSubjectX500Principal()
/*      */   {
/*  858 */     if (this.info == null)
/*  859 */       return null;
/*      */     try
/*      */     {
/*  862 */       return (X500Principal)this.info.get("subject.x500principal");
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*  867 */     return null;
/*      */   }
/*      */ 
/*      */   public Principal getIssuerDN()
/*      */   {
/*  877 */     if (this.info == null)
/*  878 */       return null;
/*      */     try {
/*  880 */       return (Principal)this.info.get("issuer.dname");
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*  885 */     return null;
/*      */   }
/*      */ 
/*      */   public X500Principal getIssuerX500Principal()
/*      */   {
/*  895 */     if (this.info == null)
/*  896 */       return null;
/*      */     try
/*      */     {
/*  899 */       return (X500Principal)this.info.get("issuer.x500principal");
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*  904 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getNotBefore()
/*      */   {
/*  914 */     if (this.info == null)
/*  915 */       return null;
/*      */     try {
/*  917 */       return (Date)this.info.get("validity.notBefore");
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/*  921 */     return null;
/*      */   }
/*      */ 
/*      */   public Date getNotAfter()
/*      */   {
/*  931 */     if (this.info == null)
/*  932 */       return null;
/*      */     try {
/*  934 */       return (Date)this.info.get("validity.notAfter");
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/*  938 */     return null;
/*      */   }
/*      */ 
/*      */   public byte[] getTBSCertificate()
/*      */     throws CertificateEncodingException
/*      */   {
/*  951 */     if (this.info != null) {
/*  952 */       return this.info.getEncodedInfo();
/*      */     }
/*  954 */     throw new CertificateEncodingException("Uninitialized certificate");
/*      */   }
/*      */ 
/*      */   public byte[] getSignature()
/*      */   {
/*  963 */     if (this.signature == null)
/*  964 */       return null;
/*  965 */     byte[] arrayOfByte = new byte[this.signature.length];
/*  966 */     System.arraycopy(this.signature, 0, arrayOfByte, 0, arrayOfByte.length);
/*  967 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   public String getSigAlgName()
/*      */   {
/*  978 */     if (this.algId == null)
/*  979 */       return null;
/*  980 */     return this.algId.getName();
/*      */   }
/*      */ 
/*      */   public String getSigAlgOID()
/*      */   {
/*  990 */     if (this.algId == null)
/*  991 */       return null;
/*  992 */     ObjectIdentifier localObjectIdentifier = this.algId.getOID();
/*  993 */     return localObjectIdentifier.toString();
/*      */   }
/*      */ 
/*      */   public byte[] getSigAlgParams()
/*      */   {
/* 1004 */     if (this.algId == null)
/* 1005 */       return null;
/*      */     try {
/* 1007 */       return this.algId.getEncodedParams(); } catch (IOException localIOException) {
/*      */     }
/* 1009 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean[] getIssuerUniqueID()
/*      */   {
/* 1019 */     if (this.info == null)
/* 1020 */       return null;
/*      */     try {
/* 1022 */       UniqueIdentity localUniqueIdentity = (UniqueIdentity)this.info.get("issuerID.id");
/*      */ 
/* 1025 */       if (localUniqueIdentity == null) {
/* 1026 */         return null;
/*      */       }
/* 1028 */       return localUniqueIdentity.getId(); } catch (Exception localException) {
/*      */     }
/* 1030 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean[] getSubjectUniqueID()
/*      */   {
/* 1040 */     if (this.info == null)
/* 1041 */       return null;
/*      */     try {
/* 1043 */       UniqueIdentity localUniqueIdentity = (UniqueIdentity)this.info.get("subjectID.id");
/*      */ 
/* 1046 */       if (localUniqueIdentity == null) {
/* 1047 */         return null;
/*      */       }
/* 1049 */       return localUniqueIdentity.getId(); } catch (Exception localException) {
/*      */     }
/* 1051 */     return null;
/*      */   }
/*      */ 
/*      */   public AuthorityKeyIdentifierExtension getAuthorityKeyIdentifierExtension()
/*      */   {
/* 1062 */     return (AuthorityKeyIdentifierExtension)getExtension(PKIXExtensions.AuthorityKey_Id);
/*      */   }
/*      */ 
/*      */   public byte[] getIssuerKeyIdentifier()
/*      */   {
/* 1071 */     if (this.issuerKeyId == null) {
/* 1072 */       AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = getAuthorityKeyIdentifierExtension();
/*      */ 
/* 1074 */       if (localAuthorityKeyIdentifierExtension != null) {
/*      */         try
/*      */         {
/* 1077 */           this.issuerKeyId = ((KeyIdentifier)localAuthorityKeyIdentifierExtension.get("key_id")).getIdentifier();
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1085 */         this.issuerKeyId = new byte[0];
/*      */       }
/*      */     }
/*      */ 
/* 1089 */     return this.issuerKeyId.length != 0 ? this.issuerKeyId : null;
/*      */   }
/*      */ 
/*      */   public BasicConstraintsExtension getBasicConstraintsExtension()
/*      */   {
/* 1098 */     return (BasicConstraintsExtension)getExtension(PKIXExtensions.BasicConstraints_Id);
/*      */   }
/*      */ 
/*      */   public CertificatePoliciesExtension getCertificatePoliciesExtension()
/*      */   {
/* 1108 */     return (CertificatePoliciesExtension)getExtension(PKIXExtensions.CertificatePolicies_Id);
/*      */   }
/*      */ 
/*      */   public ExtendedKeyUsageExtension getExtendedKeyUsageExtension()
/*      */   {
/* 1118 */     return (ExtendedKeyUsageExtension)getExtension(PKIXExtensions.ExtendedKeyUsage_Id);
/*      */   }
/*      */ 
/*      */   public IssuerAlternativeNameExtension getIssuerAlternativeNameExtension()
/*      */   {
/* 1128 */     return (IssuerAlternativeNameExtension)getExtension(PKIXExtensions.IssuerAlternativeName_Id);
/*      */   }
/*      */ 
/*      */   public NameConstraintsExtension getNameConstraintsExtension()
/*      */   {
/* 1137 */     return (NameConstraintsExtension)getExtension(PKIXExtensions.NameConstraints_Id);
/*      */   }
/*      */ 
/*      */   public PolicyConstraintsExtension getPolicyConstraintsExtension()
/*      */   {
/* 1147 */     return (PolicyConstraintsExtension)getExtension(PKIXExtensions.PolicyConstraints_Id);
/*      */   }
/*      */ 
/*      */   public PolicyMappingsExtension getPolicyMappingsExtension()
/*      */   {
/* 1157 */     return (PolicyMappingsExtension)getExtension(PKIXExtensions.PolicyMappings_Id);
/*      */   }
/*      */ 
/*      */   public PrivateKeyUsageExtension getPrivateKeyUsageExtension()
/*      */   {
/* 1166 */     return (PrivateKeyUsageExtension)getExtension(PKIXExtensions.PrivateKeyUsage_Id);
/*      */   }
/*      */ 
/*      */   public SubjectAlternativeNameExtension getSubjectAlternativeNameExtension()
/*      */   {
/* 1177 */     return (SubjectAlternativeNameExtension)getExtension(PKIXExtensions.SubjectAlternativeName_Id);
/*      */   }
/*      */ 
/*      */   public SubjectKeyIdentifierExtension getSubjectKeyIdentifierExtension()
/*      */   {
/* 1187 */     return (SubjectKeyIdentifierExtension)getExtension(PKIXExtensions.SubjectKey_Id);
/*      */   }
/*      */ 
/*      */   public byte[] getSubjectKeyIdentifier()
/*      */   {
/* 1196 */     if (this.subjectKeyId == null) {
/* 1197 */       SubjectKeyIdentifierExtension localSubjectKeyIdentifierExtension = getSubjectKeyIdentifierExtension();
/*      */ 
/* 1199 */       if (localSubjectKeyIdentifierExtension != null) {
/*      */         try
/*      */         {
/* 1202 */           this.subjectKeyId = ((KeyIdentifier)localSubjectKeyIdentifierExtension.get("key_id")).getIdentifier();
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1210 */         this.subjectKeyId = new byte[0];
/*      */       }
/*      */     }
/*      */ 
/* 1214 */     return this.subjectKeyId.length != 0 ? this.subjectKeyId : null;
/*      */   }
/*      */ 
/*      */   public CRLDistributionPointsExtension getCRLDistributionPointsExtension()
/*      */   {
/* 1223 */     return (CRLDistributionPointsExtension)getExtension(PKIXExtensions.CRLDistributionPoints_Id);
/*      */   }
/*      */ 
/*      */   public boolean hasUnsupportedCriticalExtension()
/*      */   {
/* 1232 */     if (this.info == null)
/* 1233 */       return false;
/*      */     try {
/* 1235 */       CertificateExtensions localCertificateExtensions = (CertificateExtensions)this.info.get("extensions");
/*      */ 
/* 1237 */       if (localCertificateExtensions == null)
/* 1238 */         return false;
/* 1239 */       return localCertificateExtensions.hasUnsupportedCriticalExtension(); } catch (Exception localException) {
/*      */     }
/* 1241 */     return false;
/*      */   }
/*      */ 
/*      */   public Set<String> getCriticalExtensionOIDs()
/*      */   {
/* 1254 */     if (this.info == null)
/* 1255 */       return null;
/*      */     try
/*      */     {
/* 1258 */       CertificateExtensions localCertificateExtensions = (CertificateExtensions)this.info.get("extensions");
/*      */ 
/* 1260 */       if (localCertificateExtensions == null) {
/* 1261 */         return null;
/*      */       }
/* 1263 */       TreeSet localTreeSet = new TreeSet();
/* 1264 */       for (Extension localExtension : localCertificateExtensions.getAllExtensions()) {
/* 1265 */         if (localExtension.isCritical()) {
/* 1266 */           localTreeSet.add(localExtension.getExtensionId().toString());
/*      */         }
/*      */       }
/* 1269 */       return localTreeSet; } catch (Exception localException) {
/*      */     }
/* 1271 */     return null;
/*      */   }
/*      */ 
/*      */   public Set<String> getNonCriticalExtensionOIDs()
/*      */   {
/* 1284 */     if (this.info == null)
/* 1285 */       return null;
/*      */     try
/*      */     {
/* 1288 */       CertificateExtensions localCertificateExtensions = (CertificateExtensions)this.info.get("extensions");
/*      */ 
/* 1290 */       if (localCertificateExtensions == null) {
/* 1291 */         return null;
/*      */       }
/* 1293 */       TreeSet localTreeSet = new TreeSet();
/* 1294 */       for (Extension localExtension : localCertificateExtensions.getAllExtensions()) {
/* 1295 */         if (!localExtension.isCritical()) {
/* 1296 */           localTreeSet.add(localExtension.getExtensionId().toString());
/*      */         }
/*      */       }
/* 1299 */       localTreeSet.addAll(localCertificateExtensions.getUnparseableExtensions().keySet());
/* 1300 */       return localTreeSet; } catch (Exception localException) {
/*      */     }
/* 1302 */     return null;
/*      */   }
/*      */ 
/*      */   public Extension getExtension(ObjectIdentifier paramObjectIdentifier)
/*      */   {
/* 1314 */     if (this.info == null)
/* 1315 */       return null;
/*      */     try
/*      */     {
/*      */       CertificateExtensions localCertificateExtensions;
/*      */       try {
/* 1320 */         localCertificateExtensions = (CertificateExtensions)this.info.get("extensions");
/*      */       } catch (CertificateException localCertificateException) {
/* 1322 */         return null;
/*      */       }
/* 1324 */       if (localCertificateExtensions == null) {
/* 1325 */         return null;
/*      */       }
/* 1327 */       Extension localExtension1 = localCertificateExtensions.getExtension(paramObjectIdentifier.toString());
/* 1328 */       if (localExtension1 != null) {
/* 1329 */         return localExtension1;
/*      */       }
/* 1331 */       for (Extension localExtension2 : localCertificateExtensions.getAllExtensions()) {
/* 1332 */         if (localExtension2.getExtensionId().equals(paramObjectIdentifier))
/*      */         {
/* 1334 */           return localExtension2;
/*      */         }
/*      */       }
/*      */ 
/* 1338 */       return null;
/*      */     } catch (IOException localIOException) {
/*      */     }
/* 1341 */     return null;
/*      */   }
/*      */ 
/*      */   public Extension getUnparseableExtension(ObjectIdentifier paramObjectIdentifier)
/*      */   {
/* 1346 */     if (this.info == null)
/* 1347 */       return null;
/*      */     try
/*      */     {
/*      */       CertificateExtensions localCertificateExtensions;
/*      */       try {
/* 1352 */         localCertificateExtensions = (CertificateExtensions)this.info.get("extensions");
/*      */       } catch (CertificateException localCertificateException) {
/* 1354 */         return null;
/*      */       }
/* 1356 */       if (localCertificateExtensions == null) {
/* 1357 */         return null;
/*      */       }
/* 1359 */       return (Extension)localCertificateExtensions.getUnparseableExtensions().get(paramObjectIdentifier.toString());
/*      */     } catch (IOException localIOException) {
/*      */     }
/* 1362 */     return null;
/*      */   }
/*      */ 
/*      */   public byte[] getExtensionValue(String paramString)
/*      */   {
/*      */     try
/*      */     {
/* 1374 */       ObjectIdentifier localObjectIdentifier1 = new ObjectIdentifier(paramString);
/* 1375 */       String str = OIDMap.getName(localObjectIdentifier1);
/* 1376 */       Object localObject1 = null;
/* 1377 */       CertificateExtensions localCertificateExtensions = (CertificateExtensions)this.info.get("extensions");
/*      */       Iterator localIterator;
/* 1380 */       if (str == null)
/*      */       {
/* 1382 */         if (localCertificateExtensions == null) {
/* 1383 */           return null;
/*      */         }
/*      */ 
/* 1386 */         for (localIterator = localCertificateExtensions.getAllExtensions().iterator(); localIterator.hasNext(); ) { localObject2 = (Extension)localIterator.next();
/* 1387 */           ObjectIdentifier localObjectIdentifier2 = ((Extension)localObject2).getExtensionId();
/* 1388 */           if (localObjectIdentifier2.equals(localObjectIdentifier1)) {
/* 1389 */             localObject1 = localObject2;
/* 1390 */             break;
/*      */           } }
/*      */       }
/*      */       else {
/*      */         try {
/* 1395 */           localObject1 = (Extension)get(str);
/*      */         }
/*      */         catch (CertificateException localCertificateException) {
/*      */         }
/*      */       }
/* 1400 */       if (localObject1 == null) {
/* 1401 */         if (localCertificateExtensions != null) {
/* 1402 */           localObject1 = (Extension)localCertificateExtensions.getUnparseableExtensions().get(paramString);
/*      */         }
/* 1404 */         if (localObject1 == null) {
/* 1405 */           return null;
/*      */         }
/*      */       }
/* 1408 */       byte[] arrayOfByte = ((Extension)localObject1).getExtensionValue();
/* 1409 */       if (arrayOfByte == null) {
/* 1410 */         return null;
/*      */       }
/* 1412 */       Object localObject2 = new DerOutputStream();
/* 1413 */       ((DerOutputStream)localObject2).putOctetString(arrayOfByte);
/* 1414 */       return ((DerOutputStream)localObject2).toByteArray(); } catch (Exception localException) {
/*      */     }
/* 1416 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean[] getKeyUsage()
/*      */   {
/*      */     try
/*      */     {
/* 1427 */       String str = OIDMap.getName(PKIXExtensions.KeyUsage_Id);
/* 1428 */       if (str == null) {
/* 1429 */         return null;
/*      */       }
/* 1431 */       KeyUsageExtension localKeyUsageExtension = (KeyUsageExtension)get(str);
/* 1432 */       if (localKeyUsageExtension == null) {
/* 1433 */         return null;
/*      */       }
/* 1435 */       Object localObject = localKeyUsageExtension.getBits();
/*      */       boolean[] arrayOfBoolean;
/* 1436 */       if (localObject.length < 9) {
/* 1437 */         arrayOfBoolean = new boolean[9];
/* 1438 */         System.arraycopy(localObject, 0, arrayOfBoolean, 0, localObject.length);
/* 1439 */       }return arrayOfBoolean;
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/* 1443 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized List<String> getExtendedKeyUsage()
/*      */     throws CertificateParsingException
/*      */   {
/* 1455 */     if ((this.readOnly) && (this.extKeyUsage != null)) {
/* 1456 */       return this.extKeyUsage;
/*      */     }
/* 1458 */     ExtendedKeyUsageExtension localExtendedKeyUsageExtension = getExtendedKeyUsageExtension();
/* 1459 */     if (localExtendedKeyUsageExtension == null) {
/* 1460 */       return null;
/*      */     }
/* 1462 */     this.extKeyUsage = Collections.unmodifiableList(localExtendedKeyUsageExtension.getExtendedKeyUsage());
/*      */ 
/* 1464 */     return this.extKeyUsage;
/*      */   }
/*      */ 
/*      */   public static List<String> getExtendedKeyUsage(X509Certificate paramX509Certificate)
/*      */     throws CertificateParsingException
/*      */   {
/*      */     try
/*      */     {
/* 1477 */       byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.37");
/* 1478 */       if (arrayOfByte1 == null)
/* 1479 */         return null;
/* 1480 */       DerValue localDerValue = new DerValue(arrayOfByte1);
/* 1481 */       byte[] arrayOfByte2 = localDerValue.getOctetString();
/*      */ 
/* 1483 */       ExtendedKeyUsageExtension localExtendedKeyUsageExtension = new ExtendedKeyUsageExtension(Boolean.FALSE, arrayOfByte2);
/*      */ 
/* 1485 */       return Collections.unmodifiableList(localExtendedKeyUsageExtension.getExtendedKeyUsage());
/*      */     } catch (IOException localIOException) {
/* 1487 */       throw new CertificateParsingException(localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getBasicConstraints()
/*      */   {
/*      */     try
/*      */     {
/* 1498 */       String str = OIDMap.getName(PKIXExtensions.BasicConstraints_Id);
/* 1499 */       if (str == null)
/* 1500 */         return -1;
/* 1501 */       BasicConstraintsExtension localBasicConstraintsExtension = (BasicConstraintsExtension)get(str);
/*      */ 
/* 1503 */       if (localBasicConstraintsExtension == null) {
/* 1504 */         return -1;
/*      */       }
/* 1506 */       if (((Boolean)localBasicConstraintsExtension.get("is_ca")).booleanValue() == true)
/*      */       {
/* 1508 */         return ((Integer)localBasicConstraintsExtension.get("path_len")).intValue();
/*      */       }
/*      */ 
/* 1511 */       return -1; } catch (Exception localException) {
/*      */     }
/* 1513 */     return -1;
/*      */   }
/*      */ 
/*      */   private static Collection<List<?>> makeAltNames(GeneralNames paramGeneralNames)
/*      */   {
/* 1527 */     if (paramGeneralNames.isEmpty()) {
/* 1528 */       return Collections.emptySet();
/*      */     }
/* 1530 */     ArrayList localArrayList1 = new ArrayList();
/* 1531 */     for (GeneralName localGeneralName : paramGeneralNames.names()) {
/* 1532 */       GeneralNameInterface localGeneralNameInterface = localGeneralName.getName();
/* 1533 */       ArrayList localArrayList2 = new ArrayList(2);
/* 1534 */       localArrayList2.add(Integer.valueOf(localGeneralNameInterface.getType()));
/* 1535 */       switch (localGeneralNameInterface.getType()) {
/*      */       case 1:
/* 1537 */         localArrayList2.add(((RFC822Name)localGeneralNameInterface).getName());
/* 1538 */         break;
/*      */       case 2:
/* 1540 */         localArrayList2.add(((DNSName)localGeneralNameInterface).getName());
/* 1541 */         break;
/*      */       case 4:
/* 1543 */         localArrayList2.add(((X500Name)localGeneralNameInterface).getRFC2253Name());
/* 1544 */         break;
/*      */       case 6:
/* 1546 */         localArrayList2.add(((URIName)localGeneralNameInterface).getName());
/* 1547 */         break;
/*      */       case 7:
/*      */         try {
/* 1550 */           localArrayList2.add(((IPAddressName)localGeneralNameInterface).getName());
/*      */         }
/*      */         catch (IOException localIOException1) {
/* 1553 */           throw new RuntimeException("IPAddress cannot be parsed", localIOException1);
/*      */         }
/*      */ 
/*      */       case 8:
/* 1558 */         localArrayList2.add(((OIDName)localGeneralNameInterface).getOID().toString());
/* 1559 */         break;
/*      */       case 3:
/*      */       case 5:
/*      */       default:
/* 1562 */         DerOutputStream localDerOutputStream = new DerOutputStream();
/*      */         try {
/* 1564 */           localGeneralNameInterface.encode(localDerOutputStream);
/*      */         }
/*      */         catch (IOException localIOException2)
/*      */         {
/* 1568 */           throw new RuntimeException("name cannot be encoded", localIOException2);
/*      */         }
/* 1570 */         localArrayList2.add(localDerOutputStream.toByteArray());
/*      */       }
/*      */ 
/* 1573 */       localArrayList1.add(Collections.unmodifiableList(localArrayList2));
/*      */     }
/* 1575 */     return Collections.unmodifiableCollection(localArrayList1);
/*      */   }
/*      */ 
/*      */   private static Collection<List<?>> cloneAltNames(Collection<List<?>> paramCollection)
/*      */   {
/* 1583 */     int i = 0;
/* 1584 */     for (Object localObject1 = paramCollection.iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (List)((Iterator)localObject1).next();
/* 1585 */       if ((((List)localObject2).get(1) instanceof byte[]))
/*      */       {
/* 1587 */         i = 1;
/*      */       }
/*      */     }
/*      */     Object localObject2;
/* 1590 */     if (i != 0) {
/* 1591 */       localObject1 = new ArrayList();
/* 1592 */       for (localObject2 = paramCollection.iterator(); ((Iterator)localObject2).hasNext(); ) { List localList = (List)((Iterator)localObject2).next();
/* 1593 */         Object localObject3 = localList.get(1);
/* 1594 */         if ((localObject3 instanceof byte[])) {
/* 1595 */           ArrayList localArrayList = new ArrayList(localList);
/*      */ 
/* 1597 */           localArrayList.set(1, ((byte[])localObject3).clone());
/* 1598 */           ((List)localObject1).add(Collections.unmodifiableList(localArrayList));
/*      */         } else {
/* 1600 */           ((List)localObject1).add(localList);
/*      */         }
/*      */       }
/* 1603 */       return Collections.unmodifiableCollection((Collection)localObject1);
/*      */     }
/* 1605 */     return paramCollection;
/*      */   }
/*      */ 
/*      */   public synchronized Collection<List<?>> getSubjectAlternativeNames()
/*      */     throws CertificateParsingException
/*      */   {
/* 1618 */     if ((this.readOnly) && (this.subjectAlternativeNames != null)) {
/* 1619 */       return cloneAltNames(this.subjectAlternativeNames);
/*      */     }
/* 1621 */     SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = getSubjectAlternativeNameExtension();
/*      */ 
/* 1623 */     if (localSubjectAlternativeNameExtension == null)
/* 1624 */       return null;
/*      */     GeneralNames localGeneralNames;
/*      */     try
/*      */     {
/* 1628 */       localGeneralNames = (GeneralNames)localSubjectAlternativeNameExtension.get("subject_name");
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1632 */       return Collections.emptySet();
/*      */     }
/* 1634 */     this.subjectAlternativeNames = makeAltNames(localGeneralNames);
/* 1635 */     return this.subjectAlternativeNames;
/*      */   }
/*      */ 
/*      */   public static Collection<List<?>> getSubjectAlternativeNames(X509Certificate paramX509Certificate)
/*      */     throws CertificateParsingException
/*      */   {
/*      */     try
/*      */     {
/* 1647 */       byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.17");
/* 1648 */       if (arrayOfByte1 == null) {
/* 1649 */         return null;
/*      */       }
/* 1651 */       DerValue localDerValue = new DerValue(arrayOfByte1);
/* 1652 */       byte[] arrayOfByte2 = localDerValue.getOctetString();
/*      */ 
/* 1654 */       SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = new SubjectAlternativeNameExtension(Boolean.FALSE, arrayOfByte2);
/*      */       GeneralNames localGeneralNames;
/*      */       try {
/* 1660 */         localGeneralNames = (GeneralNames)localSubjectAlternativeNameExtension.get("subject_name");
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/* 1664 */         return Collections.emptySet();
/*      */       }
/* 1666 */       return makeAltNames(localGeneralNames);
/*      */     } catch (IOException localIOException1) {
/* 1668 */       throw new CertificateParsingException(localIOException1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized Collection<List<?>> getIssuerAlternativeNames()
/*      */     throws CertificateParsingException
/*      */   {
/* 1681 */     if ((this.readOnly) && (this.issuerAlternativeNames != null)) {
/* 1682 */       return cloneAltNames(this.issuerAlternativeNames);
/*      */     }
/* 1684 */     IssuerAlternativeNameExtension localIssuerAlternativeNameExtension = getIssuerAlternativeNameExtension();
/*      */ 
/* 1686 */     if (localIssuerAlternativeNameExtension == null)
/* 1687 */       return null;
/*      */     GeneralNames localGeneralNames;
/*      */     try
/*      */     {
/* 1691 */       localGeneralNames = (GeneralNames)localIssuerAlternativeNameExtension.get("issuer_name");
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1695 */       return Collections.emptySet();
/*      */     }
/* 1697 */     this.issuerAlternativeNames = makeAltNames(localGeneralNames);
/* 1698 */     return this.issuerAlternativeNames;
/*      */   }
/*      */ 
/*      */   public static Collection<List<?>> getIssuerAlternativeNames(X509Certificate paramX509Certificate)
/*      */     throws CertificateParsingException
/*      */   {
/*      */     try
/*      */     {
/* 1710 */       byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.18");
/* 1711 */       if (arrayOfByte1 == null) {
/* 1712 */         return null;
/* 1715 */       }
/*      */ DerValue localDerValue = new DerValue(arrayOfByte1);
/* 1716 */       byte[] arrayOfByte2 = localDerValue.getOctetString();
/*      */ 
/* 1718 */       IssuerAlternativeNameExtension localIssuerAlternativeNameExtension = new IssuerAlternativeNameExtension(Boolean.FALSE, arrayOfByte2);
/*      */       GeneralNames localGeneralNames;
/*      */       try {
/* 1723 */         localGeneralNames = (GeneralNames)localIssuerAlternativeNameExtension.get("issuer_name");
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/* 1727 */         return Collections.emptySet();
/*      */       }
/* 1729 */       return makeAltNames(localGeneralNames);
/*      */     } catch (IOException localIOException1) {
/* 1731 */       throw new CertificateParsingException(localIOException1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public AuthorityInfoAccessExtension getAuthorityInfoAccessExtension() {
/* 1736 */     return (AuthorityInfoAccessExtension)getExtension(PKIXExtensions.AuthInfoAccess_Id);
/*      */   }
/*      */ 
/*      */   private void parse(DerValue paramDerValue)
/*      */     throws CertificateException, IOException
/*      */   {
/* 1755 */     if (this.readOnly) {
/* 1756 */       throw new CertificateParsingException("cannot over-write existing certificate");
/*      */     }
/*      */ 
/* 1759 */     if ((paramDerValue.data == null) || (paramDerValue.tag != 48)) {
/* 1760 */       throw new CertificateParsingException("invalid DER-encoded certificate data");
/*      */     }
/*      */ 
/* 1763 */     this.signedCert = paramDerValue.toByteArray();
/* 1764 */     DerValue[] arrayOfDerValue = new DerValue[3];
/*      */ 
/* 1766 */     arrayOfDerValue[0] = paramDerValue.data.getDerValue();
/* 1767 */     arrayOfDerValue[1] = paramDerValue.data.getDerValue();
/* 1768 */     arrayOfDerValue[2] = paramDerValue.data.getDerValue();
/*      */ 
/* 1770 */     if (paramDerValue.data.available() != 0) {
/* 1771 */       throw new CertificateParsingException("signed overrun, bytes = " + paramDerValue.data.available());
/*      */     }
/*      */ 
/* 1774 */     if (arrayOfDerValue[0].tag != 48) {
/* 1775 */       throw new CertificateParsingException("signed fields invalid");
/*      */     }
/*      */ 
/* 1778 */     this.algId = AlgorithmId.parse(arrayOfDerValue[1]);
/* 1779 */     this.signature = arrayOfDerValue[2].getBitString();
/*      */ 
/* 1781 */     if (arrayOfDerValue[1].data.available() != 0) {
/* 1782 */       throw new CertificateParsingException("algid field overrun");
/*      */     }
/* 1784 */     if (arrayOfDerValue[2].data.available() != 0) {
/* 1785 */       throw new CertificateParsingException("signed fields overrun");
/*      */     }
/*      */ 
/* 1788 */     this.info = new X509CertInfo(arrayOfDerValue[0]);
/*      */ 
/* 1791 */     AlgorithmId localAlgorithmId = (AlgorithmId)this.info.get("algorithmID.algorithm");
/*      */ 
/* 1795 */     if (!this.algId.equals(localAlgorithmId))
/* 1796 */       throw new CertificateException("Signature algorithm mismatch");
/* 1797 */     this.readOnly = true;
/*      */   }
/*      */ 
/*      */   private static X500Principal getX500Principal(X509Certificate paramX509Certificate, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/* 1807 */     byte[] arrayOfByte1 = paramX509Certificate.getEncoded();
/* 1808 */     DerInputStream localDerInputStream1 = new DerInputStream(arrayOfByte1);
/* 1809 */     DerValue localDerValue1 = localDerInputStream1.getSequence(3)[0];
/* 1810 */     DerInputStream localDerInputStream2 = localDerValue1.data;
/*      */ 
/* 1812 */     DerValue localDerValue2 = localDerInputStream2.getDerValue();
/*      */ 
/* 1814 */     if (localDerValue2.isContextSpecific((byte)0)) {
/* 1815 */       localDerValue2 = localDerInputStream2.getDerValue();
/*      */     }
/*      */ 
/* 1818 */     localDerValue2 = localDerInputStream2.getDerValue();
/* 1819 */     localDerValue2 = localDerInputStream2.getDerValue();
/* 1820 */     if (!paramBoolean) {
/* 1821 */       localDerValue2 = localDerInputStream2.getDerValue();
/* 1822 */       localDerValue2 = localDerInputStream2.getDerValue();
/*      */     }
/* 1824 */     byte[] arrayOfByte2 = localDerValue2.toByteArray();
/* 1825 */     return new X500Principal(arrayOfByte2);
/*      */   }
/*      */ 
/*      */   public static X500Principal getSubjectX500Principal(X509Certificate paramX509Certificate)
/*      */   {
/*      */     try
/*      */     {
/* 1834 */       return getX500Principal(paramX509Certificate, false);
/*      */     } catch (Exception localException) {
/* 1836 */       throw new RuntimeException("Could not parse subject", localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static X500Principal getIssuerX500Principal(X509Certificate paramX509Certificate)
/*      */   {
/*      */     try
/*      */     {
/* 1846 */       return getX500Principal(paramX509Certificate, true);
/*      */     } catch (Exception localException) {
/* 1848 */       throw new RuntimeException("Could not parse issuer", localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static byte[] getEncodedInternal(Certificate paramCertificate)
/*      */     throws CertificateEncodingException
/*      */   {
/* 1860 */     if ((paramCertificate instanceof X509CertImpl)) {
/* 1861 */       return ((X509CertImpl)paramCertificate).getEncodedInternal();
/*      */     }
/* 1863 */     return paramCertificate.getEncoded();
/*      */   }
/*      */ 
/*      */   public static X509CertImpl toImpl(X509Certificate paramX509Certificate)
/*      */     throws CertificateException
/*      */   {
/* 1874 */     if ((paramX509Certificate instanceof X509CertImpl)) {
/* 1875 */       return (X509CertImpl)paramX509Certificate;
/*      */     }
/* 1877 */     return X509Factory.intern(paramX509Certificate);
/*      */   }
/*      */ 
/*      */   public static boolean isSelfIssued(X509Certificate paramX509Certificate)
/*      */   {
/* 1886 */     X500Principal localX500Principal1 = paramX509Certificate.getSubjectX500Principal();
/* 1887 */     X500Principal localX500Principal2 = paramX509Certificate.getIssuerX500Principal();
/* 1888 */     return localX500Principal1.equals(localX500Principal2);
/*      */   }
/*      */ 
/*      */   public static boolean isSelfSigned(X509Certificate paramX509Certificate, String paramString)
/*      */   {
/* 1899 */     if (isSelfIssued(paramX509Certificate))
/*      */       try {
/* 1901 */         if (paramString == null)
/* 1902 */           paramX509Certificate.verify(paramX509Certificate.getPublicKey());
/*      */         else {
/* 1904 */           paramX509Certificate.verify(paramX509Certificate.getPublicKey(), paramString);
/*      */         }
/* 1906 */         return true;
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/* 1911 */     return false;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.x509.X509CertImpl
 * JD-Core Version:    0.6.2
 */