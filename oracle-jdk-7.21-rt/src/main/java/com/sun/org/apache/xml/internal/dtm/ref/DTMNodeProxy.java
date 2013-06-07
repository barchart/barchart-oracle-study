/*      */ package com.sun.org.apache.xml.internal.dtm.ref;
/*      */ 
/*      */ import com.sun.org.apache.xml.internal.dtm.DTM;
/*      */ import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
/*      */ import com.sun.org.apache.xml.internal.utils.XMLString;
/*      */ import com.sun.org.apache.xpath.internal.NodeSet;
/*      */ import java.util.Vector;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.CDATASection;
/*      */ import org.w3c.dom.Comment;
/*      */ import org.w3c.dom.DOMConfiguration;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.DOMImplementation;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.DocumentFragment;
/*      */ import org.w3c.dom.DocumentType;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.EntityReference;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.w3c.dom.ProcessingInstruction;
/*      */ import org.w3c.dom.Text;
/*      */ import org.w3c.dom.TypeInfo;
/*      */ import org.w3c.dom.UserDataHandler;
/*      */ 
/*      */ public class DTMNodeProxy
/*      */   implements Node, Document, Text, Element, Attr, ProcessingInstruction, Comment, DocumentFragment
/*      */ {
/*      */   public DTM dtm;
/*      */   int node;
/*      */   private static final String EMPTYSTRING = "";
/*   79 */   static final DOMImplementation implementation = new DTMNodeProxyImplementation();
/*      */   protected String fDocumentURI;
/*      */   protected String actualEncoding;
/*      */   private String xmlEncoding;
/*      */   private boolean xmlStandalone;
/*      */   private String xmlVersion;
/*      */ 
/*      */   public DTMNodeProxy(DTM dtm, int node)
/*      */   {
/*   89 */     this.dtm = dtm;
/*   90 */     this.node = node;
/*      */   }
/*      */ 
/*      */   public final DTM getDTM()
/*      */   {
/*  100 */     return this.dtm;
/*      */   }
/*      */ 
/*      */   public final int getDTMNodeNumber()
/*      */   {
/*  110 */     return this.node;
/*      */   }
/*      */ 
/*      */   public final boolean equals(Node node)
/*      */   {
/*      */     try
/*      */     {
/*  125 */       DTMNodeProxy dtmp = (DTMNodeProxy)node;
/*      */ 
/*  129 */       return (dtmp.node == this.node) && (dtmp.dtm == this.dtm);
/*      */     }
/*      */     catch (ClassCastException cce) {
/*      */     }
/*  133 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean equals(Object node)
/*      */   {
/*      */     try
/*      */     {
/*  153 */       return equals((DocumentFragment)node);
/*      */     }
/*      */     catch (ClassCastException cce) {
/*      */     }
/*  157 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean sameNodeAs(Node other)
/*      */   {
/*  171 */     if (!(other instanceof DTMNodeProxy)) {
/*  172 */       return false;
/*      */     }
/*  174 */     DTMNodeProxy that = (DTMNodeProxy)other;
/*      */ 
/*  176 */     return (this.dtm == that.dtm) && (this.node == that.node);
/*      */   }
/*      */ 
/*      */   public final String getNodeName()
/*      */   {
/*  186 */     return this.dtm.getNodeName(this.node);
/*      */   }
/*      */ 
/*      */   public final String getTarget()
/*      */   {
/*  204 */     return this.dtm.getNodeName(this.node);
/*      */   }
/*      */ 
/*      */   public final String getLocalName()
/*      */   {
/*  214 */     return this.dtm.getLocalName(this.node);
/*      */   }
/*      */ 
/*      */   public final String getPrefix()
/*      */   {
/*  223 */     return this.dtm.getPrefix(this.node);
/*      */   }
/*      */ 
/*      */   public final void setPrefix(String prefix)
/*      */     throws DOMException
/*      */   {
/*  235 */     throw new DTMDOMException((short)7);
/*      */   }
/*      */ 
/*      */   public final String getNamespaceURI()
/*      */   {
/*  245 */     return this.dtm.getNamespaceURI(this.node);
/*      */   }
/*      */ 
/*      */   public final boolean supports(String feature, String version)
/*      */   {
/*  266 */     return implementation.hasFeature(feature, version);
/*      */   }
/*      */ 
/*      */   public final boolean isSupported(String feature, String version)
/*      */   {
/*  282 */     return implementation.hasFeature(feature, version);
/*      */   }
/*      */ 
/*      */   public final String getNodeValue()
/*      */     throws DOMException
/*      */   {
/*  295 */     return this.dtm.getNodeValue(this.node);
/*      */   }
/*      */ 
/*      */   public final String getStringValue()
/*      */     throws DOMException
/*      */   {
/*  305 */     return this.dtm.getStringValue(this.node).toString();
/*      */   }
/*      */ 
/*      */   public final void setNodeValue(String nodeValue)
/*      */     throws DOMException
/*      */   {
/*  317 */     throw new DTMDOMException((short)7);
/*      */   }
/*      */ 
/*      */   public final short getNodeType()
/*      */   {
/*  327 */     return this.dtm.getNodeType(this.node);
/*      */   }
/*      */ 
/*      */   public final Node getParentNode()
/*      */   {
/*  338 */     if (getNodeType() == 2) {
/*  339 */       return null;
/*      */     }
/*  341 */     int newnode = this.dtm.getParent(this.node);
/*      */ 
/*  343 */     return newnode == -1 ? null : this.dtm.getNode(newnode);
/*      */   }
/*      */ 
/*      */   public final Node getOwnerNode()
/*      */   {
/*  354 */     int newnode = this.dtm.getParent(this.node);
/*      */ 
/*  356 */     return newnode == -1 ? null : this.dtm.getNode(newnode);
/*      */   }
/*      */ 
/*      */   public final NodeList getChildNodes()
/*      */   {
/*  370 */     return new DTMChildIterNodeList(this.dtm, this.node);
/*      */   }
/*      */ 
/*      */   public final Node getFirstChild()
/*      */   {
/*  383 */     int newnode = this.dtm.getFirstChild(this.node);
/*      */ 
/*  385 */     return newnode == -1 ? null : this.dtm.getNode(newnode);
/*      */   }
/*      */ 
/*      */   public final Node getLastChild()
/*      */   {
/*  396 */     int newnode = this.dtm.getLastChild(this.node);
/*      */ 
/*  398 */     return newnode == -1 ? null : this.dtm.getNode(newnode);
/*      */   }
/*      */ 
/*      */   public final Node getPreviousSibling()
/*      */   {
/*  409 */     int newnode = this.dtm.getPreviousSibling(this.node);
/*      */ 
/*  411 */     return newnode == -1 ? null : this.dtm.getNode(newnode);
/*      */   }
/*      */ 
/*      */   public final Node getNextSibling()
/*      */   {
/*  423 */     if (this.dtm.getNodeType(this.node) == 2) {
/*  424 */       return null;
/*      */     }
/*  426 */     int newnode = this.dtm.getNextSibling(this.node);
/*      */ 
/*  428 */     return newnode == -1 ? null : this.dtm.getNode(newnode);
/*      */   }
/*      */ 
/*      */   public final NamedNodeMap getAttributes()
/*      */   {
/*  441 */     return new DTMNamedNodeMap(this.dtm, this.node);
/*      */   }
/*      */ 
/*      */   public boolean hasAttribute(String name)
/*      */   {
/*  453 */     return -1 != this.dtm.getAttributeNode(this.node, null, name);
/*      */   }
/*      */ 
/*      */   public boolean hasAttributeNS(String namespaceURI, String localName)
/*      */   {
/*  467 */     return -1 != this.dtm.getAttributeNode(this.node, namespaceURI, localName);
/*      */   }
/*      */ 
/*      */   public final Document getOwnerDocument()
/*      */   {
/*  478 */     return (Document)this.dtm.getNode(this.dtm.getOwnerDocument(this.node));
/*      */   }
/*      */ 
/*      */   public final Node insertBefore(Node newChild, Node refChild)
/*      */     throws DOMException
/*      */   {
/*  494 */     throw new DTMDOMException((short)7);
/*      */   }
/*      */ 
/*      */   public final Node replaceChild(Node newChild, Node oldChild)
/*      */     throws DOMException
/*      */   {
/*  510 */     throw new DTMDOMException((short)7);
/*      */   }
/*      */ 
/*      */   public final Node removeChild(Node oldChild)
/*      */     throws DOMException
/*      */   {
/*  524 */     throw new DTMDOMException((short)7);
/*      */   }
/*      */ 
/*      */   public final Node appendChild(Node newChild)
/*      */     throws DOMException
/*      */   {
/*  538 */     throw new DTMDOMException((short)7);
/*      */   }
/*      */ 
/*      */   public final boolean hasChildNodes()
/*      */   {
/*  548 */     return -1 != this.dtm.getFirstChild(this.node);
/*      */   }
/*      */ 
/*      */   public final Node cloneNode(boolean deep)
/*      */   {
/*  560 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final DocumentType getDoctype()
/*      */   {
/*  570 */     return null;
/*      */   }
/*      */ 
/*      */   public final DOMImplementation getImplementation()
/*      */   {
/*  580 */     return implementation;
/*      */   }
/*      */ 
/*      */   public final Element getDocumentElement()
/*      */   {
/*  592 */     int dochandle = this.dtm.getDocument();
/*  593 */     int elementhandle = -1;
/*  594 */     for (int kidhandle = this.dtm.getFirstChild(dochandle); 
/*  595 */       kidhandle != -1; 
/*  596 */       kidhandle = this.dtm.getNextSibling(kidhandle))
/*      */     {
/*  598 */       switch (this.dtm.getNodeType(kidhandle))
/*      */       {
/*      */       case 1:
/*  601 */         if (elementhandle != -1)
/*      */         {
/*  603 */           elementhandle = -1;
/*  604 */           kidhandle = this.dtm.getLastChild(dochandle);
/*      */         }
/*      */         else {
/*  607 */           elementhandle = kidhandle;
/*  608 */         }break;
/*      */       case 7:
/*      */       case 8:
/*      */       case 10:
/*  614 */         break;
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 9:
/*      */       default:
/*  617 */         elementhandle = -1;
/*  618 */         kidhandle = this.dtm.getLastChild(dochandle);
/*      */       }
/*      */     }
/*      */ 
/*  622 */     if (elementhandle == -1) {
/*  623 */       throw new DTMDOMException((short)9);
/*      */     }
/*  625 */     return (Element)this.dtm.getNode(elementhandle);
/*      */   }
/*      */ 
/*      */   public final Element createElement(String tagName)
/*      */     throws DOMException
/*      */   {
/*  639 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final DocumentFragment createDocumentFragment()
/*      */   {
/*  649 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Text createTextNode(String data)
/*      */   {
/*  661 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Comment createComment(String data)
/*      */   {
/*  673 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final CDATASection createCDATASection(String data)
/*      */     throws DOMException
/*      */   {
/*  688 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final ProcessingInstruction createProcessingInstruction(String target, String data)
/*      */     throws DOMException
/*      */   {
/*  704 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Attr createAttribute(String name)
/*      */     throws DOMException
/*      */   {
/*  718 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final EntityReference createEntityReference(String name)
/*      */     throws DOMException
/*      */   {
/*  733 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final NodeList getElementsByTagName(String tagname)
/*      */   {
/*  744 */     Vector listVector = new Vector();
/*  745 */     Node retNode = this.dtm.getNode(this.node);
/*  746 */     if (retNode != null)
/*      */     {
/*  748 */       boolean isTagNameWildCard = "*".equals(tagname);
/*  749 */       if (1 == retNode.getNodeType())
/*      */       {
/*  751 */         NodeList nodeList = retNode.getChildNodes();
/*  752 */         for (int i = 0; i < nodeList.getLength(); i++)
/*      */         {
/*  754 */           traverseChildren(listVector, nodeList.item(i), tagname, isTagNameWildCard);
/*      */         }
/*      */       }
/*  757 */       else if (9 == retNode.getNodeType()) {
/*  758 */         traverseChildren(listVector, this.dtm.getNode(this.node), tagname, isTagNameWildCard);
/*      */       }
/*      */     }
/*      */ 
/*  762 */     int size = listVector.size();
/*  763 */     NodeSet nodeSet = new NodeSet(size);
/*  764 */     for (int i = 0; i < size; i++)
/*      */     {
/*  766 */       nodeSet.addNode((DocumentFragment)listVector.elementAt(i));
/*      */     }
/*  768 */     return nodeSet;
/*      */   }
/*      */ 
/*      */   private final void traverseChildren(Vector listVector, Node tempNode, String tagname, boolean isTagNameWildCard)
/*      */   {
/*  787 */     if (tempNode == null)
/*      */     {
/*  789 */       return;
/*      */     }
/*      */ 
/*  793 */     if ((tempNode.getNodeType() == 1) && ((isTagNameWildCard) || (tempNode.getNodeName().equals(tagname))))
/*      */     {
/*  796 */       listVector.add(tempNode);
/*      */     }
/*  798 */     if (tempNode.hasChildNodes())
/*      */     {
/*  800 */       NodeList nodeList = tempNode.getChildNodes();
/*  801 */       for (int i = 0; i < nodeList.getLength(); i++)
/*      */       {
/*  803 */         traverseChildren(listVector, nodeList.item(i), tagname, isTagNameWildCard);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public final Node importNode(Node importedNode, boolean deep)
/*      */     throws DOMException
/*      */   {
/*  825 */     throw new DTMDOMException((short)7);
/*      */   }
/*      */ 
/*      */   public final Element createElementNS(String namespaceURI, String qualifiedName)
/*      */     throws DOMException
/*      */   {
/*  841 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Attr createAttributeNS(String namespaceURI, String qualifiedName)
/*      */     throws DOMException
/*      */   {
/*  857 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final NodeList getElementsByTagNameNS(String namespaceURI, String localName)
/*      */   {
/*  871 */     Vector listVector = new Vector();
/*  872 */     Node retNode = this.dtm.getNode(this.node);
/*  873 */     if (retNode != null)
/*      */     {
/*  875 */       boolean isNamespaceURIWildCard = "*".equals(namespaceURI);
/*  876 */       boolean isLocalNameWildCard = "*".equals(localName);
/*  877 */       if (1 == retNode.getNodeType())
/*      */       {
/*  879 */         NodeList nodeList = retNode.getChildNodes();
/*  880 */         for (int i = 0; i < nodeList.getLength(); i++)
/*      */         {
/*  882 */           traverseChildren(listVector, nodeList.item(i), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
/*      */         }
/*      */       }
/*  885 */       else if (9 == retNode.getNodeType())
/*      */       {
/*  887 */         traverseChildren(listVector, this.dtm.getNode(this.node), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
/*      */       }
/*      */     }
/*  890 */     int size = listVector.size();
/*  891 */     NodeSet nodeSet = new NodeSet(size);
/*  892 */     for (int i = 0; i < size; i++)
/*      */     {
/*  894 */       nodeSet.addNode((DocumentFragment)listVector.elementAt(i));
/*      */     }
/*  896 */     return nodeSet;
/*      */   }
/*      */ 
/*      */   private final void traverseChildren(Vector listVector, Node tempNode, String namespaceURI, String localname, boolean isNamespaceURIWildCard, boolean isLocalNameWildCard)
/*      */   {
/*  919 */     if (tempNode == null)
/*      */     {
/*  921 */       return;
/*      */     }
/*      */ 
/*  925 */     if ((tempNode.getNodeType() == 1) && ((isLocalNameWildCard) || (tempNode.getLocalName().equals(localname))))
/*      */     {
/*  929 */       String nsURI = tempNode.getNamespaceURI();
/*  930 */       if (((namespaceURI == null) && (nsURI == null)) || (isNamespaceURIWildCard) || ((namespaceURI != null) && (namespaceURI.equals(nsURI))))
/*      */       {
/*  934 */         listVector.add(tempNode);
/*      */       }
/*      */     }
/*  937 */     if (tempNode.hasChildNodes())
/*      */     {
/*  939 */       NodeList nl = tempNode.getChildNodes();
/*  940 */       for (int i = 0; i < nl.getLength(); i++)
/*      */       {
/*  942 */         traverseChildren(listVector, nl.item(i), namespaceURI, localname, isNamespaceURIWildCard, isLocalNameWildCard);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public final Element getElementById(String elementId)
/*      */   {
/*  957 */     return (Element)this.dtm.getNode(this.dtm.getElementById(elementId));
/*      */   }
/*      */ 
/*      */   public final Text splitText(int offset)
/*      */     throws DOMException
/*      */   {
/*  971 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final String getData()
/*      */     throws DOMException
/*      */   {
/*  983 */     return this.dtm.getNodeValue(this.node);
/*      */   }
/*      */ 
/*      */   public final void setData(String data)
/*      */     throws DOMException
/*      */   {
/*  995 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final int getLength()
/*      */   {
/* 1006 */     return this.dtm.getNodeValue(this.node).length();
/*      */   }
/*      */ 
/*      */   public final String substringData(int offset, int count)
/*      */     throws DOMException
/*      */   {
/* 1021 */     return getData().substring(offset, offset + count);
/*      */   }
/*      */ 
/*      */   public final void appendData(String arg)
/*      */     throws DOMException
/*      */   {
/* 1033 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final void insertData(int offset, String arg)
/*      */     throws DOMException
/*      */   {
/* 1046 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final void deleteData(int offset, int count)
/*      */     throws DOMException
/*      */   {
/* 1059 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final void replaceData(int offset, int count, String arg)
/*      */     throws DOMException
/*      */   {
/* 1074 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final String getTagName()
/*      */   {
/* 1084 */     return this.dtm.getNodeName(this.node);
/*      */   }
/*      */ 
/*      */   public final String getAttribute(String name)
/*      */   {
/* 1097 */     DTMNamedNodeMap map = new DTMNamedNodeMap(this.dtm, this.node);
/* 1098 */     Node node = map.getNamedItem(name);
/* 1099 */     return null == node ? "" : node.getNodeValue();
/*      */   }
/*      */ 
/*      */   public final void setAttribute(String name, String value)
/*      */     throws DOMException
/*      */   {
/* 1112 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final void removeAttribute(String name)
/*      */     throws DOMException
/*      */   {
/* 1124 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Attr getAttributeNode(String name)
/*      */   {
/* 1137 */     DTMNamedNodeMap map = new DTMNamedNodeMap(this.dtm, this.node);
/* 1138 */     return (Attr)map.getNamedItem(name);
/*      */   }
/*      */ 
/*      */   public final Attr setAttributeNode(Attr newAttr)
/*      */     throws DOMException
/*      */   {
/* 1152 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Attr removeAttributeNode(Attr oldAttr)
/*      */     throws DOMException
/*      */   {
/* 1166 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public boolean hasAttributes()
/*      */   {
/* 1176 */     return -1 != this.dtm.getFirstAttribute(this.node);
/*      */   }
/*      */ 
/*      */   public final void normalize()
/*      */   {
/* 1182 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final String getAttributeNS(String namespaceURI, String localName)
/*      */   {
/* 1195 */     Node retNode = null;
/* 1196 */     int n = this.dtm.getAttributeNode(this.node, namespaceURI, localName);
/* 1197 */     if (n != -1)
/* 1198 */       retNode = this.dtm.getNode(n);
/* 1199 */     return null == retNode ? "" : retNode.getNodeValue();
/*      */   }
/*      */ 
/*      */   public final void setAttributeNS(String namespaceURI, String qualifiedName, String value)
/*      */     throws DOMException
/*      */   {
/* 1215 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final void removeAttributeNS(String namespaceURI, String localName)
/*      */     throws DOMException
/*      */   {
/* 1229 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Attr getAttributeNodeNS(String namespaceURI, String localName)
/*      */   {
/* 1242 */     Attr retAttr = null;
/* 1243 */     int n = this.dtm.getAttributeNode(this.node, namespaceURI, localName);
/* 1244 */     if (n != -1)
/* 1245 */       retAttr = (Attr)this.dtm.getNode(n);
/* 1246 */     return retAttr;
/*      */   }
/*      */ 
/*      */   public final Attr setAttributeNodeNS(Attr newAttr)
/*      */     throws DOMException
/*      */   {
/* 1261 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final String getName()
/*      */   {
/* 1271 */     return this.dtm.getNodeName(this.node);
/*      */   }
/*      */ 
/*      */   public final boolean getSpecified()
/*      */   {
/* 1285 */     return true;
/*      */   }
/*      */ 
/*      */   public final String getValue()
/*      */   {
/* 1295 */     return this.dtm.getNodeValue(this.node);
/*      */   }
/*      */ 
/*      */   public final void setValue(String value)
/*      */   {
/* 1305 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public final Element getOwnerElement()
/*      */   {
/* 1316 */     if (getNodeType() != 2) {
/* 1317 */       return null;
/*      */     }
/*      */ 
/* 1320 */     int newnode = this.dtm.getParent(this.node);
/* 1321 */     return newnode == -1 ? null : (Element)this.dtm.getNode(newnode);
/*      */   }
/*      */ 
/*      */   public Node adoptNode(Node source)
/*      */     throws DOMException
/*      */   {
/* 1337 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public String getInputEncoding()
/*      */   {
/* 1354 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public void setEncoding(String encoding)
/*      */   {
/* 1370 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public boolean getStandalone()
/*      */   {
/* 1387 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public void setStandalone(boolean standalone)
/*      */   {
/* 1403 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public boolean getStrictErrorChecking()
/*      */   {
/* 1424 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public void setStrictErrorChecking(boolean strictErrorChecking)
/*      */   {
/* 1444 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public String getVersion()
/*      */   {
/* 1461 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public void setVersion(String version)
/*      */   {
/* 1477 */     throw new DTMDOMException((short)9);
/*      */   }
/*      */ 
/*      */   public Object setUserData(String key, Object data, UserDataHandler handler)
/*      */   {
/* 1548 */     return getOwnerDocument().setUserData(key, data, handler);
/*      */   }
/*      */ 
/*      */   public Object getUserData(String key)
/*      */   {
/* 1561 */     return getOwnerDocument().getUserData(key);
/*      */   }
/*      */ 
/*      */   public Object getFeature(String feature, String version)
/*      */   {
/* 1587 */     return isSupported(feature, version) ? this : null;
/*      */   }
/*      */ 
/*      */   public boolean isEqualNode(Node arg)
/*      */   {
/* 1633 */     if (arg == this) {
/* 1634 */       return true;
/*      */     }
/* 1636 */     if (arg.getNodeType() != getNodeType()) {
/* 1637 */       return false;
/*      */     }
/*      */ 
/* 1641 */     if (getNodeName() == null) {
/* 1642 */       if (arg.getNodeName() != null) {
/* 1643 */         return false;
/*      */       }
/*      */     }
/* 1646 */     else if (!getNodeName().equals(arg.getNodeName())) {
/* 1647 */       return false;
/*      */     }
/*      */ 
/* 1650 */     if (getLocalName() == null) {
/* 1651 */       if (arg.getLocalName() != null) {
/* 1652 */         return false;
/*      */       }
/*      */     }
/* 1655 */     else if (!getLocalName().equals(arg.getLocalName())) {
/* 1656 */       return false;
/*      */     }
/*      */ 
/* 1659 */     if (getNamespaceURI() == null) {
/* 1660 */       if (arg.getNamespaceURI() != null) {
/* 1661 */         return false;
/*      */       }
/*      */     }
/* 1664 */     else if (!getNamespaceURI().equals(arg.getNamespaceURI())) {
/* 1665 */       return false;
/*      */     }
/*      */ 
/* 1668 */     if (getPrefix() == null) {
/* 1669 */       if (arg.getPrefix() != null) {
/* 1670 */         return false;
/*      */       }
/*      */     }
/* 1673 */     else if (!getPrefix().equals(arg.getPrefix())) {
/* 1674 */       return false;
/*      */     }
/*      */ 
/* 1677 */     if (getNodeValue() == null) {
/* 1678 */       if (arg.getNodeValue() != null) {
/* 1679 */         return false;
/*      */       }
/*      */     }
/* 1682 */     else if (!getNodeValue().equals(arg.getNodeValue())) {
/* 1683 */       return false;
/*      */     }
/*      */ 
/* 1696 */     return true;
/*      */   }
/*      */ 
/*      */   public String lookupNamespaceURI(String specifiedPrefix)
/*      */   {
/* 1709 */     short type = getNodeType();
/* 1710 */     switch (type)
/*      */     {
/*      */     case 1:
/* 1713 */       String namespace = getNamespaceURI();
/* 1714 */       String prefix = getPrefix();
/* 1715 */       if (namespace != null)
/*      */       {
/* 1717 */         if ((specifiedPrefix == null) && (prefix == specifiedPrefix))
/*      */         {
/* 1719 */           return namespace;
/* 1720 */         }if ((prefix != null) && (prefix.equals(specifiedPrefix)))
/*      */         {
/* 1722 */           return namespace;
/*      */         }
/*      */       }
/* 1725 */       if (hasAttributes()) {
/* 1726 */         NamedNodeMap map = getAttributes();
/* 1727 */         int length = map.getLength();
/* 1728 */         for (int i = 0; i < length; i++) {
/* 1729 */           Node attr = map.item(i);
/* 1730 */           String attrPrefix = attr.getPrefix();
/* 1731 */           String value = attr.getNodeValue();
/* 1732 */           namespace = attr.getNamespaceURI();
/* 1733 */           if ((namespace != null) && (namespace.equals("http://www.w3.org/2000/xmlns/")))
/*      */           {
/* 1735 */             if ((specifiedPrefix == null) && (attr.getNodeName().equals("xmlns")))
/*      */             {
/* 1738 */               return value;
/* 1739 */             }if ((attrPrefix != null) && (attrPrefix.equals("xmlns")) && (attr.getLocalName().equals(specifiedPrefix)))
/*      */             {
/* 1743 */               return value;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1755 */       return null;
/*      */     case 6:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/* 1769 */       return null;
/*      */     case 2:
/* 1771 */       if (getOwnerElement().getNodeType() == 1) {
/* 1772 */         return getOwnerElement().lookupNamespaceURI(specifiedPrefix);
/*      */       }
/*      */ 
/* 1775 */       return null;
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     }
/*      */ 
/* 1784 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isDefaultNamespace(String namespaceURI)
/*      */   {
/* 1861 */     return false;
/*      */   }
/*      */ 
/*      */   public String lookupPrefix(String namespaceURI)
/*      */   {
/* 1878 */     if (namespaceURI == null) {
/* 1879 */       return null;
/*      */     }
/*      */ 
/* 1882 */     short type = getNodeType();
/*      */ 
/* 1884 */     switch (type)
/*      */     {
/*      */     case 6:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/* 1901 */       return null;
/*      */     case 2:
/* 1903 */       if (getOwnerElement().getNodeType() == 1) {
/* 1904 */         return getOwnerElement().lookupPrefix(namespaceURI);
/*      */       }
/*      */ 
/* 1907 */       return null;
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     }
/*      */ 
/* 1916 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isSameNode(Node other)
/*      */   {
/* 1937 */     return this == other;
/*      */   }
/*      */ 
/*      */   public void setTextContent(String textContent)
/*      */     throws DOMException
/*      */   {
/* 1987 */     setNodeValue(textContent);
/*      */   }
/*      */ 
/*      */   public String getTextContent()
/*      */     throws DOMException
/*      */   {
/* 2035 */     return getNodeValue();
/*      */   }
/*      */ 
/*      */   public short compareDocumentPosition(Node other)
/*      */     throws DOMException
/*      */   {
/* 2047 */     return 0;
/*      */   }
/*      */ 
/*      */   public String getBaseURI()
/*      */   {
/* 2075 */     return null;
/*      */   }
/*      */ 
/*      */   public Node renameNode(Node n, String namespaceURI, String name)
/*      */     throws DOMException
/*      */   {
/* 2086 */     return n;
/*      */   }
/*      */ 
/*      */   public void normalizeDocument()
/*      */   {
/*      */   }
/*      */ 
/*      */   public DOMConfiguration getDomConfig()
/*      */   {
/* 2103 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDocumentURI(String documentURI)
/*      */   {
/* 2115 */     this.fDocumentURI = documentURI;
/*      */   }
/*      */ 
/*      */   public String getDocumentURI()
/*      */   {
/* 2127 */     return this.fDocumentURI;
/*      */   }
/*      */ 
/*      */   public String getActualEncoding()
/*      */   {
/* 2142 */     return this.actualEncoding;
/*      */   }
/*      */ 
/*      */   public void setActualEncoding(String value)
/*      */   {
/* 2154 */     this.actualEncoding = value;
/*      */   }
/*      */ 
/*      */   public Text replaceWholeText(String content)
/*      */     throws DOMException
/*      */   {
/* 2204 */     return null;
/*      */   }
/*      */ 
/*      */   public String getWholeText()
/*      */   {
/* 2229 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isElementContentWhitespace()
/*      */   {
/* 2239 */     return false;
/*      */   }
/*      */ 
/*      */   public void setIdAttribute(boolean id)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setIdAttribute(String name, boolean makeId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setIdAttributeNode(Attr at, boolean makeId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setIdAttributeNS(String namespaceURI, String localName, boolean makeId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public TypeInfo getSchemaTypeInfo()
/*      */   {
/* 2281 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isId() {
/* 2285 */     return false;
/*      */   }
/*      */ 
/*      */   public String getXmlEncoding()
/*      */   {
/* 2291 */     return this.xmlEncoding;
/*      */   }
/*      */   public void setXmlEncoding(String xmlEncoding) {
/* 2294 */     this.xmlEncoding = xmlEncoding;
/*      */   }
/*      */ 
/*      */   public boolean getXmlStandalone()
/*      */   {
/* 2299 */     return this.xmlStandalone;
/*      */   }
/*      */ 
/*      */   public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
/* 2303 */     this.xmlStandalone = xmlStandalone;
/*      */   }
/*      */ 
/*      */   public String getXmlVersion()
/*      */   {
/* 2308 */     return this.xmlVersion;
/*      */   }
/*      */ 
/*      */   public void setXmlVersion(String xmlVersion) throws DOMException {
/* 2312 */     this.xmlVersion = xmlVersion;
/*      */   }
/*      */ 
/*      */   static class DTMNodeProxyImplementation
/*      */     implements DOMImplementation
/*      */   {
/*      */     public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId)
/*      */     {
/* 1487 */       throw new DTMDOMException((short)9);
/*      */     }
/*      */ 
/*      */     public Document createDocument(String namespaceURI, String qualfiedName, DocumentType doctype)
/*      */     {
/* 1492 */       throw new DTMDOMException((short)9);
/*      */     }
/*      */ 
/*      */     public boolean hasFeature(String feature, String version)
/*      */     {
/* 1505 */       if ((("CORE".equals(feature.toUpperCase())) || ("XML".equals(feature.toUpperCase()))) && (("1.0".equals(version)) || ("2.0".equals(version))))
/*      */       {
/* 1508 */         return true;
/* 1509 */       }return false;
/*      */     }
/*      */ 
/*      */     public Object getFeature(String feature, String version)
/*      */     {
/* 1537 */       return null;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy
 * JD-Core Version:    0.6.2
 */