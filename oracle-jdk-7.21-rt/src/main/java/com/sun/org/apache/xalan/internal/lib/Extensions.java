/*     */ package com.sun.org.apache.xalan.internal.lib;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
/*     */ import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
/*     */ import com.sun.org.apache.xalan.internal.xslt.EnvironmentCheck;
/*     */ import com.sun.org.apache.xml.internal.utils.Hashtree2Node;
/*     */ import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
/*     */ import com.sun.org.apache.xpath.internal.NodeSet;
/*     */ import com.sun.org.apache.xpath.internal.objects.XBoolean;
/*     */ import com.sun.org.apache.xpath.internal.objects.XNumber;
/*     */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.DocumentFragment;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
/*     */ import org.w3c.dom.traversal.NodeIterator;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ 
/*     */ public class Extensions
/*     */ {
/*     */   public static NodeSet nodeset(ExpressionContext myProcessor, Object rtf)
/*     */   {
/*  92 */     if ((rtf instanceof NodeIterator))
/*     */     {
/*  94 */       return new NodeSet((NodeIterator)rtf);
/*     */     }
/*     */     String textNodeValue;
/*     */     String textNodeValue;
/*  98 */     if ((rtf instanceof String))
/*     */     {
/* 100 */       textNodeValue = (String)rtf;
/*     */     }
/*     */     else
/*     */     {
/*     */       String textNodeValue;
/* 102 */       if ((rtf instanceof Boolean))
/*     */       {
/* 104 */         textNodeValue = new XBoolean(((Boolean)rtf).booleanValue()).str();
/*     */       }
/*     */       else
/*     */       {
/*     */         String textNodeValue;
/* 106 */         if ((rtf instanceof Double))
/*     */         {
/* 108 */           textNodeValue = new XNumber(((Double)rtf).doubleValue()).str();
/*     */         }
/*     */         else
/*     */         {
/* 112 */           textNodeValue = rtf.toString();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 119 */       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 120 */       DocumentBuilder db = dbf.newDocumentBuilder();
/* 121 */       Document myDoc = db.newDocument();
/*     */ 
/* 123 */       Text textNode = myDoc.createTextNode(textNodeValue);
/* 124 */       DocumentFragment docFrag = myDoc.createDocumentFragment();
/*     */ 
/* 126 */       docFrag.appendChild(textNode);
/*     */ 
/* 128 */       return new NodeSet(docFrag);
/*     */     }
/*     */     catch (ParserConfigurationException pce)
/*     */     {
/* 132 */       throw new WrappedRuntimeException(pce);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static NodeList intersection(NodeList nl1, NodeList nl2)
/*     */   {
/* 150 */     return ExsltSets.intersection(nl1, nl2);
/*     */   }
/*     */ 
/*     */   public static NodeList difference(NodeList nl1, NodeList nl2)
/*     */   {
/* 166 */     return ExsltSets.difference(nl1, nl2);
/*     */   }
/*     */ 
/*     */   public static NodeList distinct(NodeList nl)
/*     */   {
/* 183 */     return ExsltSets.distinct(nl);
/*     */   }
/*     */ 
/*     */   public static boolean hasSameNodes(NodeList nl1, NodeList nl2)
/*     */   {
/* 196 */     NodeSet ns1 = new NodeSet(nl1);
/* 197 */     NodeSet ns2 = new NodeSet(nl2);
/*     */ 
/* 199 */     if (ns1.getLength() != ns2.getLength()) {
/* 200 */       return false;
/*     */     }
/* 202 */     for (int i = 0; i < ns1.getLength(); i++)
/*     */     {
/* 204 */       Node n = ns1.elementAt(i);
/*     */ 
/* 206 */       if (!ns2.contains(n)) {
/* 207 */         return false;
/*     */       }
/*     */     }
/* 210 */     return true;
/*     */   }
/*     */ 
/*     */   public static XObject evaluate(ExpressionContext myContext, String xpathExpr)
/*     */     throws SAXNotSupportedException
/*     */   {
/* 233 */     return ExsltDynamic.evaluate(myContext, xpathExpr);
/*     */   }
/*     */ 
/*     */   public static NodeList tokenize(String toTokenize, String delims)
/*     */   {
/* 252 */     Document doc = DocumentHolder.m_doc;
/*     */ 
/* 255 */     StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
/* 256 */     NodeSet resultSet = new NodeSet();
/*     */ 
/* 258 */     synchronized (doc)
/*     */     {
/* 260 */       while (lTokenizer.hasMoreTokens())
/*     */       {
/* 262 */         resultSet.addNode(doc.createTextNode(lTokenizer.nextToken()));
/*     */       }
/*     */     }
/*     */ 
/* 266 */     return resultSet;
/*     */   }
/*     */ 
/*     */   public static NodeList tokenize(String toTokenize)
/*     */   {
/* 284 */     return tokenize(toTokenize, " \t\n\r");
/*     */   }
/*     */ 
/*     */   public static Node checkEnvironment(ExpressionContext myContext)
/*     */   {
/*     */     Document factoryDocument;
/*     */     try
/*     */     {
/* 314 */       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/* 315 */       DocumentBuilder db = dbf.newDocumentBuilder();
/* 316 */       factoryDocument = db.newDocument();
/*     */     }
/*     */     catch (ParserConfigurationException pce)
/*     */     {
/* 320 */       throw new WrappedRuntimeException(pce);
/*     */     }
/*     */ 
/* 323 */     Node resultNode = null;
/*     */     try
/*     */     {
/* 328 */       resultNode = checkEnvironmentUsingWhich(myContext, factoryDocument);
/*     */ 
/* 330 */       if (null != resultNode) {
/* 331 */         return resultNode;
/*     */       }
/*     */ 
/* 334 */       EnvironmentCheck envChecker = new EnvironmentCheck();
/* 335 */       Hashtable h = envChecker.getEnvironmentHash();
/* 336 */       resultNode = factoryDocument.createElement("checkEnvironmentExtension");
/* 337 */       envChecker.appendEnvironmentReport(resultNode, factoryDocument, h);
/* 338 */       envChecker = null;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 342 */       throw new WrappedRuntimeException(e);
/*     */     }
/*     */ 
/* 345 */     return resultNode;
/*     */   }
/*     */ 
/*     */   private static Node checkEnvironmentUsingWhich(ExpressionContext myContext, Document factoryDocument)
/*     */   {
/* 359 */     String WHICH_CLASSNAME = "org.apache.env.Which";
/* 360 */     String WHICH_METHODNAME = "which";
/* 361 */     Class[] WHICH_METHOD_ARGS = { Hashtable.class, String.class, String.class };
/*     */     try
/*     */     {
/* 367 */       Class clazz = ObjectFactory.findProviderClass("org.apache.env.Which", true);
/* 368 */       if (null == clazz) {
/* 369 */         return null;
/*     */       }
/*     */ 
/* 372 */       Method method = clazz.getMethod("which", WHICH_METHOD_ARGS);
/* 373 */       Hashtable report = new Hashtable();
/*     */ 
/* 376 */       Object[] methodArgs = { report, "XmlCommons;Xalan;Xerces;Crimson;Ant", "" };
/* 377 */       Object returnValue = method.invoke(null, methodArgs);
/*     */ 
/* 380 */       Node resultNode = factoryDocument.createElement("checkEnvironmentExtension");
/* 381 */       Hashtree2Node.appendHashToNode(report, "whichReport", resultNode, factoryDocument);
/*     */ 
/* 384 */       return resultNode;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/* 389 */     return null;
/*     */   }
/*     */ 
/*     */   private static class DocumentHolder
/*     */   {
/*     */     private static final Document m_doc;
/*     */ 
/*     */     static
/*     */     {
/*     */       try
/*     */       {
/* 410 */         m_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
/*     */       }
/*     */       catch (ParserConfigurationException pce)
/*     */       {
/* 415 */         throw new WrappedRuntimeException(pce);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.lib.Extensions
 * JD-Core Version:    0.6.2
 */