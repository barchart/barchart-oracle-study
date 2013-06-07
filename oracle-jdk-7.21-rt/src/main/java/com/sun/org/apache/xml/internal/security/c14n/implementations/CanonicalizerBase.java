/*     */ package com.sun.org.apache.xml.internal.security.c14n.implementations;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizerSpi;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
/*     */ import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Comment;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.ProcessingInstruction;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public abstract class CanonicalizerBase extends CanonicalizerSpi
/*     */ {
/*  66 */   private static final byte[] _END_PI = { 63, 62 };
/*  67 */   private static final byte[] _BEGIN_PI = { 60, 63 };
/*  68 */   private static final byte[] _END_COMM = { 45, 45, 62 };
/*  69 */   private static final byte[] _BEGIN_COMM = { 60, 33, 45, 45 };
/*  70 */   private static final byte[] __XA_ = { 38, 35, 120, 65, 59 };
/*  71 */   private static final byte[] __X9_ = { 38, 35, 120, 57, 59 };
/*  72 */   private static final byte[] _QUOT_ = { 38, 113, 117, 111, 116, 59 };
/*  73 */   private static final byte[] __XD_ = { 38, 35, 120, 68, 59 };
/*  74 */   private static final byte[] _GT_ = { 38, 103, 116, 59 };
/*  75 */   private static final byte[] _LT_ = { 38, 108, 116, 59 };
/*  76 */   private static final byte[] _END_TAG = { 60, 47 };
/*  77 */   private static final byte[] _AMP_ = { 38, 97, 109, 112, 59 };
/*  78 */   static final AttrCompare COMPARE = new AttrCompare();
/*     */   static final String XML = "xml";
/*     */   static final String XMLNS = "xmlns";
/*  81 */   static final byte[] equalsStr = { 61, 34 };
/*     */   static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
/*     */   static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
/*     */   static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
/*     */   protected static final Attr nullNode;
/*     */   List nodeFilter;
/*     */   boolean _includeComments;
/* 100 */   Set _xpathNodeSet = null;
/*     */ 
/* 105 */   Node _excludeNode = null;
/* 106 */   OutputStream _writer = new UnsyncByteArrayOutputStream();
/*     */ 
/*     */   public CanonicalizerBase(boolean paramBoolean)
/*     */   {
/* 114 */     this._includeComments = paramBoolean;
/*     */   }
/*     */ 
/*     */   public byte[] engineCanonicalizeSubTree(Node paramNode)
/*     */     throws CanonicalizationException
/*     */   {
/* 125 */     return engineCanonicalizeSubTree(paramNode, (Node)null);
/*     */   }
/*     */ 
/*     */   public byte[] engineCanonicalizeXPathNodeSet(Set paramSet)
/*     */     throws CanonicalizationException
/*     */   {
/* 135 */     this._xpathNodeSet = paramSet;
/* 136 */     return engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this._xpathNodeSet));
/*     */   }
/*     */ 
/*     */   public byte[] engineCanonicalize(XMLSignatureInput paramXMLSignatureInput)
/*     */     throws CanonicalizationException
/*     */   {
/*     */     try
/*     */     {
/* 148 */       if (paramXMLSignatureInput.isExcludeComments()) {
/* 149 */         this._includeComments = false;
/*     */       }
/* 151 */       if (paramXMLSignatureInput.isOctetStream())
/* 152 */         return engineCanonicalize(paramXMLSignatureInput.getBytes());
/*     */       byte[] arrayOfByte;
/* 154 */       if (paramXMLSignatureInput.isElement()) {
/* 155 */         return engineCanonicalizeSubTree(paramXMLSignatureInput.getSubNode(), paramXMLSignatureInput.getExcludeNode());
/*     */       }
/*     */ 
/* 158 */       if (paramXMLSignatureInput.isNodeSet()) {
/* 159 */         this.nodeFilter = paramXMLSignatureInput.getNodeFilters();
/*     */ 
/* 161 */         circumventBugIfNeeded(paramXMLSignatureInput);
/*     */ 
/* 163 */         if (paramXMLSignatureInput.getSubNode() != null) {
/* 164 */           arrayOfByte = engineCanonicalizeXPathNodeSetInternal(paramXMLSignatureInput.getSubNode());
/*     */         }
/* 166 */         return engineCanonicalizeXPathNodeSet(paramXMLSignatureInput.getNodeSet());
/*     */       }
/*     */ 
/* 171 */       return null;
/*     */     } catch (CanonicalizationException localCanonicalizationException) {
/* 173 */       throw new CanonicalizationException("empty", localCanonicalizationException);
/*     */     } catch (ParserConfigurationException localParserConfigurationException) {
/* 175 */       throw new CanonicalizationException("empty", localParserConfigurationException);
/*     */     } catch (IOException localIOException) {
/* 177 */       throw new CanonicalizationException("empty", localIOException);
/*     */     } catch (SAXException localSAXException) {
/* 179 */       throw new CanonicalizationException("empty", localSAXException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setWriter(OutputStream paramOutputStream)
/*     */   {
/* 186 */     this._writer = paramOutputStream;
/*     */   }
/*     */ 
/*     */   byte[] engineCanonicalizeSubTree(Node paramNode1, Node paramNode2)
/*     */     throws CanonicalizationException
/*     */   {
/* 201 */     this._excludeNode = paramNode2;
/*     */     try {
/* 203 */       NameSpaceSymbTable localNameSpaceSymbTable = new NameSpaceSymbTable();
/* 204 */       int i = -1;
/* 205 */       if ((paramNode1 != null) && (paramNode1.getNodeType() == 1))
/*     */       {
/* 207 */         getParentNameSpaces((Element)paramNode1, localNameSpaceSymbTable);
/* 208 */         i = 0;
/*     */       }
/* 210 */       canonicalizeSubTree(paramNode1, localNameSpaceSymbTable, paramNode1, i);
/* 211 */       this._writer.close();
/*     */       byte[] arrayOfByte;
/* 212 */       if ((this._writer instanceof ByteArrayOutputStream)) {
/* 213 */         arrayOfByte = ((ByteArrayOutputStream)this._writer).toByteArray();
/* 214 */         if (this.reset) {
/* 215 */           ((ByteArrayOutputStream)this._writer).reset();
/*     */         }
/* 217 */         return arrayOfByte;
/* 218 */       }if ((this._writer instanceof UnsyncByteArrayOutputStream)) {
/* 219 */         arrayOfByte = ((UnsyncByteArrayOutputStream)this._writer).toByteArray();
/* 220 */         if (this.reset) {
/* 221 */           ((UnsyncByteArrayOutputStream)this._writer).reset();
/*     */         }
/* 223 */         return arrayOfByte;
/*     */       }
/* 225 */       return null;
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 228 */       throw new CanonicalizationException("empty", localUnsupportedEncodingException);
/*     */     } catch (IOException localIOException) {
/* 230 */       throw new CanonicalizationException("empty", localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void canonicalizeSubTree(Node paramNode1, NameSpaceSymbTable paramNameSpaceSymbTable, Node paramNode2, int paramInt)
/*     */     throws CanonicalizationException, IOException
/*     */   {
/* 247 */     if (isVisibleInt(paramNode1) == -1)
/* 248 */       return;
/* 249 */     Node localNode1 = null;
/* 250 */     Object localObject = null;
/* 251 */     OutputStream localOutputStream = this._writer;
/* 252 */     Node localNode2 = this._excludeNode;
/* 253 */     boolean bool = this._includeComments;
/* 254 */     HashMap localHashMap = new HashMap();
/*     */     while (true) {
/* 256 */       switch (paramNode1.getNodeType()) {
/*     */       case 5:
/*     */       case 10:
/*     */       default:
/* 260 */         break;
/*     */       case 2:
/*     */       case 6:
/*     */       case 12:
/* 266 */         throw new CanonicalizationException("empty");
/*     */       case 9:
/*     */       case 11:
/* 270 */         paramNameSpaceSymbTable.outputNodePush();
/* 271 */         localNode1 = paramNode1.getFirstChild();
/* 272 */         break;
/*     */       case 8:
/* 275 */         if (bool)
/* 276 */           outputCommentToWriter((Comment)paramNode1, localOutputStream, paramInt); break;
/*     */       case 7:
/* 281 */         outputPItoWriter((ProcessingInstruction)paramNode1, localOutputStream, paramInt);
/* 282 */         break;
/*     */       case 3:
/*     */       case 4:
/* 286 */         outputTextToWriter(paramNode1.getNodeValue(), localOutputStream);
/* 287 */         break;
/*     */       case 1:
/* 290 */         paramInt = 0;
/* 291 */         if (paramNode1 != localNode2)
/*     */         {
/* 294 */           Element localElement = (Element)paramNode1;
/*     */ 
/* 296 */           paramNameSpaceSymbTable.outputNodePush();
/* 297 */           localOutputStream.write(60);
/* 298 */           String str = localElement.getTagName();
/* 299 */           UtfHelpper.writeByte(str, localOutputStream, localHashMap);
/*     */ 
/* 301 */           Iterator localIterator = handleAttributesSubtree(localElement, paramNameSpaceSymbTable);
/* 302 */           if (localIterator != null)
/*     */           {
/* 304 */             while (localIterator.hasNext()) {
/* 305 */               Attr localAttr = (Attr)localIterator.next();
/* 306 */               outputAttrToWriter(localAttr.getNodeName(), localAttr.getNodeValue(), localOutputStream, localHashMap);
/*     */             }
/*     */           }
/* 309 */           localOutputStream.write(62);
/* 310 */           localNode1 = paramNode1.getFirstChild();
/* 311 */           if (localNode1 == null) {
/* 312 */             localOutputStream.write(_END_TAG);
/* 313 */             UtfHelpper.writeStringToUtf8(str, localOutputStream);
/* 314 */             localOutputStream.write(62);
/*     */ 
/* 316 */             paramNameSpaceSymbTable.outputNodePop();
/* 317 */             if (localObject != null)
/* 318 */               localNode1 = paramNode1.getNextSibling();
/*     */           }
/*     */           else {
/* 321 */             localObject = localElement;
/*     */           }
/*     */         }
/*     */         break;
/*     */       }
/* 325 */       while ((localNode1 == null) && (localObject != null)) {
/* 326 */         localOutputStream.write(_END_TAG);
/* 327 */         UtfHelpper.writeByte(((Element)localObject).getTagName(), localOutputStream, localHashMap);
/* 328 */         localOutputStream.write(62);
/*     */ 
/* 330 */         paramNameSpaceSymbTable.outputNodePop();
/* 331 */         if (localObject == paramNode2)
/* 332 */           return;
/* 333 */         localNode1 = ((Node)localObject).getNextSibling();
/* 334 */         localObject = ((Node)localObject).getParentNode();
/* 335 */         if ((localObject != null) && (((Node)localObject).getNodeType() != 1)) {
/* 336 */           paramInt = 1;
/* 337 */           localObject = null;
/*     */         }
/*     */       }
/* 340 */       if (localNode1 == null)
/* 341 */         return;
/* 342 */       paramNode1 = localNode1;
/* 343 */       localNode1 = paramNode1.getNextSibling();
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] engineCanonicalizeXPathNodeSetInternal(Node paramNode)
/*     */     throws CanonicalizationException
/*     */   {
/*     */     try
/*     */     {
/* 353 */       canonicalizeXPathNodeSet(paramNode, paramNode);
/* 354 */       this._writer.close();
/*     */       byte[] arrayOfByte;
/* 355 */       if ((this._writer instanceof ByteArrayOutputStream)) {
/* 356 */         arrayOfByte = ((ByteArrayOutputStream)this._writer).toByteArray();
/* 357 */         if (this.reset) {
/* 358 */           ((ByteArrayOutputStream)this._writer).reset();
/*     */         }
/* 360 */         return arrayOfByte;
/* 361 */       }if ((this._writer instanceof UnsyncByteArrayOutputStream)) {
/* 362 */         arrayOfByte = ((UnsyncByteArrayOutputStream)this._writer).toByteArray();
/* 363 */         if (this.reset) {
/* 364 */           ((UnsyncByteArrayOutputStream)this._writer).reset();
/*     */         }
/* 366 */         return arrayOfByte;
/*     */       }
/* 368 */       return null;
/*     */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 370 */       throw new CanonicalizationException("empty", localUnsupportedEncodingException);
/*     */     } catch (IOException localIOException) {
/* 372 */       throw new CanonicalizationException("empty", localIOException);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void canonicalizeXPathNodeSet(Node paramNode1, Node paramNode2)
/*     */     throws CanonicalizationException, IOException
/*     */   {
/* 387 */     if (isVisibleInt(paramNode1) == -1)
/* 388 */       return;
/* 389 */     int i = 0;
/* 390 */     NameSpaceSymbTable localNameSpaceSymbTable = new NameSpaceSymbTable();
/* 391 */     if ((paramNode1 != null) && (paramNode1.getNodeType() == 1))
/* 392 */       getParentNameSpaces((Element)paramNode1, localNameSpaceSymbTable);
/* 393 */     Node localNode = null;
/* 394 */     Object localObject1 = null;
/* 395 */     OutputStream localOutputStream = this._writer;
/* 396 */     int j = -1;
/* 397 */     HashMap localHashMap = new HashMap();
/*     */     while (true)
/*     */     {
/*     */       Object localObject2;
/* 399 */       switch (paramNode1.getNodeType()) {
/*     */       case 5:
/*     */       case 10:
/*     */       default:
/* 403 */         break;
/*     */       case 2:
/*     */       case 6:
/*     */       case 12:
/* 409 */         throw new CanonicalizationException("empty");
/*     */       case 9:
/*     */       case 11:
/* 413 */         localNameSpaceSymbTable.outputNodePush();
/*     */ 
/* 415 */         localNode = paramNode1.getFirstChild();
/* 416 */         break;
/*     */       case 8:
/* 419 */         if ((this._includeComments) && (isVisibleDO(paramNode1, localNameSpaceSymbTable.getLevel()) == 1))
/* 420 */           outputCommentToWriter((Comment)paramNode1, localOutputStream, j); break;
/*     */       case 7:
/* 425 */         if (isVisible(paramNode1))
/* 426 */           outputPItoWriter((ProcessingInstruction)paramNode1, localOutputStream, j); break;
/*     */       case 3:
/*     */       case 4:
/* 431 */         if (isVisible(paramNode1)) {
/* 432 */           outputTextToWriter(paramNode1.getNodeValue(), localOutputStream);
/* 433 */           for (localObject2 = paramNode1.getNextSibling(); 
/* 435 */             (localObject2 != null) && ((((Node)localObject2).getNodeType() == 3) || (((Node)localObject2).getNodeType() == 4)); 
/* 438 */             localObject2 = ((Node)localObject2).getNextSibling()) {
/* 439 */             outputTextToWriter(((Node)localObject2).getNodeValue(), localOutputStream);
/* 440 */             paramNode1 = (Node)localObject2;
/* 441 */             localNode = paramNode1.getNextSibling();
/*     */           }
/*     */         }
/* 438 */         break;
/*     */       case 1:
/* 448 */         j = 0;
/* 449 */         localObject2 = (Element)paramNode1;
/*     */ 
/* 451 */         String str = null;
/* 452 */         int k = isVisibleDO(paramNode1, localNameSpaceSymbTable.getLevel());
/* 453 */         if (k == -1) {
/* 454 */           localNode = paramNode1.getNextSibling();
/*     */         }
/*     */         else {
/* 457 */           i = k == 1 ? 1 : 0;
/* 458 */           if (i != 0) {
/* 459 */             localNameSpaceSymbTable.outputNodePush();
/* 460 */             localOutputStream.write(60);
/* 461 */             str = ((Element)localObject2).getTagName();
/* 462 */             UtfHelpper.writeByte(str, localOutputStream, localHashMap);
/*     */           } else {
/* 464 */             localNameSpaceSymbTable.push();
/*     */           }
/*     */ 
/* 467 */           Iterator localIterator = handleAttributes((Element)localObject2, localNameSpaceSymbTable);
/* 468 */           if (localIterator != null)
/*     */           {
/* 470 */             while (localIterator.hasNext()) {
/* 471 */               Attr localAttr = (Attr)localIterator.next();
/* 472 */               outputAttrToWriter(localAttr.getNodeName(), localAttr.getNodeValue(), localOutputStream, localHashMap);
/*     */             }
/*     */           }
/* 475 */           if (i != 0) {
/* 476 */             localOutputStream.write(62);
/*     */           }
/* 478 */           localNode = paramNode1.getFirstChild();
/*     */ 
/* 480 */           if (localNode == null) {
/* 481 */             if (i != 0) {
/* 482 */               localOutputStream.write(_END_TAG);
/* 483 */               UtfHelpper.writeByte(str, localOutputStream, localHashMap);
/* 484 */               localOutputStream.write(62);
/*     */ 
/* 486 */               localNameSpaceSymbTable.outputNodePop();
/*     */             } else {
/* 488 */               localNameSpaceSymbTable.pop();
/*     */             }
/* 490 */             if (localObject1 != null)
/* 491 */               localNode = paramNode1.getNextSibling();
/*     */           }
/*     */           else {
/* 494 */             localObject1 = localObject2;
/*     */           }
/*     */         }
/*     */         break;
/*     */       }
/* 498 */       while ((localNode == null) && (localObject1 != null)) {
/* 499 */         if (isVisible((Node)localObject1)) {
/* 500 */           localOutputStream.write(_END_TAG);
/* 501 */           UtfHelpper.writeByte(((Element)localObject1).getTagName(), localOutputStream, localHashMap);
/* 502 */           localOutputStream.write(62);
/*     */ 
/* 504 */           localNameSpaceSymbTable.outputNodePop();
/*     */         } else {
/* 506 */           localNameSpaceSymbTable.pop();
/*     */         }
/* 508 */         if (localObject1 == paramNode2)
/* 509 */           return;
/* 510 */         localNode = ((Node)localObject1).getNextSibling();
/* 511 */         localObject1 = ((Node)localObject1).getParentNode();
/* 512 */         if ((localObject1 != null) && (((Node)localObject1).getNodeType() != 1)) {
/* 513 */           localObject1 = null;
/* 514 */           j = 1;
/*     */         }
/*     */       }
/* 517 */       if (localNode == null)
/* 518 */         return;
/* 519 */       paramNode1 = localNode;
/* 520 */       localNode = paramNode1.getNextSibling();
/*     */     }
/*     */   }
/*     */ 
/* 524 */   int isVisibleDO(Node paramNode, int paramInt) { if (this.nodeFilter != null) {
/* 525 */       Iterator localIterator = this.nodeFilter.iterator();
/* 526 */       while (localIterator.hasNext()) {
/* 527 */         int i = ((NodeFilter)localIterator.next()).isNodeIncludeDO(paramNode, paramInt);
/* 528 */         if (i != 1)
/* 529 */           return i;
/*     */       }
/*     */     }
/* 532 */     if ((this._xpathNodeSet != null) && (!this._xpathNodeSet.contains(paramNode)))
/* 533 */       return 0;
/* 534 */     return 1; }
/*     */ 
/*     */   int isVisibleInt(Node paramNode) {
/* 537 */     if (this.nodeFilter != null) {
/* 538 */       Iterator localIterator = this.nodeFilter.iterator();
/* 539 */       while (localIterator.hasNext()) {
/* 540 */         int i = ((NodeFilter)localIterator.next()).isNodeInclude(paramNode);
/* 541 */         if (i != 1)
/* 542 */           return i;
/*     */       }
/*     */     }
/* 545 */     if ((this._xpathNodeSet != null) && (!this._xpathNodeSet.contains(paramNode)))
/* 546 */       return 0;
/* 547 */     return 1;
/*     */   }
/*     */ 
/*     */   boolean isVisible(Node paramNode) {
/* 551 */     if (this.nodeFilter != null) {
/* 552 */       Iterator localIterator = this.nodeFilter.iterator();
/* 553 */       while (localIterator.hasNext()) {
/* 554 */         if (((NodeFilter)localIterator.next()).isNodeInclude(paramNode) != 1)
/* 555 */           return false;
/*     */       }
/*     */     }
/* 558 */     if ((this._xpathNodeSet != null) && (!this._xpathNodeSet.contains(paramNode)))
/* 559 */       return false;
/* 560 */     return true;
/*     */   }
/*     */ 
/*     */   void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable) {
/* 564 */     if (!paramElement.hasAttributes()) {
/* 565 */       return;
/*     */     }
/* 567 */     NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
/* 568 */     int i = localNamedNodeMap.getLength();
/* 569 */     for (int j = 0; j < i; j++) {
/* 570 */       Attr localAttr = (Attr)localNamedNodeMap.item(j);
/* 571 */       if ("http://www.w3.org/2000/xmlns/" == localAttr.getNamespaceURI())
/*     */       {
/* 576 */         String str1 = localAttr.getLocalName();
/* 577 */         String str2 = localAttr.getNodeValue();
/* 578 */         if ((!"xml".equals(str1)) || (!"http://www.w3.org/XML/1998/namespace".equals(str2)))
/*     */         {
/* 582 */           paramNameSpaceSymbTable.addMapping(str1, str2, localAttr);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   final void getParentNameSpaces(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
/*     */   {
/* 592 */     ArrayList localArrayList = new ArrayList(10);
/* 593 */     Node localNode1 = paramElement.getParentNode();
/* 594 */     if ((localNode1 == null) || (localNode1.getNodeType() != 1)) {
/* 595 */       return;
/*     */     }
/*     */ 
/* 598 */     Node localNode2 = localNode1;
/* 599 */     while ((localNode2 != null) && (localNode2.getNodeType() == 1)) {
/* 600 */       localArrayList.add((Element)localNode2);
/* 601 */       localNode2 = localNode2.getParentNode();
/*     */     }
/*     */ 
/* 604 */     ListIterator localListIterator = localArrayList.listIterator(localArrayList.size());
/*     */     Object localObject;
/* 605 */     while (localListIterator.hasPrevious()) {
/* 606 */       localObject = (Element)localListIterator.previous();
/* 607 */       handleParent((Element)localObject, paramNameSpaceSymbTable);
/*     */     }
/*     */ 
/* 610 */     if (((localObject = paramNameSpaceSymbTable.getMappingWithoutRendered("xmlns")) != null) && ("".equals(((Attr)localObject).getValue())))
/*     */     {
/* 612 */       paramNameSpaceSymbTable.addMappingAndRender("xmlns", "", nullNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   abstract Iterator handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
/*     */     throws CanonicalizationException;
/*     */ 
/*     */   abstract Iterator handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
/*     */     throws CanonicalizationException;
/*     */ 
/*     */   abstract void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput)
/*     */     throws CanonicalizationException, ParserConfigurationException, IOException, SAXException;
/*     */ 
/*     */   static final void outputAttrToWriter(String paramString1, String paramString2, OutputStream paramOutputStream, Map paramMap)
/*     */     throws IOException
/*     */   {
/* 660 */     paramOutputStream.write(32);
/* 661 */     UtfHelpper.writeByte(paramString1, paramOutputStream, paramMap);
/* 662 */     paramOutputStream.write(equalsStr);
/*     */ 
/* 664 */     int i = paramString2.length();
/* 665 */     int j = 0;
/* 666 */     while (j < i) {
/* 667 */       char c = paramString2.charAt(j++);
/*     */       byte[] arrayOfByte;
/* 669 */       switch (c)
/*     */       {
/*     */       case '&':
/* 672 */         arrayOfByte = _AMP_;
/* 673 */         break;
/*     */       case '<':
/* 676 */         arrayOfByte = _LT_;
/* 677 */         break;
/*     */       case '"':
/* 680 */         arrayOfByte = _QUOT_;
/* 681 */         break;
/*     */       case '\t':
/* 684 */         arrayOfByte = __X9_;
/* 685 */         break;
/*     */       case '\n':
/* 688 */         arrayOfByte = __XA_;
/* 689 */         break;
/*     */       case '\r':
/* 692 */         arrayOfByte = __XD_;
/* 693 */         break;
/*     */       default:
/* 696 */         if (c < '') {
/* 697 */           paramOutputStream.write(c); continue;
/*     */         }
/* 699 */         UtfHelpper.writeCharToUtf8(c, paramOutputStream);
/*     */ 
/* 701 */         break;
/*     */       }
/* 703 */       paramOutputStream.write(arrayOfByte);
/*     */     }
/*     */ 
/* 706 */     paramOutputStream.write(34);
/*     */   }
/*     */ 
/*     */   static final void outputPItoWriter(ProcessingInstruction paramProcessingInstruction, OutputStream paramOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 718 */     if (paramInt == 1) {
/* 719 */       paramOutputStream.write(10);
/*     */     }
/* 721 */     paramOutputStream.write(_BEGIN_PI);
/*     */ 
/* 723 */     String str1 = paramProcessingInstruction.getTarget();
/* 724 */     int i = str1.length();
/*     */     int k;
/* 726 */     for (int j = 0; j < i; j++) {
/* 727 */       k = str1.charAt(j);
/* 728 */       if (k == 13) {
/* 729 */         paramOutputStream.write(__XD_);
/*     */       }
/* 731 */       else if (k < 128)
/* 732 */         paramOutputStream.write(k);
/*     */       else {
/* 734 */         UtfHelpper.writeCharToUtf8(k, paramOutputStream);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 739 */     String str2 = paramProcessingInstruction.getData();
/*     */ 
/* 741 */     i = str2.length();
/*     */ 
/* 743 */     if (i > 0) {
/* 744 */       paramOutputStream.write(32);
/*     */ 
/* 746 */       for (k = 0; k < i; k++) {
/* 747 */         char c = str2.charAt(k);
/* 748 */         if (c == '\r')
/* 749 */           paramOutputStream.write(__XD_);
/*     */         else {
/* 751 */           UtfHelpper.writeCharToUtf8(c, paramOutputStream);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 756 */     paramOutputStream.write(_END_PI);
/* 757 */     if (paramInt == -1)
/* 758 */       paramOutputStream.write(10);
/*     */   }
/*     */ 
/*     */   static final void outputCommentToWriter(Comment paramComment, OutputStream paramOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 770 */     if (paramInt == 1) {
/* 771 */       paramOutputStream.write(10);
/*     */     }
/* 773 */     paramOutputStream.write(_BEGIN_COMM);
/*     */ 
/* 775 */     String str = paramComment.getData();
/* 776 */     int i = str.length();
/*     */ 
/* 778 */     for (int j = 0; j < i; j++) {
/* 779 */       char c = str.charAt(j);
/* 780 */       if (c == '\r') {
/* 781 */         paramOutputStream.write(__XD_);
/*     */       }
/* 783 */       else if (c < '')
/* 784 */         paramOutputStream.write(c);
/*     */       else {
/* 786 */         UtfHelpper.writeCharToUtf8(c, paramOutputStream);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 791 */     paramOutputStream.write(_END_COMM);
/* 792 */     if (paramInt == -1)
/* 793 */       paramOutputStream.write(10);
/*     */   }
/*     */ 
/*     */   static final void outputTextToWriter(String paramString, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 805 */     int i = paramString.length();
/*     */ 
/* 807 */     for (int j = 0; j < i; j++) {
/* 808 */       char c = paramString.charAt(j);
/*     */       byte[] arrayOfByte;
/* 810 */       switch (c)
/*     */       {
/*     */       case '&':
/* 813 */         arrayOfByte = _AMP_;
/* 814 */         break;
/*     */       case '<':
/* 817 */         arrayOfByte = _LT_;
/* 818 */         break;
/*     */       case '>':
/* 821 */         arrayOfByte = _GT_;
/* 822 */         break;
/*     */       case '\r':
/* 825 */         arrayOfByte = __XD_;
/* 826 */         break;
/*     */       default:
/* 829 */         if (c < '')
/* 830 */           paramOutputStream.write(c);
/*     */         else {
/* 832 */           UtfHelpper.writeCharToUtf8(c, paramOutputStream);
/*     */         }
/* 834 */         break;
/*     */       }
/* 836 */       paramOutputStream.write(arrayOfByte);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  89 */       nullNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns");
/*     */ 
/*  91 */       nullNode.setValue("");
/*     */     } catch (Exception localException) {
/*  93 */       throw new RuntimeException("Unable to create nullNode" + localException);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerBase
 * JD-Core Version:    0.6.2
 */