/*      */ package sun.security.tools;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.URLClassLoader;
/*      */ import java.net.URLConnection;
/*      */ import java.security.CodeSigner;
/*      */ import java.security.Key;
/*      */ import java.security.KeyStore;
/*      */ import java.security.KeyStore.Entry;
/*      */ import java.security.KeyStore.PasswordProtection;
/*      */ import java.security.KeyStore.PrivateKeyEntry;
/*      */ import java.security.KeyStore.ProtectionParameter;
/*      */ import java.security.KeyStore.SecretKeyEntry;
/*      */ import java.security.KeyStore.TrustedCertificateEntry;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.Provider;
/*      */ import java.security.PublicKey;
/*      */ import java.security.Security;
/*      */ import java.security.Signature;
/*      */ import java.security.Timestamp;
/*      */ import java.security.UnrecoverableEntryException;
/*      */ import java.security.UnrecoverableKeyException;
/*      */ import java.security.cert.CRL;
/*      */ import java.security.cert.CertPath;
/*      */ import java.security.cert.CertStore;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.X509CRL;
/*      */ import java.security.cert.X509CRLEntry;
/*      */ import java.security.cert.X509CRLSelector;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.text.Collator;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import java.util.jar.JarEntry;
/*      */ import java.util.jar.JarFile;
/*      */ import javax.crypto.KeyGenerator;
/*      */ import javax.net.ssl.HostnameVerifier;
/*      */ import javax.net.ssl.HttpsURLConnection;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.TrustManager;
/*      */ import javax.net.ssl.X509ExtendedTrustManager;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.BASE64Decoder;
/*      */ import sun.misc.BASE64Encoder;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.pkcs.PKCS10;
/*      */ import sun.security.pkcs.PKCS10Attribute;
/*      */ import sun.security.pkcs.PKCS10Attributes;
/*      */ import sun.security.pkcs.PKCS9Attribute;
/*      */ import sun.security.provider.certpath.ldap.LDAPCertStoreHelper;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ import sun.security.util.Password;
/*      */ import sun.security.util.PathList;
/*      */ import sun.security.x509.AccessDescription;
/*      */ import sun.security.x509.AlgorithmId;
/*      */ import sun.security.x509.AuthorityInfoAccessExtension;
/*      */ import sun.security.x509.AuthorityKeyIdentifierExtension;
/*      */ import sun.security.x509.BasicConstraintsExtension;
/*      */ import sun.security.x509.CRLDistributionPointsExtension;
/*      */ import sun.security.x509.CRLExtensions;
/*      */ import sun.security.x509.CRLReasonCodeExtension;
/*      */ import sun.security.x509.CertAndKeyGen;
/*      */ import sun.security.x509.CertificateAlgorithmId;
/*      */ import sun.security.x509.CertificateExtensions;
/*      */ import sun.security.x509.CertificateIssuerName;
/*      */ import sun.security.x509.CertificateSerialNumber;
/*      */ import sun.security.x509.CertificateSubjectName;
/*      */ import sun.security.x509.CertificateValidity;
/*      */ import sun.security.x509.CertificateVersion;
/*      */ import sun.security.x509.CertificateX509Key;
/*      */ import sun.security.x509.DNSName;
/*      */ import sun.security.x509.DistributionPoint;
/*      */ import sun.security.x509.ExtendedKeyUsageExtension;
/*      */ import sun.security.x509.Extension;
/*      */ import sun.security.x509.GeneralName;
/*      */ import sun.security.x509.GeneralNameInterface;
/*      */ import sun.security.x509.GeneralNames;
/*      */ import sun.security.x509.IPAddressName;
/*      */ import sun.security.x509.IssuerAlternativeNameExtension;
/*      */ import sun.security.x509.KeyIdentifier;
/*      */ import sun.security.x509.KeyUsageExtension;
/*      */ import sun.security.x509.OIDName;
/*      */ import sun.security.x509.PKIXExtensions;
/*      */ import sun.security.x509.RFC822Name;
/*      */ import sun.security.x509.SubjectAlternativeNameExtension;
/*      */ import sun.security.x509.SubjectInfoAccessExtension;
/*      */ import sun.security.x509.SubjectKeyIdentifierExtension;
/*      */ import sun.security.x509.URIName;
/*      */ import sun.security.x509.X500Name;
/*      */ import sun.security.x509.X509CRLEntryImpl;
/*      */ import sun.security.x509.X509CRLImpl;
/*      */ import sun.security.x509.X509CertImpl;
/*      */ import sun.security.x509.X509CertInfo;
/*      */ 
/*      */ public final class KeyTool
/*      */ {
/*  106 */   private boolean debug = false;
/*  107 */   private Command command = null;
/*  108 */   private String sigAlgName = null;
/*  109 */   private String keyAlgName = null;
/*  110 */   private boolean verbose = false;
/*  111 */   private int keysize = -1;
/*  112 */   private boolean rfc = false;
/*  113 */   private long validity = 90L;
/*  114 */   private String alias = null;
/*  115 */   private String dname = null;
/*  116 */   private String dest = null;
/*  117 */   private String filename = null;
/*  118 */   private String infilename = null;
/*  119 */   private String outfilename = null;
/*  120 */   private String srcksfname = null;
/*      */ 
/*  128 */   private Set<Pair<String, String>> providers = null;
/*  129 */   private String storetype = null;
/*  130 */   private String srcProviderName = null;
/*  131 */   private String providerName = null;
/*  132 */   private String pathlist = null;
/*  133 */   private char[] storePass = null;
/*  134 */   private char[] storePassNew = null;
/*  135 */   private char[] keyPass = null;
/*  136 */   private char[] keyPassNew = null;
/*  137 */   private char[] newPass = null;
/*  138 */   private char[] destKeyPass = null;
/*  139 */   private char[] srckeyPass = null;
/*  140 */   private String ksfname = null;
/*  141 */   private File ksfile = null;
/*  142 */   private InputStream ksStream = null;
/*  143 */   private String sslserver = null;
/*  144 */   private String jarfile = null;
/*  145 */   private KeyStore keyStore = null;
/*  146 */   private boolean token = false;
/*  147 */   private boolean nullStream = false;
/*  148 */   private boolean kssave = false;
/*  149 */   private boolean noprompt = false;
/*  150 */   private boolean trustcacerts = false;
/*  151 */   private boolean protectedPath = false;
/*  152 */   private boolean srcprotectedPath = false;
/*  153 */   private CertificateFactory cf = null;
/*  154 */   private KeyStore caks = null;
/*  155 */   private char[] srcstorePass = null;
/*  156 */   private String srcstoretype = null;
/*  157 */   private Set<char[]> passwords = new HashSet();
/*  158 */   private String startDate = null;
/*      */ 
/*  160 */   private List<String> ids = new ArrayList();
/*  161 */   private List<String> v3ext = new ArrayList();
/*      */ 
/*  312 */   private static final Class[] PARAM_STRING = { String.class };
/*      */   private static final String JKS = "jks";
/*      */   private static final String NONE = "NONE";
/*      */   private static final String P11KEYSTORE = "PKCS11";
/*      */   private static final String P12KEYSTORE = "PKCS12";
/*  318 */   private final String keyAlias = "mykey";
/*      */ 
/*  321 */   private static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.util.Resources");
/*      */ 
/*  323 */   private static final Collator collator = Collator.getInstance();
/*      */ 
/* 3683 */   private static final String[] extSupported = { "BasicConstraints", "KeyUsage", "ExtendedKeyUsage", "SubjectAlternativeName", "IssuerAlternativeName", "SubjectInfoAccess", "AuthorityInfoAccess", null, "CRLDistributionPoints" };
/*      */ 
/*      */   public static void main(String[] paramArrayOfString)
/*      */     throws Exception
/*      */   {
/*  332 */     KeyTool localKeyTool = new KeyTool();
/*  333 */     localKeyTool.run(paramArrayOfString, System.out);
/*      */   }
/*      */ 
/*      */   private void run(String[] paramArrayOfString, PrintStream paramPrintStream) throws Exception {
/*      */     try {
/*  338 */       parseArgs(paramArrayOfString);
/*  339 */       if (this.command != null)
/*  340 */         doCommands(paramPrintStream);
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */       Iterator localIterator1;
/*  343 */       System.out.println(rb.getString("keytool.error.") + localException);
/*  344 */       if (this.verbose) {
/*  345 */         localException.printStackTrace(System.out);
/*      */       }
/*  347 */       if (!this.debug)
/*  348 */         System.exit(1);
/*      */       else
/*  350 */         throw localException;
/*      */     }
/*      */     finally
/*      */     {
/*      */       char[] arrayOfChar1;
/*      */       Iterator localIterator2;
/*  353 */       for (char[] arrayOfChar2 : this.passwords) {
/*  354 */         if (arrayOfChar2 != null) {
/*  355 */           Arrays.fill(arrayOfChar2, ' ');
/*  356 */           arrayOfChar2 = null;
/*      */         }
/*      */       }
/*      */ 
/*  360 */       if (this.ksStream != null)
/*  361 */         this.ksStream.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   void parseArgs(String[] paramArrayOfString)
/*      */   {
/*  371 */     int i = 0;
/*  372 */     int j = paramArrayOfString.length == 0 ? 1 : 0;
/*      */ 
/*  374 */     for (i = 0; (i < paramArrayOfString.length) && (paramArrayOfString[i].startsWith("-")); i++)
/*      */     {
/*  376 */       String str1 = paramArrayOfString[i];
/*      */       Object localObject2;
/*  379 */       if (i == paramArrayOfString.length - 1) {
/*  380 */         for (localObject2 : Option.values())
/*      */         {
/*  382 */           if (collator.compare(str1, ((Option)localObject2).toString()) == 0) {
/*  383 */             if (((Option)localObject2).arg == null) break; errorNeedArgument(str1); break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  392 */       ??? = null;
/*  393 */       ??? = str1.indexOf(':');
/*  394 */       if (??? > 0) {
/*  395 */         ??? = str1.substring(??? + 1);
/*  396 */         str1 = str1.substring(0, ???);
/*      */       }
/*      */ 
/*  401 */       ??? = 0;
/*  402 */       for (Object localObject3 : Command.values()) {
/*  403 */         if (collator.compare(str1, localObject3.toString()) == 0) {
/*  404 */           this.command = localObject3;
/*  405 */           ??? = 1;
/*  406 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  410 */       if (??? == 0)
/*      */       {
/*  412 */         if (collator.compare(str1, "-export") == 0) {
/*  413 */           this.command = Command.EXPORTCERT;
/*  414 */         } else if (collator.compare(str1, "-genkey") == 0) {
/*  415 */           this.command = Command.GENKEYPAIR;
/*  416 */         } else if (collator.compare(str1, "-import") == 0) {
/*  417 */           this.command = Command.IMPORTCERT;
/*      */         }
/*  422 */         else if (collator.compare(str1, "-help") == 0) {
/*  423 */           j = 1;
/*      */         }
/*  429 */         else if ((collator.compare(str1, "-keystore") == 0) || (collator.compare(str1, "-destkeystore") == 0))
/*      */         {
/*  431 */           this.ksfname = paramArrayOfString[(++i)];
/*  432 */         } else if ((collator.compare(str1, "-storepass") == 0) || (collator.compare(str1, "-deststorepass") == 0))
/*      */         {
/*  434 */           this.storePass = getPass((String)???, paramArrayOfString[(++i)]);
/*  435 */           this.passwords.add(this.storePass);
/*  436 */         } else if ((collator.compare(str1, "-storetype") == 0) || (collator.compare(str1, "-deststoretype") == 0))
/*      */         {
/*  438 */           this.storetype = paramArrayOfString[(++i)];
/*  439 */         } else if (collator.compare(str1, "-srcstorepass") == 0) {
/*  440 */           this.srcstorePass = getPass((String)???, paramArrayOfString[(++i)]);
/*  441 */           this.passwords.add(this.srcstorePass);
/*  442 */         } else if (collator.compare(str1, "-srcstoretype") == 0) {
/*  443 */           this.srcstoretype = paramArrayOfString[(++i)];
/*  444 */         } else if (collator.compare(str1, "-srckeypass") == 0) {
/*  445 */           this.srckeyPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  446 */           this.passwords.add(this.srckeyPass);
/*  447 */         } else if (collator.compare(str1, "-srcprovidername") == 0) {
/*  448 */           this.srcProviderName = paramArrayOfString[(++i)];
/*  449 */         } else if ((collator.compare(str1, "-providername") == 0) || (collator.compare(str1, "-destprovidername") == 0))
/*      */         {
/*  451 */           this.providerName = paramArrayOfString[(++i)];
/*  452 */         } else if (collator.compare(str1, "-providerpath") == 0) {
/*  453 */           this.pathlist = paramArrayOfString[(++i)];
/*  454 */         } else if (collator.compare(str1, "-keypass") == 0) {
/*  455 */           this.keyPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  456 */           this.passwords.add(this.keyPass);
/*  457 */         } else if (collator.compare(str1, "-new") == 0) {
/*  458 */           this.newPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  459 */           this.passwords.add(this.newPass);
/*  460 */         } else if (collator.compare(str1, "-destkeypass") == 0) {
/*  461 */           this.destKeyPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  462 */           this.passwords.add(this.destKeyPass);
/*  463 */         } else if ((collator.compare(str1, "-alias") == 0) || (collator.compare(str1, "-srcalias") == 0))
/*      */         {
/*  465 */           this.alias = paramArrayOfString[(++i)];
/*  466 */         } else if ((collator.compare(str1, "-dest") == 0) || (collator.compare(str1, "-destalias") == 0))
/*      */         {
/*  468 */           this.dest = paramArrayOfString[(++i)];
/*  469 */         } else if (collator.compare(str1, "-dname") == 0) {
/*  470 */           this.dname = paramArrayOfString[(++i)];
/*  471 */         } else if (collator.compare(str1, "-keysize") == 0) {
/*  472 */           this.keysize = Integer.parseInt(paramArrayOfString[(++i)]);
/*  473 */         } else if (collator.compare(str1, "-keyalg") == 0) {
/*  474 */           this.keyAlgName = paramArrayOfString[(++i)];
/*  475 */         } else if (collator.compare(str1, "-sigalg") == 0) {
/*  476 */           this.sigAlgName = paramArrayOfString[(++i)];
/*  477 */         } else if (collator.compare(str1, "-startdate") == 0) {
/*  478 */           this.startDate = paramArrayOfString[(++i)];
/*  479 */         } else if (collator.compare(str1, "-validity") == 0) {
/*  480 */           this.validity = Long.parseLong(paramArrayOfString[(++i)]);
/*  481 */         } else if (collator.compare(str1, "-ext") == 0) {
/*  482 */           this.v3ext.add(paramArrayOfString[(++i)]);
/*  483 */         } else if (collator.compare(str1, "-id") == 0) {
/*  484 */           this.ids.add(paramArrayOfString[(++i)]);
/*  485 */         } else if (collator.compare(str1, "-file") == 0) {
/*  486 */           this.filename = paramArrayOfString[(++i)];
/*  487 */         } else if (collator.compare(str1, "-infile") == 0) {
/*  488 */           this.infilename = paramArrayOfString[(++i)];
/*  489 */         } else if (collator.compare(str1, "-outfile") == 0) {
/*  490 */           this.outfilename = paramArrayOfString[(++i)];
/*  491 */         } else if (collator.compare(str1, "-sslserver") == 0) {
/*  492 */           this.sslserver = paramArrayOfString[(++i)];
/*  493 */         } else if (collator.compare(str1, "-jarfile") == 0) {
/*  494 */           this.jarfile = paramArrayOfString[(++i)];
/*  495 */         } else if (collator.compare(str1, "-srckeystore") == 0) {
/*  496 */           this.srcksfname = paramArrayOfString[(++i)];
/*  497 */         } else if ((collator.compare(str1, "-provider") == 0) || (collator.compare(str1, "-providerclass") == 0))
/*      */         {
/*  499 */           if (this.providers == null) {
/*  500 */             this.providers = new HashSet(3);
/*      */           }
/*  502 */           localObject2 = paramArrayOfString[(++i)];
/*  503 */           String str2 = null;
/*      */ 
/*  505 */           if (paramArrayOfString.length > i + 1) {
/*  506 */             str1 = paramArrayOfString[(i + 1)];
/*  507 */             if (collator.compare(str1, "-providerarg") == 0) {
/*  508 */               if (paramArrayOfString.length == i + 2) errorNeedArgument(str1);
/*  509 */               str2 = paramArrayOfString[(i + 2)];
/*  510 */               i += 2;
/*      */             }
/*      */           }
/*  513 */           this.providers.add(Pair.of(localObject2, str2));
/*      */         }
/*  520 */         else if (collator.compare(str1, "-v") == 0) {
/*  521 */           this.verbose = true;
/*  522 */         } else if (collator.compare(str1, "-debug") == 0) {
/*  523 */           this.debug = true;
/*  524 */         } else if (collator.compare(str1, "-rfc") == 0) {
/*  525 */           this.rfc = true;
/*  526 */         } else if (collator.compare(str1, "-noprompt") == 0) {
/*  527 */           this.noprompt = true;
/*  528 */         } else if (collator.compare(str1, "-trustcacerts") == 0) {
/*  529 */           this.trustcacerts = true;
/*  530 */         } else if ((collator.compare(str1, "-protected") == 0) || (collator.compare(str1, "-destprotected") == 0))
/*      */         {
/*  532 */           this.protectedPath = true;
/*  533 */         } else if (collator.compare(str1, "-srcprotected") == 0) {
/*  534 */           this.srcprotectedPath = true;
/*      */         } else {
/*  536 */           System.err.println(rb.getString("Illegal.option.") + str1);
/*  537 */           tinyHelp();
/*      */         }
/*      */       }
/*      */     }
/*  541 */     if (i < paramArrayOfString.length) {
/*  542 */       System.err.println(rb.getString("Illegal.option.") + paramArrayOfString[i]);
/*  543 */       tinyHelp();
/*      */     }
/*      */ 
/*  546 */     if (this.command == null) {
/*  547 */       if (j != 0) {
/*  548 */         usage();
/*      */       } else {
/*  550 */         System.err.println(rb.getString("Usage.error.no.command.provided"));
/*  551 */         tinyHelp();
/*      */       }
/*  553 */     } else if (j != 0) {
/*  554 */       usage();
/*  555 */       this.command = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isKeyStoreRelated(Command paramCommand) {
/*  560 */     return (paramCommand != Command.PRINTCERT) && (paramCommand != Command.PRINTCERTREQ);
/*      */   }
/*      */ 
/*      */   void doCommands(PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/*  568 */     if (this.storetype == null) {
/*  569 */       this.storetype = KeyStore.getDefaultType();
/*      */     }
/*  571 */     this.storetype = KeyStoreUtil.niceStoreTypeName(this.storetype);
/*      */ 
/*  573 */     if (this.srcstoretype == null) {
/*  574 */       this.srcstoretype = KeyStore.getDefaultType();
/*      */     }
/*  576 */     this.srcstoretype = KeyStoreUtil.niceStoreTypeName(this.srcstoretype);
/*      */ 
/*  578 */     if (("PKCS11".equalsIgnoreCase(this.storetype)) || (KeyStoreUtil.isWindowsKeyStore(this.storetype)))
/*      */     {
/*  580 */       this.token = true;
/*  581 */       if (this.ksfname == null) {
/*  582 */         this.ksfname = "NONE";
/*      */       }
/*      */     }
/*  585 */     if ("NONE".equals(this.ksfname)) {
/*  586 */       this.nullStream = true;
/*      */     }
/*      */ 
/*  589 */     if ((this.token) && (!this.nullStream)) {
/*  590 */       System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { this.storetype }));
/*      */ 
/*  592 */       System.err.println();
/*  593 */       tinyHelp();
/*      */     }
/*      */ 
/*  596 */     if ((this.token) && ((this.command == Command.KEYPASSWD) || (this.command == Command.STOREPASSWD)))
/*      */     {
/*  598 */       throw new UnsupportedOperationException(MessageFormat.format(rb.getString(".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}"), new Object[] { this.storetype }));
/*      */     }
/*      */ 
/*  602 */     if (("PKCS12".equalsIgnoreCase(this.storetype)) && (this.command == Command.KEYPASSWD)) {
/*  603 */       throw new UnsupportedOperationException(rb.getString(".keypasswd.commands.not.supported.if.storetype.is.PKCS12"));
/*      */     }
/*      */ 
/*  607 */     if ((this.token) && ((this.keyPass != null) || (this.newPass != null) || (this.destKeyPass != null))) {
/*  608 */       throw new IllegalArgumentException(MessageFormat.format(rb.getString(".keypass.and.new.can.not.be.specified.if.storetype.is.{0}"), new Object[] { this.storetype }));
/*      */     }
/*      */ 
/*  612 */     if ((this.protectedPath) && (
/*  613 */       (this.storePass != null) || (this.keyPass != null) || (this.newPass != null) || (this.destKeyPass != null)))
/*      */     {
/*  615 */       throw new IllegalArgumentException(rb.getString("if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified"));
/*      */     }
/*      */ 
/*  620 */     if ((this.srcprotectedPath) && (
/*  621 */       (this.srcstorePass != null) || (this.srckeyPass != null))) {
/*  622 */       throw new IllegalArgumentException(rb.getString("if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified"));
/*      */     }
/*      */ 
/*  627 */     if ((KeyStoreUtil.isWindowsKeyStore(this.storetype)) && (
/*  628 */       (this.storePass != null) || (this.keyPass != null) || (this.newPass != null) || (this.destKeyPass != null)))
/*      */     {
/*  630 */       throw new IllegalArgumentException(rb.getString("if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified"));
/*      */     }
/*      */ 
/*  635 */     if ((KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) && (
/*  636 */       (this.srcstorePass != null) || (this.srckeyPass != null))) {
/*  637 */       throw new IllegalArgumentException(rb.getString("if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified"));
/*      */     }
/*      */ 
/*  642 */     if (this.validity <= 0L)
/*  643 */       throw new Exception(rb.getString("Validity.must.be.greater.than.zero"));
/*      */     Object localObject1;
/*      */     Object localObject3;
/*      */     Object localObject4;
/*  648 */     if (this.providers != null) {
/*  649 */       localObject1 = null;
/*  650 */       if (this.pathlist != null) {
/*  651 */         localObject3 = null;
/*  652 */         localObject3 = PathList.appendPath((String)localObject3, System.getProperty("java.class.path"));
/*      */ 
/*  654 */         localObject3 = PathList.appendPath((String)localObject3, System.getProperty("env.class.path"));
/*      */ 
/*  656 */         localObject3 = PathList.appendPath((String)localObject3, this.pathlist);
/*      */ 
/*  658 */         localObject4 = PathList.pathToURLs((String)localObject3);
/*  659 */         localObject1 = new URLClassLoader((URL[])localObject4);
/*      */       } else {
/*  661 */         localObject1 = ClassLoader.getSystemClassLoader();
/*      */       }
/*      */ 
/*  664 */       for (localObject3 = this.providers.iterator(); ((Iterator)localObject3).hasNext(); ) { localObject4 = (Pair)((Iterator)localObject3).next();
/*  665 */         localObject5 = (String)((Pair)localObject4).fst;
/*      */         Class localClass;
/*  667 */         if (localObject1 != null)
/*  668 */           localClass = ((ClassLoader)localObject1).loadClass((String)localObject5);
/*      */         else {
/*  670 */           localClass = Class.forName((String)localObject5);
/*      */         }
/*      */ 
/*  673 */         String str = (String)((Pair)localObject4).snd;
/*      */         Object localObject6;
/*      */         Object localObject7;
/*  675 */         if (str == null) {
/*  676 */           localObject6 = localClass.newInstance();
/*      */         } else {
/*  678 */           localObject7 = localClass.getConstructor(PARAM_STRING);
/*  679 */           localObject6 = ((Constructor)localObject7).newInstance(new Object[] { str });
/*      */         }
/*  681 */         if (!(localObject6 instanceof Provider)) {
/*  682 */           localObject7 = new MessageFormat(rb.getString("provName.not.a.provider"));
/*      */ 
/*  684 */           Object[] arrayOfObject = { localObject5 };
/*  685 */           throw new Exception(((MessageFormat)localObject7).format(arrayOfObject));
/*      */         }
/*  687 */         Security.addProvider((Provider)localObject6);
/*      */       }
/*      */     }
/*      */     Object localObject5;
/*  691 */     if ((this.command == Command.LIST) && (this.verbose) && (this.rfc)) {
/*  692 */       System.err.println(rb.getString("Must.not.specify.both.v.and.rfc.with.list.command"));
/*      */ 
/*  694 */       tinyHelp();
/*      */     }
/*      */ 
/*  698 */     if ((this.command == Command.GENKEYPAIR) && (this.keyPass != null) && (this.keyPass.length < 6)) {
/*  699 */       throw new Exception(rb.getString("Key.password.must.be.at.least.6.characters"));
/*      */     }
/*      */ 
/*  702 */     if ((this.newPass != null) && (this.newPass.length < 6)) {
/*  703 */       throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
/*      */     }
/*      */ 
/*  706 */     if ((this.destKeyPass != null) && (this.destKeyPass.length < 6)) {
/*  707 */       throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
/*      */     }
/*      */ 
/*  716 */     if (isKeyStoreRelated(this.command)) {
/*  717 */       if (this.ksfname == null) {
/*  718 */         this.ksfname = (System.getProperty("user.home") + File.separator + ".keystore");
/*      */       }
/*      */ 
/*  722 */       if (!this.nullStream) {
/*      */         try {
/*  724 */           this.ksfile = new File(this.ksfname);
/*      */ 
/*  726 */           if ((this.ksfile.exists()) && (this.ksfile.length() == 0L)) {
/*  727 */             throw new Exception(rb.getString("Keystore.file.exists.but.is.empty.") + this.ksfname);
/*      */           }
/*      */ 
/*  730 */           this.ksStream = new FileInputStream(this.ksfile);
/*      */         } catch (FileNotFoundException localFileNotFoundException) {
/*  732 */           if ((this.command != Command.GENKEYPAIR) && (this.command != Command.GENSECKEY) && (this.command != Command.IDENTITYDB) && (this.command != Command.IMPORTCERT) && (this.command != Command.IMPORTKEYSTORE) && (this.command != Command.PRINTCRL))
/*      */           {
/*  738 */             throw new Exception(rb.getString("Keystore.file.does.not.exist.") + this.ksfname);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  745 */     if (((this.command == Command.KEYCLONE) || (this.command == Command.CHANGEALIAS)) && (this.dest == null))
/*      */     {
/*  747 */       this.dest = getAlias("destination");
/*  748 */       if ("".equals(this.dest)) {
/*  749 */         throw new Exception(rb.getString("Must.specify.destination.alias"));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  754 */     if ((this.command == Command.DELETE) && (this.alias == null)) {
/*  755 */       this.alias = getAlias(null);
/*  756 */       if ("".equals(this.alias)) {
/*  757 */         throw new Exception(rb.getString("Must.specify.alias"));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  762 */     if (this.providerName == null)
/*  763 */       this.keyStore = KeyStore.getInstance(this.storetype);
/*      */     else {
/*  765 */       this.keyStore = KeyStore.getInstance(this.storetype, this.providerName);
/*      */     }
/*      */ 
/*  788 */     if (!this.nullStream) {
/*  789 */       this.keyStore.load(this.ksStream, this.storePass);
/*  790 */       if (this.ksStream != null) {
/*  791 */         this.ksStream.close();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  798 */     if ((this.nullStream) && (this.storePass != null)) {
/*  799 */       this.keyStore.load(null, this.storePass);
/*  800 */     } else if ((!this.nullStream) && (this.storePass != null))
/*      */     {
/*  803 */       if ((this.ksStream == null) && (this.storePass.length < 6)) {
/*  804 */         throw new Exception(rb.getString("Keystore.password.must.be.at.least.6.characters"));
/*      */       }
/*      */     }
/*  807 */     else if (this.storePass == null)
/*      */     {
/*  811 */       if ((!this.protectedPath) && (!KeyStoreUtil.isWindowsKeyStore(this.storetype)) && ((this.command == Command.CERTREQ) || (this.command == Command.DELETE) || (this.command == Command.GENKEYPAIR) || (this.command == Command.GENSECKEY) || (this.command == Command.IMPORTCERT) || (this.command == Command.IMPORTKEYSTORE) || (this.command == Command.KEYCLONE) || (this.command == Command.CHANGEALIAS) || (this.command == Command.SELFCERT) || (this.command == Command.STOREPASSWD) || (this.command == Command.KEYPASSWD) || (this.command == Command.IDENTITYDB)))
/*      */       {
/*  824 */         int i = 0;
/*      */         do {
/*  826 */           if (this.command == Command.IMPORTKEYSTORE) {
/*  827 */             System.err.print(rb.getString("Enter.destination.keystore.password."));
/*      */           }
/*      */           else {
/*  830 */             System.err.print(rb.getString("Enter.keystore.password."));
/*      */           }
/*      */ 
/*  833 */           System.err.flush();
/*  834 */           this.storePass = Password.readPassword(System.in);
/*  835 */           this.passwords.add(this.storePass);
/*      */ 
/*  839 */           if ((!this.nullStream) && ((this.storePass == null) || (this.storePass.length < 6))) {
/*  840 */             System.err.println(rb.getString("Keystore.password.is.too.short.must.be.at.least.6.characters"));
/*      */ 
/*  842 */             this.storePass = null;
/*      */           }
/*      */ 
/*  847 */           if ((this.storePass != null) && (!this.nullStream) && (this.ksStream == null)) {
/*  848 */             System.err.print(rb.getString("Re.enter.new.password."));
/*  849 */             localObject3 = Password.readPassword(System.in);
/*  850 */             this.passwords.add(localObject3);
/*  851 */             if (!Arrays.equals(this.storePass, (char[])localObject3)) {
/*  852 */               System.err.println(rb.getString("They.don.t.match.Try.again"));
/*      */ 
/*  854 */               this.storePass = null;
/*      */             }
/*      */           }
/*      */ 
/*  858 */           i++;
/*  859 */         }while ((this.storePass == null) && (i < 3));
/*      */ 
/*  862 */         if (this.storePass == null) {
/*  863 */           System.err.println(rb.getString("Too.many.failures.try.later"));
/*      */ 
/*  865 */           return;
/*      */         }
/*  867 */       } else if ((!this.protectedPath) && (!KeyStoreUtil.isWindowsKeyStore(this.storetype)) && (isKeyStoreRelated(this.command)))
/*      */       {
/*  871 */         if (this.command != Command.PRINTCRL) {
/*  872 */           System.err.print(rb.getString("Enter.keystore.password."));
/*  873 */           System.err.flush();
/*  874 */           this.storePass = Password.readPassword(System.in);
/*  875 */           this.passwords.add(this.storePass);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  881 */       if (this.nullStream) {
/*  882 */         this.keyStore.load(null, this.storePass);
/*  883 */       } else if (this.ksStream != null) {
/*  884 */         this.ksStream = new FileInputStream(this.ksfile);
/*  885 */         this.keyStore.load(this.ksStream, this.storePass);
/*  886 */         this.ksStream.close();
/*      */       }
/*      */     }
/*      */     Object localObject2;
/*  890 */     if ((this.storePass != null) && ("PKCS12".equalsIgnoreCase(this.storetype))) {
/*  891 */       localObject2 = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
/*      */ 
/*  893 */       if ((this.keyPass != null) && (!Arrays.equals(this.storePass, this.keyPass))) {
/*  894 */         localObject3 = new Object[] { "-keypass" };
/*  895 */         System.err.println(((MessageFormat)localObject2).format(localObject3));
/*  896 */         this.keyPass = this.storePass;
/*      */       }
/*  898 */       if ((this.newPass != null) && (!Arrays.equals(this.storePass, this.newPass))) {
/*  899 */         localObject3 = new Object[] { "-new" };
/*  900 */         System.err.println(((MessageFormat)localObject2).format(localObject3));
/*  901 */         this.newPass = this.storePass;
/*      */       }
/*  903 */       if ((this.destKeyPass != null) && (!Arrays.equals(this.storePass, this.destKeyPass))) {
/*  904 */         localObject3 = new Object[] { "-destkeypass" };
/*  905 */         System.err.println(((MessageFormat)localObject2).format(localObject3));
/*  906 */         this.destKeyPass = this.storePass;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  911 */     if ((this.command == Command.PRINTCERT) || (this.command == Command.IMPORTCERT) || (this.command == Command.IDENTITYDB) || (this.command == Command.PRINTCRL))
/*      */     {
/*  913 */       this.cf = CertificateFactory.getInstance("X509");
/*      */     }
/*      */ 
/*  916 */     if (this.trustcacerts) {
/*  917 */       this.caks = getCacertsKeyStore();
/*      */     }
/*      */ 
/*  921 */     if (this.command == Command.CERTREQ) {
/*  922 */       localObject2 = null;
/*  923 */       if (this.filename != null) {
/*  924 */         localObject2 = new PrintStream(new FileOutputStream(this.filename));
/*      */ 
/*  926 */         paramPrintStream = (PrintStream)localObject2;
/*      */       }
/*      */       try {
/*  929 */         doCertReq(this.alias, this.sigAlgName, paramPrintStream);
/*      */       } finally {
/*  931 */         if (localObject2 != null) {
/*  932 */           ((PrintStream)localObject2).close();
/*      */         }
/*      */       }
/*  935 */       if ((this.verbose) && (this.filename != null)) {
/*  936 */         localObject3 = new MessageFormat(rb.getString("Certification.request.stored.in.file.filename."));
/*      */ 
/*  938 */         localObject4 = new Object[] { this.filename };
/*  939 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*  940 */         System.err.println(rb.getString("Submit.this.to.your.CA"));
/*      */       }
/*  942 */     } else if (this.command == Command.DELETE) {
/*  943 */       doDeleteEntry(this.alias);
/*  944 */       this.kssave = true;
/*  945 */     } else if (this.command == Command.EXPORTCERT) {
/*  946 */       localObject2 = null;
/*  947 */       if (this.filename != null) {
/*  948 */         localObject2 = new PrintStream(new FileOutputStream(this.filename));
/*      */ 
/*  950 */         paramPrintStream = (PrintStream)localObject2;
/*      */       }
/*      */       try {
/*  953 */         doExportCert(this.alias, paramPrintStream);
/*      */       } finally {
/*  955 */         if (localObject2 != null) {
/*  956 */           ((PrintStream)localObject2).close();
/*      */         }
/*      */       }
/*  959 */       if (this.filename != null) {
/*  960 */         localObject3 = new MessageFormat(rb.getString("Certificate.stored.in.file.filename."));
/*      */ 
/*  962 */         localObject4 = new Object[] { this.filename };
/*  963 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*      */       }
/*  965 */     } else if (this.command == Command.GENKEYPAIR) {
/*  966 */       if (this.keyAlgName == null) {
/*  967 */         this.keyAlgName = "DSA";
/*      */       }
/*  969 */       doGenKeyPair(this.alias, this.dname, this.keyAlgName, this.keysize, this.sigAlgName);
/*  970 */       this.kssave = true;
/*  971 */     } else if (this.command == Command.GENSECKEY) {
/*  972 */       if (this.keyAlgName == null) {
/*  973 */         this.keyAlgName = "DES";
/*      */       }
/*  975 */       doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
/*  976 */       this.kssave = true;
/*  977 */     } else if (this.command == Command.IDENTITYDB) {
/*  978 */       localObject2 = System.in;
/*  979 */       if (this.filename != null)
/*  980 */         localObject2 = new FileInputStream(this.filename);
/*      */       try
/*      */       {
/*  983 */         doImportIdentityDatabase((InputStream)localObject2);
/*      */ 
/*  985 */         if (localObject2 != System.in)
/*  986 */           ((InputStream)localObject2).close();
/*      */       }
/*      */       finally
/*      */       {
/*  985 */         if (localObject2 != System.in)
/*  986 */           ((InputStream)localObject2).close();
/*      */       }
/*      */     }
/*  989 */     else if (this.command == Command.IMPORTCERT) {
/*  990 */       localObject2 = System.in;
/*  991 */       if (this.filename != null) {
/*  992 */         localObject2 = new FileInputStream(this.filename);
/*      */       }
/*  994 */       localObject3 = this.alias != null ? this.alias : "mykey";
/*      */       try {
/*  996 */         if (this.keyStore.entryInstanceOf((String)localObject3, KeyStore.PrivateKeyEntry.class))
/*      */         {
/*  998 */           this.kssave = installReply((String)localObject3, (InputStream)localObject2);
/*  999 */           if (this.kssave) {
/* 1000 */             System.err.println(rb.getString("Certificate.reply.was.installed.in.keystore"));
/*      */           }
/*      */           else {
/* 1003 */             System.err.println(rb.getString("Certificate.reply.was.not.installed.in.keystore"));
/*      */           }
/*      */         }
/* 1006 */         else if ((!this.keyStore.containsAlias((String)localObject3)) || (this.keyStore.entryInstanceOf((String)localObject3, KeyStore.TrustedCertificateEntry.class)))
/*      */         {
/* 1009 */           this.kssave = addTrustedCert((String)localObject3, (InputStream)localObject2);
/* 1010 */           if (this.kssave) {
/* 1011 */             System.err.println(rb.getString("Certificate.was.added.to.keystore"));
/*      */           }
/*      */           else {
/* 1014 */             System.err.println(rb.getString("Certificate.was.not.added.to.keystore"));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1019 */         if (localObject2 != System.in)
/* 1020 */           ((InputStream)localObject2).close();
/*      */       }
/*      */       finally
/*      */       {
/* 1019 */         if (localObject2 != System.in)
/* 1020 */           ((InputStream)localObject2).close();
/*      */       }
/*      */     }
/* 1023 */     else if (this.command == Command.IMPORTKEYSTORE) {
/* 1024 */       doImportKeyStore();
/* 1025 */       this.kssave = true;
/* 1026 */     } else if (this.command == Command.KEYCLONE) {
/* 1027 */       this.keyPassNew = this.newPass;
/*      */ 
/* 1030 */       if (this.alias == null) {
/* 1031 */         this.alias = "mykey";
/*      */       }
/* 1033 */       if (!this.keyStore.containsAlias(this.alias)) {
/* 1034 */         localObject2 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/*      */ 
/* 1036 */         localObject3 = new Object[] { this.alias };
/* 1037 */         throw new Exception(((MessageFormat)localObject2).format(localObject3));
/*      */       }
/* 1039 */       if (!this.keyStore.entryInstanceOf(this.alias, KeyStore.PrivateKeyEntry.class)) {
/* 1040 */         localObject2 = new MessageFormat(rb.getString("Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key"));
/*      */ 
/* 1042 */         localObject3 = new Object[] { this.alias };
/* 1043 */         throw new Exception(((MessageFormat)localObject2).format(localObject3));
/*      */       }
/*      */ 
/* 1046 */       doCloneEntry(this.alias, this.dest, true);
/* 1047 */       this.kssave = true;
/* 1048 */     } else if (this.command == Command.CHANGEALIAS) {
/* 1049 */       if (this.alias == null) {
/* 1050 */         this.alias = "mykey";
/*      */       }
/* 1052 */       doCloneEntry(this.alias, this.dest, false);
/*      */ 
/* 1054 */       if (this.keyStore.containsAlias(this.alias)) {
/* 1055 */         doDeleteEntry(this.alias);
/*      */       }
/* 1057 */       this.kssave = true;
/* 1058 */     } else if (this.command == Command.KEYPASSWD) {
/* 1059 */       this.keyPassNew = this.newPass;
/* 1060 */       doChangeKeyPasswd(this.alias);
/* 1061 */       this.kssave = true;
/* 1062 */     } else if (this.command == Command.LIST) {
/* 1063 */       if (this.alias != null)
/* 1064 */         doPrintEntry(this.alias, paramPrintStream, true);
/*      */       else
/* 1066 */         doPrintEntries(paramPrintStream);
/*      */     }
/* 1068 */     else if (this.command == Command.PRINTCERT) {
/* 1069 */       doPrintCert(paramPrintStream);
/* 1070 */     } else if (this.command == Command.SELFCERT) {
/* 1071 */       doSelfCert(this.alias, this.dname, this.sigAlgName);
/* 1072 */       this.kssave = true;
/* 1073 */     } else if (this.command == Command.STOREPASSWD) {
/* 1074 */       this.storePassNew = this.newPass;
/* 1075 */       if (this.storePassNew == null) {
/* 1076 */         this.storePassNew = getNewPasswd("keystore password", this.storePass);
/*      */       }
/* 1078 */       this.kssave = true;
/* 1079 */     } else if (this.command == Command.GENCERT) {
/* 1080 */       if (this.alias == null) {
/* 1081 */         this.alias = "mykey";
/*      */       }
/* 1083 */       localObject2 = System.in;
/* 1084 */       if (this.infilename != null) {
/* 1085 */         localObject2 = new FileInputStream(this.infilename);
/*      */       }
/* 1087 */       localObject3 = null;
/* 1088 */       if (this.outfilename != null) {
/* 1089 */         localObject3 = new PrintStream(new FileOutputStream(this.outfilename));
/* 1090 */         paramPrintStream = (PrintStream)localObject3;
/*      */       }
/*      */       try {
/* 1093 */         doGenCert(this.alias, this.sigAlgName, (InputStream)localObject2, paramPrintStream);
/*      */       } finally {
/* 1095 */         if (localObject2 != System.in) {
/* 1096 */           ((InputStream)localObject2).close();
/*      */         }
/* 1098 */         if (localObject3 != null)
/* 1099 */           ((PrintStream)localObject3).close();
/*      */       }
/*      */     }
/* 1102 */     else if (this.command == Command.GENCRL) {
/* 1103 */       if (this.alias == null) {
/* 1104 */         this.alias = "mykey";
/*      */       }
/* 1106 */       localObject2 = null;
/* 1107 */       if (this.filename != null) {
/* 1108 */         localObject2 = new PrintStream(new FileOutputStream(this.filename));
/* 1109 */         paramPrintStream = (PrintStream)localObject2;
/*      */       }
/*      */       try {
/* 1112 */         doGenCRL(paramPrintStream);
/*      */       } finally {
/* 1114 */         if (localObject2 != null)
/* 1115 */           ((PrintStream)localObject2).close();
/*      */       }
/*      */     }
/* 1118 */     else if (this.command == Command.PRINTCERTREQ) {
/* 1119 */       localObject2 = System.in;
/* 1120 */       if (this.filename != null)
/* 1121 */         localObject2 = new FileInputStream(this.filename);
/*      */       try
/*      */       {
/* 1124 */         doPrintCertReq((InputStream)localObject2, paramPrintStream);
/*      */ 
/* 1126 */         if (localObject2 != System.in)
/* 1127 */           ((InputStream)localObject2).close();
/*      */       }
/*      */       finally
/*      */       {
/* 1126 */         if (localObject2 != System.in)
/* 1127 */           ((InputStream)localObject2).close();
/*      */       }
/*      */     }
/* 1130 */     else if (this.command == Command.PRINTCRL) {
/* 1131 */       doPrintCRL(this.filename, paramPrintStream);
/*      */     }
/*      */ 
/* 1135 */     if (this.kssave) {
/* 1136 */       if (this.verbose) {
/* 1137 */         localObject2 = new MessageFormat(rb.getString(".Storing.ksfname."));
/*      */ 
/* 1139 */         localObject3 = new Object[] { this.nullStream ? "keystore" : this.ksfname };
/* 1140 */         System.err.println(((MessageFormat)localObject2).format(localObject3));
/*      */       }
/*      */ 
/* 1143 */       if (this.token) {
/* 1144 */         this.keyStore.store(null, null);
/*      */       } else {
/* 1146 */         localObject2 = this.storePassNew != null ? this.storePassNew : this.storePass;
/* 1147 */         if (this.nullStream) {
/* 1148 */           this.keyStore.store(null, (char[])localObject2);
/*      */         } else {
/* 1150 */           localObject3 = new ByteArrayOutputStream();
/* 1151 */           this.keyStore.store((OutputStream)localObject3, (char[])localObject2);
/* 1152 */           localObject4 = new FileOutputStream(this.ksfname); localObject5 = null;
/*      */           try { ((FileOutputStream)localObject4).write(((ByteArrayOutputStream)localObject3).toByteArray()); }
/*      */           catch (Throwable localThrowable2)
/*      */           {
/* 1152 */             localObject5 = localThrowable2; throw localThrowable2;
/*      */           } finally {
/* 1154 */             if (localObject4 != null) if (localObject5 != null) try { ((FileOutputStream)localObject4).close(); } catch (Throwable localThrowable3) { ((Throwable)localObject5).addSuppressed(localThrowable3); } else ((FileOutputStream)localObject4).close();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doGenCert(String paramString1, String paramString2, InputStream paramInputStream, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1169 */     Certificate localCertificate1 = this.keyStore.getCertificate(paramString1);
/* 1170 */     byte[] arrayOfByte = localCertificate1.getEncoded();
/* 1171 */     X509CertImpl localX509CertImpl1 = new X509CertImpl(arrayOfByte);
/* 1172 */     X509CertInfo localX509CertInfo1 = (X509CertInfo)localX509CertImpl1.get("x509.info");
/*      */ 
/* 1174 */     X500Name localX500Name = (X500Name)localX509CertInfo1.get("subject.dname");
/*      */ 
/* 1177 */     Date localDate1 = getStartDate(this.startDate);
/* 1178 */     Date localDate2 = new Date();
/* 1179 */     localDate2.setTime(localDate1.getTime() + this.validity * 1000L * 24L * 60L * 60L);
/* 1180 */     CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
/*      */ 
/* 1183 */     PrivateKey localPrivateKey = (PrivateKey)recoverKey(paramString1, this.storePass, this.keyPass).fst;
/*      */ 
/* 1185 */     if (paramString2 == null) {
/* 1186 */       paramString2 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/* 1188 */     Signature localSignature = Signature.getInstance(paramString2);
/* 1189 */     localSignature.initSign(localPrivateKey);
/*      */ 
/* 1191 */     X509CertInfo localX509CertInfo2 = new X509CertInfo();
/* 1192 */     localX509CertInfo2.set("validity", localCertificateValidity);
/* 1193 */     localX509CertInfo2.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & 0x7FFFFFFF));
/*      */ 
/* 1195 */     localX509CertInfo2.set("version", new CertificateVersion(2));
/*      */ 
/* 1197 */     localX509CertInfo2.set("algorithmID", new CertificateAlgorithmId(AlgorithmId.getAlgorithmId(paramString2)));
/*      */ 
/* 1200 */     localX509CertInfo2.set("issuer", new CertificateIssuerName(localX500Name));
/*      */ 
/* 1202 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
/* 1203 */     int i = 0;
/* 1204 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */     while (true) {
/* 1206 */       localObject1 = localBufferedReader.readLine();
/* 1207 */       if (localObject1 == null) {
/*      */         break;
/*      */       }
/* 1210 */       if ((((String)localObject1).startsWith("-----BEGIN")) && (((String)localObject1).indexOf("REQUEST") >= 0)) {
/* 1211 */         i = 1;
/*      */       } else {
/* 1213 */         if ((((String)localObject1).startsWith("-----END")) && (((String)localObject1).indexOf("REQUEST") >= 0))
/*      */           break;
/* 1215 */         if (i != 0)
/* 1216 */           localStringBuffer.append((String)localObject1);
/*      */       }
/*      */     }
/* 1219 */     Object localObject1 = new BASE64Decoder().decodeBuffer(new String(localStringBuffer));
/* 1220 */     PKCS10 localPKCS10 = new PKCS10((byte[])localObject1);
/*      */ 
/* 1222 */     localX509CertInfo2.set("key", new CertificateX509Key(localPKCS10.getSubjectPublicKeyInfo()));
/* 1223 */     localX509CertInfo2.set("subject", new CertificateSubjectName(this.dname == null ? localPKCS10.getSubjectName() : new X500Name(this.dname)));
/*      */ 
/* 1225 */     CertificateExtensions localCertificateExtensions = null;
/* 1226 */     Iterator localIterator = localPKCS10.getAttributes().getAttributes().iterator();
/* 1227 */     while (localIterator.hasNext()) {
/* 1228 */       localObject2 = (PKCS10Attribute)localIterator.next();
/* 1229 */       if (((PKCS10Attribute)localObject2).getAttributeId().equals(PKCS9Attribute.EXTENSION_REQUEST_OID)) {
/* 1230 */         localCertificateExtensions = (CertificateExtensions)((PKCS10Attribute)localObject2).getAttributeValue();
/*      */       }
/*      */     }
/* 1233 */     Object localObject2 = createV3Extensions(localCertificateExtensions, null, this.v3ext, localPKCS10.getSubjectPublicKeyInfo(), localCertificate1.getPublicKey());
/*      */ 
/* 1239 */     localX509CertInfo2.set("extensions", localObject2);
/* 1240 */     X509CertImpl localX509CertImpl2 = new X509CertImpl(localX509CertInfo2);
/* 1241 */     localX509CertImpl2.sign(localPrivateKey, paramString2);
/* 1242 */     dumpCert(localX509CertImpl2, paramPrintStream);
/* 1243 */     for (Certificate localCertificate2 : this.keyStore.getCertificateChain(paramString1))
/* 1244 */       if ((localCertificate2 instanceof X509Certificate)) {
/* 1245 */         X509Certificate localX509Certificate = (X509Certificate)localCertificate2;
/* 1246 */         if (!isSelfSigned(localX509Certificate))
/* 1247 */           dumpCert(localX509Certificate, paramPrintStream);
/*      */       }
/*      */   }
/*      */ 
/*      */   private void doGenCRL(PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1255 */     if (this.ids == null) {
/* 1256 */       throw new Exception("Must provide -id when -gencrl");
/*      */     }
/* 1258 */     Certificate localCertificate = this.keyStore.getCertificate(this.alias);
/* 1259 */     byte[] arrayOfByte = localCertificate.getEncoded();
/* 1260 */     X509CertImpl localX509CertImpl = new X509CertImpl(arrayOfByte);
/* 1261 */     X509CertInfo localX509CertInfo = (X509CertInfo)localX509CertImpl.get("x509.info");
/*      */ 
/* 1263 */     X500Name localX500Name = (X500Name)localX509CertInfo.get("subject.dname");
/*      */ 
/* 1266 */     Date localDate1 = getStartDate(this.startDate);
/* 1267 */     Date localDate2 = (Date)localDate1.clone();
/* 1268 */     localDate2.setTime(localDate2.getTime() + this.validity * 1000L * 24L * 60L * 60L);
/* 1269 */     CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
/*      */ 
/* 1273 */     PrivateKey localPrivateKey = (PrivateKey)recoverKey(this.alias, this.storePass, this.keyPass).fst;
/*      */ 
/* 1275 */     if (this.sigAlgName == null) {
/* 1276 */       this.sigAlgName = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/*      */ 
/* 1279 */     X509CRLEntry[] arrayOfX509CRLEntry = new X509CRLEntry[this.ids.size()];
/* 1280 */     for (int i = 0; i < this.ids.size(); i++) {
/* 1281 */       String str = (String)this.ids.get(i);
/* 1282 */       int j = str.indexOf(':');
/* 1283 */       if (j >= 0) {
/* 1284 */         CRLExtensions localCRLExtensions = new CRLExtensions();
/* 1285 */         localCRLExtensions.set("Reason", new CRLReasonCodeExtension(Integer.parseInt(str.substring(j + 1))));
/* 1286 */         arrayOfX509CRLEntry[i] = new X509CRLEntryImpl(new BigInteger(str.substring(0, j)), localDate1, localCRLExtensions);
/*      */       }
/*      */       else {
/* 1289 */         arrayOfX509CRLEntry[i] = new X509CRLEntryImpl(new BigInteger((String)this.ids.get(i)), localDate1);
/*      */       }
/*      */     }
/* 1292 */     X509CRLImpl localX509CRLImpl = new X509CRLImpl(localX500Name, localDate1, localDate2, arrayOfX509CRLEntry);
/* 1293 */     localX509CRLImpl.sign(localPrivateKey, this.sigAlgName);
/* 1294 */     if (this.rfc) {
/* 1295 */       paramPrintStream.println("-----BEGIN X509 CRL-----");
/* 1296 */       new BASE64Encoder().encodeBuffer(localX509CRLImpl.getEncodedInternal(), paramPrintStream);
/* 1297 */       paramPrintStream.println("-----END X509 CRL-----");
/*      */     } else {
/* 1299 */       paramPrintStream.write(localX509CRLImpl.getEncodedInternal());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doCertReq(String paramString1, String paramString2, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1310 */     if (paramString1 == null) {
/* 1311 */       paramString1 = "mykey";
/*      */     }
/*      */ 
/* 1314 */     Pair localPair = recoverKey(paramString1, this.storePass, this.keyPass);
/* 1315 */     PrivateKey localPrivateKey = (PrivateKey)localPair.fst;
/* 1316 */     if (this.keyPass == null) {
/* 1317 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */ 
/* 1320 */     Certificate localCertificate = this.keyStore.getCertificate(paramString1);
/* 1321 */     if (localCertificate == null) {
/* 1322 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
/*      */ 
/* 1324 */       localObject2 = new Object[] { paramString1 };
/* 1325 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/* 1327 */     Object localObject1 = new PKCS10(localCertificate.getPublicKey());
/* 1328 */     Object localObject2 = createV3Extensions(null, null, this.v3ext, localCertificate.getPublicKey(), null);
/*      */ 
/* 1330 */     ((PKCS10)localObject1).getAttributes().setAttribute("extensions", new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, localObject2));
/*      */ 
/* 1334 */     if (paramString2 == null) {
/* 1335 */       paramString2 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/*      */ 
/* 1338 */     Signature localSignature = Signature.getInstance(paramString2);
/* 1339 */     localSignature.initSign(localPrivateKey);
/* 1340 */     X500Name localX500Name = this.dname == null ? new X500Name(((X509Certificate)localCertificate).getSubjectDN().toString()) : new X500Name(this.dname);
/*      */ 
/* 1345 */     ((PKCS10)localObject1).encodeAndSign(localX500Name, localSignature);
/* 1346 */     ((PKCS10)localObject1).print(paramPrintStream);
/*      */   }
/*      */ 
/*      */   private void doDeleteEntry(String paramString)
/*      */     throws Exception
/*      */   {
/* 1353 */     if (!this.keyStore.containsAlias(paramString)) {
/* 1354 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/*      */ 
/* 1356 */       Object[] arrayOfObject = { paramString };
/* 1357 */       throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */     }
/* 1359 */     this.keyStore.deleteEntry(paramString);
/*      */   }
/*      */ 
/*      */   private void doExportCert(String paramString, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1368 */     if ((this.storePass == null) && (!KeyStoreUtil.isWindowsKeyStore(this.storetype)))
/*      */     {
/* 1370 */       printWarning();
/*      */     }
/* 1372 */     if (paramString == null)
/* 1373 */       paramString = "mykey";
/*      */     Object localObject2;
/* 1375 */     if (!this.keyStore.containsAlias(paramString)) {
/* 1376 */       localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/*      */ 
/* 1378 */       localObject2 = new Object[] { paramString };
/* 1379 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 1382 */     Object localObject1 = (X509Certificate)this.keyStore.getCertificate(paramString);
/* 1383 */     if (localObject1 == null) {
/* 1384 */       localObject2 = new MessageFormat(rb.getString("Alias.alias.has.no.certificate"));
/*      */ 
/* 1386 */       Object[] arrayOfObject = { paramString };
/* 1387 */       throw new Exception(((MessageFormat)localObject2).format(arrayOfObject));
/*      */     }
/* 1389 */     dumpCert((Certificate)localObject1, paramPrintStream);
/*      */   }
/*      */ 
/*      */   private char[] promptForKeyPass(String paramString1, String paramString2, char[] paramArrayOfChar)
/*      */     throws Exception
/*      */   {
/* 1399 */     if ("PKCS12".equalsIgnoreCase(this.storetype))
/* 1400 */       return paramArrayOfChar;
/* 1401 */     if ((!this.token) && (!this.protectedPath))
/*      */     {
/* 1404 */       for (int i = 0; i < 3; i++) {
/* 1405 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
/*      */ 
/* 1407 */         Object[] arrayOfObject = { paramString1 };
/* 1408 */         System.err.println(localMessageFormat.format(arrayOfObject));
/* 1409 */         if (paramString2 == null) {
/* 1410 */           System.err.print(rb.getString(".RETURN.if.same.as.keystore.password."));
/*      */         }
/*      */         else {
/* 1413 */           localMessageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
/*      */ 
/* 1415 */           localObject = new Object[] { paramString2 };
/* 1416 */           System.err.print(localMessageFormat.format(localObject));
/*      */         }
/* 1418 */         System.err.flush();
/* 1419 */         Object localObject = Password.readPassword(System.in);
/* 1420 */         this.passwords.add(localObject);
/* 1421 */         if (localObject == null)
/* 1422 */           return paramArrayOfChar;
/* 1423 */         if (localObject.length >= 6) {
/* 1424 */           System.err.print(rb.getString("Re.enter.new.password."));
/* 1425 */           char[] arrayOfChar = Password.readPassword(System.in);
/* 1426 */           this.passwords.add(arrayOfChar);
/* 1427 */           if (!Arrays.equals((char[])localObject, arrayOfChar)) {
/* 1428 */             System.err.println(rb.getString("They.don.t.match.Try.again"));
/*      */           }
/*      */           else
/*      */           {
/* 1432 */             return localObject;
/*      */           }
/*      */         } else { System.err.println(rb.getString("Key.password.is.too.short.must.be.at.least.6.characters")); }
/*      */ 
/*      */       }
/*      */ 
/* 1438 */       if (i == 3) {
/* 1439 */         if (this.command == Command.KEYCLONE) {
/* 1440 */           throw new Exception(rb.getString("Too.many.failures.Key.entry.not.cloned"));
/*      */         }
/*      */ 
/* 1443 */         throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1448 */     return null;
/*      */   }
/*      */ 
/*      */   private void doGenSecretKey(String paramString1, String paramString2, int paramInt)
/*      */     throws Exception
/*      */   {
/* 1457 */     if (paramString1 == null) {
/* 1458 */       paramString1 = "mykey";
/*      */     }
/* 1460 */     if (this.keyStore.containsAlias(paramString1)) {
/* 1461 */       localObject1 = new MessageFormat(rb.getString("Secret.key.not.generated.alias.alias.already.exists"));
/*      */ 
/* 1463 */       localObject2 = new Object[] { paramString1 };
/* 1464 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 1467 */     Object localObject1 = null;
/* 1468 */     Object localObject2 = KeyGenerator.getInstance(paramString2);
/* 1469 */     if (paramInt != -1)
/* 1470 */       ((KeyGenerator)localObject2).init(paramInt);
/* 1471 */     else if ("DES".equalsIgnoreCase(paramString2))
/* 1472 */       ((KeyGenerator)localObject2).init(56);
/* 1473 */     else if ("DESede".equalsIgnoreCase(paramString2))
/* 1474 */       ((KeyGenerator)localObject2).init(168);
/*      */     else {
/* 1476 */       throw new Exception(rb.getString("Please.provide.keysize.for.secret.key.generation"));
/*      */     }
/*      */ 
/* 1480 */     localObject1 = ((KeyGenerator)localObject2).generateKey();
/* 1481 */     if (this.keyPass == null) {
/* 1482 */       this.keyPass = promptForKeyPass(paramString1, null, this.storePass);
/*      */     }
/* 1484 */     this.keyStore.setKeyEntry(paramString1, (Key)localObject1, this.keyPass, null);
/*      */   }
/*      */ 
/*      */   private static String getCompatibleSigAlgName(String paramString)
/*      */     throws Exception
/*      */   {
/* 1493 */     if ("DSA".equalsIgnoreCase(paramString))
/* 1494 */       return "SHA1WithDSA";
/* 1495 */     if ("RSA".equalsIgnoreCase(paramString))
/* 1496 */       return "SHA256WithRSA";
/* 1497 */     if ("EC".equalsIgnoreCase(paramString)) {
/* 1498 */       return "SHA256withECDSA";
/*      */     }
/* 1500 */     throw new Exception(rb.getString("Cannot.derive.signature.algorithm"));
/*      */   }
/*      */ 
/*      */   private void doGenKeyPair(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
/*      */     throws Exception
/*      */   {
/* 1511 */     if (paramInt == -1) {
/* 1512 */       if ("EC".equalsIgnoreCase(paramString3))
/* 1513 */         paramInt = 256;
/* 1514 */       else if ("RSA".equalsIgnoreCase(paramString3))
/* 1515 */         paramInt = 2048;
/*      */       else {
/* 1517 */         paramInt = 1024;
/*      */       }
/*      */     }
/*      */ 
/* 1521 */     if (paramString1 == null)
/* 1522 */       paramString1 = "mykey";
/*      */     Object localObject2;
/* 1525 */     if (this.keyStore.containsAlias(paramString1)) {
/* 1526 */       localObject1 = new MessageFormat(rb.getString("Key.pair.not.generated.alias.alias.already.exists"));
/*      */ 
/* 1528 */       localObject2 = new Object[] { paramString1 };
/* 1529 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 1532 */     if (paramString4 == null) {
/* 1533 */       paramString4 = getCompatibleSigAlgName(paramString3);
/*      */     }
/* 1535 */     Object localObject1 = new CertAndKeyGen(paramString3, paramString4, this.providerName);
/*      */ 
/* 1541 */     if (paramString2 == null)
/* 1542 */       localObject2 = getX500Name();
/*      */     else {
/* 1544 */       localObject2 = new X500Name(paramString2);
/*      */     }
/*      */ 
/* 1547 */     ((CertAndKeyGen)localObject1).generate(paramInt);
/* 1548 */     PrivateKey localPrivateKey = ((CertAndKeyGen)localObject1).getPrivateKey();
/*      */ 
/* 1550 */     X509Certificate[] arrayOfX509Certificate = new X509Certificate[1];
/* 1551 */     arrayOfX509Certificate[0] = ((CertAndKeyGen)localObject1).getSelfCertificate((X500Name)localObject2, getStartDate(this.startDate), this.validity * 24L * 60L * 60L);
/*      */ 
/* 1554 */     if (this.verbose) {
/* 1555 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for"));
/*      */ 
/* 1557 */       Object[] arrayOfObject = { new Integer(paramInt), localPrivateKey.getAlgorithm(), arrayOfX509Certificate[0].getSigAlgName(), new Long(this.validity), localObject2 };
/*      */ 
/* 1562 */       System.err.println(localMessageFormat.format(arrayOfObject));
/*      */     }
/*      */ 
/* 1565 */     if (this.keyPass == null) {
/* 1566 */       this.keyPass = promptForKeyPass(paramString1, null, this.storePass);
/*      */     }
/* 1568 */     this.keyStore.setKeyEntry(paramString1, localPrivateKey, this.keyPass, arrayOfX509Certificate);
/*      */ 
/* 1571 */     doSelfCert(paramString1, null, paramString4);
/*      */   }
/*      */ 
/*      */   private void doCloneEntry(String paramString1, String paramString2, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/* 1583 */     if (paramString1 == null) {
/* 1584 */       paramString1 = "mykey";
/*      */     }
/*      */ 
/* 1587 */     if (this.keyStore.containsAlias(paramString2)) {
/* 1588 */       localObject1 = new MessageFormat(rb.getString("Destination.alias.dest.already.exists"));
/*      */ 
/* 1590 */       localObject2 = new Object[] { paramString2 };
/* 1591 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 1594 */     Object localObject1 = recoverEntry(this.keyStore, paramString1, this.storePass, this.keyPass);
/* 1595 */     Object localObject2 = (KeyStore.Entry)((Pair)localObject1).fst;
/* 1596 */     this.keyPass = ((char[])((Pair)localObject1).snd);
/*      */ 
/* 1598 */     KeyStore.PasswordProtection localPasswordProtection = null;
/*      */ 
/* 1600 */     if (this.keyPass != null) {
/* 1601 */       if ((!paramBoolean) || ("PKCS12".equalsIgnoreCase(this.storetype))) {
/* 1602 */         this.keyPassNew = this.keyPass;
/*      */       }
/* 1604 */       else if (this.keyPassNew == null) {
/* 1605 */         this.keyPassNew = promptForKeyPass(paramString2, paramString1, this.keyPass);
/*      */       }
/*      */ 
/* 1608 */       localPasswordProtection = new KeyStore.PasswordProtection(this.keyPassNew);
/*      */     }
/* 1610 */     this.keyStore.setEntry(paramString2, (KeyStore.Entry)localObject2, localPasswordProtection);
/*      */   }
/*      */ 
/*      */   private void doChangeKeyPasswd(String paramString)
/*      */     throws Exception
/*      */   {
/* 1619 */     if (paramString == null) {
/* 1620 */       paramString = "mykey";
/*      */     }
/* 1622 */     Pair localPair = recoverKey(paramString, this.storePass, this.keyPass);
/* 1623 */     Key localKey = (Key)localPair.fst;
/* 1624 */     if (this.keyPass == null) {
/* 1625 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */ 
/* 1628 */     if (this.keyPassNew == null) {
/* 1629 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("key.password.for.alias."));
/*      */ 
/* 1631 */       Object[] arrayOfObject = { paramString };
/* 1632 */       this.keyPassNew = getNewPasswd(localMessageFormat.format(arrayOfObject), this.keyPass);
/*      */     }
/* 1634 */     this.keyStore.setKeyEntry(paramString, localKey, this.keyPassNew, this.keyStore.getCertificateChain(paramString));
/*      */   }
/*      */ 
/*      */   private void doImportIdentityDatabase(InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/* 1646 */     System.err.println(rb.getString("No.entries.from.identity.database.added"));
/*      */   }
/*      */ 
/*      */   private void doPrintEntry(String paramString, PrintStream paramPrintStream, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/* 1657 */     if ((this.storePass == null) && (paramBoolean) && (!KeyStoreUtil.isWindowsKeyStore(this.storetype)))
/*      */     {
/* 1659 */       printWarning();
/*      */     }
/*      */     Object localObject1;
/*      */     Object[] arrayOfObject1;
/* 1662 */     if (!this.keyStore.containsAlias(paramString)) {
/* 1663 */       localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/*      */ 
/* 1665 */       arrayOfObject1 = new Object[] { paramString };
/* 1666 */       throw new Exception(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     }
/*      */     Object localObject2;
/* 1669 */     if ((this.verbose) || (this.rfc) || (this.debug)) {
/* 1670 */       localObject1 = new MessageFormat(rb.getString("Alias.name.alias"));
/*      */ 
/* 1672 */       arrayOfObject1 = new Object[] { paramString };
/* 1673 */       paramPrintStream.println(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */ 
/* 1675 */       if (!this.token) {
/* 1676 */         localObject1 = new MessageFormat(rb.getString("Creation.date.keyStore.getCreationDate.alias."));
/*      */ 
/* 1678 */         localObject2 = new Object[] { this.keyStore.getCreationDate(paramString) };
/* 1679 */         paramPrintStream.println(((MessageFormat)localObject1).format(localObject2));
/*      */       }
/*      */     }
/* 1682 */     else if (!this.token) {
/* 1683 */       localObject1 = new MessageFormat(rb.getString("alias.keyStore.getCreationDate.alias."));
/*      */ 
/* 1685 */       arrayOfObject1 = new Object[] { paramString, this.keyStore.getCreationDate(paramString) };
/* 1686 */       paramPrintStream.print(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     } else {
/* 1688 */       localObject1 = new MessageFormat(rb.getString("alias."));
/*      */ 
/* 1690 */       arrayOfObject1 = new Object[] { paramString };
/* 1691 */       paramPrintStream.print(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     }
/*      */ 
/* 1695 */     if (this.keyStore.entryInstanceOf(paramString, KeyStore.SecretKeyEntry.class)) {
/* 1696 */       if ((this.verbose) || (this.rfc) || (this.debug)) {
/* 1697 */         localObject1 = new Object[] { "SecretKeyEntry" };
/* 1698 */         paramPrintStream.println(new MessageFormat(rb.getString("Entry.type.type.")).format(localObject1));
/*      */       }
/*      */       else {
/* 1701 */         paramPrintStream.println("SecretKeyEntry, ");
/*      */       }
/* 1703 */     } else if (this.keyStore.entryInstanceOf(paramString, KeyStore.PrivateKeyEntry.class)) {
/* 1704 */       if ((this.verbose) || (this.rfc) || (this.debug)) {
/* 1705 */         localObject1 = new Object[] { "PrivateKeyEntry" };
/* 1706 */         paramPrintStream.println(new MessageFormat(rb.getString("Entry.type.type.")).format(localObject1));
/*      */       }
/*      */       else {
/* 1709 */         paramPrintStream.println("PrivateKeyEntry, ");
/*      */       }
/*      */ 
/* 1713 */       localObject1 = this.keyStore.getCertificateChain(paramString);
/* 1714 */       if (localObject1 != null) {
/* 1715 */         if ((this.verbose) || (this.rfc) || (this.debug)) {
/* 1716 */           paramPrintStream.println(rb.getString("Certificate.chain.length.") + localObject1.length);
/*      */ 
/* 1718 */           for (int i = 0; i < localObject1.length; i++) {
/* 1719 */             localObject2 = new MessageFormat(rb.getString("Certificate.i.1."));
/*      */ 
/* 1721 */             Object[] arrayOfObject3 = { new Integer(i + 1) };
/* 1722 */             paramPrintStream.println(((MessageFormat)localObject2).format(arrayOfObject3));
/* 1723 */             if ((this.verbose) && ((localObject1[i] instanceof X509Certificate)))
/* 1724 */               printX509Cert((X509Certificate)localObject1[i], paramPrintStream);
/* 1725 */             else if (this.debug)
/* 1726 */               paramPrintStream.println(localObject1[i].toString());
/*      */             else
/* 1728 */               dumpCert(localObject1[i], paramPrintStream);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1733 */           paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + getCertFingerPrint("SHA1", localObject1[0]));
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/* 1738 */     else if (this.keyStore.entryInstanceOf(paramString, KeyStore.TrustedCertificateEntry.class))
/*      */     {
/* 1741 */       localObject1 = this.keyStore.getCertificate(paramString);
/* 1742 */       Object[] arrayOfObject2 = { "trustedCertEntry" };
/* 1743 */       localObject2 = new MessageFormat(rb.getString("Entry.type.type.")).format(arrayOfObject2) + "\n";
/*      */ 
/* 1745 */       if ((this.verbose) && ((localObject1 instanceof X509Certificate))) {
/* 1746 */         paramPrintStream.println((String)localObject2);
/* 1747 */         printX509Cert((X509Certificate)localObject1, paramPrintStream);
/* 1748 */       } else if (this.rfc) {
/* 1749 */         paramPrintStream.println((String)localObject2);
/* 1750 */         dumpCert((Certificate)localObject1, paramPrintStream);
/* 1751 */       } else if (this.debug) {
/* 1752 */         paramPrintStream.println(((Certificate)localObject1).toString());
/*      */       } else {
/* 1754 */         paramPrintStream.println("trustedCertEntry, ");
/* 1755 */         paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + getCertFingerPrint("SHA1", (Certificate)localObject1));
/*      */       }
/*      */     }
/*      */     else {
/* 1759 */       paramPrintStream.println(rb.getString("Unknown.Entry.Type"));
/*      */     }
/*      */   }
/*      */ 
/*      */   KeyStore loadSourceKeyStore()
/*      */     throws Exception
/*      */   {
/* 1768 */     int i = 0;
/*      */ 
/* 1770 */     FileInputStream localFileInputStream = null;
/*      */     Object localObject1;
/* 1772 */     if (("PKCS11".equalsIgnoreCase(this.srcstoretype)) || (KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)))
/*      */     {
/* 1774 */       if (!"NONE".equals(this.srcksfname)) {
/* 1775 */         System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { this.srcstoretype }));
/*      */ 
/* 1777 */         System.err.println();
/* 1778 */         tinyHelp();
/*      */       }
/* 1780 */       i = 1;
/*      */     }
/* 1782 */     else if (this.srcksfname != null) {
/* 1783 */       localObject1 = new File(this.srcksfname);
/* 1784 */       if ((((File)localObject1).exists()) && (((File)localObject1).length() == 0L)) {
/* 1785 */         throw new Exception(rb.getString("Source.keystore.file.exists.but.is.empty.") + this.srcksfname);
/*      */       }
/*      */ 
/* 1789 */       localFileInputStream = new FileInputStream((File)localObject1);
/*      */     } else {
/* 1791 */       throw new Exception(rb.getString("Please.specify.srckeystore"));
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1798 */       if (this.srcProviderName == null)
/* 1799 */         localObject1 = KeyStore.getInstance(this.srcstoretype);
/*      */       else {
/* 1801 */         localObject1 = KeyStore.getInstance(this.srcstoretype, this.srcProviderName);
/*      */       }
/*      */ 
/* 1804 */       if ((this.srcstorePass == null) && (!this.srcprotectedPath) && (!KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)))
/*      */       {
/* 1807 */         System.err.print(rb.getString("Enter.source.keystore.password."));
/* 1808 */         System.err.flush();
/* 1809 */         this.srcstorePass = Password.readPassword(System.in);
/* 1810 */         this.passwords.add(this.srcstorePass);
/*      */       }
/*      */ 
/* 1814 */       if (("PKCS12".equalsIgnoreCase(this.srcstoretype)) && 
/* 1815 */         (this.srckeyPass != null) && (this.srcstorePass != null) && (!Arrays.equals(this.srcstorePass, this.srckeyPass)))
/*      */       {
/* 1817 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
/*      */ 
/* 1819 */         Object[] arrayOfObject = { "-srckeypass" };
/* 1820 */         System.err.println(localMessageFormat.format(arrayOfObject));
/* 1821 */         this.srckeyPass = this.srcstorePass;
/*      */       }
/*      */ 
/* 1825 */       ((KeyStore)localObject1).load(localFileInputStream, this.srcstorePass);
/*      */     } finally {
/* 1827 */       if (localFileInputStream != null) {
/* 1828 */         localFileInputStream.close();
/*      */       }
/*      */     }
/*      */ 
/* 1832 */     if ((this.srcstorePass == null) && (!KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)))
/*      */     {
/* 1836 */       System.err.println();
/* 1837 */       System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
/*      */ 
/* 1839 */       System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.the.srckeystore."));
/*      */ 
/* 1841 */       System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
/*      */ 
/* 1843 */       System.err.println();
/*      */     }
/*      */ 
/* 1846 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private void doImportKeyStore()
/*      */     throws Exception
/*      */   {
/* 1856 */     if (this.alias != null) {
/* 1857 */       doImportKeyStoreSingle(loadSourceKeyStore(), this.alias);
/*      */     } else {
/* 1859 */       if ((this.dest != null) || (this.srckeyPass != null) || (this.destKeyPass != null)) {
/* 1860 */         throw new Exception(rb.getString("if.alias.not.specified.destalias.srckeypass.and.destkeypass.must.not.be.specified"));
/*      */       }
/*      */ 
/* 1863 */       doImportKeyStoreAll(loadSourceKeyStore());
/*      */     }
/*      */   }
/*      */ 
/*      */   private int doImportKeyStoreSingle(KeyStore paramKeyStore, String paramString)
/*      */     throws Exception
/*      */   {
/* 1883 */     String str = this.dest == null ? paramString : this.dest;
/*      */ 
/* 1885 */     if (this.keyStore.containsAlias(str)) {
/* 1886 */       localObject1 = new Object[] { paramString };
/* 1887 */       if (this.noprompt) {
/* 1888 */         System.err.println(new MessageFormat(rb.getString("Warning.Overwriting.existing.alias.alias.in.destination.keystore")).format(localObject1));
/*      */       }
/*      */       else {
/* 1891 */         localObject2 = getYesNoReply(new MessageFormat(rb.getString("Existing.entry.alias.alias.exists.overwrite.no.")).format(localObject1));
/*      */ 
/* 1893 */         if ("NO".equals(localObject2)) {
/* 1894 */           str = inputStringFromStdin(rb.getString("Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry."));
/*      */ 
/* 1896 */           if ("".equals(str)) {
/* 1897 */             System.err.println(new MessageFormat(rb.getString("Entry.for.alias.alias.not.imported.")).format(localObject1));
/*      */ 
/* 1900 */             return 0;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1906 */     Object localObject1 = recoverEntry(paramKeyStore, paramString, this.srcstorePass, this.srckeyPass);
/* 1907 */     Object localObject2 = (KeyStore.Entry)((Pair)localObject1).fst;
/*      */ 
/* 1909 */     KeyStore.PasswordProtection localPasswordProtection = null;
/*      */ 
/* 1915 */     if (this.destKeyPass != null)
/* 1916 */       localPasswordProtection = new KeyStore.PasswordProtection(this.destKeyPass);
/* 1917 */     else if (((Pair)localObject1).snd != null) {
/* 1918 */       localPasswordProtection = new KeyStore.PasswordProtection((char[])((Pair)localObject1).snd);
/*      */     }
/*      */     try
/*      */     {
/* 1922 */       this.keyStore.setEntry(str, (KeyStore.Entry)localObject2, localPasswordProtection);
/* 1923 */       return 1;
/*      */     } catch (KeyStoreException localKeyStoreException) {
/* 1925 */       Object[] arrayOfObject = { paramString, localKeyStoreException.toString() };
/* 1926 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported."));
/*      */ 
/* 1928 */       System.err.println(localMessageFormat.format(arrayOfObject));
/* 1929 */     }return 2;
/*      */   }
/*      */ 
/*      */   private void doImportKeyStoreAll(KeyStore paramKeyStore)
/*      */     throws Exception
/*      */   {
/* 1935 */     int i = 0;
/* 1936 */     int j = paramKeyStore.size();
/* 1937 */     Object localObject1 = paramKeyStore.aliases();
/* 1938 */     while (((Enumeration)localObject1).hasMoreElements()) {
/* 1939 */       localObject2 = (String)((Enumeration)localObject1).nextElement();
/* 1940 */       int k = doImportKeyStoreSingle(paramKeyStore, (String)localObject2);
/*      */       Object localObject3;
/* 1941 */       if (k == 1) {
/* 1942 */         i++;
/* 1943 */         localObject3 = new Object[] { localObject2 };
/* 1944 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Entry.for.alias.alias.successfully.imported."));
/* 1945 */         System.err.println(localMessageFormat.format(localObject3));
/* 1946 */       } else if ((k == 2) && 
/* 1947 */         (!this.noprompt)) {
/* 1948 */         localObject3 = getYesNoReply("Do you want to quit the import process? [no]:  ");
/* 1949 */         if ("YES".equals(localObject3))
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1955 */     localObject1 = new Object[] { Integer.valueOf(i), Integer.valueOf(j - i) };
/* 1956 */     Object localObject2 = new MessageFormat(rb.getString("Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled"));
/*      */ 
/* 1958 */     System.err.println(((MessageFormat)localObject2).format(localObject1));
/*      */   }
/*      */ 
/*      */   private void doPrintEntries(PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1967 */     if ((this.storePass == null) && (!KeyStoreUtil.isWindowsKeyStore(this.storetype)))
/*      */     {
/* 1969 */       printWarning();
/*      */     }
/* 1971 */     else paramPrintStream.println();
/*      */ 
/* 1974 */     paramPrintStream.println(rb.getString("Keystore.type.") + this.keyStore.getType());
/* 1975 */     paramPrintStream.println(rb.getString("Keystore.provider.") + this.keyStore.getProvider().getName());
/*      */ 
/* 1977 */     paramPrintStream.println();
/*      */ 
/* 1980 */     MessageFormat localMessageFormat = this.keyStore.size() == 1 ? new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entry")) : new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entries"));
/*      */ 
/* 1985 */     Object[] arrayOfObject = { new Integer(this.keyStore.size()) };
/* 1986 */     paramPrintStream.println(localMessageFormat.format(arrayOfObject));
/* 1987 */     paramPrintStream.println();
/*      */ 
/* 1989 */     Enumeration localEnumeration = this.keyStore.aliases();
/* 1990 */     while (localEnumeration.hasMoreElements()) {
/* 1991 */       String str = (String)localEnumeration.nextElement();
/* 1992 */       doPrintEntry(str, paramPrintStream, false);
/* 1993 */       if ((this.verbose) || (this.rfc)) {
/* 1994 */         paramPrintStream.println(rb.getString("NEWLINE"));
/* 1995 */         paramPrintStream.println(rb.getString("STAR"));
/*      */ 
/* 1997 */         paramPrintStream.println(rb.getString("STARNN"));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static <T> Iterable<T> e2i(Enumeration<T> paramEnumeration)
/*      */   {
/* 2004 */     return new Iterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/* 2007 */         return new Iterator()
/*      */         {
/*      */           public boolean hasNext() {
/* 2010 */             return KeyTool.1.this.val$e.hasMoreElements();
/*      */           }
/*      */ 
/*      */           public T next() {
/* 2014 */             return KeyTool.1.this.val$e.nextElement();
/*      */           }
/*      */           public void remove() {
/* 2017 */             throw new UnsupportedOperationException("Not supported yet.");
/*      */           }
/*      */         };
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static Collection<? extends CRL> loadCRLs(String paramString)
/*      */     throws Exception
/*      */   {
/* 2030 */     Object localObject1 = null;
/* 2031 */     URI localURI = null;
/* 2032 */     if (paramString == null)
/* 2033 */       localObject1 = System.in;
/*      */     else {
/*      */       try {
/* 2036 */         localURI = new URI(paramString);
/* 2037 */         if (!localURI.getScheme().equals("ldap"))
/*      */         {
/* 2040 */           localObject1 = localURI.toURL().openStream();
/*      */         }
/*      */       } catch (Exception localException1) {
/*      */         try {
/* 2044 */           localObject1 = new FileInputStream(paramString);
/*      */         } catch (Exception localException2) {
/* 2046 */           if ((localURI == null) || (localURI.getScheme() == null)) {
/* 2047 */             throw localException2;
/*      */           }
/* 2049 */           throw localException1;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2054 */     if (localObject1 != null)
/*      */     {
/*      */       try
/*      */       {
/* 2060 */         localObject2 = new ByteArrayOutputStream();
/* 2061 */         localObject3 = new byte[4096];
/*      */         while (true) {
/* 2063 */           int i = ((InputStream)localObject1).read((byte[])localObject3);
/* 2064 */           if (i < 0) break;
/* 2065 */           ((ByteArrayOutputStream)localObject2).write((byte[])localObject3, 0, i);
/*      */         }
/* 2067 */         return CertificateFactory.getInstance("X509").generateCRLs(new ByteArrayInputStream(((ByteArrayOutputStream)localObject2).toByteArray()));
/*      */       }
/*      */       finally {
/* 2070 */         if (localObject1 != System.in) {
/* 2071 */           ((InputStream)localObject1).close();
/*      */         }
/*      */       }
/*      */     }
/* 2075 */     Object localObject2 = localURI.getPath();
/* 2076 */     if (((String)localObject2).charAt(0) == '/') localObject2 = ((String)localObject2).substring(1);
/* 2077 */     Object localObject3 = new LDAPCertStoreHelper();
/* 2078 */     Object localObject4 = ((LDAPCertStoreHelper)localObject3).getCertStore(localURI);
/* 2079 */     X509CRLSelector localX509CRLSelector = ((LDAPCertStoreHelper)localObject3).wrap(new X509CRLSelector(), null, (String)localObject2);
/*      */ 
/* 2081 */     return ((CertStore)localObject4).getCRLs(localX509CRLSelector);
/*      */   }
/*      */ 
/*      */   public static List<CRL> readCRLsFromCert(X509Certificate paramX509Certificate)
/*      */     throws Exception
/*      */   {
/* 2091 */     ArrayList localArrayList = new ArrayList();
/* 2092 */     CRLDistributionPointsExtension localCRLDistributionPointsExtension = X509CertImpl.toImpl(paramX509Certificate).getCRLDistributionPointsExtension();
/*      */ 
/* 2094 */     if (localCRLDistributionPointsExtension == null) return localArrayList;
/* 2095 */     for (DistributionPoint localDistributionPoint : (List)localCRLDistributionPointsExtension.get("points"))
/*      */     {
/* 2097 */       GeneralNames localGeneralNames = localDistributionPoint.getFullName();
/* 2098 */       if (localGeneralNames != null) {
/* 2099 */         for (GeneralName localGeneralName : localGeneralNames.names()) {
/* 2100 */           if (localGeneralName.getType() == 6) {
/* 2101 */             URIName localURIName = (URIName)localGeneralName.getName();
/* 2102 */             for (CRL localCRL : loadCRLs(localURIName.getName())) {
/* 2103 */               if ((localCRL instanceof X509CRL)) {
/* 2104 */                 localArrayList.add((X509CRL)localCRL);
/*      */               }
/*      */             }
/* 2107 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2112 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   private static String verifyCRL(KeyStore paramKeyStore, CRL paramCRL) throws Exception
/*      */   {
/* 2117 */     X509CRLImpl localX509CRLImpl = (X509CRLImpl)paramCRL;
/* 2118 */     X500Principal localX500Principal = localX509CRLImpl.getIssuerX500Principal();
/* 2119 */     for (String str : e2i(paramKeyStore.aliases())) {
/* 2120 */       Certificate localCertificate = paramKeyStore.getCertificate(str);
/* 2121 */       if ((localCertificate instanceof X509Certificate)) {
/* 2122 */         X509Certificate localX509Certificate = (X509Certificate)localCertificate;
/* 2123 */         if (localX509Certificate.getSubjectX500Principal().equals(localX500Principal))
/*      */           try {
/* 2125 */             ((X509CRLImpl)paramCRL).verify(localCertificate.getPublicKey());
/* 2126 */             return str;
/*      */           }
/*      */           catch (Exception localException) {
/*      */           }
/*      */       }
/*      */     }
/* 2132 */     return null;
/*      */   }
/*      */ 
/*      */   private void doPrintCRL(String paramString, PrintStream paramPrintStream) throws Exception
/*      */   {
/* 2137 */     for (CRL localCRL : loadCRLs(paramString)) {
/* 2138 */       printCRL(localCRL, paramPrintStream);
/* 2139 */       String str = null;
/* 2140 */       if (this.caks != null) {
/* 2141 */         str = verifyCRL(this.caks, localCRL);
/* 2142 */         if (str != null) {
/* 2143 */           System.out.println("Verified by " + str + " in cacerts");
/*      */         }
/*      */       }
/* 2146 */       if ((str == null) && (this.keyStore != null)) {
/* 2147 */         str = verifyCRL(this.keyStore, localCRL);
/* 2148 */         if (str != null) {
/* 2149 */           System.out.println("Verified by " + str + " in keystore");
/*      */         }
/*      */       }
/* 2152 */       if (str == null) {
/* 2153 */         paramPrintStream.println(rb.getString("STAR"));
/*      */ 
/* 2155 */         paramPrintStream.println("WARNING: not verified. Make sure -keystore and -alias are correct.");
/* 2156 */         paramPrintStream.println(rb.getString("STARNN"));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void printCRL(CRL paramCRL, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2164 */     if (this.rfc) {
/* 2165 */       X509CRL localX509CRL = (X509CRL)paramCRL;
/* 2166 */       paramPrintStream.println("-----BEGIN X509 CRL-----");
/* 2167 */       new BASE64Encoder().encodeBuffer(localX509CRL.getEncoded(), paramPrintStream);
/* 2168 */       paramPrintStream.println("-----END X509 CRL-----");
/*      */     } else {
/* 2170 */       paramPrintStream.println(paramCRL.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doPrintCertReq(InputStream paramInputStream, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2177 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
/* 2178 */     StringBuffer localStringBuffer = new StringBuffer();
/* 2179 */     int i = 0;
/*      */     while (true) {
/* 2181 */       localObject = localBufferedReader.readLine();
/* 2182 */       if (localObject == null) break;
/* 2183 */       if (i == 0) {
/* 2184 */         if (((String)localObject).startsWith("-----"))
/* 2185 */           i = 1;
/*      */       }
/*      */       else {
/* 2188 */         if (((String)localObject).startsWith("-----")) {
/*      */           break;
/*      */         }
/* 2191 */         localStringBuffer.append((String)localObject);
/*      */       }
/*      */     }
/* 2194 */     Object localObject = new PKCS10(new BASE64Decoder().decodeBuffer(new String(localStringBuffer)));
/*      */ 
/* 2196 */     PublicKey localPublicKey = ((PKCS10)localObject).getSubjectPublicKeyInfo();
/* 2197 */     paramPrintStream.printf(rb.getString("PKCS.10.Certificate.Request.Version.1.0.Subject.s.Public.Key.s.format.s.key."), new Object[] { ((PKCS10)localObject).getSubjectName(), localPublicKey.getFormat(), localPublicKey.getAlgorithm() });
/*      */ 
/* 2199 */     for (PKCS10Attribute localPKCS10Attribute : ((PKCS10)localObject).getAttributes().getAttributes()) {
/* 2200 */       ObjectIdentifier localObjectIdentifier = localPKCS10Attribute.getAttributeId();
/* 2201 */       if (localObjectIdentifier.equals(PKCS9Attribute.EXTENSION_REQUEST_OID)) {
/* 2202 */         CertificateExtensions localCertificateExtensions = (CertificateExtensions)localPKCS10Attribute.getAttributeValue();
/* 2203 */         if (localCertificateExtensions != null)
/* 2204 */           printExtensions(rb.getString("Extension.Request."), localCertificateExtensions, paramPrintStream);
/*      */       }
/*      */       else {
/* 2207 */         paramPrintStream.println(localPKCS10Attribute.getAttributeId());
/* 2208 */         paramPrintStream.println(localPKCS10Attribute.getAttributeValue());
/*      */       }
/*      */     }
/* 2211 */     if (this.debug)
/* 2212 */       paramPrintStream.println(localObject);
/*      */   }
/*      */ 
/*      */   private void printCertFromStream(InputStream paramInputStream, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2223 */     Collection localCollection = null;
/*      */     try {
/* 2225 */       localCollection = this.cf.generateCertificates(paramInputStream);
/*      */     } catch (CertificateException localCertificateException) {
/* 2227 */       throw new Exception(rb.getString("Failed.to.parse.input"), localCertificateException);
/*      */     }
/* 2229 */     if (localCollection.isEmpty()) {
/* 2230 */       throw new Exception(rb.getString("Empty.input"));
/*      */     }
/* 2232 */     Certificate[] arrayOfCertificate = (Certificate[])localCollection.toArray(new Certificate[localCollection.size()]);
/* 2233 */     for (int i = 0; i < arrayOfCertificate.length; i++) {
/* 2234 */       X509Certificate localX509Certificate = null;
/*      */       try {
/* 2236 */         localX509Certificate = (X509Certificate)arrayOfCertificate[i];
/*      */       } catch (ClassCastException localClassCastException) {
/* 2238 */         throw new Exception(rb.getString("Not.X.509.certificate"));
/*      */       }
/* 2240 */       if (arrayOfCertificate.length > 1) {
/* 2241 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Certificate.i.1."));
/*      */ 
/* 2243 */         Object[] arrayOfObject = { new Integer(i + 1) };
/* 2244 */         paramPrintStream.println(localMessageFormat.format(arrayOfObject));
/*      */       }
/* 2246 */       if (this.rfc) dumpCert(localX509Certificate, paramPrintStream); else
/* 2247 */         printX509Cert(localX509Certificate, paramPrintStream);
/* 2248 */       if (i < arrayOfCertificate.length - 1)
/* 2249 */         paramPrintStream.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doPrintCert(final PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/*      */     Object localObject1;
/*      */     Object localObject2;
/*      */     Object localObject3;
/* 2255 */     if (this.jarfile != null) {
/* 2256 */       localObject1 = new JarFile(this.jarfile, true);
/* 2257 */       localObject2 = ((JarFile)localObject1).entries();
/* 2258 */       localObject3 = new HashSet();
/* 2259 */       byte[] arrayOfByte = new byte[8192];
/* 2260 */       int i = 0;
/* 2261 */       while (((Enumeration)localObject2).hasMoreElements()) {
/* 2262 */         JarEntry localJarEntry = (JarEntry)((Enumeration)localObject2).nextElement();
/* 2263 */         InputStream localInputStream = null;
/*      */         try
/*      */         {
/* 2265 */           localInputStream = ((JarFile)localObject1).getInputStream(localJarEntry);
/* 2266 */           while (localInputStream.read(arrayOfByte) != -1);
/*      */         }
/*      */         finally
/*      */         {
/* 2272 */           if (localInputStream != null) {
/* 2273 */             localInputStream.close();
/*      */           }
/*      */         }
/* 2276 */         CodeSigner[] arrayOfCodeSigner1 = localJarEntry.getCodeSigners();
/* 2277 */         if (arrayOfCodeSigner1 != null)
/*      */         {
/*      */           Object localObject6;
/*      */           Object localObject7;
/* 2278 */           for (CodeSigner localCodeSigner : arrayOfCodeSigner1) {
/* 2279 */             if (!((Set)localObject3).contains(localCodeSigner)) {
/* 2280 */               ((Set)localObject3).add(localCodeSigner);
/* 2281 */               paramPrintStream.printf(rb.getString("Signer.d."), new Object[] { Integer.valueOf(++i) });
/* 2282 */               paramPrintStream.println();
/* 2283 */               paramPrintStream.println();
/* 2284 */               paramPrintStream.println(rb.getString("Signature."));
/* 2285 */               paramPrintStream.println();
/* 2286 */               for (Object localObject5 = localCodeSigner.getSignerCertPath().getCertificates().iterator(); ((Iterator)localObject5).hasNext(); ) { localObject6 = (Certificate)((Iterator)localObject5).next();
/* 2287 */                 localObject7 = (X509Certificate)localObject6;
/* 2288 */                 if (this.rfc) {
/* 2289 */                   paramPrintStream.println(rb.getString("Certificate.owner.") + ((X509Certificate)localObject7).getSubjectDN() + "\n");
/* 2290 */                   dumpCert((Certificate)localObject7, paramPrintStream);
/*      */                 } else {
/* 2292 */                   printX509Cert((X509Certificate)localObject7, paramPrintStream);
/*      */                 }
/* 2294 */                 paramPrintStream.println();
/*      */               }
/* 2296 */               localObject5 = localCodeSigner.getTimestamp();
/* 2297 */               if (localObject5 != null) {
/* 2298 */                 paramPrintStream.println(rb.getString("Timestamp."));
/* 2299 */                 paramPrintStream.println();
/* 2300 */                 for (localObject6 = ((Timestamp)localObject5).getSignerCertPath().getCertificates().iterator(); ((Iterator)localObject6).hasNext(); ) { localObject7 = (Certificate)((Iterator)localObject6).next();
/* 2301 */                   X509Certificate localX509Certificate = (X509Certificate)localObject7;
/* 2302 */                   if (this.rfc) {
/* 2303 */                     paramPrintStream.println(rb.getString("Certificate.owner.") + localX509Certificate.getSubjectDN() + "\n");
/* 2304 */                     dumpCert(localX509Certificate, paramPrintStream);
/*      */                   } else {
/* 2306 */                     printX509Cert(localX509Certificate, paramPrintStream);
/*      */                   }
/* 2308 */                   paramPrintStream.println();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2315 */       ((JarFile)localObject1).close();
/* 2316 */       if (((Set)localObject3).size() == 0)
/* 2317 */         paramPrintStream.println(rb.getString("Not.a.signed.jar.file"));
/*      */     }
/* 2319 */     else if (this.sslserver != null) {
/* 2320 */       localObject1 = SSLContext.getInstance("SSL");
/* 2321 */       localObject2 = new boolean[1];
/* 2322 */       ((SSLContext)localObject1).init(null, new TrustManager[] { new X509ExtendedTrustManager()
/*      */       {
/*      */         public X509Certificate[] getAcceptedIssuers()
/*      */         {
/* 2326 */           return new X509Certificate[0];
/*      */         }
/*      */ 
/*      */         public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
/*      */         {
/* 2331 */           throw new UnsupportedOperationException();
/*      */         }
/*      */ 
/*      */         public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString, Socket paramAnonymousSocket)
/*      */           throws CertificateException
/*      */         {
/* 2337 */           throw new UnsupportedOperationException();
/*      */         }
/*      */ 
/*      */         public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString, SSLEngine paramAnonymousSSLEngine)
/*      */           throws CertificateException
/*      */         {
/* 2343 */           throw new UnsupportedOperationException();
/*      */         }
/*      */ 
/*      */         public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
/*      */         {
/* 2348 */           for (int i = 0; i < paramAnonymousArrayOfX509Certificate.length; i++) {
/* 2349 */             X509Certificate localX509Certificate = paramAnonymousArrayOfX509Certificate[i];
/*      */             try {
/* 2351 */               if (KeyTool.this.rfc) {
/* 2352 */                 KeyTool.this.dumpCert(localX509Certificate, paramPrintStream);
/*      */               } else {
/* 2354 */                 paramPrintStream.println("Certificate #" + i);
/* 2355 */                 paramPrintStream.println("====================================");
/* 2356 */                 KeyTool.this.printX509Cert(localX509Certificate, paramPrintStream);
/* 2357 */                 paramPrintStream.println();
/*      */               }
/*      */             } catch (Exception localException) {
/* 2360 */               if (KeyTool.this.debug) {
/* 2361 */                 localException.printStackTrace();
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2367 */           if (paramAnonymousArrayOfX509Certificate.length > 0)
/* 2368 */             this.val$certPrinted[0] = true;
/*      */         }
/*      */ 
/*      */         public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString, Socket paramAnonymousSocket)
/*      */           throws CertificateException
/*      */         {
/* 2375 */           checkServerTrusted(paramAnonymousArrayOfX509Certificate, paramAnonymousString);
/*      */         }
/*      */ 
/*      */         public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString, SSLEngine paramAnonymousSSLEngine)
/*      */           throws CertificateException
/*      */         {
/* 2381 */           checkServerTrusted(paramAnonymousArrayOfX509Certificate, paramAnonymousString);
/*      */         }
/*      */       }
/*      */        }, null);
/*      */ 
/* 2385 */       HttpsURLConnection.setDefaultSSLSocketFactory(((SSLContext)localObject1).getSocketFactory());
/* 2386 */       HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
/*      */       {
/*      */         public boolean verify(String paramAnonymousString, SSLSession paramAnonymousSSLSession) {
/* 2389 */           return true;
/*      */         }
/*      */       });
/* 2397 */       localObject3 = null;
/*      */       try {
/* 2399 */         new URL("https://" + this.sslserver).openConnection().connect();
/*      */       } catch (Exception localException1) {
/* 2401 */         localObject3 = localException1;
/*      */       }
/*      */ 
/* 2405 */       if (localObject2[0] == 0) {
/* 2406 */         Exception localException2 = new Exception(rb.getString("No.certificate.from.the.SSL.server"));
/*      */ 
/* 2408 */         if (localObject3 != null) {
/* 2409 */           localException2.initCause((Throwable)localObject3);
/*      */         }
/* 2411 */         throw localException2;
/*      */       }
/*      */     } else {
/* 2414 */       localObject1 = System.in;
/* 2415 */       if (this.filename != null)
/* 2416 */         localObject1 = new FileInputStream(this.filename);
/*      */       try
/*      */       {
/* 2419 */         printCertFromStream((InputStream)localObject1, paramPrintStream);
/*      */ 
/* 2421 */         if (localObject1 != System.in)
/* 2422 */           ((InputStream)localObject1).close();
/*      */       }
/*      */       finally
/*      */       {
/* 2421 */         if (localObject1 != System.in)
/* 2422 */           ((InputStream)localObject1).close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doSelfCert(String paramString1, String paramString2, String paramString3)
/*      */     throws Exception
/*      */   {
/* 2434 */     if (paramString1 == null) {
/* 2435 */       paramString1 = "mykey";
/*      */     }
/*      */ 
/* 2438 */     Pair localPair = recoverKey(paramString1, this.storePass, this.keyPass);
/* 2439 */     PrivateKey localPrivateKey = (PrivateKey)localPair.fst;
/* 2440 */     if (this.keyPass == null) {
/* 2441 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */ 
/* 2444 */     if (paramString3 == null) {
/* 2445 */       paramString3 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/*      */ 
/* 2449 */     Certificate localCertificate = this.keyStore.getCertificate(paramString1);
/* 2450 */     if (localCertificate == null) {
/* 2451 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key"));
/*      */ 
/* 2453 */       localObject2 = new Object[] { paramString1 };
/* 2454 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/* 2456 */     if (!(localCertificate instanceof X509Certificate)) {
/* 2457 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.X.509.certificate"));
/*      */ 
/* 2459 */       localObject2 = new Object[] { paramString1 };
/* 2460 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 2465 */     Object localObject1 = localCertificate.getEncoded();
/* 2466 */     Object localObject2 = new X509CertImpl((byte[])localObject1);
/* 2467 */     X509CertInfo localX509CertInfo = (X509CertInfo)((X509CertImpl)localObject2).get("x509.info");
/*      */ 
/* 2472 */     Date localDate1 = getStartDate(this.startDate);
/* 2473 */     Date localDate2 = new Date();
/* 2474 */     localDate2.setTime(localDate1.getTime() + this.validity * 1000L * 24L * 60L * 60L);
/* 2475 */     CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
/*      */ 
/* 2477 */     localX509CertInfo.set("validity", localCertificateValidity);
/*      */ 
/* 2480 */     localX509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & 0x7FFFFFFF));
/*      */     X500Name localX500Name;
/* 2485 */     if (paramString2 == null)
/*      */     {
/* 2487 */       localX500Name = (X500Name)localX509CertInfo.get("subject.dname");
/*      */     }
/*      */     else
/*      */     {
/* 2491 */       localX500Name = new X500Name(paramString2);
/* 2492 */       localX509CertInfo.set("subject.dname", localX500Name);
/*      */     }
/*      */ 
/* 2496 */     localX509CertInfo.set("issuer.dname", localX500Name);
/*      */ 
/* 2503 */     X509CertImpl localX509CertImpl = new X509CertImpl(localX509CertInfo);
/* 2504 */     localX509CertImpl.sign(localPrivateKey, paramString3);
/* 2505 */     AlgorithmId localAlgorithmId = (AlgorithmId)localX509CertImpl.get("x509.algorithm");
/* 2506 */     localX509CertInfo.set("algorithmID.algorithm", localAlgorithmId);
/*      */ 
/* 2509 */     localX509CertInfo.set("version", new CertificateVersion(2));
/*      */ 
/* 2512 */     CertificateExtensions localCertificateExtensions = createV3Extensions(null, (CertificateExtensions)localX509CertInfo.get("extensions"), this.v3ext, localCertificate.getPublicKey(), null);
/*      */ 
/* 2518 */     localX509CertInfo.set("extensions", localCertificateExtensions);
/*      */ 
/* 2520 */     localX509CertImpl = new X509CertImpl(localX509CertInfo);
/* 2521 */     localX509CertImpl.sign(localPrivateKey, paramString3);
/*      */ 
/* 2524 */     this.keyStore.setKeyEntry(paramString1, localPrivateKey, this.keyPass != null ? this.keyPass : this.storePass, new Certificate[] { localX509CertImpl });
/*      */ 
/* 2528 */     if (this.verbose) {
/* 2529 */       System.err.println(rb.getString("New.certificate.self.signed."));
/* 2530 */       System.err.print(localX509CertImpl.toString());
/* 2531 */       System.err.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean installReply(String paramString, InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/* 2552 */     if (paramString == null) {
/* 2553 */       paramString = "mykey";
/*      */     }
/*      */ 
/* 2556 */     Pair localPair = recoverKey(paramString, this.storePass, this.keyPass);
/* 2557 */     PrivateKey localPrivateKey = (PrivateKey)localPair.fst;
/* 2558 */     if (this.keyPass == null) {
/* 2559 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */ 
/* 2562 */     Certificate localCertificate = this.keyStore.getCertificate(paramString);
/* 2563 */     if (localCertificate == null) {
/* 2564 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
/*      */ 
/* 2566 */       localObject2 = new Object[] { paramString };
/* 2567 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 2571 */     Object localObject1 = this.cf.generateCertificates(paramInputStream);
/* 2572 */     if (((Collection)localObject1).isEmpty()) {
/* 2573 */       throw new Exception(rb.getString("Reply.has.no.certificates"));
/*      */     }
/* 2575 */     Object localObject2 = (Certificate[])((Collection)localObject1).toArray(new Certificate[((Collection)localObject1).size()]);
/*      */     Certificate[] arrayOfCertificate;
/* 2577 */     if (localObject2.length == 1)
/*      */     {
/* 2579 */       arrayOfCertificate = establishCertChain(localCertificate, localObject2[0]);
/*      */     }
/*      */     else {
/* 2582 */       arrayOfCertificate = validateReply(paramString, localCertificate, (Certificate[])localObject2);
/*      */     }
/*      */ 
/* 2587 */     if (arrayOfCertificate != null) {
/* 2588 */       this.keyStore.setKeyEntry(paramString, localPrivateKey, this.keyPass != null ? this.keyPass : this.storePass, arrayOfCertificate);
/*      */ 
/* 2591 */       return true;
/*      */     }
/* 2593 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean addTrustedCert(String paramString, InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/* 2605 */     if (paramString == null) {
/* 2606 */       throw new Exception(rb.getString("Must.specify.alias"));
/*      */     }
/* 2608 */     if (this.keyStore.containsAlias(paramString)) {
/* 2609 */       localObject1 = new MessageFormat(rb.getString("Certificate.not.imported.alias.alias.already.exists"));
/*      */ 
/* 2611 */       Object[] arrayOfObject1 = { paramString };
/* 2612 */       throw new Exception(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     }
/*      */ 
/* 2616 */     Object localObject1 = null;
/*      */     try {
/* 2618 */       localObject1 = (X509Certificate)this.cf.generateCertificate(paramInputStream);
/*      */     } catch (ClassCastException localClassCastException) {
/* 2620 */       throw new Exception(rb.getString("Input.not.an.X.509.certificate"));
/*      */     } catch (CertificateException localCertificateException) {
/* 2622 */       throw new Exception(rb.getString("Input.not.an.X.509.certificate"));
/*      */     }
/*      */ 
/* 2626 */     int i = 0;
/* 2627 */     if (isSelfSigned((X509Certificate)localObject1)) {
/* 2628 */       ((X509Certificate)localObject1).verify(((X509Certificate)localObject1).getPublicKey());
/* 2629 */       i = 1;
/*      */     }
/*      */ 
/* 2632 */     if (this.noprompt) {
/* 2633 */       this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2634 */       return true;
/*      */     }
/*      */ 
/* 2638 */     String str1 = null;
/* 2639 */     String str2 = this.keyStore.getCertificateAlias((Certificate)localObject1);
/*      */     Object localObject2;
/*      */     Object[] arrayOfObject2;
/* 2640 */     if (str2 != null) {
/* 2641 */       localObject2 = new MessageFormat(rb.getString("Certificate.already.exists.in.keystore.under.alias.trustalias."));
/*      */ 
/* 2643 */       arrayOfObject2 = new Object[] { str2 };
/* 2644 */       System.err.println(((MessageFormat)localObject2).format(arrayOfObject2));
/* 2645 */       str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.no."));
/*      */     }
/* 2647 */     else if (i != 0) {
/* 2648 */       if ((this.trustcacerts) && (this.caks != null) && ((str2 = this.caks.getCertificateAlias((Certificate)localObject1)) != null))
/*      */       {
/* 2650 */         localObject2 = new MessageFormat(rb.getString("Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias."));
/*      */ 
/* 2652 */         arrayOfObject2 = new Object[] { str2 };
/* 2653 */         System.err.println(((MessageFormat)localObject2).format(arrayOfObject2));
/* 2654 */         str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.to.your.own.keystore.no."));
/*      */       }
/*      */ 
/* 2657 */       if (str2 == null)
/*      */       {
/* 2660 */         printX509Cert((X509Certificate)localObject1, System.out);
/* 2661 */         str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
/*      */       }
/*      */     }
/*      */ 
/* 2665 */     if (str1 != null) {
/* 2666 */       if ("YES".equals(str1)) {
/* 2667 */         this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2668 */         return true;
/*      */       }
/* 2670 */       return false;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2676 */       localObject2 = establishCertChain(null, (Certificate)localObject1);
/* 2677 */       if (localObject2 != null) {
/* 2678 */         this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2679 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 2684 */       printX509Cert((X509Certificate)localObject1, System.out);
/* 2685 */       str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
/*      */ 
/* 2687 */       if ("YES".equals(str1)) {
/* 2688 */         this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2689 */         return true;
/*      */       }
/* 2691 */       return false;
/*      */     }
/*      */ 
/* 2695 */     return false;
/*      */   }
/*      */ 
/*      */   private char[] getNewPasswd(String paramString, char[] paramArrayOfChar)
/*      */     throws Exception
/*      */   {
/* 2708 */     char[] arrayOfChar1 = null;
/* 2709 */     char[] arrayOfChar2 = null;
/*      */ 
/* 2711 */     for (int i = 0; i < 3; i++) {
/* 2712 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("New.prompt."));
/*      */ 
/* 2714 */       Object[] arrayOfObject1 = { paramString };
/* 2715 */       System.err.print(localMessageFormat.format(arrayOfObject1));
/* 2716 */       arrayOfChar1 = Password.readPassword(System.in);
/* 2717 */       this.passwords.add(arrayOfChar1);
/* 2718 */       if ((arrayOfChar1 == null) || (arrayOfChar1.length < 6)) {
/* 2719 */         System.err.println(rb.getString("Password.is.too.short.must.be.at.least.6.characters"));
/*      */       }
/* 2721 */       else if (Arrays.equals(arrayOfChar1, paramArrayOfChar)) {
/* 2722 */         System.err.println(rb.getString("Passwords.must.differ"));
/*      */       } else {
/* 2724 */         localMessageFormat = new MessageFormat(rb.getString("Re.enter.new.prompt."));
/*      */ 
/* 2726 */         Object[] arrayOfObject2 = { paramString };
/* 2727 */         System.err.print(localMessageFormat.format(arrayOfObject2));
/* 2728 */         arrayOfChar2 = Password.readPassword(System.in);
/* 2729 */         this.passwords.add(arrayOfChar2);
/* 2730 */         if (!Arrays.equals(arrayOfChar1, arrayOfChar2)) {
/* 2731 */           System.err.println(rb.getString("They.don.t.match.Try.again"));
/*      */         }
/*      */         else {
/* 2734 */           Arrays.fill(arrayOfChar2, ' ');
/* 2735 */           return arrayOfChar1;
/*      */         }
/*      */       }
/* 2738 */       if (arrayOfChar1 != null) {
/* 2739 */         Arrays.fill(arrayOfChar1, ' ');
/* 2740 */         arrayOfChar1 = null;
/*      */       }
/* 2742 */       if (arrayOfChar2 != null) {
/* 2743 */         Arrays.fill(arrayOfChar2, ' ');
/* 2744 */         arrayOfChar2 = null;
/*      */       }
/*      */     }
/* 2747 */     throw new Exception(rb.getString("Too.many.failures.try.later"));
/*      */   }
/*      */ 
/*      */   private String getAlias(String paramString)
/*      */     throws Exception
/*      */   {
/* 2756 */     if (paramString != null) {
/* 2757 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Enter.prompt.alias.name."));
/*      */ 
/* 2759 */       Object[] arrayOfObject = { paramString };
/* 2760 */       System.err.print(localMessageFormat.format(arrayOfObject));
/*      */     } else {
/* 2762 */       System.err.print(rb.getString("Enter.alias.name."));
/*      */     }
/* 2764 */     return new BufferedReader(new InputStreamReader(System.in)).readLine();
/*      */   }
/*      */ 
/*      */   private String inputStringFromStdin(String paramString)
/*      */     throws Exception
/*      */   {
/* 2774 */     System.err.print(paramString);
/* 2775 */     return new BufferedReader(new InputStreamReader(System.in)).readLine();
/*      */   }
/*      */ 
/*      */   private char[] getKeyPasswd(String paramString1, String paramString2, char[] paramArrayOfChar)
/*      */     throws Exception
/*      */   {
/* 2787 */     int i = 0;
/* 2788 */     char[] arrayOfChar = null;
/*      */     do
/*      */     {
/*      */       MessageFormat localMessageFormat;
/*      */       Object[] arrayOfObject1;
/* 2791 */       if (paramArrayOfChar != null) {
/* 2792 */         localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
/*      */ 
/* 2794 */         arrayOfObject1 = new Object[] { paramString1 };
/* 2795 */         System.err.println(localMessageFormat.format(arrayOfObject1));
/*      */ 
/* 2797 */         localMessageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
/*      */ 
/* 2799 */         Object[] arrayOfObject2 = { paramString2 };
/* 2800 */         System.err.print(localMessageFormat.format(arrayOfObject2));
/*      */       } else {
/* 2802 */         localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
/*      */ 
/* 2804 */         arrayOfObject1 = new Object[] { paramString1 };
/* 2805 */         System.err.print(localMessageFormat.format(arrayOfObject1));
/*      */       }
/* 2807 */       System.err.flush();
/* 2808 */       arrayOfChar = Password.readPassword(System.in);
/* 2809 */       this.passwords.add(arrayOfChar);
/* 2810 */       if (arrayOfChar == null) {
/* 2811 */         arrayOfChar = paramArrayOfChar;
/*      */       }
/* 2813 */       i++;
/* 2814 */     }while ((arrayOfChar == null) && (i < 3));
/*      */ 
/* 2816 */     if (arrayOfChar == null) {
/* 2817 */       throw new Exception(rb.getString("Too.many.failures.try.later"));
/*      */     }
/*      */ 
/* 2820 */     return arrayOfChar;
/*      */   }
/*      */ 
/*      */   private void printX509Cert(X509Certificate paramX509Certificate, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2847 */     MessageFormat localMessageFormat = new MessageFormat(rb.getString(".PATTERN.printX509Cert"));
/*      */ 
/* 2849 */     Object[] arrayOfObject = { paramX509Certificate.getSubjectDN().toString(), paramX509Certificate.getIssuerDN().toString(), paramX509Certificate.getSerialNumber().toString(16), paramX509Certificate.getNotBefore().toString(), paramX509Certificate.getNotAfter().toString(), getCertFingerPrint("MD5", paramX509Certificate), getCertFingerPrint("SHA1", paramX509Certificate), getCertFingerPrint("SHA-256", paramX509Certificate), paramX509Certificate.getSigAlgName(), Integer.valueOf(paramX509Certificate.getVersion()) };
/*      */ 
/* 2860 */     paramPrintStream.println(localMessageFormat.format(arrayOfObject));
/*      */ 
/* 2862 */     if ((paramX509Certificate instanceof X509CertImpl)) {
/* 2863 */       X509CertImpl localX509CertImpl = (X509CertImpl)paramX509Certificate;
/* 2864 */       X509CertInfo localX509CertInfo = (X509CertInfo)localX509CertImpl.get("x509.info");
/*      */ 
/* 2867 */       CertificateExtensions localCertificateExtensions = (CertificateExtensions)localX509CertInfo.get("extensions");
/*      */ 
/* 2869 */       if (localCertificateExtensions != null)
/* 2870 */         printExtensions(rb.getString("Extensions."), localCertificateExtensions, paramPrintStream);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void printExtensions(String paramString, CertificateExtensions paramCertificateExtensions, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2877 */     int i = 0;
/* 2878 */     Iterator localIterator1 = paramCertificateExtensions.getAllExtensions().iterator();
/* 2879 */     Iterator localIterator2 = paramCertificateExtensions.getUnparseableExtensions().values().iterator();
/* 2880 */     while ((localIterator1.hasNext()) || (localIterator2.hasNext())) {
/* 2881 */       Extension localExtension = localIterator1.hasNext() ? (Extension)localIterator1.next() : (Extension)localIterator2.next();
/* 2882 */       if (i == 0) {
/* 2883 */         paramPrintStream.println();
/* 2884 */         paramPrintStream.println(paramString);
/* 2885 */         paramPrintStream.println();
/*      */       }
/* 2887 */       paramPrintStream.print("#" + ++i + ": " + localExtension);
/* 2888 */       if (localExtension.getClass() == Extension.class) {
/* 2889 */         byte[] arrayOfByte = localExtension.getExtensionValue();
/* 2890 */         if (arrayOfByte.length == 0) {
/* 2891 */           paramPrintStream.println(rb.getString(".Empty.value."));
/*      */         } else {
/* 2893 */           new HexDumpEncoder().encodeBuffer(localExtension.getExtensionValue(), paramPrintStream);
/* 2894 */           paramPrintStream.println();
/*      */         }
/*      */       }
/* 2897 */       paramPrintStream.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isSelfSigned(X509Certificate paramX509Certificate)
/*      */   {
/* 2905 */     return signedBy(paramX509Certificate, paramX509Certificate);
/*      */   }
/*      */ 
/*      */   private boolean signedBy(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) {
/* 2909 */     if (!paramX509Certificate2.getSubjectDN().equals(paramX509Certificate1.getIssuerDN()))
/* 2910 */       return false;
/*      */     try
/*      */     {
/* 2913 */       paramX509Certificate1.verify(paramX509Certificate2.getPublicKey());
/* 2914 */       return true; } catch (Exception localException) {
/*      */     }
/* 2916 */     return false;
/*      */   }
/*      */ 
/*      */   private static Certificate getTrustedSigner(Certificate paramCertificate, KeyStore paramKeyStore)
/*      */     throws Exception
/*      */   {
/* 2931 */     if (paramKeyStore.getCertificateAlias(paramCertificate) != null) {
/* 2932 */       return paramCertificate;
/*      */     }
/* 2934 */     Enumeration localEnumeration = paramKeyStore.aliases();
/* 2935 */     while (localEnumeration.hasMoreElements()) {
/* 2936 */       String str = (String)localEnumeration.nextElement();
/* 2937 */       Certificate localCertificate = paramKeyStore.getCertificate(str);
/* 2938 */       if (localCertificate != null)
/*      */         try {
/* 2940 */           paramCertificate.verify(localCertificate.getPublicKey());
/* 2941 */           return localCertificate;
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*      */         }
/*      */     }
/* 2947 */     return null; } 
/* 2955 */   private X500Name getX500Name() throws IOException { BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
/* 2956 */     String str1 = "Unknown";
/* 2957 */     String str2 = "Unknown";
/* 2958 */     String str3 = "Unknown";
/* 2959 */     String str4 = "Unknown";
/* 2960 */     String str5 = "Unknown";
/* 2961 */     String str6 = "Unknown";
/*      */ 
/* 2963 */     String str7 = null;
/*      */ 
/* 2965 */     int i = 20;
/*      */     X500Name localX500Name;
/*      */     do { if (i-- < 0) {
/* 2968 */         throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
/*      */       }
/*      */ 
/* 2971 */       str1 = inputString(localBufferedReader, rb.getString("What.is.your.first.and.last.name."), str1);
/*      */ 
/* 2974 */       str2 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.organizational.unit."), str2);
/*      */ 
/* 2978 */       str3 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.organization."), str3);
/*      */ 
/* 2981 */       str4 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.City.or.Locality."), str4);
/*      */ 
/* 2984 */       str5 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.State.or.Province."), str5);
/*      */ 
/* 2987 */       str6 = inputString(localBufferedReader, rb.getString("What.is.the.two.letter.country.code.for.this.unit."), str6);
/*      */ 
/* 2991 */       localX500Name = new X500Name(str1, str2, str3, str4, str5, str6);
/*      */ 
/* 2993 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Is.name.correct."));
/*      */ 
/* 2995 */       Object[] arrayOfObject = { localX500Name };
/* 2996 */       str7 = inputString(localBufferedReader, localMessageFormat.format(arrayOfObject), rb.getString("no"));
/*      */     }
/* 2998 */     while ((collator.compare(str7, rb.getString("yes")) != 0) && (collator.compare(str7, rb.getString("y")) != 0));
/*      */ 
/* 3001 */     System.err.println();
/* 3002 */     return localX500Name;
/*      */   }
/*      */ 
/*      */   private String inputString(BufferedReader paramBufferedReader, String paramString1, String paramString2)
/*      */     throws IOException
/*      */   {
/* 3009 */     System.err.println(paramString1);
/* 3010 */     MessageFormat localMessageFormat = new MessageFormat(rb.getString(".defaultValue."));
/*      */ 
/* 3012 */     Object[] arrayOfObject = { paramString2 };
/* 3013 */     System.err.print(localMessageFormat.format(arrayOfObject));
/* 3014 */     System.err.flush();
/*      */ 
/* 3016 */     String str = paramBufferedReader.readLine();
/* 3017 */     if ((str == null) || (collator.compare(str, "") == 0)) {
/* 3018 */       str = paramString2;
/*      */     }
/* 3020 */     return str;
/*      */   }
/*      */ 
/*      */   private void dumpCert(Certificate paramCertificate, PrintStream paramPrintStream)
/*      */     throws IOException, CertificateException
/*      */   {
/* 3030 */     if (this.rfc) {
/* 3031 */       BASE64Encoder localBASE64Encoder = new BASE64Encoder();
/* 3032 */       paramPrintStream.println("-----BEGIN CERTIFICATE-----");
/* 3033 */       localBASE64Encoder.encodeBuffer(paramCertificate.getEncoded(), paramPrintStream);
/* 3034 */       paramPrintStream.println("-----END CERTIFICATE-----");
/*      */     } else {
/* 3036 */       paramPrintStream.write(paramCertificate.getEncoded());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void byte2hex(byte paramByte, StringBuffer paramStringBuffer)
/*      */   {
/* 3044 */     char[] arrayOfChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*      */ 
/* 3046 */     int i = (paramByte & 0xF0) >> 4;
/* 3047 */     int j = paramByte & 0xF;
/* 3048 */     paramStringBuffer.append(arrayOfChar[i]);
/* 3049 */     paramStringBuffer.append(arrayOfChar[j]);
/*      */   }
/*      */ 
/*      */   private String toHexString(byte[] paramArrayOfByte)
/*      */   {
/* 3056 */     StringBuffer localStringBuffer = new StringBuffer();
/* 3057 */     int i = paramArrayOfByte.length;
/* 3058 */     for (int j = 0; j < i; j++) {
/* 3059 */       byte2hex(paramArrayOfByte[j], localStringBuffer);
/* 3060 */       if (j < i - 1) {
/* 3061 */         localStringBuffer.append(":");
/*      */       }
/*      */     }
/* 3064 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private Pair<Key, char[]> recoverKey(String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
/*      */     throws Exception
/*      */   {
/* 3078 */     Key localKey = null;
/*      */     MessageFormat localMessageFormat;
/*      */     Object[] arrayOfObject;
/* 3080 */     if (!this.keyStore.containsAlias(paramString)) {
/* 3081 */       localMessageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/*      */ 
/* 3083 */       arrayOfObject = new Object[] { paramString };
/* 3084 */       throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */     }
/* 3086 */     if ((!this.keyStore.entryInstanceOf(paramString, KeyStore.PrivateKeyEntry.class)) && (!this.keyStore.entryInstanceOf(paramString, KeyStore.SecretKeyEntry.class)))
/*      */     {
/* 3088 */       localMessageFormat = new MessageFormat(rb.getString("Alias.alias.has.no.key"));
/*      */ 
/* 3090 */       arrayOfObject = new Object[] { paramString };
/* 3091 */       throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */     }
/*      */ 
/* 3094 */     if (paramArrayOfChar2 == null)
/*      */       try
/*      */       {
/* 3097 */         localKey = this.keyStore.getKey(paramString, paramArrayOfChar1);
/*      */ 
/* 3099 */         paramArrayOfChar2 = paramArrayOfChar1;
/* 3100 */         this.passwords.add(paramArrayOfChar2);
/*      */       }
/*      */       catch (UnrecoverableKeyException localUnrecoverableKeyException) {
/* 3103 */         if (!this.token) {
/* 3104 */           paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
/* 3105 */           localKey = this.keyStore.getKey(paramString, paramArrayOfChar2);
/*      */         } else {
/* 3107 */           throw localUnrecoverableKeyException;
/*      */         }
/*      */       }
/*      */     else {
/* 3111 */       localKey = this.keyStore.getKey(paramString, paramArrayOfChar2);
/*      */     }
/*      */ 
/* 3114 */     return Pair.of(localKey, paramArrayOfChar2);
/*      */   }
/*      */ 
/*      */   private Pair<KeyStore.Entry, char[]> recoverEntry(KeyStore paramKeyStore, String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
/*      */     throws Exception
/*      */   {
/*      */     Object localObject2;
/* 3129 */     if (!paramKeyStore.containsAlias(paramString)) {
/* 3130 */       localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/*      */ 
/* 3132 */       localObject2 = new Object[] { paramString };
/* 3133 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 3136 */     Object localObject1 = null;
/*      */     try
/*      */     {
/* 3143 */       localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
/* 3144 */       paramArrayOfChar2 = null;
/*      */     }
/*      */     catch (UnrecoverableEntryException localUnrecoverableEntryException1) {
/* 3147 */       if (("PKCS11".equalsIgnoreCase(paramKeyStore.getType())) || (KeyStoreUtil.isWindowsKeyStore(paramKeyStore.getType())))
/*      */       {
/* 3150 */         throw localUnrecoverableEntryException1;
/*      */       }
/*      */ 
/* 3155 */       if (paramArrayOfChar2 != null)
/*      */       {
/* 3159 */         localObject1 = new KeyStore.PasswordProtection(paramArrayOfChar2);
/* 3160 */         localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/* 3167 */           localObject1 = new KeyStore.PasswordProtection(paramArrayOfChar1);
/* 3168 */           localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
/* 3169 */           paramArrayOfChar2 = paramArrayOfChar1;
/*      */         } catch (UnrecoverableEntryException localUnrecoverableEntryException2) {
/* 3171 */           if ("PKCS12".equalsIgnoreCase(paramKeyStore.getType()))
/*      */           {
/* 3176 */             throw localUnrecoverableEntryException2;
/*      */           }
/*      */ 
/* 3181 */           paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
/* 3182 */           localObject1 = new KeyStore.PasswordProtection(paramArrayOfChar2);
/* 3183 */           localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3189 */     return Pair.of(localObject2, paramArrayOfChar2);
/*      */   }
/*      */ 
/*      */   private String getCertFingerPrint(String paramString, Certificate paramCertificate)
/*      */     throws Exception
/*      */   {
/* 3197 */     byte[] arrayOfByte1 = paramCertificate.getEncoded();
/* 3198 */     MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
/* 3199 */     byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
/* 3200 */     return toHexString(arrayOfByte2);
/*      */   }
/*      */ 
/*      */   private void printWarning()
/*      */   {
/* 3207 */     System.err.println();
/* 3208 */     System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
/*      */ 
/* 3210 */     System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.your.keystore."));
/*      */ 
/* 3212 */     System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
/*      */ 
/* 3214 */     System.err.println();
/*      */   }
/*      */ 
/*      */   private Certificate[] validateReply(String paramString, Certificate paramCertificate, Certificate[] paramArrayOfCertificate)
/*      */     throws Exception
/*      */   {
/* 3235 */     PublicKey localPublicKey = paramCertificate.getPublicKey();
/* 3236 */     for (int i = 0; (i < paramArrayOfCertificate.length) && 
/* 3237 */       (!localPublicKey.equals(paramArrayOfCertificate[i].getPublicKey())); i++);
/* 3241 */     if (i == paramArrayOfCertificate.length) {
/* 3242 */       localObject1 = new MessageFormat(rb.getString("Certificate.reply.does.not.contain.public.key.for.alias."));
/*      */ 
/* 3244 */       localObject2 = new Object[] { paramString };
/* 3245 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */ 
/* 3248 */     Object localObject1 = paramArrayOfCertificate[0];
/* 3249 */     paramArrayOfCertificate[0] = paramArrayOfCertificate[i];
/* 3250 */     paramArrayOfCertificate[i] = localObject1;
/*      */ 
/* 3252 */     Object localObject2 = (X509Certificate)paramArrayOfCertificate[0];
/*      */ 
/* 3254 */     for (i = 1; i < paramArrayOfCertificate.length - 1; i++)
/*      */     {
/* 3257 */       for (int j = i; j < paramArrayOfCertificate.length; j++) {
/* 3258 */         if (signedBy((X509Certificate)localObject2, (X509Certificate)paramArrayOfCertificate[j])) {
/* 3259 */           localObject1 = paramArrayOfCertificate[i];
/* 3260 */           paramArrayOfCertificate[i] = paramArrayOfCertificate[j];
/* 3261 */           paramArrayOfCertificate[j] = localObject1;
/* 3262 */           localObject2 = (X509Certificate)paramArrayOfCertificate[i];
/* 3263 */           break;
/*      */         }
/*      */       }
/* 3266 */       if (j == paramArrayOfCertificate.length) {
/* 3267 */         throw new Exception(rb.getString("Incomplete.certificate.chain.in.reply"));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3272 */     if (this.noprompt) {
/* 3273 */       return paramArrayOfCertificate;
/*      */     }
/*      */ 
/* 3277 */     Certificate localCertificate1 = paramArrayOfCertificate[(paramArrayOfCertificate.length - 1)];
/* 3278 */     Certificate localCertificate2 = getTrustedSigner(localCertificate1, this.keyStore);
/* 3279 */     if ((localCertificate2 == null) && (this.trustcacerts) && (this.caks != null))
/* 3280 */       localCertificate2 = getTrustedSigner(localCertificate1, this.caks);
/*      */     Object localObject3;
/* 3282 */     if (localCertificate2 == null) {
/* 3283 */       System.err.println();
/* 3284 */       System.err.println(rb.getString("Top.level.certificate.in.reply."));
/*      */ 
/* 3286 */       printX509Cert((X509Certificate)localCertificate1, System.out);
/* 3287 */       System.err.println();
/* 3288 */       System.err.print(rb.getString(".is.not.trusted."));
/* 3289 */       localObject3 = getYesNoReply(rb.getString("Install.reply.anyway.no."));
/*      */ 
/* 3291 */       if ("NO".equals(localObject3)) {
/* 3292 */         return null;
/*      */       }
/*      */     }
/* 3295 */     else if (localCertificate2 != localCertificate1)
/*      */     {
/* 3297 */       localObject3 = new Certificate[paramArrayOfCertificate.length + 1];
/*      */ 
/* 3299 */       System.arraycopy(paramArrayOfCertificate, 0, localObject3, 0, paramArrayOfCertificate.length);
/*      */ 
/* 3301 */       localObject3[(localObject3.length - 1)] = localCertificate2;
/* 3302 */       paramArrayOfCertificate = (Certificate[])localObject3;
/*      */     }
/*      */ 
/* 3306 */     return paramArrayOfCertificate;
/*      */   }
/*      */ 
/*      */   private Certificate[] establishCertChain(Certificate paramCertificate1, Certificate paramCertificate2)
/*      */     throws Exception
/*      */   {
/* 3321 */     if (paramCertificate1 != null)
/*      */     {
/* 3324 */       localObject1 = paramCertificate1.getPublicKey();
/* 3325 */       localObject2 = paramCertificate2.getPublicKey();
/* 3326 */       if (!localObject1.equals(localObject2)) {
/* 3327 */         throw new Exception(rb.getString("Public.keys.in.reply.and.keystore.don.t.match"));
/*      */       }
/*      */ 
/* 3333 */       if (paramCertificate2.equals(paramCertificate1)) {
/* 3334 */         throw new Exception(rb.getString("Certificate.reply.and.certificate.in.keystore.are.identical"));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3343 */     Object localObject1 = null;
/* 3344 */     if (this.keyStore.size() > 0) {
/* 3345 */       localObject1 = new Hashtable(11);
/* 3346 */       keystorecerts2Hashtable(this.keyStore, (Hashtable)localObject1);
/*      */     }
/* 3348 */     if ((this.trustcacerts) && 
/* 3349 */       (this.caks != null) && (this.caks.size() > 0)) {
/* 3350 */       if (localObject1 == null) {
/* 3351 */         localObject1 = new Hashtable(11);
/*      */       }
/* 3353 */       keystorecerts2Hashtable(this.caks, (Hashtable)localObject1);
/*      */     }
/*      */ 
/* 3358 */     Object localObject2 = new Vector(2);
/* 3359 */     if (buildChain((X509Certificate)paramCertificate2, (Vector)localObject2, (Hashtable)localObject1)) {
/* 3360 */       Certificate[] arrayOfCertificate = new Certificate[((Vector)localObject2).size()];
/*      */ 
/* 3364 */       int i = 0;
/* 3365 */       for (int j = ((Vector)localObject2).size() - 1; j >= 0; j--) {
/* 3366 */         arrayOfCertificate[i] = ((Certificate)((Vector)localObject2).elementAt(j));
/* 3367 */         i++;
/*      */       }
/* 3369 */       return arrayOfCertificate;
/*      */     }
/* 3371 */     throw new Exception(rb.getString("Failed.to.establish.chain.from.reply"));
/*      */   }
/*      */ 
/*      */   private boolean buildChain(X509Certificate paramX509Certificate, Vector<Certificate> paramVector, Hashtable<Principal, Vector<Certificate>> paramHashtable)
/*      */   {
/* 3388 */     Principal localPrincipal = paramX509Certificate.getIssuerDN();
/* 3389 */     if (isSelfSigned(paramX509Certificate))
/*      */     {
/* 3392 */       paramVector.addElement(paramX509Certificate);
/* 3393 */       return true;
/*      */     }
/*      */ 
/* 3397 */     Vector localVector = (Vector)paramHashtable.get(localPrincipal);
/* 3398 */     if (localVector == null) {
/* 3399 */       return false;
/*      */     }
/*      */ 
/* 3405 */     Enumeration localEnumeration = localVector.elements();
/* 3406 */     while (localEnumeration.hasMoreElements()) {
/* 3407 */       X509Certificate localX509Certificate = (X509Certificate)localEnumeration.nextElement();
/*      */ 
/* 3409 */       PublicKey localPublicKey = localX509Certificate.getPublicKey();
/*      */       try {
/* 3411 */         paramX509Certificate.verify(localPublicKey); } catch (Exception localException) {
/*      */       }
/* 3413 */       continue;
/*      */ 
/* 3415 */       if (buildChain(localX509Certificate, paramVector, paramHashtable)) {
/* 3416 */         paramVector.addElement(paramX509Certificate);
/* 3417 */         return true;
/*      */       }
/*      */     }
/* 3420 */     return false;
/*      */   }
/*      */ 
/*      */   private String getYesNoReply(String paramString)
/*      */     throws IOException
/*      */   {
/* 3431 */     String str = null;
/* 3432 */     int i = 20;
/*      */     do {
/* 3434 */       if (i-- < 0) {
/* 3435 */         throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
/*      */       }
/*      */ 
/* 3438 */       System.err.print(paramString);
/* 3439 */       System.err.flush();
/* 3440 */       str = new BufferedReader(new InputStreamReader(System.in)).readLine();
/*      */ 
/* 3442 */       if ((collator.compare(str, "") == 0) || (collator.compare(str, rb.getString("n")) == 0) || (collator.compare(str, rb.getString("no")) == 0))
/*      */       {
/* 3445 */         str = "NO";
/* 3446 */       } else if ((collator.compare(str, rb.getString("y")) == 0) || (collator.compare(str, rb.getString("yes")) == 0))
/*      */       {
/* 3448 */         str = "YES";
/*      */       } else {
/* 3450 */         System.err.println(rb.getString("Wrong.answer.try.again"));
/* 3451 */         str = null;
/*      */       }
/*      */     }
/* 3453 */     while (str == null);
/* 3454 */     return str;
/*      */   }
/*      */ 
/*      */   public static KeyStore getCacertsKeyStore()
/*      */     throws Exception
/*      */   {
/* 3463 */     String str = File.separator;
/* 3464 */     File localFile = new File(System.getProperty("java.home") + str + "lib" + str + "security" + str + "cacerts");
/*      */ 
/* 3467 */     if (!localFile.exists()) {
/* 3468 */       return null;
/*      */     }
/* 3470 */     FileInputStream localFileInputStream = null;
/* 3471 */     KeyStore localKeyStore = null;
/*      */     try {
/* 3473 */       localFileInputStream = new FileInputStream(localFile);
/* 3474 */       localKeyStore = KeyStore.getInstance("jks");
/* 3475 */       localKeyStore.load(localFileInputStream, null);
/*      */     } finally {
/* 3477 */       if (localFileInputStream != null) {
/* 3478 */         localFileInputStream.close();
/*      */       }
/*      */     }
/* 3481 */     return localKeyStore;
/*      */   }
/*      */ 
/*      */   private void keystorecerts2Hashtable(KeyStore paramKeyStore, Hashtable<Principal, Vector<Certificate>> paramHashtable)
/*      */     throws Exception
/*      */   {
/* 3493 */     Enumeration localEnumeration = paramKeyStore.aliases();
/* 3494 */     while (localEnumeration.hasMoreElements()) {
/* 3495 */       String str = (String)localEnumeration.nextElement();
/* 3496 */       Certificate localCertificate = paramKeyStore.getCertificate(str);
/* 3497 */       if (localCertificate != null) {
/* 3498 */         Principal localPrincipal = ((X509Certificate)localCertificate).getSubjectDN();
/* 3499 */         Vector localVector = (Vector)paramHashtable.get(localPrincipal);
/* 3500 */         if (localVector == null) {
/* 3501 */           localVector = new Vector();
/* 3502 */           localVector.addElement(localCertificate);
/*      */         }
/* 3504 */         else if (!localVector.contains(localCertificate)) {
/* 3505 */           localVector.addElement(localCertificate);
/*      */         }
/*      */ 
/* 3508 */         paramHashtable.put(localPrincipal, localVector);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Date getStartDate(String paramString)
/*      */     throws IOException
/*      */   {
/* 3518 */     GregorianCalendar localGregorianCalendar = new GregorianCalendar();
/* 3519 */     if (paramString != null) {
/* 3520 */       IOException localIOException = new IOException(rb.getString("Illegal.startdate.value"));
/*      */ 
/* 3522 */       int i = paramString.length();
/* 3523 */       if (i == 0) {
/* 3524 */         throw localIOException;
/*      */       }
/* 3526 */       if ((paramString.charAt(0) == '-') || (paramString.charAt(0) == '+'))
/*      */       {
/* 3528 */         int j = 0;
/* 3529 */         while (j < i) {
/* 3530 */           int k = 0;
/* 3531 */           switch (paramString.charAt(j)) { case '+':
/* 3532 */             k = 1; break;
/*      */           case '-':
/* 3533 */             k = -1; break;
/*      */           default:
/* 3534 */             throw localIOException;
/*      */           }
/* 3536 */           for (int m = j + 1; 
/* 3537 */             m < i; m++) {
/* 3538 */             n = paramString.charAt(m);
/* 3539 */             if ((n < 48) || (n > 57)) break;
/*      */           }
/* 3541 */           if (m == j + 1) throw localIOException;
/* 3542 */           int n = Integer.parseInt(paramString.substring(j + 1, m));
/* 3543 */           if (m >= i) throw localIOException;
/* 3544 */           int i1 = 0;
/* 3545 */           switch (paramString.charAt(m)) { case 'y':
/* 3546 */             i1 = 1; break;
/*      */           case 'm':
/* 3547 */             i1 = 2; break;
/*      */           case 'd':
/* 3548 */             i1 = 5; break;
/*      */           case 'H':
/* 3549 */             i1 = 10; break;
/*      */           case 'M':
/* 3550 */             i1 = 12; break;
/*      */           case 'S':
/* 3551 */             i1 = 13; break;
/*      */           default:
/* 3552 */             throw localIOException;
/*      */           }
/* 3554 */           localGregorianCalendar.add(i1, k * n);
/* 3555 */           j = m + 1;
/*      */         }
/*      */       }
/*      */       else {
/* 3559 */         String str1 = null; String str2 = null;
/* 3560 */         if (i == 19) {
/* 3561 */           str1 = paramString.substring(0, 10);
/* 3562 */           str2 = paramString.substring(11);
/* 3563 */           if (paramString.charAt(10) != ' ')
/* 3564 */             throw localIOException;
/* 3565 */         } else if (i == 10) {
/* 3566 */           str1 = paramString;
/* 3567 */         } else if (i == 8) {
/* 3568 */           str2 = paramString;
/*      */         } else {
/* 3570 */           throw localIOException;
/*      */         }
/* 3572 */         if (str1 != null) {
/* 3573 */           if (str1.matches("\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d")) {
/* 3574 */             localGregorianCalendar.set(Integer.valueOf(str1.substring(0, 4)).intValue(), Integer.valueOf(str1.substring(5, 7)).intValue() - 1, Integer.valueOf(str1.substring(8, 10)).intValue());
/*      */           }
/*      */           else
/*      */           {
/* 3578 */             throw localIOException;
/*      */           }
/*      */         }
/* 3581 */         if (str2 != null) {
/* 3582 */           if (str2.matches("\\d\\d:\\d\\d:\\d\\d")) {
/* 3583 */             localGregorianCalendar.set(11, Integer.valueOf(str2.substring(0, 2)).intValue());
/* 3584 */             localGregorianCalendar.set(12, Integer.valueOf(str2.substring(0, 2)).intValue());
/* 3585 */             localGregorianCalendar.set(13, Integer.valueOf(str2.substring(0, 2)).intValue());
/* 3586 */             localGregorianCalendar.set(14, 0);
/*      */           } else {
/* 3588 */             throw localIOException;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 3593 */     return localGregorianCalendar.getTime();
/*      */   }
/*      */ 
/*      */   private static int oneOf(String paramString, String[] paramArrayOfString)
/*      */     throws Exception
/*      */   {
/* 3606 */     int[] arrayOfInt = new int[paramArrayOfString.length];
/* 3607 */     int i = 0;
/* 3608 */     int j = 2147483647;
/* 3609 */     for (int k = 0; k < paramArrayOfString.length; k++) {
/* 3610 */       localObject1 = paramArrayOfString[k];
/* 3611 */       if (localObject1 == null) {
/* 3612 */         j = k;
/*      */       }
/* 3615 */       else if (((String)localObject1).toLowerCase(Locale.ENGLISH).startsWith(paramString.toLowerCase(Locale.ENGLISH)))
/*      */       {
/* 3617 */         arrayOfInt[(i++)] = k;
/*      */       } else {
/* 3619 */         localObject2 = new StringBuffer();
/* 3620 */         m = 1;
/* 3621 */         for (char c : ((String)localObject1).toCharArray()) {
/* 3622 */           if (m != 0) {
/* 3623 */             ((StringBuffer)localObject2).append(c);
/* 3624 */             m = 0;
/*      */           }
/* 3626 */           else if (!Character.isLowerCase(c)) {
/* 3627 */             ((StringBuffer)localObject2).append(c);
/*      */           }
/*      */         }
/*      */ 
/* 3631 */         if (((StringBuffer)localObject2).toString().equalsIgnoreCase(paramString)) {
/* 3632 */           arrayOfInt[(i++)] = k;
/*      */         }
/*      */       }
/*      */     }
/* 3636 */     if (i == 0)
/* 3637 */       return -1;
/* 3638 */     if (i == 1) {
/* 3639 */       return arrayOfInt[0];
/*      */     }
/*      */ 
/* 3642 */     if (arrayOfInt[1] > j) {
/* 3643 */       return arrayOfInt[0];
/*      */     }
/* 3645 */     StringBuffer localStringBuffer = new StringBuffer();
/* 3646 */     Object localObject1 = new MessageFormat(rb.getString("command.{0}.is.ambiguous."));
/*      */ 
/* 3648 */     Object localObject2 = { paramString };
/* 3649 */     localStringBuffer.append(((MessageFormat)localObject1).format(localObject2));
/* 3650 */     localStringBuffer.append("\n    ");
/* 3651 */     for (int m = 0; (m < i) && (arrayOfInt[m] < j); m++) {
/* 3652 */       localStringBuffer.append(' ');
/* 3653 */       localStringBuffer.append(paramArrayOfString[arrayOfInt[m]]);
/*      */     }
/* 3655 */     throw new Exception(localStringBuffer.toString());
/*      */   }
/*      */ 
/*      */   private GeneralName createGeneralName(String paramString1, String paramString2)
/*      */     throws Exception
/*      */   {
/* 3668 */     int i = oneOf(paramString1, new String[] { "EMAIL", "URI", "DNS", "IP", "OID" });
/* 3669 */     if (i < 0)
/* 3670 */       throw new Exception(rb.getString("Unrecognized.GeneralName.type.") + paramString1);
/*      */     Object localObject;
/* 3673 */     switch (i) { case 0:
/* 3674 */       localObject = new RFC822Name(paramString2); break;
/*      */     case 1:
/* 3675 */       localObject = new URIName(paramString2); break;
/*      */     case 2:
/* 3676 */       localObject = new DNSName(paramString2); break;
/*      */     case 3:
/* 3677 */       localObject = new IPAddressName(paramString2); break;
/*      */     default:
/* 3678 */       localObject = new OIDName(paramString2);
/*      */     }
/* 3680 */     return new GeneralName((GeneralNameInterface)localObject);
/*      */   }
/*      */ 
/*      */   private ObjectIdentifier findOidForExtName(String paramString)
/*      */     throws Exception
/*      */   {
/* 3697 */     switch (oneOf(paramString, extSupported)) { case 0:
/* 3698 */       return PKIXExtensions.BasicConstraints_Id;
/*      */     case 1:
/* 3699 */       return PKIXExtensions.KeyUsage_Id;
/*      */     case 2:
/* 3700 */       return PKIXExtensions.ExtendedKeyUsage_Id;
/*      */     case 3:
/* 3701 */       return PKIXExtensions.SubjectAlternativeName_Id;
/*      */     case 4:
/* 3702 */       return PKIXExtensions.IssuerAlternativeName_Id;
/*      */     case 5:
/* 3703 */       return PKIXExtensions.SubjectInfoAccess_Id;
/*      */     case 6:
/* 3704 */       return PKIXExtensions.AuthInfoAccess_Id;
/*      */     case 8:
/* 3705 */       return PKIXExtensions.CRLDistributionPoints_Id;
/* 3706 */     case 7: } return new ObjectIdentifier(paramString);
/*      */   }
/*      */ 
/*      */   private CertificateExtensions createV3Extensions(CertificateExtensions paramCertificateExtensions1, CertificateExtensions paramCertificateExtensions2, List<String> paramList, PublicKey paramPublicKey1, PublicKey paramPublicKey2)
/*      */     throws Exception
/*      */   {
/* 3729 */     if ((paramCertificateExtensions2 != null) && (paramCertificateExtensions1 != null))
/*      */     {
/* 3731 */       throw new Exception("One of request and original should be null.");
/*      */     }
/* 3733 */     if (paramCertificateExtensions2 == null) paramCertificateExtensions2 = new CertificateExtensions();
/*      */ 
/*      */     try
/*      */     {
/* 3737 */       if (paramCertificateExtensions1 != null)
/* 3738 */         for (localIterator = paramList.iterator(); localIterator.hasNext(); ) { str1 = (String)localIterator.next();
/* 3739 */           if (str1.toLowerCase(Locale.ENGLISH).startsWith("honored=")) {
/* 3740 */             localObject1 = Arrays.asList(str1.toLowerCase(Locale.ENGLISH).substring(8).split(","));
/*      */ 
/* 3743 */             if (((List)localObject1).contains("all")) {
/* 3744 */               paramCertificateExtensions2 = paramCertificateExtensions1;
/*      */             }
/*      */ 
/* 3747 */             for (localObject2 = ((List)localObject1).iterator(); ((Iterator)localObject2).hasNext(); ) { String str2 = (String)((Iterator)localObject2).next();
/* 3748 */               if (!str2.equals("all"))
/*      */               {
/* 3751 */                 i = 1;
/*      */ 
/* 3753 */                 j = -1;
/* 3754 */                 String str3 = null;
/* 3755 */                 if (str2.startsWith("-")) {
/* 3756 */                   i = 0;
/* 3757 */                   str3 = str2.substring(1);
/*      */                 } else {
/* 3759 */                   int m = str2.indexOf(':');
/* 3760 */                   if (m >= 0) {
/* 3761 */                     str3 = str2.substring(0, m);
/* 3762 */                     j = oneOf(str2.substring(m + 1), new String[] { "critical", "non-critical" });
/*      */ 
/* 3764 */                     if (j == -1) {
/* 3765 */                       throw new Exception(rb.getString("Illegal.value.") + str2);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */ 
/* 3770 */                 String str4 = paramCertificateExtensions1.getNameByOid(findOidForExtName(str3));
/* 3771 */                 if (i != 0) {
/* 3772 */                   Extension localExtension = (Extension)paramCertificateExtensions1.get(str4);
/* 3773 */                   if (((!localExtension.isCritical()) && (j == 0)) || ((localExtension.isCritical()) && (j == 1)))
/*      */                   {
/* 3775 */                     localExtension = Extension.newExtension(localExtension.getExtensionId(), !localExtension.isCritical(), localExtension.getExtensionValue());
/*      */ 
/* 3779 */                     paramCertificateExtensions2.set(str4, localExtension);
/*      */                   }
/*      */                 } else {
/* 3782 */                   paramCertificateExtensions2.delete(str4);
/*      */                 }
/*      */               } }
/* 3785 */             break;
/*      */           }
/*      */         }
/* 3789 */       String str1;
/*      */       Object localObject1;
/*      */       Object localObject2;
/*      */       int i;
/*      */       int j;
/* 3789 */       for (Iterator localIterator = paramList.iterator(); localIterator.hasNext(); ) { str1 = (String)localIterator.next();
/*      */ 
/* 3791 */         boolean bool1 = false;
/*      */ 
/* 3793 */         i = str1.indexOf('=');
/* 3794 */         if (i >= 0) {
/* 3795 */           localObject1 = str1.substring(0, i);
/* 3796 */           localObject2 = str1.substring(i + 1);
/*      */         } else {
/* 3798 */           localObject1 = str1;
/* 3799 */           localObject2 = null;
/*      */         }
/*      */ 
/* 3802 */         j = ((String)localObject1).indexOf(':');
/* 3803 */         if (j >= 0) {
/* 3804 */           if (oneOf(((String)localObject1).substring(j + 1), new String[] { "critical" }) == 0) {
/* 3805 */             bool1 = true;
/*      */           }
/* 3807 */           localObject1 = ((String)localObject1).substring(0, j);
/*      */         }
/*      */ 
/* 3810 */         if (!((String)localObject1).equalsIgnoreCase("honored"))
/*      */         {
/* 3813 */           int k = oneOf((String)localObject1, extSupported);
/*      */           Object localObject4;
/*      */           int i1;
/*      */           int i3;
/*      */           String str5;
/*      */           Object localObject3;
/*      */           int i5;
/*      */           String str6;
/*      */           String str9;
/* 3814 */           switch (k) {
/*      */           case 0:
/* 3816 */             int n = -1;
/* 3817 */             boolean bool2 = false;
/* 3818 */             if (localObject2 == null) {
/* 3819 */               bool2 = true;
/*      */             } else {
/*      */               try {
/* 3822 */                 n = Integer.parseInt((String)localObject2);
/* 3823 */                 bool2 = true;
/*      */               }
/*      */               catch (NumberFormatException localNumberFormatException) {
/* 3826 */                 localObject4 = ((String)localObject2).split(","); i1 = localObject4.length; i3 = 0; } for (; i3 < i1; i3++) { str5 = localObject4[i3];
/* 3827 */                 String[] arrayOfString = str5.split(":");
/* 3828 */                 if (arrayOfString.length != 2) {
/* 3829 */                   throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */                 }
/*      */ 
/* 3832 */                 if (arrayOfString[0].equalsIgnoreCase("ca"))
/* 3833 */                   bool2 = Boolean.parseBoolean(arrayOfString[1]);
/* 3834 */                 else if (arrayOfString[0].equalsIgnoreCase("pathlen"))
/* 3835 */                   n = Integer.parseInt(arrayOfString[1]);
/*      */                 else {
/* 3837 */                   throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 3844 */             paramCertificateExtensions2.set("BasicConstraints", new BasicConstraintsExtension(Boolean.valueOf(bool1), bool2, n));
/*      */ 
/* 3847 */             break;
/*      */           case 1:
/* 3849 */             if (localObject2 != null) {
/* 3850 */               localObject3 = new boolean[9];
/* 3851 */               for (str5 : ((String)localObject2).split(",")) {
/* 3852 */                 i5 = oneOf(str5, new String[] { "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly", "contentCommitment" });
/*      */ 
/* 3864 */                 if (i5 < 0) {
/* 3865 */                   throw new Exception(rb.getString("Unknown.keyUsage.type.") + str5);
/*      */                 }
/* 3867 */                 if (i5 == 9) i5 = 1;
/* 3868 */                 localObject3[i5] = 1;
/*      */               }
/* 3870 */               localObject4 = new KeyUsageExtension((boolean[])localObject3);
/*      */ 
/* 3873 */               paramCertificateExtensions2.set("KeyUsage", Extension.newExtension(((KeyUsageExtension)localObject4).getExtensionId(), bool1, ((KeyUsageExtension)localObject4).getExtensionValue()));
/*      */             }
/*      */             else
/*      */             {
/* 3878 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */ 
/*      */             break;
/*      */           case 2:
/* 3883 */             if (localObject2 != null) {
/* 3884 */               localObject3 = new Vector();
/* 3885 */               for (str5 : ((String)localObject2).split(",")) {
/* 3886 */                 i5 = oneOf(str5, new String[] { "anyExtendedKeyUsage", "serverAuth", "clientAuth", "codeSigning", "emailProtection", "", "", "", "timeStamping", "OCSPSigning" });
/*      */ 
/* 3898 */                 if (i5 < 0) {
/*      */                   try {
/* 3900 */                     ((Vector)localObject3).add(new ObjectIdentifier(str5));
/*      */                   } catch (Exception localException1) {
/* 3902 */                     throw new Exception(rb.getString("Unknown.extendedkeyUsage.type.") + str5);
/*      */                   }
/*      */                 }
/* 3905 */                 else if (i5 == 0)
/* 3906 */                   ((Vector)localObject3).add(new ObjectIdentifier("2.5.29.37.0"));
/*      */                 else {
/* 3908 */                   ((Vector)localObject3).add(new ObjectIdentifier("1.3.6.1.5.5.7.3." + i5));
/*      */                 }
/*      */               }
/* 3911 */               paramCertificateExtensions2.set("ExtendedKeyUsage", new ExtendedKeyUsageExtension(Boolean.valueOf(bool1), (Vector)localObject3));
/*      */             }
/*      */             else {
/* 3914 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */ 
/*      */             break;
/*      */           case 3:
/*      */           case 4:
/* 3920 */             if (localObject2 != null) {
/* 3921 */               localObject3 = ((String)localObject2).split(",");
/* 3922 */               localObject4 = new GeneralNames();
/* 3923 */               for (str6 : localObject3) {
/* 3924 */                 j = str6.indexOf(':');
/* 3925 */                 if (j < 0) {
/* 3926 */                   throw new Exception("Illegal item " + str6 + " in " + str1);
/*      */                 }
/* 3928 */                 String str7 = str6.substring(0, j);
/* 3929 */                 str9 = str6.substring(j + 1);
/* 3930 */                 ((GeneralNames)localObject4).add(createGeneralName(str7, str9));
/*      */               }
/* 3932 */               if (k == 3) {
/* 3933 */                 paramCertificateExtensions2.set("SubjectAlternativeName", new SubjectAlternativeNameExtension(Boolean.valueOf(bool1), (GeneralNames)localObject4));
/*      */               }
/*      */               else
/*      */               {
/* 3937 */                 paramCertificateExtensions2.set("IssuerAlternativeName", new IssuerAlternativeNameExtension(Boolean.valueOf(bool1), (GeneralNames)localObject4));
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 3942 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */ 
/*      */             break;
/*      */           case 5:
/*      */           case 6:
/* 3948 */             if (bool1) {
/* 3949 */               throw new Exception(rb.getString("This.extension.cannot.be.marked.as.critical.") + str1);
/*      */             }
/*      */ 
/* 3952 */             if (localObject2 != null) {
/* 3953 */               localObject3 = new ArrayList();
/*      */ 
/* 3955 */               localObject4 = ((String)localObject2).split(",");
/* 3956 */               for (str6 : localObject4) {
/* 3957 */                 j = str6.indexOf(':');
/* 3958 */                 int i7 = str6.indexOf(':', j + 1);
/* 3959 */                 if ((j < 0) || (i7 < 0)) {
/* 3960 */                   throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */                 }
/*      */ 
/* 3963 */                 str9 = str6.substring(0, j);
/* 3964 */                 String str10 = str6.substring(j + 1, i7);
/* 3965 */                 String str11 = str6.substring(i7 + 1);
/* 3966 */                 int i10 = oneOf(str9, new String[] { "", "ocsp", "caIssuers", "timeStamping", "", "caRepository" });
/*      */                 ObjectIdentifier localObjectIdentifier;
/* 3975 */                 if (i10 < 0) {
/*      */                   try {
/* 3977 */                     localObjectIdentifier = new ObjectIdentifier(str9);
/*      */                   } catch (Exception localException2) {
/* 3979 */                     throw new Exception(rb.getString("Unknown.AccessDescription.type.") + str9);
/*      */                   }
/*      */                 }
/*      */                 else {
/* 3983 */                   localObjectIdentifier = new ObjectIdentifier("1.3.6.1.5.5.7.48." + i10);
/*      */                 }
/* 3985 */                 ((List)localObject3).add(new AccessDescription(localObjectIdentifier, createGeneralName(str10, str11)));
/*      */               }
/*      */ 
/* 3988 */               if (k == 5) {
/* 3989 */                 paramCertificateExtensions2.set("SubjectInfoAccess", new SubjectInfoAccessExtension((List)localObject3));
/*      */               }
/*      */               else
/* 3992 */                 paramCertificateExtensions2.set("AuthorityInfoAccess", new AuthorityInfoAccessExtension((List)localObject3));
/*      */             }
/*      */             else
/*      */             {
/* 3996 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */ 
/*      */             break;
/*      */           case 8:
/* 4001 */             if (localObject2 != null) {
/* 4002 */               localObject3 = ((String)localObject2).split(",");
/* 4003 */               localObject4 = new GeneralNames();
/* 4004 */               for (str6 : localObject3) {
/* 4005 */                 j = str6.indexOf(':');
/* 4006 */                 if (j < 0) {
/* 4007 */                   throw new Exception("Illegal item " + str6 + " in " + str1);
/*      */                 }
/* 4009 */                 String str8 = str6.substring(0, j);
/* 4010 */                 str9 = str6.substring(j + 1);
/* 4011 */                 ((GeneralNames)localObject4).add(createGeneralName(str8, str9));
/*      */               }
/* 4013 */               paramCertificateExtensions2.set("CRLDistributionPoints", new CRLDistributionPointsExtension(bool1, Collections.singletonList(new DistributionPoint((GeneralNames)localObject4, null, null))));
/*      */             }
/*      */             else
/*      */             {
/* 4018 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */ 
/*      */             break;
/*      */           case -1:
/* 4023 */             localObject3 = new ObjectIdentifier((String)localObject1);
/* 4024 */             localObject4 = null;
/* 4025 */             if (localObject2 != null) {
/* 4026 */               localObject4 = new byte[((String)localObject2).length() / 2 + 1];
/* 4027 */               int i2 = 0;
/* 4028 */               for (int i8 : ((String)localObject2).toCharArray())
/*      */               {
/*      */                 int i9;
/* 4030 */                 if ((i8 >= 48) && (i8 <= 57)) {
/* 4031 */                   i9 = i8 - 48;
/* 4032 */                 } else if ((i8 >= 65) && (i8 <= 70)) {
/* 4033 */                   i9 = i8 - 65 + 10; } else {
/* 4034 */                   if ((i8 < 97) || (i8 > 102)) continue;
/* 4035 */                   i9 = i8 - 97 + 10;
/*      */                 }
/*      */ 
/* 4039 */                 if (i2 % 2 == 0) {
/* 4040 */                   localObject4[(i2 / 2)] = ((byte)(i9 << 4));
/*      */                 }
/*      */                 else
/*      */                 {
/*      */                   int tmp2446_2445 = (i2 / 2);
/*      */                   Object tmp2446_2440 = localObject4; tmp2446_2440[tmp2446_2445] = ((byte)(tmp2446_2440[tmp2446_2445] + i9));
/*      */                 }
/* 4044 */                 i2++;
/*      */               }
/* 4046 */               if (i2 % 2 != 0) {
/* 4047 */                 throw new Exception(rb.getString("Odd.number.of.hex.digits.found.") + str1);
/*      */               }
/*      */ 
/* 4050 */               localObject4 = Arrays.copyOf((byte[])localObject4, i2 / 2);
/*      */             } else {
/* 4052 */               localObject4 = new byte[0];
/*      */             }
/* 4054 */             paramCertificateExtensions2.set(((ObjectIdentifier)localObject3).toString(), new Extension((ObjectIdentifier)localObject3, bool1, new DerValue((byte)4, (byte[])localObject4).toByteArray()));
/*      */ 
/* 4057 */             break;
/*      */           case 7:
/*      */           default:
/* 4059 */             throw new Exception(rb.getString("Unknown.extension.type.") + str1);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 4064 */       paramCertificateExtensions2.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension(new KeyIdentifier(paramPublicKey1).getIdentifier()));
/*      */ 
/* 4067 */       if ((paramPublicKey2 != null) && (!paramPublicKey1.equals(paramPublicKey2))) {
/* 4068 */         paramCertificateExtensions2.set("AuthorityKeyIdentifier", new AuthorityKeyIdentifierExtension(new KeyIdentifier(paramPublicKey2), null, null));
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 4073 */       throw new RuntimeException(localIOException);
/*      */     }
/* 4075 */     return paramCertificateExtensions2;
/*      */   }
/*      */ 
/*      */   private void usage()
/*      */   {
/*      */     Object localObject1;
/*      */     int j;
/* 4082 */     if (this.command != null) {
/* 4083 */       System.err.println("keytool " + this.command + rb.getString(".OPTION."));
/*      */ 
/* 4085 */       System.err.println();
/* 4086 */       System.err.println(rb.getString(this.command.description));
/* 4087 */       System.err.println();
/* 4088 */       System.err.println(rb.getString("Options."));
/* 4089 */       System.err.println();
/*      */ 
/* 4092 */       localObject1 = new String[this.command.options.length];
/* 4093 */       String[] arrayOfString = new String[this.command.options.length];
/*      */ 
/* 4096 */       j = 0;
/*      */ 
/* 4099 */       int k = 0;
/* 4100 */       for (int m = 0; m < localObject1.length; m++) {
/* 4101 */         Option localOption = this.command.options[m];
/* 4102 */         localObject1[m] = localOption.toString();
/* 4103 */         if (localOption.arg != null)
/*      */         {
/*      */           int tmp178_176 = m;
/*      */           Object tmp178_175 = localObject1; tmp178_175[tmp178_176] = (tmp178_175[tmp178_176] + " " + localOption.arg);
/* 4104 */         }if (localObject1[m].length() > k) {
/* 4105 */           k = localObject1[m].length();
/*      */         }
/* 4107 */         arrayOfString[m] = rb.getString(localOption.description);
/*      */       }
/* 4109 */       for (m = 0; m < localObject1.length; m++) {
/* 4110 */         System.err.printf(" %-" + k + "s  %s\n", new Object[] { localObject1[m], arrayOfString[m] });
/*      */       }
/*      */ 
/* 4113 */       System.err.println();
/* 4114 */       System.err.println(rb.getString("Use.keytool.help.for.all.available.commands"));
/*      */     }
/*      */     else {
/* 4117 */       System.err.println(rb.getString("Key.and.Certificate.Management.Tool"));
/*      */ 
/* 4119 */       System.err.println();
/* 4120 */       System.err.println(rb.getString("Commands."));
/* 4121 */       System.err.println();
/* 4122 */       for (Object localObject2 : Command.values()) {
/* 4123 */         if (localObject2 == Command.KEYCLONE) break;
/* 4124 */         System.err.printf(" %-20s%s\n", new Object[] { localObject2, rb.getString(localObject2.description) });
/*      */       }
/* 4126 */       System.err.println();
/* 4127 */       System.err.println(rb.getString("Use.keytool.command.name.help.for.usage.of.command.name"));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void tinyHelp()
/*      */   {
/* 4133 */     usage();
/* 4134 */     if (this.debug) {
/* 4135 */       throw new RuntimeException("NO BIG ERROR, SORRY");
/*      */     }
/* 4137 */     System.exit(1);
/*      */   }
/*      */ 
/*      */   private void errorNeedArgument(String paramString)
/*      */   {
/* 4142 */     Object[] arrayOfObject = { paramString };
/* 4143 */     System.err.println(new MessageFormat(rb.getString("Command.option.flag.needs.an.argument.")).format(arrayOfObject));
/*      */ 
/* 4145 */     tinyHelp();
/*      */   }
/*      */ 
/*      */   private char[] getPass(String paramString1, String paramString2) {
/* 4149 */     char[] arrayOfChar = getPassWithModifier(paramString1, paramString2);
/* 4150 */     if (arrayOfChar != null) return arrayOfChar;
/* 4151 */     tinyHelp();
/* 4152 */     return null;
/*      */   }
/*      */ 
/*      */   public static char[] getPassWithModifier(String paramString1, String paramString2)
/*      */   {
/* 4157 */     if (paramString1 == null)
/* 4158 */       return paramString2.toCharArray();
/*      */     Object localObject1;
/* 4159 */     if (collator.compare(paramString1, "env") == 0) {
/* 4160 */       localObject1 = System.getenv(paramString2);
/* 4161 */       if (localObject1 == null) {
/* 4162 */         System.err.println(rb.getString("Cannot.find.environment.variable.") + paramString2);
/*      */ 
/* 4164 */         return null;
/*      */       }
/* 4166 */       return ((String)localObject1).toCharArray();
/*      */     }
/* 4168 */     if (collator.compare(paramString1, "file") == 0) {
/*      */       try {
/* 4170 */         localObject1 = null;
/*      */         try {
/* 4172 */           localObject1 = new URL(paramString2);
/*      */         } catch (MalformedURLException localMalformedURLException) {
/* 4174 */           localObject2 = new File(paramString2);
/* 4175 */           if (((File)localObject2).exists()) {
/* 4176 */             localObject1 = ((File)localObject2).toURI().toURL();
/*      */           } else {
/* 4178 */             System.err.println(rb.getString("Cannot.find.file.") + paramString2);
/*      */ 
/* 4180 */             return null;
/*      */           }
/*      */         }
/* 4183 */         BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(((URL)localObject1).openStream()));
/*      */ 
/* 4185 */         Object localObject2 = localBufferedReader.readLine();
/* 4186 */         localBufferedReader.close();
/* 4187 */         if (localObject2 == null) {
/* 4188 */           return new char[0];
/*      */         }
/* 4190 */         return ((String)localObject2).toCharArray();
/*      */       }
/*      */       catch (IOException localIOException) {
/* 4193 */         System.err.println(localIOException);
/* 4194 */         return null;
/*      */       }
/*      */     }
/* 4197 */     System.err.println(rb.getString("Unknown.password.type.") + paramString1);
/*      */ 
/* 4199 */     return null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  326 */     collator.setStrength(0);
/*      */   }
/*      */ 
/*      */   static enum Command
/*      */   {
/*  164 */     CERTREQ("Generates.a.certificate.request", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.SIGALG, KeyTool.Option.FILEOUT, KeyTool.Option.KEYPASS, KeyTool.Option.KEYSTORE, KeyTool.Option.DNAME, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  168 */     CHANGEALIAS("Changes.an.entry.s.alias", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.DESTALIAS, KeyTool.Option.KEYPASS, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  172 */     DELETE("Deletes.an.entry", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  176 */     EXPORTCERT("Exports.certificate", new KeyTool.Option[] { KeyTool.Option.RFC, KeyTool.Option.ALIAS, KeyTool.Option.FILEOUT, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  180 */     GENKEYPAIR("Generates.a.key.pair", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.KEYALG, KeyTool.Option.KEYSIZE, KeyTool.Option.SIGALG, KeyTool.Option.DESTALIAS, KeyTool.Option.DNAME, KeyTool.Option.STARTDATE, KeyTool.Option.EXT, KeyTool.Option.VALIDITY, KeyTool.Option.KEYPASS, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  185 */     GENSECKEY("Generates.a.secret.key", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.KEYPASS, KeyTool.Option.KEYALG, KeyTool.Option.KEYSIZE, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  189 */     GENCERT("Generates.certificate.from.a.certificate.request", new KeyTool.Option[] { KeyTool.Option.RFC, KeyTool.Option.INFILE, KeyTool.Option.OUTFILE, KeyTool.Option.ALIAS, KeyTool.Option.SIGALG, KeyTool.Option.DNAME, KeyTool.Option.STARTDATE, KeyTool.Option.EXT, KeyTool.Option.VALIDITY, KeyTool.Option.KEYPASS, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  194 */     IMPORTCERT("Imports.a.certificate.or.a.certificate.chain", new KeyTool.Option[] { KeyTool.Option.NOPROMPT, KeyTool.Option.TRUSTCACERTS, KeyTool.Option.PROTECTED, KeyTool.Option.ALIAS, KeyTool.Option.FILEIN, KeyTool.Option.KEYPASS, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V }), 
/*      */ 
/*  199 */     IMPORTKEYSTORE("Imports.one.or.all.entries.from.another.keystore", new KeyTool.Option[] { KeyTool.Option.SRCKEYSTORE, KeyTool.Option.DESTKEYSTORE, KeyTool.Option.SRCSTORETYPE, KeyTool.Option.DESTSTORETYPE, KeyTool.Option.SRCSTOREPASS, KeyTool.Option.DESTSTOREPASS, KeyTool.Option.SRCPROTECTED, KeyTool.Option.SRCPROVIDERNAME, KeyTool.Option.DESTPROVIDERNAME, KeyTool.Option.SRCALIAS, KeyTool.Option.DESTALIAS, KeyTool.Option.SRCKEYPASS, KeyTool.Option.DESTKEYPASS, KeyTool.Option.NOPROMPT, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V }), 
/*      */ 
/*  206 */     KEYPASSWD("Changes.the.key.password.of.an.entry", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.KEYPASS, KeyTool.Option.NEW, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V }), 
/*      */ 
/*  210 */     LIST("Lists.entries.in.a.keystore", new KeyTool.Option[] { KeyTool.Option.RFC, KeyTool.Option.ALIAS, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  214 */     PRINTCERT("Prints.the.content.of.a.certificate", new KeyTool.Option[] { KeyTool.Option.RFC, KeyTool.Option.FILEIN, KeyTool.Option.SSLSERVER, KeyTool.Option.JARFILE, KeyTool.Option.V }), 
/*      */ 
/*  216 */     PRINTCERTREQ("Prints.the.content.of.a.certificate.request", new KeyTool.Option[] { KeyTool.Option.FILEIN, KeyTool.Option.V }), 
/*      */ 
/*  218 */     PRINTCRL("Prints.the.content.of.a.CRL.file", new KeyTool.Option[] { KeyTool.Option.FILEIN, KeyTool.Option.V }), 
/*      */ 
/*  220 */     STOREPASSWD("Changes.the.store.password.of.a.keystore", new KeyTool.Option[] { KeyTool.Option.NEW, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V }), 
/*      */ 
/*  226 */     KEYCLONE("Clones.a.key.entry", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.DESTALIAS, KeyTool.Option.KEYPASS, KeyTool.Option.NEW, KeyTool.Option.STORETYPE, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V }), 
/*      */ 
/*  230 */     SELFCERT("Generates.a.self.signed.certificate", new KeyTool.Option[] { KeyTool.Option.ALIAS, KeyTool.Option.SIGALG, KeyTool.Option.DNAME, KeyTool.Option.STARTDATE, KeyTool.Option.VALIDITY, KeyTool.Option.KEYPASS, KeyTool.Option.STORETYPE, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V }), 
/*      */ 
/*  234 */     GENCRL("Generates.CRL", new KeyTool.Option[] { KeyTool.Option.RFC, KeyTool.Option.FILEOUT, KeyTool.Option.ID, KeyTool.Option.ALIAS, KeyTool.Option.SIGALG, KeyTool.Option.EXT, KeyTool.Option.KEYPASS, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.STORETYPE, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V, KeyTool.Option.PROTECTED }), 
/*      */ 
/*  239 */     IDENTITYDB("Imports.entries.from.a.JDK.1.1.x.style.identity.database", new KeyTool.Option[] { KeyTool.Option.FILEIN, KeyTool.Option.STORETYPE, KeyTool.Option.KEYSTORE, KeyTool.Option.STOREPASS, KeyTool.Option.PROVIDERNAME, KeyTool.Option.PROVIDERCLASS, KeyTool.Option.PROVIDERARG, KeyTool.Option.PROVIDERPATH, KeyTool.Option.V });
/*      */ 
/*      */     final String description;
/*      */     final KeyTool.Option[] options;
/*      */ 
/*      */     private Command(String paramString, KeyTool.Option[] paramArrayOfOption) {
/*  246 */       this.description = paramString;
/*  247 */       this.options = paramArrayOfOption;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  251 */       return "-" + name().toLowerCase(Locale.ENGLISH);
/*      */     }
/*      */   }
/*      */ 
/*      */   static enum Option {
/*  256 */     ALIAS("alias", "<alias>", "alias.name.of.the.entry.to.process"), 
/*  257 */     DESTALIAS("destalias", "<destalias>", "destination.alias"), 
/*  258 */     DESTKEYPASS("destkeypass", "<arg>", "destination.key.password"), 
/*  259 */     DESTKEYSTORE("destkeystore", "<destkeystore>", "destination.keystore.name"), 
/*  260 */     DESTPROTECTED("destprotected", null, "destination.keystore.password.protected"), 
/*  261 */     DESTPROVIDERNAME("destprovidername", "<destprovidername>", "destination.keystore.provider.name"), 
/*  262 */     DESTSTOREPASS("deststorepass", "<arg>", "destination.keystore.password"), 
/*  263 */     DESTSTORETYPE("deststoretype", "<deststoretype>", "destination.keystore.type"), 
/*  264 */     DNAME("dname", "<dname>", "distinguished.name"), 
/*  265 */     EXT("ext", "<value>", "X.509.extension"), 
/*  266 */     FILEOUT("file", "<filename>", "output.file.name"), 
/*  267 */     FILEIN("file", "<filename>", "input.file.name"), 
/*  268 */     ID("id", "<id:reason>", "Serial.ID.of.cert.to.revoke"), 
/*  269 */     INFILE("infile", "<filename>", "input.file.name"), 
/*  270 */     KEYALG("keyalg", "<keyalg>", "key.algorithm.name"), 
/*  271 */     KEYPASS("keypass", "<arg>", "key.password"), 
/*  272 */     KEYSIZE("keysize", "<keysize>", "key.bit.size"), 
/*  273 */     KEYSTORE("keystore", "<keystore>", "keystore.name"), 
/*  274 */     NEW("new", "<arg>", "new.password"), 
/*  275 */     NOPROMPT("noprompt", null, "do.not.prompt"), 
/*  276 */     OUTFILE("outfile", "<filename>", "output.file.name"), 
/*  277 */     PROTECTED("protected", null, "password.through.protected.mechanism"), 
/*  278 */     PROVIDERARG("providerarg", "<arg>", "provider.argument"), 
/*  279 */     PROVIDERCLASS("providerclass", "<providerclass>", "provider.class.name"), 
/*  280 */     PROVIDERNAME("providername", "<providername>", "provider.name"), 
/*  281 */     PROVIDERPATH("providerpath", "<pathlist>", "provider.classpath"), 
/*  282 */     RFC("rfc", null, "output.in.RFC.style"), 
/*  283 */     SIGALG("sigalg", "<sigalg>", "signature.algorithm.name"), 
/*  284 */     SRCALIAS("srcalias", "<srcalias>", "source.alias"), 
/*  285 */     SRCKEYPASS("srckeypass", "<arg>", "source.key.password"), 
/*  286 */     SRCKEYSTORE("srckeystore", "<srckeystore>", "source.keystore.name"), 
/*  287 */     SRCPROTECTED("srcprotected", null, "source.keystore.password.protected"), 
/*  288 */     SRCPROVIDERNAME("srcprovidername", "<srcprovidername>", "source.keystore.provider.name"), 
/*  289 */     SRCSTOREPASS("srcstorepass", "<arg>", "source.keystore.password"), 
/*  290 */     SRCSTORETYPE("srcstoretype", "<srcstoretype>", "source.keystore.type"), 
/*  291 */     SSLSERVER("sslserver", "<server[:port]>", "SSL.server.host.and.port"), 
/*  292 */     JARFILE("jarfile", "<filename>", "signed.jar.file"), 
/*  293 */     STARTDATE("startdate", "<startdate>", "certificate.validity.start.date.time"), 
/*  294 */     STOREPASS("storepass", "<arg>", "keystore.password"), 
/*  295 */     STORETYPE("storetype", "<storetype>", "keystore.type"), 
/*  296 */     TRUSTCACERTS("trustcacerts", null, "trust.certificates.from.cacerts"), 
/*  297 */     V("v", null, "verbose.output"), 
/*  298 */     VALIDITY("validity", "<valDays>", "validity.number.of.days");
/*      */ 
/*      */     final String name;
/*      */     final String arg;
/*      */     final String description;
/*      */ 
/*  302 */     private Option(String paramString1, String paramString2, String paramString3) { this.name = paramString1;
/*  303 */       this.arg = paramString2;
/*  304 */       this.description = paramString3; }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  308 */       return "-" + this.name;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.tools.KeyTool
 * JD-Core Version:    0.6.2
 */