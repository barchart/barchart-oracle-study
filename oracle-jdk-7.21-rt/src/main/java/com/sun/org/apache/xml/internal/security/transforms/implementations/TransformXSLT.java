/*     */ package com.sun.org.apache.xml.internal.security.transforms.implementations;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.Transform;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
/*     */ import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class TransformXSLT extends TransformSpi
/*     */ {
/*     */   public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xslt-19991116";
/*     */   static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
/*     */   static final String defaultXSLTSpecNSprefix = "xslt";
/*     */   static final String XSLTSTYLESHEET = "stylesheet";
/*  66 */   private static Class xClass = null;
/*     */ 
/*  73 */   static Logger log = Logger.getLogger(TransformXSLT.class.getName());
/*     */ 
/*     */   protected String engineGetURI()
/*     */   {
/*  83 */     return "http://www.w3.org/TR/1999/REC-xslt-19991116";
/*     */   }
/*     */ 
/*     */   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, Transform paramTransform)
/*     */     throws IOException, TransformationException
/*     */   {
/*  98 */     return enginePerformTransform(paramXMLSignatureInput, null, paramTransform);
/*     */   }
/*     */ 
/*     */   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
/*     */     throws IOException, TransformationException
/*     */   {
/*     */     Object localObject1;
/* 104 */     if (xClass == null) {
/* 105 */       localObject1 = new Object[] { "SECURE_PROCESSING_FEATURE not supported" };
/* 106 */       throw new TransformationException("generic.EmptyMessage", (Object[])localObject1);
/*     */     }
/*     */     try {
/* 109 */       localObject1 = paramTransform.getElement();
/*     */ 
/* 111 */       localObject2 = XMLUtils.selectNode(((Element)localObject1).getFirstChild(), "http://www.w3.org/1999/XSL/Transform", "stylesheet", 0);
/*     */ 
/* 115 */       if (localObject2 == null) {
/* 116 */         localObject3 = new Object[] { "xslt:stylesheet", "Transform" };
/*     */ 
/* 118 */         throw new TransformationException("xml.WrongContent", (Object[])localObject3);
/*     */       }
/*     */ 
/* 121 */       Object localObject3 = TransformerFactory.newInstance();
/* 122 */       Class localClass = localObject3.getClass();
/* 123 */       Method localMethod = localClass.getMethod("setFeature", new Class[] { String.class, Boolean.TYPE });
/*     */ 
/* 125 */       localMethod.invoke(localObject3, new Object[] { "http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE });
/*     */ 
/* 133 */       StreamSource localStreamSource1 = new StreamSource(new ByteArrayInputStream(paramXMLSignatureInput.getBytes()));
/*     */ 
/* 146 */       Object localObject4 = new ByteArrayOutputStream();
/* 147 */       Transformer localTransformer = ((TransformerFactory)localObject3).newTransformer();
/* 148 */       Object localObject6 = new DOMSource((Node)localObject2);
/* 149 */       StreamResult localStreamResult = new StreamResult((OutputStream)localObject4);
/*     */ 
/* 151 */       localTransformer.transform((Source)localObject6, localStreamResult);
/*     */ 
/* 153 */       StreamSource localStreamSource2 = new StreamSource(new ByteArrayInputStream(((ByteArrayOutputStream)localObject4).toByteArray()));
/*     */ 
/* 157 */       localObject4 = ((TransformerFactory)localObject3).newTransformer(localStreamSource2);
/*     */       try
/*     */       {
/* 165 */         ((Transformer)localObject4).setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
/*     */       }
/*     */       catch (Exception localException) {
/* 168 */         log.log(Level.WARNING, "Unable to set Xalan line-separator property: " + localException.getMessage());
/*     */       }
/*     */ 
/* 172 */       if (paramOutputStream == null) {
/* 173 */         localObject5 = new ByteArrayOutputStream();
/* 174 */         localObject6 = new StreamResult((OutputStream)localObject5);
/* 175 */         ((Transformer)localObject4).transform(localStreamSource1, (Result)localObject6);
/* 176 */         return new XMLSignatureInput(((ByteArrayOutputStream)localObject5).toByteArray());
/*     */       }
/* 178 */       Object localObject5 = new StreamResult(paramOutputStream);
/*     */ 
/* 180 */       ((Transformer)localObject4).transform(localStreamSource1, (Result)localObject5);
/* 181 */       localObject6 = new XMLSignatureInput((byte[])null);
/* 182 */       ((XMLSignatureInput)localObject6).setOutputStream(paramOutputStream);
/* 183 */       return localObject6;
/*     */     } catch (XMLSecurityException localXMLSecurityException) {
/* 185 */       localObject2 = new Object[] { localXMLSecurityException.getMessage() };
/*     */ 
/* 187 */       throw new TransformationException("generic.EmptyMessage", (Object[])localObject2, localXMLSecurityException);
/*     */     } catch (TransformerConfigurationException localTransformerConfigurationException) {
/* 189 */       localObject2 = new Object[] { localTransformerConfigurationException.getMessage() };
/*     */ 
/* 191 */       throw new TransformationException("generic.EmptyMessage", (Object[])localObject2, localTransformerConfigurationException);
/*     */     } catch (TransformerException localTransformerException) {
/* 193 */       localObject2 = new Object[] { localTransformerException.getMessage() };
/*     */ 
/* 195 */       throw new TransformationException("generic.EmptyMessage", (Object[])localObject2, localTransformerException);
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 197 */       localObject2 = new Object[] { localNoSuchMethodException.getMessage() };
/*     */ 
/* 199 */       throw new TransformationException("generic.EmptyMessage", (Object[])localObject2, localNoSuchMethodException);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 201 */       localObject2 = new Object[] { localIllegalAccessException.getMessage() };
/*     */ 
/* 203 */       throw new TransformationException("generic.EmptyMessage", (Object[])localObject2, localIllegalAccessException);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 205 */       Object localObject2 = { localInvocationTargetException.getMessage() };
/*     */ 
/* 207 */       throw new TransformationException("generic.EmptyMessage", (Object[])localObject2, localInvocationTargetException);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  69 */       xClass = Class.forName("javax.xml.XMLConstants");
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXSLT
 * JD-Core Version:    0.6.2
 */