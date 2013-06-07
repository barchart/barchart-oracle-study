/*     */ package com.sun.org.apache.xpath.internal.jaxp;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.res.XSLMessages;
/*     */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*     */ import com.sun.org.apache.xpath.internal.XPath;
/*     */ import com.sun.org.apache.xpath.internal.XPathContext;
/*     */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.xpath.XPathConstants;
/*     */ import javax.xml.xpath.XPathExpression;
/*     */ import javax.xml.xpath.XPathExpressionException;
/*     */ import javax.xml.xpath.XPathFunctionException;
/*     */ import javax.xml.xpath.XPathFunctionResolver;
/*     */ import javax.xml.xpath.XPathVariableResolver;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.traversal.NodeIterator;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ public class XPathExpressionImpl
/*     */   implements XPathExpression
/*     */ {
/*     */   private XPathFunctionResolver functionResolver;
/*     */   private XPathVariableResolver variableResolver;
/*     */   private JAXPPrefixResolver prefixResolver;
/*     */   private XPath xpath;
/*  67 */   private boolean featureSecureProcessing = false;
/*     */ 
/*  69 */   private boolean useServicesMechanism = true;
/*     */ 
/* 237 */   static DocumentBuilderFactory dbf = null;
/* 238 */   static DocumentBuilder db = null;
/* 239 */   static Document d = null;
/*     */ 
/*     */   protected XPathExpressionImpl()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected XPathExpressionImpl(XPath xpath, JAXPPrefixResolver prefixResolver, XPathFunctionResolver functionResolver, XPathVariableResolver variableResolver)
/*     */   {
/*  79 */     this.xpath = xpath;
/*  80 */     this.prefixResolver = prefixResolver;
/*  81 */     this.functionResolver = functionResolver;
/*  82 */     this.variableResolver = variableResolver;
/*  83 */     this.featureSecureProcessing = false;
/*     */   }
/*     */ 
/*     */   protected XPathExpressionImpl(XPath xpath, JAXPPrefixResolver prefixResolver, XPathFunctionResolver functionResolver, XPathVariableResolver variableResolver, boolean featureSecureProcessing, boolean useServicesMechanism)
/*     */   {
/*  91 */     this.xpath = xpath;
/*  92 */     this.prefixResolver = prefixResolver;
/*  93 */     this.functionResolver = functionResolver;
/*  94 */     this.variableResolver = variableResolver;
/*  95 */     this.featureSecureProcessing = featureSecureProcessing;
/*  96 */     this.useServicesMechanism = useServicesMechanism;
/*     */   }
/*     */ 
/*     */   public void setXPath(XPath xpath) {
/* 100 */     this.xpath = xpath;
/*     */   }
/*     */ 
/*     */   public Object eval(Object item, QName returnType) throws TransformerException
/*     */   {
/* 105 */     XObject resultObject = eval(item);
/* 106 */     return getResultAsType(resultObject, returnType);
/*     */   }
/*     */ 
/*     */   private XObject eval(Object contextItem) throws TransformerException
/*     */   {
/* 111 */     XPathContext xpathSupport = null;
/* 112 */     if (this.functionResolver != null) {
/* 113 */       JAXPExtensionsProvider jep = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing);
/*     */ 
/* 115 */       xpathSupport = new XPathContext(jep);
/*     */     } else {
/* 117 */       xpathSupport = new XPathContext();
/*     */     }
/*     */ 
/* 120 */     xpathSupport.setVarStack(new JAXPVariableStack(this.variableResolver));
/* 121 */     XObject xobj = null;
/*     */ 
/* 123 */     Node contextNode = (Node)contextItem;
/*     */ 
/* 128 */     if (contextNode == null)
/* 129 */       xobj = this.xpath.execute(xpathSupport, -1, this.prefixResolver);
/*     */     else {
/* 131 */       xobj = this.xpath.execute(xpathSupport, contextNode, this.prefixResolver);
/*     */     }
/* 133 */     return xobj;
/*     */   }
/*     */ 
/*     */   public Object evaluate(Object item, QName returnType)
/*     */     throws XPathExpressionException
/*     */   {
/* 171 */     if (returnType == null)
/*     */     {
/* 173 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
/*     */ 
/* 176 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 180 */     if (!isSupported(returnType)) {
/* 181 */       String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
/*     */ 
/* 184 */       throw new IllegalArgumentException(fmsg);
/*     */     }
/*     */     try {
/* 187 */       return eval(item, returnType);
/*     */     }
/*     */     catch (NullPointerException npe)
/*     */     {
/* 192 */       throw new XPathExpressionException(npe);
/*     */     } catch (TransformerException te) {
/* 194 */       Throwable nestedException = te.getException();
/* 195 */       if ((nestedException instanceof XPathFunctionException)) {
/* 196 */         throw ((XPathFunctionException)nestedException);
/*     */       }
/*     */ 
/* 200 */       throw new XPathExpressionException(te);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String evaluate(Object item)
/*     */     throws XPathExpressionException
/*     */   {
/* 232 */     return (String)evaluate(item, XPathConstants.STRING);
/*     */   }
/*     */ 
/*     */   public Object evaluate(InputSource source, QName returnType)
/*     */     throws XPathExpressionException
/*     */   {
/* 277 */     if ((source == null) || (returnType == null)) {
/* 278 */       String fmsg = XSLMessages.createXPATHMessage("ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL", null);
/*     */ 
/* 281 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 285 */     if (!isSupported(returnType)) {
/* 286 */       String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
/*     */ 
/* 289 */       throw new IllegalArgumentException(fmsg);
/*     */     }
/*     */     try {
/* 292 */       if (dbf == null) {
/* 293 */         dbf = FactoryImpl.getDOMFactory(this.useServicesMechanism);
/* 294 */         dbf.setNamespaceAware(true);
/* 295 */         dbf.setValidating(false);
/*     */       }
/* 297 */       db = dbf.newDocumentBuilder();
/* 298 */       Document document = db.parse(source);
/* 299 */       return eval(document, returnType);
/*     */     } catch (Exception e) {
/* 301 */       throw new XPathExpressionException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String evaluate(InputSource source)
/*     */     throws XPathExpressionException
/*     */   {
/* 328 */     return (String)evaluate(source, XPathConstants.STRING);
/*     */   }
/*     */ 
/*     */   private boolean isSupported(QName returnType)
/*     */   {
/* 333 */     if ((returnType.equals(XPathConstants.STRING)) || (returnType.equals(XPathConstants.NUMBER)) || (returnType.equals(XPathConstants.BOOLEAN)) || (returnType.equals(XPathConstants.NODE)) || (returnType.equals(XPathConstants.NODESET)))
/*     */     {
/* 339 */       return true;
/*     */     }
/* 341 */     return false;
/*     */   }
/*     */ 
/*     */   private Object getResultAsType(XObject resultObject, QName returnType)
/*     */     throws TransformerException
/*     */   {
/* 347 */     if (returnType.equals(XPathConstants.STRING)) {
/* 348 */       return resultObject.str();
/*     */     }
/*     */ 
/* 351 */     if (returnType.equals(XPathConstants.NUMBER)) {
/* 352 */       return new Double(resultObject.num());
/*     */     }
/*     */ 
/* 355 */     if (returnType.equals(XPathConstants.BOOLEAN)) {
/* 356 */       return new Boolean(resultObject.bool());
/*     */     }
/*     */ 
/* 359 */     if (returnType.equals(XPathConstants.NODESET)) {
/* 360 */       return resultObject.nodelist();
/*     */     }
/*     */ 
/* 363 */     if (returnType.equals(XPathConstants.NODE)) {
/* 364 */       NodeIterator ni = resultObject.nodeset();
/*     */ 
/* 366 */       return ni.nextNode();
/*     */     }
/*     */ 
/* 370 */     String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
/*     */ 
/* 373 */     throw new IllegalArgumentException(fmsg);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.jaxp.XPathExpressionImpl
 * JD-Core Version:    0.6.2
 */