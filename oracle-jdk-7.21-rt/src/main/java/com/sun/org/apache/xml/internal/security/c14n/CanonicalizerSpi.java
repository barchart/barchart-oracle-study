/*     */ package com.sun.org.apache.xml.internal.security.c14n;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ public abstract class CanonicalizerSpi
/*     */ {
/* 197 */   protected boolean reset = false;
/*     */ 
/*     */   public byte[] engineCanonicalize(byte[] paramArrayOfByte)
/*     */     throws ParserConfigurationException, IOException, SAXException, CanonicalizationException
/*     */   {
/*  66 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
/*  67 */     InputSource localInputSource = new InputSource(localByteArrayInputStream);
/*  68 */     DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/*  71 */     localDocumentBuilderFactory.setNamespaceAware(true);
/*     */ 
/*  73 */     DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/*     */ 
/* 100 */     Document localDocument = localDocumentBuilder.parse(localInputSource);
/* 101 */     byte[] arrayOfByte = engineCanonicalizeSubTree(localDocument);
/* 102 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public byte[] engineCanonicalizeXPathNodeSet(NodeList paramNodeList)
/*     */     throws CanonicalizationException
/*     */   {
/* 115 */     return engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(paramNodeList));
/*     */   }
/*     */ 
/*     */   public byte[] engineCanonicalizeXPathNodeSet(NodeList paramNodeList, String paramString)
/*     */     throws CanonicalizationException
/*     */   {
/* 131 */     return engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(paramNodeList), paramString);
/*     */   }
/*     */ 
/*     */   public abstract String engineGetURI();
/*     */ 
/*     */   public abstract boolean engineGetIncludeComments();
/*     */ 
/*     */   public abstract byte[] engineCanonicalizeXPathNodeSet(Set paramSet)
/*     */     throws CanonicalizationException;
/*     */ 
/*     */   public abstract byte[] engineCanonicalizeXPathNodeSet(Set paramSet, String paramString)
/*     */     throws CanonicalizationException;
/*     */ 
/*     */   public abstract byte[] engineCanonicalizeSubTree(Node paramNode)
/*     */     throws CanonicalizationException;
/*     */ 
/*     */   public abstract byte[] engineCanonicalizeSubTree(Node paramNode, String paramString)
/*     */     throws CanonicalizationException;
/*     */ 
/*     */   public abstract void setWriter(OutputStream paramOutputStream);
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.c14n.CanonicalizerSpi
 * JD-Core Version:    0.6.2
 */