/*     */ package com.sun.org.apache.xpath.internal.jaxp;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.res.XSLMessages;
/*     */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*     */ import com.sun.org.apache.xpath.internal.XPathContext;
/*     */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.NamespaceContext;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
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
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class XPathImpl
/*     */   implements javax.xml.xpath.XPath
/*     */ {
/*     */   private XPathVariableResolver variableResolver;
/*     */   private XPathFunctionResolver functionResolver;
/*     */   private XPathVariableResolver origVariableResolver;
/*     */   private XPathFunctionResolver origFunctionResolver;
/*  66 */   private NamespaceContext namespaceContext = null;
/*     */   private JAXPPrefixResolver prefixResolver;
/*  71 */   private boolean featureSecureProcessing = false;
/*  72 */   private boolean useServiceMechanism = true;
/*     */ 
/* 160 */   private static Document d = null;
/*     */ 
/*     */   XPathImpl(XPathVariableResolver vr, XPathFunctionResolver fr)
/*     */   {
/*  75 */     this.origVariableResolver = (this.variableResolver = vr);
/*  76 */     this.origFunctionResolver = (this.functionResolver = fr);
/*     */   }
/*     */ 
/*     */   XPathImpl(XPathVariableResolver vr, XPathFunctionResolver fr, boolean featureSecureProcessing, boolean useServiceMechanism)
/*     */   {
/*  81 */     this.origVariableResolver = (this.variableResolver = vr);
/*  82 */     this.origFunctionResolver = (this.functionResolver = fr);
/*  83 */     this.featureSecureProcessing = featureSecureProcessing;
/*  84 */     this.useServiceMechanism = useServiceMechanism;
/*     */   }
/*     */ 
/*     */   public void setXPathVariableResolver(XPathVariableResolver resolver)
/*     */   {
/*  93 */     if (resolver == null) {
/*  94 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathVariableResolver" });
/*     */ 
/*  97 */       throw new NullPointerException(fmsg);
/*     */     }
/*  99 */     this.variableResolver = resolver;
/*     */   }
/*     */ 
/*     */   public XPathVariableResolver getXPathVariableResolver()
/*     */   {
/* 108 */     return this.variableResolver;
/*     */   }
/*     */ 
/*     */   public void setXPathFunctionResolver(XPathFunctionResolver resolver)
/*     */   {
/* 117 */     if (resolver == null) {
/* 118 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathFunctionResolver" });
/*     */ 
/* 121 */       throw new NullPointerException(fmsg);
/*     */     }
/* 123 */     this.functionResolver = resolver;
/*     */   }
/*     */ 
/*     */   public XPathFunctionResolver getXPathFunctionResolver()
/*     */   {
/* 132 */     return this.functionResolver;
/*     */   }
/*     */ 
/*     */   public void setNamespaceContext(NamespaceContext nsContext)
/*     */   {
/* 141 */     if (nsContext == null) {
/* 142 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "NamespaceContext" });
/*     */ 
/* 145 */       throw new NullPointerException(fmsg);
/*     */     }
/* 147 */     this.namespaceContext = nsContext;
/* 148 */     this.prefixResolver = new JAXPPrefixResolver(nsContext);
/*     */   }
/*     */ 
/*     */   public NamespaceContext getNamespaceContext()
/*     */   {
/* 157 */     return this.namespaceContext;
/*     */   }
/*     */ 
/*     */   private DocumentBuilder getParser()
/*     */   {
/*     */     try
/*     */     {
/* 175 */       DocumentBuilderFactory dbf = FactoryImpl.getDOMFactory(this.useServiceMechanism);
/* 176 */       dbf.setNamespaceAware(true);
/* 177 */       dbf.setValidating(false);
/* 178 */       return dbf.newDocumentBuilder();
/*     */     }
/*     */     catch (ParserConfigurationException e) {
/* 181 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private XObject eval(String expression, Object contextItem)
/*     */     throws TransformerException
/*     */   {
/* 188 */     com.sun.org.apache.xpath.internal.XPath xpath = new com.sun.org.apache.xpath.internal.XPath(expression, null, this.prefixResolver, 0);
/*     */ 
/* 190 */     XPathContext xpathSupport = null;
/* 191 */     if (this.functionResolver != null) {
/* 192 */       JAXPExtensionsProvider jep = new JAXPExtensionsProvider(this.functionResolver, this.featureSecureProcessing);
/*     */ 
/* 194 */       xpathSupport = new XPathContext(jep);
/*     */     } else {
/* 196 */       xpathSupport = new XPathContext();
/*     */     }
/*     */ 
/* 199 */     XObject xobj = null;
/*     */ 
/* 201 */     xpathSupport.setVarStack(new JAXPVariableStack(this.variableResolver));
/*     */ 
/* 204 */     if ((contextItem instanceof Node)) {
/* 205 */       xobj = xpath.execute(xpathSupport, (Node)contextItem, this.prefixResolver);
/*     */     }
/*     */     else {
/* 208 */       xobj = xpath.execute(xpathSupport, -1, this.prefixResolver);
/*     */     }
/*     */ 
/* 211 */     return xobj;
/*     */   }
/*     */ 
/*     */   public Object evaluate(String expression, Object item, QName returnType)
/*     */     throws XPathExpressionException
/*     */   {
/* 247 */     if (expression == null) {
/* 248 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
/*     */ 
/* 251 */       throw new NullPointerException(fmsg);
/*     */     }
/* 253 */     if (returnType == null) {
/* 254 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
/*     */ 
/* 257 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 261 */     if (!isSupported(returnType)) {
/* 262 */       String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
/*     */ 
/* 265 */       throw new IllegalArgumentException(fmsg);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 270 */       XObject resultObject = eval(expression, item);
/* 271 */       return getResultAsType(resultObject, returnType);
/*     */     }
/*     */     catch (NullPointerException npe)
/*     */     {
/* 276 */       throw new XPathExpressionException(npe);
/*     */     } catch (TransformerException te) {
/* 278 */       Throwable nestedException = te.getException();
/* 279 */       if ((nestedException instanceof XPathFunctionException)) {
/* 280 */         throw ((XPathFunctionException)nestedException);
/*     */       }
/*     */ 
/* 284 */       throw new XPathExpressionException(te);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isSupported(QName returnType)
/*     */   {
/* 291 */     if ((returnType.equals(XPathConstants.STRING)) || (returnType.equals(XPathConstants.NUMBER)) || (returnType.equals(XPathConstants.BOOLEAN)) || (returnType.equals(XPathConstants.NODE)) || (returnType.equals(XPathConstants.NODESET)))
/*     */     {
/* 297 */       return true;
/*     */     }
/* 299 */     return false;
/*     */   }
/*     */ 
/*     */   private Object getResultAsType(XObject resultObject, QName returnType)
/*     */     throws TransformerException
/*     */   {
/* 305 */     if (returnType.equals(XPathConstants.STRING)) {
/* 306 */       return resultObject.str();
/*     */     }
/*     */ 
/* 309 */     if (returnType.equals(XPathConstants.NUMBER)) {
/* 310 */       return new Double(resultObject.num());
/*     */     }
/*     */ 
/* 313 */     if (returnType.equals(XPathConstants.BOOLEAN)) {
/* 314 */       return new Boolean(resultObject.bool());
/*     */     }
/*     */ 
/* 317 */     if (returnType.equals(XPathConstants.NODESET)) {
/* 318 */       return resultObject.nodelist();
/*     */     }
/*     */ 
/* 321 */     if (returnType.equals(XPathConstants.NODE)) {
/* 322 */       NodeIterator ni = resultObject.nodeset();
/*     */ 
/* 324 */       return ni.nextNode();
/*     */     }
/* 326 */     String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
/*     */ 
/* 329 */     throw new IllegalArgumentException(fmsg);
/*     */   }
/*     */ 
/*     */   public String evaluate(String expression, Object item)
/*     */     throws XPathExpressionException
/*     */   {
/* 360 */     return (String)evaluate(expression, item, XPathConstants.STRING);
/*     */   }
/*     */ 
/*     */   public XPathExpression compile(String expression)
/*     */     throws XPathExpressionException
/*     */   {
/* 382 */     if (expression == null) {
/* 383 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
/*     */ 
/* 386 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */     try {
/* 389 */       com.sun.org.apache.xpath.internal.XPath xpath = new com.sun.org.apache.xpath.internal.XPath(expression, null, this.prefixResolver, 0);
/*     */ 
/* 392 */       return new XPathExpressionImpl(xpath, this.prefixResolver, this.functionResolver, this.variableResolver, this.featureSecureProcessing, this.useServiceMechanism);
/*     */     }
/*     */     catch (TransformerException te)
/*     */     {
/* 397 */       throw new XPathExpressionException(te);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object evaluate(String expression, InputSource source, QName returnType)
/*     */     throws XPathExpressionException
/*     */   {
/* 433 */     if (source == null) {
/* 434 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "source" });
/*     */ 
/* 437 */       throw new NullPointerException(fmsg);
/*     */     }
/* 439 */     if (expression == null) {
/* 440 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
/*     */ 
/* 443 */       throw new NullPointerException(fmsg);
/*     */     }
/* 445 */     if (returnType == null) {
/* 446 */       String fmsg = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
/*     */ 
/* 449 */       throw new NullPointerException(fmsg);
/*     */     }
/*     */ 
/* 454 */     if (!isSupported(returnType)) {
/* 455 */       String fmsg = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { returnType.toString() });
/*     */ 
/* 458 */       throw new IllegalArgumentException(fmsg);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 463 */       Document document = getParser().parse(source);
/*     */ 
/* 465 */       XObject resultObject = eval(expression, document);
/* 466 */       return getResultAsType(resultObject, returnType);
/*     */     } catch (SAXException e) {
/* 468 */       throw new XPathExpressionException(e);
/*     */     } catch (IOException e) {
/* 470 */       throw new XPathExpressionException(e);
/*     */     } catch (TransformerException te) {
/* 472 */       Throwable nestedException = te.getException();
/* 473 */       if ((nestedException instanceof XPathFunctionException)) {
/* 474 */         throw ((XPathFunctionException)nestedException);
/*     */       }
/* 476 */       throw new XPathExpressionException(te);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String evaluate(String expression, InputSource source)
/*     */     throws XPathExpressionException
/*     */   {
/* 510 */     return (String)evaluate(expression, source, XPathConstants.STRING);
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 529 */     this.variableResolver = this.origVariableResolver;
/* 530 */     this.functionResolver = this.origFunctionResolver;
/* 531 */     this.namespaceContext = null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xpath.internal.jaxp.XPathImpl
 * JD-Core Version:    0.6.2
 */