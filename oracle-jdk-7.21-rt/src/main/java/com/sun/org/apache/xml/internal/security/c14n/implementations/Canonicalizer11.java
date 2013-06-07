/*     */ package com.sun.org.apache.xml.internal.security.c14n.implementations;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public abstract class Canonicalizer11 extends CanonicalizerBase
/*     */ {
/*  61 */   boolean firstCall = true;
/*  62 */   final SortedSet result = new TreeSet(COMPARE);
/*     */   static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
/*     */   static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
/*  66 */   static Logger log = Logger.getLogger(Canonicalizer11.class.getName());
/*     */ 
/* 179 */   XmlAttrStack xmlattrStack = new XmlAttrStack();
/*     */ 
/*     */   public Canonicalizer11(boolean paramBoolean)
/*     */   {
/* 187 */     super(paramBoolean);
/*     */   }
/*     */ 
/*     */   Iterator handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
/*     */     throws CanonicalizationException
/*     */   {
/* 207 */     if ((!paramElement.hasAttributes()) && (!this.firstCall)) {
/* 208 */       return null;
/*     */     }
/*     */ 
/* 211 */     SortedSet localSortedSet = this.result;
/* 212 */     localSortedSet.clear();
/* 213 */     NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
/* 214 */     int i = localNamedNodeMap.getLength();
/*     */ 
/* 216 */     for (int j = 0; j < i; j++) {
/* 217 */       Attr localAttr = (Attr)localNamedNodeMap.item(j);
/* 218 */       String str1 = localAttr.getNamespaceURI();
/*     */ 
/* 220 */       if ("http://www.w3.org/2000/xmlns/" != str1)
/*     */       {
/* 223 */         localSortedSet.add(localAttr);
/*     */       }
/*     */       else
/*     */       {
/* 227 */         String str2 = localAttr.getLocalName();
/* 228 */         String str3 = localAttr.getValue();
/* 229 */         if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(str3)))
/*     */         {
/* 235 */           Node localNode = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, localAttr);
/*     */ 
/* 237 */           if (localNode != null)
/*     */           {
/* 239 */             localSortedSet.add(localNode);
/* 240 */             if (C14nHelper.namespaceIsRelative(localAttr)) {
/* 241 */               Object[] arrayOfObject = { paramElement.getTagName(), str2, localAttr.getNodeValue() };
/* 242 */               throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 248 */     if (this.firstCall)
/*     */     {
/* 252 */       paramNameSpaceSymbTable.getUnrenderedNodes(localSortedSet);
/*     */ 
/* 254 */       this.xmlattrStack.getXmlnsAttr(localSortedSet);
/* 255 */       this.firstCall = false;
/*     */     }
/*     */ 
/* 258 */     return localSortedSet.iterator();
/*     */   }
/*     */ 
/*     */   Iterator handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
/*     */     throws CanonicalizationException
/*     */   {
/* 277 */     this.xmlattrStack.push(paramNameSpaceSymbTable.getLevel());
/* 278 */     int i = isVisibleDO(paramElement, paramNameSpaceSymbTable.getLevel()) == 1 ? 1 : 0;
/* 279 */     NamedNodeMap localNamedNodeMap = null;
/* 280 */     int j = 0;
/* 281 */     if (paramElement.hasAttributes()) {
/* 282 */       localNamedNodeMap = paramElement.getAttributes();
/* 283 */       j = localNamedNodeMap.getLength();
/*     */     }
/*     */ 
/* 286 */     SortedSet localSortedSet = this.result;
/* 287 */     localSortedSet.clear();
/*     */     Object localObject;
/* 289 */     for (int k = 0; k < j; k++) {
/* 290 */       localObject = (Attr)localNamedNodeMap.item(k);
/* 291 */       String str1 = ((Attr)localObject).getNamespaceURI();
/*     */ 
/* 293 */       if ("http://www.w3.org/2000/xmlns/" != str1)
/*     */       {
/* 295 */         if ("http://www.w3.org/XML/1998/namespace" == str1) {
/* 296 */           if (((Attr)localObject).getLocalName().equals("id")) {
/* 297 */             if (i != 0)
/*     */             {
/* 300 */               localSortedSet.add(localObject);
/*     */             }
/*     */           }
/* 303 */           else this.xmlattrStack.addXmlnsAttr((Attr)localObject);
/*     */         }
/* 305 */         else if (i != 0)
/*     */         {
/* 308 */           localSortedSet.add(localObject);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 314 */         String str2 = ((Attr)localObject).getLocalName();
/* 315 */         String str3 = ((Attr)localObject).getValue();
/* 316 */         if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(str3)))
/*     */         {
/* 326 */           if (isVisible((Node)localObject)) {
/* 327 */             if ((i != 0) || (!paramNameSpaceSymbTable.removeMappingIfRender(str2)))
/*     */             {
/* 333 */               Node localNode = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, (Attr)localObject);
/* 334 */               if (localNode != null) {
/* 335 */                 localSortedSet.add(localNode);
/* 336 */                 if (C14nHelper.namespaceIsRelative((Attr)localObject)) {
/* 337 */                   Object[] arrayOfObject = { paramElement.getTagName(), str2, ((Attr)localObject).getNodeValue() };
/*     */ 
/* 339 */                   throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 344 */           else if ((i != 0) && (str2 != "xmlns"))
/* 345 */             paramNameSpaceSymbTable.removeMapping(str2);
/*     */           else
/* 347 */             paramNameSpaceSymbTable.addMapping(str2, str3, (Attr)localObject);
/*     */         }
/*     */       }
/*     */     }
/* 351 */     if (i != 0)
/*     */     {
/* 353 */       Attr localAttr = paramElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
/* 354 */       localObject = null;
/* 355 */       if (localAttr == null)
/*     */       {
/* 357 */         localObject = paramNameSpaceSymbTable.getMapping("xmlns");
/* 358 */       } else if (!isVisible(localAttr))
/*     */       {
/* 361 */         localObject = paramNameSpaceSymbTable.addMappingAndRender("xmlns", "", nullNode);
/*     */       }
/*     */ 
/* 364 */       if (localObject != null) {
/* 365 */         localSortedSet.add(localObject);
/*     */       }
/*     */ 
/* 369 */       this.xmlattrStack.getXmlnsAttr(localSortedSet);
/* 370 */       paramNameSpaceSymbTable.getUnrenderedNodes(localSortedSet);
/*     */     }
/*     */ 
/* 373 */     return localSortedSet.iterator();
/*     */   }
/*     */ 
/*     */   public byte[] engineCanonicalizeXPathNodeSet(Set paramSet, String paramString)
/*     */     throws CanonicalizationException
/*     */   {
/* 386 */     throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
/*     */   }
/*     */ 
/*     */   public byte[] engineCanonicalizeSubTree(Node paramNode, String paramString)
/*     */     throws CanonicalizationException
/*     */   {
/* 400 */     throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
/*     */   }
/*     */ 
/*     */   void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput)
/*     */     throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
/*     */   {
/* 407 */     if (!paramXMLSignatureInput.isNeedsToBeExpanded())
/* 408 */       return;
/* 409 */     Document localDocument = null;
/* 410 */     if (paramXMLSignatureInput.getSubNode() != null)
/* 411 */       localDocument = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getSubNode());
/*     */     else {
/* 413 */       localDocument = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getNodeSet());
/*     */     }
/* 415 */     XMLUtils.circumventBug2650(localDocument);
/*     */   }
/*     */ 
/*     */   void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) {
/* 419 */     if (!paramElement.hasAttributes()) {
/* 420 */       return;
/*     */     }
/* 422 */     this.xmlattrStack.push(-1);
/* 423 */     NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
/* 424 */     int i = localNamedNodeMap.getLength();
/* 425 */     for (int j = 0; j < i; j++) {
/* 426 */       Attr localAttr = (Attr)localNamedNodeMap.item(j);
/* 427 */       if ("http://www.w3.org/2000/xmlns/" != localAttr.getNamespaceURI())
/*     */       {
/* 429 */         if ("http://www.w3.org/XML/1998/namespace" == localAttr.getNamespaceURI()) {
/* 430 */           this.xmlattrStack.addXmlnsAttr(localAttr);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 435 */         String str1 = localAttr.getLocalName();
/* 436 */         String str2 = localAttr.getNodeValue();
/* 437 */         if ((!"xml".equals(str1)) || (!"http://www.w3.org/XML/1998/namespace".equals(str2)))
/*     */         {
/* 441 */           paramNameSpaceSymbTable.addMapping(str1, str2, localAttr);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 447 */   private static String joinURI(String paramString1, String paramString2) throws URISyntaxException { String str1 = null;
/* 448 */     String str2 = null;
/* 449 */     String str3 = "";
/* 450 */     String str4 = null;
/* 451 */     String str5 = null;
/*     */ 
/* 454 */     if (paramString1 != null) {
/* 455 */       if (paramString1.endsWith("..")) {
/* 456 */         paramString1 = paramString1 + "/";
/*     */       }
/* 458 */       localURI = new URI(paramString1);
/* 459 */       str1 = localURI.getScheme();
/* 460 */       str2 = localURI.getAuthority();
/* 461 */       str3 = localURI.getPath();
/* 462 */       str4 = localURI.getQuery();
/* 463 */       str5 = localURI.getFragment();
/*     */     }
/*     */ 
/* 466 */     URI localURI = new URI(paramString2);
/* 467 */     String str6 = localURI.getScheme();
/* 468 */     String str7 = localURI.getAuthority();
/* 469 */     String str8 = localURI.getPath();
/* 470 */     String str9 = localURI.getQuery();
/* 471 */     Object localObject1 = null;
/*     */ 
/* 474 */     if ((str6 != null) && (str6.equals(str1)))
/* 475 */       str6 = null;
/*     */     String str10;
/*     */     String str11;
/*     */     String str12;
/*     */     String str13;
/* 477 */     if (str6 != null) {
/* 478 */       str10 = str6;
/* 479 */       str11 = str7;
/* 480 */       str12 = removeDotSegments(str8);
/* 481 */       str13 = str9;
/*     */     } else {
/* 483 */       if (str7 != null) {
/* 484 */         str11 = str7;
/* 485 */         str12 = removeDotSegments(str8);
/* 486 */         str13 = str9;
/*     */       } else {
/* 488 */         if (str8.length() == 0) {
/* 489 */           str12 = str3;
/* 490 */           if (str9 != null)
/* 491 */             str13 = str9;
/*     */           else
/* 493 */             str13 = str4;
/*     */         }
/*     */         else {
/* 496 */           if (str8.startsWith("/")) {
/* 497 */             str12 = removeDotSegments(str8);
/*     */           } else {
/* 499 */             if ((str2 != null) && (str3.length() == 0)) {
/* 500 */               str12 = "/" + str8;
/*     */             } else {
/* 502 */               int i = str3.lastIndexOf('/');
/* 503 */               if (i == -1)
/* 504 */                 str12 = str8;
/*     */               else {
/* 506 */                 str12 = str3.substring(0, i + 1) + str8;
/*     */               }
/*     */             }
/* 509 */             str12 = removeDotSegments(str12);
/*     */           }
/* 511 */           str13 = str9;
/*     */         }
/* 513 */         str11 = str2;
/*     */       }
/* 515 */       str10 = str1;
/*     */     }
/* 517 */     Object localObject2 = localObject1;
/* 518 */     return new URI(str10, str11, str12, str13, localObject2).toString();
/*     */   }
/*     */ 
/*     */   private static String removeDotSegments(String paramString)
/*     */   {
/* 523 */     log.log(Level.FINE, "STEP   OUTPUT BUFFER\t\tINPUT BUFFER");
/*     */ 
/* 528 */     String str1 = paramString;
/* 529 */     while (str1.indexOf("//") > -1) {
/* 530 */       str1 = str1.replaceAll("//", "/");
/*     */     }
/*     */ 
/* 534 */     StringBuffer localStringBuffer = new StringBuffer();
/*     */ 
/* 538 */     if (str1.charAt(0) == '/') {
/* 539 */       localStringBuffer.append("/");
/* 540 */       str1 = str1.substring(1);
/*     */     }
/*     */ 
/* 543 */     printStep("1 ", localStringBuffer.toString(), str1);
/*     */ 
/* 546 */     while (str1.length() != 0)
/*     */     {
/* 553 */       if (str1.startsWith("./")) {
/* 554 */         str1 = str1.substring(2);
/* 555 */         printStep("2A", localStringBuffer.toString(), str1);
/* 556 */       } else if (str1.startsWith("../")) {
/* 557 */         str1 = str1.substring(3);
/* 558 */         if (!localStringBuffer.toString().equals("/")) {
/* 559 */           localStringBuffer.append("../");
/*     */         }
/* 561 */         printStep("2A", localStringBuffer.toString(), str1);
/*     */       }
/* 565 */       else if (str1.startsWith("/./")) {
/* 566 */         str1 = str1.substring(2);
/* 567 */         printStep("2B", localStringBuffer.toString(), str1);
/* 568 */       } else if (str1.equals("/."))
/*     */       {
/* 570 */         str1 = str1.replaceFirst("/.", "/");
/* 571 */         printStep("2B", localStringBuffer.toString(), str1);
/*     */       }
/*     */       else
/*     */       {
/*     */         int i;
/* 582 */         if (str1.startsWith("/../")) {
/* 583 */           str1 = str1.substring(3);
/* 584 */           if (localStringBuffer.length() == 0) {
/* 585 */             localStringBuffer.append("/");
/* 586 */           } else if (localStringBuffer.toString().endsWith("../")) {
/* 587 */             localStringBuffer.append("..");
/* 588 */           } else if (localStringBuffer.toString().endsWith("..")) {
/* 589 */             localStringBuffer.append("/..");
/*     */           } else {
/* 591 */             i = localStringBuffer.lastIndexOf("/");
/* 592 */             if (i == -1) {
/* 593 */               localStringBuffer = new StringBuffer();
/* 594 */               if (str1.charAt(0) == '/')
/* 595 */                 str1 = str1.substring(1);
/*     */             }
/*     */             else {
/* 598 */               localStringBuffer = localStringBuffer.delete(i, localStringBuffer.length());
/*     */             }
/*     */           }
/* 601 */           printStep("2C", localStringBuffer.toString(), str1);
/* 602 */         } else if (str1.equals("/.."))
/*     */         {
/* 604 */           str1 = str1.replaceFirst("/..", "/");
/* 605 */           if (localStringBuffer.length() == 0) {
/* 606 */             localStringBuffer.append("/");
/* 607 */           } else if (localStringBuffer.toString().endsWith("../")) {
/* 608 */             localStringBuffer.append("..");
/* 609 */           } else if (localStringBuffer.toString().endsWith("..")) {
/* 610 */             localStringBuffer.append("/..");
/*     */           } else {
/* 612 */             i = localStringBuffer.lastIndexOf("/");
/* 613 */             if (i == -1) {
/* 614 */               localStringBuffer = new StringBuffer();
/* 615 */               if (str1.charAt(0) == '/')
/* 616 */                 str1 = str1.substring(1);
/*     */             }
/*     */             else {
/* 619 */               localStringBuffer = localStringBuffer.delete(i, localStringBuffer.length());
/*     */             }
/*     */           }
/* 622 */           printStep("2C", localStringBuffer.toString(), str1);
/*     */         }
/* 628 */         else if (str1.equals(".")) {
/* 629 */           str1 = "";
/* 630 */           printStep("2D", localStringBuffer.toString(), str1);
/* 631 */         } else if (str1.equals("..")) {
/* 632 */           if (!localStringBuffer.toString().equals("/"))
/* 633 */             localStringBuffer.append("..");
/* 634 */           str1 = "";
/* 635 */           printStep("2D", localStringBuffer.toString(), str1);
/*     */         }
/*     */         else
/*     */         {
/* 641 */           i = -1;
/* 642 */           int j = str1.indexOf('/');
/* 643 */           if (j == 0) {
/* 644 */             i = str1.indexOf('/', 1);
/*     */           } else {
/* 646 */             i = j;
/* 647 */             j = 0;
/*     */           }
/*     */           String str2;
/* 650 */           if (i == -1) {
/* 651 */             str2 = str1.substring(j);
/* 652 */             str1 = "";
/*     */           } else {
/* 654 */             str2 = str1.substring(j, i);
/* 655 */             str1 = str1.substring(i);
/*     */           }
/* 657 */           localStringBuffer.append(str2);
/* 658 */           printStep("2E", localStringBuffer.toString(), str1);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 666 */     if (localStringBuffer.toString().endsWith("..")) {
/* 667 */       localStringBuffer.append("/");
/* 668 */       printStep("3 ", localStringBuffer.toString(), str1);
/*     */     }
/*     */ 
/* 671 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private static void printStep(String paramString1, String paramString2, String paramString3) {
/* 675 */     if (log.isLoggable(Level.FINE)) {
/* 676 */       log.log(Level.FINE, " " + paramString1 + ":   " + paramString2);
/* 677 */       if (paramString2.length() == 0)
/* 678 */         log.log(Level.FINE, "\t\t\t\t" + paramString3);
/*     */       else
/* 680 */         log.log(Level.FINE, "\t\t\t" + paramString3);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class XmlAttrStack
/*     */   {
/*  69 */     int currentLevel = 0;
/*  70 */     int lastlevel = 0;
/*     */     XmlsStackElement cur;
/*  77 */     List levels = new ArrayList();
/*     */ 
/*  79 */     void push(int paramInt) { this.currentLevel = paramInt;
/*  80 */       if (this.currentLevel == -1)
/*  81 */         return;
/*  82 */       this.cur = null;
/*  83 */       while (this.lastlevel >= this.currentLevel) {
/*  84 */         this.levels.remove(this.levels.size() - 1);
/*  85 */         if (this.levels.size() == 0) {
/*  86 */           this.lastlevel = 0;
/*  87 */           return;
/*     */         }
/*  89 */         this.lastlevel = ((XmlsStackElement)this.levels.get(this.levels.size() - 1)).level;
/*     */       } }
/*     */ 
/*     */     void addXmlnsAttr(Attr paramAttr) {
/*  93 */       if (this.cur == null) {
/*  94 */         this.cur = new XmlsStackElement();
/*  95 */         this.cur.level = this.currentLevel;
/*  96 */         this.levels.add(this.cur);
/*  97 */         this.lastlevel = this.currentLevel;
/*     */       }
/*  99 */       this.cur.nodes.add(paramAttr);
/*     */     }
/*     */     void getXmlnsAttr(Collection paramCollection) {
/* 102 */       if (this.cur == null) {
/* 103 */         this.cur = new XmlsStackElement();
/* 104 */         this.cur.level = this.currentLevel;
/* 105 */         this.lastlevel = this.currentLevel;
/* 106 */         this.levels.add(this.cur);
/*     */       }
/* 108 */       int i = this.levels.size() - 2;
/* 109 */       int j = 0;
/* 110 */       XmlsStackElement localXmlsStackElement = null;
/* 111 */       if (i == -1) {
/* 112 */         j = 1;
/*     */       } else {
/* 114 */         localXmlsStackElement = (XmlsStackElement)this.levels.get(i);
/* 115 */         if ((localXmlsStackElement.rendered) && (localXmlsStackElement.level + 1 == this.currentLevel))
/* 116 */           j = 1;
/*     */       }
/* 118 */       if (j != 0) {
/* 119 */         paramCollection.addAll(this.cur.nodes);
/* 120 */         this.cur.rendered = true;
/* 121 */         return;
/*     */       }
/*     */ 
/* 124 */       HashMap localHashMap = new HashMap();
/* 125 */       ArrayList localArrayList = new ArrayList();
/* 126 */       int k = 1;
/*     */       Iterator localIterator;
/*     */       Object localObject1;
/* 127 */       for (; i >= 0; i--) {
/* 128 */         localXmlsStackElement = (XmlsStackElement)this.levels.get(i);
/* 129 */         if (localXmlsStackElement.rendered) {
/* 130 */           k = 0;
/*     */         }
/* 132 */         localIterator = localXmlsStackElement.nodes.iterator();
/* 133 */         while ((localIterator.hasNext()) && (k != 0)) {
/* 134 */           localObject1 = (Attr)localIterator.next();
/* 135 */           if (((Attr)localObject1).getLocalName().equals("base")) {
/* 136 */             if (!localXmlsStackElement.rendered)
/* 137 */               localArrayList.add(localObject1);
/*     */           }
/* 139 */           else if (!localHashMap.containsKey(((Attr)localObject1).getName()))
/* 140 */             localHashMap.put(((Attr)localObject1).getName(), localObject1);
/*     */         }
/*     */       }
/* 143 */       if (!localArrayList.isEmpty()) {
/* 144 */         localIterator = this.cur.nodes.iterator();
/* 145 */         localObject1 = null;
/* 146 */         Object localObject2 = null;
/*     */         Attr localAttr;
/* 147 */         while (localIterator.hasNext()) {
/* 148 */           localAttr = (Attr)localIterator.next();
/* 149 */           if (localAttr.getLocalName().equals("base")) {
/* 150 */             localObject1 = localAttr.getValue();
/* 151 */             localObject2 = localAttr;
/* 152 */             break;
/*     */           }
/*     */         }
/* 155 */         localIterator = localArrayList.iterator();
/* 156 */         while (localIterator.hasNext()) {
/* 157 */           localAttr = (Attr)localIterator.next();
/* 158 */           if (localObject1 == null) {
/* 159 */             localObject1 = localAttr.getValue();
/* 160 */             localObject2 = localAttr;
/*     */           } else {
/*     */             try {
/* 163 */               localObject1 = Canonicalizer11.joinURI(localAttr.getValue(), (String)localObject1);
/*     */             } catch (URISyntaxException localURISyntaxException) {
/* 165 */               localURISyntaxException.printStackTrace();
/*     */             }
/*     */           }
/*     */         }
/* 169 */         if ((localObject1 != null) && (((String)localObject1).length() != 0)) {
/* 170 */           localObject2.setValue((String)localObject1);
/* 171 */           paramCollection.add(localObject2);
/*     */         }
/*     */       }
/*     */ 
/* 175 */       this.cur.rendered = true;
/* 176 */       paramCollection.addAll(localHashMap.values());
/*     */     }
/*     */ 
/*     */     static class XmlsStackElement
/*     */     {
/*     */       int level;
/*  74 */       boolean rendered = false;
/*  75 */       List nodes = new ArrayList();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11
 * JD-Core Version:    0.6.2
 */