/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.utils.IdResolver;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.XMLStructure;
/*     */ import javax.xml.crypto.dom.DOMStructure;
/*     */ import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
/*     */ import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
/*     */ import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
/*     */ import javax.xml.crypto.dsig.spec.XPathType;
/*     */ import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class DOMUtils
/*     */ {
/*     */   public static Document getOwnerDocument(Node paramNode)
/*     */   {
/*  60 */     if (paramNode.getNodeType() == 9) {
/*  61 */       return (Document)paramNode;
/*     */     }
/*  63 */     return paramNode.getOwnerDocument();
/*     */   }
/*     */ 
/*     */   public static Element createElement(Document paramDocument, String paramString1, String paramString2, String paramString3)
/*     */   {
/*  79 */     String str = paramString3 + ":" + paramString1;
/*     */ 
/*  81 */     return paramDocument.createElementNS(paramString2, str);
/*     */   }
/*     */ 
/*     */   public static void setAttribute(Element paramElement, String paramString1, String paramString2)
/*     */   {
/*  93 */     if (paramString2 == null) return;
/*  94 */     paramElement.setAttributeNS(null, paramString1, paramString2);
/*     */   }
/*     */ 
/*     */   public static void setAttributeID(Element paramElement, String paramString1, String paramString2)
/*     */   {
/* 108 */     if (paramString2 == null) return;
/* 109 */     paramElement.setAttributeNS(null, paramString1, paramString2);
/* 110 */     IdResolver.registerElementById(paramElement, paramString2);
/*     */   }
/*     */ 
/*     */   public static Element getFirstChildElement(Node paramNode)
/*     */   {
/* 123 */     Node localNode = paramNode.getFirstChild();
/* 124 */     while ((localNode != null) && (localNode.getNodeType() != 1)) {
/* 125 */       localNode = localNode.getNextSibling();
/*     */     }
/* 127 */     return (Element)localNode;
/*     */   }
/*     */ 
/*     */   public static Element getLastChildElement(Node paramNode)
/*     */   {
/* 140 */     Node localNode = paramNode.getLastChild();
/* 141 */     while ((localNode != null) && (localNode.getNodeType() != 1)) {
/* 142 */       localNode = localNode.getPreviousSibling();
/*     */     }
/* 144 */     return (Element)localNode;
/*     */   }
/*     */ 
/*     */   public static Element getNextSiblingElement(Node paramNode)
/*     */   {
/* 157 */     Node localNode = paramNode.getNextSibling();
/* 158 */     while ((localNode != null) && (localNode.getNodeType() != 1)) {
/* 159 */       localNode = localNode.getNextSibling();
/*     */     }
/* 161 */     return (Element)localNode;
/*     */   }
/*     */ 
/*     */   public static String getAttributeValue(Element paramElement, String paramString)
/*     */   {
/* 179 */     Attr localAttr = paramElement.getAttributeNodeNS(null, paramString);
/* 180 */     return localAttr == null ? null : localAttr.getValue();
/*     */   }
/*     */ 
/*     */   public static Set nodeSet(NodeList paramNodeList)
/*     */   {
/* 191 */     return new NodeSet(paramNodeList);
/*     */   }
/*     */ 
/*     */   public static String getNSPrefix(XMLCryptoContext paramXMLCryptoContext, String paramString)
/*     */   {
/* 230 */     if (paramXMLCryptoContext != null) {
/* 231 */       return paramXMLCryptoContext.getNamespacePrefix(paramString, paramXMLCryptoContext.getDefaultNamespacePrefix());
/*     */     }
/*     */ 
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */   public static String getSignaturePrefix(XMLCryptoContext paramXMLCryptoContext)
/*     */   {
/* 246 */     return getNSPrefix(paramXMLCryptoContext, "http://www.w3.org/2000/09/xmldsig#");
/*     */   }
/*     */ 
/*     */   public static void removeAllChildren(Node paramNode)
/*     */   {
/* 255 */     NodeList localNodeList = paramNode.getChildNodes();
/* 256 */     int i = 0; for (int j = localNodeList.getLength(); i < j; i++)
/* 257 */       paramNode.removeChild(localNodeList.item(i));
/*     */   }
/*     */ 
/*     */   public static boolean nodesEqual(Node paramNode1, Node paramNode2)
/*     */   {
/* 265 */     if (paramNode1 == paramNode2) {
/* 266 */       return true;
/*     */     }
/* 268 */     if (paramNode1.getNodeType() != paramNode2.getNodeType()) {
/* 269 */       return false;
/*     */     }
/*     */ 
/* 272 */     return true;
/*     */   }
/*     */ 
/*     */   public static void appendChild(Node paramNode1, Node paramNode2)
/*     */   {
/* 281 */     Document localDocument = getOwnerDocument(paramNode1);
/* 282 */     if (paramNode2.getOwnerDocument() != localDocument)
/* 283 */       paramNode1.appendChild(localDocument.importNode(paramNode2, true));
/*     */     else
/* 285 */       paramNode1.appendChild(paramNode2);
/*     */   }
/*     */ 
/*     */   public static boolean paramsEqual(AlgorithmParameterSpec paramAlgorithmParameterSpec1, AlgorithmParameterSpec paramAlgorithmParameterSpec2)
/*     */   {
/* 291 */     if (paramAlgorithmParameterSpec1 == paramAlgorithmParameterSpec2) {
/* 292 */       return true;
/*     */     }
/* 294 */     if (((paramAlgorithmParameterSpec1 instanceof XPathFilter2ParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof XPathFilter2ParameterSpec)))
/*     */     {
/* 296 */       return paramsEqual((XPathFilter2ParameterSpec)paramAlgorithmParameterSpec1, (XPathFilter2ParameterSpec)paramAlgorithmParameterSpec2);
/*     */     }
/*     */ 
/* 299 */     if (((paramAlgorithmParameterSpec1 instanceof ExcC14NParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof ExcC14NParameterSpec)))
/*     */     {
/* 301 */       return paramsEqual((ExcC14NParameterSpec)paramAlgorithmParameterSpec1, (ExcC14NParameterSpec)paramAlgorithmParameterSpec2);
/*     */     }
/*     */ 
/* 304 */     if (((paramAlgorithmParameterSpec1 instanceof XPathFilterParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof XPathFilterParameterSpec)))
/*     */     {
/* 306 */       return paramsEqual((XPathFilterParameterSpec)paramAlgorithmParameterSpec1, (XPathFilterParameterSpec)paramAlgorithmParameterSpec2);
/*     */     }
/*     */ 
/* 309 */     if (((paramAlgorithmParameterSpec1 instanceof XSLTTransformParameterSpec)) && ((paramAlgorithmParameterSpec2 instanceof XSLTTransformParameterSpec)))
/*     */     {
/* 311 */       return paramsEqual((XSLTTransformParameterSpec)paramAlgorithmParameterSpec1, (XSLTTransformParameterSpec)paramAlgorithmParameterSpec2);
/*     */     }
/*     */ 
/* 314 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean paramsEqual(XPathFilter2ParameterSpec paramXPathFilter2ParameterSpec1, XPathFilter2ParameterSpec paramXPathFilter2ParameterSpec2)
/*     */   {
/* 320 */     List localList1 = paramXPathFilter2ParameterSpec1.getXPathList();
/* 321 */     List localList2 = paramXPathFilter2ParameterSpec2.getXPathList();
/* 322 */     int i = localList1.size();
/* 323 */     if (i != localList2.size()) {
/* 324 */       return false;
/*     */     }
/* 326 */     for (int j = 0; j < i; j++) {
/* 327 */       XPathType localXPathType1 = (XPathType)localList1.get(j);
/* 328 */       XPathType localXPathType2 = (XPathType)localList2.get(j);
/* 329 */       if ((!localXPathType1.getExpression().equals(localXPathType2.getExpression())) || (!localXPathType1.getNamespaceMap().equals(localXPathType2.getNamespaceMap())) || (localXPathType1.getFilter() != localXPathType2.getFilter()))
/*     */       {
/* 332 */         return false;
/*     */       }
/*     */     }
/* 335 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean paramsEqual(ExcC14NParameterSpec paramExcC14NParameterSpec1, ExcC14NParameterSpec paramExcC14NParameterSpec2)
/*     */   {
/* 340 */     return paramExcC14NParameterSpec1.getPrefixList().equals(paramExcC14NParameterSpec2.getPrefixList());
/*     */   }
/*     */ 
/*     */   private static boolean paramsEqual(XPathFilterParameterSpec paramXPathFilterParameterSpec1, XPathFilterParameterSpec paramXPathFilterParameterSpec2)
/*     */   {
/* 345 */     return (paramXPathFilterParameterSpec1.getXPath().equals(paramXPathFilterParameterSpec2.getXPath())) && (paramXPathFilterParameterSpec1.getNamespaceMap().equals(paramXPathFilterParameterSpec2.getNamespaceMap()));
/*     */   }
/*     */ 
/*     */   private static boolean paramsEqual(XSLTTransformParameterSpec paramXSLTTransformParameterSpec1, XSLTTransformParameterSpec paramXSLTTransformParameterSpec2)
/*     */   {
/* 352 */     XMLStructure localXMLStructure1 = paramXSLTTransformParameterSpec2.getStylesheet();
/* 353 */     if (!(localXMLStructure1 instanceof DOMStructure)) {
/* 354 */       return false;
/*     */     }
/* 356 */     Node localNode1 = ((DOMStructure)localXMLStructure1).getNode();
/*     */ 
/* 358 */     XMLStructure localXMLStructure2 = paramXSLTTransformParameterSpec1.getStylesheet();
/* 359 */     Node localNode2 = ((DOMStructure)localXMLStructure2).getNode();
/*     */ 
/* 361 */     return nodesEqual(localNode2, localNode1);
/*     */   }
/*     */ 
/*     */   static class NodeSet extends AbstractSet
/*     */   {
/*     */     private NodeList nl;
/*     */ 
/*     */     public NodeSet(NodeList paramNodeList)
/*     */     {
/* 197 */       this.nl = paramNodeList;
/*     */     }
/*     */     public int size() {
/* 200 */       return this.nl.getLength();
/*     */     }
/* 202 */     public Iterator iterator() { return new Iterator() {
/* 203 */         int index = 0;
/*     */ 
/*     */         public void remove() {
/* 206 */           throw new UnsupportedOperationException();
/*     */         }
/*     */         public Object next() {
/* 209 */           if (!hasNext()) {
/* 210 */             throw new NoSuchElementException();
/*     */           }
/* 212 */           return DOMUtils.NodeSet.this.nl.item(this.index++);
/*     */         }
/*     */         public boolean hasNext() {
/* 215 */           return this.index < DOMUtils.NodeSet.this.nl.getLength();
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMUtils
 * JD-Core Version:    0.6.2
 */