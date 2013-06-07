/*     */ package com.sun.org.apache.xml.internal.security.utils;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public class XMLUtils
/*     */ {
/*  55 */   private static boolean ignoreLineBreaks = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Boolean run() {
/*  58 */       return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.ignoreLineBreaks"));
/*     */     }
/*     */   })).booleanValue();
/*     */ 
/* 232 */   static String dsPrefix = null;
/* 233 */   static Map namePrefixes = new HashMap();
/*     */ 
/*     */   public static Element getNextElement(Node paramNode)
/*     */   {
/*  72 */     while ((paramNode != null) && (paramNode.getNodeType() != 1)) {
/*  73 */       paramNode = paramNode.getNextSibling();
/*     */     }
/*  75 */     return (Element)paramNode;
/*     */   }
/*     */ 
/*     */   public static void getSet(Node paramNode1, Set paramSet, Node paramNode2, boolean paramBoolean)
/*     */   {
/*  86 */     if ((paramNode2 != null) && (isDescendantOrSelf(paramNode2, paramNode1))) {
/*  87 */       return;
/*     */     }
/*  89 */     getSetRec(paramNode1, paramSet, paramNode2, paramBoolean);
/*     */   }
/*     */ 
/*     */   static final void getSetRec(Node paramNode1, Set paramSet, Node paramNode2, boolean paramBoolean)
/*     */   {
/*  94 */     if (paramNode1 == paramNode2)
/*     */       return;
/*     */     Object localObject;
/*  97 */     switch (paramNode1.getNodeType()) {
/*     */     case 1:
/*  99 */       paramSet.add(paramNode1);
/* 100 */       Element localElement = (Element)paramNode1;
/* 101 */       if (localElement.hasAttributes()) {
/* 102 */         localObject = ((Element)paramNode1).getAttributes();
/* 103 */         for (int i = 0; i < ((NamedNodeMap)localObject).getLength(); i++) {
/* 104 */           paramSet.add(((NamedNodeMap)localObject).item(i));
/*     */         }
/*     */       }
/*     */ 
/*     */     case 9:
/* 109 */       for (localObject = paramNode1.getFirstChild(); localObject != null; localObject = ((Node)localObject).getNextSibling()) {
/* 110 */         if (((Node)localObject).getNodeType() == 3) {
/* 111 */           paramSet.add(localObject);
/* 112 */           while ((localObject != null) && (((Node)localObject).getNodeType() == 3)) {
/* 113 */             localObject = ((Node)localObject).getNextSibling();
/*     */           }
/* 115 */           if (localObject == null)
/* 116 */             return;
/*     */         }
/* 118 */         getSetRec((Node)localObject, paramSet, paramNode2, paramBoolean);
/*     */       }
/* 120 */       return;
/*     */     case 8:
/* 122 */       if (paramBoolean) {
/* 123 */         paramSet.add(paramNode1);
/*     */       }
/* 125 */       return;
/*     */     case 10:
/* 127 */       return;
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/* 129 */     case 7: } paramSet.add(paramNode1);
/*     */   }
/*     */ 
/*     */   public static void outputDOM(Node paramNode, OutputStream paramOutputStream)
/*     */   {
/* 142 */     outputDOM(paramNode, paramOutputStream, false);
/*     */   }
/*     */ 
/*     */   public static void outputDOM(Node paramNode, OutputStream paramOutputStream, boolean paramBoolean)
/*     */   {
/*     */     try
/*     */     {
/* 158 */       if (paramBoolean) {
/* 159 */         paramOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
/*     */       }
/*     */ 
/* 162 */       paramOutputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(paramNode));
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */     catch (InvalidCanonicalizerException localInvalidCanonicalizerException) {
/* 168 */       localInvalidCanonicalizerException.printStackTrace();
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 170 */       localCanonicalizationException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void outputDOMc14nWithComments(Node paramNode, OutputStream paramOutputStream)
/*     */   {
/*     */     try
/*     */     {
/* 191 */       paramOutputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(paramNode));
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */     catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
/*     */     {
/*     */     }
/*     */     catch (CanonicalizationException localCanonicalizationException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getFullTextChildrenFromElement(Element paramElement)
/*     */   {
/* 216 */     StringBuffer localStringBuffer = new StringBuffer();
/* 217 */     NodeList localNodeList = paramElement.getChildNodes();
/* 218 */     int i = localNodeList.getLength();
/*     */ 
/* 220 */     for (int j = 0; j < i; j++) {
/* 221 */       Node localNode = localNodeList.item(j);
/*     */ 
/* 223 */       if (localNode.getNodeType() == 3) {
/* 224 */         localStringBuffer.append(((Text)localNode).getData());
/*     */       }
/*     */     }
/*     */ 
/* 228 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   public static Element createElementInSignatureSpace(Document paramDocument, String paramString)
/*     */   {
/* 244 */     if (paramDocument == null) {
/* 245 */       throw new RuntimeException("Document is null");
/*     */     }
/*     */ 
/* 248 */     if ((dsPrefix == null) || (dsPrefix.length() == 0)) {
/* 249 */       return paramDocument.createElementNS("http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */     }
/* 251 */     String str = (String)namePrefixes.get(paramString);
/* 252 */     if (str == null) {
/* 253 */       StringBuffer localStringBuffer = new StringBuffer(dsPrefix);
/* 254 */       localStringBuffer.append(':');
/* 255 */       localStringBuffer.append(paramString);
/* 256 */       str = localStringBuffer.toString();
/* 257 */       namePrefixes.put(paramString, str);
/*     */     }
/* 259 */     return paramDocument.createElementNS("http://www.w3.org/2000/09/xmldsig#", str);
/*     */   }
/*     */ 
/*     */   public static boolean elementIsInSignatureSpace(Element paramElement, String paramString)
/*     */   {
/* 272 */     return ElementProxy.checker.isNamespaceElement(paramElement, paramString, "http://www.w3.org/2000/09/xmldsig#");
/*     */   }
/*     */ 
/*     */   public static boolean elementIsInEncryptionSpace(Element paramElement, String paramString)
/*     */   {
/* 285 */     return ElementProxy.checker.isNamespaceElement(paramElement, paramString, "http://www.w3.org/2001/04/xmlenc#");
/*     */   }
/*     */ 
/*     */   public static Document getOwnerDocument(Node paramNode)
/*     */   {
/* 299 */     if (paramNode.getNodeType() == 9)
/* 300 */       return (Document)paramNode;
/*     */     try
/*     */     {
/* 303 */       return paramNode.getOwnerDocument();
/*     */     } catch (NullPointerException localNullPointerException) {
/* 305 */       throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + localNullPointerException.getMessage() + "\"");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Document getOwnerDocument(Set paramSet)
/*     */   {
/* 322 */     Object localObject = null;
/* 323 */     Iterator localIterator = paramSet.iterator();
/* 324 */     while (localIterator.hasNext()) {
/* 325 */       Node localNode = (Node)localIterator.next();
/* 326 */       int i = localNode.getNodeType();
/* 327 */       if (i == 9)
/* 328 */         return (Document)localNode;
/*     */       try
/*     */       {
/* 331 */         if (i == 2) {
/* 332 */           return ((Attr)localNode).getOwnerElement().getOwnerDocument();
/*     */         }
/* 334 */         return localNode.getOwnerDocument();
/*     */       } catch (NullPointerException localNullPointerException) {
/* 336 */         localObject = localNullPointerException;
/*     */       }
/*     */     }
/*     */ 
/* 340 */     throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + (localObject == null ? "" : localObject.getMessage()) + "\"");
/*     */   }
/*     */ 
/*     */   public static Element createDSctx(Document paramDocument, String paramString1, String paramString2)
/*     */   {
/* 356 */     if ((paramString1 == null) || (paramString1.trim().length() == 0)) {
/* 357 */       throw new IllegalArgumentException("You must supply a prefix");
/*     */     }
/*     */ 
/* 360 */     Element localElement = paramDocument.createElementNS(null, "namespaceContext");
/*     */ 
/* 362 */     localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + paramString1.trim(), paramString2);
/*     */ 
/* 365 */     return localElement;
/*     */   }
/*     */ 
/*     */   public static void addReturnToElement(Element paramElement)
/*     */   {
/* 375 */     if (!ignoreLineBreaks) {
/* 376 */       Document localDocument = paramElement.getOwnerDocument();
/* 377 */       paramElement.appendChild(localDocument.createTextNode("\n"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void addReturnToElement(Document paramDocument, HelperNodeList paramHelperNodeList) {
/* 382 */     if (!ignoreLineBreaks)
/* 383 */       paramHelperNodeList.appendChild(paramDocument.createTextNode("\n"));
/*     */   }
/*     */ 
/*     */   public static void addReturnBeforeChild(Element paramElement, Node paramNode)
/*     */   {
/* 388 */     if (!ignoreLineBreaks) {
/* 389 */       Document localDocument = paramElement.getOwnerDocument();
/* 390 */       paramElement.insertBefore(localDocument.createTextNode("\n"), paramNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Set convertNodelistToSet(NodeList paramNodeList)
/*     */   {
/* 402 */     if (paramNodeList == null) {
/* 403 */       return new HashSet();
/*     */     }
/*     */ 
/* 406 */     int i = paramNodeList.getLength();
/* 407 */     HashSet localHashSet = new HashSet(i);
/*     */ 
/* 409 */     for (int j = 0; j < i; j++) {
/* 410 */       localHashSet.add(paramNodeList.item(j));
/*     */     }
/*     */ 
/* 413 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   public static void circumventBug2650(Document paramDocument)
/*     */   {
/* 430 */     Element localElement = paramDocument.getDocumentElement();
/*     */ 
/* 433 */     Attr localAttr = localElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
/*     */ 
/* 436 */     if (localAttr == null) {
/* 437 */       localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
/*     */     }
/*     */ 
/* 440 */     circumventBug2650internal(paramDocument);
/*     */   }
/*     */ 
/*     */   private static void circumventBug2650internal(Node paramNode)
/*     */   {
/* 450 */     Node localNode1 = null;
/* 451 */     Node localNode2 = null;
/*     */     while (true)
/*     */     {
/* 454 */       switch (paramNode.getNodeType()) {
/*     */       case 1:
/* 456 */         Element localElement1 = (Element)paramNode;
/* 457 */         if (localElement1.hasChildNodes())
/*     */         {
/* 459 */           if (localElement1.hasAttributes()) {
/* 460 */             NamedNodeMap localNamedNodeMap = localElement1.getAttributes();
/* 461 */             int i = localNamedNodeMap.getLength();
/*     */ 
/* 463 */             for (Node localNode3 = localElement1.getFirstChild(); localNode3 != null; 
/* 464 */               localNode3 = localNode3.getNextSibling())
/*     */             {
/* 466 */               if (localNode3.getNodeType() == 1)
/*     */               {
/* 469 */                 Element localElement2 = (Element)localNode3;
/*     */ 
/* 471 */                 for (int j = 0; j < i; j++) {
/* 472 */                   Attr localAttr = (Attr)localNamedNodeMap.item(j);
/* 473 */                   if ("http://www.w3.org/2000/xmlns/" == localAttr.getNamespaceURI())
/*     */                   {
/* 475 */                     if (!localElement2.hasAttributeNS("http://www.w3.org/2000/xmlns/", localAttr.getLocalName()))
/*     */                     {
/* 479 */                       localElement2.setAttributeNS("http://www.w3.org/2000/xmlns/", localAttr.getName(), localAttr.getNodeValue());
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         break;
/*     */       case 5:
/*     */       case 9:
/* 489 */         localNode1 = paramNode;
/* 490 */         localNode2 = paramNode.getFirstChild();
/*     */       }
/*     */ 
/* 493 */       while ((localNode2 == null) && (localNode1 != null)) {
/* 494 */         localNode2 = localNode1.getNextSibling();
/* 495 */         localNode1 = localNode1.getParentNode();
/*     */       }
/* 497 */       if (localNode2 == null) {
/* 498 */         return;
/*     */       }
/*     */ 
/* 501 */       paramNode = localNode2;
/* 502 */       localNode2 = paramNode.getNextSibling();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Element selectDsNode(Node paramNode, String paramString, int paramInt)
/*     */   {
/* 513 */     while (paramNode != null) {
/* 514 */       if (ElementProxy.checker.isNamespaceElement(paramNode, paramString, "http://www.w3.org/2000/09/xmldsig#")) {
/* 515 */         if (paramInt == 0) {
/* 516 */           return (Element)paramNode;
/*     */         }
/* 518 */         paramInt--;
/*     */       }
/* 520 */       paramNode = paramNode.getNextSibling();
/*     */     }
/* 522 */     return null;
/*     */   }
/*     */ 
/*     */   public static Element selectXencNode(Node paramNode, String paramString, int paramInt)
/*     */   {
/* 533 */     while (paramNode != null) {
/* 534 */       if (ElementProxy.checker.isNamespaceElement(paramNode, paramString, "http://www.w3.org/2001/04/xmlenc#")) {
/* 535 */         if (paramInt == 0) {
/* 536 */           return (Element)paramNode;
/*     */         }
/* 538 */         paramInt--;
/*     */       }
/* 540 */       paramNode = paramNode.getNextSibling();
/*     */     }
/* 542 */     return null;
/*     */   }
/*     */ 
/*     */   public static Text selectDsNodeText(Node paramNode, String paramString, int paramInt)
/*     */   {
/* 553 */     Object localObject = selectDsNode(paramNode, paramString, paramInt);
/* 554 */     if (localObject == null) {
/* 555 */       return null;
/*     */     }
/* 557 */     localObject = ((Node)localObject).getFirstChild();
/* 558 */     while ((localObject != null) && (((Node)localObject).getNodeType() != 3)) {
/* 559 */       localObject = ((Node)localObject).getNextSibling();
/*     */     }
/* 561 */     return (Text)localObject;
/*     */   }
/*     */ 
/*     */   public static Text selectNodeText(Node paramNode, String paramString1, String paramString2, int paramInt)
/*     */   {
/* 572 */     Object localObject = selectNode(paramNode, paramString1, paramString2, paramInt);
/* 573 */     if (localObject == null) {
/* 574 */       return null;
/*     */     }
/* 576 */     localObject = ((Node)localObject).getFirstChild();
/* 577 */     while ((localObject != null) && (((Node)localObject).getNodeType() != 3)) {
/* 578 */       localObject = ((Node)localObject).getNextSibling();
/*     */     }
/* 580 */     return (Text)localObject;
/*     */   }
/*     */ 
/*     */   public static Element selectNode(Node paramNode, String paramString1, String paramString2, int paramInt)
/*     */   {
/* 591 */     while (paramNode != null) {
/* 592 */       if (ElementProxy.checker.isNamespaceElement(paramNode, paramString2, paramString1)) {
/* 593 */         if (paramInt == 0) {
/* 594 */           return (Element)paramNode;
/*     */         }
/* 596 */         paramInt--;
/*     */       }
/* 598 */       paramNode = paramNode.getNextSibling();
/*     */     }
/* 600 */     return null;
/*     */   }
/*     */ 
/*     */   public static Element[] selectDsNodes(Node paramNode, String paramString)
/*     */   {
/* 609 */     return selectNodes(paramNode, "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */   }
/*     */ 
/*     */   public static Element[] selectNodes(Node paramNode, String paramString1, String paramString2)
/*     */   {
/* 618 */     int i = 20;
/* 619 */     Object localObject = new Element[i];
/* 620 */     int j = 0;
/*     */ 
/* 622 */     while (paramNode != null) {
/* 623 */       if (ElementProxy.checker.isNamespaceElement(paramNode, paramString2, paramString1)) {
/* 624 */         localObject[(j++)] = ((Element)paramNode);
/* 625 */         if (i <= j) {
/* 626 */           int k = i << 2;
/* 627 */           Element[] arrayOfElement2 = new Element[k];
/* 628 */           System.arraycopy(localObject, 0, arrayOfElement2, 0, i);
/* 629 */           localObject = arrayOfElement2;
/* 630 */           i = k;
/*     */         }
/*     */       }
/* 633 */       paramNode = paramNode.getNextSibling();
/*     */     }
/* 635 */     Element[] arrayOfElement1 = new Element[j];
/* 636 */     System.arraycopy(localObject, 0, arrayOfElement1, 0, j);
/* 637 */     return arrayOfElement1;
/*     */   }
/*     */ 
/*     */   public static Set excludeNodeFromSet(Node paramNode, Set paramSet)
/*     */   {
/* 646 */     HashSet localHashSet = new HashSet();
/* 647 */     Iterator localIterator = paramSet.iterator();
/*     */ 
/* 649 */     while (localIterator.hasNext()) {
/* 650 */       Node localNode = (Node)localIterator.next();
/*     */ 
/* 652 */       if (!isDescendantOrSelf(paramNode, localNode))
/*     */       {
/* 654 */         localHashSet.add(localNode);
/*     */       }
/*     */     }
/* 657 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   public static boolean isDescendantOrSelf(Node paramNode1, Node paramNode2)
/*     */   {
/* 670 */     if (paramNode1 == paramNode2) {
/* 671 */       return true;
/*     */     }
/*     */ 
/* 674 */     Object localObject = paramNode2;
/*     */     while (true)
/*     */     {
/* 677 */       if (localObject == null) {
/* 678 */         return false;
/*     */       }
/*     */ 
/* 681 */       if (localObject == paramNode1) {
/* 682 */         return true;
/*     */       }
/*     */ 
/* 685 */       if (((Node)localObject).getNodeType() == 2)
/* 686 */         localObject = ((Attr)localObject).getOwnerElement();
/*     */       else
/* 688 */         localObject = ((Node)localObject).getParentNode();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean ignoreLineBreaks()
/*     */   {
/* 694 */     return ignoreLineBreaks;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.utils.XMLUtils
 * JD-Core Version:    0.6.2
 */