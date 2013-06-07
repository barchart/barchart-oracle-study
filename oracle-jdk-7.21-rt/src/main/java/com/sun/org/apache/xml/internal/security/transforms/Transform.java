/*     */ package com.sun.org.apache.xml.internal.security.transforms;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.Init;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
/*     */ import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public final class Transform extends SignatureElementProxy
/*     */ {
/*  61 */   static Logger log = Logger.getLogger(Transform.class.getName());
/*     */ 
/*  65 */   private static boolean alreadyInitialized = false;
/*     */ 
/*  68 */   private static HashMap transformClassHash = null;
/*     */ 
/*  70 */   private static HashMap transformSpiHash = new HashMap();
/*     */ 
/*  72 */   private TransformSpi transformSpi = null;
/*     */ 
/*     */   public Transform(Document paramDocument, String paramString, NodeList paramNodeList)
/*     */     throws InvalidTransformException
/*     */   {
/*  88 */     super(paramDocument);
/*     */ 
/*  90 */     this._constructionElement.setAttributeNS(null, "Algorithm", paramString);
/*     */ 
/*  93 */     this.transformSpi = getTransformSpi(paramString);
/*  94 */     if (this.transformSpi == null) {
/*  95 */       Object[] arrayOfObject = { paramString };
/*  96 */       throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject);
/*     */     }
/*     */ 
/* 100 */     if (log.isLoggable(Level.FINE)) {
/* 101 */       log.log(Level.FINE, "Create URI \"" + paramString + "\" class \"" + this.transformSpi.getClass() + "\"");
/*     */ 
/* 103 */       log.log(Level.FINE, "The NodeList is " + paramNodeList);
/*     */     }
/*     */ 
/* 107 */     if (paramNodeList != null)
/* 108 */       for (int i = 0; i < paramNodeList.getLength(); i++)
/* 109 */         this._constructionElement.appendChild(paramNodeList.item(i).cloneNode(true));
/*     */   }
/*     */ 
/*     */   public Transform(Element paramElement, String paramString)
/*     */     throws InvalidTransformException, TransformationException, XMLSecurityException
/*     */   {
/* 129 */     super(paramElement, paramString);
/*     */ 
/* 132 */     String str = paramElement.getAttributeNS(null, "Algorithm");
/*     */     Object[] arrayOfObject;
/* 134 */     if ((str == null) || (str.length() == 0)) {
/* 135 */       arrayOfObject = new Object[] { "Algorithm", "Transform" };
/*     */ 
/* 137 */       throw new TransformationException("xml.WrongContent", arrayOfObject);
/*     */     }
/*     */ 
/* 140 */     this.transformSpi = getTransformSpi(str);
/* 141 */     if (this.transformSpi == null) {
/* 142 */       arrayOfObject = new Object[] { str };
/* 143 */       throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Transform getInstance(Document paramDocument, String paramString)
/*     */     throws InvalidTransformException
/*     */   {
/* 161 */     return getInstance(paramDocument, paramString, (NodeList)null);
/*     */   }
/*     */ 
/*     */   public static Transform getInstance(Document paramDocument, String paramString, Element paramElement)
/*     */     throws InvalidTransformException
/*     */   {
/* 180 */     HelperNodeList localHelperNodeList = new HelperNodeList();
/*     */ 
/* 182 */     XMLUtils.addReturnToElement(paramDocument, localHelperNodeList);
/* 183 */     localHelperNodeList.appendChild(paramElement);
/* 184 */     XMLUtils.addReturnToElement(paramDocument, localHelperNodeList);
/*     */ 
/* 186 */     return getInstance(paramDocument, paramString, localHelperNodeList);
/*     */   }
/*     */ 
/*     */   public static Transform getInstance(Document paramDocument, String paramString, NodeList paramNodeList)
/*     */     throws InvalidTransformException
/*     */   {
/* 204 */     return new Transform(paramDocument, paramString, paramNodeList);
/*     */   }
/*     */ 
/*     */   public static void init()
/*     */   {
/* 211 */     if (!alreadyInitialized) {
/* 212 */       transformClassHash = new HashMap(10);
/*     */ 
/* 214 */       Init.init();
/* 215 */       alreadyInitialized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void register(String paramString1, String paramString2)
/*     */     throws AlgorithmAlreadyRegisteredException
/*     */   {
/* 234 */     Class localClass = getImplementingClass(paramString1);
/* 235 */     if (localClass != null) {
/* 236 */       localObject = new Object[] { paramString1, localClass };
/* 237 */       throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", (Object[])localObject);
/*     */     }
/*     */ 
/* 241 */     Object localObject = Thread.currentThread().getContextClassLoader();
/*     */     try
/*     */     {
/* 244 */       transformClassHash.put(paramString1, Class.forName(paramString2, true, (ClassLoader)localObject));
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 247 */       throw new RuntimeException(localClassNotFoundException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getURI()
/*     */   {
/* 257 */     return this._constructionElement.getAttributeNS(null, "Algorithm");
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput performTransform(XMLSignatureInput paramXMLSignatureInput)
/*     */     throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException
/*     */   {
/* 277 */     XMLSignatureInput localXMLSignatureInput = null;
/*     */     try
/*     */     {
/* 280 */       localXMLSignatureInput = this.transformSpi.enginePerformTransform(paramXMLSignatureInput, this);
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 282 */       arrayOfObject = new Object[] { getURI(), "ParserConfigurationException" };
/* 283 */       throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, localParserConfigurationException);
/*     */     }
/*     */     catch (SAXException localSAXException) {
/* 286 */       Object[] arrayOfObject = { getURI(), "SAXException" };
/* 287 */       throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, localSAXException);
/*     */     }
/*     */ 
/* 291 */     return localXMLSignatureInput;
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput performTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream)
/*     */     throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException
/*     */   {
/* 311 */     XMLSignatureInput localXMLSignatureInput = null;
/*     */     try
/*     */     {
/* 314 */       localXMLSignatureInput = this.transformSpi.enginePerformTransform(paramXMLSignatureInput, paramOutputStream, this);
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 316 */       arrayOfObject = new Object[] { getURI(), "ParserConfigurationException" };
/* 317 */       throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, localParserConfigurationException);
/*     */     }
/*     */     catch (SAXException localSAXException) {
/* 320 */       Object[] arrayOfObject = { getURI(), "SAXException" };
/* 321 */       throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, localSAXException);
/*     */     }
/*     */ 
/* 325 */     return localXMLSignatureInput;
/*     */   }
/*     */ 
/*     */   private static Class getImplementingClass(String paramString)
/*     */   {
/* 335 */     return (Class)transformClassHash.get(paramString);
/*     */   }
/*     */ 
/*     */   private static TransformSpi getTransformSpi(String paramString) throws InvalidTransformException
/*     */   {
/*     */     try {
/* 341 */       Object localObject1 = transformSpiHash.get(paramString);
/* 342 */       if (localObject1 != null) {
/* 343 */         return (TransformSpi)localObject1;
/*     */       }
/* 345 */       localObject2 = (Class)transformClassHash.get(paramString);
/* 346 */       if (localObject2 != null) {
/* 347 */         TransformSpi localTransformSpi = (TransformSpi)((Class)localObject2).newInstance();
/* 348 */         transformSpiHash.put(paramString, localTransformSpi);
/* 349 */         return localTransformSpi;
/*     */       }
/*     */     } catch (InstantiationException localInstantiationException) {
/* 352 */       localObject2 = new Object[] { paramString };
/* 353 */       throw new InvalidTransformException("signature.Transform.UnknownTransform", (Object[])localObject2, localInstantiationException);
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 356 */       Object localObject2 = { paramString };
/* 357 */       throw new InvalidTransformException("signature.Transform.UnknownTransform", (Object[])localObject2, localIllegalAccessException);
/*     */     }
/*     */ 
/* 360 */     return null;
/*     */   }
/*     */ 
/*     */   public String getBaseLocalName()
/*     */   {
/* 365 */     return "Transform";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.transforms.Transform
 * JD-Core Version:    0.6.2
 */