/*     */ package com.sun.org.apache.xml.internal.security.transforms.implementations;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transform;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.Base64;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.Text;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class TransformBase64Decode extends TransformSpi
/*     */ {
/*     */   public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#base64";
/*     */ 
/*     */   protected String engineGetURI()
/*     */   {
/*  84 */     return "http://www.w3.org/2000/09/xmldsig#base64";
/*     */   }
/*     */ 
/*     */   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, Transform paramTransform)
/*     */     throws IOException, CanonicalizationException, TransformationException
/*     */   {
/* 101 */     return enginePerformTransform(paramXMLSignatureInput, null, paramTransform);
/*     */   }
/*     */ 
/*     */   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
/*     */     throws IOException, CanonicalizationException, TransformationException
/*     */   {
/*     */     try
/*     */     {
/*     */       Object localObject1;
/*     */       Object localObject2;
/*     */       Object localObject3;
/* 109 */       if (paramXMLSignatureInput.isElement()) {
/* 110 */         localObject1 = paramXMLSignatureInput.getSubNode();
/* 111 */         if (paramXMLSignatureInput.getSubNode().getNodeType() == 3) {
/* 112 */           localObject1 = ((Node)localObject1).getParentNode();
/*     */         }
/* 114 */         localObject2 = new StringBuffer();
/* 115 */         traverseElement((Element)localObject1, (StringBuffer)localObject2);
/* 116 */         if (paramOutputStream == null) {
/* 117 */           localObject3 = Base64.decode(((StringBuffer)localObject2).toString());
/* 118 */           return new XMLSignatureInput((byte[])localObject3);
/*     */         }
/* 120 */         Base64.decode(((StringBuffer)localObject2).toString(), paramOutputStream);
/* 121 */         localObject3 = new XMLSignatureInput((byte[])null);
/* 122 */         ((XMLSignatureInput)localObject3).setOutputStream(paramOutputStream);
/* 123 */         return localObject3;
/*     */       }
/*     */ 
/* 126 */       if ((paramXMLSignatureInput.isOctetStream()) || (paramXMLSignatureInput.isNodeSet()))
/*     */       {
/* 129 */         if (paramOutputStream == null) {
/* 130 */           localObject1 = paramXMLSignatureInput.getBytes();
/* 131 */           localObject2 = Base64.decode((byte[])localObject1);
/* 132 */           return new XMLSignatureInput((byte[])localObject2);
/*     */         }
/* 134 */         if ((paramXMLSignatureInput.isByteArray()) || (paramXMLSignatureInput.isNodeSet()))
/* 135 */           Base64.decode(paramXMLSignatureInput.getBytes(), paramOutputStream);
/*     */         else {
/* 137 */           Base64.decode(new BufferedInputStream(paramXMLSignatureInput.getOctetStreamReal()), paramOutputStream);
/*     */         }
/*     */ 
/* 140 */         localObject1 = new XMLSignatureInput((byte[])null);
/* 141 */         ((XMLSignatureInput)localObject1).setOutputStream(paramOutputStream);
/* 142 */         return localObject1;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 150 */         localObject1 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(paramXMLSignatureInput.getOctetStream());
/*     */ 
/* 154 */         localObject2 = ((Document)localObject1).getDocumentElement();
/* 155 */         localObject3 = new StringBuffer();
/* 156 */         traverseElement((Element)localObject2, (StringBuffer)localObject3);
/* 157 */         byte[] arrayOfByte = Base64.decode(((StringBuffer)localObject3).toString());
/*     */ 
/* 159 */         return new XMLSignatureInput(arrayOfByte);
/*     */       } catch (ParserConfigurationException localParserConfigurationException) {
/* 161 */         throw new TransformationException("c14n.Canonicalizer.Exception", localParserConfigurationException);
/*     */       } catch (SAXException localSAXException) {
/* 163 */         throw new TransformationException("SAX exception", localSAXException);
/*     */       }
/*     */     } catch (Base64DecodingException localBase64DecodingException) {
/* 166 */       throw new TransformationException("Base64Decoding", localBase64DecodingException);
/*     */     }
/*     */   }
/*     */ 
/*     */   void traverseElement(Element paramElement, StringBuffer paramStringBuffer) {
/* 171 */     Node localNode = paramElement.getFirstChild();
/* 172 */     while (localNode != null) {
/* 173 */       switch (localNode.getNodeType()) {
/*     */       case 1:
/* 175 */         traverseElement((Element)localNode, paramStringBuffer);
/* 176 */         break;
/*     */       case 3:
/* 178 */         paramStringBuffer.append(((Text)localNode).getData());
/*     */       }
/* 180 */       localNode = localNode.getNextSibling();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.transforms.implementations.TransformBase64Decode
 * JD-Core Version:    0.6.2
 */