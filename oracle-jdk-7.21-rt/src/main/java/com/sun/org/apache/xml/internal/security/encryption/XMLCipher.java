/*      */ package com.sun.org.apache.xml.internal.security.encryption;
/*      */ 
/*      */ import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
/*      */ import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
/*      */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*      */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*      */ import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
/*      */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
/*      */ import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.EncryptedKeyResolver;
/*      */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
/*      */ import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
/*      */ import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
/*      */ import com.sun.org.apache.xml.internal.security.utils.Base64;
/*      */ import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
/*      */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*      */ import com.sun.org.apache.xml.internal.utils.URI;
/*      */ import com.sun.org.apache.xml.internal.utils.URI.MalformedURIException;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.security.InvalidAlgorithmParameterException;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.Key;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.NoSuchProviderException;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.crypto.BadPaddingException;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.IllegalBlockSizeException;
/*      */ import javax.crypto.NoSuchPaddingException;
/*      */ import javax.crypto.spec.IvParameterSpec;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.DocumentFragment;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.w3c.dom.Text;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class XMLCipher
/*      */ {
/*   87 */   private static Logger logger = Logger.getLogger(XMLCipher.class.getName());
/*      */   public static final String TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
/*      */   public static final String AES_128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
/*      */   public static final String AES_256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
/*      */   public static final String AES_192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
/*      */   public static final String RSA_v1dot5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
/*      */   public static final String RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
/*      */   public static final String DIFFIE_HELLMAN = "http://www.w3.org/2001/04/xmlenc#dh";
/*      */   public static final String TRIPLEDES_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
/*      */   public static final String AES_128_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
/*      */   public static final String AES_256_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
/*      */   public static final String AES_192_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
/*      */   public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
/*      */   public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
/*      */   public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
/*      */   public static final String RIPEMD_160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
/*      */   public static final String XML_DSIG = "http://www.w3.org/2000/09/xmldsig#";
/*      */   public static final String N14C_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
/*      */   public static final String N14C_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
/*      */   public static final String EXCL_XML_N14C = "http://www.w3.org/2001/10/xml-exc-c14n#";
/*      */   public static final String EXCL_XML_N14C_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
/*      */   public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
/*      */   public static final int ENCRYPT_MODE = 1;
/*      */   public static final int DECRYPT_MODE = 2;
/*      */   public static final int UNWRAP_MODE = 4;
/*      */   public static final int WRAP_MODE = 3;
/*      */   private static final String ENC_ALGORITHMS = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\n";
/*      */   private Cipher _contextCipher;
/*  173 */   private int _cipherMode = -2147483648;
/*      */ 
/*  175 */   private String _algorithm = null;
/*      */ 
/*  177 */   private String _requestedJCEProvider = null;
/*      */   private Canonicalizer _canon;
/*      */   private Document _contextDocument;
/*      */   private Factory _factory;
/*      */   private Serializer _serializer;
/*      */   private Key _key;
/*      */   private Key _kek;
/*      */   private EncryptedKey _ek;
/*      */   private EncryptedData _ed;
/*      */ 
/*      */   private XMLCipher()
/*      */   {
/*  209 */     logger.log(Level.FINE, "Constructing XMLCipher...");
/*      */ 
/*  211 */     this._factory = new Factory(null);
/*  212 */     this._serializer = new Serializer();
/*      */   }
/*      */ 
/*      */   private static boolean isValidEncryptionAlgorithm(String paramString)
/*      */   {
/*  224 */     boolean bool = (paramString.equals("http://www.w3.org/2001/04/xmlenc#tripledes-cbc")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#aes128-cbc")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#aes256-cbc")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#aes192-cbc")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#rsa-1_5")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-tripledes")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes128")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes256")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes192"));
/*      */ 
/*  237 */     return bool;
/*      */   }
/*      */ 
/*      */   public static XMLCipher getInstance(String paramString)
/*      */     throws XMLEncryptionException
/*      */   {
/*  271 */     logger.log(Level.FINE, "Getting XMLCipher...");
/*  272 */     if (null == paramString)
/*  273 */       logger.log(Level.SEVERE, "Transformation unexpectedly null...");
/*  274 */     if (!isValidEncryptionAlgorithm(paramString)) {
/*  275 */       logger.log(Level.WARNING, "Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\n");
/*      */     }
/*  277 */     XMLCipher localXMLCipher = new XMLCipher();
/*      */ 
/*  279 */     localXMLCipher._algorithm = paramString;
/*  280 */     localXMLCipher._key = null;
/*  281 */     localXMLCipher._kek = null;
/*      */     try
/*      */     {
/*  288 */       localXMLCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
/*      */     }
/*      */     catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
/*      */     {
/*  292 */       throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
/*      */     }
/*      */ 
/*  295 */     String str = JCEMapper.translateURItoJCEID(paramString);
/*      */     try
/*      */     {
/*  298 */       localXMLCipher._contextCipher = Cipher.getInstance(str);
/*  299 */       logger.log(Level.FINE, "cihper.algoritm = " + localXMLCipher._contextCipher.getAlgorithm());
/*      */     }
/*      */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  302 */       throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
/*      */     } catch (NoSuchPaddingException localNoSuchPaddingException) {
/*  304 */       throw new XMLEncryptionException("empty", localNoSuchPaddingException);
/*      */     }
/*      */ 
/*  307 */     return localXMLCipher;
/*      */   }
/*      */ 
/*      */   public static XMLCipher getInstance(String paramString1, String paramString2)
/*      */     throws XMLEncryptionException
/*      */   {
/*  329 */     XMLCipher localXMLCipher = getInstance(paramString1);
/*      */ 
/*  331 */     if (paramString2 != null) {
/*      */       try {
/*  333 */         localXMLCipher._canon = Canonicalizer.getInstance(paramString2);
/*      */       } catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/*  335 */         throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
/*      */       }
/*      */     }
/*      */ 
/*  339 */     return localXMLCipher;
/*      */   }
/*      */ 
/*      */   public static XMLCipher getInstance(String paramString, Cipher paramCipher) throws XMLEncryptionException
/*      */   {
/*  344 */     logger.log(Level.FINE, "Getting XMLCipher...");
/*  345 */     if (null == paramString)
/*  346 */       logger.log(Level.SEVERE, "Transformation unexpectedly null...");
/*  347 */     if (!isValidEncryptionAlgorithm(paramString)) {
/*  348 */       logger.log(Level.WARNING, "Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\n");
/*      */     }
/*  350 */     XMLCipher localXMLCipher = new XMLCipher();
/*      */ 
/*  352 */     localXMLCipher._algorithm = paramString;
/*  353 */     localXMLCipher._key = null;
/*  354 */     localXMLCipher._kek = null;
/*      */     try
/*      */     {
/*  361 */       localXMLCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
/*      */     }
/*      */     catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
/*      */     {
/*  365 */       throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
/*      */     }
/*      */ 
/*  368 */     String str = JCEMapper.translateURItoJCEID(paramString);
/*      */     try
/*      */     {
/*  371 */       localXMLCipher._contextCipher = paramCipher;
/*      */ 
/*  373 */       logger.log(Level.FINE, "cihper.algoritm = " + localXMLCipher._contextCipher.getAlgorithm());
/*      */     }
/*      */     catch (Exception localException) {
/*  376 */       throw new XMLEncryptionException("empty", localException);
/*      */     }
/*      */ 
/*  379 */     return localXMLCipher;
/*      */   }
/*      */ 
/*      */   public static XMLCipher getProviderInstance(String paramString1, String paramString2)
/*      */     throws XMLEncryptionException
/*      */   {
/*  397 */     logger.log(Level.FINE, "Getting XMLCipher...");
/*  398 */     if (null == paramString1)
/*  399 */       logger.log(Level.SEVERE, "Transformation unexpectedly null...");
/*  400 */     if (null == paramString2)
/*  401 */       logger.log(Level.SEVERE, "Provider unexpectedly null..");
/*  402 */     if ("" == paramString2)
/*  403 */       logger.log(Level.SEVERE, "Provider's value unexpectedly not specified...");
/*  404 */     if (!isValidEncryptionAlgorithm(paramString1)) {
/*  405 */       logger.log(Level.WARNING, "Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\n");
/*      */     }
/*  407 */     XMLCipher localXMLCipher = new XMLCipher();
/*      */ 
/*  409 */     localXMLCipher._algorithm = paramString1;
/*  410 */     localXMLCipher._requestedJCEProvider = paramString2;
/*  411 */     localXMLCipher._key = null;
/*  412 */     localXMLCipher._kek = null;
/*      */     try
/*      */     {
/*  418 */       localXMLCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
/*      */     }
/*      */     catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/*  421 */       throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
/*      */     }
/*      */     try
/*      */     {
/*  425 */       String str = JCEMapper.translateURItoJCEID(paramString1);
/*      */ 
/*  428 */       localXMLCipher._contextCipher = Cipher.getInstance(str, paramString2);
/*      */ 
/*  430 */       logger.log(Level.FINE, "cipher._algorithm = " + localXMLCipher._contextCipher.getAlgorithm());
/*      */ 
/*  432 */       logger.log(Level.FINE, "provider.name = " + paramString2);
/*      */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  434 */       throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
/*      */     } catch (NoSuchProviderException localNoSuchProviderException) {
/*  436 */       throw new XMLEncryptionException("empty", localNoSuchProviderException);
/*      */     } catch (NoSuchPaddingException localNoSuchPaddingException) {
/*  438 */       throw new XMLEncryptionException("empty", localNoSuchPaddingException);
/*      */     }
/*      */ 
/*  441 */     return localXMLCipher;
/*      */   }
/*      */ 
/*      */   public static XMLCipher getProviderInstance(String paramString1, String paramString2, String paramString3)
/*      */     throws XMLEncryptionException
/*      */   {
/*  467 */     XMLCipher localXMLCipher = getProviderInstance(paramString1, paramString2);
/*  468 */     if (paramString3 != null) {
/*      */       try {
/*  470 */         localXMLCipher._canon = Canonicalizer.getInstance(paramString3);
/*      */       } catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/*  472 */         throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
/*      */       }
/*      */     }
/*  475 */     return localXMLCipher;
/*      */   }
/*      */ 
/*      */   public static XMLCipher getInstance()
/*      */     throws XMLEncryptionException
/*      */   {
/*  491 */     logger.log(Level.FINE, "Getting XMLCipher for no transformation...");
/*      */ 
/*  493 */     XMLCipher localXMLCipher = new XMLCipher();
/*      */ 
/*  495 */     localXMLCipher._algorithm = null;
/*  496 */     localXMLCipher._requestedJCEProvider = null;
/*  497 */     localXMLCipher._key = null;
/*  498 */     localXMLCipher._kek = null;
/*  499 */     localXMLCipher._contextCipher = null;
/*      */     try
/*      */     {
/*  505 */       localXMLCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
/*      */     }
/*      */     catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/*  508 */       throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
/*      */     }
/*      */ 
/*  511 */     return localXMLCipher;
/*      */   }
/*      */ 
/*      */   public static XMLCipher getProviderInstance(String paramString)
/*      */     throws XMLEncryptionException
/*      */   {
/*  533 */     logger.log(Level.FINE, "Getting XMLCipher, provider but no transformation");
/*  534 */     if (null == paramString)
/*  535 */       logger.log(Level.SEVERE, "Provider unexpectedly null..");
/*  536 */     if ("" == paramString) {
/*  537 */       logger.log(Level.SEVERE, "Provider's value unexpectedly not specified...");
/*      */     }
/*  539 */     XMLCipher localXMLCipher = new XMLCipher();
/*      */ 
/*  541 */     localXMLCipher._algorithm = null;
/*  542 */     localXMLCipher._requestedJCEProvider = paramString;
/*  543 */     localXMLCipher._key = null;
/*  544 */     localXMLCipher._kek = null;
/*  545 */     localXMLCipher._contextCipher = null;
/*      */     try
/*      */     {
/*  548 */       localXMLCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
/*      */     }
/*      */     catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/*  551 */       throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
/*      */     }
/*      */ 
/*  554 */     return localXMLCipher;
/*      */   }
/*      */ 
/*      */   public void init(int paramInt, Key paramKey)
/*      */     throws XMLEncryptionException
/*      */   {
/*  579 */     logger.log(Level.FINE, "Initializing XMLCipher...");
/*      */ 
/*  581 */     this._ek = null;
/*  582 */     this._ed = null;
/*      */ 
/*  584 */     switch (paramInt)
/*      */     {
/*      */     case 1:
/*  587 */       logger.log(Level.FINE, "opmode = ENCRYPT_MODE");
/*  588 */       this._ed = createEncryptedData(1, "NO VALUE YET");
/*  589 */       break;
/*      */     case 2:
/*  591 */       logger.log(Level.FINE, "opmode = DECRYPT_MODE");
/*  592 */       break;
/*      */     case 3:
/*  594 */       logger.log(Level.FINE, "opmode = WRAP_MODE");
/*  595 */       this._ek = createEncryptedKey(1, "NO VALUE YET");
/*  596 */       break;
/*      */     case 4:
/*  598 */       logger.log(Level.FINE, "opmode = UNWRAP_MODE");
/*  599 */       break;
/*      */     default:
/*  601 */       logger.log(Level.SEVERE, "Mode unexpectedly invalid");
/*  602 */       throw new XMLEncryptionException("Invalid mode in init");
/*      */     }
/*      */ 
/*  605 */     this._cipherMode = paramInt;
/*  606 */     this._key = paramKey;
/*      */   }
/*      */ 
/*      */   public EncryptedData getEncryptedData()
/*      */   {
/*  623 */     logger.log(Level.FINE, "Returning EncryptedData");
/*  624 */     return this._ed;
/*      */   }
/*      */ 
/*      */   public EncryptedKey getEncryptedKey()
/*      */   {
/*  641 */     logger.log(Level.FINE, "Returning EncryptedKey");
/*  642 */     return this._ek;
/*      */   }
/*      */ 
/*      */   public void setKEK(Key paramKey)
/*      */   {
/*  658 */     this._kek = paramKey;
/*      */   }
/*      */ 
/*      */   public Element martial(EncryptedData paramEncryptedData)
/*      */   {
/*  678 */     return this._factory.toElement(paramEncryptedData);
/*      */   }
/*      */ 
/*      */   public Element martial(EncryptedKey paramEncryptedKey)
/*      */   {
/*  698 */     return this._factory.toElement(paramEncryptedKey);
/*      */   }
/*      */ 
/*      */   public Element martial(Document paramDocument, EncryptedData paramEncryptedData)
/*      */   {
/*  715 */     this._contextDocument = paramDocument;
/*  716 */     return this._factory.toElement(paramEncryptedData);
/*      */   }
/*      */ 
/*      */   public Element martial(Document paramDocument, EncryptedKey paramEncryptedKey)
/*      */   {
/*  733 */     this._contextDocument = paramDocument;
/*  734 */     return this._factory.toElement(paramEncryptedKey);
/*      */   }
/*      */ 
/*      */   private Document encryptElement(Element paramElement)
/*      */     throws Exception
/*      */   {
/*  751 */     logger.log(Level.FINE, "Encrypting element...");
/*  752 */     if (null == paramElement)
/*  753 */       logger.log(Level.SEVERE, "Element unexpectedly null...");
/*  754 */     if (this._cipherMode != 1) {
/*  755 */       logger.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
/*      */     }
/*  757 */     if (this._algorithm == null) {
/*  758 */       throw new XMLEncryptionException("XMLCipher instance without transformation specified");
/*      */     }
/*  760 */     encryptData(this._contextDocument, paramElement, false);
/*      */ 
/*  762 */     Element localElement = this._factory.toElement(this._ed);
/*      */ 
/*  764 */     Node localNode = paramElement.getParentNode();
/*  765 */     localNode.replaceChild(localElement, paramElement);
/*      */ 
/*  767 */     return this._contextDocument;
/*      */   }
/*      */ 
/*      */   private Document encryptElementContent(Element paramElement)
/*      */     throws Exception
/*      */   {
/*  786 */     logger.log(Level.FINE, "Encrypting element content...");
/*  787 */     if (null == paramElement)
/*  788 */       logger.log(Level.SEVERE, "Element unexpectedly null...");
/*  789 */     if (this._cipherMode != 1) {
/*  790 */       logger.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
/*      */     }
/*  792 */     if (this._algorithm == null) {
/*  793 */       throw new XMLEncryptionException("XMLCipher instance without transformation specified");
/*      */     }
/*  795 */     encryptData(this._contextDocument, paramElement, true);
/*      */ 
/*  797 */     Element localElement = this._factory.toElement(this._ed);
/*      */ 
/*  799 */     removeContent(paramElement);
/*  800 */     paramElement.appendChild(localElement);
/*      */ 
/*  802 */     return this._contextDocument;
/*      */   }
/*      */ 
/*      */   public Document doFinal(Document paramDocument1, Document paramDocument2)
/*      */     throws Exception
/*      */   {
/*  816 */     logger.log(Level.FINE, "Processing source document...");
/*  817 */     if (null == paramDocument1)
/*  818 */       logger.log(Level.SEVERE, "Context document unexpectedly null...");
/*  819 */     if (null == paramDocument2) {
/*  820 */       logger.log(Level.SEVERE, "Source document unexpectedly null...");
/*      */     }
/*  822 */     this._contextDocument = paramDocument1;
/*      */ 
/*  824 */     Document localDocument = null;
/*      */ 
/*  826 */     switch (this._cipherMode) {
/*      */     case 2:
/*  828 */       localDocument = decryptElement(paramDocument2.getDocumentElement());
/*  829 */       break;
/*      */     case 1:
/*  831 */       localDocument = encryptElement(paramDocument2.getDocumentElement());
/*  832 */       break;
/*      */     case 4:
/*  834 */       break;
/*      */     case 3:
/*  836 */       break;
/*      */     default:
/*  838 */       throw new XMLEncryptionException("empty", new IllegalStateException());
/*      */     }
/*      */ 
/*  842 */     return localDocument;
/*      */   }
/*      */ 
/*      */   public Document doFinal(Document paramDocument, Element paramElement)
/*      */     throws Exception
/*      */   {
/*  856 */     logger.log(Level.FINE, "Processing source element...");
/*  857 */     if (null == paramDocument)
/*  858 */       logger.log(Level.SEVERE, "Context document unexpectedly null...");
/*  859 */     if (null == paramElement) {
/*  860 */       logger.log(Level.SEVERE, "Source element unexpectedly null...");
/*      */     }
/*  862 */     this._contextDocument = paramDocument;
/*      */ 
/*  864 */     Document localDocument = null;
/*      */ 
/*  866 */     switch (this._cipherMode) {
/*      */     case 2:
/*  868 */       localDocument = decryptElement(paramElement);
/*  869 */       break;
/*      */     case 1:
/*  871 */       localDocument = encryptElement(paramElement);
/*  872 */       break;
/*      */     case 4:
/*  874 */       break;
/*      */     case 3:
/*  876 */       break;
/*      */     default:
/*  878 */       throw new XMLEncryptionException("empty", new IllegalStateException());
/*      */     }
/*      */ 
/*  882 */     return localDocument;
/*      */   }
/*      */ 
/*      */   public Document doFinal(Document paramDocument, Element paramElement, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/*  899 */     logger.log(Level.FINE, "Processing source element...");
/*  900 */     if (null == paramDocument)
/*  901 */       logger.log(Level.SEVERE, "Context document unexpectedly null...");
/*  902 */     if (null == paramElement) {
/*  903 */       logger.log(Level.SEVERE, "Source element unexpectedly null...");
/*      */     }
/*  905 */     this._contextDocument = paramDocument;
/*      */ 
/*  907 */     Document localDocument = null;
/*      */ 
/*  909 */     switch (this._cipherMode) {
/*      */     case 2:
/*  911 */       if (paramBoolean)
/*  912 */         localDocument = decryptElementContent(paramElement);
/*      */       else {
/*  914 */         localDocument = decryptElement(paramElement);
/*      */       }
/*  916 */       break;
/*      */     case 1:
/*  918 */       if (paramBoolean)
/*  919 */         localDocument = encryptElementContent(paramElement);
/*      */       else {
/*  921 */         localDocument = encryptElement(paramElement);
/*      */       }
/*  923 */       break;
/*      */     case 4:
/*  925 */       break;
/*      */     case 3:
/*  927 */       break;
/*      */     default:
/*  929 */       throw new XMLEncryptionException("empty", new IllegalStateException());
/*      */     }
/*      */ 
/*  933 */     return localDocument;
/*      */   }
/*      */ 
/*      */   public EncryptedData encryptData(Document paramDocument, Element paramElement)
/*      */     throws Exception
/*      */   {
/*  950 */     return encryptData(paramDocument, paramElement, false);
/*      */   }
/*      */ 
/*      */   public EncryptedData encryptData(Document paramDocument, String paramString, InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/*  970 */     logger.log(Level.FINE, "Encrypting element...");
/*  971 */     if (null == paramDocument)
/*  972 */       logger.log(Level.SEVERE, "Context document unexpectedly null...");
/*  973 */     if (null == paramInputStream)
/*  974 */       logger.log(Level.SEVERE, "Serialized data unexpectedly null...");
/*  975 */     if (this._cipherMode != 1) {
/*  976 */       logger.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
/*      */     }
/*  978 */     return encryptData(paramDocument, null, paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */   public EncryptedData encryptData(Document paramDocument, Element paramElement, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/*  999 */     logger.log(Level.FINE, "Encrypting element...");
/* 1000 */     if (null == paramDocument)
/* 1001 */       logger.log(Level.SEVERE, "Context document unexpectedly null...");
/* 1002 */     if (null == paramElement)
/* 1003 */       logger.log(Level.SEVERE, "Element unexpectedly null...");
/* 1004 */     if (this._cipherMode != 1) {
/* 1005 */       logger.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
/*      */     }
/* 1007 */     if (paramBoolean) {
/* 1008 */       return encryptData(paramDocument, paramElement, "http://www.w3.org/2001/04/xmlenc#Content", null);
/*      */     }
/*      */ 
/* 1011 */     return encryptData(paramDocument, paramElement, "http://www.w3.org/2001/04/xmlenc#Element", null);
/*      */   }
/*      */ 
/*      */   private EncryptedData encryptData(Document paramDocument, Element paramElement, String paramString, InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/* 1020 */     this._contextDocument = paramDocument;
/*      */ 
/* 1022 */     if (this._algorithm == null) {
/* 1023 */       throw new XMLEncryptionException("XMLCipher instance without transformation specified");
/*      */     }
/*      */ 
/* 1027 */     String str1 = null;
/*      */     Object localObject2;
/* 1028 */     if (paramInputStream == null) {
/* 1029 */       if (paramString == "http://www.w3.org/2001/04/xmlenc#Content") {
/* 1030 */         localObject1 = paramElement.getChildNodes();
/* 1031 */         if (null != localObject1) {
/* 1032 */           str1 = this._serializer.serialize((NodeList)localObject1);
/*      */         } else {
/* 1034 */           localObject2 = new Object[] { "Element has no content." };
/* 1035 */           throw new XMLEncryptionException("empty", (Object[])localObject2);
/*      */         }
/*      */       } else {
/* 1038 */         str1 = this._serializer.serialize(paramElement);
/*      */       }
/* 1040 */       logger.log(Level.FINE, "Serialized octets:\n" + str1);
/*      */     }
/*      */ 
/* 1043 */     Object localObject1 = null;
/*      */ 
/* 1047 */     if (this._contextCipher == null) {
/* 1048 */       String str2 = JCEMapper.translateURItoJCEID(this._algorithm);
/* 1049 */       logger.log(Level.FINE, "alg = " + str2);
/*      */       try
/*      */       {
/* 1052 */         if (this._requestedJCEProvider == null)
/* 1053 */           localObject2 = Cipher.getInstance(str2);
/*      */         else
/* 1055 */           localObject2 = Cipher.getInstance(str2, this._requestedJCEProvider);
/*      */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 1057 */         throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
/*      */       } catch (NoSuchProviderException localNoSuchProviderException) {
/* 1059 */         throw new XMLEncryptionException("empty", localNoSuchProviderException);
/*      */       } catch (NoSuchPaddingException localNoSuchPaddingException) {
/* 1061 */         throw new XMLEncryptionException("empty", localNoSuchPaddingException);
/*      */       }
/*      */     } else {
/* 1064 */       localObject2 = this._contextCipher;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1071 */       ((Cipher)localObject2).init(this._cipherMode, this._key);
/*      */     } catch (InvalidKeyException localInvalidKeyException) {
/* 1073 */       throw new XMLEncryptionException("empty", localInvalidKeyException);
/*      */     }
/*      */     Object localObject4;
/*      */     try {
/* 1077 */       if (paramInputStream != null)
/*      */       {
/* 1079 */         arrayOfByte2 = new byte[8192];
/* 1080 */         localObject3 = new ByteArrayOutputStream();
/*      */         int i;
/* 1081 */         while ((i = paramInputStream.read(arrayOfByte2)) != -1) {
/* 1082 */           localObject4 = ((Cipher)localObject2).update(arrayOfByte2, 0, i);
/* 1083 */           ((ByteArrayOutputStream)localObject3).write((byte[])localObject4);
/*      */         }
/* 1085 */         ((ByteArrayOutputStream)localObject3).write(((Cipher)localObject2).doFinal());
/* 1086 */         localObject1 = ((ByteArrayOutputStream)localObject3).toByteArray();
/*      */       } else {
/* 1088 */         localObject1 = ((Cipher)localObject2).doFinal(str1.getBytes("UTF-8"));
/* 1089 */         logger.log(Level.FINE, "Expected cipher.outputSize = " + Integer.toString(((Cipher)localObject2).getOutputSize(str1.getBytes().length)));
/*      */       }
/*      */ 
/* 1093 */       logger.log(Level.FINE, "Actual cipher.outputSize = " + Integer.toString(localObject1.length));
/*      */     }
/*      */     catch (IllegalStateException localIllegalStateException) {
/* 1096 */       throw new XMLEncryptionException("empty", localIllegalStateException);
/*      */     } catch (IllegalBlockSizeException localIllegalBlockSizeException) {
/* 1098 */       throw new XMLEncryptionException("empty", localIllegalBlockSizeException);
/*      */     } catch (BadPaddingException localBadPaddingException) {
/* 1100 */       throw new XMLEncryptionException("empty", localBadPaddingException);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1102 */       throw new XMLEncryptionException("empty", localUnsupportedEncodingException);
/*      */     }
/*      */ 
/* 1107 */     byte[] arrayOfByte1 = ((Cipher)localObject2).getIV();
/* 1108 */     byte[] arrayOfByte2 = new byte[arrayOfByte1.length + localObject1.length];
/*      */ 
/* 1110 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
/* 1111 */     System.arraycopy(localObject1, 0, arrayOfByte2, arrayOfByte1.length, localObject1.length);
/*      */ 
/* 1113 */     Object localObject3 = Base64.encode(arrayOfByte2);
/*      */ 
/* 1115 */     logger.log(Level.FINE, "Encrypted octets:\n" + (String)localObject3);
/* 1116 */     logger.log(Level.FINE, "Encrypted octets length = " + ((String)localObject3).length());
/*      */     try
/*      */     {
/* 1120 */       localObject4 = this._ed.getCipherData();
/* 1121 */       CipherValue localCipherValue = ((CipherData)localObject4).getCipherValue();
/*      */ 
/* 1123 */       localCipherValue.setValue((String)localObject3);
/*      */ 
/* 1125 */       if (paramString != null) {
/* 1126 */         this._ed.setType(new URI(paramString).toString());
/*      */       }
/* 1128 */       EncryptionMethod localEncryptionMethod = this._factory.newEncryptionMethod(new URI(this._algorithm).toString());
/*      */ 
/* 1130 */       this._ed.setEncryptionMethod(localEncryptionMethod);
/*      */     } catch (URI.MalformedURIException localMalformedURIException) {
/* 1132 */       throw new XMLEncryptionException("empty", localMalformedURIException);
/*      */     }
/* 1134 */     return this._ed;
/*      */   }
/*      */ 
/*      */   public EncryptedData loadEncryptedData(Document paramDocument, Element paramElement)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1149 */     logger.log(Level.FINE, "Loading encrypted element...");
/* 1150 */     if (null == paramDocument)
/* 1151 */       logger.log(Level.SEVERE, "Context document unexpectedly null...");
/* 1152 */     if (null == paramElement)
/* 1153 */       logger.log(Level.SEVERE, "Element unexpectedly null...");
/* 1154 */     if (this._cipherMode != 2) {
/* 1155 */       logger.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE...");
/*      */     }
/* 1157 */     this._contextDocument = paramDocument;
/* 1158 */     this._ed = this._factory.newEncryptedData(paramElement);
/*      */ 
/* 1160 */     return this._ed;
/*      */   }
/*      */ 
/*      */   public EncryptedKey loadEncryptedKey(Document paramDocument, Element paramElement)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1176 */     logger.log(Level.FINE, "Loading encrypted key...");
/* 1177 */     if (null == paramDocument)
/* 1178 */       logger.log(Level.SEVERE, "Context document unexpectedly null...");
/* 1179 */     if (null == paramElement)
/* 1180 */       logger.log(Level.SEVERE, "Element unexpectedly null...");
/* 1181 */     if ((this._cipherMode != 4) && (this._cipherMode != 2)) {
/* 1182 */       logger.log(Level.FINE, "XMLCipher unexpectedly not in UNWRAP_MODE or DECRYPT_MODE...");
/*      */     }
/* 1184 */     this._contextDocument = paramDocument;
/* 1185 */     this._ek = this._factory.newEncryptedKey(paramElement);
/* 1186 */     return this._ek;
/*      */   }
/*      */ 
/*      */   public EncryptedKey loadEncryptedKey(Element paramElement)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1204 */     return loadEncryptedKey(paramElement.getOwnerDocument(), paramElement);
/*      */   }
/*      */ 
/*      */   public EncryptedKey encryptKey(Document paramDocument, Key paramKey)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1220 */     logger.log(Level.FINE, "Encrypting key ...");
/*      */ 
/* 1222 */     if (null == paramKey)
/* 1223 */       logger.log(Level.SEVERE, "Key unexpectedly null...");
/* 1224 */     if (this._cipherMode != 3) {
/* 1225 */       logger.log(Level.FINE, "XMLCipher unexpectedly not in WRAP_MODE...");
/*      */     }
/* 1227 */     if (this._algorithm == null)
/*      */     {
/* 1229 */       throw new XMLEncryptionException("XMLCipher instance without transformation specified");
/*      */     }
/*      */ 
/* 1232 */     this._contextDocument = paramDocument;
/*      */ 
/* 1234 */     byte[] arrayOfByte = null;
/*      */     Cipher localCipher;
/* 1237 */     if (this._contextCipher == null)
/*      */     {
/* 1240 */       String str1 = JCEMapper.translateURItoJCEID(this._algorithm);
/*      */ 
/* 1243 */       logger.log(Level.FINE, "alg = " + str1);
/*      */       try
/*      */       {
/* 1246 */         if (this._requestedJCEProvider == null)
/* 1247 */           localCipher = Cipher.getInstance(str1);
/*      */         else
/* 1249 */           localCipher = Cipher.getInstance(str1, this._requestedJCEProvider);
/*      */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 1251 */         throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
/*      */       } catch (NoSuchProviderException localNoSuchProviderException) {
/* 1253 */         throw new XMLEncryptionException("empty", localNoSuchProviderException);
/*      */       } catch (NoSuchPaddingException localNoSuchPaddingException) {
/* 1255 */         throw new XMLEncryptionException("empty", localNoSuchPaddingException);
/*      */       }
/*      */     } else {
/* 1258 */       localCipher = this._contextCipher;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1265 */       localCipher.init(3, this._key);
/* 1266 */       arrayOfByte = localCipher.wrap(paramKey);
/*      */     } catch (InvalidKeyException localInvalidKeyException) {
/* 1268 */       throw new XMLEncryptionException("empty", localInvalidKeyException);
/*      */     } catch (IllegalBlockSizeException localIllegalBlockSizeException) {
/* 1270 */       throw new XMLEncryptionException("empty", localIllegalBlockSizeException);
/*      */     }
/*      */ 
/* 1273 */     String str2 = Base64.encode(arrayOfByte);
/*      */ 
/* 1275 */     logger.log(Level.FINE, "Encrypted key octets:\n" + str2);
/* 1276 */     logger.log(Level.FINE, "Encrypted key octets length = " + str2.length());
/*      */ 
/* 1279 */     CipherValue localCipherValue = this._ek.getCipherData().getCipherValue();
/* 1280 */     localCipherValue.setValue(str2);
/*      */     try
/*      */     {
/* 1283 */       EncryptionMethod localEncryptionMethod = this._factory.newEncryptionMethod(new URI(this._algorithm).toString());
/*      */ 
/* 1285 */       this._ek.setEncryptionMethod(localEncryptionMethod);
/*      */     } catch (URI.MalformedURIException localMalformedURIException) {
/* 1287 */       throw new XMLEncryptionException("empty", localMalformedURIException);
/*      */     }
/* 1289 */     return this._ek;
/*      */   }
/*      */ 
/*      */   public Key decryptKey(EncryptedKey paramEncryptedKey, String paramString)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1306 */     logger.log(Level.FINE, "Decrypting key from previously loaded EncryptedKey...");
/*      */ 
/* 1308 */     if (this._cipherMode != 4) {
/* 1309 */       logger.log(Level.FINE, "XMLCipher unexpectedly not in UNWRAP_MODE...");
/*      */     }
/* 1311 */     if (paramString == null) {
/* 1312 */       throw new XMLEncryptionException("Cannot decrypt a key without knowing the algorithm");
/*      */     }
/*      */ 
/* 1315 */     if (this._key == null)
/*      */     {
/* 1317 */       logger.log(Level.FINE, "Trying to find a KEK via key resolvers");
/*      */ 
/* 1319 */       localObject1 = paramEncryptedKey.getKeyInfo();
/* 1320 */       if (localObject1 != null)
/*      */         try {
/* 1322 */           this._key = ((KeyInfo)localObject1).getSecretKey();
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*      */         }
/* 1327 */       if (this._key == null) {
/* 1328 */         logger.log(Level.SEVERE, "XMLCipher::decryptKey called without a KEK and cannot resolve");
/* 1329 */         throw new XMLEncryptionException("Unable to decrypt without a KEK");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1334 */     Object localObject1 = new XMLCipherInput(paramEncryptedKey);
/* 1335 */     byte[] arrayOfByte = ((XMLCipherInput)localObject1).getBytes();
/*      */ 
/* 1337 */     String str = JCEMapper.getJCEKeyAlgorithmFromURI(paramString);
/*      */     Object localObject2;
/*      */     Cipher localCipher;
/* 1341 */     if (this._contextCipher == null)
/*      */     {
/* 1344 */       localObject2 = JCEMapper.translateURItoJCEID(paramEncryptedKey.getEncryptionMethod().getAlgorithm());
/*      */ 
/* 1348 */       logger.log(Level.FINE, "JCE Algorithm = " + (String)localObject2);
/*      */       try
/*      */       {
/* 1351 */         if (this._requestedJCEProvider == null)
/* 1352 */           localCipher = Cipher.getInstance((String)localObject2);
/*      */         else
/* 1354 */           localCipher = Cipher.getInstance((String)localObject2, this._requestedJCEProvider);
/*      */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException1) {
/* 1356 */         throw new XMLEncryptionException("empty", localNoSuchAlgorithmException1);
/*      */       } catch (NoSuchProviderException localNoSuchProviderException) {
/* 1358 */         throw new XMLEncryptionException("empty", localNoSuchProviderException);
/*      */       } catch (NoSuchPaddingException localNoSuchPaddingException) {
/* 1360 */         throw new XMLEncryptionException("empty", localNoSuchPaddingException);
/*      */       }
/*      */     } else {
/* 1363 */       localCipher = this._contextCipher;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1369 */       localCipher.init(4, this._key);
/* 1370 */       localObject2 = localCipher.unwrap(arrayOfByte, str, 3);
/*      */     }
/*      */     catch (InvalidKeyException localInvalidKeyException) {
/* 1373 */       throw new XMLEncryptionException("empty", localInvalidKeyException);
/*      */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException2) {
/* 1375 */       throw new XMLEncryptionException("empty", localNoSuchAlgorithmException2);
/*      */     }
/*      */ 
/* 1378 */     logger.log(Level.FINE, "Decryption of key type " + paramString + " OK");
/*      */ 
/* 1380 */     return localObject2;
/*      */   }
/*      */ 
/*      */   public Key decryptKey(EncryptedKey paramEncryptedKey)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1399 */     return decryptKey(paramEncryptedKey, this._ed.getEncryptionMethod().getAlgorithm());
/*      */   }
/*      */ 
/*      */   private static void removeContent(Node paramNode)
/*      */   {
/* 1409 */     while (paramNode.hasChildNodes())
/* 1410 */       paramNode.removeChild(paramNode.getFirstChild());
/*      */   }
/*      */ 
/*      */   private Document decryptElement(Element paramElement)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1424 */     logger.log(Level.FINE, "Decrypting element...");
/*      */ 
/* 1426 */     if (this._cipherMode != 2)
/* 1427 */       logger.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE...");
/*      */     String str;
/*      */     try
/*      */     {
/* 1431 */       str = new String(decryptToByteArray(paramElement), "UTF-8");
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1433 */       throw new XMLEncryptionException("empty", localUnsupportedEncodingException);
/*      */     }
/*      */ 
/* 1437 */     logger.log(Level.FINE, "Decrypted octets:\n" + str);
/*      */ 
/* 1439 */     Node localNode = paramElement.getParentNode();
/*      */ 
/* 1441 */     DocumentFragment localDocumentFragment = this._serializer.deserialize(str, localNode);
/*      */ 
/* 1448 */     if ((localNode != null) && (localNode.getNodeType() == 9))
/*      */     {
/* 1452 */       this._contextDocument.removeChild(this._contextDocument.getDocumentElement());
/* 1453 */       this._contextDocument.appendChild(localDocumentFragment);
/*      */     }
/*      */     else {
/* 1456 */       localNode.replaceChild(localDocumentFragment, paramElement);
/*      */     }
/*      */ 
/* 1460 */     return this._contextDocument;
/*      */   }
/*      */ 
/*      */   private Document decryptElementContent(Element paramElement)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1472 */     Element localElement = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
/*      */ 
/* 1476 */     if (null == localElement) {
/* 1477 */       throw new XMLEncryptionException("No EncryptedData child element.");
/*      */     }
/*      */ 
/* 1480 */     return decryptElement(localElement);
/*      */   }
/*      */ 
/*      */   public byte[] decryptToByteArray(Element paramElement)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1498 */     logger.log(Level.FINE, "Decrypting to ByteArray...");
/*      */ 
/* 1500 */     if (this._cipherMode != 2) {
/* 1501 */       logger.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE...");
/*      */     }
/* 1503 */     EncryptedData localEncryptedData = this._factory.newEncryptedData(paramElement);
/*      */ 
/* 1505 */     if (this._key == null)
/*      */     {
/* 1507 */       localObject = localEncryptedData.getKeyInfo();
/*      */ 
/* 1509 */       if (localObject != null) {
/*      */         try
/*      */         {
/* 1512 */           ((KeyInfo)localObject).registerInternalKeyResolver(new EncryptedKeyResolver(localEncryptedData.getEncryptionMethod().getAlgorithm(), this._kek));
/*      */ 
/* 1517 */           this._key = ((KeyInfo)localObject).getSecretKey();
/*      */         }
/*      */         catch (KeyResolverException localKeyResolverException)
/*      */         {
/*      */         }
/*      */       }
/* 1523 */       if (this._key == null) {
/* 1524 */         logger.log(Level.SEVERE, "XMLCipher::decryptElement called without a key and unable to resolve");
/*      */ 
/* 1526 */         throw new XMLEncryptionException("encryption.nokey");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1531 */     Object localObject = new XMLCipherInput(localEncryptedData);
/* 1532 */     byte[] arrayOfByte1 = ((XMLCipherInput)localObject).getBytes();
/*      */ 
/* 1536 */     String str = JCEMapper.translateURItoJCEID(localEncryptedData.getEncryptionMethod().getAlgorithm());
/*      */     Cipher localCipher;
/*      */     try
/*      */     {
/* 1541 */       if (this._requestedJCEProvider == null)
/* 1542 */         localCipher = Cipher.getInstance(str);
/*      */       else
/* 1544 */         localCipher = Cipher.getInstance(str, this._requestedJCEProvider);
/*      */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 1546 */       throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
/*      */     } catch (NoSuchProviderException localNoSuchProviderException) {
/* 1548 */       throw new XMLEncryptionException("empty", localNoSuchProviderException);
/*      */     } catch (NoSuchPaddingException localNoSuchPaddingException) {
/* 1550 */       throw new XMLEncryptionException("empty", localNoSuchPaddingException);
/*      */     }
/*      */ 
/* 1558 */     int i = localCipher.getBlockSize();
/* 1559 */     byte[] arrayOfByte2 = new byte[i];
/*      */ 
/* 1566 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
/* 1567 */     IvParameterSpec localIvParameterSpec = new IvParameterSpec(arrayOfByte2);
/*      */     try
/*      */     {
/* 1570 */       localCipher.init(this._cipherMode, this._key, localIvParameterSpec);
/*      */     } catch (InvalidKeyException localInvalidKeyException) {
/* 1572 */       throw new XMLEncryptionException("empty", localInvalidKeyException);
/*      */     } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {
/* 1574 */       throw new XMLEncryptionException("empty", localInvalidAlgorithmParameterException);
/*      */     }
/*      */ 
/*      */     byte[] arrayOfByte3;
/*      */     try
/*      */     {
/* 1580 */       arrayOfByte3 = localCipher.doFinal(arrayOfByte1, i, arrayOfByte1.length - i);
/*      */     }
/*      */     catch (IllegalBlockSizeException localIllegalBlockSizeException)
/*      */     {
/* 1585 */       throw new XMLEncryptionException("empty", localIllegalBlockSizeException);
/*      */     } catch (BadPaddingException localBadPaddingException) {
/* 1587 */       throw new XMLEncryptionException("empty", localBadPaddingException);
/*      */     }
/*      */ 
/* 1590 */     return arrayOfByte3;
/*      */   }
/*      */ 
/*      */   public EncryptedData createEncryptedData(int paramInt, String paramString)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1634 */     EncryptedData localEncryptedData = null;
/* 1635 */     CipherData localCipherData = null;
/*      */ 
/* 1637 */     switch (paramInt) {
/*      */     case 2:
/* 1639 */       CipherReference localCipherReference = this._factory.newCipherReference(paramString);
/*      */ 
/* 1641 */       localCipherData = this._factory.newCipherData(paramInt);
/* 1642 */       localCipherData.setCipherReference(localCipherReference);
/* 1643 */       localEncryptedData = this._factory.newEncryptedData(localCipherData);
/* 1644 */       break;
/*      */     case 1:
/* 1646 */       CipherValue localCipherValue = this._factory.newCipherValue(paramString);
/* 1647 */       localCipherData = this._factory.newCipherData(paramInt);
/* 1648 */       localCipherData.setCipherValue(localCipherValue);
/* 1649 */       localEncryptedData = this._factory.newEncryptedData(localCipherData);
/*      */     }
/*      */ 
/* 1652 */     return localEncryptedData;
/*      */   }
/*      */ 
/*      */   public EncryptedKey createEncryptedKey(int paramInt, String paramString)
/*      */     throws XMLEncryptionException
/*      */   {
/* 1692 */     EncryptedKey localEncryptedKey = null;
/* 1693 */     CipherData localCipherData = null;
/*      */ 
/* 1695 */     switch (paramInt) {
/*      */     case 2:
/* 1697 */       CipherReference localCipherReference = this._factory.newCipherReference(paramString);
/*      */ 
/* 1699 */       localCipherData = this._factory.newCipherData(paramInt);
/* 1700 */       localCipherData.setCipherReference(localCipherReference);
/* 1701 */       localEncryptedKey = this._factory.newEncryptedKey(localCipherData);
/* 1702 */       break;
/*      */     case 1:
/* 1704 */       CipherValue localCipherValue = this._factory.newCipherValue(paramString);
/* 1705 */       localCipherData = this._factory.newCipherData(paramInt);
/* 1706 */       localCipherData.setCipherValue(localCipherValue);
/* 1707 */       localEncryptedKey = this._factory.newEncryptedKey(localCipherData);
/*      */     }
/*      */ 
/* 1710 */     return localEncryptedKey;
/*      */   }
/*      */ 
/*      */   public AgreementMethod createAgreementMethod(String paramString)
/*      */   {
/* 1721 */     return this._factory.newAgreementMethod(paramString);
/*      */   }
/*      */ 
/*      */   public CipherData createCipherData(int paramInt)
/*      */   {
/* 1733 */     return this._factory.newCipherData(paramInt);
/*      */   }
/*      */ 
/*      */   public CipherReference createCipherReference(String paramString)
/*      */   {
/* 1744 */     return this._factory.newCipherReference(paramString);
/*      */   }
/*      */ 
/*      */   public CipherValue createCipherValue(String paramString)
/*      */   {
/* 1755 */     return this._factory.newCipherValue(paramString);
/*      */   }
/*      */ 
/*      */   public EncryptionMethod createEncryptionMethod(String paramString)
/*      */   {
/* 1765 */     return this._factory.newEncryptionMethod(paramString);
/*      */   }
/*      */ 
/*      */   public EncryptionProperties createEncryptionProperties()
/*      */   {
/* 1773 */     return this._factory.newEncryptionProperties();
/*      */   }
/*      */ 
/*      */   public EncryptionProperty createEncryptionProperty()
/*      */   {
/* 1781 */     return this._factory.newEncryptionProperty();
/*      */   }
/*      */ 
/*      */   public ReferenceList createReferenceList(int paramInt)
/*      */   {
/* 1790 */     return this._factory.newReferenceList(paramInt);
/*      */   }
/*      */ 
/*      */   public Transforms createTransforms()
/*      */   {
/* 1803 */     return this._factory.newTransforms();
/*      */   }
/*      */ 
/*      */   public Transforms createTransforms(Document paramDocument)
/*      */   {
/* 1817 */     return this._factory.newTransforms(paramDocument);
/*      */   }
/*      */ 
/*      */   private class Factory
/*      */   {
/*      */     private Factory()
/*      */     {
/*      */     }
/*      */ 
/*      */     AgreementMethod newAgreementMethod(String paramString)
/*      */     {
/* 2025 */       return new AgreementMethodImpl(paramString);
/*      */     }
/*      */ 
/*      */     CipherData newCipherData(int paramInt)
/*      */     {
/* 2034 */       return new CipherDataImpl(paramInt);
/*      */     }
/*      */ 
/*      */     CipherReference newCipherReference(String paramString)
/*      */     {
/* 2043 */       return new CipherReferenceImpl(paramString);
/*      */     }
/*      */ 
/*      */     CipherValue newCipherValue(String paramString)
/*      */     {
/* 2052 */       return new CipherValueImpl(paramString);
/*      */     }
/*      */ 
/*      */     EncryptedData newEncryptedData(CipherData paramCipherData)
/*      */     {
/* 2068 */       return new EncryptedDataImpl(paramCipherData);
/*      */     }
/*      */ 
/*      */     EncryptedKey newEncryptedKey(CipherData paramCipherData)
/*      */     {
/* 2077 */       return new EncryptedKeyImpl(paramCipherData);
/*      */     }
/*      */ 
/*      */     EncryptionMethod newEncryptionMethod(String paramString)
/*      */     {
/* 2086 */       return new EncryptionMethodImpl(paramString);
/*      */     }
/*      */ 
/*      */     EncryptionProperties newEncryptionProperties()
/*      */     {
/* 2094 */       return new EncryptionPropertiesImpl();
/*      */     }
/*      */ 
/*      */     EncryptionProperty newEncryptionProperty()
/*      */     {
/* 2102 */       return new EncryptionPropertyImpl();
/*      */     }
/*      */ 
/*      */     ReferenceList newReferenceList(int paramInt)
/*      */     {
/* 2111 */       return new ReferenceListImpl(paramInt);
/*      */     }
/*      */ 
/*      */     Transforms newTransforms()
/*      */     {
/* 2119 */       return new TransformsImpl();
/*      */     }
/*      */ 
/*      */     Transforms newTransforms(Document paramDocument)
/*      */     {
/* 2128 */       return new TransformsImpl(paramDocument);
/*      */     }
/*      */ 
/*      */     AgreementMethod newAgreementMethod(Element paramElement)
/*      */       throws XMLEncryptionException
/*      */     {
/* 2150 */       if (null == paramElement) {
/* 2151 */         throw new NullPointerException("element is null");
/*      */       }
/*      */ 
/* 2154 */       String str = paramElement.getAttributeNS(null, "Algorithm");
/*      */ 
/* 2156 */       AgreementMethod localAgreementMethod = newAgreementMethod(str);
/*      */ 
/* 2158 */       Element localElement1 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KA-Nonce").item(0);
/*      */ 
/* 2161 */       if (null != localElement1) {
/* 2162 */         localAgreementMethod.setKANonce(localElement1.getNodeValue().getBytes());
/*      */       }
/*      */ 
/* 2170 */       Element localElement2 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OriginatorKeyInfo").item(0);
/*      */ 
/* 2174 */       if (null != localElement2) {
/*      */         try {
/* 2176 */           localAgreementMethod.setOriginatorKeyInfo(new KeyInfo(localElement2, null));
/*      */         }
/*      */         catch (XMLSecurityException localXMLSecurityException1) {
/* 2179 */           throw new XMLEncryptionException("empty", localXMLSecurityException1);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2185 */       Element localElement3 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "RecipientKeyInfo").item(0);
/*      */ 
/* 2189 */       if (null != localElement3) {
/*      */         try {
/* 2191 */           localAgreementMethod.setRecipientKeyInfo(new KeyInfo(localElement3, null));
/*      */         }
/*      */         catch (XMLSecurityException localXMLSecurityException2) {
/* 2194 */           throw new XMLEncryptionException("empty", localXMLSecurityException2);
/*      */         }
/*      */       }
/*      */ 
/* 2198 */       return localAgreementMethod;
/*      */     }
/*      */ 
/*      */     CipherData newCipherData(Element paramElement)
/*      */       throws XMLEncryptionException
/*      */     {
/* 2216 */       if (null == paramElement) {
/* 2217 */         throw new NullPointerException("element is null");
/*      */       }
/*      */ 
/* 2220 */       int i = 0;
/* 2221 */       Element localElement = null;
/* 2222 */       if (paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").getLength() > 0)
/*      */       {
/* 2225 */         i = 1;
/* 2226 */         localElement = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").item(0);
/*      */       }
/* 2229 */       else if (paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").getLength() > 0)
/*      */       {
/* 2232 */         i = 2;
/* 2233 */         localElement = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").item(0);
/*      */       }
/*      */ 
/* 2238 */       CipherData localCipherData = newCipherData(i);
/* 2239 */       if (i == 1)
/* 2240 */         localCipherData.setCipherValue(newCipherValue(localElement));
/* 2241 */       else if (i == 2) {
/* 2242 */         localCipherData.setCipherReference(newCipherReference(localElement));
/*      */       }
/*      */ 
/* 2245 */       return localCipherData;
/*      */     }
/*      */ 
/*      */     CipherReference newCipherReference(Element paramElement)
/*      */       throws XMLEncryptionException
/*      */     {
/* 2264 */       Attr localAttr = paramElement.getAttributeNodeNS(null, "URI");
/*      */ 
/* 2266 */       CipherReferenceImpl localCipherReferenceImpl = new CipherReferenceImpl(localAttr);
/*      */ 
/* 2270 */       NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "Transforms");
/*      */ 
/* 2273 */       Element localElement = (Element)localNodeList.item(0);
/*      */ 
/* 2276 */       if (localElement != null) {
/* 2277 */         XMLCipher.logger.log(Level.FINE, "Creating a DSIG based Transforms element");
/*      */         try {
/* 2279 */           localCipherReferenceImpl.setTransforms(new TransformsImpl(localElement));
/*      */         }
/*      */         catch (XMLSignatureException localXMLSignatureException) {
/* 2282 */           throw new XMLEncryptionException("empty", localXMLSignatureException);
/*      */         } catch (InvalidTransformException localInvalidTransformException) {
/* 2284 */           throw new XMLEncryptionException("empty", localInvalidTransformException);
/*      */         } catch (XMLSecurityException localXMLSecurityException) {
/* 2286 */           throw new XMLEncryptionException("empty", localXMLSecurityException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2291 */       return localCipherReferenceImpl;
/*      */     }
/*      */ 
/*      */     CipherValue newCipherValue(Element paramElement)
/*      */     {
/* 2300 */       String str = XMLUtils.getFullTextChildrenFromElement(paramElement);
/*      */ 
/* 2302 */       CipherValue localCipherValue = newCipherValue(str);
/*      */ 
/* 2304 */       return localCipherValue;
/*      */     }
/*      */ 
/*      */     EncryptedData newEncryptedData(Element paramElement)
/*      */       throws XMLEncryptionException
/*      */     {
/* 2334 */       EncryptedData localEncryptedData = null;
/*      */ 
/* 2336 */       NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
/*      */ 
/* 2343 */       Element localElement1 = (Element)localNodeList.item(localNodeList.getLength() - 1);
/*      */ 
/* 2346 */       CipherData localCipherData = newCipherData(localElement1);
/*      */ 
/* 2348 */       localEncryptedData = newEncryptedData(localCipherData);
/*      */ 
/* 2350 */       localEncryptedData.setId(paramElement.getAttributeNS(null, "Id"));
/*      */ 
/* 2352 */       localEncryptedData.setType(paramElement.getAttributeNS(null, "Type"));
/*      */ 
/* 2354 */       localEncryptedData.setMimeType(paramElement.getAttributeNS(null, "MimeType"));
/*      */ 
/* 2356 */       localEncryptedData.setEncoding(paramElement.getAttributeNS(null, "Encoding"));
/*      */ 
/* 2359 */       Element localElement2 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
/*      */ 
/* 2363 */       if (null != localElement2) {
/* 2364 */         localEncryptedData.setEncryptionMethod(newEncryptionMethod(localElement2));
/*      */       }
/*      */ 
/* 2371 */       Element localElement3 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
/*      */ 
/* 2374 */       if (null != localElement3) {
/*      */         try {
/* 2376 */           localEncryptedData.setKeyInfo(new KeyInfo(localElement3, null));
/*      */         } catch (XMLSecurityException localXMLSecurityException) {
/* 2378 */           throw new XMLEncryptionException("Error loading Key Info", localXMLSecurityException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2384 */       Element localElement4 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
/*      */ 
/* 2388 */       if (null != localElement4) {
/* 2389 */         localEncryptedData.setEncryptionProperties(newEncryptionProperties(localElement4));
/*      */       }
/*      */ 
/* 2393 */       return localEncryptedData;
/*      */     }
/*      */ 
/*      */     EncryptedKey newEncryptedKey(Element paramElement)
/*      */       throws XMLEncryptionException
/*      */     {
/* 2429 */       EncryptedKey localEncryptedKey = null;
/* 2430 */       NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
/*      */ 
/* 2433 */       Element localElement1 = (Element)localNodeList.item(localNodeList.getLength() - 1);
/*      */ 
/* 2436 */       CipherData localCipherData = newCipherData(localElement1);
/* 2437 */       localEncryptedKey = newEncryptedKey(localCipherData);
/*      */ 
/* 2439 */       localEncryptedKey.setId(paramElement.getAttributeNS(null, "Id"));
/*      */ 
/* 2441 */       localEncryptedKey.setType(paramElement.getAttributeNS(null, "Type"));
/*      */ 
/* 2443 */       localEncryptedKey.setMimeType(paramElement.getAttributeNS(null, "MimeType"));
/*      */ 
/* 2445 */       localEncryptedKey.setEncoding(paramElement.getAttributeNS(null, "Encoding"));
/*      */ 
/* 2447 */       localEncryptedKey.setRecipient(paramElement.getAttributeNS(null, "Recipient"));
/*      */ 
/* 2450 */       Element localElement2 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
/*      */ 
/* 2454 */       if (null != localElement2) {
/* 2455 */         localEncryptedKey.setEncryptionMethod(newEncryptionMethod(localElement2));
/*      */       }
/*      */ 
/* 2459 */       Element localElement3 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
/*      */ 
/* 2462 */       if (null != localElement3) {
/*      */         try {
/* 2464 */           localEncryptedKey.setKeyInfo(new KeyInfo(localElement3, null));
/*      */         } catch (XMLSecurityException localXMLSecurityException) {
/* 2466 */           throw new XMLEncryptionException("Error loading Key Info", localXMLSecurityException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2472 */       Element localElement4 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
/*      */ 
/* 2476 */       if (null != localElement4) {
/* 2477 */         localEncryptedKey.setEncryptionProperties(newEncryptionProperties(localElement4));
/*      */       }
/*      */ 
/* 2481 */       Element localElement5 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "ReferenceList").item(0);
/*      */ 
/* 2485 */       if (null != localElement5) {
/* 2486 */         localEncryptedKey.setReferenceList(newReferenceList(localElement5));
/*      */       }
/*      */ 
/* 2489 */       Element localElement6 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName").item(0);
/*      */ 
/* 2493 */       if (null != localElement6) {
/* 2494 */         localEncryptedKey.setCarriedName(localElement6.getFirstChild().getNodeValue());
/*      */       }
/*      */ 
/* 2498 */       return localEncryptedKey;
/*      */     }
/*      */ 
/*      */     EncryptionMethod newEncryptionMethod(Element paramElement)
/*      */     {
/* 2515 */       String str = paramElement.getAttributeNS(null, "Algorithm");
/*      */ 
/* 2517 */       EncryptionMethod localEncryptionMethod = newEncryptionMethod(str);
/*      */ 
/* 2519 */       Element localElement1 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeySize").item(0);
/*      */ 
/* 2523 */       if (null != localElement1) {
/* 2524 */         localEncryptionMethod.setKeySize(Integer.valueOf(localElement1.getFirstChild().getNodeValue()).intValue());
/*      */       }
/*      */ 
/* 2529 */       Element localElement2 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OAEPparams").item(0);
/*      */ 
/* 2533 */       if (null != localElement2) {
/* 2534 */         localEncryptionMethod.setOAEPparams(localElement2.getNodeValue().getBytes());
/*      */       }
/*      */ 
/* 2541 */       return localEncryptionMethod;
/*      */     }
/*      */ 
/*      */     EncryptionProperties newEncryptionProperties(Element paramElement)
/*      */     {
/* 2557 */       EncryptionProperties localEncryptionProperties = newEncryptionProperties();
/*      */ 
/* 2559 */       localEncryptionProperties.setId(paramElement.getAttributeNS(null, "Id"));
/*      */ 
/* 2562 */       NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");
/*      */ 
/* 2566 */       for (int i = 0; i < localNodeList.getLength(); i++) {
/* 2567 */         Node localNode = localNodeList.item(i);
/* 2568 */         if (null != localNode) {
/* 2569 */           localEncryptionProperties.addEncryptionProperty(newEncryptionProperty((Element)localNode));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2574 */       return localEncryptionProperties;
/*      */     }
/*      */ 
/*      */     EncryptionProperty newEncryptionProperty(Element paramElement)
/*      */     {
/* 2592 */       EncryptionProperty localEncryptionProperty = newEncryptionProperty();
/*      */ 
/* 2594 */       localEncryptionProperty.setTarget(paramElement.getAttributeNS(null, "Target"));
/*      */ 
/* 2596 */       localEncryptionProperty.setId(paramElement.getAttributeNS(null, "Id"));
/*      */ 
/* 2604 */       return localEncryptionProperty;
/*      */     }
/*      */ 
/*      */     ReferenceList newReferenceList(Element paramElement)
/*      */     {
/* 2621 */       int i = 0;
/* 2622 */       if (null != paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference").item(0))
/*      */       {
/* 2625 */         i = 1;
/* 2626 */       } else if (null != paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference").item(0))
/*      */       {
/* 2629 */         i = 2;
/*      */       }
/*      */ 
/* 2634 */       ReferenceListImpl localReferenceListImpl = new ReferenceListImpl(i);
/* 2635 */       NodeList localNodeList = null;
/*      */       int j;
/*      */       String str;
/* 2636 */       switch (i) {
/*      */       case 1:
/* 2638 */         localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference");
/*      */ 
/* 2641 */         for (j = 0; j < localNodeList.getLength(); j++) {
/* 2642 */           str = ((Element)localNodeList.item(j)).getAttribute("URI");
/* 2643 */           localReferenceListImpl.add(localReferenceListImpl.newDataReference(str));
/*      */         }
/* 2645 */         break;
/*      */       case 2:
/* 2647 */         localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference");
/*      */ 
/* 2650 */         for (j = 0; j < localNodeList.getLength(); j++) {
/* 2651 */           str = ((Element)localNodeList.item(j)).getAttribute("URI");
/* 2652 */           localReferenceListImpl.add(localReferenceListImpl.newKeyReference(str));
/*      */         }
/*      */       }
/*      */ 
/* 2656 */       return localReferenceListImpl;
/*      */     }
/*      */ 
/*      */     Transforms newTransforms(Element paramElement)
/*      */     {
/* 2665 */       return null;
/*      */     }
/*      */ 
/*      */     Element toElement(AgreementMethod paramAgreementMethod)
/*      */     {
/* 2674 */       return ((AgreementMethodImpl)paramAgreementMethod).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(CipherData paramCipherData)
/*      */     {
/* 2683 */       return ((CipherDataImpl)paramCipherData).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(CipherReference paramCipherReference)
/*      */     {
/* 2692 */       return ((CipherReferenceImpl)paramCipherReference).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(CipherValue paramCipherValue)
/*      */     {
/* 2701 */       return ((CipherValueImpl)paramCipherValue).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(EncryptedData paramEncryptedData)
/*      */     {
/* 2710 */       return ((EncryptedDataImpl)paramEncryptedData).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(EncryptedKey paramEncryptedKey)
/*      */     {
/* 2719 */       return ((EncryptedKeyImpl)paramEncryptedKey).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(EncryptionMethod paramEncryptionMethod)
/*      */     {
/* 2728 */       return ((EncryptionMethodImpl)paramEncryptionMethod).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(EncryptionProperties paramEncryptionProperties)
/*      */     {
/* 2737 */       return ((EncryptionPropertiesImpl)paramEncryptionProperties).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(EncryptionProperty paramEncryptionProperty)
/*      */     {
/* 2746 */       return ((EncryptionPropertyImpl)paramEncryptionProperty).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(ReferenceList paramReferenceList) {
/* 2750 */       return ((ReferenceListImpl)paramReferenceList).toElement();
/*      */     }
/*      */ 
/*      */     Element toElement(Transforms paramTransforms)
/*      */     {
/* 2759 */       return ((TransformsImpl)paramTransforms).toElement();
/*      */     }
/*      */ 
/*      */     private class AgreementMethodImpl
/*      */       implements AgreementMethod
/*      */     {
/* 2774 */       private byte[] kaNonce = null;
/* 2775 */       private List agreementMethodInformation = null;
/* 2776 */       private KeyInfo originatorKeyInfo = null;
/* 2777 */       private KeyInfo recipientKeyInfo = null;
/* 2778 */       private String algorithmURI = null;
/*      */ 
/*      */       public AgreementMethodImpl(String arg2)
/*      */       {
/* 2784 */         this.agreementMethodInformation = new LinkedList();
/* 2785 */         URI localURI = null;
/*      */         try
/*      */         {
/*      */           String str;
/* 2787 */           localURI = new URI(str);
/*      */         }
/*      */         catch (URI.MalformedURIException localMalformedURIException) {
/*      */         }
/* 2791 */         this.algorithmURI = localURI.toString();
/*      */       }
/*      */ 
/*      */       public byte[] getKANonce()
/*      */       {
/* 2796 */         return this.kaNonce;
/*      */       }
/*      */ 
/*      */       public void setKANonce(byte[] paramArrayOfByte)
/*      */       {
/* 2801 */         this.kaNonce = paramArrayOfByte;
/*      */       }
/*      */ 
/*      */       public Iterator getAgreementMethodInformation()
/*      */       {
/* 2806 */         return this.agreementMethodInformation.iterator();
/*      */       }
/*      */ 
/*      */       public void addAgreementMethodInformation(Element paramElement)
/*      */       {
/* 2811 */         this.agreementMethodInformation.add(paramElement);
/*      */       }
/*      */ 
/*      */       public void revoveAgreementMethodInformation(Element paramElement)
/*      */       {
/* 2816 */         this.agreementMethodInformation.remove(paramElement);
/*      */       }
/*      */ 
/*      */       public KeyInfo getOriginatorKeyInfo()
/*      */       {
/* 2821 */         return this.originatorKeyInfo;
/*      */       }
/*      */ 
/*      */       public void setOriginatorKeyInfo(KeyInfo paramKeyInfo)
/*      */       {
/* 2826 */         this.originatorKeyInfo = paramKeyInfo;
/*      */       }
/*      */ 
/*      */       public KeyInfo getRecipientKeyInfo()
/*      */       {
/* 2831 */         return this.recipientKeyInfo;
/*      */       }
/*      */ 
/*      */       public void setRecipientKeyInfo(KeyInfo paramKeyInfo)
/*      */       {
/* 2836 */         this.recipientKeyInfo = paramKeyInfo;
/*      */       }
/*      */ 
/*      */       public String getAlgorithm()
/*      */       {
/* 2841 */         return this.algorithmURI;
/*      */       }
/*      */ 
/*      */       public void setAlgorithm(String paramString)
/*      */       {
/* 2846 */         URI localURI = null;
/*      */         try {
/* 2848 */           localURI = new URI(paramString);
/*      */         }
/*      */         catch (URI.MalformedURIException localMalformedURIException) {
/*      */         }
/* 2852 */         this.algorithmURI = localURI.toString();
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 2867 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "AgreementMethod");
/*      */ 
/* 2871 */         localElement.setAttributeNS(null, "Algorithm", this.algorithmURI);
/*      */ 
/* 2873 */         if (null != this.kaNonce) {
/* 2874 */           localElement.appendChild(ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "KA-Nonce")).appendChild(XMLCipher.this._contextDocument.createTextNode(new String(this.kaNonce)));
/*      */         }
/*      */ 
/* 2881 */         if (!this.agreementMethodInformation.isEmpty()) {
/* 2882 */           Iterator localIterator = this.agreementMethodInformation.iterator();
/* 2883 */           while (localIterator.hasNext()) {
/* 2884 */             localElement.appendChild((Element)localIterator.next());
/*      */           }
/*      */         }
/* 2887 */         if (null != this.originatorKeyInfo) {
/* 2888 */           localElement.appendChild(this.originatorKeyInfo.getElement());
/*      */         }
/* 2890 */         if (null != this.recipientKeyInfo) {
/* 2891 */           localElement.appendChild(this.recipientKeyInfo.getElement());
/*      */         }
/*      */ 
/* 2894 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class CipherDataImpl
/*      */       implements CipherData
/*      */     {
/*      */       private static final String valueMessage = "Data type is reference type.";
/*      */       private static final String referenceMessage = "Data type is value type.";
/* 2910 */       private CipherValue cipherValue = null;
/* 2911 */       private CipherReference cipherReference = null;
/* 2912 */       private int cipherType = -2147483648;
/*      */ 
/*      */       public CipherDataImpl(int arg2)
/*      */       {
/*      */         int i;
/* 2918 */         this.cipherType = i;
/*      */       }
/*      */ 
/*      */       public CipherValue getCipherValue()
/*      */       {
/* 2923 */         return this.cipherValue;
/*      */       }
/*      */ 
/*      */       public void setCipherValue(CipherValue paramCipherValue)
/*      */         throws XMLEncryptionException
/*      */       {
/* 2930 */         if (this.cipherType == 2) {
/* 2931 */           throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is reference type."));
/*      */         }
/*      */ 
/* 2935 */         this.cipherValue = paramCipherValue;
/*      */       }
/*      */ 
/*      */       public CipherReference getCipherReference()
/*      */       {
/* 2940 */         return this.cipherReference;
/*      */       }
/*      */ 
/*      */       public void setCipherReference(CipherReference paramCipherReference)
/*      */         throws XMLEncryptionException
/*      */       {
/* 2946 */         if (this.cipherType == 1) {
/* 2947 */           throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is value type."));
/*      */         }
/*      */ 
/* 2951 */         this.cipherReference = paramCipherReference;
/*      */       }
/*      */ 
/*      */       public int getDataType()
/*      */       {
/* 2956 */         return this.cipherType;
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 2967 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CipherData");
/*      */ 
/* 2971 */         if (this.cipherType == 1) {
/* 2972 */           localElement.appendChild(((XMLCipher.Factory.CipherValueImpl)this.cipherValue).toElement());
/*      */         }
/* 2974 */         else if (this.cipherType == 2) {
/* 2975 */           localElement.appendChild(((XMLCipher.Factory.CipherReferenceImpl)this.cipherReference).toElement());
/*      */         }
/*      */ 
/* 2981 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class CipherReferenceImpl
/*      */       implements CipherReference
/*      */     {
/* 2993 */       private String referenceURI = null;
/* 2994 */       private Transforms referenceTransforms = null;
/* 2995 */       private Attr referenceNode = null;
/*      */ 
/*      */       public CipherReferenceImpl(String arg2)
/*      */       {
/*      */         Object localObject;
/* 3002 */         this.referenceURI = localObject;
/* 3003 */         this.referenceNode = null;
/*      */       }
/*      */ 
/*      */       public CipherReferenceImpl(Attr arg2)
/*      */       {
/*      */         Object localObject;
/* 3010 */         this.referenceURI = localObject.getNodeValue();
/* 3011 */         this.referenceNode = localObject;
/*      */       }
/*      */ 
/*      */       public String getURI()
/*      */       {
/* 3016 */         return this.referenceURI;
/*      */       }
/*      */ 
/*      */       public Attr getURIAsAttr()
/*      */       {
/* 3021 */         return this.referenceNode;
/*      */       }
/*      */ 
/*      */       public Transforms getTransforms()
/*      */       {
/* 3026 */         return this.referenceTransforms;
/*      */       }
/*      */ 
/*      */       public void setTransforms(Transforms paramTransforms)
/*      */       {
/* 3031 */         this.referenceTransforms = paramTransforms;
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 3042 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CipherReference");
/*      */ 
/* 3046 */         localElement.setAttributeNS(null, "URI", this.referenceURI);
/*      */ 
/* 3048 */         if (null != this.referenceTransforms) {
/* 3049 */           localElement.appendChild(((XMLCipher.Factory.TransformsImpl)this.referenceTransforms).toElement());
/*      */         }
/*      */ 
/* 3053 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class CipherValueImpl implements CipherValue {
/* 3058 */       private String cipherValue = null;
/*      */ 
/*      */       public CipherValueImpl(String arg2)
/*      */       {
/*      */         Object localObject;
/* 3069 */         this.cipherValue = localObject;
/*      */       }
/*      */ 
/*      */       public String getValue()
/*      */       {
/* 3074 */         return this.cipherValue;
/*      */       }
/*      */ 
/*      */       public void setValue(String paramString)
/*      */       {
/* 3084 */         this.cipherValue = paramString;
/*      */       }
/*      */ 
/*      */       Element toElement() {
/* 3088 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CipherValue");
/*      */ 
/* 3091 */         localElement.appendChild(XMLCipher.this._contextDocument.createTextNode(this.cipherValue));
/*      */ 
/* 3094 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class EncryptedDataImpl extends XMLCipher.Factory.EncryptedTypeImpl
/*      */       implements EncryptedData
/*      */     {
/*      */       public EncryptedDataImpl(CipherData arg2)
/*      */       {
/* 3123 */         super(localCipherData);
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 3146 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
/*      */ 
/* 3150 */         if (null != super.getId()) {
/* 3151 */           localElement.setAttributeNS(null, "Id", super.getId());
/*      */         }
/*      */ 
/* 3154 */         if (null != super.getType()) {
/* 3155 */           localElement.setAttributeNS(null, "Type", super.getType());
/*      */         }
/*      */ 
/* 3158 */         if (null != super.getMimeType()) {
/* 3159 */           localElement.setAttributeNS(null, "MimeType", super.getMimeType());
/*      */         }
/*      */ 
/* 3163 */         if (null != super.getEncoding()) {
/* 3164 */           localElement.setAttributeNS(null, "Encoding", super.getEncoding());
/*      */         }
/*      */ 
/* 3168 */         if (null != super.getEncryptionMethod()) {
/* 3169 */           localElement.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
/*      */         }
/*      */ 
/* 3172 */         if (null != super.getKeyInfo()) {
/* 3173 */           localElement.appendChild(super.getKeyInfo().getElement());
/*      */         }
/*      */ 
/* 3176 */         localElement.appendChild(((XMLCipher.Factory.CipherDataImpl)super.getCipherData()).toElement());
/*      */ 
/* 3178 */         if (null != super.getEncryptionProperties()) {
/* 3179 */           localElement.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
/*      */         }
/*      */ 
/* 3183 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class EncryptedKeyImpl extends XMLCipher.Factory.EncryptedTypeImpl
/*      */       implements EncryptedKey
/*      */     {
/* 3214 */       private String keyRecipient = null;
/* 3215 */       private ReferenceList referenceList = null;
/* 3216 */       private String carriedName = null;
/*      */ 
/*      */       public EncryptedKeyImpl(CipherData arg2)
/*      */       {
/* 3222 */         super(localCipherData);
/*      */       }
/*      */ 
/*      */       public String getRecipient()
/*      */       {
/* 3227 */         return this.keyRecipient;
/*      */       }
/*      */ 
/*      */       public void setRecipient(String paramString)
/*      */       {
/* 3232 */         this.keyRecipient = paramString;
/*      */       }
/*      */ 
/*      */       public ReferenceList getReferenceList()
/*      */       {
/* 3237 */         return this.referenceList;
/*      */       }
/*      */ 
/*      */       public void setReferenceList(ReferenceList paramReferenceList)
/*      */       {
/* 3242 */         this.referenceList = paramReferenceList;
/*      */       }
/*      */ 
/*      */       public String getCarriedName()
/*      */       {
/* 3247 */         return this.carriedName;
/*      */       }
/*      */ 
/*      */       public void setCarriedName(String paramString)
/*      */       {
/* 3252 */         this.carriedName = paramString;
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 3281 */         Element localElement1 = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedKey");
/*      */ 
/* 3285 */         if (null != super.getId()) {
/* 3286 */           localElement1.setAttributeNS(null, "Id", super.getId());
/*      */         }
/*      */ 
/* 3289 */         if (null != super.getType()) {
/* 3290 */           localElement1.setAttributeNS(null, "Type", super.getType());
/*      */         }
/*      */ 
/* 3293 */         if (null != super.getMimeType()) {
/* 3294 */           localElement1.setAttributeNS(null, "MimeType", super.getMimeType());
/*      */         }
/*      */ 
/* 3297 */         if (null != super.getEncoding()) {
/* 3298 */           localElement1.setAttributeNS(null, "Encoding", super.getEncoding());
/*      */         }
/*      */ 
/* 3301 */         if (null != getRecipient()) {
/* 3302 */           localElement1.setAttributeNS(null, "Recipient", getRecipient());
/*      */         }
/*      */ 
/* 3305 */         if (null != super.getEncryptionMethod()) {
/* 3306 */           localElement1.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
/*      */         }
/*      */ 
/* 3309 */         if (null != super.getKeyInfo()) {
/* 3310 */           localElement1.appendChild(super.getKeyInfo().getElement());
/*      */         }
/* 3312 */         localElement1.appendChild(((XMLCipher.Factory.CipherDataImpl)super.getCipherData()).toElement());
/*      */ 
/* 3314 */         if (null != super.getEncryptionProperties()) {
/* 3315 */           localElement1.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
/*      */         }
/*      */ 
/* 3318 */         if ((this.referenceList != null) && (!this.referenceList.isEmpty())) {
/* 3319 */           localElement1.appendChild(((XMLCipher.Factory.ReferenceListImpl)getReferenceList()).toElement());
/*      */         }
/*      */ 
/* 3322 */         if (null != this.carriedName) {
/* 3323 */           Element localElement2 = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName");
/*      */ 
/* 3327 */           Text localText = XMLCipher.this._contextDocument.createTextNode(this.carriedName);
/* 3328 */           localElement2.appendChild(localText);
/* 3329 */           localElement1.appendChild(localElement2);
/*      */         }
/*      */ 
/* 3332 */         return localElement1;
/*      */       }
/*      */     }
/*      */ 
/*      */     private abstract class EncryptedTypeImpl {
/* 3337 */       private String id = null;
/* 3338 */       private String type = null;
/* 3339 */       private String mimeType = null;
/* 3340 */       private String encoding = null;
/* 3341 */       private EncryptionMethod encryptionMethod = null;
/* 3342 */       private KeyInfo keyInfo = null;
/* 3343 */       private CipherData cipherData = null;
/* 3344 */       private EncryptionProperties encryptionProperties = null;
/*      */ 
/*      */       protected EncryptedTypeImpl(CipherData arg2)
/*      */       {
/*      */         Object localObject;
/* 3347 */         this.cipherData = localObject;
/*      */       }
/*      */ 
/*      */       public String getId()
/*      */       {
/* 3354 */         return this.id;
/*      */       }
/*      */ 
/*      */       public void setId(String paramString)
/*      */       {
/* 3361 */         this.id = paramString;
/*      */       }
/*      */ 
/*      */       public String getType()
/*      */       {
/* 3368 */         return this.type;
/*      */       }
/*      */ 
/*      */       public void setType(String paramString)
/*      */       {
/* 3375 */         if ((paramString == null) || (paramString.length() == 0)) {
/* 3376 */           this.type = null;
/*      */         } else {
/* 3378 */           URI localURI = null;
/*      */           try {
/* 3380 */             localURI = new URI(paramString);
/*      */           }
/*      */           catch (URI.MalformedURIException localMalformedURIException) {
/*      */           }
/* 3384 */           this.type = localURI.toString();
/*      */         }
/*      */       }
/*      */ 
/*      */       public String getMimeType()
/*      */       {
/* 3392 */         return this.mimeType;
/*      */       }
/*      */ 
/*      */       public void setMimeType(String paramString)
/*      */       {
/* 3399 */         this.mimeType = paramString;
/*      */       }
/*      */ 
/*      */       public String getEncoding()
/*      */       {
/* 3406 */         return this.encoding;
/*      */       }
/*      */ 
/*      */       public void setEncoding(String paramString)
/*      */       {
/* 3413 */         if ((paramString == null) || (paramString.length() == 0)) {
/* 3414 */           this.encoding = null;
/*      */         } else {
/* 3416 */           URI localURI = null;
/*      */           try {
/* 3418 */             localURI = new URI(paramString);
/*      */           }
/*      */           catch (URI.MalformedURIException localMalformedURIException) {
/*      */           }
/* 3422 */           this.encoding = localURI.toString();
/*      */         }
/*      */       }
/*      */ 
/*      */       public EncryptionMethod getEncryptionMethod()
/*      */       {
/* 3430 */         return this.encryptionMethod;
/*      */       }
/*      */ 
/*      */       public void setEncryptionMethod(EncryptionMethod paramEncryptionMethod)
/*      */       {
/* 3437 */         this.encryptionMethod = paramEncryptionMethod;
/*      */       }
/*      */ 
/*      */       public KeyInfo getKeyInfo()
/*      */       {
/* 3444 */         return this.keyInfo;
/*      */       }
/*      */ 
/*      */       public void setKeyInfo(KeyInfo paramKeyInfo)
/*      */       {
/* 3451 */         this.keyInfo = paramKeyInfo;
/*      */       }
/*      */ 
/*      */       public CipherData getCipherData()
/*      */       {
/* 3458 */         return this.cipherData;
/*      */       }
/*      */ 
/*      */       public EncryptionProperties getEncryptionProperties()
/*      */       {
/* 3465 */         return this.encryptionProperties;
/*      */       }
/*      */ 
/*      */       public void setEncryptionProperties(EncryptionProperties paramEncryptionProperties)
/*      */       {
/* 3473 */         this.encryptionProperties = paramEncryptionProperties;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class EncryptionMethodImpl
/*      */       implements EncryptionMethod
/*      */     {
/* 3486 */       private String algorithm = null;
/* 3487 */       private int keySize = -2147483648;
/* 3488 */       private byte[] oaepParams = null;
/* 3489 */       private List encryptionMethodInformation = null;
/*      */ 
/*      */       public EncryptionMethodImpl(String arg2)
/*      */       {
/* 3495 */         URI localURI = null;
/*      */         try
/*      */         {
/*      */           String str;
/* 3497 */           localURI = new URI(str);
/*      */         }
/*      */         catch (URI.MalformedURIException localMalformedURIException) {
/*      */         }
/* 3501 */         this.algorithm = localURI.toString();
/* 3502 */         this.encryptionMethodInformation = new LinkedList();
/*      */       }
/*      */ 
/*      */       public String getAlgorithm() {
/* 3506 */         return this.algorithm;
/*      */       }
/*      */ 
/*      */       public int getKeySize() {
/* 3510 */         return this.keySize;
/*      */       }
/*      */ 
/*      */       public void setKeySize(int paramInt) {
/* 3514 */         this.keySize = paramInt;
/*      */       }
/*      */ 
/*      */       public byte[] getOAEPparams() {
/* 3518 */         return this.oaepParams;
/*      */       }
/*      */ 
/*      */       public void setOAEPparams(byte[] paramArrayOfByte) {
/* 3522 */         this.oaepParams = paramArrayOfByte;
/*      */       }
/*      */ 
/*      */       public Iterator getEncryptionMethodInformation() {
/* 3526 */         return this.encryptionMethodInformation.iterator();
/*      */       }
/*      */ 
/*      */       public void addEncryptionMethodInformation(Element paramElement) {
/* 3530 */         this.encryptionMethodInformation.add(paramElement);
/*      */       }
/*      */ 
/*      */       public void removeEncryptionMethodInformation(Element paramElement) {
/* 3534 */         this.encryptionMethodInformation.remove(paramElement);
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 3546 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod");
/*      */ 
/* 3549 */         localElement.setAttributeNS(null, "Algorithm", this.algorithm);
/*      */ 
/* 3551 */         if (this.keySize > 0) {
/* 3552 */           localElement.appendChild(ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "KeySize").appendChild(XMLCipher.this._contextDocument.createTextNode(String.valueOf(this.keySize))));
/*      */         }
/*      */ 
/* 3559 */         if (null != this.oaepParams) {
/* 3560 */           localElement.appendChild(ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "OAEPparams").appendChild(XMLCipher.this._contextDocument.createTextNode(new String(this.oaepParams))));
/*      */         }
/*      */ 
/* 3567 */         if (!this.encryptionMethodInformation.isEmpty()) {
/* 3568 */           Iterator localIterator = this.encryptionMethodInformation.iterator();
/* 3569 */           localElement.appendChild((Element)localIterator.next());
/*      */         }
/*      */ 
/* 3572 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class EncryptionPropertiesImpl
/*      */       implements EncryptionProperties
/*      */     {
/* 3584 */       private String id = null;
/* 3585 */       private List encryptionProperties = null;
/*      */ 
/*      */       public EncryptionPropertiesImpl()
/*      */       {
/* 3591 */         this.encryptionProperties = new LinkedList();
/*      */       }
/*      */ 
/*      */       public String getId() {
/* 3595 */         return this.id;
/*      */       }
/*      */ 
/*      */       public void setId(String paramString) {
/* 3599 */         this.id = paramString;
/*      */       }
/*      */ 
/*      */       public Iterator getEncryptionProperties() {
/* 3603 */         return this.encryptionProperties.iterator();
/*      */       }
/*      */ 
/*      */       public void addEncryptionProperty(EncryptionProperty paramEncryptionProperty) {
/* 3607 */         this.encryptionProperties.add(paramEncryptionProperty);
/*      */       }
/*      */ 
/*      */       public void removeEncryptionProperty(EncryptionProperty paramEncryptionProperty) {
/* 3611 */         this.encryptionProperties.remove(paramEncryptionProperty);
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 3622 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties");
/*      */ 
/* 3625 */         if (null != this.id) {
/* 3626 */           localElement.setAttributeNS(null, "Id", this.id);
/*      */         }
/* 3628 */         Iterator localIterator = getEncryptionProperties();
/* 3629 */         while (localIterator.hasNext()) {
/* 3630 */           localElement.appendChild(((XMLCipher.Factory.EncryptionPropertyImpl)localIterator.next()).toElement());
/*      */         }
/*      */ 
/* 3634 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class EncryptionPropertyImpl
/*      */       implements EncryptionProperty
/*      */     {
/* 3648 */       private String target = null;
/* 3649 */       private String id = null;
/* 3650 */       private HashMap attributeMap = new HashMap();
/* 3651 */       private List encryptionInformation = null;
/*      */ 
/*      */       public EncryptionPropertyImpl()
/*      */       {
/* 3658 */         this.encryptionInformation = new LinkedList();
/*      */       }
/*      */ 
/*      */       public String getTarget() {
/* 3662 */         return this.target;
/*      */       }
/*      */ 
/*      */       public void setTarget(String paramString) {
/* 3666 */         if ((paramString == null) || (paramString.length() == 0)) {
/* 3667 */           this.target = null;
/* 3668 */         } else if (paramString.startsWith("#"))
/*      */         {
/* 3674 */           this.target = paramString;
/*      */         } else {
/* 3676 */           URI localURI = null;
/*      */           try {
/* 3678 */             localURI = new URI(paramString);
/*      */           }
/*      */           catch (URI.MalformedURIException localMalformedURIException) {
/*      */           }
/* 3682 */           this.target = localURI.toString();
/*      */         }
/*      */       }
/*      */ 
/*      */       public String getId() {
/* 3687 */         return this.id;
/*      */       }
/*      */ 
/*      */       public void setId(String paramString) {
/* 3691 */         this.id = paramString;
/*      */       }
/*      */ 
/*      */       public String getAttribute(String paramString) {
/* 3695 */         return (String)this.attributeMap.get(paramString);
/*      */       }
/*      */ 
/*      */       public void setAttribute(String paramString1, String paramString2) {
/* 3699 */         this.attributeMap.put(paramString1, paramString2);
/*      */       }
/*      */ 
/*      */       public Iterator getEncryptionInformation() {
/* 3703 */         return this.encryptionInformation.iterator();
/*      */       }
/*      */ 
/*      */       public void addEncryptionInformation(Element paramElement) {
/* 3707 */         this.encryptionInformation.add(paramElement);
/*      */       }
/*      */ 
/*      */       public void removeEncryptionInformation(Element paramElement) {
/* 3711 */         this.encryptionInformation.remove(paramElement);
/*      */       }
/*      */ 
/*      */       Element toElement()
/*      */       {
/* 3724 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");
/*      */ 
/* 3727 */         if (null != this.target) {
/* 3728 */           localElement.setAttributeNS(null, "Target", this.target);
/*      */         }
/*      */ 
/* 3731 */         if (null != this.id) {
/* 3732 */           localElement.setAttributeNS(null, "Id", this.id);
/*      */         }
/*      */ 
/* 3738 */         return localElement;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class ReferenceListImpl
/*      */       implements ReferenceList
/*      */     {
/*      */       private Class sentry;
/*      */       private List references;
/*      */ 
/*      */       public ReferenceListImpl(int arg2)
/*      */       {
/*      */         int i;
/* 3831 */         if (i == 1)
/* 3832 */           this.sentry = DataReference.class;
/* 3833 */         else if (i == 2)
/* 3834 */           this.sentry = KeyReference.class;
/*      */         else {
/* 3836 */           throw new IllegalArgumentException();
/*      */         }
/* 3838 */         this.references = new LinkedList();
/*      */       }
/*      */ 
/*      */       public void add(Reference paramReference) {
/* 3842 */         if (!paramReference.getClass().equals(this.sentry)) {
/* 3843 */           throw new IllegalArgumentException();
/*      */         }
/* 3845 */         this.references.add(paramReference);
/*      */       }
/*      */ 
/*      */       public void remove(Reference paramReference) {
/* 3849 */         if (!paramReference.getClass().equals(this.sentry)) {
/* 3850 */           throw new IllegalArgumentException();
/*      */         }
/* 3852 */         this.references.remove(paramReference);
/*      */       }
/*      */ 
/*      */       public int size() {
/* 3856 */         return this.references.size();
/*      */       }
/*      */ 
/*      */       public boolean isEmpty() {
/* 3860 */         return this.references.isEmpty();
/*      */       }
/*      */ 
/*      */       public Iterator getReferences() {
/* 3864 */         return this.references.iterator();
/*      */       }
/*      */ 
/*      */       Element toElement() {
/* 3868 */         Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "ReferenceList");
/*      */ 
/* 3872 */         Iterator localIterator = this.references.iterator();
/* 3873 */         while (localIterator.hasNext()) {
/* 3874 */           Reference localReference = (Reference)localIterator.next();
/* 3875 */           localElement.appendChild(((ReferenceImpl)localReference).toElement());
/*      */         }
/*      */ 
/* 3878 */         return localElement;
/*      */       }
/*      */ 
/*      */       public Reference newDataReference(String paramString) {
/* 3882 */         return new DataReference(paramString);
/*      */       }
/*      */ 
/*      */       public Reference newKeyReference(String paramString) {
/* 3886 */         return new KeyReference(paramString);
/*      */       }
/*      */ 
/*      */       private class DataReference extends XMLCipher.Factory.ReferenceListImpl.ReferenceImpl
/*      */       {
/*      */         DataReference(String arg2)
/*      */         {
/* 3946 */           super(str);
/*      */         }
/*      */ 
/*      */         public Element toElement() {
/* 3950 */           return super.toElement("DataReference");
/*      */         }
/*      */       }
/*      */ 
/*      */       private class KeyReference extends XMLCipher.Factory.ReferenceListImpl.ReferenceImpl {
/*      */         KeyReference(String arg2) {
/* 3956 */           super(str);
/*      */         }
/*      */ 
/*      */         public Element toElement() {
/* 3960 */           return super.toElement("KeyReference");
/*      */         }
/*      */       }
/*      */ 
/*      */       private abstract class ReferenceImpl
/*      */         implements Reference
/*      */       {
/*      */         private String uri;
/*      */         private List referenceInformation;
/*      */ 
/*      */         ReferenceImpl(String arg2)
/*      */         {
/*      */           Object localObject;
/* 3900 */           this.uri = localObject;
/* 3901 */           this.referenceInformation = new LinkedList();
/*      */         }
/*      */ 
/*      */         public String getURI() {
/* 3905 */           return this.uri;
/*      */         }
/*      */ 
/*      */         public Iterator getElementRetrievalInformation() {
/* 3909 */           return this.referenceInformation.iterator();
/*      */         }
/*      */ 
/*      */         public void setURI(String paramString) {
/* 3913 */           this.uri = paramString;
/*      */         }
/*      */ 
/*      */         public void removeElementRetrievalInformation(Element paramElement) {
/* 3917 */           this.referenceInformation.remove(paramElement);
/*      */         }
/*      */ 
/*      */         public void addElementRetrievalInformation(Element paramElement) {
/* 3921 */           this.referenceInformation.add(paramElement);
/*      */         }
/*      */ 
/*      */         public abstract Element toElement();
/*      */ 
/*      */         Element toElement(String paramString)
/*      */         {
/* 3930 */           Element localElement = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", paramString);
/*      */ 
/* 3934 */           localElement.setAttribute("URI", this.uri);
/*      */ 
/* 3940 */           return localElement;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private class TransformsImpl extends com.sun.org.apache.xml.internal.security.transforms.Transforms
/*      */       implements Transforms
/*      */     {
/*      */       public TransformsImpl()
/*      */       {
/* 3756 */         super();
/*      */       }
/*      */ 
/*      */       public TransformsImpl(Document arg2)
/*      */       {
/*      */         Object localObject;
/* 3763 */         if (localObject == null) {
/* 3764 */           throw new RuntimeException("Document is null");
/*      */         }
/*      */ 
/* 3767 */         this._doc = localObject;
/* 3768 */         this._constructionElement = createElementForFamilyLocal(this._doc, getBaseNamespace(), getBaseLocalName());
/*      */       }
/*      */ 
/*      */       public TransformsImpl(Element arg2)
/*      */         throws XMLSignatureException, InvalidTransformException, XMLSecurityException, TransformationException
/*      */       {
/* 3785 */         super("");
/*      */       }
/*      */ 
/*      */       public Element toElement()
/*      */       {
/* 3795 */         if (this._doc == null) {
/* 3796 */           this._doc = XMLCipher.this._contextDocument;
/*      */         }
/* 3798 */         return getElement();
/*      */       }
/*      */ 
/*      */       public com.sun.org.apache.xml.internal.security.transforms.Transforms getDSTransforms()
/*      */       {
/* 3803 */         return this;
/*      */       }
/*      */ 
/*      */       public String getBaseNamespace()
/*      */       {
/* 3810 */         return "http://www.w3.org/2001/04/xmlenc#";
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Serializer
/*      */   {
/*      */     Serializer()
/*      */     {
/*      */     }
/*      */ 
/*      */     String serialize(Document paramDocument)
/*      */       throws Exception
/*      */     {
/* 1855 */       return canonSerialize(paramDocument);
/*      */     }
/*      */ 
/*      */     String serialize(Element paramElement)
/*      */       throws Exception
/*      */     {
/* 1870 */       return canonSerialize(paramElement);
/*      */     }
/*      */ 
/*      */     String serialize(NodeList paramNodeList)
/*      */       throws Exception
/*      */     {
/* 1896 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 1897 */       XMLCipher.this._canon.setWriter(localByteArrayOutputStream);
/* 1898 */       XMLCipher.this._canon.notReset();
/* 1899 */       for (int i = 0; i < paramNodeList.getLength(); i++) {
/* 1900 */         XMLCipher.this._canon.canonicalizeSubtree(paramNodeList.item(i));
/*      */       }
/* 1902 */       localByteArrayOutputStream.close();
/* 1903 */       return localByteArrayOutputStream.toString("UTF-8");
/*      */     }
/*      */ 
/*      */     String canonSerialize(Node paramNode)
/*      */       throws Exception
/*      */     {
/* 1913 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 1914 */       XMLCipher.this._canon.setWriter(localByteArrayOutputStream);
/* 1915 */       XMLCipher.this._canon.notReset();
/* 1916 */       XMLCipher.this._canon.canonicalizeSubtree(paramNode);
/* 1917 */       localByteArrayOutputStream.close();
/* 1918 */       return localByteArrayOutputStream.toString("UTF-8");
/*      */     }
/*      */ 
/*      */     DocumentFragment deserialize(String paramString, Node paramNode)
/*      */       throws XMLEncryptionException
/*      */     {
/* 1934 */       StringBuffer localStringBuffer = new StringBuffer();
/* 1935 */       localStringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><fragment");
/*      */ 
/* 1940 */       Node localNode1 = paramNode;
/*      */       Object localObject2;
/*      */       Object localObject3;
/* 1942 */       while (localNode1 != null)
/*      */       {
/* 1944 */         localObject1 = localNode1.getAttributes();
/*      */         int i;
/* 1946 */         if (localObject1 != null)
/* 1947 */           i = ((NamedNodeMap)localObject1).getLength();
/*      */         else {
/* 1949 */           i = 0;
/*      */         }
/* 1951 */         for (int j = 0; j < i; j++) {
/* 1952 */           localObject2 = ((NamedNodeMap)localObject1).item(j);
/* 1953 */           if ((((Node)localObject2).getNodeName().startsWith("xmlns:")) || (((Node)localObject2).getNodeName().equals("xmlns")))
/*      */           {
/* 1957 */             localObject3 = paramNode;
/* 1958 */             int k = 0;
/* 1959 */             while (localObject3 != localNode1) {
/* 1960 */               NamedNodeMap localNamedNodeMap = ((Node)localObject3).getAttributes();
/* 1961 */               if ((localNamedNodeMap != null) && (localNamedNodeMap.getNamedItem(((Node)localObject2).getNodeName()) != null))
/*      */               {
/* 1963 */                 k = 1;
/* 1964 */                 break;
/*      */               }
/* 1966 */               localObject3 = ((Node)localObject3).getParentNode();
/*      */             }
/* 1968 */             if (k == 0)
/*      */             {
/* 1971 */               localStringBuffer.append(" " + ((Node)localObject2).getNodeName() + "=\"" + ((Node)localObject2).getNodeValue() + "\"");
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1976 */         localNode1 = localNode1.getParentNode();
/* 1978 */       }localStringBuffer.append(">" + paramString + "</" + "fragment" + ">");
/* 1979 */       Object localObject1 = localStringBuffer.toString();
/*      */       DocumentFragment localDocumentFragment;
/*      */       try { DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*      */ 
/* 1984 */         localDocumentBuilderFactory.setNamespaceAware(true);
/* 1985 */         localDocumentBuilderFactory.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
/* 1986 */         DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/* 1987 */         localObject2 = localDocumentBuilder.parse(new InputSource(new StringReader((String)localObject1)));
/*      */ 
/* 1990 */         localObject3 = (Element)XMLCipher.this._contextDocument.importNode(((Document)localObject2).getDocumentElement(), true);
/*      */ 
/* 1992 */         localDocumentFragment = XMLCipher.this._contextDocument.createDocumentFragment();
/* 1993 */         Node localNode2 = ((Element)localObject3).getFirstChild();
/* 1994 */         while (localNode2 != null) {
/* 1995 */           ((Element)localObject3).removeChild(localNode2);
/* 1996 */           localDocumentFragment.appendChild(localNode2);
/* 1997 */           localNode2 = ((Element)localObject3).getFirstChild();
/*      */         }
/*      */       }
/*      */       catch (SAXException localSAXException)
/*      */       {
/* 2002 */         throw new XMLEncryptionException("empty", localSAXException);
/*      */       } catch (ParserConfigurationException localParserConfigurationException) {
/* 2004 */         throw new XMLEncryptionException("empty", localParserConfigurationException);
/*      */       } catch (IOException localIOException) {
/* 2006 */         throw new XMLEncryptionException("empty", localIOException);
/*      */       }
/*      */ 
/* 2009 */       return localDocumentFragment;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.encryption.XMLCipher
 * JD-Core Version:    0.6.2
 */