/*     */ package com.sun.org.apache.xml.internal.security.c14n;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class Canonicalizer
/*     */ {
/*     */   public static final String ENCODING = "UTF8";
/*     */   public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE = "(.//. | .//@* | .//namespace::*)";
/*     */   public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
/*     */   public static final String ALGO_ID_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
/*     */   public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
/*     */   public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
/*     */   public static final String ALGO_ID_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
/*     */   public static final String ALGO_ID_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
/*  85 */   static boolean _alreadyInitialized = false;
/*  86 */   static Map _canonicalizerHash = null;
/*     */ 
/*  88 */   protected CanonicalizerSpi canonicalizerSpi = null;
/*     */ 
/*     */   public static void init()
/*     */   {
/*  96 */     if (!_alreadyInitialized) {
/*  97 */       _canonicalizerHash = new HashMap(10);
/*  98 */       _alreadyInitialized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private Canonicalizer(String paramString)
/*     */     throws InvalidCanonicalizerException
/*     */   {
/*     */     try
/*     */     {
/* 112 */       Class localClass = getImplementingClass(paramString);
/*     */ 
/* 114 */       this.canonicalizerSpi = ((CanonicalizerSpi)localClass.newInstance());
/*     */ 
/* 116 */       this.canonicalizerSpi.reset = true;
/*     */     } catch (Exception localException) {
/* 118 */       Object[] arrayOfObject = { paramString };
/*     */ 
/* 120 */       throw new InvalidCanonicalizerException("signature.Canonicalizer.UnknownCanonicalizer", arrayOfObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final Canonicalizer getInstance(String paramString)
/*     */     throws InvalidCanonicalizerException
/*     */   {
/* 135 */     Canonicalizer localCanonicalizer = new Canonicalizer(paramString);
/*     */ 
/* 137 */     return localCanonicalizer;
/*     */   }
/*     */ 
/*     */   public static void register(String paramString1, String paramString2)
/*     */     throws AlgorithmAlreadyRegisteredException
/*     */   {
/* 151 */     Class localClass = getImplementingClass(paramString1);
/*     */ 
/* 153 */     if (localClass != null) {
/* 154 */       Object[] arrayOfObject = { paramString1, localClass };
/*     */ 
/* 156 */       throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 161 */       _canonicalizerHash.put(paramString1, Class.forName(paramString2));
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 163 */       throw new RuntimeException("c14n class not found");
/*     */     }
/*     */   }
/*     */ 
/*     */   public final String getURI()
/*     */   {
/* 173 */     return this.canonicalizerSpi.engineGetURI();
/*     */   }
/*     */ 
/*     */   public boolean getIncludeComments()
/*     */   {
/* 182 */     return this.canonicalizerSpi.engineGetIncludeComments();
/*     */   }
/*     */ 
/*     */   public byte[] canonicalize(byte[] paramArrayOfByte)
/*     */     throws ParserConfigurationException, IOException, SAXException, CanonicalizationException
/*     */   {
/* 202 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
/* 203 */     InputSource localInputSource = new InputSource(localByteArrayInputStream);
/* 204 */     DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 206 */     localDocumentBuilderFactory.setNamespaceAware(true);
/*     */ 
/* 209 */     localDocumentBuilderFactory.setValidating(true);
/*     */ 
/* 211 */     DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/*     */ 
/* 235 */     localDocumentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
/*     */ 
/* 238 */     Document localDocument = localDocumentBuilder.parse(localInputSource);
/* 239 */     byte[] arrayOfByte = canonicalizeSubtree(localDocument);
/*     */ 
/* 241 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public byte[] canonicalizeSubtree(Node paramNode)
/*     */     throws CanonicalizationException
/*     */   {
/* 254 */     return this.canonicalizerSpi.engineCanonicalizeSubTree(paramNode);
/*     */   }
/*     */ 
/*     */   public byte[] canonicalizeSubtree(Node paramNode, String paramString)
/*     */     throws CanonicalizationException
/*     */   {
/* 267 */     return this.canonicalizerSpi.engineCanonicalizeSubTree(paramNode, paramString);
/*     */   }
/*     */ 
/*     */   public byte[] canonicalizeXPathNodeSet(NodeList paramNodeList)
/*     */     throws CanonicalizationException
/*     */   {
/* 281 */     return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramNodeList);
/*     */   }
/*     */ 
/*     */   public byte[] canonicalizeXPathNodeSet(NodeList paramNodeList, String paramString)
/*     */     throws CanonicalizationException
/*     */   {
/* 296 */     return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramNodeList, paramString);
/*     */   }
/*     */ 
/*     */   public byte[] canonicalizeXPathNodeSet(Set paramSet)
/*     */     throws CanonicalizationException
/*     */   {
/* 309 */     return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramSet);
/*     */   }
/*     */ 
/*     */   public byte[] canonicalizeXPathNodeSet(Set paramSet, String paramString)
/*     */     throws CanonicalizationException
/*     */   {
/* 322 */     return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramSet, paramString);
/*     */   }
/*     */ 
/*     */   public void setWriter(OutputStream paramOutputStream)
/*     */   {
/* 332 */     this.canonicalizerSpi.setWriter(paramOutputStream);
/*     */   }
/*     */ 
/*     */   public String getImplementingCanonicalizerClass()
/*     */   {
/* 341 */     return this.canonicalizerSpi.getClass().getName();
/*     */   }
/*     */ 
/*     */   private static Class getImplementingClass(String paramString)
/*     */   {
/* 351 */     return (Class)_canonicalizerHash.get(paramString);
/*     */   }
/*     */ 
/*     */   public void notReset()
/*     */   {
/* 358 */     this.canonicalizerSpi.reset = false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.c14n.Canonicalizer
 * JD-Core Version:    0.6.2
 */